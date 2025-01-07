export function getHighlightPanelLayout() {
    return HighlightPanelLayout;
}

export const HighlightPanelLayout = [
    { label: 'Classification', mapping: 'sClassificationName',value: '', 'isLink':false},
    { label: 'Intent', mapping: 'sIntentName',value: '', 'isLink':false},
    { label: 'Case Owner', mapping: 'sCase_Owner',value: '', 'isLink':false},
    { label: 'Owner Queue', mapping: 'sOwner_Queue',value: '', 'isLink':false},
    { label: 'Case Age', mapping: 'sCase_Age',value: '', 'isLink':false},
    { label: 'Status', mapping: 'sStatus',value: '', 'isLink':false}
]