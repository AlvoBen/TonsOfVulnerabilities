export const alertLayout = [
    {
        label: "+/-",
        isExpand: true,
        ExpandIcon: '',
        CollapseIcon: '',
        style: 'width:5%;'
    }, {
        label: "Alert Name",
        value: '',
        mappingField: 'sAlertName',
        style: 'width:13%;cursor: pointer;',
        sorting: true,
        desc: true,
        asc: false,
        iconname: 'utility:arrowdown',
        mousehover: false,
        mousehovericon: '',
    }, {
        label: "Action",
        value: '',
        mappingField: 'sActionTaken',
        style: 'width:20%;cursor: pointer;',
        sorting: false,
        desc: true,
        asc: false,
        iconname: 'utility:arrowdown',
        mousehover: false,
        mousehovericon: '',
    }, {
        label: "Not Delivered /Term Reason",
        value: '',
        mappingField: 'sTermNotDeliveredReason',
        style: 'width:15%;cursor: pointer;',
        sorting: false,
        desc: true,
        asc: false,
        iconname: 'utility:arrowdown',
        mousehover: false,
        mousehovericon: '',
    }, {
        label: "Action taken Date/Time",
        value: '',
        mappingField: 'sActionDateTime',
        style: 'width:15%;cursor: pointer;',
        sorting: false,
        desc: true,
        asc: false,
        iconname: 'utility:arrowdown',
        mousehover: false,
        mousehovericon: '',
    }, {
        label: "Associate ID",
        value: '',
        mappingField: 'sAssociateID',
        style: 'width:10%;cursor: pointer;',
        sorting: false,
        desc: true,
        asc: false,
        iconname: 'utility:arrowdown',
        mousehover: false,
        mousehovericon: '',
    }
]

export function getModal() {
    return alertLayout;
}