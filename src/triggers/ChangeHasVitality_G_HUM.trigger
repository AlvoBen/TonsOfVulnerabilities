/***********************************************************************************************************************
Trigger Name     : ChangeHasVitality_G_HUM
Version          : 1.0  
Created Date     : Nov 30, 2015
Function         : Trigger to Update HasVitality Field of PolicyMember Object after deleting Member Cache Record
Test Class       : ChangeHasVitality_T_HUM
****************************************************************************
Modification Log:
*Developer Name          Code Review #         Date                       Description
*------------------------------------------------------------------------------------------------------------
* Apoorv Jain                               11/30/2015                    Original Version
*********************************************************************************************************************/

trigger ChangeHasVitality_G_HUM on Member_Cache__c (after delete) 
{
   if( Trigger.isAfter&& Trigger.isDelete)
       MemberCache_H_HUM process = new MemberCache_H_HUM(Trigger.old, MemberCache_H_HUM.triggeredAction.afterdelete);
}