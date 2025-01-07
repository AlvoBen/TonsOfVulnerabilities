/*******************************************************************************************************************************
LWC JS Name : interactionButtonsHum.js
Function    : This JS serves as controller to interactionButtonsHum.html. 

Modification Log: 
Developer Name           Code Review                      Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Supriya                                                 06/04/2021                 US: 2176313 
* Manohar Billa                                           02/16/2023                 US:3272618 - Verify Demographics changes for QS
*********************************************************************************************************************************/
import { LightningElement, track, api } from 'lwc';
import getInteractionAndCase from '@salesforce/apex/MemberIcons_LC_HUM.getInteractionAndCase';
import { getLabels } from 'c/crmUtilityHum';

export default class InteractionButtonsHum extends LightningElement {
    @api sRecordId;
    @api sPageName;
    @api sPlanId;
    @api memberPlan = false;
    @track interactionPills = [];
    @track labels = getLabels();

    baseCss = 'slds-button interaction-btn ';
    colorCss = {
        green: 'slds-button_success',
        orange: 'highlight-amber-button slds-button_neutral ',
        red: 'slds-button_destructive',
        white: 'highlight-white-button slds-button_neutral'
    };

    connectedCallback() {
        this.loadInteractionPills();
    }


    /**
     * Format interaction pill details from response
     * @param {*} sText 
     * @param {*} sColor 
     * @param {*} iCount 
     * @param {*} scrollTo 
     * @param {*} sType 
     */
    getInterPillObj(sText, sColor, iCount, scrollTo, sType) {
        return {
            text: sText,
            cssClass: this.baseCss + this.colorCss[sColor],
            sType,
            scrollTo,
            iCount
        };
    }

    /**
     * Process interactions response and push
     * to interactionPills array
     */
    loadInteractionPills() {
        const me = this;
        const ipParams = {
            InteractionId: '',
            AccountId: me.sRecordId,
            sPageName: me.sPageName,
            MemberPlanID: me.sPlanId 
        };
        getInteractionAndCase(ipParams)
            .then(res => {
                console.log("interaction  pill response--", res);
                if (res) {
                    const { sOpenCasesColor, iOpenCases, bIsOpenCasesVisible, bIsPast14DaysIntsVisible,
                        bIsTodaysIntsVisible, iInteractionsCreatedInPast14Days,
                        iInteractionsCreatedToday, sPast14DaysInteractionsColor, sTodaysInteractionsColor } = res;
                    const pills = [];
    
                    if (bIsTodaysIntsVisible) {
                        pills.push(me.getInterPillObj(me.labels.interactionTodayHum, sTodaysInteractionsColor,
                            iInteractionsCreatedToday, 'interactions', 'interactionstoday'));
                        // publish a custom event to parent comp with interactions created today  
                        var parms = { type : 'interactionstoday' , count : iInteractionsCreatedToday  };
                        this.dispatchEvent(new CustomEvent('interbtnevt', {
                            detail: parms
                        }));
                    }
                    if (bIsPast14DaysIntsVisible) {
                        pills.push(me.getInterPillObj(me.labels.interactionLastHum, sPast14DaysInteractionsColor,
                            iInteractionsCreatedInPast14Days, 'interactions', 'interactions14day'));
                    }
                    if (bIsOpenCasesVisible) {
                        if (me.memberPlan) {
                            pills.push(me.getInterPillObj(me.labels.planOpenCasesHum, "white", "0", 'casehistory', 'casehistory'));
                        } else {
                            pills.push(me.getInterPillObj(me.labels.openCasesHum, sOpenCasesColor, iOpenCases, 'casehistory', 'casehistory'));
                        }   
                    }
                    me.interactionPills = pills;
                }
               

            })
            .catch(err => {
                console.log('Error', err);
            });
    }

    /**
     * Fire event to parent on click of pills
     * to navigate to relevant table
     * @param {*} evnt 
     */
    onInterPillClick(evnt) {
        const dataId = evnt.currentTarget.getAttribute("data-id");
        const selectedPill = this.interactionPills.find(item => item.sType === dataId);
        if (selectedPill.iCount) {
            this.dispatchEvent( new CustomEvent('interactionnavigate', {
                detail : selectedPill
            }));
        }
    }
}