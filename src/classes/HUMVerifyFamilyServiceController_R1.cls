/*******************************************************************************************************************************
Apex Class Name : HUMVerifyFamilyServiceController
Version         : 1.0
Created On      : 25/07/2014
Function        : Class contains request to WSDL  HUMVerifyFamilyCOBService and to display "Last Verified By" and "Last Verified on" details on Account Page
Test Class      : HUMVerifyFamilyServiceControllerTest
           
Modification Log: 
* Developer Name           Code Review               Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
*Kritika Popat              17076                    07/17/2014               Original Version
*Asha Deepika Koyyana       17674                    09/03/2014               As part of defect fix(defect #152088) modified getDependents method
*Manish Kumar               17925                    09/14/2014               Fixed defect #152088
*Manish Kumar               17925                    09/15/2014               Code fix for CC review#17925
*Asha Deepika Koyyana       18218                    09/25/2014               In UpdateDemographics service,for userID field in UpdatedMemberDemographicsDetailsDTO method 
                                                                              instead of alias field passing Network_User_Id__c.
*Asha Deepika Koyyana       18218                    09/26/2014               As a part of defect(#156041) fix modified the soql query in getAccountList method              
                                                                              As a part of defect(#156048) fix modified the logic in getPersonDemoGraphicsInputDTO,
                                                                              getUpdateMemberInputDTO method to pass multiple genkeys to the service.
*Asha Deepika Koyyana       18289                    10/01/2014               As a part of defect fix(#155408) modified Last Verified by in populateDemographics method                                                                                 
*Asha Deepika Koyyana       18355                    10/06/2014               As a part of requirement #2411 added getPolicyMembers method  
*Asha Deepika Koyyana       18447                    10/10/2014               As a part of defect fix(#157315) in getPolicyMembers  method referring Policy Member Object 
                                                                              instead of policy object.
*Asha Deepika Koyyana       18519                    10/13/2014               As a part of CR-00051, modified the code in mapPolicyMembers method.
*Asha Deepika Koyyana       18519                    10/17/2014               Fixed CC comments(#18519).
*Asha Deepika Koyyana       19104                    11/05/2014               As per requirement #2411 added error message 
*Asha Deepika Koyyana       19104                    11/10/2014               Fixed CC comments(#19104) 
*Santhi Mandava             19338                    11/18/2014               Fixed defect(#161645)
*Asha Deepika Koyyana       20030                    12/22/2014               Fixed a  part of defect(#164143) i.e. when user is not present in Salesforce,instead of  
                                                                              displaying blank in Verify Demographics section displaying Network User Id.                                                                               
*Asha Deepika Koyyana       20465                    01/22/2015               Fixed defect (#168828)  
*Asha Deepika Koyyana       21123                    02/24/2015               As per REQ - 148668,updated the code to use HUMVerifyFamilyCOBService
*ChiranjeeviRao Ravuri      21123                    02/25/2015               Assigned 0(Zero) value to DepSSN,SubSSN as per ICD changes in getInputUpdateMemcovDetailsDTO method 
                                                                              to fix defect #173910.0001.                                      
*Subhamay Mondal									 04/07/2017				  REQ - 306385 - Highlight on the "Verify Demographics" section on Member account page 
*Saurabh Kaple              162071                   01/10/2018		          REQ - 333069 - Genkey conversion- updates to person service request	
*Dinesh Subramaniyan							     01/12/2018			      REQ - 349663 - Ability to display indicators on Quick Start when Member Demographic Verification And/Or Guidance Alerts are due (RF)																		                                     
* Sai Ayyagari              172913                   01/23/2018               REQ - 328929 Fix for HUMExceptionHelper: DML
* Rajesh CG                 176180                   01/25/2018			      REQ - 350788 - Convert Address Verification on HumVerifyFamilyServiceController_R1 to Asynch
*  Ajay Chakradhar                                   07/29/2020               REQ - 1065536 Replace the Person Service on Verify demographics from SOAP to REST
* Ajay Chakradhar                           		 05/05/2021	              REQ- 2184294 - Replacing constants with Consumer Management Constants
*******************************************************************************************************************************/

public with sharing class HUMVerifyFamilyServiceController_R1
{
    private List<HUMVerifyFamilyDemoWrapperHelper> lstSelectedMembers = new List<HUMVerifyFamilyDemoWrapperHelper>();
    private map<Id, Policy_Member__c> mapPolicyMem = new map<Id, Policy_Member__c>();
    private set<Id> setMemID = new set<Id>(); 
    private Account oAccount = new Account();
    private String sResult = '';
    private boolean bVerify = false;
    private boolean bOnlyCbisPolicies = false;
    private boolean bOnlyHumanaPolicies = false;
    HUMVerifyFamilyCOBService.GetLastVerifiedDatesResponse_elementFuture oHUMFutureResponse = new HUMVerifyFamilyCOBService.GetLastVerifiedDatesResponse_elementFuture();
    HUMVerifyFamilyCOBService.OIDemographicsOutputDTO oHUMDemographicsOutputDTO = new HUMVerifyFamilyCOBService.OIDemographicsOutputDTO();
    HUMVerifyFamilyCOBService.UpdateDemographicsResponse_elementFuture oHUMDemographicFutureResponse = new HUMVerifyFamilyCOBService.UpdateDemographicsResponse_elementFuture();
    public boolean bCoreApiServiceREST{get;set;}
    private string sRequest='';
    private string sUpdateRequest='';
    private String sBody ='' ;
        
     //Capture Any DML Exceptions in errorLog object
    public List<Error_Log__c> listErrorLogs 
    {
        get 
        {
            if(listErrorLogs==null) {return new List<Error_Log__c>();}
            return listErrorLogs;
        }
        set;
    }
   
    /*
    * Method Name : HUMVerifyFamilyServiceController
    * Description : Constructor for initializing the variables
    * Return type : NA
    * Paramater   : NA
    */
    public HUMVerifyFamilyServiceController_R1()
    {
        
        lstDispAccounts = new List<HUMVerifyFamilyDemoWrapperHelper>();
        lstVerifyDemoGraphics = new List<HUMVerifyDemoGraphicsWrapperHelper>();
        lstVerifyDemoWrapper = new list<VerifyDemographicGetWrapper_DTO_HUM>();
        try
        {
            bCoreApiServiceREST = HUMUtilityHelper.isCRMFunctionalityON('1065536');
            sAccId = ApexPages.currentPage().getParameters().get('accountId');  
            sIds = ApexPages.currentPage().getParameters().get('ids');
            if(sAccId != NULL)
            {
                 oAccount = [Select Enterprise_ID__c ,Mbr_Gen_Key__c from Account where id =: sAccId Limit 1];
            }
            getDependents();
        }
        catch(Exception ex) 
        {
             //Restrict insert of error Logs
            HUMExceptionHelper.bRestrictLog=true;
            listErrorLogs = HUMExceptionHelper.logErrors(ex, 'HUMVerifyFamilyServiceController', 'HUMVerifyFamilyServiceController');      
        }               
    }
    
    
    /*
    * Method Name : HUMVerifyFamilyServiceController
    * Description : Constructor for initializing the variables
    * Return type : NA
    * Paramater   : StandardController
    */
    public HUMVerifyFamilyServiceController_R1(ApexPages.StandardController controller) 
    {	
        lstVerifyDemoGraphics = new list<HUMVerifyDemoGraphicsWrapperHelper>();
    	lstVerifyDemoWrapper = new list<VerifyDemographicGetWrapper_DTO_HUM>();
        try
        {
            bCoreApiServiceREST = HUMUtilityHelper.isCRMFunctionalityON('1065536');
            sAccId = ApexPages.currentPage().getParameters().get('Id');
            if(!String.isBlank(sAccId))
            {
                oAccount = [Select Enterprise_ID__c , Mbr_Gen_Key__c  from Account where id =: sAccId Limit 1];
            }
            
        }
        catch(Exception ex) 
        {
            //Restrict insert of error Logs
            HUMExceptionHelper.bRestrictLog=true;
            listErrorLogs = HUMExceptionHelper.logErrors(ex, 'HUMVerifyFamilyServiceController', 'HUMVerifyFamilyServiceController');      
        } 
    }
    
    public String sAccId{get;set;}
    public String sIds {get;set;}
    public boolean bRender{get;set;}
    public List<HUMVerifyFamilyDemoWrapperHelper> lstDispAccounts {get; set;}
    public List<HUMVerifyDemoGraphicsWrapperHelper> lstVerifyDemoGraphics{get;set;}
    public List<VerifyDemographicGetWrapper_DTO_HUM> lstVerifyDemoWrapper{get;set;}   
    public boolean bVerifyMessage{get;set;}
    
    /*
    * Method Name : getPolicyMembers
    * Description : Gets all policy Members of the Account and throws a warning
                    message if Account contains only CBIS policy Members
    * Return type : void
    * Parameter   : NA
    */
    public void getPolicyMembers()
    {
        //To meet the requirement #2411
        HUM_CBIS_Enabled__c oHUMCbisFlag = HUM_CBIS_Enabled__c.getValues('HUM_CBIS_Flag');
        bRender = true;
        
        if(oHUMCbisFlag.CBIS_Flag__c)
        { 
            
            if(OnlyCbisPolicies()) 
            {
                bRender = false;
                //throw new HUMCustomException(system.Label.HUMCBISPoliciesMsg);
                ApexPages.addmessage(new ApexPages.message(ApexPages.severity.WARNING,system.Label.HUMCBISPoliciesMsg));
            }
        }     
    }
    
   /*
    * Method Name : onlyCbisPolicies
    * Description : Checks whether only Cbis Policies are present 
    * Return type : boolean
    * Parameter   : NA
    */
    private boolean onlyCbisPolicies()
    {
        bOnlyCbisPolicies = false;
        
        if(!String.isblank(sAccId)) 
        {
            if([Select count(Id) from Policy_Member__c where   Member__c =: sAccId] == [Select count(Id) from Policy_Member__c where Member__c =: sAccId AND Policy_Platform__c  =: system.Label.HUMCbisPlatform])    
            {   
                bOnlyCbisPolicies = true;
            }
        }
        return bOnlyCbisPolicies ;        
    }
   
    /*
    * Method Name : onlyHumanaPolicies
    * Description : Checks whether only  Humana policies are present
    * Return type : boolean
    * Parameter   : NA
    */
    private boolean onlyHumanaPolicies()
    {
        bOnlyHumanaPolicies = false;
        
        if(!String.isblank(sAccId)) 
        {
            if([Select count(Id) from Policy_Member__c where   Member__c =: sAccId] == [Select count(Id) from Policy_Member__c where Member__c =: sAccId AND Policy_Platform__c  !=: system.Label.HUMCbisPlatform])     
            {   
                bOnlyHumanaPolicies = true;
            }
        }
        return bOnlyHumanaPolicies ;        
    }
    
    /*
    * Method Name : mapPolicyMembers
    * Description : Gets the list and map of Policy_Members Id
    * Return type : void
    * Parameter   : NA
    */
    private void mapPolicyMembers()
    {
        List<Policy_Member__c> lstPolicyMember = new List<Policy_Member__c>();
        List<id> lstPolicyID = sIds.split(',');
        set<Id> setSubscriber = new set<ID>();
       
        //As a part of CR-00051 modified the below code
        for(Policy_Member__c oPolicyMem : [Select Subscriber__c From Policy_Member__c where Member__c  =: sAccId and Id IN: lstPolicyID ] )
        {
            if(oPolicyMem.Subscriber__c != NULL)  setSubscriber.add(oPolicyMem.Subscriber__c); 
        }
         
        if(setSubscriber.isempty())
        { 
            lstPolicyMember = [Select Member__r.Id, Relationship__c from Policy_Member__c where Member__c =: sAccId and Id IN: lstPolicyID] ;
        }
        else
        {     
            lstPolicyMember = [Select Member__r.Id, Relationship__c From Policy_Member__c where ID IN : setSubscriber] ; 
        } 
        
        //As part of defect fix (161645) moved 'for' loop from else block.
        for(Policy_Member__c oPolicyMem : lstPolicyMember)
        {
            setMemID.add(oPolicyMem.Member__r.Id);
            mapPolicyMem.put(oPolicyMem.Member__r.Id, oPolicyMem);
        }
        
        //As part of Defect fix (defect #152088) modified the SOQL queries 
        for(Policy_Member__c oPolicyMem : [Select Member__r.Id, Relationship__c From Policy_Member__c where Subscriber__c IN : lstPolicyMember])
        {
            setMemID.add(oPolicyMem.Member__r.Id);
            mapPolicyMem.put(oPolicyMem.Member__r.Id, oPolicyMem);
        }
    }

   /*
    * Method Name : getAccountList
    * Description : Gets the list of Accounts to display
    * Return type : void
    * Parameter   : NA
    */
    private void getAccountList()
    {
        Policy_Member__c oPolicyMem = new Policy_Member__c();
        String sRelation = '';
        
        //As a part of defect(#156041) fix modified the soql query
        for(Account oAccPopulate : [Select Id, Enterprise_ID__c, Mbr_Gen_Key__c , FirstName, LastName, Gender__c, Birthdate__c, PersonHomePhone, PersonMailingStreet, PersonMailingCity, PersonMailingState, PersonMailingPostalCode From Account where Id IN: setMemID]) 
        {
            oPolicyMem = mapPolicyMem.get(oAccPopulate.Id);
            sRelation = oPolicyMem.Relationship__c;
            lstDispAccounts.add(new HUMVerifyFamilyDemoWrapperHelper(oAccPopulate, sRelation));
         }
        if(lstDispAccounts.isempty() || (lstDispAccounts.size()==1 && lstDispAccounts[0].Account.Id==sAccId))  noDependents();
    }
    
    /*
    * Method Name : getDependents
    * Description : Gets the Dependents of the Account 
    * Return type : void
    * Parameter   : NA
    */
    private void getDependents()
    {
       try
       {
            //Code fix for CC review#17925
            mapPolicyMembers();
            getAccountList();
        } 
        catch(Exception ex) 
        {
             //Restrict insert of error Logs
            HUMExceptionHelper.bRestrictLog=true;
            listErrorLogs = HUMExceptionHelper.logErrors(ex, 'HUMVerifyFamilyServiceController', 'getDependents');      
        }
    }
    
    /*
    * Method Name : noDependents
    * Description : If there are no Dependents throws a warning message 
    * Return type : boolean
    * Parameter   : NA
    */
    private boolean noDependents()
    {
        try 
        {
            throw new HUMCustomException(system.Label.HUMNoDependents);
        }
        catch(Exception e) 
        {
           //Restrict insert of error Logs
            HUMExceptionHelper.bRestrictLog=true;
            listErrorLogs =  HUMExceptionHelper.logErrors(e, 'HUMVerifyFamilyServiceController', 'noDependents');
            return false; 
        }
        return true;  
    }
   
    /*
    * Method Name   :    getLastVerifyDates()
    * Description   :    To hit the Async webservice GetLastVerifiedDates
    * Return Type   :    void 
    * Parameter     :    NA
    */
    public Continuation getLastVerifyDates()
    {	
        getPolicyMembers();
        HUMVerifyFamilyCOBService.soap oHUMSoap = new HUMVerifyFamilyCOBService.soap();
        HUM_Webservice_Callout__c oHUMWebServiceCall = HUM_Webservice_Callout__c.getValues('AddressVerification');
        oHUMSoap.clientCertName_x = oHUMWebServiceCall.Certificate_Name__c; 
        oHUMSoap.endpoint_x =  oHUMWebServiceCall.End_Point_URL__c;
        oHUMSoap.timeout_x = Integer.valueOf(oHUMWebServiceCall.Timeout__c);
        Integer TIMEOUT_INT_SECS = Integer.valueOf(oHUMWebServiceCall.Timeout__c) / 1000;
        Continuation cont = new Continuation(TIMEOUT_INT_SECS);
        HUMVerifyFamilyCOBService.OIDemographicsInputDTO oHUMOIDemographicsInputDTO = new HUMVerifyFamilyCOBService.OIDemographicsInputDTO();
        oHUMOIDemographicsInputDTO.Category = ''; 
        oHUMOIDemographicsInputDTO.PlatformCode = '';
        oHUMOIDemographicsInputDTO.MemberGenKey = long.valueOf((oAccount.Mbr_Gen_Key__c == NULL) ? '0' : oAccount.Mbr_Gen_Key__c);
        oHUMOIDemographicsInputDTO.EnterpriseId = (oAccount.Enterprise_ID__c == NULL) ? '0' : oAccount.Enterprise_ID__c;  
		Try
			{
				cont.continuationMethod = 'HUMVerifyFamilyServiceController_R1.processGetLastVerifyDates';
				oHUMFutureResponse = oHUMSoap.beginGetLastVerifiedDates(cont,oHUMOIDemographicsInputDTO);    
			}
			Catch(exception ex)
			{
				HUMExceptionHelper.bShowErrorOnPage = false;
				HUMExceptionHelper.bRestrictLog = true;
				HUMExceptionHelper.bShowErrorOnPage = true;
				HUMExceptionHelper.logErrors(ex, 'HUMVerifyFamilyServiceController', 'getLastVerifyDates');
			 }
		Return cont;
    }
    /*
    * Method Name   :    processGetLastVerifyDates
    * Description   :    continuation call out response process
    * Return Type   :    Object
    * Parameter     :    VOID
    */
    public Object processGetLastVerifyDates() {   
      try
    {	
		   oHUMDemographicsOutputDTO =(HUMVerifyFamilyCOBService.OIDemographicsOutputDTO)oHUMFutureResponse.getValue();
		   populateDemographics(oHUMDemographicsOutputDTO);
		   }
	  catch(exception ex)
			{
				HUMExceptionHelper.bShowErrorOnPage = false;
				HUMExceptionHelper.bRestrictLog = true;
				HUMExceptionHelper.bShowErrorOnPage = true;
				HUMExceptionHelper.logErrors(ex, 'HUMVerifyFamilyServiceController', 'processGetLastVerifyDates');
			 }
       return null;
   }
    /*
    * Method Name   :    populateDemographics
    * Description   :    Populates Last verified by ,Last Verified On in VerifyDemographics section
    * Return Type   :    void
    * Parameter     :    HUMVerifyFamilyCOBService.OIDemographicsOutputDTO
    */
    private void populateDemographics( HUMVerifyFamilyCOBService.OIDemographicsOutputDTO oHUMDemographicsOutputDTO)
    {
        String sNetworkUserId = '';
        HUMVerifyDemoGraphicsWrapperHelper overify = new HUMVerifyDemoGraphicsWrapperHelper();
        overify.sLastVerifiedOn = (NULL != oHUMDemographicsOutputDTO.DemographicsLastVerifiedDate) ? Date.valueOf(oHUMDemographicsOutputDTO.DemographicsLastVerifiedDate) : NULL;
        //As a part of defect fix(#155408) modified the code to display user full name
        overify.sLastVerifiedBy = '';
        if(NULL != oHUMDemographicsOutputDTO.DemographicsVerifiedBy)
        {
            sNetworkUserId = oHUMDemographicsOutputDTO.DemographicsVerifiedBy.trim();
            if(!String.isblank(sNetworkUserId))
            {
                try
                {
                    User oUser = [Select Name from User where Network_User_Id__c  =: sNetworkUserId Limit 1];
                    if(oUser != NULL )  overify.sLastVerifiedBy = oUser.Name;
                }      
                catch(exception ex)
                {
                    overify.sLastVerifiedBy = sNetworkUserId;
                }        
            }
        }
        else
        {
            overify.sLastVerifiedBy = system.Label.HUMVerifyGetLastVerifyError;
        }
        overify.iDaysSinceLastVerified  = (NULL != overify.sLastVerifiedOn) ? Date.valueOf(String.valueOf(overify.sLastVerifiedOn)).daysBetween(system.Today()) : -1;
        bVerifyMessage = (overify.iDaysSinceLastVerified > 90) ? true : false;
        lstVerifyDemoGraphics = new List<HUMVerifyDemoGraphicsWrapperHelper>();
        lstVerifyDemoGraphics.add(overify);
        if(bVerify)
        {
            if(!OnlyHumanaPolicies() && !OnlyCbisPolicies()) throw new HUMCustomException(system.Label.HUMHumanaCBISPolicies);
        }
    }
    /*
    * Method Name   :    verifyDemographics
    * Description   :    Hitting the Async webservice UpdateDemographics
    * Return Type   :    void
    * Parameter     :    NA
    */
    public Continuation verifyDemographics()
    {	
        HUMVerifyFamilyCOBService.soap oHUMSoap = new HUMVerifyFamilyCOBService.soap();
        HUM_Webservice_Callout__c oHUMWebServiceCall = HUM_Webservice_Callout__c.getValues('AddressVerification');
        oHUMSoap.clientCertName_x = oHUMWebServiceCall.Certificate_Name__c; 
        oHUMSoap.endpoint_x =  oHUMWebServiceCall.End_Point_URL__c;
        oHUMSoap.timeout_x = Integer.valueOf(oHUMWebServiceCall.Timeout__c);
        Integer TIMEOUT_INT_SECS = Integer.valueOf(oHUMWebServiceCall.Timeout__c) / 1000;
        lstSelectedMembers = new List<HUMVerifyFamilyDemoWrapperHelper>();
        String sActionType = Label.HUMServiceActionType;
        String sMemberId = '';
        String sTrimSize = Label.HUMServiceTrimSize;
        HUM_CBIS_Enabled__c oHUMCbisFlag = HUM_CBIS_Enabled__c.getValues('HUM_CBIS_Flag');
        if(oHUMCbisFlag.CBIS_Flag__c) bVerify = true;
        if(NULL != lstDispAccounts && !lstDispAccounts.isempty())
        {
            for (HUMVerifyFamilyDemoWrapperHelper oHUMVerifyFamilyDemoWrapperHelper : lstDispAccounts) 
            {
                if (oHUMVerifyFamilyDemoWrapperHelper.bSelected == true) lstSelectedMembers.add(oHUMVerifyFamilyDemoWrapperHelper); 
            }
        }
        HUMVerifyFamilyCOBService.PersonDemographicsLastVerifiedInfoDTO oPerDemLstVerifyInfo = getPersonDemoGraphicsInputDTO();
        HUMVerifyFamilyCOBService.UpdatedMemberDemographicsDetailsDTO oUpdateMemDemoDetails = getUpdateMemberInputDTO();
        Continuation cont = new Continuation(TIMEOUT_INT_SECS);
        try
        {
            cont.continuationMethod = 'HUMVerifyFamilyServiceController_R1.processverifyDemographics';
            oHUMDemographicFutureResponse = oHUMSoap.beginUpdateDemographics(cont,sActionType , oPerDemLstVerifyInfo, sMemberId , sTrimSize , oUpdateMemDemoDetails);    
        }     
        catch(exception ex)
        {
            HUMExceptionHelper.bShowErrorOnPage = false;
            HUMExceptionHelper.bRestrictLog = true;
            HUMExceptionHelper.bShowErrorOnPage = true;
            HUMExceptionHelper.logErrors(ex, 'HUMVerifyFamilyServiceController', 'verifyDemographics');
        }
      return cont;   
    }
    /*
    * Method Name   :    processverifyDemographics
    * Description   :    continuation call out response process
    * Return Type   :    Object
    * Parameter     :    VOID
    */
    public Object processverifyDemographics() {   
		try{
			   sResult =(String)oHUMDemographicFutureResponse.getValue();
			   return getLastVerifyDates();
			}
		catch(exception ex)
			{
				HUMExceptionHelper.bShowErrorOnPage = false;
				HUMExceptionHelper.bRestrictLog = true;
				HUMExceptionHelper.bShowErrorOnPage = true;
				HUMExceptionHelper.logErrors(ex, 'HUMVerifyFamilyServiceController', 'processverifyDemographics');
				return null;
			}
   }
    /* 
    * Method Name  :    getPersonDemoGraphicsInputDTO
    * Description  :    Forming PersonDemographicsLastVerifiedInfoDTO to hit the webservice Update Demographics
    * Return Type  :    HUMVerifyFamilyCOBService.PersonDemographicsLastVerifiedInfoDTO
    * Parameter    :    NA
    */ 
    private HUMVerifyFamilyCOBService.PersonDemographicsLastVerifiedInfoDTO getPersonDemoGraphicsInputDTO()
    {
        HUMVerifyFamilyCOBService.PersonDemographicsLastVerifiedInfoDTO oPerDemLstVerifyInfo = new  HUMVerifyFamilyCOBService.PersonDemographicsLastVerifiedInfoDTO();
        oPerDemLstVerifyInfo.SelectedMemberGenKey = '';
        HUMVerifyFamilyCOBService.PersonCoverageInfoDTO oPerCovInfo = getInputPersonCoverageInfoDTO();
        HUMVerifyFamilyCOBService.ArrayOfPersonCoverageInfoDTO  oArrPerCovInfo = new HUMVerifyFamilyCOBService.ArrayOfPersonCoverageInfoDTO();
        oArrPerCovInfo.PersonCoverageInfoDTO = new HUMVerifyFamilyCOBService.PersonCoverageInfoDTO[]{oPerCovInfo};                   
        HUMVerifyFamilyCOBService.PersonMemberInfoDTO oPerMemInfo = new HUMVerifyFamilyCOBService.PersonMemberInfoDTO();
        HUMVerifyFamilyCOBService.ArrayOfPersonMemberInfoDTO oArrPerMemInfo = new HUMVerifyFamilyCOBService.ArrayOfPersonMemberInfoDTO();
        oArrPerMemInfo.PersonMemberInfoDTO = new HUMVerifyFamilyCOBService.PersonMemberInfoDTO[]{};
        // As a part of defect(#156048) fix modified the code to pass multiple genkeys to the service.
        if(NULL != lstSelectedMembers && !lstSelectedMembers.isEmpty())
        {   
            for(integer i = 0 ; i < lstSelectedMembers.size() ; i++) 
            {
                HUMVerifyFamilyCOBService.PersonMemberInfoDTO oPerMemInfo1 = new HUMVerifyFamilyCOBService.PersonMemberInfoDTO();
                oPerMemInfo1.MemberGenKey = lstSelectedMembers[i].Account.Mbr_Gen_Key__c;
                oPerMemInfo1.EnterpriseId = lstSelectedMembers[i].Account.Enterprise_ID__c;
                oArrPerMemInfo.PersonMemberInfoDTO.add(oPerMemInfo1);
            }
        }
        else
        {
            HUMVerifyFamilyCOBService.PersonMemberInfoDTO oPerMemInfo1 = new HUMVerifyFamilyCOBService.PersonMemberInfoDTO();
             oPerMemInfo1.MemberGenKey = oAccount.Mbr_Gen_Key__c;
             oPerMemInfo1.EnterpriseId = oAccount.Enterprise_ID__c;
            oArrPerMemInfo.PersonMemberInfoDTO.add(oPerMemInfo1);
        }
        oPerMemInfo.DemographicMemberCoverageInfo = oArrPerCovInfo;
        oPerDemLstVerifyInfo.DemographicMemberInfo = oArrPerMemInfo;
        return oPerDemLstVerifyInfo; 
    }
    /*
    * Method Name  :    getUpdateMemberInputDTO
    * Description  :    Forming UpdatedMemberDemographicsDetailsDTO  to hit update Demographics webservice
    * Parameter    :    NA
    * Return Type  :    HUMVerifyFamilyCOBService.UpdatedMemberDemographicsDetailsDTO 
    */ 
    private HUMVerifyFamilyCOBService.UpdatedMemberDemographicsDetailsDTO getUpdateMemberInputDTO()
    {
        HUMVerifyFamilyCOBService.UpdatedMemberDemographicsDetailsDTO oUpdateMemDemoDetails = getInputUpdateMemDemoDetailsDTO(); 
        HUMVerifyFamilyCOBService.UpdatedMemberAddressDetailsDTO oUpdateMemAddrDetails = getInputUpdateMemAddrDetailsDTO();
        HUMVerifyFamilyCOBService.ArrayOfUpdatedMemberAddressDetailsDTO oUpdateMemAddDetails = new HUMVerifyFamilyCOBService.ArrayOfUpdatedMemberAddressDetailsDTO();
        oUpdateMemAddDetails.UpdatedMemberAddressDetailsDTO = new HUMVerifyFamilyCOBService.UpdatedMemberAddressDetailsDTO[]{}; 
        // As a part of defect(#156048) fix modified the code to pass multiple genkeys to the service.
        if(NULL != lstSelectedMembers && !lstSelectedMembers.isEmpty())
        {   
            for(integer i = 0 ; i < lstSelectedMembers.size() ; i++) 
            {
                HUMVerifyFamilyCOBService.UpdatedMemberAddressDetailsDTO oUpdateMemAddrDetails1 = getInputUpdateMemAddrDetailsDTO();
                oUpdateMemAddrDetails1.MemGenKey = lstSelectedMembers[i].Account.Mbr_Gen_Key__c;
                oUpdateMemAddrDetails1.EnterpriseId = lstSelectedMembers[i].Account.Enterprise_ID__c;
                oUpdateMemAddDetails.UpdatedMemberAddressDetailsDTO.add(oUpdateMemAddrDetails1);
            }
        }
        else
        {
            HUMVerifyFamilyCOBService.UpdatedMemberAddressDetailsDTO oUpdateMemAddrDetails1 = getInputUpdateMemAddrDetailsDTO();
            oUpdateMemAddrDetails1.MemGenKey = oAccount.Mbr_Gen_Key__c;
            oUpdateMemAddrDetails1.EnterpriseId = oAccount.Enterprise_ID__c;
            oUpdateMemAddDetails.UpdatedMemberAddressDetailsDTO.add(oUpdateMemAddrDetails1);
        }
        HUMVerifyFamilyCOBService.UpdatedMemberCoverageDetailsDTO oUpdateMemcovDetails = getInputUpdateMemcovDetailsDTO();           
        HUMVerifyFamilyCOBService.ArrayOfUpdatedMemberCoverageDetailsDTO oArrPerCovInfoAdd = new HUMVerifyFamilyCOBService.ArrayOfUpdatedMemberCoverageDetailsDTO();
        oArrPerCovInfoAdd.UpdatedMemberCoverageDetailsDTO = new HUMVerifyFamilyCOBService.UpdatedMemberCoverageDetailsDTO[]{oUpdateMemcovDetails};   
        HUMVerifyFamilyCOBService.UpdatedMemberCoverageDetailsDTO updateCoverageDetails;
        oUpdateMemDemoDetails.UpdatedMemberCoverageDetails = oArrPerCovInfoAdd;
        oUpdateMemDemoDetails.UpdatedMemberResidentialAddressDetails = new HUMVerifyFamilyCOBService.UpdatedMemberResidentialAddressDTO();    
        oUpdateMemDemoDetails.UpdatedMemberAddressDetails = oUpdateMemAddDetails; 
        return oUpdateMemDemoDetails;
    }   
    /*
    * Method Name  :    getInputPersonCoverageInfoDTO
    * Description  :    To get successful response from service passing
                      blank values to non mandatory input and valid inputs to mandatory fields
    * Return Type  :    HUMVerifyFamilyCOBService.PersonCoverageInfoDTO  
    * Parameter    :    NA
    */
    private HUMVerifyFamilyCOBService.PersonCoverageInfoDTO  getInputPersonCoverageInfoDTO() 
    {       
       HUMVerifyFamilyCOBService.PersonCoverageInfoDTO oPerCovInfotemp = new HUMVerifyFamilyCOBService.PersonCoverageInfoDTO();
       oPerCovInfotemp.Category = '';
       oPerCovInfotemp.MemberIDBase = '';
       oPerCovInfotemp.MemberIDDependentCode = '';
       oPerCovInfotemp.SrcPlatformCode = '';
       return oPerCovInfotemp;           
    }
    /*
    * Method Name   :    getInputUpdateMemDemoDetailsDTO
    * Description   :    To get successful response from service passing
                       blank values to non mandatory input and valid inputs to mandatory fields
    * Return Type   :    HUMVerifyFamilyCOBService.UpdatedMemberDemographicsDetailsDTO 
    * Parameter     :    NA
    */
    private HUMVerifyFamilyCOBService.UpdatedMemberDemographicsDetailsDTO getInputUpdateMemDemoDetailsDTO()
    {          
       Id userID =  UserInfo.getuserID();
       User oUser = [Select Network_User_Id__c from User where Id =: userID Limit 1];
       HUMVerifyFamilyCOBService.UpdatedMemberDemographicsDetailsDTO oUpdateMemDemoDetailsTemp = new HUMVerifyFamilyCOBService.UpdatedMemberDemographicsDetailsDTO(); 
       oUpdateMemDemoDetailsTemp.CntyCd = '';
       oUpdateMemDemoDetailsTemp.DOB = '';
       oUpdateMemDemoDetailsTemp.FirstName = '';
       oUpdateMemDemoDetailsTemp.Gender = '';
       oUpdateMemDemoDetailsTemp.GroupGenKeys = Label.HUMServiceGroupMemSubGenkeys;
       oUpdateMemDemoDetailsTemp.IsHUManaOneHCR = '';
       oUpdateMemDemoDetailsTemp.LastName = '';
       oUpdateMemDemoDetailsTemp.MiddleInit = '';
       oUpdateMemDemoDetailsTemp.SSN = '';
       oUpdateMemDemoDetailsTemp.TransType = '';
       oUpdateMemDemoDetailsTemp.UpdateType = Label.HUMServiceUpdateType;
       oUpdateMemDemoDetailsTemp.UpdateCnt = Label.HUMServiceUpdateCnt;
       if(NULL != lstSelectedMembers && !lstSelectedMembers.isEmpty())
       {
          oUpdateMemDemoDetailsTemp.UpdateCnt = String.valueof(lstSelectedMembers.size());
       }
       oUpdateMemDemoDetailsTemp.userID = '';
       if(oUser.Network_User_Id__c != NULL)
       {
           oUpdateMemDemoDetailsTemp.userID = oUser.Network_User_Id__c;
       }
       return oUpdateMemDemoDetailsTemp;         
    }
    /*
    * Method Name  :    getInputUpdateMemcovDetailsDTO
    * Description  :    To get successful response from service passing
                         blank values to non mandatory input and valid inputs to mandatory fields
    * Return Type  :    HUMVerifyFamilyCOBService.UpdatedMemberCoverageDetailsDTO 
    * Parameter    :    NA
    */
    private HUMVerifyFamilyCOBService.UpdatedMemberCoverageDetailsDTO getInputUpdateMemcovDetailsDTO()
    {
       HUMVerifyFamilyCOBService.UpdatedMemberCoverageDetailsDTO oUpdateMemcovDetailsTemp = new HUMVerifyFamilyCOBService.UpdatedMemberCoverageDetailsDTO();
       oUpdateMemcovDetailsTemp.MbrGenKey = Label.HUMServiceGroupMemSubGenkeys; 
       oUpdateMemcovDetailsTemp.SubGenKey = Label.HUMServiceGroupMemSubGenkeys; 
       oUpdateMemcovDetailsTemp.AE_ContractId = '';
       oUpdateMemcovDetailsTemp.AE_DepSequence = '';
       oUpdateMemcovDetailsTemp.AE_SrcGrpId = '';
       oUpdateMemcovDetailsTemp.AE_SrcPlatformCd = '';
       oUpdateMemcovDetailsTemp.AE_SubscrSequence = '';
       oUpdateMemcovDetailsTemp.Benefit = '';
       oUpdateMemcovDetailsTemp.CusCovSeq = '';
       oUpdateMemcovDetailsTemp.CusGenKey = '';
       oUpdateMemcovDetailsTemp.DepDOB = '';
       oUpdateMemcovDetailsTemp.DepFirstName = '';
       oUpdateMemcovDetailsTemp.DepLastName = '';
       oUpdateMemcovDetailsTemp.DepSSN = Label.HUMConstantZero;
       oUpdateMemcovDetailsTemp.EffDate = '';
       oUpdateMemcovDetailsTemp.EndDate = '';
       oUpdateMemcovDetailsTemp.MbrTermDate = '';
       oUpdateMemcovDetailsTemp.MbrTermReason = '';
       oUpdateMemcovDetailsTemp.Product = '';
       oUpdateMemcovDetailsTemp.RecordSeparator = '';
       oUpdateMemcovDetailsTemp.Status = '';
       oUpdateMemcovDetailsTemp.SubFirstName = '';
       oUpdateMemcovDetailsTemp.SubLastName = '';
       oUpdateMemcovDetailsTemp.SubSSN = Label.HUMConstantZero;
       oUpdateMemcovDetailsTemp.SubscriberInd = '';
       return oUpdateMemcovDetailsTemp;
    }
    /*
    * Method Name   :    getInputUpdateMemAddrDetailsDTO
    * Description   :    To get successful response from service passing
                        blank values to non mandatory input and valid inputs to mandatory fields
    * Return Type   :    HUMVerifyFamilyCOBService.UpdatedMemberAddressDetailsDTO 
    * Parameter     :    NA
    */
    private HUMVerifyFamilyCOBService.UpdatedMemberAddressDetailsDTO getInputUpdateMemAddrDetailsDTO()
    {
       HUMVerifyFamilyCOBService.UpdatedMemberAddressDetailsDTO oUpdateMemAddrDetailsTemp = new HUMVerifyFamilyCOBService.UpdatedMemberAddressDetailsDTO();
       oUpdateMemAddrDetailsTemp.Addr1 = '';
       oUpdateMemAddrDetailsTemp.Addr2 = '';
       oUpdateMemAddrDetailsTemp.City = '';
       oUpdateMemAddrDetailsTemp.FirstName = '';
       oUpdateMemAddrDetailsTemp.HomeEmail  = '';
       oUpdateMemAddrDetailsTemp.HomePhone = '';
       oUpdateMemAddrDetailsTemp.LastName = '';
       oUpdateMemAddrDetailsTemp.MemGenKey = '';
	   oUpdateMemAddrDetailsTemp.EnterpriseId = '';
       oUpdateMemAddrDetailsTemp.MiddleInitial = '';
       oUpdateMemAddrDetailsTemp.State = ''; 
       oUpdateMemAddrDetailsTemp.WorkEmail = '';
       oUpdateMemAddrDetailsTemp.WorkPhone = '';
       oUpdateMemAddrDetailsTemp.WorkPhoneExt = '';
       oUpdateMemAddrDetailsTemp.ZipPlusCd = '';
       oUpdateMemAddrDetailsTemp.Zip = ''; 
       return oUpdateMemAddrDetailsTemp; 
    }
    /*
    * Method Name   :    getLastVerifyDates()
    * Description   :    To Trigger CoreApiServiceREST GetVerifyDemographics service and fetch lastVerifyDates
    * Return Type   :    Continuation 
    * Parameter     :    NA
    */
    public Continuation getLastVerifyDatesRest()
    {	
        getPolicyMembers();
     	Continuation conGo = new Continuation(120);
     	if(string.isNotBlank(sAccId))
        {
            HUM_Webservice_Callout__c oGVDService = HUM_Webservice_Callout__c.getValues('GetVerifyDemographics');
            Integer iTIMEOUT_INT_SECS = Integer.valueof(oGVDService.Timeout__c)/1000;
            try
            {  
             	sBody = buildRequestGetVerifyDemographics(sAccId);
             	if(string.isNotBlank(sBody))
			    {
                    HttpRequest request = new HttpRequest();
                    request.setMethod(oGVDService.Request_Type__c); 
                    request.setHeader(GlobalCommonConstants_HUM.CONTENT_TYPE_HUM, oGVDService.Content_Type__c);  
                    request.setHeader(GlobalCommonConstants_HUM.SOAPACTION_HUM, '');    
                    request.setHeader(GlobalCommonConstants_HUM.CONNECTION_HUM,GlobalCommonConstants_HUM.KEEP_ALIVE_HUM);
                    request.setBody(sBody);
                    if(!Test.isRunningTest())    request.setClientCertificateName(oGVDService.Certificate_Name__c);    
                    request.setTimeout(Integer.valueof(oGVDService.Timeout__c));
                    request.setEndpoint(oGVDService.End_Point_URL__c);
                    conGo.continuationMethod = 'processGetVerifyDemographicsResponse';
                    conGo.State = sAccId;
                    this.sRequest = conGo.addHttpRequest(request);
                    if(Test.isRunningTest())  throw new HUMCustomException('Error');
			    }
            }
			Catch(exception ex)
			{
				HUMExceptionHelper.logErrors(ex, 'HUMVerifyFamilyServiceController_R1', 'getLastVerifyDates');
                return null;
			}
        }
     	return conGo;
    }

    /*
    *  This method is used to build the required inputs needed for VerifyDemographics service
    *  @name   buildRequestGetVerifyDemographics
    *  @param  string sRecordId -  Account to get data from service
    *  @return string - Returns the Request body in Serialized form
    */
    @testvisible private string buildRequestGetVerifyDemographics(string sRecordId)
    {   
        string sJsonBodyGo = '';
        try
		{
            if(string.isNotBlank(sRecordId))
            { 
                Account objAccount = [Select id,Enterprise_ID__c from Account where id =:sRecordId LIMIT 1];
                VerifyDemographicGetRequest_DTO_HUM oRequest = new VerifyDemographicGetRequest_DTO_HUM();
                VerifyDemographicGetRequest_DTO_HUM.GetverificationstatusRequest objGetVerificationStatus =  
                    new VerifyDemographicGetRequest_DTO_HUM.GetverificationstatusRequest();
                	objGetVerificationStatus.PersonId = (objAccount.Enterprise_ID__c != Null) ? objAccount.Enterprise_ID__c : '';
                VerifyDemographicGetRequest_DTO_HUM.RequestHeader objGetRequestHeader = 
                    new VerifyDemographicGetRequest_DTO_HUM.RequestHeader();
                	objGetRequestHeader.RequestID = String.valueOf((Math.random() * 100));
                	objGetRequestHeader.SessionID = UserInfo.getSessionId();
                oRequest.GetverificationstatusRequest = objGetVerificationStatus;
                oRequest.RequestHeader = objGetRequestHeader;
                sJsonBodyGo = JSON.serialize(oRequest);
		    }
            else    sJsonBodyGo =  null;
        }
	    catch(exception ex)
		{
			HUMExceptionHelper.logErrors(ex, 'HUMVerifyFamilyServiceController_R1', 'processGetLastVerifyDates');
            sJsonBodyGo  =  NULL;
	    }
        return sJsonBodyGo ;
    }
    
    /*
    *  This method is used to Parse response from getVerifyDemographics service
    *  @name   processGetVerifyDemographicsResponse
    *  @param  NA
    *  @return void
    */
    @testvisible
    public void processGetVerifyDemographicsResponse()  
    { 
        List<VerifyDemographicGetWrapper_DTO_HUM> lstVerifyDemographic = new List<VerifyDemographicGetWrapper_DTO_HUM >();
        try
        {        
            HttpResponse sResponse = Continuation.getResponse(this.sRequest);
            if ((sResponse != null && !sResponse.getBody().containsIgnoreCase(GlobalCommonConstants_HUM.VOB_FAULT_CODE) && 
                (sResponse.getStatusCode() == GlobalCommonConstants_HUM.HTTP_OK_HUM || sResponse.getStatusCode() == GlobalCommonConstants_HUM.HTTP_ACCEPTED_HUM)) || Test.isRunningTest())
            {
                VerifyDemographicGetResponse_DTO_HUM objResponse = new VerifyDemographicGetResponse_DTO_HUM();
                objResponse = (VerifyDemographicGetResponse_DTO_HUM)System.JSON.deserialize(sResponse.getBody(),VerifyDemographicGetResponse_DTO_HUM.class); 
                if (objResponse != null){ 
                    lstVerifyDemographic = processResponse(objResponse);
                } 
            }
            if(sResponse != null && sResponse.getBody().containsIgnoreCase(GlobalCommonConstants_HUM.VOB_FAULT_CODE)){
            	processDefaultVerifyResponse();
            }      
        }        
        catch(Exception ex) {
            HUMExceptionHelper.bShowErrorOnPage = false;
            HUMExceptionHelper.bRestrictLog = true;
            HUMExceptionHelper.bShowErrorOnPage = true;
            HUMExceptionHelper.logErrors(ex, 'HUMVerifyFamilyServiceController_R1', 'processGetVerifyDemographicsResponse');
        }
    }
	
    /**
    * This method generates list of VerifyDemographic details.
    * @Nmae     processResponse
    * @param    VerifyDemographicResponse_DTO_HUM response
    * @return   List of VerifyDemographicWrapper_DTO_HUM
    */
    public list<VerifyDemographicGetWrapper_DTO_HUM> processResponse(VerifyDemographicGetResponse_DTO_HUM resVerifyDemographic)
    {
        try
        {
            lstVerifyDemoWrapper.clear();
            VerifyDemographicGetWrapper_DTO_HUM objVerifyDemoWrapper =new VerifyDemographicGetWrapper_DTO_HUM();
            VerifyDemographicGetResponse_DTO_HUM.GetverificationstatusResponse objgetverification = 
                resVerifyDemographic.GetverificationstatusResponse;
            objVerifyDemoWrapper.iDaysSinceLastVerified = (NULL != objgetverification.demographics.lastVerifiedDate) ? Date.valueOf(objgetverification.demographics.lastVerifiedDate).daysBetween(system.Today()) : -1;
            if(objgetverification.demographics.verifiedBy != Null){
                List<User> lstUser = [Select Name,Network_User_Id__c from User where ( Network_User_Id__c  =: objgetverification.demographics.verifiedBy OR Name =: objgetverification.demographics.verifiedBy) Limit 1];    
                objVerifyDemoWrapper.sLastVerifiedBy = '';
                if(lstUser != null && !lstUser.isEmpty()){
                    objVerifyDemoWrapper.sLastVerifiedBy = (NULL != lstUser[0].Name) ? lstUser[0].Name : lstUser[0].Network_User_Id__c;    
                }
                if(lstUser.isEmpty()){
                    objVerifyDemoWrapper.sLastVerifiedBy = objgetverification.demographics.verifiedBy;    
                }
            }
            if(objgetverification.demographics.lastVerifiedDate != Null){
            	objVerifyDemoWrapper.sLastVerifiedOn = '';
                List <String> splitDateTime = objgetverification.demographics.lastVerifiedDate.split('T');
                List <String> splitDates = splitDateTime[0].split('-');
                List <String> splitTime = splitDateTime[1].split(':');
                
                DateTime vDateTime = DateTime.newInstance(Integer.valueOf(splitDates[0]), Integer.valueOf(splitDates[1]), Integer.valueOf(splitDates[2]), Integer.valueOf(splitTime[0]), 0, 0);
                String formattedDateTime = vDateTime.format('EEE MMM dd hh:mm:ss');
                objVerifyDemoWrapper.sLastVerifiedOn = formattedDateTime +' GMT '+Integer.valueOf(splitDates[0]); 
            }
            bVerifyMessage = (objVerifyDemoWrapper.iDaysSinceLastVerified > 90) ? true : false;
            lstVerifyDemoWrapper.add(objVerifyDemoWrapper);
            if(bVerify){	
                if(!OnlyHumanaPolicies() && !OnlyCbisPolicies()) throw new HUMCustomException(system.Label.HUMHumanaCBISPolicies);
            }
            return lstVerifyDemoWrapper;
        }
        catch(Exception ex) {
            HUMExceptionHelper.bShowErrorOnPage = false;
            HUMExceptionHelper.bRestrictLog = true;
            HUMExceptionHelper.bShowErrorOnPage = true;
            HUMExceptionHelper.logErrors(ex, 'HUMVerifyFamilyServiceController_R1', 'processResponse');
            return null;
        }
    }
    
    /**
    * This method generates list of VerifyDemographic with default details.
    * @Name     processDefaultVerifyResponse
    * @param    VerifyDemographicResponse_DTO_HUM response
    * @return   List of VerifyDemographicWrapper_DTO_HUM
    */
    public list<VerifyDemographicGetWrapper_DTO_HUM> processDefaultVerifyResponse()
    {
        try
        {
            VerifyDemographicGetWrapper_DTO_HUM objVerifyDemoWrapper =new VerifyDemographicGetWrapper_DTO_HUM();
            objVerifyDemoWrapper.sLastVerifiedBy = '';
            String defaultGMT = '1-01-01T00:00:00.000Z';
            if(defaultGMT != Null){ 
                List <String> splitDateTime = defaultGMT.split('T');
                List <String> splitDates = splitDateTime[0].split('-');
                List <String> splitTime = splitDateTime[1].split(':');
                
                DateTime vDateTime = DateTime.newInstance(Integer.valueOf(splitDates[0]), Integer.valueOf(splitDates[1]), Integer.valueOf(splitDates[2]), Integer.valueOf(splitTime[0]), 0, 0);
                String formattedDateTime = vDateTime.format('EEE MMM dd HH:mm:ss');
                objVerifyDemoWrapper.sLastVerifiedOn = formattedDateTime +' GMT '+Integer.valueOf(splitDates[0]); 
            }
            objVerifyDemoWrapper.iDaysSinceLastVerified = (NULL != defaultGMT) ? Date.valueOf(defaultGMT).daysBetween(system.Today()) : -1;
         	bVerifyMessage = (objVerifyDemoWrapper.iDaysSinceLastVerified > 90) ? true : false;
          	lstVerifyDemoWrapper.add(objVerifyDemoWrapper);
            if(bVerify){	
                if(!OnlyHumanaPolicies() && !OnlyCbisPolicies()) throw new HUMCustomException(system.Label.HUMHumanaCBISPolicies);
            }
            return lstVerifyDemoWrapper;
        }
        catch(Exception ex) {
            HUMExceptionHelper.bShowErrorOnPage = false;
            HUMExceptionHelper.bRestrictLog = true;
            HUMExceptionHelper.bShowErrorOnPage = true;
            HUMExceptionHelper.logErrors(ex, 'HUMVerifyFamilyServiceController_R1', 'processDefaultVerifyResponse');
            return null;
        }
    }

    /*
    * Method Name   :    verifyDemographics
    * Description   :    To Trigger CoreApiServiceREST updateVerifyDemographics service and to Update Demographics
    * Return Type   :    Continuation
    * Parameter     :    NA
    */
    public Continuation verifyDemographicsRest()
    {	
        HUM_Webservice_Callout__c oUVDService = HUM_Webservice_Callout__c.getValues('UpdateVerifyDemographics');
        Integer iTIMEOUT_INT_SECS = Integer.valueof(oUVDService.Timeout__c)/1000;
        Continuation conGo = new Continuation(iTIMEOUT_INT_SECS);
     	if(string.isNotBlank(sAccId))
        {
            try
        	{  	
            	sBody = buildRequestUpdateVerifyDemographics(sAccId);
            	if(string.isNotBlank(sBody))
                {
                    HUM_CBIS_Enabled__c oHUMCbisFlag = HUM_CBIS_Enabled__c.getValues('HUM_CBIS_Flag');
        			if(oHUMCbisFlag.CBIS_Flag__c) bVerify = true;
                    HttpRequest request = new HttpRequest();
                    request.setMethod(oUVDService.Request_Type__c); 
                    request.setHeader(GlobalCommonConstants_HUM.CONTENT_TYPE_HUM, oUVDService.Content_Type__c);  
                    request.setHeader(GlobalCommonConstants_HUM.SOAPACTION_HUM, '');    
                    request.setHeader(GlobalCommonConstants_HUM.CONNECTION_HUM,GlobalCommonConstants_HUM.KEEP_ALIVE_HUM);
                    request.setBody(sBody);
                    if(!Test.isRunningTest())    request.setClientCertificateName(oUVDService.Certificate_Name__c);    
                    request.setTimeout(Integer.valueof(oUVDService.Timeout__c));
                    request.setEndpoint(oUVDService.End_Point_URL__c);
                    conGo.continuationMethod = 'processUpdateVerifyDemographicsResponse';
                    conGo.State = sAccId;
                    conGo.addHttpRequest(request);
                    this.sUpdateRequest = conGo.addHttpRequest(request);
                    if(Test.isRunningTest())  throw new HUMCustomException('Error');
                }     
             	else
                {
                	return null;
                }
			}
		    catch(exception ex)
			{
				HUMExceptionHelper.bShowErrorOnPage = false;
				HUMExceptionHelper.bRestrictLog = true;
				HUMExceptionHelper.bShowErrorOnPage = true;
				HUMExceptionHelper.logErrors(ex, 'HUMVerifyFamilyServiceController_R1', 'verifyDemographics');
				return null;
			}
        }
        return conGo;	
    } 
    
    /* 
    *  This method is used to build the required inputs needed for VerifyDemographics service
    *  @name   buildRequestUpdateVerifyDemographics
    *  @param  string sRecordId -  Account to get data from service
    *  @return string - Returns the Request body in Serialized form
    */ 
    @testvisible private string buildRequestUpdateVerifyDemographics(string sAccId)
    {
        string sJsonBodyGo = '';
        try
        {
            if(string.isNotBlank(sAccId))
            { 	
                List<String> lstEnterpriseIds = new List<String>();
                VerifyDemographicUpdateRequest_DTO_HUM oRequest = new VerifyDemographicUpdateRequest_DTO_HUM();
                VerifyDemographicUpdateRequest_DTO_HUM.RequestHeader objRequestHeader = 
                    new VerifyDemographicUpdateRequest_DTO_HUM.RequestHeader();
                    objRequestHeader.RequestID = String.valueOf((Math.random() * 100));
                    objRequestHeader.SessionID = UserInfo.getSessionId();
                oRequest.RequestHeader = objRequestHeader;
                VerifyDemographicUpdateRequest_DTO_HUM.PutverificationstatusRequest objPutVerificationStatusRequest = 
                    new VerifyDemographicUpdateRequest_DTO_HUM.PutverificationstatusRequest();
                VerifyDemographicUpdateRequest_DTO_HUM.Demographics objDemographics =
                    new VerifyDemographicUpdateRequest_DTO_HUM.Demographics();
                	objDemographics.source = 'CRM';
                    objDemographics.lastVerifiedDate = String.valueOf(system.now().format('yyyy-MM-dd\'T\'HH:mm:ss.SSS\'Z\''));
                	Id userID =  UserInfo.getuserID();
    				List<User> lstUser = [Select Name,Network_User_Id__c from User where Id =: userID Limit 1];
    				objDemographics.verifiedBy = '';		
             		if(lstUser != null && !lstUser.isEmpty()){
                    	objDemographics.verifiedBy = (NULL != lstUser[0].Name) ? lstUser[0].Name : lstUser[0].Network_User_Id__c;        
                    }
             	objPutVerificationStatusRequest.demographics = objDemographics;
                
             	List<Account> lstAccount = [Select id,Enterprise_ID__c from Account where id =:sAccId LIMIT 1];
                if(lstAccount != null && !lstAccount.isEmpty()){
                	lstEnterpriseIds.add(lstAccount[0].Enterprise_ID__c);
                }
                objPutVerificationStatusRequest.PersonId = lstEnterpriseIds;
                
                oRequest.PutverificationstatusRequest = objPutVerificationStatusRequest;
                sJsonBodyGo = JSON.serialize(oRequest);
                return sJsonBodyGo;    
            }
            else    sJsonBodyGo =  null;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'HUMVerifyFamilyServiceController_R1', 'buildRequestUpdateVerifyDemographics');
            sJsonBodyGo  =  NULL;
        }
        return sJsonBodyGo ;
    }   
           
    /*
    *  This method is used to Parse response from VerifyDemographics service
    *  @name   processVerifyDemographicsResponse
    *  @param  List and object
    *  @return object
    */
    @testvisible
    public object processUpdateVerifyDemographicsResponse()
    { 
    	try{        
            HttpResponse sResponse = Continuation.getResponse(this.sUpdateRequest);
            if ((sResponse != null && !sResponse.getBody().containsIgnoreCase(GlobalCommonConstants_HUM.VOB_FAULT_CODE) && 
                (sResponse.getStatusCode() == GlobalCommonConstants_HUM.HTTP_OK_HUM || sResponse.getStatusCode() == GlobalCommonConstants_HUM.HTTP_ACCEPTED_HUM)) || Test.isRunningTest())
            {       
                VerifyDemographicUpdateResponse_DTO_HUM objResponse = new VerifyDemographicUpdateResponse_DTO_HUM();
                objResponse = (VerifyDemographicUpdateResponse_DTO_HUM)System.JSON.deserialize(sResponse.getbody(),VerifyDemographicUpdateResponse_DTO_HUM.class); 
                if (objResponse != null)
                { 
                    if(objResponse.PutVerificationStatusResponse.Status == 'success'){
                        return getLastVerifyDatesRest();
                    }
                    if(objResponse.PutVerificationStatusResponse.Status == 'failure'){
                        return null;
                    }
                }
            }
        }
        catch(Exception ex) {
            HUMExceptionHelper.bShowErrorOnPage = false;
            HUMExceptionHelper.bRestrictLog = true;
            HUMExceptionHelper.bShowErrorOnPage = true;
            HUMExceptionHelper.logErrors(ex, 'HUMVerifyFamilyServiceController_R1', 'processUpdateVerifyDemographicsResponse');
            return null;
        }
        return getLastVerifyDatesRest();
    }
 }