--
-- Copyright 2005-2015 The Kuali Foundation
--
-- Licensed under the Educational Community License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
--
-- http://www.opensource.org/licenses/ecl2.php
--
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
--

--
--  KULRICE-7377: KREW_RTE_NODE_T still defines DOC_TYP_ID as NUMBER(19)
--

-- drop the old one
DROP TABLE OLD_KREW_RTE_NODE_T CASCADE CONSTRAINTS PURGE
/


--
--  KULRICE-7375: Rice master data source has KREW_DOC_TYP_PROC_T.INIT_RTE_NODE_ID still defined as a NUMBER
--

-- drop the old one
DROP TABLE OLD_KREW_DOC_TYP_PROC_T CASCADE CONSTRAINTS PURGE
/
