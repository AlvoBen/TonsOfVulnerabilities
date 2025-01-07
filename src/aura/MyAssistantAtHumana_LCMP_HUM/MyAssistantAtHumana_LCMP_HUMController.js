(
    {
        doInit : function(component, event, helper) {
            helper.inOnOffSwitch(component);
            var action = component.get("c.getAiChatAccess");
            action.setCallback(this, function(response) {
                var state = response.getState();
                if (state === "SUCCESS") {
                    if(response.getReturnValue() == true) {
                        component.set("v.hasChatAssistantUser", true);
                    }
                    else {
                        var utilityAPI = component.find("utilitybar");
                        utilityAPI.disableUtilityPopOut({
                            disabled: true,
                            disabledText: "Pop-out is disabled"
                        });
                    }
                }
            });
            $A.enqueueAction(action);
        }, 

        onRender: function (component, event, helper) {
            helper.getNavigationContext(component);
            var utilityBarAPI = component.find("utilitybar");
            utilityBarAPI.isUtilityPoppedOut()
            .then(function(response) {
                component.set("v.popOutState", response);      
            })
            .catch(function(error) {
                console.log(error);
            });          
        },

        onTabFocused: function (component, event, helper) {
            helper.getNavigationContext(component);
        },

        onTabCreated: function (component, event, helper) {
            helper.getNavigationContext(component);
        },

        onTabClosed: function (component, event, helper) {
            helper.getNavigationContext(component);
        }
    }
)