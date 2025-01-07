/********************************************************************************************************************************************************
 * author * Ritik Agarwal
 * description * JSON structre of show set of fields on case edit/create screen on UI and will rotate this JSON and fill values in it and then use it
 *               on HTML.
 
* Technical layout decide details - there is one attribute in JSON model "onerow" which is being used to decide section layout which 2*2 or 1*1 
 *                                        with set of fields, if this attribute is false then we have 2 sction in one row and with 2 grid layout fields
 *                                        in it. And if this attribute is true means we have one section with set of fields in 2 col grid.       

* Technical JSOn attribute Details * There are some set of attributes that is being used in JSON to identify its behave ------
 
 *  1. "Prefill" attribute - this attribute will illustrate that value is prefill on UI and also user can change it later from UI.
 *  2. "customoutput" - this attribute will illustrate that value is prefill on UI and user can not change it from UI means it is only reaonly field.
 *  3. "picklist" - this attribute will illustrate that field is picklist field means custom combobox ,not standard that is given by record-edit-form.
 *  4. "radio" - this attribute will illustrate that field should be displayed on UI in radio format using HTML input type radio.
 ***********************************************************************************************************************************************/


export function getTexasLayout(caseId, recType,IntAboutType,userGroup) {
    if(userGroup.bGbo){
        if(recType === 'Member Case' ){
            return texasInformation;       
        }
    }
}

const texasInformation = [
     {
        title: 'Texas Complaint Letter',
        onerow: false,
        fields: [{ label: 'Language Preference', mapping: 'Language_Preference__c', value: '' }
        ]

    }
];