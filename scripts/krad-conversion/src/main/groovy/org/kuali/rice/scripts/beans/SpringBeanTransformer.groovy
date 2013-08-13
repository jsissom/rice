/**
 * Copyright 2005-2013 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.scripts.beans

import groovy.util.logging.Log
import groovy.xml.QName
import org.apache.commons.lang.StringUtils

/**
 * This class handles basic conversion of properties
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Log
class SpringBeanTransformer {

    // Used as the default prefix on all krad converted files
    public static String OUTPUT_CONV_FILE_PREFIX = "KradConv";

    // holds all variables
    def config

    // dictionary properties transform map
    def ddPropertiesMap

    // control definition transform map
    def ddBeanControlMap

    // bean property removal list
    def ddPropertiesRemoveList

    // namespace schema (p and xsi)
    def pNamespaceSchema
    def xsiNamespaceSchema

    Map<String, String> definitionDataObjects = [:];
    Map<String, String> parentBeans = [:];

    def init(config) {
        ddPropertiesMap = config.map.convert.dd_prop
        ddBeanControlMap = config.map.convert.dd_bean_control
        ddPropertiesRemoveList = config.list.remove.dd_beans
        pNamespaceSchema = config.msg_bean_schema
        xsiNamespaceSchema = config.msg_xml_schema_legacy
    }

    // bean utilities

    /**
     *
     * @param builderDelegate
     * @param beanNode
     * @param renamedBeanNames
     */
    public void renamePropertyBeans(NodeBuilder builderDelegate, Node beanNode, Map<String, String> renamedBeanNames) {
        beanNode.property.each { beanProperty ->
            beanProperty.beans.each { propertyBeans ->
                if (renamedBeanNames.containsKey(propertyBeans.@parent)) {
                    propertyBeans.@parent = renamedBeanNames.get(propertyBeans.@parent);
                }
            }
        }
    }

    /**
     * function assists with relabeled beans within a property
     * should handle individual beans and beans within a list
     *
     * TODO: make sure list case is tested
     *
     * @param beanNode
     * @param renamedBeanNames
     * @param useStub
     */
    public void renamePropertyBeans(Node beanNode, Map<String, String> renamedBeanNames, boolean useStub) {
        beanNode.property.bean.findAll { renamedBeanNames.containsKey(it.@parent) }.each { replaceBean ->
            String replaceBeanParent = replaceBean.@parent;
            if (useStub) {
                replaceBean.replaceNode {
                    bean(parent: renamedBeanNames.get(replaceBeanParent))
                }
            } else {
                replaceBean.@parent = renamedBeanNames.get(replaceBeanParent);
            }
        }
    }

    /**
     * Removes the children bean of the bean its called on
     * TODO: review if this is necessary
     *
     * @param beanNode
     */
    public void removeChildrenBeans(Node beanNode) {
        removeChildrenBeans(beanNode, beanNode.@id);
    }

    /**
     * Searching the current xml structure the bean node is in and removes any children
     * any beans where the parent id matches the bean id.
     *
     * @param beanNode
     */
    public void removeChildrenBeans(Node beanNode, String beanId) {
        // loop through the nodes til you reach the root
        def rootNode = beanNode;
        while (rootNode instanceof Node && rootNode.parent() != null) {
            rootNode = rootNode.parent();
        }

        rootNode.bean.findAll { it.@parent == beanNode.@id }.each { childNode ->
            if (childNode != null) {
                childNode.replaceNode({})
            }
        }
    }

    /**
     * Transform property bean list into new property beans
     * TODO: see if it can be merged with rename property beans
     *
     * @param builder
     * @param beanNode
     */
    def transformPropertyBeanList(NodeBuilder builder, Node beanNode, Map<String, String> replaceProperties, Closure attrCondition, Closure beanTransform) {
        beanNode.property.findAll { replaceProperties.keySet().contains(it.@name) }.each { propertyNode ->
            builder.property(name: replaceProperties.get(propertyNode.@name)) {
                list {
                    propertyNode.list.bean.each { innerBean ->
                        def attributeValue;
                        innerBean.attributes().each { key, value ->
                            if (attrCondition(key, value)) {
                                attributeValue = value
                            }
                        }

                        beanTransform(builder, attributeValue)
                    }
                }
            }
        }
    }

    // helper closures - attribute conditional checks

    def attributeNameAttrCondition = {
        key, value -> key.toString().endsWith("attributeName") }

    def nameAttrCondition = {
        key, value -> key.toString().endsWith("name") }

    // helper closures - bean transforms

    def genericBeanTransform = {
        builderDelegate, beanParent, attrValue -> builderDelegate.bean('xmlns:p': pNamespaceSchema, parent: beanParent, 'p:propertyName': attrValue) }

    def inputFieldBeanTransform = {
        builderDelegate, attrValue -> genericBeanTransform(builderDelegate, 'Uif-InputField', attrValue) }

    def attributeFieldBeanTransform = {
        builderDelegate, attrValue -> genericBeanTransform(builderDelegate, 'AttributeField', attrValue) }

    def dataFieldBeanTransform = {
        builderDelegate, attrValue -> genericBeanTransform(builderDelegate, 'Uif-DataField', attrValue) }

    def lookupCriteriaFieldBeanTransform = {
        builderDelegate, attrValue -> genericBeanTransform(builderDelegate, 'Uif-LookupCriteriaInputField', attrValue) }

    def valueFieldTransform = {
        builderDelegate, attrValue -> builderDelegate.value(attrValue) }

    // Property utilities

    /**
     * Copies properties from bean to the builder delegate based on the property names list
     *
     * @param builderDelegate
     * @param beanNode
     * @param propertyNames
     * @return
     */
    def copyProperties(NodeBuilder builderDelegate, Node beanNode, List<String> propertyNames) {
        beanNode.property.findAll { propertyNames.contains(it.@name) }.each { beanProperty ->
            if (beanProperty.list) {
                builderDelegate.property(name: beanProperty.@name) {
                    list {
                        beanProperty.list.value.each {
                            value(it.@value)
                        }
                    }
                }
            } else {
                builderDelegate.property(name: beanProperty.@name, value: beanProperty.@value)
            }
        }
    }

    /**
     * renames properties within the bean based on the map containing property names and their replacement
     *
     * @param beanNode
     * @param renamedPropertyNames
     * @return
     */
    def renameProperties(Node beanNode, Map<String, String> renamedPropertyNames) {
        beanNode.property.findAll { renamedPropertyNames.containsKey(it.@name) }.each { beanProperty -> beanProperty.@name = renamedPropertyNames.get(beanProperty.@name) }
    }

    /**
     * Copies properties from the bean node to the delegate and renames using a map containing property names and their replacements
     *
     * @param builderDelegate
     * @param beanNode
     * @param renamedPropertyNames
     * @return
     */
    def renameProperties(NodeBuilder builderDelegate, Node beanNode, Map<String, String> renamedPropertyNames) {
        beanNode.property.each { beanProperty ->
            if (renamedPropertyNames.containsKey(beanProperty.@name)) {
                builderDelegate.property(name: renamedPropertyNames.get(beanProperty.@name), value: beanProperty.@value)
            }
        }
    }

    def removeProperties(Node beanNode, List<String> propertyNames) {
        beanNode.property.findAll { propertyNames.contains(it.@name) }.each { Node beanProperty -> beanProperty.replaceNode {} }
    }

    /**
     * Retrieves the object class name based on related business object entry bean
     *
     * @param beanNode
     * @return
     */
    protected String getObjectClassName(Node beanNode) {
        String definitionName = beanNode.@id;
        String definitionChildName = StringUtils.removeEnd(definitionName, "-parentBean");
        String objClassName = "";
        if (definitionDataObjects.get(definitionName)) {
            objClassName = definitionDataObjects.get(definitionName);
        } else if (definitionDataObjects.get(definitionChildName)) {
            objClassName = definitionDataObjects.get(definitionChildName);
        } else if (beanNode.property.find { it.@name == "businessObjectClass" }) {
            objClassName = beanNode.property.find { it.@name == "businessObjectClass" }?.@value;
        }
        return objClassName;
    }

    /**
     * extracts object names from bean id
     *
     * @param beanNode
     * @return
     */
    private String getMaintenanceDocumentObjectName(Node beanNode) {
        def suffixes = ["-parentBean", "MaintenanceDocument"];
        def objectName = beanNode.@id.toString();
        suffixes.each { suffix -> objectName = objectName.replaceFirst(suffix); }
        return objectName;
    }

    /**
     * Turns bean id into a more readable form
     *
     * @param beanId
     * @return
     */
    def getTitleFromBeanId(String beanId) {
        // TODO: review whether camel case check is necessary
        return beanId?.replaceAll(~/\s/, '-');
    }

    /**
     *
     * @param builder
     * @param viewName
     */
    def addViewNameProperty(NodeBuilder builder, String viewName) {
        createProperty(builder, "viewName", getTitleFromBeanId(viewName))
    }

    /**
     * replaces namespace properties (p:name) with a property tag
     * Allows transformation scripts to handle property tags properly
     *
     * @param beanNode
     */
    def fixNamespaceProperties(beanNode) {
        def count = 0;
        log.finer "loading " + beanNode.attributes()
        def remAttrs = []
        if (beanNode.attributes()) {
            def attrs = beanNode.attributes()

            attrs.keySet().each {
                count++
                log.finer "adding property: " + it + " " + it.class.name
                if (it instanceof QName) {
                    beanNode.appendNode("property", [name: it.getLocalPart(), value: attrs.get(it)])
                    remAttrs.add(it)
                }
            }
            remAttrs.each { beanNode.attributes().remove(it) }
        }
        log.finer "finishing fix properties: " + beanNode
    }

    protected void createProperty(NodeBuilder builder, String name, String value) {
        if (value != null) {
            builder.property(name: name, value: value);
        }
    }

    /**
     * used to add comments; current implementation uses meta tags in place of standard
     * comments (node.plus and the xml serialize did not handle xml comments well)
     *
     * @param builder
     * @param comment
     * @return
     */
    def addComment(NodeBuilder builder, String comment) {
        if (comment != null) {
            builder.meta(key: "comment", value: comment)
        }
    }

    protected String fixComments(String fileText) {
        return fileText.replaceAll(/<meta key="comment" value="(.*?)"\/>/, '<!-- $1 -->');
    }

    /**
     * Modifies control and controlField elements into Uif Control elements
     *
     * @param beanNode
     * @param renamedControlBeans
     * @return
     */
    def transformControlProperty(def beanNode, Map<String, String> renamedControlBeans) {
        if (beanNode.property.findAll { "control".equals(it.@name) }.size() > 0) {
            Node beanProperty = beanNode.property.find { "control".equals(it.@name) };
            String controlDefParent = beanProperty.bean[0].@parent.toString();
            if (beanProperty != null) {
                if (beanNode.property.findAll { ["control", "controlField"].contains(it.@name) }.size() == 2) {
                    this.removeProperties(beanNode, ["control"]);
                } else {
                    beanProperty.@parent = "controlField"; // rename property as control field
                    beanProperty.replaceNode {
                        if (renamedControlBeans.get(controlDefParent) != null) {
                            property(name: "controlField") {
                                transformControlDefinitionBean(delegate, beanProperty.bean[0], renamedControlBeans)
                            }
                        }
                    }

                    if ("Uif-DropdownControl".equals(renamedControlBeans.get(controlDefParent))) {
                        beanProperty.plus {
                            property(name: "optionsFinder") {
                                bean(class: "org.kuali.rice.krad.keyvalues.PersistableBusinessObjectValuesFinder")
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Used for transforming control definitions into control field properties
     *
     * @param builder
     * @param controlDefBean
     * @param controlDefReplacements
     * @return
     */
    def transformControlDefinitionBean(NodeBuilder builder, Node controlDefBean, Map<String, String> controlDefReplacements) {
        String controlDefParent = controlDefBean.@parent.toString();
        if (controlDefReplacements[controlDefParent] != null && controlDefReplacements[controlDefParent] == "Uif-DropdownControl") {
            builder.bean(parent: "Uif-DropdownControl")
        } else if (controlDefReplacements[controlDefParent] != null && controlDefReplacements[controlDefParent] == "Uif-TextAreaControl") {
            //
        } else if (controlDefReplacements[controlDefParent] != null) {
            builder.bean(parent: controlDefReplacements[controlDefParent])
        } else {
            builder.bean(parent: "Uif-" + controlDefParent.replace("Definition", ""))
        }
    }

    /**
     * Converts title attribute into property bean along with a primary keys property
     *
     * @param beanNode
     */
    private void transformTitleAttribute(beanNode) {
        def titleAttrBeanNode = beanNode.property.find { it.@name == "titleAttribute" }
        if (titleAttrBeanNode != null) {
            titleAttrBeanNode.replaceNode {
                property(name: "titleAttribute", value: titleAttrBeanNode.@value)
                property(name: "primaryKeys") {
                    list {
                        value(titleAttrBeanNode.@value)
                    }
                }
            }
        }
    }

}
