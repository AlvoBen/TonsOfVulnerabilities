/*******************************************************************************************************************************
Function    : This JS serves as controller to inquiryTaskDetailsHum.html. 
Modification Log: 
Developer Name                    Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Mohan kumar N                 04/22/2021                  US: 2023678 
* Supriya Shastri               10/07/2021                  US: 2732069
*********************************************************************************************************************************/
import { LightningElement, api, track } from 'lwc';
import { getLabels, hcConstants } from 'c/crmUtilityHum';
import isSandboxOrg from '@salesforce/apex/SearchUtilty_H_HUM.isSandboxOrgInfo';

export default class InquiryTaskDetailsHum extends LightningElement {
    @api bInquiryPage = false;
    @api oParams;
    @track labels = getLabels();
    @track stickyHeaderCss = "";
    @track inquiryItems = [{
        title: this.labels.inquiryNotesHum,
        dataId: hcConstants.INQUIRY_NOTES,
        css: 'tabular-views ',
        bAccordianTable: false
    }, {
        title: this.labels.inquiryAuditTrailHum,
        dataId: hcConstants.INQUIRY_AUDIT_TRAIL,
        css: 'tabular-views slds-m-top_small',
        bAccordianTable: false
    }, {
        title: this.labels.inquiryAttachmentsHum,
        dataId: hcConstants.INQUIRY_ATTACHEMNTS,
        css: 'tabular-views slds-m-top_small',
        bAccordianTable: true
    }, {
        title: this.labels.taskListHum,
        dataId: hcConstants.INQUIRY_TASK_LIST,
        css: 'tabular-views slds-m-top_small slds-m-bottom_small',
        bAccordianTable: false
    }];

    @track taskItems = [{
        title: this.labels.taskNotesHum,
        dataId: hcConstants.TASK_NOTES
    }, {
        title: this.labels.taskAuditTrailHum,
        dataId: hcConstants.TASK_AUDIT_TRAIL
    }, {
        title: this.labels.taskAttachmentsHum,
        dataId: hcConstants.TASK_ATTACHMENTS
    }];

    connectedCallback() {
        const me = this;
        isSandboxOrg()
            .then(hasSystemToolBar => {
                me.hasSystemToolBar = hasSystemToolBar;
                me.stickyHeaderCss = hasSystemToolBar ? 'sticky-header-system-bar' : 'sticky-header';
            })
            .catch(err => {
                console.error('Error', err);
            });
    }

    /**
     * Returns jump links
     */
    get jumpLinks() {
        return this.bInquiryPage ? this.inquiryItems: this.taskItems;
    }    

    /**
     * Handle clock of the Jump links. 
     * @param {*} evnt 
     */
    handleJumpLinkClick(evnt) {
        const selector = evnt.currentTarget.getAttribute("data-link-id");
        if(this.bInquiryPage){
            this.scrollTo(selector);
        }
        else{
            this.template.querySelector('c-inquiry-task-table-container-hum').scrollTo(selector);
        }
        
    }

    /**
     * Scrolls to the view for which selector is sent
     * @param {*} selector 
     */
    scrollTo(selector){
        const cmpElement = this.template.querySelector(`[data-id=${selector}]`);
        cmpElement && cmpElement.scrollIntoView();
    }
}