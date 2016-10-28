package main.uti;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.PorterStemFilter;

public class KStemStandardAnalyzer extends Analyzer {
	public final TokenStream tokenStream(String fieldName, Reader reader) {
		return new PorterStemFilter(new StandardAnalyzer().tokenStream(fieldName, reader));
	}
}
