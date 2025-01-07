const defaultModel = {
    header: { name: "Plan Member", icon: { size: "large", name: "custom:custom60" } },
    recordDetail: [],
    actions: []
}

const policyRcc = {
    header: { name: "Plan Member", icon: { size: "large", name: "custom:custom60" } },
    recordDetail: [
        { label: "Member ID", mapping: "Name", copyToClipBoard: true },
        { label: "Plan Name", mapping: "Plan.iab_description__c" },
        { label: "Group Name", mapping: "Display_Group_Name__c", copyToClipBoard: true },
        { label: "Product Type - Product Type Code", mapping: "Product_Type__c-Product_Type_Code__c", seperator:"-"},
        { label: "Status", mapping: "Member_Coverage_Status__c", showIcon: true },
        { label: "Effective - End Date", mapping: "EffectiveFrom-EffectiveTo", bDate: true, seperator:"-"}
    ],
    actions: []
}

const policyPharmacy = {
    header: { name: "Plan Member", icon: { size: "large", name: "custom:custom60" } },
    recordDetail: [
        { label: "Status", mapping: "Member_Coverage_Status__c",showIcon: true },
        { label: "Member ID", mapping: "Name", copyToClipBoard: true },
        { label: "Plan", mapping: "Plan.Name"},
        { label: "Product Type - Product Type Code", mapping: "Product_Type__c-Product_Type_Code__c" ,seperator:"-"},
        { label: "Effective - End Date", mapping: "EffectiveFrom-EffectiveTo", bDate: true, seperator:"-" }
    ],
    actions: []
}

const policyProvider = {
    header: { name: "Plan Member", icon: { size: "large", name: "custom:custom60" } },
    recordDetail: [
        { label: "Member ID", mapping: "Name", copyToClipBoard: true },
        { label: "Group Name", mapping: "Display_Group_Name__c", copyToClipBoard: true },
        { label: "ASO Indicator", mapping: "ASO__c" },
        { label: "Product - Product Type", mapping: "Product__c-Product_Type__c", seperator:"-"},
        { label: "Status", mapping: "Member_Coverage_Status__c", showIcon: true },
        { label: "Effective Date - End Date", mapping: "EffectiveFrom-EffectiveTo", bDate: true, seperator:"-" },
        { label: "Platform", mapping: "Policy_Platform__c" }
    ],
    actions: []
}

const policyGbo = {
    header: { name: "Plan Member", icon: { size: "large", name: "custom:custom60" } },
    recordDetail: [
        { label: "Product Description", mapping: "Product_Description__c"},
        { label: "Effective - End Date", mapping: "EffectiveFrom-EffectiveTo", bDate: true, seperator:"-" },
        //{ label: "Member ID - Suffix", mapping: "Name-Member_Dependent_Code__c", seperator:"-" },
        { label: "Member ID", mapping: "Name", copyToClipBoard: true },
        { label: "Group Number", mapping: "GroupNumber", copyToClipBoard: true },
        { label: "Group Name", mapping: "Display_Group_Name__c", copyToClipBoard: true },
        { label: "ASO Indicator", mapping: "ASO__c" },
        { label: "Issue State", mapping: "Issue_State__c" }
    ],
    actions: []
}

export const getModal = (accountType, oUserGroup,profileName) => {
    let oModel;
    if(oUserGroup.bRcc || (oUserGroup.bGeneral && (profileName==='Customer Care Specialist' || profileName==='Customer Care Supervisor'))){
        oModel = policyRcc;
    }
    else if(oUserGroup.bProvider){
        oModel = policyProvider;
    }
    else if(oUserGroup.bGbo){
        oModel = policyGbo
    }
    else if(oUserGroup.bPharmacy || (oUserGroup.bGeneral && (profileName==='Humana Pharmacy Specialist'))){
        oModel = policyPharmacy;
    }
    else {
        oModel = defaultModel;
    }
    return oModel;
}