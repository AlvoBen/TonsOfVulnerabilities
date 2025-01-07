/*******************************************************************************************************************************
Function    : This JS serves as controller to groupIdCardHighlightsPanelsHum.html. 
Modification Log: 
Developer Name                    Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Suraj patil                   12/24/2021                  US: 1464525
*********************************************************************************************************************************/

import { api, track, wire } from 'lwc';
import { NavigationMixin } from 'lightning/navigation';
import crmserviceHelper from 'c/crmserviceHelper';
import { getLabels} from 'c/crmUtilityHum';
import { getModal } from './formFieldsmodel';
import { CurrentPageReference } from 'lightning/navigation';
import getPolicyDetails from '@salesforce/apex/GroupIdCards_LC_HUM.fetchGroupAccountDetails';

export default class groupIdCardHighlightsPanelsHum extends NavigationMixin(crmserviceHelper) {
    @api recordId;
    @track oFormFields;
    @track bDataLoaded = false;
    @api header = {};
    @track memberIcons;
    @track labels = getLabels();
    @api sMemberName = "";
    @track accountId = "";
    @wire(CurrentPageReference)
    currentPageReference(pageRef){
        this.pageRef = pageRef;
    }

    connectedCallback() {
        const me = this;
        const oFormFields = getModal();
        me.header = oFormFields.header;
        getPolicyDetails({
            recId: this.pageRef.attributes.attributes.C__Id
        })
            .then(oData => {
				if(oData != null){
					me.sMemberName = oData.Name;
					me.processData(oData, oFormFields)
				}
				else{
                    me.sMemberName = '';
                    me.processData(oData, oFormFields)
                }
            })
            .catch(error => {
                console.error("Error", error);
            });
    }

    /**
     * Update data to the value property of the Model
     * @param {*} oData 
     * @param {*} oFormFields 
     */
    @api
    processData(oData, oFormFields) {
        this.oFormFields = [
            ...oFormFields.recordDetail.map(item => {
                let value;
                let iconCls = "";
                if (item.mapping.indexOf('.') > 0) {
                    const tmp = item.mapping.split('.');
                    const tmpval = oData.hasOwnProperty(tmp[0]) ? oData[tmp[0]][tmp[1]] : "";
                    value = tmpval ? tmpval : '';
                }
                else {
                    value = oData.hasOwnProperty(item.mapping) ? oData[item.mapping] : "";
                }
                
                if (item.mapping === 'Recordtype.name') {
                    value = 'Group';
                }
                return {
                    ...item,
                    value,
                    iconCls
                }
            })]
        this.bDataLoaded = true;
    }
}