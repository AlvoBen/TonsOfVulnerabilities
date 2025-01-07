/*****************************************************************************************************************************
Component Name   : pharmacyHpieCreditCardDetailsHum.js
Version          : 1.0
Function         : lwc to display credit card details

Modification Log:
* Developer Name           Code Review                 Date                         Description
*---------------------------------------------------------------------------------------------------------------------
* Jonathan Dickinson							       08/17/2023					User Story 4908778: T1PRJ0870026 - SF - Tech - C12 Mail Order Management - Pharmacy - Finance tab
* Jonathan Dickinson			                       09/04/2023					User Story 4999697: T1PRJ0870026 MF27456 HPIE/CRM - SF Tech- Mail Order Management - Pharmacy - Finance tab - Lightning - Edit Credit Card, One time payment
*******************************************************************************************************************************************************************/

import { LightningElement, track, api } from 'lwc';
import customcss from '@salesforce/resourceUrl/PharmacyLightning_SR_HUM';
import { loadStyle } from 'lightning/platformResourceLoader';
import getExpiredAndExpiringSoonIconURLs from '@salesforce/apex/MemberIcons_LC_HUM.getExpiredAndExpiringSoonIconURLs';
import { getUniqueId } from 'c/crmUtilityHum';
import { getLayout } from './layoutconfig';
import nocardmsg from '@salesforce/label/c.FINANCIAL_CREDITCARD_NODATA';
const ICON_ARROW_UP = 'utility:arrowup';
const ICON_ARROW_DOWN = 'utility:arrowdown';

export default class PharmacyHpieCreditCardDetailsHum extends LightningElement {
    @track creditCardInfoLayout = getLayout();
    @track _creditcarddata;
    @track lstCreditCards = [];
    @track dataExist = false;
    @track loaded = true;
    @track numCards = 0;
    @track expiringSoonCreditCardIconURL;
    @track expiredCreditCardIconURL;

    labels = {
      nocardmsg
    }

    @api
    get creditcarddata() {
        return this._creditcarddata;
    }
    
    set creditcarddata(value) {
        if (value) {
            this._creditcarddata = value;
            this.loaded = false;
            this.loadCommonCSS();
            if(this.expiredCreditCardIconURL && this.expiringSoonCreditCardIconURL) {
                this.processCreditCards();
                this.updateCardIcon();
            } else {
                (async () => {await this.getIconURLs();})().then(() => {
                    this.processCreditCards();
                    this.updateCardIcon();
                });
            }
        }
    }

    async getIconURLs() {
        await getExpiredAndExpiringSoonIconURLs({ sPageName: 'Humana Pharmacy', iconType: 'expired' })
            .then(result => {
                this.expiredCreditCardIconURL = result;
            })
            .catch(err => {
                console.log(err);
            })

        await getExpiredAndExpiringSoonIconURLs({ sPageName: 'Humana Pharmacy', iconType: 'expiring' })
            .then(result => {
                this.expiringSoonCreditCardIconURL = result;
            })
            .catch(err => {
                console.log(err);
            })

        // used so this method can have await applied to it in the connectedCallback
        return new Promise((resolve) => resolve(''));
    }

    updateCardIcon() {
        if (this.lstCreditCards && this.lstCreditCards.length > 0) {
            this.lstCreditCards.forEach(k => {
                k.IconLabel = k?.IconURL === this.expiredCreditCardIconURL ? 'Expired' : k?.IconURL === this.expiringSoonCreditCardIconURL ? 'Expiring' : ''
            })
        }
    }

    processCreditCards() {
        this.lstCreditCards = [];
        if (this.creditcarddata && this.creditcarddata?.paymentCards && Array.isArray(this.creditcarddata.paymentCards) && this.creditcarddata.paymentCards.length > 0) {
            this.dataExist = true;
            this.numCards = this.creditcarddata.paymentCards.length;
            this.creditcarddata.paymentCards.forEach(k => {
                if (k) {
                    this.lstCreditCards.push({
                        Id: getUniqueId(),
                        cardType: k?.z0type?.description ?? '',
                        last4Digits: k?.tokenKey ? k.tokenKey.slice(-4) : '',
                        cardHolderName: `${k?.firstName}${k?.middleName ? ' ' + k?.middleName + ' ' : ' '}${k?.lastName}`,
                        expirationDate: `${k?.expirationMonth.toString().padStart(2, '0')}/${k?.expirationYear.toString().slice(-2)}`,
                        autoCharge: k?.autoCharge ? 'Y' : 'N',
                        status: k?.active ? 'A' : 'I',
                        spendingAccount: k?.spendingAccount ? 'Y' : 'N',
                        creditCardKey: k?.key ?? '',
                        IsEditIcon: true,
                        IconURL: this.checkCreditCards(k),
                    })
                }
            })
            const cloneCreditCardData = this.lstCreditCards;
            cloneCreditCardData.sort(this.sortBy('expirationDate', -1, this.getPrimer('expirationDate')));
            this.lstCreditCards = cloneCreditCardData;
        }
        this.loaded = true;
    }

    checkCreditCards(card) {
        let currentdate = new Date();
        if (card.active) {
            let months;
            let expiMonth = card.expirationMonth;
            let expiYear = card.expirationYear;
            let expidate = new Date(expiYear, expiMonth - 1, 1);
            months = (expidate.getFullYear() - currentdate.getFullYear()) * 12;
            months -= currentdate.getMonth() + 1;
            months += expidate.getMonth();
            if (months < 0) {
                return this.expiredCreditCardIconURL;
            } else if (months == 0) {
                return this.expiringSoonCreditCardIconURL;
            } else {
                return '';
            }
        }
    }

    //load css
    loadCommonCSS() {
        Promise.all([
            loadStyle(this, customcss + '/PharmacyLightning_CSS_HUM.css')
        ]).catch(error => {
            console.log('Error Occured', error);
        });
    }

    onHandleSort(event) {
        if (this.lstCreditCards && Array.isArray(this.lstCreditCards) && this.lstCreditCards.length > 0) {
            event.preventDefault();
            let header = event.currentTarget.dataset.label;
            let sortedBy = event.currentTarget.getAttribute('data-id');
            let sortDirection = event.currentTarget.dataset.iconname === ICON_ARROW_DOWN ? 'asc' : 'desc';
            this.creditCardInfoLayout.forEach(element => {
                if (element?.allowsortig) {
                    if (element.label === header) {
                        element.mousehover = false;
                        element.sorting = true;
                        element.iconname = element.iconname === ICON_ARROW_DOWN ? ICON_ARROW_UP : ICON_ARROW_DOWN;
                    } else {
                        element.mousehover = false;
                        element.sorting = false;
                    }
                }
            });
            const cloneData = [...this.lstCreditCards];
            cloneData.sort(this.sortBy(sortedBy, sortDirection === 'asc' ? 1 : -1, this.getPrimer(sortedBy)));
            this.lstCreditCards = cloneData;
        }
    }

    handleMouseEnter(event) {
        let header = event.target.dataset.label;
        this.creditCardInfoLayout.forEach(element => {
            if (element.label === header && element?.allowsortig) {
                element.mousehover = true,
                    element.mousehovericon = event.target.dataset.iconname === ICON_ARROW_DOWN ? ICON_ARROW_UP : ICON_ARROW_DOWN;
            }
        });
    }

    handleMouseLeave(event) {
        let header = event.target.dataset.label;
        this.creditCardInfoLayout.forEach(element => {
            if (element.label === header && element?.allowsortig) {
                element.mousehover = false
            }
        });
    }

    getPrimer(field) {
        switch(field) {
            case 'expirationDate':
                return (date) => date.split('/').reverse().join('');
            case 'cardHolderName':
                return (name) => name.toLowerCase();
            default: 
                return null;
        }
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

    handleRowAction(event) {
        let selectedRowKey = event && event?.currentTarget && event.currentTarget.hasAttribute('data-cardkey') ?
            Number(event.currentTarget.getAttribute('data-cardkey')) : null;
        if (selectedRowKey) {
            let selectedRowData = this.lstCreditCards.find(el => el.creditCardKey === selectedRowKey);
            this.dispatchEvent(new CustomEvent('editclick', {
                detail: {
                    carddata: selectedRowData
                }
            }))
        }
    }
}