/*
LWC Name        : benefitSearchDentalHum.js
Function        : LWC to display benefit search details.

Modification Log:
* Developer Name                  Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Nirmal Garg                06/01/2023                   initial version - US - 4648135
****************************************************************************************************************************/

import { LightningElement, api, track } from 'lwc';

export default class GenericHorizontalSearchHum extends LightningElement {

    @api filterconfig;
    @api buttonConfig;
    @track displayfilter = false;
    @track showValidationMsg = false;
    @track stateValue = false;
    @track filterConfig = [
        {
            visible: true,
            searchbar: true,
            placeholder: 'Keyword',
            label: 'Filter by keyword',
            keyword: '',
            name: 'sSearch'
        }
    ]

    handleChange(event) {
        this.dispatchEvent(new CustomEvent('selectitem', {
            detail: {
                label: event.target.options.find(k => k.value === event.detail.value).label,
                value: event.detail.value,
                name: event.target.dataset.id
            }, bubbles: true, composed: true
        }))
    }
    handleButtonClick(event) {
        if (event?.target?.name === 'Search') {
            let isValid = true;
            let inputFields = this.template.querySelectorAll('.validate');
            inputFields.forEach(inputField => {
                if (!inputField.checkValidity()) {
                    inputField.reportValidity();
                    isValid = false;
                }
            });
            if (isValid) {
                this.fireButtonClickEvent(event.target.name);
            } else {
                return;
            }
        } else {
            this.fireButtonClickEvent(event.target.name);
        }

    }

    fireButtonClickEvent(buttonname) {
        this.dispatchEvent(new CustomEvent('buttonclick', {
            detail: {
                name: buttonname
            }, bubbles: true, composed: true
        }))
    }

    handleFilterClick() {
        this.displayfilter = true;
    }

    @api
    setData(filterconfig) {
        this.filterconfig = filterconfig;
        this.buttonConfig = this.buttonConfig;
    }

    handleClearFilter({ detail }) {
        if (detail && detail === 'Clear') {
            this.isdisabled = true;
            this.filterConfig = [
                {
                    visible: true,
                    searchbar: true,
                    placeholder: 'Keyword',
                    label: 'Filter by keyword',
                    keyword: '',
                    name: 'sSearch'
                }
            ]
            this.updateFilterIcon("Clear");
            this.dispatchEvent(new CustomEvent('buttonclick', {
                detail: {
                    name: "Clear"
                }, bubbles: true, composed: true
            }))
        } else if (detail && detail === 'Close') {
            this.displayfilter = false;
            this.updateFilterIcon("Close");
            
        }
    }

    handleDropdownChange(event) {
        console.log(JSON.stringify(event));
    }

    handleDropdownClear(event) {
        console.log(JSON.stringify(event));
    }

    highlightFields() {
        console.log('Highlighted');
    }

    removehighlight() {
        console.log('Remove Highlighted');
    }

    updateFilterIcon(calledfrom) {
        let filtericon = this.template.querySelector('.filtericon');
        if (filtericon) {
            if (this.filterConfig.find(k => k.name === 'sSearch')?.keyword?.length >= 3) {
                switch (calledfrom) {
                    case "Close":
                        filtericon.classList.remove('blackIcon');
                        filtericon.classList.add('filledIcon');
                        break;
                    case "Clear":
                        filtericon.classList.remove('filledIcon');
                        filtericon.classList.add('blackIcon');
                        break;
                }
            } else {
                filtericon.classList.remove('filledIcon');
                filtericon.classList.add('blackIcon');
            };
        }
    }

    handleKeyWordValue({ detail }) {
        if (detail && detail?.value && detail?.name === 'sSearch' && detail?.value?.length >= 3) {
            this.filterConfig.find(k => k.name === detail?.name).keyword = detail.value;
        }
    }
}