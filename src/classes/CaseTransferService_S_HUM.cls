/*****************************************************************************************
Apex class Name    : CaseTransferService_S_HUM
Version            : 1.0
Function           : This class is used to trigger a service call for Case Transfer.
Created Date       : 10 Jan 2019
Test Class         : CaseTransferService_T_HUM

    Modification Log
 *   Developer                   Code Review             Date               Description
 * -------------------------------------------------------------------------------------------------------------------------------                
 *   Ajay Chakradhar                                    10-01-2019          Original Version
 *   Ajay Chakradhar                                    26-03-2019          Defect-408427,408454 field mapping fixes
 *   Ajay Chakradhar                                    27-03-2019          Defect-408383 Claim Number
 *   Ajay Chakradhar                                    28-03-2019          Defect-408983 Owner Queue 
 *   Ajay Chakradhar                                    01-04-2019          Defect-409346 TemplateName
 *   Ajay Chakradhar                                    09-04-2019          REQ-389747 - Lock case with Case Status of External Transfer in Progress  
 *   Ajay Chakradhar                                    03-12-2019          REQ-586495,REQ-592179 Release-02/28/2020
 *   Ajay Chakradhar                                    17-12-2019          REQ-730552 Release-02/28/2020
 *   Ajay Chakradhar                                    06-01-2020          REQ-586495 Release-02/28/2020
 *   Ajay Chakradhar                                    14-01-2020          REQ-788342 Release-02/28/2020
 *   Ajay Chakradhar                                    22-01-2020          REQ-818259 Provider Details Release-02/28/2020
 *   Ajay Chakradhar                                    31-01-2020          DEF-455060 LoggedInformation Release-02/28/2020
 *   Ajay Chakradhar                                    10-02-2020          DEF-454931 Case History Order Release-02/28/2020
 *   Sumanth Naredla                                    04-05-2020          919796 - PR00093202 - CR-010 - Image Gap (Short Term Solution - New Case Creation)
 *   Asish Behera                                       04-07-2020          PR00093202-CR009-Testing-SF-- Add Additional Tanks to CCP to CRM Q/V Crosswalk (Provider Disputes)
 *   Asish Behera                                       04-13-2020          PR00093202-CR010-DCN and DCN URL should be sent to MHK Issue fix.
 *   Asish Behera                                       04-25-2020          PR00093202- UAT defect to poulate last modified queue and created Queue n MHK
 *   Ajay Chakradhar                                    04-28-2020          PR00091070-CA-9072928 FIX REQ-1037981 Release-06/05/2020
 *   Santhi Mandava                                     06-15-2020          User Story 1137169: PR00094268 - CaseTransferService_S_HUM performance concerns
 *   Asish Behera                                       06-17-2020          PR00093202-1197809 MF-4 sending Tax id and Case initiation date
 *   Asish Behera                                       06-23-2020          PR00093202-1197812 MF-4 sending Unit Number
 *   Sumanth Naredla                                    07-01-2020          1226831 - PR00093202 - TECH - Change Request# CR-012 - Image Gap (Long Term Solution - File To to CRM August Requirement)
 *   Asish Behera                                       07-02-2020          PR00093202-1197812 MF-4 Date format issue
 *   Asish Behera                                       07-06-2020          PR00093202-1197812 MF-4 Date format issue
 *   Santhi Mandava                                     07-31-2020          Policy plan details SOQL fix
 *   Mohammed Noor                                      09-18-2020          PR00088383 - MF6 - CRMP : Transfer of a CRM case created from an Availity submission to MHK
 *   Jasmeen Shangari                                   02-01-2021          Remove switch for REQ-1137169 & Filter CaseComment And Attachment for MHK against REQ - 1414643 & Fix for INC0596323  related to DependentCode issue
 *   Saikumar Boga                                      03/11/2021          US 1979757 CR20 - CRM Verbiage for CRM to PD Mhk Case transfer
 *   Ravikanth Samudrala                                05-18-2023          US-4531288 - T1PRJ0901110 MF1.11 - Additional Fields (blue marked) to the CRM PDF - CRM and MHK 
 *   Nirmal Garg                                        02/24/2024          US-5221803 - T1PRJ0865978 - C06 - Lightning - Case Mgt - INC2165142 - Grievance Appeals Provider Disputes Systems - Case transfers from CRM to MHK are creating using the incorrect Eligibility
**************************************************************************************************************************************************************************************************************************************************************************/

public with sharing class CaseTransferService_S_HUM
{
    /*
    *  This method Is used to invoke case service transfer from process builder
    *  @name   CaseTransferInvoke
    *  @param  List<Id> -  List of Case Ids
    *  @return void  
    */
    @InvocableMethod
    public static void CaseTransferInvoke(List<Id> Id) {
        String caseId = Id[GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_ZERO_HUM];            
        If (!String.isBlank(caseId))
        {
            CaseTransferProcess(caseId);
        }
    }
    /*
    *  This method is used to initiate the case transfer service call to external system.
    *  @name   CaseTransferProcess
    *  @param  string sRecordId -  Case id
    *  @return void
    */
    public String requestLabel;
    private static Boolean bSwitch_MF4_1197812;
    private static Boolean bSwitch_MF4_1197809;
    private static Boolean bSwitch_CR12_1226831;
    private static Boolean bSwitch_1348280;
    private static Boolean bSwitch_1348280_1;
    private static Boolean bSwitch_1414643;
    @TestVisible private static string sQueueName;
    @TestVisible private static String sBody ='' ;
    public static Boolean SwitchCRMToMHKAdditlnFields {get;set;}
    @future(callout = true)
    public static void CaseTransferProcess(string sRecordId)
    {
        HUM_Webservice_Callout__c oCTService = HUM_Webservice_Callout__c.getValues('CaseTransferService');
        Integer iTIMEOUT_INT_SECS = Integer.valueof(oCTService.Timeout__c)/1000;
        bSwitch_MF4_1197812 = HUMUtilityHelper.isCRMFunctionalityON('1197812');
        bSwitch_MF4_1197809 = HUMUtilityHelper.isCRMFunctionalityON('1197809');
        bSwitch_CR12_1226831 = HUMUtilityHelper.isCRMFunctionalityON('1226831');
        bSwitch_1348280 = HUMUtilityHelper.isCRMFunctionalityON('1348280');
        bSwitch_1348280_1 = HUMUtilityHelper.isCRMFunctionalityON('1348280_1');
        bSwitch_1414643 =  HUMUtilityHelper.isCRMFunctionalityON('1414643');
        SwitchCRMToMHKAdditlnFields = HUMUtilityHelper.isCRMFunctionalityON('4531288');
        try
        {  
             sBody = buildCaseTransferRequest(sRecordId);
            
            if(string.isNotBlank(sBody))
            {
                HttpRequest request = new HttpRequest();
                request.setMethod(oCTService.Request_Type__c); 
                request.setHeader(GLOBAL_CONSTANT_HUM.CONTENT_TYPE_HUM, oCTService.Content_Type__c);  
                request.setHeader(GLOBAL_CONSTANT_HUM.SOAPACTION_HUM, '');    
                request.setHeader(GLOBAL_CONSTANT_HUM.CONNECTION_HUM,GLOBAL_CONSTANT_HUM.KEEP_ALIVE_HUM);
                request.setBody(sBody);
                if(!Test.isRunningTest())    
                request.setClientCertificateName(oCTService.Certificate_Name__c);    
                request.setTimeout(Integer.valueof(oCTService.Timeout__c));
                request.setEndpoint(oCTService.End_Point_URL__c);
                Http http = new Http();
                HttpResponse response  = http.send(request);
                //Failure Response
                if(response.getBody().containsIgnoreCase(GLOBAL_CONSTANT_HUM.VOB_FAULT_CODE)) 
                {
                    JSONParser parser = JSON.createParser(response.getBody());
                    String errorMessage;
                    while(parser.nextToken() != null) {
                        if((parser.getCurrentToken() == JSONToken.FIELD_NAME) && 
                           (parser.getText() == 'error-message')) {
                               parser.nextToken();
                               errorMessage = parser.getText();
                           }
                    }
                    
                    if(!String.isBlank(errorMessage))
                    {   
                        //Adding a case comment
                        CaseComment oComment = new CaseComment();
                        oComment.ParentId = sRecordId;
                        if(errorMessage.containsIgnoreCase('Failed to establish a backside connection') || errorMessage.containsIgnoreCase('Internal Error'))
                        {    
                            oComment.CommentBody = System.Label.Case_Transfer_Communication_Failure;
                        }
                        else if(errorMessage.containsIgnoreCase('SourceID/SourceDesc/Channel/TransactionID/TransactionTime/TransactionType cannot be empty') ||
                           errorMessage.containsIgnoreCase('BIP3113E: Exception detected in message flow CaseTransferService_WebServiceAdapter.WebService_Adapter (integration node IBD02)') ||
                           errorMessage.containsIgnoreCase('Failure to process your request') ||
                           errorMessage.containsIgnoreCase('Value is expected') || errorMessage.containsIgnoreCase('Parsing Error'))
                        {    
                            oComment.CommentBody = System.Label.Case_Transfer_Data_Error;
                        }
                        else
                        {
                            oComment.CommentBody = 'Run Time Exception-Unknown Error';
                        }
                        Database.SaveResult saveCaseComment = Database.insert(oComment,false);
                    }
                }    
                //Success Response
                if (response != null && !response.getBody().containsIgnoreCase(GLOBAL_CONSTANT_HUM.VOB_FAULT_CODE) && 
                     (response.getStatusCode() == GLOBAL_CONSTANT_HUM.HTTP_OK_HUM || response.getStatusCode() == GLOBAL_CONSTANT_HUM.HTTP_ACCEPTED_HUM))
                {
                    //Adding a case comment
                    CaseComment oComment = new CaseComment();
                    oComment.ParentId = sRecordId;
                    oComment.CommentBody = sQueueName.equalsIgnoreCase('Business Correspondence CRM to Macess') ? System.Label.Macess_Case_Transferred_Comments:System.Label.Case_Transferred_Comments;
                    oComment.CommentBody = sQueueName.equalsIgnoreCase('Provider Correspondence to MHK') ? System.Label.Mhk_Case_Transferred_Comments:System.Label.Case_Transferred_Comments;
                        
                    Database.SaveResult saveCaseComment = Database.insert(oComment,false);
                    
            }        
        }
     } 
        catch(Exception ex)
        {   
            HUMExceptionHelper.logErrors(ex, 'CaseTransferService_S_HUM', 'CaseTransferProcess');
            
        }     
    }
    
    /*
    *  This method is used to create request header object
    *  @name   getHeaderDetails
    *  @param  Case data
    *  @return CaseTransferRequest_H_HUM.Header
    */
    private static CaseTransferRequest_H_HUM.Header getHeaderDetails(List<Case> lstCaseData)
    {
        CaseTransferRequest_H_HUM.Header oHeader = new CaseTransferRequest_H_HUM.Header(); 
        //source
        CaseTransferRequest_H_HUM.Source oSource = new CaseTransferRequest_H_HUM.Source();
        oSource.Channel = 'SALESFORCE';
        oSource.SourceDesc = 'Customer Relationship Management';
        oSource.SourceID = 'CRM';
        oHeader.Source = oSource;

        //DestinationList
        oHeader.DestinationList = getDestinationList();

        //TransactionDetails
        oHeader.TransactionDetails = getTransactionDetails(lstCaseData);
        return oHeader;
        
    }
   
    /*
    *  This method is used to create DestinationList request object
    *  @name   getDestinationList
    *  @param  NA
    *  @return List<CaseTransferRequest_H_HUM.DestinationList>
    */
    private static List<CaseTransferRequest_H_HUM.DestinationList> getDestinationList()
    {
        List<CaseTransferRequest_H_HUM.DestinationList> lstDestinationList = 
        new List<CaseTransferRequest_H_HUM.DestinationList>();
        
        CaseTransferRequest_H_HUM.DestinationList oDestinationList = new CaseTransferRequest_H_HUM.DestinationList();
        oDestinationList.TargetSystem = 'CRE';
        oDestinationList.TargetSystemQueue = '';
        lstDestinationList.add(oDestinationList);
        return lstDestinationList;
    }
   
    /*
    *  This method is used to create TransactionDetails request object
    *  @name   getTransactionDetails
    *  @param  Case data
    *  @return CaseTransferRequest_H_HUM.TransactionDetails
    */
    private static CaseTransferRequest_H_HUM.TransactionDetails getTransactionDetails(List<Case> lstCaseData)
   {
        //TransactionDetails
        String currentDate = '',target = ' ',replacement = 'T'; 
        CaseTransferRequest_H_HUM.TransactionDetails oTransactionDetails =
        new CaseTransferRequest_H_HUM.TransactionDetails();       
       
        if(lstCaseData != Null && !lstCaseData.isEmpty())
            {     
            oTransactionDetails.TransactionID = '';
            oTransactionDetails.SourceTransactionID = (lstCaseData[0].CaseNumber != Null) ? lstCaseData[0].CaseNumber : '';    
            currentDate = string.valueof(DateTime.now());
            String requestThruDate = currentDate.replace(target, replacement);
            oTransactionDetails.TransactionTime = requestThruDate;
            oTransactionDetails.SourceTransactionTime = requestThruDate;
            oTransactionDetails.TransactionType = 'CASE_TRANSFER_REQ';
        }
        return oTransactionDetails;
    }
    
    /*
    *  This method is used to create Correspondent request object
    *  @name   getCorrespondentList
    *  @param  Case data
    *  @return List<CaseTransferRequest_H_HUM.Correspondent>
    */
    private static List<CaseTransferRequest_H_HUM.Correspondent> getCorrespondentList(List<Case> lstCaseData)
    {
        //Corresponding
        List<CaseTransferRequest_H_HUM.Correspondent> lstCorrespondent = new List<CaseTransferRequest_H_HUM.Correspondent>();
                
        CaseTransferRequest_H_HUM.Correspondent oCorrespondent = new CaseTransferRequest_H_HUM.Correspondent();
        oCorrespondent.SubscriberID = '';
        oCorrespondent.SubmitterID = '';      
        oCorrespondent.SolicitationID = '';      
        oCorrespondent.FederalTaxID = '';      
        oCorrespondent.InquiryReason = '';      
        oCorrespondent.InquiryType = '';
        oCorrespondent.SubmitterType = (lstCaseData[0].Interacting_With_Type__c != Null) ? lstCaseData[0].Interacting_With_Type__c : ''; 

        //Person under Correspondent
        CaseTransferRequest_H_HUM.Person oCorpPerson = new CaseTransferRequest_H_HUM.Person();
        oCorpPerson.LastName = ''; 
        oCorpPerson.FirstName = ''; 
        oCorpPerson.MiddleName = '';
        oCorpPerson.SSN = '';
        oCorpPerson.DeceasedDate = '';
        oCorpPerson.DateOfMarriage = '';
        oCorpPerson.DateOfBirth = '';
        oCorpPerson.IsFullTimeStudent = '';
        oCorpPerson.IsSmoker = '';
        oCorpPerson.IsPermanentlyDisabled = '';
        oCorpPerson.NamePrefix = '';
        oCorpPerson.NameSuffix = '';
        oCorpPerson.Gender = '';
        oCorpPerson.MaritalStatus = '';
        oCorpPerson.Language = '';
        oCorpPerson.PersonId = '';
        if(lstCaseData[0].Interacting_With__r.LastName != null) oCorpPerson.LastName = lstCaseData[0].Interacting_With__r.LastName;   
        if(lstCaseData[0].Interacting_With__r.FirstName != null) oCorpPerson.FirstName = lstCaseData[0].Interacting_With__r.FirstName;    
        oCorrespondent.Person = oCorpPerson;
        lstCorrespondent.add(oCorrespondent);
        return lstCorrespondent;
    }
    
    /*
    *  This method is used to create inquiries request object
    *  @name   getRelatedInquiries
    *  @param  Case id, linked cases list
    *  @return List<CaseTransferRequest_H_HUM.RelatedInquiries>
    */
    private static List<CaseTransferRequest_H_HUM.RelatedInquiries> getRelatedInquiries(String sCaseId,List<Case_Junction__c> lstLinkedCases)
    {
        //RelatedInquiries
        List<CaseTransferRequest_H_HUM.RelatedInquiries> lstRelatedInquiries = new List<CaseTransferRequest_H_HUM.RelatedInquiries>();
        String linkedCaseNumber;
        if(lstLinkedCases != Null && !lstLinkedCases.isEmpty())
        {
            CaseTransferRequest_H_HUM.RelatedInquiries oRelatedInquiries;
            for(integer i = 0; i < lstLinkedCases.size(); i++)
            {
                if(lstLinkedCases[i].Case_Master__c == sCaseId)
                {
                    linkedCaseNumber= lstLinkedCases[i].Case__r.Casenumber;   
                }
                else
                {
                    linkedCaseNumber= lstLinkedCases[i].Case_Master__r.Casenumber;    
                }
                oRelatedInquiries = new CaseTransferRequest_H_HUM.RelatedInquiries();
                oRelatedInquiries.InquiryPlatform = '';
                oRelatedInquiries.InitiatingSys = '';    
                oRelatedInquiries.InquiryID = (linkedCaseNumber != Null) ? linkedCaseNumber : ''; 
                lstRelatedInquiries.add(oRelatedInquiries);    
            }
        }
        return lstRelatedInquiries;    
    }
    
    /*
    *  This method is used to Document request object
    *  @name   getDocumentsList
    *  @param  attachments list,case data,dcn details
    *  @return List<CaseTransferRequest_H_HUM.Document>
    */
    private static List<CaseTransferRequest_H_HUM.Document> getDocumentsList(List<Attachment__c> lstAttachment,List<Case> lstCase,map<string,string> mapDCN_URLData)
    {
        String currentDate = '',target = ' ',replacement = 'T'; 
        List<CaseTransferRequest_H_HUM.Document> lstDocument = new List<CaseTransferRequest_H_HUM.Document>();
        if(!lstAttachment.isEmpty())
        {
            for(Integer i=0;i<lstAttachment.size();i++)
            {
                CaseTransferRequest_H_HUM.Document oDocument = new CaseTransferRequest_H_HUM.Document();
                oDocument.DocumentType = '';    
                oDocument.CaseType = '';
                oDocument.CaseSubType = '';
                oDocument.SourceDocumentID = (lstAttachment[i].DocId__c != Null) ? lstAttachment[i].DocId__c : '';    
                if(HUMUtilityHelper.isCRMFunctionalityON(GLOBAL_CONSTANT_HUM.sUS1334714) && String.isNotBlank(lstAttachment[i].Document_Source__c) && lstAttachment[i].Document_Source__c.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.sDocSourceMRM))
                {
                    oDocument.DocumentSource = lstAttachment[i].Document_Source__c;
                    oDocument.TrackingID = String.isNotBlank(lstAttachment[i].AlternateDocId__c) ? lstAttachment[i].AlternateDocId__c : '';
                }
                else
                {
                    oDocument.DocumentSource = 'Ehub';
                    oDocument.TrackingID = '';
                }

                currentDate = string.valueof(lstAttachment[i].CreatedDate);
                String receivedDateTime = currentDate.replace(target, replacement);
                oDocument.ReceivedDateTime = receivedDateTime;
                oDocument.MethodOfReceipt = '';
                oDocument.FaxNumber = '';
                oDocument.DocumentVersion = (lstAttachment[i].Version_Id__c != Null) ? lstAttachment[i].Version_Id__c : '';    

                CaseTransferRequest_H_HUM.FileList oFileList = new CaseTransferRequest_H_HUM.FileList();
                oFileList.FileName = (lstAttachment[i].Name != Null) ? lstAttachment[i].Name : '';    

                oDocument.FileList = oFileList;
                lstDocument.add(oDocument);
            }
        }
        // DCN exists
        
        set<string> attchmentLogKeys = mapDCN_URLData.keyset();
        if(lstCase[0].DCN__c != null && mapDCN_URLData !=null && !attchmentLogKeys.isempty() && !attchmentLogKeys.contains('DCN') && !attchmentLogKeys.contains('DCN URL'))
        {
            CaseTransferRequest_H_HUM.Document oDocument = new CaseTransferRequest_H_HUM.Document();
            oDocument.DocumentType = (lstCase[0].Doc_Type__c != Null) ? lstCase[0].Doc_Type__c : '';
            if(bSwitch_MF4_1197812)
            oDocument.UnitNumber = mapDCN_URLData.containskey(HUMConstants.HUMMacesUnitNumber) ? mapDCN_URLData.get(HUMConstants.HUMMacesUnitNumber) : '';
            oDocument.CaseType = '';
            oDocument.CaseSubType = '';                       
            oDocument.DocumentControlNumber = lstCase[0].DCN__c;
            if(bSwitch_CR12_1226831)
            oDocument.DocumentSourceCode = (lstCase[0].DCN_Type__c != Null) ? lstCase[0].DCN_Type__c : ''; 
            oDocument.DocumentSource = 'Ehub';  
            oDocument.SourceDocumentID = ''; 
            oDocument.TrackingID = '';
            oDocument.ReceivedDateTime = '';
            oDocument.MethodOfReceipt = '';
            oDocument.FaxNumber = '';
            oDocument.DocumentVersion = '';

            CaseTransferRequest_H_HUM.FileList oFileList = new CaseTransferRequest_H_HUM.FileList();
            oFileList.FileName = '';

            oDocument.FileList = oFileList;
            lstDocument.add(oDocument);
        }
        // No Documents
        if(lstAttachment.isEmpty() && (lstCase[0].DCN__c == null || (lstCase[0].DCN__c != null && string.IsNotBlank(mapDCN_URLData.get(HUMConstants.URL_value)))))
        {
            CaseTransferRequest_H_HUM.Document oDocument = new CaseTransferRequest_H_HUM.Document();
            oDocument.DocumentType = '';
            oDocument.CaseType = '';
            oDocument.CaseSubType = '';
            oDocument.SourceDocumentID = '';
            oDocument.DocumentControlNumber = '';                       
            oDocument.DocumentSource = '';
            oDocument.TrackingID = '';
            oDocument.ReceivedDateTime = '';
            oDocument.MethodOfReceipt = '';
            oDocument.FaxNumber = '';
            oDocument.DocumentVersion = '';

            CaseTransferRequest_H_HUM.FileList oFileList = new CaseTransferRequest_H_HUM.FileList();
            oFileList.FileName = '';

            oDocument.FileList = oFileList;
            lstDocument.add(oDocument);
        }
        return lstDocument;
    }
    
    /*
    *  This method is used to claim request object
    *  @name   getClaimDetails
    *  @param  attachment log reocrds
    *  @return List<CaseTransferRequest_H_HUM.Claim>
    */
    private static List<CaseTransferRequest_H_HUM.Claim> getClaimDetails(List<Attachment_Log__c> lstAttachmentLogs)
    {
        List<CaseTransferRequest_H_HUM.Claim> lstClaim = new List<CaseTransferRequest_H_HUM.Claim>();
        CaseTransferRequest_H_HUM.Claim oClaim = new CaseTransferRequest_H_HUM.Claim();
        oClaim.ClaimNumber = '';
        if(lstAttachmentLogs != Null && !lstAttachmentLogs.isEmpty())
        {  
            if(lstAttachmentLogs[0].User_Selected_Value__c != null || lstAttachmentLogs[0].Standard_Value__c != null)
            {
                String claimStr = (lstAttachmentLogs[0].Standard_Value__c != null) ? lstAttachmentLogs[0].Standard_Value__c : lstAttachmentLogs[0].User_Selected_Value__c;
                Integer valueStart = claimStr.indexOf('Claim/Pre-D');
                if(valueStart > 1)
                {
                    string subClaim = claimStr.mid(valueStart,valueStart+40);
                    Integer valueStarted = subClaim.indexOf('~');
                    if(valueStarted > 1)
                    {
                        Integer valueEnd = subClaim.indexOf('^Claims');
                        if(valueEnd > 1)
                        {
                            oClaim.ClaimNumber = subClaim.substring(valueStarted+1,valueEnd);
                        }
                        else if(valueEnd == -1)
                        {
                            oClaim.ClaimNumber = subClaim.substring(valueStarted+1,subClaim.length());
                        }
                    }       
                }
            }
        }
        oClaim.IsPaid = '';                      
        oClaim.ClaimStatus = '';                 
        oClaim.TimelinessDays = '';
        oClaim.TypeOfService = '';
        oClaim.DateOfService = '';
        oClaim.AuthorizationInformation = '';    
        oClaim.PARIndicator = '';            
        oClaim.CaseType = '';        
        lstClaim.add(oClaim);
        return lstClaim;
    }
    
    /*
    *  This method is used to provider request object
    *  @name   getProviderDetails
    *  @param  case details
    *  @return CaseTransferRequest_H_HUM.Provider
    */
    private static CaseTransferRequest_H_HUM.Provider getProviderDetails(List<Case> lstCase)
    {
        CaseTransferRequest_H_HUM.Provider oProvider = new CaseTransferRequest_H_HUM.Provider();
        if(bSwitch_MF4_1197809 && lstCase[0].Interacting_With_Type__c == GLOBAL_CONSTANT_HUM.AUTHDETAIL_PROVIDER_HUM){
            oProvider.ProviderID = HUMUtilityHelper.getTaxId(lstCase[0].Interacting_With__c);
        }else{
            oProvider.ProviderID = '';  
        }              
        //REQ-818259
        oProvider.NationalProviderId = (lstCase[0].Interacting_With_Type__c == 'Provider' && lstCase[0].Interacting_With__r.NPI_ID__c != Null) ? lstCase[0].Interacting_With__r.NPI_ID__c : '';     
        oProvider.ProviderFullName = (lstCase[0].Interacting_With_Type__c == 'Provider' && lstCase[0].Interacting_With__r.Name != Null) ? lstCase[0].Interacting_With__r.Name : '';         
        oProvider.FirstName = (lstCase[0].Interacting_With_Type__c == 'Provider' && lstCase[0].Interacting_With__r.Individual_First_Name__c != Null) ? lstCase[0].Interacting_With__r.Individual_First_Name__c : '';                        
        oProvider.LastName = (lstCase[0].Interacting_With_Type__c == 'Provider' && lstCase[0].Interacting_With__r.Individual_Last_Name__c != Null) ? lstCase[0].Interacting_With__r.Individual_Last_Name__c : '';                       
        //REQ-818259 
        oProvider.ProviderType = '';        
        oProvider.ContactFirstName = '';                
        oProvider.ContactLastName = '';

        List<CaseTransferRequest_H_HUM.PostalAddressList> lstPostalAddressList = new List<CaseTransferRequest_H_HUM.PostalAddressList>();
        CaseTransferRequest_H_HUM.PostalAddressList oPostalAddressList = new 
            CaseTransferRequest_H_HUM.PostalAddressList();
        oPostalAddressList.AddressLine1 = '';     
        oPostalAddressList.AddressLine2 = '';     
        oPostalAddressList.AddressLine3 = '';     
        oPostalAddressList.AddressLine4 = '';     
        oPostalAddressList.City = '';                        
        oPostalAddressList.StateCode = '';            
        oPostalAddressList.ZipCode = '';                
        oPostalAddressList.Country = '';                  
        oPostalAddressList.CountyCode = '';          
        oPostalAddressList.CountyName = '';          
        oPostalAddressList.EffectiveDate = '';
        oPostalAddressList.EndDate = '';                  
        oPostalAddressList.AddressType = '';        
        oPostalAddressList.AddressRank = '';        
        oPostalAddressList.IsAddressSameAsSubscriber = '';

        lstPostalAddressList.add(oPostalAddressList);
        //PostalAddressList - provider
        oProvider.PostalAddressList = lstPostalAddressList ;

        List<CaseTransferRequest_H_HUM.PhoneNumberList> lstPhoneNumberList = new List<CaseTransferRequest_H_HUM.PhoneNumberList>();
        CaseTransferRequest_H_HUM.PhoneNumberList oPhoneNumberList = new
            CaseTransferRequest_H_HUM.PhoneNumberList();
        oPhoneNumberList.PhoneNumber = '';               
        oPhoneNumberList.Extension = '';                 
        oPhoneNumberList.PagerPinIdentifier = '';            
        oPhoneNumberList.CountryCallCode = '';           
        oPhoneNumberList.EffectiveDate = '';             
        oPhoneNumberList.EndDate = '';                   
        oPhoneNumberList.PhoneNumberType = '';           
        oPhoneNumberList.PhoneNumberRank = '';           
        oPhoneNumberList.IsPhoneSameAsSubscriber = '';       
        oPhoneNumberList.CountryName = '';
        
        lstPhoneNumberList.add(oPhoneNumberList);
        //PhoneNumberList - provider
        oProvider.PhoneNumberList = lstPhoneNumberList ;
        return oProvider;
    }
    
    /*
    *  This method is used to member request object
    *  @name   getMemberDetails
    *  @param  case details,member id record, policy plans
    *  @return List<CaseTransferRequest_H_HUM.Member>
    */
    private static List<CaseTransferRequest_H_HUM.Member> getMemberDetails(List<Case> lstCase,List<Member_ID__c> lstMemberId,List<Policy__c> lstPolicyPlans)
    {
        List<CaseTransferRequest_H_HUM.Member> lstMember = new List<CaseTransferRequest_H_HUM.Member>();
        CaseTransferRequest_H_HUM.Member oMember = new CaseTransferRequest_H_HUM.Member();
        
        //Person - Member
        CaseTransferRequest_H_HUM.Person oPerson = new CaseTransferRequest_H_HUM.Person();
        if(lstCase[0].Account != Null)
        {
            oPerson.FirstName = (lstCase[0].Account.FirstName != Null) ? lstCase[0].Account.FirstName : '';    
            oPerson.LastName = (lstCase[0].Account.LastName != Null) ? lstCase[0].Account.LastName : '';    
            oPerson.MiddleName = (lstCase[0].Account.MiddleName != Null) ? lstCase[0].Account.MiddleName : '';    
            oPerson.PersonId = (lstCase[0].Account.Enterprise_ID__c != Null) ? lstCase[0].Account.Enterprise_ID__c : '';
            oPerson.SSN = '';                        
            oPerson.DeceasedDate = '';              
            oPerson.DateOfMarriage = '';            
            oPerson.DateOfBirth = '';               
            oPerson.IsFullTimeStudent = '';         
            oPerson.IsSmoker = '';                   
            oPerson.IsPermanentlyDisabled = '';     
            oPerson.NamePrefix = '';                 
            oPerson.NameSuffix = '';                 
            oPerson.Gender = '';                     
            oPerson.MaritalStatus = '';             
            oPerson.Language = '';                   
        }   
        oMember.Person = oPerson;
        //Plan - Member
        CaseTransferRequest_H_HUM.Plan oPlan = new CaseTransferRequest_H_HUM.Plan();
        if(String.IsNotBlank(lstCase[0].Policy_Member__c))
        {
            if(bSwitch_1414643)
            {
                if(lstMemberId!=null && !lstMemberId.isEmpty()){
                    oPlan.MemberID = lstMemberId[0].Name;           
                    oPlan.DependentCode = (String.IsNotBlank(lstMemberId[0].member_dependent_code__c)) ? lstMemberId[0].member_dependent_code__c : '';        
                }else{
                    oPlan.MemberID = '';    
                    oPlan.DependentCode = '';
                }
            }
            else
            {
                //REQ-730552 
                if(!lstMemberId.isEmpty() && lstMemberId.size()!=0){
                    oPlan.MemberID = lstMemberId[0].Name;
                }else{
                    oPlan.MemberID = '';    
                }
                
                oPlan.DependentCode = (lstCase[0].Policy_Member__r.Relationship_Code__c != Null) ? lstCase[0].Policy_Member__r.Relationship_Code__c : '';        
            }
            
            //REQ-730552 
            oPlan.PlanStartDate = (lstCase[0].Policy_Member__r.Effective_Date__c != Null) ? CaseTransferService_S_HUM.dateFormatting(lstCase[0].Policy_Member__r.Effective_Date__c) : '';
            oPlan.PlanEndDate = (lstCase[0].Policy_Member__r.End_Date__c != Null) ? CaseTransferService_S_HUM.dateFormatting(lstCase[0].Policy_Member__r.End_Date__c) : '';
            oPlan.GroupNumber = (lstCase[0].Policy_Member__r.Group_Number__c != Null) ? lstCase[0].Policy_Member__r.Group_Number__c : '';    
              oPlan.DivisionId = '';                  
            oPlan.IssueState = (lstCase[0].Policy_Member__r.Issue_State__c != Null) ? lstCase[0].Policy_Member__r.Issue_State__c : '';    
            oPlan.MemberGenKey = '';
            if(!lstPolicyPlans.isEmpty()){
                oPlan.SourcePlatform = (lstPolicyPlans[0].Platform__c != Null) ? lstPolicyPlans[0].Platform__c : '';
                oPlan.LineOfBusiness =  String.isNotBlank(lstPolicyPlans[0].Platform__c) && lstPolicyPlans[0].Platform__c.equalsIgnoreCase('LV') ? String.isNotBlank(lstPolicyPlans[0].ORIGINAL_MAJOR_LOB__C) ? lstPolicyPlans[0].ORIGINAL_MAJOR_LOB__C : String.isNotBlank(lstPolicyPlans[0].MAJOR_LOB_FRM__C) ? lstPolicyPlans[0].MAJOR_LOB_FRM__C : '': (String.isNotBlank(lstPolicyPlans[0].Platform__c) && lstPolicyPlans[0].Platform__c.equalsIgnoreCase('EM') ? String.isNotBlank(lstPolicyPlans[0].Source_Major_LOB__c) ? lstPolicyPlans[0].Source_Major_LOB__c : '' : '') ;
            }
            if(lstPolicyPlans.isEmpty()){
                oPlan.SourcePlatform = '';    
                oPlan.LineOfBusiness = '';    
            }
        }
        oMember.Plan = oPlan ;
        //PlatformPointerList - Member
        List<CaseTransferRequest_H_HUM.PlatformPointerList> lstPlatformPointerList = 
            new List<CaseTransferRequest_H_HUM.PlatformPointerList>();
        CaseTransferRequest_H_HUM.PlatformPointerList oPlatformPointerList = new 
            CaseTransferRequest_H_HUM.PlatformPointerList();
                oPlatformPointerList.Application = '';
                oPlatformPointerList.KeyType = '';
                oPlatformPointerList.KeyValue = '';
                oPlatformPointerList.Source = '';
        lstPlatformPointerList.add(oPlatformPointerList);
        
        oMember.PlatformPointerList = lstPlatformPointerList ;
        lstMember.add(oMember);
        return lstMember;
    }
    
     /*
    *  This method is used to CaseDetails request object
    *  @name   getCaseDetails
    *  @param  case details,case history
    *  @return CaseTransferRequest_H_HUM.CaseDetails
    */
    private static CaseTransferRequest_H_HUM.CaseDetails getCaseDetails(List<Case> lstCase,List<CaseHistory> lstRoutingQueue,List<CaseHistory> lstCaseSubQueue)
    {
        String currentDate = '',target = ' ',replacement = 'T'; 
        CaseTransferRequest_H_HUM.CaseDetails oCaseDetails = new CaseTransferRequest_H_HUM.CaseDetails();
        oCaseDetails.SourceChannel = (lstCase[0].Origin != Null) ? lstCase[0].Origin : '';
        oCaseDetails.CaseNumber = (lstCase[0].CaseNumber != Null) ? lstCase[0].CaseNumber : '';
        //CA-9072928 fix    
        oCaseDetails.RoutingQueue = (lstCase[0].Owner_Queue__c  != Null) ? lstCase[0].Owner_Queue__c  : ''; 
        oCaseDetails.RoutingView = (lstCase[0].Work_Queue_View_Name__c != Null) ? lstCase[0].Work_Queue_View_Name__c : '';
        if(!lstRoutingQueue.isEmpty())
        {
            oCaseDetails.CaseOriginatingQueue = (lstRoutingQueue[0].OldValue != Null) ? string.valueof(lstRoutingQueue[0].OldValue) : '';    
        }
        else
        {
            oCaseDetails.CaseOriginatingQueue = lstCase[0].Owner_Queue__c;
        }
        if(!lstCaseSubQueue.isEmpty())
        {
            oCaseDetails.CaseOriginatingSubQueue = '';  
            if(lstCaseSubQueue[0].OldValue != Null){
                oCaseDetails.CaseOriginatingSubQueue = string.valueof(lstCaseSubQueue[0].OldValue);
            }else if (lstCaseSubQueue[0].OldValue == Null) {
                oCaseDetails.CaseOriginatingSubQueue = string.valueof(lstCaseSubQueue[0].NewValue);
            }
        }
        else
        {   
            oCaseDetails.CaseOriginatingSubQueue = lstCase[0].Work_Queue_View_Name__c;
        }
        oCaseDetails.CaseType = (lstCase[0].Type != Null) ? lstCase[0].Type : '';    
        oCaseDetails.CaseSubType = (lstCase[0].Subtype__c != Null) ? lstCase[0].Subtype__c : '';    
        oCaseDetails.Status = (lstCase[0].Status != Null) ? lstCase[0].Status : '';    
        oCaseDetails.CaseAge = (lstCase[0].Case_Age__c != Null) ? string.valueof(lstCase[0].Case_Age__c) : '';    
        currentDate = string.valueof(lstCase[0].CreatedDate);
        String caseOpenDate = currentDate.replace(target, replacement);
        oCaseDetails.CaseOpenDate = caseOpenDate;                        
        oCaseDetails.ReOpenCaseAge = (lstCase[0].Re_Open_Case_Age__c != Null) ? string.valueof(lstCase[0].Re_Open_Case_Age__c) : '';    
                
        if(lstCase[0].RecordType.Name != Null)
        {
            Map<ID, Schema.RecordTypeInfo> rtMap = Schema.SObjectType.Case.getRecordTypeInfosById();
            String currentRecordType = rtMap.get(lstCase[0].RecordTypeid).getName();
            String updatedRecordType = currentRecordType.removeStart('Closed ');
            oCaseDetails.CaseRecordType = (updatedRecordType != '') ? updatedRecordType : '';    
        }else{
            oCaseDetails.CaseRecordType = '';    
        }
        
        oCaseDetails.Classification = (lstCase[0].Classification_Id__c != Null) ? oCaseDetails.Classification = lstCase[0].Classification_Id__r.Name : '';
        oCaseDetails.ClassificationType = (lstCase[0].Classification_Type__c != Null) ? lstCase[0].Classification_Type__c : '';    
        oCaseDetails.Intent = (lstCase[0].Intent_Id__c != Null) ? oCaseDetails.Intent = lstCase[0].Intent_Id__r.Name : '';
        oCaseDetails.Topic = (lstCase[0].Topic__c != Null) ? lstCase[0].Topic__c : '';    
        oCaseDetails.Priority = (lstCase[0].Priority != Null) ? lstCase[0].Priority : '';  
        oCaseDetails.InteractingWith = (lstCase[0].Interacting_With__r.Name != Null) ? lstCase[0].Interacting_With__r.Name : '';
        oCaseDetails.InquiringAboutType = (lstCase[0].Interacting_About_Type__c != Null) ? lstCase[0].Interacting_About_Type__c : ''; 
        oCaseDetails.AccountName = (lstCase[0].Account.Name != Null) ? lstCase[0].Account.Name : '';
        if(bSwitch_MF4_1197809 && lstCase[0].Case_Initiated_Date__c !=null)
          oCaseDetails.ReceivedDateTime = String.valueof(lstCase[0].Case_Initiated_Date__c)+'T00:00:00';   
        return oCaseDetails; 
    }
    
     /*
    *  This method is used to CaseAdditionalDetails request object
    *  @name   getCaseAdditionalDetails
    *  @param  DCN data,case action response records,case data, case history,template submission data
    *  @return List<CaseTransferRequest_H_HUM.CaseAdditionalDetails>
    */
    private static List<CaseTransferRequest_H_HUM.CaseAdditionalDetails> getCaseAdditionalDetails(map<string,string> mapDCN_URLData,List<Case_Action_Response__c> lstCaseActionResponseData,
                                                                                                  List<case> lstCase,List<CaseHistory> lstCaseHistory,List<CaseHistory> lstCaseSubQueue,
                                                                                                  List<Template_Submission_Data__c> lstTemplateSubData,List<Case_Action_Response__c> lstCaseActionResponseDataList)
    {
        List<CaseTransferRequest_H_HUM.CaseAdditionalDetails> lstCaseAdditionalDetails = new List<CaseTransferRequest_H_HUM.CaseAdditionalDetails>();                
        CaseTransferRequest_H_HUM.CaseAdditionalDetails caseAddDetails;
        if(lstTemplateSubData != null && !lstTemplateSubData.IsEmpty())
        {
            for(Integer i=0;i<lstTemplateSubData.size();i++) 
            {
                caseAddDetails = new CaseTransferRequest_H_HUM.CaseAdditionalDetails();
                caseAddDetails.Name = lstTemplateSubData[i].Name__c;
                caseAddDetails.Value = lstTemplateSubData[i].Value__c;
                caseAddDetails.Type = lstTemplateSubData[i].Template_Submission__r.Template__r.Description__c;
                lstCaseAdditionalDetails.add(caseAddDetails);
            }
        }
        if(lstCaseActionResponseDataList != Null && !lstCaseActionResponseDataList.isEmpty())
        {
             String sTemplateName = lstCaseActionResponseDataList[0].Action__r.Case_Action__r.Name +' Template';
            for(Integer i=0;i<lstCaseActionResponseDataList.size();i++)
            {
                caseAddDetails = new CaseTransferRequest_H_HUM.CaseAdditionalDetails();
                caseAddDetails.Name = lstCaseActionResponseDataList[i].Process_Question__c;
                caseAddDetails.Value = lstCaseActionResponseDataList[i].Process_Question_Response__c;
                caseAddDetails.Type = sTemplateName ;
                lstCaseAdditionalDetails.add(caseAddDetails);
            } 
            caseAddDetails = new CaseTransferRequest_H_HUM.CaseAdditionalDetails();
            caseAddDetails.Name = 'Process Type|Created Date|Created By|Last Modified Date|Last Modified By';
            caseAddDetails.Value = sTemplateName +'|'+ lstCaseActionResponseDataList[0].CreatedDate.format() +'|'+ string.valueof(lstCaseActionResponseDataList[0].CreatedBy.Name) +'|'+ lstCaseActionResponseDataList[0].LastModifiedDate.format() +'|'+ string.valueof(lstCaseActionResponseDataList[0].LastModifiedBy.Name);
            caseAddDetails.Type = 'Process';
            lstCaseAdditionalDetails.add(caseAddDetails);   
        }
        //REQ-586495 
        //system information
        if(SwitchCRMToMHKAdditlnFields)
        {
            String caseAddtnlInfo = 'Case';
            if(lstCase[0].Interacting_With_Name__c != null)
            {
                caseAddDetails = new CaseTransferRequest_H_HUM.CaseAdditionalDetails();
                caseAddDetails.Name = 'Interacting_With_Name__c';
                caseAddDetails.Value = lstCase[0].Interacting_With_Name__c ;
                caseAddDetails.Type = caseAddtnlInfo;
                lstCaseAdditionalDetails.add(caseAddDetails);
            }
            if(lstCase[0].Call_Benefit_Category__c != null)
            {
                caseAddDetails = new CaseTransferRequest_H_HUM.CaseAdditionalDetails();
                caseAddDetails.Name = 'Call_Benefit_Category__c';
                caseAddDetails.Value = lstCase[0].Call_Benefit_Category__c;
                caseAddDetails.Type = caseAddtnlInfo;
                lstCaseAdditionalDetails.add(caseAddDetails);
            }
            
            //caseInteractions Information
            List<CaseInteraction_DTO_HUM>  caseInteractionDTOHUMDTO = new list<CaseInteraction_DTO_HUM>();
             caseInteractionDTOHUMDTO = CaseInteraction_C_HUM.getInteractionDTOlist(lstCase[0].Id);
            for(CaseInteraction_DTO_HUM caseInteractionVar : caseInteractionDTOHUMDTO)
            {
                caseAddDetails = new CaseTransferRequest_H_HUM.CaseAdditionalDetails();
                caseAddDetails.Name = 'Interaction Number|Interaction Origin|Interacting With|Interacting With Type|Created Date|Created By|Created By Queue|Associated to Case Date|GenesysInteractionID|CallRecordingLink';
                caseAddDetails.Value = caseInteractionVar.InteractionNumber.split(',')[0] +'|'+caseInteractionVar.InteractionOrigin +'|'+caseInteractionVar.InteractionWith +'|'+caseInteractionVar.InteractionWithType +'|'+caseInteractionVar.CreatedDate +'|'+caseInteractionVar.CreatedBy.split(',')[0] +'|'+caseInteractionVar.CreatedByQueue +'|'+caseInteractionVar.AssociatedtoCaseDate+'|'+caseInteractionVar.GenesysInteractionID+'|'+caseInteractionVar.CallRecordingLink;
                caseAddDetails.Type = 'Case Interaction';
                lstCaseAdditionalDetails.add(caseAddDetails);    
            }
            
              //LivechatTranscript Information
            List<LiveChatTranscript_DTO_HUM>  liveChatTranscriptDTOHUMList = new list<LiveChatTranscript_DTO_HUM>();
             liveChatTranscriptDTOHUMList = LiveChatTranscript_C_HUM.getLiveChatDTOList(lstCase[0].Id);
            for(LiveChatTranscript_DTO_HUM liveChatTranscriptDTOVar : liveChatTranscriptDTOHUMList)
            {
                caseAddDetails = new CaseTransferRequest_H_HUM.CaseAdditionalDetails();
                caseAddDetails.Name = 'Name|StartTime|EndTime|Owner|Status|HumanaChatTranscript|URL';
                caseAddDetails.Value = liveChatTranscriptDTOVar.Name.split(',')[0] +'|'+liveChatTranscriptDTOVar.StartTime +'|'+liveChatTranscriptDTOVar.EndTime +'|'+liveChatTranscriptDTOVar.Owner.split(',')[0] +'|'+liveChatTranscriptDTOVar.Status+'|'+liveChatTranscriptDTOVar.HumanaChatTranscript+'|'+liveChatTranscriptDTOVar.URL;
                caseAddDetails.Type = 'LiveChatTranscript';
                lstCaseAdditionalDetails.add(caseAddDetails);    
            }
            
              //LivechatTranscriptEvent Information
            List<LiveChatTranscriptEvent_DTO_HUM>  liveChatTranscriptEventDTOHUMList = new list<LiveChatTranscriptEvent_DTO_HUM>();
             liveChatTranscriptEventDTOHUMList = LiveChatTranscript_C_HUM.getLiveChatEventDTOList(lstCase[0].Id);
            for(LiveChatTranscriptEvent_DTO_HUM liveChatTranscriptEventDTOVar : liveChatTranscriptEventDTOHUMList)
            {
                caseAddDetails = new CaseTransferRequest_H_HUM.CaseAdditionalDetails();
                caseAddDetails.Name = 'Time|Agent|Type|Detail|LiveChatTranscriptName|';
                caseAddDetails.Value = liveChatTranscriptEventDTOVar.StartTime +'|'+liveChatTranscriptEventDTOVar.Agent +'|'+liveChatTranscriptEventDTOVar.Type +'|'+liveChatTranscriptEventDTOVar.Detail +'|'+liveChatTranscriptEventDTOVar.HumanaChatTranscriptName;
                caseAddDetails.Type = 'LiveChatTranscriptEvent';
                lstCaseAdditionalDetails.add(caseAddDetails);
            }
            
            //logged info
             List<CaseLoggedInfo_DTO_HUM>  attachmenLogHUMList = new list<CaseLoggedInfo_DTO_HUM>();
             attachmenLogHUMList = CaseLoggedInfoDataTable_C_HUM.getLogDetails(lstCase[0].Id);
            for(CaseLoggedInfo_DTO_HUM attachmenLogHUMVar : attachmenLogHUMList)
            {
                caseAddDetails = new CaseTransferRequest_H_HUM.CaseAdditionalDetails();
                caseAddDetails.Name = 'Type|Created Date|Created By|Created By Queue';
                caseAddDetails.Value = attachmenLogHUMVar.subType +'|'+attachmenLogHUMVar.createdDate +'|'+attachmenLogHUMVar.createdBy.split(',')[0] +'|'+attachmenLogHUMVar.createdByQueue;
                caseAddDetails.Type = 'Logged Information';
                lstCaseAdditionalDetails.add(caseAddDetails);    
            }
              //Linked cases  
             List<LinkedCases_Wrapper_DTO_HUM>  linkedCasesHUMList = new list<LinkedCases_Wrapper_DTO_HUM>();
             linkedCasesHUMList = LinkedCasesExtension_C_HUM.getLinkedCases(lstCase[0].Id);
            for(LinkedCases_Wrapper_DTO_HUM linkedCasesHUMVar : linkedCasesHUMList)
            {
                caseAddDetails = new CaseTransferRequest_H_HUM.CaseAdditionalDetails();
                caseAddDetails.Name = 'Case Number|Classification|Intent|Interacting About|Status|Date/Time Opened|Date/Time Closed|Created By|Created By Queue|Linked Date|Linked By|Linked By Queue';
                caseAddDetails.Value = linkedCasesHUMVar.sCaseCaseNumber.split(',')[0] +'|'+linkedCasesHUMVar.sCaseClassification +'|'+linkedCasesHUMVar.sCaseIntent +'|'+linkedCasesHUMVar.sCaseInteractingAbout+'|'+linkedCasesHUMVar.sCaseStatus +'|'+linkedCasesHUMVar.dCaseCreatedDate +'|'+linkedCasesHUMVar.dCaseClosedDate +'|'+linkedCasesHUMVar.sCreatedByName.split(',')[0]+'|'+linkedCasesHUMVar.sCaseCreatedByQueue+'|'+linkedCasesHUMVar.slinkedDate+'|'+linkedCasesHUMVar.slinkedBy.split(',')[0]+'|'+linkedCasesHUMVar.sWorkQueueName;
                caseAddDetails.Type = 'Linked Cases';
                lstCaseAdditionalDetails.add(caseAddDetails);    
            }
        }
        
        String sysInfo = 'System Information';
        if(lstCase[0].CreatedDate != null)
        {
            caseAddDetails = new CaseTransferRequest_H_HUM.CaseAdditionalDetails();
            caseAddDetails.Name = 'Date/Time Opened';
            caseAddDetails.Value = lstCase[0].CreatedDate.format();
            caseAddDetails.Type = sysInfo;
            lstCaseAdditionalDetails.add(caseAddDetails);
        }
        if(lstCase[0].ClosedDate != null)
        {
            caseAddDetails = new CaseTransferRequest_H_HUM.CaseAdditionalDetails();
            caseAddDetails.Name = 'Date/Time Closed';
            caseAddDetails.Value = lstCase[0].ClosedDate.format();
            caseAddDetails.Type = sysInfo;
            lstCaseAdditionalDetails.add(caseAddDetails);
        }
        if(lstCase[0].Re_Open_Case_Date__c != null){
            caseAddDetails = new CaseTransferRequest_H_HUM.CaseAdditionalDetails();
            caseAddDetails.Name = 'Re-Open Case Date';
            caseAddDetails.Value = string.valueof(lstCase[0].Re_Open_Case_Date__c);
            caseAddDetails.Type = sysInfo;
            lstCaseAdditionalDetails.add(caseAddDetails);
        }
        if(lstCase[0].CreatedBy.Name != null){
            caseAddDetails = new CaseTransferRequest_H_HUM.CaseAdditionalDetails();
            caseAddDetails.Name = 'Created By';
            caseAddDetails.Value = string.valueof(lstCase[0].CreatedBy.Name);
            caseAddDetails.Type = sysInfo;
        lstCaseAdditionalDetails.add(caseAddDetails);
        }
        if(lstCase[0].LastModifiedBy.Name != null){
        caseAddDetails = new CaseTransferRequest_H_HUM.CaseAdditionalDetails();
            caseAddDetails.Name = 'Last Modified By';
            caseAddDetails.Value = string.valueof(lstCase[0].LastModifiedBy.Name);
            caseAddDetails.Type = sysInfo;
        lstCaseAdditionalDetails.add(caseAddDetails);
        }
        if(lstCase[0].Days_Since_Last_Modified__c != null){
            caseAddDetails= new CaseTransferRequest_H_HUM.CaseAdditionalDetails();
            caseAddDetails.Name = 'Days Since Last Modified';
            caseAddDetails.Value = string.valueof(lstCase[0].Days_Since_Last_Modified__c);
            caseAddDetails.Type = sysInfo;
            lstCaseAdditionalDetails.add(caseAddDetails);
        }
        if(!lstCaseSubQueue.isEmpty() && lstCaseSubQueue.size() != 0){
            if(lstCaseSubQueue[0].NewValue != null){
            caseAddDetails = new CaseTransferRequest_H_HUM.CaseAdditionalDetails();
                caseAddDetails.Name = 'Work Queue View Name';
                caseAddDetails.Value = string.valueof(lstCaseSubQueue[0].NewValue);
                caseAddDetails.Type = sysInfo;
            lstCaseAdditionalDetails.add(caseAddDetails);
            }
        }
        String createdByQueueValue='',lastModifiedQueueValue='';
        if(!lstCaseHistory.isEmpty() && lstCaseHistory.size() != 0){
            if(lstCaseHistory.size() == 1){
                if(String.IsBlank(String.Valueof(lstCaseHistory[0].NewValue))){
                    createdByQueueValue = String.valueof(lstCaseHistory[0].oldValue);
                }else{
                    createdByQueueValue = String.valueof(lstCaseHistory[0].NewValue);
                }
                lastModifiedQueueValue = String.valueof(lstCaseHistory[0].NewValue);
            }else{
                createdByQueueValue = String.valueof(lstCaseHistory[0].NewValue);
                lastModifiedQueueValue = String.valueof(lstCaseHistory[lstCaseHistory.size()-1].NewValue);
            }
        }
        // If case is directly transfered to MHK when created in CRM then case history will be blank. so populating from case.
        lastModifiedQueueValue = String.isBlank(lastModifiedQueueValue)? lstCase[0].LastModifiedby_Queue__c :lastModifiedQueueValue;
        // If case is directly transfered to MHK when created in CRM then case history will be blank. so populating from case.
        createdByQueueValue = String.isBlank(createdByQueueValue)? lstCase[0].Created_By_Queue__c :createdByQueueValue;
        if(createdByQueueValue != null || Test.isRunningTest()){
            caseAddDetails = new CaseTransferRequest_H_HUM.CaseAdditionalDetails();
            caseAddDetails.Name = 'Created By Queue';
            caseAddDetails.Value = createdByQueueValue;
            caseAddDetails.Type = sysInfo;
            lstCaseAdditionalDetails.add(caseAddDetails);
        }
        if(lastModifiedQueueValue != null || Test.isRunningTest()){
                caseAddDetails = new CaseTransferRequest_H_HUM.CaseAdditionalDetails();
            caseAddDetails.Name = 'Last Modified By Queue';
            caseAddDetails.Value = lastModifiedQueueValue;
            caseAddDetails.Type = sysInfo;
            lstCaseAdditionalDetails.add(caseAddDetails);
        }
                
        // ProcessInformation 
        if(!lstTemplateSubData.isEmpty() && lstTemplateSubData.size() != 0){
            
            caseAddDetails = new CaseTransferRequest_H_HUM.CaseAdditionalDetails();
            caseAddDetails.Name = 'Process Type|Created Date|Created By|Last Modified Date|Last Modified By';
            caseAddDetails.Value = string.valueof(lstTemplateSubData[0].Template_Submission__r.Template__r.Description__c) +'|'+ lstTemplateSubData[0].Template_Submission__r.CreatedDate.format() +'|'+ string.valueof(lstTemplateSubData[0].Template_Submission__r.CreatedBy.Name) +'|'+ lstTemplateSubData[0].Template_Submission__r.LastModifiedDate.format() +'|'+ string.valueof(lstTemplateSubData[0].Template_Submission__r.LastModifiedBy.Name);
            caseAddDetails.Type = 'Process';
            lstCaseAdditionalDetails.add(caseAddDetails);
        }
                
        //CaseHistory Information
        List<CaseHistory_DTO_HUM>  lstCaseHistoryDTO = new list<CaseHistory_DTO_HUM>();
        lstCaseHistoryDTO = CaseHistoryDataTable_C_HUM.getCaseHistoryDTOList(string.valueof(lstCase[0].id),'CaseTransfer');
        for(CaseHistory_DTO_HUM caseHistory : lstCaseHistoryDTO)
        {
            caseAddDetails = new CaseTransferRequest_H_HUM.CaseAdditionalDetails();
            caseAddDetails.Name = 'Date|User|Action';
            string actionString = string.valueof(caseHistory.action).replaceAll('amp;lt;bamp;gt;','');
            actionString = actionString.replaceAll('amp;lt;/bamp;gt;', '');
            caseAddDetails.Value = string.valueof(caseHistory.dateCreated) +'|'+ string.valueof(caseHistory.user.substringBefore(',')) +'|'+ actionString;
            caseAddDetails.Type = 'Case History';
            lstCaseAdditionalDetails.add(caseAddDetails);    
        }
        //REQ-586495 
        // sending DCN in addional info.
        if (string.IsNotBlank(mapDCN_URLData.get(HUMConstants.URL_value))) {
            CaseTransferRequest_H_HUM.CaseAdditionalDetails caseAddDetailsDCN = new CaseTransferRequest_H_HUM.CaseAdditionalDetails();
            caseAddDetailsDCN.Name = 'DCN';
            caseAddDetailsDCN.Value = mapDCN_URLData.containskey('DCN')? mapDCN_URLData.get('DCN') : null;
            caseAddDetailsDCN.Type = HUMConstants.Sysinfo;
            lstCaseAdditionalDetails.add(caseAddDetailsDCN);
        
            CaseTransferRequest_H_HUM.CaseAdditionalDetails caseAddDetailsURL = new CaseTransferRequest_H_HUM.CaseAdditionalDetails();
            caseAddDetailsURL.Name = HUMConstants.URL_value;
            caseAddDetailsURL.Value = mapDCN_URLData.containskey(HUMConstants.URL_value)? mapDCN_URLData.get(HUMConstants.URL_value) : null ;
            caseAddDetailsURL.Type = HUMConstants.Sysinfo;
            lstCaseAdditionalDetails.add(caseAddDetailsURL);              
        }
        return lstCaseAdditionalDetails;
    }
    
 
   
    /*
    *  This method is used to create Notes request object
    *  @name   getCaseNotes
    *  @param  case comments
    *  @return List<CaseTransferRequest_H_HUM.Notes>
    */
    private static List<CaseTransferRequest_H_HUM.Notes> getCaseNotes(List<CaseComment> lstCaseComments)
    {
        //Case Notes
        String currentDate = '',target = ' ',replacement = 'T'; 
        List<CaseTransferRequest_H_HUM.Notes> lstNotes = new List<CaseTransferRequest_H_HUM.Notes>();
        for(Integer i=0;i<lstCaseComments.size();i++) 
        {    
        CaseTransferRequest_H_HUM.Notes oNotes = new CaseTransferRequest_H_HUM.Notes();
            oNotes.Note = lstCaseComments[i].CommentBody;
            oNotes.NoteType = 'CRMNotes';
            oNotes.CreatedBy = lstCaseComments[i].CreatedBy.Name;
            currentDate = string.valueof(lstCaseComments[i].CreatedDate);
            String createdDateTime = currentDate.replace(target, replacement);
            oNotes.CreatedDateTime = createdDateTime;
        lstNotes.add(oNotes);
        }
        if(lstCaseComments.isEmpty() && lstCaseComments.size() == 0) 
        {    
        CaseTransferRequest_H_HUM.Notes oNotes = new CaseTransferRequest_H_HUM.Notes();
            oNotes.Note = '';
            oNotes.NoteType = '';
            oNotes.CreatedBy = '';
            oNotes.CreatedDateTime = '';
        lstNotes.add(oNotes);
        }
        return lstNotes;
    }
    
    /*
    *  This method is used to create GriveanceAndAppeal request object
    *  @name   getGAndADetails
    *  @param  case details
    *  @return CaseTransferRequest_H_HUM.GriveanceAndAppeal
    */
    private static CaseTransferRequest_H_HUM.GriveanceAndAppeal getGAndADetails(List<case> lstCase)
    {
        //GriveanceAndAppeal
        CaseTransferRequest_H_HUM.GriveanceAndAppeal oGriveanceAndAppeal = new CaseTransferRequest_H_HUM.GriveanceAndAppeal();
        oGriveanceAndAppeal.Complaint = (lstCase[0].Complaint__c != Null) ? lstCase[0].Complaint__c : '';    
        oGriveanceAndAppeal.ComplaintReason = (lstCase[0].Complaint_Reason__c != Null) ? lstCase[0].Complaint_Reason__c : '';     
        oGriveanceAndAppeal.ComplaintType = (lstCase[0].Complaint_Type__c != Null) ? lstCase[0].Complaint_Type__c : '';    
        oGriveanceAndAppeal.GnAReason = (lstCase[0].G_A_Reason__c != Null) ? lstCase[0].G_A_Reason__c : '';    
        oGriveanceAndAppeal.GnARights = (lstCase[0].G_A_Rights_Given__c != Null) ? lstCase[0].G_A_Rights_Given__c : '';    
        oGriveanceAndAppeal.OralGriveanceCategory = (lstCase[0].Oral_Grievance_Category__c != Null) ? lstCase[0].Oral_Grievance_Category__c : '';    
        oGriveanceAndAppeal.OralGriveanceSubCategory = (lstCase[0].Oral_Grievance_Sub_Category__c != Null) ? lstCase[0].Oral_Grievance_Sub_Category__c : '';    
        return oGriveanceAndAppeal;
    }
    
    /*
    *  This method is used to create AssociateDetails request object
    *  @name   getAssociateDetails
    *  @param  case details,user details
    *  @return CaseTransferRequest_H_HUM.AssociateDetails
    */
    private static CaseTransferRequest_H_HUM.AssociateDetails getAssociateDetails(List<case> lstCase,List<User> lstUser)
    {
        //AssociateDetails
        CaseTransferRequest_H_HUM.AssociateDetails oAssociateDetails = new CaseTransferRequest_H_HUM.AssociateDetails();
        oAssociateDetails.AssociateID = (lstUser[0].Network_User_Id__c != Null) ? lstUser[0].Network_User_Id__c : '';     
        
        //Person under AssociateDetails
        CaseTransferRequest_H_HUM.Person oAssociateDetailPerson = new CaseTransferRequest_H_HUM.Person();
        if(lstCase[0].Interacting_With__r.LastName != null){
            oAssociateDetailPerson.LastName = lstCase[0].Interacting_With__r.LastName;    
        }else{
            oAssociateDetailPerson.LastName = ''; 
        }
        if(lstCase[0].Interacting_With__r.FirstName != null){
            oAssociateDetailPerson.FirstName = lstCase[0].Interacting_With__r.FirstName;    
        }else{
            oAssociateDetailPerson.FirstName = ''; 
        }
        oAssociateDetailPerson.MiddleName = '';
        oAssociateDetailPerson.SSN = '';
        oAssociateDetailPerson.DeceasedDate = '';
        oAssociateDetailPerson.DateOfMarriage = '';
        oAssociateDetailPerson.DateOfBirth = '';
        oAssociateDetailPerson.IsFullTimeStudent = '';
        oAssociateDetailPerson.IsSmoker = '';
        oAssociateDetailPerson.IsPermanentlyDisabled = '';
        oAssociateDetailPerson.NamePrefix = '';
        oAssociateDetailPerson.NameSuffix = '';
        oAssociateDetailPerson.Gender = '';
        oAssociateDetailPerson.MaritalStatus = '';
        oAssociateDetailPerson.Language = '';
        oAssociateDetailPerson.PersonId = '';
    
        oAssociateDetails.Person = oAssociateDetailPerson;
        return oAssociateDetails;
    }

    /*
    *  This method is used to create Attachments request object
    *  @name   getAttachments
    *  @param  attachment log data
    *  @return List<CaseTransferRequest_H_HUM.Attachments> 
    */
    private static List<CaseTransferRequest_H_HUM.Attachments> getAttachments(List<Attachment_Log__c> lstAttachmentLogs)
    {
        //Attachments
        List<CaseTransferRequest_H_HUM.Attachments> lstAttachments = new List<CaseTransferRequest_H_HUM.Attachments>();
        //for loop  lstAttachmentLogs
        for(Integer i=0;i<lstAttachmentLogs.size();i++) 
        {    
        CaseTransferRequest_H_HUM.Attachments oAttachments = new CaseTransferRequest_H_HUM.Attachments();
            oAttachments.AttachmentID = lstAttachmentLogs[i].Name;
            oAttachments.AttachmentType = lstAttachmentLogs[i].Attachment_Type__c;
        lstAttachments.add(oAttachments);
        }
        if(lstAttachmentLogs.isEmpty() && lstAttachmentLogs.size() == 0)
        {
        CaseTransferRequest_H_HUM.Attachments oAttachments = new CaseTransferRequest_H_HUM.Attachments();
            oAttachments.AttachmentID = '';
            oAttachments.AttachmentType = '';
        lstAttachments.add(oAttachments);    
        }
        return lstAttachments;
    }
    
    /*
    *  This method is used to build the request json for case transfer service call.
    *  @name   buildCaseTransferRequest
    *  @param  string sRecordId -  case id
    *  @return string - Returns the Request body
    */
    @testvisible public static string buildCaseTransferRequest(string sRecordId)
    { 
        string sJsonBodyGo = Null;
        try
        {
            if(string.isNotBlank(sRecordId))
            { 
                List<case> lstCase = [select id,CaseNumber,Account.id,Policy_Member__c,CreatedDate,ClosedDate,Classification_Type__c,Subtype__c,
                                      Owner.Name,Origin,Interacting_With_Type__c,Owner_Queue__c,Status,Case_Age__c,Re_Open_Case_Age__c,
                                      Re_Open_Case_Date__c,RecordType.Name,Classification_Id__r.Name,Intent_Id__r.Name,Work_Queue_View_Name__c,
                                      Topic__c,Priority,Interacting_With__r.Name,Interacting_With__r.FirstName,Interacting_With__r.LastName,
                                      Interacting_About_Type__c,DCN__c,Doc_Type__c,Type,G_A_Rights_Given__c,G_A_Reason__c,LastModifiedby_Queue__c,Created_By_Queue__c,
                                      Oral_Grievance_Category__c,Oral_Grievance_Sub_Category__c,Complaint__c,Complaint_Reason__c,
                                      Complaint_Type__c,Case_Initiated_Date__c,Interacting_With__c,Account.Name,CreatedBy.Name,LastModifiedBy.Name,Days_Since_Last_Modified__c,DCN_Type__c,
                                      Interacting_With__r.NPI_ID__c, Interacting_With__r.Individual_First_Name__c, Interacting_With__r.Individual_Last_Name__c,
                                      Account.LastName, Account.FirstName,Account.MiddleName,Account.Enterprise_ID__c,Policy_Member__r.Policy__c,
                                      Policy_Member__r.Name,Policy_Member__r.Effective_Date__c,Policy_Member__r.End_Date__c,Policy_Member__r.Member__c,Policy_Member__r.Group_Number__c,Policy_Member__r.Relationship_Code__c,Policy_Member__r.Issue_State__c,
                                      Interacting_With_Name__c,Call_Benefit_Category__c 
                                      from CASE where id =: sRecordId limit 1];
                List<User> lstUser = new List<User>(); 
                List<Case_Junction__c> lstLinkedCases = new List<Case_Junction__c>();
                List<CaseComment> lstCaseComments = new List<CaseComment>();
                List<CaseHistory> lstRoutingQueue = new List<CaseHistory>();
                List<CaseHistory> lstCaseSubQueue = new List<CaseHistory>();
                List<CaseHistory> lstCaseHistory = new List<CaseHistory>();
                List<Attachment__c> lstAttachment = new List<Attachment__c>();
                List<Attachment_Log__c> lstAttachmentLogs = new List<Attachment_Log__c>();
                //List<Attachment_Log_Item__c> lstAttachmentLogItems = new List<Attachment_Log_Item__c>();
                List<Policy__c> lstPolicyPlans = new List<Policy__c>();
                List<Template_Submission_Data__c> lstTemplateSubData = new List<Template_Submission_Data__c>();
                List<Case_Action_Response__c> lstCaseActionResponseData = new List<Case_Action_Response__c>();
                List<Case_Action_Response__c> lstCaseActionResponseDataList = new List<Case_Action_Response__c>();
                List<Member_ID__c> lstMemberId = new List<Member_ID__c>();
                map<string,string> mapDCN_URLData ;
                if(lstCase != Null && !lstCase.isEmpty())
                {
                    String policyMemId = '';
                    sQueueName = lstCase[0].Owner_Queue__c;
                    if(lstCase[0].Policy_Member__c != Null)
                    {
                        policyMemId = lstCase[0].Policy_Member__c;  
                    }
                    String sCaseType = '';
                    if(bSwitch_1414643 && lstCase[0].Type != Null)
                    {
                        sCaseType = lstCase[0].Type;  
                    }
                    
                    //REQ-592179 
                    //User
                    lstUser = [SELECT Id,Network_User_Id__c FROM User where id =: userInfo.getUserId() limit 1];
                    
                    //LinkedCases
                    lstLinkedCases = [SELECT Id,Case_Master__c, Case__c, Case_Master__r.Casenumber, Case__r.Casenumber FROM Case_Junction__c
                                                where Case_Master__c=: sRecordId OR Case__c=: sRecordId order by createddate desc];
                    //REQ-592179 
                    
                    
                    //case history
                    //Retrieving all records in a single query and looping through all records to take required data
                    List<String> lstFieldValues = new List<String>{'LastModifiedby_Queue__c','Owner_Queue__c','Work_Queue_View_Name__c'};
                    List<CaseHistory> lstTempCaseHistory = [SELECT Id, CaseId, Field, OldValue, NewValue, createddate FROM CaseHistory where field IN : lstFieldValues  AND caseid =: sRecordId order by field,createdDate ASC];   
                    CaseHistory objOwnerQueue; 
                    CaseHistory objQueueView;
                    if(lstTempCaseHistory != Null && !lstTempCaseHistory.IsEmpty())
                    {
                        For(Integer i = 0; i < lstTempCaseHistory.size(); i++)
                        {
                            if(lstTempCaseHistory[i].Field == 'LastModifiedby_Queue__c')
                            {
                                lstCaseHistory.Add(lstTempCaseHistory[i]);
                            }
                            else if(lstTempCaseHistory[i].Field == 'Owner_Queue__c')
                            {
                                objOwnerQueue = lstTempCaseHistory[i];
                            }
                            else if(lstTempCaseHistory[i].Field == 'Work_Queue_View_Name__c')
                            {
                                objQueueView = lstTempCaseHistory[i];
                            } 
                        }
                        if(objOwnerQueue != Null) lstRoutingQueue.Add(objOwnerQueue);
                        if(objQueueView != Null) lstCaseSubQueue.Add(objQueueView);
                    }
                    //REQ-730552 
                    //MemberID ---- Added null check
                    if(String.IsNotBlank(policyMemId)) lstMemberId = [SELECT Name, member_dependent_code__c FROM Member_ID__c where Type__c='Member-Id-Base' AND Policy_Member__c =: policyMemId Limit 1];    
                    //REQ-730552 

        
                    if (bSwitch_1414643 && String.IsNotBlank(sCaseType) && sCaseType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.MHK_CASE_TYPE))
                                                                                                                                                                                            {
                        //Case Comments
                        lstCaseComments = [SELECT Id,CommentBody, CreatedById,CreatedBy.Name,CreatedDate, ParentId FROM CaseComment where ParentId =: sRecordId And CreatedBy.Name!=: GLOBAL_CONSTANT_HUM.DatapowerUserName];

                        //Attachments of Case
                        lstAttachment = [Select Id, Name, Related_To_Case__c,CreatedDate,Version_Id__c,DocId__c,AlternateDocId__c,Document_Source__c FROM Attachment__c where Related_To_Case__c =: sRecordId And CreatedBy.Name!=: GLOBAL_CONSTANT_HUM.DatapowerUserName];
                    }
                    else
                    {
                        //Case Comments
                        lstCaseComments = [SELECT Id,CommentBody, CreatedById,CreatedBy.Name,CreatedDate, ParentId FROM CaseComment where ParentId =: sRecordId];

                        //Attachments of Case
                        lstAttachment = [Select Id, Name, Related_To_Case__c,CreatedDate,Version_Id__c,DocId__c,AlternateDocId__c,Document_Source__c FROM Attachment__c where Related_To_Case__c =: sRecordId];                    
                    }        
                    
                    //Attachment Logs                    
                    lstAttachmentLogs = [SELECT Id, Case__c, Name, Attachment_Type__c,User_Selected_Value__c,Standard_Value__c,(select Name,Value__c from Attachment_Log_Items__r) FROM Attachment_Log__c where Case__c =: sRecordId];
            
                    //Template data of Case ---Retrieving Template_Submission__c record details from Template_Submission_Data__c records
                    lstTemplateSubData = [Select Name__c, Value__c, Template_Submission__r.Template__r.Description__c,Template_Submission__r.LastModifiedDate,
                                          Template_Submission__r.LastModifiedBy.Name, Template_Submission__r.CreatedDate, Template_Submission__r.CreatedBy.Name from Template_Submission_Data__c 
                                          where Template_Submission__r.Submission_Owner__r.Object_Owner_ID__c =: sRecordId ORDER BY Name__c ASC];
                    
                    //QAA - Case Action ---- Retrieving Case_Action_Response__c records based on caseid instead of action id. 
                        lstCaseActionResponseData = [SELECT Process_Question__c, Process_Question_Response__c,CreatedDate, LastModifiedDate,CreatedBy.Name,LastModifiedBy.Name,Action__r.Case_Action__r.Name FROM Case_Action_Response__c where Action__r.Case__r.id=:sRecordId];    
                        List<Case_Action_Response__c> lstYesValues = new List<Case_Action_Response__c>();
                        List<Case_Action_Response__c> lstNoValues = new List<Case_Action_Response__c>();
                        String Verbally ='No';

                        //Removed extra FOR loop to know user response to a particular question    
                        for(Case_Action_Response__c caseActionResp : lstCaseActionResponseData)
                        {
                            if(caseActionResp.Process_Question__c.Contains('Is the Member') && caseActionResp.Process_Question_Response__c.Contains('Yes')){
                                Verbally = 'Yes';   
                            }
                            if((!caseActionResp.Process_Question__c.Contains('DISCLAIMER')) && (!caseActionResp.Process_Question__c.Contains('NOTE')) && (!caseActionResp.Process_Question__c.Contains('Grievance and Appeal')) 
                                && (!caseActionResp.Process_Question__c.Contains('REQUIRED ACTION')) && (!caseActionResp.Process_Question__c.Contains('Access')) && (!caseActionResp.Process_Question__c.Contains('G&A')) && (!caseActionResp.Process_Question__c.Contains('Provider DBA')))
                            {   
                                lstYesValues.add(caseActionResp);
                            }    
                    
                            if(caseActionResp.Process_Question__c.Contains('Process Type') || caseActionResp.Process_Question__c.Contains('Is the Member'))
                            {
                                lstNoValues.add(caseActionResp);
                            }    
                        }
                        if(Verbally == 'Yes')
                        {
                            lstCaseActionResponseDataList.Addall(lstYesValues);
                        }
                        else
                        {
                            lstCaseActionResponseDataList.Addall(lstNoValues);
                        }

                    if(!lstCase.isEmpty() && String.isNotBlank(lstCase[0].Policy_Member__c))
                    {
                        if(bswitch_1348280){
                        String policyId = lstCase[0].Policy_Member__r.Policy__c;

                        if(String.isNotBlank(policyId))
                        {  
                                      //Policy Plan Details
                                      lstPolicyPlans = [Select id, Name, Source_Major_LOB__c,Issue_State__c,Platform__c, MAJOR_LOB_FRM__C,ORIGINAL_MAJOR_LOB__C
                                                                                               from Policy__c where id =: policyId Limit 1];
                        }
                      }
                    }
                }
                String currentDate = '',target = ' ',replacement = 'T'; 
                CaseTransferRequest_H_HUM oRequest = new CaseTransferRequest_H_HUM();
            
                CaseTransferRequest_H_HUM.TransferCaseRequest oTransferCaseRequest =
                    new CaseTransferRequest_H_HUM.TransferCaseRequest();
                //Header
                CaseTransferRequest_H_HUM.Header oHeader = getHeaderDetails(lstCase);
                
                //TransferCaseRequest-Header
                oTransferCaseRequest.Header = oHeader ;
                
                //Message
                CaseTransferRequest_H_HUM.Message oMessage = new CaseTransferRequest_H_HUM.Message();

                //Corresponding
                List<CaseTransferRequest_H_HUM.Correspondent> lstCorrespondent = getCorrespondentList(lstCase);
                oMessage.Correspondent = lstCorrespondent;

                //REQ-592179  
                //RelatedInquiries
                List<CaseTransferRequest_H_HUM.RelatedInquiries> lstRelatedInquiries = getRelatedInquiries(sRecordId,lstLinkedCases);                
                oMessage.RelatedInquiries = lstRelatedInquiries;

                //REQ-592179 
                //Documents
                mapDCN_URLData = prepareCaseDCNandURLData(lstAttachmentLogs);
                List<CaseTransferRequest_H_HUM.Document> lstDocument = getDocumentsList(lstAttachment,lstCase,mapDCN_URLData );
                //Document - Message
                oMessage.Document = lstDocument;

                //Claims
                List<CaseTransferRequest_H_HUM.Claim> lstClaim = getClaimDetails( lstAttachmentLogs);
                //Claim - Message
                oMessage.Claim = lstClaim;
                
              
                //Provider
                CaseTransferRequest_H_HUM.Provider oProvider = getProviderDetails(lstCase);
                //Provider- Message
                oMessage.Provider = oProvider;

                //Member
                List<CaseTransferRequest_H_HUM.Member> lstMember = getMemberDetails(lstCase,lstMemberId,lstPolicyPlans);
                oMessage.Member = lstMember;

                //Case Details
                CaseTransferRequest_H_HUM.CaseDetails oCaseDetails = getCaseDetails(lstCase,lstRoutingQueue,lstCaseSubQueue);
                if(oCaseDetails!=NULL){
                oMessage.CaseDetails = oCaseDetails;
                  }
                //REQ-586495 
                //CaseAdditionalDetails
                List<CaseTransferRequest_H_HUM.CaseAdditionalDetails> lstCaseAdditionalDetails = getCaseAdditionalDetails(mapDCN_URLData,lstCaseActionResponseData,lstCase,lstCaseHistory,lstCaseSubQueue,lstTemplateSubData,lstCaseActionResponseDataList);
                oCaseDetails.CaseAdditionalDetails = lstCaseAdditionalDetails;

                //Case Notes
                List<CaseTransferRequest_H_HUM.Notes> lstNotes = getCaseNotes(lstCaseComments);
                oCaseDetails.Notes = lstNotes;

                //GriveanceAndAppeal
                CaseTransferRequest_H_HUM.GriveanceAndAppeal oGriveanceAndAppeal = getGAndADetails(lstCase);
                oCaseDetails.GriveanceAndAppeal = oGriveanceAndAppeal;

                //REQ-592179 
                //AssociateDetails
                CaseTransferRequest_H_HUM.AssociateDetails oAssociateDetails = getAssociateDetails(lstCase,lstUser);
                oCaseDetails.AssociateDetails = oAssociateDetails;

                //REQ-592179 
                //Attachments
                List<CaseTransferRequest_H_HUM.Attachments> lstAttachments = getAttachments(lstAttachmentLogs);
                oCaseDetails.Attachments = lstAttachments;
                oMessage.CaseDetails = oCaseDetails;
                
                //TransferCaseRequest-Message
                oTransferCaseRequest.Message = oMessage;

                oRequest.TransferCaseRequest = oTransferCaseRequest;
                sJsonBodyGo = JSON.serialize(oRequest);
            }
        }
        catch(Exception ex)
        {   
            HUMExceptionHelper.logErrors(ex, 'CaseTransferService_S_HUM', 'buildCaseTransferRequest');
            sJsonBodyGo  =  NULL;
        }
        return sJsonBodyGo ;
    }

    public static map<string,string> prepareCaseDCNandURLData(list<Attachment_Log__c> lstAttachmentLogs){
        map<string,string> dcn_urlData = new map<string,string>();
        if(bswitch_1348280_1)
        dcn_urlData.put('Recieved By','');
         
        try{
            for(Attachment_Log__c objAttLog : lstAttachmentLogs){
                list<Attachment_Log_Item__c> lstATTLI = objAttLog.Attachment_Log_Items__r;
                if(lstATTLI!=null && !lstATTLI.isEmpty() && lstATTLI.size()>1){
                    for(Attachment_Log_Item__c objALI : lstATTLI){
                        if(objALI.Name == HUMConstants.DCN_value){
                            dcn_urlData.put(HUMConstants.DCN_value,objALI.Value__c);
                        }
                        if(objALI.Name == HUMConstants.URL_value){
                            dcn_urlData.put(HUMConstants.URL_value,objALI.Value__c);
                        }
                        if(bSwitch_MF4_1197812 && objALI.Name == HUMConstants.HUMMacesUnitNumber){
                            dcn_urlData.put(HUMConstants.HUMMacesUnitNumber,objALI.Value__c);
                        }
                    }
                }
            }    
       }catch (exception ex){
           HUMExceptionHelper.logErrors(ex, 'CaseTransferService_S_HUM', 'prepareCaseDCNandURLData');
       }
       return dcn_urlData;
    }
    
    /*
    *  This method is used to format the case related date fields into expected format
    *  @name   dateFormatting
    *  @param  string theDateValue 
    *  @return string - Returns formatted date
    */
    public static string dateFormatting(String theDateValue)
    {   
        String theDate = theDateValue;
        String finalDate= '';
        if(String.isBlank(theDate))
        {
            finalDate = '';
        }
        else
        {
            String reqDate= theDate.subString(0,theDate.length());
            String [] dateArray = reqDate.split('/');
            String day='',month='',deterDate='';
            if(dateArray[1].length() == 1){ day = '0'+dateArray[1];
                                          }else{
                                              day = dateArray[1];
                                          }    
            if(dateArray[0].length() == 1){ month = '0'+dateArray[0];
                                          }else{
                                              month = dateArray[0];
                                          }
            finalDate = dateArray[2]+'-'+month+'-'+day;
        }
        return finalDate;
    }
   
}