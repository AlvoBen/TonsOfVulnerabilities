/*
LWC Name        : pcpprovidersearch.html
Function        : LWC to display pcp provider search screen.

Modification Log:
* Developer Name                  Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Nirmal Garg                     08/30/2022                  initial version US3602368
****************************************************************************************************************************/
import { LightningElement, api, track } from 'lwc';
import { ShowToastEvent } from 'lightning/platformShowToastEvent';
import { getModel } from './layoutConfig';
import customcss from '@salesforce/resourceUrl/PharmacyLightning_SR_HUM';
import { loadStyle } from 'lightning/platformResourceLoader';
import getProviders from '@salesforce/apexContinuation/PCPUpdate_LC_HUM.getProviders';
import { providersearchrequest } from './providersearchrequest';
const ICON_ARROW_UP = 'utility:arrowup';
const ICON_ARROW_DOWN = 'utility:arrowdown';
const INITIAL_RECORDS = 5;
const SEARCH_TEXT = 'All Primary Care Physician Specialties';
const SEARCH_CATEGORY = '2';
export default class PcpProviderSearchHum extends LightningElement {
	@track lstDistance = getModel("miles");
	@api memberdob;
	@api zipcode;
	@api personid;
	@api memberpcpdata;
	@api plandata;
	@api providernetworkid;
	@api requestEffectiveDate;
	@track loaded = true;
	@track isDataAvailable = false;
	@track providerSearchList = [];
	@track inputMiles = '3';
	@track filteredProviders = [];
	@track totalCount = 0;
	@track filteredcount = 0;
	@track searchPerformed = false;
	@track providersearchLayout = getModel('providersearchtable');
	@track keyword = '';
	@track offset = 1;
	@track providerSearchList1 = [];
	@track searchMatchType = 'Contains';
	@api providernetworkIds = [];

	handleSearchClick(event) {
		if (this.inputMiles || this.zipcode) {
			this.providerSearchList = [];
			this.offset = 1;
			this.callProviderSearch(true);
		} else {
			this.showToast("Error", 'input error', "error");
		}
	}


	handleMilesInput(event) {
		if (event.detail.value !== '') {
			this.inputMiles = event.detail.value;
		} else {
			this.inputMiles = null;
		}
	}

	handleMouseEnter(event) {
		let header = event.target.dataset.label;
		if (event?.target?.dataset?.sortable) {
			this.providersearchLayout.forEach(element => {
				if (element.label === header) {
					element.mousehover = true,
						element.mousehovericon = event.target.dataset.iconname === ICON_ARROW_DOWN ? ICON_ARROW_UP : ICON_ARROW_DOWN;
				}
			});
		}
	}

	handleMouseLeave(event) {
		let header = event.target.dataset.label;
		if (event?.target?.dataset?.sortable) {
			this.providersearchLayout.forEach(element => {
				if (element.label === header) {
					element.mousehover = false
				}
			});
		}
	}

	handleZipCodeInput(event) {
		if (event && event?.detail && event?.detail.value) {
			this.zipcode = event.detail.value;
			let reg = new RegExp('^[0-9]*$');
			if (reg.test(this.zipcode) === false) {
				this.zipcode = this.zipcode.length > 0 ? this.zipcode.substring(0, this.zipcode.length - 1) : '';
				if (this.template.querySelector('.zipcode') != null) {
					this.template.querySelector('.zipcode').value = this.zipcode
				}
			}
		}
	}

	connectedCallback() {
		this.loadCommonCSS();
	}

	showToast(strTitle, strMessage, strStyle) {
		this.dispatchEvent(new ShowToastEvent({
			title: strTitle,
			message: strMessage,
			variant: strStyle
		}));
	}

	//load css
	loadCommonCSS() {
		Promise.all([
			loadStyle(this, customcss + '/PharmacyLightning_CSS_HUM.css')
		]).catch(error => {
			console.log('Error Occured', error);
		});
	}

	getMemberDOB(dob) {
		if (dob) {
			return new Date(dob).toISOString().split('T')[0];
		} else {
			return '';
		}
	}

	callProviderSearch(calledFromSearchButton) {
		if (calledFromSearchButton) {
			this.loaded = false;
		}

		let pcpplan = this.plandata?.Plan?.Product_Description__c &&
			this.plandata?.Plan?.Product_Description__c.includes(' ') ?
			this.plandata?.Plan?.Product_Description__c.split(' ')[0] : null;
		let pcpoption = this.plandata?.Plan?.Product_Description__c &&
			this.plandata?.Plan?.Product_Description__c.includes(' ') ?
			this.plandata?.Plan?.Product_Description__c.split(' ')[1] : null;

		this.searchCategory = this.keyword && this.keyword?.length >= 3 ? '1' : SEARCH_CATEGORY;
		this.searchText = this.keyword && this.keyword?.length >= 3 ? this.keyword : SEARCH_TEXT;

		if (this.providernetworkIds && this.providernetworkIds.length > 0) {
			this.providernetworkIds.forEach(k => {
				let request = new providersearchrequest(1, this.zipcode, null, parseInt(k?.networkId), false, parseInt(this.inputMiles), null,
					this.plandata?.Plan?.Selling_Market_Number__c ?? null, pcpplan,
					this.requestEffectiveDate, pcpoption, null,
					this.getMemberDOB(this.memberdob), this.searchCategory,
					this.searchText, this.offset, null, this.providerTypeId, this.searchMatchType);
				getProviders({ request: JSON.stringify(request) })
					.then(result => {
						if (result) {
							this.loaded = true;
							let data = JSON.parse(result);
							if (data && data?.ProviderDirectoryResults && data?.ProviderDirectoryResults?.providers
								&& Array.isArray(data.ProviderDirectoryResults.providers) && data.ProviderDirectoryResults.providers.length > 0) {
								data.ProviderDirectoryResults.providers.forEach(k => {
									this.providerSearchList.push({
										id: this.getUniqueId(),
										physicianName: k?.publishName ?? '',
										pcpNumber: this.getCASId(k?.affiliations?.pcpNumber ?? ''),
										physicianAddress: `${this.getAddress(k?.office?.address ?? '')}`,
										physicianDistance: k && k?.office && k?.office?.distance != null ? k?.office?.distance === 0 ? '-' :
											`${k?.office?.distance} mi` : '',
										providerStatus: this.getPatientStatus(k?.affiliations?.newPatientIndicator ?? ''),
										phone: this.getPhone(k?.office?.phone ?? ''),
										specialty: this.getSpeciality(k?.affiliations?.practices ?? ''),
										patientStatus: k?.affiliations?.flagPcp === true ? true : false,
										checked: false
									})
								})
								this.totalCount = this.providerSearchList.length;
								this.filterdata();
							}
						} else {
							this.loaded = true;
						}
					}).catch(error => {
						console.log(error);
						this.loaded = true;
					})
            })
        }	
	}

	getUniqueId() {
		return Math.random().toString(16).slice(2);
	}

	@api
	updateNetworkId(networkId, networkIds) {
		this.providernetworkid = networkId;
		this.providernetworkIds = networkIds;
    }

	getPatientCheckStatus(data) {
		if (data && data?.length > 0) {
			if (data.toLocaleLowerCase() === 'y') {
				return true;
			} else if (data.toLocaleLowerCase() === 'n') {
				return false;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	getPatientStatus(data) {
		if (data && data?.length > 0) {
			if (data.toLocaleLowerCase() === 'y') {
				return 'Accepting new patients';
			} else if (data.toLocaleLowerCase() === 'n') {
				return 'Not accepting new patients';
			} else {
				return ''
			}
		} else {
			return ''
		}
	}

	clearSearchData() {
		this.keyword = '';
		this.filterdata();
	}

	renderedCallback() {
		this.template.querySelectorAll('.chkbox').forEach(k => {
			k.indeterminate = true;
		})
	}

	handleRadioClick(event) {
		let itemchecked = false;
		if (event) {
			let selectedItem = event.target.dataset.id;
			let item = this.filteredProviders.find(k => k.checked === true);
			this.filteredProviders.forEach(k => {
				k.checked = false;
			})
			let index = this.filteredProviders.findIndex(k => k.id === event.target.dataset.id);
			if (index > - 1) {
				if (item) {
					if (item.id === this.filteredProviders[index].id) {
						this.filteredProviders[index].checked = false;
					} else {
						this.filteredProviders[index].checked = true;
						itemchecked = true;
					}
				} else {
					this.filteredProviders[index].checked = !this.filteredProviders[index].checked;
					itemchecked = true;
				}
				this.dispatchEvent(new CustomEvent('selectedprovider', {
					detail: {
						providerdetails: this.filteredProviders[index],
						checked: itemchecked
					}
				}))
			}
		}
	}

	getCASId(data) {
		let tmp = '';
		if (data && Array.isArray(data) && data.length > 0) {
			data.forEach(k => {
				if (k && k?.length > 0) {
					tmp += `${k},`
				}
			})
		}
		return tmp && tmp.length > 0 && tmp.endsWith(',') ? tmp.substring(0, tmp.length - 1) : tmp;
	}

	getAddress(data) {
		let tmp = '';
		if (data) {
			tmp += data?.addressLine1 && data?.addressLine1?.length > 0 ? data?.addressLine2
				&& data?.addressLine2?.length > 0 ? `${data?.addressLine1 ?? ''}${data?.addressLine2 ?? ''},\n`
				: data?.addressLine1 && data?.addressLine1?.length > 0 ? `${data?.addressLine1 ?? ''},\n` : '' : '';
			tmp += data?.city && data?.city?.length > 0 ? data.city + ',' : '';
			tmp += `${data?.stateCode ?? ''} ${data?.zipCode ?? ''}`;
		}
		return tmp
	}

	getDistance(distance) {
		if (distance) {
			if (distance === 0) {
				return `${distance} mi`;
			}
		} else {
			return '';
		}
	}

	handleScroll(event) {
		if ((event.target.scrollHeight - Math.round(event.target.scrollTop) === event.target.clientHeight)
			|| (event.target.scrollHeight - Math.round(event.target.scrollTop) === event.target.clientHeight - 1)
			|| (event.target.scrollHeight - Math.round(event.target.scrollTop) === event.target.clientHeight + 1)) {
			if (this.filteredcount != undefined && this.totalCount != undefined) {
				if ((this.filteredcount + INITIAL_RECORDS) >= this.totalCount) {
					this.filteredcount = this.totalCount;
					this.filteredProviders = this.providerSearchList;
					this.offset = this.totalCount + 1;
					this.callProviderSearch(false);
				} else {
					this.filteredcount = this.filteredcount + INITIAL_RECORDS;
					this.filteredProviders = this.providerSearchList.slice(0, this.filteredcount);
				}
			}
		}
	}

	getSpeciality(data) {
		let tmp = '';
		if (data && Array.isArray(data) && data.length > 0) {
			data.forEach(k => {
				tmp += `${k},`
			})
			return tmp && tmp.endsWith(',') ? tmp.substring(0, tmp.length - 1) : tmp;
		}
		return tmp;
	}


	getPhone(phoneNumberString) {
		if (phoneNumberString && phoneNumberString.length > 0) {
			let cleaned = ('' + phoneNumberString).replace(/\D/g, '');
			let match = cleaned.match(/^(\d{3})(\d{3})(\d{4})$/);
			if (match) {
				return '(' + match[1] + ') ' + match[2] + '-' + match[3];
			}
			return null;
		} else {
			return '';
		}
	}

	filterdata() {
		this.filteredProviders = [];
		if (this.providerSearchList && Array.isArray(this.providerSearchList) && this.providerSearchList.length > 0) {
			this.totalCount = this.providerSearchList.length;
			if (this.providerSearchList.length > INITIAL_RECORDS) {
				this.filteredProviders = this.providerSearchList.slice(0, INITIAL_RECORDS);
			} else {
				this.filteredProviders = this.providerSearchList;
			}
			this.filteredcount = this.filteredProviders.length;
			this.isDataAvailable = true;
		} else {
			this.totalCount = 0;
			this.isDataAvailable = false;
		}
	}

	handleKeywordSearch(event) {
		this.keyword = event?.detail?.value ?? '';
	}

	onHandleSort(event) {
		if (this.providerSearchList.length > 0) {
			event.preventDefault();
			if (event?.currentTarget?.dataset?.sortable) {
				let header = event.currentTarget.dataset.label;
				let sortedBy = event.currentTarget.getAttribute('data-id');
				let sortDirection = event.currentTarget.dataset.iconname === ICON_ARROW_DOWN ? 'asc' : 'desc';
				this.providersearchLayout.forEach(element => {
					if (element.label === header) {
						element.mousehover = false;
						element.sorting = true;
						element.iconname = element.iconname === ICON_ARROW_DOWN ? ICON_ARROW_UP : ICON_ARROW_DOWN;
					} else {
						element.mousehover = false;
						element.sorting = false;
					}
				});
				const cloneData = [...this.providerSearchList];
				cloneData.sort(this.sortBy(sortedBy, sortDirection === 'asc' ? 1 : -1));
				this.providerSearchList = cloneData;
				this.filterdata();
			}

		}
	}
	sortBy(field, reverse, primer) {
		const key = primer
			? function (x) {
				return primer(x[field]);
			}
			: function (x) {
				return x[field];
			};
		return function (a, b) {
			a = key(a);
			b = key(b);
			return reverse * ((a > b) - (b > a));
		};
	}
}