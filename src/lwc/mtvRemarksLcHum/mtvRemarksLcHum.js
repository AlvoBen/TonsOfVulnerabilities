/*******************************************************************************************************************************
LWC JS Name : mtvRemarksLcHum.js
Function    : This JS serves as controller to mtvRemarksLcHum

Modification Log: 
Developer Name           Code Review                      Date                         Description
*--------------------------------------------------------------------------------------------------------------------------------
Sathish Babu                                          06/01/2022                   User story 3662197
Apurva Urkude                                         01/24/2023                    Defect Fix-7020
*********************************************************************************************************************************/
import { LightningElement, track, api, wire } from 'lwc';
import getDataFromService from '@salesforce/apexContinuation/MTV_Member_Provider_LC_HUM.initiateMTVRequest';
import customcss from '@salesforce/resourceUrl/LightningCRMAssets_SR_HUM';
import { loadStyle } from 'lightning/platformResourceLoader';
import { CurrentPageReference } from 'lightning/navigation';
import { ShowToastEvent } from 'lightning/platformShowToastEvent';
import ClaimsSummary_Service_Error from '@salesforce/label/c.ClaimsSummary_Service_Error';
import ClaimDetails_NoRecords_Msg from '@salesforce/label/c.ClaimDetails_NoRecords_Msg';
import {
    performLogging,
    setEventListener,
    checkloggingstatus,
    clearLoggedValues,
    getLoggingKey
} from 'c/loggingUtilityHum';

const columns = [
    {
        label: 'Entity Type',
        fieldName: 'sRemarkEntityType',
        type: 'text',
        sortable: true,
        hideDefaultActions: true,
        initialWidth: 150
    },
    {
        label: 'Identifier',
        fieldName: 'sRemarkIdentifier',
        type: 'text',
        sortable: true,
        hideDefaultActions: true,
        initialWidth: 150
    },

    {
        label: 'Create Date',
        fieldName: 'sRemarkCreationTimestamp',
        type: 'text',
        sortable: true,
        hideDefaultActions: true,
        initialWidth: 200
    },

    {
        label: 'Type',
        fieldName: 'sRemarkType',
        type: 'text',
        hideDefaultActions: true,
        initialWidth: 100
    },
    {
        label: 'Category',
        fieldName: 'sRemarkCategory',
        hideDefaultActions: true,
        type: 'text',
        initialWidth: 100
    },
    {
        label: 'Text',
        fieldName: 'sRemarkExtendedText',
        type: 'text',
        wrapText: true,
        onmouseover: true,
        hideDefaultActions: true,
        initialWidth: 400
    }
];

export default class mtvRemarksLcHum extends LightningElement {
    columns = columns;
    @track mtvData = [];
    @track mtvDataOrg;
    @api claimnumber;
    @api userAgent;
    @track noOfRecords;
    @track bNoData = false;
    @track bLoading = false;
    @api noDataMsg = 'No Records to Display';
    @track direction = 'asc';
    @track sortBy;
    @track dateSortIconHandler = 'utility:arrowup';
    @track eTypesorticonHandler = 'utility:arrowup';
    @track identifiersorticonHandler = 'utility:arrowup';
    @track sTypesortHandler = 'utility:arrowup';
    @track sCategorysortHandler = 'utility:arrowup';
    @track sTextsortHandler = 'utility:arrowup';
    focus1 = true;
    focus2 = false;
    focus3 = false;
    focus4 = false;
    focus5 = false;
    focus6 = false;

    @track loggingkey;
    showloggingicon = true;
    autoLogging = true;

    label = {
        ClaimDetails_NoRecords_Msg,
        ClaimsSummary_Service_Error
    };

    @wire(CurrentPageReference)
    pageRef;

    connectedCallback() {
        this.bLoading = true;
        this.initiateRequest();
    }

    renderedCallback() {
        Promise.all([
            loadStyle(this, customcss + '/CRM_Assets/styles/logging.css')
        ]).catch((error) => {});
    }

    initiateRequest() {
        getDataFromService({
            claimNumber: this.claimnumber
        })
            .then((data) => {

                if (
                    data == null ||
                    data == undefined||
                    data == ClaimDetails_NoRecords_Msg 
                ) {
                    this.bLoading = false;
                    this.bNoData = true;
		            this.EmptyResponse = 'No Records to Display';
                } else if (data == ClaimsSummary_Service_Error) {
                    this.bLoading = false;
                    this.bNoData = true;
		            this.EmptyResponse = 'No Records to Display';
                    this.showToast('',ClaimsSummary_Service_Error, 'error', 'sticky');
                } else {

                if (data && data.length > 0) {
                    this.showErrorMessage = false;
                    this.bLoading = false;
                    this.mtvDataOrg = this.sortDataOnLoad(data);
                   
                    if (this.mtvData == null || this.mtvData == undefined) {
                        
                        this.bLoading = false;
                        this.bNoData = true;
                    }
                    this.mtvData = JSON.parse(JSON.stringify(this.mtvDataOrg));
                    this.noOfRecords = Object.keys(this.mtvData.data).length;
                }
            }
                 
                
            })
            .catch((error) => {
                console.log(error);
                this.isError = true;
                this.bNoData = true;
                this.EmptyResponse = 'No Records to Display';
            });

        if (this.autoLogging) {
            getLoggingKey(this.pageRef).then((result) => {
                this.loggingkey = result;
            });
        }

    }

    handleLogging(event) {
       
        if (this.loggingkey && checkloggingstatus(this.loggingkey)) {

            performLogging(
                event,
                this.createRelatedField(),
                'claim Details',
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
                        'claim Details',
                        this.loggingkey,
                        this.pageRef
                    );
                }
            });
        }
    }

    showToast(strTitle, strMessage, strStyle, strMode) {
        this.dispatchEvent(
            new ShowToastEvent({
                title: strTitle,
                message: strMessage,
                variant: strStyle,
                mode: strMode
            })
        );
    }
    createRelatedField() {
        return [
            {
                label: 'Claim',
                value: this.claimnumber
            }
        ];
    }

    sortDataOnLoad(tempArray) {
        let parseData = JSON.parse(tempArray);
        parseData.data.sort(function (a, b) {
            let o1 = a['sRemarkCreationTimestamp'];
            let o2 = b['sRemarkCreationTimestamp'];
            if (o1 < o2) return 1;
            if (o1 > o2) return -1;
            return 0;
        });
        return parseData;
    }

    sortfocus(fieldname) {
        if (fieldname == 'sRemarkEntityType') {
            this.focus1 = true;
            this.focus2 = false;
            this.focus3 = false;
            this.focus4 = false;
            this.focus5 = false;
            this.focus6 = false;
        } else if (fieldname == 'sRemarkIdentifier') {
            this.focus1 = false;
            this.focus2 = true;
            this.focus3 = false;
            this.focus4 = false;
            this.focus5 = false;
            this.focus6 = false;
        } else if (fieldname == 'sRemarkCreationTimestamp') {
            this.focus1 = false;
            this.focus2 = false;
            this.focus3 = true;
            this.focus4 = false;
            this.focus5 = false;
            this.focus6 = false;
        } else if (fieldname == 'sRemarkType') {
            this.focus1 = false;
            this.focus2 = false;
            this.focus3 = false;
            this.focus4 = true;
            this.focus5 = false;
            this.focus6 = false;
        } else if (fieldname == 'sRemarkCategory') {
            this.focus1 = false;
            this.focus2 = false;
            this.focus3 = false;
            this.focus4 = false;
            this.focus5 = true;
            this.focus6 = false;
        } else if (fieldname == 'sRemarkExtendedText') {
            this.focus1 = false;
            this.focus2 = false;
            this.focus3 = false;
            this.focus4 = false;
            this.focus5 = false;
            this.focus6 = true;
        }
    }

    updateColumnSorting(event) {
        
        var selectedField = event.target.outerText;

        var sortby = '';
        if (selectedField == 'Create Date') {
            sortby = 'sRemarkCreationTimestamp';
        } else if (selectedField == 'Identifier') {
            sortby = 'sRemarkIdentifier';
        } else if (selectedField == 'Entity Type') {
            sortby = 'sRemarkEntityType';
        } else if (selectedField == 'Type') {
            sortby = 'sRemarkType';
        } else if (selectedField == 'Category') {
            sortby = 'sRemarkCategory';
        } else if (selectedField == 'Text') {
            sortby = 'sRemarkExtendedText';
        }
        this.sortData(sortby);
        this.sortfocus(sortby);
    }
    sortData(fieldname) {
        let parseData = JSON.parse(JSON.stringify(this.mtvData.data));
        
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
        if (fieldname == 'sRemarkCreationTimestamp') {
            
            this.dateSortIconHandler =
                this.direction === 'asc'
                    ? 'utility:arrowup'
                    : 'utility:arrowdown';
        }
        if (fieldname == 'sRemarkEntityType') {
            
            this.eTypesorticonHandler =
                this.direction === 'asc'
                    ? 'utility:arrowup'
                    : 'utility:arrowdown';
        }
        if (fieldname == 'sRemarkIdentifier') {
            
            this.identifiersorticonHandler =
                this.direction === 'asc'
                    ? 'utility:arrowup'
                    : 'utility:arrowdown';
        }
        if (fieldname == 'sRemarkType') {
           
            this.sTypesortHandler =
                this.direction === 'asc'
                    ? 'utility:arrowup'
                    : 'utility:arrowdown';
        }
        if (fieldname == 'sRemarkCategory') {
           
            this.sCategorysortHandler =
                this.direction === 'asc'
                    ? 'utility:arrowup'
                    : 'utility:arrowdown';
        }
        if (fieldname == 'sRemarkExtendedText') {
           
            this.sTextsortHandler =
                this.direction === 'asc'
                    ? 'utility:arrowup'
                    : 'utility:arrowdown';
        }
        this.mtvData = [];
        this.mtvData.data = parseData;
        
    }
	
	get generateLogId(){
        return Math.random().toString(16).slice(2);
    }
    
}