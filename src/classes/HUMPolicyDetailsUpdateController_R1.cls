/*******************************************************************************************************************************
Apex Class Name : HUMPolicyDetailsUpdateController_R1
Version         : 1.0
Created On      : 08/18/2014
Function        : This class makes call to GBE to update the policy exchange details when policy member is loaded.
                  This class should be WITHOUT SHARING as the the policy exchange details need to be update on every policy member load. 
Test Class      : HUMPolicyDetailsUpdateControllerTest                  
Modification Log: 
 * Developer Name           Review Number                      Date                       Description
 *------------------------------------------------------------------------------------------------------------------------------
 * Anil Pilaka                   17432                       08/18/2014                 Original Version
 * Anil Pilaka                   18484                       10/13/2014                 Updated code to add the only call to GBE based on custom setting
 * Anil Pilaka                   18581                       10/15/2014                 Added logic to send the Subgroup Id, benefit packagage Id and benefit sequence # 
                                                                                        and subgroup Id based on policy platform.
 * Anil Pilaka                   18581                       10/16/2014                 Created a new method to invokeGBE to make more readable  
 * Chaitanya Kumar               18889                       10/29/2014                 Updated code for Defect -159338.0001,159337.0001 
 * Shruthi Karanth               18973                       11/04/2014                 Updated code for Defect -159338.0001
 * Shruthi Karanth               19008                       11/05/2014                 Updated the condition to send Sub Group Id     
 * Sagar Tapadia                 19069                       11/06/2014                 Updated code to default value of Policy related fields to None or blank if there
                                                                                        is exception or response is null 
 * Chaitanya Kumar               20146                       12/30/2014                 Fixed Defect 166216
 * Chaitanya Kumar               20146                       06/01/2015                 Updated as per review comments # 20146   
 * Chaitanya Kumar                                           06/27/2016                 Modified line#100 & 101 to return new page reference                                          
 * Subhamay Mandal                                           03/06/2017                 REQ - Rerrange Policy Memeber Page
 * Siddharth Mehta                                           07/25/2017                 REQ - 323549 : SF - TECH - Remove AHT Components Dependencies (RF)
 * Santhi Mandava                                            03/21/2018                 REQ - 355953 Removed "detokenizeResults" method and corrected code accordingly.
 * Rakesh Patni                  243679                      06/14/2018                 REQ - 361927 - Logging functionality implementation
 * Riddhi Mittal											 02/08/2020					CCP functionality removed - Group search Service and add GBE service
 *******************************************************************************************************************************/

public without sharing class HUMPolicyDetailsUpdateController_R1
{

    private String sNoneValue = HUMConstants__c.getInstance('NONE').Stringvalue__c;
    private String sClassName = 'HUMPolicyDetailsUpdateController';
    private String sAppName = HUMConstants__c.getInstance('GRPSVC_CLIENT_NAME').Stringvalue__c ;   
    private String sMethodName = ''; 
    private String sNullValue = HUMConstants__c.getInstance('UNINITIALIZED_VALUE').Stringvalue__c;  
    private String sLVPlatForm =  HUMConstants__c.getInstance('LV_PLATFORM').Stringvalue__c;
    private String sEMPlatForm =  HUMConstants__c.getInstance('GRPSVC_PLATFORM').Stringvalue__c;
    public Policy_Member__c policyMember = new Policy_Member__c();
    private String sExchange='';
    private String sExchangeType='';
    private String sMetallicTier= '';
    private String sExchangeSegment='';
    private String sProductSegment='';
    private boolean bIsHumanaOne=false;
	 //start
    @TestVisible private String sRequest ='';
     private Policy_Plan_Details__c policyPlan = new Policy_Plan_Details__c();
     @TestVisible private PolicyDetailsUpdate_S_HUM oServicePolicyMem = new PolicyDetailsUpdate_S_HUM ();
      public GBEServiceResponse_H_HUM resObj=null;
      Public Boolean ccpOnOff {get; set;}
	  //end

    /*
     * Name          :    HUMPolicyDetailsUpdateController - Constructor with Standard Controller
     * Description   :    Initialize few variables
     * Return Type   :    NA
     * Parameters    :    ApexPages.StandardController 
     */ 
    public HUMPolicyDetailsUpdateController_R1(ApexPages.StandardController controller) 
    {  
	   ccpOnOff = false;
        // on/off switch functionality for ccp
        ccpOnOff = HUMUtilityHelper.isCRMFunctionalityON('1185085');
        if(ccpOnOff)
        {
        String PolMemId = ApexPages.currentPage().getParameters().get('id'); 
        if(PolMemId!= null )
        {
            policyMember =[select id, name,Effective_Date__c,End_Date__c,Policy__r.Product__r.Sold_Product_Key_Value__c,Policy__r.Group_Name__r.Source_Customer_Key__c,Policy__r.Platform__c,Policy__r.Source_Cust_Cov_Key__c,Group_Number__c,Policy__r.Last_GBE_Call_Date__c,Policy__c,Policy__r.Group_Name__c from Policy_Member__c where id =:PolMemId limit 1 ];
        }
        }
		else{
        policyMember = (Policy_Member__c)controller.getRecord(); 
		}
    }

    /*
     * Method Name   :    updatePolicyDetails
     * Description   :    This method makes call to GBE when the policy member page is loaded and when the EXCHANGE INDICATOR is TRUE
     * Return Type   :    Pagereference
     * Parameters    :    NA
     */
    public PageReference updatePolicyDetails()
    {
        sMethodName = 'updatePolicyDetails';
        
        // aht 03062017 original pagelayout
        Pagereference pageRefePolicyPage = new Pagereference('/apex/PolicyMemberDetail_VF_HUM?id='+policyMember.id + '&type=Policy Member&subtype=Policy Member Detail');
        // eoc
		 if(ccpOnOff == false)
         {
        try
        { 
            policyMember = [select id,Effective_Date__c,End_Date__c,Policy__r.Product__r.Sold_Product_Key_Value__c,Policy__r.Group_Name__r.Source_Customer_Key__c,Policy__r.Platform__c,Policy__r.Source_Cust_Cov_Key__c,Group_Number__c,Policy__r.Last_GBE_Call_Date__c,Policy__c,Policy__r.Group_Name__c from Policy_Member__c where id=:policyMember.id];
            /*Make call to GBE if the group number is not empty and the last GBE call date is less than
              today + # Days in Custom setting.
             */                           
            if(policyMember.Group_Number__c <> null && 
                    (policyMember.Policy__r.Last_GBE_Call_Date__c == null ||
                    policyMember.Policy__r.Last_GBE_Call_Date__c.adddays(Integer.valueof(HUMConstants__c.getInstance('GROUP_GBE_CALL_DAYS').Integervalue__c)) < system.today()))
            {
                HUMIntGroupSearchServiceHelper.ArrayOfGroupExchangeInfoDetailDTO respvar =  invokeGBEService(policyMember.Group_Number__c,policyMember);
                if(respvar<>null)
                {
                    if(respvar.GroupExchangeInfoDetailDTO<>null && respvar.GroupExchangeInfoDetailDTO.size()>0)
                    {   
                        //Based on response updating Policy details.
                        updateGBEPolicyData(respvar.GroupExchangeInfoDetailDTO[0]);
                    } 
                }
                else
                {
                    //If response is NULL then update policy with None or blank value for fields
                    updatePolicy(false);
                }              
            }
        }
        catch(Exception ex)
        { 
            HUMExceptionHelper.logErrors(ex,sClassName,sMethodName);
            //In case of exception update policy with None or blank value for fields
            updatePolicy(false);
        }
		}
        return pageRefePolicyPage;         
    }

    /*
     * Method Name   :    invokeGBEService
     * Description   :    This method updates the metallic tier, exchange name, exchange type and business segment fields on policy.
     * Return Type   :    HUMIntGroupSearchServiceHelper.ArrayOfGroupExchangeInfoDetailDTO
     * Parameters    :    String group If and Policy_Member__c
     */      

    public HUMIntGroupSearchServiceHelper.ArrayOfGroupExchangeInfoDetailDTO invokeGBEService(String groupId,Policy_Member__c policyMember){

        HUMIntGroupSearchServiceHelper.RequestGroupExchangeDTO  gbeData = new HUMIntGroupSearchServiceHelper.RequestGroupExchangeDTO();
        HUMIntGroupSearchServiceHelper.ResponseGroupExchangeDTO gbeRespData = new HUMIntGroupSearchServiceHelper.ResponseGroupExchangeDTO();
        HUMIntGroupSearchServiceHelper.GroupExchangeInfoDetailDTO finalGBEData = new HUMIntGroupSearchServiceHelper.GroupExchangeInfoDetailDTO();
        HUMIntGroupSearchServiceHelper.GroupExchangeDetailDTO groupData = new HUMIntGroupSearchServiceHelper.GroupExchangeDetailDTO();
        HUMIntGroupSearchServiceHelper.ArrayOfGroupExchangeDetailDTO exchangeData = new HUMIntGroupSearchServiceHelper.ArrayOfGroupExchangeDetailDTO();
        HUMIntGroupSearchServiceHelper.GroupExchangeDetailDTO[] GroupExchangeDetailDTO = new HUMIntGroupSearchServiceHelper.GroupExchangeDetailDTO[]{};

        gbeData.ClientName = sAppName ;
        gbeData.ApplicationUser = sAppName;
        groupData.Platform = policyMember.Policy__r.Platform__c;
        //For LV platform the first 6 characters of Source Cust Cov Key will be the Group ID              
        groupData.GroupId = (groupData.Platform == sLVPlatForm ? policyMember.Policy__r.Source_Cust_Cov_Key__c.subString(0,6):groupId);
        //For EM platform the first 8 characters of Soure Cust Cov Key is the subgroup ID and for LV the characters from 9-12 act as the sub group ID

        groupData.SubGroupId = (groupData.Platform == sEMPlatForm ? policyMember.Policy__r.Source_Cust_Cov_Key__c.subString(0,8) : groupData.Platform == sLVPlatForm ? policyMember.Policy__r.Source_Cust_Cov_Key__c.subString(9,12):'');  
        //For LV platform the characters from 6-9 of source cust cov key acts the benefit sequence number
        groupData.BenefitSequenceNumber = (groupData.Platform == sLVPlatForm ? policyMember.Policy__r.Source_Cust_Cov_Key__c.subString(6,9) : ''); 
        groupData.BenefitPackageId = (groupData.Platform == sEMPlatForm ? policyMember.Policy__r.Product__r.Sold_Product_Key_Value__c : ''); 
        groupData.StartDate = (policyMember.Effective_Date__c <> null ? policyMember.Effective_Date__c : sNoneValue);
        groupData.EndDate = policyMember.End_Date__c <> null ? policyMember.End_Date__c : sNoneValue;

        GroupExchangeDetailDTO.add(groupData);
        exchangeData.GroupExchangeDetailDTO = GroupExchangeDetailDTO;
        gbeData.GroupExchangeDetailList = exchangeData ; 

        HUMIntGroupSearchService.soap requestMsg = (HUMIntGroupSearchService.soap)HUMSearchHelper.getServiceRequestInstance('GroupSearchService',
                'HUMIntGroupSearchService.soap');       
        gbeRespData  = requestMsg.GetGBEInfo(gbeData);         
        return gbeRespData<>null?gbeRespData.GroupExchangeInfoDetailList:null;
    }    

    /*
     * Method Name   :    updateGBEPolicyData
     * Description   :    This method calls the group service to fetch the exchange and segment details
     * Return Type   :    Void
     * Parameters    :    HUMIntGroupSearchServiceHelper.GroupExchangeInfoDetailDTO
     */

    public void updateGBEPolicyData(HUMIntGroupSearchServiceHelper.GroupExchangeInfoDetailDTO gbeData){

        if(gbeData!= null)
        {
            sExchange = gbeData.ExchangeName != null ? (gbeData.ExchangeName.equalsIgnoreCase(sNullValue) ? sNoneValue : gbeData.ExchangeName) : sNoneValue ;
            sExchangeType =  gbeData.ExchangeType != null ? (gbeData.ExchangeType.equalsIgnoreCase(sNullValue) ? '' : gbeData.ExchangeType) : '' ;
            sMetallicTier =  gbeData.MetallicTier != null ? (gbeData.MetallicTier.equalsIgnoreCase(sNullValue) ? sNoneValue : gbeData.MetallicTier) : sNoneValue ;
            sExchangeSegment = (gbeData.ExchangeSegment <> null && gbeData.ExchangeSegment<>''? gbeData.ExchangeSegment:'');
            sProductSegment = (gbeData.ProductSegment <> null && gbeData.ProductSegment<>''? gbeData.ProductSegment:'');
            bIsHumanaOne = (gbeData.IsHumanaOne != null ? gbeData.IsHumanaOne:false);
            updatePolicy(true);
        }
    }    

    /*
     * Method Name   :    updatePolicy
     * Description   :    This method updates Policy based on boolean check
     * Return Type   :    Void
     * Parameters    :    boolean
     */
    public void updatePolicy(boolean bCheck)
    {
        List<Policy__c> lstPolicies = new list<Policy__c>();
        Policy__c policyDetails = new Policy__c(id=policyMember.Policy__c,           
                Exchange__c = bCheck==true?sExchange:sNoneValue,
                        Exchange_Type__c = bCheck==true?sExchangeType:'',
                                Metallic_Tier__c = bCheck==true?sMetallicTier:sNoneValue,
                                        Last_GBE_Call_Date__c = bCheck==true?system.today():null,
                                                Business_Segment__c =  bCheck == true?sExchangeSegment+'-'+sProductSegment:'');   
        lstPolicies.add(policyDetails);            

        Database.SaveResult[] saveResults = Database.update(lstPolicies, false);
        boolean isError = HUMExceptionHelper.processSaveResults(saveResults, lstPolicies, 
                'HUMPolicyDetailsUpdateController', 'updatePolicy', 'Policy__c');

        if(!isError)
        {
            List<Account> groupAccount =[Select id ,Humana_One_Indicator__c from Account where Id=:policyMember .Policy__r.Group_Name__c];
            if(groupAccount!=null && groupAccount.size()>0)
            {
                groupAccount[0].Humana_One_Indicator__c=bIsHumanaOne;
                saveResults = Database.update(groupAccount, false);
                isError = HUMExceptionHelper.processSaveResults(saveResults, groupAccount, 
                        'HUMPolicyDetailsUpdateController', 'updatePolicy', 'Account');

            }
        }
    }
	
	//Start
     /*
     * Method Name   :    getGBEInformationAsync
     * Description   :    This method makes call to GBE when the policy member page is loaded and when the EXCHANGE INDICATOR is TRUE
     * Return Type   :    Continuation
     * Parameters    :    NA
     */
    public Object getGBEInformationAsync()
    { 
      
            Pagereference pageRef = new Pagereference('/apex/PolicyMemberDetail_VF_HUM?id='+policyMember.id + '&type=Policy Member&subtype=Policy Member Detail');
     
            policyMember = [select id,
                            Effective_Date__c,
                            End_Date__c,
                            Policy__r.Product__r.Sold_Product_Key_Value__c,
                            Policy__r.Group_Name__r.Source_Customer_Key__c,
                            Policy__r.Platform__c,
                            Policy__r.Source_Cust_Cov_Key__c,
                            Group_Number__c,
                            Policy__r.Last_GBE_Call_Date__c,
                            Policy__c,
                            Policy__r.Group_Name__c 
                            from Policy_Member__c 
                            where 
                            id=:policyMember.id];
            List<String> policyStartDateSplit = new List<String>();
            if(String.isNotBlank(policyMember.Effective_Date__c))  
            {              
               policyStartDateSplit = policyMember.Effective_Date__c.split('/');
            }
            List<String> policyEndDateSplit =  policyMember.End_Date__c.split('/');
            if(!policyStartDateSplit.isEmpty())
            {
                Date policyStartDate = Date.newInstance(Integer.valueOf(policyStartDateSplit[2]),Integer.valueOf(policyStartDateSplit[0]),Integer.valueOf(policyStartDateSplit[1]));
            }
            Date policyEndDate =   Date.newinstance(Integer.valueOf(policyEndDateSplit[2]),Integer.valueOf(policyEndDateSplit[0]),Integer.valueOf(policyEndDateSplit[1]));
            Date todayDate = System.today();
            //Humana Defined End Date 
            Date fixedEndDate =Date.newInstance(9999,12,31);

            //Identify right Policy Plan record as per the policy member Selected
            for(Policy_Plan_Details__c policyPlanDetail : [select id,
                                                           Exchange__c,
                                                           Business_Segment__c,
                                                           Exchange_Indicator__c,
                                                           Exchange_Type__c,
                                                           Metallic_Tier__c,
                                                           Coverage_Plan_Effective_Date__c,
                                                           Coverage_Plan_End_Date__c,
                                                           Product__r.Sold_Product_Key_Value__c,
                                                           Policy__r.Group_Name__r.Source_Customer_Key__c,
                                                           Platform__c,Source_Cust_Cov_Key__c,
                                                           Group_Number__c,
                                                           Policy__r.Last_GBE_Call_Date__c,
                                                           Policy__c,Policy__r.Group_Name__c,
                                                           Last_GBE_Call_Date__c
                                                           from 
                                                           Policy_Plan_Details__c 
                                                           where 
                                                           Policy__c =: policyMember.policy__c and ETL_Record_Deleted__c=false ])
            {

                //Convert String to Date
                List<String> startDateSplit = policyPlanDetail.Coverage_Plan_Effective_Date__c.split('/');
                List<String> endDateSplit =  policyPlanDetail.Coverage_Plan_End_Date__c.split('/');
                Date startDate = Date.newInstance(Integer.valueOf(startDateSplit[2]),Integer.valueOf(startDateSplit[0]),Integer.valueOf(startDateSplit[1]));
                Date endDate =   Date.newInstance(Integer.valueOf(endDateSplit[2]),Integer.valueOf(endDateSplit[0]),Integer.valueOf(endDateSplit[1]));
                boolean policyPresent=false;
                //Business Rule to identify right policy plan record
                if((policyEndDate == fixedEndDate || policyEndDate >= todayDate) && (todayDate >= startDate && endDate >= todayDate))
                {
                    policyPresent=true;
                }
                else
                {
                    if(policyEndDate >= startDate && endDate >= policyEndDate)
                    {
                        policyPresent=true;
                    }
                }                 
                  if(policyPresent)
                {   
                    /*Make call to GBE if the group number is not empty and the last GBE call date is less than  today + # Days in Custom setting.   */ 
                    if(policyPlanDetail.Group_Number__c <> null && 
                            (policyPlanDetail.Last_GBE_Call_Date__c == null ||
                            policyPlanDetail.Last_GBE_Call_Date__c.adddays(Integer.valueof(HUMConstants__c.getInstance('GROUP_GBE_CALL_DAYS').Integervalue__c)) < system.today()))
                    {
                        policyPlan = policyPlanDetail;
                    }
                    break;
                }
                }
                if(policyPlan.id!=null)
                {
                    sRequest = '';
                    HUM_Webservice_Callout__c ServiceObj = HUM_Webservice_Callout__c.getValues('GroupBusinessEntityService');
                    Integer iTIMEOUT_INT_SECS = Integer.valueof(ServiceObj.Timeout__c)/1000;
                  
                    Continuation con = new Continuation(iTIMEOUT_INT_SECS );
                    con.continuationMethod='processResponse';
                    HttpRequest request = new HttpRequest();
                    oServicePolicyMem.setupRequest(request, ServiceObj,policyPlan,policyMember); 
                    sRequest = con.addHttpRequest(request);
                    return  con;
                }   
                else
                {
                    List<UserRecordAccess> userRecAccess =  [SELECT RecordId, HasReadAccess FROM UserRecordAccess WHERE UserId = :userinfo.getUserId() AND RecordId = :policyMember.id];
                    if(null != userRecAccess && userRecAccess.size() > 0 &&  userRecAccess[0].HasReadAccess)
                    {
                        pageRef = new Pagereference('/apex/PolicyMemberDetail_VF_HUM?id='+policyMember.id + '&type=Policy Member&subtype=Policy Member Detail');
                    }
                    return pageRef; 
                }  
                
    }
    
    public object processResponse()
    { 
       Pagereference pageRef = new Pagereference('/apex/PolicyMemberDetail_VF_HUM?id='+policyMember.id + '&type=Policy Member&subtype=Policy Member Detail');
        try 
        {
            resObj = new  GBEServiceResponse_H_HUM ();
            if(policyPlan.id!=null) 
            {
                HttpResponse response = Continuation.getResponse(sRequest); 
                if(response <> null && (response.getStatusCode() == 200 || response.getStatusCode() == 202)) 
                {
                resObj = (GBEServiceResponse_H_HUM)System.JSON.deserialize(response.getBody(),GBEServiceResponse_H_HUM.class);
                if(resObj<>null && resObj.GroupInfo<>null )
                    { 
                        if(resObj.GroupInfo.Exchange<>null)
                        {  
                            //Based on response updating Policy details.
                            updateGBEPolicyData11(resObj);
                        } 
                    }
                    else
                    {  
                        updateExchangeDetails(false);
                    } 
              } 
            }    
           List<UserRecordAccess> userRecAccess =  [SELECT RecordId, HasReadAccess FROM UserRecordAccess WHERE UserId = :userinfo.getUserId() AND RecordId = :policyMember.id];
           if(null != userRecAccess && userRecAccess.size() > 0 &&  userRecAccess[0].HasReadAccess)
           {
                pageRef = new Pagereference('/apex/PolicyMemberDetail_VF_HUM?id='+policyMember.id + '&type=Policy Member&subtype=Policy Member Detail');
           }
        }
        catch(Exception ex)
        {   
            HUMExceptionHelper.logErrors(ex,'HUMPolicyDetailsUpdateController','ProcessResponse');
            //In case of exception update policy with None or blank value for fields
            updateExchangeDetails(false);     
        }

         return pageRef;    
    }
    
     /*
     * Method Name   :    updateGBEPolicyData11
     * Description   :    This method calls the group service to fetch the exchange and segment details
     * Return Type   :    Void
     * Parameters    :    HUMIntGroupSearchServiceHelper.GroupExchangeInfoDetailDTO
     */
    public void updateGBEPolicyData11(GBEServiceResponse_H_HUM oResponse ){
        
        if(oResponse!= null)
        {
            sExchange = oResponse.GroupInfo.Exchange.ExchangeName != null ? (oResponse.GroupInfo.Exchange.ExchangeName.equalsIgnoreCase(sNullValue) || oResponse.GroupInfo.Exchange.ExchangeName=='' ? sNoneValue : oResponse.GroupInfo.Exchange.ExchangeName) : sNoneValue ;
            sExchangeType =  oResponse.GroupInfo.Exchange.ExchangeType != null ? (oResponse.GroupInfo.Exchange.ExchangeType.equalsIgnoreCase(sNullValue) ? '' : oResponse.GroupInfo.Exchange.ExchangeType) : '' ;
            sMetallicTier =  oResponse.GroupInfo.Exchange.MetallicTier != null ? (oResponse.GroupInfo.Exchange.MetallicTier.equalsIgnoreCase(sNullValue) || oResponse.GroupInfo.Exchange.MetallicTier=='' ? sNoneValue :oResponse.GroupInfo.Exchange.MetallicTier) : sNoneValue ;
            sExchangeSegment = (oResponse.GroupInfo.Exchange.ExchangeSegment <> null && oResponse.GroupInfo.Exchange.ExchangeSegment<>''? oResponse.GroupInfo.Exchange.ExchangeSegment:'');
            sProductSegment = (oResponse.GroupInfo.Exchange.LineOfBusiness <> null && oResponse.GroupInfo.Exchange.LineOfBusiness <>''? oResponse.GroupInfo.Exchange.LineOfBusiness:'');
            bIsHumanaOne = (oResponse.GroupInfo.Exchange.IsHumanaOne != null ? boolean.valueOf(oResponse.GroupInfo.Exchange.IsHumanaOne):boolean.valueOf(false));
            updateExchangeDetails(true);
        }
    }    

    /*
     * Method Name   :    updateExchangeDetails
     * Description   :    This method updates Policy based on boolean check
     * Return Type   :    Void
     * Parameters    :    boolean
     */
    public void updateExchangeDetails(boolean bCheck)
    {     
        Policy__c policyDetails = new Policy__c(id=policyMember.Policy__c,           
                Exchange__c = bCheck==true?sExchange:sNoneValue,
                        Exchange_Type__c = bCheck==true?sExchangeType:'',
                                Metallic_Tier__c = bCheck==true?sMetallicTier:sNoneValue,
                                        Last_GBE_Call_Date__c = bCheck==true?system.today():null,
                                                Business_Segment__c =  bCheck == true?sExchangeSegment+'-'+sProductSegment:'');   

        Database.SaveResult[] saveResults;
        saveResults  = Database.update(new List<Policy__c>{policyDetails}, false);
        boolean isError = HUMExceptionHelper.processSaveResults(saveResults, new List<Policy__c>{policyDetails}, 
                'HUMPolicyDetailsUpdateController', 'updatePolicy', 'Policy__c');

        if(!isError)
        {

            policyPlan.Exchange__c = policyDetails.Exchange__c;
            policyPlan.Exchange_Type__c = policyDetails.Exchange_Type__c;
            policyPlan.Metallic_Tier__c = policyDetails.Metallic_Tier__c;
            policyPlan.Last_GBE_Call_Date__c = policyDetails.Last_GBE_Call_Date__c;
            policyPlan.Business_Segment__c = policyDetails.Business_Segment__c;

            saveResults  = Database.update(new List<Policy_Plan_Details__c>{policyPlan}, false);
            isError = HUMExceptionHelper.processSaveResults(saveResults, new List<Policy_Plan_Details__c>{policyPlan}, 
                    'HUMPolicyDetailsUpdateController', 'updatePolicy', 'Policy_Plan_Details__c');

            if(!isError)    
            {

                List<Account> groupAccount =[Select id ,Humana_One_Indicator__c from Account where Id=:policyMember .Policy__r.Group_Name__c];
                if(groupAccount!=null && groupAccount.size()>0)
                {
                    groupAccount[0].Humana_One_Indicator__c=bIsHumanaOne;
                    saveResults = Database.update(groupAccount, false);
                    isError = HUMExceptionHelper.processSaveResults(saveResults, groupAccount, 
                            'HUMPolicyDetailsUpdateController', 'updatePolicy', 'Account');
                }
            } 
        } 
    } 
    
    //end
	
}