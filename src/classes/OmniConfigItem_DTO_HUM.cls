/*
  Apex Class Name  : OmniConfigItem_DTO_HUM
  Version          : 1.0 
  Created Date     : Mar 01 2019
  Function         : DTO class to insert/update/rollback Omni Configuration Item using Devops helper class addUpdateOmniConfigItem methods
  Test Class       : 
*********************************************************************************************************************************************************************************
Modification Log:

* Modification Id           Developer Name             Review Number               Date                       Description
*---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* 1.0                       Syed Mubarak T                                      03/01/2019                      Original Version
*---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------*/


public class OmniConfigItem_DTO_HUM
{
    public string BusinessConfigName = ''; // Name of the Business Area and Name of the Configuration Item
    public string workItemName = ''; // Name of the Object E.g., Case,Chatbot etc
    public integer maxNoOfWorkItems; // Maximum number of Work Items for a Single Omni Queue.
    public integer maxLimitofBusinessArea; // maximum no of Work items for the Business Area. (total work items)
    public String DefaultOmniQueueName = ''; // Name of the Default Omni Queue for the Business Area 
    public String OverflowAssigneeQueueName = ''; // Name of the Omni Overflow Assignee Queue for Routing Configuration of this Business Area
    public Boolean isUpdate; // True if it is an Update Operation
    public Integer totalOrgLimit = 150000; // total Org limit for Work items. Do not assign any value for this. Modify only when the Limit for total org was changed.
    
}