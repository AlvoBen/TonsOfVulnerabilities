import { LightningElement, track, wire, api } from "lwc";
import findRecords from "@salesforce/apex/InteractionController_LC_HUM.findRecords";
import findInteractionRecords from "@salesforce/apex/InteractionController_LC_HUM.findInteractionRecords";
export default class InteractionCmpLookup extends LightningElement {
  @track recordsList;
  @track searchKey = "";
  @api selectedValue;
  @api selectedRecordId;
  @api objectApiName;
  @api iconName;
  @api lookupLabel;
  @api helpMsg;
  @track message;
  @track labelCss = "slds-form-element__label";
  @api interactingWith = false;
  @api clickLookup;
  @api bdisabled;
  lookupStar = "*";
  strString = "";
  strString1 = "";
  @api
  changeMessage(strString) {
    this.removeRecordOnLookup();
  }

  @api
  setIntNumLkpValue(strString, strString1) {
    this.setRecordLkpValues(strString, strString1);
  }

  @api
  setIntWithLkpValue(strString, strString1) {
    this.setRecordLkpValues(strString, strString1);
  }

  @api
  enableDisableElement(bVal) {
    this.bdisabled = bVal;
  }

  setRecordLkpValues(strString, strString1) {
    this.selectedValue = strString;
    this.selectedRecordId = strString1;
  }

  onLeave(event) {
    setTimeout(() => {
      this.searchKey = "";
      this.recordsList = null;
    }, 300);
  }

  onRecordSelection(event) {
    this.selectedRecordId = event.target.dataset.key;
    this.selectedValue = event.target.dataset.name;
    this.searchKey = "";
    this.onSeletedRecordUpdate();
  }

  handleOnKeyUp(event) {
    if(event.code === "Enter") {
      this.handleKeyChange(event);
    }
  }

  handleKeyChange(event) {
    const searchKey = event.target.value;
    this.searchKey = searchKey;
    this.getLookupResult();
  }

  removeRecordOnLookup(event) {
    this.searchKey = "";
    this.selectedValue = null;
    this.selectedRecordId = null;
    this.recordsList = null;
    this.onSeletedRecordUpdate();
  }

  getLookupResult() {
    if (this.objectApiName === "account") {
      findRecords({ searchKey: this.searchKey, objectName: this.objectApiName })
        .then((result) => {
          if (result.length === 0) {
            this.recordsList = [];
            this.message = "No Records Found";
          } else {
            this.recordsList = result;
            this.message = "";
          }
          this.error = undefined;
        })
        .catch((error) => {
          this.error = error;
          this.recordsList = undefined;
        });
    } else if (this.objectApiName === "Interaction__c") {
      findInteractionRecords({
        searchKey: this.searchKey,
        objectName: this.objectApiName
      })
        .then((result) => {
          if (result.length === 0) {
            this.recordsList = [];
            this.message = "No Records Found";
          } else {
            this.recordsList = result;
            this.message = "";
          }
          this.error = undefined;
        })
        .catch((error) => {
          this.error = error;
          this.recordsList = undefined;
        });
    }
  }

  onSeletedRecordUpdate() {
    const passEventr = new CustomEvent("recordselection", {
      detail: {
        selectedRecordId: this.selectedRecordId,
        selectedValue: this.selectedValue
      }
    });
    this.dispatchEvent(passEventr);
  }

  openModal() {
    this.clickLookup = true;
    const passEventr = new CustomEvent("openlookup", {
      detail: {
        clickLookup: this.clickLookup,
        interactingWith: this.interactingWith,
        objectName: this.objectApiName,
        searchKey: this.searchKey
        
      }
    });
    this.dispatchEvent(passEventr);
  }
}