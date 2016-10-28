package test.itemsim;

import main.itemsim.Item;
import main.itemsim.ParseData;
import org.junit.Test;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ParseDataTest {
    /**
     * test normal response file
     * @throws Exception
     */
    @Test
    public void readNormalData() throws Exception {
        ParseData myparser = new ParseData("searchout/test_facebook.json",false);
        List<Item> itemList = myparser.readData();
        assertEquals(itemList.size(), 100);
        assertEquals(itemList.get(0).getID(), "160891622414");
    }

    @Test
    public void readSimpleData() throws Exception {
        ParseData myparser = new ParseData("searchout/test_iphone_6_plus.json",false);
        List<Item> itemList = myparser.readData();
        assertEquals(itemList.size(), 3);
        assertEquals(itemList.get(0).getID(), "191891222417");
    }

    /**
     * test empty response file
     * @throws Exception
     */
    @Test
    public void readEmptyData() throws Exception {
        ParseData myparser = new ParseData("searchout/test_empty_peppypig.json",false);
        List<Item> itemList = myparser.readData();
        assertEquals(itemList.size(), 0);
    }

}