/*
LWC Name        : pharmacyAddPrescriptionPopoverHum.js
Function        : LWC to display prescription popover.
Modification Log:
* Developer Name                  Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
 vishal Shinde                                           02/29/2024               US - 5142800- Mail Order Management - Pharmacy - "Prescriptions & Order Summary" tab - Prescriptions – Create Order
****************************************************************************************************************************/
import { LightningElement, api, track } from 'lwc';
import { getUniqueId } from 'c/crmUtilityHum';
const MINIMUM_WORD_LENGTH = 3;
export default class PharmacyAddPrescriptionPopoverHum extends LightningElement { 

    @api prescriptions;
    @track prescriptionKey;
    @track selectedPrescriptionCount = 0;
    @track finalPrescriptionList = [];
    @track keyword = '';
    @track searchPresciptionList = [];
    connectedCallback() {
        this.performFilter();
    }

    @api
    addPrescription(data) {
        this.prescriptions = data;
        this.performFilter();
    }
    
    createFinalPrscriptionList(data) { 
        this.finalPrescriptionList = [];
        this.finalPrescriptionList = data && Array.isArray(data) && data?.length > 0 ? data : null;
        if (this.finalPrescriptionList && Array.isArray(this.finalPrescriptionList) && this.finalPrescriptionList?.length > 0) {
            this.finalPrescriptionList = this.finalPrescriptionList.map((item) => ({
                ...item,
                id: getUniqueId()
            }))
        }
        this.selectedPrescriptionCount = this.finalPrescriptionList.filter(k => k?.addedToCart === true)?.length ?? 0;
    }

    hidePrescriptionPopover() {
        this.selectedPrescriptions = [];
        this.selectedPrescriptionCount = 0; 
        this.dispatchEvent(new CustomEvent('closeaddprescription'));
    }

    handlePrescriptionAdd(event){
        this.prescriptionKey = JSON.parse(JSON.stringify(event.detail.key));
        this.check = JSON.parse(JSON.stringify(event.detail.check));
        if(this.check){
            this.selectedPrescriptionCount = this.selectedPrescriptionCount + 1;
        }
        else if(!this.check){
            this.selectedPrescriptionCount = this.selectedPrescriptionCount - 1;
        }
        this.handlePassToParentComponent();
    }

    handlePassToParentComponent(){
        this.dispatchEvent(new CustomEvent('passtoparentcomponent', {
          detail: {
              msg: this.prescriptionKey,
              check :this.check 
          }
      }))
  }


    handleKeywordSearch(event) {
        this.keyword = event?.detail?.value ?? '';
        this.performFilter();
    } 

    performFilter() {
        if (this.keyword?.length >= MINIMUM_WORD_LENGTH) {
            let tmp = [];
            this.prescriptions.forEach(a => {
                Object.values(a).forEach(b => {
                    let tempNode = JSON.stringify(b);
                    if (null != tempNode && tempNode.toLowerCase().includes(this.keyword.toLowerCase()) && !tmp.includes(a)) {
                        tmp.push(a);
                    }
                })
            });
            this.createFinalPrscriptionList(tmp?.length > 0 ? tmp : null);
        }
        else {
            this.createFinalPrscriptionList(this.prescriptions);
        }
    }


    clearSearchData() {
        this.keyword = '';
        this.performFilter();
    }

    passToCreateOrder() {
        this.dispatchEvent(new CustomEvent('passcheckforparentcomponent')); 
    }

    get getInnerDivStyle() {
        let maxheight = window.innerHeight - 318;
        return 'max-height:' + maxheight + 'px;';
    }
}
