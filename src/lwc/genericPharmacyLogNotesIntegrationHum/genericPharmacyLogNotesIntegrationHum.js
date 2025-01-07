/*
LWC Name: genericPharmacyLogNotesIntegrationHum.html
Function: Generic Component for pharmacy log notes HPIE integration
Modification Log:
* Developer Name                  Date                         Description
* Nirmal Garg                  09/01/2023                 		Initial Version
* Jonathan Dickinson           02/27/2024                 User Story 5738539: T1PRJ1374973: DF 8518 - 8519 - 8520; C06 Case Management; Lightning - Case Comments - Error when adding comment before closing or transferring a case and notes reflected in incorrect section
* Jonathan Dickinson           02/29/2024                 User Story 4835317: T1PRJ0865978 - MF 27481 - C12; Mail Order Management; Pharmacy - Mail Order Pharmacy page gap- Member Consent (Lightning)
****************************************************************************************************************************/
import { LightningElement } from 'lwc';
import addLogNote from '@salesforce/apex/Pharmacy_HPIE_LC_HUM.addLogNote';
import addFamilyLogNote from '@salesforce/apex/Pharmacy_HPIE_LC_HUM.addFamilyLogNote';
import { getProfileDetails } from 'c/pharmacyHPIEIntegrationHum';
import { AddFamilyNote_DTO_HUM } from './addFamilyNoteRequest';

const PATIENT_NOT_FOUND_ERROR = 'patient not found';

export default class GenericPharmacyLogNotesIntegrationHum extends LightningElement { }

/**
 * 
 * @param {String} personId enterpriseId on the Account
 * @param {String} userId network Id for the user
 * @param {String} organization value should be HUMANA
 * @param {String} logCode Log code value
 * @param {String} logNoteMsg Log note message
 * @description Add a single patient note
 */
export function addLogNoteDetails(personId, userId, organization, logCode, logNoteMsg) {
    return new Promise((resolve, reject) => {
        addLogNote({
            personId: personId, userId: userId, organization: organization,
            logNoteCode: logCode, logNoteMsg: logNoteMsg
        }).then(result => {
            resolve(result);
        }).catch(error => {
            reject(error);
        })
    });
}

/**
 * 
 * @param {String} userId network Id for the user
 * @param {String} organizationPatientId enterpriseId on the Account
 * @param {String} organization value should be HUMANA
 * @param {String} accountId accountId retrieved from the getprofiledetail HPIE service
 * @param {[]} notes array of objects. This will contain each note to be added
 * @description Add a group of family notes
 */
export function addFamilyNote(userId, organizationPatientId, organization, accountId, notes) {
    let addFamilyNoteRequest = JSON.stringify(new AddFamilyNote_DTO_HUM(userId, organizationPatientId, organization, accountId, notes));

    return new Promise((resolve, reject) => {
        addFamilyLogNote({ jsonrequest: addFamilyNoteRequest}).then(result => {
            resolve(result);
        }).catch(error => {
            reject(error);
        })
    });
}

/**
 * 
 * @param {String} userId network Id for the user
 * @param {String} organizationPatientId enterpriseId on the Account
 * @param {String} organization value should be HUMANA
 * @param {String} logCode Log code value
 * @param {String} logNoteMsg Log note message
 * @description Add a single family note when the consumer does not pass the required account Id
 */
export function createLogNote(userId, organizationPatientId, organization, logCode, logNoteMsg){
    return new Promise((resolve, reject) => { 
        getProfileDetails(organizationPatientId, userId, organization)
            .then(profileResult => {

                let accountId = profileResult?.AccountId ?? '';
                if (!accountId) {
                    reject(new Error(PATIENT_NOT_FOUND_ERROR));
                }

                let note = [
                    {
                        noteCode: logCode,
                        logNote: logNoteMsg
                    }
                ];

                return addFamilyNote(userId, organizationPatientId, organization, accountId, note);
            })
            .then(addNoteResult => {
                resolve(addNoteResult);
            })
            .catch(error => {
                reject(error);
            });
    });
}