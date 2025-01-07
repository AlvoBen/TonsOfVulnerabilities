/******************************************************************************
Apex Class Name  : DEAAProcess_Case_SC_HUM
Version          : 1.0
Created Date     : Jan 30, 2020
Function         : Schedule the DEAAProcess_Case_B_HUM Batch Class.
Test Class Name  : DEAAProcess_Case_SC_T_HUM
*******************************************************************************

Modification Log:

* Developer Name        Code Review     Date             Description
*------------------------------------------------------------------------------
* Moshitha Gunasekaran                  01/30/2020        Original Version
******************************************************************************/
global With sharing  Class DEAAProcess_Case_SC_HUM implements Schedulable
{
    /**
        *  @name execute
        *  @param SchedulableContext
        *  @return NA
        *  @throws NA
    */
    
    global void execute(SchedulableContext ctx)
    {
       DEAAProcess_Case_B_HUM deaaBatchJob = new DEAAProcess_Case_B_HUM();       
       Database.executeBatch(deaaBatchJob, 200);
    }
}