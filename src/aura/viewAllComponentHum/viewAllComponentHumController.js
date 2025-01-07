({
	doInit : function(component, event, helper) {
        if(component.get('v.pageReference')){
         var pageRef = component.get('v.pageReference');
         component.set('v.viewAllData',JSON.parse(pageRef.state.c__data));
        }
    }
})