/*******************************************************************************************************************************
LWC JS Name : coachingMemberAssociatedForms.js
Function    : This JS serves as controller to coachingMemberAssociatedForms.html. 

Modification Log: 
Developer Name           Code Review                      Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Prudhvi Pamarthi                                        05/22/2021                 Initial version
*********************************************************************************************************************************/
import { LightningElement,track,api,wire } from 'lwc';
import { getRecord } from 'lightning/uiRecordApi';
export default class CoachingMemberAssociatedForms extends LightningElement {
    @api recordId;
    @track recordType;     
    @track oUserGroup;
    @track bShowAssociatedForm = false;
    @track bShowPOA = false;

    @wire(getRecord, { recordId: '$recordId', fields: ['Account.Name'] })
    wiredAccount({ error, data }) {
      if (data) {
            this.recordType = data.recordTypeInfo.name;
            let oUserGroup = {};            
            oUserGroup.bRcc = false;        
            oUserGroup.bProvider = false;
            oUserGroup.bGbo = false;
            oUserGroup.bPharmacy = false;
            oUserGroup.bGeneral = true;  
            this.oUserGroup = oUserGroup;
            this.bShowAssociatedForm = true;
            this.bShowPOA = true;
      }
      else if (error) {
         console.log('Error Occured', error);
      }
   }

   handleCustomEvent(event) {
    this.vPOAData = event.detail;
    this.template.querySelector('c-coaching-p-o-a-hum').processResponseFromAssociatedUser(event.detail);
 }
}