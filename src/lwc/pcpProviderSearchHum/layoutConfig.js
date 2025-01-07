export function getModel(modelname) {
  if (modelname) {
    switch (modelname) {
      case "providersearchtable":
        return providerSearchLayout;
      case "miles":
        return miles;
    }
  }
}

export const providerSearchLayout = [
  {
    label: '',
    isIcon: true,
    style: 'width:1%;'
  },
  {
    label: 'Physician Name',
    fieldName: 'physicianName',
    type: 'text',
    sortable: true,
    wrapText: true,
    hideDefaultActions: true,
    iconname: 'utility:arrowdown',
    mousehover: false,
    mousehovericon: '',
    id: 'FieldName',
    style: 'width:14%;cursor: pointer;',
    mappingField: 'physicianName',
    bold: true
  }, {
    label: 'PCP#',
    fieldName: 'pcpNumber',
    type: 'text',
    wrapText: true,
    hideDefaultActions: true,
    iconname: 'utility:arrowdown',
    mousehover: false,
    mousehover: false,
    mousehovericon: '',
    id: 'Value',
    style: 'width:14%;',
    mappingField: 'pcpNumber'
  }, {
    label: 'Address',
    fieldName: 'physicianAddress',
    type: 'text',
    wrapText: true,
    hideDefaultActions: true,
    iconname: 'utility:arrowdown',
    mousehover: false,
    mousehovericon: '',
    id: 'RelatedRecord',
    style: 'width:14%;',
    mappingField: 'physicianAddress'
  }, {
    label: 'Phone',
    fieldName: 'phone',
    type: 'text',
    wrapText: true,
    hideDefaultActions: true,
    iconname: 'utility:arrowdown',
    mousehover: false,
    mousehovericon: '',
    id: 'RelatedRecord',
    style: 'width:14%;',
    mappingField: 'phone'
  }, {
    label: 'Distance',
    fieldName: 'physicianDistance',
    type: 'text',
    sortable: true,
    hideDefaultActions: true,
    style: 'width:12%;cursor: pointer;',
    mappingField: 'physicianDistance',
    bold: true
  }, {
    label: 'Specialities',
    fieldName: 'specialty',
    sortable: true,
    type: 'text',
    wrapText: true,
    hideDefaultActions: true,
    iconname: 'utility:arrowdown',
    mousehover: false,
    mousehovericon: '',
    id: 'RelatedRecord',
    style: 'width:14%;cursor: pointer;',
    mappingField: 'specialty'
  }, {
    label: 'Provider Status',
    fieldName: 'providerStatus',
    type: 'text',
    sortable: true,
    wrapText: true,
    hideDefaultActions: true,
    iconname: 'utility:arrowdown',
    mousehover: false,
    mousehovericon: '',
    id: 'RelatedRecord',
    style: 'width:16%;cursor: pointer;',
    mappingField: 'physicianAddress'
  }];

export const miles = [{
  label: '3 Miles',
  value: '3'
},
{
  label: '5 Miles',
  value: '5'
},
{
  label: '10 Miles',
  value: '10'
},
{
  label: '15 Miles',
  value: '15'
},
{
  label: '20 Miles',
  value: '20'
},
{
  label: '30 Miles',
  value: '30'
},
{
  label: '40 Miles',
  value: '40'
},
{
  label: '50 Miles',
  value: '50'
},
{
  label: '75 Miles',
  value: '75'
},
{
  label: '100 Miles',
  value: '100'
}
];