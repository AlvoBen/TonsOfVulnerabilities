/*******************************************************************************************************************************
LWC JS Name : acountDetailSubgroupsDivisionsHum.js
Function    : This JS serves as controller to accountDetailSubgroupsDivisionsHum.html. 

Modification Log: 
Developer Name           Code Review                      Date                         Description
*--------------------------------------------------------------------------------------------------------------------------------
* Ashish Kumar                                          12/18/2020                    initial version
*Surendra Vemulapalli                                   09/16/2021                    Added getRecord method
* Ritik                                                 10/24/2021                    US: 2638348
* Vardhman Jain                                         04/22/2022                    US-3046016_3045989: Group Account logging stories changes.
*********************************************************************************************************************************/
import {
    LightningElement,
    track,
    api,
    wire
} from 'lwc';
import groupDetail from '@salesforce/apexContinuation/GroupDetailDivisionSubgroup_LC_HUM.getGroupInfo';
import { NavigationMixin } from 'lightning/navigation';
import { getDivisionLayout } from './layoutConfig';
import { getRecord } from 'lightning/uiRecordApi';
import { getLabels } from 'c/customLabelsHum';
 

export default class AccountDetailSubgroupsDivisionsHum extends NavigationMixin(LightningElement) {
    @track groupSubDivisionList;
    @track noResults = false;
    @api recordId;
    @track divisionLayout = [];
    @track labels = getLabels();
	@track loggingRelatedField = [{
        label : "Division-Class ID Number",
        mappingField : "sSubGroupID"
    }];
    connectedCallback() {
            groupDetail({
                sAccId: this.recordId
            }).then(res => {
                if (res) {
                    if (res.listGrouDivisionSubGroup && res.listGrouDivisionSubGroup.length > 0) {
                        this.groupSubDivisionList = res.listGrouDivisionSubGroup;
                        this.noResults = false;
                    } else {
                        this.noResults = true;
                    }
                }
                else{
                    this.noResults = true;
                }
            }).catch(error => {
                console.log('Error Occured', error);
            });
    }
/**
Fetching Source Platform Code value to decide the model to display
*@param {*} $recordId 
 */
    @wire(getRecord, { recordId: '$recordId', fields: ['Account.Source_Platform_Code__c'] })
    wiredAccount({ error, data }) {
      if (data) {
         this.divisionLayout = getDivisionLayout(data.fields.Source_Platform_Code__c.value);
      }
      else if (error) {
         console.log('Error Occured', error);
      }
   }

      /**
 * Open External MVTx link page 
 * @param {*} navItem 
 */
    handleclicked(event){
        const config = {
            type: 'standard__webPage',
            attributes: {
                url: event.detail.payLoad
            }
        };
        this[NavigationMixin.Navigate](config);
    }
}