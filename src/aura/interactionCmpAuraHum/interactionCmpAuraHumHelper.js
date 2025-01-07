({    
    createObj: function(product, price) {
        var obj = new Object();
        obj.product = product;
        obj.price = price;
        return obj;
    },
    createAllTabObj: function(tabId, parentTabId, subtabs,  isSubtab) {
        var obj = new Object();
        obj.parentTabId = parentTabId;
        obj.tabId = tabId;
        obj.subtabs = subtabs;
        obj.isSubtab = isSubtab;
        return obj;
    },
    createparentonlyObj2: function(tabId, parentTabId, subtabs,  objectApiName) {
        var obj = new Object();
        obj.parentTabId = parentTabId;
        obj.tabId = tabId;
        obj.subtabs = subtabs;
        obj.objectApiName = objectApiName;
        return obj;
    },
    createValidObj: function(tabId, allTabObj) {
        var obj = new Object();
        obj.tabId = tabId;
        obj.allTabObj = allTabObj;
        return obj;
    },
    createValTabObj: function(tabId, parentTabId) {
        var obj = new Object();
        obj.parentTabId = parentTabId;
        obj.tabId = tabId;
        return obj;
    },
    createparentonlyObj: function(tabId, parentTabId, objectApiName) {
        var obj = new Object();
        obj.parentTabId = parentTabId;
        obj.tabId = tabId;       
        obj.objectApiName = objectApiName;
        return obj;
    },
    createChildObj: function(tabId, parentTabId, parentobjectApiName, objectApiName) {
        var obj = new Object();
        obj.parentTabId = parentTabId;
        obj.tabId = tabId;
        obj.parentobjectApiName = parentobjectApiName;
        obj.objectApiName = objectApiName;
        return obj;
    },
    handleRefreshedIntObj: function (component, message, firingtabid1, firingtabParentid1, currentTabId1, currentTabParentId1) {
        var firingtabParentid = '';
        const firingtabid = firingtabid1;
        const currentTabId = currentTabId1;
        var currentTabParentId = currentTabParentId1;
        const subtabs = [];       
        message.map((el) => {
            if(el.key === "firingTabId") {
                firingtabParentid = el.value;
            }
            if(el.key === "subtabs") {
                subtabs.push(el.value);                
            }
        })
        if (currentTabParentId === firingtabParentid) {
            component.find('interactionCmpHum').handleInteractionFieldChanges1(message);
        }
        
        if(subtabs.length > 0) {
            subtabs[0].map((el) => {
                if(currentTabId === el.tabId) {
                    component.find('interactionCmpHum').handleInteractionFieldChanges1(message);
                }
            })
        }
    },
    createInitObj: function(tabId, parentTabId, subtabs, objectApiName) {
        var obj = new Object();
        obj.parentTabId = parentTabId;
        obj.tabId = tabId;
        obj.subtabs = subtabs;
        obj.objectApiName = objectApiName;
        return obj;
    }
})