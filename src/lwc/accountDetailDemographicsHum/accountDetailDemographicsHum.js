/*******************************************************************************************************************************
LWC JS Name : AccountDetailDemographicsHum.js
Function    : This JS serves as controller to AccountDetailDemographicsHum.html. 

Modification Log: 
Developer Name           Date                          Description
*-----------------------------------------------------------------------------------------------------
* Joel George            03/05/2021                    initial version
* Mohan Kumar N          10/14/2021                    US: 2581508 Redesign

*********************************************************************************************************************************/
import { LightningElement, api, track, wire} from 'lwc';
import { getUserGroup } from 'c/crmUtilityHum';
import { getLabels } from 'c/customLabelsHum';
import pubSubHum from 'c/pubSubHum';
import { CurrentPageReference } from 'lightning/navigation';

export default class AccountDetailDemographicsHum extends LightningElement {
	@api recordId;
	@track oDetails = {};
	@track bShowDemographics = true;
	@track bShowWarningIcon = false;
	@track labels = getLabels();

	@wire(CurrentPageReference) pageRef;

	connectedCallback() {
		const { bGbo } = getUserGroup();
		if (bGbo) {
			this.bShowDemographics = false;
			return;
        }
		pubSubHum.registerListener('on-verify-demographics', this.loadData.bind(this), this);
	}

	/**
	 * Load data to the verifu demographics form
	 * @param {*} highlightsData 
	 */
	loadData(highlightsData) {
		const { iDaysSinceLastVerified, sLastVerifiedBy, sLastVerifiedOn } = highlightsData[0];
		this.bShowWarningIcon = iDaysSinceLastVerified && iDaysSinceLastVerified > 90;
		this.oDetails = {
			sLastVerifiedOn: sLastVerifiedOn,
			sLastVerifiedBy: sLastVerifiedBy,
			iDaysSinceLastVerified: iDaysSinceLastVerified
		};
	}
}