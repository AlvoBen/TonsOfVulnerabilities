/*
LWC Name        : PcpSelectedPhysicianHum.html
Function        : LWC to display Selected physician Section on PCP Update Flow .

Modification Log:
* Developer Name                  Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Aishwarya Pawar                 01/20/2023                   REQ- 4136289
*------------------------------------------------------------------------------------------------------------------
*/
import { LightningElement, api, track } from 'lwc';

export default class PcpSelectedPhysicianHum extends LightningElement {
    @api selectedPhysician;
    @track editSelectedPhysician = false;
    @track PcpNumber = '';
    @track physicianName = '';
    @track contactInformation = '';
    @track selectedPhysicianData = {};



    formatPhoneNumber(phoneNumberString) {
        var cleaned = ('' + phoneNumberString).replace(/\D/g, '');
        var match = cleaned.match(/^(1|)?(\d{3})(\d{3})(\d{4})$/);
        if (match) {
            var intlCode = (match[1] ? '+1 ' : '');
            return [intlCode, '(', match[2], ') ', match[3], '-', match[4]].join('');
        }
        return null;
    }

    @api
    updateData(data) {
        this.selectedPhysicianData = data;
        this.PcpNumber = this.selectedPhysicianData?.pcpNumber ?? '';
        this.physicianName = this.selectedPhysicianData?.physicianName ?? '';
    }

    handleEdit() {
        this.editSelectedPhysician = true;
    }

    handleCancel() {
        this.PcpNumber = this.selectedPhysicianData?.pcpNumber ?? '';
        this.contactInformation = this.getSelectedPhysicianData;
        this.physicianName = this.selectedPhysicianData?.physicianName ?? '';
        this.editSelectedPhysician = false;
    }

    handleInputChange(event) {
        if (event.target.dataset.id == 'PCPNumber') {
            this.PcpNumber = event.detail.value;
            let reg = new RegExp('^[0-9]*$');
            if (reg.test(this.PcpNumber) == false) {
                this.PcpNumber = this.PcpNumber.substring(0, this.PcpNumber.length - 1) >= 0 ? this.PcpNumber.substring(0, this.PcpNumber.length - 1) : '';
                if (this.template.querySelector('.pcpnumber') != null) {
                    this.template.querySelector('.pcpnumber').value = this.PcpNumber;
                }
            }
        }
        else if (event.target.dataset.id == 'ContactInfo') {
            this.contactInformation = event.detail.value;
        }
        else if (event.target.dataset.id == 'PhysicianName') {
            this.physicianName = event.detail.value;
        }

    }


    get getSelectedPhysicianData() {
        if (this.selectedPhysicianData && Object.keys(this.selectedPhysicianData).length > 0) {
            let physicianData = this.selectedPhysicianData.phone ? this.selectedPhysicianData.phone + '\n' : '';
            physicianData += this.selectedPhysicianData.physicianAddress ? this.selectedPhysicianData.physicianAddress : '';
            return physicianData;
        }
        return '';
    }


    updatePhysicianInfo() {
        this.selectedPhysicianData.phone = '';
        this.selectedPhysicianData.physicianAddress = '';
        this.selectedPhysicianData.pcpNumber = this.PcpNumber ? this.PcpNumber : '';
        this.selectedPhysicianData.physicianName = this.physicianName ? this.physicianName : '';
        let tempaddress = '';
        let contactInfo = this.contactInformation ? this.contactInformation.split('\n') : [];
        if (contactInfo && Array.isArray(contactInfo) && contactInfo.length > 0) {
            if (contactInfo.length === 1) {
                if (contactInfo[0].includes('(') && contactInfo[0].length >= 14) {
                    this.selectedPhysicianData.phone = contactInfo[0].substring(0, 14);
                    this.selectedPhysicianData.physicianAddress = contactInfo[0].length >= 14 ? contactInfo[0].substring(14) : '';
                } else {
                    if (contactInfo[0].length >= 10) {
                        let pp = contactInfo[0].substring(0, 10);
                        if (!isNaN(pp)) {
                            this.selectedPhysicianData.phone = this.formatPhoneNumber(contactInfo[0].substring(0, 10));
                            this.selectedPhysicianData.physicianAddress = contactInfo[0].length >= 10 ? contactInfo[0].substring(10) : '';
                        } else {
                            this.selectedPhysicianData.physicianAddress = contactInfo[0] ?? '';
                        }

                    } else {
                        this.selectedPhysicianData.physicianAddress = contactInfo[0] ?? '';
                    }

                }
            } else {
                contactInfo.forEach((data, index) => {
                    if (index === 0) {
                        if (data.includes('(') && data.length >= 14) {
                            this.selectedPhysicianData.phone = data.substring(0, 14);
                            tempaddress = data.substring(14).length > 0 ? data.substring(14) + ' ' : data.substring(14) + '';
                        } else if (data.length >= 10 && !isNaN(data.substring(0, 10)) && this.formatPhoneNumber(data.substring(0, 10))) {
                            this.selectedPhysicianData.phone = this.formatPhoneNumber(data.substring(0, 10));
                            tempaddress = data.substring(10).length > 0 ? data.substring(10) + ' ' : data.substring(10) + '';
                        } else {
                            tempaddress = data + '\n';
                        }

                    } else {
                        tempaddress = tempaddress + data + '\n';
                    }
                })
            }
        }
        if (tempaddress
            && tempaddress.endsWith('\n')) {
            this.selectedPhysicianData.physicianAddress = tempaddress.substring(0, tempaddress.length - 1);
        }
        this.editSelectedPhysician = false;
        this.dispatchEvent(new CustomEvent('updatephysician', {
            detail: {
                data: this.selectedPhysicianData,
            }
        }))

    }

}