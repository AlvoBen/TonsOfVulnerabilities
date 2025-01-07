/*
LWC Name        : Go365DetailsTab.html
Function        : LWC to display Go365 Details.

Modification Log:
* Developer Name                  Date                         Description
* Pallavi Shewale				  02/25/2022			       US-2557646 Original Version
* Swapnali Sonawane			      07/13/2023				   US-4812119
***************************************************************************************************************************/
import { LightningElement,track,api,wire } from 'lwc';
import getVitalityInformation from '@salesforce/apexContinuation/Go365Controller_LC_HUM.getVitalityInformation';
import getWebLink from '@salesforce/apex/Go365Controller_LC_HUM.getWebEmulatedURL';
import { invokeWorkspaceAPI } from 'c/workSpaceUtilityComponentHum';
import { NavigationMixin } from 'lightning/navigation';
import go365_InfoMessage from '@salesforce/label/c.Go365_InfoMessage'
import { getUniqueId } from 'c/crmUtilityHum';
import { getModel, getMedicareSet } from './layoutConfig';


export default class Go365DetailsTab extends  NavigationMixin(LightningElement) {
    @track data;
    @track activityData = [];
    @track columns = getModel();
    @track medicareSet = getMedicareSet();
    @track sortBy;
    @track sortDirection;
    isMedicareMedicaid = false;
    groupName;
    groupNumber;
    accID;
    groupID;
    Go365DetailResponse = {};
    memberPlanData;
    loaded = false;
    @track bActiveDataPresent = false;

    @wire(getWebLink) webEmulatedURLLink;

    label = {
        go365_InfoMessage
    }

    @api memberplan(memberdata) {
        this.memberPlanData = memberdata;
        if (this.memberPlanData) {
            this.groupName = this.memberPlanData?.Display_Group_Name__c ?? '';
            this.groupNumber = this.memberPlanData?.GroupNumber ?? '';
            this.accID = this.memberPlanData?.MemberId ?? '';
            this.groupID = this.memberPlanData?.Plan?.PayerId ?? '';
            this.isMedicareMedicaid = this.memberPlanData?.Product__c && this.memberPlanData?.Product_Type__c
                && this.memberPlanData?.Product__c?.toUpperCase() === 'MED' && this.medicareSet.includes(this.memberPlanData.Product_Type__c)
                ? true : false;
            if (this.accID) {
                this.getGo365Details(this.accID);
            }
        }
    }

    getGo365Details(accID) {
        getVitalityInformation({ accountID: accID })
            .then(result => {
                if (result && result != 'null' && result?.length > 0) {
                    let response = JSON.parse(result);
                    this.loaded = true;
                    this.getGo365HealthAssessment(response);
                    this.getActivityTableData(response);
                }
                this.loaded = true;
            }).catch(err => {
                this.loaded = true;
                console.log("Error occured - " + err);
            });
    }

    getGo365HealthAssessment(resultVitality) {
        this.Go365DetailResponse = {
            go365: resultVitality?.vitalityServiceResponse?.Response?.MemberVitalityData ? 'Yes' : 'No',
            go365Age: resultVitality?.vitalityServiceResponse?.Response?.MemberVitalityData?.VitalityAge ?? '',
            go365EntityId: resultVitality?.vitalityServiceResponse?.Response?.MemberVitalityData?.MemberEntityNumber ?? '',
            healthAssessment: resultVitality?.vitalityServiceResponse?.Response?.MemberVitalityData?.IsHraCompleted === 'false' ? 'No' : 'Yes',
            status: resultVitality?.vitalityServiceResponse?.Response?.MemberVitalityData?.VitalityStatuses?.EarnedStatus ?? '',
            webRegistered: resultVitality?.vitalityStateData?.webRegistered === 'true' ? 'Yes' : 'No',
            memGenKey: resultVitality?.vitalityStateData?.MemGenKey ?? '',
            effectiveDate: resultVitality?.vitalityServiceResponse?.Response?.MemberVitalityData ? 'Yes' : '',
            myHumanaLastLogindate: resultVitality?.vitalityStateData?.lastLoginDate?.length >= 10
                ? resultVitality?.vitalityStateData?.lastLoginDate?.substr(0, resultVitality?.lastLoginDate?.indexOf(' ')) : '',
            bucks: resultVitality?.vitalityServiceResponse?.Response?.MemberVitalityData?.VitalityBucksBalances?.SpendableBucksBalance ?? '',
            points: resultVitality?.vitalityServiceResponse?.Response?.MemberVitalityData?.VitalityBucksBalances?.RewardType !== 'USDollars'
                ? (resultVitality?.vitalityServiceResponse?.Response?.MemberVitalityData?.VitalityBucksBalances?.StatusBucksBalance ?? '') : null
        }
    }


    getActivityTableData(resultVitality) {
        let lstResult;
        this.activityData = [];
        if (resultVitality && resultVitality?.vitalityServiceResponse && resultVitality?.vitalityServiceResponse?.Response) {
            lstResult = resultVitality?.vitalityServiceResponse?.Response?.MemberVitalityData?.MemberGoals?.MemberGoal ?? null;
            if (lstResult != null && Array.isArray(lstResult) && lstResult?.length > 0) {
                lstResult.forEach(k => {
                    this.activityData.push({
                        id: getUniqueId(),
                        activity: k?.GoalName ?? '',
                        activityStatus: k?.Status ?? '',
                        points: k?.GoalPointValue ?? '',
                        days: k?.DaysUntillExpiration ?? '',
                        endDate: k?.ExternalEndDate ?? ''
                    })
                });
            }
            if (this.activityData && Array.isArray(this.activityData) && this.activityData?.length > 0) {
                this.bActiveDataPresent = true;
                this.sortData('activity', 'desc');
            }
        }
    }

    openWebEmulate() {
        if (this.webEmulatedURLLink?.data && this.Go365DetailResponse?.memGenKey) {
            window.open(this.webEmulatedURLLink.data + '?MemberGenKey=' + this.Go365DetailResponse.memGenKey);
        }
    }

    handleSortData(event) {
        this.sortBy = event.detail.fieldName;
        this.sortDirection = event.detail.sortDirection;
        this.sortData(event.detail.fieldName, event.detail.sortDirection);
    }

    sortData(fieldname, direction) {
        let cloneData = [...this.activityData];
        let keyValue = (a) => {
            return a[fieldname];
        };
        let isReverse = direction === 'asc' ? 1 : -1;
        cloneData.sort((x, y) => {
            x = keyValue(x) ? keyValue(x) : '';
            y = keyValue(y) ? keyValue(y) : '';
            return isReverse * ((x > y) - (y > x));
        });
        this.activityData = cloneData;
    }

    renderedCallback() {
        this.updateTabName();
    }

    async updateTabName() {
        if (await invokeWorkspaceAPI('isConsoleNavigation')) {
            let focusedTab = await invokeWorkspaceAPI('getFocusedTabInfo');
            await invokeWorkspaceAPI('setTabIcon', {
                tabId: focusedTab.tabId,
                icon: "standard:account"
            });
        }
    }

    async navigateToBusinessAccount() {
        let pageref = {
            type: 'standard__recordPage',
            attributes: {
                recordId: this.groupID,
                objectApiName: 'Account',
                actionName: 'view'
            },
        }
        let focusedTab = await invokeWorkspaceAPI('getFocusedTabInfo');
        if (await invokeWorkspaceAPI('isConsoleNavigation')) {
            await invokeWorkspaceAPI('openSubtab', {
                parentTabId: (focusedTab.hasOwnProperty('isSubtab') && focusedTab.isSubtab) ? focusedTab.parentTabId : focusedTab.tabId,
                pageReference: pageref
            });
        }
    }
}