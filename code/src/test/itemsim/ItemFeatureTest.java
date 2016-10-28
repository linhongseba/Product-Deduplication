package test.itemsim;

import info.debatty.java.utils.SparseDoubleVector;
import main.itemsim.Item;
import main.itemsim.ItemFeature;
import main.itemsim.ParseData;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class ItemFeatureTest {
    private ItemFeature testFeature;

    @Before
    public void setUp() throws Exception {
        ParseData myParser = new ParseData("searchout/test_iphone_6_plus.json",false);
        List<Item> itemList = myParser.readData();

        boolean useSubTitle = false;
        boolean useLocation = false;
        boolean useCondition = false;
        testFeature = new ItemFeature(itemList, useSubTitle, useLocation, useCondition);
        testFeature.TFIDF();
    }

    @Test
    public void getNumDimTest() throws Exception {
        assertEquals(testFeature.getNumDim(), 18);
    }

}