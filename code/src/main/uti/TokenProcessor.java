package main.uti;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;

/**
 * TokenProcessor: tokenize and normalize each token in text
 * We could call either the standard lucene analyzer, with/without stop word removal,
 * with/without port stem, with/without tokenizing digit
 * @author linhong
 */
public class TokenProcessor {
	private static Analyzer kStemStdAnalyzer;

	public TokenProcessor(){
		kStemStdAnalyzer = new KStemStandardAnalyzer();
	}

	public TokenStream getTokenStream(String inputStr){
		TokenStream stream = kStemStdAnalyzer.tokenStream("contents", new StringReader(inputStr));
		return stream;
	}

    /**
     * put each token of the tokenized text into an String and return
     * @param inputStr
     * @return the tokenized Str
     */
	public String getTokenString(String inputStr){
		StringBuffer sb = new StringBuffer();
		TokenStream stream = this.getTokenStream(inputStr);
		try{
			while (true) {
				Token token = stream.next();
				if (token == null) break;
				sb.append(token.termBuffer(), 0, token.termLength()).append(" ");
			}
		}catch(IOException e){
                    e.printStackTrace();
		}
		return sb.toString().toLowerCase().trim();
	}

    /**
     * put each token of the tokenized text into an ArrayList<String> and return
     * @param inputStr
     * @return ArrayList<String> </String>
     */
    public List<String> getStrings(String inputStr){
        List<String> result=new ArrayList<String>(100);
        TokenStream stream = this.getTokenStream(inputStr);
        try{
            while (true) {
                Token token = stream.next();
                if (token == null) break;
                result.add(new String(token.termBuffer(),0,token.termLength()));
            }
        }catch(IOException e){
            e.printStackTrace();
        }
        return result;
    }

	/**
     * Unit Test for the basic tokenization processor
     */
	public static void main(String[] arg){
		String input="www.ntu.edu.sg  ipods ipod ipod.s's iphone] [wii] Copyright � 2006, " +
                "Center for Intelligent Information Retrieval,University of Massachusetts, Amherst. " +
                "All rights reserved. University of Massachusetts must not be used to endorse or " +
                "promote products derived from this software without prior written permission. " +
                "To obtain permission, contact info@ciir.cs.umass.edu.";

        String input2="www.ntu.edu.sg  ipods ipod ipod.s's iphone] [wii] Copyright � 2006, Center" +
                " for Intelligent Information Retrieval,University of Massachusetts, Amherst. " +
                "All rights reserved. University of Massachusetts must not be used to endorse or " +
                "promote products derived from this software without prior written permission. " +
                "To obtain permission, contact info@ciir.cs.umass.edu.";

        String input3="www.ntu.edu.sg  ipods ipod ipod.s's iphone] [wii] Copyright � 2006, " +
                "Center for Intelligent Information Retrieval,University of Massachusetts, Amherst. " +
                "All rights reserved. University of Massachusetts must not be used to endorse" +
                " or promote products derived from this software without prior written permission." +
                " To obtain permission, contact info@ciir.cs.umass.edu.";

        String input4="The AU was originally defined as the length of the semi-major axis" +
                        " of the Earth's elliptical orbit around the Sun. In 1976 the International" +
                        " Astronomical Union revised the definition of the AU for greater precision," +
                        " defining it as that length for which the Gaussian gravitational constant (k)" +
                        " takes the value 0.017 202 098 95 when the units of measurement are the " +
                        "astronomical units of length, mass and time.[5][6][7] An equivalent definition " +
                        "is the radius of an unperturbed circular Newtonian orbit about the Sun of a " +
                        "particle having infinitesimal mass, moving with an angular frequency of " +
                        "0.017 202 098 95 radians per day,[2] or that length for which the heliocentric" +
                        " gravitational constant (the product GM) is equal to (0.017 202 098 95)2 AU3/d2." +
                        " It is approximately equal to the mean Earth-Sun distance.";

		String input5 = "上海";

		TokenProcessor tp = new TokenProcessor();
		System.out.println(tp.getTokenString(input));
		System.out.println(tp.getTokenString(input2));
		System.out.println(tp.getTokenString(input3));
        System.out.println(tp.getTokenString(input4));
        System.out.println(tp.getTokenString(input5));

        List<String> a = tp.getStrings(input);
        for(int i=0;i<a.size();i++){
            System.out.println(a.get(i));
        }
	}

}
