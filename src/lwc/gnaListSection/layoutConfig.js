export const grievanceAppealsListLayout = [
    {
        label: "",
        isExpand: true,
        ExpandIcon: "",
        CollapseIcon: "",
        style: "width:5%;"
    },
    {
        label: "Case Number/Type",
        value: "",
        mappingField: "sCase",
        style: "width:20%;"
    },
    {
        label: "Humana Received Date",
        value: "",
        mappingField: "sManualReceivedDate",
        style: "width:15%;"
    },
    {
        label: "Provider Name",
        value: "",
        mappingField: "sProviderDisplayName",
        style: "width:15%;"
    },
    {
        label: "Category",
        value: "",
        mappingField: "sCategory",
        style: "width:15%;"
    },
    {
        label: "Status",
        value: "",
        mappingField: "sStatus",
        style: "width:15%;"
    },
    {
        label: "Priority",
        value: "",
        mappingField: "sPriority",
        style: "width:10%;"
    },
    {
        label: "Product",
        value: "",
        mappingField: "sMemberElgGrp",
        style: "width:10%;"
    }
];

export function getModal() {
    return grievanceAppealsListLayout;
}