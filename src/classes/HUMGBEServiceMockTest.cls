/**************************************************************************************************************************************
Apex Class Name : HUMGBEServiceMockTest
Version         : 1.0
Created On      : August 27 2015
Function        : This class mocks the response of HUMGBEService

* Modification Log:
* Developer Name          Code Review                   Date                       Description
*---------------------------------------------------------------------------------------------------------------------------------------
* Vishal Verma            25870                        08/27/2015                 Original Version            
* Vishal Verma            25870   	                   08/31/2015                 Updated Mock to add DentalEffectiveDate  
* Gargey Sharma           26691                        09/24/2015                 Added new parameters as per REQ - 222748 : Add Paging to Vitality Info
* Gargey Sharma           26912	                       10/01/2015                 Added new parameters as per REQ- 233762 : GBE Service Updates                       
****************************************************************************************************************************************/
@isTest
global class HUMGBEServiceMockTest implements WebServiceMock {
   global void doInvoke(
           Object stub,
           Object request,
           Map<String, Object> response,
           String endpoint,
           String soapAction,
           String requestName,
           String responseNS,
           String responseName,
           String responseType) {
           	HUMGBEService.GetGroupInfoResponse_element respElem = new HUMGBEService.GetGroupInfoResponse_element();
           	
		    HUMGBEService.ResponseGroupDetail_element RespGrpDetail = new HUMGBEService.ResponseGroupDetail_element();
		    RespGrpDetail.GroupId = 'Id';
	        RespGrpDetail.Platform = 'P';
	        RespGrpDetail.BenefitPackageId = '11';
	        RespGrpDetail.StartDate = '2013-01-01';
	        RespGrpDetail.EndDate = '2014-01-01';
	        RespGrpDetail.AsOfDate = 'New';
	        RespGrpDetail.BenefitStatus = True;
	        RespGrpDetail.ExchangeStatus = True;
	        RespGrpDetail.VitalityStatus = True;
	        RespGrpDetail.BenefitStatusNote ='Note';
	        RespGrpDetail.ExchangeStatusNote = 'Note';
	        RespGrpDetail.VitalityStatusNote = 'Note';
	        RespGrpDetail.VitalityRecordsTotalCount = 'count';
	        
		    HUMGBEService.ResponseGroupExchangeDTO ResponseGroupExchangeDTO = new HUMGBEService.ResponseGroupExchangeDTO();
		    ResponseGroupExchangeDTO.ExchangeIndicator = 'LV';
		    ResponseGroupExchangeDTO.ExchangeName = 'LV';
		    ResponseGroupExchangeDTO.ExchangeType = 'New';
		    ResponseGroupExchangeDTO.SalesChannelIdentifier = 'New';
		    ResponseGroupExchangeDTO.ExchangeSegment = 'Segmenent';
		    ResponseGroupExchangeDTO.BillingPlatform = 'Humana';
		    ResponseGroupExchangeDTO.MetallicTier = 'New';
		    ResponseGroupExchangeDTO.IsHumanaOne = True;
		    ResponseGroupExchangeDTO.BillPlatformInd = 'HUM';
		    ResponseGroupExchangeDTO.IsCommercial = false;
		    ResponseGroupExchangeDTO.IsMedicare = true;
		    ResponseGroupExchangeDTO.LineOfBusiness = 'F';
		    RespGrpDetail.ResponseGroupExchangeDTO = ResponseGroupExchangeDTO; 
		    
		    HUMGBEService.ResponseGroupBenefitDTO ResponseGroupBenefitDTO = new HUMGBEService.ResponseGroupBenefitDTO();
		    ResponseGroupBenefitDTO.GroupName = 'Grp Name';
        	ResponseGroupBenefitDTO.BenfitSequenceNumber = '123';
        	ResponseGroupBenefitDTO.EffectiveDate = '2013-11-01';
        	ResponseGroupBenefitDTO.EnrolledSubscriberCountDental = '121';
	        ResponseGroupBenefitDTO.EnrolledSubscriberCountMedical = '12';
	        ResponseGroupBenefitDTO.NextRenewalDate = '2011-01-01';
	        ResponseGroupBenefitDTO.Product = 'Prod';
	        ResponseGroupBenefitDTO.ProductCode = 'P';
	        ResponseGroupBenefitDTO.DentalEffectiveDate = '2011-01-01';
		    RespGrpDetail.ResponseGroupBenefitDTO = ResponseGroupBenefitDTO;
		    
		    HUMGBEService.VitalityIndicatorListDTO VitalityIndicatorListDTO = new HUMGBEService.VitalityIndicatorListDTO();
		    List<HUMGBEService.VitalityIndicatorDTO> lstVitalityIndicatorDTO = new List<HUMGBEService.VitalityIndicatorDTO>();
		    HUMGBEService.VitalityIndicatorDTO VitalityIndicatorDTO = new HUMGBEService.VitalityIndicatorDTO();
		    VitalityIndicatorDTO.VitalityIndicator = True;
        	VitalityIndicatorDTO.SubGroupID = 'Id';
        	VitalityIndicatorDTO.SubGroupName = 'Name';
        	VitalityIndicatorDTO.UnitCount = '1';
        	VitalityIndicatorDTO.Unit = 'unit';
        	VitalityIndicatorDTO.EffectiveProvision = 'EP';
        	lstVitalityIndicatorDTO.add(VitalityIndicatorDTO);
        	VitalityIndicatorListDTO.VitalityIndicatorDTO = lstVitalityIndicatorDTO;
        	RespGrpDetail.VitalityIndicatorListDTO = VitalityIndicatorListDTO;
        	respElem.ResponseGroupDetail = RespGrpDetail;
        	system.assert(respElem != null);
        	response.put('response_x', respElem);
    }
}