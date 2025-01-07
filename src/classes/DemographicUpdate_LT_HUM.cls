/*****************************************************************************************************************************************************************************
    @Apex Class Name  : DemographicUpdate_LT_HUM
    @Version          : 1.0
    @Created Date     : 06/09/2022
    @Test Class For  : DemographicUpdate_LS_HUM
******************************************************************************************************************************************************************************
Modification Log:

* Developer Name           Review Number              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Ashish Kumar                                 		  06/09/2022                 Original Version
******************************************************************************************************************************************************************************/

@istest
public class DemographicUpdate_LT_HUM
{
    /*
    * This method creates test data for test class
    * @param        None
    * @return       None
    */
     @testSetup static void prepareTestData() 
     {  
         id RecordTypeID=Schema.SObjectType.case.getrecordtypeinfosByname().get('Medicare Case').getRecordtypeID();
         
         Account account = HUMTestDataHelperTest.getAccount();
         account.Name = 'Test Account';
         insert account;
         
         Case cse = HUMTestDataHelperTest.getCase();
         cse.subject = 'Test Subject';
         cse.Description = 'Test Description';
         cse.AccountId = account.id;
         cse.RecordTypeId = RecordTypeID;
         insert cse;
         
         User oSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
         insert oSUser;
      
        
        System.runAs(osUser) 
        {
            HUM_Webservice_Callout__c objAddressStandardize = new HUM_Webservice_Callout__c();
            objAddressStandardize.Name = 'MBEPlusService';
            objAddressStandardize.Active__c = True;
            objAddressStandardize.Enable_Service_Tester__c = True;
            objAddressStandardize.Certificate_Name__c = 'Humana_GuidanceCare_Deimos';
            objAddressStandardize.Content_Type__c = 'application/json';
            objAddressStandardize.Request_Type__c = 'POST';
            objAddressStandardize.Timeout__c = '12000';
            objAddressStandardize.End_Point_URL__c = 'https://int-crmservicews.humana.com/MBEPLUSServiceREST/GetMember';
            insert objAddressStandardize;
            
        }
    }
    /*
    * Method to test success response for DemographicUpdate
    * @param        None
    * @return       None
    */
    private static testmethod void testSuccessDemographicUpdate()
    {
        User testUser = [Select Id,Name from User where LastName='test User1' Limit 1];
        Case caserec=[select id,subject,description,Accountid from case where subject='Test Subject' limit 1];
        system.runAs(testUser)
        { 
            Test.StartTest();
            DemographicUpdate_LS_HUM objDemographicUpdate = new DemographicUpdate_LS_HUM();
            Continuation conti = (Continuation)DemographicUpdate_LS_HUM.startMemberRequest(caserec.id);
            
            Map<String, HttpRequest> requests = conti.getRequests();
            String reqBody = '{"GetMember":{"Member":{"PlatformPointerList":null,"GetCriteria":{"PersonIdCriteria":{"PersonId":"1055550886","PeriodList":{"Period":["Current","Past","Future"]},"AsOfDate":"2020-05-11"}},"Contact":{"ContactProfile":{"PhoneList":{"PersonPhone":""},"EmailList":{"PersonEmail":""},"AddressList":{"PersonAddress":""}}}}}}';
			HttpRequest request = new HttpRequest();
            request.setBody(reqBody);
            
            HttpResponse response = new HttpResponse();
            response.setBody('{"GetMemberResponse":{"Body":{"MessageList":{"Message":[{"Type":"Information","Source":"OSE Services","Description":"Member Details returned successfully.","Code":"OSI001"}]},"Member":{"DateOfBirth":"10/08/1947","FirstName":"RICHILEE","Gender":"M","LastName":"EDNILAO","MiddleInitial":"E","Ssn":"436293539","Contact":{"ContactProfile":{"EmailList":{"PersonEmail":[{"Address":"HOME@HUMANATEST.COM","Type":"Primary"},{"Address":"WORK@HUMANATEST.COM","Type":"Secondary"}]},"PhoneList":{"PersonPhone":[{"PhoneNumber":"9857587716","Type":"Home"},{"PhoneNumber":"2360274185","Type":"Work"}]},"AddressList":{"PersonAddress":[{"AddressLine1":"4931 TETRICK ROAD","City":"DES ALLEMANDS","StateCode":"LA","County":"ST. CHARLES","ZipCode":"70030","Type":"Home","CountyCode":"089"},{"AddressLine1":"4938 PATRICK ROAD","City":"DES ALLEMANDS","StateCode":"LA","County":"ST. CHARLES","ZipCode":"70030","Type":"Work","CountyCode":"089"}]},"MasterId":"1011599009","PlatformPointerList":{"PlatformProxyKey":[{"Application":"","KeyType":"PERSONID","KeyValue":"041955050","Source":"LV"},{"Application":"","KeyType":"AKANAME","KeyValue":"crissy48","Source":"WPP"},{"Application":"","KeyType":"PERSONID","KeyValue":"0467021707201456","Source":"EM"},{"Application":"","KeyType":"ODS_GENKEY","KeyValue":"0616724172618","Source":"ODS"},{"Application":"","KeyType":"NIEXTERNALSYSTEMENTITYID","KeyValue":"2534986316","Source":"HV"}]}}}}}}}');
        	response.setStatusCode(200);
            response.setStatus('success');
            Test.setContinuationResponse('continuation-1', response);
            Object result = Test.invokeContinuationMethod(objDemographicUpdate, conti);
            DemographicUpdate_LS_HUM.generateBlankResponse();
            List<String> labels = new List<String>();
            labels.add('continuation-1');
            DemographicUpdate_LS_HUM.processMemberResponse(labels);
            
            DemographicMBEPlusResponse_DTO_HUM dtoResponse = new DemographicMBEPlusResponse_DTO_HUM();
            DemographicMBEPlusResponse_DTO_HUM.GetMemberResponse dtoMemberResponse = new DemographicMBEPlusResponse_DTO_HUM.GetMemberResponse();
            DemographicMBEPlusResponse_DTO_HUM.Body dtoBody = new DemographicMBEPlusResponse_DTO_HUM.Body();
            DemographicMBEPlusResponse_DTO_HUM.Member dtoMember = new DemographicMBEPlusResponse_DTO_HUM.Member();
            DemographicMBEPlusResponse_DTO_HUM.MessageList dtoMsgList = new DemographicMBEPlusResponse_DTO_HUM.MessageList();
            DemographicMBEPlusResponse_DTO_HUM.Contact dtoContact = new DemographicMBEPlusResponse_DTO_HUM.Contact();
            DemographicMBEPlusResponse_DTO_HUM.ContactProfile dtoContactProfile = new DemographicMBEPlusResponse_DTO_HUM.ContactProfile();
            DemographicMBEPlusResponse_DTO_HUM.EmailList dtoEmailList = new DemographicMBEPlusResponse_DTO_HUM.EmailList();
            DemographicMBEPlusResponse_DTO_HUM.PhoneList dtoPhoneList = new DemographicMBEPlusResponse_DTO_HUM.PhoneList();
            DemographicMBEPlusResponse_DTO_HUM.AddressList dtoAddressList = new DemographicMBEPlusResponse_DTO_HUM.AddressList();
            DemographicMBEPlusResponse_DTO_HUM.PersonEmail dtoPersonEmail = new DemographicMBEPlusResponse_DTO_HUM.PersonEmail();
            DemographicMBEPlusResponse_DTO_HUM.PersonPhone dtoPersonPhone = new DemographicMBEPlusResponse_DTO_HUM.PersonPhone();
            DemographicMBEPlusResponse_DTO_HUM.PersonAddress dtoPersonAddress = new DemographicMBEPlusResponse_DTO_HUM.PersonAddress();
            
         //   DemographicUpdate_LS_HUM.morphMemberResponse(dtoResponse);
            Test.StopTest();
        }
    } 
    
    /*
    * Method to test failure response for DemographicUpdate
    * @param        None
    * @return       None
    */
    private static testmethod void testFailureDemographicUpdate()
    {
        User testUser = [Select Id,Name from User where LastName='test User1' Limit 1];
        Case caserec=[select id,subject,description,Accountid from case where subject='Test Subject' limit 1];
        string emptystring='';
        system.runAs(testUser)
        { 
            Test.StartTest();
            DemographicUpdate_LS_HUM objDemographicUpdate = new DemographicUpdate_LS_HUM();
            Continuation conti = (Continuation)DemographicUpdate_LS_HUM.startMemberRequest(emptystring);
            
            Map<String, HttpRequest> requests = conti.getRequests();
            String reqBody = '{"GetMember":{"Member":{"PlatformPointerList":null,"GetCriteria":{"PersonIdCriteria":{"PersonId":"1055550886","PeriodList":{"Period":["Current","Past","Future"]},"AsOfDate":"2020-05-11"}},"Contact":{"ContactProfile":{"PhoneList":{"PersonPhone":""},"EmailList":{"PersonEmail":""},"AddressList":{"PersonAddress":""}}}}}}';
			HttpRequest request = new HttpRequest();
            request.setBody(reqBody);
            
            HttpResponse response = new HttpResponse();
            response.setBody('{"GetMemberResponse":""}');
            Test.setContinuationResponse('continuation-1', response);
            Object result = Test.invokeContinuationMethod(objDemographicUpdate, conti);
            DemographicMBEPlusResponse_DTO_HUM.Message dtoMessage = new DemographicMBEPlusResponse_DTO_HUM.Message();
            DemographicMBEPlusResponse_DTO_HUM dtoMessage1 = new DemographicMBEPlusResponse_DTO_HUM();
            DemographicUpdateMBEPlus_DTO_HUM wrapperResponse = new DemographicUpdateMBEPlus_DTO_HUM();
            dtoMessage.Code = 'test code';
            dtoMessage.Description = 'test desc';
            dtoMessage.Source = 'teset soucre';
            dtoMessage.Type = 'test type';
             List<String> labels = new List<String>();
            labels.add('continuation-1');
            DemographicUpdate_LS_HUM.processMemberResponse(labels);
          //  DemographicUpdateMBEPlus_DTO_HUM dtos=DemographicUpdate_LS_HUM.morphMemberResponse(dtoMessage1);
           // system.assertNotEquals(null, dtos);
            Test.StopTest();
        }
    }
    
    
}