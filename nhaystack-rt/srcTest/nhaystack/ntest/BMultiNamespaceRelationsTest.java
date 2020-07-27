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
import javax.baja.sys.BMarker;
import javax.baja.sys.BRelation;
import javax.baja.sys.BStation;
import javax.baja.sys.BString;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import javax.baja.tag.Id;
import javax.baja.tag.Relations;
import javax.baja.tag.Tag;
import javax.baja.tag.Tags;
import javax.baja.util.BFolder;

import nhaystack.ntest.helper.BNHaystackStationTestBase;
import org.projecthaystack.HDict;
import org.projecthaystack.HMarker;
import org.projecthaystack.HRef;
import org.projecthaystack.HVal;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@Test
@NiagaraType
public class BMultiNamespaceRelationsTest extends BNHaystackStationTestBase
{
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.ntest.BMultiNamespaceRelationsTest(2979906276)1.0$ @*/
/* Generated Fri Jul 12 16:34:06 EDT 2019 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  @Override
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BMultiNamespaceRelationsTest.class);

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
        testFolder.add(POINT_SOURCE_NAME, pointSource = new BNumericPoint());
    }

    public void emptyNamespaceReturnsHaystackRelationsOnly()
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
            Tag.newTag("b:bMarker")); // never added

        BNumericPoint pointTargetHS = addPointTarget("PointTargetHS");
        BNumericPoint pointTargetB = addPointTarget("PointTargetB");
        rebuildCache();

        addRelations(
            new BRelation(Id.newId("hs:hsRelation"), pointTargetHS),
            new BRelation(Id.newId("b:hsRelation"),  pointTargetB),
            new BRelation(Id.newId("b:bRelation"),   pointTargetB));

        assertTags(
            Arrays.asList(
                Tag.newTag("hsMarker"),
                Tag.newTag("hsRelation", "C.Test.PointTargetHS")),
            Arrays.asList(
                Tag.newTag("bMarker"),
                Tag.newTag("bRelation", "C.Test.PointTargetB")));
    }

    private void addHaystackAndBTagsRelations()
    {
        addTags(
            Tag.newTag("hs:hsMarker"),
            Tag.newTag("b:bMarker"),
            Tag.newTag("c:cMarker")); // never added

        BNumericPoint pointTargetHS = addPointTarget("PointTargetHS");
        BNumericPoint pointTargetB = addPointTarget("PointTargetB");
        BNumericPoint pointTargetC = addPointTarget("PointTargetC");
        rebuildCache();

        addRelations(
            new BRelation(Id.newId("hs:hsRelation1"), pointTargetHS),
            new BRelation(Id.newId("hs:hsRelation2"), pointTargetHS), // overlapped by b
            new BRelation(Id.newId("hs:bRelation2"),  pointTargetHS), // overlaps b
            new BRelation(Id.newId("b:bRelation1"),   pointTargetB),
            new BRelation(Id.newId("b:bRelation2"),   pointTargetB),  // overlapped by hs
            new BRelation(Id.newId("b:hsRelation2"),  pointTargetB),  // overlaps hs
            new BRelation(Id.newId("c:hsRelation1"),  pointTargetC),  // overlaps hs (never used)
            new BRelation(Id.newId("c:bRelation1"),   pointTargetC),  // overlaps b (never used)
            new BRelation(Id.newId("c:cRelation"),    pointTargetC)); // never added
    }

    private void assertHaystackThenB()
    {
        addHaystackAndBTagsRelations();

        assertTags(
            Arrays.asList(
                Tag.newTag("hsMarker"),
                Tag.newTag("bMarker"),
                Tag.newTag("hsRelation1", "C.Test.PointTargetHS"),
                Tag.newTag("hsRelation2", "C.Test.PointTargetHS"),
                Tag.newTag("bRelation1",  "C.Test.PointTargetB"),
                Tag.newTag("bRelation2",  "C.Test.PointTargetHS")),
            Arrays.asList(
                Tag.newTag("cMarker"),
                Tag.newTag("cRelation", "C.Test.PointTargetC")));
    }

    private void assertBThenHaystack()
    {
        addHaystackAndBTagsRelations();

        assertTags(
            Arrays.asList(
                Tag.newTag("hsMarker"),
                Tag.newTag("bMarker"),
                Tag.newTag("hsRelation1", "C.Test.PointTargetHS"),
                Tag.newTag("hsRelation2", "C.Test.PointTargetB"),
                Tag.newTag("bRelation1",  "C.Test.PointTargetB"),
                Tag.newTag("bRelation2",  "C.Test.PointTargetB")),
            Arrays.asList(
                Tag.newTag("cMarker"),
                Tag.newTag("cRelation", "C.Test.PointTargetC")));
    }

    private void addHaystackAndBAndCTagsRelations()
    {
        addTags(
            Tag.newTag("hs:hsMarker"),
            Tag.newTag("b:bMarker"),
            Tag.newTag("c:cMarker"),
            Tag.newTag("d:dMarker")); // never added

        BNumericPoint pointTargetHS = addPointTarget("PointTargetHS");
        BNumericPoint pointTargetB = addPointTarget("PointTargetB");
        BNumericPoint pointTargetC = addPointTarget("PointTargetC");
        BNumericPoint pointTargetD = addPointTarget("PointTargetD");
        rebuildCache();

        addRelations(
            new BRelation(Id.newId("hs:hsRelation1"), pointTargetHS),
            new BRelation(Id.newId("hs:hsRelation2"), pointTargetHS), // overlapped by b
            new BRelation(Id.newId("hs:hsRelation3"), pointTargetHS), // overlapped by c
            new BRelation(Id.newId("hs:bRelation2"),  pointTargetHS), // overlaps b
            new BRelation(Id.newId("hs:cRelation2"),  pointTargetHS), // overlaps c
            new BRelation(Id.newId("b:bRelation1"),   pointTargetB),
            new BRelation(Id.newId("b:bRelation2"),   pointTargetB),  // overlapped by hs
            new BRelation(Id.newId("b:bRelation3"),   pointTargetB),  // overlapped by c
            new BRelation(Id.newId("b:hsRelation2"),  pointTargetB),  // overlaps hs
            new BRelation(Id.newId("b:cRelation3"),   pointTargetB),  // overlaps c
            new BRelation(Id.newId("c:cRelation1"),   pointTargetC),
            new BRelation(Id.newId("c:cRelation2"),   pointTargetC),  // overlapped by hs
            new BRelation(Id.newId("c:cRelation3"),   pointTargetC),  // overlapped by b
            new BRelation(Id.newId("c:hsRelation3"),  pointTargetC),  // overlaps hs
            new BRelation(Id.newId("c:bRelation3"),   pointTargetC),  // overlaps b
            new BRelation(Id.newId("d:hsRelation1"),  pointTargetD),  // overlaps hs (never used)
            new BRelation(Id.newId("d:bRelation1"),   pointTargetD),  // overlaps b (never used)
            new BRelation(Id.newId("d:cRelation1"),   pointTargetD),  // overlaps c (never used)
            new BRelation(Id.newId("d:dRelation"),    pointTargetD)); // never added
    }

    private void assertHaystackThenBThenC()
    {
        addHaystackAndBAndCTagsRelations();

        assertTags(
            Arrays.asList(
                Tag.newTag("hsMarker"),
                Tag.newTag("bMarker"),
                Tag.newTag("cMarker"),
                Tag.newTag("hsRelation1", "C.Test.PointTargetHS"),
                Tag.newTag("hsRelation2", "C.Test.PointTargetHS"),
                Tag.newTag("hsRelation3", "C.Test.PointTargetHS"),
                Tag.newTag("bRelation1",  "C.Test.PointTargetB"),
                Tag.newTag("bRelation2",  "C.Test.PointTargetHS"),
                Tag.newTag("bRelation3",  "C.Test.PointTargetB"),
                Tag.newTag("cRelation1",  "C.Test.PointTargetC"),
                Tag.newTag("cRelation2",  "C.Test.PointTargetHS"),
                Tag.newTag("cRelation3",  "C.Test.PointTargetB")),
            Arrays.asList(
                Tag.newTag("dMarker"),
                Tag.newTag("dRelation", "C.Test.PointTargetD")));
    }

    private void assertBThenHaystackThenC()
    {
        addHaystackAndBAndCTagsRelations();

        assertTags(
            Arrays.asList(
                Tag.newTag("hsMarker"),
                Tag.newTag("bMarker"),
                Tag.newTag("cMarker"),
                Tag.newTag("hsRelation1", "C.Test.PointTargetHS"),
                Tag.newTag("hsRelation2", "C.Test.PointTargetB"),
                Tag.newTag("hsRelation3", "C.Test.PointTargetHS"),
                Tag.newTag("bRelation1",  "C.Test.PointTargetB"),
                Tag.newTag("bRelation2",  "C.Test.PointTargetB"),
                Tag.newTag("bRelation3",  "C.Test.PointTargetB"),
                Tag.newTag("cRelation1",  "C.Test.PointTargetC"),
                Tag.newTag("cRelation2",  "C.Test.PointTargetHS"),
                Tag.newTag("cRelation3",  "C.Test.PointTargetB")),
            Arrays.asList(
                Tag.newTag("dMarker"),
                Tag.newTag("dRelation", "C.Test.PointTargetD")));
    }

    private void assertBThenCThenHaystack()
    {
        addHaystackAndBAndCTagsRelations();

        assertTags(
            Arrays.asList(
                Tag.newTag("hsMarker"),
                Tag.newTag("bMarker"),
                Tag.newTag("cMarker"),
                Tag.newTag("hsRelation1", "C.Test.PointTargetHS"),
                Tag.newTag("hsRelation2", "C.Test.PointTargetB"),
                Tag.newTag("hsRelation3", "C.Test.PointTargetC"),
                Tag.newTag("bRelation1",  "C.Test.PointTargetB"),
                Tag.newTag("bRelation2",  "C.Test.PointTargetB"),
                Tag.newTag("bRelation3",  "C.Test.PointTargetB"),
                Tag.newTag("cRelation1",  "C.Test.PointTargetC"),
                Tag.newTag("cRelation2",  "C.Test.PointTargetC"),
                Tag.newTag("cRelation3",  "C.Test.PointTargetB")),
            Arrays.asList(
                Tag.newTag("dMarker"),
                Tag.newTag("dRelation", "C.Test.PointTargetD")));
    }

    private BNumericPoint addPointTarget(String name)
    {
        BNumericPoint point = new BNumericPoint();
        testFolder.add(name, point);
        return point;
    }

    private void addTags(Tag... tagsToAdd)
    {
        Tags componentTags = pointSource.tags();
        Arrays.stream(tagsToAdd).forEach(componentTags::set);
    }

    private void addRelations(BRelation... relationsToAdd)
    {
        Relations componentRelations = pointSource.relations();
        Arrays.stream(relationsToAdd).forEach(componentRelations::add);
    }

    private void assertTags(List<Tag> tagsPresent, List<Tag> tagsAbsent)
    {
        HDict dict = client.readById(HRef.make("C." + TEST_FOLDER_NAME + '.' + POINT_SOURCE_NAME));

        for (Tag tagPresent : tagsPresent)
        {
            String tagName = tagPresent.getId().getName();
            assertTrue(dict.has(tagName), tagName + " tag is missing");

            BIDataValue expectedTagValue = tagPresent.getValue();
            HVal actualTagValue = dict.get(tagName);
            if (actualTagValue instanceof HMarker)
            {
                assertEquals(expectedTagValue.getClass(), BMarker.class, tagName + " tag type is wrong");
            }
            else if (actualTagValue instanceof HRef)
            {
                assertEquals(expectedTagValue.getClass(), BString.class, tagName + " tag type is wrong");
                assertEquals(((HRef) actualTagValue).val, ((BString) expectedTagValue).getString(), tagName + " tag value is wrong");
            }
        }

        for (Tag tagAbsent : tagsAbsent)
        {
            String tagName = tagAbsent.getId().getName();
            assertFalse(dict.has(tagName), tagName + " tag should not be present");
        }
    }

    private static final String TEST_FOLDER_NAME = "Test";
    private static final String POINT_SOURCE_NAME = "PointSource";

    private BFolder testFolder;
    private BNumericPoint pointSource;
}