package InformationRetrieval;

import Dictionary.EnglishWordComparator;
import InformationRetrieval.Document.Collection;
import InformationRetrieval.Document.DocumentWeighting;
import InformationRetrieval.Document.IndexType;
import InformationRetrieval.Index.TermWeighting;
import Math.Matrix;

public class TestInformationRetrieval {
    private static void testDatasets(){
        Collection collection;
        Matrix distances;
        String[] datasets = {"ant", "camel", "ivy", "jedit", "lucene", "poi", "synapse", "tomcat", "velocity", "xalan"};
        for (int i = 0; i < datasets.length; i++){
            collection = new Collection("/Users/olcay/Dropbox/OlcayHoca-Clone/sources/" + datasets[i] + "/filtered/", "/Users/olcay/Dropbox/OlcayHoca-Clone/" + datasets[i] + ".txt", IndexType.INVERTED_INDEX, new EnglishWordComparator());
            distances = collection.cosineSimilarity(TermWeighting.NATURAL, DocumentWeighting.IDF);
            distances.printToFile(datasets[i] + ".txt");
            System.out.println(datasets[i] + " job done");
        }
    }

    public static void main(String[] args){
        testDatasets();
    }
}
