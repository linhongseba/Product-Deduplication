package main.uti;

import info.debatty.java.utils.SparseDoubleVector;

public class SimilarityUtil {
    /**
     * compute the exact jaccard similarity between feature vectors of two items
     */
    public static double jaccard(SparseDoubleVector v1, SparseDoubleVector v2) {
        return v1.jaccard(v2);
    }

    /**
     * compute the exact cosine similarity between feature vectors of two items
     */
    public static double cosineSimilarity(SparseDoubleVector v1, SparseDoubleVector v2) {
        return v1.cosineSimilarity(v2);
    }

    /**
     * compute the exact nGram similarity between feature vectors of two items
     */
    public static double nGram(SparseDoubleVector v1, SparseDoubleVector v2){
        return v1.qgram(v2);
    }
}
