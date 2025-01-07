/*******************************************************************************************************************************
Apex Class Name : QueueViewItem_D_HUM
Version         : 1.0
Created On      : 06/14/2016 
Function        : Data Layer for QueueViewItem_G_HUM

Modification Log: 
* Developer Name            Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Harshith Mandya          	  		                06/14/2016                   Original Version
* Rajesh Keswani          	 163966 		        10/07/2016                   REQ - 333701; Set/Group field, Edit Expression UI & Deleting Queue View Items - Expression impacts.
********************************************************************************************************************************/
public with sharing class QueueViewItem_D_HUM 
{
	/**
    * Returns List<Queue_View__c>
    * <p>
    * Method to fetch Queue_View__c details
    *
    * @param     setQueueViewIds  		Set of queue view ids
    * @return    List<Queue_View__c>
    */
	public static List<Queue_View__c> getQVInfo(Set<Id> setQueueViewIds)
    {
        return [Select id, Name, Expression__c from Queue_View__c where ID IN: setQueueViewIds limit: (limits.getLimitQueryRows() - limits.getQueryRows())];
    }

    /**
    * Method to update Queue_View__c
    *
    * @param     lstQV  				List of queue views to be updated
    * @return    void
    */
    public static void updateQV(List<Queue_View__c> lstQV)
    {
        update lstQV;
    }
    // updated by Rajesh Keswani 
    /**
     * Method to update Expression for Queue_View__c
     *
     * @param     lstQV  				List of queue views to be updated
     * @param     lstQVI  				List of queue views Items
     * @return    void
    */
    public static void updateQVExpression(List < Queue_View__c > lstQV, List<Queue_View_Item__c> lstQVI)
    {
        if (lstQV != null && lstQV.size() > 0)
        {
            for (Queue_view_item__c QVIobj: lstQVI)
            {
                for (Queue_View__c QVobj: lstQV)
                {
                    
                    if (string.isNotBlank(QVobj.Expression__c) && QVobj.Expression__c.containsIgnoreCase(QVIobj.Name))
                    {
                        String updatedExpression = removeQVINameFromExpression(QVobj.Expression__c, QVIobj.Name);
                        QVobj.Expression__c = updatedExpression;
                    }
                }
            }
        }
    }
    
	/**
     * Method to calculate Expression for Queue_View__c
     *
     * @param     Expression  			Expression Field that needs to be updated
     * @param     QVIname  				Queue view Item name that needs to be deleted
     * @return    string
    */
    public static string removeQVINameFromExpression(string Expression, string QVIname)
    {
        string[] expressionList = Expression.split(QVIname);
        string finalString = '';
        for (string str: expressionList)
        {
            str = str.trim();
            str = str.removeStartIgnoreCase('OR');
            str = str.trim();
            str = str.removeEndIgnoreCase('OR');
            str = str.trim();
            str = str.removeStartIgnoreCase('AND');
            str = str.trim();
            str = str.removeEndIgnoreCase('AND');
            finalString = finalString + str;
        }
        finalString = finalString.remove(' AND ()');
        finalString = finalString.remove(' OR ()');
        finalString = finalString.remove('() AND ');
        finalString = finalString.remove('() OR ');
         finalString = finalString.remove(' AND (())');
        finalString = finalString.remove(' OR (())');
        finalString = finalString.remove('(()) AND ');
        finalString = finalString.remove('(()) OR ');
        finalString = finalString.remove('(())');
        finalString = finalString.remove('()');
        return finalString;
    }
}