package main.itemsim;

import info.debatty.java.utils.SparseDoubleVector;
import main.uti.SimilarityUtil;

/**
 * compute the similarity of two items using a specific similarity metric:
 * sim (i, j) = 0.5 * cosine(i,j) + 0.4 * jaccard(i,j) + 0.1 * simprice(i,j)
 */
public class LinearSimilarity implements SimilarityMeasure {
    public double calculateSimilarityScore(Item a, SparseDoubleVector v1, Item b, SparseDoubleVector v2) {
        double sim = 0;
        sim += 0.5 * SimilarityUtil.cosineSimilarity(v1, v2);
        sim += 0.4 * SimilarityUtil.jaccard(v1, v2);
        double price1 = a.getPrice();
        double price2 = b.getPrice();
        if (price1 != 0 || price2 != 0) {
            double priceSim = Math.abs(price1 - price2) / Math.max(price1, price2);
            sim += 0.1 * (1 - priceSim);
        }
        return sim;
    }
}
