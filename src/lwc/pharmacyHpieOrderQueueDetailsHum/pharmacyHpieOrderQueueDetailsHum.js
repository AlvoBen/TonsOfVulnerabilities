/*
Function        : LWC to display demographics details .

Modification Log:
* Developer Name                  Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Nirmal Garg                    08/25/2023                    Initial version
*****************************************************************************************************************************/
import { LightningElement, track, api } from 'lwc';
import { getEvents } from 'c/pharmacyHPIEIntegrationHum';
import { getUniqueId } from 'c/crmUtilityHum';
export default class PharmacyHpieOrderQueueDetailsHum extends LightningElement {
    @api orderId;
    @api enterpriseId;
    @api userId;
    @api organization;
    @track sortedDirection;
    @track sortedBy;
    @track isQueueAvailable = false;
    @track loaded = false;
    @track queuedata = [];
    @track columns = [
        {
            label: 'Order Queue Detail',
            fieldName: 'name',
            sortable: true,
            hideDefaultActions: true,
            cellAttributes: {
                class: { fieldName: 'cellBorder' }
            }
        }, {
            label: 'Date Time Completed',
            fieldName: 'date',
            sortable: true,
            hideDefaultActions: true,
            cellAttributes: {
                class: { fieldName: 'cellBorder' }
            }
        }, {
            label: 'User Name',
            fieldName: 'user',
            sortable: true,
            hideDefaultActions: true,
            cellAttributes: {
                class: { fieldName: 'cellBorder' }
            }
        },
    ];
    connectedCallback() {
        this.getOrderEvents();
    }

    getOrderEvents() {
        getEvents(this.enterpriseId, this.userId, this.organization ?? 'HUMANA', this.orderId)
            .then(result => {
                this.processResult(result);
            }).catch(error => {
                console.log(error);
                this.loaded = true;
            })
    }

    processResult(events) {
        if (events && Array.isArray(events?.events) && events?.events?.length > 0) {
            events?.events?.forEach(k => {
                if (k?.events && Array.isArray(k?.events) && k?.events?.length > 0) {
                    k?.events?.forEach(h => {
                        this.queuedata.push({
                            id: h?.id ?? getUniqueId(),
                            name: h?.name ?? '',
                            date: h?.z0date ?? '',
                            user: h?.z0user ?? ''
                        })
                    })
                }
            })
        }
        this.isQueueAvailable = this.queuedata?.length > 0 ? true : false;
        this.queuedata = this.queuedata?.length > 0 ? this.queuedata.reverse() : this.queuedata;
        let currentQueue = this.getCurrentQueue(this.orderTasks);
        if (currentQueue && currentQueue?.length <= 0) {
            this.queuedata.push({
                id: getUniqueId(),
                name: 'OPEN',
                date: '',
                user: ''
            })
        }
        this.loaded = true;
    }

    getCurrentQueue(tasks) {
        let currentQueue = '';
        if (tasks && Object.keys(tasks)?.length > 0) {
            currentQueue = tasks?.task?.name ? tasks?.task?.name :
                tasks?.lines && Array.isArray(tasks?.lines) && tasks?.lines?.length > 0
                    ? tasks.lines[0]?.task?.name : '';
        }
        return currentQueue;
    }

    onHandleSort(event) {
        let fieldName = event.detail.fieldName;
        let sortDirection = event.detail.sortDirection;
        const cloneData = [...this.queuedata];
        this.queuedata = [];

        let reverse = sortDirection === 'asc' ? 1 : -1;
        switch (fieldName) {
            case "date":
                let key = (a) => {
                    return a[fieldName];
                };
                cloneData.sort(function (a, b) {
                    if (a?.QueueDate === null || a?.QueueDate === '') {
                        return sortDirection === 'asc' ? -1 : 1;
                    }
                    if (b?.QueueDate === null || b?.QueueDate === '') {
                        return sortDirection === 'asc' ? 1 : -1;
                    }
                    a = new Date(key(a));
                    b = new Date(key(b));
                    return reverse * ((a > b) - (b > a));
                });

                break;
            case "name":
            case "user":
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