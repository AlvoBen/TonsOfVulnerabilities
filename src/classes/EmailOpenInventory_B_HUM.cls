/*******************************************************************************************************************************
Apex Class Name : EmailOpenInventory_B_HUM
Version         : 1.0
Created On      : 04/08/2021
Function        : Nightly batch process to send email notification to CCA who has open cases and tasks
Test Class      : EmailOpenInventory_T_HUM 

Modification Log: 
* Developer Name                         Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Vijayakumara MN / Subhamay Mandal                                 04/08/2021                 Initial version
*******************************************************************************************************************************/
global with sharing Class EmailOpenInventory_B_HUM Implements Database.Batchable<SObject> {
    // list of user id from console
    private Set<Id> setId;
    private EmailOpenInventory_H_HUM oHelper;
    global EmailOpenInventory_B_HUM() {
        oHelper = new EmailOpenInventory_H_HUM();
    }
    
    global EmailOpenInventory_B_HUM(Set<Id> setId) {
        this();
        this.setId = setId;
    }

    /**
     * start batch to get all list of CCA
     */
    global Database.QueryLocator start(Database.BatchableContext BC) {
        return Database.getQueryLocator(oHelper.getQueryCCA(setId));
    }

    /**
     * process open inventory in execute method with a set
     */
    global void execute(Database.BatchableContext BC, List<User> scopeUsers) {
        oHelper.processOpenInventory(scopeUsers);
    }

    /**
     * Finish method call for batch
     */
    global void finish(Database.BatchableContext BC) {
        
    }
}