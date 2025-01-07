/*******************************************************************************************************************************
LWC JS Name : taskViewControllerHUM
Function    : This apex class is used for taskHistoryView and taskHistoryTable

Modification Log: 
Developer Name           Code Review                      Date                         Description
*--------------------------------------------------------------------------------------------------------------------------------
* Shailesh Bagade                                        22/06/22                       initial version
* Prasuna Pattabhi                                       09/21/23                       MCD Defect 8137 Fix
*********************************************************************************************************************************/
public with sharing class taskViewControllerHUM
{
    
    @testVisible private static Map<Id, Task_Field_History__c> mapIdTaskFieldHistory = NULL;
    @testVisible private static Task oTask = new Task();
    private static String sAccessibleFields = '';
    private static Map<String, HUMObject_History_Metadata__c> mapObjHisMetadata = new Map<String, HUMObject_History_Metadata__c>([Select name, Tracked_Field__c , Parent_Tracked_Object__c from HUMObject_History_Metadata__c]);
    public static string sTaskId{get;set;}
    public static List<Task_Field_History__c> lstFieldUpdated{get;set;}
    public static Map<DateTime, String> mapQueue {get; set;}
    
    /*
    * Method Name   :    getTaskFieldTracking
    * Description   :    This method fetches the task history record based on record
    * Return Type   :    List<wrapperView>
    * Parameters    :    String recId
    */
    @AuraEnabled
    public static List<wrapperView> getTaskFieldTracking(String recId)
    {
        
        try {
            sTaskId = recId;
            List<String> trackingFieldValues = new List<String>
                                                    {'ActivityDate','Priority',
                                                    'Status','Type','Task_Owner__c',
                                                    'Owner_Queue__c','Work_Queue_View_Name__c',
                                                    'LastModifiedby_Queue__c','Credentialing_Task__c','Task_Completion_Date__c'};
            getAccessibleFields();
            String sQuery = 'Select Task_Number__c, ';
            sQuery += sAccessibleFields;
            sQuery += ' From Task ' +
                    ' Where Id =: sTaskId' +
                    ' Limit 1';
            sQuery = string.EscapeSingleQuotes(sQuery);
            oTask = Database.query(sQuery);
            
            lstFieldUpdated = [Select Created_At__c, SObjFieldName__c, SObjFieldValue__c, CreatedById, CreatedBy.Name,
                                ParentFieldHistory_Id__c from Task_Field_History__c 
                                Where SObjID__c =: sTaskId AND 
                                SObjFieldName__c IN :trackingFieldValues
                                ORDER BY Created_At__c DESC ];
            
            mapIdTaskFieldHistory = new Map<Id, Task_Field_History__c>();

            List<wrapperView>  lstProcessedTrackingList  = trackHistoryData();
            List<wrapperView>  lstFinalTrackingList = new List<wrapperView>();

           
           if(lstProcessedTrackingList.size() > 0){
                return lstProcessedTrackingList;
            }
            else{
                return null;
            }
        }
        catch (Exception ex) {
            HUMExceptionHelper.logErrors(ex, 'HUMTaskHistoryController', 'getTaskFieldTracking'); 
            throw new HUMCustomException(ex.getMessage());
            
        }
    }

    /*
    * Method Name   :    getAccessibleFields
    * Description   :    This method is used for getting all the fields from HUMObject_History_Metadata__c custom settings
                         that are being tracked on the Task object. 
    * Return Type   :    void
    * Parameters    :    NA
    */
    @testVisible private static void getAccessibleFields()
    {
        for (String sHisObj : mapObjHisMetadata.keySet()) 
        {
            sAccessibleFields += mapObjHisMetadata.get(sHisObj).Tracked_Field__c + ', ';
        }
        sAccessibleFields = sAccessibleFields.subString(Integer.valueOf(HUMConstants__c.getInstance('TaskHistoryZero').StringValue__c), sAccessibleFields.LastIndexOf(','));
        sAccessibleFields = string.escapeSingleQuotes(sAccessibleFields);
    }
    
    /*
    * Method Name   :    trackHistoryData
    * Description   :    Used to fetch tracked field data from Task Field History object. 
    * Return Type   :    List<wrapperView>
    * Parameters    :    NA
    */
    public static List<wrapperView> trackHistoryData()
    {
        List<Task_Field_History__c> lstOfUpdatedFields = lstFieldUpdated.deepClone();
        String sOldDueDate = '';
            
        for (Task_Field_History__c b : lstOfUpdatedFields)
        {
            mapIdTaskFieldHistory.put(b.ParentFieldHistory_Id__c, b);
        }
        Task_Field_History__c oTrackFieldHistory = new Task_Field_History__c();
        List<Schema.DescribeSObjectResult> describeSobjectsResult = Schema.describeSObjects(new List<String>{System.Label.HUMTrackedTaskObject});
        Map<String, Schema.SObjectField> mapAllFields = describeSobjectsResult[Integer.valueOf(HUMConstants__c.getInstance('TaskHistoryZero').StringValue__c)].fields.getMap();

        for(Task_Field_History__c oTFH : lstFieldUpdated) 
        {
            if(mapIdTaskFieldHistory.containskey(oTFH.id))
            {
                oTrackFieldHistory = mapIdTaskFieldHistory.get(oTFH.id);
                
                String sFieldLabel = mapAllFields.get(oTFH.SObjFieldName__c).getDescribe().getLabel();
                
                if(oTFH.SObjFieldName__c != System.Label.HUMLastModifiedQueue)
                {
                    if(oTFH.SObjFieldName__c == HUMConstants__c.getInstance('TaskActivityDate').StringValue__c)
                        getActivityDate(oTFH, oTrackFieldHistory);

                    if(string.isEmpty(oTFH.SObjFieldValue__c) && (!string.isEmpty(oTrackFieldHistory.SObjFieldValue__c)))
                        oTFH.SObjFieldValue__c = HUMConstants__c.getInstance('TaskHistoryChanged').StringValue__c + '  ' + sFieldLabel + '  ' + HUMConstants__c.getInstance('TaskHistoryTo').StringValue__c + '  ' + oTrackFieldHistory.SObjFieldValue__c;
                    else
                    {
                        if((!string.isEmpty(oTFH.SObjFieldValue__c)) && (!string.isEmpty(oTrackFieldHistory.SObjFieldValue__c)))
                            oTFH.SObjFieldValue__c = HUMConstants__c.getInstance('TaskHistoryChanged').StringValue__c + '  ' + sFieldLabel + '  '  + HUMConstants__c.getInstance('TaskHistoryFrom').StringValue__c + '  '  + oTFH.SObjFieldValue__c + '  '  + HUMConstants__c.getInstance('TaskHistoryTo').StringValue__c + '  ' + oTrackFieldHistory.SObjFieldValue__c;
                        if((!string.isEmpty(oTFH.SObjFieldValue__c)) && string.isEmpty(oTrackFieldHistory.SObjFieldValue__c))
                            oTFH.SObjFieldValue__c = HUMConstants__c.getInstance('TaskHistoryDeleted').StringValue__c + '  '  + oTFH.SObjFieldValue__c + '  '  + HUMConstants__c.getInstance('TaskHistoryIn').StringValue__c + '  '  + sFieldLabel;        
                    }
                }
            }
            else
            {   
                String sFieldLabel = mapAllFields.get(oTFH.SObjFieldName__c).getDescribe().getLabel(); 
                if(oTFH.SObjFieldName__c != System.Label.HUMLastModifiedQueue)
                {
                    if(oTFH.SObjFieldName__c == HUMConstants__c.getInstance('TaskActivityDate').StringValue__c)
                    {
                        if(!string.isEmpty(oTFH.SObjFieldValue__c))  
                            oTFH.SObjFieldValue__c = DateTime.valueOf(oTFH.SObjFieldValue__c).format(HUMConstants__c.getInstance('TaskHistoryDateFormat').StringValue__c);

                        if(oTask.get(oTFH.SObjFieldName__c) != NULL) 
                        {
                            Date dtTaskDate = Date.valueOf(oTask.get(oTFH.SObjFieldName__c));
                            Datetime dtTaskDateTime = datetime.newInstance(dtTaskDate.year(), dtTaskDate.month(),dtTaskDate.day());
                            String sTaskDueDate = DateTime.valueOf(dtTaskDateTime).format(HUMConstants__c.getInstance('TaskHistoryDateFormat').StringValue__c);
                            
                            if(string.isEmpty(oTFH.SObjFieldValue__c))    
                                oTFH.SObjFieldValue__c = HUMConstants__c.getInstance('TaskHistoryChanged').StringValue__c + '  '  + sFieldLabel + '  ' + HUMConstants__c.getInstance('TaskHistoryTo').StringValue__c + '  '  + sTaskDueDate; 
                            else
                                oTFH.SObjFieldValue__c = HUMConstants__c.getInstance('TaskHistoryChanged').StringValue__c + '  '  + sFieldLabel + '  '  + HUMConstants__c.getInstance('TaskHistoryFrom').StringValue__c + '  '  + oTFH.SObjFieldValue__c + '  '  + HUMConstants__c.getInstance('TaskHistoryTo').StringValue__c + '  '  + sTaskDueDate;     
                        }
                        else
                        {
                            if(!string.isEmpty(oTFH.SObjFieldValue__c))
                            oTFH.SObjFieldValue__c = HUMConstants__c.getInstance('TaskHistoryDeleted').StringValue__c + '  '  +  oTFH.SObjFieldValue__c + '  '  + HUMConstants__c.getInstance('TaskHistoryIn').StringValue__c + '  '  + sFieldLabel;
                        }
                    }                        
                    else
                    {
                        if(string.isEmpty(oTFH.SObjFieldValue__c) && oTask.get(oTFH.SObjFieldName__c) != NULL)    
                            oTFH.SObjFieldValue__c = HUMConstants__c.getInstance('TaskHistoryChanged').StringValue__c + '  '  + sFieldLabel + '  ' + HUMConstants__c.getInstance('TaskHistoryTo').StringValue__c + '  '  + oTask.get(oTFH.SObjFieldName__c); 
                        else
                        {
                        if((!string.isEmpty(oTFH.SObjFieldValue__c)) && oTask.get(oTFH.SObjFieldName__c) != NULL)
                            oTFH.SObjFieldValue__c = HUMConstants__c.getInstance('TaskHistoryChanged').StringValue__c + '  '  + sFieldLabel + '  '  + HUMConstants__c.getInstance('TaskHistoryFrom').StringValue__c + '  '  + oTFH.SObjFieldValue__c + '  '  + HUMConstants__c.getInstance('TaskHistoryTo').StringValue__c + '  '  + oTask.get(oTFH.SObjFieldName__c); 
                            
                        if((!string.isEmpty(oTFH.SObjFieldValue__c)) && oTask.get(oTFH.SObjFieldName__c) == NULL)
                            oTFH.SObjFieldValue__c = HUMConstants__c.getInstance('TaskHistoryDeleted').StringValue__c + '  '  +  oTFH.SObjFieldValue__c + '  '  + HUMConstants__c.getInstance('TaskHistoryIn').StringValue__c + '  '  + sFieldLabel;        
                        }
                    }   
                }
            }
        }
        
        removeBlankValues(lstFieldUpdated);
        return trackLastModifiedQueue(lstFieldUpdated);        
    }
    
    /*
    * Method Name   :    getActivityDate
    * Description   :    Used to get Date from DateTime field
    * Return Type   :    void
    * Parameters    :    Task_Field_History__c object containing old value,Task_Field_History__c  containing new value
    */
    @testVisible private static void getActivityDate(Task_Field_History__c oTFH, Task_Field_History__c oTrackFieldHistory)
    {
        if(!string.isEmpty(oTFH.SObjFieldValue__c))    oTFH.SObjFieldValue__c = DateTime.valueOf(oTFH.SObjFieldValue__c).format(HUMConstants__c.getInstance('TaskHistoryDateFormat').StringValue__c);
        if(!string.isEmpty(oTrackFieldHistory.SObjFieldValue__c))    oTrackFieldHistory.SObjFieldValue__c = DateTime.valueOf(oTrackFieldHistory.SObjFieldValue__c).format(HUMConstants__c.getInstance('TaskHistoryDateFormat').StringValue__c);
        
    }
    
    /*
    * Method Name   :    trackLastModifiedQueue
    * Description   :    Tracking the last modified by queue
    * Return Type   :    List<wrapperView>
    * Parameters    :    List of Task_Field_History__c object type 
    */
    @testVisible private static List<wrapperView> trackLastModifiedQueue(List<Task_Field_History__c> lstFieldUpdated)
    {
        try
        {
            String sCurrentQueue = oTask.LastModifiedby_Queue__c;
            Map<DateTime, String> mapTempData = new Map<DateTime, String>();
            mapQueue = new Map<DateTime, String>{NULL => ''};
            
            for(Integer i = 0 ; i < lstFieldUpdated.size() ; i++)
            {
                if(lstFieldUpdated.get(i).SObjFieldName__c == System.Label.HUMLastModifiedQueue)
                {
                    mapTempData.put(lstFieldUpdated.get(i).Created_At__c, lstFieldUpdated.get(i).SObjFieldValue__c);
                }
            }
            for(Integer i = 0 ; i < lstFieldUpdated.size() ; i++)
            {
                if(i == Integer.valueOf(HUMConstants__c.getInstance('TaskHistoryZero').StringValue__c))
                { 
                    mapQueue.put(lstFieldUpdated.get(i).Created_At__c, sCurrentQueue);
                    
                    if(mapTempData.get(lstFieldUpdated.get(i).Created_At__c) != NULL)
                    {
                        sCurrentQueue = mapTempData.get(lstFieldUpdated.get(i).Created_At__c);
                    }
                }
                else
                {
                    if(mapQueue.get(lstFieldUpdated.get(i).Created_At__c) == NULL)
                    { 
                        if(mapTempData.get(lstFieldUpdated.get(i).Created_At__c) == NULL)
                        {
                            mapQueue.put(lstFieldUpdated.get(i).Created_At__c, sCurrentQueue);
                        }
                        else
                        {
                            mapQueue.put(lstFieldUpdated.get(i).Created_At__c, sCurrentQueue);
                            sCurrentQueue = mapTempData.get(lstFieldUpdated.get(i).Created_At__c);
                        }
                    }
                }
            }
            List<wrapperView> wrpList = removeTrackOfLastModifiedQueue(lstFieldUpdated);
            return wrpList;
        }
        catch (Exception e)
        {    
            HUMExceptionHelper.bRestrictLog=true;
            HUMExceptionHelper.logErrors(e, 'HUMTaskHistoryController', 'trackLastModifiedQueue');  
            return null;        
        
        }
    }
    
    /*
    * Method Name   :    removeTrackOfLastModifiedQueue
    * Description   :    For removing the track of Last Modified By Queue.
    * Return Type   :    List<wrapperView>
    * Parameters    :    List of Task_Field_History__c type 
    */
    @testVisible private static List<wrapperView> removeTrackOfLastModifiedQueue(List<Task_Field_History__c> lstFieldUpdated)
    {
        try{
            List<wrapperView> wrpView = new List<wrapperView>();
            for(integer i = 0 ; i < lstFieldUpdated.size() ; i ++)
            {   
                if(lstFieldUpdated.get(i).SObjFieldName__c.equals(System.Label.HUMLastModifiedQueue)) 
                {
                    lstFieldUpdated.remove(i);
                    i--;
                    continue;
                }

                wrapperView wrp = new wrapperView();
                wrp.taskCreatedDate = lstFieldUpdated.get(i).Created_At__c.format('M/d/yyyy hh:mm a');
                wrp.taskCreatedBy = lstFieldUpdated.get(i).CreatedBy.Name;
                wrp.taskChangeAction = lstFieldUpdated.get(i).SObjFieldValue__c;
                wrp.Id = lstFieldUpdated.get(i).Id;
                wrp.taskModifiedByQueue = mapQueue.get(lstFieldUpdated.get(i).Created_At__c);
                wrp.taskNumber = oTask.Task_Number__c;
                wrp.mapQueue = mapQueue;
                wrpView.add(wrp);
            }
            return wrpView;
        }
        catch(Exception ex){
            HUMExceptionHelper.logErrors(ex, 'HUMTaskHistoryController', 'removeTrackOfLastModifiedQueue');  
            return null;
        }
    }
    
    /*
    * Method Name   :    removeBlankValues
    * Description   :    For remove blank values.
    * Return Type   :    void
    * Parameters    :    List of Task_Field_History__c object type
    */
    @testVisible private static void removeBlankValues(List<Task_Field_History__c> lstFieldUpdated){
        for(integer i = 0 ; i < lstFieldUpdated.size() ; i ++)
        {   
            if(lstFieldUpdated.get(i).SObjFieldValue__c == '' || lstFieldUpdated.get(i).SObjFieldValue__c == null) 
            {
                lstFieldUpdated.remove(i);
                i--;
            }
        }
       
    }
    // the wrapper class is used to hold the task history fields
    public class wrapperView{
        @AuraEnabled public String taskCreatedDate;
        @AuraEnabled public String Id;
        @AuraEnabled public String taskCreatedBy;
        @AuraEnabled public String taskChangeAction;
        @AuraEnabled public String taskModifiedByQueue;
        @AuraEnabled public String taskNumber;
        @AuraEnabled public Map<DateTime, String> mapQueue;
         
        
    }    
}