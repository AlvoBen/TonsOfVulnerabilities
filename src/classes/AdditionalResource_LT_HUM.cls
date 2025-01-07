/*****************************************************************************************
Apex class Name    : AdditionalResource_LT_HUM
Version            : 1.0
Function           : Test class for AdditionalResource_LC_HUM

Modification Log:
 *   Developer                   Code Review             Date               Description
 * ------------------------------------------------------------------------------------------------------------------------------   
 *   Aishwarya Pawar                                     09/26/2022          REQ- 3742726-T1PRJ0170850- MF19876 - Lightning- Additional Resources -Utility Bar
 
********************************************************************************************************************************/

@isTest(seeAllData= false)
public class AdditionalResource_LT_HUM {
    /*
* Method Name : setUpTestData
* Description : This method creates test data for test class
* Return type : None
* Parameter   : None
*/
    
    @testSetup static void setUpTestData() {
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser;
        system.assertNotEquals(oCCSUser, NULL, 'Value not matched');
        
        HUMConstants__c oHUMConstant = new HUMConstants__c(Name= 'HUMConstantHundered', IntegerValue__c =100); 
        insert oHUMConstant;
        System.assertNotEquals(oHUMConstant, NULL, 'Value not matched');
        
        List<CustomLink__c > lstCustomLinks = new List<CustomLink__c >();
        Id customlinkid = schema.SObjectType.CustomLink__c.getRecordTypeInfosByName().get('Custom Link').getRecordTypeId();
        for(Integer i=0; i<10; i++) 
        {
            CustomLink__c oMedicareCustomLink = new CustomLink__c (Name='Resource Center- Group Medicare' + i ,Subsection__c = 'Medicare Related Resources',URL__c='test.salesforce.com',RecordTypeId=customlinkid);
            CustomLink__c oCommercialCustomLink = new CustomLink__c (Name='Resource Center- Group Commercial' + i ,Subsection__c = 'Commercial Related Resources',URL__c='test.salesforce.com',RecordTypeId=customlinkid);
            CustomLink__c ogMedicaidCustomLink = new CustomLink__c (Name='Resource Center- Group Medicaid' + i ,Subsection__c = 'Medicaid Related Resources',URL__c='test.salesforce.com',RecordTypeId=customlinkid);
            CustomLink__c ogMedicareCustomLink = new CustomLink__c (Name='Resource Center- Group General' + i ,Subsection__c = 'General Related Resources',URL__c='test.salesforce.com',RecordTypeId=customlinkid);
            lstCustomLinks.add(oMedicareCustomLink);
            lstCustomLinks.add(oCommercialCustomLink);
            lstCustomLinks.add(ogMedicaidCustomLink);
            lstCustomLinks.add(ogMedicareCustomLink);
        }
        insert lstCustomLinks;
    }
    
    /*
* Method Name   :    testGetLinks
* Description   :    This method is used to test AdditionalResource_LC_HUM -getList class
* Return Type   :    NA
* Parameters    :    NA
*/
    static testMethod void testGetLinks()
    {
        User oCCSUser =  [Select Id,Name,lastname from User where LastName='test User1'];
        System.runAs(oCCSUser)
        {
            Test.StartTest();
            AdditionalResource_LC_HUM additionalResource= new AdditionalResource_LC_HUM();
            List<CustomLink__c> oHUMCustomLink= AdditionalResource_LC_HUM.getLinks();
            system.assertNotEquals(0, oHUMCustomLink.size());
            Test.StopTest();
        } 
    }
    
    static testMethod void testGetLinksException()
    {
        User oCCSUser =  [Select Id,Name,lastname from User where LastName='test User1'];
        HUMConstants__c constvalue = HUMConstants__c.getValues('HUMConstantHundered');
        constvalue.IntegerValue__c = null;
        update constvalue;
        System.runAs(oCCSUser)
        {
            Test.StartTest();
            try{
                List<CustomLink__c> oHUMCustomLink= AdditionalResource_LC_HUM.getLinks();
                system.assertNotEquals(0, oHUMCustomLink.size());
            }catch(exception ex){
                system.assertNotEquals('Error', ex.getMessage());
            }
            Test.StopTest();
        } 
    }

    @isTest
    static void testGetSections() {
        User oCCSUser =  [Select Id,Name,lastname from User where LastName='test User1'];
        System.runAs(oCCSUser)
        {
            Test.StartTest();
            AdditionalResource_LC_HUM additionalResource= new AdditionalResource_LC_HUM();
            List<String> oHUMCustomLinkSections= AdditionalResource_LC_HUM.getSections();
            system.assertNotEquals(0, oHUMCustomLinkSections.size());
            Test.StopTest();
        } 
    }
}