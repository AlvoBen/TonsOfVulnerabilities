package com.sap.security.api.ume;

/**
 * Class PopulateAttributes is used to define specific attributes which can be used 
 * to populate an IPrincipal object. If you know in advance which kind of 
 * attributes you want to access use this class to define the used attributes.
 * You will have better performance and less back and forth communication with 
 * the server if you specify the desired attributes.
 * 
 * @deprecated use com.sap.security.api.AttributeList() instead
 */
public class PopulateAttributes extends com.sap.security.api.AttributeList
{
	private static final long serialVersionUID = -8086034239063719966L;
	
    public static final String VERSIONSTRING = "$Id: //engine/js.security.api.lib/dev/src/_tc~sec~ume~api/java/com/sap/security/api/ume/PopulateAttributes.java#1 $ from $DateTime: 2008/09/17 17:07:41 $ ($Change: 217696 $)";
    
    public PopulateAttributes()
    {
    	super();
    }

/***
 * Add attributes to this instance of AttributeList 
 * @param populateAttributes instance of another AttributeList which is used to copy
 * the attributes into this instance
 ***/        
    public void addPopulateAttributes(PopulateAttributes populateAttributes)
    {
        if (populateAttributes != null)
        {
            int size = populateAttributes.getSize();
            for (int i=0; i<size; i++)
            {
                this.addAttribute( populateAttributes.getNameSpaceOfAttributeAt(i),
                                    populateAttributes.getAttributeNameOfAttributeAt(i),
                                    populateAttributes.getAttributeTypeOfAttributeAt(i) );
            }
        }
    }

}

