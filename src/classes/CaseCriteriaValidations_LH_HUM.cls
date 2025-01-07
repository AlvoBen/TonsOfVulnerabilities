/*****************************************************************************************
Apex class Name    : CaseCriteriaValidations_LH_HUM
Version            : 1.0
Function           : Handle all case related validations
Created Date       : 03/08/2021 
Test Class         : CaseCriteriaValidations_LT_HUM

Modification Log:
 *   Developer                   Code Review             Date               Description
 * ------------------------------------------------------------------------------------------------------------------------------   
    Ritik Agarwal                                       02/07/2022          Initial version(#2039867)
 *********************************************************************************************************************************/

public with Sharing class CaseCriteriaValidations_LH_HUM {
    
    public Static Account caseAccount;
    public Static Memberplan caseMemPlan;
    public Static User currentUser;
    public static InputParamsModel inputData;
    
    @AuraEnabled
    public static Object inputsForValidation(String inputParams){
        inputData = (InputParamsModel) JSON.deserialize(inputParams, InputParamsModel.class);
        if(String.isNotBlank(inputData.accountId)){
            caseAccount = [Select id, Recordtype.Name from Account Where ID=:inputData.accountId];
        }
        if(String.isNotBlank(inputData.memPlanId)){
            caseMemPlan = [Select id, Product__c, Plan.Product__r.Product_Type__c, Plan.Product__r.Major_LOB__c from Memberplan Where ID=:inputData.memPlanId];
        }
        currentUser = [Select id , Profile.Name from user where id=:UserInfo.getUserId()];
        return prepareDataModel();
    }
    
    public Static Object prepareDataModel(){
        DataModel modelToReturn =  new DataModel();
        modelToReturn.interactingWithRecType = caseAccount?.Recordtype?.Name;
        modelToReturn.profileName = currentUser?.Profile?.Name;
        modelToReturn.memPlanProduct = caseMemPlan?.Product__c; 
        modelToReturn.memPlanPolicyProductType = caseMemPlan?.Plan?.Product__r?.Product_Type__c;
        modelToReturn.memPlanPolicyMajorLob = caseMemPlan?.Plan?.Product__r?.Major_LOB__c;
        return modelToReturn; 
    }
    
    public with Sharing class InputParamsModel{
        public String accountId;
        public String memPlanId;
        
        public InputParamsModel(){} 
    }
    
    public with Sharing class DataModel{
        @AuraEnabled
        public String interactingWithRecType;
        @AuraEnabled
        public String profileName;
        @AuraEnabled
        public String memPlanProduct;
        @AuraEnabled
        public String memPlanPolicyProductType;
        @AuraEnabled
        public String memPlanPolicyMajorLob;
        public DataModel(){
        } 
    }
}