/*******************************************************************************************************************************
LWC JS Name : standardTableComponentHum/standardTableHelpers.js
Function    : This file contains pure helper functions for standardTableComponentHum component
Modification Log: 
Developer Name           Code Review              Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Mohan Kumar N                                   02/03/2021                 Adding Chat transcript model
* Joel George                                     03/05/2021                 Added logic to navigate to chat detail
* Arpit Jain/Navajit Sarkar                       03/10/2021                 Call Transfer button changes
* Ritik Agarwal                                   03/20/2021                 button disabled for legacy delete
* Supriya Shastri                                 07/02/2021                 US-2172712
* Mohan Kumar N                                    09/01/2021                US: 2160764 - Configure table column width
* Swapnali Sonawane                               05/27/2022                 US-3143662 - Medical Plan - Benefit Accumulators
* Vardhman Jain                                   12/22/2022                 US3879280: Lightning - Consumer/Implement for CPD/General Search & Person Account Page
* Deepak Khandelwal                               16/02/2023                 US4146763: Lightning - OSB Medicare, OSB Vision and Fitness-ODS Feed
 * Nirmal Garg                                    02/21/2023                 US4304352 changes
 * Vardhman Jain                                  16/08/2023                 US4813842:T1PRJ1097507- MF26942 - C01/Account Management/Pharmacy - Person Account- Block Access to Non-Medical Plans
*********************************************************************************************************************************/

import { memberSearch, groupSearch, casehistory, policies, accountdetailpolicy,
  grouppolicies, interactions,groupAccountPolicies, providerResults,
  providerInteractions, purchaserplan, casehistoryaccordian, providerOpenCases, consumerIds, agencyResults,
  chattranscript, mtvremarks, accumsModel } from './standardTableModels';

const getValue = (record, mapping) => {
let value;
if(mapping.includes('.')){ 

const mapDepth = mapping.split('.');
value = record;
mapDepth.forEach(item => {
value = value ? value[item] : ''
});
}
else{
value = record[mapping];
}
return value;
}


export const getHelpText = (screenType, labels) => {
let helpText = '';
switch(screenType){
case 'Member Search': 
helpText = labels.searchResultsMemberHelptext;
break;
case 'Group Search': 
helpText = labels.searchResultsHelptext;
break;
case 'providerResutls': 
helpText = labels.searchResultsProviderHelptext;
break;
case 'agencyResults': 
helpText = labels.searchResultsAgencyHelptext;
break;
}
return helpText;
}

/**
* Apply in line stlye only to configure the table columns width
* @param {*} aModel 
*/
export const applyStyles = (aModel) => {
return [aModel[0].map(item => {
return {
...item,
styles: item.width ? `flex: 0 0 ${item.width}`: '' // apply dynamic widths
}
})]
}
/**
* Populates data into model and creates records
* @param {*} model Model Array which needs to be used for data population
* @param {*} dataArr Data Array
*/
export const populatedDataIntoModel = (model, dataArr) =>{
let temp1 = [];
let tempdarry = [];
let updatedModel = applyStyles(model);
dataArr.forEach((dataRecord) => {
updatedModel.forEach((modelRecord) => {
temp1 = [];
let rowCss = "";
modelRecord.forEach((v) => {
  if(v.compoundx){
    v.compoundvalue.forEach((compv) => {
      compv.value = getValue(dataRecord,compv.fieldName);	  
      if(compv.hasOwnProperty('isOSB')){  
        compv.isOSB = dataRecord['isOSB'];   
      }
	  if (compv.hasOwnProperty('isLock')) { 
		compv.isLocked = dataRecord[compv.isLock];  
		}
	  if(compv.hasOwnProperty('isHpuc')){  
      compv.isHpu = dataRecord[compv.isHpuc];  
        }
      if (compv.hasOwnProperty('link')) {  
          compv.link = (dataRecord.hasOwnProperty(compv.hasOwnProperty('linkToChange') ? compv.linkToChange : '')) ? dataRecord[compv.linkToChange] : compv.link;
          if (dataRecord.hasOwnProperty('disable') && compv?.fieldName === 'hpLink' && dataRecord?.hpLink?.includes('XX')) {
              compv.disable = dataRecord.hasOwnProperty('disable') ? dataRecord['disable'] : false;
          }
      } if(compv.link && compv.value)
      {
          let navItem = compv.value.split('#&;');
          compv.value = navItem[0];
          compv.navToItem = navItem[1];
      }
      if(compv.customFunc === 'CASE_CREATION' && dataRecord.isOpenedInTwoWeeks){
        rowCss = 'results-row-highlight';
      } 
    });
  }
  else{
    if (v.fieldName) {
      v.value = getValue(dataRecord,v.fieldName);
    }
  }
  if (v.button) {
    v.compoundvalue.forEach((compv) => {
      if (compv.hasOwnProperty('rowData')) {
        compv.rowData = Object.assign(compv.rowData, dataRecord);
      }
      compv.hasOwnProperty('buttondisabled') ? compv.buttondisabled= getValue(dataRecord, compv.fieldName) :'';
    });
  }
   if (v.isIcon) {
    if (v.hasOwnProperty('isLock')) { 
		v.isLocked = dataRecord[v.isLock];
		}
	}
  if(v.isCheckbox){
    v.isLink = dataRecord.isLink;
  }
  if(v.radio){
    v.checked = dataRecord.checked;
    v.isLocked = dataRecord.isLocked;
	v.sLockMessage = dataRecord.sLockMessage;
    v.disabled = dataRecord.disabled;
  }
  else if(v.isViewAll){
    v.filteredList = dataRecord.filteredList;
    v.actualList = dataRecord.actualList;
    v.showViewAll = dataRecord.showViewAll;
  }
  v.customCss = `results-table-cell  ${v.customCss}`;   
});
let record = JSON.parse(JSON.stringify(modelRecord));
record.rowCss = rowCss;
temp1.push(record);      
});    
tempdarry.push(temp1);
});
return tempdarry;
}

/**
* Returns meta data for the given table tpe
* @param {*} selectedScreen 
*/
export const getTableModal = (selectedScreen) => {
let model;
switch(selectedScreen){
case 'MTV Remark':
model = mtvremarks;
break;
case 'Member Search': 
model = memberSearch;
break;
case 'Group Search': 
model = groupSearch;
break;
case 'Case Search': 
model = casehistory;
break;
case 'Case Search Accordian': 
model = casehistoryaccordian;
break;
case 'purchaserplan':
model = purchaserplan;
break;
case 'Policy': 
model = policies;
break;
case 'accountdetailpolicy': 
model = accountdetailpolicy;
break;
case 'grouppolicies':
model = grouppolicies;
break;
case 'Interactions':
model = interactions;
break;
case 'groupAccountPolicies':
model = groupAccountPolicies;
break;
case 'providerResutls':
model = providerResults;
break;
case 'providerInteractions':
model = providerInteractions;
break;
case 'Consumer Table':
model = consumerIds;
break;
case 'providerOpenCases':
model = providerOpenCases;
break;
case 'agencyResults':
model = agencyResults;
break;
case 'chattranscript':
model = chattranscript;
break;
case 'Benefits - Accums':
model = accumsModel;
break;
default:''
}
return model;
}

export const compute = (selectedScreen, dataArr) => {
return populatedDataIntoModel(getTableModal(selectedScreen), dataArr); 
};