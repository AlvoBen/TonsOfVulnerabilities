/*
Function        : LWC to display demographics details .

Modification Log:
* Developer Name                  Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Nirmal Garg                    07/18/2022                 user story 4861950: T1PRJ0870026 - HPIE/CRM - C12 Mail Order Management - Pharmacy - "Details" Tab - Lightning - Patient APIUS-3975339 Change - RTI Icon Display Logic
* Atul Patil                    07/28/2023                  user story 4861950: T1PRJ0870026 - HPIE/CRM - C12 Mail Order Management - Pharmacy - "Details" Tab - Lightning - Patient API
* Nirmal Garg                    09/13/2023                 US#5071365
*****************************************************************************************************************************/
import { LightningElement, api, track, wire } from 'lwc';
export default class PharmacyHpiePrescriptionCardHum extends LightningElement {
    @api item;
    @api selectedindex;
    @api userId;
    @api addtocart;
    @api enterpriseId;
    @api organization;
    @api isCardClick;
    @api icon;
    @api prckey;
    @api orderEligible;
    @track selectedPrsList;
    @track isPrescription = false;
    @track popOver = false;
    @track openAutoRefillModal = false;
    @track autoFillFlag = false;
    @track loaded = true;

    get isAddToCart() {
        return this.addtocart;
    }

    get checkordereligible() {
        return this.orderEligible;
    }

    get autoRefillEligibilty() {
        return this.item?.autoRefillEligible?.toLowerCase() == "yes" && !this.item?.status?.toLowerCase()?.includes('inactive') ? true : false;
    }

    connectedCallback() {
        this.autoFillFlag = this.item?.autoRefillEnrolled && this.item?.autoRefillEnrolled === true
            || this.item?.autoRefillEnrolled?.toString()?.toLowerCase() === 'yes' ? true : false;
    }

    closeModal(event) {
        if (event && event?.detail?.displayLoader === true) {
            this.openAutoRefillModal = false;
            this.loaded = false;
        } else {
            this.openAutoRefillModal = false;
        }
    }

    toggleAutoRefill() {
        this.openAutoRefillModal = true;
    }

    openPopOver() {
        this.isCardClick = false;
        this.popOver = true;
    }

    handleCloseClick() {
        this.isPrescription = false;
    }

    handleAddCartClick() {
        this.addtocart = true;
        const addCartEvent = new CustomEvent('addcart', {
            detail: {
                prescriptioncolor: this.icon.icontype,
                prescriptionnumber: this.item.key,
                addedprescription: this.item
            }
        });
        this.dispatchEvent(addCartEvent);
        this.popOver = false;
    }

    handleUpdateAutoRefill(event) {
        this.openAutoRefillModal = false;
        if (event && event?.detail) {
            this.openAutoRefillModal = false;
            this.popOver = false;
            this.autoFillFlag = event?.detail?.autoFillFlag;
            this.fireEventToParent({
                selectedCard: this.selectedindex,
                selectedKey: this.prckey,
                autoRefill: event?.detail?.autoFillFlag,
                prescriptionKey: this.item.key,
                isCardClick: true
            }, 'updateautoreill');
        }
        this.loaded = true;
    }

    fireEventToParent(data, eventname) {
        this.dispatchEvent(new CustomEvent(eventname, {
            detail: data
        }));
    }


    handleClosePrescription(evt) {
        this.isPrescription = evt.detail;
    }

    closePopOver() {
        this.popOver = false;
    }

    cardClick(event) {
        this.fireEventToParent({
            selectedCard: this.selectedindex,
            selectedKey: this.prckey,
            prescriptionKey: this.item.key,
            isCardClick: true
        }, 'cardclickselect');
    }

    get getdrugcontent() {
        if (this.item?.writtenDrug) {
            let index = this.item?.WrittenDrug?.indexOf(" ");
            return index > 0 ? this.item?.writtenDrug?.substring(0, index)?.trim() : '';
        }
        return '';
    }

    handleblur() {
        let maindiv = this.template.querySelector('[data-id="maindiv"]');
        if (maindiv) {
            maindiv.classList.remove('selectedcard');
        }
    }

    @api
    displayBorder(RxNumber) {
        let maindiv = this.template.querySelector('[data-id="maindiv"]');
        if (maindiv) {
            if (RxNumber === this.item?.key) {
                maindiv.classList.add('selectedcard');
            } else {
                maindiv.classList.remove('selectedcard');
            }
        }
    }

    get checkrefilllength() {
        return this.item?.refillsRemaining?.toString()?.length >= 2 ? true : false;
    }
}