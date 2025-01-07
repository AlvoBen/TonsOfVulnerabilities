/*******************************************************************************************************************************
Apex Class Name : WQV_ExpressionBuilder_C_HUM.cls
Version         : 1.0
Created On      : December 2017
Function        : Class contains methods for Expression BUilder for Work Queue View.

Modification Log:
* Developer Name            Code Review                Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------
* Avinash Choubey									12/20/2017					Original Version
* Rajesh Keswani          	 163966                 12/22/2017                  REQ - 335304; Set/Group field, Edit Expression UI & Deleting Queue View Items - Expression impacts.
* Prudhvi Pamarthi                                  04/08/2021                  US#2163299 Checkmarx scan issue fix
* Prudhvi Pamarthi                                  05/05/2021                  US#2212581 Checkmarx issue fix
* Prasuna Pattabhi                                  13/07/2021                  US#2393392 Checkmarx issue fix
-*/
public with sharing class WQV_ExpressionBuilder_C_HUM
{
    public String serializeData
    {
        get;
        set;
    }
    public string QVName{get;set;}
    public string WQName{get;set;}
    public Map < String, String > mapQviToApi;
    public Map < String, List < String >> mapSetToQvi;
    public Map < String, List < String >>mapAPItoQvi;
    public Queue_View__c QueveViewObj;
    public string qvId;
    
    public WQV_ExpressionBuilder_C_HUM()
    {
        mapQviToApi = new Map < String, String > ();
        if(ApexPages.currentPage()!=null){
            qvId = ApexPages.currentPage().getParameters().get('id');
        }
        if (null != qvId)
        {
            qvId = qvId.escapeHtml4();
            QVI_DTO_HUM qviData = new QVI_DTO_HUM();
            Integer rownum = 1;
            for (Queue_View_Item__c objQVI: [SELECT Id, Name, Queue_View__c,Queue_View__r.name,Queue_View__r.Work_Queue_Name__r.name,  API_Name__c, Field_Operator__c, Field_Value__c, Set_Group__c FROM Queue_View_Item__c where Queue_View__c =: qvId])
            {
                qviData.lstQVIWrapper.add(new QVI_DTO_HUM.QVIWrapper(objQVI, rowNum++));
                mapQviToApi.put(objQVI.Name, objQVI.API_Name__c);
                QVName = objQVI.Queue_View__r.name;
                WQName = objQVI.Queue_View__r.Work_Queue_Name__r.name;
            }
            serializeData = JSON.serialize(qviData);
            serializeData = serializeData.remove('{"lstQVIWrapper":');
            serializeData = serializeData.removeEnd('}');
        }
    }
    
    
    /**
    * builds expression and updates QVI records
    * <p>
    * Method to builds expression and updates QVI records
    * Between different "sets/groups" apply OR
    * Within a "set/group":
    - When the API is the same, apply OR
    - When the API is different, apply AND
    * @param QVI_DTO_HUM        QVI Data 
    * @return                void
    */
    public Object buildExpression()
    {
        QVI_DTO_HUM wrlist = buildQVIDTO();
        String ExpressionBuilder = '';
        List < String > tempLstString;
        List < String > oldLstString;
        mapSetToQvi = new Map < String, List < String >> ();
        List < String > lstSetToQvi;
        Integer CountValue;
        Integer OuterSetCount = 1;
        Integer OuterCountValue;
        Queue_view_item__c QVI;
        try
        {
            list < Queue_view_item__c > listQVI = new List < Queue_view_item__c > ();
            getSetofQVI(wrlist,QVI,listQVI,tempLstString,oldLstString);
            for (String strSet: mapSetToQvi.keySet())
            {
                mapAPItoQvi = new Map < String, List < String >> ();
                ExpressionBuilder = ExpressionBuilder + '(';
                lstSetToQvi = new List < String > ();
                lstSetToQvi = mapSetToQvi.get(strSet);
                buildAPIQVIMap(strSet,tempLstString,oldLstString ,lstSetToQvi);
                OuterCountValue = 1;
                for (String strApi: mapAPItoQvi.keySet())
                {
                    CountValue = 1;
                    ExpressionBuilder = ExpressionBuilder + '(';
                    for (String eachStr: mapAPItoQvi.get(strApi))
                    {
                        ExpressionBuilder = (CountValue == 1) ? ExpressionBuilder + eachStr : ExpressionBuilder + ' OR ' + eachStr;
                        CountValue++;
                    }
                    ExpressionBuilder = ExpressionBuilder + ')';
                    ExpressionBuilder = (OuterCountValue < mapAPItoQvi.size()) ? ExpressionBuilder + ' AND ' : ExpressionBuilder + ')';
                    OuterCountValue++;
                } 
                if (OuterSetCount < mapSetToQvi.size())  ExpressionBuilder = ExpressionBuilder + ' OR ';
                OuterSetCount++;
            }
            return navigateQueueView(ExpressionBuilder,listQVI);
        }
        catch(Exception ex)
        {
            ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.error, 'Something went wrong. Please contact the helpdesk if issue persists.'));
            return null;
        }
    }
    
    /**
    * Prepare QVI records
    * <p>
    * Method to prepare set of QVI records
    * @ Param String  strSet
    * @ Param List < String > tempLstString
    * @ Param List < String > oldLstString
    * @ Param Map < String, List < String >>mapAPItoQvi
    * @return                void
    */
    private void  buildAPIQVIMap(String strSet, List < String > tempLstString, List < String > oldLstString , List < String > lstSetToQvi)
    {
        for (String strQvi: lstSetToQvi)
            {
                tempLstString = new List < String > ();
                oldLstString = new List < String > ();
                tempLstString.add(strQvi);
                if (mapAPItoQvi.get(mapQviToApi.get(strQvi)) != null)
                {
                    oldLstString = mapAPItoQvi.get(mapQviToApi.get(strQvi));
                    if (!oldLstString.isEmpty())
                        for (String str: oldLstString) tempLstString.add(str);
                    mapAPItoQvi.put(mapQviToApi.get(strQvi), tempLstString);
                }
                else mapAPItoQvi.put(mapQviToApi.get(strQvi), tempLstString);
            }
    }
    
     /**
    * Prepare QVI records
    * <p>
    * Method to prepare set of QVI records
    * @ Param String  Expression
    * @ Param list < Queue_view_item__c > lstQVI
    * @return    PageReference
    */
    private PageReference navigateQueueView(String sExpression , list < Queue_view_item__c > lstQVI)
    {
        QueveViewObj = new Queue_View__c();
        if(Schema.sObjectType.Queue_View__c.fields.id.isAccessible())
        QueveViewObj.Id = qvId;
        If (Schema.sObjectType.Queue_View__c.fields.Expression__c.isUpdateable())
        QueveViewObj.Expression__c = sExpression;
        if (lstQVI != null && lstQVI.size() > 0) update lstQVI;
        update QueveViewObj; 
        String returnString = '/' + QueveViewObj.Id;
        PageReference pageRef = new PageReference(returnString);
        return pageRef;
    }
    
    /**
    * Build QVI DTO
    * <p>
    * Method to build DTO of QVI record
    * @return                void
    */
    private QVI_DTO_HUM buildQVIDTO()
    {
        serializeData = serializeData.removeStart('{');
        serializeData = serializeData.removeEnd('}');
        serializeData = '{"lstQVIWrapper": ' + serializeData + '}';
        QVI_DTO_HUM wrlistDTO = (QVI_DTO_HUM) JSON.deserialize(serializeData, QVI_DTO_HUM.class);
        return wrlistDTO;
    }
    
    /**
    * Redirect to QVI records
    * <p>
    * Method to redirect to QVI records
    * @return                void
    */
    public PageReference redirectDirectly()
    {
        String returnString = '/' + qvId;
        PageReference pageRef = new PageReference(returnString);
        return pageRef;
    }
    
    /**
    * Prepare QVI records
    * <p>
    * Method to prepare set of QVI records
    * @ Param QVI_DTO_HUM wrlist
    * @ Param Queue_view_item__c QVI
    * @ Param list < Queue_view_item__c > listQVI
    * @ Param List < String > tempLstString
    * @ Param List < String > oldLstString
    * @return                void
    */
    private void getSetofQVI(QVI_DTO_HUM wrlist,Queue_view_item__c QVI,list < Queue_view_item__c > listQVI,List < String > tempLstString,List < String > oldLstString)
    {
        for (QVI_DTO_HUM.QVIWrapper wrpr: wrlist.lstQVIWrapper)
        {
            if(wrpr.sSetGroup != null)
            {
                QVI = new Queue_view_item__c(id = wrpr.sRecId, Set_Group__c = wrpr.sSetGroup.trim());
                listQVI.add(QVI);
                tempLstString = new List < String > ();
                oldLstString = new List < String > ();
                tempLstString.add(wrpr.sRecName);
                if (mapSetToQvi.get(wrpr.sSetGroup) != null) 
                {
                    oldLstString = mapSetToQvi.get(wrpr.sSetGroup);
                    if (!oldLstString.isEmpty())  tempLstString.addAll(oldLstString);
                    mapSetToQvi.put(wrpr.sSetGroup, tempLstString);
                }
                else
                {
                    if (wrpr.sSetGroup != null && wrpr.sSetGroup.split(',').size() > 0)
                    {
                        for (string setStr: wrpr.sSetGroup.split(','))
                        {
                            if (mapSetToQvi.get(setStr) != null)
                            {
                                list < string > oldValue = mapSetToQvi.get(setStr);
                                oldValue.add(wrpr.sRecName);
                                mapSetToQvi.put(setStr, oldValue);
                            }
                            else mapSetToQvi.put(setStr, new list <string >{wrpr.sRecName});
                        }
                    }
                    else mapSetToQvi.put(wrpr.sSetGroup, tempLstString);
                }
            }
        }
    }
}