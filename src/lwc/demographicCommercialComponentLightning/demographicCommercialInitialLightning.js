import startMemberRequest from '@salesforce/apexContinuation/DemographicUpdate_LS_HUM.startMemberRequest';
import retrieveEligibility from '@salesforce/apex/EligiblityRequest_LD_HUM.retrieveEligibility';
import { buildFieldDataModel, buildEligibilityModel, fetchEligibleModel } from './demographicCommercialHelperLightning';
import { pubsub } from 'c/pubsubComponent';
import { showToastEvent } from 'c/crmserviceHelper';

function initialize() {
    debugger;
    this.errorTrace.stack = 'Method: initialize, file: demographicCommercialInitialLightning.js';
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
    this.errorTrace.stack = 'Method: setInitialState, file: demographicCommercialInitialLightning.js';
    pubsub.subscribe('triggerMBEService', triggerMBEService.bind(this));
}

function triggerEligibilityService() {
    if (this.recordCaseId === 'undefined' || this.recordCaseId == null) {
        this.recordCaseId = this.encodedData;
    }
    this.errorTrace.stack = 'Method: triggerEligibilityService, file: demographicCommercialInitialLightning.js';
    this.isLoadOverlay = true;

    retrieveEligibility({ caseRecordId: this.recordCaseId, platformValue: (this.templateName === 'GBO') ? 'EM' : 'LV', templateName: this.templateName } )
        .then(result => {
            let dataObj = JSON.parse(result);
            if(dataObj.calloutErrored) {
                if(typeof dataObj.faultResponseDTO !== "undefined") {
                    this.faultEligibility = dataObj.faultResponseDTO;
                    this.isEligibilityFailed = true;
                    this.isLoadOverlay = false;
                    showToastEvent.apply(this, ['Eligibility Service Error', dataObj.serviceCalloutError]);
                }
            }
            else {
                if(typeof dataObj.eligibilityResponseDTO !== "undefined") {
                    this.responseEligibility = dataObj.eligibilityResponseDTO;
                    let responseResult = this.responseEligibility.ValidateEligibilityResponse.result;
                    let responseResultMessage = this.responseEligibility.ValidateEligibilityResponse.errormessage;
                    if(responseResult.toLowerCase() === 'ok') {
                        let metadataObject = JSON.parse(dataObj.metadataDTO);
                        
                        this.isEligibilityFailed = false;
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
                        this.isLoadOverlay = false;
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
}

function triggerMBEService() {
    this.errorTrace.stack = 'Method: triggerMBEService, file: demographicCommercialInitialLightning.js';
    pubsub.publish('toggleLoader', {
        detail: { showLoader: true }
    });
    startMemberRequest({ caseRecordId: this.recordCaseId } )
        .then(result => {
            console.log('startMemberRequest result',result);
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
    this.errorTrace.stack = 'Method: setGlobalState, file: demographicCommercialInitialLightning.js';
    this.checking = true;
}

export {
    initialize
}