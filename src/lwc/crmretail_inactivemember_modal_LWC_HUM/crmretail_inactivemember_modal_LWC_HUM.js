/*******************************************************************************************************************************
File Name : crmretail_inactivemember_modal_LWC_HUM.js 
Version : 1.0
Created On : 3/17/2022
Function : This file holds the js functions for inactive member popup
Modification Log: 
* Modification ID		Date				Developer Name           	Description                                         
*------------------------------------------------------------------------------------------------------------------------------
* 1.0       			05/17/2022			Lakshmi Madduri				Orginal Version
* 2.0       			07/05/2022			Mohamed Thameem				Home Page Modernization	
* 3.0            	    07/08/2022          Navajit Sarkar       	    Home Page Modernization	
****************************************************************************************************************************/
import { LightningElement,api} from 'lwc';
import { NavigationMixin } from "lightning/navigation";
import INACTIVE_POLICY from '@salesforce/label/c.CRMRetail_InactiveMem_Exists';
import NONMEMBERACCOUNT_EXISTS from '@salesforce/label/c.CRMRetail_NonMemberAcc_Exists';
import EXPIREDMEMBERMSG1 from '@salesforce/label/c.CRMRETAIL_SOFTDELETED_MSG_1';
import EXPIREDMEMBERMSG2 from '@salesforce/label/c.CRMRETAIL_SOFTDELETED_MSG_2';
import UNEXPECTED_ERROR from '@salesforce/label/c.CRMRetail_Unexpected_Error';
import EXPIREDMEMMATCHFOUND from '@salesforce/label/c.CRMRetail_InactiveMemVisitorAcc_MatchFound';
import EXPIREDMEMMSGWITHCLICKNEW from '@salesforce/label/c.CRMRetail_InactiveMemExpiredMsg';
import HEADERTITLE from'@salesforce/label/c.CRMRetail_ExpiredMemberCard';
import RECOMMENDEDMSG from '@salesforce/label/c.CRMRetail_InactiveMem_RecommendedMsg';
import CRMRetail_Modernized_cmp from '@salesforce/label/c.CRMRetail_Modernized_cmp';
import { ShowToastEvent } from 'lightning/platformShowToastEvent';
const columns = [
 { label: 'Name', fieldName: 'accountURL', type: 'url',typeAttributes: {label: { fieldName: 'Name' }, target: '_blank'}},
    { label: 'Birthdate', fieldName: 'Birthdate__c'},
    { label: 'State', fieldName: 'PersonMailingState'},
    { label: 'Zip Code', fieldName: 'PersonMailingPostalCode' },
    { label: 'Phone', fieldName: 'PersonHomePhone' }
];
export default class Crmretail_inactivemember_modal_LWC_HUM extends NavigationMixin(LightningElement) {
    @api inactiveMemberData;
    @api sourceComp;
    hasVisitorRecords=false;
    columns=columns;
    data=[];
    dataObj;
    displayMsg;
    isModalOpen=true;
    visitorData;
    isScanCard;
    inactiveMemberAccDetails;
    isInactiveMember;
    isExpiredMember;
    headertitle=HEADERTITLE;
    displaymsgforInactiveAcc;
    displaymsgforExistingAccPrefix;
    displaymsgforExistingAccSuffix;
    connectedCallback(){
        var sMsg;
        try                
        {  
            if(this.inactiveMemberData[0].isInactiveMember){
                this.isInactiveMember=true;
            }
            if(this.inactiveMemberData[0].isExpiredMember){
                this.isExpiredMember=true;
            }
            this.isScanCard =  this.inactiveMemberData[1].isScanCard;
            this.inactiveMemberAccDetails = JSON.parse(this.inactiveMemberData[2].inactiveMemberAcc);                      

            if(this.inactiveMemberData[3]){
                this.visitorData = JSON.parse(this.inactiveMemberData[3].NonMemberAccount);
                this.hasVisitorRecords=true;
                if(this.isScanCard)
                {
                    this.displayMsg = EXPIREDMEMBERMSG1 +' ' + this.inactiveMemberAccDetails.Name +' '+EXPIREDMEMBERMSG2+' '+RECOMMENDEDMSG;
                    this.continuationMsg = EXPIREDMEMMATCHFOUND;
                    this.continuationMsg = this.continuationMsg.replace('XXXX',this.inactiveMemberAccDetails.Name);
                }                
                else{
                    sMsg = NONMEMBERACCOUNT_EXISTS;
                    this.displaymsgforExistingAccPrefix=sMsg.split('|')[0]+' ';
                    this.displaymsgforExistingAccSuffix=sMsg.split('|')[1];
                }
            }
            else{
                if(this.isScanCard)
                {
                    this.displayMsg = EXPIREDMEMBERMSG1 +' ' + this.inactiveMemberAccDetails.Name +' '+EXPIREDMEMBERMSG2+' '+RECOMMENDEDMSG;
                    this.continuationMsg = EXPIREDMEMMSGWITHCLICKNEW;
                }                
                else{
                    this.displaymsgforInactiveAcc = INACTIVE_POLICY;
                }
            }
            if(this.visitorData){
                this.dataObj = [{           
                    "Name":this.visitorData.Name,
                    "Birthdate__c":this.inactiveMemberAccDetails.Birthdate__c,
                    "PersonMailingState":this.visitorData.PersonMailingState,
                    "PersonMailingPostalCode":this.visitorData.PersonMailingPostalCode,
                    "PersonHomePhone": this.formatPhoneNumber(this.visitorData.PersonHomePhone),
                    "Id":this.visitorData.Id,
                    "accountURL":'/'+this.visitorData.Id
                }];       
                this.data=this.dataObj;
            }            
        }
        catch(err){
            if(err.message){
                this.showToast(err.message);
            }
            else{
                this.showToast(UNEXPECTED_ERROR);
            }
        }        
    }
    formatPhoneNumber(phone) 
    {
        var cleaned = ('' + phone).replace(/\D/g, '');
        var match = cleaned.match(/^(\d{3})(\d{3})(\d{4})$/);                 
        if(match)
        {
            phone = '(' + match[1] + ') ' + match[2] + '-' + match[3];
        } 
        return phone;
    }
    closeModal(){
        this.isModalOpen=false;
        const closeevent = new CustomEvent('closeinactivemodal');
        this.dispatchEvent(closeevent);
    }
    handleNewButtonClick() {
        var accObj;
        try {
            accObj = {
                "FirstName": this.inactiveMemberAccDetails.FirstName,
                "LastName": this.inactiveMemberAccDetails.LastName,
                "Birthdate__c": this.inactiveMemberAccDetails.Birthdate__c,
                "Gender__c": this.inactiveMemberAccDetails.Gender__c,
                "PersonMailingStreet": this.inactiveMemberAccDetails.PersonMailingStreet,
                "PersonMailingCity": this.inactiveMemberAccDetails.PersonMailingCity,
                "PersonMailingState": this.inactiveMemberAccDetails.PersonMailingState,
                "PersonMailingPostalCode": this.inactiveMemberAccDetails.PersonMailingPostalCode,
                "ScanCardFlow": this.isScanCard
            };
            if((this.isScanCard && this.isInactiveMember) || (!this.isScanCard)){
                accObj.ParentId = this.inactiveMemberAccDetails.Id;
            }

            let navConfig;
            if (this.sourceComp==CRMRetail_Modernized_cmp) {
                sessionStorage.setItem('c__insertAccountObj', JSON.stringify(accObj));
                let cmpDef = {
                    componentDef: "c:crmRetail_newVisitorInformation_LWC_HUM",
                };

                let encodedDef = btoa(JSON.stringify(cmpDef));
                navConfig = {
                    type: "standard__webPage",
                    attributes: {
                        url: "/one/one.app#" + encodedDef
                    }
                };
            }
            else{
                 navConfig = {
                    type: "standard__component",
                    attributes: {
                        componentName: "c__VisitorInformation_LCMP_HUM"
                    },
                    state: {
                        c__insertAccountObj: accObj
                    }
                };
            }

            if(navConfig) {
                this[NavigationMixin.Navigate](navConfig);
            }

        }
        catch(err){
            if(err.message){
                this.showToast(err.message);
            }
            else{
                this.showToast(UNEXPECTED_ERROR);
            }
        }
    }
    handleonsitecheckin()
    {
        var accountFields;
        try
        {
            const accObj={
                Id:this.visitorData.Id,
                Name:this.visitorData.Name
            };
            if(this.isInactiveMember){
                accObj.ExpiredMemAccId = this.inactiveMemberAccDetails.Id;
            }
            else{
                accObj.ExpiredMemAccId = null; 
            }
            accountFields = JSON.stringify(accObj);
            const checkinevent = new CustomEvent('checkin',{detail:{accountFields}});
            this.dispatchEvent(checkinevent);
        }
        catch(err){
            if(err.message){
                this.showToast(err.message);
            }
            else{
                this.showToast(UNEXPECTED_ERROR);
            }
        }
    }
    handlevirtualcheckin()
    {
        var accountFields;
        try
        {
            const accObj={
                Id:this.visitorData.Id,
                Name:this.visitorData.Name,
                ExpiredMemAccId : this.inactiveMemberAccDetails.Id
            };
            accountFields = JSON.stringify(accObj);
            const checkinevent = new CustomEvent('vcheckin',{detail:{accountFields}});
            this.dispatchEvent(checkinevent);
        }
        catch(err){
            if(err.message){
                this.showToast(err.message);
            }
            else{
                this.showToast(UNEXPECTED_ERROR);
            }
        }
    }
    showToast(message){
        const evt = new ShowToastEvent({
            title: message,
            message: message,
            variant: "error",
            mode: 'dismissable'
        });
        this.dispatchEvent(evt);    
    }
}