(
    {
        onRender: function (component, event, helper) {
            var utilityAPI = component.find("utilitybar");
            utilityAPI.isUtilityPoppedOut().then(function (response) {
                if (response === true) {
                    utilityAPI.disableUtilityPopOut({
                        disabled: true,
                        disabledText: "Pop-in is disabled"
                    });
                    component.set("v.popOutState", true);
                }
                component.find('isUtilityPoppedOut').set('v.value', response);
            })
                .catch(function (error) {
                    console.log(error);
                });

        },
        getValueFromLwc: function (component, event, helper) {
            component.set("v.businessName", event.getParam('BusinessGroup'));
            component.set("v.lwcLoaded", true);
            if (event.getParam('BusinessGroup') && event.getParam('BusinessGroup') != '') {
                component.set("v.pretextPresent", true);
            }

        },
        doInit : function(component, event, helper) {
            var action = component.get("c.getQSAccess");
            action.setCallback(this, function(response) {
                var state = response.getState();
                if (state === "SUCCESS") {
                    if(response.getReturnValue() == true){
                        component.set("v.showQS", true);
                    }else{
                        component.set("v.qsAccessMsg", true);
                    }
                }
            });
            $A.enqueueAction(action);
        },
        handleResetClick: function (component, event, helper) {
            component.set("v.resetBtnClick", "Clicked-" + Date.now());
        }

    }
)