/*******************************************************************************************************************************
Component Name : GenericDropDownHum
Version        : 1.0
Created On     : 2/04/2021
Function       : Typeahead dropdown with input and search feature, Component can be configured using options, label,
                 placeholder, value. IsValid to indicate validition
                 
Modification Log: 
* Developer Name               Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------------
* Mohan Kumar                  02/04/2021                	Original Version
* Supriya Shastri              02/04/2021                	Original Version
* Mohan kuamr N                06/01/2021                	DF-3167
* Mohan kuamr N                09/01/2021                	US:2337436 Adding Accessibility
* Mohan kuamr N                09/29/2021                	DF: 3812
* Deepak Khandelwal			   09/01/2023					US: 4926578 State Field
**************************************************************************************************************************************/
import { LightningElement, track, api } from 'lwc';
import { hcConstants, getLabels, eventKeys } from 'c/crmUtilityHum';

export default class GenericDropDownHum extends LightningElement {
    @api label;
    @api value;
    @api placeholder;
    @api isValid;
    @api required;
    @track selectedValue;
    @track isSelected = false;
    @track showList = false;
    @track listItems = [];
    @track inputClass = 'slds-input hc-generic-dropdown dropdown-input';
    @track bShowErrorMsg = false;
    @track labels = getLabels();
    _options = [];
    _baseCss = 'slds-input hc-generic-dropdown dropdown-input ';
    _errorCss = ' error-highlight';
    _highlightedOptionElement = null;
    _highlightedOptionIndex = -1;
    /**
     * options will be shown in dropdown list. Ex format: 
     * [{ "label": "Alaska", value: "AL"}, {"label": "Texas", value: "AX"}]
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
        const me = this;
        // listen focus out off Drop down 
        me.genericDropDownFocusOut = function (evnt) {
            const dropDownContainer = evnt.toElement && evnt.toElement.classList
                && evnt.toElement.classList.contains('hc-generic-dropdown');
            if (dropDownContainer === false && me.showList) {
                me.showOrHideList(false);
                me.clearInputValue();
            }
        };
        document.addEventListener('click', this.genericDropDownFocusOut, false);
        //updating css if field is invalid
        me.inputClass = (me.isValid) ? me._baseCss + me._errorCss : me._baseCss;

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
     * Clears selection and input
     * value on form reset
     */
    @api
    reset() {
        this.clearInputValue();
        this.onSelectionClear(undefined ,  true);
    }

    /**
     * Fires on every key input. Handles filter of the records and accessibility
     * @param {*} event 
     */
    onKeyUpHandler(event) {
        const me = this;
        switch(event.key){
            case eventKeys.ArrowUp: 
                me.highlightOption(this._highlightedOptionIndex - 1);
                break;
            case eventKeys.ArrowDown: 
                me.highlightOption(this._highlightedOptionIndex + 1);
                break;
            case eventKeys.Enter: 
                me.onEnter();
                break;
            case eventKeys.Esc: 
            case eventKeys.Escape:
                me.showOrHideList(false);
                break;
            case eventKeys.Tab: // Do nothing on tab to allow default accessibility to work
                break;
            default:                
                me.applyFilter(event);
        }
    }

    /**
     * Filters the records for input characters
     * @param {*} event 
     */
    applyFilter(event){
        const searchStr = event.target.value;       
        const me = this;
		var firstChar =searchStr.charAt(0).toUpperCase();
        var Twodigit  =searchStr.charAt(0).toUpperCase()+''+searchStr.charAt(1).toUpperCase();    
       if (searchStr.length <= 2) { 
            me.temparry = me._options.filter(item => item.label.startsWith(firstChar));    
            me.temparry2 = me.temparry.filter(item => item.value.includes(Twodigit));    
            if(me.temparry.length == 0){        
                me.listItems = me._options.filter(item => item.value.includes("-None-"));        
            }else if(me.temparry2.length == 0){        
                me.listItems = me._options.filter(item => item.value.includes("-None-"));        
            }
             else{        
                me.listItems = me.temparry.filter(item => item.value.toLowerCase().indexOf(searchStr.toLowerCase()) >= 0) ;
                console.log('@@@ me.listItems 2--->>>'+JSON.stringify(me.listItems));
            }  
        }
         else if (searchStr.length > 2) { 
             me.temparry = me._options.filter(item => item.label.startsWith(firstChar));
             me.listItems = me.temparry.filter(item => item.label.toLowerCase().indexOf(searchStr.toLowerCase()) >= 0);
         }
          else if (event.key === eventKeys.Backspace || event.key === eventKeys.Delete) {
             me.listItems = me._options;
         }
        me.highlightOption(0);
        me.showOrHideList(me.listItems.length > 0);
        me.focusInputEle();
    }

    /**
     * Scroll across the dropdown whenever needed. Specifically while accessing the dropdown using arrow keys
     * @param {*} element 
     */
    scrollIntoViewIfNeeded(element) {
        const scrollingParent = this.template.querySelector('.list-scroll');
        const parentRect = scrollingParent.getBoundingClientRect();
        const findMeRect = element.getBoundingClientRect();
        if (findMeRect.top < parentRect.top) {
            if (element.offsetTop + findMeRect.height < parentRect.height) {
                scrollingParent.scrollTop = 0;
            } else {
                scrollingParent.scrollTop = element.offsetTop;
            }
        } else if (findMeRect.bottom > parentRect.bottom) {
            scrollingParent.scrollTop += findMeRect.bottom - parentRect.bottom;
        }
    }


    /**
     * Handles Enter key press.Selects the highlighted option
     */
    onEnter(){
        const me = this;
        let selectedVal;
        if(me.listItems.length < 1 || !me._highlightedOptionElement) {
            return;
        }
        this.updateValue(this._highlightedOptionElement.innerText);
        me.listItems.forEach(temp => {
            if (temp.label === me.selectedValue) {
                selectedVal = temp.value;
            }
        });
        this.fireSelectionEvent(selectedVal);
        me.isSelected = true;
        me.focusCloseIcon();
        me.showOrHideList(false);
    }

    /**
     * Highlight the option for the passing index
     * @param {*} index 
     */
    highlightOption(index = 0) {
        const me = this;
        this.showOrHideList(true);
        index = index > me.listItems.length-1 ? 0: index < 0 ? 0: index; // If reached end, start from beginning
        
        if(me.listItems.length === 0){
            return;  // Return if there are no records.
        }
        // adding wait to allow dom to render first then apply the styles.
        setTimeout(() => {
            this.removeHighlight();
            const firstItem = this.template.querySelector(`[data-options-id='${index}']`);
            if(firstItem){
                this._highlightedOptionElement = firstItem;
                this._highlightedOptionIndex = index;
                firstItem.setAttribute('aria-selected', true);
                firstItem && firstItem.classList.toggle('slds-has-focus', true);
                this.scrollIntoViewIfNeeded(firstItem);
            }
        }, 10);
    }

    /**
     * Remvove highlight for the current record
     */
    removeHighlight() {
        if(this._highlightedOptionElement){
            this._highlightedOptionElement.setAttribute('aria-selected', false);
            this._highlightedOptionElement.classList.toggle('slds-has-focus', false);
        }
    }

    /**
    * Handles preselected value
    * and updates the same on
    * element
    */
   @api
    preSelectValue(val) {
        this.isSelected = true;
        this.updateValue(val);
    }

    updateValue(val){
        this.selectedValue = val;
    }


    /**
    * Handles dropdown list
    * focus and visibility on click 
    * of down arrow
    */
    onTriggerClick() {
        if(this.showList){
            this.showOrHideList(false);
        }
        else{
            this.showOrHideList(this.listItems.length > 0);
            this.focusInputEle();
            this.highlightOption(0);
        }
    }

    /**
     * Fires selection event
     * when user selects value
     * from dropdown
     */
    onSelection(event) {
        const me = this;
        let selectedVal;
        me.showOrHideList(false);
        this.updateValue(event.target.innerText);
        me.listItems.forEach(temp => {
            if (temp.label === me.selectedValue) {
                selectedVal = temp.value;
            }
        });
        this.fireSelectionEvent(selectedVal);
        me.isSelected = true;
        me.focusCloseIcon();
    }

    /**
     * Dispatch blur event to parent
     * @param {*} event 
     */
    onBlurHandler(event) {
        this.delayEventFire('blur', event.target.value);
    }
    
    /**
     * Dispatch focus event to parent
     * @param {*} event 
     */
    onFocusHandler(event) {
        this.delayEventFire('focus', event.target.value);
    }

    /**
     * Handle input focus out handler
     */
    onFocusOutHandler(event) {    
        if(!event.target.classList.contains('hc-generic-dropdown')){  //avoid closing when trigger clicked
            setTimeout(() => {
                this.showOrHideList(false); //Delay the list close to update the DOM
            },600);
        }
    }

    /**
     * Delay the eventfire due to drop down selection update
     * @param {*} eventName 
     * @param {*} detail 
     */
    delayEventFire(eventName, detail){
        const me = this;
        setTimeout(() => {
            me.fireEvent(eventName, detail);
        }, 500);
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
    onSelectionClear(event , bReset = false) {
        const me = this;
        me.clearInputValue();
        if(!bReset){
            const inuptEle = me.template.querySelector('input');
            inuptEle.focus();
        }
        me.listItems = me._options;
        this.fireClearEvent(me.selectedValue);
    }

    /**
     * Show or hide the dropdown list using boolea. true: to show, false: to hide
     * @param {*} isVisible 
     */
    showOrHideList(isVisible) {
        this.showList = isVisible;
        if(!isVisible){
            this.resetHighlights();
        }
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

    /**
     * If the value is empty and field is required then this method will show validation error with message
     */
    @api
    reportValidity() {
        const me = this;
        if(me.required && !me.isSelected){
            me.inputClass =  me._baseCss + me._errorCss;
            me.bShowErrorMsg = true;
        }
        else{
            me.inputClass = me._baseCss;
            me.bShowErrorMsg = false;
        }
    }

    /**
     * Reset highlight elements
     */
    resetHighlights() {
        this._highlightedOptionElement = null;
        this._highlightedOptionIndex = -1;
    }

    /**
     * Focus input element
     */
    focusInputEle() {
        const inputEle = this.template.querySelector('input');
        inputEle.focus();
    }
     
    /**
     * Move focus to close icon on selection
     */
    focusCloseIcon() {
        const me = this;
        setTimeout(() => {
            me.template.querySelector('.close-icon').focus();  // adding delay to render DOM
        },10);
    }
}