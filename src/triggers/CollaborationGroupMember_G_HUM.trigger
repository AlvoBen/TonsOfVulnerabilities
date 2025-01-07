/*******************************************************************************************************************************
Apex Trigger Name : CollaborationGroupMember_G_HUM 
Version           : 1.0
Created On        : 09/15/2016
Function          : This serves as Trigger on the CollaborationGroupMember object to restrict user to unfollow from Brodcast group.
                
Modification Log: 
* Developer Name           Code Reveiw                 Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------
* Rajesh Keswani            39866                   09/15/2016                 Original Version
--------------------------------------------------------------------------------------------------------------------------------------*/

trigger CollaborationGroupMember_G_HUM on CollaborationGroupMember (before delete)
{
    if(Trigger.isBefore && Trigger.isDelete)
    {
        CollaborationGrpMemberTriggerHandler_HUM.processCollaborationGrpMemberBeforeDelete(Trigger.old);
    }    
}