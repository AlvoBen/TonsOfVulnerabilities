/*******************************************************************************************************************************
Apex Class Name : HUMPolicyDetailsUpdateController 
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
 * Chaitanya Kumar               29201                       06/04/2016                 Updated code as per REQ - 269948 , Moved GBE to REST version and included Policy Plan Details object
 * Chaitanya Kumar               31157                       06/27/2016                 Updated code as per Defect #231481.0001. 
 * Avinash Choubey                                           10/14/2016                 Defect# 225798.002 fix.  added record access check before redirecting to PolicyMemberDetail_VF_HUM page.                
 * Pallavi R                     83046                       03/25/2017                 GBE service making two callouts 0 REq 304134
 * Prasanthi Kandula             105889                      06/28/2017                 Defect #298138.0001 Fix. Added null checks for effective date
 * Siddharth Mehta                                           07/25/2017                 REQ - 323549 : SF - TECH - Remove AHT Components Dependencies (RF)
 * Prudhvi Pamarthi              220819                      04/26/2018                 REQ - 359033: Fix for CA Ticket-7413702
 * Prudhvi Pamarthi              223563                      05/03/2018                 Defect Fix: 356253
 * Rakesh Patni                  243679                      06/14/2018                 REQ - 361927 - Logging functionality implementation
 * Ajay Chakradhar										     10/13/2021			        US 2712261 : Global Const and HumConst files changes for Performance improvement 
 *******************************************************************************************************************************/

public without sharing class HUMPolicyDetailsUpdateController
{   
    @TestVisible private String sRequest ='';
    @TestVisible private PolicyDetailsUpdate_S_HUM oServicePolicyMem = new PolicyDetailsUpdate_S_HUM ();
    private String sNoneValue = 'NONE';   
    private String sNullValue = 'Uninitialized';  
    private String sLVPlatForm = 'LV';
    private String sEMPlatForm = 'EM';  
    public Policy_Member__c policyMember = new Policy_Member__c();
    private Policy_Plan_Details__c policyPlan = new Policy_Plan_Details__c();
    private String sExchange='';
    private String sExchangeType='';
    private String sMetallicTier= '';
    private String sExchangeSegment='';
    private String sProductSegment='';
    private boolean bIsHumanaOne=false;
    public GBEServiceResponse_H_HUM resObj=null;
    
    /*
     * Name          :    HUMPolicyDetailsUpdateController - Constructor with Standard Controller
     * Description   :    Initialize few variables
     * Return Type   :    NA
     * Parameters    :    ApexPages.StandardController 
     */  
    public HUMPolicyDetailsUpdateController ()
    {
        String PolMemId = ApexPages.currentPage().getParameters().get('id'); 
        if(PolMemId!= null )
        {
            policyMember =[select id, name,Effective_Date__c,End_Date__c,Policy__r.Product__r.Sold_Product_Key_Value__c,Policy__r.Group_Name__r.Source_Customer_Key__c,Policy__r.Platform__c,Policy__r.Source_Cust_Cov_Key__c,Group_Number__c,Policy__r.Last_GBE_Call_Date__c,Policy__c,Policy__r.Group_Name__c from Policy_Member__c where id =:PolMemId limit 1 ];
        } 
    }
    
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
                            updateGBEPolicyData(resObj);
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
     * Method Name   :    updateGBEPolicyData
     * Description   :    This method calls the group service to fetch the exchange and segment details
     * Return Type   :    Void
     * Parameters    :    HUMIntGroupSearchServiceHelper.GroupExchangeInfoDetailDTO
     */
    public void updateGBEPolicyData(GBEServiceResponse_H_HUM oResponse ){

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
    }