({
    waitingTimeId: null,
	setStartTimeOnUI : function(component) {
        component.set("v.ltngIsDisplayed",true);
        var currTime =component.get("v.ltngCurrTime");
        var ss = currTime.split(":");
        var dt = new Date();
        dt.setHours(ss[0]);
        dt.setMinutes(ss[1]);
        dt.setSeconds(ss[2]);
        
        var dt2 = new Date(dt.valueOf() + 1000);
        var temp = dt2.toTimeString().split(" ");
        var ts = temp[0].split(":");
        
        component.set("v.ltngCurrTime",ts[0] + ":" + ts[1] + ":" + ts[2]);
        this.waitingTimeId =setTimeout($A.getCallback(() => this.setStartTimeOnUI(component)), 1000);
    
    },
    minimizeUtility : function(component, event, helper) {
        var utilityAPI = component.find("utilitybar");
        utilityAPI.getAllUtilityInfo().then(function(response) {
            var data = JSON.parse(JSON.stringify(response));
            for (var i = 0; i < data.length; i++) {
           if (data[i].utilityLabel == "Omni Timer") {
           
        utilityAPI.minimizeUtility({
                utilityId : data[i].id
            });
        }
    }
        })
    },
       
        openFirstUtility : function(component, event, helper) {
            
            var utilityAPI = component.find("utilitybar");
            utilityAPI.getAllUtilityInfo().then(function(response) {

                var data = JSON.parse(JSON.stringify(response));
                for (var i = 0; i < data.length; i++) {
               if (data[i].utilityLabel == "Omni Timer") {
               
            utilityAPI.openUtility({
                    utilityId : data[i].id
                });
            }
        }            
           })
            .catch(function(error) {
                console.log(error);
            });
        },
        setResetTimeOnUI : function(component) {
            component.set("v.ltngIsDisplayed",false);
            component.set("v.ltngCurrTime","00:00:00");
            window.clearTimeout(this.waitingTimeId);
        },
        
    
})