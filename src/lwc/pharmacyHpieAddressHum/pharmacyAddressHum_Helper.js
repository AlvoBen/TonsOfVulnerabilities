/*--
File Name        : pharmacyAddressHum_Helper.js
Version          : 1.0 
Created Date     : 08/20/2021   
Function         : Lightning Web Component used to display and edit ePost Addresses 
Modification Log :
* Developer                          Date                  Description
*************************************************************************************************
* Jonathan Dickinson			 09/22/2023				   User Story 5061288: T1PRJ0870026   MF 27406 HPIE/CRM - SF Tech- Mail Order Management - Pharmacy -  Details tab - Address section
**************************************************************************************************/

export class USPSRecomendationRequest {
	constructor(addressLine1, addressLine2, city, stateCode, zipCode) {
		this.AddressLine1 = addressLine1;
		this.AddressLine2 = addressLine2;
		this.City = city;
		this.StateCode = stateCode;
		this.ZipCode = zipCode;
		this.IncludeValidationDetails = false;
		this.IncludeInputOnError = false;
		this.IncludeDPV = false;
		this.IncludeGeoCode = false;
		this.IncludeEWS = false;
	}
}

export class identity {
	constructor(organizationPatientId, organization, userId) {
		this.organizationPatientId = organizationPatientId;
		this.organization = organization;
		this.requestTime = new Date().toISOString();
		this.userId = userId;
	}
}

export class addressRequest {
	constructor(
		code,
		description,
		addressLine1,
		addressLine2,
		city,
		stateCode,
		zipCode,
		overrideReason,
		overrideReasonCode,
		uspsValidated,
		active,
		key
	) {
		this.code = code;
		this.description = description;
		this.addressLine1 = addressLine1;
		this.addressLine2 = addressLine2;
		this.city = city;
		this.stateCode = stateCode;
		this.zipCode = zipCode;
		this.overrideReason = overrideReason;
		this.overrideReasonCode = overrideReasonCode;
		this.uspsValidated = uspsValidated;
		this.active = active;
		this.key = key;
	}
}

export const addressTypeConfig = new Map([
	['S', { code: '11', description: 'SHIPPING' }],
	['B', { code: '10', description: 'BILLING' }],
	['P', { code: '1', description: 'HOME' }],
	['A', { code: '9', description: 'ALTERNATE' }]
]);