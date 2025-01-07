import { LightningElement,api, track } from 'lwc';
import { ShowToastEvent } from 'lightning/platformShowToastEvent';
import { NavigationMixin } from 'lightning/navigation';
import deleteSdoHRecords from '@salesforce/apex/CRMRetail_OOO_Tracking_H_HUM.deleteSDoHBeforeOOOTrackingDelete';

import ERROR_TEXT from '@salesforce/label/c.CRMRetail_Error_HUM';
import SUCCESS_TEXT from '@salesforce/label/c.CRM_Retail_Waiver_Functionality_Expiration_Success';
import CLOSE_TEXT from '@salesforce/label/c.CRM_Retail_Close_Message';
import RECORD_SYNC_ERROR from '@salesforce/label/c.CRMRetail_Sync_Error_Message';
import RECORD_DELETE_ERROR from '@salesforce/label/c.CRM_Retail_Delete_Error_Message';
import RECORD_DELETE_SUCCESS from '@salesforce/label/c.CRMRetail_OOO_Delete_Success';

export default class CRMRetail_OOO_TrackingDel_LWC_HUM extends NavigationMixin(LightningElement) {
    @api oooRecordId;
    @api objectApiName;
  
    @track error;
    closeDeleteModal(){
       const closeQA = new CustomEvent(CLOSE_TEXT);
        this.dispatchEvent(closeQA);
    }
    deleteOneOnOne(event) {
        deleteSdoHRecords({ oooRecordId : this.oooRecordId,
                            isOverrideButton:true
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
                this.dispatchEvent(
                    new ShowToastEvent({
                        title: SUCCESS_TEXT,
                        message: RECORD_DELETE_SUCCESS,
                        variant: 'success'
                    })
                );              
                this[NavigationMixin.Navigate]({
                    type: 'standard__recordRelationshipPage',
                        attributes: {
                            recordId: result,
                                objectApiName: 'Account',
                                    relationshipApiName: 'One_on_One_Trackings__r',
                                        actionName: 'view'
                                            }
                    });
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
             
    
}