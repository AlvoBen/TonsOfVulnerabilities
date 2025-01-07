/*
LWC Name        : custom_dashboard_CsrHum.js
Function        : JS for custom_dashboard_CsrHum.html

Modification Log:
* Developer Name                  Date                          Description
*------------------------------------------------------------------------------------------------------------------------------
* Shailesh B                      07/03/2022                    Original Version
*****************************************************************************************************************************/
import { api, LightningElement, track, wire } from 'lwc';
import getQueueList from '@salesforce/apex/HUMQueueSelection_LWC.getQueueList';
import { getColOptionalFields, getColStaticFields, getTotalColumns} from 'c/dashboardUtilityHum';
export default class Custom_dashboard_CsrHum extends LightningElement {

    @api isUserSupervisor;
    value = [];
    @track sColumnSet = [];
    isApplyPullButtonDisable = false;

    @track newColumns = [];
    isSupervisor;
    pageCount = 1;
    totalCount;
    @track isLoading = false;
    @track caseCount = 0;
    selectedValues = [];
    @track column1 =[];
    isLoading = true;
    caseCountColorClass= '';
    taskCountColorClass = '';

    connectedCallback(){
        this.isSupervisor = this.isUserSupervisor;
    }
    get columns(){
        return this.column1;
    }

    handlechildClick(event) {
        this.newColumns = [];
        event.detail.multiple.forEach(element => {
            if (element.selected && element.hasOwnProperty('selected') && !this.newColumns.some(el => el.label === element.label) && element.value !== 'selectall') {
                this.newColumns.push({
                    label: element.label,
                    fieldName: element.value,
                    initialWidth : element.hasOwnProperty('initialWidth') ? element.initialWidth : '',
                    type: element.hasOwnProperty('type') ? element.type : 'text',
                    typeAttributes : element.hasOwnProperty('typeAttributes') ? element.typeAttributes : '',
                    sortable: true,
                    wrapText: true
                });
            }
        });
        const objChild = this.template.querySelector('c-custom_dashboard_table_-csr-hum');
        objChild.addDeleteColumns(this.newColumns);
    }
    handleProfile(e) {
        this.showCaseUpdateError = false;

        if (e.detail.currentProfile){
            this.currentProfile = e.detail.currentProfile;

        }
        try {

            let columns = {};
            getTotalColumns(this.currentProfile).then(result => {
                columns = result;
            }).then(() => {
                this.column1 = columns.staticColumns;
                let options = columns.optColumns;
                this.template.querySelector('c-custom-multi-select-combobox-hum')?.setMenuItems(options);
                this.column1.forEach(item => {
                    if(item.hasOwnProperty('typeAttributes')){
                        item.typeAttributes.hasOwnProperty('label') &&
                            item.typeAttributes.label.hasOwnProperty('fieldName') ?
                            this.sColumnSet.push(item.typeAttributes.label.fieldName) : this.sColumnSet.push(item.fieldName);
                    }
                    else{
                        this.sColumnSet.push(''+item.fieldName+'');
                    }

                });
                this.totalCount = e.detail.totalCount;
                this.pageCount = 1;
                //clear existing rows

                this.template.querySelector('c-custom_dashboard_table_-csr-hum')?.clearRows();
                this.template.querySelector('lightning-input').value = null;
            }).catch(error => {
                console.log(error);
            });
        } catch (error) {
            console.log(error);
        }

    }
    handlePageCount(event) {
        this.pageCount = event.detail.pageCount;
        this.totalCount = event.detail.totalRecountCount;
    }

    handlePullWorkItem() {
        const objChild = this.template.querySelector('c-custom_filter_-csr-hum');
        objChild.pullWorkItems();
    }

    handleDisablepullwork(event){
        this.isApplyPullButtonDisable = event.detail.isApplyPullButtonDisable;
    }


    handleInputChange(event) {
        this.template.querySelector('c-custom_dashboard_table_-csr-hum').onSearchKeyUp(event.detail.value);
    }
    handleFetchOpenCaseTaskCount(event){
        let totalOpenCaseTask = 0;
        this.totalOpenCaseCount = event.detail.caseCount;
        this.totalOpenTaskCount = event.detail.taskCount;
        this.totalOpenCaseTask = this.totalOpenCaseCount + this.totalOpenTaskCount;

        if(this.totalOpenCaseTask <= 2 ){
            this.caseCountColorClass = 'slds-is-open-green';
        }
        else if(this.totalOpenCaseTask >= 3 && this.totalOpenCaseTask <= 6){
            this.caseCountColorClass = 'slds-is-open-yellow';
        }
        else if(this.totalOpenCaseTask > 6){
            this.caseCountColorClass = 'slds-is-open-red';
        }
    }
}