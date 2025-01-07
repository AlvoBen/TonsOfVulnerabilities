/*****************************************************************************************************************************************************************************
    @Apex Class Name  : DemographicUpdateTemplate_DTO_T_HUM
    @Version          : 1.0
    @Created Date     : June 26th, 2020
    @Function         : Test class for DemographicUpdateTemplate_DTO_HUM
******************************************************************************************************************************************************************************
Modification Log:

* Developer Name           Review Number              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Riddhi Mittal                                  26/06/2020                    Original Version
******************************************************************************************************************************************************************************/
@istest
public class DemographicUpdateTemplate_DTO_T_HUM {
 /*
    * Method to test DemographicUpdateTemplateDTO Request structure
    * @param        None
    * @return       None
    */
    Private Static testmethod void TestDemographicUpdateTemplate_DTO_T_HUM()
    {
       	User usr = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert usr; 
        system.assertEquals(usr.Lastname == 'test User1',true); 
        system.runAs(usr)
        {
            Test.startTest();    	
          DemographicUpdateTemplate_DTO_HUM objDemographicUpdateTemplate_DTO_HUM = new DemographicUpdateTemplate_DTO_HUM();
            DemographicUpdateTemplate_DTO_HUM.TemplateBase objTemplateBase = new DemographicUpdateTemplate_DTO_HUM.TemplateBase();
             DemographicUpdateTemplate_DTO_HUM.TemplateData objTemplateData = new DemographicUpdateTemplate_DTO_HUM.TemplateData();
             DemographicUpdateTemplate_DTO_HUM.MedicareContact objMedicareContact = new DemographicUpdateTemplate_DTO_HUM.MedicareContact();
             DemographicUpdateTemplate_DTO_HUM.MedicareCritical objMedicareCritical = new DemographicUpdateTemplate_DTO_HUM.MedicareCritical();
             DemographicUpdateTemplate_DTO_HUM.MedicareAVF objMedicareAVF = new DemographicUpdateTemplate_DTO_HUM.MedicareAVF();
             DemographicUpdateTemplate_DTO_HUM.MedicaidAVF objMedicaidAVF = new DemographicUpdateTemplate_DTO_HUM.MedicaidAVF();
            DemographicUpdateTemplate_DTO_HUM.CommercialCritical objCommercialCritical = new DemographicUpdateTemplate_DTO_HUM.CommercialCritical();
            DemographicUpdateTemplate_DTO_HUM.CommercialContact objCommercialContact = new DemographicUpdateTemplate_DTO_HUM.CommercialContact();
            
            objCommercialContact.PermanentResidentialAddress = 'Test PermanentResidentialAddress';
             objCommercialContact.PermanentResidentialCityName = 'Test PermanentResidentialCityName';
            objCommercialContact.PermanentResidentialStateCode = 'Test PermanentResidentialStateCode';
            objCommercialContact.PermanentResidentialZipCode = 'Test PermanentResidentialZipCode';
            objCommercialContact.MailingAddress = 'Test MailingAddress';
            objCommercialContact.MailingCityName = 'Test MailingCityName';
            objCommercialContact.MailingStateCode = 'Test MailingStateCode';
            objCommercialContact.MailingZipCode = 'Test MailingZipCode';
            objCommercialContact.HomeEmail = 'Test HomeEmail';
            objCommercialContact.WorkEmail = 'Test WorkEmail';
            objCommercialContact.HomePhone = 'Test HomePhone';
            objCommercialContact.Mobile = 'Test Mobile';
            objCommercialContact.WorkPhone = 'Test WorkPhone';
            objCommercialContact.WorkPhoneExt = 'Test WorkPhoneExt';
            
             objCommercialCritical.FirstName = 'Test FirstName';
            objCommercialCritical.MiddleInitial = 'Test MiddleInitial';
            objCommercialCritical.LastName = 'Test LastName';
            objCommercialCritical.Gender = 'Test Gender';
            objCommercialCritical.DateOfBirth = 'Test DateOfBirth';
            objCommercialCritical.SSN = 'Test SSN';
            
            objMedicaidAVF.PermanentResidentialAddress = 'Test PermanentResidentialAddress';
            objMedicaidAVF.PermanentResidentialCityName = 'Test PermanentResidentialCityName';
            objMedicaidAVF.PermanentResidentialStateCode = 'Test PermanentResidentialStateCode';
            objMedicaidAVF.PermanentResidentialZipCode = 'Test PermanentResidentialZipCode';
            objMedicaidAVF.PermanentResidentialCountyName = 'Test PermanentResidentialCountyName';
            objMedicaidAVF.DoesMemberHaveMailAddressSameAsPermAddress = 'Test DoesMemberHaveMailAddressSameAsPermAddress';
            objMedicaidAVF.MailingAddress = 'Test MailingAddress';
            objMedicaidAVF.MailingCityName = 'Test MailingCityName';
            objMedicaidAVF.MailingStateCode = 'Test MailingStateCode';
            objMedicaidAVF.MailingZipCode = 'Test MailingZipCode';
            objMedicaidAVF.MailingCounty = 'Test MailingCounty';
            
            objMedicareAVF.PersonSpeakingWith = 'Test PersonSpeakingWith';
            objMedicareAVF.RelationToMember = 'Test RelationToMember';
            objMedicareAVF.PermanentResidentialPhoneNumber = 'Test PermanentResidentialPhoneNumber';
            objMedicareAVF.WhatAddressMemberCallingToUpdate = 'Test WhatAddressMemberCallingToUpdate';
            objMedicareAVF.PermanentResidentialAddress = 'Test PermanentResidentialAddress';
            objMedicareAVF.PermanentResidentialCityName = 'Test PermanentResidentialCityName';
            objMedicareAVF.PermanentResidentialStateCode = 'Test PermanentResidentialStateCode';
            objMedicareAVF.PermanentResidentialZipCode = 'Test PermanentResidentialZipCode';
            objMedicareAVF.PermanentResidentialCountyName = 'Test PermanentResidentialCountyName';
            objMedicareAVF.PermanentResidentialStartDate = 'Test PermanentResidentialStartDate';
            objMedicareAVF.DoesMemberHaveMailAddressDiffFromPermAddress = 'Test DoesMemberHaveMailAddressDiffFromPermAddress';
            objMedicareAVF.DoesResMemberHaveTemporaryAddress = 'Test DoesResMemberHaveTemporaryAddress';
            objMedicareAVF.DoesMemberWantMailSentToTemporaryAddress = 'Test DoesMemberWantMailSentToTemporaryAddress';
            objMedicareAVF.DoesMemberWantMailAddressDiffFromResAddress = 'Test DoesMemberWantMailAddressDiffFromResAddress';
            objMedicareAVF.MailingAddress = 'Test MailingAddress';
             objMedicareAVF.MailingCityName = 'Test MailingCityName';
             objMedicareAVF.MailingStateCode = 'Test MailingStateCode';
             objMedicareAVF.MailingCounty = 'Test MailingCounty';
             objMedicareAVF.MailingZipCode = 'Test MailingZipCode';
             objMedicareAVF.DoesMailMemberHaveTemporaryAddress = 'Test DoesMailMemberHaveTemporaryAddress';
             objMedicareAVF.DoesMemberHaveResAddressDiffFromMailAddress = 'Test DoesMemberHaveResAddressDiffFromMailAddress';
             objMedicareAVF.TemporaryAddress = 'Test TemporaryAddress';
             objMedicareAVF.TemporaryCityName = 'Test TemporaryCityName';
             objMedicareAVF.TemporaryStateCode = 'Test TemporaryStateCode';
             objMedicareAVF.TemporaryCountyName = 'Test TemporaryCountyName';
             objMedicareAVF.TemporaryZipCode = 'Test TemporaryZipCode';
             objMedicareAVF.TemporaryOSAStartDate = 'Test TemporaryOSAStartDate';
             objMedicareAVF.TemporaryOSAEndDate = 'Test TemporaryOSAEndDate';
            
            objMedicareCritical.FirstName = 'Test FirstName';
             objMedicareCritical.MiddleInitial = 'Test MiddleInitial';
             objMedicareCritical.LastName = 'Test LastName';
             objMedicareCritical.Gender = 'Test Gender';
             objMedicareCritical.DateOfBirth = 'Test DateOfBirth';
             objMedicareCritical.SSN = 'Test SSN';
            
            objMedicareContact.HomeEmail = 'Test HomeEmail';
            objMedicareContact.HomePhone = 'Test HomePhone';
            objMedicareContact.Mobile = 'Test Mobile';
            objMedicareContact.WorkEmail = 'Test WorkEmail';
            objMedicareContact.WorkPhone = 'Test WorkPhone';
            objMedicareContact.WorkPhoneExt = 'Test WorkPhoneExt';
            
            objTemplateData.rsoCOD = objMedicareContact;
            objTemplateData.rsoCRD = objMedicareCritical;
            objTemplateData.rsoMAU = objMedicareAVF;
            objTemplateData.rsoMDU = objMedicaidAVF;
            objTemplateData.gboCOD = objCommercialContact;
            objTemplateData.gboCRD = objCommercialCritical;
            
            objTemplateBase.data = objTemplateData;
             objTemplateBase.key = 'Test key';
             objTemplateBase.templateName = 'Test templateName';

            Test.stopTest();    
        }
    }
}