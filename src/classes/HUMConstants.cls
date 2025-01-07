/********************************************************************************************************************************************************
Apex Class Name :  HUMConstants
Version: 1.0 
Created Date     : MAY 21 2014
Function         : Constants declaration here that are going to be used across the org.
Modification Log : 
* Developer               Cose review                   Date                 Description
* -------------------------------------------------------------------------------------------------------------------------------------------------------               
* Sagar Tapadia                                      05/21/2014              Original Version
* Manish Kumar              15771                    08/05/2014              Added static variables for Case/Task routing
* SuryaKumari Medicherla    24701                    07/15/2015              Added static variables for Task Edit/Close functionality(Defect:180311.0002)
* Kritika P Popat           24704                    07/16/2015              Added static variables for HUMEligibilityInfoController
* Pradeep Raju              24021                    07/21/2015              Added variables for Interaction Log
* Pradeep Raju              24572                    08/03/2015              Added variables for Alerts
* Harshith Mandya                                    07/30/2015              Moved Custom labels/Custom Settings in Search
* SuryaKumari Medicherla                             08/11/2015              Added static variables for REQ-168974
* Asha Deepika Koyyana      25637                    08/21/2015              Added static variables for AVF Error logs     
* Pradeep Raju              25840                    08/26/2015         `    Added static variables for REQ-202260
* Pradeep Raju              25840                    09/01/2015              Updated the class to add New variables for REQ-202260
* Shreya Choodamani         26106                    09/04/2015              Updated the constant value of EXT_ID_AGENT to fix defect#:198511
* Gargey Sharma             26155                    09/10/2015              Added constants for HUMServiceTesterController
* Prasanthi Kandula         26064                    09/07/2015              Replaced Labels with Constant Values for HUMCOBProcessController
* SuryaKumari Medicherla    26141                    09/07/2015              Moved constants of HUMLaunchCaseDisenrollmentExtension Class
* Harshith Mandya           26141                    09/15/2015              Incorporated review comments
* Shreya Choodamani         26106                    09/23/2015              Added constant values for defect#: 188140.0001
*Sruthi Adivikolanu         26599                    09/16/2015              Moved constants from custom setting to Constants class for HUMtsklistviewcontroller
*Sruthi Adivikolanu         26599                    09/30/2015              Moved constants from Labels to Class for HumCasehistorycomponent Controller
* Kritika Popaat            26546                    10/01/2015              Moved constants from Labels for HUMTaskEditPageExtension
* SuryaKumari Medicherla    27320                    10/30/2015              Created New constant variables for REQ-198627 And REQ-198628
* Praveen Kumar Parimi      27872                    11/17/2015              Added Constants for Group Search Controller. 
* Praveen Kumar Parimi      28018                    11/20/2015              Added Constant for GroupDetailSection_S_T_HUM. 
* Praveen Kumar Parimi      28018                    11/23/2015              Added Constant of sDummyBenValue for  GroupDetailSection_S_HUM.
* Harshith Mandya           27805                    11/19/2015              Added Constants for Pharamacy Service ( REQ - 236993)
* Sharan Shanmugam          28708                    12/16/2015              Modified value of HUMCCPEligibilityRSO to Eligibility (RSO ENR)                                    
* sruthi Adivikolanu        28156                    11/26/2015              Moved constants used in Huminqhistorycontroler from Custom setting/Custom Labels to Class (Req - 198629 Legacy Contact history on Policy)
* Harshith Mandya           28341                    01/12/2015              Addec Constants for Medicare DAC ( REQ - 215100)
* Cody Sanders              29406                    02/03/2016              Added Constants for HUMTaskListViewController (REQ-194306)
* Avinash Choubey           29427                    02/05/2016              Addedd case edit page constants 
* Avinash Choubey                                    02/18/2016              Updated HUMClosedCaseCRMToCCPInquiries constants as given in REQ- 157160
* Sharan Shanmugam                                   02/18/2016              Added constants for REQ - 157784       
* Avinash Choubey           30240                    03/02/2016              Added constant related to CI changes
* Avinash Kumar Choubey     30267                    03/03/2016              Added constant related to CI changes \
* Prerna PRasad             30452                    03/12/2016              Added constants for Classification Type And Classification as per REQ - 241728  
* Pradeepkumar Dani         30467                    03/25/2016              Added Constants related to Value of benefits functionality
* Samridh Manucha           30467                    03/25/2016              Added Constants for MBE service REQ - 241768
* Aviansh Choubey           30467                    03/25/2016              Added Constants for MBE service REQ - 244445
* Vishal Verma              30727                    03/30/2016              Added constant for Policy Member EndDate   
* Harshith Mandya                                    04/06/2016              Defect fix# 221133.0001
* Samridh Manucha                                    04/11/2016              REQ - 237144
* Sharan Shanmugam          30046                    02/26/2016              Added constant VALUE_ONEFOURTHREEFOURNINE as part of REQ - 252546  
* Pradeepkumar Dani         24929                    04/13/2016              Updated VOB Constants for CAS Policy Check
* Aviansh Choubey                                    03/25/2016              Added Constants for MBE service REQ - 241730
* Avinash Choubey                                    04/18/2016              Design Change in All Accums Req ( 237142, 246888 etc)
* Vishal Verma              26826                    05/06/2016              Added constant for MasterServices value 
* Pradeepkumar Dani         26877                    05/06/2016              Added constant for GeneralBenefits value 
* Sharan Shanmugam          26877                    05/09/2016              Added constants for REQ - 176873
* Pradeepkumar Dani         26877                    05/09/2016              Constants for MentorRedirect_C_HUM - Mentor Link on Benefit Search Component
* Pradeepkumar Dani         27609                    05/18/2016              Added constants for Benefits_D_HUM
* Vishal Verma              28638                    05/31/2016              Modified EPSS custom setting name
* Shikhar Mehta                                      06/20/2016              Added one constant for REQ 237000
* Apoorv Jain                                         08/25/2016             REQ - 269936 : VOB -Mentor Link for All segments
* Rajesh Chandgothia                                 12/29/2016              REQ - 291038 Demographic section  
* Prafull Verma                                      04/05/2017              REQ - 306322 : SF -TECH - Softphone routing optimization
* Amar Gadewar										 04/19/2017				 REQ - 311385 Benefits Grid Fix for 	Medicare Medicaid Policy
* Rajesh Chandgothia        89722                    04/20/2017              REQ 305690,310761,308313 Create Edit Order Subtab
* Albin Arul                90774                    05/05/2017              REQ - 311466: New Pharmacy Case recordtype changes
* Mohammed Noor             96594                    05/10/2017              REQ - 311432 Softphone / CRM Results for All Family Members by searching using
*                                                                            Member Id only And launch optimization
*Prafull Verma              99782                    06/07/2017				 REQ - 304829: Added New error Messages for Medicare Enrollment Case Account search
*Prafull Verma              118513                   08/09/2017              REQ - 326174 Production issue in Provider Search for NPIID
*Sai Ayyagari               148971                   11/16/2017              REQ - 337124 Fix for Without Sharing Query Is taking > 10 seconds
*Prafull Verma                                       02/21/2017              Added Constaint for Appeal Recordtyp Case
*Anurag Shah                201051                   03/13/2018              REQ - 354520: SF -USER - Research / Attachments - MA Post Claims Summary.
* Satyam Malviya			203380                   3/20/2018				 REQ - 350793 -  MA Post Associated Claims Section
*Vandana Chaudhari                                   02/13/2018              REQ - 352485 - Tech - SF - 85578 - Search User Interface Updates for ResultType
* Satyam Malviya			211170                   04/05/2018			     REQ -356659 -Added EDIT MODE - Associated Claims Section
* Suraj Patil                 		    			  04/11/2018        	 REQ - 359093, REQ - 351208, REQ - 354346, REQ - 351171 Task Edit Page for Appeal task
* Prafull Verma             215949                    04/09/2018             REQ-359106 SF - TECH - Research/Attachments - Code Edit Denial - Event Entry
* Satyam Malviya            214125                    04/11/2018             REQ - 333161 - SF - USER - DECISION                            
* Suraj Patil				213522					 04/11/2018			  	REQ - 351207 -  SF - USER - Research/Attachments - Task Section
* Pallavi Ravishankar       214921                   04/12/2018              REQ - 350326 - Case Coding - Added 'Code Verified' event
* Vandana Chaudhari  		220488   	             04/25/2018              REQ - 355781 CR788: Web Chat Auto Create Case (CMO CR5548)  
*Vandana Chaudhari                                   04/26/2018              REQ - 352485 - Tech - SF - 85578 - Search User Interface Updates
* Sumit Sharma  			 	           		     05/02/2018         	 REQ - 355781 - Auto Case creation from web chat defect fixed
* Satyam Malviya            239709                   06/07/2018              REQ - 363556 - SF - Tech Case Edit Rollback
*Lakshmi Vutukuri                                    06/05/2018              REQ - 363582 - - Case Coding - Removed 'Code Verified' event.
*Praveen Kumar Parimi      	242821               	 06/05/2018              REQ - 366101 - -Change the Patten of Birthdate.
* Amar Gadewar										 07/03/2018				 REQ - 367117 - AHT Clean up  
* Amar Gadewar										 07/06/2018				 REQ - 367788 - Ability in CRM Console to display a News Ticker at the footer of CRM console (CRM)  
* Vandana Chaudhari                                  06/22/2018              REQ - 356041 - Display Password
* Vandana Chaudhari     			 		         07/20/2018         	 REQ – 370601 CA Ticket - 7646745 Fix - Web Chat Auto Create Case  
* Archana Das                                   	 07/12/2018              REQ - 367852 - Change Constant value for Suffix as per softphone parameters   
* Pallavi Ravishankar      268909              8/07/2018              REQ - 361926  - Added Constant for Task type = 'Extension Request' 
* Vandana Chaudhari                                  08/28/2018              NBA MF4-Guidance Alert
* SUmit Pharande 															 NBA MF3/4 Rollback
* Satyam Malviya                                     11/27/2018              NBA MF-4  
* Dinesh Subramaniyan				     04/18/2018		    REQ - 396416 - Modify logic for CAS benefit grids to auto-populate
* Yogesh Gupta										05/02/2019              REQ-388831 Used for REQ-388831 to hold max count for SOSL result
* Syed Mubarak T 										04/30/2019					REQ - 380470 Application Search in Search Enrollment
* Yogesh Gupta										5/15/2019				Req -388832 - Regex for identify alphanumeric value 
* Seema Kaulgi                                       03/15/2019              REQ - 379048  SF - Ability for all CRM users to SEARCH for member specific Transaction Reply Reports (TRR) in CRM
* Sumanth Naredla                                      07/08/2019                 REQ - 399974 - User Story - News Feed Specific to GBO (CRMS-GBO Only) (ID# 36)
* Chetan Sindhe				400121					 07/15/2019				 Added Static Instance Fields HUMSearchTypeAccount and HumGCMUnknownMemberRecordType for new record type for Visitor.
* Rakesh Patni									08/20/2019				   REQ - 406104. Launch Compass Functionality
* Sagar MN                                          2/25/2020                  Updated First Name and Last name error message on account edit warning message
* Asish Behera                                      2/25/2020                  Updated Sysinfo constant to accept System Information
* Asish Behera                                      06/23/2020                Maces Unit Number constant added as part story 1197812
* Gaurav Dharra									09/22/2020					Added community names and permission set for Jive communities
* Atia Uzma                                          01/21/2021                   User Story 1186358: PR00091574 - MF 1 - Quality - "Medicare Expedited Appeals Template" Updates (CRM)
* Isha Gupta                                      03/29/2021                Added constants for IVR data of Interaction object
* Harshada Kamble                                 01/05/2023                US-3959734 : Salesforce Genesys CRM Authentication Status should not change.
* Harshada Kamble                                 02/15/2023                US 3979946: T1PRJ0036776: PCC VOC Survey Transfer Identification 
********************************************************************************************************************************************************/

Public Class HUMConstants
{
    //string for boolean variable TRUE 
    Public Static final Boolean FLAG_TRUE = True;

    //string for CF value
    Public Static final String CF_VALUE ='CF';

    //string for taskID value
    Public Static final String TASKID = 'taskID';

    //string for LKID value
    Public Static final String LKID_VALUE = '_lkid';

    //string for caseID value
    Public Static final String CASEID = 'caseID';

    //string for saveURL value
    Public Static final String SAVEURL = '&saveURL=%2F';

    //string for boolean variable FALSE
    Public Static final Boolean FLAG_FALSE = False;

    //empty string
    Public Static final String EMPTY_STRING = '';

    //NULL
    Public Static final String NULL_VALUE = NULL;

    //string for case origin    
    Public Static final String CASE_ORIGIN='Inbound Call';

    //string for case type
    Public Static final String CASE_TYPE='Customer Inquiry';

    //string for case status
    Public Static final String CASE_STATUS='In Progress';

    //string for interacting about type
    Public Static final String INTERACTING_ABOUT_TYPE='Member';

    //string for interacting with type
    Public Static final String INTERACTING_WITH_TYPE='Agent';

    //string for GA rights on case
    Public Static final String GA_RIGHTS_GIVEN='No';

    //String for complaint on case
    Public Static final String COMPLAINT = 'No';

    //string defining name of Task Object
    Public Static final String OBJECT_TASK = 'Task';

    //string for EDIT Task
    Public Static final String EDIT_TASK = 'Edit Task';

    //string for New TASK
    Public Static final String NEW_TASK = 'New Task';

    //string defining name of Case Object
    Public Static final String OBJECT_CASE = 'Case';

    //string for interacting about group type as Unknown
    Public Static final String INTERACTING_ABOUT_GROUP_TYPE='Unknown-Employer';

    //string for interacting with group type as Unknown
    Public Static final String INTERACTING_WITH_GROUP_TYPE='Unknown-Employer';

    //string for interacting about agency type as Unknown
    Public Static final String INTERACTING_ABOUT_AGENCY_TYPE='Unknown-Agent';

    //string for interacting with agency type as Unknown
    Public Static final String INTERACTING_WITH_AGENCY_TYPE='Unknown-Agent';

    //string for unknown group user
    Public Static final String UNKNOWN_GROUP_USER='UnknownGroup';

    //string for defining object as Account
    Public Static final String OBJECT_ACCOUNT = 'Account';

    //string for defining object as Policy member
    Public Static final String OBJECT_POLICYMEMBER = 'Policy_Member__c';

    //string for group webservice client name
    Public Static final String GRPSVC_CLIENT_NAME = 'CCP';

    //string for group web service app user
    Public Static final String GRPSVC_APP_USER = 'MXG0195';

    //string for group web service platform
    Public Static final String GRPSVC_PLATFORM = 'EM';

    //string for agency web service key
    Public Static final String AGENCYSVC_KEY = '56ivr@979';

    //string for agency web service team id
    Public Static final String AGENCYSVC_TEAMID = 'IVR';

    //string for alerts member account type
    Public Static final String MEMBER_ACCOUNT = 'M';

    //string for alerts provider account type
    Public Static final String PROVIDER_ACCOUNT = 'P';

    //string for alerts group account type
    Public Static final String GROUP_ACCOUNT = 'G';

    //string for alerts agency account type
    Public Static final String AGENCY_ACCOUNT = 'A';

    //string for alerts broker account type
    Public Static final String BROKER_ACCOUNT = 'B';

    //string for Member
    Public Static final String MEMBER = 'Member';

    //string for provider
    Public Static final String PROVIDER = 'provider';

    //string for group
    Public Static final String GROUPSTRING = 'GROUP';

    //string for group
    Public Static final String GROUP_TYPE= 'Group';

    //string for group
    Public Static final String PROVIDER_TYPE= 'Provider';

    //string for agency type
    Public Static final String AGENCY = 'Agency';

    //string for broker type
    Public Static final String BROKER = 'Broker';

    //string for agent/broker
    Public Static final String AGENT_BROKER = 'Agent/Broker';

    //string for guidance mandatory alert
    Public Static final String GUIDANCE_ALERT_TYPE_GM = 'GM';

    //string for guidance informational alert
    Public Static final String GUIDANCE_ALERT_TYPE_GI = 'GI';

    //string for non-guidance informational alert
    Public Static final String NON_GUIDANCE_ALERT_TYPE_IN = 'IN';

    //string for non-guidance mandatory alert
    Public Static final String NON_GUIDANCE_ALERT_TYPE_MD = 'MD';

     //string for group + member
    Public Static final String ALERT_SHOW_TO_GROUP = 'Group + Member';

    //string for dependent
    Public Static final String RELATIONSHIP_TYPE1 = 'Dependent';

     //string for spouse
    Public Static final String RELATIONSHIP_TYPE2 = 'Spouse';

     //string for disabled dependent
    Public Static final String RELATIONSHIP_TYPE3 = 'Disabled Dependent';

   //string for child
    Public Static final String RELATIONSHIP_TYPE4 = 'Child';

    //string for employee
    Public Static final String RELATIONSHIP_TYPE5 = 'Employee';

    //string for alert location CI
    Public Static final String ALERT_LOCATION_TYPE1 = 'CI';

    //string for alert location ME
    Public Static final String ALERT_LOCATION_TYPE2 = 'ME';

    //string for alert default reason
    Public Static final String ALERTS_DEFAULT_REASON = 'Please select reason';

    //string for alerts request
    Public Static final String ALERTS_SOAP_XML1 = '<CallerContext><ContactID>';

    //string for alerts request
    Public Static final String ALERTS_SOAP_XML2 = '</ContactID><InquiryID>0000</InquiryID><User><UserID></UserID><TeamID>0</TeamID>';

    //string for alerts request
    Public Static final String ALERTS_SOAP_XML3 = '<SiteID>0</SiteID><CRDType>01</CRDType><DeptID>0</DeptID></User><Inqr><MGAPType>';

    //string for alerts request
    Public Static final String ALERTS_SOAP_XML4 = '</MGAPType><MGAPSubType>';

    //string for alerts request
    Public Static final String ALERTS_SOAP_XML5 = '</MGAPSubType><GenKey>';

    //string for alerts request
    Public Static final String ALERTS_SOAP_XML6 = '</GenKey></Inqr><Inqa><MGAPType>';

    //string for alerts request
    Public Static final String ALERTS_SOAP_XML7 = '</MGAPType><MGAPSubType>';

    //string for alerts request
    Public Static final String ALERTS_SOAP_XML8 = '</MGAPSubType><GenKey>';

    //string for alerts request
    Public Static final String ALERTS_SOAP_XML9 = '</GenKey></Inqa></CallerContext>';

    //string for unknown Member
    Public Static final String UNKNOWN_MEMBER = 'Unknown Member';

   Public Static final Integer HUMLimitValue = 3500;


    //string for unknown provider
    Public Static final String UNKNOWN_PROVIDER = 'UnknownProvider';

    //string for unknown Group
    Public Static final String UNKNOWN_GROUP = 'UnknownGroup';

    //string for unknown Agency
    Public Static final String UNKNOWN_AGENCY = 'UnknownAgency';

    //string for Not Develivered Alert type
    Public Static final String NOT_DELIVERED = 'Not Delivered';

    //string for Term Alert Alert type
    Public Static final String TERM_ALERT = 'Term Alert';

    //string for alerts datetime format
    Public Static final String ALERTS_DATETIME_FORMAT = 'yyyyMMddHHmmss';

    //string for Alert Option
    Public Static final String Option = 'Option';

    //string for Alerts default reason selection
    Public Static final String ALERT_REASON = 'Please select reason';

    //string for Alerts Authentication type
    Public Static final String ALERTS_AUTHENTICATION = 'Alerts';

    //string for Interacting With Type
    Public Static final String MEMBERCAREGIVER = 'Member Representative or Caregiver';

    //Integer for pagination of Non-Guidance Alerts
    Public Static final Integer CURRENT_PAGE_SIZE=4;

    //String for MTMservice Requestor
    Public Static final String MTMSERV_REQUESTOR = 'MTMCCP';

    //String for MTMservice MemberIdentityQualifier
    Public Static final String MTMSERV_MEMIDENQUAL = 'MemberGenKey';

    //Error Severity Type -Warning 
    Public Static final String SEVERITY_WARNING='WARNING';

    //Error Severity Type -Confirm 
    Public Static final String SEVERITY_CONFIRM='CONFIRM';

    //Error Severity Type -Error 
    Public Static final String SEVERITY_ERROR='ERROR';

    //Error Severity Type -Fatal 
    Public Static final String SEVERITY_FATAL='FATAL';

    //Error Severity Type -Info 
    Public Static final String SEVERITY_INFO='INFO';

    // Start of List of valid of Exceptions
    Public Static final String DML_EXCEPTION ='System.DMLException';

    Public Static final String NULLPOINTER_EXCEPTION ='System.NullPointerException';

    Public Static final String QUERY_EXCEPTION ='System.QueryException';

    Public Static final String SECURITY_EXCEPTION='System.SecurityException';

    Public Static final String MATH_EXCEPTION ='System.MathException';

    Public Static final String CALLOUT_EXCEPTION ='System.CallOutException';

    Public Static final String JSON_EXCEPTION ='System.JSONException';

    Public Static final String LIST_EXCEPTION ='System.ListException';

    Public Static final String TYPE_EXCEPTION ='System.TypeException';

    Public Static final String INVALID_PARAMTER_EXCEPTION='System.InvalidParameterValueException';

    Public Static final String NO_ACCESS_EXCEPTION='System.NoAccessException';

    Public Static final String SEARCH_EXCEPTION='System.SearchException';

    Public Static final String SERIALIZATION_EXCEPTION='System.SerializationException';

    Public Static final String XML_EXCEPTION='System.XmlException';

    Public Static final String SOBJECT_EXCEPTION ='System.SObjectException';

    Public Static final String CUSTOM_EXCEPTION='HUMCustomException';


    //End of List of Valid Exceptions

    //Constant to identiy Integration Eror
    Public Static final String ERROR_INTEGRATION ='Integration Error';

    //Constant to Identity Application Error
    Public Static final String ERROR_APPLICATION ='Application Error';

    //Exception list that come under all Other Kind of exceptions
    Public Static final Set<String> setOtherExceptionTypes = New Set<String>{
                                                                DML_EXCEPTION,
                                                                NULLPOINTER_EXCEPTION,
                                                                QUERY_EXCEPTION,
                                                                SECURITY_EXCEPTION,
                                                                MATH_EXCEPTION,
                                                                CALLOUT_EXCEPTION,
                                                                JSON_EXCEPTION,
                                                                LIST_EXCEPTION,
                                                                TYPE_EXCEPTION,
                                                                INVALID_PARAMTER_EXCEPTION ,
                                                                NO_ACCESS_EXCEPTION ,
                                                                SEARCH_EXCEPTION,
                                                                SERIALIZATION_EXCEPTION,
                                                                XML_EXCEPTION,
                                                                SOBJECT_EXCEPTION,
                                                                CUSTOM_EXCEPTION

                                                               };

    //List of Valid Exceptions 
    Public Static final Set<StatusCode> setValidExceptionCodes = New Set<StatusCode>{StatusCode.FIELD_CUSTOM_VALIDATION_EXCEPTION};

    //List of Valid Error Severity types
    Public Static final Map<String, ApexPages.Severity> mapSeverityTypes = New Map<String,ApexPages.Severity> {

                                                                             SEVERITY_WARNING => ApexPages.Severity.Warning ,
                                                                             SEVERITY_CONFIRM => ApexPages.Severity.Confirm ,
                                                                             SEVERITY_ERROR => ApexPages.Severity.Error ,
                                                                             SEVERITY_INFO => ApexPages.Severity.Info ,
                                                                             SEVERITY_FATAL => ApexPages.Severity.Fatal,
                                                                             null => ApexPages.Severity.Fatal

                                                                            }; 

    //string for CaseTask sortBy Priority
    Public Static final String PRIORITY = 'Priority';

    //Integer for CaseTask to initialize integer varable with 0
    Public Static final Integer ZERO = 0;    

    //string for CaseTask selectedView to see Case And Task both
    Public Static final String BOTH = 'Both';

    //string for CaseTask selectedAssign
    Public Static final String ASSIGNED = 'Assigned';

    //string for CaseTask selectedAssign
    Public Static final String ASSIGNED_ALL = 'AssignedAll';

    //string for CaseTask selectedAssign
    Public Static final String UNASSIGNED = 'UnAssigned';

    //string for CaseTask for All
    Public Static final String ALL = 'All';

    //string for CaseTask for recordID
    Public Static final String RECORDID = 'recordID';

    //string for CaseTask for semiColon
    Public Static final String SEMICOLON = ';';

    //string for CaseTask for age
    Public Static final String AGE = 'Age';

    //string for CaseTask for Age
    Public Static final String PRIORITY_AGE = 'Priority&Age';

    //string for CaseTask for ID
    Public Static final String ID = 'ID';

    //string for account id from url
    Public Static final String URL_ID = 'id';

    //string for interaction id from url
    Public Static final String INTERACTION_ID = '&interactionId=';

    //string for CaseTask for due date
    Public Static final String DUE_DATE = 'Due Date';

    //string for CaseTask for critical
    Public Static final String CRITICAL = 'Critical';

    //string for CaseTask for high
    Public Static final String HIGH = 'High';

    //string for CaseTask for normal
    Public Static final String NORMAL = 'Normal';

    //string for CaseTask for viewOther
    Public Static final String VIEW_OTHER = 'viewOther';

    //string for CaseTask for QueueAll
    Public Static final String QUEUE_ALL = 'QueueAll';

    //string for CaseTask for QueueView
    Public Static final String QUEUE_VIEW = 'QueueView';

    //string for CaseTask for CaseID
    Public Static final String SFDC_CASE_ID = '500';

    //string for CaseTask for other
    Public Static final String OTHER = 'Other';

    //string for TaskEditPage for what_id
    Public Static final String WHAT_ID = 'what_id';

    //string for TaskEditPage for none
    Public Static final String NONE = 'None';

    //string for TaskEditPage for none
    Public Static final String USER = 'User';

    //string for TaskEditPage for none
    Public Static final String QUEUE = 'Queue';

    //string for searchfieldshelper for Encryption
    Public Static final String OBJECT_ACCOUNT_API_FIRSTNAME = 'FirstName';

    //string for searchfieldshelper for Encryption
    Public Static final String OBJECT_ACCOUNT_API_LASTNAME = 'LastName';

    //string for alerts Not delivered
    Public Static final String NOT_DELIVERED_VALUE = 'notdelivered';

    //string for alerts term alert
    Public Static final String TERM_ALERT_VALUE = 'termalert';

    //string for alerts url id
    Public Static final String ALERT_URL_ID = 'alertId';

    //string for alerts interaction value
    Public Static final String SVALUE_ONE = '1';

    //Integer for TaskListViewController
    Public Static final Integer VALUE_THREE = 3;

    //Integer for TaskListViewController
    Public Static final Integer VALUE_TWO = 2;

    //Integer for TaskListViewController
    Public Static final Integer VALUE_ONE = 1;

    //Integer for HUMCustomInteractionLogController
    Public Static final Integer VALUE_MINUS_ONE = -1;

    //Integer for HUMCustomInteractionLogController
    Public Static final Integer VALUE_FIVE = 5;

    //Integer for HUMCustomInteractionLogController
    Public Static final Integer VALUE_FIFTEEN = 15;

    //Integer for HUMCustomInteractionLogController
    Public Static final Integer VALUE_THIRTYTHREE = 33;

    //Integer for TaskListViewController
    Public Static final Integer VALUE_ZERO = 0;
    Public Static final String HUMSingleSlash= '/';
    Public Static final String HUMCaseCloseReturnURL = '/s?retURL=';
    Public Static final String HUMWorkTask = 'Work Task';

    Public Static final String Group_Case_Type = 'Group_Case';
    Public Static final String Provider_Case_Type = 'Provider_Case';
    Public Static final String Agent_Broker_Case_Type = 'Agent_Broker_Case';
    Public Static final String HUMClosed = 'Closed';
    Public Static final String HUMStatusCancelled = 'Cancelled';
    Public Static final String HUMRSOMedicare = 'RSO- Medicare';
    Public Static final String HUMGSOCommercial = 'GSO- Commercial';
    Public Static final String Member_Case_Type = 'Member_Case';
    Public Static final String HUMSpecialty = 'Specialty';
    Public Static final String HUMDental = 'Dental';
    Public Static final String HUMMedicareDAC = 'Medicare DAC';

    Public Static final String HUMClose = 'Close';
    Public Static final String HUMretURL = 'retURL';
    Public Static final String HUMRecordType = 'RecordType';
    Public Static final String HUMRecordtypeURL = '&RecordType=';
    Public Static final String HUMApexTaskEditPage = '/apex/HUMTaskEditPage';
    Public Static final String HUMId = 'Id';
    Public Static final String HUMEnt = '&ent=Task';
    Public Static final String HUMNoOverrideEqualsOne = '&nooverride=1';

    //Constant declaration for HUMClosedCaseCRMToCCPInquiries
    Public Static final String VALUE_ONEFOURTHREEFOURNINE = '14349';
    Public Static final String VALUE_ONETWOSEVENFIVEONE = '12751';
    Public Static final String VALUE_ONEONETWOONE = '1121';
    Public Static final String VALUE_THREESIXTWONINE = '3629';
    Public Static final String VALUE_ONEFIVETHREE = '153';
    Public Static final String HUMCCPCallsRSO = 'Calls (RSO)';  
    Public Static final String HUMCCPEligibilityRSO = 'Eligibility (RSO)';
    Public Static final String HUMCCPEnrollmentRSO = 'Enrollment (RSO)';
    Public Static final String HUMCCPDisenrollment = 'Disenrollment';
    Public Static final String HUMCCPTermCancelMember = 'Term/Cancel Member';
    Public Static final String HUMCCPLEPCC = 'Late Enrollment Penalty (LEP)/Creditable Coverage (CC)';
    Public Static final String HUMCCPMedicarePartD = 'Medicare Part D Redetermination Request';
    Public Static final String HUMCCPCreatedById = 'MXG0195';
    Public Static final String HUMMedicareTemplate = 'Medicare_Election_Lock_In_Template';
    Public Static final String HUMIntAbt = 'InteractingAbout';
    Public Static final String HUMIntWith = 'InteractingWith';
    Public Static final String HUMClosedStatus = 'Closed';
    Public Static final String HUMCCPInquirySubType = 'MA';
    Public Static final String HUMCCPGAInquiryType = 'GI';
    Public Static final String HUMCCPIntegrationStatus = 'NI';
    Public Static final String HUMCCPInquiryIntegrationStatus = 'IN';   
    Public Static final String HUMCCPGAMediaTypeCode = 'CR';
    Public Static final String HUMPartDProcessData1 = 'Next Steps'; 
    Public Static final String HUMPartDProcessData2 = 'Transfer the CRM Case to the following location';    
    Public Static final String HUMPartDProcessData3 = 'Service Center : Louisville';    
    Public Static final String HUMPartDProcessData4 = 'Department : Louisville G&A/Correspondence Screening';   
    Public Static final String HUMPartDProcessData5 = 'Topic : Part D Redetermination Reviews'; 
    Public Static final String HUMPartDProcessData6 = 'Overview:';  
    Public Static final String HUMPartDProcessData7 = 'For multiple requests: if it is the same drug, different dosages, you may utilize the same form (up to three requests). If it is a different drug, a different form is required';    
    Public Static final String HUMPartDProcessData8 = 'Expedite Request:';  
    Public Static final String HUMPartDProcessData9 = 'Did the member or member representative state at any time that applying the 72 hour standard review timeframe may seriously jeopardize the life or health of the enrollee or the enrollees ability to regain maximum function?:';    
    Public Static final String HUMCCPPriorityCodeWW = 'WW';
    Public Static final String HUMCasePriorityCriticial = 'Critical';
    Public Static final String HUMNewLine = '\n';
    Public Static final String HUMPartDExpediteRequest = 'ExpediteRequest'; 
    Public Static final String HUMPartDDrugInformation = 'Drug Information:';
    Public Static final String HUMPartDDrugName = 'Drug Name:'; 
    Public Static final String HUMDrugName = 'DrugName'; 
    Public Static final String HUMPartDDrugDosage = 'Drug Dosage:'; 
    Public Static final String HUMDrugDosage = 'DrugDosage';   
    Public Static final String HUMDrugDosage1 = 'DrugDosage1';    
    Public Static final String HUMDrugDosage2 = 'DrugDosage2';    
    Public Static final String HUMDrugDosage3 = 'DrugDosage3';    
    Public Static final String HUMPartDRXObtained = 'Rx Obtained:'; 
    Public Static final String HUMRXObtained = 'RxObtained';    
    Public Static final String HUMRXObtained1 = 'RxObtained1';    
    Public Static final String HUMRXObtained2 = 'RxObtained2';    
    Public Static final String HUMRXObtained3 = 'RxObtained3';    
    Public Static final String HUMPartDDateTimeOpened = 'Date/Time Opened:';    
    Public Static final String HUMDateTimeOpened = 'DateTimeOpened';    
    Public Static final String HUMDateTimeOpened1 = 'DateTimeOpened1';    
    Public Static final String HUMDateTimeOpened2 = 'DateTimeOpened2';    
    Public Static final String HUMDateTimeOpened3 = 'DateTimeOpened3';    
    Public Static final String HUMPartDDenialDate = 'Denial Date:'; 
    Public Static final String HUMDenialDate = 'DenialDate';    
    Public Static final String HUMDenialDate1 = 'DenialDate1';    
    Public Static final String HUMDenialDate2 = 'DenialDate2';    
    Public Static final String HUMDenialDate3 = 'DenialDate3';    
    Public Static final String HUMPartDDateOfFill = 'Date Of Fill:';    
    Public Static final String HUMDateOfFill = 'DateOfFill';
    Public Static final String HUMDateOfFill1 = 'DateOfFill1';
    Public Static final String HUMDateOfFill2 = 'DateOfFill2';
    Public Static final String HUMDateOfFill3 = 'DateOfFill3';
    Public Static final String HUMPartDReasonForAppeal = 'Reason For Appealing Denial:';    
    Public Static final String HUMReasonForAppeal = 'ReasonForAppeal';
    Public Static final String HUMReasonForAppeal1 = 'ReasonForAppeal1';
    Public Static final String HUMReasonForAppeal2 = 'ReasonForAppeal2';
    Public Static final String HUMReasonForAppeal3 = 'ReasonForAppeal3';
    Public Static final String HUMPartDSupportingDetails = 'Supporting Details';
    Public Static final String HUMSupportingDetails = 'SupportingDetail';
    Public Static final String HUMSupportingDetails1 = 'SupportingDetail1';
    Public Static final String HUMSupportingDetails2 = 'SupportingDetail2';
    Public Static final String HUMSupportingDetails3 = 'SupportingDetail3';
    Public Static final String HUMPartDPhysicianInformation = 'Prescribing Physician’s Information:';
    Public Static final String HUMPartDPhysicianName = 'Physician Name:';
    Public Static final String HUMPhysicianName = 'PhysicianName';
    Public Static final String HUMPartDContactPerson = 'Office Contact Person:';
    Public Static final String HUMContactPerson = 'PhysicianOfficeContactPerson';
    Public Static final String HUMPartDAddress = 'Address';
    Public Static final String HUMPhysicianAddress = 'PhysicianAddress';
    Public Static final String HUMPartDPhysicianCity = 'City:';
    Public Static final String HUMPhysicianCity = 'PhysicianCity';
    Public Static final String HUMPartDPhysicianState = 'State:';
    Public Static final String HUMPhysicianState = 'PhysicianState';
    Public Static final String HUMPartDPhysicianZipCode= 'Zip Code:';
    Public Static final String HUMPhysicianZipCode = 'PhysicianZipCode';
    Public Static final String HUMPartDTelephoneNo = 'Telephone Number:';
    Public Static final String HUMPhysicianTelephoneNo = 'PhysicianTelephoneNumber';
    Public Static final String HUMPartDExt = 'Ext:';
    Public Static final String HUMPhysicianExt = 'PhysicianExt';
    Public Static final String HUMPartDFaxNumber = 'Fax Number:';
    Public Static final String HUMPhysicianFaxNumber = 'PhysicianFaxNumber';
    Public Static final String HUMPartDAdditionalInfo = 'Additional Info:';
    Public Static final String HUMPhysicianAdditionalInfo = 'PhysicianAdditionalInfo';

    //Constant declaration for HUMEligibilityInfoController
    Public Static final String HUMEligibleYes = 'Yes';
    Public Static final String HUMEligibleNo = 'No';
    Public Static final String HUMCharY = 'Y';
    Public Static final String HUMCharN = 'N'; 
    Public Static final Integer HUMZero = 0;
    Public Static final String HUMDateFormat = 'MM/dd/YYYY';
    Public Static final Integer HUMSix = 6;
    Public Static final Integer HUMNine = 9;
    Public Static final String HUMNoValue = 'No Value';
    Public Static final String HUMSemicolon = ';';

    Public Static final String HumMeberCertIDType = 'Cert ID';
    Public Static final String HUMEESLegacyMemberSearchHelper_SSN = 'SSN';
    Public Static final String HUMSourcePersonID = 'SourcePersonID';
    Public Static final String HUMCbisPlatform = 'CB';

    Public Static final String HUMObjectType = 'objectType';
    Public Static final String HUMobjectNumber = 'objectNumber';
    Public Static final String HUMActionType = 'Error';
    Public Static final String HUMQueueYesForEdit = 'YesForEdit';
    Public Static final String HUMTask_Number = 'Task_Number';
    Public Static final String HUMObjectId = 'objectId';
    Public Static final String HUMQueueYesForInteraction = 'YesForInteraction';
    Public Static final String HUMQueueYesForAttachment = 'YesForAttachement';
    Public Static final String HUMNextPage = 'nextPage';
    Public Static final String HUMParentObjectId = 'ParentObjectId';
    Public Static final String HUMCaseStandardURL = 'sStandardUrlForTab';
    Public Static final String HUMQueueYesFromAccountRelatedList = 'YesFromAccountRelatedList';
    Public Static final String HUMcaseRecordType = 'caseRecordType';
    Public Static final String HUMAccRecordTypeName = 'Account RecordType';
    Public Static final String HUMQueueYesFromCaseHistory = 'YesFromCaseHistory';
    Public Static final String HUMApexinURL = '/apex/';
    Public Static final String HUMWhatId = 'whatId';
    Public Static final String HUMDotCom = '.com/';
    Public Static final String HUMCaseURLInteraction = '&interactionId=';
    Public Static final String HUMsmallId = 'id';

    Public Static final String HUMCCSpecialistProfile = 'Customer Care Specialist';
    Public Static final String HUMCCSupervisorProfile = 'Customer Care Supervisor';
    Public Static final String HUMSelectTopic = 'Select a Topic';

    Public Static final String HUMAgentBrokerCase = 'Agent/Broker Case';
    Public Static final String HUMGroupCase = 'Group Case';
    Public Static final String HUMUnknownCase = 'Unknown Case';
    Public Static final String HUMProviderCase = 'Provider Case';
    Public Static final String HUMMemberCase = 'Member Case';
    Public Static final String HUMHPAgentBrokerCase = 'HP Agent/Broker Case';
    Public Static final String HUMHPGroupCase = 'HP Group Case';
    Public Static final String HUMHPUnknownCase = 'HP Unknown Case';
    Public Static final String HUMHPProviderCase = 'HP Provider Case';
    Public Static final String HUMHPMemberCase = 'HP Member Case';
    Public Static final String HUMHPSpecialistProfile = 'Humana Pharmacy Specialist';
    Public Static final String HUMUser = 'User';
    Public Static final String HUMChangeCaseOwner = 'HUMChangeCaseOwner';
    Public Static final String HUMQAAProcess = 'QAA';
    Public Static final String HUMUserForCaseChangeOwner = 'User:';
    Public Static final String HUMChangeOwnerDate = '; Date:';
    Public Static final String HUMChangeQMSReferral = '; QMS Referral #';
    Public Static final String HUMQMSReferralSuccess = 'Referral Updated Successfully in QMS';

    Public Static final String HUMCOwnerResponseBoday1 = 'Attachment Name : Quality Referral Details; Attachment Id : Quality Referral Details; Referral Id:';
    Public Static final String HUMCOwnerResponseBoday2 = '; Is Inquiry Updated in Quality: true; Referral Message:';
    Public Static final String HUMQMSProviderTaxId = 'Provider Tax ID';

    Public Static final String HUMQMSProviderName = 'Provider\'s Name';
    Public Static final String HUMQMSDateIncidence = 'Date of the incident/service';
    Public Static final String HUMQMSComplaintDetails = 'Complaint Details';
    Public Static final String HUMBeforeDelete = 'BeforeDelete';

    Public Static final String HUMREGULAR = 'regular';
    Public Static final String HUMInProgress = 'In Progress';
    Public Static final String HUMPending = 'Pending';
    Public Static final String HUMPendingTask = 'Pending - Task';
    Public Static final String HUMPendingResponse = 'Pending - Response';
    Public Static final String HUMAllOtherView = 'Other';
    Public Static final String PriorityAndAge = 'Priority&Age';
    Public Static final String HUM_OF = '(of';

    //Constant declaration for HUMClosedCaseCRMToCCPInquiries   
    Public Static final String ZEROValue = '0';
    Public Static final String CCPInqRId = '0000000000';
    Public Static final String HUMConstantRSOEligibility = 'Eligibility (RSO)';
    Public Static final String HUMConstantMemTermCancel = 'Term/Cancel Member';
    Public Static final String HUMConstantOneOneTwoOne = '1121';
    Public Static final String HUMConstantLEPCC = 'Late Enrollment Penalty (LEP)/Creditable Coverage (CC)';
    Public Static final String HUMConstantThreeSixTwoNine = '3629';
    Public Static final String HUMConstantRSOEnrollment = 'Enrollment (RSO)';
    Public Static final String HUMConstantRSODisEnrollment = 'Disenrollment';
    Public Static final String HUMConstantOneFiftyThree = '153';
    Public Static final String HUMConstantcreatedByID = 'MXG0195';
    Public Static final String UnknownAgentBroker = 'Unknown Agent/Broker';
    Public Static final String HUMUnknownAgentBroker = 'UA';
    Public Static final String UnknownGroup = 'Unknown Group';
    Public Static final String HUMUnknownGroup = 'UE';
    Public Static final String UnknownProvider = 'Unknown Provider';
    Public Static final String HUMUnKnownProvider = 'UP';
    Public Static final String UnknownMember = 'Unknown Member';
    Public Static final String HUMUnKnownMember = 'UM';
    Public Static final String HUMAttachmentLogForDisenrollment = 'Medicare_Election_Lock_In_Template';
    Public Static final String HUMCaseInteractingAbout = 'InteractingAbout';
    Public Static final String HUMCaseInteractingWith = 'InteractingWith';
    Public Static final String HUMKnownMember = 'KM';
    Public Static final String HUMKnownGroup = 'KE';
    Public Static final String HUMKnownProvider = 'KP';
    Public Static final String HUMKnownAgentBroker = 'KA';
    Public Static final String HUMCCPGCPInquiryId = 'CCP/GCP Inquiry Id';
    Public Static final String HUMCCPGCPCaseComment = 'This is a system generated message. Error with creating GCP inquiry, please contact help desk.';
    Public Static final String HUMConstantUserSiteName = 'Green Bay';
    Public Static final String HUMCCPMediaTypeCode = 'OT';
    Public Static final String HUMCCPIndicator = 'O';
    Public Static final String HUMCCPStatusCode = 'OP';
    Public Static final String HUMCCPPriorityCode = 'LL';
    Public Static final String HUMConstantIntegrationStatus = 'IN';
    Public Static final String HUMCCPInquiryType = 'CI';
    Public Static final String HUMCCPInquirySubTypeExp = 'EA';
    //Search related constants from "HUMSearchFieldsHelper" class
    Public Static final String HUMAlphaNumericPattern = '^[a-zA-Z0-9-.\' , &]*$';
    Public Static final String HumSearchGroupNumberAlphaNumericValidation = 'Please enter only alpha-numeric values for Group Number field';
    Public Static final String HumSearchMemberIdAlphaNumericValidation = 'Please enter only alpha-numeric values for Id field';
    Public Static final Integer HUMMemberIdCharatcerLimit = 3;

    Public Static final String HumSearchMemberIdCharacterValidation = 'Member Id must be between 3 and 17 Characters';
    Public Static final Integer HUMSuffixCharacterLimit = 2;
    Public Static final String HUMSearchSuffixCharacterValidation = 'Member ID suffix must be 2-3 characters.';
    Public Static final String HumSearchSuffixNumericValidation = 'Member ID suffix must be numeric.';

    Public Static final String HumSearchFirstNameAlphaNumericValidation = 'The only special characters that can be used are an apostrophe, a period, a dash, a comma or an ampersand in the First Name field.';
    Public Static final String HUMLastNameAlphaNumericPattern = '^[a-zA-Z0-9-.\' ,]*$';
    Public Static final String HUMSELastNameAlphaNumericPattern = '^[a-zA-Z0-9-.\' , &]*$';
    Public Static final String HumSESearchLastNameAlphaNumericValidation = 'The only special characters that can be used are an apostrophe, a period, a dash, a comma or an ampersand in the Last Name field.';
    Public Static final String HumSearchLastNameAlphaNumericValidation = 'The only special characters that can be used are an apostrophe, a period, a dash or a comma in the Last Name field.';
    Public Static final Integer HUMFirstNameCharacterLimit = 2;
    Public Static final String HumSearchLastNameCharacterValidation = 'Last Name must be at least 2 characters';


    Public Static final String HumMemberSearchBirthDateValidation = 'Please Enter Valid Date in the Format MM/DD/YYYY in Birth Date field';
    Public Static final String HUMLeapYearFebPattern = '^(0[2])[- / .](0[1-9]|1[0-9]|2[0-9])[- / .](1[9][0-9][0-9]|2[0][0-9][0-9])$';
    Public Static final String HUM30DaysPattern = '^(1[1]|0[4]|0[6]|0[9])[- / .](0[1-9]|1[0-9]|2[0-9]|3[0])[- / .](1[9][0-9][0-9]|2[0][0-9][0-9])$';
    Public Static final String HUM31DaysPattern = '^(1[0]|1[2]|0[1]|0[3]|0[5]|0[7]|0[8])[- / .](0[1-9]|1[0-9]|2[0-9]|3[0-1])[- / .](1[9][0-9][0-9]|2[0][0-9][0-9])$';
    Public Static final String HUMNonLeapYearFebPattern = '^(0[2])[- / .](0[1-9]|1[0-9]|2[0-8])[- / .](1[9][0-9][0-9]|2[0][0-9][0-9])$';

    Public Static final String HUMBirthDatePattern = '^(0[1-9]|1[012])[- /.](0[1-9]|[12][0-9]|3[01])[- /.](19|20)\\d\\d$';
    Public Static final String HUMSearchBirthDateValidation = 'Birth Date cannot be a future date';
    Public Static final String HUMUnknownZipcodePattern = '^[0-9]{5,5}+$';
    Public Static final String HumZipcodeCharacterLimit = 'Zip code must be 5 digits numeric value';
    Public Static final String HUMNumericPattern = '^[0-9]{5,5}+$';

    Public Static final Integer HUMPhoneNumberCharacterLimit = 10;
    Public Static final String HumPhoneCharacterLimit = 'Phone numbers must be 10 digits';
    Public Static final String HumPhoneValidationForMember = 'Please enter only Numbers for phone field.';
    Public Static final String HUMAgencySearchAgentIDValidation = 'Agent Id should be a number';

    Public Static final String HumSearchAgencyNameValidation = 'The only special characters that can be used are an apostrophe, a period, a dash, a comma or an ampersand in the Business Name/First Name field.';
    Public Static final String HUMStateDefaultValue = 'None';
    Public Static final String HUM_Agency_Search_Fields_Not_Entered = 'Please enter value for at least one of the Search fields for Agency/Broker';
    Public Static final String HUM_Group_Search_Fields_Not_Entered = 'Please enter value for at least one of the Search fields for Group';

    Public Static final String HUMGroupSearchInvalidCombination = 'Please search using Group Number OR Group Name + State';
    Public Static final String HUM_Provider_Search_Fields_Not_Entered = 'Please enter value for at least one of the Search fields for Provider';
    Public Static final Integer HUMProviderSearchTaxIDValue = 9;
    Public Static final String HumProviderSearchTaxIDLength = 'Tax ID must be 9 Characters';
    Public Static final Integer HUMProviderSearchNPIValue = 10;

    Public Static final String HumProviderSearchNPILength = 'NPI must be 10 Characters';
    Public Static final String HumSearchTaxIDAlphaNumericValidation = 'Please enter Alpha-Numeric characters for Tax ID field';
    Public Static final String HumSearchNPIAlphaNumericValidation = 'Please enter Alpha-Numeric characters for NPI field';
    Public Static final String HUMFacilityNamePattern = '^[a-zA-Z0-9_ ]*$';
    Public Static final String HumSearchFacilityNameAlphaNumericValidation = 'Please enter Alpha-Numeric characters for Facility Name field';

    Public Static final String HUMSearchEnrollmentFirstName = 'Please Enter First Name';
    Public Static final String HUMSearchEnrollmentLastName = 'Please enter Last Name';
    Public Static final String HUMSearchEnrollmentBirthDate = 'A Date of Birth is Required to search for CBIS members.';
    Public Static final String HUMNone = 'None';
    Public Static final String HUMNoneDisplay = '---None--';
    Public Static final String HUMSearchEnrollmentState = 'A State is required to search for CBIS members.';
    Public Static final String HUM_Object_Account = 'Account';
    Public Static final String HUM_Object_Policy = 'Policy__C';    
	
    Public static final String HUMSearchTypeAccount = 'Account';
    Public Static final String HUMSearchTypeMember = 'Member';
    Public Static final String HUMSearchTypeGroup = 'Group';
    Public Static final String HUMAccountFirstName = 'FirstName';
    Public Static final String HUMAccountLastName = 'LastName';
    Public Static final String HUMSearchTypeProvider = 'Provider';
    Public Static final String HUMSearchTypeAgency = 'Agency';
    Public Static final String HumSearchMailingstatecodeValidation = 'Invalid Mailing State Code.';

    Public Static final String HUMAccountIndividualFirstName = 'Individual_First_Name__c';
    Public Static final String HUMAccountIndividualLastName = 'Individual_Last_Name__c';
    Public Static final String HUMAccountNPIID = 'NPI_ID__c';
    Public Static final String HUMAccountBillingpostalcode = 'BillingPostalCode';
    Public Static final String HUMAccountName = 'Name';

    Public Static final String HUMAccountBirthDate = 'Birthdate__c';
    Public Static final String HUMAccountPhone = 'PersonHomePhone';
    Public Static final String HUMAccountPostalCode = 'PersonMailingPostalCode';
    Public Static final String HUMGroupNumber = 'Group_Number__c';
    Public Static final String HUMMemberSearchConstant = '0';
    Public Static final String HUMObjectMemberID = 'member_id__c';

    Public Static final String HUMObjectConsumerID = 'ConsumerID__c';
    Public Static final String HUMConsumerTaxID = 'Consumer_ID__c';
    Public Static final String HUMAgentSearchInvalidCombination = 'Please search using \n  Agent ID + Agent Type \n OR \n Tax ID + Agent Type \n OR \n Agent Type + Business Name (OR First Name + Last Name) + State';
    Public Static final String HUMUSAPhonePattern = '^(\\+\\d{1,2}\\s)?\\(?\\d{3}\\)?[\\s.-]\\d{3}[\\s.-]\\d{4}$';

     //Search related constants from "HUMSearchComponentBaseHelper" class
    Public Static final String HUMAgentBrokerRecordTypeName = 'Agent/Broker';
    Public Static final String HUMValidAccountEmailRegExp = '^[_A-Za-z0-9%+-]+(\\.[_A-Za-z0-9+%]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,3})$';

    //Search related constants from "HUMGroupSearchControllerV2" class
    Public Static final Integer ListSizePageMemSearch = 10;
    Public Static final String HUMSearch_returned_more_than_100_entries = 'Your search returned more than 100 entries. Please refine your search.';
    Public Static final String HUM_Group_Account_Record_Type = 'Group';
    Public Static final String HUM_UnknownGroup_Account_Record_Type = 'Unknown_Group';

    // Search related constants from "HUMProviderSearchControllerV2" class
    Public Static final String HumSearchEPSSServiceApplicationKey = '28AD1338-9A7D-41DB-BB87-F9FDFEE3DA01';
    Public Static final String HUMProviderFilterWarning = 'Results cannot be displayed due to overly common criteria: Please expand your search criteria with additional information.';
    Public Static final String HUMProviderWarning = 'Results cannot be displayed due to overly common criteria: Please expand your search criteria with additional information.';
    Public Static final String Hum_Provider_results = 'No Provider information was found in the system with the matching criteria.  Please search again using one of the following: Tax ID, NPI, Facility/Provider Group, First Name + Last Name, State + Secondary Criteria, Zip Code + Secondary Criteria, Specialty + Secondary Criteria"';

    Public Static final String HumServiceTaxIDtag = 'TaxID';
    Public Static final String HumProviderRecordType = 'Provider';
    Public Static final String HumUnknownProviderRecordType = 'Unknown_Provider';
    Public Static final String HumProviderNameIndividual = 'I';

    Public Static final String HumProviderFacilityRecord = 'F';
    Public Static final String HumproviderGroupsfdc = 'G';
    Public Static final String HumProviderPaginationSize = '10';
    Public Static final String HUMExtMemAccountTenantID = '00';
    Public Static final String HumServiceFacilitytag = 'Facility';
    Public Static final Integer HUMServiceTimeOut = 120;

    Public Static final String Hum_Uniqueue_ProviderSearchError = '"Please search using one of the following: Tax ID or NPI or  Facility/Provider Group or First Name + Last Name or  State + Secondary Criteria or  Zip Code + Secondary Criteria or Specialty + Secondary Criteria"';
    Public Static final String Hum_State_Name = '"Please search using State + any of the following secondary criteria: First Name + Last Name, Facility/Provider Group';
    Public Static final String Hum_Zipcode_Name = 'Please search using Zip Code + any of the following secondary criteria: First Name + Last Name,  Facility/Provider Group';
    Public Static final String Hum_Specialty_Name = 'Please search using Specialty + First Name + Last Name';
    Public Static final String Hum_Last_Name = 'Please search using First Name + Last Name + any other Secondary Criteria (optional)';
    Public Static final String Hum_First_Name = 'Please search using First Name + Last Name + any other Secondary Criteria (optional)';
    Public Static final String HUMGroupSearchNoResultFound = 'No Group information was found in the system with the matching criteria.  Please search again using \n Group Number \n OR \n Group Name + State';

    //Constants used in HUMMemberSearchEnrollmentControllerV2(HUMMemberSearchEnrollmentController - OldVersion)
    Public Static final String HUMCBISCode = 'CBIS';
    Public Static final String HUMTRRCode = 'Transaction Reply Report (TRR)';
    Public Static final String HUMSearchEnrollmentSelectServices = 'Please select one of the options from Services';
    Public Static final String HUMEESService = 'Customer Interface/Metavance';
    Public Static final String HUMEESLegacyMemberSearchHelper_Coverages_PlatformMemberCoverage = 'Coverages:PlatformMemberCoverage';
    Public Static final String HUMH1Service = 'H1 Marketplace Search';
    Public Static final String HUMTrackEnrollmentService = 'Track Enrollment Status'; //Value not present in Mercury and Test- IMP - Label not present???
    Public Static final String HUMAEService = 'Automated Enrollment (AE) History';
    Public Static final String HUMSearchEnrollmentDate = 'Start Date OR End Date field should not be left Blank';
    Public Static final String HumStartnEndDate = 'Please Enter Valid Date in the Format MM/DD/YYYY in Date field'; //Value not present in mercury and TEST - - IMP - Label not present???
    Public Static final String HumSearchEnrollmentDateValid1 = 'Start Date Should always happen before End Date';
    Public Static final String HUMSearchEnrollmentSsnservice = 'Please select one of the options from Services<br/>Please enter the SSN Number';
    Public Static final String HumSearchenrollmentSsnReq = 'Please enter the SSN Number';
    Public Static final String HumSearchEnrollmentNoRes = 'No Results Found';
    Public Static final String HUMSearchEnrollmentohbpSearch = 'ohbpSearch';
    Public Static final String HUMOHBPSvc = 'OHBPService';
    Public Static final String HUMOHBPReq = 'HUMOHBPService.soap';
    Public Static final String HUMOHBPMemberSearchReq_Header = '<Member><Header><ClientMetaData><DataItem Name=\'ClientName\'>GCP-OHBP</DataItem><DataItem Name=\'ApplicationFunction\'>CCP.MemberSearch</DataItem><DataItem Name=\'ApplicationUser\'>GCP</DataItem><DataItem Name=\'ApplicationRequester\'>Requester</DataItem></ClientMetaData><ProxyInfo><ProxyItem Name=\'ServerName\'>ivr.medical.family</ProxyItem> <ProxyItem Name=\'PathInfo\'>IVR/Medical</ProxyItem><ProxyItem Name=\'Version\'>0.0</ProxyItem> </ProxyInfo> </Header><List Family=\'true\' Coverages=\'true\'><Inputs><SSN>';
    Public Static final String HUMOHBPMemberSearchReq_End = '</SSN></Inputs></List></Member>';
    Public Static final String HUMOHBPMember = 'Member';
    Public Static final String HUMOHBPSearchFirstName = 'FirstName';
    Public Static final String HUMOHBPSearchFstName = 'firstName';
    Public Static final String HUMOHBPSearchLastName = 'LastName';
    Public Static final String HUMOHBPSearchLstName = 'lastName';
    Public Static final String HUMOHBPSearchBirthDate = 'DateOfBirth';
    Public Static final String HUMOHBPSearchBirthDt = 'birthDate';
    Public Static final String HUMOHBPSearchPlanID = 'PlanID';
    Public Static final String HUMOHBPSearchIDPlan = 'planID';
    Public Static final String HUMOHBPSearchTranID = 'TransactionID';
    Public Static final String HUMOHBPSearchExtID = 'externalID';      
    Public Static final String HUMOHBPSearchPlanName = 'PlanName';
    Public Static final String HUMOHBPSearchProd = 'product';
    Public Static final String HUMOHBPSearchEffDate = 'ProposedEffectiveDateEM';
    Public Static final String HUMOHBPSearchEfectDate = 'effectiveDate';
    Public Static final String HUMH1ExchRequestType = 'E';
    Public Static final String HUMAEHistSvc = 'AEHistoryService';
    Public Static final String HUMAEHisReq = 'HUMIntAEHistoryService.soap';
    Public Static final String HUMAEHistReqHeader = 'CCPAEREQST';
    Public Static final String HUMSearchEnrollmentCBISSearch = 'CBISSearch';
    Public Static final String HUMCBISRestrictedError = 'This member cannot be accessed in CRM Service. Please utilize source system to access information.';
    Public Static final String HUMSearchEnrollmentTrackEnrollment = 'TrackEnrollmentSearch';
    Public Static final String HUMSearchEnrollmentAEHistory = 'HUMSearchEnrollmentAEHistory';
    Public Static final String HUMSearchEnrollmentbuildUnknownAccountMap = 'buildUnknownAccountMap';
    Public Static final String HUMSearchEnrollmentH1Exchange = 'H1ExchangeSearch';


    //Constants used in HUMMemberSearchControllerV2(HUMMemberSearchController - Old Version)
    Public Static final String HUMMemberSearchCriteriaViolation = 'No Member information was found in the system with the matching criteria.\nPlease search again using one of the following: ID,<br/>Group Number, Phone, First Name + Secondary Criteria, Last Name + Secondary Criteria, Birth Date + Secondary Criteria, State + Secondary Criteria, Zip Code + Secondary Criteria';
    Public Static final Integer HUMMemberSearchMaxLimit = 100;
    Public Static final Integer HUMConstantZero = 0;
    Public Static final String HumMemberRecordType = 'Member';
    Public Static final String HumGCMUnknownMemberRecordType = 'CRMRetail Visitor';
    Public Static final String HUM_UnknownMember_Account_Record_Type = 'Unknown_Member'; //Value not present in Mercury and Test- IMP - Label not present???
    Public Static final String HumMemberSearchQueryOrderBy = 'ORDER BY lastName';
    Public Static final String HumMemberIdSearchQueryOrderBy = 'ORDER BY Policy_Member__r.Member__r.lastName';    
    Public Static final String HumSearchSortExpressionASC = 'ASC';
    Public Static final Integer MembersearchConstantZero = 0;
    Public Static final String HUM_Member_Search_Fields_Not_Entered = 'Please enter value for at least one of the Search fields for Member'; //Value not present in Mercury and Test- IMP - Label not present???
    Public Static final String HumErrorBirthDateSearch = 'Please search using Birth Date + any of the following secondary criteria: ID, Group Number, Phone, First Name + Last Name';
    Public Static final String HumErrorBirthDateSearchMedicare = 'Please search using Birth Date + any of the following secondary criteria: ID, First Name + Last Name';
    Public Static final String HumErrorBirthDateSearchCombination = 'Please Enter First Name and Last Name with Birth Date field';
    Public Static final String HumErrorZipCodeSearch = 'Please search using Zip Code + any of the following secondary criteria: ID, Group Number, Phone, First Name + Last Name';
    Public Static final String HumErrorZipCodeSearchMedicare = 'Please search using Zip Code + any of the following secondary criteria: ID, First Name + Last Name';
    Public Static final String HumErrorPostalCodeSearchCombination = 'Please enter First Name and Last Name with Zip Code field';
    Public Static final String HumErrorStateSearch = 'Please search using State + any of the following secondary criteria: ID, Group Number, Phone, First Name + Last Name';
    Public Static final String HumErrorStateSearchMedicare = 'Please search using State + any of the following secondary criteria: ID, First Name + Last Name';
    Public Static final String HumErrorStateSearchCombination = 'Please Enter First Name and Last Name with State field';
    Public Static final String HumErrorFirstNameSearch = 'Please search using First Name + any of the following secondary criteria: Last Name, ID, Group Number, Phone, Birth Date, State, or Zip Code';
    Public Static final String HumErrorFirstNameSearchMedicare = 'Please search using First Name + any of the following secondary criteria: Last Name, ID, Birth Date, State, or Zip Code';
    Public Static final String HumErrorLastNameSearch = 'Please search using Last Name + any of the following secondary criteria: First Name, ID, Group Number, Phone, Birth Date, State, or Zip Code';
    Public Static final String HumErrorLastNameSearchMedicare = 'Please search using Last Name + any of the following secondary criteria: First Name, ID, Birth Date, State, or Zip Code';
    Public Static final String HumSearchSuffixMemberIdValidation = 'ID field is required when a Suffix is present.';
    Public Static final String HumSearchSortExpressionDESC = 'DESC';
    Public Static final Integer Member_SearchLimit = 101;
    Public Static final Integer Unknown_SearchLimit = 1;

    //Constants used in HUMAgencySearchControllerV2(HUMAgencySearchController - Old Version)
    Public Static final Integer HUMAgencyIntialization = 0;
    Public Static final String HUMAgentSearchNoResultFound = 'No Agency/Broker information was found in the system with the matching criteria.<br>Please search again using Agent ID + Agent Type<br>OR<br>Tax ID + Agent Type<br>OR<br>Agent Type + Business Name (OR First Name + Last Name) + State';
    Public Static final Integer HUMAgencyObjectSize = 100;
    Public Static final String HUM_Agency_Account_Record_Type = 'Agent_Broker';//Value not present in Mercury and Test- IMP - Label not present???
    Public Static final String HUM_UnknownAgency_Account_Record_Type = 'Unknown_Agent_Broker';//Value not present in Mercury and Test- IMP - Label not present???
    Public Static final String HUMAgencySearchAgentTypeValue1 = 'All';    
    Public Static final String HUMAgencySearchAgentTypeValue2 = 'Agency';
    Public Static final String HUMAgencySearchAgentTypeValue3 = 'Broker';
    Public Static final Integer HUMMaxDateYear = 4000;
    Public Static final Integer HUMMinDateYear = 1700;
    Public Static final String EXT_ID_AGENT = 'AGN|00|SOLAR|';
    Public Static final String TENANTID_GEN = '00';


    //Constants used in HUMSearchControllerV2(HUMSearchController - Old Version)
    Public Static final String SEARCH_MEMBER = 'Member';
    Public Static final String SEARCH_PROVIDER = 'Provider';
    Public Static final String SEARCH_GROUP = 'Group';
    Public Static final String SEARCH_AGENCY = 'Agency';
    Public Static final String SEARCH_AGENT = 'Agent';
    Public Static final String SEARCH_ENROLLMENT = 'Enrollment';

    Public Static final String TAB_NAME_MEMBER = 'MemberSearch';
    Public Static final String TAB_NAME_PROVIDER = 'ProviderSearch';
    Public Static final String TAB_NAME_GROUP = 'GroupSearch';
    Public Static final String TAB_NAME_AGENCY = 'AgencyBrokerSearch';

    Public Static final String TAB_NAME_ENROLLMENT = 'SearchEnrollment';

    Public Static final String INTABTFNAME = 'iAboutFirstName';
    Public Static final String INTABTLNAME = 'iAboutLastName';
    Public Static final String INTABTID = 'iAboutID';
    Public Static final String INTABTDOB = 'iAboutDOB';
    Public Static final String INTABOUTTYPE = 'iAboutType';
    Public Static final String INTWITHFNAME = 'iWithFirstName';
    Public Static final String INTWITHLNAME = 'iWithLastName';
    Public Static final String INTWITHID = 'iWithID';
    Public Static final String INTWITHDOB = 'iWithDOB';
    Public Static final String INTWITHTYPE = 'iWithType';
    Public Static final String FIRSTNAME = 'firstName';
    Public Static final String LASTNAME = 'lastName';
    Public Static final String CALLTYPE = 'callerType';
    Public Static final String UNIQUEID = 'uuID';
    Public Static final String INTABTIDSUFFIX = 'sIntAboutSuffixId';
    Public Static final String INTWITHIDSUFFIX = 'sIntWithSuffixId';
    Public Static final String INTERACTIONID = 'interactionId'; 
    Public Static final String SURVEYID = 'surveyId'; 
    Public Static final String CALL_CATEGORY_DESC = 'callCategoryDesc';

	Public Static final String IABTNPIID ='sIntAboutNPIId';
    Public Static final String IWITHNPID ='sIntWithNPIId';
	
    Public Static final String AMP = '&';
    Public Static final String EQUAL = '=';
    Public Static final String UNDEFINED = 'undefined';

    Public Static final String CONSUMERID_FIELD = 'consumer_id__c';

    Public Static final String sIndividualprovider ='I';
    Public Static final String sFacilityprovider ='F';
    Public Static final String sGroupprovider ='G';

    Public Static final String sMemSearchContCallback = 'processResponseSFDC';
    Public Static final String sProviderSearchContCallback = 'processResponse';
    Public Static final String sProviderSearchContSFDCCallback = 'processResponseSFDC';
    Public Static final String sLastName = 'sLastName';
    Public Static final String sTaxID = 'sTaxID';
    Public Static final String sNPI = 'sNPI';
    Public Static final String sFirstName = 'sFirstName';
    Public Static final String sFacilityName = 'sFacilityName';
    Public Static final String sPostalCode = 'sPostalCode';
    Public Static final String sSpeciality = 'sSpeciality';
    Public Static final String sHiphan = '-';

    Public Static final String sQuestionMark = '?';
    Public Static final Integer HUMOne = 1;
    Public Static final Integer INTACCOUNTIDENDINDEX = 20;
    Public Static final String HUMObjID = 'objId';

    Public Static final String HUMChatIdParameter = '&chatId';
    Public Static final String HUMWebChatText = 'Web Chat';
    Public Static final String HUMQueueSelectionPage = 'HUMQueueSelection';

    Public Static final String sOtherView = 'Other';
    Public Static final String sStatusClosed = 'Closed';
    Public Static final String sStatusCompleted = 'Completed';
    Public Static final String sStatusCancelled = 'Cancelled';
    Public Static final String sFilterEquals = 'Equals';
    Public Static final String sFilterNotEquals = 'Not Equals';

    Public Static final String sAVFErrorCode = '00000';
    Public Static final String sAVFPluginClass = 'HUMAVFTemplateService_Plugin'; 
    Public Static final String sAVFMethod = 'invoke'; 
    Public Static final String sAVFType = 'AVF Service Error';   

    Public Static final String sPageKeyForcompatibility = 'X-UA-Compatible'; 
    Public Static final String sPageValueForcompatibility = 'IE=8'; 
    //public static final String HUMAttachmentLogForDisenrollment = 'Medicare_Election_Lock_In_Template'; 
    Public Static final String HUMDisEnrollmentURL1 = '&contactId=0&inquiryId=0&taskId=0&userId=0&teamId=0'; 
    Public Static final String HUMDisEnrollmentURL2 = '&templateType=Medicare_Election_Lock_In_Template&templateSubtype=_'; 
    Public Static final String HUMDisEnrollmentURL3 = '&GCPTemplateId=EL&language=en_US&UMID='; 
    Public Static final String HUMCaseIDParameter = '?CaseId='; 
    Public Static final String HUMPolicyDetailsParameter = '&policyDetails='; 
    Public Static final String HUMDisenrollmentPlanInfo = 'No Value'; 

    Public Static final String CASE_ACTION = 'New';

    Public Static final Integer HumListsizeLimit = 101;

    //Constants used in HUMServiceTesterController
    Public Static final String Get = 'GET';
    Public Static final String PUT = 'PUT';
    Public Static final String POST = 'POST';
    Public Static final String DEL = 'DELETE';
    Public Static final String Application_json = 'application/json';
    Public Static final String Application_xml = 'application/xml';
    Public Static final String Text_xml = 'text/xml';
    Public Static final String Application_x_www_form_urlencoded = 'Application/x-www-form-urlencoded';
    Public Static final String text_plain = 'text/plain';

    Public Static final Integer HUMConstantZerovalue = 0;
    Public Static final String HUMCOBProcessType = 'ProcessType';
    Public Static final String HUMTabId = 'tabId';
    Public Static final String HUMLISTSIZEZERO= 'HUMLISTSIZEZERO';
    Public Static final String HUMCOBView = 'View';
    Public Static final String HUMCOBIncorrectCaseAction = 'Case Action info already exists for this case and is not related to COB. Please navigate to proper page to view data.';
    Public Static final String HUMCOBEditLabel = 'This Process type is not allowed to be edited. If changes are needed, please create a new Case';
    Public Static final String HUMCOBEdit = 'Edit';
    Public Static final String HUMCOBAddOI = 'Add OI';
    Public Static final String HUMCOBRemoveOI = 'Remove OI';
    Public Static final String HUMCOBTableName = 'COB Table';
    Public Static final String HUMCOBTemplateLabel = 'COB';
    Public Static final String HUMCOBMemberRelationshipLabel = 'MemberRelationship';
    Public Static final String OIPolicySubsFName = 'Other Insurance Policy subscriber First name';
    Public Static final String OIPolicySubsLName = 'Other Insurance Policy subscriber Last name';
    Public Static final String HUMCOBMemberLabel = 'Member';
    Public Static final String HUMCOBRelationshipLabel = 'Relationship';
    Public Static final String HUMCOBOICarrierName = 'OI Carrier Name';
    Public Static final String HUMCOBOIEffectiveDate = 'OI Effective Date';
    Public Static final String HUMCOBOIEndDate = 'OI End Date';
    Public Static final String HUMCOBOIMedicalVerifiedDate = 'OI Medical Last Verified Date';
    Public Static final String HUMCOBOIDentalVerifiedDate = 'OI Dental Last Verified Date';
    Public Static final String HUMCOBOIPrimary = 'OI Primary';
    Public Static final String HUMCOBDropDownType = 'Drop down';
    Public Static final String HUMCOBOtherInsSubscriberName = 'Other Insurance Policy subscriber name';
    Public Static final String HUMCOBNoneSelectOption = 'Select a Value';
    Public Static final String MemberFirstName = 'Member First Name';
    Public Static final String MemberLastName = 'Member Last Name';
    Public Static final String HUMCOBOIShouldNeverListed = 'OI Should Never Have Been Listed';
    Public Static final String HUMCOBOtherCarrier = 'OTHER Carrier';
    Public Static final String HUMCOBVerified = 'Verified';
    Public Static final String HUMCOBMissingResponses = '<b>Please provide responses for below OI Questions:</b><br/>';
    Public Static final String HUMCOBCaseUnavailableErrorMsg = 'Unable to Save. No Case Record found. Please contact Administrator.';
    Public Static final String HUMCOBCheckboxType = 'Checkbox';
    Public Static final String HUMCOBTextType = 'Text';
    Public Static final String HUMCOBDateQuestionType = 'Date';
    Public Static final String HUMAddOI = 'Add OI Row-';
    Public Static final String HUMCOBOtherCarrierName = 'Other Carrier Name';
    Public Static final String HUMRemoveOI = 'Remove OI Row-';
    Public Static final String HUMCOBSubscriberDOB = 'Other Insurance subscriber DOB';
    Public Static final String HUMActionDateFormat = 'Enter Date either in MMDDYYYY format or MM/DD/YYYY format.';
    Public Static final String HUMCOBTextAreaType = 'Text Area';
    Public Static final String HUMBackSlash = '<br/>';
    Public Static final Integer HumLimit = 3500;
    Public Static final Integer HUMCaseTaskPageSize = 25;
    Public Static final Integer HUMWorkQueueSetupLimit = 5000;

     //Constants Added for the RSO Defect fix 188140.0001
    Public Static final String HUMCase = 'Case';
    Public Static final String HUMExistingProcess = 'Old/Existing Process';
    Public Static final String HUMNewProcess = 'New Process';
    Public Static final Integer HUMLastIndex = 15;
    Public Static final Integer HUMConstantOne = 1;
    Public Static final String STATUS = 'status';

    Public Static final Integer HumQueueLimit = 1000;
    Public Static final String HUM_QUEUE = 'Queue';
    Public Static final Integer CaseTaskQuery_Limit = 3500;
    Public Static final String SFDC_USERID_HUM = '5';
    Public Static final String DueDate_HUM = 'DueDate';

    Public Static final String SORTDATE_Hum ='CreatedDate';
    Public Static final String CONFIRM_HUM = 'yes';
    Public Static final Integer HUMPageSize = 5;

    Public Static final String sQueueSelection = 'HUMQueueSelection';
    Public Static final String sTaskEditPage = 'HUMTaskEditPage';
    Public Static final String sTaskDescription = 'Description';
    Public Static final String HUMNewCaseRedirectPage = 'HUMNewCaseRedirect';
    Public Static final String sEditTaskPage= 'Task Edit';
    Public Static final String sNewTaskPage = 'New Task';
    Public Static final String sTask = 'Task';
    Public Static final String sNoneDisplay = '---None--';
    Public Static final String sNone = 'None';

    //Constant Variables created for REQ-198627 And REQ-198628
    Public Static final String sYESValue = 'Y';
    Public Static final String sNoValue = 'N';
    Public Static final String sDayValue = 'D';
    Public Static final String sMonthValue = 'M';
    Public Static final String sHourValue = 'H';
    Public Static final String sFMValue = 'FM';
    Public Static final String sIMValue = 'IM';
    Public Static final String sEMvalue = 'EM';
    Public Static final String sGDvalue = 'GD';
    Public Static final String sBenfitPackageId = 'SFINHDHP';
    Public Static final String sZeroOne = '01';
    Public Static final String sZeroZeroOne = '001';
    Public Static final Integer iGBESTimeOut = 120;
    Public Static final String sProcessMethodName = 'processResponse';    
    Public Static final String sMTV_Value = 'MTV';
    Public Static final String sLV_Value= 'LV';
    Public Static final String sCI_Value = 'CI';
    Public Static final String sCAS_Value = 'CAS';
    Public Static final String sEM_Value= 'EM';

    //Constants Variables for HTTP Status Codes
    Public Static final Integer HTTP_OK = 200;
    Public Static final Integer HTTP_ACCEPTED = 202; 

    // constants for Group Search Controller
    Public Static final String Seperator = '|';
    Public Static final String sGRPValue = 'GRP';
    Public Static final String sODSValue = 'ODS';
    Public Static final String sDummyBenValue = 'XXXXXXXX';
    
    //constants used in HUMInquiryHistoryController
    Public Static final String DATETIMEOPENED = 'DateTimeOpened';
    Public Static final String DESCENDING_LITERAL = 'DESC';
    Public Static final String bViewAll = 'bViewAll';
    Public Static final String Yes = 'yes';
    Public Static final String refId = 'refId';
    Public Static final String fromDt= 'fDate';
    Public Static final String toDt = 'tDate' ;
    Public Static final Integer fromDateVal = 90;
    
    // ----------------------- Constants for Pharamcy Service starts --------------------------

    Public Static final String HUM_PARTD_DRUGREQUESTOR = 'CRM';
    Public Static final String HUM_PARTD_DRUGSEARCHKEY = 'Name';
    Public Static final String HUM_PARTD_SAMPLEDRUG = 'Sample Drug';
    Public Static final String HUM_PARTD_DOSAGEREQUESTOR = 'CDT';
    Public Static final String HUM_PARTD_DOSAGESEARCHKEY = 'MedId';
    Public Static final String HUM_PARTD_DRUGNAME = 'drugName';
    Public Static final String HUM_PARTD_MEDIDVALUE = 'MedIdValue';
    Public Static final String HUM_PARTD_DRUGPLUGIN_NAME = 'DrugSearchName';
    Public Static final String HUM_PARTD_DRUGPLUGIN_TAG = 'DrugSearch';
    Public Static final String HUM_PARTD_DRUGPLUGIN_RESPONSEOUTPUT = 'responseOutput';
    Public Static final String HUM_PARTD_DRUGPLUGIN_CALLOUTEXCEPTION ='calloutException';
    Public Static final Integer HUM_PARTD_ZERO = 0;
    Public Static final Integer HUM_PARTD_FIFTEEN = 15;
    Public Static final String HUM_PARTD_DOSAGEPLUGIN_NAME ='DrugDosage';
    Public Static final String HUM_PARTD_DOSAGEPLUGIN_TAG = 'DrugSearch';

    // ----------------------- Constants for Pharamcy Service ends --------------------------
    
    
     //Constants for Case Attchment functionality -- Classes  HUMAttachmentNewButtonOverrideExtension
    Public Static final String HUM_CASEATTACHMENT_ATTCURRENTSUBTABIDPARAM = 'attCurrentSubTabIdParam';
    Public Static final String HUM_CASEATTACHMENT_HUMSMALLID = 'id';
    Public Static final String HUM_CASEATTACHMENT_HUMAPEXINURL = '/apex/';
    Public Static final String HUM_CASEATTACHMENT_HUMATTACHMENTEDITPAGE = 'HUMAttachmentEdit';
    Public Static final String HUM_CASEATTACHMENT_HUMQUEUESELECTIONPAGE = 'HUMQueueSelection';
    Public Static final String HUM_CASEATTACHMENT_HUMNEXTPAGE = 'nextPage';
    Public Static final String HUM_CASEATTACHMENT_HUMOBJECTTYPE = 'objectType';
    Public Static final String HUM_CASEATTACHMENT_HUMACTIONTYPE = 'Error';
    Public Static final String HUM_CASEATTACHMENT_HUMQUEUEYESFORATTACHMENT = 'YesForAttachement';
    Public Static final String HUM_CASEATTACHMENT_HUMOBJECTNUMBER = 'objectNumber';
    Public Static final String HUM_CASEATTACHMENT_HUMOBJECTID = 'objectId';
    Public Static final String HUM_CASEATTACHMENT_HUMTASK_NUMBER = 'Task_Number';
    
    
    
    //--------------------------------- Constant variable for Launch Case Action Disenrollment
     Public Static final String  HUM_MEDICAREDAC_CASEID = 'CaseId';
     Public Static final Integer HUM_CASEACTIONLAUNCH_ZERO = 0;
     Public Static final Integer HUM_CASEACTIONLAUNCH_LASTINDEX = 15;
     Public Static final Integer HUM_CASEACTIONLAUNCH_ONE = 1;
     Public Static final String HUM_CASEACTIONLAUNCH_HUMFLOWADDRESSVERIFDESC = 'Medicare Address Verification Form';
     Public Static final String HUM_CASEACTIONLAUNCH_HUMFLOWMEDICAREAVF = 'Medicare AVF';
     Public Static final String HUM_CASEACTIONLAUNCH_HUMFLOWMEDICAREEXPEDITEDDESC = 'Medicare/Medicaid Expedited Appeal';
     Public Static final String HUM_CASEACTIONLAUNCH_HUMFLOWMEDICAREEXPEDITED = 'Medicare/Medicaid Expedited Appeal';
     Public Static final String HUM_CASEACTIONLAUNCH_HUMFLOWMEDICAREOIDESCRIPTION = 'Medicare Other Insurance (OI) Form';
     Public Static final String HUM_CASEACTIONLAUNCH_HUMFLOWMEDICAREOI = 'Medicare OI';
     Public Static final String HUM_CASEACTIONLAUNCH_HUMFLOWMEDICAREPARTDESCR = 'Medicare Part D Redetermination Request';
     Public Static final String HUM_CASEACTIONLAUNCH_HUMFLOWMEDICAREPARTD =  'Medicare Part D';
     Public Static final String HUM_CASEACTIONLAUNCH_HUMFLOWTABID = 'tabId';
     Public Static final String HUM_CASEACTIONLAUNCH_HUMFLOWCOBPROCESSTYPE = 'ProcessType';
     Public Static final String HUM_CASEACTIONLAUNCH_HUMATTACHMENTLOGFORDAC = 'Medicare_Election_Lock_In_Template';
     Public Static final String HUM_CASEACTIONLAUNCH_HUMMEDICAREDAC = 'Medicare DAC';
     Public Static final String HUM_CASEACTIONLAUNCH_CASESUBTABID = 'caseSubTabId';
     
    //--------------------------------- Constant variable for Launch Case Action Disenrollment ends
    
    //Constants for Links framework
    Public Static final String EQUALS = 'Equals';
    Public Static final String NOT_EQUALS = 'Not Equals';
    Public Static final String CONTAINS = 'Contains';
    Public Static final String STARTS_WITH = 'Starts With';
    Public Static final String DOES_NOT_START_WITH = 'Does Not Start With';
    Public Static final String ENDS_WITH = 'Ends With';
    Public Static final String AKA_NAME_SESSION_EXPIRED_ERR = 'The aka name session has expired, please logout of salesforce and log back in again to continue';
    Public Static final String LINKSPAGENAME = 'DisplayCustomLinks_VF_HUM';
    Public Static final String SFDCQUERYEXCEPTION = 'Criteria source cannot query foreign key relationships more than 5 levels away from the root SObject';
    Public Static final String ERR_USERID_BLANK = 'User ID or Variable Name cannot be blank.';
    
    //---------------------Billing Constants-------------
    Public Static final String sCallingSystemID = 'CRMS';
    Public Static final String sUserIDType = 'INTL';
    Public Static final String sTransactionType = 'P';
    Public Static final String sRateLevel = 'Account';
    Public Static final String sCurrentBalance = 'Current Balance';
    Public Static final String sCurrentNonSSABalance = 'Current Non-SSA Balance';
    Public Static final String sPastDue = 'Past Due';
    Public Static final String sNonSSAPastDue = 'Non-SSA Past Due';
    Public Static final String sBillingProfStatus = 'All'; 
    
    //----------------------------Constants Added for Latency Fixes starts--------------------------------------
    Public Static final String HUM_CIPHERCLOUD = 'CIPHERCLOUD';
    Public Static final String HUM_APPLICATION_NAME = 'APPLICATION_NAME';
    
    //----------------------------Constants added for Latency Fixes ends----------------------------------------    
    
    //--------------------------------- Constants for HUMTaskListViewController ---------------------------------
    Public Static final String STRING_NUMERIC_ZERO_ZERO_FIVE = '005';    
    Public Static final String ERROR_TEXT_CASE_RECLAIM = 'You can\'t claim this Case, it\'s already been claimed';
    Public Static final String ERROR_TEXT_TASK_RECLAIM = 'You can\'t claim this Task, it\'s already been claimed';
    Public Static final String ERROR_TEXT_TASK_ROUTING_SELECT = 'Please select at least one record to Accept';
    Public Static final String PRIORITY_AND_AGE_FORMATTED = 'Priority & Age';
    Public Static final String ERROR_QUEUE_NO_ACCESS = 'You don\'t have access to Queues';
    Public Static final String ERROR_TASK_NO_RECORD = 'Please select atleast one record to Accept';
    Public Static final String ERROR_TASK_CHANGE_OWNER = 'Please select atleast one record to Change Owner';
    Public Static final String HUM_STRING_CASES = 'Cases';
    Public Static final String HUM_STRING_TASKS = 'Tasks';  
    
    
     //---------------------C/I Redesign Constants-------------
    Public Static final String HUM_CASEEDIT_CLOSEVALIDATIONMESSAGE= 'Please select status as closed or cancelled.';
    Public Static final String HUM_CASEEDIT_VALIDATIONEXP = 'FIELD_CUSTOM_VALIDATION_EXCEPTION';
    Public Static final String HUM_CASEEDIT_STATUS = 'Status';
    Public Static final String HUM_CASEEDIT_CLOSED = 'Closed';  
    Public Static final String HUM_CASEEDIT_CANCELLED = 'Cancelled';  
    Public Static final String HUM_CASEEDIT_CLOSEDSTATUS = '&Status=Closed&';
    Public Static final String HUM_CASEEDIT_CANCELLEDSTATUS = '&Status=Cancelled&';
    Public Static final String HUM_CASEEDIT_PAGEFROM = 'PageFrom';
    Public Static final String HUM_CASEEDIT_CASEHISTORY = 'CaseHistory';
    Public Static final String HUM_CASEEDIT_NEWCASE = 'New Case';
    Public Static final String HUM_CASEEDIT_ACCOUNTRELLIST = 'AccountRelatedList';
    Public Static final String HUM_CASEEDIT_CASENAME = 'Edit Case :';
    Public Static final String HUM_CASEEDIT_EDITPAGENAME = 'caseEdit_VF_HUM';
    Public Static final String HUM_CASEEDIT_CASECLOSEERRORPAGEMSG = 'The open Work Task(s) listed below must be closed before closing or canceling the case.';
    Public Static final String HUM_CASEEDIT_CINOTAVAIABLE = 'NOTAVAILABLE';
    Public Static final String HUM_CASEEDIT_CLOSEERRORPAGE ='/apex/HUMCaseCloseErrorPage?id=';
    Public Static final String HUM_CASEEDIT_STATUSFORURL = '&Status=';
    
        //---------------------Case history Constants----------------------
        Public Static final Integer HUM_CASEHISTORY_CONSTANTHUNDRED = 100;
    
        //----------------------Constants for Classifications And Classification Types------------------
     Public Static final String HUM_CCP_CLASSIFICATION_TYPE_ENROLLMENTRSO = 'Enrollment (RSO)';  
     Public Static final String HUM_CCP_CLASSIFICATION_ELIGIBILITYRSOENR = 'Eligibility (RSO ENR)';
     Public Static final String HUM_CCP_CLASSIFICATION_ENROLLMENTRSOENR = 'Enrollment (RSO ENR)';
     
      //---------------------Vision Constants-------------
    Public Static final String HUM_APINAME_POLICY = 'Policy__c';
    Public Static final String HUM_APINAME_POLICYMEMBER = 'policy_Member__c';    
    Public Static final String HUM_LABEL_POLICYPRODTYPE = 'Product Type Code'; 
    Public Static final String HUM_LABEL_POLICYMEMPRODTYPE  = 'Product Type';   
    Public Static final Integer HUM_LABEL_PAGESIZE  = 5;   
    Public Static final Integer HUM_LABEL_PAGENUMBER  = 1;           
    Public Static final String HUM_FIELDVALUE_PRODUCTNAME  = 'VIS'; 

    //--------------------------REQ 246888------------------
    Public Static final String HUM_VITALITY_ENTITY_ID = 'Vitality-Entity-ID'; 
    Public Static final String HUM_VOBWEBACTRESBACK = 'processContinuationVobHistory';
    Public Static final String HUM_ACCUMMBECALLBACK ='processMBECallResponse';
    Public Static final String HUM_ACCUMMBESERVICE='MBEService';
    Public Static final String HUM_ACCUMODSGENKEYE='ODS_GENKEY';
    Public Static final Integer HUM_VOBONE = 1;
    Public Static final String HUM_DOLLAR_SIGN = '$';
    Public Static final String HUM_ZERO_SIGN = '0';
    Public Static final String HUM_DOLLAR_ZERO_SIGN = '$0.00';
    Public Static final String HUM_TRUE_VAL = 'true';
    Public Static final String HUM_FALSE_VAL = 'false';
    Public Static final Integer HUM_MONTH_VAL = 12;
    Public Static final Integer HUM_DAY_VAL = 31; 
    Public Static final String HUM_ACCUMS_DETAILS = 'AccumsService';
    Public Static final String HUM_ACCUMS_CALLBACK = 'prepareServiceData';
    Public Static final String HUM_TYPES = 'Current';
    Public Static final String HUM_TIME_FORMAT = 'T23:59:59';

    // ---------------------------------- Start VOB - Constant variables for class Benefits_C_HUM,Benefits_S_HUM,Benefits_D_HUM -------------------------------
    Public Static final String  HUM_BENEFITS_MED = 'MED';
    Public Static final String  HUM_BENEFITS_DEN = 'DEN';
    Public Static final String  VOB_MEMBERMASTERCRITERIAPERSONID_HUM = 'MemberMasterCriteriaPersonId';
    Public Static final String  VOB_CURRENT_HUM = 'Current';
    Public Static final String  VOB_PAST_HUM = 'Past';
    Public Static final String  VOB_FUTURE_HUM = 'Future';
    Public Static final String  VOB_BENEFITPACKAGEID_HUM = 'BenefitPackageId';
    Public Static final String  VOB_ACTIVEMEMBERSTATUS_HUM = 'Active';
    Public Static final String  VOB_VALUEPDQ_HUM = 'PDQ';
    Public Static final String  VOB_SERVICESWITHPLACEOFSERVICE_HUM = 'ServicesWithPlaceOfService';
    Public Static final String  VOB_RIDERS_HUM = 'Riders';
    Public Static final String  VOB_PLANCOMMENTS_HUM = 'PlanComments';
    Public Static final String  VOB_RXDETAIL_HUM = 'RxDetail';
    Public Static final String  VOB_TERMEDDATE_HUM = '12/31/9999';
    Public Static final String  VOB_MASTERSERVICES_HUM = 'MasterServices';
    Public Static final String  VOB_GENERALBENEFITS_HUM = 'GeneralBenefits';
    
    //--------------------------------------- Start MBEConstants for REQ- 244445---------------------------------//
    Public Static final String HUM_PCP_SER_CONTENTTYPE = 'content-type';
    Public Static final String HUM_PCP_SER_CONECTION = 'Connection';
    Public Static final String HUM_PCP_SER_KEEPALIVE = 'keep-alive';
    Public Static final String HUM_PCP_SER_SOAPACTION = 'SOAPAction';
    Public Static final String HUM_PCP_MEMCR_PERIOD = 'Current';
    Public Static final String HUM_PCP_MEMCR_PAST_PERIOD = 'Past';
    Public Static final String HUM_PCP_MEMCR_FUTURE_PERIOD = 'Future';
    Public Static final String HUM_PCP_MEMCR_TYPE = 'MemberMasterCriteriaPersonId';
    Public Static final String HUM_PCP_DATESEPERATOR = '-';
    Public Static final String HUM_MBE_SERVICE = 'MBEService';
    Public Static final String HUM_PCP_WARNINGMSG = 'History table reflects previous and future PCP’s/PCD\'s. Future PCP’s/PCD\'s assigned will show no end date'; 
    Public Static final String HUM_PCP_REFDATEFORMT ='yyyy-MM-dd';    
    Public Static final String HUM_PCP_PROCESSRESMETHOD ='ProcessMbeResponse';
    Public Static final String HUM_PCP_MBESERVICENAME ='MBEService';     
    //--------------------------------------- End MBEConstants for REQ- 244445---------------------------------//
    //--------------------------------------- Start MBEConstants for REQ-244431---------------------------------//    
    Public Static final String HUM_GBE_SERVICE = 'GroupBusinessEntityService';
    Public Static final String HUM_PLATFORM_CODE_LV = 'LV';
    Public Static final String HUM_PLATFORM_CODE_CI = 'CI';
    Public Static final String HUM_PLATFORM_CODE_CAS = 'CAS';
    Public Static final String HUM_PLATFORM_CODE_EM = 'EM';
    Public Static final String HUM_INCLUSION_CRITERIA = 'BENEFITS';
    Public Static final String HUM_INDICATOR_PRIORCOVERAGE = 'PriorCoverageIndicator';
    Public Static final String HUM_INDICATOR_PRIORORTHO = 'PriorOrthoIndicator';
    Public Static final String HUM_INDICATOR_INCOMPLETE = 'IncompleteIndicator';
    Public Static final String HUM_MBE_GBE_PBE_Response = 'parseMBE_GBE_PBEResponse'; 
    //--------------------------------------- End MBEConstants for REQ- 244431---------------------------------//
    // -------------------------------------- REQ - 237144 ----------------------------------------------------//
    
    Public Static final String VOB_ACCUMS_ZERO_HUM = '0';
    Public Static final String VOB_ACCUMS_ZERODOTZERO_HUM = '0.00';
    Public Static final String VOB_ACCUMS_NOLIMIT_HUM = 'No Limit';
    // ---------------------------------- End VOB - Constant variables for class Benefits_C_HUM,Benefits_S_HUM,Benefits_D_HUM -------------------------------
    
    // ------------------------- Defect # 221133.0001 -----------------------------------------------------------------------//
    
    Public Static final String THREEFOURSEVENFOUR_CCPGCPINQUIRY_HUM = '3474';
    Public Static final String THREEFOURSEVENFOURONE_CCPGCPINQUIRY_HUM = '3474_1';
    Public Static final String THREEFOURNINEZERO_CCPGCPINQUIRY_HUM = '3490';
    
     //--------------------------------------- Start MBEConstants for REQ- 241730---------------------------------//
     Public Static final String HUMCCPGCPRSOMEDICARE ='RSO- Medicare';
     Public Static final String HUMCCPGCPMEDICAIDLOB ='MCD';
     Public Static final String HUMCCPGCPMEDICAID ='Medicaid';
     Public Static final String HUMCCPGCPMEDICARE ='Medicare';
    Public Static final String HUMCCPGCPMEDICAIDTEAMID ='14733'; 
    Public Static final String HUMCCPGCPMEDICARETEAMID ='3467'; 
    
    //--------------------------------------- Start MBEConstants for REQ- 241730---------------------------------//
    //--------------------------------REQ - 237426 -----------------------------------------------//
    Public Static final String VOB_PBE_FAULT_CODE = 'faultcode';
    //--------------------------------MBE DTO Cretion -----------------------------------------------//
    Public Static final String VOB_GENKEY_KEYTYPE = 'ODS_GENKEY';
     // -------------------------REQ - 241731 -----------------------------------------------------------------------//
   
    Public Static final String VOB_DENTAL_PARAMETERID_HUM = 'id';
    Public Static final String VOB_DENTAL_CCPURL_HUM = 'CCPURL';
    Public Static final String VOB_DENTAL_CONTRACTID_HUM = '?txtContractID=';
    Public Static final String VOB_DENTAL_TXTMBRID_HUM = '&txtMbrID=';
    Public Static final String VOB_DENTAL_CRTDBY_HUM = '&CrtdBy=';
    Public Static final String VOB_DENTAL_REFERENCEANDINQUIRYID_HUM = '&txtCrtdTm=&ReferenceId=&InquiryId=';
    Public Static final String VOB_DENTAL_TARGET_HUM = 'TARGET';
    
    //------------------------------- REQ - 176873 ---------------------------------------------------// 
    
    Public Static final String VOB_CERTIFICATE_PARAMETERID_HUM = 'Id';
    Public Static final String VOB_CERTIFICATE_CERTIFICATEURL_HUM = 'CertificateURL';
    Public Static final String VOB_CERTIFICATE_CALLBACK_HUM = 'ProcessMBEResponse';
    Public Static final String VOB_CERTIFICATE_PERS_HUM = '?pers=%20';
    Public Static final String VOB_CERTIFICATE_PTFM_HUM = '&ptfm=';
    Public Static final String VOB_CERTIFICATE_PROD_HUM = '&prod=';
    Public Static final String VOB_CERTIFICATE_LOB_HUM = '&lob=';
    Public Static final String VOB_CERTIFICATE_SEQ_HUM = '&seq=';
    Public Static final String VOB_CERTIFICATE_CUST_HUM = '&cust=';
    Public Static final String VOB_CERTIFICATE_CCNBR_HUM = '&ccnbr=';
    Public Static final String VOB_CERTIFICATE_SUB_HUM = '&sub=';
    Public Static final String VOB_CERTIFICATE_ASOF_HUM = '&asof=';
    Public Static final String VOB_CERTIFICATE_PORT_HUM = '&port=22';
    
    
     // ----------------------- Constants for Launch GCP Starts--------------------------
    
    Public Static final String HUM_LaunchGCPCCP_CALLINGAPP = 'SFDC';
    Public Static final String HUM_LaunchGCPCCP_MODE = 'View';
    Public Static final String HUM_LaunchGCPCCP_INBOUNDCALL = 'Inbound Call';
    Public Static final String HUM_LaunchGCPCCP_INBOUNDCODE = 'IC';
    Public Static final String HUM_LaunchGCPCCP_OUTBOUNDCALL = 'Outbound Call';
    Public Static final String HUM_LaunchGCPCCP_OUTBOUNDCODE = 'OC';    
    Public Static final String HUM_LaunchGCPCCP_FIMMASPLATFORM = 'FM';
    Public Static final String HUM_LaunchGCPCCP_CONTRACTIDTYPE = 'ContractID';     
    Public Static final String HUM_LaunchGCPCCP_GROUP = 'Group';
    Public Static final String HUM_LaunchGCPCCP_MEMBER = 'Member';
    Public Static final String HUM_LaunchGCPCCP_AGENTBROKER = 'Agent/Broker';
    Public Static final String HUM_LaunchGCPCCP_PROVIDER = 'Provider';    
    Public Static final String HUM_LaunchGCPCCP_UNKNOWNGROUP = 'Unknown Group';
    Public Static final String HUM_LaunchGCPCCP_UNKNOWNMEMBER = 'Unknown Member';
    Public Static final String HUM_LaunchGCPCCP_UNKNOWAGENTBROKER = 'Unknown Agent/Broker';
    Public Static final String HUM_LaunchGCPCCP_UNKNOWNPROVIDER = 'Unknown Provider';
    Public Static final String HUM_LaunchGCPCCP_UNKNOWN = 'Unknown';    
    Public Static final String HUM_LaunchGCPCCP_KM = 'KM';
    Public Static final String HUM_LaunchGCPCCP_KE = 'KE';
    Public Static final String HUM_LaunchGCPCCP_KP = 'KP';
    Public Static final String HUM_LaunchGCPCCP_KA = 'KA';    
    Public Static final String HUM_LaunchGCPCCP_UM = 'UM';
    Public Static final String HUM_LaunchGCPCCP_UE = 'UE';
    Public Static final String HUM_LaunchGCPCCP_UP = 'UP';
    Public Static final String HUM_LaunchGCPCCP_UA = 'UA';    
    Public Static final String HUM_LaunchGCPCCP_AGENCY = 'Agency';
    Public Static final String HUM_LaunchGCPCCP_BROKER = 'Broker';    
    Public Static final String HUM_LaunchGCPCCP_A = 'A';
    Public Static final String HUM_LaunchGCPCCP_B = 'B';
    
    //---------------------------------Constants for Launch GCP Ends--------------------------
    //--------------------------------------- Start MBEConstants for REQ- 244444---------------------------------//
    Public Static final String HUM_PCP_EPS_PROCESSRESMETHOD ='pcpActiveHistoryFinalResponse';
    Public Static final String HUM_PCP_EPS_PARONLY ='false';
    Public Static final String HUM_PCP_EPS_IDENTIFIERTYPE ='NpiID';
    Public Static final String HUM_PCP_EPS_REQ_DETAILS = 'EPSServiceREST_V1';
    Public Static final String HUM_PCP_EPS_ADDRESS_ALL = 'All';
    Public Static final String HUM_PCP_EPS_ADDRESS_SERVICE = 'Service';
    Public Static final String HUM_PCP_EPS_ADDRESS_BILLING = 'Billing';
    Public Static final String HUM_PCP_EPS_ADDRESS_MAIL = 'Mail';
    Public Static final String HUM_EPS_PHONE = 'phone';
    Public Static final String HUM_REF_DATE = 'refdate';
    //--------------------------------------- End MBEConstants for REQ- 244444---------------------------------// 
     // --------------------------------Constants for Policy Update Starts---------------------
    
    Public Static final String HUMPOLICYUPDATE_NONE = 'NONE'; 
    Public Static final String HUMPOLICYUPDATE_GRPSVC_CLIENT_NAME = 'CCP';
    Public Static final String HUMPOLICYUPDATE_UNINITIALIZED_VALUE = 'Uninitialized'; 
    Public Static final String HUMPOLICYUPDATE_LV_PLATFORM = 'LV'; 
    Public Static final String HUMPOLICYUPDATE_GRPSVC_PLATFORM = 'EM'; 
    Public Static final Integer HUMPOLICYUPDATE_GROUP_GBE_CALL_DAYS = 3;
    Public Static final String HUMPOLICYUPDATE_GRP_SEARCH_SERVICE = 'GroupSearchService' ;
    Public Static final String HUMPOLICYUPDATE_HUMINSTGRPSEARCHSERVICESOAP = 'HUMIntGroupSearchService.soap';
    Public Static final String HUMPOLICYUPDATE_CLASSNAME = 'HUMPolicyDetailsUpdateController';
    Public Static final String HUMPOLICYUPDATE_METHODUPDATEPOLICY = 'updatePolicy';
    Public Static final String HUMPOLICYUPDATE_METHODUPDATEPOLICYDETAILS = 'updatePolicyDetails';
    Public Static final String HUMPOLICYUPDATE_POLICYMEMBER = 'Policy__c';
    Public Static final String HUMPOLICYUPDATE_ACCOUNT = 'Account';
    
    // --------------------------------Constants for Policy Update Ends---------------------
    // -------------------------REQ - 252624 Start-----------------------------------------------------------------------//
   
    Public Static final String VOB_REMAPPURL_HUM = 'REMAPPURL';
    // -------------------------REQ - 252624 END-----------------------------------------------------------------------//
    
     // --------------------------------Constants for REQ  REQ - 237004 And REQ - 237066 ---------------------
    
    Public Static final String RSO_MEMBER_TYPE = 'Member-Id-Base;';
    Public Static final String POLICY_PLATFORM_INFO = 'LV;';    
    Public Static final String HUMMedicare = 'MedicareID';
    Public Static final String HUMConstRecord = 'Record';
    Public Static final String HUMPOAFields = 'First Name;Last Name;Phone;POA Type;';
    Public Static final String HUMMEMBER_ID_TYPE = 'Member-Id-Base';
    
       //--------------------------------------- Start constants for MentorRedirect_C_HUM---------------------------------//
    Public Static final String PARAMSUBGROUP_MENTORREDIRECT_HUM = '?queryText=subgroup:';
    Public Static final String CONSTANTSUBGROUP_MENTORREDIRECT_HUM = '&benefitgrid=0&filterType=Documents';
    Public Static final String ZEROS_MENTORREDIRECT_HUM = '000';
    Public Static final String TARGET_MENTORREDIRECT_HUM = 'TARGET=';    
    Public Static final String PARAMFILTER_MENTORREDIRECT_HUM = '?filterType=Documents';
    
    public static final String PARAMTITLE_MENTORREDIRECT_HUM = '?queryText=';
    public static final String CONSTANTPARAMEND_MENTORREDIRECT_HUM = '&source=CCP&benefitgrid=0&filterType=Benefit Grids';
    Public Static final String SUBGROUP_MENTORREDIRECT_HUM = 'subgroup:';

    //--------------------------------------- End constants for MentorRedirect_C_HUM---------------------------------//
       //--------------------------------------- Start constants for RightSourceService ------------------------------- //
    
     Public Static final String HUM_RSGetMember_Response = 'parseRSGetMemberResponse'; 
     Public Static final String HUM_RSS_SER_CONTENTTYPE = 'content-type';
     Public Static final String HUM_RSS_SER_CONECTION = 'Connection';
     Public Static final String HUM_RSS_SER_KEEPALIVE = 'keep-alive';
     Public Static final String HUM_RSS_SER_SOAPACTION = 'SOAPAction';
     Public Static final String HUM_CreateEditOrder_Response = 'parseMemberAndScriptResponse';
   //--------------------------------------- End constants for RightSourceService ---------------------------------//
    
    //--------------------------------------- Constants for REQ - 304806 And REQ - 304807 ---------------------------//
    
    Public Static final String HUMMedicareCase = 'Medicare Case';
   // ------ Constants for REQ - 306322 : ------------------------
  
   Public Static final String HUMiAboutTypeDesc  ='iAboutTypeDesc';    
   Public Static final String HUMiWithTypeDesc  = 'iWithTypeDesc';    
   Public Static final String HUMiWithType  = 'iWithType';      
   Public Static final String HUMiAboutType  = 'iAboutType';  
   Public Static final String HUMName = 'Name';
   
     //---------------------------------------------------Req - 333042 Resolution Constant-----------------//
   Public Static final String HUMAppealCase = 'Appeal Case';  // used for Resolution Appeal case
   Public Static final String HUM_APPEAL_CASE_RT = 'Appeal Case';

	//	-------------Constant for REQ - 332329 ------------------------------------//
	Public Static final String BENEFITMATRIX_MTV_PLATFORM_HUM = 'CRM CCP MTV Benefit Matrix';
    Public Static final String BENEFITMATRIX_CAS_PLATFORM_HUM = 'CRM CAS Benefit Matrix';
    Public Static final String BENEFITMATRIX_FILTER_DOCUMENTS_HUM = '&filterType=Documents';
	
	  //---------- Constants  for Omni Routing Configuration ------------- //
    Public Static final String HUMQueueRoutingConfig = 'QueueRoutingConfig';
   Public Static final String HUMQueueRoutingConfigRoutingModel = 'MostAvailable'; 
   Public Static final String HUMQRConfigEndpointURL = 'OmniRoutingConfig_URL';
   Public Static final String HUMOverflowQueueName =  'Omni_Overflow_Assignee_Queue';    
   
   //------------- Constant for REQ - REQ - 357679 ------------------- //
   
   Public Static final String sProcFilterType = '?filterType=Codes&queryText=';   
	
    public static final String HUMDateTimeFormat = 'MM/dd/yyyy hh:mm a';
	//------------- REQ - 359093 - SF - USER - Research/Attachments - New TASK tab - EDIT mode for manual creation----------------//
	public static final string ListdefaultID = 'DEFAULT_VALUE';
	public static final string ListdefaultValue = '--None--';          
	
    
	
	//Used for Auto case creation by Web Chat origin
    Public Static final String WEBCHAT_DEFAULT_LIVEAGENT_NAME = 'liveagent Site Guest User';   
	
	// RSO Business Communication Chatter Group
	public static final String RSO_CUSTOMER_SERVICE_NEWSFEED_GROUP = 'RSO Customer Service News Feed';
	
    // GBO Business Communication Chatter Group
	Public Static final String GBO_CUSTOMER_SERVICE_NEWSFEED_GROUP = 'GBO News Feed';

 // DCN and URL
    Public Static final String URL_value  = 'DCN URL'; 
    Public Static final String DCN_value  = 'DCN';
    Public Static final String Sysinfo    = 'System Information' ;    

	// Used for REQ - 356041  
     Public Static final Map<String, String> DOCUMENT_TYPE= New Map<String, String>
        {
        'PWD' => 'Password'
        };
   // Used for  REQ 361926 
   Public Static final String HUMExtensionTask = 'Extension Request'; 	
   Public Static final String HUMExtensionLabel = 'Extension Task';

   Public Static final Map<String, String> ALERT_ACTIONS = New Map<String, String>
        {
            'D' => 'Delivered',
            'N' => 'Not Delivered',
            'T' => 'Term Alert'
        };	
   //Used for REQ-388831 to hold max count for SOSL result
	Public Static final Integer maxCountOfSOSL =  101 ;
	
	//------------- Constant for REQ - REQ - 380740 ------------------- //
	
	Public Static final String APPLICATION_SEARCH = 'Application Search';
	
    //Regex for identify alphanumeric value - Req -388832
    Public Static final String regexAlphanumeric= '[^A-Za-z0-9]'; 
	
	//Launch Compass Functionalities
	public static final string HUMDefaultMentorFunction = 'Contact Center RSO';
	public static final string HUMDefaultMentorSubFunction = 'Individual Medicare';
    public Static final String HUMLaunchCompassON = 'ON';
    public Static final String HUMLaunchCompassSwitch = 'HUMLaunchCompassSwitch';
	public static final string HUMCompassFunction = 'function';
	public static final string HUMCompassSubFunction = 'subfunction';
	public static final string HUMCompassIntent = 'intent';
	public static final string HUMCompassClassification = 'classification';
	public static final string HUMCompassPgSource = 'pgname';
	public static final string HUMCompassRSOParam = 'RSO';
    public static final string HUMCompassCaseId = 'caseid';
    public static final string HUMCompassCaseStartWith = '500';
    public static final integer HUMCompassMaxIDLength = 18;
    public static final string HUMSingleSpaceValue = ' ';

    //added for Softphone Links
	public static final string HUMSoftphoneLinksTransferA2A = 'Alternate Softphone Link to another Alternate Softphone Link';
	public static final string HUMSoftphoneLinksTransferD2D = 'Default Softphone Link to another Default Softphone Link';
	public static final string HUMSoftphoneLinksTransferD2A = 'Default Softphone Link to another Alternate Softphone Link';

    public static final string HUMQA_Org_Identifier = 'humqap';
    public static final string HUMQA_MentorCanvasApp = 'SFDCMentorCanvasAppQA';
    public static final string HUMMentorCanvasApp = 'SFDCMentorCanvasApp';
    public static final string HumMentorCanvasAppRF = 'SFDCMentorCanvasAppRF';
    public static final string HUMCompassCaseName = 'casename';
	public static final string HUMRapidForceApp = 'CRM Service Rapid Force';
	public static final string HUMCompassButtonRF = 'Launch Compass';
	public static final string HUMCompassButtonNonRF = 'Search Compass';
	
	public static final string PaidThroughDate = 'PaidThruDate';
	public static final string HUMMacesUnitNumber = 'Macess Unit Number';

    // String for Go365Commercial jive community name
    public static final string HUMGo365Commercial = 'Go365Commercial';
    
    // String for Go365Medicare jive community name
    public static final string HUMGo365Medicare = 'Go365Medicare';
    
    // String for MyHumanaSupport jive community name
    public static final string HUMHumanaSupport = 'humanasupport';
    
    // String for Go365Commercial jive community permission set
    public static final string HUMGo365CommercialPS = 'Community_Member_Go365_Commercial';
    
    // String for Go365Medicare jive community permission set
    public static final string HUMGo365MedicarePS = 'Community_Member_Go365_Medicare';
    
    // String for MyHumanaSupport jive community permission set
    public static final string HUMHumanaSupportPS = 'Community_Member_Support';
    
    // String for Humana Community Member profile
    public static final string HUMHumanaCommunityMemberProfile = 'Humana Community Member';

    // String for Humana Community Member profile
    public static final string HUMJITExceptionMessage = 'Could not find account';

    // String for Humana Community 
    public static final string HUMCommunity = 'Community';

    // String for Auto Interaction population
    public static final string autoInteractionIDvalue = 'autoInteractionIDvalue';

    //US-3959734 : Salesforce Genesys CRM Authentication Status should not change 
    public static final string setRecordIdAccCase = 'setRecordIdAccCase';
    
    //US-3979946 : T1PRJ0036776: PCC- PLT: VOC Survey Transfer Identification
    public static final string uuidata = 'uuidata';

    //Start - Added below constanst for US4595853
    Public Static final String SWITCH_US4595853 = '4595853';
    //string for chat transcript API field for Agent Message
    Public Static final String LIVEAGENT_API_AGENT_MESSAGE = 'AGENT_MESSAGE';
    //string for chat transcript API field for Client Message
    Public Static final String LIVEAGENT_API_CLIENT_MESSAGE = 'CLIENT_MESSAGE';
    //End - Added below constanst for US4595853
    //Added below constanst for US4609720
    Public Static final String SWITCH_US4609720 = '4609720';
    //Added below constanst for US5158379
    Public Static final String SWITCH_US5158379 = '5158379';
}