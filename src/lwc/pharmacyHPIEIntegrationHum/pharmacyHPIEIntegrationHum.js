/*
Function        : LWC to display demographics details .

Modification Log:
* Developer Name                  Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Nirmal Garg                    07/18/2022                 user story 4861950, 4861945
* Atul Patil                    07/28/2023                  user story 4861950, 4861945
* Vishal Shinde                  31/8/2023                 US-4908765-Mail Order Management - Pharmacy - OMS Originated Notes and profile fix
* Jonathan Dickinson			 09/04/2023				   User Story 4999697: T1PRJ0870026 MF27456 HPIE/CRM - SF Tech- Mail Order Management - Pharmacy - Finance tab - Lightning - Edit Credit Card, One time payment
* Jonathan Dickinson			 09/22/2023				    User Story 5061288: T1PRJ0870026   MF 27406 HPIE/CRM - SF Tech- Mail Order Management - Pharmacy -  Details tab - Address section
* vishal Shinde                  02/29/2024                 US - 5142800- Mail Order Management - Pharmacy - "Prescriptions & Order Summary" tab - Prescriptions – Create Order
* Vishal Shinde                  02/29/2024                 USer Story - HPIE Mail Order Management - Pharmacy - "Prescriptions & Order summary" tab - Finance Queue /Member consent
* Jonathan Dickinson             02/29/2024                 User Story 4835317: T1PRJ0865978 - MF 27481 - C12; Mail Order Management; Pharmacy - Mail Order Pharmacy page gap- Member Consent (Lightning)
*****************************************************************************************************************************/
import { LightningElement } from 'lwc';
import getFinanceData from '@salesforce/apex/Pharmacy_HPIE_LC_HUM.getFinanceDetails'; 
import getPrescription from '@salesforce/apex/Pharmacy_HPIE_LC_HUM.getPrescriptions';
import getProfile from '@salesforce/apex/Pharmacy_HPIE_LC_HUM.getProfileDetails';
import getDemographics from '@salesforce/apex/Pharmacy_HPIE_LC_HUM.getDemographicsDetails';
import getPreferences from '@salesforce/apex/Pharmacy_HPIE_LC_HUM.getPreferences'; 
import getOrdersSummary from '@salesforce/apex/Pharmacy_HPIE_LC_HUM.getOrderSummary';
import getOrderDetail from '@salesforce/apex/Pharmacy_HPIE_LC_HUM.getOrderDetails'; 
import getOrderEvents from '@salesforce/apex/Pharmacy_HPIE_LC_HUM.getEvents';
import getOrderTasks from '@salesforce/apex/Pharmacy_HPIE_LC_HUM.getTasks';
import getPlanDetails from '@salesforce/apex/Pharmacy_HPIE_LC_HUM.getPlanDetails';
import getPatientLognotes from '@salesforce/apex/Pharmacy_HPIE_LC_HUM.getPatientLognotes';
import getFamilyLognotes from '@salesforce/apex/Pharmacy_HPIE_LC_HUM.getFamilyLognotes';
import getHealthHistoryDetails from '@salesforce/apex/Pharmacy_HPIE_LC_HUM.getHealthHistoryDetails';
import addFinance from '@salesforce/apex/Pharmacy_HPIE_LC_HUM.addFinance';
import updateFinance from '@salesforce/apex/Pharmacy_HPIE_LC_HUM.updateFinance';
import addOneTimePayment from '@salesforce/apex/Pharmacy_HPIE_LC_HUM.addOneTimePayment';
import updateCapTypeData from '@salesforce/apex/Pharmacy_HPIE_LC_HUM.updateCapType';
import updateEmailData from '@salesforce/apex/Pharmacy_HPIE_LC_HUM.updateEmail';
import updatePrimaryPhoneData from '@salesforce/apex/Pharmacy_HPIE_LC_HUM.updatePrimaryPhone';
import updateAlternatePhoneData from '@salesforce/apex/Pharmacy_HPIE_LC_HUM.updateAlternatePhone';
import updatePrescription from '@salesforce/apex/Pharmacy_HPIE_LC_HUM.updatePrescription';
import updateMemberConsent from '@salesforce/apex/Pharmacy_HPIE_LC_HUM.updateMemberConsent';
import addNewAddress from '@salesforce/apex/Pharmacy_HPIE_LC_HUM.addNewAddress';
import updateAddresses from '@salesforce/apex/Pharmacy_HPIE_LC_HUM.updateAddress';
import updateOrder from '@salesforce/apex/Pharmacy_HPIE_LC_HUM.updateOrder';
import releaseOrder from '@salesforce/apex/Pharmacy_HPIE_LC_HUM.releaseOrder';
import createOrder from '@salesforce/apex/Pharmacy_HPIE_LC_HUM.createOrder'; //create order
import createFill from '@salesforce/apex/Pharmacy_HPIE_LC_HUM.createFill'; // add fills
export default class PharmacyHPIEIntegrationHum extends LightningElement { }

export function getFinanceDetails(personId, userId, organization) {
    return new Promise((resolve, reject) => {
        getFinanceData({ personId: personId, userId: userId, organization: organization, requesttime: new Date().toISOString() })
            .then(result => {
                resolve(result)
            }).catch(error => {
                reject(error);
            })
    });
}

export function getHealthHistoryData(personId, userId, organization) {
    return new Promise((resolve, reject) => {
        getHealthHistoryDetails({ personId: personId, userId: userId, organization: organization, requesttime: new Date().toISOString() })
            .then(result => {
                resolve(result);
            }).catch(error => {
                reject(error);
            })
    });
}

export function getDemographicsDetails(personId, userId, organization) {
    return new Promise((resolve, reject) => {
        getDemographics({ personId: personId, userId: userId, organization: organization, requesttime: new Date().toISOString() })
            .then(result => {
                resolve(result);
            }).catch(error => {
                reject(error);
            })
    });
}

export function getProfileDetails(personId, userId, organization) {
    return new Promise((resolve, reject) => {
        getProfile({ personId: personId, userId: userId, organization: organization, requestedTime: new Date().toISOString() })
            .then(result => {
                resolve(result);
            }).catch(error => {
                reject(error);
            })
    }); 
}

export function getPrescriptions(personId, userId, organization, startDate, endDate) {
    return new Promise((resolve, reject) => {
        getPrescription({ personId: personId, userId: userId, organization: organization, requestedTime: new Date().toISOString(), startDate: startDate, endDate: endDate })
            .then(result => {
                resolve(result);
            }).catch(error => {
                reject(error);
            })
    });
}

export function getEvents(personId, userId, organization, orderId) {
    return new Promise((resolve, reject) => {
        getOrderEvents({ personId: personId, userId: userId, organization: organization, requestedTime: new Date().toISOString(), orderId: orderId })
            .then(result => {
                resolve(result);
            }).catch(error => {
                reject(error);
            })
    });
}

export function getOrderDetailsData(personId, userId, organization, orderId) {
    return new Promise((resolve, reject) => {
        getOrderDetail({ personId: personId, userId: userId, organization: organization, requestedTime: new Date().toISOString(), orderId: orderId })
            .then(result => {
                resolve(result);
            }).catch(error => {
                reject(error);
            })
    });
}

export function getPatientLognotesData(personId, userId, organization, startDate, endDate) {
    return new Promise((resolve, reject) => {
        getPatientLognotes({ personId: personId, userId: userId, organization: organization, startDate: startDate, endDate: endDate })
            .then(result => {
                resolve(result)
            }).catch(error => {
                reject(error);
            })
    });
}

export function getFamilyLognotesData(personId, userId, organization, startDate, endDate,accData) {
    return new Promise((resolve, reject) => {
        getFamilyLognotes({ personId: personId, userId: userId, organization: organization, startDate: startDate, endDate: endDate, accData:accData })
            .then(result => {
                resolve(result)
            }).catch(error => {
                reject(error);
            })
    });
}

export function getTasks(personId, userId, organization) {
    return new Promise((resolve, reject) => {
        getOrderTasks({ personId: personId, userId: userId, organization: organization, requestedTime: new Date().toISOString() })
            .then(result => {
                resolve(result);
            }).catch(error => {
                reject(error);
            })
    });
}

export function getOrders(personId, userId, organization, startDate, endDate) {
    return new Promise((resolve, reject) => {
        getOrdersSummary({ personId: personId, userId: userId, organization: organization, requestedTime: new Date().toISOString(), startDate: startDate, endDate: endDate })
            .then(result => {
                resolve(result);
            }).catch(error => {
                reject(error);
            })
    });
}

export function getPlans(personId, userId, organization) {
    return new Promise((resolve, reject) => {
        getPlanDetails({ personId: personId, userId: userId, organization: organization, requestedTime: new Date().toISOString() })
            .then(result => {
                resolve(result)
            }).catch(error => {
                reject(error);
            })
    });
}

export function createOrders(createOrderRequest) {
    return new Promise((resolve, reject) => {
        createOrder({ jsonrequest: createOrderRequest})
            .then((result) => {
                resolve(result);
            })
            .catch((error) => {
                reject(error); 
            });
    });
}

export function createfills(createFillRequest) { 
    return new Promise((resolve, reject) => {
        createFill({ jsonrequest: createFillRequest})
            .then((result) => {
                resolve(result);
            })
            .catch((error) => { 
                reject(error);
            });
    });
}


export function updateOrders(updateOrderRequest) {
    return new Promise((resolve, reject) => {
        updateOrder({ jsonrequest: updateOrderRequest })
            .then((result) => {
                resolve(result);
            })
            .catch((error) => {
                reject(error);
            });
    });
}

export function releaseOrders(releaseOrderRequest,releaseOrdercheck) { 
    return new Promise((resolve, reject) => {
        releaseOrder({ jsonrequest: releaseOrderRequest,releaseOrdercheck:releaseOrdercheck  })
            .then((result) => {
                resolve(result);
            })
            .catch((error) => {
                reject(error);
            });
    });
}


export function addNewCreditCard(cardRequest) {
    return new Promise((resolve, reject) => {
        addFinance({ parameters: cardRequest })
            .then(result => {
                resolve(result);
            }).catch(error => {
                reject(error);
            })
    });
}

export function updateCreditCard(cardRequest) {
    return new Promise((resolve, reject) => {
        updateFinance({ parameters: cardRequest })
            .then(result => {
                resolve(result);
            }).catch(error => {
                reject(error);
            })
    });
}

export function makeOneTimePayment(paymentRequest) {
    return new Promise((resolve, reject) => {
        addOneTimePayment({ parameters: paymentRequest })
            .then(result => {
                resolve(result);
            }).catch(error => {
                reject(error);
            })
    });
}

export function updatePrescriptionDetails(key, personId, userId, organization, autoRefill, archived) {
    return new Promise((resolve, reject) => {
        updatePrescription({
            key: key, personId: personId, userId: userId,
            organization: organization, requestedTime: new Date().toISOString(),
            autoRefill: autoRefill,
            archived: archived
        }).then(result => {
            resolve(result);
        }).catch(error => {
            reject(error)
        })
    });
}

export function updateMemberProfile() {

}

export function updateAddress(addressRequest) {
	return new Promise((resolve, reject) => {
		updateAddresses({ jsonrequest: addressRequest })
			.then((result) => {
				resolve(result);
			})
			.catch((error) => {
				reject(error);
			});
	});
}

export function addNewAddressDetails(addressRequest) {
	return new Promise((resolve, reject) => {
		addNewAddress({ jsonrequest: addressRequest })
			.then((result) => {
				resolve(result);
			})
			.catch((error) => {
				reject(error);
			});
	});
}

export function updateCustomerPreference() {

}

export function updateEmailAddress(personId, userId, organization, email) {
    return new Promise((resolve, reject) => {
        updateEmailData({ personId: personId, userId: userId, requestedTime: new Date().toISOString(), organization: organization, emailId: email })
            .then(result => {
                resolve(result);
            }).catch(error => {
                reject(error);
            })
    });
}

export function updatePrimaryPhone(personId, userId, organization, primaryPhone) {
    return new Promise((resolve, reject) => {
        updatePrimaryPhoneData({ personId: personId, userId: userId, requestedTime: new Date().toISOString(), organization: organization, primaryPhoneNumber: primaryPhone })
            .then(result => {
                resolve(result);
            }).catch(error => {
                reject(error);
            })
    });
}

export function updateAlternatePhone(personId, userId, organization, alternatePhone) {
    return new Promise((resolve, reject) => {
        updateAlternatePhoneData({ personId: personId, userId: userId, requestedTime: new Date().toISOString(), organization: organization, alternatePhoneNumber: alternatePhone })
            .then(result => {
                resolve(result);
            }).catch(error => {
                reject(error);
            })
    });
}


export function updateCapType(personId, userId, organization, capType) {
    return new Promise((resolve, reject) => {
        updateCapTypeData({ personId: personId, userId: userId, requestedTime: new Date().toISOString(), organization: organization, capType: capType })
            .then(result => {
                resolve(result);
            }).catch(error => {
                reject(error);
            })
    });
}


export function updateMemberConsentDetails(personId, userId, organization, consentBeginDate, memberConsent) {
    return new Promise((resolve, reject) => {
        updateMemberConsent({
            personId: personId, userId: userId, requestedTime: new Date().toISOString(),
            organization: organization, consentBeginDate: consentBeginDate, memberConsent: memberConsent
        }).then(result => {
            resolve(result);
        }).catch(error => {
            reject(error);
        })
    });
}

export function addLogNote() {
    return new Promise((resolve, reject) => {
        resolve(true);
    });
}

export function getPreference(personId, userId, organization) {
    return new Promise((resolve, reject) => {
        getPreferences({ personId: personId, userId: userId, organization: organization, requestedTime: new Date().toISOString() })
            .then(result => {
                resolve(result)
            }).catch(error => {
                reject(error)
            }) 
    });
}

