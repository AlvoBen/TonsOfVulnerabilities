/*******************************************************************************************************************************
LWC JS Name : claimPaymentDetailDetailHUM.js
Function    : This JS serves as Controller to claimPaymentDetailDetailHUM

Modification Log: 
Developer Name           Code Review                      Date                         Description
*--------------------------------------------------------------------------------------------------------------------------------
Suraj Patil                                         14/08/2022                    User story 3530478
Raj Paliwal											19/09/2022					  User story 3831690										
Suraj Patil                                         06/10/2022                    User story 3580835
Sagar G        									                    18/08/2023	      		        User story 4762116: T1PRJ0891415 - MF4439757 - Test Class Code Coverage
Apurva Urkude                                       07/28/2023                    Defect-Fix 7946
Apurva Urkude                                       08/10/2023                    Defect-Fix 7975
*********************************************************************************************************************************/
import { LightningElement, track , wire, api} from 'lwc';
import { CurrentPageReference } from 'lightning/navigation';
import { MessageContext } from 'lightning/messageService';
import { ShowToastEvent } from 'lightning/platformShowToastEvent';
import { NavigationMixin } from 'lightning/navigation';
import getClaimPaymentDetails from '@salesforce/apexContinuation/ClaimDetailsService_LC_HUM.initiatePaymentRequest';
import getApplauncherEnvironment from '@salesforce/apex/ClaimDetailsService_LC_HUM.getApplauncherEnvironment';
import getCASApplauncherData from '@salesforce/apex/ClaimDetailsService_LC_HUM.getCASApplauncherData';
import ClaimsService_Error from '@salesforce/label/c.ClaimsSummary_Service_Error';
import customcss from '@salesforce/resourceUrl/LightningCRMAssets_SR_HUM';
import { loadStyle } from 'lightning/platformResourceLoader';
import getUserDetails from '@salesforce/apex/GetCurrentUserDetails.getUserDetails';
import { 
performLogging, 
setEventListener, 
checkloggingstatus, 
clearLoggedValues,
 getLoggingKey
 } from 'c/loggingUtilityHum';

export default class claimPaymentDetailDetailHUM extends NavigationMixin (LightningElement) {
@track direction = 'asc';
@track PaymentDetailRecords;
@track pageNumber = 1;
@api claimdetail;
Processeddate;
isExpanded=false;
lastProcessedoptions=[];
AdjInd;
sPaymentCheckNumber;
ClaimNumber;
PlatformCode;
bDisplayPaymentsInfoMsgMTV = false;
bDisplayPaymentsInfoMsgCAS = false;
bDisplayPaymentsInfoMsg = false;
Environment = '';
@track url = 'AppLauncher:';
bIsLoading = true;
mapData = new Map();
@track lineItemAccordianData;
MTVEMPlatform = false;
CheckStatusLink = '';
@track sortDirHandler;
overfieldname;
focus1=false;
focus2=false;
focus3=false;
focus4=true;
focus5=false;
@track columnsHeaders = {
    'Claim Dates/ID': 'sLastProcessDate',
    'Seq#': 'sSequenceNum',
    'Payment': 'sPaymentAmount',
    'Method' : 'sPaymentType',
    'Issue Date': 'sPaymentIssueDate',
    'Payee': 'sPayee'
};
noData = false;
totalCount;
@wire(MessageContext)
messageContext;
@wire(CurrentPageReference) pageRef;
@track loggingkey;
showloggingicon = true;
@track autoLogging = true;
@track countervalue;
ownerQueue='';

	connectedCallback() 
	{
		if(this.claimdetail.ClaimDetailLines !=null || this.claimdetail.ClaimDetailLines != undefined)
		{
      for(var key in this.claimdetail.ClaimDetailLines)
      {
        this.mapData.set(this.claimdetail.ClaimDetailLines[key].sLastProcessDate+' ('+this.claimdetail.ClaimDetailLines[key].sSrcClaimLineSequence+')',this.claimdetail.ClaimDetailLines[key]);
      }
      for (let [key, value] of this.mapData.entries()) 
      {
        if(this.claimdetail.platform == 'EM'){
        this.lastProcessedoptions.push({ label: key, value: key});
        this.lastProcessedoptions.sort(function(a,b){
                return new Date(a.label.substring(0,10)) - new Date(b.label.substring(0,10))
                    })
              this.lastProcessedoptions= this.lastProcessedoptions.reverse();
        }
        if(this.claimdetail.platform == 'LV'){
        this.lastProcessedoptions.push({ label: key.split(' (')[0], value: key});
        this.lastProcessedoptions.sort(function(a,b){
              return new Date(a.label) - new Date(b.label)
                        })
                this.lastProcessedoptions= this.lastProcessedoptions.reverse();
        }
      }

      this.value = this.lastProcessedoptions[0].value;
      this.lineItemAccordianData = this.mapData.get(this.value);
        if(this.claimdetail.platform == 'EM')
        this.MTVEMPlatform = true;
      const unique = new Set(this.lineItemAccordianData.sCheckNbr.split('0'));
      if((this.lineItemAccordianData.sCheckNbr === '' || this.lineItemAccordianData.sCheckNbr === '0' || unique.size === 1) && this.claimdetail.adjustment === 'Y')
      {
        this.bDisplayPaymentsInfoMsg = true;
        this.bIsLoading = false;
        if(this.claimdetail.platform == 'EM') this.bDisplayPaymentsInfoMsgMTV = true;
        if(this.claimdetail.platform == 'LV') this.bDisplayPaymentsInfoMsgCAS = true; 
      }
      
      if(this.bDisplayPaymentsInfoMsg == false)
      {
      this.callClaimPaymentDetailsService();
      this.getUserQueue();
      }
    }
	  else{
	    this.bIsLoading = false;
	    this.noData = true;
	  }	
	}
  
	callClaimPaymentDetailsService()
	{
		getClaimPaymentDetails({
			AdjInd : this.claimdetail.adjustment,
			sPaymentCheckNumber : this.lineItemAccordianData.sCheckNbr,
			ClaimNumber : this.claimdetail.sClaimNbr,
			PlatformCode : this.claimdetail.platform
		})
		.then(result => {
			this.bIsLoading = false;
			if(result === ClaimsService_Error){
				this.noData = true;
				this.showToast("", ClaimsService_Error, "error");
			}
			else
			{
				let PaymentDetailResponse = JSON.parse(result);
				if (PaymentDetailResponse !== null && PaymentDetailResponse != undefined) 
				{
					if(PaymentDetailResponse.length > 0)
					{
						PaymentDetailResponse.forEach(element => {
							element.sLastProcessDate = this.lineItemAccordianData.sLastProcessDate;
							if(this.claimdetail.platform == 'EM') element.sSequenceNum = this.lineItemAccordianData.sSrcClaimLineSequence;
							else element.sSequenceNum = '';
							element.sPayee = this.lineItemAccordianData.sPayeeCd;
							element.sPaymentAmount = '$'+element.sPaymentAmount;
							element.sBankAccCode = this.claimdetail.sBankCode;
					              if(element.sPaymentType=='CHK')
					              {
					                element.isCHK = true;
					                this.CheckStatusLink = element.sPaymentUrl;
					              }
					              else
					              element.isCHK = false;

					              let key = 1;
					              if(element.ClaimDetails != null && element.ClaimDetails.ClaimDetail != null)
					              {
					                element.ClaimDetails.ClaimDetail.forEach(ele => {
									  this.countervalue =key;
					                  ele.Counter = key;
					                  key++;
					                });
					              }
            					});
            this.PaymentDetailRecords = PaymentDetailResponse.map(item => {
                return {
                    ...item,
                    Expanded:false
                }
            });
						this.sortData('sPaymentIssueDate');
						this.totalCount = this.PaymentDetailRecords.length;
					}
					else
					{
						if(this.claimdetail.adjustment === 'Y')
						{
							result = '[{"sSequenceNum":"","sRemit":"","sPaymentUrl":"","sPaymentTypeID":"","sPaymentType":"","sPaymentStatusDate":"","sPaymentStatus":"","sPaymentIssueDate":"","sPaymentAmount":"","sPayeeName":"","sPayeeID":"","sPayeeAddress":"","sPayee":"","sLastProcessDate":"","sCheckNumber":"","sCheckDate":"","sBankAccCode":"","ClaimDetails":{"ClaimDetail":[]},"bPaymentInfoMsg":false}]';
							  let PaymentDetailResponse = JSON.parse(result);
							  PaymentDetailResponse.forEach(element => {
								if(this.claimdetail.platform == 'EM') element.sSequenceNum = this.lineItemAccordianData.sSrcClaimLineSequence;                
								else element.sSequenceNum = '';
							  });
							  this.PaymentDetailRecords = PaymentDetailResponse.map(item => {
								return {
									...item,
									Expanded:false
								}
							  });
							  this.totalCount = '1';
						}
						else if (this.claimdetail.adjustment === 'N' && this.claimdetail.status == 'Pended') 
						{
							this.noData = true;
						}
						else if (this.claimdetail.adjustment === 'N' && this.claimdetail.status != 'Pended')
						{
							this.bDisplayPaymentsInfoMsg = true;
							if(this.claimdetail.platform == 'EM') this.bDisplayPaymentsInfoMsgMTV = true;
							if(this.claimdetail.platform == 'LV') this.bDisplayPaymentsInfoMsgCAS = true; 
						}
					}
				}
				else{
				this.noData = true;
				}
			} 
		})
		.catch(err => {
			console.log('Error in connectedCallback of claimPaymentDetailDetailHUM');
			this.noData = true;
			this.showToast("", ClaimsService_Error, "error");
		});
		
			if (this.autoLogging) {
      getLoggingKey(this.pageRef).then((result) => {
          this.loggingkey = result;
      });
  }
	}

  showToast(strTitle, strMessage, variantname) {
    this.dispatchEvent(new ShowToastEvent({
        title: strTitle,
        message: strMessage,
        variant: variantname
    }));
  }

  handleSort(event) {
      this.sortData(this.columnsHeaders[event.target.outerText]);
      this.overfieldname=this.columnsHeaders[event.target.outerText];
      this.sortfocus(this.overfieldname);
  }

  sortfocus(fieldname){
    if(fieldname=="sSequenceNum"){
        this.focus1=true;
        this.focus2=false;
        this.focus3=false;
        this.focus4=false;
        this.focus5=false;   
    }
    else if(fieldname=="sPaymentAmount"){
        this.focus1=false;
        this.focus2=true;
        this.focus3=false;
        this.focus4=false;
        this.focus5=false;
    }
    else if(fieldname=="sPaymentType"){
        this.focus1=false;
        this.focus2=false;
        this.focus3=true;
        this.focus4=false;
        this.focus5=false;  
    }
    else if(fieldname=="sPaymentIssueDate"){
        this.focus1=false;
        this.focus2=false;
        this.focus3=false;
        this.focus4=true;
        this.focus5=false;
    }
    else if(fieldname=="sPayee"){
      this.focus1=false;
      this.focus2=false;
      this.focus3=false;
      this.focus4=false;
      this.focus5=true;
    }
  }

  sortData(fieldname) {
      let parseData = JSON.parse(JSON.stringify(this.PaymentDetailRecords));
      let keyValue = (a) => {
          return a[fieldname];
      };

      this.direction = this.direction === 'asc' ? 'desc' : 'asc';
      let isReverse = this.direction === 'asc' ? 1 : -1;
      parseData.sort((x, y) => {
          x = keyValue(x) ? keyValue(x) : '';
          y = keyValue(y) ? keyValue(y) : '';
          return isReverse * ((x > y) - (y > x));
      });
      
      if(fieldname=='sSequenceNum'){
          this.sortDirHandler=(this.direction==='asc')?'utility:arrowup':'utility:arrowdown';   
      }
      if(fieldname=='sPaymentAmount'){
          this.sortDirHandler=(this.direction==='asc')?'utility:arrowup':'utility:arrowdown';
      }
      if(fieldname=='sPaymentType'){
          this.sortDirHandler=(this.direction==='asc')?'utility:arrowup':'utility:arrowdown';
      }
      if(fieldname=='sPaymentIssueDate'){
          this.sortDirHandler=(this.direction==='asc')?'utility:arrowup':'utility:arrowdown';
      }
      if(fieldname=='sPayee'){
          this.sortDirHandler=(this.direction==='asc')?'utility:arrowup':'utility:arrowdown';
      }
      this.PaymentDetailRecords = [];
      this.PaymentDetailRecords = parseData;
  }

  handleToggle(event){
      var index=event.target.value;
      var data = JSON.parse(JSON.stringify(this.PaymentDetailRecords));
      data[index].Expanded=(data[index].Expanded==false)?true:false;
      this.PaymentDetailRecords = data;
      event.target.iconName=(event.target.iconName=='utility:chevronright')?'utility:chevrondown':'utility:chevronright';
  }

  handleChange(event) {
    this.value = event.detail.value;
	this.lineItemAccordianData = this.mapData.get(this.value);
  }

  OpenMTVNewLegacywindow()
  {
    getApplauncherEnvironment()
    .then(result => {
      if(result != null && result != ''){
        this.Environment = result;
      }
      this.url = '';
      this.url = 'AppLauncher:';
      this.url += 'CallingApplication=SFDC&' + 'ApplicationType=NonWeb&' + 'Mode=View&' + 'ApplicationName=MTVLegacy&' + 'EntityType=Member&' + 'Functionality=KM&' + 'ClaimNumber=' + this.claimdetail.sClaimNbr + '&' + 'Environment=' +  this.Environment;
      
      
      this.url = this.url.replace(/=null&/, '=&');
      this[NavigationMixin.Navigate]({
        type: "standard__webPage",
        attributes: {
          url: this.url
        }
      });
    })
  }

  OpenNewCASLegacywindow()
  {
    let ClientNumber = '';
    let MemberId = '';
    let FirstName = '';
    let Relation = '';
    let Environment = '';
    getCASApplauncherData({sMemberPlanId : this.claimdetail.recID})
    .then(result => {
      if(result != null && result != undefined){
        ClientNumber = result.ClientNumber;
        MemberId = result.MemberId;
        FirstName = result.FirstName;
        Relation = result.Relation;
        Environment = result.Environment;
      }
      this.url = '';
      this.url = 'AppLauncher:';
      this.url +=  'CallingApplication=SFDC&' + 'ApplicationType=NonWeb&' + 'Mode=View&' + 'ApplicationName=CASLegacyPrefill&' + 'EntityType=Member&' + 'Functionality=KM&' + 'SessionName=CAS - CLIENT&' + 'ClientNumber=' + ClientNumber + '&' + 'MemberID=' + MemberId + '&' + 'FirstName=' + FirstName + '&' + 'Relation=' + Relation + '&' +'ClaimNumber=' + this.claimdetail.sClaimNbr + '&' + 'Environment=' +  Environment;
      
      this.url = this.url.replace(/=null&/, '=&');
      this[NavigationMixin.Navigate]({
        type: "standard__webPage",
        attributes: {
            url: this.url
        }
      });
    })
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
            value: this.claimdetail.sClaimNbr
        }
    ];
}
  OpenCheckStatusLink()
  {
    this[NavigationMixin.Navigate]({
      type: "standard__webPage",
      attributes: {
          url: this.CheckStatusLink
      }
    });
  }

renderedCallback() {
    Promise.all([
        loadStyle(this, customcss + '/CRM_Assets/styles/logging.css')
    ]).catch((error) => {});
}

  OpenClaimInquiryTool() 
  {
    var mapForm = document.createElement("form");
    mapForm.method = "POST";
    mapForm.action = 'http://test-slservices.humana.com/IHT/CITIntegration.aspx';
    mapForm.target = "_blank";

        var mapInput = document.createElement("input");
        mapInput.type = "hidden";
        mapInput.name = 'ClaimNbr';
        mapInput.value = this.claimdetail.sClaimNbr;
        mapForm.appendChild(mapInput);

        var mapInput1 = document.createElement("input");
        mapInput1.type = "hidden";
        mapInput1.name = 'DepartmentData';
        mapInput1.value = this.ownerQueue;
        mapForm.appendChild(mapInput1);
    
    document.body.appendChild(mapForm);
    mapForm.submit();
    document.body.removeChild(mapForm);
  }
  
    get getclaimnumber()
  {
    let data='ClaimNumber'+ this.countervalue;
    return data;
  }

  get getClaimPayment()
  {
    let data= 'Paid Amount'+ this.countervalue;
    return data;
  }
  
   get generateLogId(){
    return Math.random().toString(16).slice(2);
   }

   getUserQueue(){
    getUserDetails({}).then(res => {
     this.ownerQueue=res.Current_Queue__c;
      }).catch(error => {
      console.log('error in profile of myHumanacard--', error);
    });
  }
}