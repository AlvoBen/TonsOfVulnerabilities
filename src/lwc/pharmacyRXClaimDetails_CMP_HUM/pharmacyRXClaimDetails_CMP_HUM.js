/******************************************************************************************************************
LWC Name           : PharmacyRXClaimDetails_CMP_HUM.js
Version            : 1.0
Function           : This js component contains logic to fetch and displayPharmacy Rx Claims Detail information on UI.
Created On         : July 24 2020
*******************************************************************************************************************
Modification Log:
* Developer Name            Code Review                Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------
* Ranadheer Goud                                     07/24/2020                 Original Version - REQ - 1151554 -- PR00094920- MF 1- Rx Claims- Pharmacy (Rx) Claim Details page (RxXP)
*******************************************************************************************************************/

import { LightningElement, api, wire,track } from 'lwc';
import GetClaimDetail from '@salesforce/apexContinuation/PharmacyRXClaimsDetail_C_HUM.GetClaimDetail';

export default class RecordDetailComponent extends LightningElement {
    data = {};
    displayErrorMessage = false;
    connectedCallback() {
        GetClaimDetail({
            memberGenKey: this.ClaimID,
            authorizationNumber: this.AuthorizationNumber
        }).
        then(data => {
            if (data && data != {}) {
                this.data = data;
            } else if (data != null && data != undefined) {
                if (data == 'No Data') {
                    this.displayNoDataMessage = true;
                } else if (data == 'Integration Error') {
                    this.displayErrorMessage = true;
                }
                }else if(data == null){
                    this.displayErrorMessage = true;

                }
            console.log('data' + JSON.stringify(data));

        }).catch(e => {
            this.displayErrorMessage = true;
            console.log('Error : ' + JSON.stringify(e));
        })        
    }
    
    scrolltoElement;
    isExpanded = true;

    activeSections = ['Claim Detail'];
    claimOpenSections = ['ClaimInformation', 'ClaimPricingInformation'];
    errorOpenSections = ['Errors', 'AdditionalMessages', 'ClaimDURMessage'];
    drugInformationOpenSections = ['DrugIndicatorsOrDrugClasses', 'DosingRoute'];
    benefitsOpenSections = ['GeneralMember', 'AppliedAmounts'];
    @api
    rowId;
    @api
    ClaimID
    @api
    AuthorizationNumber
    @api
    memberName

    gotoTarget(event) {

        var active = this.template.querySelector('.Accordian').activeSectionName;
        if (!active.includes(event.target.name)) {
            active.push(event.target.name);
            this.template.querySelector('.Accordian').activeSectionName = active;
        }
       
            var elem = this.template.querySelectorAll('.' + event.target.className)[1];
            var elemTop = elem.getBoundingClientRect().top - 50;
            elemTop += window.scrollY ? window.scrollY : window.pageYOffset;
           
        window.setTimeout(function(){
            window.scrollTo(0, elemTop);
        },1000);

       

    }
    togglePricing(event) {
        this.isExpanded = !this.isExpanded;
    }
    get errorColumn() {
        var columns = [{
                label: 'NCPDP',
                fieldName: 'NCPDP',
                hideDefaultActions: true
            },
            {
                label: 'Reject Field',
                fieldName: 'RejectField',
                hideDefaultActions: true
            },
            {
                label: 'Argus',
                fieldName: 'Argus',
                hideDefaultActions: true
            },
            {
                label: 'Action',
                fieldName: 'Action',
                hideDefaultActions: true
            },
            {
                label: 'Argus Error Text',
                fieldName: 'ArgusErrorText',
                hideDefaultActions: true
            },

        ];
        return columns;
    }
    get additionalMessagesColumn() {
        var columns = [{
                label: 'Qualifier',
                fieldName: 'Qualifier',
                hideDefaultActions: true
            },
            {
                label: 'Continue',
                fieldName: 'Continue',
                hideDefaultActions: true
            },
            {
                label: 'Message',
                fieldName: 'Message',
                hideDefaultActions: true
            },
        ];
        return columns;

    }
    get deductibleMessageColumn() {
        var columns = [{
            label: 'Deductible Messages',
            fieldName: 'DM',
            hideDefaultActions: true
        }, ];
        return columns;
    }
    get ClaimDURMesagesColumn() {
        var columns = [{
                label: 'DUR Free Text Message',
                fieldName: 'DURFreeTextMessage',
                hideDefaultActions: true
            },
            {
                label: 'DUR Additional Text Message',
                fieldName: 'DURAdditionalTextMessage',
                hideDefaultActions: true
            },
        ];
        return columns;


    }
    get BenefitStageDetailseColumn() {
        var columns = [{
                label: 'Benefit Stage Qualifier',
                fieldName: 'BenefitStageQualifier',
                hideDefaultActions: true
            },
            {
                label: 'Benefit Stage Amount',
                fieldName: 'BenefitStageAmount',
                hideDefaultActions: true
            },
        ];
        return columns;

    }
    
    get Address() {
        if (this.data && this.data.RxClaimPharmacy) {
            return this.data.RxClaimPharmacy.PharmacyAddress1 + this.data.RxClaimPharmacy.PharmacyAddress2;
        } else {
            return null;
        }
    }
    get AWP_TOT_AMT() {
        if (this.data && this.data.RxClaimAmounts) {
            var amnt = this.data.RxClaimAmounts.filter(e => {
                return e.ClaimAmountType == 'AWP_TOT_AMT';
            });
            if (amnt.length) {
                return amnt[0].ClaimAmountValue;
            } else {
                return null
            }
        } else {
            return null;
        }
    }
    get UC_AMT() {
        if (this.data && this.data.RxClaimAmounts) {
            var amnt = this.data.RxClaimAmounts.filter(e => {
                return e.ClaimAmountType == 'UC_AMT';
            });
            if (amnt.length) {
                return amnt[0].ClaimAmountValue;
            } else {
                return null
            }
        } else {
            return null;
        }
    }
    get DISPENSE_FEE() {
        if (this.data && this.data.RxClaimAmounts) {
            var amnt = this.data.RxClaimAmounts.filter(e => {
                return e.ClaimAmountType == 'DISPENSE_FEE';
            });
            if (amnt.length) {
                return amnt[0].ClaimAmountValue;
            } else {
                return null
            }
        } else {
            return null;
        }
    }
    get WAC_INGRD_COST_AMT() {
        if (this.data && this.data.RxClaimAmounts) {
            var amnt = this.data.RxClaimAmounts.filter(e => {
                return e.ClaimAmountType == 'WAC_INGRD_COST_AMT';
            });
            if (amnt.length) {
                return amnt[0].ClaimAmountValue;
            } else {
                return null
            }
        } else {
            return null;
        }
    }
    get PHAR_INGR_COST_AMT() {
        if (this.data && this.data.RxClaimAmounts) {
            var amnt = this.data.RxClaimAmounts.filter(e => {
                return e.ClaimAmountType == 'PHAR_INGR_COST_AMT';
            });
            if (amnt.length) {
                return amnt[0].ClaimAmountValue;
            } else {
                return null
            }
        } else {
            return null;
        }
    }
    get RX_SALES_TAX_AMT() {
        if (this.data && this.data.RxClaimAmounts) {
            var amnt = this.data.RxClaimAmounts.filter(e => {
                return e.ClaimAmountType == 'RX_SALES_TAX_AMT';
            });
            if (amnt.length) {
                return amnt[0].ClaimAmountValue;
            } else {
                return null
            }
        } else {
            return null;
        }
    }
    get PHAR_TOTAL_PAID_AMT() {
        if (this.data && this.data.RxClaimAmounts) {
            var amnt = this.data.RxClaimAmounts.filter(e => {
                return e.ClaimAmountType == 'PHAR_TOTAL_PAID_AMT';
            });
            if (amnt.length) {
                return amnt[0].ClaimAmountValue;
            } else {
                return null
            }
        } else {
            return null;
        }
    }
    get TROOP_APPLY_AMT() {
        if (this.data && this.data.RxClaimAmounts) {
            var amnt = this.data.RxClaimAmounts.filter(e => {
                return e.ClaimAmountType == 'TROOP_APPLY_AMT';
            });
            if (amnt.length) {
                return amnt[0].ClaimAmountValue;
            } else {
                return null
            }
        } else {
            return null;
        }
    }
    get OTH_TROOP_AMT() {
        if (this.data && this.data.RxClaimAmounts) {
            var amnt = this.data.RxClaimAmounts.filter(e => {
                return e.ClaimAmountType == 'OTH_TROOP_AMT';
            });
            if (amnt.length) {
                return amnt[0].ClaimAmountValue;
            } else {
                return null
            }
        } else {
            return null;
        }
    }
    get PLRO_AMT() {
        if (this.data && this.data.RxClaimAmounts) {
            var amnt = this.data.RxClaimAmounts.filter(e => {
                return e.ClaimAmountType == 'PLRO_AMT';
            });
            if (amnt.length) {
                return amnt[0].ClaimAmountValue;
            } else {
                return null
            }
        } else {
            return null;
        }
    }
    get COV_GAP_AMT() {
        if (this.data && this.data.RxClaimAmounts) {
            var amnt = this.data.RxClaimAmounts.filter(e => {
                return e.ClaimAmountType == 'COV_GAP_AMT';
            });
            if (amnt.length) {
                return amnt[0].ClaimAmountValue;
            } else {
                return null
            }
        } else {
            return null;
        }
    }
    get TOT_TROOP_ACCUM_AMT() {
        if (this.data && this.data.RxClaimAmounts) {
            var amnt = this.data.RxClaimAmounts.filter(e => {
                return e.ClaimAmountType == 'TOT_TROOP_ACCUM_AMT';
            });
            if (amnt.length) {
                return amnt[0].ClaimAmountValue;
            } else {
                return null
            }
        } else {
            return null;
        }
    }
    get TROOP_APPLY_AMT() {
        if (this.data && this.data.RxClaimAmounts) {
            var amnt = this.data.RxClaimAmounts.filter(e => {
                return e.ClaimAmountType == 'TROOP_APPLY_AMT';
            });
            if (amnt.length) {
                return amnt[0].ClaimAmountValue;
            } else {
                return null
            }
        } else {
            return null;
        }
    }
    get TimeAdded(){
        if(this.data && this.data.ClaimLoadDate){
            var d = new Date(Date.parse(this.data.ClaimLoadDate));
            return d.toLocaleTimeString('en-US',{hour12:false});
        }else{
            return null;
        }
    }
    get TimeUpdated(){
        if(this.data && this.data.ClaimAdjudicationDate){
            var d = new Date(Date.parse(this.data.ClaimAdjudicationDate));
            return d.toLocaleTimeString('en-US',{hour12:false});
        }else{
            return null;
        }

    }
    get DateFilled(){
        if(this.data && this.data.DateOfService){
            var d = new Date(Date.parse(this.data.DateOfService));
            return d.toLocaleDateString();
        }else{
            return null;
        }

    }
    get DateUpdatedId(){
        if(this.data && this.data.ClaimAdjudicationDate){
            var d = new Date(Date.parse(this.data.ClaimAdjudicationDate));
            return d.toLocaleDateString();
        }else{
            return null;
        }
    }
    get DateAdded(){
        if(this.data && this.data.ClaimLoadDate){
            var d = new Date(Date.parse(this.data.ClaimLoadDate));
            return d.toLocaleDateString();
        }else{
            return null;
        }

    }
    get DateWritten(){
        if(this.data && this.data.RxWrittenDate){
            var d = new Date(Date.parse(this.data.RxWrittenDate));
            return d.toLocaleDateString();
        }else{
            return null;
        }

    }
    get NDC(){
        if(this.data && this.data.NDC && this.data.NDC.length ==11){
                var ndc = this.data.NDC.split('');
                ndc.splice(5,0,'-');
                ndc.splice(10,0,'-');
                return ndc.join('');
        }else{
            return null;
        }
    }
    get Phone(){
        if(this.data 
            && this.data.RxClaimPharmacy 
            && this.data.RxClaimPharmacy.PharmacyPhone
            && this.data.RxClaimPharmacy.PharmacyPhone.length == 10){
                var phonne = this.data.RxClaimPharmacy.PharmacyPhone.split('');
                phonne.splice(0,0,'(');
                phonne.splice(4,0,')');
                phonne.splice(8,0,'-');
                return phonne.join('');
            }else{
                return null;
            }
    }
    hideError() {
        this.showError = false;
        this.displayErrorMessage = false;
        this.displayNoDataMessage = false;
    }
    navigateToNova(){
        
        window.open('https:///www.salesforce.com');
    }
}