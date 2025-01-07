/*
Function        : LWC to display demographics details .

Modification Log:
* Developer Name                  Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Nirmal Garg                    08/25/2023                    Initial version
*****************************************************************************************************************************/
import { LightningElement, api, track } from 'lwc';
const INITIAL_LOAD_CARDS = 6;
const MINIMUM_WORD_LENGTH = 3;
export default class PharmacyViewRxPopoverHum extends LightningElement {
    @track countOrders;
    @track totalRXData = [];
    @track filterRXData = [];
    @api prescriptions;
    @track keyword = '';

    @api
    displayPopOver(orderdata) {
        if (this.totalRXData?.findIndex(k => k?.orderId === orderdata?.orderId) < 0) {
            if (orderdata && Object.keys(orderdata)?.length > 0) {
                this.totalRXData.push({
                    orderId: orderdata?.orderId ?? '',
                    items: this.getOrderPrescription(orderdata),
                    expanded: false
                })
            }
            this.totalRXData = this.totalRXData.map((item) => ({
                ...item,
                expanded: item?.orderId === orderdata?.orderId ? true : false
            }))
        }
        this.performSorting();
        this.filterRXData = this.totalRXData;
        this.countOrders = this.filterRXData?.length;
    }

    performSorting() {
        this.totalRXData.sort(function (a, b) {
            return a?.orderId > b?.orderId ? 1 : -1;
        });
    }

    getOrderPrescription(orderdata) {
        let items = [];
        if (orderdata && orderdata?.lines && Array.isArray(orderdata?.lines) && orderdata?.lines?.length > 0) {
            orderdata?.lines.forEach(k => {
                let prescription = this.prescriptions.find(h => h?.key == k?.product?.prescription?.prescriptionKey);
                if (prescription && Object.keys(prescription)?.length > 0) {
                    items.push({
                        quantity: prescription?.quantity ?? '',
                        key: prescription?.key ?? '',
                        daysSupply: prescription?.daysSupply ?? '',
                        refillsRemaining: prescription?.refillsRemaining ?? '',
                        status: prescription?.status?.description ?? '',
                        writtenDrug: prescription?.product?.drug?.label ?? '',
                        dispensedDrug: this.getDispensedDrug(prescription),
                    })
                }
            })
        }
        return items;
    }

    getDispensedDrug(item) {
        return item && Array.isArray(item?.fills) && item?.fills?.length > 0
            ? (item.fills[0]?.itemLabel ?? '') : '';
    }

    closeRXPanel() {
        this.dispatchEvent(new CustomEvent('closerxpanel'));
    }

    handleKeywordSearch(event) {
        this.filterRXData = [];
        let name = event.detail.name;
        let value = event.detail.value;
        this.keyword = value;
        if (value?.length >= MINIMUM_WORD_LENGTH) {
            let filterByText = value;
            let tmp = [];
            for (const [key, value] of Object.entries(this.totalRXData)) {
                console.log(typeof value);
            }
        }
        else {
            this.totalRXData = this.totalRXData.map((item) => ({
                ...item,
                expanded: false
            }))
            this.filterRXData = this.totalRXData;
        }
        this.countOrders = this.filterRXData?.length;
    }

    clearSearchData() {
        this.keyword = '';
        this.filterRXData = this.totalRXData;
        this.performSorting();
        this.countOrders = this.filterRXData?.length;
    }
}