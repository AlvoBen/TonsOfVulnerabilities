({
	myAction : function(component, event, helper) {
		
	},
     
    closeQuickAction : function(component, event, helper)
    {
        $A.get('e.force:refreshView').fire(); // to refresh detail page
    	$A.get('e.force:closeQuickAction').fire(); 
		
	}
})