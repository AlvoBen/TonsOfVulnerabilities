/**************************************************************************** 
Apex Class Name  : MemberPlanInfoWrapper_DTO_HUM_V2
Version          : 1.0  
Created Date     : 5/27/2022
Function         : DTO class benefit Accums service  
Test class       : AccumsType_LT_HUM_V2
****************************************************************************
Modification Log:
* Developer Name                                  Date                       Description
*---------------------------------------------------------------------------------------------------
* Swapnali Sonawane 							  5/27/2022				     US- 3143662 Benefit Accums
***************************************************************************************************/
public with sharing class MemberPlanInfoWrapper_DTO_HUM_V2 {
    public Set<ID> setRemainingPolicyMember =new Set<ID>();
    public List<String> lstLabels = new List<String> ();
    public MemberPlan objCurrentPolicyMember = new MemberPlan();
    public List<MemberPlan> lstPolicyMember = new List<MemberPlan>();
    public List<VobBenefitsCurrent_DTO_HUM> listOfDTOData = new List<VobBenefitsCurrent_DTO_HUM>();
    public String accumsType ='';
    public Integer startCounter = 0;
    public date dtOfService = null;
    
    /**
    * Returns None
    * <p>
    * Constructor for the class , Initialize variables
    * 
    * @param  subscriberGenKey in string format.
    * @param  MemberPlan record
    * @param  Boolean; yes if memgenkey is vaialable in cache else false.
    * @param  Boolean; yes if account is already sent for webActivity service else false.
    * @param  date dtDateOfService
    * @return  None
    * @see
    */
    public MemberPlanInfoWrapper_DTO_HUM_V2(MemberPlan tobjCurrentPolicyMember,List<MemberPlan> tlstPolicyMember,Set<ID> tsetRemainingPolicyMember, List<String> tlstLabels,List<VobBenefitsCurrent_DTO_HUM> tlistOfDTOData,String  sAccumsType,Integer iStartCounter, date dtDateOfService)
    {
        objCurrentPolicyMember = tobjCurrentPolicyMember;
        lstPolicyMember = tlstPolicyMember;
        setRemainingPolicyMember = tsetRemainingPolicyMember;
        lstLabels = tlstLabels;
        listOfDTOData = tlistOfDTOData;
        accumsType = sAccumsType;
        startCounter = iStartCounter;
        dtOfService  = dtDateOfService;
    }
}