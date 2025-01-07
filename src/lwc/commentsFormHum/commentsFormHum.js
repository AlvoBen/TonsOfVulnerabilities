/*******************************************************************************************************************************
LWC JS Name : commentsFormHum.js
Function    : This JS serves as controller to commentsFormHum.html

Modification Log: 
Developer Name           Code Review                      Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Supriya                                                 12/10/2021                 US: 1464457 
* Supriya Shastri                                         03/17/2021                 US: 2908813
* Ritik Agarawal                                          04/04/2022  
* Supriya Shastri                                         04/08/2022                 US: 3244079
* Vinay Lingegowda                                    	  05/25/2022                 Added logic for HP casecomments
* Shailesh Bagade                                    	  07/01/2022                 Added logic for Task comment
* M K Manoj                                               07/27/2022                 US-3522143,3495639 ,Case Comments- Character Limitation Warning Message- HP and Non-HP Case Record Types
*********************************************************************************************************************************/
import { LightningElement, track, api } from 'lwc';
import getUserDetails from '@salesforce/apex/GetCurrentUserDetails.getUserDetails';
import { getCaseLabels } from "c/customLabelsHum";
import customcss from '@salesforce/resourceUrl/LightningCRMAssets_SR_HUM';
import { loadStyle } from 'lightning/platformResourceLoader';
import { getUserGroup } from 'c/crmUtilityHum';

export default class CommentsFormHum extends LightningElement {
    @api lstCodes = [];
    @api caseId;
    @track errorHighlights = '';
    @track commentErrorHighlights = '';
    @track codeErrorHighlights = '';
    @api bLogCodeVisible;
    @track oUserGroup = getUserGroup();
    @api deleteForm = false;
    @api lineItemDetails;
    @api editForm = false;
    labels = getCaseLabels();
    @api profileName;
    @api inputLength = 2900;
    @track maxLimitMsg;
    @api isItTask = false;
    showSpinner;
    @track bDisplayLimitMsg = true;
    showMaxLimitMsg;
    showInputLength;
    @track sCaseCommentCmp;
    @track bIsChecked = true;
    lstHpCases = ['Closed HP Agent/Broker Case', 'Closed HP Group Case', 'Closed HP Member Case', 'Closed HP Provider Case', 'Closed HP Unknown Case', 'HP Agent/Broker Case', 'HP Group Case', 'HP Member Case', 'HP Provider Case', 'HP Unknown Case'];
    @api sCaseRecordTypeName;

    async connectedCallback() {
        this.showSpinner = true;

        this.loadCommonCss();

        if (this.isItTask) {
            //for task
            this.inputLength = 32000;
            this.maxLimitMsg = 'Limit: ' + this.inputLength + ' characters';

        }
        else {
            //for case
            await this.getProfileName();
            this.getCommentLimit();
        }
        this.showInputLength = this.inputLength;
        this.showMaxLimitMsg = this.maxLimitMsg;
        this.showSpinner = false;
    }

    get getComment() {
        this.sComments = this.lineItemDetails ? this.lineItemDetails.footer.fieldvalue : '';
        if (this.lineItemDetails && !this.isItTask && this.bIsChecked) {
            this.sCaseCommentCmp = this.template.querySelector(`[data-id='comments']`);
            this.maxLimitMsg = (this.inputLength - this.sComments.length) + ' characters remaining';
            this.showInputLength = this.inputLength;
            this.showMaxLimitMsg = this.maxLimitMsg;
            this.sCaseCommentCmp ? this.maxcharLimitReachedvalidation(this.sCaseCommentCmp) : '';
        }
        return this.sComments;
    }


    async getProfileName() {
        try {
            const res = await getUserDetails();
            if (res.Profile.Name === this.labels.HumUtilityPharmacy || res?.Profile?.Name === this.labels.HPS_RUL) {
                this.inputLength = 1900;
                this.maxLimitMsg = this.inputLength + ' characters remaining';
            }

        } catch (error) {

        }
    }

    getCommentLimit() {

        if (this.bLogCodeVisible || this.lstHpCases.includes(this.sCaseRecordTypeName)) {
            this.inputLength = 1900;
            this.maxLimitMsg = this.inputLength + ' characters remaining';
        }
        else {
            this.inputLength = 2900;
            this.maxLimitMsg = this.inputLength + ' characters remaining';
        }
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
        if (this.bLogCodeVisible) {
            let codeVal = this.template.querySelector(`[data-id='code-picklist']`).value;
            valArr.push(codeVal);
            let commentInput = this.template.querySelector(`[data-id='comments']`).value;
            let commentVal = (commentInput.trim().length !== 0) ? commentInput : null;
            valArr.push(commentVal);
            if (codeVal && commentVal) {
                this.dispatchEvent(new CustomEvent('modify', { detail: valArr })); //Fire event to pass input values
            }
            else {
                this.codeErrorHighlights = this.commentErrorHighlights = 'comments-error-highlights';
            }
        }
        else {
            let commentInput = this.template.querySelector(`[data-id='comments']`).value;
            let commentVal = (commentInput.trim().length !== 0) ? commentInput : null;
            valArr.push(commentVal);
            valArr.push(false);
            if (commentVal) {
                this.dispatchEvent(new CustomEvent('modify', { detail: valArr })); //Fire event to pass input values
            }
            else {
                this.codeErrorHighlights = this.commentErrorHighlights = 'comments-error-highlights';
            }
        }
    }

    /**
   * Method Name: checkCharLimit
   * Function: used to check character limit onchange on comment box
   */
    checkCharLimit() {
        if (!this.isItTask) {
            if (!this.sCaseCommentCmp) {
                this.sCaseCommentCmp = this.template.querySelector(`[data-id='comments']`);
            }
            this.bIsChecked = false;
            this.maxLimitMsg = (this.inputLength - this.sCaseCommentCmp.value.length) + ' characters remaining';
            this.showInputLength = this.inputLength;
            this.showMaxLimitMsg = this.maxLimitMsg;
            this.maxcharLimitReachedvalidation(this.sCaseCommentCmp);
        }

    }

    maxcharLimitReachedvalidation(sCaseComment) {
        if (this.inputLength <= sCaseComment.value.length) {
            sCaseComment.setCustomValidity('Warning: Exceeded character limit');
            this.bDisplayLimitMsg = false;
        } else {
            sCaseComment.setCustomValidity("");
            this.bDisplayLimitMsg = true;
        }
        sCaseComment.reportValidity();
    }
}