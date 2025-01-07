/*
LWC Name        : layoutConfig.js
Function        : helper file for credit card functionalities.
Modification Log:
* Developer Name                  Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Jonathan Dickinson			  08/17/2023					User Story 4908778: T1PRJ0870026 - SF - Tech - C12 Mail Order Management - Pharmacy - Finance tab
****************************************************************************************************************************/

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
    mappingField: 'cardType',
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
    mappingField: 'last4Digits',
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
    mappingField: 'cardHolderName',
    allowsortig: true
}, {
    label: 'Expiration Date',
    sorting: true,
    desc: true,
    asc: false,
    iconname: 'utility:arrowdown',
    mousehover: false,
    mousehovericon: '',
    id: 'ExpirationDate',
    style: 'width:15%;cursor: pointer;',
    mappingField: 'expirationDate',
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
    mappingField: 'autoCharge',
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
    mappingField: 'status',
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
    mappingField: 'spendingAccount',
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