/*******************************************************************************************************************************
Trigger Name    : CTCI_G_HUM
Version         : 1.0
Created On      : 11/10/2016
Function        : Trigger execution sequence logic for CTCI_Junction__c  object.
                  
Modification Log: 
* Developer Name         Code Review              Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Ranjeeth Nagishetty                            22/03/2017                 original version
                                                                            REQ - 304967: SF - TECH - Validation rule for CTCI List Record
* Ashok Nutalapati			                     01/07/2020                 Add Humana Pharmacy Log Code to CTCI List Pages in Business Configuration App (CRMS)
* Samantha Tennyson                              01/07/2021              User Story 1745934: PR00091574 - MF 1 - Quality - Prevent Complaint/G&A Rights Given fields to be selected on Cases with specific Classification and Intent Combinations (CRM)
* Atia Uzma                                      06/21/2021                 User Story 2348383: T1PRJ0001827 - MF 1 - Case Transfer Assist - Business Configuration enhancements (CRM)
*******************************************************************************************************************************/

trigger CTCI_G_HUM  on CTCI_Junction__c  (before insert, before update, after update)  
{
 try
  {
     HUMTriggerSwitch__c objTriggerSwitch = HUMTriggerSwitch__c.getValues('CTCI_Junction__c');
     if(objTriggerSwitch != NULL && objTriggerSwitch.Exeute_Trigger__c && (!CheckDuplicateCTCI_H_HUM.isTriggerExecuted))
     {
        if(Trigger.isBefore)
        {
             if(Trigger.isInsert)
             {
                  CheckDuplicateCTCI_H_HUM.checkForCTCIDuplicateonInsert(Trigger.New);
             }
             if(Trigger.isUpdate)
             {
                  CheckDuplicateCTCI_H_HUM.checkForDuplicateAssocitedCICTUpdate(Trigger.New,Trigger.oldMap);
             }
         }       
     }
     if(Trigger.isAfter && Trigger.isUpdate)
     {    
          CheckDuplicateCTCI_H_HUM.emptyComplaintGASetup(Trigger.New,Trigger.oldMap);
          CheckDuplicateCTCI_H_HUM.inactivateCTARecords(Trigger.New,Trigger.oldMap);         
     }
  }
  catch(Exception e)
  {
       HUMExceptionHelper.logErrors(e, 'CTCI_G_HUM ', 'CTCI_G_HUM ');
  }

}