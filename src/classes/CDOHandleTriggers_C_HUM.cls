/*****************************************************************************************************************************************************************************
Apex Class Name : CDOHandleTriggers_C_HUM
Version         : 1.0
Created On      : 04/01/2019
Function        : We keep the trigger itself logicless and handle the logic here. At project release, there is only 1 
                  trigger (CDO_Gifts_Given__c) after delete. 
Test Class      : CDOHandleTriggers_T_HUM
Modification Log: 
* Developer Name               Code Review                         Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
*Lakshmi Madduri												6/18/2019				CDO Implementation **************************************************************************************************************************************************/
public with sharing class CDOHandleTriggers_C_HUM {
    /*
    * Method Name   :   GiftGivenDelete
    * Description   :   Create the parameter map and then call the flow that handles the calculation of gifts for the 
    *                   parent.
    * Return Type   :   void
    * Parameters    :   List<CDO_Gifts_Given__c>
    */   
    public static void GiftGivenDelete(List<CDO_Gifts_Given__c> allGiftsGiven) {
        try
        {
            for(CDO_Gifts_Given__c currentGiftGiven: allGiftsGiven) {
                Map<String,Object> params = new Map<String, Object>();
                params.put('CDO_ParentId', currentGiftGiven.CDO_Internal_Parent_Record_Id__c);
                Flow.Interview.CDO_Calculate_Gift_Total giftGivenFlow = new Flow.Interview.CDO_Calculate_Gift_Total(params);
    
                giftGivenFlow.start();
            }
        }
        catch(Exception e){
            HUMExceptionHelper.logErrors(e, 'CDOHandleTriggers_C_HUM ', 'GiftGivenDelete');
        }
    }
}