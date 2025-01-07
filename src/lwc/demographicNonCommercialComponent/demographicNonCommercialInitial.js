import startMemberRequest from '@salesforce/apexContinuation/DemographicUpdate_D_HUM.startMemberRequest';
import retrieveEligibility from '@salesforce/apex/EligiblityRequest_D_HUM.retrieveEligibility';
import { buildFieldDataModel, buildEligibilityModel, fetchEligibleModel } from './demographicNonCommercialHelper';
import { pubsub } from 'c/pubsubComponent';
import { showToastEvent } from 'c/crmserviceHelper';

function initialize() {
    this.errorTrace.stack = 'Method: initialize, file: demographicNonCommercialInitial.js';
    pubsub.publish('toggleLoader', {
        detail: { showLoader: true }
    });
    //1. Set Initial State
    setInitialState.apply(this);

    //2. Eligibility Service
    triggerEligibilityService.apply(this);

    //3. State Consolidation
    setGlobalState.apply(this);
}

function setInitialState() {
    this.errorTrace.stack = 'Method: setInitialState, file: demographicNonCommercialInitial.js';
    pubsub.subscribe('triggerMBEService', triggerMBEService.bind(this));
    console.log("Initial State");
}

function triggerEligibilityService() {
    this.errorTrace.stack = 'Method: triggerEligibilityService, file: demographicNonCommercialInitial.js';
    this.isLoadOverlay = true;
    retrieveEligibility({ caseRecordId: this.recordCaseId, platformValue: (this.templateName === 'GBO') ? 'EM' : 'LV', templateName: this.templateName } )
        .then(result => {
            let dataObj = JSON.parse(result);
            if(dataObj.calloutErrored) {
                if(typeof dataObj.faultResponseDTO !== "undefined") {
                    this.faultEligibility = dataObj.faultResponseDTO;
                    this.isEligibilityFailed = true;
                    showToastEvent.apply(this, ['Eligibility Service Error', dataObj.serviceCalloutError]);
                }
            }
            else {
                if(typeof dataObj.eligibilityResponseDTO !== "undefined") {
                    this.responseEligibility = dataObj.eligibilityResponseDTO;
                    let responseResult = this.responseEligibility.ValidateEligibilityResponse.result;
                    let responseResultMessage = this.responseEligibility.ValidateEligibilityResponse.errormessage;
                    if(responseResult.toLowerCase() === 'ok') {
                        this.isEligibilityFailed = false;
                        let metadataObject = JSON.parse(dataObj.metadataDTO);
                        if(typeof metadataObject.off !== 'undefined') {
                            buildEligibilityModel.apply(this);
                        }
                        else {
                            this.eligibleHeirarchy = metadataObject;
                            fetchEligibleModel.apply(this);
                        }
                    }
                    else {
                        this.isEligibilityFailed = true;
                        showToastEvent.apply(this, ['Eligibility Service Error', responseResult + '-' + responseResultMessage]);
                    }
                }
            }
        })
        .catch(error => {
            pubsub.publish('toggleLoader', {
                detail: { showLoader: false }
            });
            this.isLoadOverlay = false;
            showToastEvent.apply(this, ['JavaScript Error', `${(this.errorTrace.stack) ? (this.errorTrace.stack + ' ' + error.message) : error.message }`]);
        });
    console.log("Eligibility Service");
}

function triggerMBEService() {
    this.errorTrace.stack = 'Method: triggerMBEService, file: demographicNonCommercialInitial.js';
    startMemberRequest({ caseRecordId: this.recordCaseId } )
        .then(result => {
            let dataObj = JSON.parse(result);
            if(typeof dataObj.responseMBE !== "undefined") {
                this.responseMBE = dataObj.responseMBE;
                this.isServiceFailed = false;
                buildFieldDataModel.apply(this);
                let dataMbeObj = this.responseMBE.memberResponseDTO.GetMemberResponse.Body.Member;
                let coverageKeyArray = [];
                let coverageObjArray = [];
                if(typeof dataMbeObj.SoldProductList !== "undefined" && dataMbeObj.SoldProductList !== null) {
                    if(typeof dataMbeObj.SoldProductList.SoldProduct !== "undefined" && dataMbeObj.SoldProductList.SoldProduct !== null) {
                        for (const product of dataMbeObj.SoldProductList.SoldProduct) {
                            for(let key in product) {
                                if(product.hasOwnProperty(key)) {
                                    if(product[key] !== null) {
                                        for (const detail of product[key].SoldProductDetailList.SoldProductDetail) {
                                            for(let dKey in detail) {
                                                if(detail.hasOwnProperty(dKey)) {
                                                    for (const role of detail[dKey].CoveredRoleList.CoveredRole) {
                                                        coverageKeyArray.push(role.CoverageKey);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                for (const coverage of coverageKeyArray) {
                    let covArray = coverage.split('|');
                    if(covArray.length > 0) {
                        let tempObj = {};
                        tempObj[covArray[0]] = covArray[2];
                        tempObj.source = covArray[3];
                        tempObj.subscriber = covArray[1];
                        coverageObjArray.push(tempObj);
                    }
                }

                this.memberIdToSourceIdMap = coverageObjArray;
            }
            else if(typeof dataObj.responseFault !== "undefined") {
                this.faultMBE = dataObj.responseFault;
                this.isServiceFailed = true;
                buildFieldDataModel.apply(this);
            }
            pubsub.publish('toggleLoader', {
                detail: { showLoader: false }
            });
            this.isLoadOverlay = false;
            this.isNotRendered = false;
        })
        .catch(error => {
            pubsub.publish('toggleLoader', {
                detail: { showLoader: false }
            });
            this.isLoadOverlay = false;
            showToastEvent.apply(this, ['JavaScript Error', `${(this.errorTrace.stack) ? (this.errorTrace.stack + ' ' + error.message) : error.message }`]);
        });
}

function setGlobalState() {
    this.errorTrace.stack = 'Method: setGlobalState, file: demographicNonCommercialInitial.js';
    this.checking = true;
}

export {
    initialize
}