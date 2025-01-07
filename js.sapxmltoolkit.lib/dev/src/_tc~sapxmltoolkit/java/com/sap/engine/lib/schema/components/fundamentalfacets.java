package com.sap.engine.lib.schema.components;

import com.sap.engine.lib.schema.Constants;
import com.sap.engine.lib.schema.components.impl.ffacets.Value;
import com.sap.engine.lib.schema.validator.ReusableObjectsPool;

/**
 * Represents the 'fundamental facets' of a simple type
 * taken as a whole.
 *
 * The API is a bit different from the spec here.
 * In the spec the term 'fundamental facets' is referred to
 * as a set of components rather than a single component
 * (See XMLSchema - part 2).
 *
 * Since most of those 'fundamental facets' enclose only
 * a single boolean property, I decided to unite them
 * into this pseudo-component; it's worth the clarity,
 * optimization matters less. :)
 *
 *
 * A thing I really wanted to include here a validating method,
 * though the purpose of the API is not to validate, but to
 * only expose the components. In fact, the 'parse' method (as
 * well as the equality-relation and order-relation methods -
 * 'equal' and 'less') do a similar thing.
 *
 * Note: The code for built-in type validation used to reside in
 * com/inqmy/lib/schema/builtin/*. This interface should be used
 * instead.
 *
 * @author  Nick Nickolov, nick_nickolov@abv.bg
 * @version 06-Mar-02, 11:27:57
 */
public interface FundamentalFacets extends Constants {

  Value parse(String strValue);
  
  Value parse(String strValue, ReusableObjectsPool pool);
}

