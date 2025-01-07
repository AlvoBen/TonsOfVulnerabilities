/**************************************************************************** 
Apex Class Name  : ClaimMobiusDetails_LS_HUM 
Version          : 1.0  
Created Date     : 06/01/2022
Function         : Service that returns member claim statements and forms request for getting the claims statement document
Test Class       : ClaimMobiusDetailsService_LT_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Prashant Moghe 	   	                   		06/01/2022                 Original Version
* Anuradha Gajbhe								09/28/2022				   Enabling Member Claim Statements Tab Capabilities Member/Provider Claims Statements- View LWC Convertion
* Sagar Gulleve	                   		        10/11/2022                 DF-6312 Fix
* Anuradha Gajbhe                               10/25/2023                 US: 5211327- TECH - Regression DF 8177 - The View link is not working from process section of the Member and provider claim statements attached to case
***************************************************************************************************/
public with sharing class ClaimMobiusDetails_LS_HUM
{
    
    /*
     * Method Name   :  setupRequest
     * Description   :  This method is used to form http request.
     * Return Type   :  HttpRequest 
     * Parameters    :  HttpRequest, HUM_Webservice_Callout__c, String 
     */    
    public HttpRequest setupRequest(HttpRequest oRequest,HUM_Webservice_Callout__c oSvcHeaderPayments, String sRequestBody)
    {
        oRequest.setMethod(oSvcHeaderPayments.Request_Type__c);
        oRequest.setBody(sRequestBody);
        oRequest.setEndpoint(oSvcHeaderPayments.End_Point_URL__c);
         if(!Test.isRunningTest())    
        {
           oRequest.setClientCertificateName(oSvcHeaderPayments.Certificate_Name__c);
        }
        oRequest.setTimeout(Integer.valueof(oSvcHeaderPayments.Timeout__c));
        oRequest.setHeader(GLOBAL_CONSTANT_HUM.Content_type_HUM, oSvcHeaderPayments.Content_Type__c);
        oRequest.setHeader('SOAPAction', '');    
        oRequest.setHeader('Connection','keep-alive');
        return oRequest;          
    }
    
    /*
    * Method Name   :  processResponse
    * Description   :  This method Is used to Process the Json response from service to Wrapper List
    * Return Type   :  List<ClaimsPaymentDetails_LDTO_HUM>
    * Parameters    :  string(Json response), string(statementType)
    */
	@AuraEnabled
    public static List<ClaimsDocumentsList_LDTO_HUM> processResponse(string response, string statementType, Id sPolMemId)
    {
        try
        {
            MobiusServiceGetDocListResponse_LH_HUM objResp = new MobiusServiceGetDocListResponse_LH_HUM();
            List<ClaimsDocumentsList_LDTO_HUM> listClaimStatementsResp = new List<ClaimsDocumentsList_LDTO_HUM>();
            List<MemberPlan> lstMember_Records = ClaimDetails_LD_HUM_V2.getMemberGenKey(sPolMemId);
            List<MemberPlan> lstSubscriber_Record;
            if(lstMember_Records[0].SubscriberPlanId__c!=NULL && !(lstMember_Records.isEmpty())){
            	lstSubscriber_Record = ClaimDetails_LD_HUM_V2.getMemberGenKey(lstMember_Records[0].SubscriberPlanId__c);
            }
            List<String> lstClaimStatementsType = new List<String>{'COM','PDP','SSA','FAM','H1F','H1I','MADP','EBSF','EBSI','XBSF','XBSI','ZBSI','EOBSTMT','Blank(EOBSTMT)','or null'};
            if(response != null) objResp = (MobiusServiceGetDocListResponse_LH_HUM)System.Json.deserialize(response,MobiusServiceGetDocListResponse_LH_HUM.class);

            if((objResp != null) && (objResp.getDocumentListResponse != null) && (objResp.getDocumentListResponse.getDocumentListReturn != null) && (objResp.getDocumentListResponse.getDocumentListReturn.DocumentSearch != null))
            {            
                List<MobiusServiceGetDocListResponse_LH_HUM.DocumentSearch> lstDoc = objResp.getDocumentListResponse.getDocumentListReturn.DocumentSearch;
                
                for(MobiusServiceGetDocListResponse_LH_HUM.DocumentSearch objDoc:lstDoc)
                {
                    List<MobiusServiceGetDocListResponse_LH_HUM.TopicResult> lstTR = objDoc.TopicResultList.TopicResult;
                    for(MobiusServiceGetDocListResponse_LH_HUM.TopicResult objTR:lstTR)
                    {
                        List<MobiusServiceGetDocListResponse_LH_HUM.TopicKey> lstTK = objTR.TopicKeyList.TopicKey;                        
                        for(MobiusServiceGetDocListResponse_LH_HUM.TopicKey objTK:lstTK)
                        {   
                            //converting the received MemberGenKey from backend to Integer to remove preceding zeros and then converting back to String used to compare below.
                            String convertedMbrGenKey; 
                            if(String.isNotBlank(objTK.MemberGenKey)) {
                                convertedMbrGenKey =String.valueOf(Long.valueOf(objTK.MemberGenKey));
                            }
                            
                            String docKey = objTR.DocumentKey;
                            ClaimsDocumentsList_LDTO_HUM objwrapper = new ClaimsDocumentsList_LDTO_HUM();
                            objwrapper.sDocumentKey = '/lightning/n/mobius_Doc_View_Hum?c__DocumentKey='+ docKey;
							if(objTR.PrintedDate != null)    objwrapper.sPrintedDate = objTR.PrintedDate.substring(0, 4) + '-' + objTR.PrintedDate.substring(5, 7) + '-' +objTR.PrintedDate.substring(8, 10) ;
							if(objTK.statementBeginDate != null && 7 <= objTK.statementBeginDate.length())    objwrapper.sStatementBeginDate= objTK.statementBeginDate;
							if(objTK.statementEndDate != null && 7 <= objTK.statementEndDate.length())    objwrapper.sStatementEndDate= objTK.statementEndDate;
                            objwrapper.sTopicName = objTK.topicName;
                            objwrapper.sStatementType = objTK.statementType;
                            objwrapper.sStatementDescription = objTK.statementDescription;
                            objwrapper.sPersonId = objTK.PersonId;  
                            if(statementType == 'Provider') objwrapper.sRemitId = objTK.RemitId;                                                                                         
                            objwrapper.MemberGenKey =  objTK.MemberGenKey;                                                                                         
                            if(lstClaimStatementsType.contains(objwrapper.sStatementType)){
                            if(lstMember_Records[0].SubscriberPlanId__c!=NULL && !(lstMember_Records.isEmpty())){
                                if(lstSubscriber_Record[0].Member.Mbr_Gen_Key__c == convertedMbrGenKey || lstMember_Records[0].Member.Mbr_Gen_Key__c == convertedMbrGenKey ){
                                  listClaimStatementsResp.add(objwrapper);  
                                }                                
                            }
                            else if (lstMember_Records[0].Member.Mbr_Gen_Key__c == convertedMbrGenKey){
                                  listClaimStatementsResp.add(objwrapper);  
                                }                         
                            }
                            else
                            {
                               listClaimStatementsResp.add(objwrapper);   
                            }
                            
                        }                
                    }                
                
                }
            }
            return listClaimStatementsResp;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'ClaimMobiusDetails_LS_HUM', 'processResponse');
            return Null;
        }
    }
    
       /*
       * Returns an object 
       * Method Name: assignValuesToDocumentHelper
       * This method is used to assign values to the request helper class
       * Return type: MobiusServiceGetDocumentRequest_LH_HUM
       * Param: String
       */   
       public MobiusServiceGetDocumentRequest_LH_HUM assignValuesToDocumentHelper(String sDocumentKey )
       {
           MobiusServiceGetDocumentRequest_LH_HUM oMobiusDocReq = new MobiusServiceGetDocumentRequest_LH_HUM();
           MobiusServiceGetDocumentRequest_LH_HUM.getDocument oDoc = new MobiusServiceGetDocumentRequest_LH_HUM.getDocument();
           if(String.isNotBlank(sDocumentKey))
           {
              oDoc.Documentkey=sDocumentKey;
              oMobiusDocReq.getDocument = oDoc;             
              return oMobiusDocReq ;
           }
           else
           {
               return null;
           }
      }
      
      /*
       * Method Name   :  setupMobiusDocRequest
       * Description   :  This method is used to form http request for  ClaimStatementsDocument_C_HUM
       * Return Type   :  HttpRequest 
       * Parameters    :  HttpRequest,HUM_Webservice_Callout__c,string 
       */
       public HttpRequest setupMobiusDocRequest(HttpRequest oRequest,HUM_Webservice_Callout__c oSvcMobiusDocument,String sRequestBody)
       { 
           oRequest.setMethod(oSvcMobiusDocument.Request_Type__c);  
           oRequest.setBody(sRequestBody);
           oRequest.setHeader('content-type', oSvcMobiusDocument.Content_Type__c); 
           oRequest.setHeader('SOAPAction', '');    
           oRequest.setHeader('Connection','keep-alive');
           if(!Test.isRunningTest())oRequest.setClientCertificateName(oSvcMobiusDocument.Certificate_Name__c);
           oRequest.setTimeout(GLOBAL_CONSTANT_HUM.Service_Timeout_HUM);
           oRequest.setEndpoint(oSvcMobiusDocument.End_Point_URL__c); 
           return oRequest;    
       }
       
       /*
       * Method Name   :  parseDocumentResponse
       * Description   :  This method is used to parse the httpResponse for ClaimStatementsDocument_C_HUM
       * Return Type   :  ClaimStatementsDocument_LDTO_HUM
       * Parameters    :  HttpResponse response
       */
       public ClaimStatementsDocument_LDTO_HUM parseDocumentResponse(HttpResponse response)
       { 
       if(response!=null)
        {
           MobiusServiceGetDocumentResponse_LH_HUM oResponse = new MobiusServiceGetDocumentResponse_LH_HUM(); 
           MobiusServiceGetDocumentResponse_LH_HUM.getDocumentResponse getDocResponse = new MobiusServiceGetDocumentResponse_LH_HUM.getDocumentResponse(); 
           oResponse = (MobiusServiceGetDocumentResponse_LH_HUM)System.JSON.deserialize(response.getbody(),MobiusServiceGetDocumentResponse_LH_HUM.class); 
           ClaimStatementsDocument_LDTO_HUM oDocDTO=new ClaimStatementsDocument_LDTO_HUM();
           ClaimStatementsDocument_LDTO_HUM.getDocumentResponse ogetDocumentResponse=new ClaimStatementsDocument_LDTO_HUM.getDocumentResponse();
           getDocResponse=oResponse.getDocumentResponse; 
           ogetDocumentResponse.getDocumentReturn=getDocResponse.getDocumentReturn;
           oDocDTO.getDocumentResponse=ogetDocumentResponse;
           return oDocDTO;
       }
       else
       {
           return null;
       }
       }
}