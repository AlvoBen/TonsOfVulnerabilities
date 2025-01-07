/*******************************************************************************************************************************
LWC JS Name : providerSearchTablesHum.js
Function    : This JS serves as controller to providerSearchTablesHum.html. 

Modification Log: 
Developer Name           Code Review                      Date                         Description
*--------------------------------------------------------------------------------------------------------------------------------
* Pradeep Dani                                          12/18/2020                    initial version
* Ashish Kumar                                          03/24/2021                    Changes related to Provider Search Speciality Changes
* Saikumar Boga                                         06/14/2021                    Changes Related to Unknown Provider Form
* Kajal Namdev/Vardhman Jain                            05/25/2021                    US-3334446-Added changes to solve the blank table issue for 
*********************************************************************************************************************************/
import { LightningElement, api, wire, track } from 'lwc';
import startRequest from '@salesforce/apexContinuation/ProviderSearch_LC_HUM.search';
import startRequestUnknown from '@salesforce/apex/ProviderSearch_LC_HUM.searchUnknown';
import createRequestUnknown from '@salesforce/apex/ProviderSearch_LC_HUM.createUnknownProviderSearch';
import interactionList from '@salesforce/apex/ProviderSearch_LC_HUM.getInteractionList';
import caseList from '@salesforce/apex/ProviderSearch_LC_HUM.getAccountCaseList';
import upsertProviderAccount from '@salesforce/apex/ProviderSearch_LC_HUM.insertProviderAccount';
import { getLocaleDate, hcConstants, hasSystemToolbar } from "c/crmUtilityHum";
import { getLabels } from 'c/customLabelsHum';
import { CurrentPageReference } from 'lightning/navigation';
import hasCRMS_685_PCC_Customer_Service_Access from '@salesforce/customPermission/CRMS_685_PCC_Customer_Service_Access';
import { getProviderSearchLayout } from './providerSearchTableModels';

export default class ProviderSearchTables extends LightningElement {
    accountList;

    @api showinteractioncases = false;
    @track isDataLoaded = false;
    @track noResults = false;
    @track isInteractionsLoaded = false;
    @track isCasesLoaded = false;
    @track interactionsData = [];
    @track casesData = [];
    @track labels = getLabels();
    @track resultreceived;
    @track searchResultLayout;
    @track bProvider = false;
    @track isShowBackToResults = false;
    @track bShowTable = true;

    @wire(CurrentPageReference) pageRef;

    connectedCallback() {
        this.bProvider = hasCRMS_685_PCC_Customer_Service_Access ? true : false;
    }

    backToResults() {
        this.isShowBackToResults = false;
        this.isCasesLoaded = false;
        this.isInteractionsLoaded = false;
        this.template.querySelector('[data-id="p-search-results"]').backToResult();
    }

    @api
    resetResults() {
        const me = this;
        me.resultreceived = null;
        me.bShowTable = false;
        me.isCasesLoaded = false;
        me.isInteractionsLoaded = false;
        me.isShowBackToResults = false;
    }

    get containerCss(){
        return hasSystemToolbar ? 'searchpage-results-system': 'searchpage-results'
    }

    @api
    handleProviderSearchEvent(oFormData) {
        this.formDataRecieved = oFormData;
        this.searchResultLayout = getProviderSearchLayout(oFormData);
        if (this.formDataRecieved && !this.formDataRecieved.sUnknownProviderSearch) {
            startRequest({ searchFormData: oFormData })
                .then(result => {
                    this.accountList = JSON.parse(result).map(item => {
                        const taxId = item.sTaxID ? item.sTaxID.split(',') : [];
                        let filteredList = taxId;
                        const showViewAll = taxId.length > 2;
                        if (showViewAll) {
                            filteredList = [taxId[0], taxId[1]];
                        }
                        return {
                            ...item,
                            filteredList,
                            actualList: taxId.join("</br>"),
                            showViewAll
                        }
                    });

                    this.accountList= oFormData.sSpeciality && oFormData.sSpeciality!==hcConstants.OPTION_NONE && oFormData.sSpeciality!= undefined ? this.orderListBasedOnCondition(this.accountList,this.splitSelectedSpeciality(oFormData.sSpeciality, ['/',','])) : this.accountList;
                    
                    if (this.accountList.length < 1) {
                        this.noResults = true;
                        this.isDataLoaded = false;
                    } else {
                        this.noResults = false;
                        this.isDataLoaded = true;
                        this.resultreceived = this.accountList;
                    }
                    this.bShowTable = true;
                }).catch(error => {
                    console.log('Error Occured', error);
                });
        }
        else if (this.formDataRecieved && this.formDataRecieved.sUnknownProviderSearch) {
            startRequestUnknown({ searchFormData: oFormData }).then(result => {
                this.accountList = JSON.parse(result).map(item => {
                    const taxId = item.sTaxID ? item.sTaxID.split(',') : [];
                    let filteredList = taxId;
                    const showViewAll = taxId.length > 2;
                    if (showViewAll) {
                        filteredList = [taxId[0], taxId[1]];
                    }
					 if(!item.sExtID){
                            item.sExtID = item.sMemberId;
                        }
                    return {
                        ...item,
                        filteredList,
                        actualList: taxId.join('\n'),
                        showViewAll
                    }
                });
                this.accountList= oFormData.sSpeciality && oFormData.sSpeciality!==hcConstants.OPTION_NONE && oFormData.sSpeciality!= undefined ? this.orderListBasedOnCondition(this.accountList,this.splitSelectedSpeciality(oFormData.sSpeciality, ['/',','])) : this.accountList;

                if (this.accountList.length < 1) {
                    this.noResults = true;
                    this.isDataLoaded = false;
                } else {  
                    this.noResults = false;
                    this.resultreceived = this.accountList;
                    this.isDataLoaded = true;
                }
                this.bShowTable = true;
            }).catch(error => {
                console.log('Error Occured', error);
            });
        }
    }

	
    @api     /* Search Method for UnknownProviderForm Account */
    createProviderSearchEvent(oFormData) {
        this.formDataRecieved = oFormData;
        this.searchResultLayout = getProviderSearchLayout(oFormData);
        if (this.formDataRecieved) {
            createRequestUnknown({ searchFormData: oFormData })
                .then(result => {
                    this.accountList = JSON.parse(result).map(item => {
                        const taxId = item.sTaxID ? item.sTaxID.split(',') : [];
                    let filteredList = taxId;
                    const showViewAll = taxId.length > 2;
                    if (showViewAll) {
                        filteredList = [taxId[0], taxId[1]];
                    }
                    return {
                        ...item,
                        filteredList,
                        actualList: taxId.join('\n'),
                        showViewAll
                    }
                 });
                 
                 this.accountList= oFormData.sSpeciality && oFormData.sSpeciality!==hcConstants.OPTION_NONE && oFormData.sSpeciality!= undefined ? this.orderListBasedOnCondition(this.accountList,this.splitSelectedSpeciality(oFormData.sSpeciality, ['/',','])) : this.accountList;
                   
                      if (this.accountList.length < 1) {
                        this.noResults = true;
                        this.isDataLoaded = false;
                    } else {
                        this.noResults = false;
                        this.isDataLoaded = true;
                        this.resultreceived = this.accountList;
                    }
                    this.bShowTable = true;
                }).catch(error => {
                    console.log('Error Occured', error);
                });
        }
       
    }

    /**
    * Description - method to split a string based on the the list of delimiter provided
    */
    splitSelectedSpeciality(searchSelectedParam, searchSplitCharList) {   
        let listOfSpecialityToSearch= [searchSelectedParam];
        for(let i=0; i<searchSplitCharList.length; i++)
        {   
            if(searchSelectedParam.includes(searchSplitCharList[i]))
            {   
                listOfSpecialityToSearch = searchSelectedParam.split(searchSplitCharList[i]);
                break;
            }
        }
        return listOfSpecialityToSearch; 
    }

    /**
    * Description - method to order the list of speciality by putting the found specilaity on the top of the results
    */
    orderListBasedOnCondition(accList, listOfSpecialityToSearch) {   
        let specialityMatch=[];
        let specialityNotMatching=[];
        accList.forEach(item=>{
            let conditionMet = false;
            for(let i=0; i<listOfSpecialityToSearch.length; i++){
                let strval = listOfSpecialityToSearch[i].length >=4 ? listOfSpecialityToSearch[i].trim().substring(0,4) : listOfSpecialityToSearch[i];
                if(item.sSpeciality && item.sSpeciality.includes(strval))
                {
                 specialityMatch.push(item);
                 conditionMet=true;
                 break;
                }
            }
        !conditionMet ? specialityNotMatching.push(item): '';
        });

        return [...specialityMatch,...specialityNotMatching];
    }

    hideOrShowBcackToResults(aData) {
        this.isShowBackToResults = aData.length > 1 ? true : false;
    }

    loadInteractionsData(accntId) {
        interactionList({ accountId: accntId }).then(result => {
            if (result != null && result != undefined) {
                var interactList = [];
                result.forEach(interactionObj => {
                    let sIntAbout = '';
                    if (interactionObj.Interaction_Members__r && interactionObj.Interaction_Members__r.length > 0) {
                        interactionObj.Interaction_Members__r.forEach(element => {
                            sIntAbout += ((element.Interacting_About__r && element.Interacting_About__r.Name) ? element.Interacting_About__r.Name : '') + ', ';
                        });
                    }
                    var tempObj = {
                        ...interactionObj,
                        interactionWith: interactionObj.Interacting_With__r.Name,
                        interactionAbout: sIntAbout.substring(0, sIntAbout.length - 2),
                        modifiedDate: getLocaleDate(interactionObj.LastModifiedDate)
                    };
                    interactList.push(tempObj);
                });
                this.interactionsData = interactList;
            }
            this.isInteractionsLoaded = true;
        }).catch(error => {
            console.log('Error Occured', error);
        });
    }

    loadCasesData(accntId) {
        caseList({ accountId: accntId }).then(result => {
            if (result != null && result != undefined) {
                var interactCasesList = [];
                result.forEach(caseObj => {
                    var tempObj = {
                        urlCaseId: caseObj.CaseNumber,
                        caseStatus: caseObj.Status,
                        openCaseDate: getLocaleDate(caseObj.CreatedDate)
                    };
                    if (caseObj.CTCI_List__r) {
                        tempObj = {
                            ...tempObj,
                            caseClassification: caseObj.CTCI_List__r.Classification__r.Name,
                            caseIntention: caseObj.CTCI_List__r.Intent__r.Name,
                        }
                    }
                    interactCasesList.push(tempObj);
                });
                this.casesData = interactCasesList;
            }
            this.isCasesLoaded = true;
        }).catch(error => {
            console.log('Error Occured', error);
        });;
    }

    handleRecordSelection(event) {
        const me = this;
        me.hideOrShowBcackToResults(this.resultreceived);
        if (this.bProvider) {
            const recUniqueId = event.detail.Id;
            const selectedrow1 = me.resultreceived.filter(item => item.sExtID === recUniqueId);
            const selectedrow = selectedrow1[0];
            // upsert provider account on select 
            var accountObjRecord = {
                BillingStreet: selectedrow.sAddress,
                Birthdate__c: selectedrow.sBirthdate,
                RecordTypeId: selectedrow.sPend,
                BillingCity: selectedrow.sCity,
                Provider_Classification__c: selectedrow.sClassification,
                DBA__c: selectedrow.sDBA,
                Degree__c: selectedrow.sDegree,
                Enterprise_ID__c: selectedrow.sEnterpriseID,
                Account_External_ID__c: selectedrow.sExtID,
                Individual_First_Name__c: selectedrow.sFirstName,
                Gender__c: selectedrow.sGender,
                Individual_Last_Name__c: selectedrow.sLastName,
                NPI_ID__c: selectedrow.sNPI,
                Phone: selectedrow.sPhone,
                Phone_Ext__c: selectedrow.sPhoneExtn,
                Source_Platform_Code__c: selectedrow.sPlatform,
                BillingPostalCode: selectedrow.sPostalCode,
                ShippingCity: selectedrow.sServiceCity,
                ShippingStatecode: selectedrow.sServiceState,
                ShippingStreet: selectedrow.sServiceaddress,
                shippingPostalCode: selectedrow.sServicezip,
                Description: selectedrow.sSpeciality,
                BillingStatecode: selectedrow.sState,
                Taxonomy_Code__c: selectedrow.sTaxmonycode
            };

            let accountJSON = JSON.stringify(accountObjRecord);
            let strTaxIds = JSON.stringify(selectedrow.sTaxID);
            upsertProviderAccount({
                consumerIds: strTaxIds,
                accountJson: accountJSON,
                externalId: recUniqueId
            }).then(result => {
            }).catch(error => {
                console.log('Error Occured', error);
            });            
            me.loadInteractionsData(recUniqueId);
            me.loadCasesData(recUniqueId);
        }
    }
}