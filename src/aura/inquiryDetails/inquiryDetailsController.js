({
	doInit : function(component, event, helper) {
         var pageRef = component.get('v.pageReference');
         component.set('v.inquiryDetailsData',JSON.parse(pageRef.state.c__data));
         console.log('inquiryDetails',component.get('v.inquiryDetailsData'));
    }
})