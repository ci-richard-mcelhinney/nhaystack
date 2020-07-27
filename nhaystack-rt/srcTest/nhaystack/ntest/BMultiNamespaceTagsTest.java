//
// Copyright 2019 Tridium, Inc. All Rights Reserved.
// Licensed under the Academic Free License version 3.0
//
// History:
//     19 Jul 2019  Eric Anderson  Creation
//
package nhaystack.ntest;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import javax.baja.control.BNumericPoint;
import javax.baja.data.BIDataValue;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.sys.BInteger;
import javax.baja.sys.BMarker;
import javax.baja.sys.BStation;
import javax.baja.sys.BString;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import javax.baja.tag.Tag;
import javax.baja.tag.Tags;
import javax.baja.util.BFolder;

import nhaystack.ntest.helper.BNHaystackStationTestBase;
import org.projecthaystack.HDict;
import org.projecthaystack.HMarker;
import org.projecthaystack.HNum;
import org.projecthaystack.HRef;
import org.projecthaystack.HStr;
import org.projecthaystack.HVal;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@Test
@NiagaraType
public class BMultiNamespaceTagsTest extends BNHaystackStationTestBase
{
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.ntest.BMultiNamespaceTagsTest(2979906276)1.0$ @*/
/* Generated Fri Jul 12 16:34:06 EDT 2019 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  @Override
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BMultiNamespaceTagsTest.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    @BeforeTest
    @Override
    public void setupStation() throws Exception
    {
        super.setupStation();

        client = openClient(false);
    }

    @BeforeMethod
    public void beforeMethod()
    {
        BStation station = stationHandler.getStation();
        if (station.get(TEST_FOLDER_NAME) != null)
        {
            station.remove(TEST_FOLDER_NAME);
        }

        station.add(TEST_FOLDER_NAME, testFolder = new BFolder());
        testFolder.add(POINT_NAME, point = new BNumericPoint());

        rebuildCache();
    }

    public void emptyNamespaceReturnsHaystackTagsOnly()
    {
        nhaystackService.setPrioritizedNamespaces("");
        assertHaystackOnly();
    }

    @DataProvider
    private Object[][] namespaceAndAssertProvider()
    {
        return new Object[][]
        {
            {"hs", (Runnable) this::assertHaystackOnly},
            {"hs,b", (Runnable) this::assertHaystackThenB},
            {"b,hs", (Runnable) this::assertBThenHaystack},
            {"hs,b,c", (Runnable) this::assertHaystackThenBThenC},
            {"b,hs,c", (Runnable) this::assertBThenHaystackThenC},
            {"b,c,hs", (Runnable) this::assertBThenCThenHaystack},
        };
    }

    @Test(dataProvider = "namespaceAndAssertProvider")
    public void testTagsReturnedForNamespace(String namespaces, Runnable assertTags)
    {
        nhaystackService.setPrioritizedNamespaces(namespaces);
        assertTags.run();
    }

    private void assertHaystackOnly()
    {
        addTags(
            Tag.newTag("hs:hsMarker"),
            Tag.newTag("b:bMarker"),
            Tag.newTag("hs:hsInt", 1),
            Tag.newTag("b:hsInt", 2),
            Tag.newTag("b:bInt", 2));

        assertTags(
            Arrays.asList(
                Tag.newTag("hsMarker"),
                Tag.newTag("hsInt", 1)),
            Arrays.asList(
                Tag.newTag("bMarker"),
                Tag.newTag("bInt", 2)));
    }

    private void addHaystackAndBTags()
    {
        addTags(
            Tag.newTag("hs:hsMarker"),
            Tag.newTag("b:bMarker"),
            Tag.newTag("c:cMarker"),     // never added
            Tag.newTag("hs:hsInt1", 1),
            Tag.newTag("hs:hsInt2", 1),  // overlapped by b
            Tag.newTag("hs:bInt2",  1),  // overlaps b
            Tag.newTag("b:bInt1",   2),
            Tag.newTag("b:bInt2",   2),  // overlapped by hs
            Tag.newTag("b:hsInt2",  2),  // overlaps hs
            Tag.newTag("c:hsInt1",  3),  // overlaps hs (never used)
            Tag.newTag("c:bInt1",   3),  // overlaps b (never used)
            Tag.newTag("c:cInt",    3)); // never added
    }

    private void assertHaystackThenB()
    {
        addHaystackAndBTags();

        assertTags(
            Arrays.asList(
                Tag.newTag("hsMarker"),
                Tag.newTag("bMarker"),
                Tag.newTag("hsInt1", 1),
                Tag.newTag("hsInt2", 1),
                Tag.newTag("bInt1",  2),
                Tag.newTag("bInt2",  1)),
            Arrays.asList(
                Tag.newTag("cMarker"),
                Tag.newTag("cInt", 3)));
    }

    private void assertBThenHaystack()
    {
        addHaystackAndBTags();

        assertTags(
            Arrays.asList(
                Tag.newTag("hsMarker"),
                Tag.newTag("bMarker"),
                Tag.newTag("hsInt1", 1),
                Tag.newTag("hsInt2", 2),
                Tag.newTag("bInt1",  2),
                Tag.newTag("bInt2",  2)),
            Arrays.asList(
                Tag.newTag("cMarker"),
                Tag.newTag("cInt", 3)));
    }

    private void addHaystackAndBAndCTags()
    {
        addTags(
            Tag.newTag("hs:hsMarker"),
            Tag.newTag("b:bMarker"),
            Tag.newTag("c:cMarker"),
            Tag.newTag("d:dMarker"),     // never added
            Tag.newTag("hs:hsInt1", 1),
            Tag.newTag("hs:hsInt2", 1),  // overlapped by b
            Tag.newTag("hs:hsInt3", 1),  // overlapped by c
            Tag.newTag("hs:bInt2",  1),  // overlaps b
            Tag.newTag("hs:cInt2",  1),  // overlaps c
            Tag.newTag("b:bInt1",   2),
            Tag.newTag("b:bInt2",   2),  // overlapped by hs
            Tag.newTag("b:bInt3",   2),  // overlapped by c
            Tag.newTag("b:hsInt2",  2),  // overlaps hs
            Tag.newTag("b:cInt3",   2),  // overlaps c
            Tag.newTag("c:cInt1",   3),
            Tag.newTag("c:cInt2",   3),  // overlapped by hs
            Tag.newTag("c:cInt3",   3),  // overlapped by b
            Tag.newTag("c:hsInt3",  3),  // overlaps hs
            Tag.newTag("c:bInt3",   3),  // overlaps b
            Tag.newTag("d:hsInt1",  4),  // overlaps hs (never used)
            Tag.newTag("d:bInt1",   4),  // overlaps b (never used)
            Tag.newTag("d:cInt1",   4),  // overlaps c (never used)
            Tag.newTag("d:dInt",    4)); // never added
    }

    private void assertHaystackThenBThenC()
    {
        addHaystackAndBAndCTags();

        assertTags(
            Arrays.asList(
                Tag.newTag("hsMarker"),
                Tag.newTag("bMarker"),
                Tag.newTag("cMarker"),
                Tag.newTag("hsInt1", 1),
                Tag.newTag("hsInt2", 1),
                Tag.newTag("hsInt3", 1),
                Tag.newTag("bInt1",  2),
                Tag.newTag("bInt2",  1),
                Tag.newTag("bInt3",  2),
                Tag.newTag("cInt1",  3),
                Tag.newTag("cInt2",  1),
                Tag.newTag("cInt3",  2)),
            Arrays.asList(
                Tag.newTag("dMarker"),
                Tag.newTag("dInt", 4)));
    }

    private void assertBThenHaystackThenC()
    {
        addHaystackAndBAndCTags();

        assertTags(
            Arrays.asList(
                Tag.newTag("hsMarker"),
                Tag.newTag("bMarker"),
                Tag.newTag("cMarker"),
                Tag.newTag("hsInt1", 1),
                Tag.newTag("hsInt2", 2),
                Tag.newTag("hsInt3", 1),
                Tag.newTag("bInt1",  2),
                Tag.newTag("bInt2",  2),
                Tag.newTag("bInt3",  2),
                Tag.newTag("cInt1",  3),
                Tag.newTag("cInt2",  1),
                Tag.newTag("cInt3",  2)),
            Arrays.asList(
                Tag.newTag("dMarker"),
                Tag.newTag("dInt", 4)));
    }

    private void assertBThenCThenHaystack()
    {
        addHaystackAndBAndCTags();

        assertTags(
            Arrays.asList(
                Tag.newTag("hsMarker"),
                Tag.newTag("bMarker"),
                Tag.newTag("cMarker"),
                Tag.newTag("hsInt1", 1),
                Tag.newTag("hsInt2", 2),
                Tag.newTag("hsInt3", 3),
                Tag.newTag("bInt1",  2),
                Tag.newTag("bInt2",  2),
                Tag.newTag("bInt3",  2),
                Tag.newTag("cInt1",  3),
                Tag.newTag("cInt2",  3),
                Tag.newTag("cInt3",  2)),
            Arrays.asList(
                Tag.newTag("dMarker"),
                Tag.newTag("dInt", 4)));
    }

    private void addTags(Tag... tagsToAdd)
    {
        Tags componentTags = point.tags();
        Arrays.stream(tagsToAdd).forEach(componentTags::set);
    }

    private void assertTags(List<Tag> tagsPresent, List<Tag> tagsAbsent)
    {
        HDict dict = client.readById(HRef.make("C." + TEST_FOLDER_NAME + '.' + POINT_NAME));

        for (Tag tagPresent : tagsPresent)
        {
            String tagName = tagPresent.getId().getName();
            assertTrue(dict.has(tagName), tagName + " tag is missing");

            BIDataValue expectedTagValue = tagPresent.getValue();
            HVal actualTagValue = dict.get(tagName);
            if (expectedTagValue instanceof BMarker)
            {
                assertEquals(actualTagValue.getClass(), HMarker.class, tagName + " tag type is wrong");
            }
            else if (expectedTagValue instanceof BString)
            {
                assertEquals(actualTagValue.getClass(), HStr.class, tagName + " tag type is wrong");
                assertEquals(((HStr) actualTagValue).val, ((BString) expectedTagValue).getString(), tagName + " tag value is wrong");
            }
            else if (expectedTagValue instanceof BInteger)
            {
                assertEquals(actualTagValue.getClass(), HNum.class, tagName + " tag type is wrong");
                assertEquals(((HNum) actualTagValue).val, ((BInteger) expectedTagValue).getDouble(), tagName + " tag value is wrong");
            }
        }

        for (Tag tagAbsent : tagsAbsent)
        {
            String tagName = tagAbsent.getId().getName();
            assertFalse(dict.has(tagName), tagName + " tag should not be present");
        }
    }

    private static final String TEST_FOLDER_NAME = "Test";
    private static final String POINT_NAME = "Point";

    private BFolder testFolder;
    private BNumericPoint point;
}