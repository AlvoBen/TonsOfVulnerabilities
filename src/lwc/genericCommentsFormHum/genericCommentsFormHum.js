import { LightningElement, track, api } from 'lwc';
import getCodes from '@salesforce/apex/CaseCommentsDataTable_LC_HUM.getCaseCommentOptions';
import getUserDetails from '@salesforce/apex/GetCurrentUserDetails.getUserDetails';
import customcss from '@salesforce/resourceUrl/LightningCRMAssets_SR_HUM';
import { loadStyle } from 'lightning/platformResourceLoader';
import { getUserGroup } from 'c/crmUtilityHum';

export default class GenericCommentsFormHum extends LightningElement {
    @api caseId;
    @api myLabels = {};
    @track errorHighlights = '';
    @track commentErrorHighlights = '';
    @track codeErrorHighlights = '';
    @track oUserGroup = getUserGroup();
    @api deleteForm = false;
    @api lineItemDetails;
    @api editForm = false;
    @api profileName;
    @track inputLength = 32000;
    @track maxLimitMsg

    connectedCallback() {
        this.maxLimitMsg = 'Limit: '+ this.inputLength + ' characters'
        this.loadCommonCss();
    }

    get getComment() {
        return this.lineItemDetails ? this.lineItemDetails.footer.fieldvalue : '';
    }

    loadCommonCss() {
        Promise.all([
            loadStyle(this, customcss + '/CRM_Assets/styles/commonStyles.css')
        ]).catch(error => {
            console.log('Error Occured', error);
        });
    }
    @api
    hasData() {
        let valArr = [];
        this.commentErrorHighlights = '';
        if (this.editForm) {
            let commentInput = this.template.querySelector(`[data-id='comments']`).value;
            let commentVal = (commentInput.trim().length !==0) ? commentInput : null;
            valArr.push(commentVal);
            if (!commentVal) {
                this.commentErrorHighlights = 'comments-error-highlights';
            } else {
                this.dispatchEvent(new CustomEvent('modify', { detail: valArr })); //Fire event to pass input values
            }
        // } else {
        //     let codeVal = this.template.querySelector(`[data-id='code-picklist']`).value;
        //     valArr.push(codeVal);
        //     let commentInput = this.template.querySelector(`[data-id='comments']`).value;
        //     let commentVal = (commentInput.trim().length !==0) ? commentInput : null;
        //     valArr.push(commentVal);
        //     if (codeVal && commentVal) {
        //         this.dispatchEvent(new CustomEvent('modify', { detail: valArr })); //Fire event to pass input values
        //     } else if (!codeVal && commentVal) {
        //         //this.codeErrorHighlights = 'comments-error-highlights';
        //     } else if (!commentVal && codeVal) {
        //         this.commentErrorHighlights = 'comments-error-highlights';
        //     }
        //     else {
        //        // this.codeErrorHighlights = this.commentErrorHighlights = 'comments-error-highlights';
        //     }
        }
    }
}