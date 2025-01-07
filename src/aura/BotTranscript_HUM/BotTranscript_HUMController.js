({
    onCustomEvent: function(cmp, evt, helper) {
        var conversation = cmp.find("conversationKit");
        var data = evt.getParam("data");
        var type = evt.getParam("type");
		var recordId = evt.getParam("recordId");
        
        console.log("type:" + type + "\n data:" + data);
        console.log("recordId:" + recordId);
        
        conversation.sendMessage({
            recordId: recordId,
            message: {
                text: data
            }
        })
        .then(function(result){
            if (result) {
                    console.log("Successfully sent message.");
                } else {
                    console.log("Failed to send message.");
                }
        });
    }
})