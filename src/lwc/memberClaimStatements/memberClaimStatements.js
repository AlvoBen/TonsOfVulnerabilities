/*******************************************************************************************************************************
LWC JS Name : MemberClaimStatements.js
Function    : This JS serves as controller to MemberClaimStatements.html. 
Modification Log: 
  Developer Name           Code Review                      Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Anuradha Gajbhe                                         12/09/2022                 Original Version
* Sagar Gulleve                                           21/09/2022                 DF-6230 fix
* Anuradha Gajbhe                                         28/09/2022                 Enabling Member Claim Statements Tab Capabilities Member/Provider Claims Statements- View LWC Convertion
* Suraj Patil                                             07/10/2022                 Defect DF-6303
* Dimple Sharma                                           08/31/2023                 Defect DF-8041
*********************************************************************************************************************************/
import { LightningElement, api, track, wire } from 'lwc';
import initClaimStatementsRequest from '@salesforce/apexContinuation/MTV_Member_Provider_LC_HUM.initClaimStatementsRequest';
import { CurrentPageReference } from 'lightning/navigation';
import ClaimDetails_NoRecords_Msg from '@salesforce/label/c.ClaimDetails_NoRecords_Msg';
import ClaimsSummary_Service_Error from '@salesforce/label/c.ClaimsSummary_Service_Error';
import { ShowToastEvent } from 'lightning/platformShowToastEvent';
import customcss from '@salesforce/resourceUrl/LightningCRMAssets_SR_HUM';
import { loadStyle } from 'lightning/platformResourceLoader';
import {
    performLogging,
    performTableLogging,
    setEventListener,
    checkloggingstatus,
    clearLoggedValues,
    getLoggingKey
    } from 'c/loggingUtilityHum';


export default class MemberClaimStatements extends LightningElement {
    label = {
	     ClaimDetails_NoRecords_Msg,
	     ClaimsSummary_Service_Error
	    };

    deliveryMethodValue = '';
    get DeliveryMethodOptions() {
        return [
            { label: 'Mail', value: 'MailValue' },
            { label: 'Fax', value: 'FaxValue' }
        ];
    }

    get RecipientTypeOptions() {
        return [
            { label: 'Member', value: 'Member' },
            {
                label: 'Member Representative or Caregiver',
                value: 'MemberRepresentativeorCaregiver'
            },
            { label: 'Other', value: 'Other' }
        ];
    }

    ismemberstatement = true;
    @track memberClaimStatementData = [];
    @track hardCodedData;
    @track sortBy;
    @track sortDirection;
    @track bNoData = false;
    @track bLoading = false;
    @api totalNumberOfRecords;
    @api EmptyResponse;
    @api sortedOnLoadData;
    @api loadMoreStatus;
    record = {};
    @track sStatementTypesortHandler = 'utility:arrowup';
    @track sPrintedDatesortHandler = 'utility:arrowup';
    @track sStatementBeginDatesortHandler = 'utility:arrowup';
    @track sStatementEndDatesortHandler = 'utility:arrowup';
    @track memberClaimStatementDatarr = [];
    @track direction = 'asc';
    @api bViewAction = false;
    @api bViewPrint = false;
    @api isModalOpen = false;
    @track memberStatementItem = [];
    recordId;
	@api accountid;
    @track loggingkey;
    showloggingicon = true;
    autoLogging = true;

    @wire(CurrentPageReference)
    pageRef;
    
    focus1 = true;
    focus2 = false;
    focus3 = false;
    focus4 = false;

    connectedCallback() {
        this.bLoading = true;
	let navData = this.pageRef.attributes.url
	    ? this.pageRef.attributes.url.split('&')
            : '';
        let newObj = {};
        if (navData.length > 0) {
            navData.map((item) => {
                let splittedData = item.split('=');
                newObj[splittedData[0]] = splittedData[1];
            });
        }
        this.MemName = newObj.MemberId;
        this.ClmNbr = newObj.ClaimNbr;
		this.recordId = newObj.recordId;
        this.invokeRequest();
    }

    constructor() {
        super();
    }

    renderedCallback() {
        Promise.all([
            loadStyle(this, customcss + '/CRM_Assets/styles/logging.css')
        ]).catch((error) => {});
    }
    async invokeRequest() {
        await initClaimStatementsRequest({
            sMemName: this.MemName,
            sClmNbr: this.ClmNbr
        })
            .then((data) => {
    		if (
                    data == null ||
                    data == undefined ||
                    data == ClaimDetails_NoRecords_Msg
                ) {
                    this.bLoading = false;
                    this.bNoData = true;
                    this.EmptyResponse = 'No Records to Display';
                } else if (data == ClaimsSummary_Service_Error) {
                    this.bLoading = false;
                    this.bNoData = true;
                    this.EmptyResponse = 'No Records to Display';
                    this.showToast('', data, 'error', 'sticky');
                } else {
                    this.sortedOnLoadData = this.sortDataOnLoad(data);
                    this.bLoading = false;

                if (
                    this.sortedOnLoadData == null ||
                    this.sortedOnLoadData == undefined
                ) {
                    this.bLoading = false;
                    this.bNoData = true;
                }
                this.memberClaimStatementDatarr = JSON.parse(
                    JSON.stringify(this.sortedOnLoadData)
                );
                this.totalNumberOfRecords = this.memberClaimStatementDatarr.data.length;
	       }
            })
            .catch((error) => {
                this.EmptyResponse = 'No Records to Display';
                this.bNoData = true;
                console.log(
                    'Error Received in member claim statements: ',
                    error
                );
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
    
    createRelatedField() {
        return [
            {
                label: 'Claim:',
                value: this.ClmNbr
            }
        ];
    }

    //OnLoad - Sort Data Functionality -- START -- //

    sortDataOnLoad(tempArray) {
        let parseData = JSON.parse(tempArray);
        parseData.data.sort(function (a, b) {
            let o1 = a['sPrintedDate'];
            let o2 = b['sPrintedDate'];

            if (new Date(o1) < new Date(o2)) return 1;
            if (new Date(o1) > new Date(o2)) return -1;
            return 0;
        });
        return parseData;
    }

    //OnLoad - Sort Data Functionality -- END -- //

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

    openModal() {
        this.isModalOpen = true;
    }
    
    closeModal() {
        this.isModalOpen = false;
    }
    
    handleRowAction(event) {
        let actionName = event.target.outerText;
        let rowIndex = event.currentTarget.getAttribute('data-att');
        this.memberStatementItem = this.memberClaimStatementDatarr.data[
            rowIndex
        ];
        switch (actionName) {
            case 'Send':
                this.send(this.memberStatementItem.sDocumentKey);
                break;
            case 'View':
                this.view(this.memberStatementItem.sDocumentKey);
                break;
            default:
                null;
        }
    }

    view(row) {
        this.bViewPrint = false;
        this.bViewAction = true;
        this.recordItem = row;
        this.openModal();
    }

    send(row) {
		this.bViewPrint = false;
        this.bViewAction = false;
        this.recordItem = row;
        this.openModal();
    }

    openPdf() {
        this.bViewPrint = true;
    }

    closePopup(event) {
        this.closeModal();
    }
    sortfocus(fieldname) {
        if (fieldname == 'sStatementDescription') {
            this.focus1 = true;
            this.focus2 = false;
            this.focus3 = false;
            this.focus4 = false;

        } else if (fieldname == 'sPrintedDate') {
            this.focus1 = false;
            this.focus2 = true;
            this.focus3 = false;
            this.focus4 = false;

        } else if (fieldname == 'sStatementBeginDate') {
            this.focus1 = false;
            this.focus2 = false;
            this.focus3 = true;
            this.focus4 = false;
        } else if (fieldname == 'sStatementEndDate') {
            this.focus1 = false;
            this.focus2 = false;
            this.focus3 = false;
            this.focus4 = true;
        }
    }
    updateColumnSorting(event) {
        var selectedField = event.target.outerText;
        var sortby = '';
        if (selectedField == 'Statement Type') {
            sortby = 'sStatementDescription';
        } else if (selectedField == 'Date Sent') {
            sortby = 'sPrintedDate';
        } else if (selectedField == 'Begin Statement Period') {
            sortby = 'sStatementBeginDate';
        } else if (selectedField == 'End Statement Period') {
            sortby = 'sStatementEndDate';
        }
        this.sortData(sortby);
        this.sortfocus(sortby);
    }
    sortData(fieldname) {
        try{
        let parseData = JSON.parse(
            JSON.stringify(this.memberClaimStatementDatarr.data)
        );
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
        if (fieldname == 'sStatementDescription') {
            this.sStatementTypesortHandler =
                this.direction === 'asc'
                    ? 'utility:arrowup'
                    : 'utility:arrowdown';
        }
        if (fieldname == 'sPrintedDate') {
            this.sPrintedDatesortHandler =
                this.direction === 'asc'
                    ? 'utility:arrowup'
                    : 'utility:arrowdown';
        }
        if (fieldname == 'sStatementBeginDate') {
            this.sStatementBeginDatesortHandler =
                this.direction === 'asc'
                    ? 'utility:arrowup'
                    : 'utility:arrowdown';
        }
        if (fieldname == 'sStatementEndDate') {
            this.sStatementEndDatesortHandler =
                this.direction === 'asc'
                    ? 'utility:arrowup'
                    : 'utility:arrowdown';
        }
        this.memberClaimStatementDatarr.data = [];
        this.memberClaimStatementDatarr.data = parseData;
    }
    catch(error){
        console.log(
            'Error Received in member claim statements: ',
            error
        );
    }
    }


         
	handleSendRowAction(event) {
		this.bViewPrint = false;
        this.bViewAction = false;
        let rowIndex = event.currentTarget.getAttribute('data-att');
        this.memberStatementItem = this.memberClaimStatementDatarr.data[rowIndex];
        this.openModal();
    }
    
    handleCancelClickSelect() {
        this.isModalOpen = false;
    }
    handleSubmit() {
        this.isModalOpen = false;
    }
}