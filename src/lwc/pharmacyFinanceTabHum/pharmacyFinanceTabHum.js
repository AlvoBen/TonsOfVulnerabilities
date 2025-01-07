/*
JS Controller        : PharmacyMemberPaymentInformation
Version              : 1.0
Created On           : 11/9/2021
Function             : Component to display to pharmacy member payment information

Modification Log:
* Developer Name                      Date                         Description
* Swapnali Sonawane                   11/9/2021                   Original Version
* Nirmal Garg                         3/3/2022                    US-3001765-Invoice request flow
*------------------------------------------------------------------------------------------------------------------------------
*/
import { LightningElement, api, track } from 'lwc';

export default class PharmacyFinanceTabHum extends LightningElement {

	@api pharmacydemographicdetails;
	@api recordid;
	@api networkid;
	@api enterpriseid;
	@track shipAddress='';
	@track billingAddress='';
	@track permanentAddress='';
	@track emailaddress='';

	connectedCallback() {
		let demographicDetails;
		demographicDetails = this.pharmacydemographicdetails;
		if(demographicDetails && demographicDetails?.Email){
      this.emailaddress = demographicDetails.Email;
    }
		if (demographicDetails && demographicDetails?.Address) {
			let address = [];
			address = demographicDetails && demographicDetails?.Address &&
			Array.isArray(demographicDetails.Address) ? demographicDetails.Address : null;
			if (address && Array.isArray(address) && address.length > 0) {
				address.forEach(k => {
					switch (k.AddressType) {
						case "B":
						this.billingAddress = k.AddressLine1 + ", " + k.City + "," + k.StateCode + " " + k.ZipCode;
						break;
						case "S":
						this.shipAddress = k.AddressLine1 + ", " + k.City + "," + k.StateCode + " " + k.ZipCode;
						break;
						case "P":
						this.permanentAddress = k.AddressLine1 + ", " + k.City + "," + k.StateCode + " " + k.ZipCode;
					}
				})
			}
		}
	}
}