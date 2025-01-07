/*******************************************************************************************************************************
LWC JS Name : CaseGnaComponentHum.js
Function    : This JS serves as helper to CaseGnaComponentHum.html 

Modification Log: 
Developer Name           Code Review                      Date                         Description
*--------------------------------------------------------------------------------------------------------------------------------
* Gowthami Thota                                           06/20/2022                   initial version
*********************************************************************************************************************************/
import { LightningElement, track, api, wire } from "lwc";
import { getGnALayout } from "./layoutConfig";
import caseValidations from "c/caseValidationsHum";

export default class CaseGnaComponentHum extends caseValidations {
  @api  oCaseObject; //passed from closeCaseHum 
  @track case = {};
  @track errorHighlights;
  @track GnAForm = {}
  
  connectedCallback() {
     this.preFillValues();
  }

  
  /**
   * Method Name: preFillValues
   * Function: populate value for prefill fields, parameters are sent from closeCaseHum
   */
  preFillValues() {
    let jsonModel = getGnALayout(this.oCaseObject.Id, this.oCaseObject.RecordType.Name,this.oCaseObject.Interacting_About_Type__c);
    let GnAModel = jsonModel ? JSON.parse(JSON.stringify(jsonModel)) : [];
    if(GnAModel.length > 0){
        this.GnAForm = GnAModel;
    }else{
      this.GnAForm = null;
    }
  }
  /**
   * Method Name: saveGandA
   * Function: this method is to recieve the input values and return it to the calling cmp -closeCaseHum
   */
  @api saveGandA() {
    let recType=this.oCaseObject.RecordType.Name;
    let GnARights = this.template.querySelector(`[data-id='ga-rights']`).value;
    let GnAReason = this.template.querySelector(`[data-id='ga-reason']`).value;
    let complaint = this.template.querySelector(`[data-id='complaint']`).value;
    let complaintReason = this.template.querySelector(`[data-id='complaint-reason']`).value;
    let complaintType = this.template.querySelector(`[data-id='complaint-type']`).value;
  
    if(GnARights === 'Yes' && (GnAReason === '' || GnAReason === "--None--")){
      
      this.GnAForm[0]['fields'][2].errorHighlights = 'error-highlight';
      this.GnAForm[0]['fields'][2].ShowErrorMsg = true;
    }
    else if((complaint === 'Yes'  ||complaint === "Yes - Medicaid" || complaint === "Yes - Medicare Part C" ||
            complaint === "Yes - Medicare Part D") && (complaintReason === '' || complaintReason === '--None--')) {
     
      this.GnAForm[0]['fields'][3].errorHighlights = 'error-highlight';
      this.GnAForm[0]['fields'][3].ShowErrorMsg = true;
    }
    else if((complaint === 'Yes'  ||complaint === "Yes - Medicaid" || complaint === "Yes - Medicare Part C" ||
            complaint === "Yes - Medicare Part D") && (complaintType === '' || complaintType === '--None--')) {
     
      this.GnAForm[0]['fields'][5].errorHighlights = 'error-highlight';
      this.GnAForm[0]['fields'][5].ShowErrorMsg = true;
    }


  /*Validation code starts here */
      //With Complaint and Complaint Reason, Complaint type should be filled
      let fldSelectorComplaint = this.checkNullValues(
        { "lightning-input-field": "fieldName" },
        ["Complaint__c","Complaint_Reason__c","Complaint_Type__c"]
      );
      //G&A reason is required if G&A Rights Given is selected as Yes
      let fldSelectorGnA = this.checkNullValues(
        { "lightning-input-field": "fieldName" },
        ["G_A_Rights_Given__c","G_A_Reason__c"]
      );

      // this if is for handle required field values
      if (!(this.handleRequiredFieldsValidation({ "lightning-input-field": "fieldName" }, 
                                                  ["G_A_Rights_Given__c","Complaint__c"]))) {
        return 'REQUIRED_FIELD_VALIDATION_FAILED';
      }
        
      else if (
        (fldSelectorGnA[0].value === "Yes" && (fldSelectorGnA[1].value === "" ||
          fldSelectorGnA[1].value === "--None--" ))
        ) {
      return 'GnA_REASON_VALIDATION_FAILED'; 
      }
      else if(
      (fldSelectorComplaint[0].value === "No")) {
        const recordData = this.template.querySelectorAll(
          "lightning-input-field"
        );
        
        if (recordData) {
          recordData.forEach((field) => {
            this.case[field.fieldName] = field.value;
          });
        }
        return this.case;
      } 
      else if (
        (fldSelectorComplaint[0].value === "Yes" ||
        fldSelectorComplaint[0].value === "Yes - Medicaid" ||
        fldSelectorComplaint[0].value === "Yes - Medicare Part C" ||
        fldSelectorComplaint[0].value === "Yes - Medicare Part D")  &&
        (fldSelectorComplaint[1].value === "" || fldSelectorComplaint[1].value === "--None--") ||
        (fldSelectorComplaint[2].value === "" || fldSelectorComplaint[2].value === "--None--")
        ) {
      return 'COMPLAINT_TYPE_REASON_VALIDATION_FAILED';    
      }
/*Validation code ends */ 

    const recordData = this.template.querySelectorAll(
        "lightning-input-field"
      );
      
      if (recordData) {
        recordData.forEach((field) => {
          this.case[field.fieldName] = field.value;
        });
      }
      return this.case;
      
  }

  handleChange(event){
    let GnARights = this.template.querySelector(`[data-id='ga-rights']`).value;
    let GnAReason = this.template.querySelector(`[data-id='ga-reason']`).value;
    let complaint = this.template.querySelector(`[data-id='complaint']`).value;
    let complaintReason = this.template.querySelector(`[data-id='complaint-reason']`).value;
    let complaintType = this.template.querySelector(`[data-id='complaint-type']`).value;

    let fieldSelctor = this.GnAForm;
    if(GnARights === 'Yes' && GnAReason !== "--None--" && complaintReason == ''){
     
      fieldSelctor[0]['fields'][2].errorHighlights = '';
      fieldSelctor[0]['fields'][2].ShowErrorMsg = false; 
      this.GnAForm = [...fieldSelctor];
    }
    else if((complaint === 'Yes'  ||complaint === "Yes - Medicaid" || complaint === "Yes - Medicare Part C" ||
        complaint === "Yes - Medicare Part D") && (complaintReason !== '' || complaintReason !== '--None--') && (complaintType === '' || complaintType === '--None--')){
      
      fieldSelctor[0]['fields'][3].errorHighlights = '';
      fieldSelctor[0]['fields'][3].ShowErrorMsg = false;
      this.GnAForm = [...fieldSelctor];
    }
    else if((complaint === 'Yes'  ||complaint === "Yes - Medicaid" || complaint === "Yes - Medicare Part C" ||
        complaint === "Yes - Medicare Part D") && (complaintType !== '' || complaintType !== '--None--')){
     
     fieldSelctor[0]['fields'][5].errorHighlights = '';
     fieldSelctor[0]['fields'][5].ShowErrorMsg = false; 
      this.GnAForm = [...fieldSelctor];
    }
    else if(complaint === 'No'){
      fieldSelctor[0]['fields'][3].errorHighlights = '';
      fieldSelctor[0]['fields'][3].ShowErrorMsg = false; 
      fieldSelctor[0]['fields'][5].errorHighlights = '';
      fieldSelctor[0]['fields'][5].ShowErrorMsg = false; 
      this.GnAForm = [...fieldSelctor];
    }
    if(GnARights === 'No'){
      fieldSelctor[0]['fields'][2].errorHighlights = '';
      fieldSelctor[0]['fields'][2].ShowErrorMsg = false; 
      this.GnAForm = [...fieldSelctor];
    }
  }
  
  handleRequiredFieldsValidation(selectorValidate, fieldsToValidate) {
    const selectorToValidate = this.checkNullValues(selectorValidate, fieldsToValidate);
    return this.updateFieldValidation(selectorToValidate, 'Complete this field', '', false);
  } 
}