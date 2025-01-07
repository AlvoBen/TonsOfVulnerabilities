/*
Component        : Pharmacy Finance Tab Container
Version          : 1.0
Created On       : 11/07/2023
Function         : Component to display subtabs on Finance tabs

Modification Log: 
* Developer Name                       Date                         Description
* Monali Jagtap                       18/08/2023                   US-4943744 T1PRJ0870026 MF27456 HPIE/CRM -C12 Mail Order Management-Pharmacy-Finance tab-Lightning/Classic
* Jonathan Dickinson			  	  09/04/2023				   User Story 4999697: T1PRJ0870026 MF27456 HPIE/CRM - SF Tech- Mail Order Management - Pharmacy - Finance tab - Lightning - Edit Credit Card, One time payment
* Vishal Shinde                       10/10/2023                 User Story 5002422- Mail Order Management; Pharmacy - identify Error Messaging and parameters (Lightning)
*------------------------------------------------------------------------------------------------------------------------------
*/
import { LightningElement, api } from 'lwc';

export default class PharmacyHpieFinanceTabContainerHum extends LightningElement {
    @api recordId;
    @api financeDetails;
    @api enterpriseId;
    @api userId;
    @api demographicsDetails;
    @api profileDetails;
    @api financeServiceError;

    handleCardSave() {
        this.dispatchEvent(new CustomEvent("cardsavesuccess"));
    }
}