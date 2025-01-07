/*
 * Copyright (c) 2007 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.sdo.testcase.internal;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringBufferInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sap.sdo.api.impl.SapHelperProvider;
import com.sap.sdo.testcase.SdoTestCase;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.helper.HelperContext;

/**
 * @author D042774
 *
 */
public class SdoPerformanceTest extends SdoTestCase {
    /**
     * @param pHelperContext
     */
    public SdoPerformanceTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    public static int RUNS = 10000;
    public static final String[][] ACCESSORS =
        { { "channel", "title" }, { "channel", "description" }, { "channel", "language" }, { "channel", "item", "title" }, { "channel", "item", "link" },
            { "channel", "item", "description" }, { "channel", "item", "guid" }, { "channel", "item", "pubDate" }, { "channel", "item", "BuyItNowPrice" },
            { "channel", "item", "CurrentPrice" }, { "channel", "item", "EndTime" }, { "channel", "item", "BidCount" }, { "channel", "item", "Category" },
            { "channel", "item", "AuctionType" } };
    private String[] PATHS = new String[] { "/channel/title", "/channel/description", "/channel/language", "/channel/item/title", "/channel/item/link",
        "/channel/item/description", "/channel/item/guid", "/channel/item/pubDate", "/channel/item/BuyItNowPrice", "/channel/item/CurrentPrice",
        "/channel/item/EndTime", "/channel/item/BidCount", "/channel/item/Category", "/channel/item/AuctionType" };

    private StringBuilder xml = new StringBuilder();
    private long diff = 0;

    private DataObject _origin;

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    @Before
    public void setUp() throws Exception {
        try {
            _helperContext.getXSDHelper().define(
                getClass().getClassLoader().getResourceAsStream(PACKAGE + "message_1K.xsd"),
                null);
            InputStream is = getClass().getClassLoader().getResourceAsStream(PACKAGE + "message_1K.xml");
            BufferedReader r = new BufferedReader(new InputStreamReader(is));
            String s;
            while ((s = r.readLine()) != null)
                xml.append(s);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    @After
    public void tearDown() throws Exception {
        _origin = null;
        long millis = diff / 1000000;
        System.out.printf("Mapping %d data objects took %d ms (=%f mappings/sec)\n", RUNS, millis, (double) (1000 * RUNS) / millis);
    }

    @Test
    public void testSimpleMappingPath() {
        System.out.println("*** Test of Simple Mapping using pathes! ***");
        for (int i = 0; i < RUNS; i++) {
            measureSimpleMappingPath();
            if ((i + 1) % 1000 == 0) {
                System.out.printf("%d data objects mapped, took %d ns\n", i + 1, diff);
                System.out.flush();
            }
        }
    }

    @Test
    public void testSimpleMappingIndex() {
        System.out.println("*** Test of Simple Mapping using indexes! ***");
        for (int i = 0; i < RUNS; i++) {
            measureSimpleMappingIndex();
            if ((i + 1) % 1000 == 0) {
                System.out.printf("%d data objects mapped, took %d ns\n", i + 1, diff);
                System.out.flush();
            }
        }
    }

    @Test
    public void testXMLParsing() {
        System.out.println("*** Test of XML Parsing! ***");
        for (int i = 0; i < RUNS; i++) {
            measureXMLParsing(_helperContext);
            if ((i + 1) % 1000 == 0) {
                System.out.printf("%d data objects mapped, took %d ns\n", i + 1, diff);
                System.out.flush();
            }
        }
    }

    @Test
    public void testXMLRendering() throws Exception {
        System.out.println("*** Test of XML Rendering! ***");
        StringBufferInputStream is = new StringBufferInputStream(xml.toString());
        DataObject root = _helperContext.getXMLHelper().load(is).getRootObject();
        for (int i = 0; i < RUNS; i++) {
            measureXMLRendering(_helperContext, root);
            if ((i + 1) % 1000 == 0) {
                System.out.printf("%d data objects mapped, took %d ns\n", i + 1, diff);
                System.out.flush();
            }
        }
    }

    @Test
    public void testXMLParsingWithoutSchema() {
        System.out.println("*** Test of XML Parsing without Schema! ***");
        HelperContext ctx = SapHelperProvider.getNewContext();
        for (int i = 0; i < RUNS; i++) {
            measureXMLParsing(ctx);
            if ((i + 1) % 1000 == 0) {
                System.out.printf("%d data objects mapped, took %d ns\n", i + 1, diff);
                System.out.flush();
            }
        }
    }

    @Test
    public void testXMLRenderingWithoutSchema() throws Exception {
        System.out.println("*** Test of XML Rendering without Schema! ***");
        HelperContext ctx = SapHelperProvider.getNewContext();
        StringBufferInputStream is = new StringBufferInputStream(xml.toString());
        DataObject root = ctx.getXMLHelper().load(is).getRootObject();
        for (int i = 0; i < RUNS; i++) {
            measureXMLRendering(ctx, root);
            if ((i + 1) % 1000 == 0) {
                System.out.printf("%d data objects mapped, took %d ns\n", i + 1, diff);
                System.out.flush();
            }
        }
    }

    private void measureSimpleMappingPath() {

        // StringBufferInputStream is1 = new StringBufferInputStream(xml.toString());
        StringBufferInputStream is2 = new StringBufferInputStream(xml.toString());
        // DataObject target = _helperContext.getDataFactory().create("http://www.sap.com","rss");

        // load XML into SDO
        try {
            // DataObject target = _helperContext.getXMLHelper().load(is1).getRootObject();
            if (_origin == null) {
                _origin = _helperContext.getXMLHelper().load(is2).getRootObject();
            }
            DataObject target;

            // int[][] indexes = computePropertyIndexes(origin);
            // Property[][] indexes = computeProperties(origin);
            DataObject os, ot;
            int i, j;
            // TODO: einbauen -> caching (aka. einfacher Pfadindex)
            // String[] paths = generateXPATHs();
            // RSS r1 = new RSS(origin);
            // RSS r2 = new RSS(target);
            long start, end;
            start = System.nanoTime();

            target = _helperContext.getDataFactory().create("http://www.sap.com", "rss");
            for (i = 0; i < ACCESSORS.length; i++) {
                os = _origin;
                ot = target;

                for (j = 0; j < ACCESSORS[i].length - 1; j++) {
                    os = os.getDataObject(ACCESSORS[i][j]);
                    if (ot.isSet(ACCESSORS[i][j]))
                        ot = ot.getDataObject(ACCESSORS[i][j]);
                    else
                        ot = ot.createDataObject(ACCESSORS[i][j]);
                    // ot = ot.getDataObject(indexes[i][j]);
                }
                // ot.set(indexes[i][indexes[i].length - 1], os.get(indexes[i][indexes[i].length - 1]));
                ot.set(ACCESSORS[i][ACCESSORS[i].length - 1], _origin.get(PATHS[i]));
            }

            /*
             *
             * for (i=0; i<ACCESSORS.length; i++) { os = origin; ot = target; for (j=0; j<ACCESSORS[i].length-1; j++) {
             * os = os.getDataObject(ACCESSORS[i][j]); ot = ot.getDataObject(ACCESSORS[i][j]); }
             * ot.set(ACCESSORS[i][ACCESSORS[i].length-1], os.get(ACCESSORS[i][ACCESSORS[i].length-1])); }
             */

            /*
             * for (i = 0; i < paths.length; i++) { target.set(paths[i], origin.get(paths[i])); }
             */
            /*
             * r2.channel.description = r1.channel.description; r2.channel.language = r1.channel.language;
             * r2.channel.title = r1.channel.title; r2.channel.item.autionType = r1.channel.item.autionType;
             * r2.channel.item.bidCount = r1.channel.item.bidCount; r2.channel.item.buyItNowPrice =
             * r1.channel.item.buyItNowPrice; r2.channel.item.category = r1.channel.item.category;
             * r2.channel.item.currentPrice = r1.channel.item.currentPrice; r2.channel.item.description =
             * r1.channel.item.description; r2.channel.item.endTime = r1.channel.item.endTime; r2.channel.item.guid =
             * r1.channel.item.guid; r2.channel.item.link = r1.channel.item.link; r2.channel.item.pubDate =
             * r1.channel.item.pubDate; r2.channel.item.title = r1.channel.item.title;
             */
            end = System.nanoTime();
            diff += end - start;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void measureSimpleMappingIndex() {

        // StringBufferInputStream is1 = new StringBufferInputStream(xml.toString());
        StringBufferInputStream is2 = new StringBufferInputStream(xml.toString());
        // DataObject target = _helperContext.getDataFactory().create("http://www.sap.com","rss");

        // load XML into SDO
        try {
            // DataObject target = _helperContext.getXMLHelper().load(is1).getRootObject();
            if (_origin == null) {
                _origin = _helperContext.getXMLHelper().load(is2).getRootObject();
            }
            DataObject target;

            int[][] indexes = computePropertyIndexes(_origin);
            // Property[][] indexes = computeProperties(origin);
            DataObject os, ot;
            int i, j;
            // TODO: einbauen -> caching (aka. einfacher Pfadindex)
            // String[] paths = generateXPATHs();
            // RSS r1 = new RSS(origin);
            // RSS r2 = new RSS(target);
            long start, end;
            start = System.nanoTime();

            target = _helperContext.getDataFactory().create("http://www.sap.com", "rss");
            //for (i = 0; i < ACCESSORS.length; i++) {
            for (i = 0; i < indexes.length; i++) {
                os = _origin;
                ot = target;

//                for (j = 0; j < ACCESSORS[i].length - 1; j++) {
//                    os = os.getDataObject(ACCESSORS[i][j]);
//                    if (ot.isSet(ACCESSORS[i][j]))
//                        ot = ot.getDataObject(ACCESSORS[i][j]);
//                    else
//                        ot = ot.createDataObject(ACCESSORS[i][j]);
//                }
                for (j = 0; j < indexes[i].length - 1; j++) {
                    os = os.getDataObject(indexes[i][j]);
                    if (ot.isSet(indexes[i][j]))
                        ot = ot.getDataObject(indexes[i][j]);
                    else
                        ot = ot.createDataObject(indexes[i][j]);
                }
                ot.set(indexes[i][indexes[i].length - 1], os.get(indexes[i][indexes[i].length - 1]));
                // ot.set(ACCESSORS[i][ACCESSORS[i].length - 1], origin.get(PATHS[i]));
            }

            /*
             *
             * for (i=0; i<ACCESSORS.length; i++) { os = origin; ot = target; for (j=0; j<ACCESSORS[i].length-1; j++) {
             * os = os.getDataObject(ACCESSORS[i][j]); ot = ot.getDataObject(ACCESSORS[i][j]); }
             * ot.set(ACCESSORS[i][ACCESSORS[i].length-1], os.get(ACCESSORS[i][ACCESSORS[i].length-1])); }
             */

            /*
             * for (i = 0; i < paths.length; i++) { target.set(paths[i], origin.get(paths[i])); }
             */
            /*
             * r2.channel.description = r1.channel.description; r2.channel.language = r1.channel.language;
             * r2.channel.title = r1.channel.title; r2.channel.item.autionType = r1.channel.item.autionType;
             * r2.channel.item.bidCount = r1.channel.item.bidCount; r2.channel.item.buyItNowPrice =
             * r1.channel.item.buyItNowPrice; r2.channel.item.category = r1.channel.item.category;
             * r2.channel.item.currentPrice = r1.channel.item.currentPrice; r2.channel.item.description =
             * r1.channel.item.description; r2.channel.item.endTime = r1.channel.item.endTime; r2.channel.item.guid =
             * r1.channel.item.guid; r2.channel.item.link = r1.channel.item.link; r2.channel.item.pubDate =
             * r1.channel.item.pubDate; r2.channel.item.title = r1.channel.item.title;
             */
            end = System.nanoTime();
            diff += end - start;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void measureXMLParsing(HelperContext context) {
        StringBufferInputStream is = new StringBufferInputStream(xml.toString());
        try {
            long start, end;
            Map options = new HashMap();
            options.put("com.sap.sdo.api.helper.SapXmlHelper.SimplifyOpenContent", Boolean.FALSE.toString());
            options.put("com.sap.sdo.api.helper.SapXmlHelper.DefineSchemas", Boolean.FALSE.toString());
            start = System.nanoTime();
            context.getXMLHelper().load(is, null, options);
            end = System.nanoTime();
            diff += (end - start);
            /*
             * DocumentBuilderFactory dom_factory = DocumentBuilderFactory.newInstance();
             * dom_factory.setNamespaceAware(true); // never forget this! DocumentBuilder builder =
             * dom_factory.newDocumentBuilder(); // _helperContext.getXMLHelper().load(is); Document document = builder.parse(is);
             *
             * DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance(); DocumentBuilder
             * docBuilder = docBuilderFactory.newDocumentBuilder(); Document target = docBuilder.newDocument();
             *
             * Element root = target.createElement("rss"); target.appendChild(root); // Document target =
             * DOMImplementationRegistry.newInstance().getDOMImplementation("XML 1.0 Traversal +Events //
             * 2.0").createDocument("http://www.sap.com", "rss", null);
             *
             * XPathFactory factory = XPathFactory.newInstance();
             *
             * XPathExpression channel_title = factory.newXPath().compile("/rss/channel/title/text()"),
             * channel_description = factory.newXPath().compile( "/rss/channel/description/text()"), channel_language =
             * factory.newXPath().compile("/rss/channel/language/text()"), channel_item_title = factory
             * .newXPath().compile("/rss/channel/item/title/text()"), channel_item_link =
             * factory.newXPath().compile("/rss/channel/item/link/text()"), channel_item_description = factory
             * .newXPath().compile("/rss/channel/item/description/text()"), channel_item_category =
             * factory.newXPath().compile( "/rss/channel/item/Category/text()"), channel_item_auctionType =
             * factory.newXPath().compile("/rss/channel/item/AuctionType/text()"), channel_item_pubDate = factory
             * .newXPath().compile("/rss/channel/item/pubDate/text()"), channel_item_buyItNowPrice =
             * factory.newXPath().compile( "/rss/channel/item/BuyItNowPrice/text()"), channel_item_currentPrice =
             * factory.newXPath().compile("/rss/channel/item/CurrentPrice/text()"), channel_item_endTime = factory
             * .newXPath().compile("/rss/channel/item/EndTime/text()"), channel_item_bidCount =
             * factory.newXPath().compile( "/rss/channel/item/BidCount/text()"); start = System.nanoTime();
             *
             * Element channel = target.createElement("channel"); root.appendChild(channel);
             *
             * addTextElement(document, target, channel, "title", channel_title); addTextElement(document, target,
             * channel, "description", channel_description); addTextElement(document, target, channel, "language",
             * channel_language);
             *
             * Element item = target.createElement("item"); channel.appendChild(item); addTextElement(document, target,
             * item, "title", channel_item_title); addTextElement(document, target, item, "description",
             * channel_item_description); addTextElement(document, target, item, "Category", channel_item_category);
             * addTextElement(document, target, item, "AuctionType", channel_item_auctionType); addTextElement(document,
             * target, item, "pubDate", channel_item_pubDate); addTextElement(document, target, item, "BuyItNowPrice",
             * channel_item_buyItNowPrice); addTextElement(document, target, item, "CurrentPrice",
             * channel_item_currentPrice); addTextElement(document, target, item, "EndTime", channel_item_endTime);
             * addTextElement(document, target, item, "BidCount", channel_item_bidCount);
             */
            /*
             * channel.title = o.getString("/channel/title"); channel.description = o.getString("/channel/description");
             * channel.language = o.getString("/channel/language"); channel.item = new Item(); channel.item.title =
             * o.getString("/channel/item/title"); channel.item.link = o.getString("/channel/item/link");
             * channel.item.description = o.getString("/channel/item/description"); channel.item.category =
             * o.getString("/channel/item/Category"); channel.item.autionType =
             * o.getString("/channel/item/AuctionType"); channel.item.pubDate = o.getString("/channel/item/pubDate");
             * channel.item.buyItNowPrice = o.getLong("/channel/item/BuyItNowPrice"); channel.item.currentPrice =
             * o.getLong("/channel/item/CurrentPrice"); channel.item.endTime = o.getLong("/channel/item/EndTime");
             * channel.item.bidCount = o.getInt("/channel/item/BidCount");
             */

//            end = System.nanoTime();
//            diff += (end - start);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void measureXMLRendering(HelperContext context, DataObject root) {
        /*
         * try { DocumentBuilderFactory dom_factory = DocumentBuilderFactory.newInstance();
         * dom_factory.setNamespaceAware(true); // never forget this! DocumentBuilder builder =
         * dom_factory.newDocumentBuilder(); Document document = builder.parse(is);
         *
         * long start, end;
         *
         * Transformer transformer = TransformerFactory.newInstance().newTransformer(); OutputStream os = new
         * ByteArrayOutputStream(65536); StreamResult result = new StreamResult(os); DOMSource source = new
         * DOMSource(document);
         *
         * start = System.nanoTime(); transformer.transform(source, result); end = System.nanoTime();
         *
         * diff += (end - start); } catch (Exception e) { e.printStackTrace(); }
         */
        long start, end;

        start = System.nanoTime();
        context.getXMLHelper().save(root, "http://www.sap.com", "rss");
        end = System.nanoTime();
        diff += end - start;
    }

    private int[][] computePropertyIndexes(DataObject root) {
        List<Property> list;
        int[][] indexes = new int[ACCESSORS.length][];
        for (int i = 0; i < ACCESSORS.length; i++) {
            DataObject o = root;
            indexes[i] = new int[ACCESSORS[i].length];
            for (int j = 0; j < ACCESSORS[i].length; j++) {
                list = o.getInstanceProperties();
                for (int k = 0; k < list.size(); k++) {
                    if (list.get(k).getName().equals(ACCESSORS[i][j])) {
                        indexes[i][j] = k;
                        if (j < ACCESSORS[i].length - 1)
                            o = o.getDataObject(k);
                        break;
                    }
                }
            }
        }
        return indexes;
    }

    public static void main(String[] args) {
        try {
            SdoPerformanceTest test =
                new SdoPerformanceTest(SapHelperProvider.DEFAULT_CONTEXT_ID, null);
            RUNS = 100000;
            test.setUp();
            test.testSimpleMappingIndex();
            test.tearDown();
            test.setUp();
            test.testSimpleMappingPath();
            test.tearDown();
//            test.setUp();
//            test.testXMLParsingWithoutSchema();
//            test.tearDown();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
