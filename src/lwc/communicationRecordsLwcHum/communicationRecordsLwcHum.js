/*******************************************************************************************************************************
LWC JS Name : CommunicationRecordsLwcHum.JS
Function    : This JS serves as Controller to CommunicationRecordsLwcHum

Modification Log: 
Developer Name           Code Review                      Date                         Description
*------------------------------------------------------------------------------------------------------------------------------------------------------
Suraj Patil                                         14/07/2022                    User story 3443327 Contact Information Tab.
Raj Paliwal                                         17/08/2022                    User story 3706036 Accordion Drop down Update
Raj Paliwal                                         22/08/2022                    User story 3705963 Updated Sorting and additional records view
Vishal Shinde                                       11/7/2022                     User story 3277055 CRM Service Benefits 2022- Auth/Referral: Lightning Build for Summary & Details - Logging Auth Details
Vishal Shinde                                       11/25/2022                    Defect- Fix:6678
*******************************************************************************************************************************************************/
import { LightningElement, api, track, wire } from 'lwc';
import { communicationRecordsColumns } from './columnConfig';
import callCommunicationService from '@salesforce/apexContinuation/ClinicalAuthDetails_LC_HUM.callCommunicationService';
import { CurrentPageReference } from 'lightning/navigation';
import customcss from '@salesforce/resourceUrl/LightningCRMAssets_SR_HUM';
import { MessageContext } from 'lightning/messageService';
import { loadStyle } from 'lightning/platformResourceLoader';
import {
    performLogging,
    setEventListener,
    checkloggingstatus,
    clearLoggedValues,
    getLoggingKey
} from 'c/loggingUtilityHum';
export default class CommunicationLwcHum extends LightningElement {
    @api authId;
    @api memberplanid;
    @api communicationrecordresponse = [];
    @track direction = 'asc';
    @track _communicationRecords;
    @track pageNumber = 1;
    isExpanded=false;
    
    @track sortDirHandler;
    overfieldname;
    focus1=true;
    focus2=false;
    focus3=false;
    focus4=false;
    focus5=false;
    focus6=false;
    focus7=false;
    focus8=false;

    @track columnsHeaders = {
        'Created/Update Date': 'LastModifiedDateFormated',
        'Created/Update By': 'LastModifiedBy',
        'Department': 'Department',
        'Communication Type' : 'CommunicationType',
        'Contact Method': 'ContactMethod',
        'Contact Result': 'ContactResult',
        'Contact Attempt': 'ContactAttempt',
        'Details': 'Details'
    };

    noData = false;
    count = 0;
    pageRecords = 0;
    totalCount;
    allRecordsRetrieved = false;
    @track allLetterRecords = [];
    @track dummayData = [];
    @track loggingkey;
    showloggingicon = true;
    autoLogging = true;
    @track startLogging = false

    get totalRecordCount() {
        return this.allRecordsRetrieved;
    }
    get records() {
        return this._communicationRecords;
    }

    get columns() {
        return communicationRecordsColumns;
    }

    get TotalRecordsFound() {
        return this.totalCount;
    }

    get infoMsg() {
        return `The  table is displaying 1 to ${this.pageRecords} of ${this.TotalRecordsFound} Communication Records`;
    }

    get recordCount() {
        const count = this._communicationRecords
            ? this._communicationRecords.length
            : 0;
        return `${count} of ${count} items`;
    }

    get generateRequest() {
        return this.getLettersRequest;
    }
	
	get generateLogId(){
        return Math.random().toString(16).slice(2);
    }

 @wire(CurrentPageReference)
    pageRef;

    @wire(MessageContext)
    messageContext;
    connectedCallback() {
        if (this.communicationrecordresponse !== undefined && this.communicationrecordresponse !== null && this.communicationrecordresponse.CommunicationRecords !==null && this.communicationrecordresponse.CommunicationRecords !== undefined && this.communicationrecordresponse.CommunicationRecords.length > 0) {
            let key = 0;

            this.allLetterRecords = this.communicationrecordresponse.CommunicationRecords.map(item => {
                return {
                    ...item,
                    Expanded:false,
                    name: key,
                }
                key = key + 1;
            });
            this._communicationRecords = this.allLetterRecords;
            this.direction = 'asc';
            this.sortData('LastModifiedDateFormated');
            this.pageRecords = this.communicationrecordresponse.CommunicationRecords.length
            this.totalCount = this.communicationrecordresponse.TotalRecordsFound;
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
			
        } catch (e) {
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
                'Communication Records',
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
                        'Communication Records',
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
    handleClick() {
        let pagenumber = this.pageNumber.toString();
        if (this.pageRecords + 50 > this.TotalRecordsFound) {
            this.allRecordsRetrieved = true;
        }

        callCommunicationService({
            sAuthID: this.authId,
            RecId: this.memberplanid,
            sCommunicationPageNumber: pagenumber
        })
        .then(result => {
            if (result !== null && result != undefined && result != '') {
                let parsedResp = JSON.parse(result);

                if(parsedResp.CommunicationRecordsResponse !== null && parsedResp.CommunicationRecordsResponse !== undefined)
                {
                    let tempCommmunicationRecords = [];
                    tempCommmunicationRecords = parsedResp.CommunicationRecordsResponse.CommunicationRecordsList.CommunicationRecords.map(item => {
                        return {
                            ...item,
                            Expanded:false,
                            name: this.key,
                        }
                        this.key++;
                    });
                                             
                    for (const Comm of tempCommmunicationRecords) {
                        this.allLetterRecords.push(Comm);
                    }
                    this._communicationRecords = this.allLetterRecords;
                    this.direction = 'asc';
                    this.sortData('LastModifiedDateFormated');
                    this.pageRecords = this._communicationRecords.length;
                    this.pageNumber++;                    
                }
            }
        })
        .catch(err => {
        });
 
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
        else if(fieldname=="Department"){
            this.focus1=false;
            this.focus2=false;
            this.focus3=true;
            this.focus4=false;
            this.focus5=false;
            this.focus6=false;
            this.focus7=false;
            this.focus8=false;    
        }
        else if(fieldname=="CommunicationType"){
            this.focus1=false;
            this.focus2=false;
            this.focus3=false;
            this.focus4=true;
            this.focus5=false;
            this.focus6=false;
            this.focus7=false;
            this.focus8=false;
        }
        else if(fieldname=="ContactMethod"){
            this.focus1=false;
            this.focus2=false;
            this.focus3=false;
            this.focus4=false;
            this.focus5=true;
            this.focus6=false;
            this.focus7=false;
            this.focus8=false;
        }
        else if(fieldname=="ContactResult"){
            this.focus1=false;
            this.focus2=false;
            this.focus3=false;
            this.focus4=false;
            this.focus5=false;
            this.focus6=true;
            this.focus7=false;
            this.focus8=false;
        }
        else if(fieldname=="ContactAttempt"){
            this.focus1=false;
            this.focus2=false;
            this.focus3=false;
            this.focus4=false;
            this.focus5=false;
            this.focus6=false;
            this.focus7=true;
            this.focus8=false;
        }
        else if(fieldname=='Details'){
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

        if(fieldname=='LastModifiedDateFormated'){
            this.sortDirHandler=(this.direction==='asc')?'utility:arrowup':'utility:arrowdown';   
        }
        if(fieldname=='LastModifiedBy'){
            this.sortDirHandler=(this.direction==='asc')?'utility:arrowup':'utility:arrowdown';
        }
        if(fieldname=='Department'){
            this.sortDirHandler=(this.direction==='asc')?'utility:arrowup':'utility:arrowdown';
        }
        if(fieldname=='CommunicationType'){
            this.sortDirHandler=(this.direction==='asc')?'utility:arrowup':'utility:arrowdown';
        }
        if(fieldname=='ContactMethod'){
            this.sortDirHandler=(this.direction==='asc')?'utility:arrowup':'utility:arrowdown';
        }
        if(fieldname=='ContactResult'){
            this.sortDirHandler=(this.direction==='asc')?'utility:arrowup':'utility:arrowdown';
        }
        if(fieldname=='ContactAttempt'){
            this.sortDirHandler=(this.direction==='asc')?'utility:arrowup':'utility:arrowdown';
        }
        if(fieldname=='Details'){
            this.sortDirHandler=(this.direction==='asc')?'utility:arrowup':'utility:arrowdown';
        }
        
        this._communicationRecords = [];
        this._communicationRecords = parseData;
    }

    handleToggle(event){
        var index=event.target.value;
        var data = JSON.parse(JSON.stringify(this._communicationRecords));
        data[index].Expanded=(data[index].Expanded==false)?true:false;
        this._communicationRecords = data;
        event.target.iconName=(event.target.iconName=='utility:chevrondown')?'utility:chevronright':'utility:chevrondown';
    }
}