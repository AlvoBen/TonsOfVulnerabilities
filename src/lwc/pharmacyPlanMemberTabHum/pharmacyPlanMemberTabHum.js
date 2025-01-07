/*******************************************************************************************************************************
LWC JS Name : PharmacyPlanMemberTabHum.js
Function    : This LWC component serves as input to Pharmacy Plan member Tab

Modification Log:
Developer Name                             Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Swapnali Sonawane                 	  08/04/2021                  DF-3494: Policy Member Id on Plan Member tab is not matching with Policy member Id on Search page
* Swapnali Sonawane                       09/02/2021                  UserStory:2508657 HP- Ability to add LIS and Other Insurance Details to the Plan Member card
* Swapnali Sonawane                       10/05/2021                  UserStory:2508657 DF#3833 Error message "A component error has occurred" is coming when click on Person Account tab from Plan Member tab on HP page
* Swapnali Sonawane						  11/02/2021                  DF-4006 :Icon is not displaying in Other Insurance subtab header, showing as loading
* Firoja Begam                            01/18/2021                  UserStory:2943289 Lightning - LIS (Low Income Subsidy) rebranding to "Extra Help"
* Jonathan Dickinson                      08/15/2022                  US-3248340
* Nirmal Garg                             11/10/2022                  DF-6592
*********************************************************************************************************************************/

import { LightningElement, wire, api, track } from 'lwc';
import { pageRef, invokeWorkspaceAPI, openSubTab } from 'c/workSpaceUtilityComponentHum';
import { NavigationMixin, CurrentPageReference } from 'lightning/navigation';
import { getLocaleDate, sortTable, hcConstants, getLabels } from 'c/crmUtilityHum';
import getLIS from '@salesforce/apexContinuation/MemberPlanEligibility_LC_HUM.callCIMedMultipleMemberService';
import getOI from '@salesforce/apexContinuation/Pharmacy_LC_HUM.getOIData';
import hasCRMS_205_CCSPDPPharmacyPilot from '@salesforce/customPermission/CRMS_205_CCSPDPPharmacyPilot';
import hasCRMS_206_CCSHumanaPharmacyAccess from '@salesforce/customPermission/CRMS_206_CCSHumanaPharmacyAccess';
import { getAllMemberPlans } from 'c/genericMemberPlanDetails';
export default class PharmacyPlanMemberTabHum extends NavigationMixin(LightningElement)
{
    @api recId;
    @api entId;
    @track openModal = false;
    @track currentPageReference = null;
    @track urlStateParameters = null;
    @track accID;
    @track iCount = 0;
    @track isMorePolicy = false;
    @track isPolicy = true;
    @track memName;
    @track oViewAllParams = {};
    @track nameOfScreen = "accountdetailpolicy";
    @track title = "Policies";
    @track recordID;
    @track effDate;
    @track endDate;
    @track status;
    @track product;
    @track policyList;
    @track isOIHyperlink;
    @track oiValue;
    @track isLISHyperlink;
    @track lisEndDate;
    @track lisEffectiveDate;
    @track lisLevel;
    @track lisEligible;
    @track labels = getLabels();
    @track enterpriceId;
    @track platformType;
    @track userId;
    @track userProfile;
    @track planUnavailable = true;

    connectedCallback() {
        this.accID = this.recId;
        this.enterpriceId = this.entId;
        this.getPolicyDetails();
        this.oViewAllParams = {
            sRecordTypeName: 'Member',
            sRecordId: this.accID,
            calledfrom: 'Pharmacy'
        }
    }

    @wire(CurrentPageReference)
    getStateParameters(currentPageReference) {
        if (currentPageReference) {
            this.urlStateParameters = currentPageReference.state;
            this.setParametersBasedOnUrl();
        }
    }

    setParametersBasedOnUrl() {
        this.accID = this.urlStateParameters?.c__AccountID ?? null;
        this.recordID = this.urlStateParameters?.c__AccountID ?? null
        this.entId = this.urlStateParameters?.c__enterpriceID ?? '';
        this.userId = atob(this.urlStateParameters?.c__userId) ?? '';
        this.userProfile = atob(this.urlStateParameters?.c__userProfile).replace('_', ' ') ?? '';
        this.getPolicyDetails();
    }

    async navigateToPolicyPage() {
        let pageref = {
            type: 'standard__recordPage',
            attributes: {
                recordId: this.recordID,
                objectApiName: 'MemberPlan',
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

    getPolicyDetails() {
        getAllMemberPlans(this.accID).then(result => {
            if (result && Array.isArray(result) && result?.length > 0) {
                this.isPolicy = true;
                this.isMorePolicy = result?.length > 1 ? true : false;
                this.recordID = result[0].Id;
                this.memName = result[0].Name;
                this.effDate = result[0].EffectiveFrom ? getLocaleDate(result[0].EffectiveFrom) : '';
                this.endDate = result[0].EffectiveTo ? getLocaleDate(result[0].EffectiveTo) : '';
                this.status = result[0].Member_Coverage_Status__c;
                this.product = result[0].Product__c;
                this.platformType = result[0].Policy_Platform__c;
                this.planCheck();
                this.getLISData();
                this.getOIData();
            } else {
                this.isPolicy = false;
            }
        }).catch(error => {
            console.log(error);
        })
    }

    planCheck() {
        if (this.userProfile) {
            this.planUnavailable = this.userProfile === 'Humana Pharmacy Specialist'
                || ((this.userProfile === 'Customer Care Specialist'
                    || this.userProfile === 'Customer Care Supervisor')
                    && (hasCRMS_205_CCSPDPPharmacyPilot || hasCRMS_206_CCSHumanaPharmacyAccess)) ?
                this.product?.toUpperCase() != 'MED' ? true : false : true;
        }
    }

    getOIData() {
        getOI({ sMemberPlanId: this.recordID, sEnterpriceId: this.enterpriceId }).then(result => {
            if (result) {
                this.oiValue = result.validOIWrapperList ? hcConstants.YES : hcConstants.NO;
                this.isOIHyperlink = result.validOIWrapperList ? true : false;
            }
        }).catch(error => {
            console.log(error);
        })
    }


    getLISData() {
        getLIS({ memberPlanId: this.recordID }).then(result => {
            if (result) {
                if (result.EligibilityServiceResponse != undefined || result.EligibilityServiceResponse != null) {
                    const aData = Object.entries(result.EligibilityServiceResponse);
                    aData.forEach(item => {
                        if (item) {
                            if (item[0] === "ExtraHelp") {   //  set fields for LIS
                                this.lisEligible = item[1].Eligible;
                                this.lisEffectiveDate = item[1].EffectiveDate;
                                this.lisEndDate = item[1].EndDate;
                                this.lisLevel = item[1].Level;
                                if (this.lisEligible === hcConstants.YES) {
                                    this.isLISHyperlink = true;
                                } else {
                                    this.isLISHyperlink = false;
                                }
                            }
                        }
                    });
                }
            }
        }).catch(error => {
            console.log(error);
        })
    }

    /**
     * Added hyperlinks to navigate to on click of LIS values
     * @param {*} event
     */
    openLISLink(event) {
        const eligibility = event.currentTarget.dataset.val;
        if (eligibility === hcConstants.YES) {
            window.open(hcConstants.LIS_HELP_PAGE);
        }
    }


    onViewAllClick(evnt) {
        this.hitApi({
            nameOfScreen: this.nameOfScreen,
            title: this.title,
            oParams: {
                ...this.oViewAllParams
            },
        });
    }

    /**
    * Handles hyperlink click
    * @param {*} evnt
    */
    onOIHyperLinkClick(evnt) {
        this.OnOpenTabRequest(this.labels.otherInsuranceDetails_Hum, hcConstants.OTHER_INSURANCE);
    }

    /**
     *
     * @param {*} title
     * @param {*} type
     */
    OnOpenTabRequest(title, type) {
        let sIcon = 'standard:account';
        openSubTab({
            config: {
                recordId: this.recordID,
                type,
                policyMemberId: this.memName,
                platformType: this.platformType,
                enterpriceId: this.enterpriceId
            },
            title: `${title} : ${this.memName}`,
            icon: sIcon,
        }, 'pharmacyOtherInsuranceComponentHum', this);
    }

    async hitApi(compDetails, auraName = 'viewAllComponentHum') {
        const me = this;
        let pageReference = pageRef(compDetails, auraName);
        let subTabDetail;
        let tabName = '';
        if (await invokeWorkspaceAPI('isConsoleNavigation')) {
            let focusedTab = await invokeWorkspaceAPI('getFocusedTabInfo');
            if (focusedTab && focusedTab.subtabs && focusedTab.subtabs.length > 0) {
                focusedTab.subtabs.forEach(item => {
                    if (item.customTitle === compDetails.title) {
                        invokeWorkspaceAPI('openTab', {
                            url: item.url
                        });
                        tabName = item.customTitle;
                    }
                });
            }
            if (focusedTab.isSubtab) {
                try {
                    tabName = await this.getTabDetailsFromSubtab(focusedTab, compDetails);
                } catch (error) {
                    tabName = '';
                }
            }
            if (focusedTab && tabName !== compDetails.title) {
                subTabDetail = await invokeWorkspaceAPI('openSubtab', {
                    parentTabId: (focusedTab.hasOwnProperty('isSubtab') && focusedTab.isSubtab) ? focusedTab.parentTabId : focusedTab.tabId,
                    pageReference: pageReference
                });
                await invokeWorkspaceAPI('setTabLabel', {
                    tabId: subTabDetail,
                    label: compDetails.title
                });
                await invokeWorkspaceAPI('setTabIcon', {
                    tabId: subTabDetail,
                    icon: "standard:account",
                    iconAlt: compDetails.nameOfScreen
                });
                let urls = await invokeWorkspaceAPI('getTabURL', {
                    tabId: subTabDetail
                });
                me.subTabDetails = urls;
                me.subTabId = me.subTabDetails;
            }
        }
    }

    renderedCallback() {
        this.planCheck();
    }
}