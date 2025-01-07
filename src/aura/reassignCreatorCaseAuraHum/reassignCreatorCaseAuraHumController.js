({
	doInit : function(component, event, helper) {
		let irctc = component.get("v.iRTC");
        component.set("v.iRTC", irctc++);
	},
    
    closeQuickAction : function(component, event, helper)
    {
        $A.get('e.force:refreshView').fire(); // to refresh detail page
    	$A.get('e.force:closeQuickAction').fire(); 
		
	}

})