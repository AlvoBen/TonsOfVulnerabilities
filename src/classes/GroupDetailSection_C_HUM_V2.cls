/*****************************************************************************************
Apex class Name    : GroupDetailSection_C_HUM_V2 
Version            : 1.0
Function           : Controller to VF Page:: GroupDetailSectionHUM.
                     This class is a updated version where the existing soap callout has been replaced with the REST callout.
Created Date       : March 10 2016
Test Class         : GroupDetailSection_C_T_HUM_V2 

Modification Log:
 *   Developer                   Code Review             Date               Description
 * ------------------------------------------------------------------------------------------------------------------------------  
 *   Praveen Kumar Parimi          23955                03/10/2016          Original Version
 *   Harshith Mandya                                    04/21/2106          Defect # 223238.0001
 *   Riddhi Mittal										07/27/2020			User Story 1138911: PR00085453 - MF 18 - Add EDI Field to Business Account Page
 *   Ajay Chakradhar										        10/13/2021			 US 2712261 : Global Const and HumConst files changes for Performance improvement 
*********************************************************************************************************************************/

global with sharing class GroupDetailSection_C_HUM_V2 {
	    
    @testVisible private Integer iTotalRec = (Integer)HUMConstants__c.getInstance('GBES_DivisionSubGroup_Records_HUM').IntegerValue__c;
    @testVisible private Integer iMaxRecordsToDisplay = (Integer)HUMConstants__c.getInstance('GBES_DivisionSubGroup_Records_HUM').IntegerValue__c;       
    @testVisible private GroupDetailSection_S_HUM_V2 restobjServiceInstance = NULL;
    @testVisible private GBEServiceRequest_H_HUM oRequest = NULL;
    @testVisible private GBEServiceRequest_H_HUM.GroupDetailDTO grpDetailDTO = NULL;
    @testVisible private GBEServiceResponse_H_HUM oResponse = NULL;    
    @testVisible private GBEServiceResponse_H_HUM.DivisionList arrVitalityIndicatorDTO = NULL;      
    @testVisible private GBEServiceResponse_H_HUM oReturnedresponse = NULL;     
    public String requestLabel = '';
    Public String GROUPSTRING = 'GROUP';
    Public Integer NUMBER_CONSTANT_FOUR_HUM = 4;
    Public String sZeroOne = '01';
    Public String sZeroZeroOne = '001';
    Public String sDummyBenValue = 'XXXXXXXX';
    Public String sMTV_Value = 'MTV';
    Public String sLV_Value= 'LV';
    Public String sCI_Value = 'CI';
    Public String sCAS_Value = 'CAS';
    Public String sEM_Value= 'EM';
    Public Integer HTTP_OK = 200;
    Public Integer HTTP_ACCEPTED = 202;

    /*
     * Method Name : GroupDetailSection_C_HUM_V2() 
     * Description : Constructor for the class , initialize variable
     * Return type : N/A
     * Parameter   : ApexPages.StandardController
     */
    public GroupDetailSection_C_HUM_V2(ApexPages.StandardController oStdController)
    {
       try
        {
           iCount = 1;
           iTotalCount = 0;
           bShowDivisionDetails = true;
           bShowGroupDetails = true;
           if(!Test.isRunningTest()) oStdController.addFields(new List<String> {'Source_Customer_Key__c','Source_Platform_Code__c'});
           objGrpAccount = (Account)oStdController.getrecord();           
           restobjServiceInstance = new GroupDetailSection_S_HUM_V2();
		    // On/Off switch functionality for EDI in Bussiness Account Page
           ediOnOff= HUMUtilityHelper.isCRMFunctionalityON('1138911');
           ediResponseFilter = Integer.valueOf(NUMBER_CONSTANT_FOUR_HUM);
        }
       catch(Exception ex)
        {
           HUMExceptionHelper.logErrors(ex, 'GroupDetailSection_C_HUM_V2', 'GroupDetailSection_C_HUM_V2');
        }  
    }
    
    public String sMedicalEffDate {get;set;}
    public String sDentalEffDate {get;set;}
    public String sNxtRenDt {get;set;}
    public String sMedGrpEnrllCount {get;set;}
    public String sDentalGrpEnrllCount {get;set;}
    public Boolean bPrevious{get;set;}
    public Boolean bNext {get;set;}
    public List<GroupDetailSection_H_HUM> lstVitalityRecords{get;set;}
    public Boolean bShowGroupDetails{get;set;}
    public Boolean bShowDivisionDetails{get;set;}
    public Integer iCount{get;set;}
    public Integer iTotalCount{get;set;}
    public Integer iSize{get;set;}
    public Integer iCountPerPage{get;set;}
    public Account objGrpAccount{get;set;}
	public String EDIValue{get;set;}
    Public Boolean ediOnOff {get; set;}
    Public Integer ediResponseFilter{set;get;}
    
    /*
     * Method Name   :  formRequestBody()
     * Description   :  This method is used to form the request body
     * Return Type   :  String
     * Parameters    :  String, String
     */
    @testVisible private string formRequestBody(String sStartPosition,String sRecordsToreturn) 
    {
           GBEServiceRequest_H_HUM oRequest = new GBEServiceRequest_H_HUM();
           grpDetailDTO = new GBEServiceRequest_H_HUM.GroupDetailDTO();
           string sformBody = '';
           grpDetailDTO.GroupId = objGrpAccount.Source_Customer_Key__c;
            
           if(!String.isBlank(objGrpAccount.Source_Platform_Code__c))
           	{
               if(objGrpAccount.Source_Platform_Code__c.equalsIgnoreCase(sLV_Value))
               	 {            
                 	grpDetailDTO.SubGroupId = sZeroZeroOne;
                	grpDetailDTO.BenefitPackageId = '';
            	 }
               else
            	 {            
            		grpDetailDTO.SubGroupId = objGrpAccount.Source_Customer_Key__c+ sZeroOne;
            		grpDetailDTO.BenefitPackageId = sDummyBenValue;
            	 }
            }            
           grpDetailDTO.Platform = objGrpAccount.Source_Platform_Code__c;                     
           grpDetailDTO.BenefitSequenceNumber= '';
           grpDetailDTO.InclusionCriteria = GROUPSTRING;
           grpDetailDTO.DivisionStartPosition = sStartPosition;            
           grpDetailDTO.DivisionRecordsToReturn = sRecordsToreturn;            
           oRequest.GroupDetailDTO = grpDetailDTO;                    
           sformBody = JSON.serialize(oRequest);             
     	   return sformBody;
    }
    
    /*
     * Method Name   :  getGroupInfo()
     * Description   :  This method is used to Call the Service while loading the page
     * Return Type   :  Object
     * Parameters    :  NA
     */
             
    public Object getGroupInfo() 
    {       
           if(!String.isblank(objGrpAccount.Source_Platform_Code__c) && !String.isblank(objGrpAccount.Source_Customer_Key__c))
        	{                 
		       String sRequestBody = formRequestBody(String.valueOf(iCount),String.valueOf(iMaxRecordsToDisplay));           
		       HUM_Webservice_Callout__c oSvcHeaderGBEService = HUM_Webservice_Callout__c.getValues('GroupBusinessEntityService');           
		       HttpRequest request = restobjServiceInstance.setuprequestToGBE(oSvcHeaderGBEService, sRequestBody);                   
		       Integer iTIMEOUT_INT_SECS = Integer.valueof(oSvcHeaderGBEService.Timeout__c)/1000;
		       Continuation continuation = new Continuation(iTIMEOUT_INT_SECS);       
		       continuation.continuationMethod = HUMVitalityServiceConstants.CONTINUATION_PROCESSRESPONSE_HUM;                    
		       this.requestLabel = continuation.addHttpRequest(request);             
		       return continuation;   
        }
           else
            {
	           bShowGroupDetails =  true;
	           bShowDivisionDetails = false;
	           return NULL;
        	}
    }
    
    /*
     * Method Name   :  getNext()
     * Description   :  This method will be called whenever we click on Next button to show more records
     * Return Type   :  Object
     * Parameters    :  NA
     */
    public Object getNext()
    {
           if(iTotalCount > iMaxRecordsToDisplay)
        	{
               iCount += iMaxRecordsToDisplay; 
            if(iCount+iMaxRecordsToDisplay > iTotalCount)
            {
               iTotalRec = (iTotalCount - iCount) + 1;
            }            
	           String sRequestBody = formRequestBody(String.valueOf(iCount),String.valueOf(iTotalRec));
	           HUM_Webservice_Callout__c oSvcHeaderGBEService = HUM_Webservice_Callout__c.getValues('GroupBusinessEntityService');                    
	           HttpRequest request = restobjServiceInstance.setuprequestToGBE(oSvcHeaderGBEService, sRequestBody);        
	           Integer iTIMEOUT_INT_SECS = Integer.valueof(oSvcHeaderGBEService.Timeout__c)/1000;
	           Continuation continuation = new Continuation(iTIMEOUT_INT_SECS);        
	           continuation.continuationMethod = HUMVitalityServiceConstants.CONTINUATION_PROCESSRESPONSENEXT_HUM;                    
	           this.requestLabel = continuation.addHttpRequest(request);            
	           return continuation;            
        	}
           else
        	{
               return null;
        	}   
    }
        
    /*
     * Method Name   :  getPrevious()
     * Description   :  This method is will be called whenever user clicks on "Previous" button
     * Return Type   :  Object
     * Parameters    :  NA
    */
    public Object getPrevious()
    {
           if(iTotalCount > iMaxRecordsToDisplay && iCount > 1)
            {        
               iCount -= iMaxRecordsToDisplay; 
           	   iTotalRec = iMaxRecordsToDisplay;
               String sRequestBody = formRequestBody(String.valueOf(iCount),String.valueOf(iMaxRecordsToDisplay));
               HUM_Webservice_Callout__c oSvcHeaderGBEService = HUM_Webservice_Callout__c.getValues('GroupBusinessEntityService'); 
               HttpRequest request = restobjServiceInstance.setuprequestToGBE(oSvcHeaderGBEService, sRequestBody);        
               Integer iTIMEOUT_INT_SECS = Integer.valueof(oSvcHeaderGBEService.Timeout__c)/1000;
               Continuation continuation = new Continuation(iTIMEOUT_INT_SECS);        
               continuation.continuationMethod = HUMVitalityServiceConstants.CONTINUATION_PROCESSRESPONSENEXT_HUM;                                
               this.requestLabel = continuation.addHttpRequest(request);               
               return continuation;
            }
           else
            {
               return null;
            }   
    }   

    /*
     * Method Name   :    processResponse() 
     * Description   :    Method to process the Response from getGroupInfo().
     * return Type   :    void
     * Parameters    :    NA
    */   
    public void processResponse() 
    {    
       try
        {         
           HttpResponse response = Continuation.getResponse(this.requestLabel);             
           oReturnedresponse = checkResponse(response); 
                                    
           if(oReturnedresponse != NULL)
            {
               bShowGroupDetails = true;                 
               if(oReturnedresponse.GroupInfo.Platform != NULL && (oReturnedresponse.GroupInfo.Platform.equals(sMTV_Value) || oReturnedresponse.GroupInfo.Platform.equals(sLV_Value)  || oReturnedresponse.GroupInfo.Platform.equals(sCI_Value ) || oReturnedresponse.GroupInfo.Platform.equals(sCAS_Value) || oReturnedresponse.GroupInfo.Platform.equals(sEM_Value)))    
                 {
                     groupInfoDivisonData();  
                 }
               else    
                 {
                     bShowDivisionDetails = false;
                 }
             }
           else
            {    
               bShowGroupDetails = false;    
            }
         }
       catch(Exception ex)
        {
           bShowGroupDetails =  false;
           HUMExceptionHelper.logErrors(ex, 'GroupDetailSection_C_HUM_V2', 'processResponse');
        }
    }    

    /*
     * Method Name   :    checkResponse() 
     * Description   :    Method to check the Response from processResponse().
     * return Type   :    GBEServiceResponse_H_HUM
     * Parameters    :    HttpResponse
    */   
    public GBEServiceResponse_H_HUM checkResponse(HttpResponse response)
    {          
       if(response.getStatusCode() == HTTP_OK || response.getStatusCode() == HTTP_ACCEPTED) 
        {        
            oResponse = (GBEServiceResponse_H_HUM)System.JSON.deserialize(response.getbody(),GBEServiceResponse_H_HUM.class);            
        }              
       return oResponse;         
    }    
    
    /*
     * Method Name   :  groupInfoDivisonData()
     * Description   :  This is the used to process Group Info Division Data.
     * Return Type   :  void
     * Parameters    :  NA
    */
    @testVisible private void groupInfoDivisonData()
    {    
       try
        {
            bShowDivisionDetails = true;        
            if(oReturnedresponse.GroupInfo!= NULL)   groupInfoData();             
            if(oReturnedresponse.GroupInfo.DivisionList != NULL)   arrVitalityIndicatorDTO = oReturnedresponse.GroupInfo.DivisionList;           
            bNext = false;
            bPrevious = (iCount == 1 ? true : false);
            iSize = Integer.valueOf(oReturnedresponse.GroupInfo.DivisionRecordsTotalCount);            
            if(iSize <= iMaxRecordsToDisplay)
             {
                bNext = true;
                bPrevious = true;
                iCountPerPage = iSize;
             }
            else if(iTotalRec < iMaxRecordsToDisplay && Integer.valueOf(oReturnedresponse.GroupInfo.DivisionRecordsTotalCount) > iMaxRecordsToDisplay)
             {
                iSize = iTotalRec;
                bNext = true;
                iCountPerPage  = iTotalRec+iCount -1;
             }
            else
             {
                iSize = iMaxRecordsToDisplay;
                iCountPerPage = iCount+iSize-1;
             }            
            lstVitalityRecords = new List<GroupDetailSection_H_HUM>();             
            for(Integer i = 0; i < iSize; i++)
            {            
                if(arrVitalityIndicatorDTO != NULL && arrVitalityIndicatorDTO.division[i] != NULL)
                {                        
                     lstVitalityRecords.add(new GroupDetailSection_H_HUM(
	                     	objGrpAccount.Source_Customer_Key__c,                            
	                     	Boolean.ValueOf(arrVitalityIndicatorDTO.division[i].VitalityIndicator), 
	                        arrVitalityIndicatorDTO.division[i].SubGroupID, 
	                        arrVitalityIndicatorDTO.division[i].SubGroupName,
	                        arrVitalityIndicatorDTO.division[i].unitCount, 
	                        arrVitalityIndicatorDTO.division[i].Unit, 
	                        arrVitalityIndicatorDTO.division[i].EffectiveProvision));                                
                }             
            }  
        }
       catch(Exception ex)
        {
            bShowGroupDetails = false;
            HUMExceptionHelper.logErrors(ex, 'GroupDetailSection_C_HUM_V2', 'groupInfoDivisonData');
        }   
    }
    
    /*
     * Method Name   :  groupInfoData()
     * Description   :  This is the used to get GroupInfo Data
     * Return Type   :  void
     * Parameters    :  NA
    */
    @testVisible private void groupInfoData()
    {    
         if(!String.ISBLANK(oReturnedresponse.GroupInfo.EffectiveDate))    sMedicalEffDate = oReturnedresponse.GroupInfo.EffectiveDate;         
         if(!String.ISBLANK(oReturnedresponse.GroupInfo.DentalOriginalEffectiveDate))    sDentalEffDate = oReturnedresponse.GroupInfo.DentalOriginalEffectiveDate;         
         if(!String.ISBLANK(oReturnedresponse.GroupInfo.NextRenewalDate))    sNxtRenDt = oReturnedresponse.GroupInfo.NextRenewalDate;         
         sMedGrpEnrllCount = oReturnedresponse.GroupInfo.EnrolledSubscriberCountMedical;
         sDentalGrpEnrllCount = oReturnedresponse.GroupInfo.EnrolledSubscriberCountDental;
         if(!String.isBlank(sDentalGrpEnrllCount) && sDentalGrpEnrllCount.trim().equals('0'))    sDentalGrpEnrllCount = ''; 
         if(!String.isBlank(sMedGrpEnrllCount) && sMedGrpEnrllCount.trim().equals('0'))    sMedGrpEnrllCount = ''; 
         iTotalCount = Integer.Valueof(oReturnedresponse.GroupInfo.DivisionRecordsTotalCount);
    }    
    
   /**   
	 * Method to process the response from GBE Service 
	 * when user clicks on "Next" button on the page.
	 * @param   NA
	 * @return	Void 
	 */ 
    public void processResponseNextData() 
    {          
				 try
         {         
           HttpResponse response = Continuation.getResponse(this.requestLabel);             
           oReturnedresponse = checkResponse(response); 
             if(oReturnedresponse != NULL)
             {
                 bShowGroupDetails = true;
                  groupInfoDivisonData(); 
                
             }
             else
             {    
                  bShowGroupDetails = false;    
             }
         }
         catch(Exception ex)
         {
             bShowGroupDetails =  false;
             HUMExceptionHelper.logErrors(ex, 'GroupDetailSection_C_HUM_V2', 'processResponse');
         }
    } 
	
	 // EDI functionality starts
       /*
     * REQ-1138911- Integration for EDI Group 
     * @method - remote action method to parse the EDI service response
     * @param - groupId - which is actually Account.Source_Customer_Key__c value , if it is empty then Group Number
     * @return - Continuation Object
    */
   @RemoteAction
    global static Continuation getEDIDataCntrlMethod(string groupId){
    try{
        return PolicyMemberDetail_S_HUM.sendRequestEDI(groupId);
       }
    catch(Exception ex)
         {
             HUMExceptionHelper.logErrors(ex, 'GroupDetailSection_C_HUM_V2', 'getEDIDataCntrlMethod');
              return null;
         }
        
    } 
    
       /*
     * REQ-1138911- Integration for EDI Group 
     * Returns responses of continuation object
     * @method - Callback Method to parse the EDI service response 
     * @param - label object and continuation state object
     * @return - Object
    */
   global static object processEDIResponse(List < string > labels, Object State){ 
    try{
        return PolicyMemberDetail_S_HUM.processEDIResponse(labels, state);  
       }
    catch(Exception ex)
         {
             HUMExceptionHelper.logErrors(ex, 'GroupDetailSection_C_HUM_V2', 'processEDIResponse');
             return null;
         }     
    }
    
    //EDI functionality Ends
	
}