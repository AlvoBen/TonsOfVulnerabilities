export class pharmacyWebIssuesFlowParameters{
    constructor(MemberName, MemberId, MemberDOB, caseid, TN, paraminputSID,
        SubmissionData, TicketRequired, hasprocess){
            if(hasprocess){
                this.flowParams = [
                    {
                        name: "ParamInputSID",
                        type: "String",
                        value: paraminputSID,
                    },
                    {
                        name: "WebIssues_Summary_Data",
                        type: "String",
                        value: SubmissionData != null && SubmissionData != undefined
                        ? SubmissionData : '',
                    },
                    {
                        name: "Ticket_Required",
                        type: "Boolean",
                        value: TicketRequired === 'true' ? true : false,
                    },
                    {
                        name: "TN",
                        type: "String",
                        value: TN,
                    },
                ];
            }else{
                this.flowParams = [
                    {
                        name: "Members_Name",
                        type: "String",
                        value: MemberName != null && MemberName != undefined
                            ? MemberName : '',
                    },
                    {
                        name: "Members_ID",
                        type: "String",
                        value: MemberId != null && MemberId != undefined
                            ? MemberId : '',
                    },
                    {
                        name: "Members_DateOf_Birth",
                        type: "String",
                        value: MemberDOB != null && MemberDOB != undefined
                            ? MemberDOB : '',
                    },
                    {
                        name: "Case_ID",
                        type: "String",
                        value: caseid,
                    },
                    {
                        name: "TN",
                        type: "String",
                        value: TN,
                    },
                    {
                        name: "ParamInputSID",
                        type: "String",
                        value: paraminputSID,
                    },
                ];
            }
        }
}