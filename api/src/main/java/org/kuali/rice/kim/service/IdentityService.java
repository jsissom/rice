package org.kuali.rice.kim.service;

import java.util.List;
import java.util.Map;

import org.kuali.rice.kim.bo.entity.dto.KimEntityDefaultInfo;
import org.kuali.rice.kim.bo.entity.dto.KimEntityNameInfo;
import org.kuali.rice.kim.bo.entity.dto.KimEntityNamePrincipalNameInfo;
import org.kuali.rice.kim.bo.entity.dto.KimEntityPrivacyPreferencesInfo;
import org.kuali.rice.kim.bo.entity.dto.KimPrincipalInfo;
import org.kuali.rice.kim.bo.reference.dto.AddressTypeInfo;
import org.kuali.rice.kim.bo.reference.dto.AffiliationTypeInfo;
import org.kuali.rice.kim.bo.reference.dto.CitizenshipStatusInfo;
import org.kuali.rice.kim.bo.reference.dto.EmailTypeInfo;
import org.kuali.rice.kim.bo.reference.dto.EmploymentStatusInfo;
import org.kuali.rice.kim.bo.reference.dto.EmploymentTypeInfo;
import org.kuali.rice.kim.bo.reference.dto.EntityNameTypeInfo;
import org.kuali.rice.kim.bo.reference.dto.EntityTypeInfo;
import org.kuali.rice.kim.bo.reference.dto.ExternalIdentifierTypeInfo;
import org.kuali.rice.kim.bo.reference.dto.PhoneTypeInfo;

/**
 * This service provides operations to query for principal and entity data.
 * 
 * <p>A principal represents an entity that can authenticate.  In essence, a principal can be
 * thought of as an "account" or as an entity's authentication credentials.  A principal has
 * an id which is used to uniquely identify it.  It also has a name which represents the
 * principal's username and is typically what is entered when authenticating.  All principals
 * are associated with one and only one entity.
 * 
 * <p>An entity represents a person or system.  Additionally, other "types" of entities can
 * be defined in KIM.  Information like name, phone number, etc. is associated with an entity.
 * It is the representation of a concrete person or system.  While an entity will typically
 * have a single principal associated with it, it is possible for an entity to have more than
 * one principal or even no principals at all (in the case where the entity does not actually
 * authenticate).
 * 
 * <p>This service also provides operations for querying various pieces of reference data, such as 
 * address types, affiliation types, phone types, etc.
 * 
 * <p>This service provides read-only operations.  For write operations, see
 * {@link IdentityUpdateService}.
 * 
 * @see IdentityUpdateService
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public interface IdentityService {
	    
	/** 
	 * Get the principal with the given unique principal ID 
	 */
	KimPrincipalInfo getPrincipal(String principalId);
	
	/**
	 * Get the principal with the given principalName.
	 */
	KimPrincipalInfo getPrincipalByPrincipalName(String principalName);

	/**
	 * Get the principal with the given name and password.
	 */
	KimPrincipalInfo getPrincipalByPrincipalNameAndPassword(String principalName, String password);
	    
	/**
	 * Get the entity default info for the entity with the given id.
	 */
	KimEntityDefaultInfo getEntityDefaultInfo( String entityId );
	
	/**
	 * Get the entity default info for the entity of the principal with the given principal id.
	 */
	KimEntityDefaultInfo getEntityDefaultInfoByPrincipalId( String principalId );
	
	/**
	 * Get the entity default info for the entity of the principal with the given principal name.
	 */
	KimEntityDefaultInfo getEntityDefaultInfoByPrincipalName( String principalName );
	
	/**
	 * Gets a List of entity default info for entities based on the given search criteria.
	 * 
	 * <p>If unbounded is set to false, then this method will return all results.  If unbounded is set to
	 * true then the number of search results will be bounded based on default configuration for number
	 * of search results returned in a a bounded search.
	 * 
	 * <p>The searchCriteria Map is a map of entity field names to search values.
	 */
	List<? extends KimEntityDefaultInfo> lookupEntityDefaultInfo( Map<String,String> searchCriteria, boolean unbounded );
	
	/**
	 * Returns a count of the number of entities that match the given search criteria.
	 */
	int getMatchingEntityCount( Map<String,String> searchCriteria );
	
	/**
	 * Gets the privacy preferences for the entity with the given entity id.
	 */
	KimEntityPrivacyPreferencesInfo getEntityPrivacyPreferences( String entityId );
	
	/**
	 * Gets the name for the principals with ids in the given List.
	 * 
	 * <p>The resulting Map contains the principalId as the key and the name information as the value.
	 * When fetching names by principal id, the resulting name info contains the entity's name info
	 * as well as the principal's name info.
	 */
    Map<String, KimEntityNamePrincipalNameInfo> getDefaultNamesForPrincipalIds(List<String> principalIds);
    
    /**
     * Gets the names for the entities with ids in the given list.
     */
    Map<String, KimEntityNameInfo> getDefaultNamesForEntityIds(List<String> entityIds);

    /**
     * Gets the address type for the given address type code.
     */
	public AddressTypeInfo getAddressType( String code );

    /**
     * Gets the affiliation type for the given affiliation type code.
     */
	public AffiliationTypeInfo getAffiliationType( String code );
	
	/**
	 * Gets the citizenship status for the given citizenship status code.
	 */
	public CitizenshipStatusInfo getCitizenshipStatus( String code );
	
    /**
     * Gets the email type for the given email type code.
     */
	public EmailTypeInfo getEmailType( String code );

    /**
     * Gets the employment status for the given employment status code.
     */
	public EmploymentStatusInfo getEmploymentStatus( String code );
	
    /**
     * Gets the employment type for the given employment type code.
     */
	public EmploymentTypeInfo getEmploymentType( String code );
	
    /**
     * Gets the entity name type for the given entity name type code.
     */
	public EntityNameTypeInfo getEntityNameType( String code );
	
    /**
     * Gets the entity type for the given entity type code.
     */
	public EntityTypeInfo getEntityType( String code );
	
    /**
     * Gets the external identifier type for the given external identifier type code.
     */
	public ExternalIdentifierTypeInfo getExternalIdentifierType( String code );
	
    /**
     * Gets the phone type for the given phone type code.
     */
	public PhoneTypeInfo getPhoneType( String code );
    
}
