/*******************************************************************************************************************************
LWC JS Name : claimsTableComponentHum.js
Function    : This JS serves as controller to claimTable.html. 
Modification Log: 
  Developer Name           Code Review                      Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------- 
* Apurva Urkude										                      04/18/2023		  US-4399890: Claims- Filter Functionality on Claims Detail Page 
* Suraj Patil												              04/18/2023		  US-4399869: Claims- Add "Total of Last Processed Date" field/data 
* Apurva Urkude                                                           06/30/2023          US-4757243: INC2390166 - Go Live Incident Line items component error RAID 031
* Apurva Urkude                                                           07/17/2023          Defect Fix-7859
* Sagar G      									                          25/07/2023	      User story 4603414: T1PRJ0883127- MF 4633852 - Not displaying multiple lines of blank code for the EX field when viewing HP claim details (Lightning)
* Sagar G      									                          11/08/2023          DF-7974: QA_Lightning_US4858079_T1PRJ0865978_While clicking on this hyperlink "Access the Claim Inquiry Tool Link for Code Editing Rationale" it is navigating to the test region page instead of QA region page
* Sagar G      									                          25/08/2023          US-5008473: T1PRJ0865978 - MF 4796385 - C03, Contact Servicing- Code Editing update
* Apurva Urkude                                           09/01/2023          US-4997879: F 4937947 - C03-Contact Servicing- Add hyperlink to Mentor for Claims "Modifier" field
* Apurva Urkude                                           09/27/2023          Defect Fix-DF-8167
* Sagar G      									                          09/29/2023          T1PRJ0865978 - MF 4743214 - INC2569481 C03, Contact Servicing- Code Editing update
*********************************************************************************************************************************/

import { LightningElement, track, api, wire } from 'lwc';
import { compute, populatedDataIntoModel, getHelpText, getTableModal } from './claimsTableHelpers';
import claimsTableHum from './claimsTableHum';
import { getLabels, hcConstants } from 'c/crmUtilityHum';
import loggingcss from '@salesforce/resourceUrl/LightningCRMAssets_SR_HUM';
import { loadStyle } from 'lightning/platformResourceLoader';
import { CurrentPageReference } from 'lightning/navigation';
import { performTableLogging, performLogging, getLoggingKey, checkloggingstatus} from 'c/loggingUtilityHum';
import getCauseCodeCustomSettingDetails from '@salesforce/apex/ClaimDetailsService_LC_HUM.getCauseCodeCustomSettingDetails';
import { NavigationMixin } from 'lightning/navigation';
import getUserDetails from '@salesforce/apex/GetCurrentUserDetails.getUserDetails';
import getClaimInquiryToolLink from '@salesforce/apex/ClaimDetailsService_LC_HUM.getClaimInquiryToolLink';
import codeEditRationale from '@salesforce/apexContinuation/ClaimDetailsService_LC_HUM.getRationale';
import { ShowToastEvent } from "lightning/platformShowToastEvent";
import redirectMentorDocument from '@salesforce/apex/ClaimDetailsService_LC_HUM.redirectMentorDocument';

export default class claimsTableComponentHum extends NavigationMixin (LightningElement) {
    // @api response;
  @api responsedata;
  @api nameOfScreen;
  @api title = "Results";
  @api bHideTitle = false;
  @api resultClass = "selectRow";
  @track accordianicon = "utility:jump_to_right";
  @api resulttantTableStructure;
  @api noDataMessage;
  @api showViewAll = false;
  @track columns;
  @api claimNumber;
  @track tableModal;
  @track loaded = false;
  @track tempList;
  @track noDataAvailable = false;
  @track resultsHelpText = '';
  @track arraytranss;
  @api templatemodal;
  @track tablechildModal;
  @track claimAccordionData;
  @api templateaccordian = false;
  @track showhideaccordian = false;
  @track enableViewAll = false; 
  @track bClaims = false;
  labels = getLabels();
  @track claimresponse;
  @api codeEditInfo;
  MTVEMPlatform=false;
  @api claimplatformcode;
  @api relatedInputField;
  @track loggingkey;
  pageRef;
  @api calledFromLogging = false;
  @api screenName;
	@api denPol = false;
  @api claimdetailresposne;
	@track Totallastvalue;
  @api totallastprocdate;
  @track lstTotaLineItems =[];
  @track firstlastprocdate = '';
  @track hideTotalProcRow = false;
  @track lastprocdate='';
  ownerQueue='';
  ClaimInquiryToolURL;
  codeEditBool=false;
  @api diagIndicator;
  HumanaCodeEdit=false;
  iHealthCodeEdit=false;
  @track iRespDesc='';
  @track filtercode;
  @track mentorcode;

	@wire(getClaimInquiryToolLink,{})
  claimtoolLink(response,error){
    if(response){
      this.ClaimInquiryToolURL=response.data;
    }else if(error){
      console.log('getClaimInquiryToolLink error'+JSON.stringify(error));
    }
  } 
	
  render() {
    return claimsTableHum;
  }

	expandrow(event) 
	{
    if (event.currentTarget.getAttribute('data-actionname') == "utility:chevrondown") 
		{
			this.showhideaccordian = true;
			this.accordianicon = "utility:chevronright";
			let newaar = [];
			for(let i=0; i< this.tableModal.length; i++){
				this.claimresponse.forEach(ele => {
				  if(ele.Id == this.tableModal[i][0][0].value) {
					this.claimAccordionData = ele;
				 this.bClaims = true;
				  }
				});
				if (event.currentTarget.getAttribute('data-att') == this.tableModal[i][0][0].value) { 
					newaar.push(Object.assign({}, { "expand": false, "Id": this.tableModal[i][0][0].value, "celldata": this.tableModal[i] }));
				}
				else if(this.arraytranss[i].expand == true)
				{
					newaar.push(Object.assign({}, { "expand": true, "Id": this.tableModal[i][0][0].value, "celldata": this.tableModal[i],"accordiondata1":this.claimAccordionData}));
				}
				else{
					newaar.push(Object.assign({}, { "expand": false, "Id": this.tableModal[i][0][0].value, "celldata": this.tableModal[i]}));
				}
			}
			this.arraytranss = newaar;
		}
		else 
		{

		var selArrIndex = event.target.dataset.att;

		var selectedarr;
      this.claimresponse.forEach(ele => {
      if(ele.Id == selArrIndex) {
      selectedarr= ele;
      }
      });
		var paymentCode = selectedarr?.PaymentCodes?.PaymentCode;
		let diagnosisCode = selectedarr?.DiagCode?.Code;
		let paiddesc = selectedarr?.sPaidStatDesc;
		let cptModCode = selectedarr?.CPTModCode;
		let serviceCode = selectedarr?.sServiceCode;
		let eventName = event.target.name;
    this.codeEditBool=false;
    this.iHealthCodeEdit=false;
    this.HumanaCodeEdit=false;
    this.iRespDesc='';
   
		  codeEditRationale({
			lstDiagnosisCd: diagnosisCode,
			strPaymentCode: JSON.stringify(paymentCode),
			sPlatformCode: this.claimplatformcode,
			sDiagIndicator: this.diagIndicator,
			sPaidStatDesc: paiddesc,
			sCPTModCode: cptModCode,
			sServiceCode: serviceCode,
			sClmNbr: this.claimNumber
		})
			.then((responsedata) => {
			  if(responsedata!=null && responsedata.length!=0){
          var flagiHealthCodeEdit = false;
          var rationalDesc =[];
          for(var i=0;i<responsedata.length;i++){
              if (responsedata[i].sCodeEditInd == 'HumanaCodeEdit' && !(rationalDesc.includes(responsedata[i].sRationaleDesc))){
                this.codeEditBool=true;
                this.HumanaCodeEdit=true;
                this.iRespDesc=responsedata[0].sRationaleDesc;
              } 
              if (responsedata[i].sCodeEditInd == 'iHealthCodeEdit' && !flagiHealthCodeEdit) {
                flagiHealthCodeEdit = true;
                if(rationalDesc.length==0){
                  this.codeEditBool=true;
                  this.iHealthCodeEdit=true;
                }
              }
          }
        }
				this.showhideaccordian = true;
				this.accordianicon = "utility:chevrondown";
				this.claimresponse = JSON.parse(JSON.stringify(this.claimresponse));
				  this.claimresponse.forEach((element,index)=>{
					element?.PaymentCodes?.PaymentCode.forEach((ele,ind)=>{
						if(index==selArrIndex-1){
						  element.codeEditHandler=this.codeEditBool;
              element.iHealthCodeEdit=this.iHealthCodeEdit;
              element.HumanaCodeEdit=this.HumanaCodeEdit;
              element.iRespDesc=this.iRespDesc;
						}
						else{
						  element.codeEditHandler=element.codeEditHandler==true?true:false;
              element.iHealthCodeEdit=element.iHealthCodeEdit==true?true:false;
              element.HumanaCodeEdit=element.HumanaCodeEdit==true?true:false;
						}
						if(ele.sLegacyCd==''){
						  this.claimresponse[index].PaymentCodes.PaymentCode.splice(ind, 1);
						}
					});
				  });
				let newaar = [];
					for(let i=0; i< this.tableModal.length; i++){
					this.claimresponse.forEach(ele => {
						if(ele.Id == this.tableModal[i][0][0].value) {
							this.claimAccordionData = ele;
							this.bClaims = true;
						}
					});
					if(eventName!=='expandAll')
					{
						if (selArrIndex == this.tableModal[i][0][0].value) 
						{
							newaar.push(Object.assign({}, { "expand": true, "Id": this.tableModal[i][0][0].value, "celldata": this.tableModal[i],"accordiondata1":this.claimAccordionData}));
						}
						else if(this.arraytranss[i].expand == true)
						{
							newaar.push(Object.assign({}, { "expand": true, "Id": this.tableModal[i][0][0].value, "celldata": this.tableModal[i],"accordiondata1":this.claimAccordionData}));
						}
						else
						{ 
							newaar.push(Object.assign({}, { "expand": false, "Id": this.tableModal[i][0][0].value, "celldata": this.tableModal[i]}));
						}
					}
						}
			this.arraytranss = newaar;
			})
        .catch((e) => {
            console.log('exception' + e);
        });
		}
	}
  connectedCallback() {
    this.computecallback(this.response);
    this.getUserQueue();
  }
  @api
  accordiancomputecallback(response) {
    this.tablechildModal = null;
    if (response) {
      if(this.nameOfScreen == 'ClaimDetails') {
        this.claimAccordionData = response;
        this.bClaims = true;
      }
      else {
      this.tablechildModal = compute('purchaserplan', response);
      if (this.tablechildModal)
        this.childcolumns = JSON.parse(JSON.stringify(this.tablechildModal[0][0]));
      }
    }
  }
  @api
  computecallback(response) {
	if(this.claimplatformcode == 'EM') this.MTVEMPlatform = true; 
    this.claimresponse = response;
    this.tableModal = null;
    this.arraytranss = null;
    if (response.length!=0) {
    if(this.claimresponse[0].claimtype != 'Medical' && this.claimresponse[0].claimtype != 'Ambulatory' && this.claimresponse[0].claimtype != 'Hospital') this.denPol = true;
    setTimeout(() => {
      
		if(this.denPol){ this.nameOfScreen = 'denClaimDetails'}
        const tableData = this.templatemodal ?this.denPol? populatedDataIntoModel('denClaimDetails', response): populatedDataIntoModel(JSON.parse(JSON.stringify(this.templatemodal)), response) : compute(this.nameOfScreen, response);
        const TotaltableData = this.templatemodal ?this.denPol? populatedDataIntoModel('denClaimDetails', response): populatedDataIntoModel(JSON.parse(JSON.stringify(this.templatemodal)), response) : compute(this.nameOfScreen, response);
		const totalRecords = tableData.length;
        if (this.showViewAll) {
          this.enableViewAll = totalRecords > hcConstants.MIN_RECORDS;  // if records count greater thatn hcConstants.MIN_RECORDS
          this.tableModal = this.enableViewAll ? tableData.splice(0, hcConstants.MIN_RECORDS): tableData;
        }
        else{
          this.tableModal = tableData;
        }
        this.resultsHelpText = getHelpText(this.nameOfScreen, this.labels);
        if (this.tableModal) {
          if (this.templateaccordian) {
            let arraytrans = [];
            response.forEach((resp) => {
              if (!(resp.Product__c == 'MED' && resp.Product_Type__c == 'PDP' && resp.Product_Type_Code__c == 'PDP')) {
                this.tableModal.forEach((arg1) => {
                  arg1.forEach((arg2) => {
                    arg2.forEach((arg3) => {
                      if (arg3.hasOwnProperty('accordian')) {
                        if (resp.Id == arg3.value && this.nameOfScreen !='ClaimDetails') {
                          arg3.accordian = false;
                        }
                        if(this.nameOfScreen == 'ClaimDetails'|| this.nameOfScreen == 'denClaimDetails')
                        arg3.accordian = true;
                      }
                    })
                  })
                });
              }
            });   
            this.tableModal.forEach((arg1) => {
              arraytrans.push(Object.assign({}, { "expand": false, "Id": arg1[0][0].value, "celldata": arg1 }));
            });
            this.arraytranss = arraytrans;

            if(this.firstlastprocdate === '' || this.firstlastprocdate === this.totallastprocdate){
              this.firstlastprocdate = this.totallastprocdate;
              this.hideTotalProcRow = false;
            }
            if(this.firstlastprocdate !== this.totallastprocdate){
              this.hideTotalProcRow = true;
            }
            if(this.hideTotalProcRow == false && (this.Totallastvalue == null || this.Totallastvalue == '' || this.Totallastvalue == undefined)){
              this.lstTotaLineItems.sChargeAmt = this.claimdetailresposne.ClaimDetailLineTotal.sChargeAmt;
              this.lstTotaLineItems.sBenAllowAmt = this.claimdetailresposne.ClaimDetailLineTotal.sBenAllowAmt;
              this.lstTotaLineItems.sProvWriteOff = this.claimdetailresposne.ClaimDetailLineTotal.sProvWriteOff;
              this.lstTotaLineItems.sExcludeAmt = this.claimdetailresposne.ClaimDetailLineTotal.sExcludeAmt;
              this.lstTotaLineItems.sBenDenyAmt = this.claimdetailresposne.ClaimDetailLineTotal.sBenDenyAmt;
              this.lstTotaLineItems.sCopayAmt = '$'+ this.claimdetailresposne.Copay;
              this.lstTotaLineItems.sDeductAmt = '$'+ this.claimdetailresposne.Deduct;
              this.lstTotaLineItems.sCoInsAmt = '$'+ this.claimdetailresposne.CoIns;
              this.lstTotaLineItems.sCobPaidAmt = this.claimdetailresposne.ClaimDetailLineTotal.sCobPaidAmt;
              this.lstTotaLineItems.sMbrRespAmt = this.claimdetailresposne.ClaimDetailLineTotal.sMbrRespAmt;
              this.lstTotaLineItems.sPaidAmt = this.claimdetailresposne.ClaimDetailLineTotal.sPaidAmt;

              let index = 0;
              let temparray = [];
              TotaltableData.forEach((itemnew) => {
                itemnew.forEach((item) => {
                  if(this.Totallastvalue == null){
                    item.forEach((item1) => {
                      if(index > 2){ temparray.push(item1); }
                      index++;
                    });
                    this.Totallastvalue = temparray;
                  }
                });
              });
              
              this.Totallastvalue.forEach((totalitem) => {
                let divJsonValue = '';
                totalitem.compoundvalue.forEach((totalitem1) => {
                  totalitem1.value = this.lstTotaLineItems[totalitem1.fieldName];
                  divJsonValue = divJsonValue + totalitem1.label +': '+ totalitem1.value+'\n';
                });
                totalitem.JSONLoggingData = divJsonValue;
              });
            }
            this.lastprocdate =this.totallastprocdate;
            if(this.totallastprocdate.includes("("))
            this.lastprocdate = this.totallastprocdate.substring(0, this.totallastprocdate.indexOf("("));
          }
          this.tempList = this.tableModal;
		  if(this.denPol){ this.nameOfScreen = 'denClaimDetails'}
          const cols = this.templatemodal ? this.templatemodal : getTableModal(this.nameOfScreen);
          this.columns = cols[0];
          this.noDataAvailable = this.tableModal.length ? false : true;
          this.loaded = true;
        }

        if (this.tableModal.length === 1) {
          if (this.nameOfScreen !== 'Policy' && this.nameOfScreen !== 'grouppolicies') {
            this.tableModal[0][0][0].checked = true;
            this.getInteractionData(this.tableModal[0][0][0].value);
          }
        }
      
    }, 1);
  }
  }

  @track tableData;

  @api
  get response() {
    return this.tableData;
  }
  set response(value) {
    this.tableData = value;
    if (value) {
      this.computecallback(this.tableData);
    }
  }
  fetchRecordDetails(event) {
    const actionName = event.currentTarget.getAttribute('data-actionname');

    if (actionName === 'FIREINTERACTIONS') {
      this.getInteractionData(event.target.value);
    }
  }

  getInteractionData(recordId) {
    var uniqueId = recordId;
    let tmp = [];
    let tempVar = [];
    this.tableModal.forEach((primaryRec) => {
      primaryRec.forEach((record) => {
        const recordFound = record.find(item => item.value === uniqueId);
        if (recordFound) {
          recordFound.checked = true;
          tmp.push(record);
        }
      });
    });

    tempVar.push(tmp);

    this.tableModal = null;

    setTimeout(() => {
      this.tableModal = tempVar;
      const detail = { Id: uniqueId };
      const interaction = new CustomEvent('interactions', { detail });
      this.dispatchEvent(interaction);
    }, 1);
  }


  oncheckboxcheck(event) {
    const detail = { checked: event.target.checked };
    const checkboxcheck = new CustomEvent('checkboxcheck', { detail });
    this.dispatchEvent(checkboxcheck);
  }

  @api
  backToResult() {
    this.tableModal = null;
    setTimeout(() => {
      this.tableModal = this.tempList.map((primaryRec) => {
        return primaryRec.map((record) => {
          const recordFound = record.find(item => item.radio === true);
          if (recordFound) {
            recordFound.checked = false;
          }
          return record;
        });
      });
    }, 1);
  }

  @api
  recordIdToNavigate(recId) {
    this.template.querySelector('c-standard-compound-table-hum').navigateToDetailPage(recId);
  }

   
     @wire(CurrentPageReference)
       wiredPageRef(pageRef) {
        this.pageRef = pageRef;
    }

    handleLogging(event) {
        let trelement = this.traverseUpTill(event.target, 'TR');
        if (!this.calledFromLogging) {
            if (this.loggingkey && checkloggingstatus(this.loggingkey)) {
                performTableLogging(
                    event,
                    this.tableData,
                    this.relatedInputField,
                    this.columns,
                    this.screenName,
                    this.pageRef,
                    this.createRelatedField(),
                    this.loggingkey
                );
            } else {
                getLoggingKey(this.pageRef).then((result) => {
                    this.loggingkey = result;
                    if (
                        this.loggingkey &&
                        checkloggingstatus(this.loggingkey)
                    ) {
                        performTableLogging(
                            event,
                            this.tableData,
                            this.relatedInputField,
                            this.columns,
                            this.screenName,
                            this.pageRef,
                            this.createRelatedField(),
                            this.loggingkey
                        );
                    }
                });
            }
        }
        this.dispatchEvent(
            new CustomEvent('tablerowselected', {
                detail: trelement
            })
        );
    }

    traverseUpTill(clickedElement, nodeName) {
        let prtNode = clickedElement;
        if (prtNode && prtNode.parentElement) {
            while (prtNode.parentElement) {
                prtNode = prtNode.parentElement;
                if (prtNode && prtNode.nodeName == nodeName) {
                    return prtNode;
                }
            }
        }
        return null;
    }

    createRelatedField() {
        return [
            {
                label: 'Claim',
                value: this.claimNumber
            }
        ];
    }

    /**
     * Applies pre-selected filters to subtab table
     * and CSS from utility commonstyles file
     * after DOM is rendered
     */
    renderedCallback() {
       Promise.all([
            loadStyle(this, loggingcss + '/CRM_Assets/styles/logging.css')
        ]).catch((error) => {
            console.log('Error Occured', error);
        });
    }


    handleLogging1(event) {
       
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

    openCauseCodeMentorUrl()
    {
        getCauseCodeCustomSettingDetails()
        .then(result => {
            if(result != null && result != ''){
                this.url = result;
                this[NavigationMixin.Navigate]({
                type: "standard__webPage",
                attributes: {
                    url: this.url
                }
                });
            }
        })
    }

  openMentorCodeMentorUrl(event)
    {

      this.mentorcode= event.target.dataset.value;
      let eleID = event.currentTarget.getAttribute('id');
      if(eleID.includes('POTCode')){  
      this.filtercode = 'Place Of Trtmnt';
      }
      if(eleID.includes('Modifier')){
        this.filtercode = 'CPT/HCPCModifier';
      }
      redirectMentorDocument({strLinkName : '',
        sMentorCode : this.mentorcode, sFilterCodeSet : this.filtercode})
        .then(result => {
          if(result.length != 0 &&  result != ' '){
                this.url = result;
                this[NavigationMixin.Navigate]({
                type: "standard__webPage",
                attributes: {
                    url: this.url
                }
                });
            }
            else{

              var sMsg =  'Mentor Document not available.';
              this.showToast("", sMsg, "warning","sticky");

 
            }
        })
    }

    OpenClaimInquiryTool() 
    {
    var mapForm = document.createElement("form");
    mapForm.method = "POST";
    mapForm.action = this.ClaimInquiryToolURL;
    mapForm.target = "_blank";

        var mapInput = document.createElement("input");
        mapInput.type = "hidden";
        mapInput.name = 'ClaimNbr';
        mapInput.value = this.claimNumber;
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
      showToast(strTitle, strMessage, strStyle, strMode) {
      this.dispatchEvent(
          new ShowToastEvent({
              title: strTitle,
              message: strMessage,
              variant: strStyle,
              mode: strMode
          })
      );
  }
   
}