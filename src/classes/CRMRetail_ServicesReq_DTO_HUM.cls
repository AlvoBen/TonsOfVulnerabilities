/***********************************************************************************************************************************
* Apex Class Name  : CRMRetail_ServicesReq_DTO_HUM 
* Version          : 1.0
* Created Date     : 6/14/2022
* Function         : DTO Class for consolidated web service request
****************************************************************************
* Developer Name                             Code Review #           Date                 Description
*---------------------------------------------------------------------------------------------------------------------------------
* Lakshmi Madduri                       					         6/14/2022           US-3258682 T1PRJ0154546 / SF / MF9 Storefront - SNP: Indicators Field - Data Population
* Vinoth L                                         	   	   			 4/26/2023           US-4566290: T1PRJ0154546 / SF / MF3 Storefront: Display MyHumana Enrollment & Go365 Enrollment - Backend Logic
**********************************************************************************************************************************/
public class CRMRetail_ServicesReq_DTO_HUM {
	public GetSDoHInfo GetSDoHInfo;
	public GetEngagementIndexInfo GetEngagementIndexInfo;
	public GetMedicareMemberInfo GetMedicareMemberInfo;
	public GetMHG365Info GetMHG365Info;

	public class GetMemberInfo {
		public List<MemberDetails> MemberDetails;
	}

	public class GetEngagementIndexInfo {
		public String EnterpriseId;
	}

	public class GetSDoHInfo {
		public String EnterpriseId;
		public String toDate;
		public String fromDate;
	}

	public class GetMedicareMemberInfo {
		public GetMemberInfo getMemberInfo;
	}

	public class MemberDetails {
		public String MedicareID;
		public String MemberCardID;
		public String GroupID;
		public String BenefitSequenceNumber;
		public String AsOfDate;
		public String LISInfo;
		public String LTSSInfo;
		public String ESRDInfo;
		public String SPAPInfo;
		public String SNPInfo;
		public String POAInfo;
	}

	public class GetMHG365Info {
		public String IDType;
        public String IDValue;
        public String VitalityFlag;
        
	}
}