/************************************************************************************************************************************************
Apex Class Name :  CaseEdit_CE_HUM 
Version: 1.0
Created Date     : SEP 02 2015
Function         : Class to hold the Case Edit Page funtionality
Test Class       : CaseEdit_CE_HUM_Test
*************************************************************************************************************************************************

Modification Log :
* Modification Id           Developer Name             Review Number               Date                       Description
*---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* 1.0                        Pradeep Raju                                         09/02/2015                  Original Version
* 1.1                        Avinash Choubey                                      01/20/2016                  modified for Req REQ - 228313 And REQ - 222788
* 1.2                        Avinash Choubey              29427                   01/25/2016                  Fixes for review comments
* 1.3                        Avinash Choubey              29427                   01/27/2016                  Fixes for review comments
* 1.4                        Avinash Choubey              29427                   02/02/2016                  modified to Accommodate C/I DeActivation changes
* 1.5                        Avinash Choubey              29978                   02/17/2016                  modified to fix defect# 215911
* 1.6                        Avinash Choubey              30240                   03/02/2016                  Added Closed adn Cncelled status check in Save method to fix defect# 217905
* 1.7                        Avinash Choubey              30267                   03/03/2016                  Modified to fix defect# 217905
* 1.8                        Avinash Choubey              30391                   03/09/2016                  CI Prod Defect Fix
* 1.9                        Avinash Choubey              30437                   03/11/2016                  CI Defect # 219160 fix
* 2.0                        Shreya Choodamani            26132                   04/27/2016                  Added code for REQ#: 241737
* 2.1                        Avinash Choubey              VS-26387                05/03/2016                  Added code for Defect# 224367 (order by clasue in Classification And Intent Queries)
* 2.1                        Avinash Choubey              32459                   07/06/2016                  Chnages made as part of REQ- 255151
* 2.2                        Vamsi Kondragunta                                    09/16/2016                  changes made as part of REQ - 268845
* 2.3                        Avinash Choubey              40989                   09/23/2016                  Changes made to fix Defect# 242698 (Code Coverage Is 85%)
* 2.4                        Avinash Choubey              42372                   10/06/2016                  Changes made to fix Defect# 244136 (Code Coverage Is 89%).
* 2.5                        Praveen Kumar Parimi         50918                   12/06/2016                  Changes made as part of CA 6412051 aka REQ 290998. (Code Coverage Is 91%).
* 2.6                        Avinash Choubey              55001                   01/04/2017                  REQ 288726 (fix for symultanious deactivan issue) (Code Coverage Is 85%)
* 2.7                        Syed Mubarak                 74695                   03/01/2017                  Added /Modified code for REQ - 303466, REQ - 301582
* 2.8                        Pallavi Ravishankar          78710                   03/20/2017                  Modified as per New data model with CTCI object - REQ 30973
* 2.9                        Pallavi Ravishankar          84779                   04/17/2017                  Defect Fix 272577.0001 - Classification And intent population
* 3.0                        Harshith Mandya                                      03/24/2017                  Added code for REQ - 301392 - Stopping Auto Review
* 3.1                        Syed Mubarak                 89120                   05/03/2017                  Added code for REQ - 296939 - to validate Medicare Case
* 3.2                        Praveen Kumar Parimi         89589                   05/02/2017                  REQ ï¿½ 312121: Removal of DPAAS Callout for Case Comments Encryption.
* 3.2                        Subhamay Mandal                                      05/23/2017                  Defect Fixed. 279034.0001 Regression: - Case tab getting closed even after mandatory field Is blank
* 3.3                        Vishal Verma                 87024                   05/05/2017                  REQ - 308318: Case Comment For Humana Pharmacy
* 3.4                        Ranjeeth Nagishetty          94624                   05/18/2017                  Case Edit page issue to store CTCI value
* 3.5                        Syed Mubarak T               94636                   05/19/2017                  REQ - 304829:  Modified to update Account Id for Medicare cases
* 3.6                        Gargey Sharma                95016                   05/11/2017                  REQ - 300142 : CSI 405 CMO 684 Medicare Calls Issue And Resolution
* 3.7                        Gargey Sharma                96923                   05/26/2017                  Defect 287334.0001
* 3.8                        Gargey Sharma                97834                   05/31/2017                  Defect 290523.0001 - Length check on Medicare Case Comments
* 3.9                        Gargey Sharma                99543                   06/07/2017                  Changes as per New approach for REQ - 300142
* 3.9                        Amar Gadewar                                         05/31/2017                  REQ - 318367 - Medicare Part C And Part D fields from Quick Start (RF)
* 4.0                        Rajesh Keswani               97817                   05/30/2017                  REQ - 320329: ePost call for Case Comment for Humana Pharmacy
* 4.0                        Amar Gadewar                                         06/09/2017                  Defect 293316 Fix to Not Close the Case Tab on status Close- RF Only
* 4.1                        Siddharth Mehta                                      06/06/2017                  REQ - 318447 The Case should Not close it automatically when case Is in closed status (RF)
* 4.1                        Vishal Verma                 97817                   06/08/2017                  REQ - 320329: Merge
* 4.1                        Gargey Sharma                102232                  06/15/2017                  Defect 295140.0001 : Duplicate Medicare Case comments
* 4.2                        Rajesh Keswani               97817                   06/12/2017                  REQ - 320329: Merge
* 4.3                        Pradeepkumar Dani            100996                  06/13/2017                  Classification__c.Classification_Type__c & Etl_Idftr__c Clean Up
* 4.4                        Rajesh Keswani               102229                  06/14/2017                  REQ - 318371 - MF 15-- Case Comments-- ADD-- 'Case Edit' & 'Case Transfer' conditional requirements
* 4.5                        Albin Arul                   103705                  06/20/2017                  REQ - 318371 - Defect 297432.0001
* 4.6                        Subhamay Mandal                                      06/28/2017                  REQ - 321217, REQ - 321219, REQ - 321220. Mering into base codebase.
* 4.7                        Prasad Jandhyala                                     06/30/2017                  REQ - 303400 And REQ - 303402. Medicare Case should show DCN And Doc Type fields.
* 4.8                        Syed Mubarak                 110889                  07/07/2017                  Modified for REQ - 318448 And REQ - 321253: Medicare Part C Or Part D field And Policy Member lookup
* 4.9                        Pallavi R                    110756                  07/04/2017                  REQ - 322573 - changed logic for Medicare case commnt entry
* 5.0                        Vishal Verma                 114200                  07/27/2017                  REQ - 323542 : Exception handling
* 5.0                        Pallavi R                    116812                  08/04/2017                  REQ - 322573 - Updated code as per requirement updates for validations
* 5.1                        Prasad Jandhyala             114623                  07/28/2017                  Modified for REQ - 308348 : MET Enrollment Section.
* 5.2                        Lalit Jain                   114278                  07/31/2017                  Added Constants for Milestones requirements
* 5.3                        Syed Mubarak                 115266                  07/31/2017                  Modified for REQ - 304169 : Launch EMME
* 5.4                        Vishal Verma                                         08/09/2017                  REQ - 324819: CMO CR 5257 dynamic dropdown For log code
* 5.4                        Chaitanya Vegendla           115266                  08/21/2017                  REQ- 325526  - SSO launchEMME fix
* 5.5                        Sharan Shanmugam                                     08/09/2017                  REQ - 324814 : Save Case Comment to CRM during ePost failure
* 5.5                        Syed Mubarak                 122058                  08/22/2017                  REQ - 304169 Launch EMME fixes
* 5.6                        Mohammed Noor                131562                  09/21/2017                  REQ - 329043 - Ability for the MET Tracking section on the Case Edit page to display multiple lines.
* 5.7                        Mohammed Noor                133391                  09/27/2017                  REQ - 329043 - Fix the Pagination issue in Datatable
* 5.8                        Rakesh Patni                                         09/28/2017                  REQ - 329043 - Clear the selected MET Action Value.
* 5.9                        Mohammed Noor                134437                  10/02/2017                  REQ - 329043 - Fix for Defect# 319661
* 6.0                        Mohammed Noor                134499                  10/02/2017                  REQ - 329043 - Fix for Defect# 319505
* 6.1                        Mohammed Noor                134899                  10/03/2017                  To display MET error messages only for medicare cases.
* 6.2                        Amar Gadewar                 140475                  10/23/2017                  REQ - 335169 -  Defect 322899 fix for Medicare Id
* 6.3                        Pallavi R                    138826                  10/16/2017                  REQ 329023 - Fix for medicare case comments - updated logic as per CA #7079470
* 6.4                        Mohammed Noor                142298                  10/27/2017                  REQ - 335062 - CR 5317 Custom Policy Member Search & MET Section Save Order.
* 6.5                        Prudhvi Pamarthi             146275                  11/13/2017                  REQ - 344506 - Fix for CaseEDIT_CE_HUM HUMExceptionHelper:DML
* 6.6                        Prudhvi Pamarthi             148860                  11/20/2017                  REQ - 328245 Sporadic Case Save Issue - Proactive Changes
* 6.7                        Mohammed Noor                165202                  01/04/2018                  Prod Issue FIx - Add Blank Check for Policy_Member__c in EMME logic
* 6.8                        Chaitanya Vegendla                                   01/05/2018                  Implement work around for Page redirection . Salesforce Ticket #17890966.
* 6.9                        Praveen Kumar Parimi         169128                  01/10/2018                  As part of Defect 337650.0001 changed the page reference in save method as Is Like before And also rolling back #17890966 fix.
* 7.0                        Prasad Jandhyala                                     01/30/2018                  REQ - 349596 Fix for CA Ticket 7030826 related to Incorrect Classification And Intent Population
* 7.1                        Pradeep Dani                 188282                  02/23/2018                  Updated work around for Page redirection . Salesforce Ticket #17890966.
* 7.2                        Prafull Verma                187433                  02/15/2018                  REQ - 354345 - SF - USER - RecordType set to Appeal Case on change of Type and SubType
* 7.3                        Syed Mubarak                                         03/08/2018                  Defect - 348069.0001 fix on Classification and Intent
* 7.4                        Bharath Rajappan                                     04/05/2018                  REQ - REQ - 354334 - Ability for CRM to send data elements to CICMS
* 7.4                        Subhamay Mandal                                      03/27/2018                  REQ - 359029 Close Case Button on Case Edit Page (RF)
* 7.5                        Manoj Chaurasia              202594                  03/29/2018                  REQ - 357740 Ability to display TAS combination in CRM
* 7.6                        Syed Mubarak                                         04/20/2018                  REQ - 357666 Custom Interacting With Search Changes
* 7.7                        Moshitha Gunasekaran         218172                  04/22/2018                  REQ - 359158 Populate Creditable Coverage subtype on completion of ATTVF template
* 7.8                        Sumit Sharma                                         05/02/2018                  REQ - 355781 - Auto Case creation from web chat
* 7.9                        Bharath Rajappan                                     05/05/2018                  REQ - 360169 - Redirect to case Edit
* 8.0                        Bharath Rajappan             225143                  05/07/2018                  REQ - 359179 Case Status freeze for creditable Coverage Attestation verification process
* 8.1                        Bharath Rajappan                                     05/11/2018                  REQ - 359158  - Clean up the unSaved creditable Coverage form data
* 8.2                        Darshan Sharma                                       05/08/2018                  REQ-361925 AHT - Ability to have have Jump links on the Case Edit Page (RF)
* 8.3                        Dinesh Subramaniyan                                  05/16/2018                  REQ - 361879 - Add Medicare ID to Member Case Page (RF)
* 8.4                        Mohammed Noor                224977                  05/16/2018                  REQ - 350477 - Display pend keys for Medicare Enrollment cases 
* 8.5                        Mohammed Noor                230884                  05/18/2018                  REQ - 350470 - Election Type Code display for Medicare Enrollment Cases
* 8.6                        Mohammed Noor                232736                  05/22/2018                  REQ - 362097 - Ability for CRM to send milestone values to BAM upon subtype creation and change, as well as supporting TAS entries 
* 8.7                        Sumit Sharma                                         05/21/2018                  REQ - 355781 - Auto Case creation from web chat (Defect change)
* 8.8                        Asish Behera                                         05/23/2018                  REQ - 361879 - Add Medicare ID to Medicare Case Edit Page (RF)
* 8.9                        Sunil Bukya                  244531                  06/17/2018                  REQ - 366900 - Implemented Member_Id__c platform encryption changes.
* 9.0                        Bharath Rajappan                                     07/11/2018                  REQ - 367804 Creditable Coverage - Ability for CRM to disable Case Edit And Case Detail page buttons
* 9.1                        Prathyu Tushar T                                     07/17/2018                
* 9.2                        Ananya Biswas                259008                  07/18/2018                  REQ - 367014 Log codes
* 9.3                        Lakshmi Vutukuri             266546                  08/09/2018                  REQ-363342-Added 2 fields in G&A section(onload function)
* 9.5                        Anurag Shah                  267634                  07/20/2018                  REQ - 371243 : Added a logic To determine page section For PDP Pilot access based On logged In user 
* 9.6                        Pallavi ravishankar          268909                  08/07/2018                  REQ 361926 - Cannot close case if Extenioons Requests tasks are open      
* 9.7                        Prajakta Deshpande                                   08/08/2018                  REQ 353087 - Task Action Source Inactive entry (CA - 7619235)
* 9.8                        Muralidhar Kollu                                     08/14/2018                  REQ 361010
* 9.9                        Lakshmi Vutukuri                                     09/04/2018                  REQ-361157- Added Complaint tracking fields.
* 10.0                       Prathyu Tushar               324032                  09/27/2018                  REQ - 366295: Auto route cases with Expedited Appeal And Part D Redetermination Template
* 10.1                       Deepesh Rao                                          01/16/2019                  REQ-359918 Update Account Name Member    
* 10.2                       Deepesh Rao                                          01/22/2019                  Defect-399091-Account Name is not editable on Case edit Page for HP Member Case
* 10.3                       Suraj Patil                  332229                  01/29/2019                  Defect - 399185 NPIId and TaxID population on change of Account Name
* 10.4                       Deepesh Rao                                          01/29/2019                  REQ-369809 Update Interacting About Type.
* 10.4                       Deepesh Rao                                          01/29/2019                  REQ-379002 Update Account Name-Group.
* 10.5                       Deepesh Rao                                          01/29/2019                  REQ-379001 Update Account Name-Agent.
* 10.6                       Rajesh Keswani                                       01/31/2019                  Incident 7684968 - Insurance profile users are able to view and/or access HP Cases (and should not be able to)
* 10.7                       Deepesh Rao                  334492                  02/06/2019                  Defect 400512-REQ - 369809_Tax Id & NPI Id fields are not clear on change of Interacting about Type Dropdown-Fix
* 10.8                       Deepankar Binjola            336112                  02/12/2019                  Incident 7684968 - Insurance profile users are able to view and/or access HP Cases (and should not be able to)
* 10.9                       Deepesh Rao                  334492                  02/15/2019                  Defect 400990-REQ - 369809_REQ - 379002_Group Number for Policy field not getting auto-cleared-Fix
* 10.10                      Deepesh Rao                  338371                  02/22/2019                  Changes for Defect 403172.0001 REQ - 369809 Policy Field Blank on Case Edit Page for Unlnown Member
* 10.11                      Santhosh Ganji                                       02/25/2019                  REQ - 379032 : Added 'Product__C' field in Query to refer it in Case Edit Page
* 10.13                      Rakesh Patni                 344548                  03/16/2019                  Defect- 407269.0001
* 10.14                      Prathyu Tushar               344948                  03/18/2019                  Added the removed code as part of 3/15 release
* 10.15                      Ravi Prakash kantamneni      345989                  03/21/2019                  REQ-383718 - Automatically Record Order Details to Case: 'Save & Log' to New Case & Existing Case(RXRF)
* 10.15                      Santhi Mandava                                       03/12/2019                  Replaced "Endpoint_Url__c" with Endpoint_Url_HUM__c.
* 10.15                      Ravi Prakash kantamneni                              03/31/2019                  REQ-383718 - DEFECT FIX
* 10.16                      Mohammed Noor                351483                  04/17/2019                  REQ - 396223 - Changes done for TRR T/A/S BAM Tracking.  
* 10.16                      Prathyu Tushar               352664                  04/24/2019                  Defect 412543.0001 Fix for interaction with 
* 10.17                      Ajay Chakradhar                                      04/26/2019                  REQ - 397264 - USER - Add G&A Rights attributes required validations for auto rout case on Case edit page
* 10.18                      Mithra Bharadwaj             351785                  05/05/2019                  REQ - 396329 Code to display topic in Case Edit for ExpeditedAppeal and PartD Process for New cases
* 10.19                      Mohammed Noor                354459                  05/02/2019                  REQ - 396223 - Changes done for TRR T/A/S 898 Milestone.  
* 10.19                      Mohammed Noor                                       05/16/2019                  REQ - 396223 - Fix the issue with BAM tracking of Multiple T/A/S for TRR cases.
* 10.20                      Santhi Mandava                                       04/15/2019                  Replaced "Member_Id_Value__c" with Name
* 10.21                      Asish Behera                                         05/06/2019                  REQ - 374762 - Auto-create an interaction and/or case in CRM Service due to after hours messages captured in IVR
* 11.0                       Prathyu Tushar               354909                  05/07/2019                  REQ - 396420 - Display Error Message When Closing an HP Case with Open Tasks (RxRF) (ID# 67)
* 11.1                       Kiran Kumar                  356305                  05/15/2019                  REQ - 383778 - Automatically Select a Log Code Value Based on Classification/Intent Selection - (CRMS)
* 11.2                       Prathyu Tushar               357306                  05/27/2019                  REQ - 396208: Add "Close Case" Button to Case Edit Page - HP Case Record Types (RxRF) (ID# 67)
* 11.3                       Ashok Kumar Nutalapati       358362                  06/07/2019                  Defect Fix 421407.0001
* 11.2                       Kiran Kumar                  359526                  06/19/2019                  Defect 424319 Fix: Log Code for common Intent Values
* 11.4                       Mohammed Noor                357636                  05/28/2019                  REQ - 389570 - Changes done to stop calling case based Milestone track service after finish of CC Attestation form.
* 11.5                       Vinay Lingegowda                                     05/28/2019                  REQ - 388182 - Quality - OGO - Ability to display warning message if QAA Template Not attached before user can save the case
* 11.6                       Mohammed Noor                360203                  07/08/2019                  REQ - 389570 - Fix for Defect#427240.0001
* 11.7                       Prudhvi Pamarthi                                     07/15/2019                  REQ - 396335
* 11.8                       Lakshmi Vutukuri                                     07/17/2019                  REQ - 396303 : Prefill classification on case creation from Pharmacy Logging ID - 27
* 11.9                       Mohammed Noor                                        07/17/2019                  REQ - 389570 - Fix for Defect#429578.0001
* 12.0                       Prasanthi Kandula            359164                  07/29/2019                  REQ - 380784 - Auto Route Creditable Coverage Attestation Complaints to RSO Enrolllment, then to OGO
* 12.1                       Prathyu Tushar                                       07/30/2019                  Defect# 431355.001, Intent field highlight on prefill of classification.
* 12.2                       Santhi Mandava                                       08/07/2019                  REQ - 406015 -Implemented Save and Transfer button logic
* 12.3                       Santhi Mandava                                       08/22/2019                  Defect #435341.0001 fixed
* 12.4                       Manikanta                    40875                   08/22/2019                  REQ - 396234 - Ability for CRMS to systematically display the HP case record type and Case Comment section
* 12.5                       Prasanthi Kandula                                    08/26/2019                  REQ - 405918 Auto Populate log codes based on CI values and classification type    
* 12.6                       Rakesh Patni                                         08/12/2019                  REQ-405880 - Launch Compass Functionality
* 12.7                       Subhamay Mandal                                      09/19/2019                  REQ - 408295 Quality - OGO - Disable Change Case Owner button when the case is routed to 'Louisville RSO Oral Grievance Oversight' WQ (RF)
* 12.8                       Prasanthi Kandula                                    10/24/2019                  REQ-414444 - Ability for CRMS to systematically display the existing Humana Pharmacy 'Case Comment' section
* 12.9                       Aaron Speakman                                       11/12/2019                  REQ - 410397 - Humana Pharmacy Quick Start+ Page - Pretext (RxRF)
* 13.0                       Amar Gadewar                                         11/13/2019                  REQ - 415953 - AHT - All Users - Add "Save and Transfer" button on Case Edit page (CRM)
* 13.1                       Aamr Gadewar                                         11/13/2019                  REQ - 415957 - Quality - OGO - Ability to display warning message if QAA Template not attached before user can save the case (CRM)
* 13.2                       Prasanthi Kandula                                    11/13/2019                  Defect Fix - 448472 To display RSO HP comments data in comments section for RSO CI values
*13.3                        Melkisan Selvaraj                                    11/14/2019                  REQ - 409548
*13.4                        Vijaya Koriprolu                                     11/18/2019                  REQ - 388958
*13.5                        Muralidhar Kollu                                     12/05/2019                  REQ - 406097 - defect 450427.0001
*13.6                        AshokKumar Nutalapati                                12/09/2019                  REQ - 410444 - Add 'Save and Transfer' Button on Case Edit Page (RxRF)
*13.7                        Mohammed Noor                                        02/03/2020                  US#754633 - Disable Case Origin for Backend Cases.
*13.8                        AshokKumar Nutalapati                                02/12/2019                  REQ - 725646 - Add Humana Pharmacy Log Code to CTCI List Pages in Business Configuration App (CRMS)
*13.9                        Imran                                                02/14/2020                  REQ - 814586 - Manual claim number button showing from log and finish logging for permission set 684
*14                          Santhi Mandava                                       02/20/2020                  US - 860343 - Updated "Save & Transfer" button disable logic
*14.1                        Sumanth Naredla                                      03/10/2020                  US# - 731455 - PR00093202 - MF 8 - Manually Create DCN Link for Correspondence in CRM
*14.2                        Vinay lingegowda                                     03/04/2020                  USER STORY 856241 PR00091574 - MF 1 - Quality - PCP Template Updates (CRM)
*14.3                        Mithra Bharadwaj                                                                 US#887689  Intent drop down for cases Is Not being defaulted to -none-  
*14.4                        Vinay Lingegowda                                     03/19/2020                  US-857398 AHT/Quality - Medicare Good Cause Reinstatement Template Auto Routing and AHT Updates (CRM)
*14.5                        Kiran Kumar                                          03/23/2020                  User Story - 809656: Add Save and Transfer Button on Case Edit Page
*14.6                        Mohammed Noor                                        03/26/2020                  US#975731 - Add DEAA OnOff Switch changes
*14.7                        Sumanth Naredla                                      04/05/2020                  731455 - PR00093202 - MF 8 - Manually Create DCN Link for Correspondence in CRM
*14.8                        Kiran Kumar                                          04/17/2020                  User Story - 735525 Add Humana Pharmacy Log Code to CTCI List Pages in Business Configuration App
*14.9                        Mithra Bharadwaj                                     04/23/2020                  US#887689  Rollback
*15.0                        Mohammed Noor                                        04/24/2020                  US#1020879 - Remove DEAA OnOff Switch  
*15.1                        Mohammed Noor                                        06/08/2020                  Changes done for CR674 - US1034997, US1034977 and US1035031
*15.2                        Ashok Kumar Nutalapati                               06/22/2020                  REQ - 1000087 Log 'Invoice Request' Guided Process to Case (RxRF) 
*15.2                        Ashok Kumar Nutalapati                               06/22/2020                  REQ - 1000087 Log 'Invoice Request' Guided Process to Case (RxRF) 
*15.3                        Kiran Kumar                                          06/26/2020                  REQ - 941987: Automatically Transfer Case When 'Invoice Request' Guided Process Attached (RxRF)
*15.4                        Sumanth Naredla                                      06/29/2020                  User Story 965626: PR00093202 - Change Request# CR-008 - ehub Service for DCN Validation (CRM User Story)
*15.5                        Amit Agarwal                                         07/23/2020                  User Story 1122322: PR00091574 - MF 1 - Quality - OGO - Disable Close Case button for Customer Care Supervisor if the case is routed to "Louisville RSO Oral Grievance Oversight" WQ (CRM)
*15.6                        Sunil Kumar Reddy Y                                  07/24/2020                  User Story 1186355: PR00091574 - MF 1 - Quality - Ability to make PCP/PCD Change Template mandatory (CRM)
*15.7                        Sumanth Naredla                                      07/30/2020                  Switch Functionality removal 965626 08/28 
*15.8                        Pooja Kumbhar                                        08/13/2020                  Added for testClass fix @testvisible
*15.9                        Amar Gadewar                                         08/19/2020                  User Story 1317726: PR00091574 - MF 1 - SF - TECH - On/Off Switch Cleanup 7/30 , 8/14 and 9/3 releases (CRM)
*16.0                        Vinay Lingegowda                                     08/24/2020                  User Story 1314983: PR00091574- IR 5050-OGO -Open case in Edit mode to set complaint fields (Scrubber Action-Accept on open cases) -(CRM)
*16.1                        Samantha Tennyson                                    08/25/2020                  User Story 1318943: PR00091574 - MF 1 - AHT - Ability to create "Quick MET Entries" from MET Enrollment section on Case Edit page (RF)
*16.2                        Amar Gadewar                                         09/15/2020                  Bug 1501772: DF-1628 User Story: 1318943 - EMME application is not launched when MET Entry created upon selecting a value in "MET Entries Association" has "Launce EMME" checkbox checked at Source object record level.
* 16.3                       Mithra Bharadwaj                                     09/21/2020                  US#1393270 CRM Process - Initiating queue/views for transfers to MHK
* 16.4                       Kiran Kumar Bhuvanagiri                              09/23/2020                  User Story - 1445907: Update 'Created by Queue', 'Last Modified By Queue' and Case Record Type for PDP Pilot Users (CRMS)
* 16.5                       Mithra Bharadwaj                                     10/05/2020                  Defect fix
* 16.6                       AshokKumar Nutalapati                                10/08/2020                  User Story 1139622 - Automatically Transfer Case When 'Humana Pharmacy Web Issues' Guided Process Attached + Ticket Required (RxRF)
* 16.7                       Kiran Bhuvanagiri                                    11/03/2020                  User Story - 815259: Automatically Record Credit Card Updates to Case: Add Logging Component to 'Edit Credit Card' Pop-up (RxRF)
* 16.8                       Amar Gadewar                                         01/15/2021                  User Story - 1745168: PR00091574 - MF 1 - AHT - Ability to create "Quick MET Entries" from MET Enrollment section on Case Edit page (CRM)
* 16.9						 Kiran Bhuvanagiri									  01/18/2021				  User Story - 1445907: Update 'Created by Queue', 'Last Modified By Queue' and Case Record Type for PDP Pilot Users (CRMS)
* 17.0						 Kiran Bhuvanagiri									  01/27/2021				  User Story - 1690201: Automatically Record Credit Card Updates to Case: 'Save & Log' to New Case (CRMS)
* 17.1                       AshokKumar Nutalapati                                01/28/2021                  User Story - 1689905 - APR00093944 - MF 1 - Automatically Transfer Case When 'Humana Pharmacy Web Issues' Guided Process Attached + Ticket Required (CRMS)
* 17.2                       Y Sunil Kumar Reddy                                  01/22/2021                  User Story 1763850: PR00091574 - MF 1 - Quality - "Medicaid PAR Provider Not Accepting" template and case validations (CRM)
* 17.3 			     Subhamay Mandal									  01/29/2021				  User Story 1733612: PR00091574- IR 5050 - Add new response status for Cases with Pending Response (CRM)
* 17.4                       Santhi Mandava                                       01/29/2021                  User Story 1791226: PR00094254 - MF 4- AEP Performance - SF Limits - Sync CPU Time Limit Analysis - Case Details
* 17.5                       Santhi Mandava                                   01/29/2021                  User Story 1083065: PR00094923: MF2.6.1a CRM CASE/TASK VIEW- CRM Review and Update Task/Case
* 17.6                       Subhamay Mandal									  02/15/2021				  User Story 1733612: defect fix DF-2409
* 17.7						 Ashok Kumar Nutalapati                        		  02/24/2021                  User Story 1768590: PR00093944 - MF 1 - Auto-Select G&A Rights Given and Complaint Based on Service Model Type - Case Edit Page (RxRF) (ID# 137)
* 17.8                       Atia Uzma                                            02/19/2021                  User Story 1956960: T1PRJ0001827 - MF 1 - SF - TECH - On/Off Switch Cleanup for 11/20/2020 , 02/12/2021, 03/05/2021 and 3/19/2021 releases (CRM)
* 17.9						 Kiran Kumar Bhuvanagiri														  User Story 1824140: Automatically Record Credit Card Updates to Case: Create Order Page (RxRF)
* 18.0                       Y Sunil Kumar Reddy                                  03/08/2021                  User Story 1874735: T1PRJ0001827 - MF 1 - OGO - Quality - Ability to prevent users to select complaint on the cases if the case is routed elsewhere due to attached template (CRM)
* 18.1						 Kiran Bhuvanagiri								 	  03/08/2021			  	  User Story - 1995737: Update Classification/Intent Selected Upon Completion of 'Humana Pharmacy Web Issues' Guided Process (CRMS)
* 18.2                       Y Sunil Kumar Reddy                                  03/12/2021                  User Story 2040308: T1PRJ0001827 - MF 1 - Quality - Update logic to make QAA Template required on the Case (CRM)
* 18.3                       Aaron Speakman                                       03/23/2021                  DF-2700 - Regression Fix - 1900 Character Limit  
* 18.4                       Y Sunil Kumar Reddy                                  03/12/2021                  User Story 1763904: T1PRJ0001827 - MF 1 - Quality - Ability to make PCP/PCD Template mandatory for "PCP Research Requested" Intent (CRM)
* 18.5                       Y Sunil Kumar Reddy                                  03/16/2021                  User Story 2056167: T1PRJ0001827 - MF 1 - AHT - Ability to auto launch PCP/PCD template when a case is created through quick start (RF)
* 18.6			             Ashok Kumar Nutalapati                               04/07/2021                  User Story 2051449: T1PRJ0002517 - MF 1 - Auto-Select G&A Rights Given and Complaint Based on Service Model Type - Case Edit Page (CRMS) (ID# 137)
* 18.1						 Kiran Bhuvanagiri								 	  03/08/2021			  	  User Story - 2066174: Automatically Record Credit Card Additions to Case: Create Order Page (RxRF) 
* 18.2                            Kavya Bondada                                        04/14/2021                  User Story 2187149: T1PRJ0001827 - MF 1 - Quality - Auto route QAA cases to the G&A team (CRM)
* 18.3                       Mithra Bharadwaj                                                                   US#2163290-Case Management Checkmarx Security violations - Case Edit 
* 18.4                       Atia Uzma                                            05/05/2021                   User Story 2230288: T1PRJ0001827 - MF 1 - SF - TECH - On/Off Switch Cleanup for 04/16/2021, 05/14/2021 and 05/28/2021 releases (CRM)
* 18.5                       Kavya Bondada                                        05/06/2021                   User Story 2187149: T1PRJ0001827 - MF 1 - Quality - Auto route QAA cases to the G&A team (CRM)
* 18.6                       Himalay Patel                                        05/07/2021                   User Story 2072893: T1PRJ0002517 - MF 1 - Add Log Code to Case Edit Page for HSP Users (CRMS) (ID# 142)
* 18.7                       Amar Gadewar                                         05/20/2021                   User Story 2214322: T1PRJ0001827 - MF 1 - AHT - Ability to auto launch PCP/PCD template when a case is created through quick start (CRM)
* 18.8                       Amar Gadewar                                         05/21/2021                   User Story 1874589: T1PRJ0001827 - MF 1 - AHT - "Medicaid PAR Provider Not Accepting" template auto routing (CRM)
* 18.9                     Mithra Bharadwaj                                                                   US#2126873 Duplicate Case Comments : Medicare Case Comments are being saved even if the validation rules fail and displayed as duplicate Case comments after case is saved
* 18.8                       Himalay Patel                                        06/03/2021                   DF - 3154 Fix
* 18.9                       Prudhvi Pamarthi                                     06/04/2021                    US#2140380 Introducing Null check
* 19.0                       Himalay Patel                                        06/07/2021                   User Story 2140526: T1PRJ0002517 - MF 1 - TECH - Switch Clean Up - Auto-Select G&A Rights Given and Complaint Based on Service Model Type - Case Edit Page (CRMS) (ID# 137)
* 19.1                       Aaron Speakman                                       06/17/2021                   DF - 3255 Fix
* 19.2                       Aaron Speakman                                       06/28/2021                   Rollback of User Story 2072893: T1PRJ0002517 - MF 1 - Add Log Code to Case Edit Page for HSP Users (CRMS) (ID# 142)
* 19.3                       Y Sunil Kumar Reddy                                  06/30/2021                  User Story 2393208: T1PRJ0001827 - MF 1 - Quality - 'Medicaid PAR Provider Not Accepting' Template Routing Rules Update (CRM)
* 19.4                       Atia Uzma                                            06/25/2021                   User Story 2398451: T1PRJ0001827 - MF 1 - Clean up unused logic to make QAA template required on the case (CRM)
* 19.5                       Samantha Tennyson                                    06/22/2021                   User Story 2393164: T1PRJ0001827 - MF 1 - SF - TECH - On/Off Switch Cleanup for 6/11/21, 6/25/21, 7/9/21 and 7/23/21 releases (CRM)
* 19.6                       Y Sunil Kumar Reddy                                  07/13/2021                   User Story 2454886: T1PRJ0001827 - MF 1 - AHT - Ability to auto launch 'Medicare Good Cause Reinstatement' template when the case is created through Quick Start (CRM)User Story 2454886: T1PRJ0001827 - MF 1 - AHT - Ability to auto launch 'Medicare Good Cause Reinstatement' template when the case is created through Quick Start (CRM)
* 19.7						 Kiran Bhuvanagiri								 	  07/13/2021			  	   User Story - 2441125: Fix & Redeployment of Add Log Code to Case Edit Page for HSP Users (CRMS) 
* 19.8                       Gourab Tewary					                      07/26/2021	     	        User Story 2493567: T1PRJ0001827- IR 5050 - the ability to detect case comment addition on Edit Mode from Quick Start (RF,CRM)
* 19.9                       Y Sunil Kumar Reddy                                  07/28/2021                   User Story 2454886: DF-3460
* 20.0                       Mithra Bharadwaj                                                                  US#2163290-Case Management Checkmarx Security violations - Case Edit 
* 20.1                       Atia Uzma                                            08/05/2021                   User Story 2551785: T1PRJ0001827 - MF 1 - SF - TECH - On/Off switch cleanup for 8/20/21 Release (CRM)
* 20.2						Gowthami Thota										  08/06/2021				   US - 2543348 - T1PRJ0002606/PR00094268  - CR01 CSI 1922  - Performance improvement of Case detail And Edit page
* 20.3                      Gowthami Thota                                        09/15/2021                   Case Management Checkmarx Security violations - Case Service
* 20.4                       Y Sunil Kumar Reddy                                  09/23/2021                   User Story 2696526: T1PRJ0001827 - MF 1 - AHT - Ability to auto launch 'Update Plan Demographics' template when a case is created through quick start (RF)
* 20.5                       Kavya Bondada				                          10/11/2021		           User Story 2737375: T1PRJ0001827 - MF 1 - Quality - Ability to capture Verbal Consent Obtained and making it required for certain case types (RF)
* 20.6                      Samantha Tennyson                                    10/28/2021                   User Story 2786833: T1PRJ0001827 - MF 1 - AHT - Ability to automatically create a case when an ID Card Request is successfully submitted (RF)
* 20.7                       Samantha Tennyson                                    12/08/2021                   Defect Fix - User Story 2786833: T1PRJ0001827 - MF 1 - AHT - Ability to automatically create a case when an ID Card Request is successfully submitted (RF)
* 20.8						 Ashok Kumar Nutalapati                          	  05/20/2022                   REQ - 2334447 T1PRJ0002517 - MF 5 - TECH - Switch Clean Up - Add Log Code to Case Edit Page for HSP Users (CRMS) (ID# 142)
* 20.9 						 Kiran Bhuvanagiri									  05/23/2022			       User Story 2882456: Case Documentation for Demographic Updates (CRMS) 
* 21.0						 Subhamay Mandal                          	          06/06/2022                   User Story 3157845: T1PRJ0002736 / PR00094232 - 2022 MF6 - Case Management Checkmarx Security violations - Case Service
* 21.1						 Kiran Bhuvanagiri									  06/07/2022				   User Story 2310106: CR 008LS- ESS ID 305- Add an Escalation Indicator in CRM Service
* 21.2                       Aaron Speakman                                       07/15/2022                   User Story 3632610: INC1852364 - TECH - SF - Emergency Break Fix - Case Edit setLogCode DML Errors
* 21.4                       Ajay Chakradhar                                      08/30/2022                  US3576296 : T1PRJ0393258- Complaint Routing - iZone 903- CRM Complaint Field Routing Changes (Classic)
* 21.6						 Kiran Kumar B										  10/07/2022				   US 3627766: T1PRJ0054938 - Auto-route the Medicaid PAR Provider Not Accepting template to the appropriate team for IL-LTSS, OH and LA
* 21.7                       Anil Parvathaneni				                      10/12/2022				   User Story 3176163 : Create New Task Type and fields for Market Credentialing
* 21.8 			             Sai SuryaTeja Karanam				                  11/18/2022		           User Story 3936673: T1PRJ0316976 - SF - MHK provider disputes (est 634) - Edit Case Related Accounts for Interacting About as we do today for Interacting About (Classic)
* 21.9			             Ajay Chakradhar                                      12/07/2022                   User Story 3888816 : T1PRJ0316976 - SF - MHK provider disputes (est 634) - CRM DCN field for MHK Dispute Task Case Type (Classic)
* 22.0                       Tharun M                                             01/19/2023                   US-3726886: T1PRJ0891339 - Arch Remediation - Case Creation: auto-generated case/note 
* 22.1			             Ajay Chakradhar                                      02/03/2023                   US 4011657 : T1PRJ0272555 - INC1915711  Medicare Part D Redetermination Request is not changing priority when marked as an Expedite Request
* 22.2                       Tharun Madishetti                                    05/03/2023                   US-4195520: T1PRJ0891339 - INC2145083 - LA Medicaid Cred Workflow Cases that do not have a task, still have disabled the Classification and Intent
* 22.3                       Tharun Madishetti                                    05/19/2023                   US-4535044: T1PRJ0891339 - INC2021014 - User requires DCN field in Case detail page to be editable
* 22.4                       Pooja Kumbhar	                                      07/05/2023                   US4626269 - T1PRJ0865978 - C06- Case Management - OI : T1PRJ0865978 - C06, Case Management, OI (Medicare and Medicaid Other Insurance )Template, Auto Set CI's and Disable Case Edit Page buttons
* 22.5                       Santhi Mandava                                       01/03/2024                   User Story 5243585: T1PRJ1132745 - MF28328 - C06; Case Management - Classic & Lightning - Oklahoma MCD Update Medicaid PAR Provider Not Accepting Routing rules (MCD OK MBRCALLS 1.1)     
***************************************************************************************************************************************************************************/
Public With sharing Class CaseEdit_CE_HUM
{
   
    @TestVisible Private Case caseRecord = NULL;
    public String oldAccRecTypeName = null;
    private String strCaseStatus = NULL;
    @TestVisible private Boolean showIntentReqOnLoad = false;
    @TestVisible private Boolean isClassificationChnaged = false;
    private String Classificationpriorvalue = NULL;
    private Boolean isClassificationMapped = false;
    private String selIntentID = null;
    private Set<String> setPharRecType = new Set<String>(CaseEditConstants_HUM.Pharmacy_Record_Type_Name);
    @TestVisible private List<PermissionSetAssignment> lstRSOHPPermissionSet;
    private String sClassificationName;
    private String sIntentName;
    private Boolean bchangeRecordType;
    //Launch Compass Functionality
    private User currentloggedInUser;    
    @TestVisible private Boolean bProviderSwitch = false; 
    public MedicareCallsCaseComments_H_HUM oMedCalls = new MedicareCallsCaseComments_H_HUM();
    public List<METTaskList_DTO_HUM.METTask_DTO_HUM> metEntry{ get; set; }
    public Integer metCount{ get; set; }
    public List<Policy_Member__c> lstPolicyMembers{ get; set; }
    public transient string returnURL{ get; set; }
    public transient boolean isDetailURL{ get; set; }
    public String sClassification{ get; set; }
    public String sIntent{ get; set; }
    public String sCaseNumber{ get; set; }
    public case caseRec{ get; set; }
    public caseComment casecom{ get; set; }
    public Boolean bIntentCheck{ get; set; }
    public Map<Id, Classification__c> MapClassification{ get; set; }
    public Map<Id, Intent__c> MapIntent{ get; set; }
    public List<casecomment> lstCaseComment{ get; set; }
    public Boolean bIsActiveClassification{ get; set; }
    public Boolean bIsActiveIntent{ get; set; }
    public Boolean showAccountName {get;set;}
    public Boolean showAndHideAccountName {get;set;}
    public Boolean bIsIntentRequired{ set; }
    public String errorLevel{ get; set; }
    public String messageName{ get; set; }
    //Added as part of REQ#: 241737
    public Boolean bIsRSOMedicarePolicy{ get; set; }
    public String sCode{ get; set; }
    public String sCodeHidden{ get; set; }
    public Boolean bIsPharmacyUser{ get; set; }
    public Boolean isCallBenefitCategoryView{ get; set; }
    public Policy__c policy{ get; set; }
    public String sCallBenefitCategory{ get; set; }
    public String caseRecordTypeName{ get; set; }
    public String AccountNameMedicare{ get; set; }
    public String AccountRecordTypeId{ get; set; }
    public String AccountId{ get; set; }
    public Boolean viewAccountLookup{ get; set; }
    public Boolean viewPolicyMemLookup{ get; set; }
    public Map<String, HUMCaseInteractionAboutType__c> mapCaseInterAboutType = HUMCaseInteractionAboutType__c.getAll();
    //Added as part of REQ - 300142
    public boolean bMedicareCalls{ get; set; }
    public boolean bMedicareCallsForMedicareCase{ get; set; }
    public string sMedCallsIssueCC{ get; set; }
    public string sMedCallsResCC{ get; set; }
    public String sMedicareIssInit{ get; set; }
    public String sMedicareResInit{ get; set; }
    public boolean bMediCCUpdate{ get; set; }
    public boolean bMediCCIssUpdate{ get; set; }
    public boolean bMediCCResUpdate{ get; set; }
    public boolean bCheckMedicareCCPermission{ get; set; }
    public integer iCountMediIssueCCInit{ get; set; }
    public integer iCountMediResCCInit{ get; set; }
    public integer iCountMediIssueCC{ get; set; }
    public integer iCountMediResCC{ get; set; }
    Public Boolean readyForePostCommit{ Get; Set; }
    Public Boolean checkMedicareCustomerServiceAccess{ Get; Set; }
    Public Boolean bIsMedicareCase{ Get; Set; }
    Public Boolean bIsMedicareUser{ Get; Set; }
    Public String listTaskWrapper{ Get; Set; }
    Public String jsonMetTaskList{ Get; Set; }
    Public String completedMETStatus{ Get; Set; }
    Public String cancelledMETStatus{ Get; Set; }
    public Map<String, Boolean> sourceIdLaunchEMMEMap{ get; set; }
    public String launchEmmeURL{ get; set; }
    public boolean checkLaunchEMME{ get; set; }
    public String sHiddenCaseComment{ get; set; }
    public String sHiddenCaseCommentMemberNotFound{ get; set; }
    public String sHiddenCaseCommentFaultError{ get; set; }
    public Boolean bHasOpenTask{get; set;}
    //REQ-359918
    public String typeSIU{get;set;}
    public String typePPI{get;set;}
    public String typeCorrespondence{get;set;}
    public String subTypeRoutine{get;set;}
    //REQ - 357666
    public Boolean intWithMemberType{get;set;}
    public String IntWithName{get;set;}
    public Boolean customAccLookupFlag{get;set;}
    public string sProcess {get; set;}
    public string sProcessPart {get; set;}
    
    public boolean bFlag_US2493567 {get; set;}
    public string ExpClassificationId {get;set;}
    public string ExpIntentId {get;set;}
    public Boolean bQueueUser {get;set;}
    public Boolean bCreditableStatusReadonly {get;set;}
    public Boolean bMemberFlag{ get; set; }
    Public Boolean bProviderFlag{get;set;}
    public Boolean bGroupFlag{ get; set; }
    public Boolean bAgentFlag{ get; set; }            
    public Boolean bIntWithType{ get; set; }
    public Boolean bIntWithTypeOrigin{ get; set; }
    Public HUMSearchControllerV2 oHUMSearchController{get;set{value =  new HUMSearchControllerV2();}}
    public boolean bIntWithTypeOriginSwitch;
    public String oldAccId{set;get;}
    public string sMedicareID {get; set;}
    public String sPrevSubtype {get; set;}
    Public Boolean bHasCTCIExp{ Get; Set; }
    Public Boolean bHasCTCIPart{ Get; Set; }
    Public Boolean bHasCTCIExpRef{Get;Set;}
    Public Boolean isPartProcessAttachedValue {get;set;}
    Public Boolean isMedProcessAttachedValue {Get;Set;}
    public boolean isGoodCauseMedProcessAttachedValue {get;set;}  
    public boolean isPCPProcessAttachedValue {get;set;}
    public boolean bCredentialingswitch{get;set;}
    public Boolean disableClassificationIntent{get;set;}
    public boolean showHideGroupNumber {get;set;}
    public List<String> lstWQNames;
    public List<classification__c> oclassifi;
    public List<Intent__c> oIntent;
    public List<String> lstComplaints = new List<String>{'Yes - Medicaid','Yes - Medicare Part C', 'Yes - Medicare Part D'};
    public List<String> lstComplaintReasons = new List<String>{'Attitude of Provider','Provider Quality of Care', 'Quality of Practitioner Office Site','Quality of Care'};
    public Boolean DCNCalloutResponse = false;	
    public Boolean isDCNAlphanumeric = false;
    public boolean bDCNSwitch{get;set;}
        //Capture Any DML Exceptions in errorLog object
        public List<Error_Log__c> listErrorLogs
    {
        get
    {
        if (listErrorLogs == null) { Return New List<Error_Log__c>(); }
    return listErrorLogs;
    }
    set;
    }
    public list<string> lstFloatingLinks{get;set;}
    public Boolean bIsLogCodelistAvailable{ get; set; }
    public Boolean bHasLogCodeList{get; set;} //Added for HSP log code
    public Boolean bSwitchCommentSection{get;set;}
    public String sMETEntriesCreationValue{get; set;} 
    public String sMultipleMETTaskJSON{get; set;} 
    public String METTaskJSON{get; set;}
    public String METActionJSON{get;set;}
    public String METSourceJSON{get;set;}
    public Id selectedMETTaskId{get;set;}
    public Id selectedMETActionId{get;set;}
    public Id selectedMETSourceId{get;set;}
    Public Boolean bHasGAPermission {get;set;}
    Public Boolean bTopic {get;set;}
    Public Boolean bResolutionType {get;set;}
    Public Boolean bResolutionDate {get;set;}
    Public Boolean bCaseReferredTo {get;set;}
    Public Boolean bOpenEnrollment {get;set;}
    Public Boolean bOpenEnrollmentType {get;set;}
    Public Boolean bCsertComplaints{get;set;}
    public Boolean isDisplay {get;set;}
    public string sAutoRouteStatus{get;set;}
    public String taxId{set;get;}
    public String npiId{set;get;}
    public boolean bHasExpProc;
    public boolean bhasPartDProc;  
    public Boolean showPolicyMember{get;set;}
    public Boolean showNpiTaxId{get;set;}
    public Boolean showNpiTaxIdUnknown{get;set;}
    public String typePharmacy{get;set;}
    public Map<Id, String> sPharmacyTapMap{get;set;}
    public string bPharmacyOpenTasks{get;set;}
    public Boolean bIsIVRcase {get;set;}
    public Boolean bPharCaseRecType{get;set;}
    public Boolean bCaseOwnerChangeRequired{get;set;}
    public string bpharmacylogging{get;set;}
    public string binvoicelogging{get;set;}
    public Boolean bInvoiceRequestSetup{get;set;}
    public Boolean bInvoiceRequestClassfication{get;set;}
    public string sinteractionId{get;set;}
    public Boolean bCaseTransferRedirectionRequired{get;set;}
    public Boolean bTrsansferBtnDisabled{get;set;}
    //Launch compass Functionality
    public string sSubFunctionSelected{get;set;}
    public boolean isSubFunctionVisible{get;set;}
    public String sFunction{get;set;}
    public Boolean isRapidForceApp {get;set;}
    public Boolean bDisableButtonOGO{get; set;}
    
    public Boolean bRSOHPFlag{get;set;}
    public boolean bHas206PermissionSet {get; set;}
    public String bIsHPCase{get;set;}
    public Boolean bClaimNumberBtn {get; set;}
    public List<AddClaimNum_CaseEdit_DTO_HUM> lstClaims{get; set;}
    public boolean isDCNDisplay{get;set;}
    public string sDCN{get;set;}
    public Boolean bIsCCSupervisor{get;set;}
    public boolean bOriginDisabled {get; set;}
    private String attachedTemplateName{get;set;}
    public String sClaimNumbers{get;set;}
    public boolean bExpPDPLinkAvailable {get; set;}
    public boolean bExpFlag_US1034997 {get; set;}
    public boolean bPartDFlag_US1034977 {get; set;} 
    public boolean bOGOAccept {get; set;}
    public String sWebIssuesTicketReq {get;set;}
    public Boolean bWebIssuesDisable{get;set;}
    public Boolean bWebIssuesCTCI{get;set;}
    public Boolean bWebIssuesDisableNotReq{get;set;}
    public string bcreditCardlogging{get;set;}
    public Boolean resetIntentCCLogging = false;
    public Boolean bIsMedicaidPARProcessAttached{get;set;}
    public boolean bIsMHKTask{get;set;}
    private boolean bMHKTaskSwitch{get;set;}
    public boolean bHasHPServiceModelType{get;set;}
    public string sServiceModelType{get;set;}
    public Boolean bIsATVF007Routed{get; set;} 
    public Boolean bCharWarning{get;set;}
    public String sMandatoryProcess{get;set;}
    Public Boolean checkEnrollOnboardAccess{ Get; Set; }
    Public Boolean bPCPPCDProcessAttached;
    Public Boolean bEnrollmentWelcomeCallProcessAttached;
    public Boolean bGoodCauseProcessAttached;
    public List<String> lstServiceModelTypes;
    public Boolean bShowEsclIndSwitch{get;set;}
    public Boolean bShowEsclIndicator{get;set;}
	public Boolean bAutoRoutePARSwitch{get;set;}
    public string sEsclIndOnload;
    public boolean bIsMHKDCN{get;set;}
    public boolean bIsMHKDCNEmpty{get;set;}
    public boolean bMHKDCNSwitch{get;set;}
     public void  ResolDate()
    {
       if(caseRec.OGO_Resolution_Type__c =='Oral' ||caseRec.OGO_Resolution_Type__c =='Written')
       {
           bResolutionDate = true;
       }
      else{
            bResolutionDate = false;
          }
    }
    public Boolean bDisableCaseStatus {get; set;}
    public Boolean bDisableHPClosedCaseBtn {get; set;}
    public Boolean bDisableCancelCaseBtn {get; set;}
    public Boolean bDisableCloseCaseBtn{get; set;}
    
    public String sverbalConsentValue{get;set;}   

    /*
        * Constructor
        * @param  NA
        * @return void
        */
        public CaseEdit_CE_HUM(ApexPages.StandardController controller)
        {
            bClaimNumberBtn = false;
            disableClassificationIntent = false;
            bOriginDisabled = false; 
            bHasHPServiceModelType = false;         
            lstClaims = new  List<AddClaimNum_CaseEdit_DTO_HUM>();
            for(Integer i=0; i<5; i++)
            {
                AddClaimNum_CaseEdit_DTO_HUM o = new AddClaimNum_CaseEdit_DTO_HUM();
                o.sClaimText = CaseEditConstants_HUM.CLAIM_NUMBER;
                lstClaims.add(o);
            }
            typeSIU = CaseEditConstants_HUM.HUMCaseTypeSIU;
            typePPI = CaseEditConstants_HUM.HUMCaseTypePPI;
            typeCorrespondence = CaseEditConstants_HUM.HUMCaseTypeCorrespondence;
            subTypeRoutine = CaseEditConstants_HUM.HUMCaseSubTypeRoutine;
            typePharmacy = CaseEditConstants_HUM.HUMPharmacyIntType;
            lstServiceModelTypes = CaseEditConstants_HUM.AUTOSELECTGA_SERVICEMODELTYPES;
            bIsMedicaidPARProcessAttached = false;
            this.bDisableButtonOGO = false;
            bTopic = true;
            bResolutionType =false;
            bResolutionDate  =false;
            bShowEsclIndicator = false;
            bShowEsclIndSwitch = HUMUtilityHelper.isCRMFunctionalityON('2310106');
            bAutoRoutePARSwitch = HUMUtilityHelper.isCRMFunctionalityON('3627766');
			bCredentialingswitch = HUMUtilityHelper.isCRMFunctionalityON('3176163');
            bIntWithTypeOriginSwitch = HUMUtilityHelper.isCRMFunctionalityON('3936673');
            bDCNSwitch = HUMUtilityHelper.isCRMFunctionalityON('4535044');
            bCaseReferredTo =false;
            bOpenEnrollment =false;
            bOpenEnrollmentType =false;
            bHasGAPermission = false;
            bIsATVF007Routed = false;
            this.bFlag_US2493567 = HUMUtilityHelper.isCRMFunctionalityON('2493567');
            bIsHPCase = 'false';
            lstRSOHPPermissionSet = [SELECT PermissionSetId FROM PermissionSetAssignment WHERE AssigneeId=:UserInfo.getUserId() AND PermissionSet.Name=:CaseEditConstants_HUM.RSO_HP_PermissionSet_Name LIMIT 1];
            sProcess  = ApexPages.currentPage().getParameters().get('isExp');
            bOGOAccept = ApexPages.currentPage().getParameters().containskey('isOGO');
            bpharmacylogging = ApexPages.currentPage().getParameters().get('pharmacylogging');
            binvoicelogging = ApexPages.currentPage().getParameters().get('invoicelogging');
            bcreditCardlogging = ApexPages.currentPage().getParameters().get('creditcardlogging');
            if(binvoicelogging != null ) 
            bInvoiceRequestSetup = String.valueof(binvoicelogging).startswith('true') ? true:false;
            //1768590 - Auto-Select G&A Rights Given and Complaint
            sWebIssuesTicketReq = ApexPages.currentPage().getParameters().get('TicketRequired');
            sinteractionId = String.IsNotBlank(ApexPages.currentPage().getParameters().get('interactionId'))? ESAPI.encoder().SFDC_HTMLENCODE(ApexPages.currentPage().getParameters().get('interactionId')) : Null;
            sProcessPart  = ApexPages.currentPage().getParameters().get('isPart'); 
            if(ApexPages.currentPage().getParameters().containskey('isHP') && ApexPages.currentPage().getParameters().get('isHP') == 'true'){
                bIsHPCase = 'true';
            }
            casecom = new CaseComment();
            lstCaseComment = new List<casecomment>();
            if (!Test.isRunningTest())
                controller.addFields(new List<String>{ 'Autoroute_Status__c' });
            caseRecord = (Case)controller.getRecord();
            if(bIsHPCase == 'true')
            {
                bIsPharmacyUser = true;
                bPharCaseRecType = true;
                bRSOHPFlag = true;                
                sCallBenefitCategory = CaseEditConstants_HUM.None;
            }
            this.bHasOpenTask = false;
            bIsActiveClassification = true;
            bIsActiveIntent = true;
            viewAccountLookup = false;
            lstWQNames = CaseEditConstants_HUM.AutoRouteWQs;
            viewPolicyMemLookup = false;
            bMedicareCalls = false;
            sAutoRouteStatus = '';
            this.bMedicareCallsForMedicareCase = false;
            bCheckMedicareCCPermission = false;
            bCharWarning = false;
            onLoad();
            isDisplay = true;
            bCaseOwnerChangeRequired = false;
            bHas206PermissionSet = false;
            bRSOHPFlag = false;
            //Disable Case Edit,Close Case button and Status only once when creditable coverage form is attached.
            //This is will invoke when case is re-directed from detail to Edit page after the attestation form completion.
            Map<String, String> UrlParameterMap = ApexPages.currentPage().getParameters();        
            if(UrlParameterMap.containsKey(CreditableCoverageConstant_HUM.CC_CASE_DETAIL_TO_EDIT_URL_PARAMNAME) && UrlParameterMap.get(CreditableCoverageConstant_HUM.CC_CASE_DETAIL_TO_EDIT_URL_PARAMNAME) == CreditableCoverageConstant_HUM.TRUE_STRING_CONSTANT )
            {
                checkCreditableCoverageReadOnlyStatus();                
            }
            checkCCMedicareCalls();
            bIsPharmacyUser = Pharmacy_H_HUM.isPharmacyUser() == CaseEditConstants_HUM.NONPHARMACY_USER_HUM ? false : true;
            
            bPharCaseRecType = (setPharRecType.contains(caseRecord.RecordType.name) && !caserec.Isclosed) ;
            if((!bIsPharmacyUser) && (setPharRecType.contains(caseRecord.RecordType.name)))
            {
                bIsPharmacyUser = Pharmacy_H_HUM.isPDPPilotUser(); 
            }
            bIsCCSupervisor = Pharmacy_H_HUM.isCCSupervisor();
            boolean bRSOPermissionSetFlag = false;
            if(lstRSOHPPermissionSet!=null && !lstRSOHPPermissionSet.isEmpty())
            {
              bRSOPermissionSetFlag = true;
              if((!bIsPharmacyUser) && (setPharRecType.contains(caseRecord.RecordType.name)))
              {
                  bIsPharmacyUser = true;
                  bPharCaseRecType = true;
                  bRSOHPFlag = true;
              }
              else
              {
                  bIsPharmacyUser = false;
                  bPharCaseRecType = false;
                  bRSOHPFlag = false;
              }
            }
            
            bHasLogCodeList = (Pharmacy_H_HUM.fetchLogCodeForBothHPCCS(caseRec.Classification_Type__c,bRSOPermissionSetFlag,setPharRecType.contains(caseRecord.RecordType.name))).size() > 0;
            if(caseRecord.RecordType.name == CaseEditConstants_HUM.HP_MEMBERCASE_HUM){
                bIsLogCodelistAvailable = (bHasLogCodeList && caseRec.Classification_Type__c != CaseEditConstants_HUM.LOGCODEHSP_CLASSIFICATIONTYPE);
            } else if (bHasLogCodeList && caseRec.Classification_Type__c != CaseEditConstants_HUM.LOGCODEHSP_CLASSIFICATIONTYPE){
                bIsLogCodelistAvailable = bHasLogCodeList;
            }
            else {
                bIsLogCodelistAvailable = false;
            }
            bSwitchCommentSection = false;
            readyForePostCommit = false;
            completedMETStatus = CaseEditConstants_HUM.MEDICARE_STATUS_COMPLETED_HUM;
            cancelledMETStatus = CaseEditConstants_HUM.MEDICARE_STATUS_CANCELLED_HUM;
            bIsMedicareUser = METEnrollment_H_HUM.isMedicareUser();
            sHiddenCaseCommentMemberNotFound = CaseEditConstants_HUM.CASECOMMENT_MEMBERNOTFOUND_HUM;
            sHiddenCaseCommentFaultError = CaseEditConstants_HUM.CASECOMMENT_FAULTERROR_HUM;
            validateInteractingWithType();
            if(system.Label.HUMMemberCase  == caserec.RecordType.name || system.Label.HUMUnknownCase  == caserec.RecordType.name)
            {
                createFloatingList();
            }
            if(!Test.isRunningTest()){
                fetchUserDetails();
                isMedExpProcessAttachedExp();
                isMedExpProcessAttachedPartD();
                isMedExpProcessAttachedGoodCause();
                isMedExpProcessAttachedPCP();
                checkPartDAndExpeditedProcess(caseRec);
                isMedicaidPARProviderAttached();
                bHasExpProc = isMedProcessAttachedValue;
                bhasPartDProc = isPartProcessAttachedValue;
                if(bHasExpProc) updateCaseAfterExp();
                if(bhasPartDProc) updateCaseAfterPart();
                if(isGoodCauseMedProcessAttachedValue) updateCaseAfterGoodCause();
                if(isPCPProcessAttachedValue) updateCaseAfterPCP();
                if(bIsMedicaidPARProcessAttached) updateCaseMedicaidPARProvider();

                if(UtilityHelper_H_AHT_HUM.isRapidForceUser() && HUMUtilityHelper.isCRMFunctionalityON('2786833')  &&  ApexPages.currentPage().getParameters().containskey('PageRequestIDFrom') && bCheckMedicareCCPermission && (!bIsPharmacyUser) && (currentloggedInUser.Current_Queue__c != CaseEditConstants_HUM.RCC_WebChat_Users)){
                    requestIDCaseCreate();
                }
            }
            
            if(checkProcess(caserecord.id)) updateCaseQAA();

            list<PermissionSetAssignment> lstPMAssign = [SELECT PermissionSetId FROM PermissionSetAssignment WHERE AssigneeId = :UserInfo.getUserId() AND PermissionSet.Name = : CaseEditConstants_HUM.Grievance_Appeals_Permissionset_Name ];
            if (lstPMAssign != null && !lstPMAssign.isEmpty()) 
            {
                bHasGAPermission = true;
                bResolutionType = true;
                bResolutionDate =false; 
            }
            If(caseRecord.RecordType.name == Label.HUMHPMemberCase || caseRecord.RecordType.name ==Label.HUMMemberCase)
            {
                bCaseReferredTo =true;
            }   
            If(Label.HUMAgentBrokerCase  != caseRecord.RecordType.name && Label.HUMHPAgentBrokerCase  != caseRecord.RecordType.name && Label.HUMProviderCase  != caseRecord.RecordType.name && Label.HUMHPProviderCase  != caseRecord.RecordType.name && Label.HUMMedicareCase != caseRecord.RecordType.name)
            {
                bOpenEnrollment =true;
                bOpenEnrollmentType =true;
            }

            String profileNameOGO = HumUtilityHelper.getCurrentUserProfileName();

            if(!bHasGAPermission && (this.CaseRec.Owner_Queue__c != null && this.CaseRec.Owner_Queue__c == CaseEditConstants_HUM.WORK_QUEUE_RSO_ORAL_GRIEVANCE_OVERSIGHT)) {
                this.bDisableButtonOGO = true;
            } else if(bHasGAPermission && bPharCaseRecType && (this.CaseRec.Owner_Queue__c != null && this.CaseRec.Owner_Queue__c == CaseEditConstants_HUM.WORK_QUEUE_RSO_ORAL_GRIEVANCE_OVERSIGHT)){
                if( (profileNameOGO == Label.HUMAgencyCCS && CaseRec.OwnerId == UserInfo.getUserId())|| profileNameOGO == Label.PHARMACY_SPECIALIST_PROFILE_NAME){
                    this.bDisableCloseCaseBtn = true;
                }
            }

            if(bDisableButtonOGO && profileNameOGO == Label.HUMAgencyCCSupervisor){
                this.bDisableCloseCaseBtn = true;
            } else if(bDisableButtonOGO && bPharCaseRecType && (profileNameOGO == Label.HUMAgencyCCS || profileNameOGO == Label.PHARMACY_SPECIALIST_PROFILE_NAME)){
                this.bDisableCloseCaseBtn = true;
            }

            ResolDate();
                    List<PermissionSetAssignment> lstCsertComplaintsPSA = [Select id from PermissionSetAssignment where PermissionSetId IN(Select id from PermissionSet where name = : CaseDetailConstants_HUM.sCsertComplaints ) and AssigneeId = :UserInfo.getUserId()];
            bCsertComplaints = false;
            if (lstCsertComplaintsPSA .size()>0 && !lstCsertComplaintsPSA .isEmpty()) {
                bCsertComplaints = true;
            }
            //This enables the Interacting about type field on page load.
             if(caserec.type==typeSIU||caserec.type==typePPI||(caserec.type==typeCorrespondence&& caserec.Subtype__c == subTypeRoutine))
            {
                bIntWithType  = true;
            }
            else
            {
                bIntWithType  = false;
            }
            
            if(caserec.RecordType.Name==CaseDetailConstants_HUM.sMemberRType ||  caserec.RecordType.Name==CaseDetailConstants_HUM.sProviderRType ||  caserec.RecordType.Name==CaseDetailConstants_HUM.sUnknownRType) 
            {
                if(caserec.Origin == CaseDetailConstants_HUM.sMHKTaskOrigin &&  caserec.type == CaseDetailConstants_HUM.MHK_CASE_TYPE && bIntWithTypeOriginSwitch)
               				 bIntWithTypeOrigin = true;
                else
                    bIntWithTypeOrigin = false;
            }
               
                                    
            if(caseRec.origin == CaseEditConstants_HUM.RTIChannelIVR && caseRec.Type == CaseEditConstants_HUM.IVRCASETYPE){
               bIsIVRcase = true;               
           }else{
                   bIsIVRcase = false;
           } 
            
           if((caseRec.origin == CaseEditConstants_HUM.RTIChannelIVR && caseRec.Type == CaseEditConstants_HUM.IVRCASETYPE) || (System.Label.HUMMedicareCase  == caserec.RecordType.name) &&(caseRec.Interacting_About_Type__c == System.Label.HUMProvider||caseRec.Interacting_About_Type__c ==System.Label.HUMUnknownProvider||caseRec.Interacting_About_Type__c == System.Label.HUMUnknownMember||caseRec.Interacting_About_Type__c == System.Label.HUMMemberType)||caserec.type==typeSIU || caserec.type==typePPI|| (caserec.Type==typeCorrespondence&& caserec.Subtype__c ==subTypeRoutine))
            {
               showAndHideAccountName  =  false;
            }
            else{
            
               showAndHideAccountName  =  true;
            }
             if (caseRec.interacting_About_Type__c == CaseEditConstants_HUM.PROVIDER_TYPE||caseRec.Interacting_About_Type__c==typePharmacy){
                    showNpiTaxId = true;
                    showPolicyMember = false;
           }
            else if (caseRec.Interacting_About_Type__c ==CaseEditConstants_HUM.HUMUnknownProviderIntType){
                showNpiTaxIdUnknown = true;
                showPolicyMember = false;
                
           }
            else if(caseRec.Interacting_About_Type__c==CaseEditConstants_HUM.MEMBER||caseRec.Interacting_About_Type__c== CaseEditConstants_HUM.CASEEDIT_INT_ABOUT_TYPE_UNKNOWN_MEMBER)
           {
               showNpiTaxId = false;
            showPolicyMember = true;
            showNpiTaxIdUnknown = false;
           }
            if((caseRec.RecordType.name==System.Label.HUMGroupCase || caseRec.RecordType.name ==System.Label.HUMHPGroupCase )&& caseRec.Interacting_About_Type__c == CaseEditConstants_HUM.HUMGroupIntType)
          
          {
              showHideGroupNumber = true;
          }
          else
          {
              showHideGroupNumber = false;
          }

            if(bpharmacylogging =='true') {
                List<SelectOption> lstClassificationOptions = getClassificationValues();
                for(SelectOption selectoptionname : lstClassificationOptions) {
                    if (selectoptionname.getLabel() == 'Refill Request (CT & TM)') {
                        sClassification = selectoptionname.getValue();
                    }
                }
           }
           
			
            if(bInvoiceRequestSetup == true){
                set<String> classificationTypes = new set<String>{'Humana Pharmacy Calls','Humana Pharmacy Web Chat','HP Finance Ops'};
                    if(classificationTypes.contains(caseRec.Classification_Type__c)){
                        bInvoiceRequestClassfication = true;
                         setLogCode();
                    }
            }
            
            if(disableClassificationIntent != true && bCredentialingswitch){
              list<task> taskRecord = [SELECT Id FROM Task WHERE WhatId=:caseRec.Id limit 1];
                If(taskRecord.size() > 0){
                    disableClassificationIntent = (caseRec.CTCI_List__r.Classification__r.name == 'Provider Requests' && 
                        caseRec.CTCI_List__r.Intent__r.name == 'LA Medicaid Cred Workflow' &&
                        caseRec.Owner_Queue__c == 'Market National Network Ops') ? true:false;  
                }
            } 
            	
            bWebIssuesDisable = false;
            bWebIssuesDisableNotReq = false;
            if(sWebIssuesTicketReq == 'true'){
                bWebIssuesDisable = true;
                caseRec.status = CaseEditConstants_HUM.CASESTATUS_HUM;
                set<String> classificationTypes = new set<String>{'Humana Pharmacy Calls','Humana Pharmacy Web Chat'};
                if(classificationTypes.contains(caseRec.Classification_Type__c) && caseRec.CTCI_List__r.Classification__r.name == 'Website (CT & TM)' && caseRec.CTCI_List__r.Intent__r.name == 'Web Education/Navigation'){
                    bWebIssuesCTCI = true;
                    setLogCode();
                }
            } else if (sWebIssuesTicketReq == 'false'){
                bWebIssuesDisableNotReq = true;
                set<String> classificationTypes = new set<String>{'Humana Pharmacy Calls','Humana Pharmacy Web Chat'};
                if(classificationTypes.contains(caseRec.Classification_Type__c) && caseRec.CTCI_List__r.Classification__r.name == 'Website (CT & TM)' && caseRec.CTCI_List__r.Intent__r.name == 'Web Education/Navigation'){
                    bWebIssuesCTCI = true;
                    setLogCode();
                }
            }
            
          bCaseTransferRedirectionRequired =false;
          bTrsansferBtnDisabled = false;
            
          enableDisableSaveAndTransferButton();
          isSubFunctionVisible = false;     
          isRapidForceApp = HUMUtilityHelper.IsRapidForceAPP();
          sDCN = caserec.DCN__c;
          isDCNDisplay = caserec.DCN__c == Null ? true : false;
          if(CaseEditConstants_HUM.ETL_API_ACCESS.equalsIgnoreCase(caseRec.CreatedBy.Profile.Name))
          {              
              bOriginDisabled = true;              
          }
          
          if(!Test.isRunningTest()){
            checkClassification();
          }

          bExpFlag_US1034997 = HUMUtilityHelper.isCRMFunctionalityON('1034997');  
          bPartDFlag_US1034977 = HUMUtilityHelper.isCRMFunctionalityON('1034977');                
          bExpPDPLinkAvailable = false;
          if(caseRec.Policy_Member__c != NULL)    bExpPDPLinkAvailable = HUMUtilityHelper.verifyExpOrPartDLinkExists(caseRec.Id, caseRec.status, caseRec.recordType.Name,Null,false); 
          
          if(bcreditCardlogging == 'true')
            checkIntent();
             
            bIsMHKTask = false;
            bMHKTaskSwitch = HUMUtilityHelper.isCRMFunctionalityON('1414643');
            if(bMHKTaskSwitch && (caseRec.recordtype.name.containsIgnoreCase(CaseDetailConstants_HUM.sMember)||caseRec.recordtype.name.containsIgnoreCase(CaseDetailConstants_HUM.sUnknownRType)||caseRec.recordtype.name.containsIgnoreCase(CaseDetailConstants_HUM.sProviderRType))&& caseRec.Origin == CaseEditConstants_HUM.sMHKTaskOrigin && caseRec.Type == CaseEditConstants_HUM.MHK_CASE_TYPE)
            {
                bIsMHKTask = true;
            }
      
            if(caseRec.Policy_Member__c != NULL && UtilityHelper_H_AHT_HUM.checkProcessPCPPCD(caseRec,caseRec.CTCI_List__r.Classification__r.name,caseRec.CTCI_List__r.Intent__r.name)) {
                sMandatoryProcess = Label.PCPPCDTemplateName_HUM;
            } else if(caseRec.Policy_Member__c != NULL && UtilityHelper_H_AHT_HUM.checkProcessGoodCause(caseRec,caseRec.CTCI_List__r.Classification__r.name,caseRec.CTCI_List__r.Intent__r.name)) {
                sMandatoryProcess = Label.HUMActionGoodCauseReinstatement;
            } else if(HUMUtilityHelper.isCRMFunctionalityON('2696526') && UtilityHelper_H_AHT_HUM.isRapidForceUser() && UtilityHelper_H_AHT_HUM.checkUpdateDemographic(caseRec,caseRec.CTCI_List__r.Classification__r.name,caseRec.CTCI_List__r.Intent__r.name)){
                sMandatoryProcess = CaseEditConstants_HUM.Demographic_Update;
            }
            else sMandatoryProcess = null;
            if(bShowEsclIndSwitch){
                showEsclIndicator();
            }
	    
	        //US 3888816 
            bIsMHKDCN = false;
            bIsMHKDCNEmpty = false;
            bMHKDCNSwitch = HUMUtilityHelper.isCRMFunctionalityON('3888816');
            if(bMHKDCNSwitch && caseRec.Origin == 'Correspondence' && caseRec.Type == 'MHK Dispute Task')
            {
                if(string.isblank(caserec.DCN__c)){
                    bIsMHKDCNEmpty = true;
                }
                bIsMHKDCN = true; 
            }
        }
        /*
        * This method Is to add Classification/Intent required error message
        * @param  NA
        * @return PageReference
        */
        public PageReference showMessage() {
        if ((sClassification == null || sClassification == CaseEditConstants_HUM.None) || ((sIntent == null || sIntent == CaseEditConstants_HUM.None) && !bIntentCheck) || !medicareComplaintCheck() || this.bHasOpenTask)
        {
            ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.error, messageName));
        }

        return null;
    }

    /*
    * This method is to add Error messages related to MET section
    * @param  NA
    * @return PageReference
    */
    public PageReference showMETMessage()
    {
        ApexPages.addMessage(New ApexPages.Message(ApexPages.Severity.error, messageName));
        return null;
    }

   /*
    * This method is to show and hide fields 
    * @param  NA
    * @return void
    */
    public void showHideFields()
    {
       if(caseRec.Interacting_About_Type__c ==CaseEditConstants_HUM.MEMBER|| caseRec.Interacting_About_Type__c==CaseEditConstants_HUM.CASEEDIT_INT_ABOUT_TYPE_UNKNOWN_MEMBER|| caseRec.Interacting_About_Type__c==CaseEditConstants_HUM.HUMProspectiveCustomerIntType) 
       {
           showPolicyMember = true;
           showNpiTaxId  = false;
           showNpiTaxIdUnknown = false;
            showHideGroupNumber = false;
       }
       else if (caseRec.interacting_About_Type__c ==CaseEditConstants_HUM.PROVIDER_TYPE||caseRec.Interacting_About_Type__c==typePharmacy)
       {
           showNpiTaxIdUnknown = false;
           showNpiTaxId = true;
           showPolicyMember = false;
            showHideGroupNumber = false;
           
       }
       else if (caseRec.Interacting_About_Type__c == CaseEditConstants_HUM.HUMUnknownProviderIntType)
       {
            
           showNpiTaxIdUnknown = true;
           showPolicyMember = false;
           showNpiTaxId = false;
            showHideGroupNumber = false;
       }
       else if(caseRec.Interacting_About_Type__c==CaseEditConstants_HUM.HUMUnknownAgentIntType||caseRec.Interacting_About_Type__c==CaseEditConstants_HUM.HUMAgentIntType||caseRec.Interacting_About_Type__c==CaseEditConstants_HUM.HUMUnknownGroupIntType||caseRec.Interacting_About_Type__c==CaseEditConstants_HUM.HUMGroupIntType)
       {
           showPolicyMember = false;
           showNpiTaxId  = false;
           showNpiTaxIdUnknown = false;
           
       }
    
    }


    /*
    * This method is to make intent field required based on Classification
    * @param  NA
    * @return boolean
    */
    public boolean   getbIsIntentRequired()
    {
        if (showIntentReqOnLoad)
        {
            return true;
        }
        if (sClassification == null || sClassification == CaseEditConstants_HUM.None)
        {
            return  false;

        }
        else
        {
            return  true;
        }
    }

    /*
    * This method is called on load of case edit page
    * @param   NA
    * @return  void
    */
    public void onLoad()
    {
        try
        {
            showIntentReqOnLoad = false;                
            
            this.caseRec = [Select Id,Created_By_Queue__c,Verbal_Consent_Obtained__c,Department__c, Service_Center__c, Topic__c,Intent_Id__c, Classification_Id__c, Type, Account.Id, Account.Name, Account.RecordTypeId, RecordTypeId, CaseNumber, Classification_Type__c, Subtype__c, Complaint__c, RecordType.name,
                Group_Number_for_Policy__c, Case_Referred_To__c, Policy_Member__c, Complaint_Reason__c, G_A_Reason__c, Behavioral_Health__c, Language_Preference__c, G_A_rights_Given__c, priority, Follow_Up_Needed__c, Follow_up_Due_Date__c, Policy_Member__r.Member__r.RecordType.Name, Status, Call_Benefit_Category__c,
                Policy_Member__r.Policy__c, Interacting_About_Type__c, CTCI_List__c, CTCI_List__r.Classification__c, CTCI_List__r.Classification__r.name, CTCI_List__r.Classification__r.Is_Active__c,
                CTCI_List__r.Intent__c, CTCI_List__r.Intent__r.name, CTCI_List__r.Intent__r.Is_Active__c, CTCI_List__r.Classification_Type__c,Oral_Grievance_Category__c,Oral_Grievance_Sub_Category__c ,
                CTCI_List__r.Classification_Type__r.name, Interacting_With_Name__c, Origin, Product__c, Product_Type__c, Doc_Type__c, DCN_Formula__c, Work_Queue_View_Name__c, AutoRoute_Status__c,
                Policy_Member__r.Group_Number__c, Policy_Member__r.Policy_Platform__c,OpenEnrollment__c ,OpenEnrollmentType__c, Medicare_Track_ID__c, Policy_Member__r.Name, Case_Owner__c,Interacting_With_Type__c,Interacting_With__c,Interacting_With__r.Name,Guided_Process_Flag__c,Owner_Queue__c, Election_Type_Code__c, Pend_Key__c,
                                                OGO_Resolution_Date__c,OGO_Resolution_Type__c,Rx_Complaint_category__c,Rx_Complaint_date__c,Rx_Complaint_origin__c,Rx_Complaint_reason__c,IsClosed, CreatedBy.Profile.Name, DCN__c, Policy_Member__r.Product_type_code__c, Response_Status__c,Policy_Member__r.Segment_Indicator__c,Policy_Member__r.Issue_State__c,Policy_Member__r.Product_Type__c, OwnerId, Policy_Member__r.Member__r.PersonHomePhone, Escalation_Indicator__c,Escalation_Indicator_Date__c from Case Where Id = :caseRecord.Id];

            sEsclIndOnload = caseRec.Escalation_Indicator__c;
            oldAccId = caseRec.AccountId;      
            sPrevSubtype = caseRec.Subtype__c; 
            oldAccRecTypeName = (NULL != caseRec.Account.RecordTypeId) ? Schema.SObjectType.Account.getRecordTypeInfosById().get(caseRec.Account.RecordTypeId).getName() : NULL;
            //  start set the default status to Closed Or Cancelled when navigated from close case button
            strCaseStatus = String.isNotBlank(ApexPages.currentPage().getParameters().get(CaseEditConstants_HUM.CASEEDIT_STATUS_HUM)) ? ESAPI.encoder().SFDC_HTMLENCODE(ApexPages.currentPage().getParameters().get(CaseEditConstants_HUM.CASEEDIT_STATUS_HUM)) : NULL;
            if (strCaseStatus != null && (strCaseStatus == CaseEditConstants_HUM.CASEEDIT_CLOSED_HUM || strCaseStatus == CaseEditConstants_HUM.CASEEDIT_CANCELLED_HUM))
            {
                if (Schema.sObjectType.Case.fields.Status.isUpdateable()) caseRec.status = strCaseStatus;
            }
            //  End set the default status to closed when navigated from close case button
            sCaseNumber = caseRec.caseNumber;
            AccountNameMedicare = caseRec.Account.Name;
            AccountId = caseRec.AccountId;
            
            /* Changes for REQ 1824140 Credit Card Updates to a Case */
            String sComment = String.isNotBlank(ApexPages.currentPage().getParameters().get('caseComment')) ? ESAPI.encoder().SFDC_HTMLENCODE(ApexPages.currentPage().getParameters().get('caseComment')) : NULL;
            if(sComment !=null && sComment.contains('NEW_LINE')){
               List<String> parts = sComment.split('NEW_LINE');
                for(Integer i=0; i<parts.size();i++){
                    if(i==0){
                        sComment = parts[i];
                    }else{
                        sComment += '\n\n' + parts[i];
                    }
                }    
            }
            /* Changes for REQ 2882456 Case Documentation for Demographic Updates (CRMS) */
            else if(sComment !=null && sComment.contains('NEXT_LINE')){
                List<String> parts = sComment.split('NEXT_LINE');
                 for(Integer i=0; i<parts.size();i++){
                     if(i==0){
                         sComment = parts[i];
                     }else{
                         sComment += '\n' + parts[i];
                     }
                 }    
             }
            caseCom.CommentBody = sComment;            
            if(caseCom.CommentBody !=null && caseCom.CommentBody.contains('INT_WITH')){
                List<Interaction__c> listInteraction = [SELECT Id, Interacting_With__c,Interacting_With__r.Name, Interacting_With_type__c, Caller_Name__c FROM Interaction__c WHERE Id =:sinteractionId];
                
                if(!listInteraction.isEmpty()){             
                    Interaction__c objInteraction = listInteraction[0];
                    if(objInteraction != null && objInteraction.Interacting_With_type__c != null){              
                        caseCom.CommentBody = caseCom.CommentBody.replace('INT_WITH', objInteraction.Interacting_With_type__c);
                    }
                }
                else{
                    caseCom.CommentBody = caseCom.CommentBody.replace('INT_WITH contacted us to place order ', 'Placed order ');   
					caseCom.CommentBody = caseCom.CommentBody.replace('INT_WITH contacted us to update ', 'Updated ');
					caseCom.CommentBody = caseCom.CommentBody.replace('INT_WITH contacted us to add ', 'Added ');
                }
            }

            if(caseCom.CommentBody !=null && caseCom.CommentBody.length() > 1900){
                caseCom.CommentBody = caseCom.CommentBody.substring(0, 1900);
                bCharWarning = true;
            } 

            if(caseCom.CommentBody !=null) sMedCallsResCC = caseCom.CommentBody;

            if ((caseRec.CTCI_List__c != null) && caseRec.CTCI_List__r.Classification__c != null)
            {
                sClassification = caseRec.CTCI_List__r.Classification__c;
                if (caseRec.Classification_Type__c == caseRec.CTCI_List__r.Classification_Type__r.name)
                {
                    isClassificationMapped = true;
                }
                if (caseRec.CTCI_List__r.Intent__c != null)
                {
                    sIntent = caserec.CTCI_List__r.Intent__c;
                    showIntentReqOnLoad = true;
                }
            }
            else
            {
                isClassificationMapped = true;
            }
            //REQ - 241737: Showing Reminder message only When Policy Is Not available Or only RSO Medicare policy Is being transferred
            bIsRSOMedicarePolicy = string.IsBlank(caseRec.Policy_Member__c) || caseRec.Policy_Member__r.Member__r.RecordType.Name == CaseEditConstants_HUM.CASEEDIT_UNKNOWN_MEMBER_HUM || HUMGetBusinessDescription.getPMBusinessDescription(caseRec.Policy_Member__c) ? true : false;

            //REQ - 303466, REQ - 301582 To show/hide Medicare Part C/Part D when the policy Is Medicare/Non Medicare
            caseRecordTypeName = Schema.SObjectType.Case.getRecordTypeInfosById().get(CaseRec.RecordTypeId).getName();
            If(caseRecordTypeName == CaseEditConstants_HUM.MEDICARE_CASE) {
                bIsMedicareCase = true;
            }
            else {
                bIsMedicareCase = false;
            }
            isCallBenefitCategoryView = false;
            sCallBenefitCategory = caseRec.Call_Benefit_Category__c;
            checkLaunchEMME = false;
            sverbalConsentValue =caseRec.Verbal_Consent_Obtained__c;
            metCount = 0;
            metEntry = new List<METTaskList_DTO_HUM.METTask_DTO_HUM>();

            checkMedicareCustomerServiceAccess = false;
            validateMedicareCustomerServiceAccess();
            if (checkMedicareCustomerServiceAccess)
            {
                if (caseRec.Policy_Member__r.Policy__c != null)
                {
                    this.policy = [SELECT Major_Lob__c, Policy__c.Product__c, Policy__c.Product__r.Product_Type__c, Policy__c.Product__r.Segment_Type__c, Policy__c.Product__r.ProductCode from Policy__c WHERE id = :caseRec.Policy_Member__r.Policy__c LIMIT 1];

                    //List<String> majorLOB = CaseEditConstants_HUM.MEDICARE_LOB_LIST();
                    Map<String, Set<String>> medicareLOBProductType = CaseEditConstants_HUM.MEDICARE_LOB_PRODUCTTYPE_LIST();
                    if (policy.Product__r.ProductCode != null && policy.Product__r.ProductCode != '' && policy.Product__r.ProductCode == CaseEditConstants_HUM.MEDICARE_POLICY && medicareLOBProductType.containsKey(policy.Major_Lob__c))
                    {
                        Set<String> TempProductType;
                        TempProductType = medicareLOBProductType.get(policy.Major_Lob__c);
                        if (policy.Product__r.Product_Type__c != null && policy.Product__r.Product_Type__c != '' && TempProductType.contains(policy.Product__r.Product_Type__c))
                        {
                            isCallBenefitCategoryView = true;
                        }
                        else if (policy.Product__r.Product_Type__c == null || policy.Product__r.Product_Type__c == '')
                        {
                            isCallBenefitCategoryView = checkProductSegmentType();
                        }
                    }




                }
                else if ((string.IsBlank(caseRec.Policy_Member__c) || caseRec.Policy_Member__c == null) && (caseRec.Interacting_About_Type__c == CaseEditConstants_HUM.Member))
                {
                    isCallBenefitCategoryView = true;
                }
            }
            else
            {
                isCallBenefitCategoryView = false;
            }
            sMedicareID = '';           
            if (caseRec.Policy_Member__c != null)
            {
                    List<Member_ID__c> listMemberIds = [select id,Name from Member_ID__c where Policy_Member__c =:caseRec.Policy_Member__c and Type__c =: CaseEditConstants_HUM.HUMMedicare limit 1];

                    if(!listMemberIds.isempty())
                    {
                        sMedicareID = listMemberIds[0].Name;
                    }
            }
            
            if (Test.isRunningTest()) throw new HUMCustomException('Error');
            bProviderSwitch = ([select id,active__c from Provider_functionality_switch__c where active__c=true limit 1]).size()>0?true:false;
        }
        catch (Exception ex)
        {
            HUMExceptionHelper.bRestrictLog = true;
            listErrorLogs = HUMExceptionHelper.logErrors(ex, 'CaseEdit_CE_HUM', 'onLoad');
        }
    }

    /*
    * This method to check if this is a Medcare call case - REQ - 300142
    * @param    NA
    * @return   void
    */
    public void checkCCMedicareCalls()
    {
        if (caseRecordTypeName != CaseEditConstants_HUM.MEDICARE_CASE && caseRecordTypeName != CaseEditConstants_HUM.CLOSED_MEDICARE_CASE)
        {
            if (caseRec.Policy_Member__c != null && checkMedicareCustomerServiceAccess)
            {
                bMedicareCalls = oMedCalls.checkMedicareCare(caseRec.Origin, policy.Product__r.ProductCode, policy.Major_Lob__c, policy.Product__r.Product_Type__c, policy.Product__r.Segment_Type__c);
                list<PermissionSetAssignment> lstPMAssign = [SELECT PermissionSetId FROM PermissionSetAssignment WHERE AssigneeId = :UserInfo.getUserId() AND PermissionSet.Name = : CaseEditConstants_HUM.MEDICARECALLS_PERMISSIONSET_HUM];
                if (lstPMAssign != null && !lstPMAssign.isEmpty()) bCheckMedicareCCPermission = true;
                if (bMedicareCalls == true && bCheckMedicareCCPermission == true) getCCMedicareCalls();
            }
        }
        else if(caseRecordTypeName == CaseEditConstants_HUM.MEDICARE_CASE){
            if (caseRec.Policy_Member__c != null && checkMedicareCustomerServiceAccess)
            {
                this.bMedicareCallsForMedicareCase = oMedCalls.checkMedicareCare(caseRec.Origin, policy.Product__r.ProductCode, policy.Major_Lob__c, policy.Product__r.Product_Type__c, policy.Product__r.Segment_Type__c);
            }
        }
            
    }

    /*
    * This method to get case comments in case of Medicare Calls
    * @param    NA
    * @return   void
    */
    public void getCCMedicareCalls()
    {
        bMediCCIssUpdate = false;
        bMediCCResUpdate = false;
        map<Id, CaseComment> mapMediCC = new map<Id, CaseComment>([SELECT Id, CommentBody FROM CaseComment WHERE ParentId = :caseRec.Id]);
        list<CaseCommentType_Junction__c> lstCCTJunc = [SELECT Id, Name, CaseCommentType__r.Description__c, CaseCommentType__r.Name, CaseComment__c FROM CaseCommentType_Junction__c WHERE CaseComment__c IN : mapMediCC.keyset() order by createdDate ASC];
        if (lstCCTJunc != null && !lstCCTJunc.isEmpty())
        {
            for (CaseCommentType_Junction__c oCCTJ : lstCCTJunc)
            {
                if (oCCTJ.CaseCommentType__r.Name == CaseEditConstants_HUM.MEDICARECALLS_ISSUE_HUM && mapMediCC.containsKey(Id.valueOf(oCCTJ.CaseComment__c)))
                {
                    sMedicareIssInit = mapMediCC.get(Id.valueOf(oCCTJ.CaseComment__c)).CommentBody;
                }
                if (oCCTJ.CaseCommentType__r.Name == CaseEditConstants_HUM.MEDICARECALLS_RESO_HUM && mapMediCC.containsKey(Id.valueOf(oCCTJ.CaseComment__c)))
                {
                    sMedicareResInit = mapMediCC.get(Id.valueOf(oCCTJ.CaseComment__c)).CommentBody;
                }
            }
            if (String.isNotBlank(sMedicareIssInit) && String.isNotBlank(sMedicareResInit)) bMediCCUpdate = true;
            else bMediCCUpdate = false;
        }
        else bMediCCUpdate = false;
    }

    /*
    * This method to get case comments in case of Medicare Calls
    * @param    NA
    * @return   void
    */
    public boolean checkMandatoryforClosedCase()
    {
        boolean isClosedRequired = false;

        if (caseRec.status == CaseEditConstants_HUM.CASEEDIT_CLOSED_HUM)
        {
            if ((String.isBlank(caseRecord.G_A_Rights_Given__c) && String.isBlank(caseRecord.Complaint__c)) || String.isBlank(caseRecord.G_A_Rights_Given__c) || String.isBlank(caseRecord.Complaint__c))
            {
                isClosedRequired = true;
            }
            if (String.isNotBlank(caseRecord.G_A_Rights_Given__c) && caseRecord.G_A_Rights_Given__c.equalsIgnoreCase('yes'))
            {
                if (String.isBlank(caseRecord.G_A_Reason__c))isClosedRequired = true;
            }
            if (String.isNotBlank(caseRecord.Complaint__c) && (!caseRecord.Complaint__c.equalsIgnoreCase('No')))
            {
                if (String.isBlank(caseRecord.Complaint_Reason__c) || String.isBlank(caseRecord.Complaint_Type__c) || (String.isBlank(caseRecord.Complaint_Reason__c) && String.isBlank(caseRecord.Complaint_Type__c)))
                {
                    isClosedRequired = true;
                }
            }
        }
        return isClosedRequired;
    }

     /*
    * This method to get the classifications based on the classification type
    * @param    NA
    * @return   SelectOptions
    */
    public List<SelectOption> getClassificationValues()
    {
        set<Id> classificationIds = getClassificationIds();
        selIntentID = sIntent;
        string ClassificationId = caseRec.CTCI_List__r.Classification__c;
        if ((null != caseRec.CTCI_List__c) &&  String.isnotblank(ClassificationId)  && String.isnotblank(sClassification) && (sClassification.length() > 18 || sClassification == CaseEditConstants_HUM.None || ClassificationId != sClassification)) // to avoid code break If we edit exsiting saved case and sclassification value is changed from None 
        {
            isClassificationChnaged = true;
            sIntent = null;
            isClassificationMapped = true;
       }
        showIntentReqOnLoad = false;
        List<SelectOption> lstClassificationOptions = new List<SelectOption>();
        MapClassification = new Map<Id, Classification__c>();
        if ((null != caseRec.CTCI_List__c) && String.isnotblank(ClassificationId) && (!caseRec.CTCI_List__r.Classification__r.Is_Active__c || !isClassificationMapped))
        {
            bIsActiveClassification = false;
            lstClassificationOptions.add(new SelectOption(caseRec.CTCI_List__r.Classification__c, caseRec.CTCI_List__r.Classification__r.name));
        }
        lstClassificationOptions.add(new SelectOption(CaseEditConstants_HUM.None, CaseEditConstants_HUM.CASEEDIT_NONE_DISP_HUM));
        for (Classification__c classification : [Select id, Name from Classification__c where
            Id IN : classificationIds order by name])
        {
            if (sClassification == null && (null != caseRec.CTCI_List__c) && caseRec.CTCI_List__r.Classification__c == classification.ID)
            {
                sClassification = classification.id;
                bIsActiveClassification = true;
            }
            if(!bIsActiveClassification)
                lstClassificationOptions.add(new SelectOption(classification.Id+CaseEditConstants_HUM.CASEEDIT_CLASSIFICATION_SEPERATOR, classification.Name));
            else
                lstClassificationOptions.add(new SelectOption(classification.Id, classification.Name));

            MapClassification.put(classification.Id, classification);
        }
        if (sClassification == null)
        {
            sClassification = CaseEditConstants_HUM.None;
            bIntentCheck = true;
        }
        if((ClassificationId == null && String.isnotblank(sClassification) ) || ClassificationId != sClassification  ) // to set Intent to Null if Classification is changed and to  avoid code break when we there is change Classification type
        {
                    sIntent = null;
        } 
        return lstClassificationOptions;
    }

    /*
    * This method gets the Intent values based on the classification
    * @param  NA
    * @return SelectOptions
    */
    public List<SelectOption> getIntentValues()
    {
        String intentSuffix = '';
        isClassificationChnaged = false;
        string ClassificationId = caseRec.CTCI_List__r.Classification__c;
        if ((null != caseRec.CTCI_List__c) &&  String.isnotblank(ClassificationId)  && String.isnotblank(sClassification) && (sClassification.length() > 18 || sClassification == CaseEditConstants_HUM.None || ClassificationId != sClassification))
        {
            isClassificationChnaged = true;
            sIntent = null;
            isClassificationMapped = true;
        }

        List<SelectOption> lstIntentOptions = new List<SelectOption>();
        MapIntent = new Map<Id, Intent__c>();
        bIntentCheck = true;
        if (!isClassificationChnaged && sIntent != null && sIntent != CaseEditConstants_HUM.None && (null != caseRec.CTCI_List__c) && caseRec.CTCI_List__r.Classification__c != null &&  caseRec.CTCI_List__r.Intent__c != null && (!caseRec.CTCI_List__r.Intent__r.Is_Active__c || !caseRec.CTCI_List__r.Classification__r.Is_Active__c || !isClassificationMapped) && sClassification != CaseEditConstants_HUM.None &&  sClassification == caseRec.CTCI_List__r.Classification__c)
        {
            lstIntentOptions.add(new SelectOption(caseRec.CTCI_List__r.Intent__c, caseRec.CTCI_List__r.Intent__r.name));
            bIntentCheck = false;
            bIsActiveIntent = false;
            intentSuffix = CaseEditConstants_HUM.CASEEDIT_INTENT_SEPERATOR;
        }

        lstIntentOptions.add(new SelectOption(CaseEditConstants_HUM.None, CaseEditConstants_HUM.CASEEDIT_NONE_DISP_HUM));
        if (!String.isBlank(sClassification) && sClassification != CaseEditConstants_HUM.None && isClassificationMapped)
        {
            Set<Id> intentIds = getIntentIds(sClassification.removeEnd(CaseEditConstants_HUM.CASEEDIT_CLASSIFICATION_SEPERATOR));
            for (Intent__c intent : [Select id, name from Intent__c where id IN : intentIds order by name])
            {
                if (sClassification != null && sClassification.length() == 18 && sIntent == null && (null != caseRec.CTCI_List__c) && caserec.CTCI_List__r.Intent__c == intent.ID && !isClassificationChnaged)
                {
                    sIntent = intent.id+intentSuffix;
                    bIsActiveIntent = true;
                }
                lstIntentOptions.add(new SelectOption(intent.Id+intentSuffix, intent.Name));
                MapIntent.put(intent.Id, intent);
                bIntentCheck = false;
            }
        }
        if (sClassification == CaseEditConstants_HUM.None)
        {
            sIntent = CaseEditConstants_HUM.None;
        }
        // invokes function to set Log Code
        if(sIntent != null){
            setLogCode();
        }

        return lstIntentOptions;
    }


    /*
    * This is a generic Method to save the case record
    * @param NA
    * @return  void
    */
    public void callSave()
    {
        sClassification = sClassification.removeEnd(CaseEditConstants_HUM.CASEEDIT_CLASSIFICATION_SEPERATOR);
        sIntent = sIntent.removeEnd(CaseEditConstants_HUM.CASEEDIT_INTENT_SEPERATOR);
        if (isClassificationMapped == true || caseRec.CTCI_List__c == null)
        {
            List<CTCI_Junction__c> lstCtci = [select id, Classification_Type__c, Classification__c, Intent__c from CTCI_Junction__c where Classification_Type__r.name = :caseRec.Classification_Type__c AND Classification__c = : sClassification AND Intent__c = : sIntent and IsActive__c = true];
            if (lstCtci != null && !lstCtci.isempty())
            {
              if(schema.SobjectType.Case.fields.CTCI_List__c.isUpdateable())
                caserecord.CTCI_List__c = lstCtci[0].id;
            }
        }
        
        checkPartDAndExpeditedProcess(caseRec);
        
       if(schema.SobjectType.case.fields.Follow_up_Due_Date__c.isUpdateable())
        caserecord.Follow_up_Due_Date__c = caserec.Follow_up_Due_Date__c;
      if(schema.SobjectType.case.fields.Election_Type_Code__c.isUpdateable()) 
        caserecord.Election_Type_Code__c = caserec.Election_Type_Code__c;
      if(schema.SobjectType.case.fields.priority.isUpdateable())
        caserecord.priority = caserec.priority;
       if(schema.SobjectType.case.fields.Call_Benefit_Category__c.isUpdateable()) 
        caserecord.Call_Benefit_Category__c = sCallBenefitCategory;
       if(schema.SobjectType.case.fields.SubType__c.isUpdateable()) 
        caserecord.SubType__c = CaseRec.Subtype__c;
      if(schema.SobjectType.case.fields.type.isUpdateable())
        caserecord.type = CaseRec.type;
      if(schema.SobjectType.case.fields.RecordTypeId.isUpdateable()) 
        caserecord.RecordTypeId = CaseRec.RecordTypeId;
      if(schema.SobjectType.case.fields.DCN__c.isUpdateable()) 
        caserecord.DCN__c= CaseRec.DCN__c;
      if(schema.SobjectType.case.fields.policy_member__c.isUpdateable()) 
        caserecord.policy_member__c = caseRec.Policy_Member__c;
       if(schema.SobjectType.case.fields.AccountId.isUpdateable()) 
        caserecord.AccountId = caseRec.AccountId;
       if(schema.SobjectType.case.fields.Interacting_About_Type__c.isUpdateable()) 
        caserecord.Interacting_About_Type__c = caseRec.Interacting_About_Type__c;
      if(schema.SobjectType.case.fields.Interacting_With_Type__c.isUpdateable()) 
        caserecord.Interacting_With_Type__c = caseRec.Interacting_With_Type__c;
     if(schema.SobjectType.case.fields.Interacting_With__c.isUpdateable()) 
        caserecord.Interacting_With__c = caseRec.Interacting_With__c;
     if(schema.SobjectType.case.fields.Escalation_Indicator__c.isUpdateable() && bShowEsclIndSwitch) 
            caserecord.Escalation_Indicator__c = caseRec.Escalation_Indicator__c;
     if(schema.SobjectType.case.fields.Escalation_Indicator_Date__c.isUpdateable()&& bShowEsclIndSwitch)
     {
        if(caseRec.Escalation_Indicator__c != null && caseRec.Escalation_Indicator__c != sEsclIndOnload){
            String escIndDate = !string.isBlank(caseRec.Escalation_Indicator_Date__c) ? caseRec.Escalation_Indicator_Date__c : '' ;
            caserecord.Escalation_Indicator_Date__c = escIndDate +'\n'+system.now().format('MM/dd/yyyy hh:mm:ss a') + ' - '+caseRec.Escalation_Indicator__c;
        }
     }
      if(schema.SobjectType.case.fields.OGO_Resolution_Type__c.isUpdateable()) 
        caserecord.OGO_Resolution_Type__c = caseRec.OGO_Resolution_Type__c;
        if(!String.IsBlank(caseRec.OGO_Resolution_Type__c))
        {
           if(schema.SobjectType.case.fields.OGO_Resolution_Date__c.isUpdateable())
            caserecord.OGO_Resolution_Date__c = caseRec.OGO_Resolution_Date__c;
        }
        else
        {
            if (Schema.sObjectType.Case.fields.OGO_Resolution_Date__c.isUpdateable()) caserecord.OGO_Resolution_Date__c = null;
        }
        if(schema.SobjectType.case.fields.Verbal_Consent_Obtained__c.isUpdateable()) 
            caserecord.Verbal_Consent_Obtained__c =sverbalConsentValue;
        
        /* Defect 400990  Compare OldAccount id with new one and when Interacting about type is Group make Policy null*/
        if(caseRec.AccountId!=oldAccId && caserec.Interacting_About_Type__c ==CaseEditConstants_HUM.HUMGroupIntType) { 
       if(schema.SobjectType.Case.fields.Policy__c.isUpdateable())
        caserecord.Policy__c = null; 
        } 
         if(schema.SobjectType.Case.fields.Topic__C.isUpdateable())

        caserecord.Topic__C = caseRec.Topic__C;
    
       if(schema.SobjectType.Case.fields.Case_Referred_To__c.isUpdateable())
        caserecord.Case_Referred_To__c = caseRec.Case_Referred_To__c;
      if(schema.SobjectType.Case.fields.OpenEnrollment__c.isUpdateable()) 
        caserecord.OpenEnrollment__c = caseRec.OpenEnrollment__c;
        
       if(schema.SobjectType.Case.fields.OpenEnrollmentType__c.isUpdateable())
        caserecord.OpenEnrollmentType__c = caseRec.OpenEnrollmentType__c;

        CaseComment caseCommentRec = new CaseComment();
        if(bMedicareCalls == true && bCheckMedicareCCPermission == true && !bRSOHPFlag && !bIsPharmacyUser)
            casecom.CommentBody = '';
        if (casecom.CommentBody<>NULL && casecom.CommentBody<> '')
        {
           if(schema.SobjectType.CaseComment.fields.ParentId.isCreateable())
            caseCommentRec.ParentId = caserecord.Id;
           if(schema.SobjectType.CaseComment.fields.CommentBody.isCreateable())
            caseCommentRec.CommentBody = (bIsPharmacyUser && bIsLogCodelistAvailable) ? '(' + sCodeHidden + ') ' + casecom.commentBody : casecom.commentBody;
            lstCaseComment = new List<CaseComment>{ caseCommentRec };
        }
        //Add logic to change case owner with current agent if request is coming from web chat
        if((caserec.Origin == CaseEditConstants_HUM.HUMWebChatText && caserec.Case_Owner__c == CaseEditConstants_HUM.WEBCHAT_DEFAULT_LIVEAGENT_NAME) || bCaseOwnerChangeRequired ){
            List<User> currentUser = [Select id, name, Current_Queue__c from User where ID=:userinfo.getuserID() ];
            if(!currentUser.isEmpty()){
               if(schema.SobjectType.case.fields.OwnerId.isUpdateable())
                caserecord.OwnerId = currentUser[0].id;
               if(schema.SobjectType.case.fields.Case_Owner__c.isUpdateable())
                caserecord.Case_Owner__c = currentUser[0].name;
               if(schema.SobjectType.case.fields.Owner_Queue__c.isUpdateable())
                caserecord.Owner_Queue__c = currentUser[0].Current_Queue__c;
            }
        }
        if(bOGOAccept){
            if(schema.SobjectType.Case.fields.Topic__C.isUpdateable())
            caserecord.Topic__C = CaseEditConstants_HUM.TOPIC_ORAL_GRIEVANCE;
            if(schema.SobjectType.case.fields.Case_Owner__c.isUpdateable())
            caserecord.Case_Owner__c = caserecord.Owner_Queue__c;
            Group oGroup = [select Id from Group where  Type = 'Queue' AND NAME =: caserecord.Owner_Queue__c Limit 1];
            if(oGroup != Null && Schema.sObjectType.Case.fields.OwnerId.isUpdateable()) caserecord.OwnerId = oGroup.id;
        }
        /* update auto route status to route case to Invoice Request */ 
        if(bInvoiceRequestSetup != null && bInvoiceRequestSetup == true){
            if(schema.SobjectType.case.fields.Autoroute_Status__c.isUpdateable())
             caserecord.Autoroute_Status__c = 'Invoice Requests';
        }
        /* update auto route status to route case to Humana Pharmacy Web Issues */ 
        if(sWebIssuesTicketReq == 'true'){
            if(Schema.sObjectType.Case.fields.Autoroute_Status__c.isUpdateable()) caserecord.Autoroute_Status__c = 'Humana Pharmacy Web Issues';
        }
        if(schema.SobjectType.case.fields.Response_Status__c.isUpdateable())
        if (Schema.sObjectType.Case.fields.Response_Status__c.isUpdateable()) caserecord.Response_Status__c = caseRec.Response_Status__c;
        if(String.isNotBlank(caserecord.status) && CaseEditConstants_HUM.CASEEDIT_CLOSED_HUM.equalsIgnoreCase(caserecord.status)) {
            if(Schema.sObjectType.Case.fields.Response_Status__c.isUpdateable()) caserecord.Response_Status__c = '';
        }
        //US3576296
        if(HUMUtilityHelper.isCRMFunctionalityON('3576296') && caserecord.status == 'Closed' && (caserecord.Complaint__c == 'Yes - Medicaid' 
            || caserecord.Complaint__c == 'Yes - Medicare Part C' || caserecord.Complaint__c == 'Yes - Medicare Part D')){
               if(schema.SobjectType.case.fields.status.isUpdateable()){
                   caserecord.status = 'External Transfer in Progress';
               }
               if (Schema.sObjectType.Case.fields.Case_Complaint_Routed__c.isUpdateable()) caserecord.Case_Complaint_Routed__c = True;
        }
        //US3576296
        update caserecord;
    }
    /*
    * This method checks for QAA process attached to the case record
    * @param   caserecord id
    * @return  true Or false
    */
    public boolean checkProcess(id CaseId){
        Boolean bSuccess = false;
        try{
            List<Case_Action_Association__c> lstCaseActionAssociation = [SELECT Id, Name, Case__r.CaseNUmber, Case_Action__r.name, Action__c FROM Case_Action_Association__c where Case_Action__r.Name = 'QAA' and Case__c = :CaseId];
            bSuccess =  (!lstCaseActionAssociation.IsEmpty())? true : false ;  
        }catch(exception e){
            System.debug('Error while checking Template process');
            System.debug(e.getMessage());
        }
        return bSuccess;
    }
    
    private void enableDisableSaveAndTransferButton()
    {
        if(String.IsNotBlank(attachedTemplateName))
        {
            if(attachedTemplateName == CaseEditConstants_HUM.ATTESTATION_TEMPLATE_NAME)
            {
                if(caseRec.Case_Owner__c  == CaseEditConstants_HUM.CASE_RSO_ENROLLMENT_OWNER 
                && caseRec.Guided_Process_Flag__c == true && caseRec.status == CaseEditConstants_HUM.CASE_STATUS_PENDING_RESPONSE)
                {
                    if(currentloggedInUser.Current_Queue__c != caseRec.Owner_Queue__c)
                    {
                        bTrsansferBtnDisabled = true;
                    }
                }
            }
            
            else if(attachedTemplateName == '95C90830-3133' || attachedTemplateName == 'BC929CD6-E06A' 
                || attachedTemplateName == CaseEditConstants_HUM.PCPTEMPLATEUNIQUEID || attachedTemplateName == CaseEditConstants_HUM.GOODCAUSEUNIQUEID
                    || attachedTemplateName == CaseAutoRouteConstant_C_HUM.MEDICAID_PAR_PROVIDER_UNIQUEID)
            {
                if(sAutoRouteStatus == 'Evaluate Expedited Appeal' || sAutoRouteStatus == 'Evaluate Part D' || sAutoRouteStatus == CaseEditConstants_HUM.AUTOROUTING_PCP 
                    || sAutoRouteStatus == CaseEditConstants_HUM.AUTOROUTING_GOODCAUSE || sAutoRouteStatus == CaseEditConstants_HUM.AUTOROUTING_GOODCAUSETIE
                        || sAutoRouteStatus == CaseAutoRouteConstant_C_HUM.AUTOROUTING_MEDICAID_PAR_PROVIDER || sAutoRouteStatus == CaseAutoRouteConstant_C_HUM.AUTOROUTING_MEDICAID_PAR_PROVIDER_KY || sAutoRouteStatus == CaseAutoRouteConstant_C_HUM.AUTOROUTING_MEDICAID_PAR_PROVIDER_IL)
                {
                    bTrsansferBtnDisabled = true;
                }
                else if(currentloggedInUser.Current_Queue__c != caseRec.Owner_Queue__c)
                {
                    bTrsansferBtnDisabled = true;
                }
            }
        }
    }
    /*
    * This method checks if part D, expedited appeal or PCP/PCD processes attached to given case or not
    * @param   Case
    * @return  NA
    */
   @testVisible private void checkPartDAndExpeditedProcess(Case caseRec)
    {
       bPCPPCDProcessAttached = false;
	bEnrollmentWelcomeCallProcessAttached = false;
       bGoodCauseProcessAttached = false;
        attachedTemplateName = '';
        if(caseRec.id != null)
        {
            List<Template_Submission__c> lstTemplatesData = [SELECT Id, Template__r.Unique_Template_Name__c,(SELECT Id, Template_Field__r.Name, Value__c FROM Template_Submission_Data__r) FROM Template_Submission__c where Submission_Owner__r.Object_Owner_ID__c = :caseRec.Id];
            if(lstTemplatesData!=null && !lstTemplatesData.isEmpty()){
                if(lstTemplatesData[0].Template__r.Unique_Template_Name__c=='d884d160-77c0-4d1b-95aa-edf8c81f3a59'){
                    bPCPPCDProcessAttached = UtilityHelper_H_AHT_HUM.isTemplateSubmittedHasValue(lstTemplatesData[0].Template_Submission_Data__r);
                }
                if(lstTemplatesData[0].Template__r.Unique_Template_Name__c==CaseEditConstants_HUM.GOODCAUSEUNIQUEID){
                    bGoodCauseProcessAttached = UtilityHelper_H_AHT_HUM.isTemplateSubmittedHasValue(lstTemplatesData[0].Template_Submission_Data__r);
                }
	          if(lstTemplatesData[0].Template__r.Unique_Template_Name__c==CaseEditConstants_HUM.Enrollment_Onboard_Welcome_Call){
                    bEnrollmentWelcomeCallProcessAttached = UtilityHelper_H_AHT_HUM.isTemplateSubmittedHasValue(lstTemplatesData[0].Template_Submission_Data__r);
                }
              }
            isMedProcessAttachedValue  = false;
            isPartProcessAttachedValue = false;
            isGoodCauseMedProcessAttachedValue  = false;
            isPCPProcessAttachedValue  = false;
            bIsMedicaidPARProcessAttached = false;
            for(Template_Submission__c  objTempSubmission : lstTemplatesData)
            {
                switch on objTempSubmission.Template__r.Unique_Template_Name__c
                {
                    when '95C90830-3133' {
                        isMedProcessAttachedValue  = true;
                        attachedTemplateName = '95C90830-3133';
                    }
                    when 'BC929CD6-E06A' {
                        isPartProcessAttachedValue = true;
                        attachedTemplateName = 'BC929CD6-E06A';
                    }
                    when 'E0A13562-89EE-48B8-8EBA-E485C6583FF1' {
                        isGoodCauseMedProcessAttachedValue  = true;
                        attachedTemplateName = CaseEditConstants_HUM.GOODCAUSEUNIQUEID;
                    }
                    when'd884d160-77c0-4d1b-95aa-edf8c81f3a59' {
                        isPCPProcessAttachedValue  = true;
                        attachedTemplateName = CaseEditConstants_HUM.PCPTEMPLATEUNIQUEID;
                    }
                    when 'ATVF007' {
                        attachedTemplateName= 'ATVF007';
                    }
                    when 'deeef952-9f63-49ce-97d0-c7eb3204b519' {
                        bIsMedicaidPARProcessAttached = true;
                        attachedTemplateName= CaseAutoRouteConstant_C_HUM.MEDICAID_PAR_PROVIDER_UNIQUEID;
                    }
                }
            }
        }
        else
        {
            isMedProcessAttachedValue  = null;
            isPartProcessAttachedValue = null;
            isGoodCauseMedProcessAttachedValue = false;
            isPCPProcessAttachedValue = false;
            bIsMedicaidPARProcessAttached = false;
        }
    }

    /*
    * This method validates the DCN then call the save method
    * @param   NA
    * @return  PageReference
    */
    public Pagereference validateDCNBeforeSave()
    {
        if(bDCNSwitch)
        {
            if(isDCNDisplay && !string.isblank(caserec.DCN__c) && caserec.Type == CaseEditConstants_HUM.CorrespondenceInq)
        	{    
                if(!caserec.DCN__c.isAlphaNumeric()){
                    isDCNAlphanumeric = false;
                }else{
                    isDCNAlphanumeric = true;
                    DCNCalloutResponse = HUMUtilityHelper.ehubValidationDCNService(caserec.DCN__c);
                }
        	}
        }        
        return save();
    }

    /*
    * This method saves the case record
    * @param   NA
    * @return  PageReference
    */
    public Pagereference save()
    {
        this.bHasOpenTask = false;
        boolean executeNextStep = true;
        SavePoint dataSavePoint = Database.setSavepoint(); 
        returnURL = '';
        Pagereference pgref = Null;
        try
        {
            readyForePostCommit = false;
            boolean bCheckMediCC = true;
            
            if (!Test.isRunningTest())
            {
                if(sAutoRouteStatus != '' && Schema.sObjectType.Case.fields.Autoroute_Status__c.isUpdateable()) caserecord.Autoroute_Status__c = sAutoRouteStatus;
                if (schema.SobjectType.case.fields.Autoroute_Status__c.isUpdateable() && caserecord.Autoroute_Status__c != CaseEditConstants_HUM.AUTOROUTING_COMPLETED && caserecord.Autoroute_Status__c != CaseEditConstants_HUM.AUTOROUTING_PARTD && 
                    caserecord.Autoroute_Status__c != CaseEditConstants_HUM.AUTOROUTING_ExpeditedAppeal && caserecord.AutoRoute_Status__c != 'Routed Pending Accept' && 
                    caserecord.Autoroute_Status__c != CaseEditConstants_HUM.AUTOROUTING_PCP && caserecord.Autoroute_Status__c != CaseEditConstants_HUM.AUTOROUTING_GOODCAUSE && 
                    caserecord.Autoroute_Status__c != CaseEditConstants_HUM.AUTOROUTING_GOODCAUSETIE && !isCreditableCoverageSubtypeElligible() && 
                    caserecord.Autoroute_Status__c != CaseAutoRouteConstant_C_HUM.AUTOROUTING_MEDICARE && caserecord.Autoroute_Status__c != CaseAutoRouteConstant_C_HUM.AUTOROUTING_KY_IL_MCD && 
                    caserecord.Autoroute_Status__c != CaseAutoRouteConstant_C_HUM.AUTOROUTING_FL_MCD &&  caserecord.Autoroute_Status__c != CaseAutoRouteConstant_C_HUM.AUTOROUTING_MEDICAID_PAR_PROVIDER && caserecord.Autoroute_Status__c != CaseAutoRouteConstant_C_HUM.AUTOROUTING_MEDICAID_PAR_PROVIDER_KY && caserecord.Autoroute_Status__c != CaseAutoRouteConstant_C_HUM.AUTOROUTING_MEDICAID_PAR_PROVIDER_IL && 
                    caserecord.Autoroute_Status__c != CaseAutoRouteConstant_C_HUM.OI_AUTOROUTING_MEDICALUPDATE && caserecord.Autoroute_Status__c !=  CaseAutoRouteConstant_C_HUM.OI_AUTOROUTING_RXMCDONLY && caserecord.Autoroute_Status__c != CaseAutoRouteConstant_C_HUM.OI_AUTOROUTING_RXMESPDP  && caserecord.Autoroute_Status__c != CaseAutoRouteConstant_C_HUM.OI_AUTOROUTING_RX090DENIAL && caserecord.Autoroute_Status__c != CaseAutoRouteConstant_C_HUM.OI_AUTOROUTING_OIPRESCRIPTION && caserecord.Autoroute_Status__c != CaseAutoRouteConstant_C_HUM.OI_AUTOROUTING_NEWBORN)
                    if (Schema.sObjectType.Case.fields.Autoroute_Status__c.isUpdateable()) caserecord.Autoroute_Status__c = CaseEditConstants_HUM.AUTOROUTING_EVALUATE;
            }
           if(schema.SobjectType.case.fields.status.isUpdateable())
            caserecord.status = caseRec.status;
            if (caserecord.status == CaseEditConstants_HUM.CASEEDIT_CLOSED_HUM || caserecord.status == CaseEditConstants_HUM.CASEEDIT_CANCELLED_HUM)
            {
                List<Task> lstOpenTasks = [Select id, Task_Number__c from Task where Whatid = :caserecord.id AND (Type = : CaseEditConstants_HUM.CASEEDIT_WORK_TASK_HUM or Type = :CaseEditConstants_HUM.HUMExtensionTask) and Status != : CaseEditConstants_HUM.CASEEDIT_CLOSED_HUM order by createddate asc];
                this.bHasOpenTask = !lstOpenTasks.isEmpty();
                executeNextStep = lstOpenTasks.isEmpty();
                sPharmacyTapMap = new Map<id,string>();
                for(Task oTask: lstOpenTasks){
                    sPharmacyTapMap.put(oTask.id,oTask.Task_Number__c);
                }
                bPharmacyOpenTasks = JSON.serialize(sPharmacyTapMap);
                If(bCaseTransferRedirectionRequired)
                {
                    ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.Error, Label.CaseTransferErrorMessage_HUM));
                    pgref = null;
                    returnURL = '';
                    executeNextStep = false; 
                }
            }

            if (executeNextStep && sClassification != null && sClassification != CaseEditConstants_HUM.None && sIntent != null && sIntent != CaseEditConstants_HUM.None && medicareComplaintCheck() && (!checkMandatoryforClosedCase()) && !bRSOHPFlag)
            {
                if (bMedicareCalls == true && bCheckMedicareCCPermission == true)
                {
                    if (bMediCCUpdate)
                    {
                        if (iCountMediIssueCCInit >= 10 && iCountMediIssueCCInit <= 2000 && iCountMediResCCInit >= 10 && iCountMediResCCInit <= 2000) bMediCCUpdate = true;
                        else bMediCCUpdate = false;
                    }
                    bCheckMediCC = oMedCalls.createUpdateCaseCommentRecord(sMedCallsIssueCC, sMedCallsResCC, caserecord.Id, iCountMediIssueCC, iCountMediResCC, caseRec.status, bMediCCUpdate, bMediCCIssUpdate, bMediCCResUpdate, false);
                }
            }
            if(!String.isBlank(sClassification))
                oclassifi = [SELECT Id, Name FROM Classification__c where id = :sClassification];
            if(!String.isBlank(sIntent))
                oIntent = [SELECT Id, Name FROM Intent__c where id = :sIntent];

            
            
            Boolean bIsQAANeeded = (oclassifi.size() > 0 && oclassifi[0].Name == 'Complaints or Compliments' && oIntent.size() > 0 && oIntent[0].Name == 'QAA' && lstComplaints.contains(caserecord.Complaint__c));
            
            
            if(bIsQAANeeded){                
                if(!checkProcess(caserecord.id)) {
                    ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.WARNING, Label.QAAPROCESS_WARNING_HUM));
                    pgref = null;
                    returnURL = '';
                    executeNextStep = false;   
                }
            }
            checkPartDAndExpeditedProcess(caseRec);
             if(oclassifi != null && !oclassifi.isEmpty()  && oclassifi[0].Name == CaseEditConstants_HUM.GOODCAUSECLASSIFICATION && oIntent != null && !oIntent.isEmpty() && oIntent[0].Name == CaseEditConstants_HUM.GOODCAUSEINTENT  && CaseEditConstants_HUM.GoodCauseAutoRouteClassificationTypes.contains(CaseRec.Classification_Type__c) && (( String.isNotBlank(attachedTemplateName) &&  !bGoodCauseProcessAttached) || String.isBlank(attachedTemplateName) )&& null != caseRec.Policy_Member__c && String.isNotBlank(caseRec.Policy_Member__r.Product_type_code__c) && CaseEditConstants_HUM.PRODUCT_TYPE_PDP == caseRec.Policy_Member__r.Product_type_code__c )
            {
                ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.ERROR, 'The Medicare Good Cause Reinstatement Process is required before saving the case.'));
                pgref = null;
                returnURL = '';
                executeNextStep = false;   
            }

            if(executeNextStep && !bPCPPCDProcessAttached && caseRec.status!=CaseEditConstants_HUM.CASEEDIT_CANCELLED_HUM && oclassifi!=null && oIntent!=null && !oclassifi.isEmpty() && !oIntent.isEmpty() && UtilityHelper_H_AHT_HUM.checkProcessPCPPCD(CaseRec,oclassifi[0].Name,oIntent[0].name)) {
                ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.ERROR, Label.PCP_PCD_Process_Warning_HUM));
                pgref = null;
                returnURL = '';
                executeNextStep = false;
            }
			Boolean hasCustomPermission = FeatureManagement.checkPermission(CaseEditConstants_HUM.Enroll_Onboard_PERMISSIONSET_HUM);			         
               if(hasCustomPermission){       
					if(executeNextStep  && !bEnrollmentWelcomeCallProcessAttached && caseRecord.RecordType.name == CaseEditConstants_HUM.MEMBERCASE_HUM && caseRec.Policy_Member__c != null && String.isNotBlank(caseRec.Policy_Member__c) &&  null != caseRec.Policy_Member__r.Segment_Indicator__c && caseRec.Policy_Member__r.Segment_Indicator__c == CaseEditConstants_HUM.Segment_Individual_Medicare && oclassifi != null && !oclassifi.isEmpty() && oclassifi[0].Name == CaseEditConstants_HUM.Case_Classification_Communication && oIntent != null && !oIntent.isEmpty() && oIntent[0].Name == CaseEditConstants_HUM.Case_intent_Welcome_Call) {                      
					  ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.ERROR, Label.Enrollment_Welcome_Call_Warning_Hum));
						pgref = null;
						returnURL = '';
						executeNextStep = false;
               }
            } 
			
        if(executeNextStep && bIsMedicaidPARProcessAttached && String.isNotBlank(caseRecord.Complaint__c) && caserecord.Complaint__c.equalsIgnoreCase(CaseEditConstants_HUM.MEDICAID)){
                ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.ERROR, Label.Complaint_Cant_be_marked_Medicaid_PAR_Provider));
                pgref = null;
                returnURL = '';
                executeNextStep = false;
            }
            if(executeNextStep && !bIsMedicaidPARProcessAttached && UtilityHelper_H_AHT_HUM.checkProcessMedicaidPARProviderNotAccepting(CaseRec,oclassifi,oIntent) ){
                ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.ERROR, Label.Medicaid_PAR_Provider_Not_Accepting_Template_Mandatory));
                pgref = null;
                returnURL = '';
                executeNextStep = false;
            }
        if(executeNextStep &&  lstComplaints.contains(caserecord.Complaint__c) && (CaseEditConstants_HUM.lstAutoRouteStatus.contains(sAutoRouteStatus) || bIsATVF007Routed)){
                ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.ERROR,Label.Complaint_must_be_selected_No));
                pgref = null;
                returnURL = '';
                executeNextStep = false;
            }
            if( executeNextStep && sAutoRouteStatus == CaseAutoRouteConstant_C_HUM.AUTOROUTING_FL_MCD  && (string.isBlank(caseRecord.G_A_Rights_Given__c) || string.isBlank(caseRecord.Complaint__c))){
               
                if (String.isBlank(caseRecord.G_A_Rights_Given__c) ) {
                    ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.ERROR,Label.HUMGandARightsError));
                }
                if (String.isBlank(caseRecord.Complaint__c)) {
                    ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.ERROR,Label.HUMComplaintError));
                }
                pgref = null;
                returnURL = '';
                executeNextStep = false;
            }
            
            if (executeNextStep && ((string.isNotBlank(casecom.CommentBody) && string.isBlank(sCode)) || (string.isNotBlank(sCode) && string.isBlank(casecom.CommentBody))) && bIsPharmacyUser && bIsLogCodelistAvailable)
            {
                ApexPages.Message myMsg;
                if (string.isBlank(sCode)) myMsg = new ApexPages.Message(ApexPages.Severity.ERROR, 'Humana Pharmacy Log Code:  You must enter a value');
                if (string.isBlank(casecom.CommentBody)) myMsg = new ApexPages.Message(ApexPages.Severity.ERROR, 'Case Comments:  You must enter a value');
                ApexPages.addMessage(myMsg);
                pgref = null;
                returnURL = '';
                executeNextStep = false;
            }

            boolean isCreateAttachmentForDCN = false;
             list<Case> lstCase = new list<Case>();
            if(executeNextStep && isDCNDisplay && !string.isblank(caserec.DCN__c) && caserec.Type == CaseEditConstants_HUM.CorrespondenceInq){
                if(!isDCNAlphanumeric || !(bDCNSwitch && caserec.DCN__c.isAlphaNumeric())){
                    ApexPages.Message myMsg = new ApexPages.Message(ApexPages.Severity.ERROR, 'Warning: Please enter only alpha-numeric values for the DCN field');
                    ApexPages.addMessage(myMsg);
                    pgref = null;
                    returnURL = '';
                    executeNextStep = false;
                } else {
                    boolean isSuccessResponse = false;
                    if(!bDCNSwitch){
                        isSuccessResponse = HUMUtilityHelper.ehubValidationDCNService(caserec.DCN__c); 
                    } else{
                        isSuccessResponse = DCNCalloutResponse;
                    }
                    
                    if(isSuccessResponse){
                        lstCase.add(Caserec); 
                        isCreateAttachmentForDCN = true;
                    }else{
                        ApexPages.Message myMsg = new ApexPages.Message(ApexPages.Severity.ERROR, 'DCN :  The DCN provided is not valid. Please enter a valid DCN.');
                        ApexPages.addMessage(myMsg);
                        pgref = null;
                        returnURL = '';
                        executeNextStep = false;
                    }
                }
            } 


            if (executeNextStep && sClassification != null && sClassification != CaseEditConstants_HUM.None && sIntent != null && sIntent != CaseEditConstants_HUM.None && medicareComplaintCheck() && bCheckMediCC)
            {
                updateCaseRecord();
                if (Caserec.Policy_Member__c != null && String.isNotBlank(Caserec.Policy_Member__c))
                {
                    Policy_Member__c p = [SELECT Id, Member__c FROM Policy_Member__c WHERE Id = :Caserec.Policy_Member__c];
                    if (p.Member__c == CaseRec.AccountId) { callSave(); }
                    else
                    {
                        ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.error, CaseEditConstants_HUM.POLICY_MEMBER_ERROR));
                        pgref = null;
                        returnURL = '';
                        executeNextStep = false;
                    }
                }
                else { callSave(); }
                                               
                if (executeNextStep && bIsMedicareCase && listTaskWrapper != Null && String.isNotBlank(listTaskWrapper)) {
                    METTaskList_DTO_HUM metTaskWrapperListInstance = (METTaskList_DTO_HUM)JSON.deserialize(listTaskWrapper, METTaskList_DTO_HUM.Class);
                    List<Task> taskList = METEnrollment_H_HUM.createTasks(metTaskWrapperListInstance, caserecord);

                    for (Task objTask : taskList) {
                        if (objTask.Id == null && sourceIdLaunchEMMEMap != null && sourceIdLaunchEMMEMap.containsKey(objTask.MET_Source__c)) {
                            checkLaunchEMME = sourceIdLaunchEMMEMap.get(objTask.MET_Source__c);
                            Member_ID__c memberNumber;
                            String URLValue;
                            if (checkLaunchEMME)
                            {
                                if (CaseRec.Policy_Member__c != null && String.isNotBlank(CaseRec.Policy_Member__c))
                                {
                                        memberNumber = [SELECT Name, Member_Dependent_Code__c  FROM Member_ID__c WHERE Policy_Member__c = :caseRec.policy_member__c AND Type__c = : CaseEditConstants_HUM.HUMMEMBER_ID_TYPE LIMIT 1];  
                                }
                                URLValue = MET_Enrollment_Launch_EMME_URL__c.getInstance('URL').Launch_EMME_URL__c;
                                List<String> urlValues = URLValue.split('\\?');
                                URLValue = urlValues[0] + '?';
                                Integer i = 0;
                                Boolean isMember = ((CaseRec.Policy_Member__c != null && CaseRec.Interacting_About_Type__c == CaseEditConstants_HUM.MEMBER) ? true : false);
                                for (String urlVal : urlValues[1].split('\\&'))
                                {
                                    if (memberNumber != null)
                                    {
                                        urlVal = urlVal.replace('V1', (String.isNotBlank(memberNumber.Member_Dependent_Code__c) && isMember) ? memberNumber.Member_Dependent_Code__c : '');
                                        urlVal = urlVal.replace('V2', (String.isnotblank(memberNumber.Name) && isMember) ? membernumber.Name : '');
                                        
                                    }
                                    else
                                    {
                                        urlVal = urlVal.replace('V1', '');
                                        urlVal = urlVal.replace('V2', '');
                                    }
                                    urlVal = urlVal.replace('V3', (string.IsNotBlank(CaseRec.Policy_Member__r.Group_Number__c) && isMember) ? CaseRec.Policy_Member__r.Group_Number__c : '');
                                    urlVal = urlVal.replace('V4', (String.IsnotBlank(CaseRec.Policy_Member__r.Policy_Platform__c) && isMember) ? CaseRec.Policy_Member__r.Policy_Platform__c : '');
                                    urlVal = urlVal.replace('V5', (String.isNotBlank(CaseRec.Medicare_Track_ID__c)) ? CaseRec.Medicare_Track_ID__c : '');
                                    urlVal = urlVal.replace('V6', (String.isNotBlank(CaseRec.CaseNumber)) ? CaseRec.CaseNumber : '');
                                    if (i != 0)
                                        URLValue = URLValue + '&' + urlVal;
                                    else
                                        URLValue = URLValue + urlVal;

                                    i++;
                                }
                                String ssoURL = Endpoint_Url_HUM__c.getInstance('SSO_LOGINURL').Remote_Url__c;
                                launchEmmeURL = ssoURL + '?HIDDENTARGET=' + URLValue;
                                break;
                            }
                        }
                    }
                    List<Database.UpsertResult> lstsaveTasks = Database.upsert(taskList, true);
                    HUMExceptionHelper.processUpsertResults(lstsaveTasks, taskList, 'CaseEdit_CE_HUM', 'save', 'Task');
                    writeMstoneToTrackService(lstsaveTasks, taskList);
                }
                else if(executeNextStep && String.isBlank(sPrevSubtype) && String.isNotBlank(caseRec.subtype__c))   //Non-Medicare to Medicare Case Update (Subtype change from None to Medicare Subtype)
               {
                    /* Get Milestones for the Case Subtype Change */
                    caseSubtypeOnlyChangeMStone(); 
                }
                
                if (executeNextStep && bIsPharmacyUser && bIsLogCodelistAvailable && (string.isNotBlank(casecom.CommentBody) && string.isNotBlank(sCode)))
                {
                    readyForePostCommit = true;
                    pgref = null;
                    returnURL = '';
                    executeNextStep = false;
                }
                else if (executeNextStep)
                {
                    if (casecom.CommentBody<>NULL && casecom.CommentBody<> '')
                    {
                        saveCaseComment();
                    }
                    saveClaimNumbersTobackend();
                    PageReference pageRef;
                                      if(bCaseTransferRedirectionRequired)
                    {
                        Case objCase = [Select Owner_Queue__c from Case Where Id =: caseRec.id];
                        If(objCase != Null && objCase.Owner_Queue__c ==  caseRecord.Owner_Queue__c)
                        {   
                            pageRef = Page.HUMChangeCaseOwner;
                            pageRef.getParameters().put('id',caseRec.id);
                            returnURL = '';
                            pageRef.setredirect(true);
                        }
                        else
                        {
                            bCaseTransferRedirectionRequired = false;
                            pageRef = getDetailPage(pageRef);
                        }
                    }
                    else
                    {
                        pageRef = getDetailPage(pageRef);
                    }
                    if (checkLaunchEMME)
                    {
                        pgref = null;
                        returnURL = '';
                        executeNextStep = false;
                    }
                    else
                    {
                        pgref = pageRef;
                    }
                }
            if(executeNextStep && isCreateAttachmentForDCN)
                HUMUtilityHelper.createAttachmentForDCN(lstCase);
            }  
            else
            {
                pgref = null;
                returnURL = '';
                executeNextStep = false;
            }
                           
        }
        catch (Exception ex)
        {
            // check to show custom validation message on same page
            if (ex.getMessage().contains(CaseEditConstants_HUM.CASEEDIT_VALIDATION_EXP_HUM))
            {
                ApexPages.addMessages(ex);
               database.rollback(dataSavePoint);
            }
            else
            {
                HUMExceptionHelper.logErrors(ex, 'CaseEdit_CE_HUM', 'save');
            }
            pgref = null;
            returnURL = '';
            executeNextStep = false;
        }
        if (isDetailURL == false && !bCaseTransferRedirectionRequired && !Test.isRunningTest()) return null;
        return pgref;
    }
    
    /*
    * This returns case detail pagereference object
    * @param   PageReference
    * @return  PageReference
    */
    private PageReference getDetailPage(PageReference pageRef)
    {        
        pageRef = new PageReference('/' + caseRecord.Id);
        pageRef.setRedirect(true);
        returnURL = '/' + caseRecord.Id;
        return pageRef;
    }
    
    /*
    * This method is to save claim numbers to case
    * @param   NA
    * @return  void
    */
    
    public void saveClaimNumbersTobackend()
    {
        if(lstClaims != null && String.IsNotBlank(sClaimNumbers) && bClaimNumberBtn)        
        {
                    sClaimNumbers = sClaimNumbers.substring(0,sClaimNumbers.length());
                    List<String> lstClaimNumbers = sClaimNumbers.split(',');
                    Attachment_Log__c  AttachmentObj = new Attachment_Log__c();
                  if(schema.SobjectType.Attachment_Log__c.fields.case__c.isCreateable())
                    AttachmentObj.case__c = caserecord.id;
                  if(schema.SobjectType.Attachment_Log__c.fields.Source_System__c.isCreateable())
                    AttachmentObj.Source_System__c = CaseEditConstants_HUM.CRM_CON;
                  if(schema.SobjectType.Attachment_Log__c.fields.Attachment_Type__c.isCreateable())
                    AttachmentObj.Attachment_Type__c = CaseEditConstants_HUM.CLAIMS_MANUAL_ENTRY;
                 if(schema.SobjectType.Attachment_Log__c.fields.Attachment_Sub_type__c.isCreateable())  
                    AttachmentObj.Attachment_Sub_type__c = CaseEditConstants_HUM.CLAIMS_MANUAL_ENTRY;
                 if(schema.SobjectType.Attachment_Log__c.fields.Created_By__c.isCreateable())  
                    AttachmentObj.Created_By__c = UserInfo.getName();
                    Database.SaveResult sResult = Database.insert(AttachmentObj, true); 
                    if(sResult.isSuccess())
                    {
                       List<Attachment_Log_Item__c> lstAttachLogItem = new List<Attachment_Log_Item__c>();
                       If(lstClaimNumbers != Null && !lstClaimNumbers.isEmpty())
                       { 
                         for(Integer I=0;I<lstClaimNumbers.size();I++)
                          {
                            if(String.isNotBlank(lstClaimNumbers[I]))
                                {
                                    Attachment_Log_Item__c AttachLogItem = new Attachment_Log_Item__c(name=CaseEditConstants_HUM.CLAIM_NUMBER, value__c = lstClaimNumbers[I],Attachment_Log__c = AttachmentObj.id);                                    
                                    lstAttachLogItem.add(AttachLogItem);
                                }
                            }
                            Database.SaveResult[] srlstAttachLogItem = Database.insert(lstAttachLogItem, false);
                            sClaimNumbers = '';
                            List<Error_Log__c> listErrorLogs = new List<Error_Log__c>();
                            for(Database.SaveResult sr : srlstAttachLogItem) 
                            {
                                if(!sr.isSuccess()) 
                                {
                                      for(Database.Error err : sr.getErrors()) 
                                      {
                                        listErrorLogs.add(new Error_Log__c(Error_Code__c = String.valueOf(err.getStatusCode()),
                                        Error_Message__c = err.getMessage(),
                                        class_name__c = 'CaseEdit_CE_HUM',
                                        Method_Name__c = 'Save',
                                        Object_Type__c = 'Attachment_Log_Item__c'));
                                        HUMExceptionHelper.saveLog(listErrorLogs, false); 
                                        }
                                      }
                                }
                            }
                    }
                    else
                    { 
                       throw new HUMCustomException('Error in Creating attachment records to store claim numbers: '+sResult.getErrors()[0]);                     
                    }                       
              }
         }

    /*
    * This method saves the case comment record
    * @param   NA
    * @return  void
    */
    public void saveCaseComment()
    {
        List<Database.SaveResult> lstsaveCaseComments = Database.insert(lstCaseComment, false);

        for (Database.SaveResult sr : lstsaveCaseComments)
        {
            if (!sr.isSuccess())
            {
                for (Database.Error err : sr.getErrors())
                {
                    if (err.getMessage().contains(CaseEditConstants_HUM.CASECOMMENT_SIZE_EXCEPTION_HUM))
                    {
                        throw new HUMCustomException(system.Label.CASECOMMENT_LENGTHWARNING_HUM);
                    }
                }
            }
        }
    }

    /*
    * This method saves the HP case comment record
    * @param   NA
    * @return  void
    */
    public pageReference saveHPCaseComment()
    {
        returnURL = '';
        string bRedirect = '';
        PageReference pageRef;
        string bErrorCaseComment = '';
        caseComment caseComObj;
        List<CaseComment> lstHPCaseComment = new List<CaseComment>();

        if (ApexPages.currentpage().getParameters().containsKey('redirectFlag'))
        {
            bRedirect = ApexPages.currentpage().getParameters().get('redirectFlag');
        }
        if (ApexPages.currentpage().getParameters().containsKey('errorCaseComment'))
        {
            bErrorCaseComment = ApexPages.currentpage().getParameters().get('errorCaseComment');
        }

        if (bErrorCaseComment == 'true')
        {
            sHiddenCaseComment = sHiddenCaseCommentMemberNotFound;
        }
        else if (bErrorCaseComment == 'false')
        {
            sHiddenCaseComment = sHiddenCaseCommentFaultError;
        }

        if (string.isNotBlank(sHiddenCaseComment) && schema.SobjectType.caseComment.fields.parentId.isCreateable() && schema.SobjectType.caseComment.fields.commentBody.isCreateable())
        {
            caseComObj = new caseComment(parentId = caseRecord.id, commentBody = sHiddenCaseComment);
            if (caseComObj != null)  lstHPCaseComment.add(caseComObj);
        }
        if (string.isNotBlank(casecom.CommentBody) && string.isNotBlank(sCodeHidden))
        {
            caseComObj = new caseComment();
            caseComObj.CommentBody = '(' + sCodeHidden + ') ' + casecom.commentBody;
            caseComObj.parentId = caseRecord.id;
            if (caseComObj != null)  lstHPCaseComment.add(caseComObj);
        }
        if (!lstHPCaseComment.isEmpty())
        {
            insert lstHPCaseComment;
            if (bRedirect == 'true')
            {
                if(bCaseTransferRedirectionRequired)
                {
                    Case objCase = [Select Owner_Queue__c from Case Where Id =: caseRec.id];
                    If(objCase != Null && objCase.Owner_Queue__c ==  caseRecord.Owner_Queue__c)
                    {
                        pageRef = Page.HUMChangeCaseOwner;
                        pageRef.getParameters().put('id',caseRec.id);
                        //returnURL = '/apex/humchangecaseowner?id=' + caseRecord.id;
                        returnURL = CaseDetailConstants_HUM.surl + CaseEditConstants_HUM.HUMChangeCaseOwner + CaseDetailConstants_HUM.sparam + caseRecord.id;
                        pageRef.setredirect(true);
                    }
                    else
                    {
                        bCaseTransferRedirectionRequired = false;
                        pageRef = getDetailPage(pageRef);
                    }
                }
                else
                {
                    pageRef = getDetailPage(pageRef);
                }
            }
        }
        if (isDetailURL == false && !bCaseTransferRedirectionRequired && !Test.isRunningTest()) return null;
        return pageRef;
    }
    /*
    * This method calls HUMNewCaseUnderConstruction page to display user friendly message  on click of Save & New
    * @param   NA
    * @return  Pagereference
    */
    public pageReference saveNew()
    {
        try
        {
            PageReference pg = Page.HUMNewCaseUnderConstruction;
            pg.setRedirect(true);
            return pg;
        }
        catch (Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'CaseEdit_CE_HUM', 'saveNew');
            return null;
        }
    }
    
     /*
    * Method name : changeCaseRecordType
    * Description : This method will change the case record type based on the conditions
    * Return Type : void
    * Parameter   : N/A
    */
    @testvisible
    private void changeCaseRecordType()
    {
        try
        {
            bchangeRecordType = false;
            String sClassificationTrim = sClassification.removeEnd(CaseEditConstants_HUM.CASEEDIT_CLASSIFICATION_SEPERATOR);
            for (Classification__c classification : [SELECT Name FROM Classification__c where id = :sClassificationTrim])
            {
                sClassificationName = classification.Name;
            }
            for (Intent__c intent : [SELECT Name FROM Intent__c where id = :sIntent])
            {
                sIntentName = intent.Name;
            }
            List<RSO_HP_CTCI_Combination__mdt> lstRSOHPCTCI = [SELECT id FROM RSO_HP_CTCI_Combination__mdt WHERE Classification_Type__c=:caseRec.Classification_Type__c AND Classification__c=:sClassificationName AND Intent__c=:sIntentName];
            if ((lstRSOHPCTCI!=null && !lstRSOHPCTCI.isEmpty()) && (!setPharRecType.contains(caseRecord.RecordType.name)))
            {
                bchangeRecordType = getCaseRecordType(CaseEditConstants_HUM.CASE_RECORD_TYPES);
                bIsPharmacyUser = true;
                bPharCaseRecType = true;
                bRSOHPFlag = true;                
                sCallBenefitCategory = CaseEditConstants_HUM.None;
            }
            else if ((lstRSOHPCTCI==null || lstRSOHPCTCI.isEmpty()) && (setPharRecType.contains(caseRecord.RecordType.name)))
            {
                bchangeRecordType = getCaseRecordType(CaseEditConstants_HUM.HP_CASE_RECORD_TYPES);
                bIsPharmacyUser = false;
                bPharCaseRecType = false;
                bRSOHPFlag = false;               
            }           
        }
        catch (Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'CaseEdit_CE_HUM', 'changeCaseRecordType');
        }
    }
    
    /*
    * Method name : getCaseRecordType
    * Description : This method returns the case record type
    * Return Type : boolean
    * Parameter   : Map<String,String>
    */
    @testvisible
    private boolean getCaseRecordType(Map<String,String> mapCaseRecordTypes)
    {
        String sCaseRecName = '';
        Id iCaseRecordType = null;
            
        if(mapCaseRecordTypes!=null && !mapCaseRecordTypes.isEmpty())
        {
            sCaseRecName = mapCaseRecordTypes.get(caseRecord.RecordType.name);
            if(String.isNotBlank(sCaseRecName)){
                sCaseRecName =  sCaseRecName.trim();
                iCaseRecordType = Schema.SObjectType.Case.getRecordTypeInfosByName().get(String.valueOf(sCaseRecName)).getRecordTypeId();
                if(schema.SobjectType.case.fields.RecordTypeId.isUpdateable()) caseRec.RecordTypeId = iCaseRecordType;
                caseRec.RecordType.Name = sCaseRecName;
               if(schema.SobjectType.case.fields.RecordTypeId.isUpdateable()) caseRecord.RecordTypeId = iCaseRecordType;
                caseRecord.RecordType.Name = sCaseRecName;
                bchangeRecordType = true;
            }
        }
        return bchangeRecordType;
    }

    /*
    * This method clears the  warnning message on value change of Intent Value
    * @param  NA
    * @return void
    */
    public void checkIntent()
    {
        if (MapIntent != null && sIntent != null)
        {
            ApexPages.getMessages().clear();
        }
        if (lstRSOHPPermissionSet!=null && !lstRSOHPPermissionSet.isEmpty())
        {
            bHas206PermissionSet = true;
            changeCaseRecordType();
        }
        setLogCode();
        
        //end auto populate log code based on Classification & Intent RXRF
    } 

    /*
    * This method gets  Picklist Values for the Field call Benefit Category
    * @param  NA
    * @return List<String>
    */
    public List<SelectOption> getCallBenefitCategoryValues()
    {

        List<Schema.PicklistEntry> lstCallBenefitPicklist = Case.Call_Benefit_Category__c.getDescribe().getPicklistValues();
        List<SelectOption> callBenefitCategoryOptions = New List<SelectOption>();
        callBenefitCategoryOptions.add(New SelectOption(CaseEditConstants_HUM.None, CaseEditConstants_HUM.CASEEDIT_NONE_DISP_HUM));

        if (policy != null)
        {
            if (policy.Product__r.Product_Type__c == null || policy.Product__r.Product_Type__c == '')
            {
                callBenefitCategoryOptions.add(New SelectOption(lstCallBenefitPicklist[0].getValue(), lstCallBenefitPicklist[0].getLabel()));
                callBenefitCategoryOptions.add(New SelectOption(lstCallBenefitPicklist[1].getValue(), lstCallBenefitPicklist[1].getLabel()));
            }
            else if (policy.Product__r.Product_Type__c == CaseEditConstants_HUM.PRODUCT_TYPE_MAPD)
            {
                callBenefitCategoryOptions.add(New SelectOption(lstCallBenefitPicklist[0].getValue(), lstCallBenefitPicklist[0].getLabel()));
                callBenefitCategoryOptions.add(New SelectOption(lstCallBenefitPicklist[1].getValue(), lstCallBenefitPicklist[1].getLabel()));
            }
            else if (policy.Product__r.Product_Type__c == CaseEditConstants_HUM.PRODUCT_TYPE_MA)
            {
                callBenefitCategoryOptions.add(New SelectOption(lstCallBenefitPicklist[0].getValue(), lstCallBenefitPicklist[0].getLabel()));
            }
            else if (policy.Product__r.Product_Type__c == CaseEditConstants_HUM.PRODUCT_TYPE_PDP)
            {
                callBenefitCategoryOptions.add(New SelectOption(lstCallBenefitPicklist[1].getValue(), lstCallBenefitPicklist[1].getLabel()));
            }
        }
        else
        {
            for (Schema.PicklistEntry p : lstCallBenefitPicklist)
            {
                callBenefitCategoryOptions.add(New SelectOption(p.getValue(), p.getLabel()));
            }
        }
        return callBenefitCategoryOptions;
    }

    /*
    * This method checks Call Benefit Category is Not null And satisfies corresponding Complaint value
    * @param  NA
    * @return Boolean
    */
    public Boolean medicareComplaintCheck()
    {
        Boolean isPartCPartD = true;
        if (isCallBenefitCategoryView == true && sCallBenefitCategory != null && sCallBenefitCategory == CaseEditConstants_HUM.None && bRSOHPFlag == false)
        {
            isPartCPartD = false;
        }
        return  isPartCPartD;
    }

    /*
    * This method checks whether selected policy is Medicare policy based on SegmentType
    * @param  NA
    * @return Boolean
    */
    public Boolean checkProductSegmentType()
    {
        Boolean segmentTypeFlag = false;

        if (policy.Product__r.Segment_Type__c != null && policy.Product__r.Segment_Type__c != '')
        {
            if (policy.Major_Lob__c == CaseEditConstants_HUM.MAJOR_LOB_MCD  && policy.Product__r.Segment_Type__c == CaseEditConstants_HUM.SEGMENT_TYPE_MCD)
            {
                segmentTypeFlag = true;
            }
            else if (policy.Major_Lob__c == CaseEditConstants_HUM.MAJOR_LOB_MER && policy.Product__r.Segment_Type__c == CaseEditConstants_HUM.SEGMENT_TYPE_MCR)
            {
                segmentTypeFlag = true;
            }
        }
        return segmentTypeFlag;
    }

    /*
    * This method is used to return classification type ids from ctci junction object
    * @param  NA
    * @return void
    */
    private Set<Id> getClassificationIds()
    {
        Set<Id> clsIds = new Set<Id>();
        list<CTCI_Junction__c> ctciList = [select id, Classification_Type__c, Classification__c from CTCI_Junction__c where Classification_Type__r.name = :caseRec.Classification_Type__c and IsActive__c = true];
        if (!ctciList.isEmpty())
        {
            for (CTCI_Junction__c ctci : ctciList)
            {
                clsIds.add(ctci.Classification__c);
            }
        }

        return clsIds;
    }

    /*
    * This method is used to return cIntent ids from ctci junction object for selected classification
    * @param  NA
    * @return void
    */
    private Set<Id> getIntentIds(Id sClassification)
    {
        Set<Id> intentIds = new Set<Id>();
        list<CTCI_Junction__c> ctciList = [select id, Classification_Type__c, Classification__c, Intent__c from CTCI_Junction__c where Classification_Type__r.name = :caseRec.Classification_Type__c AND Classification__c = : sClassification AND IsActive__c = true];
       if (!ctciList.isEmpty())
        {
            for (CTCI_Junction__c ctci : ctciList)
            {
                intentIds.add(ctci.Intent__c);
            }
        }

        return intentIds;
    }

    /*
    * This method gets  Picklist Values for the Field Verbal consent Obtained
    * @param  NA
    * @return List<SelectOption>
    */
    public List<SelectOption> getlstVerbalConsentOptionValues()
    {
        List<Schema.PicklistEntry> lstVerbalConsentPicklist = Case.Verbal_Consent_Obtained__c.getDescribe().getPicklistValues();
        List<SelectOption> lstVerbalConsentOptions = New List<SelectOption>();
        for (Schema.PicklistEntry p : lstVerbalConsentPicklist)
        {
            lstVerbalConsentOptions.add(New SelectOption(p.getValue(), p.getLabel()));
        }
        return lstVerbalConsentOptions;
    }

    /*
    * This method checks whether selected subtype is medicare and changes the record type accordingly
    * @param  NA
    * @return NA
    */
    public void updateCaseRecord()
    {
        String accRecTypeName;
        String profileName = HumUtilityHelper.getCurrentUserProfileName();
        
    List<MEDENROLLMENT_CASE_SUBTYPE_HUM__c> medicareCaseSubTypeList = MEDENROLLMENT_CASE_SUBTYPE_HUM__c.getAll().values();

        if (AccountRecordTypeId == NULL || AccountRecordTypeId == '')  accRecTypeName = (NULL != caseRec.Account.RecordTypeId) ? Schema.SObjectType.Account.getRecordTypeInfosById().get(caseRec.Account.RecordTypeId).getName() : NULL;
        else
        {
            accRecTypeName = (NULL != AccountRecordTypeId) ? Schema.SObjectType.Account.getRecordTypeInfosById().get(AccountRecordTypeId).getName() : NULL;
             if (caseRec.AccountId != AccountId&&(caseRec.Interacting_About_Type__c!=System.Label.HUMMemberType&& caseRec.Interacting_About_Type__c!=System.Label.HUMUnknownMember))
             {
                if(Schema.sObjectType.Case.fields.Policy_Member__c.isUpdateable()) CaseRec.Policy_Member__c = NULL;
             }
        }
        if(oldAccRecTypeName!= accRecTypeName && profileName!= CaseEditConstants_HUM.HUMHPSpecialistProfile && !CaseRec.RecordType.name.contains(CaseEditConstants_HUM.HumHPRecType)){
           
           CaseRec.RecordTypeId = HUMUtilityHelper.getCaseRecordType(accRecTypeName,CaseRec.Type , CaseRec.SubType__c, CaseRec.RecordTypeId,true ); 
        }
        else{

            if (Schema.sObjectType.Case.fields.RecordTypeId.isUpdateable()) CaseRec.RecordTypeId = HUMUtilityHelper.getCaseRecordType(accRecTypeName,CaseRec.Type , CaseRec.SubType__c, CaseRec.RecordTypeId );  
        }           
        if (Schema.sObjectType.Case.fields.Interacting_About_Type__c.isUpdateable()) caseRec.Interacting_About_Type__c = mapCaseInterAboutType.get(accRecTypeName).CaseInteraction_About_Type__c;
    }

    /**
    * Returns responses of continuation object
    * <p>
    * Callback Method to parse the Get log Note service response
    *
    * @param  List<String> List of Continuation Labels
    * @param  Object State of Continuation
    * @return Object
    */

    public static object parseLogNoteResponse(List < string > labels, object State)
    {
        RightSourceEditMemberResponse_DTO_HUM dtoRes;
        boolean successFlag;
        String sNoFaultResponse ='';
        String sFaultResponse = '';
        List<String> lstString = new List<String>();
        try
        {
            HttpResponse logNoteResponse = Continuation.getResponse(labels[0]);

            if (logNoteResponse != null && !logNoteResponse.getBody().containsIgnoreCase(CaseEditConstants_HUM.VOB_FAULT_CODE) && ((logNoteResponse.getStatusCode() == CaseEditConstants_HUM.HTTP_OK_HUM || logNoteResponse.getStatusCode() == CaseEditConstants_HUM.HTTP_ACCEPTED_HUM) || Test.isRunningTest()))
            {
                dtoRes = (RightSourceEditMemberResponse_DTO_HUM)System.JSON.deserialize(logNoteResponse.getBody(), RightSourceEditMemberResponse_DTO_HUM.class);
                if (null != dtoRes && null != dtoRes.EditMemberResponse && dtoRes.EditMemberResponse.ErrorDescription != CaseEditConstants_HUM.HUM_MEMBER_NOT_FOUND)
                {
                    if (dtoRes != null && dtoRes.EditMemberResponse != null && dtoRes.EditMemberResponse.UID == '')
                    {
                        successFlag = true;
                        sNoFaultResponse = 'true';
                    }
                    else if (dtoRes != null && dtoRes.EditMemberResponse != null && string.isNotBlank(dtoRes.EditMemberResponse.UID))
                    {
                        Error_Log__c errorLog = new Error_Log__c(Error_Code__c = 'Sever Down Exception',
                            Error_Message__c = dtoRes.EditMemberResponse.UID,
                            Request_Sent__c = true,
                            class_name__c = 'CaseEdit_CE_HUM',
                            Method_Name__c = 'parseLogNoteResponse',
                            Type__c = 'Integration Error');
                        if (errorLog != null) insert errorLog;
                        successFlag = false;
                        sNoFaultResponse = 'false';
                    }
                }
                else
                {
                    sFaultResponse = 'true';
                }
            }
            else
            {
                // Fault Code
                sFaultResponse = Pharmacy_H_HUM.logHpFaultError(logNoteResponse, 'CaseEdit_CE_HUM', 'parseLogNoteResponse', State != null ? String.valueof(State) : '').contains('Member not found') ? 'true' : 'false';
            }
            lstString.add(sNoFaultResponse);
            lstString.add(sFaultResponse);
        }
        catch (Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'CaseEdit_CE_HUM', 'parseLogNoteResponse');

        }
        return lstString;
    }

    /**
    * Returns continuation object
    * <p>
    * Method to call continuation serive for log note.
    *
    * @param sComment                      String
    * @param sEnterpriseId                 String
    * @param sNetworkUserId                String
    * @param sRecordId                     String
    * @param sCode                         String
    * @return                              continuation
    */
    @RemoteAction
        public static Continuation sendRequestLogNotes(String sComment, String sEnterpriseId, String sNetworkUserId, String sRecordId, String sCode, String caseId)
    {
        try
        {
            HUM_Webservice_Callout__c serviceDetails = HUM_Webservice_Callout__c.getValues('RighSourceService_EditMember');
            list<string> stateList = new list<string>();
            // Create continuation with a timeout
            integer iTIMEOUT_INT_SECS = Integer.valueof(serviceDetails.Timeout__c) / 1000;
            Continuation con = new Continuation(iTIMEOUT_INT_SECS);
            con.continuationMethod = 'parseLogNoteResponse';
            con.State = sRecordId;
            HttpRequest httpEditGetMemberReqObj = PharmacyCaseComment_C_HUM.builtHttpRequest(serviceDetails);
            httpEditGetMemberReqObj.setBody(JSON.serialize(PharmacyCaseComment_C_HUM.RequestLogNotes(sComment, sEnterpriseId, sNetworkUserId, sCode)));
            con.addHttpRequest(httpEditGetMemberReqObj);
            return con;
        }
        catch (Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'CaseEdit_CE_HUM', 'sendRequestLogNotes');
            return null;
        }
    }    

    /**
    * Returns list of log node
    * <p>
    * Method to create list of log notes
    *
    * @param  NA
    * @return list<SelectOption>
    */
    public list<SelectOption> getCaseCommentOptions()
    {
        list<SelectOption> lstSortBy = new list<SelectOption>();
        lstSortBy.add(new SelectOption('', ''));
        for (String code : Pharmacy_H_HUM.fetchLogCode(caseRec.Classification_Type__c))
        {
            lstSortBy.add(new SelectOption(code, code));
        }
        return lstSortBy;

    }

    /*
    * This method is to populate Log Node Code.
    * @param  NA
    * @return Pagereference
    */
    public PageReference populateHiddentVal()
    {
        sCodeHidden = sCode;
        return null;
    }
    /*
    * This method helps in opening/closing the Modal window on for Account Lookup
    * @param  NA
    * @return NA
    */
    public void getopenAccountLookup()
    {
        if (viewAccountLookup)
        {
            viewAccountLookup = false;
        }
        else
        {
            viewAccountLookup = true;
        }
    }
    /*
    * This method helps in showing/hiding the Modal window on for Custom Interacting With Lookup
    * @param  NA
    * @return NA
    */
    
    public void validateInteractingWithType()
    {
        IntWithName = caseRec.Interacting_With__r.Name;
        if(caseRec.Interacting_With_Type__c == 'Member')
        {
            intWithMemberType = true;
        }
        else
        {
            intWithMemberType = false;
        }
    }
    
    /*
    * This method Is to Check to display Esclation indicator
    * @param  NA
    * @return void
    */
    public void showEsclIndicator()
    {
        List<String> lstInteractingAboutType = new List<String>{'Agent', 'Group', 'Member', 'Provider', 'Unknown-Agent', 'Unknown-Group', 'Unknown-Member', 'Unknown-Provider'};
        List<String> lstInteractionOrigin = new List<String>{'Service Inquiry', 'NINA Web Chat', 'Internal Process', 'Watson Voice', 'Watson Web Chat', 'Vantage', 'Cancelled'};
        if(caseRec.status != 'Closed' && caseRec.Status != 'Cancelled' && lstInteractingAboutType.contains(caseRec.Interacting_About_Type__c) && !lstInteractionOrigin.contains(caseRec.Origin)){
            bShowEsclIndicator = true;
            if(caseRec.Origin == 'IVR' && caseRec.Classification_Type__c != 'Calls (RSO)'){
                bShowEsclIndicator = false;
            }  
        }
    } 
    
    /*
    * This method is to check the permission set and Case origin for Medicare Calls Part C or Part D
    * @param  NA
    * @return null
    */

    public void validateMedicareCustomerServiceAccess()
    {
        try
        {
            String UserId = Userinfo.getUserId();
            PermissionSetAssignment pCheck = [SELECT Id FROM PermissionSetAssignment WHERE PermissionSet.Name = :CaseEditConstants_HUM.MEDICARECALLS_PERMISSIONSET_HUM AND AssigneeId = : userId];
            if (pCheck != null && CaseRec.Origin == CaseEditConstants_HUM.MEDICARECALLS_CASEORIGIN_HUM)
            {
                checkMedicareCustomerServiceAccess = true;
            }
            else { checkMedicareCustomerServiceAccess = false; }
        }
        catch (Exception e)
        {
            checkMedicareCustomerServiceAccess = false;
        }

    }

    /*
    * This method is used to reset render variables for Medicare Part C&D field(used only in case of medicare enrollment cases)
    * @param  NA
    * @return NA
    */
    public void rerenderMedicareSection()
    {
        isCallBenefitCategoryView = false;
        sCallBenefitCategory = '';
        if (checkMedicareCustomerServiceAccess)
        {
            if (string.isNotBlank(caseRec.Policy_Member__c))
            {
                list<Policy_Member__c> lstPolmem = [SELECT Id, Policy__c FROM Policy_member__c WHERE Id = :caseRec.Policy_Member__c LIMIT 1];
                if (!lstPolmem.isEmpty() && lstPolmem[0].Policy__c != null)
                {
                    this.policy = [SELECT Major_Lob__c, Policy__c.Product__c, Policy__c.Product__r.Product_Type__c, Policy__c.Product__r.Segment_Type__c, Policy__c.Product__r.ProductCode from Policy__c WHERE id = :lstPolmem[0].Policy__c LIMIT 1];
                    Map<String, Set<String>> medicareLOBProductType = CaseEditConstants_HUM.MEDICARE_LOB_PRODUCTTYPE_LIST();
                    if (string.isNotBlank(policy.Product__r.ProductCode) && policy.Product__r.ProductCode == CaseEditConstants_HUM.MEDICARE_POLICY && medicareLOBProductType.containsKey(policy.Major_Lob__c))
                    {
                        Set<String> TempProductType = medicareLOBProductType.get(policy.Major_Lob__c);
                        if (string.isNotBlank(policy.Product__r.Product_Type__c) && TempProductType.contains(policy.Product__r.Product_Type__c)) isCallBenefitCategoryView = true;
                        else if (string.isBlank(policy.Product__r.Product_Type__c)) isCallBenefitCategoryView = checkProductSegmentType();
                    }
                }
                else if (string.IsBlank(caseRec.Policy_Member__c) && caseRec.Interacting_About_Type__c == CaseEditConstants_HUM.Member) isCallBenefitCategoryView = true;
            }
            else isCallBenefitCategoryView = true;
            if (isCallBenefitCategoryView)
            {
                sCallBenefitCategory = CaseEditConstants_HUM.None;
                getCallBenefitCategoryValues();
                if (string.IsBlank(caseRec.Policy_Member__c)) policy = null;
            }
        }
    }

    /*
    * This method is used to populate JSON for rendering MET Tasks as DataTable in MET Enrollment Section
    * @param  caseRecord
    * @return List<Task>
    */
    public void fetchMetTaskList()
    {
        try
        {
            METTaskList_DTO_HUM objMETTaskList = METEnrollment_H_HUM.getTaskListDTO(caseRecord.Id);
            jsonMetTaskList = JSON.serialize(objMETTaskList.listMETTaskDTO);
            jsonMetTaskList = jsonMetTaskList.replace('\'', '\\\'');
            jsonMetTaskList = '{"data":' + jsonMetTaskList + '}';
        }
        catch (Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'CaseEdit_CE_HUM', 'fetchMetTaskList');
        }
    }
    
    /*
    * This method is used to populate existing multiple tasks in MET Enrollment Section
    * @param  NA
    * @return void
    */
    public void loadMultipleMETTask()
    {
        try{          
            sMultipleMETTaskJSON = '';
            if(String.isNotBlank(sMETEntriesCreationValue) && sMETEntriesCreationValue.length() >0){                
                List<METEnrollment_H_HUM.MultipleMETTask_DTO_AHT_HUM> lstMultipleMETTask = METEnrollment_H_HUM.getMultipleMETTasks(caseRec.Type, caseRec.SubType__c, sMETEntriesCreationValue);

                 if(!lstMultipleMETTask.isEmpty()) {
                    if (sourceIdLaunchEMMEMap == null) sourceIdLaunchEMMEMap = new Map<String, Boolean>();

                    for (METEnrollment_H_HUM.MultipleMETTask_DTO_AHT_HUM dtoTask : lstMultipleMETTask) {
                        sourceIdLaunchEMMEMap.put(dtoTask.sSourceId, dtoTask.bLaunchEMME);
                    }
                    
                    lstMultipleMETTask.sort();
                    sMultipleMETTaskJSON = JSON.serialize(lstMultipleMETTask);
                   
                }
            }
        } catch(Exception e){
            HUMExceptionHelper.logErrors(e, 'CaseEdit_CE_HUM', 'loadMultipleMETTask');
        }
    }
    
     /**
     * Get list of values for drop down
     * @return List<SelectOption>
     */
    public List<SelectOption> getMETEntriesCreation() {
        List<SelectOption> options = new List<SelectOption>();
        try{
            List<String> lstMETEntries  = METEnrollment_H_HUM.getMultipleMETEntries(caseRec.Type, caseRec.SubType__c);
            if(!lstMETEntries.isEmpty()) {
                options.add(new SelectOption('', CaseEditConstants_HUM.NONEDISPLAY_HUM));
                for(String sValue : lstMETEntries) {
                    options.add(new SelectOption(sValue, sValue));
                }
            }
        } catch(Exception e) {
            HUMExceptionHelper.logErrors(e, 'CaseEdit_CE_HUM', 'getMETEntriesCreation');      
        }
       return options;
    }
    
    /*
    * This method is used to populate existing tasks in MET Enrollment Section
    * @param  NA
    * @return void
    */
    
    public void loadMETTask()
    {
        String caseType = caseRec.Type;
        String caseSubType = caseRec.SubType__c;
        List<MET_Task__c> lstMetTask = METEnrollment_H_HUM.getMETTasks(caseType,caseSubType);
        METTaskJSON = JSON.serialize(lstMetTask);    
    }
   
    /*
    * This method is used to populate existing actions related to MET Task that is selected in  MET Enrollment Section
    * @param  NA
    * @return void
    */
        
    public void loadMETAction()
    {
        String caseType = caseRec.Type;
        String caseSubType = caseRec.SubType__c;
        List<Met_Action__c> lstMetAction = METEnrollment_H_HUM.getMETActions(caseType,caseSubType,selectedMETTaskId);
        METActionJSON = JSON.serialize(lstMetAction);
    }
   
    /*
    * This method is used to populate existing sources related to MET Action that is selected in MET Enrollment Section
    * @param  NA
    * @return void
    */
    
    public void loadMETSource()
    {
                String caseType = caseRec.Type;
        String caseSubType = caseRec.SubType__c;
        if (sourceIdLaunchEMMEMap == null) sourceIdLaunchEMMEMap = new Map<String, Boolean>();
        List<MET_Source__c> lstMetSource = METEnrollment_H_HUM.getMETSources(caseType,caseSubType,selectedMETTaskId,selectedMETActionId);
        if (lstMetSource != null && !lstMetSource.isEmpty())
        {
            for (MET_Source__c src : lstMetSource) {
                sourceIdLaunchEMMEMap.put(src.id, src.Launch_EMME__c);
            }
        }
        
        METSourceJSON = JSON.serialize(lstMetSource);
    }

    /*
    * Method to get the newly created TaskID from Upsert Operation of CreateTasks Method and pass this ID to write Milestones to Track Service
    *
    * @param  listUpsertResults(task upsert results), taskList(list of task records)
    * @return na
    */
    @TestVisible private void writeMstoneToTrackService(List<Database.UpsertResult> listUpsertResults, List<Task> taskList)
    {
        ID idTask = null;
        list<Id> resLst = new list<Id>();
        if (listUpsertResults != null && listUpsertResults.size() > 0)
        {
            //Iterate through all the Upsert Results records and find out the newly created Task record.
            for (Integer count = 0; count < listUpsertResults.size(); count++)
            {
                if (listUpsertResults[count].isSuccess() && listUpsertResults[count].isCreated())
                {
                    resLst.add(listUpsertResults[count].getID());
                }
            }
        }
        if (resLst != null && !resLst.isEmpty())        
        {            
            //Identify if it is a TRR Case
            if(caseRecord.Origin.equals(CaseEditConstants_HUM.TRR_ORIGIN) && caseRecord.Type.equals(CaseEditConstants_HUM.TRR_CASE_TYPE) && caseRecord.subtype__c.equals(CaseEditConstants_HUM.TRR_CASE_SUBTYPE))            
            {
                evaluatetasksforTRRTracking(resLst, taskList);
            }
            // Call the TrackService_H_HUM class to get the matching Milestone and send to Track Service
            else if(caseRecord.subtype__c.equals(sPrevSubtype))
            {               
                System.enqueueJob(new TrackService_Q_HUM(resLst, caseRecord.Id));
            }        
            else
            {                               
                /* Create Milestones for Both Subtype change and TAS entries */
                caseSubtypeAndTAS_MStone(resLst);
            }
        }
        else  //No new MET Tasks Added.
        {                                    
            if(!sPrevSubtype.equals(caseRecord.subtype__c) && String.isNotBlank(caseRec.subtype__c) && String.isNotBlank(sPrevSubtype))
            {
                /* Create Milestones for the Case Subtype Change*/
                caseSubtypeOnlyChangeMStone();                                 
            }            
        }
    }

    /*
    * This method is to redirect Page after Launching new Window for launch EMME
    * @param  NA
    * @return PageReference
    */
    public PageReference pageRedirect()
    {
        returnURL = '';
        PageReference pageRef;
        if (sClassification != null && sClassification != CaseEditConstants_HUM.None && sIntent != null && sIntent != CaseEditConstants_HUM.None && medicareComplaintCheck() && checkLaunchEMME)
        {
            pageRef = new PageReference('/' + caseRecord.Id);
            pageRef.setRedirect(true);
            returnURL = '/' + caseRecord.Id;

        }
        else
        {
            return null;
        }
        if (isDetailURL == false && !Test.isRunningTest())
            return null;

        return pageRef;
    }
                

    /*
    * This method helps in opening/closing the Modal window on for Policy Member Lookup
    * @param  NA
    * @return NA
    */
    public void getopenPolicyMemLookup()
    {
        if (viewPolicyMemLookup)
        {
            viewPolicyMemLookup = false;
        }
        else
        {
            viewPolicyMemLookup = true;
        }
    }

    /*
    * This method is to get the List of related Policy Member Records.
    * @param  NA
    * @return NA
    */
    public void getPolicyMemberRecs()
    {
        try
        {
            List<Policy_Member__c> lstPolicyMembers1 = new List<Policy_Member__c>();
            if(String.isNotBlank(caseRec.AccountId))
            {
                lstPolicyMembers = [SELECT id, Name, ETL_Record_Deleted__c, Product__c, Product_Type__c, Group_Name__c, Group_Number__c, Effective_Date__c, End_Date__c, Status__c  FROM Policy_Member__c where Member__c = :caseRec.AccountId order by Status__c asc];
                if(lstPolicyMembers != null && lstPolicyMembers.size()>0)
                {
                    lstPolicyMembers.sort();
                    List<Policy_Member__c> lstLegacyDelPols = new List<Policy_Member__c>();
                    For(Integer i = 0 ; i < lstPolicyMembers.size() ; i++)
                    {
                        if(lstPolicyMembers[i].Etl_Record_Deleted__c == False)
                            lstPolicyMembers1.add(lstPolicyMembers[i]);
                        else
                            lstLegacyDelPols.add(lstPolicyMembers[i]);
                    }
                    lstPolicyMembers = new List<Policy_Member__c>();
                    lstPolicyMembers.AddAll(lstPolicyMembers1);
                    lstPolicyMembers.AddAll(lstLegacyDelPols);
                }
            }
        }
        catch (exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'CaseEdit_CE_HUM', 'getPolicyMemberRecs');
        }
    }
    
    //Creditable Coverage attestation Classification and intent pre-population Variable    
    public string creditableCoverageClassificationId {get;set;}
    public string creditableCoverageIntentId {get;set;}
    /*
    * This method is to update SubType of Case on ATTVF template exit.
    * @param  NA
    * @return NA
    */
    public void updateCaseAfterATTVF()
    {        
        cleanUpUnFinishedCreditableCoverageTemplateData();            
        Case c = [Select Case_Owner__c,Guided_Process_Flag__c, Owner_Queue__c, recordtypeId, recordtype.name, SubType__c,Topic__c,Status,CTCI_List__r.Classification__r.Name,CTCI_List__r.Classification__c,CTCI_List__r.Intent__c,CTCI_List__r.Intent__r.Name from Case Where Id = :caseRecord.Id];
        
        if(String.isNotBlank(c.SubType__c) && c.SubType__c.equals(CaseEditConstants_HUM.CREDITABLECOVERAGECASESUBTYPE))
        {
            caseRec.SubType__c = c.SubType__c;
            sPrevSubtype = c.SubType__c;    //to avoid calling the track Service again on case save
        } 
        
        sClassification = c.CTCI_List__r.Classification__c;
        sIntent = c.CTCI_List__r.Intent__c;
        if(isCreditableCoverageSubtypeElligible())
        {
            if(schema.SobjectType.Case.fields.Topic__C.isUpdateable()){
            caseRec.Topic__c = CaseEditConstants_HUM.CREDITABLECOVERAGECASESUBTYPE;
            caseRecord.Topic__c = CaseEditConstants_HUM.CREDITABLECOVERAGECASESUBTYPE;
            }
            caseRec.case_owner__c = c.case_owner__c;
            caseRec.Owner_queue__c = c.Owner_queue__c;
            caseRecord.Owner_queue__c = c.Owner_queue__c;
            caseRec.recordType.name = c.recordtype.name;
            caseRec.Guided_Process_Flag__c = c.Guided_Process_Flag__c;
            attachedTemplateName = 'ATVF007';
            bIsATVF007Routed = true;
        }
                     
        creditableCoverageClassificationId  = c.CTCI_List__r.Classification__c;
        creditableCoverageIntentId = c.CTCI_List__r.Intent__c ;  
        caseRec.status = c.status;
        checkCreditableCoverageReadOnlyStatus();             
        enableDisableSaveAndTransferButton();             
    }    
             /*
    * This method is to check the case subtype is eligible for creditable Coverage.    
    * @param  NA
    * @return Boolean
    * 
    */
    @TestVisible
    private Boolean isCreditableCoverageSubtypeElligible()
    {       
        Boolean isCreditableCoverageSubtypeElligible = false;
        List<Template__c> lstTemplateObj = [Select Id from Template__c where Unique_Template_Name__c= :CaseEditConstants_HUM.ATTESTATION_TEMPLATE_NAME];  
        //Check Attestation Template exist
        if(lstTemplateObj.size()>0)
        {   
            List<Template_Submission_Owner__c> lstSubmissionOwner = [SELECT Id FROM Template_Submission_Owner__c WHERE Object_Owner_ID__c = :caseRec.Id];
            //Check Attestation Template submission exist
            if(lstSubmissionOwner.size()>0)
            {
                List<Template_Submission__c> lstTemplateSubmission = [SELECT Id FROM Template_Submission__c WHERE Submission_Owner__c = :lstSubmissionOwner[0].Id AND Template__c = :lstTemplateObj[0].Id];
                //Check Attestation Template submission owner exist
                if(lstTemplateSubmission.size()>0)
                {
                    //Check Attestation Template Submission data exist
                    for(Template_Submission_Data__c subDataObj : [SELECT Id, Template_Field__r.Name, Value__c FROM Template_Submission_Data__c WHERE Template_Submission__c = :lstTemplateSubmission[0].Id])    
                    {
                        //Check Attestation Disclaimer is selected (i.e.) Process attached and its ready to send CICMS 
                        if(subDataObj.Template_Field__r.Name  == CaseEditConstants_HUM.ATTESTATION_DISCLAIMERNAME && subDataObj.Value__c == CaseEditConstants_HUM.ATTESTATION_DISCLAIMERYESVALUE )
                        {
                            isCreditableCoverageSubtypeElligible = true;
                            break;
                        }
                    }
                }
            }
        }
        
        return isCreditableCoverageSubtypeElligible;
    }
    
    /*
    * This method is to make check the creditable process is attached with the case.    
    * @param  NA
    * @return NA
    * 
    */
    @TestVisible
    private Boolean isCreditableCoverageProcessAttached()
    {       
        Boolean isCreditableCoverageProcessAttached = false;
        List<Template__c> lstTemplateObj = [Select Id from Template__c where Unique_Template_Name__c= :CaseEditConstants_HUM.ATTESTATION_TEMPLATE_NAME];  
        //Check Attestation Template exist
        if(lstTemplateObj.size()>0)
        {   
            List<Template_Submission_Owner__c> lstSubmissionOwner = [SELECT Id FROM Template_Submission_Owner__c WHERE Object_Owner_ID__c = :caseRec.Id];
            //Check Attestation Template submission exist
            if(lstSubmissionOwner.size()>0)
            {                
                List<Template_Submission__c> lstTemplateSubmission = [SELECT Id FROM Template_Submission__c WHERE Submission_Owner__c = :lstSubmissionOwner[0].Id AND Template__c = :lstTemplateObj[0].Id];
                //Check Attestation Template submission owner exist
                if(lstTemplateSubmission.size()>0)
                {
                    //Check Attestation Template Submission data exist
                    for(Template_Submission_Data__c subDataObj : [SELECT Id, Template_Field__r.Name, Value__c FROM Template_Submission_Data__c WHERE Template_Submission__c = :lstTemplateSubmission[0].Id])    
                    {
                        //Check Attestation data is attached 
                        if(string.isNotBlank(subDataObj.Value__c))
                        {
                            isCreditableCoverageProcessAttached = true;
                            break;
                        }
                    }
                }
            }
        }
        
        return isCreditableCoverageProcessAttached;
    }
    /*
    * This method is to make case status as readonly when it fall in creditable coverage condition and make it readonly.    
    * @param  NA
    * @return NA
    * 
    */
    @TestVisible
    private void checkCreditableCoverageReadOnlyStatus()
    {
       /*This will disable the case status only once between Attestation form finish action and the save case action*/
        bCreditableStatusReadonly = false;
        if(caserec.status == CaseEditConstants_HUM.CASE_STATUS_PENDING_RESPONSE)
        {
            bCreditableStatusReadonly = ([SELECT Value__c FROM Template_Submission_Data__c WHERE Template_Submission__r.Template__r.Unique_Template_Name__c = :CaseEditConstants_HUM.ATTESTATION_TEMPLATE_NAME AND Template_Submission__r.Submission_Owner__r.Object_Owner_ID__c = :caserec.Id].size()>0) ? true : false;            
        }
    }
    
    /*
    * This method is to clean up unfinished creditable coverage in order to launch the creditable form again.    
    * @param  NA
    * @return NA
    * 
    */   
    public void cleanUpUnFinishedCreditableCoverageTemplateData()
    {
         HUMValidSubmissionHelper.deleteInValidSubmission(caserec.id);
    }

/*
    * This method Is to create the list of floating links which are visible at the top of the customized case Detail VF pages 
    * @param  NA
    * @return void
    */
    public void createFloatingList()
    {
        lstFloatingLinks = new list<string>();
        lstFloatingLinks.add(CaseDetailConstants_HUM.sfloatLink20); // Case Information
        lstFloatingLinks.add(CaseDetailConstants_HUM.sfloatLink2); // Related Acc
        lstFloatingLinks.add(CaseDetailConstants_HUM.sfloatLink4);  // Additional info
        If(bMedicareCalls && bCheckMedicareCCPermission && !bIsPharmacyUser)
        {
            lstFloatingLinks.add(CaseDetailConstants_HUM.sfloatLink21 );  // Medicare Case comments
        }
        If((!bMedicareCalls || !bCheckMedicareCCPermission) || bIsPharmacyUser)
        {
            lstFloatingLinks.add(CaseDetailConstants_HUM.sfloatLink7);     // Case comments
        }
        if(system.Label.HUMMemberCase  == caserec.RecordType.name)
        {
            lstFloatingLinks.add(CaseDetailConstants_HUM.sfloatLink5);     // G&AComplaint
        }
        if(system.Label.HUMMemberCase  == caserec.RecordType.name)
        {
            lstFloatingLinks.add(CaseDetailConstants_HUM.sfloatLink6);    // Texas ComplaintLetter
        }
        lstFloatingLinks.add(CaseDetailConstants_HUM.sfloatLink17);    // System Info
    }
    
    /*
    * This method is to clean up unfinished creditable coverage in order to launch the creditable form again.    
    * @param  NA
    * @return NA
    * 
    */   
    public void cleanUpTemplateData()
    {
        String isAttvfCalled = Apexpages.currentPage().getParameters().get('isATTVF') ;       
        if( string.isNotBlank(isAttvfCalled ) && isAttvfCalled  == 'true')
        {
            cleanUpUnFinishedCreditableCoverageTemplateData();
        }        
    }
    
    /*
    * This method Is to create the Milestone Entry for only the Case Subtype changes 
    * @param  NA
    * @return void
    */
    @TestVisible
    private void caseSubtypeOnlyChangeMStone()
    {
        set<MET_Milestone__c> prevSubtypeMS = new set<MET_Milestone__c>();   //Should be set as empty to verify the historical Enrollment History for a 898 already written
        set<MET_Milestone__c> caseMS = new set<MET_Milestone__c>();
        map<Id, set<MET_Milestone__c>> mapCaseMilestones = new map<Id, set<MET_Milestone__c>>();
        
        /* Get Milestones for the Case Subtype Change*/             
        caseMS =  CaseMilestoneTrackService_C_HUM.getUICaseMilestone(caseRec, sPrevSubtype, prevSubtypeMS);
        
        if(!caseMS.isEmpty())
        {                    
            mapCaseMilestones.put(caseRec.Id, caseMS);            
            System.enqueueJob(new TrackService_Q_HUM(mapCaseMilestones));
        }
    }
    
    /*
    * This method Is to create the Milestone Entry for only the Case Subtype changes along with T/A/S entries
    * @param  NA
    * @return void
    */
    @TestVisible
    private void caseSubtypeAndTAS_MStone(list<Id> resLst)
    {
        list<MET_Milestone__c> lstTaskMS = [SELECT Id, Name, Case_Type__c, Case_Subtype__c, Is_Active__c, Is_Milestone__c, Launch_EMME__c, Action__c, Task__c, Source_Name__c,
                                             MET_Milestone__c.Task__r.Name, MET_Milestone__c.Action__r.Name, MET_Milestone__c.Source_Name__r.Name 
                                            FROM MET_Milestone__c WHERe Is_Active__c = true AND Is_Milestone__c = true];
        
        list<Task> lstTasks = [SELECT Id, WhoId, WhatId, RecordTypeId, MET_Track__c, MET_Task__r.Case_Subtype__c, MET_Source__c, MET_Action__c, MET_Task__c FROM Task where Id IN :resLst];                           
                        
        map<Id, set<MET_Milestone__c>> mapCaseMilestones = new map<Id, set<MET_Milestone__c>>();
        
        set<MET_Milestone__c> caseMS = new set<MET_Milestone__c>();
        set<MET_Milestone__c> prevSubtypeMS = new set<MET_Milestone__c>();
        set<MET_Milestone__c> newSubtypeMS = new set<MET_Milestone__c>();
        
        map<String, MET_Milestone__c> mapTaskMS = new map<String, MET_Milestone__c>();
        
        for(MET_Milestone__c ms : lstTaskMS)
        {
            String Key = '';
            if(String.isNotBlank(ms.Name) && String.isNotBlank(ms.Case_Type__c) && String.isNotBlank(ms.Case_Subtype__c) && ms.Task__c != null && ms.Action__c != null && ms.Source_Name__c != null)
            {
                key = ms.Case_Type__c + ms.Case_Subtype__c + ms.Task__c + ms.Action__c + ms.Source_Name__c;
                mapTaskMS.put(key, ms);
            }                   
        }
        
        for(Task tRec : lstTasks)
        {            
            if(sPrevSubtype.equals(tRec.MET_Task__r.Case_Subtype__c))
            {                     
                String sPrevKey = caseRec.Type + sPrevSubtype + tRec.MET_Task__c + tRec.MET_Action__c + tRec.MET_Source__c;
                if(mapTaskMS.containskey(sPrevKey))
                {
                    prevSubtypeMS.add(mapTaskMS.get(sPrevKey));
                }                       
            }
            else
            {                     
                String snewKey = caseRec.Type + caseRec.Subtype__c + tRec.MET_Task__c + tRec.MET_Action__c + tRec.MET_Source__c;
                if(mapTaskMS.containskey(snewKey))
                {
                    newSubtypeMS.add(mapTaskMS.get(snewKey));
                }
            }                   
        }
        
        /* Get Milestones for the Case Subtype Change*/             
        caseMS =  CaseMilestoneTrackService_C_HUM.getUICaseMilestone(caseRec, sPrevSubtype, prevSubtypeMS);
                                        
        set<MET_Milestone__c> finalMSList = new set<MET_Milestone__c>();
                        
        /* Add Milestones for the Tasks Created for Previous Subtypes*/
        if(!prevSubtypeMS.isEmpty())
        {                   
            finalMSList.addAll(prevSubtypeMS);
        }
        
        /* Add the Subtype change Case Milestones*/
        if(!caseMS.isEmpty())
        {
            finalMSList.addAll(caseMS);
        }
                        
        /* Add Milestones for the Tasks Created for New Subtypes*/              
        if(!newSubtypeMS.isEmpty())
        {
            finalMSList.addAll(newSubtypeMS);
        }
        if(!finalMSList.isEmpty())
        {                
            mapCaseMilestones.put(caseRec.Id, finalMSList);
            System.enqueueJob(new TrackService_Q_HUM(mapCaseMilestones));
        }            
    }
    /*
     * Method Name   :    getVerifyAccountType
     * Description   :    Send the boolean value to display the ButtonSection on vf page 
     * Return Type   :    boolean
     * Parameters    :    NA
     */
     public Boolean getVerifyAccountType()
     {
         try
            {
                 List<Account> accnts = [Select id,Name,RecordType.Name from Account where id =: AccountId  limit 1 ];
                 
                 if(!(accnts.isEmpty()))
                 {
                    for(Account accnt : accnts)
                        {
                            if(accnt.RecordType.Name==CaseEditConstants_HUM.INTERACTINGWITHTYPE_MEMBER ||accnt.RecordType.Name==CaseEditConstants_HUM.CASEEDIT_UNKNOWN_MEMBER_HUM ||accnt.RecordType.Name==CaseEditConstants_HUM.MEMBERCAREGIVER)
                                {
                                 isDisplay = true;
                                }
                                else
                                {
                                isDisplay = false;
                                }
                        }
                 }
           }
         catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'CaseEdit_CE_HUM', 'getVerifyAccountType');
        }
         return  isDisplay;
     }


	 /*
    * This method Is to update classification and intents for Enrolmment Welcome Call process
    * @param  NA
    * @return void
    */   
    public void updateCaseAfterEnrollmentWelcomeCall()
    {        
        String processVal =  Apexpages.currentPage().getParameters().get('redirectPage');                           
            List<CTCI_Junction__c> lstCTCIs = [SELECT Id, Name, Classification__r.name, Classification_Type__r.Name, Intent__r.Name, Classification__c, Classification_Type__c, Intent__c, IsActive__c FROM CTCI_Junction__c where Classification__r.name=: CaseEditConstants_HUM.Case_Classification_Communication and Intent__r.Name=: CaseEditConstants_HUM.Case_intent_Welcome_Call and Classification_Type__r.Name=:CaseRec.Classification_Type__c and IsActive__c=TRUE];
            if(lstCTCIs != Null && !lstCTCIs.isEmpty())
            {
                    CTCI_Junction__c classiObj = lstCTCIs[0];
                    sClassification = classiObj.Classification__c; 
                    sIntent = classiObj.Intent__c;
                    ExpClassificationId = classiObj.Classification__c;  
                    ExpIntentId =classiObj.Intent__c;                                 
            }           
        }
       

    /*
    * This method Is to update classification and intents for medicare appeal process
    * @param  NA
    * @return void
    */   
    public void updateCaseAfterExp()
    {
        bTrsansferBtnDisabled = true;
        String processVal =  Apexpages.currentPage().getParameters().get('redirectPage');
        bHasCTCIExp = false;       
        if(HUMUtilityHelper.isCRMFunctionalityON('1393270'))
        {
            case ofetchCurrentPriority=[select priority,topic__c from case where Id=: CaseRecord.id];
            List<CTCI_Junction__c> lstCTCIs = [SELECT Id, Name, Classification__r.name, Classification_Type__r.Name, Intent__r.Name, Classification__c, Classification_Type__c, Intent__c, IsActive__c FROM CTCI_Junction__c where Classification__r.name=: CaseEditConstants_HUM.sExpCalssification and Intent__r.Name=: CaseEditConstants_HUM.sExpIntent and Classification_Type__r.Name=:CaseRec.Classification_Type__c and IsActive__c=TRUE];
            if(lstCTCIs != Null && !lstCTCIs.isEmpty())
            {
                    CTCI_Junction__c classiObj = lstCTCIs[0];
                    sClassification = classiObj.Classification__c; 
                    sIntent = classiObj.Intent__c; 
                    ExpClassificationId = classiObj.Classification__c;  
                    ExpIntentId =classiObj.Intent__c;
            
            }
            caseRec.status = 'In Progress';
            sAutoRouteStatus = 'Evaluate Expedited Appeal'; 
            if(ofetchCurrentPriority.topic__c != NULL)
           caseRec.topic__c = ofetchCurrentPriority.topic__c; 
           attachedTemplateName = '95C90830-3133';
        }
        else
            {
        if(lstWQNames.Contains(CaseRec.Owner_Queue__c) && CaseRec.Work_Queue_View_Name__c == CaseEditConstants_HUM.HUM_ALLOTHERVIEW)
        {   
            case ofetchCurrentPriority=[select priority,topic__c from case where Id=: CaseRecord.id];
            if(CaseRec.Classification_Type__c == CaseEditConstants_HUM.CASE_CLASSIFICATION_TYPE_CALLRSO){
                List<CTCI_Junction__c> lstCTCIs = [SELECT Id, Name, Classification__r.name, Classification_Type__r.Name, Intent__r.Name, Classification__c, Classification_Type__c, Intent__c, IsActive__c FROM CTCI_Junction__c where Classification__r.name=: CaseEditConstants_HUM.sExpCalssification and Intent__r.Name=: CaseEditConstants_HUM.sExpIntent and Classification_Type__r.Name=: CaseEditConstants_HUM.CASE_CLASSIFICATION_TYPE_CALLRSO and IsActive__c=TRUE];                
                if(lstCTCIs != Null && !lstCTCIs.isEmpty()) bHasCTCIExp = true;
                if(bHasCTCIExp == true)
                {
                    CTCI_Junction__c classiObj = lstCTCIs[0];
                    sClassification = classiObj.Classification__c; 
                    sIntent = classiObj.Intent__c; 
                    ExpClassificationId = classiObj.Classification__c;  
                    ExpIntentId =classiObj.Intent__c; 
                }
            }
            caseRec.status = 'In Progress';
            sAutoRouteStatus = 'Evaluate Expedited Appeal'; 
            if(ofetchCurrentPriority.topic__c != NULL)
            caseRec.topic__c = ofetchCurrentPriority.topic__c; 
            attachedTemplateName = '95C90830-3133';          
         }
       } 
        
        isMedExpProcessAttachedExp();
        enableDisableSaveAndTransferButton();
    }
   
    /*
    * This method Is to update classification and intents for Part D process
    * @param  NA
    * @return void
    */
    public void updateCaseAfterPart()
    {
        bTrsansferBtnDisabled = true;
        String processVal =  Apexpages.currentPage().getParameters().get('redirectPage');
        case ofetchCurrentPriority=[select priority,topic__c from case where Id=: CaseRecord.id];
        bHasCTCIPart = false;
        if(HUMUtilityHelper.isCRMFunctionalityON('1393270'))
        {
             List<CTCI_Junction__c> lstCTCIs = [SELECT Id, Name, Classification__r.name, Classification_Type__r.Name, Intent__r.Name, Classification__c, Classification_Type__c, Intent__c, IsActive__c FROM CTCI_Junction__c where Classification__r.name=: CaseEditConstants_HUM.sPartDCalssification and Intent__r.Name=: CaseEditConstants_HUM.sPartDIntent and Classification_Type__r.Name=:CaseRec.Classification_Type__c and IsActive__c=TRUE];
                bHasCTCIPart = false;
                if(lstCTCIs != Null && !lstCTCIs.isEmpty())
                { 
                    bHasCTCIPart = true;
                }
                if(bHasCTCIPart == true)
                {
                    CTCI_Junction__c classiObj = lstCTCIs[0];
                    sClassification = classiObj.Classification__c; 
                    sIntent = classiObj.Intent__c; 
                    ExpClassificationId = classiObj.Classification__c;  
                    ExpIntentId =classiObj.Intent__c; 
                }
            caseRec.status = 'In Progress';
            sAutoRouteStatus = 'Evaluate Part D';
            if(ofetchCurrentPriority.topic__c != NULL)
            caseRec.topic__c = ofetchCurrentPriority.topic__c;  
            attachedTemplateName = 'BC929CD6-E06A';
            if(ofetchCurrentPriority.priority == 'Critical' && HUMUtilityHelper.isCRMFunctionalityON('4011657')){
                if(Schema.sObjectType.Case.fields.Priority.isUpdateable()) caseRec.Priority = 'Critical';    
            }           
        }
        else
        {
        if(lstWQNames.Contains(CaseRec.Owner_Queue__c) && CaseRec.Work_Queue_View_Name__c == CaseEditConstants_HUM.HUM_ALLOTHERVIEW)
        {           
            if(CaseRec.Classification_Type__c == CaseEditConstants_HUM.CASE_CLASSIFICATION_TYPE_CALLRSO){    
                List<CTCI_Junction__c> lstCTCIs = [SELECT Id, Name, Classification__r.name, Classification_Type__r.Name, Intent__r.Name, Classification__c, Classification_Type__c, Intent__c, IsActive__c FROM CTCI_Junction__c where Classification__r.name=: CaseEditConstants_HUM.sPartDCalssification and Intent__r.Name=: CaseEditConstants_HUM.sPartDIntent and Classification_Type__r.Name=: CaseEditConstants_HUM.CASE_CLASSIFICATION_TYPE_CALLRSO and IsActive__c=TRUE];             
                if(lstCTCIs != Null && !lstCTCIs.isEmpty())
                { 
                    bHasCTCIPart = true;
                }
                if(bHasCTCIPart == true)
                {
                    CTCI_Junction__c classiObj = lstCTCIs[0];
                    sClassification = classiObj.Classification__c; 
                    sIntent = classiObj.Intent__c; 
                    ExpClassificationId = classiObj.Classification__c;  
                    ExpIntentId =classiObj.Intent__c; 
                }
            }
            caseRec.status = 'In Progress';
            sAutoRouteStatus = 'Evaluate Part D';
            caseRec.Priority = ofetchCurrentPriority.priority;                 
        }
        if(ofetchCurrentPriority.topic__c != NULL)
        caseRec.topic__c = ofetchCurrentPriority.topic__c;  
        attachedTemplateName = 'BC929CD6-E06A';
       }                
        isMedExpProcessAttachedPartD();  
        enableDisableSaveAndTransferButton();   
    }    
    /*
    * This method Is to update classification and intents for medicare appeal process
    * @param  NA
    * @return void
    */   
    public void updateCaseAfterGoodCause()
    {   
        List <Template_Submission_Data__c> flowValue = [SELECT Value__c, Template_Field__r.Name FROM Template_Submission_Data__c 
                                                        WHERE Template_Submission__r.Template__r.Unique_Template_Name__c  =: CaseEditConstants_HUM.GOODCAUSEUNIQUEID  
                                                        AND Template_Submission__r.Submission_Owner__r.Object_Owner_ID__c =:caseRec.Id AND Template_Field__r.Name =: CaseEditConstants_HUM.GCRTERMEDPLAN LIMIT 1]; 
        if(!flowValue.isEmpty() &&  flowValue[0].Value__c == 'Yes'){    
            if(CaseEditConstants_HUM.GoodCauseAutoRouteClassificationTypes.contains(CaseRec.Classification_Type__c) && CaseRec.AutoRoute_Status__c != 'Completed')
            { 
                List<CTCI_Junction__c> lstCTTIRecords = [SELECT Id, Classification__c, Classification_Type__c, Intent__c  FROM CTCI_Junction__c WHERE Classification__r.name='Billing' AND Intent__r.Name='Reinstatement Review' AND Classification_Type__r.Name=:CaseEditConstants_HUM.GoodCauseAutoRouteClassificationTypes]; 
                if(lstCTTIRecords != Null && !lstCTTIRecords.isEmpty())
                {
                    sClassification = lstCTTIRecords[0].Classification__c; 
                    sIntent = lstCTTIRecords[0].Intent__c; 
                    ExpClassificationId = lstCTTIRecords[0].Classification__c;  
                    ExpIntentId =lstCTTIRecords[0].Intent__c; 
                }
                If(schema.SobjectType.case.fields.Status.isUpdateable()) caseRec.status = 'In Progress';
                List <Template_Submission_Data__c> flowMedicareValue = [SELECT Value__c, Template_Field__r.Name FROM Template_Submission_Data__c 
                                                                        WHERE Template_Submission__r.Template__r.Unique_Template_Name__c  =:CaseEditConstants_HUM.GOODCAUSEUNIQUEID 
                                                                        AND Template_Submission__r.Submission_Owner__r.Object_Owner_ID__c =:caseRec.Id AND Template_Field__r.Name='GCRIsMedicationNeeded' LIMIT 1];
                if(flowMedicareValue != Null && !flowMedicareValue.isEmpty() &&  flowMedicareValue[0].Value__c == 'Yes'){
                    sAutoRouteStatus = CaseEditConstants_HUM.AUTOROUTING_GOODCAUSE;
                    if(Schema.sObjectType.Case.fields.Topic__c.isUpdateable()) caseRec.topic__c = CaseEditConstants_HUM.AUTOROUTING_GOODCAUSE;
                }   
                else if(flowMedicareValue != Null && !flowMedicareValue.isEmpty() &&  flowMedicareValue[0].Value__c == 'No'){
                    sAutoRouteStatus = CaseEditConstants_HUM.AUTOROUTING_GOODCAUSETIE;
			        if(schema.SobjectType.case.fields.Topic__c.isUpdateable())
                    caseRec.topic__c = 'Termed in Error (TIE)';
                }   
                attachedTemplateName = CaseEditConstants_HUM.GOODCAUSEUNIQUEID;
            }
        }    
        isMedExpProcessAttachedGoodCause();
        enableDisableSaveAndTransferButton();
    }  
    /*
    * This method Is to update Autoroute status for PCP/PCP questions process when service fund error is occured
    * @param  NA
    * @return void
    */   
    public void updateCaseAfterPCP()
    {   
        List <Template_Submission_Data__c> flowValue = [SELECT Value__c, Template_Field__r.Name FROM Template_Submission_Data__c WHERE Template_Submission__r.Template__r.Unique_Template_Name__c  =: CaseEditConstants_HUM.PCPTEMPLATEUNIQUEID AND Template_Submission__r.Submission_Owner__r.Object_Owner_ID__c =:caseRec.Id AND Template_Field__r.Name =:CaseEditConstants_HUM.SERVICEFUNDCHECK LIMIT 1];
        if(flowValue != null && !flowValue.isEmpty() && flowValue[0].Value__c == 'Yes'){    
            if(CaseRec.AutoRoute_Status__c != 'Completed')
            {   
                if(schema.SobjectType.case.fields.status.isUpdateable()) caseRec.status = 'In Progress';
                sAutoRouteStatus = CaseEditConstants_HUM.AUTOROUTING_PCP;
                attachedTemplateName = CaseEditConstants_HUM.PCPTEMPLATEUNIQUEID;
            }
        }    
        isMedExpProcessAttachedPCP();
        enableDisableSaveAndTransferButton();
    }  
    /*
    * This method Is to disable fields and buttons for medicare appeal process
    * @param  NA
    * @return void
    */
    public void isMedExpProcessAttachedExp()
    {
        List<GroupMember> lstGrpMem = [select Group.Name,group.type from GroupMember where UserOrGroupId = :UserInfo.getUserId()   and group.type='Regular' and Group.Name in ('LV GA and Correspondence Screening PGQ','LV GA and Correspondence Screening E PGV')];
        if(lstGrpMem.size() == 2)
        {
            bQueueUser = false;
        }
        else
        {
            bQueueUser = true;
        }
        boolean bFlag = false;
        if(sAutoRouteStatus == 'Evaluate Expedited Appeal' || (caseRec.AutoRoute_Status__c == 'Completed' && caseRec.Owner_Queue__c == 'LV G and A and Correspondence Screening' )){
            bFlag = true;
        }
         

        
        boolean bMedExpAppeal = (([SELECT Value__c FROM Template_Submission_Data__c WHERE Template_Submission__r.Template__r.Unique_Template_Name__c = '95C90830-3133' AND Template_Submission__r.Submission_Owner__r.Object_Owner_ID__c = :caseRec.Id]).size()>0  && bQueueUser && caseRec.status != 'Closed' && bFlag) ? true : false;
        if(bMedExpAppeal) {
            bDisableCaseStatus = true;
            bDisableCancelCaseBtn = true;
            bDisableCloseCaseBtn = true;

            if(HUMUtilityHelper.isCRMFunctionalityON('1393270'))
            {
                bHasCTCIExpRef = (([select id from case where  Classification_Type__c =: CaseRec.Classification_Type__c and Classification_Id__r.Name =: CaseEditConstants_HUM.sExpCalssification and Intent_Id__r.Name =: CaseEditConstants_HUM.sExpIntent  and id=:caseRec.id]).size()>0 ) ? true : false;  
            }
            else
            {
                bHasCTCIExpRef = (([select id from case where  Classification_Type__c =: CaseEditConstants_HUM.CASE_CLASSIFICATION_TYPE_CALLRSO and id=:caseRec.id]).size()>0 ) ? true : false; 
            }

        }
        
        if(Label.Case_Transfer_Service_Flag == '1')
        {
            isMedProcessAttachedValue  = (([SELECT Value__c FROM Template_Submission_Data__c WHERE Template_Submission__r.Template__r.Unique_Template_Name__c  = '95C90830-3133'  AND Template_Submission__r.Submission_Owner__r.Object_Owner_ID__c = :caseRec.Id]).size()>0) ? true : false;
        }
    }  
    
    /*
    * This method Is to disable fields and buttons for Part D process
    * @param  NA
    * @return void
    */
    public void isMedExpProcessAttachedPartD()
    {       
                List<GroupMember> lstGrpMem = [select Group.Name,group.type from GroupMember where UserOrGroupId = :UserInfo.getUserId()   and group.type='Regular' and Group.Name in ('LV GA and Correspondence Screening PGQ', 'LV GA and Correspondence Screening P PGV')];
        if(lstGrpMem.size() == 2)
        {
            bQueueUser = false;
        }
        else
        {
            bQueueUser = true;
        }
        boolean bFlag = false;
        if(sAutoRouteStatus == 'Evaluate Part D' || (caseRec.AutoRoute_Status__c == 'Completed' && caseRec.Owner_Queue__c == 'LV G and A and Correspondence Screening' )){
            bFlag = true;
        }
         
        
        boolean bMedicareProcessPartD = (([SELECT Value__c FROM Template_Submission_Data__c WHERE Template_Submission__r.Template__r.Unique_Template_Name__c =  'BC929CD6-E06A' AND Template_Submission__r.Submission_Owner__r.Object_Owner_ID__c = :caseRec.Id]).size()>0 && bQueueUser  && caseRec.status != 'Closed' && bFlag) ? true : false; 

        if(bMedicareProcessPartD) {

            bDisableCaseStatus = true;
            bDisableCancelCaseBtn = true;
            bDisableCloseCaseBtn = true;

            if(HUMUtilityHelper.isCRMFunctionalityON('1393270'))
            {
                bHasCTCIExpRef = (([select id from case where  Classification_Type__c = :caseRec.Classification_Type__c and Classification_Id__r.Name =: CaseEditConstants_HUM.sPartDCalssification and Intent_Id__r.Name =: CaseEditConstants_HUM.sPartDIntent  and id=:caseRec.id]).size()>0 ) ? true : false;
            }
            else 
            {
                bHasCTCIExpRef = (([select id from case where  Classification_Type__c =: CaseEditConstants_HUM.CASE_CLASSIFICATION_TYPE_CALLRSO and id=:caseRec.id]).size()>0 ) ? true : false;
            }
        }
    }
    /*
    * This method Is to disable fields and buttons for medicare Good Cause process
    * @param  NA
    * @return void
    */
    public void isMedExpProcessAttachedGoodCause()
    { 
        List<GroupMember> lstGrpMem = [select Group.Name,group.type from GroupMember where UserOrGroupId = :UserInfo.getUserId() and group.type='Regular' and Group.Name =: CaseEditConstants_HUM.GOODCAUSEGROUPNAME];
        if(lstGrpMem.size() == 1)
        {
            bQueueUser = false;
        }
        else
        {
            bQueueUser = true;
        }
        boolean bFlag = false;
        if((sAutoRouteStatus == CaseEditConstants_HUM.AUTOROUTING_GOODCAUSE || sAutoRouteStatus == CaseEditConstants_HUM.AUTOROUTING_GOODCAUSETIE)|| (caseRec.AutoRoute_Status__c == 'Completed' && caseRec.Owner_Queue__c == CaseEditConstants_HUM.GOODCAUSEQUEUE )){
            bFlag = true;
        }
        List <Template_Submission_Data__c> flowValue = [SELECT Value__c, Template_Field__r.Name FROM Template_Submission_Data__c WHERE Template_Submission__r.Template__r.Unique_Template_Name__c  =:CaseEditConstants_HUM.GOODCAUSEUNIQUEID AND Template_Submission__r.Submission_Owner__r.Object_Owner_ID__c =:caseRec.Id AND Template_Field__r.Name=: CaseEditConstants_HUM.GCRTERMEDPLAN LIMIT 1];
            
            boolean bGoodCause = (flowValue != Null && !flowValue.isEmpty() && bQueueUser && caseRec.status != 'Closed' && bFlag); 
            
            if(bGoodCause) {
                bDisableCaseStatus = true;
                bDisableHPClosedCaseBtn = true;
                bDisableCancelCaseBtn = true;
                bDisableCloseCaseBtn = true;

                if(flowValue != Null && !flowValue.isEmpty() &&  flowValue[0].Value__c == 'Yes'){
                    bHasCTCIExpRef = (CaseEditConstants_HUM.GoodCauseAutoRouteClassificationTypes.contains(CaseRec.Classification_Type__c));
               
            }
            
            
            isGoodCauseMedProcessAttachedValue  = true;              
        }
    }
    /*
    * This method Is to disable fields and buttons for PCP process
    * @param  NA
    * @return void
    */
    public void isMedExpProcessAttachedPCP()
    { 
        List<GroupMember> lstGrpMem = [select Group.Name,group.type from GroupMember where UserOrGroupId = :UserInfo.getUserId() and group.type='Regular' and Group.Name =: CaseEditConstants_HUM.PCPGROUPNAME];
        if(lstGrpMem.size() == 1)
        {
            bQueueUser = false;
        }
        else
        {
            bQueueUser = true;
        }
        boolean bFlag = false;
        if(sAutoRouteStatus == CaseEditConstants_HUM.AUTOROUTING_PCP){
            bFlag = true;
        }
        List <Template_Submission_Data__c> flowValue = [SELECT Value__c, Template_Field__r.Name FROM Template_Submission_Data__c WHERE Template_Submission__r.Template__r.Unique_Template_Name__c  =: CaseEditConstants_HUM.PCPTEMPLATEUNIQUEID AND Template_Submission__r.Submission_Owner__r.Object_Owner_ID__c =:caseRec.Id AND Template_Field__r.Name =:CaseEditConstants_HUM.SERVICEFUNDCHECK LIMIT 1];
        if(flowValue != Null && !flowValue.isEmpty() && flowValue[0].Value__c == 'Yes'){
            
            
            boolean bPCPPCD = (bQueueUser && caseRec.status != 'Closed' && bFlag); 

            if(bPCPPCD) {
                bDisableCaseStatus = true;
                bDisableHPClosedCaseBtn = true;
                bDisableCancelCaseBtn = true;
                bDisableCloseCaseBtn = true;
            }


            isPCPProcessAttachedValue = true;
        }
        
    }
            /*
    * This method is toset the NpiID/taxId
    * @param  NA
    * @return void
    */
     public pageReference populateNPITAX(){
       caserecord.NPI_ID__c = String.isNotBlank(npiId) ? npiId : '';
      caserecord.TAX_ID__c = String.isNotBlank(taxId) ? taxId : '';
     return null;
     }


    /*
    * This method is to retrun the taxId
    * @param  NA
    * @return void
    */
    @RemoteAction
    public static String getTaxId(String Accid){    
     List<ConsumerID__c> lstConsumerId = [select Consumer_ID__c from ConsumerID__c where Account__r.id =: Accid];     
     String taxId = ''; 
     if(lstConsumerId.size()>0)
         taxId = lstConsumerId[0].Consumer_ID__c;
     return taxId;
    }
     /*
    * This method is to clear NPI and TAX ID
    * @param  NA
    * @return void
    */ 
     public void clearNPITAX()
     {
       caserecord.NPI_ID__c = '';
       caserecord.TAX_ID__c = '';
     }
               
    /*
    * This method Is to evaluate the TRR T/A/S for BAM tracking
    * @param  resLst(list of succesfully inserted TaskIDs) and taskList(list of all the Tasks added)
    * @return void
    */
    @TestVisible
    private void evaluatetasksforTRRTracking(list<ID> resLst, list<task> taskList)
    {        
        set<ID> setTaskID = new set<ID>();
        map<ID, task> mapTasks = new map<ID, task>();
        map<ID, MET_Milestone__c> mapTaskMS = new map<ID, MET_Milestone__c>();
        set<ID> setMETTaskIDs = new set<ID>();
        set<ID> setMETActionIDs = new set<ID>();
        set<ID> setMETSourceIDs = new set<ID>();        
        map<string, MET_Milestone__c> mapMilestones = new map<string, MET_Milestone__c>();
                          
        if(resLst!=null && !resLst.isEmpty() && taskList!=null && !taskList.isEmpty())
        {
            setTaskID.addAll(resLst);
            for(task taskRec : taskList)
            {
                if(setTaskID.contains(taskRec.ID) && taskRec.MET_Task__c!=null && taskRec.MET_Action__c!=null && taskRec.MET_Source__c!=null)
                {
                    mapTasks.put(taskRec.ID, taskRec);
                    setMETTaskIDs.add(taskRec.MET_Task__c);
                    setMETActionIDs.add(taskRec.MET_Action__c);
                    setMETSourceIDs.add(taskRec.MET_Source__c);
                }
            }            
            if(mapTasks!=null && !mapTasks.isEmpty() && setMETTaskIDs!=null && !setMETTaskIDs.isEmpty() 
                && setMETActionIDs!=null && !setMETActionIDs.isEmpty() && setMETSourceIDs!=null && !setMETSourceIDs.isEmpty())
            {
                list<MET_Milestone__c> lstMilestones = [SELECT ID,Name,Case_Subtype__c, Task__c, Action__c, Source_Name__c,Task__r.Name, Action__r.Name, Source_Name__r.Name FROM MET_Milestone__c 
                                                    WHERE Case_Type__c = :caseRec.Type and Case_Subtype__c =:caseRec.SubType__c and Task__c IN :setMETTaskIDs and Source_Name__c IN :setMETSourceIDs and Action__c IN :setMETActionIDs and Is_Active__c = true and Is_Milestone__c = true];
                if(lstMilestones!=null && !lstMilestones.isEmpty())
                {
                    for(MET_Milestone__c milestone : lstMilestones)
                    {
                        mapMilestones.put(String.valueOf(milestone.Task__c)+String.valueOf(milestone.Action__c)+String.valueOf(milestone.Source_Name__c), milestone);
                    }                                        
                    for(task taskRec : mapTasks.values())
                    {
                        string sKey = String.valueOf(taskRec.MET_Task__c) + String.valueOf(taskRec.MET_Action__c) + String.valueOf(taskRec.MET_Source__c);
                        
                        if(mapMilestones.containsKey(sKey))
                        {
                            mapTaskMS.put(taskRec.ID, mapMilestones.get(sKey));
                        }
                    }
                    if(mapTaskMS!=null && !mapTaskMS.isEmpty())
                    {
                        callTrackServiceForTRR(mapTaskMS);
                    }                
                }                                                                   
            }
        }   
    }
    
    /*
    * This method is to call the BAM Track service 
    * @param  mapTaskMS(map of TaskID and its corresponding MET Milestone)
    * @return void
    */
    @TestVisible
    private void callTrackServiceForTRR(map<ID, MET_Milestone__c> mapTaskMS)
    {
        string sUISeriesSrcID;
        string sRejSrcID;
        list<Enrollment_Track_History__c> lstHistForInsert = new list<Enrollment_Track_History__c>();
        map<string, map<string, set<Enrollment_Track_History__c>>> mapCaseEnrollHistory = new map<string, map<string, set<Enrollment_Track_History__c>>>();
        map<string, list<Enrollment_Track_History__c>> mapTrackHistory = new map<string, list<Enrollment_Track_History__c>>();
        if(mapTaskMS!=null && !mapTaskMS.isEmpty())
        {            
            list<Enrollment_Track_History__c> lstTrackHistory = [SELECT Id, Name, Case__c, Recipient_App__c, Source_App__c, Recipient_Track_ID__c, Source_Track_Id__c, Member_ID__c, Status__c, Milestone_Id__c, Milestone_MET_Id__c, createddate 
                                                                  FROM Enrollment_Track_History__c WHERE Case__c = :caseRec.ID AND Milestone_MET_Id__c = null AND Source_Track_Id__c!=null];            
            if(lstTrackHistory!=null && !lstTrackHistory.isEmpty())
            {
                set<string> setSTOPMS = new set<string>{CaseEditConstants_HUM.TRR_REJ_STOP_MS, CaseEditConstants_HUM.TRR_UI_SERIES_STOP_MS};
                for(Enrollment_Track_History__c trackHistory : lstTrackHistory)
                {
                    if(mapTrackHistory.containsKey(trackHistory.Source_Track_Id__c))
                    {
                        mapTrackHistory.get(trackHistory.Source_Track_Id__c).add(trackHistory);
                    }
                    else
                    {
                        mapTrackHistory.put(trackHistory.Source_Track_Id__c, new list<Enrollment_Track_History__c>{trackHistory});
                    }
                }               
                for(string sSourceID : mapTrackHistory.keySet())
                {
                    list<Enrollment_Track_History__c> lstEnrollHist = mapTrackHistory.get(sSourceID);
                    if(lstEnrollHist!=null && !lstEnrollHist.isEmpty())
                    {
                        if(!setSTOPMS.contains(lstEnrollHist[lstEnrollHist.size()-1].Milestone_Id__c))
                        {
                            if(lstEnrollHist[lstEnrollHist.size()-1].Milestone_Id__c.startsWith('18'))  sUISeriesSrcID = lstEnrollHist[lstEnrollHist.size()-1].Source_Track_Id__c;
                            else if(lstEnrollHist[lstEnrollHist.size()-1].Milestone_Id__c.startsWith('19')) sRejSrcID = lstEnrollHist[lstEnrollHist.size()-1].Source_Track_Id__c; 
                        }
                    }
                }
                
                Enrollment_Track_History__c TrackHisRec;
                if(String.isNotBlank(sUISeriesSrcID) || String.isNotBlank(sRejSrcID))
                {
                    for(MET_Milestone__c milestone : mapTaskMS.values())
                    {                                
                        if(milestone.Name.startsWith(CaseEditConstants_HUM.TRR_REJ_MS_PATTERN) && String.isNotBlank(sRejSrcID))
                        {
                            lstHistForInsert.add(getNewTrackHistory(caseRec.ID, milestone.Name, sRejSrcID, milestone.ID));
                        }
                        else if(milestone.Name.startsWith(CaseEditConstants_HUM.TRR_UI700_MS_PATTERN) && String.isNotBlank(sUISeriesSrcID))
                        {
                            lstHistForInsert.add(getNewTrackHistory(caseRec.ID, milestone.Name, sUISeriesSrcID, milestone.ID));
                        }
                        else if(milestone.Name.equals(CaseEditConstants_HUM.CRM_STOP_MILESTONE))
                        {
                            if(String.isNotBlank(sRejSrcID))
                                lstHistForInsert.add(getNewTrackHistory(caseRec.ID, milestone.Name, sRejSrcID, milestone.ID));
                            else if(String.isNotBlank(sUISeriesSrcID))
                                lstHistForInsert.add(getNewTrackHistory(caseRec.ID, milestone.Name, sUISeriesSrcID, milestone.ID));
                        }
                    }
                    if(lstHistForInsert!=null && !lstHistForInsert.isEmpty())
                    {
                        insert lstHistForInsert;                        
                        map<string, set<Enrollment_Track_History__c>> mapHistBySrcID = new map<string, set<Enrollment_Track_History__c>>();                         
                        for(Enrollment_Track_History__c histRec : lstHistForInsert)
                        {
                            if(mapHistBySrcID.containsKey(histRec.Source_Track_Id__c))
                            {
                                mapHistBySrcID.get(histRec.Source_Track_Id__c).add(histRec);    
                            }
                            else
                            {
                                mapHistBySrcID.put(histRec.Source_Track_Id__c, new set<Enrollment_Track_History__c>{histRec});
                            }
                        }                       
                        if(mapHistBySrcID!=null && !mapHistBySrcID.isEmpty())
                        {
                            mapCaseEnrollHistory.put(caseRec.ID, mapHistBySrcID);   
                            system.enqueueJob(new TrackService_Q_HUM(mapCaseEnrollHistory));
                        }
                    }    
                }                                                
            }                                       
        }
    }
    
    /*
    * This method is to create the new Enrollment track History record instance 
    * @param  sCaseID(Case ID), sMstoneName(milestone to track), sSrcID(source track ID to track against) and sMET_ID(MET_Milestone ID for tracking)
    * @return Enrollment_Track_History__c instance
    */
    @TestVisible
    private Enrollment_Track_History__c getNewTrackHistory(string sCaseID, string sMstoneName, string sSrcID, string sMET_ID)
    {
        Enrollment_Track_History__c TrackHisRec = new Enrollment_Track_History__c();        
         if(schema.SobjectType.Enrollment_Track_History__c.fields.Milestone_Id__c.isCreateable())
        TrackHisRec.Milestone_Id__c = sMstoneName;
         if(schema.SobjectType.Enrollment_Track_History__c.fields.Source_App__c.isCreateable())
        TrackHisRec.Source_App__c = 'CRM';
         if(schema.SobjectType.Enrollment_Track_History__c.fields.Recipient_App__c.isCreateable())
        TrackHisRec.Recipient_App__c = 'CRM';
         if(schema.SobjectType.Enrollment_Track_History__c.fields.Status__c.isCreateable())
        TrackHisRec.Status__c = 'Pending';
         if(schema.SobjectType.Enrollment_Track_History__c.fields.Case__c.isCreateable())
        TrackHisRec.Case__c = sCaseID;
         if(schema.SobjectType.Enrollment_Track_History__c.fields.Source_Track_Id__c.isCreateable())
        TrackHisRec.Source_Track_Id__c = sSrcID;
         if(schema.SobjectType.Enrollment_Track_History__c.fields.Milestone_MET_Id__c.isCreateable())
        TrackHisRec.Milestone_MET_Id__c = sMET_ID;
        return TrackHisRec; 
    }

    /* 
     * This method is to Auto Populate the Humana Pharmacy Log Code based on Classification & Intent Values
     */
     public void setLogCode()
     {
        try{
            Set<String> setPharRecType = new Set<String>(CaseEditConstants_HUM.Pharmacy_Record_Type_Name_LogCode_Population);
            String sClassificationType;
            if(sClassification != null && sIntent != null && CaseRec.Classification_Type__c != null && setPharRecType.contains(caseRecord.RecordType.name)){
                if(lstRSOHPPermissionSet != null && !lstRSOHPPermissionSet .isEmpty())
                    sClassificationType = CaseEditConstants_HUM.CASE_CLASSIFICATION_TYPE_CALLRSO;
                else
                    sClassificationType = CaseRec.Classification_Type__c;
                String sClassificationTrim = sClassification.removeEnd(CaseEditConstants_HUM.CASEEDIT_CLASSIFICATION_SEPERATOR);
                list<CTCI_Junction__c> listOfCTCI = [SELECT id,Humana_Pharmacy_Log_Code__c,Service_Model_Type__c FROM CTCI_Junction__c WHERE Classification__c = :sClassificationTrim AND Intent__c =:sIntent AND (Humana_Pharmacy_Log_Code__c !=null OR Service_Model_Type__c !=null) AND Classification_Type__r.name =:sClassificationType ];
                sCode = listOfCTCI.size()>0 ? listOfCTCI[0].Humana_Pharmacy_Log_Code__c:'';
                sCodeHidden = sCode;
                sServiceModelType = listOfCTCI.size()>0 ? listOfCTCI[0].Service_Model_Type__c:'';

                if (lstServiceModelTypes.Contains(sServiceModelType)) {
                    bHasHPServiceModelType = true;
                } else {
                    bHasHPServiceModelType = false;
                }

                if (caseRecord.RecordType.name == CaseEditConstants_HUM.HP_MEMBERCASE_HUM && sClassificationType == CaseEditConstants_HUM.LOGCODEHSP_CLASSIFICATIONTYPE){
                    if (sServiceModelType == CaseEditConstants_HUM.LOGCODEHSP_SERVICEMODELTYPE){
                        if (bHasLogCodeList && !(bIsPharmacyUser && bIsLogCodelistAvailable)) {
                            //Show Log Code - Log Code Currently Not Displaying - Rerender
                            bIsLogCodelistAvailable = true;
                            bSwitchCommentSection = true;
                        } else if (bHasLogCodeList && (bIsPharmacyUser && bIsLogCodelistAvailable)){
                            //Show Log Code - Log Code Currently Displaying - No Rerender
                            bIsLogCodelistAvailable = true;
                            bSwitchCommentSection = false;
                        }
                    } else if (sServiceModelType != CaseEditConstants_HUM.LOGCODEHSP_SERVICEMODELTYPE){
                        if (bIsPharmacyUser && bIsLogCodelistAvailable){
                            //Hide Log Code - Log Code Currently Displaying - Rerender
                            bIsLogCodelistAvailable = false;
                            bSwitchCommentSection = true;
                        } else if (!(bIsPharmacyUser && bIsLogCodelistAvailable)){
                            //Hide Log Code - Log Code Currently Not Displaying - No Rerender
                            bIsLogCodelistAvailable = false; 
                            bSwitchCommentSection = false;
                        }
                    }
                }           
            }
            else {
                bHasHPServiceModelType = false;
                
                if (caseRecord.RecordType.name == CaseEditConstants_HUM.HP_MEMBERCASE_HUM && CaseRec.Classification_Type__c == CaseEditConstants_HUM.LOGCODEHSP_CLASSIFICATIONTYPE && sIntent != null){
                    if (bHasLogCodeList && !(bIsPharmacyUser && bIsLogCodelistAvailable)) {
                        //Hide Log Code - Log Code Currently Not Displaying - No Rerender
                        bIsLogCodelistAvailable = false;
                        bSwitchCommentSection = false;
                    } else if (bHasLogCodeList && (bIsPharmacyUser && bIsLogCodelistAvailable)){
                        //Hide Log Code - Log Code Currently Displaying - Rerender
                        bIsLogCodelistAvailable = false;
                        bSwitchCommentSection = true;
                    }
                }
            }
        }catch(Exception e){

            HUMExceptionHelper.logErrors(e, 'CaseEDIT_CE_HUM', 'setLogCode');
        
        }
    }
    /*
    * This method is to fetch Sub Functions for the Current User
    * @param  N/A
    * @return List SelectOption
    */
     public List<SelectOption> getlstSubFunctions()
     {
        List<SelectOption> lSubFunc = new List<SelectOption>();
        try
        {
            lSubFunc = HUMUtilityHelper.getlstSubFunctions(sFunction,currentloggedInUser,CaseEditConstants_HUM.EMPTY_STRING);
            if(!String.isBlank(currentloggedInUser.Mentor_Sub_Function__c))
            {
                sSubFunctionSelected = currentloggedInUser.Mentor_Sub_Function__c;
            }
            else if(sFunction == CaseEditConstants_HUM.HUMDefaultMentorFunction)
            {
                sSubFunctionSelected = CaseEditConstants_HUM.HUMDefaultMentorSubFunction;
            }
            else
            {
                sSubFunctionSelected = CaseEditConstants_HUM.EMPTY_STRING;
            }

        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'CaseEdit_CE_HUM', 'getlstSubFunctions');
        }
        return lSubFunc;
    }
        
    /*
    * This method is to fetch Details for the Current User such as Sub function, Function
    * @param  N/A
    * @return N/A
    */
    @testvisible
    private void fetchUserDetails()
    {
        try
        {
            currentloggedInUser = HUMUtilityHelper.fetchUserDetails();
            sFunction = HUMUtilityHelper.fetchUserMentorFunction(currentloggedInUser);
            if(!String.isBlank(sFunction))
            {
                isSubFunctionVisible = true;
            }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'CaseEdit_CE_HUM', 'fetchUserDetails');
        }
    }
   
   /*
    * This method is to check Classification pre-condition to add claims numbers manually
    * @param  N/A
    * @return N/A
    */ 
    
    public void checkClassification()
    {
      try
      {
           /* To reset Intent Field for Credit Card Logging Component*/ 
          if(resetIntentCCLogging && bcreditCardlogging == 'true'){       
                caseRec.CTCI_List__c = null;
                sIntent = null;  
          }
          
            String sClassificationId;
            if(String.IsNotBlank(sClassification) && sClassification != 'None')
            {
                sClassificationId = sClassification;
            }
            
            if(String.IsNotBlank(sClassificationId))
            {

          
                    if(bProviderSwitch)
            {
                        bClaimNumberBtn = false;
                        List<CaseEdit_Add_Claim_Number_Btn__mdt> lstClassificationCombo;
                        String sClassificationName;
                        sClassificationId = sClassificationId.removeEnd(CaseEditConstants_HUM.CASEEDIT_CLASSIFICATION_SEPERATOR);
                    for (Classification__c classification : [SELECT Name FROM Classification__c where id =: sClassificationId ])
                        {
                            sClassificationName = classification.Name;
                        }
                    case CaseRecClaims=[select id, Classification_Type__c from case where Id =: caseRecord.id];
                    lstClassificationCombo = [SELECT id FROM CaseEdit_Add_Claim_Number_Btn__mdt WHERE Classification_Type__c=:caseRec.Classification_Type__c AND Classification__c=:sClassificationName];
            
                    if(lstClassificationCombo!=null && !lstClassificationCombo.isEmpty())
                        {
                            bClaimNumberBtn = true;
                        }
                    else
                        {
                            bClaimNumberBtn = false;
                        }
           }
        
        }
        resetIntentCCLogging = true;
    }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'CaseEdit_CE_HUM', 'checkClassification');
        }
    }
    /*
    * This method is to update classification, intent and disable buttons (Close Case, Save and Transfer, Cancel) for QAA processed cases.
    * @param  N/A
    * @return N/A
    */ 
    public void updateCaseQAA() {

        try{
            

            if((UtilityHelper_H_AHT_HUM.isQAAAutoRoute(caseRec)) || Test.isRunningTest()) {

                if(!(caseRec.AutoRoute_Status__c == 'Completed')) {

                    List<CTCI_Junction__c> lstCTCIs = [SELECT Id, Name, Classification__r.name, Classification_Type__r.Name, Intent__r.Name, Classification__c, Classification_Type__c, Intent__c, IsActive__c FROM CTCI_Junction__c where Classification__r.name=: CaseAutoRouteConstant_C_HUM.sQAACalssification and Intent__r.Name=: CaseAutoRouteConstant_C_HUM.sQAAIntent and Classification_Type__r.name = :caseRec.Classification_Type__c  and IsActive__c=TRUE];

                    
                    if(null!= lstCTCIs  && !lstCTCIs.isEmpty()){
                        
                        sClassification = lstCTCIs[0].Classification__c; 
                        sIntent = lstCTCIs[0].Intent__c; 
                        ExpClassificationId = lstCTCIs[0].Classification__c;  
                        ExpIntentId =lstCTCIs[0].Intent__c; 
			            bDisableCaseStatus = true;
                        caseRec.status = 'In Progress';
                        bHasCTCIExpRef = true;

                        if (null != caseRec.Policy_Member__r.Segment_Indicator__c && CaseAutoRouteConstant_C_HUM.LST_MEDICAREPLANS.contains(caseRec.Policy_Member__r.Segment_Indicator__c)) {
                            
                            sAutoRouteStatus = CaseAutoRouteConstant_C_HUM.AUTOROUTING_MEDICARE;
                    
                        } else if ((null != caseRec.Policy_Member__r.Issue_State__c && CaseAutoRouteConstant_C_HUM.LST_MCDSTATES_KYIL.contains(caseRec.Policy_Member__r.Issue_State__c))
                                && ((null != caseRec.Policy_Member__r.Segment_Indicator__c && CaseAutoRouteConstant_C_HUM.MEDICAIDPLAN == caseRec.Policy_Member__r.Segment_Indicator__c)
                                    || (null != caseRec.Policy_Member__r.Product_Type__c && CaseAutoRouteConstant_C_HUM.PRODUCT_TYPE_MCD == caseRec.Policy_Member__r.Product_Type__c))) {
                            
                            sAutoRouteStatus = CaseAutoRouteConstant_C_HUM.AUTOROUTING_KY_IL_MCD;

                        } else if ((null != caseRec.Policy_Member__r.Issue_State__c && CaseAutoRouteConstant_C_HUM.POLICY_MEMBER_ISSUE_STATE_FL == caseRec.Policy_Member__r.Issue_State__c)
                        && ((null != caseRec.Policy_Member__r.Segment_Indicator__c && CaseAutoRouteConstant_C_HUM.MEDICAIDPLAN == caseRec.Policy_Member__r.Segment_Indicator__c)
                            || (null != caseRec.Policy_Member__r.Product_Type__c && CaseAutoRouteConstant_C_HUM.PRODUCT_TYPE_MCD == caseRec.Policy_Member__r.Product_Type__c))) {
                            
                            sAutoRouteStatus = CaseAutoRouteConstant_C_HUM.AUTOROUTING_FL_MCD;
                        }
                    }
                }
           
                String pgqName ='';
                if (null != caseRec.Policy_Member__r.Segment_Indicator__c && CaseAutoRouteConstant_C_HUM.LST_MEDICAREPLANS.contains(caseRec.Policy_Member__r.Segment_Indicator__c)) {
                    pgqName=CaseAutoRouteConstant_C_HUM.QAAGroupPlanValue.get('Medicare');
                }else{
                    pgqName=CaseAutoRouteConstant_C_HUM.QAAGroupPlanValue.get(caseRec.Policy_Member__r.Issue_State__c);
                }
                bQueueUser = true;
                List<GroupMember> lstGrpMems =[Select Group.Name,group.type from GroupMember where UserOrGroupId = :UserInfo.getUserId()   and group.type='Regular' and Group.Name = :pgqName ];
                if(lstGrpMems.size() > 0){
                    bQueueUser = false;
                }
                boolean bFlag = false;
                if((sAutoRouteStatus == CaseAutoRouteConstant_C_HUM.AUTOROUTING_FL_MCD || sAutoRouteStatus == CaseAutoRouteConstant_C_HUM.AUTOROUTING_KY_IL_MCD || sAutoRouteStatus == CaseAutoRouteConstant_C_HUM.AUTOROUTING_MEDICARE  )|| (caseRec.AutoRoute_Status__c == 'Completed' && CaseAutoRouteConstant_C_HUM.QAAQueues.contains(caseRec.Owner_Queue__c))){
                    bFlag = true;
                }
                Boolean bQAA = (bQueueUser &&  bFlag)? true : false;
                if(bQAA){
                    bTrsansferBtnDisabled = true;
                    bDisableCancelCaseBtn = true;
                    bDisableCloseCaseBtn = true;
                }
            }
            
        } catch(Exception e){

            HUMExceptionHelper.logErrors(e, 'CaseEdit_CE_HUM', 'updateCaseQAA');
        }
    }


     /*
    * This method is to disable buttons (Close Case, Save and Transfer, Cancel) for Medicaid PAR Provider Auto Route and set the Classification and Intent value
    * @param  N/A
    * @return N/A
    */ 
    public void updateCaseMedicaidPARProvider() {

        try{
            List <Template_Submission_Data__c> lstStateFlowValue  = UtilityHelper_H_AHT_HUM.getTemplateData(CaseAutoRouteConstant_C_HUM.MEDICAID_PAR_PROVIDER_UNIQUEID,CaseAutoRouteConstant_C_HUM.LST_MEDICAIDPAR_FIELDS,caseRec);

            if(lstStateFlowValue != null && !lstStateFlowValue.isEmpty()){ 
                bIsMedicaidPARProcessAttached = false;
                for(Template_Submission_Data__c data : lstStateFlowValue){
                    if(data.value__c!=null){
                        bIsMedicaidPARProcessAttached = true;
                        attachedTemplateName= CaseAutoRouteConstant_C_HUM.MEDICAID_PAR_PROVIDER_UNIQUEID;
                        break;
                    }
                }
 
                if(bIsMedicaidPARProcessAttached && CaseRec.AutoRoute_Status__c != 'Completed')
                { 
                    List<CTCI_Junction__c> lstCTCIs = [SELECT Id, Name, Classification__r.name, Classification_Type__r.Name, Intent__r.Name, Classification__c, Classification_Type__c, Intent__c, IsActive__c FROM CTCI_Junction__c where Classification__r.name=: CaseAutoRouteConstant_C_HUM.sMedicaidPARProviderClassification and Intent__r.Name=: CaseAutoRouteConstant_C_HUM.sMedicaidPARProviderIntent and Classification_Type__r.name = :caseRec.Classification_Type__c  and IsActive__c=TRUE];
                    if(null!= lstCTCIs  && !lstCTCIs.isEmpty()) {    
                        
                        sClassification = lstCTCIs[0].Classification__c; 
                        sIntent = lstCTCIs[0].Intent__c; 
                        ExpClassificationId = lstCTCIs[0].Classification__c;  
                        ExpIntentId =lstCTCIs[0].Intent__c;
                        bHasCTCIExpRef = true;
                        caseRec.status = 'In Progress';                      
                        sAutoRouteStatus = CaseAutoRouteConstant_C_HUM.AUTOROUTING_MEDICAID_PAR_PROVIDER;
                        UtilityHelper_H_AHT_HUM.TemplateSubmissionData oTempSubData = UtilityHelper_H_AHT_HUM.getTemplateSubmissionWrapper(lstStateFlowValue);
                        if(oTempSubData!=null){
                            if(oTempSubData.sStateField == CaseAutoRouteConstant_C_HUM.MEDICAIDPAR_PROGROUPST_FL){
                                caseRec.topic__c = CaseAutoRouteConstant_C_HUM.MEDICAIDPAR_PROGROUPST_FLTOPIC;
                            }
                            else if(oTempSubData.sStateField == CaseAutoRouteConstant_C_HUM.MEDICAIDPAR_PROGROUPST_KY){ 
                                if(oTempSubData.sBehavioralHealthQuestion == CaseAutoRouteConstant_C_HUM.String_Yes){
                                    caseRec.topic__c = CaseAutoRouteConstant_C_HUM.MEDICAIDPAR_PROGROUPST_SCTOPIC_Behavioral_Health; 
                                }   
                                else sAutoRouteStatus = CaseAutoRouteConstant_C_HUM.AUTOROUTING_MEDICAID_PAR_PROVIDER_KY;
                            }
                            else if(oTempSubData.sStateField == CaseAutoRouteConstant_C_HUM.MEDICAIDPAR_PROGROUPST_SC){
                                if(oTempSubData.sBehavioralHealthQuestion == CaseAutoRouteConstant_C_HUM.String_Yes){
                                    if(Schema.sObjectType.Case.fields.topic__c.isUpdateable()) caseRec.topic__c = CaseAutoRouteConstant_C_HUM.MEDICAIDPAR_PROGROUPST_SCTOPIC_Behavioral_Health;
                                }
                                else caseRec.topic__c = CaseAutoRouteConstant_C_HUM.MEDICAIDPAR_PROGROUPST_SCTOPIC_South_Carolina;
                                
                            }
                            else if(oTempSubData.sStateField == CaseAutoRouteConstant_C_HUM.MEDICAIDPAR_PROGROUPST_IL){
                                caseRec.topic__c = CaseAutoRouteConstant_C_HUM.MEDICAIDPAR_PROGROUPST_ILTOPIC;
                            }
							//US-3627766
                            else if(bAutoRoutePARSwitch && oTempSubData.sStateField == CaseAutoRouteConstant_C_HUM.MEDICAIDPAR_PROGROUPST_IL_LTSS){
                                sAutoRouteStatus = CaseAutoRouteConstant_C_HUM.AUTOROUTING_MEDICAID_PAR_PROVIDER_IL;
                            }
                            else if(bAutoRoutePARSwitch && oTempSubData.sStateField == CaseAutoRouteConstant_C_HUM.MEDICAIDPAR_PROGROUPST_OH){
                                if(oTempSubData.sBehavioralHealthQuestion == CaseAutoRouteConstant_C_HUM.String_Yes){
                                    if(Schema.sObjectType.Case.fields.topic__c.isUpdateable()) caseRec.topic__c = CaseAutoRouteConstant_C_HUM.MEDICAIDPAR_PROGROUPST_SCTOPIC_Behavioral_Health;
                                }
                                else{
                                    if(Schema.sObjectType.Case.fields.topic__c.isUpdateable()) caseRec.topic__c = CaseAutoRouteConstant_C_HUM.MEDICAIDPAR_PROGROUPST_OHTOPIC;
                                }
                            }
                            else if(bAutoRoutePARSwitch && oTempSubData.sStateField == CaseAutoRouteConstant_C_HUM.MEDICAIDPAR_PROGROUPST_LA){
                                if(oTempSubData.sBehavioralHealthQuestion == CaseAutoRouteConstant_C_HUM.String_Yes){
                                    if (Schema.sObjectType.Case.fields.topic__c.isUpdateable()) caseRec.topic__c = CaseAutoRouteConstant_C_HUM.MEDICAIDPAR_PROGROUPST_SCTOPIC_Behavioral_Health;
                                }
                                else{ 
                                    if (Schema.sObjectType.Case.fields.topic__c.isUpdateable()) caseRec.topic__c = CaseAutoRouteConstant_C_HUM.MEDICAIDPAR_PROGROUPST_LATOPIC;
                                }
                            }else if(HUMUtilityHelper.isCRMFunctionalityON('5243585')){
                                if(oTempSubData.sStateField == CaseAutoRouteConstant_C_HUM.MEDICAIDPAR_PROGROUPST_OK){
                                    if(oTempSubData.sBehavioralHealthQuestion == CaseAutoRouteConstant_C_HUM.String_Yes){
                                        if (Schema.sObjectType.Case.fields.topic__c.isUpdateable()) caseRec.topic__c = CaseAutoRouteConstant_C_HUM.MEDICAIDPAR_PROGROUPST_SCTOPIC_Behavioral_Health;
                                    }else{
                                        if (Schema.sObjectType.Case.fields.topic__c.isUpdateable()) caseRec.topic__c = CaseAutoRouteConstant_C_HUM.MEDICAIDPAR_PROGROUPST_OKTOPIC;
                                    }
                                }
                            }
                        }
                    }

                }
                isMedicaidPARProviderAttached();
                enableDisableSaveAndTransferButton();
            }
        } catch(Exception e){

            HUMExceptionHelper.logErrors(e, 'CaseEdit_CE_HUM', 'updateCaseMedicaidPARProvider');
        }
    }

    /*
    * Populates Default values to Case
    * 
    * @see displays default values when Request ID Card button is clicked from ID Card Management
    */  
    public void requestIDCaseCreate(){
       try{
            sMedCallsIssueCC = 'CALLBACK NUMBER: ' + caseRec.Policy_Member__r.Member__r.PersonHomePhone + '\n\n' + Label.CaseComments_CaseEdit_HUM;
            sMedCallsResCC = Label.CaseComments2_CaseEdit_HUM;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'CaseEdit_CE_HUM', 'requestIDCaseCreate');
        }
    }
	
    public void isMedicaidPARProviderAttached(){
        try{
            String sGroupName='';
            List<Template_Submission_Data__c> lstStateFlowValue = UtilityHelper_H_AHT_HUM.getTemplateData(CaseAutoRouteConstant_C_HUM.MEDICAID_PAR_PROVIDER_UNIQUEID,CaseAutoRouteConstant_C_HUM.LST_MEDICAIDPAR_FIELDS,caseRec);
            UtilityHelper_H_AHT_HUM.TemplateSubmissionData oTempSubData = UtilityHelper_H_AHT_HUM.getTemplateSubmissionWrapper(lstStateFlowValue);
            if(oTempSubData != null){
                if(oTempSubData.sStateField == CaseAutoRouteConstant_C_HUM.MEDICAIDPAR_PROGROUPST_KY){ 
                    if(oTempSubData.sBehavioralHealthQuestion == CaseAutoRouteConstant_C_HUM.String_Yes){
                        sGroupName = CaseAutoRouteConstant_C_HUM.MEDICAID_PAR_PROVIDER_AUTOROUTE_GROUP;
                    }   
                    else sGroupName = CaseAutoRouteConstant_C_HUM.MEDICAID_PAR_PROVIDER_AUTOROUTE_GROUP_KY;
                }
				else if(oTempSubData.sStateField == CaseAutoRouteConstant_C_HUM.MEDICAIDPAR_PROGROUPST_IL_LTSS){
                    sGroupName = CaseAutoRouteConstant_C_HUM.MEDICAID_PAR_PROVIDER_AUTOROUTE_GROUP_IL;
				}
                else sGroupName = CaseAutoRouteConstant_C_HUM.MEDICAID_PAR_PROVIDER_AUTOROUTE_GROUP;
            }
            List<GroupMember> lstGrpMem = [select Group.Name,group.type from GroupMember where UserOrGroupId =: UserInfo.getUserId()   and group.type='Regular' and Group.Name =:  sGroupName];
            bQueueUser = true;
            if(!lstGrpMem.isEmpty())
            {
                bQueueUser = false;
            }
            
            boolean bFlag = false;
            if(
                (sAutoRouteStatus == CaseAutoRouteConstant_C_HUM.AUTOROUTING_MEDICAID_PAR_PROVIDER) || 
                (sAutoRouteStatus == CaseAutoRouteConstant_C_HUM.AUTOROUTING_MEDICAID_PAR_PROVIDER_KY) || 
                (sAutoRouteStatus == CaseAutoRouteConstant_C_HUM.AUTOROUTING_MEDICAID_PAR_PROVIDER_IL) ||
				(caseRec.AutoRoute_Status__c == 'Completed' &&
                    (caseRec.Owner_Queue__c == CaseAutoRouteConstant_C_HUM.MEDICAID_PAR_PROVIDER_AUTOROUTE_QUEUE || 
                    caseRec.Owner_Queue__c == CaseAutoRouteConstant_C_HUM.MEDICAID_PAR_PROVIDER_AUTOROUTE_QUEUE_KY ||
					caseRec.Owner_Queue__c == CaseAutoRouteConstant_C_HUM.MEDICAID_PAR_PROVIDER_AUTOROUTE_QUEUE_IL)
                )
            ){
                bFlag = true;
            }
        
        
            boolean bMedicaidPARProvider = (bIsMedicaidPARProcessAttached && bQueueUser  && caseRec.status != 'Closed' && bFlag) ? true : false; 
    
            if(bMedicaidPARProvider) {
    
                bDisableCaseStatus = true;
                bDisableCancelCaseBtn = true;
                bDisableCloseCaseBtn = true;
                if(bHasCTCIExpRef!=null && !bHasCTCIExpRef){ 
                    bHasCTCIExpRef = (([select id from case where  Classification_Type__c =: CaseRec.Classification_Type__c and Classification_Id__r.Name =: CaseAutoRouteConstant_C_HUM.sMedicaidPARProviderClassification and Intent_Id__r.Name =: CaseAutoRouteConstant_C_HUM.sMedicaidPARProviderIntent  and id=:caseRec.id]).size()>0 ) ? true : false; 
                }
               
            }
        } catch(Exception e){

            HUMExceptionHelper.logErrors(e, 'CaseEdit_CE_HUM', 'isMedicaidPARProviderAttached');
        }
    }
    
 }