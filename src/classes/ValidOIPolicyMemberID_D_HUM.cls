/**************************************************************************************************************************************************
    @Apex class Name    : ValidOIPolicyMemberID_D_HUM
    @Function           :  This class serves as data class for MBEPlus OI service.
    @Version            : 1.0
    @test class          : ValidOIPolicyMemberID_D_T_HUM

Modification Log:
* Developer Name         Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Riddhi Mittal                             06/19/2020                  Original Version 
* Raja Sekhar Reddy M V                     08/20/2020	                PR00095673_US_1276005_DualstatusIndicator
* Ajay Chakradhar                           09/27/2020	                US 1467120 - PR00093155 - MF 21 - Other Insurance (OI) Status in CRM
* Ajay Chakradhar                           11/20/2020	                US 1641087 - PR00093155 - MF 21 - Release 12/18
* Ajay Chakradhar                           02/11/2021	                US 1707499 - PR00093155 - MF 21 - Release 03/19/21
* Ajay Chakradhar                           03/15/2021	                US 1707499 - PR00093155 - MF 21 - Add On/Off flag for adding OICOB Node in the request
* Ajay Chakradhar                           05/05/2021	                REQ - 2184294 - Replacing constants with Consumer Management Constants
* Ajay Chakradhar                           05/19/2021	                REQ - 2104099 - T1PRJ0002198/PR00093155- Primary field in the CRM OI Grid on the Policy Page
************************************************************************************************************************************************* */

public with sharing class ValidOIPolicyMemberID_D_HUM
{   
    public static map<String, Policy_Member__c> mapPolicyMembers = new map<string, Policy_Member__c>();
    public static map<String, list<Policy_Plan_Details__c>> mapPolicyPlanDetails = new map<string, list<Policy_Plan_Details__c>>();
    public static set<String> setRemainingIDs = new set<String>();
    public static String sClassName = 'ValidOIPolicyMemberID_D_HUM';
    public class ServiceDownException extends Exception {}
    @TestVisible private static String continuationLabel;
    Private Static String sOI_CALLBACK_METHOD = 'processMBEOtherInsuranceResponse';
    Private Static String sNumOne = '1';  
    Private Static String sNumZero = '0';
   /*
    * Method Name : retrieveCoverageDetails
    * Description : This method is used to retrieve the coverage details for a PolicyMember to be dispalyed on COB template
    * Return type : Void
    * Paramater   : None
    */
   public static void retrieveCoverageDetails(String polMemId)
    {   
        try
        {  
            if(String.isNotBlank(polMemId))
            {
                list<policy_member__c> lstPolicyMem = new list<policy_Member__c>();
                set<Id> setPolicyIds = new set<Id>();
                lstPolicyMem = [Select Id, Relationship__c, Name, Member__c, Member__r.Name, Member__r.FirstName , Member__r.LastName , Member__r.Enterprise_Id__c,
                                Policy_Member_External_Id__c, Effective_Date__c, End_Date__c,Policy__c, Policy__r.Group_Name__r.Source_Customer_Key__c,Policy__r.Source_Cust_Cov_Key__c, 
                                Policy__r.Product__r.Sold_Product_Key_Value__c, Policy_Platform__c, Group_Number__c From Policy_Member__c where Id = : polMemId limit 1];
                
                if(lstPolicyMem!=null && !lstPolicyMem.isEmpty() && String.isNotBlank(lstPolicyMem[0].Member__r.Enterprise_Id__c))
                {
                    mapPolicyMembers.put(lstPolicyMem[0].Member__r.Enterprise_Id__c, lstPolicyMem[0]);
                    if(lstPolicyMem[0].Policy__c !=null){ 
                        setPolicyIds.add(lstPolicyMem[0].Policy__c); 
                    }
                }
                if(mapPolicyMembers!=null && !mapPolicyMembers.isEmpty()) 
                {	
                    setRemainingIDs.addAll(mapPolicyMembers.keySet());
                    if(setPolicyIds!=null && !setPolicyIds.isEmpty())
                    {
                        list<Policy_Plan_Details__c> lstPolicyPlans = new list<Policy_Plan_Details__c>();
                        lstPolicyPlans = [SELECT Id, Name, Coverage_Plan_Effective_Date__c, Coverage_Plan_End_Date__c, Policy__c, 
                                          Product__c,Product__r.Sold_Product_Key_Value__c, Policy_Group_Number__c, Source_Cust_Cov_Key__c 
                                          FROM Policy_Plan_Details__c Where Policy__c IN :setPolicyIds ];
                        if(lstPolicyPlans!=null && !lstPolicyPlans.isEmpty())
                        {
                            for(Policy_Plan_Details__c oPlan : lstPolicyPlans)
                            {
                                if(mapPolicyPlanDetails.containsKey(oPlan.Policy__c)){ 
                                    mapPolicyPlanDetails.get(oPlan.Policy__c).add(oPlan); 
                                }else{ 
                                    mapPolicyPlanDetails.put(oPlan.Policy__c, new list<Policy_Plan_Details__c>{oPlan}); 
                                }          
                            }
                        }    
                    }    
                }
            }        
       }
       catch(Exception e)
        {   
           HUMExceptionHelper.logErrors(e, sClassName, 'retrieveCoverageDetails');
        } 
    }
  
    /**
    * Returns an object and method calls the MBEPlus web service
    * @pram  id of the policy member in string format
    * @param  type in string format 
    * @return  object
    */
    public static Continuation getOtherInsuranceData(string sRecordId, string sPolicyMemberId)
    {  
        try
        {
            Policy_Member__c objPolicyMember = [select Id, Member__r.Enterprise_Id__c,ETL_Record_Deleted__c From Policy_Member__c where Id = : sPolicyMemberId limit 1];
            String sEnterpriseId = (objPolicyMember.Member__r.Enterprise_Id__c != Null) ? objPolicyMember.Member__r.Enterprise_Id__c : ''; 
            Continuation con = new Continuation(GlobalCommonConstants_HUM.SERVICETIMEOUT);
            MBEPlusOIRequest_DTO_HUM oReq = new MBEPlusOIRequest_DTO_HUM();
            oReq = HUMProcessUtilityHelper.createMBEPlusRequest(sEnterpriseID,HUMUtilityHelper.isCRMFunctionalityON('1707499_Switch')); // construct MBE+ Request
            String sBodyJSON;
            if(oReq!=null){
                sBodyJSON = JSON.serialize(oReq);
            }                    
            if(String.isNotBlank(sBodyJSON))
            { 
                HttpRequest mbeRequest = HUMProcessUtilityHelper.builtHttpRequest();                        
                if(mbeRequest!=null)
                {
                    mbeRequest.setBody(sBodyJSON);
                    con.continuationMethod = sOI_CALLBACK_METHOD;
                    continuationLabel = con.addHttpRequest(mbeRequest);
                    con.State = objPolicyMember;
                }
            }
            return con;
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, sClassName, 'getOtherInsuranceData');
        }
        return null;
    }
    
    /*
    *  This method is used to Parse response from MBEPlus service
    *  @name   ProcessMBEOtherInsuranceResponse
    *  @param  List and object
    *  @return object
    */
    @testvisible
    public static object ProcessMBEOtherInsuranceResponse(List<string> labels,object state)
    {   
        PolicyMemberMBEResWrapper_DTO_HUM oPolMemberDetails = new PolicyMemberMBEResWrapper_DTO_HUM();
        try
        {
            Policy_Member__c oPolMemberInfo = (Policy_Member__c)state;
            retrieveCoverageDetails(oPolMemberInfo.Id); // retrive details of Policy Member
            HttpResponse mbeResponse = Continuation.getResponse(labels[0]);
            if ((mbeResponse != null && !mbeResponse.getBody().containsIgnoreCase(GlobalCommonConstants_HUM.VOB_FAULT_CODE) && 
                (mbeResponse.getStatusCode() == GlobalCommonConstants_HUM.HTTP_OK_HUM || mbeResponse.getStatusCode() == GlobalCommonConstants_HUM.HTTP_ACCEPTED_HUM )
                ) || Test.isRunningTest() )
            {  
			    MBEPlusOIResponse_DTO_HUM outResponse = (MBEPlusOIResponse_DTO_HUM)System.JSON.deserialize(mbeResponse.getbody(), MBEPlusOIResponse_DTO_HUM.class);                                                               
            	if(outResponse != null && outResponse.GetMemberResponse != null && outResponse.GetMemberResponse.Body != null && 
                   outResponse.GetMemberResponse.Body.Member != null && String.isNotBlank(outResponse.GetMemberResponse.Body.Member.MasterId) && 
                   mapPolicyMembers.containsKey(outResponse.GetMemberResponse.Body.Member.MasterId))    
                {	
                    Policy_Member__c oPolicyMember = mapPolicyMembers.get(outResponse.GetMemberResponse.Body.Member.MasterId);
                    list<Policy_Plan_Details__c> lstPlans = new list<Policy_Plan_Details__c>();
                    if(mapPolicyPlanDetails!=null && !mapPolicyPlanDetails.isEmpty() && mapPolicyPlanDetails.containsKey(oPolicyMember.Policy__c)){
                    	lstPlans = mapPolicyPlanDetails.get(oPolicyMember.Policy__c);    
                    } 
                    //Retrieve COB details from MBE Response
                    //Introduced new switch '1707499_Switch' to handle switch between OICoordinationOfBenefit and CoordinationOfBenefit node from MBE+ response 
                    if(HUMUtilityHelper.isCRMFunctionalityON('1707499_Switch'))
                    {
                        //Below code will parse MBE+ repsonse with new node OICoordinationOfBenefit 
                    	list<MBEPlusOIResponse_DTO_HUM.OICoordinationOfBenefit> lstOICOBFromService = HUMProcessUtilityHelper.parseMBERespForOICOBDetails(outResponse, oPolicyMember, lstPlans); 
                    	oPolMemberDetails.validOIWrapperList = (lstOICOBFromService!=null && !lstOICOBFromService.isEmpty() && !oPolMemberInfo.ETL_Record_Deleted__c) ? processOICOBData(lstOICOBFromService) : Null ;
                    }else{
                        //Below code will parse MBE+ repsonse with old node OICoordinationOfBenefit
                    	list<MBEPlusOIResponse_DTO_HUM.CoordinationOfBenefit> lstCOBFromService = HUMProcessUtilityHelper.parseMBERespForCOBDetails(outResponse, oPolicyMember, lstPlans);     
                    	oPolMemberDetails.validOIWrapperList = (lstCOBFromService!=null && !lstCOBFromService.isEmpty() && !oPolMemberInfo.ETL_Record_Deleted__c) ? processCOBData(lstCOBFromService) : Null ;
                    }
                    // To assign dual status indicator field 
                    oPolMemberDetails.isMemDualEligibleInLastTwelveMonth = DualEligibleDetailsHelper_H_HUM.processDualResponse(outResponse);	
                }
			}
        } 
        catch (Exception ex)
        {   
            HUMExceptionHelper.logErrors(ex, sClassName, 'processMBEOtherInsuranceResponse');
            return null;   
        }
        return oPolMemberDetails;     
    }
    
    /**
    * This method generates list of other insurance on policy member page.
    * @Nmae     processOICOBData
    * @param    ValidOIWrapper_DTO_HUM response
    * @return   List of ValidOIWrapper_DTO_HUM 
    */
    public static list<ValidOIWrapper_DTO_HUM> processOICOBData(list<MBEPlusOIResponse_DTO_HUM.OICoordinationOfBenefit> lstCoordinationOfBenefit)
    { 
        list<ValidOIWrapper_DTO_HUM>  lstOtherInsuranceData = new list<ValidOIWrapper_DTO_HUM>();
        for(MBEPlusOIResponse_DTO_HUM.OICoordinationOfBenefit oCoordinationOfBenefit : lstCoordinationOfBenefit)
        { 
            ValidOIWrapper_DTO_HUM oOIWrap = new ValidOIWrapper_DTO_HUM();

            oOIWrap.sCarrierName = oCoordinationOfBenefit.CarrierName;
            oOIWrap.sEffectiveDate = oCoordinationOfBenefit.EffectiveDate;
            oOIWrap.sEndDate = oCoordinationOfBenefit.EndDate;
            //US2104099 - In Primary field CRM displays Y for the Primary and N for the Secondary and Blank for undefined
            if(HUMUtilityHelper.isCRMFunctionalityON('2104099')){
                oOIWrap.sPrimary = (oCoordinationOfBenefit.Primary == sNumOne) ? 'Y' : (oCoordinationOfBenefit.Primary == sNumZero) ? 'N' : '';
            }else{
            	oOIWrap.sPrimary = (oCoordinationOfBenefit.Primary == Label.HUMConstOne) ? 'Y' : 'N';     
            }
            oOIWrap.sOILastVerifiedDate = oCoordinationOfBenefit.OILastVerifiedDate;
            oOIWrap.sLastUpdatedDate = oCoordinationOfBenefit.LastUpdatedDate;   
            lstOtherInsuranceData.add(oOIWrap);
        }
        return lstOtherInsuranceData;
    }
    
    /**
    * This method generates list of other insurance on policy member page.
    * @Nmae     processCOBData
    * @param    ValidOIWrapper_DTO_HUM response
    * @return   List of ValidOIWrapper_DTO_HUM 
    */
    public static list<ValidOIWrapper_DTO_HUM> processCOBData(list<MBEPlusOIResponse_DTO_HUM.CoordinationOfBenefit> lstCoordinationOfBenefit)
    { 
        list<ValidOIWrapper_DTO_HUM>  lstOtherInsuranceData = new list<ValidOIWrapper_DTO_HUM>();
        for(MBEPlusOIResponse_DTO_HUM.CoordinationOfBenefit oCoordinationOfBenefit : lstCoordinationOfBenefit)
        { 
            ValidOIWrapper_DTO_HUM oOIWrap = new ValidOIWrapper_DTO_HUM();

            oOIWrap.sCarrierName = oCoordinationOfBenefit.CarrierName;
            oOIWrap.sEffectiveDate = oCoordinationOfBenefit.EffectiveDate;
            oOIWrap.sEndDate = oCoordinationOfBenefit.EndDate;
            //US2104099 - In Primary field CRM displays Y for the Primary and N for the Secondary and Blank for undefined
            if(HUMUtilityHelper.isCRMFunctionalityON('2104099')){
                oOIWrap.sPrimary = (oCoordinationOfBenefit.Primary == sNumOne) ? 'Y' : (oCoordinationOfBenefit.Primary == sNumZero) ? 'N' : '';
            }else{
            	oOIWrap.sPrimary = (oCoordinationOfBenefit.Primary == Label.HUMConstOne) ? 'Y' : 'N';     
            }
            oOIWrap.sOILastVerifiedDate = oCoordinationOfBenefit.OILastVerifiedDate;
            oOIWrap.sLastUpdatedDate = oCoordinationOfBenefit.LastUpdatedDate;  
            lstOtherInsuranceData.add(oOIWrap);
        }
        return lstOtherInsuranceData;
    }
}