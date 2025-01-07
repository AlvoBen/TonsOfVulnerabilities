export const getEnrollModal = () => {
  return  bInterventionEnrolls;
}

export const getAvailableModal = () => {
    return  bNotProgramEligible;  
}

const bInterventionEnrolls =[[
    { "compoundx": false, "label": "Program Name", "text": true, "value": "", "fieldName": "programName", "disabled": "No" },
    { "compoundx": false, "label": "Intervention Vendor", "text": true, "value": "", "fieldName": "InterventionVendor", "disabled": "No" },
    { "compoundx": false, "label": "Care Manager Name", "text": true, "value": "", "fieldName": "CareManagerName", "disabled": "No" },
    { "compoundx": false, "label": "Phone Number", "text": true, "value": "", "fieldName": "phoneNumber", "disabled": "No" },
    { "compoundx": false, "label": "Begin Date", "text": true, "value": "", "fieldName": "BeginDate", "disabled": "No" },
    { "compoundx": false, "label": "End Date", "text": true, "value": "", "fieldName": "EndDate", "disabled": "No" }
]]

const bNotInterventionEnroll =[[
    { "compoundx": false, "label": "Program Name", "text": true, "value": "", "fieldName": "programName", "disabled": "No" },
    { "compoundx": false, "label": "Care Manager Name", "text": true, "value": "", "fieldName": "CareManagerName", "disabled": "No" },
    { "compoundx": false, "label": "Phone Number", "text": true, "value": "", "fieldName": "phoneNumber", "disabled": "No" },
    { "compoundx": false, "label": "Begin Date", "text": true, "value": "", "fieldName": "BeginDate", "disabled": "No" },
    { "compoundx": false, "label": "End Date", "text": true, "value": "", "fieldName": "EndDate", "disabled": "No" }   
]]

const bNotProgramEligible =[[
    { "compoundx": false, "label": "Program Name", "text": true, "value": "", "fieldName": "sprogramName", "disabled": "No" },
    { "compoundx": false, "label": "Intervention Vendor", "text": true, "value": "", "fieldName": "InterventionVendor", "disabled": "No" },
    { "compoundx": false, "label": "Begin Date", "text": true, "value": "", "fieldName": "BeginDate", "disabled": "No" },
    { "compoundx": false, "label": "End Date", "text": true, "value": "", "fieldName": "EndDate", "disabled": "No" },
    { "compoundx": false, "label": "Comments", "text": true, "value": "", "fieldName": "Comments", "disabled": "No" },  
]]

const btProgramEligibles =[[
    { "compoundx": false, "label": "Program Name", "text": true, "value": "", "fieldName": "sprogramName", "disabled": "No" },
    { "compoundx": false, "label": "Intervention Vendor", "text": true, "value": "", "fieldName": "InterventionVendor", "disabled": "No" },
    { "compoundx": false, "label": "Program Code", "text": true, "value": "", "fieldName": "ProgramCode", "disabled": "No" }
]]