/*****************************************************************************************************************************************************************************
    @Apex Class Name  : DemographicUpdateMBEPlus_DTO_T_HUM
    @Version          : 1.0
    @Created Date     : May 14th, 2020
    @Function         : Test class for DemographicUpdateMBEPlus_DTO_HUM
******************************************************************************************************************************************************************************
Modification Log:

* Developer Name           Review Number              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Ajay Chakradhar                                  14/05/2020                    Original Version
******************************************************************************************************************************************************************************/
@isTest
public class DemographicUpdateMBEPlus_DTO_T_HUM {
    /*
    * Method to test DemographicMBEPlus DTO structure
    * @param        None
    * @return       None
    */
    Private Static testmethod void TestDemographicMBEPlusDTO()
    {
       	User usr = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert usr; 
        system.assertEquals(usr.Lastname == 'test User1',true); 
        system.runAs(usr)
        {
            Test.startTest();
            DemographicUpdateMBEPlus_DTO_HUM dtoDemographicUpdateMBEPlus= new DemographicUpdateMBEPlus_DTO_HUM();
            	dtoDemographicUpdateMBEPlus.memberCalloutError = 'Error';
            	dtoDemographicUpdateMBEPlus.memberCalloutErrored = false;
            
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
            DemographicMBEPlusResponse_DTO_HUM.Message dtoMessage = new DemographicMBEPlusResponse_DTO_HUM.Message();
            List<DemographicMBEPlusResponse_DTO_HUM.Message> lstMessage = new List<DemographicMBEPlusResponse_DTO_HUM.Message>();
            dtoMessage.Code = 'test code';
            dtoMessage.Description = 'test desc';
            dtoMessage.Source = 'teset soucre';
            dtoMessage.Type = 'test type';
            lstMessage.add(dtoMessage);
            
            List<DemographicMBEPlusResponse_DTO_HUM.PersonEmail> dtoPersonEmailList = new List<DemographicMBEPlusResponse_DTO_HUM.PersonEmail>();
            dtoPersonEmail.Address = 'test addr';
            dtoPersonEmail.Type = 'test type';
            dtoPersonEmailList.add(dtoPersonEmail);
            
            List<DemographicMBEPlusResponse_DTO_HUM.PersonPhone> dtoPersonPhoneList = new List<DemographicMBEPlusResponse_DTO_HUM.PersonPhone>();
            dtoPersonPhone.PhoneNumber = '9870876876';
            dtoPersonPhone.Type = 'test tyep';
            dtoPersonPhoneList.add(dtoPersonPhone);
            
            List<DemographicMBEPlusResponse_DTO_HUM.PersonAddress> dtoPersonAddressList = new List<DemographicMBEPlusResponse_DTO_HUM.PersonAddress>();
            dtoPersonAddress.AddressLine1 = 'test line2';
            dtoPersonAddress.City = 'texas';
            dtoPersonAddress.County = 'usa';
            dtoPersonAddress.CountyCode = 'tx';
            dtoPersonAddress.StateCode = '3487';
            dtoPersonAddress.Type = 'test';
            dtoPersonAddress.ZipCode = '9659';
            dtoPersonAddressList.add(dtoPersonAddress);
            
            dtoMember.FirstName = 'test1';
            dtoMember.LastName = 'test2';
            dtoMember.DateOfBirth = '090990';
            dtoMember.Gender = 'M';
            dtoMember.MiddleInitial = 'M';
            dtoMember.Ssn = 'test';
            dtoMember.MasterId = 'test';
            dtoPhoneList.PersonPhone = dtoPersonPhoneList;
            dtoAddressList.PersonAddress = dtoPersonAddressList;
            dtoEmailList.PersonEmail = dtoPersonEmailList;
            dtoContactProfile.PhoneList = dtoPhoneList;
            dtoContactProfile.EmailList = dtoEmailList;
            dtoContactProfile.AddressList = dtoAddressList;
            dtoContact.ContactProfile = dtoContactProfile;
            dtoMember.Contact = dtoContact;
            dtoMsgList.Message = lstMessage; 
            
            dtoBody.Member = dtoMember;
            dtoBody.MessageList  = dtoMsgList;
            dtoMemberResponse.Body = dtoBody;
            dtoResponse.GetMemberResponse = dtoMemberResponse;
            	dtoDemographicUpdateMBEPlus.memberResponseDTO  = dtoResponse;
            String jsonString = '{"Fault":{"faultcode":"Code-127.1.1","faultstring":"testString","detail":{"transactionInfo":{"transaction-id":"1655241399","transaction-client":"10.232.9.94","URL-in":"http://127.0.0.1:8316/OSAServiceREST/isOSA","processor-name":"JSONServicesGateway","processor-type":"MultiprotocolGateway"},"errorInfo":{"error-code":"0x00d30003","error-message":"InternalServerError","error-subcode":"0x00d30003","error_headers":"0x00d30003","error_protocol_reason_phrase":"0x00d3000","error_protocol_response":"0x00d3000"}}}}';
            	dtoDemographicUpdateMBEPlus.faultResponseDTO = FaultCode_DTO_HUM.parse(jsonString);
            system.assertNotEquals(null, dtoDemographicUpdateMBEPlus);
            Test.stopTest();    
        }
    }
}