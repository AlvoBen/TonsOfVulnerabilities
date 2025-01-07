/************************************************************************************************************************
Apex Class Name  : HUMAVFTemplateRequestHelperTest
Version          : 1.0
Created Date     : May 8 2015
Function         : Test class for HUMAVFTemplateRequestHelper
*************************************************************************************************************************
Modification Log:
* Developer Name           Review Number              Date                       Description
*------------------------------------------------------------------------------------------------------------------------
* Sharan Shanmugam          23024                   05/08/2015                   Original Version
* Sharan Shanmugam          23048              		05/27/2015                   Defect 184881.0001 - Updated Long to String.
*************************************************************************************************************************/
@isTest
private class HUMAVFTemplateRequestHelperTest
{        
   /*    
    * Method Name : testHUMAVFTemplateRequestHelper    
    * Description : Test method for the request helper  
    * Return type : void    
    * Paramater   : NA    
    */    
    static testMethod void testHUMAVFTemplateRequestHelper()    
    {
        User u = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert u;
        System.assert(u.Id != null);
        system.runAs(u) 
        {
            Test.startTest();
            HUMAVFTemplateRequestHelper avfTempReqHelp = new HUMAVFTemplateRequestHelper();
            avfTempReqHelp.CaseNumber = '1234';
            avfTempReqHelp.EditSequenceNumber = '1234';
            avfTempReqHelp.HumanaAssociateName = '1234';
            avfTempReqHelp.PersonFirstName = '1234';
            avfTempReqHelp.PersonLastName = '1234';
            avfTempReqHelp.LastSaveDate = '1234';
            avfTempReqHelp.MemberUMID = '1234';
            avfTempReqHelp.MedicareId = '1234';
            avfTempReqHelp.EnterprisePersonID = '1234';
            avfTempReqHelp.CMSMAPlanNumber = '1234';
            avfTempReqHelp.CMSPDPPlanNumber = '1234';
            avfTempReqHelp.PersonSpeakingWith = '1234';
            avfTempReqHelp.RelationToMember = '1234';
            avfTempReqHelp.PermanentResidentialAddress = '1234';
            avfTempReqHelp.PermanentResidentialCityName = '1234';
            avfTempReqHelp.PermanentResidentialCountyName = '1234';
            avfTempReqHelp.PermanentResidentialStateCode = '1234';
            avfTempReqHelp.PermanentResidentialZipCode = '1234';
            avfTempReqHelp.PermanentResidentialZipPlusCode = '1234';
            avfTempReqHelp.PermanentResidentialPhoneNumber = '1234';
            avfTempReqHelp.PermanentResidentialStartDate = '1234';
            avfTempReqHelp.MailingAddress = '1234';
            avfTempReqHelp.MailingCityName = '1234';
            avfTempReqHelp.MailingStateCode = '1234';
            avfTempReqHelp.MailingZipCode = '1234';
            avfTempReqHelp.MailingZipPlusCode = '1234';
            avfTempReqHelp.TemporaryAddress = '1234';
            avfTempReqHelp.TemporaryCityName = '1234';
            avfTempReqHelp.TemporaryCountyName = '1234';
            avfTempReqHelp.TemporaryStateCode = '1234';
            avfTempReqHelp.TemporaryZipCode = '1234';
            avfTempReqHelp.TemporaryZipPlusCode = '1234';
            avfTempReqHelp.TemporaryOSAStartDate = '1234';
            avfTempReqHelp.TemporaryOSAEndDate = '1234';
            avfTempReqHelp.E2ETrackID = '1234';
            avfTempReqHelp.E2EActivityID = '1234';
            avfTempReqHelp.E2EMilestoneID = '1234';
            System.assert(avfTempReqHelp.E2EMilestoneID != null);
            Test.stopTest();
        }
    }
}