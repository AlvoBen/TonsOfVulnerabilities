/*******************************************************************************************************************************
Apex Class Name : VerifyDemograhics_LC_HUM
Version         : 1.0
Created On      : 02/23/2021
Function        : Class contains request to WSDL  HUMVerifyFamilyCOBService and to display "Last Verified By" and "Last Verified on" details on Account Page
Test Class      : VerifyDemographics_LT_HUM
Modification Log:
Developer Name           Code Review                      Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
*Ankima Srivastava										 02/23/2021				  initial version
*Pallavi Shewale									         12/27/2021               USER STORY 2882068           
*****************************************************************************************************************/
public with sharing class VerifyDemograhics_LC_HUM
{
    public static HUMVerifyFamilyCOBService.UpdateDemographicsResponse_elementFuture oHUMDemographicFutureResponse;
    private static String sResult = '';
    public static boolean bCoreApiServiceREST;
   
    @AuraEnabled (continuation=true)
    public static Continuation APIService(String recId,String action){
         Integer TIMEOUT_INT_SECS;
        Continuation contObj ;
        bCoreApiServiceREST = HUMUtilityHelper.isCRMFunctionalityON('1065536');
       // bCoreApiServiceREST = false;
       
        if(action == 'onLoad'){
            if(bCoreApiServiceREST==true){
                 contObj = new Continuation(120);
                 contObj = getLastVerifyDatesRest1(recId);    
            }else{
                 HUM_Webservice_Callout__c oHUMWebServiceCall = HUM_Webservice_Callout__c.getValues('AddressVerification');
                 TIMEOUT_INT_SECS = Integer.valueOf(oHUMWebServiceCall.Timeout__c) / 1000;
                 contObj = new Continuation(TIMEOUT_INT_SECS);
                 contObj = getLastVerifyDates(recId);
            }
            
        } else if(action == 'onclickVerify'){
            if(bCoreApiServiceREST==true){
                 HUM_Webservice_Callout__c oUVDService = HUM_Webservice_Callout__c.getValues('UpdateVerifyDemographics');
                TIMEOUT_INT_SECS = Integer.valueof(oUVDService.Timeout__c)/1000;
                contObj = new Continuation(TIMEOUT_INT_SECS);
                
                 contObj = verifyDemographicsRest(recId);
            }else{
                 HUM_Webservice_Callout__c oHUMWebServiceCall = HUM_Webservice_Callout__c.getValues('AddressVerification');
                 TIMEOUT_INT_SECS = Integer.valueOf(oHUMWebServiceCall.Timeout__c) / 1000;
                 contObj = new Continuation(TIMEOUT_INT_SECS);
                 contObj = verifyDemographics(recId);    
            }
        }
        return  contObj;
    }
    
    /*
    * Method Name   :    getLastVerifyDates()
    * Description   :    call to service class method in order to hit the Async webservice getLastVerifyDates
    * Return Type   :    Continuation 
    * Parameter     :    Account Id
    */
    @AuraEnabled( continuation=true)
    public static Continuation getLastVerifyDates(String recId)
    {
       return VerifyDemographics_LS_HUM.getLastVerifyDates(recId);
    }
    
    /*
    * Method Name   :    processGetLastVerifyDates
    * Description   :    call to service class continuation method to process the response
    * Return Type   :    List<HUMVerifyDemoGraphicsWrapperHelper>
    * Parameter     :    Object
    */
    @AuraEnabled @TestVisible
    public static List<HUMVerifyDemoGraphicsWrapperHelper> processGetLastVerifyDates(Object state) {
        return VerifyDemographics_LS_HUM.processGetLastVerifyDates(state);
   }
    
   /*
    * Method Name   :    verifyDemographics
    * Description   :    calling service class method to hit the Async webservice UpdateDemographics
    * Return Type   :    Continuation
    * Parameter     :    Account Id
    */
    @AuraEnabled (continuation=true)
    public static Continuation verifyDemographics(String recId)
    {   
         return VerifyDemographics_LS_HUM.verifyDemographics(recId);  
    }
    
    /*
    * Method Name   :    processverifyDemographics
    * Description   :    call to service class continuation method to call out response process
    * Return Type   :    Object
    * Parameter     :    Object
    */
    @AuraEnabled (continuation=true)
    public static Object processverifyDemographics(Object state) {
        return VerifyDemographics_LS_HUM.processverifyDemographics(state);
        
    }
   
   /*
    * Method Name   :    getLastVerifyDates()
    * Description   :    Calling service class method to Trigger CoreApiServiceREST GetVerifyDemographics service and fetch lastVerifyDates
    * Return Type   :    Continuation 
    * Parameter     :    Account Id
    */
     @AuraEnabled(continuation=true )
    public static Continuation getLastVerifyDatesRest1(String recId)
    {  
      return VerifyDemographics_LS_HUM.getLastVerifyDatesRest(recId);
    }

    /*
    *  This method is used to Parse response from getVerifyDemographics service
    *  @name   processGetVerifyDemographicsResponse
    *  @param  List and object
    *  @return object
    */
       @AuraEnabled( continuation=true )
    public  static list<VerifyDemographicGetWrapper_DTO_HUM> processGetVerifyDemographicsResponse(List < string > labels, Object State) 
    {   
       return VerifyDemographics_LS_HUM.processGetVerifyDemographicsResponse(labels, state);
    }
   
    /*
    * Method Name   :    verifyDemographics
    * Description   :    To Trigger CoreApiServiceREST updateVerifyDemographics service and to Update Demographics
    * Return Type   :    Continuation
    * Parameter     :    Account Id
    */
    @AuraEnabled (continuation = true)
    public static Continuation verifyDemographicsRest(String recordId)
    {   
        return VerifyDemographics_LS_HUM.verifyDemographicsRest(recordId);
    } 
     
           
    /*
    *  This method is used to Parse response from VerifyDemographics service
    *  @name   processVerifyDemographicsResponse
    *  @param  List and object
    *  @return object
    */
    @testvisible @AuraEnabled(continuation=true)
    public static object processUpdateVerifyDemographicsResponse(List<String> labels,object state)
    { 
        return VerifyDemographics_LS_HUM.processUpdateVerifyDemographicsResponse(labels, state);
      
    }
 }