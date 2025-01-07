let AddressListResponse = {
    Id: "",
    SuccessFlag: "",
    AddressLine1: "",
    AddressLine2: "",
    City: "",
    StateCode: "",
    ZipCode: "",
    StateID: "",
    ZipCodePlus: "",
    CountyName: "",
    CountyID: "",
    Longitude: "",
    Latitude: "",
    Deliverable: "",
    ReturnCode: "",
    Valid: "",
    ChangeIndicator: ""
}

let MailingAddressListResponse = {
    Id: "",
    SuccessFlag: "",
    AddressLine1: "",
    AddressLine2: "",
    City: "",
    StateCode: "",
    ZipCode: "",
    StateID: "",
    ZipCodePlus: "",
    CountyName: "",
    CountyID: "",
    Longitude: "",
    Latitude: "",
    Deliverable: "",
    ReturnCode: "",
    Valid: "",
    ChangeIndicator: ""
}

let TempAddressListResponse = {
    Id: "",
    SuccessFlag: "",
    AddressLine1: "",
    AddressLine2: "",
    City: "",
    StateCode: "",
    ZipCode: "",
    StateID: "",
    ZipCodePlus: "",
    CountyName: "",
    CountyID: "",
    Longitude: "",
    Latitude: "",
    Deliverable: "",
    ReturnCode: "",
    Valid: "",
    ChangeIndicator: ""
}

let mailingAddressStandardizedResponseDTO = {
    addressStandardizedResponse: {
        StandardizeAddressResponseList: {
            AddressList: [MailingAddressListResponse]
        }
    }
}

let tempAddressStandardizedResponseDTO = {
    addressStandardizedResponse: {
        StandardizeAddressResponseList: {
            AddressList: [TempAddressListResponse]
        }
    }
}

let addressStandardizedResponseDTO = {
    addressStandardizedResponse: {
        StandardizeAddressResponseList: {
            AddressList: [AddressListResponse]
        }
    }
}

let AddressListRequest = {
    // Id: "00",
    AddressLine1: "",
    AddressLine2: "",
    // AddressLine3: "",
    City: "",
    StateCode: "",
    ZipCode: "",
    IncludeValidationDetails: false,
    IncludeInputOnError: false,
    IncludeDPV: false,
    IncludeGeoCode: true,
    IncludeEWS: false
}

let StandardizeAddressRequestList = {
    AddressList: [AddressListRequest]
}

let addressDisplayData = {
    AddressLine1: "",
    AddressLine2: "",
    City: "",
    StateCode: "",
    ZipCode: "",
    CountyName: "",
}

let OSARequestModel = {
    isOSARequest : {
        PlanID: [],
        FipsCode: "",
        ZipCode: ""
    }
}

let OSAResponseItem = {
    PlanID: "",
    OSA: ""
}

let OSAResponseModel = {
    isOSAResponse: [OSAResponseItem]
}

let OSAReplyModel = {
    osaServiceResponse: OSAResponseModel,
    serviceCalloutError: "",
    calloutErrored: false
}

const summaryDataModel = [
    {
        title: 'Residential Address Information',
        fields: [
            {
                label: 'Residential Address 1', value: undefined, foreignKey: 2, display: false, propName: "value"
            },
            {
                label: 'Residential Address 2', value: undefined, foreignKey: 3, display: false, propName: "value"
            },
            {
                label: 'Residential City', value: undefined, foreignKey: 4, display: false, propName: "value"
            },
            {
                label: 'Residential State', value: undefined, foreignKey: 5, display: false, propName: "value"
            },
            {
                label: 'Residential Zip Code', value: undefined, foreignKey: 6, display: false, propName: "value"
            },
            {
                label: 'Residential County', value: undefined, foreignKey: 7, display: false, propName: "value"
            },
            {
                label: 'Mailing Address same as Residential Address?', value: undefined, foreignKey: 8, display: false, propName: "valueLabel"
            }
        ]
    },
    {
        title: 'Mailing Address Information',
        fields: [
            {
                label: 'Mailing Address 1', value: undefined, foreignKey: 9, display: false, propName: "value"
            },
            {
                label: 'Mailing Address 2', value: undefined, foreignKey: 10, display: false, propName: "value"
            },
            {
                label: 'Mailing City', value: undefined, foreignKey: 11, display: false, propName: "value"
            },
            {
                label: 'Mailing State', value: undefined, foreignKey: 12, display: false, propName: "value"
            },
            {
                label: 'Mailing Zip Code', value: undefined, foreignKey: 13, display: false, propName: "value"
            },
            {
                label: 'Mailing County', value: undefined, foreignKey: 14, display: false, propName: "value"
            },
            {
                label: 'Residential Address same as Mailing Address?', value: undefined, foreignKey: 15, display: false, propName: "valueLabel"
            }
        ]
    }
]

export {
    AddressListRequest,
    AddressListResponse,
    MailingAddressListResponse,
    TempAddressListResponse,
    StandardizeAddressRequestList,
    addressStandardizedResponseDTO,
    mailingAddressStandardizedResponseDTO,
    tempAddressStandardizedResponseDTO,
    addressDisplayData,
    OSARequestModel,
    OSAReplyModel,
    summaryDataModel
}