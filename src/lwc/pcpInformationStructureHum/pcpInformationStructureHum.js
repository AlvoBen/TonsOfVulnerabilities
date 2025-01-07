/*
Modification Log: 
* Developer Name                       	Date                        Description
* Jonathan Dickinson                   	04/04/2022                  US-3196414
* Abhishek Mangutkar					05/13/2022					US-3312839
* Abhishek Mangutkar					06/15/2022					DF-5124
* Divya Bhamre                          11/02/2022                  US - 3833519
* Aishwarya Pawar                       05/19/2023                  US - 4516245
*------------------------------------------------------------------------------------------------------------------------------
--*/
import { LightningElement, api, wire, track } from 'lwc';
import { ShowToastEvent } from 'lightning/platformShowToastEvent';
import {performLogging,getLoggingKey,checkloggingstatus} from 'c/loggingUtilityHum';
import { CurrentPageReference } from 'lightning/navigation';
import loggingcss from '@salesforce/resourceUrl/LightningCRMAssets_SR_HUM';
import { loadStyle } from 'lightning/platformResourceLoader';

export default class PcpInformationStructureHum extends LightningElement {
    @api
    pcppcddata;
    @api
    retrievedAllPhoneNumbersInParent;

    @api
    pcppcdtype;

    @api pcpsection;
    @api memberid;
	@api rowindex;

    loggingSubscription;
    startLogging = false;
    collectedLoggedData = [];
    @track loggingkey;
    pageRef;
    @track bpracticelocation;

    connectedCallback() {
        this.bpracticelocation = this.pcppcdtype === 'PCD' ? true : false;
    }
    get changeEndDateFormat(){
        if(this.pcppcddata.EndDate){
            return new Date(this.pcppcddata.EndDate).toLocaleDateString("en-US");
        }else{
            return '';
        }
    }
	
	get generateLogId(){
        return Math.random().toString(16).slice(2);
    }

    get changeEffDateFormat(){
        if(this.pcppcddata.EffectiveDate){
            return new Date(this.pcppcddata.EffectiveDate).toLocaleDateString("en-US");
        }else{
            return '';
        }
    }   

    

    @wire(CurrentPageReference)
    wiredPageRef(pageRef) {
        this.pageRef = pageRef;
    }

    handleLogging(event) {
        if(this.loggingkey && checkloggingstatus(this.loggingkey)){
            performLogging(event,this.createRelatedField(),`${this.pcpsection}-${this.pcppcdtype}`,this.loggingkey,this.pageRef);
        }else{
            getLoggingKey(this.pageRef).then(result =>{
                this.loggingkey = result;
                if(this.loggingkey && checkloggingstatus(this.loggingkey)){
                    performLogging(event,this.createRelatedField(),`${this.pcpsection}-${this.pcppcdtype}`,this.loggingkey,this.pageRef);
                }
            })
        }        
    }

    createRelatedField(){
        return [{
            label : 'Member Id',
            value : this.memberid,
			rowIndex: this.rowindex
        }];
    }

    /**
     * Applies pre-selected filters to subtab table
     * and CSS from utility commonstyles file
     * after DOM is rendered
     */
   renderedCallback() {
    Promise.all([
      loadStyle(this, loggingcss + '/CRM_Assets/styles/logging.css')
    ]).catch(error => {
    });
  }
}