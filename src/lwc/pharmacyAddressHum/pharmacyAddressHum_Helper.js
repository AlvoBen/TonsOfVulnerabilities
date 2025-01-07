/*--
File Name        : pharmacyAddressHum_Helper.js
Version          : 1.0 
Created Date     : 08/20/2021   
Function         : Lightning Web Component used to display and edit ePost Addresses 
Modification Log :
* Developer                          Date                  Description
*************************************************************************************************
* Kiran Bhuvanagiri                 08/20/2021            User Story 2421627: Original Version
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

export class ePostUpdateAddressRequest {
    constructor(enterpriseID, networkID) {
        this.EditMemberRequest = {
            members: [
                {
                    phones: null,
                    logNotes: null,
                    EnterprisePersonID: enterpriseID,
                    customerPreference: null,
                    customerDetail: null,
                    creditCards: null,
                    communicationPreferences: null,
                    addresses: {
                        address: [
                            {
                                zipCode: '',
                                uspsValidate: false,
                                stateCode: '',
                                overrideReasonCode: "1",
                                isActive: '',
                                city: '',
                                addressType: '',
                                addressName: '',
                                addressLine2: '',
                                addressLine1: '',
                                addressKey: "-1"
                            }
                        ]
                    }
                }
            ],
            CustomerServiceId: networkID
        }
    }
}