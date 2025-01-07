public with sharing class NBALwcMultiLookupController {
    public NBALwcMultiLookupController() {

    }
	
	/**
    *  Method to for the class , Initialize variables and get results.
    * @ Param       String ObjectName, String fieldName, String value, List<String> selectedRecId
    * @ Return      List
    */
    @AuraEnabled(cacheable=true)
    public static List<SObJectResult> getResults(String ObjectName, String fieldName, String value, List<String> selectedRecId) {
        List<SObJectResult> sObjectResultList = new List<SObJectResult>();
	Boolean sNBASwitchCall;
         sNBASwitchCall = HUMUtilityHelper.isCRMFunctionalityON('2164068');
        if(selectedRecId == null)
            selectedRecId = new List<String>();

        if(String.isNotEmpty(value)) {
          if(sNBASwitchCall==true) {
            String queryString='%' + value.trim() + '%';
            String query = 'Select Id,'+fieldName+' FROM '+ObjectName+' WHERE '+fieldName+' LIKE :queryString and ID NOT IN: selectedRecId WITH USER_MODE';
            for(sObject so : Database.Query(query)) {
                String fieldvalue = (String)so.get(fieldName);
                sObjectResultList.add(new SObjectResult(fieldvalue, so.Id));
            }
            }
            else
            {
                String query = 'Select Id,'+fieldName+' FROM '+ObjectName+' WHERE '+fieldName+' LIKE \'%' + value.trim() + '%\' and ID NOT IN: selectedRecId WITH USER_MODE'; 
                for(sObject so : Database.Query(query)) {
                String fieldvalue = (String)so.get(fieldName);
                sObjectResultList.add(new SObjectResult(fieldvalue, so.Id));
            }
            }
        }
        return sObjectResultList;
    }
    
    public class SObjectResult {
        @AuraEnabled
        public String recName;
        @AuraEnabled
        public Id recId;
        
        public SObJectResult(String recNameTemp, Id recIdTemp) {
            recName = recNameTemp;
            recId = recIdTemp;
        }
        public SObJectResult() {
          
        }
    }
    /**
    *  Method to save the results in work queue junction.
    * @ Param       String recommendationIdToSave, List<String> workQueudIdsToSave
    * @ Return      Boolean
    */
    @AuraEnabled
    public static Boolean saveJunctionRecord(String recommendationIdToSave, List<String> workQueudIdsToSave)
    
    {
     Boolean sNBASwitchCall;
    sNBASwitchCall = HUMUtilityHelper.isCRMFunctionalityON('2164068');
    Boolean isSavedRecord = false;
    
       List<Recommendation_Work_Queue_Juntion__c> existingRecommendationJunction = [Select Id, Name, Work_Queue_Setup__c  From Recommendation_Work_Queue_Juntion__c 
                                                                                      Where Recommendation__c =: recommendationIdToSave WITH USER_MODE];
         
          Set<Id> setOfWorkQueueIds = new Set<Id>();
          for(Recommendation_Work_Queue_Juntion__c junctions : existingRecommendationJunction){
              if(junctions.Work_Queue_Setup__c != null)
                  setOfWorkQueueIds.add(junctions.Work_Queue_Setup__c);
          } 
          
          
          
    
        List<Recommendation_Work_Queue_Juntion__c> junctionsToSaveList =  new List<Recommendation_Work_Queue_Juntion__c>();
        Set<Id> setOfJunctionIdsToDelete = new Set<Id>();
        Set<Id> setOfJunctionIdsToUpdate = new Set<Id>();
        
        for(String workQueueIds :  workQueudIdsToSave){
            if(workQueueIds != null && workQueueIds != ''   ){
               
                if(!setOfWorkQueueIds.contains(workQueueIds)){
              
                    Recommendation_Work_Queue_Juntion__c junctions = new Recommendation_Work_Queue_Juntion__c();
                    junctions.Recommendation__c = recommendationIdToSave;
                    junctions.Work_Queue_Setup__c = workQueueIds;
                    junctionsToSaveList.add(junctions);
             
                }
            }
        }
        
        for(Recommendation_Work_Queue_Juntion__c junctions : existingRecommendationJunction){
             if(junctions.Work_Queue_Setup__c != null){
                 if(!workQueudIdsToSave.contains(junctions.Work_Queue_Setup__c)){
                     setOfJunctionIdsToDelete.add(junctions.Id);
                 }
             }
         }
        
       List<Recommendation_Work_Queue_Juntion__c> junctionsToDelete = [Select Id, Name From Recommendation_Work_Queue_Juntion__c WHERE ID IN: setOfJunctionIdsToDelete WITH USER_MODE];
         SObjectType workJunction = Schema.getGlobalDescribe().get('Recommendation_Work_Queue_Juntion__c'); 
        if(junctionsToDelete.size() >0  ){	
			if(workJunction.getDescribe().isAccessible() && workJunction.getDescribe().isDeletable()) {
				delete junctionsToDelete;
			}	
		}
         if(sNBASwitchCall==true)
        {
        SObjectAccessDecision securityDecision = Security.stripInaccessible(AccessType.CREATABLE, junctionsToSaveList);
     	junctionsToSaveList= securityDecision.getRecords(); 
        insert junctionsToSaveList;
	}
        else
        {
          insert junctionsToSaveList;  
        }	
        isSavedRecord  = true;
        return isSavedRecord;
    }
	
    /**
    *  Method to load the results of work queue.
    * @ Param       String recommendationIdToFetch
    * @ Return      List
    */
    @AuraEnabled
    public static List<Recommendation_Work_Queue_Juntion__c> loadWorkQueueRecord(String recommendationIdToFetch){
       List<Recommendation_Work_Queue_Juntion__c> listOfworkQueues = [Select Work_Queue_Setup__c, Recommendation__r.Name, Work_Queue_Setup__r.Name From Recommendation_Work_Queue_Juntion__c 
            Where Recommendation__c =: recommendationIdToFetch WITH USER_MODE];
        return listOfworkQueues;
    }
	
    /**
    *  Method to load the recommendation name.
    * @ Param       String recommendationIdString
    * @ Return      String
    */
     
    @AuraEnabled(cacheable=true)
    public static String getNameOfRecommendation (String recommendationIdString) {
        List<Recommendation> recommendationList =  [SELECT Id, Name FROM Recommendation WHERE Id = :recommendationIdString WITH USER_MODE LIMIT 1];
        return recommendationList[0].Name;
    } 
}