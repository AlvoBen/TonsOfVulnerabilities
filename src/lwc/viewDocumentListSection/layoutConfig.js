export const documentLayout = [{
    label : 'File Name',
    sorting : false,
    desc : false,
    asc : true,
    iconname : 'utility:arrowup',
    mousehover : false,
    mousehovericon : '',
    id : 'sFileName',
    islink:true,
    style: 'width:25%;cursor: pointer;',
    divstyle:'width: 100%;height: 25px;'
},{
    label : 'Type',
    sorting : false,
    desc : false,
    asc : true,
    iconname : 'utility:arrowup',
    iconname : '',
    mousehover : false,
    mousehovericon : '',
    id:'sType',
    style: 'width:25%;cursor: pointer;',
    divstyle:'width: 100%;height: 25px;'
},{
    label : 'Created By',
    sorting : false,
    desc : false,
    asc : true,
    iconname : 'utility:arrowup',
    iconname : '',
    mousehover : false,
    mousehovericon : '',
    id:'sCreatedBy',
    style: 'width:25%;cursor: pointer;',
    divstyle:'width: 100%;height: 25px;'
},{
    label : 'Document Created',
    sorting :true,
    desc : true,
    asc : false,
    iconname : 'utility:arrowdown',
    mousehover : false,
    mousehovericon : '',
    id:'sDocumentCreated',
    style: 'width:25%;cursor: pointer;',
    divstyle:'width: 100%;height: 25px;'
}]

export function getlayout(params) {
    if(params === 'document'){
        return documentLayout;
    }
}