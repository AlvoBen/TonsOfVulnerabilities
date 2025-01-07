/*******************************************************************************************************************************
LWC JS Name : memberSearchFormHum.js
Function    : This LWC component serves as input for to search member data in CRM strides

Modification Log:
Developer Name           Code Review              Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Joel George                                   12/18/2020                   Member Search init version
* Arpit Jain/Navajit Sarkar			                02/10/2021				           Modifications for Search and Integration
* Supriya Shastri                               02/24/2021                   Added generic dropdown, Suffix conditions
* Mohan Kumar N			                            02/26/2021	                 US:1749525: Formatting phone number
* Mohan Kumar N                                 03/12/2021                   US:1942036: Password popup
* Ashish Kumar								                	03/24/2020					         Added logic for showing password popup for specific user
* Ritik Agarwal								                	03/20/2021					         Added logic for button greyed out
* Mohan Kumar N                                 05/18/2021                    US: 2272975- Launch member plan detail
* Supriya Shastri                               06/09/2021                   Added M.I to Search Results
* Supriya Shastri                               07/08/2021                   US-1989455
* Pallavi Shewale                 		          07/19/2021                    US: 2364907- Search- Add Humana Pharmacy Account Number to the Search screen
* Firoja Begam                                  10/27/2021                   Member Search Name validation Rule update on State selection
* Vardhman Jain                                 10/21/2021                   US:2098890- Account Management - Group Account - Launch Member Search
* Vardhman Jain                                 11/08/2021                   DF-4036 Fix
* Ajay Chakradhar                			          11/16/2021                   US 2498075 : DF-4066 Fix : Search result Validation Rules
* Visweswararao jayavarapu                      02/25/2022                   User story ( 3053487) SF - Limited Access to non-Medical Plans on Search Page

* Ashish Kumar/Vardhman Jain                    04/18/2022                   US-3334329 Unknown Provider validation

* Surendra v                                    07/12/2022                   US3533203
* Santhi Mandava                                09/29/2022                   US3398901- Homeoffice/CPD changes
* Jonathan Dickinson                            12/02/2022                   US-3999824
* Abhishek Mangutkar                  			12/06/2022                   US3996413- Fixing Pharmacy Issues
* Swapnali Sonawane                  			01/20/2023                   US4061348- Home Office Restriction-Mail Order pharmacy
* Abhishek Mangutkar							01/23/2023					 US 4134646
* Nirmal Garg							        02/13/2023					 US 4134646
* Deepak Khandelwal                             16/02/2023                    US4146763: Lightning - OSB Medicare, OSB Vision and Fitness-ODS Feed
* Swapnali Sonawane                             02/21/2023                   US-4304352 changes.
* Muthukumar									03/16/2023					 DF-7377
* Santhi Mandava                                05/10/2023                   Fixed defects 7602 and 7606
* Sivaprakash Rajendran                         05/23/2023                   US#4456352 - Member information search using Member ID not returning expected results
* Santhi Mandava								07/17/2023			         User Story 4782867:Search page results incorrectly displaying
* Nirmal Garg                                   08/18/2023                   user story 4861950: T1PRJ0870026 - HPIE/CRM - C12 Mail Order Management
* Pinky Vijur                                   11/16/2023                   DF-8292 Fix
*********************************************************************************************************************************/
import { track, api, wire } from "lwc";
import { NavigationMixin } from 'lightning/navigation';
import getCustomInputFieldPermission from '@salesforce/apex/SearchResultsBasedOnPermission_LH_HUM.getCustomFieldsToDisplay';
import saveForm from '@salesforce/apex/MemberSearch_LC_HUM.searchRecords';
import startRequest from '@salesforce/apexContinuation/MemberSearch_LC_HUM.getInvokeMBEPlusService2';
import getStateValues from '@salesforce/apex/SearchUtilty_H_HUM.getStateValues';
import getPolicyList from '@salesforce/apex/MemberSearchActiveFuturePolicies_LC_HUM.getMemberPlanDetails';
import hasCRMS_205_CCSPDPPharmacyPilot from '@salesforce/customPermission/CRMS_205_CCSPDPPharmacyPilot';
import hasCRMS_302_HPTraditionalInsuranceData from '@salesforce/customPermission/CRMS_302_HPTraditionalInsuranceData';
import CRMS_301_HPInsuranceData from '@salesforce/customPermission/CRMS_301_HPInsuranceData';
import hasCRMS_684_Medicare_Customer_Service_Access from '@salesforce/customPermission/CRMS_684_Medicare_Customer_Service_Access';
import hasCRMS_206_CCSHumanaPharmacyAccess from '@salesforce/customPermission/CRMS_206_CCSHumanaPharmacyAccess';
import { CurrentPageReference } from 'lightning/navigation';
import { ShowToastEvent } from 'lightning/platformShowToastEvent';
import {
    isSpecialCharsExists, getLocaleDate, hcConstants, getUserGroup, getFormattedDate,
    getSessionItem, setSessionItem, isDateValid, compareDate, sortTable, getBaseUrl, hasSystemToolbar
} from "c/crmUtilityHum";
import { getLabels } from 'c/customLabelsHum';
import { getPolicyLayout } from './memberSearchPolicyModals';
import { getRecord } from 'lightning/uiRecordApi';
import USER_ID from '@salesforce/user/Id';
import PROFILE_NAME_FIELD from '@salesforce/schema/User.Profile.Name';
import NETWORK_ID_FIELD from '@salesforce/schema/User.Network_User_Id__c';
import CURRENT_QUEUE from '@salesforce/schema/User.Current_Queue__c';
import { getHPColumnLayout } from './humanaPharmacyIDModals';
import { getWithoutHPColumnLayout } from './humanaPharmacyIDModals';
import pharmacyServiceErrorMessage from '@salesforce/label/c.PHARMACY_DEMOGRAPHIC_FILTER_ERROR';
import crmserviceHelper from 'c/crmserviceHelper';
import pubSubHum from 'c/pubSubHum';
import Pharmacy_Mem_Not_found from "@salesforce/label/c.Pharmacy_Mem_Not_found";
import CRMRetail_Error_Label from "@salesforce/label/c.CRMRetail_Error_Label";
import { invokeGetMemberService } from 'c/pharmacyRightSourceIntegrationHum';
const HOME_OFFICE_ALL = 'Home Office All';
const arrFields = ['Account.Account_Security_Answer__c', 'Account.Account_Security_Question__c', 'Account.Account_Security_EndDate__c', 'Account.Account_Security_Access__c'];
const MEMBER_NOT_FOUND = 'MEMBER NOT FOUND';
import { checkHomeOfficeAccess } from 'c/genericCheckUserHOAccess';
import isCRMFunctionalityONJS from '@salesforce/apex/HUMUtilityHelper_LWC.isCRMFunctionalityONJS';
import { getCustomSettingValue } from 'c/genericCustomSettingValueHum';
import { getProfileDetails } from 'c/pharmacyHPIEIntegrationHum';
export default class customSearchController extends NavigationMixin(crmserviceHelper) {

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
    @api isNavigatedFrmGrp;

    @track showBackToResults = false;
    @track stateOptions = [];
    @track showErrorMsg = false;
    @track isFormValid = true;
    @track labels = getLabels();
    @track oUserGroup = getUserGroup();
    @track newpolicy = [];
    @track hpColumnLayout = [];
    @track stateValue;
    @track showValidationMsg = false;
    @track bShowModal = false;
    @track profileName;
    @track netWorkId;
    @track workQueue;
    @track enterpriseId;
    @track buttonsConfig = [{
        text: getLabels().HUMAlertsAcknowledge,
        isTypeBrand: true,
        eventName: hcConstants.CLOSE
    }]
    @track accountId = '';
    @track questionDetails = {
        question: '',
        answer: '',
        pValue: ''
    };
    preSelectedPolicyId = ""; // this property used to preserve policy preselection and pass to account detail when click name

    @track pharmacyMemberNotFoundMsg = Pharmacy_Mem_Not_found;
    @track pharmacyservicemsg = pharmacyServiceErrorMessage;
    @track errorHeader = CRMRetail_Error_Label;
    @track isSoftphoneSwitchON = false;
    @wire(CurrentPageReference) pageRef;
    @wire(getRecord, { recordId: '$accountId', fields: arrFields })
    wiredAccount({
        error,
        data
    }) {
        if (data) {
            const termDate = data.fields.Account_Security_EndDate__c.value ? getLocaleDate(data.fields.Account_Security_EndDate__c.value) : null;
            const todayDate = getLocaleDate(new Date());
            if (compareDate(termDate, todayDate) === 1) {
                this.questionDetails = {
                    question: data.fields.Account_Security_Question__c.value,
                    pValue: data.fields.Account_Security_Access__c.value,
                    answer: data.fields.Account_Security_Answer__c.value
                }
                this.selMemberAccId = this.accountId;
                const { bRcc, bPharmacy, bGeneral, bGbo } = this.oUserGroup;

                if ((bRcc || bPharmacy || bGeneral || bGbo) && this.questionDetails.pValue !== null) {
                    this.openModal(this.accountId);
                }
            }
        }
        else {
            console.error('Error', error);
        }
    }

    @wire(getRecord, {
        recordId: USER_ID,
        fields: [PROFILE_NAME_FIELD, NETWORK_ID_FIELD, CURRENT_QUEUE]
    }) wireuser({
        error,
        data
    }) {
        if (error) {
            this.error = error;
        } else if (data) {
            this.profilename = data.fields.Profile.value.fields.Name.value;
            this.netWorkId = data.fields.Network_User_Id__c.value;
            this.workQueue = data.fields.Current_Queue__c.value;
        }
    }

    @wire(isCRMFunctionalityONJS, { sStoryNumber: '4404844_New' })
    switchFuntion({ error, data }) {
        if (data) {
            this.isSoftphoneSwitchON = data['4404844_New'];
        }
        if (error) {
            console.log('error---', error)
        }
    }

    sSSN = '';
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
    originalAccountList = [];
    accountCount = '';
    inputFieldSuffix;
    inputFieldGroupNumber;
    inputFieldPid;
    policyList;
    pharmacyAccount = 'No';

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

    @track formState = {
        isMemberid: false,
        isSuffix: false,
        isBirthdate: false,
        isPhone: false,
        isGroupNumber: false,
        isPID: false,
        isState: false,
        isPostalCode: false,
        isFirstName: false,
        isLastName: false
    };

    @api encodedData;

    @api
    encodedValues(encodedDatas) {
        let encodedData = encodedDatas['member'];
        this.sFirstName = encodedData.sFirstName;
        this.sLastName = encodedData.sLastName;
        this.sMemberid = encodedData.sMemberid;
        this.sBirthdate = encodedData.sBirthdate;
        this.sPhone = encodedData.sPhone;
        this.sSuffix = encodedData.sSuffix;
        this.sGroupNumber = encodedData.sGroupNumber;
        this.sPID = encodedData.sPID;
        this.sState = encodedData.sState;

        let me = this;
        this.template.querySelectorAll(".inputfield").forEach(function (field) {
            if (field.name == "firstName") {
                field.value = me.sFirstName;
            } else if (field.name == "lastName") {
                field.value = me.sLastName;
            } else if (field.name == "state") {
                field.value = me.sState;
            } else if (field.name == "ID") {
                field.value = me.sMemberid;
            } else if (field.name == "birthdate") {
                field.value = me.sBirthdate;
            } else if (field.name == "phone") {
                field.value = me.sPhone;
            } else if (field.name == "Suffix") {
                field.value = me.sSuffix;
            } else if (field.name == "groupNumber") {
                field.value = me.sGroupNumber;
            } else if (field.name == "PID") {
                field.value = me.sPID;
            }
        });

        this.formData['sFirstName'] = this.sFirstName;
        this.formData['sLastName'] = this.sLastName;
        this.formData['sMemberid'] = this.sMemberid;
        this.formData['sBirthdate'] = this.sBirthdate;
        this.formData['sPhone'] = this.sPhone;
        this.formData['sSuffix'] = this.sSuffix;
        this.formData['sGroupNumber'] = this.sGroupNumber;
        this.formData['sPID'] = this.sPID;
        this.formData['sState'] = this.sState;


        setTimeout(() => {
            if (this.isSoftphoneSwitchON) this.handleValidationNew();
            else this.handleValidation();
        }, 1);

    }
    connectedCallback() {
        getCustomInputFieldPermission({ sInput: this.inputType, sScreenType: this.screenType }).then(result => {
            const currentDate = new Date();
            const strResult = JSON.stringify(result);
            this.inputFieldSuffix = strResult.includes("Suffix");
            this.inputFieldGroupNumber = strResult.includes("groupNumber");
            this.inputFieldPid = strResult.includes("PID");
            this.today = currentDate.getFullYear() + '-' + (currentDate.getMonth() + 1) + '-' + (currentDate.getDate() > 9 ? currentDate.getDate() : '0' + currentDate.getDate());
            //DF-4036 Fix
            if (this.encodedData && this.encodedData != null && Object.keys(this.encodedData).includes('member')) {
                this.encodedValues(this.encodedData);
            }
            //DF-4036 Fix
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
     * Standard table container css
     */
    get containerCss() {
        return hasSystemToolbar ? 'searchpage-results-system slds-var-m-left_small' : 'searchpage-results slds-var-m-left_small'
    }

    /**
     * Handle state chagne
     * @param {*} event
     */
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

    /**
     * update data on field change
     * @param {*} evnt
     */
    onFieldChange(evnt) {
        const fieldKey = evnt.currentTarget.getAttribute('data-id');
        this.formData[fieldKey] = evnt.target.value;
        if (fieldKey == 'sMemberid') {
            this.sSSN = evnt.target.value;
        }
    }
    /**
     * Update phone field to the binding property
     * @param {*} event
     */
    updatePhone(event) {
        let phVal = event.target.value;
        let phField = this.template.querySelector(".phone-field");
        if (phVal) {
            const phone = this.getFormatedPhoneNum(phVal, phField);
            event.target.value = phone;
            this.formData.sPhone = phone;
        }
        else {
            this.formData.sPhone = "";
        }
    }

    getpermissioncheck(res, policiesMeta, sLockMessage) {
        const me = this;
        getCustomInputFieldPermission({ sInput: 'Result', sScreenType: 'Policy' }).then(result => {
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
            var response = JSON.parse(JSON.stringify(res.lstMemberPlans));
            response.forEach(function (item) {
                item.EffectiveFrom = getLocaleDate(item.EffectiveFrom);
                item.EffectiveTo = getLocaleDate(item.EffectiveTo);
                item.PlanName = (item.Plan && item.Plan.iab_description__c) ? item.Plan.iab_description__c : '';
                item.MedicareId__c = item.Member.MedicareId__c;
                item.Name = (item.Member_Id_Base__c && item.Member_Dependent_Code__c) ? (item.Member_Id_Base__c + '-' + item.Member_Dependent_Code__c) : item.Name;
            })
            this.policyList = me.processPolicyData(sortTable(response, 'Member_Coverage_Status__c', 'Product__c'));
               if ((this.policyList.length != 0 && this.policyList.length > 0) && (this.profilename === hcConstants.PHARMACY_SPECIALIST_PROFILE_NAME || (this.profilename === hcConstants.Customer_Care_Specialist && (hasCRMS_205_CCSPDPPharmacyPilot || hasCRMS_206_CCSHumanaPharmacyAccess)) || (this.profilename === 'Customer Care Supervisor' && hasCRMS_205_CCSPDPPharmacyPilot))) {
                    var filteredpolicylist = this.policyList.filter(function (policyListitem) {
                    return policyListitem.Product__c == hcConstants.MED_POLICY;
                });
                this.policyList = filteredpolicylist;
            }
            this.verifyPolicyAccess(res, this.policyList, sLockMessage);
        }).catch(error => {
            console.log("Error Occured", error);
        });
    }
    verifyPolicyAccess(policyWrapper, policyList, sLockMessage) {
        policyList.forEach(ele => {
            let iPlanId = policyWrapper.mapPolicyPlans[ele.Id];
            ele.isLocked = iPlanId && !policyWrapper.mapRecordAccess[iPlanId];
            ele.sLockMessage = sLockMessage;
            ele.isOSB = ele.OSB_Indicator__c ? ele.OSB_Indicator__c == 'O' ? true : false : false;
            if (ele.isOSB) {
                ele.checked = false;
                ele.disabled = true;
            }
            if (ele.isLocked) {
                ele = this.maskPolicyData(ele);
                ele.disabled = ele.isLocked;
                if (ele.disabled) ele.checked = false;
            }
        });
    }

    maskPolicyData(objPlanMem) {
        objPlanMem.Name = 'XXXX';
        objPlanMem.MedicareId__c = 'XXXX';
        objPlanMem.PlanName = 'XXXX';
        objPlanMem.EffectiveFrom = 'XXXXXXXX';
        objPlanMem.EffectiveTo = 'XXXXXXXX';
        objPlanMem.Member_Coverage_Status__c = 'XXXX';
        return objPlanMem;
    }
    /**
     * Validate date format and trigger remove highlight method
     * @param {*} event
     */
    onDateFieldBlur(event) {
        const me = this;
        const field = event.target;
        if (field.value !== "") {
            me.updateFieldValidation(field, isDateValid(field.value) ? "" : me.labels.HumStartnEndDate);
        }
        this.highlightByFieldValue(event);
    }

    /**
     * Handles record click interaction
     * @param {*} event
     */
    handleInteraction(event) {
        const me = this;

        me.showBackToResults = me.accountCount > 1;
        me.accountId = event.detail.Id;
        let res = me.accountList.find(t => t.Id === me.accountId);
        let pharmcyLink = JSON.parse(JSON.stringify(res));
        let eID = res.enterpriseID;
        this.enterpriseId = res.enterpriseID;
        let customerValue = '';
        const sLockMessage = this.labels.LimitedAccessMessage;
        let hopolicypresent = false;
        getPolicyList({ sAccId: me.accountId })
            .then(res => {
                if (res.lstMemberPlans.length > 0) {
                    let tempHOPolicies = res?.lstMemberPlans.filter(k => k?.Plan?.Payer?.Security_Groups__c === 'Home Office');
                    if (tempHOPolicies && Array.isArray(tempHOPolicies) && tempHOPolicies.length > 0) {
                        hopolicypresent = true;
                    }
                    me.getpermissioncheck(res, getPolicyLayout(this.oUserGroup), sLockMessage);
                } else {
                    me.noPolicyData = false;
                }
            }).catch(err => {
                console.log("Error Occured", error);
            });
        pubSubHum.fireEvent(this.pageRef, "CLEAR_MEMBER_POLICY_INT", {});
        if (res.hpLink === '') {
            if (eID) {
                this.getPharmacyMemberId(eID).then(result => {
                    customerValue = result;
                    if (pharmcyLink.isLocked) {
                        this.checkHomeOfficeAccess(HOME_OFFICE_ALL).then(result => {
                            if (result) {
                                this.updateChildComponent(this.isStringEmpty(customerValue) ? '' : customerValue, pharmcyLink, false);
                            } else {
                                this.updateChildComponent(this.isStringEmpty(customerValue) ? '' : 'XXXXX', pharmcyLink, false);
                            }
                        }).catch(error => {
                            console.log(error);
                        });
                    } else {
                        if (hopolicypresent) {
                            this.checkHomeOfficeAccess(HOME_OFFICE_ALL).then(result => {
                                if (result) {
                                    this.updateChildComponent(this.isStringEmpty(customerValue) ? '' : customerValue, pharmcyLink, false);
                                } else {
                                    this.updateChildComponent(this.isStringEmpty(customerValue) ? '' : 'XXXXX', pharmcyLink, false);
                                }
                            }).catch(error => {
                                console.log(error);
                            });
                        } else {
                            this.updateChildComponent(this.isStringEmpty(customerValue) ? '' : customerValue, pharmcyLink, false, false);
                        }
                    }
                }).catch(error => {
                    console.error(error);
                })
            }
        }


    }

    isStringEmpty(input) {
        if (input != null && input != undefined && input != '' && input?.length >= 0) {
            return false;
        } else {
            return true;
        }
    }

    checkHomeOfficeAccess(gpname) {
        return new Promise((resolve, reject) => {
            checkHomeOfficeAccess(gpname).then(result => {
                console.log(result);
                resolve(result);
            }).catch(error => {
                console.log(error);
                reject(false);
            })
        })
    }

    updateChildComponent(inputvalue, data, flag) {
        if (inputvalue) {
            data.hpLink = inputvalue;
            data.disable = inputvalue && inputvalue?.includes('XX') ? true : false;
        } else {
            data.hpLink = '';
        }
        this.template.querySelector('c-standard-table-component-hum').computecallback([data], flag);
    }

    callRS() {
        let customerValue = '';
        let todaysdate = new Date();
        let sDate = new Date();
        sDate.setMonth(todaysdate.getMonth() - 3);
        let startDate = ((sDate.getMonth() + 1).toString().length == 1 ? '0' + (sDate.getMonth() + 1) : (sDate.getMonth() + 1)) + '/' + (sDate.getDate().toString().length === 1 ? '0' + sDate.getDate() : sDate.getDate()) + '/' + sDate.getFullYear();
        let endDate = ((todaysdate.getMonth() + 1).toString().length == 1 ? '0' + (todaysdate.getMonth() + 1) : (todaysdate.getMonth() + 1)) + '/' + (todaysdate.getDate().toString().length === 1 ? '0' + todaysdate.getDate() : todaysdate.getDate()) + '/' + todaysdate.getFullYear();
        return new Promise((resolve, reject) => {
            invokeGetMemberService(this.enterpriseId, this.netWorkId, startDate, endDate, this.accountId)
                .then(result => {
                    if (result && result?.message) {
                        if (result?.message?.toLowerCase().includes('serviceerror')) {
                            console.log("Error Occured", result?.message);
                            this.showToast(this.errorHeader, this.pharmacyservicemsg, "error");
                            this.pharmacyAccount = 'ServiceError';
                            reject(result?.message);
                        }
                    } else {
                        if (result != null && result != undefined && result != '' && result?.length > 0) {
                            let pharmacyMemberDetails = JSON.parse(result);
                            if (pharmacyMemberDetails?.GetMemberReponse?.Customers?.ErrorDescription) {
                                if (pharmacyMemberDetails.GetMemberReponse.Customers.ErrorDescription.toUpperCase() == MEMBER_NOT_FOUND) {
                                    //show no member found tost msg
                                    this.showToast(this.errorHeader, this.pharmacyMemberNotFoundMsg, "error");
                                    this.pharmacyAccount = 'No';
                                    resolve('');
                                }
                            }
                            else {
                                customerValue = pharmacyMemberDetails?.GetMemberReponse?.Customers?.Customer[0]?.AccountNumber?.CustomerValue ?? '';
                                this.pharmacyAccount = 'Yes';
                                resolve(customerValue);
                            }
                        }
                        else {
                            //show no member found tost msg
                            this.showToast(this.errorHeader, this.pharmacyMemberNotFoundMsg, "error");
                            this.pharmacyAccount = 'No';
                            resolve('');
                        }
                    }
                }).catch(error => {
                    console.log("Error Occured", error);
                    this.showToast(this.errorHeader, this.pharmacyservicemsg, "error");
                    this.pharmacyAccount = 'ServiceError';
                    reject(error);
                });
        })
    }

    callHPIE() {
        return new Promise((resolve, reject) => {
            getProfileDetails(this.enterpriseId, this.netWorkId, this.organization ?? 'HUMANA').then(result => {
                if (result && Object.keys(result)?.length > 0) {
                    this.pharmacyAccount = 'Yes';
                    resolve(result?.AccountId ?? '');
                }
            }).catch(error => {
                console.log(error);
                this.pharmacyAccount = 'ServiceError';
                reject('ServiceError')
            })
        });
    }

    getPharmacyMemberId(eid) {
        return new Promise((resolve, reject) => {
            getCustomSettingValue('Switch', 'HPIE Switch')
                .then(result => {
                    if (result && result?.IsON__c && result?.IsON__c === true) {
                        this.callHPIE(this.accId, this.memberFirstName, this.memberLastName, this.enterpriseId)
                            .then(result => {
                                resolve(result)
                            }).catch(error => {
                                reject(error);
                            })
                    } else {
                        this.callRS()
                            .then(result => {
                                resolve(result)
                            }).catch(error => {
                                reject(error);
                            })
                    }
                })
        });
    }

    /**
    * Format date as user enters keys
    * @param {*} event
    */
    formatDateOnKeyUp(event) {
        if (event.keyCode === 8 || event.keyCode === 46) { //exclude backspace and delete key
            return;
        }
        let dtValue = event.target.value;
        dtValue = getFormattedDate(dtValue);
        event.target.value = dtValue;
        if (isDateValid(dtValue)) {
            this.formData.sBirthdate = dtValue;
        }
        this.enterSeach(event)
    }


    /**
     * Process Policy Data
     * @param {*} dataObj
     */
    processPolicyData(dataObj) {
        const me = this;
        let data;
        this.preSelectedPolicyId = "";
        if (hasCRMS_205_CCSPDPPharmacyPilot || hasCRMS_684_Medicare_Customer_Service_Access || hasCRMS_206_CCSHumanaPharmacyAccess) {
            data = me.updatePreselectForCCS(dataObj);
        }
        else if (hasCRMS_302_HPTraditionalInsuranceData || CRMS_301_HPInsuranceData || this.profilename === 'Humana Pharmacy Specialist') {
            data = me.updatePreselectForHPS(dataObj);
        }
        else {
            data = dataObj;
        }

        if (this.preSelectedPolicyId) {
            pubSubHum.fireEvent(this.pageRef, "MEMBER_POLICY_INT", {
                message: this.preSelectedPolicyId
            });
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
        if (checkedStatus) {
            this.preSelectedPolicyId = item.Id;
        }
        return checkedStatus;
    }

    hasAccessToHP(item) {
        let pharmcyLink;


        if (this.profilename === 'Humana Pharmacy Specialist' || ((this.profilename === 'Customer Care Specialist' || this.profilename === 'Customer Care Supervisor') && (hasCRMS_205_CCSPDPPharmacyPilot || hasCRMS_206_CCSHumanaPharmacyAccess))) {
            pharmcyLink = item.Enterprise_ID__c;

            this.hpColumnLayout = getHPColumnLayout;

        }
        else {

            this.hpColumnLayout = getWithoutHPColumnLayout;

        }

        return pharmcyLink;
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
                    checked: this.isMedPolicyWithActive(item, MED_POLICY, STATUS_ACTIVE)
                }
            })
        }
        return data;
    }

    /**
     * Returns true if the policy is med and status is active else false
     * @param {*} item
     * @param {*} MED_POLICY
     * @param {*} STATUS_ACTIVE
     */
    isMedPolicyWithActive(item, MED_POLICY, STATUS_ACTIVE) {
        const bSatisfied = item.Product__c === MED_POLICY && item.Member_Coverage_Status__c === STATUS_ACTIVE;
        if (bSatisfied) {
            this.preSelectedPolicyId = item.Id;
        }
        return bSatisfied;
    }

    /**
     * Handle Back to result click
     */
    backToResults() {
        this.showBackToResults = false;
        this.policyList = null;
        this.noPolicyData = false;
        this.preSelectedPolicyId = "";
        setTimeout(() => {
            this.template.querySelector('c-standard-table-component-hum').computecallback(this.accountList, false);
        }, 1)
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



    handleValidationNew() {
        const me = this;
        let hasFieldValue = false;
        this.policyList = null;
        this.noPolicyData = false;
        this.showBackToResults = false;
        this.showNoDataView = false;
        let dateField = this.template.querySelector(".birthdate-input");
        //Added for US#4456352 - Member information search using Member ID not returning expected results  
        //Check the DOB value and reset the form data and local assignments if its empty before validating
        if (dateField.value === '') {
            this.sBirthdate = '';
            this.formData.sBirthdate = '';
        }

        me.isFormValid = true;
        let isPrimaryCriteriaPresent = false;
        me.showValidationMsg = false;
        me.clearSearchResults();
        const formFields = me.template.querySelectorAll(".inputfield");
        formFields.forEach(function (field) {
            field.required = false;
            me.updateFieldValidation(field, "");
            if (field.value) {
                hasFieldValue = true;
                me.showErrorMsg = false;
            }
            switch (field.name) {
                case 'lastName':
                    me.formState.lastNameField = field;
                    me.formState.isLastName = field.value ? true : false;
                    break;
                case 'firstName':
                    me.formState.firstNameField = field;
                    me.formState.isFirstName = field.value ? true : false;
                    break;
                case 'phone':
                    me.formState.phoneField = field;
                    me.formState.isPhone = field.value ? true : false;
                    break;
                case 'zipCode':
                    me.formState.zipCodeField = field;
                    me.formState.isPostalCode = field.value ? true : false;
                    break;
                case 'ID':
                    me.formState.idField = field;
                    me.formState.isMemberid = field.value ? true : false;
                    break;
                case 'PID':
                    me.formState.pidField = field;
                    me.formState.isPIDid = field.value ? true : false;
                    break;
                case 'Suffix':
                    me.formState.suffixField = field;
                    me.formState.isSuffix = field.value ? true : false;
                    break;
                case 'groupNumber':
                    me.formState.groupNumberField = field;
                    me.formState.isGroupNumber = field.value ? true : false;
                    break;
                case 'birthdate':
                    me.formState.birthDateField = field;
                    me.formState.isBirthdate = field && field.value ? true : false;
                    break;
                default:
            }
        });
        if (!dateField.reportValidity()) {
            hasFieldValue = true;
        }
        if (hasFieldValue || me.stateValue) {
            me.showErrorMsg = false;
        }
        else {
            me.showErrorMsg = true;
            me.isFormValid = false;
        }
        if (me.formState.isMemberid || (me.formState.isMemberid && me.formState.isState) || me.formState.isGroupNumber || me.formState.isPhone
            || (me.formState.isFirstName && me.formState.isLastName) || me.formState.isPID) {
            isPrimaryCriteriaPresent = true;
        }
        if (!isPrimaryCriteriaPresent) {
            if (!this.validateBirthDateCombination()) return;
            if (!this.validateZipCodeCombination()) return;
            if (!this.validateStateCombination()) return;
            if (!this.validateNameCombination()) return;
        }
        if (this.isFormValid) me.showValidationMsg = false;
        this.searchMembers();
    }

    searchMembers() {
        const me = this;
        const sLockMessage = this.labels.LimitedAccessMessage;
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
                    saveForm({ formMemberSearchWrapper: this.formData }).then(result => {
                        let res = result.lstAccounts;
                        this.hasData = res.length > 0;
                        this.accountCount = res.length;

                        this.showNoDataView = !this.hasData;
                        if (this.hasData) {
                            this.accountList = res.map(item => ({
                                Id: item.Id,
                                RecordType: item.RecordType.Name,
                                FirstName: (item.MiddleName) ? item.FirstName + ', ' + item.MiddleName : item.FirstName,
                                LastName: item.LastName,
                                Birthdate__c: item.Birthdate__c,
                                Mbr_Gen_Key__c: item.Mbr_Gen_Key__c,
                                PersonMailingState: item.PersonMailingState,
                                PersonMailingPostalCode: item.PersonMailingPostalCode,
                                PersonHomePhone: item.PersonHomePhone,
                                ETL_Record_Deleted__c: item.ETL_Record_Deleted__c,
                                disable: item.ETL_Record_Deleted__c,
                                enterpriseID: this.hasAccessToHP(item),
                                isLocked: !result.mapRecordAccess[item.Id],
                                sLockMessage: sLockMessage,
                                hpLink: ''
                            }));
                        }
                        else {
                            this.noRecordlabel = this.labels.memberSearchNoResultsHum;
                        }
                        if (this.accountList && this.accountList.length > 0) {
                            this.originalAccountList = this.accountList;
                        }
                        this.setUnknownFlag(false);
                    }).catch(err => {
                        this.showToast(this.labels.crmSearchError, this.labels.crmToastError, "error");
                        this.setUnknownFlag(false);
                        console.log("Error Occured", err);
                    });
                }
                else {
                    startRequest({ sPIdVal: this.formData.sPID }).then(result => {
                        let oListAccounts = [];
                        let oListTemp = [];
                        if (result && result.lstAccounts && result.lstAccounts.length > 0) {
                            let isUnknownSearch = me.formData.sUnknownMemCheck;
                            oListAccounts = result.lstAccounts.map(item => ({
                                Id: item.Id,
                                RecordType: item.RecordType.Name,
                                FirstName: item.FirstName,
                                LastName: item.LastName,
                                Birthdate__c: item.Birthdate__c,
                                PersonMailingState: item.PersonMailingState,
                                PersonMailingPostalCode: item.PersonMailingPostalCode,
                                PersonHomePhone: item.PersonHomePhone,
                                ETL_Record_Deleted__c: item.ETL_Record_Deleted__c,
                                isLocked: !result.mapRecordAccess[item.Id],
                                sLockMessage: sLockMessage,
                                disable: item.ETL_Record_Deleted__c
                            }));
                            oListAccounts.forEach(function (accEl) {
                                if (isUnknownSearch == true) {
                                    if ("Unknown Member" == accEl.RecordType) oListTemp.push(accEl);
                                }
                                else {
                                    oListTemp.push(accEl);
                                }
                            });
                        }
                        if (oListTemp.length > 0) {
                            this.accountList = oListTemp;
                            this.hasData = true;
                        }
                        else {
                            this.noRecordlabel = this.labels.memberSearchNoResultsHum;
                            this.showNoDataView = true;
                        }
                        this.setUnknownFlag(false);
                    }).catch(error => {
                        this.noRecordlabel = this.labels.memberSearchNoResultsHum;
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
        pubSubHum.fireEvent(this.pageRef, "CLEAR_MEMBER_POLICY_INT", {});
    }

    /**
     * Validating Form
     * @param {*} event
     */
    handleValidation(event) {
        const me = this;
        let hasFieldValue = false;
        this.policyList = null;
        this.noPolicyData = false;
        this.showBackToResults = false;
        this.showNoDataView = false;
        let dateField = this.template.querySelector(".birthdate-input");
        //Added for US#4456352 - Member information search using Member ID not returning expected results  
        //Check the DOB value and reset the form data and local assignments if its empty before validating
        if (dateField.value === '') {
            this.sBirthdate = '';
            this.formData.sBirthdate = '';
        }

        const reWordSelect = new RegExp(/\w+/);
        const reZip = new RegExp(/\d{5}/);
        const onlyNumber = new RegExp(/^\d+$/);
        const rePhone = new RegExp(/^\(?([0-9]{3})\)?[ ]?([0-9]{3})[-]?([0-9]{4})$/);
        const sLockMessage = this.labels.LimitedAccessMessage;
        me.isFormValid = true;
        let isPrimaryCriteriaPresent = false;
        me.showValidationMsg = false;
        me.clearSearchResults();
        const formFields = me.template.querySelectorAll(".inputfield");
        formFields.forEach(function (field) {
            field.required = false;
            me.updateFieldValidation(field, "");
            if (field.value) {
                hasFieldValue = true;
                me.showErrorMsg = false;
            }
            switch (field.name) {
                case 'lastName':
                    me.formState.lastNameField = field;
                    me.formState.isLastName = field.value ? true : false;
                    if (me.formState.isLastName) {
                        me.verifySpecialChars(field, me.labels.memberSearchSpecialCharLastNameHum);
                        if (field.value.length < 2) me.verifyFieldLength(field, 2, me.labels.memberSearchLastNameHum);
                    }
                    break;
                case 'firstName':
                    me.formState.firstNameField = field;
                    me.formState.isFirstName = field.value ? true : false;
                    if (me.formState.isFirstName) me.verifySpecialChars(field, me.labels.memberSearchSpecialCharFirstNameHum);
                    break;
                case 'phone':
                    me.formState.phoneField = field;
                    me.formState.isPhone = field.value ? true : false;
                    if (me.formState.isPhone) {
                        me.verifyFieldLength(field, 14, me.labels.HumPhoneCharacterLimit);
                        me.validateFieldData(rePhone, field, me.labels.HumPhoneInvalidCharacter);
                    }
                    break;
                case 'zipCode':
                    me.formState.zipCodeField = field;
                    me.formState.isPostalCode = field.value ? true : false;
                    if (me.formState.isPostalCode) {
                        me.validateFieldData(reZip, field, me.labels.HumZipcodeCharacterLimit);
                        if (field.value.length < 5) {
                            me.verifyFieldLength(field, 5, me.labels.memberSearchZipEnterHum);
                        }
                    }
                    break;
                case 'ID':
                    me.formState.idField = field;
                    me.formState.isMemberid = field.value ? true : false;
                    if (me.formState.isMemberid) {
                        me.verifyFieldLength(field, 3, me.labels.memberSearchIDCriteriaHum);
                        me.verifySpecialChars(field, me.labels.memberSearchIDAlphaCriteriaHum);
                        me.validateFieldData(reWordSelect, field, me.labels.memberSearchIDAlphaCriteriaHum);
                    }
                    break;
                case 'PID':
                    me.formState.pidField = field;
                    me.formState.isPIDid = field.value ? true : false;
                    break;
                case 'Suffix':
                    me.formState.suffixField = field;
                    me.formState.isSuffix = field.value ? true : false;
                    if (me.formState.isSuffix) {
                        me.validateFieldData(onlyNumber, field, me.labels.HumSearchSuffixNumericValidation);
                        me.verifyFieldLength(field, 2, me.labels.memberSuffixLimitHum);
                    }
                    break;
                case 'groupNumber':
                    me.formState.groupNumberField = field;
                    me.formState.isGroupNumber = field.value ? true : false;
                    if (me.formState.isGroupNumber) {
                        me.verifySpecialChars(field, me.labels.memberSearchSpecialCharLastNameHum);
                        me.validateFieldData(reWordSelect, field, me.labels.memberSearchGroupAlphaCriteriaHum);
                    }
                    break;
                case 'birthdate':
                    me.formState.birthDateField = field;
                    me.formState.isBirthdate = field && field.value ? true : false;
                    if (me.formState.isBirthdate) {
                        if (!isDateValid(field.value)) {
                            me.updateFieldValidation(field, me.labels.HumStartnEndDate);
                            me.isFormValid = false;
                        }
                        else if (compareDate(getLocaleDate(field.value), getLocaleDate(new Date())) === 1) {
                            me.updateFieldValidation(field, hcConstants.BIRTH_DATE_NOT_FUTURE);
                            me.isFormValid = false;
                        }
                    }
                    break;
                default:
            }
        });
        if (!dateField.reportValidity()) {
            hasFieldValue = true;
        }
        if (hasFieldValue || me.stateValue) {
            me.showErrorMsg = false;
        }
        else {
            me.showErrorMsg = true;
            me.isFormValid = false;
        }
        if (me.formState.isMemberid || (me.formState.isMemberid && me.formState.isState) || me.formState.isGroupNumber || me.formState.isPhone
            || (me.formState.isFirstName && me.formState.isLastName) || me.formState.isPID) {
            isPrimaryCriteriaPresent = true;
        }
        if (!isPrimaryCriteriaPresent) {
            if (!this.validateBirthDateCombination()) return;
            if (!this.validateZipCodeCombination()) return;
            if (!this.validateStateCombination()) return;
            if (!this.validateNameCombination()) return;
        }
        if (this.isFormValid)
            me.showValidationMsg = false;
        this.searchMembers();
    }

    validateBirthDateCombination() {
        let isValid = true;
        if (this.formState.isBirthdate) {
            if (!this.formState.isFirstName && !this.formState.isLastName && !this.formState.isMemberid && !this.formState.isGroupNumber && !this.formState.isPhone) {
                this.isFormValid = isValid = false;
                this.updateFieldValidation(this.formState.birthDateField, this.labels.HumErrorBirthDateSearch);
                return isValid;
            } else if (!this.formState.isFirstName || !this.formState.isLastName) {
                this.isFormValid = isValid = false;
                this.updateFieldValidation(this.formState.birthDateField, this.labels.HumErrorBirthDateSearchCombination);
                return isValid;
            }
        }
        return isValid;
    }

    validateZipCodeCombination() {
        let isValid = true;
        if (this.formState.isPostalCode) {
            if (!this.formState.isFirstName && !this.formState.isLastName && !this.formState.isMemberid && !this.formState.isGroupNumber && !this.formState.isPhone) {
                this.isFormValid = isValid = false;
                this.updateFieldValidation(this.formState.zipCodeField, this.labels.HumErrorZipCodeSearch);
                return isValid;
            } else if (!this.formState.isFirstName || !this.formState.isLastName) {
                this.isFormValid = isValid = false;
                this.updateFieldValidation(this.formState.zipCodeField, this.labels.HumErrorPostalCodeSearchCombination);
                return isValid;
            }
        }
        return isValid;
    }

    validateStateCombination() {
        let isValid = true;
        if (this.stateValue) {
            if (!this.formState.isFirstName && !this.formState.isLastName && !this.formState.isMemberid && !this.formState.isGroupNumber && !this.formState.isPhone) {
                this.showValidationMsg = true;
                this.isFormValid = isValid = false;
                return isValid;
            } else if (!this.formState.isFirstName || !this.formState.isLastName) {
                this.showValidationMsg = true;
                this.isFormValid = isValid = false;
                return isValid;
            }
        }
        return isValid;
    }

    validateNameCombination() {
        let isValid = true;
        if (this.formState.isFirstName && !this.formState.isLastName && !this.formState.isMemberid
            && !this.formState.isGroupNumber && !this.formState.isPhone && !this.stateValue && !this.formState.isBirthdate && !this.formState.isPostalCode) {
            this.isFormValid = isValid = false;
            this.updateFieldValidation(this.formState.firstNameField, this.labels.memberSearchFirstNameCriteriaHum);
            return isValid;
        } else if (this.formState.isLastName && !this.formState.isFirstName && !this.formState.isMemberid
            && !this.formState.isGroupNumber && !this.formState.isPhone && !this.stateValue && !this.formState.isBirthdate && !this.formState.isPostalCode) {
            this.isFormValid = isValid = false;
            this.updateFieldValidation(this.formState.lastNameField, this.labels.HumErrorMedicareLastNameSearch);
            return isValid;
        }
        return isValid;
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
        } else {
            return strNumber;
        }
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
    @api
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

        this.sMemberid = '';
        this.sBirthdate = '';
        this.sPhone = '';
        this.sGroupNumber = '';
        this.sPID = '';
        this.sState = '';
        this.sPostalCode = '';
        this.sFirstName = '';
        this.sLastName = '';

        this.formData.sUnknownMemCheck = false;
        this.template.querySelector("c-generic-drop-down-hum").reset();

        this.template.querySelectorAll("lightning-input").forEach((field) => {
            field.required = false;
            field.value = "";
            me.updateFieldValidation(field, "");
        });

        me.showErrorMsg = false;
        me.showValidationMsg = false;
        me.stateValue = false;

        this.clearSearchResults();
        pubSubHum.fireEvent(this.pageRef, "CLEAR_MEMBER_POLICY_INT", {});
    }

    clearSearchResults() {
        this.accountList = null;
        this.hasData = false;
        this.showNoDataView = false;
        this.policyList = null;
        this.noPolicyData = false;
        this.showBackToResults = false;
        this.preSelectedPolicyId = "";
    }

    clearStateHandler(event) {
        this.formData.sState = "";
        this.stateValue = false;
        this.showValidationMsg = false;
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
            //Updated validation rule to remove highlight from name fields based on Statevalue selected
            const { sFirstName, sLastName, sState } = this.formData;
            inp.forEach(function (element) {
                element.required = (sState.length) ?
                    (sFirstName.length && sLastName.length) ? false : true : false;
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

    /**
     * Format phone number on key up
     * @param {*} event
     */
    formatPhoneNumber(event) {
        if (event.keyCode === 8 || event.keyCode === 46) {
            return;
        }
        let phNumber = event.target.value;
        phNumber = phNumber.replace(/[()-]|[ ]/gi, "");
        const onlyNumber = new RegExp(/^\d+$/);
        if (onlyNumber.test(phNumber)) {
            if (phNumber.length < 3) {
                phNumber = '(' + phNumber;
            } else if (phNumber.length < 6) {
                phNumber = '(' + phNumber.substring(0, 3) + ') ' + phNumber.substring(3, 6);
            } else if (phNumber.length > 5) {
                phNumber = '(' + phNumber.substring(0, 3) + ') ' + phNumber.substring(3, 6) + '-' + phNumber.substring(6, 10);
            }
        }

        event.target.value = phNumber;
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

    navigateToSearchEnroll() {
        const detail = { tabVal: 'searchEnroll', memberId: this.sSSN };
        const tabNavigate = new CustomEvent('tabnavigation', { detail });
        this.dispatchEvent(tabNavigate);
    }

    /**
     * Open password popup modal
     * @param {*} key
     */
    openModal(key) {
        this.bShowModal = getSessionItem(key) ? false : true;
    }

    /**
     * clsoe password popup modal
     * @param {*} key
     */
    closeModal() {
        this.bShowModal = false;
        setSessionItem(this.selMemberAccId, true);
    }

    handlepolicyInteraction(evnt) {
        pubSubHum.fireEvent(this.pageRef, "MEMBER_POLICY_INT", {
            message: evnt.detail.Id
        });
    }
    /**
     * Event listenr to listen hyper link click on the standard table
     * @param {*} evnt
     */
    onHyperLickClick(evnt) {
        const me = this;
        const accountId = evnt.detail.accountId;
        const policyTable = me.template.querySelector(`[data-id='member-policy']`);
        let selPolicyId = policyTable && policyTable.selectedRecordId();
        if (!selPolicyId) {
            selPolicyId = this.preSelectedPolicyId;
        }
        if (selPolicyId) {
            setSessionItem(hcConstants.MEMBER_POLICY_ID, selPolicyId + '##' + accountId);
            const url = `${getBaseUrl()}/lightning/r/MemberPlan/${selPolicyId}/view?ws=%2Flightning%2Fr%2FAccount%2F${accountId}%2Fview?%2Fc__pharmacyMember%2F${this.pharmacyAccount}`;

            this[NavigationMixin.Navigate]({
                type: 'standard__webPage',
                attributes: { //set account id here, and get the id on highlights panel to nav
                    url
                }
            });
        }
        else {
            this.navigateToViewAccountDetail(accountId, 'Account', 'view');
        }
    }
}