export const memberPaymentLayout = [{
    label : 'Payment Scheduled Date',
    sorting : true,
    desc : true,
    asc : false,
    iconname : 'utility:arrowdown',
    mousehover : false,
    mousehovericon : '',
    id : 'PaymentScheduledDate',
    style: 'width:22%;cursor: pointer;'
},{
    label : 'Payment Processed Date',
    sorting : false,
    desc : true,
    asc : false,
    iconname : 'utility:arrowdown',
    mousehover : false,
    mousehover : false,
    mousehovericon : '',
    id:'PaymentProcessedDate',
    style: 'width:20%;cursor: pointer;'
},{
    label : 'Credit Card Type',
    sorting : false,
    desc : true,
    asc : false,
    iconname : 'utility:arrowdown',
    mousehover : false,
    mousehovericon : '',
    id:'CreditCardTypeLiteral',
    style: 'width:15%;cursor: pointer;'
},{
    label : 'Last 4 Digits',
    sorting : false,
    desc : true,
    asc : false,
    iconname : 'utility:arrowdown',
    mousehover : false,
    mousehovericon : '',
    id:'Last4Digit',
    style: 'width:12%;cursor: pointer;'
},{
    label : 'Amount',
    sorting : false,
    desc : true,
    asc : false,
    iconname : 'utility:arrowdown',
    mousehover : false,
    mousehovericon : '',
    id:'Amount',
    style: 'width:10%;cursor: pointer;'
},{
    label : 'Payment Source',
    sorting : false,
    desc : true,
    asc : false,
    iconname : 'utility:arrowdown',
    mousehover : false,
    mousehovericon : '',
    id:'SourceApplicationCodeLiteral',
    style: 'width:10%;cursor: pointer;'
}]

export function getlayout(params) {
    if(params === 'memberpayment'){
        return memberPaymentLayout;
    }
}