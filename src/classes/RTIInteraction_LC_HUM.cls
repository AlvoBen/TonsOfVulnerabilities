/**************************************************************************** 
Apex Class Name  : RTIInteraction_LC_HUM
Version          : 1.0  
Created Date     : 07/12/2022
Function         : Controller class RTIDetailHum
Test class       : RTIInteraction_LT_HUM
****************************************************************************
Modification Log:
* Developer Name                                  Date                       Description
*---------------------------------------------------------------------------------------------------
* Swapnali Sonawane 							  7/12/2022				     US-3298002 RTI Implementation
* Divya Bhamre                                     2/17/2022                 US - 4286513
***************************************************************************************************/

public with sharing class RTIInteraction_LC_HUM {
    static Map<String, RTIApplicationValues_H_HUM.Attributes> mapRTIattributes  = RTIApplicationValues_H_HUM.getApplicationFieldAttributes('RTI'); 
   
    public static boolean bIsDataAvailable;
    public static String sMinStartDate;
    public static String sMaxEndDate;
    private static string sInteractingWithCode = 'All';
    private static string sCategoryCode = 'All';
    private static string sOldInteractingWithCode = 'All';
    private static string sOldCategoryCode = 'All';
    private static String sRequest ='';
    private static Integer iRecordLimit = 200;
    private static RTISummary_LS_HUM oServiceRTI = new RTISummary_LS_HUM();
    private static String sOldSelectedChannel ='All';
    public static String sSelectedDirection;
    private static Integer iSkip =0;
    private static Integer iTotalRec;
    public static  String iRTICards { get; set; }
    public static String sSelectedChannel {get;set;}
    public static boolean bIsPageOnLoad {get; set;}
    public static List<RTISummary_DTO_HUM> lstRTIInteractions{get; private set;}
    @TestVisible public static String sEnterpriseId {get;private set;}
    @TestVisible public static String sCategoryValue {get;set;}
    @TestVisible public static String sInteractionWithType {get; private set;}
    @TestVisible public static String sStartDate{get;set;}
    @TestVisible public static String sEndDate{get;set;}
    
   @TestVisible
	@AuraEnabled(continuation=true)
    public static List<RTI_Application_Value__mdt> GetRTIApplicationValue()
    {
        List<RTI_Application_Value__mdt> RTIAppList = [select Attribute_Label__c, Attribute_Value__c,
                                                                Field_Label__c, Parent_Attribute_Value__c, Disposition_Code__c,Category_Code__c,
                                                                Status_Code__c,
                                                                Parent_Field_Label__c, Sequence__c from 
                                                                RTI_Application_Value__mdt where
                                                                IsActive__c = true order by 
                                                                Sequence__c limit 1000 ];  
        return RTIAppList;
    }
    /*
    * Method Name   :  search
    * Description   :  This method is used to call ther service when user clicks on 'Search' button
    * Return Type   :  Continuation
    * Parameters    :  NA
    */
    @TestVisible
    @AuraEnabled(continuation=true)
    public static Object search(string sEnterpriseId,string sStartDate, string sEndDate ,string sSelectedChannel,string sCategoryValue,string sDirectionValue, integer iSkipNo)
    {
       
        
        try
        {
            CRMFunctionality_ONOFF_Switch__c objFunctionalitySwitch = CRMFunctionality_ONOFF_Switch__c.getValues('4286513');
        boolean switchValue = objFunctionalitySwitch != null ? objFunctionalitySwitch.IsON__c : false;
      
            if(switchValue==false){
                bIsDataAvailable = true;     
                sSelectedDirection = sDirectionValue;
                
                if((!(sCategoryValue.equalsIgnoreCase('All')))) 
                {
                    if(sSelectedChannel == 'WEB,WB' || sSelectedChannel == 'PRINT')   sCategoryCode = sCategoryValue;
                    if(sSelectedChannel == 'IV')    sInteractingWithCode = sCategoryValue; 
                    
                }
                if(String.isNotBlank(sEnterpriseId))
                {
                    HUM_Webservice_Callout__c oSvcHeaderRTI = new HUM_Webservice_Callout__c();
                    oSvcHeaderRTI = HUM_Webservice_Callout__c.getValues('RTIService_GetInteractions_V2');
                    Integer iTIMEOUT_INT_SECS = Integer.valueof(oSvcHeaderRTI.Timeout__c)/1000;
                    HttpRequest request = new HttpRequest();
                
                    iSkip = iSkipNo;
                    oServiceRTI.setupRequest(request, oSvcHeaderRTI, sEnterpriseId, sStartDate, sEndDate, sSelectedChannel, iSkip, sCategoryCode, sInteractingWithCode, sSelectedDirection, mapRTIattributes.get('Channel').getAttributeValues(sSelectedDirection));    
                    sOldSelectedChannel = sSelectedChannel;
                    sOldCategoryCode = sCategoryCode;
                    sOldInteractingWithCode = sInteractingWithCode;
                   
                    Continuation con = new Continuation(iTIMEOUT_INT_SECS );
                    sRequest = con.addHttpRequest(request);
                    con.continuationMethod='processResponse';
                    return con;  
                }
                else    
                {
                    
                    return Null;
                }
            }else{
                throw new HUMCustomException('Service Error');
            }
        }  
       
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'RTIInteraction_LC_HUM', 'search');
            return ex;
        }   
    }

    /*
    * Method Name   :  processResponse
    * Description   :  This method is used to get response from service.
    * Return Type   :  void 
    * Parameters    :  NA
    */
    @TestVisible
    @AuraEnabled (continuation=true)
    public Static Object processResponse(List<string> labels,Object State)
    {
        try 
        {   
            return  Json.serialize(oServiceRTI.responseparse(labels,State));
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'RTIInteraction_LC_HUM', 'processResponse');
			return ex;
        }
    }
    
   /*
    * Method Name   :  search
    * Description   :  This method Is used to call the Service when clicked on View Link 
    * Return Type   :  Continuation
    * Parameters    :  NA
    */
    @TestVisible
    @AuraEnabled(continuation=true)
    public static Object getRTIInteractionDetails(string sInteractionKey,string sChannelCode, string sCategoryDesc)
    {
        try
        {
            sRequest = ''; 
            HUM_Webservice_Callout__c oSvcHeaderRTI;          
            oSvcHeaderRTI = HUM_Webservice_Callout__c.getValues('RTIService_GetInteractionDetails_V2'); 
            Integer iTIMEOUT_INT_SECS = Integer.valueof(oSvcHeaderRTI.Timeout__c) / 1000;
            HttpRequest request = New HttpRequest();
            oServiceRTI.setupInteractionDetailsRequest(request, oSvcHeaderRTI, sInteractionKey, sChannelCode, iSkip, iRecordLimit);   
            Continuation con = New Continuation(iTIMEOUT_INT_SECS);                      
            sRequest = con.addHttpRequest(request);       
            con.state = sChannelCode + '_' + sCategoryDesc;
            con.continuationMethod ='processDetailsResponse';                     
           
            return con;
        }     
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'RTIInteraction_LC_HUM', 'getRTIInteractionDetails');
            return NULL;
        }   
    }

    /*
    * Method Name   :  processResponse
    * Description   :  This method is used to get response from RTIService_GetInteractionDetails_V2 service.
    * Return Type   :  void 
    * Parameters    :  NA
    */
    @TestVisible
    @AuraEnabled (continuation=true)
    public Static Object processDetailsResponse(List<string> labels,Object state)
    {
        try 
        {   
            return  Json.serialize(oServiceRTI.responseParseDetails(labels,state));
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'RTIInteraction_LC_HUM', 'processDetailsResponse');
			return ex;
        }
    }
   
}