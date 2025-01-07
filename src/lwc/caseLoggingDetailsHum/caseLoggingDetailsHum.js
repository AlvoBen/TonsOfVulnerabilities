/*******************************************************************************************************************************
Function    : This JS serves as controller to CaseLoggingDetailsHum.html. 
Modification Log: 
Developer Name             Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Prasuna Pattabhi       07/04/2022                    Original Version
* Prasuna Pattabhi       08/04/2022                   Fixed the view all tab issue
* Prasuna Pattabhi       09/26/2022                   US_3802959 - popup changes
*********************************************************************************************************************************/
import { LightningElement,wire,api,track } from 'lwc';
import hasDeleteAccess from '@salesforce/apex/caseLoggingDetails_LC.hasDeleteAccess';
import deleteAttachment from '@salesforce/apex/caseLoggingDetails_LC.deleteAttachment';
import getCaseNumber from '@salesforce/apex/caseLoggingDetails_LC.getCaseNumber';
import getCaseLoggedDetails from '@salesforce/apex/caseLoggingDetails_LC.getCaseLoggedDetails';
import {invokeWorkspaceAPI} from 'c/workSpaceUtilityComponentHum';

export default class CaseLoggingDetailsHum extends LightningElement {
    
    @track attachmentData = [];
    @api recordId;
    @api caseNumber;
    @api showViewAll;
    @api viewAll;
    @api breadCrumbItems=[];
    @track buttonsConfig = [{text: 'Delete',isTypeBrand: true,eventName: 'yes'},{text: 'Cancel',isTypeBrand: false, eventName: 'no'}];
    modalVisible = false;
    @track count=0;
    attachmentId;
    columns;
    userProfile;
    pharmacyUser;
    showDeleteIcon;
    noOfRecs =10;  
    @track noDataFound = false;  

	@track delConfirmMsg='';
    @wire(getCaseNumber,{caseId:'$recordId'})caseNumberData({error,data}){
        this.caseNumber = data;
    }

    @wire(hasDeleteAccess,{caseId:'$recordId'})accessData({error,data}){
        this.showDeleteIcon = data;
        if(this.showDeleteIcon){            
            this.columns = [                
                { type: 'button-icon',fixedWidth:40,label:'',hideDefaultActions: true,
                    typeAttributes:{iconName:'action:preview',alternativeText:'Preview',
                    title:'Preview',iconClass:'slds-button_icon-xx-small',variant:'bare'},
                    cellAttributes: { alignment: 'center'}
                },
                { type: 'button-icon',label: '',fixedWidth:40,hideDefaultActions: true,
                    typeAttributes:{iconName:'action:delete',alternativeText:'Delete',
                    title:'Delete',iconClass:'slds-button_icon-xx-small ',variant:'bare'},
                    cellAttributes: { alignment: 'center' }
                },               
                { label: 'Type', fieldName: 'subType',hideDefaultActions: true },
                { label: 'Created Date', fieldName: 'createdDate',hideDefaultActions: true },
                { label: 'Created By', fieldName: 'userId', hideDefaultActions: true, type: 'url', typeAttributes:{label: {fieldName: 'createdBy' }}},
                { label: 'Created by Queue', fieldName: 'createdByQueue',hideDefaultActions: true }
            ];
        }else{
            this.columns = [                
                { type: 'button-icon',fixedWidth:35,label:'',hideDefaultActions: true,
                    typeAttributes:{iconName:'action:preview',alternativeText:'Preview',
                    title:'Preview',variant:'bare',iconClass:'slds-button_icon-xx-small'},
                    cellAttributes: { alignment: 'center'}
                },
                { label: 'Type', fieldName: 'subType',hideDefaultActions: true },
                { label: 'Created Date', fieldName: 'createdDate',hideDefaultActions: true },
                { label: 'Created By', fieldName: 'userId', hideDefaultActions: true, type: 'url', typeAttributes:{label: {fieldName: 'createdBy' }}},
                { label: 'Created by Queue', fieldName: 'createdByQueue',hideDefaultActions: true }
            ]; 
        }
    }    

    connectedCallback(){
        this.getAttachmentDetails();     
    }

    getAttachmentDetails(){
        
        if(this.showViewAll){
            this.viewAll=false;
            this.noOfRecs = 0;
        }
        getCaseLoggedDetails({caseId:this.recordId,noOfRows:this.noOfRecs}).then(result=>{
            this.count = result.Count;
            this.noDataFound = false;
            if(this.count==0){
                this.noDataFound = true;
            }
            if(result){
                this.attachmentData = JSON.parse(result.data);
                this.viewAll=this.count>this.noOfRecs && this.noOfRecs!=0?true:false;
            }
        });
    }
    handleRowAction(event) { 
        const actionName = event.detail.action.title;
        let attachment = event.detail.row;
        this.attachmentId = attachment.Id;
        let source = attachment.sourceSystem;
		this.delConfirmMsg ='';
        if(actionName =='Delete'){  
            this.modalVisible = true;
			this.delConfirmMsg ='Are you sure you want to delete logged information "'+attachment.subType+' + '+attachment.createdBy+' + '+attachment.createdByQueue+' + '+attachment.createdDate+'"?';
        }else{
            this.modalVisible = false;
            //openLWCSubtab('displayLoggedInfoHum', attachment, { label: 'Logged Information', icon: 'utility:package' });
            let componentDef = {
                componentDef: "c:displayLoggedInfoHum",            
                attributes: {encodedData: attachment}
            };            
            let encodedComponentDef = btoa(JSON.stringify(componentDef));
            let url;
            if(source =='CRM'){
                url= "/apex/DisplayAttachmentLog_VF_HUM?attachId="+attachment.Id+"&createdbyqueue="+attachment.createdByQueue;
            }else{
                url =  "/one/one.app#" + encodedComponentDef;
            }
            invokeWorkspaceAPI("getFocusedTabInfo").then(focusedTab => {
                invokeWorkspaceAPI("openSubtab", {
                    parentTabId: focusedTab.parentTabId!=null?focusedTab.parentTabId:focusedTab.tabId,                
                    url: url,                
                    focus: true
                }).then(newTabId=>{
                    let tabLabel = 'Logged Info : '+attachment.subType;
                    invokeWorkspaceAPI("setTabLabel", {tabId: newTabId,label:tabLabel });
                    invokeWorkspaceAPI("setTabIcon",{tabId: newTabId,icon: 'utility:package',iconAlt:''});
                });
            });
        }
    }

    handleYes(event){
        this.modalVisible = false;              
        deleteAttachment({attachmentId:this.attachmentId}).then(result=>{            
            this.getAttachmentDetails();
        });
    }

    handleNo(event){
        this.modalVisible = false;
    }

    onViewAllClick(event) {
        let hasTabOpened = false;
        const data ={recordId:this.recordId,relatedListName:'Logged Information',showViewAll : true,caseNumber : this.caseNumber};
        let componentDef = {componentDef: "c:caseLoggingDetailTableContainerHum",attributes: {encodedData: data}};
        let encodedComponentDef = btoa(JSON.stringify(componentDef));
        invokeWorkspaceAPI("getFocusedTabInfo").then(focusedTab => {            
            invokeWorkspaceAPI('getTabInfo', { tabId:focusedTab.parentTabId!=null?focusedTab.parentTabId:focusedTab.tabId}).then(tabDetails=>{  
                if (tabDetails && tabDetails.subtabs && tabDetails.subtabs.length > 0) {
                    tabDetails.subtabs.forEach((item) => {                                   
                      if(item.customTitle == 'Logged Information' && item.pageReference && 
                        item.pageReference?.attributes && item.pageReference?.attributes?.attributes && 
                        item.pageReference?.attributes?.attributes?.encodedData && 
                        item.pageReference.attributes.attributes.encodedData.recordId == this.recordId) {
                        invokeWorkspaceAPI("openTab", {url: item.url});
                        invokeWorkspaceAPI("refreshTab",{tabId:item.tabId, includeAllSubtabs:false});
                        hasTabOpened = true;                                     
                      }
                    });
                    if(hasTabOpened==false){
                        invokeWorkspaceAPI("openSubtab", {
                            parentTabId: focusedTab.parentTabId!=null?focusedTab.parentTabId:focusedTab.tabId,                
                            url: "/one/one.app#" + encodedComponentDef,                
                            focus: true
                        }).then(newTabId=>{
                            let tabLabel = 'Logged Information';
                            invokeWorkspaceAPI("setTabLabel", {tabId: newTabId,label:tabLabel });
                            invokeWorkspaceAPI("setTabIcon",{tabId: newTabId,icon: 'utility:package',iconAlt:''});
                        });
                    }
                }else{
                    invokeWorkspaceAPI("openSubtab", {
                        parentTabId: focusedTab.parentTabId!=null?focusedTab.parentTabId:focusedTab.tabId,                
                        url: "/one/one.app#" + encodedComponentDef,                
                        focus: true
                    }).then(newTabId=>{
                        let tabLabel = 'Logged Information';
                        invokeWorkspaceAPI("setTabLabel", {tabId: newTabId,label:tabLabel });
                        invokeWorkspaceAPI("setTabIcon",{tabId: newTabId,icon: 'utility:package',iconAlt:''});
                    });
                }
            });            
        });                
    }
}