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
import { getUniqueId } from 'c/crmUtilityHum';
import {
	USPSRecomendationRequest,
	identity,
	addressRequest,
	addressTypeConfig
} from './pharmacyAddressHum_Helper';
import {
	addNewAddressDetails,
	updateAddress
} from 'c/pharmacyHPIEIntegrationHum';
import US2593758SwitchLabel from '@salesforce/label/c.US2593758SwitchLabel';
import { toastMsge } from 'c/crmUtilityHum';

const DEFAULT_OVERRIDE_REASON = 'MANUAL ADDRESS - ASSOCIATE USPS/UPS VALIDATED';

export default class PharmacyHpieAddressHum extends LightningElement {
	@api recordId;
	@api enterpriseId;
	@api userId;
	@api isLightning = false;

	//Basic data structures to manage address data and inputs
	@track addresses;
	@track addressMap = new Map();
	@track selectedAddress = new Map();
	@track uniqueAddressData = new Map();
	@track setOfAddressType = new Set();
	@track selectedOptions = [];
	@track addressToValidate = '';
	@track selectedAddressStr = '';
	@track trackAddressClickBy = '';
	@track _addressList = [];

	//Manages if ePost data is returned or not on inital load
	@track loaded = false;
	@track displayErrorMessage = false;
	@track ePostLoading = false;
	@track statusError = false;
	@track showUSPSSecondError = false;
	@track infoMessage = false;
	@track toggleAddresskey = '';
	@track warningMessage = '';

	//Used to keep track of which page should display
	@track bShowAddressSelection = true;
	@track bShowAddressChange = false;
	@track bShowAddAddress = false;
	@track bShowUSPSValidation = false;

	@track bIsEditButton = false;
	@track bShowOtherForm = false;
	@track bShowMappedAddresses = false;
	@track bEditBackClicked = false;
	@track bIsApplyButton = true;

	//Variables for the address edit form
	@track updateAddressLine1;
	@track updateAddressLine2;
	@track updateAddressCity;
	@track updateAddressState;
	@track updateAddressZip;

	//Variables for the add address form
	@track addAddressType;
	@track addAddress1;
	@track addAddress2;
	@track addAddressCity;
	@track addAddressState;
	@track addAddressZip;

	@track selectedAddressKey;
	@track addressToUpdate;
	@track existingcaseid;
	@track existingcasenumber;

	//Variable for check New Address or Address Change Apply button click
	@track checkAddAddress;

	@api setAddressDetails(data) {
		this.addresses = this.prepareAddressData(data?.Addresses ?? null);
		if (
			this.addresses &&
			Array.isArray(this.addresses) &&
			this.addresses?.length > 0
		) {
			this.addresses.forEach((k) => {
				this.addressMap.set(k?.uniqueKey ?? '', k);
				if (!this.uniqueAddressData.has(k.key.toString())) {
					let tempStr = k.addressLine1;
					if (k.addressLine1) {
						tempStr += ` ${k.addressLine2}`;
					}
					tempStr += `, ${k.city} ${k.stateCode} ${k.zipCode}`;
					this.uniqueAddressData.set(k.key.toString(), tempStr);
				}
			});
			this.displayErrorMessage = false;
			this.loaded = true;
			this.ePostLoading = false;
		} else {
			this.displayErrorMessage = true;
			this.loaded = true;
			this.ePostLoading = false;
		}
	}

	prepareAddressData(lstAddress) {
		if (lstAddress && Array.isArray(lstAddress) && lstAddress?.length > 0) {
			lstAddress = lstAddress.map((item) => ({
				...item,
				typeOfAddress: this.getAddressType(item?.z0type?.description),
				status: item?.active && item?.active === true ? true : false,
				uniqueKey: getUniqueId(),
				showToggle:
					item?.z0type?.description?.charAt(0)?.toUpperCase() !== 'A'
						? false
						: true,
				street: `${item?.addressLine1 ?? ''}${
					item?.addressLine2 && item?.addressLine2?.trim()?.length > 0
						? ` ${item?.addressLine2}`
						: ''
				}`,
				addressChecked: false
			}));
			lstAddress = this.sortAddressData(lstAddress);
			return this.sortAddressData2(lstAddress);
		}
		return null;
	}

	getAddressType(addressDesc) {
		return addressDesc === 'HOME' ? 'PERMANENT' : addressDesc;
	}

	sortAddressData(lstAddress) {
		if (lstAddress && Array.isArray(lstAddress) && lstAddress?.length > 0) {
			lstAddress.sort(function (a, b) {
				let type1 =
					a?.z0type?.description?.charAt(0)?.toUpperCase() ?? '';
				let type2 =
					b?.z0type?.description?.charAt(0)?.toUpperCase() ?? '';
				return type1 > type2 ? -1 : type1 < type2 ? 1 : 0;
			});
			return lstAddress;
		}
		return null;
	}

	sortAddressData2(lstAddress) {
		if (lstAddress && Array.isArray(lstAddress) && lstAddress?.length > 0) {
			lstAddress.sort(function (a, b) {
				let type1 =
					a?.z0type?.description?.charAt(0)?.toUpperCase() ?? '';
				let type2 =
					b?.z0type?.description?.charAt(0)?.toUpperCase() ?? '';
				return type1 === 'H' && type2 === 'B'
					? 1
					: type1 === 'B' && type2 === 'H'
					? -1
					: type1 > type2
					? -1
					: type1 < type2
					? 1
					: a?.status && a?.status !== b?.status
					? -1
					: b?.status && a?.status !== b?.status
					? 1
					: a?.city?.toUpperCase() > b?.city?.toUpperCase()
					? 1
					: a?.city?.toUpperCase() < b?.city?.toUpperCase()
					? -1
					: 0;
			});
			return lstAddress;
		}
		return null;
	}

	saveToEpost(addresses, calledFrom, elementID) {
		this.ePostLoading = true;

		const identityValues = new identity(
			this.enterpriseId,
			this.organization ?? 'HUMANA',
			this.userId
		);

		if (this.checkAddAddress === 'New Address') {
			const address = addresses[0];
			const addrType = addressTypeConfig.get(address.addressType);

			let uspsAddressRequest = new addressRequest(
				addrType.code,
				addrType.description,
				address.addressLine1,
				address.addressLine2 ?? '',
				address.city,
				address.stateCode,
				address.zipCode,
				this.overrideReason ?? DEFAULT_OVERRIDE_REASON,
				1,
				address.uspsValidate,
				true,
				-1
			);
			const request = JSON.stringify({
				...identityValues,
				...uspsAddressRequest
			});
			addNewAddressDetails(request)
				.then((data) => {
					if (data) {
						// only refresh the highlight panel for shipping address updates
						let highlightPanelRefresh =
							addrType.code === '11' ? true : false;
						this.handleAddressSaveSuccess(
							calledFrom,
							highlightPanelRefresh
						);
					} else {
						this.handleAddressSaveFailure(calledFrom, elementID);
					}
				})
				.catch((error) => {
					this.handleAddressSaveFailure(calledFrom, elementID);
					console.log('Error: ' + error.message);
				});
		} else if (this.checkAddAddress === 'Address Change') {
			let lstAddresses = [];

			if (
				addresses &&
				Array.isArray(addresses) &&
				addresses?.length > 0
			) {
				addresses.forEach((address) => {
					const addrType = addressTypeConfig.get(address.addressType);
					let uspsAddressRequest = new addressRequest(
						addrType.code,
						addrType.description,
						address.addressLine1,
						address.addressLine2 ?? '',
						address.city,
						address.stateCode,
						address.zipCode,
						this.overrideReason ?? DEFAULT_OVERRIDE_REASON,
						1,
						address.uspsValidate,
						true,
						this.selectedAddressKey === 'Add New Address'
							? -1
							: Number(this.selectedAddressKey)
					);

					lstAddresses.push(uspsAddressRequest);
				});

				const request = JSON.stringify({
					...identityValues,
					addresses: lstAddresses
				});
				updateAddress(request)
					.then((data) => {
						if (data) {
							// only refresh the highlight panel for shipping address updates
							let highlightPanelRefresh = false;
							if (
								Array.isArray(data.addresses) &&
								data.addresses.length > 0
							) {
								highlightPanelRefresh = data.addresses.some(
									(el) => el.z0type.code === '11'
								);
							}
							this.handleAddressSaveSuccess(
								calledFrom,
								highlightPanelRefresh
							);
						} else {
							this.handleAddressSaveFailure(
								calledFrom,
								elementID
							);
						}
					})
					.catch((error) => {
						this.handleAddressSaveFailure(calledFrom, elementID);
						console.log('Error: ' + error.message);
					});
			}
		} else if (this.checkAddAddress === 'Status Update') {
			let lstAddresses = [];
			let address = addresses[0];
			let uspsAddressRequest = new addressRequest(
				address.z0type.code,
				address.z0type.description,
				address.addressLine1,
				address.addressLine2 ?? '',
				address.city,
				address.stateCode,
				address.zipCode,
				address.overrideReason ?? DEFAULT_OVERRIDE_REASON,
				address.overrideReasonCode,
				address.uspsValidated,
				!address.active,
				address.key
			);

			lstAddresses.push(uspsAddressRequest);

			const request = JSON.stringify({
				...identityValues,
				addresses: lstAddresses
			});
			updateAddress(request)
				.then((data) => {
					if (data) {
						this.handleAddressSaveSuccess(calledFrom, false);
					} else {
						this.handleAddressSaveFailure(calledFrom, elementID);
					}
				})
				.catch((error) => {
					this.handleAddressSaveFailure(calledFrom, elementID);
					console.log('Error: ' + error.message);
				});
		}
	}

	handleAddressSaveSuccess(calledFrom, highlightPanelRefresh) {
		if (calledFrom === 'handleUSPSSaveAddresses') {
			this.displaySuccessNotification();
			this.generateCaseComment();
			this.resetAddressValues();
		}

		if (
			US2593758SwitchLabel.toUpperCase() === 'Y' &&
			calledFrom === 'handleUSPSSaveAddresses'
		) {
			this.displaySuccessNotification();
		}

		const addressSaveEvent = new CustomEvent('addresssavesuccess', {
			detail: { refreshHighlightPanel: highlightPanelRefresh }
		});

		this.dispatchEvent(addressSaveEvent);
	}

	handleAddressSaveFailure(calledFrom, elementID) {
		this.ePostLoading = false;
		if (calledFrom === 'handleUSPSSaveAddresses') {
			this.showUSPSSecondError = true;
		} else {
			this.statusError = true;
			this.undoSwitchToggle(elementID);
		}
	}

	generateCaseComment() {
		let sInitialAddress = '';
		let sAddressType = '';
		let hasInteraction = false;

		if (US2593758SwitchLabel.toUpperCase() === 'Y') {
			for (let address of this.selectedAddress.values()) {
				sInitialAddress = address.addressLine1;
				if (address.addressLine2) {
					sInitialAddress += ' ' + address.addressLine2;
				}
				sInitialAddress += `, ${address.city} ${address.stateCode} ${address.zipCode}`;
				sAddressType += address.typeOfAddress + ', ';
				address.fullAddress = sInitialAddress;
			}
			if (this.interactionId !== undefined && this.interactionId !== '')
				hasInteraction = true;

			sAddressType = sAddressType.substring(0, sAddressType.length - 2);

			if (this.bIsEditButton) {
				this.sFullAddressForEachType = '';
				if (hasInteraction) {
					if (this.selectedOptions.length < 2) {
						this.initialAddress = `${this.interactionType} contacted us to Update ${sAddressType} Address from ${sInitialAddress} to `;
					} else {
						this.initialAddress = `${this.interactionType} contacted us to Update ${sAddressType}\nfrom: `;

						for (let address of this.selectedAddress.values()) {
							if (address.AddressType !== '9') {
								this.sFullAddressForEachType +=
									address.typeOfAddress +
									', ' +
									address.fullAddress +
									'\n';
							}
						}
						this.initialAddress +=
							this.sFullAddressForEachType + 'to ';
					}
				} else {
					if (this.selectedOptions.length < 2) {
						this.initialAddress = `We were contacted to Update ${sAddressType} Address from ${sInitialAddress} to `;
					} else {
						this.initialAddress = `We were contacted to Update ${sAddressType}\nfrom: `;

						for (let address of this.selectedAddress.values()) {
							if (address.z0type.code !== '9') {
								this.sFullAddressForEachType += `${address.typeOfAddress}, ${address.fullAddress}\n`;
							}
						}
						this.initialAddress +=
							this.sFullAddressForEachType + 'to ';
					}
				}
			}
			let tempStr = this.addressToUpdate.AddressLine1;
			if (this.addressToUpdate.AddressLine2) {
				tempStr += ' ' + this.addressToUpdate.AddressLine2;
			}
			tempStr += `, ${this.addressToUpdate.City} ${this.addressToUpdate.StateCode} ${this.addressToUpdate.ZipCode}`;
			this.updatedAddressStr = tempStr;
			sAddressType = this.addressToUpdate.AddressTypeLiteral;
			if (this.bIsEditButton) {
				this.initialAddress += this.updatedAddressStr;
			} else {
				if (hasInteraction) {
					this.initialAddress = `${this.interactionType} contacted us to add ${this.newAddressLiteralType} address as ${this.updatedAddressStr}`;
				} else {
					this.initialAddress = `We were contacted to add ${this.newAddressLiteralType} address as ${this.updatedAddressStr}`;
				}
			}
			const uspsValidationComponent = this.template.querySelector(
				'c-usps-validation-hum'
			);
			if (uspsValidationComponent) {
				uspsValidationComponent.createAddressObject(
					this.initialAddress
				);
			}
		}
	}

	resetAddressValues() {
		this.selectedAddress = new Map();
		this.selectedOptions = [];
		this.addAddress2 = '';
		this.addAddressType = '';
		this.addAddress1 = '';
		this.addAddressCity = '';
		this.addAddressState = '';
		this.addAddressZip = '';
		this.addressToValidate = '';
		this.selectedAddressStr = '';
		this.selectedAddressKey = '';
		this.bShowOtherForm = false;
		this.bIsEditButton = false;
		this.bShowMappedAddresses = false;
		this.goToPage('addressSelection');
	}

	displaySuccessNotification() {
		toastMsge(
			'Success!',
			'Address has successfully changed for this Member',
			'success',
			'sticky'
		);
	}

	undoSwitchToggle(elementID) {
		let target = this.template.querySelector(`[data-id="${elementID}"]`);
		if (target.checked) {
			target.checked = false;
		} else {
			target.checked = true;
		}
	}

	handleShowButton(event) {
		let eventKey = event.target.dataset.checkboxid;
		let tempKey = '';
		let eventAddresstype = event.target.dataset.addresstype;

		// keep track of the value of addressChecked so that the value isn't reset when going back to addressSelection screen
		if (
			this.addresses.findIndex(
				(address) => address.uniqueKey === eventKey
			) >= 0
		) {
			this.addresses.find(
				(address) => address.uniqueKey === eventKey
			).addressChecked = event.target.checked;
		}

		if (event.target.checked === true) {
			for (let selectedKey of this.selectedAddress.keys()) {
				if (selectedKey.charAt(0) === eventAddresstype) {
					tempKey = selectedKey;
				}
			}
			if (tempKey !== '') {
				let target = this.template.querySelector(
					`[data-checkboxid="${tempKey}"]`
				);
				target.checked = false;
				this.selectedAddress.delete(tempKey);
			}
			this.selectedAddress.set(eventKey, this.addressMap.get(eventKey));
		} else {
			if (this.selectedAddress.has(eventKey)) {
				this.selectedAddress.delete(eventKey);
			}
		}
		if (this.selectedAddress.size > 0) {
			let setOfType = new Set();
			this.selectedAddress.forEach((data) => {
				setOfType.add(data.typeOfAddress);
			});
			this.setOfAddressType = setOfType;
			this.bIsEditButton = true;
		} else {
			this.bIsEditButton = false;
		}
	}

	handleAddressChange(event) {
		this.addressToValidate = event.detail.value;
		if (event.detail.value === 'Add New Address') {
			this.updateAddressLine1 = '';
			this.updateAddressLine2 = '';
			this.updateAddressCity = '';
			this.updateAddressState = '';
			this.updateAddressZip = '';
			this.bShowOtherForm = true;
			this.bShowMappedAddresses = false;
			this.bIsApplyButton = true;
			this.trackAddressClickBy = 'AddAddress';
		} else {
			this.bShowOtherForm = false;
			this.bShowMappedAddresses = true;
			this.selectedAddressKey = event.detail.value;
			this.selectedAddressStr = this.uniqueAddressData.get(
				event.detail.value
			);
			this.bIsApplyButton = false;
		}
	}

	addAddressClick() {
		this.goToPage('addAddress');
	}

	editAddressClick() {
		this.selectedOptions = [];

		for (let [key, address] of this.selectedAddress.entries()) {
			let temp_Obj = {
				label: address.typeOfAddress,
				name: key
			};
			this.selectedOptions.push(temp_Obj);
		}

		this.goToPage('editAddress');
	}

	handleRemove(event) {
		const name = event.target.name;
		this.selectedOptions = this.selectedOptions.filter(
			(item) => item.name !== name
		);
		if (this.selectedOptions.length === 0) {
			this.handleCancel();
		} else if (this.selectedAddress.has(name)) {
			this.selectedAddress.delete(name);

			if (
				this.addresses.findIndex(
					(address) => address.uniqueKey === name
				) >= 0
			) {
				this.addresses.find(
					(address) => address.uniqueKey === name
				).addressChecked = false;
			}
		}
	}

	resetCheckboxes() {
		this.addresses.forEach((address) => {
			address.addressChecked = false;
		});
	}

	handleCancel() {
		this.resetCheckboxes();
		this.selectedOptions = [];
		this.addAddressType = '';
		this.addAddress1 = '';
		this.addAddress2 = '';
		this.addAddressCity = '';
		this.addAddressState = '';
		this.addAddressZip = '';
		this.selectedAddress = new Map();
		this.addressToValidate = '';
		this.checkAddAddress = '';

		this.selectedAddressStr = '';
		this.selectedAddressKey = '';
		this.bShowOtherForm = false;
		this.bIsEditButton = false;
		this.bShowMappedAddresses = false;
		this.goToPage('addressSelection');
	}

	handleEditBack() {
		this.addressToValidate = '';
		this.selectedAddressStr = '';
		this.selectedAddressKey = '';
		this.bShowOtherForm = false;
		this.bShowMappedAddresses = false;
		this.bEditBackClicked = true;
		this.goToPage('addressSelection');
	}

	handleAddApply() {
		const isInputFilled = [
			...this.template.querySelectorAll('lightning-input')
		].reduce((validSoFar, inputField) => {
			inputField.reportValidity();
			return validSoFar && inputField.checkValidity();
		}, true);

		const isDropdownfilled = [
			...this.template.querySelectorAll('lightning-combobox')
		].reduce((validSoFar, inputField) => {
			inputField.reportValidity();
			return validSoFar && inputField.checkValidity();
		}, true);

		if (isInputFilled && isDropdownfilled) {
			this.checkAddAddress = 'New Address';
			this.addressToUpdate = new USPSRecomendationRequest(
				this.addAddress1,
				this.addAddress2,
				this.addAddressCity,
				this.addAddressState,
				this.addAddressZip
			);
			this.goToPage('uspsValidation');
		}
	}

	handleEditApply() {
		const isInputFilled = [
			...this.template.querySelectorAll('lightning-input')
		].reduce((validSoFar, inputField) => {
			inputField.reportValidity();
			return validSoFar && inputField.checkValidity();
		}, true);

		const isDropdownfilled = [
			...this.template.querySelectorAll('lightning-combobox')
		].reduce((validSoFar, inputField) => {
			inputField.reportValidity();
			return validSoFar && inputField.checkValidity();
		}, true);

		if (isInputFilled && isDropdownfilled) {
			this.checkAddAddress = 'Address Change';

			if (this.addressToValidate === 'Add New Address') {
				this.addressToUpdate = new USPSRecomendationRequest(
					this.updateAddressLine1,
					this.updateAddressLine2,
					this.updateAddressCity,
					this.updateAddressState,
					this.updateAddressZip
				);
			} else {
				for (let value of this.addressMap.values()) {
					if (value.key.toString() === this.addressToValidate) {
						this.addressToUpdate = new USPSRecomendationRequest(
							value.addressLine1,
							value.addressLine2,
							value.city,
							value.stateCode,
							value.zipCode
						);
						break;
					}
				}
			}

			this.goToPage('uspsValidation');
		}
	}

	//Used to control which page should display within the component
	goToPage(nameOfPage) {
		switch (String(nameOfPage)) {
			case 'addressSelection':
				this.bShowAddressChange = false;
				this.bShowAddAddress = false;
				this.bShowUSPSValidation = false;
				this.bShowAddressSelection = true;
				break;
			case 'editAddress':
				this.bShowAddressSelection = false;
				this.bShowAddAddress = false;
				this.bShowUSPSValidation = false;
				this.bShowAddressChange = true;
				break;
			case 'addAddress':
				this.bShowAddressSelection = false;
				this.bShowAddressChange = false;
				this.bShowUSPSValidation = false;
				this.bShowAddAddress = true;
				break;
			case 'uspsValidation':
				this.bShowAddressSelection = false;
				this.bShowAddressChange = false;
				this.bShowAddAddress = false;
				this.bShowUSPSValidation = true;
				break;
			default:
				this.bShowAddressChange = false;
				this.bShowAddAddress = false;
				this.bShowUSPSValidation = false;
				this.bShowAddressSelection = true;
		}
	}

	//Handlers for Add Address form
	handleaddAddressType(event) {
		this.addAddressType = event.target.value;

		this.selectedOptions = [];
		let temp_Obj = {
			label: event.target.options.find(
				(opt) => opt.value === event.detail.value
			).label,
			name: this.addAddressType
		};
		this.selectedOptions.push(temp_Obj);
	}

	handleaddAddress1(event) {
		this.addAddress1 = event.target.value;
		event.stopPropagation();
	}

	handleaddAddress2(event) {
		this.addAddress2 = event.target.value;
		event.stopPropagation();
	}

	handleaddCity(event) {
		let inputVal = event.target.value;
		event.target.value = inputVal.replace(/[0-9]/g, '');
		this.addAddressCity = event.target.value;
		event.stopPropagation();
	}

	handleaddState(event) {
		this.addAddressState = event.target.value;
	}

	handleZipChange(event) {
		let inputVal = event.target.value;
		if (!isFinite(inputVal)) {
			event.target.value = inputVal.toString().slice(0, -1);
		}
		this.addAddressZip = event.target.value;
	}

	get newApplyButton() {
		return !(
			this.addAddressState &&
			this.addAddressZip &&
			this.addAddressCity &&
			this.addAddress1 &&
			this.addAddressType
		);
	}

	handleZipChangeClear(event) {
		event.stopPropagation();
	}

	//Handlers for Edit Page form
	handleEditAddress1(event) {
		this.updateAddressLine1 = event.target.value;
		this.handlebIsApplyButtonEnable();
		event.stopPropagation();
	}

	handleEditAddress2(event) {
		this.updateAddressLine2 = event.target.value;
		event.stopPropagation();
	}

	handleEditCity(event) {
		let inputVal = event.target.value;
		event.target.value = inputVal.replace(/[0-9]/g, '');
		this.updateAddressCity = event.target.value;
		this.handlebIsApplyButtonEnable();
		event.stopPropagation();
	}

	handleEditState(event) {
		this.updateAddressState = event.target.value;
		this.handlebIsApplyButtonEnable();
	}

	handleEditZipChange(event) {
		let inputVal = event.target.value;
		if (!isFinite(inputVal)) {
			event.target.value = inputVal.toString().slice(0, -1);
		}
		this.updateAddressZip = event.target.value;
		this.handlebIsApplyButtonEnable();
	}

	handlebIsApplyButtonEnable() {
		this.bIsApplyButton = !(
			this.updateAddressState &&
			this.updateAddressZip &&
			this.updateAddressCity &&
			this.updateAddressLine1
		);
	}

	handleOtherCancel(event) {
		if (this.selectedAddressStr === '') {
			this.addressToValidate = '';
		}
		this.bShowOtherForm = false;
		if (this.trackAddressClickBy === 'ModifyAddress') {
			this.bIsApplyButton = false;
		} else if (this.trackAddressClickBy === 'AddAddress') {
			this.bIsApplyButton = true;
		}
	}

	handleOtherDone(event) {
		const isInputFilled = [
			...this.template.querySelectorAll('lightning-input')
		].reduce((validSoFar, inputField) => {
			inputField.reportValidity();
			return validSoFar && inputField.checkValidity();
		}, true);

		const isDropdownfilled = [
			...this.template.querySelectorAll('lightning-combobox')
		].reduce((validSoFar, inputField) => {
			inputField.reportValidity();
			return validSoFar && inputField.checkValidity();
		}, true);

		let otherStr = this.updateAddressLine1;
		if (this.updateAddressLine1 != '' && this.updateAddressLine1 != null) {
			otherStr += ' ' + this.updateAddressLine2;
		}
		otherStr += `, ${this.updateAddressCity} ${this.updateAddressState} ${this.updateAddressZip}`;

		if (isInputFilled && isDropdownfilled) {
			this.selectedAddressStr = otherStr;
			this.selectedAddressKey = 'Add New Address';
			this.bShowOtherForm = false;
			this.addressToValidate = 'Add New Address';
			this.bShowMappedAddresses = true;
			this.bIsApplyButton = false;
		}
		this.handlebIsApplyButtonEnable();
	}

	handleModifyAddress() {
		this.bShowOtherForm = true;
		this.bIsApplyButton = true;
		this.trackAddressClickBy = 'ModifyAddress';

		if (this.selectedAddressKey !== 'Add New Address') {
			for (let value of this.addressMap.values()) {
				if (value.key.toString() === this.addressToValidate) {
					this.updateAddressLine1 = value.addressLine1;
					this.updateAddressLine2 = value.addressLine2;
					this.updateAddressCity = value.city;
					this.updateAddressState = value.stateCode;
					this.updateAddressZip = value.zipCode;

					break;
				}
			}
		}
	}

	handleUSPSBack() {
		if (this.checkAddAddress === 'New Address') {
			this.goToPage('addAddress');
		} else {
			this.goToPage('editAddress');
		}
	}

	handleStatusChange(event) {
		this.toggleAddresskey = event.target.value;
		if (this.toggleAddresskey) {
			this.warningMessage =
				'Would you like to keep the status update made to this member?';
		}
		this.infoMessage = true;
	}

	handleStatusError(event) {
		event.preventDefault();
		if (event.target.id.includes('errorId')) {
			this.statusError = false;
		}
	}

	handleUSPSSaveAddresses(event) {
		let literalStartChar = event.detail.address[0].addressType;
		this.newAddressLiteralType =
			literalStartChar === 'S'
				? 'Shipping'
				: literalStartChar === 'P'
				? 'Permanent'
				: literalStartChar === 'B'
				? 'Billing'
				: 'Alternate';

		this.showUSPSSecondError = false;

		if (event.detail.isNewCase === true) {
			this.ePostLoading = true;
			this.saveToEpost(
				event.detail.address,
				'handleUSPSSaveAddresses',
				null
			);
		} else {
			this.ePostLoading = false;
			this.saveToEpost(
				event.detail.address,
				'handleUSPSSaveAddresses',
				null
			);
		}
	}

	handleRemoveUSPSSelection(event) {
		this.selectedAddress.delete(event.detail);
		if (
			this.addresses.findIndex(
				(address) => address.uniqueKey === event.detail
			) >= 0
		) {
			this.addresses.find(
				(address) => address.uniqueKey === event.detail
			).addressChecked = false;
		}
		this.selectedOptions = this.selectedOptions.filter(
			(item) => item.name !== event.detail
		);
	}

	handleWarningMessage(event) {
		this.undoSwitchToggle(this.toggleAddresskey);
		this.toggleAddresskey = '';
		this.infoMessage = false;
	}

	handleWarningYes(event) {
		const addresskey = this.toggleAddresskey;
		let addressData;
		this.checkAddAddress = 'Status Update';

		if (this.addressMap.has(addresskey)) {
			addressData = this.addressMap.get(addresskey);
			let lstAddress = [];
			lstAddress.push(addressData);

			this.saveToEpost(lstAddress, 'handleStatusChange', addresskey);
		}
		this.toggleAddresskey = '';
		this.infoMessage = false;
	}

	//Generates lists for UI forms
	get addressTypes() {
		return [
			{
				label: 'Shipping',
				value: 'S'
			},
			{
				label: 'Billing',
				value: 'B'
			},
			{
				label: 'Permanent',
				value: 'P'
			},
			{
				label: 'Alternate',
				value: 'A'
			}
		];
	}
	
	get items() {
		this._addressList = [];
		let temp_Obj = {
			label: 'Add New Address',
			value: 'Add New Address'
		};
		this._addressList.push(temp_Obj);
		for (let [key, addressStr] of this.uniqueAddressData.entries()) {
			let temp_Obj = {
				label: addressStr,
				value: key
			};
			this._addressList.push(temp_Obj);
		}
		return this._addressList;
	}

	get stateOptions() {
		return [
			{
				label: 'AL',
				value: 'AL'
			},
			{
				label: 'AK',
				value: 'AK'
			},
			{
				label: 'AZ',
				value: 'AZ'
			},
			{
				label: 'AR',
				value: 'AR'
			},
			{
				label: 'CA',
				value: 'CA'
			},
			{
				label: 'CO',
				value: 'CO'
			},
			{
				label: 'CT',
				value: 'CT'
			},
			{
				label: 'DE',
				value: 'DE'
			},
			{
				label: 'DC',
				value: 'DC'
			},
			{
				label: 'FL',
				value: 'FL'
			},
			{
				label: 'GA',
				value: 'GA'
			},
			{
				label: 'HI',
				value: 'HI'
			},
			{
				label: 'ID',
				value: 'ID'
			},
			{
				label: 'IL',
				value: 'IL'
			},
			{
				label: 'IN',
				value: 'IN'
			},
			{
				label: 'IA',
				value: 'IA'
			},
			{
				label: 'KS',
				value: 'KS'
			},
			{
				label: 'KY',
				value: 'KY'
			},
			{
				label: 'LA',
				value: 'LA'
			},
			{
				label: 'ME',
				value: 'ME'
			},
			{
				label: 'MD',
				value: 'MD'
			},
			{
				label: 'MA',
				value: 'MA'
			},
			{
				label: 'MI',
				value: 'MI'
			},
			{
				label: 'MN',
				value: 'MN'
			},
			{
				label: 'MS',
				value: 'MS'
			},
			{
				label: 'MO',
				value: 'MO'
			},
			{
				label: 'MT',
				value: 'MT'
			},
			{
				label: 'NE',
				value: 'NE'
			},
			{
				label: 'NV',
				value: 'NV'
			},
			{
				label: 'NH',
				value: 'NH'
			},
			{
				label: 'NJ',
				value: 'NJ'
			},
			{
				label: 'NM',
				value: 'NM'
			},
			{
				label: 'NY',
				value: 'NY'
			},
			{
				label: 'NC',
				value: 'NC'
			},
			{
				label: 'ND',
				value: 'ND'
			},
			{
				label: 'OH',
				value: 'OH'
			},
			{
				label: 'OK',
				value: 'OK'
			},
			{
				label: 'OR',
				value: 'OR'
			},
			{
				label: 'PA',
				value: 'PA'
			},
			{
				label: 'PR',
				value: 'PR'
			},
			{
				label: 'RI',
				value: 'RI'
			},
			{
				label: 'SC',
				value: 'SC'
			},
			{
				label: 'SD',
				value: 'SD'
			},
			{
				label: 'TN',
				value: 'TN'
			},
			{
				label: 'TX',
				value: 'TX'
			},
			{
				label: 'UT',
				value: 'UT'
			},
			{
				label: 'VT',
				value: 'VT'
			},
			{
				label: 'VA',
				value: 'VA'
			},
			{
				label: 'WA',
				value: 'WA'
			},
			{
				label: 'WV',
				value: 'WV'
			},
			{
				label: 'WI',
				value: 'WI'
			},
			{
				label: 'WY',
				value: 'WY'
			}
		];
	}
}