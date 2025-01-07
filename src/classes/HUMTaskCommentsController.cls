/**************************************************************************************************************************
Apex class Name    : HUMTaskCommentsController
Version            : 1.0
Function           : Controller for HUMTaskCommentsComponent ,to display Tak comments in Task detial page
Created On         : Feb 27 2015
Test Class         : HUMTaskCommentsControllerTest

Modification Log:
*   Developer                   Code Review         Date                Description
* ------------------------------------------------------------------------------------------------------------------------------                
*  Asha Deepika Koyyana          21256              2/27/2015             Original Version
*  Asha Deepika Koyyana          21642              3/12/2015             Implemented the feedbcak given by Businees.
*  Asha Deepika Koyyana          21857              3/26/2015             Fixed defect #177621.0001         
* Prudhvi Pamarthi              154304               01/19/2018            REQ - 328929 Fix for HUMExceptionHelper: DML 
*********************************************************************************************************************************/

public with sharing class HUMTaskCommentsController
{ 
    
    public List<Task_Field_History__c> lstFieldUpdates = new List<Task_Field_History__c>();
    private List<Task_Field_History__c> lstTaskFieldHis = new List<Task_Field_History__c>();
    private map<DateTime, String > mapLastQueue = new map<DateTime, String>();
    private Map<DateTime, String> mapTempQueue = new Map<DateTime, String>();
    private Integer iSizeZero = Integer.valueOf(HUMConstants__c.getInstance('HUMLISTSIZEZERO').IntegerValue__c);
    private Integer iList_size = Integer.valueOf( HUMConstants__c.getInstance('ListSize').IntegerValue__c);
    private Integer iCounter = iSizeZero;
    private Integer iTotalSize = iSizeZero;
    private Integer iMax_limit = iSizeZero;
    private Integer iPageFirstRecord = iSizeZero;
    private String sCurrentQueue ='';
    private String sCurQueue ='';
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
    * Method Name   :    HUMTaskCommentsController - Constructor
    * Description   :    This method is used for instantiating local variables.    
    * Return Type   :    NA
    * Parameters    :    NA
    */
    public HUMTaskCommentsController()
    {
        try
        {
            sTaskId = ApexPages.currentPage().getParameters().get('id');
            List<String> lstString = new List<String>();
            lstString.add(System.Label.HumTaskLastModifiedQueue);
            lstString.add(System.Label.HUMTaskDescription);
            lstFieldUpdates =[SELECT ID,Created_At__c,SObjFieldName__c,SObjFieldValue__c,CreatedBy.Name, ParentFieldHistory_Id__c  from Task_Field_History__c WHERE  SObjFieldName__c In : lstString AND SObjID__c like : sTaskId + '%' ORDER BY Created_At__c DESC  ];
            sCurQueue =[SELECT LastModifiedby_Queue__c FROM TASK WHERE ID=:sTaskId Limit 1].LastModifiedby_Queue__c ;
            getModifiedQueue(lstFieldUpdates);
            getComments();
         } 
        catch(Exception ex)
        {
            //Restrict insert of error Logs
            HUMExceptionHelper.bRestrictLog=true;
            listErrorLogs = HUMExceptionHelper.logErrors(ex, 'HUMTaskCommentsController', 'HUMTaskCommentsController');
        }
    }
    
    public Id sTaskId {get; set;}
    
   /*
    * Method Name : getModifiedQueue
    * Description : Populates Map Queue
    * Return type : void
    * Parameter   : List
    */
    public void getModifiedQueue(List<Task_Field_History__c> lstLastModifyQueue)
    {
        Map<DateTime, String> mapTempQueue = new Map<DateTime, String>();
        mapLastQueue = new Map<DateTime, String>{NULL => ''};
        sCurrentQueue = sCurQueue ;
        
        for(Integer i = iSizeZero; i < lstLastModifyQueue.size() ; i++)
        {
            if(lstLastModifyQueue.get(i).SObjFieldName__c == System.Label.HUMLastModifiedQueue)
            {
                mapTempQueue.put(lstLastModifyQueue.get(i).Created_At__c, lstLastModifyQueue.get(i).SObjFieldValue__c);
            }
        }
        for(Integer i = iSizeZero; i < lstLastModifyQueue.size() ; i++)
        {
            if(i == iSizeZero)
            { 
                mapLastQueue.put(lstLastModifyQueue.get(i).Created_At__c, sCurrentQueue);
                
                if(mapTempQueue.get(lstLastModifyQueue.get(i).Created_At__c) != NULL)
                {
                    sCurrentQueue = mapTempQueue.get(lstLastModifyQueue.get(i).Created_At__c);
                }
            }
            else
            {
                if(mapLastQueue.get(lstLastModifyQueue.get(i).Created_At__c) == NULL)
                {
                    if(mapTempQueue.get(lstLastModifyQueue.get(i).Created_At__c) == NULL)
                    {
                        mapLastQueue.put(lstLastModifyQueue.get(i).Created_At__c, sCurrentQueue);
                    }
                    else
                    {
                        mapLastQueue.put(lstLastModifyQueue.get(i).Created_At__c, sCurrentQueue);
                        sCurrentQueue = mapTempQueue.get(lstLastModifyQueue.get(i).Created_At__c);
                    }
                }
            }
        }
    } 
     
   /*
    * Method Name : getComments
    * Description : Gets the Task Comments
    * Return type : void
    * Parameter   : NA
    */
    public void getComments()
    {
        String LastModifiedByQueue = '';
        integer i = iSizeZero;
        
        for(Task_Field_History__c oTaskFH: lstFieldUpdates)
        {
           LastModifiedByQueue = mapLastQueue.get( oTaskFH.created_at__c) ;
           
            if(oTaskFH.SObjFieldName__c.equals(System.Label.HUMTaskDescription) && i < HUMConstants__c.getInstance('HUMTaskDescriptionLimitSize').IntegerValue__c)
            { 
                i++;
                if(oTaskFH.SObjFieldValue__c != NULL)
                {
                    String sCreatedDate = oTaskFH.Created_At__c.format(System.Label.HUMDateTimeFormat);
                    oTaskFH.SObjFieldValue__c= System.Label.HUMTaskCreatedBy + ' ' + oTaskFH.CreatedBy.Name + System.Label.HUMComma + ' ' + LastModifiedByQueue + ' ' + sCreatedDate + ' \r\n' + oTaskFH.SObjFieldValue__c ;
                    lstTaskFieldHis.add(oTaskFH); 
                }   
            } 
        }    
        getPagination(lstTaskFieldHis);
    }
    
    /*
    * Method Name   :    getlstComments
    * Description   :    Gets the Paginated list of comments
    * Return Type   :    List<Task_Field_History__c>
    * Parameters    :    List<Task_Field_History__c>
    */
    public List<Task_Field_History__c> getlstComments()
    {
        return getPagination(lstTaskFieldHis);
    }
    
    /*
    * Method Name   :    getPagination
    * Description   :    Pagination for Task Comments data
    * Return Type   :    List<Task_Field_History__c>
    * Parameters    :    List<Task_Field_History__c>
    */
    public List<Task_Field_History__c> getPagination(List<Task_Field_History__c> lstFieldUpdatesPage)
    {
        List <Task_Field_History__c>  lstTrackFieldHisPage = new List <Task_Field_History__c>();
        iTotalSize = lstFieldUpdatesPage.size();
        iMax_limit = ((iCounter + ilist_size) > iTotalSize) ? iTotalSize : (iCounter + ilist_size);
        iCounter = (iCounter > iMax_limit) ? iSizeZero : iCounter;
        
        if(!lstFieldUpdatesPage.isEmpty())
        {
            lstTrackFieldHisPage.clear();
        }
        
        if(lstFieldUpdatesPage.isEmpty())    return NULL;
        
        for(integer i = iCounter ; i < iMax_limit ; i++)
        {
            lstTrackFieldHisPage.add(lstFieldUpdatesPage.get(i));
        }
        iPageFirstRecord = iCounter;
          
        return lstTrackFieldHisPage;
    }
    
    /*  
    * Method Name   :    getPrevious
    * Description   :    This Method is used to display the previous page record(s)    
    * Return Type   :    void    
    * Parameters    :    NA
    */
    public void getPrevious()
    {
        iCounter -= ilist_size;
        iPageFirstRecord = iCounter;
    }
    
    /*  
    * Method Name   :    getNext
    * Description   :    This Method is used to display the next page record(s)    
    * Return Type   :    void    
    * Parameters    :    NA
    */
    public void getNext()
    {
        iCounter += ilist_size;
        iPageFirstRecord = iCounter;
    }
    
    /*  
    * Method Name   :    getDisablePrevious
    * Description   :    This method will disable the previous buttons    
    * Return Type   :    boolean
    * Parameters    :    NA
    */
    public Boolean getDisablePrevious()
    {
        return (iCounter > iSizeZero) ? false : true;
    }
    
    /*  
    * Method Name   :    getDisableNext
    * Description   :    This method will disable the next buttons    
    * Return Type   :    boolean
    * Parameters    :    NA
    */
    public Boolean getDisableNext()
    {
        return (iCounter + iList_size < iTotalSize) ? false : true;
    }
    
    /*  
    * Method Name   :    getFirstList
    * Description   :    This Method is used to display the first page record(s)    
    * Return Type   :    void    
    * Parameters    :    NA
    */
    public void getFirstList()
    {
        iCounter = iSizeZero;
    }
    
   /*  
    * Method Name   :    getLastList
    * Description   :    This Method is used to display the last page record(s)    
    * Return Type   :    void    
    * Parameters    :    NA
    */
    public void getLastList()
    {
        integer iRec = (iTotalSize/iList_size);
        if(iTotalSize <= iList_size)
        {
            iCounter = iSizeZero;
        }   
        else if(iTotalSize > iRec*iList_size)
        {
            iCounter = (iRec*iList_size);
        }   
        else 
        {
            iCounter = (iRec*iList_size) - iList_size;
        }   
     }
    
     /*  
     * Method Name   :    getPageFirstRecord
     * Description   :    This Method is used to display the first page record(s)    
     * Return Type   :    Integer
     * Parameters    :    NA
     */
    public Integer getPageFirstRecord()
    {
        return iPageFirstRecord +(Integer.valueOf(HUMConstants__c.getInstance('HUMConstantOne').IntegerValue__c));
    }
    
     /*
     * Method Name   :    getPageLastRecord
     * Description   :    This Method is used to display the last page record(s)    
     * Return Type   :    Integer
     * Parameters    :    NA
     */
    public Integer getPageLastRecord()
    {
        return getDisableNext() ? iTotalSize : (iPageFirstRecord + iList_size);
    }
    
     /*  
     * Method Name   :    getTotal_size
     * Description   :    This Method is used to display the total size of record(s)    
     * Return Type   :    integer
     * Parameters    :    NA
     */
    public Integer getTotal_size()
    {
        return iTotalSize;
    }
    
 }