/******************************************************************************************************************************
Apex Class Name  : MilestoneTimeCalculator_HUM
Version          : 1.0 
Created Date     : Sept 20 2018
Function         : 
Test Class       : MilestoneTimeCalculator_T_HUM
Modification Log :
 *  Developer              Code Review         Date                 Description
 * ------------------------------------------------------------------------------------------------------------------------------
 * Asish Behera                             03/24/2019             	Milestones time fethching from workqueue and view.
 * Subhamay Mandal			     			04/09/2019		    	Defect 409931.0001 fix. REQ 389686 : If the case status is Pending Response/ Pending Transfer Milestones are not displayed
 * Subhamay Mandal			     			05/14/2019		    	REQ - 396287 Quality - MIR : Fix log issue 
 * Subhamay Mandal			     			03/24/2021		    	User Story 1946416: T1PRJ0001827- IR 5050- CheckMarx violation fixes (High items)
********************************************************************************************************************************/
global with sharing class MilestoneTimeCalculator_HUM implements Support.MilestoneTriggerTimeCalculator {
    // SLA Time for Milestone
    private Integer iSLATime = Integer.valueOf('0');
    
    /**
    * Calculates the milestone trigger time based on the specified case and milestone type and returns the time in minutes.
    * @param String ID of the case the milestone is applied to.
    * @param String ID of the milestone type
    * @return Integer The calculated trigger time in minutes.
    */
    global Integer calculateMilestoneTriggerTime(String sCaseId, String sMilestoneTypeId){
        try{
            List<Case> listofCases = [SELECT Priority, Owner_Queue__c, Work_Queue_View_Name__c, Status, Transferred_Date__c, CreatedDate 
                                      FROM Case WHERE Id=:sCaseId and Status in : Constants_C_AHT_HUM.listMIRCaseStatus];
        
            if(!listofCases.IsEmpty() && sMilestoneTypeId != null){
                Case oCase = listofCases[0];
                String sOwnerQueue = oCase.Owner_Queue__c;
                String sWQViewName = oCase.Work_Queue_View_Name__c;

                List<Work_Queue_Setup__c>  listWorkQueueSetup = [SELECT SLA_In_Days__c, SLA_On_Case_Transfer__c,
                								 (SELECT Name,SLA_In_Days__c, SLA_On_Case_Transfer__c
                								  FROM Queue_Views__r 
                                                  WHERE name =:sWQViewName and Work_Queue_Name__r.Name =:sOwnerQueue AND SLA_In_Days__c != null AND SLA_In_Days__c !=0) 
                                                  FROM Work_Queue_Setup__c WHERE name =:sOwnerQueue AND SLA_In_Days__c != null AND SLA_In_Days__c !=0 LIMIT 1];
                
                
                Integer iTempTime = Integer.valueOf('0');
                Boolean bOnTransferDate = false;
                
                if(!listWorkQueueSetup.isEmpty()) {
                	Work_Queue_Setup__c  worksetup = listWorkQueueSetup.get(0);
                	
                	iTempTime = (null != worksetup.SLA_In_Days__c && Integer.valueOf(worksetup.SLA_In_Days__c) > 0) ? Integer.valueOf(worksetup.SLA_In_Days__c) : Integer.valueOf('0');
                    bOnTransferDate = (null != worksetup.SLA_On_Case_Transfer__c) ? worksetup.SLA_On_Case_Transfer__c : false;
                    
                	if(!worksetup.Queue_Views__r.isEmpty()) {
                		iTempTime = (null != worksetup.Queue_Views__r.get(0).SLA_In_Days__c && Integer.valueOf(worksetup.Queue_Views__r.get(0).SLA_In_Days__c) > 0) ? Integer.valueOf(worksetup.Queue_Views__r.get(0).SLA_In_Days__c): Integer.valueOf('0');
                		bOnTransferDate = (null != worksetup.Queue_Views__r.get(0).SLA_On_Case_Transfer__c) ? worksetup.Queue_Views__r.get(0).SLA_On_Case_Transfer__c : false;		
                	}
                } 
                               
                // get SLA timing
                if(!bOnTransferDate &&  iTempTime > 0 ) {
                    Date dtCreateDate = oCase.CreatedDate.date();
                    Date dtTransferDate = oCase.Transferred_Date__c != null ? oCase.Transferred_Date__c.date() : System.today();
                    iTempTime = (iTempTime - (dtCreateDate.daysBetween(dtTransferDate)));
                }
                
                this.iSLATime = iTempTime * 1440;
            }
            
            if(iSLATime <= 0 ){
            	this.iSLATime = Integer.valueOf('1');
            }
            
        }catch(Exception e){
        	
            HUMExceptionHelper.logErrors(e, 'MilestoneTimeCalculator_HUM', 'calculateMilestoneTriggerTime');
        }   
        return this.iSLATime;        
    }
}