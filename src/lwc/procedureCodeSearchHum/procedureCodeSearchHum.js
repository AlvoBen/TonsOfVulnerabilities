import { LightningElement, track ,api } from 'lwc';
import searchCodes from '@salesforce/apexContinuation/guidedFlowAuthSummary_LC_Hum.searchDetails';
import isAuthorizationRequired from '@salesforce/apexContinuation/guidedFlowAuthSummary_LC_Hum.invokeAuthRefRequest';
import getSIDFlow from '@salesforce/apex/guidedFlowAuthSummary_LC_Hum.getSID';
import coderrormsg from "@salesforce/label/c.CodeSetErrorMsg";
import { ShowToastEvent } from 'lightning/platformShowToastEvent';
const MIN_CHAR_LENGTH = 3;
const columns = [
    { label: 'Code', fieldName: 'code', type: 'text', sortable: true, wrapText: true, fixedWidth: 120 },
    { label: 'Description', fieldName: 'description', type: 'text', sortable: true, wrapText: true },
];
const INITIAL_LOAD_RECORDS = 5;
const INPUT_ERROR_MESSAGE = 'Please enter either code or service.'
const  CodeSetWarningMsg   = 'Search Criteria resulted in more than 100 entries. Only the first 100 records have been returned.'
export default class ProcedureCodeSearchHum extends  LightningElement {
    @api MemberName;
    @api BirthDate;
    @api MemberID ;
    @api recordId ;
    @api Phone;
    @track codedata = [];
    @track strCode;
    @track strService;
    @track bError = false;
    @track inputerror = false;
    @track errorMessage = '';
    columns = columns;
    @track bDataExist = false;
    defaultSortDirection = 'asc';
    sortDirection = 'asc';
    sortedBy;
    @track loaded = true;
    @track totalCodeData = [];
    @track totalCount = 0;
    @track filteredcount;
    @track sMinDate;
    @track sMaxDate;
    @track sDos;
    dateOfService;
    @track isAuthRequired = false ;
    @track isloaded = false ;
    previous = false;
    SID
    authMessage = '';
    procedureCode = [];
    label = {
        coderrormsg
    }

    connectedCallback() {
        this.initialDates();
    }

    handleCancel() {
        this.bDataExist = false;
        this.inputerror = false;
        this.bError = false;
        this.totalCodeData = [];
        this.codedata = [];
        if (this.template.querySelector('lightning-input') != null) {
            this.template.querySelectorAll('lightning-input').forEach(k => {
                k.value = '';
            })
        }
        this.strCode = '';
        this.strService = '';
        let todaysdate = new Date();
        this.sDos = todaysdate.toISOString().substring(0, 10);
    }

    displayToastEvent(message, variant, title) {
        this.dispatchEvent(new ShowToastEvent({
            title: title,
            message: message,
            variant: variant,
            mode: 'dismissable'
        }));
    }

    handleFinish() {
       
        this.loaded = false;
        let selectedRecords = this.template.querySelector("lightning-datatable").getSelectedRows();  
        if(selectedRecords.length > 1){
            this.loaded = true;

            this.bError = true;
            this.errorMessage = 'Selecting only 1 code is allowed.';
        }
        else if(selectedRecords.length < 1){
            this.loaded = true;

            this.bError = true;
            this.errorMessage = 'Select one code to proceed.';
        }
        else if (this.sDos == null) {
            this.loaded = true;
            this.bError = true;
            this.errorMessage = 'Date of Service is required';
        }
        else{
        let obj = {};
        let request  = {};
        let Servicedetails  = {};
        let Procedurecode = {};
        let Code = [];
        let Contactdetails ={};
        let MemberDetail ={};
        let  Provider = [];
        let ProviderDetails = {};
        ProviderDetails.ProviderType = "Requesting";
        ProviderDetails.NPI="1427096122";
        ProviderDetails.TaxID ="111222333";
        ProviderDetails.ProviderID = '';
        Provider.push(ProviderDetails);
        this.MemberID = this.MemberID.substring(0,9);
      
        MemberDetail.MemberID = this.MemberID;        
        MemberDetail.DOB = this.BirthDate;
        Contactdetails.ContactPhoneNumber   = this.Phone;
        Contactdetails.ContactName   = this.MemberName;
        let sDate = this.sDos
           
        if(sDate.includes('-') ){
            sDate = sDate.split('-');
            if (sDate.length > 0) {
                sDate = sDate[1] + '/' + sDate[2] + '/' + sDate[0];
                Servicedetails.DateOfService = sDate;

            }   
        }
        else{
            Servicedetails.DateOfService = sDate;

        }
        for (let i = 0; i < selectedRecords.length; i++) {
            Procedurecode = {};
                   Procedurecode.Code = selectedRecords[i].code; 
                   Code.push(Procedurecode);
                
        } 
        Servicedetails.ProcedureCodes = Code;      
        request.Member = MemberDetail;
        request.ServiceDetails = Servicedetails;
        request.ContactDetails  = Contactdetails;
        request.Provider = Provider;
        obj.IsAuthRequiredRequest = request;
        this.procedureCode = obj;
        this.callAuthRef(this.recordId,this.procedureCode);
        }
    }

    callAuthRef(recordId,obj){
        this.isloaded =false ;

        isAuthorizationRequired({ sRecordId :recordId, jObj :JSON.stringify(obj)}) 
       .then((result) => {
            this.isAuthRequired = true;
            this.isloaded = true ;
            this.authMessage  = '"' +result + '"';
            this.callFlow(result);
        })
        .catch(error => {            
            console.log('Resultttt--->' +error);
        })

    }
    callFlow(message){
        
        getSIDFlow({authMessage :message})        
        .then((result) => {
            this.SID = result;
        })
        .catch(error => {           
            console.log('Resultttt--->' +error);
        })     

    }

    navigateToCase() {
        this.isloaded = false ;
        let message = 'callfromLWC' ;
        window.location.assign(window.location.origin +'/apex/GuidedProcessLogTo_VF_HUM?policymemberID='+this.recordId+'&SubmissionData='+this.authMessage+'&Message='+message+'&SubmissionID='+this.SID  );
    }
    
    initialDates() {
        let todaysdate = new Date();
        this.sMaxDate = todaysdate.toISOString().substring(0, 10);
        let minDate = new Date();
        minDate.setMonth(todaysdate.getMonth() - 18);
        this.sMinDate = minDate.toISOString().substring(0, 10);
        this.sDos = todaysdate.toISOString().substring(0, 10);
    }

    handleDateChange(event) {
        let datedata = event.detail;

        if (datedata.keyname === 'DOS') {
            this.sDos = datedata.datevalue;
        }
    }
    handleSearch() {
        this.loaded = false;
        this.codedata = [];
        this.totalCodeData = [];
        this.bDataExist = false ;
        this.strCode = this.template.querySelector("lightning-input[data-code]").value !== ''? this.template.querySelector("lightning-input[data-code]").value.trim() : '' ; 
        this.strService =  this.template.querySelector("lightning-input[data-service]").value !== '' ? this.template.querySelector("lightning-input[data-service]").value.trim() : '' ; 
        if (this.checkvalidation()) {
            searchCodes({ textToBeSearch: this.strCode ? this.strCode : this.strService })
                .then((result) => {
                  
                    let response = JSON.parse(result);
                    if (!response.hasOwnProperty('data')) {
                        this.errorMessage = result;
                        this.bError = true;
                        this.loaded = true;
                    } else {
                        this.codedata = [];
                        this.totalCodeData = [];
                        this.bError = false;
                        let data = response.data;
                        if(data.length == 100){
                            this.bError = true;
                            this.errorMessage = CodeSetWarningMsg;
                        }
                        if (data && Array.isArray(data) && data.length > 0) {
                            data.forEach(k => {
                                this.totalCodeData.push({
                                    id: k.Id,
                                    code: k.sCode,
                                    description: k.sDescription,
                                    status: k.sStatus
                                });
                            })
                        }
                        this.totalCodeData.sort(function (a, b) {
                            return a.code > b.code ? 1 : -1
                        })
                        this.bDataExist = true;
                        this.filterdetails();
                        this.loaded = true;
                    }
                }).catch(error => {
                    this.loaded = true;
                    console.log('Resultttt--->' +error);
                })
        } else {
            this.bError = true;
            this.inputerror = true;
            this.loaded = true;
        }

    }
    filterdetails() {
        if (this.totalCodeData != null) {
            if (this.totalCodeData.length <= INITIAL_LOAD_RECORDS) {
                this.codedata = this.totalCodeData;
                this.totalCount = this.totalCodeData.length;
                this.filteredcount = this.totalCodeData.length;
            } else {
                this.codedata = this.totalCodeData.slice(0, INITIAL_LOAD_RECORDS);
                this.totalCount = this.totalCodeData.length;
                this.filteredcount = INITIAL_LOAD_RECORDS;
            }
        }
    }
    onHandleSort(event) {
        const { fieldName: sortedBy, sortDirection } = event.detail;
        const cloneData = [...this.totalCodeData];

        cloneData.sort(this.sortBy(sortedBy, sortDirection === 'asc' ? 1 : -1));
        this.totalCodeData = cloneData;
        this.sortDirection = sortDirection;
        this.sortedBy = sortedBy;
        this.filterdetails();
    }

    loadMoreData(event) {
        if (this.filteredcount != undefined && this.totalCount != undefined) {
            if ((this.filteredcount + INITIAL_LOAD_RECORDS) >= this.totalCount) {
                this.filteredcount = this.totalCount;
                this.codedata = this.totalCodeData;
            } else {
                this.filteredcount = this.filteredcount + INITIAL_LOAD_RECORDS;
                this.codedata = this.totalCodeData.slice(0, this.filteredcount);
            }
        }
    }

    sortBy(field, reverse, primer) {
        const key = primer
            ? function (x) {
                return primer(x[field]);
            }
            : function (x) {
                return x[field];
            };

        return function (a, b) {
            a = key(a);
            b = key(b);
            return reverse * ((a > b) - (b > a));
        };
    }
 checkvalidation() {       

        if (this.strCode && this.strService) {            
            return false
        } else {
            if (this.strCode && (this.strService === undefined || this.strService === null || this.strService === '')) {
                return true
            } else if (this.strService && (this.strCode === undefined || this.strCode === null || this.strCode === '')) {
                return true;
            }           
            return false;
        }
    }

    handlePrevious(){
        this.isAuthRequired = false;
        this.loaded = true;
        this.bError = false;
    }
    handleEdit(event){
        event.stopPropagation(); 
    }
}