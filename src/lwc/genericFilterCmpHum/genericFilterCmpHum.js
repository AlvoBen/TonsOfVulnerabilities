/*
    LWC Name: genericFilterCmpHum.html
Function: LWC to apply filters on search details;

Modification Log:
* Developer Name                  Date                         Description
* ------------------------------------------------------------------------------------------------------------------------------
* Atul Patil						06 / 02 / 2023					US - 4648135
* Nirmal Garg                       06/06/2023                  DF-7730 fix
****************************************************************************************************************************/
import { LightningElement, api } from 'lwc';

export default class GenericFilterCmpHum extends LightningElement {
    @api buttonConfig;
    @api filterconfig;
    @api displayfilter = false;
    @api isdisabled = false;

    @api clearMultiselect(payload) {
        if (payload) {
            if (
                this.template.querySelector(
                    'c-generic-multiselect-picklist-hum'
                ) != null
            ) {
                this.template
                    .querySelectorAll('c-generic-multiselect-picklist-hum')
                    .forEach((k) => {
                        k.clearSelection(payload);
                    });
            }
        }
    }


    @api clearComboBox() {
        this.template.querySelectorAll('lightning-combobox').forEach((ele) => {
            ele.value = '';
        });

        if (
            this.template.querySelector('c-generic-keyword-search-hum') != null
        ) {
            this.template
                .querySelector('c-generic-keyword-search-hum')
                .clearSearchData();
        }
    }



    handleClick(event) {
        this.dispatchEvent(
            new CustomEvent('filterbtnclick', {
                detail: event.target.dataset.label
            })
        );
        if (event.target.dataset.label && event.target.dataset.label.toLowerCase() === 'clear') {
            if (this.template.querySelector('c-generic-multiselect-picklist-hum') != null) {
                this.template.querySelectorAll('c-generic-multiselect-picklist-hum')
                    .forEach((k) => {
                        k.clearDropDowns();
                    });
            }



            if (this.template.querySelectorAll('c-generic-date-selector') != null
            ) {
                this.template
                    .querySelectorAll('c-generic-date-selector')
                    .forEach((ele) => {
                        ele.clearDates();
                    });
            }


        }
    }

    handleChange(event) {
        this.isdisabled = false;
        this.dispatchEvent(
            new CustomEvent('selectcmbvalue', {
                detail: {
                    label: event.target.dataset.id,
                    value: event.detail.value,
                    name: event.target.name
                }
            })
        );
    }

    get getStyle() {
        let maxheight = window.innerHeight - 160;
        return (
            ' border: 1px solid #dddbda ;width:320px;height: ' +
            maxheight +
            'px;'
        );
    }

    @api
    setData(filterconfig, buttonconfig) {
        this.filterconfig = filterconfig;
        this.buttonConfig = buttonconfig;
    }
}