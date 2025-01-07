/*
LWC Name        : benefitsServiceHelperHum.js
Function        : JS file for consuming PBE,GBE and MBE Service.

Modification Log:
* Developer Name                  Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Nirmal Garg                    06/01/2023                   Original Version
* Apurva Urkude                  06/05/2023                   User Story-4654062
****************************************************************************************************************************/
import { LightningElement } from 'lwc';
import getPBEResponse from '@salesforce/apexContinuation/Benefits_LC_HUM.invokePBEService';
import getGBEResponse from '@salesforce/apexContinuation/Benefits_LC_HUM.invokeGBEService';
import getMBEResponse from '@salesforce/apexContinuation/Benefits_LC_HUM.invokeMBEService';
import getBillingProfiles from '@salesforce/apexContinuation/Benefits_LC_HUM.getBillingProfiles';
import getBenefitSearch from '@salesforce/apexContinuation/Benefits_LC_HUM.getBenefitSearch';
const invokePBEService = (calledFor, inputParams) => {
    return new Promise((resolve, reject) => {
        if (calledFor) {
            switch (calledFor?.toLowerCase()) {
                case "initialsearch":
                    getPBEResponse({ memberPlanId: inputParams?.memberplanid ?? '', asOfDate: inputParams?.asofdate ?? '' }).then(result => {
                        if(result){
                            if (result === '111111' || result === '000305' || result === '000306') {
                                resolve({
                                    serviceError: true,
                                    errorCode: result
                                })
                            } else {
                                resolve(JSON.parse(result));
                            }
                        }
                        else{
                            resolve({
                                serviceError: true,
                                errorCode: 'service error'
                            })
                        }
                        
                    }).catch(error => {
                        reject(error);
                    })
                    break;
                case "benefitsearch":
                    getBenefitSearch({
                        sServiceCategory: inputParams?.lstServiceCategoryCode ?? [],
                        sTypeOfService: inputParams?.selectedTypeOfService ?? '',
                        sPlaceOfService: inputParams?.selectedPlaceOfService ?? '',
                        sPar: inputParams?.selectedPARCode ?? '',
                        platformCode: inputParams?.platformCode ?? '',
                        productKey: inputParams?.productId ?? '',
                        refdate: inputParams?.refdate ?? '',
                        isCAS: inputParams?.isCAS ?? false,
                        ADACode: inputParams?.adaCode ?? ''
                    }).then(result => {
                        resolve(result);
                    }).catch(error => {
                        reject(error);
                    })
                    break;
                default:
                    reject(new Error('no pbe service call'))

            }
        } else {
            reject(new Error('mandatory paramateres missing.'))
        }
    });
}

const invokeGBEService = (memberplanid, asofdate = '') => {
    return new Promise((resolve, reject) => {
        getGBEResponse({ memberPlanId: memberplanid, asOfDate: asofdate }).then(result => {
            let GBEResponse = JSON.parse(result);
            if (GBEResponse) {
                resolve({
                    Certificate: GBEResponse?.sCertificate ?? '',
                    MHVenderCode: GBEResponse?.sMentalHealthVendorCode ?? '',
                    MaxDependentAge: GBEResponse?.sMaximumDependentAge ?? '',
                    MaxStudentAge: GBEResponse?.sMaximumStudentAge ?? '',
                    Market: GBEResponse?.sMarketName ?? '',
                    SellingLedgerNumber: GBEResponse?.sSellingLedger ?? '',
                    SellingLedgerDescription: GBEResponse?.sLedgerDescription ?? '',
                    LastRenewalDate: GBEResponse?.sLastRenewalDate ?? '',
                    GBENetworkDesc: GBEResponse?.sBenefitNetwork ?? '',
                    OpenEnrolEnddate: GBEResponse?.sOpenEnrolEnddate ?? '',
                    OpenEnrolBegindate: GBEResponse?.sOpenEnrolBegindate ?? '',
                    GroupEnrollmentCount: GBEResponse?.sGroupEnrollmentCount ?? '',
                    HourlyReq: GBEResponse?.sHourlyReq ?? ''
                })
            } else {
                resolve(null);
            }
        }).catch(error => {
            reject(error);
        })
    });
}

const invokeMBEService = (memberplanid, asofdate = '') => {
    return new Promise((resolve, reject) => {
        getMBEResponse({ memberPlanId: memberplanid, asOfDate: asofdate }).then(result => {
            if (result && result === 'Code_83.1.1') {
                resolve({
                    serviceError: true,
                    errorCode: result
                })
            } else {
                let MBEResponse = JSON.parse(result);
                if (MBEResponse) {
                    resolve({
                        IndicatorList: MBEResponse?.IndicatorList ?? null,
                        Network: MBEResponse?.Network ?? null,
                        CoverageType: MBEResponse?.sCoverageType ?? null,
                        PolicyIndicatorList: MBEResponse?.PolicyIndicatorList ?? null,
                        WaitingPeriodList: MBEResponse?.WaitingPeriodList ?? null,
                        OriginalEffectiveDate: MBEResponse?.sOriginalEffectiveDate ?? '',
                        EnrollmentInfo: MBEResponse?.EnrollmentInfo ?? null,
                        paidThruDate: MBEResponse?.paidThruDate ?? ''
                    })
                } else {
                    resolve(null);
                }
            }
        }).catch(error => {
            reject(error);
        })
    });
}

const getPaidThruDate = (networkId, sourceCoverageId, platformValue, exchangeType, productType, product) => {
    return new Promise((resolve, reject) => {
        getBillingProfiles({
            sNetworkID: networkId, sSearchID: sourceCoverageId, sPlatform: platformValue,
            sExchangeType: exchangeType,
            sProductType: productType, sProduct: product
        }).then(result => {
            resolve(JSON.parse(result));
        }).catch(err => {
            reject(err);
        });
    });
}

export { invokePBEService, invokeMBEService, invokeGBEService, getPaidThruDate }