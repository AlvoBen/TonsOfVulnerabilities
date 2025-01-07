/*
*******************************************************************************************************************************
File Name        : crmRetail_MobileScanner_LWC_HUM.js
Version          : 1.0 
Created Date     : 01/12/2023 
Function         : Lightning web component used for the Mobile Scan Card functionality.
Modification Log :
* Developer                 Code review         Date                  Description
*******************************************************************************************************************************
* Mohamed Thameem      	                	    01/12/2023            Original Version : Request 3866581 - Mobile Scanner
* Mohamed Thameem      	                	    03/01/2023            Request 4231928 - Mobile Scanner Phase 2
* Vinoth L                                      10/03/2023            User Story 5132659 Ability to Edit Mobile Location Field
*/
import { LightningElement} from 'lwc';
import getorSetLocationInCache from '@salesforce/apex/CRMRetail_HomePage_LC_HUM.getorSetLocationInCache';
import updateMobileCache from '@salesforce/apex/CRMRetail_HomePage_LC_HUM.updateMobileCache'
import CRMRetail_CheckInSuccess_Text from '@salesforce/label/c.CRMRetail_CheckInSuccess_Text';
import CRMRetail_DuplicateInteraction_Key from '@salesforce/label/c.CRMRetail_DuplicateInteraction_Key';
import CRMRetail_MaxReached_Key from '@salesforce/label/c.CRMRetail_MaxReached_Key';
import { ShowToastEvent } from 'lightning/platformShowToastEvent';
import CRMRetail_CheckInSuccessMsg from '@salesforce/label/c.CRMRetail_CheckInSuccessMsg';
import CRM_Retail_Waiver_Functionality_Expiration_Success from '@salesforce/label/c.CRM_Retail_Waiver_Functionality_Expiration_Success';
import CRMRetail_Success_Variant from '@salesforce/label/c.CRMRetail_Success_Variant';
import CRMRetail_Location_Occupancy_Title from '@salesforce/label/c.CRMRetail_Location_Occupancy_Title';
import CRMRetail_Duplicate_Interaction from '@salesforce/label/c.CRMRetail_Duplicate_Interaction';
import acknowledgeNotifications from '@salesforce/apex/CRMRetail_HomePage_LC_HUM.acknowledgeNotifications';
import CRM_Retail_Waiver_Success_Text from '@salesforce/label/c.CRM_Retail_Waiver_Success_Text';
import CRM_Retail_Waiver_Failure_Text from '@salesforce/label/c.CRM_Retail_Waiver_Failure_Text';
import CRMRetail_Error_Label from '@salesforce/label/c.CRMRetail_Error_Label';
import CRMRetail_Error_variant from '@salesforce/label/c.CRMRetail_Error_variant';
import CRM_Retail_Signed_Error_text from '@salesforce/label/c.CRM_Retail_Signed_Error_text';
import FORM_FACTOR from '@salesforce/client/formFactor';
import TIME_ZONE from '@salesforce/i18n/timeZone';
import CRMRetail_Interaction_Display_Criteria from '@salesforce/label/c.CRMRetail_Interaction_Display_Criteria';
import CRMRetail_Interaction_Date from '@salesforce/label/c.CRMRetail_Interaction_Date';
import CRMRetail_Scan_Card_Button from '@salesforce/label/c.CRMRetail_Scan_Card_Button';
import CRMRetail_Location from '@salesforce/label/c.CRMRetail_Location';
import CRMRetail_Checkin_Error_Message from '@salesforce/label/c.CRMRetail_Checkin_Error_Message';
import CRMRetail_Scanner_Mobile_Section_Title from '@salesforce/label/c.CRMRetail_Scanner_Mobile_Section_Title';
import CRMRetail_DupIntList_Key from '@salesforce/label/c.CRMRetail_DupIntList_Key';
import CRMRetail_NotificationList_Key from '@salesforce/label/c.CRMRetail_NotificationList_Key';
import CRMRetail_Save_Home_Location from '@salesforce/label/c.CRMRetail_Save_Home_Location';
import CRMRetail_LocationError from '@salesforce/label/c.CRMRetail_LocationError';
import CRMRetail_SelectHomeLocation from '@salesforce/label/c.CRMRetail_SelectHomeLocation';
import CRMRetail_SaveHomeLocationError from '@salesforce/label/c.CRMRetail_SaveHomeLocationError';
import CRMRetail_CacheSetupError from '@salesforce/label/c.CRMRetail_CacheSetupError';
import CRMREtail_HomeLocation_Error from '@salesforce/label/c.CRMREtail_HomeLocation_Error';
import CRMRetail_LocationCacheUpdateError from '@salesforce/label/c.CRMRetail_LocationCacheUpdateError';
import cancelButtonLabel from '@salesforce/label/c.CRMRetail_Cancel_ButtonLabel';

import fetchAccountDetails from '@salesforce/apex/CRMRetail_HomePage_LC_HUM.fetchAccountDetails';
import saveDefaultLocationRecord from '@salesforce/apex/CRMRetail_HomePage_LC_HUM.saveDefaultLocationRecord';
import loadLocationsandInteractionDate from '@salesforce/apex/CRMRetail_HomePage_LC_HUM.loadLocationsandInteractionDate';
import fetchSwitch from '@salesforce/apex/CRMRetail_HomePage_LC_HUM.fetchSwitchResults';
export default class CrmRetail_MobileScanner_LWC_HUM extends LightningElement {
    label = {
        CRMRetail_Interaction_Display_Criteria : CRMRetail_Interaction_Display_Criteria,
        cRMRetail_Location : CRMRetail_Location,
        CRMRetail_Interaction_Date : CRMRetail_Interaction_Date,
        CRMRetail_Scan_Card_Button : CRMRetail_Scan_Card_Button,
        CRMRetail_Save_Home_Location : CRMRetail_Save_Home_Location,
        CRMRetail_Scanner_Mobile_Section_Title : CRMRetail_Scanner_Mobile_Section_Title,
        CRMRetail_SaveHomeLocationError : CRMRetail_SaveHomeLocationError,
        CRMRetail_SelectHomeLocation : CRMRetail_SelectHomeLocation,
        CRMRetail_LocationError : CRMRetail_LocationError,
        CRMRetail_CacheSetupError : CRMRetail_CacheSetupError
    }
    defaultLocation;
    isModalActive;
    pageName = 'Home';
    locationList = [];
    notificationData;
    isNotificationModalActive = false;
    interactionDate;
    isSpinnerVisible=false;
    genericModalHeader;
    duplicateIntList;
    isDuplicateModalOpen;
    disableLocation=false;
    switchMap;
    enableHomeLocation=true;
    cancelButtonLabel = cancelButtonLabel;

    connectedCallback() {
        fetchSwitch()
        .then(result => {
            if(result){
                this.switchMap = result;
                if(this.switchMap && this.switchMap.hasOwnProperty('Switch_5132659') && !this.switchMap['Switch_5132659']){
                    this.enableHomeLocation = false;
                    this.disableLocation = true;
                }                   
            }
        });
        if(sessionStorage.getItem(CRMRetail_DupIntList_Key) != null && sessionStorage.getItem(CRMRetail_DupIntList_Key))
        {
            var duplicateListFromSession = sessionStorage.getItem(CRMRetail_DupIntList_Key);
            sessionStorage.clear();
            this.genericModalHeader = CRMRetail_Duplicate_Interaction;
            this.duplicateIntList.push(duplicateListFromSession);
            this.isDuplicateModalOpen=true;
        }
        if(sessionStorage.getItem(CRMRetail_NotificationList_Key) != null && sessionStorage.getItem(CRMRetail_NotificationList_Key)){            
            var notificationListFromSession = JSON.parse(sessionStorage.getItem(CRMRetail_NotificationList_Key));
            sessionStorage.clear(); 
            var accId = notificationListFromSession.accountRec?notificationListFromSession.accountRec.Id : '';
            fetchAccountDetails({accountId:accId})
            .then((result)=>{
                notificationListFromSession.visitorId = result.GCM_Visitor_Barcode__c ? result.GCM_Visitor_Barcode__c : '';
                notificationListFromSession.accountRec.Name=result.Name ? result.Name : '';
                notificationListFromSession.accountRec.Enterprise_ID__c=result.Enterprise_ID__c ? result.Enterprise_ID__c : null;
                this.notificationData = notificationListFromSession;
                this.isNotificationModalActive = true;
            })
            .catch((error)=>{
                this.showToastMessage(CRMRetail_Checkin_Error_Message,CRMRetail_Error_Label,CRMRetail_Error_variant);
            });                      
        }
        
        updateMobileCache({device:FORM_FACTOR})
        .catch(error=>{
            this.showToastMessage(CRMRetail_CheckInSuccessMsg, CRMRetail_Checkin_Error_Message, CRMRetail_Success_Variant);
        })

        let today = new Date(new Date().toLocaleString("en-US", {timeZone:TIME_ZONE}));
        this.interactionDate = today.toLocaleDateString();     
        
        loadLocationsandInteractionDate()
        .then(result =>{
            var listoflocations = JSON.parse(result.listoflocations);
            var options = [];
            options.push({label: 'None', value: 'None'});

            listoflocations.forEach(entry=>{
                options.push({label: entry, value: entry})
            })
            this.locationList = options;
            
            if(!this.disableLocation)
            {
                this.defaultLocation = result.location;
            }
            else
            {
                getorSetLocationInCache({sNewLoc:'',isRestoreDefault:true})
                .then(location=>{
                    this.defaultLocation = location;
                })
                .catch(error=>{
                    this.showToastMessage(CRMRetail_CheckInSuccessMsg, CRMRetail_Checkin_Error_Message, CRMRetail_Success_Variant);
                }); 
            }
         })
         .catch(error => {
             this.showToastMessage(this.processApexErrorMessage(error) ,CRMRetail_CacheSetupError , 'error');   
          });         
        
    }

    ackNotification(event) {
        this.isNotificationModalActive = false;
        var notificationData = event.detail.data;
        if (notificationData.ack) {
            if (notificationData.waiverDate) {
                this.isWaiverAcknowledged = true;
            }
            var lstOfRecToUpdate = [];
            lstOfRecToUpdate.push(notificationData);
            var lstOfAccountIds = [];
            lstOfAccountIds.push(notificationData.accId);
            var currLocation = notificationData.currentLocation;
        }
        else {
            this.notificationData = null;
            this.isNotificationModalActive = false;
        }
        if (lstOfRecToUpdate !== null && lstOfRecToUpdate !== undefined && lstOfRecToUpdate.length > 0) {
            this.isSpinnerVisible = true;
            acknowledgeNotifications({ inputJSON: JSON.stringify(lstOfRecToUpdate), accIds: lstOfAccountIds, currentLocation: currLocation })
                .then((data) => {
                    this.isSpinnerVisible = false;
                    if (this.isWaiverAcknowledged) {
                        this.showToastMessage(CRM_Retail_Waiver_Success_Text, CRM_Retail_Waiver_Functionality_Expiration_Success, CRMRetail_Success_Variant);
                    }
                    if (!data) {
                        this.showToastMessage(CRM_Retail_Waiver_Failure_Text, CRMRetail_Error_Label, CRMRetail_Error_variant);
                    }
                })
                .catch((error) => {
                    this.isSpinnerVisible = false;
                    this.showToastMessage(CRM_Retail_Signed_Error_text, CRMRetail_Error_Label, CRMRetail_Error_variant);
                });
        }
    }

    handleScanComplete(event) {
        this.scannedCode = event.detail;
    }

    handleLoading(event)
    {
        this.isSpinnerVisible = event.detail.isloading;
    }

    handleinactivememModalClose(){        
        this.isModalActive=false;
    }

    handleScanCardEvt(event) {
        var origin = event.detail.eventOrigin;
        this.notificationData = {};
        switch (origin) {
            case CRMRetail_CheckInSuccess_Text:
                this.showToastMessage(CRMRetail_CheckInSuccessMsg, CRM_Retail_Waiver_Functionality_Expiration_Success, CRMRetail_Success_Variant);
                this.notificationData = event.detail.notificationData != '[]' ? event.detail.notificationData : {};
                this.isNotificationModalActive = true;
                this.isModalActive = false;
                break;
            case CRMRetail_DuplicateInteraction_Key:
                this.genericModalHeader = CRMRetail_Duplicate_Interaction;
                this.duplicateIntList = event.detail.DupIntList;
                this.isDuplicateModalOpen = true;
                this.isNotificationModalActive = false;
                this.isModalActive = false;
                break;
            case CRMRetail_MaxReached_Key:
                this.genericModalHeader = CRMRetail_Location_Occupancy_Title;
                this.duplicateIntList = event.detail.expiredList;
                this.isDuplicateModalOpen = true;
                this.isNotificationModalActive = false;
                this.isModalActive = false;
                break;
        }
    }

    enableScancard(event) {
        this.isModalActive = true;
    }

    handleLocationChange(event)
    {
        this.defaultLocation = event.target.value;

        if(this.defaultLocation == 'None'){
            this.showToastMessage(CRMREtail_HomeLocation_Error,CRMRetail_LocationError,CRMRetail_Error_variant);
        }

        getorSetLocationInCache({sNewLoc : this.defaultLocation, isRestoreDefault : false})
        .then(result=>{})
        .catch(error=>{
            this.showToastMessage(this.processApexErrorMessage(error), CRMRetail_LocationCacheUpdateError , CRMRetail_Error_variant);
        }) 
        

    }

    saveHomeLocation() {
        if(this.defaultLocation != 'None'){
            var loc = this.defaultLocation;
            saveDefaultLocationRecord({newLocationName: loc })
            .then(result =>{
                this.showToastMessage("Default Location saved as: " + this.defaultLocation, "Success!", "success");                
            })
            .catch(error=>{
                this.showToastMessage(this.processApexErrorMessage(error), CRMRetail_SaveHomeLocationError , 'error'); 
            })
                
        }
        else {
            this.showToastMessage(CRMRetail_SelectHomeLocation ,CRMRetail_LocationError , 'error');       
        }
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

    handleduplicatemodalclosedevt() {
        this.isModalActive = false;
        this.isDuplicateModalOpen = false;
        this.genericModalHeader = '';
        this.duplicateIntList = [];
    }

    handleScanCardModalClose() {
        this.isModalActive = false;
    }

    showToastMessage(message, title, variant) {
        var mode = (variant == 'success' || variant == 'warning') ? 'dismissable' : 'sticky';
        const evt = new ShowToastEvent({
            title: title,
            message: message,
            variant: variant,
            mode: mode
        });
        this.dispatchEvent(evt);
    }
}