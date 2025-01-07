/*****************************************************************************************************
 Apex Class Name : CaseTransferAssist_H_HUM
 Version         : 1.0 
 Created Date    : 06/16/2020
 Function        : Holds the logic for trigger events on Case_Transfer_Assist__c Object
 Test Class      : HUMChangeCaseOwnerControllerTest
 Modification Log :

* Developer                 Code Review         Date                        Description
* -----------------------------------------------------------------------------------------------------                 
* Sunil Kumar Reddy Y                        06/16/2020                 User Story 1200920: PR00091574 - MF 1 - AHT - Ability to auto-populate Service Center and Department on Case Transfer page (RF)
*******************************************************************************************************/
public with sharing class CaseTransferAssist_H_HUM {

    private static Map<String,Case_Transfer_Assist__c> ctaMap = new Map<String,Case_Transfer_Assist__c>();
    private static Map<Id,String> proMap = new Map<Id,String>();

    /**
    *  Duplicate CTA combination check during insert
    *  @param List<Case_Transfer_Assist__c> triggerNew
    *  @return void
    *  @throws NA
    */
    public static void preventDuplicates(List<Case_Transfer_Assist__c> triggerNew){
        initializeData(triggerNew);
    String uniqueKey = '';
      for(Case_Transfer_Assist__c cta : triggerNew ){                        
        if(cta.CTCI_List__c !=null && cta.product__c!=null && cta.Work_Queue_Setup__c!=null){                  
            uniqueKey = cta.Classification__c+'|'+cta.Classification_Type__c+'|'+cta.Intent__c+'|'+proMap.get(cta.product__c)+'|'+cta.Product_Type__c+'|'+cta.Product_Type_Code__c;
            if(ctaMap.containsKey(uniqueKey))
            {
                cta.addError(System.Label.CaseTransferAssistDuplicateError);
            }
        }
      }
    }

    	/**
    *  Duplicate CTA combination check during update
    *  @param List<Case_Transfer_Assist__c> triggerNew
    *  @param Map<Id,Case_Transfer_Assist__c> oldMap
    *  @return void
    *  @throws NA
    */
    public static void preventDuplicates(List<Case_Transfer_Assist__c> triggerNew,Map<Id,Case_Transfer_Assist__c> oldMap ){
        initializeData(triggerNew);
    String uniqueKey = '';
      for(Case_Transfer_Assist__c cta : triggerNew ){                        
        if((cta.CTCI_List__c !=null && cta.product__c!=null && cta.Work_Queue_Setup__c!=null) && (cta.CTCI_List__c!=oldMap.get(cta.Id).CTCI_List__c ||cta.Product__c!=oldMap.get(cta.Id).Product__c || cta.Work_Queue_Setup__c!=oldMap.get(cta.Id).Work_Queue_Setup__c) ){                  
            uniqueKey = cta.Classification__c+'|'+cta.Classification_Type__c+'|'+cta.Intent__c+'|'+proMap.get(cta.product__c)+'|'+cta.Product_Type__c+'|'+cta.Product_Type_Code__c;
            if(ctaMap.containsKey(uniqueKey))
            {
              cta.addError(System.Label.CaseTransferAssistDuplicateError);
            }
        }
      }
    }

    private static void initializeData(List<Case_Transfer_Assist__c> triggerNew){
         Set<String> classificationSet = new Set<String>();
         Set<String> classificationTypeSet = new Set<String>();
         Set<String> intentSet = new Set<String>();
         Set<Id> productSet = new Set<Id>();
         Set<String> proTypeSet = new Set<String>();
         Set<String> proTypeCodeSet = new Set<String>();
         List<Case_Transfer_Assist__c>  listToComapre = new List<Case_Transfer_Assist__c>();

        for(Case_Transfer_Assist__c cta : triggerNew){
            classificationSet.add(cta.Classification__c);
            classificationTypeSet.add(cta.Classification_Type__c);
            intentSet.add(cta.Intent__c);
            proTypeSet.add(cta.Product_Type__c);
            proTypeCodeSet.add(cta.Product_Type_Code__c);
            productSet.add(cta.product__c);
        } 

        for(Product2 pro : [SELECT id,name from product2 WHERE id =:productSet]){
          proMap.put(pro.id,pro.name);
        }

        listToComapre = [SELECT id,Classification__c,Classification_Type__c,Intent__c,product__r.name,product__c,Product_Type__c,Product_Type_Code__c FROM Case_Transfer_Assist__c WHERE  CTCI_List__r.Classification_Type__r.Name=:classificationTypeSet AND CTCI_List__r.Classification__r.Name=:classificationSet AND CTCI_List__r.Intent__r.Name=:intentSet AND product__r.name=:proMap.values() AND Product__r.Major_LOB__c=:proTypeSet AND Product__r.Product_Type__c=:proTypeCodeSet];
        for(Case_Transfer_Assist__c cta :listToComapre ){
            ctaMap.put(cta.Classification__c+'|'+cta.Classification_Type__c+'|'+cta.Intent__c+'|'+cta.product__r.name+'|'+cta.Product_Type__c+'|'+cta.Product_Type_Code__c,cta);
        }
             
    }
}