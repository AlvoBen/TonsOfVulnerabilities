/*******************************************************************************************************************************
Trigger Name    : NBARecommendation_G_HUM
Version         : 1.0
Created On      : 09/07/2020
Function        : Trigger execution sequence logic for Recommendation object.
                  
Modification Log: 
* Developer Name         Code Review              Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Yogesh Gupta                         			  09/07/2020            	original version
* Sayali Nemade									  11/05/2020				REQ - 1041220 -- PR00090631 - MF 9 - MVP Ability to 
modify and store alerts in CRM Service
*******************************************************************************************************************************/

trigger NBARecommendation_G_HUM on Recommendation (before insert, before update, before delete) {

	try {          
       if(Trigger.isBefore) {    
            
            if(Trigger.isInsert || Trigger.isUpdate) {
                NBARecommendation_H_HUM.checkForDuplicateRecommendation(Trigger.New, Trigger.oldMap);
            }
			
			if(Trigger.isUpdate){
                NBARecommendation_H_HUM.trackHistoryForRecommendation(Trigger.New, Trigger.oldMap);   
            }
			
			if(Trigger.isInsert || Trigger.isUpdate || Trigger.isDelete) {
           		NBARecommendation_H_HUM.ReadOnlyForRecommendation(Trigger.New, Trigger.old); 	  
            } 
			
			if(NBARecommendation_H_HUM.isFirstTime) {
        		NBARecommendation_H_HUM.isFirstTime = false;
				
				if(trigger.isInsert)  
				{  
					NBARecommendation_H_HUM.PriorityIncerementInsertRecommendation(trigger.new);
				}
			   
				else if (trigger.isUpdate)
				{
					NBARecommendation_H_HUM.PriorityIncerementUpdateRecommendation(trigger.new,trigger.old, trigger.oldmap);
				}
			   
				else if(trigger.isDelete)
				{
					NBARecommendation_H_HUM.PriorityIncerementDeleteRecommendation(trigger.old);
				}
			}
		}
    }
	
    catch(Exception ex) {
        HUMExceptionHelper.logErrors(ex, 'NBARecommendation_G_HUM', 'NBARecommendation_G_HUM');
    }
}