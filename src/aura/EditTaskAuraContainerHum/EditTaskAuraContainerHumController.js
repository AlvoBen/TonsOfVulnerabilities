({
    doInit:function(){
   		componet.get("v.recordId");
  	},
    
    closeQA : function() {         
        $A.get("e.force:closeQuickAction").fire();
		$A.get("e.force:refreshView").fire();           
	}
})