export const benefitLayout = [
    {
        label : "+/-",
        isExpand: true,
        ExpandIcon : '',
        CollapseIcon : '',
        style : 'width:4%;'
    },{
    label : "Cause Code",
        value : '',
        mappingField : 'CASCauseCode',
        style : 'width:10%;'

    },{
        label : "Service Category",
        value : '',
        mappingField : 'ServiceCategory',
        style : 'width:10%;'
    },{
        label : "Type of Service",
        value : '',
        mappingField : 'TypeOfService',
        style : 'width:10%;'
    },{
        label : "Benefit Type",
        value : '',
        mappingField : 'BenefitType',
        style : 'width:10%;'
    },{
        label : "Description",
        value : '',
        mappingField : 'BenefitDescription',
        style : 'width:10%;'
    },{
        label : "Coverage Type",
        value : '',
        mappingField : 'CoverageType',
        style : 'width:10%;'
    },{
        label : "Place of Service",
        value : '',
        mappingField : 'PlaceOfService',
        style : 'width:10%;'
    },{
        label : "TIER",
        value : '',
        mappingField : 'TierNumber',
        style : 'width:8%;'
    },{
        label : "PAR",
        value : '',
        mappingField : 'Par',
        style : 'width:8%;'
    },{
        label : "Non PAR",
        value : '',
        mappingField : 'NonPar',
        style : 'width:8%;'
    }
]

export  function getModal(name) {
    if(name === 'benefit'){
        return benefitLayout;
    }else if('benefit child'){
        return '';
    }
}