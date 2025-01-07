/* 
Function   : Controller for dualEligibilityDetailsHum.html

Modification Log:
* Developer Name                Date                       Description
* Mohan Kumar N                 07/08/2021                 US: 2364782- Dual eligibity screen
* Mohan Kumar N                 07/08/2021                 US: 2448831- Dual eligibity  formatting update
* Vishal Shinde                 21/08/2023                 US :T1PRJ0865978 -Pharmacy - Plan Member Page - Add/Remove Quick Links - (Lightning)
*/
import { LightningElement, api, track } from 'lwc';
import { getLabels, sortByDate, getLocaleDate } from 'c/crmUtilityHum';
import getDualStatus from '@salesforce/apexContinuation/MemberPlanDetail_LC_HUM.getDualStatusData';
import { costShareInfo, deemingPeriod, datesMap} from './dualEligibityModels';
export default class DualEligibilityDetailsHum extends LightningElement {
    @api config;
    @api recordId;
    @track aDeeminPeriods = [];
    @track aDualStatusLevel = [];
    @track bDataLoaded = false;
    @track costShareInfoModel;
    @track deemingPeriodModel;
    @track labels = getLabels();
    connectedCallback(){
        this.costShareInfoModel = costShareInfo;
        this.deemingPeriodModel = deemingPeriod;
        this.loadData();
    }

    /**
     * Load dual status ad deeming period data
     */
    async loadData(){
        const me = this;
        const { sEffectiveDateDeeming, sEffectiveDate, sEndDate, sEndDateDeeming} = datesMap;
        
        try {
            const oData = await getDualStatus({
                memberPlanId: this.config?.recordId ?? this.recordId //this.config.recordId
            });
            const { lstDeemingPeriods, lstDualStatusLevel, isOnSwitch} = oData;
            if(isOnSwitch){
                me.aDeeminPeriods = lstDeemingPeriods ? me.formatDates(sortByDate(lstDeemingPeriods, sEffectiveDateDeeming), sEffectiveDateDeeming, sEndDateDeeming) : [];
                me.aDualStatusLevel = lstDualStatusLevel ? me.formatDates(sortByDate(lstDualStatusLevel, sEffectiveDate),sEffectiveDate, sEndDate) : [];
                me.bDataLoaded = true;
            }           
        }
        catch(err){
            console.error('Error', err);
        }
    }

     /**
     * Format Dates in the MM/DD/YYYY
     * @param {*} aData array of objects
     * @param {*} startDate start date
     * @param {*} endDate end date
     */
    formatDates(aData, startDate, endDate) {
        return aData.map(item => ({
            ...item,
            [startDate]: getLocaleDate(item[startDate]),
            [endDate]: getLocaleDate(item[endDate])
        }));
    }
}