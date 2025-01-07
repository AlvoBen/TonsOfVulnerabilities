({
	doInit : function(component, event, helper) {      
        try{           
            helper.doInit(component);            
        }
        catch(e) {
            helper.generateErrorMessage("Component Init Error", e.message, "error");
        }         
	},
    ackNotification : function(component, event, helper) {	        
        helper.ackNotification(component,event);  
	} 
})