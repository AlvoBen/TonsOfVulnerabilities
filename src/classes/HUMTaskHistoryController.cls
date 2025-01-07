/*****************************************************************************************************************************************************************************
Apex Class Name : HUMTaskHistoryController
Version         : 1.0
Created On      : 02/19/2015
Function        : It is the main controller class for tracking task field updates.
Test Class      : HUMTaskHistoryControllerTest

Modification Log: 
* Developer Name           Code Review                 Date                        Description
********************************************************************************************************************************************************************************
* Kritika P Popat            21275                    02/19/2015                  Original version
* Kritika P Popat            21743                    03/23/2015                  Fixed defect 176756.0001(Lines modified 457 to 485)
* Prudhvi Pamarthi            154304                  01/19/2018            REQ - 328929 Fix for HUMExceptionHelper: DML 
* Mithra Bharadwaj                                                          US#1522825 SonarQube Changes
********************************************************************************************************************************************************************************/
public with sharing class HUMTaskHistoryController
{
    private Integer iCounterSize = Integer.valueOf(HUMConstants__c.getInstance('TaskHistoryZero').StringValue__c);
    private Integer iPageRecordSize = Integer.valueOf(HUMConstants__c.getInstance('TaskHistoryZero').StringValue__c);
    private List<Task_Field_History__c> lstTaskListPagination;
    private Integer iListSize = Integer.valueOf(HUMConstants__c.getInstance('TaskHistoryListSize').StringValue__c);
    private Integer iTotalSize = Integer.valueOf(HUMConstants__c.getInstance('TaskHistoryZero').StringValue__c);
    @testVisible private Map<Id, Task_Field_History__c> mapIdTaskField = NULL;
    @testVisible private Task oTask = new Task();
    private String sAccessibleFields = '';
    private Map<String, HUMObject_History_Metadata__c> mapObjHisMetadata = new Map<String, HUMObject_History_Metadata__c>([Select name, Tracked_Field__c , Parent_Tracked_Object__c from HUMObject_History_Metadata__c]);
    
     //Capture Any DML Exceptions in errorLog object
    public List<Error_Log__c> listErrorLogs 
    {
        get 
        {
            if(listErrorLogs==null) {return new List<Error_Log__c>();}
            return listErrorLogs;
        }
        set;
    } 
    
    /*
    * Method Name   :    HUMTaskHistoryController
    * Description   :    Constructor for this class
    * Return Type   :    NA
    * Parameters    :    NA
    */
    public HUMTaskHistoryController()
    {
        sTaskId = ApexPages.currentPage().getParameters().get('id');
                
        getAccessibleFields();
        String sQuery = 'Select ';
        sQuery += sAccessibleFields;
        sQuery += ' From Task ' +
                  ' Where Id =: sTaskId' +
                  ' Limit 1';
        sQuery = string.EscapeSingleQuotes(sQuery);
        oTask = Database.query(sQuery);
        
        lstFieldUpdated = [Select Created_At__c, SObjFieldName__c, SObjFieldValue__c, CreatedById, CreatedBy.Name,
                             ParentFieldHistory_Id__c from Task_Field_History__c 
                             Where SObjID__c like : sTaskId + '%' AND SObjFieldName__c NOT IN ('Description','OwnerID') 
                             ORDER BY Created_At__c DESC Limit : Integer.valueOf(HUMConstants__c.getInstance('TaskHistoryQueryLimitSize').StringValue__c)];
        
        mapIdTaskField = new Map<Id, Task_Field_History__c>();
        trackHistoryData();
    }
    
    public string sTaskId{get;set;}
    public List<Task_Field_History__c> lstFieldUpdated{get;set;}
    public Map<DateTime, String> mapQueue {get; set;}
    
    /*
    * Method Name   :    getAccessibleFields
    * Description   :    This method is used for getting all the fields from HUMObject_History_Metadata__c custom settings
                         that are being tracked on the Task object. 
    * Return Type   :    void
    * Parameters    :    NA
    */
    @testVisible private void getAccessibleFields()
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
    * Return Type   :    void
    * Parameters    :    NA
    */
    public void trackHistoryData()
    {
        List<Task_Field_History__c> lstOfUpdatedFields = lstFieldUpdated.deepClone();
        String sOldDueDate = '';
            
        for (Task_Field_History__c b : lstOfUpdatedFields)
        {
            mapIdTaskField.put(b.ParentFieldHistory_Id__c, b);
        }
        Task_Field_History__c oTrackFieldHistory = new Task_Field_History__c();
        List<Schema.DescribeSObjectResult> describeSobjectsResult = Schema.describeSObjects(new List<String>{System.Label.HUMTrackedTaskObject});
        Map<String, Schema.SObjectField> mapAllFields = describeSobjectsResult[Integer.valueOf(HUMConstants__c.getInstance('TaskHistoryZero').StringValue__c)].fields.getMap();

        for(Task_Field_History__c oTFH : lstFieldUpdated) 
        {
            if(mapIdTaskField.containskey(oTFH.id))
            {
                oTrackFieldHistory = mapIdTaskField.get(oTFH.id);
                
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
                        if(!string.isEmpty(oTFH.SObjFieldValue__c))   oTFH.SObjFieldValue__c = DateTime.valueOf(oTFH.SObjFieldValue__c).format(HUMConstants__c.getInstance('TaskHistoryDateFormat').StringValue__c);
                        
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
        
        trackLastModifiedQueue(lstFieldUpdated);
        
        removeRepeatedValues(lstFieldUpdated);
        
        getPaginationOfTaskField();
    }
    
    /*
    * Method Name   :    getActivityDate
    * Description   :    Used to get Date from DateTime field
    * Return Type   :    void
    * Parameters    :    Task_Field_History__c object containing old value,Task_Field_History__c  containing new value
    */
    @testVisible private void getActivityDate(Task_Field_History__c oTFH, Task_Field_History__c oTrackFieldHistory)
    {
        if(!string.isEmpty(oTFH.SObjFieldValue__c))    oTFH.SObjFieldValue__c = DateTime.valueOf(oTFH.SObjFieldValue__c).format(HUMConstants__c.getInstance('TaskHistoryDateFormat').StringValue__c);
        if(!string.isEmpty(oTrackFieldHistory.SObjFieldValue__c))    oTrackFieldHistory.SObjFieldValue__c = DateTime.valueOf(oTrackFieldHistory.SObjFieldValue__c).format(HUMConstants__c.getInstance('TaskHistoryDateFormat').StringValue__c);
    }
    
    /*
    * Method Name   :    trackLastModifiedQueue
    * Description   :    Tracking the last modified by queue
    * Return Type   :    void
    * Parameters    :    List of Task_Field_History__c object type 
    */
    @testVisible private void trackLastModifiedQueue(List<Task_Field_History__c> lstFieldUpdated)
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
            removeTrackOfLastModifiedQueue(lstFieldUpdated);
        }
        catch (Exception e)
        {            
            HUMExceptionHelper.bRestrictLog=true;
            listErrorLogs = HUMExceptionHelper.logErrors(e, 'HUMTaskHistoryController', 'trackLastModifiedQueue'); 
        }
    }
    
    /*
    * Method Name   :    removeTrackOfLastModifiedQueue
    * Description   :    For removing the track of Last Modified By Queue.
    * Return Type   :    void
    * Parameters    :    List of Task_Field_History__c type 
    */
    @testVisible private void removeTrackOfLastModifiedQueue(List<Task_Field_History__c> lstFieldUpdated)
    {
        for(integer i = 0 ; i < lstFieldUpdated.size() ; i ++)
        {   
            if(lstFieldUpdated.get(i).SObjFieldName__c.equals(System.Label.HUMLastModifiedQueue)) 
            {
                lstFieldUpdated.remove(i);
                i--;
            }
        }
    }
    
    /*
    * Method Name   :    removeBlankValues
    * Description   :    For remove blank values.
    * Return Type   :    void
    * Parameters    :    List of Task_Field_History__c object type
    */
    @testVisible private void removeBlankValues(List<Task_Field_History__c> lstFieldUpdated)
    {
        for(integer i = 0 ; i < lstFieldUpdated.size() ; i ++)
        {   
            if(lstFieldUpdated.get(i).SObjFieldValue__c == '' || lstFieldUpdated.get(i).SObjFieldValue__c == null) 
            {
                lstFieldUpdated.remove(i);
                i--;
            }
        }
       
    }
    
    /*
    * Method Name   :    removeRepeatedValues
    * Description   :    For displaying one record per time stamp on screen.
    * Return Type   :    void
    * Parameters    :    List of Task_Field_History__c object type
    */
    @testVisible private void removeRepeatedValues(List<Task_Field_History__c> lstFieldUpdated)
    {
        Boolean bIsRepeated = false;
        DateTime dtTemp = NULL;
        
        for(integer i = 0 ; i < lstFieldUpdated.size() ; i++)
        {
            if(i == Integer.valueOf(HUMConstants__c.getInstance('TaskHistoryZero').StringValue__c))
            {
                dtTemp = lstFieldUpdated.get(i).Created_At__c;
                continue;
            }
            if(dtTemp == lstFieldUpdated.get(i).Created_At__c)
            {
                lstFieldUpdated.get(i).Created_At__c = NULL;
            }
            else
            {
                dtTemp = lstFieldUpdated.get(i).Created_At__c;
            }
        }
    }
    
   /*
   * Method Name   :    getFieldsRecord
   * Description   :    Paginated list for Task Update History data to display in VF page.
   * Return Type   :    List of Task_Field_History__c object type
   * Parameters    :    NA
   */
    public List<Task_Field_History__c> getFieldsRecord()
    {
        return lstTaskListPagination;
    }
    
   /*
   * Method Name   :    getPaginationOfTaskField
   * Description   :    Pagination for Task Update History data 
   * Return Type   :    void
   * Parameters    :    NA
   */
    private void getPaginationOfTaskField()
    {
        Integer i = Integer.valueOf(HUMConstants__c.getInstance('TaskHistoryZero').StringValue__c);
        lstTaskListPagination = new List <Task_Field_History__c>();
        iTotalSize = Integer.valueOf(HUMConstants__c.getInstance('TaskHistoryZero').StringValue__c);
        
        for(Task_Field_History__c objTemp : lstFieldUpdated)
        {
            if(iTotalSize == Integer.valueOf(HUMConstants__c.getInstance('TaskHistoryTotalPageSize').StringValue__c))    break;
            if(objTemp.Created_At__c != NULL)    iTotalSize++;
        }
        
        if(lstFieldUpdated.isEmpty())    return;
        
        lstTaskListPagination = new List<Task_Field_History__c>();
        
        while(i < iListSize && iCounterSize < lstFieldUpdated.size())
        {
            if(lstFieldUpdated.get(iCounterSize).Created_At__c != NULL)
            {
                lstTaskListPagination.add(lstFieldUpdated.get(iCounterSize++));
                while(iCounterSize < lstFieldUpdated.size() && lstFieldUpdated.get(iCounterSize).Created_At__c == NULL)
                {
                    lstTaskListPagination.add(lstFieldUpdated.get(iCounterSize++));
                }
            i++;
            iPageRecordSize++;
            }
            
        }
    }
    
   /*
   * Method Name   :    getCounter
   * Description   :    Count for tracking the data to be displayed on screen.
   * Return Type   :    void
   * Parameters    :    NA
   */
    private void getCounter()
    {
        Integer iTemp = Integer.valueOf(HUMConstants__c.getInstance('TaskHistoryZero').StringValue__c), iRow = Integer.valueOf(HUMConstants__c.getInstance('TaskHistoryZero').StringValue__c);
        
        if(iPageRecordSize == Integer.valueOf(HUMConstants__c.getInstance('TaskHistoryZero').StringValue__c))
        {
            iCounterSize = Integer.valueOf(HUMConstants__c.getInstance('TaskHistoryZero').StringValue__c);
        }
        else
        {
            for(Task_Field_History__c objTemp : lstFieldUpdated)
            {
                if(objTemp.Created_At__c != NULL)    iTemp++;
                if(iTemp == iPageRecordSize + Integer.valueOf(HUMConstants__c.getInstance('TaskHistoryConstantOne').StringValue__c))    break;
                iRow++;
            }
            iCounterSize = iRow;
        }
    }
    
    /*  
    * Method Name   :    getFirst
    * Description   :    This Method is used to display the first page record(s)    
    * Return Type   :    void    
    * Parameters    :    NA
    */
    public void getFirst()
    {
        iCounterSize = Integer.valueOf(HUMConstants__c.getInstance('TaskHistoryZero').StringValue__c);
        iPageRecordSize = Integer.valueOf(HUMConstants__c.getInstance('TaskHistoryZero').StringValue__c);
        getPaginationOfTaskField();
    }
    
    /*  
    * Method Name   :    getDisableFirst
    * Description   :    This Method is used to disable first button   
    * Return Type   :    Boolean    
    * Parameters    :    NA
    */
    public Boolean getDisableFirst()
    {
        return (iCounterSize == Integer.valueOf(HUMConstants__c.getInstance('TaskHistoryZero').StringValue__c) || iPageRecordSize <= iListSize || iTotalSize <= iListSize) ? Boolean.valueOf(HUMConstants__c.getInstance('FLAG_TRUE').StringValue__c) : Boolean.valueOf(HUMConstants__c.getInstance('FLAG_FALSE').StringValue__c);
    }
    
    /*  
    * Method Name   :    getNext
    * Description   :    This Method is used to display the next page record(s)    
    * Return Type   :    void    
    * Parameters    :    NA
    */
    public void getNext()
    {
        getPaginationOfTaskField();
    }
    
    /*  
    * Method Name   :    getDisableNext
    * Description   :    This method will disable the next button.    
    * Return Type   :    Boolean
    * Parameters    :    NA
    */
    public Boolean getDisableNext()
    {
        return (iTotalSize <= iListSize || iTotalSize <= iPageRecordSize) ? Boolean.valueOf(HUMConstants__c.getInstance('FLAG_TRUE').StringValue__c) : Boolean.valueOf(HUMConstants__c.getInstance('FLAG_FALSE').StringValue__c);
    }
    
    /*  
    * Method Name   :    getPrevious
    * Description   :    This Method is used to display the previous page record(s).  
    * Return Type   :    void    
    * Parameters    :    NA
    */
    public void getPrevious()
    {
        Integer iTemp = iPageRecordSize/iListSize;
        iPageRecordSize = (Math.mod(iPageRecordSize, iListSize) > Integer.valueOf(HUMConstants__c.getInstance('TaskHistoryZero').StringValue__c)) ? (iTemp - Integer.valueOf(HUMConstants__c.getInstance('TaskHistoryConstantOne').StringValue__c))*iListSize : (iTemp - Integer.valueOf(HUMConstants__c.getInstance('TaskHistoryConstantTwo').StringValue__c))*iListSize;
        getCounter();
        getPaginationOfTaskField();
    }
    
    /*  
    * Method Name   :    getDisablePrevious
    * Description   :    This method will disable the previous button.    
    * Return Type   :    Boolean
    * Parameters    :    NA
    */
    public Boolean getDisablePrevious()
    {
        return (iCounterSize == Integer.valueOf(HUMConstants__c.getInstance('TaskHistoryZero').StringValue__c) || iTotalSize <= iListSize || iPageRecordSize <= iListSize) ? Boolean.valueOf(HUMConstants__c.getInstance('FLAG_TRUE').StringValue__c) : Boolean.valueOf(HUMConstants__c.getInstance('FLAG_FALSE').StringValue__c);
    }
    
    /*  
    * Method Name   :    getLast
    * Description   :    This Method is used to display the last page record(s).    
    * Return Type   :    void    
    * Parameters    :    NA
    */
    public void getLast()
    {
        Integer iRec = iTotalSize/iListSize;
        iPageRecordSize = (Math.mod(iTotalSize, iListSize) > Integer.valueOf(HUMConstants__c.getInstance('TaskHistoryZero').StringValue__c)) ? iRec*iListSize : (iRec - Integer.valueOf(HUMConstants__c.getInstance('TaskHistoryConstantOne').StringValue__c))*iListSize;
        getCounter();
        getPaginationOfTaskField();
    }
    
    /*  
    * Method Name   :    getDisableLast
    * Description   :    This Method is used to disable last button.   
    * Return Type   :    Boolean    
    * Parameters    :    NA
    */
    public Boolean getDisableLast()
    {
        return (iTotalSize <= iListSize || iTotalSize <= iPageRecordSize) ? Boolean.valueOf(HUMConstants__c.getInstance('FLAG_TRUE').StringValue__c) : Boolean.valueOf(HUMConstants__c.getInstance('FLAG_FALSE').StringValue__c);
    }
    
    /*  
    * Method Name   :    getFirstRecordOf_Page
    * Description   :    This method is used to display the first record of the page.
    * Return Type   :    Integer
    * Parameters    :    NA
    */
    public Integer getFirstRecordOf_Page()
    {
        integer iRec = iPageRecordSize/iListSize; 
        integer iFrom = (Math.mod(iPageRecordSize, iListSize) > Integer.valueOf(HUMConstants__c.getInstance('TaskHistoryZero').StringValue__c)) ? ((iRec*iListSize)+ Integer.valueOf(HUMConstants__c.getInstance('TaskHistoryConstantOne').StringValue__c)) : (iPageRecordSize-iListSize+ Integer.valueOf(HUMConstants__c.getInstance('TaskHistoryConstantOne').StringValue__c));
        return iFrom;
    }
    
    /*  
    * Method Name   :    getLastRecordOf_Page  
    * Description   :    This method is used to display the last record for that page.  
    * Return Type   :    Integer
    * Parameters    :    NA
    */
    public Integer getLastRecordOf_Page()
    {
        return iPageRecordSize;
    }
    
    /*  
    * Method Name   :    getTotal_SizeRecord
    * Description   :    This method is used to display the total no. of records.
    * Return Type   :    Integer
    * Parameters    :    NA
    */
    public Integer getTotal_SizeRecord()
    {
        return iTotalSize;
    }
}