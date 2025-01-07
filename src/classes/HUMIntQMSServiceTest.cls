/******************************************************************************************************************************
Apex Class Name  : HUMIntQMSServiceTest
Version          : 1.0 
Created Date     : August 14 2014
Function         : Test class for HUMIntQMSService
                   Used to cover the WSDL class for QMS service.

Modification Log :
 *   Modification ID     Developer                  Code Review         Date                 Description
 * ------------------------------------------------------------------------------------------------------------------------------                
 *           1.0         Vishal Verma                17703              08/13/2014          Original Version 
 *           2.0         Sharan Shanmugam            22195              04/03/2015          Made changes as per REQ - 167153  
 *           3.0         Gargey Sharma            	 28243              11/26/2015          REQ - 237212 (QMS Referral Service with Encryption)                                                                              
 *********************************************************************************************************************************/
@isTest
private class HUMIntQMSServiceTest
{

    /*
    * Method Name : createCustomData
    * Description : Create Test Dat 
    * Return type : void
    * Paramater   : NA
    */
    @testSetup 
    static void createCustomData() 
    {
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;
        system.assert(user.Id != null);
        insert new HUM_Webservice_Callout__c(Name='QMSReferralService', Certificate_Name__c='Humana_GuidanceCare_Mars', SampleParameter__c='sample',  Active__c=true, End_Point_URL__c='https://int-crmservicews.humana.com/Services/Data/QMSReferralService.svc/asmx', Timeout__c='12000', Request_Type__c='POST', Content_Type__c='text/xml',Methods_Available__c='A;B;C');
    }
    
     /*
    * Method Name : TestIntQMSService
    * Description : Test method 
    * Return type : void
    * Paramater   : NA
    */  
    private Static TestMethod void TestIntQMSService()
    {
    	User oUser = [Select Id , Name from User where Username= 'user1user1@humcrmstest.com'  order by LastModifiedDate desc Limit 1];
        Test.setMock(WebServiceMock.class, new HUMQMSServiceMockTest());
        system.runas(oUser)
        {
             Test.StartTest();
                HUMIntQMSService intQmsService = new HUMIntQMSService();                
                HUMIntQMSService.AttachmentDataContract AttachmentDataContract = new HUMIntQMSService.AttachmentDataContract();
                HUMIntQMSService.ApplicationFault ApplicationFault = new HUMIntQMSService.ApplicationFault();
                HUMIntQMSService.ArrayOfFaultException_FaultReasonData ArrayOfFaultException_FaultReasonData = new HUMIntQMSService.ArrayOfFaultException_FaultReasonData();
                HUMIntQMSService.SaveReferralCRMS_element SaveReferralCRMS_element = new HUMIntQMSService.SaveReferralCRMS_element();                
                HUMIntQMSService.FaultException_FaultCodeData FaultException_FaultCodeData = new HUMIntQMSService.FaultException_FaultCodeData();
                HUMIntQMSService.FaultException_FaultReasonData FaultException_FaultReasonData = new HUMIntQMSService.FaultException_FaultReasonData();
                HUMIntQMSService.LoadTransactionsFault LoadTransactionsFault = new HUMIntQMSService.LoadTransactionsFault();                
                HUMIntQMSService.QualityReferralCRMRequestDataContract QualityReferralCRMRequestDataContract = new HUMIntQMSService.QualityReferralCRMRequestDataContract();                
                HUMIntQMSService.SaveReferralCRMSResponse_element SaveReferralCRMSResponse_element = new HUMIntQMSService.SaveReferralCRMSResponse_element();                
                HUMIntQMSService.FaultError FaultError = new HUMIntQMSService.FaultError();                
                HUMIntQMSService.ArrayOfAttachmentDataContract ArrayOfAttachmentDataContract = new HUMIntQMSService.ArrayOfAttachmentDataContract();                
                HUMIntQMSService.QualityReferralCRMResponseDataContract QualityReferralCRMResponseDataContract = new HUMIntQMSService.QualityReferralCRMResponseDataContract();
                HUMIntQMSService.AsyncBasicHttpBinding_IQMSReferralService  BasicHttpBinding_IQMSReferralService = new HUMIntQMSService.AsyncBasicHttpBinding_IQMSReferralService();   
                HUMIntQMSService.QMSFault qmsFault= new HUMIntQMSService.QMSFault();
                HUMIntQMSService.SystemError sysErr = new HUMIntQMSService.SystemError();
                HUMIntQMSService.UnexpectedErrorFault ErrFau = new HUMIntQMSService.UnexpectedErrorFault();
                HUMIntQMSService.TimeoutFault TOFault = new HUMIntQMSService.TimeoutFault();
                HUMIntQMSService.ArgumentFault ArgFault = new HUMIntQMSService.ArgumentFault();
                HUMIntQMSService.ArrayOfKeyValueOfstringstring arrKey = new HUMIntQMSService.ArrayOfKeyValueOfstringstring();
                HUMIntQMSService.CommunicationFault CommFault = new HUMIntQMSService.CommunicationFault();
                HUMIntQMSService.DataContract dataCon = new HUMIntQMSService.DataContract();
                HUMIntQMSService.CommunicationError CommErr = new HUMIntQMSService.CommunicationError();
                HUMIntQMSService.Error err = new HUMIntQMSService.Error();
                HUMIntQMSService.ValidationFault valFau = new HUMIntQMSService.ValidationFault();
                HUMIntQMSService.BaseFault BaseFau = new HUMIntQMSService.BaseFault();
                HUMIntQMSService.ArrayOfFaultException_FaultCodeData ArrFau = new HUMIntQMSService.ArrayOfFaultException_FaultCodeData();
                
                QualityReferralCRMRequestDataContract.Guid = 'Test';
                HUMIntQMSService.ArrayOfAttachmentDataContract Att = new HUMIntQMSService.ArrayOfAttachmentDataContract();
                HUMIntQMSService.AttachmentDataContract ADC = new HUMIntQMSService.AttachmentDataContract();
                ADC.Guid = '1234';
        		ADC.AttachmentURL = 'https://';
        		ADC.CreatedDate = DateTime.ValueOf('2013-01-01 00:00:00');
        		ADC.FileName = 'Test';
        		ADC.LastModifiedDate = DateTime.ValueOf('2013-01-01 00:00:00');
                list<HUMIntQMSService.AttachmentDataContract> lstADC = new list<HUMIntQMSService.AttachmentDataContract>();
                lstADC.add(ADC);
                att.AttachmentDataContract = lstADC;
        		QualityReferralCRMRequestDataContract.Attachments = Att;
        		QualityReferralCRMRequestDataContract.CaseCreatedDate = DateTime.ValueOf('2013-01-01 00:00:00');
        		QualityReferralCRMRequestDataContract.CaseId = 100000002;
        		QualityReferralCRMRequestDataContract.CaseNotes = 'Test';
        		QualityReferralCRMRequestDataContract.CaseStatus = 'Test';
        		QualityReferralCRMRequestDataContract.CaseType = 'Test';
        		QualityReferralCRMRequestDataContract.CreatedDate = DateTime.ValueOf('2013-01-01 00:00:00');
        		QualityReferralCRMRequestDataContract.CreatedDepartmentName = 'Test';
        		QualityReferralCRMRequestDataContract.CreatedFirstName = 'Test';
        		QualityReferralCRMRequestDataContract.CreatedId = 'Test';
        		QualityReferralCRMRequestDataContract.CreatedLastName = 'Test';
        		QualityReferralCRMRequestDataContract.CreatedQueueName = 'Test';
        		QualityReferralCRMRequestDataContract.CreatedSiteName = 'Test';
        		QualityReferralCRMRequestDataContract.DateOfOccurrence = '20012001';
        		QualityReferralCRMRequestDataContract.InteractingAbout = 'Member';
        		QualityReferralCRMRequestDataContract.InteractingWith = 'Member';
        		QualityReferralCRMRequestDataContract.InteractionID = 20000007;
        		QualityReferralCRMRequestDataContract.LastModifiedBy = 'Mem';
        		QualityReferralCRMRequestDataContract.LastModifiedDate = DateTime.ValueOf('2013-01-01 00:00:00');
        		QualityReferralCRMRequestDataContract.MemberDOB = '20012001';
        		QualityReferralCRMRequestDataContract.MemberFirstName = 'Test';
        		QualityReferralCRMRequestDataContract.MemberID = '1234';
        		QualityReferralCRMRequestDataContract.MemberLastName = 'xyz';
        		QualityReferralCRMRequestDataContract.OwnerDepartmentName = 'qms';
        		QualityReferralCRMRequestDataContract.OwnerFirstName = 'Mem';
        		QualityReferralCRMRequestDataContract.OwnerId = 'M';
        		QualityReferralCRMRequestDataContract.OwnerQueueId = 'GB';
        		QualityReferralCRMRequestDataContract.OwnerQueueName = 'Green';
        		QualityReferralCRMRequestDataContract.OwnerSiteName = 'Office';
        		QualityReferralCRMRequestDataContract.ProviderName = 'Hum';
        		QualityReferralCRMRequestDataContract.ProviderTaxID = '11111111';
        		QualityReferralCRMRequestDataContract.ReferralDate = DateTime.ValueOf('2013-01-01 00:00:00');
        		QualityReferralCRMRequestDataContract.TemplateNotes = 'Notes';
        		QualityReferralCRMRequestDataContract.ToDepartment = 'Market';
        		QualityReferralCRMRequestDataContract.ToQueue = 'Quality';
        		QualityReferralCRMRequestDataContract.ToServiceCenter = 'Q';      
             Test.StopTest();   
        }
    }
}