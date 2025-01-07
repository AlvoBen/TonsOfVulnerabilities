/*******************************************************************************************************************************
LWC JS Name : memberPlanLookupHum.js
Function    : This JS serves as helper to memberPlanLookupHum.html 

Modification Log: 
Developer Name           Code Review                      Date                         Description
*--------------------------------------------------------------------------------------------------------------------------------
* Gowthami Thota                                          06/29/2023                   initial version
* Gowthami Thota                                          08/16/2023                   DF-7953- QAS_Lightning
* Prasuna Pattabhi                                        10/19/2023                   User Story 4828065: T1PRJ0865978 - C06; Case Management; Pharmacy - Case Edit- Block Access to Non-Medical Plans
*********************************************************************************************************************************/
import { LightningElement, track, api } from 'lwc';
import getMemberPlans from '@salesforce/apex/PoliciesSearchResults_LC_HUM.getMemberPlanDtls';
import { getLabels } from 'c/customLabelsHum';
import { getLocaleDate, getFinalFilterList, hcConstants } from 'c/crmUtilityHum';
import { memPlanLayout } from './layoutModel';
import { loadStyle } from "lightning/platformResourceLoader";
import modal from "@salesforce/resourceUrl/modalMemPlan";
import isCRMFunctionalityONJS from '@salesforce/apex/HUMUtilityHelper_LWC.isCRMFunctionalityONJS';

export default class Customlookup extends LightningElement {
    @api recordId;
    @api selectedAccountId;
    @track labels = getLabels();
    @track oMemberPlanData = [];
    @track bInfiniteScroll;
    @api bShowPopupModal;
    @track Keyword;
    @track bLoadTable = false;
    @track oMemPlanLayout = [];
    @track selectedMemPlanId;
    @track filterObj = {};
    showViewAll;
    oViewAllParams;
    totalRecords;
    tempRecs;
    recIdToPass;
    @track isPharmacyUser = false;
    @track bisSwitchOn4828065 = false;

    connectedCallback() {
        loadStyle(this, modal);
        if(this.selectedAccountId != undefined && this.selectedAccountId != null){
            this.recIdToPass = this.selectedAccountId;
        }else{
            this.recIdToPass = this.recordId;
        }
        this.oMemPlanLayout = memPlanLayout;
        this.getSwitchData.call(this).then(result => {
            if (result && result === true) {
                this.handleOpenModal();
            }
        });
    }
    handleOpenModal() {
        this.bShowPopupModal = true;
        this.showPlanMemberRecs();
    }
    /* Apex call to get list of member plan records */
    showPlanMemberRecs() {
        getMemberPlans({ recId: this.recIdToPass })
            .then(result => {
                if (result && result.lstMemberPlans && result.lstMemberPlans.length > 0)
                {
                    if(this.bisSwitchOn4828065 == true){
                        this.isPharmacyUser = result.bIsPharmacyUser;
                    }
                    var response = JSON.parse(JSON.stringify(result.lstMemberPlans));
                    var bSwitchOn = this.bisSwitchOn4828065;
                    var bPharmacy = this.isPharmacyUser;
                    response.forEach(function (item) {
                        item.EffectiveFrom = getLocaleDate(item.EffectiveFrom);
                        item.EffectiveTo = getLocaleDate(item.EffectiveTo);                        
                        item.disabled = bSwitchOn == true?((item.ETL_Record_Deleted__c == true || (item.Product__c != 'MED' && bPharmacy == true))?true:false):(item.ETL_Record_Deleted__c == true ? true : false);                                                
                    })
                    this.tempRecs = response;
                    this.oMemberPlanData = response;
                    this.error = undefined;
                }
                this.bLoadTable = true;
                this.setTotalRecords(this.oMemberPlanData);
            })
            .catch(error => {
                this.error = error;
                this.tempRecs = undefined;
            })

    }
    /* To close the modal on click of close*/
    handleCloseModal() {
        this.bShowPopupModal = false;
        this.dispatchEvent(new CustomEvent('close'));
    }
    /* Method executed when onclick of Save on modal */
    handleSave() {
        if(this.selectedMemPlanId != undefined && this.selectedMemPlanId != ''){
            let selectedRecord = this.oMemberPlanData.filter(obj => obj.Id ==  this.selectedMemPlanId );
            const detail = { record: selectedRecord };
            const selectedmemberplan = new CustomEvent('memberplanselect', {detail});
            this.dispatchEvent(selectedmemberplan);
            
        }else if(this.bisSwitchOn4828065 == true && this.oMemberPlanData?.length == 1){
            var bNoSelection = this.oMemberPlanData[0].Product__c != 'MED' && this.isPharmacyUser == true ? true : false;
            if(this.oMemberPlanData[0].ETL_Record_Deleted__c != true && bNoSelection == false){
                let selectedRecord = this.oMemberPlanData.filter(obj => obj.Id == this.oMemberPlanData[0].Id );
                const detail = { record: selectedRecord };
                const selectedmemberplan = new CustomEvent('memberplanselect', {detail});
                this.dispatchEvent(selectedmemberplan); 
            }
        }else if(this.oMemberPlanData.length == 1 && this.oMemberPlanData[0].ETL_Record_Deleted__c != true){
            let selectedRecord = this.oMemberPlanData.filter(obj => obj.Id == this.oMemberPlanData[0].Id );
            const detail = { record: selectedRecord };
            const selectedmemberplan = new CustomEvent('memberplanselect', {detail});
            this.dispatchEvent(selectedmemberplan);  

        } 
        this.bShowPopupModal = false;
        this.handleCloseModal();
    }
    /* Method will be fired when user enters text in search field */
    SearchKeyword(event) {
        let count = 1;
        const me = this;
        if (event) {
            let value = event.target.value;
            if (value.length > hcConstants.MIN_SEARCH_CHAR) {
                me.iconName = 'standard:filter';
                this.filterObj['searchByWord'] = [value];
            }
            else { 
                me.iconName = 'utility:filterList';
                if (this.filterObj.hasOwnProperty('searchByWord')) {
                    delete this.filterObj['searchByWord'];
                    count = 0;
                }
            }
        } else {
            let tempObj = JSON.parse(JSON.stringify(this.filterObj));
            if (tempObj['searchByWord']) {
                me.template.querySelector('.keyword-field').value = tempObj['searchByWord'].toString();
            }
        }
        if (Object.keys(this.filterObj).length > 0) {
            this.getFilterList(this.tempRecs, this.filterObj)
        } else {
            if(count === 0){
                this.template.querySelector('c-standard-table-component-hum').computecallback(this.tempRecs);  
            }
            if(this.tempRecs !== undefined)
                this.totalRecords = this.tempRecs.length;
            
            }
    }

    getFilterList(data, filterProperties) {
        let filterListData = {};
        this.selectedMemPlanId = undefined;
        filterListData = getFinalFilterList(data, filterProperties, data, 'sCreatedOn');
        let uniqueChars = filterListData.uniqueList;
        this.oMemberPlanData = uniqueChars;
        this.setTotalRecords(this.oMemberPlanData);
        if (this.oMemberPlanData.length <= 0) {
            this.template.querySelector('c-standard-table-component-hum').noDataMessage = this.labels.policyNoRecordsHum;
        }
        this.template.querySelector('c-standard-table-component-hum').computecallback(this.oMemberPlanData);

    }
    /*Method to set total records to be displayed */
    setTotalRecords(oData) {
        if (oData) {
            this.totalRecords = Object.values(oData).length;
        }
    }
    /*Listening to event fired from standardTable when radio button selected*/
    handleRadioSelect(event) {
        this.selectedMemPlanId = event.detail.recId;
        
    }
    getSwitchData() {
        return new Promise((resolve, reject) => {
            isCRMFunctionalityONJS({ sStoryNumber: ['4828065'] })
                .then(result => {
                    this.bisSwitchOn4828065 = result['4828065'];
                    resolve(true);
                }).catch(error => {
                    console.log(error);
                    reject(false);
                })
        })
    }

}