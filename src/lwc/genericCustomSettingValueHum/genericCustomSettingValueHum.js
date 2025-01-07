/*
LWC Name        : GenericCustomSettingValueHum.js
Function        : JS file for reading custom metadata values.

Modification Log:
* Developer Name                  Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Nirmal Garg                    07/26/2023                   Original Version
****************************************************************************************************************************/
import { LightningElement } from 'lwc';
import getCustomSetting from '@salesforce/apex/GenericCustomSettingValue_LC_HUM.getCustomSettingValue';
export default class GenericCustomSettingValueHum extends LightningElement { }

export function getCustomSettingValue(type, name) {
    return new Promise((resolve, reject) => {
        getCustomSetting({ type: type, name: name }).then(result => {
            if (result && Object.keys(result)?.length > 0) {
                switch (type?.toLowerCase()) {
                    case 'switch':
                        resolve(result);
                    case 'webservice':
                        resolve(result);
                    case 'certificate':
                        resolve(result);
                }
            } else {
                resolve(null);
            }
        }).catch(error => {
            reject(error);
        })
    });
}