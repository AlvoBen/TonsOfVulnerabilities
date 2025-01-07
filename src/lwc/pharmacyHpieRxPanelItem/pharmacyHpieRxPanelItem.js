/*
Function        : LWC to display demographics details .

Modification Log:
* Developer Name                  Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Nirmal Garg                    08/25/2023                    Initial version
*****************************************************************************************************************************/
import { api, LightningElement } from 'lwc';
export default class PharmacyHpieRxPanelItem extends LightningElement {
    @api item;

    @api expanded = false;
    get expandCollapseIcon() {
        return this.expanded ? 'utility:switch' : 'utility:chevronright';
    }

    toggleDetailSection() {
        this.expanded = !this.expanded;
    }
}