import { api, LightningElement, wire, track } from 'lwc';
import getClaimDetails from '@salesforce/apexContinuation/ClaimDetailsService_LC_HUM.claimDetailsRequest';
import { publish, MessageContext } from 'lightning/messageService';
import CONNECTOR_CHANNEL from '@salesforce/messageChannel/updateLablelHUM__c';
import { CurrentPageReference } from 'lightning/navigation';
import { fireEvent } from 'c/pubsubLinkFramework';
import  customcss from '@salesforce/resourceUrl/LightningCRMAssets_SR_HUM';
import { loadStyle } from 'lightning/platformResourceLoader';
import { copyToClipBoard } from 'c/crmUtilityHum';
import { performLogging, setEventListener, checkloggingstatus, clearLoggedValues, getLoggingKey} from 'c/loggingUtilityHum';

export default class ClaimDetailsLWC_HUM extends LightningElement {
    @track claimDetail=[];
    @track bIsLoading = true;
    @api beginDos;
    @api endDos;
    @api sClaimTypeSummary;
    @api sPlamMemId;
    @api sClaimGenKey;
    @track bMultiDiagCd = false;
    @api lstDiagCode= [];
    @track bisClaimTypeMedical ;
    @track bAuthMedical;
    @track bSingleDiagCode;
    @api sClaimNbr;
    @api sClaimId;
    @track bPolPlatRow;
    @api recieptdate;
    @api lastprocdate;
    @api claimtype;
    @api adjustment;
    @api platform;
    @api producttype;
    @api providerName;
    @api status;
    @api recID;
	@api MemberId;
    LWCVariables = [];
    @track bTexasdia = false;
    showLoggingIcon = true;
    startLogging = false;
    collectedLoggedData = [];
    autoLogging = true;
    @track urlClaimNumber;

    @wire(MessageContext)
    messageContext;
    @wire(CurrentPageReference)
    currentPageReference(pageRef) {
        this.pageRef = pageRef;
    }


    constructor(){
        super();
    }

    connectedCallback(){
        let url = '';
        url = this.pageRef.attributes.url;
        let navData = url.split('&');
        let newObj = {
        };
        navData.map(item => {
            let splittedData = item.split('=');
            newObj[splittedData[0]] = splittedData[1];
        });
        this.sClaimId = newObj.ClaimGenKey;
		this.claimtype = newObj.ClaimType;
        this.urlClaimNumber = newObj.ClaimNbr;
		this.adjustment = newObj.AdjustInd;
        this.platform = newObj.PlatformCd;
		this.status = newObj.Status;
        this.recID = newObj.recordId;
		this.providerName = newObj.ProviderName;
        this.Deduct = newObj.Deduct;
        this.Copay = newObj.Copay;
        this.CoIns = newObj.CoIns;
        this.callDetailService();
        this.beginDos = newObj.StartDate;
        this.endDos = newObj.EndDate;
        this.recieptdate = newObj.ReceiptDate;
        this.lastprocdate = newObj.ProcessDate;
        this.producttype = newObj.ProductType;   
		this.MemberId = newObj.MemberId;
    }
    
     renderedCallback() {
        Promise.all([
            loadStyle(this, customcss + '/CRM_Assets/styles/logging.css')
        ]).catch((error) => {});
    }
	
    async callDetailService()
    {
        const res = await getClaimDetails({claimId : this.sClaimId})
        .then(data => {
            this.claimDetail = data;	
			this.claimDetail.adjustment = this.adjustment;
			this.claimDetail.platform = this.platform;	
			this.claimDetail.status = this.status;
			this.claimDetail.recID = this.recID;
			this.claimDetail.providerName = this.providerName;
			this.claimDetail.sclaimtype = this.claimtype;
            this.claimDetail.Deduct = this.Deduct;
            this.claimDetail.Copay = this.Copay;
            this.claimDetail.CoIns = this.CoIns;
            this.bIsLoading = false;
            this.processDisplay();
            this.LWCVariables.push({ 'ClaimNumberId': this.urlClaimNumber });
			this.LWCVariables.push({ 'ClaimTypeInitial': (this.claimtype == 'Medical' || this.claimtype == 'Ambulatory' || this.claimtype == 'Hospital') ? 'M' : 'D'});
			//Claims new links START
            //CAS Claim Prefill and MTV Claim Prefill
            this.LWCVariables.push({ 'platformCode': this.platform });
           //Nucleus Claim Review Tool
            this.LWCVariables.push({ 'claimTypeTextId': this.claimtype });
            //END
			//Claims image
            this.LWCVariables.push({ 'srcLvCASPrefix': this.claimDetail?.sSrcLvCASPrefix });
			this.LWCVariables.push({ 'startDateId': this.beginDos });
			//END
            fireEvent(this.pageRef, 'LinkVariableEvent', this.LWCVariables);
			fireEvent(this.pageRef,'LineItemDataEvent',this.claimDetail);
			fireEvent(this.pageRef, 'ClaimDataEvent', this.claimDetail);
			fireEvent(this.pageRef, 'RefreshLinkPanel', '');
        })
        .catch((error) => {
            console.log('Exception Error Occured', error);
        });
        this.bisClaimTypeMedical = true;
	 if (this.autoLogging) {
            getLoggingKey(this.pageRef).then((result) => {
                this.loggingkey = result;
            });
        }
    }

    processDisplay(){
        if(this.bIsLoading == false)
        {
            if(this.claimDetail?.lstDiagnosisCodes?.length != null && this.claimDetail?.lstDiagnosisCodes?.length > 0){
                this.claimDetail.lstDiagnosisCodes.forEach((element,index) => {
                    this.lstDiagCode.push({
                        key : index+1,
                        value : element
                    })
                });
            }
            if(this.claimDetail?.bAuthKeyFlag == true && this.bisClaimTypeMedical == true)
            {
                this.bAuthMedical = true;
            }
            else{
                this.bAuthMedical = false;
            }

            if(this.bAuthMedical == false && this.claimDetail?.lstDiagnosisCodes?.length != null && this.claimDetail?.lstDiagnosisCodes?.length == 1)
            {
                this.bSingleDiagCode = true;
            }
            else{
                this.bSingleDiagCode = false;
            }
            if(this.claimDetail?.lstDiagnosisCodes?.length != null && this.claimDetail?.lstDiagnosisCodes?.length > 1)
            {
                this.bMultiDiagCd = true;
            }
            if(this.bAuthMedical == false && this.bMultiDiagCd == true)
            {
                this.bPolPlatRow = true;
            }
            else{
                this.bPolPlatRow = false;
            }
	     if(this.claimDetail?.sDeficiencyInd == 'Y'){
                this.bTexasdia = true;
            } else{
                this.bTexasdia = false;
            }
        }
    }

     get getDiagnosiscode()
    {
        if(this.claimDetail?.lstDiagnosisCodes?.length != null && this.claimDetail?.lstDiagnosisCodes?.length > 0)
        {
            var diagnosiscode= [];
            diagnosiscode = this.claimDetail.lstDiagnosisCodes;
            diagnosiscode = diagnosiscode.join(' ');
            return diagnosiscode;
        }
        return ' ';
    }

	copyToBoard(event){
		let copiedVal=  event.currentTarget.dataset.field;
        copyToClipBoard(copiedVal);
    }

    renderedCallback(){
        if(this.bIsLoading == false && this.bMultiDiagCd == true)
        {
            if(this.claimDetail?.lstDiagnosisCodes?.length != null && this.claimDetail?.lstDiagnosisCodes?.length > 0){
                let len = this.claimDetail.lstDiagnosisCodes.length;
                let rowCount = Math.ceil(len/4);
                let daigcodes =  this.claimDetail.lstDiagnosisCodes;
                let diagtable = {};
                let divdiagtable = [];
                for(let j = 0; j<rowCount; j++)
                {
                    diagtable[j] = daigcodes.splice(0, 4);
                }
                var sdiv = this.template.querySelector('.DiagnosisDiv');
                var divcontainer = document.createElement("div");
                divcontainer.className='slds-grid  slds-wrap';
                let key=1;
                for(let i = 0; i<rowCount; i++)
                {
                    for(let k=0; k<4; k++)
                    {
                        let rowdiv = document.createElement("div");
                        rowdiv.className = "slds-size_1-of-4 slds-p-left_small slds-p-top_small";
                        if(diagtable[i][k] != undefined)
                        {
                            let rowdivhead = document.createElement("div");
                            let divheadtext = document.createTextNode(key);
                            key++;
                            rowdivhead.appendChild(divheadtext);
                            let rowdivbody = document.createElement("div");
                            rowdivbody.className = 'slds-border_bottom';
                                                    
                            let rowval = document.createElement("a");
                            rowval.setAttribute("target", '_blank');
                            rowval.setAttribute("data-value", diagtable[i][k] );
                            rowval.addEventListener('click', this.openMentorCode);
                            rowval.className = 'diagrowval';
                            rowval.innerHTML += diagtable[i][k];
                            rowdivbody.appendChild(rowval);
                            rowdiv.appendChild(rowdivhead);
                            rowdiv.appendChild(rowdivbody);
                        }
                        divcontainer.appendChild(rowdiv);
                    }
                }
                sdiv.appendChild(divcontainer);
                let rowonclick = this.template.querySelector('.diagrowval');
                rowonclick.addEventListener('click', this.openMentorCode);
		}
            const payload = {claimNumber: this.urlClaimNumber};
            publish(this.messageContext, CONNECTOR_CHANNEL, payload);
        }
    }

    openMentorCode(events)
    {
        let sDiagCodeParam = events.currentTarget.dataset.value ;
        let sDiagCode = sDiagCodeParam;
        if (sDiagCodeParam.length > 3) {
            sDiagCode = sDiagCodeParam.substring(0, 3);
            sDiagCode = sDiagCode + ".";
            sDiagCode = sDiagCode + sDiagCodeParam.substring(3, sDiagCodeParam.length);
        }
        let sDocumentUrl = '/apex/OpenMentorDocument_VF_HUM?mentor_Code=' + sDiagCode;
        window.open(sDocumentUrl, "_blank");
    }

    copyToClipboard(event)
    {
        let content= event.currentTarget.dataset.cont;
        let tempTextAreaField = document.createElement('textarea');
        tempTextAreaField.value = content;
        document.body.appendChild(tempTextAreaField);
        tempTextAreaField.select();
        document.execCommand('copy');
        tempTextAreaField.remove();
    }
    
     handleLogging(event) {
        if (this.loggingkey && checkloggingstatus(this.loggingkey)) {
            performLogging(
                event,
                this.createRelatedField(),
                'Claim Details',
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
                        'Claim Details',
                        this.loggingkey,
                        this.pageRef
                    );
                }
            });
        }
    }

    
    createRelatedField() {
        return [
            {
                label: 'Claim',
                value: this.claimDetail.sClaimNbr
            }
        ];
    }
	
	 get generateLogId(){
        return Math.random().toString(16).slice(2);
    }
}