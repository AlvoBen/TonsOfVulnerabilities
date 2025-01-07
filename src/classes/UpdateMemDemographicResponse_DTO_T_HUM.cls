/*****************************************************************************************************************************************************************************
    @Apex Class Name  : UpdateMemDemographicResponse_DTO_T_HUM
    @Version          : 1.0
    @Created Date     : June 26, 2020
    @Function         : Test class for UpdateMemberDemographicsResponse_DTO_HUM
******************************************************************************************************************************************************************************
Modification Log:

* Developer Name           Review Number              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Riddhi Mittal                                  26/06/2020                    Original Version
******************************************************************************************************************************************************************************/
@istest
public class UpdateMemDemographicResponse_DTO_T_HUM {
    
    /*
    * Method to test updateDemographicResponseDTO Request structure
    * @param        None
    * @return       None
    */
    Private Static testmethod void TestUpdateMemberDemographicResponse_DTO_T_HUM()
    {
       	User usr = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert usr; 
        system.assertEquals(usr.Lastname == 'test User1',true); 
        system.runAs(usr)
        {
            Test.startTest(); 
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
            system.assertNotEquals(Null, objUpdateMemberDemographicsResponseDTO);
            Test.stopTest();
        }
    }
}