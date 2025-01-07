const defaultModel = {
    header: { name: "Business Account", icon: { size: "large", name: "standard:account" }},
    recordDetail: [],
    actions: []
}

const AccountRcc = {
    header: { name: "Business Account", icon: { size: "large", name: "standard:account" }},
    recordDetail: [
        { label: "Parent Account", mapping: "Name", bIsCustom: false },
        { label: "Account Record Type", mapping: "Recordtype.name", bIsCustom: false },
        { label: "Group Number", mapping: "Group_Number__c", bIsCustom: false },
        { label: "Mailing Address", mapping: "BillingAddress", bIsCustom: true, bAddress: true},
        { label: "SIC Code", mapping: "Sic", bIsCustom: false}
    ],
    actions: []
}

export const getModal = () => {
    let oModel; 
    oModel = AccountRcc;
    return oModel;
}