/**************************************************************************** 
Apex Class Name  : HUMMemberSearchEnrollment_DTO
Version          : 1.0  
Created Date     : 10/21/2022 
Function         : Here the response from EES Legacy Search service stored
Test Class       : HUMMemberSearchEnrollmentControllerV2Test

Modification Log: 
* Developer Name           Code Review                  Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Nilesh Gadkar                                        10/21/2022                 Original Version
*******************************************************************************************************************************/  


public class HUMMemberSearchEnrollment_DTO {
    public List<GetResponse> GetResponse;

    public class GetResponse{
        public Member Member;
        public FamilyList FamilyList;
        public EmailAddressesList EmailAddressesList;
        public PhoneNumberList PhoneNumberList;
    }
    public class Member{
        public String FirstName = '';
        public String MiddleInitial ='';
        public String LastName = '';
        public String DateOfBirth='';
        public String Gender ='';
        public String SSN ='';
        public String Age='';
        public Address Address;
        public String PID='';
        public String SrcPlatformCode='';
        public String Prefix='';
        public String Suffix='';
        public String MiddleName ='';
        public String MaritalStatus='';
        public String Smoker='';
        public String Language='';
        public String DeceasedDate ='';
    }
    public class Address{
        public String Line1='';
        public String Line2='';
        public String Line3='';
        public String City='';
        public String Country='';
        public String State ='';
        public String Zip='';
        public String ZipPlus='';

    }
    public class FamilyList{
        public list<Family> Family;
    }
    public class Family{
        public Member Member;
        public MemberContractList MemberContractList;
        public EmailAddressesList EmailAddressList;
        public PhoneNumberList PhoneNumberList;
    }
    public class MemberContractList{
        public list<MemberContracts> MemberContracts;
    }
    public class MemberContracts{
        public String ContractID='';
        public String EffectiveDate='';
        public string EndDate='';
        public Groups Groups;
        public Coverages Coverages;
    }
    public class Groups{
        public String GroupID='';
        public String GroupName='';
    }
    public class Coverages{
        public List<PlatformMemberCoverage> PlatformMemberCoverage;
    }
    public class PlatformMemberCoverage{
        public String LineOfBusiness='';
        public String Category='';
        public String SrcPlatformCode='';
        public String CategoryDesciption='';
        public String CoverageTier='';
        public String MemberCoverageEffectiveDate='';
        public String MemberCoverageEndDate='';
        public String MemberEnrollmentEffectiveDate='';
        public String MemberEnrollmentEndDate='';
        public String PrePostTax='';
        public String CoveragePeriod='';
        public String RelationCode='';
        public String TermedReason='';
        public String Student='';
        public String Disabled='';
        public String MemberIDBase='';
        public String MemberIDDependentCode='';
        public String SubscriberIDDependentCode='';
        public PropertiesList PropertiesList;
        public String HSAContributions='';
        public String TobaccoUser='';

    }
    public class PropertiesList{
        public list<Properties> Properties;
    }
    public class Properties{
        public String PropertyKey='';
        public String PrpertyValue='';
    }
    public class EmailAddressesList{
        public list<EmailAddress> EmailAddress;
    }
    public class EmailAddress{
        public String Address='';
        public String Type='';
        public String Primary='';
    }
    public class PhoneNumberList{
        public list<PhoneNumber> PhoneNumber;
    }
    public class PhoneNumber{
        public String PNumber='';
        public String Extension='';
        public String Type='';
        public String Primary='';
    }

}