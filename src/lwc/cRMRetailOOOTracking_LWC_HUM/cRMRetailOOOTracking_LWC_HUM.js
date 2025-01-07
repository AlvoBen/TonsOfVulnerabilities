import { LightningElement,api,track,wire } from 'lwc';
import checksOperactionTypeForSDohRecords from '@salesforce/apex/CRMRetail_OOO_Tracking_H_HUM.checksOperactionTypeForSDohRecords';
import getScreenedResult from '@salesforce/apex/CRMRetail_OOO_Tracking_H_HUM.getScreenedResult';
import { ShowToastEvent } from 'lightning/platformShowToastEvent';
import {NavigationMixin} from 'lightning/navigation';
import UPDATE from '@salesforce/label/c.CRMRetail_Update';
import CREATE from '@salesforce/label/c.CRM_Retail_Create_Option';
import CREATE_MESSAGE from '@salesforce/label/c.CRM_Retail_One_On_One_Tracking_Create_Message';
import SAVE_MESSAGE from '@salesforce/label/c.CRM_Retail_One_on_One_Tracking_Save_Message';
import EDIT_TITLE from '@salesforce/label/c.CRM_Retail_Edit_One_on_One_Tracking_Title';
import CREATE_TITLE from '@salesforce/label/c.CRM_Retail_New_One_on_One_Tracking_Title';
import RELATED_LIST_ROW_CHECK from '@salesforce/label/c.CRM_Retail_Related_List_Row_Check';
import SAVE_AND_NEW from '@salesforce/label/c.CRM_Retail_Save_And_New_Option';
import REQUIRED_FIELD_MISSING_TEXT from '@salesforce/label/c.CRMRetail_OOO_Tracking_Required_Text';
import REQUIRED_FIELD_MISSING_TEXT_DUALLIST from '@salesforce/label/c.CRMRetail_OOO_Tracking_DualList_Required_Text';
import OOO_TRACKING_CREATION_ERROR from '@salesforce/label/c.CRMRetail_OOO_Tracking_Creation_Error_Text';
import SDoh_CREATION_ERROR from '@salesforce/label/c.CRMRetail_SDoh_Record_Creation_Error';
import MOVE_DETERMINENT_TEXT from '@salesforce/label/c.CRMRetail_SDoH_Screening_Related_Text';
import deleteSdoHRecords from '@salesforce/apex/CRMRetail_OOO_Tracking_H_HUM.deleteSDoHBeforeOOOTrackingDelete';
import callSDohAPIOnUpdateOrInsert from '@salesforce/apex/CRMRetail_OOO_Tracking_H_HUM.callSDohAPIOnUpdateOrInsert';
import RECORD_SYNC_ERROR from '@salesforce/label/c.CRMRetail_Sync_Error_Message';
import RECORD_DELETE_ERROR from '@salesforce/label/c.CRM_Retail_Delete_Error_Message';
import RECORD_SAVE from '@salesforce/label/c.CRM_Retail_Record_Save';
import RECORD_SAVE_AND_NEW from '@salesforce/label/c.CRM_Retail_Save_And_New';
import OOO_Opacity from '@salesforce/label/c.CRM_Retail_OOO_opacity';
import RECORD_CREATED_DATE from '@salesforce/label/c.CRM_Retail_Created_Date';
import DETERMINANT_VERY_LIKELY from '@salesforce/label/c.CRM_Retail_Very_Likely';
import DETERMINANT_VERY_UNLIKELY from '@salesforce/label/c.CRM_Retail_Very_Unlikely';
import APPOINTMENT_TIME from '@salesforce/label/c.CRM_Retail_Appointment_Time';
import ERROR_TEXT from '@salesforce/label/c.CRMRetail_Error_HUM';
import SUCCESS_TEXT from '@salesforce/label/c.CRM_Retail_Waiver_Functionality_Expiration_Success';
import FIELDS_TEXT from '@salesforce/label/c.CRM_Retail_Fields';
import ACCOUNT_RELATION from '@salesforce/label/c.CRM_Retail_Account_Relation';
import ENTERPRISE_ID from '@salesforce/label/c.CRM_Retail_Enterprise_Id_Key';
import SAVE_OPTION from '@salesforce/label/c.CRM_Retail_Save_Option';
import INSERT_TEXT from '@salesforce/label/c.CRM_Retail_Insert_Event';
import UPDATE_TEXT from '@salesforce/label/c.CRM_Retail_Update_Event';
import DELETE_TEXT from '@salesforce/label/c.CRM_Retail_Delete_Event';
import MODALCLOSE from '@salesforce/label/c.CRM_Retail_HandleClose';
import getCRMFunctionalitySwitch from '@salesforce/apex/CRMRetail_HomePage_LC_HUM.fetchSwitchResults';
import getRecentOOORecords from '@salesforce/apex/CRMRetail_OOO_Tracking_H_HUM.getRecentOOORecords';
import UNEXPECTED_ERROR from '@salesforce/label/c.CRMRetail_Checkin_Error_Message';
import NoOOORecord from '@salesforce/label/c.CRMRetail_No_Recent_OOO_Record_Msg';
export default class CRMRetailOOOTracking_LWC_HUM extends NavigationMixin(LightningElement) {
    @api oooRecordId;
    @api selectedRowData;
    isShowSaveAndNew;
    recordFormLabel;
    accountName;
    isShowScreenigModal;
    isShowConfirmationModal;
    isEligibleForScreening;
    nameOfButtonClicked;
    @track screenedDeterminantValues;
    moveDeterText=MOVE_DETERMINENT_TEXT;
    isShowSystemInfo =false;
    currentChosenValues = [];
    queriedAvailabeValues=[];
    isCheckedSdohScreening = false;
    listOfSdohDeterminantToInsertVeryLikely = [];
    listOfSdohDeterminantToInsertVeryUnLikely = [];
    listOfSdohDeterminantToDeleteVeryLikely = [];
    listOfSdohDeterminantToDeleteVeryUnLikely = [];
    listOfSdohDeterminantToUpdateVeryLikely = [];
    listOfSdohDeterminantToUpdateVeryUnLikely = [];
    copyOfqueriedChosenValues = [];
    hasCurrentRecord =false;
    oneOnOneCreatedDate =null;
    listOfStatus = [];
    enterpriseId;
    accId;
    isChangeDeterminants =false;
    isShowSpinner=false;
    isShowSpinnerMain=false;
    switch_3434998 = true;
    switch_OOORecent_4647349;
    styleClass='slds-modal__content';
    divStyleClass='slds-modal__content slds-p-around_medium';  
    oOOTrackinColumns =  [
        { label: 'Date/Time',wrapText: true, type:'date',typeAttributes:{
            year: "numeric",
            month: "2-digit",
            day: "2-digit",
            hour: "2-digit",
            minute: "2-digit"
        },fixedWidth : 150, fieldName: 'AppointmentDateTime' },
        { label: 'Created By', wrapText: true,fixedWidth : 150,fieldName: 'CreatedByName' },
        { label: 'Description of Visit', wrapText: true, fieldName: 'DescriptionOfVisit' },
        { label: 'Other Comments',   wrapText: true, fieldName: 'OtherComments'},
    ];
    oOOTrackingRecords = [];    
    showRecentOOOTable = false;
    showRecentOOOSection = false;
    showNoOOOData = false;
    NoOOORecordLabel = NoOOORecord;
    handleFormLoad(event){ 
        getCRMFunctionalitySwitch({})
        .then(result=>{
            if(result)
            {
                this.switch_3434998 = (result.Switch_3434998) ? result.Switch_3434998: false;        
                this.switch_OOORecent_4647349 = (result.Switch_4647349) ? result.Switch_4647349 : false;
                this.styleClass = this.switch_3434998 ? 'slds-modal__content modalBody' : 'slds-modal__content';
                this.divStyleClass = this.switch_3434998 ? 'slds-modal__content slds-p-around_medium modalBody' : 'slds-modal__content slds-p-around_medium';        
                if(this.isCurrentRecordAvailable()){
                    this.hasCurrentRecord = true;
                    this.isShowSaveAndNew = false;
                    this.recordFormLabel = EDIT_TITLE;
                    var objectValue=event.detail.records[this.oooRecordId];
                    var fieldsValue=objectValue[FIELDS_TEXT];
                    var appoTime=fieldsValue[APPOINTMENT_TIME].value;
                    this.template.querySelector(".appointmentTimeId").value = appoTime;
                    this.accountName =fieldsValue[ACCOUNT_RELATION].displayValue;
                    this.oneOnOneCreatedDate= fieldsValue[RECORD_CREATED_DATE].value;
                    this.isShowSystemInfo=true;
                    if(this.template.querySelector(".sdoHCompletedId").value){
                        this.isCheckedSdohScreening = true;
                    }
                    getScreenedResult({ 
                        oooRecordId : this.oooRecordId
                            }).then((result) => {
                                if(result){
                                    if(result.hasOwnProperty(DETERMINANT_VERY_LIKELY)){
                                        this.currentChosenValues = result[DETERMINANT_VERY_LIKELY]; 
                                        this.copyOfqueriedChosenValues = result[DETERMINANT_VERY_LIKELY];
                                    }
                                    if(result.hasOwnProperty(DETERMINANT_VERY_UNLIKELY)){
                                        this.queriedAvailabeValues = result[DETERMINANT_VERY_UNLIKELY];
                                    }
                                    if(result.hasOwnProperty(ENTERPRISE_ID)){
                                        this.enterpriseId = result[ENTERPRISE_ID];
                                    }
                                }
                            })
                        .catch((error) => {
                        });
                }else{
                    this.isShowSaveAndNew = true;
                    this.recordFormLabel = CREATE_TITLE; 
                }
                if(this.selectedRowData != "undefined" && this.selectedRowData != null ){
                    var dateValue = new Date(this.selectedRowData.Date);
                    const dtf = new Intl.DateTimeFormat('en', {
                        year: 'numeric',
                            month: '2-digit',
                                day: '2-digit'
                                    })
                        const [{value: mo}, , {value: da}, , {value: ye}] = dtf.formatToParts(dateValue);
                    let formatedDate = `${ye}-${mo}-${da}`;
                    var hourValue = dateValue.getHours() > 9 ? dateValue.getHours() : '0'+dateValue.getHours();
                    var minValue = dateValue.getMinutes() > 9 ? dateValue.getMinutes() : '0'+dateValue.getMinutes();
                    var timeValue = hourValue+':'+minValue+':00.000'; 
                    this.template.querySelector(".accId").value = this.selectedRowData.accountId;
                    this.template.querySelector(".locId").value = this.selectedRowData.locationId;
                    this.template.querySelector(".visitorTypeId").value = this.selectedRowData.visitorType;
                    this.template.querySelector(".appointmentDateId").value = formatedDate;
                    this.template.querySelector(".appointmentTimeId").value = timeValue;
                    this.template.querySelector(".interactionId").value = this.selectedRowData.id.split("-")[0];
                    this.accountName =this.selectedRowData.accountName;
                    this.enterpriseId = this.selectedRowData.EnterpriseId;
                    this.accId =  this.selectedRowData.accountId;
                    this.isChangeDeterminants = true;
                } 
                this.loadRecentOOOTracking();      
            }           
        });                        
    }

    loadRecentOOOTracking()
    {
        if(this.switch_OOORecent_4647349)
        {
            this.showRecentOOOSection = true;
            getRecentOOORecords({accountId : this.accId, oooId : this.oooRecordId })
            .then(ooodata=>{
                let tempIns=[];
                if(!ooodata?.isError)
                {
                    if(ooodata?.sResult?.length>0)
                    {
                        this.showRecentOOOTable = true;
                        this.showNoOOOData = false;
                        ooodata.sResult.forEach(entry=>{
    
                            const dateStr = entry.Appointment_Date__c;
                            const timeInMilliseconds = entry.Appointment_Time__c;
                            const timeInDate = new Date(timeInMilliseconds);
    
                            const hours = timeInDate.getUTCHours();
                            const minutes = timeInDate.getUTCMinutes();
                            const seconds = timeInDate.getUTCSeconds();
                            const milliseconds = timeInDate.getUTCMilliseconds();
    
                            const [year, month, day] = dateStr.split("-").map(Number);
    
                            const combinedDateTime = new Date(year, month - 1, day, hours, minutes, seconds, milliseconds);
                            let dateTime = combinedDateTime.toISOString();
    
                            tempIns.push( 
                            { 
                                AppointmentDateTime : dateTime,
                                DescriptionOfVisit : entry.Description_of_Visit__c,
                                CreatedByName : entry.CreatedBy.Name,
                                OtherComments : entry.Other_Comments__c
                            });
                        })
                        this.oOOTrackingRecords = tempIns;
                    }
                    else{
                        this.showNoOOOData = true;
                        this.showRecentOOOTable = false;
                    }
                }
                else{
                    this.showRecentOOOSection = true;
                    this.showRecentOOOTable = true;
                    this.errorHandler(UNEXPECTED_ERROR);
                } 
            })
            .catch(error=>{
                this.showRecentOOOSection = true;
                this.showRecentOOOTable = true;
                this.errorHandler(UNEXPECTED_ERROR);
            })
        }
    }

    handleFormSubmit(event) { 
        if(this.switch_3434998){
            this.isShowSpinnerMain = true;    
        }       
        if(event.target.name === SAVE_AND_NEW){
            this.nameOfButtonClicked = RECORD_SAVE_AND_NEW;
        }
        else{
            this.nameOfButtonClicked = SAVE_OPTION;
        }
        if( this.template.querySelector(".sdoHCompletedId").value){
            if( this.template.querySelector(".sdohDetermntId").value != ""){
                if(this.checkFieldValidity()){
                    this.moveToNextScreen();
                }
            }
            else
            {
                this.template.querySelector(".sdohDeterRequiredId").classList.remove("slds-hide");
                this.template.querySelector(".showErrorText").setError(REQUIRED_FIELD_MISSING_TEXT_DUALLIST);
                if(this.switch_3434998){
                    this.isShowSpinnerMain = false;    
                } 
            }
        }
        else{ 
            if(this.checkFieldValidity()){ 
                this.template.querySelector(".showErrorText").classList.add("slds-hide");    
                this.template.querySelector(".appointmentTimeHiddenId").value = this.template.querySelector(".appointmentTimeId").value;
                if(this.isCheckedSdohScreening && !this.template.querySelector(".sdoHCompletedId").value){
                    this.deleteSDohRecords();
                }
                else{
                    this.template.querySelector("lightning-record-edit-form").submit();
                }
            }
            if(this.switch_3434998){
                this.isShowSpinnerMain = false;    
            } 
        }  
    }
    moveToNextScreen(){
        this.isEligibleForScreening = true;
        var splitedArry = [];
        this.screenedDeterminantValues =  this.template.querySelector(".sdohDetermntId").value;
        splitedArry =  this.template.querySelector(".sdohDetermntId").value.split(';');
        var optionsVal =  [];
        for(var i = 0;i < splitedArry.length; i++){
            optionsVal.push({label:splitedArry[i],
                value:splitedArry[i]
                    }); 
        }
        this.selectedDeterminentsForScreening = optionsVal;
        if(this.hasCurrentRecord){
            for(let k=0;k<this.queriedAvailabeValues.length;k++){
                if(!splitedArry.includes(this.queriedAvailabeValues[k])){
                    this.isChangeDeterminants=true;
                }
            }
            for(let l=0;l<this.copyOfqueriedChosenValues.length;l++){
                if(!splitedArry.includes(this.copyOfqueriedChosenValues[l])){
                    this.isChangeDeterminants =true;
                }
            }
            for(let m=0;m<splitedArry.length;m++){
                if(!this.queriedAvailabeValues.includes(splitedArry[m]) && !this.copyOfqueriedChosenValues.includes(splitedArry[m])){
                    this.isChangeDeterminants =true; 
                }
            }
        }
        this.template.querySelector(".oneOoneModal").classList.add(OOO_Opacity);
        this.isShowScreenigModal = true;
        if(this.switch_3434998){
            this.isShowSpinnerMain = false;    
        } 
    }
    onChangeSdhoDeteminants(event){
        this.isChangeDeterminants=true;
        this.currentChosenValues = event.detail.value;
    }
    handleFormSuccess(event) {
        this.isShowSpinner = false;
        if(this.isEligibleForScreening){            
            var recId = event.detail.id; 
            var oooCreatedDate=event.detail.fields[RECORD_CREATED_DATE].value;
            this.creatSdohDataAfterScreening(oooCreatedDate,recId);
        }
        if(this.nameOfButtonClicked == RECORD_SAVE_AND_NEW){
            this.template.querySelectorAll('.inputVal').forEach(function(fields) {
                fields.reset();
            });
            this.template.querySelector('.sdoHCompletedId').reset();
            this.template.querySelector('.sdohDetermntId').reset();
            this.isShowConfirmationModal = false;
            this.isShowScreenigModal = false;
            this.template.querySelector('.oneOoneModal').classList.remove(OOO_Opacity);
            this.template.querySelector(".sdohDeterRequiredId").classList.add("slds-hide"); 
            this.loadRecentOOOTracking();
        }
        else{  
            this.redirectAfterEdit();
        }
        if(this.hasCurrentRecord){
            this.showSuccessToast(UPDATE);
        }else if(!this.switch_3434998){
            this.showSuccessToast(CREATE);
        }
    }
    creatSdohDataAfterScreening(oooCreatedDate,oooRecordId){
        checksOperactionTypeForSDohRecords({ 
            createdDate : oooCreatedDate,
                oooRecordId : oooRecordId,
                    sdohDeterToInstVryLkly : this.listOfSdohDeterminantToInsertVeryLikely,
                        sdohDeterToInstVryUnLkly : this.listOfSdohDeterminantToInsertVeryUnLikely,
                            sdohDeterToDelVryLkly : this.listOfSdohDeterminantToDeleteVeryLikely,
                                sdohDeterToDelVryUnLkly : this.listOfSdohDeterminantToDeleteVeryUnLikely,
                                    sdohDeterToUpdtVryLkly : this.listOfSdohDeterminantToUpdateVeryLikely,
                                        sdohDeterToUpdtVryUnLkly : this.listOfSdohDeterminantToUpdateVeryUnLikely
                                            }).then((result) => {
                                            })
            .catch((error) => {
                let message = SDoh_CREATION_ERROR;
                this.errorHandler(message);
                this.isShowSpinner = false;
            });
    }
    handlerOOOError(){
        let message=OOO_TRACKING_CREATION_ERROR;
        this.errorHandler(message);
        this.isShowSpinner = false;
    }
    errorHandler(message){
        const evt = new ShowToastEvent({
            title: ERROR_TEXT,
                message: message,
                    variant: 'error',
                        mode: 'dismissable'
                            });
        this.dispatchEvent(evt);    
    }
    submitScreeningResults() {
        if(this.switch_3434998){
            this.isShowSpinner = true; 
        }         
        if(this.currentChosenValues.length>0){
            this.isShowSpinner = true;
            if(this.isChangeDeterminants){
                if(this.hasCurrentRecord){
                    this.prepareDeterminantsListOnUpdate();
                    this.callAPI();
                }
                else{
                    this.prepareDeterminantsListOnInsertForSecondScreen(); 
                    this.callAPI(); 
                }
            }
            else{
                if(this.listOfStatus.length>0){
                    this.callAPI()
                }
                else{
                this.template.querySelector(".sdohDetermntId").value = this.screenedDeterminantValues;
                this.template.querySelector(".appointmentTimeHiddenId").value = this.template.querySelector(".appointmentTimeId").value;
                this.template.querySelector("lightning-record-edit-form").submit();
                }
            }   
        }
        else{            
            this.isShowConfirmationModal = true;
        }
    }
    submitConfirmationDetails() {
        this.isShowSpinner = true;
        if(this.isChangeDeterminants){
            if(this.hasCurrentRecord){
                this.prepareDeterminantsListOnUpdate();
                this.callAPI();
            }
            else{
                this.prepareDeterminantsListOnInsertForSecondScreen(); 
                this.callAPI(); 
            }
        }else{
            if(this.listOfStatus.length>0){
                this.callAPI()
            }
            else{
            this.template.querySelector(".sdohDetermntId").value = this.screenedDeterminantValues;
            this.template.querySelector(".appointmentTimeHiddenId").value = this.template.querySelector(".appointmentTimeId").value;
            this.template.querySelector("lightning-record-edit-form").submit();
            }
        }  
    }
    closeConfirmationModel(){ 
        this.isShowConfirmationModal = false;
        this.isShowScreenigModal = true;
        if(this.switch_3434998){
            this.isShowSpinner = false; 
        }
    }
    closeScreenigModel() { 
        this.isShowScreenigModal = false ;
        this.template.querySelector('.oneOoneModal').classList.remove(OOO_Opacity);
        this.template.querySelector(".sdohDeterRequiredId").classList.add("slds-hide"); 
        this.template.querySelector(".showErrorText").classList.add("slds-hide");
    }
    
    cancelMainModal(){        
        if(this.switch_3434998 && !this.hasCurrentRecord){
            const refreshEvnt=new CustomEvent(MODALCLOSE);
            this.dispatchEvent(refreshEvnt);
        }
        else{
            this.redirectAfterEdit();        
        }        
    }
    redirectAfterEdit () {
        if(this.hasCurrentRecord){
            var currUrl=JSON.stringify((window.location));
            if(currUrl.includes(RELATED_LIST_ROW_CHECK)){
                this[NavigationMixin.Navigate]({
                    type: 'standard__recordRelationshipPage',
                        attributes: {
                            recordId: this.template.querySelector(".accId").value,
                                objectApiName: 'Account',
                                    relationshipApiName: 'One_on_One_Trackings__r',
                                        actionName: 'view'
                                            }
                });
            }
            else{
                this[NavigationMixin.Navigate]({
                    type: 'standard__recordPage',
                        attributes: {
                            recordId: this.oooRecordId,
                                actionName: 'view'
                                    }
                });
            }
        }else{
            const refreshEvnt=new CustomEvent(RECORD_SAVE);
            this.dispatchEvent(refreshEvnt);
            
        }  
    }
    hideDependencyError(){
        if(!this.template.querySelector(".sdoHCompletedId").value){
            this.template.querySelector(".sdohDeterRequiredId").classList.add("slds-hide"); 
        }    
    }
    isCurrentRecordAvailable(){
        if(this.oooRecordId !== "undefined" && this.oooRecordId != null){
            return true;
        }
        return false;
    }
    checkFieldValidity(){
        var flag=true;
        this.template.querySelectorAll(".inputVal").forEach(element => {
            if(!element.reportValidity()){
                this.template.querySelector(".showErrorText").setError(REQUIRED_FIELD_MISSING_TEXT);
                this.template.querySelector(".showErrorText").classList.add("slds-show");
                flag=false;
            }
        });
        if(!this.template.querySelector('.appointmentTimeId').reportValidity()){
            this.template.querySelector(".showErrorText").setError(REQUIRED_FIELD_MISSING_TEXT);
            this.template.querySelector(".showErrorText").classList.add("slds-show");
            flag=false;
        }
        return flag;
    }
    showSuccessToast(eventType) {
        var toastMessage;
        if(eventType == UPDATE){
            toastMessage = SAVE_MESSAGE;
        }
        else{
            toastMessage = CREATE_MESSAGE;
        }
        const evt = new ShowToastEvent({
            title: SUCCESS_TEXT,
                message: toastMessage,
                    variant: 'success',
                        mode: 'dismissable'
                            });
        this.dispatchEvent(evt);        
    }
    deleteSDohRecords() {
        deleteSdoHRecords({ oooRecordId : this.oooRecordId,
                            isOverrideButton :false
                        })
            .then((result) => { 
                if(result === ERROR_TEXT){
                    this.dispatchEvent(
                        new ShowToastEvent({
                            title: RECORD_DELETE_ERROR,
                                message: RECORD_SYNC_ERROR,
                                    variant: 'error'
                                        })
                    );
                    
                } 
                else{
                    this.template.querySelector("lightning-record-edit-form").submit();
                }         
            })
            .catch(error => {
                this.dispatchEvent(
                    new ShowToastEvent({
                        title: RECORD_DELETE_ERROR,
                            message: RECORD_SYNC_ERROR,
                                variant: 'error'
                                    })
                );
            });
    } 
    prepareDeterminantsListOnUpdate(){
        this.clearAllVaraibles();
        this.listOfStatus = [];
        let currentChoosenDeterminants =  this.currentChosenValues;
        let currListOfAvailabeOption =this.getCurrentDeterOptions();
        for(let j=0 ;j<this.copyOfqueriedChosenValues.length;j++){
            if(!currentChoosenDeterminants.includes(this.copyOfqueriedChosenValues[j])){
                if(currListOfAvailabeOption.includes(this.copyOfqueriedChosenValues[j])){
                    this.listOfSdohDeterminantToUpdateVeryUnLikely.push(this.copyOfqueriedChosenValues[j]);
                }else{
                    this.listOfSdohDeterminantToDeleteVeryLikely.push(this.copyOfqueriedChosenValues[j]);   
                }
            }else{
                if(!currListOfAvailabeOption.includes(this.copyOfqueriedChosenValues[j])){
                    this.listOfSdohDeterminantToDeleteVeryLikely.push(this.copyOfqueriedChosenValues[j]);
                }
            }
        }
        for(let i=0; i<currListOfAvailabeOption.length;i++){
            if(this.queriedAvailabeValues.includes(currListOfAvailabeOption[i])){
                if(currentChoosenDeterminants.includes(currListOfAvailabeOption[i])){
                    this.listOfSdohDeterminantToUpdateVeryLikely.push(currListOfAvailabeOption[i]);
                }
            }else if(!this.copyOfqueriedChosenValues.includes(currListOfAvailabeOption[i])){
                if(currentChoosenDeterminants.includes(currListOfAvailabeOption[i])){
                    this.listOfSdohDeterminantToInsertVeryLikely.push(currListOfAvailabeOption[i]);
                }else{
                    this.listOfSdohDeterminantToInsertVeryUnLikely.push(currListOfAvailabeOption[i]);
                }
            }
        }
        for(let k=0 ;k<this.queriedAvailabeValues.length; k++){
            if(!currListOfAvailabeOption.includes(this.queriedAvailabeValues[k])){
                this.listOfSdohDeterminantToDeleteVeryUnLikely.push(this.queriedAvailabeValues[k]);   
            }
        }
        if(this.listOfSdohDeterminantToInsertVeryLikely.length >0 || this.listOfSdohDeterminantToInsertVeryUnLikely.length >0) 
        {
            this.listOfStatus.push(INSERT_TEXT);
        }  
        if(this.listOfSdohDeterminantToUpdateVeryLikely.length >0 || this.listOfSdohDeterminantToUpdateVeryUnLikely.length >0) 
        {
            this.listOfStatus.push(UPDATE_TEXT);
        }  
        if(this.listOfSdohDeterminantToDeleteVeryUnLikely.length >0 || this.listOfSdohDeterminantToDeleteVeryLikely.length >0) 
        {
            this.listOfStatus.push(DELETE_TEXT);
        }                          
    }
    prepareDeterminantsListOnInsertForSecondScreen(){
        this.clearAllVaraibles();
        let currentChoosenDeterminants = this.currentChosenValues;
        let currListOfAvailabeOption =this.getCurrentDeterOptions();
        for(let i=0 ;i < currListOfAvailabeOption.length;i++){
            if(currentChoosenDeterminants.includes(currListOfAvailabeOption[i])){
                this.listOfSdohDeterminantToInsertVeryLikely.push(currListOfAvailabeOption[i]);
            }
            else{
                this.listOfSdohDeterminantToInsertVeryUnLikely.push(currListOfAvailabeOption[i]);   
            }
        }
        if(this.listOfSdohDeterminantToInsertVeryLikely.length > 0 || this.listOfSdohDeterminantToInsertVeryUnLikely.length >0) 
        {
            this.listOfStatus.push(INSERT_TEXT);
        } 
    }
    prepareDeterminantsListOnInsertForConfirmationScreen(){
        this.clearAllVaraibles();
        this.listOfStatus = [];
        this.listOfSdohDeterminantToInsertVeryUnLikely = this.getCurrentDeterOptions();
        if(this.listOfSdohDeterminantToInsertVeryUnLikely) 
        {
            this.listOfStatus.push(INSERT_TEXT);
        } 
    }
    getCurrentDeterOptions(){
        let currentAvailableOptions = this.template.querySelector(".socialDeterDualBoxId").options;
        let currListOfAvailabeOption =[];
        if(currentAvailableOptions.length>0){
            for(let i=0;i<currentAvailableOptions.length;i++){
                currListOfAvailabeOption.push(currentAvailableOptions[i].label);
            }
        }
        return currListOfAvailabeOption;
    }
    callAPI(){
        callSDohAPIOnUpdateOrInsert({ 
            createdDate:this.oneOnOneCreatedDate,
                sdohDeterToInstVryLkly : this.listOfSdohDeterminantToInsertVeryLikely,
                    sdohDeterToInstVryUnLkly : this.listOfSdohDeterminantToInsertVeryUnLikely,
                        sdohDeterToDelVryLkly : this.listOfSdohDeterminantToDeleteVeryLikely,
                            sdohDeterToDelVryUnLkly : this.listOfSdohDeterminantToDeleteVeryUnLikely,
                                sdohDeterToUpdtVryLkly : this.listOfSdohDeterminantToUpdateVeryLikely,
                                    sdohDeterToUpdtVryUnLkly : this.listOfSdohDeterminantToUpdateVeryUnLikely,
                                        listOfStatus : this.listOfStatus,
                                            accEnterpriseId :this.enterpriseId
                                    })
            .then((result) => {           
                if(result.length == 0){
                    this.template.querySelector(".sdohDetermntId").value = this.screenedDeterminantValues;
                    this.template.querySelector(".appointmentTimeHiddenId").value = this.template.querySelector(".appointmentTimeId").value;
                    this.template.querySelector("lightning-record-edit-form").submit();
                }
                else{
                    this.isChangeDeterminants = false;
                    this.isShowSpinner = false;
                    this.listOfStatus = result;
                    this.dispatchEvent(
                        new ShowToastEvent({
                            title: RECORD_DELETE_ERROR,
                                message: RECORD_SYNC_ERROR,
                                    variant: 'error'
                                        })
                    );
                    
                }          
            })
            .catch(error => {                
                this.isChangeDeterminants = false;
                this.isShowSpinner = false;
                this.dispatchEvent(
                    new ShowToastEvent({
                        title: RECORD_DELETE_ERROR,
                            message: RECORD_SYNC_ERROR,
                                variant: 'error'
                                    })
                );
                
            }); 
        
    }
    clearAllVaraibles(){
        this.listOfSdohDeterminantToInsertVeryLikely = [];
        this.listOfSdohDeterminantToInsertVeryUnLikely = [];
        this.listOfSdohDeterminantToDeleteVeryLikely = [];
        this.listOfSdohDeterminantToDeleteVeryUnLikely = [];
        this.listOfSdohDeterminantToUpdateVeryLikely = [];
        this.listOfSdohDeterminantToUpdateVeryUnLikely = [];
    }
    
}