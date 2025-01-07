/*******************************************************************************************************************************
Apex Class Name : CaseTransferUserPopulation_LH_HUM
Version         : 1.0
Created On      : 02/24/2022
Function        : Modal for work queue setup object, work queue junction object 
Test Class      : CaseTransfer_LT_HUM

Modification Log: 
* Version Number             Developer Name              Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
*     1.0 					 Ashish Kumar 											02/24/2022                 Original Version
*********************************************************************************************************************************/

public class CaseTransferUserPopulation_LH_HUM {
    
    //String Variables
    @AuraEnabled  public String sSelectedWorkQueue;
    @AuraEnabled  public String sPublicGroupName;
    @AuraEnabled  public Work_Queue_setup__c oWorkQueueSetup;
    @AuraEnabled  public List<Work_Queue_Junction__c> lstWorkQueueJunction;
    @AuraEnabled public List<classificationIntentModel.OptionModel> lstTopics;

    public CaseTransferUserPopulation_LH_HUM(){}
}