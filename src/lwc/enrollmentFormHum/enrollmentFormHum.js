/* 
LWC Name        : enrollmentFormHum
Function        : Enrollment Search Form

*Modification Log:
* Developer Name                  Date                         Description
*
* Ritik Agarwal                  10/04/2020                Original Version
* RajKishore                     11/21/2020                add Desiginig
* Supriya Shastri                02/24/2021                Added generic dropdown
* Anil Kumar                     07/30/2021                Added Unknonwn Member Create form Logic
* Supriya Shastri                09/25/2021                US-2548741 Fields retention
* Supriya Shastri                10/12/2021                US-2548741 Fields retention reset and clear functionality
* Supriya Shastri                10/18/2021                DF-3897
* Firoja Begam                   10/27/2021                Enrollment Search Validation rule update
*visweswararao jayavarapu        05/05/2022                US3346025:Account Management -Enrollment Search results & Dual Eligibility status not displaying properly
*visweswararao jayavarapu        03/21/2023                User Story 4376139: T1PRJ0865978 - MF23651 - DF#7248/Enrollment Search Clicks Remediation
*visweswararao jayavarapu		 03/30/2023				   US#4376139 Rollback
*MuthuKumar                      04/04/2023                US 4313675 Enhanced CIM Story changes.
*visweswararao jayavarapu		 04/07/2023				   User Story 4404841: T1PRJ0865978 - MF24206 - Add SSN field in UI and enhance the search capability for CBIS in enrollment search
*MuthuKumar						 04/14/2023				   DF-7529 Fix
*Swarnalina Laha				 06/29/2023				   US-4620636: T1PRJ0865978 - MF26459 - DF7598 - C04/Consumer/CBIS Enrollment search saves data from other application search when switching radio button
*Hima Bindu Ramayanam			 07/03/2023				   User Story 4362885: T1PRJ0865978 - MF24283 - C08, Consumer/Search - RCC - Fast Follow: Add pre-populated dates for Search Enrollment
*/
import { LightningElement, track, api } from 'lwc';
import { getLabels, hcConstants, isSpecialCharsExists, getFormattedDate, isDateValid,toastMsge } from "c/crmUtilityHum";
import getStateValues from '@salesforce/apex/SearchUtilty_H_HUM.getStateValues';
import { ciFormMeta, cbisFormMeta,cimCbisFormMeta, trrFormMeta, appSearchFormMeta } from './enrollFormsMetaData';
import  getSwitchValue from '@salesforce/apex/HUMUtilityHelper_LWC.isCRMFunctionalityONJS';
import HumCIMValidationMsg from '@salesforce/label/c.HumCIMValidationMsg';

export default class EnrollmentFormHum extends LightningElement {

  labels = getLabels();
  @api finalssn;
  @api retainfield;
  @api passinputparam;
  @track loaded = false;
  @track formFields;
  @track passselected;
  @track showAddtionalSearchError = false;
  @track showErrorMessage = false;
  @track showDateComparisonMessage = false;
  @track isFormValid = true;
  @track stateOptions = [];
  @track blankDatesMessage = "";
  @track ssnBlankMessage = "";
  @track stateValue = false;;
  @track createUnknownGrp = false;
  @track isCIMSwitch;
  @track isCBISSwitch;

  isSearchClicked= false;
  isTRRForm = false;
  showTRRmandatFieldsMsg = false;
  hasStartDate = false;
  hasEndDate = false;
  hasFromDate = false;
  hasToDate = false;
  effStartDate = '';
  effEndDate = '';
  isCharValid = true;
  hasEffStartDate = false;
  hasEffEndDate = false;
  formData = {};
  ssnClearInfo = {};
  @track updatedFields;
  formReset = false;
  @track showValidationMsg = false; @track accountList = [];
  @track accountList = [];
  @track hasData =false;
  handleevent(event){
    console.log('Insideevent')
   this.hasData =event.detail.hasData;
    this.accountList=event.detail.accountList;
    const lwcEvent1= new CustomEvent('eventname1', {
      detail:{hasData:this.hasData, accountList : this.accountList} 
     });
    this.dispatchEvent(lwcEvent1);
  }

  handleCreate(){
    this.createUnknownGrp = true;
    this.template.querySelector("c-create-unknown-member").handleModalValueChange();
    console.log('createUnknownGrp:'+this.createUnknownGrp);
  } 
  connectedCallback() {
    getStateValues()
      .then(data => {
        if (data) {
          const options = []
          for (let key in data) {
            options.push({ label: key, value: data[key] });
          }
          this.stateOptions = options;
        }
      })
      .catch(error => {
        console.log("Error--> ", error)
      });
	  const sStoryNumber = ['4313675','4404841'];
	  getSwitchValue({sStoryNumber}).then(result => {
        if(result){ 
          this.isCIMSwitch = result['4313675'];
          this.isCBISSwitch = result['4404841'];
        }
      })
      .catch(error => {
        console.log('error in retrieving switch',error);
      })
  }
  
 @api 
    blankModal(){
     this.loaded = false;
     this.formFields = [];
  }
  
   /**
   * Format date as user enters keys
   * @param {*} event 
   */
  formatDateOnKeyUp(event) {
    const me = this;
    if(event.keyCode === 8 || event.keyCode === 46){ //exclude backspace and delete key
      return;
    }
    let { value: dtValue, name: fielddKey } = event.target;
    dtValue = getFormattedDate(dtValue);
    event.target.value = dtValue;  
    if(isDateValid(dtValue)){
      me.formData[fielddKey] = dtValue;
    }
  }

  /**
   * Validates Date field and displays warning message if required
   * @param {*} field 
   */
  reportDateValidation(field) {
    const me = this;
    const dateValue = field.value;
    if(dateValue !== ""){
      const isDtValid = isDateValid(dateValue);
      me.updateValidationField(field, isDtValid ? "" : me.labels.HumStartnEndDate);
      return isDtValid;
    }
    return true
  }

  /**
   * Validate date format and trigger remove highlight method
   * @param {*} event 
   */
  onDateFieldBlur(event){
    this.reportDateValidation(event.target);
    this.removehighlight();
  }

  /**
   * Validate All the date fields
   * @param  {Object} dateFields 
   */
  validateDateFieldsFormat(dateFields){
    const me = this;
    return dateFields.every(field => me.reportDateValidation(field));
  }

  @api
  formatdata(selectedSystem, dataRetained) {
    const me = this;
    this.loaded = true;
    this.passselected = selectedSystem;
    let formFields;
    let isSsnForm = false;
    this.formFields = [];
    this.formData = {};
    const {TES, CBIS, CIM, MARKETSEARCH, AUTOENROLL, TRR, APPSEARCH} = hcConstants;
    this.reset();
    switch(selectedSystem) {
        case CBIS:
          if(this.isCBISSwitch){
            formFields = me.getPrePopulatedForm(cimCbisFormMeta, false);
			isSsnForm = true; 
            break;
          }else{
            formFields = me.getPrePopulatedForm(cbisFormMeta, false);
            break;
          }
          
        case TES: 
        case MARKETSEARCH:
        case AUTOENROLL:
        //case CIM:
          formFields = me.getPrePopulatedForm(ciFormMeta, false);
          isSsnForm = true;
          break;
        case TRR:
          formFields = me.getPrePopulatedForm(trrFormMeta, false);
		  break;
        case APPSEARCH:
          formFields = me.getPrePopulatedForm(appSearchFormMeta, false);
          break;
		case CIM:
          if(this.isCIMSwitch){
            formFields = me.getPrePopulatedForm(cimCbisFormMeta, false);
            isSsnForm = true;
            break;
          }else{
            formFields = me.getPrePopulatedForm(ciFormMeta, false);
            isSsnForm = true;
            break;
          }
      default:
    }
    
    // Adding setTimeout to wait for clearing previous form
    setTimeout(() =>{
      this.formFields = formFields.map(item => ({
        ...item,
        size: item.size ? item.size : 12
      }));
      if(me.finalssn != "" && isSsnForm && !me.ssnClearInfo[me.passselected]){
        //Adding timeout to wait for DOM to render elements with data, then fire on Search event
        setTimeout(() => {
           me.onSearchClick(false);
        }, 1);
      }
    }, 100)
    
  }

  /**
   * Fires custom event on change of field value
   * @param {*} event 
   */
  handleFieldChange(event) {
    if (event.target.value.length > 0) {
      this.updatedFields = { ...this.updatedFields, [event.target.name]: event.target.value  };
    } else {
      this.updatedFields = { ...this.retainfield }
    }
  }

  getPrePopulatedForm(formFields, retainfield) {
    const me = this;
    if (retainfield) {
      formFields.forEach(modal => {
        switch (modal.fieldName) {
		  case 'sEffectiveDate':
          case 'sEndDate':
		  case 'sEffectiveDateFrom':
          case 'sEffectiveDateTo':
          case 'sReceivedDateFrom':
          case 'sReceivedDateTo':
          case 'Hum_ReceivedDateFrom':
          case 'Hum_ReceivedDateTo':
            modal.val = retainfield[modal.fieldName] ? retainfield[modal.fieldName] : me.prefillFieldValue(modal.fieldName);
            break;
          case 'DOB':
          case 'fName':
          case 'lName':
            modal.val = retainfield[modal.fieldName]
           break;
           case 'sSSN':
             modal.val = me.finalssn ? me.finalssn : retainfield.sSSN;
             me.finalssn = null;
             break;
        }
      });
      return formFields;
    }
    else {
      if (me.formReset) {
        return formFields.map(item => ({
          ...item,
          val:  me.prefillFieldValue(item.fieldName)
        }))
      } else {
        return me.ssnClearInfo[me.passselected] ? formFields : formFields.map(item => ({
          ...item,
          val: item.fieldName === 'sSSN' ? me.finalssn : 
		  me.prefillFieldValue(item.fieldName)
        }));
      } 
    }
  }
  
  prefillFieldValue(item){
    var itemValue;
    var preFillStartDate = new Date();
    var preFillEndDate= new Date() ; 
    preFillStartDate.setDate(preFillStartDate.getDate()-90);

    itemValue = item === 'sEffectiveDateFrom' || item === 'sReceivedDateFrom' || item === 'Hum_ReceivedDateFrom' || item === 'sEffectiveDate'? this.formatmmddyyyy(preFillStartDate) : //start date
                item === 'sEffectiveDateTo' || item === 'sReceivedDateTo' || item === 'Hum_ReceivedDateTo'|| item === 'sEndDate' ? this.formatmmddyyyy(preFillEndDate) :''
      return itemValue;
  }

  formatmmddyyyy(date){
    function pad(n) {return (n<10 ? '0'+n : n);}
    return [pad(date.getMonth() + 1), pad(date.getDate()), date.getFullYear()].join('/');
  }

  clearSsn() {
    this.ssnClearInfo[this.passselected] = true;
  }

  /**
   * Return carried over fields
   * to parent container
   */
  @api
  getRetainedFields() {
    return this.updatedFields;
  }

  /**
   * update data on field change
   * @param {*} evnt 
   */
  onFieldChange(evnt) {
    const {name, value, required} = evnt.target
    this.formData[name] = value === hcConstants.OPTION_NONE ? "": value;
    if (value.length > 0 && (evnt.keyCode !== 8 || evnt.keyCode !== 46)) {
      this.updatedFields = { ...this.updatedFields, [name]: value };
    } else {
      this.updatedFields = { ...this.retainfield};
    }
    if (value.length <1) {
      this.isFormValid = false;
      delete this.formData[name];
    } 
  }

  /**
   * Handle state chagne
   * @param {*} event 
   */
  stateSelectionHandler(event) {
    let stateVal = event.detail.value;
    this.formData.sState = stateVal;
    if(stateVal.length) {
      this.stateValue = true;
	  this.showValidationMsg=false;
    } else {
      this.formData.sState = "";
    }
  }

  onSearchClick(Prevstatus) {
    const me = this;
    me.isTRRForm = false;
	if(Prevstatus==false){ 
      me.validateInputField(false);
    } else{
    me.validateInputField(true);
    }
    if(me.isFormValid) {
      const clickev = new CustomEvent('clickev', {
        detail: { passselected: this.passselected, data: me.formData }
      });
      this.dispatchEvent(clickev);
    } else {
      me.clearSearchResults();
    }
  }

  clearLayouts() {
    this.updatedFields = null;
    this.formFields.forEach (field=> {
      field.val = this.prefillFieldValue(field.fieldName);
    });
    this.dispatchEvent(new CustomEvent('resetinputs'));
  }


  reset(event) {
    const me =this;
    const inputs = me.template.querySelectorAll('.inputfield');
    const gDropDown = this.template.querySelector("c-generic-drop-down-hum");

    if(event && event.target){ // check for reset button click
      me.clearSsn();
      me.formReset = true;
      me.clearLayouts();
    }
    inputs.forEach(function (item) {
      item.value = me.prefillFieldValue(item.name);
      item.required = false;
      me.updateValidationField(item, "");
    })
    gDropDown && gDropDown.reset();
    me.formData = {};
    me.clearSearchResults();
    me.clearFooterErrorMesgs();
    me.stateValue = false;
    me.showValidationMsg = false;
  }

  clearSearchResults() {
    const resetSarchEnrollmentEvent = new CustomEvent('resetsearchenrollment', {
      detail: { passselected: this.passselected, data: this.formData }
    });
    this.dispatchEvent(resetSarchEnrollmentEvent);
  }

  clearStateHandler() {
    this.formData.sState = "";
    this.stateValue = false;
  }

  highlightMandatoryFields(event) {
    const field = event.target;
    const fieldName = field.name;
    switch(fieldName){
      case 'fName':
      case 'lName':
      case 'DOB':
      case 'sSSN':
      case 'sEffectiveDate':
      case 'sEndDate':
        this.highlightFields();
        break;
      default:
    }
  }

 removehighlight() {
    //For Field Highlight Seperate function to be called for CIM/AE/TES/H1M
    const {TES, CBIS, CIM, MARKETSEARCH, AUTOENROLL, TRR, APPSEARCH} = hcConstants;
    if(this.isCIMSwitch){
      if(this.passselected === AUTOENROLL || this.passselected === TES || this.passselected === MARKETSEARCH){
        this.toggleFieldHighlightsCI('.mandatoryHighlight', false)
      }else{
      this.toggleFieldHighlights('.mandatoryHighlight', false)
     }
    }else{
      if(this.passselected === CIM || this.passselected === AUTOENROLL || this.passselected === TES || this.passselected === MARKETSEARCH){
        this.toggleFieldHighlightsCI('.mandatoryHighlight', false)
      }else{
      this.toggleFieldHighlights('.mandatoryHighlight', false)
     }
    }
  }

  validateInputField(fromSearch, field){
    const {TES, CBIS, CIM, MARKETSEARCH, AUTOENROLL, TRR, APPSEARCH} = hcConstants;
    switch(this.passselected) {
      case CBIS:
        if(this.isCBISSwitch){
          this.validateCIMCBISForm(fromSearch, field);
        }else{
          this.validateCBISInputField();
        }  
      break;
      case APPSEARCH:
        this.validateAppSearchInputField(fromSearch, field);
        break;
      case TRR:
        this.validateTRRInputField(fromSearch, field);       
       break; 
      case TES:
      case MARKETSEARCH:
      case AUTOENROLL:
        this.validateCIForm(fromSearch, field)
        break;
		case CIM:
        if(this.isCIMSwitch){
          if(fromSearch){
            this.validateCIMCBISForm(fromSearch, field);
          }else{
            this.isFormValid = false;
          }
        }else{
          this.validateCIForm(fromSearch, field);
        }
        break;
      default:
    }
  }
	validateCIMCBISForm(fromSearch){
	const me = this;
	me.formData['isCIMSwitch'] = this.isCIMSwitch;
	me.formData['isCBISSwitch'] = this.isCBISSwitch;
	console.log('##--me.formData in validateCIMForm :'+JSON.stringify(me.formData ));
	const { HUM_SSN_Invalid_Message,HUM_SSN_Invalid_Digits_Message } = me.labels;

	var isSsnValid = true;
	var isDemographiValid = true;
	var ssnValue;
	var firstNameValue = me.template.querySelector(".cim-cbis-fn")?.value;
	var lastNameValue = me.template.querySelector(".cim-cbis-ln")?.value;
	var dobValue = me.template.querySelector(".cim-cbis-dob")?.value;
	const dobField = me.template.querySelector(".birthdate-input");
	const ssnField = me.template.querySelector(".cim-cbis-SSN");
	const reSSN = new RegExp(/^[0-9]+$/);
	var dobValidation = me.reportDateValidation(dobField) ? true: false;
	this.updateValidationField(ssnField, "");
	me.toggleFieldHighlights('.cim-cbis-input', false);
	me.showValidationMsg = false;


	//none of the field values entered
	if(!ssnField?.value && !firstNameValue && !lastNameValue && !dobValue && !me.stateValue){
	this.showValidationMsg = false;
	this.isFormValid = false;
	toastMsge('Error',HumCIMValidationMsg,'error','dismissable');
	}// ssn enter and any  other  demographic field values enteres
	else if(ssnField?.value && (firstNameValue || lastNameValue || dobValue || me.stateValue)){
	this.isFormValid = false;
	this.showValidationMsg = false;
	toastMsge('Error',HumCIMValidationMsg,'error','dismissable');
	}
	else if(ssnField.value){
	if(ssnField.value.length !== 9 || !reSSN.test(ssnField.value)){
	  this.updateValidationField(ssnField, HUM_SSN_Invalid_Digits_Message);
	  this.isSsnValid = false;
	  this.isFormValid = false;
	}else{
	  this.isSsnValid = true;
	  this.isFormValid = true;
	}
	}
	else if(!ssnField?.value && (!firstNameValue || !lastNameValue || !dobValue || !me.stateValue || !dobValidation)){
	this.showValidationMsg = !me.stateValue?true:false;
	me.toggleFieldHighlights('.cim-cbis-input', true);
	this.isFormValid = false;
	}else{
	this.showValidationMsg = false;
	this.isFormValid = true;
	}
	console.log('isFormvalid',this.isFormValid);
	}
  validateCIForm(fromSearch){
    const me = this;    
    const startDteField = me.template.querySelector(".effect-start-date");
    const endDateField = me.template.querySelector(".effect-end-date");
    const { HUM_SSN_Invalid_Message, HumStartnEndDate, HUM_SSN_Invalid_Digits_Message } = me.labels;    
    me.updateValidationField(startDteField, "");
    me.updateValidationField(endDateField, "");
    me.blankDatesMessage = "";
    me.ssnBlankMessage = "";
    me.showErrorMessage = false;
    const reSSN = new RegExp(/^[0-9]+$/);
    let isMarketFormValid = true;
    if(fromSearch){
      const ssnField = me.template.querySelector(".ssn-id");
      this.updateValidationField(ssnField, "");
      if(ssnField.value !== ""){
        if(ssnField.value.length !== 9 || !reSSN.test(ssnField.value)){
          this.updateValidationField(ssnField, HUM_SSN_Invalid_Digits_Message);
          isMarketFormValid = false;
        }
        else if(!me.validateDateFieldsFormat([startDteField, endDateField])){ //check for invalid date formats
          isMarketFormValid = false;
        }
        else{
          if(!me.verifyDateTimeline('sEffectiveDate', 'sEndDate')){
            isMarketFormValid = false;
          }
        }        
      }
      else{
        isMarketFormValid = false;
        if(!startDteField.value || !endDateField.value){
          me.ssnBlankMessage = me.labels.HUM_SSN_Invalid_Message;
          if(!startDteField.value && me.reportDateValidation(startDteField) || 
            !endDateField.value && me.reportDateValidation(endDateField)){
          }
        }
        else{
          me.updateValidationField(ssnField, HUM_SSN_Invalid_Message); 
        }               
      }
    }
    me.isFormValid = isMarketFormValid;
  }

  validateCBISInputField(){
    const me = this;
    me.toggleFieldHighlights('.cbis-input', true);
    const dobField = me.template.querySelector(".birthdate-input");
    this.template.querySelectorAll('.cbis-input').forEach((field) => {
      if (!field.value && !me.stateValue) {
        me.showValidationMsg = true;
        me.isFormValid = false;
      } else if(!me.stateValue) {
        me.showValidationMsg = true;
        me.isFormValid = false;
      } else if(field.name === "DOB") {
        me.isFormValid = me.reportDateValidation(dobField) ? true: false;
      } else {
        me.showValidationMsg = false;
        me.isFormValid = true;
      }
    });
  }

  isValidCombofield() {
    const me = this;
    const comboFields = [...me.template.querySelectorAll(".combo-field")];
    let validFields = comboFields.every((field) => {
      return field.value !== '' && field.value !== null;
    })
    return validFields;
  }

  validateTRRInputField(fromSearch, field){
    const me = this;
    if(fromSearch){
      me.isTRRForm = true;
      let isTrrFormValid = true;
      let isValidMedicareId = false;
      me.clearFooterErrorMesgs();
      const startDate = this.formData['Hum_ReceivedDateFrom'];
      const endDate = this.formData['Hum_ReceivedDateTo'];

      const medicareFields = me.template.querySelector(".medicare-id");
      const fNameFields = me.template.querySelector(".firstname");
      const lNameFields = me.template.querySelector(".lastname");
      const dobField = me.template.querySelector(".birthdate-input");
      const isInValidFLLNDob = [fNameFields.value, lNameFields.value, dobField.value].some(item => item !== "");
      this.updateValidationField(medicareFields, "");
      this.updateValidationField(fNameFields, "");
      this.updateValidationField(lNameFields, "");
      let validFName = this.verifySpecialChars(fNameFields, this.labels.AM_Search_Invalid_Name_Error);
      let validLName = this.verifySpecialChars(lNameFields, this.labels.AM_Search_Invalid_Name_Error);
      
      if (medicareFields.value) {
        isTrrFormValid = isValidMedicareId = this.verifyFieldLengthRange(medicareFields, 9, 13, this.labels.enrollmentSearchFormMedicareIDError);
      }

      // All fields Empty
      if (!medicareFields.value && !this.isValidCombofield() && !startDate && !endDate) {
        me.showTRRmandatFieldsMsg = true;
        isTrrFormValid = false;
      }
      else if (this.isValidCombofield()) { // FN, LN, DOB Valid
        if (!validFName || !validLName) {
          isTrrFormValid = false;
        } 
      } // FN or LN or DOB exists along with medicare id
      else if(isInValidFLLNDob && isValidMedicareId){
        isTrrFormValid = false;
        me.showTRRmandatFieldsMsg = true;
      }
      else if(startDate && endDate){ 
        if(!isValidMedicareId){// startdate and enddate and no medicare id
          isTrrFormValid = this.verifyNoAdditionalInputs('Hum_ReceivedDateFrom', 'Hum_ReceivedDateTo', field);
        }
        if(isInValidFLLNDob){
          isTrrFormValid = false;
          me.showTRRmandatFieldsMsg = true;
        }
        const validDates = this.verifyDateTimeline('Hum_ReceivedDateFrom', 'Hum_ReceivedDateTo');
        isTrrFormValid = isTrrFormValid ? validDates: isTrrFormValid;
      }

      const dateFields = [...me.template.querySelectorAll(".date-field")];
      const validDtFields = me.validateDateFieldsFormat(dateFields);

      if(!validDtFields){
        isTrrFormValid = false; // if Date is invalid
      }
      me.isFormValid = isTrrFormValid;
    }
  }

  validateAppSearchInputField(fromSearch, field) {
    const me = this;
    
    if (fromSearch) {
      me.clearFooterErrorMesgs();
      const medicareFields = me.template.querySelector(".medicare-id");
      const medicaidFields = me.template.querySelector(".medicaid-id");
      const fNameFields = me.template.querySelector(".firstname");
      const lNameFields = me.template.querySelector(".lastname");
      const dobField = me.template.querySelector(".birthdate-input");
      
      const {sEffectiveDateFrom, sEffectiveDateTo, sReceivedDateFrom, sReceivedDateTo, sBarCode,
        sOECConfirmationId, sApplicationId} = me.formData;
      const dateFieldsValues = [sEffectiveDateFrom, sEffectiveDateTo, sReceivedDateFrom, sReceivedDateTo];
      const isAllDatesEmpty = dateFieldsValues.every(item => item == '' || item == null || item == undefined );
      const isSomeDateEntered = dateFieldsValues.some(item => item != '' && item!== null && item !== undefined );

      this.updateValidationField(medicareFields, "");
      this.updateValidationField(medicaidFields, "");
      this.updateValidationField(fNameFields, "");
      this.updateValidationField(lNameFields, "");
      let validFName = me.verifySpecialChars(fNameFields, me.labels.AM_Search_Invalid_Name_Error);
      let validLName = me.verifySpecialChars(lNameFields, me.labels.AM_Search_Invalid_Name_Error);
      let isAppFormValid = true;
      let isValidMedicareId = false;
      let isValidMediCIid = false;
      const isAllFnLnEmpty = [fNameFields.value, lNameFields.value, dobField.value].every(item => item === "");
      const isSomeFnLnEntered = [fNameFields.value, lNameFields.value, dobField.value].some(item => item !== "");
      const isAllFnLnDobEnterd = [fNameFields.value, lNameFields.value, dobField.value].every(item => item !== "");
      
      if (medicareFields.value) {  // check for valid medicare id
        isValidMedicareId = this.verifyFieldLengthRange(medicareFields, 9, 13, this.labels.enrollmentSearchFormMedicareIDError);
        if(!isValidMedicareId){
          isAppFormValid = false;
        }
      }
      if (medicaidFields.value) { // check for valid medicaid
        isValidMediCIid =this.verifyFieldLengthRange(medicaidFields, 2, 21, this.labels.AM_Medicaid_Error);
        if(!isValidMediCIid){
          isAppFormValid = false;// If Medicaid is not valid
        }
      }

      const isFnLnDobEntered = this.isValidCombofield();

      if (isFnLnDobEntered) {
        if (!validFName && !validLName) {
          isAppFormValid = false; // first name and last name invalid
        }
      }

      if(!medicareFields.value && !medicaidFields.value && !sApplicationId
        && !sOECConfirmationId && !sBarCode && isAllFnLnEmpty && me.reportDateValidation(dobField) && isAllDatesEmpty){
          me.showErrorMessage = true;
          isAppFormValid = false; // All Fields Blank Scenario
      }
      else if(isSomeFnLnEntered && !isAllFnLnEmpty && !isAllFnLnDobEnterd && (!medicareFields.value && !medicaidFields.value 
        && !sApplicationId && !sOECConfirmationId && !sBarCode)){
        isAppFormValid = false; // first name and lastname and dob invalid
        if(!fNameFields.value || !lNameFields.value || (!dobField.value && me.reportDateValidation(dobField))){
          me.showTRRmandatFieldsMsg = true;
        }
      }
      
      if (isSomeDateEntered && !isAllDatesEmpty) {
        const isOtherFiedlsValid = this.checkAllEmptyFields(field, '.formfield'); 
        if(isAppFormValid){
          isAppFormValid = isOtherFiedlsValid; // If other fields not entered
        }
      }

      
      const isEffectiveDtValid = this.verifyDateTimeline('sEffectiveDateFrom', 'sEffectiveDateTo');
      const isRecevedDtValid = this.verifyDateTimeline('sReceivedDateFrom', 'sReceivedDateTo');
      if(!isEffectiveDtValid || !isRecevedDtValid){ // Dates start and end dates mismatch
          isAppFormValid = false;
          me.showDateComparisonMessage = true;
      }

      const dateFields = [...me.template.querySelectorAll(".date-field")];
      const validDtFields = me.validateDateFieldsFormat(dateFields);
      if(!validDtFields){
        isAppFormValid = false;  // Invalid date format case. Error will be shown by LWC
      }
      
      me.isFormValid = isAppFormValid;
    }
  }


  checkAllEmptyFields(field,selector){
    const inputFields = this.template.querySelectorAll(selector);
    let showError = true;
    inputFields.forEach((inputField)=>{
      if(inputField.value !== ''){
        showError = false;
      }
    });

    if(showError){
      this.showAddtionalSearchError = true;
      this.showTRRmandatFieldsMsg = false;
      this.showErrorMessage = false;
      this.isFormValid = false;
      return false;
    }else{
      this.showAddtionalSearchError = false;
      this.showDateComparisonMessage = false;
      this.isFormValid = true;
    }
    return true;
  }


  updateValidationField(field, message){
    field.setCustomValidity(message);
    field.reportValidity();
  }

  verifyInputField(value, regex){
    if(!regex.test(value)){
      this.isFormValid = false;
      return true;
    }else{
      return false;
    }
  }

  verifyFieldLengthRange(field, min, max, message) {
    if ((field.value.length > min) && (field.value.length < max)) {
      this.updateValidationField(field, '');
      return true;
    } else {
      this.updateValidationField(field, message);
      return false;
    }
  }

  verifySpecialChars(field, message) {
    if (isSpecialCharsExists(field.value)) {
      this.updateValidationField(field, message);
      this.isFormValid = false;
      return false;
    } else {
      this.updateValidationField(field, '');
      return true;
    }
  }

  highlightFields(event) {
    const me = this;
    me.clearFooterErrorMesgs();
    me.toggleFieldHighlights(".mandatoryHighlight", true);
  }

  /**
   * Clear stacked messages in footer
   */
  clearFooterErrorMesgs(){
    const me = this;    
    me.showErrorMessage = me.showAddtionalSearchError = me.showTRRmandatFieldsMsg = me.showDateComparisonMessage = false;
    me.blankDatesMessage = me.ssnBlankMessage = "";
  }

  toggleFieldHighlights(selector, bReequired){
    let inp = this.template.querySelectorAll(selector);
    inp.forEach(function (element) {
      element.required = bReequired;
      element.reportValidity();
    }, this);
  }

  /**
   * Highlight fields based on combined value of ssn, effectivedate and enddate for CIM/AE/TES/H1M
   */

  toggleFieldHighlightsCI(selector, bReequired){
    const sSSNValue = this.template.querySelector(".ssn-id");
    const effectiveDateValue = this.template.querySelector(".effect-start-date");
    const endDateValue = this.template.querySelector(".effect-end-date");
      let inp = this.template.querySelectorAll(selector);
      inp.forEach(function (element) {
        element.required = (sSSNValue.value || effectiveDateValue.value || endDateValue.value) ? true : false;
        element.reportValidity();      
      }, this);
    }

  verifyNoAdditionalInputs(startDate, endDate, field) {
    const  hasEffStartDate = (this.formData[startDate]) ? true : false;
    const hasEffEndDate =  (this.formData[endDate]) ? true : false;
    if(hasEffEndDate && hasEffStartDate) {
        this.checkAllEmptyFields(field,'.formfield');
    } else {
      this.showAddtionalSearchError = false;
      this.showDateComparisonMessage = false;
    }
  }

  verifyDateTimeline(startDate,endDate) {
      this.hasEffStartDate = (this.formData[startDate]) ? true : false;
      this.hasEffEndDate =  (this.formData[endDate]) ? true : false;
    if(this.hasEffEndDate && this.hasEffStartDate) {
      let sDate = new Date(this.formData[startDate]);
      let eDate = new Date(this.formData[endDate]);
     
      if((sDate.getTime() > eDate.getTime())) {
        this.showDateComparisonMessage = true;
        this.isFormValid = false;
        return false;
      } else {
        this.showDateComparisonMessage = false;
      }
    }
    return true;
  }
}