package test.uti;

import main.uti.TokenProcessor;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TokenProcessorTest {
    private TokenProcessor tp = new TokenProcessor();

    @Test
    public void getTokenStringTest() throws Exception {
        String input="ipods ipod ipod.s's iphone] [wii] Copyright � 2006, " +
                "Center for Intelligent Information Retrieval,University of Massachusetts, Amherst. ";

        String expected = "ipod ipod ipod. s iphon wii copyright 2006 center intellig inform retriev" +
                " univers massachusett amherst";
        assertEquals(expected, tp.getTokenString(input));
    }

    @Test
    public void getStringsTest() throws Exception {
        String input="ipods ipod ipod.s's iphone] [wii] Copyright � 2006, " +
                "Center for Intelligent Information Retrieval,University of Massachusetts, Amherst. ";

        List<String> list = Arrays.asList("ipod", "ipod", "ipod.", "s", "iphon", "wii", "copyright",
                "2006", "center", "intellig", "inform", "retriev", "univers", "massachusett", "amherst");
        assertEquals(list, tp.getStrings(input));

    }
}