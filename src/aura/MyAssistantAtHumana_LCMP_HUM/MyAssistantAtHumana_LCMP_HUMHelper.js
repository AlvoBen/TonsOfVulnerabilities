({
    getNavigationContext:function(component){
        var workspaceAPI = component.find("workspace");
        workspaceAPI.getFocusedTabInfo()
        .then(function(response) {     
            if(Object.keys(response).length > 0) {
                if(response.pageReference.attributes.objectApiName == 'Account') {
                    component.set("v.accountId", response.pageReference.attributes.recordId);
                    component.find('myAssistantAtHumana').checkHeaderDisplayAccount();
                }
                else if(response.pageReference.attributes.objectApiName =='MemberPlan') {
                    const memberPlanId = response.pageReference.attributes.recordId;
                    workspaceAPI.getTabInfo( {tabId: response.parentTabId } )
                    .then(function(response) {
                        component.set("v.accountId", response.pageReference.attributes.recordId);
                        component.set("v.memberPlanId", memberPlanId);
                        var openMemberPlanTabs = 0;
                        for(let tab of response.subtabs) {
                            if(tab.pageReference.attributes.objectApiName) {
                                if(tab.pageReference.attributes.objectApiName == "MemberPlan") {
                                    openMemberPlanTabs++;
                                }
                            }
                        }
                        if(openMemberPlanTabs > 1) {
                            component.set("v.multipleMemberPlansOpen", true);
                        }
                        else {
                            component.set("v.multipleMemberPlansOpen", false);
                        }
                    })
                    .catch(function(error) {
                        console.log(error);
                    });
                    component.find('myAssistantAtHumana').checkHeaderDisplayMemberPlan();
                }
                else if(response.pageReference.state.c__tabdetails == 'protectedAccount') {
                    component.set("v.accountId", response.pageReference.state.c__dataList);
                    component.find('myAssistantAtHumana').checkHeaderDisplayAccount();
                }
                else {
                    if('v.popOutState') {
                        component.set("v.accountId", null);
                        component.set("v.memberPlanId", null);
                        component.find('myAssistantAtHumana').hideHeaderDisplay();
                    }
                }
            }
            else {
                if('v.popOutState') {
                    component.set("v.accountId", null);
                    component.set("v.memberPlanId", null);
                    component.find('myAssistantAtHumana').hideHeaderDisplay();
                }
            }
        })
        .catch(function(error) {
            console.log("Error in getAccountNavigationContext()" + error);
        });  
    },

    inOnOffSwitch: function(component,event,helper){
        var action = component.get("c.getMyAHOnOffSwitchBooleanValue");
        action.setCallback(this,function(response){
            var state = response.getState();
            if(state === 'SUCCESS'){
                var switch5029083 = response.getReturnValue();
                if(!switch5029083){
                    var utilityAPI = component.find("utilitybar");
                    utilityAPI.disableUtilityPopOut({
                        disabled: true,
                        disabledText: "Pop-out is disabled"
                    });
                }
                component.set("v.switch5029083", switch5029083);
            }
            else {
               
                console.log('Error fetching MyAH CRM Functionality On/Off Switch');
            }
        });
        $A.enqueueAction(action);
    }
})