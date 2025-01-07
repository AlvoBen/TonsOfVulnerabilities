/*******************************************************************************************************************************
LWC JS Name : DisplayFieldsInColumnHum.js
Function    : This JS serves as controller to DisplayFieldsInColumnHum

Modification Log: 
Developer Name           Code Review                      Date                         Description
*--------------------------------------------------------------------------------------------------------------------------------
Sagar Gulleve                                         14/07/2022                    User story 3393658 Contact Information Tab.
Vishal Shinde                                         11/7/2022                     User story 3277055 CRM Service Benefits 2022- Auth/Referral: Lightning Build for Summary & Details - Logging Auth Details
Vishal Shinde                                         11/25/2022                    DF-6708 - At time when we click on a field value to log, they are not getting logged on first instance. 
*********************************************************************************************************************************/
import { LightningElement,api,track,wire } from 'lwc';
import { MessageContext } from 'lightning/messageService';
import customcss from '@salesforce/resourceUrl/LightningCRMAssets_SR_HUM';
import { loadStyle } from 'lightning/platformResourceLoader';
import { performLogging,setEventListener,checkloggingstatus,clearLoggedValues,getLoggingKey} from 'c/loggingUtilityHum';
import { CurrentPageReference } from 'lightning/navigation';

export default class DisplayFieldsInColumnHum extends LightningElement {
    @api sectionHeader;
    @api sectionData;
    columnSize;
	@track tmpAuthReferralNumber1;
    @track loggingkey;
    showloggingicon = true;
    autoLogging = true;
    @track startLogging = false
    donotlog=false;
    gridsize;
	
    @api
    get columnSize() {
        return this.columnSize;
    }
	
	@wire(MessageContext)
    messageContext;

    @wire(CurrentPageReference)
    pageRef;

    renderedCallback() {
        Promise.all([
            loadStyle(this, customcss + '/CRM_Assets/styles/logging.css')
        ]).catch((error) => {});
    }
	
	connectedCallback(){
        try {
            
            this.tempMemName = this.pageRef.attributes.url.split('?');
            this.tmpAuthReferralNumber = this.tempMemName[1].split('=');
            
            this.tmpAuthReferralNumber = this.tmpAuthReferralNumber[1].split('&');
            this.tmpAuthReferralNumber1 = this.tmpAuthReferralNumber[0];
            
 if(this.autoLogging){
                getLoggingKey(this.pageRef).then(result =>{
                    this.loggingkey = result;
                    console.log('LOGGING KEYY--->'+JSON.stringify(this.loggingkey));
                });
            }

        } catch (e) {
            console.log('Error: ' + e.error);
        }
    }
	
	handleLogging(event) {
        if(this.sectionHeader=='Contact Information'){
        if (this.loggingkey && checkloggingstatus(this.loggingkey)) {
            performLogging(
                event,
                this.createRelatedField(),
                'Information/Contact Information',
                this.loggingkey,
                this.pageRef
            );
        } else {
            getLoggingKey(this.pageRef).then((result) => {
                this.loggingkey = result;
                if (this.loggingkey && checkloggingstatus(this.loggingkey)) {
                    performLogging(
                        event,
                        this.createRelatedField(),
                        'Information/Contact Information',
                        this.loggingkey,
                        this.pageRef
                    );
                }
            });
        }
    }
    if(this.sectionHeader!='Contact Information'){
        if (this.loggingkey && checkloggingstatus(this.loggingkey)) {
            performLogging(
                event,
                this.createRelatedField(),
                'Information/Authorization/Referral Information',
                this.loggingkey,
                this.pageRef
            );
        } else {
            getLoggingKey(this.pageRef).then((result) => {
                this.loggingkey = result;
                if (this.loggingkey && checkloggingstatus(this.loggingkey)) {
                    performLogging(
                        event,
                        this.createRelatedField(),
                        'Information/Authorization/Referral Information',
                        this.loggingkey,
                        this.pageRef
                    );
                }
            });
        }
    }
}
	
	createRelatedField() {
        return [
            {
                label: 'Authorization Referral #',
                value: this.tmpAuthReferralNumber1
            }
        ]
    }
    set columnSize(columnSize) {
        if (columnSize) {
            this.gridsize =
                'slds-col slds-grow slds-p-horizontal_small slds-size_1-of-' +
                columnSize;
        } else {
            this.gridsize = 'slds-col slds-p-horizontal_small';
        }
    }
}