/*******************************************************************************************************************************
LWC JS Name : accountSecuritySectionHum.js
Function    : Renders Security Details section

Modification Log: 
Developer Name               Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Mohan                   11/13/2021                    US: 2440592 Member Account Re design
*********************************************************************************************************************************/

import { LightningElement, api, track } from 'lwc';
import getUserInformationDTO from '@salesforce/apex/UserAssociatedInformation_LC_HUM.getUserInformationDTO';
import { getLabels } from 'c/customLabelsHum';
import {  expandAccordianRow, compareDate,  getLocaleDate,  hcConstants } from 'c/crmUtilityHum';
import saveFormdata from '@salesforce/apex/UserAssociatedInformation_LD_HUM.performPopupDetailsUpdate';
import { ShowToastEvent } from "lightning/platformShowToastEvent";


export default class AccountSecuritySectionHum extends LightningElement {
  @api recordId;
  @track aData = {};
  @track iCount = 0;
  @track isDeceased = false;
  @track labels = getLabels();
  @track showQuestionList = false;
  @track bShowModal = false;
  @track showActionModal = false;
  @track editQuestion = false;
  @track deleteQuestion = false;
  @track createForm = true;
  @track isContinueModal = false;
  @track buttonsConfig;
  @track modalTitle = "Account Password Notification";
  @track overlayModalTitle = "";
  @track showCreateQuestion = false;

  @track accordianFieldSample = {
    Question: ['UserValue', 'SecurityQuestion', 'Answer']
  };
  @track accordianFields = [];

  @track accordianFieldLabelMapping = {
    UserValue: hcConstants.PASS,
    Answer: hcConstants.ANSWER,
    SecurityQuestion: hcConstants.SECURITY_QUESTION
  };

  connectedCallback() {
    const me = this;
    me.searchDatabase();
  }

  /**
   * Description - creating keys in the response for accordian functionality, also checking if termed and deceased
   */
  createKeyInDocumentForms(response) {
    //adding keys to aData for sorting, accordion and BlankFields
    let newResponse = response.map(item => {
      this.isDeceased = item.DocumentType === hcConstants.PASS ? item.isDeceased : this.isDeceased;

      return {
        ...item,
        ETL_Record_Deleted__c: item.DocumentType !== hcConstants.PASS,
        Accordian: item.DocumentType === hcConstants.PASS || item.DocumentType === hcConstants.CONSENT_FOR_PHI || item.DocumentType === hcConstants.MEDICAL_ORDER,
        iconName: 'utility:jump_to_right',
        extraField: false,
        blankField: item.DocumentType === hcConstants.EXECUTOR ? false : this.isDeceased,
        terminatedForm: compareDate(getLocaleDate(item.TerminationDate), getLocaleDate(new Date())) === 0 ? true : false,
        extraFieldMapping: this.getExtraFieldMapping(item)
      }
    });

    let otherFormsData = [];
    //making values blank on the basis of blankField key for Password Type Form
    newResponse.forEach((item) => {
      if ((item.blankField || item.terminatedForm) && item.DocumentType === hcConstants.PASS) {
        for (let key of Object.keys(item)) {
          if (key !== 'blankField' && key !== 'Accordian' && key !== 'ETL_Record_Deleted__c' && key !== 'iconName' && key !== 'extraField' && key !== 'DocumentType' && key !== 'extraFieldMapping' && key !== 'isDeceased' && key !== 'terminatedForm') {
            item[key] = '';
          }
        }
        this.showCreateQuestion = true;
      } else if ((!item.blankField && !item.terminatedForm) && item.DocumentType === hcConstants.PASS) {
        this.showCreateQuestion = false;
      }
    });

    newResponse.forEach((item) => {
      if (item.terminatedForm && !item.blankField) {
        if (item.DocumentType === hcConstants.PASS) {
          otherFormsData.push(item);
        }
      } else if (!item.terminatedForm && !item.blankField) {
        otherFormsData.push(item);
      }
    });

    newResponse = otherFormsData;

    return newResponse;
  }

  /**
   * Description - this method decides which set of fields should be visible when accordian is clicked
   */
  getExtraFieldMapping(item) {
    let  sMapping = ""; 
    if (item && item.DocumentType === hcConstants.PASS){
          sMapping = hcConstants.QUESTION;
    }
    return sMapping;
  }

  /**
   * Description - this method is fired when accordian is clicked and will show the set of related fields on the
   *                basis of the clicked doc type.
   */
  expandrow(event) {
    this.aData = expandAccordianRow(event, [this.aData])[0];
    let newResponse;

    //for showing more fields
    let fieldList = this.accordianFieldSample[event.currentTarget.getAttribute('data-doctype')];
    if (this.aData.iconName === 'utility:jump_to_bottom' && fieldList) {
      const docType = event.currentTarget.getAttribute('data-doctype');
      if (this.aData.extraFieldMapping === docType) {
        newResponse = fieldList.map((itemInside) => {

          return ({
            label: this.accordianFieldLabelMapping[itemInside],
            value: this.aData[itemInside]
          });
        });
      }

    }
    this.accordianFields = newResponse;
  }

  expandlist(event) {
    this.showQuestionList = !this.showQuestionList;
  }

  /**
   * Description - this method fetches the password from the backend
   */
  searchDatabase(save = false) {
    getUserInformationDTO({
      sAccountId: this.recordId
    })
      .then(aResponse => {
        if (aResponse && aResponse.length > 0) {
          this.aData = this.createKeyInDocumentForms(aResponse)[0];
          if (save) {
            this.afterSaveHandler();
          }
        }
      })
      .catch(error => {
        console.log('Error', error);
      });
  }

  /**
   * Description - this method is used for the popup related automation that are visible on the User Assocaited form
   */
  afterSaveHandler() {
    try {
      this.bShowModal = false;
      this.showQuestionList = false;
      let accordianAll = this.template.querySelectorAll('.accordianPass');
      accordianAll[0].click();
    } catch (error) {
      console.log('Error', error);
    }
  }

  /**
   * Open password popup on click of create password
   */
  createPwdHandler() {
    this.bShowModal = true;
    this.createForm = true;
    this.editQuestion = false;
    this.modalTitle = "New Password";
    this.showQuestionList = false;
  }

  /**
   * Verify password popup for unsaved changes and the close
   */
  closeModal() {
    this.template.querySelector('c-question-form-hum').hasData();
    this.template.querySelector('c-question-form-hum').handleHighlightsonLoad();
  }

  /**
   * Handles accordian open click
   * @param {*} event 
   */
  handleAccordian() {
    this.searchDatabase(true);
  }

  /**
   * Handles edit form modifications
   * @param {*} event 
   */
  modifiedHandler(event) {
    if (event.detail) {
      this.bShowModal = true;
      this.showActionModal = true;
      this.buttonsConfig = [{
        text: 'Continue',
        isTypeBrand: true,
        eventName: 'continue'
      }, {
        text: getLabels().HUMCancel,
        isTypeBrand: false,
        eventName: 'closeoverlay'
      }]
      this.overlayModalTitle = "Unsaved Changes";
      this.isContinueModal = true;
      this.deleteQuestion = false;
    } else {
      this.closeAllModals();
    }
  }

  /**
   * Closes both modals with
   * form and unsaved changes prompt
   */
  closeAllModals() {
    this.bShowModal = false;
    this.showActionModal = false;
  }

  handleContinue() {
    this.bShowModal = false;
    this.showActionModal = false;
    this.resetForm();
  }

  /**
   * Close continue modal
   */
  closeOverlayModal() {
    this.showActionModal = false;
  }

  /**
   * Save create and edit form
   * @param {*} event 
   */
  saveForm(event) {
    this.template.querySelector('c-question-form-hum').saveFields(event);
  }

  /**
   * Reset Create and Edit form
   */
  resetForm() {
    this.template.querySelector('c-question-form-hum').resetFields();
  }

  deleteForm() {
    saveFormdata({
      popupDetailsWrapper: JSON.stringify({}),
      recId: this.recordId
    }).then(res => {
      if (res) {
        this.showToast(this.labels.passwordDeletedHum, "", "success"); //Shows toast on successfull deletion
        this.handleAccordian();
      }
    }).catch(err => {
      console.log("error", err);
    });
    this.closeOverlayModal();
  }

  /**
   * Open popup on click of edit in the list
   */
  editPwdHandler() {
    this.bShowModal = true;
    this.editQuestion = true;
    this.modalTitle = "Edit Password";
    this.deleteQuestion = false;
    this.createForm = false;
    this.showQuestionList = false;
  }

  /**
   * Handle Delete password
   */
  deletePwdHandler() {
    this.showQuestionList = false;
    this.showActionModal = true;
    this.deleteQuestion = true;
    this.isContinueModal = false;
    this.editQuestion = false;
    this.createForm = false;
    this.overlayModalTitle = "Delete Password";
    this.buttonsConfig = [{
      text: getLabels().HUMCancel,
      isTypeBrand: false,
      eventName: 'closeoverlay'
    }, {
      text: 'Delete',
      isTypeBrand: true,
      eventName: 'delete'
    }]
  }

  /**
   * Generiic method to handle toast messages
   * @param {*} strTitle 
   * @param {*} strMessage 
   * @param {*} strStyle 
   */
  showToast(strTitle, strMessage, strStyle) {
    this.dispatchEvent(
      new ShowToastEvent({
        title: strTitle,
        message: strMessage,
        variant: strStyle,
      })
    );
  }
}