export class CreateAndEditOrderRequest_DTO {
    constructor(orderKey, EnterpriseId, isCancel, networkID, overrideconsent, copayconsent, lstScriptKey, moveToRouting) {        
        if (isCancel) {
           this.CreateAndEditOrderRequest = {
                EnterprisePersonID : EnterpriseId,
                CustomerServiceId : networkID,
                orderKey : orderKey,
                cancelOrder : 'true',
                orderPlatform : 'trad'
            }
        }
        else{
            this.CreateAndEditOrderRequest = {
                EnterprisePersonID : EnterpriseId,
                CustomerServiceId : networkID,
                orderKey : orderKey
            }
            if (overrideconsent) {
                this.CreateAndEditOrderRequest.userDefined1 = lstScriptKey;
                this.CreateAndEditOrderRequest.overrideQueueConsent = overrideconsent;
                let oScriptOrders = {};
                if (copayconsent) {
                    let script = new ScriptObject_DTO(lstScriptKey,copayconsent);
                    oScriptOrders.script = [];
                    oScriptOrders.script.push(script);
                    this.CreateAndEditOrderRequest.scriptOrders = oScriptOrders;
                }else{
                    let script = new ScriptObject_DTO(lstScriptKey,false);
                    let oScriptOrders = {};
                    oScriptOrders.script = [];
                    oScriptOrders.script.push(script);
                    this.CreateAndEditOrderRequest.scriptOrders = oScriptOrders;
                }
            }else{ 
                this.CreateAndEditOrderRequest.moveToRouting = moveToRouting;
            }
        }
    }
}

export class MoveToRouting_DTO {
    constructor(orderNumber)
    {
        this.orderKey = [orderNumber];
    }
}

export class ScriptObject_DTO {
    constructor(scriptKey, coPayConsent) {
        if(coPayConsent === true){ 
            this.scriptKey = scriptKey;
            this.coPayConsent = coPayConsent;
        }
        else{ 
            this.scriptKey = scriptKey;
        }
        
    }   
}