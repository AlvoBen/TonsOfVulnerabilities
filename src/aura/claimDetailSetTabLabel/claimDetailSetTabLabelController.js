/********************************************************************************************************************************
File Name       : claimDetailSetTabLabelController.js
Version         : 1.0
Created On      : 24/06/2021
Function        : to open the subtab when message recieved via message channel

* Modification Log:
* Developer Name            Code Review                 Date                       Description
******************************************************************************************************************************
* Vamshi Krishna Pemberthi                             03/10/2022                  REQ - 2917779 -Passing Multiple Parameters  from link framework when a LWC component is opened in a subtab
******************************************************************************************************************************/
({
    handleChanged: function (component, message, helper) {
        // Read the message argument to get the values in the message payload
        component.set("v.claimnumber", message.getParam("claimNumber"));
        var workspaceAPI = component.find("workspace");
        workspaceAPI.getFocusedTabInfo().then(function (response) {
            var focusedTabId = response.tabId;
            workspaceAPI.setTabLabel({
                tabId: focusedTabId,
                label: 'Claim-' + component.get("v.claimnumber")
            })
                
        }).catch(function (error) {
            
        });
    }
})