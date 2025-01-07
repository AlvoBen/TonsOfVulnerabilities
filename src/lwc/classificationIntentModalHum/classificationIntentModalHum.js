/*******************************************************************************************************************************
LWC JS Name : classificationIntentModalHum.js
Function    : This JS serves as helper to classificationIntentModalHum.html 

Modification Log: 
Developer Name           Code Review                      Date                         Description
*--------------------------------------------------------------------------------------------------------------------------------
* Gowthami Thota                                           06/20/2022                  initial version
* Kalyani Pachpol                                          02/17/2023                  US-4256639
* Kalyani Pachpol                                          05/04/2023                  US-4486808
*********************************************************************************************************************************/
import { LightningElement, track, api} from "lwc";
import { getCTCILayout } from "./layoutConfig";
import populateCaseData from "@salesforce/apex/CaseDetails_LC_Hum.populateCaseData";
import { toastMsge} from "c/crmUtilityHum";
import caseValidations from "c/caseValidationsHum";
import getRecordTypeChange from '@salesforce/apex/CaseDetails_LC_Hum.getRecordTypeChange';

export default class classificationIntentModalHum extends caseValidations {
    @api recordId;
    @api pageRefData; //this variable is used for getting URL attributes that coming from other pages
    @track resultData;
    @track caseForm;
    @track case = {};
    @api oCaseObject;

    connectedCallback() {
        this.caseDetaildata();
    }

    /**
     * Method Name : caseDetaildata
     * Function: get data from apex call and prefill default fields
     */
    async caseDetaildata() {
        try {
         const pageName = (this.pageRefData && this.pageRefData.hasOwnProperty('pageName')) ? this.pageRefData.pageName : '';
         let inputParams = { callerPageName: pageName };         
         const result = await populateCaseData({ objectID: this.recordId, params: JSON.stringify(inputParams) }); //, isPharmacyLogging: true         
         this.resultData = result;
		 let logcodedetail=this.resultData.ctciModel.ctciObj.Humana_Pharmacy_Log_Code__c;
         if (result) {
          this.preFillValues(result);
       }
	   const hplogcodeevent = new CustomEvent("hplogcodedetail", {
        detail: {
          showlogcodes: logcodedetail
        }
      });
	  this.dispatchEvent(hplogcodeevent);

    } catch (error) {
      console.log("Error---> ", error);
      let message = error.body.message
        .replace(/&amp;/g, "&")
        .replace(/&quot;/g, '"');
      toastMsge("", message, "error", "pester");
    }
  }
    /**
     * Method Name: preFillValues
     * @param {*} result is the response coming from apex
     * Function: populate value for prefill fields, this method is being called from caseDetaildata
     */
     async preFillValues(result) { 
      const recordTypeName = result.prefillValues.caseRecordTypeName;
      let jsonModel = getCTCILayout(this.recordId, recordTypeName);
      let caseModel = jsonModel ? JSON.parse(JSON.stringify(jsonModel)) : [];
      caseModel.forEach((item) => {
      item.fields.forEach((fl) => {
        const classificatioToIntent =
          this.resultData.ctciModel.classificationToIntent;
        if (fl.picklist) {
          fl.options = result[fl.source][fl.mapping]
            ? [
              ...[{ label: "--None--", value: "--None--" }],
              ...result[fl.source][fl.mapping]
            ]
            : [];
          fl.readOnly = !(fl.options.length > 1);

          //below if is to check if intent picklist has values. if so autopopulate it on the case edit screen.
          if (
            result[fl.source][fl.selectedValue] &&
            fl.selectedValue === "intentName"
          ) {
            fl.options = result[fl.source].classificationName
              ? [
                ...[{ label: "--None--", value: "--None--" }],
                ...classificatioToIntent[result[fl.source].classificationName]
              ]
              : [];
            fl.readOnly = !(fl.options.length > 1);
            const intentMap =
              this.resultData.ctciModel.mpOfclassificationIntentToCTCIId[
              result[fl.source].classificationName
              ];
            fl.value = intentMap[result[fl.source][fl.selectedValue]];
          }
          // below if is to check if classification picklist has values. if so autopopulate it on case edit screen.
          if (result[fl.source][fl.selectedValue]) {
            fl.value = result[fl.source][fl.selectedValue];
          } else {
            fl.value = "--None--";
          }
        } 
      });
    });
   
     this.caseForm = (caseModel.length > 0) ? caseModel : null;
    }

    /**
     * Method: handleChange
     * @param {*} event 
     * Function: this method is used to manupulate picklist value and called from onchage attribut in combobox. 
     */
    handleChange(event) {
        if (event.target.name === "classificationToIntentValues") {
            this.handleClassificationChange(event);
        } else if (event.target.name === "CTCI_List__c") {
            this.handleIntentChange(event);
        }
    }

    /**
     * Method Name : handleClassificationChange
     * @param {*} event 
     * Function : this function is being called from handlechange function on the change of classification value
     */
    handleClassificationChange(event) {
        // below line is used to assign classification id to case object (this.case)
        this.case.Classification_Id__c = event.detail.value;
        const classificatioToIntent = this.resultData.ctciModel.classificationToIntent;
        const intentField = this.template.querySelector('[data-id="case-intent"]');
        if (classificatioToIntent[event.detail.value]) {
            intentField.options = [
            ...[{ label: "--None--", value: "--None--" }],
            ...classificatioToIntent[event.detail.value]
           ];
        }
        intentField.value = "--None--";
        intentField.disabled = classificatioToIntent[event.detail.value]
      ? false
      : true;
    }

    /**
     * Method Name : handleIntentChange 
     * @param {*} event 
     * Function : this function is being called from handlechange function on the change of Intent value
     */
    async handleIntentChange(event) {
        const classWithIntentCTCI =
        this.resultData.ctciModel.mpOfclassificationIntentToCTCIId;
        // classWithIntentCTCI ==> this variable is to store map<classification Id, map<Intent Id, CTCI Id>>
    const classification = this.template.querySelector(
      '[data-id="case-classification"]'
    );
    const intentWithCTCI = classWithIntentCTCI[classification.value];
    this.case[event.target.name] = intentWithCTCI[event.detail.value]
      ? intentWithCTCI[event.detail.value]
      : null;
    // below line is used to assign Intent id to case object (this.case)
    this.case.Intent_Id__c = event.detail.value;
	
    await this.handleRecordtypeChange(classification.value,event.detail.value);
    this.handleLogCodeVisibility(classification.value, event.detail.value, false);
  }

    /**
     * Method Name : onSave
     * Function : this function is use to send the case data in apex to create the case record
     */
    @api
     fetchIntent(){
         return this.template.querySelector('[data-id="case-intent"]'); // get logcode in JS
            
     }
    @api 
    saveCTCI() {
        try {
            const recordData = this.template.querySelectorAll("lightning-combobox");
            const intentField = this.template.querySelector('[data-id="case-intent"]');
            const classification = this.template.querySelector('[data-id="case-classification"]');
            if(classification.value == '--None--' || classification.value == '' || classification.value == null){
              classification.setCustomValidity('Complete this field');
              classification.reportValidity();
              return 'CTCI_REQUIRED_FIELD_VALIDATION_FAILED';
            }
            else{
              classification.setCustomValidity(""); 
              classification.reportValidity(); 
            }

            if(intentField.value === '--None--' || intentField.value === '' || intentField.value === null){
              intentField.setCustomValidity('Complete this field');
              intentField.reportValidity();
              return 'CTCI_REQUIRED_FIELD_VALIDATION_FAILED';
            }
            else {
              intentField.setCustomValidity(""); 
              intentField.reportValidity();
               
            }

            if (recordData) {
                recordData.forEach((field) => {
                    this.case[field.name] = field.value;
                });
            }
            return this.case;
        } catch (error) {
            console.log("error in onsaveCTCI--- ", error);
        }
    }
	
	handleLogCodeVisibility(classification, intentId, onLoadEdit) {
      try {
        const classWithIntentCTCI =
          this.resultData.ctciModel.mpOfclassificationIntentToCTCIId;
        const intentWithCTCI = classWithIntentCTCI[classification];
        const serviceModelTypes = ['Insurance/Plan', 'Humana Pharmacy'];
        const caseClassificationTypes = ['Calls (RSO)', 'HP Clinical Services', 'HP Finance Ops', 'HP RxE Calls', 'HP Specialty Calls', 'Humana Pharmacy Calls', 'Humana Pharmacy Web Chat'];
        const mapOfCTCIIdToCTCI = this.resultData.ctciModel.mapOfCTCIIdToCTCI;
        const ctciId = intentWithCTCI[intentId];
        let ctciObj = {};
        if (!onLoadEdit) {
          let ctciList = Object.values(mapOfCTCIIdToCTCI);
          let filteredCTCI = ctciList.filter((item) => {
            return (
              item.Classification__c === classification &&
              item.Intent__c === intentId
            );
          });
          
                 ctciObj = filteredCTCI[0];
        } else {
          ctciObj = this.resultData.ctciModel.ctciObj;
        }

        let caseClassificationType = this.resultData.objCase.Classification_Type__c;
        const serviceModelType = ctciObj && ctciObj.hasOwnProperty('Service_Model_Type__c') ? ctciObj.Service_Model_Type__c : null;
        let pickListVisible = (this.resultData.prefillValues.caseRecordTypeName.search("HP") !== -1) && (serviceModelTypes.includes(serviceModelType) && caseClassificationTypes.includes(caseClassificationType));
        let logcode ;
        let updatedRecordType = this.resultData?.prefillValues?.caseRecordTypeName?? null;
       if(pickListVisible)
       {
       logcode=ctciObj.Humana_Pharmacy_Log_Code__c;
       }
       else{
        logcode=null;
       }
        const hpcomment = new CustomEvent("hpcommentvisibility", {
          detail: {
            showhpcomment: pickListVisible,
			      showlogcode: logcode,
            showrecordtype: updatedRecordType
          }
        });
        this.dispatchEvent(hpcomment);
        }catch (error) {
          console.log('Error in handleLogCodeVisibility--- ', error);
        }
  }
  
  async handleRecordtypeChange(classification, intentId)
    {
      let recordType= this.resultData?.prefillValues?.caseRecordTypeName?? null;
	  if(recordType)
      {
      const result = await getRecordTypeChange({ clasId: this.case.Classification_Id__c, intId: this.case.Intent_Id__c, caseRt: recordType , clasType: this.resultData.prefillValues.classificationType});
      if(result)
      {
        if(result.returnRT) {
          const rtName = result.returnRT.split('=')[0];
              const rtId = result.returnRT.split('=')[1];
             this.caseUpdateRT = rtId;
              this.resultData.objCase.RecordTypeId = rtId;
              this.resultData.prefillValues.caseRecordTypeName = rtName;
              this.resultData.ctciModel.classificationName = this.case.Classification_Id__c;
              this.resultData.ctciModel.intentName = this.case.Intent_Id__c;
			   if(result.ctciObj) {
          this.resultData.ctciModel.ctciObj = result.ctciObj;
        }
              await this.preFillValues(this.resultData);
        }
       
      }
         
     }
	}
}