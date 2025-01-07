/*******************************************************************************************************************************
Component Name : CreateUnkownGroupHum
Version        : 1.0
Created On     : 6/23/2021
Function       : This component is for creating Unknow Group account functionality on Group Search tab
                 
Modification Log: 
* Version          Developer Name             Code Review              Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------------
  1.0			   Surendra Vemulapalli								06/23/2021					US1464387- Creating Unknown Group Account (Health Cloud Lightning App)
  1.1        Ankit Avula                                            09/01/2021                    US2365934 populate state field with zipcode input
**************************************************************************************************************************************/

import { LightningElement, track, api, wire } from 'lwc';
import getStateValues from '@salesforce/apex/SearchUtilty_H_HUM.getStateValues';
import createUnknowGroup from "@salesforce/apex/CreateUnknowGroup_LC_HUM.createUnknowGroup";
import { ShowToastEvent } from 'lightning/platformShowToastEvent';
import searchStateCodeWS from '@salesforce/apex/StateCodeUtility_WS_HUM.searchStateCode';
import { getLabels } from "c/crmUtilityHum";

export default class CreateUnknownGroupHum extends LightningElement {

    @track accList = [];
    @track modalOpen;
    @track stateOptions = [];
    @track Options = [];
    @track unState = '-None-';
    @track unAccountList = []; 
    @track showerrorstate;
    @track showerrorActName;
    @track unPhone;
    @track labels = getLabels();
    stOptions = [];
      
    isFormValid = true;
    boolIsOnceSubmit = false;

    /**
  * Rest fields after save or cancel clicked
  */
    handleReset() {
      this.formData.unAccountName = '';
      this.formData.unTaxId	= '';
       this.formData.unBillingCity	= '';
      this.formData.unWorkEmail	= '';
      this.formData.unGroupNumber	= '';
      this.formData.unBillingStreet = '';
      this.formData.unZipCode	= '';
      this.formData.unPhone	= '';
      this.formData.unState = '-None-';
      this.showerrorActName = '';
      this.showerrorstate = '';
      this.isFormValid = false;
      this.unState = '';
      this.unPhone = '';
    }

    /**
  * Data to send apex class
  */

    @track formData = {
      unAccountName	:	this.unAccountName,
      unTaxId	:	this.unTaxId,
      unBillingCity	:	this.unBillingCity,
      unWorkEmail	:	this.unWorkEmail,
      unGroupNumber	:	this.unGroupNumber,
      unBillingStreet:	this.unBillingStreet,
      unZipCode	:	this.unZipCode,
      unPhone	:	this.unPhone,
      unState: this.unState,
      };

       
/**
  * Fetch State options on component load
  */
    connectedCallback() {
     getStateValues().then(data => {
        if (data) {
            const sOptions = [];
            for (let key in data) {
              if(key == 'None'){
                  sOptions.push({ label: '-None-', value: data[key] });
              }
              else{
                  sOptions.push({ label: key, value: data[key] });
              }
            }
            this.stateOptions = sOptions;
          }
        }).catch(error => {
            console.log('Error Occured', error);
        });
        //this.handleFieldValidation();
      }
      //Event Handler for State Code field
      stateSelectionHandler(event) {
        if(this.unState != '' && this.unState != undefined){
          this.formData.unState = this.unState;
        }
        else {
          this.formData.unState = event.target.value;
        }
        
        if (this.boolIsOnceSubmit) {
          this.template.querySelector(".statelookup").showValidationError();
        }
      }
     clearStateHandler(event) {
        this.formData.unState = '-None-';
        this.unState = '';
        if (this.boolIsOnceSubmit) {
          this.template.querySelector(".statelookup").showValidationError();
        }
      }

      /**
       * Formating Phone Numbere
      */

       formatPhoneNumber(event) {
        if (event.keyCode === 8 || event.keyCode === 46) { 
          return;                            }
        let oldphNumber = event.target.value;
        let phNumber = oldphNumber.replace(/\D/g,'');
        phNumber = phNumber.replace(/[()-]|[ ]/gi, "");
        const onlyNumber = new RegExp(/^\d+$/);
        if (onlyNumber.test(phNumber)){
           if (phNumber.length > 0 && phNumber.length < 3) {
                phNumber = '(' + phNumber;
            } else if (phNumber.length < 6) {
                phNumber = '(' + phNumber.substring(0, 3) + ') ' + phNumber.substring(3, 6);
            } else if (phNumber.length > 5) {
                phNumber = '(' + phNumber.substring(0, 3) + ') ' + phNumber.substring(3, 6) + '-' + phNumber.substring(6, 10);
            }
            
      }
      event.target.value = phNumber;
      this.formData.unPhone = phNumber;
    }

    /*
    Validate phone number size on change
    */

   validatePhoneNumber(event){
     
    let phoneNumber = this.getFormatedPhoneNum(event.detail.value);
      if ( phoneNumber && phoneNumber.length != 10) 
            {
                 this.updateFieldValidation(event.target, "Phone Number must be 10 digits");
            }
          
            else 
            {
              this.updateFieldValidation(event.target, '');
            }
       }
     
 
       /**
   * Hightlight validation failures
   */
  handleFieldValidation() {
    this.boolIsOnceSubmit = true;
    this.showErrorMsg = false;
    this.isFormValid = true;
    this.template.querySelector(".statelookup").showValidationError();
    let inp = this.template.querySelectorAll(".NameHighlight");
    inp.forEach(function (element) {
      if(element.required = true && 
         (element.value == '' || 
         (element.label =='Tax ID' && element.value==='None')
         )
        )
      {
         element.setCustomValidity("Enter " + element.label );
         this.isFormValid = false;
      }
      else if(element.required = true && (element.label =='Billing State/Province' && element.value==='-None-')){
        element.setCustomValidity("Select value from list");
        this.isFormValid = false;
      } 
      else {
        element.setCustomValidity("");
      
      }
       element.reportValidity();
    },this);

    let patt = this.template.querySelectorAll(".patternvalid");
    patt.forEach(function (item) {
      if(!item.checkValidity()){
            this.isFormValid = false;
      }
    },this);

    let phoneNumber = this.template.querySelector("[data-id='phone']");
          
       if(phoneNumber!==null){
        let phone = this.getFormatedPhoneNum(phoneNumber.value); 
        if(phone !== '')
          {

            if ( phone.length != 10 && phone.length != 0) {
              this.updateFieldValidation(phoneNumber, "Phone Number must be 10 digits");
              this.isFormValid = false;
          }
          else {
              this.updateFieldValidation(phoneNumber, '');
          }
          }
  }
  if(this.formData.unState == '-None-' || this.formData.unState == '' || this.formData.unState == undefined){
    this.isFormValid = false;
  }
} 
  inputChangeHandler(event) {
    let val = event.detail.value;
    let selectedType = event.target.name;
    if (selectedType === "groupName") {
      this.formData.unAccountName = val;
      this.handleFieldValidation();
    } else if (selectedType === "taxId") {
      this.formData.unTaxId = val;
      this.handleFieldValidation();
    } else if (selectedType === "billingCity") {
      this.formData.unBillingCity = val;
    } /*else if (selectedType === "state") {
      this.formData.unState = val;
      this.handleFieldValidation();
    } */else if (selectedType === "workEmail") {
      this.formData.unWorkEmail = val;
    } else if (selectedType === "groupNumber") {
      this.formData.unGroupNumber = val;
    }else if (selectedType === "billingStreet") {
      this.formData.unBillingStreet = val;
    }else if (selectedType === "zipCode") {
      this.formData.unZipCode = event.target.value;
      this.searchStateCode(event.target.value);
    }else if (selectedType === "phone") {
      this.formData.unPhone = val;
      this.validatePhoneNumber(event)


    }
  }
 
/**
  * Capture value from groupSearchForm component
  */
    @api handleModalValueChange(sGroupName,sGroupNumber,sState) {

      if(sGroupName !='' || sGroupName !=null){
        this.unAccountName = sGroupName;
        this.formData.unAccountName = sGroupName;
       }
      
     if(sGroupNumber !='' || sGroupNumber !=null){
      this.unGroupNumber = sGroupNumber;
      this.formData.unGroupNumber = sGroupNumber;
    }

     if(sState !='' || sState !=null){
  
      this.populatestate(sState);
      
     }
        this.modalOpen = true;
     }

    closeModal() {
        // to close modal set isModalOpen tarck value as false
        this.handleReset();
        this.modalOpen = false;
    }

    submitDetails() {
        // to close modal set isModalOpen tarck value as false
        // code to call apex method to create Account record
        
        this.handleFieldValidation();
           
        if (this.isFormValid) {

        createUnknowGroup({ unKnowGroupCreateInputWraper: JSON.stringify(this.formData) }).then(
         (result) => {
            if (result) {
                       
             this.resultsTrue=true;                        
           
             this.accList = result.map(item => ({
              Id: item.Id,
              RecordType: 'Unknown Group',
              Name: item.Name,
              Group_Number__c: item.Group_Number__c,
              Phone: item.Phone,
              BillingState: item.BillingState,
              BillingPostalCode: item.BillingPostalCode,
              BillingStreet: item.BillingStreet
             }));
               
                   const lwcEvent= new CustomEvent('unknowgroupevent', {
                   detail:{resultsTrue:this.resultsTrue, accList : this.accList} 
                  });
                  
                 this.dispatchEvent(lwcEvent)
                }             
               else {
                 this.noRecordlabel = this.labels.memberSearchNoResultsHum;
                  }
          })
            this.closeModal();
          
            this.showToast('Success!', 'Account created successfully', 'Success');
        }
    }
    

/**
  * Populate state based on Zipcode
  */

handleStateCode(event) {
  console.log('handleStateCode', event.target.value);
  this.statecode = event.target.value;
  const stateSelect = this.template.querySelector('.sc');
  if (stateSelect) {
      this.stateOptions.forEach(conValues => {
          if (conValues.value === this.statecode) {
              console.log('handleStateCode', conValues.value === this.statecode);
              console.log('conValues', conValues.label);
               this.unState = conValues.value;
          }
      });
  }
}


populatestate(strstate) {
  if (strstate) {
      this.stOptions.forEach(conValues => {
            if (conValues.value == strstate) {
              this.unState = conValues.label;
              this.formData.unState = conValues.label;
          }
      });
  }
}

 /**
  * Display toast message 
  * when an exception is
  * thrown
  */
  showToast(strTitle, strMessage, strStyle) {
    this.dispatchEvent(new ShowToastEvent({
      title: strTitle,
      message: strMessage,
      variant: strStyle,
      mode:'dismissable'
    }));
  }

  updateFieldValidation(field, message) {
    field.setCustomValidity(message);
    field.reportValidity();
}

/*
* function to remove special characters in phone number
*/
getFormatedPhoneNum(strNumber) {
  if (!strNumber) {
      strNumber = '';
  }
  strNumber = strNumber.replace(/-/g, '');
  strNumber = strNumber.replace(/\D/g, '');
  return strNumber;
}

 /*
  searchStateCode will get the state value and populate the state field with the input as the zip code
 */
searchStateCode(zipCodeValue){
  let remText = zipCodeValue.replace(/\s/g, "");
  let length = remText.length;
  if(length >= 5){
    searchStateCodeWS({ zipCode: zipCodeValue }).then(
      (result) => {
          if (result) {
              this.stateOptions.forEach(conValues => {
                  if (conValues.value === result) {
                      this.unState = conValues.label;
                      this.formData.unState = conValues.label;
                  }
                }); 
          }
      })
  }
}
}