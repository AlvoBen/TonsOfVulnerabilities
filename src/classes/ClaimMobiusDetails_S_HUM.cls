/**************************************************************************** 
Apex Class Name  : ClaimMobiusDetails_S_HUM 
Version          : 1.0  
Created Date     : May 12, 2016
Function         : Service that returns member claim statements and forms request for getting the claims statement document
Test Class       : ClaimMobiusDetailsService_T_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Muralidhar Kollu         27974             05/12/2016                    Original Version
* Pallavi Ravishankar      27974             05/18/2016                    Added logic for calling service for 
                                                                            getting claims document response
* Muralidhar Kollu         218524             04/20/2018                    Mobius issue fix
* Muralidhar Kollu                           10/17/2018                    extra null checks in processResponse method
* Muralidhar Kollu         355971            05/17/2019                    REQ - 397216
* Anjani Vaddadi                             02/25/2020                    REQ-764250
* Ranadheer                                  04/19/2021                    US 2187946, Convert the Memeber Gen Key to match the backend 
***************************************************************************************************/
public with sharing class ClaimMobiusDetails_S_HUM
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
    * Return Type   :  List<ClaimsPaymentDetails_DTO_HUM>
    * Parameters    :  string(Json response), string(statementType)
    */
    public List<ClaimsDocumentsList_DTO_HUM> processResponse(string response, string statementType, Id sPolMemId)
    {
        try
        {
            MobiusServiceGetDocListResponse_H_HUM objResp = new MobiusServiceGetDocListResponse_H_HUM();
            List<ClaimsDocumentsList_DTO_HUM> listClaimStatementsResp = new List<ClaimsDocumentsList_DTO_HUM>();
            List<Policy_Member__c> lstMember_Records = ClaimDetails_D_HUM_V2.getMemberGenKey(sPolMemId);
            List<Policy_Member__c> lstSubscriber_Record;
            if(lstMember_Records[0].Subscriber__c!=NULL && !(lstMember_Records.isEmpty())){
            	lstSubscriber_Record = ClaimDetails_D_HUM_V2.getMemberGenKey(lstMember_Records[0].Subscriber__c);
            }
            List<String> lstClaimStatementsType = new List<String>{'COM','PDP','SSA','FAM','H1F','H1I','MADP','EBSF','EBSI','XBSF','XBSI','ZBSI','EOBSTMT','Blank(EOBSTMT)','or null'};
            if(response != null) objResp = (MobiusServiceGetDocListResponse_H_HUM)System.Json.deserialize(response,MobiusServiceGetDocListResponse_H_HUM.class);

            if((objResp != null) && (objResp.getDocumentListResponse != null) && (objResp.getDocumentListResponse.getDocumentListReturn != null) && (objResp.getDocumentListResponse.getDocumentListReturn.DocumentSearch != null))
            {            
                List<MobiusServiceGetDocListResponse_H_HUM.DocumentSearch> lstDoc = objResp.getDocumentListResponse.getDocumentListReturn.DocumentSearch;
                
                for(MobiusServiceGetDocListResponse_H_HUM.DocumentSearch objDoc:lstDoc)
                {
                    List<MobiusServiceGetDocListResponse_H_HUM.TopicResult> lstTR = objDoc.TopicResultList.TopicResult;
                    for(MobiusServiceGetDocListResponse_H_HUM.TopicResult objTR:lstTR)
                    {
                        List<MobiusServiceGetDocListResponse_H_HUM.TopicKey> lstTK = objTR.TopicKeyList.TopicKey;                        
                        for(MobiusServiceGetDocListResponse_H_HUM.TopicKey objTK:lstTK)
                        {   
                            //converting the received MemberGenKey from backend to Integer to remove preceding zeros and then converting back to String used to compare below.
                            String convertedMbrGenKey; 
                            if(String.isNotBlank(objTK.MemberGenKey)) {
                                convertedMbrGenKey =String.valueOf(Long.valueOf(objTK.MemberGenKey));
                            }
                            
                            String docKey = objTR.DocumentKey;
                            ClaimsDocumentsList_DTO_HUM objwrapper = new ClaimsDocumentsList_DTO_HUM();
                            objwrapper.sDocumentKey = '/apex/MobiusDocView_VF_HUM?DocumentKey='+ docKey;
                            if(objTR.PrintedDate != null)    objwrapper.sPrintedDate = objTR.PrintedDate.substring(5, 7) + '/' + objTR.PrintedDate.substring(8, 10) + '/' +objTR.PrintedDate.substring(0, 4);
                            objwrapper.sTopicName = objTK.topicName;
                            if(objTK.statementBeginDate != null && 7 <= objTK.statementBeginDate.length())    objwrapper.sStatementBeginDate= objTK.statementBeginDate.substring(5, 7) + '/' + objTK.statementBeginDate.substring(8) + '/' +objTK.statementBeginDate.substring(0, 4);
                            if(objTK.statementEndDate != null && 7 <= objTK.statementEndDate.length())    objwrapper.sStatementEndDate= objTK.statementEndDate.substring(5, 7) + '/' + objTK.statementEndDate.substring(8) + '/' +objTK.statementEndDate.substring(0, 4);
                            objwrapper.sStatementType = objTK.statementType;
                            objwrapper.sStatementDescription = objTK.statementDescription;
                            objwrapper.sPersonId = objTK.PersonId;  
                            if(statementType == 'Provider') objwrapper.sRemitId = objTK.RemitId;                                                                                         
                            objwrapper.MemberGenKey =  objTK.MemberGenKey;                                                                                         
                            if(lstClaimStatementsType.contains(objwrapper.sStatementType)){
                            if(lstMember_Records[0].Subscriber__c!=NULL && !(lstMember_Records.isEmpty())){
                                if(lstSubscriber_Record[0].Member__r.Mbr_Gen_Key__c == convertedMbrGenKey || lstMember_Records[0].Member__r.Mbr_Gen_Key__c == convertedMbrGenKey ){
                                  listClaimStatementsResp.add(objwrapper);  
                                }                                
                            }
                            else if (lstMember_Records[0].Member__r.Mbr_Gen_Key__c == convertedMbrGenKey){
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
            HUMExceptionHelper.logErrors(ex, 'ClaimMobiusDetails_S_HUM', 'processResponse');
            return Null;
        }
    }
    
       /*
       * Returns an object 
       * Method Name: assignValuesToDocumentHelper
       * This method is used to assign values to the request helper class
       * Return type: MobiusServiceGetDocumentRequest_H_HUM
       * Param: String
       */   
       public MobiusServiceGetDocumentRequest_H_HUM assignValuesToDocumentHelper(String sDocumentKey )
       {
           MobiusServiceGetDocumentRequest_H_HUM oMobiusDocReq = new MobiusServiceGetDocumentRequest_H_HUM();
           MobiusServiceGetDocumentRequest_H_HUM.getDocument oDoc = new MobiusServiceGetDocumentRequest_H_HUM.getDocument();
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
       * Return Type   :  ClaimStatementsDocument_DTO_HUM
       * Parameters    :  HttpResponse response
       */
       public ClaimStatementsDocument_DTO_HUM parseDocumentResponse(HttpResponse response)
       { 
       if(response!=null)
        {
           MobiusServiceGetDocumentResponse_H_HUM oResponse = new MobiusServiceGetDocumentResponse_H_HUM(); 
           MobiusServiceGetDocumentResponse_H_HUM.getDocumentResponse getDocResponse = new MobiusServiceGetDocumentResponse_H_HUM.getDocumentResponse(); 
           oResponse = (MobiusServiceGetDocumentResponse_H_HUM)System.JSON.deserialize(response.getbody(),MobiusServiceGetDocumentResponse_H_HUM.class); 
           ClaimStatementsDocument_DTO_HUM oDocDTO=new ClaimStatementsDocument_DTO_HUM();
           ClaimStatementsDocument_DTO_HUM.getDocumentResponse ogetDocumentResponse=new ClaimStatementsDocument_DTO_HUM.getDocumentResponse();
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