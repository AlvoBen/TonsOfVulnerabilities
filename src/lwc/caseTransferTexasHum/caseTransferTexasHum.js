/*******************************************************************************************************************************
LWC JS Name : CaseTransferTexasHum.js
Function    : This JS serves as helper to CaseTransferTexasHum.html 

Modification Log: 
Developer Name           Code Review                      Date                         Description
*--------------------------------------------------------------------------------------------------------------------------------
* Ankima Srivastava                                      03/01/2022                   initial version
*********************************************************************************************************************************/
import { LightningElement, track, api, wire } from "lwc";
import { getTexasLayout } from "./layoutConfig";
import caseValidations from "c/caseValidationsHum";

export default class CaseInformationComponentHum extends caseValidations {
  @track case = {};
  @api oCaseObject;
  @api oUserGroup;
  
  connectedCallback() {
    this.preFillValues();
  }

  
  /**
   * Method Name: preFillValues
   * Function: populate value for prefill fields, parameters are sent from caseTransferContainer
   */
  preFillValues() {
    let jsonModel = getTexasLayout(this.oCaseObject.Id, this.oCaseObject.RecordType.Name,this.oCaseObject.Interacting_About_Type__c,this.oUserGroup);
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