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

export class addressRequest {
    constructor(personId, organization, userId, code, description, addressLine1, addressLine2, city, stateCode, zipCode, overrideReason, overrideReasonCode, uspsValidated, active, key) {
        this.organizationPatientId = personId;
        this.organization = organization;
        this.requestTime = new Date().toISOString();
        this.userId = userId;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.city = city;
        this.stateCode = stateCode;
        this.zipCode = zipCode;
        this.code = code;
        this.description = description;
        this.overrideReason = overrideReason;
        this.overrideReasonCode = overrideReasonCode;
        this.uspsValidated = uspsValidated;
        this.active = active;
        this.key = key;
    }
}

class addressDTO {
    constructor(code, description, addressLine1, addressLine2, city, stateCode, zipCode, overrideReason, overrideReasonCode, uspsValidated, active) {
        this.type = new typeDTO(code, description);
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.city = city;
        this.stateCode = stateCode;
        this.zipCode = zipCode;
        this.overrideReason = overrideReason;
        this.overrideReasonCode = overrideReasonCode;
        this.uspsValidated = uspsValidated;
        this.active = active;
    }
}

class typeDTO {
    constructor(code, description) {
        this.code = code;
        this.description = description;
    }
}

export class EASRequest {
    constructor(addressLine1, addressLine2, addresscity, addressstatecode, addresszipcode,) {
        this.AddressLine1 = addressLine1;
        this.AddressLine2 = addressLine2;
        this.City = addresscity;
        this.StateCode = addressstatecode;
        this.ZipCode = addresszipcode;
    }
}