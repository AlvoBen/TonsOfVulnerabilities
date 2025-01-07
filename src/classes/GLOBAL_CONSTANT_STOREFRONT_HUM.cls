/********************************************************************************************************************************************************
Apex Class Name  : GLOBAL_CONSTANT_STOREFRONT_HUM
Version          : 1.0 
Created Date     : MAR 16 2020
Function         : Constants declaration here that are going to be used across storefront components in the org.
Modification Log :       
* Developer                         Date                 Description
* -------------------------------------------------------------------------------------------------------------------------------------------------------               
* Melkisan Selvaraj             03/16/2020              Original Version REQ - 801835
* Luke P. Cecil					08/24/2020				User Story 1243103: PR00091869 - MF 11 - CRM Storefront Virtual Visitors: New Virtual Non-Member Visitor
* Luke P. Cecil                 09/28/2020              User Story 1474878: PR00091869 - MF 11 - CRM Storefront Scheduled Interactions - Search/Add Visitors Future Interaction Date Error
* Abhishek Maurya				12/22/2020			    User-Story 1755491- Substitute Member Id to Visitor Id on Interaction
* Vinoth.L                 		12/03/2020              User Story 1694784: PR00091869 - MF 9  - CRM Storefront Home Page: Visitor Interactions List Enhancements
* Vinoth L						02/04/2021			    User Story 1878176- Super Admin Role and Permissions Changes
* Vinoth L                      05/04/2021          	User Story 2046674- Notification Interaction Generation
* Vinoth L                      05/14/2021              User Story 2071202 -CRM Storefront Notifications: Date Suppression
* Vinoth L                      05/14/2021              User Story 2068463 -CRM Storefront Notifications: Determine Last Notification Date
* Abhishek Maurya               05/14/2021              User Story 2072454 -CRM Storefront Notifications: Veteran Eligibility for Action Required Prompt 
* Vinoth L                      04/22/2021              User Story 2144275 -CRM Storefront Notifications: New Member Eligibility for Action Required Prompt
* Vinoth L                      05/14/2021          	User Story 2194098 -CRM Storefront Home Page: Pagination
* Abhishek Maurya               05/27/2021              User Story 2159526 -CRM Storefront: Set Visitor ID Using Medical Policy
* Ekta 		                    06/01/2021          	User Story 2322470 -CRM Storefront Virtual Neighborhood Center - New Visitor
* Abhishek Maurya               06/07/2021              User Story 2360937 -CRM Storefront SDoH: Health Educator 1x1 Dropdown
* Isha Gupta                    06/16/2021              User Story 2361161 - CRM Storefront VNC: Create Event Service Constants
* Ananya Singh					06/30/2021				User Stroy 2389080 -  CRM Storefront VNC: Unregister Participant for Event
* Abhishek Maurya               06/26/2021              User Story 2308421 -CRM Storefront SDoH: Action Required Prompt
* Vinoth L                      06/26/2021          	User Story 2308568 -CRM Storefront SDoH: Track DH&A Screening Results
* Abhishek Maurya               07/03/2021              USer Story 2146105 -CRM Storefront: Delete Calendar Event 
* Akshay Pai                    07/15/2021              US-2360057 : Restrict Access to Virtual Locations
* Vinoth L                      07/21/2021              User Story - 2492158 T1PRJ0001894 - MF 7 - CRM Storefront SDoH: Track All Screenings
* Abhishek Maurya               07/21/2021              User Story - 2491779 T1PRJ0001894 - MF 7 - CRM Storefront SDoH: One-on-One Tracking Enhancements
* Vinoth L                      09/02/2021              User Story - 2508432 T1PRJ0001894 - MF 7 - CRM Storefront SDoH: Share Screening Results - Call DH&A 
* Abhishek Maurya               09/02/2021              User Story - 2599455 T1PRJ0001894 - MF 7 - CRM Storefront SDoH: Share Screening Results - Update Existing 1x1 Tracking
* Ananya                		09/13/2021	  		 	User Story - 2668025 T1PRJ0001894 - MF0 - Sprint 45- Tech -  Storefront VNC SonrQube security vulnerability fixes  
* Vinoth L                      09/29/2021              User Story - BreakFix T1PRJ0001894 - MF 7 - CRM Storefront: SDoH Retrieval - No Member Found
* Ananya                        09/30/2021              User Story - 2561838 T1PRJ0001894 - MF 12 - CRM Storefront Task Management: Task Access and Custom Fields
* Ekta                          09/23/2021            	User Story 2561850 -T1PRJ0001894 - MF 12 - CRM Storefront Task Management: Contacted Button
* Vinoth L                      09/23/2021            	User Story 2635072 -T1PRJ0001894 - MF 12 - CRM Storefront Task Management: Action Required Prompt
* Vinoth L                      10/06/2021            	User Story - 2578364 T1PRJ0001894 - MF 12 - CRM Storefront Task Management: Import Function
* Abhishek Maurya               10/06/2021              User Story - 2578377 -CRM Storefront Task Management: Import File Confirmation
* Vinoth L                      10/20/2021              User Story - 2578436 T1PRJ0001894 - MF 12 - CRM Storefront Task Management: Add Tasks
* Abhishek Maurya               10/20/2021              User Story - 2578417 T1PRJ0001894 - MF 12 - CRM Storefront Task Management: Add Task Validation
* Ekta Bist                     10/20/2021              User Story - 2578506 T1PRJ0001894 - MF 12 - CRM Storefront Task Management: Export Errors
* Ekta Bist                     10/20/2021              User Story - 2956323 Task Import BreakFIx
* Ekta Bist                     12/13/2021              User Story 2792824: T1PRJ0001894 MF7 Storefront Engagement: Go365 Eligible Interaction Reason Hover
* Ekta Bist                     12/22/2021              User Story 2789307: T1PRJ0001894 MF7 Storefront Engagement: Account Hover
* Lakshmi Madduri               01/19/2022              User Story - 2792911 CRM Storefront Home Page: Visitor Interactions Scroll
* Vinoth L						01/27/2022				User Story 2789917- T1PRJ0154546 MF9 Storefront Search/Add Visitors: Prevent Duplicate Virtual Visitors
* Vivek Sharma					01/13/2022				User Story 2792815: T1PRJ0154546 MF9 Storefront Systematically Remove Notification & SDOH Records When User Deletes an Interaction
* Vinoth L                      01/15/2022            	User Story - 2793063 T1PRJ0154546 MF8 Storefront Home Page: Birthday Icon
* Vivek Sharma                  02/15/2022              User Story 2940968: T1PRJ0154546 MF9 Storefront Home Page: Missing 'Event' Interactions
* Vivek Sharma                  02/25/2022              User Story 3118168: T1PRJ0154546 MF9 Storefront - Mass Delete Task Records & New List View 
* Ekta Bist                     03/07/2022              User Story 3153697 - T1PRJ0154546 MF10 Storefront - Update 'Reason for Visit' Picklist Values on Health Educator 1x1 Tracking Form  
* Lakshmi Madduri               02/17/2022              User Story 3104178 - T1PRJ0154546 MF9 Storefront Modernization - Create New Dashboards Tab
* Lakshmi Madduri               05/17/2022              US-3201663 Storefront - Scan Card: Inactive Accounts
* Vinoth L                      3/18/2022               User Story 2792978 -T1PRJ0154546 / SF / MF9 Storefront - Search/Add Visitors: Inactive Accounts
* Vivek Sharma                  03/09/2022              User Story 3075114: T1PRJ0154546 MF9 Storefront - Figure 8 Service Strategy Documentation
* Akshay Pai                    06/03/2022              User Story 3175716: T1PRJ0154546 MF10 Storefront - Dummy API for DynaTrace (Salesforce)
* Vinoth L                      06/03/2022              User Story 3434998: T1PRJ0154546 / SF / MF9 Storefront: Duplicate Notification Interactions
* Vivek Sharma                  04/06/2022              User Story 3248244: T1PRJ0154546 / SF / MF9 Storefront - Display Visitor ID
* Lakshmi Madduri               6/14/2022               US-3258682 T1PRJ0154546 / SF / MF9 Storefront - SNP: Indicators Field - Data Population
* Vivek Sharma                  04/25/2022              User Story 3316820: T1PRJ0154546 / SF / MF9 Storefront - SNP: Change Notifications Section to Custom
* Vinoth L                      06/15/2022              User Story 3316885: T1PRJ0154546 / SF / MF9 Storefront - SNP: Action Required/Account Hover/Alert Icon
* Sahil Verma                   07/06/2022              User Story 3450456: T1PRJ0154546 / SF / MF9 Storefront Modernization (Coordinator/Service Associate) - Home (Right Side) Added Attendance Switch
* Vivek Sharma                  06/28/2022              User Story 3450197: T1PRJ0154546 / SF / MF9 Storefront Modernization (Business Admin/Super Admin) - Home (Right Side)
* Vinoth L	                    07/01/2022           	User Story 3449791: Storefront Modernization (Business Admin/Super Admin) - Home (Left Side)
* Vivek Sharma                  07/18/2022              User Story 3450175: T1PRJ0154546 / SF / MF9 Storefront Modernization (Business Admin/Super Admin) - Interactions/Events (Left)
* Vivek Sharma                  08/04/2022              User Story 3516893: T1PRJ0154546 / SF / MF9 Storefront: Calendar Scroll to Top
* Vinoth L	                    08/01/2022           	User Story 3510484: T1PRJ0154546 / SF / MF9 Storefront: Recommend Events (Story 1)
* Vivek Sharma                  09/22/2022              User Story 3581472: T1PRJ0154546 / SF / MF9 Storefront: Ability to Create Follow Up Task From Visitor Check-Ins
* Vinoth L                      09/14/2022              Task Import Case Sensitivity Fix
* Sahil Verma       	        09/23/2022              User Story 3850860 - T1PRJ0154546 / SF / MF9 Storefront Modernization - Navigation upon check-ins
* Navajit Sarkar                09/27/2022              User Story 3782843: MF9 Storefront: Modernization - Interactions/Events - Ability to View Calendar Events related Constants
* Vinoth L                      10/11/2022              User Story 3759560: T1PRJ0154546 / SF / MF4 Storefront: Add Account Information on Tasks 
* Mohamed Thameem               01/05/2023              User Story 2792916: T1PRJ0154546 / SF / MF9 Storefront Home Page: Visitor Interactions Search    
* Mohamed Thameem               11/20/2022              User Story 3866581: Modernizing Dashboard
* Vinoth L                      11/28/2022              User Story 4012388 T1PRJ0154546 / SF/ MF9 Storefront - Ability to View First Time Visit - Currebnt CY in Storefront 
* Vinoth L                      01/05/2023              User Story 4107848: T1PRJ0154546 / SF / MF3 Storefront: Update Visitor ID & Type Population Logic
* Mohamed Thameem      	        01/12/2023              Request 3866581 - Mobile Scanner
* Sahil Verma                   02/01/2023              User Story 4231796: T1PRJ0154546 / DP / MF9 Storefront - Ability to View Deceased Information on Member Account
* Vinoth L                      02/28/2023           	User Story 4349337: T1PRJ0154546 / SF / MF9 Storefront: Deprecate Old Home Page Components
* Mohamed Thameem      	        03/03/2023              Mobile Card Scanning - Member visitor - Phase 2
* Sahil Verma        	        02/28/2023              US-4438175 T1PRJ0154546 / SF / MF9 Storefront: Interaction Display Criteria section Enhancement on Interaction/Events tab
* Mohamed Thameem      	        03/21/2023              User Story 4407867 - Auto-close tasks for deceased member
* Vinoth L                      04/26/2023              User Story 4566290: T1PRJ0154546 / SF / MF3 Storefront: Display MyHumana Enrollment & Go365 Enrollment - Backend Logic
* Mohamed Thameem               05/12/2023              US - 4594514 Storefront: Auto-close tasks for members with missing contact info
* Vinoth L						05/31/2023				User Story 4662486: T1PRJ0154546 / SF / MF9 Storefront - Edit attendance field permissions on Event
* Vinoth L						06/24/2023				User Story 4769775: - T1PRJ0154546 / SF / MF9 Storefront - Carry Forward First Time Visitor to Event Interactions
* Mohamed Thameem      	        07/10/2023           	User Story 4807673 : T1PRJ0154546 / SF / MF9 Storefront - One-Time Update of Event Interactions
* Mohamed Thameem               07/07/2023              REQ- 4832304 First Time Visitor Report
* Vinoth L                      08/26/2023              User Story 5012060: T1PRJ0154546 / DP / MF9 Storefront - Add preferred name on Account
* Vinoth L						09/06/2023				DEFECT-8081: Scan Card No Match Error Text Verbiage Fix
* Vinoth L						09/12/2023				User Story 5084370: T1PRJ0154546 / SF / MF9 Storefront - Add preferred name on New Visitor Page
* Vinoth L						10/03/2023				User Story 5132659 Ability to Edit Mobile Location Field
* Mohamed Thameem               11/01/2023              User Story 5290960: T1PRJ0154546 / SF / MF9 Storefront [DEFECT ID # 8261] - Update Guest Card # Logic
* Vinoth L						10/21/2023				User Story 5225414: T1PRJ0154546 / SF / MF9 Storefront - Show Future Policy Members in Search Results
* Srinivasan Baskaran           30/01/2024              User Story 5668877: Placeholder Story : Moving constants to CRM_GenericHelper_H_HUM class
******************************************************************************************************************************************************/
Public Class GLOBAL_CONSTANT_STOREFRONT_HUM
{
    Public Static final String CRMRETAIL_DUPLICATES = 'Duplicates';
    Public Static final String CRMRETAIL_STATUS = 'Status';
    Public Static final String CRMRETAIL_SUCCESS = 'SUCCESS';
    Public Static final String CRMRETAIL_WAIVERDATEEXPIRATION = 'WaiverDateExpiration';
    Public Static final String CRMRETAIL_BARCODE_C_HUM = 'BarcodeController_LTNG_C_HUM';
    Public Static final String CRMRETAIL_UPDATEBARCODE = 'updateBarcodeCheckIn';
    Public Static final String CRMRETAIL_EMPTYACCOUNT_ERRMSG = 'Error updating Account. Because Account Object is empty.';
    Public Static final String CRMRETAIL_ERROR = 'Error';
    Public Static final String CRMRETAIL_FAILURE = 'FAILURE';
    Public Static final String CRMRETAIL_SAVEBARCODE = 'saveBarcode';
    Public Static final String CRMRETAIL_NEWVISITORERROR = 'Error inserting new Visitor. Because Account Object is empty.';
    Public Static final String CRMRETAIL_PROCESSCHECKIN = 'processCheckIn';
    Public Static final String CRMRETAIL_NOMATCHERROR = 'No match was found using this visitor card. Use the Search/Add to search for the visitor.'; 
    Public Static final String CRMRETAIL_NOMATCHERROR_MOBILE = 'No match was found using this visitor card.'; 
    Public Static final String CRMRETAIL_CURRENTDEVICE = 'currentdevice';
    Public Static final String CRMRETAIL_FORMFACTOR_MEDIUM = 'Medium';
    Public Static final String CRMRETAIL_FORMFACTOR_SMALL = 'Small';
    Public Static final String CRMRETAIL_REASONERROR = 'Error retrieving Interaction Reason';
    public static final String CRMRETAIL_CURRENTDATE = 'currentDate';
    public static final String CRMRETAIL_FIRSTSTIME_CHECKIN = 'Visitor Check-In (1st Time)';
    public static final String CRMRETAIL_VISITOR_CHECKIN = 'Visitor Check-In';
    public static final String CRMRETAIL_FIRST_TIME_VISITOR = 'FirstTimeVisitor'; 
    public static final String CRMRETAIL_DUPLICATEINTERACTION = 'There seems to be a duplicate Interaction record.';
    public static final String CRMRETAIL_INTPROCESSINGERROR = 'There seems to be an error in processing Interaction Reason while inserting Interaction record.';
    public static final String CRMRETAIL_SEARCHVISITOR_C_HUM = 'SearchVisitorController_LTNG_C_HUM';
    public static final String CRMRETAIL_INITIATESEARCHVISITOR = 'initiateSearchAddVisitor';
    public static final String CRMRETAIL_POLICYID = 'PolicyId';
    public static final String CRMRETAIL_FIRSTNAME = 'FirstName';
    public static final String CRMRETAIL_LASTNAME = 'LastName';
    public static final String CRMRETAIL_PERSONMAILSTATE = 'personmailingstate';
    public static final String CRMRETAIL_PERSONHOMEPHONE = 'personhomephone';
    public static final String CRMRETAIL_PROCESSMEMBERSEARCH = 'runProcessMemberSearch';
    public static final String CRMRETAIL_FETCHSTATEVALUE = 'fetchStateValue';
    public static final String CRMRETAIL_ID = 'Id';
    public static final String CRMRETAIL_NAME = 'Name';
    public static final String CRMRETAIL_ISMEMBER = 'isMember';
    public static final String CRMRETAIL_CREATEINTERACTIONS = 'createInteractions';
    public static final String CRMRETAIL_CURRENTLOCATION = 'currentLocation';
    public static final String CRMRETAIL_INTOBJCACHE = 'interactionsObjCache';
    public static final String CRMRETAIL_MULTIPLEREFRESHERROR = 'Cannot create Interaction as your Cache is reset due to multiple refreshes of browser. Please navigate to Home and proceed with your actions.';
    public static final String CRMRETAIL_VISITORID = 'visitorid';
    public static final String CRMRETAIL_VISITORTYPE = 'visitortype';
    public static final String CRMRETAIL_INTREASON = 'intReason';
    public static final String CRMRETAIL_ACCNAME = 'accName';
    public static final String CRMRETAIL_INTOBJ = 'interactionsObj';
    public static final String CRMRETAIL_INVALIDINT_ERROR = 'Error in creating Interaction due to invalid Interaction Type or Reason or Location.';
    public static final String CRMRETAIL_DATECONVERTERROR = 'Error in converting Interaction Date to a valid format.';
    public static final String CRMRETAIL_GETUCTDATETIME = 'getUTCDateTime';
    public static final String CRMRETAIL_CURRINTDATE = 'currentInteractionDate';
    public static final String CRMRETAIL_DEDUCEFIRSTTIMEVISITOR = 'deduceFirstTimeVisitor';
    public static final String CRMRETAIL_FETCHREASON_ERROR = 'Error in fetching First Time Visitor Interaction Reason.';
    public static final String CRMRETAIL_FETCHINTREASON = 'fetchInteractionReason';
    public static final String CRMRETAIL_FETCHREASON_ERROR_2 = 'Error in fetching Interaction Reason.';
    public static final String CRMRETAIL_SOCIAL = 'Social';
    public static final String CRMRETAIL_NONE = 'None';
    public static final String CRMRETAIL_LOCATION = 'location';
    public static final String CRMRETAIL_INTDATE = 'interactionDate';
    public static final String CRMRETAIL_FETCHLOCATION = 'fetchLocationValue';
    public static final String CRMRETAIL_CACHERESERT_ERROR = 'Due to refresh of your browser; your cache has been reset. Please navigate to your home page.';
    public static final String CRMRETAIL_HOMEPAGE_C_HUM = 'HomePageController_LTNG_C_HUM';
    public static final String CRMRETAIL_HOMEPAGE_LC_HUM = 'CRMRetail_HomePage_LC_HUM';
    public static final String CRMRETAIL_GETLOCATIONS = 'getLocations';
    public static final String CRMRETAIL_GETEVENTS = 'getEvents';
    public static final String CRMRETAIL_SAVEDEFLOCATION = 'saveDefaultLocationRecord';
    public static final String CRMRETAIL_COMMITINTERACTION = 'commitInteractions';
    public static final String CRMRETAIL_DEDUCELOCATIONCACHE = 'deduceLocationCache';
    public static final String CRMRETAIL_UPDATECACHE = 'updateCache';
    public static final String CRMRETAIL_GETUSERNAME = 'getUserName';
    public static final String CRMRETAIL_DEDUCEINTDATECACHE = 'deduceInteractionDateCache';
    public static final String CRMRETAIL_UPDATEINTDATECACHE = 'updateIntDateCache';
    public static final String CRMRETAIL_DISPLAYDATE = 'displayDate';
    public static final String CRMRETAIL_CREATEINT = 'createNewInteractions';
    public static final String CRMRETAIL_DELETEINT = 'deleteInteraction';
    public static final String CRMRETAIL_READINT = 'readInteractions';
    public static final String CRMRETAIL_NEWINT_ERROR= 'Error in reading newly created interactions. Please contact your System Admin.';
    public static final String CRMRETAIL_SIGNED = 'signed';
    public static final String CRMRETAIL_DECLINED = 'declined';
    public static final String CRMRETAIL_UPDASTEWAIVERDATE = 'updateWaiverDate';  
    public static final String CRMRETAIL_BUSINESSADMIN = 'CRMRetail_300_BusinessAdministrator';
    public static final String CRMRETAIL_COORDINATOR = 'CRMRetail_100_Coordinator';
    public static final String CRMRETAIL_400SUPERADMIN = 'CRMRetail_400_Super_Admin';
    Public Static final List<String> CRMRETAIL_SWITCH_LIST = New List<String>{'801805','942642','1243060','1243111','1243103','1396838', '1474878', '1418677','1418700', '1418680', '1586810','1694784','2194098','2360057','2578377','2792824','2789307','2792911','2789917','2793063','2940968','3201663','3075114','3434998','3248244','3316885','3117495','5132659'};
    public static final String CRMRETAIL_FIRSTTIME_CHECKIN_REASON ='firtstTimeCheckinReason';
    public static final String CRMRETAIL_VIRTUAL_VISITOR_ID ='Virtual Visitor';
    public static final String CRMRETAIL_CATEGORY_SUPPORT = 'Support';
    public static final String CRMRETAIL_CATEGORY_VIRTUAL = 'Virtual';
    public static final String CRMRETAIL_CATEGORY_SCH_VIRTUAL = 'Scheduled Virtual';
    public static final String CRMRETAIL_CATEGORY_SCH_ONSITE = 'Scheduled Onsite';
    public static final String CRMRETAIL_RETAIL_VISITOR = 'CRMRetail Visitor';
    public static final String CRMRETAIL_MEMBER = 'Member';
    public static final String CRMRETAIL_NON_MEMBER = 'Non-Member';
    Public Static final String CRMRETAIL_ERROR_DELETE_INTERACTION_EXIST ='Deleting an event is not allowed when Interactions already exist.';
    Public Static final String CRMRETAIL_ERROR_UPDATE_INTERACTION_EXIST ='Event Calendar Details cannot be changed when Interactions already exist';
    Public Static final String CRMRETAIL_ERROR_INSERT_DUPLICATE ='This event already exists in the Calendar.';
    public static final String CRMRETAIL_POLICY_PRODUCT_TYPE = 'MED';
    public static final String CRMRETAIL_MEMBER_TYPE = 'Member-Id-Base';
    public static final String CRMRETAIL_SSN_TYPE = 'SSN';
    public static final String CRMRETAIL_WAIVER_DATE='WaiverDate';
    public static final String CRMRETAIL_LOCATION_NAME_AND_INTER_DATE='LocationAndInterDate';
    public static final String CRMRETAIL_CATEGORY='Category';
    public static final String CRMRETAIL_WAIVER_REQUIRED='Waiver Required';
    public static final String CRMRETAIL_FIRSTTIME_VISITOR='First Time Visitor';    
    Public Static final Set<String> CRMRETAIL_INTERACTION_NOTIFICATION_FIELDS = New Set<String>{'Aging In','New Member', 'SDoH', 'Veteran','Waiver Required'};
    public static final String CRMRETAIL_NOTIFICATION='Notification';
    public static final String CRMRETAIL_AGING_IN = 'Aging In';
    public static final String CRMRETAIL_VETERAN = 'Veteran';
    public static final String CRMRETAIL_SDoH_SCREENING ='SDoH Screening';
    public static final String CRMRETAIL_NEW_MEMBER = 'New Member';	
    public static final String CRMRETAIL_WAIVERDATE_KEY = 'waiverDate';
    public static final String CRMRETAIL_AGING_IN_KEY = 'agingIn'; 
    public static final String CRMRETAIL_VETERAN_KEY = 'veteran';
    public static final String CRMRETAIL_SDoH_SCREENING_KEY ='sDoHScreening';
    public static final String CRMRETAIL_NEW_MEMBER_KEY = 'newMember'; 
    public static final String CRMRETAIL_VETERAN_STATUS = 'VeteranStatus'; 
    public static final String CRMRETAIL_NOTIFICATION_OPT_OUT = 'NotificationOptOut';
    public static final String CRMRETAIL_CONFIRMED_VETERAN = 'confirmed-veteran';
    public static final String CRMRETAIL_IDENTIFIED_VETERAN = 'identified-veteran';
    public static final String CRMRETAIL_RESET_NOTIFY = 'Reset Notification';
    public static final String CRMRETAIL_ONE_TIME_NOTIFY = 'One Time Notification';
    public static final String CRMRETAIL_FOLLOW_UP_RECURRING_NOTIFY = 'Follow-Up Recurring Notification';
    public static final String CRMRETAIL_SUPPRESS_RECURRING_NOTIFY = 'Suppress Recurring Notification';
    public static final String CRMRETAIL_NO_MATCHING_NOTIFICATION_CONDITION = 'No Matching Condition';     
    public static final String CRMRETAIL_NEWMEMBER = 'New Member';
    Public Static final Set<String> CRMRETAIL_LIST_OF_PRODUCT_TYPES = New Set<String>{'MA','MAPD','PDP'};
    Public static final Decimal CRMRETAIL_AGE_CALCULATION = 12.0;
	Public Static final Integer INT_CRMRETAIL_MEMBER_SEARCH_LIMIT = 101;
    Public Static final Integer INT_CRMRETAIL_PERSONPHONE_SEARCH_LIMIT = 100;
    Public Static final String STRING_CRMRETAIL_OTHER='other';
    Public Static final String STRING_CRMRETAIL_POLICY_NAME_STARTS_WITH='H';
    public Static final String STRING_CRMRETAIL_UNKNOWN='Unknown';
    public Static final String STRING_CRMRETAIL_ACCOUNT='Account';
    Public Static final String STRING_CRMRETAIL_ERROR_MSG = 'Request was invalid.';
    Public Static final String STRING_CRMRETAIL_GENDER = 'Undisclosed'; 
    Public Static final Integer INT_CRMRETAIL_STATUSCODE = 200;
	Public Static final String STRING_CRMRETAIL_CONTENTTYPE = 'Content-Type';
	Public Static final String STRING_CRMRETAIL_APPLICATION_JSON = 'application/json';
    Public Static final String STRING_CRMRETAIL_ACTIVE = 'Active';
    Public Static final String STRING_CRMRETAIL_EQUERY = 'Select Id,FirstName,LastName,RecordType.Name,Birthdate__c, PersonEmail, GCM_Visitor_Barcode__c, PersonMailingPostalCode from Account';
    Public Static final String STRING_CRMRETAIL_GENERIC_ERROR_MESSAGE = 'Required fields missing';  
    Public Static final String STRING_CRMRETAIL_GCM_VISITOR_BARCODE = 'VNC';
    Public Static final String STRING_CRMRETAIL_NO_MATCHING_ACCOUNT = 'No Matching Account Found';
    Public Static final String STRING_CRMRETAIL_MEMERROR_MSG = 'Member Accounts cannot be updated with this request.';
    Public Static final String STRING_CRMRETAIL_GENDER_MALE = 'Male';
	Public Static final String STRING_CRMRETAIL_GENDER_FEMALE = 'Female';
    Public Static final String STRING_REQUIRED_PERMISSION_SET = 'Required Permission Set';
    Public Static final String STRING_CRMRETAIL_NO_EVENT = 'Event not found';
    Public Static final Integer STRING_CRMRETAIL_DAILY_MAXLIMIT=100;
    Public Static final Integer STRING_CRMRETAIL_WEEKLY_MAXLIMIT=53;
    Public Static final Integer STRING_CRMRETAIL_MONTHLY_MAXLIMIT=59;
    Public Static final String STRING_CRMRETAIL_NO_LOCATION = 'Given location not found';
    Public Static final String STRING_CRMRETAIL_NO_EVENTCREATED = 'No event created as per the given inputs.';
    Public Static final String STRING_CRMRETAIL_EVENTCREATION_FAILED = 'Something went wrong,Events creation failed.';
    Public Static final String STRING_CRMRETAIL_EVENT_END_DATE_ERROR='Event end date cannot be less than Event start date';
    Public Static final String STRING_CRMRETAIL_FREQUENCY_ERROR='REQUIRED FIELD MISSING: Frequency';
    Public Static final String STRING_CRMRETAIL_FREQUENCY_INTERVAL_ERROR='REQUIRED FIELD MISSING: frequecyInterval for DAILY recuuring events';
    Public Static final String STRING_CRMRETAIL_REPEAT_WEEKDAYS_ERROR='REQUIRED FIELD MISSING: repeatWeekDays attribute is missing for WEEKLY recurring event.';
    Public Static final String STRING_CRMRETAIL_ALLWEEKDAYS_ERROR='Since all week days are specified, Please use the Frequency type as DAILY with frequencyInterval as 1';
    Public Static final String STRING_CRMRETAIL_ATLEAST_ONEWEEKDAY_ERROR='Atleast one week day value need to be specified';
    Public Static final String STRING_CRMRETAIL_REPEAT_MONTHDATES_ERROR='REQUIRED FIELD MISSING: repeatMonthDates for MONTHLY recuuring events';
    Public Static final String STRING_CRMRETAIL_ALLMONTHDAYS_ERROR='RepeatMonthDates contains more than the maximum allowed limit. It can contain integer values representing a maximum of 31 days';
    Public Static final String STRING_CRMRETAIL_ATLEAST_ONEMONTHDAY_ERROR='At least one day of the month needs to be specified.';
    Public Static final String STRING_CRMRETAIL_REPEATS_ENDDATE_ERROR='REQUIRED FIELD MISSING: numberOfRepeats Or repeatEndDate required';
    Public Static final String STRING_CRMRETAIL_DAILY_LIMIT_ERROR='REQUIRED FIELD MISSING: numberOfRepeats should be more than 0 and less than '+STRING_CRMRETAIL_DAILY_MAXLIMIT+' for Daily frequency';
    Public Static final String STRING_CRMRETAIL_WEEKLY_LIMIT_ERROR='REQUIRED FIELD MISSING: numberOfRepeats should be more than 0 and less than '+STRING_CRMRETAIL_WEEKLY_MAXLIMIT+' for Weekly frequency';
    Public Static final String STRING_CRMRETAIL_MONTHLY_LIMIT_ERROR='REQUIRED FIELD MISSING: numberOfRepeats should be more than 0 and less than '+STRING_CRMRETAIL_MONTHLY_MAXLIMIT+' for Monthly frequency';
    Public Static final String STRING_CRMRETAIL_REPEAT_END_DATE_ERROR='Repeat End Date must be greater that event start date';
    Public Static final String STRING_CRMRETAIL_REPEAT_END_DATE_LIMIT_ERROR='Repeat End Date cannot be more than 2 years from event start date';
    Public Static final String STRING_CRMRETAIL_DAILY='DAILY';
    Public Static final String STRING_CRMRETAIL_WEEKLY='WEEKLY';
    Public Static final String STRING_CRMRETAIL_MONTHLY='MONTHLY';
    Public Static final String STRING_CRMRETAIL_SUN='SUN';
    Public Static final String STRING_CRMRETAIL_MON='MON';
    Public Static final String STRING_CRMRETAIL_TUE='TUE';
    Public Static final String STRING_CRMRETAIL_WED='WED';
    Public Static final String STRING_CRMRETAIL_THU='THU';
    Public Static final String STRING_CRMRETAIL_FRI='FRI';
    Public Static final String STRING_CRMRETAIL_SAT='SAT';
    Public Static final String STRING_CRMRETAIL_VALID='Valid';
    Public Static final String STRING_CRMRETAIL_NO_INT_FOUND='No active Interaction Reason found with Id : ';
    Public Static final String STRING_CRMRETAIL_NO_LOC_FOUND='No active location found having source system : ';
    Public Static final String STRING_CRMRETAIL_LOC_METADATA='CRMRetail_DefLoc__mdt';
    Public Static final String STRING_CRMRETAIL_NO_LOC_NAME_FOUND='No active location found with name : ';
    Public Static final String STRING_CRMRETAIL_INVALID_WEEKDAY='Provided Weekday Value is not valid : ';
    Public Static final String STRING_CRMRETAIL_INVALID_MONTHDAY='Provided Monthday Value is not valid : ';
    Public Static final Integer STRING_CRMRETAIL_MINLIMIT=1;
    Public Static final String STRING_CRMRETAIL_EVENT_REC_TYPE_DEV_NAME = 'CRMRetail_Event';
    Public Static final String STRING_CRMRETAIL_EVENT_SUBJECT = 'Event Detail';
    Public Static final String STRING_CRMRETAIL_CATEGORY_ONSITE = 'Onsite';  
    Public Static final String STRING_CRMRETAIL_CATEGORY_ERROR = 'Valid values for Category could be : Virtual OR Onsite';
    Public Static final String STRING_CRMRETAIL_DUPLICATE_ERROR = 'Duplicate Scheduled Interaction Exists';
    Public Static final String STRING_CRMRETAIL_GENERAL_ERROR = 'Something went wrong - ';
    Public Static final String STRING_CRMRETAIL_CREATECHECK_IN = 'createCheckInInteraction';
    Public Static final String STRING_CRMRETAIL_CREATE_FIRST_CHECK_IN = 'create1stCheckIn';
    Public Static final String STRING_CRMRETAIL_CREATE_EVENT_INT = 'createEvntInt'; 
    Public Static final String STRING_CRMRETAIL_NO_INTERACTION_FOUND = 'No Interaction Found for the Interaction Id : '; 
    Public Static final String STRING_CRMRETAIL_CATEGORY_RECORDED = 'Recorded'; 
    Public Static final String STRING_CRMRETAIL_ALREADY_ATTENDED = 'This account is already assigned to '; 
    Public Static final String STRING_CRMRETAIL_INTERACTION_MISSING = 'Interaction Id is missing';
    Public Static final String STRING_CRMRETAIL_REASON_MISSING = 'Reason Id is missing';
    Public Static final String STRING_CRMRETAIL_ACCOUNT_MISSING = 'Account Id is missing';
    Public Static final String STRING_INTERACTION_ID_NOT_FOUND = 'Interaction ID is not found';     
    Public Static final String STRING_CRMRETAIL_CHECK_IN = 'CheckIn'; 
    Public Static final String STRING_CRMRETAIL_EXCEPTION_END = ': [';  
    Public Static final String STRING_CRMRETAIL_CUSTOM_VALIDATION = 'CUSTOM_VALIDATION_EXCEPTION, ';
    Public Static final String STRING_CRMRETAIL_SDoH = 'SDoH';
    Public Static final Integer INTEGER_HTTP_ACCEPTED_HUM = 202;
    Public Static final String STRING_VOB_FAULT_CODE = 'faultcode';
    Public Static final String STRING_LIKELY = 'Likely';
    Public Static final String STRING_VERY_LIKELY = 'Very Likely';
    Public Static final String STRING_SDOH_CUSTOM_SETTING = 'CRMRetail_SDoH';
    Public Static final String STRING_CRMRETAIL_HEALTH_EDUCATOR_REASON = 'Health Educator';
    Public static final String STRING_CRMRETAIL_SERVICE_NOT_AVAILABLE = 'Service Not Available';
    Public Static final String STRING_LOCATION_EDIT_MSG = 'You are not authorized to edit Locations. Please contact a Super Administrator.';
    Public Static final String STRING_LOCATION_DEL_MSG = 'You are not authorized to delete Locations. Please contact a Super Administrator.';
	Public Static final String STRING_VIRTUAL_LOC_INELIGIBILITY_MSG = 'You are only allowed to view information for this virtual location.';
    Public Static final String STRING_VIRTUAL_LOC_ELIGIBILITY_CUSTOM_PERM_NAME = 'CRM_Retail_Virtual_Location_Access';  
    Public Static final String STRING_CRMRETAIL_INVALID_INTERACTION_ID = 'Attendance can be marked only for scheduled interactions.';
    public static final Set<String> SET_CRMRETAIL_SDoH_DETERMINANTS = New Set<String>{'Financial Strain','Food Insecurity','Housing Insecurity','Loneliness','Transportation'};
    public static final String STRING_CRMRETAIL_HNC = 'Humana Neighborhood Center';
    public static final String STRING_CRMRETAIL_HNC_SHORT = 'HNC';
    public static final Integer STRING_CRMRETAIL_HNC_TRIMMED_MAX_LENGTH =  12;
    public static final String STRING_CRMRETAIL_VERY_UNLIKELY = 'Very Unlikely';
    public static final String STRING_CRMRETAIL_RATING_FOR_CHOSEN_LIST = '5';
    public static final String STRING_CRMRETAIL_RATING_FOR_AVAILABLE_LIST = '1';
    public static final String STRING_CRMRETAIL_UPDATE_EVENT_TYPE = 'UPDATE';
    public static final String STRING_CRMRETAIL_VALUE = 'value';
    public static final String STRING_CRMRETAIL_DHNA_CREATE = 'CRMRetail_SDoH_Create';
    public static final String STRING_CRMRETAIL_DHNA_UPDATE = 'CRMRetail_SDoH_Update';
    public static final String STRING_CRMRETAIL_DHNA_DELETE = 'CRMRetail_SDoH_Delete';
    public static final String STRING_CRMRETAIL_API_FAILURE_RESPONSE = 'FAILED';
    public static final String STRING_CRMRETAIL_API_CONFLICT_RESPONSE = 'CONFLICT';    
    public static final String STRING_CRMRETAIL_API_NOT_FOUND_RESPONSE = 'NOT_FOUND';    
    Public Static final Integer INTEGER_HTTP_STATUS_OK = 201; 
    public static final String STRING_CRMRETAIL_API_NOT_FOUND_STATUS = 'Not Found';
    public static final String STRING_CRMRETAIL_ENTERPRISE_ID_KEY = 'EnterpriseId';    
	public static final String STRING_CRMRETAIL_DELETE_EVENT_TYPE = 'DELETE';
    public static final String STRING_CRMRETAIL_INSERT_EVENT_TYPE = 'INSERT';
    public static final String STRING_CRMRETAIL_INSUFFICIENT_ACCESS = 'User doesn\'t have required access to ';
    public static final String STRING_CRMRETAIL_INSUFFICIENTFIELD_ACCESS = 'following fields of ';
    public static final String STRING_CRMRETAIL_SDoH_HELPER_CLASSNAME = 'CRMRetail_SDoHHelper_H_HUM';
    public static final String STRING_CRMRETAIL_SDoH_HELPER_METHODNAME = 'CRMRetail_SDoHHelper_H_HUM.getSDoHData';
    public static final String STRING_CRMRETAIL_ERROR_KEY = 'error';
    public static final String STRING_CRMRETAIL_MESSAGE_KEY = 'message';
    public static final String STRING_CRMRETAIL_SDRPERSONID = 'sdrPersonId';
    public static final String STRING_CRMRETAIL_JSON_EXCEPTION_KEY = 'Error in De-Serializing the Response'; 
    public static final String STRING_CRMRETAIL_ATTENDANCE = 'Attendance__c';   
    public static final String STRING_LIGHTNING_URL_TASK_ID = '/lightning/r/Task/';
    public static final String STRING_BACKGROUND_URL = '/edit?nooverride=1&backgroundContext=%2Flightning%2Fr%2FTask%2F';
	public static final String STRING_END_URL = '%2Fview';
    public static final String STRING_NO_OVERRIDE = 'nooverride';
    public static final STRING STRING_CURRENT_PAGE_PARAM = '1';
    public static final String STRING_CRM_RETAIL_TASK = 'CRMRetail Task';  
    public static final String STRING_CRMRETAIL_TASK_CONTACTED_CLASS = 'CRMRetail_Contacted';
    public static final String STRING_CRMRETAIL_TASK_CONTACTED_METHODNAME = 'CRMRetail_Contacted.getInteractionInfo';
    public static final String STRING_CRMRETAIL_TASK_COMPLETED_STATUS= 'Completed';
    public static final String STRING_CRMRETAIL_TASK_WARNING_KEY= 'isWarningScenario';
    public static final String STRING_CRMRETAIL_TASK_ERROR_KEY = 'isErrorScenario'; 
    public static final String STRING_CRMRETAIL_TASK_NOTIFICATIONDATA_KEY='NotificationData';
    public static final String STRING_CRMRETAIL_TASK_COMPLETED_REASON = 'Contacted Member';
    public static final String STRING_CRMRETAIL_TASK_ISREQUIRED = 'Required Fields(Related To/Location/Status) are Missing in Task. Please add all the required values and try again';
    public static final String STRING_CRMRETAIL_TASK_INSERTINTERACTIONS = 'CRMRetail_Contacted.insertInteractions';
    public static final String STRING_CRMRETAIL_TASK = 'Task';
    public static final String STRING_CRMRETAIL_TASK_COMPLETED_MESSAGE = 'This task has already been completed.';
    public static final String STRING_CRMRETAIL_TASK_INACTIVE_MESSAGE = 'Interaction Reason is inactive, please use a valid/active reason value';
    public static final String STRING_CRMRETAIL_TASK_ALREADY ='already';
    public static final String STRING_CRMRETAIL_TASK_ACCOUNTID='AccountId';
    public static final String STRING_CRMRETAIL_TASK_IMPORT_CLASS_NAME = 'CRMRetail_Task_Import_Helper_HUM';
    public static final String STRING_CRMRETAIL_TASK_IMPORT_BUTTON_VISIBLE='IsTaskImportButtonVisible';    
    public static final String STRING_CRMRETAIL_TASKBUTTON = 'isShowTaskImportBtn';
    public static final String STRING_CRMRETAIL_SWITCH_2578377 = 'Switch_2578377';
    public static final String STRING_CRMRETAIL_FUTURE_STATUS = 'Future';
    public static final Set<String> SET_CRMRETAIL_PRIORITY = New Set<String>{'HIGH','NORMAL','LOW'};
    public static final String STRING_CRMRETAIL_MEMBERID = 'memberid';
    public static final String STRING_CRMRETAIL_ASSIGNEDTO = 'assignedto';
	public static final String STRING_CRMRETAIL_LOCATION = 'location';
    public static final String STRING_CRMRETAIL_SUBJECT = 'subject';        
    public static final String STRING_CRMRETAIL_INVALID_MEMBERID = 'Invalid Member ID;';
    public static final String STRING_CRMRETAIL_INVALID_LOCATION = 'Invalid Location;';
    public static final String STRING_CRMRETAIL_INVALID_ASSIGNEDTO = 'Invalid Assigned to User;';
    public static final String STRING_CRMRETAIL_INVALID_SUBJECT = 'Invalid Subject;';
    public static final String STRING_CRMRETAIL_INVALID_DUEDATE = 'Invalid Due Date;';
    public static final String STRING_CRMRETAIL_INVALID_PRIORITY = 'Invalid Priority;';    
    public static final String STRING_CRMRETAIL_DUPLICATE_TASK = 'Duplicate Task';
    public static final String STRING_CRMRETAIL_VALIDCOUNT = 'ValidCount';
    public static final String STRING_CRMRETAIL_INVLAIDRECORD = 'InvalidRecord';
    public static final String STRING_CRMRETAIL_DUPLICATE_KEY ='Duplicate';
    public static final String STRING_CRMRETAIL_NON_DUPLICATE_KEY = 'Non-Duplicate';
    public static final String STRING_CRMRETAIL_TASK_VALIDATEMETHOD = 'validateTaskFieldsOnImport';
    public static final String STRING_CRMRETAIL_TASK_INSERTMETHOD = 'insertTaskRecords';
    public static final String STRING_CRMRETAIL_TASK_QUERYMETHOD = 'queryAllRecords';
    public static final Set<String> SET_CRMRETAIL_PERMISSION_SETS = New Set<String>{'CRMRetail_300_BusinessAdministrator','CRMRetail_100_Coordinator','CRMRetail_400_Super_Admin','CRMRetail_200_ServiceAssociate','CRMRetail_000_ProfileUser'};
    public static final List<String> LIST_STRING_STATUSLIST = new List<String>{'\'Active\'','\'Future\''};   
    public static final Integer INTEGER_LIMIT_200 = 200;
    public static final String STRING_OR = ' OR ';
    public static final String STRING_CRMRETAIL_GO365_ELIGIBLE = 'go365EligibleChecks';
    public static final String STRING_CRMRETAIL_INTERACTION_HANDLER = 'Storefront_Interaction_H_HUM';
    public static final String STRING_CRMRETAIL_ACCOUNT_ENGAGEMENT = 'updateInteractionEngagement';    
    public static final String STRING_CRMRETAIL_RELATED_INTERACTIONS = 'deleteRelatedInteractions';
    public static final String STRING_CRMRETAIL_AFTER_INSERT = 'processAfterInsert';
    public static final String STRING_CRMRETAIL_AFTER_DELETE = 'processAfterDelete';
    public static final String STRING_CRMRETAIL_PROCESS_MISSING_EVENT = 'processMissingEvent';
    public static final String STRING_CRMRETAIL_TASKMASSDELETE = 'CRMRetail_TaskMassDelete_H_HUM';
    public static final String STRING_CRMRETAIL_GETTASK = 'processTaskRecords';
    public static final String STRING_CRMRETAIL_INVALIDTASKRECORDTYPE = 'Mass delete functionality is limited to CRM Retail Task records.';
    public static final String STRING_CRMRETAIL_INVALIDTASKOWNERSHIP = 'Mass delete functionality is limited to records that you created.';
    public static final String STRING_CRMRETAIL_INVALIDTASKSTATUS = 'Mass delete functionality is limited to records where Status = \'Not Started\'.';
    public static final String STRING_CRMRETAIL_TASKDMLERROR = 'Unexpected error occurred while attempting to Mass Delete. Please try again.';
    public static final String STRING_CRMRETAIL_TASKSUCCESSTEXT = ' task records were successfully deleted.';
    public static final String STRING_CRMRETAIL_TASKNSSTATUS = 'Not Started';
    public static final String STRING_CRMRETAIL_TASKRECORDTYPENAME = 'CRMRetail_Task';
    Public static final String STRING_CRMRETAIL_REASONTOVISIT = 'BMI Screening';
    public static List<String> lstServiceAssReports = new List<String>{'No_Of_Scheduled_Interactions','No_Of_CheckIns','Weekly_Event_Attendance'};
    public static List<String> lstAdminReports = new List<String>{'No_Of_Member_Interactions_By_Location','No_Of_NonMember_Interactions_By_Location','Notifications_By_Visitor_Type','Interactions_wNew_Vs_Existing_Members','First_Time_Visitor_Referrals','No_Of_Health_Educator_1x1_Tracking','Event_RSVP_Attendance'} ;   
    public static List<String> lstServiceAssViewReports = new List<String>{'No_Of_Scheduled_Interactions_View','No_Of_CheckIns_View','Weekly_Event_Attendance_View','Weekly_Frequent_Visitors_View'};
    public static List<String> lstAdminViewReports = new List<String>{'No_Of_Member_Interactions_By_Location_View','No_Of_NonMember_Interactions_By_Location_View','Notifications_By_Visitor_Type_View','Interactions_wNew_Vs_Existing_Members_View','First_Time_Visitor_Referrals_View','No_Of_Health_Educator_1x1_Tracking_View','Event_RSVP_Attendance_View','Completed_Tasks_Contact_Attempts_View','FirstTime_Interaction_Reason_by_Location_View'} ;   
    Public static String STRING_ADMINREPORTFILTER = '{column:\'Storefront_Interaction__c.Location__c\',operator:\'equals\',value:\'';
    Public Static String STRING_JSONENDSTRING = '\'}';
    Public Static String STRING_HEALTHEDUCATORFILTER = '{column:\'CRMRetail_OOO_Tracking__c.CRM_Retail_Location__c.Name\',operator:\'equals\',value:\'';
    Public Static String STRING_SERVICEASSREPORTFILTER = '[{column:\'Storefront_Interaction__c.Location__c.Name__lookup\',operator:\'equals\',value:\'';
    Public Static String STRING_CUSTOMDASHBOARDCLASSNAME = 'CRM_Retail_Custom_Dashboard_C_HUM';
    Public Static String STRING_FETCHREPORTIDS = 'fetchReportIds';
    Public Static String STRING_UNDEFINED = 'Undefined';
    Public Static String STRING_SETREPORTFILTER = 'setReportFilter';
    Public Static String STRING_FETCHVISITORSANDTASKS = 'fetchVisitorsAndTasks';
    Public Static String STRING_LOUISVILLE = 'Louisville';
    Public Static String STRING_GETHOMELOCATION = 'getHomeLocation';
    Public Static String STRING_GETLOCATIONS = 'getLocations';
    Public Static String STRING_TASKCOUNT = 'TaskCount';
    Public Static String STRING_CONTACTATTEMPTS = 'ContactAttempts';
    public static final String STRING_LASTMONTH='Last Month';
    public static final String STRING_ADMINREPORTFILTER_1 = ',{column:\'Storefront_Interaction__c.Interaction_Date__c\',operator:\'greaterOrEqual\',value:\'';
    public static final String STRING_ADMINREPORTFILTER_2 = '\'},{column:\'Storefront_Interaction__c.Interaction_Date__c\',operator:\'lessOrEqual\',value:\'';
    public static final String STRING_SERVICEASSREPORTFILTER_1 = ',{column:\'Storefront_Interaction__c.Interaction_Date__c\',operator:\'equals\',value:\'TODAY\'}]';
    public static final String STRING_HEALTHEDUCATORFILTER_1 = ',{column:\'CRMRetail_OOO_Tracking__c.CreatedDate\',operator:\'greaterOrEqual\',value:\'';
    public static final String STRING_HEALTHEDUCATORFILTER_2 = '\'},{column:\'CRMRetail_OOO_Tracking__c.CreatedDate\',operator:\'lessOrEqual\',value:\'';
    public static final String STRING_ADMINREPORTFILTER_DEFAULT = '\'},{column:\'Storefront_Interaction__c.Interaction_Date__c\',operator:\'equals\',value:\'';
    Public Static String STRING_CRMRETAILVISITORTYPE = 'CRM_Retail_Interaction_Visitor_Type__c';
    Public Static String STRING_ACCOUNTFIELD = 'Account__c';
    Public Static String STRING_NONMEMBER = 'NonMember';
    Public Static String STRING_COMPLETEDTASKS = 'completedTasksAndContactAttempts';
    Public Static String STRING_RETRIEVEWEEKLYVISITORS = 'retrieveWeeklyFrequentVisitors';
    Public Static String STRING_CALLACTIONMETHOD = 'callActionMethod';
    public static final String STRING_CRMRETAIL_200_SERVICEASSOCIATE = 'CRMRetail_200_ServiceAssociate';
    public static final String STRING_CRMRETAIL_NORECORDFOUND='No Record Found';
    public static final String STRING_FETCHNONMEMEBRACC='fetchNonMemberAccOfInactiveMember';
    public static final String STRING_PROCESSINACTIVEMEMBERCHECKIN='processInactiveMemberCheckin';
    public static final String STRING_TERMED='Termed';
    public static final String STRING_INVALIDENDDATE='12/31/9999';
    public static final String STRING_EXPIRED_MEM_KEY='expiredMemId';
    public static final String STRING_CRMRETAIL_CREATEDUMMYRES = '{"isSuccess":true}';
    public static final String STRING_END_DATE='End_Date';
    public static final String STRING_EFFECTIVE_DATE='Effective_Date';
    public static final String STRING_RSOMEDICARE = 'HUMRSOMedicare';
    public static final String STRING_LV = 'LV';
    public static final String STRING_PARSESERVICERESPONSE = 'parseServiceResponse';
    public static final String STRING_SDOHSUCCESS = 'SDoHSuccess';
    public static final String STRING_SDOHERROR = 'SDoHError';
    public static final String STRING_ENGAGEMENTSUCESS = 'EngagementIndexSuccess';
    public static final String STRING_ENGAGEMENTERROR = 'EngagementIndexError';
    public static final String STRING_MEDICAREMEMBERINFOSUCCESS = 'MedicareMemberInfoSuccess';
    public static final String STRING_MEDICAREMEMEBERINFOERROR = 'MedicareMemberInfoError';
    public static final String STRING_DATEFORMAT = 'MM/dd/YYYY';
    public static final String STRING_Y = 'Y';
    public static final String STRING_N = 'N';
    public static final String STRING_GETALLDATA = 'CRMRetail_GetAllData';
    public static final String STRING_CLASSNAME = 'CRMRetail_SDoH_Helper_HUM';
    public static final String STRING_0 = '0';
    public static final String CRMRETAIL_CUSTOMNOTIF_HUM='CRMRetail_Custom_Notification_C_HUM';
    public static final String CRMRETAIL_GETINDICATOR='getVisitorIndicators';
    public static final String STRING_CRMRETAIL_INDICATOR_KEY = 'visitorIndicator';
    public static final String STRING_CRMRETAIL_SNP_DE = 'DE';
    public static final String STRING_CRMRETAIL_SNP_CC = 'CC';
    public static final String STRING_CRMRETAIL_ATTENDANCE_SWITCH = 'Attendance';
    public static final String STRING_CRMRETAIL_PROCESS_DUPLICATE = 'processDuplicateInteractions';
    public static final String STRING_CRMRETAIL_ADMIN_CUSTOM_PERMISSION='CRM_Retail_Admin';
    public static final String STRING_CRMRETAIL_ALREADY_ASSIGNED_TEXT=' is already assigned to ';
    public static final String CRMRETAIL_STRING_VISITOR_BARCODE_HUM='GC';  
    public static final String CRMRETAIL_STRING_CHECKIN_TYPE = 'checkin';
    public static final String CRMRETAIL_STRING_ISMAXREACHED = 'isMaxReached';
    public static final String CRMRETAIL_STRING_ISSOFTDELETED = 'isSoftDeleted';
    public static final String CRMRETAIL_STRING_INACTIVEMEMBERVISITORACCOUNT ='InactiveMemberVisitorAccount';
    public static final String CRMRETAIL_STRING_INACTIVEMEMBER ='InactiveMember';
    public static final String CRMRETAIL_STRING_INCREASE_TEXT = 'Increase';
    public static final String CRMRETAIL_STRING_REDUCE_TEXT = 'Reduce';
    public static final String CRMRETAIL_STRING_DEFAULTLOCATION = 'defaultLocation';
    public static final String CRMRETAIL_STRING_DEFAULTDATE = 'defaultIntDate';
    public static final String CRMRETAIL_STRING_CURRINTDATE = 'currIntDate';
    public static final String CRMRETAIL_STRING_LIST_OF_LOCATIONS = 'listoflocations';
    public static final String CRMRETAIL_STRING_CURR_ATT = 'currAtt';
    public static final String CRMRETAIL_STRING_INTREASONNAME = 'intReasonName';
    public static final String CRMRETAIL_STRING_ISSUCCESS ='isSuccess';
    public static final String CRMRETAIL_STRING_NOTIFICATION_RECORDS = 'NotificationsRecords';
    public static final String CRMRETAIL_STRING_CHECK_IN = 'Check-in';
    public static final String CRMRETAIL_STRING_VISITOR_ID = 'visitorId';
    public static final String CRMRETAIL_STRING_ACC_ID = 'accId';
    public static final String CRMRETAIL_STRING_VISITOR_TYPE = 'visitorType';
    public static final String CRMRETAIL_STRING_SWITH_ATTENDANCE_KEY = 'Switch_Attendance';
    Public Static final List<String> CRMRETAIL_MODERNIZEDHOME_SWITCH_LIST = New List<String>{'Attendance','5225414', '3516893','3551183','3510484','3581472','3850860','3782843','3759560','2792916','4012388','4231796','3316885','3434998','4231928','4438175','4647349','4695921','4769775','5012149','5084370','5012060','5132659','5290960'};
    public static final String CRMRETAIL_STRING_MODERNIZED_HOMEPAGE_CONTROLLER = 'CRMRetail_HomePage_LC_HUM';
    public static final String CRMRETAIL_STRING_MODERNIZED_HOMEPAGE_INTERACTIONSOBJ = 'CRMRetail_HomePage_LC_HUM.interactionsObj';
    public static final String CRMRETAIL_STRING_MODERNIZED_HOMEPAGE_FETCHACCOUNTDETAILS_METHOD = 'fetchAccountDetails';
    public static final String CRMRETAIL_STRING_MODERNIZED_VISITORINFO_CONTROLLER = 'CRMRetail_VisitorInformation_LC_HUM';
	public static final String CRMRETAIL_STRING_MODERNIZED_VISITORINFO_NEWVIRTUALVISITOR_METHOD = 'handleNewVirtualVisitor';
	public static final String CRMRETAIL_STRING_MODERNIZED_VISITORINFO_NEWVIRTUALVISITORINTERACTION_METHOD = 'newVirtualVisitorInteraction';
    public static final String CRMRETAIL_STRING_MODERNIZED_LOGUIEXCEPTION_METHOD = 'logUIException';
    public static final String CRMRETAIL_STRING_NOTIFICATIONOPTOUT = 'CRMRetailNotificationOptOut__c';
    public static final String CRMRETAIL_STRING_SELECTED_LOCATION = 'SelectedLocation';
    public static final String CRMRETAIL_STRING_SELECTED_DATE = 'SelectedDate';
    public static final String CRMRETAIL_STRING_CHECKIN_INT = 'CheckInInteractions';
    public static final String CRMRETAIL_STRING_ONSITE_INT = 'OnsiteInteractions';
    public static final String CRMRETAIL_STRING_CHECKIN_COUNT = 'CheckinCount';
    public static final String CRMRETAIL_STRING_ONSITE_COUNT = 'OnsiteCount';
    public static final String CRMRETAIL_STRING_VIRTUAL_COUNT = 'VirtualCount';
    public static final String CRMRETAIL_STRING_VIRTUAL_INT = 'VirtualInteractions';
    public static final String CRMRETAIL_STRING_NOTIF_RECORD = 'NotificationsRecord';
    public static final String CRMRETAIL_STRING_ELIGIBLE_FOR_BELL_ICON = 'accIdEligibleForBellIcon';
    public static final String CRMRETAIL_STRING_DEDUCE_HE_ASSOCIATION = 'deduceHEAssociation';
    public static final String CRMRETAIL_STRING_CREATE_SCH_INTERACTION = 'createScheduledInteraction';
    public static final String CRMRETAIL_STRING_NEW = 'new';
    public static final String CRMRETAIL_STRING_SCH_ONSITE = 'scheduledOnsite';
    public static final String CRMRETAIL_STRING_SCH_VIRTUAL = 'scheduledVirtual';
    public static final String CRMRETAIL_STRING_ISNEWEVTBUTTONVISIBLE = 'isNewEvtButtonVisible';
    public static final String CRMRETAIL_STRING_ENDTIME = 'endTime';
    public static final String CRMRETAIL_STRING_STARTTIME = 'startTime';
    public static final String CRMRETAIL_STRING_IS_ELIGIBLE = 'isEligible';
    public static final String CRMRETAIL_STRING_HOME = 'Home';
    public static final String CRMRETAIL_STRING_EVENT_INTERACTIONS = 'EventInteractions';
    public static final String CRMRETAIL_INTERACTIONEVENTCLS = 'CRMRetail_InteractionsEvents_LC_HUM';
    public static final String CRMRETAIL_STRING_CHANGEATTENDANCECOUNT_METHOD = 'changeAttendanceCount';
    public static final String CRMRETAIL_STRING_SATTENDANCE = 'Attendance__c';
    public static final String CRMRETAIL_STRING_MODERNIZED_HOMEPAGE_PROCESSNEWCHECKIN_METHOD = 'processNewCheckIn';
    public static final String CRMRETAIL_STRING_MODERNIZED_HOMEPAGE_UPDATEBARCODE_METHOD = 'updateBarcode';
    public static final String CRMRETAIL_STRING_MODERNIZED_HOMEPAGE_ACKNOWLEDGENOTIFICATIONS_METHOD ='acknowledgeNotifications';
    public static final String CRMRETAIL_STRING_SPREFERENCE = 'Storefront_Preference__c';
    public static final String CRMRETAIL_STRING_SINTERACTION = 'Storefront_Interaction__c';
    Public static final String CRMRETAIL_SCHEDULED='Scheduled';
    public static final String CRMRETAIL_RETRIEVEINTERACTIONS = 'retrieveInteractions';
    public static final String CRMRETAIL_GETSDOHACRONYM = 'getSDohAcronym';
    public static final String CRMRETAIL_GETLOCATIONANDDATEVALUES = 'getLocationAndDateValues';
    Public Static final String STRING_CRMRETAIL_CUSTOM_VALIDATION_EXCEPTION = 'CUSTOM_VALIDATION_EXCEPTION';
    public static final String STRING_CRMRETAIL_HOMEDASHBOARD_PAGENAME='CRM_Retail_Custom_Dashboard_VF_HOME_HUM';
    public static final String CRMRETAIL_STRING_ELIGIBLE_FOR_RECOMMENDATION = 'accIdEligibleForRecommendation';
    public static final String STRING_CRMRETAIL_TRUE = 'true';
    public static final String STRING_CRMRETAIL_CREATETASK = 'createTasks';
    public static final String STRING_CRMRETAIL_NORMAL = 'Normal';
    public static final String STRING_CRMRETAIL_FOLLOWUP = 'Follow-up: ';
    public static final String STRING_CRMRETAIL_BUSINESSHOURS = 'CRMRetail_BusinessHours';
    public static final String STRING_CRMRETAIL_INTERACTIONTYPE = 'Interaction_Type__c';
    public static final String STRING_CRMRETAIL_EXPR0 = 'expr0';
    public static final String STRING_CRMRETAIL_NOTAPPLICABLE = 'N/A';
    public static final String STRING_CRMRETAIL_EVENTRECOMMENDATION='EventRecommendation';
    public static final String CRMRETAIL_EVENTRECOMMENDATION_CLASS='CRMRetail_RecommendEvents_C_HUM';
    public static final String CRMRETAIL_STRING_RECOMMENDATION_SWITCH = 'Switch_3510484';
    public static final String STRING_CRMRETAIL_SUCCESSLIST = 'successList';
    public static final String CRMRETAIL_CREATEINTERACTIONFORRECOMMEND_METHOD = 'createNewInteractionsForRecommendation';
    public static final Integer CRMRETAIL_STRING_RECOMMENDATION_COUNT = 5;
    public static final String CRMRETAIL_TASKDATEFORMAT = 'yyyy-MM-dd';
    public static final String CRMRETAIL_TIMEZONEGMT = 'GMT';
    public static final String CRMRETAIL_STRING_HIGHPHEN = '-';
    public static final String CRMRETAIL_TASKSUBJECTDATE_FORMAT = 'MMMM dd, yyyy';    
    public static final Integer CRMRETAIL_STRING_RECOMMENDATION_ELIGIBILITY_COUNT = 10;
    public static final String CRMRETAIL_STRING_ACCOUNT_KEY = 'Account__c';    
    public static final String CRMRETAIL_STRING_GET_RECOMMENDATION_EVENTS = 'getRecommendedEvents';
    public static final String CRMRETAIL_STRING_NO_EVENTS = 'noEvents';
    public static final String CRMRETAIL_STRING_RECOMMENDED_EVENTS = 'recommendedEvents';
    public static final String CRMRETAIL_STRING_UPCOMING_EVENTS = 'upcomingEvents';
    public static final String CRMRETAIL_STRING_RECOMMENDATIONDUPLICATE_TEXT ='The member has already RSVPd for one or more of the following events - ';
    public static final String CRMRETAIL_STRING_FOLLOW_UPTASK_ERROR = 'This is a Follow-up task, please use edit button to complete the task';
    public static final String CRMRETAIL_STRING_SPACED_HYPHEN = ' - ';
    public static final String CRMRETAIL_STRING_SCRIPT_THROWN_EXCEPTION = 'Script-thrown exception';
    public static final String CRMRETAIL_TASK_RECTYPEID_KEY = 'taskRecordTypeID';
    public static final Set<String> CRMRETAIL_SET_INTERACTION_CATEGORY_TYPES = New Set<String>{'Scheduled Onsite','Scheduled Virtual','Notification'};
    public static final STRING STRING_CRMRETAIL_TASK_INVALID_REASON = 'The interaction reason matching the subject does not exist, please use a valid interaction reason value';
    public static final STRING STRING_CRMRETAIL_SWITCH_CASE_SENSITIVITY = '3573183';
    public static final String CRMRETAIL_STRING_CREATEDBY_COLUMN_SWITCH = 'Switch_3782843';
    public static final String CRMRETAIL_STRING_GENERIC_HELPER_TEXT = 'GenericHelper_CRMRetail_H_HUM';
    public static final String CRMRETAIL_STRING_GETTASKACCOUNTINFO_TEXT = 'getTaskAccountInfo';
    public static final String CRMRETAIL_STRING_FRSTTIMEVSTR_TEXT = 'First Time Visitor This Year';
    public static final String CRMRETAIL_STRING_INTDETAILS_KEY = 'interactionDetails';
    public static final String CRMRETAIL_STRING_VCHECKIN_TYPE = 'vcheckin';
    public static final String CRMRETAIL_STRING_AMPERSAND = '&&';
    public static final String CRMRETAIL_STRING_SELECT_ACCOUNT_QUERY = 'Select Id,FirstName,CRMRetail_PreferredName__c,Enterprise_ID__c,LastName,Birthdate__C,Name,Waiver_Date__c,CRMRetailNotificationOptOut__c,Veteran_Status__c,(SELECT Id, Name,ETL_Record_Deleted__C,Effective_Date__c,status__c,End_Date__c,Member__C,Product__c,Policy__r.Platform__c,Policy__r.Product_Type__c,Policy__r.Product__r.Major_LOB__c,Policy__r.Source_Cust_Cov_Key__c,Policy__r.Product__r.ProductCode,Member__r.Enterprise_ID__c FROM Policy_Members__r WHERE Member__r.ETL_Record_Deleted__c = false AND ETL_Record_Deleted__c = false ';
    public static final String CRMRETAIL_STRING_PRODUCT_CHECK = 'AND Product__c =';
    public static final String CRMRETAIL_STRING_ORDERBY_CHECK = ' ORDER BY Status__c),GCM_Visitor_Barcode__c,RecordType.Name from Account where Id IN ';
    public static final String CRMRETAIL_SHOWICONS = 'showIcons';
    public static final String CRMRETAIL_DECEASED = 'Deceased';
    public static final String CRMRETAIL_DECEASEDICONNAME = 'utility:resource_absence';
    public static final String CRMRETAIL_SHOW = 'show';
    public static final String CRMRETAIL_COLOR = 'color';
    public static final String CRMRETAIL_ICON = 'icon';
    public static final String CRMRETAIL_REDCOLOR = 'Red';
    public static final String CRMRETAIL_STRING_UNABLE_TO_CONTACT = 'Unable to Contact';
    public static final String CRMRETAIL_STRING_TASKAUTOCLOSE_CLASS = 'CRMRetail_TaskAutoClose_B_HUM';
    public static final String CRMRETAIL_STRING_BATCHFAIL_MSG = 'Storefront Batch Processing for updating tasks failed on : ';
    public static final String STRING_CRMRETAIL_BATCHERROR_TASKID = ' Task Id : ';
    public static final String STRING_CRMRETAIL_TASK_BTACHERROR = ', Error: ';
    public static final String STRING_CRMRETAIL_ERROR_UPDATING_TASK = 'Error Updating Task: ';
    public static final String STRING_CRMRETAIL_BATCHERROR_RECORDS = ' record(s).';
    public static final String STRING_CRMRETAIL_BATCHERROR_DETAILS = ' Error Details - ';
    public static final List<String> STRING_CRMRETAIL_AUTO_TASK_CLOSE_STATUS_LIST = New List<String>{'\'Not Started\'','\'In Progress\''};  
    public static final String STRING_CRMRETAIL_PHONENOT_LISTED = 'No Phone Number Listed';
    public static final String STRING_CRMRETAIL_NEWLINE = '\n';
    public static final STRING CRMRETAIL_STRING_MHG365_ERROR = 'ErrorMemberWebActivity';
    public static final STRING CRMRETAIL_STRING_MHG365_VitalityEntity = 'VitalityEntityID';
    public static final STRING CRMRETAIL_STRING_MHG365_CUST_SET = 'HUMMemberWebActivityDetailsService';
    public static final STRING CRMRETAIL_STRING_MHG365_ISWEBREGISTERED_KEY = 'IsWebRegistered';
    public static final STRING CRMRETAIL_STRING_MHG365_LASTLOGINDATETIME_KEY = 'LastLoginDateTime';
    public static final STRING CRMRETAIL_STRING_MHG365_NI_PLATFORM_KEY = 'NI';
    public static final STRING CRMRETAIL_STRING_MHG365_FAULT_KEY = 'Fault';
    public static final STRING CRMRETAIL_STRING_MHG365_PERSONID = 'PERSONID';
    public static final STRING CRMRETAIL_STRING_MHG365_VITALITY_Y ='Y';
    public static final STRING CRMRETAIL_STRING_BLACKCOLOR = 'black';
    public static final STRING CRMRETAIL_STRING_CHECKICON = 'utility:check';
    public static final STRING CRMRETAIL_STRING_OK_TEXT = 'OK';
    public static final INTEGER CRMRETAIL_INTEGER_STATUSCODE = 200;
    public static final STRING CRMRETAIL_STRING_MEMBERACTIVITY_ERROR_NODE = 'MemberActivityError';
    public static final STRING CRMRETAIL_STRING_MHG365_ERROR_FETCHING_DETAILS = 'Error in Fetching MyHumana and Go365 Details from the Webservice. Please try again after some time';
    public static final STRING CRMRETAIL_STRING_MHG365_ISERROR_KEY = 'isError';
    public static final STRING CRMRETAIL_STRING_MHG365_ERRORMSG_KEY = 'errorMsg';
    public static final STRING CRMRETAIL_STRING_MHG365_ORDER = 'order';
    public static final STRING CRMRETAIL_STRING_MYHUMANA_ERROR_NODE = 'MyHumanaError';
    public static final STRING CRMRETAIL_STRING_GO365_ERROR_NODE = 'Go365Error';
    public static final STRING CRMRETAIL_STRING_LASTLOGIN_ERROR_NODE = 'LastLoginError';
    public static final STRING CRMRETAIL_STRING_MYHUMANA_ERROR_FETCHING_DETAILS = 'Error in Fetching MyHumana Details from the Webservice. Please try again after some time';
    public static final STRING CRMRETAIL_STRING_GO365_ERROR_FETCHING_DETAILS = 'Error in Fetching Go365 Details from the Webservice. Please try again after some time';
    public static final STRING CRMRETAIL_STRING_LSTLGN_DTTME_ERROR_FETCHING_DETAILS = 'Error in Fetching Last Login Date/Time Details from the Webservice. Please try again after some time';
    public static final STRING CRMRETAIL_STRING_MHG365_VITALITY_ENTITY_TYPE = 'Vitality-Entity-ID';
    public static final STRING CRMRETAIL_STRING_EVT_ATTND_UPDATE_ERROR_MSG = 'You are not allowed to edit the attendance fields, please reach out to your local manager for assistance.';
    public static final STRING CRMRETAIL_STRING_INQUIRY_INTERACTION_TYPE = 'Inquiry';
    public static final STRING CRMRETAIL_STRING_CSV_FORMAT = '.csv';
	public static final STRING CRMRETAIL_STRING_ERROR_CSV_COLUMN_HEADER = 'SalesforceID, Error Reason\n';
    public static final Integer CRMRETAIL_INTEGER_TOPEVENTS_COUNT = 5;
    public static final STRING CRMRETAIL_STRING_ISFUTUREMEMBER = 'isFutureMember';
    Public Static final String STRING_USER_ACTIONTYPE_CREATABLE = 'CREATABLE';
    Public Static final String STRING_USER_ACTIONTYPE_UPDATABLE = 'UPDATABLE';
    Public Static final String STRING_USER_ACTIONTYPE_READABLE = 'READABLE';
    Public Static final String STRING_USER_ACTIONTYPE_UPSERTABLE ='UPSERTABLE';
    Public Static final String STRING_DML_UPDATE_ACCESS = 'update';
    Public Static final String STRING_DML_READ_ACCESS = 'read';
    Public Static final String STRING_DML_UPSERT_ACCESS = 'upsert';
    Public Static final String STRING_DML_CREATE_ACCESS = 'create ';
    Public static final String STRING_USER = 'User ';
    Public Static final String STRING_INSUFFICIENT_FIELD_ACCESS = ' does not have access to field(s) : ';
    Public Static final String STRING_INSUFFICIENT_ACCESS = ' does not have sufficient access to ';

    public static final Set<String> CRMRETAIL_SET_NONFIRSTTIMEVISIT_TYPES = New Set<String>{'Inquiry','Visitor Referral Channel','Outbound Call Campaign'};
    //public static final STRING CRMRETAIL_STRING_ISFUTUREMEMBER = 'isFutureMember';    
    public static final STRING CRMRETAIL_STRING_ADDMEMBER_KEY = 'addmember';
    public static final STRING CRMRETAIL_STRING_GETMEMBER_KEY = 'getmember';
    public static final STRING CRMRETAIL_STRING_UPDATEMEMBER_KEY = 'updatemember';
    public static final STRING CRMRETAIL_STRING_DELETEMEMBER_KEY = 'deletemember';
    Public Static final String CRMRETAIL_STRING_GETALLDATA_KEY = 'getalldata';
    public static final String STRING_CRMRETAIL_DHNA_CREATE_MULESOFT = 'CRMRetail_SDoH_Create_Mulesoft';
    public static final String STRING_CRMRETAIL_DHNA_UPDATE_MULESOFT = 'CRMRetail_SDoH_Update_Mulesoft';
    public static final String STRING_CRMRETAIL_DHNA_DELETE_MULESOFT = 'CRMRetail_SDoH_Delete_Mulesoft';
    public static final String STRING_CRMRETAIL_GETALLDATA_MULESOFT = 'CRMRetail_GetAllData_Mulesoft';
    Public Static final String STRING_CRMRETAIL_SDOH_CUSTOM_SETTING_MULESOFT = 'CRMRetail_SDoH_Mulesoft';
    Public Static final String STRING_CRMRETAIL_SDOH_NAMEDCREDS_MULESOFT = 'callout:NC_CRMRetail_SDOH_Mulesoft_Service/';
    Public Static final String STRING_CRMRETAIL_GETALLDATA_NAMEDCREDS_MULESOFT = 'callout:NC_CRMRetail_GETALL_Mulesoft_Service/';
   
    public static final String STRING_CRMRETAIL_UPSERT_EVENT_TYPE = 'UPSERT';
}