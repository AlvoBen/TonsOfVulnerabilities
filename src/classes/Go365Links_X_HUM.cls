/*******************************************************************************************************************************************************
Apex Class Name  : Go365Links_X_HUM
Version          : 1.0
Created Date     : 10/2/2017
Function         : This is controller class for visual force page 'Go365_VF_HUM'.This is used to retrieve custom links dynamically.

*****************************************************************************************************************************************************

Modification Log:

* Developer Name            Review Number            Date                       Description
-----------------------------------------------------------------------------------------------------------------------------------------------------
* Muralidhar Kollu             70895               10/2/2017                 Original Version
************************************************************************************************************************************************/

public with sharing class Go365Links_X_HUM extends SObjectDescriptorImpl_H_HUM
{
    public Go365Links_X_HUM (ApexPages.StandardController stdController) 
    { 
        
    } 
   
    /*
    * Method Name   :    getRecords
    * Description   :    This method always returns either NULL or List of sObjects depending upon implementation. Use this method to get list of multiple records
                         in form of sObject.
    * Return Type   :    List of sObjects
    * Parameters    :    NA
    */  
    public List<sObject> getRecords()
    {
        return NULL;
    }
   
    /*
    * Method Name   :    getRecord
    * Description   :    This method always returns either NULL or List of sObjects depending upon implementation. Use this method to get list of multiple records
                         in form of sObject.
    * Return Type   :    List of sObjects
    * Parameters    :    NA
    */
     public sObject getRecord()
     {
        return NULL;
     }
    
    /*
     * Method Name   :    getThis 
     * Description   :    Store the Current Page Instance
     * Return Type   :    DisplayLinksManager_C_HUM
     * Parameters    :    NA
     */
     public Go365Links_X_HUM getThis() 
     {
        return this;
     }
}