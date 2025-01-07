/*****************************************************************************************
Apex class Name    : QueueViewsWithItems_C_HUM 
Version            : 1.0
Function           : This is the main class for displaying Queue Views and Queue View Items on VF page using Data table extension.
Created Date       : 09/05/2018 
Test Class         : QueueViewsWithItems_T_HUM 

Modification Log:
*   Developer                   Code Review             Date                Description
* ------------------------------------------------------------------------------------------------------------------------------   
*  Pavan Karnam                 329420                   04/01/2019            REQ-372715 Data table extensions for Queue Views 
*  Prudhvi Pamarthi                                      04/08/2021            US#2163299 Checkmarx scan issue fix
*  Prasuna Pattabhi                                      09/01/2021            US#2548428 Checkmarx scan issue fix
*  Prasuna Pattabhi                                      09/09/2021            US#2548428 Checkmarx scan issue Changes Rollback
*  Prasuna Pattabhi                                      09/20/2021            US#2548428 Checkmarx scan issue fix
*********************************************************************************************************************************/

Public with sharing Class QueueViewsWithItems_C_HUM 
{     
    Public String sQueueViewData{Get;Set;}
    Public Id editId{Get;Set;}
    Public Id editqviId{Get;Set;}
    Public Id delqviId{Get;Set;}
    Public string URL{Get;Set;}  
    Public Id sWQSID{Get;Set;} 
    
    /*
* Method Name   :  QueueViewsWithItems_C_HUM()
* Description   :  This Is a constructor for class QueueViewsWithItems_C_HUM()
* Return Type   :  NA
* Parameters    :  NA
*/
    
    Public QueueViewsWithItems_C_HUM(ApexPages.StandardController controller)
    { 
        Id sWQSID = ApexPages.currentPage().getParameters().get('Id'); 
        List<Queue_View__c> lstQueueView = [SELECT id, Name FROM Queue_View__c WHERE Work_Queue_Name__c =: ESAPI.encoder().SFDC_HTMLENCODE(sWQSID) ];
        List<Queue_View_Item__c> lstQueueViewItems = [SELECT Id,API_Name__c,Field_Operator__c, Field_Value__c,LastModifiedById, LastModifiedBy.Name,LastModifiedDate,Queue_View__c, Queue_View__r.Name from Queue_View_Item__c where Queue_View__r.Id in :lstQueueView];
        List<QueueViewWrapper> lstFinalData = new List<QueueViewWrapper>();
        QueueViewWrapper objTemp;
        Map<String,List<Queue_View_Item__c>> mapQueueView = new Map<String,List<Queue_View_Item__c>>();
        for(Queue_View_Item__c oQueueViewItems : lstQueueViewItems)
        {
                List<Queue_View_Item__c> lstTemp = new List<Queue_View_Item__c>();
                lstTemp.add(oQueueViewItems);
                String sTempQVI = oQueueViewItems.Queue_View__c+'+'+oQueueViewItems.Queue_View__r.Name;
                if(mapQueueView.containsKey(sTempQVI))
                {
                                List<Queue_View_Item__c> lstAllViewItems = mapQueueView.get(sTempQVI);
                                lstAllViewItems.addAll(lstTemp);
                                mapQueueView.put(sTempQVI,lstAllViewItems);
                }
                else
                {
                                mapQueueView.put(sTempQVI,lstTemp);
                }
        }
        for(Queue_View__c oQV : lstQueueView)
        {
                string sTempQV = oQV.id+'+'+oQV.Name;
                if(mapQueueView.containsKey(sTempQV))
                {
                List<Queue_View_Item__c> lstQ = mapQueueView.get(sTempQV);
                objTemp = new QueueViewWrapper();
                objTemp.sViewName = oQV.Name;
                objTemp.sViewId = oQV.Id;
                objTemp.lstItems = new List<ViewItemWrapper>();
                ViewItemWrapper objItemTemp;
                for(Queue_View_Item__c oqvi : lstQ)
                  {
                    objItemTemp = new ViewItemWrapper(oqvi.Id,oqvi.API_Name__c ,oqvi.Field_Operator__c,oqvi.Field_Value__c,oqvi.LastModifiedById,oqvi.LastModifiedBy.Name,oqvi.LastModifiedDate);
                    objTemp.lstItems.Add(objItemTemp);
                  }
                 } 
                else{
                     objTemp = new QueueViewWrapper();
                     objTemp.sViewName = oQV.Name;
                     objTemp.sViewId = oQV.Id;
                     objTemp.lstItems = new List<ViewItemWrapper>();
                
                }
                  lstFinalData.Add(objTemp);     
        }
        sQueueViewData = JSON.serialize(lstFinalData);
        sQueueViewData = '{"data":'+sQueueViewData +'}';
    } 
    
 /*
* Method Name   :  newQueueView()
* Description   :  This method is to create a new Queue View while clicking on the New Queue View button on VF page
* Return Type   :  Null
* Parameters    :  NA
*/
    public pageReference newQueueView()
    {  
        Id sWQSID = ApexPages.currentPage().getParameters().get('Id');
        Work_Queue_Setup__c lstqws = [select Id,Name from Work_Queue_Setup__c where Id=:ESAPI.encoder().SFDC_HTMLENCODE(sWQSID) Limit 1];
        string workQueueName = lstqws.Name;
        string workQueueName1 = workQueueName.replace(' ','+');
        URL = '/apex/QueueViewItemCreation_VF_HUM?' + workQueueName + '=' + workQueueName1 + '&' + workQueueName + '_lkid=' + string.EscapeSingleQuotes(sWQSID) +'&scontrolCaching=1&retURL=%2F' + sWQSID + '&sfdc.override=1';
        return null;   
    } 
    
     /*
* Method Name   :  editQueueView()
* Description   :  This method is to edit Queue View while clicking on the edit button on VF page of a Queue View.
* Return Type   :  Null
* Parameters    :  NA
*/
    
    public pageReference editQueueView()
    {  
        Id sWQSID = ApexPages.currentPage().getParameters().get('Id');   
        URL='/apex/QueueViewItemCreation_VF_HUM?id=' + editId + '&retURL=' + string.EscapeSingleQuotes(sWQSID);
        return null;  
    }   
     
          /*
* Method Name   :  editQueueViewItem()
* Description   :  This method is to edit Queue View Item while clicking on the edit button on VF page of a Queue View Item.
* Return Type   :  Null
* Parameters    :  NA
*/
    
     public pageReference editQueueViewItem()
     {  
        Id sWQSID1 = ApexPages.currentPage().getParameters().get('Id');
        URL='/' + editqviId + '/e?retURL=' + string.escapeSingleQuotes(sWQSID1);
        return null;  
     } 
    
              /*
* Method Name   :  deleteQueueViewItem()
* Description   :  This method is to delete Queue View Item while clicking on the delete button on VF page of a Queue View Item.
* Return Type   :  Null
* Parameters    :  NA
*/ 
    
    
    public pageReference deleteQueueViewItem()
    { 
        Id sWQSID2 = ApexPages.currentPage().getParameters().get('Id');   
        Queue_View_Item__c delQVI= [SELECT Id,LastModifiedDate, LastModifiedById, Field_Operator__c, Field_Value__c, API_Name__c from Queue_View_Item__c where Id=:delqviId LIMIT 1];
        URL = '/' + string.escapeSingleQuotes(sWQSID2);
        if(Schema.getGlobalDescribe().get('Queue_View_Item__c').getDescribe().isDeletable()){ 
            delete delQVI; 
        }
        return null;  
    } 
    
    Public class QueueViewWrapper
    {
        Public String sViewName{get;set;}
        Public String sViewId{get;set;}
        List<ViewItemWrapper> lstItems{get;set;}
    }
    
    Public class ViewItemWrapper
    {
        Public String sViewItemId{get;set;}
        Public String sAPIName{get;set;}
        Public String sFOperator{get;set;}
        Public String sFValue{get;set;}
        Public Id sModfiedBy{get;set;}
        Public String sModfiedByName{get;set;}
        Public DateTime sModfiedDate{get;set;}
    
    public ViewItemWrapper(String sId,String api,String ope,String sval,Id modby,String modbyname, DateTime Moddate)
    {
            This.sViewItemId = sId;
            This.sAPIName = api; 
            This.sFOperator = ope;
            This.sFValue = sval;
            This.sModfiedBy = modby;
            This.sModfiedByName = modbyname;
            This.sModfiedDate = Moddate;
     }
   }
}