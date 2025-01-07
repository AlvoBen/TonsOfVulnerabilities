/*******************************************************************************************************************************
LWC JS Name : CoachingTableComponentHum.js
Function    : This LWC component to display results returned

Modification Log:
Developer Name           Code Review              Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Jasmeen Shangari                              03/08/2021                    init version
*********************************************************************************************************************************/
import { LightningElement, track, api } from 'lwc';
import { compute, populatedDataIntoModel, getHelpText, getTableModal } from './coachingTableHelper';
import standardTableHum from './coachingTableHum';
import { getLabels, hcConstants } from 'c/coachUtilityHum';

export default class CoachingTableComponentHum extends LightningElement {
    // @api response;
    @api nameOfScreen;
    @api title = "Results";
    @api bHideTitle = false;
    @api resultClass = "selectRow";
    @api isOverflow;
    @api resulttantTableStructure;
    @api noDataMessage;
    @api showViewAll = false;
    @track accordianicon = "utility:jump_to_right";
    @track columns;
    @track tableModal;
    @track loaded = false;
    @track tempList;
    @track noDataAvailable = false;
    @track resultsHelpText = '';
    @track arraytranss;
    @api templatemodal;
    @track tablechildModal;
    @api templateaccordian = false;
    @track showhideaccordian = false;
    @track enableViewAll = false; // processed track variable for view All. Required to prevent override of api showViewAll
    labels = getLabels();


    render() {
        return standardTableHum;
    }
    expandrow(event) {
        if (event.currentTarget.getAttribute('data-actionname') == "utility:jump_to_bottom") {
            this.showhideaccordian = false;
            this.accordianicon = "utility:jump_to_right";
            let newaar = [];
            this.tableModal.forEach((arg1) => {
                newaar.push(Object.assign({}, { "expand": false, "Id": arg1[0][0].value, "celldata": arg1 }));
            });
            this.arraytranss = newaar;
        }
        else {
            this.showhideaccordian = true;
            this.accordianicon = "utility:jump_to_bottom";
            let newaar = [];
            this.tableModal.forEach((arg1) => {
                if (event.currentTarget.getAttribute('data-att') == arg1[0][0].value) {
                    newaar.push(Object.assign({}, { "expand": true, "Id": arg1[0][0].value, "celldata": arg1 }));
                    const getaccordiandata = new CustomEvent('getaccordiandata', { detail: { "Id": event.currentTarget.getAttribute('data-att') } });
                    this.dispatchEvent(getaccordiandata);
                }
                else {
                    newaar.push(Object.assign({}, { "expand": false, "Id": arg1[0][0].value, "celldata": arg1 }));
                }
            });
            this.arraytranss = newaar;
        }
    }
    connectedCallback() {
        this.computecallback(this.response);

    }
    @api
    accordiancomputecallback(response) {
        this.tablechildModal = null;
        if (response && response.length > 0) {
            this.tablechildModal = compute('purchaserplan', response);
            if (this.tablechildModal)
                this.childcolumns = JSON.parse(JSON.stringify(this.tablechildModal[0][0]));
        }
    }
    @api
    computecallback(response) {
        this.tableModal = null;
        this.arraytranss = null;
        setTimeout(() => {
            if (response) {
                const tableData = this.templatemodal ? populatedDataIntoModel(JSON.parse(JSON.stringify(this.templatemodal)), response) : compute(this.nameOfScreen, response);
                const totalRecords = tableData.length;
                if (this.showViewAll) {
                    this.enableViewAll = totalRecords > hcConstants.MIN_RECORDS;  // if records count greater thatn hcConstants.MIN_RECORDS
                    this.tableModal = this.enableViewAll ? tableData.splice(0, hcConstants.MIN_RECORDS) : tableData;
                }
                else {
                    this.tableModal = tableData;
                }
                this.resultsHelpText = getHelpText(this.nameOfScreen, this.labels);
                if (this.tableModal) {
                    if (this.templateaccordian) {
                        let arraytrans = [];
                        response.forEach((resp) => {
                            if (!(resp.Product__c == 'MED' && resp.Product_Type__c == 'PDP' && resp.Product_Type_Code__c == 'PDP')) {
                                this.tableModal.forEach((arg1) => {
                                    arg1.forEach((arg2) => {
                                        arg2.forEach((arg3) => {
                                            if (arg3.hasOwnProperty('accordian')) {
                                                if (resp.Id == arg3.value) {
                                                    arg3.accordian = false;
                                                }
                                            }
                                        })
                                    })
                                });
                            }
                        });

                        this.tableModal.forEach((arg1) => {
                            arraytrans.push(Object.assign({}, { "expand": false, "Id": arg1[0][0].value, "celldata": arg1 }));
                        });
                        this.arraytranss = arraytrans;
                    }
                    this.tempList = this.tableModal;
                    const cols = this.templatemodal ? this.templatemodal : getTableModal(this.nameOfScreen);
                    this.columns = cols[0];
                    this.noDataAvailable = this.tableModal.length ? false : true;
                    this.loaded = true;
                }

                if (this.tableModal.length === 1) {
                    if (this.nameOfScreen !== 'Policy' && this.nameOfScreen !== 'grouppolicies') {
                        this.tableModal[0][0][0].checked = true;
                        this.getInteractionData(this.tableModal[0][0][0].value);
                    }
                }
            }
        }, 1);
    }

    @track tableData;

    @api
    get response() {
        return this.tableData;
    }
    set response(value) {
        this.tableData = value;
        if (value) {
            this.computecallback(this.tableData);
        }
    }
    fetchRecordDetails(event) {
        const actionName = event.currentTarget.getAttribute('data-actionname');

        if (actionName === 'FIREINTERACTIONS') {
            this.getInteractionData(event.target.value);
        }
    }

    getInteractionData(recordId) {
        var uniqueId = recordId;
        let tmp = [];
        let tempVar = [];
        this.tableModal.forEach((primaryRec) => {
            primaryRec.forEach((record) => {
                const recordFound = record.find(item => item.value === uniqueId);
                if (recordFound) {
                    recordFound.checked = true;
                    tmp.push(record);
                }
            });
        });

        tempVar.push(tmp);

        this.tableModal = null;

        setTimeout(() => {
            this.tableModal = tempVar;
            const detail = { Id: uniqueId };
            const interaction = new CustomEvent('interactions', { detail });
            this.dispatchEvent(interaction);
        }, 1);
    }


    oncheckboxcheck(event) {
        const detail = { checked: event.target.checked, recId: event.currentTarget.dataset.id };
        const checkboxcheck = new CustomEvent('checkboxcheck', { detail });
        this.dispatchEvent(checkboxcheck);
    }

    @api
    backToResult() {
        this.tableModal = null;
        setTimeout(() => {
            this.tableModal = this.tempList.map((primaryRec) => {
                return primaryRec.map((record) => {
                    const recordFound = record.find(item => item.radio === true);
                    if (recordFound) {
                        recordFound.checked = false;
                    }
                    return record;
                });
            });
        }, 1);
    }

    @api
    recordIdToNavigate(recId) {
        this.template.querySelector('c-coaching-compound-table-hum').navigateToDetailPage(recId);
    }
}