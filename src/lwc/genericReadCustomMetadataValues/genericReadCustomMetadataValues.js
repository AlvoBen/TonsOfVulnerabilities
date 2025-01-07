/*
LWC Name        : GenericReadCustomMetadataValues.js
Function        : JS file for reading custom metadata values.

Modification Log:
* Developer Name                  Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Nirmal Garg                    06/01/2023                   Original Version
****************************************************************************************************************************/
import { LightningElement } from 'lwc';
import getMetadataValues from '@salesforce/apex/GenericReadCustomMetadat_LC_HUM.getMetadataValues';
export default class GenericReadCustomMetadataValues extends LightningElement { }

const getCustomMetadataValues = (pagename) => {
    let customvalues = [];
    return new Promise((resolve, reject) => {
        if (pagename && pagename?.length > 0) {
            getMetadataValues({ pagename: pagename }).then(result => {
                if (result && Array.isArray(result) && result?.length > 0) {
                    result.forEach(k => {
                        customvalues.push({
                            label: k?.Label__c ?? '',
                            name: k?.Name__c ?? '',
                            dataid: k?.DataId__c ?? '',
                            placeholder: k?.Placeholder__c ?? '',
                            visible: k?.Visible__c ?? false,
                            required: k?.Required__c ?? false,
                            multipicklist: k?.Multipicklist__c ?? false,
                            combobox: k?.ControlType__c === 'ComboBox' ? true : false,
                            textbox: k?.ControlType__c === 'TextBox' ? true : false,
                            style: k?.Style__c ?? '',
                            class: k?.Class__c ?? '',
                            disabled: false,
                            button: k?.ControlType__c === 'Button' ? true : false,
                            variant: k?.Variant__c ?? 'brand',
                            order: k?.Order__c ?? 0,
                            value: '',
                            options: [],
                            fieldName: k?.FieldName__c ?? ''
                        })
                    })
                    resolve(customvalues);
                } else {
                    resolve(null);
                }
            })
        } else {
            reject(new Error('Page name is missing.'))
        }
    });
}

export { getCustomMetadataValues }