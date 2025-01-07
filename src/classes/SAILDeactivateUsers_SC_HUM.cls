/******************************************************************************
Apex Class Name  : SAILDeactivateUsers_SC_HUM
Version          : 1.0
Created Date     : Nov 09 2016
Function         : Schedule the SAIL_DeactivateUsers_Batch Class.
Test Class Name  : SAILDeactivateUsers_SC_T_HUM
*******************************************************************************

Modification Log:

* Developer Name   Code Review    Date             Description
*------------------------------------------------------------------------------
* Kent Sanders      48578       11/09/2016     Initial creation of the class.
* Avinash Choubey   137529      10/11/2017     REQ 332365 - chnaged class to with Sharing
******************************************************************************/
Global With sharing  Class SAILDeactivateUsers_SC_HUM Implements Schedulable
{
    /**
        *  @name execute
        *  @param SchedulableContext
        *  @return NA
        *  @throws NA
    */
    
    global void execute(SchedulableContext ctx)
    {
       SAILDeactivateUsers_B_HUM deactUsr= new SAILDeactivateUsers_B_HUM();       
       Database.executeBatch(deactUsr);
    }
}