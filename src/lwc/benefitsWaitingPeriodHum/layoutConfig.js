export const BenefitsWaitingPeriodColumns = [        
    { 
        label: 'Description', 
        fieldName: 'Description', 
        type: 'text', 
        style: 'width:20%;' 
    },
    { 
        label: 'Start Date', 
        fieldName: 'startDate', 
        style: 'width:10%;' 
    },
    { 
        label: 'End Date', 
        fieldName: 'endDate', 
        style: 'width:10%;' 
    },
    { 
        label: 'Waive Accident/Injuries', 
        fieldName: 'waiveAccident', 
        style: 'width:16%;' 
    },
    { 
        label: 'Person Prior Coverage Reduction', 
        fieldName: 'personPriorCoverageReduction', 
        style: 'width:16%;' 
    },
    { 
        label: 'Member Eligibility Reduction', 
        fieldName: 'memberEligibiltyReduction', 
        style: 'width:16%;' 
    }
]

export function getModel() {
    return BenefitsWaitingPeriodColumns;
}