/*
LWC Name        : PharmacyOrderQueueDetailsHum.js
Function        : LWC to display pharmacy order queue details.

Modification Log:
* Developer Name                  Date                         Description
* Swapnali Sonawane               12/05/2022                   US- 3969790 Migration of the order queue detail capability
****************************************************************************************************************************/
import { LightningElement, track, api} from 'lwc';

export default class PharmacyOrderQueueDetails extends LightningElement {
    @api queuedata;
    @track isQueueAvailable = false;
    @track sortedDirection;
    @track sortedBy;
    @track columns = [
        {
            label: 'Order Queue Detail',
            fieldName: 'QueueName',
            sortable: true,
            hideDefaultActions: true,
            cellAttributes: {
                class: { fieldName: 'cellBorder' }
            }
        }, {
            label: 'Date Time Completed',
            fieldName: 'QueueDate',
            sortable: true,
            hideDefaultActions: true,
            cellAttributes: {
                class: { fieldName: 'cellBorder' }
            }
        }, {
            label: 'User Name',
            fieldName: 'QueueUser',
            sortable: true,
            hideDefaultActions: true,
            cellAttributes: {
                class: { fieldName: 'cellBorder' }
            }
        },
    ];
    connectedCallback(){
        this.isQueueAvailable = this.queuedata && Array.isArray(this.queuedata)
        && this.queuedata.length > 0 ? true : false;
    }

    onHandleSort(event) {
        let fieldName = event.detail.fieldName;
        let sortDirection = event.detail.sortDirection;
        const cloneData = [...this.queuedata];
        this.queuedata = [];
    
        let reverse = sortDirection === 'asc' ? 1 : -1;
        switch (fieldName) {
            case "QueueDate":
    
                let key = (a) => {
                    return a[fieldName];
                };
                cloneData.sort(function (a, b) {
                    if(a?.QueueDate === null  || a?.QueueDate === ''){
                        return sortDirection === 'asc' ? -1 : 1;
                    }
                    if(b?.QueueDate === null  || b?.QueueDate === ''){
                        return sortDirection === 'asc' ? 1 : -1;
                    }
                    a = new Date(key(a));
                    b = new Date(key(b));
                    return reverse * ((a > b) - (b > a));
                });
    
                break;
            case "QueueName":
            case "QueueUser":
    
                let keyValue = (a) => {
                    return a[fieldName];
                };
    
                cloneData.sort((x, y) => {
                    x = keyValue(x) ? keyValue(x) : '';
                    y = keyValue(y) ? keyValue(y) : '';
    
                    return reverse * ((x > y) - (y > x));
                });
    
                break;
        }
        this.queuedata = cloneData;
        this.sortedDirection = sortDirection;
        this.sortedBy = fieldName;
    }
}