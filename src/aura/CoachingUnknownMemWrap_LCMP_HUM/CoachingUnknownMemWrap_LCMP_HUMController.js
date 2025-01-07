({
    /**
     * init function 
     * @param {*} component
     * @param {*} event
     * @param {*} helper
     */
    doInit : function(component, event, helper) {
        var workspaceAPI = component.find("workspace");
        //set the Tab name as Unknown Account
        workspaceAPI.getFocusedTabInfo().then(function(response) {            
            var focusedTabId = response.tabId;
            workspaceAPI.setTabLabel({
                tabId: focusedTabId,
                label: "Unknown Account"
            });
            
            //remove the loading icon from the tab.
            workspaceAPI.setTabIcon({
            tabId: focusedTabId,
            icon: "standard:person_account"            
            });            
        });
    },

    /**
     * function to close the Tab.
     * @param {*} component
     * @param {*} event
     * @param {*} helper
     */
    handleCloseTab : function(component, event, helper) {
        var workspaceAPI = component.find("workspace");
        //get the focused TabId to close the tab
        workspaceAPI.getFocusedTabInfo().then(response => {            
            var focusedTabId = response.tabId;
            //close the console tab
            workspaceAPI.closeTab({
                tabId : focusedTabId
            });
        });
    },

    /**
     * function to close the current tab and open the account detail page in new tab
     * @param {*} component
     * @param {*} event
     * @param {*} helper
     */
    handleSaveEvent : function(component, event, helper) {
        var workspaceAPI = component.find("workspace");

        //get the focused TabId to close the tab
        workspaceAPI.getFocusedTabInfo().then(function(response) {            
            var focusedTabId = response.tabId;        
            var acctId = event.getParam('accId');  
                
            if(acctId) {    
                //open new tab
                workspaceAPI.openTab({
                    recordId : acctId,
                    focus : true
                }).then(response => {            
                    //close the console tab
                    workspaceAPI.closeTab({
                        tabId : focusedTabId
                    });
                });
            }
        });                                                           
    },

})