import { LightningElement, api, wire,track} from 'lwc';
import { initialize } from './demographicCommercialInitialLightning';
import { DisplayComponent, DriverComponent, EligibilityModel } from './demographicCommercialModelLightning';
import { DrivenComponent } from './demographicCommercialModelExtnLightning';
import fetchStateNames from '@salesforce/apex/DemographicUpdateController_LC_HUM.fetchStateNames';
import { pubsub } from 'c/pubsubComponent';
import { showToastEvent } from 'c/crmserviceHelper';
import { uConstants } from 'c/updatePlanDemographicConstants';
import { invokeWorkspaceAPI } from "c/workSpaceUtilityComponentHum";

export default class DemographicCommercialComponentLightning extends LightningElement {
    @track closeMsg = uConstants.Cancel_Message;
    @track openModal = false;
    @api showCancel = false;
    @track classStyle;
    @api recordCaseId;
    @api isFormInvalid = false;
    @api isNotRendered = false;
    @api encodedData;
    @api title = 'Update Plan Demographics';
    appData = {
        driver: [], driven: { dirty: [], reactive: []}
    };
    always = true;
    isMedicaid = false;
    isMedicare = false;
    isServiceFailed = false;
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
    uspsModel = {};
    stateOptions = [];
    templateName = 'GBO';
    @api isLoadOverlay = false;
    @api isFromLink = false;
    errorTrace = {
        stack: undefined
    };
    isBadAddress = false;
    isAddressVerified = false;
    currentIndex;
    currentPlatform = '';
   
    EligiblePlatProdModel = [];
    EligiblePlatformList = [];
    EligibilePolicyMemIdList = [];
    eligibleDetailsList = {
        data: undefined, generics: undefined
    };
    
    eligibleHeirarchy = {}; 

    errorCallback(error, stack) {
	pubsub.publish('toggleOverlay', {
            detail: { toggleValue: true }
        });
        showToastEvent.apply(this, ['JavaScript Error', `${(this.errorTrace.stack) ? (this.errorTrace.stack + ' ' + error.message) : error.message }`]);
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
        catch(error) {
            showToastEvent.apply(this, ['JavaScript Error', `${(this.errorTrace.stack) ? (this.errorTrace.stack + ' ' + error.message) : error.message }`]);
        }
        
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
            pubsub.subscribe('isAddressBad', this.toggleBadAddress.bind(this));
            pubsub.subscribe('emitCurrentIndex', this.toggleCurrentIndex.bind(this));
            this.errorTrace.stack = undefined;
        }
        catch(error) {
            showToastEvent.apply(this, ['JavaScript Error', `${(this.errorTrace.stack) ? (this.errorTrace.stack + ' ' + error.message) : error.message }`]);
        }
    }

    @wire(fetchStateNames)
    wiredStateOptions({error, data}) {
        try {
            if(data) {
                let tempArray = [];
                for(let key in data) {
                    if(typeof key !== "undefined") {
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
            else if(error) {
                this.stateOptions = [];
                showToastEvent.apply(this, ['State Options Retrieval Error', error.body.message]);
                this.errorTrace.stack = undefined;
            }
        }
        catch(e) {
            showToastEvent.apply(this, ['JavaScript Error', `${(this.errorTrace.stack) ? (this.errorTrace.stack + ' ' + error.message) : error.message }`]);
        }
    }

    consumeFromDriver(event) {
        try {
            this.appData.driver = event.detail.data;
            this.errorTrace.stack = undefined;
        }
        catch(error) {
            showToastEvent.apply(this, ['JavaScript Error', `${(this.errorTrace.stack) ? (this.errorTrace.stack + ' ' + error.message) : error.message }`]);
        }
    }

    consumeFromDriven(event) {
        try {
            this.appData.driven.reactive = event.detail.reactiveData;
            this.appData.driven.dirty = event.detail.dirtyData;
            this.errorTrace.stack = undefined;
        }
        catch(error) {
            showToastEvent.apply(this, ['JavaScript Error', `${(this.errorTrace.stack) ? (this.errorTrace.stack + ' ' + error.message) : error.message }`]);
        }
    }
    
    toggleCurrentIndex(event) {
        try{
            this.currentIndex = event.detail.data;
        }
        catch(error){
            showToastEvent.apply(this, ['JavaScript Error', `${(this.errorTrace.stack) ? (this.errorTrace.stack + ' ' + error.message) : error.message }`]);
        }
    }

    toggleBadAddress(event) {
        try{
            this.isBadAddress = event.detail.data;
            this.isAddressVerified = event.detail.isAddressVerified;
            if(+this.currentIndex === 1) {
                pubsub.publish('toggleNextBtn', {
                    detail: {data: this.isBadAddress }
                });
            }
        }
        catch(error){
            showToastEvent.apply(this, ['JavaScript Error', `${(this.errorTrace.stack) ? (this.errorTrace.stack + ' ' + error.message) : error.message }`]);
        }
    }

    processActionPost() {
        try {
            this.isNotRendered = !this.isNotRendered;
            pubsub.publish("processMainNext");
            if(!this.isNotRendered) {
                this.title = 'Update Plan Demographics';
            }
            else {
                this.title = arguments[0];
            }
            this.errorTrace.stack = undefined;
        }
        catch(error) {
            showToastEvent.apply(this, ['JavaScript Error', `${(this.errorTrace.stack) ? (this.errorTrace.stack + ' ' + error.message) : error.message }`]);
        }
    }

    handleNextClick() {
        try {
            for (const item of this.appData.driver) {
                if(item.status === 'In Progress') {
                    switch(item.eventName) {
                        case "launchsummary":
                            this.processActionPost.apply(this, [item.title]);
                            pubsub.publish("launchsummary", {
                                detail: { 
                                    
                                    data: this.appData.driven.reactive.filter(field => field.isSummary === true), 
                                    title: item.title, order: item.order,
                                    buttonName: 'Submit',
                                    msgCriteria: { showBtnMessage: true, showReadMsg: true },
                                    isEnd: true,
                                    bottom: false,
                                    mode: this.templateName
                                }

                            });
                            break;
                        case "launchavf":
                            this.processActionPost.apply(this, [item.title]);
                            pubsub.publish("launchavf", {
                                detail: { data: this.appData.driven.dirty, options: this.stateOptions, recordId: this.recordCaseId, title: item.title }
                            });
                            break;
                        case "launchmedicaid":
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
        catch(error) {
            showToastEvent.apply(this, ['JavaScript Error', `${(this.errorTrace.stack) ? (this.errorTrace.stack + ' ' + error.message) : error.message }`]);
        }
    }

    toggleNextButton(event) {
        try {
	        if(+this.currentIndex === 1) {
                this.isFormInvalid = (!event.detail.data) ? this.isBadAddress : event.detail.data;
            }
            else {
                this.isFormInvalid = event.detail.data;
            }
            this.errorTrace.stack = undefined;
        }
        catch(error) {
            showToastEvent.apply(this, ['JavaScript Error', `${(this.errorTrace.stack) ? (this.errorTrace.stack + ' ' + error.message) : error.message }`]);
        }
    }

    toggleOverlay(event) {
        try {
            this.isLoadOverlay = event.detail.toggleValue;
            this.errorTrace.stack = undefined;
        }
        catch(error) {
            showToastEvent.apply(this, ['JavaScript Error', `${(this.errorTrace.stack) ? (this.errorTrace.stack + ' ' + error.message) : error.message }`]);
        }
    }

    toggleRouting(e) {
        try {
            if(this.displayMessageModel.routing !== '') {
                this.displayRouting = e.detail.display;
            }
            else {
                this.displayRouting = false;
            }
            this.errorTrace.stack = undefined;
        }
        catch(error) {
            showToastEvent.apply(this, ['JavaScript Error', `${(this.errorTrace.stack) ? (this.errorTrace.stack + ' ' + error.message) : error.message }`]);
        }
    }

    toggleHeirarchyMessage(e) {
        try {
            let keyName = e.detail.keyName;
            let display = e.detail.display;
            let message = '';
            message = this.displayMessageModel[keyName.toLowerCase()];
            message = (message) ? message : '';
            display = (message === '') ? false : display;
            pubsub.publish('toggleMessages', {
                detail: { message: message, display: display }
            });
            this.errorTrace.stack = undefined;
        }
        catch(error) {
            showToastEvent.apply(this, ['JavaScript Error', `${(this.errorTrace.stack) ? (this.errorTrace.stack + ' ' + error.message) : error.message }`]);
        }
    }

    processUpdateModel(e) {
        try {
            let visibilityIndex = e.detail.visibility;
            let data = [];
            let avfData = (e.detail.avfData) ? e.detail.avfData : undefined;
            let avData = {
                residential: { status: false, data: {} },
                mailing: { status: false, data: {} }
            };

            if(avfData) {
                data = avfData;
            }
            else {
                for (const field of this.appData.driven.dirty) {
                    if(field.visibility.indexOf(+visibilityIndex) > -1) {
                        data.push(field);
                    }
                }
            }

            for (const button of this.drivenButtonModel) {
                if(button.order === 1) {
                    avData.residential.data = button.responseAVObj;
                    avData.residential.status = true;
                    avData.residential.standardStatus = button.standardStatus;
                }
                else if(button.order === 2) {
                    avData.mailing.data = button.responseAVObj;
                    avData.mailing.status = true;
                    avData.mailing.standardStatus = button.standardStatus;
                }
            }

            pubsub.publish('addToAVDataModel', {
                detail: {
                    key: 'cod',
                    data: avData
                }
            });

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
                    eligibleInfoList: this.eligibleDetailsList 
                }
            });
            
            pubsub.publish('addToUpdateDataModel', {
                detail: {
                    key: (+visibilityIndex === 1) ? 'cod' : ((+visibilityIndex === 2) ? 'crd' : ((+visibilityIndex === 3) ? 'mau' : 'mdu')),
                    data: data
                }
            });
            this.errorTrace.stack = undefined;
        }
        catch(error) {
            showToastEvent.apply(this, ['JavaScript Error', `${(this.errorTrace.stack) ? (this.errorTrace.stack + ' ' + error.message) : error.message }`]);
        }
    }
	
	 disconnectedCallback() {
        pubsub.unsubcribe();
    }
}