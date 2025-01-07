/*
Function        : LWC to display demographics details .

Modification Log:
* Developer Name                  Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Nirmal Garg                    07/18/2022                user story 4861950, 4861945
* Atul Patil                     07/28/2023                user story 4861950, 4861945
* Jonathan Dickinson			 09/22/2023				   User Story 5061288: T1PRJ0870026   MF 27406 HPIE/CRM - SF Tech- Mail Order Management - Pharmacy -  Details tab - Address section
*****************************************************************************************************************************/
import { LightningElement, api, track } from 'lwc';
export default class PharmacyHpieDetailsTabContainerHum extends LightningElement {

    @api financeDetails;
    @api profileDetails;
    @api demographicDetails;
    @api preferenceDetails;
    @api accountId;
    @api enterpriseId;
    @api userId;
    @api userProfile;
    refreshHandlerID;

    @api setProfileDetails(data) {
        this.profileDetails = data;
        this.passDataToChildComponents([{
            name: 'account',
            data: 'profile'
        }]);
    }

    @api setFinanceDetails(data) {
        this.financeDetails = data;
        this.passDataToChildComponents([{
            name: 'account',
            data: 'finance'
        }])
    }

    @api setDemographicDetails(data) {
        this.demographicDetails = data;
        this.passDataToChildComponents([{
            name: 'address',
            data: 'demographics'
        }, {
            name: 'account',
            data: 'demographics'
        }])
    }

    @api setPreferenceDetails(data) {
        this.preferenceDetails = data;
        this.passDataToChildComponents([{
            name: 'account',
            data: 'preference'
        }])
    }

    passDataToChildComponents(data) {
        if (data && Array.isArray(data) && data?.length > 0) {
            data.forEach(k => {
                switch (k?.name?.toLowerCase()) {
                    case "account":
                        switch (k?.data?.toLowerCase()) {
                            case "profile":
                                if (this.template.querySelector('c-pharmacy-hpie-account-details-hum') != null) {
                                    this.template.querySelector('c-pharmacy-hpie-account-details-hum').setProfileDetails(this.profileDetails)
                                }
                                break;
                            case 'finance':
                                if (this.template.querySelector('c-pharmacy-hpie-account-details-hum') != null) {
                                    this.template.querySelector('c-pharmacy-hpie-account-details-hum').setFinanceDetails(this.financeDetails)
                                }
                                break;
                            case 'demographics':
                                if (this.template.querySelector('c-pharmacy-hpie-account-details-hum') != null) {
                                    this.template.querySelector('c-pharmacy-hpie-account-details-hum').setDemographicDetails(this.demographicDetails)
                                }
                                break;
                            case 'preference':
                                if (this.template.querySelector('c-pharmacy-hpie-account-details-hum') != null) {
                                    this.template.querySelector('c-pharmacy-hpie-account-details-hum').setPreferenceDetails(this.preferenceDetails)
                                }
                                break;
                        }
                        break;
                    case "address":
                        switch (k?.data?.toLowerCase()) {
                            case "demographics":
                                if (this.template.querySelector('c-pharmacy-hpie-address-hum') != null) {
                                    this.template.querySelector('c-pharmacy-hpie-address-hum').setAddressDetails(this.demographicDetails)
                                }
                                break;
                        }
						break;
                }
            })
        }
    }

    handleAddressSaveSuccess(event) {
        const addressSaveEvent = new CustomEvent('addresssavesuccess', {
			detail: {refreshHighlightPanel: event.detail.refreshHighlightPanel}
		});

		this.dispatchEvent(addressSaveEvent);
    }
}