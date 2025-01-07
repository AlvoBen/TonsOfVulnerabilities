/*******************************************************************************************************************************
Component Name : agentSearchFormHum
Version        : 1.0
Created On     : 10/08/2020
Function       : Collect the User's Input in a Form and perform Search for Broker / Agency
                 
Modification Log: 
* Developer Name                       Date                       Description
*------------------------------------------------------------------------------------------------------
* Rahul Krishan                    10/08/2020                 Original Version
* Ashish Kumar                     12/01/2020 
* Arpit Jain/Navajit Sarkar        02/10/2021				       Modifications for Search and Integration
* Supriya Shastri                  02/24/2021                   Added generic dropdown
* Mohan kuamr N                    06/01/2021                	DF-3167
* Saikumar Boga                   07/13/2021                   Chnages for Unknown Agent/Broker Form
* Supriya Shastri                 09/24/2021                   Agent/Broker reset fixes
* Firoja Begam                    10/27/2021                   Agent Search Name validation Rule to allow space
**************************************************************************************************************************************/

import { LightningElement, track, api } from 'lwc';
import getStateValues from '@salesforce/apex/SearchUtilty_H_HUM.getStateValues';
import searchAgencyBroker from '@salesforce/apexContinuation/AgencyBrokerSearch_LC_HUM.searchAgencyBroker';
import createSearchAgencyBroker from '@salesforce/apex/AgencyBrokerSearch_LC_HUM.unknownCreateSearchAgencyBroker';
import { ShowToastEvent } from "lightning/platformShowToastEvent";
import upsertAgentAccount from '@salesforce/apex/AgencyBrokerSearch_LC_HUM.insertAgentAccount';
import { isSpecialCharsExists, hcConstants, hasSystemToolbar } from "c/crmUtilityHum";
import { getLabels } from 'c/customLabelsHum';

export default class agentSearchFormHum extends LightningElement {
   @track showBackToResults = false;
   @track accList = [];
   @track options = [];
   resultsTrue = false;
   noResults = false;
   @track sFirstName = '';
   @track sLastName = '';
   @track sState = '';
   @track sAgencyName = '';
   @track sAgentId = '';
   @track sTaxID = '';
   @track sAgentType = 'Agency';
   labels = getLabels();
   isUnknownAgencySearch = false;
   @track isFormValid = true;
   @track showErrorMsg = false;
   @track showValidationMsg = false;
   @track serverresponseobj;
   @track stateValue = false;
   @track agencyValue;
   @track createUnknownAgent = false;


   @track formData = {
      sFirstName: this.sFirstName,
      sLastName: this.sLastName,
      sState: this.sState,
      sAgencyName: this.sFirstName,
      sAgentId: this.sAgentId,
      sTaxID: this.sTaxID,
      sAgentType: this.sAgentType,
      isUnknownAgencySearch: this.isUnknownAgencySearch,
   };
   @track createFormData = {
      sFirstName: this.sFirstName,
      sLastName: this.sLastName,
      sState: this.sState,
      sAgencyName: this.sFirstName,
      sAgentId: this.sAgentId,
      sTaxID: this.sTaxID,
      sAgentType: this.sAgentType,
      isUnknownAgencySearch: this.isUnknownAgencySearch,
   };

   handleCreate() {
      this.createUnknownAgent = true;
      this.template.querySelector("c-unknown-agent-form-hum").handleValueChange(this.formData);

   }
   handleCreateSearch(event) {
      let response = event.detail;
      this.accList = null;
      this.resultsTrue = false;
      this.noResults = false;
      this.createFormData = response;
      createSearchAgencyBroker({ agencySearchWrapper: this.createFormData }).then(res => {
         if (res && res != null && res.lstSObjects.length > 0) {
            this.serverresponseobj = res;
            this.accList = this.serverresponseobj.lstSObjects;
            let temp = [];
            let result = [];
            result = this.accList;
            let strTaxId = JSON.parse(this.serverresponseobj.sTaxIds);
            for (let i = 0; i < result.length; i++) {
               const firstNameTemp = (result[i].Individual_First_Name__c) ? result[i].Individual_First_Name__c : result[i].Name;
               const lastNameTemp = (result[i].Individual_Last_Name__c) ? result[i].Individual_Last_Name__c : '';

               let rectype =
                  this.createFormData.isUnknownAgencySearch == false
                     ? ((this.createFormData.sAgentType) == "All") ? "Broker" : this.createFormData.sAgentType
                     : "UNKNOWN";

               const agencyId = (result[i].Id != undefined)
                  ? result[i].Id
                  : result[i].Account_External_ID__c;

               temp.push({
                  strFirst: firstNameTemp,
                  urlLastName: lastNameTemp,
                  strAgentId: result[i].Agent_ID__c,
                  strTaxId: strTaxId[result[i].Account_External_ID__c],
                  strStreet: result[i].BillingStreet,
                  strState: (rectype == "UNKNOWN") ? result[i].BillingState : result[i].BillingStateCode,
                  strZipCode: result[i].BillingPostalCode,
                  recordType: rectype,
                  sAgencyExtId: agencyId,
                  strCity: result[i].BillingCity,
                  strWorkEmail: result[i].Work_Email__c,
                  salesforceAccount: result[i]
               });
            }
            this.accList = temp;
            this.resultsTrue = true;
            this.noResults = false;

         }
         else {
            this.noResults = true;
            this.resultsTrue = false;
         }
      })
         .catch(error => {
            this.noResults = true;
            this.resultsTrue = false;
         });

   }

   handleFromUnknownSearch() {
      this.formData.isUnknownAgencySearch = true;
      this.handleSearch();
   }

   handleFromSearch() {
      this.formData.isUnknownAgencySearch = false;
      this.showBackToResults = false;
      this.handleSearch()
   }

   enterSeach(event) {
      if (event.keyCode === 13) {
         this.handleFromSearch();
      }
   }

   showVisualIndicator(evnt) {
      const fieldType = evnt.currentTarget.getAttribute('data-field');
      const selector = this.getSelectorByField(fieldType);
      this.highlightOrRemove(selector, true);
   }

   /**
   * Hightlight validation failures
   */
   highlightFields() {
      let inp = this.template.querySelectorAll(".state-change");
      inp.forEach(function (element) {
         element.required = true;
         element.reportValidity();
      }, this);
   }


   removeVisualIndicator(evnt) {
      const fieldType = evnt.currentTarget.getAttribute('data-field');
      const selector = this.getSelectorByField(fieldType);
      this.highlightOrRemove(selector, false);
   }

   removehighlight() {
      let inp = this.template.querySelectorAll(".state-change");
      inp.forEach(function (element) {
         element.required = false;
         element.reportValidity();
      }, this);
   }

   getSelectorByField(fieldType) {
      let selector = ""; // class of the field which needs to be highlighted
      switch (fieldType) {
         case 'agent-type':
            selector = ".id-input";
            break;
         case 'last-name':
            selector = '.last-name-change';
            break;
         case 'first-name':
            selector = '.first-name-change';
            break;
         case 'state-field':
            selector = '.state-change'
            break;
         case 'tax-id':
         case 'agent-id':
            selector = '.agent-type';
            break;
         default:
      }
      return selector;
   }

   /**
     * update data on field change
     * @param {*} evnt 
     */
   onFieldChange(evnt) {
      const fieldKey = evnt.currentTarget.getAttribute('data-id');
      this.formData[fieldKey] = evnt.target.value === hcConstants.OPTION_NONE ? "" : evnt.target.value;
   }

   stateSelectionHandler(event) {
      let stateVal = event.detail.value;
      this.formData.sState = stateVal;
      if (stateVal.length) {
         this.stateValue = true;
         this.highlightFields();
      } else {
         this.formData.sState = "";
         this.removehighlight();
      }
   }

   typeSelectionHandler(event) {
      if (event) {
         let agencyVal = event.detail.value;
         const { value } = event ? event.detail : 'Agency';
         this.formData.sAgentType = value === 'Broker' ? 'All' : value;
         if (agencyVal.length) {
            this.agencyValue = true;
         } else {
            this.formData.sAgentType = "";
         }
      } else {
         this.formData.sAgentType = 'Agency';
      }
   }

   handleAgentChange(event) {
      //as per classic logic if it is broker value is All
      const { value } = event.detail;
      this.formData.sAgentType = value === 'Broker' ? 'All' : value;
      this.toggleHighlight(value, ".id-input");
   }

   highlightOrRemove(fields, isRequired) {
      var inp = this.template.querySelectorAll(fields);
      inp.forEach(function (element) {
         element.required = isRequired;
         element.reportValidity();
         if (!isRequired) {
            element.reportValidity();
         }
      }, this);
   }

   @api
   encodedValues(encodedDatas) {
      let encodedData = encodedDatas['agent'];
      this.sFirstName = encodedData.sFirstName;
      this.sLastName = encodedData.sLastName;
      this.sState = encodedData.sState;
      this.sAgencyName = encodedData.sFirstName;
      this.sAgentId = encodedData.sAgentId;
      this.sTaxID = encodedData.sTaxID;
      this.sAgentType = encodedData.sAgentType;
      let me = this;
      this.template.querySelectorAll(".inputfield").forEach(function (field) {
         if (field.name == "agentFirstName") {
            field.value = me.sFirstName;
         } else if (field.name == "agentLastName") {
            field.value = me.sLastName;
         } else if (field.name == "agentState") {
            field.value = me.sState;
         } else if (field.name == "AgentId") {
            field.value = me.sAgentId;
         } else if (field.name == "agentTaxId") {
            field.value = me.sTaxID;
         } else if (field.name == "AgentType") {
            field.value = me.sAgentType;
         }
      });
      this.formData['sFirstName'] = this.sFirstName;
      this.formData['sLastName'] = this.sLastName;
      this.formData['sState'] = this.sState;
      this.formData['sAgencyName'] = this.sAgencyName;
      this.formData['sAgentId'] = this.sAgentId;
      this.formData['sTaxID'] = this.sTaxID;
      this.formData['sAgentType'] = this.sAgentType;
      setTimeout(() => {
         this.handleFromSearch();
      }, 1);
   }
   @api encodedData;
   connectedCallback() {
      this.noResults = false;
      if (this.encodedData && this.encodedData != null && Object.keys(this.encodedData).includes('agent')) {
         this.encodedValues(this.encodedData);
      }
      getStateValues().then(data => {
         if (data) {
            for (let key in data) {
               const opt = { label: key, value: data[key] };
               this.options = [...this.options, opt];
            }
         }
      })
         .catch(error => {
            this.showToast(
               this.labels.crmSearchError,
               this.labels.crmToastError,
               "error"
            );
         });
   }

   get containerCss(){
      return hasSystemToolbar ? 'searchpage-results-system slds-m-left_small': 'searchpage-results slds-m-left_small'
   }

   handleSearch() {
      const me = this;
      me.accList = null;
      me.resultsTrue = false;
      me.noResults = false;
      let iInvalidFields = 0;
      let agentTypeField = this.template.querySelector(".agent-type");
      const fNameFields = this.template.querySelector(".firstname");
      let isFirstName = fNameFields.value ? true : false;
      me.showErrorMsg = false;
      me.showValidationMsg = false;
      me.isFormValid = true;
      const inpFields = this.template.querySelectorAll(".inputfield");
      inpFields.forEach(function (field) {
         me.updateFieldValidation(field, "");
         if (!field.value) {
            iInvalidFields++;
         }
      });

      if (iInvalidFields === 4 && !me.stateValue) {
         me.showErrorMsg = true;
         me.isFormValid = false;
      }
      else if (!agentTypeField.value || (!isFirstName && me.stateValue)) {
         me.showValidationMsg = true;
         me.isFormValid = false;
      }

      inpFields.forEach(function (field) {
         if (field.name && field.value && field.value !== "None") {
            switch (field.name) {
               case 'agentLastName':
                  me.verifySpecialChars(
                     field,
                     me.labels.HumSearchAgencyNameValidation
                  );
                  if (!me.stateValue) {
                     me.showValidationMsg = true;
                     me.isFormValid = false;
                  } else if (me.stateValue && !isFirstName) {
                     me.noResults = true;
                     me.isFormValid = false;
                  }
                  break;
               case 'agentFirstName':
                  me.verifySpecialChars(field, me.labels.HumSearchAgencyNameValidation);
                  if (!me.stateValue) {
                     me.showValidationMsg = true;
                     me.isFormValid = false;
                  }
                  break;
               default:
            }
         }
      });

      if (me.isFormValid) {
         searchAgencyBroker({ agencySearchWrapper: this.formData }).then(res => {
            if (res && res != null && res.lstSObjects.length > 0) {
               this.serverresponseobj = res;
               this.accList = this.serverresponseobj.lstSObjects;
               let temp = [];
               let result = [];
               result = this.accList;
               let strTaxId = JSON.parse(this.serverresponseobj.sTaxIds);
               for (let i = 0; i < result.length; i++) {
                  const firstNameTemp = (result[i].Individual_First_Name__c) ? result[i].Individual_First_Name__c : result[i].Name;
                  const lastNameTemp = (result[i].Individual_Last_Name__c) ? result[i].Individual_Last_Name__c : '';

                  let rectype;
                  let agentDatata  = result[i].hasOwnProperty('RecordType') ? result[i].RecordType.Name : '';
                     if(agentDatata === "Unknown Agent/Broker"){
                        rectype =  "UNKNOWN";
                     }
                     else{
                        rectype = ((this.formData.sAgentType) == "All") ? "Broker" : this.formData.sAgentType;
                     }

                  const agencyId = (result[i].Id != undefined)
                     ? result[i].Id
                     : result[i].Account_External_ID__c;

                  temp.push({
                     strFirst: firstNameTemp,
                     urlLastName: lastNameTemp,
                     strAgentId: result[i].Agent_ID__c,
                     strTaxId: strTaxId[result[i].Account_External_ID__c],
                     strStreet: result[i].BillingStreet,
                     strState: (rectype == "UNKNOWN") ? result[i].BillingState : result[i].BillingStateCode,
                     strZipCode: result[i].BillingPostalCode,
                     recordType: rectype,
                     sAgencyExtId: agencyId,
                     strCity: result[i].BillingCity,
                     strWorkEmail: result[i].Work_Email__c,
                     salesforceAccount: result[i]
                  });
               }
               this.accList = temp;
               this.resultsTrue = true;
               this.noResults = false
            }
            else {
               this.noResults = true;
               this.resultsTrue = false;
            }
         })
            .catch(error => {
               this.noResults = true;
               this.resultsTrue = false;
            });
      } else {
         me.clearResults();
      }

   }

   get optionsAgentType() {
      return [
         { label: "Agency", value: "Agency" },
         { label: "Broker", value: "Broker" },
      ];
   }

   /**
   * Handle form reset
   */
   @api
   handleResetAgent() {
      this.formData.sFirstName = '';
      this.formData.sLastName = '';
      this.formData.sState = '';
      this.formData.sAgencyName = '';
      this.formData.sAgentId = '';
      this.formData.sTaxID = '';
      this.formData.sTaxID = '';
      this.formData.sAgentType = '';
      this.formData.isUnknownAgencySearch = false;
      this.noResults = false;
      this.clearResults();

      const gDropDowns = this.template.querySelector(`[data-id='state-field']`);
      gDropDowns && gDropDowns.reset();

      this.template.querySelector('c-generic-drop-down-hum').preSelectValue("Agency");
      this.template.querySelectorAll(".inputfield").forEach((field) => {
         this.agencyValue = true;
         this.formData.sAgentType = 'Agency';
         if (field.name === "agentState") {
            field.value = "";
         } else {
            field.required = false;
            field.value = "";
         }

         this.updateFieldValidation(field, "");
      });
      this.showErrorMsg = false;
      this.showValidationMsg = false;
      this.stateValue = false;
   }

   clearResults() {
      this.accList = [];
      this.resultsTrue = false;
      this.showBackToResults = false;
   }

   clearStateHandler() {
      this.formData.sState = "";
      this.stateValue = false;
      this.showValidationMsg = false;
   }

   clearTypeHandler() {
      this.formData.sAgentType = "";
      this.agencyValue = false;
   }

   backToResults() {
      this.showBackToResults = false;
      this.template.querySelector('c-standard-table-component-hum').backToResult();
   }

   /**
   * Add or remove validation height
   * to selected field
   * @param {*} value
   * @param {*} elementHighlight
   */
   toggleHighlight(value, elementHighlight) {
      if (value) {
         this.highlightOrRemove(elementHighlight, true);
      } else {
         this.highlightOrRemove(elementHighlight, false);
      }
   }

   /**
   * Update Field validation with message
   * @param {*} field
   * @param {*} message
   */
   updateFieldValidation(field, message) {
      field.setCustomValidity(message);
      field.reportValidity();
   }

   /**
     * Verify for special characters
     * @param {*} errorMsg
     */
   verifySpecialChars(field, errorMsg) {
      const isInValid = isSpecialCharsExists(field.value, '!`@#$%^*()+=[]\\;/{}|":<>?~_') ? true : false; //Updated specialchar rule to allow space
      if (isInValid) {
         this.updateFieldValidation(field, errorMsg);
         this.isFormValid = false;
      }
   }

   showToast(strTitle, strMessage, strStyle) {
      this.dispatchEvent(
         new ShowToastEvent({
            title: strTitle,
            message: strMessage,
            variant: strStyle,
         })
      );
   }
   //upsert logic
   handleRecordSelection(event) {
      if (this.accList.length > 1) {
         this.showBackToResults = true;
      }
      let accountId = event.detail.Id;
      if (!accountId.startsWith("0")) {
         if (this.accList) {
            const recUniqueId = event.detail.Id;
            let accountJSON;
            for (let obj of JSON.parse(JSON.stringify(this.serverresponseobj.lstSObjects))) {
               if (obj.Account_External_ID__c === recUniqueId) {
                  accountJSON = JSON.stringify(obj);
               }
            }
            let str;
            let strTaxIds = JSON.parse(this.serverresponseobj.sTaxIds);
            for (let key in strTaxIds) {
               if (key === recUniqueId) {
                  str = JSON.stringify(strTaxIds[key]);
               }
            }
            //Server call
            upsertAgentAccount({
               consumerIds: str,
               accountJson: accountJSON,
               externalId: recUniqueId
            })
               .then(result => {
                  this.template.querySelector('c-standard-table-component-hum').recordIdToNavigate(result);
               })
               .catch(error => {
                  console.log(error);
               });
         }
      }
   }
}