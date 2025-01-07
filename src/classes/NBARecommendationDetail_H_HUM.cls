/*******************************************************************************************************************************
Trigger Name    : NBARecommendationDetail_H_HUM 
Version         : 1.0
Created On      : 10/31/2020
Function        : Hold the logic trigger for logic for Recommendation Detail object.
                  
Modification Log: 
* Developer Name         Code Review              Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Yogesh Gupta                                    09/22/2020                original version
* Sayali Nemade                                   01/29/2021                Checkmarx Issues Resolved - Changed without sharing to with sharing 
* Nirmal Garg										  11/26/2021				Defect 4215 - removed dependency from global constant and hum constant
*******************************************************************************************************************************/

public with sharing class NBARecommendationDetail_H_HUM {
    Public Static final String MEMBER = 'Member';
	Public Static final String CASEINFOSERVICE_ACCOUNTGROUPTYPE_HUM = 'Group';
  /**
    *  Populate External Id 
    *  @name populateExternalIdAndAccount
    *  @param lstNewRecommendations Holds the list of all Recommendation detail records
    *  @return void
    *  @throws NA   
    */
    
    public static void populateExternalIdAndAccount(List<Recommendation_Detail__c> lstNewRecommendationDetails)
    {       
        Set<Id> setOfAccountId = new Set<Id>();
        Set<String> setOfEnterpriseId = new Set<String>();
        Set<String> setOfGenKey = new Set<String>();
        Set<Id> setOfRecomIds = new Set<Id>();
        //Check which field was selected from the Recommendation detail layout
        for(Recommendation_Detail__c recomDetail : lstNewRecommendationDetails){           
            if(recomDetail.Account__c != null)  
                setOfAccountId.add(recomDetail.Account__c);
                
            if(recomDetail.Enterprise_Id__c != null)    
                setOfEnterpriseId.add(recomDetail.Enterprise_Id__c);
            
            if(recomDetail.GenKey__c != null)   
                setOfGenKey.add(recomDetail.GenKey__c);
                
            if(recomDetail.Recommendation__c != null)   
                setOfRecomIds.add(recomDetail.Recommendation__c);
                
        }
        //Get the Eneterprise Id from Account or Get Account  from the entered Eneterprise Id or GenKey
        List<Account> listOfAccount = [Select Id, Name, Enterprise_Id__c From Account Where 
                                        Enterprise_Id__c IN : setOfEnterpriseId OR Enterprise_Id__c IN: setOfGenKey OR Id IN: setOfAccountId];       
        Map<Id, String> mapOfAccountWithEnterpriseId = new Map<Id, String>();
        Map<String, Id> mapOfEnterpriseIdWithAccount = new Map<String, Id>();
        for(Account acc : listOfAccount){  
            mapOfAccountWithEnterpriseId.put(acc.Id, acc.Enterprise_Id__c);
            mapOfEnterpriseIdWithAccount.put(acc.Enterprise_Id__c, acc.Id);           
        }
        //Get the Recommendation Number from Recommendation Id
        List<Recommendation> listOfRecoms = [Select Id, Name, Recommendation_Number__c From Recommendation Where Id IN: setOfRecomIds];
        Map<Id, String> mapOfRecomIdWithRNumber = new Map<Id, String>();
        for(Recommendation rec : listOfRecoms){
            mapOfRecomIdWithRNumber.put(rec.Id, rec.Recommendation_Number__c);
        }
		
		Set<String> setOfNewExternalIds =  new Set<String>();
    	
        //Populdate the External Id field and Eneterprise Id if the Account look up is selected 
        for(Recommendation_Detail__c detail : lstNewRecommendationDetails){
		    if(string.IsBlank(detail.External_Id__c )) {
				if(detail.Type__c == MEMBER){
					if(detail.Account__c != null){
							detail.External_Id__c = mapOfAccountWithEnterpriseId.get(detail.Account__c) + '|' +  detail.Type__c + '|' + 
													mapOfRecomIdWithRNumber.get(detail.Recommendation__c);
							detail.Enterprise_Id__c  =mapOfAccountWithEnterpriseId.get(detail.Account__c);
							setOfNewExternalIds.add(detail.External_Id__c);
					}
					else{
					
							detail.External_Id__c = detail.Enterprise_Id__c  + '|' +  detail.Type__c + '|' + 
												mapOfRecomIdWithRNumber.get(detail.Recommendation__c); 
							setOfNewExternalIds.add(detail.External_Id__c);											
					}
				}
            
				if(detail.Type__c == CASEINFOSERVICE_ACCOUNTGROUPTYPE_HUM){
					if(detail.Account__c != null){
							detail.External_Id__c = mapOfAccountWithEnterpriseId.get(detail.Account__c) + '|' +  detail.Type__c + '|' + 
													mapOfRecomIdWithRNumber.get(detail.Recommendation__c);
							detail.GenKey__c  =mapOfAccountWithEnterpriseId.get(detail.Account__c);
							setOfNewExternalIds.add(detail.External_Id__c);
					}
					else{
						detail.External_Id__c = detail.GenKey__c + '|' +  detail.Type__c + '|' + 
												mapOfRecomIdWithRNumber.get(detail.Recommendation__c);
						setOfNewExternalIds.add(detail.External_Id__c);
					}
				}
    		}
		}
        //populdate the Account lookup from Enterprise Id
        for(Recommendation_Detail__c detail : lstNewRecommendationDetails){
            if(detail.Account__c == null && (detail.GenKey__c != null || detail.Enterprise_Id__c != null)){
                if(mapOfEnterpriseIdWithAccount.containsKey(detail.Enterprise_Id__c))
                    detail.Account__c = mapOfEnterpriseIdWithAccount.get(detail.Enterprise_Id__c);
                if(mapOfEnterpriseIdWithAccount.containsKey(detail.GenKey__c))
                    detail.Account__c = mapOfEnterpriseIdWithAccount.get(detail.GenKey__c);
            }
        }
        
        //If the Account is not found in CRM
        for(Recommendation_Detail__c detail : lstNewRecommendationDetails){
            if(detail.Account__c == null){
                detail.addError('Account not found in CRM'); 
            }
        }
		
		List<Recommendation_detail__c> listOfExistingdetails = [Select Id, Name, External_id__c From recommendation_detail__c 
        														Where External_id__c IN: setOfNewExternalIds];
        
        for(Recommendation_Detail__c detail : lstNewRecommendationDetails){
            for(Recommendation_Detail__c detailEx : listOfExistingdetails){
            	if(detail.External_id__c == detailEx.External_id__c)
            		 detail.addError('Recommendation already present for the selected account'); 
            }
        }
    }
}