/*
Function        : LWC PharmacyHpieOrderDetailsEditHum.

Modification Log:
* Developer Name                  Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Jonathan Dickinson              02/29/2024                    Initial Version
*****************************************************************************************************************************/
export class UpdateOrderRequest_DTO_HUM {
    constructor(userId, personCode,orderId,shippingMethodCode,releaseDate,creditKey,shippingKey,billingKey,paymentDate,priscriptions,isCPAY_250, paymentMethodCode, paymentMethodDescription ) {
        this.sourceApplication = 'CRM',
        this.userId = userId,
        this.requestTime = new Date().toISOString(),
        this.organizationPatientId = personCode,
        this.organization = 'HUMANA',
        this.order = new Order_DTO_HUM(orderId,shippingMethodCode,releaseDate,creditKey,shippingKey,billingKey,paymentDate,priscriptions,isCPAY_250, paymentMethodCode, paymentMethodDescription, );
    }
}

class Order_DTO_HUM {
    constructor(orderId,shippingMethodCode,releaseDate,creditKey,shippingKey,billingKey,paymentDate,priscriptions,isCPAY_250, paymentMethodCode, paymentMethodDescription ) {
        this.orderId = orderId,
        this.pharmacyNPI = '',
        this.cancelOrder = false,
        this.cancelReasonCode= 0,
        this.releaseDate = releaseDate,
        this.billing = new Billing_DTO_HUM(creditKey, billingKey, paymentDate, paymentMethodCode, paymentMethodDescription);
        this.shipping = new Shipping_DTO_HUM(shippingMethodCode,shippingKey);
        this.lines = new Lines_DTO_HUM(priscriptions,isCPAY_250);
    }
}

class Lines_DTO_HUM{
    constructor(priscriptions,isCPAY_250) {
        let tmp = [];
        priscriptions.forEach (h => {
        tmp.push(new OrderLines_DTO_HUM(h.scriptKey,isCPAY_250, h.rxConsent));
    })
        return tmp;
    }
}
class OrderLines_DTO_HUM{
    constructor(key,isCPAY_250,memberConsent) {
        if (isCPAY_250 === true)
        {
            this.copay= {
                "consent": true
            };
        }
        if(memberConsent != null){ this.memberConsent = memberConsent;}
        this.product = new Product_DTO_HUM(key);
    }
}

class Product_DTO_HUM{
    constructor(key){
        this.prescription = {
            "prescriptionKey": key
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

class Billing_DTO_HUM {
    constructor(creditKey,billingKey,paymentDate, paymentMethodCode, paymentMethodDescription) {
        this.address = new Address_DTO_HUM(billingKey);
        this.payment = new Payment_DTO_HUM(creditKey,paymentDate, paymentMethodCode, paymentMethodDescription);
    }
}

class Address_DTO_HUM {
    constructor(addressKey) {
       this.key = addressKey
    }
}

class Payment_DTO_HUM {
    constructor(creditKey,paymentDate, paymentMethodCode, paymentMethodDescription) {
        this.method = {
            "code": paymentMethodCode,
            "description": paymentMethodDescription
          }
          this.date = paymentDate;
          this.card = {
            "key": creditKey
          }
    }

}