import { LightningElement } from 'lwc';
import { NavigationMixin } from 'lightning/navigation';
import clickHere from '@salesforce/label/c.CRMRetail_Service_Strategy_ClickHere';
import strategyLabel_1 from '@salesforce/label/c.CRMRetail_Service_Strategy_Line_1';
import strategyLabel_2 from '@salesforce/label/c.CRMRetail_Service_Strategy_Line_2';
import headerLabel from '@salesforce/label/c.CRMRetail_Service_Strategy_Header_Text';
export default class CRMRetail_HomePage_InfoCard_LWC_HUM extends NavigationMixin(LightningElement) {
    label = {
        clickHere,
        strategyLabel_1,
        strategyLabel_2,
        headerLabel
    };
    redirectToFilesTab(event) {
        this[NavigationMixin.GenerateUrl]({
            type: 'standard__objectPage',
            attributes: {
                objectApiName: 'ContentDocument',
                actionName: 'home',
            },
        }).then(url => { window.open(url) });
    }
}