/******************************************************************************************************************
LWC Name           : genericTemplateDataCreationHum.js
Version            : 1.0
Function           : Component to display generic template creation.
Created On         : 09/13/2022
*******************************************************************************************************************
Modification Log:
* Developer Name            Code Review                Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------
* Nirmal garg                                     09/13/2022                Original Version
* Nirmal Garg                                     04/12/2023                US4460894
*******************************************************************************************************************/
import { LightningElement, wire } from 'lwc';
import { createRecord, updateRecord, deleteRecord } from 'lightning/uiRecordApi';
import getTemplateField from '@salesforce/apex/GenericTemplateCreation_LC_HUM.getTemplateFields';
import TEMPLATE_SUBMISSION_OWNER_OBJECT from '@salesforce/schema/Template_Submission_Owner__c';
import OBJECT_OWNER_ID_FIELD from '@salesforce/schema/Template_Submission_Owner__c.Object_Owner_ID__c';
import OBJECT_OWNER_TYPE_FIELD from '@salesforce/schema/Template_Submission_Owner__c.Object_Owner_Type__c';
import TEMPLATE_SUBMISSION_OBJECT from '@salesforce/schema/Template_Submission__c';
import TEMPLATE_ID_FIELD from '@salesforce/schema/Template_Submission__c.Template__c';
import SUBMISSION_OWNER_FIELD from '@salesforce/schema/Template_Submission__c.Submission_Owner__c';
import VERSION_FIELD from '@salesforce/schema/Template_Submission__c.Version__c';
import TEMPLATE_SUBMISSION_DATA_OBJECT from '@salesforce/schema/Template_Submission_Data__c';
import TEMPLATE_FIELD_ID_FIELD from '@salesforce/schema/Template_Submission_Data__c.Template_Field__c';
import TEMPLATE_SUBMISSION_ID_FIELD from '@salesforce/schema/Template_Submission_Data__c.Id';
import SUBMISSION_ID_FIELD from '@salesforce/schema/Template_Submission_Data__c.Template_Submission__c';
import NAME_FIELD from '@salesforce/schema/Template_Submission_Data__c.Name__c';
import VALUE_FIELD from '@salesforce/schema/Template_Submission_Data__c.Value__c';
import getTemplateDetails from '@salesforce/apex/GenericTemplateCreation_LC_HUM.getTemplateDetails';
import getTempDataDetails from '@salesforce/apex/GenericTemplateCreation_LC_HUM.getTemplateDataDetails';
import getTemplateSubmissionOwnerId from '@salesforce/apex/GenericTemplateCreation_LC_HUM.getTemplateSubmissionOwnerId';
let templatedata = new Map();

const getTemplateDataDetails = (tempSubmissionId) => {
    return new Promise((resolve, reject) => {
        if (!isStringEmpty(tempSubmissionId)) {
            getTempDataDetails({ tempSubmissionId: tempSubmissionId })
                .then(result => {
                    resolve(result);
                }).catch(error => {
                    reject(error);
                })
        } else {
            reject(new Error('Template Submission Id is missing.'))
        }
    })
}

function isStringEmpty(input) {
    if (input != null && input != undefined && input != '' && input?.length >= 0) {
        return false;
    } else {
        return true;
    }
}

const getTemplateData = (templateName) => {
    return new Promise((resolve, reject) => {
        getTemplateDetails({ templatename: templateName })
            .then(result => {
                if (result && Array.isArray(result) && result.length > 0) {
                    if (!checkMapKey('template')) {
                        addDataToMap('template', result)
                    }
                    resolve(result);
                } else {
                    resolve(null);
                }
            }).catch(error => {
                console.error(error);
                reject(error);
            })
    })
}

const getTemplateFields = (tempId) => {
    return new Promise((resolve, reject) => {
        getTemplateField({ tempid: tempId })
            .then(result => {
                resolve(result);
            }).catch(error => {
                reject(error);
            })
    })
}

const createTemplateSubmissionOwner = (whoid, whattype) => {
    const fields = {};
    fields[OBJECT_OWNER_ID_FIELD.fieldApiName] = whoid;
    fields[OBJECT_OWNER_TYPE_FIELD.fieldApiName] = whattype;
    const recordInput = { apiName: TEMPLATE_SUBMISSION_OWNER_OBJECT.objectApiName, fields };
    return new Promise((resolve, reject) => {
        createRecord(recordInput)
            .then(template => {
                resolve(template.id)
            })
            .catch(error => {
                reject(error)
            });
    })
}

const createTemplateSubmission = (ownerid, templateId) => {
    const fields = {};
    fields[SUBMISSION_OWNER_FIELD.fieldApiName] = ownerid;
    fields[TEMPLATE_ID_FIELD.fieldApiName] = templateId;
    fields[VERSION_FIELD.fieldApiName] = 0;
    const recordInput = { apiName: TEMPLATE_SUBMISSION_OBJECT.objectApiName, fields };
    return new Promise((resolve, reject) => {
        createRecord(recordInput)
            .then(template => {
                resolve(template.id)
            })
            .catch(error => {
                reject(error)
            });
    })
}

const createTemplateFields = (submissionId, templatefieldId, name) => {
    const fields = {};
    fields[TEMPLATE_FIELD_ID_FIELD.fieldApiName] = templatefieldId;
    fields[SUBMISSION_ID_FIELD.fieldApiName] = submissionId;
    fields[VALUE_FIELD.fieldApiName] = '';

    const recordInput = { apiName: TEMPLATE_SUBMISSION_DATA_OBJECT.objectApiName, fields };
    return new Promise((resolve, reject) => {
        createRecord(recordInput)
            .then(template => {
                resolve(template)
            })
            .catch(error => {
                reject(error)
            });
    })
}

const checkMapKey = (key) => {
    return templatedata.has(key) ? true : false;
}

const addDataToMap = (key, data) => {
    templatedata.set(key, data);
}

const generateTemplateData = (templateName, caseId) => {
    templatedata.clear();
    return new Promise((resolve, reject) => {
        getTemplateData(templateName).then(result => {
            if (result && templatedata.has('template')) {
                getTemplateFields(templatedata.get('template')[0]?.Id)
                    .then(result => {
                        if (!checkMapKey('templateFields')) {
                            templatedata.set('templateFields', result);
                        }
                        if (result && Array.isArray(result) && result?.length > 0) {
                            createTemplateSubmissionOwner(caseId, 'Case')
                                .then(result => {
                                    if (!checkMapKey('templateSubmissionOwner')) {
                                        addDataToMap('templateSubmissionOwner', result);
                                    }
                                    if (result) {
                                        createTemplateSubmission(result, templatedata.get('template')[0]?.Id)
                                            .then(result => {
                                                if (!checkMapKey('templateSubmission')) {
                                                    addDataToMap('templateSubmission', result);
                                                }
                                                if (result) {
                                                    Promise.all(
                                                        templatedata.get('templateFields').map(item => {
                                                            if (item && item?.Id && item.Name) {
                                                                return createTemplateFields(result, item.Id, item.Name)
                                                            }
                                                        })
                                                    ).then(result => {
                                                        if (!checkMapKey('templateSubmissionData')) {
                                                            addDataToMap('templateSubmissionData', result);
                                                        }
                                                        resolve(templatedata);
                                                    }).catch(error => {
                                                        console.error(error);
                                                        reject(error);
                                                    })
                                                }
                                            })
                                    }
                                }).catch(error => {
                                    throw new Error(error);
                                })
                        }
                    }).catch(error => {
                        throw new Error(error);
                    })
            }
        }).catch(error => {
            reject(error)
        })
    })

}

const updateTemplateSubmissionData = (tempId, value) => {
    // Create the recordInput object
    const fields = {};
    fields[TEMPLATE_SUBMISSION_ID_FIELD.fieldApiName] = tempId;
    fields[VALUE_FIELD.fieldApiName] = value;
    const recordInput = { fields };
    return new Promise((resolve, reject) => {
        updateRecord(recordInput)
            .then(result => {
                resolve(result)
            }).catch(error => {
                reject(error);
            })
    })
}

const getTemplateSubmissionOwnerIds = (sCaseId) => {
    return new Promise((resolve, reject) => {
        getTemplateSubmissionOwnerId({ caseId: sCaseId.hasOwnProperty('caseId') ? sCaseId['caseId'] : sCaseId })
            .then(result => {
                resolve(result);
            }).catch(error => {
                reject(error);
            })
    })
}

const deleteUnsavedData = (tempSubmissionOwnerId, tempSubmissionId) => {
    if (!isStringEmpty(tempSubmissionOwnerId)){
        deleteRecord(tempSubmissionOwnerId)
            .then((result) => {
                console.log('record has been deleted')
            }).catch(error => {
                console.log(error);
            });
    }
    if (!isStringEmpty(tempSubmissionId)) {
        deleteRecord(tempSubmissionId)
            .then((result) => {
                console.log('record has been deleted')
            }).catch(error => {
                console.log(error);
            })
    }
}

const createTemplateRecord = (templateNumber, templateId, whoId, whattype) => {
    let outputdata = [];
    let counter = 0;
    let templateSubmissionData = [];
    return new Promise((resolve, reject) => {
        try {
            getTemplateFields(templateId).then(fields => {
                if (fields && Array.isArray(fields) && fields.length > 0) {
                    createTemplateSubmissionOwner(whoId, whattype).then(result => {
                        createTemplateSubmission(result, templateId).then(result => {
                            outputdata.push({
                                SubmissionId: result,
                                ObjectName: 'Template_Submission__c'
                            })
                            counter = 0;
                            createTemplateSubmissionData(result, fields).then(res => {
                                resolve(res);
                            }).catch(error => {
                                reject(error);
                            })

                        }).catch(error => {
                            console.log(error);
                        })
                    }).catch(error => {
                        console.log(error);
                    })
                }

            })

        } catch (error) {
            reject(error)
        }
    })
}

export { createTemplateRecord, getTemplateFields, createTemplateSubmissionOwner, createTemplateSubmission, createTemplateFields, deleteUnsavedData, generateTemplateData, updateTemplateSubmissionData, getTemplateDataDetails, getTemplateSubmissionOwnerIds };