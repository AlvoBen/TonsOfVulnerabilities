({
    doInit : function(component, event, helper) {
        let myPageRef = component.get("v.pageReference");
        let origRecId = myPageRef.state.c__recordId;
            component.set("v.recordId", origRecId);
        let position = myPageRef.state.ws.search("c__interactionId");
        let len = myPageRef.state.ws.length;
        let interactionId = myPageRef.state.ws.substring(position+len, position+17);
        if (position == -1)interactionId = '';
            var action1 = component.get("c.getUserCurrentQueue");
            action1.setCallback(this, function(response) {
                var state = response.getState();
                if(state === "SUCCESS") {
                   
                    let currentQueue = response.getReturnValue();
                    component.set("v.userQueue", currentQueue);
                    if(currentQueue == null || currentQueue == ''){
                        component.set("v.loaded", false);
                        var toastEvent = $A.get("e.force:showToast");
                        toastEvent.setParams({
                            message: 'Current User is not assigned to any Queue',
                            type:'Error',
                            mode:'Pester'
                        }),
                        toastEvent.fire();
                    }
                }
                });
                $A.enqueueAction(action1);

            var action = component.get("c.cloneCase");
            action.setParams({recordId: origRecId, interactionId: interactionId});
            action.setCallback(this, function(response){
                var state = response.getState();
                if(response.getReturnValue() !=null){
                if(state == 'SUCCESS') {
                    let newRecId = response.getReturnValue();
                    component.set("v.cloneRecId", newRecId);
                   if(newRecId !=null){
                    var workspaceAPI = component.find("workspace");
                    var focusedTab = workspaceAPI.getFocusedTabInfo();
                        var casedata = {};
                        casedata.Id = newRecId;
                        casedata.objApiName = 'Case';
                        var componentDef = {
                            componentDef: "c:caseInformationComponentHum",        
                            attributes: {encodedData:casedata }};
                        var encodedComponentDef = btoa(JSON.stringify(componentDef));
                        workspaceAPI.openSubtab({
                            parentTabId:focusedTab.parentTabId,  
                            url: "/one/one.app#" + encodedComponentDef,
                            focus:true
                        }).then(function(response) {
                            workspaceAPI.closeTab({ tabId: focusedTab.tabId })
                            .then(function(res){
                                workspaceAPI.focusTab({ tabId: response });
                            })
                        })
                        component.set("v.loaded", false);
                   }
                    
                }
           }
            });
           $A.enqueueAction(action);

    }
})