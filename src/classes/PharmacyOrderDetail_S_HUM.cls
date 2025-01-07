/*******************************************************************************************************************************
Apex Class Name : PharmacyOrderDetail_S_HUM.cls
Version         : 1.0
Created On      : 2017
Function        : Service Class for PharmacyOrderDetail_C_HUM.

Modification Log:
* Developer Name            Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------*
* Avinash Choubey           87810                      04/06/2017                  REQ 305689 Original Version
* Rajesh Keswani            		                  07/17/2017                 Adding network ID in service Request
* Sharan Shanmugam									   07/27/2017				 REQ - 323542 : Exception handling
* AshokKumar Nutalapati		338598         			   02/21/2019			     REQ - 383717: Ability to Read Rx #, Written Drug And Dispensed Drug Included in Order from Humana Pharmacy Page
* Melkisan Selvaraj									   03/04/2019                REQ - 387544 : Splunk error logging Issues
* Pinky Vijur                                          05/11/2021                  REQ 2230065: T1PRJ0002606 -2021 MF1- SF - Humana Constants Cleanup Humana Pharmacy
* Pallavi Shewale					                   06/08/2021		     US-1491784 Changes - Finance Queue
*********************************************************************************************************************************/
Public with sharing Class PharmacyOrderDetail_S_HUM {
            
    Public Static final String PHARMACY_PROCESS_ORD_RES_HUM = 'ProcessOrderResponse';
    Public Static final String PHARMACY_SUMMARY_HUM = 'Summary';
    Public Static final String VOB_FAULT_CODE = 'faultcode';
    Public Static final Integer HTTP_OK_HUM = 200;
    Public Static final Integer HTTP_ACCEPTED_HUM = 202; 
    Public Static final String PHAR_PROCESSCHILDORDERDETAILS_HUM = 'processChildOrderDetails';
    Public Static final String PHARMACY_GETORDER_RES_HUM = 'processGetOrderResponse';
    Public Static final String PHARMACY_RELORDER_RES_HUM = 'processRelOrderResponse';  
    Public Static final String HUM_MEMBER_NOT_FOUND = 'Member not found';
    Public Static final String HUM_RSS_SER_KEEPALIVE = 'keep-alive';
    Public Static final String HUM_RSS_SER_SOAPACTION = 'SOAPAction';
    Public Static final String HUM_RSS_SER_CONTENTTYPE = 'content-type';
    Public Static final String HUM_RSS_SER_CONECTION = 'Connection';
        /**
         * Returns HttpRequest request with request body
         * <p>
         * Method to prepare httpRequest details And certification details
         *
         * @param  HUM_Webservice_Callout__c record .
         * @return HttpRequest
         */
        public static HttpRequest builtHttpRequest(HUM_Webservice_Callout__c ServiceObj) {
            HttpRequest request = new HttpRequest();
            if (ServiceObj != null && ServiceObj.Active__c) 
            {
                request.setMethod(ServiceObj.Request_Type__c);
                if (!Test.isRunningTest() && String.isNotBlank(ServiceObj.Certificate_Name__c)) request.setClientCertificateName(ServiceObj.Certificate_Name__c);
                if (String.isNotBlank(ServiceObj.Content_Type__c)) request.setHeader(HUM_RSS_SER_CONTENTTYPE, ServiceObj.Content_Type__c);
                request.setHeader(HUM_RSS_SER_CONECTION, HUM_RSS_SER_KEEPALIVE);
                request.setEndpoint(ServiceObj.End_Point_URL__c);
                request.setHeader(HUM_RSS_SER_SOAPACTION, '');
            }
            return request;
        }
        
        /**
         * Returns continuation object
         * <p>
         * Method to call continuation serive for the serive for GetOrderResponse services .
         *
         * @param pharmacyCls                     Pharmacy_C_HUM class instance
         * @return                                continuation
         */
        public static Continuation invokeOrderDetail(String orderKeyValue, String source, String startDate, String endDate, String enterpriseId, string networkID, String sRecordId) {
              try
              {
                    HUM_Webservice_Callout__c RSServiceDetails = HUM_Webservice_Callout__c.getValues('RighSourceService_GetOrderDetails');
                    integer iTIMEOUT_INT_SECS = Integer.valueof(RSServiceDetails.Timeout__c) / 1000;
                    Continuation con = new Continuation(iTIMEOUT_INT_SECS);
                    con.continuationMethod = PHARMACY_PROCESS_ORD_RES_HUM;
                    If (String.isBlank(source) || source != PHARMACY_SUMMARY_HUM) 
                    {
                        HUM_Webservice_Callout__c RSServicegetOrderDetails = HUM_Webservice_Callout__c.getValues('RighSourceService_GetOrders');
                        HttpRequest httpRSGetOrderReqObj = builtHttpRequest(RSServicegetOrderDetails);
                        httpRSGetOrderReqObj.setBody(JSON.serialize(PharmacyOrderDetail_D_HUM.requestStructureGetOrderResponse(enterpriseId, startDate, endDate, networkID)));
                        con.addHttpRequest(httpRSGetOrderReqObj);
                    }
                    HttpRequest httpRSGetOrderDetailReqObj = builtHttpRequest(RSServiceDetails);
                    httpRSGetOrderDetailReqObj.setBody(JSON.serialize(PharmacyOrderDetail_D_HUM.requestStructureGetOrderDetailResponse(orderKeyValue,networkID)));
                    con.addHttpRequest(httpRSGetOrderDetailReqObj);
                    con.state = new PharmacyOrderWrapper_DTO_HUM(null, null, null, source, orderKeyValue,networkID,sRecordId);
                    return con;
             }
            catch (Exception ex)
            {
                HUMExceptionHelper.logErrors(ex, 'PharmacyOrderDetail_S_HUM', 'invokeOrderDetail');
                return null;
            }
        }
        
        /**
         * Returns responses of continuation object
         * <p>
         * parseGetAdjPayResponse Method to parse the service response
         *
         * @param List<string> labels .
         * @param  object State
         * @return Object
         */
        public static Object ProcessOrderResponse(List < string > labels, Object State) {
            try
            {
                    PharmacyOrderWrapper_DTO_HUM objOrderWrapDTO = (PharmacyOrderWrapper_DTO_HUM) state;
                    Integer conLableCount = 0;
                    HttpResponse responseOrderSummary = Continuation.getResponse(labels[conLableCount]);
                    if (String.isNotBlank(objOrderWrapDTO.source) && objOrderWrapDTO.source!= PHARMACY_SUMMARY_HUM) 
                    {
                        RightSourceGetOrderResponse_DTO_HUM oResponseGetOrder;
                           If (responseOrderSummary != null && !responseOrderSummary.getBody().containsIgnoreCase(VOB_FAULT_CODE) && ((responseOrderSummary.getStatusCode() == HTTP_OK_HUM || responseOrderSummary.getStatusCode() == HTTP_ACCEPTED_HUM) || Test.isRunningTest()))
                   
                        {
                            oResponseGetOrder = (RightSourceGetOrderResponse_DTO_HUM) System.JSON.deserialize(responseOrderSummary.getBody(), RightSourceGetOrderResponse_DTO_HUM.class);
                            objOrderWrapDTO = PharmacyOrderDetail_D_HUM.getOrderFromOrderSummary(oResponseGetOrder, objOrderWrapDTO);
                        }
                        conLableCount++;
            
                    }
                    RightSourceGetOrderDetResponse_DTO_HUM.GetOrderDetailResponse oResponse = new RightSourceGetOrderDetResponse_DTO_HUM.GetOrderDetailResponse();
                    HttpResponse orderDetailResponse = Continuation.getResponse(labels[conLableCount]);
                    String changedOrderkey = '';
                    If(orderDetailResponse != null && !orderDetailResponse.getBody().containsIgnoreCase(VOB_FAULT_CODE) && ((orderDetailResponse.getStatusCode() == HTTP_OK_HUM || orderDetailResponse.getStatusCode() == HTTP_ACCEPTED_HUM) || Test.isRunningTest()))
                    {
                        objOrderWrapDTO.objParentOrder = PharmacyOrderDetail_D_HUM.prepareOrderDetails((RightSourceGetOrderDetResponse_DTO_HUM) System.JSON.deserialize(orderDetailResponse.getbody(), RightSourceGetOrderDetResponse_DTO_HUM.class));
                    }
                    else
                    {
                        Pharmacy_H_HUM.logHpFaultError(responseOrderSummary,'PharmacyOrderDetail_S_HUM', 'processOrderResponse', objOrderWrapDTO.sRecordId);
                    }		        
                    if (null != objOrderWrapDTO.objParentOrder && !objOrderWrapDTO.objParentOrder.OrderItems.IsEmpty()) {
            
                        for (RightSourceGetOrderDetResponse_DTO_HUM.OrderItems orItem: objOrderWrapDTO.objParentOrder.OrderItems) 
                        {
                            if (null != orItem.ChangedOrder && null != orItem.ChangedOrder.ChangedOrderKey && null != orItem.ChangedOrder.ChangedOrderType && !String.IsBlank(orItem.ChangedOrder.ChangedOrderType) && !String.IsBlank(orItem.ChangedOrder.ChangedOrderKey) && orItem.ChangedOrder.ChangedOrderType == 'C') 
                            {
                                changedOrderkey = orItem.ChangedOrder.ChangedOrderKey;
                                break;
                            }
                        }
                    }
                    if (!String.IsBlank(changedOrderkey)) 
                    {
                        HUM_Webservice_Callout__c RSServiceDetails = HUM_Webservice_Callout__c.getValues('RighSourceService_GetOrderDetails');
                        // Create continuation with a timeout
                        integer iTIMEOUT_INT_SECS = Integer.valueof(RSServiceDetails.Timeout__c) / 1000;
                        Continuation con = new Continuation(iTIMEOUT_INT_SECS);
                        con.continuationMethod = PHAR_PROCESSCHILDORDERDETAILS_HUM;
                        HttpRequest httpRSGetOrderDetailReqObj = builtHttpRequest(RSServiceDetails);
                        httpRSGetOrderDetailReqObj.setBody(JSON.serialize(PharmacyOrderDetail_D_HUM.requestStructureGetOrderDetailResponse(changedOrderkey,objOrderWrapDTO.networkID)));
                        con.addHttpRequest(httpRSGetOrderDetailReqObj);
                        con.state = objOrderWrapDTO;
                        return con;
                    } 
                   
                    return objOrderWrapDTO;
             }
            catch (Exception ex)
            {
                HUMExceptionHelper.logErrors(ex, 'PharmacyOrderDetail_S_HUM', 'ProcessOrderResponse');
                return null;
            }
            
        }
    
        /**
         * Returns responses of continuation object
         * <p>
         * processChildOrder Method to parse the service response
         *
         * @param List<string> labels .
         * @param  object State
         * @return Object
         */
        public static Object processChildOrder(List < string > labels, Object State) {
               
           try
           {
                PharmacyOrderWrapper_DTO_HUM objOrderWrapDTO = (PharmacyOrderWrapper_DTO_HUM) state;
                RightSourceGetOrderDetResponse_DTO_HUM.GetOrderDetailResponse oResponse = new RightSourceGetOrderDetResponse_DTO_HUM.GetOrderDetailResponse();
                HttpResponse orderDetailResponse = Continuation.getResponse(labels[0]);
                if (orderDetailResponse != null && !orderDetailResponse.getBody().containsIgnoreCase(VOB_FAULT_CODE) && ((orderDetailResponse.getStatusCode() == HTTP_OK_HUM || orderDetailResponse.getStatusCode() == HTTP_ACCEPTED_HUM) || Test.isRunningTest()))
                {
                    objOrderWrapDTO.objChildOrder = PharmacyOrderDetail_D_HUM.prepareOrderDetails((RightSourceGetOrderDetResponse_DTO_HUM) System.JSON.deserialize(orderDetailResponse.getbody(), RightSourceGetOrderDetResponse_DTO_HUM.class));
                }
                else
                {
                    // Fault Code
                    Pharmacy_H_HUM.logHpFaultError(orderDetailResponse,'PharmacyOrderDetail_S_HUM', 'processChildOrder', objOrderWrapDTO.sRecordId);
                }
                return objOrderWrapDTO;
             }
            catch (Exception ex)
            {
                HUMExceptionHelper.logErrors(ex, ' PharmacyOrderDetail_S_HUM', 'processChildOrder');
                return null;
            }
        }
    
    
        /**
         * Returns continuation object
         * <p>
         * Method to call continuation serive for the serive for GetOrderResponse services .
         *
         * @param pharmacyCls                     Pharmacy_C_HUM class instance
         * @return                                continuation
         */
        public static Continuation callGetOrder(String orderKeyValue, String startDate, String endDate, String enterpriseId, String CurrentQueueName, string networkID, String sRecordId) {
            try
            {
                HUM_Webservice_Callout__c RSServicegetOrderDetails = HUM_Webservice_Callout__c.getValues('RighSourceService_GetOrders');
                integer iTIMEOUT_INT_SECS = Integer.valueof(RSServicegetOrderDetails.Timeout__c) / 1000;
                Continuation con = new Continuation(iTIMEOUT_INT_SECS);
                con.continuationMethod = PHARMACY_GETORDER_RES_HUM;
                HttpRequest httpRSGetOrderReqObj = builtHttpRequest(RSServicegetOrderDetails);
                httpRSGetOrderReqObj.setBody(JSON.serialize(PharmacyOrderDetail_D_HUM.requestStructureGetOrderResponse(enterpriseId, startDate, endDate, networkID)));	        
                con.addHttpRequest(httpRSGetOrderReqObj);
                con.state = new List < String > { orderKeyValue, CurrentQueueName, sRecordId };
                return con;
                }
            catch (Exception ex)
            {
                HUMExceptionHelper.logErrors(ex, 'PharmacyOrderDetail_S_HUM', 'callGetOrder');
                return null;
            }
        }
    
        /**
         * Returns responses of continuation object
         * <p>
         * parseGetAdjPayResponse Method to parse the service response
         *
         * @param List<string> labels .
         * @param  object State
         * @return Object
         */
        public static Object processGetOrderResponse(List < string > labels, Object State) {
            List <String> lstData = (List <String> ) state;
            Boolean bQueeuMatched = false;
            try
            {	       
                HttpResponse responseOrderSummary = Continuation.getResponse(labels[0]);
                RightSourceGetOrderResponse_DTO_HUM oResponseGetOrder;	        
                
                if (responseOrderSummary != null && !responseOrderSummary.getBody().containsIgnoreCase(VOB_FAULT_CODE) &&  ((responseOrderSummary.getStatusCode() == HTTP_OK_HUM || responseOrderSummary.getStatusCode() == HTTP_ACCEPTED_HUM) || Test.isRunningTest()))
                {
                    oResponseGetOrder = (RightSourceGetOrderResponse_DTO_HUM) System.JSON.deserialize(responseOrderSummary.getBody(), RightSourceGetOrderResponse_DTO_HUM.class);
                    bQueeuMatched = PharmacyOrderDetail_D_HUM.checkCurrentQueue(oResponseGetOrder, lstData);
                }
                else
                {
                    // Fault Code
                    Pharmacy_H_HUM.logHpFaultError(responseOrderSummary,'PharmacyOrderDetail_S_HUM', 'processGetOrderResponse', lstData[2]);
                }
                return bQueeuMatched;
            }
            catch (Exception ex)
            {
                HUMExceptionHelper.logErrors(ex, 'PharmacyOrderDetail_S_HUM', 'processGetOrderResponse');
                return null;
            }
        }
        
        /**
         * Returns continuation object
         * <p>
         * Method to call continuation serive for the serive for CreateEditOrder services .
         *
         * @param pharmacyCls                     Pharmacy_C_HUM class instance
         * @return                                continuation
         */
        public static Continuation sendRequestReleaseAndCancelOrder(String OrderKey, String EnterpriseId, Boolean isCancel, string networkID, String sRecordId , Boolean overrideconsent, Boolean copayconsent,string lstScriptKey ) {
               try
               { 
                    HUM_Webservice_Callout__c RSServiceDetails = HUM_Webservice_Callout__c.getValues('RighSourceService_CreateAndEditOrder');
                    // Create continuation with a timeout
                    integer iTIMEOUT_INT_SECS = Integer.valueof(RSServiceDetails.Timeout__c) / 1000;
                    Continuation con = null;
                            RSCreateAndEditOrderRequest_DTO_HUM objRSreq = PharmacyOrderDetail_D_HUM.requestStructureRSReleaseAndCancelOrder(orderKey, EnterpriseId, isCancel,networkID,overrideconsent,copayconsent,lstScriptKey);
                    if (null != objRSreq) 
                    {
                        con = new Continuation(iTIMEOUT_INT_SECS);
                        con.continuationMethod = PHARMACY_RELORDER_RES_HUM;
                        con.state = sRecordId;
                        HttpRequest httpRSRelOrderReq = builtHttpRequest(RSServiceDetails);
                        httpRSRelOrderReq.setBody(JSON.serialize(objRSreq));
                        con.addHttpRequest(httpRSRelOrderReq);
                    }
                    return con;
                }
            catch (Exception ex)
            {
                HUMExceptionHelper.logErrors(ex, 'PharmacyOrderDetail_S_HUM', 'sendRequestReleaseAndCancelOrder');
                return null;
            }
        }
        
        /**
         * Returns responses of continuation object
         * <p>
         * parseRSGetScriptResponse Method to parse the service response
         *
         * @param  object state,List<string> labels .
         * @return Object
         */
        public static object processRelOrderResponse(List <string> labels, Object State) {
            Boolean bRelCancelSuccess = false;
            try
            {
                    HttpResponse resRSRelOrder = Continuation.getResponse(labels[0]);
                    RSCreateAndEditOrderResponse_DTO_HUM obResRelOrder = new RSCreateAndEditOrderResponse_DTO_HUM();
                    if (resRSRelOrder != null && !resRSRelOrder.getBody().containsIgnoreCase(VOB_FAULT_CODE) &&  ((resRSRelOrder.getStatusCode() == HTTP_OK_HUM || resRSRelOrder.getStatusCode() == HTTP_ACCEPTED_HUM) || Test.isRunningTest()))
                    
                    {
                        obResRelOrder = (RSCreateAndEditOrderResponse_DTO_HUM)System.JSON.deserialize(resRSRelOrder.getBody(), RSCreateAndEditOrderResponse_DTO_HUM.class);
                        if (obResRelOrder != null && null != obResRelOrder.CreateAndEditOrderResponse && String.isNotBlank(obResRelOrder.CreateAndEditOrderResponse.transactionKey)) bRelCancelSuccess = true;
                    }
                    else
                    {
                        // Fault Code
                        Pharmacy_H_HUM.logHpFaultError(resRSRelOrder,'PharmacyOrderDetail_S_HUM', 'processRelOrderResponse', State != null ? String.valueof(State) : '');
                    }
            
                    if (null != obResRelOrder && null != obResRelOrder.CreateAndEditOrderResponse && obResRelOrder.CreateAndEditOrderResponse.ErrorDescription != HUM_MEMBER_NOT_FOUND)
                        return bRelCancelSuccess;
                    else
                        return null;
             }
            catch (Exception ex)
            {
                HUMExceptionHelper.logErrors(ex, 'PharmacyOrderDetail_S_HUM', 'processRelOrderResponse');
                return null;
            }
        }
    }