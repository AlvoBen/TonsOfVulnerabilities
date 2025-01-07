/*
LWC Name        : pharmacyUSPSAddressVerification.js
Function        : LWC to display USPS address

Modification Log:
* Developer Name                  Date                         Description
* Swapnali Sonawane               11/01/2022                   US- 3729809 Migration of the UI enhancements in the addresses section.
*************************************************************************************************************************** */
import { LightningElement,api } from 'lwc';
import uspsAddressNotConfirmedMsg from '@salesforce/label/c.uspsAddressNotConfirmedMsg';
export default class PharmacyUSPSAddressVerification extends LightningElement 
{
    @api valaddr;
    @api addrtoupdate;
    @api address;
    @api showerr;
    get addressOptions() {
        return [
            { label: 'Address Line1', value: 'Address' },
        ];
    }
    
    get USPSAddrsOptions() {
        return [
            { label: 'Address Line1', value: 'USPSAdd' },
        ];
    }
    value='USPSAdd';
    label = {
        uspsAddressNotConfirmedMsg
    }
    connectedCallback(){
        if (this.valaddr != undefined && this.valaddr.AddressLine1 != null)
         {
             this.value='USPSAdd';
         }else{this.value='Address';}
    }
    handleCancelUSPS(){
        this.dispatchEvent(new CustomEvent('cancel'));
    }
        
    handleSave(){
        this.dispatchEvent(new CustomEvent('savedata'));
        this.address = false;
        this.showerr = false;
    }
    handleOptionChange(event)
    {
        this.selectedAddOption = event.detail.value;
        if (this.selectedAddOption=='USPSAdd')
        {
            this.selectedAddress = this.valaddr;
        }else{this.selectedAddress = this.addrtoupdate;}

        this.dispatchEvent(new CustomEvent('shippingaddress',{
            detail : {
                'ShippingAddress' : this.selectedAddress
            }
        }));
    }
}