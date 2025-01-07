/***************************************************************************************************************
 Developer Name          Code Review                Date                       Description
 ------------------------------------------------------------------------------------------------------------------------------
  Vaishnavi Manda                                  05/17/2021                Added MF6 Checkmarx test method
  Vamshi Krishna Pemberthi							09/01/2021				improved code coverage 
 *******************************************************************************************************************************/
@istest
public class CreateCaaserequest1_T_hum {
    
     static testMethod void test_CreateInteractionWithCase_ForMember()
    {
                             Test.startTest();   
         HUMConstants__c  objhumc = New HUMConstants__c();
            objhumc.name ='UNKNOWN_MEMBER';
            objhumc.StringValue__c ='Unknown Member';
            insert objhumc ;
            system.assertEquals(objhumc.id != null, True);
         Account ProviderAcc = HUMTestDataHelperTest.getAccount();
            ProviderAcc.Name = 'test account';
            ProviderAcc.Enterprise_ID__c = '123456';
            ProviderAcc.NPI_ID__c = '1234567890';
            ProviderAcc.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Provider').getRecordTypeId();
            insert ProviderAcc;
        HUMSearchLimit__c Humsearch=new HUMSearchLimit__c();
        Humsearch.Search_Limit__c=10;
        Humsearch.name='Member_SearchLimit';
        insert Humsearch;
      //  CreateInteractionResponse_DTO_HUM response1 = CreateInteractionWithCaseRest_D_HUM.createInteraction(owrap);
      //  system.assertNotEquals(response1,NULL);
           List<InteractingAccount_DTO_HUM> wrap = new list<InteractingAccount_DTO_HUM>();
           InteractingAccount_DTO_HUM InteractingAccount=new InteractingAccount_DTO_HUM();
InteractingAccount.AddressLine1='';
        InteractingAccount.AddressLine2='';
        InteractingAccount.City='';
        InteractingAccount.DOB='';
        InteractingAccount.EmailID='';
 InteractingAccount.FirstName='';
       InteractingAccount.HomePhone='';
       InteractingAccount.ID='';
       InteractingAccount.IDType='UnknownMember';
           InteractingAccount.PlatformCode='';
        InteractingAccount.MemberIdDependentCode='';
        InteractingAccount.LastName='';
        InteractingAccount.State='';
       InteractingAccount.EntityType='Member';
        InteractingAccount.ZipCode='';
        InteractingAccount.ZipPlus='';
        InteractingAccount.ProviderClassification='Facility';
        InteractingAccount.AccountName='';
           wrap.add(InteractingAccount);
          try{ 
        //CreateCaseRequest_H_HUM.getInteractingAccountDetails_V4(wrap, '');
           }catch(exception ex){
			   HUMExceptionHelper.logErrors(ex,'CreateCaaserequest1_T_hum','test_CreateInteractionWithCase_ForMember'); 			
		   }
        CreateCaseRequest_H_HUM.getGroupAccountMap(InteractingAccount, '');
           CreateCaseRequest_H_HUM.getGroupRecordId(wrap, '');
           CreateCaseRequest_H_HUM.getmemberRecordId(wrap, '');
        
         
           CreateCaseRequest_H_HUM.getProviderAccountMap(InteractingAccount, '');
        
           CreateCaseRequest_H_HUM.getInteractionWrapObj(InteractingAccount, '');
          // CreateCaseRequest_H_HUM.prepareUnknownAccts(New CaseServiceRestInteraction_DTO_HUM(),'');
          CreateCaseRequest_H_HUM.insertConsumerTaxIds(New list<CreateCaseRequest_H_HUM.CreateUnknownMembers_DTO_HUM>());
        try{
         CreateCaseRequest_H_HUM.getProviderRecordId(new list<InteractingAccount_DTO_HUM>(), '');
        }catch(exception ex){
			HUMExceptionHelper.logErrors(ex,'CreateCaaserequest1_T_hum','test_CreateInteractionWithCase_ForMember'); 			
		}
         
               Test.StopTest();
    }
 static testMethod void test_CreateInteractionWithCase_ForMember1()
    {
                             Test.startTest();   
         HUMConstants__c  objhumc = New HUMConstants__c();
            objhumc.name ='UNKNOWN_MEMBER';
            objhumc.StringValue__c ='Unknown Member';
            insert objhumc ;
            system.assertEquals(objhumc.id != null, True);
         Account ProviderAcc = HUMTestDataHelperTest.getAccount();
            ProviderAcc.Name = 'test account';
            ProviderAcc.Enterprise_ID__c = '123456';
            ProviderAcc.NPI_ID__c = '1234567890';
            ProviderAcc.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Provider').getRecordTypeId();
            insert ProviderAcc;
      //  CreateInteractionResponse_DTO_HUM response1 = CreateInteractionWithCaseRest_D_HUM.createInteraction(owrap);
      //  system.assertNotEquals(response1,NULL);
              HUMSearchLimit__c Humsearch=new HUMSearchLimit__c();
        Humsearch.Search_Limit__c=10;
        Humsearch.name='Member_SearchLimit';
        insert Humsearch;
           List<InteractingAccount_DTO_HUM> wrap = new list<InteractingAccount_DTO_HUM>();
           InteractingAccount_DTO_HUM InteractingAccount=new InteractingAccount_DTO_HUM();
InteractingAccount.AddressLine1='';
        InteractingAccount.AddressLine2='';
        InteractingAccount.City='';
        InteractingAccount.DOB='';
        InteractingAccount.EmailID='';
 InteractingAccount.FirstName='';
       InteractingAccount.HomePhone='';
       InteractingAccount.ID='';//ProviderAcc.Id;
       InteractingAccount.IDType='Member-Id-Base';
           InteractingAccount.PlatformCode='';
        InteractingAccount.MemberIdDependentCode='';
        InteractingAccount.LastName='';
        InteractingAccount.State='';
       InteractingAccount.EntityType='Member';
        InteractingAccount.ZipCode='';
        InteractingAccount.ZipPlus='';
        InteractingAccount.ProviderClassification='Facility';
        InteractingAccount.AccountName='';
           wrap.add(InteractingAccount);
          // CreateCaseRequest_H_HUM.getInteractingAccountDetails_V4(wrap, '');
           CreateCaseRequest_H_HUM.getGroupAccountMap(InteractingAccount, '');
           CreateCaseRequest_H_HUM.getGroupRecordId(wrap, '');
           CreateCaseRequest_H_HUM.getmemberRecordId(wrap, '');
          
           CreateCaseRequest_H_HUM.getProviderAccountMap(InteractingAccount, '');
        
           CreateCaseRequest_H_HUM.getInteractionWrapObj(InteractingAccount, '');
          CreateCaseRequest_H_HUM.insertConsumerTaxIds(new list<CreateCaseRequest_H_HUM.CreateUnknownMembers_DTO_HUM>());
        try{
         CreateCaseRequest_H_HUM.getProviderRecordId(new list<InteractingAccount_DTO_HUM>(), '');
        }catch(exception ex){
			HUMExceptionHelper.logErrors(ex,'CreateCaaserequest1_T_hum','test_CreateInteractionWithCase_ForMember1'); 			
		}
         
               Test.StopTest();
    }
static testMethod void test_CreateInteractionWithCase_ForMember2()
    {
                             Test.startTest();   
         HUMConstants__c  objhumc = New HUMConstants__c();
            objhumc.name ='UNKNOWN_MEMBER';
            objhumc.StringValue__c ='Unknown Member';
            insert objhumc ;
            system.assertEquals(objhumc.id != null, True);
         Account ProviderAcc = HUMTestDataHelperTest.getAccount();
            ProviderAcc.Name = 'test account';
            ProviderAcc.Enterprise_ID__c = '123456';
            ProviderAcc.NPI_ID__c = '1234567890';
            ProviderAcc.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Provider').getRecordTypeId();
            insert ProviderAcc;
                   HUMSearchLimit__c Humsearch=new HUMSearchLimit__c();
        Humsearch.Search_Limit__c=10;
        Humsearch.name='Member_SearchLimit';
        insert Humsearch;
      //  CreateInteractionResponse_DTO_HUM response1 = CreateInteractionWithCaseRest_D_HUM.createInteraction(owrap);
      //  system.assertNotEquals(response1,NULL);
           List<InteractingAccount_DTO_HUM> wrap = new list<InteractingAccount_DTO_HUM>();
           InteractingAccount_DTO_HUM InteractingAccount=new InteractingAccount_DTO_HUM();
InteractingAccount.AddressLine1='';
        InteractingAccount.AddressLine2='';
        InteractingAccount.City='';
        InteractingAccount.DOB='';
        InteractingAccount.EmailID='';
 InteractingAccount.FirstName='';
       InteractingAccount.HomePhone='';
       InteractingAccount.ID=ProviderAcc.Id;
       InteractingAccount.IDType='UnknownMember';
           InteractingAccount.PlatformCode='';
        InteractingAccount.MemberIdDependentCode='';
        InteractingAccount.LastName='';
        InteractingAccount.State='';
       InteractingAccount.EntityType='Member';
        InteractingAccount.ZipCode='';
        InteractingAccount.ZipPlus='';
        InteractingAccount.ProviderClassification='Facility';
        InteractingAccount.AccountName='';
           wrap.add(InteractingAccount);
          try{ 
        //CreateCaseRequest_H_HUM.getInteractingAccountDetails_V4(wrap, '');
           }catch(exception ex){
			   HUMExceptionHelper.logErrors(ex,'CreateCaaserequest1_T_hum','test_CreateInteractionWithCase_ForMember2'); 	
		   }
        CreateCaseRequest_H_HUM.getGroupAccountMap(InteractingAccount, '');
           CreateCaseRequest_H_HUM.getGroupRecordId(wrap, '');
          CreateCaseRequest_H_HUM.getmemberRecordId(wrap, '');
        
         
           CreateCaseRequest_H_HUM.getProviderAccountMap(InteractingAccount, '');
        
           CreateCaseRequest_H_HUM.getInteractionWrapObj(InteractingAccount, '');
          // CreateCaseRequest_H_HUM.prepareUnknownAccts(New CaseServiceRestInteraction_DTO_HUM(),'');
          CreateCaseRequest_H_HUM.insertConsumerTaxIds(New list<CreateCaseRequest_H_HUM.CreateUnknownMembers_DTO_HUM>());
        try{
         CreateCaseRequest_H_HUM.getProviderRecordId(new list<InteractingAccount_DTO_HUM>(), '');
        }catch(exception ex){
			HUMExceptionHelper.logErrors(ex,'CreateCaaserequest1_T_hum','test_CreateInteractionWithCase_ForMember2'); 	
		}
         
               Test.StopTest();
    }
     static testMethod void test_CreateInteractionWithCase_ForMember3()
    {
                             Test.startTest();   
         HUMConstants__c  objhumc = New HUMConstants__c();
            objhumc.name ='UNKNOWN_MEMBER';
            objhumc.StringValue__c ='Unknown Member';
            insert objhumc ;
            system.assertEquals(objhumc.id != null, True);
         Account ProviderAcc = HUMTestDataHelperTest.getAccount();
            ProviderAcc.Name = 'test account';
            ProviderAcc.Enterprise_ID__c = '123456';
            ProviderAcc.NPI_ID__c = '1234567890';
            ProviderAcc.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Provider').getRecordTypeId();
            insert ProviderAcc;
                    HUMSearchLimit__c Humsearch=new HUMSearchLimit__c();
        Humsearch.Search_Limit__c=10;
        Humsearch.name='Member_SearchLimit';
        insert Humsearch;
      //  CreateInteractionResponse_DTO_HUM response1 = CreateInteractionWithCaseRest_D_HUM.createInteraction(owrap);
      //  system.assertNotEquals(response1,NULL);
           List<InteractingAccount_DTO_HUM> wrap = new list<InteractingAccount_DTO_HUM>();
           InteractingAccount_DTO_HUM InteractingAccount=new InteractingAccount_DTO_HUM();
InteractingAccount.AddressLine1='';
        InteractingAccount.AddressLine2='';
        InteractingAccount.City='';
        InteractingAccount.DOB='';
        InteractingAccount.EmailID='';
 InteractingAccount.FirstName='';
       InteractingAccount.HomePhone='';
       InteractingAccount.ID=ProviderAcc.Id;
       InteractingAccount.IDType='UnknownMember';
           InteractingAccount.PlatformCode='';
        InteractingAccount.MemberIdDependentCode='';
        InteractingAccount.LastName='';
        InteractingAccount.State='';
       InteractingAccount.EntityType='UnknownProvider';
        InteractingAccount.ZipCode='';
        InteractingAccount.ZipPlus='';
        InteractingAccount.ProviderClassification='Facility';
        InteractingAccount.AccountName='';
           wrap.add(InteractingAccount);
          try{ 
        //CreateCaseRequest_H_HUM.getInteractingAccountDetails_V4(wrap, '');
           }catch(exception ex){
			   HUMExceptionHelper.logErrors(ex,'CreateCaaserequest1_T_hum','test_CreateInteractionWithCase_ForMember3'); 	
		   }
        CreateCaseRequest_H_HUM.getGroupAccountMap(InteractingAccount, '');
           CreateCaseRequest_H_HUM.getGroupRecordId(wrap, '');
           CreateCaseRequest_H_HUM.getmemberRecordId(wrap, '');
        
         
           CreateCaseRequest_H_HUM.getProviderAccountMap(InteractingAccount, '');
        
           CreateCaseRequest_H_HUM.getInteractionWrapObj(InteractingAccount, '');
          // CreateCaseRequest_H_HUM.prepareUnknownAccts(New CaseServiceRestInteraction_DTO_HUM(),'');
          CreateCaseRequest_H_HUM.insertConsumerTaxIds(New list<CreateCaseRequest_H_HUM.CreateUnknownMembers_DTO_HUM>());
        try{
         CreateCaseRequest_H_HUM.getProviderRecordId(new list<InteractingAccount_DTO_HUM>(), '');
        }catch(exception ex){
			HUMExceptionHelper.logErrors(ex,'CreateCaaserequest1_T_hum','test_CreateInteractionWithCase_ForMember3'); 
		}
		try{
         CreateCaseRequest_H_HUM.getProviderRecordId(null, '');
        }catch(exception ex){
			HUMExceptionHelper.logErrors(ex,'CreateCaaserequest1_T_hum','test_CreateInteractionWithCase_ForMember3'); 
		}
         
               Test.StopTest();
    }
     static testMethod void test_CreateInteractionWithCase_ForMember4()
    {
                             Test.startTest(); 
        
         HUMConstants__c  objhumc = New HUMConstants__c();
            objhumc.name ='UNKNOWN_MEMBER';
            objhumc.StringValue__c ='Unknown Member';
            insert objhumc ;
         HUMConstants__c  objhumc2 = New HUMConstants__c();
            objhumc2.name ='PIPE_CHARACTER';
            objhumc2.StringValue__c ='PIPE_CHARACTER';
            insert objhumc2 ;
            system.assertEquals(objhumc.id != null, True);
         Account ProviderAcc = HUMTestDataHelperTest.getAccount();
            ProviderAcc.Name = 'test account';
            ProviderAcc.Enterprise_ID__c = '123456';
            ProviderAcc.NPI_ID__c = '1234567890';
            ProviderAcc.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Provider').getRecordTypeId();
            insert ProviderAcc;
                    HUMSearchLimit__c Humsearch=new HUMSearchLimit__c();
        Humsearch.Search_Limit__c=10;
        Humsearch.name='Member_SearchLimit';
        insert Humsearch;
                        HUMConstants__c  objhumc1 = New HUMConstants__c();
                objhumc1.name ='UNKNOWN_MEMBER_EXTID';
                objhumc1.StringValue__c ='UKM|00|SFDC|';
                insert objhumc1 ;
      //  CreateInteractionResponse_DTO_HUM response1 = CreateInteractionWithCaseRest_D_HUM.createInteraction(owrap);
      //  system.assertNotEquals(response1,NULL);
           List<InteractingAccount_DTO_HUM> wrap = new list<InteractingAccount_DTO_HUM>();
           InteractingAccount_DTO_HUM InteractingAccount=new InteractingAccount_DTO_HUM();
InteractingAccount.AddressLine1='';
        InteractingAccount.AddressLine2='';
        InteractingAccount.City='';
        InteractingAccount.DOB='';
        InteractingAccount.EmailID='';
 InteractingAccount.FirstName='';
       InteractingAccount.HomePhone='';
       InteractingAccount.ID=ProviderAcc.Id;
       InteractingAccount.IDType='GroupID';
           InteractingAccount.PlatformCode='';
        InteractingAccount.MemberIdDependentCode='';
        InteractingAccount.LastName='';
        InteractingAccount.State='';
       InteractingAccount.EntityType='Group';
        InteractingAccount.ZipCode='';
        InteractingAccount.ZipPlus='';
        InteractingAccount.ProviderClassification='Facility';
        InteractingAccount.AccountName='';
           wrap.add(InteractingAccount);
          try{ 
        //CreateCaseRequest_H_HUM.getInteractingAccountDetails_V4(wrap, '');
           }catch(exception ex){
			   HUMExceptionHelper.logErrors(ex,'CreateCaaserequest1_T_hum','test_CreateInteractionWithCase_ForMember4'); 
		   }
        CreateCaseRequest_H_HUM.getGroupAccountMap(InteractingAccount, '');
           CreateCaseRequest_H_HUM.getGroupRecordId(wrap, '');
                    try{
                        CreateCaseRequest_H_HUM.getmemberRecordId(wrap, '');
         }catch(HUMCustomException ex){
			 HUMExceptionHelper.logErrors(ex,'CreateCaaserequest1_T_hum','test_CreateInteractionWithCase_ForMember4');
		 }
         
           CreateCaseRequest_H_HUM.getProviderAccountMap(InteractingAccount, '');
        
           CreateCaseRequest_H_HUM.getInteractionWrapObj(InteractingAccount, '');
          // CreateCaseRequest_H_HUM.prepareUnknownAccts(new CaseServiceRestInteraction_DTO_HUM(),'');
          CreateCaseRequest_H_HUM.insertConsumerTaxIds(new list<CreateCaseRequest_H_HUM.CreateUnknownMembers_DTO_HUM>());
        try{
         CreateCaseRequest_H_HUM.getProviderRecordId(new list<InteractingAccount_DTO_HUM>(), '');
        }catch(exception ex){
			HUMExceptionHelper.logErrors(ex,'CreateCaaserequest1_T_hum','test_CreateInteractionWithCase_ForMember4');
		}
         
               Test.StopTest();
    }
     static testMethod void test_CreateInteractionWithCase_ForMember5()
    {
                             Test.startTest();   
         HUMConstants__c  objhumc = New HUMConstants__c();
            objhumc.name ='UNKNOWN_MEMBER';
            objhumc.StringValue__c ='Unknown Member';
            insert objhumc ;
            system.assertEquals(objhumc.id != null, True);
         Account ProviderAcc = HUMTestDataHelperTest.getAccount();
            ProviderAcc.Name = 'test account';
            ProviderAcc.Enterprise_ID__c = '123456';
            ProviderAcc.NPI_ID__c = '1234567890';
            ProviderAcc.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Provider').getRecordTypeId();
            insert ProviderAcc;
                    HUMSearchLimit__c Humsearch=new HUMSearchLimit__c();
        Humsearch.Search_Limit__c=10;
        Humsearch.name='Member_SearchLimit';
        insert Humsearch;
      //  CreateInteractionResponse_DTO_HUM response1 = CreateInteractionWithCaseRest_D_HUM.createInteraction(owrap);
      //  system.assertNotEquals(response1,NULL);
           List<InteractingAccount_DTO_HUM> wrap = new list<InteractingAccount_DTO_HUM>();
           InteractingAccount_DTO_HUM InteractingAccount=new InteractingAccount_DTO_HUM();
InteractingAccount.AddressLine1='';
        InteractingAccount.AddressLine2='';
        InteractingAccount.City='';
        InteractingAccount.DOB='';
        InteractingAccount.EmailID='';
 InteractingAccount.FirstName='';
       InteractingAccount.HomePhone='';
       InteractingAccount.ID=ProviderAcc.Id;
       InteractingAccount.IDType='UnknownMember';
           InteractingAccount.PlatformCode='';
        InteractingAccount.MemberIdDependentCode='';
        InteractingAccount.LastName='';
        InteractingAccount.State='';
       InteractingAccount.EntityType='Agent';
        InteractingAccount.ZipCode='';
        InteractingAccount.ZipPlus='';
        InteractingAccount.ProviderClassification='Facility';
        InteractingAccount.AccountName='';
           wrap.add(InteractingAccount);
          try{
        //CreateCaseRequest_H_HUM.getInteractingAccountDetails_V4(wrap, '');
           }catch(exception ex){
			   HUMExceptionHelper.logErrors(ex,'CreateCaaserequest1_T_hum','test_CreateInteractionWithCase_ForMember5');
		   }
        CreateCaseRequest_H_HUM.getGroupAccountMap(InteractingAccount, '');
           CreateCaseRequest_H_HUM.getGroupRecordId(wrap, '');
           CreateCaseRequest_H_HUM.getmemberRecordId(wrap, '');
			try{
            CreateCaseRequest_H_HUM.getGroupRecordId(null, '');
           }catch(exception ex){
            HUMExceptionHelper.logErrors(ex,'CreateCaaserequest1_T_hum','test_CreateInteractionWithCase_ForMember5');
         }
         
           CreateCaseRequest_H_HUM.getProviderAccountMap(InteractingAccount, '');
        
           CreateCaseRequest_H_HUM.getInteractionWrapObj(InteractingAccount, '');
          // CreateCaseRequest_H_HUM.prepareUnknownAccts(New CaseServiceRestInteraction_DTO_HUM(),'');
          CreateCaseRequest_H_HUM.insertConsumerTaxIds(New list<CreateCaseRequest_H_HUM.CreateUnknownMembers_DTO_HUM>());
        try{
         CreateCaseRequest_H_HUM.getProviderRecordId(new list<InteractingAccount_DTO_HUM>(), '');
        }catch(exception ex){
			HUMExceptionHelper.logErrors(ex,'CreateCaaserequest1_T_hum','test_CreateInteractionWithCase_ForMember5');
		}
         
               Test.StopTest();
    }
     static testMethod void test_CreateInteractionWithCase_ForMember6()
    {
                             Test.startTest(); 
        
            HUMConstants__c  objhumc3 = New HUMConstants__c();
            objhumc3.name ='PIPE_CHARACTER';
            objhumc3.StringValue__c ='PIPE_CHARACTER';
            insert objhumc3 ;
             HUMConstants__c  objhumc2 = New HUMConstants__c();
            objhumc2.name ='UNKNOWN_MEMBER_EXTID';
            objhumc2.StringValue__c ='UNKNOWN_MEMBER_EXTID';
            insert objhumc2 ;
         HUMConstants__c  objhumc = New HUMConstants__c();
            objhumc.name ='UNKNOWN_MEMBER';
            objhumc.StringValue__c ='Unknown Member';
            insert objhumc ;
            system.assertEquals(objhumc.id != null, True);
         Account ProviderAcc = new account();//HUMTestDataHelperTest.getAccount();
            ProviderAcc.Name = 'test account';
            ProviderAcc.Enterprise_ID__c = '123456';
            ProviderAcc.NPI_ID__c = '1234567890';
            ProviderAcc.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Provider').getRecordTypeId();
            insert ProviderAcc;
                    HUMSearchLimit__c Humsearch=new HUMSearchLimit__c();
        Humsearch.Search_Limit__c=10;
        Humsearch.name='Member_SearchLimit';
        insert Humsearch;
      //  CreateInteractionResponse_DTO_HUM response1 = CreateInteractionWithCaseRest_D_HUM.createInteraction(owrap);
      //  system.assertNotEquals(response1,NULL);
           List<InteractingAccount_DTO_HUM> wrap = new list<InteractingAccount_DTO_HUM>();
           InteractingAccount_DTO_HUM InteractingAccount=new InteractingAccount_DTO_HUM();
InteractingAccount.AddressLine1='';
        InteractingAccount.AddressLine2='';
        InteractingAccount.City='';
        InteractingAccount.DOB='';
        InteractingAccount.EmailID='';
 InteractingAccount.FirstName='';
       InteractingAccount.HomePhone='';
       InteractingAccount.ID=ProviderAcc.Id;
       InteractingAccount.IDType='Member-Id-Base';
           InteractingAccount.PlatformCode='';
        InteractingAccount.MemberIdDependentCode='member';
        InteractingAccount.LastName='';
        InteractingAccount.State='';
       InteractingAccount.EntityType='Member';
        InteractingAccount.ZipCode='';
        InteractingAccount.ZipPlus='';
        InteractingAccount.ProviderClassification='Facility';
        InteractingAccount.AccountName='';
           wrap.add(InteractingAccount);
          try{ 
        //CreateCaseRequest_H_HUM.getInteractingAccountDetails_V4(wrap, '');
           }catch(exception ex){
			   HUMExceptionHelper.logErrors(ex,'CreateCaaserequest1_T_hum','test_CreateInteractionWithCase_ForMember6');
		   }
        CreateCaseRequest_H_HUM.getGroupAccountMap(InteractingAccount, '');
		try{
         CreateCaseRequest_H_HUM.getGroupAccountMap(null, '');
        }catch(exception ex){
			HUMExceptionHelper.logErrors(ex,'CreateCaaserequest1_T_hum','test_CreateInteractionWithCase_ForMember6');
		}
           CreateCaseRequest_H_HUM.getGroupRecordId(wrap, '');
           try{  CreateCaseRequest_H_HUM.getmemberRecordId(wrap, '');
        }catch(HUMCustomException ex){
			HUMExceptionHelper.logErrors(ex,'CreateCaaserequest1_T_hum','test_CreateInteractionWithCase_ForMember6');
		}
         
           CreateCaseRequest_H_HUM.getProviderAccountMap(InteractingAccount, '');
        
           CreateCaseRequest_H_HUM.getInteractionWrapObj(InteractingAccount, '');
          // CreateCaseRequest_H_HUM.prepareUnknownAccts(new CaseServiceRestInteraction_DTO_HUM(),'');
          CreateCaseRequest_H_HUM.insertConsumerTaxIds(new list<CreateCaseRequest_H_HUM.CreateUnknownMembers_DTO_HUM>());
        try{
         CreateCaseRequest_H_HUM.getProviderRecordId(new list<InteractingAccount_DTO_HUM>(), '');
        }catch(exception ex){
			HUMExceptionHelper.logErrors(ex,'CreateCaaserequest1_T_hum','test_CreateInteractionWithCase_ForMember6');
		}
         
               Test.StopTest();
    }
     static testMethod void test_CreateInteractionWithCase_ForMember7()
    {
                             Test.startTest();   
         HUMConstants__c  objhumc = New HUMConstants__c();
            objhumc.name ='UNKNOWN_MEMBER_EXTID';
            objhumc.StringValue__c ='Unknown Member';
            insert objhumc ;
        HUMConstants__c  objhumc1 = New HUMConstants__c();
            objhumc1.name ='UNKNOWN_MEMBER';
            objhumc1.StringValue__c ='Unknown Member';
            insert objhumc1 ;
            system.assertEquals(objhumc.id != null, True);
         Account ProviderAcc = new account();//HUMTestDataHelperTest.getAccount();
            ProviderAcc.Name = 'test account';
            ProviderAcc.Enterprise_ID__c = '123456';
            ProviderAcc.NPI_ID__c = '1234567890';
            ProviderAcc.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Provider').getRecordTypeId();
            insert ProviderAcc;
                    HUMSearchLimit__c Humsearch=new HUMSearchLimit__c();
        Humsearch.Search_Limit__c=10;
        Humsearch.name='Member_SearchLimit';
        insert Humsearch;
      //  CreateInteractionResponse_DTO_HUM response1 = CreateInteractionWithCaseRest_D_HUM.createInteraction(owrap);
      //  system.assertNotEquals(response1,NULL);
           List<InteractingAccount_DTO_HUM> wrap = new list<InteractingAccount_DTO_HUM>();
           InteractingAccount_DTO_HUM InteractingAccount=new InteractingAccount_DTO_HUM();
InteractingAccount.AddressLine1='';
        InteractingAccount.AddressLine2='';
        InteractingAccount.City='';
        InteractingAccount.DOB='';
        InteractingAccount.EmailID='';
 InteractingAccount.FirstName='';
       InteractingAccount.HomePhone='';
       InteractingAccount.ID=ProviderAcc.Id;
       InteractingAccount.IDType='Member-Id-Base';
           InteractingAccount.PlatformCode='';
        InteractingAccount.MemberIdDependentCode='member';
        InteractingAccount.LastName='';
        InteractingAccount.State='';
       InteractingAccount.EntityType='Member';
        InteractingAccount.ZipCode='';
        InteractingAccount.ZipPlus='';
        InteractingAccount.ProviderClassification='Facility';
        InteractingAccount.AccountName='';
           wrap.add(InteractingAccount);
          try{
        //CreateCaseRequest_H_HUM.getInteractingAccountDetails_V4(wrap, '');
           }catch(exception ex){
			   HUMExceptionHelper.logErrors(ex,'CreateCaaserequest1_T_hum','test_CreateInteractionWithCase_ForMember7');
		   }
        CreateCaseRequest_H_HUM.getGroupAccountMap(InteractingAccount, '');
           CreateCaseRequest_H_HUM.getGroupRecordId(wrap, '');
          try{  CreateCaseRequest_H_HUM.getmemberRecordId(wrap, '');
        }catch(exception ex){
			HUMExceptionHelper.logErrors(ex,'CreateCaaserequest1_T_hum','test_CreateInteractionWithCase_ForMember7');
		}
         
           CreateCaseRequest_H_HUM.getProviderAccountMap(InteractingAccount, '');
        
           CreateCaseRequest_H_HUM.getInteractionWrapObj(InteractingAccount, '');
          // CreateCaseRequest_H_HUM.prepareUnknownAccts(new CaseServiceRestInteraction_DTO_HUM(),'');
          CreateCaseRequest_H_HUM.insertConsumerTaxIds(new list<CreateCaseRequest_H_HUM.CreateUnknownMembers_DTO_HUM>());
        try{
         CreateCaseRequest_H_HUM.getProviderRecordId(new list<InteractingAccount_DTO_HUM>(), '');
        }catch(exception ex){
			HUMExceptionHelper.logErrors(ex,'CreateCaaserequest1_T_hum','test_CreateInteractionWithCase_ForMember7');
		}
         
               Test.StopTest();
    }
    static testMethod void test_CreateInteractionWithCase_ForMember8()
    {
                             Test.startTest();
       
         Account ProviderAcc = HUMTestDataHelperTest.getAccount();
            ProviderAcc.Name = 'test account';
            ProviderAcc.Enterprise_ID__c = '123456';
            ProviderAcc.NPI_ID__c = '1234567890';
            ProviderAcc.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Provider').getRecordTypeId();
            insert ProviderAcc;
        HUMConstants__c  objhumc = New HUMConstants__c();
            objhumc.name ='UNKNOWN_MEMBER';
            objhumc.StringValue__c ='Unknown Member';
            insert objhumc ;
        CreateCaseRequest_H_HUM.calltoUpsertGroupAccounts();
         HUMConstants__c  objhumc1 = New HUMConstants__c();
            objhumc1.name ='UNKNOWN_MEMBER_EXTID';
            objhumc1.StringValue__c ='552751837';
            insert objhumc1 ;
         HUMConstants__c  objhumc2 = New HUMConstants__c();
            objhumc2.name ='PIPE_CHARACTER';
            objhumc2.StringValue__c ='552751837';
            insert objhumc2 ;
            system.assertEquals(objhumc.id != null, True);
                HUMSearchLimit__c Humsearch=new HUMSearchLimit__c();
        Humsearch.Search_Limit__c=10;
        Humsearch.name='Member_SearchLimit';
        insert Humsearch;
      //  CreateInteractionResponse_DTO_HUM response1 = CreateInteractionWithCaseRest_D_HUM.createInteraction(owrap);
      //  system.assertNotEquals(response1,NULL);
           List<InteractingAccount_DTO_HUM> wrap = new list<InteractingAccount_DTO_HUM>();
           InteractingAccount_DTO_HUM InteractingAccount=new InteractingAccount_DTO_HUM();
InteractingAccount.AddressLine1='';
        InteractingAccount.AddressLine2='';
        InteractingAccount.City='';
        InteractingAccount.DOB='';
        InteractingAccount.EmailID='';
 InteractingAccount.FirstName='';
       InteractingAccount.HomePhone='';
       InteractingAccount.ID=ProviderAcc.Id;
       InteractingAccount.IDType='GroupID';
           InteractingAccount.PlatformCode='';
        InteractingAccount.MemberIdDependentCode='';
        InteractingAccount.LastName='';
        InteractingAccount.State='';
       InteractingAccount.EntityType='Group';
        InteractingAccount.ZipCode='';
        InteractingAccount.ZipPlus='';
        InteractingAccount.ProviderClassification='';
        InteractingAccount.AccountName='';
           wrap.add(InteractingAccount);
          try{ 
        //CreateCaseRequest_H_HUM.getInteractingAccountDetails_V4(wrap, '');
           }catch(exception ex){
			   HUMExceptionHelper.logErrors(ex,'CreateCaaserequest1_T_hum','test_CreateInteractionWithCase_ForMember8');
		   }
        CreateCaseRequest_H_HUM.getGroupAccountMap(InteractingAccount, '');
           CreateCaseRequest_H_HUM.getGroupRecordId(wrap, '');
           try{CreateCaseRequest_H_HUM.getmemberRecordId(wrap, '');
        }catch(HUMCustomException ex){
			HUMExceptionHelper.logErrors(ex,'CreateCaaserequest1_T_hum','test_CreateInteractionWithCase_ForMember8');
		}
         
           CreateCaseRequest_H_HUM.getProviderAccountMap(InteractingAccount, '');
        
           CreateCaseRequest_H_HUM.getInteractionWrapObj(InteractingAccount, '');
          // CreateCaseRequest_H_HUM.prepareUnknownAccts(new CaseServiceRestInteraction_DTO_HUM(),'');
          CreateCaseRequest_H_HUM.insertConsumerTaxIds(new list<CreateCaseRequest_H_HUM.CreateUnknownMembers_DTO_HUM>());
        try{
         CreateCaseRequest_H_HUM.getProviderRecordId(new list<InteractingAccount_DTO_HUM>(), '');
        }catch(exception ex){
			HUMExceptionHelper.logErrors(ex,'CreateCaaserequest1_T_hum','test_CreateInteractionWithCase_ForMember8');
		}
         
               Test.StopTest();
    }
    static testMethod void test_CreateInteractionWithCase_ForMember9()
    {
                             Test.startTest();   
         Account ProviderAcc1 = HUMTestDataHelperTest.getAccount();
            ProviderAcc1.Name = 'test account';
            ProviderAcc1.Enterprise_ID__c = '123456';
            ProviderAcc1.NPI_ID__c = '1234567890';
            ProviderAcc1.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Provider').getRecordTypeId();
            insert ProviderAcc1;
        HUMConstants__c  objhumc = New HUMConstants__c();
            objhumc.name ='UNKNOWN_MEMBER';
            objhumc.StringValue__c ='Unknown Member';
            insert objhumc ;
        CreateCaseRequest_H_HUM.calltoUpsertGroupAccounts();
            system.assertEquals(objhumc.id != null, True);
         Account ProviderAcc = new account();//HUMTestDataHelperTest.getAccount();
            ProviderAcc.Name = 'test account';
            ProviderAcc.Enterprise_ID__c = '123456';
            ProviderAcc.NPI_ID__c = '1234567890';
            ProviderAcc.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Provider').getRecordTypeId();
            insert ProviderAcc;
                    HUMSearchLimit__c Humsearch=new HUMSearchLimit__c();
        Humsearch.Search_Limit__c=10;
        Humsearch.name='Member_SearchLimit';
        insert Humsearch;
      //  CreateInteractionResponse_DTO_HUM response1 = CreateInteractionWithCaseRest_D_HUM.createInteraction(owrap);
      //  system.assertNotEquals(response1,NULL);
           List<InteractingAccount_DTO_HUM> wrap = new list<InteractingAccount_DTO_HUM>();
           InteractingAccount_DTO_HUM InteractingAccount=new InteractingAccount_DTO_HUM();
InteractingAccount.AddressLine1='';
        InteractingAccount.AddressLine2='';
        InteractingAccount.City='';
        InteractingAccount.DOB='';
        InteractingAccount.EmailID='';
 InteractingAccount.FirstName='';
       InteractingAccount.HomePhone='';
       InteractingAccount.ID=ProviderAcc.Id;
       InteractingAccount.IDType='GroupID';
           InteractingAccount.PlatformCode='';
        InteractingAccount.MemberIdDependentCode='member';
        InteractingAccount.LastName='';
        InteractingAccount.State='';
       InteractingAccount.EntityType='Member';
        InteractingAccount.ZipCode='';
        InteractingAccount.ZipPlus='';
        InteractingAccount.ProviderClassification='Facility';
        InteractingAccount.AccountName='';
           wrap.add(InteractingAccount);
          
          try{ 
        //CreateCaseRequest_H_HUM.getInteractingAccountDetails_V4(wrap, '');
           }catch(exception ex){
			   HUMExceptionHelper.logErrors(ex,'CreateCaaserequest1_T_hum','test_CreateInteractionWithCase_ForMember9');
		   }
        CreateCaseRequest_H_HUM.getGroupAccountMap(InteractingAccount, 'GroupID');
           CreateCaseRequest_H_HUM.getGroupRecordId(wrap, '');
          try{  CreateCaseRequest_H_HUM.getmemberRecordId(wrap, '');
        }catch(exception ex){
			HUMExceptionHelper.logErrors(ex,'CreateCaaserequest1_T_hum','test_CreateInteractionWithCase_ForMember9');
		}
         
           CreateCaseRequest_H_HUM.getProviderAccountMap(InteractingAccount, '');
        
           CreateCaseRequest_H_HUM.getInteractionWrapObj(InteractingAccount, '');
          // CreateCaseRequest_H_HUM.prepareUnknownAccts(new CaseServiceRestInteraction_DTO_HUM(),'');
          CreateCaseRequest_H_HUM.insertConsumerTaxIds(new list<CreateCaseRequest_H_HUM.CreateUnknownMembers_DTO_HUM>());
        try{
         CreateCaseRequest_H_HUM.getProviderRecordId(new list<InteractingAccount_DTO_HUM>(), '');
        }catch(exception ex){
			HUMExceptionHelper.logErrors(ex,'CreateCaaserequest1_T_hum','test_CreateInteractionWithCase_ForMember9');
		}
         
               Test.StopTest();
    }
     static testMethod void test_CreateInteractionWithCase_ForMember10()
    {
                             Test.startTest(); 
        
            HUMConstants__c  objhumc3 = New HUMConstants__c();
            objhumc3.name ='PIPE_CHARACTER';
            objhumc3.StringValue__c ='PIPE_CHARACTER';
            insert objhumc3 ;
             HUMConstants__c  objhumc2 = New HUMConstants__c();
            objhumc2.name ='UNKNOWN_MEMBER_EXTID';
            objhumc2.StringValue__c ='UNKNOWN_MEMBER_EXTID';
            insert objhumc2 ;
         HUMConstants__c  objhumc = New HUMConstants__c();
            objhumc.name ='UNKNOWN_MEMBER';
            objhumc.StringValue__c ='Unknown Member';
            insert objhumc ;
            system.assertEquals(objhumc.id != null, True);
         Account ProviderAcc = new account();//HUMTestDataHelperTest.getAccount();
            ProviderAcc.Name = 'test account';
            ProviderAcc.Enterprise_ID__c = '123456';
            ProviderAcc.NPI_ID__c = '1234567890';
            ProviderAcc.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Provider').getRecordTypeId();
            insert ProviderAcc;
                    HUMSearchLimit__c Humsearch=new HUMSearchLimit__c();
        Humsearch.Search_Limit__c=10;
        Humsearch.name='Member_SearchLimit';
        insert Humsearch;
      //  CreateInteractionResponse_DTO_HUM response1 = CreateInteractionWithCaseRest_D_HUM.createInteraction(owrap);
      //  system.assertNotEquals(response1,NULL);
           List<InteractingAccount_DTO_HUM> wrap = new list<InteractingAccount_DTO_HUM>();
           InteractingAccount_DTO_HUM InteractingAccount=new InteractingAccount_DTO_HUM();
      
InteractingAccount.AddressLine1='';
        InteractingAccount.AddressLine2='';
        InteractingAccount.City='';
        InteractingAccount.DOB='';
        InteractingAccount.EmailID='';
 InteractingAccount.FirstName='';
       InteractingAccount.HomePhone='';
       InteractingAccount.ID='';
       InteractingAccount.IDType='UnknownProvider';
           InteractingAccount.PlatformCode='';
        InteractingAccount.MemberIdDependentCode='member';
        InteractingAccount.LastName='';
        InteractingAccount.State='';
       InteractingAccount.EntityType='Member';
        InteractingAccount.ZipCode='';
        InteractingAccount.ZipPlus='';
        InteractingAccount.ProviderClassification='Facility';
        InteractingAccount.AccountName='';
           wrap.add(InteractingAccount);
        try{
           CreateCaseRequest_H_HUM.getProviderAccountMap(InteractingAccount, 'test account');
        }catch(Exception ex){
			HUMExceptionHelper.logErrors(ex,'CreateCaaserequest1_T_hum','test_CreateInteractionWithCase_ForMember10');
		}
               Test.StopTest();
    }
    static testMethod void test_CreateInteractionWithCase_ForMember11()
    {
                             Test.startTest();   
         HUMConstants__c  objhumc = New HUMConstants__c();
            objhumc.name ='UNKNOWN_MEMBER';
            objhumc.StringValue__c ='Unknown Member';
            insert objhumc ;
            system.assertEquals(objhumc.id != null, True);
         Account ProviderAcc = HUMTestDataHelperTest.getAccount();
            ProviderAcc.Name = 'test account';
            ProviderAcc.Enterprise_ID__c = '123456';
            ProviderAcc.NPI_ID__c = '1234567890';
            ProviderAcc.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Provider').getRecordTypeId();
            insert ProviderAcc;
                   HUMSearchLimit__c Humsearch=new HUMSearchLimit__c();
        Humsearch.Search_Limit__c=10;
        Humsearch.name='Member_SearchLimit';
        insert Humsearch;
      //  CreateInteractionResponse_DTO_HUM response1 = CreateInteractionWithCaseRest_D_HUM.createInteraction(owrap);
      //  system.assertNotEquals(response1,NULL);
           List<InteractingAccount_DTO_HUM> wrap = new list<InteractingAccount_DTO_HUM>();
           InteractingAccount_DTO_HUM InteractingAccount=new InteractingAccount_DTO_HUM();
InteractingAccount.AddressLine1='';
        InteractingAccount.AddressLine2='';
        InteractingAccount.City='';
        InteractingAccount.DOB='';
        InteractingAccount.EmailID='';
 InteractingAccount.FirstName='';
       InteractingAccount.HomePhone='';
       InteractingAccount.ID=ProviderAcc.Id;
       InteractingAccount.IDType='Unknown Member';
           InteractingAccount.PlatformCode='';
        InteractingAccount.MemberIdDependentCode='';
        InteractingAccount.LastName='';
        InteractingAccount.State='';
       InteractingAccount.EntityType='Provider';
        InteractingAccount.ZipCode='';
        InteractingAccount.ZipPlus='';
        InteractingAccount.ProviderClassification='Facility';
        InteractingAccount.AccountName='';
           wrap.add(InteractingAccount);
          try{
    //CreateCaseRequest_H_HUM.getInteractingAccountDetails_V4(wrap, '');
           }catch(exception ex){
			   HUMExceptionHelper.logErrors(ex,'CreateCaaserequest1_T_hum','test_CreateInteractionWithCase_ForMember11');
		   }
        CreateCaseRequest_H_HUM.getGroupRecordId(new List<InteractingAccount_DTO_HUM>(),'test');
      
               Test.StopTest();
    }
    static testMethod void test_CreateInteractionWithCase_ForMember12()
    {
                             Test.startTest();   
         HUMConstants__c  objhumc = New HUMConstants__c();
            objhumc.name ='UNKNOWN_MEMBER';
            objhumc.StringValue__c ='Unknown Member';
            insert objhumc ;
            system.assertEquals(objhumc.id != null, True);
         Account ProviderAcc = HUMTestDataHelperTest.getAccount();
            ProviderAcc.Name = 'test account';
            ProviderAcc.Enterprise_ID__c = '123456';
            ProviderAcc.NPI_ID__c = '1234567890';
            ProviderAcc.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Provider').getRecordTypeId();
            insert ProviderAcc;
                   HUMSearchLimit__c Humsearch=new HUMSearchLimit__c();
        Humsearch.Search_Limit__c=10;
        Humsearch.name='Member_SearchLimit';
        insert Humsearch;
      //  CreateInteractionResponse_DTO_HUM response1 = CreateInteractionWithCaseRest_D_HUM.createInteraction(owrap);
      //  system.assertNotEquals(response1,NULL);
           List<InteractingAccount_DTO_HUM> wrap = new list<InteractingAccount_DTO_HUM>();
           InteractingAccount_DTO_HUM InteractingAccount=new InteractingAccount_DTO_HUM();
InteractingAccount.AddressLine1='';
        InteractingAccount.AddressLine2='';
        InteractingAccount.City='';
        InteractingAccount.DOB='';
        InteractingAccount.EmailID='';
 InteractingAccount.FirstName='';
       InteractingAccount.HomePhone='';
       InteractingAccount.ID='';
       InteractingAccount.IDType='UnknownProvider';
           InteractingAccount.PlatformCode='';
        InteractingAccount.MemberIdDependentCode='';
        InteractingAccount.LastName='';
        InteractingAccount.State='';
       InteractingAccount.EntityType='Member';
        InteractingAccount.ZipCode='';
        InteractingAccount.ZipPlus='';
        InteractingAccount.ProviderClassification='Facility';
        InteractingAccount.AccountName='';
           wrap.add(InteractingAccount);
          try{ 
    //CreateCaseRequest_H_HUM.getInteractingAccountDetails_V4(wrap, '');
           }catch(exception ex){
			   HUMExceptionHelper.logErrors(ex,'CreateCaaserequest1_T_hum','test_CreateInteractionWithCase_ForMember12');
		   }
        
               Test.StopTest();
    }
     static testMethod void test_CreateInteractionWithCase_ForMember13()
    {
                             Test.startTest();   
         HUMConstants__c  objhumc = New HUMConstants__c();
            objhumc.name ='UNKNOWN_MEMBER';
            objhumc.StringValue__c ='Unknown Member';
            insert objhumc ;
            system.assertEquals(objhumc.id != null, True);
         Account ProviderAcc = HUMTestDataHelperTest.getAccount();
            ProviderAcc.Name = 'test account';
            ProviderAcc.Enterprise_ID__c = '123456';
            ProviderAcc.NPI_ID__c = '1234567890';
            ProviderAcc.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Provider').getRecordTypeId();
            insert ProviderAcc;
                   HUMSearchLimit__c Humsearch=new HUMSearchLimit__c();
        Humsearch.Search_Limit__c=10;
        Humsearch.name='Member_SearchLimit';
        insert Humsearch;
      //  CreateInteractionResponse_DTO_HUM response1 = CreateInteractionWithCaseRest_D_HUM.createInteraction(owrap);
      //  system.assertNotEquals(response1,NULL);
           List<InteractingAccount_DTO_HUM> wrap = new list<InteractingAccount_DTO_HUM>();
           InteractingAccount_DTO_HUM InteractingAccount=new InteractingAccount_DTO_HUM();
InteractingAccount.AddressLine1='';
        InteractingAccount.AddressLine2='';
        InteractingAccount.City='';
        InteractingAccount.DOB='';
        InteractingAccount.EmailID='';
 InteractingAccount.FirstName='';
       InteractingAccount.HomePhone='';
       InteractingAccount.ID='';
       InteractingAccount.IDType='TAXID';
           InteractingAccount.PlatformCode='';
        InteractingAccount.MemberIdDependentCode='';
        InteractingAccount.LastName='';
        InteractingAccount.State='';
       InteractingAccount.EntityType='Member';
        InteractingAccount.ZipCode='';
        InteractingAccount.ZipPlus='';
        InteractingAccount.ProviderClassification='Facility';
        InteractingAccount.AccountName='';
           wrap.add(InteractingAccount);
          CreateCaseRequest_H_HUM.getInteractionWrapObj(InteractingAccount,'');
        InteractingAccount.IDType='NPIID';
        CreateCaseRequest_H_HUM.getInteractionWrapObj(InteractingAccount,'');
        InteractingAccount.IDType='AGENTID';
        CreateCaseRequest_H_HUM.getInteractionWrapObj(InteractingAccount,'');
        InteractingAccount.IDType='GroupID';
        CreateCaseRequest_H_HUM.getInteractionWrapObj(InteractingAccount,'');
               Test.StopTest();
    }

    static testMethod void test_CreateInteractionWithCase_ForMember14()
    {
        Test.startTest();
         Account ProviderAcc1 = HUMTestDataHelperTest.getAccount();
            ProviderAcc1.Name = 'test account';
        ProviderAcc1.Enterprise_ID__c = '123456';
        ProviderAcc1.NPI_ID__c = '1234567890';
        ProviderAcc1.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Provider').getRecordTypeId();
        insert ProviderAcc1;
        HUMConstants__c  objhumc = New HUMConstants__c();
        objhumc.name ='UNKNOWN_MEMBER';
        objhumc.StringValue__c ='Unknown Member';
        insert objhumc ;
        CreateCaseRequest_H_HUM.calltoUpsertGroupAccounts();
        system.assertEquals(objhumc.id != null, True);
        Account ProviderAcc = new account();//HUMTestDataHelperTest.getAccount();
        ProviderAcc.Name = 'test account';
        ProviderAcc.Enterprise_ID__c = '123456';
        ProviderAcc.NPI_ID__c = '1234567890';
        ProviderAcc.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Provider').getRecordTypeId();
        insert ProviderAcc;
        HUMSearchLimit__c Humsearch=new HUMSearchLimit__c();
        Humsearch.Search_Limit__c=10;
        Humsearch.name='Member_SearchLimit';
        insert Humsearch;
        //  CreateInteractionResponse_DTO_HUM response1 = CreateInteractionWithCaseRest_D_HUM.createInteraction(owrap);
        //  system.assertNotEquals(response1,NULL);
        List<InteractingAccount_DTO_HUM> wrap = new list<InteractingAccount_DTO_HUM>();
        InteractingAccount_DTO_HUM InteractingAccount=new InteractingAccount_DTO_HUM();
        InteractingAccount.AddressLine1='';
        InteractingAccount.AddressLine2='';
        InteractingAccount.City='';
        InteractingAccount.DOB='';
        InteractingAccount.EmailID='';
        InteractingAccount.FirstName='';
        InteractingAccount.HomePhone='';
        InteractingAccount.ID=ProviderAcc.Id;
        InteractingAccount.IDType='MemberID';
        InteractingAccount.PlatformCode='';
        InteractingAccount.MemberIdDependentCode='';
        InteractingAccount.LastName='';
        InteractingAccount.State='';
        InteractingAccount.EntityType='Member';
        InteractingAccount.ZipCode='';
        InteractingAccount.ZipPlus='';
        InteractingAccount.ProviderClassification='Facility';
        InteractingAccount.AccountName='';
        wrap.add(InteractingAccount);
        try{
            CreateCaseRequest_H_HUM.getInteractingAccountDetails_V4(InteractingAccount, 'Interacting_About');
        }catch(exception ex){
            HUMExceptionHelper.logErrors(ex,'CreateCaaserequest1_T_hum','test_CreateInteractionWithCase_ForMember9');
        }
        
        Test.StopTest();
    }
    
     static testMethod void test_CreateInteractionWithCase_ForGroup()
    {
        Test.startTest();
         Account ProviderAcc1 = HUMTestDataHelperTest.getAccount();
            ProviderAcc1.Name = 'test account';
        ProviderAcc1.Enterprise_ID__c = '123456';
        ProviderAcc1.NPI_ID__c = '1234567890';
        ProviderAcc1.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Provider').getRecordTypeId();
        insert ProviderAcc1;
        HUMConstants__c  objhumc = New HUMConstants__c();
        objhumc.name ='UNKNOWN_MEMBER';
        objhumc.StringValue__c ='Unknown Member';
        insert objhumc ;
        CreateCaseRequest_H_HUM.calltoUpsertGroupAccounts();
        system.assertEquals(objhumc.id != null, True);
        Account ProviderAcc = new account();//HUMTestDataHelperTest.getAccount();
        ProviderAcc.Name = 'test account';
        ProviderAcc.Enterprise_ID__c = '123456';
        ProviderAcc.NPI_ID__c = '1234567890';
        ProviderAcc.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Provider').getRecordTypeId();
        insert ProviderAcc;
        HUMSearchLimit__c Humsearch=new HUMSearchLimit__c();
        Humsearch.Search_Limit__c=10;
        Humsearch.name='Member_SearchLimit';
        insert Humsearch;
        //  CreateInteractionResponse_DTO_HUM response1 = CreateInteractionWithCaseRest_D_HUM.createInteraction(owrap);
        //  system.assertNotEquals(response1,NULL);
        List<InteractingAccount_DTO_HUM> wrap = new list<InteractingAccount_DTO_HUM>();
        InteractingAccount_DTO_HUM InteractingAccount=new InteractingAccount_DTO_HUM();
        InteractingAccount.AddressLine1='';
        InteractingAccount.AddressLine2='';
        InteractingAccount.City='';
        InteractingAccount.DOB='';
        InteractingAccount.EmailID='';
        InteractingAccount.FirstName='';
        InteractingAccount.HomePhone='';
        InteractingAccount.ID=ProviderAcc.Id;
        InteractingAccount.IDType='GroupID';
        InteractingAccount.PlatformCode='';
        InteractingAccount.MemberIdDependentCode='member';
        InteractingAccount.LastName='';
        InteractingAccount.State='';
        InteractingAccount.EntityType='Group';
        InteractingAccount.ZipCode='';
        InteractingAccount.ZipPlus='';
        InteractingAccount.ProviderClassification='Facility';
        InteractingAccount.AccountName='';
        wrap.add(InteractingAccount);
        
        try{ 
            //CreateCaseRequest_H_HUM.getInteractingAccountDetails_V4(wrap, '');
        }catch(exception ex){
            HUMExceptionHelper.logErrors(ex,'CreateCaaserequest1_T_hum','test_CreateInteractionWithCase_ForMember9');
        }
        CreateCaseRequest_H_HUM.getGroupAccountMap(InteractingAccount, 'GroupID');
        CreateCaseRequest_H_HUM.getGroupRecordId(wrap, '');
        try{  CreateCaseRequest_H_HUM.getmemberRecordId(wrap, '');
           }catch(exception ex){
               HUMExceptionHelper.logErrors(ex,'CreateCaaserequest1_T_hum','test_CreateInteractionWithCase_ForMember9');
           }
        
        CreateCaseRequest_H_HUM.getProviderAccountMap(InteractingAccount, '');
        
        CreateCaseRequest_H_HUM.getInteractionWrapObj(InteractingAccount, '');
        // CreateCaseRequest_H_HUM.prepareUnknownAccts(new CaseServiceRestInteraction_DTO_HUM(),'');
        CreateCaseRequest_H_HUM.insertConsumerTaxIds(new list<CreateCaseRequest_H_HUM.CreateUnknownMembers_DTO_HUM>());
        try{
            CreateCaseRequest_H_HUM.getProviderRecordId(new list<InteractingAccount_DTO_HUM>(), '');
            CreateCaseRequest_H_HUM.getInteractingAccountDetails_V4(InteractingAccount, 'Interacting_About');
        }catch(exception ex){
            HUMExceptionHelper.logErrors(ex,'CreateCaaserequest1_T_hum','test_CreateInteractionWithCase_ForMember9');
        }
        
        Test.StopTest();
    }

     static testMethod void test_CreateInteractionWithCase_ForGroup1()
    {
        Test.startTest();
         Account ProviderAcc1 = HUMTestDataHelperTest.getAccount();
            ProviderAcc1.Name = 'test account';
        ProviderAcc1.Enterprise_ID__c = '123456';
        ProviderAcc1.NPI_ID__c = '1234567890';
        ProviderAcc1.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();
        insert ProviderAcc1;
        
        HUMConstants__c  objhumc = New HUMConstants__c();
        objhumc.name ='UNKNOWN_MEMBER';
        objhumc.StringValue__c ='Unknown Member';
        insert objhumc ;
        CreateCaseRequest_H_HUM.calltoUpsertGroupAccounts();
        system.assertEquals(objhumc.id != null, True);
        Account ProviderAcc = new account();//HUMTestDataHelperTest.getAccount();
        ProviderAcc.Name = 'test account';
        ProviderAcc.Enterprise_ID__c = '123456';
        ProviderAcc.NPI_ID__c = '1234567890';
        ProviderAcc.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();
        ProviderAcc.ETL_Record_Deleted__c = false;
        providerAcc.Group_Number__c = '123456789';
        providerAcc.Source_Platform_Code__c = 'LV';
        insert ProviderAcc;
        
        HUMSearchLimit__c Humsearch=new HUMSearchLimit__c();
        Humsearch.Search_Limit__c=10;
        Humsearch.name='Member_SearchLimit';
        insert Humsearch;
        //  CreateInteractionResponse_DTO_HUM response1 = CreateInteractionWithCaseRest_D_HUM.createInteraction(owrap);
        //  system.assertNotEquals(response1,NULL);
        List<InteractingAccount_DTO_HUM> wrap = new list<InteractingAccount_DTO_HUM>();
        InteractingAccount_DTO_HUM InteractingAccount=new InteractingAccount_DTO_HUM();
        InteractingAccount.AddressLine1='';
        InteractingAccount.AddressLine2='';
        InteractingAccount.City='';
        InteractingAccount.DOB='';
        InteractingAccount.EmailID='';
        InteractingAccount.FirstName='';
        InteractingAccount.HomePhone='';
        InteractingAccount.ID=ProviderAcc.Group_Number__c;
        InteractingAccount.IDType='GroupID';
        InteractingAccount.PlatformCode='';
        InteractingAccount.MemberIdDependentCode='member';
        InteractingAccount.LastName='';
        InteractingAccount.State='';
        InteractingAccount.EntityType='Group';
        InteractingAccount.ZipCode='';
        InteractingAccount.ZipPlus='';
        InteractingAccount.ProviderClassification='Facility';
        InteractingAccount.AccountName='';
        wrap.add(InteractingAccount);
        
        try{ 
            //CreateCaseRequest_H_HUM.getInteractingAccountDetails_V4(wrap, '');
        }catch(exception ex){
            HUMExceptionHelper.logErrors(ex,'CreateCaaserequest1_T_hum','test_CreateInteractionWithCase_ForMember9');
        }
        CreateCaseRequest_H_HUM.getGroupAccountMap(InteractingAccount, 'GroupID');
        CreateCaseRequest_H_HUM.getGroupRecordId(wrap, '');
        try{  CreateCaseRequest_H_HUM.getmemberRecordId(wrap, '');
           }catch(exception ex){
               HUMExceptionHelper.logErrors(ex,'CreateCaaserequest1_T_hum','test_CreateInteractionWithCase_ForMember9');
           }
        
        CreateCaseRequest_H_HUM.getProviderAccountMap(InteractingAccount, '');
        
        CreateCaseRequest_H_HUM.getInteractionWrapObj(InteractingAccount, '');
        // CreateCaseRequest_H_HUM.prepareUnknownAccts(new CaseServiceRestInteraction_DTO_HUM(),'');
        CreateCaseRequest_H_HUM.insertConsumerTaxIds(new list<CreateCaseRequest_H_HUM.CreateUnknownMembers_DTO_HUM>());
        try{
            CreateCaseRequest_H_HUM.getProviderRecordId(new list<InteractingAccount_DTO_HUM>(), '');
            CreateCaseRequest_H_HUM.getInteractingAccountDetails_V4(InteractingAccount, 'Interacting_About');
        }catch(exception ex){
            HUMExceptionHelper.logErrors(ex,'CreateCaaserequest1_T_hum','test_CreateInteractionWithCase_ForMember9');
        }
        
        Test.StopTest();
    }
        
    //new testcases
    static testMethod void test_CreateInteractionWithCase_ForProvider()
    {
        Test.startTest();
      Account ProviderAcc1 = HUMTestDataHelperTest.getAccount();
            ProviderAcc1.Name = 'test account';
        ProviderAcc1.Enterprise_ID__c = '123456';
        ProviderAcc1.NPI_ID__c = '1234567890';
        ProviderAcc1.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Provider').getRecordTypeId();
        insert ProviderAcc1;
        HUMConstants__c  objhumc = New HUMConstants__c();
        objhumc.name ='UNKNOWN_MEMBER';
        objhumc.StringValue__c ='Unknown Member';
        insert objhumc ;
        CreateCaseRequest_H_HUM.calltoUpsertGroupAccounts();
        system.assertEquals(objhumc.id != null, True);
        Account ProviderAcc = new account();//HUMTestDataHelperTest.getAccount();
        ProviderAcc.Name = 'test account';
        ProviderAcc.Enterprise_ID__c = '123456';
        ProviderAcc.NPI_ID__c = '1234567890';
        ProviderAcc.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Provider').getRecordTypeId();
        insert ProviderAcc;
        HUMSearchLimit__c Humsearch=new HUMSearchLimit__c();
        Humsearch.Search_Limit__c=10;
        Humsearch.name='Member_SearchLimit';
        insert Humsearch;
        //  CreateInteractionResponse_DTO_HUM response1 = CreateInteractionWithCaseRest_D_HUM.createInteraction(owrap);
        //  system.assertNotEquals(response1,NULL);
        List<InteractingAccount_DTO_HUM> wrap = new list<InteractingAccount_DTO_HUM>();
        InteractingAccount_DTO_HUM InteractingAccount=new InteractingAccount_DTO_HUM();
        InteractingAccount.AddressLine1='';
        InteractingAccount.AddressLine2='';
        InteractingAccount.City='';
        InteractingAccount.DOB='';
        InteractingAccount.EmailID='';
        InteractingAccount.FirstName='';
        InteractingAccount.HomePhone='';
        InteractingAccount.ID='1234567890';
        InteractingAccount.IDType='NPIID';
        InteractingAccount.PlatformCode='';
        InteractingAccount.MemberIdDependentCode='';
        InteractingAccount.LastName='';
        InteractingAccount.State='';
        InteractingAccount.EntityType='Provider';
        InteractingAccount.ZipCode='';
        InteractingAccount.ZipPlus='';
        InteractingAccount.ProviderClassification='';
        InteractingAccount.AccountName='';
        wrap.add(InteractingAccount);
        
        try{ 
            CreateCaseRequest_H_HUM.getInteractingAccountDetails_V4(InteractingAccount, 'Interacting_With');
        }catch(exception ex){
            HUMExceptionHelper.logErrors(ex,'CreateCaaserequest1_T_hum','test_CreateInteractionWithCase_ForMember9');
        }
        CreateCaseRequest_H_HUM.getGroupAccountMap(InteractingAccount, 'Interacting_With');
        CreateCaseRequest_H_HUM.getGroupAccountMap(InteractingAccount, 'Interacting_With');
        CreateCaseRequest_H_HUM.getGroupRecordId(wrap, '');
        try{  CreateCaseRequest_H_HUM.getmemberRecordId(wrap, '');
           }catch(exception ex){
               HUMExceptionHelper.logErrors(ex,'CreateCaaserequest1_T_hum','test_CreateInteractionWithCase_ForMember9');
           }
        
        CreateCaseRequest_H_HUM.getProviderAccountMap(InteractingAccount, '');
        CreateCaseRequest_H_HUM.getInteractionWrapObj(InteractingAccount, '');
        CreateCaseRequest_H_HUM.insertConsumerTaxIds(new list<CreateCaseRequest_H_HUM.CreateUnknownMembers_DTO_HUM>());
        try{
            //Added
            CreateCaseRequest_H_HUM.getProviderRecordId_V4(InteractingAccount, 'Interacting_With');
            //Interacting_About
        }catch(exception ex){
            HUMExceptionHelper.logErrors(ex,'CreateCaaserequest1_T_hum','test_CreateInteractionWithCase_ForProvider');
        }
        
        Test.StopTest();
    }

     //Tax_Id
    static testMethod void test_CreateInteractionWithCase_ForProvider1()
    {
        Test.startTest();
        Account ProviderAcc1 = HUMTestDataHelperTest.getAccount();
        ProviderAcc1.Name = 'test account';
        ProviderAcc1.Enterprise_ID__c = '123456';
        ProviderAcc1.NPI_ID__c = '1234567890';
        ProviderAcc1.Account_External_ID__c ='1234567';
        ProviderAcc1.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Provider').getRecordTypeId();
        insert ProviderAcc1;
        HUMConstants__c  objhumc = New HUMConstants__c();
        objhumc.name ='UNKNOWN_MEMBER';
        objhumc.StringValue__c ='Unknown Member';
        insert objhumc ;
        CreateCaseRequest_H_HUM.calltoUpsertGroupAccounts();
        system.assertEquals(objhumc.id != null, True);
        Account ProviderAcc = new account();//HUMTestDataHelperTest.getAccount();
        ProviderAcc.Name = 'test account';
        ProviderAcc.Enterprise_ID__c = '123456';
        ProviderAcc.NPI_ID__c = '1234567890';
        ProviderAcc.Account_External_ID__c = '12345678';
        ProviderAcc.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Provider').getRecordTypeId();
        insert ProviderAcc;
        
        Map<String,Account> accountMapToUpsert = new  Map<String,Account>();
        accountMapToUpsert.put('Interacting_With',ProviderAcc);
        accountMapToUpsert.put('Account_External_ID__c',ProviderAcc1);
        accountMapToUpsert.put('Interacting_About',ProviderAcc);
        accountMapToUpsert.put('Account_External_ID__c',ProviderAcc);
            
        HUMSearchLimit__c Humsearch=new HUMSearchLimit__c();
        Humsearch.Search_Limit__c=10;
        Humsearch.name='Member_SearchLimit';
        insert Humsearch;
		
		  ConsumerID__c oConsumerId = new ConsumerID__c();
            oConsumerId.Account__c = ProviderAcc1.ID;
            oConsumerId.Consumer_External_ID__c = 'UKM|00|SFDC|HumanaId|a0uL00000058F50IAE';
            oConsumerId.Consumer_ID__c = '12345';
            oConsumerId.ID_Type__c = 'TaxID';
            insert oConsumerId;
            System.AssertNotEquals(oConsumerId, NULL);
			        
           ConsumerID__c consumeracct = [SELECT ID,Account__c,Consumer_ID__c,ID_Type__c FROM ConsumerID__c WHERE Consumer_ID__c = '12345' LIMIT 1];
            Id [] fixedSearchResults2= new Id[1];
            fixedSearchResults2[0] = consumeracct.id;
            Test.setFixedSearchResults(fixedSearchResults2);  
        
        HUM_Webservice_Callout__c oHumEPSSservice = new HUM_Webservice_Callout__c();
        oHumEPSSservice.Name = 'EPSServiceREST_V1';
        oHumEPSSservice.Request_Type__c ='POST';
        oHumEPSSservice.End_Point_URL__c = 'https://crmservicews.humana.com/EPSServiceREST_V17.12/EnterpriseProviderSearch';
        oHumEPSSservice.Active__c = true;
        oHumEPSSservice.Certificate_Name__c = 'Humana_GuidanceCare';
        oHumEPSSservice.Content_Type__c = 'application/json';
        oHumEPSSservice.Timeout__c = '12000';
        oHumEPSSservice.Enable_Service_Tester__c = true;
        insert oHumEPSSservice;
        
        List<InteractingAccount_DTO_HUM> wrap = new list<InteractingAccount_DTO_HUM>();
        InteractingAccount_DTO_HUM InteractingAccount=new InteractingAccount_DTO_HUM();
        InteractingAccount.AddressLine1='';
        InteractingAccount.AddressLine2='';
        InteractingAccount.City='';
        InteractingAccount.DOB='';
        InteractingAccount.EmailID='';
        InteractingAccount.FirstName='';
        InteractingAccount.HomePhone='';
        InteractingAccount.ID='12345';
        InteractingAccount.IDType='TaxID';
        InteractingAccount.PlatformCode='';
        InteractingAccount.MemberIdDependentCode='00';
        InteractingAccount.LastName='';
        InteractingAccount.State='';
        InteractingAccount.EntityType='Provider';
        InteractingAccount.ZipCode='';
        InteractingAccount.ZipPlus='';
        InteractingAccount.ProviderClassification='F';
        InteractingAccount.AccountName='';
        wrap.add(InteractingAccount);
        
       // CreateInteractionResponse_DTO_HUM response1 = CreateInteractionWithCaseRest_D_HUM.createInteraction(owrap);
       // system.assertNotEquals(response1,NULL);
        
        CaseServiceProvider_v4_H_HUM.prepareMapWithExtrnlId('1234567890','12345','UKM|00|SFDC|HumanaId|a0uL00000058F50IAE','Interacting_With');
        CaseServiceProvider_v4_H_HUM.performDMLOperationsQueuable(accountMapToUpsert);
            
        try{ 
            CreateCaseRequest_H_HUM.getInteractingAccountDetails_V4(InteractingAccount, 'Interacting_With');
        }catch(exception ex){
            HUMExceptionHelper.logErrors(ex,'CreateCaaserequest1_T_hum','test_CreateInteractionWithCase_ForMember9');
        }
        CreateCaseRequest_H_HUM.getGroupAccountMap(InteractingAccount, 'Interacting_With');
        CreateCaseRequest_H_HUM.getGroupAccountMap(InteractingAccount, 'Interacting_With');
        CreateCaseRequest_H_HUM.getGroupRecordId(wrap, '');
        try{  CreateCaseRequest_H_HUM.getmemberRecordId(wrap, '');
           }catch(exception ex){
               HUMExceptionHelper.logErrors(ex,'CreateCaaserequest1_T_hum','test_CreateInteractionWithCase_ForMember9');
           }
        
        CreateCaseRequest_H_HUM.getProviderAccountMap(InteractingAccount, '');
        CreateCaseRequest_H_HUM.getInteractionWrapObj(InteractingAccount, '');
        CreateCaseRequest_H_HUM.insertConsumerTaxIds(new list<CreateCaseRequest_H_HUM.CreateUnknownMembers_DTO_HUM>());
        try{
            //Added
            CreateCaseRequest_H_HUM.getProviderRecordId_V4(InteractingAccount, 'Interacting_With');
            //Interacting_About
        }catch(exception ex){
            HUMExceptionHelper.logErrors(ex,'CreateCaaserequest1_T_hum','test_CreateInteractionWithCase_ForProvider');
        }
        Test.StopTest();
    }
    
       static testMethod void test_CreateInteractionWithCase_ForAgent()
    {
        Test.startTest();
       	Account ProviderAcc1 = HUMTestDataHelperTest.getAccount();
        ProviderAcc1.Name = 'test account';
        ProviderAcc1.Enterprise_ID__c = '123456';
        ProviderAcc1.NPI_ID__c = '1234567890';
        ProviderAcc1.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Provider').getRecordTypeId();
        insert ProviderAcc1;
        HUMConstants__c  objhumc = New HUMConstants__c();
        objhumc.name ='UNKNOWN_MEMBER';
        objhumc.StringValue__c ='Unknown Member';
        insert objhumc ;
        CreateCaseRequest_H_HUM.calltoUpsertGroupAccounts();
        system.assertEquals(objhumc.id != null, True);
        Account AgentAcc = new account();//HUMTestDataHelperTest.getAccount();
        AgentAcc.Name = 'test account';
        AgentAcc.Agent_ID__c = '123456';
        AgentAcc.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Agent/Broker').getRecordTypeId();
        insert AgentAcc;
        HUMSearchLimit__c Humsearch=new HUMSearchLimit__c();
        Humsearch.Search_Limit__c=10;
        Humsearch.name='Member_SearchLimit';
        insert Humsearch;

        HUM_Webservice_Callout__c objWebserviceCallout = new HUM_Webservice_Callout__c();
        objWebserviceCallout.Name ='AgencySearchService';
        objWebserviceCallout.End_Point_URL__c ='https://crmservicews.humana.com/Producer/Entity/Producer.svc';
        objWebserviceCallout.Certificate_Name__c = 'Humana_GuidanceCare';
        objWebserviceCallout.Timeout__c = '12000';
        insert objWebserviceCallout;

        //  CreateInteractionResponse_DTO_HUM response1 = CreateInteractionWithCaseRest_D_HUM.createInteraction(owrap);
        //  system.assertNotEquals(response1,NULL);
        List<InteractingAccount_DTO_HUM> wrap = new list<InteractingAccount_DTO_HUM>();
        InteractingAccount_DTO_HUM InteractingAccount=new InteractingAccount_DTO_HUM();
        InteractingAccount.AddressLine1='';
        InteractingAccount.AddressLine2='';
        InteractingAccount.City='';
        InteractingAccount.DOB='';
        InteractingAccount.EmailID='';
        InteractingAccount.FirstName='';
        InteractingAccount.HomePhone='';
        InteractingAccount.ID='123456';
        InteractingAccount.IDType='AgentID';
        InteractingAccount.PlatformCode='';
        InteractingAccount.MemberIdDependentCode='';
        InteractingAccount.LastName='';
        InteractingAccount.State='';
        InteractingAccount.EntityType='Agent';
        InteractingAccount.ZipCode='';
        InteractingAccount.ZipPlus='';
        InteractingAccount.ProviderClassification='';
        InteractingAccount.AccountName='';
        wrap.add(InteractingAccount);

     InteractingAccount_DTO_HUM InteractingAccount1=new InteractingAccount_DTO_HUM();
        InteractingAccount1.AddressLine1='';
        InteractingAccount1.AddressLine2='';
        InteractingAccount1.City='';
        InteractingAccount1.DOB='';
        InteractingAccount1.EmailID='';
        InteractingAccount1.FirstName='Test';
        InteractingAccount1.HomePhone='';
        InteractingAccount1.ID='';
        InteractingAccount1.IDType='AgentID';
        InteractingAccount1.PlatformCode='';
        InteractingAccount1.MemberIdDependentCode='';
        InteractingAccount1.LastName='method';
        InteractingAccount1.State='';
        InteractingAccount1.EntityType='Agent';
        InteractingAccount1.ZipCode='';
        InteractingAccount1.ZipPlus='';
        InteractingAccount1.ProviderClassification='';
        InteractingAccount1.AccountName='';
       
        HUMConstants__c  objhum = New HUMConstants__c();
            objhum.name ='TENANTID_GEN';
            objhum.StringValue__c ='00';
            insert objhum ;
        
        try{ 
            CreateCaseRequest_H_HUM.getInteractingAccountDetails_V4(InteractingAccount, 'Interacting_With');
            CreateCaseRequest_H_HUM.getInteractingAccountDetails_V4(InteractingAccount, 'Interacting_About');
        }catch(exception ex){
            HUMExceptionHelper.logErrors(ex,'CreateCaaserequest1_T_hum','test_CreateInteractionWithCase_ForMember9');
        }
        CreateCaseRequest_H_HUM.getGroupAccountMap(InteractingAccount, 'GroupID');
        CreateCaseRequest_H_HUM.getGroupRecordId(wrap, '');
        try{  CreateCaseRequest_H_HUM.getmemberRecordId(wrap, '');
           }catch(exception ex){
               HUMExceptionHelper.logErrors(ex,'CreateCaaserequest1_T_hum','test_CreateInteractionWithCase_ForMember9');
           }
        
        CreateCaseRequest_H_HUM.getProviderAccountMap(InteractingAccount, 'Interacting_With');
		CreateCaseRequest_H_HUM.getProviderAccountMap(InteractingAccount, 'Interacting_With');
        CreateCaseRequest_H_HUM.getInteractionWrapObj(InteractingAccount, '');
        
        CreateCaseRequest_H_HUM.insertConsumerTaxIds(new list<CreateCaseRequest_H_HUM.CreateUnknownMembers_DTO_HUM>());
        try{
            //Added
            CreateCaseRequest_H_HUM.getAgentRecordId_V4(InteractingAccount, 'Interacting_With');
        }catch(exception ex){
            HUMExceptionHelper.logErrors(ex,'CreateCaaserequest1_T_hum','test_CreateInteractionWithCase_ForAgent');
        }
         CreateCaseRequest_H_HUM.getAgentRecordId_V4(InteractingAccount1, 'Interacting_With');
       Test.StopTest();
    }
    
    static testMethod void test_CreateInteractionWithCase_UnknownProvider()
    {
          Test.startTest();
         HUMConstants__c  objhumc = New HUMConstants__c();
            objhumc.name ='UNKNOWN_MEMBER';
            objhumc.StringValue__c ='Unknown Member';
            insert objhumc ;
         HUMConstants__c  objhumc2 = New HUMConstants__c();
            objhumc2.name ='PIPE_CHARACTER';
            objhumc2.StringValue__c ='PIPE_CHARACTER';
            insert objhumc2 ;
            system.assertEquals(objhumc.id != null, True);
         Account ProviderAcc = HUMTestDataHelperTest.getAccount();
            ProviderAcc.Name = 'test account';
            ProviderAcc.Enterprise_ID__c = '123456';
            ProviderAcc.NPI_ID__c = '1234567890';
            ProviderAcc.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Provider').getRecordTypeId();
            insert ProviderAcc;
                    HUMSearchLimit__c Humsearch=new HUMSearchLimit__c();
        Humsearch.Search_Limit__c=10;
        Humsearch.name='Member_SearchLimit';
        insert Humsearch;
                        HUMConstants__c  objhumc1 = New HUMConstants__c();
                objhumc1.name ='UNKNOWN_MEMBER_EXTID';
                objhumc1.StringValue__c ='UKM|00|SFDC|';
                insert objhumc1 ;
      //  CreateInteractionResponse_DTO_HUM response1 = CreateInteractionWithCaseRest_D_HUM.createInteraction(owrap);
      //  system.assertNotEquals(response1,NULL);
           List<InteractingAccount_DTO_HUM> wrap = new list<InteractingAccount_DTO_HUM>();
           InteractingAccount_DTO_HUM InteractingAccount=new InteractingAccount_DTO_HUM();
InteractingAccount.AddressLine1='';
        InteractingAccount.AddressLine2='';
        InteractingAccount.City='';
        InteractingAccount.DOB='';
        InteractingAccount.EmailID='';
 InteractingAccount.FirstName='';
       InteractingAccount.HomePhone='';
       InteractingAccount.ID=ProviderAcc.Id;
       InteractingAccount.IDType='UnknownProvider';
           InteractingAccount.PlatformCode='';
        InteractingAccount.MemberIdDependentCode='';
        InteractingAccount.LastName='';
        InteractingAccount.State='';
       InteractingAccount.EntityType='Provider';
        InteractingAccount.ZipCode='';
        InteractingAccount.ZipPlus='';
        InteractingAccount.ProviderClassification='Facility';
        InteractingAccount.AccountName='';
           wrap.add(InteractingAccount);
          try{ 
        //CreateCaseRequest_H_HUM.getInteractingAccountDetails_V4(wrap, '');
           }catch(exception ex){
			   HUMExceptionHelper.logErrors(ex,'CreateCaaserequest1_T_hum','test_CreateInteractionWithCase_ForMember4'); 
		   }
        CreateCaseRequest_H_HUM.getGroupAccountMap(InteractingAccount, '');
           CreateCaseRequest_H_HUM.getGroupRecordId(wrap, '');
                    try{
                        CreateCaseRequest_H_HUM.getmemberRecordId(wrap, '');
         }catch(HUMCustomException ex){
			 HUMExceptionHelper.logErrors(ex,'CreateCaaserequest1_T_hum','test_CreateInteractionWithCase_ForMember4');
		 }
         
           CreateCaseRequest_H_HUM.getProviderAccountMap(InteractingAccount, '');
        	CreateCaseRequest_H_HUM.getProviderAccountMap(InteractingAccount, '');
        
           CreateCaseRequest_H_HUM.getInteractionWrapObj(InteractingAccount, '');
          // CreateCaseRequest_H_HUM.prepareUnknownAccts(new CaseServiceRestInteraction_DTO_HUM(),'');
          CreateCaseRequest_H_HUM.insertConsumerTaxIds(new list<CreateCaseRequest_H_HUM.CreateUnknownMembers_DTO_HUM>());
        try{
         CreateCaseRequest_H_HUM.getProviderRecordId(wrap, 'Interacting_With');
        }catch(exception ex){
			HUMExceptionHelper.logErrors(ex,'CreateCaaserequest1_T_hum','test_CreateInteractionWithCase_ForMember4');
		}
         
               Test.StopTest();
    }
    
     static testMethod void test_CreateInteractionWithCase_UnknownMember()
    {
                             Test.startTest(); 
        	 HUMConstants__c  objhumc3 = New HUMConstants__c();
            objhumc3.name ='PIPE_CHARACTER';
            objhumc3.StringValue__c ='PIPE_CHARACTER';
            insert objhumc3 ;
             HUMConstants__c  objhumc2 = New HUMConstants__c();
            objhumc2.name ='UNKNOWN_MEMBER_EXTID';
            objhumc2.StringValue__c ='UNKNOWN_MEMBER_EXTID';
            insert objhumc2 ;
         HUMConstants__c  objhumc = New HUMConstants__c();
            objhumc.name ='UNKNOWN_MEMBER';
            objhumc.StringValue__c ='Unknown Member';
            insert objhumc ;
            system.assertEquals(objhumc.id != null, True);
         Account ProviderAcc = new account();//HUMTestDataHelperTest.getAccount();
            ProviderAcc.Name = 'test account';
            ProviderAcc.Enterprise_ID__c = '123456';
            ProviderAcc.NPI_ID__c = '1234567890';
            ProviderAcc.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Provider').getRecordTypeId();
            insert ProviderAcc;
                    HUMSearchLimit__c Humsearch=new HUMSearchLimit__c();
        Humsearch.Search_Limit__c=10;
        Humsearch.name='Member_SearchLimit';
        insert Humsearch;
      //  CreateInteractionResponse_DTO_HUM response1 = CreateInteractionWithCaseRest_D_HUM.createInteraction(owrap);
      //  system.assertNotEquals(response1,NULL);
           List<InteractingAccount_DTO_HUM> wrap = new list<InteractingAccount_DTO_HUM>();
           InteractingAccount_DTO_HUM InteractingAccount=new InteractingAccount_DTO_HUM();
InteractingAccount.AddressLine1='';
        InteractingAccount.AddressLine2='';
        InteractingAccount.City='';
        InteractingAccount.DOB='';
        InteractingAccount.EmailID='';
 InteractingAccount.FirstName='';
       InteractingAccount.HomePhone='';
       InteractingAccount.ID='12341234';
       InteractingAccount.IDType='UnknownMember';
           InteractingAccount.PlatformCode='';
        InteractingAccount.MemberIdDependentCode='';
        InteractingAccount.LastName='';
        InteractingAccount.State='';
       InteractingAccount.EntityType='Member';
        InteractingAccount.ZipCode='';
        InteractingAccount.ZipPlus='';
        InteractingAccount.ProviderClassification='Facility';
        InteractingAccount.AccountName='';
           wrap.add(InteractingAccount);

        InteractingAccount_DTO_HUM InteractingAccount1=new InteractingAccount_DTO_HUM();
        InteractingAccount1.AddressLine1='';
        InteractingAccount1.AddressLine2='';
        InteractingAccount1.City='';
        InteractingAccount1.DOB='';
        InteractingAccount1.EmailID='';
        InteractingAccount1.FirstName='';
        InteractingAccount1.HomePhone='';
        InteractingAccount1.ID='';
        InteractingAccount1.IDType='UnknownMember';
        InteractingAccount1.PlatformCode='';
        InteractingAccount1.MemberIdDependentCode='';
        InteractingAccount1.LastName='';
        InteractingAccount1.State='';
        InteractingAccount1.EntityType='Member';
        InteractingAccount1.ZipCode='';
        InteractingAccount1.ZipPlus='';
        InteractingAccount1.ProviderClassification='Facility';
        InteractingAccount1.AccountName=''; 
        wrap.add(InteractingAccount1); 
       
          try{ 
        CreateCaseRequest_H_HUM.getInteractingAccountDetails_V4(InteractingAccount, 'Interacting_About');
           }catch(exception ex){
			   HUMExceptionHelper.logErrors(ex,'CreateCaaserequest1_T_hum','test_CreateInteractionWithCase_ForMember6');
		   }
		   try{ 
            CreateCaseRequest_H_HUM.getInteractingAccountDetails_V4(InteractingAccount1, 'Interacting_About');
               }catch(exception ex){
                HUMExceptionHelper.logErrors(ex,'CreateCaaserequest1_T_hum','test_CreateInteractionWithCase_ForMember6');
             }
        CreateCaseRequest_H_HUM.getGroupAccountMap(InteractingAccount, '');
           CreateCaseRequest_H_HUM.getGroupRecordId(wrap, '');
           try{  CreateCaseRequest_H_HUM.getmemberRecordId(wrap, '');
        }catch(HUMCustomException ex){
			HUMExceptionHelper.logErrors(ex,'CreateCaaserequest1_T_hum','test_CreateInteractionWithCase_ForMember6');
		}
         
           CreateCaseRequest_H_HUM.getProviderAccountMap(InteractingAccount, '');
        
           CreateCaseRequest_H_HUM.getInteractionWrapObj(InteractingAccount, '');
          // CreateCaseRequest_H_HUM.prepareUnknownAccts(new CaseServiceRestInteraction_DTO_HUM(),'');
          CreateCaseRequest_H_HUM.insertConsumerTaxIds(new list<CreateCaseRequest_H_HUM.CreateUnknownMembers_DTO_HUM>());
        try{
         CreateCaseRequest_H_HUM.getProviderRecordId(new list<InteractingAccount_DTO_HUM>(), '');
        }catch(exception ex){
			HUMExceptionHelper.logErrors(ex,'CreateCaaserequest1_T_hum','test_CreateInteractionWithCase_ForMember6');
		}
     try{
            CreateCaseRequest_H_HUM.getInteractingAccountDetails_V4(InteractingAccount1, 'Interacting_About');                         
        }catch(exception ex){
            HUMExceptionHelper.logErrors(ex,'CreateCaaserequest1_T_hum','test_CreateInteractionWithCase_ForMember6');
        }         
       Test.StopTest();
    }
	
	static testMethod void test_CreateInteractionWithCase_UnknownMember2()
    {
                             Test.startTest(); 
        	 HUMConstants__c  objhumc3 = New HUMConstants__c();
            objhumc3.name ='PIPE_CHARACTER';
            objhumc3.StringValue__c ='PIPE_CHARACTER';
            insert objhumc3 ;
             HUMConstants__c  objhumc2 = New HUMConstants__c();
            objhumc2.name ='UNKNOWN_MEMBER_EXTID';
            objhumc2.StringValue__c ='UNKNOWN_MEMBER_EXTID';
            insert objhumc2 ;
         HUMConstants__c  objhumc = New HUMConstants__c();
            objhumc.name ='UNKNOWN_MEMBER';
            objhumc.StringValue__c ='Unknown Member';
            insert objhumc ;
            system.assertEquals(objhumc.id != null, True);
         Account ProviderAcc = new account();//HUMTestDataHelperTest.getAccount();
            ProviderAcc.Name = 'test account';
            ProviderAcc.Enterprise_ID__c = '123456';
            ProviderAcc.NPI_ID__c = '1234567890';
            //ProviderAcc.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('UNKNOWN_MEMBER').getRecordTypeId();
            insert ProviderAcc;
                    HUMSearchLimit__c Humsearch=new HUMSearchLimit__c();
        Humsearch.Search_Limit__c=10;
        Humsearch.name='Member_SearchLimit';
        insert Humsearch;
           List<InteractingAccount_DTO_HUM> wrap = new list<InteractingAccount_DTO_HUM>();
          
        InteractingAccount_DTO_HUM InteractingAccount1=new InteractingAccount_DTO_HUM();
        InteractingAccount1.AddressLine1='';
        InteractingAccount1.AddressLine2='';
        InteractingAccount1.City='';
        InteractingAccount1.DOB='';
        InteractingAccount1.EmailID='';
        InteractingAccount1.FirstName='';
        InteractingAccount1.HomePhone='';
        InteractingAccount1.ID='';
        InteractingAccount1.IDType='UnknownMember';
        InteractingAccount1.PlatformCode='';
        InteractingAccount1.MemberIdDependentCode='';
        InteractingAccount1.LastName='';
        InteractingAccount1.State='';
        InteractingAccount1.EntityType='Member';
        InteractingAccount1.ZipCode='';
        InteractingAccount1.ZipPlus='';
        InteractingAccount1.ProviderClassification='Facility';
        InteractingAccount1.AccountName=''; 
        wrap.add(InteractingAccount1); 
       
          
         try{ 
            CreateCaseRequest_H_HUM.getInteractingAccountDetails_V4(InteractingAccount1, 'Interacting_About');
               }catch(exception ex){
                HUMExceptionHelper.logErrors(ex,'CreateCaaserequest1_T_hum','test_CreateInteractionWithCase_ForMember6');
             }
        //CreateCaseRequest_H_HUM.getGroupAccountMap(InteractingAccount, '');
           CreateCaseRequest_H_HUM.getGroupRecordId(wrap, '');
           
          CreateCaseRequest_H_HUM.insertConsumerTaxIds(new list<CreateCaseRequest_H_HUM.CreateUnknownMembers_DTO_HUM>());
        try{
         CreateCaseRequest_H_HUM.getProviderRecordId(new list<InteractingAccount_DTO_HUM>(), '');
        }catch(exception ex){
			HUMExceptionHelper.logErrors(ex,'CreateCaaserequest1_T_hum','test_CreateInteractionWithCase_ForMember6');
		}
     try{
            CreateCaseRequest_H_HUM.getInteractingAccountDetails_V4(InteractingAccount1, 'Interacting_About');                         
        }catch(exception ex){
            HUMExceptionHelper.logErrors(ex,'CreateCaaserequest1_T_hum','test_CreateInteractionWithCase_ForMember6');
        }         
       Test.StopTest();
    }

    static testMethod void test_CreateInteractionWithCase_ForMember23()
    {
                             Test.startTest();   
         HUMConstants__c  objhumc = New HUMConstants__c();
            objhumc.name ='UNKNOWN_MEMBER';
            objhumc.StringValue__c ='Unknown Member';
            insert objhumc ;
            system.assertEquals(objhumc.id != null, True);
         Account ProviderAcc = HUMTestDataHelperTest.getAccount();
            ProviderAcc.Name = 'test account';
            ProviderAcc.Enterprise_ID__c = '123456';
            ProviderAcc.NPI_ID__c = '1234567890';
            ProviderAcc.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Provider').getRecordTypeId();
            insert ProviderAcc;
                   HUMSearchLimit__c Humsearch=new HUMSearchLimit__c();
        Humsearch.Search_Limit__c=10;
        Humsearch.name='Member_SearchLimit';
        insert Humsearch;
        
        List<Account> alist = [select Name, Enterprise_ID__C, NPI_ID__C from Account limit 1];
      //  CreateInteractionResponse_DTO_HUM response1 = CreateInteractionWithCaseRest_D_HUM.createInteraction(owrap);
      //  system.assertNotEquals(response1,NULL);
           List<InteractingAccount_DTO_HUM> wrap = new list<InteractingAccount_DTO_HUM>();
           InteractingAccount_DTO_HUM InteractingAccount=new InteractingAccount_DTO_HUM();
InteractingAccount.AddressLine1='';
        InteractingAccount.AddressLine2='';
        InteractingAccount.City='';
        InteractingAccount.DOB='';
        InteractingAccount.EmailID='';
 InteractingAccount.FirstName='';
       InteractingAccount.HomePhone='';
       InteractingAccount.ID=ProviderAcc.Id;
       InteractingAccount.IDType='UnknownMember';
           InteractingAccount.PlatformCode='';
        InteractingAccount.MemberIdDependentCode='';
        InteractingAccount.LastName='';
        InteractingAccount.State='';
       InteractingAccount.EntityType='Member';
        InteractingAccount.ZipCode='';
        InteractingAccount.ZipPlus='';
        InteractingAccount.ProviderClassification='Facility';
        InteractingAccount.AccountName='';
           wrap.add(InteractingAccount);

           

          try{ 
        //CreateCaseRequest_H_HUM.getInteractingAccountDetails_V4(wrap, '');
           }catch(exception ex){
			   HUMExceptionHelper.logErrors(ex,'CreateCaaserequest1_T_hum','test_CreateInteractionWithCase_ForMember2'); 	
		   }
        CreateCaseRequest_H_HUM.getGroupAccountMap(InteractingAccount, '');
           CreateCaseRequest_H_HUM.getGroupRecordId(wrap, '');
          CreateCaseRequest_H_HUM.getmemberRecordId(wrap, '');
        
         
           CreateCaseRequest_H_HUM.getProviderAccountMap(InteractingAccount, '');

           CreateCaseRequest_H_HUM.accountMapToUpsert.put('UnknownMember', alist);
              
           CreateCaseRequest_H_HUM.getProviderAccountMap(InteractingAccount, 'UnknownMember');

           CreateCaseRequest_H_HUM.groupaccountMapToUpsert.put('group', alist);
        CreateCaseRequest_H_HUM.getGroupAccountMap(InteractingAccount, 'group');

           CreateCaseRequest_H_HUM.getInteractionWrapObj(InteractingAccount, '');
          // CreateCaseRequest_H_HUM.prepareUnknownAccts(New CaseServiceRestInteraction_DTO_HUM(),'');
          CreateCaseRequest_H_HUM.insertConsumerTaxIds(New list<CreateCaseRequest_H_HUM.CreateUnknownMembers_DTO_HUM>());
        try{
         CreateCaseRequest_H_HUM.getProviderRecordId(new list<InteractingAccount_DTO_HUM>(), '');
        }catch(exception ex){
			HUMExceptionHelper.logErrors(ex,'CreateCaaserequest1_T_hum','test_CreateInteractionWithCase_ForMember2'); 	
      }
      try{
         CreateCaseRequest_H_HUM.calltoUpsertGroupAccounts();
        }catch(exception ex){
			HUMExceptionHelper.logErrors(ex,'CreateCaaserequest1_T_hum','test_CreateInteractionWithCase_ForMember2'); 	
      }
      try{
         CreateCaseRequest_H_HUM.unknownAccountMapToUpsert = null;
         CreateCaseRequest_H_HUM.accountMapToUpsert = null;
   
         CreateCaseRequest_H_HUM.calltoUpsertAccounts();
        }catch(exception ex){
			HUMExceptionHelper.logErrors(ex,'CreateCaaserequest1_T_hum','test_CreateInteractionWithCase_ForMember2'); 	
		}
         
               Test.StopTest();
    }

    static testMethod void test_CreateInteractionWithCase_ForMember24()
    {
                             Test.startTest();   
         HUMConstants__c  objhumc = New HUMConstants__c();
            objhumc.name ='UNKNOWN_MEMBER';
            objhumc.StringValue__c ='Unknown Member';
            insert objhumc ;
            system.assertEquals(objhumc.id != null, True);

            HUMConstants__c  objhumc1 = New HUMConstants__c();
                objhumc1.name ='UNKNOWN_MEMBER_EXTID';
                objhumc1.StringValue__c ='UKM|00|SFDC|';
                insert objhumc1 ;

                HUMConstants__c  objhumc3 = New HUMConstants__c();
            objhumc3.name ='PIPE_CHARACTER';
            objhumc3.StringValue__c ='PIPE_CHARACTER';
            insert objhumc3 ;
         Account ProviderAcc = HUMTestDataHelperTest.getAccount();
            ProviderAcc.Name = 'test account';
            ProviderAcc.Enterprise_ID__c = '123456';
            ProviderAcc.NPI_ID__c = '1234567890';
            ProviderAcc.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Provider').getRecordTypeId();
            insert ProviderAcc;
                   HUMSearchLimit__c Humsearch=new HUMSearchLimit__c();
        Humsearch.Search_Limit__c=10;
        Humsearch.name='Member_SearchLimit';
        insert Humsearch;
        
        List<Account> alist = [select Name, Enterprise_ID__C, NPI_ID__C from Account limit 1];
      //  CreateInteractionResponse_DTO_HUM response1 = CreateInteractionWithCaseRest_D_HUM.createInteraction(owrap);
      //  system.assertNotEquals(response1,NULL);
           List<InteractingAccount_DTO_HUM> wrap = new list<InteractingAccount_DTO_HUM>();
           InteractingAccount_DTO_HUM InteractingAccount1=new InteractingAccount_DTO_HUM();
InteractingAccount1.AddressLine1='';
        InteractingAccount1.AddressLine2='';
        InteractingAccount1.City='';
        InteractingAccount1.DOB='';
        InteractingAccount1.EmailID='';
 InteractingAccount1.FirstName='abt';
       InteractingAccount1.HomePhone='';
       InteractingAccount1.ID='';
       InteractingAccount1.IDType='GroupID';
           InteractingAccount1.PlatformCode='';
        InteractingAccount1.MemberIdDependentCode='';
        InteractingAccount1.LastName='abtestt';
        InteractingAccount1.State='';
       InteractingAccount1.EntityType='Member';
        InteractingAccount1.ZipCode='';
        InteractingAccount1.ZipPlus='';
        InteractingAccount1.ProviderClassification='Facility';
        InteractingAccount1.AccountName='abtest';
        
           wrap.add(InteractingAccount1);

           CreateCaseRequest_H_HUM.getGroupAccountMap(InteractingAccount1, ''); 

           CreateCaseRequest_H_HUM.CreateUnknownMembers_DTO_HUM unMem= new  CreateCaseRequest_H_HUM.CreateUnknownMembers_DTO_HUM();
           unMem.MemberInteractingId = 'SSN';
           list<CreateCaseRequest_H_HUM.CreateUnknownMembers_DTO_HUM> lstunmem= new list<CreateCaseRequest_H_HUM.CreateUnknownMembers_DTO_HUM>();
           lstunmem.add(unMem);
           try{
            CreateCaseRequest_H_HUM.insertConsumerTaxIds(lstunmem);
         }catch(exception ex){
            HUMExceptionHelper.logErrors(ex,'CreateCaaserequest1_T_hum','test_CreateInteractionWithCase_ForMember2'); 	
         }
               Test.StopTest();
    }

    static testMethod void test_CreateInteractionWithCase_ForMember26()
   {
                            Test.startTest(); 
       
           HUMConstants__c  objhumc3 = New HUMConstants__c();
           objhumc3.name ='PIPE_CHARACTER';
           objhumc3.StringValue__c ='PIPE_CHARACTER';
           insert objhumc3 ;
            HUMConstants__c  objhumc2 = New HUMConstants__c();
           objhumc2.name ='UNKNOWN_MEMBER_EXTID';
           objhumc2.StringValue__c ='UNKNOWN_MEMBER_EXTID';
           insert objhumc2 ;
        HUMConstants__c  objhumc = New HUMConstants__c();
           objhumc.name ='UNKNOWN_MEMBER';
           objhumc.StringValue__c ='Unknown Member';
           insert objhumc ;
           system.assertEquals(objhumc.id != null, True);
        Account ProviderAcc = new account();//HUMTestDataHelperTest.getAccount();
           ProviderAcc.Name = 'test account';
           ProviderAcc.Enterprise_ID__c = '123456';
           ProviderAcc.NPI_ID__c = '1234567890';
           ProviderAcc.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Unknown Group').getRecordTypeId();
           insert ProviderAcc;
                   HUMSearchLimit__c Humsearch=new HUMSearchLimit__c();
       Humsearch.Search_Limit__c=10;
       Humsearch.name='Member_SearchLimit';
       insert Humsearch;
     //  CreateInteractionResponse_DTO_HUM response1 = CreateInteractionWithCaseRest_D_HUM.createInteraction(owrap);
     //  system.assertNotEquals(response1,NULL);
          List<InteractingAccount_DTO_HUM> wrap = new list<InteractingAccount_DTO_HUM>();
          InteractingAccount_DTO_HUM InteractingAccount=new InteractingAccount_DTO_HUM();
InteractingAccount.AddressLine1='';
       InteractingAccount.AddressLine2='';
       InteractingAccount.City='';
       InteractingAccount.DOB='';
       InteractingAccount.EmailID='';
InteractingAccount.FirstName='';
      InteractingAccount.HomePhone='';
      InteractingAccount.ID=ProviderAcc.Id;
      InteractingAccount.IDType='Member-Id-Base';
          InteractingAccount.PlatformCode='';
       InteractingAccount.MemberIdDependentCode='member';
       InteractingAccount.LastName='';
       InteractingAccount.State='';
      InteractingAccount.EntityType='Member';
       InteractingAccount.ZipCode='';
       InteractingAccount.ZipPlus='';
       InteractingAccount.ProviderClassification='Facility';
       InteractingAccount.AccountName='';
          wrap.add(InteractingAccount);
         try{ 
       //CreateCaseRequest_H_HUM.getInteractingAccountDetails_V4(wrap, '');
          }catch(exception ex){
           HUMExceptionHelper.logErrors(ex,'CreateCaaserequest1_T_hum','test_CreateInteractionWithCase_ForMember6');
        }
       CreateCaseRequest_H_HUM.getGroupAccountMap(InteractingAccount, '');
       
       try{
        CreateCaseRequest_H_HUM.getGroupAccountMap(null, '');
       }catch(exception ex){
        HUMExceptionHelper.logErrors(ex,'CreateCaaserequest1_T_hum','test_CreateInteractionWithCase_ForMember6');
     }

          CreateCaseRequest_H_HUM.getGroupRecordId(wrap, '');
          try{  CreateCaseRequest_H_HUM.getmemberRecordId(wrap, '');
       }catch(HUMCustomException ex){
        HUMExceptionHelper.logErrors(ex,'CreateCaaserequest1_T_hum','test_CreateInteractionWithCase_ForMember6');
     }
        
          CreateCaseRequest_H_HUM.getProviderAccountMap(InteractingAccount, '');
       
          CreateCaseRequest_H_HUM.getInteractionWrapObj(InteractingAccount, '');
         // CreateCaseRequest_H_HUM.prepareUnknownAccts(new CaseServiceRestInteraction_DTO_HUM(),'');
         CreateCaseRequest_H_HUM.insertConsumerTaxIds(new list<CreateCaseRequest_H_HUM.CreateUnknownMembers_DTO_HUM>());
       try{
        CreateCaseRequest_H_HUM.getProviderRecordId(new list<InteractingAccount_DTO_HUM>(), '');
       }catch(exception ex){
        HUMExceptionHelper.logErrors(ex,'CreateCaaserequest1_T_hum','test_CreateInteractionWithCase_ForMember6');
     }
        
              Test.StopTest();
   }

   static testMethod void test_CreateInteractionWithCase_ForMember27()
    {
                             Test.startTest();   
         HUMConstants__c  objhumc = New HUMConstants__c();
            objhumc.name ='UNKNOWN_MEMBER';
            objhumc.StringValue__c ='Unknown Member';
            insert objhumc ;
            system.assertEquals(objhumc.id != null, True);
         Account ProviderAcc = HUMTestDataHelperTest.getAccount();
            ProviderAcc.Name = 'test account';
            ProviderAcc.Enterprise_ID__c = '123456';
            ProviderAcc.NPI_ID__c = '1234567890';
            ProviderAcc.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Provider').getRecordTypeId();
            insert ProviderAcc;
      //  CreateInteractionResponse_DTO_HUM response1 = CreateInteractionWithCaseRest_D_HUM.createInteraction(owrap);
      //  system.assertNotEquals(response1,NULL);
              HUMSearchLimit__c Humsearch=new HUMSearchLimit__c();
        Humsearch.Search_Limit__c=10;
        Humsearch.name='Member_SearchLimit';
        insert Humsearch;
           List<InteractingAccount_DTO_HUM> wrap = new list<InteractingAccount_DTO_HUM>();
           InteractingAccount_DTO_HUM InteractingAccount=new InteractingAccount_DTO_HUM();
InteractingAccount.AddressLine1='';
        InteractingAccount.AddressLine2='';
        InteractingAccount.City='';
        InteractingAccount.DOB='';
        InteractingAccount.EmailID='';
 InteractingAccount.FirstName='';
       InteractingAccount.HomePhone='';
       InteractingAccount.ID='';//ProviderAcc.Id;
       InteractingAccount.IDType='';
           InteractingAccount.PlatformCode='';
        InteractingAccount.MemberIdDependentCode='';
        InteractingAccount.LastName='';
        InteractingAccount.State='';
       InteractingAccount.EntityType='Member';
        InteractingAccount.ZipCode='';
        InteractingAccount.ZipPlus='';
        InteractingAccount.ProviderClassification='Facility';
        InteractingAccount.AccountName='';
           wrap.add(InteractingAccount);
           CreateCaseRequest_H_HUM.getGroupAccountMap(InteractingAccount, '');
           CreateCaseRequest_H_HUM.getmemberRecordId(wrap, '');
          
           CreateCaseRequest_H_HUM.getProviderAccountMap(InteractingAccount, '');
        
           CreateCaseRequest_H_HUM.getInteractionWrapObj(InteractingAccount, '');
          CreateCaseRequest_H_HUM.insertConsumerTaxIds(new list<CreateCaseRequest_H_HUM.CreateUnknownMembers_DTO_HUM>());
        try{
         CreateCaseRequest_H_HUM.getProviderRecordId(new list<InteractingAccount_DTO_HUM>(), '');
        }catch(exception ex){
			HUMExceptionHelper.logErrors(ex,'CreateCaaserequest1_T_hum','test_CreateInteractionWithCase_ForMember1'); 			
		}
         
               Test.StopTest();
    }
    
}