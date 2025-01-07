/********************************************************************************************************************************************************
Apex Class Name  : GLOBAL_CONSTANT_HUM
Version          : 1.0 
Created Date     : FEB 10 2016
Test Class       : GLOBAL_CONSTANT_T_HUM
Function         : Constants declaration here that are going to be used across the org.
Modification Log :       
* Developer                       Code review              Date                 Description
* -------------------------------------------------------------------------------------------------------------------------------------------------------               
* Ranjeeth Nagishetty              29646                  02/10/2016              Original Version REQ- 244460
* Ranjeeth Nagishetty              29646                  02/10/2016              REQ - 170232 Added constants related to Clinical
* Santhi Mandava                   24069                  03/29/2016              Added constants related to logging
* Kritika Popat                    24466                  03/13/2016              Added constants related to Claims
* Harika Devi Kalidindi            25398                  04/18/2016              REQ-244383 Added constants related to Claim Image
* Praveen Kumar Parimi             25571                  04/22/2016              REQ-244430 Added constants related to Claims Intermediate Page And reordered constants as per alphabetical order.
* Praveen Kumar Parimi             25571                  04/27/2016              REQ-244430 Added constants for Max Dates.
* Praveen Kumar Parimi             26255                  04/28/2016              REQ-262430 Added constants for PCP/PCD Page.
* Santhi Mandava                   26846                  05/05/2016              REQ-262430 Added constants for logging.
* Samridh Manucha                  27178                  05/11/2016              Defect #222777.0002- Addind Dental Value as constant
* Gargey Sharma                    27522                  05/16/2016              Defect #225876.0001- Added Contant value for Public group And policy type 
* Harika Devi Kalidindi            27717                  05/18/2016              Defect #225410.0001-Modified constant value for PLATFORMCODE_HUM 
* Praveen Kumar Parimi             27942                  05/20/2016              Added Constants for Guided Process.                     
* Gargey Sharma                    28937                  06/01/2016              REQ - 269631: Added contant related to CAS Legacy Link 
* Avinash Choubey                  29441                  06/07/2016              REQ - 241731: Added constant related to Tooth History redirect URL 
* Pallavi Ravishankar              29226                  06/07/2016              REQ - 263142: Added contant related to CAS Legacy Prefill Link 
* Vishal Verma                     29581                  06/07/2016              Added constants for Certificate of Coverage 
* Harika Devi Kalidindi            29799                  06/10/2016              Identified PLATFORMCODE_HUM value Is being overridden
* Avinash Choubey                  29936                  06/13/2016              Defect# 229257 Added constant for  Benefits_D class  
* Avinash Choubey                  29988                  06/13/2016              Defect# 228609 Added constant for  AccumsType_D class
* Rajesh Keswani                   30160                  06/09/2016              Constants for REQ: 224050 - Implemented Benefit Search logic  
* Harika Devi Kalidindi            30521                  06/17/2016              Defect 230083.0001 Added home office.
* Praveen Kumar Parimi             30688                  06/20/2016              Added Constants for PCP/PCD And View Related Benefits Parameters
* Harika Devi Kalidindi            30711                  06/21/2016              Defect 229209.0001 Modified Constant value for ImageLoadDaysBuffer_HUM
* Avinash Choubey                  30980                  06/23/2016              Defect# 230271 Added constant for  PrimaryCare_CE_HUM class
* Vishal Verma                     33337                  07/28/2016              Updated constants for REQ - 270349
* Avinash Choubey                  32594                  08/03/2016              Defect# 235634 Added constant for  HUMUpdateCaseTaskHelper
* Praveen Kumar Parimi             32594                  07/14/2016              Added Constants related to Linked Cases Section
* Harika Devi Kalidindi            33954                  07/14/2016              REQ 269761 :Added Constants related to Linked Cases Section.
* Pradeepkumar Dani                34678                  08/02/2016              Added constants for live agent functionality - REQ - 270091
* Praveen Kumar Parimi             36948                  08/29/2016              Added Constant for process response.
* Vishal Verma                     40368                  09/19/2016              Added Constant for Due Items
* Avinash Choubey                  42551                  09/23/2016              Added constant for REQ 252450
* Sharn Shanmugam                  42551                  10/04/2016              Added constants for WQ App related components
* Pradeepkumar Dani                42551                  10/05/2016              Added constants forREQ-00669, REQ-244446, REQ-244447, REQ-244449 Changes
* Vamsi Kondragunta                42634                  10/07/2016              Changes As part of req :283728
* Vishal Verma                     44250                  10/25/2016              Changes As part of req :244448
* Gargey Sharma                    47056                  10/31/2016              REQ - 270080 : EPSS Conversion - SOAP to REST
* Pradeepkumar Dani                46100                  10/19/2016              Added constant for REQ - 287463
* Vishal Verma                     46667                  11/04/2016              Changes for Req:290021
* Santhi Mandava                   47535                  11/15/2016              Added constant for Req - 290132
* Pallavi Ravishankar                                     11/17/2016              Added Constants for Defect Fix - 247701.0001
* Praveen Kumar Parimi             48578                  11/17/2016              Added Constants for REQ - 273467. 
* Shruthi Karanth                  48462                  11/28/2016              Added constants fro REQ - 290045
* Vamsi Kondragunta                49317                  11/28/2016              Added constants for defect fix 250188.0001
* Praveen Kumar Parimi             50918                  12/07/2016              Added constant for CA 6412051 aka REQ 290998  
* Tanushree Hazari                 53059                  12/23/2016              Added constant for CA 6161081 - REQ - 291785.
* Praveen Kumar Parimi             56654                  01/15/2017              Changed the benefits page to newer version.
* Prasanthi Kandula                55297                  05/01/2017              Added constant for REQ-290027
* Gargey Sharma                    57200                  01/15/2017              Added constants for Power Of Attorney
* Pallavi Ravishankar              61268                  01/31/2017              Added constants for RTI Requirements
* Vamsi Kondragunta                59634                  02/01/2016              Updated the value for Pending -Transfer
* Sharan Shanmugam                 59634                  02/13/2017              Added constants for REQ - 296936
* Gargey Sharma                    70895                  02/13/2017              Added constants for Go365 Kids Delegate And Kids Delegate History service 
* Pradeepkumar Dani                61786                  02/13/2017              GetOffers implementation using JS remoting
* Sharan Shanmugam                                        02/24/2017              Added constants for REQ - 296663 And REQ - 296664
* Rajesh Chandgothia               70352                  02/14/2017              Member prescription Req# REQ - 296752
* Santhi Mandava                   71723                  03/01/2017              Added constants for REQ - 299436,REQ - 299435,REQ - 300041,REQ - 170202,REQ - 301537 
* Syed Mubarak                     74695                  03/01/2017              Added Constants for REQ - 303466, REQ - 301582
* Santhi Mandava                   74574                  03/09/2017              Added constants for REQ - 304157
* Vishal Verma                     74544                  03/09/2017              Defect 263119.0001 : Added -3 in constant
* Gargey Sharma                    57200                  01/15/2017              Added constants for Power Of Attorney
* Gargey Sharma                    75252                  02/13/2017              Added constants for Go365 Kids Delegate And Kids Delegate History service
* Gargey Sharma                    75252                  03/09/2017              Added constants for SaveOffers Implementation
* Sharan Shanmugam                 74899                  03/10/2017              Added constants for getOrderDetails Implementation
* Rajesh Chandgothia               75938                  03/08/2017              Edit order Req# REQ - 296651
* Jasmeen Shangari                 76619                  03/16/2017              Removed MCD LOB from Medicare list for REQ - 303466, REQ - 301582
* Jasmeen Shangari                 78252                  03/22/2017              Added PDP LOB in Medicare list for REQ - 303466, REQ - 301582
* Sharan Shanmugam                 78325                  03/28/2017              Added constants for editMember Implementation
* Harshith Mandya                                         03/24/2017              Added constants for REQ - 301392 - Stopping Auto Review
* Vishal Verma                     78304                  03/16/2017              REQ-296926 : Added MTM Indicator 
* Albin Arul                       78959                  03/24/2017              REQ - 296721 : Implemented Member Prescription section - Member Consent change
* Sharan Shanmugam                 85884                  04/24/2017              REQ - 291107 : Added constants for eligibility MTM indicator
* Pradeepkumar Dani                85359                  04/18/2017              REQ - 306323 - Update Classification names for closed case case inquiry
* Santhi Mandava                   87365                  04/27/2017              Removed "CASE_QAATEMPLATE_ENHANCEDQAAACCESS" string.
* Syed Mubarak                                            05/03/2017              Added constants for REQ - 296939 And REQ - 298771
* Vishal Verma                                            05/05/2017              Added constants for REQ - 311395
* Shruthi Karanth                  91794                  05/10/2017              Added constants for REQ - 296939
* Albin Arul                       94816                  05/18/2017              Added constants for REQ - 303397
* Gargey Sharma                    95016                  05/18/2017              Added constants for REQ - 300142  
* Shiva Pasumarty                  98098                  05/31/2017              Added constants for REQ - 313481  CA Ticket# 6734451
* Sharan Shanmugam                                        06/02/2017              Defect 291463.0001 REQ - 303395 
* Prafull Verma                    103536                 06/20/2017              Added Constants for REQ - 318353  
* Vishal Verma                     106668                 06/30/2017              Added Constants for REQ - 322595 : CA Ticket - 6880202
* Amar Gadewar                                            07/03/2017              REQ - 320483 - Quick Start Permision Set Constant
* Kritika Popat                                           06/30/2017              Added constants related to RTI security
* Syed Mubarak                      110889                07/19/2017              Added Constants for REQ - 318448
* Mohammed Noor                                           07/20/2017              Added Constants for REQ - 315928 - Creditable Coverage Intake - Case Service
* Shivam Anand                      106733                07/24/2017              Added constants related to RTI Print View Link
* Vishal Verma                      114200                07/27/2017              REQ - 323542 : Exception handling 
* Gargey Sharma                     112845                07/20/2017              REQ - 322649 Added condition in query, Not to display MET Tasks
* Prasad Jandhyala                  114623                07/28/2017              Modified for REQ - 308348 : MET Enrollment Section.
* Lalit Jain                        114278                07/31/2017              Added Constants for Milestones requirements
* Syed Mubarak                      115266                07/31/2017              Added Constants for REQ - 304169 : Launch EMME
* Vishal Verma                      116001                08/01/2017              Added constant for CA 6950067
* Sharan Shanmugam                                        08/09/2017              REQ - 324814 : Save Case Comment to CRM during ePost failure
* Mohammed Noor                     128981                09/12/2017              REQ - 329040 - Ability for CRM Service to create unknown member accounts resulting from Conduent data
* Pallavi R                         141064                10/17/2017              RQ 311453 - added constants for good cause reinstatement flow
* Vishal Verma                      149549                11/20/2017              Defect#327649.0001 : Added a condition when the New case Is clicked while on going chat
* Saurabh Kaple                     162080                01/09/2018              REQ - 333066 - Update Getoffers service request
* Manoj Chaurasia                   167654                12/28/2017              Added Constatnt for REQ - 350420 
* Manoj Chaurasia                   167654                01/04/2018              Added Constatnt for REQ - 349585
* Saurabh Kaple                     162078                01/09/2018              REQ - 338410 - Inquiry Search Service- Use Genkey from platform instead of MBE genkey conversion 
* Prasad Jandhyala                                        01/30/2018              REQ - 349596 Fix for CA Ticket 7030826 related to Incorrect Classification And Intent Population
* Manoj Chaurasia                   178236                02/02/2018              REQ - 349585:  Defect# 341375.0001 - Added Constant
* Mohammed Noor                                           02/06/2018              Removed the Constants added for Defect# 341375.
* Prasad Jandhyala                  180277                02/07/2018              REQ - 335173 : Added logic for redirection to CaseClosed_Edit_VF_HUM 
* Shivam Anand                      197530                03/05/2018              REQ349581- Add Member Preferences Link to CRM Service Person Account Page
* Vijaya Koriprolu                  197530                03/06/2018              REQ349580- Add Member Preferences Link on CRM Policy Member Page
* Mohammed Noor                     202879                03/21/2018              REQ - 359092 - Add New Work Queue Views for Omni Channel.
* Suraj Patil                       200646                03/22/2018              REQ - 354517 Pharmacy Case comment And non - Pharmacy case comment
* Bharath Rajappan                                        03/22/2018              REQ - 357304 Creditable Coverage Attestation constants added 
* Mohammed Noor                                           03/21/2018              REQ - 359092 - Fix Defect# 351496.0001 - Correct the View Names
* Mohammed Noor                                           04/04/2018              REQ - 359092 - Fix Defect# 353301.0001 - Correct the View Name
* Manoj Chaurasia                   209583                04/04/2018              REQ-356054 Ability for Medicare Enrollment Supervisors to add And remove users from Omni Queue.
* Rajesh CG                         210580                04/06/2018              REQ - 358952
* Manoj Chaurasia                   214472                04/13/2018              REQ - 358347: Defect#354614.0001 - Added Constants for Default Milestones ID as '9999' 
* Mohammed Noor                     216753                04/17/2018              REQ - 357368 - SF - USER - Ability for CRM to send cases to Omni Channel.
* Satyam Malviya                    211170                04/05/2018              REQ -356659 -Added EDIT MODE - Associated Claims Section
* Melkisan Selvaraj                 207348                04/02/2018              Added constants for ID cards
* Suraj Patil                       213522                04/11/2018              REQ - 351207 -  SF - USER - Research/Attachments - Task Section
* Suraj Patil                       213417                04/11/2018              REQ - 359093, REQ - 351208, REQ - 354346, REQ - 351171 Task Edit Page for Appeal task
* Vandana Chaudhari                                       04/20/2018              REQ - 358952
* Muralidhar Kollu                 218524                 04/23/2018              REQ - 303425
* Bharath Rajappan                                        05/04/2018              REQ - 356021
* Bharath Rajappan                 225143                 05/07/2018              REQ - 359179 Case Status freeze for creditable Coverage Attestation verification process
* Moshitha Gunasekaran             224503                 05/02/2018              REQ - 350862 - Fix to process Omni Routing rules for asynchrnous call from ETL API Acess profile
* Sheetal Choughule                                       05/10/2018              REQ - 362020
* Sheetal Choughule                                       05/11/2018              TEST And TERRA build fix REQ - 362020
* Bharath Rajappan                                        05/12/2018              REQ - 359179 - Added creditable Coverage constant
* Vandana Chaudhari                                       05/15/2018              REQ - 362020
* Moshitha Gunasekaran             230876                 05/19/2018              Defect fix for 358913 to associate policy for Member
* Mithra Bharadwaj                 231314                 05/21/2018
* Pradeep Dani                     234619                 05/28/2018              Date format update - defect#360377.0001
* Satyam Malviya                   234037                 04/05/2018              Remove code related to REQ -356659 as part of resolution rollback
* Mohammed Noor                    240362                 06/06/2018              REQ – 357355 - Add Disenrollment Specialty And Enrollment Hold views as Omni Eligible
* Prerna Prasad                    240144                 06/06/2018              Date format update - defect#362214.0001
* Prudhvi Pamarthi                 237056                 06/15/2018              REQ - 363608 : EPSS -Update App Key for CRM Service
* Bharath Rajappan                                        05/31/2018              REQ - 361863 - send follow up due date to track grace period  
* Asish Behera                                            06/25/2018              REQ - 367102 have the Person Speaking With And the Relationship to Member fields prefilled if the Interacting With Is equal to Member Or Member Representative Or CareGiver        
* Melkisan Selvaraj                252166                 06/19/2018              REQ - 366157 MBEServices replaced by MBEPlusService
* Asish Behera                                            07/12/2018              Defect 369783.0001 fix
* Asish Behera                                            07/24/2018              REQ - 367881 Defect 371524.0001
* Satyam Malviya                   268094                 08/07/2018              REQ - 370542 - MF 3.2- Available Processes added for PDP Pilot users
* Mohammed Noor                    268720                 08/08/2018              REQ - 362124 - Added New constant variable.
* Anurag Shah                      267634                 07/20/2018              REQ - 371243 - Adding the 'Queue Selection’ fields to the Person Account/ Policy Member pages
* Moshitha Gunasekaran             262118                 08/09/2018              REQ - 370423 - SF - USER - Unknown Member Account ID Display
* Melkisan Selvaraj                266572                 07/23/2018              REQ - 361165 Certificate of Coverage Link 
* Vijay Gurusamy                   272865                 08/03/2018              REQ - 367015 HP Insurance data
* Vandana Chaudhari                                       08/28/2018              NBA MF4-Guidance Alert
* Charan Konakandla                289399                 09/21/2018              REQ-367752 - Added Constants for PID search.
* Melkisan Selvaraj                289968                 09/24/2018              REQ - 376382 Modify rules for CAS benefit grids to auto-populate
* Moshitha Gunasekaran             292111                 09/28/2018              REQ - 371378 - TECH- Case Service with Attachment Log backend mapping
* Srivinas Damera                  293796                 09/30/2018              REQ - 369721 - eHub DSS 2.0 upgrade
* Moshitha Gunasekaran             294763                 10/05/2018              REQ - 375754 - TECH - SF - Textbox copy paste And validation error for unknown member 
* Prudhvi Pamarthi                 296559                 10/15/2018              REQ - 376380 Humana Pharmacy User - Case comments error message Not displaying properly if member doesn't have an ePost account
* Avinash Kumar Choubey                                   11/1/2018               REQ 378715 changes
* Suraj Patil                                             10/30/2018              REQ - 350626 - Power of attorney display from designee service.
* Charan Konakadla                 314128                 11/28/2018              REQ- 377179  - Provider search for Correspondance
* Satyam Malviya                                          11/27/2018              NBA MF-4   
* Melkisan Selvaraj                317009                 12/06/2018              REQ - 378820 - Correspondence related constants added.
* Asish Behera                                            12/31/2018              REQ - 382703 - User - Ability to prevent attaching multiple processes to cases (CRM)
* Rakesh Patni                      319302                12/13/2018              REQ - 380402
* Prathyu Tushar                   324032                 01/02/2019              REQ - 380419: Auto Route Case Wq list And auto route status field constant values
* Deepesh Rao                                             01/16/2019              REQ - 359918
* Santhosh Ganji                                          01/17/2018              REQ - 380499 & 380500 - Correspondence related constants added.
* Deepesh Rao                                             01/29/2019              REQ-369809 Update interacting About Type
* Shailesh Mali                                           01/30/2019              REQ-380714
* Santhosh Ganji                                          01/31/2019              REQ - 378996 - Correspondence related constants added.
* Deepankar Binjola                 336112                02/12/2019              MF-4 Financial Recovery
* AshokKumar Nutalapati            338598                 02/21/2019              REQ - 383717: Ability to Read Rx #, Written Drug And Dispensed Drug Included in Order from Humana Pharmacy Page 
* Amar Gadewar                                            02/21/2019              REQ - 383787 - - Rapid Force Permisison Set Constant
* Lakshmi Madduri                                           2/23/2019               REQ-385258
* Divya Proddaturi                 340085                  2/27/2019              REQ-383849
* Melkisan Selvaraj                341652                 03/04/2019              REQ - 387544
* Subhamay Mandal                                         02/18/2019              REQ - REQ - 383947 User Story B&E - Enable CI PAPI to the Case Page (RF) 
* Pallavi R                        338657                 01/29/2019              REQ - 383432 - Product type MCD check for Medicaid Good Cause Request Process
* Sumit Pharande                   345615                 03/27/2019              REQ - 362063 PCI Changes
* Kent Sanders                     383510                 03/26/2019              REQ - 383510 AutoUserDeac Change
* Moshitha Gunasekaran             348700                 03/25/2019              REQ - 380742 - Ability to view Transaction Reply Report (TRR) Process data for a specific member on a Medicare case
* Keerti Durbha                                           04/05/2019              REQ - 392177 - Fix Mentor Benifit Matrix Link
* Syed Mubarak T                                          04/01/2019              REQ - 383800 - Add TRR Template Fields to Omni Evaluation.
* Praveen Kumar Parimi                                    04/04/2019              REQ - 393077 aka CA Ticket 8138421 - Reading the Member not found from DTO instead of reading it as fault.
* Amar Gadewar                                            04/16/2019              REQ - 396366 Quality - RCC/Pharmacy - Display Additional Warning Message on ID Card Mgmt Page (RF) 
* Santhosh Ganji                                          04/19/2019              REQ - 386935 - Case Detail Restrictions (MED vs non-MED cases)—READ ONLY features     
* Dinesh Subramaniyan                                     04/18/2018                  REQ - 396416 - Modify logic for CAS benefit grids to auto-populate   
* Santhosh Ganji                                          04/22/2019              REQ - 379035 - MF 8-- Change Queue Owner page: limit the insurance WQV selections for HP profile
* Mohammed Noor                    351483                 04/12/2019              REQ - 396223 - Add new constants for TRR process BAM Tracking             
* Subhamay Mandal                                         04/22/2019              REQ - 396217 Quality - MIR - Ability for CSR to see remaining SLA field on the Case/Tasks tab (RF)
* Mohammed Noor                   354459                  05/02/2019              REQ - 396223 - Add additional constants for TRR process BAM Tracking
* Asish Behera                                            05/06/2019              REQ - 374762 - Auto-create an interaction and/or case in CRM Service due to after hours messages captured in IVR
* Mohammed Noor                                           05/09/2019              REQ - 396223 - Additional constants for Defect# 415166.0001
* Santhi Mandava                  355559                  05/10/2019              Removed "ENCRYPTIONTYPE" custom setting creation

* Santhosh Ganji                                          05/24/2019              REQ - 400147 - Humana Pharmacy Web Chat users Access to cases
* Suraj patil                                             06/1/2019               REQ - 366241 - MTV Member Remarks - Policy Member Page
* Suraj patil                                             06/1/2019               REQ - 350561 - Display Guidance alert data beyond 24 hours
* Prathyu Tushar                                          07/12/2019              REQ - 403624:  Automatically Select a Log Code Value Based on Classification/Intent Selection - HP Agent/Broker (CRMS) (ID# 30)
* Prathyu Tushar                                          07/17/2019              REQ - 396302:  required constand for ID - 27 mainstream push
* Satyam Malviya                                          07/11/2019              REQ - 402887 - Create a CRM Application Launcher - PBS Links
* Seema Kaulgi                                            07/04/2019              REQ - 403811 GBO Watson (Group) 
* Santhi Mandava                                          08/06/2019              REQ - 403609 Included product types related to group medicare member icon 
* Moshitha Gunasekaran                                    08/20/2019              REQ - 404608 - Support TaxID for Known and Unknown Agent
* Manikanta                                               08/21/2019              REQ - 396234 - Ability for CRMS to systematically display the HP case record type and Case Comment section
* Sagar Mn                                                08/26/2019              REQ - 405925,405927 - Case service v4 constant added.
* Suraj Patil                                             10/03/2019              REQ - 409544 allow consumer to send Int With type=Internal
* Chetan Sindhe                                           11/13/2019              REQ - 400054 - CRM Retail Agent of Record
* Shailesh Mali                                           11/15/2019              REQ - 409603 Process Builder logic Migration to Apex Code.
* Santhi Mandava                                          11/15/2019              Included member icons related constants.
* Satyam Malviya                                          11/11/2019                   REQ-404584 - Original Version - Created new class for RX connect functionality
* Sumit Pharande                                          11/19/2019              REQ-412009 New Variable added
* Mohammed Noor                                           12/07/2019              US826586 - Constants added for Case Workflow rules optimization. 
* Santhosh Ganji                                          02/01/2020              USER STORY 729006 - Constant Added for HP Service Model WorkQueues. 
* Amar Gadewar                                            01/28/2020              USER STORY 729689 PR00091574 - MF 1 - User Interface to manage Icons display (CRM)
* Mohammed Noor                                           02/19/2020              Added New constants for REQ - 411383, REQ - 411383 And REQ - 410422. 
*  Anjani  Vaddadi                                        21/02/2020                   REQ- RTI VAT changes
* Vinay lingegowda                                        03/04/2020                  USER STORY 856241 PR00091574 - MF 1 - Quality - PCP Template Updates (CRM)
* Kapil Arora                                             03/18/2020              USER STORY    857398 MF 1 - AHT/Quality - Medicare Good Cause Reinstatement Template Auto Routing and AHT Updates (CRM)
* Vinay Lingegowda                                        03/19/2020              US-857398 AHT/Quality - Medicare Good Cause Reinstatement Template Auto Routing and AHT Updates (CRM)
* Shailesh Mali                                           04/20/2020             REQ - 791947 - Accumulator SOAP to RESOLUTION_SUCCESS
* Shailesh Mali                                           04/20/2020             REQ - 749402 Accumulator Description
* Keerti Durbha                                           04/22/2020             738134- PR00091318 - MF 9 - Medicare OSB Vision And Fitness
* Sayali Nemade                                           4/29/2020              REQ - 842472 - Pharmacy Incomm Service V2
* Mohammed Noor                                           05/07/2020             USER STORY 1035056 - Stop CRM to CPP Inquiry creation.
* Akshay Pai                                              05/04/2020             REQ-1048944 New Variables added
* Satyam Malviya                                          5/21/2020              DF 766 Fix Corrected Application key that have been passes to RXConnect Link and MemberPreference Link Fix
* Satyam Malviya                                          05/14/2020             Fixed DF-716 - 728173 - Secondary Accumulators in Accumulator Information are displayed when VisitsUsed value equals zero and DF-724-749402 - Dental accumulators are not matching CCP/GCP as intended
* Pooja Kumbhar                                           05/29/2020             REQ-846171 added new constants for Member Maintainance
* Pooja Kumbhar                                           06/15/2020             MM Change in link name - updating the constant
* Pooja Kumbhar                                           06/12/2020             Added Req-number constant for ON/OFF switch SegmentIndicator and LOB
* Prudhvi Pamarthi                                        06/12/2020             US#800050 Close Or Cancel two Or more cases at once with case comments within CRM Service
* Himalay Patel                                           06/24/2020             REQ - 939336  Display Logged 'Invoice Request' Process on Case Detail Page (RxRF)
* Sai Kumar Boga                                          06/29/2020             REQ - 1197867 MF 1- Development- FR1.1 Update case service to allow case owner to be updated            
* Subhamay Mandal                                         06/29/2020             USER STORY 1203975 PR00095159 - IR 50-50 OGO - MVP - CRM Action page layout to provide feed back for ODM returned Case
* Mithra Bharadwaj                                        07/06/2020             US#1199202 added MsgDefCode value
* Sai Kumar Boga                                          07/09/2020             REQ - 1200210 Exception Handling in Update Case Service for Invalid Case id
* Subhamay Mandal                                         07/13/2020             USER STORY 1143645 update deferred message
* Pooja Kumbhar                                           07/07/2020             Removing constant for REQ-1201595 :- PR00091494 - PLT - Remove Temporary On-Off flag - MF WR-04914 - Business Segment Indicator Field And LOB field
* Vinay Lingegowda                                        07/23/2020             USER STORY 1278792 PR00091574-IR-50-50-OGO- disable close case option for actionable cases
* Akshay Pai                                              07/16/2020             REQ - 1292117 IVA NINA - Member Secure chat changes
*   Prudhvi Pamarthi                                      07/23/2020             US#1045266 Transfer two or more cases at once with case comments within CRM Service
* Vinay Lingegowda                                        07/23/2020             USER STORY 1275884 PR00091574-IR 50-50 OGO - trigger auto clone case for Accept Recommendation
* Y Sunil Kumar Reddy                                     07/24/2020             User Story 1186355: PR00091574 - MF 1 - Quality - Ability to make PCP/PCD Change Template mandatory (CRM)
* Mohammed Noor                                           07/24/2020             US825176 PR00093155 - MF 18 - Added new constants.
* Pradeep Kumar Yadav                                     07/22/2020             User Story 1248496: CA ticket- 9228963 - CRM Service: Collection size 1, 050 exceeds to maximum size of 1000
* Vinay Lingegowda                                        03/08/2020             DF-1245 Fix.
* Asish Behera                                            08/07/2020             HP classification Types constant Added
* Y Sunil Kumar Reddy                                     08/11/2020             DF-1323 Fix - Added a string constant : Clear
* Ashok Kumar Nutalapati                                  08/12/2020             REQ-1220259 - Log 'Invoice Request' Guided Process to Case(CRMS)
* Akshay Pai                                              08/05/2020             REQ - 1131866 IVA WATSON - Member Secure chat changes
* Riya Agarwal                                            08/18/2020             REQ - 760189-PR00091606 - MF 4 - Finance Code Order Detail Page
* Sayali Nemade                                           07/24/2020             REQ 1137828 MF 3 - CRM User Access to CCP GCP Data in Repository (Legacy Contact History Section - Group & Member)
* Vinay Lingegowda                                    08/24/2020             User Story 1314983: PR00091574- IR 5050-OGO -Open case in Edit mode to set complaint fields (Scrubber Action-Accept on open cases) -(CRM)
* Vinay Lingegowda                                        08/24/2020             User Story 1293633
* B Manogna                                               09/01/2020             USER STORY-1409272-PR00094007 - MF 2 - CDO Patient Relations My Wellness Journey: Card Number Verification
* Akshay Pai                                              09/02/2020             REQ - 1133858 IVA WATSON - Group/Agent Secure chat changes
* Subhamay Mandal                                         09/08/2020             User Story 1293633: PR00091574- IR 5050-OGO - Recommendation actions on re-opened cases (reject )-(CRM)
* Riya Agarwal                                            09/07/2020             REQ - 1338123 - PR00090723 - MF 3 - CCP GCP Archived Inquiry Details Page - Task List Section
* Mohammed Noor                                           09/18/2020             PR00088383 - MF6 - US1334690 and US1334714 - Added new constants.
* Anoop Nadukandiyil                                      10/12/2020             User Story 1566969: PR00094007 - MF 1 - CDO Tech Story - Salesforce Winter 21/Code fixes- Account external Id Prefix 
* Himalay Patel                                           10/14/2020             REQ - 1145316  Display Completed 'Humana Pharmacy Web Issues' Process on Case Detail Page (RxRF) 
* Firoja Begam                                            10/27/2020             US-1279281- PR00095366 - MF 1 - new fields in policy details section (member policy page)
* Raja Sekhar Reddy M V                               10/28/2020             User Story 1609149: PR00094816 - MF 5 - DEV - Add Go365 Medicaid Guardian designation form to Associated Forms And Information Section of Person Acct Page in CRM Service
* Y Sunil Kumar Reddy                                     11/05/2020             US-1641500 Added Green Bay Grievance And Appeals Workqueue string
* Sai Kumar Boga                                          11/10/2020                 REQ - 1567028  CALL_MF03_FR02: KY MMC 2021(Added per CR17)_CRM service must create a NATIVE AMERICAN indicator
* Vinay Lingegowda                                        11/13/2020             USER STORY 1628353 PR00091574 - IR 5050 - OGO - Add a new View "Clarabridge Recommendations" for Re-opened cases (CRM)
* Mohammed Noor                                           11/19/2020             US1654207 - Added new constants.
* Ajay Chakradhar                                         11/20/2020             US 1641087 - PR00093155 - MF 21 - Release 12/18
* Sayali Gavane                                           11/25/2020             REQ - 1457838 - PR00095581 - MF 53 - Out of Area Indicator for CRM
* Sayali Gavane                                        01/19/2021                 REQ_1450427-CA ticket - 9261131 (Sections in benefit page are not displaying) 
* Firoja Begam                                            12/28/2020             US1755360 - EAS Service side changes - CR1876 (CR1808 Part 2) - SF
* Atia Uzma                                               01/21/2021             User Story 1186358: PR00091574 - MF 1 - Quality - "Medicare Expedited Appeals Template" Updates (CRM)
* B Manogna                                               01/21/2021             User Story - 1881617 - CDO Renamed 'Ready to Change' status to 'PCP Appointment Scheduled' for lead record type change logic
* Riya Agarwal                                        01/29/2021            REQ - 1759871 - PR00094167 - MF 4 - TECH - SF - CRMS – Legacy App Launcher code clean up - PBS, PBS Comments
* Jasmeen Shangari                                        02/03/2021             REQ - 1414643 - Filter CaseComment and Attachment for MHK
* Moshitha Gunasekaran                                    02/08/2021            User Story 1083065: PR00094923: MF2.6.1a CRM CASE/TASK VIEW- CRM Review and Update Task/Case
Santhi Mandava                                            02/10/2021                  User Story 1083065: PR00094923: MF2.6.1a CRM CASE/TASK VIEW- CRM Review and Update Task/Case
* Vinay Lingegowda                                     02/12/2021                 USER STORY 1748357 PR00091574 - IR 5050 - Add a Transfer Reason in Case/Tasks page - Supervisor Bulk Transfer (CRM)
* Firoja Begam                                            02/10/2021             US-1760349 - PR00095180 - CRM 6.01 Add PEO Indicator for MTV and CAS products  to Member Policy Page (copy)
* Vijaykumar                                              02/22/2021                 User story 1956905 T1PRJ0001827- IR 5050 - Case/tasks page: Supervisor: Add new filter by Age option( CRM)
* Mohammed Noor                                           02/24/2021             USER STORY 1801197 - PR00094010 -STRIDES MVP MF 6 - Unified Search Button Case page
* Anjani Vaddadi                                          02/26/2021              User Story#-1260069 -T1PRJ0003372 - MF 1 - FR1.1 Veteran Member Icon
* Y Sunil Kumar Reddy                                     03/08/2021             User Story 1874735: T1PRJ0001827 -MF 1 - OGO - Quality - Ability to prevent users to select complaint on the cases if the case Is routed elsewhere due to attached template (CRM)
* Vinay Lingegowda                                        03/08/2021             USER STORY 2044469 IR 5050 - OGO - Optimize Accept recommendation actions for queues with clone & link feature (CRM)
* T Prasanna Sai Kumar                                    03/10/2021             REQ - 2003668 - T1PRJ0002533 - MF 1 - CDO Patient Relations Leads: Converted Date And Converted By
*   Saikumar Boga                                         03/11/2021           US 1979274 CR18 - CRM to MHK Transfer for Unknown Members
* Himalay Patel                                           03/16/2021             REQ - 1711424: Display Expired and Expiring Soon Credit Card Icon (CRMS) (ID# 131)
* Anjani Vaddadi                                          03/17/2021             User Story#-1260081 -T1PRJ0003372 - MF 2, MF 3, MF 4 - Veteran Update Functionlity
* Atia Uzma                                               03/23/2021             User Story 1898796: T1PRJ0001827 - MF 1 - RF Clean Up (RF)
* B Manogna                                               03/26/2021             User Story 2067890: T1PRJ0002533 - MF 1 - CDO Patient Relations: Add Gift to All Campaign Members
* Ashok Kumar Nutalapati                                  04/07/2021             User Story 2051449: T1PRJ0002517 - MF 1 - Auto-Select G&A Rights Given and Complaint Based on Service Model Type - Case Edit Page (CRMS) (ID# 137)
* Vinay Lingegowda                                        04/07/2021             USER STORY 2173367 T1PRJ0001827- IR 5050 -  Update "Assigned" as  "Assigned to me" - Work-Items -Cases/Tasks page (CRM)
* B Manogna                                               04/24/2021             User Story 2227763: T1PRJ0002533 - MF 1 - CDO Patient Relations Gifts Given: Correct Total Given this Year (Second Run) 
* Kiran Kumar                                             05/21/2021             User Story 2169124: Update 'Humana Pharmacy Web Issues' Recommended Process Classification/Intent Criteria (CRMS)
* Giri Babu                                               05/25/2021             US - 2015850 Veterans Update History Tracking  
* Gourab Tewary                                           06/14/2021             User Story 2330055: T1PRJ0001827 - IR 5050 - Good Cause Reinstatement template - required for Classification Type - RCC Web Chat (CRM)
* Kavya Bondada                                           07/13/2021             User Story 2451369: T1PRJ0001827 - MF 1 - Quality - Change the label of 'Cancel' button on the Case Edit page to 'Exit without Saving' (CRM)
* Sagar MN                                                08/03/2021             US 2098896 Added constant for TRR Template List
* Tummala Vijaya Lakshmi                                  08/27/2021             USER STORY 2578074 - T1PRJ0002980/PR00095219 - MF 5 - Supervisor Case Management
* Nikhil Malhotra                                         10/26/2021             REQ - 2571421 Removing Lead Record Type(Ready to Convert)
* AshokKumar Nutalapati                                   01/10/2022             REQ - 2843422 T1PRJ0002517 - MF 1 - Inactivate Rx: Process Content (RxRF) (ID# 163)
* Venkata Nagasiva                                05/17/2022                US-3367406 T1PRJ0010888 - MF [1] - Rebranding - Update text references from "Humana Pharmacy" to "Centerwell Pharmacy" - Web Issues Guided Flow - Process Name
* Ankima/Isha                                             11/21/2022             Added variable used in ArchivalCaseHistory
* Pinky Vijur                                             01/20/2023             3215226    T1PRJ0891339 - MF 5 - TECH - Pharmville Decommissioning - Permission Set Usage
* Shaik Mujeebur Rahaman                        02/07/2023                US-4129681 T1PRJ0170850 - MF 22334 - Lightning - Today's Task (My Due Work)
* Nilesh Gadkar                       05/18/2023        User Story 4407664: T1PRJ0191994- MF 130- Display Member Race and Ethnicity in CRM Service- ‘Transaction Reply Report (TRR)’ Template (Classic)
******************************************************************************************************************************************************/
Public Class GLOBAL_CONSTANT_HUM
{
    public static final String Business = 'Business';
    public static final String Correspondence_CRM_to_Macess = 'Correspondence CRM to Macess';
    
    // US 1641087 //
    Public Static final String OI_CALLBACK_METHOD = 'processMBEOtherInsuranceResponse';
    
    //----------------------- US-1760349- PR00095180 -- PEO Indicator------------------------------------------------//
    public static final String PEO = 'PEO';
    public static final String PE = 'PE';
    public static final String PEO_YES = 'Yes';
    public static final String PEO_NO = 'No';
    
    //----------------------- US-1279281- PR00095366 ------------------------------------------------//
    public static final String PROCESSRESPONSE_HUM = 'processResponseVendor';
    public static final String ACCL = 'ACCL';
    //-----------------------------------------------------------REQ - 357370-------------------------------------
    Public Static final String sLegacyCode_SubString = 'amp&';    
    //------------------------------------------------------------REQ - 289363--------------------------------------------------// REQ - 313481
    Public Static final String ASPECTSERVICE_VISITOR = 'Visitor';
    Public Static final String ASPECTSERVICE_CHAT_STATUS_MISSED = 'Missed';
    Public Static final String ASPECTSERVICE_IDENTIFIER = 'TCSDATA';
    Public Static final String ASPECTSERVICE_CHAT_SLA = '30';
    Public Static final Integer ASPECTSERVICE_DEFAULT_DURATION = -30;

    Public Static final String AUTHTYPE_BHINP_HUM = 'BHInpatient';
    Public Static final String AUTHTYPE_BHOUP_HUM = 'Outpatient'; 
    Public Static final String AUTHTYPE_INP_HUM = 'Inpatient';
    Public Static final String AUTHTYPE_OUP_HUM = 'BHOutpatient';
    Public Static final String AUTHDETAIL_FACILITY_HUM = 'Facility';
    Public Static final String AUTHDETAIL_PROVIDER_HUM = 'Provider';
    Public Static final String AUTHDETAIL_REQUESTING_HUM = 'Requesting';
    Public Static final String AUTHDETAIL_TREATING_HUM = 'Treating';

    Public Static final String BACKSLASH_HUM = '/';
    Public Static final String BENEFITURL_HUM = '/apex/BenefitsV2_VF_HUM?Id=';
    Public Static final String BENEFITPAGEINDICATOR_HUM = 'BEN';

    Public Static final String CASAppName = 'CASLegacy';
    Public Static final String CASPrefillAppName = 'CASLegacyPrefill';
    Public Static final String CASE_CONSTANT_HUM = 'Case';
    Public Static final String CASEID_CONSTANT_HUM = 'CaseId';    
    Public Static final String CASEACTIONLAUNCH_HUMFLOWADDRESSVERIFDESC_HUM = 'Medicare Address Verification Form';
    Public Static final String CASEACTIONLAUNCH_HUMFLOWMEDICAREAVF_HUM = 'Medicare AVF';
    Public Static final String CASEACTIONLAUNCH_HUMFLOWMEDICAREEXPEDITEDDESC_HUM = 'Medicare/Medicaid Expedited Appeal';
    Public Static final String CASEACTIONLAUNCH_HUMFLOWMEDICAREEXPEDITED_HUM = 'Medicare/Medicaid Expedited Appeal';
    Public Static final String CASEACTIONLAUNCH_HUMFLOWMEDICAREOIDESCRIPTION_HUM = 'Medicare Other Insurance (OI) Form';
    Public Static final String CASEACTIONLAUNCH_HUMFLOWMEDICAREOI_HUM = 'Medicare OI';
    Public Static final String CASEACTIONLAUNCH_HUMFLOWMEDICAREPARTDESCR_HUM = 'Medicare Part D Redetermination Request';
    Public Static final String CASEACTIONLAUNCH_HUMFLOWMEDICAREPARTD_HUM =  'Medicare Part D';
    Public Static final String CASEACTIONLAUNCH_HUMFLOWTABID_HUM = 'tabId';
    Public Static final String CASEACTIONLAUNCH_HUMFLOWCOBPROCESSTYPE_HUM = 'ProcessType';
    Public Static final String CASEACTIONLAUNCH_HUMATTACHMENTLOGFORDAC_HUM = 'Medicare_Election_Lock_In_Template';
    Public Static final String CASEACTIONLAUNCH_HUMMEDICAREDAC_HUM = 'Medicare DAC';
    Public Static final String CASEACTIONLAUNCH_CASESUBTABID_HUM = 'caseSubTabId';
    Public Static final String CASEACTIONLAUNCH_HUMFLOWAUTHREFERRALFLOWDESC_HUM = 'Authorization/Referral Requirements';
    Public Static final String CASEACTIONLAUNCH_HUMFLOWAUTHREFERRALFLOWPROCESS_HUM = 'Auth Referral'; 
    Public Static final String CASEACTIONLAUNCH_HUMFLOWMEDICAREREINSTATDESC_HUM  = 'Medicare Good Cause Reinstatement Form'; 
    Public Static final String CASEACTIONLAUNCH_HUMFLOWMEDICAREREINSTATPROCESS_HUM  =  'Medicare Good Cause Reinstatement';
    Public Static final String CASEACTIONLAUNCH_HUMFLOWMEDICAIDREQUESTDESC_HUM  = 'Medicaid Good Cause Request Form'; 
    Public Static final String CASEACTIONLAUNCH_HUMFLOWMEDICAIDREQUESTPROCESS_HUM  =  'Medicaid Good Cause Request'; 
    Public Static final String CASEACTIONLAUNCH_HUMFLOWPCPPCDPROCESS_HUM  =  'PCP/PCD Questions'; 
    Public Static final String CLAIMS_DENTAL_HUM = 'Dental';
    Public Static final String ClaimsService_Summary_HUM = 'ClaimsService_GetClaimsSummary';
    Public Static final String Content_type_HUM = 'content-type';
    Public Static final String Connection_HUM = 'Connection';
    Public Static final Integer Constant_100_HUM = 100;
    Public Static final String CREATEDDATE_HUM = 'dCreatedDate';
    Public Static final String CRMSERVICESYSTEMNAME_HUM = 'CRM';
    Public Static final String CASEACTIONLAUNCH_HUMFLOWMEDICAREDAC_HUM = 'Disenrollment and Cancellation';
    Public Static final String CASEACTIONLAUNCH_HUMFLOWMEDICARECCA_HUM = 'Creditable Coverage Attestation';

 //-------------------------------------Case Type and Subtypes-----------------------REQ-359918
    public Static final String HUMCaseTypeSIU = 'SIU';
    public Static final String HUMCaseTypePPI= 'PPI';
    public Static final String HUMCaseTypeCorrespondence ='Correspondence Inquiry';
    public Static final String HUMCaseSubTypeRoutine=null;
    Public Static final String HUMPharmacyIntType = 'Pharmacy';
    Public Static final String HUMProspectiveCustomerIntType = 'Prospective Customer';
    Public Static final String HUMUnknownProviderIntType = 'Unknown-Provider';
    Public Static final String HUMUnknownGroupIntType = 'Unknown-Group'; 
    Public Static final String HUMUnknownAgentIntType = 'Unknown-Agent';
    Public Static final String HUMAgentIntType = 'Agent';
    Public Static final String HUMGroupIntType = 'Group';

    
//----------------------------------------------------------------------------------
    Public Static final String HumHPRecType = 'HP';
    Public Static final String DATECONSTANT_HUM = '%2F';
    Public Static final Integer DEFAULTBATCHLIMIT = 5;
    Public Static final String DEN = 'DEN';
    Public Static final String GO365_WEBEMULATE = 'https://qa-hss.humana.com/ITSecurity/HSSOneClick/LaunchHSS.aspx?TARGET=https://qa-slhss.humana.com/WebEmulation/EmulateMember.aspx'; 
    Public Static final String GP_CASEID_HUM = 'a9b09f4d-0911-4f05';
    Public Static final String GROUP_RECORDTYPE_HUM = 'GROUP'; 
    Public Static final String HOME_OFFICE_ALL = 'HOME_OFFICE_ALL';
    Public Static final String HOME_OFFICE_DENTAL = 'HOME_OFFICE_DENTAL';
    Public Static final String HOME_OFFICE_HUM = 'Home Office'; 
    Public Static final String HOME_OFFICE_MEDICAL = 'HOME_OFFICE_MEDICAL';
    Public Static final Integer HTTP_ACCEPTED_HUM = 202; 
    Public Static final Integer HTTP_OK_HUM = 200;
    Public Static final String HTTPREQUEST_AUTHCATEGORY_HUM = 'All';
    Public Static final String HTTPREQUEST_PAGESIZE_HUM = '50';
    Public Static final String HTTPREQUEST_PERSONID_HUM = 'SdrPersonID';
      Public Static final String HUMATTACHMENTLOGFORDAC_HUM = 'HUMATTACHMENTLOGFORDAC_HUM';
    Public Static final String HYPHEN_HUM = '-';
    Public Static final String CRITICAL_HUM = 'Critical';
    Public Static final String HIGH_HUM = 'High';
    Public Static final String NORMAL_HUM = 'Normal';
    Public Static final String AGE_HUM = 'Age';
    Public Static final String PRIORITY_HUM = 'Priority';
    Public Static final String DUEDATE_HUM = 'DueDate';
    Public Static final String PRIORITYANDAGE_HUM = 'Priority&Age';
    Public Static final String TASKLISTVIEW_AGETASKSONCASE_HUM = 'Age - Cases and Tasks by oldest Case Age';
    Public Static final Integer LIMITVALUE_HUM = 3500;
    Public Static final String ID2_HUM = 'ID';
    Public Static final String CASEACTIONLAUNCHCONTROLLER_GROUPCASETYPE_HUM = 'Group_Case';
    Public Static final String CASEACTIONLAUNCHCONTROLLER_PROVIDERCASETYPE_HUM = 'Provider_Case';
    Public Static final String CASEACTIONLAUNCHCONTROLLER_AGENTBROKERCASETYPE_HUM = 'Agent_Broker_Case';
    Public Static final String CASEHISTORYCOMPONENTCONTROLLER_SEACRHSORTDESC_HUM = 'DESC';
    Public Static final String CASEHISTORYCOMPONENTCONTROLLER_SEACRHSORTASC_HUM = 'ASC';
      Public Static final String CASECOMMENT_SIZE_EXCEPTION_HUM = 'data value too large';
    Public Static final String CHANGECASEOWNER_UNKNOWNMEMBER_HUM = 'Unknown Member';
    Public Static final String CHANGECASEOWNER_AGENTBROKERCASE_HUM = 'Agent/Broker Case';
    Public Static final String CHANGECASEOWNER_GROUPCASE_HUM = 'Group Case';
    Public Static final String CHANGECASEOWNER_UNKNOWNCASE_HUM = 'Unknown Case';
    Public Static final String CHANGECASEOWNER_PROVIDERCASE_HUM = 'Provider Case';
    Public Static final String CHANGECASEOWNER_MEMBERCASE_HUM = 'Member Case';
    Public Static final String CASEACTIONLAUNCHCONTROLLER_HP_GROUPCASETYPE_HUM = 'HP_Group_Case';
    Public Static final String CASEACTIONLAUNCHCONTROLLER_HP_PROVIDERCASETYPE_HUM = 'HP_Provider_Case';
    Public Static final String CASEACTIONLAUNCHCONTROLLER_HP_AGENTBROKERCASETYPE_HUM = 'HP_Agent_Broker_Case';
    Public Static final String CASEACTIONLAUNCHCONTROLLER_HP_MEMBERCASETYPE_HUM = 'HP_Member_Case';
    Public Static final String CASEACTIONLAUNCHCONTROLLER_HP_UNKNOWNCASETYPE_HUM= 'HP_Unknown_Case';
    Public Static final String USER_HUM = 'User';
    Public Static final String CHANGECASEOWNER_CCSPECIALISTPROFILE_HUM = 'Customer Care Specialist';
    Public Static final String NONE_HUM = 'None';
    Public Static final String NONEDISPLAY_HUM = '---None--';
    Public Static final String CHANGECASEOWNER_SELECTTOPIC_HUM = 'Select a Topic';
    Public Static final String ID3_HUM = 'Id';
    Public Static final String CHANGECASEOWNER_CHANGEOWNERDATE_HUM = '; Date:';
    Public Static final String CHANGECASEOWNER_CHANGEQMSREFERRAL_HUM = '; QMS Referral #';
    Public Static final String CHANGECASEOWNER_QMSREFERRALSUCCESS_HUM = 'Referral Updated Successfully in QMS';
    Public Static final String CHANGECASEOWNER_COWNERRESPONSEBODY1_HUM = 'Attachment Name : Quality Referral Details; Attachment Id : Quality Referral Details; Referral Id:';
    Public Static final String CHANGECASEOWNER_COWNERRESPONSEBODY2_HUM = '; Is Inquiry Updated in Quality: true; Referral Message:';
    Public Static final String CHANGECASEOWNER_QMSPROVIDERTAXID_HUM = 'Provider Tax ID';
    Public Static final String CHANGECASEOWNER_QMSPROVIDERNAME_HUM = 'Provider\'s Name';
    Public Static final String CHANGECASEOWNER_QMSPROVIDERFIRSTNAME_HUM = 'Provider First Name';
    Public Static final String CHANGECASEOWNER_QMSPROVIDERFACILITY = 'Is this for a Provider or a Facility/Group?';
    Public Static final String CHANGECASEOWNER_QMSPROVIDERGROUPNAME_HUM = 'Facility/ Group Name';
    Public Static final String CHANGECASEOWNER_QMSPROVIDERLASTNAME_HUM = 'Provider Last Name';
    Public Static final String CHANGECASEOWNER_QMSPROVIDERFACILITYTAXID_HUM = 'Provider/Facility/Group Tax ID';
    
    Public Static final String CHANGECASEOWNER_QMSDATEINCIDENCE_HUM = 'Date of the incident/service';
    Public Static final String CHANGECASEOWNER_QMSCOMPLAINTDETAILS_HUM = 'Complaint Details';
     Public Static final String CHANGECASEOWNER_QMSPROVIDERADDRESS_HUM = 'Provider Street Address';
    Public Static final String CHANGECASEOWNER_QMSPROVIDERCITY_HUM = 'Provider City';
    Public Static final String CHANGECASEOWNER_QMSPROVIDERSTATE_HUM = 'Provider State';
    Public Static final String CHANGECASEOWNER_QMSPROVIDERZIPCODE_HUM = 'Provider Zip Code'; 
    Public Static final String CHANGECASEOWNER_QMSPROVIDERNPI_HUM = 'Provider/Facility/Group NPI';
    
    Public Static final String CLOSEDCASE_CCPGCPRSOMEDICARE_HUM ='RSO- Medicare';
    Public Static final String CLOSEDCASE_CCPGCPMEDICAIDLOB_HUM ='MCD';
    Public Static final String CLOSEDCASE_CCPGCPMEDICAID_HUM ='Medicaid';
    Public Static final String CLOSEDCASE_CCPGCPMEDICARE_HUM ='Medicare';
    Public Static final String VALUE_ONEFOURTHREEFOURNINE = '14349';
    Public Static final String NEWLINE_HUM = '\n';
    Public Static final String CASEACTIONLAUNCHCONTROLLER_GSOCOMMERCIAL_HUM = 'GSO- Commercial';
    Public Static final String CASEACTIONLAUNCHCONTROLLER_MEMBERCASETYPE_HUM = 'Member_Case';
    Public Static final String CLOSEDCASE_CCPGCPMEDICAIDTEAMID_HUM ='14733'; 
    Public Static final String CLOSEDCASE_CCPGCPMEDICARETEAMID_HUM ='3467';
    Public Static final String CLOSEDCASE_PARTDDRUGINFORMATION_HUM = 'Drug Information:';
    Public Static final String CLOSEDCASE_PARTDPHYSICIANINFORMATION_HUM = 'Prescribing Physician’s Information:';
    Public Static final String CLOSEDCASE_PARTDEXPEDITEREQUEST_HUM = 'ExpediteRequest';
    Public Static final String CLOSEDCASE_PARTDPROCESSDATA9_HUM = 'Did the member or member representative state at any time that applying the 72 hour standard review timeframe may seriously jeopardize the life or health of the enrollee or the enrollees ability to regain maximum function?:';
    Public Static final String CLOSEDCASE_PHYSICIANNAME_HUM = 'PhysicianName';
    Public Static final String CLOSEDCASE_PARTDCONTACTPERSON = 'Office Contact Person:';
    Public Static final String CLOSEDCASE_CONTACTPERSON_HUM = 'PhysicianOfficeContactPerson';
    Public Static final String CLOSEDCASE_PARTDADDRESS_HUM = 'Address';
    Public Static final String CLOSEDCASE_PHYSICIANADDRESS_HUM = 'PhysicianAddress';
    Public Static final String CLOSEDCASE_PARTDPHYSICIANCITY_HUM = 'City:';
    Public Static final String CLOSEDCASE_PHYSICIANCITY_HUM = 'PhysicianCity';
    Public Static final String CLOSEDCASE_PARTDPHYSICIANSTATE_HUM = 'State:';
    Public Static final String CLOSEDCASE_PARTDPROCESSDATA1_HUM = 'Next Steps'; 
    Public Static final String CLOSEDCASE_PARTDPROCESSDATA2_HUM = 'Transfer the CRM Case to the following location';    
    Public Static final String CLOSEDCASE_PARTDPROCESSDATA3_HUM = 'Service Center : Louisville';    
    Public Static final String CLOSEDCASE_PARTDPROCESSDATA4_HUM = 'Department : Louisville G&A/Correspondence Screening';   
    Public Static final String CLOSEDCASE_PARTDPROCESSDATA5_HUM = 'Topic : Part D Redetermination Reviews'; 
    Public Static final String CLOSEDCASE_PARTDPROCESSDATA6_HUM = 'Overview:';  
    Public Static final String CLOSEDCASE_PARTDPROCESSDATA7_HUM = 'For multiple requests: if it is the same drug, different dosages, you may utilize the same form (up to three requests). If it is a different drug, a different form is required';    
    Public Static final String CLOSEDCASE_PARTDPROCESSDATA8_HUM = 'Expedite Request:';  

    Public Static final String CLOSEDCASE_PARTDDRUGDOSAGE_HUM = 'Drug Dosage:'; 
    Public Static final String HUMDrugDosage = 'DrugDosage';   
    Public Static final String CLOSEDCASE_DRUGDOSAGE1_HUM = 'DrugDosage1';    
    Public Static final String CLOSEDCASE_DRUGDOSAGE2_HUM = 'DrugDosage2';    
    Public Static final String CLOSEDCASE_DRUGDOSAGE3_HUM = 'DrugDosage3';    
    Public Static final String CLOSEDCASE_PARTDRXOBTAINED_HUM = 'Rx Obtained:'; 
    Public Static final String HUMRXObtained = 'RxObtained';    
    Public Static final String CLOSEDCASE_RXOBTAINED1_HUM = 'RxObtained1';    
    Public Static final String CLOSEDCASE_RXOBTAINED2_HUM = 'RxObtained2';    
    Public Static final String CLOSEDCASE_RXOBTAINED3_HUM = 'RxObtained3';    
    Public Static final String CLOSEDCASE_PARTDDATETIMEOPENED_HUM = 'Date/Time Opened:';    
    Public Static final String CLOSEDCASE_DATETIMEOPENED_HUM = 'DateTimeOpened';    
    Public Static final String CLOSEDCASE_DATETIMEOPENED1_HUM = 'DateTimeOpened1';    
    Public Static final String CLOSEDCASE_DATETIMEOPENED2_HUM = 'DateTimeOpened2';    
    Public Static final String CLOSEDCASE_DATETIMEOPENED3_HUM = 'DateTimeOpened3';    
    Public Static final String CLOSEDCASE_PARTDDENIALDATE_HUM = 'Denial Date:'; 
    Public Static final String HUMDenialDate = 'DenialDate';    
    Public Static final String CLOSEDCASE_DENIALDATE1_HUM = 'DenialDate1';    
    Public Static final String CLOSEDCASE_DENIALDATE2_HUM = 'DenialDate2';    
    Public Static final String CLOSEDCASE_DENIALDATE3_HUM = 'DenialDate3';    
    Public Static final String CLOSEDCASE_PARTDDATEOFFILL_HUM = 'Date Of Fill:';    
    Public Static final String HUMDateOfFill = 'DateOfFill';
    Public Static final String CLOSEDCASE_DATEOFFILL1_HUM = 'DateOfFill1';
    Public Static final String CLOSEDCASE_DATEOFFILL2_HUM = 'DateOfFill2';
    Public Static final String CLOSEDCASE_DATEOFFILL3_HUM = 'DateOfFill3';
    Public Static final String CLOSEDCASE_PARTDREASONFORAPPEAL_HUM = 'Reason For Appealing Denial:';    
    Public Static final String CLOSEDCASE_REASONFORAPPEAL_HUM = 'ReasonForAppeal';
    Public Static final String CLOSEDCASE_REASONFORAPPEAL1_HUM = 'ReasonForAppeal1';
    Public Static final String CLOSEDCASE_REASONFORAPPEAL2_HUM = 'ReasonForAppeal2';
    Public Static final String CLOSEDCASE_REASONFORAPPEAL3_HUM = 'ReasonForAppeal3';
    Public Static final String CLOSEDCASE_PARTDSUPPORTINGDETAILS_HUM = 'Supporting Details';
    Public Static final String HUMSupportingDetails = 'SupportingDetail';
    Public Static final String CLOSEDCASE_SUPPORTINGDETAILS1_HUM = 'SupportingDetail1';
    Public Static final String CLOSEDCASE_SUPPORTINGDETAILS2_HUM = 'SupportingDetail2';
    Public Static final String CLOSEDCASE_SUPPORTINGDETAILS3_HUM = 'SupportingDetail3';
    Public Static final String HUMConstantcreatedByID = 'MXG0195';
    Public Static final String CLOSEDCASE_UNKNOWNAGENTBROKER_HUM = 'Unknown Agent/Broker';
    Public Static final String CLOSEDCASE_UNKNOWNAGENTBROKER2_HUM = 'UA';
    Public Static final String CLOSEDCASE_UNKNOWNGROUP_HUM = 'Unknown Group';
    Public Static final String CLOSEDCASE_UNKNOWNGROUP2_HUM = 'UE';
    Public Static final String CLOSEDCASE_UNKNOWNPROVIDER_HUM = 'Unknown Provider';
    Public Static final String CLOSEDCASE_UNKNOWNPROVIDER2_HUM = 'UP';
    Public Static final String CLOSEDCASE_UNKNOWNMEMBER_HUM = 'Unknown Member';
    Public Static final String CLOSEDCASE_UNKNOWNMEMBER2_HUM = 'UM';
    Public Static final String CLOSEDCASE_CCPCLASSIFICATIONTYPEENROLLMENTRSO_HUM = 'Enrollment (RSO)';  
    Public Static final String CLOSEDCASE_CCPCLASSIFICATIONELIGIBILITYRSOENR_HUM = 'Eligibility';
    Public Static final String CLOSEDCASE_CCPCLASSIFICATIONENROLLMENTRSOENR_HUM = 'Enrollment';
    Public Static final String CLOSEDCASE_CCPTERMCANCELMEMBER_HUM = 'Term/Cancel Member';
    Public Static final String THREEFOURSEVENFOUR_CCPGCPINQUIRY_HUM = '3474';
    Public Static final String THREEFOURSEVENFOURONE_CCPGCPINQUIRY_HUM = '3474_1';
    Public Static final String THREEFOURNINEZERO_CCPGCPINQUIRY_HUM = '3490';
    Public Static final String CLOSEDCASE_CCPLEPCC_HUM = 'Late Enrollment Penalty (LEP)/Creditable Coverage (CC)';
    Public Static final String HUMCCPCallsRSO = 'Calls (RSO)';  
    Public Static final String HUMCCPEligibilityRSO = 'Eligibility (RSO)';
    Public Static final String HUMCCPEnrollmentRSO = 'Enrollment (RSO)';
    Public Static final String CLOSEDCASE_CCPDISENROLLMENT_HUM = 'Disenrollment';
    Public Static final String CLOSEDCASE_CCPMEDICAREPARTD_HUM = 'Medicare Part D Redetermination Request';
    Public Static final String CLOSEDCASE_HUMFLOWMEDICAREEXPEDITEDDESC_HUM = 'Medicare/Medicaid Expedited Appeal';
    Public Static final String CLOSEDCASE_PARTDPHYSICIANNAME_HUM = 'Physician Name:';
    Public Static final String CLOSEDCASE_PHYSICIANSTATE_HUM = 'PhysicianState';
    Public Static final String CLOSEDCASE_PARTDPHYSICIANZIPCODE_HUM = 'Zip Code:';
    Public Static final String CLOSEDCASE_PHYSICIANZIPCODE_HUM = 'PhysicianZipCode';
    Public Static final String CLOSEDCASE_PARTDTELEPHONENO_HUM = 'Telephone Number:';
    Public Static final String CLOSEDCASE_PHYSICIANTELEPHONENO_HUM = 'PhysicianTelephoneNumber';
    Public Static final String CLOSEDCASE_PARTDEXT_HUM = 'Ext:';
    Public Static final String CLOSEDCASE_PHYSICIANEXT_HUM = 'PhysicianExt';
    Public Static final String CLOSEDCASE_PARTDFAXNUMBER_HUM = 'Fax Number:';
    Public Static final String CLOSEDCASE_PHYSICIANFAXNUMBER_HUM = 'PhysicianFaxNumber';
    Public Static final String CLOSEDCASE_PARTDADDITIONALINFO = 'Additional Info:';
    Public Static final String CLOSEDCASE_PHYSICIANADDITIONALINFO = 'PhysicianAdditionalInfo';
    Public Static final String CLOSEDCASE_DRUGNAME_HUM = 'DrugName';
    Public Static final String CLOSEDCASE_PARTDDRUGNAME_HUM = 'Drug Name:'; 
    Public Static final String CLOSEDCASE_CCPCREATEDBYID_HUM = 'MXG0195';
    Public Static final String CLOSEDCASE_MEDICARETEMPLATE_HUM = 'Medicare_Election_Lock_In_Template';
    Public Static final String MEMBER = 'Member';
    Public Static final String CLOSEDCASE_KNOWNMEMBER_HUM = 'KM';
    Public Static final String CLOSEDCASE_KNOWNGROUP_HUM = 'KE';
    Public Static final String CLOSEDCASE_KNOWNPROVIDER_HUM = 'KP';
    Public Static final String CLOSEDCASE_KNOWNAGENTBROKER_HUM = 'KA';
    Public Static final String CLOSEDCASE_GROUPTYPE_HUM= 'Group';
    Public Static final String PROVIDER_TYPE= 'Provider';
    Public Static final String EMPLOYER_TYPE= 'Employer';   
    Public Static final String CLOSEDCASE_AGENCY_HUM = 'Agency';
    Public Static final String CLOSEDCASE_BROKER_HUM = 'Broker';
    Public Static final String CLOSEDCASE_AGENTBROKER_HUM = 'Agent/Broker';
    Public Static final String MEMBERCAREGIVER = 'Member Representative or Caregiver';
    Public Static final String MEMBEREP = 'Member Representative';
    Public Static final String RELATIONTOMEMBERSELF = 'Self';
    Public Static final String INTERACTINGWITHTYPE_MEMBER = 'Member';
    Public Static final String CLOSEDCASE_INTABT_HUM = 'InteractingAbout';
    Public Static final String CLOSEDCASE_INTWITH_HUM = 'InteractingWith';
    Public Static final String CLOLSEDCASE_CCPGCPCASECOMMENT_HUM = 'This is a system generated message. Error with creating GCP inquiry, please contact help desk';
    Public Static final String CLOSEDCASE_AGENCYACCOUNT_HUM = 'A';
    Public Static final String CLOSEDCASE_BROKERACCOUNT_HUM = 'B';
    Public Static final String CLOSEDCASE_ZEROVALUE_HUM = '0';
    Public Static final String CLOSEDCASE_ZCCPINQRID_HUM = '0000000000';
    Public Static final String CLOSEDCASE_CCPINQUIRYSUBTYPE_HUM = 'MA';
    Public Static final String CLOSEDCASE_CCPGAINQUIRYTYPE_HUM = 'GI';
    Public Static final String CLOSEDCASE_CCPINTEGRATIONSTATUS = 'NI';
    Public Static final String CLOSEDCASE_CCPINQUIRYINTEGRATIONSTATUS_HUM = 'IN';   
    Public Static final String CLOSEDCASE_CCPGAMEDIATYPECODE_HUM = 'CR';
    Public Static final String CLOSEDCASE_CCPINQUIRYSUBTYPEEXP = 'EA';
    Public Static final String CLOSEDCASE_CCPINQUIRYTYPE = 'CI';
    Public Static final String CLOSEDCASE_CCPPRIORITYCODEWW_HUM = 'WW';
    Public Static final String CLOSEDCASE_CCPPRIORITYCODE_HUM = 'LL';
    Public Static final String HUMCasePriorityCriticial = 'Critical';
    Public Static final String CLOSEDCASE_CCPINDICATOR_HUM = 'O';
    Public Static final String CLOSEDCASE_CCPSTATUSCODE_HUM = 'OP';
    Public Static final String CLOSEDCASE_HUMCCPMEDIATYPECODE_HUM = 'OT';
    Public Static final String CLOSEDCASE_CONSTANTUSERSITENAME_HUM = 'Green Bay';
    Public Static final String CASESTATUS_HUM='In Progress';
    Public Static final String CLOSEDCASE_CCPGCPINQUIRYID_HUM = 'CCP/GCP Inquiry Id';
    Public Static final String CASEINFOSERVICE_ACCOUNTMEMBERTYPE_HUM = 'Member';
    Public Static final String CASEINFOSERVICE_ACCOUNTPROVIDERTYPE_HUM = 'Provider';
    Public Static final String CASEINFOSERVICE_ACCOUNTGROUPTYPE_HUM = 'Group';
    Public Static final String CASEINFOSERVICE_ACCOUNTAGENTTYPE_HUM = 'Agent';
    Public Static final String CLOSEDCASE_AGENTBROKER2_HUM = 'Agent_Broker';
    Public Static final String CASEINFOSERVICE_GETCASEINFOMANDATORYERROR_HUM = 'There are no records for the criteria provided';
    Public Static final String CASEINFOSERVICE_GETCASEINFOCASEIDERROR_HUM = 'There are no records present for this CaseId provided';
    Public Static final String CASEINFOSERVICE_GETCASEINFOINTERACTIONIDERROR_HUM = 'There are no records present for this Interaction';
    Public Static final String CASEINFOSERVICE_GETCASEINFOMANDATROYINFOERROR_HUM = 'Please provide the mandatory fields for retrieving the results';
    Public Static final String CASEINFOSERVICE_BOTH_HUM = 'both';
    Public Static final String CASEACTIONLAUNCHCONTROLLER_SPECIALITY_HUM = 'Specialty';
    Public Static final String CASEACTIONLAUNCHCONTROLLER_DENTAL_HUM = 'Dental';
    Public Static final String CASEACTIONLAUNCHCONTROLLER_Classification_HUM = 'Complaints or Compliments';
    Public Static final String CASEACTIONLAUNCHCONTROLLER_Intent_HUM = 'QAA';
    Public Static final String CASEACTIONLAUNCHCONTROLLER_MEDICAREDAC_HUM = 'Medicare DAC';
    Public Static final String CASEACTIONLAUNCHCONTROLLER_OBJECTACCOUNT_HUM = 'Account';
    Public Static final String CASEACTIONLAUNCHCONTROLLER_OBJECTPOLICYMEMBER_HUM = 'Policy_Member__c';
    Public Static final String CASEACTIONLAUNCHCONTROLLER_OBJECTPOLICY_HUM = 'Policy__C'; 
    Public Static final String CASEACTIONLAUNCHCONTROLLER_CONFIRM_HUM = 'yes';
    Public Static final String CASEINFOSERVICE_DATEVALUE_HUM='0001-01-01';
    Public Static final String ID_HUM = 'id';
    Public Static final String INTERACTIONID_HUM = '&interactionId=';
    Public Static final String ImageLoadDaysBuffer_HUM = '730';

    Public Static final String Keep_alive_HUM = 'keep-alive';

    Public Static final String MAX_DATE_HUM = '12/31/9999';
    Public Static final String MAX_SFDC_DATE_HUM = '12/31/4000';
    Public Static final String Mbr_HUM = 'Mbr';
    Public Static final String MED = 'MED';
    Public Static final String MEMBERID_HUM = 'MemberID';    
    Public Static final String MEMBER_RECORDTYPE_HUM = 'Member';    

    Public Static final String NAME_HUM = 'name';
    Public Static final String None = 'None';
    Public Static final Integer NUMBER_CONSTANT_ZERO_HUM = 0;
    Public Static final Integer NUMBER_CONSTANT_FIFTEEN_HUM = 15;
    Public Static final Integer NUMBER_CONSTANT_THIRTYTHREE_HUM = 33;
    Public Static final Integer NUMBER_CONSTANT_MINUSONE_HUM = -1; 
    Public Static final Integer NUMBER_CONSTANT_ONE_HUM = 1; 
    Public Static final Integer NUMBER_CONSTANT_TWO_HUM = 2;
    Public Static final Integer NUMBER_CONSTANT_FOUR_HUM = 4;
    Public Static final Integer NUMBER_CONSTANT_EIGHT_HUM = 8;    
    Public Static final Integer NUMBER_CONSTANT_HUNDRED_HUM = 100;
    Public Static final Integer NUMBER_CONSTANT_TWOHUNDRED_HUM = 200;
    Public Static final Integer NUMBER_CONSTANT_FIVE_HUM = 5;
    Public Static final Integer NUMBER_CONSTANT_SIX_HUM = 6;
    Public Static final Integer NUMBER_CONSTANT_THREE_HUM = 3;
    Public Static final Integer NUMBER_CONSTANT_SEVENTHOUSAND_HUM = 7000;
    Public Static final Integer NUMBER_MAXWORKTASK_HUM = 9999;
    Public Static final Integer NUMBER_TESTLIMIT_HUM = 50000;
    Public Static final Integer NUMBER_LASTLOGINDAYS_HUM = -60;
    Public Static final Integer NUMBER_CREATEDDATEDAYS_HUM = -60;

    Public Static final String OBJECT_ACCOUNT_HUM = 'Account';
    Public Static final String OBJECT_POLICYMEMBER_HUM = 'Policy_Member__c';
    Public Static final String OBJECT_MEMBERPLAN_HUM = 'MemberPlan';
    Public Static final String OBJECT_POLICY_HUM = 'Policy__C';
    Public Static final String OBJECT_MEMBERID_HUM ='Member_ID__c';

    Public Static final Integer PageSize_HUM = 5;
    Public Static final String PCP_PCD_PAGEINDICATOR_HUM = 'PCP/PCD';
    Public Static final String PLATFORMCODE_HUM  = 'EM';
    Public Static final String PRIMARYCARE_URL_HUM = '/apex/PrimaryCare_VF_HUM?id=';
    Public Static final String PAGEINDICATOR_HUM  = 'PageIndicator';
    Public Static final String PRODUCT_TYPE_HUM = '&ProductType=';
    Public Static final String PRODUCT_TYPE_CONS_HUM = 'ProductType';
    Public Static final String POLRELLIST_PRODUCTTYPE_HUM = 'VIS';
    Public Static final String PROCESS_RESP_CONS_HUM = 'processResponse';  

    Public Static final String REFERENCEDATE_HUM = '&refdate=';

    Public Static final String Sort_Expression_DESC_HUM = 'DESC';
    Public Static final String SORTDATE_HUM ='CreatedDate';
    Public Static final String Sort_Expression_ASC_HUM = 'ASC';    
    Public Static final Integer Service_Timeout_HUM  = 120000;
    Public Static final String SERVICESTARTDATE_HUM = 'SrvcStartDate'; 
    Public Static final String SrcMTCVPrefix_HUM  = '42'; 
    Public Static final String SOAPAction_HUM = 'SOAPAction';
    Public Static final String SUBTYPE_HUM = '&subtype='; 
    Public Static final String SUBTYPECONS_HUM = 'subtype';

    Public Static final String TYPE_HUM = '&Type=';
    Public Static final String TYPECONS_HUM = 'Type';  

    Public Static final String VOB_CERTIFICATE_PERS_HUM = '?pers=%20';
    Public Static final String VOB_CERTIFICATE_PERSV2_HUM = '?pers=';
    Public Static final String VOB_CERTIFICATE_PTFM_HUM = '&ptfm=';
    Public Static final String VOB_CERTIFICATE_PROD_HUM = '&prod=';
    Public Static final String VOB_CERTIFICATE_LOB_HUM = '&lob=';
    Public Static final String VOB_CERTIFICATE_SEQ_HUM = '&seq=';
    Public Static final String VOB_CERTIFICATE_CUST_HUM = '&cust=';
    Public Static final String VOB_CERTIFICATE_CCNBR_HUM = '&ccnbr=';
    Public Static final String VOB_CERTIFICATE_SUB_HUM = '&sub=';
    Public Static final String VOB_CERTIFICATE_ASOF_HUM = '&asof=';
    Public Static final String VOB_CERTIFICATE_PORT_HUM = '&port=22';
   // Added as part of REQ -241237 Used in DentalInformationRedirect_C_HUM class  
    Public Static final String VOB_DENTAL_CCPURL_HUM = 'CCPURL';
    Public Static final String VOB_DENTAL_CONTRACTID_HUM = '?ContrId=';
    Public Static final String VOB_DENTAL_TXTMBRID_HUM = '&MemId=';
    Public Static final String VOB_DENTAL_CRTDBY_HUM = '&CrtdBy=';
    Public Static final String VOB_DENTAL_REFERENCEANDINQUIRYID_HUM = '&txtCrtdTm=&RefId=&InqId=';
    Public Static final String VOB_DENTAL_TARGET_HUM = 'TARGET';
    Public Static final String VOB_DENTAL_ENV_HUM = '&Env=PROD1';


    //---------------------VOB  Accums HISTORY And CURRENT Constants-------------
    Public Static final String VOB_ACCUMS_MBECALLBACK_HUM ='processMBECallResponse';
    Public Static final String VOB_ACCUMS_MBESERVICE_HUM='MBEService';
    Public Static final String VOB_ACCUMS_DOLLAR_SIGN_HUM = '$';
    Public Static final String VOB_ACCUMS_DOLLAR_ZERO_SIGN_HUM = '$0.00';

//--------------------------REQ 246888------------------
    Public Static final String VOB_ACCUMS_SERVICE_HUM = 'AccumsService';
    Public Static final String VOB_ACCUMS_PREPARESERVICEDATA_HUM = 'prepareServiceData';
    Public Static final String VOB_TIME_FORMAT_HUM = 'T23:59:59';

// -------------------------REQ - 237144 -----------------------------------------------------------------------//
    Public Static final String VOB_ACCUMS_ZERODOTZERO_HUM = '0.00';
    Public Static final String VOB_ACCUMS_NOLIMIT_HUM = 'No Limit';

    Public Static final String  VOB_MEMBERMASTERCRITERIAPERSONID_HUM = 'MemberMasterCriteriaPersonId';
    Public Static final String  VOB_CURRENT_HUM = 'Current';
    Public Static final String  VOB_PAST_HUM = 'Past';
    Public Static final String  VOB_FUTURE_HUM = 'Future';
    Public Static final String  VOB_PLATFORM_MTV_HUM = 'MTV';
    Public Static final String  VOB_PLATFORM_CI_HUM = 'CI';
    Public Static final String  VOB_PLATFORM_CAS_HUM = 'CAS';
    Public Static final String  VOB_PLATFORM_EM_HUM = 'EM';
    Public Static final String  VOB_PLATFORM_LV_HUM = 'LV';
    Public Static final String  VOB_BENEFITPACKAGEID_HUM = 'BenefitPackageId';
    Public Static final String  VOB_VALUEPDQ_HUM = 'PDQ';
    Public Static final String  VOB_MASTERSERVICES_HUM = 'MasterServices';
    Public Static final String  VOB_GENERALBENEFITS_HUM = 'GeneralBenefits';
    Public Static final String  VOB_RIDERS_HUM = 'Riders';
    Public Static final String  VOB_PLANCOMMENTS_HUM = 'PlanComments';
    Public Static final String  VOB_RXDETAIL_HUM = 'RxDetail'; 
    Public Static final String  VOB_BENEFITS = 'BENEFITS';
    Public Static final String  VOB_GENKEY_KEYTYPE = 'ODS_GENKEY'; 
    Public Static final String  VOB_ACTIVEMEMBERSTATUS_HUM = 'Active';
    Public Static final String  VOB_BOTH = 'Both';  
    Public Static final String  VOB_MEDICARE ='RSO- Medicare';
    Public Static final String VOB_REMAPPURL_HUM = 'REMAPPURL';
    Public Static final String VOB_FAULT_CODE = 'faultcode';
    Public Static final String VOB_Response_HUM = 'parseMBE_GBE_PBEResponse';
    Public Static final String VOB_DATE_ERROR_HUM = 'Please utilize calendar for Date of Service selection';
    Public Static final Integer VOB_ServiceTimeOut_HUM = 120; 

    // -------------------------REQ - 224050 -----------------------------------------------------------------------//
    Public Static final String VOB_BENEFITSEARCH_SERVICELIST_HUM = 'Service';
    Public Static final String VOB_BENEFITSEARCH_LIMITATION_HUM = 'Limitation';
    Public Static final String VOB_BENEFITSEARCH_COINSURANCELIST_HUM = 'CoInsurance';
    Public Static final String VOB_BENEFITSEARCH_DEDUCTIBLELIST_HUM = 'Deductible';
    Public Static final String VOB_BENEFITSEARCH_MAXOUTOFPOCKETLIST_HUM = 'MaxOutOfPocket';
    Public Static final String VOB_BENEFITSEARCH_OUTOFPOCKETLIST_HUM = 'OutOfPocket';
    Public Static final String VOB_BENEFITSEARCH_COPAYOFFICEVISITLIST_HUM = 'CopayOfficeVisit';
    Public Static final String VOB_BENEFITSEARCH_SUPERMOOPLIST_HUM = 'SuperMoop';
    Public Static final String VOB_BENEFITSEARCH_INNETWORK_HUM = 'InNetwork';
    Public Static final String VOB_BENEFITSEARCH_OUTNETWORK_HUM = 'OutOfNetwork';
    Public Static final String VOB_BENEFITSEARCH_DOLLAR_HUM = 'Dollar';
    Public Static final String VOB_BENEFITSEARCH_PERCENTAGE_HUM = 'Percentage';
    Public Static final String VOB_ACCUMS_PERCENTAGE_SIGN_HUM = '%';
    Public Static final String VOB_PAR_NO_HUM = 'No';
    Public Static final String VOB_PAR_YES_HUM = 'Yes';
    Public Static final String VOB_PAR_DEFAULT_HUM = 'Par/Non Par/Both';
    Public Static final String VOB_BENEFITSEARCH_PROCESSCALL_HUM = 'processBenefitSearch';
    Public Static final String VOB_BENEFITURL_HUM = '/apex/Benefits_VF_HUM?';
    Public Static final String VOB_PBESERVICEDETAIL_HUM = 'ServicesWithPlaceOfService';
    Public Static final String VOB_BENEFITSEARCH_INNETWORKNODE_HUM = 'inNetworkNode';
    Public Static final String VOB_BENEFITSEARCH_OUTNETWORKNODE_HUM = 'outNetworkNode';
    Public Static final String VOB_BENEFITSEARCH_RXCOPAYMENT_HUM = 'Copayment';
    Public Static final String VOB_BENEFITSEARCH_RXDEDUCTIBLE_HUM = 'Deductible';
    Public Static final String VOB_BENEFITSEARCH_RXDETAIL_HUM = 'RxDetail';
    Public Static final String VOB_BENEFITSEARCH_RXAPPENDCOPAYMENT_HUM = 'RX_Copayment';
    Public Static final String VOB_BENEFITSEARCH_RXAPPENDDEDUCTIBLE_HUM = 'RX_Deductible';
    Public Static final String VOB_BENEFITSEARCH_RXINDIVIDUAL_HUM = 'Individual';
    Public Static final String VOB_BENEFITSEARCH_RXDUCTIBLEPHARMFAMILY_HUM = 'Deductible – Pharmacy Family';
    Public Static final String VOB_BENEFITSEARCH_RXCOPAAYPHARM_HUM = 'Copay – Pharmacy';
    Public Static final String VOB_BENEFITSEARCH_RXDUCTIBLEPHARM_HUM = 'Deductible – Pharmacy Individual';
    Public Static final String VOB_BENEFITSEARCH_RXCOVTYPEFAMILY_HUM = 'Family';
    //--------------------------------------- Start REQ- 244445---------------------------------// 
    //--------------------------------------- Start Active PCP for REQ- 270163---------------------------------//
    Public Static final String PCP_EPS_PROCESSRESMETHOD_HUM ='pcpActiveHistoryFinalResponse';
    Public Static final String PCP_EPS_PARONLY_HUM ='false';
    Public Static final String PCP_EPS_IDENTIFIERTYPE_HUM ='NpiID';
    Public Static final String PCP_EPS_REQ_DETAILS_HUM = 'EPSServiceV2_SearchProvider';
    Public Static final String PCP_EPS_ADDRESS_ALL_HUM = 'All';
    Public Static final String PCP_EPS_ADDRESS_SERVICE_HUM = 'Service';
    Public Static final String PCP_EPS_ADDRESS_BILLING_HUM = 'Billing';
    Public Static final String PCP_EPS_ADDRESS_MAIL_HUM = 'Mail';
    Public Static final String PCP_EPS_PHONE_HUM = 'phone';
    Public Static final String PCP_REF_DATE_HUM = 'refdate';
    Public Static final String PCP_APPLICATIONKEY_HUM = '28AD1338-9A7D-41DB-BB87-F9FDFEE3DA01';//'b35bc8f5-a390-4c91-8b5b-4327708550ec';
    Public Static final String PCP_SER_CONTENTTYPE_HUM = 'content-type';
    Public Static final String PCP_SER_CONECTION_HUM = 'Connection';
    Public Static final String PCP_SER_KEEPALIVE_HUM = 'keep-alive';
    Public Static final String PCP_SER_SOAPACTION_HUM = 'SOAPAction';
    Public Static final String PCP_MBESERVICENAME_HUM ='MBEService';
    Public Static final String PCP_MBEPLUSSERVICENAME_HUM = 'MBEPlusService';
    Public Static final String PCP_MEMCR_PERIOD_HUM = 'Current';
    Public Static final String PCP_MEMCR_TYPE_HUM = 'MemberMasterCriteriaPersonId';
    Public Static final String PCP_PROCESSRESMETHOD_HUM ='ProcessMbeResponse';
    //--------------------------------------- End Active PCP for REQ- 270163---------------------------------// 
    Public Static final String VOB_PCP_WARNINGMSG_HUM = 'History table reflects previous and future PCP’s/PCD\'s. Future PCP’s/PCD\'s assigned will show no end date'; 
    Public Static final String VOB_PCP_REFDATEFORMT_HUM ='yyyy-MM-dd';  
    //--------------------------------------- END REQ- 244445---------------------------------// 

    //--------------------------------------- Start REQ- 255151---------------------------------//

    Public Static final String CASECLONE_QUEUE_SEL_PAGE_HUM = '/apex/HUMQueueSelection';
    Public Static final String CASECLONE_NEXT_PAGE_HUM = 'nextPage';
    Public Static final String CASECLONE_OBJECT_TYPE_HUM = 'objectType';
    Public Static final String CASECLONE_REDIRECT_PAGE_HUM = 'HUMNewCaseRedirect';
    Public Static final String CASEEDIT_NONE_DISP_HUM = '---None--';
    Public Static final String CASEEDIT_VALIDATION_EXP_HUM = 'FIELD_CUSTOM_VALIDATION_EXCEPTION';
    Public Static final String CASEEDIT_STATUS_HUM = 'Status';
    Public Static final String CASEEDIT_CLOSED_HUM = 'Closed';  
    Public Static final String CASEEDIT_CANCELLED_HUM = 'Cancelled';  
    Public Static final String CASEEDIT_WORK_TASK_HUM = 'Work Task';
    Public Static final String CASEEDIT_UNKNOWN_MEMBER_HUM = 'Unknown Member';
    Public Static final String CASEEDIT_CLOSEERRORPAGE_HUM ='/apex/HUMCaseCloseErrorPage?id=';
    Public Static final String CASEEDIT_STATUSFORURL_HUM = '&Status=';
     Public Static final String CASEEDIT_CINOTAVAILABLE_HUM = 'NOTAVAILABLE';
      //--------------------------------------- END REQ- 255151---------------------------------// 
    //--------------------------------------- Start REQ- 270351---------------------------------// 
    Public Static final Integer TASKLISTVIEW_CASETASKPAGESIZE_HUM = 25;
    Public Static final Integer TASKLISTVIEW_HUMWORKQUEUESETUPLIMIT_HUM = 5000;
    Public Static final String TASKLISTVIEW_QUEUE_HUM = 'Queue';
    Public Static final String TASKLISTVIEW_REGULAR_HUM = 'regular';
    Public Static final String TASKLISTVIEW_INPROGRESS_HUM = 'In Progress';
    Public Static final String TASKLISTVIEW_PENDING_HUM = 'Pending';
    Public Static final String TASKLISTVIEW_PENDINGTASK_HUM = 'Pending - Task';
    Public Static final String TASKLISTVIEW_PENDINGRESPONSE_HUM = 'Pending - Response';
    Public Static final String TASKLISTVIEW_PENDINGTRANSFER_HUM = 'Pending - Transfer';
    Public Static final String TASKLISTVIEW_ALLOTHERVIEW_HUM = 'Other';
    Public Static final Integer TASKLISTVIEW_CASETASKQUERYLIMIT_HUM = 3500;
    Public Static final String TASKLISTVIEW_PRIORITYAGE_HUM = 'Priority&Age';
    Public Static final String TASKLISTVIEW_BOTH_HUM = 'Both';
    Public Static final String TASKLISTVIEW_ASSIGNED_HUM = 'Assigned to me';
    Public Static final String TASKLISTVIEW_ALL_HUM = 'All';
    Public Static final String TASKLISTVIEW_ERRORQUEUENOACCESS_HUM = 'You don\'t have access to Queues';
    Public Static final String TASKLISTVIEW_RECORDID_HUM = 'recordID';
    Public Static final String TASKLISTVIEW_OBJECTTASK_HUM = 'Task';
    Public Static final String TASKLISTVIEW_STRINGNUMERICZEROZEROFIVE_HUM  = '005';
    Public Static final String TASKLISTVIEW_ERRORTEXTCASERECLAIM_HUM = 'You can\'t claim this Case, it\'s already been claimed';
    Public Static final String TASKLISTVIEW_ERRORTEXTTASKRECLAIM_HUM = 'You can\'t claim this Task, it\'s already been claimed';
    Public Static final String TASKLISTVIEW_ERRORTEXTTASKROUTINGSELECT_HUM = 'Please select at least one record to Accept';
    Public Static final String TASKLISTVIEW_ERRORTASKNORECORD_HUM = 'Please select atleast one record to Accept';
    Public Static final String TASKLISTVIEW_SFDCCASEID_HUM = '500';
    Public Static final String TASKLISTVIEW_ERRORTASKCHANGEOWNER_HUM = 'Please select atleast one record to Change Owner';
    Public Static final String TASKLISTVIEW_OBJECTCASE_HUM = 'Case';
    Public Static final String TASKLISTVIEW_UNASSIGNED_HUM = 'UnAssigned';
    Public Static final String TASKLISTVIEW_VIEWOTHER_HUM = 'viewOther';
    Public Static final String TASKLISTVIEW_QUEUEALL_HUM = 'QueueAll';
    Public Static final String TASKLISTVIEW_QUEUEVIEW_HUM = 'QueueView';
    Public Static final String TASKLISTVIEW_ASSIGNEDALL_HUM = 'AssignedAll';
    Public Static final String TASKLISTVIEW_OTHER_HUM = 'Other';
    Public Static final String TASKLISTVIEW_AGE_HUM = 'Age';
    Public Static final String TASKLISTVIEW_DUEDATE_HUM = 'DueDate';
    Public Static final String TASKLISTVIEW_ID_HUM = 'Id';
    Public Static final String TASKLISTVIEW_PRIORITY_HUM = 'Priority';
    Public Static final String TASKLISTVIEW_PRIORITYANDAGEFORMATTED_HUM = 'Priority & Age';
    Public Static final String TASKLISTVIEW_CASES_HUM = 'Cases';
  Public Static final String TASKLISTVIEW_TASKS_HUM = 'Tasks';
  Public Static final String TASKLISTVIEW_SELECT_HUM = '--Select--';
  Public Static final String TASKLISTVIEW_DUESPACEDATE_HUM = 'Due Date';
  Public Static final String TASKLISTVIEW_AGECASESONLY_HUM = 'Age - Cases Only';
  Public Static final String TASKLISTVIEW_AGETASKSONLY_HUM = 'Age - Tasks Only';
  Public Static final String TASKLISTVIEW_PRIORITYONLY_HUM ='Priority';
  Public Static final String TASKLISTVIEW_PRIORITYTSKONLY_HUM ='Priority - Tasks Only';
  Public Static final String TASKLISTVIEW_PRIORITYCASEONLY_HUM ='Priority - Cases Only';
  Public Static final String TASKLISTVIEW_PRIORITYHIGHANDCRTCLONLY_HUM = 'Priority - Critical & High Only';
  Public Static final String TASKLISTVIEW_PRIORITYNORMALONLY_HUM = 'Priority - Normal Only';
  Public Static final String TASKLISTVIEW_PULLWORKVALIDATION_HUM = 'Please select a value in the Work Queue, View, and  Filter By fields prior to clicking the Pull Work button.';
  Public Static final String TASKLISTVIEW_PULLWORKNOITEMS_HUM = 'There are no work items left with the selected options. Please select new options and click the Pull Work button.';

  Public Static final String UNKNOWNACCOUNT_UNKNOWNAGENTRECORDTYPE_HUM = 'Unknown Agent/Broker';
    Public Static final String UNKNOWNACCOUNT_UNKNOWNGROUPRECORDTYPE_HUM = 'Unknown Group';
    Public Static final String UNKNOWNACCOUNT_UNKNOWNPROVIDERRECORDTYPE_HUM = 'Unknown Provider';
    Public Static final String UNKNOWNACCOUNT_UNKNOWNMEMBER_HUM = 'Unknown Member';
    Public Static final String UNKNOWNACCOUNT_UNKNOWNAGENTEXTID_HUM = 'UKA|00|SFDC|';
    Public Static final String UNKNOWNACCOUNT_UNKNOWNGROUPEXTID_HUM = 'UKG|00|SFDC|';
    Public Static final String UNKNOWNACCOUNT_UNKNOWNPROVIDEREXTID_HUM = 'UKP|00|SFDC|';
    Public Static final String UNKNOWNACCOUNT_UNKNOWNMEMBEREXTID_HUM = 'UKM|00|SFDC|';
    Public Static final String UNKNOWNACCOUNT_TENANTIDGEN_HUM = '00';
    Public Static final String UNKNOWNACCOUNT_ACCOUNTMEMBER_HUM = 'M';
    Public Static final String UNKNOWNACCOUNT_ACCOUNTPROVIDER_HUM = 'P';
    Public Static final String UNKNOWNACCOUNT_ACCOUNTGROUP_HUM = 'G';
    Public Static final String UNKNOWNACCOUNT_ACCOUNTAGENCY_HUM = 'A';
    Public Static final String UNKNOWNACCOUNT_UNKNOWNMEMBERNOTATION_HUM = 'M';
    Public Static final String UNKNOWNACCOUNT_CONSUMERIDFIELD_HUM = 'consumer_id__c';
    Public Static final Integer UNKNOWNACCOUNT_LISTINDEXZERO_HUM = 0;
    Public Static final String UNKNOWNACCOUNT_CONSUMERIDTAXID_HUM = 'TAXID';
    Public Static final String UNKNOWNACCOUNT_PIPECHARACTER_HUM = '|';

    Public Static final String UPDATETASKHELPER_CLOSED_HUM = 'Closed';
    Public Static final String UPDATETASKHELPER_COMPLETED_HUM = 'Completed';
    Public Static final String UPDATETASKHELPER_ALLOTHERVIEW_HUM = 'Other';
    Public Static final String UPDATETASKHELPER_OBJECTCASE_HUM = 'Case';

    Public Static final String TASKNEWBUTTON_WHOID_HUM = 'who_id';
    Public Static final String TASKNEWBUTTON_WHATID_HUM = 'what_id';
    Public Static final String TASKNEWBUTTON_RETURNURL_HUM = 'retURL';
    Public Static final String TASKNEWBUTTON_PARENTID_HUM = 'ParentTaskId';
    Public Static final String TASKNEWBUTTON_RECORDTYPE_HUM = 'RecordType';
    Public Static final String TASKNEWBUTTON_NO_OVERRIDE_EQUALS_ONE_HUM = '&nooverride=1';
    Public Static final String TASKNEWBUTTON_APEX_IN_URL_HUM = '/apex/';
    Public Static final String TASKNEWBUTTON_QUEUE_SELECTION_PAGE_HUM = 'HUMQueueSelection';
    Public Static final String TASKNEWBUTTON_OBJECTTYPE_HUM = 'objectType';
    Public Static final String TASKNEWBUTTON_NEXT_PAGE_HUM = 'HUMNextPage';
    Public Static final String TASKNEWBUTTON_TASK_EDIT_PAGE_HUM = 'HUMTaskEditPage';
    Public Static final String TASKNEWBUTTON_OVERRIDE_EQUALS_ONE_HUM = '&sfdc.override=1';
    Public Static final String TASKNEWBUTTON_OBJECT_TASK_HUM = 'Task';
    Public Static final String TASKTRIGGER_EXECUTION_HUM = 'QueueViewBatchSetup';   

    //--------------------------------------- End REQ- 270351---------------------------------// 

    //--------------------------------------- Start Defect 235634 HUMUpdateCaseHelper class---------------------------------// 

    Public Static final String CASETRANSFER_EQUALS_HUM = 'Equals';
    Public Static final String CASETRANSFER_NOT_EQUALS_HUM = 'Not Equals';
    Public Static final String CASETRANSFER_CL_ID_HUM ='classification_id__c';
    Public Static final String CASETRANSFER_INTENT_ID_HUM ='intent_id__c';
    Public Static final String CASETRANSFER_CL_HUM ='classification__c';
    Public Static final String CASETRANSFER_INTENT_HUM ='intent__c';

    //--------------------------------------- End Defect 235634 HUMUpdateCaseHelper class---------------------------------//
    //--------------------------------------- Start Defect 233190 AccountDetailR1_C_Hum class---------------------------------//
        Public Static final String SFDC_HELP_HOST = 'help.salesforce.com';
    //--------------------------------------- End Defect 233190 AccountDetailR1_C_Hum class---------------------------------//

    //---------------------------------------START live agent global constants ---------------------------------------//
    Public Static final String MEMBER_LIVEAGENT_HUM = 'Member';
    Public Static final String WEBCHAT_LIVEAGENT_HUM = 'Web Chat';
    //Added below constanst for US4609720
    Public Static final String ASYNC_WEB_CHAT = 'Async Web Chat';
    Public Static final String RCCWEBCHAT_LIVEAGENT_HUM = 'RCC Web Chat';
    Public Static final String CRYPTOKEY_LIVEAGENT_HUM = '78dd574bfdec4ef38042df8091c299c6';
    Public Static final String CRYPTOALGORITHM_LIVEAGENT_HUM = 'AES256';
    Public Static final String MEMGENKEY_LIVEAGENT_HUM = '43E8A2319A1A1D9401E892F200021394721BB8F3797889C33CE2743C710AD619A653FF5FDA5000545A6F077A05D0D23BF0723C79461335161F8B3C78B4DB152D';
    Public Static final String SESSIONID_LIVEAGENT_HUM = '6E6633CF97C837DE8788327F24651FF637D43C678770EEE8FCA41B9B21D6C8CA';
    //---------------------------------------END live agent global constants ---------------------------------------//
    //--------REQ - 265083-----------------------------------------------------------------------------------------------------------//
    Public Static final String CHATTERGROUPNAME_HUM = 'CRM Service Communication';
    Public Static final String CHATTERGROUPNAMETEST_HUM = 'Test CRM Service Communication';
    Public Static final String UNFOLLOWERRORMSG_HUM = 'You cannot unfollow the Broadcast group record!';  

     //---------------------------------------------------------------------Start  REQ - 252450 -----------------------------------------------------------------------------------------------------------//
     Public Static final String CASEMGMT_DATE_TIME_FORMAT = 'MM/dd/yyyy HH:mm a';
     Public Static final String CASEMGMT_PARENT_TASKID = 'parentTaskId';
     Public Static final String CASEMGMT_WHO_ID = 'who_id';
     Public Static final String CASEMGMT_WHAT_ID = 'what_id';
     Public Static final String CASEMGMT_RETURL = 'retURL';
     Public Static final String CASEMGMT_RECORDTYPE = 'RecordType';
     Public Static final String CASEMGMT_WHOID = 'whoid';
     Public Static final String CASEMGMT_WHATID = 'whatid';
     Public Static final String CASEMGMT_TASKEDITPAGE_HUM = 'HUMTaskEditPage';
     Public Static final String CASEMGMT_OVERRIDE_ONE = '&amp;nooverride=1';
     Public Static final String CASEMGMT_APEXIN_HUM = '/apex/';
     Public Static final String CASEMGMT_BME_PROFILE_HUM = 'BME User';
     Public Static final String CASEMGMT_NEW_TASK_HUM = 'New Task';
     Public Static final String CASEMGMT_TASK_EDIT_PAGE_HUM = 'Task Edit page';
     Public Static final String CASEMGMT_TASK_NUMBER_HUM = 'Task_Number';
     Public Static final String CASEMGMT_ERROR = 'Error';
     Public Static final String CASEMGMT_YESFOREDIT_HUM = 'YesForEdit';
     Public Static final String CASEMGMT_DESCRIPTION_HUM = 'Description';
     Public Static final String CASEMGMT_USERID_INITIAL_HUM = '005';
     Public Static final String CASEMGMT_GROUPID_INITIAL_HUM = '00G';
     Public Static final String CASEMGMT_OWNERID_HUM =  'ownerId';
     Public Static final String CASEMGMT_TASKID_HUM =  'taskId';

     //---------------------------------------------------------------------End  REQ - 252450 -----------------------------------------------------------------------------------------------------------//

    //----------------------------------------------------------------------Work Queue related constants-------------------------------------------------------------------------------------------------//
    Public Static final String WORKQUEUE_APP_ACTIVE_HUM = 'Active (Work Queue)';
    Public Static final String WORKQUEUE_APP_CRITERIA_HUM = 'Criteria_';
    Public Static final String WORKQUEUE_APP_HOMEOFFICEALLINDICATORALL_HUM = 'Home Office All';
    Public Static final String WORKQUEUE_APP_HOMEOFFICEINDICATORDENTAL_HUM = 'Home Office Dental'; 
    Public Static final String WORKQUEUE_APP_HOMEOFFICEINDICATORMEDICALPHARMACY_HUM = 'Home Office Medical/Pharmacy';
    Public Static final String WORKQUEUE_APP_HOMEOFFICEINDICATORMEDICALPHARMACYGENERAL_HUM = 'Humana General';  
    Public Static final String WORKQUEUE_APP_NONE_HUM = 'none';
    Public Static final String WORKQUEUE_APP_NONEPICKLIST_HUM = '--None--';
    Public Static final String WORKQUEUE_APP_PGQ_HUM = 'PGQ';
    Public Static final String WORKQUEUE_APP_PGQID_HUM = 'PGQ ID';
    Public Static final String WORKQUEUE_APP_PGQNAME_HUM = 'PGQ Name';
    Public Static final String WORKQUEUE_APP_PGV_HUM = 'PGV';
    Public Static final String WORKQUEUE_APP_PGVID_HUM = 'PGV ID';
    Public Static final String WORKQUEUE_APP_PGVNAME_HUM = 'PGV Name';
    Public Static final String WORKQUEUE_APP_PRIORITY_HUM = 'priority_';
    Public Static final String WORKQUEUE_ENHANCEMENT_CLOSED_HUM = 'Closed';
    Public Static final String WORKQUEUE_ENHANCEMENT_WHAT_ID_HUM = 'what_id';
    Public Static final String WORKQUEUE_ENHANCEMENT_NEWTASK_HUM = 'New Task';
    Public Static final String WORKQUEUE_ENHANCEMENT_RECORDTYPE_HUM = 'RecordType';
    Public Static final String WORKQUEUE_ENHANCEMENT_CCSPECIALISTPROFILE_HUM = 'Customer Care Specialist';
    Public Static final String WORKQUEUE_ENHANCEMENT_ID_HUM = 'Id';
    Public Static final String WORKQUEUE_ENHANCEMENT_SINGLESLASH_HUM = '/';
    Public Static final String WORKQUEUE_ENHANCEMENT_APEXINURL_HUM = '/apex/';
    Public Static final String WORKQUEUE_ENHANCEMENT_NEXTPAGE_HUM = 'nextPage';
    Public Static final String WORKQUEUE_ENHANCEMENT_OBJECTTYPE_HUM = 'objectType';
    Public Static final String WORKQUEUE_ENHANCEMENT_TASK_HUM = 'Task';
    Public Static final String WORKQUEUE_ENHANCEMENT_TASKNUMBER_HUM= 'Task_Number';
    Public Static final String WORKQUEUE_ENHANCEMENT_ACTIONTYPE_HUM = 'Error';
    Public Static final String WORKQUEUE_ENHANCEMENT_QUEUEYESFOREDIT_HUM = 'YesForEdit';
    Public Static final String WORKQUEUE_ENHANCEMENT_NONE_HUM = 'None';
    Public Static final String WORKQUEUE_ENHANCEMENT_SELECTTOPIC_HUM = 'Select a Topic';
    Public Static final String WORKQUEUE_ENHANCEMENT_WHATID_HUM = 'whatId';
    Public Static final String WORKQUEUE_ENHANCEMENT_CASECOUNT_HUM = 'casecount';
    Public Static final String WORKQUEUE_ENHANCEMENT_TASKCOUNT_HUM = 'taskcount';    
    Public Static final String WORKQUEUE_CASERVICEDESK_LOGINKEY_HUM = 'CRM Support';
    Public Static final String WORKQUEUE_CASERVICEDESK_LOGINVALUE_HUM = 'U#8WyGLD1M';
    Public Static final String WORKQUEUE_CASERVICEDESK_ATTRIB1_HUM = 'customer';
    Public Static final String WORKQUEUE_CASERVICEDESK_ATTRIB3_HUM = 'category';
    Public Static final String WORKQUEUE_CASERVICEDESK_ATTRIB4_HUM = 'pcat:402238';
    Public Static final String WORKQUEUE_CASERVICEDESK_ATTRIB5_HUM = 'group';
    Public Static final String WORKQUEUE_CASERVICEDESK_ATTRIB6_HUM = 'cnt:D43EA79626005349B453F49ED8F80D95';
    Public Static final String WORKQUEUE_CASERVICEDESK_ATTRIB7_HUM = 'urgency';
    Public Static final Integer WORKQUEUE_CASERVICEDESK_ATTRIB8_HUM = 2;
    Public Static final String WORKQUEUE_CASERVICEDESK_ATTRIB9_HUM = 'impact';
    Public Static final Integer WORKQUEUE_CASERVICEDESK_ATTRIB10_HUM = 3;
    Public Static final String WORKQUEUE_CASERVICEDESK_ATTRIB11_HUM = 'summary';
    Public Static final String WORKQUEUE_CASERVICEDESK_ATTRIB12_HUM = 'Creating ticket for the Errors occured in batch';
    Public Static final String WORKQUEUE_CASERVICEDESK_ATTRIB13_HUM = 'description';
    Public Static final String WORKQUEUE_CASERVICEDESK_ATTRIB15_HUM = 'type';
    Public Static final String WORKQUEUE_CASERVICEDESK_ATTRIB16_HUM = 'I';
    Public Static final String WORKQUEUE_CASERVICEDESK_PROP_HUM = 'test';
    Public Static final String WORKQUEUE_CASERVICEDESK_ATTRIB_HUM = 'type';
    Public Static final String WORKQUEUESETUP_OBJECT_HUM = 'Work_Queue_Setup__c';
    Public Static final String CUSTOMOBJECT_SUFFIX_HUM = '__c';
    Public Static final String HASH_HUM = '#';
    Public Static final String GROUP_REGULAR_HUM = 'Regular';
    Public Static final String GROUP_QUEUE_HUM = 'Queue';
    Public Static final String HUM_ALLOTHERVIEW = 'Other';
    Public Static final String QUEUEVIEW_ACTIVE_HUM = 'Active (Queue View)';
    Public Static final String QUEUEVIEW_QID_HUM = 'qId';
    Public Static final String QUEUEVIEW_WQID_HUM = 'wqId';     
    Public Static final String QUEUEVIEW_RETURL_HUM = 'retURL';
    Public Static final String QUEUEVIEW_EMPLOYERGROUPNAME_HUM = 'EmployerGroupName';
    Public Static final String QUEUEVIEW_COMPLAINTTYPE_HUM = 'ComplaintType';
    Public Static final String QUEUEVIEWITEM_TOPIC_HUM = 'Topic';
    Public Static final String QUEUEVIEWITEM_VFPAGE_HUM = '/apex/QueueViewItemCreation_VF_HUM?qId=';
    Public Static final String HUM_CLOSED = 'Closed';
    Public Static final String HUM_COMPLETED = 'Completed';
    Public Static final String HUM_CANCELLED = 'Cancelled';
    Public Static final String EQUALS_HUM = 'Equals';

    //---------------------------------------------------------------------Start  Defect - 225798 -----------------------------------------------------------------------------------------------------------//

    Public Static final String POLICYUPDATE_NONE_HUM = 'NONE'; 
    Public Static final String POLICYUPDATE_GRPSVC_CLIENT_NAME_HUM = 'CCP';
    Public Static final String POLICYUPDATE_UNINITIALIZED_VALUE_HUM = 'Uninitialized'; 
    Public Static final String POLICYUPDATE_VIEWALL_HUM = 'bViewAll';
    Public Static final String POLICYUPDATE_REFID_HUM = 'refId';
    Public Static final String POLICYUPDATE_FDATE_HUM= 'fDate';
    Public Static final String POLICYUPDATE_TDATE_HUM = 'tDate' ;
    Public Static final Integer POLICYUPDATE_FDATEVAL_HUM = 90;
   // ------------------------------------------------------------REQ - 282367--------------------------------------------------//

     Public Static final String CLOSEDCASE_CCPMEDICAREENROLLMENT_TEAMID_HUM ='2472';
     Public Static final String CLOSEDCASE_CCPCLASSIFICATION_TYPE_MEDICAREENROLLMENT_HUM = 'Enrollment (RSO)';
     Public Static final String CLOSEDCASE_CCPCLASSIFICATION_NAME_MEDICAREENROLLMENT_HUM ='Enrollment/Effective Date Correction';
     Public Static final String CLOSEDCASE_CCPINTENT_NAME_MEDICAREENROLLMENT_HUM ='Processed';
     Public Static final String CLOSEDCASE_CCPWORK_QUEUE_VIEW_NAME_NONSTATEHUM ='Non State Based Contracts Reconciliation';
     Public Static final String CLOSEDCASE_CCPWORK_QUEUE_VIEW_NAME_CMSHUM ='CMS Retro-Active Processing - Individual Medicare';


     //--------------------------------------------------------REQ - 270080 : EPSS Conversion- SOAP To REST--------------------------------------------------------------------------------------------------//
    Public Static final String INDIVIDUALTAG_PROVIDERSEARCH_HUM = 'Individual';
    Public Static final String GROUP_PROVIDERSEARCH_HUM = 'Group';
    Public Static final String FACILITYTAG_PROVIDERSEARCH_HUM = 'Facility';
    Public Static final String NPITAG_PROVIDERSEARCH_HUM = 'NpiID';
    Public Static final String TAXIDTAG_PROVIDERSEARCH_HUM = 'TaxID';
    Public Static final String PROVIDERUNIQUE_PROVIDERSEARCH_HUM = 'PRV';
    Public Static final String EXTMEMACCTTENANTID_PROVIDERSEARCH_HUM = '00';
    Public Static final String BILLINGADDRESSTYPE_PROVIDERSEARCH_HUM = 'Billing';
    Public Static final String SERVICEADDRESSTYPE_PROVIDERSEARCH_HUM = 'Service';
    Public Static final Integer TAXIDCOUNT_PROVIDERSEARCH_HUM = 3;
    Public Static final String RESTV1SERVICENAME_PROVIDERSEARCH_HUM = 'EPSServiceREST_V1';
    Public Static final Integer MINDIFF_PROVIDERSEARCH_HUM = 10000000;

    //----------------------------Start  REQ - 287463 --------------------------//
    Public Static final String HEADERROWEMPTYSTYLE_HUM = 'headerRowEmpty';
    Public Static final String HEADERROWALERTS_HUM = 'headerRowAlerts';
    //----------------------------END  REQ - 287463 --------------------------//

    Public Static final String POLICYMEMBER_CLAIMPAYMENT_METHOD_HUM = 'CHK';
    Public Static final String POLICYMEMBER_CLAIMPAYMENT_SEARCHTYPE_HUM = 'CLM_NO_TIN';
    
    //------------------------- Break fix for Medicare Exp Appeal ----------------------//
    
    Public Static final String CLOSEDCASE_MED_EXP_APPEAL_PROCESSDATA1_HUM = 'Next Steps'; 
    Public Static final String CLOSEDCASE_MED_EXP_APPEAL_PROCESSDATA2_HUM = 'Please close the CRM Case. No further action is required.';    
    Public Static final String CLOSEDCASE_MED_EXP_APPEAL_PROCESSDATA3_HUM = 'Overview:';
    
    Public Static final String CLOSEDCASE_MED_EXP_APPEAL_MEMBERNAME = 'Member\'s Name:';
    Public Static final String CLOSEDCASE_MED_EXP_APPEAL_MEMBERNAME_HUM = 'MemberFullName';
    Public Static final String CLOSEDCASE_MED_EXP_APPEAL_MEMBERID = 'Member ID #:';
    Public Static final String CLOSEDCASE_MED_EXP_APPEAL_MEMBERID_HUM = 'MemberId';
    Public Static final String CLOSEDCASE_MED_EXP_APPEAL_MEMBERTELEPHONENUMBER = 'Member\'s Telephone Number:';
    Public Static final String CLOSEDCASE_MED_EXP_APPEAL_MEMBERTELEPHONENUMBER_HUM = 'MemberCallersTelephoneNumber';
    
    Public Static final String CLOSEDCASE_MED_EXP_APPEALCALLER_MEMBER_INFORMATION_HUM = 'Caller(Member) Information:';
    Public Static final String CLOSEDCASE_MED_EXP_APPEAL_CALLER_APPEALDATE ='Appeal Date:';
    Public Static final String CLOSEDCASE_MED_EXP_APPEAL_CALLER_APPEALDATE_HUM='MemberAppealDate';
    Public Static final String CLOSEDCASE_MED_EXP_APPEAL_CALLER_APPEALTIME='Appeal Time:';
    Public Static final String CLOSEDCASE_MED_EXP_APPEAL_CALLER_APPEALTIME_HUM='MemberAppealTime';
    Public Static final String CLOSEDCASE_MED_EXP_APPEAL_CALLERNAME='Caller\'s Name:';
    Public Static final String CLOSEDCASE_MED_EXP_APPEAL_CALLERNAME_HUM='MemberCallersName';
    Public Static final String CLOSEDCASE_MED_EXP_APPEAL_CALLER_RELATIONSHIPTO_MEMBER='Relationship To Member:';
    Public Static final String CLOSEDCASE_MED_EXP_APPEAL_CALLERNAME_RELATIONSHIPTO_MEMBER_HUM='MemberRelationshipToMember';
    Public Static final String CLOSEDCASE_MED_EXP_APPEAL_CALLERTELEPHONENUMBER = 'Caller\'s Telephone Number:';
    Public Static final String CLOSEDCASE_MED_EXP_APPEAL_CALLERTELEPHONENUMBER_HUM = 'MemberHomePhone';
    Public Static final String CLOSEDCASE_MED_EXP_APPEAL_CALLERSTREETADDRESS = 'Caller\'s Street Address:';
    Public Static final String CLOSEDCASE_MED_EXP_APPEAL_CALLERSTREETADDRESS_HUM = 'MemberCallersStreetAddress';
    Public Static final String CLOSEDCASE_MED_EXP_APPEAL_CALLERCITY = 'Caller\'s City:';
    Public Static final String CLOSEDCASE_MED_EXP_APPEAL_CALLERCITY_HUM = 'MemberCallersCity';
    Public Static final String CLOSEDCASE_MED_EXP_APPEAL_CALLERSTATE = 'Caller\'s State:';
    Public Static final String CLOSEDCASE_MED_EXP_APPEAL_CALLERSTATE_HUM = 'MemberCallersState';
    Public Static final String CLOSEDCASE_MED_EXP_APPEAL_CALLERZIPCODE = 'Caller\'s Zip Code:';
    Public Static final String CLOSEDCASE_MED_EXP_APPEAL_CALLERZIPCODE_HUM = 'MemberCallersZipCode';
    
    Public Static final String CLOSEDCASE_MED_EXP_APPEALCALLER_OTHER_MEMBER_INFORMATION_HUM = 'Caller(Other) Information:';
    Public Static final String CLOSEDCASE_MED_EXP_APPEAL_CALLER_OTHER_APPEALDATE ='Appeal Date:';
    Public Static final String CLOSEDCASE_MED_EXP_APPEAL_CALLER_OTHER_APPEALDATE_HUM='CallerAppealDate';
    Public Static final String CLOSEDCASE_MED_EXP_APPEAL_CALLER_OTHER_APPEALTIME='Appeal Time:';
    Public Static final String CLOSEDCASE_MED_EXP_APPEAL_CALLER_OTHER_APPEALTIME_HUM='CallerAppealTime';
    Public Static final String CLOSEDCASE_MED_EXP_APPEAL_OTHER_CALLERNAME='Caller\'s Name:';
    Public Static final String CLOSEDCASE_MED_EXP_APPEAL_OTHER_CALLERNAME_HUM='CallerName';
    Public Static final String CLOSEDCASE_MED_EXP_APPEAL_OTHER_CALLER_RELATIONSHIPTO_MEMBER='Relationship To Member:';
    Public Static final String CLOSEDCASE_MED_EXP_APPEAL_OTHER_CALLERNAME_RELATIONSHIPTO_MEMBER_HUM='CallerRelationshipToMember';
    Public Static final String CLOSEDCASE_MED_EXP_APPEAL_OTHER_CALLERTELEPHONENUMBER = 'Caller\'s Telephone Number:';
    Public Static final String CLOSEDCASE_MED_EXP_APPEAL_OTHER_CALLERTELEPHONENUMBER_HUM = 'CallerTelephoneNumber';
    Public Static final String CLOSEDCASE_MED_EXP_APPEAL_OTHER_CALLERSTREETADDRESS = 'Caller\'s Street Address:';
    Public Static final String CLOSEDCASE_MED_EXP_APPEAL_OTHER_CALLERSTREETADDRESS_HUM = 'CallerStreetAddress';
    Public Static final String CLOSEDCASE_MED_EXP_APPEAL_OTHER_CALLERCITY = 'Caller\'s City:';
    Public Static final String CLOSEDCASE_MED_EXP_APPEAL_OTHER_CALLERCITY_HUM = 'CallerCity';
    Public Static final String CLOSEDCASE_MED_EXP_APPEAL_OTHER_CALLERSTATE = 'Caller\'s State:';
    Public Static final String CLOSEDCASE_MED_EXP_APPEAL_OTHER_CALLERSTATE_HUM = 'CallerState';
    Public Static final String CLOSEDCASE_MED_EXP_APPEAL_OTHER_CALLERZIPCODE = 'Caller\'s Zip Code:';
    Public Static final String CLOSEDCASE_MED_EXP_APPEAL_OTHER_CALLERZIPCODE_HUM = 'CallerPostalCode';
    
    Public Static final String CLOSEDCASE_MED_EXP_APPEALDRUGINFORMATION_HUM = 'Drug Or Service Information:';
    
    Public Static final String CLOSEDCASE_MED_EXP_APPEAL_DRUGNAME_HUM = 'Drug Name/Dosage OR Service:';
    Public Static final String CLOSEDCASE_MED_EXP_APPEAL_DRUGNAME1_HUM = 'DrugDosageService';
    Public Static final String CLOSEDCASE_MED_EXP_APPEAL_DRUGNAME2_HUM = 'DrugDosageService2';
    Public Static final String CLOSEDCASE_MED_EXP_APPEAL_DRUGNAME3_HUM = 'DrugDosageService3';
    Public Static final String CLOSEDCASE_MED_EXP_APPEAL_DRUGNAME4_HUM = 'DrugDosageService4'; 
    
    Public Static final String CLOSEDCASE_MED_EXP_APPEAL_EOC_CGX_NUMBER_HUM = 'EOC or CGX Number:'; 
    Public Static final String CLOSEDCASE_MED_EXP_APPEAL_EOC_CGX_NUMBER1_HUM = 'EOCCCANumber';    
    Public Static final String CLOSEDCASE_MED_EXP_APPEAL_EOC_CGX_NUMBER2_HUM = 'EOCCCANumber2';    
    Public Static final String CLOSEDCASE_MED_EXP_APPEAL_EOC_CGX_NUMBER3_HUM = 'EOCCCANumber3';    
    Public Static final String CLOSEDCASE_MED_EXP_APPEAL_EOC_CGX_NUMBER4_HUM = 'EOCCCANumber4'; 
       
    Public Static final String CLOSEDCASE_MED_EXP_APPEAL_DENIALDATE_HUM = 'Denial Date:'; 
       
    Public Static final String CLOSEDCASE_MED_EXP_APPEAL_DENIALDATE1_HUM = 'DenialDate';    
    Public Static final String CLOSEDCASE_MED_EXP_APPEAL_DENIALDATE2_HUM = 'DenialDate2';    
    Public Static final String CLOSEDCASE_MED_EXP_APPEAL_DENIALDATE3_HUM = 'DenialDate3';    
    Public Static final String CLOSEDCASE_MED_EXP_APPEAL_DENIALDATE4_HUM = 'DenialDate4'; 
     
    
    Public Static final String CLOSEDCASE_MED_EXP_APPEALPHYSICIANINFORMATION_HUM = 'Physician’s Information:';
    
    Public Static final String CLOSEDCASE_MED_EXP_APPEAL_PHYSICIANNAME = 'Physician Name:';
    Public Static final String CLOSEDCASE_MED_EXP_APPEAL_PHYSICIANNAME_HUM = 'PhysicianName';
    Public Static final String CLOSEDCASE_MED_EXP_APPEAL_PHYSICIANTELEPHONENUMBER = 'Telephone Number:';
    Public Static final String CLOSEDCASE_MED_EXP_APPEAL_PHYSICIANTELEPHONENUMBER_HUM = 'PhysicianNumber';
    Public Static final String CLOSEDCASE_MED_EXP_APPEAL_PHYSICIANFAXNUMBER = 'Fax Number:';
    Public Static final String CLOSEDCASE_MED_EXP_APPEAL_PHYSICIANFAXNUMBER_HUM = 'PhysicianFax';
    Public Static final String CLOSEDCASE_MED_EXP_APPEAL_PHYSICIANSTREETADDRESS = 'Street Address:';
    Public Static final String CLOSEDCASE_MED_EXP_APPEAL_PHYSICIANSTREETADDRESS_HUM = 'PhysicianStreetAddress';
    Public Static final String CLOSEDCASE_MED_EXP_APPEAL_PHYSICIANCITY = 'City:';
    Public Static final String CLOSEDCASE_MED_EXP_APPEAL_PHYSICIANCITY_HUM = 'PhysicianCity';
    Public Static final String CLOSEDCASE_MED_EXP_APPEAL_PHYSICIANSTATE = 'State:';
    Public Static final String CLOSEDCASE_MED_EXP_APPEAL_PHYSICIANSTATE_HUM = 'PhysicianState';
    Public Static final String CLOSEDCASE_MED_EXP_APPEAL_PHYSICIANZIPCODE = 'Zip Code:';
    Public Static final String CLOSEDCASE_MED_EXP_APPEAL_PHYSICIANZIPCODE_HUM = 'PhysicianZipCode';
     
    Public Static final String CLOSEDCASE_MED_EXP_APPEAL_SUPPORTINGINFO_FOR_APPEAL = 'Supporting Information for the Appeal:';
    Public Static final String CLOSEDCASE_MED_EXP_APPEAL_REASON_FOR_APPEALDENIAL = 'Reason for appealing the denial:';  
    Public Static final String CLOSEDCASE_MED_EXP_APPEAL_REASON_FOR_APPEALDENIAL_HUM = 'ReasonForAppealingDenial';
    
    Public Static final String CLOSEDCASE_MED_EXP_APPEAL_CLINICAL_REASON_FOR_DISAGREEING_DENIAL = 'Clinical reasons for disagreeing with the denial:';  
    Public Static final String CLOSEDCASE_MED_EXP_APPEAL_CLINICAL_REASON_FOR_DISAGREEING_DENIAL_HUM = 'ClinicalReasonForDisagreeingDenial';
    
    Public Static final String CLOSEDCASE_MED_EXP_APPEAL_RATIONALE_STANDARD_TIMEFRAME = 'Rationale as to why the standard timeframe would endanger the member\'s health:';  
    Public Static final String CLOSEDCASE_MED_EXP_APPEAL_RATIONALE_STANDARD_TIMEFRAME_HUM = 'Rationale';
    
    Public Static final String CLOSEDCASE_MED_EXP_APPEAL_DIAGNOSIS_MEANING = 'Diagnosis and its meaning:';
    Public Static final String CLOSEDCASE_MED_EXP_APPEAL_DIAGNOSIS_MEANING_HUM = 'DiagnosisAndMeaning';
    
    Public Static final String CLOSEDCASE_MED_EXP_APPEAL_ADDITIONAL_CLINICAL_INFO = 'Additional Clinical Information:';
    Public Static final String CLOSEDCASE_MED_EXP_APPEAL_ADDITIONAL_CLINICAL_INFO_HUM = 'AdditionalClinicalInformation';
    
    
    //--------------------------Break fix end--------------------------------------------//

    //----------------------------Power Of Attorney --------------------------//
    Public Static final String POA_FIELDLIST = 'First Name;Last Name;Phone;POA Type';
    Public Static final String POA_VALIDMEMBERTYPES = 'Member-Id-Base;';
    Public Static final String POA_VALIDPLATFORMS = 'LV;';
    Public Static final String POA_DATEFORMAT = 'MM/dd/YYYY';
    Public Static final String POA_CONSTRECORD = 'Record';
    Public Static final String POA_SERVICENAME = 'CIMedicareMultipleMemberDetails';
    
     //----------------------------Start  REQ - 290027 --------------------------//
    
    Public Static final String MILESTONEDETAIL_VFPAGE_HUM = '/apex/METMileStoneDetail_VF_HUM';
    Public Static final Integer CONSTANT_10_HUM = 10;
    Public Static final String OBJECTNAME_TASK_HUM = 'Task';
    Public Static final String OBJECTNAME_ACTION_HUM = 'Action';
    Public Static final String OBJECTNAME_SOURCE_HUM = 'Source';
    Public Static final String MILESTONEID_VALIDATION_HUM = '[0-9]+';
    //----------------------------END  REQ - 290027 --------------------------// 
    Public Static final String HUM_GETORDERRES = 'parseRSGetOrderResponse';    
    
    //---------------------Req 296752----------------------------------------
    Public Static final String HUM_GETSCRIPTS= 'parseRSGetScriptResponse';
    //----------------------END----------------------------------------------
    
    //---------------------Req 296651----------------------------------------
    Public Static final String HUM_CREATEEDITORDER= 'parseCreateEditOrderResponse';
    Public Static final String HUM_LOGNOTERESPONSE= 'logNoteResponse';
    //----------------------END----------------------------------------------
    
    Public Static final Integer MINUS90 = -90;
    Public Static final String WORKQUEUEMSG = 'Please select a Work Queue before creating or editing a Case';
        
    Public Static final String HUM_CLOSEDMEMBERCASE = 'Closed Member Case'; 
    Public Static final String HUM_CLOSEDPROVIDERCASE = 'Closed Provider Case'; 
    Public Static final String HUM_CLOSEDAGENTBROKERCASE = 'Closed Agent/Broker Case'; 
    Public Static final String HUM_CLOSEDGROUPCASE = 'Closed Group Case'; 
    Public Static final String HUM_CLOSEDUNKNOWNCASE = 'Closed Unknown Case';
  
    
     //----------------------------Start  REQ - 300070 --------------------------//
     Public Static final String CUSTOM_LINKS_HIDDEN_TARGET_HUM='HIDDENTARGET';
     Public Static final String CUSTOM_LINKS_HORIZONTAL_HUM='Horizontal';

    //----------------------------End  REQ - 300070 --------------------------//
    
    Public Static final String PHARMACY_EDITMEMBER_HUM='retrieveUpdatedGetMember';
    Public Static final Integer PHARMACY_STARTMONTH_HUM = -3;
    Public Static final String HUMADDRESSERROR='CONFIRM';
    //--------------------------------------- Start constants for RightSourceService ------------------------------- //
    Public Static final String HUM_RSGETMEMBERCREDITCARD_RESPONSE = 'parseRSGetMemberCreditResponse'; 
    Public Static final String HUM_RSGETADJPAY_RESPONSE = 'parseGetAdjPayResponse'; 
    //--------------------------------------- End constants for RightSourceService ------------------------------- //

    //------------------Privacy Requirements------------------------------//
    
    Public Static final String GO365KIDSDEL_SERVICENAME = 'MemberDelegatesService';
    Public Static final String GO365KIDSDEL_PERMITTEDUSER = 'permitted_user';
    Public Static final String GO365KIDSDEL_DATAOWNER = 'data_owner';
    
    //Privacy Requirements
    
    //Alerts - START
    Public Static final String MEMBER_ALERTS_HUM = 'Member';
    Public Static final String TYPE_MEMBER_ALERTS_HUM = 'M';
    Public Static final String GI_ALERTS_HUM = 'GI';
    Public Static final String GM_ALERTS_HUM = 'GM';
    Public Static final String IN_ALERTS_HUM = 'IN';
    Public Static final String MD_ALERTS_HUM = 'MD';
    Public Static final String ZERO_ALERTS_HUM = '0';
    Public Static final String CRDTYPE_ALERTS_HUM = '01';
    Public Static final String BLANK_ALERTS_HUM = '';
    Public Static final String INQUIRYID_ALERTS_HUM = '000';
    Public Static final String INTERACT_ALERTS_HUM = 'Interact';
    Public Static final String INTERACTIONPOINT_ALERTS_HUM = 'CSCGuideAlerts_IP1';
    Public Static final String CALLBACK_ALERTS_HUM = 'callbackAlerts';
    Public Static final String FAULTCODE_ALERTS_HUM = 'faultcode';
    Public Static final String GETOFFERSSESSIONID_ALERTS_HUM = 'local.GetOffersSessionId.';
    Public Static final String OF_ALERTS_HUM = 'of';
    Public Static final String SALERTS_ALERTS_HUM = 'Alert';
    Public Static final String ALERTS_PERMISSION = 'NBA_Alerts_V2';
    Public Static final String INTERACTING_WITH_TYPE_MEMBER = 'Member';
    Public Static final String INTERACTING_WITH_TYPE_MEMBER_REP_CAREGIVER = 'Member Representative or Caregiver';
    Public Static final String RECORD_TYPE_UNKNOWN_MEMBER = 'Unknown Member';
    Public Static final String STRING_TRUE = 'true';
    Public Static final String STRING_FLASE = 'false';
    Public Static final String SOURCE_INTERACT = 'Interact';        

    
    //Alerts - END
    
     Public Static final String CASE_QAATEMPLATE_PROVIDERFACILITYQUESTION = 'Is this for a Provider or a Facility/Group?';
    Public Static final String CASE_QAATEMPLATE_PROVIDERACCESSIBILITYQUESTION ='Is any part of the complaint related to accessibility care issues?';
    Public Static final String CASE_QAATEMPLATE_ENHANCEDQAAACCESS = 'Enhanced_QAA_Template';
    Public Static final String CASE_PROVIDERSEARCH_LASTNAME = 'sLastName';
    Public Static final String CASE_PROVIDERSEARCH_TAXID = 'sTaxID';
    Public Static final String CASE_PROVIDERSEARCH_NPI = 'sNPI';
    Public Static final String CASE_PROVIDERSEARCH_FIRSTNAME = 'sFirstName';
    Public Static final String CASE_PROVIDERSEARCH_FACILITYNAME = 'sFacilityName';
    Public Static final String CASE_PROVIDERSEARCH_ADDRESS = 'sAddress';
    Public Static final String CASE_PROVIDERSEARCH_CITY = 'sCity';
    Public Static final String CASE_PROVIDERSEARCH_POSTALCODE = 'sPostalCode';
    Public Static final String CASE_PROVIDERSEARCH_STATE = 'sState';
    Public Static final String CASE_PROVIDERSEARCH_STATEDEFAULT = 'Select a Value';
    Public Static final Integer CASE_PROVIDERSEARCH_TAXIDLENGTH = 9;
    Public Static final String CASE_PROVIDERSEARCH_ALPHANUMERIC = '^[a-zA-Z0-9-.\' , &]*$';
    Public Static final String CASE_PROVIDERSEARCH_NUMERICPATTERN = '^[0-9]{5,5}+$';
    Public Static final String CASE_PROVIDERSEARCH_QAACLASS = 'QAA_Field';
    Public Static final String CASE_FACILITYGROUPTYPE = 'Facility/Group';
    Public Static final String CASE_PROVIDERWHOISSUBMITTING = 'Who is submitting the complaint?';
    Public Static final String CASE_PROVIDERVERBALCOMPLAINT = 'Is the Member willing to provide their QAA Complaint details verbally?';
    Public Static final String CASE_PROVIDERPARENTQUESTIONCLASS = 'QAA_Field_ParentQuestion';
    Public Static final String CASE_PROVIDERNAME = 'Provider Name';
    Public Static final String CASE_PROVIDERDBA = 'Provider DBA';   
    
    //-------------------------------Start REQ - 303466, REQ - 301582 ----------------------------//
    Public Static Map<String, Set<String>> MEDICARE_LOB_PRODUCTTYPE_LIST()
    {
       Map<String, Set<String>> medicareLOBProductTypeList= New Map<String,Set<String>>{                                                                          
                                                                             'MEF' => new Set<String>{'MA', 'MAPD'},
                                                                             'MEP' => new Set<String>{'MA', 'MAPD'},
                                                                             'MER' => new Set<String>{'MA', 'MAPD','Empty'},
                                                                             'MGP' => new Set<String>{'MA', 'MAPD'},
                                                                             'MGR' => new Set<String>{'MA', 'MAPD'},
                                                                             'MPD' => new Set<String>{'PDP'},
                                                                             'MRO' => new Set<String>{'MAPD'},
                                                                             'PDP' => new Set<String>{'PDP'}
                                                                            };
      Return medicareLOBProductTypeList;                                                                            
    }
   
    Public Static final String PRODUCT_TYPE_MAPD = 'MAPD';
    Public Static final String PRODUCT_TYPE_MA = 'MA';
    Public Static final String PRODUCT_TYPE_PDP = 'PDP';
    Public Static final String MAJOR_LOB_MCD = 'MCD';
    Public Static final String MAJOR_LOB_MER = 'MER';
    Public Static final String MAJOR_LOB_MES = 'MES';
    Public Static final String SEGMENT_TYPE_MCD = 'MCD';
    Public Static final String SEGMENT_TYPE_MCR = 'MCR';
    Public Static final String MEDICARE_POLICY = 'MED';    
    
    //------------------REQ - 290967, 290969------------------------------//
   
    
    //REQ - 290967, 290969 - End
    
    //-----------------SaveOffers Implementation----------------------------//
    Public Static final String SAVEOFFERS_NOTDELIVERED = 'NotDelivered';
    Public Static final String SAVEOFFERS_DELIVERED = 'Delivered';
    Public Static final String SAVEOFFERS_TERMED = 'Termed';
    Public Static final String SAVEOFFERS_CLICKED = 'Clicked';
    Public Static final String SAVEOFFERS_VIEWED = 'Viewed';
    Public Static final String SAVEOFFERS_ACCTYPE = 'M';
    Public Static final String SAVEOFFERS_MEMBER = 'Member';
    Public Static final String SAVEOFFERS_INTERACT = 'Interact';
    //SaveOffers Implementation -End
    
    Public Static final String HUM_GETORDERDETAILRES = 'parseRSGetOrderDetailResponse'; 
    Public Static final String PHAR_PROCESSCHILDORDER_HUM = 'processChildOrder';
    Public Static final String PHAR_PROCESSCHILDORDERDETAILS_HUM = 'processChildOrderDetails';
    
    Public Static final String PHARMACY_GETEDITMEMBER_HUM = 'retrieveEditMember';
    
    // REQ - 301392
    
    Public Static Final String AUTOROUTING_EVALUATE = 'Evaluate';
    Public Static Final String AUTOROUTING_COMPLETED = 'Completed';
    
    // End for REQ - 301392

    Public Static final String DNC_PHARMACY_HUM = 'DNC';
    Public Static Set<String> MEDICARE_SET = New Set<String>{'MCD','MEF','MEP','MER','MES','MGP','MGR','MPD','MRO','MRP','MSP','PDP','MAPD'};

    //----------------------------Start REQ - 296721 --------------------------//
    Public Static final String PHARMACY_MEMBERCONSENT_ENDDATE_HUM = '01/01/1970'; 
    Public Static final String PHARMACY_MEMBERCONSENT_LOGNOTECODE_HUM = 'CONSENT'; 
    Public Static final String PHARMACY_MEMBERCONSENT_LOGNOTEMESSAGEFIRSTHALF_HUM ='Member consent given on ';
    Public Static final String PHARMACY_MEMBERCONSENT_LOGNOTEMESSAGESECONDHALF_HUM = ' est. Consent will expire in 365 days (Note EPOST WILL HANDLE CONSENT EXPIRATION)';
    Public Static final String PHARMACY_MEMBERCONSENT_LOGNOTEREQUESTFOREACHORDERFIRSTHALF_HUM ='Member changed to Request Consent on Each Order on ';
    Public Static final String PHARMACY_MEMBERCONSENT_LOGNOTEREQUESTFOREACHORDERSECONDHALF_HUM = ' est.';
    Public Static final String PHARMACY_MEMBERCONSENT_LOGNOTEAT_HUM = ' at ';
    //----------------------------End  REQ - 296721 --------------------------//
    //RTI
    Public Static final String RTIService_SUMMARY_HUM = 'RTIService_GetInteractions';
    Public Static final String RTIService_Summary_HUM_V2 = 'RTIService_GetInteractions_V2';
    Public Static final String RTIServicePrint_SUMMARY_HUM = 'RTIService_GetInteractionDetails';
    Public Static final String RTIService_GetInteractionDetails_V2 = 'RTIService_GetInteractionDetails_V2';
    Public Static final String RTIChannelAll = 'All';
    Public Static final String RTIChannelIVR = 'IVR';
    Public Static final String RTIChannelIVRCode = 'IV'; 
    Public Static final String RTIchannelPrint = 'PRINT';
    Public Static final String RTIchannelWB = 'WB';
    Public Static final String RTIchannelMB = 'MB';
    Public Static final String RTIchannelMA = 'MA'; 
    Public Static final String RTIChannelVAT = 'VAT';
    Public Static final String RTISecurity = 'Home Office/General';  
    Public Static final String sChannelCode  = 'PRINT';
    Public Static final String sNoRecords  = 'No Records';
    Public Static final String sServiceError = 'Service Error';  

    //----------------------------Start REQ - 301358 --------------------------//
    Public Static final String PHARMACY_FINREQUESTROW1_HUM = 'Member Name'; 
    Public Static final String PHARMACY_FINREQUESTROW2_HUM = 'Humana Id Number'; 
    Public Static final String PHARMACY_FINREQUESTROW3_HUM = 'Account  Number'; 
    Public Static final String PHARMACY_FINREQUESTROW4_HUM = 'Order Number'; 
    Public Static final String PHARMACY_FINREQUESTROW5_HUM = 'Rx# and Name'; 
    Public Static final String PHARMACY_FINREQUESTROW6_HUM = 'Request/Concern'; 
    Public Static final String PHARMACY_FINREQUESTROW7_HUM = 'Submitted By'; 
    
    Public Static final String PHARMACY_FINREQUESTBEGINTABLE_HUM = '<table border ="1" width="60%"><tbody>';
    Public Static final String PHARMACY_FINREQUESTBODY1_HUM = '<tr><td>&nbsp;';
    Public Static final String PHARMACY_FINREQUESTBODY2_HUM = '</td><td >&nbsp';
    Public Static final String PHARMACY_FINREQUETSBODY3_HUM = '</td></tr>';
    Public Static final String PHARMACY_FINREQUESTENDTABLE_HUM = '</tbody></table>';
    
    Public Static final String PHARMACY_FINREQUESTSUBJECT_HUM = 'Traditional Mail- Member Invoice Copy/ Reimbursement Requests';
    Public Static final String PHARMACY_FINREQUESTEMAIL_HUM = 'HumanaPharmacyFinance@humana.com';
    
    //----------------------------End REQ - 301358 --------------------------//
    
    Public Static final String ELIGIBILITY_GETMEMBER_HUM = 'retrieveMTMValue';
    
     //-------------------------------Start REQ - 296939, REQ - 298771 ----------------------------//
    
    Public Static final String MEDICARE_CASE = 'Medicare Case';
    Public Static final String CLOSED_MEDICARE_CASE = 'Closed Medicare Case';
    Public Static final String ETL_API_ACCESS = 'ETL API Access';

    //-----REQ-320516
    Public Static final String DEV_MEDICARE_CASE = 'Medicare_Case';
    Public Static final String DEV_CLOSED_MEDICARE_CASE = 'Closed_Medicare_Case';
    //------REQ-320516
                //----------------------------Start REQ - 305689 --------------------------//
    Public Static final String PHARMACY_PROCESS_ORD_RES_HUM = 'ProcessOrderResponse';
    Public Static final String PHARMACY_SUMMARY_HUM = 'Summary';
    Public Static final String PHARMACY_GETORDER_RES_HUM = 'processGetOrderResponse';
    Public Static final String PHARMACY_RELORDER_RES_HUM = 'processRelOrderResponse';
    Public Static final String PHARMACY_TRAD_HUM = 'trad';
    Public Static final String PHARMACY_ZIP_CITY_ERROR =  'IncompatibleZipCity';
    //----------------------------End REQ - 305689 --------------------------//
    Public Static final String PHARMACYSPECIALIST_PROFILE_HUM = 'Humana Pharmacy Specialist';
    Public Static final String PHARMACYSUPERVISOR_PERMISSION_HUM = 'CRMS_300_Humana_Pharmacy_Supervisor';
    Public Static final String NONPHARMACY_USER_HUM = 'No';
    Public Static final String PHARMACYSUPERVISOR_USER_HUM = 'Yes - Supervisor';
    Public Static final String PHARMACYSPECIALIST_USER_HUM = 'Yes - Specialist';
    Public Static final List<String> PHARMACY_LOGNOTECODE_HUM = New List<String>{'1X CHG','ALLCNL','CCALL','CLOSE','COPALL','EZC','MCDENY','POAUNI','REFILL','RXSTP','SHCH','SUP','TEMP','TRACK','TRANS'};
    //----------------------------Start REQ - 367015 --------------------------//    
    Public Static final String PHARMACYInsuranceData_PERMISSION_HUM = 'CRMS_301_HPInsuranceData';
    //----------------------------End REQ - 367015 --------------------------//
    //----------------------------Start REQ - 303396 --------------------------//
    
    Public Static final String PHARMACY_LIS_HUM ='Low Income Subsidy (LIS)';
    //----------------------------End REQ - 303396 --------------------------//
    Public Static final String INQUIRY_FILTERTYPE_HUM = 'CRD_TYPE_DESC';
    Public Static final String INQUIRY_NONPHARMACYFILTERVALUE_HUM = 'Non-RightSource CRD Type';
    Public Static final String INQUIRY_PHARMACYFILTERVALUE_HUM = 'RightSource CRD Type';
    
    //----------------------------Start Case Service --------------------------//
    Public Static final String ACCOUNT_EXTERNALID_MEDICARE ='UKM|00|ETL|MCR|';
    Public Static final String ACCOUNT_EXTERNALID_SSN ='UKM|00|ETL|SSN|';
    Public Static final String ACCOUNT_EXTERNALID_ENTID ='UKM|00|ETL|EID|';
    Public Static final String ACCOUNT_EXTERNALID_UKEY ='UKM|00|ETL|UKEY|';
    Public Static final String ACCOUNT_EXTERNALID_MEMBERIDBASE ='UKM|00|ETL|MBID|';
    Public Static final String ACCOUNT_EXTERNALID_SFDC ='UKM|00|ETL|SFDC|';
    Public Static final String ACCOUNT_EXTERNALID_CBISALTID ='UKM|00|ETL|CBIS|';  
    Public Static final String ACCOUNT_EXTERNALID_MEDICAID ='UKM|00|ETL|MCID|';     
    Public Static final String CASE_EXTERNALID_START ='ETL_';
    Public Static final String CASESERVICE_INTWITHTYPE_HUM ='InteractingWithType';  
    Public Static final String CASESERVICE_INTABTTYPE_HUM ='InteractingAboutType'; 
    Public Static final String CASESERVICE_MEDICAREID_HUM = 'MedicareID' ;
    Public Static final String CASESERVICE_SSN_HUM = 'SSN' ;
    Public Static final String CASESERVICE_MEDICADEID_HUM = 'Medicaid-Id' ;
    Public Static final String CASESERVICE_ENTPERSONID_HUM = 'EnterprisePersonID';
    Public Static final String CASESERVICE_MEMBERIDBASE_HUM = 'Member-Id-Base';
    Public Static final String CASESERVICE_HUMANAID_HUM = 'HumanaId';
    Public Static final String CASESERVICE_CBISALTID_HUM = 'CBIS ALT ID';
    Public Static final String CASESERVICE_UNKNOWNMEMBER_HUM = 'UnknownMember';    
    Public Static final String CASESERVICE_ATTACHMENT_MESSAGE='AttachmentKey and AttachmentType are required if AttachmentDTO is not empty';
    Public Static final String HUMCaseCreateMandatoryError = 'There are no fields present to create case.';
    Public Static final String DatapowerUserName = 'PowerAPI Data';
    Public Static final String CASESERVICE_ATTACHMENT_ERRORMESSAGE = 'Unable to associate attachment log items to case.';
    //------------------------------------------------------------Start REQ - 315928--------------------------------------------------//
    Public Static Set<String> MEDICARE_CASE_SERVICE = New Set<String>{'MER','MCD','MEF','MEP','MES','MGP','MGR','MPD','MRO','MRP','PDP'};
    Public Static final String OTHER_STATE_CODE = 'OT';    
    Public Static String MEMBERIDBASE_CASE_RECCORDTYPE = '(\'Member\',\'Unknown Member\')';
    //------------------------------------------------------------End REQ - 315928--------------------------------------------------//
    
    //----------------------------End Case Service --------------------------//
    
    //-----------------------REQ - 300142------------------------------------//
    Public Static final String MEDICARECALLS_ISSUE_HUM = 'Medicare Calls Issue';
    Public Static final String MEDICARECALLS_RESO_HUM = 'Medicare Calls Resolution';
    Public Static final String MEDICARECALLS_LOB_HUM = 'Medicare';
    Public Static final String MEDICARECALLS_MAPD_HUM = 'MAPD';
    Public Static final String MEDICARECALLS_PDP_HUM = 'PDP';
    Public Static final String MEDICARECALLS_MA_HUM = 'MA';
    Public Static final String MEDICARECALLS_CASEORIGIN_HUM = 'Inbound Call';
    Public Static final String MEDICARECALLS_SEGMENTMCR_HUM = 'MCR';
    Public Static final String MEDICARECALLS_PERMISSIONSET_HUM = 'CRMS_684_Medicare_Customer_Service_Access';
    
    //------------------------------------------------------------REQ - 313481--------------------------------------------------// 
    Public Static final String CHATBATCHJOB_COMPLETED_STATUS_HUM = 'Completed';
    Public Static final String Humana_Pharmacy_Home_Office = 'Humana Pharmacy Home Office';
    
    //-----------------------Quick Start------------------------------------//
    Public Static final String QUICKSTARTACCESS_PERMISSIONSET_HUM = 'CRMS_650_Quick_Start_Access';
    
    //-----------------------Rapid Force------------------------------------//
    Public Static final String RAPIDFORCE_PERMISSIONSET_HUM = 'CRM_RF_LouisvilleUser_Access';
    
    //-----------------------MIR Pilot User------------------------------------//
    public Static final String MIR_PERMISSIONSET_HUM = 'CRMS_1100_MemberIssueResolution';
    
    //----------------------------Start REQ - 305017 --------------------------//
    Public Static final String HUM_PRIORAUTH_RESPONSE = 'parseGetPAuthResponse';
    //----------------------------End REQ - 305017 --------------------------//
    
    //--------------------- START - REQ - 318448 ------------------------------//
     Public Static final String POLICY_MEMBER_ERROR = 'Policy Member Not matching with the Account';
    //---------------------  REQ - 318448 ------------------------------//
        //----------------------------Start REQ - 323542 --------------------------//
    Public Static final String Pharmacy_IntegrationError_HUM = 'Integration Error';
    Public Static final String Pharmacy_Fatal_HUM = 'Fatal';
    Public Static final String Pharmacy_Object_HUM = 'Account';
    //https://developer.salesforce.com/docs/atlas.en-us.apexcode.meta/apexcode/apex_class_System_Continuation.htm 
    Public Static final Map<Integer,String> Pharmacy_Error_Status_Codes_HUM = New Map<Integer,String>{
        2000 => 'The timeout was reached, and the server didn’t get a chance to respond',
        2001 => 'There was a connection failure',
        2002 => 'Exceptions occurred',
        2003 => 'The response hasn’t arrived',
        2004 => 'The response size is too large'};
    //----------------------------End REQ - 323542 --------------------------//
    //----------------------------Start REQ - 300571 --------------------------//
     Public Static final String VOB_PARCODE_DEFAULT_HUM = 'Par Code';
    //----------------------------End REQ - 300571 --------------------------//
    
    //---------------------START REQ - 322649--------------------------------//
    Public Static final String MET_TASK_RECORDTYPE_HUM = 'MET_Task';
    //---------------------END REQ - 322649--------------------------------//
    //----------------------------Start REQ - 308348 --------------------------//
    Public Static final String METENROLLMENTMEDICAREEDIT_PERMISSIONSET_HUM = 'CRMS_630_MedicareElectionTracking_EnrollmentEdit';
    Public Static final String MEDICARE_STATUS_COMPLETED_HUM = 'Completed';
    Public Static final String MEDICARE_STATUS_CANCELLED_HUM = 'Cancelled';
    Public Static final String MET_EMPTY = '';
    Public Static final String MET_SELECT = '--Select--';
    Public Static final String METTASKTYPE ='MET Task';
    //----------------------------End REQ - 308348 --------------------------//

    //------------------------------------------------------------Start REQ - 321922 by LXJ6270--------------------------------------------------//     
     Public Static final String TRACKSERVICE_SOURCE_APP_HUM = 'CRM';
     Public Static final String TRACKSERVICE_RECEIPT_APP_HUM = 'CRM';
     Public Static final String TRACKSERVICE_WRITETRACK_CONNAME_HUM = 'TrackService_WriteTrack';
     Public Static final String TRACKSERVICE_MILESTONETS_FORMAT_HUM = 'yyyy-MM-dd HH:mm:ss';
     Public Static final String TRACKSERVICE_DEFAULT_LOBCODE_HUM = 'MCARE';
     Public Static final String TRACKSERVICE_MILESTONETS_TIMEZONE_HUM = 'America/New_York';
     Public Static final String TRACKSERVICE_TRACKINFO_OTRACKID_Y_HUM = 'Y';
     Public Static final String TRACKSERVICE_TRACKINFO_OTRACKID_N_HUM = 'N';
     Public Static final String TRACKSERVICE_SUCCESS_RESP_HUM = 'Success';
     Public Static final String TRACKSERVICE_ERROR_RESP_HUM = 'Failed';
     //------------------------------------------------------------End REQ - 321922 by LXJ6270--------------------------------------------------//
     
     //------------------------------------------------------------------ REQ - 304169 --------------------------------------------------------//
    Public Static final String LAUNCH_EMME_DEFAULT_URL = 'https://hss.humana.com/Emme/CreateMessage/Create?clmType=&verify=N&Suffix=&provTypeCd=TIN&addType=&mbrNo=&encl=&actionType=select&mbrCovEffDt=&agtNo=&prvNo=&clmNo=&dist=&grpCovSeqNbr=&grpNo=&trackID=50980663200&CCPInquiryNo=894569111401-000000001&prefillAction=CREATE&letter=&plat=&buttonNext=Redirect';
    
     //----------------------------Start CA 6950067 -------------------------//
    Public Static String CLOSEDHP_MEMBERCASE_HUM = 'Closed HP Member Case';
    Public Static Map<String,String> CLONECLOSEDCASE_RECORDTYPEMAP_HUM = New Map<String,String>
    {
        'Closed Member Case' => 'Member Case',
        'Closed Provider Case' => 'Provider Case',
        'Closed Agent/Broker Case' => 'Agent/Broker Case',
        'Closed Group Case' => 'Group Case',
        'Closed Unknown Case' => 'Unknown Case',
        'Closed HP Member Case' => 'HP Member Case',
        'Closed HP Provider Case' => 'HP Provider Case',
        'Closed HP Agent/Broker Case' => 'HP Agent/Broker Case',
        'Closed HP Group Case' => 'HP Group Case',
        'Closed HP Unknown Case' => 'HP Unknown Case',
        'Closed Medicare Case' =>'Medicare Case'
    };
    
    //----------------------------End CA 6950067 --------------------------//
    
    //------------------------------------------------------------------ REQ - 324814 --------------------------------------------------------//
    Public Static final String CASECOMMENT_MEMBERNOTFOUND_HUM = 'The above CRM case comment  was not sent to the Order Management System (OMS)- also known as ePost/ANS.  Error Reason: This member does not have an Order Management System (OMS)- also known as ePost/ ANS- account.  This note is not saved to ePost Rx, but saved in CRM.  You can either close this tab, or click on the ‘Exit without Saving’ button on the case to continue.';
    Public Static final String CASECOMMENT_FAULTERROR_HUM = 'The above CRM case comment  was not sent to the Order Management System (OMS)- also known as ePost/ANS.  Error Reason: The Order Management System (OMS)—also known as ePost/ ANS- is currently unavailable.  This note is not saved to ePost Rx, but saved in CRM.  You can either close this tab or click on the ‘Exit without Saving’ button on the case to continue.';
    //------------------------------------------------------------------ REQ - 324814 --------------------------------------------------------//
    Public Static final String QUEUESELECTION_ONGOINGCHAT_HUM = '570/e?ongoing=1';

    //---------------------------- REQ - 350420 -------------------------//
    Public Static final String SYSTEM_ADMINISTRATOR_PROFILE_HUM = 'System Administrator';
    Public Static final String DEPLOYMENT_PROFILE_HUM = 'Deployment'; 
    Public Static final String ETL_API_ACCESS_PROFILE_HUM  = 'ETL API Access';
    Public Static final String DEFAULT_OMNI_QUEUE_HUM  = 'Default Omni Queue'; 
    
/*  
    //---------------------------- REQ - 349585 -------------------------//
    Public Static Map<String, String> WORK_QUEUE()
    {
        Map<String, String> mapWorkQueue = New Map<String, String>();        
        mapWorkQueue.put('Louisville RSO Enrollment','Creditable Coverage,Access to Care,Agent Communication,Agent Retail Service Operations Support (ARSOS),'
        +'Best Available Evidence (BAE) / Low Income Subsidy (LIS),Category 2 Case Build,CMS Retro-Active Processing - Group Medicare,CMS Retro-Active Processing - Individual Medicare,'
        +'Consent Form,Disenrollment/Cancel,Dual Eligible Auto Assign (DEAA),Employer Subsidy Override 127,Good Cause Reinstatement,Group Medicare - Other,Incomplete Critical Errors,'
        +'Individual Medicare - Other,Medicaid,Medicare Group Pends,Medicare Supplement - Disenrollment/Cancel,Medicare Supplement - Other,Medicare Supplement - All Other,'
        +'New Enrollment - Individual Medicare,New Enrollment Pending Applications,Non State Based Contracts Reconciliation,Non State Based Contracts Termed in Error,PCP Changes,'
        +'Person Identifier (PID) Merge Request,Best Available Evidence (BAE) - Pharmacy Attestation,Power of Attorney (POA),Reinstatement Hold,Service Fund Escalations,'
        +'Special Needs Plan (SNP) Validation Support,State Based Contract Person Identifier (PID) Merge Request,State Based Contracts Reconciliation,State Based/Medicaid -Other,'
        +'Disenrollment/Cancel,Incomplete Correspondence,Chronic Care Special Needs Plan (SNP) Pre Qual Form,Individual Incomplete,Chronic Care Special Needs Plan (SNP) Incomplete,'
        +'Puerto Rico Incomplete,Group Incomplete,Dual Eligible Special Needs Plan (SNP) Incomplete,Disenrollment Specialty,Enrollment Hold');
        
        Return mapWorkQueue;                                                                            
    }
    */
    //----------------------------REQ - 338410 ------------------------//
    Public Static final String ENTERPRISEID = 'EnterpriseId';
    
    //----------------------------REQ - 349596 ------------------------//
    Public Static final String CASEEDIT_INTENT_SEPERATOR=')';
    Public Static final String CASEEDIT_CLASSIFICATION_SEPERATOR='_';

    //----------------------------REQ - 335173 ------------------------//
    Public Static final String CASECLOSEDEDITPAGE='CaseClosed_Edit_VF_HUM';
    
    //-------------------------------------------- REQ - 354517--------------------------------------------------------//
    Public Static final String CASECOMMENT_PHARMACY_MEMBERNOTFOUND_HUM = 'This member does not have an Order Management System (OMS) - also known as ePost/ANS  - account. The Case Comment is not saved to ePost Rx, but saved in CRM';
    Public Static final String CASECOMMENT_PHARMACY_FAULTERROR_HUM =  'The Order Management System (OMS) - also known as ePost/ANS  - is currently unavailable. The Case Comment is not saved to ePost Rx, but saved in CRM';
    //----------------------------------------------- REQ - 354517 --------------------------------------------------------//
    
     //----------------------------Start REQ - 349580 And 349581 --------------------------//
    Public Static final String EncryptRestService_HUM = 'EncryptRestService';
    Public Static final String MemPreference_HUM = 'MemberPreferenceURL';
    
    //----------------------------Start REQ - 357304---------------------//
    Public Static final String CASE_CLASSIFICATION_TYPE_CALLRSO = 'Calls (RSO)';
    Public Static final String CASE_CLASSIFICATION_TYPE_LINETRSO = 'LI NET (RSO)';
    Public Static final String CREDITABLECOVERAGE_TESTER_PERMISSIONSET= 'CRMS_605_BizConfig_MedicareEnrollment_Edit';
    Public Static final String CREDITABLECOVERAGE_ATTESTATION_PROCESS_NAME=  'ATTVF';
    Public Static final String CASEACTIONLAUNCH_CREDITABLECOVERAGE_ATTESTATION_DESC_HUM = 'Creditable Coverage Attestation';
    Public Static final String CASEACTIONLAUNCH_CREDITABLECOVERAGE_ATTESTATION_PROCESS_HUM  =  'Creditable Coverage Attestation'; 
    Public Static final String CREDITABLECOVERAGE_YES  =  'YES'; 
    Public Static final String CREDITABLECOVERAGE_NO  =  'NO'; 
    Public Static final String CREDITABLECOVERAGE_CICMSCALLFIELD = 'ATVF_IsCICMSCalled';
    Public Static final String CREDITABLECOVERAGECASESUBTYPE = 'Creditable Coverage';
    Public Static final String CASE_CLASSIFICATION_COMMUNICATION = 'Communication';
     Public Static final String CASE_CLASSIFICATION_WelcomeCall = 'Welcome Call';  
    Public Static final String CASE_CLASSIFICATION_ELIGIBILITY = 'Eligibility';
    Public Static final String CASE_INTENT_ELIGIBILITY = 'Got A Letter/Call';
    Public Static final String CASE_INTENT_LATEENROLLMENTPENALTY = 'Late Enrollment Penalty (LEP)/Creditable Coverage (CC)';        
    Public Static final String CASE_RSO_ENROLLMENT_QUEUENAME = 'Louisville RSO Enrollment';
    Public Static final String CASE_RSO_ENROLLMENT_OWNER = 'Louisville RSO Enrollment';
    Public Static final String CASE_STATUS_PENDING_RESPONSE = 'Pending - Response';
    Public Static final String ATTESTATION_SUMISSION_PRESENT = 'submissionId';
    Public Static final String ATTESTATION_TEMPLATE_NAME = 'ATVF007';
    Public Static final String ATTESTATION_DISCLAIMERNAME = 'ATVF_FinalDisclaimer';
    Public Static final String ATTESTATION_DISCLAIMERYESVALUE = 'Yes';
    Public Static final String SERVICE_CENTER_LOUISVILLE = 'Louisville';
    Public Static final String DEPARTMENT_RETAIL_RSO_ENROLLMENT = 'Retail Service Operations (RSO) Enrollment';
    Public Static final String CASE_CLASSIFICATION_TYPE_PROVIDER = 'Provider';
    Public Static final String CASE_INTENT_PCPPCD = 'PCP/PCD';


    //----------------------------Start REQ - 356054 --------------------------//
    Public Static final String METMEDICAREENROLLMENTOMNIEDIT_PERMISSIONSET_HUM = 'CRMS_605_BizConfig_MedicareEnrollment_Edit';
    
    //----------------------------Start REQ - 358347 --------------------------// 
    Public Static final String METMILESTONE_DEFAULT_ID = '9999';
    
    //------------- REQ - REQ - 303425----------------//
    Public Static final String MOBIUS_GETDOCUMENT_APPLICATIONKEY ='238F6F83-B8B4-11CF-8771-00A024541EE3';
    //------------------------------------------------------------------ ID CARDS --------------------------------------------------------//
    Public Static final String GROUPIDCARDMGT_DATE_FORMAT_HUM = 'MM/dd/yyyy';
    Public Static final String GROUPIDCARDMGT_CARDREQUESTDATE_HUM = 'sCardRequestDate';
    Public Static final String GROUPIDCARDMGT_GROUPNUMBER_HUM = 'sGroupNbr';
    Public Static final String GROUPIDCARDMGT_GROUPNAME_HUM = 'sGroupName';
    Public Static final String GROUPIDCARDMGT_PLATFORM_HUM = 'sPlatform';
    Public Static final String GROUPIDCARDMGT_CARDCOUNT_HUM = 'sCardCount';
    Public Static final String GROUPIDCARDMGT_REQUESTDATEHEADER_HUM = 'Request Date';
    Public Static final String GROUPIDCARDMGT_GROUPNUMBERHEADER_HUM = 'Group ID';
    Public Static final String GROUPIDCARDMGT_GROUPNAMEHEADER_HUM = 'Group Name';
    Public Static final String GROUPIDCARDMGT_PLATFORMHEADER_HUM = 'Originating System';
    Public Static final String GROUPIDCARDMGT_CARDCOUNTHEADER_HUM = 'No. of Cards Requested';
    Public Static final Integer GROUPIDCARDMGT_FROMDATEPERIOD_HUM = -540;
    Public Static final Integer IDCARDMGT_FROMDATEPERIOD_HUM = -18;
    Public Static final String GROUPIDCARDMGT_PARSEGROUPRESPONSE_HUM = 'parseGroupResponse';
    Public Static final String GROUPIDCARDMGT_HIDCMSGROUPSEARCH_HUM = 'HIDCMS_GroupSearch';
    Public Static final String GROUPIDCARDMGT_HIDCMSGROUPMEMBERLEVELSEARCH_HUM = 'HIDCMS_GroupMemberLevelSearch';
    Public Static final String MEMBERIDCARDMGT_PARSEORDERIDCARDRESPONSE_HUM = 'parseOrderIdCardResponse';
    Public Static final String MEMBERIDCARDMGT_PROCESSMEMLEVELRESPONSE_HUM = 'processMemberLevelResponse';
    Public Static final String MEMBERIDCARDMGT_HIDCMSORDERIDCARD_HUM = 'HIDCMS_OrderIdCard';
    Public Static final String MEMBERIDCARDMGT_HIDCMSMEMBERSEARCH_HUM = 'HIDCMS_MemberSearch';
    Public Static final String MEMBERIDCARDMGT_MEMIDMISSING_HUM = 'ID Card Member Search failed. Reason:Member_ID__c record is missing for Policy_Member__c ';
    Public Static final String MEMBERIDCARDMGT_GRPNUMMISSING_HUM = 'ID Card Member Search failed. Reason:Group Number is missing for Policy_Member__c ';
    Public Static final String MEMBERIDCARDMGT_MEMBERIDBASE_HUM = 'Member-Id-Base';
    Public Static final String GROUPMEMBERDETAILS_PARSERSGROUPMEMBERDETAILRESPONSE_HUM = 'parseRSGetGroupMemberDetailResponse';
    Public Static final String GROUPIDCARDMGT_GRPNUMMISSING_HUM = 'ID Card Group Search failed. Reason:Group Number is missing for Account ';
    Public Static final map<String,String> Platform_Mapping_HUM = New map<String,String>{'LV' => 'CAS','EM' => 'MTV','CB' => 'CB'};
    Public Static final String MEMBERIDCARD_HSA = 'HSA';
    Public Static final Map<String,String> Month_Format_HUM = New Map<String,String>{'JAN'=>'01','FEB'=>'02','MAR'=>'03','APR'=>'04','MAY'=>'05','JUN'=>'06','JUL'=>'07','AUG'=>'08','SEP'=>'09','OCT'=>'10','NOV'=>'11','DEC'=>'12'};
    Public Static final String LINKCRITERIA_NOTCONTAINS_HUM = 'Not Contains';
    
    //watson
    Public Static final String CASESERVICE_ACCOUNTGROUPTYPEID = 'GroupID';

    //----------------------- REQ - 350471 ---------------------------------------//
    Public Static final String CASEEDIT_INT_ABOUT_TYPE_UNKNOWN_MEMBER = 'Unknown-Member';

    //-----------------------------------REQ - 355782----------------------------------------------------------//
    Public Static final String PAIDTHROUGHDATE_RATELEVEL_HUM = 'Account';
    Public Static final String PAIDTHROUGHDATE_TRANSACTIONTYPE_HUM = 'p';
    Public Static final String PAIDTHROUGHDATE_CALLINGSYSTEMID_HUM = 'CRMS';
    Public Static final String PAIDTHROUGHDATE_USERIDTYPE_HUM = 'INTL';
    Public Static final String PAIDTHROUGHDATE_IDTYPE_HUM = 'CONTRID';
    Public Static final String PAIDTHROUGHDATE_NUMBEROFTRANSACTIONS_HUM = '1';

   //-----------------------------------REQ - 361863----------------------------------------------------------//
    Public Static final String CASE_SUBTYPE_INCOMPLETE = 'Incomplete';
    Public Static final String CASE_SUBTYPE_CRITICAL_ERROR = 'Critical Error';
    Public Static final String CASE_SUBTYPE_DISENROLLMENT = 'Disenrollment';
    Public Static final String CASE_SUBTYPE_CANCELLATION_OF_ENROLLMENT = 'Cancellation of Enrollment';
    Public Static final String CASE_SUBTYPE_CANCELLATION_OF_DISENROLLMENT = 'Cancellation of Disenrollment';
    Public Static final String CASE_SUBTYPE_DROPPED_APPLICATION = 'Dropped Application';
    Public Static final String CASE_SUBTYPE_INCOMPLETE_DE_SNP = 'Incomplete DE SNP';
    
    //------------------------------------------ REQ 361848 ----------------------------------------------------------//
    Public Static final String GROUPBENEFITS_SOURCECUSTCOVKEY_HUM = 'source_cust_cov_key__c';
    Public Static final String GROUPBENEFITS_COVPLANENDDATE_HUM = 'Coverage_Plan_end_date__c';
    Public Static final String GROUPBENEFITS_PRODUCTNAME_HUM = 'product__r.Name';
    Public Static final String GROUPBENEFITS_DATEFORMAT ='MM/dd/yyyy';
     //----------------------------------------------------------------------------------------------------------------//
    
    //-------------------------------------------REQ - 366254------------------------------------------------------------//
    Public Static final String RXLOOKUP_PROCESS_ENCRYPTSERVICE = 'ProcessEncryptService';
    Public Static final String RXLOOKUP_MEMBERIDBASE_TYPE = 'Member-Id-Base';
    Public Static final String RXLOOKUP_URL = 'RXLookUpURL';
    Public Static final String SSO_URL = 'SSO_LOGINURL';
    Public Static final String RXLOOKUP_TARGET = 'TARGET';
    //--------------------------------------------------------------------------------------------------------------------//

    //-------------------------------------------REQ - 370542------------------------------------------------------------//
    Public Static final String CASE_CLASSIFICATION_TYPE_CALLPDPPilot = 'PDP Pilot Plan Calls';    
    Public Static final String PDP_Pilot_Pharmacy_Work_Queues = 'PDP Pilot Humana Pharmacy Calls';
    Public Static final String PDPPilot_PermissionSet_Name= 'CRMS_205_CCSPDPPharmacyPilot';
    Public Static final List<String> Pharmacy_Record_Type_Name = New List<String>{'Closed HP Agent/Broker Case','Closed HP Group Case','Closed HP Member Case','Closed HP Provider Case','Closed HP Unknown Case','HP Agent/Broker Case','HP Group Case','HP Member Case','HP Provider Case','HP Unknown Case'};
    //--------------------------------------------------------------------------------------------------------------------//

    //-----------------------------------REQ - 370423----------------------------------------------------------//
    Public Static final Map<String,String> UnknownMember_ConsumerIdType= New map<String,String>
    {'Medicaid-Id' => 'Medicaid ID',
        'SSN' => 'Social Security Number',
        'HumanaId' => 'Humana ID',
        'CBIS ALT ID' => 'CBIS Alt ID',
        'MedicareID' => 'Medicare Number'
    };
    Public Static final String CONSUMER_EXTERNALID_MEDICARE ='UKM|00|ETL|MCARE|';
    Public Static final String CONSUMER_EXTERNALID_SSN ='UKM|00|ETL|SSN|';
    Public Static final String CONSUMER_EXTERNALID_MEDICAID ='UKM|00|ETL|MCAID|';

    Public Static Final String Grievance_Appeals_Permissionset_Name = 'CRMS_400_Grievance_Appeals';
    //----------------------------Start REQ - 356001 --------------------------// 
    Public Static final String OBJECTPOLICY_HUM = 'Policy__C';
    Public Static final String  OBJECTGROUP_HUM = 'Group';
    Public Static final String  OBJECTPOLICYMEMBER_HUM = 'Policy_Member__c';
    //----------------------------End REQ - 356001 --------------------------//    

    //----------------------------Start REQ - 376382 --------------------------//
    Public Static final List<String> AllowedCASProdType = New List<String>{ 'MEF','MRO','MEP','MER','PDP' };
    //----------------------------End REQ - 376382 ----------------------------//    
    //---------------------------------- REQ - 367752 -----------------------------------------------------------------//
   Public Static final String PID_CALLBACKMETHOD ='parseMBEPlusResponse';
   Public Static final String PID_MEMBERNOTAVIALBLE ='Member is not found for this PID number';
   //------------- Ehub getDocument application key----------------//
    Public Static final String EHUB_GETDOCUMENT_APPLICATIONKEY ='238F6F83-B8B4-11CF-8771-00A024541EE9';
    //--------------------------------------REQ - 369721------------------------------------------------//
    Public Static final String MRSYSCODE='CC';
    Public Static final String  MRDOCID = '1';
    Public Static final String  MRCALLOUT_SERVICE_NAME = 'EhubAddDocumentService';
    Public Static final String EHUB_URL_DOCID = '/apex/EhubDocumentViewer_VF_HUM?DocId=';
    Public Static final String EHUB_URL_VID = '&VId=';
    //---------------------------------REQ- 378715------------------------------------------------------------------------//
   Public Static final Set<String> RestrictedCASProdType = New Set<String>{'MCD','MGP','MGR','MES','MPD','MRP'};
   
   //-----------------------REQ - 350626 - Power of attorney display from designee service.-------------------------
    Public Static final String  Power_Of_Attorney = 'Power of Attorney';
    
    //-----------------------US 1609149 User Story 1609149 PR00094816 - MF 5 - DEV - Add Go365 Medicaid Guardian designation form to Associated Forms And Information Section of Person Acct Page in CRM Service-------------------------
    Public Static final String  AOR= 'Appointment of Representative';
    Public Static final String  Go365_Medicaid_Guardian = 'Go365 Medicaid Guardian';
    public Static final String  Consent_for_PHI= 'Consent for PHI';
    //----------------------------------------------REQ-377179----------------------------------------------------------//
   Public Static final String INTERACTIONTYPE_TAXID='TAXID';
   Public Static final String INTERACTIONTYPE_NPIID='NPIID';
   Public Static final String UNKNOWNPROVIDER_CASE_SERVICE='UnknownProvider';
   Public Static final String KNOWNPROVIDER_CASE_SERVICE='Provider';
   Public Static final String INTERACTIONTYPE_AGENTID='AGENTID';
   Public Static final String ERRORMSG_CASESERVICE='An error occured.';
   Public Static final String PROVIDER_CLASSIFICATION='ProviderClassification';
   Public Static final String CALLOUTEXCEPTION_TYPE='CalloutException';
   Public Static final String BILLING_COUNTRY_CODE='US';
   Public Static final String CASESERVICE_OBJECT_ACCOUNT = 'Account';
   Public Static final String UNKNOWNACCOUNT_UNKNOWNGROUPRECORDTYPE = 'Unknown Group';
   Public Static final String CASESERVICE_ACCOUNTGROUPTYPE = 'Group';
   Public Static final String CASESERVICE_ACCOUNTAGENTTYPE = 'Agent';
   Public Static final String CASESERVICE_UNKNOWNPROVIDER = 'Unknown Provider';
   Public Static final String CASESERVICE_OTHER_STATE_CODE = 'OT';    
   Public Static final String INDIVIDUAL_PROVIDER_CLASSIFICATION='Individual';
   Public Static final String SPACE_CONSATNT=' ';
   Public Static final String MISSINGNAME_UNKNOWNACCT='Missing Name';
   Public Static final String MISSINGNAME_UNKNOWNACCOUNT='MissingName';
   public static final String MEMEBER_INT_ABT_TYPE = 'Member';
   Public Static final Map<String,String> PROVIDER_CLASSIFICATIONMAP=New Map<String,String>{'Facility'=>'F','Individual'=>'I','Provider Group'=>'G'};
   //-----------------------------------------------------------------------------------------------------//
   Public Static final String UNKNOWNAGENCY_CASE_SERVICE='UnknownAgency';
   Public Static final String UNKNOWNBROKER_CASE_SERVICE='UnknownBroker';
   Public Static final String INTERACTION_AGENCY_TYPE = 'Agency';
   Public Static final String INTERACTION_BROKER_TYPE='Broker';

    //------ Einstein humana Bot -- NF -----------//
    Public Static final String EINSTEIN_BOT_CASE_ORIGIN = 'Chatbot';
    Public Static final String EINSTEIN_BOT_CASE_TYPE = 'Customer Inquiry';
    Public Static final String EInSTEIN_BOT_CASE_CLASSIFICATION = 'Humana Pharmacy';
    Public Static final String EInSTEIN_BOT_CASE_INTERACTING_TYPE = 'Member' ;
    Public Static final String EINSTEIN_BOT_CHAT_BUTTON_ROUTING_TYPE_OMNI = 'Omni';
    Public Static final String EINSTEIN_BOT_ACCOUNT_SEGMENT_INDICATOR = 'Individual-Medicare';
    Public Static final String EINSTEIN_BOT_GET_ACCOUNT_DETAILS_EVENT_TYPE = 'INIT';
    Public Static final String EINSTEIN_BOT_CLOUDINTEGRATION_USER_TYPE = 'CloudIntegrationUser';
    Public Static final String EINSTEIN_BOT_AUTOMATEDPROCESS_USER_TYPE = 'AutomatedProcess';
    Public Static final String EINSTEIN_BOT_LIVECHAT_STATUS_INPROGRESS = 'InProgress';
    Public Static final String EINSTEIN_BOT_LIVECHAT_STATUS_COMPLETED = 'Completed';
    Public Static final String EINSTEIN_BOT_CHAT_TYPE_ASSISTED = 'Humana Pharmacy Assisted';
    Public Static final String EINSTEIN_BOT_CHAT_TYPE_CHATBOT = 'Humana Pharmacy Chatbot';

    Public Static final String EINSTEIN_BOT_CHAT_LOG_TYPE = 'LOG_CHAT';
    Public Static final String EINSTEIN_BOT_CHAT_RESOLUTION_SUCCESS_TYPE = 'RESOLUTION_SUCCESS';

    Public Static final String EINSTEIN_BOT_YES = 'Yes';
    Public Static final String EINSTEIN_BOT_NO = 'No';
    Public Static final String EINSTEIN_BOT_NA = 'N/A';
    Public Static final String EINSTEIN_BOT_NULL = 'NULL';

    Public Static final String EINSTEIN_BOT_GET_ORDER_NETWORK_ID = 'Einstein';
    Public Static final String EINSTEIN_BOT_GET_ORDER_ANOTHER_ORDER = 'Another Order';

    Public Static final String EINSTEIN_BOT_GET_ORDER_OPEN_REASON = 'FAILED CLAIMS';
    Public Static final String EINSTEIN_BOT_GET_ORDER_OPEN_REASON_PICKLIST = 'failedClaims';

    Public Static final String EINSTEIN_BOT_REPORTING_METRIC_BUSINESS_START = 'Start';
    Public Static final String EINSTEIN_BOT_REPORTING_METRIC_BUSINESS_END = 'End';
    Public Static final String EINSTEIN_BOT_REPORTING_METRIC_BUSINESS_DIRECT = 'Direct';
    Public Static final String EINSTEIN_BOT_REPORTING_METRIC_TOPIC = 'Topic';
    Public Static final String EINSTEIN_BOT_LIVE_AGENT_AVAILABLE = 'Available';
    Public Static final String EINSTEIN_BOT_LIVE_AGENT_ONLINE = 'Online';
    Public Static final String EINSTEIN_BOT_NON_EINSTEIN_CHATS = 'nonEinsteinChats';
    Public Static final String EINSTEIN_BOT_EINSTEIN_CHATS = 'EinsteinChats';

    Public Static final String EINSTEIN_BOT_REPORTING_BATCH_CHAT_REQUEST = 'ChatRequest';
    Public Static final String EINSTEIN_BOT_REPORTING_BATCH_TRANSFERRED_BUTTON = 'TransferredToButton';
    Public Static final String EINSTEIN_BOT_REPORTING_BATCH_TRANSFER = 'Transfer';
    Public Static final String EINSTEIN_BOT_REPORTING_BATCH_PUSH_ASSIGNMENT = 'PushAssignment';
    Public Static final String EINSTEIN_BOT_REPORTING_BATCH_LEAVE_VISITOR = 'LeaveVisitor';
    Public Static final String EINSTEIN_BOT_ESCALATION_REQUESTED = 'Requested';
    Public Static final String EINSTEIN_BOT_ESCALATION_SUCCESSFUL = 'Successful';
    Public Static final String EINSTEIN_BOT_UPSSTART = '1z';
    Public Static final String EINSTEIN_BOT_UPS = 'UPSURL';
    Public Static final String EINSTEIN_BOT_USPS = 'USPSURL';
    Public Static final Integer EINSTEIN_BOT_ORDERSIZE = 4;
    Public Static final String EINSTEIN_BOT_HPURLPREFIX = 'HPURLPREFIX';


    //---------------------------------------------REQ - 378820------------------------------------------------------------//

    Public Static final String HUMCaseSource_CCPGCP = 'CCP/GCP';
    //------------------------------------------------REQ - 319447-----------------------------------------------------------------------------------------------------------------------------------
Public Static final Set<String> HPCHAT_OMNIQUEUES = New Set<String>{'HP Chatbot Queue'}; 
    //---------------------------------------------REQ - 380402--------------------------------------------------------//
    Public Static final String HUMGetClaimSummary_V3 = 'ClaimsService_GetClaimsSummary_V3';

    //----------------------------Start REQ - 380419 --------------------------//
    Public Static final string AUTOROUTING_PARTD = 'Evaluate Part D';
    public static final string AUTOROUTING_ExpeditedAppeal = 'Evaluate Expedited Appeal';
    /************************* Good Cause Reinstatement Implementation  **********************************************/
    public static final string AUTOROUTING_GOODCAUSE = 'Evaluate Good Cause';
    public static final string AUTOROUTING_GOODCAUSETIE = 'Termed in Error';
    public static final List<String> AutoRouteWQs = new List<String>{'Louisville RSO Calls Support', 'LV Partner Site RSO Calls C3', 'LV Partner Site RSO Calls Results Co', 'LV Partner Site RSO Calls Xerox', 'LV Partner Site RSOCalls Teleperformance','Group Service Operations Calls'}; 
    public static final List<String> GoodCauseAutoRouteClassificationTypes = new List<String>{'Calls (RSO)','Guidance Center','Web Chat', 'RCC Web Chat'};
    public static final string GOODCAUSEUNIQUEID = 'E0A13562-89EE-48B8-8EBA-E485C6583FF1';
    public static final string GCRTERMEDPLAN = 'GCRTermedPlanYesNo';
    public static final string GOODCAUSEGROUPNAME = 'Louisville Premium Payment Recovery PGQ';
    public static final string GOODCAUSEQUEUE = 'Louisville Premium Payment Recovery';
    public static final string GOODCAUSECLASSIFICATION = 'Billing';
    public static final string GOODCAUSEINTENT = 'Reinstatement Review';
    /************************* PCP Implementation  **********************************************/
    public static final string AUTOROUTING_PCP = 'PCP Reassignment';
    public static final string PCPGROUPNAME = 'PCP Assignment Research PGQ';
    public static final string SERVICEFUNDCHECK = 'Did you receive a Service Fund edit when attempting to change the member\'s PCP';
    public static final string PCPTEMPLATEUNIQUEID ='d884d160-77c0-4d1b-95aa-edf8c81f3a59';
    //---------------------------------------------REQ - 380499 & 380500 ------------------------------------------------------------//
    
    Public Static final String CASE_CLASSIFICATION_TYPE_HUMANA_PHARMACY_CALLS = 'Humana Pharmacy Calls';
    Public Static final String CASE_CLASSIFICATION_TYPE_HUMANA_PHARMACY_WEB_CHAT = 'Humana Pharmacy Web Chat';
    Public Static final String CASE_CLASSIFICATION_MEMBER_PROFILEACCOUNT_CTTM = 'Member Profile/Account (CT & TM)'; 
    Public Static final String CASE_CLASSIFICATION_PLAN_WEBMOBILE_CTTM = 'Website (CT & TM)';

    Public Static final String CASE_CLASSIFICATION_BENEFITS_CTTM = 'Benefits (CT & TM)'; 
    Public Static final String CASE_INTENT_ADDUPDATE_PLAN_DEMOGRAPHICS = 'Add/Update Plan Demographics';
    Public Static final String CASE_INTENT_WEB_EDUCATIONNAVIGATION= 'Web Education/Navigation';

    Public Static final String CASE_INTENT_TRANSFER_COB= 'Transfer to COB';
    
    public static final string CASESERVICE_ATTACHMENT_ERRORMSG = 'Unable to associate attachment to case.'; //REQ-380714
    
    Public Static final String PHARMACYTraditionalInsuranceData_PERMISSION_HUM = 'CRMS_302_HPTraditionalInsuranceData'; //REQ - 378996
    Public Static final String PHARMACY_MTM_HUM = 'Medication Therapy (MTM)'; //REQ - 378996
    
    //REQ-385258
    public static final string HUMANAPHARMACYWEBCHATGROUP = 'Humana Pharmacy Web Chat PGQ'; 
    public static final string HUMANAPHARMACYWEBCHATWQ = 'Humana Pharmacy Web Chat'; 
    //--------------------------------------------REQ-383849 -------------------------------------------------------------------
    Public Static final String CASESERVICE_ATTACHMENT_INCLUSION='Attachment';
    Public Static final String CASESERVICE_ATTACHMENTLOG_INCLUSION='Attachment Log';
    Public Static final String CASESERVICE_CASECOMMENT_INCLUSION='Case Comment';
    Public Static final String CASESERVICE_CASEDETAILS_INCLUSION='Case Details';
    Public Static final String CASESERVICE_INTERACTION_INCLUSION='Interaction'; 
    Public Static final Integer CASESERVICE_PAGINGLIMIT_INCLUSION=20;
    Public Static final String CASESERVICE_TASKS_INCLUSION='Tasks';
    Public Static final String CASESERVICE_SEARCHUNKNOWN_INCLUSION='Search Unknown';
    Public Static final String CASESERVICE_GETCASEINFODATERANGERROR_HUM='Date range is beyond 1 year';
    public static final string CASESERVICE_GROUP_NUMBER='Group Number';
    public static final string CASESERVICE_Unknown='Unknown';
    public static final string CASESERVICE_UNKNOWNDETAILS_INCLUSION = 'Unknown Details';

    //--------------------------------------------REQ - 387544--------------------------------------------------------------------//
    public static final string HUM_MEMBER_NOT_FOUND = 'Member not found';
    public static final String RECORD_TYPE_NAME_UNKNOWN_MEMBER = 'Unknown-Member';
    public static final List<String> Pharmacy_Record_Type_Name_LogCode_Population = New List<String>{'HP Member Case','HP Agent/Broker Case','HP Group Case','HP Provider Case','HP Unknown Case'};
    public static final string HumanaPharmacyCallsClassificationType = 'Humana Pharmacy Calls';
    
    //--------------------------------------------REQ - 377480  ---------------------------------------------------------//
    public static final string KMDOCUMENT = 'KMDocument';

//---------------------------------------REQ-362063---------------------------------------------------//
     Public Static final String CREDITCARD_ERROR_SIX ='CARD10006-Non-Preferred card cannot be updated';
     Public Static final String CREDITCARD_ERROR_FIVE ='CARD10005-Preferred card cannot be updated';
     
    //------------------------------------------REQ-380742----------------------------------------------------------------------
    public static final string TRR_ORIGIN = 'CRMTRR';
    public static final string TRR_FIELD_REPLYCD = 'Reply Code';
    public static final string TRR_FIELD_EFFDATE = 'Effective Date';
    public static final string TRR_FIELD_PROCDATSTMP = 'Processing Date Stamp';
    public static final string TRR_TEMPLATE_NAME = 'TRR_Template';
    public static final string TRR_TEMPLATE_NONE = 'None';
    public static final string TRR_TEMPLATE_DATETIME = 'DateTime';
    public static final string TRR_TEMPLATE_DATE = 'Date';
    public static final string TRR_TEMPLATE_COLON = ':';
    public static final list<string> TRR_TEMPLATE_FIELDS = new List<string>{'Processing Date Stamp', 'Processing Time Stamp', 'Medicare Claim Number', 'Date of Birth', 'First Name', 'Last Name', 
            'UI Initiated Change Flag', 'UI User Organization Destination','Variable', 'Reply Code', 'Effective Date', 'End Date', 
            'Disenrollment Reason Code', 'Contract', 'PBP', 'Segment ID', 'Trans Code', 'Election Type', 'SEP Reason Code','Source ID', 'Enroll Source', 
            'Cumulative Number of Uncovered Months', 'Submitted Number of Uncovered Months', 'Creditable Coverage Flag', 'Preferred Language', 'Accessible Format', 'Race', 'Ethnicity', 'Letter Track ID', 'Enr Accept Track ID' , 'Reply Type Track ID'};
 //---------------------------------------------REQ - 392177------------------------------------------------------------//
     Public Static final String VOB_TABLEOFCONENTS_URL = 'https://dctm.humana.com/Mentor/xWeb/viewtopic.aspx?sChronicleID=09000929825835cd'; 
     Public Static final String RFGBOTITLETOWNERS_PERMISSION_HUM = 'CRM_GBORF_TitletownersUser_Access';
     
    //----------------------------------------- REQ - 383800 -----------------------------------------------------------------------
    
    public static final String TEMPLATE_SUBMISSON_DATA_OBJECT =  'Template_Submission_Data__c';

    //------------------------------ REQ - 393077 aka CA Ticket 8138421-----------------------//
     public Static final String HUM_NO_ACTIVERECORD = 'No active record found';
     
     //----------------------------Live Agent Pharmacy--------------------------//
    Public static final string ENCRYPTIONKEY_PHARMACY = '78dd574bfdec4ef3';
    Public static final string IV_LIVEAGENT_PHARMACY = '0c)I$(R@0c)I$(R@';
    Public static final string ALGORITHM_PHARMACY = 'AES128';
    Public static final string LIVEAGENT_BUTTON_NAME = 'CWP_Customer_Service';
    Public static final string LIVEAGENT_PHARMACY_MEMBERGENKEY='xkjV7NMXFxcrfonPKmHcvw==';
    Public static final string LIVEAGENT_PHARMACY_SESSIONID = 'ST5xoPysnbUn0Gda0tpgRzjPt2pLD30p3qH1CTvvnE3SVtPGkLsLGrgtYKRCxz6K';
    Public static final string LIVEAGENT_BUTTON_TYPE = 'Standard';
    
    //--------------------------------------------REQ - 386935  ---------------------------------------------------------//
    public static final string WORK_QUEUE_PROVIDER_PAYMENT_INTEGRITY = 'Provider Payment Integrity';
    public static final string WORK_QUEUE_OTHER_INSURANCE_INVESTIGATION = 'Louisville Other Insurance Investigation';
    public static final string WORK_QUEUE_RSO_ORAL_GRIEVANCE_OVERSIGHT = 'Louisville RSO Oral Grievance Oversight';
    public static final string WORK_QUEUE_MEDICARE_MEMBER_COMMUNICATIONS = 'LV Medicare Member Communications';
    
    //--------------------------------------------REQ - 379035  ---------------------------------------------------------//
    public static final string SERVICE_CENTER_HUMANA_PHARMACY = 'Humana Pharmacy';
    public static final string SERVICE_CENTER_RX = 'Rx';
    public static final string TOPIC_SPECIAL_INVESTIGATIONS = 'Special Investigations Unit (SIU) Referrals';
    public static final string TOPIC_ORAL_GRIEVANCE = 'Oral Grievance';
    public static final string TOPIC_ORAL_GRIEVANCE_INTERNAL = 'Internal Only - Oral Grievance';
    public static final string TOPIC_CLARABRIDGE_ORAL_GRIEVANCE_INTERNAL = 'Internal Only - Clarabridge Oral Grievance';
    
    //------------------------------ REQ - 396223 New constants for TRR process BAM Tracking-----------------------//
    public static final set<string> TRR_REJ_CATERGORY_PRIORITY = new set<string>{'127/169 Rejection','Enrollment Rejection','Disenrollment Rejection','Cancellation of Enrollment Rejection','Cancellation of Disenrollment Rejection','4Rx Rejection','Creditable Coverage Rejection','Opt Out Rejection'};
    public static final string TRR_FIELD_CASEACTION = 'Case Action';
    public static final string TRR_FIELD_REPLYCATEGORY = 'Reply Code Category';
    public static final string TRR_FIELD_SOURCETRACKID = 'Reply Type Track ID';
    public static final string TRR_FIELD_REPLYCODE = 'Reply Code';
    public static final string TRR_FIELD_TRANSCODE = 'Trans Code';
    public static final string TRR_REJECTIONREPLY_CATEGORY = 'Rejection Replies';
    public static final string TRR_UISERIES_CATEGORY = 'UI Series';
    public static final string TRR_CASEACTION_CRE = 'CRE';
    public static final string TRR_CASEACTION_UPD = 'UPD';
    public static final string TRR_REJ_STOP_MS = '1910';
    public static final string TRR_UI_SERIES_STOP_MS = '1803';
    public static final string TRR_CASE_TYPE = 'TRR Inquiry';
    public static final string TRR_CASE_SUBTYPE = 'TRR';
    public static final string TRR_REJ_MS_PATTERN = '19';
    public static final string TRR_UI700_MS_PATTERN = '18';
    public static final string CRM_STOP_MILESTONE = '898';
    public static final String IVRCASETYPE = 'IVR Afterhours';
    public static final string TRR_Third_Party_Sub_View = 'Third Party Submission UI';
    
    //--------------------------------------------- REQ - 400147 -------------------------------------------------------------//
    Public Static final List<String> WEB_CHAT_WQUEUES = New List<String>{'Humana Pharmacy Web Chat','HP Clinical Services Web Chat'};
    public static final string WEB_CHAT_ORIGIN = 'Web Chat';
    
    public static final Integer TwoYearOldDate = -2;
    
    public static final string DateMMddyyyy = 'MMddyyyy';
    public static final string DateTimeGuidanceAlerts ='MM/dd/yyyy hh:mm:ss';
//------------------------ REQ - 399974 --------------------------------//
    
    Public Static final String CRMS_240_GBO_Access_HUM = 'CRMS_240_GBO_Segment_Service_Access'; 

    // Case type is Correspondence Inquiry
    
    Public Static final String CorrespondenceInq = 'Correspondence Inquiry';
    
    //CDO Changes
    public static final Decimal CDO_GIFT_VALUE = 0.0;
    public static final String CDO_Broker_Agent = 'CDO_Broker_Agent';
    public static final String CDO_Partner_Contacts = 'CDO_Partner_Contacts';
    public static final String CDO_Prospective_Patient = 'CDO_Prospective_Patient';
    public static final String CDO_Patient_Record = 'CDO_Patient_Record';
    
//------------------------------------------- REQ - 1566969 -------------------------------------------//
    public static final String CDO_PATIENTEXTERNALIDPREFIX='CDO|';
    
    //Splunk Email Alerts Constants.
    public static final String SPLUNK_ALERT_PATTERN = '^Monitor:\\s+(\\S+)';
    public static final String SPLUNK_FAULT_MESSAGE1 = 'Email did not have a body.';
    public static final String SPLUNK_FAULT_MESSAGE2 = 'Failed to find alert-monitor and/or alert-id in email.';
    public static final String SPLUNK_FAULT_MESSAGE3 = 'Published failure result for metric: ';

//------------------------------------------- REQ - 403693 -------------------------------------------//
    public static final string INTWITH = 'Interacting_With';
    public static final string INTABOUT = 'Interacting_About';
    public static final string ACCOUNT_ENTERPRISEID='EnterpriseID';
    public static final string ACCOUNT_AGENT_AGENTID = 'AgentID';
    public static final string ACCOUNT_PROVIDER_PROVIDERID = 'ProviderID';
    public static final string ACCOUNT_GROUP_GROUPNUM = 'GroupNumber';
    public static final string ACCOUNT_GROUP_CUSTGENKEY = 'CustomerGenKey';
    public static final string UNKNOWN_MEMBER_MEDICAIDID = 'MedicaidID';
    public static final string CASESERVICE_PROCESSDETAIL_INCLUSION='Process Detail';
    public static final string CASESERVICE_PROCESSSUMMARY_INCLUSION='Process Summary';
    
    public static final Integer MAXLIMIT=5;
    public static set<string> setMemberIDTypes = new set<string>{CASESERVICE_MEDICAREID_HUM,CASESERVICE_SSN_HUM,CASESERVICE_MEMBERIDBASE_HUM};
    public static map<string, string> mapAcctIDTypes = new map<string, string>
                                                    {
                                                        ACCOUNT_ENTERPRISEID=>'Enterprise_ID__c',
                                                        ACCOUNT_AGENT_AGENTID=>'Agent_ID__c',
                                                        ACCOUNT_PROVIDER_PROVIDERID=>'Enterprise_ID__c',
                                                        INTERACTIONTYPE_NPIID=>'NPI_ID__c',
                                                        ACCOUNT_GROUP_GROUPNUM=>'Group_Number__c',
                                                        ACCOUNT_GROUP_CUSTGENKEY=>'Enterprise_ID__c'
                                                    };
    //-------------------------REQ-402887-------------------------------//
     Public Static final String PBS_Url_New = 'Applauncher:';
     Public Static final String PBSNew_PermissionSet = 'CRMS_210_AppLauncherPilot';
     Public Static final String PBS_New = 'PBS New';
     Public Static final String PBS_New_Comments = 'PBS New Comments';                                                  


//--------------------------------------------- REQ - 402834 -------------------------------------------------------------//
    Public Static final List<String> Owner_Createdby_Queue_Name = New List<String>{'Provider Payment Integrity','Louisville Other Insurance Investigation','Louisville RSO Oral Grievance Oversight','LV Medicare Member Communications'};
    Public Static final List<String> Owner_Createdby_Queue_Webchat = New List<String>{'Humana Pharmacy Web Chat','HP Clinical Services Web Chat'};
    Public static final string sHumanaPharmacySpecProfile = 'Humana Pharmacy Specialist';  
    Public static final string sCustomerCareSuperProfile = 'Customer Care Supervisor';
    Public static final string PDP_Pilot_PGQ = 'PDP Pilot Humana Pharmacy Calls PGQ';
    
    Public Static final String ADDRERROR = 'ADDR12006';
    
    //--------------------------------------------- REQ - 403609 -------------------------------------------------------------//
    public static final string PRODUCT_TYPE_MGP = 'MGP';
    public static final string PRODUCT_TYPE_MGR = 'MGR';
    public static final string PRODUCT_TYPE_MGF = 'MGF';
    public static final string PRODUCT_TYPE_MPD = 'MPD';

    //------------------------------------------- REQ - 404608 -------------------------------------------//
    public static final string ACCOUNT_AGENT_TAXID = 'TaxID';

   //------------------------------------------- US - 1199202 -------------------------------------------//
    public static final string sMsgDefCode = 'MsgDefCode';
    

    //------------------------------------------- REQ - 396234 -------------------------------------------//
    Public Static final String RSO_HP_PermissionSet_Name= 'CRMS_206_CCSHumanaPharmacyAccess';
    Public Static final map<String,String> CASE_RECORD_TYPES = New map<String,String>
    {'Member Case' => 'HP Member Case',
        'Provider Case' => 'HP Provider Case',
        'Group Case' => 'HP Group Case',
        'Agent/Broker Case' => 'HP Agent/Broker Case',
        'Unknown Case' => 'HP Unknown Case'
    };
    Public Static final map<String,String> HP_CASE_RECORD_TYPES = New map<String,String>
    {'HP Member Case' => 'Member Case',
        'HP Provider Case' => 'Provider Case',
        'HP Group Case' => 'Group Case',
        'HP Agent/Broker Case' => 'Agent/Broker Case',
        'HP Unknown Case' => 'Unknown Case'
    };

    //------------Softphone Changes------------------//
    public static final String SOFTPHONE_READY_FOR_INSERT='Ready For Insert';
    public static final String SOFTPHONE_READY_FOR_DELETE='Ready For Delete';
    public static final String SOFTPHONE_STATUS='Update Failed;Synced;Delete Failed;Ready For Delete';
    public static final String SOFTPHONE_READY_FOR_UPDATE='Ready For Update';
    public static final String SOFTPHONE_SYNCED='Synced';
    public static final String SOFTPHONE_FAILED='Failed';
    public static final String SOFTPHONE_LINKS_ERROR_MESSAGE='Transfer all Agent to another Softphone Link before deleting';
    public static final String SOFTPHONE_SITES_ERROR_MESSAGE='Ensure to transfer all Softphone Agents to another Site manually & delete all the Softphone Links associated to the site.';
    public static final String SOFTPHONE_ERROR_MESSAGE='You cannot manually delete agent record that is already synced in the past, instead mark it as \"Ready For Delete\"';
    //SoftPhone
    public static final Set<String> SOFTPHONE_STATUSES = new Set<String>{'Ready For Insert' , 'Ready For Delete' , 'Ready For Update'};
    public static final String SOFTPHONE_PARSE_METHOD='parseSoftPhoneService';
    public static final String SOFTPHONE_INSERT_FAILED='Insert Failed';
    public static final String SOFTPHONE_UPDATE_FAILED='Update Failed';
    public static final String SOFTPHONE_DELETE_FAILED='Delete Failed';
    public static final String SOFTPHONE_CALLBACK='parseSoftphoneTransfer';
    public static final String SOFTPHONE_WEBSERVICE_BULK_DB_SYNC='AgentSoftPhoneBulkDBSync';
    public static final String SOFTPHONE_WEBSERVICE_DB_SYNC='AgentSoftPhoneDBSync';
    public static final String SOFTPHONE_BULKTRANSFER_ERRORMESSAGE_1 ='Default Softphone Link: You must select a Default Softphone Link.';
    public static final String SOFTPHONE_BULKTRANSFER_ERRORMESSAGE_2 = 'Alternate Softphone Link: You must select a Alternate Softphone Link.';
    public static final String SOFTPHONE_BULKTRANSFER_ERRORMESSAGE_3 = '# Agents to Transfer: You must enter a valid number.';
    public static final String SOFTPHONE_BULKTRANSFER_ERRORMESSAGE_4 = '# Agents to Transfer: Please enter a value less than or equal to # Agents for Transfer under Current Softphone Link Details section.';
         //-------------------------------------------- REQ - 405925 ---------------------------------------------------------------//
    public static final string Error_Code_UpdateCasev4 = '206';
    public static final String Error_Message_UpdateCasev4 = 'An Error Occured While Updating the Case';
    Public Static final String Error_Code_InvalidCaseId = '207';   
    Public static final string Error_Message_IvalidCaseId = 'Invalid Case Id';
    public static final string Error_Message_CutomValidation = 'Feild Custom Validation Rule Excpetion';
    //--------------------------------------------- REQ - 405927 --------------------------------------------------//
    public static final string Error_Code_LastInteractionInfo = '208';
    public static final string Error_Message_LastInteractionInfo = 'Error while fetching Last interaction info!!';
    
    public static final string sInternalAccountType = 'Internal';
    
        
    //Pharmacy Benefits
    public static final String PHARMACYBENEFITS_PARSEMETHOD='parseInCommDSTOTCBalanceV2';

    //---------------------------------------------CRM Retail Start-----------------------------------------------------//
    Public Static final String  AORCALLOUT_SERVICE_NAME = 'AORDetailsFetchService';
    //---------------------------------------------CRM Retail END-------------------------------------------------------//
    

    //-------------- REQ - 409603 START------------------//
    public static final string productCodeMedicalGlobal = 'DRG, FSA,  HLT,  MED,  RX,  CLINICAL, HSA, SAB,  MEDICAL,  LIF,  LTD,  LIFE,  LTC,  OTH,  STD,  UNKNOWN,  VIS,  VISION,  WHOLE LIFE TERM,  WKC';
    public static final string productCodeDentalGlobal = 'ADD,  DEN,  DPL,  ACCHLTH,  CRT,  CRTL,  DENTAL,  STOPLOSS,  VBN,  VLF,  VOLBEN,  VOLLIFE,  LIF,  LTD,  LIFE,  LTC,  OTH,  STD,  UNKNOWN,  VIS,  VISION,  WHOLE LIFE TERM,  WKC';
    public static final string MEDICAREPART_D = 'Yes - Medicare Part D';
    public static final string MEDICAREPART_C = 'Yes - Medicare Part C';
    public static final string MEDICAID = 'Yes - Medicaid';
    public static final string ROUTED_PENDING_ACCEPT = 'Routed Pending Accept';
    public static final string RSO_ORAL_GRIEVANCE_OVERSIGHT_TEAM = 'Retail Service Operations (RSO) Oral Grievance Oversight Team';
    public static final string CRMS_300_HP_Supervisor_Custom = 'CRMS_300_HP_Supervisor_Custom';
    public static final string Customer_Care_Supervisor = 'Customer Care Supervisor';
    //-------------- REQ - 409603 END------------------//
    //----------------------------------Member Icons Related Constants-------------------------------------
    Public Static final List<String> lstAvailablePages = New List<String>{'Member Account','Policy Member','Search', 'Humana Pharmacy'};
    Public Static final List<String> lstAvailablePermissionSets = New List<String>{'CRMS_684_Medicare_Customer_Service_Access','CRMS_240_GBO_Segment_Service_Access','CRMS_685_PCC_Customer_Service_Access','CRMS_300_Humana_Pharmacy_Supervisor','CRMS_205_CCSPDPPharmacyPilot','CRMS_207_PBM_Iconology','CRMS_206_CCSHumanaPharmacyAccess'};
    Public Static final List<String> lstAvailableProfiles = New List<String>{'Customer Care Specialist','Customer Care Supervisor','Humana Pharmacy Specialist'};
    
    Public static final string RXConnectPro_URL = 'RXConnectProURL';
    Public static final string RXConnectPro_Source = 'source';

     //------------------------------------------- REQ - 383958 -------------------------------------------//
     
      public static final string CRM_CON= 'CRM';
      public static final string CLAIMS_MANUAL_ENTRY ='Claims Manual Entry';
      public static final string CLAIM_NUMBER='Claim Number';
      public static final string ONLOAD_CHK='onload';
      public static final string ONCHANGE_CHK='onchange';
      
      //------------------------------------REQ-412009 ------------------------------------------------------//
      public Static final string ParseBillingServiceResponse ='ParseBillingServiceResponse';    

    //-------------------------------------------US826586(REQ - 410441) -------------------------------------------//
    public static final string HP_MEMBERCASE_HUM = 'HP Member Case';
    public static final string HP_UNKNOWNCASE_HUM = 'HP Unknown Case';
    public static final string CLOSED_HP_UNKNOWNCASE_HUM = 'Closed HP Unknown Case';
    public static final string HP_PROVIDERCASE_HUM = 'HP Provider Case';
    public static final string CLOSED_HP_PROVIDERCASE_HUM = 'Closed HP Provider Case';
    public static final string HP_GROUPCASE_HUM = 'HP Group Case';
    public static final string CLOSED_HP_GROUPCASE_HUM = 'Closed HP Group Case';
    public static final string HP_AGENTBROKERCASE_HUM = 'HP Agent/Broker Case';
    public static final string CLOSED_HP_AGENTBROKERCASE_HUM = 'Closed HP Agent/Broker Case';
    
    //--------------------------------------------- 729006 --------------------------------------------------//
      Public Static final List<String> PHARMACY_WQUEUES = New List<String>{'Provider Payment Integrity','Louisville Other Insurance Investigation','Louisville RSO Oral Grievance Oversight','LV Medicare Member Communications'};
      
    public Static final string SwitchOn ='YES'; 
    public Static final string SwitchOff ='NO'; 

    public Static final string CCBASource='CCBA'; 
    public Static Set<String> AlertType = New Set<String>{'GM','GI'}; 
    
        //------------------------------------------- REQ - 411382 --------------------------------------------//
    public static final string DEAA_Template = 'DEAA_Template';
    public static final string DEAA_Origin = 'DEAA';
    public static final string DEAA_ClassificationType = 'Enrollment (RSO)';
    public static final string DEAA_Classification = 'DEAA Plan Change';
    public static final string DEAA_Intent = 'Grace Period Expired';
    public static final string DEAA_AutoCloseComments = 'Opt Out Grace Period has expired. Case being Auto closed due to no more action required.';
    public static final string NO_Complaint_GARights = 'No'; 
    
    //------------------------------------------- REQ - 411383 and REQ - 410422 ----------------------------------------//
    public static final string DEAA_MEMBEROPTOUTOPTION = 'DEAA_MemberOptOutOption';
    public static final string DEAA_ISZAPISERVICECALLED = 'DEAA_IsZAPIServiceCalled';
    public static final string DEAA_OPTION_YES = 'Yes';
    public static final string DEAA_OPTION_NO = 'No';
    public static final string DEAA_MEMBEROPTOUTYES_MILESTONE = '1725';
    public static final string DEAA_MEMBEROPTOUTNO_MILESTONE = '1752';
    public static final string DEAA_AUTOCLOSURE_MILESTONE = '1751';
    public static final string DEAA_ZAPISERVICE = 'Zapi Service';
    public static final string DEAA_SUCCESSRESPHEADER = 'SavePartDOptOutResponse';
    public static final string DEAA_CASEORIGIN = 'DEAA';
    public static final string DEAA_CASETYPE = 'Customer Inquiry';
    public static final string DEAA_CASESUBTYPE = 'Dual Eligible Auto Assign';
    public static final string DEAA_ZAPIDATETIMEFORMAT = 'MM/dd/yyyy hh:mm:ss.ssssss';
    public static final string DEAA_RESPONSETYPE_INFO = 'Information';
    public static final string DEAA_ZAPI_ERRMSG = 'DEAA ZAPI service callout was not made as one or more mandatory fields are missing.';
    public static final string DEAA_ONOFF_SWITCH = '975731';
    
    /*---CDO Approval Process---*/
    public static final string APPROVER_NOT_DEFINED='An approver has not been defined for the selected Assigned to Region.';
    public static final string APPROVER_INACTIVE='The approver for the selected Assigned to Region is no longer an active CDO Profile user.';
    public static final string APPROVAL_STATUS_NOT_SUBMITTED = 'Not Submitted for Approval';
    public static final string CAMPAIGN_IS_REVIEW_NEEDED = 'Yes';
    public static final string CAMPAIGN_STATUS_PLANNED='Planned';
    public static final string PENDING_MANAGER_APPROVAL='Pending Manager Approval';
    public static final string PENDING_MARKETING_APPROVAL='Pending Marketing Approval';
    public static final string PENDING_COMPLIANCE_APPROVAL='Pending Compliance Approval';
    public static final string SALES_CDO_APR_TYPE='Sales';
    public static final string MARKETING_CDO_CAMPAIGN='Marketing';
    public static final string COMPLIANCE_CDO_CAMPAIGN='Compliance';
    public static final string LEGAL_CDO_CAMPAIGN='Legal';
    public static final string CDO_YES = 'Yes';
    public static final string CDO_REJECT_HUM = 'Reject';
    public static final string CDO_RECALLED_HUM = 'Recalled';
    public static final string CDO_PIPE_HUM = '|';
    public static final string RECORDTYPE_CDO_CAMPAIGN='CDO Campaign';
    
    //--------------------------------------------REQ - 791947 - Start -----------------------------------------//
    public static final string prepareServiceDataREST = 'prepareServiceDataREST';
    public Static final String AccumsServiceREST = 'AccumsServiceREST';
    //--------------------------------------------REQ - 791947 End --------------------------------------------//
    
    //-----------------------------------------REQ - 749402 -- Start -----------------------------------------//
    public static final string ProductDental = 'Dental';
    public static final string ProductDentalOrthodontic = 'Orthodontic';
    //-----------------------------------------REQ - 749402 -- End -----------------------------------------//

    //---------------------738134- PR00091318 - MF 9 - Medicare OSB Vision And Fitness--------------//
    public Static final string OSB_Indicator_value = 'O';
    
    // ------------------------ REQ - 842472---------------------------//
    public static final string Humana_Member_Id = 'HumanaMemberId';
    public static final string InComm_Card_Number = 'InCommCardNumber';
    public static final string InComm='InComm';

    //-----------REQ-1035056------------------------------//
    public static final string CCPINQUIRYOFFSWITCH = '1035056';     //CRM To CCP Inquiry Stop Switch

    //------------------------------------REQ-1048944 ------------------------------------------------------//
    public static final String CDO_Patient_Appointment = 'CDO_Patient_Appointment';
    public static final String LEAD_STATUS_PCP_APPT_SCHEDULED = 'PCP Appointment Scheduled';
    
    //------------------------------------REQ-2003668 ------------------------------------------------------//
    public static final String LEAD_STATUS_ClSD_CONVERTED = 'Closed - Converted';

    //Member Maintenance - Demographic Update MBEPlus Continuation Method Name - Req-1038141
    Public Static final String MM_DU_PROCESSRESPMETHOD_HUM ='processMemberResponse';

    //Member Maintenance - Demographic Update Address Standardization Service Name - Req-1038141
    Public Static final String MM_DU_ADDRSTDZNAME_HUM = 'Enterprise_Address_Service';
    Public Static final String MM_DU_SER_CONTENTTYPE_HUM = 'content-type';
    Public Static final String MM_DU_SER_CONECTION_HUM = 'Connection';
    Public Static final String MM_DU_SER_KEEPALIVE_HUM = 'keep-alive';
    Public Static final String MM_DU_SER_SOAPACTION_HUM = 'SOAPAction';

    //Member Maintenance - Demographic Update Outside Service Area Service Name - Req-1038141
    Public Static final String MM_DU_OSANAME_HUM = 'OutsideServiceAreaService';
    
    //-----------REQ-846171------------------------------//
    PUBLIC STATIC FINAL STRING DEMOGRAPHIC_ONOFF_SWITCH = '846171';
    PUBLIC STATIC FINAL STRING CLASSIFICATION_DEMOGRAPHIC = 'Demographics';
    PUBLIC STATIC FINAL STRING MedicareDemographic_process = 'Demographic Update';
    PUBLIC STATIC FINAL STRING CommercialDemographic_process = 'Commercial Template';
    PUBLIC STATIC FINAL STRING MedicareDemographic_Recommended = 'Update Plan Demographics';
    PUBLIC STATIC FINAL STRING CommercialDemographic_Recommended = 'Update Commercial Demographics';


    //Member Maintenance - Demographic Update Outside Service Area Service Name - Req-1038141
    Public static final String MM_CRMS_MEDPERM_NAME = 'CRMS_1200_MedicareMedicaid_Demographic_Update';
    Public static final String MM_CRMS_COMMPERM_NAME = 'CRMS_1210_Commercial_Demographic_Update';
    public static final string CONTACT_RECORDTYPE_PARTNER = 'CDO Partner Contacts';
    public static final string CONTACT_RECORDTYPE_AGENT = 'CDO Agent';
    Public static final String MM_CRMS_HPMEDPERM_NAME = 'CRMS_1220_Pharmacy_Demographic_Update';
    //-----------------------------DF 766 -----------------------------------------------------------------//
     public static final string RXConnect = 'RXConnect';
    public static final string MemberPreference = 'MemberPreference';
    
    //-----------ON/OF Flag for Segment Indicator And LOB-----------------------------//
    
    PUBLIC STATIC FINAL STRING SEGMENT_LOB_ONOFF_SWITCH = '742376'; 
        Public static final String CRM_Enroll_and_Onboard_Access = 'CRM_Enroll_and_Onboard_Access';
     //-------------------------Close/Cancel Cases - US#800050------------------------------------------------//
    public static final string Resolution_Comment = 'ASSOCIATE RESOLUTIONS:';
    public static final string WORK_QUEUE_LV_Screening = 'LV G and A and Correspondence Screening';
    public static final string WORK_QUEUE_PCP_Assignment_Research = 'PCP Assignment Research';
    public static final string Group_LV_Correspondence_Screening_PGQ = 'LV GA and Correspondence Screening PGQ';
    public static final string Group_LV_Correspondence_Screening_PGVE = 'LV GA and Correspondence Screening E PGV';
    public static final string Group_LV_Correspondence_Screening_PGVP = 'LV GA and Correspondence Screening P PGV';
    public static final string MedExpTEMPLATEUNIQUEID = '95C90830-3133'; 
    public static final string PartDTEMPLATEUNIQUEID = 'BC929CD6-E06A';
    public static final string autoRouteStatus_EvalExp = 'Evaluate Expedited Appeal';
    public static final string autoRouteStatus_EvalPartD = 'Evaluate Part D';
 //----------------------------------Invoice Request Process-------------------------------------
 Public Static final String CASEPROCESS_INVOICEREQUEST_PROCESS_HUM = 'Invoice Request'; 
 Public Static final String CASEPROCESS_INVOICEREQUEST_DESC_HUM = 'Invoice Request';
 //----------------------------------Humana Pharmacy Web Issues-------------------------------------
 Public Static final String PHARMACY_WEBISSUES_RECOMMENDED = 'Centerwell Pharmacy Web Issues';
 Public Static FINAL String PharmacyWebIssues_process = 'Centerwell Pharmacy Web Issues'; 
 //----------------------------------Humana Pharmacy Inactivate Rx-------------------------------------
 Public Static final String PHARMACY_INACTIVATERX_RECOMMENDED = 'Inactivate Rx';
 PUBLIC STATIC FINAL STRING PharmacyInactivateRx_process = 'Inactivate Rx'; 
//------------------------------------------Enrollment Welcome Call----------------------------//
    Public Static final String EnrollmentWelcome_Recommended = 'Enrollment Welcome Call';
    Public Static final String EnrollmentWelcome_Process = 'Enroll & Onboard (E&O) Welcome Call';
//-----------------------------------------REQ - 1197867 -- Start -----------------------------------------//
    Public Static final String LVRSOAgentSupport = 'LV RSO Agent Support';
    Public Static final String ARSOSEditCaseWork = 'Agent Retail Service Operation Support (ARSOS) Edit Case Work';
    Public Static final String VantageReopenedCases  = 'Vantage Reopened Cases';
//-----------------------------------------REQ - 1197867 -- End -----------------------------------------//

    //Member Maintenance - Demographic Update Validate Eligibility Service Name
    Public Static final String MM_VE_SER_NAME_HUM = 'MMPValidateEligibilityService';
    Public Static final String MM_VE_SER_CONTENTTYPE_HUM = 'content-type';
    Public Static final String MM_VE_SER_CONECTION_HUM = 'Connection';
    Public Static final String MM_VE_SER_KEEPALIVE_HUM = 'keep-alive';
    Public Static final String MM_VE_SER_SOAPACTION_HUM = 'SOAPAction';
    Public Static final String MM_VE_SER_CONSUMER_NAME = 'CRM';
    
    //Member Maintenance - Demographic Update Member Update Service Name
    Public Static final String MM_UPD_SER_NAME_HUM = 'MMPUpdateMemberService';
    Public Static final String MM_UPD_SER_CONTENTTYPE_HUM = 'content-type';
    Public Static final String MM_UPD_SER_CONECTION_HUM = 'Connection';
    Public Static final String MM_UPD_SER_KEEPALIVE_HUM = 'keep-alive';
    Public Static final String MM_UPD_SER_SOAPACTION_HUM = 'SOAPAction';
    Public Static final String MM_UPD_SER_CONSUMER_NAME = 'CRM';

    //---------------------------- OGO Case recommendation ----------------//
    public static final Id RECORDTYPE_ID_FEEDBACK_TASK = Schema.SObjectType.Task.getRecordTypeInfosByName().get('Service CRM Feedback').getRecordTypeId();
    public static final String TASK_FEEDBACK_ACCEPT_TYPE = 'Feedback Accepted';
    public static final String TASK_FEEDBACK_REJECT_TYPE = 'Feedback Rejected';
    public static final String TASK_FEEDBACK_DEFERRED_TYPE = 'Feedback Deferred';
    public static final String CASE_OGO_POPUP_MSG = ' View';
    public static final String CASE_OGO_POPUP_DEFERRED_MSG = 'Case has not been routed. Please take appropriate action for case disposition.';
    public static final String CASE_OGO_POPUP_ACCREJ_MSG = 'Case has been routed to ';
    public static final String CASE_OGO_POPUP_CLONEACCEPT_MSG1 = 'This case has been routed to ';
    public static final String CASE_OGO_POPUP_CLONEACCEPT_MSG2 = ' View. Please click Continue to create a new clone case and route it to ';
    public static final String RECORDTYPE_NAME_FEEDBACKTASK ='Service CRM Feedback';
    public static final List<String> OGO_SCRUBBER_PERMISSION_SET = New List<String>{'CRMS_401_OGO_Scrubber_Access','CRMS_400_Grievance_Appeals'};
    public static final String CASE_OGO_POPUP_ROGVIEW_ACCEPT_MSG = 'Case requires complaint fields updated before auto-routing to RSO Oral Grievance Oversight';
    public static final String CASE_OGO_POPUP_ROGVIEW_REJECT_MSG = 'The re-opened case has been routed to previous work queue/owner, and has been closed';
    public static final String CASE_OGO_ROG_VIEW = 'Recommended Oral Grievance';
    public static final String CASE_OGO_CBR_VIEW = 'Clarabridge Recommendations';
    public static final Map<String, SObjectField> MAP_CASEINTERACTION_FIELDS = Schema.getGlobalDescribe().get('Case_Interaction__c').getDescribe().fields.getMap(); 
    public static final Map<String, SObjectField> MAP_CASELINK_FIELDS = Schema.getGlobalDescribe().get('Case_Junction__c').getDescribe().fields.getMap(); 
    
    //--------------------------REQ - 1956905 --------------------------
    Public Static final String IA_MORE_THAN_10 = 'IA_MORE_THAN_10';
    Public Static final String IA_6_TO_10 = 'IA_6_TO_10';
    Public Static final String IA_UPTO_5 = 'IA_UPTO_5';
    
     // --------------- create chat transcript service -----------------------------
    Public Static final String sSuccessfulResolutionYes = 'Yes';
    Public Static final String sSuccessfulResolutionNo = 'No';
    
     //--------------------------REQ - 1292117 --------------------------
    Public Static final String CUSTOMER_TYPE_MEMBER = 'MEMBER';
    Public Static final String SOURCE_SYSTEM_NINA = 'NINA';
    Public Static final String DEFAULT_POLICY_END_DATE = '12/31/9999';
    //--------------------------REQ - 1131866--------------------------
    Public Static final String SOURCE_SYSTEM_IVAWATSON = 'IVAWATSON';    
    //--------------------------REQ - 1133858 --------------------------
    Public Static final String CUSTOMER_TYPE_GROUP = 'GROUP';
    Public Static final String CUSTOMER_TYPE_AGENT = 'AGENT';
    Public Static final String GROUP_ACC_RECTYPE_API_NAME = 'Group';
    Public Static final String AGENT_ACC_RECTYPE_API_NAME = 'Agent_Broker';
    Public Static final String GROUP_CASE_RECTYPE_API_NAME = 'Group_Case';
    Public Static final String AGENT_CASE_RECTYPE_API_NAME = 'Agent_Broker_Case';
    Public Static final String COMMERCIAL_B2B_BUTTON_API_NAME = 'Commercial_B2B_Humana_Business_Services';

    //-------------------------------------- US - 1045266 ----------------------------//
    Public Static final String General = 'General'; 
    Public Static final String HOAllOther = 'Home Office All Other';
    
     //----------------------- US - 1186355 ----------------------------------------------------------------------------//
    Public Static final String CASE_CLASSIFICATION_TYPE_MEDICAID_CALLS = 'Medicaid Calls';
    Public Static final String Louisville_Medicaid_Calls_Support = 'Louisville Medicaid Calls Support';
    Public Static final set<String> CALLS_RSO_WORKQUEUES_SET = new Set<String>{'Louisville After Hours Calls','Louisville Group Medicare Operations','Louisville RSO Calls Support','LV Partner Site RSO Calls Conduent','LV Partner Site RSO Calls Everise','LV Partner Site RSO Calls Faneuil','LV Partner Site RSO Calls HGS','LV Partner Site RSO Calls Ramp Partner','LV Partner Site RSO Calls Results Co','LV Partner Site RSO Calls Senture'};
    public static final String CLASSIFICATION_PCP_CHANGE = 'PCP Change';
    public static final String INTENT_PCP_CHANGE_MADE = 'PCP Change Made';

    //-------------------------------------- US - 825176  ----------------------------//    
    public static final String COBCALLBACKMETHOD = 'mbePlusCallBack';  

//-----------------------------User Story 1248496 -----------------------------------------------------------------//
    public static final String HTTPREQUEST_TOTALRECORDSEXCEED_HUM = '1000'; 
    // Used to Cases to breated via case service from RTM
    public static final set<String> HPClassificationTypeset = new set<String>{'HP RxE Calls'};

 //----------------------- DF - 1323 -------------------------------------------------------------------------------//
    public static final string STRING_CLEAR = 'clear';

//-----------------------------REQ - 760189-----------------------------------------------------//
     Public Static final String PHARMACY_CURRENT_QUEUE = 'FINANCE';

     //---------------------CDO Scan Card COnstants----------//
    Public static final string ALREADY_ASSIGNED = 'The scanned card is already assigned to ';
    public static final string ASSIGNED_TO_OTHER = 'The scanned card is already assigned to a different Lead or Patient Account.';
    public static final string HOME_PAGE_RECORD_NOT_FOUND = 'No matching lead or patient was found, please try again.';
    public static final string ASSIGNMENT_SUCCESS = 'The scanned card was successfully assigned to ';
    public static final string CAMPAIGN_ERROR = 'This campaign is not yet approved, as a result, we cannot add members to this campaign.';
    public static final string CAMPAIGN_MEMBER_ERROR = 'The patient or prospect has already been added as a member of this campaign.';
    public static final string CAMAPIGNMEMBER_NO_MATCH = 'No match was found for the scanned card.';
    public static final string UNEXPECTED_ERROR = 'An unexpected error has occurred. Please contact your administrator.';
    public static final string SWITCH_OFF_ERROR = 'This feature is currently unavailable. Please contact your administrator with any questions.';
    public static final string CARD_RECORDTYPE = 'CDO My Wellness Journey Card';
    public static final string INVALID_CARD_ERROR = 'Please ensure you are scanning a My Wellness Journey passport.';
   
    //-----------------------------REQ - 1137828-----------------------------------------------------//
    Public Static final String MEMBERCCP = 'AccountPage';
    Public Static final String PolicyCCP = 'GroupPolicy';
    
    //-----------------------------REQ - 1170241-----------------------------------------------------//
    Public Static final String POLICYMEMBERCCP = 'PolicyMemberPage';
    Public Static final Integer StartCountGCPCCP = 1;
    Public Static final Integer EndCountGCPCCP = 50;
    public Static final String PARSEINQUIRYSUMMARY = 'parseInquirySummary';
    public Static final String inquiryButtonRange = '500';
    public Static final String CriteriaForGCPCCP = 'IQA';
    public Static final String PharmacyUserCCPGCP = 'PH';
    public Static final String NonPharmacyUserCCPGCP = 'NP';
    public Static final String IdTypeMbrGenKeyCCPGCP = 'MemberGenKey';
    public Static final String IdTypeEnterpriseIdCCPGCP = 'EnterpriseId';
    public Static final String MTypeCCPGCP = 'M';
    public Static final String GTypeCCPGCP = 'G';
    public Static final String PTypeCCPGCP = 'P';
    public Static final String ATypeCCPGCP = 'A';
    public static final Integer TimeOutStatusCode = 2000;
    public Static final String TimeoutErrorMsg = 'The timeout was reached, and the server did not get a chance to respond. Service Name: Data Retention Inquiry Summary';
    
    //-----------------------------REQ - 1318083 -----------------------------------------------------//
    public Static final String PARSEINQUIRYDETAILS = 'parseInquiryDetails';
    public Static final String PARSEINQUIRYNOTES = 'parseInquiryNotes';
    public Static final String PARSEINQUIRYDETAILAUDIT = 'parseInquiryAudit';
    public Static final String PARSEINQWEBRESPONSE = 'parseInquiryWebResponse';
    public Static final String PARSEINQUIRYDETAILATTACHMENT = 'parseInquiryAttachment';
    public Static final String Attachment_FMUrl = 'https://gca-bps.humana.com:6001/Forms/component/LaunchForms?formObjectId=0900092981189ba7';
    public Static final String Attachment_ILUrl = 'http://calldms.humana.com/calligodms/livelink.exe?func=LL.login&username=huminc&password=calligo$2&NextURL=//calldms.humana.com/calligodms/livelink.exe/fetch%3ffunc=doc.Fetch%26nodeId=93861236';
    public Static final String Attachment_MRUrl = 'https://hss.humana.com/MedicalRecords/verifyLanding.aspx?UserRequestId=845528';
    
    //----------------------------REQ - 1338123 ---------------------------------------------//
    public Static final String PARSEINQTASKDETAILS = 'parseInquiryTaskDetail';
    public Static final String DUEDATEFORMATONE = '09/09/9999';
    public Static final String DUEDATEFORMATTWO = '9/9/9999';
    public Static final String DUEDATEFORMATTHREE = '09/9/9999';
    public Static final String DUEDATEFORMATFOUR = '9/09/9999';

     //----------------------- US#1393270 ------------------------------------------------//
    public static final string sExpCalssification = 'G&A';
    public static final string sExpIntent = 'Expedited';
    public static final string sExpTopic = 'Expedited';
     public static final string sPartDCalssification = 'Rx';
    public static final string sPartDIntent = 'Part D Redetermination';
    public static final string sPartDTopic = 'Part D Redetermination Review';
    
    //----------------------- PR00088383 - MF6 - US1334690 and US1334714 ------------------------------------------------//
    public static final string sDocSourceMRM = 'MRM';
    public static final string sMRMViewerURL = 'MRM_HUMREC_VIEWER';
    public static final String sUS1334690 = '1334690';
    Public Static final String sUS1334714 = '1334714';
    
    Public static final string sPersonAccount = 'Person Account';
    public static final string sBusinessAccount = 'Business Account';
    
    //New Alert Engine NBA MF-10 
    public static final Integer STRATEGY_MAXRESULTS = 25;

    //Workqueue Name
    Public Static final String WORK_QUEUE_GREEN_BAY_GRIEVANCE_APPEALES = 'Green Bay Grievance and Appeals';
    public static final String COMPLAINT_NO = 'NO';
    //----------------------------------Native American Indicator-------------------------------------  
    Public Static final String Race_Code_G = 'G';
    Public Static final String Race_Code_i  ='i';
    Public Static final String Namerican_Indc_Y  ='Y';
    Public Static final String Namerican_Indc_N  ='N';
    
    //------------------------US-1629617--------------------------------------------------------//
    Public Static final String MBE_SM_SER_NAME_HUM = 'MBESearchMemberService';
    Public Static final String MBE_SM_SER_CONTENTTYPE_HUM = 'content-type';
    Public Static final String MBE_SM_SER_CONECTION_HUM = 'Connection';
    Public Static final String MBE_SM_SER_KEEPALIVE_HUM = 'keep-alive';
    Public Static final String MBE_SM_SER_SOAPACTION_HUM = 'SOAPAction';
    Public Static final String MBE_SM_SER_CONSUMER_NAME = 'CRM';
    
    //----------------------- US#1654207------------------------------------------------//
    public static final string MEMBERIDDEPENDENTCODE = 'MemberIDDependentCode';
    public static final string MEMBERIDBASE = 'MemberIdBase';
    //----------------------------REQ - 1457838---------------------------------------------//
    public Static final String OOAIndicator = 'OutOfAreaIndicator';
    //----------------------------REQ - 1450427---------------------------------------------//
    public Static final String MBEFaultCode= '83.1.1';
    public Static final String PBEFaultCode= '111111';
    public Static final String PBEFaultCode1= '000305';

    //-----------------------US-1755360----------------------------------------//
    Public Static final String MM_DU_ADDRSTDZNAME_HUM_V2 = 'Enterprise_Address_Service_V2';
    
    public static final String Classification_Advanced_Resolution_ART = 'Advanced Resolution (ART)';
    public static final String Intent_PAR_Provider_Not_Accepting = 'PAR Provider Not Accepting';
    Public static final String Medicaid_PAR_Provider_Not_Accepting ='Medicaid PAR Provider Not Accepting';
    
    public static final String MHK_CASE_TYPE = 'MHK Dispute Task'; 
    
    //----------------------- US#1083065------------------------------------------------//
    public static final String Service_Center_Provider = 'Provider';
    Public Static final String Department_Correspondence_Task_MHK = 'Correspondence Task to MHK';
    Public Static final String Department_Correspondence_MHK = 'Correspondence to MHK';
    Public static final string CommentsErrorMsg = 'Please enter at least one comment to proceed with this transfer to MHK';
    public static final string NonMHKTaskErrorMsg = 'Service Center and Department combination not allowed, please update to valid entry.';   

    public static final String MHK_Switch = '1414643';
    //User Story 1083065: PR00094923: MF2.6.1a CRM CASE/TASK VIEW- CRM Review and Update Task/Case
    Public Static final String sMHKTaskOrigin = 'Correspondence';
    //-----------------------US-1748357----------------------------------------//
    Public Static final String TRANSFER_PER_PROCESS = 'Transferred Per Process';

    
    //------------------------UnifiedSearch - Constants---------------------------------------//
    public Static final Integer UNIFIEDSEARCH_DURATION = 10000;
    public Static final String UNIFIEDSEARCH_ORG = 'CoveoOrgID';
    public Static final String UNIFIEDSEARCH_SESSIONTIMEOUT = 'CoveoSessionTimeOutValue';
    public Static final String UNIFIEDSEARCH_SERVICE = 'CoveoService';
    public static final String UNIFIEDSEARCH_ERROR = 'Error';
    //----------------------------REQ - 1801197---------------------------------------------//
    public Static final String CRMSTRIDESAPP = 'CRM Strides';
    public static final String COVEOCASEPAGENAME = 'L_CRMS_Case_Detail';
    public static final String UNIFIEDSEARCHPS = 'CRMS_310_UnifiedDocumentSearch';

     
   /*---------------------------PR00096072 -MF-1 - US# -1260069 -----------------------------*/
    
    public static final string sConfirmedVeteran = 'confirmed-veteran';
    public static final string sIdentifiedVeteran = 'identified-veteran';
    
    //US1874735
    public static final List<String> lstAutoRouteStatus = New List<String>{'Evaluate Good Cause','Evaluate Expedited Appeal','PCP Reassignment','Termed in Error','Evaluate Part D'};

    /*---------------------------PR00096072 -MF-2,3,4- US#-1260081-----------------------------*/
    
    public static final string SUPDATE_SUCCESSDELAY_MESSAGE ='Veteran information saved successfully in the Veteran system. It will take 24-48 hours to reflect in the CRM system';
    public static final string VETERANSECTION_MESSAGE ='Note: Any changes to Veteran information will take 24-48 hours to reflect in CRM system.';
    public static final string SUPDATE_SUCCESSCRM_MESSAGE ='Veteran Information saved successfully'; //SUPDATE_SUCCESSCRM_MESSAGE
    public static final string SUPDATE_FAILCRMMESSAGE ='Failed to update Veteran Information in CRM system'; //SUPDATE_FAILCRMMESSAGE
    public static final string SUPDATE_FAILVETERANMESSAGE = 'Failed to update Veteran Information in Veteran system'; //SUPDATE_FAILVETERANMESSAGE
    public static final string SVALIDATION_ERROR_MESSAGE = 'Cannot Save Veteran Information as validation rule failed';  //SVALIDATION_ERROR_MESSAGE
    public static final string SUPDATE_ERROR = 'Fail to update information in Veteran System'; // SUPDATE_ERROR
    public static final string SUPDATE_FAIL_ERROR ='Fail to update Veteran information'; //SUPDATE_FAIL_ERROR
    public static final string SCRM_SOURCE ='crm-service-api'; //SCRM_SOURCE
    public static final String STIMESTAMP  ='yyyy-MM-dd\'T\'HH:mm:ss'; //STIMESTAMP
    public static final String SUNKNOWN ='Unknown';    
    Public static final String SVETERAN_SAVE_SERVICE = 'VeteranSaveWebServiceCallout';
    Public static final String SVETERAN_GET_SERVICE = 'VeteranGetWebServiceCallout';
    public static final String SINFO = 'Info';
    public static final String SCALLBACK ='processVeteranResponse';
    public static final String VETERAN_GETSERVICE_CALLBACK ='processVeterantrackingResponse';
    Public Static final String SFALSE ='False';
    
    /*---------------------------REQ - 2067890 - T1PRJ0002533 - MF 1 - CDO Patient Relations: Add Gift to All Campaign Members -----------------------------------------------------------------------*/
   
    Public Static final String LEAD_TYPE_CAMPAIGN_MEMBER = 'Lead';
    Public Static final String CONTACT_TYPE_CAMPAIGN_MEMBER = 'Contact';
    Public Static final String NO_OF_CAMPAIGN_MEMBERS = 'Campaign Members: ';
    Public Static final String CAMPAIGN_MEMBER = 'Campaign Member: ';
    Public Static final String GIFTS_GIVEN_ERROR_CODE ='New Gifts given: Add to All failed on -';
    Public Static final String GIFTS_SUCCESSFULLY_ADDED = 'Gifts Successfully Added: ';
    Public Static final String GIFTS_FAILED_TO_ADD ='Gifts Failed to Add: ';
    Public Static final String LEADS_ALREADY_CONVERTED = 'Leads Already Converted*: ';
    Public Static final String DUPLICATES_EXCLUDED = 'Duplicates Excluded: ';
    Public Static final String AGENTS_PARTNERS_EXCLUDED = 'Agents/Partners Excluded: ';
    Public Static final String LEADS_ALREADY_CONVERTED_MSG = '*Gifts cannot be attached to converted Leads. Please add the gift to the Patient Account.';
 
    
    //US2051449
    public static final List<String> AUTOSELECTGA_SERVICEMODELTYPES = new List<String>{'Humana Pharmacy','Humana Specialty Pharmacy'}; 
    
    /*---------------------------REQ - 2067890 - T1PRJ0002533 - MF 1 - CDO Patient Relations: Correct Total of Gifts Given This Year (Second Run) -----------------------------------------------------------------------*/
    Public Static final String GIFTS_RESET_FAILED = 'Gifts Reset failed for ';
    Public Static final String ERROR_DETAILS = 'Error Details - ';
    Public Static final String GIFTS_RESET_BATCH_FAILED = 'CDO Batch Processing for Gifts Reset failed on - ';
    
    //US2188419
    public static final String LOGCODEHSP_CLASSIFICATIONTYPE = 'HP Specialty Calls';
    Public Static final String LOGCODEHSP_SERVICEMODELTYPE = 'Humana Pharmacy';
    
    // US2578074 
    Public Static final String ContractProtectedData = 'Contract Protected Data';

    //US2786833
    Public static final String RCC_WebChat_Users = 'Web Retail Service Operations RSO Chat';

    //US 5107579
    Public static final string CLINICAL_SERVICES_QUEUE = 'Humana_Pharmacy_Clinical_Services';
}