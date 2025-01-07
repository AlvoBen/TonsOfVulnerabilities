/*
LWC Name        : pcpDetailsHum.js
Function        : LWC to update PCP details.

Modification Log:
* Developer Name                  Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Nirmal Garg                     04/12/2023                initial version US4460894
* Aishwarya Pawar                                    05/31/2023                 DF - 7703

*****************************************************************************************************************************/
import { LightningElement } from 'lwc';
import getPcpPcdData from "@salesforce/apexContinuation/PrimaryCare_LC_HUM.getPcpPcdData";
const MAX_NEW_DATE = '4000-12-31';
const MAX_OLD_DATE = '9999-12-31';
let pcpDetails = new Map();
let pcpPreviousData = [];
let pcpActiveFutureData = [];
let soldproductdetails = {};
export default class PcpDetailsHum extends LightningElement { }
const getPCPDetails = (personId, asOfdate, majorlob, majorlobfrom, effectiveFrom, effectiveTo, product, policymajorlobfrom, policymajorlob) => {
    return new Promise((resolve, reject) => {
        getPcpPcdData({ memberPlanId: personId, asOfDate: asOfdate })
            .then(result => {
                pcpDetails.clear();
                //parsed twice because the response is over-stringified
                let pcpServiceResponse = JSON.parse(result);
                if (pcpServiceResponse) {
                    if (!pcpDetails.has('platformpointerlist')) {
                        pcpDetails.set('platformpointerlist', pcpServiceResponse?.platformpointerlist ?? null)
                    } else {
                        pcpDetails['platformpointerlist'] = pcpServiceResponse?.platformpointerlist ?? null;
                    }
                    processData(pcpServiceResponse?.SoldProduct ?? null, majorlob, majorlobfrom, effectiveFrom, effectiveTo, product, policymajorlobfrom, policymajorlob)
                        .then(result => {
                            if (result) {
                                resolve(pcpDetails);
                            } else {
                                resolve(null)
                            }
                        })
                }
                else {
                    resolve(null);
                }
            })
            .catch(error => {
                console.log(error);
                reject(new Error('An error occured when retrieving the pcp data.'));
            })
    })
}

const getNetworkDetails = (data) => {
    if (data && data?.InsuranceCoverage?.Network) {
        if (!pcpDetails.has('Network')) {
            pcpDetails.set('Network', data?.InsuranceCoverage?.Network);
        } else {
            pcpDetails['Network'] = data?.InsuranceCoverage?.Network ?? null;
        }
    } else {
        pcpDetails.set('Network', null);
    }
}

const processData = async (data, majorlob, majorlobfrom, effectiveFrom, effectiveTo, product, policymajorlobfrom, policymajorlob) => {
    try {
        await getCoverageNode(data, majorlob, majorlobfrom, effectiveFrom, effectiveTo, product, policymajorlobfrom, policymajorlob);
        await getActivePCPPCD(soldproductdetails, effectiveTo);
        await getPCPPCDHistory(soldproductdetails);
        await getNetworkDetails(soldproductdetails);
        return new Promise((resolve) => resolve(true));
    } catch (error) {
        console.log(error);
    }
}

const getCoverageNode = (data, majorlob, majorlobfrom, effectiveFrom, effectiveTo, product, policymajorlobfrom, policymajorlob) => {
    soldproductdetails = {};
    try {
        if (data && data?.SoldProduct && Array.isArray(data?.SoldProduct) && data?.SoldProduct?.length > 0) {
            data.SoldProduct.forEach(k => {
                if (k && k?.InsuranceProduct && k?.InsuranceProduct?.SoldProductDetailList
                    && k?.InsuranceProduct?.SoldProductDetailList?.SoldProductDetail
                    && Array.isArray(k.InsuranceProduct.SoldProductDetailList.SoldProductDetail)
                    && k.InsuranceProduct.SoldProductDetailList.SoldProductDetail.length > 0) {
                    k.InsuranceProduct.SoldProductDetailList.SoldProductDetail.forEach(t => {
                        if (t && t?.InsuranceCoverage && checkvalidcoverage(t, majorlob, majorlobfrom, effectiveFrom, effectiveTo, policymajorlobfrom, policymajorlob) &&
                            k.InsuranceProduct.ProductLineCode === product) {
                            soldproductdetails = t;
                            return;
                        }
                    })
                }
            })
        }
    } catch (error) {
        console.log(error);
    }
}

const checkvalidcoverage = (coverage, majorlob, majorlobfrom, effectiveFrom, effectiveTo, policymajorlobfrom, policymajorlob) => {
    if (coverage && coverage?.InsuranceCoverage && majorlob
        && ((majorlob == 'PPO' &&
            (coverage?.InsuranceCoverage?.OperationalMajorLineofBusiness == majorlobfrom || coverage?.InsuranceCoverage?.OperationalMajorLineofBusiness == majorlob
                || coverage?.InsuranceCoverage?.OperationalMajorLineofBusiness == policymajorlobfrom || coverage?.InsuranceCoverage?.OperationalMajorLineofBusiness == policymajorlob))
            || (coverage.InsuranceCoverage.OperationalMajorLineofBusiness == majorlobfrom || coverage?.InsuranceCoverage?.OperationalMajorLineofBusiness == majorlob
                || coverage?.InsuranceCoverage?.OperationalMajorLineofBusiness == policymajorlobfrom || coverage?.InsuranceCoverage?.OperationalMajorLineofBusiness == policymajorlob))) {
        let insuranceCoverage = coverage.InsuranceCoverage;
        let startDate = insuranceCoverage.StartDate;
        let endDate = insuranceCoverage.EndDate;
        let effectivedate = effectiveTo === MAX_NEW_DATE ? MAX_OLD_DATE : effectiveTo;
        // used 'UTC' in the date strings to make sure all dates have the same time zone so there isn't offset between dates
        if (new Date(startDate + 'UTC').getTime() === new Date(effectiveFrom + 'UTC').getTime() &&
            new Date(endDate + 'UTC').getTime() === new Date(effectivedate + 'UTC').getTime()) {
            return true;
        } else {
            return false
        }
    }
    return false;
}

const getActivePCPPCD = (soldproductdetails, effectiveTo) => {
    pcpActiveFutureData = [];
    try {
        if (soldproductdetails && soldproductdetails?.InsuranceCoverage && soldproductdetails?.InsuranceCoverage?.PrimaryCarePhysician) {
            let primaryPhysician = soldproductdetails.InsuranceCoverage.PrimaryCarePhysician;
            if (primaryPhysician && primaryPhysician?.EffectiveDate && effectiveTo) {
                if (new Date(primaryPhysician.EndDate) >= new Date()) {
                    pcpActiveFutureData.push(primaryPhysician);
                }
            }
        }
        if (pcpActiveFutureData.length > 0) {
            pcpActiveFutureData.sort(compareDates);
            if (!pcpDetails.has('pcpActiveFuture')) {
                pcpDetails.set('pcpActiveFuture', pcpActiveFutureData)
            } else {
                pcpDetails['pcpActiveFuture'] = pcpActiveFutureData;
            }
        } else {
            if (!pcpDetails.has('pcpActiveFuture')) {
                pcpDetails.set('pcpActiveFuture', null)
            } else {
                pcpDetails['pcpActiveFuture'] = null;
            }
        }
    } catch (error) {
        console.log(error)
    }
}

const getPCPPCDHistory = (soldproductdetails) => {
    pcpPreviousData = [];
    try {
        if (soldproductdetails && soldproductdetails?.InsuranceCoverage && soldproductdetails?.InsuranceCoverage?.PrimaryCarePhysicianList
            && Array.isArray(soldproductdetails.InsuranceCoverage.PrimaryCarePhysicianList.PrimaryCarePhysician)
            && soldproductdetails.InsuranceCoverage.PrimaryCarePhysicianList.PrimaryCarePhysician.length > 0) {
            soldproductdetails.InsuranceCoverage.PrimaryCarePhysicianList.PrimaryCarePhysician.forEach(k => {
                if (new Date(k.EndDate) < new Date()) {
                    pcpPreviousData.push(k);
                }
            })
        }
        if (pcpPreviousData.length > 0) {
            pcpPreviousData.sort(compareDates);
            if (!pcpDetails.has('pcpHistory')) {
                pcpDetails.set('pcpHistory', pcpPreviousData)
            } else {
                pcpDetails['pcpHistory'] = pcpPreviousData;
            }
        } else {
            if (!pcpDetails.has('pcpHistory')) {
                pcpDetails.set('pcpHistory', null)
            } else {
                pcpDetails['pcpHistory'] = null;
            }
        }
    } catch (error) {
        console.log(error);
    }
}

const compareDates = (a, b) => {
    return new Date(b.EndDate) - new Date(a.EndDate);
}

export { getPCPDetails }