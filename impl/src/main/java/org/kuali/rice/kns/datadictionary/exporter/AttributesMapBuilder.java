/*
 * Copyright 2005-2006 The Kuali Foundation.
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
package org.kuali.rice.kns.datadictionary.exporter;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.datadictionary.AttributeDefinition;
import org.kuali.rice.kns.datadictionary.DataDictionaryEntryBase;
import org.kuali.rice.kns.datadictionary.control.ButtonControlDefinition;
import org.kuali.rice.kns.datadictionary.control.ControlDefinition;
import org.kuali.rice.kns.datadictionary.control.CurrencyControlDefinition;
import org.kuali.rice.kns.datadictionary.control.LinkControlDefinition;

/**
 * AttributesMapBuilder
 *
 *
 */
public class AttributesMapBuilder {

    /**
     * Default constructor
     */
    public AttributesMapBuilder() {
    }


    /**
     * @param entry
     * @return ExportMap containing the standard entries for the entry's AttributesDefinition
     */
    public ExportMap buildAttributesMap(DataDictionaryEntryBase entry) {
        ExportMap attributesMap = new ExportMap("attributes");

        for ( AttributeDefinition attribute : entry.getAttributes() ) {
            attributesMap.set(buildAttributeMap(attribute, entry.getFullClassName()));
        }

        return attributesMap;
    }

    private ExportMap buildAttributeMap(AttributeDefinition attribute, String fullClassName) {
        ExportMap attributeMap = new ExportMap(attribute.getName());

        // simple properties
        attributeMap.set("name", attribute.getName());
        attributeMap.set("forceUppercase", attribute.getForceUppercase().toString());
        attributeMap.set("label", attribute.getLabel());
        attributeMap.set("shortLabel", attribute.getShortLabel());
       
        attributeMap.set("maxLength", attribute.getMaxLength().toString());
        final BigDecimal exclusiveMin = attribute.getExclusiveMin();
        if (exclusiveMin != null) {
            attributeMap.set("exclusiveMin", exclusiveMin.toString());
        }
        final BigDecimal exclusiveMax = attribute.getInclusiveMax();
        if (exclusiveMax != null) {
            attributeMap.set("exclusiveMax", exclusiveMax.toString());
        }

        attributeMap.set("required", attribute.isRequired().toString());
        if (attribute.getSummary() != null) {
            attributeMap.set("summary", attribute.getSummary());
        }
        if (attribute.getDescription() != null) {
            attributeMap.set("description", attribute.getDescription());
        }
        if (attribute.hasFormatterClass()) {
            attributeMap.set("formatterClass", attribute.getFormatterClass().getName());
        }

        // complex properties
        if (attribute.hasValidationPattern()) {
            attributeMap.set(attribute.getValidationPattern().buildExportMap("validationPattern"));
        }

        if (attribute.hasDisplayMask()) {
            attributeMap.set("displayWorkgroup", attribute.getDisplayWorkgroup());
            attributeMap.set("displayMaskClass",attribute.getDisplayMask().getClass().toString());
        }

        attributeMap.set(buildControlMap(attribute));
        attributeMap.set("fullClassName", fullClassName);

        return attributeMap;
    }


    private ExportMap buildControlMap(AttributeDefinition attribute) {
        ControlDefinition control = attribute.getControl();
        ExportMap controlMap = new ExportMap("control");

        if (control.isCheckbox()) {
            controlMap.set("checkbox", "true");
        }
        else if (control.isHidden()) {
            controlMap.set("hidden", "true");
        }
        else if (control.isKualiUser()) {
            controlMap.set("kualiUser", "true");
        }
        else if (control.isRadio()) {
            controlMap.set("radio", "true");
            controlMap.set("valuesFinder", control.getValuesFinderClass().getName());
            if (control.getBusinessObjectClass() != null) {
                controlMap.set("businessObject", control.getBusinessObjectClass().getName());
            }
            if (StringUtils.isNotEmpty(control.getKeyAttribute())) {
                controlMap.set("keyAttribute", control.getKeyAttribute());
            }
            if (StringUtils.isNotEmpty(control.getLabelAttribute())) {
                controlMap.set("labelAttribute", control.getLabelAttribute());
            }
            if (control.getIncludeKeyInLabel() != null) {
                controlMap.set("includeKeyInLabel", control.getIncludeKeyInLabel().toString());
            }
        }
        else if (control.isSelect()) {
            controlMap.set("select", "true");
            controlMap.set("valuesFinder", control.getValuesFinderClass().getName());
            if (control.getBusinessObjectClass() != null) {
                controlMap.set("businessObject", control.getBusinessObjectClass().getName());
            }
            if (StringUtils.isNotEmpty(control.getKeyAttribute())) {
                controlMap.set("keyAttribute", control.getKeyAttribute());
            }
            if (StringUtils.isNotEmpty(control.getLabelAttribute())) {
                controlMap.set("labelAttribute", control.getLabelAttribute());
            }
            if (control.getIncludeKeyInLabel() != null) {
                controlMap.set("includeKeyInLabel", control.getIncludeKeyInLabel().toString());
            }
        }
        else if (control.isText()) {
            controlMap.set("text", "true");
            controlMap.set("size", control.getSize().toString());
        }
        else if (control.isTextarea()) {
            controlMap.set("textarea", "true");
            controlMap.set("rows", control.getRows().toString());
            controlMap.set("cols", control.getCols().toString());
        }
        else if (control.isCurrency()) {
            controlMap.set("currency", "true");
            controlMap.set("size", control.getSize().toString());
            controlMap.set("formattedMaxLength", ((CurrencyControlDefinition) control).getFormattedMaxLength().toString());
        }
        else if (control.isLookupHidden()) {
            controlMap.set("lookupHidden", "true");
        }
        else if (control.isLookupReadonly()) {
            controlMap.set("lookupReadonly", "true");
        }else if (control.isButton()) {
            controlMap.set("button", "true");
            if (StringUtils.isNotEmpty(((ButtonControlDefinition) control).getImageSrc())) {
            	controlMap.set("imageSrc", ((ButtonControlDefinition) control).getImageSrc());
            }
            if (StringUtils.isNotEmpty(((ButtonControlDefinition) control).getStyleClass())) {
            	controlMap.set("styleClass", ((ButtonControlDefinition) control).getStyleClass() );
            }
        }else if (control.isLink()) {
            controlMap.set("link", "true");
            if (StringUtils.isNotEmpty(((LinkControlDefinition) control).getTarget())) {
            	controlMap.set("target", ((LinkControlDefinition) control).getTarget());
            }
            if (StringUtils.isNotEmpty(((LinkControlDefinition) control).getStyleClass())) {
            	controlMap.set("styleClass", ((LinkControlDefinition) control).getStyleClass() );
            }
            if (StringUtils.isNotEmpty(((LinkControlDefinition) control).getHrefText())) {
            	controlMap.set("hrefText", ((LinkControlDefinition) control).getHrefText());
            }
        }

        return controlMap;
    }
}