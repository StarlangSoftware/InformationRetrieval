package InformationRetrieval.Query;

import InformationRetrieval.Document.DocumentWeighting;
import InformationRetrieval.Index.TermWeighting;

public class VectorSpaceModel {
    private double[] model;

    /**
     * Constructor for the VectorSpaceModel class. Calculates the normalized tf-idf vector of a single document.
     * @param termFrequencies Term frequencies in the document
     * @param documentFrequencies Document frequencies of terms.
     * @param documentSize Number of documents in the collection
     * @param termWeighting Term weighting scheme applied in term frequency calculation.
     * @param documentWeighting Document weighting scheme applied in document frequency calculation.
     */
    public VectorSpaceModel(int[] termFrequencies, int[] documentFrequencies, int documentSize, TermWeighting termWeighting, DocumentWeighting documentWeighting){
        double sum = 0;
        model = new double[termFrequencies.length];
        for (int i = 0; i < termFrequencies.length; i++){
            model[i] = weighting(termFrequencies[i], documentFrequencies[i], documentSize, termWeighting, documentWeighting);
            sum += model[i] * model[i];
        }
        for (int i = 0; i < termFrequencies.length; i++){
            model[i] /= Math.sqrt(sum);
        }
    }

    /**
     * Returns the tf-idf value for a column at position index
     * @param index Position of the column
     * @return tf-idf value for a column at position index
     */
    public double get(int index){
        return model[index];
    }

    /**
     * Calculates the cosine similarity between this document vector and the given second document vector.
     * @param secondModel Document vector of the second document.
     * @return Cosine similarity between this document vector and the given second document vector.
     */
    public double cosineSimilarity(VectorSpaceModel secondModel){
        double sum = 0.0;
        if (model.length != secondModel.model.length){
            return 0.0;
        } else {
            for (int i = 0; i < model.length; i++){
                sum += model[i] * secondModel.model[i];
            }
        }
        return sum;
    }

    /**
     * Calculates tf-idf value of a single word (column) of the document vector.
     * @param termFrequency Term frequency of this word in the document
     * @param documentFrequency Document frequency of this word.
     * @param documentSize Number of documents in the collection
     * @param termWeighting Term weighting scheme applied in term frequency calculation.
     * @param documentWeighting Document weighting scheme applied in document frequency calculation.
     * @return tf-idf value of a single word (column) of the document vector.
     */
    public static double weighting(double termFrequency, double documentFrequency, int documentSize, TermWeighting termWeighting, DocumentWeighting documentWeighting){
        double multiplier1 = 1, multiplier2 = 1;
        switch (termWeighting){
            case   NATURAL:
                multiplier1 = termFrequency;
                break;
            case LOGARITHM:
                if (termFrequency > 0)
                    multiplier1 = 1 + Math.log(termFrequency);
                else
                    multiplier1 = 0;
                break;
            case     BOOLE:
                if (termFrequency > 0){
                    multiplier1 = 1;
                } else {
                    multiplier1 = 0;
                }
        }
        switch (documentWeighting){
            case   NO_IDF:
                multiplier2 = 1;
                break;
            case 	   IDF:
                multiplier2 = Math.log(documentSize / (documentFrequency + 0.0));
                break;
            case PROBABILISTIC_IDF:
                if (documentSize > 2 * documentFrequency){
                    multiplier2 = Math.log((documentSize - documentFrequency) / (documentFrequency + 0.0));
                } else {
                    multiplier2 = 0.0;
                }
                break;
        }
        return multiplier1 * multiplier2;
    }

}
