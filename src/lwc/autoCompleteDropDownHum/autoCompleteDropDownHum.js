/*******************************************************************************************************************************
Component Name : autoCompleteDropDownHum
Version        : 1.0
Created On     : 2/04/2021
Function       : Typeahead dropdown with input and search feature, Component can be configured using options, label,
                 placeholder, value. IsValid to indicate validition
                 
Modification Log: 
* Version          Developer Name             Code Review              Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------------
*    1.0           Swetha                                         	16/06/2021                	Original Version
*    1.1           Ankit Avula                                      09/01/2021                  US2365934 populate state field with zipcode input
**************************************************************************************************************************************/
import { LightningElement, track, api } from 'lwc';
import { hcConstants,getLabels } from 'c/crmUtilityHum';

export default class autoCompleteDropDownHum extends LightningElement {
    @api label;
    @api placeholder;
    @api isValid;
    @api customHeight;
	@api isHelpText;
    @track selectedValue = '-None-';
    @track isSelected = false;
    @track showList = false;
    @track listItems = [];
    @track customLabels = getLabels();
    @track ulClass = 'slds-listbox slds-listbox_vertical slds-dropdown slds-dropdown_fluid slds-dropdown_left list-scroll';
    @track inputCustomClass = 'slds-input hc-generic-dropdown dropdown-input validation';
    boolIsCustomError = false;
	stateError='';

    @api isRequired;
    @api customSearch = "false";
    
    /** 
     * value is being sent from parent component and then set on the State field.
     */
    @api get value() {
        return this._value;
    }

    set value(val) {
        if (!val) return;
        if(val.length != 2 && this.selectedValue != val){
            this.isSelected = true;
            this.selectedValue = val;
            let selectedVal;
            this.closeList();
            this.listItems.forEach(temp => {
                if (temp.label === this.selectedValue) {
                    selectedVal = temp.value;
                }
            });
            this.fireSelectionEvent(selectedVal);
        } 
        this._value = val;
    }

    _options = [];
    /**
     * options will be shown in dropdown list. Ex format: 
     * [{ "label": "Alaska", value: "AL"}, {"label": "Texas", value: "TX"}]
     */
    @api set options(value) {
        if (!value) return;
        this.listItems = [...value];
        this._options = this.listItems;
    }
    get options() {
        return this._options;
    }

    connectedCallback() {
        if(this.customHeight){
            this.ulClass = 'slds-listbox slds-listbox_vertical slds-dropdown slds-dropdown_fluid slds-dropdown_left list-scroll customheight';
        }
        const me = this;
        // listen focus out off Drop down 
        me.genericDropDownFocusOut = function (evnt) {
            const dropDownContainer = evnt.toElement && evnt.toElement.classList
                && evnt.toElement.classList.contains('hc-generic-dropdown');
            if (dropDownContainer === false && me.showList) {
                me.closeList();
                me.clearInputValue();
            }
        };
        document.addEventListener('click', this.genericDropDownFocusOut, false);
        //updating css if field is invalid
        me.inputClass = (me.isValid) ? 'slds-input hc-dropdown-selection error-highlight' : 'slds-input hc-dropdown-selection';

        if (me.value) {
            me.preSelectValue(me.value);
        }
    }

    /**
     * Remove event listeners on disconnect
     */
    disconnectedCallback() {
        document.removeEventListener('click', this.genericDropDownFocusOut);
    }
    /**
     * shows error message if selected value is blank
     */
    @api
    showValidationError(){
       let field =  this.template.querySelector(".validation");
       let isInvalid = true;
       if(this.selectedValue == '-None-' || this.selectedValue == ""){
        //    field.setCustomValidity(this.customLabels.Hum_BillingStateError);
        this.inputCustomClass = 'slds-input hc-generic-dropdown dropdown-input validation inline-error';
        this.boolIsCustomError = true;
		this.stateError = 'Select value from list';
           isInvalid = true;
       }else{
        this.inputCustomClass = 'slds-input hc-generic-dropdown dropdown-input validation';  
        this.boolIsCustomError = false;
           field.setCustomValidity('');
		   isInvalid = false;
       }
       field.reportValidity();
       return isInvalid;
    }

	/**
	 * remove the validation error manually triggered
     */
    @api
    removeValidationError(){
       let field =  this.template.querySelector(".validation");
       let isInvalid = false;
       
        this.inputCustomClass = 'slds-input hc-generic-dropdown dropdown-input validation';  
        this.boolIsCustomError = false;
           field.setCustomValidity('');
       return isInvalid;
    }

    /**
     * Clears selection and input
     * value on form reset
     */
    @api
    reset() {
        this.clearInputValue();
        this.onSelectionClear();
    }

    /**
     * Shows dropdown list as per
     * matching user input on typing
     */
    onKeyUpHandler(event) {
        const me = this;
        if (event.keyCode == 27) {
            return;
        }
        const searchStr = event.target.value;
        if (this.customSearch   && this.customSearch == "true") {
            if (searchStr.length >= 1) {
                me.listItems = me._options.filter(item => item.label.toLowerCase().startsWith(searchStr.toLowerCase()));
            } else if (event.keyCode === 8 || event.keyCode === 46) {
                me.listItems = me._options;
            }
        } 
        else if(this.customSearch   && this.customSearch == "false") {
            if (searchStr.length > hcConstants.MIN_SEARCH_CHAR) {
                me.listItems = me._options.filter(item => item.label.toLowerCase().indexOf(searchStr.toLowerCase()) >= 0);
            } else if (event.keyCode === 8 || event.keyCode === 46) {
                me.listItems = me._options;
            }
        }
        
        const firstItem = me.template.querySelector(`[data-options-id='0']`);
        firstItem && firstItem.focus();
        me.shwoOrHideList(me.listItems.length > 0);
    }

    /**
    * Handles preselected value
    * and updates the same on
    * element
    */
    preSelectValue(val) {
        this.isSelected = true;
        this.updateValue(val);
    }

    updateValue(val){
        this.selectedValue = val;
        this.value = val;
    }


    /**
    * Handles dropdown list
    * focus and visibility on click 
    * of down arrow
    */
    onTriggerClick() {
        this.shwoOrHideList(this.listItems.length > 0);
        const inputEle = this.template.querySelector('input');
        inputEle.focus();
		this.onSelectionClear();
    }

    /**
     * Fires selection event
     * when user selects value
     * from dropdown
     */
    onSelection(event) {
        const me = this;
        let selectedVal;
        me.closeList();
        this.updateValue(event.target.innerText);
        me.listItems.forEach(temp => {
            if (temp.label === me.selectedValue) {
                selectedVal = temp.value;
            }
        });
        this.fireSelectionEvent(selectedVal);
        me.isSelected = true;
    }

    /**
     * Dispatch blur event to parent
     * @param {*} event 
     */
    onBlurHandler(event) {
        this.fireEvent('blur', event.target.value);
    }

    /**
     * Dispatch focus event to parent
     * @param {*} event 
     */
    onFocusHandler(event) {
        setTimeout(() => {
            this.fireEvent('focus', event.target.value);
        }, 1);
    }

    /**
     * Clears selected or
     * input value from
     * element
     */
    clearInputValue() {
        const inuptEle = this.template.querySelector('input');
        inuptEle.value = "";
        this.updateValue("");
        this.isSelected = false;
    }

    /**
     * Clears selected or input value from
     * element on click of clear icon
     */
    onSelectionClear() {
	    this.selectedValue = '';
        const me = this;
        me.clearInputValue();
        me.listItems = me._options;
        this.fireClearEvent(me.selectedValue);
		
    }

    /**
     * Show or hide the dropdown list using boolea. true: to show, false: to hide
     * @param {*} isVisible 
     */
    shwoOrHideList(isVisible) {
        this.showList = isVisible;
    }

    /**
     * Close Dropdown list
     */
    closeList(){
        this.showList = false;
    }

    /**
     * Dispatches event to parent
     * @param {*} eventName 
     * @param {*} detail 
     */
    fireEvent(eventName, detail) {
        this.dispatchEvent(new CustomEvent(eventName, { detail }));
    }

    /**
     * Fires clear event to
     * remove selected value in
     * response obj
     */
    fireClearEvent(selectedValue) {
        const detail = { data: selectedValue };
        const clear = new CustomEvent('clear', { detail });
        this.dispatchEvent(clear);
    }

    /**
     * Fires selection event to
     * update selected value in
     * response obj
     */
    fireSelectionEvent(dataToFetch) {
        const detail = { data: dataToFetch, value: dataToFetch };
        const select = new CustomEvent('select', { detail });
        this.dispatchEvent(select);
    }
	@api
	 validateSpeciality(){
        const me = this;
        let field =  this.template.querySelector(".validation");
        let boolSpecValid = false;
        me.listItems.forEach(function (field) { 
            if(me.selectedValue == field.value){
                boolSpecValid = true;
            }
        });
        if(boolSpecValid){
            this.inputCustomClass = 'slds-input hc-generic-dropdown dropdown-input validation';  
            field.setCustomValidity('');
            this.boolIsCustomError = false;
        }
        else{
            this.inputCustomClass = 'slds-input hc-generic-dropdown dropdown-input validation inline-error';
            this.boolIsCustomError = true;
            this.stateError = 'Select value from list';
        }
        return boolSpecValid;
       


    }
}