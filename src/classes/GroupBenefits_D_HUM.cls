/**************************************************************************** 
Apex Class Name  : GroupBenefits_D_HUM   
Version          : 1.0  
Created Date     : April 26, 2018
Function         : Data Layer Class for Group Benefits 
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*------------------------------------------------------------------------------------------------------------
* Rajesh keswani                               04/26/2016                 Original Version
* Shreya Choodamani                            05/03/2018                 Added the Legal Entity and Group Id variable as part of REQ#: 361851
* Joel George									07/02/2018				  Added changes to Benefit Search functionality
* Joel George									08/03/2018				  Changes made as part of REQ 367753, 362122 & 363574
* Lakshmi Madduri			268842				08/14/2018				   Added changes as part of 363357
* Anjali Kumar									09/07/2018			      REQ 374811 - Display only ADA Code dropdown options
* Dinesh Subramaniyan				       			   04/18/2018		  		  REQ - 396416 - Modify logic for CAS benefit grids to auto-populate
* Deepankar Binjola         48282               09/19/2019                        REQ - 402985 Service Catalog IMS form to prepopulate with the plan information
* Yogesh Gupta									12/03/2019		  		  REQ - 410445 Azure-731139 Add Ada code column on UI for Dental policy 
* Shailesh Mali							        02/18/2020				 REQ - 732113 Service Type Comments ordering - Dental only
* Riya Agarwal									14/04/2020				  REQ - 1004703 - CA ticket - 9041183 - The benefit snapshot at a group level does not show all of the benefits %
* Suraj Patil								   	04/09/2021				  REQ - 1905175 - PR00094226 - CR AEP readiness - Low Security Issues 
* Suraj Patil									05/07/2021				  DF 3003 and DF 3001 in Qtest
* Suraj patil									06/14/2018				  REQ - 2233176 T1PRJ0002735 (PR00094226) - SF - Mobius, MTV - CR AEP readiness - High Security Issues
***************************************************************************************************/
public with sharing class GroupBenefits_D_HUM
{
  Public Static final String  VOB_TERMEDDATE_HUM = '12/31/9999';
    Public Static final String sHiphan = '-';
    Public Static final String sEM_Value= 'EM';
    Public Static final String sMTV_Value = 'MTV';
    Public Static final String SUBGROUP_MENTORREDIRECT_HUM = 'subgroup:';
    Public Static final String sLV_Value= 'LV';
    Public Static final String sCI_Value = 'CI';
    Public Static final String sCAS_Value = 'CAS';
    Public Static final String HUM_PARTD_DRUGREQUESTOR = 'CRM';
    Public Static final String VOB_PAR_YES_HUM = 'Yes';
    Public Static final String VOB_PAR_NO_HUM = 'No';
    Public Static final String VOB_BENEFITSEARCH_INNETWORK_HUM = 'InNetwork';
    Public Static final String VOB_BENEFITSEARCH_OUTNETWORK_HUM = 'OutOfNetwork';
    Public Static final String  VOB_BOTH = 'Both';  
    Public Static final String VOB_BENEFITSEARCH_DOLLAR_HUM = 'Dollar';
    Public Static final String VOB_ACCUMS_DOLLAR_SIGN_HUM = '$';
    Public Static final String VOB_BENEFITSEARCH_PERCENTAGE_HUM = 'Percentage';
    Public Static final String VOB_ACCUMS_PERCENTAGE_SIGN_HUM = '%';
    Public Static final String  VOB_BENEFITPACKAGEID_HUM = 'BenefitPackageId';
    Public Static final String  VOB_PLATFORM_EM_HUM = 'EM';
    Public Static final String  VOB_PLATFORM_LV_HUM = 'LV';
    Public Static final String  VOB_VALUEPDQ_HUM = 'PDQ';
    Public Static final String VOB_PBESERVICEDETAIL_HUM = 'ServicesWithPlaceOfService';
    Public Static final String  VOB_PLANCOMMENTS_HUM = 'PlanComments';
    Public Static final String VOB_BENEFITSEARCH_SERVICELIST_HUM = 'Service';
    Public Static final String VOB_PAR_DEFAULT_HUM = 'Par/Non Par/Both';
    Public Static final String HYPHEN_HUM = '-';
    Public Static final String BACKSLASH_HUM = '/';
    Public Static final String VOB_PARCODE_DEFAULT_HUM = 'Par Code';
    Public Static final String MAX_DATE_HUM = '12/31/9999';
    Public Static final String  VOB_BENEFITS = 'BENEFITS';
    Public Static final String  VOB_GENERALBENEFITS_HUM = 'GeneralBenefits';
   Public Static final String  VOB_RIDERS_HUM = 'Riders';
   Public Static final String MED = 'MED';
    Public Static final String  VOB_MEDICARE ='RSO- Medicare';
    Public Static final String  VOB_RXDETAIL_HUM = 'RxDetail'; 
    Public Static final String  VOB_MASTERSERVICES_HUM = 'MasterServices';
    //============================== Group Search changes ==================================================
 /* Method for parsing of InNetwork and OutOfNetwork benefit type 
    * <p>
    * @param  object of ProductBusinessEntityResponseV2_H_HUM.Product .
    * @param  string selected PAR value .
    * @param  Map<string,list<CoverageType_DTO_HUM>> for InNetwork .
    * @param  Map<string,list<CoverageType_DTO_HUM>> for OutOfNetwork .
    * @return nothing
    */
    private Static void prepareBenefits(ProductBusinessEntityResponseV2_H_HUM.Product oProduct , String ParValue, Map<string,list<CoverageType_DTO_HUM>> inNetworkMap , Map<string,list<CoverageType_DTO_HUM>> outNetworkMap, Map<string,List<string>> lstOfServiceComments, Map<string,Boolean> isCoveredServiceInNetworks)
    {
        boolean sServiceTypeCommentSwitch = false;            
        sServiceTypeCommentSwitch = HUMUtilityHelper.isCRMFunctionalityON('732113');
		
        if(oProduct != null && oProduct.Detail != null)
        {
            if(oProduct.Detail.ServiceList != null && oProduct.Detail.ServiceList.Service != null && oProduct.Detail.ServiceList.Service.size() > 0)
            {
                List<ProductBusinessEntityResponseV2_H_HUM.Service> oServiceList = oProduct.Detail.ServiceList.Service;
                
                Boolean bPlatformLV = false;
                if(oProduct.MetaData.PlatformCode == 'LV' || oProduct.MetaData.PlatformCode == 'CI' || oProduct.MetaData.PlatformCode == 'CAS')    bPlatformLV = true;
                for(ProductBusinessEntityResponseV2_H_HUM.Service oService: oServiceList){
	               
	                
	                if(oService != null && oService.BenefitList != null && oService.BenefitList.Benefit != null && oService.BenefitList.Benefit.size () > 0)
	                {
	                    updatebenSearchResArray(inNetworkMap,outNetworkMap,oService.BenefitList.Benefit,ParValue,VOB_BENEFITSEARCH_SERVICELIST_HUM, isCoveredServiceInNetworks,bPlatformLV,oService.ServiceCategoryCode, oService.ServiceTypeID);
	                }
	                if(oService != null && oService.CommentList != null && oService.CommentList.PlanComment != null && oService.CommentList.PlanComment.size () > 0)
	                {
	                   if(sServiceTypeCommentSwitch == true)
                        	updatebenSearchServiceCommentsArraySubVerion3(oService.CommentList.PlanComment,lstOfServiceComments,oService.ServiceTypeId);
                        else
                            updatebenSearchServiceCommentsArray(oService.CommentList.PlanComment,lstOfServiceComments, oService.ServiceTypeID);
                    }
                }
            }

        }
    }

    
    /* Method for parsing of InNetwork response 
    * <p>
    * @param  object of ProductBusinessEntityResponseV2_H_HUM.Product .
    * @param  string selected PAR value .
    * @param  Map<string,list<CoverageType_DTO_HUM>> for InNetwork .
    * @param  Map<string,list<CoverageType_DTO_HUM>> for finalInNetwork Values for Non Limits .
    * @param  Map<string,list<CoverageType_DTO_HUM>> for InNetwork Values for respective Limits .
    * @return nothing
    */
    private static void prepareInNetworkResponse(Map<string,list<CoverageType_DTO_HUM>> inNetworkMap, Map<string,list<CoverageType_DTO_HUM>> finalMapofNonLimitInNetwork, Map<string,list<CoverageType_DTO_HUM>> lstOfLimitsInNetwork)
    {
        
        CoverageType_DTO_HUM coverageDTO = NULL;
        Map<string,CoverageType_DTO_HUM>  finalInNetworkMap = new Map<string,CoverageType_DTO_HUM>();
        for(string nodeType : inNetworkMap.keyset())
        {
            if(finalInNetworkMap.containsKey(nodeType))
            {
                coverageDTO = finalInNetworkMap.get(nodeType);
            }
            else
            {
                coverageDTO = new CoverageType_DTO_HUM();
                finalInNetworkMap.put(nodeType,coverageDTO);
            }
            if(inNetworkMap.get(nodeType) != null && inNetworkMap.get(nodeType).size() > 0 )
            {
                for (CoverageType_DTO_HUM oDto : inNetworkMap.get(nodeType))
                {
                    parseInNetworkMap(oDto,coverageDTO,finalMapofNonLimitInNetwork,lstOfLimitsInNetwork)    ;
                }
            }
        }
    }
    
    /* Method for parsing of InNetwork response 
    * <p>
    * @param  object of ProductBusinessEntityResponseV2_H_HUM.Product .
    * @param  string selected PAR value .
    * @param  Map<string,list<CoverageType_DTO_HUM>> for InNetwork .
    * @param  Map<string,list<CoverageType_DTO_HUM>> for finalInNetwork Values for Non Limits .
    * @param  Map<string,list<CoverageType_DTO_HUM>> for InNetwork Values for respective Limits .
    * @return nothing
    */
    private static void parseInNetworkMap(CoverageType_DTO_HUM oDto, CoverageType_DTO_HUM coverageDTO, Map<string,list<CoverageType_DTO_HUM>>finalMapofNonLimitInNetwork, Map<string,list<CoverageType_DTO_HUM>>lstOfLimitsInNetwork)
    {
        
        if(oDto.isLimit == 'false' || oDto.nodeType == 'Service' )
        {
            coverageDTO.BenefitDescription = oDto.BenefitDescription;
            coverageDTO.Par = oDto.Par;
            coverageDTO.nodeType = oDto.nodeType;
            coverageDTO.notes = oDto.notes;
            if(finalMapofNonLimitInNetwork.containsKey(oDto.nodeType))
            {
                List<CoverageType_DTO_HUM> covType = finalMapofNonLimitInNetwork.get(oDto.nodeType);
                covType.add(oDto);
            }
            else
            {
                List<CoverageType_DTO_HUM> covType = new List<CoverageType_DTO_HUM>();
                covType.add(oDto);
                finalMapofNonLimitInNetwork.put(oDto.nodeType,covType);
            }
        }
         if(oDto.isLimit == 'true')
        {
            if(lstOfLimitsInNetwork.containsKey(oDto.nodeType))
            {
                List<CoverageType_DTO_HUM> covType = lstOfLimitsInNetwork.get(oDto.nodeType);
                covType.add(oDto);
            }
            else
            {
                List<CoverageType_DTO_HUM> covType = new List<CoverageType_DTO_HUM>();
                covType.add(oDto);
                lstOfLimitsInNetwork.put(oDto.nodeType,covType);
            }
        }
    }
    
    /* Method for parsing of OutOfNetwork response 
    * <p>
    * @param  object of ProductBusinessEntityResponseV2_H_HUM.Product .
    * @param  string selected PAR value .
    * @param  Map<string,list<CoverageType_DTO_HUM>> for OutOfNetwork .
    * @param  Map<string,list<CoverageType_DTO_HUM>> for finalOutOfNetwork Values for Non Limits .
    * @param  Map<string,list<CoverageType_DTO_HUM>> for OutOfNetwork Values for respective Limits .
    * @return nothing
    */
    private static void prepareOutNetworkResponse(Map<string,list<CoverageType_DTO_HUM>>outNetworkMap,Map<string,list<CoverageType_DTO_HUM>> finalMapofNonLimitOutNetwork,Map<string,list<CoverageType_DTO_HUM>> lstOfLimitsOutNetwork)
    {
        
        CoverageType_DTO_HUM coverageDTO = NULL;
        Map<string,CoverageType_DTO_HUM> finalOutNetworkMap = new Map<string,CoverageType_DTO_HUM>();
        for(string nodeType : outNetworkMap.keyset())
        {
            if(finalOutNetworkMap.containsKey(nodeType))
            {
                coverageDTO = finalOutNetworkMap.get(nodeType);
            }
            else
            {
                coverageDTO = new CoverageType_DTO_HUM();
                finalOutNetworkMap.put(nodeType,coverageDTO);
            }
            if(outNetworkMap.get(nodeType) != null && outNetworkMap.get(nodeType).size() > 0 )
            {
                for (CoverageType_DTO_HUM oDto : outNetworkMap.get(nodeType))
                {
                    parseOutNetworkMap(oDto,coverageDTO,finalMapofNonLimitOutNetwork,lstOfLimitsOutNetwork);
                }
            }
        }
    }
    
    /* Method for parsing of OutOfNetwork response 
    * <p>
    * @param  object of ProductBusinessEntityResponseV2_H_HUM.Product .
    * @param  string selected PAR value .
    * @param  Map<string,list<CoverageType_DTO_HUM>> for OutOfNetwork .
    * @param  Map<string,list<CoverageType_DTO_HUM>> for finalOutOfNetwork Values for Non Limits .
    * @param  Map<string,list<CoverageType_DTO_HUM>> for OutOfNetwork Values for respective Limits .
    * @return nothing
    */
    private static void parseOutNetworkMap(CoverageType_DTO_HUM oDto, CoverageType_DTO_HUM coverageDTO , Map<string,list<CoverageType_DTO_HUM>> finalMapofNonLimitOutNetwork, Map<string,list<CoverageType_DTO_HUM>> lstOfLimitsOutNetwork)
    {
        
        if(oDto.isLimit == 'false' || oDto.nodeType =='Service' )
        {
            coverageDTO.BenefitDescription = oDto.BenefitDescription;
            coverageDTO.NonPar = oDto.NonPar;
            coverageDTO.nodeType = oDto.nodeType;
            coverageDTO.notes = oDto.notes;
            if(finalMapofNonLimitOutNetwork.containsKey(oDto.nodeType))
            {
                List<CoverageType_DTO_HUM> covType = finalMapofNonLimitOutNetwork.get(oDto.nodeType);
                covType.add(oDto);
            }
            else
            {
                List<CoverageType_DTO_HUM> covType = new List<CoverageType_DTO_HUM>();
                covType.add(oDto);
                finalMapofNonLimitOutNetwork.put(oDto.nodeType,covType);
            }
        }
         if(oDto.isLimit == 'true')
        {
            if(lstOfLimitsOutNetwork.containsKey(oDto.nodeType))
            {
                List<CoverageType_DTO_HUM> covType = lstOfLimitsOutNetwork.get(oDto.nodeType);
                covType.add(oDto);
            }
            else
            {
                List<CoverageType_DTO_HUM> covType = new List<CoverageType_DTO_HUM>();
                covType.add(oDto);
                lstOfLimitsOutNetwork.put(oDto.nodeType,covType);
            }
        }
    }
    
    /* Method to parse Benefit Search Result
    * <p>
    * Methd is used to Benefit Search response and returncreate DTO to show values on UI 
    * @param  ProductBusinessEntityResponseV2_H_HUM   Webservice Response .
    * @param  String        PAR/NON Par value
    * @return PBEBenefitsSearch_DTO_HUM DTO to be sown on UI
    */
    public static PBEBenefitsSearch_DTO_HUM getBenefitsSearchDTO (ProductBusinessEntityResponseV2_H_HUM oResponse,string ParValue)
    {
        
        if(ParValue !='Yes' && ParValue != 'No') ParValue = VOB_BOTH;
        Map<string,list<CoverageType_DTO_HUM>> inNetworkMap = new Map<string,list<CoverageType_DTO_HUM>>();
        Map<string,list<CoverageType_DTO_HUM>> outNetworkMap = new Map<string,list<CoverageType_DTO_HUM>>();
        Map<string,list<CoverageType_DTO_HUM>> finalMapofNonLimitInNetwork = new Map<string,list<CoverageType_DTO_HUM>>();
        Map<string,list<CoverageType_DTO_HUM>> finalMapofNonLimitInOutNetwork = new Map<string,list<CoverageType_DTO_HUM>>();
        Map<string,list<CoverageType_DTO_HUM>> finalMapofNonLimitOutNetwork = new Map<string,List<CoverageType_DTO_HUM>>();
        Map<string,List<CoverageType_DTO_HUM>> lstOfLimitsInNetwork = new Map<string,List<CoverageType_DTO_HUM>>();
        Map<string,List<CoverageType_DTO_HUM>> lstOfLimitsOutNetwork = new Map<string,List<CoverageType_DTO_HUM>>();
        Map<string,List<CoverageType_DTO_HUM>> lstOfLimitsInOutNetwork = new Map<string,List<CoverageType_DTO_HUM>>();
        Map<string,List<string>> lstOfServiceComments = new Map<string,List<string>>();
        Map<string,Boolean> isCoveredServiceInNetworks = new Map<string,Boolean>();
         
        if(oResponse != null && oResponse.GetProductResponse != null && oResponse.GetProductResponse.ProductList != null && oResponse.GetProductResponse.ProductList.Product != null )
        {
            prepareBenefits(oResponse.GetProductResponse.ProductList.Product[0], ParValue, inNetworkMap,outNetworkMap,lstOfServiceComments, isCoveredServiceInNetworks);
        }
        if(inNetworkMap.size() > 0) prepareInNetworkResponse(inNetworkMap,finalMapofNonLimitInNetwork,lstOfLimitsInNetwork);
        if(outNetworkMap.size() > 0) prepareOutNetworkResponse(outNetworkMap,finalMapofNonLimitOutNetwork,lstOfLimitsOutNetwork);
        if(ParValue == VOB_BOTH) 
        {
            finalMapofNonLimitInOutNetwork = finalMapofNonLimitInNetwork.clone();
            lstOfLimitsInOutNetwork = lstOfLimitsInNetwork.clone();
            prepareFinalMapNonLimit(finalMapofNonLimitOutNetwork, finalMapofNonLimitInOutNetwork);
            Map<string,List<CoverageType_DTO_HUM>> TempMapInOutNetwork = prepareBOTHMap(finalMapofNonLimitInOutNetwork);
            
            if(TempMapInOutNetwork != null && TempMapInOutNetwork.size() > 0 )
            {
                finalMapofNonLimitInOutNetwork.clear();
                finalMapofNonLimitInOutNetwork = TempMapInOutNetwork.clone();
            }
            prepareLimitsNetwork(lstOfLimitsOutNetwork, lstOfLimitsInOutNetwork);
        }
        
        
        List<CoverageType_DTO_HUM> lstNewIN = new List<CoverageType_DTO_HUM>();
        List<CoverageType_DTO_HUM> lstNewOUT = new List<CoverageType_DTO_HUM>();
        for (string nodeType : finalMapofNonLimitInNetwork.keyset())
        {
            if(nodeType == 'Service')
            {
                for (CoverageType_DTO_HUM oCoverageType : finalMapofNonLimitInNetwork.get(nodeType))
                {
                    if(oCoverageType.sIsCoveredBenefit.equalsIgnoreCase('False'))
                    {
                        CoverageType_DTO_HUM covDtoObj = new CoverageType_DTO_HUM();
                        covDtoObj.BenefitDescription = oCoverageType.BenefitDescription;
                        covDtoObj.placeOfServiceCode = oCoverageType.placeOfServiceCode;
                        covDtoObj.benefitType = 'Not Covered';
                        lstNewIN.add(covDtoObj);
                    }
                    else    lstNewIN.add(oCoverageType);
                }
            }
        }
        if(finalMapofNonLimitInNetwork.containsKey('Service'))    finalMapofNonLimitInNetwork.remove('Service');
        if(!lstNewIN.isEmpty())    finalMapofNonLimitInNetwork.put('Service', lstNewIN);
        
        for (string nodeType : finalMapofNonLimitOutNetwork.keyset())
        {
            if(nodeType == 'Service')
            {
                for (CoverageType_DTO_HUM oCoverageType : finalMapofNonLimitOutNetwork.get(nodeType))
                {
                    if(oCoverageType.sIsCoveredBenefit.equalsIgnoreCase('False'))
                    {
                        CoverageType_DTO_HUM covDtoObj = new CoverageType_DTO_HUM();
                        covDtoObj.BenefitDescription = oCoverageType.BenefitDescription;
                        covDtoObj.placeOfServiceCode = oCoverageType.placeOfServiceCode;
                        covDtoObj.benefitType = 'Not Covered';
                        lstNewOUT.add(covDtoObj);
                    }
                    else    lstNewOUT.add(oCoverageType);
                }
            }
        }
        if(finalMapofNonLimitOutNetwork.containsKey('Service'))    finalMapofNonLimitOutNetwork.remove('Service');
        if(!lstNewOUT.isEmpty())    finalMapofNonLimitOutNetwork.put('Service', lstNewOUT);
       
        return prepareResponseStructure(isCoveredServiceInNetworks, ParValue, lstOfLimitsInNetwork,lstOfLimitsOutNetwork,lstOfLimitsInOutNetwork, finalMapofNonLimitInNetwork, finalMapofNonLimitOutNetwork ,finalMapofNonLimitInOutNetwork,lstOfServiceComments);
    } 
    
    /* Method to prepare fianl Map of Non Limit 
    * <p>
    * @param  Map<string,list<CoverageType_DTO_HUM>> limits for OutOfNetwork .
    * @param  Map<string,list<CoverageType_DTO_HUM>> limits for  In and out Network Values  .
    * @return nothing
    */
    private static void prepareFinalMapNonLimit(Map<string,list<CoverageType_DTO_HUM>> finalMapofNonLimitOutNetwork ,Map<string,list<CoverageType_DTO_HUM>> finalMapofNonLimitInOutNetwork)
    {
        
        for(string nodeType : finalMapofNonLimitOutNetwork.keyset())
        {
            if(finalMapofNonLimitInOutNetwork.containsKey(nodeType))
            {
                List<CoverageType_DTO_HUM> temCov = finalMapofNonLimitOutNetwork.get(nodeType);
                finalMapofNonLimitInOutNetwork.get(nodeType).addAll(temCov);
            }
            else
            {
                finalMapofNonLimitInOutNetwork.put(nodeType,finalMapofNonLimitOutNetwork.get(nodeType));
            }
        }
    }
    
    /* Method to prepare map when 'Par = Both' is selected for limits 
    * <p>
    * @param  object of ProductBusinessEntityResponseV2_H_HUM.Product .
    * @param  string selected PAR value .
    * @param  Map<string,list<CoverageType_DTO_HUM>> limits for OutOfNetwork .
    * @param  Map<string,list<CoverageType_DTO_HUM>> limits for  In and out Network Values  .
    * @return nothing
    */
    private static void prepareLimitsNetwork(Map<string,List<CoverageType_DTO_HUM>> lstOfLimitsOutNetwork, Map<string,List<CoverageType_DTO_HUM>> lstOfLimitsInOutNetwork)
    {
        
        for(string benefitType : lstOfLimitsOutNetwork.keyset())
        {
            if(lstOfLimitsInOutNetwork.containsKey(benefitType))
            {
                List<CoverageType_DTO_HUM> temCovLst = lstOfLimitsInOutNetwork.get(benefitType);
                temCovLst.addAll(lstOfLimitsOutNetwork.get(benefitType));
            }
            else
            {
                lstOfLimitsInOutNetwork.put(benefitType,lstOfLimitsOutNetwork.get(benefitType));
            }
            
        }
    }
    
    /* Method to prepare map when 'Par = Both' is selected for Non limits(where isLimit = false) 
    * <p>
    * @param  Map<string,list<CoverageType_DTO_HUM>> limits for  In and out Network Values  .
    * @return Map<string,List<CoverageType_DTO_HUM>> Non - limits Map with combined PAR and NonPar values
    */
    private static Map<string,List<CoverageType_DTO_HUM>> prepareBOTHMap(Map<string,List<CoverageType_DTO_HUM>> finalMapofNonLimitInOutNetwork)
    {
        
        Map<string,List<CoverageType_DTO_HUM>> TempMapInOutNetwork = new Map<string,List<CoverageType_DTO_HUM>>();
        for (string nodeType : finalMapofNonLimitInOutNetwork.keyset())
        {
            List<CoverageType_DTO_HUM> outNetworkCoverageType = new List<CoverageType_DTO_HUM>();
            List<CoverageType_DTO_HUM> inNetworkListCoverageType = new List<CoverageType_DTO_HUM>();
            for (CoverageType_DTO_HUM oCoverageType : finalMapofNonLimitInOutNetwork.get(nodeType))
            {
                CoverageType_DTO_HUM covDtoObj = new CoverageType_DTO_HUM();
                covDtoObj.BenefitDescription = oCoverageType.BenefitDescription;
                covDtoObj.placeOfServiceCode = oCoverageType.placeOfServiceCode;
                covDtoObj.sIsCoveredBenefit = oCoverageType.sIsCoveredBenefit;
                covDtoObj.Par = oCoverageType.Par;
                covDtoObj.NonPar = oCoverageType.NonPar;
                covDtoObj.benefitType = 'Not Covered';
                
            if(string.isNotBlank(oCoverageType.Par) || oCoverageType.networkType.equalsIgnoreCase('IN'))
                {
                    if((nodeType == 'Service') && (oCoverageType.sIsCoveredBenefit.equalsIgnoreCase('False')))    inNetworkListCoverageType.add(covDtoObj);   
                    else inNetworkListCoverageType.add(oCoverageType);
                }
                else if(string.isNotBlank(oCoverageType.NonPar)||oCoverageType.networkType.equalsIgnoreCase('out'))
                {
                    if((nodeType == 'Service') && (oCoverageType.sIsCoveredBenefit.equalsIgnoreCase('False')))    outNetworkCoverageType.add(covDtoObj);
                    else    outNetworkCoverageType.add(oCoverageType);
                }
            }
            for(CoverageType_DTO_HUM InNetCovType : inNetworkListCoverageType)
            {
                for (CoverageType_DTO_HUM OutNetCovType : outNetworkCoverageType)
                {
                    
                        if(InNetCovType.BenefitDescription == OutNetCovType.BenefitDescription &&  InNetCovType.TierNumber == OutNetCovType.TierNumber && ((InNetCovType.benefitType != 'Limit_Service' && OutNetCovType.benefitType != 'Limit_Service' && InNetCovType.benefitType == OutNetCovType.benefitType) || (InNetCovType.limitName == OutNetCovType.limitName && InNetCovType.benefitType == 'Limit_Service' && OutNetCovType.benefitType == 'Limit_Service')))
                    {
                        if(nodeType == 'Service')
                        {
                            if(InNetCovType.sIsCoveredBenefit.equalsIgnoreCase('True') || OutNetCovType.sIsCoveredBenefit.equalsIgnoreCase('False'))
                            {
                                InNetCovType.NonPar = OutNetCovType.NonPar;
                                if(InNetCovType.placeOfServiceCode.equalsIgnoreCase(OutNetCovType.placeOfServiceCode))    OutNetCovType.matched = true;
                            }
                            else    
                            {    
                                OutNetCovType.Par = InNetCovType.Par;
                                if(InNetCovType.placeOfServiceCode.equalsIgnoreCase(OutNetCovType.placeOfServiceCode))    InNetCovType.matched = true;
                            }
                        }
                        else
                        {
                            InNetCovType.NonPar = OutNetCovType.NonPar;
                            OutNetCovType.matched = true;    
                        }
                    }
                }
            }   
            for(CoverageType_DTO_HUM OutNetCovType : outNetworkCoverageType)
            {
                if(OutNetCovType.matched == false) inNetworkListCoverageType.add(OutNetCovType);
            }
            TempMapInOutNetwork.put(nodeType,inNetworkListCoverageType);
        }
        return TempMapInOutNetwork;
    } 
    
    /* Method for preparing the DTO structure and assigning the Map to DTO objects 
    * <p>
    * @param  string selected PAR value .
    * @param  Map<string,list<CoverageType_DTO_HUM>> for Non Limit OutOfNetwork .
    * @param  Map<string,list<CoverageType_DTO_HUM>> for Non Limit InNetwork
    * @param  Map<string,list<CoverageType_DTO_HUM>> for Non Limit In and Out Network
    * @param  Map<string,list<CoverageType_DTO_HUM>> for Limits of In Network 
    * @param  Map<string,list<CoverageType_DTO_HUM>> for Limits of Out Network
    * @param  Map<string,list<CoverageType_DTO_HUM>> for Limits of In and Out Network
    * @return PBEBenefitsSearch_DTO_HUM object
    */
    public static PBEBenefitsSearch_DTO_HUM prepareResponseStructure(Map<string,Boolean> isCoveredServiceInNetworks, String ParValue,Map<string,List<CoverageType_DTO_HUM>> lstOfLimitsInNetwork, Map<string,List<CoverageType_DTO_HUM>>lstOfLimitsOutNetwork , Map<string,List<CoverageType_DTO_HUM>> lstOfLimitsInOutNetwork,Map<string,list<CoverageType_DTO_HUM>> finalMapofNonLimitInNetwork,Map<string,list<CoverageType_DTO_HUM>> finalMapofNonLimitOutNetwork , Map<string,list<CoverageType_DTO_HUM>>finalMapofNonLimitInOutNetwork, Map<string,List<string>> lstOfServiceComments)
    {
        
        PBEBenefitsSearch_DTO_HUM benefitsSearchDTOObj = new PBEBenefitsSearch_DTO_HUM();
        CoverageType_DTO_HUM covDtoObj = new CoverageType_DTO_HUM();
        covDtoObj.BenefitDescription = 'Not Covered';
        Map<string,List<CoverageType_DTO_HUM>> lstLimit = new Map<string,List<CoverageType_DTO_HUM>>();
        Map<string,List<string>> lstComment = new Map<string,List<string>>();
        
        if(lstOfServiceComments != null && lstOfServiceComments.size() > 0) benefitsSearchDTOObj.lstOfServiceComments = lstOfServiceComments;
        
        if(ParValue == VOB_PAR_YES_HUM)
        {
            if((isCoveredServiceInNetworks.size() == 1) && (!(isCoveredServiceInNetworks.get(VOB_BENEFITSEARCH_INNETWORK_HUM))))
            {
                
                if(benefitsSearchDTOObj.LimitFalseLst == null) benefitsSearchDTOObj.LimitFalseLst = new List<CoverageType_DTO_HUM>();
                benefitsSearchDTOObj.LimitFalseLst.add(covDtoObj);
                benefitsSearchDTOObj.lstOfLimits = lstLimit;
                benefitsSearchDTOObj.lstOfServiceComments = lstComment;
                
            }
            else
            {
                for(string nodeType : finalMapofNonLimitInNetwork.keyset())         
                {
                    if(benefitsSearchDTOObj.LimitFalseLst == null) benefitsSearchDTOObj.LimitFalseLst = new List<CoverageType_DTO_HUM>();
                    benefitsSearchDTOObj.LimitFalseLst.addAll(finalMapofNonLimitInNetwork.get(nodeType));
                }
                benefitsSearchDTOObj.lstOfLimits = lstOfLimitsInNetwork; 
            }       
        }
        else if(ParValue == VOB_PAR_NO_HUM)
        {
            if((isCoveredServiceInNetworks.size() == 1) && (!(isCoveredServiceInNetworks.get(VOB_BENEFITSEARCH_OUTNETWORK_HUM))))
            {
                if(benefitsSearchDTOObj.LimitFalseLst == null) benefitsSearchDTOObj.LimitFalseLst = new List<CoverageType_DTO_HUM>();
                benefitsSearchDTOObj.LimitFalseLst.add(covDtoObj);
                benefitsSearchDTOObj.lstOfLimits = lstLimit;
                benefitsSearchDTOObj.lstOfServiceComments = lstComment;
            }
            else
            {
                for(string nodeType : finalMapofNonLimitOutNetwork.keyset())            
                {
                    if(benefitsSearchDTOObj.LimitFalseLst == null) benefitsSearchDTOObj.LimitFalseLst = new List<CoverageType_DTO_HUM>();
                    benefitsSearchDTOObj.LimitFalseLst.addAll(finalMapofNonLimitOutNetwork.get(nodeType));
                }   
                benefitsSearchDTOObj.lstOfLimits = lstOfLimitsOutNetwork; 
            }      
        }
        else if(ParValue == VOB_BOTH)
        {
            if((isCoveredServiceInNetworks.size() == 2) && (!(isCoveredServiceInNetworks.get(VOB_BENEFITSEARCH_OUTNETWORK_HUM))) && (!(isCoveredServiceInNetworks.get(VOB_BENEFITSEARCH_INNETWORK_HUM))))
            {
                if(benefitsSearchDTOObj.LimitFalseLst == null) benefitsSearchDTOObj.LimitFalseLst = new List<CoverageType_DTO_HUM>();
                benefitsSearchDTOObj.LimitFalseLst.add(covDtoObj);
                benefitsSearchDTOObj.lstOfLimits = lstLimit;
                benefitsSearchDTOObj.lstOfServiceComments = lstComment;
            }
            else
            {
                for(string nodeType : finalMapofNonLimitInOutNetwork.keyset())          
                {
                    if(benefitsSearchDTOObj.LimitFalseLst == null) benefitsSearchDTOObj.LimitFalseLst = new List<CoverageType_DTO_HUM>();
                    benefitsSearchDTOObj.LimitFalseLst.addAll(finalMapofNonLimitInOutNetwork.get(nodeType));
                }   
                benefitsSearchDTOObj.lstOfLimits = lstOfLimitsInOutNetwork; 
            }    
        }
        return benefitsSearchDTOObj;
    }
    
    @testvisible private static void updatebenSearchServiceCommentsArray(List<ProductBusinessEntityResponseV2_H_HUM.PlanComment> planCommentsLst, Map<string, List<string>> lstOfServiceComments,String ServiceTypeID)
	{
		if(planCommentsLst != null && planCommentsLst.size() > 0)
		{
			List<String> listOfComments = new List<String>();
			for (ProductBusinessEntityResponseV2_H_HUM.PlanComment oPlanComments : planCommentsLst)
			{
				if (oPlanComments != null && string.isNotBlank(oPlanComments.Comment))
				{
					listOfComments.add(oPlanComments.Comment);
				}
			}
			lstOfServiceComments.put(ServiceTypeID, listOfComments);
		}
	}
    
	/* Method to show service type comments order based on PlanCommentSequenceNumber 
	* <p>
	* @param List<ProductBusinessEntityResponseV2_H_HUM.PlanComment> planCommentsLst, Map<string, List<string>> lstOfServiceComments,String ServiceTypeID  .
	* @return void
	*/
	@testvisible private static void updatebenSearchServiceCommentsArraySubVerion3(List<ProductBusinessEntityResponseV2_H_HUM.PlanComment> planCommentsLst, Map<string, List<string>> lstOfServiceComments,String ServiceTypeID)
	{
        Integer sequenceNo;
        Set<Integer> sequenceSet;
        List<Integer> sequenceList;
        Map<Integer,String> commentWithSequenceMap = new Map<Integer,String>();
        
		if(planCommentsLst != null && planCommentsLst.size() > 0)
		{
			List<String> listOfComments = new List<String>();
			integer count = 0;
			for (ProductBusinessEntityResponseV2_H_HUM.PlanComment oPlanComments : planCommentsLst)
			{
				 
				count++;
                if (oPlanComments != null && string.isNotBlank(oPlanComments.Comment) && oPlanComments.PlanCommentSequenceNumber == null )
                {
                    listOfComments.add(oPlanComments.Comment);
                }
                else if(oPlanComments != null && string.isNotBlank(oPlanComments.Comment) && oPlanComments.PlanCommentSequenceNumber != null){
                    sequenceNo = integer.ValueOf(oPlanComments.PlanCommentSequenceNumber + count);
                    commentWithSequenceMap.put(sequenceNo,oPlanComments.Comment);
                }
            }
            
            if(!commentWithSequenceMap.isEmpty()){
                sequenceSet = commentWithSequenceMap.keySet();
                sequenceList = new List<Integer>(sequenceSet);
                
				sequenceList.sort();
                
				//Service Type Comments in descending Order 
                for(integer i = sequenceList.size()-1; i >= 0; i--){
                    listOfComments.add(commentWithSequenceMap.get(sequenceList[i]));
                }
            }
			lstOfServiceComments.put(ServiceTypeID,listOfComments);
        }
	}
    
    /* Method for parsing of InNetwork and OutOfNetwork benefit type 
    * <p>
    * @param  string selected PAR value .
    * @param  string benefit type .
    * @param List<ProductBusinessEntityResponseV2_H_HUM.Benefit>
    * @param  Map<string,list<CoverageType_DTO_HUM>> for InNetwork .
    * @param  Map<string,list<CoverageType_DTO_HUM>> for OutOfNetwork .
    * @return nothing
    */
    @testvisible private static void updatebenSearchResArray (Map<string,list<CoverageType_DTO_HUM>> inNetworkMap,Map<string,list<CoverageType_DTO_HUM>> outNetworkMap, List<ProductBusinessEntityResponseV2_H_HUM.Benefit> BenefitLst,string parVal,string nodeTypeValue, Map<string,Boolean> isCoveredServiceInNetworks,Boolean bPlatformLV,String ServiceCatCode, String ServiceTypeID)
    {
        
        Boolean inNetworkIsCovered = false;
        Boolean outNetworkIsCovered = false;
        Boolean inNetwork = false;
        Boolean outNetwork = false;
        for(Integer j = 0 ; j < BenefitLst.size() ; j++)
        {
            if (parVal == VOB_PAR_YES_HUM || parVal == VOB_BOTH) 
            {
                if (string.isNotBlank(BenefitLst[j].NetworkType) && BenefitLst[j].NetworkType == VOB_BENEFITSEARCH_INNETWORK_HUM)
                {   
                    if(string.isNotBlank(BenefitLst[j].IsCoveredService))
                    {
                        if(!inNetwork)    inNetwork = true;  
                        if(!inNetworkIsCovered && BenefitLst[j].IsCoveredService.equalsIgnoreCase('True'))    inNetworkIsCovered  = true; 
                    }
                    if(BenefitLst[j].BenefitDetailList != null && !BenefitLst[j].BenefitDetailList.BenefitDetail.isEmpty())
                    {
                        getAssignInNetworkValue(BenefitLst[j].BenefitDetailList.BenefitDetail,inNetworkMap,nodeTypeValue,parVal,bPlatformLV, ServiceCatCode,  ServiceTypeID);
                    }
                }
            }
            if (parVal == VOB_PAR_NO_HUM || parVal == VOB_BOTH)
            {
                if (string.isNotBlank(BenefitLst[j].NetworkType) &&BenefitLst[j].NetworkType == VOB_BENEFITSEARCH_OUTNETWORK_HUM)
                {   
                    if(string.isNotBlank(BenefitLst[j].IsCoveredService))
                    {
                        if(!outNetwork)    outNetwork = true;  
                        if(!outNetworkIsCovered && BenefitLst[j].IsCoveredService.equalsIgnoreCase('True'))    outNetworkIsCovered = true; 
                    }
                    if(BenefitLst[j].BenefitDetailList != null && !BenefitLst[j].BenefitDetailList.BenefitDetail.isEmpty())
                    {
                        getAssignOutNetworkValue(BenefitLst[j].BenefitDetailList.BenefitDetail,outNetworkMap,nodeTypeValue,parVal,bPlatformLV, ServiceCatCode,  ServiceTypeID);
                    }
                }
            }
        }
        if(inNetwork)    isCoveredServiceInNetworks.put(VOB_BENEFITSEARCH_INNETWORK_HUM, inNetworkIsCovered);
        if(outNetwork)    isCoveredServiceInNetworks.put(VOB_BENEFITSEARCH_OUTNETWORK_HUM, outNetworkIsCovered);
    }
            
    /* Method for parsing of InNetwork values and prepare In network map with both values Limits and Non Limits values 
    * <p>
    * @param List<ProductBusinessEntityResponseV2_H_HUM.BenefitAmount> .
    * @param  string selected PAR value .
    * @param  string Benefit type value .
    * @param  Map<string,list<CoverageType_DTO_HUM>> for InNetwork .
    * @return nothing
    */
    //REQ-410445 Add BenefitDetail node in argument to access service ada code 
    private static void assignInNetworkValues(ProductBusinessEntityResponseV2_H_HUM.BenefitDetail benefitDetails, List<ProductBusinessEntityResponseV2_H_HUM.BenefitAmount> pbeBenAmtLst , Map<string,list<CoverageType_DTO_HUM>> networkMap , String nodeTypeValue , String sPar,string benefitsType,string placeOfServiceCode,string tierNumber, String isCoveredBenefit, String parCodeIDListIn, Boolean bPlatformLV,String ServiceCatCode, String ServiceTypeID)
    
    {			
             prepareNodeStructureInNetwork(benefitDetails, pbeBenAmtLst,networkMap,nodeTypeValue,sPar,benefitsType,placeOfServiceCode,tierNumber,isCoveredBenefit,parCodeIDListIn,bPlatformLV,ServiceCatCode, ServiceTypeID);
        
    }
     
     
    /* Method for parsing of OutOfNetwork values and prepare Out network map with both values Limits and Non Limits values 
    * <p>
    * @param List<ProductBusinessEntityResponseV2_H_HUM.BenefitAmount> .
    * @param  string selected PAR value .
    * @param  string Benefit type value .
    * @param  string tierNumber value .
    * @param  Map<string,list<CoverageType_DTO_HUM>> for OutOfNetwork .
    * @return nothing
    */
    private static void assignOutNetworkValues(List<ProductBusinessEntityResponseV2_H_HUM.BenefitAmount> pbeBenAmtLst , Map<string,list<CoverageType_DTO_HUM>> networkMap , String nodeTypeValue , String sPar,string benefitsType,string placeOfServiceCode,string tierNumber, String isCoveredBenefit, String parCodeIDListOut, Boolean bPlatformLV, String ServiceCatCode, String ServiceTypeID)
    {
       

            prepareNodeStructureOutNetwork(pbeBenAmtLst,networkMap,nodeTypeValue,sPar,benefitsType,placeOfServiceCode,tierNumber,isCoveredBenefit,parCodeIDListOut,bPlatformLV, ServiceCatCode,  ServiceTypeID);
        
    }
    
    /* Method for assigning values to DTO object  
    * <p>
    * @param CoverageType_DTO_HUM object .
    * @param  Integer .
    * @param  List<ProductBusinessEntityResponseV2_H_HUM.BenefitAmount> .
    * @param  Map<string,list<CoverageType_DTO_HUM>> for InNetwork .
    * @param  string Benefit Type .
    * @param  string tierNumber Value .
    * @return nothing
    */
    private static void createCoverageDTO(CoverageType_DTO_HUM covDtoObj , Integer index , List<ProductBusinessEntityResponseV2_H_HUM.BenefitAmount> pbeBenAmtLst , String nodeTypeValue,string benefitsType,string placeOfServiceCode,string tierNumber,String isCoveredBenefit,String parCodeIDList, Boolean bPlatformLV,String ServiceCatCode, String ServiceTypeID)
    {
        
        covDtoObj.nodeType = nodeTypeValue;        
		 if(string.isNotBlank(ServiceCatCode))  covDtoObj.ServiceCatCode = ServiceCatCode;
		 if(string.isNotBlank(ServiceTypeID))  covDtoObj.ServiceTypeID = ServiceTypeID;
		
        if(!bPlatformLV && string.isNotBlank(pbeBenAmtLst[index].BenefitDescription))  covDtoObj.BenefitDescription = pbeBenAmtLst[index].BenefitDescription;
        if(bPlatformLV && string.isNotBlank(parCodeIDList))  covDtoObj.BenefitDescription = parCodeIDList;
        if(string.isNotBlank(pbeBenAmtLst[index].ProviderSubNetworkNumber))  covDtoObj.ProviderSubNetworkNumber = pbeBenAmtLst[index].ProviderSubNetworkNumber;        
        if(string.isNotBlank(pbeBenAmtLst[index].IsLimit))  covDtoObj.isLimit = pbeBenAmtLst[index].isLimit;
        if(string.isNotBlank(pbeBenAmtLst[index].CoverageType)) covDtoObj.CoverageType = pbeBenAmtLst[index].CoverageType;
        if(string.isNotBlank(tierNumber)) covDtoObj.TierNumber = TierNumber;
        if(string.isNotBlank(isCoveredBenefit)) covDtoObj.sIsCoveredBenefit = isCoveredBenefit;
        if(string.isNotBlank(benefitsType)) covDtoObj.benefitType = benefitsType;
        covDtoObj.placeOfServiceCode = (string.isNotBlank(placeOfServiceCode)) ? placeOfServiceCode : '';
        covDtoObj.limitName = (pbeBenAmtLst[index].LimitName != 'None') ? pbeBenAmtLst[index].LimitName : pbeBenAmtLst[index].BenefitDescription;
        if(pbeBenAmtLst[index].UnitDetail != null)
        {
            String limitText ='';
              if(bPlatformLV)
              {
              
               if(string.isNotBlank(pbeBenAmtLst[index].UnitDetail.QuantityType ) && pbeBenAmtLst[index].UnitDetail.QuantityType != 'None' ) limitText =   pbeBenAmtLst[index].UnitDetail.QuantityType;
               if(string.isNotBlank(pbeBenAmtLst[index].UnitDetail.TimePeriodQualifierType) && pbeBenAmtLst[index].UnitDetail.TimePeriodQualifierType != 'None') limitText =  limitText + ' per '+ pbeBenAmtLst[index].UnitDetail.TimePeriodQualifierType;
              
              }
              else
              {
                 if(string.isNotBlank(pbeBenAmtLst[index].UnitDetail.Notes)) limitText =  pbeBenAmtLst[index].UnitDetail.Notes;
              }
			covDtoObj.limitText = limitText;
            if(string.isNotBlank(pbeBenAmtLst[index].UnitDetail.Quantity)) covDtoObj.limitAmount = getFormattedValue(pbeBenAmtLst[index].UnitDetail);
            if(pbeBenAmtLst[index].UnitDetail.QuantityPeriod != null && string.isNotBlank(pbeBenAmtLst[index].UnitDetail.QuantityPeriod.Notes))
            covDtoObj.limitComment = pbeBenAmtLst[index].UnitDetail.QuantityPeriod.Notes;
            covDtoObj.notes = pbeBenAmtLst[index].UnitDetail.Notes;
        } 
    }
    
    /* Method for appending $ or % sign after quantity based of the UnitType  
    * <p>
    * @param  ProductBusinessEntityResponseV2_H_HUM.UnitDetail .
    * @return string 
    */
    private static string getFormattedValue(ProductBusinessEntityResponseV2_H_HUM.UnitDetail oUnitDetail)
    {
        
        if( oUnitDetail != null  && string.isNotBlank(oUnitDetail.UnitType) &&  oUnitDetail.UnitType == VOB_BENEFITSEARCH_DOLLAR_HUM)
        {
            return VOB_ACCUMS_DOLLAR_SIGN_HUM + oUnitDetail.Quantity;
        }
        else if( oUnitDetail != null  && string.isNotBlank(oUnitDetail.UnitType) &&  oUnitDetail.UnitType == VOB_BENEFITSEARCH_PERCENTAGE_HUM)
        {
            return oUnitDetail.Quantity + VOB_ACCUMS_PERCENTAGE_SIGN_HUM ;
        }
        else
        {
            return oUnitDetail.Quantity ;
        }
    }
    
    /* Method for parsing of outNetwork values and prepare Out network map with both values Limits and Non Limits values 
    * <p>
    * @param List<ProductBusinessEntityResponseV2_H_HUM.BenefitAmount> .
    * @param  string selected PAR value .
    * @param  string Node type value .
    * @param  string tierNumber value .
    * @param  Map<string,list<CoverageType_DTO_HUM>> for OutNetwork .
    * @return nothing
    */
    private static void prepareNodeStructureOutNetwork(List<ProductBusinessEntityResponseV2_H_HUM.BenefitAmount> pbeBenAmtLst , Map<string,list<CoverageType_DTO_HUM>> networkMap , String nodeTypeValue , String sPar,string benefitsType,string placeOfServiceCode,string tierNumber,string isCoveredBenefit, String parCodeIDListOut, Boolean bPlatformLV,String ServiceCatCode, String ServiceTypeID)
    {
        
        for (Integer index = 0; index < pbeBenAmtLst.size(); index++)
        {
            CoverageType_DTO_HUM covDtoObj = new CoverageType_DTO_HUM();
            covDtoObj.Networktype = 'OUT';
            if(pbeBenAmtLst[index] != null && string.isNotBlank(pbeBenAmtLst[index].IsLimit))
            {
                if(pbeBenAmtLst[index].UnitDetail != null && string.isNotBlank(pbeBenAmtLst[index].UnitDetail.Quantity)) 
                {
                    covDtoObj.NonPar = getFormattedValue(pbeBenAmtLst[index].UnitDetail);
                }
                createCoverageDTO(covDtoObj, index, pbeBenAmtLst,nodeTypeValue,benefitsType,placeOfServiceCode,tierNumber,isCoveredBenefit,parCodeIDListOut,bPlatformLV, ServiceCatCode,  ServiceTypeID);
                if(networkMap!= null && networkMap.containsKey(nodeTypeValue))
                {
                    List<CoverageType_DTO_HUM> listcovType = networkMap.get(nodeTypeValue);
                    listcovType.add(covDtoObj);
                }
                else
                {
                    list<CoverageType_DTO_HUM> lstCoverages = new List<CoverageType_DTO_HUM>();
                    lstCoverages.add(covDtoObj) ;
                    networkMap.put(nodeTypeValue,lstCoverages);
                }
            }
        }
    }
    
    /* Method for parsing of InNetwork values and prepare In network map with both values Limits and Non Limits values 
    * <p>
    * @param List<ProductBusinessEntityResponseV2_H_HUM.BenefitAmount> .
    * @param  string selected PAR value .
    * @param  string Node type value .
    * @param  string TierNumber value .
    * @param  Map<string,list<CoverageType_DTO_HUM>> for InNetwork .
    * @return nothing
    */
   //REQ-410445 Add BenefitDetail node to access service ada code
    private static void prepareNodeStructureInNetwork(ProductBusinessEntityResponseV2_H_HUM.BenefitDetail details,  List<ProductBusinessEntityResponseV2_H_HUM.BenefitAmount> pbeBenAmtLst , Map<string,list<CoverageType_DTO_HUM>> networkMap , String nodeTypeValue , String sPar,string benefitsType,string placeOfServiceCode,string tierNumber, String isCoveredBenefit, String parCodeIDListIn, Boolean bPlatformLV,String ServiceCatCode, String ServiceTypeID)
    {
        //REQ-410445 Add Ada code in wrapper
        String  mapAdaCodes = '';
        if (details != null && details.ServiceAdaCodeList != null && details.ServiceAdaCodeList.Code != null)
        {
            for (string adaCodes : details.ServiceAdaCodeList.Code)
            {
                mapAdaCodes = adaCodes;
				if(String.isNotBlank(mapAdaCodes))
                	break;
            }
        }
        for (Integer index = 0; index < pbeBenAmtLst.size(); index++)
        {
            CoverageType_DTO_HUM covDtoObj = new CoverageType_DTO_HUM();
            covDtoObj.Networktype = 'IN';
            if(pbeBenAmtLst[index] != null && string.isNotBlank(pbeBenAmtLst[index].IsLimit))
            {
                if(pbeBenAmtLst[index].UnitDetail != null && string.isNotBlank(pbeBenAmtLst[index].UnitDetail.Quantity)) 
                {
                    covDtoObj.Par = getFormattedValue(pbeBenAmtLst[index].UnitDetail);
                }
				//REQ-410445 Add in wrapper
                covDtoObj.AdaCode = mapAdaCodes;
                createCoverageDTO(covDtoObj, index, pbeBenAmtLst,nodeTypeValue,benefitsType,placeOfServiceCode,tierNumber,isCoveredBenefit,parCodeIDListIn,bPlatformLV,ServiceCatCode,  ServiceTypeID);
                if(networkMap!= null && networkMap.containsKey(nodeTypeValue))
                {
                    List<CoverageType_DTO_HUM> listcovType = networkMap.get(nodeTypeValue);
                    listcovType.add(covDtoObj);
                }
                else
                {
                    list<CoverageType_DTO_HUM> lstCoverages = new List<CoverageType_DTO_HUM>();
                    lstCoverages.add(covDtoObj) ;
                    networkMap.put(nodeTypeValue,lstCoverages);
                }
            }
        }
    }
    
    /* Method for parsing of InNetwork and OutOfNetwork benefit type 
    * <p>
    * @param  string selected PAR value .
    * @param  list<ProductBusinessEntityResponseV2_H_HUM.BenefitDetail>
    * @param  string node value .
    * @param List<ProductBusinessEntityResponseV2_H_HUM.Benefit>
    * @param  Map<string,list<CoverageType_DTO_HUM>> for InNetwork .
    * @return nothing
    */
    private static void getAssignInNetworkValue(list<ProductBusinessEntityResponseV2_H_HUM.BenefitDetail> pbeBenDtlList,Map<string,list<CoverageType_DTO_HUM>> inNetworkMap,string nodeTypeValue,string parVal,Boolean bPlatformLV,String ServiceCatCode, String ServiceTypeID)
    {
        
        string tierNumber = null;
        string isCoveredBenefit = null;
        for(ProductBusinessEntityResponseV2_H_HUM.BenefitDetail pbeBenDtl : pbeBenDtlList)
        {   
            tierNumber = null;
            isCoveredBenefit = null;
            if(pbeBenDtl != null && string.isNotBlank(pbeBenDtl.TierNumber) && !'None'.equalsIgnoreCase(pbeBenDtl.TierNumber))
            {
                tierNumber = pbeBenDtl.TierNumber;
            }
            if(pbeBenDtl != null && string.isNotBlank(pbeBenDtl.IsCoveredBenefit) && !'None'.equalsIgnoreCase(pbeBenDtl.IsCoveredBenefit))
            {
                isCoveredBenefit = pbeBenDtl.IsCoveredBenefit;
            }
            if(pbeBenDtl != null && pbeBenDtl.BenefitAmountList!= null && !pbeBenDtl.BenefitAmountList.BenefitAmount.isEmpty())
            {
                if(pbeBenDtl != null && string.isNotBlank(pbeBenDtl.BenefitType)) nodeTypeValue = pbeBenDtl.BenefitType+'_Service';
                if(pbeBenDtl.PlaceOfServiceCodeList != null && pbeBenDtl.PlaceOfServiceCodeList.Code != null && pbeBenDtl.PlaceOfServiceCodeList.Code.size() > 0)
                {
                    for (string placeOfServiceCodeIn : pbeBenDtl.PlaceOfServiceCodeList.Code)
                    {
                        if(bPlatformLV && pbeBenDtl.ParCodeIDList != null && pbeBenDtl.ParCodeIDList.Code != null && pbeBenDtl.ParCodeIDList.Code.size() > 0)
                        {
                            for (string parCodeIDListIn : pbeBenDtl.ParCodeIDList.Code)
                            {
                                    assignInNetworkValues(pbeBenDtl,pbeBenDtl.BenefitAmountList.BenefitAmount,inNetworkMap,'Service',parVal,nodeTypeValue,placeOfServiceCodeIn,tierNumber,isCoveredBenefit,parCodeIDListIn,bPlatformLV, ServiceCatCode,  ServiceTypeID);
                            }
                        }
                        else    assignInNetworkValues(pbeBenDtl,pbeBenDtl.BenefitAmountList.BenefitAmount,inNetworkMap,'Service',parVal,nodeTypeValue,placeOfServiceCodeIn,tierNumber,isCoveredBenefit,'',bPlatformLV,ServiceCatCode, ServiceTypeID);         
                    }
                }
            }
        }
    }
    
    /* Method for parsing of InNetwork and OutOfNetwork benefit type 
    * <p>
    * @param  string selected PAR value .
    * @param  list<ProductBusinessEntityResponseV2_H_HUM.BenefitDetail>
    * @param  string node value .
    * @param List<ProductBusinessEntityResponseV2_H_HUM.Benefit>
    * @param  Map<string,list<CoverageType_DTO_HUM>> for OutNetwork .
    * @return nothing
    */
    private static void getAssignOutNetworkValue(list<ProductBusinessEntityResponseV2_H_HUM.BenefitDetail> pbeBenDtlList,Map<string,list<CoverageType_DTO_HUM>> outNetworkMap,string nodeTypeValue,string parVal,Boolean bPlatformLV,String ServiceCatCode, String ServiceTypeID)
    {
       
        string tierNumber = null;
        string isCoveredBenefit = ''; 
        for(ProductBusinessEntityResponseV2_H_HUM.BenefitDetail pbeBenDtl : pbeBenDtlList)
        {
            tierNumber = null;
            if(pbeBenDtl != null && string.isNotBlank(pbeBenDtl.IsCoveredBenefit) && !'None'.equalsIgnoreCase(pbeBenDtl.IsCoveredBenefit))
            {
                isCoveredBenefit = pbeBenDtl.IsCoveredBenefit;
            }
            if(pbeBenDtl != null && string.isNotBlank(pbeBenDtl.TierNumber) && !'None'.equalsIgnoreCase(pbeBenDtl.TierNumber))
            {
                tierNumber = pbeBenDtl.TierNumber;
            }
            if(pbeBenDtl != null && pbeBenDtl.BenefitAmountList!= null && !pbeBenDtl.BenefitAmountList.BenefitAmount.isEmpty())
            {
                if(pbeBenDtl != null && string.isNotBlank(pbeBenDtl.BenefitType)) nodeTypeValue = pbeBenDtl.BenefitType+'_Service';
                if(pbeBenDtl.PlaceOfServiceCodeList != null && pbeBenDtl.PlaceOfServiceCodeList.Code != null && pbeBenDtl.PlaceOfServiceCodeList.Code.size() > 0)
                {
                    for (string placeOfServiceCodeOut : pbeBenDtl.PlaceOfServiceCodeList.Code)
                    {
                        if(bPlatformLV && pbeBenDtl.ParCodeIDList != null && pbeBenDtl.ParCodeIDList.Code != null && pbeBenDtl.ParCodeIDList.Code.size() > 0)
                        {
							//REQ-410445 Add detail node to acess ada code , same for else case also 
                            for (string parCodeIDListOut : pbeBenDtl.ParCodeIDList.Code)
                            {
                                assignOutNetworkValues(pbeBenDtl.BenefitAmountList.BenefitAmount,outNetworkMap,'Service',parVal,nodeTypeValue,placeOfServiceCodeOut,tierNumber,isCoveredBenefit,parCodeIDListOut,bPlatformLV,ServiceCatCode,ServiceTypeId);
                            }
                        }
                        else   assignOutNetworkValues(pbeBenDtl.BenefitAmountList.BenefitAmount,outNetworkMap,'Service',parVal,nodeTypeValue,placeOfServiceCodeOut,tierNumber,isCoveredBenefit,'',bPlatformLV,ServiceCatCode,ServiceTypeId);    
                    }
                }                
            }
        }
    }
    
    /* Returns request for Benefits Search
    * <p>
    * Method to get the Benefits Search request
    *
    * @param String           Service Category Id
    * @param String           Type Of Service
    * @param String           Place of Service
    * @param String           Par /non Par
    * @param String           Platform Code
    * @param String           Product Key
    * @param String           Ref Date
    * @return                 ProductBusinessEntityRequestV2_H_HUM
    */
    public static ProductBusinessEntityRequestV2_H_HUM RequestStructureBenefitSearch(List<String> sServiceCategory, String sTypeOfService, String sPlaceOfService, String sPar, string platformCode, String productKey, string refdate,Boolean isCAS, string ADACode)
    {
        

        ProductBusinessEntityRequestV2_H_HUM oRequest = new ProductBusinessEntityRequestV2_H_HUM();
        ProductBusinessEntityRequestV2_H_HUM.GetProduct productNode = new ProductBusinessEntityRequestV2_H_HUM.GetProduct();

        productNode.AsOfDate = refdate.substring(6, 10) + '-' + refdate.substring(0, 2) + '-' + refdate.substring(3, 5);

        ProductBusinessEntityRequestV2_H_HUM.ProductIdentifier PrId = new ProductBusinessEntityRequestV2_H_HUM.ProductIdentifier();
        PrId.ProductId = productKey;
        if (platformCode == VOB_PLATFORM_EM_HUM)
        {
            PrId.ProductIdType = VOB_BENEFITPACKAGEID_HUM;
        }
        else if (platformCode == VOB_PLATFORM_LV_HUM)
        {
            PrId.ProductIdType = VOB_VALUEPDQ_HUM;
        }

        productNode.AsOfDate = refdate;
		
        boolean sServiceTypeCommentSwitch = false;            
        sServiceTypeCommentSwitch = HUMUtilityHelper.isCRMFunctionalityON('732113');
		
        //newly added for  PBE18.08
        if(sServiceTypeCommentSwitch == true)
            productNode.SubVersion = '3';
        else
            productNode.SubVersion = '2';
       
	    productNode.ProductIdentifier = PrId;

        ProductBusinessEntityRequestV2_H_HUM.IncludeList IncNode = new ProductBusinessEntityRequestV2_H_HUM.IncludeList();
        list<String> lstIncludeTypes = new list<String>{VOB_PBESERVICEDETAIL_HUM,VOB_PLANCOMMENTS_HUM};
        IncNode.IncludeType = lstIncludeTypes;
        productNode.IncludeList = IncNode;

        ProductBusinessEntityRequestV2_H_HUM.IncludeAdditionalDetail oIncAddDtl = new ProductBusinessEntityRequestV2_H_HUM.IncludeAdditionalDetail();
        oIncAddDtl.NonCoveredBenefit = 'true';
        oIncAddDtl.MasterServicesWithAdaCode = 'true';
        productNode.IncludeAdditionalDetail = oIncAddDtl;

        ProductBusinessEntityRequestV2_H_HUM.ICDCodeList icdCode = new ProductBusinessEntityRequestV2_H_HUM.ICDCodeList();
        icdCode.ICDCode = new List<string>();
        icdCode.ICDCode.add('?');
        productNode.ICDCodeList = icdCode;
        ProductBusinessEntityRequestV2_H_HUM.ServiceDetail serviceDetail = new ProductBusinessEntityRequestV2_H_HUM.ServiceDetail();
        serviceDetail.ServiceCategoryCode = sServiceCategory;
        serviceDetail.ServiceTypeId = sTypeOfService;
        serviceDetail.PlaceOfServiceCode = sPlaceOfService;
        serviceDetail.AdaCode = '';
        if(!isCAS)
        {
        serviceDetail.Par = (String.isNotblank(sPar) && sPar != VOB_PAR_DEFAULT_HUM) ? sPar : VOB_BOTH;
        }
        else
        {
            serviceDetail.Par =  VOB_BOTH;
            serviceDetail.ParCode = (String.isNotblank(sPar) && sPar != VOB_PARCODE_DEFAULT_HUM) ? sPar : '';
        }
        productNode.ServiceDetail = serviceDetail;
        oRequest.GetProduct = productNode;
        return oRequest;
    }
    //==============================END Group Search changes ==================================================
    /**
    * Returns Policy_Plan_Details__c object that can then be utilized for showing Benefits Information.  *
    * This method returns SOQL result of policy member.
    *
    * @param   benefitsCls               instance of the Controller class.
    * @return  Policy_Plan_Details__c    return the policy plan detail object
    */
    public static Policy_Plan_Details__c getPolicyPlan_SOQL(policy__c policyObject, string asOfDateUnformatted)
    {
        List<Policy_Plan_Details__c> objPolicyPlan = [Select Id , Benefit_Coverage__c, Coverage_Plan_Effective_Date__c, Coverage_Plan_End_Date__c , Product__r.Sold_Product_Key_Value__c,Product__r.Name FROM Policy_Plan_Details__c WHERE Policy__c = :policyObject.id AND ETL_Record_Deleted__c = false];
        if(!objPolicyPlan.isEmpty())
        {
            if(objPolicyPlan.size() == 1)
            {
                return objPolicyPlan[0];
            }
            else 
            {
			
                String sAsofDate = asOfDateUnformatted.split(HYPHEN_HUM)[1] + BACKSLASH_HUM + asOfDateUnformatted.split(HYPHEN_HUM)[2] + BACKSLASH_HUM + asOfDateUnformatted.split(HYPHEN_HUM)[0];
                for(Policy_Plan_Details__c objPlan : objPolicyPlan)
				{

                    if(MAX_DATE_HUM == objPlan.Coverage_Plan_End_Date__c || ( MAX_DATE_HUM != objPlan.Coverage_Plan_End_Date__c  &&
                    (Date.parse(objPlan.Coverage_Plan_Effective_Date__c) <=  Date.Parse(sAsofDate)  && Date.parse(sAsofDate)  <= Date.parse(objPlan.Coverage_Plan_End_Date__c))))
                    {
			
                        return objPlan;
                    }
                }
            }
        }
       return null;
    }
    /*
    * Method to get As Of Date on basis on Policy Member End Date
    * @param  String EndDate of Policy Member .
    * @return String
    */
    public static String populateAsOfDate(Policy__c  objPolicy)
    {
        //Date Formatting Criteria
        if (ApexPages.currentpage().getParameters().containsKey('refdate') && ApexPages.currentpage().getParameters().get('refdate') != '')
        {
            string s = getHTMLEncodedPageParameter('refdate');
            return s.substring(4, 8) + HYPHEN_HUM + s.substring(0, 2) + HYPHEN_HUM + s.substring(2, 4);
        }
        
        else if (string.isNotBlank(objPolicy.Coverage_Plan_end_date__c) && VOB_TERMEDDATE_HUM <> objPolicy.Coverage_Plan_end_date__c )
        {
            return objPolicy.Coverage_Plan_end_date__c.split(BACKSLASH_HUM)[2] + HYPHEN_HUM + objPolicy.Coverage_Plan_end_date__c.split(BACKSLASH_HUM)[0] + HYPHEN_HUM + objPolicy.Coverage_Plan_end_date__c.split(BACKSLASH_HUM)[1];
        }
        else
        {
            return string.valueof(date.today());
        }
    }
     /*
    * Method to get date of service to be displayed on Current Benefit Period section
    * @param  NA
    * @return String
    */
    public static String populateDateofService(){
         if (ApexPages.currentpage().getParameters().containsKey('refdate') && ApexPages.currentpage().getParameters().get('refdate') != '')
            {
                string s = ApexPages.currentpage().getParameters().get('refdate');
                return s.substring(4, 8) + HYPHEN_HUM + s.substring(0, 2) + HYPHEN_HUM + s.substring(2, 4); 
            }
            else
            {
                DateTime d = datetime.now();
                return d.format('MM/dd/yyyy');
            }
    }
    /**
    * Returns GBEServiceRequest_H_HUM object that can then be utilized for sending the webservice request.
    * This method is to create the request Structure for GBE service .
    *
    * @param    PolMemobj   an policy member record object derived from SOQL results.
    * @param    string  refdate.
    * @return  GBEServiceRequest_H_HUM      request structure of the wrapper class GBEServiceRequest_H_HUM
    */
    public static GBEServiceRequest_H_HUM RequestStructureGBEService(Policy__c Polobj, Policy_Plan_Details__c polplan, string refDatePopulated)
    {
        try
        {
            GBEServiceRequest_H_HUM request = new GBEServiceRequest_H_HUM();
            string refDateParam = refDatePopulated <> null ? refDatePopulated.split(HYPHEN_HUM)[1] + BACKSLASH_HUM + refDatePopulated.split(HYPHEN_HUM)[2] + BACKSLASH_HUM + refDatePopulated.split(HYPHEN_HUM)[0] :'';
            
            if (Polobj.Group_Name__c != null && String.isNotBlank(Polobj.Group_Name__r.source_platform_code__c))
            {
                request = prepareGBERequest(Polobj,polplan,refDateParam);
            }
            return request;
        }
        catch (Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'GroupBenefits_D_HUM', 'RequestStructureGBEService');
            return NULL;
        }
    }
    
    /**
    * Returns request structure with request body
    * <p>
    * Method to prepare GBE request
    *
    * @param  Policy_Member__c record .
    * @param  String As of Date .
    * @return GBEServiceRequest_H_HUM
    */
    @TestVisible private static GBEServiceRequest_H_HUM prepareGBERequest(policy__c PolObj, Policy_Plan_Details__c popPlan,  String AsOfDateValue)
    {
        GBEServiceRequest_H_HUM oRequest = new GBEServiceRequest_H_HUM();
        if (String.isNotBlank(PolObj.source_cust_cov_key__c))
        {
            GBEServiceRequest_H_HUM.GroupDetailDTO grpDetailDTO = new GBEServiceRequest_H_HUM.GroupDetailDTO();
            grpDetailDTO.GroupId = PolObj.source_cust_cov_key__c.substring(0, 6);
            if (PolObj.Group_Name__r.source_platform_code__c == VOB_PLATFORM_LV_HUM)
            {
                grpDetailDTO.SubGroupId = PolObj.source_cust_cov_key__c.substring(9, 12);
                grpDetailDTO.BenefitPackageId ='';
                grpDetailDTO.BenefitSequenceNumber = PolObj.source_cust_cov_key__c.substring(6, 9);
            }
            else if (PolObj.Group_Name__r.source_platform_code__c == VOB_PLATFORM_EM_HUM)
            {
                String[] str = PolObj.source_cust_cov_key__c.split(' ');
                grpDetailDTO.SubGroupId = str[0];
                grpDetailDTO.BenefitPackageId = popPlan != NULL ? popPlan.benefit_coverage__C : PolObj.benefit_coverage__C; 
                grpDetailDTO.BenefitSequenceNumber = '';
            }
            if (PolObj.Group_Name__c != null) grpDetailDTO.Platform = PolObj.Group_Name__r.source_platform_code__c;
            if (PolObj != null)    grpDetailDTO.StartDate = popPlan != NULL ? popPlan.Coverage_Plan_Effective_Date__c : PolObj.Coverage_Plan_Effective_Date__c; 
            if (PolObj != null)    grpDetailDTO.EndDate = popPlan != NULL ? popPlan.Coverage_Plan_End_Date__c : PolObj.Coverage_Plan_End_Date__c;
            
            grpDetailDTO.InclusionCriteria = VOB_BENEFITS;
            grpDetailDTO.AsOfDate = AsOfDateValue;
            oRequest.GroupDetailDTO = grpDetailDTO;
        }
        return oRequest;
    }
    
    /*
    * Returns ProductBusinessEntityRequestV2_H_HUM request
    * <p>
    * Method to prepare PBE request
    *
    * @param  cInstance BenefitsV2_C_HUM Class Instance .
    * @return ProductBusinessEntityRequestV2_H_HUM Request structure for PBE
    */
    public static ProductBusinessEntityRequestV2_H_HUM RequestStructurePBEService(policy__c PolObj, Policy_Plan_Details__c popPlan,  String AsOfDateValue)
    {
        ProductBusinessEntityRequestV2_H_HUM oRequest = new ProductBusinessEntityRequestV2_H_HUM();
        ProductBusinessEntityRequestV2_H_HUM.GetProduct productNode = new ProductBusinessEntityRequestV2_H_HUM.GetProduct();
        
		boolean sServiceTypeCommentSwitch = false;            
        sServiceTypeCommentSwitch = HUMUtilityHelper.isCRMFunctionalityON('732113');
		
        //newly added for  PBE18.08
        if(sServiceTypeCommentSwitch == true)
            productNode.SubVersion = '3';
        else
            productNode.SubVersion = '2';
		   
        productNode.AsOfDate = AsOfDateValue;
        ProductBusinessEntityRequestV2_H_HUM.ProductIdentifier PrId = new ProductBusinessEntityRequestV2_H_HUM.ProductIdentifier();
        PrId.ProductId = (popPlan != null && popPlan.Product__r.Sold_Product_Key_Value__c != NULL ?  popPlan.Product__r.Sold_Product_Key_Value__c : PolObj.Product__r.Sold_Product_Key_Value__c);
        if (PolObj.Group_Name__r.Source_Platform_Code__c == VOB_PLATFORM_EM_HUM)
        {
           PrId.ProductIdType = VOB_BENEFITPACKAGEID_HUM;
        }
        else if (PolObj.Group_Name__r.Source_Platform_Code__c == VOB_PLATFORM_LV_HUM)
        {
          PrId.ProductIdType = VOB_VALUEPDQ_HUM;
        }
        productNode.ProductIdentifier = PrId;
        ProductBusinessEntityRequestV2_H_HUM.IncludeAdditionalDetail addDetails = new ProductBusinessEntityRequestV2_H_HUM.IncludeAdditionalDetail();
        addDetails.MasterServicesWithPOS = 'true';
        addDetails.MasterServicesWithParCode = 'true';
        addDetails.MasterServicesWithAdaCode = 'true';
        productNode.IncludeAdditionalDetail = addDetails;
        ProductBusinessEntityRequestV2_H_HUM.IncludeList IncNode = new ProductBusinessEntityRequestV2_H_HUM.IncludeList();
        list<String> lstIncludeTypes = new list<String>{ VOB_GENERALBENEFITS_HUM, VOB_MASTERSERVICES_HUM, VOB_RIDERS_HUM, VOB_RXDETAIL_HUM };
        IncNode.IncludeType = lstIncludeTypes;
        productNode.IncludeList = IncNode;
        ProductBusinessEntityRequestV2_H_HUM.ICDCodeList icdCode = new ProductBusinessEntityRequestV2_H_HUM.ICDCodeList();
        icdCode.ICDCode = new List<string>();
        icdCode.ICDCode.add('?');
        productNode.ICDCodeList = icdCode;
        oRequest.GetProduct = productNode;
        return oRequest; 
    }
    
     /*
    * Returns PBEResponseV2_DTO_HUM object
    * <p>
    * Method to prepare PBE DTO to send to UI
    *
    * @param  ProductBusinessEntityResponseV2_H_HUM object.
    * @return PBEResponseV2_DTO_HUM
    */
    public static PBEResponseV2_DTO_HUM getPBEDTOData(ProductBusinessEntityResponseV2_H_HUM oResponsePBE)
    {
        PBEResponseV2_DTO_HUM PBEDtoObj = NULL;
        try
        {
            if (oResponsePBE != null)
            {
                ProductBusinessEntityResponseV2_H_HUM.GetProductResponse objGetProductResponse = oResponsePBE.GetProductResponse;
                if (objGetProductResponse != null)
                {
                    ProductBusinessEntityResponseV2_H_HUM.ProductList objProductList = objGetProductResponse.ProductList;
                    if (objProductList != null && objProductList.Product != null && objProductList.Product.size() > 0)
                    {
                        ProductBusinessEntityResponseV2_H_HUM.Product objProduct = objProductList.Product[0];
                        if (objProduct != null)
                        {
                            ProductBusinessEntityResponseV2_H_HUM.Detail objDetail = objProduct.Detail;
                            if (objProduct != null)
                            {   
                                 PBEDtoObj = parsePBEData(objDetail,objProduct.MetaData.PlatformCode);
                                 ProductBusinessEntityResponseV2_H_HUM.Definition objDefinition = objProduct.Definition;
                                  if(objDefinition != null && string.isNotBlank(objDefinition.Description)) PBEDtoObj.planOption = objDefinition.Description;
                            }
                        }
                    
					 // We are fetching PlanId on the basis of ProductIDType as "PdbPlanId"	
						if(objProduct.Definition != null && objProduct.Definition.IdentifierList != null && objProduct.Definition.IdentifierList.ProductIdentifier != null)  
                        {					 
							List<ProductBusinessEntityResponseV2_H_HUM.ProductIdentifier> lstProductIdentifier = objProduct.Definition.IdentifierList.ProductIdentifier;           
							for(ProductBusinessEntityResponseV2_H_HUM.ProductIdentifier objProductIdentifier : lstProductIdentifier )
							{
							   if(string.isNotBlank(objProductIdentifier.ProductIdType) && objProductIdentifier.ProductIdType.equalsIgnoreCase('PdbPlanId')) 
							   PBEDtoObj.pbePlanId = string.isNotBlank(objProductIdentifier.ProductId) ? objProductIdentifier.ProductId : '';
							}
						}
					}
                }
            }
		
            return PBEDtoObj;
        }
        catch (Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'GroupBenefits_D_HUM', 'getPBEDTOData');
            return NULL;
        }
    }
    
    /*
    * Returns PBEResponseV2_DTO_HUM object
    * <p>
    * Method to parse and prepare PBE DTO to send to UI
    *
    * @param  ProductBusinessEntityResponseV2_H_HUM.Detail object.
    * @return PBEResponseV2_DTO_HUM
    */
    private static PBEResponseV2_DTO_HUM parsePBEData(ProductBusinessEntityResponseV2_H_HUM.Detail objDetail,String platformCode)
    {
        Boolean bSwitch = HUMUtilityHelper.isCRMFunctionalityON('1004703');
		PBEResponseV2_DTO_HUM PBEDtoObj = new PBEResponseV2_DTO_HUM();
        if (PlatformCode != null)  PBEDtoObj.PlatformCode = platformCode;
        if (objDetail.PackageInfo != null) PBEDtoObj.PackageInfo = objDetail.PackageInfo;
        if (objDetail.ServiceTypeList != null) PBEDtoObj.ServiceTypeList = objDetail.ServiceTypeList;
        if (objDetail.ServiceCategoryList != null) PBEDtoObj.ServiceCategoryList = objDetail.ServiceCategoryList;
        if (objDetail.PlaceOfServiceList != null) PBEDtoObj.PlaceOfServiceList = objDetail.PlaceOfServiceList;
        if (objDetail.ParcodeList != null) PBEDtoObj.ParcodeList = objDetail.ParcodeList;
		if (objDetail.CauseCodeList != null) PBEDtoObj.CauseCodeList = objDetail.CauseCodeList;
        if(bSwitch)
        {
        if (objDetail.RxDetail != null) PBEDtoObj.RxDetail = objDetail.RxDetail;
        }
		
        ProductBusinessEntityResponseV2_H_HUM.GeneralBenefits objGeneralBenefits = objDetail.GeneralBenefits;
        if (objDetail.RiderList != null) PBEDtoObj.RiderList = objDetail.RiderList;
        if (objGeneralBenefits != null)
        {
            if (objGeneralBenefits.Deductible != null) PBEDtoObj.Deductible = objGeneralBenefits.Deductible;
            if (objGeneralBenefits.BenefitIndicatorList != null) PBEDtoObj.BenefitIndicatorList = objGeneralBenefits.BenefitIndicatorList;
            if (string.isNotBlank(objGeneralBenefits.IsHDHP)) PBEDtoObj.IsHDHP = objGeneralBenefits.IsHDHP;
			if(bSwitch)
            {
            if (objGeneralBenefits.OutOfPocket != null) PBEDtoObj.OutOfPocket = objGeneralBenefits.OutOfPocket;
            if (objGeneralBenefits.MaxOutOfPocket != null) PBEDtoObj.MaxOutOfPocket = objGeneralBenefits.MaxOutOfPocket;
			if (objGeneralBenefits.CoInsurance != null) PBEDtoObj.CoInsurance = objGeneralBenefits.CoInsurance;
            if (objGeneralBenefits.SuperMoop != null) PBEDtoObj.SuperMoop = objGeneralBenefits.SuperMoop;
            if (objGeneralBenefits.CopayOfficeVisit != null) PBEDtoObj.CopayOfficeVisit = objGeneralBenefits.CopayOfficeVisit;
            if (objGeneralBenefits.Limitation != null) PBEDtoObj.Limitation = objGeneralBenefits.Limitation;
			}
        }
        return PBEDtoObj;
    }
    
    /*
    * Returns none
    * <p>
    * Method to prepare DTO from GBE response.
    * @param  GBEServiceResponse_H_HUM object .
    * @return GBEResponse_DTO_HUM
    */
    public static GBEResponse_DTO_HUM getGBEDTOData(GBEServiceResponse_H_HUM oResponseGBE)
    {
        GBEResponse_DTO_HUM gbeDtoObj = NULL;
        try
        {
            if (oResponseGBE != null)
            {
                gbeDtoObj = new GBEResponse_DTO_HUM();
                GBEServiceResponse_H_HUM.GroupInfo objGrpInfo = oResponseGBE.GroupInfo;
                if (objGrpInfo != null)
                {
                    gbeDtoObj.sBenefitPackageId = objGrpInfo.BenefitPackageId;
                    gbeDtoObj.sGroupId = objGrpInfo.GroupId;

                    GBEServiceResponse_H_HUM.Benefit objBenefit = objGrpInfo.Benefit;
                    if (objBenefit != null)
                    {
                        gbeDtoObj = createGBEBenefitDTO(gbeDtoObj, objBenefit);
                    }
                }
            }
            return gbeDtoObj;
        }
        catch (Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'GroupBenefits_D_HUM', 'getGBEDTOData');
            return NULL;
        }
    }

    /*
    * Returns GBEServiceResponse_H_HUM.Benefit
    * <p>
    * Method to prepare DTO from GBE Benefit response.
    * @param  GBEServiceResponse_H_HUM gbeDtoObj .
    * @param  GBEServiceResponse_H_HUM.Benefit objBenefit .
    * @return GBEResponse_DTO_HUM
    */
    public static GBEResponse_DTO_HUM createGBEBenefitDTO(GBEResponse_DTO_HUM gbeDtoObj, GBEServiceResponse_H_HUM.Benefit objBenefit)
    {
        gbeDtoObj.sEnrollmentSource = objBenefit.EnrollmentSource;
        gbeDtoObj.sHourlyReq = objBenefit.HourlyRequirement;
        gbeDtoObj.sGroupEnrollmentCount = objBenefit.GroupEnrollmentCount;
        gbeDtoObj.sMaximumDependentAge = objBenefit.MaximumDependentAge;
        gbeDtoObj.sMaximumStudentAge = objBenefit.MaximumStudentAge;
        gbeDtoObj.sBenefitPeriod = objBenefit.BenefitPeriod;
        gbeDtoObj.sCertificate = objBenefit.Certificate;
        gbeDtoObj.sNetwork = objBenefit.Network;
        gbeDtoObj.sMentalHealthVendorCode = objBenefit.MentalHealthVendorCode;
        gbeDtoObj.sLastRenewalDate = objBenefit.LastRenewalDate;
        gbeDtoObj.sBenefitNetwork = objBenefit.Network;
        gbeDtoObj.sMarketName = objBenefit.MarketName;
        gbeDtoObj.sSellingLedger = objBenefit.SellingLedger;
        gbeDtoObj.sLedgerDescription = objBenefit.SellingLedgerDescription;
        gbeDtoObj.sLegalEntity = objBenefit.LegalEntity;
        if (objBenefit.OpenEnrollmentList != null && objBenefit.OpenEnrollmentList.OpenEnrollment != null && objBenefit.OpenEnrollmentList.OpenEnrollment.size() > 0)
        {
            GBEServiceResponse_H_HUM.OpenEnrollment openEnrollObj = objBenefit.OpenEnrollmentList.OpenEnrollment[0];
            gbeDtoObj.sOpenEnrolBegindate = openEnrollObj.BeginDate;
            gbeDtoObj.sOpenEnrolEnddate = openEnrollObj.EndDate;
        }
        return gbeDtoObj;
    }

	@testVisible public static MentorServiceRequest_DTO_HUM RequestStructureKDMService(MentorServiceRequest_DTO_HUM KDMRequestDTO)
    {  
    	string sContractNum;
	    string sPBPCode;
	    string sMedicaresegId; 
	    string sQueryText = '';
	    string sProduct; 
	    string sProductType;
	    string sPlatform; 
	    string sBenCoverage;
	    string sGrpNumber;	    
    	if(ApexPages.currentpage().getParameters().containsKey('Id'))
        {
            String sPolId = ApexPages.currentpage().getParameters().get('Id');
          if(NULL!= sPolId)
	        {
	            Policy__c objPol = [Select Id ,Product__r.Name ,Platform__c,Major_LOB__c,Contract_Number__c,PBP_Code__c,
	                                Benefit_coverage__c, Group_Number__c,Medicare_Segment_ID__c From Policy__c Where Id =: ApexPages.currentpage().getParameters().get('Id')];
	            if(objPol != null)
	            {

	                sPlatform = objPol.Platform__c;
	                sBenCoverage = objPol.Benefit_coverage__c;
	                sContractNum = objPol.Contract_Number__c;
	                sPBPCode = objPol.PBP_Code__c;
	                sMedicaresegId = objPol.Medicare_Segment_ID__c;  
	                sGrpNumber = objPol.Group_Number__c;
	                sProduct = objPol.Product__r.Name;
	                sProductType = objPol.Major_LOB__c;
	               
	               List<Reference_Business_Line_Product_Grouping__c> lstRefBusinessLineProdGroups = [SELECT Business_Description__c  FROM Reference_Business_Line_Product_Grouping__c 
                                                                                              Where ProductCode__c = :sProduct And  Major_LOB__c = : sProductType And Business_Description__c = :VOB_MEDICARE];
                    if(sProduct	 == MED && !lstRefBusinessLineProdGroups.isEmpty())
		         	{
			            sQueryText = sContractNum+sHiphan+sPBPCode;
		                if(String.isNotBlank(sMedicaresegId)) sQueryText += sHiphan +  sMedicaresegId;
		            }
		            else 
		            {
		                if( sPlatform == sEM_Value || sPlatform == sMTV_Value )
				        {
				            sQueryText = SUBGROUP_MENTORREDIRECT_HUM + sBenCoverage;
				        } 
				        else if( sPlatform == sLV_Value || sPlatform == sCI_Value || sPlatform == sCAS_Value )
				        {
				            if(String.isNotBlank(sGrpNumber))
				            {
				                sQueryText = SUBGROUP_MENTORREDIRECT_HUM + sGrpNumber;
				            }
				        } 

		            }
	            }   
	        }
        } 

    	MentorServiceRequest_DTO_HUM oRequest = new MentorServiceRequest_DTO_HUM();      
        MentorServiceRequest_DTO_HUM.Request oHasSearch = new MentorServiceRequest_DTO_HUM.Request();
      	oHasSearch.BenefitGridsOnly = true;
      	oHasSearch.ConsumingApp = HUM_PARTD_DRUGREQUESTOR;
      	oHasSearch.Query = sQueryText;
      	oRequest.Request = oHasSearch;
        return oRequest;
    }
    
	public static Boolean ParseStructureKMDocument(MentorServiceResponse_DTO_HUM KMServiceResponse)
	{
		Boolean sKMDocuStatus = false;
		if (KMServiceResponse != null && KMServiceResponse.HasSearchResultsResponse != null)
		{
				if (KMServiceResponse.HasSearchResultsResponse != null)
				{
					 sKMDocuStatus = Boolean.valueOf(KMServiceResponse.HasSearchResultsResponse.HasMatches);
				}
			
		}

		return sKMDocuStatus;
	}
    
	/*
	* Method Name   :  getHTMLEncodedPageParameter
	* Description   :  This method is used to get Encoded Page Parameter.
	* Return Type   :  String 
	* Parameters    :  String
	*/
    private static String getHTMLEncodedPageParameter(String param) {
        String paramValue = ApexPages.currentPage().getParameters().get(param);
        return ESAPI_C_HUM.encoder().SFDC_HTMLENCODE(paramValue);
    }
}