/**************************************************************************** 
Apex Class Name  : PharmacyEditMember_Order_DTO_HUM 
Version          : 1.0  
Created Date     : Feb 16, 2017
Function         : Wrapper class for GetMember Pharmacy service to store Order
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*------------------------------------------------------------------------------------------------------------
* Sumit Pharande            345615              03/27/2019                 Original Version
***************************************************************************************************/
public class PharmacyEditMember_Order_DTO_HUM 
{
  public List<PharmacyEditMember_Order_DTO_HUM.PharmacyEditMember_Order_wrapper_HUM> Orders= new List<PharmacyEditMember_Order_DTO_HUM.PharmacyEditMember_Order_wrapper_HUM>(); 
     public class PharmacyEditMember_Order_wrapper_HUM
     {  
        public String orderKey='';
        public String orderStatus= '';
     }
        
}