/*******************************************************************************************************************************
Apex Class Name : HUMTaskHeirarchy_H
Version         : 1.0
Created On      : 10/21/2016
Function        : Helper Class to Update the Count for the Total_count__c and open_count__c for existing Data on cases
Test Class      : HUMTaskHeirarchy_H _Test 

Modification Log: 
* Developer Name              Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Vamsi Kondragunta				44606			  10/21/2016              Update the data for Req:72833
* Vishal Verma   				46667  			  11/07/2016              REQ - 289372 : Data Model Updates for Case and Task
*******************************************************************************************************************************/
public class HUMTaskHeirarchy_H 
{
    /*
     * This method used to Count the case fields Open and total tasks on task   
     * @ Param    :    List<Task> 
     * @ Return   :    none
     */
	public void CountofOpenTaskForParent(List<HUM_Task_Heirarchy__c> lstTaskNewValues)
    {
       
        Set<Id> setHumDepTaskId=new Set<Id>();
        List<Task> lstUpdateTasks = new List<Task>();
        Task tempTask;
        Map<String,List<Task>> mapTotalTaskChilds = new Map<String,List<Task>>();
        Map<String,List<Task>> mapOpenTaskChilds = new Map<String,List<Task>>();
        Map<String,String> mapChildParent = new Map<String,String>();
        Boolean bUpdate = false; 
        Set<Id> setParentTskIds = new Set<Id>();
        String sClosed = HUMConstants__c.getInstance('HUMClosed').StringValue__c;
    	String sWorkTask = HUMConstants__c.getInstance('HUMWorkTask').StringValue__c;
        
        for(HUM_Task_Heirarchy__c tsk:lstTaskNewValues)
        {
            setHumDepTaskId.add(tsk.HUM_Dependent_TaskID__c);
        }
        List<HUM_Task_Heirarchy__c> lstTaskHeirarchy = [select id, Hum_CaseID__c, HUM_Dependent_TaskID__c, HUM_Parent_TaskID__c, Immediate_Parent__c, Level__c from HUM_Task_Heirarchy__c where HUM_Dependent_TaskID__c in: setHumDepTaskId];
        For(HUM_Task_Heirarchy__c tskH : lstTaskHeirarchy)
        {
            setParentTskIds.add(tskH.HUM_Parent_TaskID__c);
        }
        
        Set<String> setParentWorkTskIds = new Set<String>();
        for(Task tsk : [Select Id from task Where Id IN : setParentTskIds and Type =: sWorkTask])
        {
            setParentWorkTskIds.add(tsk.Id);
        }
        
        for(HUM_Task_Heirarchy__c tskH : [select id, Hum_CaseID__c, HUM_Dependent_TaskID__c, HUM_Parent_TaskID__c, Immediate_Parent__c, Level__c from HUM_Task_Heirarchy__c where HUM_Parent_TaskID__c in: setParentWorkTskIds])
        {
            mapChildParent.put(tskH.HUM_Dependent_TaskID__c, tskH.HUM_Parent_TaskID__c);
        }
        
        for(Task tsk : [select id,status from Task where id in :mapChildParent.keySet()])
        {
            if(mapChildParent.containsKey(tsk.id))
            {
                if(mapTotalTaskChilds.containsKey(mapChildParent.get(tsk.id)))
                {
                    mapTotalTaskChilds.get(mapChildParent.get(tsk.id)).add(tsk);
                }
                else mapTotalTaskChilds.put(mapChildParent.get(tsk.id),new Task[]{tsk});
                
                if(tsk.status != sClosed)
                {
                	if(mapOpenTaskChilds.containsKey(mapChildParent.get(tsk.id)))
                    {
                        mapOpenTaskChilds.get(mapChildParent.get(tsk.id)).add(tsk);
                    }	
                  	else mapOpenTaskChilds.put(mapChildParent.get(tsk.id),new Task[]{tsk});
                }
            }        
        }
        
        for(String parentTaskId : mapTotalTaskChilds.keySet())
        {
            tempTask = new Task();
            tempTask.Id=parentTaskId;
            tempTask.Open_Work_Task_Count__c = (mapOpenTaskChilds.containsKey(parentTaskId)) ? returnMaxValue(mapOpenTaskChilds.get(parentTaskId).Size()) : 0;
            tempTask.Total_Work_Task_Count__c = (mapTotalTaskChilds.containsKey(parentTaskId)) ? returnMaxValue(mapTotalTaskChilds.get(parentTaskId).Size()) : 0;
            lstUpdateTasks.add(tempTask);
        }
        
        if(lstUpdateTasks.size()>0)  update lstUpdateTasks; 
     }
     
     /*
     * This method used to get the Max Count for Open Work Task and Total WOrk Task   
     * @ Param    :    Integer  
     * @ Return   :    Integer
     */
     private Integer returnMaxValue(Integer iCount)
     {
     	if(iCount> GLOBAL_CONSTANT_HUM.NUMBER_MAXWORKTASK_HUM)
     	{
     		return GLOBAL_CONSTANT_HUM.NUMBER_MAXWORKTASK_HUM;
     	}
     	return iCount;
     }
}