// export labesl
export { getLabels } from './utils/labelsHum';
import userLocale from '@salesforce/i18n/locale';
import logError from '@salesforce/apex/HUMExceptionHelper.logError';
import hasCRMS_206_CCSHumanaPharmacyAccess from '@salesforce/customPermission/CRMS_206_CCSHumanaPharmacyAccess';
import hasCRMS_684_Medicare_Customer_Service_Access from '@salesforce/customPermission/CRMS_684_Medicare_Customer_Service_Access';
import hasCRMS_685_PCC_Customer_Service_Access from '@salesforce/customPermission/CRMS_685_PCC_Customer_Service_Access';
import hasCRMS_240_GBO_Segment_Service_Access from '@salesforce/customPermission/CRMS_240_GBO_Segment_Service_Access';
import hasCRMS_205_CCSPDPPharmacyPilot from '@salesforce/customPermission/CRMS_205_CCSPDPPharmacyPilot';
import hasCRMS_302_HPTraditionalInsuranceData from '@salesforce/customPermission/CRMS_302_HPTraditionalInsuranceData';

//export constants
export { hcConstants } from './utils/constantsHum';

/**
 * Format date with the given seperator
 * @param {*} dtValue 
 * @param {*} seperator 
 */
export const getFormattedDate = (dtValue, seperator = "/") => {
    let newDate = dtValue;
    if (dtValue) {
        if (dtValue.match(/^\d{2}$/) !== null) {
            newDate = dtValue + seperator;
        } else if (dtValue.match(/^\d{2}\/\d{2}$/)) {
            newDate = dtValue + seperator;
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


export const getLocaleDate = (strDate, options) => {
    let formatOptions = {
        year: "numeric",
        month: "numeric",
        day: "numeric"
    }
    if (options) {
        formatOptions = options;
    }
    const dt = new Date(strDate);
    const day = dt.getDate() < 10 ? '0' + dt.getDate() : dt.getDate();
    const mm = dt.getMonth() + 1
    const month = mm < 10 ? '0' + mm : mm;
    const year = dt.getFullYear();
    return month + '/' + day + '/' + year;

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

export const sortTable = function (data, item1, item2) {
    let aarr = data.sort(function (a, b) {
        var afirstCol = a[item1];
        var bfirstCol = b[item1];
        var aSecondCol = a[item2];
        var bSecondCol = b[item2];
        if (afirstCol === bfirstCol) {

            var key1 = aSecondCol.toUpperCase();
            var key2 = bSecondCol.toUpperCase();
            let preSortedArray = ['MED', 'DEN', 'VIS'];
            let index1, index2;
            index1 = preSortedArray.indexOf(key1);
            index2 = preSortedArray.indexOf(key2);
            index1 = index1 === -1 ? 100 : index1;
            index2 = index2 === -1 ? 100 : index2;
            if (index1 < index2) {
                return -1
            } else if (index1 > index2) {
                return 1;
            } else {
                return 0;
            }
        }

        else {
            return (afirstCol < bfirstCol) ? customSort(a, b, 'Enter') : customSort(a, b, 'Exit');
        }
    });
    return aarr;
}

export const customSort = function (data0, data1, criteria) {
    if (data0.hasOwnProperty('ETL_Record_Deleted__c') && data1.hasOwnProperty('ETL_Record_Deleted__c')) {
        if (data0['ETL_Record_Deleted__c'] || data1['ETL_Record_Deleted__c']) {
            return 0;
        }
    }
    return (criteria === 'Enter') ? -1 : 1;
}
/*this method is used to prepare the filter object when user click on any filter(picklist) 
   that value we store in this object for filter the data accordingly.*/
export const getFilterData = function (filterKey, filterVal, filterDataObj) {
    if (filterDataObj) {
        if (filterDataObj.hasOwnProperty(filterKey)) {
            filterDataObj[filterKey].push(filterVal);
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

export const getFinalFilterList = function (result, filterProp, tempList) {
    if (filterProp && result) {
        const keys = Object.keys(filterProp);
        tempList = result.slice(0);
        for (let i = 0; i < keys.length; i++) {
            const key = keys[i];
            if (key === 'sCreatedDate') {
                let d1 = new Date(filterProp[key][0]);
                let d2 = new Date(filterProp[key][1]);
                tempList = tempList.filter((record) => {
                    let resultdate = new Date(record[key]);
                    return (resultdate.getTime() >= d1.getTime() && resultdate.getTime() <= d2.getTime());
                });
            } else if (key === 'searchByWord') {
                const newList = [];
                for (let i = 0; i < tempList.length; i++) {
                    for (var propertie in tempList[i]) {
                        if (tempList[i][propertie] && typeof tempList[i][propertie] == "string") {
                            var lowerCase = tempList[i][propertie];
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
                        return val === record[key];
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
    if (hasCRMS_205_CCSPDPPharmacyPilot || hasCRMS_302_HPTraditionalInsuranceData) {
        oUserGroup.bPharmacy = true;
    }
    if (!oUserGroup.bRcc && !oUserGroup.bProvider && !oUserGroup.bGbo && !oUserGroup.bPharmacy) {
        oUserGroup.bGeneral = true;
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

export const sortTableAssociatedForms = function (data, item1, item2,customSorting = true) {
  let aarr = data.sort(function (a, b) {
    var afirstCol = a[item1];
    var bfirstCol = b[item1];
    var aSecondCol = a[item2];
    var bSecondCol = b[item2];
    if (afirstCol === bfirstCol) {
      var key1 = aSecondCol.toUpperCase();
      var key2 = bSecondCol.toUpperCase();
      let index1, index2;
      if(customSorting){
        let preSortedArray = ['MED', 'DEN', 'VIS'];
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