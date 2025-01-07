/*******************************************************************************************************************************
LWC JS Name : coachingAssociatedFormHUM.js
Function    : This JS serves as controller to coachingAssociatedFormHUM.html. 

Modification Log: 
Developer Name           Code Review                      Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Prudhvi Pamarthi               					      05/22/2021                 Original Version 
*********************************************************************************************************************************/
import { LightningElement, track, wire, api } from 'lwc';
import callServicePHIConsentInfo from '@salesforce/apexContinuation/UserAssociatedInfoCoach_C_HUM.callServicePHIConsentInfo';
import getUserInformationDTO from '@salesforce/apex/UserAssociatedInfoCoach_C_HUM.getUserInformationDTO';
import { getRecord } from 'lightning/uiRecordApi';
import { getLabels, sortTableAssociatedForms, expandAccordianRow, compareDate, getLocaleDate, hcConstants } from 'c/coachUtilityHum';
import USER_ID from '@salesforce/user/Id';
import hasDesigneeServiceSwitchAccess from '@salesforce/customPermission/DesigneeServiceSwitch';
import PROFILE_NAME_FIELD from '@salesforce/schema/User.Profile.Name';
import { NavigationMixin } from 'lightning/navigation';
import saveFormdata from '@salesforce/apex/UserAssociatedInfoCoach_D_HUM.performPopupPasswordDetailsUpdate';
import { ShowToastEvent } from "lightning/platformShowToastEvent";

export default class coachingAssociatedFormHUM extends NavigationMixin(LightningElement) {
  @api sRecordId;
  @track oResponse = [];
  @track iCount = 0;
  @track error;
  @track profilename;
  @track isDeceased = false;
  @track isResults = true;
  @track labels = getLabels();
  @track showPasswordList = false;
  @track bShowModal = false;
  @track showActionModal = false;
  @track editPassword = false;
  @track deletePassword = false;
  @track createForm = true;
  @track isContinueModal = false;
  @api buttonsConfig;
  @track modalTitle = "Account Password Notification";
  @track overlayModalTitle = "";
  @track finalResponse;
  @track filterDataResponse = [];
  @track saveClicked = false;
  @track showCreatePassword = false;


  @track accordianFieldSample = {
    Password: ['UserValue','SecurityQuestion','Answer'],
    ConsentForPHIFull: ['Relationship', 'LevelOfConsent', 'SubmissionMehtod'],
    ConsentForPHILimited: ['Relationship', 'LevelOfConsent', 'SubmissionMehtod', 'ServiceStartDate', 'LineOfCoverage', 'LimitedDisclosureDetails'],
    MedicalOrder: ['GuardianAddress'],
    CommonDocumentType: ['EffectiveDate', 'TerminationDate', 'PersonofAuthority']
  };
  @track accordianFields = [];

  @track accordianFieldLabelMapping = {
    UserValue: hcConstants.PASSWORD,
    Answer: hcConstants.ANSWER,
    Relationship: hcConstants.RELATIONSHIP,
    SecurityQuestion: hcConstants.SECURITY_QUESTION,
    LevelOfConsent: hcConstants.LEVEL_OF_CONSENT,
    SubmissionMehtod: hcConstants.SUBMISSION_METHOD,
    ServiceStartDate: hcConstants.DATES_OF_SERVICE,
    LineOfCoverage: hcConstants.LINES_OF_COVERAGE,
    LimitedDisclosureDetails: hcConstants.LIMITED_DISCLOSURE,
    GuardianAddress: hcConstants.GUARDIAN_ADDRESS
  };

  @wire(getRecord, {
    recordId: USER_ID,
    fields: [PROFILE_NAME_FIELD]
  }) wireuser({
    error,
    data
  }) {
    if (error) {
      this.error = error;
    } else if (data) {
      this.profilename = data.fields.Profile.value.fields.Name.value;
    }
  }

  connectedCallback() {
    const me = this;
    me.searchDatabase();
    if (hasDesigneeServiceSwitchAccess) {
      setTimeout(() => {
        me.search();
      }, 1)
    }

     // handles outside clisk for password list options
    me.actionListFocusOut = function (evnt) {
      const actionListContainer = evnt.toElement && evnt.toElement.classList
        && evnt.toElement.classList.contains('action-list');
      if (actionListContainer === false && me.showPasswordList) {
        me.showPasswordList = false;
      }
    };
    document.addEventListener('click', me.actionListFocusOut, false);
  }

    // Remove event listener
  disconnectedCallback() {
    document.removeEventListener('click', this.actionListFocusOut);
  }

  filterDocTypeAndFields(data) {
    data.forEach((item) => {
      if (item.DocumentType !== hcConstants.PASSWORD) {
        this.filterResponse(item);
      }
    });
  }

  /**
  * Description - this method will get the integration data related to document Types and also processing related to it
  */
  search() {
    callServicePHIConsentInfo({ sAccountId: this.sRecordId }).then(data => {
      //method to fire event for POA and populate the oResponse
      this.prepareDataForPOA(data);
      //method to create required keys and making field blank conditionally
      let newResponse = this.createKeyInDocumentForms(this.oResponse);

      this.iCount = newResponse ? newResponse.length : this.iCount;
      //method to filter
      this.filterDocTypeAndFields(newResponse);
      //2nd level sorting for documents with same type on the basis of PersonofAuthority value
      this.oResponse = sortTableAssociatedForms(newResponse, 'DocumentType', 'PersonofAuthority', false);

      this.finalResponse= this.oResponse;

    })
      .catch(error => {
        console.log('Error at c/coachingAssociatedFormHUM - search', error);
      });
  }

  /**
  * Description - method to filter the document type if all the fields are blank
  */
  filterResponse(item) {
    switch (item.DocumentType) {
      case hcConstants.CONSENT_FOR_PHI:
        let valuePHI = (item.LevelOfConsent === 'FullDisclosure') ? 'ConsentForPHIFull' : (item.LevelOfConsent === 'LimitedDisclosure') ? 'ConsentForPHILimited' : 'ConsentForPHIFull';
        this.removeDocummentType(item, [...this.accordianFieldSample[valuePHI], ...this.accordianFieldSample['CommonDocumentType']]);
      case hcConstants.MEDICAL_ORDER:
        this.removeDocummentType(item, [...this.accordianFieldSample['MedicalOrder'], ...this.accordianFieldSample['CommonDocumentType']]);
      default:
        this.removeDocummentType(item, this.accordianFieldSample['CommonDocumentType']);
    }
  }

  /**
   * Description - method to remove the blank document types
   */
  removeDocummentType(item, listOfDocTypeFields) {
    let removeDocType = listOfDocTypeFields.every(field => {
      item[field] === '';
    });
    removeDocType ? '' : this.filterDataResponse.push(item);
  }

  /**
 * Description - creating keys in the response for accordian functionality, also checking if termed and deceased
 */
  createKeyInDocumentForms(response) {
    //adding keys to oResponse for sorting, accordion and BlankFields
    let newResponse = response.map(item => {

      this.isDeceased = item.DocumentType === hcConstants.PASSWORD ? item.isDeceased : this.isDeceased;

      return {
        ...item,
        ETL_Record_Deleted__c: item.DocumentType !== hcConstants.PASSWORD,
        Accordian: item.DocumentType === hcConstants.PASSWORD || item.DocumentType === hcConstants.CONSENT_FOR_PHI || item.DocumentType === hcConstants.MEDICAL_ORDER,
        iconName: 'utility:jump_to_right',
        extraField: false,
        blankField: this.isDeceased,
        terminatedForm: compareDate(getLocaleDate(item.TerminationDate), getLocaleDate(new Date())) === 0 ? true : false,
        extraFieldMapping: this.getExtraFieldMapping(item)
      }
    });

    let otherFormsData = [];
    //making values blank on the basis of blankField key for Password Type Form
    newResponse.forEach((item) => {
      if ((item.blankField || item.terminatedForm) && item.DocumentType === hcConstants.PASSWORD) {
        for (let key of Object.keys(item)) {
          if (key !== 'blankField' && key !== 'Accordian' && key != 'ETL_Record_Deleted__c' && key !== 'iconName' && key !== 'extraField' && key !== 'DocumentType' && key !== 'extraFieldMapping' && key !== 'isDeceased' && key !== 'terminatedForm') {
            item[key] = '';
          }
        }
        this.showCreatePassword = true;
      }
      else if ((!item.blankField && !item.terminatedForm) && item.DocumentType === hcConstants.PASSWORD) {
        this.showCreatePassword = false;
      }
    });

    let tempArray = newResponse;

    newResponse.forEach((item) => {
      if (item.blankField && item.DocumentType === hcConstants.PASSWORD) {
        otherFormsData.push(tempArray[0]);
      }
      else if (item.terminatedForm && !item.blankField) {
        if (item.DocumentType === hcConstants.PASSWORD) {
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
    if (item) {
      switch (item.DocumentType) {
        case hcConstants.PASSWORD:
          return hcConstants.PASSWORD;
        case hcConstants.CONSENT_FOR_PHI:
          return (item.LevelOfConsent === 'FullDisclosure') ? 'ConsentForPHIFull' : (item.LevelOfConsent === 'LimitedDisclosure') ? 'ConsentForPHILimited' : 'ConsentForPHIFull';
        case hcConstants.MEDICAL_ORDER:
          return 'MedicalOrder';
        default:
          return '';
      }

    }
  }

  /**
 * Description - this method is fired when accordian is clicked and will show the set of related fields on the
 *                basis of the clicked doc type.
 */
  expandrow(event) {
    this.oResponse = expandAccordianRow(event, this.oResponse);
    let indx = event.currentTarget.getAttribute('data-att');
    let newResponse;

    //for showing more fields
    let fieldList = this.accordianFieldSample[event.currentTarget.getAttribute('data-doctype')];
    if (this.oResponse[indx].iconName === 'utility:jump_to_bottom' && fieldList) {
      const docType = event.currentTarget.getAttribute('data-doctype');
      this.oResponse.forEach((item) => {
        if (item['extraFieldMapping'] === docType) {
          newResponse = fieldList.map((itemInside) => {

            return ({ label: this.accordianFieldLabelMapping[itemInside], value: item[itemInside] });
          });
        }
      });
    }
    this.accordianFields = newResponse;
  }

  expandlist(event) {
    this.showPasswordList = !this.showPasswordList;

  }

  /**
 * Description - this method fetches the password from the backend
 */
  searchDatabase(save = false) {
    getUserInformationDTO({ sAccountId: this.sRecordId }).then(data => {
      if (data) {
        if (data.length > 0) {
          this.oResponse=[];
          this.oResponse = data;
          this.oResponse = this.createKeyInDocumentForms(this.oResponse);
        }
        if(!save) {
          this.iCount = data.length;
        }
        else {
          this.afterSaveHandler();
        }
      }
    })
      .catch(error => {
        console.log('Error at c/coachingAssociatedFormHUM - searchDatabase', error);
      });
  }
  /**
 * Description - this method is used for the popup related automation that are visible on the User Assocaited form
 */
  async afterSaveHandler() {
    try {
      const popupStatusMessage = await this.afterSavePopup();
      if (popupStatusMessage === 'success') {
        let accordianAll = this.template.querySelectorAll('.accordianPass');
        accordianAll[0].click();
      }
    }
    catch (error) {
      console.log('Error at c/coachingAssociatedFormHUM - afterSaveHandler', error);
    }
  }

  /**
 * Description - method to fire event for POA and populate the oResponse
 */
  prepareDataForPOA(data) {
    let oPOADataArray = [];
    let vPOAData;
    if (data) {
      for (let i = 0; i < data.length; i++) {
        if (data[i].DocumentType === hcConstants.POWER_OF_ATTORNEY) {
          oPOADataArray.push(data[i]);
        }
        else {
          this.oResponse.push(data[i]);
        }
      }
    }
    vPOAData = '{"POAData":' + JSON.stringify(oPOADataArray) + '}';

    const selectEvent = new CustomEvent(hcConstants.EVENT_POA_INFO, {
      detail: vPOAData
    });
    this.dispatchEvent(selectEvent);
  }

  /**
   * Open password popup on click of create password
   */
  createPwdHandler() {
    this.bShowModal = true;
    this.createForm = true;
    this.editPassword = false;
    this.modalTitle = "New Password";
  }

  /**
   * Verify password popup for unsaved changes and the close
   */
  closeModal() {
    this.template.querySelector('c-coaching-password-form-hum').hasData();
    this.template.querySelector('c-coaching-password-form-hum').handleHighlightsonLoad();
  }

  /**
   * Handles accordian open click
   * @param {*} event 
   */
  handleAccordian(event) {
    this.searchDatabase(true);
  }

  async afterSavePopup() {
    this.finalResponse = this.finalResponse ? this.finalResponse.splice(1, this.finalResponse.length - 1) : null;
    const res= this.oResponse;

    //for concatination
    const resNew = [...res, ...this.finalResponse];
    this.oResponse=resNew;
    return new Promise((resolve, reject) => {
      if(this.oResponse){
        this.bShowModal = false;
        this.finalResponse=this.oResponse;
        resolve('success');
      }
      else{
        reject('error in popup');
      }
    });
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
        this.deletePassword = false;
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
    this.template.querySelector('c-coaching-password-form-hum').saveFields(event);
  }

  /**
   * Reset Create and Edit form
   */
  resetForm() {
    this.template.querySelector('c-coaching-password-form-hum').resetFields();
  }

  deleteForm() {
    saveFormdata({ popupDetailsWrapper: JSON.stringify({}), recId: this.sRecordId }).then(res => {
      if (res) {
        this.showToast(this.labels.passwordDeletedHum, "", "success");  //Shows toast on successfull deletion
        this.handleAccordian();
      }
    }).catch(err => {
      console.log("error");
    });
    this.closeOverlayModal();
  }

    /**
   * Open popup on click of edit in the list
   */
  editPwdHandler() {
    this.bShowModal = true;
    this.editPassword = true;
    this.modalTitle = "Edit Password";
    this.deletePassword = false;
    this.createForm = false;
  }
  
  /**
   * Handle Delete password
   */
  deletePwdHandler() {
    this.showActionModal = true;
    this.deletePassword = true;
    this.isContinueModal = false;
    this.editPassword = false;
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