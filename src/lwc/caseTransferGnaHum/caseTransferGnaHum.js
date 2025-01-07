/*******************************************************************************************************************************
LWC JS Name : CaseTrasnferGnAHum.js
Function    : This JS serves as helper to CaseTrasnferGnAHum.html 

Modification Log: 
Developer Name           Code Review                      Date                         Description
*--------------------------------------------------------------------------------------------------------------------------------
* Ankima Srivastava                                      03/01/2022                   initial version
* Krishna Teja s                                         09/09/2022                   DF-5965 Fix
* Pooja Kumbhar											 02/03/2023		              US 4180008 - T1PRJ0170850- Lightning- Case management- Change Case Owner Missing Functionalities					
*********************************************************************************************************************************/
import { LightningElement, track, api, wire } from "lwc";
import { getGnALayout } from "./layoutConfig";
import caseValidations from "c/caseValidationsHum";

export default class CaseInformationComponentHum extends caseValidations {
  @api  oCaseObject; //passed from caseTransferContainer 
  @track case = {};
  
  connectedCallback() {
     this.preFillValues();
  }

  
  /**
   * Method Name: preFillValues
   * Function: populate value for prefill fields, parameters are sent from caseTransferContainer
   */
  preFillValues() {
    let jsonModel = getGnALayout(this.oCaseObject.Id, this.oCaseObject.RecordType.Name,this.oCaseObject.Interacting_About_Type__c, this.oCaseObject.Origin, this.oCaseObject.Type);
    let GnAModel = jsonModel ? JSON.parse(JSON.stringify(jsonModel)) : [];
    if(GnAModel.length > 0){
        this.GnAForm = GnAModel;
    }else{
      this.GnAForm = null;
    }
  }
  /**
   * Method Name: saveGandA
   * Function: this method is to recieve the input values and return it to the calling cmp -caseTransferContainer
   */
  @api saveGandA() {
    /*Validation code start*/
    let recType=this.oCaseObject.RecordType.Name;
    if((recType==='Provider Case') || (recType === 'Member Case' ) || (recType === 'Medicare Case' && this.oCaseObject.Interacting_About_Type__c === 'Member')){
		if (!(this.handleRequiredFieldsValidation({ "lightning-input-field": "fieldName" }, ['G_A_Rights_Given__c', 'Complaint__c']))) {
				console.log('return VALIDATION_FAILED');
                return 'VALIDATION_FAILED';
		}
				
      //complaint reason must have a value if complaint is yes or related to yes
       let fldSelecotr = this.checkNullValues(
        { "lightning-input-field": "fieldName" },
        ["Complaint__c","Complaint_Reason__c"]
      );
      if (
          (fldSelecotr[0].value === "Yes" ||
              fldSelecotr[0].value === "Yes - Medicaid" ||
              fldSelecotr[0].value === "Yes - Medicare Part C" ||
              fldSelecotr[0].value === "Yes - Medicare Part D")  &&
          (fldSelecotr[1].value == "" || fldSelecotr[1].value === "--None--") 
      ){
        return 'COMPLAINT_REASON_VALIDATION_FAILED';
      } 

      //With Complaint and Complaint Reason, Complaint type should be there
      let mamplanProduct = this.oCaseObject?.Member_Plan_Id__r?.Product__c;
      let fldSelectorProduct = this.checkNullValues(
        { "lightning-input-field": "fieldName" },
        ["Complaint__c","Complaint_Reason__c","Complaint_Type__c"]
      );
      if (
          (fldSelectorProduct[0].value != "" && fldSelectorProduct[0].value != "--None--" &&
          fldSelectorProduct[1].value != "" && fldSelectorProduct[1].value != "--None--" ) &&
          mamplanProduct &&
          mamplanProduct === "MED" &&
          (fldSelectorProduct[2].value === "" || fldSelectorProduct[2].value === "--None--")
      ) {
        return 'COMPLAINT_TYPE_VALIDATION_FAILED';    
      }
      //G&A reason is required if G&A Rights Given is selected as Yes
      let fldSelectorGnA = this.checkNullValues(
        { "lightning-input-field": "fieldName" },
        ["G_A_Rights_Given__c","G_A_Reason__c"]
      );
      if (
          (fldSelectorGnA[0].value === "Yes" && (fldSelectorGnA[1].value === "" ||
           fldSelectorGnA[1].value === "--None--" ))
      ) {
        return 'GnA_REASON_VALIDATION_FAILED';    
      }
      //The case can't contain both Complaint and G&A Rights Given, if yes for both is selected
      if(recType === 'Member Case'){
        let fldSelectorGnAComplaint = this.checkNullValues(
          { "lightning-input-field": "fieldName" },
          ["G_A_Rights_Given__c","Complaint__c"]
        );
        if (
            (fldSelectorGnAComplaint[0].value === "Yes" &&
            (fldSelectorGnAComplaint[1].value === "Yes" ||
             fldSelectorGnAComplaint[1].value === "Yes - Medicare Part C" ||
             fldSelectorGnAComplaint[1].value === "Yes - Medicare Part D" ||
             fldSelectorGnAComplaint[1].value === "Yes - Medicaid"))
        ) {
          return 'GnA_RIGHT_AND_COMPLAINT_VALIDATION_FAILED';    
        }
      }
      //Complaint option selected should match the plan associated to the case
      let memberPlanAssociated = this.oCaseObject.Member_Plan_Id__r;
      let memProductTypeCode = this.oCaseObject?.Member_Plan_Id__r?.Product_Type_Code__c ;
      let memPlanPolicyMajorLob = this.oCaseObject?.Member_Plan_Id__r?.Plan?.Product__r?.Major_LOB__c;
      let fldSelectorComplaint = this.checkNullValues(
        { "lightning-input-field": "fieldName" },
        ["Complaint__c"]
      );
      if (
        // (memberPlanAssociated != undefined && memberPlanAssociated!= '') &&
        memberPlanAssociated &&
        (((fldSelectorComplaint[0].value == "Yes" ||
        fldSelectorComplaint[0].value == "Yes - Medicaid") &&
            memProductTypeCode == "MAPD") ||
            ((fldSelectorComplaint[0].value == "Yes" ||
            fldSelectorComplaint[0].value == "Yes - Medicaid" ||
            fldSelectorComplaint[0].value == "Yes - Medicare Part C") &&
                memProductTypeCode == "PDP") ||
            ((fldSelectorComplaint[0].value == "Yes" ||
            fldSelectorComplaint[0].value == "Yes - Medicaid" ||
            fldSelectorComplaint[0].value == "Yes - Medicare Part D") &&
                memProductTypeCode == "MA") ||
            ((fldSelectorComplaint[0].value == "Yes" ||
            fldSelectorComplaint[0].value == "Yes - Medicaid") &&
                (memProductTypeCode == "" ||
                memProductTypeCode == null) &&
                [
                    "MEF",
                    "MER",
                    "MEP",
                    "MES",
                    "MGP",
                    "MGR",
                    "MPD",
                    "MRO",
                    "MRP",
                    "PDP"
                ].indexOf(memPlanPolicyMajorLob) !== -1) ||
            ((fldSelectorComplaint[0].value == "Yes" ||
            fldSelectorComplaint[0].value == "Yes - Medicare Part C" ||
            fldSelectorComplaint[0].value == "Yes - Medicare Part D") &&
                (memProductTypeCode == "" ||
                memProductTypeCode == null) &&
                memPlanPolicyMajorLob == "MCD") ||
            ((fldSelectorComplaint[0].value == "Yes - Medicaid" ||
            fldSelectorComplaint[0].value == "Yes - Medicare Part C" ||
            fldSelectorComplaint[0].value == "Yes - Medicare Part D") &&
                !(
                    [
                        "MEF",
                        "MER",
                        "MEP",
                        "MES",
                        "MGP",
                        "MGR",
                        "MPD",
                        "MRO",
                        "MRP",
                        "PDP",
                        "MCD"
                    ].indexOf(memPlanPolicyMajorLob) !== -1
                )))
      ) {
          return 'COMPLAINT_ASSOCIATED_PLAN_VALIDATION_FAILED';
          //return "Complaint option selected does not match the policy associated to the case, please review the Help Hover Over for guidance."
        }	
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
  
	/**
* Method Name: handleRequiredFieldsValidation
* Function: this method is used to show field level error message on required fields
* params : selectorValidate- type of field to validate like combobox or input etc....
           fieldsToValidate - name of field to validate
*/
    handleRequiredFieldsValidation(selectorValidate, fieldsToValidate) {
		const selectorToValidate = this.checkNullValues(selectorValidate, fieldsToValidate);
		return this.updateFieldValidation(selectorToValidate, 'Complete this field ', '', false);
    }
	
  

  
}