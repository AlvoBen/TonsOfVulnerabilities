export const memberPaymentLayout = [{
    label : 'Field Name',
    sorting : false,
    desc : true,
    asc : false,
    iconname : 'utility:arrowdown',
    mousehover : false,
    mousehovericon : '',
    id : 'FieldName',
    style: 'width:25%;cursor: pointer;',
    mappingField : 'LabelName'
},{
    label : 'Value',
    sorting : false,
    desc : true,
    asc : false,
    iconname : 'utility:arrowdown',
    mousehover : false,
    mousehover : false,
    mousehovericon : '',
    id:'Value',
    style: 'width:25%;cursor: pointer;',
    mappingField : 'LabelValue'
},{
    label : 'Related Record',
    sorting : false,
    desc : true,
    asc : false,
    iconname : 'utility:arrowdown',
    mousehover : false,
    mousehovericon : '',
    id:'RelatedRecord',
    style: 'width:25%;cursor: pointer;',
    mappingField : 'RelatedField'
},{
    label : 'Related Tab',
    sorting : false,
    desc : true,
    asc : false,
    iconname : 'utility:arrowdown',
    mousehover : false,
    mousehovericon : '',
    id:'RelatedTab',
    style: 'width:25%;cursor: pointer;',
    mappingField : 'Section'
}];

export function getLayout() {
    return memberPaymentLayout;
}