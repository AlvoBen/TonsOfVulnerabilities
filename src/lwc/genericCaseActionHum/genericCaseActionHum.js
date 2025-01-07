/*******************************************************************************************************************************
LWC JS Name : genericCaseActionHum.js
Function    : This JS serves as controller to genericCaseActionHum.html. 

Modification Log: 
Developer Name           Code Review                      Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Swapnali Sonawane                                       02/15/2023                  US 4178421 Determine the logic to display templates on New case page and attach case to the template on Launch
* KalyaniPachpol                                          03/02/2023                  US-4305931
* Aishwarya Pawar                                         03/14/2023                  US-4396323
* Abhishek Mangutkar                                      03/13/2023                  US-4365921
* Nirmal Garg                                             04/12/2023                  US4460894
*******************************************************************************************************************************/
import { LightningElement, track, wire } from 'lwc';
import { updateRecord, createRecord } from 'lightning/uiRecordApi';
import CASE_ID_FIELD from '@salesforce/schema/Case_Interaction__c.Case__c';
import INTERACTION_ID_FIELD from '@salesforce/schema/Case_Interaction__c.Interaction__c';
import CASE_INTERACTION_OBJECT from '@salesforce/schema/Case_Interaction__c';
import TEMPLATE_SUBMISSION_ID from "@salesforce/schema/Template_Submission_Owner__c.Id";
import OBJECT_OWNER_ID_FIELD from '@salesforce/schema/Template_Submission_Owner__c.Object_Owner_ID__c';
import { CurrentPageReference, NavigationMixin } from 'lightning/navigation';
import checkForExistingMapping from '@salesforce/apex/GenericCaseAction_LC_HUM.checkForExistingMapping';
import CASE_PARENT_ID_FIELD from '@salesforce/schema/CaseComment.ParentId';
import CASE_COMMENT_BODY_FIELD from '@salesforce/schema/CaseComment.CommentBody';
import CASE_COMMENT_OBJECT from '@salesforce/schema/CaseComment';
import createNewCase from '@salesforce/apex/Logging_LC_HUM.createNewCase';
import getCaseDetails from '@salesforce/apex/GenericCaseAction_LC_HUM.getCaseDetails';
let interactionId;
export default class GenericCaseActionHum extends NavigationMixin(LightningElement) {
    @track pageRef;
    @track pageState;
    @track stateValue;

    @wire(CurrentPageReference)
    wiredPageRef(pageRef) {
        this.pageRef = pageRef;
        this.pageState = this.pageRef?.state ?? null;
        this.getInteractionId();
    }
   
getInteractionId() {
    interactionId = '';
    if (this.pageState && typeof (this.pageState) === 'object') {
        if (this.pageState.hasOwnProperty('ws')) {
            this.stateValue = this.pageState && this.pageState.hasOwnProperty('ws') ? this.pageState['ws'] : null;
            let tempvalues = this.stateValue && this.stateValue.includes('c__interactionId') ? this.stateValue.split('c__interactionId=') : null;
            if (tempvalues && Array.isArray(tempvalues) && tempvalues?.length >= 2) {
                interactionId = tempvalues[1]?.substring(0,18) ?? null;
            }
        }
        else if (this.pageState.hasOwnProperty('c__interactionId')) {
            interactionId = this.pageState['c__interactionId'];
        }
    }
}

}



const attachProcessToCase = (processIds, caseid) => {
    // Create the recordInput object
    const fields = {};
    fields[TEMPLATE_SUBMISSION_ID.fieldApiName] = processIds;
    fields[OBJECT_OWNER_ID_FIELD.fieldApiName] = caseid;
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

const attachInteractionToCase = (caseId) => {
    const fields = {};
    fields[CASE_ID_FIELD.fieldApiName] = caseId;
    fields[INTERACTION_ID_FIELD.fieldApiName] = interactionId;
    const recordInput = { apiName: CASE_INTERACTION_OBJECT.objectApiName, fields };
    try {
        return new Promise((resolve,reject)=>{
            if (caseId && interactionId && caseId?.length === 18 && interactionId?.length === 18) {
                checkForExistingMapping({ caseId: caseId, interactionId: interactionId })
                    .then(result => {
                        if (!result) {

                            createRecord(recordInput)
                                .then(template => {
                                    resolve(template)
                                })
                                .catch(error => {
                                    reject(error)
                                });

                        } else {
                            reject(new Error('Mapping already exist'));
                        }
                    })
                    .catch(error => {
                        reject(error);
                    })
            } else {
                reject(new Error('Mandatory parameters are missing'));
            }
        })

    } catch (error) {
        throw new ParameterException(JSON.stringify(error));
    }
}


class ParameterException extends Error{
    constructor(message){
        super(message);
    }
}

function isStringEmpty(input) {
    if (input != null && input != undefined && input != '' && input?.length >= 0) {
        return false;
    } else {
        return true;
    }
}

const createCaseCommentRecord = (caseId, commentData) => {
    if (!isStringEmpty(caseId)) {
        const fields = {};
        fields[CASE_PARENT_ID_FIELD.fieldApiName] = caseId;
        fields[CASE_COMMENT_BODY_FIELD.fieldApiName] = commentData;
        const recordInput = { apiName: CASE_COMMENT_OBJECT.objectApiName, fields };
        return new Promise((resolve, reject) => {
            createRecord(recordInput)
                .then(result => {
                    resolve(result)
                })
                .catch(error => {
                    reject(error)
                });
        })

    }
}

const getInteractionId = () => {
    return interactionId ? interactionId : null;
}

const createNewCaseId = (objectId, calledFrom, interactionId) =>{
    return new Promise((resolve, reject) => {
        createNewCase({ sObjectId: objectId, calledfrom: calledFrom, newInteractionId: interactionId })
            .then(result => {
                resolve(result);
            }).catch(error => {
                reject(error);
            })
    })
}

const getCaseData = (caseId) => {
    if (!isStringEmpty(caseId)) {
        return new Promise((resolve, reject) => {
            getCaseDetails({ caseid: caseId })
                .then(result => {
                    resolve(result);
                }).catch(error => {
                    reject(error)
                })
        })

    } else {
        return new Error('Case Id missing');
    }
}

export { createNewCaseId, getCaseData, attachProcessToCase, attachInteractionToCase, getInteractionId, createCaseCommentRecord }