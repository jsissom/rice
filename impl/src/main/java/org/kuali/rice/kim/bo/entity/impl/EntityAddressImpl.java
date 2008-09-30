/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.kim.bo.entity.impl;

import java.util.LinkedHashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.kuali.rice.kim.bo.entity.EntityAddress;
import org.kuali.rice.kim.bo.reference.AddressType;
import org.kuali.rice.kim.bo.reference.impl.AddressTypeImpl;

/**
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
@Entity
@Table(name = "KR_KIM_ENTITY_ADDR_T")
public class EntityAddressImpl extends DefaultableEntityDataBase implements EntityAddress {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ENTITY_ADDR_ID")
	protected String entityAddressId;

	@Column(name = "ENTITY_ID")
	protected String entityId;

	@Column(name = "ADDR_TYP_CD")
	protected String addressTypeCode;

	@Column(name = "ENT_TYP_CD")
	protected String entityTypeCode;

	@Column(name = "CITY_NM")
	protected String cityName;

	@Column(name = "POSTAL_STATE_CD")
	protected String stateCode;

	@Column(name = "POSTAL_CD")
	protected String postalCode;

	@Column(name = "POSTAL_CNTRY_CD")
	protected String countryCode;

	@Column(name = "ADDR_LINE_1")
	protected String line1;

	@Column(name = "ADDR_LINE_2")
	protected String line2;

	@Column(name = "ADDR_LINE_3")
	protected String line3;

	@ManyToOne(targetEntity=AddressTypeImpl.class, fetch = FetchType.EAGER, cascade = {})
	@JoinColumn(name = "ADDR_TYP_CD", insertable = false, updatable = false)
	protected AddressType addressType;

	// Waiting until we pull in from KFS
	// protected State state;
	// protected PostalCode postalCode;
	// protected Country country;

	/**
	 * @see org.kuali.rice.kim.bo.entity.EntityAddress#getAddressTypeCode()
	 */
	public String getAddressTypeCode() {
		return addressTypeCode;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.EntityAddress#getCityName()
	 */
	public String getCityName() {
		return cityName;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.EntityAddress#getCountryCode()
	 */
	public String getCountryCode() {
		return countryCode;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.EntityAddress#getEntityAddressId()
	 */
	public String getEntityAddressId() {
		return entityAddressId;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.EntityAddress#getLine1()
	 */
	public String getLine1() {
		return line1;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.EntityAddress#getLine2()
	 */
	public String getLine2() {
		return line2;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.EntityAddress#getLine3()
	 */
	public String getLine3() {
		return line3;
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kim.bo.entity.EntityAddress#getPostalCode()
	 */
	public String getPostalCode() {
		return postalCode;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.EntityAddress#getStateCode()
	 */
	public String getStateCode() {
		return stateCode;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.EntityAddress#setAddressTypeCode(java.lang.String)
	 */
	public void setAddressTypeCode(String addressTypeCode) {
		this.addressTypeCode = addressTypeCode;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.EntityAddress#setCityName(java.lang.String)
	 */
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.EntityAddress#setCountryCode(java.lang.String)
	 */
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.EntityAddress#setLine1(java.lang.String)
	 */
	public void setLine1(String line1) {
		this.line1 = line1;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.EntityAddress#setLine2(java.lang.String)
	 */
	public void setLine2(String line2) {
		this.line2 = line2;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.EntityAddress#setLine3(java.lang.String)
	 */
	public void setLine3(String line3) {
		this.line3 = line3;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.EntityAddress#setPostalCode(java.lang.String)
	 */
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.EntityAddress#setStateCode(java.lang.String)
	 */
	public void setStateCode(String stateCode) {
		this.stateCode = stateCode;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.DefaultableEntityTypeData#getEntityTypeCode()
	 */
	public String getEntityTypeCode() {
		return entityTypeCode;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.DefaultableEntityTypeData#setEntityTypeCode(java.lang.String)
	 */
	public void setEntityTypeCode(String entityTypeCode) {
		this.entityTypeCode = entityTypeCode;
	}

	public String getEntityId() {
		return this.entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	public void setEntityAddressId(String entityAddressId) {
		this.entityAddressId = entityAddressId;
	}

	public AddressType getAddressType() {
		return this.addressType;
	}

	public void setAddressType(AddressType addressType) {
		this.addressType = addressType;
	}

	/**
	 * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
	 */
	@Override
	protected LinkedHashMap toStringMapper() {
		LinkedHashMap m = new LinkedHashMap();
		m.put("entityAddressId", entityAddressId);
		m.put("entityTypeCode", entityTypeCode);
		m.put("entityId", entityId);
		return m;
	}

}
