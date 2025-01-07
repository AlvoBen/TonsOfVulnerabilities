import { api, LightningElement } from 'lwc';

export default class PharmacyRxPanelItem extends LightningElement {
    @api
    item;

    @api
    expanded = false;
    get expandCollapseIcon(){
        return this.expanded ? 'utility:switch' : 'utility:chevronright';
    }

    toggleDetailSection(){
        this.expanded = !this.expanded;
    }
}