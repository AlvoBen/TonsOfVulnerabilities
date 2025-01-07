export const BenefitsAccumulatorColumns = [
    {
        label: 'Accumulator',
        style: 'width:16%;'
    },
    {
        label: 'From', showSortIcon: true,
        style: 'width:16%;'
    },
    {
        label: 'To',
        style: 'width:16%;'
    },
    {
        label: 'Limit',
        style: 'width:16%;'
    },
    {
        label: 'Used',
        style: 'width:16%;'
    },
    {
        label: 'Available',
        style: 'width:16%;'
    }
];

export function getModel() {
    return BenefitsAccumulatorColumns;
}