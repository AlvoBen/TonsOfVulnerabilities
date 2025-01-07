/*--
File Name        : uspsValidationHum_Helper.js
Version          : 1.0 
Created Date     : 08/20/2021   
Function         : Lightning Web Component used to validate an address against USPS
Modification Log :
* Developer                          Date                  Description
*************************************************************************************************
* Kiran Bhuvanagiri                 08/20/2021            User Story 2421627: Original Version
**************************************************************************************************/

export class ePostAddressDTO {
    constructor(addressLine1, addressLine2, city, stateCode, zipCode, addressType, uspsValidate) {
        this.zipCode = zipCode;
        this.uspsValidate = uspsValidate;
        this.stateCode = stateCode;
        this.overrideReasonCode = "1";
        this.isActive = 'true';
        this.city = city;
        this.addressType = addressType;
        this.addressName = '';
        this.addressLine2 = addressLine2;
        this.addressLine1 = addressLine1;
        this.addressKey = "-1";
    }
}