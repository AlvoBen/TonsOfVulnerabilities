/*******************************************************************************************************************************
Apex Class Name : CaseWQTransfer_H_HUM 
Version         : 1.0
Created On      : 08/14/2018
Function        : Used for as helper class for CaseUpdateWorkQueue_B_HUM && TaskUpdateWorkQueue_B_HUm
Test Class      :  

Modification Log: 
* Developer Name              Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Prathyu Tushar               272718                   08/14/2018              To update the Service Center, Department, Owner Queue & Topic based on Edit or Deactivation of Work Queue
* Prathyu Tushar               275969                   08/24/2018              Defect fix 379755.0001 Task Queueu Names are corrected before populating the Views 
* Santhi Mandava                                        10/15/2020                 Passing loggedin users profile name to populateviewname method
* Jasmeen Shangari                                      02/22/2021              CheckMarx fix -update class with sharing
*******************************************************************************************************************************/

public with sharing class CaseWQTransfer_H_HUM {
    
    public List<string> populateViewChanges(){
        List<string> lstQueueNames = new List<String>();
        HUMQueueViewBatchSettings__c objBatchHelper = HUMQueueViewBatchSettings__c.getValues('WorkQueueTransferChanges');
        DateTime dtEndDate=DateTime.Now();
        DateTime dtStartDate = objBatchHelper.BatchExecutionDate__c;
        Set<String> setFieldNames = new Set<String>{'IsActive__c', 'Criteria_Updated__c', 'created'};
        List<Queue_View__History> lstQueueViewHistory = [select ParentId, Parent.Work_Queue_Name__r.Name from Queue_View__History where field in :setFieldNames and createddate > : dtStartDate and createddate<= : dtEndDate];
        try{
            for(Queue_View__History oQVH : lstQueueViewHistory){
                if(!lstQueueNames.contains(oQVH.Parent.Work_Queue_Name__r.Name)) lstQueueNames.add(oQVH.Parent.Work_Queue_Name__r.Name);
            }
            List<String> lstCriteriaChanges = populateCriteriaChanges(dtStartDate);
            lstQueueNames.addAll(lstCriteriaChanges); 
        }catch(Exception ex){
            HUMExceptionHelper.logErrors(ex, 'CaseWQTransfer_H_HUM', 'populateViewChanges');
        }
        return lstQueueNames;
    }
    
    public List<String> populateCriteriaChanges(DateTime dtStartDate){
        DateTime dtEndDate=DateTime.Now();
        List<string> lstQueueNames = new List<String>();
        List<Work_Queue_Priority__History> lstWQPriorityHistory = [SELECT Id, Parent.Work_Queue_Name__r.Name FROM Work_Queue_Priority__History where createddate > : dtStartDate and createddate<= : dtEndDate];
        try{
            for(Work_Queue_Priority__History oWQPH : lstWQPriorityHistory){
                if(!lstQueueNames.contains(oWQPH.Parent.Work_Queue_Name__r.Name)) lstQueueNames.add(oWQPH.Parent.Work_Queue_Name__r.Name);
            }   
        }catch(Exception ex){
            HUMExceptionHelper.logErrors(ex, 'CaseWQTransfer_H_HUM', 'populateCriteriaChanges');
        }
        return lstQueueNames;
    }
        
    public  Map<String,Map<String,Object>> PopulateWQChanes(){
        HUMQueueViewBatchSettings__c objBatchHelper = HUMQueueViewBatchSettings__c.getValues('WorkQueueTransferChanges');
        DateTime dtEndDate=DateTime.Now();
        DateTime dtStartDate = objBatchHelper.BatchExecutionDate__c;
        Set<String> setFieldNames = new Set<String>{'Service_Center__c','Department__c','Name'};
        Map<String,Map<String,Object>> mapFinalChanges = new Map<String,Map<String,Object>>();
        Boolean bFinalName = false;
        List<case> lstcases = new List<case>();
        List<Work_Queue_Setup__History> lstSetupHistory = [SELECT Id, ParentId,Parent.Name, Field, OldValue, NewValue, createddate FROM Work_Queue_Setup__History where createddate > : dtStartDate AND createddate<= : dtEndDate Order By ParentId,Field,createddate DESC];
        try{
            if(!lstSetupHistory.IsEmpty()){
                String sParentQId = lstSetupHistory[0].ParentId;
                String sParentQName =  '';
                
                if(lstSetupHistory[0].Field != 'Name')
                {
                    sParentQName = lstSetupHistory[0].Parent.Name;
                }
                else
                {
                    sParentQName = (string)lstSetupHistory[0].get(Schema.work_queue_setup__history.oldvalue);
                    bFinalName = true;
                }
                
                String sFieldName = lstSetupHistory[0].Field;
                Map<String,Object> mapTemp = new Map<String,Object>();
                If(setFieldNames.Contains(lstSetupHistory[0].Field)) mapTemp.put((String)lstSetupHistory[0].Field,lstSetupHistory[0].NewValue);
                For(Work_Queue_Setup__History obj : lstSetupHistory)
                {
                    If(sParentQId == obj.ParentId)
                    {
                       IF(obj.Field == 'Name' && !bFinalName){
                            sParentQName = (string)obj.get(Schema.work_queue_setup__history.oldvalue);
                            bFinalName = true;
                       }        
                       If(setFieldNames.Contains(obj.Field) && sFieldName != obj.Field)
                       {
                           mapTemp.put((String)obj.Field,obj.NewValue);
                                           sFieldName = obj.Field;
                       } 
                    }
                    else
                    {
                        If(mapTemp != Null && !mapTemp.isEmpty()) 
                        {
                            mapFinalChanges.put(sParentQName,mapTemp);
                        }
                        mapTemp = new Map<String,Object>();
                        If(setFieldNames.Contains(obj.Field)) mapTemp.put((String)obj.Field,obj.NewValue);
                        sFieldName = obj.Field;
                        sParentQId = obj.ParentId;
                        IF(obj.Field != 'Name')
                        {
                            sParentQName = obj.Parent.Name;
                            bFinalName = false;
                        }
                        else
                        {
                            sParentQName = (string)obj.get(Schema.work_queue_setup__history.oldvalue);
                        }
                    } 
                }
                If(mapTemp != Null && !mapTemp.isEmpty()) mapFinalChanges.put(sParentQName,mapTemp);    
            }
        }catch(exception e){
            HUMExceptionHelper.logErrors(e, 'CaseWQTransfer_H_HUM', 'PopulateWQChanes');    
        }
        return mapFinalChanges;
    }
        
    public Map<String,Map<String,WQS_Case_Transfer_History__c>> PopulateWQTransferChanges(){
        HUMQueueViewBatchSettings__c objBatchHelper = HUMQueueViewBatchSettings__c.getValues('WorkQueueTransferChanges');
        DateTime dtEndDate=DateTime.Now();
        DateTime dtStartDate = objBatchHelper.BatchExecutionDate__c;
        String sStatus = 'In Progress';
        Map<String,Map<String,WQS_Case_Transfer_History__c>> mapUniqueValues = new Map<String,Map<String,WQS_Case_Transfer_History__c>>();
        Map<String,WQS_Case_Transfer_History__c> mapViews = new Map<String,WQS_Case_Transfer_History__c>();
        List<WQS_Case_Transfer_History__c> lstTrasferHistory = [SELECT Id, ParentId__c, ParentId__r.Name, From_Queue_View__c, To_Work_Queue__c, To_Work_Queue_Topic__c, Transfer_Status__c, Work_Queue_Setup_Name__c, Service_Center__c, Department__c, Topic__c from WQS_Case_Transfer_History__c Where Transfer_Status__c =: sStatus and createddate > : dtStartDate AND createddate<= : dtEndDate order by ParentId__c,From_Queue_View__c,createddate];
        try{
            if(!lstTrasferHistory.IsEmpty()){
                String sParentQId = lstTrasferHistory[0].ParentId__c;
                String sParentQName = lstTrasferHistory[0].ParentId__r.Name;
                String sFieldName = lstTrasferHistory[0].From_Queue_View__c;
                
                mapViews.put(sFieldName,lstTrasferHistory[0]);
                For(WQS_Case_Transfer_History__c obj : lstTrasferHistory)
                {
                    If(sParentQId == obj.ParentId__c)
                    {
                       If(sFieldName != obj.From_Queue_View__c)
                       {
                           mapViews.put((String)obj.From_Queue_View__c,obj);
                                           sFieldName = obj.From_Queue_View__c;
                       } 
                    }
                    else
                    {
                        if(mapUniqueValues.containsKey(sParentQId))
                        {
                            //Map<String,WQS_Case_Transfer_History__c> mapTempVals = mapFinalChanges.get(sParentQId);
                                    
                        }
                        else
                        {
                            mapUniqueValues.put(sParentQName,mapViews);
                        }
                        
                        mapViews = new Map<String,WQS_Case_Transfer_History__c>();
                        mapViews.put((String)obj.From_Queue_View__c,obj);
                        sFieldName = obj.From_Queue_View__c;
                        sParentQId = obj.ParentId__c;
                        sParentQName = obj.ParentId__r.Name;
                        
                    } 
                }
                mapUniqueValues.put(sParentQName,mapViews);
            }
        }catch(exception e){
            HUMExceptionHelper.logErrors(e, 'CaseWQTransfer_H_HUM', 'PopulateWQTransferChanges');   
        }
        return mapUniqueValues;
    }   
        
        
    public void WQCaseChanges(List<case> lstCase, Map<String,Map<String,Object>> mapFinalChanges, Map<String,Map<String,WQS_Case_Transfer_History__c>> mapUniqueValues){
        HUMUpdateCaseHelper oCaseHelper = new HUMUpdateCaseHelper();
        Set<string> setQueueNames = new Set<String>();
        try{
            For(Case oCase : lstCase)
            {
                Map<String,Object> mapQVals ;
                If(mapUniqueValues.containsKey(oCase.Owner_Queue__c))
                {
                    mapQVals = mapUniqueValues.get(oCase.Owner_Queue__c);
                    For(String strView : mapQVals.keySet())
                    {
                        If(oCase.Work_Queue_View_Name__c == strView)
                        {
                            WQS_Case_Transfer_History__c objHis = (WQS_Case_Transfer_History__c)mapQVals.get(strView);
                            oCase.put('Topic__c',objHis.get('Topic__c'));
                            oCase.put('Service_Center__c',objHis.get('Service_Center__c'));
                            oCase.put('Department__c',objHis.get('Department__c'));
                            String sWQName = (String)objHis.get('Work_Queue_Setup_Name__c');
                            if(oCase.Case_Owner__c == oCase.Owner_Queue__c)
                                oCase.put('Case_Owner__c',sWQName);
                            oCase.put('Owner_Queue__c',sWQName);
                        }
                    }
                }
                else If(mapFinalChanges.containsKey(oCase.Owner_Queue__c))
                {
                    mapQVals = mapFinalChanges.get(oCase.Owner_Queue__c);
                    mapQVals = mapFinalChanges.get(oCase.Owner_Queue__c);
                    For(String strView : mapQVals.keySet())
                    {
                        If(strView == 'Name')
                        {
                            String sWQName = (String)mapQVals.get(strView);
                            if(oCase.Case_Owner__c == oCase.Owner_Queue__c)
                                oCase.put('Case_Owner__c',sWQName);
                            oCase.put('Owner_Queue__c',mapQVals.get(strView));
                        }
                        else
                        {
                            oCase.put(strView,mapQVals.get(strView));
                        }
                    }
                }else{}
                if(!setQueueNames.contains(oCase.Owner_Queue__c))
                    setQueueNames.add(oCase.Owner_Queue__c);
            }
            String profileName = HumUtilityHelper.getCurrentUserProfileName();
            HUMUpdateCaseHelper.populateViewName(lstCase, false,setQueueNames,profileName);
        }catch(Exception e){
            HUMExceptionHelper.logErrors(e, 'CaseWQTransfer_H_HUM', 'WQCaseChanges');
        }
    }
    
    public void WQTaskChanges(List<Task> lstTasks, Map<String,Map<String,Object>> mapFinalChanges, Map<String,Map<String,WQS_Case_Transfer_History__c>> mapUniqueValues){
        HUMUpdateTaskHelper oTaskHelper = new HUMUpdateTaskHelper();
        Set<string> setQueueNames = new Set<String>();
        try{
            For(Task oTask : lstTasks)
            {
                Map<String,Object> mapQVals ;
                If(mapUniqueValues.containsKey(oTask.Owner_Queue__c))
                {
                    mapQVals = mapUniqueValues.get(oTask.Owner_Queue__c);
                    For(String strView : mapQVals.keySet())
                    {
                        If(oTask.Work_Queue_View_Name__c == strView)
                        {
                            WQS_Case_Transfer_History__c oWQSTH = (WQS_Case_Transfer_History__c)mapQVals.get(strView);
                            if(oWQSTH.get('Topic__c') != 'NA' )
                                oTask.put('Topic__c',oWQSTH.get('Topic__c'));
                            else
                                oTask.put('Topic__c','None');
                            oTask.put('Service_Center__c',oWQSTH.get('Service_Center__c'));
                            oTask.put('Department__c',oWQSTH.get('Department__c'));
                            String sWQName = (String)oWQSTH.get('Work_Queue_Setup_Name__c');
                            if(oTask.Task_Owner__c == oTask.Owner_Queue__c)
                                oTask.put('Task_Owner__c',sWQName);
                            oTask.put('Owner_Queue__c',sWQName);
                        }
                    }
                }
                else If(mapFinalChanges.containsKey(oTask.Owner_Queue__c))
                {
                    mapQVals = mapFinalChanges.get(oTask.Owner_Queue__c);
                    mapQVals = mapFinalChanges.get(oTask.Owner_Queue__c);
                    For(String strView : mapQVals.keySet())
                    {
                        If(strView == 'Name')
                        {
                            String sWQName = (String)mapQVals.get(strView);
                            if(oTask.Task_Owner__c == oTask.Owner_Queue__c)
                                oTask.put('Task_Owner__c',sWQName);
                            oTask.put('Owner_Queue__c',mapQVals.get(strView));
                        }
                        else
                        {
                            oTask.put(strView,mapQVals.get(strView));
                        }
                    }
                }else{}
                if(!setQueueNames.contains(oTask.Owner_Queue__c))
                    setQueueNames.add(oTask.Owner_Queue__c);
            }
            oTaskHelper.populateViewName(lstTasks, false,setQueueNames);
            
        }catch(Exception e){
            HUMExceptionHelper.logErrors(e, 'CaseWQTransfer_H_HUM', 'WQCaseChanges');
        }
    }
}