const kidsDelegationLayout = [
    {
        label: 'Member', fieldName: 'name', sortable: "true",
        hideDefaultActions: true,
    },
    {
        label: 'Date of Birth', fieldName: 'dateOfBirth', sortable: "true",
        hideDefaultActions: true,
    },
    {
        label: 'Age', fieldName: 'age', sortable: "true",
        hideDefaultActions: true,
    },
    {
        label: 'Start Date', fieldName: 'startDate', sortable: "true",
        hideDefaultActions: true,
    }];

export function getModel() {
    return kidsDelegationLayout;
}