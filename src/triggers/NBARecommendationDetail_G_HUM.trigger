/*******************************************************************************************************************************
Trigger Name    : NBARecommendationDetail_G_HUM
Version         : 1.0
Created On      :  10/31/2020
Function        : Trigger execution sequence logic for Recommendation Detail object.
                  
Modification Log: 
* Developer Name         Code Review              Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Yogesh Gupta                                    10/31/2020                original version
* Nirmal Garg									  11/26/2021				Defect 4215 - removed dependency from global constant and hum constant
*******************************************************************************************************************************/

trigger NBARecommendationDetail_G_HUM on Recommendation_Detail__c (before insert, before update) {
     Profile PROFILE = [SELECT Id, Name FROM Profile WHERE Id=:userinfo.getProfileId() LIMIT 1];
	 Public Static final String ETL_API_ACCESS_PROFILE_HUM  = 'ETL API Access';
     If(PROFILE.Name != ETL_API_ACCESS_PROFILE_HUM){
		 if(Trigger.isBefore) {                
			if(Trigger.isInsert || Trigger.isUpdate) {
				NBARecommendationDetail_H_HUM.populateExternalIdAndAccount(Trigger.New);
	 
			}
		}  
	} 	
}