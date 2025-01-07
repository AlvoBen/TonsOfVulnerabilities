/*******************************************************************************************************************************
LWC JS Name : crmUtilityHum.js
Function    : Utility functions 
Modification Log: 
Developer Name                       Date                         Description
*--------------------------------------------------------------------------------------------------------------------------------
* Mohan Kumar N                     12/18/2020                    initial version
* Mohan Kumar N                     03/03/2021                    Fix for DF-2528
* Mohan Kumar N                     04/16/2021                    Fix for DF-2876
* Mohan Kumar N                     05/18/2021                    US: 2272975- Launch member plan detail
* Gowthami Thota                    11/17/2021                    Fix for DF-4124
* Tummala Vijaya Lakshmi            09/01/2022                    US : 3711686 - Lightning- Case Management- Missing Fields Implementation(Incredibles)
* Prasuna Pattabhi                  09/30/2022                    Added 206 const to user group permissions
* Nirmal Garg                       04/12/2023                    US4460894
*********************************************************************************************************************************/
export { getLabels } from './utils/labelsHum';
import userLocale from '@salesforce/i18n/locale';
import logError from '@salesforce/apex/HUMExceptionHelper.logError';
import hasCRMS_206_CCSHumanaPharmacyAccess from '@salesforce/customPermission/CRMS_206_CCSHumanaPharmacyAccess';
import hasCRMS_684_Medicare_Customer_Service_Access from '@salesforce/customPermission/CRMS_684_Medicare_Customer_Service_Access';
import hasCRMS_630_MedicareElectionTracking_EnrollmentEdit from '@salesforce/userPermission/CRMS_630_MedicareElectionTracking_EnrollmentEdit';
import hasCRMS_605_BizConfig_MedicareEnrollment_Edit from '@salesforce/userPermission/CRMS_605_BizConfig_MedicareEnrollment_Edit';
import hasCRMS_685_PCC_Customer_Service_Access from '@salesforce/customPermission/CRMS_685_PCC_Customer_Service_Access';
import hasCRMS_240_GBO_Segment_Service_Access from '@salesforce/customPermission/CRMS_240_GBO_Segment_Service_Access';
import hasCRMS_205_CCSPDPPharmacyPilot from '@salesforce/customPermission/CRMS_205_CCSPDPPharmacyPilot';
import hasCRMS_302_HPTraditionalInsuranceData from '@salesforce/customPermission/CRMS_302_HPTraditionalInsuranceData';
import hasCRMS_301_HPInsuranceData from '@salesforce/customPermission/CRMS_301_HPInsuranceData';
import hasCRMS_300_Humana_Pharmacy_Supervisor from '@salesforce/customPermission/CRMS_300_Humana_Pharmacy_Supervisor';
import fetchBaseUrl from '@salesforce/apex/PoliciesSearchResults_LC_HUM.getSalesforceBaseUrl';
import isSandboxOrg from '@salesforce/apex/SearchUtilty_H_HUM.isSandboxOrgInfo';
import { ShowToastEvent } from "lightning/platformShowToastEvent";
//export constants
export { hcConstants } from './utils/constantsHum';
import { hcConstants } from './utils/constantsHum';

export { eventKeys } from './utils/keyboard';

/**
 * Format date with the given seperator
 * @param {*} dtValue 
 * @param {*} seperator 
 */
export const getFormattedDate = (dtValue, seperator = "/") => {
  let newDate = dtValue;
  const onlyNumber = new RegExp(/^\d+$/);
  dtValue = dtValue.replace(/[/]/gi, "");
  if (onlyNumber.test(dtValue)) {
    const dtValLength = dtValue.length;
    if (dtValLength === 2) {
      newDate = dtValue + seperator;
    }
    else if (dtValLength === 4) {
      newDate = dtValue.substring(0, 2) + seperator + dtValue.substring(2, 4);
    }
    else if (dtValLength > 4 && dtValLength !== 7) { // Not to consider formatting when remove and add month or date
      newDate = dtValue.substring(0, 2) + seperator + dtValue.substring(2, 4) + seperator + dtValue.substring(4, 8);
    }
  }
  return newDate;
}


/**
 * Returns validate of the date value. True: valid, false: invalid
 * @param {*} dtValue 
 */
export const isDateValid = (dtValue) => {
  return dtValue.match(/^\d{2}\/\d{2}\/\d{4}/) && new Date(dtValue).getTime() ? true : false;
}

/**
 * Deep copy of the object or array
 * @param {any} data 
 */
export const deepCopy = (data) => {
  return JSON.parse(JSON.stringify(data));
}

export const getSessionItem = (key) => {
  return window.sessionStorage.getItem(key);
}

export const setSessionItem = (key, value) => {
  window.sessionStorage.setItem(key, value);
}

export const removeSessionItem = (key) => {
  return window.sessionStorage.removeItem(key)
}

//Method to compare two dates and return returns 1 when Date_arg1 date is greater than the other , else return 0
export const compareDate = (sPrimaryDt, sSecondaryDt)=> {
  return sPrimaryDt && sSecondaryDt ? ((new Date(sPrimaryDt) >= new Date(sSecondaryDt)) ? 1 : 0) : 0;
}

/**
 * Returns two dates difference in days
 * @param {*} date1 
 * @param {*} date2 
 */
export const getDateDiffInDays = (date1, date2) => {
  const date1Format = new Date(date1);
  const date2Format = new Date(date2);
  const diffTime = Math.abs(date1Format - date2Format);
  return Math.ceil(diffTime / (1000 * 60 * 60 * 24));
}

/**
 * Verify given string contains special characters
 * @param {*} field 
 * @param {*} iChars 
 * @return Boolean true: contains special chars, false : does not contain special chars
 */
export function isSpecialCharsExists(strValue, paramSpecialChars) {
  let defaultSpecialChars = '!`@#$%^*()+=[]\\; /{}|":<>?~_';
  if (paramSpecialChars) { // Override when sending special chars as param
    defaultSpecialChars = paramSpecialChars;
  }
  for (let i = 0; i < strValue.length; i++) {
    if (defaultSpecialChars.indexOf(strValue.charAt(i)) != -1) {
      return true;
    }
  }
  return false;
}


/**
 * Returns locale formatted date
 * @param {string} strDate Input date to be formattted
 * @param {Object} options format options supported by native Intl.DateTimeFormat function
 */
export const getLocaleDate = (strDate, options) => {
  let oInpDate = strDate;
  let formatOptions = {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
  }
  if (options) {
    formatOptions = options;
  }
  if(strDate === ""){
    return strDate; // to avoid JS error and show value empty
  }
  if(typeof strDate === 'string') {
    oInpDate = strDate.includes('T') ? new Date(strDate) : new Date(strDate.replace(/-/g, '/'));
  }
  return new Intl.DateTimeFormat(userLocale, formatOptions).format(oInpDate);
}

export const getAllStates = function (data, statevalues) {
  for (let key in data) {
    const opt = { label: key, value: data[key] };
    statevalues = [...statevalues, opt];
  }
}


// this method is used for fetchin picklist values that need to be show on UI for filter part
export const getPickListValues = function (arryOfFieldApi, data) {
  let filterValues = [];
  let fobj = {};
  arryOfFieldApi.forEach(function (fldApiName) {
    data.forEach(function (item) {
      if (item[fldApiName])
        filterValues.push({ label: item[fldApiName], value: item[fldApiName] });
    });
    filterValues.sort((a, b) => (a.value > b.value) ? 1 : ((b.value > a.value) ? -1 : 0));
    let uniqueFilterValues = filterValues.map(JSON.stringify);
    let uniqueSet = new Set(uniqueFilterValues);
    let uniqueArray = Array.from(uniqueSet).map(JSON.parse);
    let filterObj = { [fldApiName]: uniqueArray };
    Object.assign(fobj, filterObj);
    filterValues = [];
  })
  return fobj;
}

export const sortTable = function (data, item1, item2,customSorting = true) {
  let aarr = data.sort(function (a, b) {
    var afirstCol = a[item1];
    var bfirstCol = b[item1];
    var aSecondCol = a[item2];
    var bSecondCol = b[item2];
    if (afirstCol === bfirstCol) {
      let key1 = aSecondCol ? aSecondCol.toUpperCase() : 100;
      let key2 = bSecondCol ? bSecondCol.toUpperCase() : 100;
      let index1, index2;
      if(customSorting){
        let preSortedArray = [hcConstants.MED_POLICY, hcConstants.DEN_POLICY, hcConstants.VIS_POLICY];
        index1 = preSortedArray.indexOf(key1);
        index2 = preSortedArray.indexOf(key2);
        index1 = index1 === -1 ? 100 : index1;
        index2 = index2 === -1 ? 100 : index2;
      }else{
        index1 = key1;
        index2 = key2;
    }
        if (index1 < index2) {
          return -1
        } else if (index1 > index2) {
          return 1;
        } else {
          return 0;
        }
    }

    else {
      return (afirstCol < bfirstCol) ? customSort(a,b,'Enter') : customSort(a,b,'Exit');
    }
  });
  return aarr;
}

export const customSort = function(data0,data1,criteria){
    if(data0.hasOwnProperty('ETL_Record_Deleted__c') && data1.hasOwnProperty('ETL_Record_Deleted__c')){
       if(data0['ETL_Record_Deleted__c'] || data1['ETL_Record_Deleted__c']){
      return 0;
    }
  }
    return (criteria === 'Enter') ? -1 : 1; 
}  
/*this method is used to prepare the filter object when user click on any filter(picklist) 
   that value we store in this object for filter the data accordingly.*/
   export const getFilterData = function (filterKey, filterVal, filterDataObj) {
    let duplicateValue = false;
    if (filterDataObj) {
      if (filterDataObj.hasOwnProperty(filterKey)) {
          duplicateValue = filterDataObj[filterKey].some((item)=>{
           return item===filterVal;
         });
         if(!duplicateValue){
         filterDataObj[filterKey].push(filterVal);
         }
      }
      else {
        filterDataObj[filterKey] = [filterVal];
      }
    }
    return filterDataObj;
  }

export const getPillFilterValues = function (filterFiledApiName, filterFieldValue, pillValues) {
  pillValues.push({ 'key': filterFiledApiName, 'value': filterFieldValue });
  let uniqueFilterValues = [...new Set(pillValues)];
  return uniqueFilterValues;
}

export const filtercomputeboolean = function (filterProp, val) {

  let successrec = false;
  Object.keys(filterProp).map((arg) => {

  });
  return successrec;
}

export const getFinalFilterList = function (result, filterProp, tempList,dateFilter = 'sCreatedDate') {
  if (filterProp && result) {
    const keys = Object.keys(filterProp);
    tempList = result.slice(0);
    for (let i = 0; i < keys.length; i++) {
      const key = keys[i];
      if (key === 'sCreatedDate') {
        let d1 = new Date(filterProp[key][0]);
        let d2 = new Date(filterProp[key][1]);
        tempList = tempList.filter((record) => {
          let resultdate = new Date(record[dateFilter]);
          return (resultdate.getTime() >= d1.getTime() && resultdate.getTime() <= d2.getTime());
        });
      } else if (key === 'searchByWord') {
        const newList = [];
        for (let i = 0; i < tempList.length; i++) {
          for (var propertie in tempList[i]) {
            if (tempList[i][propertie]) {
              var lowerCase = JSON.stringify(tempList[i][propertie]);
              if (lowerCase.toLowerCase().includes(filterProp[key][0].toLowerCase())) {
                newList.push(tempList[i]);
                break;
              }
            }
          }
        }
        tempList = newList;
      }
      else {
        tempList = tempList.filter((record) => {
          return filterProp[key].some((val) => {
            if(key === 'sDCN' && val === 'Yes') {
              return record[key].includes(val);
            }else{
            return val === record[key];
            }
          });
        });
      }
    }
  }

  let uniqueChars = [...new Set(tempList)];
  let finalListData = { response: tempList, uniqueList: uniqueChars };

  return finalListData;
}

/*This method is used to log the error caught from LWC.
Parms:
* @param {*} sMessage        Error Message + Stanck Trace
* @param {*} sClass          Class Name
* @param {*} sMethod         Method Name 
* @param {*} sExceptionType  Excpetion Type 
* @param {*} sErrorType      'Integration Error | Application Error'
* @param {*} oThis           controller this operator to set the track variable
*/
export const utilityLogError = function (sMessage, sClass, sMethod, sExceptionType, sErrorType, oThis) {
  logError({
    sMessage: sMessage,
    sClass: sClass,
    sMethod: sMethod,
    sExceptionType: sExceptionType,
    sErrorType: sErrorType
  }).then(errNum => {
    oThis.bSystemFailureMessage = oThis.labels.crmToastError + ': ' + errNum;
  })
}

/*This method is used to get the user group settings.
Parms:
* @param {*} oThis           controller this operator to set the track variable
*/
export const getUserGroup = function () {
  let oUserGroup = {};
  if (hasCRMS_206_CCSHumanaPharmacyAccess || hasCRMS_684_Medicare_Customer_Service_Access) {
    oUserGroup.bRcc = true;
  }
  if (hasCRMS_685_PCC_Customer_Service_Access) {
    oUserGroup.bProvider = true;
  }
  if (hasCRMS_240_GBO_Segment_Service_Access) {
    oUserGroup.bGbo = true;
  }
  if (hasCRMS_205_CCSPDPPharmacyPilot || hasCRMS_302_HPTraditionalInsuranceData ||  hasCRMS_301_HPInsuranceData || hasCRMS_300_Humana_Pharmacy_Supervisor) {
    oUserGroup.bPharmacy = true;
  }
  if (!oUserGroup.bRcc && !oUserGroup.bProvider && !oUserGroup.bGbo && !oUserGroup.bPharmacy) {
    oUserGroup.bGeneral = true;
  }
  if((hasCRMS_684_Medicare_Customer_Service_Access && hasCRMS_206_CCSHumanaPharmacyAccess) || (hasCRMS_684_Medicare_Customer_Service_Access && hasCRMS_206_CCSHumanaPharmacyAccess  && (hasCRMS_630_MedicareElectionTracking_EnrollmentEdit || hasCRMS_605_BizConfig_MedicareEnrollment_Edit )) ){
    oUserGroup.bVerbalConsent = true;
  }
  if (hasCRMS_206_CCSHumanaPharmacyAccess) {
    oUserGroup.bRSOHP = true;
  }
  return oUserGroup;
}

/*this method is for generic accordian
 */
export const expandAccordianRow = function(event,data){
  let newData;
  let index  = event.currentTarget.getAttribute('data-att');
  if(event.currentTarget.getAttribute('data-actionname') === "utility:jump_to_right")
  {
    data[index].iconName = "utility:jump_to_bottom";
    data[index].extraField = true;
    newData =  handleAccordianTypes(data,index,'right');
  }else{
    data[index].iconName = "utility:jump_to_right";
    data[index].extraField = false;
    newData = data;
  }
  return newData;
}

export const handleAccordianTypes = function(data,ind,accordionTyp){
  data.forEach((item, index)=> {
    if(accordionTyp==='right' &&  item.iconName==='utility:jump_to_bottom' && index!==parseInt(ind)){
      item.iconName =  "utility:jump_to_right";
      item.extraField = false;
    }
  });
  return data;
}

/**
 * 
  * @param - datevalue Format: 'YYYY/MM/DD' or 'MM/DD/YYYY'
   description - this method will return reverse date format in the expected format
*/
export const getReversedateFormat = function (dateVal, sFormat = 'YYYY/MM/DD') {
  dateVal = dateVal.replace(/-/g, '/'); // Replace '-' to '/' to prevent date convertion to local time zone
  const datee = new Date(dateVal);
  const day = datee.getDate() < 10 ? '0' + datee.getDate() : datee.getDate();
  const mm = datee.getMonth() + 1;
  let month = mm < 10 ? '0' + mm : mm;
  const year = datee.getFullYear();
  let newDate = "";
  if(sFormat === hcConstants.DATE_YMD){
    newDate = `${year}/${month}/${day}`;
  }
  else if(sFormat === hcConstants.DATE_MDY){
    newDate = `${month}/${day}/${year}`;
  }
  return newDate;
}

/*
   @param1 - omittedObject(Object whose properties needs to omitt means remove from it)
   @param2 - arryOfKeys(accept the object keys that needs to be delete from object)
   description - this method will return new Object with omitted properties
*/
export const deleteObjProperties = function (omittedObject, arryOfKeys) {
  arryOfKeys.map(item => {
    delete omittedObject[item];
  });
  return omittedObject;
}

/*
   @param1 - dateValue(e.g.12/12/2020)
   description - this method will return year of dateValue(param1)
*/
export const getDateYear = function (dateValue) {
  const datee = new Date(dateValue);
  return datee.getFullYear();
}

/**
 * This method will return date difference in years
 * @param1 - primaryDate Format: MM/DD/YYYY
 * @param2 - secondaryDate Format: MM/DD/YYYY
 */
export const getDateDiffInYears = function (primaryDate, secondaryDate) {
  const sDate = new Date(primaryDate);
  const eDate = new Date(secondaryDate);
  let diffYear = (sDate.getTime() - eDate.getTime()) / 1000;
  diffYear /= (60 * 60 * 24);
  return (Math.abs(diffYear / 365.25));
}

/**
 * @param1 - birthValue(e.g.mm/dd/yyyy)
   description - this method will return Age of person from curent Date
 */
export const ageCalculator = (birthValue) => {
  let dToday = new Date();
  let tDate = (birthValue).split('/');
  let dDOB = new Date(tDate[2], tDate[0] - 1, tDate[1]);
  let yeardiff = dToday.getYear() - dDOB.getYear();
  let monthdiff = dToday.getMonth() - dDOB.getMonth();
  let datediff = dToday.getDate() - dDOB.getDate();
  let iAge = yeardiff;
  if (monthdiff < 0) {
    iAge = iAge - 0.5;
    if (monthdiff < -6) {
      iAge = iAge - 0.5;
    } else if (monthdiff == -6 && datediff < 0) {
      iAge = iAge - 0.5;
    }
  } else if (monthdiff > 6) {
    iAge = iAge + 0.5;
  } else if (monthdiff == 6 && datediff >= 0) {
    iAge = iAge + 0.5;
  } else if (monthdiff == 0 && datediff < 0) {
    iAge = iAge - 0.5;
  }
  return iAge;
}

/**
 * Sort date values in the most recent or older
 * @param {*} data 
 * @param {*} key Key to copare and sort
 * @param {*} order order to sort possible values: RECENT_FIRST, OLDER_FIRST
 */
export const sortByDate = (data, key, order = hcConstants.RECENT_FIRST ) => {
  return order === hcConstants.RECENT_FIRST ? data.sort((a, b) => new Date(b[key]) - new Date(a[key])) : 
          data.sort((a, b) => new Date(a[key]) - new Date(b[key]));
}

let baseUrl = "";
/**
 * Returns the base url of the application. Ex: https://humanaservice--hlthcldcop.lightning.force.com
 */
export const getBaseUrl = () => baseUrl;

/**
 * Self invoking method to load base url on screen load
 */
(() => {
  fetchBaseUrl()
    .then(url => {
        baseUrl = url;
    });
})();

let bSandBoxOrg = false;
/**
 * Returns true if sandbox bar exists , else false
 */
export const hasSystemToolbar = () => bSandBoxOrg;

/**
 * Self invoking method to load hasSystemToolBar boolean
 */
(() => {
    isSandboxOrg()
    .then(hasSystemToolBar => {
      bSandBoxOrg = hasSystemToolBar;
    })
    .catch(err => {
      console.error('Error', err);
    });
})();

 /**
 * toastMsge
 * @param {*} msge - msge that is visible on toast
 * param {*} variant -variant like toast is success , warning or error
 */
export const toastMsge =(title ,msge , variant, mode)=>{
  dispatchEvent(
    new ShowToastEvent({
      title: title,
      message: msge,
      variant: variant,
      mode: mode
    })
  );
}

/**
 * copy value on clipboard
 * @param {*} value - the value that needs to copy on clipboard
 */
export const copyToClipBoard =(value)=>{
  let dummyMemGenKey = document.createElement("textarea");
  dummyMemGenKey.value = value;
  document.body.appendChild(dummyMemGenKey);
  dummyMemGenKey.select();
  document.execCommand("Copy");
  dummyMemGenKey.remove();
  dispatchEvent(
   new ShowToastEvent({
     title: "This text has been copied",
     message: "",
     variant: "success",
   })
 );
}

export const getCalculatedDate = (date = new Date(), months = 0, days = 0, years = 0) => {
  let todaysdate = new Date(date);
  let sDate = new Date();
  let formatOptions = {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
    timeZone: "GMT"
  }
  sDate.setMonth(todaysdate.getMonth() + months);
  sDate.setDate(sDate.getDate() + days);
  sDate.setFullYear(sDate.getFullYear() + years);
  return new Intl.DateTimeFormat('en-US', formatOptions).format(sDate)
}

export const getCalculatedDateDays = (date = new Date(), days = 0) => {
  let sDate = new Date(date);
  let formatOptions = {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
    timeZone: "EST"
  }
  sDate.setDate(sDate.getDate() + days);
  return new Intl.DateTimeFormat('en-US', formatOptions).format(sDate)
}


export function getFormatDate(date, format = 'mm/dd/yyyy') {
    let formatOptions = {
        year: "numeric",
        month: "2-digit",
        day: "2-digit",
        timeZone:"GMT"
    }
    if (format) {
        switch (format?.toLowerCase()) {
            case "mm/dd/yyyy":
                return new Intl.DateTimeFormat('en-US', formatOptions).format(new Date(date));
            case "dd/mm/yyyy":
                return new Intl.DateTimeFormat('en-GB', formatOptions).format(new Date(date));
            case "yyyy-mm-dd":
                date = new Intl.DateTimeFormat('en-US', formatOptions).format(new Date(date));
                return `${date.split('/')[2]}-${date.split('/')[0]}-${date.split('/')[1]}`
            default:
                return new Intl.DateTimeFormat('en-US', formatOptions).format(new Date(date));
        }
    } else {
        return new Error('format is missing');
    }
}

export function getUniqueId() {
    return Date.now().toString(16) + Math.random().toString(16).slice(2);
}