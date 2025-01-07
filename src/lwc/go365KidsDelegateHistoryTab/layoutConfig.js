export const detailsTabLayout = [
    {
        label: 'Effective Date',
        fieldName: 'effectiveDate',
        sortable: true,
        hideDefaultActions: true,
        cellAttributes: {
            class: { fieldName: 'cellBorder' }
        }
    }, {
        label: 'Delegate For',
        fieldName: 'delegate',
        sortable: true,
        hideDefaultActions: true,
        cellAttributes: {
            class: { fieldName: 'cellBorder' }
        }
    }, {
        label: 'Action',
        fieldName: 'action',
        sortable: true,
        hideDefaultActions: true,
        cellAttributes: {
            class: { fieldName: 'cellBorder' }
        }
    },
];

export function getModel() {
    return detailsTabLayout;
}

export function getMedicareSet() {
    return ['MCD', 'MEF', 'MEP', 'MER', 'MES', 'MGP', 'MGR', 'MPD', 'MRO', 'MRP', 'MSP', 'PDP', 'MAPD'];
}