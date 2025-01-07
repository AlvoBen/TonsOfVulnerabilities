import { LightningElement, api, wire } from 'lwc';
export default class PharmacyHpieOrderCardHum extends LightningElement {
    @api item;

    handleViewRX() {
        this.dispatchEvent(new CustomEvent("viewrx", {
            detail: {
                orderId: this.item.orderId
            }
        }));
    }

    onOrderIdClick(event) {
        this.dispatchEvent(new CustomEvent('orderidclickselect', {
            detail: {
                OrderId: this.item.orderId
            }
        }));
    }
}