public with sharing class testCaseStepPageController {

    public Test_Case_Result__c testCaseStepResult = new Test_Case_Result__c();
    public List<Test_Case_Result__c> testCaseResultList ;
    public List<Test_Case_Result__c> getTestCaseStepResults() {

        if(testCaseResultList == null){
            testCaseResultList  = new List<Test_Case_Result__c>();
            testCaseStepResult.Result_By__c = UserInfo.getUserId();
            testCaseStepResult.Tested_On__c = System.Today();
            testCaseStepResult.Test_Case_Step__c = ApexPages.currentPage().getParameters().get('stepId');

            testCaseResultList.add(testCaseStepResult);    
        }
        return testCaseResultList;
    }

    public PageReference createTestCaseStepResult(){

        try{

            System.debug('@@@@@@createDefect '+createDefect);

            // Creating a defect if CreateDefect checkbox is checked
            if(createDefect == TRUE){
                defectRec.Defect_Status__c = 'Submitted';
                defectRec.Open_Date__c = System.Today();
                defectRec.Test_Case_Step__c = ApexPages.currentPage().getParameters().get('stepId');

                if(testCaseResultList != null){
                    if(Test_Case_Result__c.SObjectType.getDescribe().isCreateable())
                        insert testCaseResultList.get(0);

                    if(Defect__c.SObjectType.getDescribe().isCreateable())
                        insert defectRec;
                }

            }
        }catch(Exception ex){
            System.debug(ex.getMessage());
        }
        return null;
    }

    public Boolean createDefect = FALSE;

    public Boolean getCreateDefect(){
        return createDefect;
    }

    public void setCreateDefect(Boolean createDefect){ this.createDefect = createDefect; }

    public Defect__c defectRec = new Defect__c();
    public Defect__c getdefectRec(){ return defectRec;}
    public void setdefectRec(Defect__c defectRec){ this.defectRec = defectRec;}

    public PageReference showCreateDefectSection(){
        if(testCaseStepResult.Status__c =='Failed'){
            createDefect = TRUE;
        }else {
            createDefect = FALSE;       
        }

        System.debug('in defect create method : '+createDefect);
        return null;
    }

    
}