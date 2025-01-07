import { LightningElement, track } from 'lwc';
import { getLabels } from "c/crmUtilityHum";

export default class AccountDetailNBAAlertsHum extends LightningElement {
    @track labels = getLabels();
}