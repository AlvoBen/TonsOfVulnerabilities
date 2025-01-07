/******************************************************************************************************************************
LWC Name        : coachingIconCompoundHum.js
Function        : LWC to display Icon for RecordType

Modification Log:
* Developer Name                  Date                          Description
*------------------------------------------------------------------------------------------------------------------------------
* Jasmeen Shangari               03/10/2021                    Original Version 
******************************************************************************************************************************/

import { LightningElement, api, track } from "lwc";
import { getLabels } from "c/coachUtilityHum";

export default class coachingIconCompoundHum extends LightningElement {
    @api iconvalprop;
    @api iconvalue;
    @api compoundvalues;
    @track value;
    @track iconImage;
    @track recordTypeHelpText = "";
    labels = getLabels();

    //Code needs to be reviewed once
    connectedCallback() {
        this.iconIdentifier();
    }

    @api
    iconIdentifier() {
        const me = this;
        let isLegacy = false;
        let isMember = false;
        let isUnknown = false;
        if (this.compoundvalues) {
            Object.values(this.compoundvalues).map((arg) => {
                if (arg["fieldName"] === "ETL_Record_Deleted__c" && arg["value"]) {
                    this.value = "LEGACY DELETE";
                    me.recordTypeHelpText = me.labels.HUMLegacyDeletedMessage;
                    isLegacy = true;
                } else if (arg["fieldName"] === "RecordType") {
                    this.iconvalue = arg["value"];
                    if (this.iconvalue === 'Member') {
                        isMember = true;
                    }
                    else {
                        isUnknown = true;
                    }
                }
            });
            if (this.value != "LEGACY DELETE") {
                this.value = this.iconvalue;
            }

            if (isLegacy == true) {
                this.iconImage = 'action:approval';
            }
            else if (isMember == true) {
                this.iconImage = 'action:user';
            }
            else if (isUnknown == true) {
                this.iconImage = 'action:question_post_action';
                me.recordTypeHelpText = me.labels.HUMLegacyDeletedMessage;
                this.value = "Unknown";
            }
        }
        else {
            this.value = this.iconvalue;
            let image = "";
            switch (this.value) {
                case 'Group':
                    image = 'action:new_group';
                    break;
                case 'Unknown Group':
                case 'Unknown Provider':
                case 'Unknown Member':
                case 'Unknown Agency':
                case 'UNKNOWN':
                    image = 'action:question_post_action';
                    me.recordTypeHelpText = me.labels.HUMLegacyDeletedMessage;
                    this.value = "Unknown";
                    break;
                case 'AGENCY/BROKER':
                case 'Agency':
                case 'Broker':
                    image = 'custom:custom84';
                    break;
                case 'Provider':
                    image = 'custom:custom14';
                    break;
            }
            this.iconImage = image;
        }
    }

    onRecordTypeOver() {
        this.toggleToolTip('slds-rise-from-ground', 'slds-fall-into-ground');
    }

    onRecordTypeOut() {
        this.toggleToolTip('slds-fall-into-ground', 'slds-rise-from-ground');
    }

    toggleToolTip(addClass, removeClass) {
        const me = this;
        if (me.recordTypeHelpText) {
            const tooltipEle = this.template.querySelector('[data-id="hc-icon-comp-tooltip"]');
            tooltipEle.classList.add(addClass);
            tooltipEle.classList.remove(removeClass);
        }
    }
}