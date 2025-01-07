({
    getEnclosingTabId: function (component, event, helper) {
        var workspaceAPI = component.find("workspace");
        var allTabObjList = [];
        var currentTabId;        				
                workspaceAPI.getEnclosingTabId().then(function (tabId) {
                
                    currentTabId = tabId;
                    component.set("v.currentTabId", currentTabId);
                })
                .then(function(response){
                    workspaceAPI.getAllTabInfo().then(function (response) {                
                        var allTabFn = response.forEach(function (arrayItem) {
                            allTabObjList.push(
                                helper.createparentonlyObj2(
                                    arrayItem.tabId,
                                    arrayItem.parentTabId,
                                    arrayItem.subtabs,
                                    arrayItem.pageReference.attributes.objectApiName
                                )
                            );
                            component.set("v.allTabObj", allTabObjList);
                        });
        
                        var tempAllTabs = component.get("v.allTabObj");
                        var tempcurtbaid = component.get("v.currentTabId");
        
                        var onlyparentTabsList = [];
                        var childTabsMap = [];

                        tempAllTabs.map((el) => {
                          var tempobj = [];
                          tempobj.push(helper.createInitObj(el.tabId, el.parentTabId, el.subtabs, 
                            el.objectApiName));                            
                            if(el.subtabs.length > 0) {
                              el.subtabs.map((elx) => {
                                tempobj.push(helper.createInitObj(elx.tabId, elx.parentTabId, elx.subtabs, 
                                  elx.objectApiName));
                              })
                            }
                            tempobj.map((x)=> {
                              if(x.tabId === tempcurtbaid) {
                                if(x.parentTabId) {
                                  component.set("v.currentTabParentId", x.parentTabId)
                                }

                                var subTabLength = x.subtabs ? x.subtabs.length : 0;
                                if(subTabLength > 0) {
                                  component.set("v.subtabs", x.subtabs)
                                }
                              }
                            });                                                  
                        })
        
                        tempAllTabs.forEach((el) => {
                            var subTabLength = el.subtabs.length ? el.subtabs.length : 0;
                            var subtabs = el.subtabs;
                            if(subTabLength) {
                                onlyparentTabsList.push(helper.createparentonlyObj(el.tabId, el.parentTabId, 
                                    el.objectApiName));
                                                                 
                                subtabs.forEach((elx) => {
                                    childTabsMap.push(helper.createChildObj(elx.tabId, elx.parentTabId, 
                                        el.objectApiName, elx.pageReference.attributes.objectApiName));                                
                                })                        
                            } else if (!subTabLength) {
                                onlyparentTabsList.push(helper.createparentonlyObj(el.tabId, el.parentTabId, 
                                    el.objectApiName));
                            }
                        })
        
                        onlyparentTabsList.map((el) => {
                            if(tempcurtbaid === el.tabId ) {
                                if(el.objectApiName === "Account") {
                                    component.set("v.setMeOnInit", true);
                                } else {
                                    component.set("v.setMeOnInit", false);
                                }
                            }
                        })
                        if(childTabsMap)
                        {
                            childTabsMap.map((el) => {
                            if(tempcurtbaid === el.tabId ) {
                                if(el.parentobjectApiName === "Account") {
                                    component.set("v.setMeOnInit", true);
                                } else {
                                    component.set("v.setMeOnInit", false);
                                }
                            }
                        })
                    }
                    }).then(() => {
                        workspaceAPI.getFocusedTabInfo().then(function(response) {
                            var focusedTabId = response.tabId;
                            var focusedParentTabId = response.parentTabId;                            
                            var childShownBool = component.get("v.setMeOnInit");
                            if(focusedParentTabId && childShownBool) {
                              component.set("v.currentTabParentId", focusedParentTabId)
                                var appEvent = $A.get("e.c:interactionAuraEvent");
                                appEvent.setParams({
                                    "message" : {focusedTabId: focusedTabId,
                                    focusedParentTabId: focusedParentTabId }});
                                appEvent.fire();
                            }
                       })
                        .catch(function(error) {
                            console.log("error" + JSON.stringify(error));
                        });

                    })
                    .catch(function (error) {
                        console.log("error" + JSON.stringify(error));
                    });
                })
                .catch(function (error) {
                    console.log("error" + JSON.stringify(error));
                });                                      
    },
    
    handleReceiveMessage: function (component, event, helper) {
        if (event != null) {
            
            var workspaceAPI = component.find("workspace");
            const message = event.getParam("handlechangeobj");

            const messageKey = event.getParam("handlechangeobj").map( el => el.key);
            const messageValue = event.getParam("handlechangeobj").map( el => el.value);
            const recordId = component.get("v.recordId");
                if (messageKey[0].startsWith("001")) {
                  if (messageKey[0] === recordId) {
                    component.set("v.handlechangeobj", message);
                    var subtabs = component.get("v.subtabs");
                    if(subtabs.length > 0) {
                      setTimeout(() => {
                        component.find('interactionCmpHum').createIntObj(true, message.focusedParentTabId, subtabs);
                      }, 1000)                       
                    }      
                  }
                } else if (messageKey[0] === "intLmsObj") {
                let messageValue = event.getParam("handlechangeobj").map( el => el.value);
                var firingtabid = event.getParam("firingTabId");
                var firingtabParentid = event.getParam("firingtabParentid");
                var currentTabId = component.get("v.currentTabId");  
                var currentTabParentId = component.get("v.currentTabParentId");             
                helper.handleRefreshedIntObj(component, messageValue[0], firingtabid, firingtabParentid, 
                     currentTabId, currentTabParentId);            
            } else {
              var setMessage = false;
              const firingtabid = event.getParam("firingTabId");
              const firingtabParentid = event.getParam("firingtabParentid");
              var currentTabId;
              var allTabObj = [];
              var validTabObj = {};

              workspaceAPI
                .getEnclosingTabId()
                .then(function (tabId) {
                  currentTabId = tabId;
                  component.set("v.currentTabId", currentTabId);
                })
                .then(function (response) {
                  workspaceAPI
                    .getAllTabInfo()
                    .then(function (response) {
                      var allTabFn = response.forEach(function (arrayItem) {
                        allTabObj.push(
                          helper.createAllTabObj(
                            arrayItem.tabId,
                            arrayItem.parentTabId,
                            arrayItem.subtabs,
                            arrayItem.isSubtab
                          )
                        );
                        component.set("v.allTabObj", allTabObj);
                      });
                    })
                    .then(function (response) {
                      var tempAllTabs = component.get("v.allTabObj");
                      var tempcurtbaid = component.get("v.currentTabId");
                      validTabObj = helper.createValidObj(
                        tempcurtbaid,
                        tempAllTabs
                      );

                      const ctabId = validTabObj.tabId;
                      const calltabobj = validTabObj.allTabObj;

                      var tSubTabList = [];
                      var tvalidTabList = [];

                      calltabobj.forEach(function (arrayItem) {
                        var subTabLength = arrayItem.subtabs.length
                          ? arrayItem.subtabs.length
                          : 0;
                        if (subTabLength > 0) {
                          tvalidTabList.push(
                            helper.createValTabObj(
                              arrayItem.tabId,
                              arrayItem.parentTabId
                            )
                          );

                          tSubTabList.push(arrayItem.subtabs);
                          tSubTabList.forEach(function (arrayItem) {
                            const aItem = arrayItem;
                            aItem.forEach(function (arrayItem) {
                              tvalidTabList.push(
                                helper.createValTabObj(
                                  arrayItem.tabId,
                                  arrayItem.parentTabId
                                )
                              );
                            });
                          });
                        } else if (subTabLength === 0) {
                          tvalidTabList.push(arrayItem);
                        }
                      });
                      var tabFamilyIds = [];

                      tvalidTabList.forEach((item) => {
                        var itemsId = item.tabId ? item.tabId : "";
                        var itemsPId = item.parentTabId ? item.parentTabId : "";

                        if (itemsPId) {
                          if (
                            firingtabid === itemsId ||
                            firingtabParentid === itemsId ||
                            firingtabid === itemsPId ||
                            firingtabParentid === itemsPId
                          ) {
                            tabFamilyIds.push(itemsId);
                          }
                        } else if (!itemsPId) {
                          if (
                            itemsId === firingtabid ||
                            itemsId === firingtabParentid
                          ) {
                            tabFamilyIds.push(itemsId);
                          }
                        }
                      });

                      setMessage = tabFamilyIds.includes(tempcurtbaid)
                        ? true
                        : false;
                      if (setMessage) {
                        component.set("v.handlechangeobj", message);
                      }
                    })
                    .catch(function (error) {
                      console.log("error" + JSON.stringify(error));
                    });
                })
                .catch(function (error) {
                  console.log("error" + JSON.stringify(error));
                });
            }            
        }
    },
        
    getCaseTabBool: function (component, event, helper) {
        var workspaceAPI = component.find("workspace");
        workspaceAPI
        .getFocusedTabInfo()
        .then(function (response) {
            var currentTabRecId = response.pageReference.attributes.recordId ? response.pageReference.attributes.recordId : "";
            var currentTabObjName = response.pageReference.attributes.objectApiName ? response.pageReference.attributes.objectApiName : "";
            if (currentTabObjName === "Case") {
                component.set("v.isCaseTab", true);
                component.set("v.caseRecordId", currentTabRecId);
            } else {
                component.set("v.isCaseTab", false);
            }
        })
        .catch(function (error) {
            console.log("error" + JSON.stringify(error));
        });
    },

    handleApplicationEvent: function (component, event, helper) {
        var message = event.getParam("message");
        const currentTabId = component.get("v.currentTabId");
        const currentTabParentId = component.get("v.currentTabParentId");
        const requestorTabId = message.focusedTabId;
        const requestorParenTabId = message.focusedParentTabId;
        if(message.focusedParentTabId === currentTabId) {
            component.find('interactionCmpHum').createIntObj(true, message.focusedParentTabId);                   
        } else if(message.messageData) {
            if(currentTabParentId === message.currentTabId){
              setTimeout(() => {
                component.find('interactionCmpHum').handleInteractionFieldChanges1(message.messageData);
              }, 1500);            
            }
        }
    }, 

    handleSendDataToChild: function (component, event, helper) {
      var currentTabId = component.get("v.currentTabId");
      var currentTabSubTabs = component.get("v.subtabs");
     var messageData = event.getParam('value');
     var appEvent = $A.get("e.c:interactionAuraEvent");
                                appEvent.setParams({
                                    "message" : {messageData : messageData,
                                      currentTabId: currentTabId,
                                      subtabs: currentTabSubTabs}});
                                appEvent.fire();
  }
});