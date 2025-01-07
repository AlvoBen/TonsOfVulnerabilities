/***********************************************************************************************************
Apex Class Name  : HUMCustomLinksControllerTest 
Version          : 1.0
Created Date     : April 4 2015
Function         : Test class for HUMCustomLinksController 
************************************************************************************************************

Modification Log:

* Developer Name                Code Review           Date                       Description
*-----------------------------------------------------------------------------------------------------------
* Asha Deepika Koyyana           22136              04/03/2015                Original Version
*Saurabh Lamba                   114940             07/28/2017                REQ - 323535 - Ability of a system to land on Humana Group Medicare listing Page directly 
*Kavya Bondada                                      07/20/2021                  User Story 2482005: T1PRJ0001827 - MF 1 - RESEARCH - Add 'Medicaid Related Resources' section under Additional Resources (RF)
*Swetha Thuti                                      09/08/2023                   User Story 4853184: T1PRJ1117588 CarePlus - Classic CRM - Create a CarePlus section on Additional Resource Widget

************************************************************************************************************/
@isTest
private class HUMCustomLinksControllerTest 
{
    
    /*
    * Method Name   :    createMedicareCustomLinks
    * Description   :    This method is used to Create Medicare Custom Links
    * Return Type   :    List<CustomLink__c >
    * Parameters    :    Integer
    */  
    public static List<CustomLink__c > createMedicareCustomLinks(Integer num)
    {
       String devRecordTypeId = Schema.SObjectType.CustomLink__c.getRecordTypeInfosByName().get('Custom Link').getRecordTypeId();
       List<CustomLink__c > lstCustomLinks = new List<CustomLink__c >();
        
        for(Integer i=0; i<num; i++) 
        {
            CustomLink__c oCustomLink = new CustomLink__c (Name='Resource Center- Group Medicare' + i ,Subsection__c = 'Medicare Related Resources',URL__c='test.salesforce.com',recordTypeId=devRecordTypeId);
            lstCustomLinks.add(oCustomLink );
        }
        insert lstCustomLinks;
        return lstCustomLinks;
    }
    
    /*
    * Method Name   :    createCommercialCustomLinks
    * Description   :    This method is used to Create Commercial Custom Links
    * Return Type   :    List<CustomLink__c >
    * Parameters    :    Integer
    */   
    public static List<CustomLink__c > createCommercialCustomLinks(Integer num)
    {
        List<CustomLink__c > lstCustomLinks = new List<CustomLink__c >();
        
        for(Integer i=0; i<num; i++) 
        {
            CustomLink__c oCustomLink = new CustomLink__c (Name='Resource Center- Group Commercial' + i ,Subsection__c = 'Commercial Related Resources',URL__c='test.salesforce.com');
            lstCustomLinks.add(oCustomLink );
        }
        insert lstCustomLinks;
        return lstCustomLinks;
    }
    
    
    /*
    * Method Name   :    createMedicaidCustomLinks
    * Description   :    This method is used to Create Medicaid Custom Links
    * Return Type   :    List<CustomLink__c >
    * Parameters    :    Integer
    */    
        
    public static List<CustomLink__c > createMedicaidCustomLinks(Integer num)
    {
        List<CustomLink__c > lstCustomLinks = new List<CustomLink__c >();
        
        for(Integer i=0; i<num; i++) 
        {
            CustomLink__c oCustomLink = new CustomLink__c (Name='Resource Center- Group Medicaid' + i ,Subsection__c = 'Medicaid Related Resources',URL__c='test.salesforce.com');
            lstCustomLinks.add(oCustomLink );
        }
        insert lstCustomLinks;
        return lstCustomLinks;
    }
    
    /*
    * Method Name   :    createGeneralCustomLinks
    * Description   :    This method is used to Create General Custom Links
    * Return Type   :    List<CustomLink__c >
    * Parameters    :    Integer
    */   
    public static List<CustomLink__c > createGeneralCustomLinks(Integer num)
    {
        List<CustomLink__c > lstCustomLinks = new List<CustomLink__c >();
        
        for(Integer i=0; i<num; i++) 
        {
            CustomLink__c oCustomLink = new CustomLink__c (Name='Resource Center- Group General' + i ,Subsection__c = 'General Related Resources',URL__c='test.salesforce.com');
            lstCustomLinks.add(oCustomLink );
        }
        insert lstCustomLinks;
        return lstCustomLinks;
    }

           /*
    * Method Name   :    createCarePlusCustomLinks
    * Description   :    This method Is used To Create CarePlus Custom Links
    * Return Type   :    List<CustomLink__c >
    * Parameters    :    Integer
    */   
    Public Static List<CustomLink__c > createCarePlusCustomLinks(Integer num)
    {
        List<CustomLink__c > lstCustomLinks = New List<CustomLink__c >();
        
        For (Integer i= 0; i<num; i++) 
        {
            CustomLink__c oCustomLink = New CustomLink__c(Name ='Resource Center- Group CarePlus' + i ,Subsection__c = 'CarePlus Related Resources',URL__c='test.salesforce.com');
            lstCustomLinks.add(oCustomLink);
        }
        insert lstCustomLinks;
        Return lstCustomLinks;
    }
    
    /*
    * Method Name   :    testOverAll
    * Description   :    This method is used to call HUMCustomLinksControllerTest class
    * Return Type   :    NA
    * Parameters    :    NA
    */
    static testMethod void testOverAll()
    {
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser;
        system.assertNotEquals(oCCSUser, NULL, 'Value not matched');
        
        HUMConstants__c oHUMConstant = new HUMConstants__c(Name= 'HUMConstantHundered', IntegerValue__c =100); 
        insert oHUMConstant;
        System.assertNotEquals(oHUMConstant, NULL, 'Value not matched');
        
        System.runAs(oCCSUser)
        {
            CustomLink__c [] lstMedicareCustomLink =  createMedicareCustomLinks(10);
            System.assertNotEquals(lstMedicareCustomLink.size() , 0);
            
            CustomLink__c [] lstMedicaidCustomLink =  createMedicaidCustomLinks(10);
            System.assertNotEquals(lstMedicaidCustomLink.size() , 0);
            
            CustomLink__c [] lstCommercialLink =  createCommercialCustomLinks(10);
            System.assertNotEquals(lstCommercialLink.size(), 0);
            
            CustomLink__c [] lstGeneralCustomLink =  createGeneralCustomLinks(10);
            System.assertNotEquals(lstGeneralCustomLink.size(),0);

            CustomLink__c [] lstCarePlusCustomLink =  createCarePlusCustomLinks(10);
            System.assertNotEquals(lstCarePlusCustomLink.size(),0);

            
            Test.StartTest();
            
            HUMCustomLinksController oHUMCustomLink = new HUMCustomLinksController();
            oHUMCustomLink.populateSubSections();
            System.assertNotEquals(oHUMCustomLink , NULL, 'Object is not instantiated');
            
            Test.StopTest();
       } 
     }
 }