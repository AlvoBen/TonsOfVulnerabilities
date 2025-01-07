/*******************************************************************************************************************************
Apex Class Name : GenericCustomSettingValue_LT_HUM
Version         : 1.0
Created On      : 07/26/2023
Function        : Test class for GenericCustomSettingValue_LC_HUM
                 
Modification Log: 
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*   1.0            Nirmal Garg                                   	   07/26/2023		          Original version
*******************************************************************************************************************************************************************/
@isTest
public class GenericCustomSettingValue_LT_HUM {
    @TestSetup
    static void makeData(){
        User objUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');   
        insert objUser;
        system.runAs(objUser)
        {
            CRMFunctionality_ONOFF_Switch__c objSwitch1 = new CRMFunctionality_ONOFF_Switch__c();
            objSwitch1.IsON__c = true;
            objSwitch1.Name = '754843';
            insert objSwitch1;
            
            HUM_Webservice_Callout__c mbeServiceRecord =new HUM_Webservice_Callout__c(Name='InquirySummaryService',Timeout__c='12000',Request_Type__c='POST', Active__c=true, Certificate_Name__c = 'Humana_GuidanceCare_HlthCldCop', 
                                                                                      Content_Type__c = 'application/json',End_Point_URL__c = 'test_End_Point');
            insert mbeServiceRecord;
            
            HUMCertificateURL__c MentorCertificateURL = new HUMCertificateURL__c(Name = 'CertificateURL',CertificateListURL__c = 'https://hss.humana.com/ITSecurity/HSSOneClick/LaunchHSS.aspx?HIDDENTARGET=https://apps.humana.com/eCertNext/default.aspx');
            insert MentorCertificateURL;
        }
    }
    @IsTest
    static void checkCertificateCustomSettinmg(){
        User testUser = [Select Id,Name from User where LastName='test User1'];
        Test.startTest();
        System.runAs(testUser){
            object objCertificate = GenericCustomSettingValue_LC_HUM.getCustomSettingValue('Certificate','CertificateURL');
            System.assertNotEquals(null,objCertificate,'success');
        }
        Test.stopTest();        
    }
    
    @IsTest
    static void checkSwitchValue(){
        User testUser = [Select Id,Name from User where LastName='test User1'];
        Test.startTest();
        System.runAs(testUser){
            object objCertificate = GenericCustomSettingValue_LC_HUM.getCustomSettingValue('Switch','754843');
            System.assertNotEquals(null,objCertificate,'success');
        }
        Test.stopTest();
    }
    
    @IsTest
    static void checkServiceCallOutMapping(){
        User testUser = [Select Id,Name from User where LastName='test User1'];
        Test.startTest();
        System.runAs(testUser){
            object objCertificate = GenericCustomSettingValue_LC_HUM.getCustomSettingValue('WebService','InquirySummaryService');
            System.assertNotEquals(null,objCertificate,'success');
        }
        Test.stopTest();
    }
    
    @IsTest
    static void checkNull(){        
        Test.startTest();
        User testUser = [Select Id,Name from User where LastName='test User1'];
        System.runAs(testUser){
            System.assertEquals(null,GenericCustomSettingValue_LC_HUM.getCustomSettingValue('',''),'success');
        }
        Test.stopTest();        
    }
    
    @IsTest
    static void checkException(){
        User testUser = [Select Id,Name from User where LastName='test User1'];
        Test.startTest();
        System.runAs(testUser){
            try{
                object result = GenericCustomSettingValue_LC_HUM.getCustomSettingValue('testing','');
            }catch(Exception e){
                system.assertEquals('Script-thrown exception', e.getMessage(),'success');
            }
        }
        Test.stopTest();
    }
}