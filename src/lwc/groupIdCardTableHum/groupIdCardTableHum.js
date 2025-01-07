import { api, track, LightningElement, wire } from 'lwc';
import { NavigationMixin } from 'lightning/navigation';
import { ShowToastEvent } from 'lightning/platformShowToastEvent';
import isSandboxOrg from '@salesforce/apex/SearchUtilty_H_HUM.isSandboxOrgInfo';
import { encodeDefaultFieldValues } from 'lightning/pageReferenceUtils';
import { CurrentPageReference } from 'lightning/navigation';
import invokeGrpService from '@salesforce/apexContinuation/GroupIdCards_LC_HUM.invokeGrpService';
import invokeGetGroupMemberDetailRequest from '@salesforce/apexContinuation/GroupIdCards_LC_HUM.invokeGetGroupMemberDetailRequest';
import SystemModstamp from '@salesforce/schema/Account.SystemModstamp';

const columnTitle = [
    { label: 'Request Date', fieldName: 'CardRequestDate', type: 'date' },
    { label: 'Group ID', fieldName: 'GroupNbr', type: 'string' },
    { label: 'Originating System', fieldName: 'Platform', type: 'string' },
    { label: 'No. of Cards Requested', fieldName: 'CardCount', type: 'string' }];

export default class GroupIdCardTableHumRana extends LightningElement {
    @api recordId;
    @api groupSearchResponse = [];
    @track finalGroupSearchResponse = [];
    @api GroupMemberLevelSearchDetail;
    @api finalGroupMemberLevelSearchDetail;
    @api rowAccordionClick = false;
    @api activeSections = [];
    @api rowExpand;
    @track iCount = 0;
    @track finalResponse = [];
    @track isResults = true;
    @track iDetailCount = 0;
    @track isDetailResults = true;
    @track idetailcountmax = false;
	@track handleFilterSearch = false;
    @api accordionIndex;
    @api actionName;
    @api closeToastNotification = false;
    renderTable = false;
    @api GroupNumber;
    @api AccountId;
    noRecordFlag = false;
    @track isShowMoreVisible = false;
    @track CurrentlyShowingRecords;
    @api totalNumberOfRecords;
    columns = columnTitle;
    data = [];
    @track SowmoreIndex = 1;
    @track TotalIdCardResult = [];
    @track stickyHeaderCss = "slds-m-bottom_x-small ";
    @track isDecending = true;
    openFilterPanel = false;
    statusFilter;
    @wire(CurrentPageReference)
    currentPageReference(pageRef) {
        this.pageRef = pageRef;
    }
    handleOpenFilter() {
        this.openFilterPanel = true;
        this.idetailcountmax = false;
    }

    closeFilter() {
        this.openFilterPanel = false;
        this.finalResponse.forEach(item => {
            item.bGrpFilter = false;
        })
        this.GroupMemberLevelSearchDetail = this.GroupMemberLevelSearchDetailForFilter;
    }

    @track GroupMemberLevelSearchDetailForFilter;

    get statusArrayForFilter() {
        let statusvalues = [];
        if (this.GroupMemberLevelSearchDetailForFilter) {
            this.GroupMemberLevelSearchDetailForFilter.forEach(element => {
                if (statusvalues.find((o) => o.label === element.Status) === undefined || statusvalues.find((o) => o.label === element.Status) === 'undefined')
                    statusvalues.push({ label: element.Status, value: element.Status });
            });
        }
        return statusvalues;
    }
    @track groupfilterarr = [];


    handleFilterApplied(event) {
        var filterjson = event.detail;
        var filterArray = [];
        this.groupfilterarr = [];
		if (this.handleFilterSearch && filterjson != null && filterjson != undefined){
            this.callDetailsService(this.accordionIndex, this.actionName, this.finalResponse[this.accordionIndex].GroupNbr, this.finalResponse[this.accordionIndex].CardRequestDate,this.finalResponse[this.accordionIndex].Platform, this.pageRef.attributes.attributes.C__Id, filterjson.firstName, filterjson.lastName, filterjson.memberId);    
        }
        this.finalResponse.forEach((item, index) => {
            if (item.bGroupMemLvlSearch == true) {
                this.groupfilterarr.push({
                    respIndx: index,
                    val: JSON.parse(JSON.stringify(item.groupMemLvlSearch))
                });
                var filterArr = [];
                item.groupMemLvlSearch.forEach(element => {
                    let isValid = true;
                    if (filterjson) {
                        if (filterjson.firstName) {
                            if (element.FirstName.toUpperCase() == filterjson.firstName.toUpperCase() || !(element.FirstName.toUpperCase().indexOf(filterjson.firstName.toUpperCase()) == -1)) {
                                isValid = isValid && true;
                            } else {
                                isValid = isValid && false;
                            }
                        }
                        if (filterjson.lastName) {
                            if (element.LastName.toUpperCase() == filterjson.lastName.toUpperCase() || !(element.LastName.toUpperCase().indexOf(filterjson.lastName.toUpperCase()) == -1)) {
                                isValid = isValid && true;
                            } else {
                                isValid = isValid && false;
                            }
                        }
                        if (filterjson.status) {
                            this.statusFilter = filterjson.status;
                            if (element.Status == filterjson.status) {
                                isValid = isValid && true;
                            } else {
                                isValid = isValid && false;
                            }
                        }
                        if (filterjson.memberId) {
                            if (element.MemberId.toUpperCase() == filterjson.memberId.toUpperCase() || !(element.MemberId.toUpperCase().indexOf(filterjson.memberId.toUpperCase()) == -1)) {
                                isValid = isValid && true;
                            } else {
                                isValid = isValid && false;
                            }
                        }
                    }
                    if (isValid) {
                        item.bGrpFilter = true;
                        filterArr.push(element);
                    }
                });
                if (item.bGrpFilter == true) {
                    this.finalResponse[index].groupMemLvlSearchFilter = JSON.parse(JSON.stringify(filterArr));
                }
            }
        })

        if (this.GroupMemberLevelSearchDetailForFilter) {
            this.groupfilterarr.forEach(element => {
            });
            this.GroupMemberLevelSearchDetail = JSON.parse(JSON.stringify(filterArray));
        }
    }

    connectedCallback() {
        invokeGrpService({
            sGroupNumber: this.pageRef.attributes.attributes.C__GroupNumber,
            sAccountId: this.pageRef.attributes.attributes.C__Id
        })
            .then(result => {
                if (result != null && result.length != 0) {
                    this.groupSearchResponse = result;
                    if (this.groupSearchResponse.length > 20) {
                        this.isShowMoreVisible = true;
                    }
                    if (this.groupSearchResponse.length === 0) {
                        this.renderTable = false;
                    } else {
                        this.renderTable = true;
                    };
                    this.finalGroupSearchResponse = this.groupSearchResponse.map(item => {
                        return {
                            ...item,
                            Accordion: false,
                            iconName: 'utility:jump_to_right',
                            selected: false,
                            bGroupMemLvlSearch: false,
                            groupMemLvlSearch: [],
                            bGrpFilter: false,
                            groupMemLvlSearchFilter: [],
                        }
                    });
                    if (this.groupSearchResponse.length <= 20) {
                        for (var i = 0; i < this.groupSearchResponse.length; i++) {
                            this.finalResponse.push(this.finalGroupSearchResponse[i]);
                        }
                    } else {
                        for (var i = 0; i < 20; i++) {
                            this.finalResponse.push(this.finalGroupSearchResponse[i]);
                        }
                    }

                    this.iCount = this.finalGroupSearchResponse ? this.finalGroupSearchResponse.length : this.iCount;
                    this.isResults = this.groupSearchResponse.length > 0;
                    this.CurrentlyShowingRecords = this.finalResponse.length;
                    this.totalNumberOfRecords = this.finalGroupSearchResponse.length;
                }
                else {
                    this.renderTable = true;
                    this.noRecordFlag = true;
                    this.CurrentlyShowingRecords = '0';
                    this.totalNumberOfRecords = '0';
                }
            })
        // Sandbox orgs has extra toolbar, so this check is needed to add appropriate css
        isSandboxOrg()
            .then(hasSystemToolBar => {
                me.hasSystemToolBar = hasSystemToolBar;
                me.stickyHeaderCss += hasSystemToolBar ? 'sticky-header-system-bar' : 'sticky-header';
            })
            .catch(err => {
            });

    }

    //logic for accordion click

    /**
     * Description - this method is fired when accordion is clicked and will show the set of related fields on the
     *                basis of the service response.
     */
    navigateToIdCardDetails(event) {
        if (this.rowAccordionClick == false) {
            this.rowAccordionClick = true;
        }
        else {
            this.rowAccordionClick = false;
        };
        this.accordionIndex = event.currentTarget.getAttribute('data-att');
        this.actionName = event.currentTarget.getAttribute('data-actionname');
        if (this.actionName === "utility:jump_to_bottom") {
            this.finalGroupSearchResponse = this.expandAccordianRow(this.finalGroupSearchResponse, this.accordionIndex, this.actionName);
        }
        else {
            this.iDetailCount = this.finalResponse[this.accordionIndex].CardCount;
                if (this.iDetailCount > 99) {
                    this.idetailcountmax = true;
                    this.handleFilterSearch = true;
                }  
                else if (this.iDetailCount > 0 && this.iDetailCount < 100){
                    this.handleFilterSearch = false;
                    this.callDetailsService(this.accordionIndex, this.actionName, this.finalResponse[this.accordionIndex].GroupNbr, this.finalResponse[this.accordionIndex].CardRequestDate,this.finalResponse[this.accordionIndex].Platform, this.pageRef.attributes.attributes.C__Id, '', '', '' );
                }   
        }
    }

    async callDetailsService(accordionIndex, actionName ,  GroupNbr , CardRequestDate, Platform, C__Id, fName, lName, memId){
        const res = await invokeGetGroupMemberDetailRequest({
            sGroupNumber: GroupNbr,
            sCardReqDate: CardRequestDate, 
            sPlatform: Platform,
            sRecordId: C__Id,
            sFirstName: fName, 
            sLastName: lName, 
            sMemberId: memId
        })
            .then(result => {
                if (result != null) {
                    this.GroupMemberLevelSearchDetail = result.GroupMembeLevelSearchResponse.GroupMemberLevelSearchDetail;
                    this.finalResponse[accordionIndex].groupMemLvlSearch = result.GroupMembeLevelSearchResponse.GroupMemberLevelSearchDetail;
                    this.finalResponse[accordionIndex].bGroupMemLvlSearch = true;
                    this.isDetailResults = this.GroupMemberLevelSearchDetail.length > 0;

                this.iDetailCount = this.GroupMemberLevelSearchDetail ? this.GroupMemberLevelSearchDetail.length : this.iDetailCount;
                if (this.iDetailCount > 99) {
                    this.idetailcountmax = true;
                }

                this.GroupMemberLevelSearchDetailForFilter = this.GroupMemberLevelSearchDetail;
                this.finalGroupSearchResponse = this.expandAccordianRow(this.finalGroupSearchResponse, accordionIndex, actionName);
                let indx = accordionIndex;
                if (this.finalGroupSearchResponse[indx].iconName === 'utility:jump_to_bottom') {
                    this.accordianFields = this.finalGroupSearchResponse;
                }
            }
            }) 
            .catch(error => {
                console.log('catch error in GetGroupMemberDetail service call', error);
            });
    }

    expandAccordianRow(data, accordionIndex, actionName) {
        let newData;
        let index = accordionIndex;
        if (actionName === "utility:jump_to_right") {
            data[index].iconName = "utility:jump_to_bottom";
            data[index].selected = true;
            data[index].Accordion = true;
            newData = this.handleAccordianTypes(data, index, 'right');
            this.rowExpand = true;
        } else {
            data[index].iconName = "utility:jump_to_right";
            data[index].selected = false;
            data[index].Accordion = false;
            newData = data;
            this.rowExpand = false;
            this.idetailcountmax = false;
        }

        return newData;
    }

    handleAccordianTypes(data, ind, accordionTyp) {
        this.rowExpand = true;
        data.forEach((item, index) => {
            if (accordionTyp === 'right' && item.iconName === 'utility:jump_to_bottom' && index !== parseInt(ind)) {
                item.iconName = "utility:jump_to_bottom";
                item.selected = true;
                item.Accordion = true;
            }
        });

        return data;
    }

    handleToastClose(evnt) {
        this.idetailcountmax = false;
    }

    handleSectionToggle(event) {
        const openSections = event.detail.openSections;
    }

    showRecordLimitNotification() {
        const event = new ShowToastEvent({
            mode: 'sticky',
            message: `The ID Card request date contains more than 100 entries.Use the "Filter" to refine results.
                      Search/Filter by Member Id, First and/or Last Name                   {0}{1}`,
            messageData: [
                {
                    label: 'Update Filter',
                    action: this.handleOpenFilter,
                },
            ],
            variant: 'warning',
        });
        this.dispatchEvent(event);
    }

    //To show notification for the selected row
    ShowToastEventNotification(event) {

        showRecord(event.currentTarget.dataset.recid)
            .then(() => {
                this.dispatchEvent(
                    new ShowToastEvent({
                        title: 'Success',
                        message: 'Record Is showing',
                        variant: 'success',
                    }),
                );
                this.connectedCallback();

            })
            .catch(error => {
                this.dispatchEvent(
                    new ShowToastEvent({
                        title: 'Error',
                        message: error.message,
                        variant: 'error',
                    }),
                );
                this.connectedCallback();

            });
    }
    removeStatusFilter() {
        this.statusFilter = null;
        this.GroupMemberLevelSearchDetail = this.GroupMemberLevelSearchDetailForFilter;
    }

    handleShowMoreClick(event) {

        if (20 * (this.SowmoreIndex + 1) <= this.finalGroupSearchResponse.length) {
            for (var i = (20 * this.SowmoreIndex) + 1; i <= 20 * (this.SowmoreIndex + 1); i++) {
                this.finalResponse.push(this.finalGroupSearchResponse[i]);
            }
        }
        else {
            this.finalResponse = this.finalGroupSearchResponse;
            this.isShowMoreVisible = false;
        }
        if (this.isDecending === true) {
            this.finalResponse.sort(this.sortFunctionAsc);
        }
        else if (this.isDecending === false) {
            this.finalResponse.sort(this.sortFunctionDes);
        }
        this.SowmoreIndex = this.SowmoreIndex + 1;
        this.CurrentlyShowingRecords = this.finalResponse.length;
    }

    SortRequestDate() {
        if (this.isDecending === true) {
            this.isDecending = false;
            this.finalResponse.sort(this.sortFunctionDes);
        }
        else if (this.isDecending === false) {
            this.isDecending = true;
            this.finalResponse.sort(this.sortFunctionAsc);
        }
    }

    sortFunctionDes(a, b) {
        var dateA = new Date(a.CardRequestDate).getTime();
        var dateB = new Date(b.CardRequestDate).getTime();
        return dateA > dateB ? 1 : -1;
    }

    sortFunctionAsc(a, b) {
        var dateA = new Date(a.CardRequestDate).getTime();
        var dateB = new Date(b.CardRequestDate).getTime();
        return dateA < dateB ? 1 : -1;
    }
}