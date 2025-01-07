/*
LWC Name        : genericKeywordSearchHum
Function        : This is generic component for keyword search.

Modification Log:
* Developer Name                  Date                          Description
*------------------------------------------------------------------------------------------------------------------------------
Nirmal Garg                       07/14/2022                     Original Version
*****************************************************************************************************************************/
import { LightningElement, api, track } from 'lwc';

export default class GenericKeywordSearchHum extends LightningElement {
    @api
    placeholder;

    @api
    keyword;

    @api
    label;

    @api
    minchar;

    @api
    name;

    @track
    searchEntered = false;

    connectedCallback() {
        if (this.keyword && this.keyword.length === 0) {
            this.searchEntered = false;
        }
    }

    @api
    clearSearchData() {
        this.keyword = '';
        this.searchEntered = false;
        let inputelements = this.template.querySelectorAll('input');
        inputelements.forEach(function (item) {
            item.value = '';
        });
        this.dispatchEvent(new CustomEvent('textclear',{
            composed : true,
            bubbles : true
        }));
    }

    findByWord(event) {
        let tname = event.target.name;
        let tvalue = event.target.value
        this.keyword = event.target.value;
        // if (tvalue && tvalue.length > 0) {
            this.searchEntered = true;
            this.dispatchEvent(new CustomEvent('keywordvalue', {
                detail: {
                    name: tname,
                    value: tvalue
                },
                composed : true,
                bubbles : true
            })
	    );
       // }
    }

}