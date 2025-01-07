/*******************************************************************************************************************************
Component Name : GroupSearchFormHum
Version        : 1.0
Created On     : 9/24/2020
Function       : This component is for the search functionality on Group Search
                 
Modification Log: 
* Version          Developer Name             Code Review              Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------------
*    1.0           Ashish Kumar                                  	09/24/2020                	Original Version
*                  Supriya Shastri                                02/24/2021                  Added generic dropdown
* 				       Surendra Vemulapalli					               			06/23/2021			        		US1464387- Creating Unknown Group Account (Health Cloud Lightning App)
* 				       Ritik Agarwal					               		      	08/12/2021			        	  US: 2572756
*                Supriya Shastri				               		      	09/22/2021			        	  US: 2468607
*                 Firoja Begam                                    10/27/2021                  Group name Validation rule update on selection of state
*                 Pavan Kumar M                                   09/08/2022                  Group Search information message US-2659333,Bug - 3535772
**************************************************************************************************************************************/

import { LightningElement, track, api } from "lwc";
import { ShowToastEvent } from 'lightning/platformShowToastEvent';
import getStateValues from "@salesforce/apex/SearchUtilty_H_HUM.getStateValues";
import searchGroupAccount from "@salesforce/apex/GroupSearch_LC_HUM.searchGroupAccount";
import getPolicyList from "@salesforce/apex/GroupSearchPolicies_LC_HUM.getPoliciesForGroup";
import { isSpecialCharsExists, getLocaleDate, deepCopy, hasSystemToolbar } from "c/crmUtilityHum";
import { getLabels } from 'c/customLabelsHum';
import GroupSearchNoResultHum from "@salesforce/label/c.GroupSearchNoResultHum";
import groupSearchAtleastOneHum from "@salesforce/label/c.groupSearchAtleastOneHum";
import groupSearchNumberCriteriaHum from "@salesforce/label/c.groupSearchNumberCriteriaHum";
import groupSearchAlphaCriteriaHum from "@salesforce/label/c.groupSearchAlphaCriteriaHum";
import { invokeWorkspaceAPI } from 'c/workSpaceUtilityComponentHum';

export default class GroupSearchFormHum extends LightningElement {

  @track accList = [];
  @track noPolicyResults = false;
  @track policylist;
  @track errorMessage;
  @track showBackToResult;
  isFormValid = true;
  options = [];
  resultsTrue = false;
  sGroupName = "";
  sGroupNumber = "";
  sState = "";
  sUnknownGroupCheck = false;
  noResults = false;
  noresultmsg = GroupSearchNoResultHum;
  stateValue = false;
  showValidation = false;
  labels = getLabels();
  @track createUnknownGrp = false;
  @track modalOpen;

  handleCreate() {
    this.createUnknownGrp = true;
    this.template.querySelector("c-create-unknown-group-hum").handleModalValueChange(this.formData.sGroupName, this.formData.sGroupNumber, this.formData.sState);

    this.handleReset();
  }

  @track formData = {
    sGroupName: this.sGroupName,
    sGroupNumber: this.sGroupNumber,
    sState: this.sState,
    sUnknownGroupCheck: this.sUnknownGroupCheck
  };

  inputChangeHandler(event) {
    let val = event.detail.value;
    let selectedType = event.target.name;
    if (selectedType === "groupName") {
      this.formData.sGroupName = val;
    } else if (selectedType === "groupNumber") {
      this.formData.sGroupNumber = val;
    } else if (selectedType === "state") {
      this.formData.sState = val;
    }
  }

  @api encodedData;
  @api
  encodedValues(encodedDatas) {

    let encodedData = encodedDatas['group'];
    this.sGroupName = encodedData.sGroupName;
    this.sGroupNumber = encodedData.sGroupNumber;
    this.sState = encodedData.sState;
    let me = this;
    this.template.querySelectorAll(".inputfield").forEach(function (field) {


      if (field.name == "groupName") {
        field.value = me.sGroupName;
      } else if (field.name == "groupNumber") {
        field.value = me.sGroupNumber;
      }
    });
    this.formData['sGroupName'] = this.sGroupName;
    this.formData['sGroupNumber'] = this.sGroupNumber;
    this.formData['sState'] = this.sState;

    setTimeout(() => {

      this.handlSearchNew('btnSearch');
    }, 1);

  }
  connectedCallback() {
    this.noResults = false;
    if (this.encodedData && this.encodedData != null && Object.keys(this.encodedData).includes('group')) {
      this.encodedValues(this.encodedData);
    }
    getStateValues()
      .then((data) => {
        if (data) {
          for (let key in data) {
            const opt = { label: key, value: data[key] };
            this.options = [...this.options, opt];
          }
        }
      })
      .catch((error) => {
        this.showToast(this.labels.crmSearchError, this.labels.crmToastError, "error");
      });
  }

  get containerCss(){
    return hasSystemToolbar ? 'searchpage-results-system slds-m-left_small': 'searchpage-results slds-m-left_small'
  }

  stateSelectionHandler(event) {
    let stateVal = event.detail.value;
    this.formData.sState = stateVal;
    if (stateVal.length) {
      this.stateValue = true;
      this.highlightGroupNameFields(); //Added function to highlight GroupName field on selection of state
    } else {
      //Added function to remove highlight from GroupName field if no state is selected
      this.formData.sState = "";
      this.removeGrouphighlight();
    }
  }

  /**
   * Hightlight Group Name Field
   */
  highlightGroupNameFields() {
    let inp = this.template.querySelectorAll(".GroupNameHighlight");
    inp.forEach(function (element) {
      element.required = true;
      element.reportValidity();
    }, this);
  }

  /**
   * Remove Group Name Field Highlight based on StateValue selected
   */
  removeGrouphighlight() {
    if (document.activeElement.tagName != 'INPUT') {
      let inp = this.template.querySelectorAll(".GroupNameHighlight");
      const { sState, sGroupName } = this.formData;
      inp.forEach(function (element) {
        element.required = (sState.length) ? (sGroupName.length) ? false : true : false;
        element.reportValidity();
      }, this);
    }
  }

  /**
   * Handle search
   * and unknown 
   * search functionality
   */
  handleSearch(event) {
    let name = event.target.name;
    this.handlSearchNew(name);
  }
  handlSearchNew(name) {
    if (name === "btnSearch") {
      this.formData.sUnknownGroupCheck = false;
    } else if (name === "unknownSearch") {
      this.accList = null;
      this.formData.sUnknownGroupCheck = true;
    }
    this.policylist = null;
    this.noPolicyResults = false;
    this.showBackToResult = false;
    this.noResults = false;
    this.handleValidation();
  }

  /**
   * Handle validation of
   * each input field
   * on search
   */
  handleValidation() {
    let cont = 0;
    let me = this;
    const formFields = 3;
    this.errorMessage = "";
    let hasGroupNumber = false;
    let hasGroupName = false;
    let hasGroupState = false;
    const sLockMessage = this.labels.US2619274LockMessage;
    let stateGroupField = this.template.querySelector("c-generic-drop-down-hum");
    let inp = this.template.querySelectorAll(".NameHighlight");
    me.isFormValid = true;
    this.template.querySelectorAll(".inputfield").forEach(function (field) {
      me.updateFieldValidation(field, "");
      if (!field.value || (field.name == "state" && field.value === "EMPTY")) {
        cont++;
      } else if (field.name == "groupName") {
        hasGroupName = true;
      } else if (field.name == "groupNumber") {
        hasGroupNumber = true;
      }
    });

    if (stateGroupField.value) {
      hasGroupState = true;
      if (stateGroupField.value !== "EMPTY" &&
        cont == 2
      ) {
        inp.forEach(function (element) {
          element.reportValidity();
        }, this);
      }
    }

    if (cont === 2 && !me.stateValue) {
      this.errorMessage = groupSearchAtleastOneHum;
      me.isFormValid = false;
    }
    else if (me.stateValue && cont == 2) {
      this.showValidation = true;
      me.isFormValid = false;
    } else {
      this.showValidation = false;
    }

    this.template
      .querySelectorAll(".inputGroupNumber")
      .forEach(function (field) {
        if (isSpecialCharsExists(field.value, '!`@#$%^*&()+=[]\\; /{}|":<>?~_')) {
          me.updateFieldValidation(field, groupSearchAlphaCriteriaHum);
          me.isFormValid = false;
        } else if (field.value && field.value.length < 2) {
          me.updateFieldValidation(field, me.labels.groupNumberCriteriaHum);
          me.isFormValid = false;
        }
      });


    if (me.isFormValid) {
	this.resultsTrue = false;
      searchGroupAccount({ oGroupSearchInputWraper: this.formData }).then(
        (res) => {
		  let result = res.lstAccounts;
          if (result.length > 0) {
            this.accList = result.map(item => ({
              Id: item.Id,
              RecordType: item.RecordType.Name,
              Name: item.Name,
              Group_Number__c: item.Group_Number__c,
              Phone: item.Phone,
              BillingState: item.BillingState,
              BillingPostalCode: item.BillingPostalCode,
			  			isLocked:!res.mapRecordAccess[item.Id],
			 				disabled :!res.mapRecordAccess[item.Id],
			 				sLockMessage : sLockMessage,
              BillingStreet: item.BillingStreet
            }));

            this.resultsTrue = true;
            this.noResults = false;
          } else {
            this.noResults = true;
            this.resultsTrue = false;
          }
        }
      ).catch(error => {
        this.noResults = true;
        this.resultsTrue = false;
      });
    }
    else {
      me.clearSearchResults();
    }
  }

  clearSearchResults() {
    let me = this;
    this.policylist = null;
    this.noPolicyResults = false;
    this.showBackToResult = false;
    this.accList = null;
    this.resultsTrue = false;
    this.noResults = false;
  }

  /**
   * Handle reset functionality
   * to clear input fields
   * and results
   */
  @api
  handleReset() {
    let me = this;
    me.clearSearchResults();

    this.formData.sGroupName = "";
    this.formData.sGroupNumber = "";
    this.formData.sState = "";
    this.formData.sUnknownGroupCheck = false;
    this.showValidation = false;
    this.template.querySelectorAll("lightning-input").forEach((field) => {
      field.required = false;
      field.value = "";
      me.updateFieldValidation(field, "");
    });
    const gDropdown = this.template.querySelector("c-generic-drop-down-hum");
    gDropdown && gDropdown.reset();

    this.errorMessage = "";
    me.showValidationMsg = false;
    me.stateValue = false;
  }

  clearStateHandler(event) {
    this.formData.sState = "";
    this.stateValue = false;
  }

  /**
   * Handle reset functionality
   * to clear input fields
   * and results
   */
  handleInteraction(event) {
    var accId = event.detail.Id;
    this.showBackToResult = true;

    getPolicyList({ sAccId: accId })
      .then((res) => {
        let response = deepCopy(res);
        response.forEach(function (item) {
          item.EffectiveFrom = getLocaleDate(item.EffectiveFrom);
          item.EffectiveTo = getLocaleDate(item.EffectiveTo);
        });
        (res.length > 0) ? (this.policylist = response, this.noPolicyResults = false) : this.noPolicyResults = true;
      })
      .catch((err) => {
        this.showToast(this.labels.crmSearchError, this.labels.crmToastError, "error");
      });

  }

  /**
   * Fire search functionality
   * validation on hitting enter
   */
  enterSeach(event) {
    if (event.keyCode === 13) {
      this.handleValidation();
    }
  }

  /**
   * Handle back to result
   * display from selected
   * member card
   */
  backToResults() {
    this.policylist = null;
    this.noPolicyResults = false;
    this.showBackToResult = false;
    this.template.querySelector('c-standard-table-component-hum').backToResult();
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
   * Display toast message 
   * when an exception is
   * thrown
   */
  showToast(strTitle, strMessage, strStyle) {
    this.dispatchEvent(new ShowToastEvent({
      title: strTitle,
      message: strMessage,
      variant: strStyle
    }));
  }

  handleunknowgpevent(event) {

    this.resultsTrue = event.detail.resultsTrue;
    this.accList = event.detail.accList;
  }

  async hyperLinkClick(event) {

    const planTable = this.template.querySelector(`[data-id='group-plan']`);
    let planId = planTable && planTable.selectedRecordId();

    let tabDetails;
    try {
      tabDetails = await invokeWorkspaceAPI('openTab', {
        recordId: event.detail.accountId
      });
      if (tabDetails) {
        this.openSubTabs(tabDetails,planId);
      }
    } catch (error) {
      console.log('error', error);
    }
  }

  async openSubTabs(tabData,planId) {
    try {
      await invokeWorkspaceAPI('openSubtab', {
        parentTabId: tabData,
        focus: false,
        recordId: planId
      });
    } catch (error) {
      console.log('error in groupform subtab', error);
    }
  }
}