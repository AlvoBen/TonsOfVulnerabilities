/*******************************************************************************************************************************
Apex Trigger Name : HUMCaseTrigger 
Version           : 1.0
Created On        : 05/21/2014
Function          : This serves as Trigger on the Case object.
                
Modification Log: 
* Developer Name           Code Reveiw                 Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Ninad Patil               16484                   05/21/2014                 Original Version
* Mrudula Anusha            17100                   06/17/2014                 Queueviews functionality
* SuryaKumari Medicherla    17100                   07/02/2014                 Queue Assignment Functionality
* Ninad Patil               17100                   08/05/2014                 Resubmitting After coding standards correction.
* Santhi Mandava            20591                   01/28/2015                 Code Modified to fix Defect # 164139
* Santhi Mandava            21968                   23/03/2015                 As per REQ-02635, removed future call out logic and refactored existing code.
* SuryaKumari Medicherla    22085                   02/04/2014                 Code Modified for REQ-72835, to avoid trigger logic when case field "Open Work Task Count" only changed due to task trigger.
* SuryaKumari Medicherla    22274                   10/04/2015                 Code Modified to fix test class failure.
* Santhi Mandava            23613                   03/06/2015                 Fixed CC issue 563-Case is not routing correctly when LastModifiedByQueue 
                                                                               is used in queue view filter criteria.
* Avinash Choubey          30464                    02/27/2016                 code Re-factored as part of REQ-252640
* Harshith Mandya                                   02/20/2016                 Implmented REQ - 300084
* PradeepKumar Dani     83713                       02/12/2016                 Implmented REQ - 308332(CaseTriggerHandler_HUM)
* Mohammed Noor                                     12/06/2018                 Remove Recursive check from Before and After Insert logic to handle Bulk API partial retry.
* Moshitha Gunasekaran                              08/20/2019                 REQ - 406898 TECH- SF - Update CaseTrigger to execute for all other jobs apart from CaseUpdateWorkQueue APEX batch
* Santhi Mandava                                    09/30/2020                 Implemented logic to avoid recursive execution
* Jasmeen Shangari                                  05/18/2021                 REQ - 1800806 - Skip Trigger logic for Wellness Coach & Manager Profile  
* Pooja Kumbhar										12/28/2022				   User story 4083329: T1PRJ0170850 - Lightning- UI Trigger Change to Support ETL
* Pooja Kumbhar										1/11/2023				   User story 4083329: Change in Flag settings
* Pooja Kumbhar									   01/30/2023				   User story 4083329: T1PRJ0170850 - Lightning- UI Trigger Change to Support ETL added Enhanced changes
* Pooja Kumbhar									    2/2/2023				    User story 4083329: Removed initialization and updated variable name 
*******************************************************************************************************************************/

trigger HUMCaseTrigger on Case (before insert, before update, after insert, after update)
{
    try
    { 
        HUMTriggerSwitch__c objTriggerSwitch = HUMTriggerSwitch__c.getValues('Case');
        if(objTriggerSwitch != NULL && objTriggerSwitch.Exeute_Trigger__c == true )
        {
            // To switch off trigger when case trigger comes only from Deployment profile and to execute for ETL batch jobs and UI
            String profileName = HumUtilityHelper.getCurrentUserProfileName();
            HUMQueueViewBatchSettings__c objQueBatchSetup = HUMQueueViewBatchSettings__c.getValues('QueueViewBatchSetup');
            // Added ETL Trigger Execution switch to stop trigger execution for specific users from ETL_User_Check__c custom setting
            String sUsername = UserInfo.getUserName();
            ETL_User_Check__c OneshotETLUser;
            if(sUsername.length()<=38) OneshotETLUser = ETL_User_Check__c.getValues(sUsername);
            CRMFunctionality_ONOFF_Switch__c TriggerExecute = CRMFunctionality_ONOFF_Switch__c.getValues('ETL Trigger Execution');
            if((objQueBatchSetup != null && ((objQueBatchSetup.TriggerExecutionRequired__c == true && !profileName.equalsIgnoreCase(GLOBAL_CONSTANT_CH_HUM.sWellnessCoachProfile) && !profileName.equalsIgnoreCase(GLOBAL_CONSTANT_CH_HUM.sWellnessManagerProfile) && OneshotETLUser == null)
            || (objQueBatchSetup.TriggerExecutionRequired__c == false && !profileName.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.DEPLOYMENT_PROFILE_HUM) && !profileName.equalsIgnoreCase(GLOBAL_CONSTANT_CH_HUM.sWellnessCoachProfile) && !profileName.equalsIgnoreCase(GLOBAL_CONSTANT_CH_HUM.sWellnessManagerProfile)  && OneshotETLUser == null )))
            || (TriggerExecute.IsON__c!= null && TriggerExecute.IsON__c==true && OneshotETLUser != null))                 
            {
                if(Trigger.IsUpdate && Trigger.isBefore )
                {    
                    if(TriggerAvoidRecursion_H_HUM.run && !TriggerAvoidRecursion_H_HUM.bFutureLogicRan) 
                        CaseTriggerHandler_HUM.ProcessCaseBeforeUpdate(Trigger.New, Trigger.OldMap);
                }
                if(Trigger.IsUpdate && Trigger.isAfter )
                {   
                    if(TriggerAvoidRecursion_H_HUM.run && !TriggerAvoidRecursion_H_HUM.bFutureLogicRan)
                    { 
                        CaseTriggerHandler_HUM.ProcessCaseAfterUpdate(Trigger.New, Trigger.OldMap, Trigger.NewMap);
                        TriggerAvoidRecursion_H_HUM.run = false;
                    }
                } 
                if(trigger.isInsert && Trigger.isBefore ) 
                {              
                    if(!TriggerAvoidRecursion_H_HUM.bFutureLogicRan)            
                        CaseTriggerHandler_HUM.processCaseBeforeInsert(Trigger.New,profileName);
                }
                if(trigger.isInsert && Trigger.isAfter ) 
                {                    
                    if(!TriggerAvoidRecursion_H_HUM.bFutureLogicRan)   
                    {        
                        CaseTriggerHandler_HUM.processCaseAfterInsert(Trigger.New,profileName );
                        TriggerAvoidRecursion_H_HUM.run = false;                   
                    }
                }
            }
        }
    }
    catch(Exception ex)
    {
        HUMExceptionHelper.logErrors(ex, 'HUMCaseTrigger', 'HUMCaseTrigger');
    }
}