/*******************************************************************************************************************************
LWC JS Name : CommunicationLwcHum.JS
Function    : This JS serves as Controller to CommunicationLwcHum

Modification Log: 
Developer Name           Code Review                      Date                         Description
*--------------------------------------------------------------------------------------------------------------------------------
Rajesh Narode                                         14/07/2022                    User story 3443327 Contact Information Tab.
Vishal Shinde                                         17/08/2022                    User story 3706110 Letters: update sorting and view.
Vishal Shinde                                         11/7/2022                     User story 3277055 CRM Service Benefits 2022- Auth/Referral: Lightning Build for Summary & Details - Logging Auth Details
Vishal Shinde                                         11/25/2022                    Defect- Fix:6678
*********************************************************************************************************************************/
import { LightningElement, api, track, wire } from 'lwc';
import { communicationColumns} from './columnConfig';
import { MessageContext } from 'lightning/messageService';
import authLettersRequest from '@salesforce/apexContinuation/ClinicalAuthDetails_LC_HUM.callLettersService';
import customcss from '@salesforce/resourceUrl/LightningCRMAssets_SR_HUM';
import { loadStyle } from 'lightning/platformResourceLoader';
import {performLogging, setEventListener, checkloggingstatus, clearLoggedValues, getLoggingKey} from 'c/loggingUtilityHum';
import { CurrentPageReference } from 'lightning/navigation';
export default class CommunicationLwcHum extends LightningElement {
    @api authId;
    @api memberplanid;
    @track direction = 'asc';
    @track _communicationRecords;
    @track Lettersrecords;
    @track pageNumber = 1;
    @track columnsHeaders = {
        'Created/Updated Date': 'LastModifiedDateFormated',
        'Created/Updated By': 'LastModifiedBy',
        'Letter Template Name': 'TemplateName',
        'Status': 'Status',
        'Fulfillment Status': 'FullfillmentStatus',
        'P2P Offered in Letters': 'P2POfferedinLetter',
        'Letter Name': 'LetterName',
        'Void Remarks': 'VoidRemarks'
    };
    noData = false;
    count = 0;
    pageRecords = 0;
    totalCount;
    allRecordsRetrieved = false;
    @track allLetterRecords = [];
    @track dummayData = [];
    @api lettersresponse = [];
    @track sortDirHandler;
    @track loggingkey;
    showloggingicon = true;
    autoLogging = true;
    @track startLogging = false

    overfieldname;

    focus1=true;
    focus2=false;
    focus3=false;
    focus4=false;
    focus5=false;
    focus6=false;
    focus7=false;
    focus8=false;

    get totalRecordCount() {
        return this.allRecordsRetrieved || this.totalCount <= 50;
    }
    get records() {
        return this._communicationRecords;
    }

    get columns() {
        return communicationColumns;
    }


    get TotalRecordsFound() {
        return this.totalCount;
    }

    get infoMsg() {
        return `The  table is displaying 1 to ${this.pageRecords} of ${this.TotalRecordsFound} letters Records`;
    }

    get recordCount() {
        const count = this.lettersresponse.LettersRecords
            ? this.lettersresponse.LettersRecords.length
            : 0;
        return `${count} of ${count} items`;
    }

    get generateRequest() {
        return this.getLettersRequest;
    }

	  @wire(MessageContext)
    messageContext;

    @wire(CurrentPageReference)
    pageRef;
	
	get generateLogId(){
        return Math.random().toString(16).slice(2);
    }
	
    connectedCallback() {
       
        if (this.lettersresponse !== undefined && this.lettersresponse !== null && this.lettersresponse.LettersRecords !==null && this.lettersresponse.LettersRecords !== undefined && this.lettersresponse.LettersRecords.length > 0) {
            
            let key = 0;
            this.allLetterRecords = this.lettersresponse.LettersRecords.map(item => {
                return {
                    ...item,
                    name: key,
                }
                key = key + 1;
            });

            this._communicationRecords = this.allLetterRecords;
            this.direction = 'asc';
            this.sortData('LastModifiedDateFormated');
            this.pageRecords = this.lettersresponse.LettersRecords.length
            this.totalCount = this.lettersresponse.TotalRecordsFound;
            if (this.pageRecords == this.TotalRecordsFound) {
                this.allRecordsRetrieved = true;
            }
		}
        else {
            this.noData = true;
            this.allRecordsRetrieved = true;
        }
		try {
            
            this.tempMemName = this.pageRef.attributes.url.split('?');
            this.tmpAuthReferralNumber = this.tempMemName[1].split('=');
            this.tmpAuthReferralNumber = this.tmpAuthReferralNumber[1].split('&');
            this.tmpAuthReferralNumber1 = this.tmpAuthReferralNumber[0];
			
			if(this.autoLogging){
                getLoggingKey(this.pageRef).then(result =>{
                    this.loggingkey = result;
                });
            }
            

        } 
         catch (e) {
            console.log('Error: ' + e.error);
        }
    }

   renderedCallback() {
        Promise.all([
            loadStyle(this, customcss + '/CRM_Assets/styles/logging.css')
        ]).catch((error) => {});
    }

	handleLogging(event) {
        if (this.loggingkey && checkloggingstatus(this.loggingkey)) {
            
            performLogging(
                event,
                this.createRelatedField(),
                'Letters',
                this.loggingkey,
                this.pageRef
            );
        } else {
            getLoggingKey(this.pageRef).then((result) => {
                this.loggingkey = result;
                
                if (this.loggingkey && checkloggingstatus(this.loggingkey)) {
                    performLogging(event, this.createRelatedField(),'Letters',this.loggingkey,this.pageRef );
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
    handleClick() {
		let pagenumber = this.pageNumber.toString();
        if (this.pageRecords + 50 > this.TotalRecordsFound) {
            this.allRecordsRetrieved = true;
        }
        authLettersRequest({
            sAuthID: this.authId,
            sPolicyMemRecID: this.memberplanid,
            sLettersPageNumber: pagenumber
        })
            .then((result) => {
                if (result !== null && result != undefined && result != '') {
                    let parsedResp = JSON.parse(result);

                    if (
                        parsedResp.LettersResponse !== null &&
                        parsedResp.LettersResponse !== undefined
                    ) {
                        let key = 0;
                        let tempLettersRecords = [];
                        tempLettersRecords = parsedResp.LettersResponse.LettersRecordsList.LettersRecords.map(item => {
                            return {
                                ...item,
                                Expanded:false,
                                name: key,
                            }
                            key++;
                        });
                        for (const oLetter of tempLettersRecords) {
                            this.allLetterRecords.push(oLetter);
                        }

                        this._communicationRecords = this.allLetterRecords;
                        this.direction = 'asc';
                        this.sortData('LastModifiedDateFormated');
                        this.pageRecords = this._communicationRecords.length;
                        this.pageNumber++;
                    }
                }
            })
            .catch((err) => {});
    }

    sortfocus(fieldname){
        if(fieldname== "LastModifiedDateFormated"){
            this.focus1=true;
            this.focus2=false;
            this.focus3=false;
            this.focus4=false;
            this.focus5=false;
            this.focus6=false;
            this.focus7=false;
            this.focus8=false;    
        }
        else if(fieldname=="LastModifiedBy"){
            this.focus1=false;
            this.focus2=true;
            this.focus3=false;
            this.focus4=false;
            this.focus5=false;
            this.focus6=false;
            this.focus7=false;
            this.focus8=false;
        }
        else if(fieldname=="TemplateName"){
            this.focus1=false;
            this.focus2=false;
            this.focus3=true;
            this.focus4=false;
            this.focus5=false;
            this.focus6=false;
            this.focus7=false;
            this.focus8=false;    
        }
        else if(fieldname=="Status"){
            this.focus1=false;
            this.focus2=false;
            this.focus3=false;
            this.focus4=true;
            this.focus5=false;
            this.focus6=false;
            this.focus7=false;
            this.focus8=false;
        }
        else if(fieldname=="FullfillmentStatus"){
            this.focus1=false;
            this.focus2=false;
            this.focus3=false;
            this.focus4=false;
            this.focus5=true;
            this.focus6=false;
            this.focus7=false;
            this.focus8=false;
        }
        else if(fieldname=="P2POfferedinLetter"){
            this.focus1=false;
            this.focus2=false;
            this.focus3=false;
            this.focus4=false;
            this.focus5=false;
            this.focus6=true;
            this.focus7=false;
            this.focus8=false;
        }
        else if(fieldname=="LetterName"){
            this.focus1=false;
            this.focus2=false;
            this.focus3=false;
            this.focus4=false;
            this.focus5=false;
            this.focus6=false;
            this.focus7=true;
            this.focus8=false;
        }
        else if(fieldname=='VoidRemarks'){
            this.focus1=false;
            this.focus2=false;
            this.focus3=false;
            this.focus4=false;
            this.focus5=false;
            this.focus6=false;
            this.focus7=false;
            this.focus8=true;
        }

    }

    handleSort(event) {
        this.sortData(this.columnsHeaders[event.target.outerText]);
        this.overfieldname=this.columnsHeaders[event.target.outerText];
        this.sortfocus(this.overfieldname);
    }

    sortData(fieldname) {
        let parseData = JSON.parse(JSON.stringify(this._communicationRecords));
        let keyValue = (a) => {
            return a[fieldname];
        };

        this.direction = this.direction === 'asc' ? 'desc' : 'asc';
        let isReverse = this.direction === 'asc' ? 1 : -1;
        parseData.sort((x, y) => {
            x = keyValue(x) ? keyValue(x) : '';
            y = keyValue(y) ? keyValue(y) : '';
            return isReverse * ((x > y) - (y > x));
        });

        if (fieldname == 'LastModifiedDateFormated') {
            this.sortDirHandler =
                this.direction === 'asc'
                    ? 'utility:arrowup'
                    : 'utility:arrowdown';
        }

        if (fieldname == 'LastModifiedBy') {
            this.sortDirHandler =
                this.direction === 'asc'
                    ? 'utility:arrowup'
                    : 'utility:arrowdown';
        }

        if (fieldname == 'TemplateName') {
            this.sortDirHandler =
                this.direction === 'asc'
                    ? 'utility:arrowup'
                    : 'utility:arrowdown';
        }

        if (fieldname == 'Status') {
            this.sortDirHandler =
                this.direction === 'asc'
                    ? 'utility:arrowup'
                    : 'utility:arrowdown';
        }

        if (fieldname == 'FullfillmentStatus') {
            this.sortDirHandler =
                this.direction === 'asc'
                    ? 'utility:arrowup'
                    : 'utility:arrowdown';
        }

        if (fieldname == 'P2POfferedinLetter') {
            this.sortDirHandler =
                this.direction === 'asc'
                    ? 'utility:arrowup'
                    : 'utility:arrowdown';
        }

        if (fieldname == 'LetterName') {
            this.sortDirHandler =
                this.direction === 'asc'
                    ? 'utility:arrowup'
                    : 'utility:arrowdown';
        }

        if (fieldname == 'VoidRemarks') {
            this.sortDirHandler =
                this.direction === 'asc'
                    ? 'utility:arrowup'
                    : 'utility:arrowdown';
        }


        this._communicationRecords = [];
        this._communicationRecords = parseData;
    }
}