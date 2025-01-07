/*******************************************************************************************************************************
LWC JS Name : AccountAssociatedFormHum.js
Function    : This JS serves as controller to AccountAssociatedFormHum.html. 

Modification Log: 
Developer Name           Date  (MM/DD/YYYY)                    Description
*--------------------------------------------------------------------------------------------------
* Rahul Krishan          12/18/2020                    initial version(azure # 1614689)
* Ashish Kumar           02/20/2021                    US-1653194 related changes
* Ashish Kumar           02/20/2021                    Refactoring(shifted constant to constsHum.js)
* Supriya Shastri        03/12/2021                    US-1464380
* Ashish Kumar           03/17/2021                    Modifications
* Pallavi Shewale	       07/29/2021		    		         US-2501707 Reusing this component to hide the header section in the humana pharamcy page.
* Mohan                  11/13/2021                    US: 2440592 Member Account Re design
*********************************************************************************************************************************/
import { LightningElement, track, wire, api } from 'lwc';
import getUserInformationDTO from '@salesforce/apex/UserAssociatedInformation_LC_HUM.getUserInformationDTO';
import callServicePHIConsentInfo from '@salesforce/apexContinuation/UserAssociatedInformation_LC_HUM.callServicePHIConsentInfo';
import { getRecord } from 'lightning/uiRecordApi';
import { sortTable, expandAccordianRow, compareDate, getLocaleDate, hcConstants } from 'c/crmUtilityHum';
import { getLabels } from 'c/customLabelsHum';
import USER_ID from '@salesforce/user/Id';
import PROFILE_NAME_FIELD from '@salesforce/schema/User.Profile.Name';
import { NavigationMixin } from 'lightning/navigation';
import pubSubHum from 'c/pubSubHum';
import { CurrentPageReference } from 'lightning/navigation';

export default class AccountAssociatedFormHUM extends NavigationMixin(LightningElement) {
  @api recordId;
  @track oResponse = [];
  @track iCount = 0;
  @track error;
  @track profilename;
  @track isDeceased = false;
  @track isResults = true;
  @track labels = getLabels();
  @track showQuestionList = false;
  @track bShowModal = false;
  @track showActionModal = false;
  @track editQuestion = false;
  @track deleteQuestion = false;
  @track createForm = true;
  @track isContinueModal = false;
  @api buttonsConfig;
  @track modalTitle = "Account Password Notification";
  @track overlayModalTitle = "";
  @track finalResponse;
  @track filterDataResponse = [];
  @track saveClicked = false;
  @track showCreateQuestion = false;
  @track displayheader = true;
  displaysection = true;


  @track accordianFieldSample = {
    ConsentForPHIFull: ['Relationship', 'LevelOfConsent', 'SubmissionMehtod'],
    ConsentForPHILimited: ['Relationship', 'LevelOfConsent', 'SubmissionMehtod', 'ServiceStartDate', 'LineOfCoverage', 'LimitedDisclosureDetails'],
    MedicalOrder: ['GuardianAddress'],
    CommonDocumentType: ['EffectiveDate', 'TerminationDate', 'PersonofAuthority']
  };
  @track accordianFields = [];

  @track accordianFieldLabelMapping = {
    UserValue: hcConstants.PASS,
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

  @wire(CurrentPageReference)
  wiredPageRef(pageRef) {
      this.pageRef = pageRef;
  }

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
    this.displaysection = this.displayheader == false?false:true;
    const me = this;
    me.searchDatabase();
  }
  handledisplaysection(){
    this.displaysection = this.displaysection == true ? false:true;
  }
    // Remove event listener
  disconnectedCallback() {
    document.removeEventListener('click', this.actionListFocusOut);
  }

  filterDocTypeAndFields(data) {
    data.forEach((item) => {
      if (item.DocumentType !== hcConstants.PASS) {
        this.filterResponse(item);
      }
    });
  }

  /**
  * Description - this method will get the integration data related to document Types and also processing related to it
  */
  search() {
    callServicePHIConsentInfo({ sAccountId: this.recordId }).then(data => {
      //method to fire event for POA and populate the oResponse
      this.prepareDataForPOA(data);
      //method to create required keys and making field blank conditionally
      let newResponse = this.createKeyInDocumentForms(this.oResponse);
      this.iCount = newResponse ? newResponse.length : this.iCount;
      //method to filter
      this.filterDocTypeAndFields(newResponse);
      //2nd level sorting for documents with same type on the basis of PersonofAuthority value
      this.oResponse = sortTable(newResponse, 'DocumentType', 'PersonofAuthority', false);
      this.isResults = this.oResponse.length > 0;
      this.finalResponse= this.oResponse;

    })
      .catch(error => {
        console.log('Error', error);
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
          if (key !== 'blankField' && key !== 'Accordian' && key != 'ETL_Record_Deleted__c' && key !== 'iconName' && key !== 'extraField' && key !== 'DocumentType' && key !== 'extraFieldMapping' && key !== 'isDeceased' && key !== 'terminatedForm') {
            item[key] = '';
          }
        }
        this.showCreateQuestion = true;
      }
      else if ((!item.blankField && !item.terminatedForm) && item.DocumentType === hcConstants.PASS) {
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
    if (item) {
      switch (item.DocumentType) {
        case hcConstants.PASS:
          return hcConstants.QUESTION;
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
    this.showQuestionList = !this.showQuestionList;

  }

  /**
 * Description - this method fetches the password from the backend
 */
  searchDatabase(save = false) {      
      getUserInformationDTO({ sAccountId: this.recordId }).then(data => {
      if (data) {
        if (data.length > 0) {
          const record = data[0];
          this.isDeceased = record.DocumentType === hcConstants.PASS ? record.isDeceased : this.isDeceased;
        }
        if(save) {
          this.afterSaveHandler();
        }
      }
      if (!save) {
        this.search();
      }
    })
      .catch(error => {
        console.log('Error at c/accountAssociatedFormHUM - searchDatabase ', error);
      });
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
    pubSubHum.fireEvent(this.pageRef, hcConstants.EVENT_POA_INFO, vPOAData);
  }

  /**
   * Handles accordian open click
   * @param {*} event 
   */
  handleAccordian(event) {
    this.searchDatabase(true);
  }  
}