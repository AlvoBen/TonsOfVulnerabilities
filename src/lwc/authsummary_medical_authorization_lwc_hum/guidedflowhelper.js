export class authReferalRequestHelper {
    constructor(SID,TN,Name,querymemgenkey, memberPlan, isMEF_MGF, isHMC_MCD_MER_MGO_MGR_MRG_POS)
        {
                this.flowParams = [
                    {
                        name: "SID",
                        type: "String",
                        value: SID,
                    },
                    {
                        name: "TN",
                        type: "String",
                        value: TN,
                    },
                    {
                        name: "Name",
                        type: "String",
                        value: Name,
                    },
                    {
                        name: "querymemgenkey",
                        type: "String",
                        value: querymemgenkey,
                    }, 
                    {
                        name: "Member_Product_Type",
                        type: "String",
                        value: memberPlan,
                    },                   
                    {
                        name: "Is_MEF_MGF",
                        type: "Boolean",
                        value: isMEF_MGF,
                    },
                    {
                        name: "Is_HMC_MCD_MER_MGO_MGR_MRG_POS",
                        type: "Boolean",
                        value: isHMC_MCD_MER_MGO_MGR_MRG_POS,
                    },                    
                ];
            }
}