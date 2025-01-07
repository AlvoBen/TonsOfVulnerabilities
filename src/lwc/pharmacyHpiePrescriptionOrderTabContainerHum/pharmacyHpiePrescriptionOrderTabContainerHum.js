/*
Function        : LWC to display demographics details .

Modification Log:
* Developer Name                  Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Nirmal Garg                   07/18/2022                 user story 4861950: T1PRJ0870026 - HPIE/CRM - C12 Mail Order Management - Pharmacy - "Details" Tab - Lightning - Patient APIUS-3975339 Change - RTI Icon Display Logic
* Atul Patil                    07/28/2023                  user story 4861950: T1PRJ0870026 - HPIE/CRM - C12 Mail Order Management - Pharmacy - "Details" Tab - Lightning - Patient API
* Atul Patil                    09/16/2023                  T1PRJ0870026 -MF27408 SF-TECH DF - 7995 - Lightning - Prescriptions data is not loading on Mail Order Pharmacy page
* Swapnali Sonawane             10/23/2023                 US - 5058187 Pharmacy Edit Order
* Jonathan Dickinson            02/27/2024                  User Story 5689879: T1PRJ1374973: DF8473; Lightning; Regression_Lightning - HPIE prescription data is not reflecting in Testfull
* Jonathan Dickinson            03/01/2024                  User Story 5058187: T1PRJ1295995 - (T1PRJ0870026)- MF 27409 HPIE/CRM SF - Tech - C12 Mail Order Management - Pharmacy - "Prescriptions & Order summary" tab - "Edit Order'
* Jonathan Dickinson             02/29/2024                 User Story 4835317: T1PRJ0865978 - MF 27481 - C12; Mail Order Management; Pharmacy - Mail Order Pharmacy page gap- Member Consent (Lightning)
*****************************************************************************************************************************/
import { LightningElement, api, track } from 'lwc';
import { getPrescriptions } from 'c/pharmacyHPIEIntegrationHum';
import { getLocaleDate } from 'c/crmUtilityHum';
import { getModel } from './layoutConfig';
const PRIOR_MONTH = 18;
export default class PharmacyHpiePrescriptionOrderTabContainerHum extends LightningElement {
    @api enterpriseId;
    @api userId;
    @api organization;
    @api recordId;
    @api preferenceDetails;
    @api demographicsDetails;
    @api financeDetails;
    @api accData;

    @track startDate;
    @track endDate;
    @track iconsModel = getModel("icons");
    @track totalPrescriptions = [];
    @track noPrescriptions = false;

    @api setPreferenceDetails(data) {
        this.preferenceDetails = data;
        this.processPreferenceData();
    }

    @api setDemographicsDetails(data) {
        this.demographicsDetails = data;
        this.processDemographicsData();
    }

    @api setFinanceDetails(data) {
        this.financeDetails = data;
        this.processFinanceDetails();
    }

    processDemographicsData() {
        this.passDataToChildComponents([{
            name: 'prescriptions',
            data: 'demographics'
        }, {
            name: 'orders',
            data: 'demographics'
        }])
    }

    processPreferenceData() {
        this.passDataToChildComponents([{
            name: 'prescriptions',
            data: 'preference'
        }, {
            name: 'orders',
            data: 'preference'
        }])
    }

    processFinanceDetails() {
        this.passDataToChildComponents([{
            name: 'prescriptions',
            data: 'finance'
        }, {
            name: 'orders',
            data: 'finance'
        }])
    }

    passDataToChildComponents(components) {
        if (components && Array.isArray(components) && components?.length > 0) {
            components.forEach(k => {
                switch (k?.name?.toLowerCase()) {
                    case 'prescriptions':
                        switch (k?.data?.toLowerCase()) {
                            case 'preference':
                                if (this.template.querySelector('c-pharmacy-hpie-prescriptions-hum') != null) {
                                    this.template.querySelector('c-pharmacy-hpie-prescriptions-hum').setPreferenceDetails(this.preferenceDetails);
                                }
                                break;
                            case 'demographics':
                                if (this.template.querySelector('c-pharmacy-hpie-prescriptions-hum') != null) {
                                    this.template.querySelector('c-pharmacy-hpie-prescriptions-hum').setDemographicsDetails(this.demographicsDetails);
                                }
                                break;
                            case 'finance':
                                if (this.template.querySelector('c-pharmacy-hpie-prescriptions-hum') != null) {
                                    this.template.querySelector('c-pharmacy-hpie-prescriptions-hum').setFinanceDetails(this.financeDetails);
                                }
                                break;
                        }
                        break;
                    case 'orders':
                        switch (k?.data?.toLowerCase()) {
                            case 'preference':
                                if (this.template.querySelector('c-pharmacy-hpie-orders-hum') != null) {
                                    this.template.querySelector('c-pharmacy-hpie-orders-hum').setPreferenceDetails(this.preferenceDetails);
                                }
                                break;
                            case 'demographics':
                                if (this.template.querySelector('c-pharmacy-hpie-orders-hum') != null) {
                                    this.template.querySelector('c-pharmacy-hpie-orders-hum').setDemographicsDetails(this.demographicsDetails);
                                }
                                break;
                            case 'finance':
                                if (this.template.querySelector('c-pharmacy-hpie-orders-hum') != null) {
                                    this.template.querySelector('c-pharmacy-hpie-orders-hum').setFinanceDetails(this.financeDetails);
                                }
                                break;
                        }
                        break;
                }
            })
        }
    }

    connectedCallback() {
        this.getPrescriptionData();
        this.processPreferenceData();
        this.processDemographicsData();
        this.processFinanceDetails();
    }

    handlePrescRefresh() {
        this.getPrescriptionData();
    }

    setPrescriptionStartEndDate() {
        let startdate = new Date();
        startdate = new Date(startdate.toUTCString());
        startdate.setUTCMonth(startdate.getUTCMonth() - PRIOR_MONTH);
        startdate.setUTCDate(startdate.getUTCDate() + 1);
        let enddate = new Date();
        enddate = new Date(enddate.toUTCString());

        let formatOptions = {
            year: "numeric",
            month: "2-digit",
            day: "2-digit"
        };
        startdate = new Intl.DateTimeFormat('en-US', formatOptions).format(new Date(startdate));
        enddate = new Intl.DateTimeFormat('en-US', formatOptions).format(new Date(enddate));
        
        this.startDate = `${startdate.split('/')[2]}-${startdate.split('/')[0]}-${startdate.split('/')[1]}`;
        this.endDate = `${enddate.split('/')[2]}-${enddate.split('/')[0]}-${enddate.split('/')[1]}`;
    }

    getPrescriptionData() {
        this.setPrescriptionStartEndDate();
        getPrescriptions(this.enterpriseId, this.userId, this.organization ?? 'HUMANA', this.startDate, this.endDate)
            .then(result => {
                
                this.noPrescriptions = result?.description?.toLowerCase() === 'prescriptions not found' ? true : false;
                this.totalPrescriptions = result?.prescriptions ?? [];
                this.addIcon();
            }).then(() => {
                if (this.template.querySelector('c-pharmacy-hpie-prescriptions-hum') != null) {
                    this.template.querySelector('c-pharmacy-hpie-prescriptions-hum').setPrescriptions(this.totalPrescriptions);
                }
                if (this.template.querySelector('c-pharmacy-hpie-orders-hum') != null) {
                    this.template.querySelector('c-pharmacy-hpie-orders-hum').setPrescriptions(this.totalPrescriptions);
                }
            }).catch(error => {
                console.log(error);
                if (this.template.querySelector('c-pharmacy-hpie-prescriptions-hum') != null) {
                    if(!this.noPrescriptions ){
                        this.template.querySelector('c-pharmacy-hpie-prescriptions-hum').displayError();
                    }else if(this.noPrescriptions){
                        this.template.querySelector('c-pharmacy-hpie-prescriptions-hum').displayNoPrescriptions();
                    }
                }
            })
    }

    addIcon() {
        this.totalPrescriptions = this.totalPrescriptions.map(item => ({
            ...item,
            icon: this.getIcon(item)
        }))
    }

    getIcon(iconname) {
        let Today = Date.parse(getLocaleDate(new Date()));
        let expdate = Date.parse(iconname?.expirationDate ?? new Date());
        let nextfilldate = Date.parse(iconname?.nextFillMaxDate ?? new Date());
        let nextfillmindate = Date.parse(iconname?.nextFillMinDate ?? new Date());
        let lastfilldate = Date.parse(iconname?.lastFillDate ?? new Date());
        if (iconname?.status?.description?.toUpperCase().indexOf('INACTIVE') >= 0) {
            return this.iconsModel.find(x => x.iconname === "INACTIVE");
        }
        else if (iconname?.status?.description?.toUpperCase().includes('EXPIRED') || iconname?.refillsRemaining <= 0 || expdate < Today) {
            if (nextfilldate <= Today) {
                return this.iconsModel.find(x => x.iconname === "greenclock");
            }
            else if (nextfilldate >= Today && nextfillmindate < Today) {
                return this.iconsModel.find(x => x.iconname === "blueclock");
            } else {
                return this.iconsModel.find(x => x.iconname === "clock");
            }
        }
        else {
            if (expdate > Today && nextfilldate < Today) {
                return this.iconsModel.find(x => x.iconname === "refillsremaining");
            } else if (nextfilldate > Today && nextfillmindate <= Today) {
                return this.iconsModel.find(x => x.iconname === "bluecalendar");
            } else {
                return this.iconsModel.find(x => x.iconname === "event");
            }
        }
    }
  
    handleCreditCardSave(){
        this.dispatchEvent(new CustomEvent("cardsave"));
    }

    handleCallDemographics(){
        this.dispatchEvent(new CustomEvent('calldemographics'));
    }

    handleCallPreference() {
        this.dispatchEvent(new CustomEvent('callpreference'));
    }

    handleLogNoteAdded() {
        this.dispatchEvent(new CustomEvent('lognoteadded'));
    }
}