export const memberPaymentLayout = [{
    label: 'Payment Scheduled Date',
    fieldName: 'dueDate',
    type: 'text',
    cellAttributes: { alignment: 'left' },
    sortable: true,
    fixedWidth: '22%',
    hideDefaultActions: true,

}, {
    label: 'Payment Processed Date',
    fieldName: 'processedDate',
    stype: 'text',
    cellAttributes: { alignment: 'left' },
    sortable: true,
    fixedWidth: '20%',
    hideDefaultActions: true,
}, {
    label: 'Credit Card Type',
    fieldName: 'creditCardType',
    stype: 'text',
    cellAttributes: { alignment: 'left' },
    sortable: true,
    fixedWidth: '15%',
    hideDefaultActions: true,
}, {
    label: 'Last 4 Digits',
    fieldName: 'last4Digits',
    stype: 'text',
    cellAttributes: { alignment: 'left' },
    sortable: true,
    fixedWidth: '12%',
    hideDefaultActions: true,
},
{
    label: 'Amount',
    fieldName: 'amount',
    type: 'currency',
    typeAttributes: { currencyCode: 'USD' },
    cellAttributes: { alignment: 'left' },
    sortable: true,
    fixedWidth: '10%',
    hideDefaultActions: true,
}, 
{
    label: 'Payment Source',
    fieldName: 'source',
    stype: 'text',
    cellAttributes: { alignment: 'left' },
    sortable: true,
    fixedWidth: '10%',
    hideDefaultActions: true,
}]

export function getModel() {
    return memberPaymentLayout;
}