/*******************************************************************************************************************************
Apex Class Name : Lib_Security_LT_HUm
Version         : 1.0
Created On      : 12/15/2021
Function        : This class is used for Test the Lib_Security_LH_HUm.

Modification Log: 
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*    1.0           Ritik Agarwal                                      12/15/2021               Original Version
*/
@isTest
public with Sharing class Lib_Security_LT_HUm {
    /*
* Method Name : testRecordUpsert
* Description : used to test upsert permission
* Return type : void
* Parameter   : NA
*/
    static @isTest void testRecordUpsert()
    {
        String caseJson = '{"Origin":"Inbound Call","Owner_Queue__c":"Humana Pharmacy Clinical Services","Type":"Customer Inquiry","Case_Owner__c":"Juan Acevedo Jr","Subtype__c":null,"Status":"In Progress","Interacting_With_Type__c":null,"Interacting_About_Type__c":"Member","Interacting_With__c":"","Interacting_With_Name__c":"","Member_Plan_Id__c":"","Priority":"Normal","OpenEnrollment__c":false,"Topic__c":null,"OpenEnrollmentType__c":null,"G_A_Rights_Given__c":null,"Complaint__c":null,"G_A_Reason__c":null,"Complaint_Reason__c":null,"Complaint_Type__c":null,"Language_Preference__c":null,"Classification_Type__c":"HP Clinical Services","RecordTypeId":"0120G000001QExrQAG","AccountId":"0012h00000f9VJEAA2"}';
        Case objCase  = (Case) System.JSON.deserialize(caseJson, Case.class);
        Lib_Security_LH_HUm.assertRead(objCase);
        Lib_Security_LH_HUm.assertUpsert(objCase);
    }
    
    /*
* Method Name : testRecordCreate
* Description : used to test create permission
* Return type : void
* Parameter   : NA
*/
    static @isTest void testRecordCreate()
    {
        String caseJson = '{"Origin":"Inbound Call","Owner_Queue__c":"Humana Pharmacy Clinical Services","Type":"Customer Inquiry","Case_Owner__c":"Juan Acevedo Jr","Subtype__c":null,"Status":"In Progress","Interacting_With_Type__c":null,"Interacting_About_Type__c":"Member","Interacting_With__c":"","Interacting_With_Name__c":"","Member_Plan_Id__c":"","Priority":"Normal","OpenEnrollment__c":false,"Topic__c":null,"OpenEnrollmentType__c":null,"G_A_Rights_Given__c":null,"Complaint__c":null,"G_A_Reason__c":null,"Complaint_Reason__c":null,"Complaint_Type__c":null,"Language_Preference__c":null,"Classification_Type__c":"HP Clinical Services","RecordTypeId":"0120G000001QExrQAG","AccountId":"0012h00000f9VJEAA2"}';
        Case objCase  = (Case) System.JSON.deserialize(caseJson, Case.class);
        Lib_Security_LH_HUm.assertCreate(objCase);
    }
    
    /*
* Method Name : testFlaseAccess
* Description : used to test no record permission
* Return type : void
* Parameter   : NA
*/
    static @isTest void testFlaseAccess()
    {
        List<Case> objCase  = new List<case>();
        try{
            Lib_Security_LH_HUm.assertCreate(objCase);
        }catch(Exception ex){
            System.assertEquals(true, ex!=null);
            System.assertEquals(true, ex.getMessage().contains('No records to found to check access'));
        }
    }
    
    /*
* Method Name : testfieldAccess
* Description : used to test no record permission
* Return type : void
* Parameter   : NA
*/
    static @isTest void testfieldAccess()
    {
        // Setup test data
        // Create a unique UserName
        String uniqueUserName = 'standarduser' + DateTime.now().getTime() + '@testorg.com';
        // This code runs as the system user
        Profile p = [SELECT Id FROM Profile WHERE Name='Humana Pharmacy Specialist'];
        User u = new User(Alias = 'standt', Email='standarduser@testorg.com',
                          EmailEncodingKey='UTF-8', LastName='Testing', LanguageLocaleKey='en_US',
                          LocaleSidKey='en_US', ProfileId = p.Id,
                          TimeZoneSidKey='America/Los_Angeles',
                          UserName=uniqueUserName);
        System.runAs(u) {
            String caseJson = '{"Origin":"Inbound Call","Owner_Queue__c":"Humana Pharmacy Clinical Services","Type":"Customer Inquiry","Case_Owner__c":"Juan Acevedo Jr","Subtype__c":null,"Status":"In Progress","Interacting_With_Type__c":null,"Interacting_About_Type__c":"Member","Interacting_With__c":"","Interacting_With_Name__c":"","Member_Plan_Id__c":"","Priority":"Normal","OpenEnrollment__c":false,"Topic__c":null,"OpenEnrollmentType__c":null,"G_A_Rights_Given__c":null,"Complaint__c":null,"G_A_Reason__c":null,"Complaint_Reason__c":null,"Complaint_Type__c":null,"Language_Preference__c":null,"Classification_Type__c":"HP Clinical Services","RecordTypeId":"0120G000001QExrQAG","AccountId":"0012h00000f9VJEAA2"}';
            
            try{
                Case objCase  = (Case) System.JSON.deserialize(caseJson, Case.class);
                Lib_Security_LH_HUm.assertUpsert(objCase);
            }catch(Exception ex){
                System.assertEquals(true, ex!=null);
                System.assertEquals(true, ex.getMessage().contains('access missing on fields'));
            }
        }
    }
}