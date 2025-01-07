/*
 * Copyright (c) 2006 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package test.suite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import com.sap.sdo.testcase.external.BqlXmlTest;
import com.sap.sdo.testcase.external.BuiltInTypesTest;
import com.sap.sdo.testcase.external.CopyHelperTest;
import com.sap.sdo.testcase.external.DataObjectAccessorExceptionsTest;
import com.sap.sdo.testcase.external.DataObjectContainmentTest;
import com.sap.sdo.testcase.external.DataTypesTest;
import com.sap.sdo.testcase.external.DynamicTypesTest;
import com.sap.sdo.testcase.external.ElementAttributeFormDefaultTest;
import com.sap.sdo.testcase.external.EqualityHelperTest;
import com.sap.sdo.testcase.external.ExtendedInstanceClassTest;
import com.sap.sdo.testcase.external.FacetTestByApi;
import com.sap.sdo.testcase.external.IsSetSetNullTest;
import com.sap.sdo.testcase.external.Jira176OpenContentTest;
import com.sap.sdo.testcase.external.KeysTest;
import com.sap.sdo.testcase.external.ManyValuePropertyTest;
import com.sap.sdo.testcase.external.MixedContentTest;
import com.sap.sdo.testcase.external.NillableTest;
import com.sap.sdo.testcase.external.NoSchemaTest;
import com.sap.sdo.testcase.external.NonSequencedDataObjectTest;
import com.sap.sdo.testcase.external.NullPointerTest;
import com.sap.sdo.testcase.external.NullableTest;
import com.sap.sdo.testcase.external.OppositeTest;
import com.sap.sdo.testcase.external.PropertyRenamingTest;
import com.sap.sdo.testcase.external.ReadOnlyTest;
import com.sap.sdo.testcase.external.RefineNonSequencedTypeTest;
import com.sap.sdo.testcase.external.RefineSequencedTypeTest;
import com.sap.sdo.testcase.external.ScaTest;
import com.sap.sdo.testcase.external.SchemaLocationTest;
import com.sap.sdo.testcase.external.SerializabilityTest;
import com.sap.sdo.testcase.external.SimpleDataObjectTest;
import com.sap.sdo.testcase.external.SimpleTypeTest;
import com.sap.sdo.testcase.external.SoapEncodingTest;
import com.sap.sdo.testcase.external.SubstitutionGroupTest;
import com.sap.sdo.testcase.external.ValuePropertyTest;
import com.sap.sdo.testcase.external.XMLHelperLoadTest;
import com.sap.sdo.testcase.external.XMLHelperSaveTest;
import com.sap.sdo.testcase.external.XPathAccessIndexMapsTest;
import com.sap.sdo.testcase.external.XPathAccessTest;
import com.sap.sdo.testcase.external.XSDHelperTest;
import com.sap.sdo.testcase.external.XmlWithoutSchemaTest;
import com.sap.sdo.testcase.external.XsdParsingTest;
import com.sap.sdo.testcase.external.bql.TestCreateDO;
import com.sap.sdo.testcase.external.glx.RemoveTypesTest;
import com.sap.sdo.testcase.external.helper.DataHelperTest;
import com.sap.sdo.testcase.external.schema.SchemaTypesTest;
import com.sap.sdo.testcase.external.simpletypes.AllSimpleTypeTests;
import com.sap.sdo.testcase.tutorial.TutorialTest;

@RunWith(Suite.class)
@SuiteClasses({
    SerializabilityTest.class,
    DynamicTypesTest.class,
    BuiltInTypesTest.class,
    AllSimpleTypeTests.class,
    SimpleDataObjectTest.class,
    NonSequencedDataObjectTest.class,
    XPathAccessTest.class,
    DataObjectContainmentTest.class,
    ManyValuePropertyTest.class,
    CopyHelperTest.class,
    EqualityHelperTest.class,
    MixedContentTest.class,
    XMLHelperSaveTest.class,
    XMLHelperLoadTest.class,
    OppositeTest.class,
    DataTypesTest.class,
    XsdParsingTest.class,
    DataHelperTest.class,
    ReadOnlyTest.class,
    NullPointerTest.class,
    IsSetSetNullTest.class,
    SimpleTypeTest.class,
    NullableTest.class,
    DataObjectAccessorExceptionsTest.class,
    XmlWithoutSchemaTest.class,
    XPathAccessIndexMapsTest.class,
    BqlXmlTest.class,
    TestCreateDO.class,
    ExtendedInstanceClassTest.class,
    ElementAttributeFormDefaultTest.class,
    PropertyRenamingTest.class,
    SchemaTypesTest.class,
    XSDHelperTest.class,
    ScaTest.class,
    NoSchemaTest.class,
    SchemaLocationTest.class,
    Jira176OpenContentTest.class,
    RefineSequencedTypeTest.class,
    RefineNonSequencedTypeTest.class,
    TutorialTest.class,
    FacetTestByApi.class,
    SubstitutionGroupTest.class,
    ValuePropertyTest.class,
    NillableTest.class,
    RemoveTypesTest.class,
    KeysTest.class,
    SoapEncodingTest.class
})
public class Basic {
}
