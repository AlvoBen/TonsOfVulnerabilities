package com.sap.security.api.persistence;

/**
 * This interface provides functionality to access a search element
 *
 * @author  Alexander Primbs
 * @version $Revision: #1 $ <BR>
 * @deprecated  use @link com.sap.security.api.ISearchAttribute instead
 */
public interface ISearchElement {

    public static final int EQUALS_OPERATOR         = 0; //= (equality)
    public static final int LIKE_OPERATOR           = 1; //LIKE (substring)
    public static final int GREATER_THAN_OPERATOR   = 2; //> (greater than)
    public static final int LESS_THAN_OPERATOR      = 3; //> (greater than)

   /**
    *  returns the namespace of this search element
    *
    * @return  The namespace
    */
    public String getAttributeNameSpace();

   /**
    *  returns the attribute name of this search element
    *
    * @return  The attribute name
    */
    public String getAttributeName();

   /**
    *  returns the value search element
    *
    * @return  The value
    */
    public Object getAttributeValue();

   /**
    *  returns the operator of this search element (EQUALS or LIKE)
    *
    * @return  The operator
    */
    public int getOperator();

   /**
    *  returns whether the search element is case sensitive
    *
    * @return  isCaseSensitive
    */
    public boolean isCaseSensitive();
}
