import { LightningElement, track, api } from 'lwc';

export default class MemIdCardsFilterHum extends LightningElement {
    @track filterJson = {
        memberId: '',
        firstName: '',
        lastName: '',
        status: ''
    };

    @api options = [];

    get showButtons() {
        return this.filterJson.memberId || this.filterJson.firstName || this.filterJson.lastName || this.filterJson.status;
    }

    handleCloseFilter(event) {
        // Creates the event with data.
        const selectedEvent = new CustomEvent('closefilter');

        // Dispatches the event.
        this.dispatchEvent(selectedEvent);
    }

    handleMemberIdChange(event) {
        this.filterJson.memberId = event.target.value;
    }

    handleFirstNameChange(event) {
        this.filterJson.firstName = event.target.value;
    }

    handleLastNameChange(event) {
        this.filterJson.lastName = event.target.value;
    }

    handleStatusChange(event) {
        this.filterJson.status = event.detail.value;
        this.fireEvent(this.filterJson);
    }

    fireEvent(data) {
        // Creates the event with data.
        const selectedEvent = new CustomEvent('filterapplied', { detail: data });

        // Dispatches the event.
        this.dispatchEvent(selectedEvent);
    }
    handleApply(event) {
        this.fireEvent(this.filterJson);

    }
    handleClearStatus(event) {
        this.filterJson.status = '';
        this.fireEvent(this.filterJson);


    }
    handleCancel(event) {
        this.filterJson.firstName = '';
        this.filterJson.lastName = '';
        this.filterJson.memberId = '';
        this.fireEvent(this.filterJson);

    }
}