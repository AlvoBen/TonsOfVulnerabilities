/*****************************************************************************************************************************************************************************
Apex Class Name : EmailOpenInventory_SC_HUM 
Version         : 1.0
Created On      : 05/12/2021
Function        : Apex code used to schedule EmailOpenInventory_B_HUM
Test Class      : 

Modification Log: 
* Developer Name               Code Review                         Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Vijaykumar											        05/12/2021						  Initial version
*************************************************************************************************************************/

public with sharing class EmailOpenInventory_SC_HUM implements Schedulable {
    /**
     * Schedule EmailOpenInventory_B_HUM process
     * @param SchedulableContext sc
     * @return void
     */
    public void execute(SchedulableContext sc) {
        try {
            EmailOpenInventory_B_HUM oBatchObj = new EmailOpenInventory_B_HUM();
            Database.executebatch(oBatchObj, 25);

        } catch(Exception ex) {
            HUMExceptionHelper.logErrors(ex, 'EmailOpenInventory_SC_HUM', 'execute');
        }
    }
}