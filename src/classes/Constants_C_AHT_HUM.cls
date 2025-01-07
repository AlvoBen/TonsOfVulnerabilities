/********************************************************************************************************************************************************
Contains constants for AHT-specific files. 

Test Class: 

Modification Log:
Developer				Code Review					Date				Description

Cody Sanders										01/26/2017			Original, added to CustomInteractionV2_C_AHT_HUM
Subhamay Mandal										02/13/2017			Added URL_ACCOUNT_DETAIL_PAGE, URL_ACCOUNT_DETAIL_PAGE_AHT, 
																		RAPIDFORCE_RELEASE_2_AHT_HUM
Subhamay Mandal										03/05/2017			Added new variable for Custom Permission 3
Subhamay Mandal										03/20/2017			Added new variable for Custom Permission 4
Subhamay Mandal										04/05/2017			Added New variable For Custom Permission 5
Amar Gadewar										04/17/2017			Added New variable For Custom Permission 6
Amar Gadewar										05/11/2017			Remove the Inbound Call Variable As getting from the HUMConstant.
Piyusha Babar           113547                       07/25/2017         REQ - 323539 -Ability Of a quick start To allow creation Of multiple cases With similar Claim Classification On the current interaction
Amar Gadewar										07/28/2017			REQ - 318368 - Adding POA Constant
Subhamay Mandal										09/29/2017			REQ - 329803 - Ability to prefill the quick start case comment fields based on the Classification and Intent selection (Part I-RF)
Amar Gadewar										04/02/2018			REQ - 359278 - Remove the Custom Permission
Subhamay Mandal										05/30/2018			Defect 361102.0001 REQ - 363405 Error message displayed is not appropriate while associating the case to policy
Asish Behera                                        06/04/2018          REQ - 366090 - Ability to Prefill the Year from the Policy Effective Date into the Individual Evidence of Coverage URL (RF)
Asish Behera                                        06/12/2018          REQ - 366095 - Add Individual Medicare Summary of Benefits link to the Member Policy Page (RF)
Subhamay Mandal                                     06/27/2018          REQ - 367101 AHT - Add Frequently Used Classification and Intent to Classification Dropdown (RF)
Amar Gadewar								   		07/02/2018			REQ - 366921 - Communication - Update Name of Ticker and Chatter Group (RF)
Amar Gadewar										07/03/2018			REQ - 367117 - AHT Clean up 
Darshan Sharma                                      09/09/2018          REQ - 374857 Quality - Ability for Case Owner to be updated when a HP Supervisor closes a Case. (RF)
Darshan Sharma 				                        01/17/2019 			REQ - 383747 Quality - Update Quick Start+ Label (RF)
Asish Behera 				                        02/17/2019 			REQ - 383941 User - Ability to set the Classification on the Case Edit Page Based on the Page a User Logged from (RF)
Subhamay Mandal								        	02/18/2019		        REQ - 386033 Display Additional Warning Message on ID Card Mgmt Page (RF)
Amar Gadewar										03/18/2019			REQ - 383941 - Defect fix for Classification not set on Case Edit Page for Billing Details page.
Subhamay Mandal										04/09/2019			Defect 409931.0001 fix. REQ � 389686 : If the case status is Pending Response/ Pending Transfer Milestones are not displayed
Subhamay Mandal                                     08/26/2019          REQ - 406018: added Method to get object's prefix.
* Subhamay Mandal									03/15/2020		    938292 QS 3.0 - AHT - Ability to create task from Quick Start (RF)
Samantha Tennyson				   					04/01/2021	        User Story 1986952: T1PRJ0001827 - MF 1 - AHT/Quality - QS 3.0 - Ability to display Pretext in Grievance and Appeals section based on Grievance/Appeal Reason selection (RF)
Samantha Tennyson				   					06/14/2021	        User Story 2372808: T1PRJ0001827 - MF 1 - Quality/FCR - Ability to display "Recommended Actions" in Quick Start (RF)
Atia Uzma                                           09/27/2021          User Story 2696506: T1PRJ0001827 - MF 1 - Iconology - ID Card Request Icon (RF)
********************************************************************************************************************************************************/

public class Constants_C_AHT_HUM 
{
    // Custom Permission - Accross Release
    public static final String  RAPIDFORCEUSER_AHT_HUM = 'RapidForceUser_AHT_HUM';
    public static final String  PHARMACY_SUPERVISOR_HUM = 'CRMS_300_HP_Supervisor_Custom';
    public static final String  FULLFILLMENT_CLASSIFICATION = 'Fulfillment';
    public static final String  ICACR_INTENT = 'ID Card/Access Card Request';
        
    public static final String  CLAIM_CLASSIFICATION = 'Claims';
    
    public static final String  OBJ_QUICKSTARTPRETEXT = 'Quick_Start_Pretext__c';
    
	public static final String RSO_CUSTOMER_SERVICE_NEWSFEED_GROUP = 'RSO Customer Service News Feed';
	public static final String TEST_CHATTER_GROUP = 'TestBusinessGroup';
	
	public Static final String REFERENCE_DOCUMENT_CI = 'Reference Document';	
	
	//REQ - 357438 - Add Individual Medicare Evidence of Coverage link to the Member Policy Page (RF)
	public static final String INDIVIDUAL_MEDICARE_EOC_DEFAULT_URL = 'https://www.humana.com/medicare/';
	public static final String INDIVIDUAL_MEDICARE_EOC_URL = 'https://www.humana-medicare.com/BenefitSummary/';
	public static final String INDIVIDUAL_MEDICARE_EOC_PDF = 'PDFs/';
	public static final String INDIVIDUAL_MEDICARE_EOC_DOT_PDF = '.pdf';
	public static final String INDIVIDUAL_MEDICARE_EOC = 'EOC';
	public static final String INDIVIDUAL_MEDICARE_SB = 'SB';
	
	public static final Map<String, SObjectField> MAP_CASE_FIELDS = Schema.getGlobalDescribe().get('Case').getDescribe().fields.getMap();	
	public static final Map<String, SObjectField> MAP_TASK_FIELDS = Schema.getGlobalDescribe().get('Task').getDescribe().fields.getMap();	
	// constans for policy status 
	public Static final String POLICYSTATUS_TERMED = 'Termed';
	public Static final String POLICYSTATUS_FUTURE = 'Future';
	public static final String SELECT_FREQUENTLY_CI = '-------------Frequently Used C&Is------------';
	public static final String SELECT_ALL_CLASSIFICATIONS = '-------------All Classifications------------------';

    	public static final String QUICKSTARTLABEL = 'QuickStart+';
    	//constant map to store all the page name where logging component is being used.
    	public static final map<String,String> map_PageName_Classfication = new map<String,String>{'Claims'=> 'Claims','Billing Profile'=>'Billing','Benefits'=>'Benefits','Billing'=>'Billing',
                                                                                            'PCP / PCD Summary' => 'PCP Change', 'Id Cards' => 'Fulfillment'};

	// MIR: list case status to calculate SLA time. 
	public static final List<String> listMIRCaseStatus = new List<String>{GLOBAL_CONSTANT_HUM.TASKLISTVIEW_INPROGRESS_HUM, GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PENDINGRESPONSE_HUM, GLOBAL_CONSTANT_HUM.TASKLISTVIEW_PENDINGTRANSFER_HUM};
	public static final String TASK_NOTIFICATION_TYPE = 'Notification Task';
	public static final Id RECORDTYPE_ID_NOTIFICATION_TASK = Schema.SObjectType.Task.getRecordTypeInfosByName().get('Service CRM').getRecordTypeId(); 
    
	//Grievance and Appeal
	public static final String OGO_PRETEXT_SETUP = 'OGO Pretext Setup';
	public static final String DIV_GA = 'DIV_GA';
    
    //Recommended Actions
    public static final String DOC_NAME_RECOMMENDED_ACTIONS = 'Recommended Actions';
	//Pharmacy RecordType Developer Names
	Public Static final List<String> Pharmacy_RecordType_DeveloperNames = New List<String>{'Closed_HP_Agent_Broker_Case','Closed_HP_Group_Case','Closed_HP_Member_Case','Closed_HP_Provider_Case','Closed_HP_Unknown_Case','HP_Agent_Broker_Case','HP_Group_Case','HP_Member_Case','HP_Provider_Case','HP_Unknown_Case'};

	//ID Card Request Icon
	public static final List<String> Classification_IDCard = New List<String>{'Fulfillment','Fulfillment (CT & TM)'};
}