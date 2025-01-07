package com.sap.sdo.api.helper;

import commonj.sdo.Property;
import commonj.sdo.helper.HelperContext;

public interface SapHelperContext extends HelperContext {

    public static final String OPTION_KEY_MIXED_CASE_JAVA_NAMES =
        SapHelperContext.class.getName() + ".MixedCaseJavaNames";
    public static final Boolean OPTION_VALUE_TRUE = Boolean.TRUE;
    public static final Boolean OPTION_VALUE_FALSE = Boolean.FALSE;

    String getId();
    SapHelperContext getParent();
    ClassLoader getClassLoader();
    void setMappingStrategyProperty(Property mappingProperty);
    /**
     * Determines if the HelperContext represented by this
     * <code>HelperContext</code> object is either the same as, or is a super context
     * of, the type represented by the specified
     * <code>assignableFrom</code> parameter. It returns <code>true</code> if so;
     * otherwise it returns <code>false</code>.
     * @param assignableFrom the <code>HelperContext</code> object to be checked
     * @return boolean
     */
    boolean isAssignableContext(HelperContext assignableFrom);

    /**
     * Set an option that is valid for several tasks in this context.
     * An option could be defined once only.
     * <table border="2" bgcolor="#eeeeee">
     * <tr><th>Option key</th><th>Option value</th><th>Description</th></tr>
     * <tr><td rowspan="2">{@link #OPTION_KEY_MIXED_CASE_JAVA_NAMES} =
     *          "com.sap.sdo.api.helper.SapHelperContext.MixedCaseJavaNames"</td>
     *     <td>{@link #OPTION_VALUE_TRUE} = Boolean.TRUE</td>
     *     <td>Java names for types and properties will be generated in camelCase style.
     *         This is the default behavior.</td>
     * </tr>
     * <tr><td>{@link #OPTION_VALUE_FALSE} = Boolean.FALSE</td>
     *     <td>SDO type and property names are used for corresponding Java names.
     *         No conversion will happen, except a first upper case letter for Java class names
     *         and in combination with get, is, or set as method names.</td>
     * </tr>
     * </table>
     * @param key option key
     * @param value option value
     */
    void setContextOption(String key, Object value);
    
    /**
     * Returns a context option, 'null' if option isn't set.
     * For valid option keys see {@link #setContextOption(String, Object)}.
     * @param key option key
     * @return option value
     */
    Object getContextOption(String key);
}
