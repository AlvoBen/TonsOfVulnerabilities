/*
LWC Name        : PharmacyOrderCardHum.js
Function        : LWC to display pharmacy order cards.

Modification Log:
* Developer Name                  Date                         Description
*
* Pallavi Shewale				  12/10/2021					US-2664846 Original Version
* Swapnali Sonawane               12/05/2022                    US- 3969790 Migration of the order queue detail capability
****************************************************************************************************************************/
import { LightningElement,api, wire } from 'lwc';
import invokeGetOrder from '@salesforce/apexContinuation/Pharmacy_LC_HUM.getOrderData';
import invokeGetOrderDetails from '@salesforce/apexContinuation/PharmacyOrderDetail_LC_HUM.getOrderDetails';


export default class PharmacyOrderCardHum extends LightningElement {
    @api item;
    @api enterpriseid;
    @api networkid;
    @api recordid;
    @api startdate;
    @api enddate;
    @api totalamount;
    orderDetails;
    prescritionDetails;
    capType;

    viewOrderRxPanelData;


    handleViewRX()
    {
        
        invokeGetOrderDetails({orderKeyValue: this.item.OrderNumber, source: this.item.OrderSource,startDate:this.startdate, endDate: this.enddate,enterpriseId:this.enterpriseid,networkID: this.networkid,sRecordId:this.recordid})
        .then(result =>{
            
            let response =  JSON.parse(result);
            let orderItems = response.OrderItems;
            let orderData = new Object();
            let odrData = [];
            orderItems.forEach(o => {                         
                if(o.ChangedOrder.ChangedOrderTypeLiteral == null) 
                {                     
                    orderData = {
                        ScriptKey: o.ScriptKey,
                        OrderItemStatusLiteral: o.OrderItemStatusLiteral,
                        Items: o.Items                                         
                    }
                    odrData.push(orderData);
                }
                if(o.ChangedOrder.ChangedOrderTypeLiteral == 'PARENT ORDER' || o.ChangedOrder.ChangedOrderTypeLiteral == 'MERGED ORDER' || o.ChangedOrder.ChangedOrderTypeLiteral == 'INVENTORY OUT-OF-STOCK')
                {
                    orderData = {
                        ScriptKey: o.ScriptKey,
                        OrderItemStatusLiteral: o.OrderItemStatusLiteral,
                        Items: o.Items                                             
                    }
                    odrData.push(orderData);
                }                                          
            });	
            this.dispatchEvent(new CustomEvent("viewrx", {
                detail : {
                    orderData : odrData,
                    orderKey : this.item.OrderNumber
                }
            }))
        }).catch(error =>{
            console.log(error);
        })
        
    }


    processGetOrder(calledFrom)
    {
  
        invokeGetOrder({orderKeyValue: this.item.OrderNumber, source: this.item.OrderSource,startDate:this.startdate, endDate: this.enddate,enterpriseId:this.enterpriseid,networkID: this.networkid,sRecordId:this.recordid})
        .then(result => {
            
            let response =  JSON.parse(result);
                let orderItems = response.objParentOrder.OrderItems;
                let orderData = new Object();
                let odrData = [];
                if(calledFrom === 'ViewRx')
                {
                    orderItems.forEach(o => {                         
                        if(o.ChangedOrder.ChangedOrderTypeLiteral == null) 
                        {                     
							orderData = {
								ScriptKey: o.ScriptKey,
								OrderItemStatusLiteral: o.OrderItemStatusLiteral,
								Items: o.Items                                         
							}
							odrData.push(orderData);
                        }
                        if(o.ChangedOrder.ChangedOrderTypeLiteral == 'PARENT ORDER' || o.ChangedOrder.ChangedOrderTypeLiteral == 'MERGED ORDER' || o.ChangedOrder.ChangedOrderTypeLiteral == 'INVENTORY OUT-OF-STOCK')
                        {
                            orderData = {
                                ScriptKey: o.ScriptKey,
                                OrderItemStatusLiteral: o.OrderItemStatusLiteral,
                                Items: o.Items                                             
                            }
							odrData.push(orderData);
                        }                                          
                    });	
                }
                else
                {
                    orderItems.forEach(o => {                         
                        orderData = {
                            ScriptKey: o.ScriptKey,
                            OrderItemStatusLiteral: o.OrderItemStatusLiteral,
                            Items: o.Items                                         
                        }
                        odrData.push(orderData);                  
                    });
                }	
            
       })
        .catch(err => {
            console.log("Error occured - " + err);
        });       
    }


    onOrderIdClick(event) {
     
        this.dispatchEvent(new CustomEvent('orderidclickselect',{
            detail : {
                OrderNumber : event.target.dataset.id 
            }
        }));

      
    }

}