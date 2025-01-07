/*****************************************************************************************
Apex Class Name :  RTIPrintSummary_C_HUM
Version: 1.0
Function           : This Is the controller class for RTI Print View Link.
Created Date       :  20 June 2017
Test Class         : RTIPrintSummary_T_HUM

        Modification Log: 
 *   Developer                   Code Review             Date               Description
 * ------------------------------------------------------------------------------------------------------------------------------   
 *  Shivam Anand                   106733               06/20/2017         Original Version
 *  Imran                                               26/02/2020         RTI VAT Changes
 *  Imran                                               13/03/2020         Req#436960 RTI VAT Changes Logo Issue
 *  Anjani Vaddadi                                      03/24/2020         RTI-MF4-437065
 *  Anjani Vaddadi                                      07/03/2020         US - 1125628- Pass Message Definition Code (MDC) on Outbound Communications attached to CRM Cases via RTI
 *  Trina Ghosh                                         02/09/2021         US:1669848 - Low Security Issues Fix
 *  Rahul Chaurasia								   		03/22/2021		   1931279 - PR00094226-MF 4-Tech-Decommissioned RTI Service
 *  Prasanthi Kandula                                   05/18/2021         US - 2082407 Added new card for channel Email
 *  Anuradha Gajbhe                                     05/18/2021         US - 2082385 Added logic to display new card for channel Text
 *  Rahul Chaurasia                                     05/19/2021         US - 2082359 Added logic to display new card for channel Web
 *********************************************************************************************************************************/

Public with sharing Class RTIPrintSummary_C_HUM
{
    @TestVisible private RTIPrintSummary_S_HUM oServiceRTI = New RTIPrintSummary_S_HUM();
    @TestVisible private String sInteractionKey = '';
    @TestVisible private Integer iSkip;
    @TestVisible private Integer iRecordLimit;
    @TestVisible private String sRequest = '';
    private boolean bIsDataAvailable;
    public List<RTIPrintSummary_DTO_HUM> lstRTIInteractions {get; private set;}
    public Boolean bIsError{get; private set;}
    public String sViewUrl {get;set;}
    public String siconPath {get;set;}
    public String sdocumentViewUrl {get;set;}        
    public Boolean bShowServiceError{get;set;} 
    public Boolean bServiceCheck{get; set;}
    public string sChannelName{get;set;}
    //MF4
    Public String sDateM{Get;Set;}
    Public String sCategoryDescription{Get;Set;}
    Public string sSubArea{Get;Set;}
    Public string smessageprintParam{Get;Set;}
    Public string sCampaignName{Get;Set;}    
    Public string sStatusDescription{Get;Set;}
    Public string sPhoneNumber{Get;Set;}
    Public string ssubStatusDesc{Get;Set;}
    Public string sTrackingId{Get;Set;}
    Public string sObjectID{Get;Set;}
    Public string statusCode{Get;Set;}
    Public Boolean bLogRTIToCase{Get;Set;}
    public string sSourceCode{get;set;}
    public string directionCode{get;set;}
    public boolean bPrintable {get; set;}
    public boolean bDisplaySendLink {get; set;}
    public string sMessageDefCodes{get; set;}
    @TestVisible  public string sChannelCode 
    {
        get
           {        
            return sChannelCode;            
           }
           set
           {    
            sChannelCode =value;
            if(sChannelCode!=null) sChannelCode =sChannelCode.Trim().toUpperCase();
            bServiceCheck=sChannelCode  == 'VAT';
           }
    }

   
             
    Public List<Error_Log__c> lstLogErrors
    {
        get
        {
            If (lstLogErrors == Null) 
                            {
                lstLogErrors = New List < Error_Log__c > ();
            } 
            Return lstLogErrors;
        }
        set;
    }
    
    /*
    * Method Name   :  RTIPrintSummary_C_HUM
    * Description   :  This Is a constructor for class RTIPrintSummary_C_HUM
    * Return Type   :  NA
    * Parameters    :  NA
    */
    Public RTIPrintSummary_C_HUM()
    {
        sObjectID = Apexpages.currentpage().getparameters().get('acctId');
        sCampaignName = Apexpages.currentpage().getparameters().get('CampaignName');
        sStatusDescription = Apexpages.currentpage().getparameters().get('StatusDescription');
        sPhoneNumber = ApexPages.currentPage().getparameters().get('PhoneNumber');
        ssubStatusDesc = ApexPages.currentPage().getparameters().get('subStatusDesc');
        if(ApexPages.currentPage().getparameters().get('InterKey')  != Null)
            sInteractionKey = ApexPages.currentPage().getparameters().get('InterKey').escapeHtml4();
        sDateM = Apexpages.currentPage().getparameters().get('DateM');
        smessageprintParam = ApexPages.Currentpage().getparameters().get('MessagePrintParm');
        sSubArea = Apexpages.currentpage().getparameters().get('SubArea');        
        sCategoryDescription = Apexpages.currentpage().getparameters().get('CategoryDesc');
        sSourceCode = Apexpages.currentpage().getparameters().get('sourceCode');
        sTrackingId = Apexpages.currentPage().getParameters().get('trackingID');
        directionCode = Apexpages.currentPage().getParameters().get('directionCode');
        iSkip = 0;        
        if(ApexPages.currentPage().getparameters().get('CategoryDesc')  != Null)
        {   
            sChannelCode = ApexPages.currentPage().getParameters().get('CategoryDesc').escapeHtml4();  
        }
            
        iRecordLimit = 200;
        bIsError = False;
        bIsDataAvailable = True;
        bShowServiceError = False;
        sChannelName = Apexpages.currentpage().getparameters().get('sChannelCode');
        bPrintable = Apexpages.currentpage().getparameters().get('bPrintable') == 'true';
        sMessageDefCodes = ApexPages.currentPage().getParameters().get('MsgDefCode');        
    }
    
   /*
    * Method Name   :  initiateRequest
    * Description   :  This method Is used to call the Service when clicked on View Link on RTIPrintDetails Page
    * Return Type   :  continuation
    * Parameters    :  NA
    */       
    Public Object initiateRequest()
    { 
        Try
        {
            sRequest = ''; 
            HUM_Webservice_Callout__c oSvcHeaderRTI;          
            If(String.isNotBlank(sInteractionKey))
            {
                  oSvcHeaderRTI = HUM_Webservice_Callout__c.getValues('RTIService_GetInteractionDetails_V2'); 
                  Map<string,Id> mapRTIIcons = new Map<string,Id>();
                    for(Document oDocument: [SELECT Id, Name FROM Document where Name ='RTI VAT Card Icon' or Name='RTI Email Icon' or Name='RTI Text Icon'])
                    {
                        mapRTIIcons.put(oDocument.Name,oDocument.Id);
                    } 
                    if(sChannelName.equalsIgnoreCase('VAT')) siconPath = '/servlet/servlet.FileDownload?file='+mapRTIIcons.get('RTI VAT Card Icon');
                    if(sChannelName.equalsIgnoreCase('EMAIL')) siconPath = '/servlet/servlet.FileDownload?file='+mapRTIIcons.get('RTI Email Icon');  
                    if(sChannelName.equalsIgnoreCase('Text')) siconPath = '/servlet/servlet.FileDownload?file='+mapRTIIcons.get('RTI Text Icon');
                Integer iTIMEOUT_INT_SECS = Integer.valueof(oSvcHeaderRTI.Timeout__c) / 1000;
                HttpRequest request = New HttpRequest();
                oServiceRTI.setupRequest(request, oSvcHeaderRTI, sInteractionKey, sChannelName, iSkip, iRecordLimit);   
                Continuation con = New Continuation(iTIMEOUT_INT_SECS);                      
                sRequest = con.addHttpRequest(request);                                        
                con.continuationMethod ='processResponse';                     
                return con;
            }
            Else
            {
                bShowServiceError = True;
                String sError = System.Label.RTIPrintSummary_No_records ;
                displayErrorMessage(True, sError, 'No Records');
                Return Null;            
            }
        }
        Catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'RTIPrintSummary_C_HUM' , 'initiateRequest');
            return NULL;          
        }
   }
    
    /*
    * Method Name   :  processResponse
    * Description   :  This method Is used to get response from service.
    * Return Type   :  void
    * Parameters    :  NA
    */
    Public void processResponse()
    {
        Try
        {            
            RTIPrintSummary_DTO_HUM RTIInteraction = New RTIPrintSummary_DTO_HUM();
            HttpResponse response = Continuation.getResponse(sRequest);            
            RTIInteraction = responseparse(response);            
            sdocumentViewUrl = RTIInteraction.sdocumentViewUrl; 
            bDisplaySendLink = (bPrintable && string.isNotBlank(sdocumentViewUrl) && string.isNotBlank(RTIInteraction.sdocumentType) && !RTIInteraction.sdocumentType.equalsIgnoreCase('SAMPLE'))?true:false;         
        }
        Catch(Exception ex)
        {
            bShowServiceError = True;
            String sError = System.Label.RTISummary_Service_Error;
            displayErrorMessage(True, System.Label.RTIPrintSummary_Service_Error, 'Service Error');
            HUMExceptionHelper.bRestrictLog = True;
            lstLogErrors = HUMExceptionHelper.logErrors(ex,'RTIPrintSummary_C_HUM','processResponse');           
        }
    }
    
 
   /*
    * Method Name   :  responseParse
    * Description   :  This method Is used to get response from service.
    * Return Type   :  void
    * Parameters    :  NA
    */
    @testvisible public RTIPrintSummary_DTO_HUM responseParse(HttpResponse sResponse)
    {
        lstRTIInteractions = New List < RTIPrintSummary_DTO_HUM > ();
        RTIPrintSummary_DTO_HUM RTIInteraction = New RTIPrintSummary_DTO_HUM();
        Integer iStatusCode = sResponse.getStatusCode();                     
        If (iStatusCode == 200 || iStatusCode == 202)
        {
            lstRTIInteractions = oServiceRTI.processResponse(sResponse.getbody());                        
            If (lstRTIInteractions == null || lstRTIInteractions.isEmpty() )
            {
                String sError = System.Label.RTIPrintSummary_No_records ;
                displayErrorMessage(True, sError, 'No Records');
            }
            Else
            {            
                For (RTIPrintSummary_DTO_HUM obj : lstRTIInteractions)   
                { 
                  RTIInteraction.sdocumentViewUrl = obj.sdocumentViewUrl;  
                  RTIInteraction.sdocumentType = obj.sdocumentType;
                }
            }
        }
        Else
        {
            bShowServiceError = True;
            String sError = System.Label.RTIPrintSummary_Service_Error;
            displayErrorMessage(True, sError, 'Service Error');
        }
        Return RTIInteraction;
    }
  
   /*
    * Method Name : displayErrorMessage
    * Description : This method Is used to display error message on UI.
    * Return type : void
    * Parameter   : Boolean , String 
    */
    @testVisible private void displayErrorMessage(Boolean bError, String sError, String errorType)
    {
        If (bError) 
        { 
            bIsDataAvailable = True;
            bIsError = bError;
            If (errorType.equalsIgnorecase('No Records')) 
            { 
                HUMExceptionHelper.addMessageToPage(ApexPages.Severity.Warning, sError, True);
            }
            If (errorType.equalsIgnorecase('Service Error')) 
            {
                HUMExceptionHelper.addMessageToPage(ApexPages.Severity.Error, sError, True);
            }
            bIsDataAvailable = False; 
        }
    }                    
 }