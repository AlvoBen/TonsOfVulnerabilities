/*
LWC Name        : PharmacyHpieOrderLinesDetailHum.html
Function        : LWC to display pharmacy HPIE order  item details.

Modification Log:
* Developer Name                  Date                         Description
*
* Atul Patil                      08/25/2023                  US - 3139633
* Swapnali Sonawane               10/23/2023                  US - 5058187 Pharmacy Edit Order
* Jonathan Dickinson              02/29/2024                  User Story 4835317: T1PRJ0865978 - MF 27481 - C12; Mail Order Management; Pharmacy - Mail Order Pharmacy page gap- Member Consent (Lightning)
****************************************************************************************************************************/
import { LightningElement, api } from 'lwc';
import pharmacyHpieOrderLinesEditDetailHum from './pharmacyHpieOrderLinesEditDetailHum.html'
import pharmacyHpieOrderLinesDetailHum from './pharmacyHpieOrderLinesDetailHum.html'

export default class PharmacyHpieOrderLinesDetailHum extends LightningElement {
    @api item;
    @api payer;
    @api calledFromEditOrder = false;
    @api memberConsent = false;
    @api isMemberConsentQueue = false;
    @api isMemberConsentRequired = false;
    consentDisplayed = '';
    showConsentGiven = false;

    connectedCallback() {
        if (this.isMemberConsentRequired) {
            this.processRxConsent();
        }
    }

    render(){
        if(this.calledFromEditOrder){
            return pharmacyHpieOrderLinesEditDetailHum;
        }
        else{
            return pharmacyHpieOrderLinesDetailHum;
        }
    }

    processRxConsent() {
        if (this.item?.rxConsent === 'A') {
            this.consentDisplayed = 'Approved';
        } else if (this.isMemberConsentQueue
        && !this.memberConsent
        && (this.item.rxConsent === 'P' || this.item.rxConsent === 'R')) {
            this.showConsentGiven = true;
        } else {
            this.consentDisplayed = '';
        }
    }

    get checkrefilllength() {
        return this.item?.refillsRemaining
            && this.item?.refillsRemaining?.toString()?.length >= 2 ? true : false;
    }

    deletePrescriptionItem(){
        this.dispatchEvent(new CustomEvent('deletescriptkey', {
            detail: {
                deletescriptkey: this.item.key
            }
          }));
    }

    handleConsentGiven() {
        this.consentDisplayed = 'Approved';
        this.showConsentGiven = false;

        this.dispatchEvent(new CustomEvent('consentgiven', {
            detail: {
                rxKey: this.item.key,
            }
        }));
    }
}