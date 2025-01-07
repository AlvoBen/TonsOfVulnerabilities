/*******************************************************************************************************************************
LWC JS Name : caseValidationsHum.js
Function    : validations Utility functions 
Modification Log: 
Developer Name                       Date                         Description
*--------------------------------------------------------------------------------------------------------------------------------
* Ritik Agarwal                     02/07/2022                   initial version
* Ritik Agarwal                     02/09/2022                   created a utility methods to get whole DOM element to further use for values etc....
* Ritik Agarwal                     02/10/2022                   created a framework for show header msg along with field level msge
* Manohar Billa                     07/28/2022                   Code change for enabling permission set for case comment feature
* Prasuna Pattabhi                                 09/30/2022               US 3730127 - Edit and closed edit Medicare Case
* Jasmeen Shangari                  09/30/20222                  By pass Medicare PartC/PartD check for Agenet, Group, Provider layouts
* Manohar Billa                     10/12/2022                   Enabling Case comment validations for RCC User with 206 permission set
 * Pooja Kumbhar                    03/08/2023        		     Regression - case is not getting created for medicare case after filling the Response status field, getting error message as "Response Status: You must choose a value."
 * Gowthami Thota                   04/07/2023                   US4461952 - Resolve error "Please enter a value for either Interacting With or Interacting With Name."   
*********************************************************************************************************************************/

import { LightningElement, track } from "lwc";
import { getUserGroup } from "c/crmUtilityHum";

export default class CaseValidationsHum extends LightningElement {
    @track headerSubMsge = ""; //this variable will contains top page error msge that comes from different validations
    @track userGroup = getUserGroup();

    /**
     * Update Field validation with message
     * @param {*} field - the selector which we want to show msge beneath field
     * @param {*} message - the field level msge like complete this field
     * * @param {*} headerMsg - the top header error msge 
     * @param {*} isHeaderMsg - if true means along with individual field msge , top page error msge also visible
     */
    updateFieldValidation(field, message, headerMsg, isHeaderMsg) {
        let isValid = true;
        field.forEach((item) => {
            if (!item.value || item.value === "--None--") {
                isValid = false;
                item.nodeName !== "lightning-input-field".toUpperCase()
                    ? item.setCustomValidity(message)
                    : "";
            } else {
                if (item.nodeName !== "lightning-input-field".toUpperCase()) {
                    item.setCustomValidity("");
                }
            }
            item.reportValidity();
        });
        if (isHeaderMsg) {
            this.showPageMsg(isValid, headerMsg);
        }
        return isValid;
    }

    /**
    * show top page error msge
    * @param {*} isValid - true means there is no any kind of error present on page after verify all validations criteira
    *  @param {*} headerMsg - the top header error msge 
    */
    showPageMsg(isValid, headerMsg) {
        const errHeader = this.template.querySelector(".page-error-message");
        if (!isValid) {
            errHeader.innerHTML =
                '<div class="slds-m-horizontal_small slds-p-around_small hc-error-header" style="color:white; font-size:20px; background: #c23934;border-radius:0.3rem">Review the error on this page.</div>' +
                '<p class="slds-p-horizontal_large slds-p-vertical_small" style="color:#c23934; font-size: 13px;">' +
                headerMsg +
                "</p>";
        } else {
            errHeader.innerHTML = " ";
        }
    }

    /**
    * this method will give whole field of HTML
    * @param {*} selector - { "lightning-input-field": "fieldName" } - key is selector name that needs to validate and value is which selector attribute needs to validate field
    *  @param {*} fields - ["Interacting_With_Type__c", "Interacting_With__c"] - array of field that needs to validate
    * Note  - it will return array of fields that is matching the condtions and needs to validate further and order of fields will be return whatever field present on DOM first
    */
    checkNullValues(selector, fields) {
        let selectorToValidate = [];
        Object.keys(selector).forEach((item) => {
            this.template.querySelectorAll(item).forEach((i) => {
                if (fields.includes(i[selector[item]])) {
                    selectorToValidate.push(i);
                }
            });
        });
        return selectorToValidate;
    }

    /**
    * this method will validate all criteria based validations for create/edit case pages
    * @param {*} dataBaseResult - response that comes from caseValidation_LH_HUM apex class and it is called from caseInformationComponent on click of save from case create/edit form
    */
    performValidations(dataBaseResult) {
        let caseData = this.resultData;
        this.headerSubMsge = "";
        let interactingRecTypeName = dataBaseResult.hasOwnProperty(
            "interactingWithRecType"
        )
            ? dataBaseResult.interactingWithRecType
            : null;
        let profileName = dataBaseResult.hasOwnProperty("profileName")
            ? dataBaseResult.profileName
            : null;
        let mamplanProduct = dataBaseResult.hasOwnProperty("memPlanProduct")
            ? dataBaseResult.memPlanProduct
            : null;
        let memPlanPolicyProductType = dataBaseResult.hasOwnProperty(
            "memPlanPolicyProductType"
        )
            ? dataBaseResult.memPlanPolicyProductType
            : null;
        let memPlanPolicyMajorLob = dataBaseResult.hasOwnProperty(
            "memPlanPolicyMajorLob"
        )
            ? dataBaseResult.memPlanPolicyMajorLob
            : null;

        //this if will make sure that these validation should not fire on edit case as on edit case it was handed OOTB
        if (!this.recordId) {
            this.handleInteractingValidation(interactingRecTypeName, profileName);
        }

        //this method will handle complaint related validations
        if (mamplanProduct != null)
        {
            this.handleComplaintAssociatePlan(
                memPlanPolicyProductType,
                memPlanPolicyMajorLob,
                mamplanProduct,
                profileName
            );
        }		
        //this method will handle complainttype related validations
        this.handleComplaintTypeValidation(mamplanProduct, profileName);
        // this if will make sure that this validation will only fire in case of HP user and HP recordtypes along with RCC user with 206 permission set
        if (
           ((profileName === "Humana Pharmacy Specialist") &&
            caseData.prefillValues.caseRecordTypeName.search("HP") !== -1 &&
            (this.userGroup.bPharmacy || this.userGroup.bGeneral)) || (this.userGroup.bRSOHP)
        ) {
            this.handleLogCodeClassificationTypes();
        }
        
        //This if will make sure that this validation is only fires when the logged in user is non hp and case record type is Member Case
        if(profileName !== "Humana Pharmacy Specialist" && caseData.prefillValues.caseRecordTypeName === 'Member Case'){
            this.handleComplaintAndGnAValidation(profileName,caseData);
        }
        //this method will handle status field with value Response-status related validations
        this.handleResolutionDateValidation(profileName);
        //this IF will make sure that user is running with criteria based validations
        if (this.headerSubMsge.length > 0) {
            this.showPageMsg(false, this.headerSubMsge);
            return false;
        }
        return true;
    }

     /**
    * perform interacting with type validation
    * @param {*} interactingRecTypeName
    *  @param {*} profileName - current user profile
    */
    handleInteractingValidation(interactingRecTypeName, profileName) {
        let fldSelecotr = this.checkNullValues(
            { "lightning-input-field": "fieldName" },
            ["Interacting_With_Type__c", "Interacting_With__c","Interacting_With_Name__c"]
        );
        //this if for validation - Interacting_With_Name_Cannot_be_Blank
        if (
            profileName &&
            profileName !== "Deployment" &&
            fldSelecotr[1].value.length <= 0 && fldSelecotr[2].value.length <=0
        ) {
            this.headerSubMsge +=
                "Please enter a value for either Interacting With or Interacting With Name." +
                "<br/>";
            return;
        }
        //this if for validation - Interacting_With_Name_Type_Validation
        if (
            profileName &&
            profileName !== "Deployment" &&
            interactingRecTypeName &&
            ((interactingRecTypeName === "Member" &&
                fldSelecotr[0].value !== "Member") ||
                (interactingRecTypeName === "Provider" &&
                    fldSelecotr[0].value !== "Provider") ||
                (interactingRecTypeName === "Agent/Broker" &&
                    fldSelecotr[0].value !== "Agent") ||
                (interactingRecTypeName === "Group" &&
                    fldSelecotr[0].value !== "Group") ||
                (interactingRecTypeName === "Unknown Agent/Broker" &&
                    fldSelecotr[0].value !== "Unknown-Agent") ||
                (interactingRecTypeName === "Unknown Member" &&
                    fldSelecotr[0].value !== "Unknown-Member") ||
                (interactingRecTypeName === "Unknown Provider" &&
                    fldSelecotr[0].value !== "Unknown-Provider") ||
                (interactingRecTypeName === "Unknown Group" &&
                    fldSelecotr[0].value !== "Unknown-Group") ||
                (interactingRecTypeName === "Internal" &&
                    fldSelecotr[0].value !== "Internal"))) {
            this.headerSubMsge +=
                "The account type entered into the Interacting With Type field does not match the person/entity selected in the Interacting With field. Please update one of the fields to resolve this error." +
                "<br/>";
        }
    }

    /**
    * perform compalin with plan related validation
    */
    handleComplaintAssociatePlan(
        memPlanPolicyProductType,
        memPlanPolicyMajorLob,
        mamplanProduct,
        profileName
    ) {
        let fldSelecotr = this.checkNullValues(
            { "lightning-input-field": "fieldName" },
            ["Member_Plan_Id__c", "Complaint__c"]
        );
        if (
            profileName &&
            profileName !== "Deployment" &&
            fldSelecotr[0].value &&
            (((fldSelecotr[1].value == "Yes" ||
                fldSelecotr[1].value == "Yes - Medicaid") &&
                memPlanPolicyProductType == "MAPD") ||
                ((fldSelecotr[1].value == "Yes" ||
                    fldSelecotr[1].value == "Yes - Medicaid" ||
                    fldSelecotr[1].value == "Yes - Medicare Part C") &&
                    memPlanPolicyProductType == "PDP") ||
                ((fldSelecotr[1].value == "Yes" ||
                    fldSelecotr[1].value == "Yes - Medicaid" ||
                    fldSelecotr[1].value == "Yes - Medicare Part D") &&
                    memPlanPolicyProductType == "MA") ||
                ((fldSelecotr[1].value == "Yes" ||
                    fldSelecotr[1].value == "Yes - Medicaid") &&
                    (memPlanPolicyProductType == "" ||
                        memPlanPolicyProductType == null) &&
                    [
                        "MEF",
                        "MER",
                        "MEP",
                        "MES",
                        "MGP",
                        "MGR",
                        "MPD",
                        "MRO",
                        "MRP",
                        "PDP"
                    ].indexOf(memPlanPolicyMajorLob) !== -1) ||
                ((fldSelecotr[1].value == "Yes" ||
                    fldSelecotr[1].value == "Yes - Medicare Part C" ||
                    fldSelecotr[1].value == "Yes - Medicare Part D") &&
                    (memPlanPolicyProductType == "" ||
                        memPlanPolicyProductType == null) &&
                    memPlanPolicyMajorLob == "MCD") ||
                ((fldSelecotr[1].value == "Yes - Medicaid" ||
                    fldSelecotr[1].value == "Yes - Medicare Part C" ||
                    fldSelecotr[1].value == "Yes - Medicare Part D") &&
                    !(
                        [
                            "MEF",
                            "MER",
                            "MEP",
                            "MES",
                            "MGP",
                            "MGR",
                            "MPD",
                            "MRO",
                            "MRP",
                            "PDP",
                            "MCD"
                        ].indexOf(memPlanPolicyMajorLob) !== -1
                    )))
        ) {
            this.headerSubMsge +=
                "Complaint option selected does not match the policy associated to the case, please review the Help Hover Over for guidance." +
                "<br/>";
        }
    }

    /**
    * perform log code related validation for HP user
    */
    handleLogCodeClassificationTypes() {
        let fldSelecotr = this.checkNullValues({ "lightning-input": "name" }, [
            "classificationType"
        ]);
        let fldSelect = this.checkNullValues({ "lightning-combobox": "name" }, [
            "pharmacyLogCode"
        ]);
        let fldSelectcasecommnt = this.checkNullValues(
            { "lightning-textarea": "label" },
            ["Case Comments"]
        );

        if(fldSelect && fldSelect.length>0){
            if (fldSelecotr[0].value && fldSelect[0].value == "--None--") {
                this.headerSubMsge += "Log Code: You must enter a value" + "<br/>";
            } else if (
                fldSelecotr[0].value &&
                fldSelect[0].value !== "--None--" &&
                (fldSelectcasecommnt[0].value == "" || fldSelectcasecommnt[0].value == undefined )
            ) {
                this.headerSubMsge += "Case Comments: You must enter a value" + "<br/>";
            }
        }
    }

    /**
    * perform compalin type related validation
    * params - mamplanProduct - product of memberplan that is associated with case
    * params - profileName -    current user profilename
    */
    handleComplaintTypeValidation(mamplanProduct, profileName) {
        let fldSelecotr = this.checkNullValues(
            { "lightning-input-field": "fieldName" },
            ["Complaint__c","Complaint_Reason__c", "Complaint_Type__c"]
        );

        if (
            profileName &&
            profileName !== "Deployment" &&
            (fldSelecotr[0].value == "Yes" ||
                fldSelecotr[0].value == "Yes - Medicaid" ||
                fldSelecotr[0].value == "Yes - Medicare Part C" ||
                fldSelecotr[0].value == "Yes - Medicare Part D") &&
            mamplanProduct &&
            mamplanProduct === "MED" &&
            fldSelecotr[1].value !== "" && fldSelecotr[2].value === ""
        ) {
            this.headerSubMsge +=
                "Complaint Type drop-down is required for this policy. Product = MED. Value must be selected in order to save." +
                "<br/>";
        }
    }

    /**
    * perform compalin and GnA type related validation
    * params - caseData - case related data
    * params - profileName -  current user profilename
    * Description - this validation will verify if GnA and Complaint's value is yes, throw error to the user
    */
    handleComplaintAndGnAValidation(profileName,caseData){
        let recordTypeName = caseData.prefillValues.caseRecordTypeName;
        let interactingAboutType = caseData.objCase.Interacting_About_Type__c;
        let fldSelecotr = this.checkNullValues(
            { "lightning-input-field": "fieldName" },
            ["Interacting_With_Type__c","G_A_Rights_Given__c","Complaint__c"]
        );
        if(fldSelecotr && fldSelecotr.length>0){
            if (
                profileName &&
                profileName !== "Deployment" && 
                (recordTypeName == "Member Case" ||
                    recordTypeName == "Closed Member Case" || 
                    recordTypeName == "Medicare Case" || 
                    recordTypeName == "Closed Medicare Case"
                ) && (fldSelecotr[2].value == "Yes" ||
                    fldSelecotr[2].value == "Yes - Medicare Part C" ||
                    fldSelecotr[2].value == "Yes - Medicare Part D" ||
                    fldSelecotr[2].value == "Yes - Medicaid"
                )&& fldSelecotr[1].value == "Yes" && 
                (fldSelecotr[0].value == "Member" ||
                    fldSelecotr[0].value == "Unknown-Member" ||
                    fldSelecotr[0].value == "Member Representative or Caregiver" ||
                    fldSelecotr[0].value == "Internal" ||
                    fldSelecotr[0].value == "Government" ||
                    fldSelecotr[0].value == "Care Manager"
                ) &&(interactingAboutType == "Member")){
                    this.headerSubMsge +=
                    "The case can't contain both Complaint and G&A Rights Given. Please choose the correct indicator or create 2 separate cases." + "<br/>";  
                } 
        }
    }
    /**
         * perform stauts wits pending-response value related validation
         * params - PrefillData, caseobj
         */
    handleResponseStautsValidation(PrefillData, caseobj) {
            if (PrefillData.responseStatus !== "Internal" && PrefillData.responseStatus !== "External") {
                if (

                    PrefillData.profile !== "Deployment" &&
                    caseobj.Status === "Pending - Response" &&
                    caseobj.Response_Status__c !== "Internal" &&
                    caseobj.Response_Status__c !== "External" && caseobj.Response_Status__c !== '' && caseobj.Response_Status__c !== "undefined" && caseobj.Response_Status__c !== null
                ) {
                    this.headerSubMsge +=
                        "Response Status: You must choose a value." + "<br/>";
                    this.showPageMsg(false, this.headerSubMsge);
                    return false;
                }
            }
            return true;
        }
	/**
    * perform stauts wits Resoltion Type and Resolution Date value related validation
    * params - profile -    current user profilename
    */
     handleResolutionDateValidation(profile) {
        let fldSelecotr = this.checkNullValues({ "lightning-input-field": "fieldName", input: "name" },["OGO_Resolution_Type__c", "OGO_Resolution_Date__c"]);
        if (
            profile && profile !== "Deployment" && fldSelecotr[0]!=undefined && fldSelecotr[1]!=undefined &&
            (
                ( fldSelecotr[0].value === "Oral" || fldSelecotr[0].value === "Written") && 
                (fldSelecotr[1].value === null || fldSelecotr[1].value === '')
            )
        ) {
            this.headerSubMsge +="Resolution Date: You must enter a value." + "<br/>";
        }
      } 
}