/********************************************************************************************************************************
File Name       : dynamicCustomLinkConsoleHumController.js
Version         : 1.0
Created On      : 24/06/2021
Function        : to open the subtab when message recieved via message channel

* Modification Log:
* Developer Name            Code Review                 Date                       Description
******************************************************************************************************************************
* Ranadheer Alwal                                      06/24/2021                  Original version
* Ranadheer Alwal                                      10/20/2021                  PRJ0002606-Link parameter is not passed from link framework when a LWC component is opened in a subtab
* Ranadheer Alwal                                      12/07/2021                  T1PRJ0002606- MF6 - SF - TECH - DF-3873 Link parameter is not passed from link framework when a LWC component is opened in a subtab 
* Suraj patil                             			   03/10/2022                  REQ - 2917779 -Passing Multiple Parameters  from link framework when a LWC component is opened in a subtab
* Sagar G                                              06/23/2022                  User Story 3366783:MF 2922840 -  CRM Service Billing Systems Integration: Lightning- Billing/Member Summary & Details-Billing logging- Details auto pop
* Rajesh Narode                                        07/13/2022                  US-3362694,3409976  
******************************************************************************************************************************/
({
    handleClose: function (component, message, helper) {
        var workspaceAPI = component.find("workspace");
        workspaceAPI.getFocusedTabInfo().then(function (response) {
            var focusedTabId = response.tabId;
            workspaceAPI.openSubtab({                                               
                parentTabId: focusedTabId,
                url: '/lightning/r/'+message.getParam("recordId")+'/view',
                focus: true
            }).then(function (responseSubTab) {
                
            });

        }).catch(function (error) {
            
        });
        workspaceAPI.getFocusedTabInfo().then(function(response) {
            var focusedTabId = response.tabId;
            workspaceAPI.closeTab({tabId: focusedTabId});
        })
        .catch(function(error) {
        });
    },

    handleChanged: function (component, message, helper) {
        // Read the message argument to get the values in the message payload
        component.set("v.url", message.getParam("url"));
        component.set("v.tabname", message.getParam("tabname"));
        let componentname = '';
        let stateJson = {};
        let isLwc = false;
        try {
            for(var i=0; i<message.getParam("url").split('?').length; i++){
                if(i == 1){
                    componentname = 'c:'+message.getParam("url").split('?')[1];
                }else if(i > 1){
                    //adding for multiple parameters 
                    for(var j=0; j< message.getParam("url").split('?')[i].split('&').length; j++)
                    {
                        let property = message.getParam("url").split('?')[i].split('&')[j].split('=')[0];
                        stateJson[property] =  message.getParam("url").split('?')[i].split('&')[j].split('=')[1];
                    }

                }else if(i == 0 && message.getParam("url").split('?')[0] == 'lwc'){
                    isLwc = true;
                }   
            }
        } catch (error) {
        }
                var compDefinition = {
            componentDef: componentname,
            attributes: stateJson
        };
        // Base64 encode the compDefinition JS object
        var encodedCompDef = btoa(JSON.stringify(compDefinition));
        var workspaceAPI = component.find("workspace");
        workspaceAPI.getFocusedTabInfo().then(function (response) {
            var focusedTabId = response.tabId;
            if (isLwc) {
                    workspaceAPI
                        .openSubtab({
                            parentTabId: focusedTabId,
                            url: '/one/one.app#' + encodedCompDef,
                            focus: !message.getParam('bfocussubtab')
                        })
                        .then(function (responseSubTab) {
                            workspaceAPI.setTabLabel({
                                tabId: responseSubTab,
                                label: component.get('v.tabname')
                            });
                        });
                } else {
                    workspaceAPI
                        .openSubtab({
                            parentTabId: focusedTabId,
                            pageReference: {
                                type: 'standard__navItemPage',
                                attributes: {
                                    apiName: message
                                        .getParam('url')
                                        .split('/')[3]
                                        .split('?')[0],
                                    url: component.get('v.url')
                                },
                                state: {
                                    c__UnqState: component.get('v.tabname')
                                }
                            },
                            focus: !message.getParam('bfocussubtab')
                        })
                        .then(function (responseSubTab) {
                            workspaceAPI.setTabLabel({
                                tabId: responseSubTab,
                                label: component.get('v.tabname')
                            });
                        });
                }

        }).catch(function (error) {
            
        });
    }
})