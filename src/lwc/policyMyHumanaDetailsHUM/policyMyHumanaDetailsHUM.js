/* 
Name   : Controller for policyMyHumanaDetailHum.js

Modification Log:
* Developer Name                Date                       Description 
* Vardhman Jain                 09/02/2022                  US: 3043287 Member Plan Logging stories Changes.
*Deepak khandelwal                            06/16/2023                    US_4525570_T1PRJ0865978 - MF25836 - C08, Consumer/CRM Lightning to pass full gen key number needed for web emulation (Lightning)
*************************************************************************************************************************/
import { LightningElement, track, api, wire } from 'lwc';
import getWebInformation from '@salesforce/apexContinuation/MemberMbeService_LC_HUM.getWebInformation';
import getUserDetails from '@salesforce/apex/GetCurrentUserDetails.getUserDetails';
import { getUserGroup, getLabels,copyToClipBoard } from 'c/crmUtilityHum';
import { getRecord} from 'lightning/uiRecordApi';
import MEMBER_NAME from '@salesforce/schema/MemberPlan.Name';
import { CurrentPageReference } from 'lightning/navigation';
import {performLogging,getLoggingKey,checkloggingstatus} from 'c/loggingUtilityHum';
import HUM_Copy_Clipboard from '@salesforce/label/c.HUM_Copy_Clipboard'; 
import CRMS_685_PCC_Customer_Service_Access from '@salesforce/customPermission/CRMS_685_PCC_Customer_Service_Access'; 
import MemberGenKeyHumLebal from '@salesforce/label/c.MemberGenKey_Hum';

export default class PolicyMyHumanaDetailsHUM extends LightningElement {
    @track response = {};
    @track labels = getLabels();
    @api recordId;
    showDetails = false;
	@track memberPlanName;
	
	@track copyClipBoardMsg = HUM_Copy_Clipboard;  
    @track copyvar =true ; 
    @track showMemGenKey =false ; 
    @track memberGenKeyvalue; 
    @track memberGenKeyLebal= MemberGenKeyHumLebal;
    get has685permission (){
        return CRMS_685_PCC_Customer_Service_Access ;
    }
	
	@wire(getRecord, {
        recordId: '$recordId',
        fields: [MEMBER_NAME]
      }) wireuser({
        error,
        data
      }) {
        if (error) {
          this.error = error;
		  console.log('error in wire method',error);
        } else if (data) {
         this.memberPlanName = data.fields.Name.value;
        }
      }

      @wire(CurrentPageReference)
      wiredPageRef(pageRef) {
          this.pageRef = pageRef;
      }
    connectedCallback() {
        this.handleProfileName();
    }

    /**
     * Params - {result i.e., data comes from pubsub which is fire from policyHighlightpanelHum}
     * Description - this method will fetch profile name from policyhighlight panel and check the visibility
     *                of card based on combination of permission set and profile name
     */
    handleProfileName() {
        getUserDetails({}).then(res => {
            const { bPharmacy, bRcc, bGbo, bGeneral } = getUserGroup();
            if (bPharmacy || bRcc || bGbo || ((bGeneral && res.Profile.Name === 'Humana Pharmacy Specialist') || (bGeneral && res.Profile.Name === 'Customer Care Specialist') || (bGeneral && res.Profile.Name === 'Customer Care Supervisor'))) {
                this.showDetails = true;
                this.fetchMyHumanaDetails(this.recordId);
            }
        }).catch(error => {
            console.log('error in profile of myHumanacard--', error);
        });
    }

    /*
     * Params - {Memberplan id}
     * Description - this method is called on load of page and will fetch myhumana section details
                     and render it on UI.
     */
    async fetchMyHumanaDetails(sRecordId) {
        try {
            const result = await getWebInformation({ sPolicyMemID: sRecordId });
            if (result !== null) {
                if (result.hasOwnProperty('IsWebRegistered') && result.IsWebRegistered === 'true') {
                    result.showEmulateLink = true;
                    result.IsWebRegistered = 'Yes' 
                }else{
                    result.showEmulateLink = false;
                    result.IsWebRegistered = 'No'
                }
                result.LastLoginDateTime = (result.hasOwnProperty('LastLoginDateTime') && result.LastLoginDateTime != null && result.LastLoginDateTime != '' && result.LastLoginDateTime.length >= 10) ? result.LastLoginDateTime.substr(0, result.LastLoginDateTime.indexOf(' ')) : '';

                this.response = result;
				
				                if(this.response.IsWebRegistered === 'Yes' ){
                    getUserDetails({}).then(res => {
                                if(this.has685permission ){
                                    this.showMemGenKey =false;
                                     }else{
                                    this.showMemGenKey =true ;
                                     }
                    }).catch(error => {
                        console.log('error in profile of myHumanacard--', error);
                    });

                }else{
                    this.showMemGenKey =false ;
                }               
                if(this.response.MemberGenKey !=null){
                this.memberGenKeyvalue =this.response.MemberGenKey.padStart(13, '0');
                }
            }
        }
        catch (error) {
            console.log('error in myHumanaDetails--', error);
        }
    }

    /*
    * Description - this method is used to open the clicked link in new tab.
    */
    openLink(event) {
        const linkToOpen = event.currentTarget.dataset.link;
                linkToOpen === 'humana' ? window.open(this.response.humanaLink) : window.open(this.response.webEmulateLink+'?MemberGenKey=' +this.memberGenKeyvalue); 
    }
	// logging changes
	 handleLogging(event) {
	try{
		if(this.loggingkey && checkloggingstatus(this.loggingkey)){
            performLogging(event,this.createRelatedField(),'MyHumana Details',this.loggingkey,this.pageRef);
        }else{
            getLoggingKey(this.pageRef).then(result =>{
                this.loggingkey = result;
                if(this.loggingkey && checkloggingstatus(this.loggingkey)){
                    performLogging(event,this.createRelatedField(),'MyHumana Details',this.loggingkey,this.pageRef);
                }
            })
			}
		}
		 catch (error) {
        console.log('error in memberplan logging-', error);
		}
    }

    createRelatedField(){
        return [{
            label : 'Member Id',
            value : this.memberPlanName
        }];
    }

	    copyToBoard(event){
        let copiedVal;
        copiedVal =  event.currentTarget.dataset.value;
        console.log('onclick of copy',copiedVal);
        copyToClipBoard(copiedVal);          
    }
}