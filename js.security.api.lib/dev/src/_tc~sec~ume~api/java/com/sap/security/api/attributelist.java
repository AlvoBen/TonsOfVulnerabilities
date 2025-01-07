package com.sap.security.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;


/**
 * Class AttributeList is used to define specific attributes which can be used 
 * to populate an IPrincipal object. If you know in advance which 
 * attributes you want to access use this class to define the used attributes.
 * You will have better performance and less communication with 
 * the server if you specify the desired attributes.
 * 
 * The object is NOT synchronized, which means that one has to expect runtime 
 * exceptions like ConcurrentModificationException or ArrayIndexOutOfBoundsException
 * when the same instance of a AttributeList object is used in more than one thread 
 * in parallel.
 * 
 * Note: Reuse of a AttributeList object which was already used to get a principal
 *       from a factory is not supported. The factory might change the content of
 *       the passed AttributeList object. 
 */

public class AttributeList implements Serializable, Cloneable {

	private static final long serialVersionUID = -2211404348089879046L;

    public static final String VERSIONSTRING = "$Id: //engine/js.security.api.lib/dev/src/_tc~sec~ume~api/java/com/sap/security/api/AttributeList.java#1 $ from $DateTime: 2008/09/17 17:07:41 $ ($Change: 217696 $)";

	private static final String   NULL_NAMESPACE                 = "THIS_IS_A_NAMESPACE_PLACEHOLDER_FOR_NULL_NAMESPACES";

/***
 * TYPE_UNKNOWN	is returned by {@link #getAttributeType(String, String)} if the type of the values of an
 * attribute cannot be determined
 ***/
    public static final int TYPE_UNKNOWN = 0;
/***
 * TYPE_STRING	is returned by {@link #getAttributeType(String, String)} if the type of the values of an
 * attribute is of type String
 ***/
    public static final int TYPE_STRING  = 1;
/***
 * TYPE_BLOB	is returned by {@link #getAttributeType(String, String)} if the type of the values of an
 * attribute is of type byte[]
 ***/
    public static final int TYPE_BLOB    = 2;

    protected ArrayList attributes;
    protected HashSet   fastAccess;
    
    protected boolean mCheckSizeLimit;
    

	/***
	 * Default constructor of AttributeList. An empty instance of AttributeList is ignored during 
	 * population of an IPrincipal object. You have to use {@link #addAttribute(String, String)}
	 * to add attributes
	 ***/
    public AttributeList()
    {
		this (true);
    }

	/***
	 * Constructor of AttributeList. An empty instance of AttributeList is ignored during 
	 * population of an IPrincipal object. 
	 * You have to use {@link #addAttribute(String, String)}to add attributes
	 * @param checkSizeLimit specifies whether the size limit of 25 attributes should be checked
	 ***/
	public AttributeList(boolean checkSizeLimit)
	{
		attributes = new ArrayList();
		fastAccess = new HashSet();
		mCheckSizeLimit = checkSizeLimit;
	}

/***
 * used to compare instances of AttributeList
 * @param obj object which should be compared with this instance
 * @return true if objects are identical, false otherwise
 ***/
    public boolean equals(Object obj)
    {
        if (!(obj instanceof AttributeList))
        {
            return false;
        }
        AttributeList pa = (AttributeList)obj;
        if (this.getSize() != pa.getSize())
        {
            return false;
        }
        boolean allOk = true;
        AttributeWrapper aw = null;
        
        for (Iterator iter=pa.attributes.iterator();iter.hasNext() && allOk;)
        {
            aw = (AttributeWrapper)iter.next();
            allOk = allOk && attributes.contains(aw);
        }
        return allOk;
    }

    public int hashCode()
    {
    	StringBuffer result = new StringBuffer("");
    	if (attributes != null)
    	{
    		int size = attributes.size();
    		result.append(size);
			String[] namespAttribute = new String[size];
			int counter = 0;
    		for (Iterator iter=attributes.iterator(); iter.hasNext(); )
    		{
    			AttributeWrapper dummy = (AttributeWrapper)iter.next();
    			namespAttribute[counter] = dummy.getNameSpace() + "|" + dummy.getAttributeName();
				counter++;
    		}
    		Arrays.sort(namespAttribute);
    		for (int i=0; i<counter; i++)
    		{
    			result.append(namespAttribute[i]);
    		}
    	}
    	return result.toString().hashCode();
    }

/***
 * returns the type of the attribute of this instance of AttributeList.
 * @param index index of attribute in AttributeList
 * @return following constants: TYPE_UNKNOWN, TYPE_STRING, TYPE_BLOB
 ***/
    public int getAttributeTypeOfAttributeAt(int index)
    {
        return this.getAttributeAt(index).getType();
    }

/***
 * returns the type of the attribute of this instance of AttributeList.
 * @param nameSpace namespace of the attribute
 * @param attributeName name of the attribute
 * @return following constants: TYPE_UNKNOWN, TYPE_STRING, TYPE_BLOB
 ***/
    public int getAttributeType(String nameSpace, String attributeName)
    {
        int size = attributes.size();
        String aName;
        String aNamespace;
        int attributeType = TYPE_UNKNOWN;
        
        for (int i=0; i<size; i++)
        {
            aName = this.getAttributeAt(i).getAttributeName();
            aNamespace = this.getAttributeAt(i).getNameSpace();
            if (aName.equals(attributeName) && aNamespace.equals(nameSpace))
            {
                return this.getAttributeAt(i).getType();
            }    
        }
        return attributeType;
    }

	/***
	 * Removes an attribute from this instance of AttributeList with following properties
	 * @param nameSpace namespace of the attribute
	 * @param attributeName name of the attribute
	 ***/
		public synchronized void removeAttribute(String nameSpace, String attributeName)
		{
			AttributeWrapper aw = new AttributeWrapper(nameSpace, attributeName, AttributeList.TYPE_UNKNOWN);
			fastAccess.remove(aw);
			attributes.remove(aw);
		}
	 
 	/***
 	 * Returns whether an attribute is contained in this instance of AttributeList with following properties
 	 * @param nameSpace namespace of the attribute
	 * @param attributeName name of the attribute
 	 * @param type the attribute type
 	 * @return boolean: true if this instance contains this attribute, otherwise false
 	 ***/
 		public boolean containsAttribute(String nameSpace, String attributeName, int type)
 		{
 			AttributeWrapper aw = new AttributeWrapper(nameSpace, attributeName, type);
 			return fastAccess.contains(aw);
 		}
	 
	 	public boolean containsAttribute(String nameSpace, String attributeName)
	 	{
	 		return this.containsAttribute(nameSpace, attributeName, TYPE_UNKNOWN);
	 	}


/***
 * Add an attribute to this instance of AttributeList with following properties
 * 
 * Note: A AttributeList can only contain 25 attributes.
 * 
 * @param nameSpace namespace of the attribute
 * @param attributeName name of the attribute
 * @throws UMRuntimeException if the AttributeList already contains 25 attributes.
 ***/
    public void addAttribute(String nameSpace, String attributeName)
    {
        this.addAttribute(nameSpace, attributeName, TYPE_UNKNOWN);
    }

/***
 * Add an attribute to this instance of AttributeList with following properties
 * @param nameSpace namespace of the attribute
 * @param attributeName name of the attribute
 * @param type of the attribute. Following constants are allowed:
 *  TYPE_UNKNOWN, TYPE_STRING, TYPE_BLOB
 * @throws UMRuntimeException if the AttributeList already contains 25 attributes.
 ***/
    public synchronized void addAttribute(String nameSpace, String attributeName, int type)
    {
    	if (mCheckSizeLimit && (attributes.size() == 25))
    	{
    		throw new UMRuntimeException("This AttributeList contains already 25 attributes.");
    	}
        if (attributeName == null)
        {
            throw new UMRuntimeException("Can't add attribute when attribute name is null!");
        }
        if (nameSpace == null)
        {
        	nameSpace = NULL_NAMESPACE;
        }
		if (nameSpace.length() > 255)
		{
			throw new UMRuntimeException("Namespaces with more than 255 characters are not supported. Length="+nameSpace.length()+", Namespace=\""+nameSpace+"\"");
		}
		if (attributeName.length() > 255)
		{
			throw new UMRuntimeException("Attribute names with more than 255 characters are not supported. Length="+attributeName.length()+", Attribute name=\""+attributeName+"\"");
		}
        if (!this.containsAttribute(nameSpace,attributeName, type))
        {
			AttributeWrapper aw = new AttributeWrapper(nameSpace,attributeName, type);
			attributes.add(aw);
			fastAccess.add(aw);
        }
    }
    
/***
 * Add attributes to this instance of AttributeList 
 * @param populateAttributes instance of another AttributeList which is used to copy
 * the attributes into this instance
 * @throws UMRuntimeException if the AttributeList already contains 25 attributes.
 ***/
    public void addAttributeList(AttributeList populateAttributes)
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
    
    private AttributeWrapper getAttributeAt(int i)
    {
    	return (AttributeWrapper)attributes.get(i);
    }
    
	/***
	 * Returns a whether this AttributeList is a subset of the given AttributeList
	 * 
	 * @return true if this AttributeList is a subset of the given AttributeList or false if this AttributeList is empty or the passed AttributeList is null
	 ***/
    public boolean isSubsetOf(AttributeList populateAttributes)
    {
		if ((populateAttributes == null) || (this.getSize() == 0))
		{
			return false;
		}
		int size = this.getSize();
		boolean allFound = true;
		for (int i=0; (i<size) && (allFound); i++)
		{
			if ( !populateAttributes.contains(this.getAttributeAt(i)) )
			{
				allFound = false;
			}
		}
		return allFound;
    }
    
    private boolean contains(AttributeWrapper attrWrapper)
    {
    	return attributes.contains(attrWrapper);
    }
    
	/***
	 * Returns a new instance of AttributeList which contains all attributes which are
	 * contained in the passed AttributeList, but not contained in this instance. 
	 * @return Object: the new AttributeList instance
	 ***/
    public AttributeList getNotContainedAttributes(AttributeList populateAttributes)
    {
		if (populateAttributes == null)
		{
			return new AttributeList();
		} 
		AttributeList attrList = new AttributeList(populateAttributes.mCheckSizeLimit);
		int size = populateAttributes.getSize();
		for (int i=0; i<size; i++)
		{
			if (!attributes.contains( populateAttributes.getAttributeAt(i) ))
			{
				attrList.addAttribute(  populateAttributes.getNameSpaceOfAttributeAt(i),
										populateAttributes.getAttributeNameOfAttributeAt(i),
										populateAttributes.getAttributeTypeOfAttributeAt(i) );
			}
		}
		return attrList;
    }
    
	/***
	 * Returns a new instance of AttributeList which contains the same data as this instance. 
	 * @return Object: the new AttributeList instance
	 ***/
    public Object clone()
    {
    	AttributeList attrList = new AttributeList(mCheckSizeLimit);
    	attrList.addAttributeList(this);
    	return attrList;
    }

/***
 * Returns the number of components in this attributeList
 ***/
    public int getSize()
    {
        return attributes.size();
    }

/***
 * Returns the namespace of an attribute at a given index in this attributeList
 * @return String: name of attribute's namespace
 ***/
    public String getNameSpaceOfAttributeAt(int index)
    {
        return this.getAttributeAt(index).getNameSpace();
    }

/***
 * Returns the name of an attribute at a given index in this attributeList
 * @param index given index in attributeList
 * @return String: name of attribute at given index
 ***/
    public String getAttributeNameOfAttributeAt(int index)
    {
        return this.getAttributeAt(index).getAttributeName();
    }

/***
 * Private internally used class to administrate attributeList
 ***/
    private class AttributeWrapper implements Serializable
    {
    	private static final long serialVersionUID = 429417855169902332L;
    	
        private String mNameSpace;
        private String mAttributeName;
        private int    mType;
        private int    mHashCode;

        public AttributeWrapper(String nameSpace, String attributeName)
        {
        	this (nameSpace, attributeName, TYPE_UNKNOWN);
        }

        public AttributeWrapper(String nameSpace, String attributeName, int type)
        {
            mNameSpace     = nameSpace;
            mAttributeName = attributeName;
            mType          = type;
            mHashCode      = 0;
        }

        public boolean equals(Object obj)
        {
            if (!(obj instanceof AttributeWrapper))
            {
                return false;
            }
            if (obj.hashCode() != this.hashCode())
            {
            	return false;
            }
            AttributeWrapper aw = (AttributeWrapper)obj;
            return (aw.mNameSpace.equals(mNameSpace) && aw.mAttributeName.equals(mAttributeName) && ((aw.mType == mType) || (aw.mType==TYPE_UNKNOWN) || (mType==TYPE_UNKNOWN) ));
        }
        
        public int hashCode()
        {
        	if (mHashCode == 0)
        	{
            	StringBuffer result = new StringBuffer(mNameSpace.length() + 1 + mAttributeName.length());
            	result.append(mNameSpace);
            	result.append("|");
            	result.append(mAttributeName);
            	mHashCode = result.toString().hashCode();
        	}
        	return mHashCode;
        }
        
        public int getType()
        {
            return mType;
        }

        public String getNameSpace()
        {
            return mNameSpace;
        }

        public String getAttributeName()
        {
            return mAttributeName;
        }

        public String toString()
        {
            StringBuffer res = new StringBuffer();
			res.append("\"");
            res.append(mNameSpace);
            res.append("\"|->\"");
            res.append(mAttributeName);
            res.append("\" (type: ");
            switch( mType)
            {
                case TYPE_STRING:
                      res.append("string");
                break;

                case TYPE_BLOB:
                      res.append("blob");
                break;

                default:
                      res.append("unknown");
                break;
            }
            res.append(")");
            return res.toString();
        }

    }

/***
 * Returns a string representation of this AttributeList.
 * Containing the String representation of each element.
 ***/
    public String toString()
    {
        StringBuffer res = new StringBuffer();
		res.append("***************************************************************************\n");
		res.append("* ");
		if (mCheckSizeLimit)
		{
			res.append("Size limit (25) enabled.");
		}
		else
		{
			res.append("No size limit.");
		}
		res.append("\n");
		res.append("* ");
		res.append(this.getClass().getName());
		res.append("\n");
        res.append("*\n");
        int size=attributes.size();
        for (int i=0; i<size; i++)
        {
            res.append("* ");
            res.append( this.getAttributeAt(i).toString() );
            res.append("\n");
        }
		res.append("***************************************************************************\n");
        return res.toString();
    }

}
