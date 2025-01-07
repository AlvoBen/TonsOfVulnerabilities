/*
Page Name        : storefrontNotificationLWCHUM
Version          : 1.0 
Created Date     :03/10/2021
Function         : Lightning Web Component used Notification functionality for CRM Retail.
Modification Log :
* Developer                 Code review         Date                  Description
*************************************************************************************************
* Abhishek Maurya       	                	 03/10/2021            	Original Version
* Vinoth L                                       05/04/2021             User Story 2046674- Notification Interaction Generation
* Abhishek Maurya                                05/14/2021             2072454 CRM Storefront Notifications: Veteran Eligibility for Action Required Prompt 
* Vinoth L                      				 04/22/2021             User Story 2144275 -CRM Storefront Notifications: New Member Eligibility for Action Required Prompt
* Vinoth L                      				 04/29/2021             User Story 2119738 -CRM Storefront Notifications: Aging-In Eligibility for Action Required Prompt
* Abhishek Maurya                                06/26/2021             User Story 2308421 -CRM Storefront SDoH: Action Required Prompt 
* Vinoth L                      				 07/11/2021             User Story 1717300 -CRM Storefront Notifications: T1PRJ0001894 - MF 8 - CRM Storefront Home Page: First Onsite Visit 
* Vinoth L                      				 07/01/2022             User Story 3449791 -CRM Storefront Modernization (Business Admin/Super Admin) - Home (Left Side) 
* Sahil Verma       	                	     09/23/2022             User Story 3850860 - T1PRJ0154546 / SF / MF9 Storefront Modernization - Navigation upon check-in
* Vinoth L                      				 11/28/2022             User Story 4012388 T1PRJ0154546 / SF/ MF9 Storefront - Ability to View First Time Visit - Currebnt CY in Storefront       
* Vinoth L                      				 08/26/2023             User Story 5012060: T1PRJ0154546 / DP / MF9 Storefront - Add preferred name on Account
**************************************************************************************************/
import { LightningElement,track,api,wire } from 'lwc';
import LAST_VISITED from '@salesforce/label/c.CRM_Retail_last_visited';
import ON from '@salesforce/label/c.CRM_Retail_on';
import FIRST_TIME_CHECK_IN from '@salesforce/label/c.CRM_Retail_Visitor_Check_In_1st_Time';
import ON_SITE_VISITOR from '@salesforce/label/c.CRM_Retail_first_time_onsite_visitor';
import VIRTUAL_VISITOR from '@salesforce/label/c.CRM_Retail_first_time_virtual_visitor';
import FIRST_TIME_VISITOR from '@salesforce/label/c.CRM_Retail_First_Time_Visitor';
import VIRTUAL from '@salesforce/label/c.CRM_Retail_Virtual';
import FOLLOWUP from '@salesforce/label/c.Follow_Up_Notification';
import CRMRetail_SNP_Text from '@salesforce/label/c.CRMRetail_SNP_Text';
import CRMRetail_Interactions_PageName from '@salesforce/label/c.CRMRetail_Interactions_PageName';
import CRMRetail_Home_text from '@salesforce/label/c.CRMRetail_Home_text';
import CRMRetail_Replace_Card from '@salesforce/label/c.CRMRetail_Replace_Card';
import Icon_Label_For_Onsite_Interaction from '@salesforce/label/c.Icon_Label_For_Onsite_Interaction';
import Icon_Label_For_Virtual_Interaction from '@salesforce/label/c.Icon_Label_For_Virtual_Interaction';
import CRMRetail_First_Time_Text from '@salesforce/label/c.CRMRetail_First_Time_Text';
import CRMRetail_This_Year_Visitor_Text from '@salesforce/label/c.CRMRetail_This_Year_Visitor_Text';
import CRMRetail_FstTime_Visit_HelpText from '@salesforce/label/c.CRMRetail_FstTime_Visit_HelpText';
export default class Storefront_Notification_LWC_HUM extends LightningElement{
    isModalOpen;
    @track notificationData={};
    ackBtnDis=true;
    isFirstTimeInactive=false;
    isShowToolTip=true;
    firstTimeHelpText='';
    listOfNotifications=[];
    listOfNotificationsToShow=[];
    listOfFollowUpIds=[];
    @api pageName;
    isRenderedRun =true;
    isSpecialNeedsPlan;
    error;
    snpText = CRMRetail_SNP_Text;
    @api switchMap;
    isFstTimeClndrVst;
    fstTimeClndrVstText;
    firstTimeCldrHelpText;
    showPreferredName;

    renderedCallback(){
        if(this.isRenderedRun){   
            if(this.pageName && this.pageName ==="TaskPage"){                
                this.template.querySelector('[data-id="divblock"]').classList.remove('slds-backdrop_open');
            }
            this.isRenderedRun = false;
        }                      
    } 
    crmFunctionalitySwitch(){                       
        this.error = undefined;  
        if(this.switchMap){     
            if(this.switchMap.Switch_3316885 && this.notificationData.visitorIndicator){
                this.isSpecialNeedsPlan = true;                                
            }
            if(this.switchMap.Switch_4012388 && this.notificationData.fstTimeClndrVst){
                this.isFstTimeClndrVst = true;
                var midText;
                if (!this.notificationData.interactionCateory)
                {                    
                    midText= Icon_Label_For_Onsite_Interaction;                        
                }
                else if (this.notificationData.interactionCateory===VIRTUAL)
                {
                    midText= Icon_Label_For_Virtual_Interaction;                   
                }
                this.fstTimeClndrVstText = CRMRetail_First_Time_Text+' '+ midText + ' '+ CRMRetail_This_Year_Visitor_Text;
                this.firstTimeCldrHelpText = CRMRetail_FstTime_Visit_HelpText;
            }
        }                            
    }
          
    @api
    get notificationWrapData() {
        return this.notificationData;
    }
    set notificationWrapData(value) { 
        this.listOfFollowUpIds = [];
        this.listOfNotifications = [];
        let notificationObject;
        if(value){
            if(typeof value === "string" && value.length>1 && value != '[]'){							
                notificationObject=JSON.parse(value.substring(1,value.length-1));
                if(notificationObject.listOfFollowUpNotificationsId !==null && notificationObject.listOfFollowUpNotificationsId !== undefined ){
                    this.listOfFollowUpIds=notificationObject.listOfFollowUpNotificationsId;
                }
                if(notificationObject.listOfNotificationRec !==null && notificationObject.listOfNotificationRec !== undefined ){
                    this.listOfNotifications=notificationObject.listOfNotificationRec;
                }
                this.generateNotificationData(notificationObject);
                this.isModalOpen =true; 
            }
            else{   				
                notificationObject =value;
                if(notificationObject !== undefined && notificationObject !== null){
                    if(notificationObject.listOfNotificationRec !== null && notificationObject.listOfNotificationRec !== undefined ){
                        for (const property in notificationObject.listOfNotificationRec) {
                            this.listOfNotifications.push(notificationObject.listOfNotificationRec[property]);
                        }
                        if(notificationObject.listOfFollowUpNotificationsId !== null && notificationObject.listOfFollowUpNotificationsId !== undefined ){
                            for (const property in notificationObject.listOfFollowUpNotificationsId) {
                                this.listOfFollowUpIds.push(notificationObject.listOfFollowUpNotificationsId[property]);
                            }
                        }
                        this.generateNotificationData(notificationObject);
                        this.isModalOpen =true;
                    }                
                }
            }
        }
    }
    generateNotificationData(notificationObject){		
        this.listOfNotificationsToShow = [];
        let checkInType=notificationObject.checkInType;        
        this.notificationData.Name=notificationObject.accountRec.Name;
        if(this.switchMap && this.switchMap.Switch_5012060 && notificationObject.accountRec.CRMRetail_PreferredName__c)
        {
            this.showPreferredName = true;
            this.notificationData.accountPreferredName = notificationObject.accountRec.CRMRetail_PreferredName__c;
        }  
        this.notificationData.accId=notificationObject.accountRec.Id;
        this.notificationData.visitorType=notificationObject.visitorType;
        this.notificationData.currentLocation= notificationObject.currentLocation;
        this.notificationData.visitorId=notificationObject.visitorId;
        this.notificationData.sdohAcronym = notificationObject.sdohAcronym;
        this.notificationData.visitorIndicator = notificationObject.visitorIndicator; 
        this.notificationData.fstTimeClndrVst = notificationObject.fstTimeClndrVst;
        this.notificationData.interactionCateory = notificationObject.interactionCateory; 
        this.crmFunctionalitySwitch();                          
        if(this.listOfNotifications!==null && this.listOfNotifications!==undefined){  
            this.ackBtnDis=this.ackButtonDisCheck(this.listOfNotifications);
            this.notificationData.allNotification=this.listOfNotificationsToShow;
        }
        if(notificationObject.isPreviousAllVirtualInteration && (notificationObject.interactionCateory==='' || notificationObject.interactionCateory=== null || notificationObject.interactionCateory== undefined)){                        
            this.notificationData.midText=ON_SITE_VISITOR;    
        }
        else if(notificationObject.previousInteractionDate!==null && notificationObject.previousInteractionDate!==undefined && notificationObject.previousInteractionDate!=='')
        {
            this.notificationData.midText= LAST_VISITED+" "+notificationObject.previousInteractionLocation+" "+ ON +" "+notificationObject.previousInteractionDate;
            this.isShowToolTip=false;
        }else{
            if (!this.isFirstTimeInactive)
            {
                if (checkInType===FIRST_TIME_CHECK_IN)
                {                    
                    if (notificationObject.interactionCateory === null || notificationObject.interactionCateory === '' || notificationObject.interactionCateory == undefined )
                    {
                        this.notificationData.midText=ON_SITE_VISITOR;                        
                    }
                    else if (notificationObject.interactionCateory===VIRTUAL)
                    {
                        this.notificationData.midText=VIRTUAL_VISITOR;
                    }
                }
            }
            else{
                this.notificationData.midText='';
            } 
        }
    }
    closeModal(){        
        this.isModalOpen = false;
        this.notificationData.ack=false;
        if(this.pageName == CRMRetail_Home_text || this.pageName == CRMRetail_Replace_Card || this.pageName == CRMRetail_Interactions_PageName){
            this.closeNotificationEvent();
        }
        else{
            this.generateNotificationEvent();                
        }     
    }
    submitDetails() {        
        this.isModalOpen = false;
        let isChecked=false;
        if(this.template.querySelector(`[data-item='Waiver Required']`)!==null && this.template.querySelector(`[data-item='Waiver Required']`).checked){            
            this.notificationData.waiverDate=true;
            isChecked=true;
        }
        if(this.template.querySelector(`[data-item='New Member']`)!==null && this.template.querySelector(`[data-item='New Member']`).checked){
            this.notificationData.newMember=true; 
            isChecked=true; 
        }
        if(this.template.querySelector(`[data-item='Veteran']`)!==null && this.template.querySelector(`[data-item='Veteran']`).checked){
            this.notificationData.veteran=true; 
            isChecked=true; 
        }
        if(this.template.querySelector(`[data-item='Aging In']`)!==null && this.template.querySelector(`[data-item='Aging In']`).checked){
            this.notificationData.agingIn=true; 
            isChecked=true; 
        }
        if(this.template.querySelector(`[data-item='SDoH']`)!==null && this.template.querySelector(`[data-item='SDoH']`).checked){
            this.notificationData.SDoH=true; 
            isChecked=true; 
        }
        if(isChecked){
            this.notificationData.ack=true;
            this.generateNotificationEvent();
        }
        else{
            this.notificationData.ack=false;
            this.generateNotificationEvent();  
        }
    }
    handleCheckboxChange() {
        const checkedItemArr = Array.from(
            this.template.querySelectorAll('lightning-input')
        ).filter(element => element.checked).map(element => element.label);
        if(checkedItemArr.length>0){
            this.ackBtnDis=false;
        }else{
            this.ackBtnDis=true;
        }
    }
    ackButtonDisCheck(listOfNotification){
        var flag=true;     
        var isFirstTime = false;   
        var isOtherType = false;
        var isSDohType=false;
        let sDohNotificationObj={};
        for (let index=0; index < listOfNotification.length; ++index) {
            if(listOfNotification[index].Notification_Type__c === 'SDoH'){
                sDohNotificationObj.notificationTypeAsKey=listOfNotification[index].Notification_Type__c;
                sDohNotificationObj.Instructions=listOfNotification[index].Instructions__c;
                sDohNotificationObj.notilabel= listOfNotification[index].Notification_Type__c+this.notificationData.sdohAcronym;
                isOtherType = true;
                flag=true;
                isSDohType=true;
                continue;
            }            
            else if(listOfNotification[index].Notification_Type__c=== FIRST_TIME_VISITOR){                
                if(listOfNotification[index].Inactive__c===true){
                    this.isFirstTimeInactive=true;
                    this.isShowToolTip=false;
                }else{                    
                    this.firstTimeHelpText=listOfNotification[index].Instructions__c;        
                }
                isFirstTime = true;
            }else{
                isOtherType = true;
                let notificationObj={};
                    notificationObj.notificationTypeAsKey=listOfNotification[index].Notification_Type__c;
                notificationObj.Instructions=listOfNotification[index].Instructions__c;
                if(this.listOfFollowUpIds.includes(listOfNotification[index].Id) && listOfNotification[index].Notification_Type__c != 'SDoH'){
                    notificationObj.notilabel=listOfNotification[index].Notification_Type__c+' '+FOLLOWUP;
                }else{
                    notificationObj.notilabel= listOfNotification[index].Notification_Type__c;
                }
                this.listOfNotificationsToShow.push(notificationObj);  
                flag = true;                              
            } 
        }
        if(!isOtherType && isFirstTime){
            flag = false;
        }
        if(this.listOfNotificationsToShow.length>0){
            this.listOfNotificationsToShow.sort(function(arg1, arg2) {
                var notificationType1 = arg1.notilabel.toUpperCase(); 
                var notificationType2 = arg2.notilabel.toUpperCase();                                                                                                                                                     
                    if (notificationType1 > notificationType2) {
                        return 1;
                    }
                    if (notificationType1 < notificationType2) {
                        return -1;
                    }                                                                                                           
                return 0;
            });
            if(this.listOfNotificationsToShow[this.listOfNotificationsToShow.length-1].notilabel.includes('Waiver')){
                if(this.listOfNotificationsToShow[0].notilabel.includes('Aging')){
                    let temp=this.listOfNotificationsToShow[0];
                    this.listOfNotificationsToShow[0]=this.listOfNotificationsToShow[this.listOfNotificationsToShow.length-1];
                    this.listOfNotificationsToShow[this.listOfNotificationsToShow.length-1]=temp;
                }
                else{
                    this.listOfNotificationsToShow.splice(0,0,this.listOfNotificationsToShow[this.listOfNotificationsToShow.length-1]);
                    this.listOfNotificationsToShow.pop();
                } 
            }else if(this.listOfNotificationsToShow[0].notilabel.includes('Aging')){
                    this.listOfNotificationsToShow.splice(this.listOfNotificationsToShow.length,0,this.listOfNotificationsToShow[0]);
                    this.listOfNotificationsToShow.shift();
            }
            if(isSDohType){
                if(this.listOfNotificationsToShow[0].notilabel.includes('Waiver')){
                   this.listOfNotificationsToShow.splice(1,0,sDohNotificationObj);
               }
               else{
                   this.listOfNotificationsToShow.splice(0,0,sDohNotificationObj);
                 }
            }
        }else{
            if(isSDohType){
            this.listOfNotificationsToShow.push(sDohNotificationObj);
            }
        }
        return flag;
    }
    generateNotificationEvent(){
        const notifiEvnt=new CustomEvent( "notificationack",{
            detail:{
                data:this.notificationData
            }
        });
        this.dispatchEvent(notifiEvnt);
    }
    closeNotificationEvent(){
        const notifiEvnt=new CustomEvent("notificationclose");
        this.dispatchEvent(notifiEvnt);
    }
}