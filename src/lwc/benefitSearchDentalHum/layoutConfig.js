export const benefitLayout = [
    {
        label: "+/-",
        isExpand: true,
        ExpandIcon: '',
        CollapseIcon: '',
        style: 'width:4%;'
    },{
        label: "Service Category",
        value: '',
        mappingField: 'categoryType',
        style: 'width:12%;'
    },{
        label: "Type of Service",
        value: '',
        mappingField: 'typeOfService',
        style: 'width:12%;'
    },{
        label: "Benefit Type",
        value: '',
        mappingField: 'BenefitType',
        style: 'width:12%;'
    }, {
        label: "Description",
        value: '',
        mappingField: 'BenefitDescription',
        style: 'width:16%;'
    }, {
        label: "Coverage Type",
        value: '',
        mappingField: 'CoverageType',
        style: 'width:10%;'
    }, {
        label: "Place of Service",
        value: '',
        mappingField: 'PlaceOfService',
        style: 'width:10%;'
    }, {
        label: "PAR",
        value: '',
        mappingField: 'Par',
        style: 'width:8%;'
    }, {
        label: "Non PAR",
        value: '',
        mappingField: 'NonPar',
        style: 'width:8%;'
    }, {
        label: "ADA code",
        value: '',
        mappingField: 'AdaCode',
        style: 'width:8%;'
    }
]

export function getModal(name) {
    if (name === 'benefit') {
        return benefitLayout;
    } 
}