import { LightningElement, api, track } from 'lwc';
import updateautorefil from '@salesforce/apexContinuation/Pharmacy_LC_HUM.updateautorefil';


export default class PharmacyPrescriptionCardHum extends LightningElement {
    @api item;
    @api selectedindex;
    @api networkId;
    @api addtocart;
    @track selectedPrsList;
    @track isPrescription = false;
    @api isCardClick;
    @api icon;
    @api prckey;
    @api orderEligible;
    @track popOver = false;
    @track openAutoRefillModal = false;
    @track autoFillFlag = false;

    get isAddToCart(){
        return this.addtocart;
        
    }
    
    get checkordereligible(){
       return this.orderEligible;
 

    }

    get AutoRefillEligibilty() {
        if (this.item.AutoRefillEligible == "Yes")
            return true;
    }

    connectedCallback() {
        if (this.item.AutoRefillEnrolled =="Yes")
        {
            this.autoFillFlag = true;
        }
        else{this.autoFillFlag = false;}
    }

    closeModal()
    {
        this.openAutoRefillModal = false;
    }

    toggleAutoRefill()
    {
        this.openAutoRefillModal = true;
    }

    openPopOver(){
        this.isCardClick = false;
        this.popOver = true;
    }
    
    handleCloseClick() {
        this.isPrescription = false;
    }

    handleAddCartClick(){
        this.addtocart = true;
       
        const addCartEvent = new CustomEvent('addcart', {
            detail:{ prescriptioncolor : this.icon.icontype,
            prescriptionnumber : this.item.RXNumber,
            addedprescription : this.item
        
        }
        });   
        this.dispatchEvent(addCartEvent);
        this.popOver = false;
    }

    
    handleClosePrescription(evt) {
        this.isPrescription = evt.detail;
    }

    closePopOver()
    {
        this.popOver=false;
    }

    cardClick(event) {

        this.isCardClick = true;
        let eveDetail = { isCardClick: this.isCardClick, selectedCard: this.selectedindex, selectedKey: this.prckey, RXNumber : this.item.RXNumber };

        // Creates the event with the data.
        const selectedEvent = new CustomEvent("cardclickselect", {
            detail: eveDetail
        });

        // Dispatches the event.
        this.dispatchEvent(selectedEvent);
    }

    get getdrugcontent() {
        if (this.item.WrittenDrug) {
            let index = this.item.WrittenDrug.indexOf(" ");
            return index > 0 ? this.item.WrittenDrug.substring(0, index).trim() : '';
        }
        return '';
    }

    get getdrugcontent() {
        if (this.item.WrittenDrug) {
            let index = this.item.WrittenDrug.indexOf(" ");
            return index > 0 ? this.item.WrittenDrug.substring(index).trim() : '';
        }
        return '';
    }

    updateAutoFill()
    {
        let autoFillVal ;
        let scriptKey='';
        this.openAutoRefillModal = false;
        
        if (this.autoFillFlag) {
        this.autoFillFlag = false;
        autoFillVal = false;
        }
        else {
        this.autoFillFlag = true;
        autoFillVal = true;
        }
        if (this.item.RXNumber){ scriptKey= this.item.RXNumber}

        
        updateautorefil({sScriptKey: scriptKey, sAutoRefill: autoFillVal, networkID: this.networkId})
        .then(result => {
            
        if(result == 'success')
          {
              let eveDetail = { selectedCard: this.selectedindex, selectedKey: this.prckey };
              // Creates the event with the data.
              const selectedEvent = new CustomEvent("autorefill", {
                   detail: eveDetail
               });
        
               // Dispatches the event.
               this.dispatchEvent(selectedEvent);
            
            }
        })
        .catch(err => {
            console.log("update auto fill service error - " + err);        
        });
        this.popOver = false;
    }

    handleblur(){
        let maindiv = this.template.querySelector('[data-id="maindiv"]');
        if(maindiv){
            maindiv.classList.remove('selectedcard');
        }
    }

    @api
    displayBorder(RxNumber){
        let maindiv = this.template.querySelector('[data-id="maindiv"]');
        if(RxNumber === this.item.RXNumber){
            maindiv.classList.add('selectedcard');
        }else{
            maindiv.classList.remove('selectedcard');
        }
    }

    get checkrefilllength(){

        return this.item.RefillsRemaining != null 

        && this.item.RefillsRemaining.toString().length >=2 

        ? true : false;

    }
}