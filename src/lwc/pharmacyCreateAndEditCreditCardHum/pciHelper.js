/*
LWC Name        : pcpSummaryScreenHum.html
Function        : LWC to display pcp provider search screen.
Modification Log:
* Developer Name                  Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Nirmal Garg                     09/26/2022                 initial version US2961209
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