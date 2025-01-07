/*****************************************************************************************************************************
Component Name   : PharmacyCreditCardDetailsHum.js
Version          : 1.0
Function         : lwc to display credit card list

Modification Log:
* Developer Name           Code Review                 Date                         Description
*---------------------------------------------------------------------------------------------------------------------
* Nirmal Garg																	12/06/2022					Original Version
*******************************************************************************************************************************************************************/

import { LightningElement, track, api } from 'lwc';
import customcss from '@salesforce/resourceUrl/PharmacyLightning_SR_HUM';
import { loadStyle } from 'lightning/platformResourceLoader';
import getExpiredAndExpiringSoonIconURLs from '@salesforce/apex/MemberIcons_LC_HUM.getExpiredAndExpiringSoonIconURLs';
import { getLayout } from './layoutconfig';
import nocardmsg from '@salesforce/label/c.FINANCIAL_CREDITCARD_NODATA';
const ICON_ARROW_UP = 'utility:arrowup';
const ICON_ARROW_DOWN = 'utility:arrowdown';

export default class PharmacyCreditCardDetailsHum extends LightningElement {
    @track creditCardInfoLayout = getLayout();
    @api creditcarddata;
    @track lstCreditCards = [];
    @track dataExist = false;
    @track loaded = true;
    @track numCards;
    @track expiringSoonCreditCardIconURL;
    @track expiredCreditCardIconURL;

    labels = {
      nocardmsg
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
    async connectedCallback() {
        this.loaded = false;
        this.loadCommonCSS();
        await this.getIconURLs();
        this.processCreditCards();
        this.updateCardIcon();
    }

    updateCardIcon() {
        if (this.lstCreditCards && this.lstCreditCards.length > 0) {
            this.lstCreditCards.forEach(k => {
                k.IconLabel = k?.IconURL === this.expiredCreditCardIconURL ? 'Expired' : k?.IconURL === this.expiringSoonCreditCardIconURL ? 'Expiring' : ''
            })
        }
    }

    @api
    updateData(creditcards) {
        this.creditcarddata = creditcards;
        this.processCreditCards();
        this.updateCardIcon();
    }

    getExpiredCardIcon() {
        return new Promise((resolve, reject) => {
            getExpiredAndExpiringSoonIconURLs({ sPageName: 'Humana Pharmacy', iconType: 'expired' })
                .then(result => {
                    this.expiredCreditCardIconURL = result;
                    resolve(true);
                })
                .catch(err => {
                    console.log(err);
                    reject(err);
                })
        })
    }

    getExpiringCardIcon() {
        return new Promise((resolve, reject) => {
            getExpiredAndExpiringSoonIconURLs({ sPageName: 'Humana Pharmacy', iconType: 'expiring' })
                .then(result => {
                    this.expiringSoonCreditCardIconURL = result;
                    resolve(true);
                })
                .catch(err => {
                    console.log(err);
                    reject(err);
                })
        })
    }

    processCreditCards() {
        this.lstCreditCards = [];
        if (this.creditcarddata && this.creditcarddata?.CreditCard && Array.isArray(this.creditcarddata.CreditCard) && this.creditcarddata.CreditCard.length > 0) {
            this.dataExist = true;
            this.numCards = this.creditcarddata.CreditCard.length;
            this.creditcarddata.CreditCard.forEach(k => {
                if (k) {
                    this.lstCreditCards.push({
                        Id: this.getUniqueId(),
                        CardType: k?.CreditCardTypeLiteral ?? '',
                        Last4Digits: k?.CreditCardLast4Digits ?? '',
                        CardHolderName: `${k?.FirstName}${k?.MiddleName ? ' ' + k?.MiddleName + ' ' : ' '}${k?.LastName}`,
                        ExpirationDate: `${k?.ExpirationMonth < 10 ? '0' + k?.ExpirationMonth : k?.ExpirationMonth}/${k?.ExpirationYear.slice(2)}`,
                        AutoCharge: k?.IsAutoCharge === 'true' ? 'Y' : 'N',
                        Status: k?.IsActive === 'true' ? 'A' : 'I',
                        SpendingAccount: k?.IsSpendingAccount === 'true' ? 'Y' : 'N',
                        CreditCardKey: k?.CreditCardKey ?? '',
                        IsEditIcon: true,
                        IconURL: this.checkCreditCards(k),
                        //IconLabel: IconURL === this.expiredCreditCardIconURL ? 'Expired' : IconURL === this.expiringSoonCreditCardIconURL ? 'Expiring' : '',
                    })
                }
            })
            const cloneCreditCardData = this.lstCreditCards;
            cloneCreditCardData.sort(this.sortBy('ExpirationDate', -1));
            this.lstCreditCards = cloneCreditCardData;
        }
        this.loaded = true;
    }

    checkCreditCards(card) {
        let currentdate = new Date();
        if (card.IsActive === 'true') {
            let months;
            let expiMonth = card.ExpirationMonth;
            let expiYear = card.ExpirationYear;
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

    getUniqueId() {
        return Math.random().toString(16).slice(2);
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
            cloneData.sort(this.sortBy(sortedBy, sortDirection === 'asc' ? 1 : -1));
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
            event.currentTarget.getAttribute('data-cardkey') : '';
        if (selectedRowKey && selectedRowKey?.length > 0) {
            let selectedRowData = this.lstCreditCards.find(el => el.CreditCardKey === selectedRowKey);
            this.dispatchEvent(new CustomEvent('editclick', {
                detail: {
                    carddata: selectedRowData
                }
            }))
        }
    }
}