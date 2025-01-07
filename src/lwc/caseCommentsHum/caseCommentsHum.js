/*******************************************************************************************************************************
LWC JS Name : caseCommentsHum.js
Function    : This JS serves as controller to caseCommentsHum.html. 

Modification Log: 
Developer Name           Code Review                      Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Supriya                                                 12/10/2021                 US: 2875394 
* Supriya Shastri                                         03/16/2021                 US: 2960774
* Supriya Shastri                                         03/17/2021                 US: 3173602, 2908813
* Ritik                                                   03/25/2022                 add pubsub to refresh comment timeline view
* Supriya Shastri                                         05/25/2022                 US: 3244079
* Vinay Lingegowda                                        06/28/2022                 US: 3177895 T1PRJ0170850 - Case Management - Case Comments - Toast for Successfully Added, Edited, and Deleted Comments
* Gowthami Thota                                          07/01/2022                 US: 3242176,Case Management - Case Closure Validations & Restrictions - HP and Non-HP Case Record Types
                                                                                            Added pusub to refresh comments
* M K Manoj                                               07/27/2022                 US-3522143,3495639 For passing Case Record Type to child cmp
* Jonathan Dickinson                                      02/27/2024                 User Story 5738539: T1PRJ1374973: DF 8518 - 8519 - 8520; C06 Case Management; Lightning - Case Comments - Error when adding comment before closing or transferring a case and notes reflected in incorrect section
*********************************************************************************************************************************/

import { LightningElement, track, api, wire } from 'lwc';
import { toastMsge } from 'c/crmUtilityHum';
import sendRequestLogNotes from '@salesforce/apexContinuation/CaseCommentsDataTable_LC_HUM.sendRequestLogNotes';
import startSaveCommentOperation from '@salesforce/apex/CaseCommentsDataTable_LC_HUM.startSaveCommentOperation';
import { getRecord } from 'lightning/uiRecordApi';
import { getCaseLabels } from "c/customLabelsHum";
import { getHistoryTimelineModel } from './layoutConfig';
import generateCaseComments from '@salesforce/apex/CaseCommentsDataTable_LC_HUM.generateCaseComments'; //CaseCommentsDataTable_LC_HUM,CaseHistoryComponent_LC_HUM
import getCurrentUserProfileName from '@salesforce/apex/CaseCommentsDataTable_LD_HUM.getCurrentUserProfileName';
import pubSubHum from 'c/pubSubHum';
import { CurrentPageReference } from 'lightning/navigation';
import saveCaseCommentData from "@salesforce/apex/CaseCommentsDataTable_LC_HUM.saveCaseCommentData";
import { getUserGroup } from 'c/crmUtilityHum';
import fetchCaseCTCIObject from '@salesforce/apex/CaseCommentsDataTable_LC_HUM.fetchCaseCTCIObject';
import getCodes from '@salesforce/apex/CaseCommentsDataTable_LC_HUM.getCaseCommentOptions';
import { getCustomSettingValue } from 'c/genericCustomSettingValueHum';
import { createLogNote } from 'c/genericPharmacyLogNotesIntegrationHum';
import USER_ID from '@salesforce/user/Id';
import NETWORK_ID_FIELD from '@salesforce/schema/User.Network_User_Id__c';

const USER_FIELDS = [NETWORK_ID_FIELD];

export default class CaseCommentsHum extends LightningElement {
    @track oUserGroup = getUserGroup();
    @track lstCodes = [];
    @wire(CurrentPageReference) pageRef;
    @track enablePopup;
    @api recordId;
    @track onloadCount = 5; //Ankima
    @track labels = getCaseLabels();
    @track buttonsConfig = [{
        text: 'Cancel',
        isTypeBrand: false,
        eventName: 'close'
    }, {
        text: 'Save',
        isTypeBrand: true,
        eventName: 'save'
    }];
    @track historyTimelineData = []; //assign it to response api to be sent to inner cmp
    @track totalcount; //assign it to response api to be sent to inner cmp
    @track filteredcount=5; //assign it to response api to be sent to inner cmp Ankima
    @track counter = 1;
    @track totalresults = []; //check use, might need to be passed
    @track showbutton = false; //check use, might need to be passed
    @api encodedData;
    @api pageRefData; //this variable is used for getting URL attributes that coming from other pages
    isEdited = false;
    @track profile;
    @track loaded;
    showSpinner;
    @track bLogCodeVisible = false;
    @track sCaseRecordTypeName;
    @track enterpriseId;

    @wire
    (getRecord, { recordId: USER_ID, fields: USER_FIELDS })
        wireUserRecord({ error, data }) {
        if (data) {
            try {
                this.netId = data.fields.Network_User_Id__c.value;
            } catch (e) {
                console.log('An error occured when handling the retrieved user record data');
            }
        } else if (error) {
            console.log('An error occured when retrieving the user record data: ' + JSON.stringify(error));
        }
    }


    @wire(getRecord, {
        recordId: '$recordId',
        fields: ['Case.Account.Enterprise_ID__c']
    })
    wiredAccount({
        error,
        data
    }) {
        if (data) {
            this.enterpriseId = data.fields.Account.value.fields.Enterprise_ID__c.value;
        }
        else if (error) {
            console.log('Error occured', error);
        }
    }


    disconnectedCallback() {
        pubSubHum.unregisterListener('case-transfer-container-hum', {}, this);
        pubSubHum.unregisterListener('close-case-hum', {}, this);
    }

    connectedCallback() {
        getCurrentUserProfileName()
            .then((result) => {
                this.profile = result;
            })
        this.loadOptions();
        this.getCaseComments();
        this.subscriptionEngine();    
    }

    async loadOptions() {
        const me = this;
        const oParams = {
            sCaseID: this.recordId
        }
        try {
            const lstOptions = await getCodes(oParams);
            me.lstCodes = lstOptions.map(item => ({
                label: item, value: item
            }))
            this.getServiceModel();
        } catch (err) {
            console.error("Error", err);
        }
    }

    async getServiceModel() {
        const me = this;
        const oParams = {
            sCaseID: this.recordId
        }
        try {
            const result = await fetchCaseCTCIObject(oParams);
            if (result) {
                // Below const is used to store values for Service Model Type field of CTCI object to hide log code for HP member case
                this.sCaseRecordTypeName=result.oCase.RecordType.Name;
                const serviceModelTypes = ['Insurance/Plan', 'Humana Pharmacy'];
                // below const used to store service model type from case object
                const serviceModelType = result.lCTCI ?  result.lCTCI[0].Service_Model_Type__c:null;
                // Below const is used to store values for Classification_Type__c field of case object to hide log code for HP member case
                const classficationTypes = ['Calls (RSO)', 'HP Clinical Services', 'HP Finance Ops', 'HP RxE Calls', 'HP Specialty Calls', 'Humana Pharmacy Calls', 'Humana Pharmacy Web Chat'];
                const classifcationType = result.oCase && result.oCase.hasOwnProperty('Classification_Type__c') ? result.oCase.Classification_Type__c : null;
                if (result.oCase.RecordType.Name === 'HP Member Case' && this.lstCodes.length > 0 && (this.oUserGroup.bPharmacy || this.profile === 'Humana Pharmacy Specialist' )) {
                    this.bLogCodeVisible = serviceModelTypes.includes(serviceModelType) && classficationTypes.includes(classifcationType);
                }
            }
        }
        catch (error) {
            console.log('error ', error);
        }
    }


    /**
     * Subscribe to event fired by change owner popup
     * on saving updated fields from caseTransferContainerHum
     */
    subscriptionEngine() {
        try {
            pubSubHum.registerListener('case-transfer-container-hum', this.loadData, this);
            pubSubHum.registerListener('close-case-hum', this.loadData, this);
            pubSubHum.registerListener('case-comment-refresh', this.loadData, this);
        }
        catch{
            console.log("Error", error);
        }
    }

    /**
     * Re-renders case comment section once
     * user adds comment from change owner popup
     */
    loadData() {
        this.getCaseComments();
    }

        /**
     * Open modal to create new comment
     * for pharmacy users
     */
    openModal() {
        this.enablePopup = true;
        
    }

    saveComment() {
        this.isEdited = true;
        this.getCaseComments();
    }

    /**
     * Process user inputs on new comments
     * modal on click of save
     * @param {*} event 
     */
    async modifiedHandler(event) {
        this.showSpinner = true;
        if (Object.values(event.detail)[1]) {

            const inputCode = Object.values(event.detail)[0];
            const inputComment = Object.values(event.detail)[1];
            let oParams = {};
            oParams = {
                sCaseId: this.recordId,
                sCaseCommentBody: inputComment,
                sCode: inputCode,
                bRedirect: false,
                bErrorCaseComment: 'false'
            };
            const switchVal = getCustomSettingValue('Switch', 'HPIE Switch');
            switchVal.then(result => {
                if (result && result?.IsON__c && result?.IsON__c === true) {
                    this.callHpieService(oParams, inputCode, inputComment);
                } else {
                    this.callRSService(oParams, inputCode, inputComment);
                }
            })
        } else {
            const lstOfCaseComment = [{CommentBody: Object.values(event.detail)[0]}];
            const saveForm = await saveCaseCommentData({
                sCaseId: this.recordId,
                caseCommentData: JSON.stringify(lstOfCaseComment)
            });
            if (saveForm) {
                this.callCommentSave();
                
            }
        }
    }

    callHpieService(oParams, inputCode, inputComment) {
        createLogNote(this.netId ?? '', this.enterpriseId ?? '', this.organization ?? 'HUMANA', inputCode, inputComment)
        .then(result => {
            if (result) {
                oParams.bErrorCaseComment = '';
            }
            this.callSaveForm(oParams);
        }).catch(error => {
            if (error?.message?.toLowerCase()?.includes('patient not found')) {
                oParams.bErrorCaseComment = "true";
            } else {
                oParams.bErrorCaseComment = "false";
            }
            console.log(error);
            this.callSaveForm(oParams);
        })
    }

    callRSService(oParams, inputCode, inputComment) {
            sendRequestLogNotes({
                sComment: inputComment,
                sCode: inputCode,
                sCaseId: this.recordId
            })
                .then((result) => {
                    if (result) {
                        if (result[0] === 'true' ) {
                            oParams.bErrorCaseComment = '';
                        }  else if(result[0] === 'false' ) {
                            oParams.bErrorCaseComment = 'false';
                        }  else if (result[1] === 'true') {
                            oParams.bErrorCaseComment = 'true';
                        }  else if (result[1] === 'false') {
                            oParams.bErrorCaseComment = 'false';
                        }
                        this.callSaveForm(oParams);
                    }
                }).catch(error => {
                    console.log("Error", error)
                })
    }

    /**
     * Makes apex call to save
     * user inputs for log code, comments
     * @param {*} oParams 
     */
    async callSaveForm(oParams) {
        const saveForm = await startSaveCommentOperation(oParams);
        if (saveForm) {
            this.callCommentSave();
        }
    }

    /**
     * Closes modal on UI,
     * re-renders timeline and shows
     * success toast
     */
    callCommentSave() {
        this.showSpinner = false;
        this.closeModal();
        this.getCaseComments();
        
        toastMsge("", this.labels.HUMCommentSaveToast, "success", "dismissable");
    }

    /**
     * Checks for user inputs on
     * comments modal on click of save
     */
    async saveForm() {
        this.template.querySelector('c-comments-form-hum').hasData();
    }

    /**
     * Closes modal
     */
    closeModal() {
        this.enablePopup = false;
    }

    deleteComment() {
        this.getCaseComments();
        toastMsge("", this.labels.HUMCommentDeleteToast, "success", "dismissable");
    }

    @api
    getCaseComments() {
        generateCaseComments({ objID: this.recordId }).then((result) => {
            let respArray = [];
            let caseComments = (JSON.parse(result)).lCaseCommentDTO;
            this.profile = (JSON.parse(result)).profileName;
            this.loaded = true;
            if (caseComments != null) {
                caseComments.forEach((oms,index) => {
                    let obj = {};
                    obj.Index = oms.caseCommentId;
                    getHistoryTimelineModel("omsmodel").forEach(x => {
                        if (x.fieldname === 'icon') {
                            obj[x.fieldname] = this.getIcon(x.mappingfield);
                        } else if (x.compoundvalue) {
                            let objComp = {};
                            let compvalues = x.compoundvalues;
                            compvalues.forEach(t => {
                                if (t.hasOwnProperty("header")) {
                                    objComp["header"] = this.getHeaderValues(t["header"], oms);
                                }
                                if (t.hasOwnProperty("body")) {
                                    objComp["body"] = this.getBodyValues(t["body"], oms)
                                }
                                if (t.hasOwnProperty("footer")) {
                                    objComp["footer"] = this.getFootervalues(t["footer"], oms)
                                }
                            });
                            obj[x.fieldname] = objComp;
                            objComp = null;
                        }
                        else {
                            obj[x.fieldname] = oms.hasOwnProperty(x.mappingfield) ? oms[x.mappingfield] : '';
                        }
                    });
                    respArray.push(obj);
                });
            }
            this.historyTimelineData = respArray;
            this.totalresults = this.historyTimelineData;
            this.totalcount = this.historyTimelineData.length;			
        }).catch((error) => {
            this.loaded = true;
            console.log('Error Occured', error);
        });
    }

    renderedCallback() {
        if(this.historyTimelineData && this.template.querySelector('c-generic-history-timeline-hum') != null) {
            setTimeout(() => this.template.querySelector('c-generic-history-timeline-hum').refreshCount(this.isEdited));
        }
    }

    getIcon(iconname) {
        return getHistoryTimelineModel("icons").find(x => x.iconname === iconname);
    }

    getHeaderValues(header, omsdata) {
        let objheader = {};
        let headervalue = '';
        header.mappingfield.split(',').forEach(t => {
            headervalue += (omsdata.hasOwnProperty(t) ? omsdata[t] != null ? omsdata[t] : '' : '') + '/';
        })

        headervalue = headervalue.endsWith('/') ? headervalue.substring(0, headervalue.length - 1) : headervalue;
        objheader[header.fieldname] = headervalue;
        objheader.fieldname = objheader.fieldname;
        objheader.fieldvalue = headervalue;
        return objheader;
    }

    getBodyValues(bodymodel, omsdata) {
        let objbody = [];
        bodymodel.forEach(b => {
            objbody.push({
                fieldname: b.fieldname,
                fieldvalue: omsdata.hasOwnProperty(b.mappingfield) ? omsdata[b.mappingfield] : '',
                islink: b.islink ? true : false,
                object: b.object,
                hidden: b.hidden ? true : false
            });
        });
        return objbody;
    }

    getFootervalues(footer, omsdata) {
        let objfooter = {};
        let footervalue = '';
        footer.mappingfield.split(',').forEach(t => {
            footervalue += (omsdata.hasOwnProperty(t) ? omsdata[t] != null ? omsdata[t] : '' : '') + '/';
        })
        footervalue = footervalue.endsWith('/') ? footervalue.substring(0, footervalue.length - 1) : footervalue;
        objfooter.fieldname = footer.fieldname;
        objfooter.fieldvalue = footervalue;
        return objfooter;
    }

    sortResult(inputdata) {
        inputdata.sort(this.sortfunction);
        return inputdata;
    }
    sortfunction(a, b) {
        let dateA = new Date(a.createddatetime.split('|')[1].trim()).getTime();
        let dateB = new Date(b.createddatetime.split('|')[1].trim()).getTime();
        return dateA > dateB ? -1 : 1;
    }

    handleScroll(event) {
        if (event.target.scrollHeight - Math.round(event.target.scrollTop) === event.target.clientHeight) {
            if (this.filteredcount != undefined && this.totalcount != undefined) {
                if (this.filteredcount < this.totalcount) {
                    if ((this.filteredcount + this.loadcount) < this.totalcount) {
                        this.historyTimelinedata = this.totalresults.slice(0, (this.filteredcount + this.loadcount));
                        this.filteredcount = this.filteredcount + this.loadcount;
                    }
                    else {
                        this.historyTimelinedata = this.totalresults.slice(0, this.totalcount);
                        this.filteredcount = this.totalcount;
                        this.showbutton = true;
                    }
                }
                else {
                    this.filteredcount = this.totalcount;
                }
            }
        }
    }
}