/*******************************************************************************************************************************
LWC JS Name : AccountDetailTranscriptsHum.js
Function    : This JS serves as controller to AccountDetailTranscriptsHum.html. 
Modification Log: 
Developer Name           Code Review                      Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Joel George                                       	 02/09/2021                  Initial version
* Joel George                                       	 03/05/2021                  Added logic to navigate to chat detail
* Ashish Kumar                                           09/20/2021                  Refactored the code realted to Time Date zone
* Ritik Agarwal                                          10/28/2021                    US-2440592
* Ashish Kumar                                          10/27/2021                    US-2607545
* Abhishek Mangutkar									 08/09/2022                   DF - 5542
* Deepak khandelwal                                      09/27/2023                   US_5050647  chat transcript view all button  
* Tharun Madishetti										 10/06/2023					  Us_5050647: Added Switch functionality
*********************************************************************************************************************************/
import { LightningElement, track, api } from 'lwc';
import getTransMethod from '@salesforce/apex/AccountDetailTranscripts_LC_HUM.getTranscripts';
import { getLabels } from 'c/customLabelsHum';
import { getCustomSettingValue } from 'c/genericCustomSettingValueHum';


export default class AccountDetailTranscriptsHum extends LightningElement {
    @api recordId;
    @api showviewAll;
    @track oTransLst;
    @track oError;
    @track sTransTitle;
    @track labels = getLabels();
	@track loggingRelatedField = [{
        label : "Chat Transcript Name",
        mappingField : "Name"
    }];
    @track screeName = 'Chat Transcripts';
	@track oViewAllParams = {};
	@track bSwitch5050647 = false;
    connectedCallback() {
       this.processChatTranscript();
    }
	
	 processChatTranscript() 
	 {
		getTransMethod({ sAccId: this.recordId }).then(data => {
            if (data) {
                this.oTransLst = data;
                this.oError = undefined;
                this.sTransTitle = this.labels.HUM_LiveChatTranscript + ' (' + data.length + ')';
            }
        }).catch(error => {
            if (error) {
                console.log('>error>', error);
                this.oError = error;
                this.oLinks = undefined;
            }
        });
		const switchVal = getCustomSettingValue('Switch', '5050647');			
		switchVal.then(result => {
			if (result && result?.IsON__c) this.bSwitch5050647 = result?.IsON__c;
			if(this.bSwitch5050647){
				this.showviewAll=(this.showviewAll==undefined)?true:false;
				this.oViewAllParams = {
					sRecordTypeName : 'Transcripts',
					sRecordId : this.recordId
				} 
			}
        });
        
     }
}