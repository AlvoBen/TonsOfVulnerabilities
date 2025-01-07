/*******************************************************************************************************************************
LWC JS Name : coachTooltipHum.js
Function    : This JS serves as controller to customTooltipHum.html. 
Modification Log: 
Developer Name                       Date                         Description
*--------------------------------------------------------------------------------------------------------------------------------
* Jasmeen Shangari                   03/09/202                    initial version
*********************************************************************************************************************************/
import { LightningElement, wire, api, track } from 'lwc';
import { getLocaleDate, getLabels } from 'c/coachUtilityHum';
export default class CoachTooltipHum extends LightningElement {
    @api helpText;
    @api linkType;
    @api linkText;
    @api recordId;
    @api linkwithtooltip;
    @track labels = getLabels();
    @track customCss = 'custom-tooltip slds-is-relative';
    @api nameofscreen;
    bMouseOutTracker = false;
    connectedCallback() {
        if (this.nameofscreen == "accountdetailpolicy") this.helpText = '';
        if (this.linkType) {
            this.customCss = `custom-help-text-type-link ${this.customCss}`;
        }
    }

    onTooltipOver() {
        this.toggleToolTip('slds-rise-from-ground', 'slds-fall-into-ground');
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
}