import { LightningElement,api } from 'lwc';

export default class CRMRetail_Gen_FlowFinishButton_LWC_HUM extends LightningElement {
@api listViewIds;
handleClick(event)
    {
    window.history.back();
    }
}