/*****************************************************************************************************************************************************************************
    @Apex Class Name  : DemographicMBEPlusRequest_DTO_T_HUM
    @Version          : 1.0
    @Created Date     : May 13th, 2020
    @Function         : Test class for DemographicMBEPlusRequest_DTO_HUM
******************************************************************************************************************************************************************************
Modification Log:

* Developer Name           Review Number              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Ajay Chakradhar                                  13/05/2020                    Original Version
******************************************************************************************************************************************************************************/
@istest
public class DemographicMBEPlusRequest_DTO_T_HUM 
{
    /*
    * Method to test DemographicMBEPlus Request structure
    * @param        None
    * @return       None
    */
    Private Static testmethod void TestDemographicMBEPlusRequest_DTO_T_HUM()
    {
       	User usr = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert usr; 
        system.assertEquals(usr.Lastname == 'test User1',true); 
        system.runAs(usr)
        {
            Test.startTest();    	
            DemographicMBEPlusRequest_DTO_HUM Demographic_DTO=New DemographicMBEPlusRequest_DTO_HUM();
            DemographicMBEPlusRequest_DTO_HUM.GetMember Demographic_DTORequest=new DemographicMBEPlusRequest_DTO_HUM.GetMember();
            DemographicMBEPlusRequest_DTO_HUM.PhoneList Demographic_DTOPhoneList=New DemographicMBEPlusRequest_DTO_HUM.PhoneList();
            DemographicMBEPlusRequest_DTO_HUM.EmailList Demographic_DTOEmaillist=New DemographicMBEPlusRequest_DTO_HUM.EmailList();
            DemographicMBEPlusRequest_DTO_HUM.AddressList Demographic_DTOAddressesList=New DemographicMBEPlusRequest_DTO_HUM.AddressList();
            DemographicMBEPlusRequest_DTO_HUM.PlatformPointerList Demographic_DTOPointerlist=New DemographicMBEPlusRequest_DTO_HUM.PlatformPointerList();
            
            DemographicMBEPlusRequest_DTO_HUM.PersonIdCriteria Demographic_DTOPersonidcriteria=New DemographicMBEPlusRequest_DTO_HUM.PersonIdCriteria();
            Demographic_DTOPhoneList.PersonPhone='123567890';
            Demographic_DTOEmaillist.PersonEmail='Test@gmail.com';
            Demographic_DTOAddressesList.PersonAddress='Test Addresses';
            Demographic_DTOPointerlist.PlatformProxyKey='Test Proxykey';
            String Asofdate=string.valueof(system.today());
            Demographic_DTOPersonidcriteria.AsOfDate=Asofdate;
            Demographic_DTOPersonidcriteria.PersonId='test id';
            system.assertNotEquals(null, Asofdate); 
            Test.stopTest();    
        }
    }
}