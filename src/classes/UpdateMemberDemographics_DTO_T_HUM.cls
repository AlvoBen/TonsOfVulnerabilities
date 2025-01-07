/*****************************************************************************************************************************************************************************
    @Apex Class Name  : UpdateMemberDemographics_DTO_T_HUM
    @Version          : 1.0
    @Created Date     : June 26th, 2020
    @Function         : Test class for UpdateMemberDemographics_DTO_HUM
******************************************************************************************************************************************************************************
Modification Log:

* Developer Name           Review Number              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Riddhi Mittal                                  26/06/2020                    Original Version
******************************************************************************************************************************************************************************/
@isTest
public class UpdateMemberDemographics_DTO_T_HUM {
    
    /*
    * Method to test UpdateMemberDemographics_DTO_T_HUM Request structure
    * @param        None
    * @return       None
    */
    Private Static testmethod void TestUpdateMemberDemographic_DTO_T_HUM()
    {
       	User usr = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert usr; 
        system.assertEquals(usr.Lastname == 'test User1',true); 
        system.runAs(usr)
        {
            Test.startTest();
			UpdateMemberDemographics_DTO_HUM objUpdateMemberDemographics_DTO_HUM = new UpdateMemberDemographics_DTO_HUM();
			objUpdateMemberDemographics_DTO_HUM.serviceCalloutError = 'Test serviceCalloutError'; 
            objUpdateMemberDemographics_DTO_HUM.calloutErrored = TRUE;
            objUpdateMemberDemographics_DTO_HUM.updateRequestDTO = 'Test updateRequestDTO';
            
            UpdateMemberDemographicsResponse_DTO_HUM objUpdateMemberDemographicsResponseDTO = new UpdateMemberDemographicsResponse_DTO_HUM();
            UpdateMemberDemographicsResponse_DTO_HUM.UpdateMemberResponse objUpdateMemberResponse = new  UpdateMemberDemographicsResponse_DTO_HUM.UpdateMemberResponse();
            UpdateMemberDemographicsResponse_DTO_HUM.Members objMembers = new UpdateMemberDemographicsResponse_DTO_HUM.Members();
            UpdateMemberDemographicsResponse_DTO_HUM.MemberCriterion objMemberCriterion = new UpdateMemberDemographicsResponse_DTO_HUM.MemberCriterion();
            objMemberCriterion.membersourcepersonid = 'Test membersourcepersonid';
            objMemberCriterion.subscribersourcepersonid = 'Test subscribersourcepersonid';
            objMemberCriterion.platform = 'Test platform';
			List<UpdateMemberDemographicsResponse_DTO_HUM.UpdateStatus> lstUpdateStatus = new List<UpdateMemberDemographicsResponse_DTO_HUM.UpdateStatus>();
            UpdateMemberDemographicsResponse_DTO_HUM.UpdateStatus objUpdateStatus = new UpdateMemberDemographicsResponse_DTO_HUM.UpdateStatus();
            objUpdateStatus.requesttype ='test request';
            objUpdateStatus.status= 'failure';
            objUpdateStatus.errorcode = '202';
            objUpdateStatus.errormessage = 'error';
            lstUpdateStatus.add(objUpdateStatus);
            objMemberCriterion.updatestatus = lstUpdateStatus;
            list<String> listsystemsupdated = new list<String>();
            listsystemsupdated.add('a');
            objMembers.systemsupdated = listsystemsupdated;
            objMembers.cdmmsgack = 'Test cdmmsgack';
            List<UpdateMemberDemographicsResponse_DTO_HUM.MemberCriterion> listUpdateMemberDemographicsResponse_DTO_HUM = new List<UpdateMemberDemographicsResponse_DTO_HUM.MemberCriterion>();
            listUpdateMemberDemographicsResponse_DTO_HUM.add(objMemberCriterion);
            objMembers.membercriterion= listUpdateMemberDemographicsResponse_DTO_HUM;
            objUpdateMemberResponse.timestamp = 'Test timestamp';
            objUpdateMemberResponse.result = 'Test result';
            objUpdateMemberResponse.responseCode = 'Test responseCode';
            objUpdateMemberResponse.requestid = 'Test requestid';
            objUpdateMemberResponse.errormessage = 'Test errormessage';
            List<String> listerrorsource = new List<String>();
            listerrorsource.add('a');
            objUpdateMemberResponse.errorsource = listerrorsource;
            list<UpdateMemberDemographicsResponse_DTO_HUM.Members> listMembers = new list<UpdateMemberDemographicsResponse_DTO_HUM.Members>();
            listMembers.add(objMembers);
            objUpdateMemberResponse.members = listMembers;
            objUpdateMemberDemographicsResponseDTO.UpdateMemberResponse = objUpdateMemberResponse;
            
            objUpdateMemberDemographics_DTO_HUM.updateResponseDTO = objUpdateMemberDemographicsResponseDTO;
             String jsonString = '{"Fault":{"faultcode":"Code-127.1.1","faultstring":"testString","detail":{"transactionInfo":{"transaction-id":"1655241399","transaction-client":"10.232.9.94","URL-in":"http://127.0.0.1:8316/OSAServiceREST/isOSA","processor-name":"JSONServicesGateway","processor-type":"MultiprotocolGateway"},"errorInfo":{"error-code":"0x00d30003","error-message":"InternalServerError","error-subcode":"0x00d30003","error_headers":"0x00d30003","error_protocol_reason_phrase":"0x00d3000","error_protocol_response":"0x00d3000"}}}}';
            objUpdateMemberDemographics_DTO_HUM.faultResponseDTO = FaultCode_DTO_HUM.parse(jsonString);
            System.assertNotEquals(null, objUpdateMemberDemographics_DTO_HUM);
            
            Test.stopTest();
        }
    }
            
            
}