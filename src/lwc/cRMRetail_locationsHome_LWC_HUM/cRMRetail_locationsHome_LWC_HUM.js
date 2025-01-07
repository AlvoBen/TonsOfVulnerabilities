import { LightningElement,wire, api } from 'lwc';
import loadLocationsandInteractionDate from '@salesforce/apex/CRMRetail_HomePage_LC_HUM.loadLocationsandInteractionDate';
import deduceAttendance from '@salesforce/apex/CRMRetail_HomePage_LC_HUM.deduceAttendance';
import getOrSetInteractionDateInCache from '@salesforce/apex/CRMRetail_HomePage_LC_HUM.getOrSetInteractionDateInCache';
import getorSetLocationInCache from '@salesforce/apex/CRMRetail_HomePage_LC_HUM.getorSetLocationInCache';
import changeAttendanceCount from '@salesforce/apex/CRMRetail_HomePage_LC_HUM.changeAttendanceCount';
import saveDefaultLocationRecord from '@salesforce/apex/CRMRetail_HomePage_LC_HUM.saveDefaultLocationRecord';
import restoreLocationandDate from '@salesforce/apex/CRMRetail_HomePage_LC_HUM.resetDefault';
import fetchSwitch from '@salesforce/apex/CRMRetail_HomePage_LC_HUM.fetchSwitchResults';
import CRMRetail_CacheSetupError from '@salesforce/label/c.CRMRetail_CacheSetupError';
import CRMRetail_IntDateCacheUpdateError from '@salesforce/label/c.CRMRetail_IntDateCacheUpdateError';
import CRMRetail_LocationCacheUpdateError from '@salesforce/label/c.CRMRetail_LocationCacheUpdateError';
import CRMRetail_SaveHomeLocationError from '@salesforce/label/c.CRMRetail_SaveHomeLocationError';
import CRMRetail_LocationError from '@salesforce/label/c.CRMRetail_LocationError';
import CRMRetail_SelectHomeLocation from '@salesforce/label/c.CRMRetail_SelectHomeLocation';
import CRMRetail_Interaction_Display_Criteria from '@salesforce/label/c.CRMRetail_Interaction_Display_Criteria';
import CRMRetail_Attendance from '@salesforce/label/c.CRMRetail_Attendance';
import CRMRetail_Location from '@salesforce/label/c.CRMRetail_Location';
import CRMRetail_Save_Home_Location from '@salesforce/label/c.CRMRetail_Save_Home_Location';
import CRMRetail_Interaction_Date from '@salesforce/label/c.CRMRetail_Interaction_Date';
import CRMRetail_Restore_Default from '@salesforce/label/c.CRMRetail_Restore_Default';
import Valid_Format_For_Calendar_Date from '@salesforce/label/c.Valid_Format_For_Calendar_Date';
import { ShowToastEvent } from 'lightning/platformShowToastEvent';
import {publish, subscribe,APPLICATION_SCOPE,MessageContext} from 'lightning/messageService';
import CRMRetailMessageChannel from '@salesforce/messageChannel/CRMRetailMessageChannel__c';
import {labels} from 'c/crmretail_interactionutility_LWC_HUM';
var customLabels = labels;
export default class cRMRetail_locationsHome_LWC_HUM extends LightningElement {
    label = {CRMRetail_Interaction_Display_Criteria,
        CRMRetail_Attendance,
        CRMRetail_Location,
        CRMRetail_Save_Home_Location,
        CRMRetail_Interaction_Date,
        CRMRetail_Restore_Default,
        Valid_Format_For_Calendar_Date
      };
    options;
    selectedValue;
    Location = [];
    today;
    Name;
    displayDate = new Date();
    isReset = false;
    count;
    disablePlus = false;
    disableMinus = false;
    subscription = null;
    Switch_Attendance = false;
    showRestoreDefaults = false;
    disableLocAndDate = true;
    @api source;
    
    @wire(MessageContext)
    messageContext;

    connectedCallback(){
        this.getSwitchResults();
        this.Initiate();
    }

    getSwitchResults(){
        fetchSwitch()
        .then(result => {
            if(result){
                if(result.Switch_4438175){
                    this.showRestoreDefaults = true;
                    this.disableLocAndDate = false;
                }
            }
        })
        .catch(error => {
            this.generateErrorMessage(customLabels.ERROR, customLabels.UNEXPECTED_ERROR);
        });
    }
    Initiate(){
        loadLocationsandInteractionDate()
        .then(result =>{
            var todayDate = result.currIntDate;
            this.today = todayDate;
            if(result['currAtt'] && !this.source){
                this.Switch_Attendance = true;
                if(result.currAtt != 'future'){
                    if(result.currAtt == '' || result.currAtt == '0'){
                        this.count = '';
                        this.disableMinus = true;
                    }
                    else{
                        this.count = result.currAtt;
                        this.disableMinus = false;
                    }
                }
                else{
                    this.count = '';
                    this.disablePlus = true;
                    this.disableMinus = true;
                }
                this.subscribeToMessageChannel();
            }
            var listOfLocations = JSON.parse(result.listoflocations);
            this.Name = result.location; 
            var options = [];
            options.push({label: 'None', value: 'None'});
            listOfLocations.forEach(key=>{
                    var isSelected = false;
                    if(this.Name === key) {
                        isSelected = true;
                    }
                options.push({label: key, value: key, selected: isSelected});
            });
            this.Location = options;   
         })
         .catch(error => {
             this.generateErrorMessage(CRMRetail_CacheSetupError , this.processApexErrorMessage(error));
          });
    }
    onChange(event) {
        var selectedValue = event.target.value;
        var displayDate =  this.today;
        var payload = {SelectedLocation : event.target.value};
        publish(this.messageContext,CRMRetailMessageChannel,payload);
        getorSetLocationInCache({sNewLoc : selectedValue, isRestoreDefault : false})
        .then(result=>{
            this.Name = selectedValue;
            this.displayDate = displayDate;
            if(result != null){
                this.adjustCount(result);
            }
            if (this.source)
            {
                this.dispatchEvent(new CustomEvent('locdatechange'));
            }
        })
        .catch(error=>{
            this.generateErrorMessage(CRMRetail_LocationCacheUpdateError , this.processApexErrorMessage(error));
        }) 
    }

    onDateChange(event){
        const isInputsCorrect = [...this.template.querySelectorAll('lightning-input')]
        .reduce((validSoFar, inputField) => {
            inputField.reportValidity();
            return validSoFar && inputField.checkValidity();
        }, true);
        if(isInputsCorrect){
            var payload = {selectedInteractionDate : event.target.value};
            publish(this.messageContext,CRMRetailMessageChannel,payload);
            getOrSetInteractionDateInCache({dateValue: event.target.value, isReset: this.isReset})
            .then(result =>{
                if(result != null){
                    this.adjustCount(result);
                }
                if (this.source)
                {
                    this.dispatchEvent(new CustomEvent('locdatechange'));
                }
            })
            .catch(error =>{
                this.generateErrorMessage(CRMRetail_IntDateCacheUpdateError , this.processApexErrorMessage(error));
            }) 
        }
    }

    restoreLocation() {
        restoreLocationandDate()
        .then(result=>{
            if(result){
                this.Name = result['defaultLocation'] ? result['defaultLocation'] : '';
                this.template.querySelectorAll("lightning-input").forEach(element => {
                    if (element.label === "Interaction Date") {
                      element.value = result['defaultIntDate'] ? result['defaultIntDate'] : '';
                    }
                });
                Promise.resolve().then(() => {
                    const inputEle = this.template.querySelector('[data-id="EndDateField"]');
                    inputEle.reportValidity();
                });
                var restorePayload = {SelectedLocation : this.Name,selectedInteractionDate : result['defaultIntDate']};
                publish(this.messageContext,CRMRetailMessageChannel,restorePayload);
                if (this.source)
                {
                    this.dispatchEvent(new CustomEvent('locdatechange'));
                }
            }
        })
        .catch(error => {
            this.error = error;
            });
        if(this.Switch_Attendance){
            this.handleEventTraverse();
        }
    }
    
    saveHomeLocation() {
        var newLocation = this.Name;
        if(newLocation != 'None'){
            saveDefaultLocationRecord({newLocationName: newLocation })
            .then(result =>{
                var toastEvent = new ShowToastEvent({
                    title: "Success!",
                    message: "Default Location saved as: " + newLocation,
                    variant: 'success'
                })
                    this.dispatchEvent(toastEvent);
            })
            .catch(error=>{
                this.generateErrorMessage(CRMRetail_SaveHomeLocationError , this.processApexErrorMessage(error)); 
            })
                
        }
        else {
            this.generateErrorMessage(CRMRetail_LocationError , CRMRetail_SelectHomeLocation);       
        }
    }

    generateErrorMessage(errTitle, errMessage) {
        var toastEvent = new ShowToastEvent({
            title: errTitle,
            message: errMessage,
            variant: 'error',
            mode: 'sticky'
        });
        this.dispatchEvent(toastEvent);
    }

    processApexErrorMessage(response) {
        var errors = response;
        var errMsg = '';
        if (errors) {
            if (errors[0] && errors[0].message) {
                errMsg = errors[0].message;
            }
        } else {
            errMsg = "Unknown error";
        }
        return errMsg;
    }

    calcCount(event) {
        changeAttendanceCount({
            dateValue: this.today,
            func: event.target.title
        })
        .then(result => {
            if(result == '0'){
                this.count = '';
                this.disableMinus = true;                    
            }
            else{
                this.count = result;
                this.disableMinus = false;                    
            }
        })
        .catch(error =>{
            this.error = error
        });
    }

    subscribeToMessageChannel() {
        if (!this.subscription) {
          this.subscription =
            subscribe(
              this.messageContext,
              CRMRetailMessageChannel,
              message => {
                this.handleEventTraverse();
                
              },
              { scope: APPLICATION_SCOPE }
            );
        }
        
      }
        
    handleEventTraverse(){
        deduceAttendance()
        .then(result => {
            if(result == '0' || result == 'future'){
                this.count ='';
                this.disableMinus = true;                    
            }
            else{
                this.count = result;
                this.disableMinus = false;                    
            }
        })
        .catch(error => {
            this.error = error;
            });
    }

    adjustCount(currentAttendance){
        if(currentAttendance != 'future'){
            if(currentAttendance == '' || currentAttendance == '0'){
                this.count = '';
                this.disableMinus = true;
            }
            else{
                this.count = currentAttendance;
                this.disableMinus = false;
                this.disablePlus = false;
            }
        }
        else{
            this.count = '';
            this.disablePlus = true;
            this.disableMinus = true;
        }
    }

    handleCSS(){
        if(this.source === true){
            var elem2 = this.template.querySelector('[data-id="selectType"]');
            var elem3 = this.template.querySelector('[data-id="EndDateField"]');
            elem2.classList.remove("comboBox");
            elem2.classList.add("comboBoxInteraction");
            elem3.classList.remove("endDateDiv");
            elem3.classList.add("endDateDivInteraction");
            
        }
    }

    renderedCallback(){
        this.handleCSS();
    }
}