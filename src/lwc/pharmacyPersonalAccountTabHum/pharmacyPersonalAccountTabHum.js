/*******************************************************************************************************************************
LWC JS Name : pharmacyPersonalAccountTabHum.js
Function    : This LWC component serves as input for toPharamcy Personal Account Details Tab  data in CRM strides

Modification Log: 
Developer Name           Code Review              Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Pallavi Shewale                 		        07/28/2021                    US: 2364907- Search- Add Humana Pharmacy Account Number to the Search screen
* Abhishek Mangutkar                            10/28/2022					  US#3897898
*********************************************************************************************************************************/
import { LightningElement, track, api } from 'lwc';
import { invokeWorkspaceAPI } from 'c/workSpaceUtilityComponentHum';
import { getAccountDetails } from 'c/genericAccountDetailsHum';
import { NavigationMixin } from 'lightning/navigation';
import { updateTabLabel } from 'c/genericUpdateTabLabel';
export default class TabLabelSetHum extends NavigationMixin(LightningElement) {
    @api fName;
    @api lName;
    @api recId;
    @track accountDetails;

    connectedCallback() {
        this.getAccountData();
    }

    getAccountData() {
        Promise.all([this.getAccountDetails()]).then(result => {
            console.log(result);
        }).catch(error => {
            console.log(error);
        })
    }

    // To Get Account Related Details
    getAccountDetails() {
        return new Promise((resolve, reject) => {
            getAccountDetails(this.recId)
                .then(result => {
                    this.accountDetails = result && Array.isArray(result) && result?.length > 0 ? result[0] : null;
                    resolve(true);
                }).catch(error => {
                    console.log(error);
                    reject(error);
                });
        });
    }


    /*Update the Humana Pharmacy Tab Name*/
    updateTabName() {
        setTimeout(() => {
            updateTabLabel(`${this.fName} ${this.lName}`, 'custom:custom65', 'Mail Order Pharmacy');
        }, 1000);
    }


    // Navigate to Account Page
    async navigateToAccount() {
        let pageref = {
            type: 'standard__recordPage',
            attributes: {
                recordId: this.recId,
                objectApiName: 'Account',
                actionName: 'view'
            },
        }
        let focusedTab = await invokeWorkspaceAPI('getFocusedTabInfo');
        if (await invokeWorkspaceAPI('isConsoleNavigation')) {
            await invokeWorkspaceAPI('openSubtab', {
                parentTabId: (focusedTab.hasOwnProperty('isSubtab') && focusedTab.isSubtab) ? focusedTab.parentTabId : focusedTab.tabId,
                pageReference: pageref
            });
        }
    }


    renderedCallback() {
        this.updateTabName();
    }

    handleEmailClick(event) {
        event.preventDefault();
    }

}