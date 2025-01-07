/*****************************************************************************************************************************************************************************
    @Apex Class Name  : EligiblityResponse_DTO_T_HUM
    @Version          : 1.0
    @Created Date     : June 24, 2020
    @Function         : Test class for EligiblityResponse_DTO_HUM
******************************************************************************************************************************************************************************
Modification Log:

* Developer Name           Review Number              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Riddhi Mittal                                  24/06/2020                    Original Version
******************************************************************************************************************************************************************************/
@isTest
public class EligiblityResponse_DTO_T_HUM {
    
    /*
    * Method to test EligiblityResponse  structure
    * @param        None
    * @return       None
    */
    Private Static testmethod void TestEligiblityResponseDTOHUM()
    {
       	User usr = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert usr; 
        system.assertEquals(usr.Lastname == 'test User1',true); 
        system.runAs(usr)
        {
            Test.startTest();
            EligiblityResponse_DTO_HUM objEligiblityResponse = new EligiblityResponse_DTO_HUM();
            EligiblityResponse_DTO_HUM.ValidateEligibilityResponse objValidateEligibilityResponse = new EligiblityResponse_DTO_HUM.ValidateEligibilityResponse();
            EligiblityResponse_DTO_HUM.Members objMembers = new EligiblityResponse_DTO_HUM.Members();
            list<EligiblityResponse_DTO_HUM.Members> listMembers = new list<EligiblityResponse_DTO_HUM.Members>() ;
            listMembers.add(objMembers);
            objValidateEligibilityResponse.members = listMembers; 
            EligiblityResponse_DTO_HUM.Eligibilities objEligibilities = new EligiblityResponse_DTO_HUM.Eligibilities();
            EligiblityResponse_DTO_HUM.GroupRecord objGroupRecord = new EligiblityResponse_DTO_HUM.GroupRecord();
            EligiblityResponse_DTO_HUM.EditableDemographicType objEditableDemographicType = new EligiblityResponse_DTO_HUM.EditableDemographicType();
            objEditableDemographicType.mailingAddress = 'Test mailingAddress';
            objEditableDemographicType.residentialAddress = 'Test residentialAddress';
            objEditableDemographicType.workEmail = 'Test workEmail';
            objEditableDemographicType.homeEmail = 'Test homeEmail';
            objEditableDemographicType.homePhone = 'Test homePhone';
            objEditableDemographicType.workPhone = 'Test workPhone';
            objEditableDemographicType.name = 'Test name';
            objEditableDemographicType.gender = 'Test gender';
            objEditableDemographicType.dob = 'Test dob';
            objEditableDemographicType.ssn = 'Test ssn';
            objGroupRecord.groupId = 'Test groupId';
            objGroupRecord.dualDemoIndicator = 'Test dualDemoIndicator';
            objGroupRecord.platformCode = 'Test platformCode';
            objGroupRecord.ediGroupIndicator = 'Test ediGroupIndicator';
            objGroupRecord.exchangeIndicator = 'Test exchangeIndicator';
            objGroupRecord.exchangeType = 'Test exchangeType';
            objGroupRecord.product = 'Test product';
            objGroupRecord.productType = 'Test productType';
            objGroupRecord.productTypeCode = 'Test productTypeCode';
            objGroupRecord.asoIndicator = 'Test asoIndicator';
            objGroupRecord.majorLOB = 'Test majorLOB';
            objEligibilities.groupRecord = objGroupRecord;
            objEligibilities.heirarchy = 'Test heirarchy';
            objEligibilities.template = 'Test template';
            objEligibilities.ruleName = 'Test ruleName';
            objEligibilities.editableDemographicType = objEditableDemographicType;
            objEligibilities.contactHierarchyMessage = 'Test contactHierarchyMessage';
            objEligibilities.criticalHierarchyMessage = 'Test criticalHierarchyMessage';
            objEligibilities.routingMessage = 'Test routingMessage';
            objEligibilities.message = 'Test message';
            objEligibilities.iseligible = 'Test iseligible';
            objEligibilities.error = 'Test error';
            list<EligiblityResponse_DTO_HUM.Eligibilities> listEligibilities = new list<EligiblityResponse_DTO_HUM.Eligibilities>();
            listEligibilities.add(objEligibilities);
            objMembers.eligibilities=listEligibilities;
            objValidateEligibilityResponse.timestamp = 'Test timestamp';
            objValidateEligibilityResponse.result = 'Test result';
            objValidateEligibilityResponse.responseCode = 'Test responseCode';
            objValidateEligibilityResponse.requestid = 'Test requestid';
            list<string> listerror = new list<string>();
	    objValidateEligibilityResponse.errorsource = listerror;            
            objEligiblityResponse.ValidateEligibilityResponse =  objValidateEligibilityResponse;
            system.assertNotEquals(Null, objEligiblityResponse);
           Test.stopTest();
    
            
        }
        
    }

}