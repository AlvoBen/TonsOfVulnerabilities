/*
* LWC Name        : genericAccountDetails.html
* Function        : LWC to display pcp provider search screen.
* Modification Log:
* Developer Name                  Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Nirmal Garg                     09/26/2023                   Initial version
****************************************************************************************************************************/
import getAccountList from "@salesforce/apex/GenericAccountDetails_LC_HUM.getAccountList";

export function getAccountDetails(recordId) {
    return new Promise((resolve, reject) => {
        getAccountList({ recordId: recordId })
            .then(result => {
                resolve(result)
            }).catch(error => {
                reject(error);
            });
    });
}