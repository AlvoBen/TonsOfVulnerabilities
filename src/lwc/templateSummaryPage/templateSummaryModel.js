export class SummaryModel {

    fieldModel = {
        label: '',
        value: ''
    };

    sectionModel = {
        title: '',
        fields: [this.fieldModel]
    };

    summaryModel = {
        title: '',
        section: [
            {
                title: '',
                fields: [this.sectionModel]
            }
        ]
    };

    generateModel = () => {
        return {
            summary: this.summaryModel
        };
    }
}