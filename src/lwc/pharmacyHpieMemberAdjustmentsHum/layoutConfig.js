export const MemberAdjustmentLayout = [{
    label: 'Date',
    fieldName: 'date',
    type: 'text',
    cellAttributes: { alignment: 'left' },
    sortable: true,
    fixedWidth: '20%',
    hideDefaultActions: true,
},
{
    label: 'Amount',
    fieldName: 'amount',
    type: 'currency',
    typeAttributes: { currencyCode: 'USD' },
    cellAttributes: {
        alignment: 'left',
        class: {
            fieldName: 'amountColor'
        },
    },
    sortable: true,
    fixedWidth: '20%',
    hideDefaultActions: true,
},
{
    label: 'Type',
    fieldName: 'type',
    type: 'text',
    cellAttributes: { alignment: 'left' },
    sortable: true,
    fixedWidth: '60%',
    hideDefaultActions: true,
}];

export function getModel() {
    return MemberAdjustmentLayout;
}