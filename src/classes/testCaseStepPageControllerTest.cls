@isTest
public class testCaseStepPageControllerTest {
    
	Public static testMethod void testCaseStepPageControllerTest(){
        Profile p = [select id from profile where name='System Administrator']; 
        User u = new User(alias = 'standt', email='user@bme.com', 
                emailencodingkey='UTF-8', lastname='Testing', languagelocalekey='en_US', 
                localesidkey='en_US', profileid = p.Id,  
                timezonesidkey='America/Los_Angeles', username='user@bme.com');
        Defect__c TestdefectRec = new Defect__c();
        System.runAs(u) {
            //Test data
            testCaseStepPageController testcasesteppagecontroller = new testCaseStepPageController(); 


            testcasesteppagecontroller.getCreateDefect();
            testcasesteppagecontroller.setCreateDefect(true);
            testcasesteppagecontroller.showCreateDefectSection();
            testcasesteppagecontroller.createDefect= true;
            testcasesteppagecontroller.createTestCaseStepResult();
            testcasesteppagecontroller.getTestCaseStepResults();
            testcasesteppagecontroller.getdefectRec();
            testcasesteppagecontroller.setdefectRec(TestdefectRec); 
            
            testCaseStepPageController testcasesteppagecontroller1 = new testCaseStepPageController(); 
            Test_Case_Result__c testCaseStepResult = new Test_Case_Result__c();
            testcasesteppagecontroller1.testCaseStepResult.Status__c ='Failed';
            testcasesteppagecontroller1.showCreateDefectSection();
            
            testCaseStepPageController testcasesteppagecontroller2 = new testCaseStepPageController(); 
            testcasesteppagecontroller2.createDefect= true;
            testcasesteppagecontroller2.getTestCaseStepResults();
            testcasesteppagecontroller2.createTestCaseStepResult();
            testcasesteppagecontroller2.testCaseStepResult = null;
            testcasesteppagecontroller2.createTestCaseStepResult();
        }
    }
}