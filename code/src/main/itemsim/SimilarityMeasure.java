package main.itemsim;

import info.debatty.java.utils.SparseDoubleVector;

/**
 * given two items and their feature vectors, calculate the similarity score of two items
 */
public interface SimilarityMeasure {
    double calculateSimilarityScore(Item a, SparseDoubleVector aFeature, Item b, SparseDoubleVector bFeature);
}
