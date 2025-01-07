/*******************************************************************************************************************************
LWC JS Name : caseContainerComponentHum.js
Function    : LWC to display alerts on Case detail page.

Modification Log:
Developer Name           Code Review                      Date                         Description
*--------------------------------------------------------------------------------------------------------------------------------
* Divya Bhamre                                            02/03/2023                    US-4207055-T1PRJ0865978-  MF 23786 Lightning- NBA- Implementation & Testing of Alerts - Case detail
* Swapnali Sonawane                                       02/15/2023                    US-4206972 and US-4207044 adding Alerts on Edit and New case Page.
*********************************************************************************************************************************/

import { api,LightningElement, track ,wire} from 'lwc';
import { getRecord } from "lightning/uiRecordApi";
import CASE_ACOUNT_ID from '@salesforce/schema/Case.AccountId';



export default class CaseContainerComponentHum extends LightningElement {
    
    @api recid;
    @api pageName;
    @api recordId;
    @track showAlert;
    @track accountId;
    @track accountPageName;
    @track caseId;

  
    
    @wire(getRecord, {
        recordId: '$caseId',
         fields: [CASE_ACOUNT_ID]
      })
      wiredAccount({ error, data }) {
        
        if (data) {
            this.accountId = data.fields.AccountId.value;
            this.showAlert = true;
           } else if (error) {
            
        }
    }
    
connectedCallback(){
        if (this.recordId && this.pageName=='Case Details'){
            this.caseId = this.recordId;
            this.accountPageName = 'Case';          
        }else if(this.recid && this.pageName =='New Case')
        {
            this.accountId = this.recid;
            this.accountPageName ='Case'; 
            this.showAlert = true;
        }else if (this.recid && this.pageName =='Case Edit')
        {
            this.accountId = this.recid;
            this.accountPageName ='Case Edit'; 
            this.showAlert = true;
        }
        
    }
    
}