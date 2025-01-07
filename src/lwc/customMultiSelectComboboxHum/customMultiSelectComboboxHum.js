/*
LWC Name        : CustomMultiSelectComboboxHum.js
Function        : CustomMultiSelectComboboxHum used to show additional columns

Modification Log:
* Developer Name                  Date                          Description
*------------------------------------------------------------------------------------------------------------------------------
* Shailesh B                      07/03/2022                    Original Version 
* Gowthami T                      07/03/2022                    Original Version 
****************************************************************************************************************************/
import { LightningElement, track, api } from 'lwc';
 
export default class CustomMultiSelectComboboxHum extends LightningElement {
    
    @api options;
    @api selectedValue;
    @api selectedValues = [];
    @api label;
    @api minChar = 2;
    @api disabled = false;
    @api multiSelect = false;
    @track value;
    @track values = [];
    @track optionData;
    @track searchString;
    @track message;
    @track showDropdown = false;
 @api
 setMenuItems(options) {
        this.showDropdown = false;
        this.options = options;
        var optionData = this.options ? (JSON.parse(JSON.stringify(this.options))) : null;
        var value = this.selectedValue ? (JSON.parse(JSON.stringify(this.selectedValue))) : null;
        var values = this.selectedValues ? (JSON.parse(JSON.stringify(this.selectedValues))) : null;
 if((value || values)) {
            var searchString;
         var count = 0;
            for(var i = 0; i < optionData.length; i++) {
                if(this.multiSelect) {
                    if(values.includes(optionData[i].value)) {
                        optionData[i].selected = true;
                        if(optionData[i].value !== 'selectall')
                            count++;
                    }  
                } else {
                    if(optionData[i].value == value) {
                        searchString = optionData[i].label;
                        optionData[i].selected =  true;
                    }
                }
            }
            if(this.multiSelect){
                this.searchString = count ===0 ? 'Show / Hide Columns' : count + ' Option(s) Selected';
                
            }
            else{
                this.searchString = searchString;
            }
        }
        this.value = value;
        this.values = values;
        this.optionData = optionData;
        //this.showOptions();
    }
 
    filterOptions(event) {
        this.searchString = event.target.value;
        if( this.searchString && this.searchString.length > 0 ) {
            this.message = '';
            if(this.searchString.length >= this.minChar) {
                var flag = true;
                for(var i = 0; i < this.optionData.length; i++) {
                    if(this.optionData[i].label.toLowerCase().trim().startsWith(this.searchString.toLowerCase().trim())) {
                        this.optionData[i].isVisible = true;
                        flag = false;
                    } else {
                        this.optionData[i].isVisible = false;
                    }
                }
                if(flag) {
                    this.message = "No results found for '" + this.searchString + "'";
                }
            }
            this.showDropdown = true;
        } else {
            this.showDropdown = false;
        }
 }
 
    selectItem(event) {
        var selectedVal = event.currentTarget.dataset.id;
        if(selectedVal) {
            var count = 0;
            var options = JSON.parse(JSON.stringify(this.optionData));
            for(var i = 0; i < options.length; i++) {
                if(!this.multiSelect) {
                    options[i].selected = false;
                } 
                if(options[i].value === selectedVal  ) {
                    if(this.multiSelect) {
                        if(this.values.includes(options[i].value)) {
                            this.values.splice(this.values.indexOf(options[i].value), 1);
                        } 
                        else {
                            this.values.push(options[i].value);
                        }
                        //options[i].selected =  true; 
                        options[i].selected = options[i].selected ? false : true;   
                    } else {
                        this.value = options[i].value;
                        this.searchString = options[i].label;
                        options[i].selected =  true;
                    }
                }
                if(selectedVal === 'selectall' ) {
                    if(this.multiSelect) {
                        if(options[i].selected) {
                            this.values.splice(this.values.indexOf(options[i].value), 1);
                            //options[i].selected =  false;
                             
                        }
                        else {
                            this.values.push(options[i].value);
                            //options[i].selected =  true;
                        }
                        options[i].selected = options[0].selected ? true : false;
                    }  
                }
                // if(options[i].selected) {
                //     count++;
                // }
            }
            if(selectedVal !== 'selectall' ) {
                let varifySelectAlltrue = true; 
                const containsAll = options.forEach(element=>{
                    if( element.value !== "selectall"){
                    //console.log('iner ' ,e.selected);
                    varifySelectAlltrue = varifySelectAlltrue && element.selected;
                    } 
                    if(element.selected=== undefined)
                        varifySelectAlltrue = false
                });
                options[0].selected = varifySelectAlltrue ? true : false
            }
            
            for(var i = 0; i < options.length; i++) {
                if(options[i].selected && options[i].value !== 'selectall') {
                    count++;
                }
            }
            this.optionData = options;
            if(this.multiSelect)
                this.searchString = count ===0 ? 'Show / Hide Columns' : count + ' Option(s) Selected';
            if(this.multiSelect)
                event.preventDefault();
            else
                this.showDropdown = false;
             //fire event
             const event1 = new CustomEvent('childclick', {
                detail: {
                    multiple: this.optionData,
                    single: this.value
                    
                }
            });
                this.dispatchEvent(event1);
        }
    }
 
    showOptions() {
        if(this.disabled == false && this.options) {
            this.message = '';
            this.searchString = '';
            var options = JSON.parse(JSON.stringify(this.optionData));
            for(var i = 0; i < options.length; i++) {
                options[i].isVisible = true;
            }
            if(options.length > 0) {
                this.showDropdown = true;
            }
            this.optionData = options;
        }
 }

 
    blurEvent() {
        try {
            var previousLabel;
            var count = 0;
            for(var i = 0; i < this.optionData.length; i++) {
                if(!this.multiSelect && this.optionData[i].value === this.value.toString()) {
                    previousLabel = this.optionData[i].label;
                }
                if(this.optionData[i].selected && this.optionData[i].value !== 'selectall') {
                    count++;
                }
            }
            if(this.multiSelect)
            this.searchString = count ===0 ? 'Show / Hide Columns': count + ' Option(s) Selected';
            else
            this.searchString = previousLabel;
            
            this.showDropdown = false;
    
            this.dispatchEvent(new CustomEvent('select', {
                detail: {
                    'payloadType' : 'multi-select',
                    'payload' : {
                        'value' : this.value,
                        'values' : this.values
                    }
                }
            }));
        } catch (error) {
            
        }
    }
}