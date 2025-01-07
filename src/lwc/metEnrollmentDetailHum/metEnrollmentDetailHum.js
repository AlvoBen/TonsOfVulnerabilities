/*******************************************************************************************************************************
LWC JS Name : MetEnrollmentDetailHum.js
Function    : This JS serves as controller to MetEnrollmentDetailHum.html. 

Modification Log: 
Developer Name           Code Review                      Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Pooja Kumbhar                                           11/02/2022                 initial version(azure #)
* Pooja Kumbhar											  11/15/2022				 DF-6615 Adding scrollbars on viewAll page
* Pooja Kumbhar											  11/29/2022				 US - 3863263 Adding standard changes for table rec count
*********************************************************************************************************************************/

import { LightningElement, track, wire, api } from 'lwc';
import getMETRecords from '@salesforce/apex/METEnrollmentCaseDetailTable_LC_HUM.getMETRecords';
import { getRecord } from 'lightning/uiRecordApi';
import TRACK_ID from '@salesforce/schema/Case.Medicare_Track_ID__c';
import Origin from '@salesforce/schema/Case.Origin';
import Type from '@salesforce/schema/Case.Type';
import Subtype from '@salesforce/schema/Case.Subtype__c';
import { ShowToastEvent } from 'lightning/platformShowToastEvent';
import { getLabels } from 'c/customLabelsHum';
import { openSubTab } from 'c/workSpaceUtilityComponentHum';

const fields = [TRACK_ID, Origin, Type, Subtype];

export default class MetEnrollmentDetailHum extends LightningElement {

    @api recordId;
    @api recordslength;
    @api minColWidth;
    
    @track popOverCss = 'custom-help-text slds-popover slds-popover_tooltip slds-fall-into-ground hc-popover-top';
    sldsTable = "slds-table slds-table_cell-buffer slds-table_bordered slds-table_col-bordered ";
    helpText = 'Copy to Clipboard';
    
    @track sMetEnrollTitle = 'MET Enrollment (' + this.MetRecords + ')';
    @track labels = getLabels();
    @track METData = [];
    @track NoMETData = false;
    @track oViewAllParams;
    TrackID;
    Origin;
    Type;
    Subtype;
    showTrackID = true;

    METcolumns = [
        { label: 'Task', fieldName: 'Task', type: 'text', hideDefaultActions: true, sortable: true, wrapText: true },
        { label: 'Action', fieldName: 'Action', type: 'text', hideDefaultActions: true, sortable: true, wrapText: true },
        { label: 'Source', fieldName: 'Source', type: 'text', hideDefaultActions: true, sortable: true, wrapText: true },
        { label: 'Status', fieldName: 'Status', type: 'text', hideDefaultActions: true, sortable: true, wrapText: true },
        { label: 'Created Date', fieldName: 'CreatedDate', type: 'text', hideDefaultActions: true, sortable: true, wrapText: true },
        {
            label: 'Created By',
            fieldName: 'CreatedBy',
            type: 'url',
            typeAttributes: { label: { fieldName: 'CreatedByName' }, target: '_self' },
            hideDefaultActions: true,
            sortable: true,
            wrapText: true
        },
        { label: 'Last Modified Date', fieldName: 'LastModifiedDate', type: 'text', hideDefaultActions: true, sortable: true, wrapText: true },
        {
            label: 'Last Modified By',
            fieldName: 'LastModifiedBy',
            type: 'url',
            typeAttributes: { label: { fieldName: 'LastModifiedByName' }, target: '_self' },
            hideDefaultActions: true,
            sortable: true,
            wrapText: true
        },
        { label: 'Cancelled Date', fieldName: 'CancelledByDate', type: 'text', hideDefaultActions: true, sortable: true, wrapText: true },
        {
            label: 'Cancelled By',
            fieldName: 'CancelledBy',
            type: 'url',
            typeAttributes: { label: { fieldName: 'CancelledByName' }, target: '_self' },
            hideDefaultActions: true,
            sortable: true,
            wrapText: true
        }

    ];

    @wire(getRecord, {
        recordId: '$recordId',
        fields: fields
    })
    wireuser({ error, data }) {
        if (error) {} else if (data) {
            this.TrackID = data.fields.Medicare_Track_ID__c.value;
            this.Origin = data.fields.Origin.value;
            this.Type = data.fields.Type.value;
            this.Subtype = data.fields.Subtype__c.value;
            if (this.Origin == "CRMTRR" && this.Type == "TRR Inquiry" && this.Subtype == "TRR") {
                this.showTrackID = false;
            }
        }
    }

    async connectedCallback() {
		if (this.minColWidth == '' || this.minColWidth == null || this.minColWidth == undefined) {
            this.minColWidth = 150;

        } 
        if (this.recordId != '' && this.recordId != null && this.recordId != undefined) {

            await getMETRecords({ sCaseRecordId: this.recordId })
                .then((data) => {
                    data = JSON.parse(data);
                    let TempMETData = [];
                    if (data.length > 0) {
                        for (var i = 0; i < data.length; i++) {
                            let elt = {};
                            elt.Task = data[i].sTask;
                            elt.Action = data[i].sAction;
                            elt.Source = data[i].sSource;
                            elt.Status = data[i].sStatus;
                            elt.CreatedDate = data[i].sCreatedDate;
                            elt.CreatedByName = data[i].sCreatedBy.split(',')[0];
                            elt.CreatedBy = '/' + data[i].sCreatedBy.split(',')[2];
                            elt.LastModifiedDate = data[i].sLastModDate;
                            elt.LastModifiedByName = data[i].sLastModifiedBy.split(',')[0];
                            elt.LastModifiedBy = '/' + data[i].sLastModifiedBy.split(',')[2];
                            elt.CancelledByDate = data[i].sCancelledByDate;
                            if (data[i].sCancelledBy != '' && data[i].sCancelledBy != null && data[i].sCancelledBy != undefined) {
                                elt.CancelledByName = data[i].sCancelledBy.split(',')[0];
                                elt.CancelledBy = '/' + data[i].sCancelledBy.split(',')[2];
                            } else {
                                elt.CancelledByName = '';
                                elt.CancelledBy = '';
                            }

                            TempMETData.push(elt);
                        }

                        // update total records count  
                        if (this.recordslength != false) {
                            this.METData = [];
                            if (data.length > 0) {
                                this.recordslength = true;
                            }
                            if (data.length > 6) {
                                this.totalRecords = '6+';

                                for (var i = 0; i < 6; i++) {
                                    this.METData.push(TempMETData[i]);
                                }
                            } else {
                                this.totalRecords = data.length;
                                this.METData = TempMETData;
                            }

                        } else if (this.recordslength == false) {
                            this.METData = [];
                            this.METData = TempMETData;
                            this.totalRecords = this.METData.length;
                        }
                        this.oViewAllParams = {
                            sRecordId: this.recordId,
                            sRecordsLength: false
                        }
                    } else {
                        this.totalRecords = '0';
                        let elt = {};
                        elt.id = 0;
                        elt.Action = 'No records to display';
                        TempMETData.push(elt);
                        this.METData = TempMETData;
                        this.NoMETData = true;
                        this.sldsTable = '';
                    }
                    }).catch(error => {
                    this.dispatchEvent(
                        new ShowToastEvent({
                            title: 'Error! ' + error.message,
                            message: error.message,
                            variant: 'error',
                        }),
                    );
                });
        }

    }

    CopyToClipboard() {
        let CopyMe = this.template.querySelector('.copy-Me');
        CopyMe = CopyMe.value;
        var hiddenInput = document.createElement("input");
        hiddenInput.setAttribute("value", CopyMe);

        //Append Hidden Input to the body
        document.body.appendChild(hiddenInput);
        hiddenInput.select();

        // Executing the copy command
        document.execCommand("copy");
        document.body.removeChild(hiddenInput);

        this.dispatchEvent(
            new ShowToastEvent({
                title: 'This text has been copied',

                variant: 'Success',
            }),
        );

    }

    doSorting(event) {
        this.sortBy = event.detail.fieldName;
        this.sortDirection = event.detail.sortDirection;
        this.sortData(this.sortBy, this.sortDirection);
    }

    sortData(fieldname, direction) {
        let parseData = JSON.parse(JSON.stringify(this.METData));
        // Return the value stored in the field
        let keyValue = (a) => {
            return a[fieldname];
        };
        // cheking reverse direction
        let isReverse = direction === 'asc' ? 1 : -1;
        // sorting data
        parseData.sort((x, y) => {
            x = keyValue(x) ? keyValue(x) : ''; // handling null values
            y = keyValue(y) ? keyValue(y) : '';
            // sorting values based on direction
            return isReverse * ((x > y) - (y > x));
        });
        this.METData = parseData;
    }

    async onViewAllClick(evnt) {
        openSubTab({
            nameOfScreen: 'METEnrollment',
            title: 'MET Enrollment',
            oParams: {
                ...this.oViewAllParams
            },
            icon: 'standard:case',
        }, undefined, this);


    }

    onMouseOut() {
        this.bMouseOutTracker = true;
        this.toggleToolTip('slds-fall-into-ground', 'slds-rise-from-ground');
    }

    omMouseOver() {
        this.bMouseOutTracker = false;
        this.toggleToolTip('slds-rise-from-ground', 'slds-fall-into-ground');
    }
    toggleToolTip(addClass, removeClass) {

        if (this.helpText) {
            const tooltipEle = this.template.querySelector('[data-custom-help="custom-help"]');
            tooltipEle.classList.add(addClass);
            tooltipEle.classList.remove(removeClass);
        }
    }


}