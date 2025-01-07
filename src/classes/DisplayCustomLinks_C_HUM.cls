/*******************************************************************************************************************************************************
Apex Class Name  : DisplayCustomLinks_C_HUM
Version          : 1.0
Created Date     : 10/22/2015
Function         : This is controller class for visual force page 'DisplayCustomLinks_VF_HUM'.This is used to retrieve custom links dynamically.
Test Class       : DisplayCustomLinks_T_HUM
*****************************************************************************************************************************************************

Modification Log:

* Developer Name            Review Number            Date                       Description
-----------------------------------------------------------------------------------------------------------------------------------------------------
* Manish Kumar                                       10/22/2015                 Original Version
* T Prasanna Sai Kumar                               03/22/2021                 Req 1912034 - Dynamic Links Vulnerabilities - DynamicCustomLinksHorizontal_CMP_HUM,DisplayCustomLinks_C_HUM,DisplayCustomLinks_T_HUM
************************************************************************************************************************************************/

public with sharing class DisplayCustomLinks_C_HUM extends SObjectDescriptorImpl_H_HUM
{
    /**
    * Returns a List of sObjects
    * <p>
    * This method always returns either NULL or List of sObjects depending
    * upon implementation. Use this method to get list of multiple records
    * in form of sObject.
    * 
    * @return    List of sObjects
    * @see       SObjectDescriptor_CI_HUM#getRecords()
    */
    public List<sObject> getRecords()
    {
        return  new List<sObject>();
    }
    
    /**
    * Returns an instance of sObject
    * <p>
    * This method always returns either NULL or an instance of sObject depending
    * upon implementation.
    * 
    * @return    An instance of sObject
    * @see       SObjectDescriptor_CI_HUM#getRecord()
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
    public DisplayCustomLinks_C_HUM getThis() 
    {
        return this;
    }
}