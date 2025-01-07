/****************************************************************************************
Apex Class Name  : CaseTransferRequest_T_HUM
Version          : 1.0 
Created Date     : 10-01-2019
Function         : Test class to CaseTransferRequest_H_HUM  Apex class 
Modification Log
*      Developer               Code Review            Date                 Description
* --------------------------------------------------------------------------------------------------------        
*   Ajay Chakradhar                                 10/01/2019              Original Version     
***********************************************************************************************************/
@isTest
private class CaseTransferRequest_T_HUM
{
    /*
    * Method Name : testSetup 
    * Description : Test method to setup common test data for all test methods     
    * Return type : void     
    * Paramater   : NA  
    */
    @testSetup 
    static void testSetup()
    {        
        HUMTestDataHelperTest.getHUMConstantsData();
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;
        System.assertEquals(oUser.LastName, 'test User1');
    }
    
    /*
     * Method Name : testCaseTransferService
     * Description : Test method for CaseTransferRequest_H_HUM
     * Return type : NA
     * Paramater   : NA
     */
    static testMethod void testCaseTransferService()
    {            
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];                
        system.runAs(oUser)
        {
          Test.StartTest();
            CaseTransferRequest_H_HUM oRequest = new CaseTransferRequest_H_HUM();
            CaseTransferRequest_H_HUM.TransferCaseRequest oTransferCaseRequest =
                    new CaseTransferRequest_H_HUM.TransferCaseRequest();
                
                //Header
                CaseTransferRequest_H_HUM.Header oHeader = new CaseTransferRequest_H_HUM.Header();
                
                //source
                CaseTransferRequest_H_HUM.Source oSource = new CaseTransferRequest_H_HUM.Source();
                oSource.Channel = 'SALESFORCE';
                oSource.SourceDesc = 'Customer Relationship Management';
                oSource.SourceID = 'CRM';
                
                oHeader.Source = oSource;
                
                //DestinationList
                List<CaseTransferRequest_H_HUM.DestinationList> lstDestinationList = 
                    new List<CaseTransferRequest_H_HUM.DestinationList>();
                
                CaseTransferRequest_H_HUM.DestinationList oDestinationList = new CaseTransferRequest_H_HUM.DestinationList();
              		oDestinationList.TargetSystem = 'CRE';
                	oDestinationList.TargetSystemQueue = '';
                lstDestinationList.add(oDestinationList);
                
                oHeader.DestinationList = lstDestinationList;
                
                //TransactionDetails
                CaseTransferRequest_H_HUM.TransactionDetails oTransactionDetails =
                    new CaseTransferRequest_H_HUM.TransactionDetails();
                		oTransactionDetails.TransactionID = '';    
                    	oTransactionDetails.SourceTransactionID = '';
                    	oTransactionDetails.SourceTransactionID = '';    
                		oTransactionDetails.TransactionTime = '';
                        oTransactionDetails.SourceTransactionTime = '';
                    	oTransactionDetails.TransactionType = 'CASE_TRANSFER_REQ';
                oHeader.TransactionDetails = oTransactionDetails;
                
                //TransferCaseRequest-Header
                oTransferCaseRequest.Header = oHeader ;
                //Message
                CaseTransferRequest_H_HUM.Message oMessage = new CaseTransferRequest_H_HUM.Message();
                
                //Corresponding
                List<CaseTransferRequest_H_HUM.Correspondent> lstCorrespondent = new List<CaseTransferRequest_H_HUM.Correspondent>();
                
                CaseTransferRequest_H_HUM.Correspondent oCorrespondent = new CaseTransferRequest_H_HUM.Correspondent();
                    oCorrespondent.SubscriberID = '';
                	oCorrespondent.SubmitterID = '';      
               		oCorrespondent.SolicitationID = '';      
               		oCorrespondent.FederalTaxID = '';      
               		oCorrespondent.InquiryReason = '';      
               		oCorrespondent.InquiryType = '';      
               		oCorrespondent.SubmitterType = '';
                //Person under Correspondent
                CaseTransferRequest_H_HUM.Person oCorpPerson = new CaseTransferRequest_H_HUM.Person();
                	oCorpPerson.LastName ='';
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
                
                	oCorrespondent.Person = oCorpPerson;
                
				lstCorrespondent.add(oCorrespondent);
                
                oMessage.Correspondent = lstCorrespondent;

                //RelatedInquiries
            	List<CaseTransferRequest_H_HUM.RelatedInquiries> lstRelatedInquiries = new List<CaseTransferRequest_H_HUM.RelatedInquiries>();
                
					CaseTransferRequest_H_HUM.RelatedInquiries oRelatedInquiries = new CaseTransferRequest_H_HUM.RelatedInquiries();
                        oRelatedInquiries.InquiryPlatform = '';
                        oRelatedInquiries.InitiatingSys = '';    
                        oRelatedInquiries.InquiryID = ''; 
                        
                    lstRelatedInquiries.add(oRelatedInquiries);    
                oMessage.RelatedInquiries = lstRelatedInquiries;    
               
                //Documents
				List<CaseTransferRequest_H_HUM.Document> lstDocument = new List<CaseTransferRequest_H_HUM.Document>();
                    CaseTransferRequest_H_HUM.Document oDocument = new CaseTransferRequest_H_HUM.Document();
                        	oDocument.DocumentType = '';    
                        	oDocument.CaseType = '';
                            oDocument.CaseSubType = '';
                        	oDocument.DocumentControlNumber = '';    
                        	oDocument.SourceDocumentID = '';    
                        	oDocument.DocumentSource = 'Ehub';
                            oDocument.TrackingID = '';
                            oDocument.ReceivedDateTime = '';
                            oDocument.MethodOfReceipt = '';
                            oDocument.FaxNumber = '';
                        	oDocument.DocumentVersion = '';    
                        
                    CaseTransferRequest_H_HUM.FileList oFileList = new CaseTransferRequest_H_HUM.FileList();
                        	oFileList.FileName = '';    
                       
        				oDocument.FileList = oFileList;
                    lstDocument.add(oDocument);
                
                //Document - Message
				oMessage.Document = lstDocument;
                
                //Claims
                List<CaseTransferRequest_H_HUM.Claim> lstClaim = new List<CaseTransferRequest_H_HUM.Claim>();
                CaseTransferRequest_H_HUM.Claim oClaim = new CaseTransferRequest_H_HUM.Claim();
                        oClaim.ClaimNumber = '';    
                        oClaim.IsPaid = '';    		         	 
                        oClaim.ClaimStatus = '';    	         
                        oClaim.TimelinessDays = '';        
                        oClaim.DateOfService = '';          
                        oClaim.TypeOfService = '';          
                        oClaim.AuthorizationInformation = '';    
                        oClaim.PARIndicator = '';            
                        oClaim.CaseType = '';
                lstClaim.add(oClaim);
                //Claim - Message
                oMessage.Claim = lstClaim;
                //Provider
                CaseTransferRequest_H_HUM.Provider oProvider = new CaseTransferRequest_H_HUM.Provider();
                		oProvider.ProviderID = '';    		    
                        oProvider.NationalProviderId = '';     
                        oProvider.ProviderFullName = '';         
                        oProvider.FirstName = '';    		        	
                        oProvider.LastName = '';    		        	
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
                //Provider- Message
                oMessage.Provider = oProvider;
                //Member
                List<CaseTransferRequest_H_HUM.Member> lstMember = new List<CaseTransferRequest_H_HUM.Member>();
                CaseTransferRequest_H_HUM.Member oMember = new CaseTransferRequest_H_HUM.Member();
                
                //Person - Member
                CaseTransferRequest_H_HUM.Person oPerson = new CaseTransferRequest_H_HUM.Person();
                	oPerson.LastName = '';
                    oPerson.FirstName = '';
                	oPerson.PersonId= '';
                	oPerson.MiddleName = '';    	
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
                oMember.Person = oPerson;
                //Plan - Member
                CaseTransferRequest_H_HUM.Plan oPlan = new CaseTransferRequest_H_HUM.Plan();
                    oPlan.MemberID = '';    
                    oPlan.PlanStartDate = '';
                    oPlan.PlanEndDate = '';
                    oPlan.GroupNumber = '';    
                  	oPlan.DivisionId = '';    	          
                    oPlan.DependentCode = '';    
                    oPlan.IssueState = '';    
                    oPlan.MemberGenKey = '';
                    oPlan.SourcePlatform = '';    
                    oPlan.LineOfBusiness = '';    
                    
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
                
                oMessage.Member = lstMember;
                //Case Details
                CaseTransferRequest_H_HUM.CaseDetails oCaseDetails = new CaseTransferRequest_H_HUM.CaseDetails();
                	oCaseDetails.SourceChannel = 'CRM';
                	oCaseDetails.CaseNumber = '';
                	oCaseDetails.CaseOriginatingQueue = '';    
                    oCaseDetails.RoutingQueue = '';    
                    oCaseDetails.CaseOriginatingSubQueue = '';    
                    oCaseDetails.RoutingView = '';
                	oCaseDetails.CaseType = '';    
                	oCaseDetails.CaseSubType = '';    
                	oCaseDetails.Status = '';    
                	oCaseDetails.CaseAge = '';    
                	oCaseDetails.CaseOpenDate = '';    			         
                	oCaseDetails.ReOpenCaseAge = '';    
                	oCaseDetails.CaseRecordType = '';    
                	oCaseDetails.Classification = '';    
                	oCaseDetails.ClassificationType = '';    
                	oCaseDetails.Intent = '';    
                	oCaseDetails.Topic = '';    
                	oCaseDetails.Priority = '';  
                	oCaseDetails.InteractingWith = '';  
                	oCaseDetails.InquiringAboutType = ''; 
                	oCaseDetails.AccountName = '';
                	oCaseDetails.ReceivedDateTime = '';
                        
                oMessage.CaseDetails = oCaseDetails;
                //CaseAdditionalDetails
                List<CaseTransferRequest_H_HUM.CaseAdditionalDetails> lstCaseAdditionalDetails = new List<CaseTransferRequest_H_HUM.CaseAdditionalDetails>();
                
                CaseTransferRequest_H_HUM.CaseAdditionalDetails caseAddDetails = new CaseTransferRequest_H_HUM.CaseAdditionalDetails();
                    caseAddDetails.Name = '';
                    caseAddDetails.Value = '';
                    caseAddDetails.Type = '';
                lstCaseAdditionalDetails.add(caseAddDetails);
                
                oCaseDetails.CaseAdditionalDetails = lstCaseAdditionalDetails;
                
                List<CaseTransferRequest_H_HUM.Notes> lstNotes = new List<CaseTransferRequest_H_HUM.Notes>();
                
				CaseTransferRequest_H_HUM.Notes oNotes = new CaseTransferRequest_H_HUM.Notes();
                	oNotes.Note = '';
                	oNotes.NoteType = 'CRMNotes';
                	oNotes.CreatedBy = '';
                    oNotes.CreatedDateTime = '';
                lstNotes.add(oNotes);
                
                oCaseDetails.Notes = lstNotes;
                CaseTransferRequest_H_HUM.GriveanceAndAppeal oGriveanceAndAppeal = new CaseTransferRequest_H_HUM.GriveanceAndAppeal();
                    oGriveanceAndAppeal.Complaint = '';    
                    oGriveanceAndAppeal.ComplaintReason = '';    
                    oGriveanceAndAppeal.ComplaintType = '';    
                    oGriveanceAndAppeal.GnAReason = '';    
                    oGriveanceAndAppeal.GnARights = '';    
                    oGriveanceAndAppeal.OralGriveanceCategory = '';    
                    oGriveanceAndAppeal.OralGriveanceSubCategory = '';    
                	
                oCaseDetails.GriveanceAndAppeal = oGriveanceAndAppeal;

                CaseTransferRequest_H_HUM.AssociateDetails oAssociateDetails = new CaseTransferRequest_H_HUM.AssociateDetails();
                    oAssociateDetails.AssociateID = '';    
                    
                //Person under AssociateDetails
                CaseTransferRequest_H_HUM.Person oAssociateDetailPerson = new CaseTransferRequest_H_HUM.Person();
                    oAssociateDetailPerson.LastName = ''; 
                    oAssociateDetailPerson.FirstName = ''; 
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
            	oCaseDetails.AssociateDetails = oAssociateDetails;
            
                List<CaseTransferRequest_H_HUM.Attachments> lstAttachments = new List<CaseTransferRequest_H_HUM.Attachments>();
                
                CaseTransferRequest_H_HUM.Attachments oAttachments = new CaseTransferRequest_H_HUM.Attachments();
                	oAttachments.AttachmentID = '';
                	oAttachments.AttachmentType = '';
                lstAttachments.add(oAttachments);    
                
                oCaseDetails.Attachments = lstAttachments;
                
                oMessage.CaseDetails = oCaseDetails;
                
                //TransferCaseRequest-Message
                oTransferCaseRequest.Message = oMessage;

                oRequest.TransferCaseRequest = oTransferCaseRequest;
            
            system.assert(oRequest!=null);
          Test.StopTest();
        }
    }
}