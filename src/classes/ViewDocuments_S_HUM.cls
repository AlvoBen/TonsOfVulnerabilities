/**************************************************************************************************************************************************
Apex class Name    :  ViewDocuments_S_HUM
Version            :  1.0
Function           :  This is Service Helper class to Documents.It forms request and parse response.
Test Class         :  ViewDocuments_T_HUM

Modification Log:
 * Developer Name        Code Review #         Date                       Description
 *---------------------------------------------------------------------------------------------------
 * Ajay Chakradhar                             10/29/2018                 Original Version 
 * Swapnali Sonawane                           04/09/2021                 Req 2119511 Added Additional Data for G&A Status Page in CRM
 * Ravikanth Samudrala                         10/07/2022                 Everest/US -3683154 : T1PRJ0272555- MF 2 SF - TECH - SonarQube 2022 - CRM_MHK : SOQL and DML inside loops.
 ************************************************************************************************************************************************* */

public with sharing class ViewDocuments_S_HUM
{
    @TestVisible private static String sBody ='' ;
    public class ServiceDownException extends Exception {}
    
    /*
    *  This method is used to form HTTP Request for View Documents service
    *  @name   sendViewDocumentsRequest
    *  @param  string sContext,sMedhokId to get data from service
    *  @return HttpRequest - Object
    */   
    public static Continuation sendViewDocumentsRequest(string sContext,string sMedhokId)
    {
        HUM_Webservice_Callout__c oGAService = HUM_Webservice_Callout__c.getValues('ViewDocuments');
        Integer iTIMEOUT_INT_SECS = Integer.valueof(oGAService.Timeout__c)/1000;
        Continuation conGo = new Continuation(iTIMEOUT_INT_SECS);
        try
        {  
            sBody = buildViewDocumentsRequest(sContext,sMedhokId);            
            if(string.isNotBlank(sBody))
            {
                HttpRequest request = new HttpRequest();
                request.setMethod(oGAService.Request_Type__c); 
                request.setHeader(GLOBAL_CONSTANT_HUM.CONTENT_TYPE_HUM, oGAService.Content_Type__c);  
                request.setHeader(GLOBAL_CONSTANT_HUM.SOAPACTION_HUM, '');    
                request.setHeader(GLOBAL_CONSTANT_HUM.CONNECTION_HUM,GLOBAL_CONSTANT_HUM.KEEP_ALIVE_HUM);
                request.setBody(sBody);
                if(!Test.isRunningTest())    request.setClientCertificateName(oGAService.Certificate_Name__c);    
                request.setTimeout(Integer.valueof(oGAService.Timeout__c));
                request.setEndpoint(oGAService.End_Point_URL__c);                
                conGo.continuationMethod = 'processViewDocumentsResponse';
                conGo.addHttpRequest(request);
                if(Test.isRunningTest())  throw new HUMCustomException('Error');
            }
            else     return null;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'ViewDocuments_S_HUM', 'sendViewDocumentsRequest');   return null;
        }
        return conGo;  
    }
    
    /*
    *  This method is used to build the required inputs needed to View Documents service
    *  @name   buildViewDocumentsRequest
    *  @param  string sContext,sMedhokId to get data from service
    *  @return string - Returns the Request body in Serialized form
    */
    @testvisible private static string buildViewDocumentsRequest(string sContext,string sMedhokId)
    {   
        string sJsonBodyGo = '';
        try
        {
            if(string.isNotBlank(sContext) && string.isNotBlank(sMedhokId))
            {  
                DocumentListRequest_H_HUM oRequest = new DocumentListRequest_H_HUM();
                DocumentListRequest_H_HUM.GetCorrespondenceAndDocumentsRequest objGetCorrespondenceAndDocuments = new DocumentListRequest_H_HUM.GetCorrespondenceAndDocumentsRequest();
                
                objGetCorrespondenceAndDocuments.context = sContext;
                objGetCorrespondenceAndDocuments.medhokID = sMedhokId;
              
                oRequest.GetCorrespondenceAndDocumentsRequest = objGetCorrespondenceAndDocuments;
                sJsonBodyGo = JSON.serialize(oRequest);
            }
            else    sJsonBodyGo =  null;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'ViewDocuments_S_HUM', 'buildViewDocumentsRequest');   sJsonBodyGo  =  NULL;
        }
        return sJsonBodyGo ;
    }
    
    /*
    *  This method is used to Parse response from View Documents service
    *  @name   processViewDocumentsResponse
    *  @param  List and object
    *  @return object
    */
    @testvisible
    public static object processViewDocumentsResponse(List<string> labels, object State)
    {
        HttpResponse sResponse = Continuation.getResponse(labels[0]);        
        List<DocumentList_DTO_HUM> lstDocumentsDTO = new List<DocumentList_DTO_HUM >();
       Try{
         if ((sResponse != null && 
              !sResponse.getBody().containsIgnoreCase(GLOBAL_CONSTANT_HUM.VOB_FAULT_CODE) 
              && (sResponse.getStatusCode() == GLOBAL_CONSTANT_HUM.HTTP_OK_HUM || sResponse.getStatusCode() == GLOBAL_CONSTANT_HUM.HTTP_ACCEPTED_HUM))
             || Test.isRunningTest())
       {
           DocumentListResponse_H_HUM objresp = new DocumentListResponse_H_HUM();
           objresp = (DocumentListResponse_H_HUM)System.JSON.deserialize(sResponse.getbody(),DocumentListResponse_H_HUM.class);
                      
           if (objresp != null){  
				return lstDocumentsDTO = processResponse(objresp);
           } 
        }
         else{              
                throw new ServiceDownException('SERVICEERROR::'+sResponse.getBody() ); 
             }
        }
        catch(ServiceDownException e)
        {
            List<Error_Log__c> errorLogList = new  List<Error_Log__c>();
            Error_Log__c logObj = HUMExceptionHelper.logOtherExceptions(e, 'ViewDocuments_S_HUM','SERVICE_FAILURE'); 
			errorLogList.add(logObj);
            string errorNumber='';
                
               Database.SaveResult[] srList =  Database.insert(errorLogList, false);
               Set<Id> errorIdSet = new Set<Id>();
               for (Database.SaveResult sr : srList) 
               {
                    if (sr.isSuccess()) { 
                        errorIdSet.add(sr.getId());
						
                    }
               }     
             
             errorLogList = null;
             errorLogList = [Select Name from Error_Log__c where Id IN :errorIdSet];
             if(errorLogList != null && errorLogList.Size() > 0)
             {
                 errorNumber = errorLogList[0].Name;
             }          
             
             DocumentList_DTO_HUM dlg= new DocumentList_DTO_HUM();  
			 dlg.internalError= 'TESTFAILURE'+'*'+ errorNumber  + '#';
             lstDocumentsDTO.add(dlg);  
			 return lstDocumentsDTO;
        }
        catch (Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'ViewDocuments_S_HUM', 'processViewDocumentsResponse');
            return null;
        }
        return null;
    }
    
    /**
    * This method convert the date in MM/DD/YYYY format .
    * @Nmae     DateConverter
    * @param    sDate
    * @return   String
    */
 	public static String DateConverter(String sDate)
    {
        
        String [] dateArray = sDate.split('-');
        String dueDate = '',day='',month='',deterDate='';
        if(dateArray[2].length() == 1){ day = '0'+dateArray[2];
                                      }else{
                                          day = dateArray[2];
                                      }    
        if(dateArray[1].length() == 1){ month = '0'+dateArray[1];
                                      }else{
                                          month = dateArray[1];
                                      }
        dueDate = month+'/'+day+'/'+dateArray[0];
        return dueDate;                
    }
    
    /**
    * This method generates list of Greivance And Appeal cases associated documents to the account.
    * @Nmae     processResponse
    * @param    DocumentListResponse_H_HUM response
    * @return   List of DocumentList_DTO_HUM
    */
    public static list<DocumentList_DTO_HUM> processResponse(DocumentListResponse_H_HUM resViewDocuments)
    {
		Boolean bSwitchCaseDoc;
        bSwitchCaseDoc=  HUMUtilityHelper.isCRMFunctionalityON('2119511');
		
        list<DocumentList_DTO_HUM> lstViewDocuments = new list<DocumentList_DTO_HUM>();
        
        DocumentListResponse_H_HUM.GetCorrespondenceAndDocumentsResponse  oCADResponse = 
        resViewDocuments.GetCorrespondenceAndDocumentsResponse;        
        
        List<DocumentListResponse_H_HUM.CorrespondencesAndDocuments> lstCorrAndDocuments = oCADResponse.CorrespondencesAndDocuments;
        for (DocumentListResponse_H_HUM.CorrespondencesAndDocuments objCorrAndDocumts : lstCorrAndDocuments)
        {
            DocumentList_DTO_HUM oDocumentsWrap = new DocumentList_DTO_HUM();
               
            oDocumentsWrap.sfileName =    objCorrAndDocumts.fileName+ ',fnOpenAttachment,'+ objCorrAndDocumts.documentId;
           
            oDocumentsWrap.sDocumentType = objCorrAndDocumts.documentType;
            oDocumentsWrap.sDocumentId = objCorrAndDocumts.documentId;
            oDocumentsWrap.sCreatedby = objCorrAndDocumts.userId;
            if (bSwitchCaseDoc == true)
            {
                //Document Created 
                string DocCreated='';
                if(objCorrAndDocumts.docSource=='Correspondence')    
                {
                        if(String.isBlank(objCorrAndDocumts.CreateDate))
                        {
                            oDocumentsWrap.sCreateDate = '';
                        }
                        else
                        {
                            DocCreated = objCorrAndDocumts.CreateDate;
                            oDocumentsWrap.sCreateDate = DateConverter(DocCreated.substring(0,10));
                        }
                }
                else if(objCorrAndDocumts.docSource=='Document') 
                {
                    
                     if(String.isBlank(objCorrAndDocumts.attachmentReceivedDate))
                     {
                           oDocumentsWrap.sCreateDate = '';
                     }
                     else
                     {
                           DocCreated =  objCorrAndDocumts.attachmentReceivedDate;
                           oDocumentsWrap.sCreateDate = DateConverter(DocCreated.substring(0,10));
                     }
                }
            }
            lstViewDocuments .add(oDocumentsWrap);    
        }
       
        return lstViewDocuments;
    }
   
}