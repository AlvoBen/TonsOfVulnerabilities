/*******************************************************************************************************************************
LWC JS Name : claimSummaryHumHelper.js
Function    : This JS serves as helper to claimSummaryHum.js. 
Modification Log: 
  Developer Name           Code Review                      Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Disha Dhole                                              01/09/2023                US-4942331: T1PRJ0865978 - MF 4796385 - C03, Contact Servicing- Add Filter for Pre-D
*********************************************************************************************************************************/
export function getFilterdData(filteredClaims, appliedFilter) {
    let finalFilteredClaims = [];
    let filterArray = [];

    if (appliedFilter.sStatusDesc && appliedFilter.sStatusDesc.length > 0) {
        filterArray.push('sStatusDesc');
    }

    if (appliedFilter.sPlatformCd && appliedFilter.sPlatformCd.length > 0) {
        filterArray.push('sPlatformCd');
    }

    if (appliedFilter.sClaimType && appliedFilter.sClaimType.length > 0) {
        filterArray.push('sClaimType');
    }

    if (appliedFilter.sPreDeterminationIndicator && appliedFilter.sPreDeterminationIndicator.length > 0) {
        filterArray.push('sPreDeterminationIndicator');
    }

    filteredClaims.forEach((item) => {
        if (filterArray && filterArray.length == 1) {
            if (appliedFilter[filterArray[0]].includes(item[filterArray[0]])) {
                finalFilteredClaims.push(item);
            }
        }

        if (filterArray && filterArray.length == 2) {
            if (
                appliedFilter[filterArray[0]].includes(item[filterArray[0]]) &&
                appliedFilter[filterArray[1]].includes(item[filterArray[1]])
            ) {
                finalFilteredClaims.push(item);
            }
        }

        if (filterArray && filterArray.length == 3) {
            if (
                appliedFilter[filterArray[0]].includes(item[filterArray[0]]) &&
                appliedFilter[filterArray[1]].includes(item[filterArray[1]]) &&
                appliedFilter[filterArray[2]].includes(item[filterArray[2]])
            ) {
                finalFilteredClaims.push(item);
            }
        }

        if (filterArray && filterArray.length == 4) {
            if (
                appliedFilter[filterArray[0]].includes(item[filterArray[0]]) &&
                appliedFilter[filterArray[1]].includes(item[filterArray[1]]) &&
                appliedFilter[filterArray[2]].includes(item[filterArray[2]]) &&
                appliedFilter[filterArray[3]].includes(item[filterArray[3]])
            ) {
                finalFilteredClaims.push(item);
            }
        }

        if (filterArray && filterArray.length == 5) {
            if (
                appliedFilter[filterArray[0]].includes(item[filterArray[0]]) &&
                appliedFilter[filterArray[1]].includes(item[filterArray[1]]) &&
                appliedFilter[filterArray[2]].includes(item[filterArray[2]]) &&
                appliedFilter[filterArray[3]].includes(item[filterArray[3]]) &&
                appliedFilter[filterArray[4]].includes(item[filterArray[4]])
            ) {
                finalFilteredClaims.push(item);
            }
        }
    });
    return finalFilteredClaims;
}