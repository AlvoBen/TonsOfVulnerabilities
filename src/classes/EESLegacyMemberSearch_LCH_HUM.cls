/*******************************************************************************************************************************
Apex Class Name : EESLegacyMemberSearch_LCH_HUM
Version         : 1.0
Created On      : 10/13/2020
Function        : 
Test Class      : MetavanceEnrollmentSearch_LT_HUM

Modification Log: 
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*    1.0           Ashish Kumar                                  	10/13/2020               	Original Version
*    3.0           Kajal Namdev                                     05/07/2021                 Replace HUMConstants with GLOBAL_CONSTANT_LH_HUM class
*	 4.0           Vardhman Jain                                    04/04/2023                 US:4404840 Enchanced CIM changes
*******************************************************************************************************************************************************************/
public with sharing class EESLegacyMemberSearch_LCH_HUM
{
    private String sClassName = 'EESLegacyMemberSearch_LCH_HUM';
    private String sMethodName = '';
    Boolean bEnrres = false;
    
     /*
     * Method Name   :   HUMEESLegacyMemberSearchHelper constructor -
     * Description   :    This is used to set boolean variable
     * Return Type   :    NA
     * Parameters    :    NA
     */
    public EESLegacyMemberSearch_LCH_HUM()
    { 
        bIsSearchEnrollmentError = false;    
    }
    
    public transient boolean bIsSearchEnrollmentError{get;set;}
    public String sSSN{get;set;}
    public Map<String,map<String,String>> mapMemInfo {get;set;}
    public Map<String,map<String,String>> mapMemInfoEm {get;set;}
    public boolean bshowdata {get;set;}
    public Map<string,String> mapMember {get;set;}
	public Map<String, Map<String,String>> memDetail {get;set;}
    public Map<String, String> memInfo {get;set;}
    /*  
     * Method name : submit
     * Description : This method is called from search button on Search Enrollment page
     * Return Type : map 
     * Parameter : This method accepts a string which will be the SSN number
     */   
    public map<String,map<String,String>> submit(String sSSN) 
    {
        try
        {
            sMethodName = 'submit';
            String sResMsg ='';
            List<HUMEESLegacySearchPlatforms__c> Platformvalues = HUMEESLegacySearchPlatforms__c.getAll().values();
            string sReqMsg = system.label.HUMEESLegacyMemberSearchHelper_Header;
            for(integer i=0;i<Platformvalues.size();i++)
            {
                sReqMsg += system.label.HUMEESLegacyMemberSearchHelper_OpenBraces+Platformvalues[i].Platform_Value__c+system.label.HUMEESLegacyMemberSearchHelper_OpenSSN+sSSN+system.label.HUMEESLegacyMemberSearchHelper_CloseSSN+Platformvalues[i].Platform_Value__c+system.label.HUMEESLegacyMemberSearchHelper_CloseBraces;
            }
            sReqMsg += system.label.HUMEESLegacyMemberSearchHelper_Indicators;
            for(integer i=0;i<Platformvalues.size();i++)
            {
                sReqMsg  += system.label.HUMEESLegacyMemberSearchHelper_OpenPlatform;
                sReqMsg += Platformvalues[i].Platform_Value__c;          
                sReqMsg  += system.label.HUMEESLegacyMemberSearchHelper_ClosePlatform;
            }

            sReqMsg += system.label.HUMEESLegacyMemberSearchHelper_ClosePlatformMember;
            HUMIntEESLegacySearchService.PlatformMemberWebServiceSoap PlatformWebService = (HUMIntEESLegacySearchService.PlatformMemberWebServiceSoap)
                                                    HUMSearchHelper.getServiceRequestInstance(system.label.HUMEESSearchSvc,system.label.HUMEESSearchReq);           
            if(PlatformWebService <> null){sResMsg = PlatformWebService.GetPlatformMembers(sReqMsg);}

            mapMemInfo =  new map<String,map<String,String>>();
            if(!String.isBlank(sResMsg))
            {
                bshowdata = true;
                Dom.XMLNode FamilyNode;
                Dom.XMLNode PlatformMember;
                Dom.Document xmlDoc = new Dom.Document();            
                xmlDoc.load(sResMsg);    
                Dom.XMLNode rootNode = xmlDoc.getRootElement();
                
                if(rootNode != null){FamilyNode = getFamily(rootNode, 'EM');}
                
                if(FamilyNode != null){PlatformMember=getMember(FamilyNode,sSSN);}
            
                if(PlatformMember != null)
                {
                    mapMemInfo =  new map<String,map<String,String>>();
                    mapMemInfo = xmlInputStringHierarchyParser(PlatformMember);
                }
            }
            return mapMemInfo;
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e,sClassName,sMethodName);
            return null;  
        }
    }
    
    /*  
     * Method name : submitAsync
     * Description : This method is called from search button on Search Enrollment page
     * Return Type : AsyncHUMLegacyMemberSearchService.GetPlatformMembersResponse_elementFuture
     * Parameter : This method accepts a string which will be the SSN number
     */   
    public AsyncHUMLegacyMemberSearchService.GetPlatformMembersResponse_elementFuture submitAsync(Continuation oCon, String sSSN) 
    {
        try
        {
            sMethodName = 'submit';
            String sResMsg = '';
            List<HUMEESLegacySearchPlatforms__c> Platformvalues = HUMEESLegacySearchPlatforms__c.getAll().values();
            string sReqMsg = system.label.HUMEESLegacyMemberSearchHelper_Header;
            for(integer i=0;i<Platformvalues.size();i++)
            {
                sReqMsg += system.label.HUMEESLegacyMemberSearchHelper_OpenBraces+Platformvalues[i].Platform_Value__c+system.label.HUMEESLegacyMemberSearchHelper_OpenSSN+sSSN+system.label.HUMEESLegacyMemberSearchHelper_CloseSSN+Platformvalues[i].Platform_Value__c+system.label.HUMEESLegacyMemberSearchHelper_CloseBraces;
            }
            sReqMsg += system.label.HUMEESLegacyMemberSearchHelper_Indicators;
            for(integer i=0;i<Platformvalues.size();i++)
            {
                sReqMsg  += system.label.HUMEESLegacyMemberSearchHelper_OpenPlatform;
                sReqMsg += Platformvalues[i].Platform_Value__c;          
                sReqMsg  += system.label.HUMEESLegacyMemberSearchHelper_ClosePlatform;
            }

            sReqMsg += system.label.HUMEESLegacyMemberSearchHelper_ClosePlatformMember;
            
            HUM_Webservice_Callout__c objWebserviceCallout = HUM_Webservice_Callout__c.getInstance('EESLegacySearchService');
            AsyncHUMLegacyMemberSearchService.AsyncPlatformMemberWebServiceSoap oPlatformWebService =  new AsyncHUMLegacyMemberSearchService.AsyncPlatformMemberWebServiceSoap(objWebserviceCallout.End_Point_URL__c,objWebserviceCallout.Certificate_Name__c,Integer.valueOf(objWebserviceCallout.Timeout__c));
            
            if(oPlatformWebService <> null)	return oPlatformWebService.beginGetPlatformMembers(oCon, sReqMsg);
            return NULL;
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e,sClassName,sMethodName);
            return null;  
        }
    }
    
    /*  
     * Method name : parseEEESResponse
     * Description : This method is called from search button on Search Enrollment page
     * Return Type : list<MemberSearchEnrollmentHelper_LC_HUM>
     * Parameter : This method accepts a string which will be response from EES system
     */  
    public list<MemberSearchEnrollmentHelper_LC_HUM> parseEEESResponse(String sResMsg,String sSSN)
    {
        mapMemInfo = new Map<String, Map<String,String>>();
        mapMemInfoEm = new Map<String, Map<String,String>>();
        list<MemberSearchEnrollmentHelper_LC_HUM> lstLegacyService = new list<MemberSearchEnrollmentHelper_LC_HUM>();
        if(!String.isBlank(sResMsg))
        {
            bshowdata = true;
            Dom.XMLNode FamilyNode;
            Dom.XMLNode FamilyNodeEm;
            Dom.XMLNode PlatformMember;
            Dom.XMLNode PlatformMemberEm;
            Dom.Document xmlDoc = new Dom.Document();            
            xmlDoc.load(sResMsg);    
            Dom.XMLNode rootNode = xmlDoc.getRootElement();
            Integer familynodeCount = 0;
            Dom.XMLNode platformemberNode;
            if(!rootNode.getChildElements().isEmpty())
            platformemberNode = rootNode.getChildElements()[0];
            if(platformemberNode != Null) 
            familynodeCount = platformemberNode.getChildElements().size();
            List<HUMEESLegacySearchPlatforms__c> Platformvalues = HUMEESLegacySearchPlatforms__c.getAll().values();
            if(rootNode != null)
            {
                FamilyNode = getFamily(rootNode, Platformvalues[0].Platform_Value__c);
                if(familynodeCount == 2)FamilyNodeEm = getFamily(rootNode, Platformvalues[1].Platform_Value__c);
                
            }
            
            if(FamilyNode != null){PlatformMember=getMember(FamilyNode,sSSN);}
            if(FamilyNodeEm != null){PlatformMemberEm=getMember(FamilyNodeEm,sSSN);}
            if(PlatformMember != null)
            {
                mapMemInfo =  new map<String,map<String,String>>();
                mapMemInfo = xmlInputStringHierarchyParser(PlatformMember);
            }
            if(PlatformMemberEm != null)
            {
                mapMemInfoEm =  new map<String,map<String,String>>();
                mapMemInfoEm = xmlInputStringHierarchyParser(PlatformMemberEm);
            }
              
        }
        
        if(NULL != mapMemInfo && !mapMemInfo.isEmpty())
        {
            MemberSearchEnrollmentHelper_LC_HUM humLegMemberObj = new MemberSearchEnrollmentHelper_LC_HUM(mapMemInfo,null);
            lstLegacyService.add(humLegMemberObj );
            
            for(Integer count = 0 ; count < Integer.valueOf(GLOBAL_CONSTANT_LH_HUM.ListSizePageMemSearch) ; count++)
            {
                if(mapMemInfo.containsKey(GLOBAL_CONSTANT_LH_HUM.HUMEESLegacyMemberSearchHelper_Coverages_PlatformMemberCoverage +count))
                    lstLegacyService.add(new MemberSearchEnrollmentHelper_LC_HUM(mapMemInfo, count));   
            }
            bEnrres = true;                                     
        } 
        if(NULL != mapMemInfoEm && !mapMemInfoEm.isEmpty())
        {
            MemberSearchEnrollmentHelper_LC_HUM humLegMemberObjMed = new MemberSearchEnrollmentHelper_LC_HUM(mapMemInfoEm,null);
            lstLegacyService.add(humLegMemberObjMed );
            
            for(Integer count = 0 ; count < Integer.valueOf(GLOBAL_CONSTANT_LH_HUM.ListSizePageMemSearch) ; count++)
            {
                if(mapMemInfoEm.containsKey(GLOBAL_CONSTANT_LH_HUM.HUMEESLegacyMemberSearchHelper_Coverages_PlatformMemberCoverage +count))
                    lstLegacyService.add(new MemberSearchEnrollmentHelper_LC_HUM(mapMemInfoEm, count));   
            }
            bEnrres = true;                                     
        } 
        return lstLegacyService;
    }
    
    /*  
     * Method name : getFamily
     * Description : This method is called from submit.
                    It will return the Family node which contains all members of the family
     * Return Type : XML node
     * Parameter : This method accepts XML node
     */
    public static Dom.XMLNode getFamily(Dom.XMLNode mpkeyValue, String platformName)
    {
        Dom.XMLNode nodeFamily;
        String nodeName;
        if(platformName == system.label.HUMCOBPlatformCodeLV) nodeName = system.label.HUMEESLegacyMemberSearchHelper_PlatformMember;
        if(platformName == system.label.HUMCOBPlatformCodeEM) nodeName = system.label.HUMEESLegacyMemberSearchHelper_Family;
        for(Dom.XMLNode echFullNode : mpkeyValue.getChildElements())
        {
            if(echFullNode.getChildElements()==null||echFullNode.getChildElements().size()==0)
            {
                if(echFullNode.getName() == nodeName )
                {
                    nodeFamily = echFullNode;
                }
            }
            else
            {
                if(echFullNode.getName() == nodeName)
                {
                    nodeFamily = echFullNode;
                    break;
                }
                else
                {
                    nodeFamily = getFamily(echFullNode,platformName);
                }
            }
        }
        return nodeFamily;
    }
       
    /*  
     * Method name : getMember
     * Description : This method is called from submit. It will return 
                    the member node whose SSN matches with SSN passed in input.
     * Return Type : XML Node
     * Parameter : This method accepts XML node and a string which will be the 
                SSN number
     */
    public static Dom.XMLNode getMember( Dom.XMLNode mpkeyValue,String sSSN)
    {
        Dom.XMLNode nodeMember;
        for(Dom.XMLNode echFullNode : mpkeyValue.getChildElements())
        {
            if(echFullNode.getChildElements()==null||echFullNode.getChildElements().size()==0)
            {
                if(echFullNode.getName() == system.label.HUMEESLegacyMemberSearchHelper_SSN)
                {
                    if(echFullNode.getText() == sSSN)
                    {
                        nodeMember = echFullNode.getParent();
                        break;
                    }
                }
            }
            else
            {
                if(nodeMember == null){nodeMember = getMember(echFullNode,sSSN);}
            }

        }
        return nodeMember;
    }
    
    /*  
    * Method name : xmlInputStringHierarchyParser
    * Description : This method will parse an XML and return the hierarchy
                    in a map. The Structure of the map is:
                    Grandparent:Parent ; map(tagname,tagvalue)
    * Return Type : map 
    * Parameter : XML node
    */
    public static map<String,map<String,String>> xmlInputStringHierarchyParser(Dom.XMLNode rootNode) 
    {
        map<String,String> mapTagsToValues = new map<String,String> {};
        map<String,map<String,String>> mapConsolidated = new map<String,map<String,String>>();
        Integer i=0;
        if(rootNode != null) 
        {
            mapConsolidated = collectHierarchy(rootNode,mapConsolidated);
        }
        return mapConsolidated;
    }
    
    /*  
    * Method name : collectHierarchy
    * Description : called from xmlInputStringHierarchyParser
    * Return Type : map 
    * Parameter : This method accepts 2 parameter: XML node & Map
    */
    public static Map<String,map<String,String>> collectHierarchy( Dom.XMLNode mpkeyValue, Map<String,map<String,String>> mapResultant )
    {
        String childKeyVal;
             
        map<String,String> mapKeyValue;
        for(Dom.XMLNode echFullNode : mpkeyValue.getChildElements())
        {
            mapKeyValue = new map<String,String>();                                    
            mapKeyValue.put(echFullNode.getName(),echFullNode.getText());
            if(echFullNode.getChildElements()==null||echFullNode.getChildElements().size()==0)
            {
                childKeyVal = echFullNode.getParent().getParent().getName()+':'+echFullNode.getParent().getName();
                if(mapResultant.containsKey(childKeyVal))
                {
                    if((mapResultant.get(childKeyVal)).containsKey(echFullNode.getName()))
                    {
                        for(integer iCount = 1; iCount < 10; iCount++)
                        {
                            if((mapResultant.containsKey(childKeyVal+(iCount))))
                            {
                                if(!(mapResultant.get(childKeyVal+(iCount)).containsKey(echFullNode.getName())))
                                {                                            
                                    mapResultant.get(childKeyVal+(iCount)).put(echFullNode.getName(),echFullNode.getText());
                                    break;
                                }                           
                             }
                             else
                             {
                                mapResultant.put(childKeyVal+(iCount),mapKeyValue);
                                break;
                             }
                                    
                         }
                    }          
                    else
                    {
                        mapResultant.get(childKeyVal).put(echFullNode.getName(),echFullNode.getText());
                    }
                }
                else
                {
                    mapResultant.put(childKeyVal,mapKeyValue);
                }
            }
            else
            {
                mapResultant = collectHierarchy(echFullNode,mapResultant);
            }
        }
        return mapResultant;
    }
	
	  /*  
     * Method name : parseEEESXMLResponse
     * Description : This method is called from submit.
                    It will return the MemberSearchEnrollmentHelper_LC_HUM object containing response
     * Return Type : List<MemberSearchEnrollmentHelper_LC_HUM>
     * Parameter : This method accepts XML response and SSN
     */
        public list<MemberSearchEnrollmentHelper_LC_HUM> parseEEESResponseNew(String sResMsg,String sSSN)
    {
        list<MemberSearchEnrollmentHelper_LC_HUM> lstLegacyService = new list<MemberSearchEnrollmentHelper_LC_HUM>();
        List<HUMMemberSearchEnrollment_DTO.GetResponse> processedResponse;//vj
        memDetail = new Map<String, Map<String,String>>();
        MemberSearchEnrollmentHelper_LC_HUM humLegMemberObjMed;
        String key;
        if(!String.isBlank(sResMsg))
        {
            Dom.Document xmlDoc = new Dom.Document();            
            xmlDoc.load(sResMsg);    
            Dom.XMLNode rootNode = xmlDoc.getRootElement();
            processedResponse=parseXML(rootNode);
        }
        if(processedResponse != null)
        {
            lstLegacyService.addAll(getFirstFamilyMember(processedResponse));
        }
        return lstLegacyService;
    }
     /*  
     * Method name : parseEEESXMLResponse
     * Description : This method is called from submit.
                    It will return the MemberSearchEnrollmentHelper_LC_HUM object containing response
     * Return Type : MemberSearchEnrollmentHelper_LC_HUM
     * Parameter : This method accepts XML response and SSN
     */
       public list<MemberSearchEnrollmentHelper_LC_HUM> parseEEESXMLResponse(String sResMsg,String sSSN)
    {
        list<MemberSearchEnrollmentHelper_LC_HUM> lstLegacyService = new list<MemberSearchEnrollmentHelper_LC_HUM>();
        List<HUMMemberSearchEnrollment_DTO.GetResponse> processedResponses;
        memDetail = new Map<String, Map<String,String>>();
        MemberSearchEnrollmentHelper_LC_HUM humLegMemberObjMed;
        Map<String, String> memInfoFamilyDetails;
        Map<String, String> memInfoFamilyAddress;
        Map<String, Map<String, String>> memMemberContract;
        Map<String, Map<String, String>> memEmails;
        Map<String, Map<String, String>> memPhone;
        List<Map<String, Map<String, String>>> memMebContracts = new List<Map<String, Map<String, String>>>();
        if(!String.isBlank(sResMsg))
        {
            Dom.Document xmlDoc = new Dom.Document();            
            xmlDoc.load(sResMsg);    
            Dom.XMLNode rootNode = xmlDoc.getRootElement();
            processedResponses=parseXML(rootNode);
        }
        if(processedResponses != null)
        {
            lstLegacyService.addAll(getFirstFamilyMember(processedResponses));
            for(HUMMemberSearchEnrollment_DTO.GetResponse processedResponse : processedResponses)
            {
                for(Integer count = 0 ; count < processedResponse.FamilyList.Family.size() ; count++)
                {
                    if(processedResponse.Member.SSN != processedResponse.FamilyList.Family.get(count).Member.SSN)
                    {
                        //Family member details
                        HUMMemberSearchEnrollment_DTO.Family familyMem = processedResponse.FamilyList.Family.get(count);
                        memInfoFamilyDetails = getFamilyDetails(familyMem.Member);
                    
                        //family member address details
                        memInfoFamilyAddress =  getFamilyAddress(familyMem.Member.Address);
                        
                        
                        //Emailaddres Details
                        memEmails = getEmails(familyMem.EmailAddressList.EmailAddress);

                        //PhoneNumber Details
                        memPhone = getPhone(familyMem.PhoneNumberList.PhoneNumber);
                        memMebContracts.clear();
                        for(Integer cnt=0; cnt < familyMem.MemberContractList.MemberContracts.size() ; cnt++)
                        {
                            memMebContracts.addAll(getMemberContract(familyMem.MemberContractList.MemberContracts.get(cnt), cnt));
                            
                        }
                        memMebContracts = sortMemberContracts(memMebContracts);
                        Integer memberIndex;
                        Boolean breakFlag=false;
                        for(Integer cnt = 0; cnt < memMebContracts.size() ; cnt++)
                        {
                            for(String key : memMebContracts.get(cnt).keySet())
                            {
                                memberIndex = Integer.valueOf(key.right(5));
                                memMemberContract = memMebContracts.get(cnt);
                                break;      
                            }
                            memDetail.put('Family:PlatformMember', memInfoFamilyDetails);
                            memDetail.put('PlatformMember:Address',memInfoFamilyAddress);
                            memDetail.putAll(memMemberContract);
                            memDetail.putAll(memEmails);
                            memDetail.putAll(memPhone);
                            humLegMemberObjMed = new MemberSearchEnrollmentHelper_LC_HUM(memDetail,memberIndex);
                            lstLegacyService.add(humLegMemberObjMed); 
                        }
                    }
                }
            }
        }
        return lstLegacyService;
    }
   public static List<MemberSearchEnrollmentHelper_LC_HUM> getFirstFamilyMember(List<HUMMemberSearchEnrollment_DTO.GetResponse> processedResponses)
    {
        List<MemberSearchEnrollmentHelper_LC_HUM> humLegMemberObj = new List<MemberSearchEnrollmentHelper_LC_HUM>();
        MemberSearchEnrollmentHelper_LC_HUM humMemberObj;
        Map<String, Map<String,String>> memDetail1 = new Map<String, Map<String,String>>();
        Map<String, String> memInfoFamilyDetails;
        Map<String, String> memInfoFamilyAddress;
        Map<String, Map<String, String>> memMemberContract;
        List<Map<String, Map<String, String>>> memMebContracts = new List<Map<String, Map<String, String>>>();
        Map<String, Map<String, String>> memEmails;
        Map<String, Map<String, String>> memPhone;
        Set<String> keySet = new Set<String>();
        Integer counter=0;
        for(HUMMemberSearchEnrollment_DTO.GetResponse processedResponse : processedResponses)
        {
            memInfoFamilyDetails = getFamilyDetails(processedResponse.Member);
                
            //family member address details
            memInfoFamilyAddress =  getFamilyAddress(processedResponse.Member.Address);
        

            for(Integer count = 0 ; count < processedResponse.FamilyList.Family.size() ; count++)
            {
                if(processedResponse.Member.SSN == processedResponse.FamilyList.Family.get(count).Member.SSN)
                {
                    memEmails = getEmails(processedResponse.EmailAddressesList.EmailAddress);
                    memPhone = getPhone(processedResponse.PhoneNumberList.PhoneNumber);
                    memMebContracts.clear();
                    for(Integer cnt=0; cnt < processedResponse.FamilyList.Family.get(count).MemberContractList.MemberContracts.size() ; cnt++)
                    {
                        memMebContracts.addAll(getMemberContract(processedResponse.FamilyList.Family.get(count).MemberContractList.MemberContracts.get(cnt), counter++));
                    }
                    memMebContracts = sortMemberContracts(memMebContracts);
                    Integer memberIndex;
                    Boolean breakFlag=false;
                    for(Integer cnt = 0; cnt < memMebContracts.size() ; cnt++)
                    {
                        for(String key : memMebContracts.get(cnt).keySet())
                        {
                            memberIndex = Integer.valueOf(key.right(5));
                            memMemberContract = memMebContracts.get(cnt);
                            break;   
                        }
                        memDetail1.put('Family:PlatformMember', memInfoFamilyDetails);
                        memDetail1.put('PlatformMember:Address',memInfoFamilyAddress);
                        memDetail1.putAll(memMemberContract);
                        memDetail1.putAll(memEmails);
                        memDetail1.putAll(memPhone);
                        humMemberObj = new MemberSearchEnrollmentHelper_LC_HUM(memDetail1,memberIndex); 
                        humLegMemberObj.add(humMemberObj);
                    }
                }
            }
        }
        
        return humLegMemberObj;
    }
 public static List<Map<String, Map<String,String>>> sortMemberContracts(List<Map<String, Map<String,String>>> getMemberContractDetails)
    {
        List<Map<String, Map<String,String>>> sortedMemberContractes = new List<Map<String, Map<String,String>>>();
        for(Integer cnt=0; cnt < getMemberContractDetails.size() ; cnt++)
        {
            for(String key : getMemberContractDetails.get(cnt).keySet())
            {
                if(key == 'Coverages:PlatformMemberCoverage' + key.right(5))
                {
                    if(getMemberContractDetails.get(cnt).get('Coverages:PlatformMemberCoverage' + key.right(5)).get('MemberCoverageEndDate') == 'NONE')
                    {
                        sortedMemberContractes.add(getMemberContractDetails.get(cnt));
                    } 
                }   
            
            }
        }   
        for(Integer cnt=0; cnt < getMemberContractDetails.size() ; cnt++)
        {
            for(String key : getMemberContractDetails.get(cnt).keySet())
            {
                if(key == 'Coverages:PlatformMemberCoverage' + key.right(5))
                {
                    if(getMemberContractDetails.get(cnt).get('Coverages:PlatformMemberCoverage' + key.right(5)).get('MemberCoverageEndDate') != 'NONE')
                    {
                        sortedMemberContractes.add(getMemberContractDetails.get(cnt));
                    } 
                }   
            
            }
            
        }
        return sortedMemberContractes;
    }
    public static List<HUMMemberSearchEnrollment_DTO.GetResponse> parseXML(Dom.XmlNode rootNode)
    {
        List<HUMMemberSearchEnrollment_DTO.GetResponse> responselist = new List<HUMMemberSearchEnrollment_DTO.GetResponse>();
        HUMMemberSearchEnrollment_DTO.GetResponse response;
        HUMMemberSearchEnrollment_DTO.Member mainMember;
        HUMMemberSearchEnrollment_DTO.FamilyList familyList;
        HUMMemberSearchEnrollment_DTO.EmailAddressesList emailAddressList;
        HUMMemberSearchEnrollment_DTO.PhoneNumberList  phoneNumberList;
        for(Dom.XMLNode PlatformMembers : rootNode.getChildElements())
        {
            for(Dom.XMLNode PlatformMember : PlatformMembers.getChildElements())
            {
                mainMember = new HUMMemberSearchEnrollment_DTO.Member();
                familyList = new HUMMemberSearchEnrollment_DTO.FamilyList();
                emailAddressList = new HUMMemberSearchEnrollment_DTO.EmailAddressesList();
                
                phoneNumberList = new HUMMemberSearchEnrollment_DTO.PhoneNumberList();
                response = new HUMMemberSearchEnrollment_DTO.GetResponse();
                Integer count=0;
                for(Dom.XmlNode child : PlatformMember.getChildElements() )
                {
                    if(child.getName()=='FirstName')
                        mainMember.FirstName  = child.getText();
                    if(child.getName()=='MiddleInitial')
                        mainMember.MiddleInitial = child.getText();
                    if(child.getName()=='LastName')
                        mainMember.LastName = child.getText();
                    if(child.getName()=='DateOfBirth')
                        mainMember.DateOfBirth = child.getText();
                    if(child.getName()=='Gender')
                        mainMember.Gender = child.getText();
                    if(child.getName()=='SSN')
                        mainMember.SSN = child.getText();
                    if(child.getName()=='Age')
                        mainMember.Age = child.getText();
                    if(child.getName()=='Address')
                        mainMember.Address = parseAddress(child);
                    if(child.getName()=='PID')
                        mainMember.PID = child.getText();
                    if(child.getName()=='SrcPlatformCode')
                        mainMember.SrcPlatformCode = child.getText();
                    if(child.getName()=='Prefix')
                        mainMember.Prefix = child.getText();
                    if(child.getName()=='Suffix')
                        mainMember.Suffix = child.getText();
                    if(child.getName()=='MiddleName')
                        mainMember.MiddleName = child.getText();
                    if(child.getName()=='MaritalStatus')
                        mainMember.MaritalStatus = child.getText();
                    if(child.getName()=='Smoker')
                        mainMember.Smoker = child.getText();
                    if(child.getName()=='Language')
                        mainMember.Language = child.getText();
                    if(child.getName()=='DeceasedDate')
                        mainMember.DeceasedDate = child.getText();
                    if(child.getName()=='Family')
                        familyList=parseFamily(child);
                    if(child.getName()=='EMailAddresses')
                        emailAddressList = parseEMailAddresses(child);
                    if(child.getName()=='PhoneNumbers')
                        phoneNumberList = parsePhoneNumbers(child);
                }
                if(mainMember.Address != null && phoneNumberList != null && emailAddressList != null && familyList != null)
                {
                    response.Member = mainMember;
                    response.PhoneNumberList = phoneNumberList;
                    response.EmailAddressesList = emailAddressList;
                    response.FamilyList = familyList;
                    responselist.add(response);
                }
            }
        }
        if(responselist.size() > 0)
        {
            return responselist;
        }
        else {
            return null;
        }

    }
  /*  
     * Method name : getMemberContract
     * Description : This method is called parseEEESXMLResponse
                    It will return the Map<String, Map<String,String>> object containing response
     * Return Type : Map<String, Map<String,String>>
     * Parameter : This method accepts List<HUMMemberSearchEnrollment_DTO.MemberContracts>
     */
    public static List<Map<String, Map<String, String>>> getMemberContract(HUMMemberSearchEnrollment_DTO.MemberContracts MemberContracts, Integer cnt)
    {
        List<Map<String, Map<String, String>>> platFormMemberList = new List<Map<String, Map<String, String>>>();
        Map<String, String> memInfoMember = new Map<String, String>();
        Map<String, String> memInfoGroup = new Map<String, String>();
        Map<String, String> memInfoPMC;
        Map<String, Map<String, String>> memDetail1;
        String keyMember,keyGroup,keyPMC;
        keyMember = 'MemberContracts:MemberContract';
        HUMMemberSearchEnrollment_DTO.MemberContracts memberCont  = MemberContracts;
        memInfoMember =  getMemberContractDetails(memberCont); 
        

        //MemberContract Group Details
        memInfoGroup =  getMemberContractGroup(memberCont.Groups);
        keyGroup='MemberContract:Group';
        
                
        //MemberContract PlatformMemberCoverage
        Integer coverageCounter =0;
        Integer PMCCounter = 1;
        String strcnt='';
        String strcoverageCounter='';
        for(HUMMemberSearchEnrollment_DTO.PlatformMemberCoverage PlatformMemberCover : memberCont.Coverages.PlatformMemberCoverage)
        {   
            memDetail1 = new Map<String, Map<String, String>>();
            memInfoPMC = new Map<String, String>();
            if(coverageCounter > 9)
                strcoverageCounter = '' + coverageCounter;
            else 
                strcoverageCounter = '0' + coverageCounter;  
                
                if(cnt > 9)
                strcnt = '' + cnt;
            else 
                strcnt = '0' + cnt; 
                
            memDetail1.put(keyMember+PMCCounter+strcnt+strcoverageCounter,memInfoMember);
            memDetail1.put(keyGroup+PMCCounter+strcnt+strcoverageCounter,memInfoGroup);
            memInfoPMC =  getPlatformMemberCoverage(PlatformMemberCover);
            keyPMC='Coverages:PlatformMemberCoverage';
            memDetail1.put(keyPMC+PMCCounter+strcnt+strcoverageCounter,memInfoPMC);
            coverageCounter++;
            PMCCounter++;
            platFormMemberList.add(memDetail1);
        }
        return platFormMemberList;
    }
    /*  
     * Method name : getEmails
     * Description : This method is called parseEEESXMLResponse
                    It will return the Map<String, Map<String,String>> object containing response
     * Return Type : Map<String, Map<String,String>>
     * Parameter : This method accepts List<HUMMemberSearchEnrollment_DTO.EmailAddress>
     */
    public static Map<String, Map<String, String>> getEmails(List<HUMMemberSearchEnrollment_DTO.EmailAddress> Email)
    {
        Map<String, String> memInfo1 = new Map<String, String>();
        Map<String, Map<String, String>> memDetail1 = new Map<String, Map<String, String>>();
        String key;
        for(Integer cnt = 0 ; cnt < Email.size() ; cnt++)
        {
            key='EMailAddresses:EMailAddress';
            HUMMemberSearchEnrollment_DTO.EmailAddress emailAddress = Email.get(cnt);
            memInfo1 =  getEmailAddress(emailAddress);
            
            if(cnt == 0)
                {memDetail1.put(key, memInfo1);}
            else 
                {memDetail1.put(key+cnt,memInfo1);}
                
        }
        return memDetail1;
    }
    /*  
     * Method name : getPhone
     * Description : This method is called parseEEESXMLResponse
                    It will return the Map<String, Map<String,String>> object containing response
     * Return Type : Map<String, Map<String,String>>
     * Parameter : This method accepts List<HUMMemberSearchEnrollment_DTO.PhoneNumber>
     */
    public static Map<String, Map<String, String>> getPhone(List<HUMMemberSearchEnrollment_DTO.PhoneNumber> phone)
    {
        Map<String, String> memInfo1 = new Map<String, String>();
        Map<String, Map<String, String>> memDetail1 = new Map<String, Map<String, String>>();
        String key;
        for(Integer cnt = 0 ; cnt < phone.size() ; cnt++)
        {  
            key='PhoneNumbers:PhoneNumber';
            HUMMemberSearchEnrollment_DTO.PhoneNumber phoneNum = phone.get(cnt);
            memInfo1 =  getPhoneNumber(phoneNum);
            if(cnt == 0)
                {memDetail1.put(key, memInfo1);}
            else 
                {memDetail1.put(key+cnt,memInfo1);}    
        }
        return memDetail1;
    }
    /*  
     * Method name : getFamilyDetails
     * Description : This method is called parseEEESXMLResponse
                    It will return the Map<String,String>> object containing response
     * Return Type : Map<String, Map<String,String>>
     * Parameter : This method accepts HUMMemberSearchEnrollment_DTO.Member
     */
    public static map<String, String> getFamilyDetails(HUMMemberSearchEnrollment_DTO.Member familyMem)
    {
        map<String, String> memInfo = new map<String, String>();
        if(familyMem != null)
        {   
            memInfo.put('FirstName',familyMem.FirstName);
            memInfo.put('MiddleInitial',familyMem.MiddleInitial);
            memInfo.put('LastName',familyMem.LastName);
            memInfo.put('DateOfBirth',familyMem.DateOfBirth);
            memInfo.put('Gender',familyMem.Gender);
            memInfo.put('SSN',familyMem.SSN);
            memInfo.put('Age',familyMem.Age);
            memInfo.put('PID',familyMem.PID);
            memInfo.put('SrcPlatformCode',familyMem.SrcPlatformCode);
            memInfo.put('Prefix',familyMem.Prefix);
            memInfo.put('Suffix',familyMem.Suffix);
            memInfo.put('MiddleName',familyMem.MiddleName);
            memInfo.put('MaritalStatus',familyMem.MaritalStatus);
            memInfo.put('Smoker',familyMem.Smoker);
            memInfo.put('Language',familyMem.Language);
            memInfo.put('DeceasedDate',familyMem.DeceasedDate);
        }
        return memInfo;
    }
    /*  
     * Method name : getFamilyAddress
     * Description : This method is called parseEEESXMLResponse
                    It will return the Map<String,String>> object containing response
     * Return Type : Map<String, Map<String,String>>
     * Parameter : This method accepts HUMMemberSearchEnrollment_DTO.Address
     */
    public static Map<String, String> getFamilyAddress(HUMMemberSearchEnrollment_DTO.Address familyMem)
    {
        map<String, String>memInfo =  new Map<String, String>();
        if(familyMem != null)
        {
            memInfo.put('Line1', familyMem.Line1);
            memInfo.put('Line2', familyMem.Line2);
            memInfo.put('Line3', familyMem.Line3);
            memInfo.put('City', familyMem.City);
            memInfo.put('Country', familyMem.Country);
            memInfo.put('State', familyMem.State);
            memInfo.put('Zip', familyMem.Zip);
            memInfo.put('ZipPlus', familyMem.ZipPlus);
        }
        return memInfo;
    }
    /*  
     * Method name : getMemberContractDetails
     * Description : This method is called parseEEESXMLResponse
                    It will return the Map<String,String>> object containing response
     * Return Type : Map<String, Map<String,String>>
     * Parameter : This method accepts HUMMemberSearchEnrollment_DTO.MemberContracts
     */
    public static Map<String, String> getMemberContractDetails(HUMMemberSearchEnrollment_DTO.MemberContracts memberContracts)
    {
        Map<String, String> memInfo =  new Map<String, String>();
        if(memberContracts != null)
        {
            memInfo.put('ContractID', memberContracts.ContractID);
            memInfo.put('EffectiveDate', memberContracts.EffectiveDate);
            memInfo.put('EndDate', memberContracts.EndDate);
        }
        return memInfo;
    }
    /*  
     * Method name : getMemberContractGroup
     * Description : This method is called parseEEESXMLResponse
                    It will return the Map<String,String>> object containing response
     * Return Type : Map<String, Map<String,String>>
     * Parameter : This method accepts HUMMemberSearchEnrollment_DTO.Groups
     */
    public static Map<String, String> getMemberContractGroup(HUMMemberSearchEnrollment_DTO.Groups groups)
    {
        Map<String, String> memInfo = new Map<String, String>();
        if(groups != null)
        {
            memInfo.put('GroupID',groups.GroupID);
            memInfo.put('GroupName',groups.GroupName);
        }
        return memInfo;
    }
    /*  
     * Method name : getPlatformMemberCoverage
     * Description : This method is called parseEEESXMLResponse
                    It will return the Map<String,String>> object containing response
     * Return Type : Map<String, Map<String,String>>
     * Parameter : This method accepts HUMMemberSearchEnrollment_DTO.PlatformMemberCoverage
     */
    public static Map<String, String> getPlatformMemberCoverage(HUMMemberSearchEnrollment_DTO.PlatformMemberCoverage coverage)
    {
        Map<String, String> memInfo = new Map<String, String>();
        if(coverage != null)
        {
            memInfo.put('LineOfBusiness', coverage.LineOfBusiness);
            memInfo.put('Category', coverage.Category);
            memInfo.put('SrcPlatformCode', coverage.SrcPlatformCode);
            memInfo.put('CategoryDesciption', coverage.CategoryDesciption);
            memInfo.put('CoverageTier', coverage.CoverageTier);
            memInfo.put('MemberCoverageEffectiveDate', coverage.MemberCoverageEffectiveDate);
            memInfo.put('MemberCoverageEndDate', coverage.MemberCoverageEndDate);
            memInfo.put('MemberEnrollmentEffectiveDate', coverage.MemberEnrollmentEffectiveDate);
            memInfo.put('MemberEnrollmentEndDate', coverage.MemberEnrollmentEndDate);
            memInfo.put('PrePostTax', coverage.PrePostTax);
            memInfo.put('CoveragePeriod', coverage.CoveragePeriod);
            memInfo.put('RelationCode', coverage.RelationCode);
            memInfo.put('TermedReason', coverage.TermedReason);
            memInfo.put('Student', coverage.Student);
            memInfo.put('Disabled', coverage.Disabled);
            memInfo.put('MemberIDBase', coverage.MemberIDBase);
            memInfo.put('MemberIDDependentCode', coverage.MemberIDDependentCode);
            memInfo.put('HSAContributions', coverage.HSAContributions);
            memInfo.put('TobaccoUser', coverage.TobaccoUser);
            return memInfo;
        }
        return null;
        
    }
    /*  
     * Method name : getEmailAddress
     * Description : This method is called parseEEESXMLResponse
                    It will return the Map<String,String>> object containing response
     * Return Type : Map<String, Map<String,String>>
     * Parameter : This method accepts HUMMemberSearchEnrollment_DTO.EmailAddress
     */
    public static Map<String, String> getEmailAddress(HUMMemberSearchEnrollment_DTO.EmailAddress email)
    {
        Map<String, String> memInfo = new Map<String, String>();
        if(email != null)
        {
            memInfo.put('Address', email.Address);
            memInfo.put('Type', email.Type);
            memInfo.put('Primary', email.Primary);
        }
        return memInfo;
    }
    /*  
     * Method name : getPhoneNumber
     * Description : This method is called parseEEESXMLResponse
                    It will return the Map<String,String>> object containing response
     * Return Type : Map<String, Map<String,String>>
     * Parameter : This method accepts HUMMemberSearchEnrollment_DTO.PhoneNumber
     */
    public static Map<String, String> getPhoneNumber(HUMMemberSearchEnrollment_DTO.PhoneNumber phoneNum)
    {
        Map<String, String> memInfo = new Map<String, String>();
        if(phoneNum != null)
        {
            memInfo.put('Number', phoneNum.PNumber);
            memInfo.put('Extension', phoneNum.Extension);
            memInfo.put('Type', phoneNum.Type);
            memInfo.put('Primary', phoneNum.Primary);
        }
        return memInfo;
    }
    /*  
     * Method name : parseAddress
     * Description : This method is called parseEEESXMLResponse
                    It will return the HUMMemberSearchEnrollment_DTO.Address object containing response
     * Return Type : HUMMemberSearchEnrollment_DTO.Address
     * Parameter : This method accepts Dom.XmlNode
     */
    public static HUMMemberSearchEnrollment_DTO.Address parseAddress(Dom.XmlNode addressNode)
    {
        HUMMemberSearchEnrollment_DTO.Address address = new HUMMemberSearchEnrollment_DTO.Address();
        for(Dom.XmlNode add : addressNode.getChildElements())
        {
            if(add.getName()=='Line1')
                address.Line1 = add.getText(); 
            if(add.getName()=='Line2')
                address.Line2 = add.getText();
            if(add.getName()=='Line3')
                address.Line3 = add.getText();
            if(add.getName()=='City')
                address.City = add.getText();
            if(add.getName()=='County')
                address.Country = add.getText();
            if(add.getName()=='State')
                address.State = add.getText();
            if(add.getName()=='Zip')
                address.Zip = add.getText();
            if(add.getName()=='ZipPlus')
                address.ZipPlus = add.getText();
        }
        return address;
    }
    /*  
     * Method name : parseEMailAddresses
     * Description : This method is called parseEEESXMLResponse
                    It will return the HUMMemberSearchEnrollment_DTO.EmailAddressesList object containing response
     * Return Type : HUMMemberSearchEnrollment_DTO.EmailAddressesList
     * Parameter : This method accepts Dom.XmlNode
     */
    public static HUMMemberSearchEnrollment_DTO.EmailAddressesList parseEMailAddresses(Dom.XmlNode emailAddress)
    {
        HUMMemberSearchEnrollment_DTO.EmailAddressesList mailList = new HUMMemberSearchEnrollment_DTO.EmailAddressesList();
        List<HUMMemberSearchEnrollment_DTO.EmailAddress> mailAddress= new List<HUMMemberSearchEnrollment_DTO.EmailAddress>();
        HUMMemberSearchEnrollment_DTO.EmailAddress mail;
        for(Dom.XmlNode emailAdd : emailAddress.getChildElements())
        {
            mail = new HUMMemberSearchEnrollment_DTO.EmailAddress();
            for(Dom.XmlNode email : emailAdd.getChildElements())
            {
                if(email.getName()=='Address')
                    mail.Address = email.getText();
                    
                if(email.getName()=='Type')
                    mail.Type = email.getText();
                if(email.getName()=='Primary')
                    mail.Primary = email.getText();
            }
            mailAddress.add(mail);
        }
        mailList.EmailAddress = mailAddress;
        return mailList;
    }
    /*  
     * Method name : parsePhoneNumbers
     * Description : This method is called parseEEESXMLResponse
                    It will return the HUMMemberSearchEnrollment_DTO.PhoneNumberList object containing response
     * Return Type : HUMMemberSearchEnrollment_DTO.PhoneNumberList
     * Parameter : This method accepts Dom.XmlNode
     */
    public static HUMMemberSearchEnrollment_DTO.PhoneNumberList parsePhoneNumbers(Dom.XmlNode PhoneNumber)
    {
        HUMMemberSearchEnrollment_DTO.PhoneNumberList phoneList = new HUMMemberSearchEnrollment_DTO.PhoneNumberList();
        List<HUMMemberSearchEnrollment_DTO.PhoneNumber> phoneNo= new List<HUMMemberSearchEnrollment_DTO.PhoneNumber>();
        HUMMemberSearchEnrollment_DTO.PhoneNumber phoneDetails;
        for(Dom.XmlNode phone : PhoneNumber.getChildElements())
        {
            phoneDetails = new HUMMemberSearchEnrollment_DTO.PhoneNumber();
            for(Dom.XmlNode pno : phone.getChildElements())
            {
                if(pno.getName()=='Number')
                    phoneDetails.PNumber = pno.getText();
                if(pno.getName()=='Extension')
                    phoneDetails.Extension = pno.getText();    
                if(pno.getName()=='Type')
                    phoneDetails.Type = pno.getText();
                if(pno.getName()=='Primary')
                    phoneDetails.Primary = pno.getText();
            }
            phoneNo.add(phoneDetails);
        }
        phoneList.PhoneNumber = phoneNo;
        return phoneList;
    }
    /*  
     * Method name : parseFamily
     * Description : This method is called parseEEESXMLResponse
                    It will return the HUMMemberSearchEnrollment_DTO.FamilyList object containing response
     * Return Type : HUMMemberSearchEnrollment_DTO.FamilyList
     * Parameter : This method accepts Dom.XmlNode
     */
    public static HUMMemberSearchEnrollment_DTO.FamilyList parseFamily(Dom.XmlNode familyNode)
    {
        HUMMemberSearchEnrollment_DTO.FamilyList familyList = new HUMMemberSearchEnrollment_DTO.FamilyList();
        List<HUMMemberSearchEnrollment_DTO.Family> family = new List<HUMMemberSearchEnrollment_DTO.Family>(); 
        HUMMemberSearchEnrollment_DTO.Family familyMember ;
        
        HUMMemberSearchEnrollment_DTO.Member familyMem ;
        HUMMemberSearchEnrollment_DTO.MemberContractList memberContractList;
        HUMMemberSearchEnrollment_DTO.EmailAddressesList emailAddressessList;
        HUMMemberSearchEnrollment_DTO.PhoneNumberList phoneNumberList;
        
        for(Dom.XmlNode platformMembers : familyNode.getChildElements())
        { 
            familyMember = new HUMMemberSearchEnrollment_DTO.Family();
            familyMem = new HUMMemberSearchEnrollment_DTO.Member();
            memberContractList = new HUMMemberSearchEnrollment_DTO.MemberContractList();
            emailAddressessList =  new HUMMemberSearchEnrollment_DTO.EmailAddressesList();
            phoneNumberList = new HUMMemberSearchEnrollment_DTO.PhoneNumberList();
            for(Dom.XmlNode platformMember :  platformMembers.getChildElements())
            {
                if(platformMember.getName()=='FirstName')
                    familyMem.FirstName  = platformMember.getText();
                if(platformMember.getName()=='MiddleInitial')
                    familyMem.MiddleInitial = platformMember.getText();
                if(platformMember.getName()=='LastName')
                    familyMem.LastName = platformMember.getText();
                if(platformMember.getName()=='DateOfBirth')
                    familyMem.DateOfBirth = platformMember.getText();
                if(platformMember.getName()=='Gender')
                    familyMem.Gender = platformMember.getText();
                if(platformMember.getName()=='SSN')
                    familyMem.SSN = platformMember.getText();
                if(platformMember.getName()=='Age')
                    familyMem.Age = platformMember.getText();
                if(platformMember.getName()=='Address')
                    familyMem.Address = parseAddress(platformMember);
                if(platformMember.getName()=='PID')
                    familyMem.PID = platformMember.getText();
                if(platformMember.getName()=='SrcPlatformCode')
                    familyMem.SrcPlatformCode = platformMember.getText();
                if(platformMember.getName()=='Prefix')
                    familyMem.Prefix = platformMember.getText();
                if(platformMember.getName()=='Suffix')
                    familyMem.Suffix = platformMember.getText();
                if(platformMember.getName()=='MiddleName')
                    familyMem.MiddleName = platformMember.getText();
                if(platformMember.getName()=='MaritalStatus')
                    familyMem.MaritalStatus = platformMember.getText();
                if(platformMember.getName()=='Smoker')
                    familyMem.Smoker = platformMember.getText();
                if(platformMember.getName()=='Language')
                    familyMem.Language = platformMember.getText();
                if(platformMember.getName()=='DeceasedDate')
                    familyMem.DeceasedDate = platformMember.getText();
                if(platformMember.getName()=='MemberContracts')
                    memberContractList = parseMemberContracts(platformMember);
                if(platformMember.getName()=='EMailAddresses')
                    emailAddressessList = parseEMailAddresses(platformMember);
                if(platformMember.getName()=='PhoneNumbers')
                    phoneNumberList = parsePhoneNumbers(platformMember); 
            }
            familyMember.Member = familyMem;
            familyMember.MemberContractList = memberContractList;
            familyMember.EmailAddressList = emailAddressessList;
            familyMember.PhoneNumberList = phoneNumberList;
            family.add(familyMember);

        }
        familyList.Family=family;
        return familyList;
    }
    /*  
     * Method name : parseMemberContracts
     * Description : This method is called parseEEESXMLResponse
                    It will return the HUMMemberSearchEnrollment_DTO.MemberContractList object containing response
     * Return Type : HUMMemberSearchEnrollment_DTO.MemberContractList
     * Parameter : This method accepts Dom.XmlNode
     */
    public static HUMMemberSearchEnrollment_DTO.MemberContractList parseMemberContracts(dom.XmlNode MemberContractsNode)
    {
        HUMMemberSearchEnrollment_DTO.MemberContractList memberContractList = new HUMMemberSearchEnrollment_DTO.MemberContractList();
        List<HUMMemberSearchEnrollment_DTO.MemberContracts> memberContract = new List<HUMMemberSearchEnrollment_DTO.MemberContracts>();
        HUMMemberSearchEnrollment_DTO.MemberContracts memberContracts ;
        
        for(Dom.XmlNode memberCon : MemberContractsNode.getChildElements())
        {
            memberContracts = new HUMMemberSearchEnrollment_DTO.MemberContracts(); 
            for(Dom.XmlNode memcon : memberCon.getChildElements())
            {
                if(memcon.getName()=='ContractID')
                    memberContracts.ContractID = memcon.getText();
                if(memcon.getName()=='EffectiveDate')
                    memberContracts.EffectiveDate = memcon.getText();
                if(memcon.getName()=='EndDate')
                    memberContracts.EndDate = memcon.getText();
                if(memcon.getName()=='Group')
                    memberContracts.Groups=parseGroup(memcon);
                if(memcon.getName()=='Coverages')
                    memberContracts.Coverages=parseCoverages(memcon);
            }
            memberContract.add(memberContracts);
        }
        memberContractList.MemberContracts = memberContract;
        return memberContractList;
    }
    /*  
     * Method name : parseGroup
     * Description : This method is called parseEEESXMLResponse
                    It will return the HUMMemberSearchEnrollment_DTO.Groups object containing response
     * Return Type : HUMMemberSearchEnrollment_DTO.Groups
     * Parameter : This method accepts Dom.XmlNode
     */
    public static HUMMemberSearchEnrollment_DTO.Groups parseGroup(Dom.XmlNode GroupNode)
    {
        HUMMemberSearchEnrollment_DTO.Groups groups = new HUMMemberSearchEnrollment_DTO.Groups();
        for(Dom.XmlNode grpNode :  GroupNode.getChildElements())
        {   
            if(grpNode.getName()=='GroupID')
                groups.GroupID =  grpNode.getText();
            if(grpNode.getName()=='GroupName')
                groups.GroupName =  grpNode.getText();  
            
        }
        return groups;
    }
    /*  
     * Method name : parseCoverages
     * Description : This method is called parseEEESXMLResponse
                    It will return the HUMMemberSearchEnrollment_DTO.Coverages object containing response
     * Return Type : HUMMemberSearchEnrollment_DTO.Coverages
     * Parameter : This method accepts Dom.XmlNode
     */
    public static HUMMemberSearchEnrollment_DTO.Coverages parseCoverages(Dom.XmlNode CoveragesNode)
    {
        HUMMemberSearchEnrollment_DTO.Coverages coverage = new HUMMemberSearchEnrollment_DTO.Coverages();
        List<HUMMemberSearchEnrollment_DTO.PlatformMemberCoverage> platformMemberCoverages = new List<HUMMemberSearchEnrollment_DTO.PlatformMemberCoverage>();
        HUMMemberSearchEnrollment_DTO.PlatformMemberCoverage platformMemberCoverage;
        for(Dom.XmlNode  coverages : CoveragesNode.getChildElements())
        {
            platformMemberCoverage = new HUMMemberSearchEnrollment_DTO.PlatformMemberCoverage();
            Integer cnt=0;
            for(Dom.XmlNode pfCoverage : coverages.getChildElements())
            {
                if(pfCoverage.getName()=='LineOfBusiness')
                    platformMemberCoverage.LineOfBusiness =  pfCoverage.getText();
                if(pfCoverage.getName()=='Category')
                    platformMemberCoverage.Category =  pfCoverage.getText();
                if(pfCoverage.getName()=='SrcPlatformCode')
                    platformMemberCoverage.SrcPlatformCode =  pfCoverage.getText();
                if(pfCoverage.getName()=='CategoryDesciption')
                    platformMemberCoverage.CategoryDesciption =  pfCoverage.getText();
                if(pfCoverage.getName()=='CoverageTier')
                    platformMemberCoverage.CoverageTier =  pfCoverage.getText();
                if(pfCoverage.getName()=='MemberCoverageEffectiveDate')
                    platformMemberCoverage.MemberCoverageEffectiveDate =  pfCoverage.getText();
                if(pfCoverage.getName()=='MemberCoverageEndDate')
                    platformMemberCoverage.MemberCoverageEndDate =  pfCoverage.getText();
                if(pfCoverage.getName()=='MemberEnrollmentEffectiveDate')
                    platformMemberCoverage.MemberEnrollmentEffectiveDate =  pfCoverage.getText();
                if(pfCoverage.getName()=='MemberEnrollmentEndDate')
                    platformMemberCoverage.MemberEnrollmentEndDate =  pfCoverage.getText();
                if(pfCoverage.getName()=='PrePostTax')
                    platformMemberCoverage.PrePostTax =  pfCoverage.getText();
                if(pfCoverage.getName()=='CoveragePeriod')
                    platformMemberCoverage.CoveragePeriod =  pfCoverage.getText();
                if(pfCoverage.getName()=='RelationCode')
                    platformMemberCoverage.RelationCode =  pfCoverage.getText();
                if(pfCoverage.getName()=='TermedReason')
                    platformMemberCoverage.TermedReason =  pfCoverage.getText();
                if(pfCoverage.getName()=='Student')
                    platformMemberCoverage.Student =  pfCoverage.getText();
                if(pfCoverage.getName()=='Disabled')
                    platformMemberCoverage.Disabled =  pfCoverage.getText();
                if(pfCoverage.getName()=='MemberIDBase')
                    platformMemberCoverage.MemberIDBase =  pfCoverage.getText();
                if(pfCoverage.getName()=='MemberIDDependentCode')
                    platformMemberCoverage.MemberIDDependentCode =  pfCoverage.getText();
                if(pfCoverage.getName()=='SubscriberIDDependentCode')
                    platformMemberCoverage.SubscriberIDDependentCode =  pfCoverage.getText();
                if(pfCoverage.getName()=='HSAContributions')
                    platformMemberCoverage.HSAContributions =  pfCoverage.getText();
                if(pfCoverage.getName()=='TobaccoUser')
                    platformMemberCoverage.TobaccoUser =  pfCoverage.getText();
            }
            platformMemberCoverages.add(platformMemberCoverage);
           cnt++;
        }
        coverage.PlatformMemberCoverage = platformMemberCoverages;
        return coverage;
    }
}