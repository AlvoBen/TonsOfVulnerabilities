({ 
    initiate: function(component) {
        component.set("v.transactionComplete", false);
        this.triggerAOREvaluate(component);
    	
    },
    triggerAOREvaluate: function(component) {
        var recordTypeName = component.get("v.simpleRecord").RecordType.Name;
        if(recordTypeName === "Member") {
            this.triggerAOR(component);
        }
        else {
            component.set("v.AORNumber", "N/A");
            component.set("v.AORBody", "N/A");
            component.set("v.showError", false);
            var elem$ = component.find("cardBodyDiv");
            $A.util.removeClass(elem$, "bodyBackground");
            component.set("v.showBody", true);
            component.set("v.transactionComplete", false);
        }
    },
    triggerAOR: function(component) {
        var action = component.get("c.retrieveAORDetails");
        action.setParams({
            accountId: component.get("v.recordId"),
            accEnterpriseId: component.get("v.simpleRecord").Enterprise_ID__c
        });
        action.setCallback(this, function(response) {
            this.processApexResponse(component, response);
        });
        $A.enqueueAction(action);
    },
    initiateRefresh: function(component) {
    	component.set("v.showError", false);
        component.set("v.showBody", false);
        component.set("v.refreshCounter", component.get("v.refreshCounter") + 1);
        this.initiate(component);
    },
    handleBodyChange: function(component) {
        var transactionValue = component.get("v.transactionComplete");
        if(component.get("v.refreshCounter") > 1) {
            transactionValue = false;
        }
        component.set("v.refreshDisabled", !transactionValue);
    },
    processApexResponse: function(component, response) {
    	if(response.getState() === "SUCCESS") {
            var resultObj = response.getReturnValue();
            var aorName = "";
            var aorNum = "";
            component.set("v.transactionComplete", false);
            if(resultObj["Data"]["State"] === "SUCCESS") {
           		aorName = resultObj["Data"]["AORName"];
                aorNum = resultObj["Data"]["AORNum"];
            }
            else if(resultObj["Data"]["State"] === "ERROR") {
                component.set("v.transactionComplete", true);
            }
            component.set("v.AORNumber", aorNum);
            component.set("v.AORBody", aorName);
            component.set("v.showError", false);
            var elem$ = component.find("cardBodyDiv");
            $A.util.removeClass(elem$, "bodyBackground");
            component.set("v.showBody", true);
        }
        else {
            var sErrorMessage = this.processApexErrorMessage(response);
            component.set("v.showBody", false);
            component.set("v.showError", true);
            component.set("v.AORErrorBody", sErrorMessage);
            var elem1$ = component.find("cardBodyDiv");
            $A.util.addClass(elem1$, "bodyBackground");
            component.set("v.transactionComplete", true);
        }
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
    }
})