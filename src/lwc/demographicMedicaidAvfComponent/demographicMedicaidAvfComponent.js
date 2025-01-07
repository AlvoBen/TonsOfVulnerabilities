import { LightningElement, api } from 'lwc';
import { loadStyle } from 'lightning/platformResourceLoader';
import fetchStateNames from '@salesforce/apex/DemographicUpdateController_LTNG_C_HUM.fetchStateNames';
import { flowPageModel, flowRenderObject, flowButtonModel, flowButtonRenderObject, modalUSPSDataModel } from './demographicMedicaidAvfInitial';
import { deriveReactiveModel, deriveReactiveBtnModel, processFieldChange, processButtonClick, processModalAction } from './demographicMedicaidAvfHelper';
import avfJobAid from '@salesforce/resourceUrl/avfJobAid';
import { pubsub } from 'c/pubsubComponent';

export default class DemographicAVFPage extends LightningElement {
    @api avfTitle;
    @api instanceId;
    @api inputArray;
    @api inputMedicaid;
    @api stateOptions;
    @api flowDataModel = {
        reactive: [], dirty: [], original: []
    };
    @api flowButtonDataModel = {
        reactive: [], dirty: [], original: []
    };
    @api summaryDataModel = {
        landingModel: [], avfModel: [], showSummary: false
    };
    @api flowDataOutput;
    @api flowPageModel = flowPageModel();
    @api flowButtonModel = flowButtonModel();
    @api flowBreadCrumbModel;
    @api isFormInvalid = false;
    @api currentPageNumber = 1;
    @api journeyParams = {
        name: "", mode: []
    };
    @api isLoading = false;
    @api isRendered = false;
    @api isAddressVerified = {
        residential: { status: false, data: {} }, 
        mailing: { status: false, data: {} }, 
        temporary: { status: false, data: {} }
    };
    @api isLoadOverlay = false;
    @api isShowModal = false;
    @api popupData = modalUSPSDataModel();
    @api modalTitle = "";
    @api isBadRequest = false;
    @api landingModel = [];
    @api display = false;
    @api sequence = false;
    badPageNumber;
    errorTrace = {
        stack: undefined
    };

    connectedCallback() {
        try {
            console.log('Demographic Medicaid AVF Component');
            loadStyle(this, avfJobAid);
            this.flowPageModel.forEach(page => {
                this.flowDataModel.original.push(page);
                this.flowDataModel.dirty.push(page);
            });
            this.flowButtonModel.forEach(button => {
                this.flowButtonDataModel.original.push(button);
                this.flowButtonDataModel.dirty.push(button);
            });
            deriveReactiveModel.apply(this, [flowRenderObject]);
            deriveReactiveBtnModel.apply(this, [flowButtonRenderObject]);
            pubsub.subscribe('launchmedicaid', this.toggleAVF.bind(this));
        }
        catch(error) {
            throw new Error(`${(this.errorTrace.stack) ? (this.errorTrace.stack + ' ' + error.message) : error.message }`);
        }
    }

    disconnectedCallback() {
        try {
            pubsub.subscribe.remove();
        }
        catch(error) {
            throw new Error(`${(this.errorTrace.stack) ? (this.errorTrace.stack + ' ' + error.message) : error.message }`);
        }
    }

    renderedCallback() {
        try {
            if(!this.isRendered) {
                deriveReactiveModel.apply(this, [flowRenderObject]);
                this.isRendered = true;
                deriveReactiveBtnModel.apply(this, [flowButtonRenderObject]);
            }
        }
        catch(error) {
            throw new Error(`${(this.errorTrace.stack) ? (this.errorTrace.stack + ' ' + error.message) : error.message }`);
        }
    }

    handleFieldChange(e) {
        try {
            this.isRendered = false;
            processFieldChange.apply(this, [e, flowRenderObject]);
        }
        catch(error) {
            throw new Error(`${(this.errorTrace.stack) ? (this.errorTrace.stack + ' ' + error.message) : error.message }`);
        }
    }

    handleButtonClick(e) {
        try {
            this.isRendered = false;
            processButtonClick.apply(this, [e, flowRenderObject]);
        }
        catch(error) {
            throw new Error(`${(this.errorTrace.stack) ? (this.errorTrace.stack + ' ' + error.message) : error.message }`);
        }
    }

    handleModalEvent(e) {
        try {
            let eventName = e.detail;
            switch(eventName.toLowerCase()) {
                case "uspsaccept":
                    processModalAction.apply(this, [flowRenderObject, true, "accept"]);
                    this.isShowModal = false;
                    this.isLoadOverlay = false;
                    break;
                case "uspsreject":
                    processModalAction.apply(this, [flowRenderObject, true, "reject"]);
                    this.isShowModal = false;
                    this.isLoadOverlay = false;
                    break;
                default:
                    break;
            }
        }
        catch(error) {
            throw new Error(`${(this.errorTrace.stack) ? (this.errorTrace.stack + ' ' + error.message) : error.message }`);
        }
    }

    toggleAVF(e) {
        try {
            this.isRendered = false;
            this.display = true;
            this.summaryDataModel.landingModel = e.detail.data;
            this.stateOptions = e.detail.options;
            this.avfTitle = e.detail.title;
            this.instanceId = e.detail.recordId;
        }
        catch(error) {
            throw new Error(`${(this.errorTrace.stack) ? (this.errorTrace.stack + ' ' + error.message) : error.message }`);
        }
    }
}