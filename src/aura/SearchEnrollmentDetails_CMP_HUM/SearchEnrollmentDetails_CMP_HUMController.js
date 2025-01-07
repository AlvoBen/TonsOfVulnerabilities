/**
Aura component Name        : SearchEnrollmentDetails_CMP_HUM.js
Function        : To Open enrollment member record in seperate tab

Modification Log:
* Developer Name                     Date                         Description
* Muthukumar                         07/29/2022                   US-3255798 Original Version 
* Muthukumar                         09/29/2022                   US-3398943 Home office/ Contract protected data implementation
**/
({
    init: function (component, event, helper) {
        
        var pageReference = component.get("v.pageReference");
        var accDetailId = pageReference.state.c__accountDetailId;
        var dataList = pageReference.state.c__dataList;
		var tabdetails =pageReference.state.c__tabdetails;
        component.set("v.accountDetailId", accDetailId);
        component.set("v.memberData", dataList);
		component.set("v.tabdetails", tabdetails);
        var workspaceAPI = component.find("workspace");
        
        workspaceAPI.getEnclosingTabId().then(function(response) {
            
            workspaceAPI.setTabLabel({
            tabId: response,
            label: accDetailId //set label you want to set
            });
         workspaceAPI.setTabIcon({
                tabId: response,
                icon: "standard:account", //set icon you want to set
                iconAlt: accDetailId //set label tooltip you want to set
            });
         
        })
    }
});