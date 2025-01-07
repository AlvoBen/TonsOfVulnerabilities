public with sharing class useCaseStepPageController {

    public Use_Case_Step__c useCaseStepResult = new Use_Case_Step__c();

    public List<Use_Case_Step__c> getUseCaseStepResults() {
        List<Use_Case_Step__c> useCaseResultList = new List<Use_Case_Step__c>();


        useCaseStepResult.Step_Number__c = 0;
        useCaseStepResult.Step_Description__c = 'First';
        useCaseStepResult.Expected_Result__c = 'Expected Result';

        useCaseStepResult.Use_Case__c =ApexPages.currentPage().getParameters().get('useCaseStepId');

        useCaseResultList.add(useCaseStepResult);    
        return useCaseResultList;
    }

    public PageReference createUseCaseStepResult(){

        insert useCaseStepResult;
        System.debug('inserted use case step : '+ useCaseStepResult.Id);
        return null;
    }

   
    

}