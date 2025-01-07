/*****************************************************************************************************************************************************************************
Apex Class Name : EmailTaskCallBackRemainder_SC_HUM
Version         : 1.0
Created On      : 10/25/2021
Function        : Apex code used to schedule EmailTaskCallBackRemainder_T_HUM
Test Class      : 

Modification Log: 
* Developer Name               Code Review                         Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Vinay Lingegowda                                                 10/25/2021                  USER STORY 2769842 T1PRJ0001827 - IR 5050 - Send Task Call Back reminder emails to supervisors (RF)
*************************************************************************************************************************/

public with sharing class EmailTaskCallBackRemainder_SC_HUM implements Schedulable {
    /**
     * Schedule EmailTaskCallBackRemainder_B_HUM process
     * @param SchedulableContext sc
     * @return void
     */
    public void execute(SchedulableContext sc) {
        try {
            EmailTaskCallBackRemainder_B_HUM oBatchObj = new EmailTaskCallBackRemainder_B_HUM ();
            Database.executebatch(oBatchObj, 100);

        } catch(Exception ex) {
            HUMExceptionHelper.logErrors(ex, 'EmailTaskCallBackRemainder_SC_HUM ', 'execute');
        }
    }
}