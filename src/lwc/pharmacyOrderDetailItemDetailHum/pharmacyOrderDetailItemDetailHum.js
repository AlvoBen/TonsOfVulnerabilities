/*
LWC Name        : pharmacyOrderDetailItemDetailHum.js
Function        : LWC to display pharmacy order edit item details.

Modification Log:
* Developer Name                  Date                         Description
*
* Abhishek Mangutkar              03/09/2022                   US - 3139633
****************************************************************************************************************************/

import { api, LightningElement } from 'lwc';
import pharmacyOrderDetailEditItemDetailHum from './pharmacyOrderDetailEditItemDetailHum.html'
import pharmacyOrderDetailItemDetailHum from './pharmacyOrderDetailItemDetailHum.html'

export default class PharmacyOrderDetailItemDetailHum extends LightningElement {
    @api
    item;

    @api
    payer;

    @api calledFromEditOrder = false;

    render(){
        if(this.calledFromEditOrder){
            return pharmacyOrderDetailEditItemDetailHum;
        }
        else{
            return pharmacyOrderDetailItemDetailHum;
        }
    }

    get checkrefilllength(){

        return this.item.RefillsRemaining != null 

        && this.item.RefillsRemaining.toString().length >=2 

        ? true : false;

    }

    deletePrescriptionItem(){
        console.log(JSON.stringify(this.item));
        console.log('Delete - script key - ' + this.item.ScriptKey);
        this.dispatchEvent(new CustomEvent('deletescriptkey', {
            detail: {
                deletescriptkey: this.item.ScriptKey
            }
          }));
    }
}