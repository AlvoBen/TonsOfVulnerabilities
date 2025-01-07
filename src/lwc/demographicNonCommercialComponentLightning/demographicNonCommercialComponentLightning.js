import { LightningElement, api, wire, track } from 'lwc';
import { initialize } from './demographicNonCommercialInitialLightning';
import { DisplayComponent, DriverComponent, DrivenComponent, EligibilityModel } from './demographicNonCommercialModelLightning';
import fetchStateNames from '@salesforce/apex/DemographicUpdateController_LC_HUM.fetchStateNames';
import { pubsub } from 'c/pubsubComponent';
import { showToastEvent } from 'c/crmserviceHelper';
import { invokeWorkspaceAPI } from "c/workSpaceUtilityComponentHum";
import { uConstants } from 'c/updatePlanDemographicConstants';

export default class DemographicNonCommercialComponentLightning extends LightningElement {
    @track closeMsg = uConstants.Cancel_Message;
    @track openModal = false;
    @api showCancel = false;
    @api showClose = false;
    @api recordCaseId;
    @api encodedData;
    @api recordPersonId;
    @api isFormInvalid = false;
    @api isNotRendered = false;
    @api title = uConstants.Update_Plan_Demographics;
    @api pageName;
    appData = {
        driver: [], driven: { dirty: [], reactive: [] }
    };
    always = true;
    isMedicaid = false;
    isMedicare = false;
    isServiceFailed = false;
    isEligibilityFailed = false;
    displayComponentClass = new DisplayComponent();
    driverComponentClass = new DriverComponent();
    drivenComponentClass = new DrivenComponent();
    eligibilityModelClass = new EligibilityModel();
    responseMBE = {};
    faultMBE = {};
    responseEligibility = {};
    faultEligibility = {};
    displayFieldModel = [];
    driverFieldModel = [];
    drivenFieldModel = [];
    drivenButtonModel = [];
    EligibilityDataModel = [];
    eligiblePolicyMemberId = '';
    noPoliciesToProceed = false;
    eligibileEditModel = {};
    displayMessageModel = {
        cod: [], crd: [], routing: []
    };
    contactMessage = '';
    criticalMessage = '';
    routingMessage = '';
    @api displayRouting = false;
    eligibleDetails = {
        data: undefined, generics: undefined
    };
    @api displayDependent = {
        msg: undefined, show: false
    };
    memberIdToSourceIdMap = [];
    ruleName = '';
    uspsModel = {};
    stateOptions = [];
    templateName = uConstants.RSO;
    @api isLoadOverlay = false;
    currentPlatform = '';
    /*below should be deleted when Elgibility Service is ready */
    @api btnName = uConstants.Toggle_to_Medicaid;
    isModeMedicaid = false;
    errorTrace = {
        stack: ''
    };
    //Mega Block Starts
    EligiblePlatProdModel = [];
    EligiblePlatformList = [];
    EligibilePolicyMemIdList = [];
    eligibleDetailsList = {
        data: undefined, generics: undefined
    };
    //Mega Block Ends
    isDateInFuture = false;
    eligibleHeirarchy = {};
    //US1441116 
    @api hasPermission = false;
    @api isFromLink = false;
    @track classStyle;

    errorCallback(error, stack) {
        pubsub.publish('toggleOverlay', {
            detail: { toggleValue: true }
        });
        showToastEvent.apply(this, ['JavaScript Error', `${(this.errorTrace.stack) ? (this.errorTrace.stack + ' ' + error.message) : error.message}`]);
    }

    connectedCallback() {
        try {
            this.isFromLink ? this.classStyle = '' : this.classStyle = 'cmp-size';
            this.isNotRendered = true;
            this.isLoadOverlay = true;
            this.subscriptionEngine();
            initialize.call(this);
            this.isFormInvalid = true;
            this.errorTrace.stack = undefined;
        }
        catch (error) {
            showToastEvent.apply(this, ['JavaScript Error', `${(this.errorTrace.stack) ? (this.errorTrace.stack + ' ' + error.message) : error.message}`]);
        }
    }

    subscriptionEngine() {
        try {
            pubsub.subscribe('emissionFromDriver', this.consumeFromDriver.bind(this));
            pubsub.subscribe('emissionFromDriven', this.consumeFromDriven.bind(this));
            pubsub.subscribe('emissionDuringFlow', this.processActionPost.bind(this));
            pubsub.subscribe('toggleNextBtn', this.toggleNextButton.bind(this));
            pubsub.subscribe('toggleOverlay', this.toggleOverlay.bind(this));
            pubsub.subscribe('toggleRouting', this.toggleRouting.bind(this));
            pubsub.subscribe('toggleHeirarchyMessage', this.toggleHeirarchyMessage.bind(this));
            pubsub.subscribe('initiateUpdateModel', this.processUpdateModel.bind(this));
            pubsub.subscribe('futureDateVerification', this.applyFutureDate.bind(this));
            this.errorTrace.stack = undefined;
        }
        catch (error) {
            showToastEvent.apply(this, ['JavaScript Error', `${(this.errorTrace.stack) ? (this.errorTrace.stack + ' ' + error.message) : error.message}`]);
        }
    }

    @wire(fetchStateNames)
    wiredStateOptions({ error, data }) {
        try {
            if (data) {
                let tempArray = [];
                for (let key in data) {
                    if (typeof key !== "undefined") {
                        tempArray.push(
                            {
                                label: data[key],
                                value: key
                            }
                        );
                    }
                }
                this.stateOptions = tempArray;
                this.errorTrace.stack = undefined;
            }
            else if (error) {
                this.stateOptions = [];
                showToastEvent.apply(this, ['State Options Retrieval Error', error.body.message]);
                this.errorTrace.stack = undefined;
            }
        }
        catch (e) {
            showToastEvent.apply(this, ['JavaScript Error', `${(this.errorTrace.stack) ? (this.errorTrace.stack + ' ' + error.message) : error.message}`]);
        }
    }

    consumeFromDriver(event) {
        try {
            this.appData.driver = event.detail.data;
            this.errorTrace.stack = undefined;
        }
        catch (error) {
            showToastEvent.apply(this, ['JavaScript Error', `${(this.errorTrace.stack) ? (this.errorTrace.stack + ' ' + error.message) : error.message}`]);
        }
    }

    consumeFromDriven(event) {
        try {
            this.appData.driven.reactive = event.detail.reactiveData;
            this.appData.driven.dirty = event.detail.dirtyData;
            this.errorTrace.stack = undefined;
        }
        catch (error) {
            showToastEvent.apply(this, ['JavaScript Error', `${(this.errorTrace.stack) ? (this.errorTrace.stack + ' ' + error.message) : error.message}`]);
        }
    }

    processActionPost() {
        try {
            this.isNotRendered = !this.isNotRendered;
            pubsub.publish("processMainNext");
            if (!this.isNotRendered) {
                this.title = uConstants.Update_Plan_Demographics;
            }
            else {
                this.title = arguments[0];
            }
            this.errorTrace.stack = undefined;
        }
        catch (error) {
            showToastEvent.apply(this, ['JavaScript Error', `${(this.errorTrace.stack) ? (this.errorTrace.stack + ' ' + error.message) : error.message}`]);
        }
    }

    applyFutureDate(e) {
        try {
            this.isDateInFuture = e.detail.isDateInFuture;
            this.errorTrace.stack = undefined;
        }
        catch (error) {
            showToastEvent.apply(this, ['JavaScript Error', `${(this.errorTrace.stack) ? (this.errorTrace.stack + ' ' + error.message) : error.message}`]);
        }
    }

    handleNextClick() {
        let isNext = false;
        let nextName = '';
        try {
            for (const option of this.appData.driver) {
                if (option.status === 'Not Started') {
                    nextName = (option.avf) ? ((option.value === 'MAU') ? 'AVF Job Aid' : 'Medicare Supplement or Medicaid Address Update') :
                        ((option.value === 'COD') ? 'Contact Demographic Update' : 'Critical Demographic Update');
                    isNext = true;
                    break;
                }
            }
            for (const item of this.appData.driver) {
                if (item.status === 'In Progress') {
                    switch (item.eventName) {
                        case "launchsummary":
                            this.processActionPost.apply(this, [item.title]);
                            pubsub.publish("launchsummary", {
                                detail: {
                                    data: this.appData.driven.reactive.filter(field => field.isSummary === true),
                                    title: item.title, order: item.order,
                                    buttonName: (isNext) ? 'Next' : 'Submit',
                                    msgCriteria: { showBtnMessage: !isNext, showReadMsg: true },
                                    isEnd: !isNext,
                                    bottom: true,
                                    nextName: nextName,
                                    mode: this.templateName
                                }
                            });
                            break;
                        case "launchavf":
                            this.dispatchEvent(new CustomEvent('toggleDirty', {
                                detail: { data: true },
                                bubbles: true,
                                composed: true,
                            }));
                            this.processActionPost.apply(this, [item.title]);
                            pubsub.publish("launchavf", {
                                detail: { data: this.appData.driven.dirty, options: this.stateOptions, recordId: this.recordCaseId, title: item.title, eligibleData: this.eligibleDetails, ruleName: this.ruleName }
                            });
                            break;
                        case "launchmedicaid":
                            this.dispatchEvent(new CustomEvent('toggleDirty', {
                                detail: { data: true },
                                bubbles: true,
                                composed: true,
                            }));
                            this.processActionPost.apply(this, [item.title]);
                            pubsub.publish("launchmedicaid", {
                                detail: { data: this.appData.driven.dirty, options: this.stateOptions, recordId: this.recordCaseId, title: item.title }
                            });
                            break;
                        default:
                            break;
                    }
                    break;
                }
            }
            this.errorTrace.stack = undefined;
        }
        catch (error) {
            showToastEvent.apply(this, ['JavaScript Error', `${(this.errorTrace.stack) ? (this.errorTrace.stack + ' ' + error.message) : error.message}`]);
        }
    }

    toggleNextButton(event) {
        try {
            this.isFormInvalid = event.detail.data;
            this.errorTrace.stack = undefined;
        }
        catch (error) {
            showToastEvent.apply(this, ['JavaScript Error', `${(this.errorTrace.stack) ? (this.errorTrace.stack + ' ' + error.message) : error.message}`]);
        }
    }

    toggleOverlay(event) {
        try {
            this.isLoadOverlay = event.detail.toggleValue;
            this.errorTrace.stack = undefined;
        }
        catch (error) {
            showToastEvent.apply(this, ['JavaScript Error', `${(this.errorTrace.stack) ? (this.errorTrace.stack + ' ' + error.message) : error.message}`]);
        }
    }

    handleDummyClick() {
        try {
            pubsub.publish('toggleLoader', {
                detail: { showLoader: true }
            });
            pubsub.publish('toggleFields', {
                detail: { index: 0 }
            });
            this.isModeMedicaid = !this.isModeMedicaid;
            initialize.call(this);
            this.btnName = (this.isModeMedicaid) ? uConstants.Toggle_to_Medicare : uConstants.Toggle_to_Medicaid;
            this.errorTrace.stack = undefined;
        }
        catch (error) {
            showToastEvent.apply(this, ['JavaScript Error', `${(this.errorTrace.stack) ? (this.errorTrace.stack + ' ' + error.message) : error.message}`]);
        }
    }

    toggleRouting(e) {
        if (this.displayMessageModel.routing !== '') {
            this.displayRouting = e.detail.display;
        }
        else {
            this.displayRouting = false;
        }
    }

    toggleHeirarchyMessage(e) {
        let keyName = e.detail.keyName;
        let display = e.detail.display;
        let message = '';
        message = this.displayMessageModel[keyName.toLowerCase()];
        message = (message) ? message : '';
        display = (message === '') ? false : display;
        pubsub.publish('toggleMessages', {
            detail: { message: message, display: display }
        });
    }

    processUpdateModel(e) {
        let visibilityIndex = e.detail.visibility;
        let data = [];
        let avData = (e.detail.addressData) ? e.detail.addressData : undefined;
        let avfData = (e.detail.avfData) ? e.detail.avfData : undefined;

        if (avfData) {
            data = avfData;

        }
        else {
            for (const field of this.appData.driven.dirty) {
                if (field.visibility.indexOf(+visibilityIndex) > -1) {
                    data.push(field);
                }
            }
        }
        if (avData) {
            pubsub.publish('addToAVDataModel', {
                detail: {
                    key: (+visibilityIndex === 1) ? 'cod' : ((+visibilityIndex === 2) ? 'crd' : ((+visibilityIndex === 3) ? 'mau' : 'mdu')),
                    data: avData
                }
            });
        }
        pubsub.publish('addToDisplayDataModel', {
            detail: {
                data: this.displayFieldModel
            }
        });
        pubsub.publish('emitEligiblePolicyMemberId', {
            detail: {
                policyMemberId: this.eligiblePolicyMemberId,
                mode: this.templateName,
                caseId: this.recordCaseId,
                eligibleInfo: this.eligibleDetails,
                coverageInfo: this.memberIdToSourceIdMap,
                eligibleInfoList: this.eligibleDetailsList, //mega update block
                isDateInFuture: this.isDateInFuture,
                isMedicaid: this.isMedicaid
            }
        });
        pubsub.publish('addToUpdateDataModel', {
            detail: {
                key: (+visibilityIndex === 1) ? 'cod' : ((+visibilityIndex === 2) ? 'crd' : ((+visibilityIndex === 3) ? 'mau' : 'mdu')),
                data: data
            }
        });
    }

    onCancel() {
        this.openModal = true;
    }
    onCloseMsg() {
        this.openModal = false;
    }
    cancelAll() {
        try {
            invokeWorkspaceAPI('isConsoleNavigation').then(isConsole => {
                if (isConsole) {
                    invokeWorkspaceAPI('getFocusedTabInfo').then(focusedTab => {
                        invokeWorkspaceAPI('disableTabClose', {
                            tabId: focusedTab.tabId,
                            disabled: false
                        });
                        invokeWorkspaceAPI('closeTab', {
                            tabId: focusedTab.tabId
                        });

                    });
                }
            });
            
        }
        catch (error) {
            console.log('Error==', error);
        }
    }
	
	disconnectedCallback() {
        pubsub.unsubcribe();
    }

}