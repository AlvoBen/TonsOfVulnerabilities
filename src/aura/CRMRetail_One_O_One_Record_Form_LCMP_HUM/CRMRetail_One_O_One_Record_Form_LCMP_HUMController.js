({
    doInit : function(component,event,helper){        
        var switchAction = component.get("c.fetchSwitchResults");
        switchAction.setCallback(this, function(response){
            if(response.getState() === "SUCCESS")
            {                
                component.set("v.SwitchMap",response.getReturnValue()); 
                component.set("v.loadInfoCard",true);                                
                if(component.get("v.SwitchMap").Switch_3434998){
                    component.set("v.switch_3434998",true);
                }                
            }
            
            component.set("v.loadLocation",true);            
        });
        $A.enqueueAction(switchAction);
    },
    handlerRefresh :function(component, event, helper) {            	
        if(component.get("v.SwitchMap").Switch_3434998){
            var retailEvent = $A.get("e.c:EventHandler_CRMRetail_LCMP_HUM");
            retailEvent.setParams({
            	"eventOrigin": "closeOOOModalWithSuccess"
        	});
        	retailEvent.fire();
    	}
        else{
            $A.get('e.force:refreshView').fire();                       
        }
    },
    closeModal :function(component, event, helper) { 
        var retailEvent = $A.get("e.c:EventHandler_CRMRetail_LCMP_HUM");
        retailEvent.setParams({
            "eventOrigin": "closeOOOModal"
        });
        retailEvent.fire();
    },
})