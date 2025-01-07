/*******************************************************************************************************************************
LWC JS Name : memberIdCardsPolicyMember.js
Function    : This JS serves as controller to memberIdCardsPolicyMember.html. 

Modification Log: 
Developer Name           Code Review                      Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Vamshi Krishna Pemberthi                              02/09/2022                    initial version(azure # )
* Vamshi Krishna Pemberthi                              03/25/2022                    User Story 3201377: ID cards adding Product Type Code to the policy tabs within history/ordering screen
* Apurva Urkude						                              02/03/2023                    UserStory 4100199: Lightning- MCD- Not able to order ID Card for OH Medicaid Pending Plan Group 325240
* Anuradha Gajbhe                                       04/03/2023                    User Story 4400042: Lightning - Contact Servicing: Contract Protected Data (CPD)/SDUP Gaps.
* Raj Paliwal                                           04/06/2023                    Defect Fix: 7493.
* Sagar G                                               07/05/2023                    User Story 4739984: T1PRJ0865978 - MF 4374416 - C10; Contact Servicing: Switch Clean-up for US 4400042
* Anuradha Gajbhe                                       07/14/2023                    US: 4325820: RCC Auto create Case for State ID Cards: Ability to automatically create a case when a State ID Card Request is successfully submitted.(Lightning)
* Raj Paliwal	                                          07/14/2023                    US: 4272710: Ability to request a State ID Card from the ID Card Managment Page(Lightning)
* Anuradha Gajbhe                                       07/28/2023                    US: 4826980: INC2411079- MF 4743214 - Go Live Incident resolve Quicklink data bleeding RAID 87.
* Disha Dhole                                           08/10/2023                    USer Story 4791169 CarePlus - Lightning - Restrict the ability to resend CarePlus Dental ID Cards
*********************************************************************************************************************************/
import { LightningElement, track, api } from 'lwc';
import getPoliciesMed from '@salesforce/apex/MemberIdCards_LWC_LD_HUM.getPoliciesMed';
import getPoliciesDen from '@salesforce/apex/MemberIdCards_LWC_LD_HUM.getPoliciesDen';
import DentalCarePlusMTVGrpNumResLst from '@salesforce/apex/MemberIdCards_LWC_LD_HUM.DentalCarePlusMTVGrpNumResLst';
import ID_Card_Security_Info_Msg from '@salesforce/label/c.ID_Card_Security_Info_Msg';
import ID_Card_Pending_MemberPlans from '@salesforce/label/c.ID_Card_Pending_MemberPlans';
import { ShowToastEvent } from 'lightning/platformShowToastEvent';
import { invokeWorkspaceAPI,openSubTabLinkFramework } from 'c/workSpaceUtilityComponentHum';
import { getLabels } from 'c/crmUtilityHum';
export default class MemberIdCardsPolicyMember extends LightningElement {
  label = {
    ID_Card_Pending_MemberPlans,
    ID_Card_Security_Info_Msg
};
    @api errorMsg;
    @api tabId;
    @track labels = getLabels();
    @track accPolicyList;
    @api recordId;
    @api sRecordId;
    @api sProductType;
    @api error;
    @api medPolicy;
    @api denPolicy;
    @track policyCount;
    @track tempList;
    @track filterObj = {};
    @track tmp = [];
    @api policiesToFetch = 'Member';
    @track nameOfScreen;
    @track pillFilterValues = [];
    @api bMedPolicy = false;
    @api bDenPolicy =false;
    @track accPolicy = [];
    @track accPolicyTmp = [];
    @track oIdCardsLayout;
    @track stateIdCardData;
    @track bStateIdCardFound = false;
    @track screentype = {
      'member':
      {
        'filterFieldApi': ['Status__c', 'Product__c'],
        'nameOfScreen': 'accountdetailpolicy'
      },
      'group':
      {
        'filterFieldApi': ['Plan_Status__c', 'Product__c'],
        'nameOfScreen': 'groupAccountPolicies'
      }
    };
    @api noActMemMsg = '';
    
    @track filterFldValues = {};
    @track getMemberSection;
    @track grpNumRestrictLst = [];
    
    @track checked = false;
    changeToggle(event){
        this.checked = !this.checked;
    }
    
    connectedCallback() {
      this.recordId = this.sRecordId;
      this.noActMemMsg = 'No Data Available';
      if(this.sProductType =='2')
      {
        this.bDenPolicy = true;
        this.methodToCall = getPoliciesDen ;
      }
      else 
      {
        this.bMedPolicy = true;
        this.methodToCall = getPoliciesMed ;
      }
      this.getDentalRestrictedData();
      this.getPoliciesData();
    }

    getDentalRestrictedData(){
      DentalCarePlusMTVGrpNumResLst()
        .then((data) => {
          if(data){
            this.grpNumRestrictLst = JSON.stringify(data);
          }
        })
        .catch((e) => {
          console.log('Exception' + e);
        });
    }
    
     getPoliciesData() {
      this.recordId = this.sRecordId;
      this.methodToCall({ recId: this.recordId })
        .then(result => {
          if (result.lstMemberPlans && result.lstMemberPlans.length > 0) {
	            const accessMap = new Map();
	            result.lstMemberPlans.forEach((element) => {
                 accessMap.set(element.Id , result.mapRecordAccess[result.mapPolicyPlans[element.Id]])
                });

                let userHasViewAccess;
                this.lstFilteredMemberPlans = [];
                result.lstMemberPlans.forEach((ele) => {
                    userHasViewAccess = accessMap.get(ele.Id);
                    if(userHasViewAccess == true){
                      this.lstFilteredMemberPlans.push(ele);
                    }
                  });
                 if (((this.lstFilteredMemberPlans && this.lstFilteredMemberPlans.length > 0) && (this.lstFilteredMemberPlans.length < result.lstMemberPlans.length))|| (result.lstMemberPlans.length > 0 && this.lstFilteredMemberPlans == 0)){
                    this.showSecurityNotification();
                 }
              let accPolicyTemp = [];
              accPolicyTemp = this.lstFilteredMemberPlans;
              accPolicyTemp = [...this.lstFilteredMemberPlans].map(record => {
                      record.tabName = record.Name + '-' + record.Product_Type__c;
	      	            record.bStateIdCardFound = false;
                      return record;
              });

              this.accPolicyTmp = [];
              this.accPolicyTmp = JSON.parse(JSON.stringify(accPolicyTemp));
              let accPolicyTemp1 = accPolicyTemp.forEach((element, index) => {
                if(element.Issue_State__c.toUpperCase() === 'LA' &&  element.Product_Type__c=== 'MCD'){
                    this.accPolicyTmp.push(element);
                    this.newindex = this.accPolicyTmp.length - 1;
                    this.accPolicyTmp[this.newindex].tabName = 'State ID Card';
                    this.accPolicyTmp[this.newindex].bStateIdCardFound = true;  
                  }
                
              });
              this.accPolicyTmp.forEach(ele => {
                let item = {};
                let CarePlusOHCount= 0;
                if(!this.grpNumRestrictLst.includes(ele.GroupNumber)){
                  item = ele;
                  this.accPolicy.push(ele);
                }
                else if(this.grpNumRestrictLst.includes(ele.GroupNumber)){
                  CarePlusOHCount++;
                  if (this.accPolicyTmp.length == CarePlusOHCount){
                    this.noActMemMsg ='No Data Available';
                  } 
                  if(ele.GroupNumber == ID_Card_Pending_MemberPlans){ 
                    this.showOHPendingPlanNotification();
                  } 
                }
              }); 
              if(result.bIdCardPCPTabSwitch){
                if(this.accPolicy != null){
                  this.accPolicy.forEach(record=>{
                    if((record.Product_Type__c === 'MER' || record.Product_Type__c === 'MCD' || record.Product_Type__c === 'MGR' ) && (record.bStateIdCardFound == false)){
			                openSubTabLinkFramework('lwc?primaryCarePhysicianHum?Id='+record.Id,'PCP','standard:account',this.tabId,false);
                    }
                  });
                }
              }
              this.accPolicy.forEach(a=>{
                if (a.Plan != null || a.Plan != undefined){
                  if (a.Plan.Payer != null || a.Plan.Payer != undefined){
                    a.Plan.Payer.Source_Customer_Key__c?a.Plan.Payer.Source_Customer_Key__c : a.Plan.Payer.Source_Customer_Key__c = ' '; 
                  }else {
                    a.Plan = {};
                    a.Plan.Payer = {};
                    a.plan.Payer.Source_Customer_Key__c = ' ';
                  }
                }else {
                  a.Plan = {};
                  a.Plan.Payer = {};
                  a.Plan.Payer.Source_Customer_Key__c = ' ';
                }
              }); 
          }else if (result && result.length > 0) {
            let accPolicyTmp = [];
            accPolicyTmp = result;

            accPolicyTmp = [...result].map(record => {
                    record.tabName = record.Name + '-' + record.Product_Type__c;
                    return record;
            });

            let hoCount = 0;
            accPolicyTmp.forEach(ele => {
              let item = {};
              if(ele.GroupNumber != ID_Card_Pending_MemberPlans){
                item = ele;
                this.accPolicy.push(item);

              }else if(ele.GroupNumber == ID_Card_Pending_MemberPlans){
                hoCount++;
                if (accPolicyTmp.length == hoCount){           
                  this.noActMemMsg ='No Data Available';
              } 
              this.showOHPendingPlanNotification();
              }
            }); 
	          this.accPolicy.forEach(a=>{
              if (a.Plan != null || a.Plan != undefined){
                if (a.Plan.Payer != null || a.Plan.Payer != undefined){
                  a.Plan.Payer.Source_Customer_Key__c?a.Plan.Payer.Source_Customer_Key__c : a.Plan.Payer.Source_Customer_Key__c = ' '; 
                }else {
                  a.Plan = {};
                  a.Plan.Payer = {};
                  a.plan.Payer.Source_Customer_Key__c = ' ';
                }
              }else {
                a.Plan = {};
                a.Plan.Payer = {};
                a.Plan.Payer.Source_Customer_Key__c = ' ';
              }
            }); 
          }
          else {
            this.errorMsg ='No records found';
          }
        if(this.accPolicy.length==0)
          {
            this.accPolicy=false;
          }
        })
        .catch(error => {
          console.log('Error', error);
        })
    }
  

    handleActive(event){
        let polObj;
        polObj = event.currentTarget.dataset.segment ;
    }
  
    showOHPendingPlanNotification(){
      const event = new ShowToastEvent({
          message: 'ID Cards are not available for OH Medicaid Pending Plan Group #325240',
          variant: 'error',
      });
      this.dispatchEvent(event);
    }
    
    showSecurityNotification(){
      const event = new ShowToastEvent({
          message: ID_Card_Security_Info_Msg,
          variant: 'warning',
      });
      this.dispatchEvent(event);
    }
  
}