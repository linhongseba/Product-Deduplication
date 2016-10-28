package main.itemsim;

import info.debatty.java.utils.SparseDoubleVector;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import main.uti.TokenProcessor;

/**
 * Given the Items, created its TFIDF vector representation using tokens from title and optionally other fields
 * @author linhong
 */
public class ItemFeature {
    // whether we will extract tokens from subtitle
    private boolean useSubtitle;

    // whether we will extract tokens from location
    private boolean useLocation;

    // whether we will extract tokens from condition field
    private boolean useCondition;

    // a list of Items
    private List<Item> cands;

    // a tokenization processor, which will tokenize a string into a collection of tokens, get rid of
    // stop words and punctuations (we keep digit here since digit is very useful in the product domain)
    private final TokenProcessor tp;

    //total number of tokens (words) in dictionary.
    private int wordcount;

    //m ap each word in string to an integer ID
    public HashMap<String,Integer> words;

    // the document frequency of each token (word)
    // DF[i] denotes number of documents that contain the word with ID i
    private ArrayList<Integer> DF;

    // the sparse representation for tfidf matrices.
    // each sparseTFIDF[i] denotes the sparse representation of token vectors for Item i
    private SparseDoubleVector []sparseTFIDFs;

    /**
     * Constructor
     * @param cands : a List of Items
     */
    public ItemFeature(List<Item> cands){
        this.cands = cands;
        this.words = new HashMap<String,Integer>(cands.size() * 50);
        wordcount = 0;
        tp = new TokenProcessor();
        this.useSubtitle = false;
        this.useLocation = false;
        this.useCondition = false;
    }

    /**
     * @param cands : a List of Items
     * @param useSubtitle : extract terms from subtitle
     * @param useLocation : extract terms from location
     * @param useCondition : extract terms from condition
     */
    public ItemFeature(List<Item> cands, boolean useSubtitle, boolean useLocation, boolean useCondition){
        this(cands);
        this.useSubtitle = useSubtitle;
        this.useLocation = useLocation;
        this.useCondition = useCondition;
    }

    /**
     * main function to compute TF/IDF
     */
    public void TFIDF() {
        // map each word to an 0-base index, compute the document frequency for each word
        DF = new ArrayList<Integer>(10000);
        for (int i = 0; i<cands.size(); i++) {
            Item p = cands.get(i);
            String content = p.getTitle();

            if (useSubtitle && p.getSubtitle() != null) {
                content += p.getSubtitle();
            }
            if (useLocation && p.getLocation() != null) {
                content += p.getLocation();
            }
            if (useCondition && p.getCondition() != null) {
                content += p.getCondition();
            }
            List<String> tokens = tp.getStrings(content);
            for (int j = 0; j < tokens.size(); j++) {
                String w = tokens.get(j);
                if (words.containsKey(w) == false) {
                    words.put(w, wordcount);
                    DF.add(1);
                    wordcount++;
                } else {
                    int idx = this.words.get(w);
                    DF.set(idx, DF.get(idx) + 1);
                }
            }
        }

        // compute the tfidf vector for each item using tokens from title and other fields
        sparseTFIDFs = new SparseDoubleVector[cands.size()];
        for (int i = 0; i < cands.size(); i++) {
            double[] TF = new double[words.size()];
            Arrays.fill(TF, 0.0);
            Item p = cands.get(i);
            String content = p.getTitle();
            if (useSubtitle && p.getSubtitle() != null) {
                content += p.getSubtitle();
            }
            if (useLocation && p.getLocation() != null) {
                content += p.getLocation();
            }
            if (useCondition && p.getCondition() != null) {
                content += p.getCondition();
            }
            //compute the term frequency for each token in Item i
            List<String> tokens = tp.getStrings(content);
            for (int j = 0; j < tokens.size(); j++){
                String w = tokens.get(j);
                int idx = this.words.get(w);
                TF[idx] += 1.0;
            }
            //compute the TFIDF score for each token in Item i
            for (int j = 0; j < TF.length; j++) {
                TF[j] = TF[j] * Math.log(1+ (double) this.cands.size() / DF.get(j));
            }
            //Item i is represented as a sparse vector of tokens weighted by TFIDF score.
            sparseTFIDFs[i] = new SparseDoubleVector(TF);
        }
    }

    /**
     * get the tfidf vector
     * @return SparseDoubleVector
     */
    public SparseDoubleVector [] getTFIDF(){
        return this.sparseTFIDFs;
    }

    /**
     *  Get the number of dimension (i.e., number of unique words)
     * @return wordcount : number of unique words (Integer)
     */
    public int getNumDim(){
        return this.wordcount;
    }

    /**
     * Print the tfidf vector for the item with index p
     * @param p
     */
    public void printVec(int p){
        System.out.println(sparseTFIDFs[p].toString());
    }

    /**
     * Return the the document frequency.
     */
    public ArrayList<Integer> getDF() {
        return this.DF;
    }

    public static void main(String []args){
        ParseData myParser = new ParseData("searchout/test_iphone_6_plus.json",true);
        List<Item> itemList = myParser.readData();

        boolean useSubTitle = false;
        boolean useLocation = false;
        boolean useCondition = false;
        ItemFeature myFeature = new ItemFeature(itemList, useSubTitle, useLocation, useCondition);
        myFeature.TFIDF();
        for (int i = 0; i < itemList.size(); i++) {
            myFeature.printVec(i);
        }
    }
}
