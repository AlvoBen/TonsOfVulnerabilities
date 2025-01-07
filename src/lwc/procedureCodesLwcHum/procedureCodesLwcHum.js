import { LightningElement, track, api,wire } from 'lwc';
import { CurrentPageReference } from 'lightning/navigation';
import { MessageContext } from 'lightning/messageService';
import customcss from '@salesforce/resourceUrl/LightningCRMAssets_SR_HUM';
import { loadStyle } from 'lightning/platformResourceLoader';
import {
    performLogging,
    setEventListener,
    checkloggingstatus,
    clearLoggedValues,
    getLoggingKey 
} from 'c/loggingUtilityHum';


export default class procedureCodesLwcHum extends LightningElement {
    @api procedureResult;
    @track procedureRowsTemp = [];
    @track procedureRows = [];
    @track direction = 'asc';
    @track noData = false;

 @track loggingkey;
    showloggingicon = true;
    autoLogging = true;
    @track startLogging = false
    get totalRecordCount() {
        const count = this.procedureRows ? this.procedureRows.length : 0;
        return `${count} of ${count} items`;
    }

 @wire(MessageContext)
    messageContext;

    @wire(CurrentPageReference)
    pageRef;
    
    get generateLogId(){
        return Math.random().toString(16).slice(2);
    }
    
  connectedCallback() {
        if (this.procedureResult == null || this.procedureResult == undefined || this.procedureResult == '' ) 
	    {
            this.noData = true;
        } else if (this.procedureResult) {
            this.procedureResult.forEach((element) => {
                this.procedureRowsTemp.push(element);
            });
            this.procedureRows = this.sortDataOnLoad(this.procedureRowsTemp);
            this.noData = false;
        }
	if (this.autoLogging) {
            getLoggingKey(this.pageRef).then((result) => {
                this.loggingkey = result;
            });
           
        }
	try {
            
            this.tempMemName = this.pageRef.attributes.url.split('?');
            this.tmpAuthReferralNumber = this.tempMemName[1].split('=');
           
            this.tmpAuthReferralNumber = this.tmpAuthReferralNumber[1].split('&');
            this.tmpAuthReferralNumber1 = this.tmpAuthReferralNumber[0];
            

        } catch (e) {
            console.log('Error: ' + e.error);
        }
    }

	renderedCallback() {
        Promise.all([
            loadStyle(this, customcss + '/CRM_Assets/styles/logging.css')
        ]).catch((error) => {});
    }
    //OnLoad - Sort Data Functionality -- START -- //

    //FillDate Descending, then PharmacyName asc, then DrugName asc

	handleLogging(event) {
        if (this.loggingkey && checkloggingstatus(this.loggingkey)) {
           
            performLogging(
                event,
                this.createRelatedField(),
                'Codes/Procedure Codes',
                this.loggingkey,
                this.pageRef
            );
        } else {
            getLoggingKey(this.pageRef).then((result) => {
                this.loggingkey = result;
                
                if (this.loggingkey && checkloggingstatus(this.loggingkey)) {
                    performLogging(
                        event,
                        this.createRelatedField(),
                        'Codes/Procedure Codes',
                        this.loggingkey,
                        this.pageRef
                    );
                }
            });
        }
    }
	createRelatedField() {
        return [
            {
                label: 'Authorization Referral #',
                value: this.tmpAuthReferralNumber1
            }
        ]
    }
    sortDataOnLoad(tempArray) {
        let parseData = JSON.parse(JSON.stringify(tempArray));
        parseData.sort(function (a, b) {
            let c1 = a['sProcedureCode'];
            let c2 = b['sProcedureCode'];

            if (c1 < c2) return -1;
            if (c1 > c2) return 1;
            return 0;
        });
        return parseData;
    }

    //OnLoad - Sort Data Functionality -- END -- //

    //Sorting Functionality  -- START -- //
    handleSort(event) {
        // field name
        this.sortBy = event.target.outerText;

        // calling sortdata function to sort the data based selected field
        if (this.sortBy.includes('Code')) {
            this.sortData('sProcedureCode');
        } else if (this.sortBy.includes('Description')) {
            this.sortData('sProcedureDescription');
        } else if (this.sortBy.includes('Requesting Units')) {
            this.sortData('sProcedureRequestingUnits');
        } else if (this.sortBy.includes('Authorized Units')) {
            this.sortData('sProcedureAuthorizedUnits');
        } else if (this.sortBy.includes('Type of Units')) {
            this.sortData('sProcedureTypeofUnits');
        } else if (this.sortBy.includes('Status')) {
            this.sortData('sProcedureStatus');
        }
    }

    sortData(fieldname) {
        // serialize the data before calling sort function
        let parseData = JSON.parse(JSON.stringify(this.procedureRows));
        // Return the value stored in the field
        let keyValue = (a) => {
            return a[fieldname];
        };
        // cheking reverse direction
        this.direction = this.direction === 'asc' ? 'desc' : 'asc';
        let isReverse = this.direction === 'asc' ? 1 : -1;
        // sorting data
        parseData.sort((x, y) => {
            x = keyValue(x) ? keyValue(x) : ''; // handling null values
            y = keyValue(y) ? keyValue(y) : '';
            // sorting values
            if (
                fieldname === 'sProcedureCode' ||
                fieldname === 'sProcedureDescription' ||
                fieldname === 'sProcedureDescription' ||
                fieldname === 'sProcedureAuthorizedUnits' ||
                fieldname === 'sProcedureDescription' ||
                fieldname === 'sProcedureStatus'
            ) {
                return isReverse * ('' + x).localeCompare(y);
            } else {
                return isReverse * ((x > y) - (y > x));
            }
        });
        this.procedureRows = parseData;
    }
    //Sorting Functionality  -- END -- //
}