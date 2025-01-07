/*******************************************************************************************************************************
LWC JS Name : accountDetailInteractionsHum.js
Function    : This JS serves as controller to accountDetailInteractionsHum.html. 
Modification Log: 
Developer Name           Code Review                      Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Pradeep Dani                                        12/18/2020                    initial version(azure # 1648022)
* Mohan Kumar N                                       03/22/2021                    US:1892975
* Ritik Agarwal                                       09/20/2021                    Refactored the code realted to Time Date zone
* Ritik Agarwal                                       10/28/2021                    US-2440592
* Ashish Kumar                                          10/27/2021                  US-2607545
* Abhishek Mangutkar			 					  05/09/2022				    US-2871585
* Abhishek Mangutkar								  08/09/2022                    DF - 5542
*********************************************************************************************************************************/
import { LightningElement, track, api, wire } from 'lwc';
import getIntsMethod from '@salesforce/apex/AccountDetailInteractions_LC_HUM.getInteractions';
import { getLabels } from 'c/customLabelsHum';
import { getRecord } from 'lightning/uiRecordApi';


export default class AccountDetailInteractionsHum extends LightningElement {
    @api recordId;
    @api nameOfScreen;
    @api isOverflow;
    @api bInfiniteScroll;
    @api title;
    @track oIntLst;
    @track oError;
    @track isDataLoaded = false;
    @track sIntTitle;
    @track enableNewInteraction = true;
    @track bShowButton = false;
    @api sRecordTypeName;
    @api showViewAll = false;
    @api breadCrumbItems;
    @track labels = getLabels();
    @track oViewAllParams = {};
	@track loggingRelatedField = [{
        label : "Interaction Number",
        mappingField : "Name"
    }];
	@track loggingScreenName;
  
    @wire(getRecord, {
        recordId: '$recordId',fields: ['Account.Name'] 
     })
     wiredAccount({
        error,
        data
     }) {
        if (data) {
           this.sRecordTypeName = data.recordTypeInfo.name;
           this.fetchInteractionData(this.sRecordTypeName);
        }
        else if(error){
           console.log('error in wire--',error);   
        }
    }

    fetchInteractionData(recType) {
        this.showViewAll = this.bInfiniteScroll===undefined;
        if (recType == 'Provider') {
            this.bShowButton = true;
        }
        this.oViewAllParams = {
            sRecordTypeName : recType,
            sRecordId : this.recordId
        }

        getIntsMethod({ sAccId: this.recordId }).then(data => {
            if (data) {
                this.oIntLst = data;
                this.isDataLoaded = true;
                this.oError = undefined;
                this.sIntTitle = this.labels.interactionsHeadingHum + ' (' + data.length + ')';
            }
        }).catch(error => {
            if (error) {
                console.log('>error>', error);
                this.oError = error;
                this.oLinks = undefined;
            }
        });
    }
}