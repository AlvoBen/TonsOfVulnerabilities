/*
LWC Name: AlertInteractionDetailsHum
Function: This component is used to display alerts on Interaction record page

Modification Log:
* Developer Name                  Date                         Description
* ------------------------------------------------------------------------------------------------------------------------------
* Nirmal Garg                     3/28/2023                   initial version
 
**************************************************************************************************************************** */
import { api, LightningElement } from 'lwc';

export default class AlertInteractionDetailsHum extends LightningElement {
    @api alert;

    get childdata() {
        let childtabledata = '<table class="slds-table slds-no-row-hover">';
        if (this.alert && this.alert?.lstQuestion && this.alert?.lstAnswer) {
            let queData = this.alert?.lstQuestion;
            let ansData = this.alert?.lstAnswer;
            for (let i = 0; i < queData.length; i++) {
                childtabledata = childtabledata + '<tr>';
                childtabledata = childtabledata + '<td style="font-weight: bold;text-align: Right;padding: 5px;width: 20%;">Question ' + (i + 1) + '</td>';
                childtabledata = childtabledata + '<td style="text-align: left;width:25%;padding: 5px;">' + queData[i] + '</td>';
                childtabledata = childtabledata + '<td style="font-weight: bold;text-align: Right;width:7%;padding: 5px;">Answer ' + (i + 1) + '</td>';
                childtabledata = childtabledata + '<td style="text-align: left;width:20%;padding: 5px;">' + ansData[i] + '</td>';
                childtabledata = childtabledata + '</tr>';
            }
        }
        childtabledata += '</table>'
        return childtabledata;
    }
}