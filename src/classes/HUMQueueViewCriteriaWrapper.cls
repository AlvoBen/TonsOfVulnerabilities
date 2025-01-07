/****************************************************************************
Apex Class Name  : HUMQueueViewCriteriaWrapper
Version          : 1.0 
Created On       : 08/12/2015
Function         : Wrapper class for the queue view criteria

Modification Log :
* Developer Name          Code Review                   Date                       Description
*-----------------------------------------------------------------------------------------------------------------------
* Pradeep Kothapally                                  08/12/2015                 Original Version
*************************************************************************************************************************/


global class HUMQueueViewCriteriaWrapper implements Comparable
{
    global String sName{get;set;}
    global Integer iCounter{get;set;}
    global String iQVId{get;set;}
    global Queue_View_Item__c objQViewItem{get;set;}
    
    global HUMQueueViewCriteriaWrapper(String Name, String sQVType, Queue_View_Item__c objViewItem, Integer counter)
    {
        sName = Name;
        iQVId = sQVType;
        iCounter = counter;
        objQViewItem = objViewItem;
    }
    
    global integer compareTo(Object objToCompare)
    {
        return Integer.valueOf(((HUMQueueViewCriteriaWrapper)objToCompare).iCounter-iCounter);
    }
}