({
	doInit : function(component, event, helper) {
        try {
        	helper.initiate(component);
        }
        catch(e) {
            helper.generateErrorMessage("Component Error", e.message, "error");
            component.set("v.transactionComplete", true);
        }
    },
    onRefresh: function(component, event, helper) {
        try {
        	helper.initiateRefresh(component);
        }
        catch(e) {
            helper.generateErrorMessage("Component Error", e.message, "error");
            component.set("v.transactionComplete", true);
        }
    },
    triggerBodyChange: function(component, event, helper) {
        try {
        	helper.handleBodyChange(component);	
        }
        catch(e) {
            helper.generateErrorMessage("Component Error", e.message, "error");
            component.set("v.transactionComplete", true);
        }
    }
})