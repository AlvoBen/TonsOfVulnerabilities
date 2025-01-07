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

import java.util.logging.Logger;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

import test.suite.SdoSuite.OptionalClasses;

import com.sap.sdo.testcase.external.schema.GenerateSchemaClassesTest;
import com.sap.sdo.testcase.internal.AbstractPropTypeTest;
import com.sap.sdo.testcase.internal.BuiltinTypeTest;
import com.sap.sdo.testcase.internal.ChangeSummaryTest;
import com.sap.sdo.testcase.internal.ClassLoader2Test;
import com.sap.sdo.testcase.internal.ClassLoaderTest;
import com.sap.sdo.testcase.internal.CombiClassLoaderTest;
import com.sap.sdo.testcase.internal.CovarianceTest;
import com.sap.sdo.testcase.internal.DataGraphSerializationTest;
import com.sap.sdo.testcase.internal.DataObjectDeleteTest;
import com.sap.sdo.testcase.internal.DelegatingDataObjectTest;
import com.sap.sdo.testcase.internal.DelegatingPropMultiValueTest;
import com.sap.sdo.testcase.internal.DelegatingPropSingleValueTest;
import com.sap.sdo.testcase.internal.DelegatorTest;
import com.sap.sdo.testcase.internal.FacetsTest;
import com.sap.sdo.testcase.internal.HelperContextSerializationTest;
import com.sap.sdo.testcase.internal.HelperContextTest;
import com.sap.sdo.testcase.internal.HelperProviderTest;
import com.sap.sdo.testcase.internal.InputSourceTest;
import com.sap.sdo.testcase.internal.InterfaceGeneratorTest;
import com.sap.sdo.testcase.internal.J1DemoBehaviorTest;
import com.sap.sdo.testcase.internal.JavaTypeFactoryTest;
import com.sap.sdo.testcase.internal.JavaVisitorTest;
import com.sap.sdo.testcase.internal.MetaDataObjectTest;
import com.sap.sdo.testcase.internal.NameConverterTest;
import com.sap.sdo.testcase.internal.NewPerformanceTest;
import com.sap.sdo.testcase.internal.OrphanHolderTest;
import com.sap.sdo.testcase.internal.OtherTypePropsTest;
import com.sap.sdo.testcase.internal.ParallelSaveTest;
import com.sap.sdo.testcase.internal.ProjectedSequenceTest;
import com.sap.sdo.testcase.internal.ProjectionPropSingleValueTest;
import com.sap.sdo.testcase.internal.ProjectionTest;
import com.sap.sdo.testcase.internal.QNameURINameTest;
import com.sap.sdo.testcase.internal.ReadOnlyExtendedTest;
import com.sap.sdo.testcase.internal.ReferenceTest;
import com.sap.sdo.testcase.internal.ReverseRenamingTest;
import com.sap.sdo.testcase.internal.SapDataHelperTest;
import com.sap.sdo.testcase.internal.SchemaResolverTest;
import com.sap.sdo.testcase.internal.SdoStreamReaderTest;
import com.sap.sdo.testcase.internal.SimpleTypeTest;
import com.sap.sdo.testcase.internal.StreamReaderCompareTest;
import com.sap.sdo.testcase.internal.TypeHelperTest;
import com.sap.sdo.testcase.internal.TypedSDOTest;
import com.sap.sdo.testcase.internal.UriNamePairTest;
import com.sap.sdo.testcase.internal.ValidationTest;
import com.sap.sdo.testcase.internal.VisitorExceptionTest;
import com.sap.sdo.testcase.internal.WeakValueHashMapTest;
import com.sap.sdo.testcase.internal.WrapperConvertTest;
import com.sap.sdo.testcase.internal.WsPerformanceTest;
import com.sap.sdo.testcase.internal.XMLStreamReaderTest;
import com.sap.sdo.testcase.internal.XPathAccessIndexMapsTest;
import com.sap.sdo.testcase.internal.XiMessage30Test;
import com.sap.sdo.testcase.internal.XmlBehaviorTest;
import com.sap.sdo.testcase.internal.XmlParseExceptionTest;
import com.sap.sdo.testcase.internal.XmlProjectionMappingStrategyTest;
import com.sap.sdo.testcase.internal.XmlStreamTest;
import com.sap.sdo.testcase.internal.XsdGenerationTest;
import com.sap.sdo.testcase.internal.XsdVisitorTest;
import com.sap.sdo.testcase.internal.merge.IntegrationHandlerTest;
import com.sap.sdo.testcase.internal.merge.IntegrationHandlerTest2;
import com.sap.sdo.testcase.internal.pojo.PojoChangeSummaryTest;
import com.sap.sdo.testcase.internal.pojo.PojoDasTest;
import com.sap.sdo.testcase.internal.pojo.PojoDataStrategyTest;
import com.sap.sdo.testcase.internal.pojo.PojoEnhancerTest;
import com.sap.sdo.testcase.internal.pojo.XmlProjectionsTest;
import com.sap.sdo.testcase.internal.propindex.PropIndexAnnoTest;

@RunWith(SdoSuite.class)
@SuiteClasses({
    Basic.class,
    BuiltinTypeTest.class,
    ChangeSummaryTest.class,
    DataGraphSerializationTest.class,
    DataObjectDeleteTest.class,
    J1DemoBehaviorTest.class,
    JavaVisitorTest.class,
    SimpleTypeTest.class,
    TypedSDOTest.class,
    TypeHelperTest.class,
    XsdVisitorTest.class,
    ReadOnlyExtendedTest.class,
    XPathAccessIndexMapsTest.class,
    PojoDasTest.class,
    PojoChangeSummaryTest.class,
    VisitorExceptionTest.class,
    MetaDataObjectTest.class,
    PropIndexAnnoTest.class,
    AbstractPropTypeTest.class,
    OtherTypePropsTest.class,
    CovarianceTest.class,
    XsdGenerationTest.class,
    FacetsTest.class,
    ReferenceTest.class,
    SchemaResolverTest.class,
    XMLStreamReaderTest.class,
    XmlStreamTest.class,
    HelperProviderTest.class,
    UriNamePairTest.class,
    XmlParseExceptionTest.class,
    XmlBehaviorTest.class,
    NameConverterTest.class,
    JavaTypeFactoryTest.class,
    PojoDataStrategyTest.class,
    HelperContextTest.class,
    DelegatingDataObjectTest.class,
    WrapperConvertTest.class,
    InterfaceGeneratorTest.class,
    WsPerformanceTest.class,
    ValidationTest.class,
    SapDataHelperTest.class,
    DelegatorTest.class,
    QNameURINameTest.class,
    DelegatingPropMultiValueTest.class,
    DelegatingPropSingleValueTest.class,
    ProjectedSequenceTest.class,
    ProjectionPropSingleValueTest.class,
    ProjectionTest.class,
    CombiClassLoaderTest.class,
    HelperContextSerializationTest.class,
    XmlProjectionsTest.class,
    PojoEnhancerTest.class,
    ReverseRenamingTest.class,
    XiMessage30Test.class,
    SdoStreamReaderTest.class,
    StreamReaderCompareTest.class,
    NewPerformanceTest.class,
    OrphanHolderTest.class,
    InputSourceTest.class,
    WeakValueHashMapTest.class,
    XmlProjectionMappingStrategyTest.class,
    ClassLoaderTest.class,
    ParallelSaveTest.class,
    IntegrationHandlerTest.class,
    IntegrationHandlerTest2.class
})
@OptionalClasses({
    ClassLoader2Test.class,
    GenerateSchemaClassesTest.class
})
public class BasicInternal {
    public final static Logger logger = Logger.getLogger(BasicInternal.class.getName());

    public static void main(String[] args) {

        Result testResult = JUnitCore.runClasses(BasicInternal.class);

        int successfulRuns = testResult.getRunCount() - testResult.getFailureCount(); // - testResult.getErrorCount();
        System.out.println("Runs: " + successfulRuns + "/" + testResult.getRunCount());
        //System.out.println("Errors: " + testResult.errorCount());
        System.out.println("Failures: " + testResult.getFailureCount());
    }
}
