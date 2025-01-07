/*******************************************************************************************************************************
Function    : This JS serves as controller to policyHighlightsPanelsHum.html.
Modification Log:
Developer Name                    Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Mohan kumar N                 04/22/2021                  US: 1464525
* Supriya Shastri               07/05/2021                  US: 2158825 Low income subisy icon
* Ritik Agarwal                 05/18/2021                  US: 2308023
* Mohan kumar N                 05/18/2021                  US: 2272975- Launch member plan detail
* Ritik Agarwal                 05/22/2021                  Added a condition for show webregistered icon
* Supriya                        06/04/2021                US: 2176313
* Ritik Agarwal                 08/03/2021                  Added a condition for hide icons if record is legacy delete
* Firoja Begam					11/02/2021				    US:2468373 OtherInsurance Icon Implementation
* Firoja Begam                  01/18/2021                  UserStory:2943289 Lightning - LIS (Low Income Subsidy) rebranding to "Extra Help"
*Aishwarya Pawar                05/13/2022                REQ - 3285223 Alert Configuration on Plan Member Page
* Supriya                       11/01/2022                  Added new case button
* Supriya Shastri				03/16/2021				      US-1985154
* Aishwarya Pawar				06/01/2022				      DF-4973 fix
* Nilanjana Sanyal               03/09/2022			    US: 3082787 - Saperating the icon display in a common component commonHighlighPanelIconHum
* Muthu kumar                    06/17/2022                 DF-5051
* Nilanjana Sanyal               06/16/2022               DF-5116: Lightning_US 3082787 -  Icon visibility / Plan Member Page - (...) dots are being visible for less than 4 Icons
* Vardhman Jain                  09/02/2022                US: 3043287 Member Plan Logging stories Changes.
* Muthukumar					01/05/2023					US 4077850 Dual Eligible Icon/Plan Member page (Enabler)
* Abhishek Mangutkar			01/23/2023					US 4134646
* Bhakti Vispute				02/01/2023					User Story 4127354: T1PRJ0865978 -21642 RCC C13, Lightning - Core Remove Dual Eligible field and Icon from the IL MMP Plans (CRM)
* Nirmal Garg			        02/13/2023					US 4134646
* Nirmal Garg                   02/15/2023                  US 3939434 - Remove pharmacy service call
* Nirmal Garg                   02/15/2023                  DF-7193 changes
* Abhishek Mangutkar            03/01/2023                  US 4286520 Remove logic for assign member plan id for logging cases
* Jonathan Dickinson            03/31/2023                  User Story 4414983: T1PRJ0865978 - Next Best Action- Move the Existing alerts on Person Account & Plan Member to right hand side 
* Atul Patil                    07/27/2023                  DF-7839 & DF-7795 Logging Issue
* Hima Bindu Ramayanam          08/04/2023                  User Story 4811589: T1PRJ0865978- MF23283 - C06: Consumer Management: Disablement of new case creation for legacy delete members & plans.
* Vardhman Jain                 28/09/2023                  User Story 5029561: T1PRJ0865978 - C06; Enabler Case Management; Pharmacy - Case Detail/Block Access to Non-Medical Plans
* Pinky Vijur                   10/09/2023                  User Story 5066626: T1PRJ0865978- MF 27275- Mail Order Management: Pharmacy - Plan Member Page 'Mail Order – Register' Button
* Vardhman Jain                 28/09/2023                  DF-8239-Fix
* Vardhman Jain					06/11/2023                  DF-8277-Fix
* Pinky Vijur                   03/01/2024                  User Story 5428942: T1PRJ0865978- MF21712 Mail Order Management; Pharmacy- Guided Flow- Web Issues- Launch from pharmacy
******************************************************************************************************************************************/
import { api, track, wire } from 'lwc';
import { NavigationMixin } from 'lightning/navigation';
import crmserviceHelper from 'c/crmserviceHelper';
import { getLabels, getUserGroup, getReversedateFormat, getSessionItem, removeSessionItem, hcConstants, copyToClipBoard } from 'c/crmUtilityHum';
import { getModal } from './formFieldsmodel';
import getPolicyDetails from '@salesforce/apex/PoliciesSearchResults_LC_HUM.getPoliciesHighlightPanel';
import getMemberIcons from '@salesforce/apex/MemberIcons_LC_HUM.getMemberIconStatus';
import getUserDetails from '@salesforce/apex/GetCurrentUserDetails.getUserDetails';
import startLowIncome from '@salesforce/apexContinuation/EligibilityInformation_LC_HUM.callCIMedMultipleMemberService';
import getWebInformation from '@salesforce/apexContinuation/MemberMbeService_LC_HUM.getWebInformation';
import { getRecord, getFieldValue} from 'lightning/uiRecordApi';
import LEGACY_FIELD from '@salesforce/schema/MemberPlan.ETL_Record_Deleted__c';
import retrieveOiValue from '@salesforce/apexContinuation/MemberPlanDetail_LC_HUM.getOIData';
import extraHelpHum from '@salesforce/label/c.HUMExtrahelp';
import hasCRMS_111_StridesAccess from '@salesforce/customPermission/CRMS_111_StridesAccess';
import hasCRMS_205_CCSPDPPharmacyPilot from '@salesforce/customPermission/CRMS_205_CCSPDPPharmacyPilot';
import hasCRMS_685_PCCCustomerServiceAccess from '@salesforce/customPermission/CRMS_685_PCC_Customer_Service_Access';
import hasCRMS_206_CCSHumanaPharmacyAccess from '@salesforce/customPermission/CRMS_206_CCSHumanaPharmacyAccess';
import {invokeWorkspaceAPI, openLWCSubtab } from 'c/workSpaceUtilityComponentHum';
import HUM_Copy_Clipboard from '@salesforce/label/c.HUM_Copy_Clipboard';
import {performLogging,getLoggingKey,checkloggingstatus, setEventListener} from 'c/loggingUtilityHum';
import MEMBER_NAME from '@salesforce/schema/MemberPlan.Name';
import MEMBER_EXT_ID from '@salesforce/schema/MemberPlan.Member_Plan_External_Id__c';
import { CurrentPageReference } from 'lightning/navigation';
import { invokeGetMemberService } from 'c/pharmacyRightSourceIntegrationHum';
import USER_ID from '@salesforce/user/Id';
import NETWORK_ID_FIELD from '@salesforce/schema/User.Network_User_Id__c';
import PROFILE_NAME_FIELD from '@salesforce/schema/User.Profile.Name';
import ACCOUNT_ID from '@salesforce/schema/MemberPlan.MemberId';
import ACCOUNT_LEGACY_FIELD from '@salesforce/schema/MemberPlan.Member.ETL_Record_Deleted__c';
import ENTERPRISE_ID from '@salesforce/schema/MemberPlan.Member.Enterprise_ID__c';
import FIRST_NAME from '@salesforce/schema/MemberPlan.Member.FirstName';
import MEM_GEN_KEY from '@salesforce/schema/MemberPlan.Member.Mbr_Gen_Key__c'
import PRODUCT_TYPE from '@salesforce/schema/MemberPlan.Product__c';
import PRODUCT_TYPE__C from '@salesforce/schema/MemberPlan.Product_Type__c';
import LAST_NAME from '@salesforce/schema/MemberPlan.Member.LastName';
import Pharmacy_Mem_Not_found from "@salesforce/label/c.Pharmacy_Mem_Not_found";
import CRMRetail_Error_Label from "@salesforce/label/c.CRMRetail_Error_Label";
import { ShowToastEvent } from 'lightning/platformShowToastEvent';
import pharmacyServiceErrorMessage from '@salesforce/label/c.PHARMACY_DEMOGRAPHIC_FILTER_ERROR';
import { getCustomSettingValue } from 'c/genericCustomSettingValueHum';
import { getProfileDetails } from 'c/pharmacyHPIEIntegrationHum';
const fields = [LEGACY_FIELD, ACCOUNT_LEGACY_FIELD]; 
const MEMBER_NOT_FOUND = 'MEMBER NOT FOUND';
const switchValue = getCustomSettingValue('Switch', 'HPIE Switch');
export default class PolicyHighlightsPanelsHum extends NavigationMixin(crmserviceHelper) {
    @api recordId;
    @track oFormFields;
    @track bDataLoaded = false;
    @api header = {};
    @track memberIcons;
    @track hLimit;
    @track labels = getLabels();
    @track copyClipBoardMsg = HUM_Copy_Clipboard;
    @api sMemberName = "";
    @track sMemberId = "";
    @track accountId = "";
    @track showInteractions;
    @wire(getRecord, { recordId: '$recordId', fields })
    account;
    @api notMemberPlanInformation = false;
    isPharmacy;
    @track oUserGroup;
    @track accLegacyDelete;
    @track showRTISection = false;
    @track recordtype;
    //Vj-Logging stories Changes.*/
    @track memberPlanName;
    @track loggingkey;
    @track showLoggingIcon = true;
    @track autoLogging = true;
    @track personid;
    @track callMBEService = false;
    @track showMailOrderPharmacy = false;
    @track netWorkId;
    @track pharmacyMemberNotFoundMsg = Pharmacy_Mem_Not_found;
    @track errorHeader = CRMRetail_Error_Label;
    @track pharmacyAccountExist = false;
    @track accId;
    @track enterpriseId;
    @track memberFirstName;
    @track memberLastName;
    @track mmpValueFormat = 'H0336 - 001 - 000';
    @track mmpCombinationValue;
    @track ContractNum;
    @track PBPCode;
    @track SegId;
    @track isNotShowDualIcon = true;
    @track producttype;
    @track pharmacyservicemsg = pharmacyServiceErrorMessage;
    @track profilename;
    @track callPharmacyServiceFlag = false;
    @track memGenKey;
    @track productypeLOB;
    @track showMailOrderRegister = false;

	bSwitch5029561= false;
    @wire(CurrentPageReference)
    wiredPageRef(pageRef) {
        this.pageRef = pageRef;
    }

    @wire(getRecord, {
        recordId: '$recordId',
        fields: [MEMBER_NAME, MEMBER_EXT_ID, ACCOUNT_ID, ENTERPRISE_ID, ENTERPRISE_ID, FIRST_NAME, LAST_NAME, PRODUCT_TYPE,MEM_GEN_KEY,PRODUCT_TYPE__C]
    }) wiredAccount({
        error,
        data
    }) {
        if (error) {
            this.error = error;
            console.log('error in wire method', error);
        } else if (data) {
            this.memberPlanName = data.fields.Name.value;
            this.personid = data.fields.Member_Plan_External_Id__c.value;
            this.accId = data.fields.MemberId.value;
            this.enterpriseId = data.fields.Member.value.fields.Enterprise_ID__c.value;
            this.memberFirstName = data.fields.Member.value.fields.FirstName.value;
            this.memberLastName = data.fields.Member.value.fields.LastName.value;
            this.producttype = data.fields.Product__c.value;
            this.memGenKey = data.fields.Member.value.fields.Mbr_Gen_Key__c.value
            this.productypeLOB = data.fields.Product_Type__c;
            this.displayPharmacyButton();
            this.callPharmacyService();
			this.getProfileName();
        }
    }

    connectedCallback() {
        if (!this.notMemberPlanInformation) {
            this.navigateToAccount();
        }
        if (this.autoLogging) {
            getLoggingKey(this.pageRef).then(result => {
                this.loggingkey = result;
            });
        }
    }

    get isNewCaseDisabled() {
        return getFieldValue(this.account.data, ACCOUNT_LEGACY_FIELD) || getFieldValue(this.account.data, LEGACY_FIELD) ? true : false;
    }

    displayPharmacyButton() {
        if (this.pharmacyAccountExist && this.producttype && this.producttype?.toLowerCase() === 'med' && this.checkForPharmacyUser()) {
            this.showMailOrderPharmacy = true;
        }
    }

    getProfileName() {
        getUserDetails({}).then(res => {
			  const switchVal = getCustomSettingValue('Switch', '5029561');
            switchVal.then(result => {
                if (result && result?.IsON__c) this.bSwitch5029561 = result?.IsON__c;
                 if(this.bSwitch5029561){
                   if(this.producttype != 'MED' && (this.profilename === 'Humana Pharmacy Specialist' || ((this.profilename === 'Customer Care Specialist' || this.profilename === 'Customer Care Supervisor') && hasCRMS_205_CCSPDPPharmacyPilot )))
                    {
                        this.getTabId();
                    }
                 }
            });
            this.profilename = res?.Profile?.Name ?? undefined;
            if (res.Profile.Name === this.labels.HumUtilityPharmacy) {
                this.isPharmacy = true;
            }
            if (!this.notMemberPlanInformation) {
                if (res.Profile.Name === this.labels.HUMUtilityCSS || res.Profile.Name === this.labels.HUMAgencyCCSupervisor || res.Profile.Name === this.labels.HumUtilityPharmacy) {
                    this.showInteractions = true;
                }
                this.loadForm(res.Profile.Name);
                this.displayPharmacyButton();
            }
        }).catch(error => {
            console.log('error in getting Profile in policyHighlightPanel', error);
        });
    }
    chechRCCGBO(profile) {
        this.showRTISection = (this.oUserGroup.bRcc || this.oUserGroup.bGbo || this.checkForPharmacyUser() || this.oUserGroup.bGeneral) ? true : false;
    }

    checkForPharmacyUser() {
        if (this.profilename === 'Humana Pharmacy Specialist' || ((this.profilename === 'Customer Care Specialist' || this.profilename === 'Customer Care Supervisor') && (hasCRMS_205_CCSPDPPharmacyPilot || hasCRMS_206_CCSHumanaPharmacyAccess))) {
            return true;
        } else {
            return false;
        }
    }
	
	    getTabId() {
        invokeWorkspaceAPI('isConsoleNavigation').then(isConsole => {
            if (isConsole) {
                invokeWorkspaceAPI('getFocusedTabInfo').then(focusedTab => {
                    this.tabId = focusedTab.tabId;
                    this.closesubtab();
                });
            }
        });
    }

    closesubtab() {
        try {
                this.showToast('','No access to the page due to restricted permissions, Please contact your Supervisor','warning');
                invokeWorkspaceAPI('isConsoleNavigation').then(isConsole => {
                    if (isConsole) {
                        invokeWorkspaceAPI('closeTab', {
                            tabId: this.tabId
                        });
                    }
                });
        }
        catch (error) {
            console.log('Error==', error);
        }
    }
    /**
     * Make an apex call to load policy details data
     */
    loadForm(profile) {
        const me = this;
        me.oUserGroup = getUserGroup();
        const oFormFields = getModal('Member', me.oUserGroup, profile);
        me.header = oFormFields.header;
        getPolicyDetails({
            recId: this.recordId
        })
            .then(oData => {
                me.sMemberName = oData.Member.Name;
                me.sMemberId = oData.Member.Id;
                me.accLegacyDelete = oData.ETL_Record_Deleted__c;
                me.ContractNum = oData?.Plan?.Contract_Number__c;
                me.PBPCode = oData?.Plan?.PBP_Code__c;
                me.SegId = oData?.Plan?.Medicare_Segment_ID__c;
                me.mmpCombinationValue = me.ContractNum + ' - ' + me.PBPCode + ' - ' + me.SegId;
                if (me.mmpCombinationValue === me.mmpValueFormat) {
                    me.isNotShowDualIcon = false;
                }
                me.recordtype = oData?.Member?.RecordType?.Name ?? '';
                me.processData(oData, oFormFields);
                this.chechRCCGBO(profile);
                this.loadMemberIcons();
            })
            .catch(error => {
                console.error("Error", error);
            });

    }


    fetchLowIncome() {
        const me = this;
        startLowIncome({ recId: this.recordId }).then(result => {
            me.isLowIncome = result ? result : false;
            me.memberIcons.find(item => item.sIconName === 'LowIncomeSubsidy').bIconVisible = (!me.isLowIncome) ? false : true;
            me.memberIcons.find(item => item.sIconName === 'LowIncomeSubsidy').sIconLabel = extraHelpHum;
            if (me.isLowIncome) {
                me.refreshIcons();
            }
        }).catch(error => {
            console.error("Error in LowIncome", error);
        });
    }

    /**
     * Navigates to the Account detail page
     */
    navigateToAccount() {
        let policyAccoutId = getSessionItem(hcConstants.MEMBER_POLICY_ID);
        if (policyAccoutId) {
            this.accountId = policyAccoutId.split('##')[1];
            this.navigateToViewAccountDetail(this.accountId, 'Account', 'view');
            removeSessionItem(hcConstants.MEMBER_POLICY_ID);
        }
    }

    /**
     * Update data to the value property of the Model
     * @param {*} oData
     * @param {*} oFormFields
     */
    @api
    processData(oData, oFormFields) {
        this.oFormFields = [
            ...oFormFields.recordDetail.map(item => {
                let value;
                let iconCls = "";
                if (item.mapping.indexOf('.') > 0) {
                    const tmp = item.mapping.split('.');
                    const tmpval = oData.hasOwnProperty(tmp[0]) ? oData[tmp[0]][tmp[1]] : "";
                    value = tmpval ? tmpval : '';
                }
                else {
                    value = oData.hasOwnProperty(item.mapping) ? oData[item.mapping] : "";
                }
                if (item.seperator) {
                    const aFields = item.mapping.split(item.seperator);
                    let primaryVal = this.getValue(aFields[0], oData);
                    let secondaryVal = this.getValue(aFields[1], oData);

                    if (item.bDate) {
                        if (primaryVal) {
                            primaryVal = getReversedateFormat(primaryVal, hcConstants.DATE_MDY);
                        }
                        if (secondaryVal) {
                            secondaryVal = getReversedateFormat(secondaryVal, hcConstants.DATE_MDY);
                        }
                    }
                    value = (primaryVal || secondaryVal) ? primaryVal + " " + item.seperator + " " + secondaryVal : "";
                }
                if (item.showIcon && item.mapping === 'Member_Coverage_Status__c') {
                    value = value ? value : "";
                    iconCls = `status-${value.toLowerCase()}`;
                }
                if (item.hasOwnProperty('copyToClipBoard') && !oData[item.mapping]) {
                    item.copyToClipBoard = false;
                }
                if (item.mapping === 'Policy_Platform__c') {
                    if (oData[item.mapping] == 'LV' && oData?.Plan?.Client_Number__c) {
                        value = oData[item.mapping] + ' (' + oData?.Plan?.Client_Number__c + ')';
                    } else {
                        value = oData[item.mapping];
                    }
                }

                return {
                    ...item,
                    value,
                    iconCls
                }
            })]
        this.bDataLoaded = true;
    }

    getValue(sMapping, oData) {
        let value = "";
        if (sMapping.indexOf('.') > 0) {
            const tmp = sMapping.split('.');
            const tmpval = oData.hasOwnProperty(tmp[0]) ? oData[tmp[0]][tmp[1]] : "";
            value = tmpval ? tmpval : '';
        } else {
            value = oData.hasOwnProperty(sMapping) ? oData[sMapping] : "";
        }
        return value;
    }



    loadMemberIcons() {
        const me = this;
        this.hLimit = hcConstants.hLimit;
        console.log('Horizontal icon limit in Policy Page', this.hLimit);
        const iconParams = {
            sPageName: 'Policy Member',
            sRecordId: me.recordId
        };
        getMemberIcons(iconParams).then((result) => {
            if (result && result.bIconsPresnt) {
                result.lstMemberIcons.forEach(item => {
                    if (item.sIconName === 'WebRegistered') {
                        item.bIconVisible = false;
                    }
                    if (item.sIconName === 'LowIncomeSubsidy') {
                        item.bIconVisible = false;

                    }
                    //make OtherInsurance Icon not visible on pageLoad, added servicecall function to verify OtherInsurance value
                    if (item.sIconName === hcConstants.OTHER_INSURANCE_ICON) {
                        item.bIconVisible = false;
                        me.callMBEService = true;
                        //this.fetchOIData();

                    }
                    if (item.sIconName === hcConstants.DualEligiblity) {
                        item.bIconVisible = false;
                        me.callMBEService = true;
                    }

                });
                if (me.callMBEService) {
                    this.fetchOIData();
                }
                me.memberIcons = result.lstMemberIcons;
                this.refreshIcons();
                if (this.account.data && !(this.account.data.fields.ETL_Record_Deleted__c.value)) {
                    this.fetchLowIncome();
                    this.fetchMyHumanaDetails();
                }
            }

        }).catch((error) => {
            console.log('Error Occured', error);

        });

    }
    /*Implementation for Icon display*/
    refreshIcons() {
        this.template.querySelector("c-common-highlight-panel-icon-hum").loadMemberIcons(this.memberIcons, this.hLimit);
    }

    openNew(event) {
        if (!this.recordId.startsWith('0Sb')) {
            openLWCSubtab('caseInformationComponentHum', this.recordId, { label: 'New Case', icon: 'standard:case' });
        }
    }

    /*
    * Description - this method is called if OtherInsurance icon need to be displayed,
                    to make service call to verify value of OtherInsurance if Yes
    */
    fetchOIData() {
        const me = this;
        retrieveOiValue({ sRecId: this.recordId }).then(result => {
            me.isOiData = result.validOIWrapperList ? true : false;
            me.memberIcons.find(item => item.sIconName === hcConstants.OTHER_INSURANCE_ICON).bIconVisible = (!me.isOiData) ? false : true;
            me.isDualEligible = result.isMemDualEligibleInLastTwelveMonth;
            if (!hasCRMS_205_CCSPDPPharmacyPilot && !hasCRMS_685_PCCCustomerServiceAccess && me.isNotShowDualIcon) {
                me.memberIcons.find(item => item.sIconName === hcConstants.DualEligiblity).bIconVisible = me.isDualEligible;
            }
            if (me.isDualEligible || me.isOiData) me.refreshIcons();
        }).catch(error => {
            console.error("Error in fetchOIData", error);
        });
    }

    /*
    * Params - {Memberplan id}
    * Description - this method is called on load of page and will fetch myhumana section details
                    and decide to show webregistered icon or not.
    */
    async fetchMyHumanaDetails() {
        try {
            const result = await getWebInformation({ sPolicyMemID: this.recordId });
            if (result !== null && result.hasOwnProperty('IsWebRegistered') && result.IsWebRegistered === 'true') {
                const web = this.memberIcons.findIndex(item => item.sIconName === 'WebRegistered');
                this.memberIcons[web].bIconVisible = '';
                if (web !== -1) {
                    this.memberIcons[web].bIconVisible = true;
                    this.refreshIcons();
                }
            }
        }
        catch (error) {
            console.log('error in policyHighlights for webregistered--', error);
        }
    }

    copyToBoard(event) {
        this.oFormFields.forEach(item => {
            let copiedVal;
            if (item.mapping === event.currentTarget.dataset.field) {
                copiedVal = (item.mapping === 'Name') ? item.value.substring(0, item.value.length - 2) : item.value;
                copyToClipBoard(copiedVal);
            }
        });
    }
    //logging changes
    handleLogging(event) {
        try {
            if (this.loggingkey && checkloggingstatus(this.loggingkey)) {
                performLogging(event, this.createRelatedField(), 'Member Plan Highlights', this.loggingkey, this.pageRef);
            } else {
                getLoggingKey(this.pageRef).then(result => {
                    this.loggingkey = result;
                    if (this.loggingkey && checkloggingstatus(this.loggingkey)) {
                        performLogging(event, this.createRelatedField(), 'Member Plan Highlights', this.loggingkey, this.pageRef);
                    }
                })
            }
        }
        catch (error) {
            console.log('error in memberplan logging-', error);
        }
    }

    createRelatedField() {
        return [{
            label: 'Member ID',
            value: this.memberPlanName
        }];
    }

    handleMailOrderPharmacyClick() {
        switchValue.then(result => {
            if (result && result?.IsON__c && result?.IsON__c === true) {
                this.navigateToHPIE(this.accId, this.memberFirstName, this.memberLastName, this.enterpriseId, this.memGenKey);
            } else {
                this.navigateToHP(this.accId, this.memberFirstName, this.memberLastName, this.enterpriseId);
            }
        });
    }

    @wire(getRecord, {
        recordId: USER_ID,
        fields: [NETWORK_ID_FIELD, PROFILE_NAME_FIELD]
    }) wireuser({
        error,
        data
    }) {
        if (error) {
            this.error = error;
        } else if (data) {
            this.netWorkId = data.fields.Network_User_Id__c.value;
            this.profilename = data.fields.Profile.value.fields.Name.value;
        }
    }

    @wire(CurrentPageReference)
    getStateParameters(currentPageReference) {
        if (currentPageReference) {
            this.getStateParameters(currentPageReference.state);
        }
    }



    navigateToHP(recordID, firstName, lastName, enterpriseID) {
        this[NavigationMixin.Navigate]({
            type: 'standard__navItemPage',
            attributes: {
                apiName: 'Pharmacy_App_Page',
                recordId: recordID
            },
            state: {
                c__AccountID: recordID,
                c__enterpriceID: enterpriseID,
                c__fName: firstName,
                c__lName: lastName,
                c__PlanMemberId: this.recordId,
                c__userId: btoa(this.netWorkId ?? ''),
                c__userProfile: btoa(this.profilename?.replace(' ', '_') ?? '')
            }
        });
    }

    navigateToHPIE(recordID, firstName, lastName, enterpriseID, memGenKey) {
        this[NavigationMixin.Navigate]({
            type: 'standard__navItemPage',
            attributes: {
                apiName: 'Pharmacy_HPIE',
                recordId: recordID
            },
            state: {
                c__AccountID: recordID,
                c__enterpriceID: enterpriseID,
                c__fName: firstName,
                c__lName: lastName,
                c__PlanMemberId: this.recordId,
                c__userId: btoa(this.netWorkId ?? ''),
                c__userProfile: btoa(this.profilename?.replace(' ', '_') ?? ''),
                c__memGenKey:btoa(memGenKey ?? ''),
                c__ProductTypeLOB: this.productypeLOB,
                c__MemberId : btoa(this.memberPlanName)
            }
        });
    }

    showToast(strTitle, strMessage, strStyle) {
        this.dispatchEvent(new ShowToastEvent({
            title: strTitle,
            message: strMessage,
            variant: strStyle
        }));
    }

    getStateParameters(stateParameters) {
        if (stateParameters?.state?.ws) {
            let stateParameter = stateParameters?.state?.ws ?? null;
            if (stateParameter && stateParameter.includes('c__pharmacyMember/Yes')) {
                this.showMailOrderPharmacy = true;
            }
            else if (stateParameter && stateParameter.includes('c__pharmacyMember/No')) {
                this.showMailOrderPharmacy = false;
            }
            else {
                this.callPharmacyServiceFlag = true;
                this.callPharmacyService();
            }
        }
    }

    callPharmacyService() {
        if (this.callPharmacyServiceFlag && this.producttype && this.producttype?.toLowerCase() === 'med') {
            switchValue.then(result => {
                if (result && result?.IsON__c && result?.IsON__c === true) {
                    setTimeout(() => {
                        this.callHPIE();
                    }, 10)
                } else {
                    setTimeout(() => {
                        this.callRS();
                    }, 10)
                }
            })
        }
    }

    callHPIE() {
        getProfileDetails(this.enterpriseId, this.netWorkId, this.organization ?? 'HUMANA').then(result => {
            if (result && Object.keys(result)?.length > 0) {
                this.pharmacyAccountExist = result?.AccountId ? true : false;
                this.displayPharmacyButton();
            }
            else{
                this.displayMailOrderRegister(); 
            }
        }).catch(error => {
            console.log(error);

        })
    }

    callRS() {
        let customerValue = '';
        let todaysdate = new Date();
        let sDate = new Date();
        sDate.setMonth(todaysdate.getMonth() - 3);
        let startDate = ((sDate.getMonth() + 1).toString().length == 1 ? '0' + (sDate.getMonth() + 1) : (sDate.getMonth() + 1)) + '/' + (sDate.getDate().toString().length === 1 ? '0' + sDate.getDate() : sDate.getDate()) + '/' + sDate.getFullYear();
        let endDate = ((todaysdate.getMonth() + 1).toString().length == 1 ? '0' + (todaysdate.getMonth() + 1) : (todaysdate.getMonth() + 1)) + '/' + (todaysdate.getDate().toString().length === 1 ? '0' + todaysdate.getDate() : todaysdate.getDate()) + '/' + todaysdate.getFullYear();
        invokeGetMemberService(this.enterpriseId, this.netWorkId, startDate, endDate, this.accountId)
            .then(result => {
                if (result && result?.message) {
                    if (result?.message?.toLowerCase().includes('serviceerror')) {
                        console.log("Error Occured", result?.message);
                        this.showToast(this.errorHeader, this.pharmacyservicemsg, "error");

                    }
                } else {
                    if (result != null && result != undefined && result != '' && result?.length > 0) {
                        let pharmacyMemberDetails = JSON.parse(result);
                        if (pharmacyMemberDetails?.GetMemberReponse?.Customers?.ErrorDescription) {
                            if (pharmacyMemberDetails.GetMemberReponse.Customers.ErrorDescription.toUpperCase() == MEMBER_NOT_FOUND) {
                                this.showToast(this.errorHeader, this.pharmacyMemberNotFoundMsg, "error");
                                this.displayMailOrderRegister();
                            }
                        }
                        else {
                            customerValue = pharmacyMemberDetails?.GetMemberReponse?.Customers?.Customer[0]?.AccountNumber?.CustomerValue ?? '';

                            this.pharmacyAccountExist = customerValue ? true : false;
                            this.displayPharmacyButton();
                            this.displayMailOrderRegister();

                        }
                    }
                    else {
                        this.showToast(this.errorHeader, this.pharmacyMemberNotFoundMsg, "error");

                    }
                }
            }).catch(error => {
                console.log("Error Occured", error);
                this.showToast(this.errorHeader, this.pharmacyservicemsg, "error");

            })
    }
    displayMailOrderRegister()
    {
        if(!this.pharmacyAccountExist && !this.showMailOrderPharmacy && this.checkForPharmacyUser())
        {
            this.showMailOrderRegister = true;
        }
    }
    handleMailOrderRegisterClick(event)
    {
       
    getCustomSettingValue('Certificate','PharmacyWebRegister')
    .then(result => {     
        let webUrl = result?.CertificateListURL__c ?? '';
        if (webUrl && webUrl?.length > 0) {
            window.open(webUrl,"_blank","toolbar=yes, scrollbars=yes, resizable=yes,width=2000");
        }
    })
    .catch(error => {
        console.log(error);
    })
    }
}