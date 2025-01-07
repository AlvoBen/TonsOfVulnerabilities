import {LightningElement, track,wire} from 'lwc';
import { ShowToastEvent } from 'lightning/platformShowToastEvent';
import hasUploadPermission from '@salesforce/customPermission/CRM_Retail_Upload_Access';
import ERROR_TEXT from '@salesforce/label/c.CRMRetail_Error_HUM';
import ACC_DENIED_TEXT from '@salesforce/label/c.CRMRetailTaskAccessDenTxt';
import FILE_TYPE_TEXT from '@salesforce/label/c.CRMRetailFileType';
import FILE_TYPE_ERROR_TEXT from '@salesforce/label/c.CRMRetailFileUploadErrorTxt';
import FILE_SIZE_ERROR_TEXT from '@salesforce/label/c.CRMRetailFileSizeErrorTxt';
import FILE_EMPTY_ERROR_TEXT from '@salesforce/label/c.CRMRetailFileEmptyErrTxt';
import COLUMN_FORMAT from '@salesforce/label/c.CRMRetailColumnFormat';
import FILE_DOES_NOT_CONT_TASK_REC from '@salesforce/label/c.CRMRetailFiileDoesNotContTaskRec';
import WRONG_HEADER_FORMAT from '@salesforce/label/c.CRMRetailWrongHeaderFormat';
import MAXIMUM_FILE_SIZE from '@salesforce/label/c.CRM_Retail_File_Size';
import ERROR_PARSING_FILE from '@salesforce/label/c.CRM_Retail_Parse_Error';
import NO_OF_REC_TO_DISPLAY from '@salesforce/label/c.CRM_Retail_Task_No_Of_Rec_To_Display';
import MAX_REC_ERROR from '@salesforce/label/c.CRMRetail_ImportTaskLimitText';
import TASK_IMPORT_SUCCESS from '@salesforce/label/c.CRMRetailImportTaskSuccessText';
import TASK_IMPORT_ERROR from '@salesforce/label/c.CRMRetailImportTaskError';
import SUPPORT_FILE_TYPE_TEXT from '@salesforce/label/c.CRMRetailImportTaskFileTypeText';
import SHOW_TEN_REC_TXT from '@salesforce/label/c.CRMRetailImportTaskLimitMsg';
import NO_TASK_IMPOR from '@salesforce/label/c.CRMRetailImportNoTaskMsg';
import INSERT_TASK_ERROR from '@salesforce/label/c.CRMRetailImportErrorTxt';
import NO_OF_TASK_TO_IMPORT from '@salesforce/label/c.CRMRetail_No_Of_Task_To_Import';
import INVALID_FILE_ERROR_TEXT from '@salesforce/label/c.CRMRetail_InvalidFileName';
import getSwitchResults from '@salesforce/apex/CRMRetail_Task_Import_Helper_HUM.fetchSwitchResults';
import sheetMinjs from '@salesforce/resourceUrl/CRMRetail_SheetMin_HUM';
import { loadScript } from 'lightning/platformResourceLoader';
import validateTaskFieldsOnImport from '@salesforce/apex/CRMRetail_Task_Import_Helper_HUM.validateTaskFieldsOnImport';
import SUCCESS_TEXT from '@salesforce/label/c.CRM_Retail_Waiver_Functionality_Expiration_Success';

export default class ImportObjects_LWC_HUM extends LightningElement {
    noOfRecordToDisplay =parseInt(NO_OF_REC_TO_DISPLAY);
    noOfTaskToImport = parseInt(NO_OF_TASK_TO_IMPORT);
    showTenRecTxt = SHOW_TEN_REC_TXT;
    suppFileTypeTxt = SUPPORT_FILE_TYPE_TEXT;
    noTaskImpTxt =  NO_TASK_IMPOR;
    @track data;
    @track showLoadingSpinner = false;
    @track firstTenRecords;
    @track allRecordCount;
    isImportVisible = false;
    MAX_FILE_SIZE = parseInt(MAXIMUM_FILE_SIZE);
    filename;
    @track isExportDisabled=true;
    @track isAddDisabled=false;
    validationError;
    validCount = 0;
    invalidCount = 0;
    @track erroredRecord;
    isShowCount = false;
    isAddDisabled = true;
   
    connectedCallback() {
        if(!hasUploadPermission){
            this.showToast(ACC_DENIED_TEXT,'error');              
        }else{
            getSwitchResults()
            .then((result) => {
                if(result.hasOwnProperty('isShowTaskImportBtn')){                    
                    this.isImportVisible= result['isShowTaskImportBtn'];
                }
            })
            .catch((error) => {
            });
            Promise.all([
               loadScript(this,sheetMinjs)
            ]).then(() => {
            })
        }
    }
    importCsv(event){
        this.isExportDisabled = true;
        this.firstTenRecords =null;
        this.data =null;
        this.allRecordCount =0;
        this.validCount = 0;
        this.invalidCount = 0;
        this.isShowCount = false;
        this.erroredRecord = null;
        if (event.target.files.length > 0) {
            let filesUploaded = event.target.files;
            this.filename = filesUploaded[0].name;
            if(!FILE_TYPE_TEXT.split(',').includes(filesUploaded[0].type)){ 
                this.showToast(FILE_TYPE_ERROR_TEXT,'error');               
            }            
            else if(filesUploaded[0].size > this.MAX_FILE_SIZE) {
                this.showToast(FILE_SIZE_ERROR_TEXT,'error')                  
            } 
            else{   
                this.readFile();
            }            
        }
    }
    readFile() {
        [...this.template.querySelector('lightning-input').files].forEach(async file => {
                try {
                    let result = await this.loadFileData(file);
                    if(result =='\n' || result == ""){
                        this.showToast(FILE_EMPTY_ERROR_TEXT,'error')
                    }else{
                        let parsedResult =  this.parseToCSV(result);
                        if(parsedResult.length > this.noOfTaskToImport){
                            this.showToast(MAX_REC_ERROR,'error');
                            this.firstTenRecords = null;
                        }
                        else if(parsedResult.length > 0){
                            this.data= parsedResult;               
                            this.allRecordCount = this.data.length;
                            this.isAddDisabled = false;
                        }
                    }
                } catch(e) {                    
                    this.showToast(ERROR_PARSING_FILE,'error');
                }
                this.showLoadingSpinner = false;
            });
    }
    async loadFileData(file){
        return new Promise((resolve, reject) => {
        this.showLoadingSpinner = true;
        var reader = new FileReader();
        reader.onload = event => {
            var data=event.target.result;
            try{
                var workbook=XLSX.read(data, {
                    type: 'binary'
                });
            }
            catch(e){
                this.showToast(INSERT_TASK_ERROR+INVALID_FILE_ERROR_TEXT,'error');
                this.showLoadingSpinner = false;
                return;
            }
            resolve(XLSX.utils.make_csv(workbook.Sheets["Sheet1"]));
        };
        reader.onerror = function(ex) {
            reject(ex);
        };
        reader.readAsBinaryString(file);
        });
    } 
    parseToCSV(csv){
        var lines=csv.split(/\r\n|\n/);      
        var result = [];
        var firstTenRec = [];
        let columnFormat = COLUMN_FORMAT;
        var commaRegex = /,(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)/g
        var quotesRegex = /^"(.*)"$/g
        var headers;
        if(lines.length <= 2){
            this.showToast(FILE_DOES_NOT_CONT_TASK_REC,'error');                  
        }
        else{
            headers = lines[0].split(commaRegex).map(h=> h.trim()).map(h => h.replace(quotesRegex, "$1"));
            if(headers.toString().toUpperCase().trim() != columnFormat.trim()){
            this.showToast(WRONG_HEADER_FORMAT,'error');                   
            }
            else {
            var tbleColumn = [];
            for(let index=0; index< headers.length;index++){
                var colObj={}  
                colObj.label = headers[index];
                colObj.fieldName = headers[index];
                tbleColumn.push(colObj);
            }
            this.columns =tbleColumn;
            for(var i=1;i<lines.length-1;i++){
                var obj = {};
                    var currentline=lines[i].split(commaRegex);
                for(var j=0;j< headers.length;j++){
                    obj[headers[j]] = currentline[j].replace(quotesRegex, "$1");
                        }
                result.push(obj);
                if(firstTenRec.length < this.noOfRecordToDisplay){
                    firstTenRec.push(obj);
                }
            }
            if(firstTenRec.length > 0){
                this.firstTenRecords = firstTenRec;
            }
            return result;
            }
        }
        return result;
    }
    showToast(message,vrnt){
        const evt = new ShowToastEvent({
            title: vrnt=='success' ? SUCCESS_TEXT: ERROR_TEXT,
                message: message,
                    variant: vrnt,
                        mode: 'dismissable'
                            });
        this.dispatchEvent(evt);    
    }
    createRecords(){
        this.showLoadingSpinner = true;
        validateTaskFieldsOnImport({jSONInput : JSON.stringify(this.data)})
        .then((result)=> {
            if(result.hasOwnProperty('ValidCount')){
              this.validCount = result['ValidCount'];
              if(!result.hasOwnProperty('InvalidRecord')){
                this.showToast(TASK_IMPORT_SUCCESS,'success');
              }
            } 
            if(result.hasOwnProperty('InvalidRecord')) 
            {
               this.erroredRecord = result['InvalidRecord'];
               this.isExportDisabled=false;
               this.invalidCount =  result['InvalidRecord'].length;
               this.showToast(TASK_IMPORT_ERROR,'error');
            }
            this.showLoadingSpinner = false;  
            this.isAddDisabled = true;
            this.isShowCount = true;    
        })
        .catch((error)=>{
            this.error = error;
            this.showLoadingSpinner = false;
            this.showToast(INSERT_TASK_ERROR,'error');
        });
    }
    exportRecords(){   
        let rowEnd = "\n";
        let csvString = "";
        let rowData = new Set();
        rowData = ['MEMBERID','LOCATION','ASSIGNEDTO','SUBJECT','DUEDATE','PRIORITY','ERROR'];
        csvString += rowData.join(",");
        csvString += rowEnd;
        
        for (let i = 0; i < this.erroredRecord.length; i++) {
          let colValue = 0;
          for (let key in rowData) {
            if (rowData.hasOwnProperty(key)) {
              let rowKey = rowData[key];
              if (colValue > 0) {
                csvString += ",";
              }
              let value = this.erroredRecord[i][rowKey] === undefined ? "" : this.erroredRecord[i][rowKey];
              value = value.replaceAll(/"/g, '""'); 
              csvString += '"' + value + '"';
              colValue++;
            }
          }
          csvString += rowEnd;
        }
        let downloadElement = document.createElement("a");    
        downloadElement.href = "data:text/csv;charset=utf-8," + encodeURIComponent(csvString);
        downloadElement.target = "_self";
        downloadElement.download = this.filename.substring(0,this.filename.lastIndexOf('.')) + '_Error.csv';
        document.body.appendChild(downloadElement);
        downloadElement.click();
    }
}