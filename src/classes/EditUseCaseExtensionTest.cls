@isTest
private class EditUseCaseExtensionTest{
Public static testMethod void EditUseCaseExtensionTest(){
         Profile p = [select id from profile where name='System Administrator']; 
         User u = new User(alias = 'standt', email='user@bme.com', 
            emailencodingkey='UTF-8', lastname='Testing', languagelocalekey='en_US', 
            localesidkey='en_US', profileid = p.Id, 
            timezonesidkey='America/Los_Angeles', username='user@bme.com');
             System.runAs(u) {
             //Test data
             Project__C r = new Project__c(Name='RTest',Type__c='Major',Status__c='Scoping');
             insert r;
             
             Release__c rel = new Release__c(Name = 'test release');
             insert rel;
             
             Major_feature__c wp = new Major_feature__c(Work_Package_Name__c='WPTEST',Status__c='Not Started',Priority__c='High',Release__c=r.id,Release1__c = rel.Id, Major_Feature_Estimate_EMs__c=1234);
             insert wp;
             Use_Case__c usecaseObj = new Use_Case__c(Trigger__c='Test', Use_Case_Name__c='Test1',Work_Package__c=wp.id);
             insert usecaseObj;
             Use_Case__c usecaseObj2 = new Use_Case__c(Trigger__c='Test', Use_Case_Name__c='Test2',Work_Package__c=wp.id);
             insert usecaseObj2;
             Use_Case__c usecaseObj3 = new Use_Case__c(Trigger__c='Test', Use_Case_Name__c='Test3',Work_Package__c=wp.id);
             insert usecaseObj3;
             
             Use_Case_Step__c UseCaseStepObj= new Use_Case_Step__c(Step_Number__c=1, Use_Case__c = usecaseObj.id);
             insert UseCaseStepObj;
             
             Use_Case_Step__c UseCaseStepObj2= new Use_Case_Step__c(Step_Number__c=0, Use_Case__c = usecaseObj.id);
             insert UseCaseStepObj2;
             
             Use_Case_Step__c UseCaseStepObj3= new Use_Case_Step__c(Step_Number__c=5, Use_Case__c = usecaseObj.id);
             insert UseCaseStepObj3;
             
             Use_Case_Step__c UseCaseStepObj4= new Use_Case_Step__c(Use_Case__c = usecaseObj.id);
             insert UseCaseStepObj4;
             
             Use_Case_Step__c UseCaseStepObj5= new Use_Case_Step__c(Step_Number__c=5, Use_Case__c = usecaseObj2.id);
             insert UseCaseStepObj5;
             Use_Case_Step__c UseCaseStepObj6= new Use_Case_Step__c(Step_Number__c=5, Use_Case__c = usecaseObj2.id);
             insert UseCaseStepObj6;
             Use_Case_Step__c UseCaseStepObj7= new Use_Case_Step__c(Step_Number__c=5, Use_Case__c = usecaseObj3.id);
             insert UseCaseStepObj7;
            
             List<Use_Case_Step__c> usecaseList = new List<Use_Case_Step__c>();
             usecaseList.add(UseCaseStepObj);
             
                         
             ApexPages.StandardController stdController = new ApexPages.StandardController(usecaseObj);
             ApexPages.StandardController stdController2 = new ApexPages.StandardController(usecaseObj2);
             ApexPages.StandardController stdController3 = new ApexPages.StandardController(usecaseObj3);
             EditUseCaseExtension editusecaseextension = new EditUseCaseExtension (stdController);
             EditUseCaseExtension editusecaseextension2 = new EditUseCaseExtension (stdController);
             EditUseCaseExtension editusecaseextension3 = new EditUseCaseExtension (stdController2);
             EditUseCaseExtension editusecaseextension4 = new EditUseCaseExtension (stdController3);
           editusecaseextension.getusecase();
           editusecaseextension.setusecase(usecaseObj);
           editusecaseextension.getUseCaseSteps();
           editusecaseextension.usecasestepList[0].selected = true;
           editusecaseextension.getSelected();
           editusecaseextension.GetSelectedAccounts();
           editusecaseextension2.GetSelectedAccounts();
           editusecaseextension.getobj();
           editusecaseextension.setobj(UseCaseStepObj);
           editusecaseextension.AddUseCaseStep();
           editusecaseextension2.setobj(UseCaseStepObj2);
           editusecaseextension2.AddUseCaseStep();
           
           editusecaseextension2.setobj(UseCaseStepObj3);
           editusecaseextension2.AddUseCaseStep();
           
           editusecaseextension2.setobj(UseCaseStepObj4);
           editusecaseextension2.AddUseCaseStep();
           editusecaseextension.AddStep_Cancel();
           editusecaseextension.removeUseCaseStep();
           editusecaseextension3.removeUseCaseStep();
           editusecaseextension.updateUseCaseStep();
           editusecaseextension2.updateUseCaseStep();
           
           editusecaseextension3.getUseCaseSteps();
           editusecaseextension3.updateUseCaseStep();
           
           editusecaseextension4.getUseCaseSteps();
           editusecaseextension4.usecasestepList[0].selected = true;
           editusecaseextension4.updateUseCaseStep();
           
           editusecaseextension.ZeroStepNumber();
           editusecaseextension.DuplicateStepNumber();
           editusecaseextension.DuplicateStepNumber_AddClick();
           EditUseCaseExtension.useCaseWrapper wrap = new EditUseCaseExtension.useCaseWrapper(UseCaseStepObj);
           
           }
           }
   }