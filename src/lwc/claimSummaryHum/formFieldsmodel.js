const defaultModel = {
    header: { name: "Claim Summary", icon: { size: "medium", name: "standard:contact" } },
    recordDetail: [],
    actions: []
}

const policyRcc = {
    header: { name: "Claim Summary", icon: { size: "medium", name: "standard:contact" } },
    recordDetail: [
        { label: "Member ID", mapping: "Name" },
        { label: "Policy", mapping: "Plan.Name" },
        { label: "Product - Product Type", mapping: "Product__c-Product_Type__c", seperator:"-"},
        { label: "Effective - End Date", mapping: "EffectiveFrom-EffectiveTo", bDate: true, seperator:"-"}
    ],
    actions: []
}

const policyPharmacy = {
    header: { name: "Claim Summary", icon: { size: "medium", name: "standard:contact" } },
    recordDetail: [
        { label: "Member ID", mapping: "Name" },
        { label: "Policy", mapping: "Plan.Name" },
        { label: "Product - Product Type", mapping: "Product__c-Product_Type__c", seperator:"-"},
        { label: "Effective - End Date", mapping: "EffectiveFrom-EffectiveTo", bDate: true, seperator:"-"}
    ],
    actions: []
}

const policyProvider = {
    header: { name: "Claim Summary", icon: { size: "medium", name: "standard:contact" } },
    recordDetail: [
        { label: "Member ID", mapping: "Name" },
        { label: "Policy", mapping: "Plan.Name" },
        { label: "Product - Product Type", mapping: "Product__c-Product_Type__c", seperator:"-"},
        { label: "Effective - End Date", mapping: "EffectiveFrom-EffectiveTo", bDate: true, seperator:"-"}
    ],
    actions: []
}

const policyGbo = {
    header: { name: "Claim Summary", icon: { size: "medium", name: "standard:contact" } },
    recordDetail: [
        { label: "Member ID", mapping: "Name" },
        { label: "Policy", mapping: "Plan.Name" },
        { label: "Product - Product Type", mapping: "Product__c-Product_Type__c", seperator:"-"},
        { label: "Effective - End Date", mapping: "EffectiveFrom-EffectiveTo", bDate: true, seperator:"-"}
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