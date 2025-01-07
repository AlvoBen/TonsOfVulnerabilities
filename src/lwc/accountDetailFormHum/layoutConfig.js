/*******************************************************************************************************************************
LWC JS Name : layoutConfig.js
Function    : This JS serves as helper to accountDetailFormHum.js. 

Modification Log: 
Developer Name           Code Review                      Date                         Description
*--------------------------------------------------------------------------------------------------------------------------------
* Supriya Shastri                                         04/07/2021                   initial version
*********************************************************************************************************************************/
export function getFormLayout(formType) {
    switch (formType) {
        case 'Inquiry Details':
            return inqType;
            break;
        case 'Task Details':
            return taskType;
            break;
        case 'Web Response':
            return webType;
            break;
    }
}

const taskType = [
        { label: 'Created By', mapping: 'sCreatedBy', value: '' },
        { label: 'Action Time', mapping: 'sActionTime', value: '' },
        { label: 'Task ID', mapping: 'taskID', value: '' },
        { label: 'Action', mapping: 'sActionDesc', value: '' },
        { label: 'Date', mapping: 'sDate', value: '' },
        { label: 'Result', mapping: 'sResultDesc', value: '' },
        { label: 'Status', mapping: 'sStatus', value: '' },
        { label: 'Srcv Ctr', mapping: 'sSrvCtr', value: '' },
        { label: 'Priority', mapping: 'sPriorityDesc', value: '' },
        { label: 'Dept', mapping: 'sDept', value: '' },
        { label: 'Task Type', mapping: 'sTaskTypeDesc', value: '' },
        { label: 'Team', mapping: 'sTeam', value: '' },
        { label: 'Due Date', mapping: 'sDueDate', value: '' },
        { label: 'User', mapping: 'sUser', value: '' }
    ];

const inqType = [
        { label: 'Archived CCP Inquiry Details Number', mapping: 'CONTACT_ID', value: '' },
        { label: 'Service Center', mapping: 'CREATED_SITE_NAME', value: '' },
        { label: 'Created By', mapping: 'CREATED_BY', value: '' },
        { label: 'Priority', mapping: 'PRIORITY_CD_DESC', value: '' },
        { label: 'Created Date', mapping: 'CREATED_TS', value: '' },
        { label: 'Department', mapping: 'OWNER_DEPT_NAME', value: '' },
        { label: 'Status', mapping: 'STATUS_CD_DESC', value: '' },
        { label: 'Team', mapping: 'OWNER_TEAM_NAME', value: '' },
        { label: 'Category', mapping: 'CATEGORY_CD_DESC', value: '' },
        { label: 'User', mapping: 'OWNER_FIRST_NAME', value: '' },
        { label: 'Reason', mapping: 'REASON_CD_DESC', value: '' },
        { label: 'DCN', mapping: 'DCN', value: '' },
        { label: 'Disposition', mapping: 'DISPOSITION_DESC', value: '' }
]

const webType = [
    { label: 'Web Question', mapping: 'WebQuestion', value: '' },
    { label: 'Response', mapping: 'SelectAResponse', value: '' },
    { label: 'Additional Details', mapping: 'AdditionalDetails', value: '' },
    { label: 'Claim Number', mapping: 'ClaimNumber', value: '' },
    { label: 'Claim DOB', mapping: 'ClaimDOS', value: '' },
    { label: 'Web Response', mapping: 'WebResponse', value: '' }
];