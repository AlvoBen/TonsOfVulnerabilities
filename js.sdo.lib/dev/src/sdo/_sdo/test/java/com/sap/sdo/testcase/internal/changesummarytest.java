package com.sap.sdo.testcase.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.impl.objects.DataObjectDecorator;
import com.sap.sdo.impl.objects.GenericDataObject;
import com.sap.sdo.impl.objects.ValidationHelper;
import com.sap.sdo.impl.objects.ValidationHelper.ValidationException;
import com.sap.sdo.impl.types.builtin.ChangeSummaryType;
import com.sap.sdo.testcase.SdoTestCase;
import com.sap.sdo.testcase.typefac.DataGraphRootIntf;
import com.sap.sdo.testcase.typefac.EmployeeSdo;
import com.sap.sdo.testcase.typefac.EmployeeUtilizationSdo;
import com.sap.sdo.testcase.typefac.LoggingRootIntf;
import com.sap.sdo.testcase.typefac.OpenInterface;
import com.sap.sdo.testcase.typefac.OpenSequencedInterface;
import com.sap.sdo.testcase.typefac.ProjectSdo;
import com.sap.sdo.testcase.typefac.SequencedOppositeIntf;
import com.sap.sdo.testcase.typefac.SimpleContainedIntf;
import com.sap.sdo.testcase.typefac.SimpleContainingIntf;

import commonj.sdo.ChangeSummary;
import commonj.sdo.DataGraph;
import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Sequence;
import commonj.sdo.Type;
import commonj.sdo.ChangeSummary.Setting;
import commonj.sdo.helper.HelperContext;
import commonj.sdo.helper.XMLDocument;

public class ChangeSummaryTest extends SdoTestCase {

    /**
     * @param pHelperContext
     */
    public ChangeSummaryTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    @Test
    public void testGetChangeSummaryWithGraph() throws ValidationException {
        DataGraph dataGraph = (DataGraph)_helperContext.getDataFactory().create("commonj.sdo","DataGraphType");
        Type type = _helperContext.getTypeHelper().getType(SequencedOppositeIntf.class);
        dataGraph.createRootObject(type);

        DataObject level1Object = dataGraph.getRootObject();
        assertSame(type, level1Object.getType());
        assertSame(dataGraph, level1Object.getDataGraph());
        assertSame(level1Object, level1Object.getRootObject());
        //This is not in spec.
        assertSame(dataGraph, level1Object.getContainer());

        ChangeSummary changeSummary = dataGraph.getChangeSummary();

        assertSame(level1Object, changeSummary.getRootObject());
        assertSame(changeSummary, level1Object.getChangeSummary());

        ValidationHelper.validateTree((DataObject)dataGraph);
    }

    @Test
    public void testChangeSummaryMultiValue() throws ValidationException {

        DataGraph dataGraph = (DataGraph)_helperContext.getDataFactory().create("commonj.sdo","DataGraphType");
        Type type = _helperContext.getTypeHelper().getType(SequencedOppositeIntf.class);
        DataObject level0Object = dataGraph.createRootObject(type);

        DataObject level1aObject = _helperContext.getDataFactory().create(SequencedOppositeIntf.class);
        DataObject level1bObject = _helperContext.getDataFactory().create(SequencedOppositeIntf.class);
        DataObject level2aObject = _helperContext.getDataFactory().create(SequencedOppositeIntf.class);
        DataObject level2bObject = _helperContext.getDataFactory().create(SequencedOppositeIntf.class);

        ChangeSummary changeSummary = level0Object.getChangeSummary();

        ((SequencedOppositeIntf)level0Object).getMv().add((SequencedOppositeIntf)level1aObject);
        ((SequencedOppositeIntf)level0Object).getMv().add((SequencedOppositeIntf)level1bObject);

        ((SequencedOppositeIntf)level0Object).setName("level0Object");
        ((SequencedOppositeIntf)level1aObject).setName("level1aObject");
        ((SequencedOppositeIntf)level1bObject).setName("level1bObject");
        ((SequencedOppositeIntf)level2aObject).setName("level2aObject");
        ((SequencedOppositeIntf)level2bObject).setName("level2bObject");

        changeSummary.beginLogging();

        assertSame(changeSummary, level0Object.getChangeSummary());
        assertSame(changeSummary, level1aObject.getChangeSummary());
        assertSame(changeSummary, level1bObject.getChangeSummary());

        assertEquals(0, changeSummary.getOldValues(level0Object).size());

        ((SequencedOppositeIntf)level0Object).getMv().add((SequencedOppositeIntf)level2aObject);
        ((SequencedOppositeIntf)level0Object).getMv().add((SequencedOppositeIntf)level2bObject);
        ((SequencedOppositeIntf)level0Object).getMv().remove(level1aObject);

        assertEquals(1, changeSummary.getOldValues(level0Object).size());

        Setting setting = (Setting)changeSummary.getOldValues(level0Object).get(0);

        assertEquals("mv", setting.getProperty().getName());

        List mvList = (List)setting.getValue();

        assertEquals(level1aObject, mvList.get(0));
        assertEquals(level1bObject, mvList.get(1));

        assertEquals(null, level1aObject.getContainer());
        assertEquals(level0Object, changeSummary.getOldContainer(level1aObject));

        assertEquals(level0Object, level2aObject.getContainer());
        assertEquals(level0Object, changeSummary.getOldContainer(level2aObject));
        ValidationHelper.validateTree(level0Object);

    }

    @Test
    public void testChangeSummaryDelete() throws ValidationException {

        DataGraph dataGraph = (DataGraph)_helperContext.getDataFactory().create("commonj.sdo","DataGraphType");
        Type rootType = _helperContext.getTypeHelper().getType(DataGraphRootIntf.class);
        DataGraphRootIntf rootObject = (DataGraphRootIntf)dataGraph.createRootObject(rootType);

        DataObject level0Object = _helperContext.getDataFactory().create(SequencedOppositeIntf.class);
        DataObject level1aObject = _helperContext.getDataFactory().create(SequencedOppositeIntf.class);
        DataObject level1bObject = _helperContext.getDataFactory().create(SequencedOppositeIntf.class);
        DataObject level2aObject = _helperContext.getDataFactory().create(SequencedOppositeIntf.class);
        DataObject level2bObject = _helperContext.getDataFactory().create(SequencedOppositeIntf.class);

        ((SequencedOppositeIntf)level0Object).setName("level0Object");
        ((SequencedOppositeIntf)level1aObject).setName("level1aObject");
        ((SequencedOppositeIntf)level1bObject).setName("level1bObject");
        ((SequencedOppositeIntf)level2aObject).setName("level2aObject");
        ((SequencedOppositeIntf)level2bObject).setName("level2bObject");

        ((SequencedOppositeIntf)level0Object).getMv().add((SequencedOppositeIntf)level1aObject);
        ((SequencedOppositeIntf)level0Object).getMv().add((SequencedOppositeIntf)level1bObject);
        ((SequencedOppositeIntf)level1aObject).getMv().add((SequencedOppositeIntf)level2aObject);
        ((SequencedOppositeIntf)level1bObject).getMv().add((SequencedOppositeIntf)level2bObject);

        rootObject.setRoot((SequencedOppositeIntf)level0Object);

        ChangeSummary changeSummary = level0Object.getChangeSummary();
        changeSummary.beginLogging();

        level1bObject.delete();

        assertEquals(false, changeSummary.isDeleted(level0Object));
        assertEquals(true, changeSummary.isModified(level0Object));
        assertEquals(true, changeSummary.isDeleted(level1bObject));
        assertEquals(true, changeSummary.isDeleted(level2bObject));

        ValidationHelper.validateTree(level0Object);

        assertNull(level1bObject.getContainer());
        assertNull(level2bObject.getContainer());

        assertEquals(level0Object, changeSummary.getOldContainer(level1bObject));
        assertEquals(level1bObject, changeSummary.getOldContainer(level2bObject));

        List changes = changeSummary.getChangedDataObjects();
        assertEquals(3, changes.size());
        assertTrue(changes.contains(level0Object));
        assertTrue(changes.contains(level1bObject));
        assertTrue(changes.contains(level2bObject));

        // test if level1bObject has the old state
        DataObject oldStateLevel1bObject = (DataObject)changes.get(changes.indexOf(level1bObject));

        assertEquals(level0Object, oldStateLevel1bObject.getContainer());
        assertEquals(true, oldStateLevel1bObject.isSet("mv"));

        DataObject oldStateLevel2bObject = (DataObject)oldStateLevel1bObject.getList("mv").get(0);

        assertEquals(level2bObject, oldStateLevel2bObject);
        assertEquals(oldStateLevel1bObject, oldStateLevel2bObject.getContainer());

    }

    @Test
    public void testNonSequencedSingleValue() throws ValidationException {

        LoggingRootIntf loggingRoot = (LoggingRootIntf)_helperContext.getDataFactory().create(LoggingRootIntf.class);
        DataObject level0Object = _helperContext.getDataFactory().create(SimpleContainingIntf.class);

        loggingRoot.setSimpleContainingIntf((SimpleContainingIntf)level0Object);
        ChangeSummary changeSummary = level0Object.getChangeSummary();
        changeSummary.beginLogging();
        ((SimpleContainingIntf)level0Object).setX("level0Object");
        assertEquals(true, changeSummary.isModified(level0Object));

        changeSummary.endLogging();
        ((SimpleContainingIntf)level0Object).setX("level0Object");
        changeSummary.beginLogging();

        DataObject rootObjectCopy = _helperContext.getCopyHelper().copy(level0Object);
        assertEquals(false, changeSummary.isModified(level0Object));
        ((SimpleContainingIntf)level0Object).setX("new");
        assertEquals(true, changeSummary.isModified(level0Object));
        assertEquals(false, _helperContext.getEqualityHelper().equal(level0Object, rootObjectCopy));

        changeSummary.undoChanges();
        assertEquals(0, changeSummary.getChangedDataObjects().size());

        assertEquals(true, _helperContext.getEqualityHelper().equal(level0Object, rootObjectCopy));

        assertSame(changeSummary, level0Object.getChangeSummary());
        ValidationHelper.validateTree(level0Object);

    }

    @Test
    public void testUndoWithGraph() throws ValidationException, IOException {
        DataGraph dataGraph = (DataGraph)_helperContext.getDataFactory().create("commonj.sdo","DataGraphType");
        Type rootType = _helperContext.getTypeHelper().getType(DataGraphRootIntf.class);
        Type type = _helperContext.getTypeHelper().getType(SequencedOppositeIntf.class);
        dataGraph.createRootObject(rootType);

        DataObject rootObject = dataGraph.getRootObject();
        ChangeSummary changeSummary = dataGraph.getChangeSummary();
        SequencedOppositeIntf level1Object = (SequencedOppositeIntf)_helperContext.getDataFactory().create(type);
        ((DataGraphRootIntf)rootObject).setRoot(level1Object);

        SequencedOppositeIntf level2aObject = (SequencedOppositeIntf)_helperContext.getDataFactory().create(type);
        SequencedOppositeIntf level2bObject = (SequencedOppositeIntf)_helperContext.getDataFactory().create(type);

        level1Object.setName("level1Object");
        level2aObject.setName("level2aObject");
        level2bObject.setName("level2bObject");

        level1Object.getMv().add(level2aObject);
        level1Object.getMv().add(level2bObject);

        DataObject rootObjectCopy = _helperContext.getCopyHelper().copy(rootObject);

        changeSummary.beginLogging();

        assertTrue("\nSource: " + rootObject + "\nTarget: " + rootObjectCopy,
            _helperContext.getEqualityHelper().equal(rootObject, rootObjectCopy));

        level1Object.setName("level1ObjectChanged");
        level2aObject.setName("level2aObjectChanged");
        level2bObject.setName("level2bObjectChanged");

        assertFalse("\nSource: " + rootObject + "\nTarget: " + rootObjectCopy,
            _helperContext.getEqualityHelper().equal(rootObject, rootObjectCopy));

        changeSummary.undoChanges();

        assertTrue("\nSource: " + rootObject + "\nTarget: " + rootObjectCopy,
            _helperContext.getEqualityHelper().equal(rootObject, rootObjectCopy));

        changeSummary.beginLogging();

        assertSame(level1Object, ((DataObject)level2bObject).getContainer());

        level2aObject.getMv().add(level2bObject);

        assertSame(level2aObject, ((DataObject)level2bObject).getContainer());
        assertSame(level2aObject, level2bObject.getSv());

        changeSummary.undoChanges();

        assertSame(level1Object, ((DataObject)level2bObject).getContainer());
        assertSame(level1Object, level2bObject.getSv());

        assertTrue("\nSource: " + rootObject + "\nTarget: " + rootObjectCopy,
            _helperContext.getEqualityHelper().equal(rootObject, rootObjectCopy));

        changeSummary.beginLogging();

        level2bObject.setSv(level2aObject);

        assertSame(level2aObject, ((DataObject)level2bObject).getContainer());
        assertSame(level2aObject, level2bObject.getSv());

        String xmlChanged = _helperContext.getXMLHelper().save((DataObject)dataGraph, "commonj.sdo", "datagraph");

        changeSummary.undoChanges();

        assertSame(level1Object, ((DataObject)level2bObject).getContainer());
        assertSame(level1Object, level2bObject.getSv());

        assertTrue("\nSource: " + rootObject + "\nTarget: " + rootObjectCopy,
            _helperContext.getEqualityHelper().equal(rootObject, rootObjectCopy));
        ValidationHelper.validateTree(rootObject);

        XMLDocument xmlDocument = _helperContext.getXMLHelper().load(xmlChanged);
        StringWriter xmlChangedReloaded = new StringWriter();
        _helperContext.getXMLHelper().save(xmlDocument, xmlChangedReloaded, null);
        assertEquals(xmlChanged, xmlChangedReloaded.toString());


        DataObject parsedRoot = xmlDocument.getRootObject();
        parsedRoot.getChangeSummary().undoChanges();

        String xmlOriginal = _helperContext.getXMLHelper().save((DataObject)dataGraph, "commonj.sdo", "datagraph");
        String xmlChangedUndo = _helperContext.getXMLHelper().save(parsedRoot, "commonj.sdo", "datagraph");
        assertEquals(xmlOriginal, xmlChangedUndo);

        DataObject parsedLevel1Object = ((DataGraph)parsedRoot).getRootObject().getDataObject("root");
        DataObject parsedLevel2bObject = parsedLevel1Object.getDataObject("mv.1");
        assertSame(parsedLevel1Object, parsedLevel2bObject.getDataObject("sv"));
        assertSame(parsedLevel1Object, parsedLevel2bObject.getContainer());


        assertTrue("\nSource: " + parsedRoot + "\nTarget: " + dataGraph,
            _helperContext.getEqualityHelper().equal(parsedRoot, (DataObject)dataGraph));

    }

    @Test
    public void testUndoWithUnsetOpposites() {
        HelperContext context = _helperContext;

        // Create an empty EmployeeUtilizationSdo instance.
        EmployeeUtilizationSdo employeeUtilizationSdo =
            (EmployeeUtilizationSdo)context.getDataFactory().create(EmployeeUtilizationSdo.class);

        // Get the live list of the projects property.
        List<ProjectSdo> allProjectSdos = employeeUtilizationSdo.getProjects();
        for (int i=0; i<3; ++i) {
            // Create an empty ProjectSdo instance.
            ProjectSdo projectSdo =
                (ProjectSdo)context.getDataFactory().create(ProjectSdo.class);
            // Fill the simple values.
            projectSdo.setDescription("description " + i);
            projectSdo.setStartDate(new Date());
            projectSdo.setEndDate(new Date());
            projectSdo.setProjectId(i);
            projectSdo.setStatus(i);
            projectSdo.setTitle("title " + i);
            projectSdo.setVersion(1);

            // Add to the live list of the projects property.
            allProjectSdos.add(projectSdo);
        }

        // Get the live list of the employees property.
        List<EmployeeSdo> allEmployeeSdos = employeeUtilizationSdo.getEmployees();
        for (int i=0; i<3; ++i) {
            // Create an empty ProjectSdo instance.
            EmployeeSdo employeeSdo =
                (EmployeeSdo)context.getDataFactory().create(EmployeeSdo.class);
            // Fill the simple values.
            employeeSdo.setEmployeeId(i);
            employeeSdo.setEmail("mail " + i);
            employeeSdo.setFirstName("firstname " + i);
            employeeSdo.setLastName("lastname " + i);
            employeeSdo.setSalutation("salutation " + i);
            employeeSdo.setVersion(1);

            // Add to the live list of the employees property.
            allEmployeeSdos.add(employeeSdo);
        }

        // Create a DataGraph object.
        DataObject dataGraph = _helperContext.getDataFactory().create("commonj.sdo",
            "DataGraphType");
        // Set the EmployeeUtilizationSdo as root and start the ChangeSummary.
        dataGraph.set("employeeUtilization", employeeUtilizationSdo);
        dataGraph.getChangeSummary().beginLogging();

        String start = context.getXMLHelper().save(
            (DataObject)((DataObject)employeeUtilizationSdo).getDataGraph(),
            null,
            "datagraph");

        ProjectSdo project = employeeUtilizationSdo.getProjects().get(0);
        project.getEmployees().add(employeeUtilizationSdo.getEmployees().get(0));
        project.getEmployees().add(employeeUtilizationSdo.getEmployees().get(1));

        String linked = context.getXMLHelper().save(
            (DataObject)((DataObject)employeeUtilizationSdo).getDataGraph(),
            null,
            "datagraph");

        dataGraph.getChangeSummary().undoChanges();

        String undo = context.getXMLHelper().save(
            (DataObject)((DataObject)employeeUtilizationSdo).getDataGraph(),
            null,
            "datagraph");

        assertEquals(start, undo);

        project = employeeUtilizationSdo.getProjects().get(0);
        project.getEmployees().add(employeeUtilizationSdo.getEmployees().get(0));
        project.getEmployees().add(employeeUtilizationSdo.getEmployees().get(1));

        String linkedAgain = context.getXMLHelper().save(
            (DataObject)((DataObject)employeeUtilizationSdo).getDataGraph(),
            null,
            "datagraph");

        assertEquals(linked, linkedAgain);
    }

    @Test
    public void testUndoWithExistingOpposites() {
        HelperContext context = _helperContext;

        // Create an empty EmployeeUtilizationSdo instance.
        EmployeeUtilizationSdo employeeUtilizationSdo =
            (EmployeeUtilizationSdo)context.getDataFactory().create(EmployeeUtilizationSdo.class);

        // Get the live list of the projects property.
        List<ProjectSdo> allProjectSdos = employeeUtilizationSdo.getProjects();
        for (int i=0; i<3; ++i) {
            // Create an empty ProjectSdo instance.
            ProjectSdo projectSdo =
                (ProjectSdo)context.getDataFactory().create(ProjectSdo.class);
            // Fill the simple values.
            projectSdo.setDescription("description " + i);
            projectSdo.setStartDate(new Date());
            projectSdo.setEndDate(new Date());
            projectSdo.setProjectId(i);
            projectSdo.setStatus(i);
            projectSdo.setTitle("title " + i);
            projectSdo.setVersion(1);

            // Add to the live list of the projects property.
            allProjectSdos.add(projectSdo);
        }

        // Get the live list of the employees property.
        List<EmployeeSdo> allEmployeeSdos = employeeUtilizationSdo.getEmployees();
        for (int i=0; i<3; ++i) {
            // Create an empty ProjectSdo instance.
            EmployeeSdo employeeSdo =
                (EmployeeSdo)context.getDataFactory().create(EmployeeSdo.class);
            // Fill the simple values.
            employeeSdo.setEmployeeId(i);
            employeeSdo.setEmail("mail " + i);
            employeeSdo.setFirstName("firstname " + i);
            employeeSdo.setLastName("lastname " + i);
            employeeSdo.setSalutation("salutation " + i);
            employeeSdo.setVersion(1);

            // Add to the live list of the employees property.
            allEmployeeSdos.add(employeeSdo);

            employeeSdo.getProjects().add(employeeUtilizationSdo.getProjects().get(2));
        }
        DataObject employeeUtilization = (DataObject)employeeUtilizationSdo;
        String xml = _helperContext.getXMLHelper().save(employeeUtilization, employeeUtilization.getType().getURI(), employeeUtilization.getType().getName());
        System.out.println(xml);


        // Create a DataGraph object.
        DataObject dataGraph = _helperContext.getDataFactory().create("commonj.sdo",
            "DataGraphType");
        // Set the EmployeeUtilizationSdo as root and start the ChangeSummary.
        dataGraph.set("employeeUtilization", employeeUtilizationSdo);
        dataGraph.getChangeSummary().beginLogging();

        String start = context.getXMLHelper().save(
            (DataObject)((DataObject)employeeUtilizationSdo).getDataGraph(),
            null,
            "datagraph");

        ProjectSdo project = employeeUtilizationSdo.getProjects().get(0);
        project.getEmployees().add(employeeUtilizationSdo.getEmployees().get(0));
        project.getEmployees().add(employeeUtilizationSdo.getEmployees().get(1));

        String linked = context.getXMLHelper().save(
            (DataObject)((DataObject)employeeUtilizationSdo).getDataGraph(),
            null,
            "datagraph");

        dataGraph.getChangeSummary().undoChanges();

        String undo = context.getXMLHelper().save(
            (DataObject)((DataObject)employeeUtilizationSdo).getDataGraph(),
            null,
            "datagraph");

        assertEquals(start, undo);

        project = employeeUtilizationSdo.getProjects().get(0);
        project.getEmployees().add(employeeUtilizationSdo.getEmployees().get(0));
        project.getEmployees().add(employeeUtilizationSdo.getEmployees().get(1));

        String linkedAgain = context.getXMLHelper().save(
            (DataObject)((DataObject)employeeUtilizationSdo).getDataGraph(),
            null,
            "datagraph");

        assertEquals(linked, linkedAgain);
    }

    @Test
    public void testDataGraphWithDeleteNew() throws Exception {
        DataGraph dataGraph = (DataGraph)_helperContext.getDataFactory().create("commonj.sdo","DataGraphType");
        Type type = _helperContext.getTypeHelper().getType(DataGraphRootIntf.class);
        dataGraph.createRootObject(type);

        DataObject rootObject = dataGraph.getRootObject();
        assertEquals(type, rootObject.getType());
        assertSame(dataGraph, rootObject.getDataGraph());

        ChangeSummary changeSummary = dataGraph.getChangeSummary();

        assertSame(rootObject, changeSummary.getRootObject());
        assertSame(changeSummary, rootObject.getChangeSummary());

        SequencedOppositeIntf level1Object =
            (SequencedOppositeIntf)_helperContext.getDataFactory().create(
                _helperContext.getTypeHelper().getType(SequencedOppositeIntf.class));
        ((DataGraphRootIntf)rootObject).setRoot(level1Object);

        changeSummary.beginLogging();

        DataObject level2Object =
            _helperContext.getDataFactory().create(
                _helperContext.getTypeHelper().getType(SequencedOppositeIntf.class));

        level1Object.getMv().add((SequencedOppositeIntf)level2Object);

        level1Object.setName("name");
        level2Object.set("name", "a name");

        assertSame(rootObject, level2Object.getRootObject());
        assertSame(dataGraph, level2Object.getDataGraph());
        assertSame(changeSummary, level2Object.getChangeSummary());

        DataObject level3Object =
            _helperContext.getDataFactory().create(
                _helperContext.getTypeHelper().getType(SequencedOppositeIntf.class));

        level1Object.getMv().add((SequencedOppositeIntf)level3Object);

        level3Object.set("name", "third name");

        level2Object.delete();

        Sequence oldSeq = changeSummary.getOldSequence((DataObject)level1Object);

        assertEquals(0, oldSeq.size());
        ValidationHelper.validateTree(rootObject);
    }

    @Test
    public void testDataGraphWithDelete() throws Exception {
        DataGraph dataGraph = (DataGraph)_helperContext.getDataFactory().create("commonj.sdo","DataGraphType");
        Type type = _helperContext.getTypeHelper().getType(DataGraphRootIntf.class);
        dataGraph.createRootObject(type);

        DataObject rootObject = dataGraph.getRootObject();
        assertEquals(type, rootObject.getType());
        assertSame(dataGraph, rootObject.getDataGraph());

        ChangeSummary changeSummary = dataGraph.getChangeSummary();

        assertSame(rootObject, changeSummary.getRootObject());
        assertSame(changeSummary, rootObject.getChangeSummary());

        SequencedOppositeIntf level1Object =
            (SequencedOppositeIntf)_helperContext.getDataFactory().create(
                _helperContext.getTypeHelper().getType(SequencedOppositeIntf.class));
        ((DataGraphRootIntf)rootObject).setRoot(level1Object);

        DataObject level2Object =
            _helperContext.getDataFactory().create(
                _helperContext.getTypeHelper().getType(SequencedOppositeIntf.class));

        level1Object.getMv().add((SequencedOppositeIntf)level2Object);

        assertEquals(1, ((DataObject)level1Object).getSequence().size());
        changeSummary.beginLogging();

        assertEquals(1, changeSummary.getOldSequence((DataObject)level1Object).size());

        level1Object.setName("name");

        assertEquals(1, changeSummary.getOldSequence((DataObject)level1Object).size());

        level2Object.set("name", "a name");

        assertSame(rootObject, level2Object.getRootObject());
        assertSame(dataGraph, level2Object.getDataGraph());
        assertSame(changeSummary, level2Object.getChangeSummary());

        DataObject level3Object =
            _helperContext.getDataFactory().create(
                _helperContext.getTypeHelper().getType(SequencedOppositeIntf.class));

        level1Object.getMv().add((SequencedOppositeIntf)level3Object);

        level3Object.set("name", "third name");

        level2Object.delete();

        Sequence oldSeq = changeSummary.getOldSequence((DataObject)level1Object);

        assertEquals(1, oldSeq.size());
        Object oldValue = oldSeq.getValue(0);
        assertNotNull(oldValue);
        assertEquals(level2Object, oldValue);
        assertTrue(oldValue instanceof DataObjectDecorator);
        assertSame(
            ((DataObjectDecorator)level2Object).getInstance(),
            ((DataObjectDecorator)oldValue).getInstance());

        assertTrue(changeSummary.isDeleted(level2Object));

        List<DataObject> oldMv = (List<DataObject>)changeSummary.getOldValue((DataObject)level1Object, ((DataObject)level1Object).getProperty("mv")).getValue();
        assertTrue(oldMv.toString(), oldMv.contains(level2Object));

        List<DataObject> changedDataObjects = changeSummary.getChangedDataObjects();
        assertTrue(changedDataObjects.toString(), changedDataObjects.contains(level2Object));
        ValidationHelper.validateTree(rootObject);
    }

    @Test
    public void testCopyWithLogging() throws ValidationException {

        LoggingRootIntf loggingRoot = (LoggingRootIntf)_helperContext.getDataFactory().create(LoggingRootIntf.class);
        SimpleContainingIntf level0Object = (SimpleContainingIntf)_helperContext.getDataFactory().create(SimpleContainingIntf.class);
        SimpleContainedIntf level1Object = (SimpleContainedIntf)_helperContext.getDataFactory().create(SimpleContainedIntf.class);

        loggingRoot.setSimpleContainingIntf(level0Object);
        level0Object.setInner(level1Object);
        ChangeSummary changeSummary = loggingRoot.getChangeSummary();
        changeSummary.beginLogging();
        level0Object.setX("level0Object");
        level1Object.setName("level1Object");
        assertEquals(true, changeSummary.isModified((DataObject)level1Object));

        DataObject loggingRootCopy = _helperContext.getCopyHelper().copy((DataObject)loggingRoot);
        ChangeSummary changeSummaryCopy = loggingRootCopy.getChangeSummary();
        assertNotNull(changeSummaryCopy);
        assertNotSame(changeSummary, changeSummaryCopy);
        assertEquals(true, changeSummaryCopy.isLogging());

    }

    @Test
    public void testNonSequencedWithOpenProperties() throws ValidationException {

        LoggingRootIntf loggingRoot = (LoggingRootIntf)_helperContext.getDataFactory().create(LoggingRootIntf.class);
        DataObject rootObject = _helperContext.getDataFactory().create(OpenInterface.class);
        loggingRoot.setOpenInterface((OpenInterface)rootObject);
        ChangeSummary changeSummary = loggingRoot.getChangeSummary();

        ((OpenInterface)rootObject).setX("anX");
        rootObject.set("y", "aY");
        assertEquals("aY", rootObject.get("y"));

        changeSummary.beginLogging();
        rootObject.set("z", "aZ");
        assertEquals(true, changeSummary.isModified(rootObject));
        assertEquals(1, changeSummary.getOldValues(rootObject).size());
        Setting oldZSetting = (Setting)changeSummary.getOldValues(rootObject).get(0);
        Property zProperty = rootObject.getProperty("z");
        assertEquals(zProperty, oldZSetting.getProperty());
        assertEquals(false, oldZSetting.isSet());
        assertEquals(null, oldZSetting.getValue());
        oldZSetting = changeSummary.getOldValue(rootObject, zProperty);
        assertEquals(false, oldZSetting.isSet());
        assertEquals(null, oldZSetting.getValue());

        Property yProperty = rootObject.getProperty("y");
        assertEquals(true, rootObject.getInstanceProperties().contains(yProperty));
        rootObject.unset("y");
        assertEquals(false, rootObject.getInstanceProperties().contains(yProperty));
        assertEquals(null, rootObject.get(yProperty));

        assertEquals(2, changeSummary.getOldValues(rootObject).size());
        Setting oldYSetting = changeSummary.getOldValue(rootObject, yProperty);

        assertEquals(yProperty, oldYSetting.getProperty());
        assertEquals("aY", oldYSetting.getValue());

        changeSummary.undoChanges();

        assertEquals("aY", rootObject.get("y"));
        assertEquals(false, rootObject.getInstanceProperties().contains(zProperty));
        assertEquals(false, rootObject.isSet("z"));
        assertEquals(null, rootObject.getProperty("z"));

        ValidationHelper.validateTree(rootObject);
    }

    @Test
    public void testSequencedWithOpenProperties() throws ValidationException {
        LoggingRootIntf loggingRoot = (LoggingRootIntf)_helperContext.getDataFactory().create(LoggingRootIntf.class);
        DataObject rootObject = _helperContext.getDataFactory().create(OpenSequencedInterface.class);
        loggingRoot.setOpenSequencedInterface((OpenSequencedInterface)rootObject);
        ChangeSummary changeSummary = loggingRoot.getChangeSummary();

        ((OpenSequencedInterface)rootObject).setX("anX");
        rootObject.set("y", "aY");
        assertEquals("aY", rootObject.get("y"));

        changeSummary.beginLogging();
        assertEquals(0, changeSummary.getOldValues(rootObject).size());
        rootObject.set("z", "aZ");
        assertEquals(true, changeSummary.isModified(rootObject));
        assertEquals(changeSummary.getOldValues(rootObject).toString(), 1, changeSummary.getOldValues(rootObject).size());
        Setting oldZSetting = (Setting)changeSummary.getOldValues(rootObject).get(0);
        Property zProperty = rootObject.getProperty("z");
        assertEquals(zProperty, oldZSetting.getProperty());
        assertEquals(false, oldZSetting.isSet());
        assertEquals(null, oldZSetting.getValue());
        oldZSetting = changeSummary.getOldValue(rootObject, zProperty);
        assertEquals(false, oldZSetting.isSet());
        assertEquals(null, oldZSetting.getValue());

        Property yProperty = rootObject.getProperty("y");
        assertEquals(true, rootObject.getInstanceProperties().contains(yProperty));
        rootObject.unset("y");
        assertEquals(false, rootObject.getInstanceProperties().contains(yProperty));
        assertEquals(null, rootObject.get(yProperty));

        assertEquals(2, changeSummary.getOldValues(rootObject).size());
        Setting oldYSetting = changeSummary.getOldValue(rootObject, yProperty);

        assertEquals(yProperty, oldYSetting.getProperty());
        assertEquals("aY", oldYSetting.getValue());

        changeSummary.undoChanges();

        assertEquals("aY", rootObject.get("y"));
        assertEquals(false, rootObject.getInstanceProperties().contains(zProperty));
        assertEquals(false, rootObject.isSet("z"));
        assertEquals(null, rootObject.getProperty("z"));

        ValidationHelper.validateTree(rootObject);
    }

    @Test
    public void testSelfMadeSequencedCopy() throws ValidationException {
        LoggingRootIntf loggingRoot = (LoggingRootIntf)_helperContext.getDataFactory().create(LoggingRootIntf.class);
        DataObject originalObject = _helperContext.getDataFactory().create(OpenSequencedInterface.class);
        loggingRoot.setOpenSequencedInterface((OpenSequencedInterface)originalObject);
        ChangeSummary changeSummary = loggingRoot.getChangeSummary();

        ((OpenSequencedInterface)originalObject).setX("anX");
        originalObject.set("y", "aY");
        changeSummary.beginLogging();
        originalObject.set("z", "aZ");
        originalObject.unset("y");

        LoggingRootIntf loggingRootCopy = (LoggingRootIntf)_helperContext.getDataFactory().create(LoggingRootIntf.class);
        DataObject copyObject = _helperContext.getDataFactory().create(OpenSequencedInterface.class);
        GenericDataObject copyGdo = ((DataObjectDecorator)copyObject).getInstance();
        loggingRootCopy.setOpenSequencedInterface((OpenSequencedInterface)copyObject);
        ChangeSummary copyChangeSummary = loggingRootCopy.getChangeSummary();

        Sequence originalSequence = originalObject.getSequence();
        for (int i = 0; i < originalSequence.size(); i++) {
            copyGdo.addToSequenceWithoutCheck(originalSequence.getProperty(i), originalSequence.getValue(i));
        }

        Sequence originalOldSequence = changeSummary.getOldSequence(originalObject);
        for (int i = 0; i < originalOldSequence.size(); i++) {
            copyGdo.addToOldSequenceWithoutCheck(originalOldSequence.getProperty(i), originalOldSequence.getValue(i));
        }

        ValidationHelper.validateTree(copyObject);

        assertEquals(true, _helperContext.getEqualityHelper().equal(originalObject, copyObject));

        originalObject.getChangeSummary().undoChanges();
        copyObject.getChangeSummary().undoChanges();

        assertEquals(true, _helperContext.getEqualityHelper().equal(originalObject, copyObject));

        ValidationHelper.validateTree(originalObject);
        ValidationHelper.validateTree(copyObject);
    }

    @Test
    public void testSelfMadeSequencedCopy2() throws ValidationException {
        LoggingRootIntf loggingRoot = (LoggingRootIntf)_helperContext.getDataFactory().create(LoggingRootIntf.class);
        DataObject originalObject = _helperContext.getDataFactory().create(OpenSequencedInterface.class);
        loggingRoot.setOpenSequencedInterface((OpenSequencedInterface)originalObject);
        ChangeSummary changeSummary = loggingRoot.getChangeSummary();

        assertEquals(false, originalObject.isSet("x"));

        originalObject.set("y", "aY");
        changeSummary.beginLogging();
        ((OpenSequencedInterface)originalObject).setX("anX");

        originalObject.set("z", "aZ");
        originalObject.unset("y");

        LoggingRootIntf loggingRootCopy = (LoggingRootIntf)_helperContext.getDataFactory().create(LoggingRootIntf.class);
        DataObject copyObject = _helperContext.getDataFactory().create(OpenSequencedInterface.class);
        GenericDataObject copyGdo = ((DataObjectDecorator)copyObject).getInstance();
        loggingRootCopy.setOpenSequencedInterface((OpenSequencedInterface)copyObject);
        ChangeSummary copyChangeSummary = loggingRootCopy.getChangeSummary();

        Sequence originalSequence = originalObject.getSequence();
        for (int i = 0; i < originalSequence.size(); i++) {
            copyGdo.addToSequenceWithoutCheck(originalSequence.getProperty(i), originalSequence.getValue(i));
        }

        Sequence originalOldSequence = changeSummary.getOldSequence(originalObject);
        for (int i = 0; i < originalOldSequence.size(); i++) {
            copyGdo.addToOldSequenceWithoutCheck(originalOldSequence.getProperty(i), originalOldSequence.getValue(i));
        }

        ValidationHelper.validateTree(copyObject);

        assertEquals(true, _helperContext.getEqualityHelper().equal(originalObject, copyObject));

        assertEquals(true, originalObject.isSet("x"));
        assertEquals(true, copyObject.isSet("x"));

        originalObject.getChangeSummary().undoChanges();
        copyObject.getChangeSummary().undoChanges();

        assertEquals(true, _helperContext.getEqualityHelper().equal(originalObject, copyObject));

        ValidationHelper.validateTree(originalObject);
        ValidationHelper.validateTree(copyObject);

        assertEquals(false, originalObject.isSet("x"));
        assertEquals(false, copyObject.isSet("x"));

    }

    @Test
    public void testSelfMadeNonSequencedCopy() throws ValidationException {
        LoggingRootIntf loggingRoot = (LoggingRootIntf)_helperContext.getDataFactory().create(LoggingRootIntf.class);
        DataObject originalObject = _helperContext.getDataFactory().create(OpenInterface.class);
        loggingRoot.setOpenInterface((OpenInterface)originalObject);
        ChangeSummary changeSummary = loggingRoot.getChangeSummary();

        ((OpenInterface)originalObject).setX("anX");
        originalObject.set("y", "aY");
        changeSummary.beginLogging();
        originalObject.set("z", "aZ");
        originalObject.unset("y");

        LoggingRootIntf loggingRootCopy = (LoggingRootIntf)_helperContext.getDataFactory().create(LoggingRootIntf.class);
        DataObject copyObject = _helperContext.getDataFactory().create(OpenInterface.class);
        GenericDataObject copyGdo = ((DataObjectDecorator)copyObject).getInstance();
        loggingRootCopy.setOpenInterface((OpenInterface)copyObject);
        ChangeSummary copyChangeSummary = loggingRootCopy.getChangeSummary();

        List<Property> properties = originalObject.getInstanceProperties();
        for (Property property: properties) {
            if (originalObject.isSet(property)) {
                Object value = originalObject.get(property);
                copyGdo.setPropertyWithoutCheck(property, value);
            }
        }

        List<Setting> oldSettings = changeSummary.getOldValues(originalObject);
        for (Setting setting: oldSettings) {
            assertNotNull(setting);
            if (setting.isSet()) {
                copyGdo.setOldPropertyWithoutCheck(setting.getProperty(), setting.getValue());
            } else {
                copyGdo.setOldPropertyWithoutCheck(setting.getProperty(), GenericDataObject.UNSET);
            }

        }

        List<Setting> oldSettingsTest = copyChangeSummary.getOldValues(copyObject);
        for (Setting setting: oldSettings) {
            assertNotNull(setting);
        }

        ValidationHelper.validateTree(copyObject);

        assertEquals(true, _helperContext.getEqualityHelper().equal(originalObject, copyObject));

        originalObject.getChangeSummary().undoChanges();
        copyObject.getChangeSummary().undoChanges();

        assertEquals(true, _helperContext.getEqualityHelper().equal(originalObject, copyObject));

        ValidationHelper.validateTree(originalObject);
        ValidationHelper.validateTree(copyObject);
    }

    @Test
    public void testSelfMadeNonSequencedCopy2() throws ValidationException {
        LoggingRootIntf loggingRoot = (LoggingRootIntf)_helperContext.getDataFactory().create(LoggingRootIntf.class);
        DataObject originalObject = _helperContext.getDataFactory().create(OpenInterface.class);
        loggingRoot.setOpenInterface((OpenInterface)originalObject);
        ChangeSummary changeSummary = loggingRoot.getChangeSummary();

        assertEquals(false, originalObject.isSet("x"));

        originalObject.set("y", "aY");
        changeSummary.beginLogging();
        ((OpenInterface)originalObject).setX("anX");

        originalObject.set("z", "aZ");
        originalObject.unset("y");

        LoggingRootIntf loggingRootCopy = (LoggingRootIntf)_helperContext.getDataFactory().create(LoggingRootIntf.class);
        DataObject copyObject = _helperContext.getDataFactory().create(OpenInterface.class);
        GenericDataObject copyGdo = ((DataObjectDecorator)copyObject).getInstance();
        loggingRootCopy.setOpenInterface((OpenInterface)copyObject);
        ChangeSummary copyChangeSummary = loggingRootCopy.getChangeSummary();

        List<Property> properties = originalObject.getInstanceProperties();
        for (Property property: properties) {
            if (originalObject.isSet(property)) {
                Object value = originalObject.get(property);
                copyGdo.setPropertyWithoutCheck(property, value);
            }
        }

        List<Setting> oldSettings = changeSummary.getOldValues(originalObject);
        for (Setting setting: oldSettings) {
            assertNotNull(setting);
            if (setting.isSet()) {
                copyGdo.setOldPropertyWithoutCheck(setting.getProperty(), setting.getValue());
            } else {
                copyGdo.setOldPropertyWithoutCheck(setting.getProperty(), GenericDataObject.UNSET);
            }
        }

        List<Setting> oldSettingsTest = copyChangeSummary.getOldValues(copyObject);
        for (Setting setting: oldSettingsTest) {
            assertNotNull(setting);
        }

        ValidationHelper.validateTree(copyObject);

        assertEquals(true, _helperContext.getEqualityHelper().equal(originalObject, copyObject));

        assertEquals(true, originalObject.isSet("x"));
        assertEquals(true, copyObject.isSet("x"));

        originalObject.getChangeSummary().undoChanges();
        copyObject.getChangeSummary().undoChanges();

        assertEquals(true, _helperContext.getEqualityHelper().equal(originalObject, copyObject));

        ValidationHelper.validateTree(originalObject);
        ValidationHelper.validateTree(copyObject);

        assertEquals(false, originalObject.isSet("x"));
        assertEquals(false, copyObject.isSet("x"));

    }

    @Test
    public void testAccessingChangeSummary() {
        LoggingRootIntf loggingRoot =
            (LoggingRootIntf)_helperContext.getDataFactory().create(LoggingRootIntf.class);
        assertNotNull(loggingRoot);

        ChangeSummary changeSummary = loggingRoot.getChangeSummary();
        assertNotNull(changeSummary);
        assertSame(changeSummary, ((DataObject)loggingRoot).getChangeSummary());

        boolean csFound = false;
        // Type csType = _helperContext.getTypeHelper().getType("commonj.sdo", "ChangeSummaryType");
        Type csType = ChangeSummaryType.getInstance();
        List<Property> properties = ((DataObject)loggingRoot).getInstanceProperties();
        for (Property property : properties) {
            if (csType == property.getType()) {
                assertSame(changeSummary, ((DataObject)loggingRoot).get(property.getName()));
                csFound = true;
            }
        }
        if (!csFound) {
            fail("property of ChangeSummary not found.");
        }
    }

    @Test
    public void testOldStateDataObjects() {
        LoggingRootIntf loggingRoot = (LoggingRootIntf)_helperContext.getDataFactory().create(LoggingRootIntf.class);
        SimpleContainingIntf level0Object = (SimpleContainingIntf)_helperContext.getDataFactory().create(SimpleContainingIntf.class);
        SimpleContainedIntf level1Object = (SimpleContainedIntf)_helperContext.getDataFactory().create(SimpleContainedIntf.class);

        loggingRoot.setSimpleContainingIntf(level0Object);
        level0Object.setInner(level1Object);
        ChangeSummary changeSummary = loggingRoot.getChangeSummary();
        level0Object.setX("level0Object");
        level1Object.setName("level1Object");
        changeSummary.beginLogging();

        ((DataObject)level0Object).delete();

        assertEquals(false, changeSummary.isDeleted((DataObject)loggingRoot));
        assertEquals(true, changeSummary.isModified((DataObject)loggingRoot));
        assertEquals(true, changeSummary.isDeleted((DataObject)level0Object));
        assertEquals(true, changeSummary.isDeleted((DataObject)level1Object));

        List changedObjects = changeSummary.getChangedDataObjects();

        assertEquals(3, changedObjects.size());

        SimpleContainingIntf oldLevel0Object = null;
        for (Object changedObject: changedObjects) {
            if (changedObject.equals(level0Object)) {
                oldLevel0Object = (SimpleContainingIntf)changedObject;
            }
        }

        assertNotNull(oldLevel0Object);

        assertEquals(null, level0Object.getInner());
        assertEquals(null, level0Object.getX());
        assertEquals(null, level1Object.getName());
        assertEquals(level1Object, ((DataObject)level1Object).getRootObject());

        SimpleContainedIntf oldLevel1Object = oldLevel0Object.getInner();

        assertEquals(level1Object, oldLevel1Object);
        assertEquals("level0Object", oldLevel0Object.getX());
        assertEquals("level1Object", oldLevel1Object.getName());
        assertEquals(loggingRoot, ((DataObject)oldLevel1Object).getRootObject());

        assertEquals(null, ((DataObject)oldLevel1Object).getDataGraph());

    }

    @Test
    public void testOldStateForUnset() {
        DataObject openIntf = _helperContext.getDataFactory().create(OpenInterface.class);
        DataObject a = _helperContext.getDataFactory().create(OpenInterface.class);
        LoggingRootIntf root = (LoggingRootIntf)_helperContext.getDataFactory().create(LoggingRootIntf.class);
        root.setOpenInterface((OpenInterface)openIntf);
        openIntf.setString("testA", "valueA");

        root.getChangeSummary().beginLogging();

        openIntf.setString("testB", "valueB");
        openIntf.setString("testA", "valueC");
        openIntf.setInt("testC", 42);
        assertEquals("valueC", openIntf.getString("testA"));
        assertEquals("valueB", openIntf.getString("testB"));
        Property bProp = openIntf.getInstanceProperty("testB");
        assertNotNull(bProp);
        assertEquals("valueB", openIntf.getString(bProp));
        Property cProp = openIntf.getInstanceProperty("testC");
        assertNotNull(cProp);
        assertEquals(42, openIntf.getInt(cProp));
        openIntf.setDataObject("a", a);

        DataObject oldState = ((DataObjectDecorator)openIntf).getInstance().getOldStateFacade();
        assertNotNull(oldState);
        assertEquals("valueA", oldState.getString("testA"));
        assertEquals(null, oldState.getString(bProp));
        assertEquals(0, oldState.getInt(cProp));
        assertEquals(null, oldState.getString("testB"));
        assertEquals(int.class, cProp.getType().getInstanceClass());
        assertEquals(0, oldState.get(cProp));
        assertEquals((byte)0, oldState.getByte(cProp));

    }
}
