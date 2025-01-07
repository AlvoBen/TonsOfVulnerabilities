import {
    memberSearch, groupSearch, casehistory, policies, accountdetailpolicy,
    grouppolicies, interactions, groupAccountPolicies, providerResults,
    providerInteractions, purchaserplan, casehistoryaccordian, providerOpenCases, consumerIds, agencyResults
    , subGrpDivision
} from './coachingTableModals';

const getValue = (record, mapping) => {
    let value;
    if (mapping.includes('.')) {

        const mapDepth = mapping.split('.');
        value = record;
        mapDepth.forEach(item => {
            value = value ? value[item] : ''
        });
    }
    else if (mapping === 'dd') {
        value = 'MTVx';
    } else {
        value = record[mapping];
    }
    return value;
}


export const getHelpText = (screenType, labels) => {
    let helpText = '';
    switch (screenType) {
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
* Populates data into model and creates records
* @param {*} model Model Array which needs to be used for data population
* @param {*} dataArr Data Array
*/
export const populatedDataIntoModel = (model, dataArr) => {
    let temp1 = [];
    let tempdarry = [];
    dataArr.forEach((dataRecord) => {
        model.forEach((modelRecord) => {
            temp1 = [];
            let rowCss = "";
            modelRecord.forEach((v) => {
                if (v.compoundx) {
                    v.compoundvalue.forEach((compv) => {
                        compv.value = getValue(dataRecord, compv.fieldName);
                        if (compv.customFunc === 'CASE_CREATION' && dataRecord.isOpenedInTwoWeeks) {
                            rowCss = 'results-row-highlight';
                        }
                    });
                }
                else {
                    if (v.fieldName) {
                        v.value = getValue(dataRecord, v.fieldName);
                    }
                }
                if (v.isCheckbox) {
                    v.isLink = dataRecord.isLink;
                }
                if (v.radio) {
                    v.checked = dataRecord.checked;
                    v.disabled = dataRecord.disabled;
                }
                else if (v.isViewAll) {
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
    switch (selectedScreen) {
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
        case 'Division Table':
            model = subGrpDivision;
        default: ''
    }
    return model;
}

export const compute = (selectedScreen, dataArr) => {
    return populatedDataIntoModel(getTableModal(selectedScreen), dataArr);
};