/*******************************************************************************************************************************
LWC JS Name : genericModalHum.js
Function    : Generic modal which handles header, content and actions(footer)

Modification Log: 
Developer Name           Code Review              Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Mohan Kumar N                                  03/12/2021                   US:1942036: Password popup 
* Supriya Shastri                                03/17/2021                   US: 3173602 loading spinner
* Muthukumar 					 				 05/22/2023				      US-4522776 & 4522916 warning message
*********************************************************************************************************************************/

import { LightningElement, api, track } from 'lwc';
import { getLabels, hcConstants } from 'c/crmUtilityHum';

export default class GenericModalHum extends LightningElement {
    @api title; // Title of the modal
    @api bSpinner;
    @api bShowModal = false; // Control show and hide of the modal in the parent component
    @api buttonsConfig = [{ // buttonConfig should contain the buttons required in footer bar
        text: getLabels().HUMCancel, // Button Text
        isTypeBrand: false, // true to style as brand button , false to style as neutral
        eventName: hcConstants.CLOSE // eventName to be fired on parent component
    }, {
        text: getLabels().HUMResetButtonLabel,
        isTypeBrand: false,
        eventName: hcConstants.RESET
    }, {
        text: getLabels().HUMSaveBtn,
        isTypeBrand: false,
        eventName: hcConstants.SAVE
    }];
    @api size = 'xsmall'; // Size of the modal. possible values: xsmall, small, medium, large
    @api isContinue;

    @track labels = getLabels();
    @track closeClass = "slds-button slds-button_icon slds-modal__close slds-button_icon-inverse";
    @api overlayClass = "slds-modal__container";
	@api isShowWarning;

    connectedCallback() {
        if(this.isContinue) {
            this.closeClass = "slds-hide";
            this.overlayClass = "slds-modal__container slds-backdrop slds-backdrop_open";
        }
		if(this.isShowWarning) {
            this.closeClass = "slds-hide"; 
        }
    }

    
    /**
     * Handle Button click. Fires the corresponding events
     * @param {*} evnt 
     */
    onButtonClick(evnt) {
        
        const me = this;
        const buttonIndex = evnt.currentTarget.getAttribute('data-index');
        const activeButton = me.buttonsConfig[buttonIndex];
        if (activeButton.eventName) {
            me.fireEvent(activeButton.eventName);
        }
    }

    /**
     * Handle close icon click. Fires the close event
     */
    onClose() {
        this.fireEvent(hcConstants.CLOSE);
        this.fireEvent('closeoverlay');
    }

    
    /**
     * Fire custom events
     * @param {*} eventName 
     * @param {*} detail 
     */
    fireEvent(eventName, detail) {
        const tabNavigate = new CustomEvent(eventName, { detail });
        this.dispatchEvent(tabNavigate);
    }
}