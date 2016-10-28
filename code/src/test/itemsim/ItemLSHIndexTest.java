package test.itemsim;

import main.itemsim.Item;
import main.itemsim.ItemLSHIndex;
import main.itemsim.ParseData;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class ItemLSHIndexTest {
    private ItemLSHIndex lshIndex;
    private int bucket = 2;

    @Before
    public void setUp() throws Exception {
        ParseData myParser = new ParseData("searchout/test_iphone_6_plus.json",false);
        List<Item> itemList = myParser.readData();

        lshIndex = new ItemLSHIndex(itemList);
        lshIndex.createLSH(bucket);
    }

    @Test
    public void findCandidateItemsTest() throws Exception {
        List<Integer> candidates = lshIndex.findCandidateItems(0);
        List<Integer> expected = Arrays.asList(1, 2);
        assertEquals(expected, candidates);
    }

}