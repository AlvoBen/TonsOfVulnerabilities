/**************************************************************************** 
Apex Class Name  : CollaborationGrpMemberTriggerHandler_HUM   
Version          : 1.0  
Created Date     : Sep 15, 2016
Function         : This serves as Handler for CollaborationGroupMember Trigger.  
****************************************************************************
Modification Log:
* Developer Name          Code Review #        		 Date                       Description
*-------------------------------------------------------------------------------------------------------------
* Rajesh keswani          	39866                   09/15/2016                 Original Version
----------------------------------------------------------------------------------------------------------------------*/

public with sharing class CollaborationGrpMemberTriggerHandler_HUM
 {  
    /*
     * Method Name   :   processCollaborationGrpMemberBeforeDelete
     * Description   :   This method is used to restrict User to unfollow the chatter group
     * Return Type   :   void
     * Parameters    :   List<CollaborationGroupMember> 
     */    
    public static void processCollaborationGrpMemberBeforeDelete(List<CollaborationGroupMember> lstOldCollaborationGroupMembers)
    {
    	List<CollaborationGroup>  grpObj;
    	if(Test.isRunningTest())
    	{
    		grpObj = [SELECT Id, Name FROM CollaborationGroup where name = :GLOBAL_CONSTANT_HUM.CHATTERGROUPNAMETEST_HUM limit 1];
    	}
    	else
    	{
    		grpObj = [SELECT Id, Name FROM CollaborationGroup where name = :GLOBAL_CONSTANT_HUM.CHATTERGROUPNAME_HUM limit 1];
    	}
        if(grpObj != null && grpObj.size() > 0)
        {
            for(CollaborationGroupMember cmg : lstOldCollaborationGroupMembers)
            {
                if(cmg.CollaborationGroupId == grpObj[0].id)
                {
                    cmg.addError(GLOBAL_CONSTANT_HUM.UNFOLLOWERRORMSG_HUM);
                }
            }
        }
    }
}