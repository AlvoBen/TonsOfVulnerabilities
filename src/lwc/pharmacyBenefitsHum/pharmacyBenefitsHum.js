/*
JS Controller        : PharmacyBenefitsHum

Modification Log: 
* Developer Name                    Date                         Description
* Jonathan Dickinson                11/19/2021                   US-2721470-Original version
* Kalyani Pachpol                   06/30/2022                   3377044
* Kalyani Pachpol                   11/04/2022                   US-3578534
* Pinky Vijur                       02/03/2023                   User Story 4173330: T1PRJ0870026 Solutran Integrations - CRM FR1.01 - Pharmacy OTC Benefit Snapshot - Filter out Benefit Type - 'Flex Card Benefit'
* Tharun M                          02/03/2023                   User Story 4138214: T1PRJ0870026 Solutran Integrations - CRM FR2.01 - Update RxNova Link to Auto - Prefill SSO and Member's Details
*------------------------------------------------------------------------------------------------------------------------------
*/
import { LightningElement, api, track} from 'lwc';
import getServiceURL from '@salesforce/apex/PharmacyBenefits_LC_HUM.getServiceURL'; 
import getSoltrnOTCBalance from '@salesforce/apexContinuation/PharmacyBenefits_LC_HUM.getSoltrnOTCBalanceV3';
import { getLocaleDate, sortTable} from 'c/crmUtilityHum';
import getPolicyList from '@salesforce/apex/PharmacyBenefits_LC_HUM.getPolicyMemberData'; 

const columns= [
		
      {
        label: 'Benefit Type',
        fieldName: 'benefittype',
        type: 'text'
      },
      {
        label: 'Available Balance',
        fieldName: 'availablebalance',
        type: 'currency',
        cellAttributes: { alignment: 'left' }
      },
      {
        label: 'Last 4 Digits',
        fieldName: 'last4digits',
        type: 'text'
      },
      {
        label: 'Status',
        fieldName: 'status',
        type: 'text'
      },
	  {	
        label: 'Vendor',
        fieldName: 'vendor',
        type: 'url',
        typeAttributes: {
            label: {fieldName: 'vendorLabel'},
            target: '_blank'
        }
      }
    ];

export default class PharmacyBenefitsHum extends LightningElement {

    serviceName;
    serviceURL;
    serviceURLS = {};
    policyMemberId;
    memberId;
    pbpcode;
    contractnumber;
    proddescription;
    dateOfB;

    @api
    accountId;

    @api
    pharmacyMemberDetails 

    columns = columns;
    data = [];
    @track totalRecords =[];
    

   async connectedCallback() {
		
        await this.getPolicyDetails();
        await this.getURLs();
        await this.balanceResponcemethod();       
    }
      async getURLs() {
          await getServiceURL({ sMemberid: this.memberId, dateOfB: this.dateOfB })
            .then(result => {
                this.serviceURLS = JSON.parse(result);
            })
            .catch(error => {
                console.log(`There was an error retrieving the ${this.serviceName} URL: ${error}`);
            });
        }

    async getPolicyDetails(){
       await getPolicyList({ sAccId : this.accountId}) 
                .then(result => {
                        let policyrecords = JSON.parse(JSON.stringify(result));
                        policyrecords.forEach(function (item) {
                          item.EffectiveFrom = getLocaleDate(item.EffectiveFrom);
                          item.EffectiveTo = getLocaleDate(item.EffectiveTo);
                          item.PlanName = (item.Plan && item.Plan.iab_description__c ) ? item.Plan.iab_description__c  : '';
                          item.MedicareId__c = item.Member.MedicareId__c;
                          item.Name = item.Member_Id_Base__c ? item.Member_Id_Base__c : item.Name;
                          item.Policy__r.PBP_Code__c = item.Policy__r.PBP_Code__c != null ? item.Policy__r.PBP_Code__c : '';
                          item.Policy__r.Contract_Number__c = item.Policy__r.Contract_Number__c != null ? item.Policy__r.Contract_Number__c : '';
                          item.Policy__r.Product_Description__c = item.Policy__r.Product_Description__c != null ? item.Policy__r.Product_Description__c : '';
                          item.Member.Birthdate__c = item.Member.Birthdate__c != null ? item.Member.Birthdate__c : '';
                        });
                        this.policyList = sortTable(policyrecords,'Member_Coverage_Status__c','Product__c');
                        console.log("List",this.policyList);
                        if (this.policyList.length > 0) {
                            this.policyMemberId = this.policyList[0].Id;
                            this.memberId = this.policyList[0].Name;
                            this.pbpcode = this.policyList[0].Policy__r.PBP_Code__c;
                            this.contractnumber = this.policyList[0].Policy__r.Contract_Number__c;
                            this.proddescription = this.policyList[0].Policy__r.Product_Description__c;
                            this.dateOfB = this.policyList[0].Member.Birthdate__c;
                        }
                      }            
                    )
                .catch(error => {
                    console.log("Error Occured", error);
                });  
      }
      getUniqueId() {
        return Math.random().toString(16).slice(2);
      }
       async balanceResponcemethod()
      {
           await  getSoltrnOTCBalance({sMemberid: this.memberId, sPBPCode: this.pbpcode, sContractNumber : this.contractnumber, sProductDescription : this.proddescription})
            .then(result => {
                let responseOTC = JSON.parse(result);
          let resObject = [];
          let balanceresponse = responseOTC.BalanceInformation;
          let finalresponse = balanceresponse.Balance;
          if(finalresponse.length>0)
          {
              finalresponse.forEach((o, index) => {
                  if (o.BalanceType != 'Flex Card Benefit') {
                      if (o.Vendor) {
                          let itemObject = {};
                          itemObject.Id = this.getUniqueId();
                          if (o.Vendor == 'InComm') {
                              this.serviceURL = this.serviceURLS != null && this.serviceURLS["InComm"] != null ? this.serviceURLS["InComm"] : '';
                              itemObject.vendor = this.serviceURL;
                              itemObject.vendorLabel = 'InComm';
                          }
                          if (o.Vendor == 'Solutran') {
                              this.serviceURL = this.serviceURLS != null && this.serviceURLS["Solutran"] != null ? this.serviceURLS["Solutran"] : '';
                              itemObject.vendor = this.serviceURL;
                              itemObject.vendorLabel = 'Solutran';
                              itemObject.last4digits = o.Cards[0].Last4;
                              itemObject.status = o.Cards[0].Status;
                          }
                          if (o.Vendor == 'SS&C') {
                              this.serviceURL = this.serviceURLS != null && this.serviceURLS["RxNova"] != null ? this.serviceURLS["RxNova"] : '';
                              itemObject.vendor = this.serviceURL;
                              itemObject.vendorLabel = 'RxNova';
                          }
                          itemObject.benefittype = o.BalanceType;
                          itemObject.availablebalance = o.AvailableBalance;
                          resObject.push(itemObject);
                      }
                      else {
                          console.log('Vendor is not present');
                      }
                  }              
          });
        }
        this.totalRecords= resObject;
        return new Promise((resolve) => resolve(''));  
            })
            .catch(error => {
                console.log('There was an error retrieving the  data: ' + error);
            })
      }
}