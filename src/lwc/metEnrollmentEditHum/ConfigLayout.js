export function getMETEnrollTableLayout(layoutData) {
    return METEnrollTableLayout(layoutData);
}

const METEnrollTableLayout = function(layoutData) {
    let MetColumns = [{ label: 'Task', fieldName: 'TaskName', type: 'text', hideDefaultActions: true, wrapText: true },
        { label: 'Action', fieldName: 'ActionName', type: 'text', hideDefaultActions: true, wrapText: true },
        { label: 'Source', fieldName: 'SourceName', type: 'text', hideDefaultActions: true, wrapText: true },
        {
            label: 'Status',
            fieldName: 'sStatus',
            type: 'text',
            hideDefaultActions: true,
            wrapText: true,
            cellAttributes: {
                class: { fieldName: 'colortext' }
            }
        },
        {
            label: 'Created By',
            fieldName: 'sCreatedBy',
            type: 'url',
            typeAttributes: { label: { fieldName: 'CreatedByName' }, target: '_self' },
            hideDefaultActions: true,
            wrapText: true
        },
        {
            label: 'Last Modified By',
            fieldName: 'sLastModifiedBy',
            type: 'url',
            typeAttributes: { label: { fieldName: 'LastModifiedByName' }, target: '_self' },
            hideDefaultActions: true,
            wrapText: true
        },
        {
            label: 'Cancelled By',
            fieldName: 'sCancelledBy',
            type: 'url',
            typeAttributes: { label: { fieldName: 'CancelledByName' }, target: '_self' },
            hideDefaultActions: true,
            wrapText: true
        },
        layoutData.CancelButton
    ];

    return MetColumns;
}