/*******************************************************************************************************************************
Apex Class Name : AccountDetailR1_C_HUM.cls
Version         : 1.0
Created On      : 06/16/2016
Function        : Class is used as base class for Account Detail Skinny UI page

Modification Log:
* Developer Name            Code Review           Date                       Description
*------------------------------------------------- -----------------------------------------------------------------------------
* Swat Team                     XXXX             06/16/2016          Original Version
* Vishal Verma                  XXXX             08/09/2016          REQ - 275290 : Updated code to CRM Development Standards
* Rajesh Keswani                36372      		 08/23/2016          Defect fix for the defect 233034.0001
* Vishal Verma                  51411            12/13/2016          Fix fro defect 252792.0001.
* Amar Gadewar									 04/17/2017			 REQ - 305679 - Re-arranging Member Account Page (RF)
* Sharan Shanmugam								 05/02/2017			 REQ - 303395 - Check for Humana Pharmacy user
* Amar Gadewar									 08/08/2017			 REQ - 323549 : SF - TECH - Remove AHT Components Dependencies (RF)
* Pallavi R                    165514            12/22/2017          REQ - 350418 - Check if Current App is resolution App
* Dinesh Subramaniyan  			178520          2/02/2018            REQ - 350499 - Ability of a CRM Salesforce application to identify closing the Member Account without creating a case on interaction(CRM) 
* Darshan Sharma					             05/24/2018			 REQ - 363560 (AHT) - Add Jump Links to the Member Account Page (RF)
* Pallavi R                                      06/06/2018          REQ - 363592 - Rollback Resolution Search tab Changes  
* Amar Gadewar									 06/22/2018			 REQ - 367117 - Remove the Query for the 650 Quick Start Permission Set  
* Moshitha Gunasekaran          262118           06/28/2018          REQ - 350472 ID Number field on the Unknown Account tab
* Melkisan Selvaraj								 08/29/2018			 Defect Fix - Defect # 380546.0001
* Binod Katwal                  294960           10/12/2018          REQ - 359002 - Add Associated Forms & Information Section to Floating Navigation Link
* Satyam Malviya                                 12/18/2018          NBA - MF4 Guidance Alert      
* Rajesh Keswani         	308260	         11/02/2018          REQ - 377201 openup live chat transcript section for pharamcy users
* Prathyu Tushar                                 07/29/2019          REQ - 396182: added variable to check user having access for PDP Pilot Permissionset or not
* Prasanthi Kandula                              12/05/2019          REQ - 383988  Added boolean variable to enable logging only for Member/Group accounts 
* Amar Gadewar                                   03/18/2020          USER STORY 939081 PR00091574 - MF 1 - AHT - All Users - Auto collapse interaction panel after interaction creation (CRM)
* Amar Gadewar                                   04/03/2020          US 995046 PR00091574 - MF 1 - SF - TECH - On/Off Switch Cleanup - 4/10 and 4/24 Release (CRM)
* Amit Agarwal                                   04/23/2020          User Story 939082: PR00091574 - MF 1 - AHT - Add Date of Death field on Member Account page (CRM)
*Sunil Kumar Reddy Y                             05/22/2020          User Story 1114855: PR00091574 - MF 1 - SF - TECH - On/Off Switch Cleanup 6/5 and 6/19 releases (CRM)
* Mithra Bharadwaj                                                   US#754843 US#1071924 Member Maint.- Update Plan Demographics Button from Person Account Page- MEDICARE
* Pooja Kumbhar									 06/02/2020			 Added variables to solve the build fix 
* Amit Agarwal                                   06/25/2020          User Story 1186331: PR00091574 - MF 1 - Quality - Ability to display Repeat Caller Icon on search page and Repeat Caller Information on the Member Account page (RF)
* Amit Agarwal                                   07/17/2020          User Story 1276140: PR00091574 - MF 1 - Quality - Ability to display Repeat Caller Icon on search page and Repeat Caller Information on the Member Account page (CRM)
* Suresh Kokkeragadda                            07/29/2020          User Story 1317726: PR00091574 - MF 1 - SF - TECH - On/Off Switch Cleanup 7/30 , 8/14 and 8/28 releases (CRM)
* Sayali Nemade									 08/04/2020			 Added Switch for MF 3 - CRM User Access to CCP GCP Data in Repository (Legacy Contact History Section - Member)
* Sayali Nemade 								 08/27/2020          REQ - 1170241 MF 3 - Archived CCP Reference Summary Page - High Level  (Overall look)
* Sayali Nemade 								 09/03/2020          REQ - 1170241 DF -1509 Not able to see data on UI for Group, Provider and Agent/Broker
* Joel George									 09/03/2020          REQ - 1382511 Agent & Employer Portals: Transcript attachment to Agent or Group Details Pages
* Suraj Patil                     	   			 10/13/2020          USER STORY 1356635 PR00090631 - MF 12 - MVP - Hide Contact Handling Alerts section on Person Account and Business Account pages
* Suraj Patil                     	   			 11/03/2020          USER STORY 1390197 PR00090631 - MF 12 - MVP Adding Alert Panel to the Business Account page
* Samantha Tennyson                     	     01/22/2021          User Story 1875226: PR00091574 - MF 1 - AHT/Quality - Ability to make Repeat Caller attributes as Jump Links (RF)
* Anjani Vaddadi                                 03/17/2021          User Story#-1260081 -T1PRJ0003372 - MF 2, MF 3, MF 4 - Veteran Update Functionlity
* Atia Uzma                                       04/01/2021          User Story 2109676: T1PRJ0001827 - MF 1 - AHT/Quality - Ability to make Repeat Caller attributes as Jump Links (CRM)
* Firoja Begam                                   04/06/2021	         US2127870: INC0978500 - Floating links aren't working as expected for group account page.
* Ajay Chakradhar		                         04/28/2021			 DEF 2962 - REQ - 1441116 T1PRJ0002198/PR00093155 - MF 31-CR 1946  Create 3rd Permission Set for HP Non PDP Pilot Users
* Atia Uzma                                      05/05/2021          User Story 2230288: T1PRJ0001827 - MF 1 - SF - TECH - On/Off Switch Cleanup for 04/16/2021, 05/14/2021 and 05/28/2021 releases (CRM)
* Ajay Chakradhar                           	 05/18/2021	         REQ- 2184294 - Replacing constants with Consumer Management Constants
* Ankit Avula						   			 05/20/2021			 US2067694: T1PRJ0022885 - MF 8 - Display Small Group Contacts on Group Contact tab in CRM Service
* Vardhaman                                      06/07/2021         User Story#-1914200 -T1PRJ0002736 - MF 6 - PLT - Sonar Cube remediation/Consumer Management (URL Parameters should be Escaped/Sanitized)
* Vardhman Jain                                  07/29/2021          User story 1801212 PR00094010 - MVP MF 6 - Person Account  page - Strides
* Vardhman Jain                                  08/12/2021          User story 1801212 PR00094010 - Added Browser Check and Defect fix 3553
* Vardhaman Jain                                 06/04/2021          User Story-2615671-Veterans/Update Option in Dropdown
* Rahul Chaurasia								 10/13/2021  		 User Story 2376253: T1PRJ0003805 - Billing- Ability to see if an agent/broker is web registered
* Sahil Verma                                    10/14/2021          Show popup on Case/Plan/Detail page on click of Save button on Interaction Log Panel as part of US - 2564484
* * Vamshi Krishna Pemberthi			   11/02/2021			 Rollback User Story 2376253: T1PRJ0003805 - Billing- Ability to see if an agent/broker is web registered
* Vardhman Jain                                  11/11/2021          Added switch on Account and Policy page for Coveo
* Ajay Chakradhar                                11/22/2021          DEF 4182 FIX - Global Constant replace for performance improvement
* Bhumika S                                       2/2/2022           User Story 3010498: T1PRJ0314829 - MF 1 - Quality - Ability to move cases from an unknown to a known Member (RF)
* Ajay Chakradhar                               08/17/2022           USER STORY 2860577 : T1PRJ0100059 - MF 3 - Agnostic Customer Record Type
* Akash Surwase                                 08/17/2022           User Story 2860677:DF-5231 Fix
* Nilesh Gadkar					                 05/15/2023	         User Story 3421330: T1PRJ0881713 - FR09.09: CRM Service must create and display a command alert notification on the Person Account Page when the HRA flag/indicator = N
* Nilesh Gadkar					                 06/30/2023	         User Story 4330043: T1PRJ0891339 - MF 1.1 - Limit Population Criteria for Veteran Icon (Classic)
*****************************************************************************************************************************************/

public with sharing class AccountDetailR1_C_HUM 
{

    private final Account oAcct = NULL;
    public String ConsumerId{get;set;}
	public String ConsumerIdType{get;set;}
    private String SSNAsterix = '*****';
    public boolean isDisplay {get;set;}
    public boolean isPDPPermissionSetUser {get; set;}
    public boolean bLoggingCompVisibility {get;set;}
    public boolean bUpdatePlanDemographics {get;set;}
    public boolean bCrmlPlanDemographics {get;set;}
    public boolean bHPUpdatePlanDemographics {get;set;}
    public boolean isUpdatePlanDemographicsBtn {get;set;}
    public boolean isUpdatePlanDemographicsUser {get;set;}
    public boolean isDemographicPlanPermission {get;set;}
    public boolean isCommercialPlanPermission {get;set;}
    public String sRepeatCallerInfo{get;set;}
    private boolean bESAPILibSecurityfixSwitch {get;set;}
    Public Boolean bcontactHandlingNBARecommendation{get;set;}
    public boolean bRTIFlag {get;set;}
	public boolean bVeteranupdatepanel {get;set;}
     //Veterans MF-2 
    public boolean bShowVeteranInfoPanel {get;set;}
    private boolean bVeteraninfoSwitch =HUMUtilityHelper.isCRMFunctionalityON('1260069');
    public Veteran_DTO_HUM oVeteranDTO {get;set;}
    // INC0978500 Group Page Floating Link Incident Fix
    Public Boolean bgroupPageFloatingLinkOnOff{get;set;}
    public boolean isLink {get;set;}
    Private String SSN_HUM = 'SSN' ;
    Private String SUNKNOWN ='Unknown';
    Private String MM_CRMS_MEDPERM_NAME = 'CRMS_1200_MedicareMedicaid_Demographic_Update';
    Private String MM_CRMS_COMMPERM_NAME = 'CRMS_1210_Commercial_Demographic_Update';
    Private String MM_CRMS_HPMEDPERM_NAME = 'CRMS_1220_Pharmacy_Demographic_Update';
    Private String MEMBERCCP = 'AccountPage';
    Private String SFDC_HELP_HOST = 'help.salesforce.com';
    public boolean groupContactsListEPICCSwitch {get;set;}
    //Unified changes 1801212
	public String interactionNumber {get;set;}
    public String interactionWithType {get;set;}
    public String interactionAbout {get;set;}
    public String interactWith {get;set;}
    public boolean bShowUnifiedSearch{get;set;}
    //US_2615671_Veterans/Update Option in Dropdown
	private boolean bVeteranstatusSwitch;
    //US#2564484	
    public static boolean bPopup{get; set;}
    public Boolean bSwitch_3010498 {get;set;}
    //FIxing DF-5231 for US2860677 
    public boolean bLoggingbutton{get;set;}//used for disabling button
	public boolean agnosticOnOff{get;set;}//used for profle checking
	Public Static final String PHARMACYSPECIALIST_PROFILE_HUM = 'Humana Pharmacy Specialist';
    public boolean bagnosticlogswitch{get;set;}
    public boolean bagnosticswitch{get;set;}
    public boolean bagnosticRectypeswitch{get;set;}
    //US - 3421330
    public string sHRA_HNA_Flag{get;set;}
    
    /**
    * Assign values to boolean variables which Is used to render the sections on Account Skinny UI detail page.
    * <p>
    * Constructor Method to for the class , Initialize variables
    * @ Param 		ApexPages.StandardController Standardcontroller reference
    * @ Return 		NA
    */
    public AccountDetailR1_C_HUM(ApexPages.StandardController controller) 
    {    
     
    	if(!Test.isRunningTest())
    	{
	    	controller.addFields(new List<string>{'Interactions1__r'}); 
	    	controller.addFields(new List<string>{'LiveChatTranscripts'});
    	} 
        bLoggingbutton=false;
        agnosticOnOff = [SELECT Id, Name FROM Profile WHERE Id =:UserInfo.getProfileId()].Name == PHARMACYSPECIALIST_PROFILE_HUM ? true : false;       
        bLoggingCompVisibility = false;
        bESAPILibSecurityfixSwitch = HUMUtilityHelper.isCRMFunctionalityON('1914200');
	    bVeteranupdatepanel = HUMUtilityHelper.isCRMFunctionalityON('3315451');
        bagnosticlogswitch=HUMUtilityHelper.isCRMFunctionalityON('2860677');
    	bagnosticRectypeswitch = HUMUtilityHelper.isCRMFunctionalityON('2860577');
        bagnosticswitch = HUMUtilityHelper.isCRMFunctionalityON('3027327');
        init();    
        isPDPPermissionSetUser = UtilityHelper_H_AHT_HUM.isUserhavingPermissionSetAccess('CRMS_205_CCSPDPPharmacyPilot');
        this.oAcct = (Account)controller.getRecord();
        for(Interaction__c i : oAcct.Interactions1__r)
        {
        	interactionCount ++;
        }
        for(LiveChatTranscript i : oAcct.LiveChatTranscripts)
        {
        	transCount ++;
        }
        RecordTypeID = this.oAcct.recordtypeID;
        RecordTypeNm = this.oAcct.RecordType.Name;
		
        if((bcontactHandlingNBARecommendation && RecordTypeNm == ConsumerManagementConstants_HUM.HUMGroupIntType) || RecordTypeNm == ConsumerManagementConstants_HUM.MEMBER || RecordTypeNm == ConsumerManagementConstants_HUM.AGNOSTICCUSTOMER  || RecordTypeNm == ConsumerManagementConstants_HUM.UNKNOWN_MEMBER_HUM || RecordTypeNm == ConsumerManagementConstants_HUM.MEMBERCAREGIVER ) {
           isDisplay  = true ;
         }
        else{
           isDisplay = false ; 
        }
        
        //Incident INC0978500 -- Group Page Floating Link Fix
        if(bgroupPageFloatingLinkOnOff){
         if((bcontactHandlingNBARecommendation && RecordTypeNm == ConsumerManagementConstants_HUM.HUMGroupIntType)) {
           isLink  = true ; //added this new condition for Group Page only when isDisplay is True
         }    
        }

        if(bcontactHandlingNBARecommendation && RecordTypeNm == ConsumerManagementConstants_HUM.HUMGroupIntType){
            bRTIFlag = false;
        }
        
        if(RecordTypeNm == ConsumerManagementConstants_HUM.HUMGroupIntType)
        {
            bLoggingCompVisibility = true;
        	assignGroup();                    
        }
        else if(RecordTypeNm == ConsumerManagementConstants_HUM.AGENTBROKER_HUM)
        {
        	assignAgentBroker();                    
        }                
        else if(RecordTypeNm == ConsumerManagementConstants_HUM.PROVIDERTYPE_HUM)
        {
        	assignProvider();                                      
        }                    
        else if(RecordTypeNm == ConsumerManagementConstants_HUM.MEMBER)
        {
            bLoggingCompVisibility = true;
        	assignMember();   
        }  
        else if(RecordTypeNm == ConsumerManagementConstants_HUM.AGNOSTICCUSTOMER && bagnosticRectypeswitch)
        {   
            bLoggingbutton=true;
            bLoggingCompVisibility = true;
        	if(bagnosticswitch)
              assignAgnosticCustomer(); 
            else
               assignMember();   
        }                          
        else if(RecordTypeNm == ConsumerManagementConstants_HUM.UNKNOWNAGENTRECORDTYPE_HUM || RecordTypeNm == ConsumerManagementConstants_HUM.UNKNOWNGROUPRECORDTYPE_HUM || RecordTypeNm == ConsumerManagementConstants_HUM.UNKNOWNPROVIDERRECORDTYPE_HUM)
        {
            bcasehistory = true;                       
        }
        else if(RecordTypeNm == ConsumerManagementConstants_HUM.UNKNOWN_MEMBER_HUM)
        {
			assignUnknowMember();  
            List<ConsumerId__c> lstConsumer = [SELECT Id, Name, Account__c, Consumer_ID__c, ID_Type__c FROM ConsumerID__c where Account__c = :this.oAcct.ID Limit 1];
			if(!lstConsumer.isEmpty())
			{
				String consId = lstConsumer[0].Consumer_ID__c;
				if(String.isNotBlank(lstConsumer[0].ID_Type__c) && lstConsumer[0].ID_Type__c == SSN_HUM)
				{
					ConsumerId = SSNAsterix + consId.Right(4);
				}
				else
				{
					ConsumerId = consId;
				}
				ConsumerIdType = ConsumerManagementConstants_HUM.UnknownMember_ConsumerIdType.containsKey(lstConsumer[0].ID_Type__c) ? ConsumerManagementConstants_HUM.UnknownMember_ConsumerIdType.get(lstConsumer[0].ID_Type__c) : '';
			}               
        } 
        if(Pharmacy_H_HUM.isPharmacyUser() == ConsumerManagementConstants_HUM.NONPHARMACY_USER_HUM)
        {
        	bPharmacyUser = false;	
        }    
        else
        {
        	bPharmacyUser = true;
        	bInsuranceUser = false;
        	if(Pharmacy_H_HUM.isHPInsurancePSUser())	
        	{
        		bInsuranceUser = true;
        	}
        } 
        if(RecordTypeNm == ConsumerManagementConstants_HUM.UNKNOWN_MEMBER_HUM || RecordTypeNm == ConsumerManagementConstants_HUM.MEMBER)
        {
            bMember = true;
            showVeteranDetails();
        	createFloatingList();
        }
        if(RecordTypeNm == ConsumerManagementConstants_HUM.AGNOSTICCUSTOMER && bagnosticRectypeswitch)
        {
            bAgnosticCustomer = true;
            showVeteranDetails();
        	createFloatingList();
        }
        if(UtilityHelper_H_AHT_HUM.isUserhavingPermissionSetAccess(MM_CRMS_MEDPERM_NAME) && HUMUtilityHelper.isCRMFunctionalityON('754843') && RecordTypeNm != ConsumerManagementConstants_HUM.AGNOSTICCUSTOMER)
        {
        
            bUpdatePlanDemographics = true;
        
        }
         
        if(HUMUtilityHelper.isCRMFunctionalityON('1071924') && UtilityHelper_H_AHT_HUM.isUserhavingPermissionSetAccess(MM_CRMS_COMMPERM_NAME) && RecordTypeNm != ConsumerManagementConstants_HUM.AGNOSTICCUSTOMER)
        {
                
            bCrmlPlanDemographics = true;
        } 

        if(HUMUtilityHelper.isCRMFunctionalityON('1441116') && UtilityHelper_H_AHT_HUM.isUserhavingPermissionSetAccess(MM_CRMS_HPMEDPERM_NAME) && RecordTypeNm != ConsumerManagementConstants_HUM.AGNOSTICCUSTOMER)
        {
            bHPUpdatePlanDemographics = true;
        } 
        
        String sIntId = Apexpages.currentpage().getparameters().get('interactionId');
        sIntId = bESAPILibSecurityfixSwitch ? ESAPI.encoder().SFDC_HTMLENCODE(sIntId) : sIntId;
        RepeatCallerInformation_DTO_HUM objRepeatCallerInfo = MemberIcons_S_HUM.getRepeatCallerInformation(oAcct.Id,sIntId,true);
        if(objRepeatCallerInfo != null){
            sRepeatCallerInfo = json.serialize(objRepeatCallerInfo);
        }
	
	    
        String HRAJson='';
		
		// REQ - 1137828 Switch
 	   bSwitchlegacyContactHistory = HUMUtilityHelper.isCRMFunctionalityON('1137828'); 
	   sPageName = MEMBERCCP;
	   Id currentAccountId = ApexPages.currentPage().getParameters().get('id');
        if(currentAccountId != null) {
            List<Account> currentAccountLst = [SELECT Id, Name, RecordType.Name, Agent_type__c, Enterprise_Id__c, Mbr_Gen_Key__c, HRA_HNA_Completed__c FROM Account WHERE Id =: currentAccountId LIMIT 1];
            if(currentAccountLst != null  && currentAccountLst.size() != 0) {
                sInteractingAboutCCPGCP = currentAccountLst[0].Mbr_Gen_Key__c;
				sEnterpriseID = currentAccountLst[0].Enterprise_Id__c;
            HRAJson = currentAccountLst[0].HRA_HNA_Completed__c;
            }
            
        }
        if(!String.isBlank(HRAJson))
        {
            sHRA_HNA_Flag = getHRAStatus(HRAJson);
        }
        // US2067694 switch
       groupContactsListEPICCSwitch = HUMUtilityHelper.isCRMFunctionalityON('2067694');
	   getUnifiedSearchDeatils(sIntId);//Unified search method.

       this.bSwitch_3010498 = HUMUtilityHelper.isCRMFunctionalityON('3010498');
   } 
    
	Public String BillingAddress {get;set;}
	public integer interactionCount {get;set;}
	public integer transCount {get;set;}
    Public String MailingAddress {get;set;}
    Public Boolean bgroup {get;set;}
    Public Boolean bgroup_agent {get;set;}  
	Public Boolean bcontactHandlingAlerts {get;set;}	
    Public Boolean bverifyDemographics {get;set;}
    Public Boolean bpoliciesMem {get;set;}
    Public Boolean bpoliciesGrp {get;set;}
    Public Boolean bpoa {get;set;}
    Public Boolean bassociatedFormsInfo {get;set;}
    Public Boolean bcasehistory {get;set;}
    Public Boolean blegacyContactHistory {get;set;}
    Public Boolean bconsumerIDs {get;set;}    
    Public String RecordTypeNm {get;set;}
    Public ID RecordTypeID {get;set;}
    public string sBaseURL {get;set;}
    Public Boolean bPharmacyUser {get;set;}
    Public Boolean bInsuranceUser {get;set;}
    public list<string> lstFloatLinkelem{get;set;}
    public Boolean bAgnosticCustomer {get;set;}
    public Boolean bMember {get;set;}    
    public Boolean bSwitchlegacyContactHistory {get;set;}
    public String sPageName {get;set;}
	public String sInteractingAboutCCPGCP {get;set;}
    public String sEnterpriseID {get;set;}  
    
	
    /**
    * Method to initialize variables 
    *
    * @Param NA
    * @return NA
    */
    private void init()
    {
    	transCount = 0;
    	interactionCount = 0;
    	BillingAddress = '';
    	MailingAddress = '';
    	bgroup = false;
        bgroup_agent = false;
		bcontactHandlingAlerts = false;		
        bverifyDemographics = false;
        bpoliciesMem = false;
        bpoliciesGrp = false;
        bpoa = false;
        bassociatedFormsInfo = false;
        bcasehistory = false;
        blegacyContactHistory = false;
        bconsumerIDs = false;
        RecordTypeNm = '';
        RecordTypeID = null;
        String  baseHost =URL.getSalesforceBaseUrl().getHost();
		String baseURL =URL.getSalesforceBaseUrl().toExternalForm(); 
		sBaseURL = baseURL.replace(baseHost,SFDC_HELP_HOST);
        bMember = false;
        bAgnosticCustomer = false;        
        isDisplay = true;
		bRTIFlag = true;
        isLink = false;
        bgroupPageFloatingLinkOnOff = HUMUtilityHelper.isCRMFunctionalityON('2127870') || false;
		bcontactHandlingNBARecommendation =  HUMUtilityHelper.isCRMFunctionalityON('1041229');
        bShowVeteranInfoPanel = false;
    }
    
    /**
    * Method to assign boolean values when the selected record is of type Member
    *
    * @Param NA
    * @return NA
    */
    public void assignMember()
    {
    	
        bverifyDemographics = true;
        bpoliciesMem = true;
        bpoa = true;
        bassociatedFormsInfo = true;
        bcasehistory = true;
        blegacyContactHistory = true;
    }

    /**
    * Method to assign boolean values when the selected record is of type Agnostic Customer
    */
    public void assignAgnosticCustomer()
    {
        bpoliciesMem = true;
        bpoa = true;
        bassociatedFormsInfo = true;
        bcasehistory = true;
        
    }
    
    /* Method to assign boolean values when the selected record is of type Provider
    * @Param NA
    * @return NA
    */
    public void assignProvider()
    {
    	
        bCaseHistory = true;
        bLegacyContactHistory = true;
        bConsumerIDs = true; 	
    }
    
    /**
    * Method to assign boolean values when the selected record is of type Group
    *
    * @Param NA
    * @return NA
    */
    public void assignGroup()
    {
    	bGroup = true;
        bCaseHistory = true;
        bpoliciesGrp = true;
        bLegacyContactHistory = true;
        bConsumerIDs = true; 
        bgroup_agent = true;
    }
    
    /**
    * Method to assign boolean values when the selected record is of type Agent/Broker
    *
    * @Param NA
    * @return NA
    */
    public void assignAgentBroker()
    {
        bCaseHistory = true;
        bLegacyContactHistory = true;
        bConsumerIDs = true; 
        bgroup_agent = true;
    }
    
    /**
    * Method to assign boolean values when the selected record is of type Unknown Member
    *
    * @Param NA
    * @return NA
    */
    public void assignUnknowMember()
    {
    	bcasehistory = true; 
        bverifyDemographics = true;
        bpoliciesMem = true;
        
        bpoa = true;
        bassociatedFormsInfo = true;
        bcasehistory = true;
        blegacyContactHistory = true;
	}
    
    /*
	 * This method Is to create the list of floating links which are visible at the top of the customized Account Detail VF pages 
	 * @param  NA
	 * @return void
	 */
	public void createFloatingList()
	{
        lstFloatLinkelem = new list<string>();        
        if(bpoa)
        {
            lstFloatLinkelem.add(FloatingLinkConstants_HUM.sfloatLinkAccount0);   //Power of Attorney
        }
        if(bassociatedFormsInfo)
        {
            lstFloatLinkelem.add(FloatingLinkConstants_HUM.sfloatLinkAccount1);   //Associated Forms & Information
        }
       lstFloatLinkelem.add(FloatingLinkConstants_HUM.sfloatLinkAccount2);   //Person Account Detail
       if(bGroup)
        {
            lstFloatLinkelem.add(FloatingLinkConstants_HUM.sfloatLinkAccount4);   //Group Details
        }
        if(bverifyDemographics)
        {
            lstFloatLinkelem.add(FloatingLinkConstants_HUM.sfloatLinkAccount5);   //Verify Demographics
        }        
        
        if(bpoliciesMem)
        {
            lstFloatLinkelem.add(FloatingLinkConstants_HUM.sfloatLinkAccount7);    //Policies
        }
        if(bcasehistory)
        {
            lstFloatLinkelem.add(FloatingLinkConstants_HUM.sfloatLinkAccount8);    //Case History
        }
        if(blegacyContactHistory)
        {
            lstFloatLinkelem.add(FloatingLinkConstants_HUM.sfloatLinkAccount9);   //Legacy Contact History
        }
        if(bconsumerIDs)
        {
            lstFloatLinkelem.add(FloatingLinkConstants_HUM.sfloatLinkAccount10);    //Consumer ID
        }
        if(bpoliciesGrp)
        {
            lstFloatLinkelem.add(FloatingLinkConstants_HUM.sfloatLinkAccount11);    //Policies
        }
        lstFloatLinkelem.add(FloatingLinkConstants_HUM.sfloatLinkAccount12 + ' [' + interactionCount + ']');    //Interactions (Interacting With)
        if(bpoliciesMem)
        {
            lstFloatLinkelem.add(FloatingLinkConstants_HUM.sfloatLinkAccount13 + ' [' + transCount + ']');   //Live Chat Transcripts
        }
		      
        if (bShowVeteranInfoPanel)
        lstFloatLinkelem.add(FloatingLinkConstants_HUM.sfloatLinkAccount14);
	}

    /*
* Method Name   :  getHRAStatus
* Description   :  This method parse json response present in HRA_HNA_Completed__C field
* Return Type   :  string
* Parameters    :  NA
*/
    @testVisible private string getHRAStatus(string HRAJson)
    {
        try
        {
            HumHRAHNA_DTO HRAResponse;
            HRAResponse = (HumHRAHNA_DTO)system.json.deserialize(HRAJson,HumHRAHNA_DTO.class);
            return HRAResponse.HRAStatus;
        }
        catch(Exception ex)
        {
            return '';
        }
    }  
/*
* Method Name   :  showVeteranDetails
* Description   :  This method checks the conditions For the Veteran Section display In Account Detail page
* Return Type   :  void
* Parameters    :  NA
*/
    @testVisible private void showVeteranDetails()
    {
        if (Test.isRunningTest()){bVeteraninfoSwitch = true;}
        else bVeteraninfoSwitch = HUMUtilityHelper.isCRMFunctionalityOn('1260069');
        if (Test.isRunningTest()){bVeteranstatusSwitch = true;}
        else bVeteranstatusSwitch = HUMUtilityHelper.isCRMFunctionalityOn('2615671');		
        if (bVeteraninfoSwitch){
        bShowVeteranInfoPanel = getVetPanelVisibilityInfo(oAcct.Id);
        if (bShowVeteranInfoPanel)
        {
            oVeteranDTO = new Veteran_DTO_HUM(); 
                //US_2615671_Veterans/Update Option in Dropdown-Start
				 if(bVeteranstatusSwitch){
					If (!String.isBlank(oAcct.Veteran_Status__c))
					{
						switch on oAcct.Veteran_Status__c	
						{ 
							when 'identified-veteran' {oVeteranDTO.sVeteranStatus = ConsumerManagementConstants_HUM.sConfirmedVeteran; }
							when 'marketing-only' { oVeteranDTO.sVeteranStatus = ConsumerManagementConstants_HUM.sUnknown;}
							when else {oVeteranDTO.sVeteranStatus = oAcct.Veteran_Status__c.toLowerCase();}
						}
					}
					else
					{
						oVeteranDTO.sVeteranStatus = SUNKNOWN.toLowerCase();
					}
				}
				else{
                  oVeteranDTO.sVeteranStatus = !String.isBlank(oAcct.Veteran_Status__c) ? oAcct.Veteran_Status__c.toLowerCase():SUNKNOWN.toLowerCase();    
                }
                //US_2615671_Veterans/Update Option in Dropdown-End			                 
            oVeteranDTO.sVAHealthEnrollee = oAcct.VA_Health_Enrollee__c;             
            oVeteranDTO.sMemberGenKey = sInteractingAboutCCPGCP;            
            oVeteranDTO.sAccountName =oAcct.Name;
            oVeteranDTO.sAccountId = oAcct.Id;
            oVeteranDTO.veteranMessage = null;
            oVeteranDTO.veteranMessageType = null;
        }
        }
        else bShowVeteranInfoPanel = false;
    }

    /*
* Method Name   :  getVetPanelVisibilityInfo
* Description   :  This method checks the conditions for the Veteran Section display in Account Detail page
* Return Type   :  Boolean
* Parameters    :Account Id
*/
    @testVisible private boolean getVetPanelVisibilityInfo(String sAccountId){
        try {
            
            Boolean bShowVeteranInfo = false;
            Boolean bVeteraniconSwitch = false;
            bVeteraniconSwitch = HUMUtilityHelper.isCRMFunctionalityOn('3315370');
            map<string,Product_Type_For_Veteran_Icon_Display__c> mapProductType = Product_Type_For_Veteran_Icon_Display__c.getAll(); 
            if(mapProductType !=null && !mapProductType.isEmpty()) {
                List<Policy_Member__c> lstPolicyMem =  [Select id, Product_Type__c, Product_Type_Code__c FROM Policy_Member__c 
                                                        WHERE Member__c = :sAccountId  AND Status__c != null AND ETL_Record_Deleted__c = false];                                                                 
                for(Policy_Member__c oPolMember :lstPolicyMem) {
                    if(mapProductType.containsKey(oPolMember.Product_Type_Code__c) && !bVeteraniconSwitch){
                        bShowVeteranInfo = true;
                    }
                    else if(mapProductType.containsKey(oPolMember.Product_Type__c) && bVeteranupdatepanel){
                        bShowVeteranInfo = true;
                    }
                    //bShowVeteranInfo =  mapProductType.containsKey(oPolMember.Product_Type__c)? true : false; 
                    if(bShowVeteranInfo) break;
                }
            }
            else
            {
                bShowVeteranInfo = true;
            }
            return bShowVeteranInfo;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'VeteranDetail_C_HUM', 'getPanelVisibilityInfo');
            return null;
        } 
    }
/*
* Method Name   :  getUnifiedSearchDeatils
* Description   :  This method checks the conditions for the Unified search button display in Account Detail page
* Parameters    :Interaction id
*/
	 @testvisible private void getUnifiedSearchDeatils(string sIntId) { 
        string sBrowserContext = ApexPages.currentPage().getHeaders().get('USER-AGENT');

        if(sBrowserContext.contains(ConsumerManagementConstants_HUM.sChromeBrowser) || sBrowserContext.contains(ConsumerManagementConstants_HUM.sEdgeBrowser)){
        bShowUnifiedSearch = HUMUtilityHelper.isCRMFunctionalityON('ConsumerMgmtUnifiedSearchSwitch') && UnifiedSearchUtility_H_HUM.isUnifiedSearchEligible(); 
        if(bShowUnifiedSearch){
                getInteractiondetails(sIntId);
            } 
        }
        
    }
        @testvisible private void getInteractiondetails(string sIntId){
        if(sIntId!= null){									   
		Interaction_Member__c [] interactionMember =[SELECT Id,Interaction__r.name,Interaction__r.Interacting_With_type__c,
                                                      Interacting_About_type__c,Interaction__r.Interacting_With__c,
													  Interaction__r.Interacting_With__r.Name
													  FROM Interaction_Member__c 
                                                      Where interaction__c =:sIntId];								   
        if ( interactionMember != null && !interactionMember.isEmpty())
		{
            interactionNumber = interactionMember[0].Interaction__r.name;
            interactionWithType = interactionMember[0].Interaction__r.Interacting_With_type__c;
            interactionAbout = interactionMember[0].Interacting_About_type__c;
            interactWith = interactionMember[0].Interaction__r.Interacting_With__r.Name;
        }
        }
    }
    
    /* Method Name   :    closePopUp
    * Description   :    Close the pop up on interaction
    * Return Type   :    NA
    * Parameters    :    NA
    */
    public void closePopup() 
    {        
        try
        {  
            bPopup = false;  
        }
        catch(exception e)
        {
            HUMExceptionHelper.logErrors(e,'AccountDetailR1_C_HUM','closePopup');
        }
    }
    /* Method Name   :    saveAndContPopup
    * Description   :    Open the pop up on interaction
    * Return Type   :    NA
    * Parameters    :    NA
    */
    public PageReference saveAndContPopup() { 
        bPopUp = true;    
        return null;
   }
}