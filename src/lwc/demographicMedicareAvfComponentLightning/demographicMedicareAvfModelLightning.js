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
        title: 'Interaction and Address Update Request',
        fields: [
            {
                label: 'Person Speaking With', value: undefined, foreignKey: 1, display: false, propName: "value",
                order: 1,
                mapper: {
                    avfRequestFieldName: "PersonSpeakingWith", concat: false, concatOrder: undefined
                }
            },
            {
                label: 'Relationship to Member', value: undefined, foreignKey: 2, display: false, propName: "value",
                order: 2,
                mapper: {
                    avfRequestFieldName: "RelationToMember", concat: false, concatOrder: undefined
                }
            }
        ]
    },
    {
        title: 'Residential Address Information',
        fields: [
            {
                label: 'Permanent Residential Address Line 1', value: undefined, foreignKey: 5, display: false, propName: "value",
                order: 1,
                mapper: {
                    avfRequestFieldName: "PermanentResidentialAddress",  concat: true, concatOrder: [1, 2]
                }
            },
            {
                label: 'Permanent Residential Address Line 2', value: undefined, foreignKey: 29, display: false, propName: "value",
                order: 2, ignore: true,
                mapper: {
                    avfRequestFieldName: "PermanentResidentialAddress", concat: false, concatOrder: undefined
                }
            },
            {
                label: 'Residential City', value: undefined, foreignKey: 6, display: false, propName: "value",
                order: 3,
                mapper: {
                    avfRequestFieldName: "PermanentResidentialCityName", concat: false, concatOrder: undefined
                }
            },
            {
                label: 'Residential State', value: undefined, foreignKey: 7, display: false, propName: "value",
                order: 4,
                mapper: {
                    avfRequestFieldName: "PermanentResidentialStateCode", concat: false, concatOrder: undefined
                }
            },
            {
                label: 'Residential Zip Code', value: undefined, foreignKey: 8, display: false, propName: "value",
                order: 5,
                mapper: {
                    avfRequestFieldName: "PermanentResidentialZipCode", concat: false, concatOrder: undefined
                }
            },
            {
                label: 'Residential County', value: undefined, foreignKey: 9, display: false, propName: "value",
                order: 6,
                mapper: {
                    avfRequestFieldName: "PermanentResidentialCountyName", concat: false, concatOrder: undefined
                }
            },
            {
                label: 'Effective Moving Date Address', value: undefined, foreignKey: 10, display: false, propName: "value",
                order: 7,
                mapper: {
                    avfRequestFieldName: "PermanentResidentialStartDate", concat: false, concatOrder: undefined
                }
            },
            {
                label: 'Does the member have a mailing address different from their permanent residential address?', 
                order: 8,
                value: undefined, foreignKey: 11, display: false, propName: "valueLabel",
                mapper: {
                    avfRequestFieldName: "DoesMemberHaveMailAddressDiffFromPermAddress", concat: false, concatOrder: undefined
                }
            },
            {
                label: 'Does the member have a temporary address?', value: undefined, foreignKey: 12, display: false, propName: "valueLabel",
                order: 9,
                mapper: {
                    avfRequestFieldName: "DoesResMemberHaveTemporaryAddress", concat: false, concatOrder: undefined
                }
            },
            {
                label: 'Does the member want their mail sent to their temporary address?', 
                value: undefined, foreignKey: 13, display: false, propName: "valueLabel",
                order: 10,
                mapper: {
                    avfRequestFieldName: "DoesMemberWantMailSentToTemporaryAddress", concat: false, concatOrder: undefined
                }
            },
            {
                label: 'Does the member want their mailing address different from their residential address?',
                value: undefined, foreignKey: 14, display: false, propName: "valueLabel",
                order: 11,
                mapper: {
                    avfRequestFieldName: "DoesMemberWantMailAddressDiffFromResAddress", concat: false, concatOrder: undefined
                }
            }
        ]
    },
    {
        title: 'Mailing Address Information',
        fields: [
            {
                label: 'Mailing Address Line 1', value: undefined, foreignKey: 15, display: false, propName: "value",
                order: 1,
                mapper: {
                    avfRequestFieldName: "MailingAddress", concat: true, concatOrder: [1, 2]
                }
            },
            {
                label: 'Mailing Address Line 2', value: undefined, foreignKey: 30, display: false, propName: "value",
                order: 2, ignore: true,
                mapper: {
                    avfRequestFieldName: "MailingAddress", concat: false, concatOrder: undefined
                }
            },
            {
                label: 'Mailing City', value: undefined, foreignKey: 16, display: false, propName: "value",
                order: 3,
                mapper: {
                    avfRequestFieldName: "MailingCityName", concat: false, concatOrder: undefined
                }
            },
            {
                label: 'Mailing State', value: undefined, foreignKey: 17, display: false, propName: "value",
                order: 4,
                mapper: {
                    avfRequestFieldName: "MailingStateCode", concat: false, concatOrder: undefined
                }
            },
            {
                label: 'Mailing Zip Code', value: undefined, foreignKey: 18, display: false, propName: "value",
                order: 5,
                mapper: {
                    avfRequestFieldName: "MailingZipCode", concat: false, concatOrder: undefined
                }
            },
            {
                label: 'Mailing County', value: undefined, foreignKey: 19, display: false, propName: "value",
                order: 6,
                mapper: {
                    avfRequestFieldName: "MailingCounty", concat: false, concatOrder: undefined
                }
            },
            {
                label: 'Does the member have a temporary address?', value: undefined, foreignKey: 20, display: false, propName: "valueLabel",
                order: 7,
                mapper: {
                    avfRequestFieldName: "DoesMailMemberHaveTemporaryAddress", concat: false, concatOrder: undefined
                }
            },
            {
                label: 'Does the member have a residential address different from their mailing address provided?', 
                value: undefined, foreignKey: 21, display: false, propName: "valueLabel", order: 8,
                mapper: {
                    avfRequestFieldName: "DoesMemberHaveResAddressDiffFromMailAddress", concat: false, concatOrder: undefined
                }
            }
        ]
    },
    {
        title: 'Temporary Address Information',
        fields: [
            {
                label: 'Temporary Address Line 1', value: undefined, foreignKey: 22, display: true, propName: "value",
                order: 1,
                mapper: {
                    avfRequestFieldName: "TemporaryAddress", concat: true, concatOrder: [1, 2]
                }
            },
            {
                label: 'Temporary Address Line 2', value: undefined, foreignKey: 31, display: true, propName: "value",
                order: 2, ignore: true,
                mapper: {
                    avfRequestFieldName: "TemporaryAddress", concat: false, concatOrder: undefined
                }
            },
            {
                label: 'Temporary City', value: undefined, foreignKey: 23, display: true, propName: "value",
                order: 3,
                mapper: {
                    avfRequestFieldName: "TemporaryCityName", concat: false, concatOrder: undefined
                }
            },
            {
                label: 'Temporary State', value: undefined, foreignKey: 24, display: true, propName: "value",
                order: 4,
                mapper: {
                    avfRequestFieldName: "TemporaryStateCode", concat: false, concatOrder: undefined
                }
            },
            {
                label: 'Temporary Zip Code', value: undefined, foreignKey: 25, display: true, propName: "value",
                order: 5,
                mapper: {
                    avfRequestFieldName: "TemporaryZipCode", concat: false, concatOrder: undefined
                }
            },
            {
                label: 'Temporary County', value: undefined, foreignKey: 26, display: true, propName: "value",
                order: 6,
                mapper: {
                    avfRequestFieldName: "TemporaryCountyName", concat: false, concatOrder: undefined
                }
            },
            {
                label: 'Temporary Absence Start Date', value: undefined, foreignKey: 27, display: true, propName: "value",
                order: 7,
                mapper: {
                    avfRequestFieldName: "TemporaryOSAStartDate", concat: false, concatOrder: undefined
                }
            },
            {
                label: 'Temporary Absence End Date', value: undefined, foreignKey: 28, display: true, propName: "value",
                order: 8,
                mapper: {
                    avfRequestFieldName: "TemporaryOSAEndDate", concat: false, concatOrder: undefined
                }
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