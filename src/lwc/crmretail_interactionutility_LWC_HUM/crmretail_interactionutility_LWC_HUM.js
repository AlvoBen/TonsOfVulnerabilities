/*
*******************************************************************************************************************************
File Name        : crmretail_interactionutility_LWC_HUM.js
Version          : 1.0 
Created Date     : 07/19/2022
Function         : utility LWC to process interaction data
Modification Log :
* Developer                Date                  Description
*******************************************************************************************************************************
* Lakshmi Madduri      	  07/19/2022            Original Version
* Navajit Sarkar          08/10/2022            User Story 3510484: T1PRJ0154546 / SF / MF9 Storefront: Recommend Events (Story 1)
* Vivek Sharma            08/17/2022            User Story 3581472: T1PRJ0154546 / SF / MF9 Storefront: Ability to Create Follow Up Task From Visitor Check-Ins
* Lakshmi Madduri         09/09/2022            US 3816804 Salesforce Winter Upgrade fix for Time column
* Vinoth L                09/06/2022            DF-6112 - 09/23 - Engagement Prediction Hover Fix
Navajit Sarkar            09/27/2022            User Story 3782843: MF9 Storefront: Modernization - Interactions/Events - Ability to View Calendar Events   
* Vinoth L                01/05/2023            User Story 4107848: T1PRJ0154546 / SF / MF3 Storefront: Update Visitor ID & Type Population Logic     
*/
import {getLabels} from './crmretail_labels';
import {interactionConstants} from './crmretail_labels';
import timezone from '@salesforce/i18n/timeZone';
export const labels = getLabels();
export const allConstants = interactionConstants;
export const interactionFieldMap = (fieldName) =>{
    var gcmInteractionFieldObj = {
        id: 'Id',
        name: 'Name',
        accountId: 'Account__r.Id',
        accountName: 'Account__r.Name',
        waiverDate: 'Account__r.Waiver_Date__c',
        permissiontoContact:'Account__r.Permission_to_Contact__c',
        lastModifiedById: 'LastModifiedBy.Id',
        lastModifiedByName: 'LastModifiedBy.Name',
        lastModifiedDate: 'LastModifiedDate',
        createdByName:'CreatedBy.Name',
        Date : 'Interaction_Date__c',
        reasonId: 'Reason__r.Id',
        reasonName: 'Reason__r.Name',
        locationId: 'Location__r.Id',
        locationName: 'Location__r.Name',
        locationType: 'Location__r.Location_Type__c',
        visitorType: 'CRM_Retail_Interaction_Visitor_Type__c', 
        category: 'Category__c',
        isAllDay: 'isAllDayEvent__c',
        StartDateTime: 'Storefront_Event_Starttime__c',
        EndDateTime: 'Storefront_Event_Endtime__c',
        sDoH:'SDoH__c',
        EnterpriseId :'Account__r.Enterprise_ID__c',
        birthdate : 'Account__r.Birthdate__c',
        notificationOptOut : 'Account__r.CRMRetailNotificationOptOut__c',
        isMissingEvent : 'Missing_Event__c',
        inactiveMember : 'inactiveMember__c',
        indicator : 'Storefront_Vstr_Indicator__c',
        personContactId : 'Account__r.PersonContactId',
        preferredName:'Account__r.CRMRetail_PreferredName__c'
    };
    return gcmInteractionFieldObj[fieldName];
    }
	export const interactionFlatFields=() => {
		var flatFields = ['preferredName','EnterpriseId','sDoH','StartDateTime','EndDateTime','isAllDay', 'category', 'visitorType', 'id', 'name', 'accountId', 'accountName', 'lastModifiedById', 
						  'lastModifiedByName','lastModifiedDate','createdByName', 'Date', 'reasonId', 'reasonName', 'locationId', 'locationName','locationType','waiverDate','permissiontoContact','birthdate','notificationOptOut','isMissingEvent','inactiveMember','indicator', 'personContactId' ];
		return flatFields;
	}
    export const populateBirthdayIcon = (obj)=>{
    var birthdayIconDuration;
    var birthday = new Date();                                
    let optOut = String(obj.notificationOptOut);				                                
    var today = new Date().toUTCString();
    if(obj.birthdate != null)
    {
        var birthDate = new Date(obj.birthdate);
        obj.birthdate = birthDate.getMonth()+1+'/'+birthDate.getDate()+'/'+birthDate.getFullYear();
        if(obj.Date != null && obj.birthdate < today && (optOut == null || !optOut.includes(labels.BIRTHDAY)))
        {                                    
            var bdayFactors = obj.birthdate.split('/');                          
            var currentDate = new Date(obj.Date);                         
            if(bdayFactors[0] == labels.MONTH_COUNT && bdayFactors[1] ==labels.DAYS_COUNT){                            
                var year = currentDate.getFullYear();                    
                if(((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0)){                        
                    birthday.setFullYear(year,bdayFactors[0]-1,bdayFactors[1]);                        
                }
                else{                                        
                    birthday.setFullYear(year,bdayFactors[0]-1,bdayFactors[1]-1);                                
                }                    
            }                 
            else{                                
                birthday.setFullYear(currentDate.getFullYear(),bdayFactors[0]-1,bdayFactors[1]); 
            }                          
            birthday = birthday.getMonth()+1+'/'+birthday.getDate()+'/'+birthday.getFullYear();
            currentDate = currentDate.getMonth()+1+'/'+currentDate.getDate()+'/'+currentDate.getFullYear();                                                 
            birthday = new Date(birthday);                                                   
            currentDate = new Date(currentDate);                        
            birthdayIconDuration = Math.ceil((currentDate-birthday)/8.64e7);                              
            if(birthdayIconDuration > labels.END_DURATION){
                birthday.setFullYear(currentDate.getFullYear()+1); 
                birthdayIconDuration = Math.ceil((currentDate-birthday)/8.64e7);
            }                          
            if(birthday != null && (birthdayIconDuration >= labels.START_DURATION && birthdayIconDuration <= labels.END_DURATION)){                    
                obj.birthdayIconName = labels.BIRTHDAY_ICON;
                obj.birthdayIconLabel = obj.birthdate;
                obj.birthdayIconClass='birthdayIconOriginal';
            }
        }    
    }
    return obj;                           
    }
	export const  generateVisitorURL = (visitorId, recordTypeName) => {
	var baseURL = window.location.origin;
	var secondaryURL = "";
	switch(recordTypeName) {
		case "Member":
			secondaryURL = "/lightning/r/Account/" + visitorId + "/view";
			break;
		default:
			secondaryURL = "/lightning/r/Account/" + visitorId + "/view";
			break;
	}
	let sURL = baseURL + secondaryURL;
	return sURL;
	}
	export const populateCategoryProp = (obj,isBellDisplay) => {
	switch (obj.category) {
		case null:
			obj.categoryIconName = labels.EMPLOYEE_ORGANiZATION_ICON;
            obj.categoryIconLabel =labels.ONSITE;
            obj.categoryIconClass='btnIcon';
            if(isBellDisplay){
				obj.alertIcon = 'utility:notification';
            }
			break;
		case "Virtual":
			obj.categoryIconName = labels.EMPLOYEE_ASSET_ICON;
            obj.categoryIconLabel = labels.VIRTUAL;
            obj.categoryIconClass='btnIconOriginal';
            if(isBellDisplay){
				obj.alertIcon = 'utility:notification';
            } 
			break;
		case "Scheduled Virtual":
			obj.categoryIconName = labels.EMPLOYEE_ASSET_ICON;
            obj.categoryIconLabel = labels.SCHEDULED_VIRTUAL_ICON;
            obj.categoryIconClass='btnIconOriginal';
            obj.categoryScheduledIconName='standard:event';
			break;
		case "Scheduled Onsite":
			obj.categoryIconName = labels.EMPLOYEE_ORGANiZATION_ICON 
            obj.categoryIconLabel = labels.SCHEDULED_ONSITE_ICON;
            obj.categoryScheduledIconName= 'standard:event';
            obj.categoryIconClass='btnIcon';
			break;
		case "Recorded":
			obj.categoryIconName = labels.VIDEO_ICON; 
            obj.categoryIconLabel = labels.VIDEO_ICON_LABEL;                    
            obj.categoryIconClass='btnIconVideo';
            break;
        default:
	}
	return obj;
}
export const flattenResponse = (interactions,accIdEligibleForBellIcon,accIdEligibleForRecommendation) => {
    var gcmInteractionFlatList = [];
    let flatFieldList = interactionFlatFields();
    for(let i=0;i<interactions.length;i++)
    {
        let obj = {};
        let isBellDisplay=false;	
        let listOfCheckedbox=[];
        for(let j=0;j<flatFieldList.length;j++)
        {
            let fieldMapValue = interactionFieldMap(flatFieldList[j]);
            if(fieldMapValue.includes(".")) 
            {
                let mapArr = fieldMapValue.split(".");
                let sValue = "";
                if(flatFieldList[j] === "waiverDate") {
                    if(typeof interactions[i][mapArr[0]] !== "undefined" && typeof interactions[i][mapArr[0]][mapArr[1]] !== "undefined"){
                        let date = new Date(interactions[i][mapArr[0]][mapArr[1]]).toISOString();
                        date = date.substring(0,10);
                        let arr = date.split('-');
                        sValue = arr[1]+'/'+arr[2]+'/'+arr[0];
                        obj[flatFieldList[j]] = sValue;
                    }                    
                }
                else{
                    sValue = (typeof interactions[i][mapArr[0]] !== "undefined") ? interactions[i][mapArr[0]][mapArr[1]] : null;
                    obj[flatFieldList[j]] = sValue;
                }
            }
            else 
            {
                if(flatFieldList[j] === "Date") {
                    let localDate = (typeof interactions[i][fieldMapValue] !== "undefined") ? new Date(interactions[i][fieldMapValue]) : null;
                    if(localDate !== null && localDate !== "undefined")
                    {
                        obj[flatFieldList[j]] = (typeof interactions[i][fieldMapValue] !== "undefined") ? new Date(localDate.toLocaleString('en-US', {timeZone: timezone})) : null;   
                    }                                                
                }
                else {
                    obj[flatFieldList[j]] = (typeof interactions[i][fieldMapValue] !== "undefined") ? interactions[i][fieldMapValue] : null;  
                }

            }
        } 
        let accountid = (typeof interactions[i]['Account__r']['Id'] !== "undefined") ? interactions[i]['Account__r']['Id'] : null;
        if(accIdEligibleForBellIcon !=null && accIdEligibleForBellIcon !== "undefined" && accIdEligibleForBellIcon.includes(accountid)){               
        isBellDisplay=true; 
        }                      
        if(obj.isMissingEvent){
            obj.missingEventIconName = labels.PRIORITY_ICON;
        }
        if(accIdEligibleForRecommendation && accIdEligibleForRecommendation.includes(accountid)){               
            obj.eventRecommendationIconName = labels.CRMRetail_RecommendationSButtonIcon_HUM;  
        }  
        obj = populateBirthdayIcon(obj);                    
        obj = populateCategoryProp(obj,isBellDisplay);
        
        obj.interactionURL = window.location.origin + '/lightning/r/GCM_Interactions__c/' + interactions[i].Id + '/view';
        obj.interactionReasonURL = window.location.origin + '/lightning/r/CRM_Retail_Interaction_Reason__c/' + obj.reasonId + '/view';
        let memberRType;
        if(interactions[i].CRM_Retail_Interaction_Visitor_Type__c){
            memberRType = interactions[i].CRM_Retail_Interaction_Visitor_Type__c;
        }
        obj.accountURL = generateVisitorURL(accountid, memberRType);
        obj.isMember = (memberRType === "Member") ? true : false;                
        if(obj.isMember){
            obj.memberIconName = labels.CHECKIN_ICON;
        }else if(obj.inactiveMember){
            obj.memberIconName = labels.BLOCK_VISITOR;
            obj.memberIconLabel = labels.INACTIVE_MEMBER;
        }
        if(interactions[i].First_Time_Visitor__c){
            listOfCheckedbox.push(labels.FIRST_TIME_VISITOR);
        }
        if(interactions[i].New_Member__c){
            listOfCheckedbox.push(labels.NEW_MEMBER);
        }
        if(interactions[i].Veteran__c){
            listOfCheckedbox.push(labels.VETERAN);
        }
        if(interactions[i].Aging_In__c){
            listOfCheckedbox.push(labels.AGING_IN);
        }
        if(interactions[i].SDoH__c){
            listOfCheckedbox.push(labels.SDOH);
        }
        if(listOfCheckedbox.length > 0 || (obj.indicator && obj.indicator.includes(labels.SNP_TEXT)))
        { 
            var toolTipText = labels.INDICATORS_APPLY+" "+ obj.accountName + (obj.preferredName ? ' ('+obj.preferredName+')' : '') +":" + "\n" ;   
            if(listOfCheckedbox.includes(labels.FIRST_TIME_VISITOR)){  
                toolTipText=toolTipText +" "+ labels.FIRST_TIME_VISITOR +"\n";  
            }                    
            if(!listOfCheckedbox.includes(labels.NEW_MEMBER) && obj.indicator){                          
                var indicators = JSON.parse(obj.indicator);                                                                            
                var preFix = indicators[labels.ENGAGEMENT_PREDICTION_TEXT] ? indicators[labels.ENGAGEMENT_PREDICTION_TEXT].substring(0,1).toUpperCase() : '';                           
                toolTipText = (indicators[labels.ENGAGEMENT_PREDICTION_TEXT]) ? toolTipText +" "+ labels.ENGAGEMENT_PREDICTION_TEXT + ' ' + '('+preFix+')' +"\n" : toolTipText;  
            }  
            if(listOfCheckedbox.includes(labels.SDOH)){  
                toolTipText=toolTipText +" "+ labels.SDOH +"\n";  
            }
            if(listOfCheckedbox.includes(labels.NEW_MEMBER)){  
                toolTipText=toolTipText +" "+ labels.NEW_MEMBER +"\n";  
            }  
            if(listOfCheckedbox.includes(labels.VETERAN)){  
                toolTipText=toolTipText +" "+ labels.VETERAN +"\n";  
            }  
            if(listOfCheckedbox.includes(labels.AGING_IN)){  
                toolTipText=toolTipText +" "+ labels.AGING_IN +"\n";  
            }                                                       
            if(obj.indicator){
                var indicators = JSON.parse(obj.indicator);                         
                var snpHoverText = indicators[labels.SNP_TEXT] ? labels.SNP_TEXT + ': ' +indicators[labels.SNP_TEXT] : '';                    
                if(snpHoverText){
                    toolTipText =  toolTipText+" "+ snpHoverText +"\n";
                }
            }
            obj.notificationToolTip = toolTipText;
        }  
        else{  
            obj.notificationToolTip = obj.accountName + (obj.preferredName ? ' (' + obj.preferredName + ')' : '') + " " + labels.INDICATORS_NOTAPPLIED;
        } 
        var rlDate = labels.RELEASE_DATE;
        var relDate = new Date(rlDate); 
        var ReleaseDateTime = relDate.toISOString(); 
        var iDateTime = interactions[i].Interaction_Date__c;
        var intDateTime = new Date(iDateTime);
        var interDateTime = intDateTime.toISOString();
        if(interDateTime >= ReleaseDateTime && interactions[i].Reason__r.Name != labels.VISITOR_CHECKIN_FIRSTTIME && interactions[i].Reason__r.Name != labels.VISITOR_CHECKIN ){                                            
            if(interactions[i].Account__r.RecordType.Name == labels.VISITOR_RECORDTYPE){
                obj.reasonToolTip = labels.NOT_GO365_ELIGIBLE;
            }
            else{              
                if(interactions[i].Reason__r.isGo365Eligible__c){
                    obj.reasonToolTip = labels.GO365_ELIGIBLE;
                }
                else{
                    obj.reasonToolTip = labels.NOT_GO365_ELIGIBLE;
                }
            }
        }
        else
        {
            obj.reasonToolTip = interactions[i].Reason__r.Name; 
        }
        gcmInteractionFlatList.push(obj);
    }   
    return gcmInteractionFlatList;
}
	export const getRowActions = (row,selectedDate,isNewHealthEducator, switches) => {
        var actions = [];
        var disableEducator = true;
		var todaydate = new Date();
		var interactionDate = new Date(selectedDate);
        if(row.reasonName.includes('Health Educator'))
            disableEducator = false;
        var threeYearsBefore = new Date(new Date().setFullYear(new Date().getFullYear() - 3));
        var octDate = new Date("2020-10-01");
        var waiverDate = new Date(row.waiverDate);
			
        if(row.category != undefined && row.waiverDate != undefined)
        {
            if(row.category.includes('Scheduled Onsite') && interactionDate <= todaydate && waiverDate > threeYearsBefore && waiverDate > octDate)   
                actions.push({
                    label: "Attended", 
                    name: "attended",
                    disabled: false
                });
        }
        if(row.category != undefined && row.category.includes('Scheduled Virtual') && interactionDate <= todaydate)
            actions.push({
                label: "Attended", 
                name: "attended",
                disabled: false
            });
			actions.push(
            {
                label: "View Interaction", 
                name: "editInteractions",
                disabled: false 
            },
            {
                label: "Delete Interaction", 
                name: "deleteInteractions",
                disabled: false 
            }
        );
        if(!disableEducator && isNewHealthEducator)
        {
            actions.push(
                {
                    label: "New Health Educator 1x1 Tracking", 
                    name: "healthEducator",
                    disabled: false
                }
            );
         }
         if(switches){
            if(row.reasonName && row.reasonName.includes('Check-In') && switches['Switch_3581472'])
            actions.push({
                label: labels.CRMRetail_FollowUp_BtnLabel, 
                name: allConstants.FOLLOWUP_TASK,
                disabled: false
            });
         }
        return actions;
    }