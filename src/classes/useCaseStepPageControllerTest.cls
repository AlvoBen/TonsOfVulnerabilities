@isTest
private class useCaseStepPageControllerTest{
Public static testMethod void useCaseStepPageControllerTest(){
      Profile p = [select id from profile where name='System Administrator']; 
        User u = new User(alias = 'standt', email='user@bme.com', 
                emailencodingkey='UTF-8', lastname='Testing', languagelocalekey='en_US', 
                localesidkey='en_US', profileid = p.Id, 
                timezonesidkey='America/Los_Angeles', username='user@bme.com');
        System.runAs(u) {
            //Test data
            Project__c r = new Project__c(Name='RTest',Type__c='Major',Status__c='Scoping');
            insert r;
            Release__c relRecord = new Release__c(Name = 'test rel1');
             insert relRecord;
            Major_feature__c wp = new Major_feature__c(release1__c =relRecord.Id, Work_Package_Name__c='WPTEST',Status__c='Not Started',Priority__c='High',Release__c=r.id, Major_Feature_Estimate_EMs__c=1234);
            insert wp;
            Use_Case__c usecaseObj = new Use_Case__c(Trigger__c='Test', Use_Case_Name__c='Test1',Work_Package__c=wp.id);
            insert usecaseObj;

            Use_Case_Step__c UseCaseStepObj= new Use_Case_Step__c(Step_Number__c=1);
            insert UseCaseStepObj;

            List<Use_Case_Step__c> usecaseList = new List<Use_Case_Step__c>();
            usecaseList.add(UseCaseStepObj);

            useCaseStepPageController usecasesteppagecontroller = new useCaseStepPageController (); 
            usecasesteppagecontroller.getUseCaseStepResults();
            usecasesteppagecontroller.createUseCaseStepResult();
        }
    }
}