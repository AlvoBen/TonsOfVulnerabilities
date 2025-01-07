/*******************************************************************************************************************************
LWC JS Name : customTooltipHum.js
Function    : This JS serves as controller to customTooltipHum.html. 
Modification Log: 
Developer Name                       Date                         Description
*--------------------------------------------------------------------------------------------------------------------------------
* Akshay K                         12/18/2020                    initial version
* Mohan Kumar N                     03/03/2021                    Fix for DF-2528
* Ritik Agarwal                     03/20/2021                    add product_type condition for hover over on policyMemberId
* Ritik Agarwal                     04/20/2021                    add navigation to memberplan detail page
* Ritik Agarwal                     05/18/2021                    removed the policy references to PurchaserPlan
* Mohan Kumar N                     07/08/2021                 US: 2364782- Dual eligibity screen
* Ritik Agarwal                     09/20/2021                  Refactored the code realted to Time Date zone
* Supriya Shastri                   01/13/2021                  US-2406623
* Ashish Kumar                      01/18/2022                     Case Launch from Member Plan
* Supriya Shastri				    03/16/2021	
* Ashish/Kajal                      07/18/2022                    Added logic for archived case detail navigation 
*********************************************************************************************************************************/
import { LightningElement, api, track } from 'lwc';
import getCaseCommentDetails from '@salesforce/apex/CaseHistoryComponent_LC_HUM.getCaseCommentDetails';
import { getLabels, getUserGroup } from 'c/crmUtilityHum';
import getMemberPlanDetails from '@salesforce/apex/PoliciesSearchResults_LC_HUM.getMemberPlanDetails';

export default class CustomTooltipHum extends LightningElement {
    @api oViewAllParams = {};
    @api helpText;  // [optional] Help text to be shown on hove over
    @api linkType = false;  // [optional]  true : to show it as link than info button
    @api linkText; // [optional]  linktext if linktype is true
    @api recordId; // [optional]  record Id required if help text required to be conditionally on return of apex result
    @api linkwithtooltip; // [optional]  Fetch tooltip conditionally when link type is true
    @api align = 'right'; // [optional]  Alignment of the help text. possible values top, right, bottom, left
    @track labels = getLabels();
    @track customCss = 'custom-tooltip slds-is-relative';
    @track popOverCss = 'custom-help-text slds-popover slds-popover_tooltip slds-fall-into-ground ';
    @api nameofscreen;
    bMouseOutTracker = false;
    @api iconType = false;
    @api iconPosition = false;
    connectedCallback() {
        if (this.nameofscreen == "accountdetailpolicy") this.helpText = '';
        this.updatePosition();
        if (this.linkType) {
            this.customCss = `custom-help-text-type-link ${this.customCss}`;
        }
        if (this.iconType) {
            this.customCss = (!this.iconPosition) ? "hc-icon-tooltip" : "";
        }
    }

    /**
     * update position for the configured align
     */
     updatePosition(){
        const me = this;
        switch(me.align) {
            case 'top':
                me.popOverCss += 'hc-popover-top';
                break;
            case 'right':
                me.popOverCss += 'hc-popover-right';
                break;
            case 'bottom':
                me.popOverCss += 'hc-popover-bottom';
                break;
            case 'left':
                me.popOverCss += 'hc-popover-left';
                break;
            default:
        }
    }

    onTooltipOver() {
        if(!(this.oViewAllParams!== undefined && this.oViewAllParams.hasOwnProperty('sOptions') && this.oViewAllParams.sOptions.sAppName ==='ArchivalCaseHistory')){
            if (this.linkwithtooltip) {
                this.bMouseOutTracker = false;
                if (this.nameofscreen == "accountdetailpolicy") {
                    this.helpText = '';
                    getMemberPlanDetails({ mpId: this.recordId }).then((result) => {
                        if (result && result.Plan) {
                            const bShow = ((result.Product__c === 'MED') && (result.Product_Type__c === 'MER' || result.Product_Type__c === 'MRO' || result.Product_Type__c === 'MEP' || result.Product_Type__c === 'MEF' || result.Product_Type__c === 'MGR' || result.Product_Type__c === 'MGP' || result.Product_Type__c === 'MGO' || result.Product_Type__c === 'MGF' || result.Product_Type__c === 'PDP' || result.Product_Type__c === 'MPD') && (result.Plan.Contract_Number__c || result.Plan.PBP_Code__c || result.Plan.Medicare_Segment_ID__c));
                            const cn = (result.Plan.Contract_Number__c) ? result.Plan.Contract_Number__c + ' / ' : '/ ';
                            const pbp = (result.Plan.PBP_Code__c) ? result.Plan.PBP_Code__c + ' / ' : '  / ';
                            const msId = (result.Plan.Medicare_Segment_ID__c) ? result.Plan.Medicare_Segment_ID__c : ' ';
                            this.helpText = bShow ? (cn + pbp + msId) : '';
                            if(!this.bMouseOutTracker){
                                this.toggleToolTip('slds-rise-from-ground', 'slds-fall-into-ground');
                            }
                        }
                    }).catch((error) => {
                        console.log('Error Occured', error);
                    });

                } else {
                    this.helpText = this.labels.HUM_CaseNoComments_LC;
                    getCaseCommentDetails({ caseId: this.recordId }).then((result) => {
                        if (result) {
                            let commentbody = result.CommentBody ? (result.CommentBody.length > 500 ? (result.CommentBody).substring(0, 500) + '...' : result.CommentBody): '';
                            commentbody = commentbody.replace(/(?:\r\n|\r|\n)/g, '<br>');
                            this.helpText = '<b>' + this.labels.HUM_CaseComments_LC + ' ' + result.LastModifiedDate + '</b><br>' + commentbody + ' ';
                        }
                        if(!this.bMouseOutTracker){
                            this.toggleToolTip('slds-rise-from-ground', 'slds-fall-into-ground');
                        }
                    }).catch((error) => {
                        console.log('Error Occured', error);
                    });
                }
            } else {
                this.toggleToolTip('slds-rise-from-ground', 'slds-fall-into-ground');
            }
        }
    }

    onTooltipOut() {
        this.bMouseOutTracker = true;
        this.toggleToolTip('slds-fall-into-ground', 'slds-rise-from-ground');
    }

    toggleToolTip(addClass, removeClass) {
        const me = this;
        if (me.helpText) {
            const tooltipEle = this.template.querySelector('[data-custom-help="custom-help"]');
            tooltipEle.classList.add(addClass);
            tooltipEle.classList.remove(removeClass);
        }
    }

    /**
     * Fire event on click of hyperlinked tooltip
     * to navigate to detail page
     */
    navigateToPage(){
        let objectName;
        switch (this.nameofscreen) {
            case 'accountdetailpolicy':
                objectName = 'MemberPlan'; 
                break;
            case 'Case Search':
                const { bPharmacy, bGeneral, bRcc, bProvider, bGbo } = getUserGroup();
                objectName = (bPharmacy || bGeneral || bRcc || bProvider || bGbo) ? "Case" : null;
                break;
            case 'Archived Case':
                objectName = "Case";
                break;
            case 'Interactions':
                objectName = 'Interaction__c';
                break;
        }
        if (objectName) {
            this.dispatchEvent(new CustomEvent('detailpagenavigation', {
                detail: {payLoad: this.recordId,
                    objecttonavigate: objectName}
            }))
        }
    }        
}