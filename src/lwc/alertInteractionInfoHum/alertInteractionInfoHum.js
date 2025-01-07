/*
LWC Name: AlertInteractionInfoHum
Function: This component is used to display alerts on Interaction record page

Modification Log:
* Developer Name                  Date                         Description
* ------------------------------------------------------------------------------------------------------------------------------
* Nirmal Garg                     3/28/2023                   initial version
* Abhishek Mangutkar              4/05/2023                   DF - 7486
**************************************************************************************************************************** */
import { api, LightningElement, track } from 'lwc';
import getAlertInteractionDetails from '@salesforce/apex/RecommendationInteraction_LC_HUM.getRecommendationInfo';
import checkInteractionAccountType from '@salesforce/apex/RecommendationInteraction_LC_HUM.checkAccountInteractionType';
import { getModal } from './layoutConfig';
import MULTI_MEMBER_MSG from '@salesforce/label/c.MoreThanOne_Interaction_Interaction_Member';
const ICON_ARROW_UP = 'utility:arrowup';
const ICON_ARROW_DOWN = 'utility:arrowdown';
import NO_RECORD_MSG from '@salesforce/label/c.HUMNoRecords';
export default class AlertInteractionInfoHum extends LightningElement {
    @api recordId;
    @track expandSection = false;
    @track iconname = 'utility:chevronleft';
    @track alertDetails;
    @track columns = [];
    @track count;
    @track loaded = false;
    @track datafound = false;
    @track multimember = false;
    toggleDetailSection(event) {
        let divele = this.template.querySelector(`[data-key="${event.target.dataset.rowid}"]`);
        if (divele) {
            divele.classList.toggle('slds-hide');
        }
        event.target.iconName = event?.target?.iconName === 'utility:chevronright' ? 'utility:chevrondown' : 'utility:chevronright';
    }

    labels = {
        MULTI_MEMBER_MSG,
        NO_RECORD_MSG
    }

    connectedCallback() {
        this.expandSection = true;
        this.columns = this.getColumnDetails();
        this.getData();
    }

    handleClick() {
        this.expandSection = !this.expandSection;
        this.iconname = this.iconname === 'utility:chevronleft' ? 'utility:chevrondown' : 'utility:chevronleft';
    }

    getUniqueId() {
        return Math.random().toString(16).slice(2);
    }

    getData() {
        this.alertDetails = [];
        checkInteractionAccountType({ interactionId: this.recordId })
            .then(result => {
                if (result && result != 'null') {
                    let interactionMember = JSON.parse(result);
                    if (interactionMember && interactionMember?.bSingleInteractionMember === true && interactionMember?.bValidAccountType === true) {
                        getAlertInteractionDetails({ InteractionId: this.recordId })
                            .then(result => {
                                if (result && Array.isArray(result) && result.length > 0) {
                                    this.alertDetails = result;
                                    this.alertDetails = this.alertDetails.map(obj => ({ ...obj, Id: this.getUniqueId() }))
                                    this.count = this.alertDetails && this.alertDetails.length > 0 ? this.alertDetails.length : 0;
                                    this.defaultSort();
                                } else {
                                    this.count = 0;
                                }
                                this.loaded = true;
                                this.datafound = this.count > 0 ? true : false;
                            }).catch(error => {
                                console.log(error);
                                this.count = 0;
                                this.loaded = true;
                                this.datafound = false;
                            })
                    } else if (interactionMember && interactionMember?.bSingleInteractionMember === false && interactionMember?.bValidAccountType === true) {
                        this.count = 0;
                        this.loaded = true;
                        this.multimember = true;
                    } else {
                        this.count = 0;
                        this.loaded = true;
                    }
                }
            }).catch(error => {
                console.log(error);
                this.loaded = true;
                this.datafound = false;
            })
    }

    defaultSort() {
        const cloneData = [...this.alertDetails];
        let sortDirection = 'desc';
        cloneData.sort(this.sortBy('sAlertName', sortDirection === 'asc' ? 1 : -1));
        this.alertDetails = cloneData;
    }

    sortBy(field, reverse, primer) {
        const key = primer
            ? function (x) {
                return primer(x[field]);
            }
            : function (x) {
                return x[field];
            };

        return function (a, b) {
            a = key(a);
            b = key(b);
            return reverse * ((a > b) - (b > a));
        };

    }

    handleMouseEnter(event) {
        let header = event.target.dataset.label;
        this.columns.forEach(element => {
            if (element.label === header) {
                element.mousehover = true,
                    element.mousehovericon = event.target.dataset.iconname === ICON_ARROW_DOWN ? ICON_ARROW_UP : ICON_ARROW_DOWN;
            }
        });
    }

    handleMouseLeave(event) {
        let header = event.target.dataset.label;
        this.columns.forEach(element => {
            if (element.label === header) {
                element.mousehover = false
            }
        });
    }

    onHandleSort(event) {
        if (this.alertDetails.length > 0) {
            event.preventDefault();
            let header = event.currentTarget.dataset.label;
            let sortedBy = event.currentTarget.getAttribute('data-id');
            let sortDirection = event.currentTarget.dataset.iconname === ICON_ARROW_DOWN ? 'asc' : 'desc';
            this.columns.forEach(element => {
                if (element.label === header) {
                    element.mousehover = false;
                    element.sorting = true;
                    element.iconname = element.iconname === ICON_ARROW_DOWN ? ICON_ARROW_UP : ICON_ARROW_DOWN;
                } else {
                    element.mousehover = false;
                    element.sorting = false;
                }
            });
            const cloneData = [...this.alertDetails];
            cloneData.sort(this.sortBy(sortedBy, sortDirection === 'asc' ? 1 : -1));
            this.alertDetails = cloneData;
        }
    }

    getColumnDetails(){
        let modalData = getModal();
        modalData.forEach(element => {            
            element.mousehover = false;
            element.iconname = ICON_ARROW_DOWN;
            element.mousehovericon = ''; 
            element.sorting = element.label == 'Alert Name' ? true : false;  
            element.desc = true;
            element.asc = false;                     
        });
        return modalData;
    }
}