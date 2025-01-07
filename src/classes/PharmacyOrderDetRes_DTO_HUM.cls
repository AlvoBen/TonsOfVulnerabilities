/**************************************************************************** 
Apex Class Name :  PharmacyOrderDetRes_DTO_HUM   
Version: 1.0  
Created Date     : March 28, 2017
Function         : Wrapper class for OrderDetRes Pharmacy service 
****************************************************************************
Modification Log: 
* Developer Name          Code Review #         Date                       Description
*------------------------------------------------------------------------------------------------------------
* Avinash Kumar Choubey                      01/09/2017                 REQ-301369 DTO class for OrderDetRes
* Sharan Shanmugam							 07/27/2017					REQ - 323542 : Exception handling variable
***************************************************************************************************/
Public Class PharmacyOrderDetRes_DTO_HUM
{
    Public RightSourceGetOrderDetResponse_DTO_HUM.GetOrderDetailResponse  objParentOrder {Get;Set;}
    Public RightSourceGetOrderDetResponse_DTO_HUM.GetOrderDetailResponse  objChildOrder {Get;Set;}    
    Public String  orderLevel {Get;Set;}
    Public String  networkID {Get;Set;}
    Public String  sRecordId {Get;Set;}
    
    /**
    * Returns None
    * <p>
    * Constructor for the class , Initialize variables
    * 
    * @param  RightSourceGetOrderDetResponse_DTO_HUM.GetOrderDetailResponse  objParentOrder Parent Order Details from First Call
    * @param  RightSourceGetOrderDetResponse_DTO_HUM.GetOrderDetailResponse  objChildOrder Child Order Details from Second Call
    * @param  String  orderLevel to identify parent/child order
    * @return  None
    */
    Public PharmacyOrderDetRes_DTO_HUM(RightSourceGetOrderDetResponse_DTO_HUM.GetOrderDetailResponse  objParentOrder, RightSourceGetOrderDetResponse_DTO_HUM.GetOrderDetailResponse  objChildOrder, String  orderLevel, string networkID, String sRecordId )
    {
      this.objParentOrder =  objParentOrder ;
      this.objChildOrder = objChildOrder;
      this.orderLevel = orderLevel;
      this.networkID = networkID;
      this.sRecordId = sRecordId;
    }
}