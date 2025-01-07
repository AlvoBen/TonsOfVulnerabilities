/*
Function        : LWC to display demographics details .

Modification Log:
* Developer Name                  Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Nirmal Garg                    08/25/2023                    Initial version
* Swapnali Sonawane              10/23/2023                    US - 5058187 Pharmacy Edit Order
* Jonathan Dickinson             02/29/2024                    User Story 4835317: T1PRJ0865978 - MF 27481 - C12; Mail Order Management; Pharmacy - Mail Order Pharmacy page gap- Member Consent (Lightning)
* Jonathan Dickinson             03/07/2024                    User Story 5394128: T1PRJ1295995 - (T1PRJ0870026) - MF 29256 - Mail Order Pharmacy- Order summary : HPIE updates for split order
*****************************************************************************************************************************/
import { LightningElement, api, track } from 'lwc';
export default class PharmacyHpieOrderLinesHum extends LightningElement {

    @api orderprescription;
    @api payer;
    @api orderparentnode;
    @track orderLineItems = [];
    @api calledFromEditOrder;
    @api memberConsent = false;
    @api isMemberConsentQueue = false;
    @api isMemberConsentRequired = false;

    connectedCallback() {
        this.prepareOrderLine();
    }

    prepareOrderLine() {
        if (this.orderparentnode && this.orderparentnode?.orderPrescriptions
            && Array.isArray(this.orderparentnode?.orderPrescriptions)
            && this.orderparentnode?.orderPrescriptions?.length > 0) {
            let hasSplitFromOrder = false;
            let hasSplitToOrder = false;
            this.orderparentnode?.orderPrescriptions?.forEach(item => {
                let lineItem = {
                    copayAmount: this.orderparentnode?.copayAmount ?? 0,
                    taxAmount: this.orderparentnode?.taxAmount ?? 0,
                    dispensedDrug: item?.dispensedDrug ?? '',
                    writtenDrug: item?.writtenDrug ?? '',
                    quantity: item?.quantity ?? 0,
                    status: item?.status ?? '',
                    showSplitFromOrder: !hasSplitFromOrder && item?.changedOrder?.z0type === 'P' ? true : false,
                    showSplitToOrder: !hasSplitToOrder && item?.changedOrder?.z0type === 'C' ? true : false,
                    splitOrderId:  item?.changedOrder?.orderId ?? '',
                    key: item?.key ?? '',
                    daysSupply: item?.daysSupply ?? '',
                    icon: item?.icon ?? null,
                    refillsRemaining: item?.refillsRemaining ?? '',
                    copayConsentGiven : item?.copayConsentGiven ?? false,
                    rxConsent: item?.rxConsent ?? ''
                }

                if (!hasSplitFromOrder && lineItem.showSplitFromOrder) {
                    hasSplitFromOrder = true;
                }
                if (!hasSplitToOrder && lineItem.showSplitToOrder) {
                    hasSplitToOrder = true;
                }

                this.orderLineItems.push(lineItem);
            })
        }
    }

    splitOrderFunctionality(event){
           
        this.dispatchEvent(new CustomEvent('splitorderclick',{
            detail : {
                OrderNumber : event.target.dataset.id 
            }
        }));
    }

    handleDeleteScriptKey(event) {			
        this.orderLineItems = this.orderLineItems.filter(o => o.key !== event.detail.deletescriptkey);
        this.dispatchEvent(new CustomEvent('deletescriptkeyfromlist', {
            detail: {
                deletescriptkey: event.detail.deletescriptkey,
                copayConsentGiven : this.orderLineItems.copayConsentGiven
            }
        }));
    }

    handleConsentGiven(event) {
        this.dispatchEvent(new CustomEvent('consentgiven', {
            detail: Object.assign({}, event.detail)
        }));
    }
}