({
    selectedRecords : function(component, event, helper) {
        var selectRecName = event.getParam('selRecords');
        if(selectRecName != undefined) {
            component.set("v.selectedRecords", selectRecName);
        }
    }
})