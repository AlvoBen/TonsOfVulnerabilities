/*
LWC Name
: PharmacyPrescriptionsDetailHum.js
Function        : LWC to display pharmacyPrescriptiondeatail.

Modification Log:
* Developer Name                  Date                         Description
*
* Swapnali Sonawane               01/20/2023                   US- 4146078 Mail Order Pharmacy- Address/Close the gaps identified during user testing. - 1
****************************************************************************************************************************/
import { LightningElement, api, track } from 'lwc';

export default class PharmacyPrescriptionDetailHum extends LightningElement 
{
    @track detail;
    @track drugName;
    @api prckey;
    @track isEnable;
	@api icon;
    @track addOrderEnable;

    @api getPriscriptionDetails(prsdetail){  
       
        this.detail = prsdetail.prescriptions;  
        let disDrug = this.detail.DispensedDrug;
        
        this.isEnable = prsdetail.OrderEligible;
        this.addOrderEnable = !this.isEnable;

        const drugArr = disDrug.split(" ");
        this.drugName = drugArr[0];
      }  

    connectedCallback() { 

        this.addOrderEnable = !this.isEnable;
    }

    handleCloseClick(evt){        
        const event = new CustomEvent('cardclose', {
            detail:{ selectedKey:this.prckey}
        });
        // Fire the event from c-list
        this.dispatchEvent(event);
    }

    handleAddOrderClick(){
        this.addtocart = true;
       
        const addOrderEvent = new CustomEvent('addorder', {
            detail:{ prescriptioncolor : this.icon.icontype,
            prescriptionnumber : this.detail.RXNumber,
            addedprescription : this.detail
        
        }
        });   
        this.dispatchEvent(addOrderEvent);
        this.popOver = false;
    }

}