import Dictionary.TurkishWordComparator;
import InformationRetrieval.Document.Parameter;
import InformationRetrieval.Performance.ConfusionMatrix;
import InformationRetrieval.Document.Collection;
import InformationRetrieval.Document.DocumentWeighting;
import InformationRetrieval.Document.IndexType;
import InformationRetrieval.Index.TermWeighting;
import InformationRetrieval.Performance.DotStyle;
import InformationRetrieval.Performance.PerformanceCurve;
import InformationRetrieval.Performance.PlotStyle;
import InformationRetrieval.Query.VectorSpaceModel;
import MorphologicalAnalysis.FsmMorphologicalAnalyzer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class Traceability {

    private final Collection requirements;
    private final Collection classInfo;
    private final int[][] traceability;
    private final double[][] similarities;
    private double[] thresholds;

    /**
     * Constructor for the Tracebility class. Reads class files from class directory, requirements files from requirements directory and
     * traceability matrix from traceability file.
     * @param requirementsDirectory The directory that contains the requirements text files. Each requirement must be in a separate document.
     * @param classDirectory The directory that contains the class text files. Class text files contains the attributes, the method names,
     *                       the parameter names of a class. Each class must have a separate text file.
     * @param traceabilityFile The traceability file name. The traceability file is an M x N matrix, where M is the number of classes
     *                         and N is the number of requirements. Each number is 1 or 0 depending on that requirement is satisfied with
     *                         that class or not.
     */
    public Traceability(String requirementsDirectory, String classDirectory, String traceabilityFile){
        FsmMorphologicalAnalyzer fsm = new FsmMorphologicalAnalyzer();
        Parameter parameter = new Parameter();
        classInfo = new Collection(classDirectory, parameter);
        requirements = new Collection(requirementsDirectory, parameter);
        traceability = new int[classInfo.size()][requirements.size()];
        similarities = new double[classInfo.size()][requirements.size()];
        try {
            Scanner s = new Scanner(new File(traceabilityFile));
            for (int i = 0; i < classInfo.size(); i++)
                for (int j = 0; j < requirements.size(); j++){
                    traceability[i][j] = s.nextInt();
                }
            s.close();
        } catch (FileNotFoundException e) {
        }
    }

    /**
     * Calculates the document similarities between requirements and classes using the given term weighting and document weighting schemes.
     * Each requirement file and class files is taken as a document and converted to vector space model. Calculates also the thresholds for
     * calculating different performance measures such as tpr, fpr, FMeasure etc. Each possible threshold is the average of consecutive
     * similarities.
     * @param termWeighting Term weighting scheme.
     * @param documentWeighting Document weighting scheme.
     */
    private void calculateRequirementsClassSimilarities(TermWeighting termWeighting, DocumentWeighting documentWeighting){
        ArrayList<Double> thresholds = new ArrayList();
        for (int i = 0; i < classInfo.size(); i++){
            VectorSpaceModel model1 = classInfo.getVectorSpaceModel(i, termWeighting, documentWeighting);
            for (int j = 0; j < requirements.size(); j++){
                VectorSpaceModel model2 = requirements.getVectorSpaceModel(j, termWeighting, documentWeighting);
                similarities[i][j] = classInfo.cosineSimilarity(requirements, model1, model2);
                if (!thresholds.contains(similarities[i][j])){
                    thresholds.add(similarities[i][j]);
                }
            }
        }
        Collections.sort(thresholds);
        this.thresholds = new double[thresholds.size() - 1];
        for (int i = 0; i < thresholds.size() - 1; i++)
            this.thresholds[i] = (thresholds.get(i) + thresholds.get(i + 1)) / 2;
    }

    /**
     * Calculates the confusion matrix for a given similarity threshold. If the similarity is above the threshold, the class is associated with
     * that requirement, otherwise it is not associated. Given the gold standard associations, true positives, true negatives, false positives,
     * and false negatives are determined.
     * @param threshold Threshold parameter
     * @return Confusion matrix
     */
    private ConfusionMatrix calculatePerformance(double threshold){
        int truePositives = 0, falsePositives = 0, trueNegatives = 0, falseNegatives = 0;
        for (int i = 0; i < traceability.length; i++)
            for (int j = 0; j < traceability[0].length; j++){
                if (similarities[i][j] <= threshold){
                    if (traceability[i][j] == 0){
                        trueNegatives++;
                    } else {
                        falseNegatives++;
                    }
                } else {
                    if (traceability[i][j] == 1){
                        truePositives++;
                    } else {
                        falsePositives++;
                    }
                }
            }
        return new ConfusionMatrix(truePositives, falsePositives, trueNegatives, falseNegatives);
    }

    /**
     * Generates precision-recall and F-measure performance curves for given term weighting and document weighting schemes. The results are
     * saved in prFile for precision-recall curve, in fCurveFile for F-Measure curve. The curves are saved in pstricks format.
     * @param termWeighting Term weighting scheme.
     * @param documentWeighting Document weighting scheme.
     * @param prFile Pstricks output file for precision-recall curve.
     * @param fCurveFile Pstricks output file for F Measure curve.
     */
    public void plotPerformance(TermWeighting termWeighting, DocumentWeighting documentWeighting, String prFile, String fCurveFile){
        PerformanceCurve prCurve, fCurve;
        calculateRequirementsClassSimilarities(termWeighting, documentWeighting);
        double[] precision = new double[thresholds.length];
        double[] recall = new double[thresholds.length];
        double[] fMeasure = new double[thresholds.length];
        for (int i = 0; i < thresholds.length; i++){
            ConfusionMatrix matrix = calculatePerformance(thresholds[i]);
            precision[i] = matrix.precision();
            recall[i] = matrix.recall();
            fMeasure[i] = matrix.fMeasure();
        }
        prCurve = new PerformanceCurve(recall, precision, true, false, PlotStyle.LINE, DotStyle.STAR, 1);
        fCurve = new PerformanceCurve(thresholds, fMeasure, false, false, PlotStyle.LINE, DotStyle.STAR, 1);
        prCurve.plotPsTricks(prFile);
        fCurve.plotPsTricks(fCurveFile);
    }

    /**
     * Calculates the number of total shared words between requirements and class files given the term weighting and document weighting
     * schemes.
     * @param termWeighting Term weighting scheme
     * @param documentWeighting Document weighting scheme
     * @return Number of shared words between all class files and all requirements files.
     */
    public int sharedWordCount(TermWeighting termWeighting, DocumentWeighting documentWeighting){
        int total = 0;
        for (int i = 0; i < classInfo.size(); i++){
            VectorSpaceModel model1 = classInfo.getVectorSpaceModel(i, termWeighting, documentWeighting);
            for (int j = 0; j < requirements.size(); j++){
                VectorSpaceModel model2 = requirements.getVectorSpaceModel(j, termWeighting, documentWeighting);
                ArrayList<String> sharedWords = classInfo.sharedWordList(requirements, model1, model2);
                total += sharedWords.size();
            }
        }
        return total;
    }

    /**
     * Finds and writes all shared words between requirements and class files given the term weighting and document weighting
     * schemes.
     * @param wordListFile The name of the file where the shared words are saved.
     * @param termWeighting Term weighting scheme
     * @param documentWeighting Document weighting scheme
     */
    public void sharedWordList(String wordListFile, TermWeighting termWeighting, DocumentWeighting documentWeighting){
        try {
            FileWriter fw = new FileWriter(wordListFile);
            fw.write("Documents/Requirements\t");
            for (int j = 0; j < requirements.size(); j++){
                fw.write(requirements.getDocument(j).getFileName() + "\t");
            }
            fw.write("\n");
            for (int i = 0; i < classInfo.size(); i++){
                fw.write(classInfo.getDocument(i).getFileName() + "\t");
                VectorSpaceModel model1 = classInfo.getVectorSpaceModel(i, termWeighting, documentWeighting);
                for (int j = 0; j < requirements.size(); j++){
                    VectorSpaceModel model2 = requirements.getVectorSpaceModel(j, termWeighting, documentWeighting);
                    ArrayList<String> sharedWords = classInfo.sharedWordList(requirements, model1, model2);
                    for (String word:sharedWords){
                        fw.write(word + "-");
                    }
                    fw.write("\t");
                }
                fw.write("\n");
            }
            fw.close();
        } catch (IOException e) {
        }
    }
}
