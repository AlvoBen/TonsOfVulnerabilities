({
    fireRowClick : function(component, event, helper) {
        var myRowCLick = $A.get("e.c:PharmacyRxClaimsSummaryRowClickEvent");
        myRowCLick.setParams({
            ClaimID : event.getParam('ClaimID'),
            AuthorizationNumber:event.getParam('AuthorizationNumber')
        })
        myRowCLick.fire();
    }
})