/*******************************************************************************************************************************
LWC JS Name : custom_filter_SupHum.js
Function    : This JS serves as controller to custom_filter_SupHum.html. 

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
import CASE_CHANNEL from "@salesforce/messageChannel/CaseTaskData__c";
import getInitialLoad from '@salesforce/apex/Hum_CaseTasks_LWC.getInitialLoad';
import applyFilter from '@salesforce/apex/Hum_CaseTasks_LWC.applyFilter';
import CaseTaskAllOtherQV from '@salesforce/label/c.CaseTaskAllOtherQV';
import hasCRMS_684_Medicare_Customer_Service_Access from '@salesforce/customPermission/CRMS_684_Medicare_Customer_Service_Access';

export default class Custom_filter_SupHum extends LightningElement {

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
    sPharmacyUser;
    currentQueue = '';
    isLoading = true;
    @track filterWrp = [];
    currentProfile;
    @track showFilter_css = 'slds-dropdown-trigger_click ';
    @track allView = [];
    bswitch_JS_2578074;
    @track allPresentViewsToWorkQ = [];
    @track allWorkQueueList = [];
    label = { NoWork_CaseTaskView_HUM, CASELIMIT_MESSAGE_HUM, TIMEOUT_MESSAGE_HUM };
    firstOnly = false;
    teamMemberIdToNameMap = {};
    bMyTeamInvChk = false;
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
    async doInit() {
        try {
            this.isLoading = true;

            const data = await getInitialLoad();

            let selectedView = [];
            this.allView = data.queueViewList;
            this.currentProfile = data.currentProfile;
            this.sPharmacyUser = data.sPharmacyUser;
            //get bswitch_JS_2578074
            this.bswitch_JS_2578074 = data.bswitch_JS2578074;
            //profie check 
            this.dispatchEvent(new CustomEvent('profilecheck', {
                detail: {
                    currentProfile: data.currentProfile,
                    totalCount: data.initialFetchCaseTasks.length,
                    sPharmacyUser: this.sPharmacyUser,
                }
            }));
            //get current user for accept selected unassigned check
            this.currentUserName = data.currentUserName;
            //work queue
            this.allWorkQueueList = data.workQueueList;
            this.filters.push({ key: 'Work Queue', value: this.getListvalues(data.workQueueList), values1: [data.currentQueue == null ? '' : data.currentQueue], selectedUIvalues: [data.currentQueue == null ? '' : data.currentQueue], showSelectAll: true, allItemsSelectOnLoad: false, isMultipleSelection: true });
            this.filteredValues['Work Queue'] = data.currentQueue == null ? '' : Object.assign([], data.currentQueue.split(','));
            this.currentQueue = data.currentQueue;

            //view
            selectedView = data.queueViewList.filter(element => element.includes('(' + data.currentQueue + ')') || element.includes(CaseTaskAllOtherQV));
            
            if (selectedView.length > 0 && selectedView != null) {
                this.filters.push({ key: 'View', value: this.getListvalues(selectedView), values1: selectedView, selectedUIvalues: selectedView, showSelectAll: true, allItemsSelectOnLoad: true, isMultipleSelection: true });
                this.filteredValues['View'] = Object.assign([], selectedView);
                this.filteredValues['View'].unshift('All');
                //assgn all view for workqueue
                this.allPresentViewsToWorkQ = selectedView;
            }
            this.resultData = data.initialFetchCaseTasks;
            const messaage = {
                tableData: data.initialFetchCaseTasks,
                allPresentViewOnPanel: selectedView,
                sPharmacyUser: this.sPharmacyUser,
                currentProfile: data.currentProfile

            };
            publish(this.messageContext, CASE_CHANNEL, messaage);
            //profie check 

            //work items
            if (this.issupervisor) {
                this.filters.push({ key: 'Work Items', value: this.getListvalues(data.SelectedAssign), values1: data.SelectedAssign == null ? '' : data.SelectedAssign, selectedUIvalues: data.SelectedAssign == null ? '' : data.SelectedAssign, showSelectAll: true, allItemsSelectOnLoad: true, isMultipleSelection: true });
                this.filteredValues['Work Items'] = data.SelectedAssign == null ? '' : Object.assign([], data.SelectedAssign);
                this.filteredValues['Work Items'].unshift('All');
            }


            //Itemage itemAge
            if (this.issupervisor) {
                if (data.itemAge !== undefined) {
                    this.filters.push({ key: 'Item Age', value: this.getListvalues(data.itemAge), values1: data.itemAge == null ? '' : data.itemAge, selectedUIvalues: data.itemAge == null ? '' : data.itemAge, showSelectAll: true, allItemsSelectOnLoad: true, isMultipleSelection: true });
                    this.filteredValues['Item Age'] = data.itemAge == null ? '' : Object.assign([], data.itemAge);
                    this.filteredValues['Item Age'].unshift('All');
                }
            }
            //filter by cases selectedViews
            if (data.selectedView !== undefined) {
                this.filters.push({ key: 'Filter By', value: this.getListvalues(data.selectedView), values1: data.selectedView == null ? '' : data.selectedView, selectedUIvalues: data.selectedView == null ? '' : data.selectedView, showSelectAll: true, allItemsSelectOnLoad: true, isMultipleSelection: true });
                this.filteredValues['Filter By'] = data.selectedView == null ? '' : Object.assign([], data.selectedView);
                this.filteredValues['Filter By'].unshift('All');
            }
            //securityView select all is not there
            if (this.issupervisor) {
                if (data.securityView !== undefined) {
                    this.filters.push({ key: 'Security Group', value: this.getListvalues(data.securityView), values1: data.securityView == null ? '' : data.securityView, selectedUIvalues: data.securityView == null ? '' : data.securityView, showSelectAll: false, allItemsSelectOnLoad: false, isMultipleSelection: true });
                    this.filteredValues['Security Group'] = data.securityView == null ? '' : Object.assign([], data.securityView);
                }
            }
            //view My Team Members
            if (this.issupervisor && hasCRMS_684_Medicare_Customer_Service_Access) {
                if (data.myTeamMembers !== undefined) {
                    this.teamMemberIdToNameMap = data.myTeamMembers
                    this.filters.push({ key: 'Team Members', value: this.getTeamMembervalues(data.myTeamMembers), values1: data.myTeamMembers == null ? '' : Object.keys(data.myTeamMembers), selectedUIvalues: data.myTeamMembers == null ? '' : Object.values(data.myTeamMembers), showSelectAll: true, allItemsSelectOnLoad: true, isMultipleSelection: true });
                    this.filteredValues['Team Members'] = data.myTeamMembers == null ? '' : Object.keys(data.myTeamMembers);
                    if (this.filteredValues['Team Members'].length > 0) {
                        this.filteredValues['Team Members'].unshift('All');
                    }
                }
            }

            this._filter = [...this.filters];
            this.isLoading = false;
        } catch (error) {
            this.isLoading = false;
        }
    }

    //method to prepare label, value object format, format - value , label
    getListvalues(passedObj) {

        let queue = [];

        for (let key of passedObj) {
            const emp = Object.create(obj);
            emp.label = key;
            emp.value = key;
            queue.push(emp);
        }

        return queue;
    }
    //method to prepare label, value as [userId,username] format
    getTeamMembervalues(passedObj) {
        let queue = [];

        for (let key of Object.keys(passedObj)) {
            const emp = Object.create(obj);
            emp.label = passedObj[key];
            emp.value = key;
            queue.push(emp);
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
    showToast(title, message, variant) {
        const event = new ShowToastEvent({
            title: title,
            message: message,
            variant: variant,
            mode: 'dismissable'
        });
        this.dispatchEvent(event);
    }
    renderedCallback() {
        if (this.template.querySelector('div[data-color="Team Members"]') && !this.bMyTeamInvChk)
            this.template.querySelector('div[data-color="Team Members"]').classList.add('disableTeamMembers');
    }
    @api
    filteredValuesToParent() {
        const filterValueToParent = {
            allWorkQueueList: this.allWorkQueueList,
            lstWorkQ: this.filteredValues['Work Queue'],
            allQueueViewList: this.allPresentViewsToWorkQ,
            lstView: this.bMyTeamInvChk ? null : this.filteredValues['View'],
            lstFilterBy: this.filteredValues['Filter By'],
            lstWorkItems: this.bMyTeamInvChk ? null : (this.issupervisor ? this.filteredValues['Work Items'] : 'All'),
            lstSecurityGrp: this.issupervisor ? this.filteredValues['Security Group'] : 'All',
            lstItemAge: this.issupervisor ? this.filteredValues['Item Age'] : null,
            lstTeamMembers: this.bMyTeamInvChk ? this.filteredValues['Team Members'] : null,
            currentUserName: this.currentUserName

        };
        return filterValueToParent;
    }
    handleInsideFilterClick(event) {
        event.stopPropagation();
        return false;
    }

    handleSelectAll(event) {
        let found = [];
        this.isFiltered = true;

        found = this._filter.filter((element) => element.key === event.target.dataset.id)[0];
        found.values1 = [];
        found.selectedUIvalues = [];
        found.value.forEach(element => {
            if (found.hasOwnProperty('values1') && found.hasOwnProperty('selectedUIvalues')) {
                if (event.detail.checked) {
                    found.values1.push(element.value);
                    if (event.target.dataset.id === 'Team Members') {
                        found.selectedUIvalues.push(this.teamMemberIdToNameMap[element.value]);
                    }
                    else {
                        found.selectedUIvalues.push(element.value);
                    }

                }
                else {
                    found.values1 = [''];
                    found.selectedUIvalues = [''];
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
        else if(event.target.dataset.id === 'View' && !event.detail.checked)
            this.filteredValues[event.target.dataset.id] = [''];
        else if (!event.detail.checked)
            this.filteredValues[event.target.dataset.id].shift('All');

        //disable applyPull work button
        this.disableApplynPullButton()

    }

    disableApplynPullButton() {
        // disable apply button  rectify on permission set
       
        if (!this.bMyTeamInvChk && (this.filteredValues['Filter By'].length > 0 && this.filteredValues["Filter By"][0] !== '') &&
                (this.filteredValues['View'].length > 0 && this.filteredValues["View"][0] !== '') &&
                (this.filteredValues['Work Queue'].length > 0 && this.filteredValues["Work Queue"][0] !== '') &&
                (this.filteredValues['Security Group'].length > 0 && this.filteredValues["Security Group"][0] !== '') &&
                (this.filteredValues['Work Items'].length > 0 && this.filteredValues["Work Items"][0] !== '')
            ) {
                this.isApplyPullButtonDisable = false;
            }
        else if (this.bMyTeamInvChk && (this.filteredValues['Team Members'].length > 0 && this.filteredValues["Team Members"][0] !== '') &&
                (this.filteredValues['Filter By'].length > 0 && this.filteredValues["Filter By"][0] !== '') &&
                (this.filteredValues['Work Queue'].length > 0 && this.filteredValues["Work Queue"][0] !== '') &&
                (this.filteredValues['Security Group'].length > 0 && this.filteredValues["Security Group"][0] !== '')
            ) {
                this.isApplyPullButtonDisable = false;
            }
            else {
                this.isApplyPullButtonDisable = true;
            }
        

    }
    handleApplyclick(event) {
        this.isLoading = true;
        this.outsideFilterClick(event);
        applyFilter({
            bPerformFilter: true,
            allWorkQueueList: this.allWorkQueueList,
            lstWorkQ: this.filteredValues['Work Queue'],
            allQueueViewList: this.allPresentViewsToWorkQ,
            lstView: this.bMyTeamInvChk ? null : this.filteredValues['View'],
            lstFilterBy: this.filteredValues['Filter By'],
            lstWorkItems: this.bMyTeamInvChk ? null : (this.issupervisor ? this.filteredValues['Work Items'] : 'All'),
            lstSecurityGrp: this.issupervisor ? this.filteredValues['Security Group'] : 'All',
            lstItemAge: this.issupervisor ? this.filteredValues['Item Age'] : null,
            lstTeamMembers: this.bMyTeamInvChk ? this.filteredValues['Team Members'] : null,
        })
            .then((result) => {
                if (result.length != null) {
                    this.resultData = result
                    // Error for CASELIMIT_MESSAGE_HUM
                    if (result.length > 3500) {
                        this.showToast('Error', this.label.CASELIMIT_MESSAGE_HUM, 'error');
                    }
                    //enable change case owner button if rows selected > 1 && security group is one   
                    if (this.issupervisor && this.filteredValues['Security Group'] !== null && this.filteredValues['Security Group'].length === 1) {
                        this.filterFlag = true;
                    } else
                        this.filterFlag = false;

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
                            filterFlag: this.filterFlag,
                            sSecurityGroup: this.filteredValues['Security Group']
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

            if (event.target.dataset.id === 'Team Members') {
                //to display username on Team Member filter UI
                let uiValue = [];
                this.value.forEach(element => {
                    uiValue.push(this.teamMemberIdToNameMap[element]);
                });
                this._filter.filter(fil => fil.key === event.target.dataset.id)[0].selectedUIvalues = uiValue;
            }
            else {
                //for other filters show names as they already are in list
                this._filter.filter(fil => fil.key === event.target.dataset.id)[0].selectedUIvalues = this.value;
            }
            //select all check if all are selected
            if (this._filter.filter(fil => fil.key === event.target.dataset.id)[0].showSelectAll) {
                //to uncheck select all
                if (this.template.querySelector('lightning-input[data-id="' + event.target.dataset.id + '"]').checked)
                    this.template.querySelector('lightning-input[data-id="' + event.target.dataset.id + '"]').checked = false;
                //check if all element present, mark select all 

                let arr2 = [];
                arr2 = this.filters.filter(fil => fil.key === event.target.dataset.id)[0].value;

                const containsAll = arr2.every(element => {
                    return this.filteredValues[event.target.dataset.id].includes(element.value);
                });
                if (containsAll) {
                    this.template.querySelector('lightning-input[data-id="' + event.target.dataset.id + '"]').checked = true;
                    this.filteredValues[event.target.dataset.id].unshift('All');
                }
            }
        } else {

            this._filter.filter(fil => fil.key === event.target.dataset.id)[0].values1 = this.value;
            this.filteredValues[event.target.dataset.id] = this.value.split(',');
            this.closeOpenedFilters();

        }
        this.disableApplynPullButton();

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
        this._filter.filter(fil => fil.key === 'View')[0].selectedUIvalues = this.arr;
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

    handleResetFilter() {
        this._filter.forEach(element => {
            element.values1 = [];
            element.selectedUIvalues = [];
        });
        this.isFiltered = false;
    }


    handleFilterItemClick(event) {
        let selected = event.target.dataset.id;
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

        if (selected === 'Item Age' || selected === 'Security Group' || selected === 'Filter By') {
            const topDiv = this.template.querySelector('div[data-id="scrollToMe"]').getBoundingClientRect().top;
            this.template.querySelector('div[data-id="scroller"').scrollTop = topDiv;

        }
        window.scrollTo({
            top: 500,
            behavior: 'smooth'
        });


    }
    onFilterClick(event) {
        event.stopPropagation();
        this.closeOpenedFilters();
        return false;
    }
    handleCrossClick(event) {
        event.stopPropagation();
        document.removeEventListener('click', this._handler);
        this.closeOpenedFilters();
        this.showFilter = false;
        this.showFilter_css = 'slds-dropdown-trigger_click';
        this.iconName = 'utility:filterList';
        this.i = 0;
    }
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
    closeOpenedFilters() {
        if (this.openedFilter) {
            let previouslyOppened = this.openedFilter;
            this.template.querySelector('div[data-id="' + previouslyOppened + '"]').classList.add('slds-hide');
            this.template.querySelector('div[data-color="' + previouslyOppened + '"]').classList.toggle('yellow-color');
            this.openedFilter = null;
        }
    }
    get isRCCPermissionSetUsr() {
        if (this.issupervisor && hasCRMS_684_Medicare_Customer_Service_Access)
            return true;
        else
            return false;
            
    }
    /*
    * Methos handle view my team team inventory logic
    */
    handleChangechkMyTeamInv(event) {
        this.isFiltered = true;
        if (this.filteredValues["Team Members"].length === 0)
            return;
        this.bMyTeamInvChk = event.target.checked;
        // enable/disable Team Members, Work Items and Views
        if (this.bMyTeamInvChk) {
            event.target.dataset.id = 'Team Members';
            this.template.querySelector('lightning-input[data-id="Team Members"]').checked= true;
            this.handleSelectAll(event);
            this.template.querySelector('div[data-color="Team Members"]').classList.remove('disableTeamMembers');
            this.template.querySelector('div[data-color="View"]').classList.add('disableTeamMembers');
            this.template.querySelector('div[data-color="Work Items"]').classList.add('disableTeamMembers');

        }
        else {
            this.template.querySelector('div[data-color="Team Members"]').classList.add('disableTeamMembers');
            this.template.querySelector('div[data-color="View"]').classList.remove('disableTeamMembers');
            this.template.querySelector('div[data-color="Work Items"]').classList.remove('disableTeamMembers');
        }
    }
}