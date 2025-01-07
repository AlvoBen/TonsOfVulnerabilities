/*****************************************************************************************************************************************************************************************************************************************************
Apex Class Name  : QueueViewItemCreation_D_HUM
Version          : 1.0
Created Date     : June 01 2016
Function         : Class to create the Queue View Items based on the Queue View & Queue View Setup.
Test Class       : QueueViewItemCreation_T_HUM
 ******************************************************************************************************************************************************************************************************************************************************

Modification Log:

 * Developer Name            Review Number            Date                       Description
-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 * Sharan Shanmugam             29395              06/01/2016                   Original Version
 * Suresh Sunkari              339349              02/22/2019			        REQ - 377482 Report on Business Segments within the platform - ALOB
 * Vinay Lingegowda                                04/30/2021			        T1PRJ0001827- IR 5050 - Update Queue View - Details, New and Edit page - Add SLA fields (CRM)
 * Prudhvi Pamarthi                                07/30/2021                   US#2494738 Fix for INC1197867 Service Center and Department not visible on View Detail screen
 *****************************************************************************************************************************************************************************************************************************************************/

Public with sharing class QueueViewItemCreation_D_HUM
{
     /**
    * Returns list of group names
    * <p>
    * This method is used get group Name.
    *
    * @param id           pgvId 
    * @return             List<Group>
    */
    public List<Group> groupName(id pgvId)
    {
        return [Select Name, developerName from Group where Id =: pgvId];  
    }
    
     /**
    * Returns Queue_View__c record details
    * <p>
    * This method is used get queue info.
    *
    * @param id           sQVId 
    * @return             Queue_View__c
    */
    public Queue_View__c queueInfo(id sQVId)
    {
        return [Select Name,Accountable_Line_of_Business__c,View_Manager__c,Public_Group_View_Id__c,Work_Queue_Name__r.Name,SLA_In_Days__c,SLA_On_Case_Transfer__c, Work_Queue_Name__r.SLA_In_Days__c from Queue_View__c where Id =: sQVId];  
    }
    
     /**
    * Returns list of WQ name and ids.
    * <p>
    * This method is used get wq related list conponents.
    *
    * @param id           sWQId 
    * @return             List<Work_Queue_Setup__c>
    */
    public Work_Queue_Setup__c getWorkQueue(id sWQId)
    {
        return [Select id, Name,Service_Center__c,Department__c from Work_Queue_Setup__c where id =: sWQId LIMIT 1];
    }
    
     /**
    * Returns list of QVs for the particular WQ.
    * <p>
    * This method is used get QV records.
    *
    * @param id           sWQId 
    * @return             List<Queue_View__c>
    */
    public List<Queue_View__c> queueWQ(id sWQId)
    {
        return [Select id,name,Public_Group_View_Id__c from Queue_View__c where Work_Queue_Name__c =: sWQId];
    }
    
     /**
    * Returns list of QVIs for the particular QV.
    * <p>
    * This method is used get QVI records
    *
    * @param id           sQVId 
    * @return             List<Queue_View_Item__c>
    */
    public List<Queue_View_Item__c> getQVI(id sQVId)
    {
        return [Select ID, Name, Queue_View__c,API_Name__c,Field_Operator__c,Field_Value__c from Queue_View_Item__c where Queue_View__c =: sQVId];
    }
    
     /**
    * This method is used for upserting QV.
    *
    * @param List<Queue_View__c>           lstQV 
    * @return                              void
    */
    public void upsertQV(List<Queue_View__c> lstQV)
    {
        upsert lstQV;
    }
    
     /**
    * This method is used for upserting QVI.
    *
    * @param List<Queue_View_Item__c>           lstQWIRecords 
    * @return                                   void
    */
    public void upsertQVI(List<Queue_View_Item__c> lstQWIRecords)
    {
         upsert lstQWIRecords;
    }
    
     /**
    * This method is used for deleting QVI.
    *
    * @param List<Queue_View_Item__c>           lstDeleteQueueViewItem 
    * @return                                   void
    */
    public void deleteQVI(List<Queue_View_Item__c> lstDeleteQueueViewItem)
    {
          SObjectType myObj = Schema.getGlobalDescribe().get('Queue_View_Item__c');
         if(myObj.getDescribe().isDeletable()) 
         delete lstDeleteQueueViewItem;
    } 
    
     /**
    * This method is used for inserting group.
    *
    * @param Group           grp 
    * @return                void
    */
    public void insertGroup(Group grp)
    {
        insert grp;
    }  
    
     /**
    * This method is used for inserting group member.
    *
    * @param GroupMember           newGroupMember 
    * @return                      void
    */
    public static void insertGroupMember(GroupMember newGroupMember)
    {
        insert newGroupMember;
    }    
    
     /**
    * Returns QV info for the particular id
    * <p>
    * This method is used to get PGV.
    *
    * @param id           queueViewID 
    * @return             Queue_View__c
    */
    public static Queue_View__c getPGVQV(id queueViewID)
    {
        return [Select Work_Queue_Name__c, Public_Group_View_Id__c from Queue_View__c where id =: queueViewID];
    }   
    
     /**
    * Returns WQ PGQ for the paricular WQ id.
    * <p>
    * This method is used to get PGQ.
    *
    * @param id           sWQId 
    * @return             Work_Queue_Setup__c
    */
    public static Work_Queue_Setup__c getPGQWQ(id sWQId)
    {
        return [Select Public_Group_Queue_Id__c from Work_Queue_Setup__c where id =: sWQId];
    }  
    
     /**
    * This method is used to update group
    *
    * @param Group           queueViewID 
    * @return                void
    */
    public void updateGroup(Group grp)
    {
        Database.SaveResult[] lstSaveResult = Database.update(new List<Group> {grp},false);
        HUMExceptionHelper.bShowErrorOnPage = false;
        Boolean berror = HUMExceptionHelper.processSaveResults(lstSaveResult, new List<Group> {grp}, 'QueueViewItemCreation_D_HUM', 'updateGroup', 'Group');
        if(berror) ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.ERROR, Label.HUMPGVError));
    }  
    
     /**
    * This method is used for updating QV.
    *
    * @param List<Queue_View__c>           lstQV 
    * @return                              void
    */
    public void updateQV(List<Queue_View__c> lstQV)
    {
        update lstQV;
    } 
    
    /**
    * Returns boolean to check the whether group exists or not 
    * <p>
    * Method to validate and create group
    *
    * @param grpNameCheck         string 
    * @return                	  Boolean
    */
    public Boolean checkGrp (string grpNameCheck)
	{							
		List<Group> lstGrp = new List<Group>();
		lstGrp = [Select Name,Developername from Group Where developerName = :grpNameCheck.trim().replaceAll(' ','_')];
		return (!lstGrp.isEmpty());
	}
	
}