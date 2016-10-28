package main.itemsim;

import info.debatty.java.lsh.LSHSuperBit;
import info.debatty.java.utils.SparseDoubleVector;

import java.util.ArrayList;
import java.util.List;

/**
 * Given a list of items, call ItemFeature class to obtain the feature vector representations for items,
 * and build the super bits LSH over the feature vectors.
 * @author linhong
 */
public class ItemLSHIndex {
    // a list of item class
    private List<Item> candidates;

    // the hash signature
    private int [][]myHash;

    // the inverted index list in bucketList1,
    // each key is a bucket and its associated value is a list of integer ID of items.
    private ArrayList<Integer> []bucketLists1;

    // the TFIDF feature vector representation for items
    // TFs[i] denotes the TFIDF vector for item i
    private SparseDoubleVector []TFs;

    // the dimension of feature
    private int featureDimention;

    // the average item-pair similarity between items that are from the same bucket
    private double avgSim;

    // the standard deviation of item-pair similarity between items that are from the same bucket
    private double std;

    // similarity measurement
    private LinearSimilarity linearSimilarity;


    public ItemLSHIndex(List<Item> itemList) {
        this.candidates = itemList;
        linearSimilarity = new LinearSimilarity();
        initFeatureVector();
    }

    /**
     * Given the Items, created its tfidf vector representation using tokens from title and (subtitle),
     * We will use the LSH of the tfidf vector as the blocking schema to reduce all pair similarity computations.
     */
    private void initFeatureVector() {
        ItemFeature myfeature = new ItemFeature(candidates);
        myfeature.TFIDF();
        featureDimention = myfeature.getNumDim();
        TFs = myfeature.getTFIDF();
    }

    /**
     * Create the Super bit Hashing
     * @param buckets : number of buckets, the parameters in LSH
     */
    public void createLSH(int buckets) {
        int n = candidates.size(); //number of samples
        // we fix stages = 2
        int stages = 2;
        myHash=new int[n][];

        bucketLists1 = new ArrayList[buckets];
        for(int i = 0;i < buckets;i++){
            bucketLists1[i]=new ArrayList<Integer>(10);
        }

        LSHSuperBit lsh  = new LSHSuperBit(stages, buckets, featureDimention, 368717);
        // Compute a SuperBit signature, and a LSH hash
        for (int i = 0; i < n; i++) {
            SparseDoubleVector v = TFs[i];
            myHash[i] = lsh.hash(v);
            bucketLists1[myHash[i][0]].add(i);
        }
    }

    /**
     * given a searchItemIndex, fina a list of candidateItemIndex
     * @param searchItemIndex
     * @return candidateIndexs a List<Integer> </Integer>
     */
    public List<Integer> findCandidateItems(int searchItemIndex) {
        List<Integer> candidateIndexs = new ArrayList<>();
        int bucket1 = myHash[searchItemIndex][0];
        int bucket2 = myHash[searchItemIndex][1];
        for (int j = 0; j < bucketLists1[bucket1].size(); j++) {
            int simItem = bucketLists1[bucket1].get(j);
            if (simItem == searchItemIndex) {
                continue;
            }
            if (myHash[simItem][1] == bucket2) {
                candidateIndexs.add(simItem);
            }
        }
        return candidateIndexs;
    }

    /**
     * print the LSH signature of each item
     */
    public void printLSH() {
        for (int i = 0; i < myHash.length; i++) {
            System.out.print(candidates.get(i).getID()+"\t"+candidates.get(i).getTitle());
            System.out.println("\t"+myHash[i][0]+"\t"+myHash[i][1]);
        }
    }

    public SparseDoubleVector[] getTFs() {
        return this.TFs;
    }

    /**
     * compute the average and standard deviation of
     * item pair similarity between items that are from the same LSH bucket
     * this function is used to evaluate the quality of LSH
     */
    public void computeAvgSim(){
        avgSim = 0;
        int count = 0;
        for (int i = 0; i < bucketLists1.length; i++) {
            ArrayList<Integer> sims=bucketLists1[i];
            for (int j = 0; j < sims.size(); j++) {
                for (int k = (j+1); k < sims.size(); k++) {
                    avgSim += linearSimilarity.calculateSimilarityScore(candidates.get(j), TFs[j],
                            candidates.get(k), TFs[k]);
                    count ++;
                }
            }
        }
        if (count > 0) {
            avgSim /= count;
        }
        std = 0;
        for (int i = 0; i < bucketLists1.length; i++) {
            ArrayList<Integer> sims=bucketLists1[i];
            for (int j = 0; j < sims.size(); j++) {
                for (int k = (j+1); k < sims.size(); k++) {
                    double sim = linearSimilarity.calculateSimilarityScore(candidates.get(j), TFs[j],
                            candidates.get(k), TFs[k]);
                    System.out.println(sim);
                    std += (sim - avgSim)*(sim-avgSim);
                }
            }
        }
        std = Math.sqrt(std);
        if (count > 0) {
            std /= count;
        }
        System.out.println(avgSim);
        System.out.println(std);
    }

    public void testLSH(){
        for (int i = 0; i < candidates.size(); i++) {
            for (int j = i+1; j < candidates.size(); j++) {
                if ((myHash[i][0] == myHash[j][0]) && (myHash[i][1] == myHash[j][1])) {
                    double sim = linearSimilarity.calculateSimilarityScore(candidates.get(j), TFs[j],
                            candidates.get(i), TFs[i]);
                    System.out.println(sim);
                }
            }
        }
    }

    /**
     *
     * @return the average item similarity between items that are from the same LSH bucket
     */
    public double getAvgSim() {
        return this.avgSim;
    }

    /**
     *
     * @return the standard deviation of item similarity between items that are from the same LSH bucket
     */
    public double getStd() {
        return this.std;
    }

    public static void main(String []args) {
        ParseData myparser = new ParseData("searchout/transformers g1 hound.json",true);
        List<Item> itemList = myparser.readData();
        ItemLSHIndex doindex = new ItemLSHIndex(itemList);
        doindex.createLSH(4);
        doindex.testLSH();
        //doindex.computeAvgSim();
        //doindex.printLSH();
    }
}
