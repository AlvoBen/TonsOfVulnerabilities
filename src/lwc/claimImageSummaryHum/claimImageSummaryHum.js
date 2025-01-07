import { LightningElement,track,wire,api } from 'lwc';
import { CurrentPageReference, NavigationMixin } from 'lightning/navigation';

export default class ClaimImageSummaryHum extends LightningElement {
    @track dataRows=[];
    @api resp;
    @api noResult;
    @api isLoading;
	@api issingleImage;
    focus1 = true;
    focus2 = false;
    focus3 = false;
    focus4 = false;
    focus5 = false;
    focus6 = false;
    @track ecnDcnIconHandler = 'utility:arrowup';
    @track ReceiptIconHandler = 'utility:arrowup';
    @track ServicingProvIconHandler = 'utility:arrowup';
    @track BillingProvIconHandler = 'utility:arrowup';
    @track RepricedIconHandler = 'utility:arrowup';
    @track FrequencyIconHandler = 'utility:arrowup';
    @track direction = 'asc';
    
    infoMsg = 'Multiple images are available for this claim';
    @wire(CurrentPageReference)
    wiredPageRef(currentPageReference) {
        this.pageRef = currentPageReference;
        console.log('this.pageRef==>',this.pageRef);
    }    
    connectedCallback(){
    }
   
sortfocus(fieldname) {
    if (fieldname == 'sECN') {
        this.focus1 = true;
        this.focus2 = false;
        this.focus3 = false;
        this.focus4 = false;
        this.focus5 = false;
        this.focus6 = false;
    } else if (fieldname == 'sReceipt') {
        this.focus1 = false;
        this.focus2 = true;
        this.focus3 = false;
        this.focus4 = false;
        this.focus5 = false;
        this.focus6 = false;
    } else if (fieldname == 'sProvId') {
        this.focus1 = false;
        this.focus2 = false;
        this.focus3 = true;
        this.focus4 = false;
        this.focus5 = false;
        this.focus6 = false;
    } else if (fieldname == 'sBillingProvId') {
        this.focus1 = false;
        this.focus2 = false;
        this.focus3 = false;
        this.focus4 = true;
        this.focus5 = false;
        this.focus6 = false;
    } else if (fieldname == 'sRepriced') {
        this.focus1 = false;
        this.focus2 = false;
        this.focus3 = false;
        this.focus4 = false;
        this.focus5 = true;
        this.focus6 = false;
    } else if (fieldname == 'sFrequency') {
        this.focus1 = false;
        this.focus2 = false;
        this.focus3 = false;
        this.focus4 = false;
        this.focus5 = false;
        this.focus6 = true;
    }
}
updateColumnSorting(event) {
    var selectedField = event.target.outerText;
    console.log(selectedField);
    var sortby = '';
    if (selectedField == 'ECN/DCN') {
        sortby = 'sECN';
    } else if (selectedField == 'Receipt') {
        sortby = 'sReceipt';
    } else if (selectedField == 'Servicing Prov') {
        sortby = 'sProvId';
    } else if (selectedField == 'Billing Prov') {
        sortby = 'sBillingProvId';
    } else if (selectedField == 'Repriced') {
        sortby = 'sRepriced';
    } else if (selectedField == 'Frequency') {
        sortby = 'sFrequency';
    }
    this.sortData(sortby);
    this.sortfocus(sortby);
}

sortData(fieldname) {
    let parseData = JSON.parse(JSON.stringify(this.resp));
    let keyValue = (a) => {
        return a[fieldname];
    };
    this.direction = this.direction === 'asc' ? 'desc' : 'asc';
    let isReverse = this.direction === 'asc' ? 1 : -1;
    parseData.sort((x, y) => {
        x = keyValue(x) ? keyValue(x) : '';
        y = keyValue(y) ? keyValue(y) : '';
        return isReverse * ((x > y) - (y > x));
    });
    if (fieldname == 'sECN') {
        this.ecnDcnIconHandler =
            this.direction === 'asc'
                ? 'utility:arrowup'
                : 'utility:arrowdown';
    }
    if (fieldname == 'sReceipt') {
        this.ReceiptIconHandler =
            this.direction === 'asc'
                ? 'utility:arrowup'
                : 'utility:arrowdown';
    }
    if (fieldname == 'sProvId') {
        this.ServicingProvIconHandler =
            this.direction === 'asc'
                ? 'utility:arrowup'
                : 'utility:arrowdown';
    }
    if (fieldname == 'sBillingProvId') {
        this.BillingProvIconHandler =
            this.direction === 'asc'
                ? 'utility:arrowup'
                : 'utility:arrowdown';
    }
    if (fieldname == 'sRepriced') {
        this.RepricedIconHandler =
            this.direction === 'asc'
                ? 'utility:arrowup'
                : 'utility:arrowdown';
    }
    if (fieldname == 'sFrequency') {
        this.FrequencyIconHandler =
            this.direction === 'asc'
                ? 'utility:arrowup'
                : 'utility:arrowdown';
    }
    this.resp = [];
    this.resp = parseData;
}

}