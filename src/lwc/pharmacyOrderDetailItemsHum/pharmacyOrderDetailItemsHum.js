/*
LWC Name        : pharmacyOrderDetailItemsHum.js
Function        : LWC to display pharmacy order edit item details.

Modification Log:
* Developer Name                  Date                         Description
*
* Abhishek Mangutkar              03/09/2022                   US - 3139633
* Nirmal Garg											03/15/2022										DF-4643 fix.
****************************************************************************************************************************/

import { LightningElement,api } from 'lwc';

    export default class PharmacyOrderDetailItemsHum extends LightningElement {

        @api orderprescription;       
        @api payer;
        @api orderparentnode;
        orderLineItems = [];
        splitOrderToID;
        orderChildLineItems = [];
		@api calledFromEditOrder;
		
        connectedCallback(){
            this.prepareOrderDetailLinesTableArray(this.orderparentnode,this.orderprescription); 
        }

        prepareOrderDetailLinesTableArray(result,presult){           
            let lstScriptKey='';
            let splitOrderFROM;
            let splitOrderTO;
            if(result != null && result!= undefined && result.OrderItems != null &&  result.OrderItems!= undefined )
            {
                for(var i=0;  i < result.OrderItems.length; i++)
                {
                    let itemRow = result.OrderItems[i];
                    if(itemRow != null && itemRow != undefined && (itemRow.ChangedOrder == null || itemRow.ChangedOrder == undefined || itemRow.ChangedOrder.ChangedOrderType != "C" ) ) 
                    {   
                        let tempOrderItem  = {};
                        let writtenDrug ='';
                        let dispensedDrug = '';
                        if (itemRow != null && itemRow != undefined) {
                            for (var j = 0; j < itemRow.Items.length ; j++) {
                                let vItem = itemRow.Items[j];
                                if (vItem.ItemType == "W") writtenDrug = vItem.ItemLabel + ' (' + itemRow.Strength + ')';
                                else if (vItem.ItemType == "D") dispensedDrug = vItem.ItemLabel + ' (' + itemRow.Strength + ')';
                            }
                        
                            
                            if (itemRow.ScriptKey != null && itemRow.ScriptKey != undefined){
                            tempOrderItem.ScriptKey = itemRow.ScriptKey;
                            lstScriptKey = lstScriptKey + itemRow.ScriptKey + ',';
                            }
                            else tempOrderItem.ScriptKey = '';
    
                            
                            if (presult !== undefined && presult.length > 0) {  
                                presult.forEach(f => {
                                 
                                    if(null != tempOrderItem.ScriptKey && f.RXNumber === tempOrderItem.ScriptKey ){
                                        tempOrderItem.Quantity = f.Quantity;
                                        tempOrderItem.DaysSupply = f.DaysSupply;
                                        tempOrderItem.RefillsRemaining = f.RefillsRemaining;
                                        tempOrderItem.Icon = f.icon;
                                        tempOrderItem.checkrefilllength = f.RefillsRemaining != null && f.RefillsRemaining.toString().length >=2 ? true : false;
                                    }
                                  
                                })		
                              }	
    
                            
                            if (itemRow.OrderItemStatusLiteral != null && itemRow.OrderItemStatusLiteral != undefined) tempOrderItem.OrderItemStatus = itemRow.OrderItemStatusLiteral;
                            else tempOrderItem.OrderItemStatus = '';  
                                                
                            tempOrderItem.writtenDrug = writtenDrug;
                            tempOrderItem.dispensedDrug = dispensedDrug;
                        
                            
                            if (itemRow.CoPayCost != null && itemRow.CoPayCost != undefined && itemRow.CoPayCost.ItemAmount != null && itemRow.CoPayCost.ItemAmount != undefined) tempOrderItem.CoPayCost = itemRow.CoPayCost.ItemAmount;
                            else tempOrderItem.CoPayCost = '';					
                            
                            
                            if (itemRow.CoPayCost != null && itemRow.CoPayCost != undefined && itemRow.CoPayCost.TaxAmount != null && itemRow.CoPayCost.TaxAmount != undefined) tempOrderItem.TaxAmount = itemRow.CoPayCost.TaxAmount;
                            else tempOrderItem.TaxAmount = '';

                            if(itemRow.ChangedOrder != null && itemRow.ChangedOrder != undefined && itemRow.ChangedOrder.ChangedOrderType == "P"  ){
                                if(!splitOrderFROM){
                                    tempOrderItem.splitOrderFrom = true;
                                    splitOrderFROM = itemRow.ChangedOrder.ChangedOrderKey;
                                    this.splitOrderFromID = itemRow.ChangedOrder.ChangedOrderKey; 
                                    tempOrderItem.splitFromOrder = itemRow.ChangedOrder.ChangedOrderKey ;
                                }
                                
                            }
                           
                        }
                        
                        this.orderLineItems.push(tempOrderItem)
                    }
                    else if(itemRow.ChangedOrder != null || itemRow.ChangedOrder != undefined || itemRow.ChangedOrder.ChangedOrderType == "C"){
                        let tempOrderItem  = {};
                        let writtenDrug ='';
                        let dispensedDrug = '';
                        if (itemRow != null && itemRow != undefined) {
                            for (var j = 0; j < itemRow.Items.length ; j++) {
                                let vItem = itemRow.Items[j];
                                if (vItem.ItemType == "W") writtenDrug = vItem.ItemLabel + ' (' + itemRow.Strength + ')';
                                else if (vItem.ItemType == "D") dispensedDrug = vItem.ItemLabel + ' (' + itemRow.Strength + ')';
                            }
                        
                            
                            if (itemRow.ScriptKey != null && itemRow.ScriptKey != undefined){
                            tempOrderItem.ScriptKey = itemRow.ScriptKey;
                            lstScriptKey = lstScriptKey + itemRow.ScriptKey + ',';
                            }
                            else tempOrderItem.ScriptKey = '';
    
                            
                            if (presult !== undefined && presult.length > 0) {  
                                presult.forEach(f => {
                                 
                                    if(null != tempOrderItem.ScriptKey && f.RXNumber === tempOrderItem.ScriptKey ){
                                        tempOrderItem.Quantity = f.Quantity;
                                        tempOrderItem.DaysSupply = f.DaysSupply;
                                        tempOrderItem.RefillsRemaining = f.RefillsRemaining;
                                        tempOrderItem.Icon = f.icon;
                                        tempOrderItem.checkrefilllength = f.RefillsRemaining != null && f.RefillsRemaining.toString().length >=2 ? true : false;
                                    }
                                  
                                })		
                              }	
    
                            
                            if (itemRow.OrderItemStatusLiteral != null && itemRow.OrderItemStatusLiteral != undefined) tempOrderItem.OrderItemStatus = itemRow.OrderItemStatusLiteral;
                            else tempOrderItem.OrderItemStatus = '';  
                                                
                            tempOrderItem.writtenDrug = writtenDrug;
                            tempOrderItem.dispensedDrug = dispensedDrug;
                        
                            
                            if (itemRow.CoPayCost != null && itemRow.CoPayCost != undefined && itemRow.CoPayCost.ItemAmount != null && itemRow.CoPayCost.ItemAmount != undefined) tempOrderItem.CoPayCost = itemRow.CoPayCost.ItemAmount;
                            else tempOrderItem.CoPayCost = '';					
                            
                            
                            if (itemRow.CoPayCost != null && itemRow.CoPayCost != undefined && itemRow.CoPayCost.TaxAmount != null && itemRow.CoPayCost.TaxAmount != undefined) tempOrderItem.TaxAmount = itemRow.CoPayCost.TaxAmount;
                            else tempOrderItem.TaxAmount = '';

                            
                            if(itemRow.ChangedOrder != null && itemRow.ChangedOrder != undefined && itemRow.ChangedOrder.ChangedOrderType == "C"  ){
                               if(!splitOrderTO){
                                    tempOrderItem.splitOrderTo = true;
                                    splitOrderTO = itemRow.ChangedOrder.ChangedOrderKey;
                                    this.splitOrderTOID = itemRow.ChangedOrder.ChangedOrderKey; 
                                    tempOrderItem.splitToOrder = itemRow.ChangedOrder.ChangedOrderKey ;
                                }
                            }
                        }
                        
                        this.orderLineItems.push(tempOrderItem)
                    }
                }
            }
    
            
        }

        splitOrderFunctionality(event){
           
            this.dispatchEvent(new CustomEvent('splitorderclick',{
                detail : {
                    OrderNumber : event.target.dataset.id 
                }
            }));
        }
		
		handleDeleteScriptKey(event) {
			let scriptKey = event.detail.deletescriptkey;			
			this.orderLineItems = this.orderLineItems.filter(o => o.ScriptKey !== event.detail.deletescriptkey);
			this.dispatchEvent(new CustomEvent('deletescriptkeyfromlist', {
				detail: {
					deletescriptkey: event.detail.deletescriptkey
				}
			}));
		}
       
}