/*******************************************************************************************************************************
LWC JS Name : custom_filter_CsrHum.js
Function    : This JS serves as controller to custom_filter_CsrHum.html. 

Modification Log: 
Developer Name           Code Review                      Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Shailesh Bagade                                         12/29/2020                  initial version(Feature 2963843 - Cases/Tasks Tab)
*********************************************************************************************************************************/

import { LightningElement, track, wire, api } from 'lwc';
const obj = {
    label: '',
    value: '',
    checked: false
}
import { publish, MessageContext } from "lightning/messageService";
import NoWork_CaseTaskView_HUM from '@salesforce/label/c.NoWork_CaseTaskView_HUM';
import CASELIMIT_MESSAGE_HUM from '@salesforce/label/c.CASELIMIT_MESSAGE_HUM';
import TIMEOUT_MESSAGE_HUM from '@salesforce/label/c.TIMEOUT_MESSAGE_HUM';
import { ShowToastEvent } from 'lightning/platformShowToastEvent';
import CASE_CHANNEL from "@salesforce/messageChannel/CaseTaskDataCSR__c";
import getInitialLoad from '@salesforce/apex/Hum_CaseTasks_LWC.getInitialLoad';
import pullWorkItems from '@salesforce/apex/Hum_CaseTasks_LWC.pullWorkItems';
import applyFilter from '@salesforce/apex/Hum_CaseTasks_LWC.applyFilter';
import CaseTaskAllOtherQV from '@salesforce/label/c.CaseTaskAllOtherQV';

export default class Custom_filter_CsrHum extends LightningElement {

    @track iconName = 'utility:filterList';
    @track resultData = [];
    _handler;
    isApplyPullButtonDisable = false;
    openedFilter;
    i = 0;
    filterFlag = false;
    toggleAnimation = 'slds-dropdown slds-dropdown_right slider'
    showFilter = false;
    @api issupervisor;
    currentUserName;
    @track arr = [];
    isFiltered = false;
    @track filters = [];
    areFiltersAvail = false;
    @track value = [];
    @track _filter = [];
    @track _value = [];
    filteredValues = [];
    currentQueue = '';
    isLoading = true;
    currentProfile;
    @track showFilter_css = 'slds-dropdown-trigger_click ';
    @track allView = [];
    bswitch_JS_2578074;
    @track allPresentViewsToWorkQ = [];
    @track allWorkQueueList = [];
    label = { NoWork_CaseTaskView_HUM, CASELIMIT_MESSAGE_HUM, TIMEOUT_MESSAGE_HUM };
    firstOnly = false;
    selectedValue = ['Due Date'];
    multiSelect = false;
    hasRendered = true;
    @wire(MessageContext)
    messageContext;
    @api
    enableDisableLoader(attr) {
        this.isLoading = attr;
    }
    connectedCallback() {
        if (this.firstOnly) {
            return;
        }

        this.firstOnly = true;
        this.doInit();
    }
    /*
     * Method calls on page load
    */
    async doInit() {
        try {
            this.isLoading = true;

            const data = await getInitialLoad();
            let selectedView = [];
            this.allView = data.queueViewList;
            this.currentProfile = data.currentProfile;

            //get bswitch_JS_2578074
            this.bswitch_JS_2578074 = data.bswitch_JS2578074;
            //profie check 
            this.dispatchEvent(new CustomEvent('profilecheck', {
                detail: {
                    currentProfile: data.currentProfile,
                    totalCount: data.initialFetchCaseTasks.length,

                }
            }));
            //get current user for accept selected unassigned check
            this.currentUserName = data.currentUserName;
            //work queue
            this.allWorkQueueList = data.workQueueList;
            this.filters.push({ key: 'Work Queue', value: this.getListvalues(data.workQueueList), values1: [data.currentQueue == null ? '' : data.currentQueue], showSelectAll: true, allItemsSelectOnLoad: false, isMultipleSelection: true });
            this.filteredValues['Work Queue'] = data.currentQueue == null ? '' : Object.assign([], data.currentQueue.split(','));
            this.currentQueue = data.currentQueue;

            //view
            selectedView = data.queueViewList.filter(element => element.includes('(' + data.currentQueue + ')') || element.includes(CaseTaskAllOtherQV));

            if (selectedView.length > 0 && selectedView != null) {
                this.filters.push({ key: 'View', value: this.getListvalues(selectedView), values1: selectedView, showSelectAll: true, allItemsSelectOnLoad: true, isMultipleSelection: true });
                this.filteredValues['View'] = Object.assign([], selectedView);
                this.filteredValues['View'].unshift('All');
                //assgn all view for workqueue
                this.allPresentViewsToWorkQ = selectedView;
            }
            this.resultData = data.initialFetchCaseTasks;
            const messaage = {
                tableData: data.initialFetchCaseTasks,
                allPresentViewOnPanel: selectedView,
                currentProfile: data.currentProfile

            };
            publish(this.messageContext, CASE_CHANNEL, messaage);
            //profie check
            //sort by  &7 select all is not there
            if (!this.issupervisor) {
                if (data.pullWorkFilterByOptions !== undefined) {
                    this.filters.push({ key: 'Sort By', value: this.getListvalues(data.pullWorkFilterByOptions), values1: data.pullWorkFilterByOptions == null ? '' : data.pullWorkFilterByOptions[0], showSelectAll: false, allItemsSelectOnLoad: false, isMultipleSelection: false });
                    this.filteredValues['Sort By'] = data.pullWorkFilterByOptions == null ? '' : Object.assign([], data.pullWorkFilterByOptions[0].split(','));
                }
            }

            //filter by cases selectedViews
            if (data.selectedView !== undefined) {
                this.filters.push({ key: 'Filter By', value: this.getListvalues(data.selectedView), values1: data.selectedView == null ? '' : data.selectedView, showSelectAll: true, allItemsSelectOnLoad: true, isMultipleSelection: true });
                this.filteredValues['Filter By'] = data.selectedView == null ? '' : Object.assign([], data.selectedView);
                this.filteredValues['Filter By'].unshift('All');
            }

            this._filter = [...this.filters];
            this.isLoading = false;
        } catch (error) {
            this.isLoading = false;
        }
    }

    /*
     * method to prepare label, value object format, format - value , label
    */
    getListvalues(passedObj) {

        let queue = [];
        let i = 0;
        for (let key of passedObj) {
            const emp = Object.create(obj);
            emp.label = key;
            emp.value = key;
            i === 0 ? emp.checked = true : emp.checked = false;
            queue.push(emp);
            i++;
        }

        return queue;
    }

    closeModal() {
        this.showFilter_css = 'slds-dropdown-trigger_click';
    }
    handleMouseLeave(event) {
        this.template.querySelector('.divHelp').classList.add('slds-hide'); 
    }
    handleMouseEnter(event) {
        this.template.querySelector('.divHelp').classList.remove('slds-hide'); 

    }
    selectFilter(event) {

        if (this.showFilter) {
            this.showFilter = false;
            this.showFilter_css = 'slds-dropdown-trigger_click';
        }
        else {
            this.showFilter = true;
            this.showFilter_css = 'slds-dropdown-trigger_click  slds-is-open';
            document.addEventListener('click', this._handler = this.outsideFilterClick.bind(this));

        }
        if (this.iconName == 'standard:filter')
            this.iconName = 'utility:filterList';
        else
            this.iconName = 'standard:filter';

    }
    /**
     * Generic method to handle toast messages
     * @param {*} title 
     * @param {*} message 
     * @param {*} variant 
    */
    showToast(title, message, variant) {
        const event = new ShowToastEvent({
            title: title,
            message: message,
            variant: variant,
            mode: 'dismissable'
        });
        this.dispatchEvent(event);
    }
    /*
     * Method to handle Inside Filte Click
    */
    handleInsideFilterClick(event) {
        event.stopPropagation();
        return false;
    }
    /*
     * Restrict renderedCallback to be excuted once
    */

    handleSelectAll(event) {
        let found = [];
        this.isFiltered = true;

        found = this._filter.filter((element) => element.key === event.target.dataset.id)[0];
        found.values1 = [];
        found.value.forEach(element => {
            if (found.hasOwnProperty('values1')) {
                if (event.detail.checked) {
                    found.values1.push(element.value);
                }
                else {
                    found.values1 = [];
                }

            }
        });

        this._filter.filter((element) => {
            element.key === event.target.dataset.id
        }).values1 = found;
        this.filteredValues[event.target.dataset.id] = found.values1.join(',').split(',');

        //call method to add view if work queue is selected
        if (event.target.dataset.id === 'Work Queue' && this.template.querySelector('lightning-formatted-text[data-id="View"]') !== null) {
            this.selectRemoveViewonWorkQ(found.values1);

        }


        if (event.detail.checked)
            this.filteredValues[event.target.dataset.id].unshift('All');
        else if (!event.detail.checked)
            this.filteredValues[event.target.dataset.id].shift('All');//disable applyPull work button
        this.disableApplynPullButton()

    }
    /*
     * Method to disable Apply button
    */
    disableApplynPullButton() {
        // disable apply button rectify on permission set
        if (!this.issupervisor) {
            if ((this.filteredValues['Filter By'].length > 0 && this.filteredValues["Filter By"][0] !== '') &&
                (this.filteredValues['View'].length > 0 && this.filteredValues["View"][0] !== '') &&
                (this.filteredValues['Work Queue'].length > 0 && this.filteredValues["Work Queue"][0] !== '')) {
                this.isApplyPullButtonDisable = false;
            }
            else {
                this.isApplyPullButtonDisable = true;
            }
        }
        this.dispatchEvent(new CustomEvent('disablepullwork', {
            detail: {
                isApplyPullButtonDisable: this.isApplyPullButtonDisable
            }
        }));
    }
    /*
     * Method calls when ApplyFilter button is clicked
    */
    handleApplyclick(event) {
        this.isLoading = true;
        this.outsideFilterClick(event);
        applyFilter({
            bPerformFilter: true,
            allWorkQueueList: this.allWorkQueueList,
            lstWorkQ: this.filteredValues['Work Queue'],
            allQueueViewList: this.allPresentViewsToWorkQ,
            lstView: this.filteredValues['View'],
            lstFilterBy: this.filteredValues['Filter By'],
            lstWorkItems: 'All',
            lstSecurityGrp: 'All',
            lstItemAge: null,
            lstTeamMembers: null,
        })
            .then((result) => {
                if (result.length != null) {
                    //store result to global var to add pull work single entry to it when pressed pull work
                    this.resultData = result
                    // Error for CASELIMIT_MESSAGE_HUM(Validation 9)
                    if (result.length > 3500) {
                        this.showToast('Error', this.label.CASELIMIT_MESSAGE_HUM, 'error');
                    }

                    //send table data through event to custom_dashboard
                    const message = {
                        tableData: result,
                        allPresentViewOnPanel: this.allPresentViewsToWorkQ
                    };
                    publish(this.messageContext, CASE_CHANNEL, message);
                    //update record count on page pagination
                    this.dispatchEvent(new CustomEvent('profilecheck', {
                        detail: {
                            currentProfile: this.currentProfile,
                            totalCount: result.length,
                        }
                    }));
                }
                else {
                    this.showToast('Error', this.label.TIMEOUT_MESSAGE_HUM, 'error');

                }
                this.isLoading = false;
                this.isFiltered = false;

            })
            .catch((error) => {
                
            })
    }
    /*
     * Method calls when any Filter values is checked
    */
    handleFiltervalueChecked(event) {
        this.value = event.detail.value;
        this.isFiltered = true;
        //for multiple select options except sort by
        if (event.target.dataset.multiselect === "yes") {
            if (event.target.dataset.id === 'Work Queue' && this.template.querySelector('lightning-formatted-text[data-id="View"]') !== null) {
                this.selectRemoveViewonWorkQ(this.value);
            }
            this._filter.filter(fil => fil.key === event.target.dataset.id)[0].values1 = this.value//.join(',');
            this.filteredValues[event.target.dataset.id] = this.value.join(',').split(',');
            //select all check if all are selected
            if (this._filter.filter(fil => fil.key === event.target.dataset.id)[0].showSelectAll) {
                //to uncheck select all
                if (this.template.querySelector('lightning-input[data-id="' + event.target.dataset.id + '"]').checked)
                    this.template.querySelector('lightning-input[data-id="' + event.target.dataset.id + '"]').checked = false;
                //check if all element present, mark select all 

                let arr2 = [];
                arr2 = this.filters.filter(fil => fil.key === event.target.dataset.id)[0].value;

                const containsAll = arr2.every(element => {
                    return this.filteredValues[event.target.dataset.id].includes(element.label);
                });
                if (containsAll) {
                    this.template.querySelector('lightning-input[data-id="' + event.target.dataset.id + '"]').checked = true;
                    this.filteredValues[event.target.dataset.id].unshift('All');
                }
            }
        }
        this.disableApplynPullButton();

    }
    /*
     * Method to call Pull Work Items
    */
    @api
    pullWorkItems() {
        try {

            this.isLoading = true;
            let lstWorkQ = this.filteredValues["Work Queue"].filter(index => index !== 'All');
            let lstView = this.filteredValues["View"].filter(index => index !== 'All');
            let sFilterBy = this.filteredValues['Sort By'];
            let sFilterTypeCheck = this.filteredValues['Filter By'];

            pullWorkItems({ lstQueue: lstWorkQ, lstView: lstView, sFilterBy: sFilterBy[0], sFilterTypeCheck: sFilterTypeCheck })
                .then(result => {
                    //show error if not record displayed, customm label - NoWork_CaseTaskView_HUM
                    if (result.length !== 0) {
                        //add the single row return to total data to show
                        this.resultData.unshift(result[0]);
                        //send table data through event to custom_dashboard
                        const messaage = {
                            tableData: this.resultData,
                            allPresentViewOnPanel: this.allPresentViewsToWorkQ
                        };
                        publish(this.messageContext, CASE_CHANNEL, messaage);
                        //update record count on page pagination
                        this.dispatchEvent(new CustomEvent('profilecheck', {
                            detail: {
                                currentProfile: this.currentProfile,
                                totalCount: this.resultData.length,
                                allPresentViewOnPanel: this.allPresentViewsToWorkQ
                            }
                        }));
                        this.isLoading = false;
                    }
                    else {
                        this.isLoading = false;
                        this.showToast('Error', this.label.NoWork_CaseTaskView_HUM, 'Error');
                    }

                })
                .catch(error => {
                    this.showToast('Error', this.label.NoWork_CaseTaskView_HUM, 'Error')
                    this.isLoading = false;
                });
        } catch (error) {
            this.showToast('Error', this.label.NoWork_CaseTaskView_HUM, 'Error')
            this.isLoading = false;
        }

    }
    selectRemoveViewonWorkQ(selectedvalues) {

        this.arr = [];

        if (this.issupervisor)
            this.arr.push(CaseTaskAllOtherQV);
        selectedvalues.forEach(element => {

            for (let el of this.allView) {
                if (el.includes('(' + element + ')') && !this.arr.includes(el)) {
                    this.arr.push(el);
                }
            }
        });
        let newViews = [];
        newViews = this.getListvalues(this.arr);
        this.allPresentViewsToWorkQ = this.arr;

        //unselect 'select all', if all work Q are unselected and no view is there
        if (this.arr.length === 0) {
            this.template.querySelector('lightning-input[data-id="View"]').checked = false;
        }
        this._filter.filter(fil => fil.key === 'View')[0].value = newViews;
        this._filter.filter(fil => fil.key === 'View')[0].values1 = this.arr;
        this.filteredValues['View'] = this.arr.join(',').split(',');

        //select all or unselect all if all or none elmenet of view sleected or unselected
        if (this._filter.filter(fil => fil.key === 'View')[0].showSelectAll && this.arr.length > 0) {
            //to uncheck select all
            if (this.template.querySelector('lightning-input[data-id="View"]').checked)
                this.template.querySelector('lightning-input[data-id="View"]').checked = false;
            //check if all element present, mark select all 

            let arr2 = [];
            arr2 = this.filters.filter(fil => fil.key === 'View')[0].value;

            const containsAll = arr2.every(element => {
                return this.filteredValues['View'].includes(element.label);
            });
            if (containsAll) {
                this.template.querySelector('lightning-input[data-id="View"]').checked = true;
                this.filteredValues['View'].unshift('All');
            }
        }
    }
    /*
     * Method to Reset Filters
    */
    handleResetFilter(event) {
        this._filter.forEach(element => {
            element.values1 = [];
        });
        this.isFiltered = false;
    }
    /*
     * Method to handle Filter Item Clicked
    */
    handleFilterItemClick(event) {
        let selected = event.target.dataset.id;
        if (event.target.dataset.id !== 'Sort By') {
            this.template.querySelector('div[data-color="' + selected + '"]').classList.toggle('yellow-color');
            this.template.querySelector('lightning-icon[data-id="' + selected + '"]').iconName === 'utility:down' ? 'utility:right' : 'utility:down';
            this.template.querySelector('div[data-id="' + selected + '"]').classList.toggle('slds-hide');
            let previouslyOppened = this.openedFilter;
            if (previouslyOppened) {
                this.template.querySelector('div[data-id="' + previouslyOppened + '"]').classList.add('slds-hide');
                this.template.querySelector('div[data-color="' + previouslyOppened + '"]').classList.remove('yellow-color');
            }
            if (previouslyOppened !== selected) {
                this.openedFilter = selected;
            } else {
                this.openedFilter = null
            }

            if (selected === 'Filter By' || selected === 'Sort By') {
                const topDiv = this.template.querySelector('div[data-id="scrollToMe"]').getBoundingClientRect().top;
                this.template.querySelector('div[data-id="scroller"').scrollTop = topDiv;

            }
            window.scrollTo({
                top: 500,
                behavior: 'smooth'
            });
        }


    }
    /*
     * Method to handle when On Filter Clicked
    */
    onFilterClick(event) {
        event.stopPropagation();
        this.closeOpenedFilters();
        return false;
    }
    /*
     * Method to handle when Cross mark clicked
    */
    handleCrossClick(event) {
        event.stopPropagation();
        document.removeEventListener('click', this._handler);
        this.closeOpenedFilters();
        this.showFilter = false;
        this.showFilter_css = 'slds-dropdown-trigger_click';
        this.iconName = 'utility:filterList';
        this.i = 0;
    }
    /*
     * Method to close filter when clicked outside filter
    */
    outsideFilterClick(event) {
        event.stopPropagation();
        this.i++;
        if (this.i > 1) {
            document.removeEventListener('click', this._handler);
            this.closeOpenedFilters();
            this.showFilter = false;
            this.showFilter_css = 'slds-dropdown-trigger_click';
            this.iconName = 'utility:filterList';
            this.i = 0;
        }

    }
    /*
     * Method to close open filters
    */
    closeOpenedFilters(event) {
        if (this.openedFilter) {
            let previouslyOppened = this.openedFilter;
            this.template.querySelector('div[data-id="' + previouslyOppened + '"]').classList.add('slds-hide');
            this.template.querySelector('div[data-color="' + previouslyOppened + '"]').classList.toggle('yellow-color');
            this.openedFilter = null;
        }
    }
    /*
     * Restrict renderedCallback to be excuted once
    */
    renderedCallback() {
        if (this.hasRendered) {
            if (this._filter.filter(fil => fil.key === 'Sort By')[0]) {
                let options = this._filter.filter(fil => fil.key === 'Sort By')[0].value;
                this.template.querySelector('c-custom-multi-select-combobox-hum').setMenuItems(options);
                this.hasRendered = false;
            }
        }
    }
    handleToggle(){
        this.template.querySelector('div[data-color="Sort By"]').classList.add('yellow-color');
    }
    handleToggleOnBlur(){
        this.template.querySelector('div[data-color="Sort By"]').classList.remove('yellow-color');
    }
    /*
     * Method to handle SortBy when SortBy Filter is clicked
    */
    handleSortBy(event) {
        this.template.querySelector('div[data-color="Sort By"]').classList.remove('yellow-color');
        this.isFiltered = true;
        this._filter.filter(fil => fil.key === 'Sort By')[0].values1 = event.detail.single.split(',');
        this.filteredValues['Sort By'] = event.detail.single.split(',');
        this.disableApplynPullButton();

    }
}