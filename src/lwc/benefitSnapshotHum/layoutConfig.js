export const benefitSnapshotMedicalModel = [{
    label : 'Copay',
    Description : 'Copay Office Visit',
    data : []
},{
    label : 'Deductible',
    Description : 'Deductible',
    data : []
},{
    label : 'CoInsurance',
    Description : 'Co-Insurance',
    data : []
},{
    label : 'Out of Pocket',
    Description : 'Out of Pocket',
    data : []
},{
    label : 'Max out of Pocket',
    Description : 'Max Out Of Pocket',
    data : []
},{
    label : 'SuperMoop',
    Description : 'SuperMoop',
    data : []
},{
    label : 'Limitation',
    Description : 'Limitation',
    data : []
},{
    label : 'BenefitIndicator',
    Description : 'Benefit Indicator',
    data : []
}]

export const benefitSnapshotDentalModel = [{
    label : 'Limitation',
    Description : 'Limitation',
    data : []
}
,{
    label : 'Deductible',
    Description : 'Deductible',
    data : []
},{
    label : 'Co-Insurance',
    Description : 'Co-Insurance',
    data : []
},{
    label : 'Out of Pocket',
    Description : 'Out of Pocket',
    data : []
},{
    label : 'Max out of Pocket',
    Description : 'Max Out Of Pocket',
    data : []
},{
    label : 'SuperMoop',
    Description : 'SuperMoop',
    data : []
},{
    label : 'Copay',
    Description : 'Copay Office Visit',
    data : []
},{
    label : 'BenefitIndicator',
    Description : 'Benefit Indicator',
    data : []
}]
export function getModel(modelName) {
    if(modelName === 'benefitSnapShot'){
        return benefitSnapshotModel;
    }else if(modelName === 'benefitSnapshotColumns'){
        return BenefitSnapshotColumns;
    }else if(modelName === 'benefitSnapshotMedical'){
        return benefitSnapshotMedicalModel;
    }
 else if(modelName === 'benefitSnapshotDental'){
        return benefitSnapshotDentalModel;
    }
 else if(modelName === 'benefitSnapshotDentalColumns'){
        return benefitSnapshotDentalColumns;
    }
    else if(modelName === 'medicalBenefitCategoryCodes'){
        return MedicalBenefitsCategories;
    }
    else if(modelName === 'dentalBenefitCategoryCodes'){
        return DentalBenefitsCategories;
    }
    else if(modelName === 'benefitSubCategories'){
        return DentalBenefitsSubCategories;
    }
}

export const BenefitSnapshotColumns = [{
    label : 'Description',
    style : 'width:40%'
},{
    label : 'Tier',
    style : 'width:20%'
},{
    label : 'In-Network',
    style : 'width:20%'
},{
    label : 'Out-Network',
    style : 'width:20%'
}]

export const benefitSnapshotDentalColumns = [{
    label : 'Description',
    style : 'width:50%'
},{
    label : 'In-Network',
    style : 'width:25%'
},{
    label : 'Out-Network',
    style : 'width:25%'
}]

export const MedicalBenefitsCategories = [{
    label: 'Copay',
    code: '1'
}, {
    label: 'Deductible',
    code: '2'
}, {
    label: 'CoInsurance',
    code: '3'
}, {
    label: 'Out of Pocket',
    code: '4'
}, {
    label: 'Max out of Pocket',
    code: '5'
}, {
    label: 'SuperMoop',
    code: '6'
}, {
    label: 'Limitation',
    code: '7'
}, {
    label: 'BenefitIndicator',
    code: '8'
}]

export const DentalBenefitsCategories = [{
    label: 'Deductible',
    code: '1'
}, {
    label: 'Out of Pocket',
    code: '2'
}, {
    label: 'Max out of Pocket',
    code: '3'
}, {
    label: 'Co-Insurance',
    code: '4'
}, {
    label: 'SuperMoop',
    code: '5'
}, {
    label: 'Copay',
    code: '6'
}, {
    label: 'Limitation',
    code: '7'
}, {
    label: 'BenefitIndicator',
    code: '8'
}]

const DentalBenefitsSubCategories = [
    {
        category: 'Max out of Pocket',
        codes: [{
            label: 'Annual Maximum',
            code: '1',
        }, {
            label: 'Orthodontic Maximum',
            code: '2'
        }]
    }, {
        category: 'CoInsurance',
        codes: [{
            label: 'Preventive',
            code: '1'
        }, {
            label: 'Basic',
            code: '2'
        }, {
            label: 'Major',
            code: '3'
        }, {
            label: 'Orthodontic',
            code: '4'
        }]
    }
]