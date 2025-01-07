/*****************************************************************************************************************************************************************************
    @Apex Class Name  : EligiblityRequest_DTO_T_HUM
    @Version          : 1.0
    @Created Date     : June 24th, 2020
    @Function         : Test class for EligiblityRequest_DTO_HUM
******************************************************************************************************************************************************************************
Modification Log:

* Developer Name           Review Number              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Riddhi Mittal                                  24/06/2020                    Original Version
******************************************************************************************************************************************************************************/
@istest
public class EligiblityRequest_DTO_T_HUM {
/*
    * Method to test EligiblityRequest Request structure
    * @param        None
    * @return       None
    */
    Private Static testmethod void TestEligiblityRequest_DTO_T_HUM()
    {
       	User usr = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert usr; 
        system.assertEquals(usr.Lastname == 'test User1',true); 
        system.runAs(usr)
        {
            Test.startTest(); 
            EligiblityRequest_DTO_HUM objEligiblityRequest = new EligiblityRequest_DTO_HUM();
            EligiblityRequest_DTO_HUM.ValidateEligibilityRequest objValidateEligibilityRequest = new EligiblityRequest_DTO_HUM.ValidateEligibilityRequest();
            EligiblityRequest_DTO_HUM.Members objMembers = new EligiblityRequest_DTO_HUM.Members();
            EligiblityRequest_DTO_HUM.Membercriterion objMembercriterion = new EligiblityRequest_DTO_HUM.Membercriterion();
            EligiblityRequest_DTO_HUM.Products objProducts = new EligiblityRequest_DTO_HUM.Products();
            objValidateEligibilityRequest.timestamp = 'Test timestamp';
            objValidateEligibilityRequest.consumer = 'Test consumer';
            objValidateEligibilityRequest.requestid = 'Test requestid';
            objMembercriterion.membersourcepersonid = 'Test membersourcepersonid';
            objMembercriterion.subscribersourcepersonid = 'Test subscribersourcepersonid';
            objMembercriterion.source = 'Test source';
            objMembercriterion.groupid = 'Test groupid';
            objMembercriterion.platform = 'Test platform';
            objProducts.product = 'Test product';
            objProducts.producttype = 'Test producttype';
            objProducts.producttypecode = 'Test producttypecode';
            objProducts.productidentifier = 'Test productidentifier';
            objProducts.majorlob = 'Test majorlob';
            objProducts.dualdemoindicator = 'True' ;
            objProducts.asoindicator = 'True';
            list<EligiblityRequest_DTO_HUM.products> listProducts = new list<EligiblityRequest_DTO_HUM.products>();
            listProducts.add(objProducts);
            objMembercriterion.products = listProducts;
            list<EligiblityRequest_DTO_HUM.Membercriterion> listMembercriterion = new list<EligiblityRequest_DTO_HUM.Membercriterion>();
            listMembercriterion.add(objMembercriterion);
            objMembers.membercriterion = listMembercriterion;
            list<EligiblityRequest_DTO_HUM.Members> listMembers = new list<EligiblityRequest_DTO_HUM.Members>();
            listMembers.add(objMembers);
	    objValidateEligibilityRequest.members = listMembers;
            objEligiblityRequest.ValidateEligibilityRequest = objValidateEligibilityRequest;
            Test.stopTest();
            
            
            
        }
        
    }
    
}