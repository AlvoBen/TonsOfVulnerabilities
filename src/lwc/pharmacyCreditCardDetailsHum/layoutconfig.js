export const creditCardDetailsLayout = [{
    label: 'Credit Card Type',
    sorting: false,
    desc: true,
    asc: false,
    iconname: 'utility:arrowdown',
    mousehover: false,
    mousehovericon: '',
    id: 'CreditCardTypeLiteral',
    style: 'width:15%;cursor: pointer;',
    mappingField: 'CardType',
    allowsortig: true
}, {
    label: 'Last 4 Digits',
    sorting: false,
    desc: true,
    asc: false,
    iconname: 'utility:arrowdown',
    mousehover: false,
    mousehover: false,
    mousehovericon: '',
    id: 'CreditCardLast4Digits',
    style: 'width:10%;cursor: pointer;',
    mappingField: 'Last4Digits',
    allowsortig: true
}, {
    label: 'Card Holder Name',
    sorting: false,
    desc: true,
    asc: false,
    iconname: 'utility:arrowdown',
    mousehover: false,
    mousehovericon: '',
    id: 'CardHolderName',
    style: 'width:18%;cursor: pointer;',
    mappingField: 'CardHolderName',
    allowsortig: true
}, {
    label: 'Expiration Date',
    sorting: false,
    desc: true,
    asc: false,
    iconname: 'utility:arrowdown',
    mousehover: false,
    mousehovericon: '',
    id: 'ExpirationDate',
    style: 'width:15%;cursor: pointer;',
    mappingField: 'ExpirationDate',
    allowsortig: true
}, {
    label: 'Auto Charge',
    sorting: false,
    desc: true,
    asc: false,
    iconname: 'utility:arrowdown',
    mousehover: false,
    mousehovericon: '',
    id: 'AutoCharge',
    style: 'width:12%;cursor: pointer;',
    mappingField: 'AutoCharge',
    allowsortig: true
}, {
    label: 'Status',
    sorting: false,
    desc: true,
    asc: false,
    iconname: 'utility:arrowdown',
    mousehover: false,
    mousehovericon: '',
    id: 'Status',
    style: 'width:10%;cursor: pointer;',
    mappingField: 'Status',
    allowsortig: true
}, {
    label: 'FSA/HSA',
    sorting: false,
    desc: true,
    asc: false,
    iconname: 'utility:arrowdown',
    mousehover: false,
    mousehovericon: '',
    id: 'SpendingAccount',
    style: 'width:10%;cursor: pointer;',
    mappingField: 'SpendingAccount',
    allowsortig: true
}, {
    label: '',
    sorting: false,
    desc: true,
    asc: false,
    iconname: 'utility:arrowdown',
    mousehover: false,
    mousehovericon: '',
    id: '',
    style: 'width:5%;',
    mappingField: '',
    allowsortig: false
}, {
    label: '',
    sorting: false,
    desc: true,
    asc: false,
    iconname: 'utility:arrowdown',
    mousehover: false,
    mousehovericon: '',
    id: '',
    style: 'width:5%;',
    mappingField: '',
    allowsortig: false
}];

export function getLayout() {
    return creditCardDetailsLayout;
}