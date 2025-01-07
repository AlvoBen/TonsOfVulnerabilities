export function getDetailFormLayout(){
    const detail = {
        object: { name: "Authorization/Referral Details", icon: { size: "large", name: "standard:contact" } },
        recodDetail: {
            name: "", mapping: "Name"
        },
        ContactInformation:{
            fields:[
                { label: "Created Date", bIsCustom: false, mapping: "CreatedDate",value:''},
                { label: "Contact Method", bIsCustom: false, mapping: "ContactMethod",value:''},
            
            ]
        },
        authInfo:{
            inPatient:[
                { row:'1', label: "Admission Type", bIsCustom: false, mapping: "AdmissionType",value:''},
                { row:'1', label: "Request Type", bIsCustom: false, mapping: "RequestType",value:''},
                { row:'1', label: "Denial Reason", bIsCustom: false, mapping: "DenialReason",value:''},
                { row:'1', label: "Claims Payment Notes", bIsCustom: false, mapping: "ClaimPaymentNotes",value:''},
                { row:'2', label: "Authorization Type", bIsCustom: false, mapping: "AuthType",value:'', helpText:'Indicates whether the service request is Inpatient, Outpatient, BH Inpatient, BH Outpatient. Referrals will be listed as Authorization Type of Outpatient or BH Outpatient. BH stands for Behavioural Health.'},
                { row:'2', label: "Admission date", bIsCustom: false, mapping: "AdmissionDate",value:''},
                { row:'2', label: "Last Coverage Date", bIsCustom: false, mapping: "LastCoveredDate",value:'', helpText:'The first date services can be rendered.'},
                { row:'2', label: "Discharge Date", bIsCustom: false, mapping: "DischargeDate",value:'', helpText:'The expected last date of service.'},
                { row:'2', label: "Total Days Approved", bIsCustom: false, mapping: "TotalDaysApproved",value:'', helpText:'Calculation of days between First Day and Last Day.'},
                { row:'2', label: "Next Review Date", bIsCustom: false, mapping: "NextReviewDate",value:''},
   
            ],
			outPatient:[
                { row:'1', label: "Service Type", bIsCustom: false, mapping: "ServiceType",value:''},
                { row:'1', label: "Request Type", bIsCustom: false, mapping: "RequestType",value:''},
                { row:'1', label: "Denial Reason", bIsCustom: false, mapping: "DenialReason",value:''},
                { row:'1', label: "Claims Payment Notes", bIsCustom: false, mapping: "ClaimPaymentNotes",value:''},
                { row:'2', label: "Authorization Type", bIsCustom: false, mapping: "AuthType",value:'', helpText:'Indicates whether the service request is Inpatient, Outpatient, BH Inpatient, BH Outpatient. Referrals will be listed as Authorization Type of Outpatient or BH Outpatient. BH stands for Behavioural Health.'},
                { row:'2', label: "First Day", bIsCustom: false, mapping: "FirstDay",value:'', helpText:'The first date services can be rendered.'},
                { row:'2', label: "Last Day", bIsCustom: false, mapping: "LastDay",value:'', helpText:'The expected last date of service.'},
                { row:'2', label: "Total Days", bIsCustom: false, mapping: "TotalDaysApproved",value:'', helpText:'Calculation of days between First Day and Last Day.'},
                { row:'2', label: "Next Review Date", bIsCustom: false, mapping: "NextReviewDate",value:''},
   
            ],
           

        },
        diagonsisDetail:{
            fields:[
             //   { row:'1', label: "", bIsCustom: false, mapping: "Type",value:''},
                { row:'1', label: "Primary Diagnosis", bIsCustom: false, mapping: "Code",value:''},
                { row:'2', label: "Description", bIsCustom: false, mapping: "Description",value:''},
                { row:'3', label: "Status", bIsCustom: false, mapping: "Status",value:''},
            ]
        },  
        ProviderInformation:{
            fields:[
                { label: "Type", bIsCustom: false, mapping: "Type",value:''},
                { label: "Id", bIsCustom: false, mapping: "Id",value:''},
                { label: "Name", bIsCustom: false, mapping: "Name",value:''},
                { label: "TaxId", bIsCustom: false, mapping: "TaxId",value:''},
                { label: "ParticipatingStatus", bIsCustom: false, mapping: "ParticipatingStatus",value:''},
                { label: "AddressDetails", bIsCustom: true, mapping: "AddressDetails",value:''},
            ]
        }, 
        ProcedureInformation:{
            fields:[
                { label: "Code", bIsCustom: false, mapping: "Code",value:''},
                { label: "Description", bIsCustom: false, mapping: "Description",value:''},
                { label: "Requesting Units", bIsCustom: false, mapping: "Requesting Units",value:''},
                { label: "Authorized Units", bIsCustom: false, mapping: "Authorized Units",value:''},
                { label: "CType of Units", bIsCustom: false, mapping: "Type of Units",value:''},
                { label: "Status", bIsCustom: false, mapping: "Status",value:''}
            ]
        }
    }
    return detail;
};