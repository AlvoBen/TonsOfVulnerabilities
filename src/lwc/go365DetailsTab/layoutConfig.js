export const detailsTabLayout = [
    {
        label: 'Activity',
        fieldName: 'activity',
        sortable: true,
        hideDefaultActions: true
    }, {
        label: 'Activity Status',
        fieldName: 'activityStatus',
        sortable: true,
        hideDefaultActions: true,
    }, {
        label: "Point's",
        fieldName: 'points',
        sortable: true,
        hideDefaultActions: true,
    }, {
        label: 'Days Until Expiration',
        fieldName: 'days',
        sortable: true,
        hideDefaultActions: true,
    }, {
        label: 'EndDate',
        fieldName: 'endDate',
        sortable: true,
        hideDefaultActions: true,
    },
];

export function getModel() {
    return detailsTabLayout;
}

export function getMedicareSet() {
    return ['MCD', 'MEF', 'MEP', 'MER', 'MES', 'MGP', 'MGR', 'MPD', 'MRO', 'MRP', 'MSP', 'PDP', 'MAPD'];
}