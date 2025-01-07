/*******************************************************************************************************************************
Apex Class Name : HUMUpdateCaseTaskHelper
Version         : 1.0
Created On      : 9/23/2014
Function        : Used for trigger functionality as a helper to HUMUpdateCaseHelper and HUMUpdateTaskHelper
Test Class      : HUMUpdateCaseTaskHelperTest

Modification Log: 
* Developer Name              Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Mrudula Jaddu                17996                   9/23/2014                  Original Version
* Mrudula Jaddu                18792                   10/27/2014                 Code fix according to defefct#151415
* Mrudula Jaddu                19256                   11/14/2014                 Code changed to handle if work queue priority record is not there.
* Mrudula Jaddu                19514                   11/25/2014                 Defect 162663 - code changed to handle to Queue/View criteria comparition
* Santhi Mandava               20517                   01/23/2015                 Fixed defect #164139
* Santhi Mandava               21968                   03/16/2015                 As per REQ-02635 removed future call out related logic and updated code to include 
                                                                                  newly created MD relationship fields and refactored existing code.
* Manuel Johnson               27468                   11/05/2015                 Added Criteria and Priority 23    
* Avinash Choubey              34175                   07/29/2016                 defect 235634 fix     
* Avinash Choubey              34551                   08/04/2016                 defect 235634 missed scenario fix 
* Harshith Mandya              35994                   08/18/2016                 Defect fix # 237337.0001  
* Avinash Choubey              36263                   08/22/2016                 Defect 238691 fix 
* Harshith Mandya              47072                   03/07/2016                 Modified code to pick only active queue view     
* Avinash Choubey              46567                   11/07/2016                 defect 247444 fix    
* Harshith Mandya              50014                   11/30/2106                 Implemented REQ - 290990 -  removed all lookup related fields from 
                                                                                  Case/Task/Interaction/Attachment to Work Queue Setup and Queue View  
* Avinash Choubey                                      12/20/2016                 REQ 296640 corrected filter criteria in query strig in initializeListAndMaps method   
* Harshith Mandya                                      12/27/2016                 REQ - 296640 - Added Query to create a map of queue view And related items    
* Harshith Mandya                                      01/20/2017                 Fixed Prod Issue  
* Harshith Mandya                                      01/24/2017                 CA Ticket - 6541721       
* Harshith Mandya                                      02/20/2017                 Implmented REQ - 300084
* Pallavi Ravishankar          98201                   05/31/2017                  REQ 318433 - changed classification & intent id 
                                                                                   references to CTCI references 
                                                                                   
* Rajesh Keswani               163966                  10/07/2016                  REQ - 333701; Set/Group field, Edit Expression UI & Deleting Queue View Items - Expression impacts.                                                                                                                                                                                                                                                                                                                                         
* Rajesh Keswani               175570                  01/29/2018                  Defect Fix 339913.0001; routing logic changed when APi name is classification.
* Vijay Bhasker P                                      03/15/2018                  CA Ticket # 7205510 Fix; Updated iCriteriaLimit from 22 to 23. 
* Suresh Sunkari                                       07/10/2018                  REQ - 357640  To get max criteria limit dynamically at run time instead of hard coding it to 22 or 23
* Suresh Sunkari                                       07/16/2018                  Defect Fix  370062.0001;
* Shailesh Mali                                        08/08/2019                  REQ - 405958 WorkQueueView Optimization
* Jasmeen Shangari                                     02/08/2021                  Azure ID 1790946 - Fix for Incident - INC0806755                      
*******************************************************************************************************************************/

public with sharing class HUMUpdateCaseTaskHelper
{   
    public static List<Queue_View__c> lstQueueView = new List<Queue_View__c>();
    private static Map<String, String> mapOfPriority = new Map<String, String>();
    public static Map<String, Map<String, String>> mapOfQueuePriority = new Map<String, Map<String, String>>();
    private static  List<Queue_View__c> lstTempTwo = new List<Queue_View__c>();
    private static Integer iCriteriaLimit;
    private static Map<String, HUMQueueViewCriteria__c> mapOfFilters = null;
    public static Map<String,List<Queue_View__c>> mapViews = new Map<String,List<Queue_View__c>>();
    public static Map<Id,List<Queue_View_Item__c>> mapViewItems = new Map<Id,List<Queue_View_Item__c>>();
    public static Map<string,set<string>> expressionQVIs = new Map<string,set<string>>();
    public static String objectName = 'Work_Queue_Priority__C'; 
    private static Boolean bSwitch_1790946 = HUMUtilityHelper.isCRMFunctionalityON('1790946');
    
    /**
    * Returns void      
    * <p>
    * This method is used for initializing all the lists and maps
    *
    * @param  boolean isTrigger
    * @param  Set<String> setQueueNames
    * @return  void
    * @see
    */    
    public static void initializeListAndMaps(boolean isTrigger,Set<String> setQueueNames)
    {    
        Work_Queue_Priority_CriteriaLimit_Value__c cv = Work_Queue_Priority_CriteriaLimit_Value__c.getValues('Criteria');
        iCriteriaLimit = Math.round(cv.Value__c);
        List<Queue_View_Item__c> lstQueueItem; 
        List<Queue_View__c> lstTempViews = new List<Queue_View__c>();
        String sViewName='';
        Integer iLimit= Limits.getLimitQueryRows(); 
        mapOfFilters = HUMQueueViewCriteria__c.getAll();       
        Set<String> setQueues = new Set<String>();
        
        String sQVIQuery = 'SELECT name,API_Name__c, Field_Operator__c, Field_Value__c,Queue_View__c FROM Queue_View_Item__c Where Queue_View__r.isActive__c = TRUE';
        string sQuery=' SELECT Id,Name,Queue_View_Name__c,Expression__c, Work_Queue_Name__r.Name, Work_Queue_Name__r.Service_Center__c, Work_Queue_Name__r.Department__c FROM Queue_View__c Where IsActive__c = TRUE AND Work_Queue_Name__r.IsActive__c = TRUE ' ;
        if(!isTrigger)
        {
            sQuery = sQuery + ' AND Work_Queue_Name__r.Name IN : setQueueNames ';
            sQVIQuery = sQVIQuery + ' AND Queue_View__r.Work_Queue_Name__r.Name IN : setQueueNames ';
        }
        sQuery = sQuery + ' order by Work_Queue_Name__r.Name limit '+iLimit;
        sQVIQuery = sQVIQuery + ' order by API_Name__c limit '+iLimit;
        
        try
        {
            lstQueueView = Database.query(sQuery); 
            if(null != lstQueueView && lstQueueView.size() > 0 )
            {
                sViewName = lstQueueView[0].Work_Queue_Name__r.Name;
                For(Queue_View__c obj : lstQueueView )
                {
                    setQueues.add(obj.Work_Queue_Name__r.Name);
                     if(obj.Work_Queue_Name__r.Name != sViewName)
                     {
                         mapViews.put(sViewName,lstTempViews);
                         sViewName = obj.Work_Queue_Name__r.Name;
                         lstTempViews =new List<Queue_View__c>();
                     }
                     lstTempViews.Add(obj);
                }
                mapViews.put(sViewName,lstTempViews);
            }
            lstQueueItem = Database.query(sQVIQuery);
            if(null != lstQueueItem && lstQueueItem.size() > 0 )
            {
                for(Queue_View_Item__c qviInstance : lstQueueItem)
                {
                    if(mapViewItems.containsKey(qviInstance.Queue_View__c))
                    {
                        mapViewItems.get(qviInstance.Queue_View__c).add(qviInstance);
                    }
                    else
                    {
                        mapViewItems.put(qviInstance.Queue_View__c,new List<Queue_View_Item__c>{qviInstance});
                    }
                }
            }
             retrieveQueuePriority(setQueues);
        }
        catch(Exception e)
        {
           HUMExceptionHelper.logErrors(e, 'HUMUpdateCaseTaskHelper', 'initializeListAndMaps');
        }      
     }  
   
    /**
    * Returns void      
    * <p>
    * This method is used for get list of Queue View priorties
    *
    * @param  Set<String>
    * @return  void
    * @see
    */ 
     @TestVisible
     private static void retrieveQueuePriority(Set<String> setQueueNames)
     {
         List<Work_Queue_Priority__c> lstQueuePriority=[Select Work_Queue_Name__r.Name,Criteria_1__c,Criteria_2__c,Criteria_3__c,
            Criteria_4__c,Criteria_5__c,Criteria_6__c,Criteria_7__c,Criteria_8__c,Criteria_9__c,Criteria_10__c,Criteria_11__c,
            Criteria_12__c,Criteria_13__c,Criteria_14__c,Criteria_15__c,Criteria_16__c,Criteria_17__c,Criteria_18__c,
            Criteria_19__c,Criteria_20__c,Criteria_21__c,Criteria_22__c,Criteria_23__c,Priority_1__c,Priority_2__c,Priority_3__c,
            Priority_4__c,Priority_5__c,Priority_6__c,Priority_7__c,Priority_8__c,Priority_9__c,Priority_10__c,Priority_11__c,
            Priority_12__c,Priority_13__c,Priority_14__c,Priority_15__c,Priority_16__c,Priority_17__c,Priority_18__c,Priority_19__c,
            Priority_20__c,Priority_21__c,Priority_22__c,Priority_23__c From Work_Queue_Priority__c Where Work_Queue_Name__r.Name IN :setQueueNames 
            Limit :(limits.getLimitQueryRows() - limits.getQueryRows())];
            
            for(Work_Queue_Priority__c oWorkQueuePriority : lstQueuePriority)
            {
               mapOfPriority = new Map<String, String>();
               for(Integer i =1; i <= iCriteriaLimit ; i++)
               {
               mapOfPriority.put(String.valueOf(oWorkQueuePriority.get(GLOBAL_CONSTANT_HUM.WORKQUEUE_APP_CRITERIA_HUM + i + GLOBAL_CONSTANT_HUM.CUSTOMOBJECT_SUFFIX_HUM)),
                                 String.valueOf(oWorkQueuePriority.get(GLOBAL_CONSTANT_HUM.WORKQUEUE_APP_PRIORITY_HUM + i + GLOBAL_CONSTANT_HUM.CUSTOMOBJECT_SUFFIX_HUM)));
               } 
               mapOfQueuePriority.put(oWorkQueuePriority.Work_Queue_Name__r.Name, mapOfPriority);
            }
     } 
     
    /**
    * Returns String
    * <p>
    * This method is used to get matched view based on queue priority.
    *
    * @param  List<Queue_View__c> lstViews
    * @param  map<string,string> mapPriorit
    * @param  integer count
    * @return  String
    * @see
    */   
    public static String getMatcheViewBasedOnPriority(List<Queue_View__c> lstViews,map<string,string> mapPriority,integer count)
    {
        string sQueueViewName='';
        Try
        {
            List<Queue_View__c> lstFinal=checkViewPriority(lstViews,mapPriority,count);
            
            if(lstFinal.size() == 1)
            {
                 sQueueViewName=lstFinal[0].Queue_View_Name__c;
            }
            else
            {
                If(count <iCriteriaLimit)
                {
                    if(lstFinal.size() > 1)
                    {
                        lstTempTwo=new list<Queue_View__c>();
                        lstTempTwo=lstFinal;
                    }
                    else if(lstFinal.size() == 0 && lstViews.size() > 0)
                    {
                        lstTempTwo = lstViews;
                    }
                    count = count + 1;
                    sQueueViewName=getMatcheViewBasedOnPriority(lstTempTwo,mapPriority,count);
                }
                else if (count == iCriteriaLimit)
                {   
                    if(lstFinal.size() > 1 || (lstFinal.size() == 0  && lstViews.size() > 0))
                    {
                        sQueueViewName = GLOBAL_CONSTANT_HUM.TASKLISTVIEW_ALLOTHERVIEW_HUM;
                    }
                }
            }
        }
        catch(Exception e)
        {
           HUMExceptionHelper.logErrors(e, 'HUMUpdateCaseTaskHelper', 'getMatcheViewBasedOnPriority');
        }
        return sQueueViewName;
    }
    
    /**
    * Returns List<Queue_View__c>
    * <p>
    * This method is used to validate views based on priority.
    *
    * @param  List<Queue_View__c> lstViews
    * @param  map<string,string> mapPriorit
    * @param  integer count
    * @return List<Queue_View__c>
    * @see
    */  
    private static List<Queue_View__c> checkViewPriority(List<Queue_View__c> lstViews,map<string,string> mapPriority,integer count)
    {
        List<Queue_View__c> lstFinal =new List<Queue_View__c>();
        for(Queue_View__c obj :lstViews)
        {       
            if(mapViewItems.containsKey(obj.Id) && String.isNotBlank((string)obj.get('Expression__c')))
            {
                for(Queue_View_Item__c objItem :mapViewItems.get(obj.Id))
                    {
                    if(ExpressionEvaluator_H_HUM.queViewItemMap.get(objItem.Name)!= null && ExpressionEvaluator_H_HUM.queViewItemMap.get(objItem.Name))
                    {
                        if(mapPriority.get(objItem.API_Name__c) !=null && Integer.valueOf(mapPriority.get(objItem.API_Name__c))==count)
                        {
                            lstFinal.Add(obj);
                            break;
                        }  
                    }
                }
            }   
            else if(mapViewItems.containsKey(obj.Id))
            {
                for(Queue_View_Item__c objItem :mapViewItems.get(obj.Id))
                {
                    if(mapPriority.get(objItem.API_Name__c) !=null && Integer.valueOf(mapPriority.get(objItem.API_Name__c))==count)
                    {
                        lstFinal.Add(obj);
                        break;
                    } 
                }
            }            
        }
        return lstFinal;
    }
    
    /**
    * Returns Boolean
    * <p>
    * This method is used to check the filter conditions.
    *
    * @param  Queue_View__c oQueueView
    * @param  sObject oCaseTaskRec
    * @param  List<Queue_View_Item__c> lstQVI
    * @param  CTCI_Junction__c oCtciRec
    * @return  Boolean
    * @see
    */ 
    public static Boolean checkFilterConditions(Queue_View__c oQueueView,sObject oCaseTaskRec, List<Queue_View_Item__c> lstQVI, CTCI_Junction__c oCtci)
    {  
        CTCI_Junction__c oCtciRec= new CTCI_Junction__c();
        String sApiName='';
        set<String> setFieldValues=new set<String>();
        Integer iCount = 0 ;
        Integer iTotal = 0 ;
        Boolean bIsMatched=true;
        Set<string> queViewItemSet = new Set<string>();
        if(String.isNotBlank((string)oQueueView.get('Expression__c')))
        {
            Boolean validQVI = false;
            if(ExpressionEvaluator_H_HUM.validateQueueView(oQueueView, oCaseTaskRec, lstQVI, mapOfFilters, oCtci)) validQVI = true;
            return validQVI;
        }
        else if(lstQVI.size() > 0)
        {
            itotal++;
            sApiName = lstQVI[0].API_Name__c;
            for(integer i=0;i<lstQVI.size();i++)
            {
                if(!mapOfFilters.containsKey(lstQVI[i].API_Name__c))
                {
                    bIsMatched=false;
                    break;
                }
                if(lstQVI[i].API_Name__c != sApiName)
                {
                    setFieldValues=new set<String>();          
                    itotal++;
                    sApiName =lstQVI[i].API_Name__c;
                }
                setFieldValues.Add(lstQVI[i].Field_Value__c.trim().toLowerCase());  
                
                if((i < (lstQVI.size()-1) && lstQVI[i+1].API_Name__c != sApiName) || (i == lstQVI.size()-1))
                {
                    String tempApiName = mapOfFilters.get(sAPIName).API_Name__c;
                    String tempObjFieldValue = '';
                    if(oCaseTaskRec.getSObjectType() == Schema.Case.getSObjectType())
                    {
                     if(oCtci!=null&&(octci!=octciRec)) 
                      {   
                          if(tempApiName.toLowerCase() == GLOBAL_CONSTANT_HUM.CASETRANSFER_CL_HUM)
                          { 
                             tempObjFieldValue = (String)oCtci.getSobject('classification__r').get('Name');   
                          }
                          if(tempApiName.toLowerCase() == GLOBAL_CONSTANT_HUM.CASETRANSFER_INTENT_HUM)
                          {   
                             tempObjFieldValue = (String)oCtci.getSobject('Intent__r').get('Name');
                          } 
                      }
                    }
                    if(oCaseTaskRec.getSObjectType() == Schema.Task.getSObjectType() || (tempApiName.toLowerCase() != GLOBAL_CONSTANT_HUM.CASETRANSFER_CL_HUM && tempApiName.toLowerCase() != GLOBAL_CONSTANT_HUM.CASETRANSFER_INTENT_HUM))
                    {
                        tempObjFieldValue = (string)oCaseTaskRec.get(tempApiName);
                    }
                    if(bSwitch_1790946)
                    {
                    
                        if(String.isBlank(tempObjFieldValue) && lstQVI[i].Field_Operator__c != GLOBAL_CONSTANT_HUM.CASETRANSFER_NOT_EQUALS_HUM)
                        {
                            bIsMatched=false;
                            break;
                        }
                        if(String.isNotBlank(tempObjFieldValue))
                        {
                          tempObjFieldValue = tempObjFieldValue.trim().toLowerCase();
                            if((lstQVI[i].Field_Operator__c==GLOBAL_CONSTANT_HUM.CASETRANSFER_EQUALS_HUM && isInSet(setFieldValues,tempObjFieldValue)) || (lstQVI[i].Field_Operator__c==GLOBAL_CONSTANT_HUM.CASETRANSFER_NOT_EQUALS_HUM && !setFieldValues.contains(tempObjFieldValue)))
                            {
                                icount++;
                            }
                        }
                        else if(String.isBlank(tempObjFieldValue) && lstQVI[i].Field_Operator__c == GLOBAL_CONSTANT_HUM.CASETRANSFER_NOT_EQUALS_HUM)
                        {
                          icount++;
                        }
                    }
                    else
                    {

                        if(String.isBlank(tempObjFieldValue))
                        {
                            bIsMatched=false;
                            break;
                        }
                        tempObjFieldValue = tempObjFieldValue.trim().toLowerCase();
                        if((lstQVI[i].Field_Operator__c==GLOBAL_CONSTANT_HUM.CASETRANSFER_EQUALS_HUM && isInSet(setFieldValues,tempObjFieldValue)) || (lstQVI[i].Field_Operator__c==GLOBAL_CONSTANT_HUM.CASETRANSFER_NOT_EQUALS_HUM && !setFieldValues.contains(tempObjFieldValue)))
                        {
                            icount++;
                        }
                    }
                }
            }
             return (!(bIsMatched && icount == itotal && itotal != 0) ? false : bIsMatched );
        }
        else return false;
        
    }
    
    /**
    * Returns Set<String>
    * <p>
    * This method is used to find the updated queue names based on views/view items/queue priority.
    * @param void
    * @return  Set<String>
    */ 
    public static Set<String> getUpdatedQueueNames()
    {
        Set<String> setQueueNames=new Set<String>();
        DateTime dtEndDate=DateTime.Now();
        Date dtPastDate = Date.today().AddDays(-GLOBAL_CONSTANT_HUM.DEFAULTBATCHLIMIT);
        DateTime dtStartDate = DateTime.newInstance(dtPastDate.year(),dtPastDate.month(),dtPastDate.day(),0,0,0);
        HUMQueueViewBatchSettings__c objBatchSetup = HUMQueueViewBatchSettings__c.getValues(GLOBAL_CONSTANT_HUM.TASKTRIGGER_EXECUTION_HUM);
        If(objBatchSetup.BatchExecutionDate__c != null) 
        {
            dtStartDate =objBatchSetup.BatchExecutionDate__c;
        }
        
        for (Queue_View__c obj: [Select Work_Queue_Name__r.Name From Queue_View__c where LastModifiedDate > : dtStartDate AND LastModifiedDate<= : dtEndDate All ROWS])
        {
            setQueueNames.Add(obj.Work_Queue_Name__r.Name);
        }
       
        for(Queue_View_Item__c obj: [Select Queue_View__r.Work_Queue_Name__r.Name from Queue_View_Item__c where LastModifiedDate > : dtStartDate AND LastModifiedDate<= : dtEndDate])
        {
            setQueueNames.Add(obj.Queue_View__r.Work_Queue_Name__r.Name);
        }
        
        For(Work_Queue_Priority__c obj : [Select Work_Queue_Name__r.Name from Work_Queue_Priority__c where LastModifiedDate > : dtStartDate AND LastModifiedDate<= : dtEndDate])
        {
            setQueueNames.Add(obj.Work_Queue_Name__r.Name);
        }

        objBatchSetup.TriggerExecutionRequired__c=false;
        objBatchSetup.BatchExecutionDate__c =dtEndDate;
        update objBatchSetup;
        return setQueueNames;
    }
    
    /**
    * Returns boolean
    * <p>
    * This method is used to check if its in set
    *
    * @param  set<String> setValues 
    * @param  string tempstr
    */
    public static boolean isInSet(set<String> setValues, string tempstr)
    {
        Boolean inSet = false;
        for (string str: setValues)
        {
           str = str.toLowerCase();
            if(string.isNotBlank(tempstr) && str == tempstr) 
            {
                inSet = true;
                break;
            }
        }
        return inSet;
    }
}