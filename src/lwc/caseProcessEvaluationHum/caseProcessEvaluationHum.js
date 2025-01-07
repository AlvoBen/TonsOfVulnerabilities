/*
LWC Name        : caseProcessEvaluationHum.html
Function        : LWC to process dispaly logic for Recommended and Available Processes.

Modification Log:
* Developer Name                  Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Nirmal Garg                  	  02/13/2023                 initial version
****************************************************************************************************************************/

//#region Imports
import getInitialDetails from '@salesforce/apex/CaseProcessEvaluation_LC_Hum.getInitialDetails';
import { evaluateExpression } from './evaluateProcessExpressionHum';
//#endregion

//#region variables
let profilename;
let recommendedProcess = [];
let availableProcess = [];
let caseDetails;
let permissionsets;
let templatedetails;
let finalresult = {};
let newCaseExcludedTemplates = ['QAA', 'COB', 'PreEx'];
let caseid;
let memberplandetails;
const EMPTY_CHARATER = '';
//#endregion

const resetvariables = () => {
    profilename = null;
    recommendedProcess = [];
    availableProcess = [];
    caseDetails = null;
    permissionsets = null;
    templatedetails = null;
    finalresult = [];
    caseid = null;
    memberplandetails = null;
}

const getProcess = (casedata) => {
    return new Promise((resolve, reject) => {
        resetvariables();
        try {
            if (casedata && (casedata?.caseId || (typeof (casedata) === 'string'
                && casedata?.length > 0 && casedata?.startsWith('500')))) {
                caseid = casedata?.caseId ?? casedata;
                getData(casedata).then(result => {
                    resolve(finalresult);
                }).catch(error => {
                    reject(error);
                })
            } else {
                getData(casedata).then(result => {
                    resolve(finalresult);
                }).catch(error => {
                    reject(error);
                })
            }
        }
        catch (error) {
            reject(error);
        }
    });

}

const checkoperation = (value1, value2, operation) => {
	value1 = value1?.replaceAll(/\s+/g, EMPTY_CHARATER)?.toLowerCase() ?? value1;
    value2 = value2?.replaceAll(/\s+/g, EMPTY_CHARATER)?.toLowerCase() ?? value2;
    if (operation && (operation?.toLowerCase() === 'equals' || operation === '=')) {
        if (value1 === value2) {
            return true;
        } else {
            return false;
        }
    } else if (operation && (operation?.toLowerCase() === 'not equals' || operation === '!=')) {
        if (value1 === value2) {
            return false;
        } else {
            return true;
        }
    } else if (operation && operation?.toLowerCase() === 'includes') {
        if (value1) {
            let temp = value1.split(',');
            if (temp && Array.isArray(temp) && temp?.length > 0) {
                let tempvalue = temp.find(k => k && k?.trim() === value2);
                return tempvalue ? true : false;
            } else {
                return false;
            }
        }
    } else if (operation && operation?.toLowerCase() === 'not includes') {
        if (value1) {
            let temp = value1.split(',');
            if (temp && Array.isArray(temp) && temp?.length > 0) {
                let tempvalue = temp.find(k => k && k?.trim() === value2);
                return tempvalue ? false : true;
            } else {
                return true;
            }
        }
    }
}

const checkCriteria = (expressiondata, type) => {
    if (expressiondata && expressiondata.hasOwnProperty(type)) {
        let processdata = expressiondata[type];
        if (processdata && processdata?.display) {
            if (processdata?.criteria && Array.isArray(processdata.criteria) && processdata?.criteria?.length > 0) {
                processdata.criteria.forEach(p => {
                    if (p && p?.type) {
                        switch (p?.type) {
                            case "profile":
                                p.result = checkoperation(p.value, profilename, p.operation);
                                if (processdata?.expression) {
                                    processdata.expression = processdata.expression.replaceAll(p.sno, p.result);
                                }
                                break;
                            case "permissionset":
                                let result = false;
                                if (p?.operation === 'includes') {
                                    if (p?.value) {
                                        let temp = p?.value.split(',');
                                        if (temp && Array.isArray(temp) && temp.length > 0) {
                                            temp.forEach(k => {
                                                if (permissionsets && Array.isArray(permissionsets) && permissionsets.length > 0) {
                                                    if (permissionsets.find(t => t?.PermissionSet?.Name === k?.trim())?.Id) {
                                                        result = true;
                                                    }
                                                }
                                            })
                                        }
                                        if (processdata?.expression) {
                                            processdata.expression = processdata?.expression.replaceAll(p.sno, result);
                                        }
                                    }
                                }
                                else {
                                    p.result = checkoperation(p.value, permissionsets.find(k => k?.PermissionSet.Name === p.value)?.PermissionSet?.Name, p.operation);
                                    if (processdata?.expression) {
                                        processdata.expression = processdata?.expression.replaceAll(p.sno, p.result);
                                    }
                                }
                                break;
                            case "componentproperty":
                                if (p.mappingObject === 'caseDetails') {
                                    if (caseDetails && caseDetails.hasOwnProperty(p.mappingField)) {
                                        p.result = checkoperation(p.value, caseDetails[p.mappingField], p.operation);
                                        if (processdata?.expression) {
                                            processdata.expression = processdata.expression.replaceAll(p.sno, p.result);
                                        }
                                    }
                                }
                                break;
                        }
                    }
                })
            }
            if (processdata && processdata?.expression) {
                return evaluateExpression(processdata?.expression);
            } else {
                return false;
            }
        } else {
            return false;
        }
    } else {
        return false;
    }
}

const checkprocesslogic = () => {
    return new Promise((resolve, reject) => {
        let counter = 0;
        try {
            if (templatedetails && Array.isArray(templatedetails) && templatedetails.length > 0) {
                templatedetails.forEach(k => {
                    counter = counter + 1;
                    if (k && k?.TemplateCondition__c) {
                        let expressiondata = JSON.parse(k.TemplateCondition__c);
                        if (expressiondata && expressiondata.hasOwnProperty('recommended')) {
                            if (checkCriteria(expressiondata, 'recommended')) {
                                let tempfields = getTemplateFields(k);
                                if (recommendedProcess && Array.isArray(recommendedProcess) && recommendedProcess.length > 0) {
                                    if (recommendedProcess.findIndex(k => k?.Description__c === tempfields?.Description__c) < 0) {
                                        recommendedProcess.push(tempfields);
                                    }
                                } else {
                                    recommendedProcess.push(tempfields);
                                }
                            }
                        }
                        if (expressiondata && expressiondata.hasOwnProperty('available')) {
                            if (checkCriteria(expressiondata, 'available')) {
                                let tempfields = getTemplateFields(k);
                                if (availableProcess && Array.isArray(availableProcess) && availableProcess.length > 0) {
                                    if (availableProcess.findIndex(k => k?.Description__c === tempfields?.Description__c) < 0) {
                                        availableProcess.push(tempfields);
                                    }
                                } else {
                                    availableProcess.push(tempfields);
                                }
                            }
                        }
                    }
                });
            }
            if (caseid === undefined || caseid === null || caseid === '' || caseid?.length <= 0) {
                recommendedProcess = recommendedProcess && Array.isArray(recommendedProcess) && recommendedProcess?.length > 0
                    ? recommendedProcess.filter(k => !newCaseExcludedTemplates.includes(k?.Description__c)) : [];
                availableProcess = availableProcess && Array.isArray(availableProcess) && availableProcess?.length > 0
                    ? availableProcess.filter(k => !newCaseExcludedTemplates.includes(k?.Description__c)) : [];
            }
            finalresult["recommended"] = recommendedProcess;
            finalresult["available"] = availableProcess;
            if (counter === templatedetails.length)
                resolve('success');
        } catch (error) {
            reject(error);
        }
    });

}

const getUpdateObject = (cdata, mdata) => {
    if (mdata && Array.isArray(mdata) && mdata.length > 0) {
        cdata.MemberPlanProduct = mdata[0]?.Product__c ?? '';
        cdata.MemberPlanProductCode = mdata[0]?.Product_Type__c ?? '';
        return cdata;
    } else {
        return cdata;
    }
}

const getData = (casedata) => {
    return new Promise((resolve, reject) => {
        try {
            getInitialDetails({ caseId: caseid, memberPlanId: casedata?.MemberPlanId ?? '' }).then(result => {
                if (result && Array.isArray(result) && result?.length > 0) {
                    memberplandetails = result[0]?.lstMemberData ?? null;
                    caseDetails = result[0]?.lstCaseDetails && Array.isArray(result[0]?.lstCaseDetails) && result[0]?.lstCaseDetails?.length > 0 ? result[0]?.lstCaseDetails[0] :
                        (caseid === null || caseid === undefined || caseid === '') ? getUpdateObject(casedata, memberplandetails) : null;
                    profilename = result[0]?.profilename ?? null;
                    permissionsets = result[0]?.lstPermissionSet ?? null;
                    templatedetails = result[0]?.lstTemplateData ?? null;
                    checkprocesslogic().then(result => {
                        resolve(result);
                    }).catch(error => {
                        reject(error);
                    });
                }
            }).catch(error => {
                console.error(error);
                reject(error);
            });
        } catch (error) {
            reject(error);
        }
    });
}

const getTemplateFields = (k) => {
    return {
        templateName: k.Template_Label__c,
        MasterLabel: k.MasterLabel,
        Template_Label__c: k.Template_Label__c,
        IsVisible__c: k.IsVisible__c,
        Description__c: k.Description__c,
        Params__c: k.Params__c,
        Template_Type__c: k.Template_Type__c,
        Order__c: k.Order__c
    };
}

export { getProcess };