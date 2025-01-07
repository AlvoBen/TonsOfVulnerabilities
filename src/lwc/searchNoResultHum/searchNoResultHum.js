import { LightningElement, api } from 'lwc';

export default class SearchNoResultHum extends LightningElement {
    @api noResultMessage;
    @api messageWithIcon;
}