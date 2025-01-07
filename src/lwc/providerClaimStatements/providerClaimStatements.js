/*******************************************************************************************************************************
LWC JS Name : MemberClaimStatements.js
Function    : This JS serves as controller to MemberClaimStatements.html.
Modification Log:
  Developer Name           Code Review                      Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Anuradha Gajbhe                                         12/09/2022                 Original Version
* Sagar Gulleve                                           21/09/2022                 DF-6230 fix
* Anuradha Gajbhe                                         12/09/2022                 Enabling Member Claim Statements Tab Capabilities Member/Provider Claims Statements- View LWC Convertion.
* Vishal Shinde                                           12/5/2022                  US- 3940239-Claims System Integration Claims--Enabling Provider Claim Statements - Send- Interaction Restriction.
* Dimple Sharma                                           08/31/2023                 Defect DF-8041 Fix
* Anuradha Gajbhe								          03/01/2024				 US-5480525: DF- 8386: Address is not updating on Resend for Provider/Member Claim Statements on Case Pop-up.
*********************************************************************************************************************************/
import { LightningElement, wire, api, track } from 'lwc';
import initPrvdrClaimStatementsRequest from '@salesforce/apexContinuation/MTV_Member_Provider_LC_HUM.initPrvdrClaimStatementsRequest';
import providerAddress from '@salesforce/apex/Claim_Send_Statement_LC_HUM.providerAddress';
import ClaimSendStatementWarning from '@salesforce/label/c.ClaimSendStatementWarning';
import ClaimSendStatementError from '@salesforce/label/c.ClaimSendStatementError';
import { CurrentPageReference, NavigationMixin } from 'lightning/navigation';
import ClaimDetails_NoRecords_Msg from '@salesforce/label/c.ClaimDetails_NoRecords_Msg';
import ClaimsSummary_Service_Error from '@salesforce/label/c.ClaimsSummary_Service_Error';
import { ShowToastEvent } from 'lightning/platformShowToastEvent';
import customcss from '@salesforce/resourceUrl/LightningCRMAssets_SR_HUM';
import { loadStyle } from 'lightning/platformResourceLoader';
import {
    performLogging,
    setEventListener,
    checkloggingstatus,
    clearLoggedValues,
    getLoggingKey
} from 'c/loggingUtilityHum';
 
export default class ProviderClaimStatement extends NavigationMixin(
    LightningElement
) {
     label = {
         ClaimDetails_NoRecords_Msg,
         ClaimsSummary_Service_Error
        };
       
    prvrdrData;
    isproviderstatement = true;
    @track prvdrClaimsStmt = [];
    @track bNoData = false;
    @track bLoading = false;
    @api noDataMsg = 'No Records to Display';
    @api sortedOnLoadData;
    @track recordsCount;
    @api remittId='';
    @track direction = 'asc';
    @api isResend = false;
    @track remitSortIconHandler = 'utility:arrowup';
    @track datesorticonHandler = 'utility:arrowup';
    @track providerClaimStatementDatarr = [];
    @track providerStatementItem = [];
    @api bViewAction = false;
    @api bViewPrint = false;
    @api isModalOpen = false;
    @api accountid;
    recordId;
    focus1=true;
    focus2=false;
    overfieldname;
@track SendButtonBool=true;
   
    @track loggingkey;
    showloggingicon = true;
    autoLogging = true;
    @track columnsHeaders = {
        'Remittance ID': 'sRemitId',
        'Date Sent': 'sPrintedDate'
    };
    @api addremitId(remitId){
        this.remittId=remitId;
    this.initProviderClaimStatements();
    }
   
    @wire(CurrentPageReference)
    pageRef;
    connectedCallback() {
        try
        {
            this.bLoading = true;
            let navData = this.pageRef.attributes.url.split('&');
            let newObj = {};
            navData.map((item) => {
                let splittedData = item.split('=');
                newObj[splittedData[0]] = splittedData[1];
            });
            this.MemName  = newObj.MemberId;
            this.ClmNbr = newObj.ClaimNbr;
            this.recordId = newObj.recordId;
        }
        catch (e) {
            console.log('Error: ' + e.error);
        }
        const selectedEvent = new CustomEvent('getremittid', { detail: '' });
        this.dispatchEvent(selectedEvent);
    }
 
    constructor() {
        super();
    }
   
    renderedCallback() {
        Promise.all([
            loadStyle(this, customcss + '/CRM_Assets/styles/logging.css')
        ]).catch((error) => {});
    }
 
    initProviderClaimStatements() {
        var remitId=(this.remittId!=undefined && this.remittId!=null)?this.remittId.toString():'';
 
        initPrvdrClaimStatementsRequest({
            sMemName: this.MemName,
            sClmNbr: this.ClmNbr,
            sRemitIds: remitId
        })
            .then((result) => {
                if(result == null ||
                result == undefined ||
                result == ClaimDetails_NoRecords_Msg)
                {
                    this.bLoading = false;
                    this.bNoData = true;
                    this.noDataMsg = 'No Records to Display';
                }else if (result == ClaimsSummary_Service_Error) {
                    this.bLoading = false;
                    this.bNoData = true;
                    this.noDataMsg = 'No Records to Display';
                    this.showToast('', result, 'error', 'sticky');
                } else {
                this.sortedOnLoadData = this.sortDataOnLoad(result);
                this.prvrdrData = result;
               
                if (
                    this.sortedOnLoadData == null ||
                    this.sortedOnLoadData == undefined
                ) {
                    this.bLoading = false;
                    this.bNoData = true;
                }
                this.prvdrClaimsStmt = JSON.parse(
                    JSON.stringify(this.sortedOnLoadData)
                );
                this.providerClaimStatementDatarr = this.prvdrClaimsStmt;
                this.bLoading = false;
                this.recordsCount = this.prvdrClaimsStmt.data.length;
                }
               
            })
            .catch((error) => {
                this.EmptyResponse = 'No Records to Display';
                this.bNoData = true;
                console.log(
                    'Error received in provider claim statement Data',
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
 
    get generateLogId(){
        return Math.random().toString(16).slice(2);
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
   
    sortfocus(fieldname){
        if(fieldname=="sRemitId"){
            this.focus1=true;
            this.focus2=false;
        }
        else if(fieldname=="sPrintedDate"){
            this.focus1=false;
            this.focus2=true;
        }
    }
   
    updateColumnSorting(event) {
        this.overfieldname=this.columnsHeaders[event.target.outerText];
        var selectedField = JSON.stringify(event.target.outerText);
        var sfield = '';
        if (selectedField == 'Remittance ID') {
            sfield = 'sRemitId';
           
        } else if (selectedField == 'Date Sent') {
            sfield = 'sPrintedDate';
           
        }
       
        this.sortData(JSON.stringify(event.target.outerText));
        this.sortfocus(this.overfieldname);
    }
 
    sortData(fieldname) {
        var sfield =
            fieldname == '"Remittance ID"' ? 'sRemitId' : 'sPrintedDate';
        let parseData = JSON.parse(JSON.stringify(this.prvdrClaimsStmt.data));
        let keyValue = (a) => {
            return a[sfield];
        };
 
        this.direction = this.direction === 'asc' ? 'desc' : 'asc';
        let isReverse = this.direction === 'asc' ? 1 : -1;
        parseData.sort((x, y) => {
            x = keyValue(x) ? keyValue(x) : '';
            y = keyValue(y) ? keyValue(y) : '';
            return isReverse * ((x > y) - (y > x));
        });
        if (sfield == 'sRemitId') {
            this.remitSortIconHandler =
                this.direction === 'asc'
                    ? 'utility:arrowup'
                    : 'utility:arrowdown';
        }
        if (sfield == 'sPrintedDate') {
            this.datesorticonHandler =
                this.direction === 'asc'
                    ? 'utility:arrowup'
                    : 'utility:arrowdown';
        }
        this.prvdrClaimsStmt.data = [];
        this.prvdrClaimsStmt.data = parseData;
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
        this.providerStatementItem = this.providerClaimStatementDatarr.data[
            rowIndex
        ];
 
        switch (actionName) {
            case 'Send':
 
                providerAddress({
                    PolicyMemberId : this.recordId
                })
                .then((result) =>{
                   
       
                    if(result == ClaimSendStatementWarning)
                    {
                        this.SendButtonBool = false;
                       
                        this.showToastNotification(
                            'Error',
                             ClaimSendStatementWarning,
                            'Error',
                            ''
                        );
                    }
                    else if (result == ClaimSendStatementError)
                    {
                        this.SendButtonBool = false;
                       
                        this.showToastNotification(
                            'Error',
                             ClaimSendStatementError,
                            'Error',
                            ''
                        );
                    }
                    else
                    {
                        if(this.SendButtonBool==true){
                            this.providerAddr = JSON.parse(result);
                            this.send(this.providerStatementItem.sDocumentKey);
                        }
                    }
                })
                .catch((error) => {
                     reject(error);
                 });
                break;
            case 'View':
                this.view(this.providerStatementItem.sDocumentKey);
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
 
showToastNotification(sTitle, sMessage, sVariant, sMode) {
        const evt = new ShowToastEvent({
            title: sTitle,
            message: sMessage,
            variant: sVariant,
            mode: sMode
        });
        this.dispatchEvent(evt);
    }
    openPdf() {
        this.bViewPrint = true;
    }
    closePopup() {
        this.closeModal();
    }
 
    send(row) {
    this.bViewPrint = false;
        this.bViewAction = false;
        this.recordItem = row;
        this.openModal();
    }
 
    handleCancelClickSelect() {
        this.isModalOpen = false;
    }
    handleSubmit() {
        this.isModalOpen = false;
    }
 
}