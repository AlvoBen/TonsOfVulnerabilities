export class CreateOrderDTO {
    constructor(enterpriseId, networkId, orderKey, orderReleaseDate, deliveryInstruction, moveToRouting,
        orderPlatform,address,paymentMethods,creditCard,scriptOrders,shippingMethodCode,overrideQueueConsent) {
        this.EnterprisePersonID = enterpriseId;
        this.CustomerServiceId = networkId;
        this.orderKey = orderKey;
        this.orderReleaseDate = orderReleaseDate;
        this.deliveryInstruction = deliveryInstruction;
        this.moveToRouting = moveToRouting;
        this.orderPlatform = orderPlatform;
        this.address = new AddressDTO(address);
        this.paymentMethod = paymentMethods;
        if(creditCard != null){
            this.creditCard = creditCard;
        }
		if(overrideQueueConsent){
            this.overrideQueueConsent = overrideQueueConsent;
        } 
        this.scriptOrders = scriptOrders;
        this.shippingMethodCode = shippingMethodCode;
    }
}

export class AddressDTO{
    constructor(address){
            this.addressKey = "-1";
            this.addressLine1 = address.AddressLine1;
            this.addressLine2 = address.AddressLine2;
            this.addressName = address.AddressName;
            this.addressType = "S";
            this.city = address.City;
            this.isActive = "true";
            this.overrideReasonCode = "";
            this.stateCode = address.StateCode;
            this.uspsValidate = "true",
            this.zipCode = address.ZipCode;
    }
}