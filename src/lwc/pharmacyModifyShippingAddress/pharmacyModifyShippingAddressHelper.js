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