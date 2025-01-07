export class CreateOrderRequest_DTO_HUM {
    constructor(userId, personCode,shippingMethodCode,releaseDate,creditKey,shippingKey,billingKey,paymentDate,arrayPrescription,paymentMethodCode,paymentMethodDesc) {
        this.sourceApplication = 'CRM', 
        this.userId = userId,
        this.requestTime = new Date().toISOString(), 
        this.organizationPatientId = personCode,
        this.organization = 'HUMANA', 
        this.order = new Order_DTO_HUM(shippingMethodCode,releaseDate,creditKey,shippingKey,billingKey,paymentDate,arrayPrescription,paymentMethodCode,paymentMethodDesc);
    }
}

class Order_DTO_HUM {
    constructor(shippingMethodCode,releaseDate,creditKey,shippingKey,billingKey,paymentDate,arrayPrescription,paymentMethodCode,paymentMethodDesc) {
        this.type = new code_DTO_HUM();
        this.billing = new Billing_DTO_HUM(creditKey,billingKey,paymentDate,paymentMethodCode,paymentMethodDesc);
        this.shipping = new Shipping_DTO_HUM(shippingMethodCode,shippingKey);
        this.lines = new Lines_DTO_HUM(arrayPrescription);
    }
}

class code_DTO_HUM{ 
    constructor(){
        this.code = 29 
    }
}

class Lines_DTO_HUM{
    constructor(arrayPrescription) {
        let tmp = [];
        arrayPrescription.forEach (h => {
        tmp.push(new OrderLines_DTO_HUM(h.fillNumber,h.prescriptionKey));
    })
        return tmp;
    }
}

class OrderLines_DTO_HUM{
    constructor(fillNumber,prescriptionKey) {
        
        this.product = new Product_DTO_HUM(fillNumber,prescriptionKey);
    }
}

class Product_DTO_HUM{
    constructor(fillNumber,prescriptionKey){
        this.prescription = {
            "prescriptionKey": prescriptionKey,
            "fillNumber" : fillNumber
          }
    }
}

class Billing_DTO_HUM {
    constructor(creditKey,billingKey,paymentDate,paymentMethodCode,paymentMethodDesc) {
        this.address = new Address_DTO_HUM(billingKey);
        this.payment = new Payment_DTO_HUM(creditKey,paymentDate,paymentMethodCode,paymentMethodDesc);
    }
}

class Address_DTO_HUM {
    constructor(billingKey) {
       this.key = billingKey
    }
}

class Payment_DTO_HUM {
    constructor(creditKey,paymentDate,paymentMethodCode,paymentMethodDesc) {
        this.method = {
            "code": paymentMethodCode,
            "description": paymentMethodDesc
          }
          this.card = {
            "key": creditKey
          }
    }

}

class Shipping_DTO_HUM {
    constructor(shippingMethodCode,shippingKey) {
        this.method = {
            "code": Number(shippingMethodCode)
          };
        this.priority = true;
        this.address = new Address_DTO_HUM(shippingKey);
    }
}