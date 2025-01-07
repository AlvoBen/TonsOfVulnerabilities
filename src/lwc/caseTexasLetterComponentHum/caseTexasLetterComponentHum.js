/*******************************************************************************************************************************
LWC JS Name : CaseTexasLetterComponentHum.js
Function    : This JS serves as helper to caseTexasLetterComponentHum.html 

Modification Log: 
Developer Name           Code Review                      Date                         Description
*--------------------------------------------------------------------------------------------------------------------------------
* Gowthami Thota                                          06/20/2022                   initial version
*********************************************************************************************************************************/
import { LightningElement, track, api, wire } from "lwc";
import { getTexasLayout } from "./layoutConfig";
import caseValidations from "c/caseValidationsHum";

export default class CaseTexasLetterComponentHum extends caseValidations {
  @track case = {};
  @api oCaseObject;
  @api oUserGroup;
  
  connectedCallback() {
    this.preFillValues();
  }

  
  /**
   * Method Name: preFillValues
   * Function: populate value for prefill fields, parameters are sent from closeCaseHum
   */
  preFillValues() {
    let memberPlanAssociated = this.oCaseObject.Member_Plan_Id__c ?this.oCaseObject.Member_Plan_Id__c : null ;
    let memProductCode = memberPlanAssociated ? this.oCaseObject.Member_Plan_Id__r.Product__c : null ;
    let issueState = memberPlanAssociated ? this.oCaseObject.Member_Plan_Id__r.Issue_State__c : null ;
    let jsonModel = getTexasLayout(memberPlanAssociated,memProductCode,issueState,this.oUserGroup);
    let texasModel = jsonModel ? JSON.parse(JSON.stringify(jsonModel)) : [];
    if(texasModel.length > 0){
        this.TexasForm = texasModel;
    }else{
      this.TexasForm = null;
    }
  }
  /**
   * Method Name: saveTexas
   * Function: this method is to recieve the input values and return it to the calling cmp -caseTransferContainer
   */
  @api saveTexas() {
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
}