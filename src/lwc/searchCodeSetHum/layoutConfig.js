const searchCodeSetLayout = [
    {
        label: "Code",
        value: '',
        mappingField: 'sCode',
        style: 'width:15%;cursor: pointer;',
        sorting: true,
        desc: false,
        asc: true,
        iconname: 'utility:arrowup',
        mousehover: false,
        mousehovericon: '',
        id: 'sCode',
    }, {
        label: "Codeset",
        value: '',
        mappingField: 'sCodeset',
        style: 'width:15%;cursor: pointer;',
        sorting: false,
        desc: false,
        asc: true,
        iconname: 'utility:arrowup',
        mousehover: false,
        mousehovericon: '',
        id: 'sCodeset',
    }, {
        label: "Description",
        value: '',
        mappingField: 'sDescription',
        style: 'width:55%;cursor: pointer;',
        sorting: false,
        desc: false,
        asc: true,
        iconname: 'utility:arrowup',
        mousehover: false,
        mousehovericon: '',
        id: 'sDescription',
    }, {
        label: "Status",
        value: '',
        mappingField: 'sStatus',
        style: 'width:15%;cursor: pointer;',
        sorting: false,
        desc: false,
        asc: true,
        iconname: 'utility:arrowup',
        mousehover: false,
        mousehovericon: '',
        id: 'sStatus',
    }
];

export function getModel() {
    return searchCodeSetLayout;
}