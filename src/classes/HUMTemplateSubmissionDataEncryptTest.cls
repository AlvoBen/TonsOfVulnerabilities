/*****************************************************************************************************************************************************************************
Apex Class Name : HUMTemplateSubmissionDataEncryptTest
Version         : 1.0
Created On      : 06/11/2015
Function        : This class has test methods to check if the passed value is encrypted or not and if not, pass the value to encryption service.

Modification Log: 

* Developer Name           Code Review                 Date                        Description
*------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Jeremy                                            06/11/2015                  Original Version
* Kalyan Lanka                                      06/11/2015                  Original Version
****************************************************************************************************************************************************************/

@isTest
private class HUMTemplateSubmissionDataEncryptTest
{

    @testSetup
    static void setMe() {
        List<HUMConstants__c> hcList = new List<HUMConstants__c>();
        hcList.add( new HUMConstants__c(Name= 'HUMExclamation',StringValue__c='!', IntegerValue__c = 0));
        hcList.add(new  HUMConstants__c(Name= 'AMP',StringValue__c='&', IntegerValue__c = 0));
        hcList.add( new HUMConstants__c(Name= 'HUMDoubleStar',StringValue__c='**', IntegerValue__c = 0));
        hcList.add( new HUMConstants__c(Name= 'HUMValueOne',StringValue__c='1', IntegerValue__c = 1));
        hcList.add( new HUMConstants__c(Name= 'HUMOpenBrace',StringValue__c='(', IntegerValue__c = 1));
        hcList.add( new HUMConstants__c(Name= 'HUMAT',StringValue__c='@', IntegerValue__c = 1));
        hcList.add( new HUMConstants__c(Name= 'HUMDash',StringValue__c='-', IntegerValue__c = 1));
        hcList.add( new HUMConstants__c(Name= 'HumTemplateDataSubmisstion_Value',StringValue__c='Value__c', IntegerValue__c = 1));
        hcList.add( new HUMConstants__c(Name= 'Double_Backslash',StringValue__c='////', IntegerValue__c = 1));
        hcList.add( new HUMConstants__c(Name= 'Single_Backslash',StringValue__c='//', IntegerValue__c = 1));
        hcList.add( new HUMConstants__c(Name= 'Template_Submission_Data',StringValue__c='Template_Submission_Data__c', IntegerValue__c = 1));
        insert hcList;
    }

    static testMethod void encryptValuesTest()
    {
        List<HUMTemplateSubmissionDataDTO> Values = HUMTemplateSubmissionDataEncrypt.encryptValues(GetMockData());
        
        System.assert(Values.size() == 1);
        System.assert(Values[0].SubmissionData.size() == 10);
       
    }
    
    private static List<HUMTemplateSubmissionDataDTO> GetMockData()
    {
        HUMTemplateSubmissionDataDTO MockData = new HUMTemplateSubmissionDataDTO();
        
        Template__c t = new Template__c();
        t.Description__c = 'This is a test template';
        
        Insert t;
        
        Template_Field__c tf = new Template_Field__c();
        tf.Data_Type__c = 'String';
        tf.Name = 'Value';
        tf.Template__c = t.Id;
        
        Insert tf;
        
        Template_Submission_Owner__c o = new Template_Submission_Owner__c();
        o.Object_Owner_ID__c = '123456';
        o.Object_Owner_Type__c = 'Case';
        
        Insert o;
        
        Template_Submission__c s = new Template_Submission__c();
        s.Submission_Owner__c = o.Id;
        s.Template__c = t.Id;
            
        Insert s;
        
        MockData.SubmissionData = new List<Template_Submission_Data__c>();
        
        for(integer i = 0; i < 10; i++)
        {
          Template_Submission_Data__c sd = new Template_Submission_Data__c();
          sd.Value__c = 'Value' + i;
          sd.Template_Field__c = tf.Id;
          sd.Template_Submission__c = s.Id;
            
          Insert sd;
          
          MockData.SubmissionData.add(sd);
           
        }
        
        return new List<HUMTemplateSubmissionDataDTO>{ MockData };
            
    }
}