export class CancelOrderRequest_DTO { 
  
    constructor(orderKey, EnterpriseId,userId) { 
        this.sourceApplication = 'CRM',
        this.organization = 'HUMANA',
        this.userId = userId,
        this.requestTime = new Date().toISOString(),
        this.organizationPatientId = EnterpriseId,
        this.order = new Order_DTO_HUM(orderKey);
    } 
}

class Order_DTO_HUM {  
    constructor(orderKey) {
        this.orderId =orderKey,
        this.cancelOrder = true
        
    }
}