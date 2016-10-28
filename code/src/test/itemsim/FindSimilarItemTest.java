package test.itemsim;

import main.itemsim.FindSimilarItem;
import main.itemsim.Item;
import main.itemsim.ParseData;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class FindSimilarItemTest {
    private int bucket = 2;
    private FindSimilarItem fsi;

    @Before
    public void setUp() throws Exception {
        ParseData myParser = new ParseData("searchout/test_iphone_6_plus.json",false);
        List<Item> itemList = myParser.readData();
        fsi = new FindSimilarItem(itemList, bucket);
    }

    @Test
    public void searchSimItemTest() throws Exception {
        int result = fsi.searchSimItem(0);
        assertEquals(result, 2);
    }

}
