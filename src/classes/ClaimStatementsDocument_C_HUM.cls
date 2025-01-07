/*****************************************************************************************
* Apex class Name    : ClaimStatementsDocument_C_HUM
* Version            : 1.0
* Function           : This is Controller for ClaimStatementsDocument_VF_HUM 
* Test Class         : ClaimStatementsDocument_T_HUM

* Modification Log :
* Developer Name            Review Number           Date                       Description
-----------------------------------------------------------------------------------------------------------------------------------------------------
* Pallavi Ravishankar        27974                 05/10/2016                  Original Version
* Muralidhar Kollu           34263                 07/27/2016                  PPV defect D-00473 - added user friendly error message
* Prasanthi Kandula                                11/25/2019                  REQ - 416044 Replaced references for 'MobiusService_GetDocument' webservice callout with MobiusService_GetMobiusDocument
* Prasanthi Kandula                                02/01/2021                  US-1669846 SonarQube critical security fixes  
************************************************************************************************************************************************/
  Public with sharing class ClaimStatementsDocument_C_HUM
  {
      private ClaimMobiusDetails_S_HUM oStmtService =new ClaimMobiusDetails_S_HUM ();
      @TestVisible private String sRequest = '';
      public String sDocumentKey {get;set;}
      public Transient String sDocumentReturn{get;set;}
      public Boolean bShowServiceError{get;set;}
      
      /*
      * Method Name : ClaimStatementsDocument_C_HUM
      * Description : Constructor to initialize variables
      * Return type : NA
      * Parameter   : NA
      */
        public ClaimStatementsDocument_C_HUM()
        {
            sDocumentKey = ApexPages.currentPage().getParameters().get('DocumentKey').escapeHtml4();
            bShowServiceError=false;
            
        }
        
        /*
        * Method Name   :  initiateDocumentRequest
        * Description   :  This method is used to Call the Service while loading the page
        * Return Type   :  object
        * Parameters    :  NA
        */
        public object initiateDocumentRequest()
        { 
            ClaimMobiusDetails_S_HUM oStatementService = new ClaimMobiusDetails_S_HUM();
            MobiusServiceGetDocumentRequest_H_HUM oMobiusDocReq = new MobiusServiceGetDocumentRequest_H_HUM();
            oMobiusDocReq = oStatementService.assignValuesToDocumentHelper(sDocumentKey);
            if(oMobiusDocReq!= null)           
            {              
                HUM_Webservice_Callout__c oSvcMobiusDocument = HUM_Webservice_Callout__c.getValues('MobiusService_GetMobiusDocument'); // Replaced references for 'MobiusService_GetDocument' webservice callout with MobiusService_GetMobiusDocument
                Integer iTIMEOUT_INT_SECS = Integer.valueof(oSvcMobiusDocument.Timeout__c)/1000;
                Continuation con = new Continuation(iTIMEOUT_INT_SECS );
                con.continuationMethod='processDocumentResponse';
                string sRequestBody = JSON.Serialize(oMobiusDocReq);
                HttpRequest oRequest = new HttpRequest();
                oRequest =oStmtService.setupMobiusDocRequest(oRequest,oSvcMobiusDocument,sRequestBody);
                sRequest = con.addHttpRequest(oRequest);
                return con; 
            }     
            else
            {
                return null;
            }   
        }
       
        /*
        * Method Name   :  processDocumentResponse
        * Description   :  This method is used to get response from service.
        * Return Type   :  void 
        * Parameters    :  NA
        */
        public void processDocumentResponse()
        {
            HttpResponse response = new HttpResponse();
            try 
            {
                response = Continuation.getResponse(sRequest);
                ClaimStatementsDocument_DTO_HUM oDocDto=new ClaimStatementsDocument_DTO_HUM();
                ClaimStatementsDocument_DTO_HUM.getDocumentResponse ogetDocumentResponse=new ClaimStatementsDocument_DTO_HUM.getDocumentResponse();
                oDocDto=oStmtService.parseDocumentResponse(response);
                ogetDocumentResponse=oDocDTO.getDocumentResponse;
                sDocumentReturn=ogetDocumentResponse.getDocumentReturn;
            }
            catch(exception e)
            {
                bShowServiceError=true;
                if(response != null && response.getStatusCode() == 2004)
                {
                    ApexPages.Message myMsg = new ApexPages.Message(ApexPages.Severity.Error, Label.Claims_Statement_UnabletoLoad);
                    ApexPages.addMessage(myMsg);                                                                                            
                }
                else
                {
                    ApexPages.Message myMsg = new ApexPages.Message(ApexPages.Severity.Error, Label.ClaimsSummary_Service_Error);
                    ApexPages.addMessage(myMsg);                                                                                                                
                }
            }
        }
  }