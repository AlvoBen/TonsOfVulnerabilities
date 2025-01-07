﻿/*****************************************************************************************************************************
Component Name   : pharmacyHpieCreditCardPaymentHum.js
Version          : 1.0
Function         : lwc to display credit card payment section

Modification Log:
* Developer Name           Code Review                 Date                         Description
*---------------------------------------------------------------------------------------------------------------------
* Jonathan Dickinson							       08/17/2023					User Story 4908778: T1PRJ0870026 - SF - Tech - C12 Mail Order Management - Pharmacy - Finance tab
* Jonathan Dickinson			                       09/04/2023					User Story 4999697: T1PRJ0870026 MF27456 HPIE/CRM - SF Tech- Mail Order Management - Pharmacy - Finance tab - Lightning - Edit Credit Card, One time payment
* Jonathan Dickinson			                       10/04/2023					DF-8185
*******************************************************************************************************************************************************************/

import { LightningElement, api, track } from 'lwc';
import { ShowToastEvent } from 'lightning/platformShowToastEvent';
import creditCardPaymentSuccessMessage from "@salesforce/label/c.CreditCardPaymentSuccessMessage";
import cardpaymentsuccessmsg from '@salesforce/label/c.FINANCIAL_CARDPAYMENT_SUCESS';
import cardpaymenterrormessage from '@salesforce/label/c.FINANCIAL_CARDPAYMENT_ERROR';
import { makeOneTimePayment } from 'c/pharmacyHPIEIntegrationHum';
import { addOneTimePaymentRequest } from './addOneTimePaymentRequest';
const CREDTCARD_HIDDEN_NUMBER = ' ********';
const CREDITCARD_EXP_CONST = ' exp ';
const CREDITCARD_AUTO_CONST = '(Auto) ';
export default class PharmacyCreditCardPaymentHum extends LightningElement {
    @track _creditcarddata;
    @api balancedue;
    @api enterpriseId;
    @api networkId;
    @track lstCardComboData = [];
    @track oneTimePaymentDataObj = {};
    @track cardtype = {};
    @api recordId;
    @api userId;

    selectedCardOwnerName;
    showSelectedCardData = false;
    isSelectedVisaCard = false;
    isSelectedMasterCard = false;
    selectedCardKey;
    lstCardComboData;
    balanceDue;
    selectedCardComboData;
    showConfirmationModal;
    selectedcardTypeLiteral;
    selectedCardLast4Digits;
    enteredAmount = null;
    enteredDate = this.getTodayDate();
    minDateValue = this.getTodayDate();
    showCaseCommentBox = false;
    label = {
        creditCardPaymentSuccessMessage,
        cardpaymentsuccessmsg,
        cardpaymenterrormessage
    }
    @track cardType;
    isCardNotAvailable = true;
    isCardSelected = false;

    cardMap = new Map();

    cardCodeMap =  new Map([
        ["1", "V"],
        ["2", "M"],
        ["3", "D"],
        ["4", "A"]
    ]);

    @api
    get creditcarddata() {
        return this._creditcarddata;
    }

    set creditcarddata(value) {
        if (value) {
            this._creditcarddata = value;
            this.prepareCardComboData();
            this.updateCardType();
        }
    }

    renderedCallback(){        
        this.updateCardType();
    }

    updateCardType(){
        let cardtypelement = this.template.querySelector('.card-type');
        if(cardtypelement) {
            cardtypelement.innerHTML = "";
        }
        this.setCardTypeElement(cardtypelement);
    }

    setCardTypeElement(cardtypelement){
        if(cardtypelement){
           switch (this.cardType) {
               case "V": 
                cardtypelement.innerHTML = '<svg width="60" height="60" viewBox="0 0 146 48" fill="none" xmlns="http://www.w3.org/2000/svg"><path d="M55.4313 0.839503L36.3175 46.4402H23.8467L14.4418 10.0436C13.87 7.80492 13.3773 6.98367 11.6374 6.04075C8.80258 4.50167 4.11842 3.05992 0 2.15959L0.279833 0.839503H20.3548C21.6667 0.838187 22.9358 1.30588 23.9331 2.15815C24.9304 3.01043 25.5902 4.19114 25.7933 5.48717L30.7634 31.8767L43.0396 0.839503H55.4313ZM104.299 31.5542C104.347 19.5153 87.6548 18.8522 87.7703 13.4746C87.8068 11.8382 89.3642 10.0983 92.7708 9.65425C96.7633 9.27508 100.784 9.98128 104.408 11.6983L106.477 2.02575C102.948 0.699251 99.211 0.0132411 95.4414 0C83.7797 0 75.5733 6.205 75.5003 15.0806C75.4273 21.6445 81.3585 25.3006 85.8298 27.4906C90.4288 29.7232 91.9739 31.1588 91.9496 33.1542C91.9192 36.2202 88.2874 37.5646 84.8929 37.6193C78.9617 37.7106 75.5246 36.0194 72.7749 34.7419L70.6397 44.7307C73.3954 45.9961 78.4811 47.1032 83.7553 47.1519C96.1471 47.1519 104.256 41.0321 104.299 31.5542ZM135.086 46.4402H146L136.48 0.839503H126.406C125.329 0.829528 124.275 1.14317 123.379 1.73977C122.483 2.33636 121.787 3.18837 121.381 4.18534L103.684 46.4402H116.07L118.534 39.6268H133.669L135.086 46.4402ZM121.928 30.2828L128.133 13.1582L131.71 30.2828H121.928ZM72.2883 0.839503L62.5367 46.4402H50.735L60.4987 0.839503H72.2883Z" fill="#1E293B"></path></svg>';
                   break;
               
               case "M": 
                cardtypelement.innerHTML = '<svg  xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 146 92" width="60" height="60"><circle fill="#1e293b" cx="45.625" cy="45.625" r="45.625" /><g style="mix-blend-mode: multiply;"><circle fill="#334155" cx="100.375" cy="45.625" r="45.625" /></g></svg>';
                   break;
               
               case "A": 
                cardtypelement.innerHTML = '<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 189 48" width="60" height="60"><path clip-rule="evenodd" fill="#1e293b" fill-rule="evenodd" d="M 21.2789 0 L 0 47.0894 H 25.4738 L 28.6318 39.5814 H 35.8503 L 39.0083 47.0894 H 67.0478 V 41.3591 L 69.5463 47.0894 H 84.0505 L 86.549 41.238 V 47.0894 H 144.864 L 151.954 39.7764 L 158.594 47.0894 L 188.545 47.15 L 167.199 23.6762 L 188.545 0 H 159.058 L 152.156 7.17789 L 145.725 0 H 82.2869 L 76.8394 12.1542 L 71.2641 0 H 45.8435 V 5.53537 L 43.0156 0 H 21.2789 Z M 26.2079 6.6868 H 38.625 L 52.7393 38.6184 V 6.6868 H 66.3418 L 77.2434 29.5816 L 87.2905 6.6868 H 100.825 V 40.4766 H 92.5895 L 92.5223 13.9991 L 80.5158 40.4766 H 73.1489 L 61.0752 13.9991 V 40.4766 H 44.1331 L 40.9211 32.9013 H 23.5684 L 20.3631 40.4699 H 11.2857 L 26.2079 6.6868 Z M 141.847 6.6868 H 108.36 V 40.4566 H 141.328 L 151.954 29.2648 L 162.196 40.4566 H 172.903 L 157.341 23.6696 L 172.903 6.6868 H 162.661 L 152.089 17.7502 L 141.847 6.6868 Z M 32.2484 12.4038 L 26.5313 25.8985 H 37.9588 L 32.2484 12.4038 Z M 116.629 19.8512 V 13.6829 V 13.677 H 137.524 L 146.641 23.5417 L 137.12 33.4603 H 116.629 V 26.7263 H 134.898 V 19.8512 H 116.629 Z" /></svg>';
                   break;
               
               case "D": 
                    
                   cardtypelement.innerHTML = '<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 280 48" width="60" height="60"><path fill="#1e293b" d="M 169.902 23.575 C 169.902 28.2367 168.52 32.7938 165.931 36.6701 C 163.341 40.5465 159.661 43.5682 155.355 45.3532 C 151.048 47.1382 146.309 47.6065 141.737 46.6987 C 137.164 45.7909 132.964 43.548 129.666 40.2533 C 126.368 36.9586 124.12 32.7601 123.208 28.1885 C 122.296 23.617 122.759 18.8775 124.54 14.5694 C 126.321 10.2612 129.339 6.57768 133.213 3.98443 C 137.087 1.39117 141.642 0.0046087 146.304 0 H 146.327 C 159.356 0 169.902 10.5581 169.902 23.575 Z M 109.304 0.256369 C 96.2057 0.256369 85.7059 10.5581 85.7059 23.4468 C 85.7059 36.4754 95.9377 46.5673 109.094 46.5673 C 112.812 46.5673 116.005 45.8332 119.932 43.9919 V 33.8184 C 116.483 37.2795 113.418 38.6663 109.502 38.6663 C 100.797 38.6663 94.6208 32.3501 94.6208 23.3769 C 94.6208 14.8698 100.995 8.15744 109.106 8.15744 C 113.231 8.15744 116.355 9.62577 119.944 13.1451 V 2.97164 C 116.682 1.18661 113.023 0.252732 109.304 0.256369 Z M 70.2417 18.261 C 65.0559 16.3382 63.5293 15.0796 63.5293 12.679 C 63.5293 9.8938 66.2446 7.76121 69.9737 7.76121 C 72.5608 7.76121 74.6934 8.82169 76.9425 11.3505 L 81.4641 5.43052 C 77.8823 2.23828 73.2449 0.486263 68.4471 0.51275 C 60.6043 0.51275 54.6261 5.95492 54.6261 13.2034 C 54.6261 19.3098 57.4112 22.433 65.5337 25.358 C 68.9249 26.5583 70.638 27.3507 71.512 27.8868 C 72.3052 28.361 72.9611 29.034 73.4148 29.8392 C 73.8685 30.6444 74.1043 31.554 74.099 32.4783 C 74.099 36.0675 71.2439 38.7245 67.3866 38.7245 C 63.2613 38.7245 59.94 36.6619 57.959 32.8162 L 52.3769 38.1885 C 56.3624 44.0385 61.1404 46.6256 67.7246 46.6256 C 76.7094 46.6256 83.0023 40.6474 83.0023 32.0704 C 82.979 25.0433 80.0656 21.8619 70.2417 18.261 Z M 279.678 3.76408 C 279.681 4.65837 279.33 5.51744 278.701 6.15307 C 278.072 6.78871 277.216 7.14907 276.322 7.15523 C 275.876 7.15677 275.435 7.07012 275.023 6.90024 C 274.61 6.73036 274.236 6.48063 273.921 6.16542 C 273.606 5.85021 273.356 5.47577 273.186 5.06364 C 273.016 4.65151 272.929 4.20984 272.931 3.76408 V 3.72911 C 272.906 3.26932 272.976 2.80937 273.136 2.37747 C 273.295 1.94558 273.541 1.55086 273.859 1.21753 C 274.177 0.884206 274.559 0.619303 274.983 0.439088 C 275.407 0.258874 275.863 0.167153 276.323 0.169538 C 276.784 0.171924 277.239 0.268374 277.66 0.452969 C 278.082 0.637565 278.462 0.906405 278.776 1.243 C 279.09 1.5796 279.332 1.97685 279.488 2.41038 C 279.643 2.8439 279.708 3.30456 279.678 3.76408 Z M 278.991 3.77572 C 278.997 3.41607 278.932 3.05874 278.8 2.72412 C 278.668 2.38949 278.472 2.08414 278.222 1.8255 C 277.972 1.56686 277.673 1.36001 277.343 1.21674 C 277.013 1.07348 276.658 0.996616 276.299 0.990547 C 275.938 0.99355 275.581 1.06824 275.249 1.21029 C 274.917 1.35233 274.616 1.55891 274.365 1.81801 C 274.113 2.07712 273.916 2.3836 273.784 2.71967 C 273.651 3.05574 273.587 3.4147 273.595 3.77572 C 273.595 5.31398 274.807 6.56091 276.299 6.56091 C 276.658 6.55484 277.013 6.47798 277.343 6.33471 C 277.673 6.19145 277.972 5.98459 278.222 5.72595 C 278.472 5.46731 278.668 5.16196 278.8 4.82734 C 278.932 4.49271 278.997 4.13537 278.991 3.77572 Z M 40.4554 45.5651 H 49.1023 V 1.24692 H 40.4554 V 45.5651 Z M 188.478 31.0216 L 176.661 1.25857 H 167.222 L 186.031 46.7072 H 190.681 L 209.827 1.25857 H 200.458 L 188.478 31.0216 Z M 213.72 45.5651 H 238.238 V 38.0603 H 222.366 V 26.0921 H 237.656 V 18.5873 H 222.366 V 8.75176 H 238.238 V 1.24692 H 213.72 V 45.5651 Z M 276.753 4.11368 L 278.035 5.72186 H 276.905 L 275.809 4.20691 V 5.72186 H 274.877 V 1.82959 H 276.124 C 277.068 1.82959 277.592 2.24913 277.592 3.02991 C 277.604 3.56597 277.301 3.96218 276.753 4.11368 Z M 276.683 3.04156 C 276.683 2.7036 276.439 2.54045 275.984 2.54045 H 275.821 V 3.55431 H 275.984 C 276.439 3.55431 276.683 3.39116 276.683 3.04156 Z M 262.373 26.8962 L 276.322 45.5651 H 265.694 L 253.726 27.7586 H 252.596 V 45.5651 H 243.96 V 1.24692 H 256.779 C 266.743 1.24692 272.465 6.0365 272.465 14.3338 C 272.465 21.1278 268.875 25.5794 262.373 26.8962 Z M 263.573 14.7999 C 263.573 10.4881 260.648 8.23901 255.264 8.23901 H 252.607 V 21.6638 H 255.136 C 260.648 21.6522 263.573 19.2516 263.573 14.7999 Z M 36.4816 23.4468 C 36.4652 26.6979 35.7368 29.906 34.3476 32.8454 C 32.9585 35.7849 30.9422 38.3843 28.4407 40.4609 C 24.1871 43.9919 19.3393 45.5768 12.6269 45.5768 H 0.00614929 V 1.25857 H 12.6968 C 26.7043 1.25857 36.4816 10.3599 36.4816 23.4468 Z M 27.5783 23.3769 C 27.5783 19.135 25.7837 15.0097 22.7887 12.341 C 19.9336 9.75397 16.5425 8.75176 10.9604 8.75176 H 8.64139 V 38.0603 H 10.9604 C 16.5425 38.0603 20.0735 36.9882 22.7887 34.5409 C 25.772 31.8839 27.5783 27.6304 27.5783 23.3769 Z" /></svg>';
                   break;
               
               default: 
                cardtypelement.innerHTML = '<svg width="60" height="60" viewBox="0 0 146 48" fill="none" xmlns="http://www.w3.org/2000/svg"></svg>';
               
           }
       }
   }

    prepareCardComboData() {
        const me = this;
        me.lstCardComboData = [];
        if (me.creditcarddata && me.creditcarddata?.paymentCards && Array.isArray(me.creditcarddata.paymentCards) && me.creditcarddata.paymentCards.length > 0) {
            me.creditcarddata.paymentCards.forEach(k => {
                if (k.active && (!this.isCreditCardExpired(k))) {
                    const last4Digits = k.tokenKey.slice(-4);
                    me.lstCardComboData = me.lstCardComboData === undefined ? [] : me.lstCardComboData;
                    me.lstCardComboData.push({
                        label: k.autoCharge ? CREDITCARD_AUTO_CONST + k.z0type.description + CREDTCARD_HIDDEN_NUMBER + last4Digits + CREDITCARD_EXP_CONST + `${k.expirationMonth.toString().padStart(2, '0')}/${k.expirationYear.toString().slice(-2)}` :
                            k.z0type.description + CREDTCARD_HIDDEN_NUMBER + last4Digits + CREDITCARD_EXP_CONST + `${k.expirationMonth.toString().padStart(2, '0')}/${k.expirationYear.toString().slice(-2)}`,
                        value: k.key
                    });

                    me.cardMap.set(k.key, k);

                    if (k.autoCharge) {
                        me.selectedCardComboData = k.key;
                        me.cardType = this.cardCodeMap.get(k.z0type.code);
                        me.selectedCardOwnerName = `${k.firstName}${k.middleName ? ' ' + k.middleName + ' ' : ' '}${k.lastName}`;
                        me.selectedCardExpiryDate = `${k?.expirationMonth.toString().padStart(2, '0')}/${k?.expirationYear.toString().slice(-2)}`;
                        me.selectedCardKey = k.key;
                        me.selectedcardTypeLiteral = k.z0type.description;
                        me.selectedCardLast4Digits = last4Digits;
                        me.isCardSelected = true;
                        this.enableSubmitButton();
                    }
                }
            })

        }
    }

    isCreditCardExpired(card) {
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
                return true;
            } else {
                return false;
            }
        }
    }

    processSelectedCard(selectedCardKey) {
        const me = this;
        let cardtypelement = me.template.querySelector('.card-type');
        if(cardtypelement) {
            cardtypelement.innerHTML = "";
        }
        me.cardtype = {};
        let card = me.cardMap.get(selectedCardKey);
        if (card != null) {
            this.cardType = this.cardCodeMap.get(card.z0type.code);
            switch (card.CreditCardType) {
                case "V": {
                    me.cardtype.type = card.CreditCardTypeLiteral;
                    cardtypelement.innerHTML = '<svg width="60" height="60" viewBox="0 0 146 48" fill="none" xmlns="http://www.w3.org/2000/svg"><path d="M55.4313 0.839503L36.3175 46.4402H23.8467L14.4418 10.0436C13.87 7.80492 13.3773 6.98367 11.6374 6.04075C8.80258 4.50167 4.11842 3.05992 0 2.15959L0.279833 0.839503H20.3548C21.6667 0.838187 22.9358 1.30588 23.9331 2.15815C24.9304 3.01043 25.5902 4.19114 25.7933 5.48717L30.7634 31.8767L43.0396 0.839503H55.4313ZM104.299 31.5542C104.347 19.5153 87.6548 18.8522 87.7703 13.4746C87.8068 11.8382 89.3642 10.0983 92.7708 9.65425C96.7633 9.27508 100.784 9.98128 104.408 11.6983L106.477 2.02575C102.948 0.699251 99.211 0.0132411 95.4414 0C83.7797 0 75.5733 6.205 75.5003 15.0806C75.4273 21.6445 81.3585 25.3006 85.8298 27.4906C90.4288 29.7232 91.9739 31.1588 91.9496 33.1542C91.9192 36.2202 88.2874 37.5646 84.8929 37.6193C78.9617 37.7106 75.5246 36.0194 72.7749 34.7419L70.6397 44.7307C73.3954 45.9961 78.4811 47.1032 83.7553 47.1519C96.1471 47.1519 104.256 41.0321 104.299 31.5542ZM135.086 46.4402H146L136.48 0.839503H126.406C125.329 0.829528 124.275 1.14317 123.379 1.73977C122.483 2.33636 121.787 3.18837 121.381 4.18534L103.684 46.4402H116.07L118.534 39.6268H133.669L135.086 46.4402ZM121.928 30.2828L128.133 13.1582L131.71 30.2828H121.928ZM72.2883 0.839503L62.5367 46.4402H50.735L60.4987 0.839503H72.2883Z" fill="#1E293B"></path></svg>';
                    break;
                }
                case "M": {
                    me.cardtype.type = card.CreditCardTypeLiteral;
                    cardtypelement.innerHTML = '<svg  xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 146 92" width="60" height="60"><circle fill="#1e293b" cx="45.625" cy="45.625" r="45.625" /><g style="mix-blend-mode: multiply;"><circle fill="#334155" cx="100.375" cy="45.625" r="45.625" /></g></svg>';
                    break;
                }
                case "A": {
                    me.cardtype.type = card.CreditCardTypeLiteral;
                    cardtypelement.innerHTML = '<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 189 48" width="60" height="60"><path clip-rule="evenodd" fill="#1e293b" fill-rule="evenodd" d="M 21.2789 0 L 0 47.0894 H 25.4738 L 28.6318 39.5814 H 35.8503 L 39.0083 47.0894 H 67.0478 V 41.3591 L 69.5463 47.0894 H 84.0505 L 86.549 41.238 V 47.0894 H 144.864 L 151.954 39.7764 L 158.594 47.0894 L 188.545 47.15 L 167.199 23.6762 L 188.545 0 H 159.058 L 152.156 7.17789 L 145.725 0 H 82.2869 L 76.8394 12.1542 L 71.2641 0 H 45.8435 V 5.53537 L 43.0156 0 H 21.2789 Z M 26.2079 6.6868 H 38.625 L 52.7393 38.6184 V 6.6868 H 66.3418 L 77.2434 29.5816 L 87.2905 6.6868 H 100.825 V 40.4766 H 92.5895 L 92.5223 13.9991 L 80.5158 40.4766 H 73.1489 L 61.0752 13.9991 V 40.4766 H 44.1331 L 40.9211 32.9013 H 23.5684 L 20.3631 40.4699 H 11.2857 L 26.2079 6.6868 Z M 141.847 6.6868 H 108.36 V 40.4566 H 141.328 L 151.954 29.2648 L 162.196 40.4566 H 172.903 L 157.341 23.6696 L 172.903 6.6868 H 162.661 L 152.089 17.7502 L 141.847 6.6868 Z M 32.2484 12.4038 L 26.5313 25.8985 H 37.9588 L 32.2484 12.4038 Z M 116.629 19.8512 V 13.6829 V 13.677 H 137.524 L 146.641 23.5417 L 137.12 33.4603 H 116.629 V 26.7263 H 134.898 V 19.8512 H 116.629 Z" /></svg>';
                    break;
                }
                case "D": {
                    me.cardtype.type = card.CreditCardTypeLiteral;
                    cardtypelement.innerHTML = '<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 280 48" width="60" height="60"><path fill="#1e293b" d="M 169.902 23.575 C 169.902 28.2367 168.52 32.7938 165.931 36.6701 C 163.341 40.5465 159.661 43.5682 155.355 45.3532 C 151.048 47.1382 146.309 47.6065 141.737 46.6987 C 137.164 45.7909 132.964 43.548 129.666 40.2533 C 126.368 36.9586 124.12 32.7601 123.208 28.1885 C 122.296 23.617 122.759 18.8775 124.54 14.5694 C 126.321 10.2612 129.339 6.57768 133.213 3.98443 C 137.087 1.39117 141.642 0.0046087 146.304 0 H 146.327 C 159.356 0 169.902 10.5581 169.902 23.575 Z M 109.304 0.256369 C 96.2057 0.256369 85.7059 10.5581 85.7059 23.4468 C 85.7059 36.4754 95.9377 46.5673 109.094 46.5673 C 112.812 46.5673 116.005 45.8332 119.932 43.9919 V 33.8184 C 116.483 37.2795 113.418 38.6663 109.502 38.6663 C 100.797 38.6663 94.6208 32.3501 94.6208 23.3769 C 94.6208 14.8698 100.995 8.15744 109.106 8.15744 C 113.231 8.15744 116.355 9.62577 119.944 13.1451 V 2.97164 C 116.682 1.18661 113.023 0.252732 109.304 0.256369 Z M 70.2417 18.261 C 65.0559 16.3382 63.5293 15.0796 63.5293 12.679 C 63.5293 9.8938 66.2446 7.76121 69.9737 7.76121 C 72.5608 7.76121 74.6934 8.82169 76.9425 11.3505 L 81.4641 5.43052 C 77.8823 2.23828 73.2449 0.486263 68.4471 0.51275 C 60.6043 0.51275 54.6261 5.95492 54.6261 13.2034 C 54.6261 19.3098 57.4112 22.433 65.5337 25.358 C 68.9249 26.5583 70.638 27.3507 71.512 27.8868 C 72.3052 28.361 72.9611 29.034 73.4148 29.8392 C 73.8685 30.6444 74.1043 31.554 74.099 32.4783 C 74.099 36.0675 71.2439 38.7245 67.3866 38.7245 C 63.2613 38.7245 59.94 36.6619 57.959 32.8162 L 52.3769 38.1885 C 56.3624 44.0385 61.1404 46.6256 67.7246 46.6256 C 76.7094 46.6256 83.0023 40.6474 83.0023 32.0704 C 82.979 25.0433 80.0656 21.8619 70.2417 18.261 Z M 279.678 3.76408 C 279.681 4.65837 279.33 5.51744 278.701 6.15307 C 278.072 6.78871 277.216 7.14907 276.322 7.15523 C 275.876 7.15677 275.435 7.07012 275.023 6.90024 C 274.61 6.73036 274.236 6.48063 273.921 6.16542 C 273.606 5.85021 273.356 5.47577 273.186 5.06364 C 273.016 4.65151 272.929 4.20984 272.931 3.76408 V 3.72911 C 272.906 3.26932 272.976 2.80937 273.136 2.37747 C 273.295 1.94558 273.541 1.55086 273.859 1.21753 C 274.177 0.884206 274.559 0.619303 274.983 0.439088 C 275.407 0.258874 275.863 0.167153 276.323 0.169538 C 276.784 0.171924 277.239 0.268374 277.66 0.452969 C 278.082 0.637565 278.462 0.906405 278.776 1.243 C 279.09 1.5796 279.332 1.97685 279.488 2.41038 C 279.643 2.8439 279.708 3.30456 279.678 3.76408 Z M 278.991 3.77572 C 278.997 3.41607 278.932 3.05874 278.8 2.72412 C 278.668 2.38949 278.472 2.08414 278.222 1.8255 C 277.972 1.56686 277.673 1.36001 277.343 1.21674 C 277.013 1.07348 276.658 0.996616 276.299 0.990547 C 275.938 0.99355 275.581 1.06824 275.249 1.21029 C 274.917 1.35233 274.616 1.55891 274.365 1.81801 C 274.113 2.07712 273.916 2.3836 273.784 2.71967 C 273.651 3.05574 273.587 3.4147 273.595 3.77572 C 273.595 5.31398 274.807 6.56091 276.299 6.56091 C 276.658 6.55484 277.013 6.47798 277.343 6.33471 C 277.673 6.19145 277.972 5.98459 278.222 5.72595 C 278.472 5.46731 278.668 5.16196 278.8 4.82734 C 278.932 4.49271 278.997 4.13537 278.991 3.77572 Z M 40.4554 45.5651 H 49.1023 V 1.24692 H 40.4554 V 45.5651 Z M 188.478 31.0216 L 176.661 1.25857 H 167.222 L 186.031 46.7072 H 190.681 L 209.827 1.25857 H 200.458 L 188.478 31.0216 Z M 213.72 45.5651 H 238.238 V 38.0603 H 222.366 V 26.0921 H 237.656 V 18.5873 H 222.366 V 8.75176 H 238.238 V 1.24692 H 213.72 V 45.5651 Z M 276.753 4.11368 L 278.035 5.72186 H 276.905 L 275.809 4.20691 V 5.72186 H 274.877 V 1.82959 H 276.124 C 277.068 1.82959 277.592 2.24913 277.592 3.02991 C 277.604 3.56597 277.301 3.96218 276.753 4.11368 Z M 276.683 3.04156 C 276.683 2.7036 276.439 2.54045 275.984 2.54045 H 275.821 V 3.55431 H 275.984 C 276.439 3.55431 276.683 3.39116 276.683 3.04156 Z M 262.373 26.8962 L 276.322 45.5651 H 265.694 L 253.726 27.7586 H 252.596 V 45.5651 H 243.96 V 1.24692 H 256.779 C 266.743 1.24692 272.465 6.0365 272.465 14.3338 C 272.465 21.1278 268.875 25.5794 262.373 26.8962 Z M 263.573 14.7999 C 263.573 10.4881 260.648 8.23901 255.264 8.23901 H 252.607 V 21.6638 H 255.136 C 260.648 21.6522 263.573 19.2516 263.573 14.7999 Z M 36.4816 23.4468 C 36.4652 26.6979 35.7368 29.906 34.3476 32.8454 C 32.9585 35.7849 30.9422 38.3843 28.4407 40.4609 C 24.1871 43.9919 19.3393 45.5768 12.6269 45.5768 H 0.00614929 V 1.25857 H 12.6968 C 26.7043 1.25857 36.4816 10.3599 36.4816 23.4468 Z M 27.5783 23.3769 C 27.5783 19.135 25.7837 15.0097 22.7887 12.341 C 19.9336 9.75397 16.5425 8.75176 10.9604 8.75176 H 8.64139 V 38.0603 H 10.9604 C 16.5425 38.0603 20.0735 36.9882 22.7887 34.5409 C 25.772 31.8839 27.5783 27.6304 27.5783 23.3769 Z" /></svg>';
                    break;
                }
            }
            me.selectedCardOwnerName = `${card.firstName}${card.middleName ? ' ' + card.middleName + ' ' : ' '}${card.lastName}`;
            me.selectedCardExpiryDate = `${card?.expirationMonth.toString().padStart(2, '0')}/${card?.expirationYear.toString().slice(-2)}`;
            me.selectedCardKey = card.key;
			me.selectedCardComboData = card.key;
            me.selectedcardTypeLiteral = card.z0type.description;
            me.selectedCardLast4Digits = card.tokenKey.slice(-4);
            me.balanceDue = me.getduebalance(me.balancedue);
            me.isCardSelected = true;
            this.enableSubmitButton();
        }
    }
    handleSumitCardUpdate() {
        const allValid = [...this.template.querySelectorAll('.validate')]
            .reduce((validSoFar, inputCmp) => {
                inputCmp.reportValidity();
                return validSoFar && inputCmp.checkValidity();
            }, true);

        if (allValid && this.selectedCardComboData) {
            this.showConfirmationModal = true;
        }
        else {
            this.showConfirmationModal = false;
        }
    }

    closeModal() {
        this.showConfirmationModal = false;
    }

    handleCardChange(event) {
        const me = this;
        const cardKey = Number(event.target.value);
        me.processSelectedCard(cardKey);
    }

    handlePaymentInput(event) {
        this.enteredAmount = event.target.value;
        this.enableSubmitButton();
    }

    generateCaseComment() {
        let sCaseComments = `Processed a one-time charge payment of $${Number.parseFloat(this.enteredAmount).toFixed(2)} to credit card ending in ${this.selectedCardLast4Digits}.`;
        return sCaseComments;
    }

    handlePaymentServiceCall() {
        this.showConfirmationModal = false;
        let paymentAmount = parseFloat(this.enteredAmount);

        const paymentRequest = new addOneTimePaymentRequest(this.enterpriseId, this.userId, this.organization ?? 'HUMANA', this.selectedCardKey, paymentAmount, this.enteredDate, false);
        makeOneTimePayment(paymentRequest)
            .then(result => {
                if (result) {
                    this.enteredDate = this.getTodayDate();
                    this.minDateValue = this.getTodayDate();
                    this.showToast("Information", this.label.cardpaymentsuccessmsg, "info");
                    this.oneTimePaymentDataObj = {};
                    this.oneTimePaymentDataObj = {
                        header: "Successfully Processed",
                        tablayout: false,
                        source: "Onetime Payment",
                        message: this.label.creditCardPaymentSuccessMessage,
                        caseComment: this.generateCaseComment(),
                        redirecttocaseedit : true
                    }
                    this.enteredAmount = "";
                    this.showCaseCommentBox = true;
                } else {
                    this.showToast("Error", this.label.cardpaymenterrormessage, "error");
                    console.log("Error - " + result);
                }
            }).catch(error => {
                this.showToast("Error", this.label.cardpaymenterrormessage, "error");
                console.log("Error - Failed makeOneTimePayment - " + error);
            })
    }

    handleFinishLogging() {
        this.showCaseCommentBox = false;
    }

    showToast(strTitle, strMessage, strStyle) {
        this.dispatchEvent(new ShowToastEvent({
            title: strTitle,
            message: strMessage,
            variant: strStyle
        }));
    }

    getduebalance(balanceDue) {
        const me = this;
        if(balanceDue){
            if (parseFloat(balanceDue) === 0.0) {
                return '$ 0.00';
            } else if (parseFloat(balanceDue) > 0) {
                return '$ ' + balanceDue;
            } else if (parseFloat(balanceDue) < 0) {
                return '($' + (balanceDue * -1) + ')';
            }
        }else{
            return '';
        }  
    }

    getTodayDate() {
        let today = new Date();
        let dd = String(today.getDate()).padStart(2, '0');
        let mm = String(today.getMonth() + 1).padStart(2, '0');
        let yyyy = today.getFullYear();
        today = yyyy + '-' + mm + '-' + dd;
        return today;
    }

    handleCancelCardUpdate() {
        this.showSelectedCardData = false;
        this.enteredDate = this.getTodayDate();
        this.minDateValue = this.getTodayDate();
        this.enteredAmount = "";
        this.selectedCardComboData = null;
        this.selectedCardOwnerName = "";
        this.isCardNotAvailable = true;
        let fields = this.template.querySelectorAll('lightning-combobox');
        fields.forEach(function (item) {
            item.value = '';
        });
    }

    handlePaymentDateChange(event) {
        this.enteredDate = event.target.value;
        this.enableSubmitButton();
    }

    validateAmountInput(component, event, helper) {
        if (component.which != 46 && component.which != 8 && component.which != 0 && component.which < 48 || component.which > 57) {
            component.preventDefault();
        }
    }

    enableSubmitButton(){        
        this.isCardNotAvailable = (((this.enteredAmount?.length ?? 0) > 0) && (this.enteredAmount && (parseFloat(this.enteredAmount)) > 0) && (this.enteredDate && this.enteredDate >= this.minDateValue) && this.isCardSelected) ? false : true;
    }
}