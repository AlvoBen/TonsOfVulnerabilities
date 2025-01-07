/*******************************************************************************************************************************
LWC JS Name : coachingMemberSearchFormHum.js
Function    : This LWC component serves as input for to search member data in Coaching

Modification Log: 
Developer Name           Code Review              Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Jasmeen Shangari                              03/08/2021                   Member Search init version
* Mohammed Noor                                 06/04/2021                   US2081412 - Ability to Create Unknown Member Accounts
*********************************************************************************************************************************/
import { LightningElement, track, api, wire } from "lwc";
import getCustomInputFieldPermission from '@salesforce/apex/CoachMemberSearch_C_HUM.getCustomFieldsToDisplay';
import saveForm from '@salesforce/apex/CoachMemberSearch_C_HUM.searchRecords';
import startRequest from '@salesforce/apexContinuation/CoachMemberSearch_C_HUM.getInvokeMBEPlusService2';
import getStateValues from '@salesforce/apex/CoachMemberSearch_C_HUM.getStateValues';
import getPolicyList from '@salesforce/apex/CoachMemberActiveFuturePolicies_C_HUM.determinePolicyAccess';
import hasCRMS_205_CCSPDPPharmacyPilot from '@salesforce/customPermission/CRMS_205_CCSPDPPharmacyPilot';

//import hasCRMS_240_GBO_Segment_Service_Access from '@salesforce/customPermission/CRMS_240_GBO_Segment_Service_Access';
//Commenting this to resolved the Deployemnt Error. Dt:9-Jan-23 

import hasCRMS_302_HPTraditionalInsuranceData from '@salesforce/customPermission/CRMS_302_HPTraditionalInsuranceData';
import hasCRMS_684_Medicare_Customer_Service_Access from '@salesforce/customPermission/CRMS_684_Medicare_Customer_Service_Access';
import { CurrentPageReference } from 'lightning/navigation';
import { ShowToastEvent } from 'lightning/platformShowToastEvent';
import { getLabels, isSpecialCharsExists, getLocaleDate, hcConstants, getUserGroup } from "c/coachUtilityHum";
import { getPolicyLayout } from './coachingMemberSearchPolicyModals';

export default class coachingMemberSearchFormHum extends LightningElement {
    @api noRecordlabel;
    @api inputType = 'Input';
    @api screenType = 'MemberSearch';
    @api get suffixpermission() {
        return this.inputFieldSuffix;
    }

    @api get groupNumberPermission() {
        return this.inputFieldGroupNumber;
    }

    @api get pidPermission() {
        return this.inputFieldPid;
    }

    @track showBackToResults = false;
    @track stateOptions = [];
    @track showErrorMsg = false;
    @track isFormValid = true;
    @track labels = getLabels();
    @track oUserGroup = getUserGroup();
    @track newpolicy = [];

    @wire(CurrentPageReference) pageRef;

    sMemberid = '';
    sSuffix = '';
    sBirthdate = '';
    sPhone = '';
    sGroupNumber = '';
    sPID = '';
    sState = '';
    sPostalCode = '';
    rxNumber = '';
    sFirstName = '';
    sLastName = '';
    hasData = false;
    showNoDataView = false;
    resultsTrue = true;
    accountList = [];
    inputFieldSuffix;
    inputFieldGroupNumber;
    inputFieldPid;
    policyList;
	boolTrue = true;
    boolFalse = false;

    @track formData = {
        sMemberid: this.sMemberid,
        sSuffix: this.sSuffix,
        sBirthdate: this.sBirthdate,
        sPhone: this.sPhone,
        sGroupNumber: this.sGroupNumber,
        sPID: this.sPID,
        sState: this.sState,
        sPostalCode: this.sPostalCode,
        rxNumber: this.rxNumber,
        sFirstName: this.sFirstName,
        sLastName: this.sLastName,
        sUnknownMemCheck: false
    };

    connectedCallback() {
        getCustomInputFieldPermission({ sInput: this.inputType, sScreenType: this.screenType }).then(result => {
            const currentDate = new Date();
            const strResult = JSON.stringify(result);
            this.inputFieldSuffix = strResult.includes("Suffix");
            this.inputFieldGroupNumber = strResult.includes("groupNumber");
            this.inputFieldPid = strResult.includes("PID");
            this.today = currentDate.getFullYear() + '-' + (currentDate.getMonth() + 1) + '-' + (currentDate.getDate() > 9 ? currentDate.getDate() : '0' + currentDate.getDate());
        }).catch(error => {
            console.log("Error Occured", error);
        });
        getStateValues().then(data => {
            if (data) {
                for (let key in data) {
                    const opt = { label: key, value: data[key] };
                    this.stateOptions = [...this.stateOptions, opt];
                }
            }
        }).catch(error => {
            console.log("Error Occured", error);
        });
    }

    /**
     * Handle state chagne
     * @param {*} event 
     */
    handleChangeStateValue(event) {
        const { value } = event.detail;
        if (value !== "" && value !== hcConstants.OPTION_NONE) {
            this.highlightFields();
            this.formData.sState = value;
        } else {
            this.formData.sState = "";
            this.removehighlight();
        }
    }

    /**
     * update data on field change
     * @param {*} evnt 
     */
    onFieldChange(evnt) {
        const fieldKey = evnt.currentTarget.getAttribute('data-id');
        this.formData[fieldKey] = evnt.target.value;
    }

    /**
     * Update Birthdate
     */
    updateBirthDate(event) {
        let birthVal = event.target.value;
        if (birthVal) {
            const arr = birthVal.split('-');
            birthVal = `${arr[1]}/${arr[2]}/${arr[0]}`;
            this.formData.sBirthdate = birthVal;
            this.highlightFields();
        }
        else {
            this.formData.sBirthdate = "";
        }
    }

    getpermissioncheck(res, policiesMeta) {
        const me = this;
        getCustomInputFieldPermission({ sInput: 'Result', sScreenType: 'Policy' }).then(result => {
            const strResult = JSON.stringify(result);
            let policy = policiesMeta[0];
            let filter = result;
            let arrayOfIndex = [];
            policy.forEach(function (pol, index) {
                let match = 'false'
                filter.forEach(function (flt) {
                    if (pol.hasOwnProperty('label')) {
                        if (pol['label'].toLowerCase() === flt.toLowerCase()) {
                            arrayOfIndex.push(index);
                            match = 'true';
                        }
                    }
                });
                if (match === 'false') {
                }
            });

            let newPolicyTemp = [policiesMeta[0][0]];
            arrayOfIndex.forEach(function (item) {
                newPolicyTemp.push(policy[item]);
            });
            if (newPolicyTemp.length) {
                this.newpolicy.push(newPolicyTemp);
            }
            else {
                this.newpolicy = policiesMeta;
            }

            //moved
            var response = JSON.parse(JSON.stringify(res));
            response.forEach(function (item) {
                item.EffectiveFrom = getLocaleDate(item.EffectiveFrom);
                item.EffectiveTo = getLocaleDate(item.EffectiveTo);
                item.PlanName = (item.Plan && item.Plan.Name) ? item.Plan.Name : '';
                item.MedicareId__c = item.Member.MedicareId__c;
                item.Name = (item.Member_Id_Base__c && item.Member_Dependent_Code__c) ? (item.Member_Id_Base__c + '-' + item.Member_Dependent_Code__c) : item.Name;
            })
            this.policyList = me.processPolicyData(this.sortTable(response));
        }).catch(error => {
            console.log("Error Occured", error);
        });
    }
    /**
     * Handles record click interaction
     * @param {*} event 
     */
    handleInteraction(event) {
        this.showBackToResults = true;
        const accId = event.detail.Id;
        getPolicyList({ sAccId: accId })
            .then(res => {
                if (res.length > 0) {
                    this.getpermissioncheck(res, getPolicyLayout(this.oUserGroup));
                } else {
                    this.noPolicyData = false;
                }
            }).catch(err => {
                console.log("Error Occured", error);
            });
    }

    /**
     * Process Policy Data
     * @param {*} dataObj 
     */
    processPolicyData(dataObj) {
        const me = this;
        let data;
        if (hasCRMS_205_CCSPDPPharmacyPilot || hasCRMS_684_Medicare_Customer_Service_Access) {
            data = me.updatePreselectForCCS(dataObj);
        }
        else if (hasCRMS_302_HPTraditionalInsuranceData) {
            data = me.updatePreselectForHPS(dataObj);
        }
        else {
            data = dataObj;
        }
        return data;
    }

    /**
     * Apply HPS role rules and return status as true or false
     * @param {*} iMedPolicy 
     * @param {*} iActiveMedPloicy 
     * @param {*} iFutureMedPoly 
     */
    isHPSrulesApply(iMedPolicy, iActiveMedPloicy, iFutureMedPoly, item) {
        let checkedStatus = false;
        const { MED_POLICY, STATUS_FUTURE, STATUS_ACTIVE } = hcConstants;
        if (iMedPolicy === 1 && item.Product__c === MED_POLICY ||
            iActiveMedPloicy === 1 && item.Product__c === MED_POLICY && item.Member_Coverage_Status__c === STATUS_ACTIVE ||
            iFutureMedPoly === 1 && iActiveMedPloicy !== 1 && item.Product__c === MED_POLICY && item.Member_Coverage_Status__c === STATUS_FUTURE) {
            checkedStatus = true;
        }
        return checkedStatus;
    }

    /**
     * Pre Select Record for the user with role : Humana Pharmacy Specialist
     * @param {*} data 
     */
    updatePreselectForHPS(data) {
        let iActiveMedPloicy = 0;
        let iFutureMedPoly = 0;
        let iMedPolicy = 0;
        const { MED_POLICY, STATUS_FUTURE, STATUS_ACTIVE } = hcConstants;
        data.forEach(item => {
            if (item.Product__c === MED_POLICY) {
                iMedPolicy++;
            }

            if (item.Product__c === MED_POLICY && item.Member_Coverage_Status__c === STATUS_ACTIVE) {
                iActiveMedPloicy++;
            }

            if (item.Product__c === MED_POLICY && item.Member_Coverage_Status__c === STATUS_FUTURE) {
                iFutureMedPoly++;
            }
        })

        return data.map(item => {
            return {
                ...item,
                checked: this.isHPSrulesApply(iMedPolicy, iActiveMedPloicy, iFutureMedPoly, item),
                disabled: item.Product__c !== MED_POLICY
            }
        });
    }

    /**
     * Pre select records for the user with role : Customer Care Supervisor
     * @param {*} data 
     */
    updatePreselectForCCS(data) {
        const { MED_POLICY, STATUS_ACTIVE } = hcConstants;
        const activeMed = data.filter(item => item.Product__c === MED_POLICY && item.Member_Coverage_Status__c === STATUS_ACTIVE);

        if (activeMed.length === 1) {
            return data.map(item => {
                return {
                    ...item,
                    checked: item.Product__c === MED_POLICY && item.Member_Coverage_Status__c === STATUS_ACTIVE
                }
            })
        }
        return data;
    }

    sortTable(data) {
        let aarr = data.sort(function (a, b) {
            var afirstCol = a['Member_Coverage_Status__c'];
            var bfirstCol = b['Member_Coverage_Status__c'];
            var aSecondCol = a['Product__c'];
            var bSecondCol = b['Product__c'];
            if (afirstCol === bfirstCol) {
                var key1 = aSecondCol.toUpperCase();
                var key2 = bSecondCol.toUpperCase();
                let preSortedArray = ['MED', 'DEN', 'VIS'];
                let index1, index2;
                index1 = preSortedArray.indexOf(key1);
                index2 = preSortedArray.indexOf(key2);
                index1 = index1 === -1 ? 100 : index1;
                index2 = index2 === -1 ? 100 : index2;
                if (index1 < index2) {
                    return -1
                } else if (index1 > index2) {
                    return 1;
                } else {
                    return 0;
                }
            }
            else {
                return (afirstCol < bfirstCol) ? -1 : 1;
            }
        });
        return aarr;
    }

    /**
     * Handle Back to result click
     */
    backToResults() {
        this.showBackToResults = false;
        this.policyList = null;
        this.noPolicyData = false;
        this.template.querySelector('c-coaching-table-component-hum').backToResult();

    }

    /**
     * toggle validation highlights 
     */
    validateHighlightedFields() {
        if ((this.sBirthdate && !this.sPostalCode && !this.sState && !this.sFirstName && !this.sLastName) ||
            (!this.sBirthdate && this.sPostalCode && !this.sState && !this.sFirstName && !this.sLastName) ||
            (!this.sBirthdate && !this.sPostalCode && this.sState && !this.sFirstName && !this.sLastName)) {
            this.highlightFields();
            return false;
        } else {
            this.removehighlight();
            return true;
        }
    }

    /**
     * Validating Form
     * @param {*} event 
     */
    handleValidation(event) {
        const me = this;
        let cont = 0;
        let hasFieldValue = false;
        this.policyList = null;
        this.noPolicyData = false;
        this.showBackToResults = false;
        this.showNoDataView = false;
        let dateField = this.template.querySelector(".birthdate-input");
        let firstNameField = this.template.querySelector(".firstName");
        let isFirstName = firstNameField.value ? true : false;
        let lastNameField = this.template.querySelector(".lastName");
        let isLastName = lastNameField.value ? true : false;
        let idField = this.template.querySelector(".IDHighlight");
        let isIdField = idField && idField.value ? true : false;
        const reWordSelect = new RegExp(/\w+/);
        const reZip = new RegExp(/\d{5}/);
        const onlyNumber = new RegExp(/^\d+$/);
        const rePhone = new RegExp(/\d{10}/);
        me.isFormValid = true;
        const formFields = me.template.querySelectorAll(".inputfield");
        formFields.forEach(function (field) {
            me.updateFieldValidation(field, "");
            if (!field.value || (field.name == "state" && field.value === hcConstants.OPTION_NONE)) {
                cont++;
            }
            if (field.value) {
                hasFieldValue = true;
                me.showErrorMsg = false;
            }
        });

        if (!dateField.reportValidity()) {
            hasFieldValue = true;
        }

        if (hasFieldValue) {
            me.showErrorMsg = false;
        }
        else {
            me.showErrorMsg = true;
            me.isFormValid = false;
        }

        if (!this.validateHighlightedFields() || !this.isFormValid) {
            me.clearSearchResults();
            return;
        }

        formFields.forEach(function (field) {
            if (field.name && field.value) {
                switch (field.name) {
                    case 'lastName':
                        me.verifySpecialChars(lastNameField, me.labels.memberSearchSpecialCharLastNameHum);
                        if (field.value.length < 2) {
                            me.verifyFieldLength(field, 2, me.labels.memberSearchLastNameHum);
                        } else if (!isFirstName) {
                            me.verifyFieldLength(firstNameField, 1, me.labels.memberFirstNameSearchHum);
                            me.isFormValid = false;
                        }
                        break;
                    case 'firstName':
                        me.verifySpecialChars(field, me.labels.memberSearchSpecialCharFirstNameHum);
                        if (!isLastName) {
                            me.updateFieldValidation(lastNameField, me.labels.memberLastNameSearchHum);
                            me.isFormValid = false;
                        }
                        break;
                    case 'phone':
                        me.validateFieldData(onlyNumber, field, me.labels.HumPhoneInvalidCharacter);
                        if (me.isFormValid) {
                            me.validateFieldData(rePhone, field, me.labels.HumPhoneCharacterLimit);
                        }
                        break;
                    case 'zipCode':
                        me.validateFieldData(reZip, field, me.labels.HumZipcodeCharacterLimit);
                        if (field.value.length < 5) {
                            me.verifyFieldLength(field, 5, me.labels.memberSearchZipEnterHum);
                        } else if (!isFirstName || !isLastName) {
                            me.updateFieldValidation(field, me.labels.memberSearchZipCodeCriteriaHum);
                            me.isFormValid = false;
                        }
                        break;
                    case 'ID':
                        me.verifyFieldLength(field, 3, me.labels.memberSearchIDCriteriaHum);
                        me.verifySpecialChars(field, me.labels.memberSearchIDAlphaCriteriaHum);
                        me.validateFieldData(reWordSelect, field, me.labels.memberSearchIDAlphaCriteriaHum);
                        break;
                    case 'Suffix':
                        if (idField && !isIdField) {
                            me.updateFieldValidation(idField, me.labels.memberSearchIDMandatoryHum);
                            me.isFormValid = false;
                        }
                        break;
                    case 'groupNumber':
                        me.validateFieldData(reWordSelect, field, me.labels.memberSearchGroupAlphaCriteriaHum);
                        break;
                    case 'state':
                        if (!isFirstName || !isLastName) {
                            me.updateFieldValidation(field, me.labels.memberSearchCriteriaHum);
                            me.isFormValid = false;
                        }
                        break;
                    case 'birthdate':
                        if (!isFirstName || !isLastName) {
                            let d1 = new Date(me.today);
                            let d2 = new Date(dateField.value);
                            let dateErrorMsg = d1.getTime() >= d2.getTime() ? me.labels.memberSearchBirthdayCriteria : me.labels.memberSearchBirthdateLimit;
                            me.updateFieldValidation(field, dateErrorMsg);
                            me.isFormValid = false;
                        }
                        break;
                    default:
                }
            }
        });
        this.hasData = false;
        this.accountList = null;
        setTimeout(() => {
            if (me.isFormValid) {
                me.resultsTrue = true;
                if (this.formData.sPID === '') {
                    this.formData = {
                        ...this.formData,
                        sPhone: this.getFormatedPhoneNum(this.phone)
                    };
                    saveForm({ formMemberSearchWrapper: this.formData }).then(res => {
                        this.hasData = res.length > 0;
                        this.showNoDataView = !this.hasData;
                        if (this.hasData) {
                            this.accountList = res.map(item => ({
                                Id: item.Id,
                                RecordType: item.RecordType.Name,
                                FirstName: item.FirstName,
                                LastName: item.LastName,
                                Birthdate__c: item.Birthdate__c,
                                PersonMailingState: item.PersonMailingState,
                                PersonMailingPostalCode: item.PersonMailingPostalCode,
                                PersonHomePhone: item.PersonHomePhone,
                                ETL_Record_Deleted__c: item.ETL_Record_Deleted__c
                            }));
                        }
                        else {
                            this.noRecordlabel = this.labels.memberSearchNoResultsHum;
                        }
                        this.setUnknownFlag(false);
                    }).catch(err => {
                        this.showToast(this.labels.crmSearchError, this.labels.crmToastError, "error");
                        this.setUnknownFlag(false);
                    });
                }
                else {
                    startRequest({ sPIdVal: this.formData.sPID }).then(result => {
                        let oListAccounts = [];
                        if (result.length > 0) {
                            let isUnknownSearch = me.formData.sUnknownMemCheck;
                            result = result.map(item => ({
                                Id: item.Id,
                                RecordType: item.RecordType.Name,
                                FirstName: item.FirstName,
                                LastName: item.LastName,
                                Birthdate__c: item.Birthdate__c,
                                PersonMailingState: item.PersonMailingState,
                                PersonMailingPostalCode: item.PersonMailingPostalCode,
                                PersonHomePhone: item.PersonHomePhone,
                                ETL_Record_Deleted__c: item.ETL_Record_Deleted__c
                            }));
                            result.forEach(function (accEl) {
                                if (isUnknownSearch == true) {
                                    if ("Unknown Member" == accEl.RecordType) oListAccounts.push(accEl);
                                }
                                else {
                                    oListAccounts.push(accEl);
                                }
                            });
                        }
                        if (oListAccounts.length > 0) {
                            this.accountList = oListAccounts;
                            this.hasData = true;
                        }
                        else {
                            this.noRecordlabel = this.labels.memberSearchPIDNotFound;
                            this.showNoDataView = true;
                        }
                        this.setUnknownFlag(false);
                    }).catch(error => {
                        this.noRecordlabel = this.labels.noPidRecordsFound;
                        this.accountList = null;
                        this.showNoDataView = true;
                        this.hasData = false;
                        this.setUnknownFlag(false);
                        console.log("Error Occured", error);
                    });
                }
            }
            else {
                this.clearSearchResults();
            }
        }, 100);
    }

    setUnknownFlag(flag) {
        this.formData.sUnknownMemCheck = flag;
    }

    getFormatedPhoneNum(strNumber) {
        if (!strNumber) {
            strNumber = '';
        }
        strNumber = strNumber.replace(/-/g, '');
        const cleaned = ('' + strNumber).replace(/\D/g, '');
        //Check if the input is of correct length
        let match = cleaned.match(/^(\d{3})(\d{3})(\d{4})$/);
        if (match) {
            return '(' + match[1] + ') ' + match[2] + '-' + match[3]
        };
    }

    /**
     * Validate field length
     * @param {*} field 
     * @param {*} length 
     * @param {*} errMessage 
     */
    verifyFieldLength(field, length, errMessage) {
        if (field.value.length < length) {
            this.updateFieldValidation(field, errMessage);
            this.isFormValid = false;
        }
    }

    /**
     * Validate field data using provided regular expression
     * @param {*} regEx 
     * @param {*} field 
     * @param {*} errorMessage 
     */
    validateFieldData(regEx, field, errorMessage) {
        const me = this;
        if (!regEx.test(field.value)) {
            me.updateFieldValidation(field, errorMessage);
            me.isFormValid = false;
        }
    }
    /**
     * Verify for special characters
     * @param {*} errorMsg 
     */
    verifySpecialChars(field, errorMsg) {
        if (isSpecialCharsExists(field.value)) {
            this.updateFieldValidation(field, errorMsg);
            this.isFormValid = false;
        } if (field.name === 'phone') {
            if (/[^a-z]/i.test(field.value)) {
                this.updateFieldValidation(field, errorMsg);
                this.isFormValid = false;
            }
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

    handleUnknownSearch(event) {
        this.formData.sUnknownMemCheck = true;
        this.handleValidation(event);
    }

    /**
     * Reset form on click of reset button
     * @param {*} event 
     */
    handleReset(event) {
        const me = this;
        this.formData.sMemberid = '';
        this.formData.sSuffix = '';
        this.formData.sBirthdate = '';
        this.formData.sPhone = '';
        this.formData.sGroupNumber = '';
        this.formData.sPID = '';
        this.formData.sState = '';
        this.formData.sPostalCode = '';
        this.formData.rxNumber = '';
        this.formData.sFirstName = '';
        this.formData.sLastName = '';
        this.formData.sUnknownMemCheck = false;

        this.template.querySelectorAll("lightning-input").forEach((field) => {
            if (field.name === "state") {
                field.value = "";
            } else {
                field.required = false;
                field.value = "";
            }
            me.updateFieldValidation(field, "");
        });

        this.template.querySelectorAll("lightning-combobox").forEach((field) => {
            if (field.name === "state") {
                field.value = null;
            }
            me.updateFieldValidation(field, "");
        });

        me.showErrorMsg = false;

        this.clearSearchResults();
    }

    clearSearchResults() {
        this.accountList = null;
        this.hasData = false;
        this.showNoDataView = false;
        this.policyList = null;
        this.noPolicyData = false;
        this.showBackToResults = false;
    }
    /**
     * Hightlight validation failures
     */
    highlightFields() {
        this.showErrorMsg = false;
        let inp = this.template.querySelectorAll(".NameHighlight");
        inp.forEach(function (element) {
            element.required = true;
            element.reportValidity();
        }, this);
    }

    /**
     * Hightlight validation failures
     */
    highlightID() {
        this.showErrorMsg = false;
        let inp = this.template.querySelectorAll(".IDHighlight");
        inp.forEach(function (element) {
            element.required = true;
            element.reportValidity();
        }, this);
    }

    /**
     * Hide no input error
     */
    removeError() {
        this.showErrorMsg = false;
    }

    /**
     * Remove validation failures
     */
    removehighlight() {
        if (document.activeElement.tagName != 'INPUT') {
            let inp = this.template.querySelectorAll(".NameHighlight");
            inp.forEach(function (element) {
                element.required = false;
                element.reportValidity();
            }, this);
        }
    }

    highlightByFieldValue(event) {
        this.toggleHighLight(event.target.value);
    }

    highlightIDByFiield(event) {
        this.toggleIDHighLight(event.target.value);
    }

    highlightByZipCode(event) {
        this.formData.sPostalCode = event.target.value;
        if (event.target.value !== "") {
            this.highlightFields();
        } else {
            this.removehighlight();
        }
    }

    toggleHighLight(value) {
        if (value) {
            this.highlightFields();
        } else {
            this.removehighlight();
        }
    }

    toggleIDHighLight(value) {
        if (document.activeElement.tagName != 'INPUT') {
            let inp = this.template.querySelectorAll(".IDHighlight");
            inp.forEach(function (element) {
                element.required = false;
                element.reportValidity();
            }, this);
        }
    }

    /**
    * update data on phone field change
    */
    onPhoneFieldChange(event) {
        this.phone = event.target.value;
    }

    formatPhoneNumber(phNumber) {
        if (phNumber.length === 1) {
            return '(' + phNumber;
        } else if (phNumber.match(/^\(+(\d{3})$/)) {
            return phNumber + ') ';
        } else if (phNumber.match(/^\(+(\d{3})\)+[ ]?(\d{3})$/)) {
            return phNumber + '-';
        } else {
            return phNumber;
        }
    }

    enterSeach(event) {
        if (event.keyCode === 13) {
            this.handleValidation();
        }
    }

    showToast(strTitle, strMessage, strStyle) {
        this.dispatchEvent(new ShowToastEvent({
            title: strTitle,
            message: strMessage,
            variant: strStyle
        }));
    }
}