/*******************************************************************************************************************************
LWC JS Name : accountDetailConsumerIdsHum.js
Function    : This JS serves as controller to accountDetailConsumerIdsHum.html. 

Modification Log: 
Developer Name           Code Review                      Date                         Description
*--------------------------------------------------------------------------------------------------------------------------------
* Pradeep Dani                                          12/18/2020                    initial version
* Ashish Kumar                                          10/27/2021                    US-2607545
* Kajal Namdev                                          11/19/2021                    US-2440592
*********************************************************************************************************************************/
import { LightningElement, track, api,wire} from 'lwc';
import retrieveConsumerIds from '@salesforce/apex/AccountConsumerIdComponent_LC_HUM.retrieveConsumerIds';
import { getLabels } from "c/customLabelsHum";
import ACCOUNT_RECORDTYPE_FIELD from '@salesforce/schema/Account.RecordTypeId';
import { getRecord } from "lightning/uiRecordApi";
import { getUserGroup } from 'c/crmUtilityHum';

export default class AccountDetailConsumerIdsHum extends LightningElement {
    @track oConsumerIdLst;
    @api recordId;
    @track labels = getLabels();
    @track recordTypeName;
    @track bProvider = true;
    @wire(getRecord, { recordId: '$recordId', fields: [ACCOUNT_RECORDTYPE_FIELD] })
    getAccount({ error, data }){
        if(data){
            this.recordTypeName = data.recordTypeInfo.name;
            this.getAllConsumerId(this.recordTypeName);
        }else if(error) {
                console.log('error in wire: ', error);
        }
    };
    getAllConsumerId(recType) {
            const {bProvider} = getUserGroup();
                if(recType === 'Member' && !bProvider) {
                    this.bProvider = false;
                } else {
                    this.oConsumerIdLst = [];
                    retrieveConsumerIds({ AccountId: this.recordId }).then((result) => {
                        this.oConsumerIdLst = result;
                    });
                }
               
    }
}