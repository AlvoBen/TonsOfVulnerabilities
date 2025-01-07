export class ReleaseOrderRequest_DTO {  

    constructor(orderId,enterpriseId,userId,copayconsent,arrayPrescription,cardkey,paymentDescription) { 
        this.sourceApplication = 'CRM',
        this.organization = 'HUMANA',
        this.userId = userId,
        this.requestTime = new Date().toISOString(),
        this.organizationPatientId = enterpriseId,
        this.order = new Order_DTO_HUM(orderId,copayconsent,arrayPrescription,cardkey,paymentDescription);
    }
}

class Order_DTO_HUM {
    constructor(orderId,copayconsent,arrayPrescription,cardkey,paymentDescription) {
        if(copayconsent){
            this.orderId =orderId,
            this.billing = new Billing_DTO_HUM(cardkey,paymentDescription);
            this.lines = new Lines_DTO_HUM(copayconsent,arrayPrescription);
        }
        else{ 
            this.orderId =orderId, 
            this.lines = new Lines_DTO_HUM(copayconsent,arrayPrescription);
        }
    }
}

class Billing_DTO_HUM {
    constructor(cardkey,paymentDescription) {
        this.payment = new Payment_DTO_HUM(cardkey,paymentDescription);
    }
}

class Payment_DTO_HUM {
    constructor(cardkey,paymentDescription) {
        if(paymentDescription ==='BILL PAYMENT LATER'){
            this.card = {
                "key": 111
              }
        }
        else if(paymentDescription ==='PAYMENT CARD'){
            this.card = {
                "key": cardkey
              }
        }
          
    }

}

class Lines_DTO_HUM{
    constructor(copayconsent,arrayPrescription) {
        let tmp = [];
        arrayPrescription.forEach (h => {
       tmp.push(new OrderLines_DTO_HUM(copayconsent,h.key));
    })
        return tmp;
    }
}

class OrderLines_DTO_HUM{
    constructor(copayconsent,key) {
        if(copayconsent === true){ 
            this.copay = {
                "consent": copayconsent
              }
            this.product = new Product_DTO_HUM(key); 
        }
        else{
            this.product = new Product_DTO_HUM(key);
        }
    }
}

class Product_DTO_HUM {
    constructor(key) {
          this.prescription = {
            "prescriptionKey": key
          }
    }

}
