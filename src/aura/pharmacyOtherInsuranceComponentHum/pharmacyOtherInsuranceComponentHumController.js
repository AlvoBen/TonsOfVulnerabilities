({
	doInit : function(component, event, helper) {
         var pageRef = component.get('v.pageReference');
         component.set('v.data',JSON.parse(pageRef.state.c__data));
    }
})