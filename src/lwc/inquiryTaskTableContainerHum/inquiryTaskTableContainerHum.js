/*******************************************************************************************************************************
Function    : This JS serves as controller to inquiryTaskTableContainerHum.html. 
Modification Log: 
Developer Name                    Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Mohan kumar N                 04/22/2021                  US: 1628165 
* Supriya                       08/23/2021                  US-2363825 Standardized icons
*********************************************************************************************************************************/

import { LightningElement, api, track } from 'lwc';
import getInquiryNotes from '@salesforce/apexContinuation/InquiryDetails_LC_HUM.getInquiryNotes';
import getInquiryAudit from '@salesforce/apexContinuation/InquiryDetails_LC_HUM.getInquiryAudit';
import getInquiryAttachment from '@salesforce/apexContinuation/InquiryDetails_LC_HUM.getInquiryAttachment';
import getInquiryTaskDetails from '@salesforce/apexContinuation/InquiryDetails_LC_HUM.getInquiryTaskDetails';
import { inquiryNotes, inquiryAuditTrail, inquiryAttachments, inquiryTaskList, taskAuditTrail,
         taskNotes, taskAttachments } from './tableModals';
import { getLabels, hcConstants } from 'c/crmUtilityHum';

export default class InquiryTaskTableContainerHum extends LightningElement {
    @api oParams;
    @api showViewAll = false;
    @api bInfiniteScroll;
    @api breadCrumbItems;
    @api title;
    @api nameOfScreen;
    @api bAccordianTable = false;
    @track labels = getLabels();
    @track aModal;
    @track oData = [];
    @track oDataTaskNotes;
    @track oDataTaskAudit;
    @track oDataAttachments;
    @track bDataLoaded;
    @track bInquiryPage = true;
    @track iconName = "standard:account";
    @track nuggets = [{
        nameOfScreen: hcConstants.TASK_NOTES,
        mapping: "NoteDetails",
        iconName: "standard:note",
        title: this.labels.taskNotesHum
    },{
        nameOfScreen: hcConstants.TASK_AUDIT_TRAIL,
        mapping:"AuditDetails",
        iconName: "standard:person_account",
        title: this.labels.taskAuditTrailHum
    },{
        nameOfScreen: hcConstants.TASK_ATTACHMENTS,
        mapping: "AttachmentDetails",
        iconName: "standard:file",
        title: this.labels.taskAttachmentsHum,
        bAccordianTable: true
    }]
    @track taskDetailNuggets = [];

    connectedCallback() {
        const me = this;
        const { TASK_ATTACHMENTS, TASK_AUDIT_TRAIL, TASK_NOTES, INQUIRY_NOTES, INQUIRY_AUDIT_TRAIL,
            INQUIRY_ATTACHEMNTS, INQUIRY_TASK_LIST, TASK_DETAILS} = hcConstants;

        switch(me.nameOfScreen){
            case INQUIRY_NOTES:
                me.setModal(inquiryNotes);
                me.callApi(getInquiryNotes)
                me.iconName = "standard:note"
                break;
            case INQUIRY_AUDIT_TRAIL:
                me.setModal(inquiryAuditTrail);
                me.callApi(getInquiryAudit);
                me.iconName = "standard:person_account";
                break;
            case INQUIRY_ATTACHEMNTS:
                me.setModal(inquiryAttachments);
                me.callApi(getInquiryAttachment);
                me.iconName = "standard:file";
                break;
            case INQUIRY_TASK_LIST:
                me.setModal(inquiryTaskList);
                me.callApi(getInquiryTaskDetails,'TaskList');
                me.iconName = "standard:task";
                break;
            case TASK_DETAILS:
            case TASK_ATTACHMENTS:
            case TASK_AUDIT_TRAIL:
            case TASK_NOTES:
                me.bInquiryPage = false;
                me.getTaskDetailsData();
                break;
            default:
        }
    }

    /**
     * Scrolls to the provided selector view
     * @param {*} selector 
     */
    @api
    scrollTo(selector){
        const cmpElement = this.template.querySelector(`[data-id=${selector}]`);
        cmpElement && cmpElement.scrollIntoView();
    }

    /** 
     * sets modal for the table
     */
    setModal(aModal){
        this.aModal = aModal;
    }

    /**
     *  Common method to make api calss for getInquiryNotes, inquiryAuditTrail, inquiryAttachments
     * @param {*} fApexMethod 
     * @param {*} compDetails 
     */
     callApi(fApexMethod,compDetails=''){
        const me = this;
        const { refId: sContactId, sInquiryId } = me.oParams;
        fApexMethod({
            sContactId,
            sInquiryId
        })
        .then(oData => {
            compDetails !=='TaskList' ? me.setData(oData) : me.processTaskData(oData);
        })
        .catch(error => {
            console.error("Error", error);
        });
    }

    /**
     *  method to manipulate task details data on inquiry page so, when click on taskId new subtab will open
     * @param {*} response 
     */
     processTaskData(response){
        const me = this;
        me.oData = response.map(item => {
            return {
                ...item,
                taskId: item.taskID + '#&;' + me.oParams.refId
            }
        });
        me.bDataLoaded = true;
    }

    /**
     * Returns the Model based on the table type required
     * @param {*} screen 
     */
    getModal(screen){
        let oModel;
        const { TASK_ATTACHMENTS, TASK_AUDIT_TRAIL, TASK_NOTES} = hcConstants;
        switch(screen){
            case TASK_NOTES:
                oModel = taskNotes;
                break;
            case TASK_AUDIT_TRAIL:
                oModel = taskAuditTrail;
                break;
            case TASK_ATTACHMENTS:
                oModel = taskAttachments;
                break;
            default:
        }
        return oModel;
    }

    /**
     * Renders task detail Nuggets. Splits the response and render task details tables
     * @param {*} oData 
     */
    processData(oData) {
        const me = this;
        const dynamicNuggets = me.nameOfScreen !== hcConstants.TASK_DETAILS ?
            me.nuggets.filter(item => item.nameOfScreen === me.nameOfScreen): me.nuggets;

        me.taskDetailNuggets = dynamicNuggets.map(record => ({
            ...record,
            oData: oData ? oData[record.mapping]: [],
            showViewAll: me.showViewAll,
            aModal: me.getModal(record.nameOfScreen),
            oParams: me.oParams,
            bInfiniteScroll: me.bInfiniteScroll
        }));
        me.bDataLoaded = true;
    }

    /**
     * Make a call to getInquiryTaskDetails and process data to render table
     */
    getTaskDetailsData(){
        const me = this;
        const { refId: sContactId, sInquiryId } = me.oParams;
        getInquiryTaskDetails({
            sContactId,
            sInquiryId
        })
        .then(data => {
            const { taskId } = me.oParams;
            me.processData(data.find(item => item.taskID === taskId));
        })
        .catch(error => {
            console.error("Error", error);
        });
    }

    /**
     * Sets response to oData track variable and updates bDataLoaded as true
     * @param {*} oResponse 
     */
    setData(oResponse) {
        const me = this;
        me.oData = oResponse;
        me.bDataLoaded = true;
    }
}