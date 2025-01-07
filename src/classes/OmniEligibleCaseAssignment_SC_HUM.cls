/******************************************************************************
Apex Class Name  : OmniEligibleCaseAssignment_SC_HUM
Version          : 1.0
Created Date     : Feb 06 2018
Function         : Schedule the OmniEligibleCaseAssignment_B_HUM Batch Class.
Test Class Name  : OmniEligibleCaseAssignment_SC_T_HUM
*******************************************************************************

Modification Log:

* Developer Name   Code Review    Date             Description
*------------------------------------------------------------------------------
* Mohammed Noor                  02/06/2018        Original Version
* Sagar MN                       01/14/2019        REQ - 416054 Checmarx Sharing Vulnerability Fix
******************************************************************************/
public with sharing Class OmniEligibleCaseAssignment_SC_HUM Implements Schedulable
{
    /**
        *  @name execute
        *  @param SchedulableContext
        *  @return NA
        *  @throws NA
    */
    
    public void execute(SchedulableContext ctx)
    {
       OmniEligibleCaseAssignment_B_HUM omniBatchJob = new OmniEligibleCaseAssignment_B_HUM();       
       Database.executeBatch(omniBatchJob, 2000);
    }
}