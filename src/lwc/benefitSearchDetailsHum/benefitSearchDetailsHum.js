/*LWC Name        : BenefitSearchDetailsHum.html
Function        : LWC to display Benefit Search Details

Modification Log:
* Developer Name                  Date                         Description
* Nirmal Garg                     03/17/2022                   initial version - US - 3017464
* Aishwarya Pawar                 03/17/2022                   US-3150160 - Add limits
* Swapnali Sonawane               06/05/2023                   US-4588633 Enablement of the medical benefits UI logging - Search
***************************************************************************************************************************/


import { api, LightningElement, track } from 'lwc';
const columns = [{ label: 'Limit Name', style: 'width:13%;font-weight: normal;' }, { label: 'Limit Amount', style: 'width:13%;font-weight: normal;' }, { label: 'Limit Text', style: 'width:13%;font-weight: normal;' }, { label: 'Limit Comments', style: 'width:13%;font-weight: normal;' }, { label: 'Par/Non Par', style: 'width:13%;font-weight: normal;' }];
export default class BenefitSearchDetailsHum extends LightningElement {
    @api item;
    @track columns = columns;
    @track BenefitLimits = [];
    @track displayLimitSection;
    @track showLimitComments = false;
    getBenefitLimitData(){
        if (this.item.benefitLimits.length > 0) {
            this.showLimitComments = true;
            this.item.benefitLimits.forEach((k, index) => {
            let benefitLimit={};
            benefitLimit.index=index;
            benefitLimit.LimitName = k.limitName;
            benefitLimit.LimitAmount = k.limitAmount;
            benefitLimit.LimitText = k.limitText;
            benefitLimit.LimitComments= k.limitComment;
            benefitLimit.ParNonPar= k.Par != '' ? 'Par' : k.Par != '' ? 'Non Par' : k.Networktype.toLowerCase() == 'in' ? 'Par' :  k.Networktype.toLowerCase() == 'out' ? 'Non Par' : '' ;
            this.BenefitLimits.push(benefitLimit);
           
        });
        }
    }
    connectedCallback() {
        this.displayLimitSection = this.item.BenefitType === 'Limit' ? true : false;
        if (this.displayLimitSection) {
            this.getBenefitLimitData();
        }

    }
    get generateLogId() {
        return Math.random().toString(16).slice(2);
    }

    handleLogging(event) {

        //fire event
        const addCartEvent = new CustomEvent('detaillogging', {
            detail: {
                eventdetail: event
            }
        });
        this.dispatchEvent(addCartEvent);
    }

    handleLimitLogging(event) {
        this.getIndex(event.target, event, 'expandedrow');
    }

    getIndex(element, event, classname) {
        let indexValue;
        try {
            if (element != null && element?.classList && element?.classList?.value
                && element?.classList?.value?.includes(classname)) {
                indexValue = element.getAttribute('data-item');

                //fire event
                const addCartEvent = new CustomEvent('limitlogging', {
                    detail: {
                        eventdetail: event,
                        limitsdata: this.BenefitLimits,
                        columns: this.columns,
                        index: indexValue
                    }
                });
                this.dispatchEvent(addCartEvent);
            } else {
                if (element && element?.parentNode) {
                    this.getIndex(element.parentNode, event, classname);
                }
            }
        }
        catch (e) {
            console.log('Error: ' + e);
        }

    }
}