/*
Class Name       : VendorProgram_C_HUM 
Version          : 1.0 
Created Date     : Sep 08 2020
Function         : Class to fetch the Vendor Program field value details in Group Policy Page.
    Modification Log

  Developer       Code Review        Date                 Description
* ------------------------------------------------------------------------------------------------------------------------------                
* Firoja Begam                       9/8/2020             Original Version -- 1279297 -- new fields in policy details section (group policy page)                                      
* Ajay Chakradhar                   11/22/2021            DEF 4182 FIX - Global Constant replace for performance improvement
*/
public with sharing class VendorProgram_C_HUM {

    public Policy__c oPolicy {get;set;}
    public string  sObjectID {get;set;}
    public Map <String, Schema.SObjectType> schemaMap = Schema.getGlobalDescribe();
    public Boolean isCRMSGBOPermissionSetUser {get; set;}
    public String sProgramName {get; set;}
    public String sProgramDesc {get; set;}
    public String sProgramHoverDesc {get; set;}
    public String sProgramEff {get; set;}
    public String sProgramEnd {get; set;}
    Public Boolean ccpOnOff {get; set;}
    Public String sHUM_APINAME_POLICY = 'Policy__c';
    Public String CRMS_240_GBO_Access_HUM = 'CRMS_240_GBO_Segment_Service_Access'; 
    Public String VOB_PLATFORM_EM_HUM = 'EM';
    Public String VOB_PLATFORM_LV_HUM = 'LV';
    Public String MED = 'MED';
    Public String ACCL = 'ACCL';
    Public Static final Integer HTTP_ACCEPTED_HUM = 202; 
    Public Static final Integer HTTP_OK_HUM = 200;
    Public Static final String VOB_FAULT_CODE = 'faultcode';
    String sObjName;
    @TestVisible public String sRequest = '';
    @TestVisible private VendorProgram_S_HUM oServiceVendor = new VendorProgram_S_HUM ();
    public GBEServiceResponse_H_HUM resObj=null;
    private Policy_Plan_Details__c policyPlan = new Policy_Plan_Details__c();
    List<Policy_Plan_Details__c> policyPlanList = new List<Policy_Plan_Details__c>();
    @testVisible private GBEServiceResponse_H_HUM.VendorProgramOfferListGroup vendorProgramGroupDTO = NULL;
    @testVisible private GBEServiceResponse_H_HUM.VendorProgramOfferListSubGroup vendorProgramSubGroupDTO = NULL;
    @testVisible private List<GBEServiceResponse_H_HUM.VendorProgramOffer> vendorOfferList = new List<GBEServiceResponse_H_HUM.VendorProgramOffer>();
    public class ServiceDownException extends Exception {}
    
    public VendorProgram_C_HUM(ApexPages.StandardController controller) {
    	sObjectID = ApexPages.CurrentPage().getParameters().get('id');
        isCRMSGBOPermissionSetUser = UtilityHelper_H_AHT_HUM.isUserhavingPermissionSetAccess(CRMS_240_GBO_Access_HUM);
        ccpOnOff = false;
        ccpOnOff= HUMUtilityHelper.isCRMFunctionalityON('1279297');
    } 
       
   

    /**
     * Returns NULL
     * <p>
     * Will identify the objext(Policy) and get the record values.
     * @return      NULL
     * @see         Record Values
     */    
    Public void getValue(){
        try 
        {
            if(sObjectID != null && sObjectID.length() >= 3)   
            {        
                String sGetObjType = String.valueOf(sObjectID).substring(0,3); //get just the prefix
                for(Schema.SObjectType stype : schemaMap.values())       
                {         
                    Schema.DescribeSObjectResult oSchema = stype.getDescribe();           
                    String sPrefix = oSchema.getKeyPrefix();      
                    if(sPrefix != null && sPrefix.equals(sGetObjType))           
                    {
                        sObjName=oSchema.getName();    
                        break;           
                    }       
                } 
                if(sObjName == sHUM_APINAME_POLICY)
                {
                    SetPolicyValues();
                }
            }
        } 
        catch(Exception ex)
        {    
            HUMExceptionHelper.logErrors(ex,'VendorProgram_C_HUM','getValue');
            
        }
    }
     
    /**
     * Returns NULL
     * <p>
     * get and set the record values of Policy Object.
     * @return      NULL
     * @see         Record Values
     */     
    public void SetPolicyValues()
    {
         try {
            oPolicy = [Select Id, Product__r.Product_Type__c, Major_LOB__c,Product__r.Product_Line_Description__c, Platform__c, product__r.name from Policy__c where id =: sObjectID limit 1];
        	if(oPolicy != NULL)
            {
             if((oPolicy.Platform__c.equals(VOB_PLATFORM_EM_HUM) || oPolicy.Platform__c.equals(VOB_PLATFORM_LV_HUM)) && oPolicy.product__r.name.equals(MED) && (isCRMSGBOPermissionSetUser))
             {
              policyPlanList = [select id, Coverage_Plan_Effective_Date__c,Coverage_Plan_End_Date__c,Product__r.Sold_Product_Key_Value__c,Policy__r.Group_Name__r.Source_Customer_Key__c, Platform__c,Source_Cust_Cov_Key__c,Group_Number__c,Policy__r.Last_GBE_Call_Date__c,Policy__c,Policy__r.Group_Name__c,Last_GBE_Call_Date__c from Policy_Plan_Details__c 
                                where Policy__c =: oPolicy.Id and ETL_Record_Deleted__c=false ];
              if(policyPlanList != NULL && !policyPlanList.isEmpty())
              for(Policy_Plan_Details__c policyPlanDetail : policyPlanList)
                { 
                policyPlan = policyPlanDetail;
                }
             }
            }
        }
        catch(Exception ex)
        {    
            HUMExceptionHelper.logErrors(ex,'VendorProgram_C_HUM','SetPolicyValues');
        }
    }

     /*
     * Method Name   :    getGBEInformationAsync
     * Description   :    This method makes call to GBE when the Group policy page is loaded 
     * Return Type   :    Continuation
     * Parameters    :    NA
     */
    public Object getGroupGBEInformation()
    {
       try{
           getValue();
       if(!String.isblank(policyPlan.Platform__c) && !String.isblank(policyPlan.Group_Number__c))
        	{                 
                
                HUM_Webservice_Callout__c oSvcHeaderGBEService = HUM_Webservice_Callout__c.getValues('GroupBusinessEntityService');
                Integer iTIMEOUT_INT_SECS = Integer.valueof(oSvcHeaderGBEService.Timeout__c)/1000;
                Continuation con = new Continuation(iTIMEOUT_INT_SECS );
                con.continuationMethod=HUMVitalityServiceConstants.CONTINUATION_PROCESSRESPONSE_HUM;
                HttpRequest request = new HttpRequest();
                oServiceVendor.setupRequest(request, oSvcHeaderGBEService,policyPlan); 
                sRequest = con.addHttpRequest(request);
                return  con; 
                }
            }
            catch(Exception ex)
            {    
                HUMExceptionHelper.logErrors(ex,'VendorProgram_C_HUM','getGroupGBEInformation');
        	}
	        return NULL;
    	}
    
    
     public object processResponse()
     {   
        try 
        {
            resObj = new  GBEServiceResponse_H_HUM ();
            HttpResponse response = Continuation.getResponse(sRequest);
            boolean isResponseValid = checkResponseValidity(response);
            if(isResponseValid) 
            {
                resObj = (GBEServiceResponse_H_HUM)System.JSON.deserialize(response.getBody(),GBEServiceResponse_H_HUM.class);
                if(resObj<>null && resObj.GroupInfo<>null)
                { 
                   if(resObj.GroupInfo.Platform != NULL && (resObj.GroupInfo.Platform.equals(VOB_PLATFORM_EM_HUM) || resObj.GroupInfo.Platform.equals(VOB_PLATFORM_LV_HUM)))
                   {
                     if(resObj.GroupInfo.VendorProgramOfferListSubGroup <> null)
                        vendorInfoValue(resObj);
                    }
                }
            }
            else{
                HUMExceptionHelper.logErrors(New ServiceDownException('SERVICEERROR'+response.getBody()), 'VendorProgram_C_HUM', 'ProcessResponse');
            }    
        }
        catch(Exception ex)
        {    
            HUMExceptionHelper.logErrors(ex,'VendorProgram_C_HUM','ProcessResponse');
        }
		return null;    
    }
    
    
    /*
     * Method Name   :  vendorInfoValue()
     * Description   :  This is the used to populate Vendor Info 
     * Return Type   :  void
     * Parameters    :  NA
    */
    public void vendorInfoValue(GBEServiceResponse_H_HUM resObj){
        if(resObj != NULL)
        {
            if(resObj.GroupInfo.VendorProgramOfferListSubGroup != NULL) 
                vendorProgramSubGroupDTO = resObj.GroupInfo.VendorProgramOfferListSubGroup;
            if(vendorProgramSubGroupDTO != NULL)
            {    
                for(GBEServiceResponse_H_HUM.VendorProgramOffer programoffer: vendorProgramSubGroupDTO.VendorProgramOffer)
                {
                    if((!String.ISBLANK(programoffer.VendorIdentifier)) && (programoffer.VendorIdentifier.equalsIgnoreCase(ACCL)))
                    {          
                        if(!String.ISBLANK(programoffer.VendorIdentifierDescription)) sProgramName = programoffer.VendorIdentifierDescription; 
                        if(!String.ISBLANK(programoffer.VendorBenefitCodeDescription)) sProgramDesc = programoffer.VendorBenefitCodeDescription; 
                        if(!String.ISBLANK(programoffer.ProgramOfferEffectiveDate)) sProgramEff = programoffer.ProgramOfferEffectiveDate; 
                        if(!String.ISBLANK(programoffer.ProgramOfferEndDate)) sProgramEnd = programoffer.ProgramOfferEndDate;
                        break;
                    }	
                } 
            } 
        }
    }
    public static boolean checkResponseValidity(HttpResponse response) {
        boolean returnValue = false;
        try {
            if(response != null) {
                integer statusCode = response.getStatusCode();
                Boolean isStatusOk = (statusCode == HTTP_OK_HUM) ? true : (statusCode == HTTP_ACCEPTED_HUM) ? true : false;
                Boolean isNoFault = (response.getBody().containsIgnoreCase(VOB_FAULT_CODE)) ? false : true;
                returnValue = (isStatusOk && isNoFault) ? true : false;
            }
        }
        catch(Exception ex) {
            HUMExceptionHelper.logErrors(ex,'VendorProgram_C_HUM','checkResponseValidity');
            returnValue = false;
        }
        return returnValue;
    }
}