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
package org.kuali.rice.kim.bo.role;

import org.kuali.rice.kns.bo.Inactivateable;


/**
 * Represents a single permissions within the system.  It is qualified by a namespace and component.
 * 
 * Permissions are attached to roles.  All authorization checks should be done against permissions,
 * never against roles or groups.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public interface KimPermission extends Inactivateable {

	String getPermissionId();
	
	String getNamespaceCode();
	
	String getPermissionName();

	/** Verbose description of the Permission and functionally what permissions it implies. */
	String getPermissionDescription();

	/** Type identifier for this role.  This will control what additional attributes are available */
	String getKimTypeId();
	
}
