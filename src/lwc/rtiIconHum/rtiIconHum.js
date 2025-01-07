/*
LWC Name        : rtiIconHum
Function        : LWC to display print rtiIcon

Modification Log:
* Developer Name                  Date                          Description
*------------------------------------------------------------------------------------------------------------------------------
* Aishwarya Pawar                07/14/2022                    Original Version
* Nirmal Garg                   12/06/2022                     US-3975339 Changes - RTI only for Member Account
*****************************************************************************************************************************/
import { api, LightningElement, track, wire } from 'lwc';
import { CurrentPageReference } from 'lightning/navigation';
export default class RtiIconHum extends LightningElement {

    @api
    recordId;

    @track
    objectApiName;

    @api
    recordtype;

    get checkMemberAccount() {
        if(this.recordtype && this.recordtype.toLocaleLowerCase() === 'member'){
            return true;
        }else{
            return false;
        }
    }

    @wire(CurrentPageReference)
    wiredPageRef(currentPageReference) {
        if (currentPageReference && currentPageReference?.attributes) {
            JSON.stringify(currentPageReference);
            this.recordId = currentPageReference && currentPageReference?.attributes && currentPageReference?.attributes?.recordId
                ? currentPageReference.attributes.recordId : '';
            this.objectApiName = currentPageReference && currentPageReference?.attributes && currentPageReference?.attributes?.objectApiName
                ? currentPageReference.attributes.objectApiName : '';
        }
    }

    @track toolTipClass = 'slds-nubbin_bottom-right slds-slide-from-top-to-bottom slds-float_right';

    @track toolTipStyle = 'position:absolute;top:-52px;left:-45px';

    @track toolTipBody = 'RTI Communication';
    isRTIOpen = false;
    showRtiTooltip = false;

    handleRTIEnabled() {
        this.isRTIOpen = !this.isRTIOpen;
    }

    handleRtiIconMouseEnter() {
        this.showRtiTooltip = true;
    }

    handleRtiIconMouseLeave() {
        this.showRtiTooltip = false;
    }
}