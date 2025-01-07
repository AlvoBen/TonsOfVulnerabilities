import { api, LightningElement } from 'lwc';

export default class PharmacyCreateOrderPrescriptionHum extends LightningElement {
    @api
    item;

    get checkrefilllength(){

        return this.item.prescriptions.RefillsRemaining != null 

        && this.item.prescriptions.RefillsRemaining.toString().length >=2 

        ? true : false;

    }

    removePrescription(){
        this.dispatchEvent(new CustomEvent('removepres',{
            detail : {
                RXNumber : this.item.prescriptions.RXNumber
            }
        }))
    }
}