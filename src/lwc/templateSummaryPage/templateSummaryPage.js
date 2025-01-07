import { LightningElement, api } from 'lwc';
import { SummaryModel } from  './templateSummaryModel';
import { showToastEvent } from 'c/crmserviceHelper';

export default class TemplateSummaryPage extends LightningElement {
    @api inputSummaryData;
    @api summarySections = [];
    @api title = '';
    summaryModelClass = new SummaryModel();
    summaryModel = this.summaryModelClass.generateModel.apply(this);

    connectedCallback() {
        console.log('Template Summary Page');
        this.prepareUIDataModel.apply(this);
    }   

    prepareUIDataModel() {
        try {
            let inputSummaryJSON = JSON.parse(this.inputSummaryData);
            for(let key in inputSummaryJSON) {
                if(inputSummaryJSON.hasOwnProperty(key)) {
                    if(key === 'title') {
                        this.title = inputSummaryJSON[key];
                    }
                    else if(key === 'sections') {
                        let order = 0;
                        for (const section of inputSummaryJSON[key]) {
                            let fields = [];
                            for (const field of section.fields) {
                                if(typeof field.value !== 'undefined') {
                                    fields.push(field);
                                }
                            }
                            if(fields.length > 0) {
                                this.summarySections.push({
                                    order: order,
                                    title: (section.title === 'No Title') ? '' : section.title,
                                    fields: fields
                                });
                            }
                            order++;
                        }
                    }
                }
            }
        }
        catch(error) {
            showToastEvent.apply(this, ['JavaScript Error', `${ error.message }`]);
            console.log(error.message);
        }
    }
}