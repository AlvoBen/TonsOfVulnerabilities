({
        doInit: function(component, event, helper) {
            helper.minimizeUtility(component);
        },
     
        onStatusChanged : function(component, event, helper) {
            
        var statusId = event.getParam('statusId');
        var channels = event.getParam('channels');
        var statusName = event.getParam('statusName');
        var statusApiName = event.getParam('statusApiName');
    
        component.set("v.status",statusName);

     if (statusName != 'Available' && statusName != undefined && statusName != 'Offline' && statusName != 'Online' && statusName != 'Away')
        {
            
            helper.setResetTimeOnUI(component);
            helper.openFirstUtility(component);
            helper.setStartTimeOnUI(component);
            component.set("v.displaytime",true);
           
        }
        else{
        
            helper.setResetTimeOnUI(component);
            helper.minimizeUtility(component);
            component.set("v.displaytime",false);
    } 
        },
        onLogout : function(component, event, helper) {
            helper.setResetTimeOnUI(component);
            component.set("v.displaytime",false);
        }, 
})