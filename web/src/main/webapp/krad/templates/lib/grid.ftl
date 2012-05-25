<#--
  ~ Copyright 2006-2012 The Kuali Foundation
  ~
  ~ Licensed under the Educational Community License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~ http://www.opensource.org/licenses/ecl2.php
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  -->

<#macro grid items firstLineStyle numberOfColumns=2 renderFirstRowHeader=false renderHeaderRow=false applyAlternatingRowStyles=false
applyDefaultCellWidths=true renderRowFirstCellHeader=false renderAlternatingHeaderColumns=false>

    <#assign defaultCellWidth="${100/numberOfColumns}"/>

    <#assign colCount=0/>
    <#assign carryOverColCount=0/>
    <#assign tmpCarryOverColCount=0/>

    <#list items as item>
        <#assign colCount=colCount + 1/>
        <#assign firstRow=(item_index == 0)/>

        <#-- begin table row -->
        <#if (colCount == 1) || (numberOfColumns == 1) || (colCount % numberOfColumns == 1)>
            <#if applyAlternatingRowStyles>
                <#if evenOddClass == "even">
                    <#assign eventOddClass="odd"/>
                <#else>
                    <#assign eventOddClass="even"/>
                </#if>
            </#if>


            <#if firstRow && firstLineStyle?has_content>
              <tr class="${firstLineStyle}">
            <#else>
              <tr class="${evenOddClass}">
            </#if>

            <#-- if alternating header columns, force first cell of row to be header -->
            <#if renderAlternatingHeaderColumns>
                <#assign renderAlternateHeader=true/>
            </#if>

            <#-- if render first cell of each row as header, set cell to be rendered as header -->
            <#if renderRowFirstCellHeader>
                <#assign renderFirstCellHeader=true/>
            </#if>
        </#if>

        <#-- build cells for row -->

        <#-- skip column positions from previous rowspan -->
        <#list 1..carryOverColCount as i>
            <#assign colCount=colCount + 1/>
            <#assign carryOverColCount=carryOverColCount - 1/>

            <#if colCount % numberOfColumns == 0>
              </tr><tr>
            </#if>
        </#list>

        <#-- determine cell width by using default or configured width -->
        <#if item.width?has_content>
            <#assign cellWidth=item.width/>
        <#elseif applyDefaultCellWidths>
            <#assign cellWidth="${defaultCellWidth * item.colSpan}%"/>
        </#if>

        <#if cellWidth?has_content>
            <#assign cellWidth="width=\"${cellWidth}\""/>
        </#if>
    
        <#assign singleCellRow=(numberOfColumns == 1) || (item.colSpan == numberOfColumns)/>
        <#assign renderHeaderColumn=renderHeaderRow || (renderFirstRowHeader && firstRow)
                 || ((renderFirstCellHeader || renderAlternateHeader) && !singleCellRow)/>

        <#if renderHeaderColumn>
            <#if renderHeaderRow || (renderFirstRowHeader && firstRow)>
              <#assign headerScope="col"/>
            <#else>
              <#assign headerScope="row"/>
            </#if>

            <th scope="${headerScope}" ${cellWidth} colspan="${item.colSpan}"
                rowspan="${item.rowSpan}" ${attrBuild(component)}>
                <@template component=item/>
            </th>
        <#else>
            <td role="presentation" ${cellWidth} colspan="${item.colSpan}"
                rowspan="${item.rowSpan}" ${attrBuild(component)}>
                <@template component=item/>
            </td>
        </#if>

        <#-- flip alternating flags -->
        <#if renderAlternatingHeaderColumns>
            <#assign renderAlternateHeader=!renderAlternateHeader/>
        </#if>

        <#if renderRowFirstCellHeader>
            <#assign renderFirstCellHeader=false/>
        </#if>

        <#assign colCount=colCount + item.colSpan - 1/>

        <#-- set carry over count to hold positions for fields that span multiple rows -->
        <#assign tmpCarryOverColCount=tmpCarryOverColCount + item.rowSpan - 1/>

        <#-- end table row -->
        <#if !item_has_next || (colCount % numberOfColumns) == 0>
           </tr>

           <#assign carryOverColCount=carryOverColCount + tmpCarryOverColCount/>
           <#assign tmpCarryOverColCount=0/>
        </#if>
    </#list>

</#macro>