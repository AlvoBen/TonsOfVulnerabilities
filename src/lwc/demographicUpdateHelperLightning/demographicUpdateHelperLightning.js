import { LightningElement } from 'lwc';
import { pubsub } from 'c/pubsubComponent';
import { updateDataModel, addressVerificationDataModel, medicareAVFDataModel, updateMemberRequestModel, Members, TemplateModel } from './demographicUpdateModelLightning';
import { initialize } from './demographicUpdateInitialLightning';
import { concatAddress, populateDataModel, deriveValue, deriveRequestValue, deriveSSNRequestValue, deriveElectronicRequestValue, 
         derivePhoneRequestValue, derivePhoneExtRequestValue, populateRequestDataModel, deriveRequestUpdateType, deriveAddressRequestValue, 
         mapOriginalMemberData, procureEligibleMemberDetails, procureEligibleMemberDetailsList, deriveBirthdateRequestValue, procureTemplateModel,
         checkProcumentStatus, triggerDependentsSubmit, triggerProcessFinish, procureMiddleName } from './demographicUpdateGenericLightning';
import US0015656SwitchLabel from '@salesforce/label/c.US0015656SwitchLabel';
export default class DemographicUpdateHelperLightning extends LightningElement {
    policyMemberId = '';
    templateName = '';
    recordCaseId = '';
    updateDataModelClass = new updateDataModel();
    updatedDataModel = {};
    addressVerificationModeClass = new addressVerificationDataModel();
    addressVerificationModel = {};
    templateModelClass = new TemplateModel();
    templateDataModel = [];
    updateRequestModelClass = new updateMemberRequestModel();
    updateRequestModel = {};
    membersClass = new Members();
    member = {};
    displayDataModel = [];
    eligibleInfo = {};
    coverageInfo = [];
    dependentUpdReqModel = [];
    idCardRequired = false;
    //Mega update mode - starts
    eligibleInfoList = []; 
    primaryUpdReqModel = []; 
    procurementStatus = {
        self: undefined, dependents: undefined
    };
    platformMemberMap = {
        em: [],
        lv: []
    };
    platformMemberMsgMap = [];
    sequenceUpdateStatePrimary = [];
    sequenceUpdateStateDependent = [];
    //Mega update mode - ends
    //MiddleName - Start
    middleName = '';
    middleNameFound = false;
    //MiddleName - End
    isDateInFuture = false;
    isMedicaid = false; 
    primaryMemberName = null;
    concatAddress = concatAddress;
    deriveValue = deriveValue;
    deriveRequestValue = deriveRequestValue;
    deriveSSNRequestValue = deriveSSNRequestValue;
    deriveElectronicRequestValue = deriveElectronicRequestValue;
    derivePhoneRequestValue = derivePhoneRequestValue;
    derivePhoneExtRequestValue = derivePhoneExtRequestValue;
    deriveRequestUpdateType = deriveRequestUpdateType;
    deriveAddressRequestValue = deriveAddressRequestValue;
    deriveBirthdateRequestValue = deriveBirthdateRequestValue;
    mmpUpdateResponseDetails = ''; 

    connectedCallback() {
        this.subscriptionEngine.apply(this);
        initialize.apply(this);
    }

    subscriptionEngine() {
        pubsub.subscribe('addToUpdateDataModel', this.addToUpdateDataModel.bind(this));
        pubsub.subscribe('addToAVDataModel', this.addToAVDataModel.bind(this));
        pubsub.subscribe('addToDisplayDataModel', this.addToDisplayDataModel.bind(this));
        pubsub.subscribe('emitEligiblePolicyMemberId', this.procurePolicyMember.bind(this));
        pubsub.subscribe('startProcuring', this.procureEligibleMemberData.bind(this));
        pubsub.subscribe('startTemplating', this.processTemplating.bind(this));
        pubsub.subscribe('checkProcurement', this.checkProcumentStatusEmit.bind(this));
        pubsub.subscribe('triggerSubmitForDependent', this.triggerDependentsSubmitEmit.bind(this));
        pubsub.subscribe('checkThenProcessFinish', this.checkThenProcessFinish.bind(this));
    }

    addToUpdateDataModel(e) {
        let keyName = e.detail.key;
        let data = e.detail.data;
        this.updatedDataModel[keyName] = data;
        populateDataModel.apply(this, [keyName]);
        populateRequestDataModel.apply(this, [keyName]);
        procureTemplateModel.apply(this, [keyName]);
    }

    addToAVDataModel(e) {
        let keyName = e.detail.key;
        let data = e.detail.data; 
        this.addressVerificationModel[keyName] = data;
    }

    addToDisplayDataModel(e) {
        this.displayDataModel = e.detail.data;
        mapOriginalMemberData.apply(this);
    }

    procurePolicyMember(e) {
        this.policyMemberId = e.detail.policyMemberId;
        this.templateName = e.detail.mode;
        this.recordCaseId = e.detail.caseId;
        this.eligibleInfo = e.detail.eligibleInfo;
        this.coverageInfo = e.detail.coverageInfo;
        this.eligibleInfoList = e.detail.eligibleInfoList; //Mega update mode
        if(US0015656SwitchLabel.toUpperCase() === 'Y') {
            this.isDateInFuture = e.detail.isDateInFuture; //Future Eff Date CR
            this.isMedicaid = e.detail.isMedicaid
        }
    }

    procureEligibleMemberData(e) {
        //procureEligibleMemberDetails.apply(this); //Mega update block
            procureMiddleName.apply(this);
    }

    processTemplating(e) {

    }

    checkProcumentStatusEmit(e) {
        checkProcumentStatus.apply(this, [e])
    }

    triggerDependentsSubmitEmit(e) {
        triggerDependentsSubmit.apply(this, [e]);
    }

    checkThenProcessFinish(e) {
        triggerProcessFinish.apply(this, [e]);
    }
}