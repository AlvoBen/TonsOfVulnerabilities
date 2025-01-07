import { LightningElement, api, track, wire } from 'lwc';
import { CurrentPageReference } from 'lightning/navigation';
import {
    registerListener
  } from 'c/pubsubLinkFramework';
import getPolicyMemberDetails from '@salesforce/apex/ClaimsSummary_LD_HUM.getPolicyMemberDetails';
export default class ClaimDetailsContainer extends LightningElement {
    pageRef;
    @api claimnum = '';
    @api platformCd = '';
    @api userAgent = navigator.userAgent;
    @track bIsLoading = false;
    @track isMedical = false;
    @track isDental = false;
    isError = true;
	@track remittId=[];
	@track claimDetail;
	@track AccountId;
    @wire(CurrentPageReference)
    currentPageReference(pageRef) {
        this.pageRef = pageRef;
    }

    handleErrorMeesage({ detail }) {
        if (detail) {
            this.isError = true;
        }
    }
	
	Eventcallbackmethodaname(PublisherMessage) {
        this.claimDetail = PublisherMessage;
    }

    connectedCallback() {
		registerListener('ClaimDataEvent', this.Eventcallbackmethodaname, this);
		this.Eventcallbackmethodaname(this);
        let url = '';
        url = this.pageRef.attributes.url;
        let navData = url.split('&');
        let newObj = {};
        navData.map((item) => {
            let splittedData = item.split('=');
            newObj[splittedData[0]] = splittedData[1];
        });
        this.claimnum = newObj.ClaimNbr;
	this.platformCd = newObj.PlatformCd;
	this.recid = newObj.recordId;
	getPolicyMemberDetails({sRecId : this.recid})
	.then(result => {
	if(result != null){
		this.AccountId = result[0].MemberId;
	}
	})
	 this.getPlanDetails();
	 }
    
     getPlanDetails() {
        if (this.platformCd == 'EM') {
            this.isDental = true;
        } else {
            this.isMedical = true;
        }
    }
	handleremitvalue(event){
        this.remittId=event.detail.remitId;
		
    }
	getRemitId(event){
		
        const objChild = this.template.querySelector('c-provider-claim-statements');
        objChild.addremitId(this.remittId);
		
    }
}