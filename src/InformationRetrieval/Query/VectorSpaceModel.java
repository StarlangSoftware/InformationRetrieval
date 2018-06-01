package InformationRetrieval.Query;

import InformationRetrieval.Document.DocumentWeighting;
import InformationRetrieval.Index.TermWeighting;

public class VectorSpaceModel {
    private double[] model;

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

    public double get(int index){
        return model[index];
    }

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
