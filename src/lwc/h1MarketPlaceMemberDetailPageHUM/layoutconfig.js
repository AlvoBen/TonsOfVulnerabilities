export function getDetailFormLayout(){
    const detail = [
        {label: 'First Name', mapping: 'sFirstName', value: ''},
        {label: 'Country Name', mapping: 'sCountyName', value: ''},
        {label: 'Last Name', mapping: 'sLastName', value: ''},
        {label: 'Mailing Address Apt#', mapping: 'sMailaddr', value: ''},        
        {label: 'Date of Birth', mapping: 'sDOB', value: ''},        
        {label: 'Mailing City', mapping: 'sMailCity', value: ''},
        {label: 'Gender', mapping: 'sGender', value: ''},
        {label: 'Mailing State', mapping: 'sMailSt', value: ''},
        {label: 'Permanent Address Apt#', mapping: 'sPermanentAddress', value: ''},
        {label: 'Mailing Zip', mapping: 'sMailZip', value: ''},
        {label: 'City', mapping: 'sCity', value: ''},
        {label: 'Home phone', mapping: 'sHomePh', value: ''},
        {label: 'State', mapping: 'sState', value: ''},
        {label: 'Work Phone', mapping: 'sWorkPh', value: ''},
        {label: 'Zip', mapping: 'sZip', value: ''},
        {label: 'Smoking Indicator', mapping: 'sSmokingIndicator', value: ''}
    ];
    return detail;
}

export function getBenifitsInfoLayout(){
    const detail =[{Title:'Billing',gridSize:'4',
                data:[
                    {section:'Billing Information',label: 'Payment Option', mapping: 'sPymtOpt', value: ''},
                    {section:'Billing Information',label: 'APTC Amount', mapping: 'saptcAmount', value: ''},
                    {section:'Billing Information',label: 'Cost Share Reduction Level', mapping: 'scsrLevel', value: ''},
                    {section:'Billing Information',label: 'APTC End Date', mapping: 'saptcEndDate', value: ''},
                    {section:'Billing Information',label: 'Cost Share Effective Date', mapping: 'scsrEffDate', value: ''},
                    {section:'Billing Information',label: 'APTC Effective Date', mapping: 'saptcEffDate', value: ''},
                    {section:'Billing Information',label: 'Cost Share End Date', mapping: 'sCsrEndDate', value: ''},
                    ]},
                    {Title:'Benefit & Agent',gridSize:'3',
                data:[
                    {section:'Benefit Information',label: 'Group Id', mapping: 'sGroupId', value: ''},
                    {section:'Benefit Information',label: 'BSN', mapping: 'sStrBsn', value: ''},
                    {section:'Benefit Information',label: 'Signature Date', mapping: 'sSignDATE', value: ''},
                    {section:'Benefit Information',label: 'Effective Date', mapping: 'scovEffDt', value: ''},
                    {section:'Benefit Information',label: 'Received Date', mapping: 'sMktRcptDate', value: ''},
                    {section:'Agent Information',label: 'Member Plan Rep', mapping: 'ssalesRepName', value: ''},
                    {section:'Agent Information',label: 'Agent SSN', mapping: 'sSalesType', value: ''},
                    {section:'Agent Information',label: 'Agent/Broker Type', mapping: 'sSalesType', value: ''},
                    ]},
                    {Title:'Application Misc',gridSize:'3',
                data:[
                    {section:'Application Information',label: 'File Date', mapping: 'sFileDt', value: ''},
                    {section:'Application Information',label: 'QEC Confirmation', mapping: 'sOecConfId', value: ''},
                    {section:'Application Information',label: 'Application Channel', mapping: 'sApplnChnl', value: ''},
                    {section:'Application Information',label: 'Time Stamp', mapping: 'sCreationTs', value: ''},
                    {section:'Application Information',label: 'Processed Date', mapping: 'sProcDt', value: ''},
                    {section:'Application Information',label: 'Bar Code', mapping: 'sBarCodeBase', value: ''},
                    ]}
                        ];
    return detail;
}