/*
 * Copyright 2005-2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kns.web.struts.action;

import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.RedirectingActionForward;
import org.kuali.rice.core.util.RiceConstants;
import org.kuali.rice.kns.authorization.AuthorizationType;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.bo.Exporter;
import org.kuali.rice.kns.datadictionary.BusinessObjectEntry;
import org.kuali.rice.kns.exception.AuthorizationException;
import org.kuali.rice.kns.exception.ModuleAuthorizationException;
import org.kuali.rice.kns.inquiry.Inquirable;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.ModuleService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.RiceKeyConstants;
import org.kuali.rice.kns.web.struts.form.InquiryForm;

/**
 * This class handles actions for inquiries of business objects.
 */
public class KualiInquiryAction extends KualiAction {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KualiInquiryAction.class);

    protected void checkAuthorization(ActionForm form, String methodToCall) throws AuthorizationException {
        if (!(form instanceof InquiryForm)) {
            super.checkAuthorization(form, methodToCall);
        } else {
            try {
                Class businessObjectClass = Class.forName(((InquiryForm) form).getBusinessObjectClassName());
                AuthorizationType inquiryAuthType = new AuthorizationType.Inquiry(businessObjectClass);
                // check if the inquiry is allowed
                if (!getKualiModuleService().isAuthorized(GlobalVariables.getUserSession().getUniversalUser(), inquiryAuthType)) {
                    LOG.error("User not authorized for inquiry action for this object: " + businessObjectClass.getName());
                    throw new ModuleAuthorizationException(GlobalVariables.getUserSession().getUniversalUser().getPersonUserIdentifier(), inquiryAuthType, getKualiModuleService().getResponsibleModuleService(businessObjectClass));
                }
            }
            catch (ClassNotFoundException ex) {
                super.checkAuthorization(form, methodToCall);
            }
        }
    }

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setAttribute(KNSConstants.PARAM_MAINTENANCE_VIEW_MODE, KNSConstants.PARAM_MAINTENANCE_VIEW_MODE_INQUIRY);
        return super.execute(mapping, form, request, response);
    }

    /**
     * Gets an inquirable impl from the impl service name parameter. Then calls lookup service to retrieve the record from the
     * key/value parameters. Finally gets a list of Rows from the inquirable
     */
    public ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        InquiryForm inquiryForm = (InquiryForm) form;
        if (inquiryForm.getBusinessObjectClassName() == null) {
            LOG.error("Business object name not given.");
            throw new RuntimeException("Business object name not given.");
        }
        
        Class boClass = Class.forName(inquiryForm.getBusinessObjectClassName());
        ModuleService responsibleModuleService = KNSServiceLocator.getKualiModuleService().getResponsibleModuleService(boClass);
		if(responsibleModuleService!=null && responsibleModuleService.isExternalizable(boClass)){
			String redirectUrl = responsibleModuleService.getExternalizableBusinessObjectInquiryUrl(boClass, (Map<String, String[]>) request.getParameterMap());
			ActionForward redirectingActionForward = new RedirectingActionForward(redirectUrl);
			redirectingActionForward.setModule("/");
			return redirectingActionForward;
		}

		return continueWithInquiry(mapping, form, request, response);
    }
    
    public ActionForward continueWithInquiry(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	InquiryForm inquiryForm = (InquiryForm) form;
    	
    	if (inquiryForm.getBusinessObjectClassName() == null) {
    		LOG.error("Business object name not given.");
    		throw new RuntimeException("Business object name not given.");
    	}
    	
        BusinessObject bo = retrieveBOFromInquirable(inquiryForm);
        if (bo == null) {
            LOG.error("No records found in inquiry action.");
            GlobalVariables.getErrorMap().putError(KNSConstants.GLOBAL_ERRORS, RiceKeyConstants.ERROR_INQUIRY);
            request.setAttribute("backLocation", request.getParameter("returnLocation"));
            return mapping.findForward("inquiryError");
        }
        
        populateSections(mapping, request, inquiryForm, bo);
        
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }
    
    /**
     * Turns on (or off) the inactive record display for a maintenance collection.
     */
    public ActionForward toggleInactiveRecordDisplay(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        InquiryForm inquiryForm = (InquiryForm) form;
        if (inquiryForm.getBusinessObjectClassName() == null) {
            LOG.error("Business object name not given.");
            throw new RuntimeException("Business object name not given.");
        }
        
        BusinessObject bo = retrieveBOFromInquirable(inquiryForm);
        if (bo == null) {
            LOG.error("No records found in inquiry action.");
            GlobalVariables.getErrorMap().putError(KNSConstants.GLOBAL_ERRORS, RiceKeyConstants.ERROR_INQUIRY);
            request.setAttribute("backLocation", request.getParameter("returnLocation"));
            return mapping.findForward("inquiryError");
        }
        
        Inquirable kualiInquirable = inquiryForm.getInquirable();
        //////////////////////////////
        String collectionName = extractCollectionName(request, KNSConstants.TOGGLE_INACTIVE_METHOD);
        if (collectionName == null) {
            LOG.error("Unable to get find collection name in request.");
            throw new RuntimeException("Unable to get find collection class in request.");
        }  
        String parameterName = (String) request.getAttribute(KNSConstants.METHOD_TO_CALL_ATTRIBUTE);
        boolean showInactive = Boolean.parseBoolean(StringUtils.substringBetween(parameterName, KNSConstants.METHOD_TO_CALL_BOPARM_LEFT_DEL, "."));
        kualiInquirable.setShowInactiveRecords(collectionName, showInactive);
        //////////////////////////////
        
        populateSections(mapping, request, inquiryForm, bo);
        
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }
    
    
    @Override
    public ActionForward toggleTab(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        InquiryForm inquiryForm = (InquiryForm) form;
        if (inquiryForm.getBusinessObjectClassName() == null) {
            LOG.error("Business object name not given.");
            throw new RuntimeException("Business object name not given.");
        }
        
        BusinessObject bo = retrieveBOFromInquirable(inquiryForm);
        if (bo == null) {
            LOG.error("No records found in inquiry action.");
            GlobalVariables.getErrorMap().putError(KNSConstants.GLOBAL_ERRORS, RiceKeyConstants.ERROR_INQUIRY);
            request.setAttribute("backLocation", request.getParameter("returnLocation"));
            return mapping.findForward("inquiryError");
        }
        
        populateSections(mapping, request, inquiryForm, bo);
        
        Inquirable kualiInquirable = inquiryForm.getInquirable();
        
        return super.toggleTab(mapping, form, request, response);
    }
    
    /**
     * Handles exporting the BusinessObject for this Inquiry to XML if it has a custom XML exporter available.
     */
    public ActionForward export(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	InquiryForm inquiryForm = (InquiryForm) form;
    	if (inquiryForm.isCanExport()) {
    		BusinessObject bo = retrieveBOFromInquirable(inquiryForm);
    		if (bo == null) {
    			LOG.error("No records found in inquiry action.");
    			GlobalVariables.getErrorMap().putError(KNSConstants.GLOBAL_ERRORS, RiceKeyConstants.ERROR_INQUIRY);
    			request.setAttribute("backLocation", request.getParameter("returnLocation"));
    			return mapping.findForward("inquiryError");
    		}        
    		BusinessObjectEntry businessObjectEntry = KNSServiceLocator.getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(inquiryForm.getBusinessObjectClassName());
    		Class<? extends Exporter> exporterClass = businessObjectEntry.getExporterClass();
    		if (exporterClass != null) {
    			Exporter exporter = exporterClass.newInstance();
        		response.setContentType(KNSConstants.XML_MIME_TYPE);
        		response.setHeader("Content-disposition", "attachment; filename=export.xml");
        		exporter.export(businessObjectEntry.getBusinessObjectClass(), Collections.singletonList(bo), KNSConstants.XML_FORMAT, response.getOutputStream());
        	}
        }
        
        return null;
    }

    /**
     * Convert a Request into a Map<String,String>. Technically, Request parameters do not neatly translate into a Map of Strings,
     * because a given parameter may legally appear more than once (so a Map of String[] would be more accurate.) This method should
     * be safe for business objects, but may not be reliable for more general uses.
     */
    protected String extractCollectionName(HttpServletRequest request, String methodToCall) {
        // collection name and underlying object type from request parameter
        String parameterName = (String) request.getAttribute(KNSConstants.METHOD_TO_CALL_ATTRIBUTE);
        String collectionName = null;
        if (StringUtils.isNotBlank(parameterName)) {
            collectionName = StringUtils.substringBetween(parameterName, methodToCall + ".", ".(");
        }
        return collectionName;
    }
    
    protected BusinessObject retrieveBOFromInquirable(InquiryForm inquiryForm) {
	Inquirable kualiInquirable = inquiryForm.getInquirable();
        // retrieve the business object
        BusinessObject bo = kualiInquirable.getBusinessObject(inquiryForm.retrieveInquiryDecryptedPrimaryKeys());
        if (bo == null) {
            LOG.error("No records found in inquiry action.");
            GlobalVariables.getErrorMap().putError(KNSConstants.GLOBAL_ERRORS, RiceKeyConstants.ERROR_INQUIRY);
        }
        return bo;
    }
    
    protected void populateSections(ActionMapping mapping, HttpServletRequest request, InquiryForm inquiryForm, BusinessObject bo) {
	Inquirable kualiInquirable = inquiryForm.getInquirable();
	
        // get list of populated sections for display
        List sections = kualiInquirable.getSections(bo);
        inquiryForm.setSections(sections);
        kualiInquirable.addAdditionalSections(sections, bo);
        request.setAttribute(KNSConstants.INQUIRABLE_ATTRIBUTE_NAME, kualiInquirable);
    }
}