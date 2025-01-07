/******************************************************************************************************************
Apex Class Name :   Pharmacy_RightSourceDTO_T_HUM 
Version: 1.0
Function           : Test CLass to Cover RightSource DTO class
Created On         : October 28 2017

Modification Log:  
*   Version             Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------                
*    1.0                Vishal Verma                142419             10/28/2017           Original Version -> REQ - 335046
*                   	Priyanka Puranik                               05/05/2021           Prod Turn Test Class Coverage fix for 
																							RightSourceEditMemberResponse_DTO_HUM class
------------------------------------------------------------------------------------------------------------------*/
@isTest 
Private Class Pharmacy_RightSourceDTO_T_HUM
{

	/*
	* Method Name : setUpTestData
	* Description : This method creates test data For test Class
	* Return type : None
	* Parameter   : None
	*/																											
    @testSetup static void setUpTestData()
    {
        User objUser = HUMTestDataHelperTest.createTestUser('User1','Humana Pharmacy Specialist');   
        insert objUser;
    }
	
	/*
	* Method Name : tesRSDTO
	* Description : This method used To Test DTO For Right Source
	* Return type : None
	* Parameter   : None
	*/	
	Static testMethod void tesRSDTO()
	{
		User testUser = [Select Id, Name from User where LastName='test User1'];
		System.runAs(testUser)
		{
			Test.StartTest();
				RightSourceEditMemberResponse_DTO_HUM objEditMemDTO = New RightSourceEditMemberResponse_DTO_HUM();
				RightSourceEditMemberResponse_DTO_HUM.EditMemberResponse objEditMemRespDTO = New RightSourceEditMemberResponse_DTO_HUM.EditMemberResponse();
				System.assert(objEditMemRespDTO.transactionKey == '');
				
				RightSourceEditScriptResponse_DTO_HUM objEditScriptDTO = New RightSourceEditScriptResponse_DTO_HUM();
				RightSourceEditScriptResponse_DTO_HUM.EditScriptResponse objEditScriptRespDTO = New RightSourceEditScriptResponse_DTO_HUM.EditScriptResponse();
				System.assert(objEditScriptRespDTO.transactionKey == '');
				
				RightSourceGetListRequest_DTO_HUM objGetListDTO = New RightSourceGetListRequest_DTO_HUM();
				RightSourceGetListRequest_DTO_HUM.ListRequest objGetListReqDTO = New RightSourceGetListRequest_DTO_HUM.ListRequest();
				System.assert(objGetListReqDTO.ListType == '');
				
				RightSourceGetListResponse_DTO_HUM objGetListResp = New RightSourceGetListResponse_DTO_HUM();
				RightSourceGetListResponse_DTO_HUM.ListResponse objGetListRespDTO = New RightSourceGetListResponse_DTO_HUM.ListResponse();
				RightSourceGetListResponse_DTO_HUM.ListDetails objGetListDetailsDTO = New RightSourceGetListResponse_DTO_HUM.ListDetails();
				RightSourceGetListResponse_DTO_HUM.ListDetail objGetListDetailDTO = New RightSourceGetListResponse_DTO_HUM.ListDetail();
				System.assert(objGetListDetailDTO.ListItemKey == '');
				
				RightSourceGetMemberResponse_DTO_HUM objGetmemResp = New RightSourceGetMemberResponse_DTO_HUM();
				RightSourceGetMemberResponse_DTO_HUM.MemberReponse objmemResp = New RightSourceGetMemberResponse_DTO_HUM.MemberReponse();
				RightSourceGetMemberResponse_DTO_HUM.CustomersDetail objCustsDetResp = New RightSourceGetMemberResponse_DTO_HUM.CustomersDetail();
				RightSourceGetMemberResponse_DTO_HUM.CustomerDetail objCustDetResp = New RightSourceGetMemberResponse_DTO_HUM.CustomerDetail();
				RightSourceGetMemberResponse_DTO_HUM.CustomerDetails objCustDets = New RightSourceGetMemberResponse_DTO_HUM.CustomerDetails();
				RightSourceGetMemberResponse_DTO_HUM.CustomerIdentityDetail objCustIdentityResp = New RightSourceGetMemberResponse_DTO_HUM.CustomerIdentityDetail();
				RightSourceGetMemberResponse_DTO_HUM.CustomerKeysDetail objCustKeysDetailResp = New RightSourceGetMemberResponse_DTO_HUM.CustomerKeysDetail();
				RightSourceGetMemberResponse_DTO_HUM.CustomerKeyDetail objCustKeyDetailResp = New RightSourceGetMemberResponse_DTO_HUM.CustomerKeyDetail();
				RightSourceGetMemberResponse_DTO_HUM.CustomerPreference objCustPrefResp = New RightSourceGetMemberResponse_DTO_HUM.CustomerPreference();
				RightSourceGetMemberResponse_DTO_HUM.AddressesDetail objAddressesResp = New RightSourceGetMemberResponse_DTO_HUM.AddressesDetail();
				RightSourceGetMemberResponse_DTO_HUM.AddressDetail objAddressResp = New RightSourceGetMemberResponse_DTO_HUM.AddressDetail();
				RightSourceGetMemberResponse_DTO_HUM.PhonesDetail objPhonesResp = New RightSourceGetMemberResponse_DTO_HUM.PhonesDetail();
				RightSourceGetMemberResponse_DTO_HUM.PhoneDetail objPhoneResp = New RightSourceGetMemberResponse_DTO_HUM.PhoneDetail();
				RightSourceGetMemberResponse_DTO_HUM.AccountNumberDetail objAccNumDetResp = New RightSourceGetMemberResponse_DTO_HUM.AccountNumberDetail();
				RightSourceGetMemberResponse_DTO_HUM.FinanceDetail objFinDetResp = New RightSourceGetMemberResponse_DTO_HUM.FinanceDetail();
				RightSourceGetMemberResponse_DTO_HUM.PaymentDetail objPayDetResp = New RightSourceGetMemberResponse_DTO_HUM.PaymentDetail();
				RightSourceGetMemberResponse_DTO_HUM.NonCreditCardDetail objNonCCDetResp = New RightSourceGetMemberResponse_DTO_HUM.NonCreditCardDetail();
				RightSourceGetMemberResponse_DTO_HUM.AdjustmentsDetail objAdjustmentsDetResp = New RightSourceGetMemberResponse_DTO_HUM.AdjustmentsDetail();
				RightSourceGetMemberResponse_DTO_HUM.AdjustmentDetail objAdjustmentDetResp = New RightSourceGetMemberResponse_DTO_HUM.AdjustmentDetail();
				RightSourceGetMemberResponse_DTO_HUM.PaymentSchedulesDetail objPaySchsDetResp = New RightSourceGetMemberResponse_DTO_HUM.PaymentSchedulesDetail();
				RightSourceGetMemberResponse_DTO_HUM.PaymentScheduleDetail objPaySchDetResp = New RightSourceGetMemberResponse_DTO_HUM.PaymentScheduleDetail();
				RightSourceGetMemberResponse_DTO_HUM.AllergiesDetail objAllergiessDetResp = New RightSourceGetMemberResponse_DTO_HUM.AllergiesDetail();
				RightSourceGetMemberResponse_DTO_HUM.AllergyDetail objAllergyDetResp = New RightSourceGetMemberResponse_DTO_HUM.AllergyDetail();
				RightSourceGetMemberResponse_DTO_HUM.CreditCardsDetail objCCsDetResp = New RightSourceGetMemberResponse_DTO_HUM.CreditCardsDetail();
				RightSourceGetMemberResponse_DTO_HUM.CreditCardDetail objCCDetResp = New RightSourceGetMemberResponse_DTO_HUM.CreditCardDetail();
				RightSourceGetMemberResponse_DTO_HUM.HealthConditionsDetail objHealthConditionsDetResp = New RightSourceGetMemberResponse_DTO_HUM.HealthConditionsDetail();
				RightSourceGetMemberResponse_DTO_HUM.HealthConditionDetail objHealthConditionDetResp = New RightSourceGetMemberResponse_DTO_HUM.HealthConditionDetail();
				RightSourceGetMemberResponse_DTO_HUM.LogNotesDetail objLogNotesDetResp = New RightSourceGetMemberResponse_DTO_HUM.LogNotesDetail();
				RightSourceGetMemberResponse_DTO_HUM.LogNoteDetail objLogNoteDetResp = New RightSourceGetMemberResponse_DTO_HUM.LogNoteDetail();
				RightSourceGetMemberResponse_DTO_HUM.PlansDetail objPlansDetResp = New RightSourceGetMemberResponse_DTO_HUM.PlansDetail();
				RightSourceGetMemberResponse_DTO_HUM.PlanDetail objPlanDetResp = New RightSourceGetMemberResponse_DTO_HUM.PlanDetail();
				
				RightSourceGetOrderDetResponse_DTO_HUM objGetOrdDetResp = New RightSourceGetOrderDetResponse_DTO_HUM();
				RightSourceGetOrderDetResponse_DTO_HUM.Addresses objGetOrdDetAddResp = New RightSourceGetOrderDetResponse_DTO_HUM.Addresses();
				RightSourceGetOrderDetResponse_DTO_HUM.OrderItems objGetOrdDetOrdIResp = New RightSourceGetOrderDetResponse_DTO_HUM.OrderItems();
				RightSourceGetOrderDetResponse_DTO_HUM.CoPayCost objGetOrdDetCoPayResp = New RightSourceGetOrderDetResponse_DTO_HUM.CoPayCost();
				RightSourceGetOrderDetResponse_DTO_HUM.ShippingInformation objGetOrdDetShippingResp = New RightSourceGetOrderDetResponse_DTO_HUM.ShippingInformation();
				RightSourceGetOrderDetResponse_DTO_HUM.Queues objGetOrdDetQueuesResp = New RightSourceGetOrderDetResponse_DTO_HUM.Queues();
				RightSourceGetOrderDetResponse_DTO_HUM.Queue objGetOrdDetQueueResp = New RightSourceGetOrderDetResponse_DTO_HUM.Queue();
				RightSourceGetOrderDetResponse_DTO_HUM.ChangedOrder objGetOrdChangedOrderResp = New RightSourceGetOrderDetResponse_DTO_HUM.ChangedOrder();
				RightSourceGetOrderDetResponse_DTO_HUM.GetOrderDetailResponse objGetOrdGetOrderDetailResponseResp = New RightSourceGetOrderDetResponse_DTO_HUM.GetOrderDetailResponse();
				RightSourceGetOrderDetResponse_DTO_HUM.Items objGetOrdGetOrderItemsResp = New RightSourceGetOrderDetResponse_DTO_HUM.Items();
				RightSourceGetOrderDetResponse_DTO_HUM.CreditCardsDetail objGetOrdGetOrderCreditCardsDetailResp = New RightSourceGetOrderDetResponse_DTO_HUM.CreditCardsDetail();
				RightSourceGetOrderDetResponse_DTO_HUM.CreditCardDetail objGetOrdGetOrderCreditCardDetailResp = New RightSourceGetOrderDetResponse_DTO_HUM.CreditCardDetail();
				RightSourceGetOrderDetResponse_DTO_HUM.PlanCost objGetOrdGetOrderPlanCostResp = New RightSourceGetOrderDetResponse_DTO_HUM.PlanCost();
				
				RightSourceGetOrderResponse_DTO_HUM objGetOrdRespDTO = New RightSourceGetOrderResponse_DTO_HUM();
				RightSourceGetOrderResponse_DTO_HUM.OrdersReponse objGetOrdsResp = New RightSourceGetOrderResponse_DTO_HUM.OrdersReponse();
				RightSourceGetOrderResponse_DTO_HUM.OrdersDetail objGetOrdsDet = New RightSourceGetOrderResponse_DTO_HUM.OrdersDetail();
				RightSourceGetOrderResponse_DTO_HUM.OrderDetail objGetOrdDet = New RightSourceGetOrderResponse_DTO_HUM.OrderDetail();
				
				RightSourceGetScriptResponse_DTO_HUM objGetScriptRespDTO = New RightSourceGetScriptResponse_DTO_HUM();
				RightSourceGetScriptResponse_DTO_HUM.ScriptsResponse objGetScriptsResp = New RightSourceGetScriptResponse_DTO_HUM.ScriptsResponse();
				RightSourceGetScriptResponse_DTO_HUM.ScriptsDetail objGetScriptsDet = New RightSourceGetScriptResponse_DTO_HUM.ScriptsDetail();
				RightSourceGetScriptResponse_DTO_HUM.ScriptDetail objGetScriptDet = New RightSourceGetScriptResponse_DTO_HUM.ScriptDetail();
				RightSourceGetScriptResponse_DTO_HUM.ItemsDetail objItemsDetail= New RightSourceGetScriptResponse_DTO_HUM.ItemsDetail();
				RightSourceGetScriptResponse_DTO_HUM.ItemDetail objItemDetail= New RightSourceGetScriptResponse_DTO_HUM.ItemDetail();
				RightSourceGetScriptResponse_DTO_HUM.PhysicianDetail objPhysicianDetail = New RightSourceGetScriptResponse_DTO_HUM.PhysicianDetail();
				RightSourceGetScriptResponse_DTO_HUM.PhonesDetail objPhonesDetail= New RightSourceGetScriptResponse_DTO_HUM.PhonesDetail();
				RightSourceGetScriptResponse_DTO_HUM.PhoneDetail objPhoneDetail= New RightSourceGetScriptResponse_DTO_HUM.PhoneDetail();
				
				RSCreateAndEditOrderRequest_DTO_HUM objCreateAndEditOrder = New RSCreateAndEditOrderRequest_DTO_HUM();
				RSCreateAndEditOrderRequest_DTO_HUM.CreateAndEditOrderRequest objCreateAndEditOrderRequest = New RSCreateAndEditOrderRequest_DTO_HUM.CreateAndEditOrderRequest();
				RSCreateAndEditOrderRequest_DTO_HUM.MoveToRouting objMoveToRouting = New RSCreateAndEditOrderRequest_DTO_HUM.MoveToRouting();
				RSCreateAndEditOrderRequest_DTO_HUM.Address objAddress = New RSCreateAndEditOrderRequest_DTO_HUM.Address();
				RSCreateAndEditOrderRequest_DTO_HUM.CreditCard objCreditCard = New RSCreateAndEditOrderRequest_DTO_HUM.CreditCard();
				RSCreateAndEditOrderRequest_DTO_HUM.ScriptOrders objScriptOrders = New RSCreateAndEditOrderRequest_DTO_HUM.ScriptOrders();
				RSCreateAndEditOrderRequest_DTO_HUM.Script objScript = New RSCreateAndEditOrderRequest_DTO_HUM.Script();
				
				RSCreateAndEditOrderResponse_DTO_HUM objCreateAndEditResp = New RSCreateAndEditOrderResponse_DTO_HUM();
				RSCreateAndEditOrderResponse_DTO_HUM.CreateAndEditOrderResponse objCreateAndEditRespDTO = New RSCreateAndEditOrderResponse_DTO_HUM.CreateAndEditOrderResponse();
				
				RSGetAdjustmentAndPaymentResponseDTO_HUM obAdjPayDTO = New RSGetAdjustmentAndPaymentResponseDTO_HUM();
				RSGetAdjustmentAndPaymentResponseDTO_HUM.AdjustmentsAndPaymentsResponse obAdjPayResp = New RSGetAdjustmentAndPaymentResponseDTO_HUM.AdjustmentsAndPaymentsResponse();
				RSGetAdjustmentAndPaymentResponseDTO_HUM.AdjustmentDetails obAdjDets = New RSGetAdjustmentAndPaymentResponseDTO_HUM.AdjustmentDetails();
				RSGetAdjustmentAndPaymentResponseDTO_HUM.AdjustmentDetail obAdjDet = New RSGetAdjustmentAndPaymentResponseDTO_HUM.AdjustmentDetail();
				RSGetAdjustmentAndPaymentResponseDTO_HUM.PaymentDetails obPayjDets = New RSGetAdjustmentAndPaymentResponseDTO_HUM.PaymentDetails();
				RSGetAdjustmentAndPaymentResponseDTO_HUM.PaymentDetail obPayDet = New RSGetAdjustmentAndPaymentResponseDTO_HUM.PaymentDetail();				
				 
            	//prod test class coverage fixes
            	RightSourceEditMemberResponse_DTO_HUM.Order objEditMemRespDTO1 = New RightSourceEditMemberResponse_DTO_HUM.Order();
			Test.StopTest();
		}
	}	
}