/*
LWC Name        : GrievanceAndAppealsDetailsHum.html
Function        : LWC to display Grievance and Appeal Detail Section

Modification Log:
* Developer Name                  Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Abhishek Mangutkar             10/12/2022                 initial version - US - 3833504
* Swapnali Sonawane	             10/27/2022                 DF-6484 tab name opened is not "Case" after clicking on View Documents button
* Jonathan Dickinson             12/02/2022                 US - 4016881
*****************************************************************************************************************************/
import { LightningElement, track, api } from 'lwc';
import { getModel } from './layoutConfig';
import { openLWCSubtab } from 'c/workSpaceUtilityComponentHum';
export default class ProviderDisputeDetailsHum extends LightningElement {
    @api itemDetail;
    @track detailsObj = [];
    @track commentNotesIcon = {
        "iconname": "Notes",
        "iconvalue": "custom:custom18",
        "iconclass": "slds-icon_container slds-timeline__icon",
        "iconsize": "small"
    }
    @track commentsTimelineData;
	@track providerDisputeLayout;
    connectedCallback() {
        this.providerDisputeLayout = getModel('detailsLayout');
        this.commentTimelineModel = getModel('timelineModel')
        this.providerDisputeLayout.forEach(x => {
            this.detailsObj.push({
                fieldname: x.fieldname,
                fieldvalue: this.itemDetail.hasOwnProperty(x.mappingfield) ? this.itemDetail[x.mappingfield] : "",
                gridSize: x.gridSize,
                labelClass: x.labelClass ? x.labelClass : '',
                valueClass: x.valueClass ? x.valueClass : '',
                textbox: x.textbox ? true : false,
                copyableField: x.copyableField,
            });
        });
        this.processCommentData();

    }

    getFormattedTime(value) {
        if (value < 10) { value = "0" + value }
        return value;
    }

    getTimelineDateString(sDateTime) {
        let dateTime = new Date(sDateTime);
        let hours = dateTime.getHours();
        let amOrPm = hours >= 12 ? 'pm' : 'am';
        hours = this.getFormattedTime((hours % 12) || 12);
        let minutes = this.getFormattedTime(dateTime.getMinutes());
        let date = dateTime.toLocaleDateString('en-US');
        return `${hours}:${minutes}${amOrPm} | ${date}`;
    }

    getIcon() {
        return this.commentNotesIcon;
    }
    processCommentData() {
        this.commentsTimelineData = [];
        if (this.itemDetail && this.itemDetail?.notifications && this.itemDetail.notifications?.caseNotifications) {
            this.itemDetail.notifications.caseNotifications.forEach((caseComment, index) => {
                let tmp = [];
                this.commentTimelineModel.forEach(x => {
                    switch (x.fieldname) {

                        case "icon":
                            tmp[x.fieldname] = this.getIcon();
                            break;
                        case "createddatetime":
                            tmp[x.fieldname] = this.getTimelineDateString(caseComment[x.mappingfield]);
                            break;
                        case "headerline":
                            tmp[x.fieldname] = caseComment.hasOwnProperty(x.mappingfield) ? caseComment[x.mappingfield].substring(0, 100) : '';
                            break;
                        case "subheaderline":
                            tmp[x.fieldname] = caseComment.hasOwnProperty(x.mappingfield) ? 'Created By ' + caseComment[x.mappingfield] : '';
                            break;
                        case "expanded":
                            tmp[x.fieldname] = index == 0 ? true : false;

                            break;
                        case "wrapheaderline":
                            tmp[x.fieldname] = true;
                            break;
                        case "compoundvalues":

                            let objComp = {};
                            let compvalues = x.compoundvalues;
                            compvalues.forEach(t => {
                                if (t.hasOwnProperty("body")) {
                                    objComp["body"] = this.getBodyValues(t["body"], caseComment);
                                }

                            });
                            tmp[x.fieldname] = objComp;
                            objComp = null;
                            break;
                    }

                });
                this.commentsTimelineData.push(tmp);
            });
        }

    }
    getBodyValues(bodymodel, data) {
        let objbody = [];
        bodymodel.forEach((b) => {
            objbody.push({
                ...b,
                fieldvalue: data.hasOwnProperty(b.mappingfield)
                    ? data[b.mappingfield]
                    : "",
            });
        });
        return objbody;
    }

    redirectToDocumentlist() {
        let documentlist = [];
        documentlist.push({
            context: this.itemDetail.context,
            medhokID: this.itemDetail.medhokID,
            sCase: this.itemDetail.sCase,
            headerLabel: 'Medical Provider Correspondence Dispute Cases'

        });

        openLWCSubtab('viewDocumentListSection', documentlist, { label: 'Case ' + this.itemDetail.sCase });
      
    }
}