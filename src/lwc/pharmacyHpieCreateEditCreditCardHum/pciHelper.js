/*
LWC Name        : pciHelper.js
Function        : helper file
Modification Log:
* Developer Name                  Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Jonathan Dickinson			  08/17/2023					User Story 4908778: T1PRJ0870026 - SF - Tech - C12 Mail Order Management - Pharmacy - Finance tab
****************************************************************************************************************************/
import getPCIURL from '@salesforce/apex/PharmacyFinancial_LC_HUM.getPCIURL';
export function getPCIToken(creditcardnumber) {
    return new Promise((resolve, reject) => {
        getPCIURL().then(result => {
            let pciFormData = new FormData();
            pciFormData.append("CC", creditcardnumber);
            handleAPICall(result, pciFormData, "POST").then((result => {
                if (result) {
                    let response = result.replace('(', '').replace(')', '');
                    response = JSON.parse(response);
                    if (response?.TemporaryToken && response?.TemporaryToken?.length > 0) {
                        resolve(response.TemporaryToken);
                    }
                    else if (response?.ErrorMessage && response?.ErrorMessage?.length > 0) {
                        reject(response.ErrorMessage);
                    }
                }
            })).catch(error => {
                console.log('error in getTempToken() -> ', JSON.stringify(error));
            })
        });
    }).catch(error => {
        console.log(error);
    })
}

async function handleAPICall(url, bodyContent, methodType) {
    const fetchresponse = await fetch(url, {
        method: methodType,
        mode: "cors",
        body: bodyContent
    });
    let response = await fetchresponse.text();
    return new Promise((resolve, reject) => {
        if (response) {
            resolve(response);
        } else {
            reject(response);
        }
    })
}