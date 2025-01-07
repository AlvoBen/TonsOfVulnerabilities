({
    doInit: function(component) {    
        this.fetchSwitchResults(component);        
        this.processTaskRecords(component);         
    },
    fetchSwitchResults : function (component){  
        var action = component.get("c.fetchSwitchResults");
        action.setCallback(this,function(response){
            var state = response.getState();
            var responseArray = response.getReturnValue();
            if(state == $A.get("$Label.c.CRMRetail_SUCCESS_HUM")){
                component.set("v.switchMap",responseArray);
            }
            else{              
                var sErrorMessage = $A.get("$Label.c.CRM_Retail_Error_Updating_Task");
                this.generateErrorMessage($A.get("$Label.c.CRMRetail_Error_HUM"), sErrorMessage, $A.get("$Label.c.CRMRetail_Error_variant"));
                $A.get("e.force:closeQuickAction").fire();
            } 
        });
        $A.enqueueAction(action); 
    },
    processTaskRecords : function(component) { 
        component.set("v.Spinner",true);                                    
        var taskRecordId = component.get("v.recordId");            
        var notificationsArray=[]; 
        var action = component.get("c.getInteractionInfo");      
        action.setParams({
            taskRecordId : taskRecordId
        });
        action.setCallback(this,function(response){
            var state = response.getState();
            var responseArray = response.getReturnValue();                       
            if(state == $A.get("$Label.c.CRMRetail_SUCCESS_HUM")){ 
                if(!$A.util.isUndefinedOrNull(responseArray[$A.get("$Label.c.CRMRetail_isWarning_Key")])){
                    this.generateErrorMessage($A.get("$Label.c.CRMRetail_Warning_Text"), responseArray[$A.get("$Label.c.CRMRetail_isWarning_Key")], $A.get("$Label.c.CRMRetail_Warning_Variant"));
                    $A.get("e.force:closeQuickAction").fire();
                }
                else if(!$A.util.isUndefinedOrNull(responseArray[$A.get("$Label.c.CRMRetail_Automated_Task")])){   
                    this.preparePageNavigation(component);                    
                }
                else if(!$A.util.isUndefinedOrNull(responseArray[$A.get("$Label.c.CRMRetail_isError_Key")])){                    
                    this.generateErrorMessage($A.get("$Label.c.CRMRetail_Error_HUM"), responseArray[$A.get("$Label.c.CRMRetail_isError_Key")], $A.get("$Label.c.CRMRetail_Error_variant"));                                                                
                    $A.get("e.force:closeQuickAction").fire();
                }
                else{
                    $A.get('e.force:refreshView').fire();
                    var sErrorMessage = $A.get("$Label.c.CRM_Retail_Task_Closed");
                    this.generateErrorMessage($A.get("$Label.c.CRM_Retail_Waiver_Functionality_Expiration_Success"), sErrorMessage, $A.get("$Label.c.CRMRetail_Success_Variant"));                    
                    if(!$A.util.isUndefinedOrNull(responseArray[$A.get("$Label.c.CRMRetail_NotificationData")])){                               
                        component.set("v.notificationData",responseArray[$A.get("$Label.c.CRMRetail_NotificationData")]);
                        component.set("v.isNotificationModalActive",true);                                                         
                    }else{
                        $A.get("e.force:closeQuickAction").fire();                        
                    }
                }  
            }else{              
                var sErrorMessage = $A.get("$Label.c.CRM_Retail_Error_Updating_Task");
                this.generateErrorMessage($A.get("$Label.c.CRMRetail_Error_HUM"), sErrorMessage, $A.get("$Label.c.CRMRetail_Error_variant"));
                $A.get("e.force:closeQuickAction").fire();
            } 
            component.set("v.Spinner",false);
        });
        $A.enqueueAction(action);               
    },
    preparePageNavigation: function(component) {                  
        var editRecordEvent = $A.get("e.force:editRecord");                 
        editRecordEvent.setParams({
            "recordId": component.get("v.recordId"),            
        });
        editRecordEvent.fire();                                   
    },
    ackNotification:function(component,event){
        var notificationData=event.getParam("data");
        if(notificationData.ack){
            if(notificationData.waiverDate){
                component.set("v.isWaiverDateRecAvailable",true);
            }
            var lstOfRecToUpdate=[];
            lstOfRecToUpdate.push(notificationData);
            var lstOfAccountIds =[];
            lstOfAccountIds.push(notificationData.accId);
            component.set("v.listOfAccountIds",lstOfAccountIds);               
            component.set("v.currLocation",notificationData.currentLocation);  
        }else
        {
            component.set("v.notificationData",null);
            component.set("v.isNotificationModalActive",false); 
            $A.get("e.force:closeQuickAction").fire();            
        }
        if(lstOfRecToUpdate !== null && lstOfRecToUpdate !== undefined && lstOfRecToUpdate.length > 0){
            component.set("v.Spinner",true);
            var action = component.get("c.acknowledgeNotifications");
            action.setParams({
                inputJSON: JSON.stringify(lstOfRecToUpdate),
                accIds : lstOfAccountIds,
                currentLocation : component.get("v.currLocation")
            });
            action.setCallback(this, function(response) {
                if(response.getState() === $A.get("$Label.c.CRMRetail_SUCCESS_HUM")) {
                    var isSuccess = response.getReturnValue();                    
                    var sErrorMessage = "";
                    if(isSuccess) {
                        if(component.get("v.isWaiverDateRecAvailable")){
                            sErrorMessage = $A.get("$Label.c.CRM_Retail_Waiver_Success_Text");
                            this.generateErrorMessage($A.get("$Label.c.CRM_Retail_Waiver_Functionality_Expiration_Success"), sErrorMessage, $A.get("$Label.c.CRMRetail_Success_Variant"));
                        }
                    }
                    else {
                        sErrorMessage = $A.get("$Label.c.CRM_Retail_Waiver_Failure_Text");
                        this.generateErrorMessage($A.get("$Label.c.CRM_Retail_Waiver_Expiration_Functionality_Error"), sErrorMessage, $A.get("$Label.c.CRMRetail_Error_variant"));  
                    }
                }
                else {
                    this.generateErrorMessage($A.get("$Label.c.CRM_Retail_Signed_Error_text"), this.processApexErrorMessage(response), $A.get("$Label.c.CRMRetail_Error_variant"));
                }
                component.set("v.Spinner",false);
                component.set("v.listOfAccountIds",[]);
                component.set("v.notificationData",null);
                component.set("v.isNotificationModalActive",false);
                $A.get("e.force:closeQuickAction").fire();
            });
            $A.enqueueAction(action); 
        }
    },
    
    generateErrorMessage: function(errTitle, errMessage, msgType) {
        var sMode = (msgType === 'error') ? 'sticky' : 'dismissible';
        var toastEvent = $A.get("e.force:showToast");
        toastEvent.setParams({
            "title": errTitle,
            "message": errMessage,
            "type": msgType,
            "mode": sMode
        });
        toastEvent.fire();
    },
    processApexErrorMessage: function(response) {
        var errors = response.getError();        
        var errMsg = '';
        if (errors) {
            if (errors[0] && errors[0].message) {
                errMsg = errors[0].message;
            }
        } else {
            errMsg = "Unknown error";
        }
        return errMsg;
    },
})