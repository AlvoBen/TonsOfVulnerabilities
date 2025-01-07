/********************************************************************************************************************************
File Name       : CaseProcess_LCMP_HUMController.js
Version         : 1.0
Created On      : 24/06/2021
Function        : This is javascript controller for CaseProcess_LCMP_HUM component

* Modification Log:
* Developer Name            Code Review                 Date                       Description
******************************************************************************************************************************
* Santhi Mandava                                        07/28/2022                  Original version 
******************************************************************************************************************************/
({
    init: function (component, event, helper) {
        var pageReference = component.get("v.pageReference");
        var scaseId = pageReference.state.c__caseNo;
        component.set("v.caseId", scaseId);
        var sflowData = pageReference.state.c__flowData;
        component.set("v.flowDetails", sflowData);
        var sFlowName = pageReference.state.c__flowName;
        var bTabSwitch = pageReference.state.c__tabCloseSwitch;
        var workspaceAPI = component.find("workspace");

        workspaceAPI.getEnclosingTabId().then(function(response) {
            workspaceAPI.setTabLabel({
            tabId: response,
            label: sFlowName
            });
            workspaceAPI.setTabIcon({
                tabId: response,
                icon: "standard:case",
                iconAlt: sFlowName
            });
            workspaceAPI.disableTabClose({
                tabId: response,
                disabled: bTabSwitch
            });
        });
    }
});