//
// Copyright 2017 Tridium, Inc. All Rights Reserved.
// Licensed under the Academic Free License version 3.0
//
// History:
//     05 Dec 2017 Rowyn Brunner Creation
//

package nhaystack.ntest.helper;

import static org.testng.Assert.*;
import static org.testng.Assert.assertEquals;

import java.util.Optional;
import javax.baja.data.BIDataValue;
import javax.baja.sys.BComponent;
import javax.baja.tag.Id;
import javax.baja.tag.Tags;
import nhaystack.BHDict;
import org.projecthaystack.HDict;
import org.projecthaystack.HVal;

public final class TagTestUtil
{
    private TagTestUtil()
    {
    }

    public static void assertTag(Tags tags, String tagName, BIDataValue expectedValue)
    {
        Optional<BIDataValue> tag = tags.get(Id.newId("hs", tagName));
        assertTrue(tag.isPresent(), "Missing hs:" + tagName);
        assertEquals(tag.get(), expectedValue, "Value of hs:" + tagName + " not correct");
    }

    public static void assertDictEntry(HDict dict, String key, HVal expectedValue)
    {
        HVal actualValue = dict.get(key, false);
        assertNotNull(actualValue, "Key " + key + " not found");
        assertEquals(actualValue, expectedValue, "Value of " + key + "not correct");
    }

    public static HDict getCompDict(BComponent component)
    {
        return ((BHDict)component.get(BHDict.HAYSTACK_IDENTIFIER)).getDict();
    }
}
