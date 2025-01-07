({
    openPhonebook: function (component, event, helper) {
        if (event != null) {
            var openPhonebookUtil = component.find('utilitybar');
            openPhonebookUtil.openUtility();
        }
    },

    openutility: function (component, event, helper) {
        var utilityAPI = component.find("utilitybar");
        utilityAPI.getAllUtilityInfo().then(function (oUtils) {
            let sCtiUtilId = '';
            oUtils.forEach(oEl => {
                if (oEl.utilityIcon == 'call') sCtiUtilId = oEl.id;
            });
            if (sCtiUtilId != '') {
                utilityAPI.openUtility({ utilityId: sCtiUtilId });
            }
        }).catch(function (error) {
            console.log('Error Occurred', error);
        });
    },

    handleNavigateToAboutPage: function (component, event, helper) {
        let aboutRecordid = event.getParam('accIntIds').aboutId;
        let sintId = event.getParam('accIntIds').sInteracId;
       var workspaceAPI = component.find("workspace"); 
        workspaceAPI.openTab({
            url: `#/sObject/${aboutRecordid}/view?c__interactionId=${sintId}&c__bOnActiveCall=true`,
            focus: true
        });
    },

    disablepopout: function (component, event, helper) {
        var popoutval = event.getParam('popoutval');
        var utilityAPI = component.find("utilitybar");
        utilityAPI.disableUtilityPopOut({
            disabled: popoutval,
            disabledText: "Pop-out is disabled"
        });
    }
})