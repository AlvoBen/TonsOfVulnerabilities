import { LightningElement, api } from 'lwc';

export default class MemberSearchContainerHum extends LightningElement {

    @api resultsEnabled = false;
    @api resultList = '';

    handleResultsSidebar(event) {
        if (event.detail) {
            this.resultList = event.detail
            this.resultsEnabled = true;
        }
    }
}