@isTest
public class UseCaseController_Test {
	Public static testMethod void UseCaseControllerTest(){
         Profile p = [select id from profile where name='System Administrator']; 
         User u = new User(alias = 'standt', email='user@bme.com', 
            emailencodingkey='UTF-8', lastname='Testing', languagelocalekey='en_US', 
            localesidkey='en_US', profileid = p.Id, 
            timezonesidkey='America/Los_Angeles', username='user@bme.com');
             System.runAs(u) {
             //Test data
             project__c r = new project__c(Name='RTest',Type__c='Major',Status__c='Scoping');
             insert r;
             
             Release__c relRec = new Release__c(Name = 'test Release');
             insert relRec;
             
             Major_Feature__c wp = new Major_Feature__c(Release1__c = relRec.Id,Work_Package_Name__c='WPTEST',Status__c='Not Started',Priority__c='High',Release__c=r.id, Major_Feature_Estimate_EMs__c=1234);
             insert wp;
             Use_Case__c usecaseObj = new Use_Case__c(Trigger__c='Test', Use_Case_Name__c='Test1',Work_Package__c=wp.id);
             insert usecaseObj;
             
             Use_Case_Step__c UseCaseStepObj= new Use_Case_Step__c(Step_Number__c=1);
             insert UseCaseStepObj;
            
             List<Use_Case_Step__c> usecaseList = new List<Use_Case_Step__c>();
             usecaseList.add(UseCaseStepObj);
             
                         
            ApexPages.StandardController stdController = new ApexPages.StandardController(usecaseObj);
            UseCaseController usecasecontroller1 = new UseCaseController (stdController); 
            
            UseCaseController usecasecontroller = new UseCaseController (); 
            usecasecontroller.usecaseid = usecaseObj.Id;
            usecasecontroller.CancelNewUseCase();
            usecasecontroller.getusecase();
            usecasecontroller.setusecase(usecaseObj);
            usecasecontroller.ZeroStep(usecaseList );
            usecasecontroller.DuplicateStep(usecaseList);
            usecasecontroller.InsertUseCase();
            usecasecontroller.SaveUseCase();
            usecasecontroller.SaveAndNewUseCase();
            usecasecontroller.CancelUseCase();
            usecasecontroller.getUseCaseSteps();
            usecasecontroller.getSelected();
            usecasecontroller.GetSelectedAccounts();
            usecasecontroller.getobj();
            usecasecontroller.setobj(UseCaseStepObj);
            usecasecontroller.AddUseCaseStep();
            usecasecontroller.AddStep_Cancel();
            usecasecontroller.removeUseCaseStep();
            usecasecontroller.updateUseCaseStep();
            }
           }
}