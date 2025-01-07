export class invoiceRequestHelper {
    constructor(hasprocess, submissionData,sid,TN,emailaddress,billAddress,
        perAddress,shipAddress){
            if(hasprocess){
                this.flowParams = [
                    {
                        name: 'Invoice_Summary_Data',
                        type: 'String',
                        value: submissionData,
                    },
                    {
                        name: "ParamInputSID",
                        type: "String",
                        value: sid,//a262F000003s7k7QAA
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
                        name: "emailaddress",
                        type: "String",
                        value: emailaddress,
                    },
                    {
                        name: "Billing_1",
                        type: "String",
                        value: billAddress,
                    },
                    {
                        name: "Permanent",
                        type: "String",
                        value: perAddress,
                    },
                    {
                        name: "Shipping",
                        type: "String",
                        value: shipAddress,
                    },
                    {
                        name: "Alternate",
                        type: "String",
                        value: '',
                    },
                    {
                        name: 'Invoice_Summary_Data',
                        type: 'String',
                        value: submissionData,
                    },
                    {
                        name: "TN",
                        type: "String",
                        value: TN,
                    },
                ];
            }
        }
}