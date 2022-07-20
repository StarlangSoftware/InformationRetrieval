package Document;

import InformationRetrieval.Document.Collection;
import InformationRetrieval.Document.IndexType;
import InformationRetrieval.Document.Parameter;
import org.junit.Test;

public class CollectionTest {

    @Test
    public void testIncidenceMatrixSmall() {
        Parameter parameter = new Parameter();
        parameter.setIndexType(IndexType.INCIDENCE_MATRIX);
        Collection collection = new Collection("testCollection2", parameter);
    }

    @Test
    public void testIncidenceMatrix() {
        Parameter parameter = new Parameter();
        parameter.setIndexType(IndexType.INCIDENCE_MATRIX);
        Collection collection = new Collection("testCollection", parameter);
    }

    @Test
    public void testSaveIndexesToFileSmall() {
        Parameter parameter = new Parameter();
        parameter.setNGramIndex(true);
        Collection collection = new Collection("testCollection2", parameter);
        collection.save();
    }

    @Test
    public void testSaveIndexesToFile() {
        Parameter parameter = new Parameter();
        parameter.setNGramIndex(true);
        Collection collection = new Collection("testCollection", parameter);
        collection.save();
    }

    @Test
    public void testLoadIndexesFromFileSmall() {
        Parameter parameter = new Parameter();
        parameter.setNGramIndex(false);
        parameter.setLoadIndexesFromFile(true);
        Collection collection = new Collection("testCollection2", parameter);
        System.out.println();
    }

    @Test
    public void testLoadIndexesFromFile() {
        Parameter parameter = new Parameter();
        parameter.setLoadIndexesFromFile(true);
        Collection collection = new Collection("testCollection", parameter);
        System.out.println();
    }

    @Test
    public void testConstructIndexesInDiskSmall() {
        Parameter parameter = new Parameter();
        parameter.setConstructIndexInDisk(true);
        parameter.setNGramIndex(false);
        parameter.setDocumentLimit(1);
        Collection collection = new Collection("testCollection2", parameter);
        System.out.println();
    }

    @Test
    public void testConstructIndexesInDisk() {
        Parameter parameter = new Parameter();
        parameter.setConstructIndexInDisk(true);
        parameter.setNGramIndex(false);
        Collection collection = new Collection("testCollection", parameter);
        System.out.println();
    }

    @Test
    public void testLimitNumberOfDocuments() {
        Parameter parameter = new Parameter();
        parameter.setNGramIndex(false);
        parameter.setLimitNumberOfDocumentsLoaded(true);
        parameter.setDocumentLimit(10);
        Collection collection = new Collection("testCollection", parameter);
        System.out.println();
    }

    @Test
    public void testConstructDictionaryAndIndexesInDiskSmall() {
        Parameter parameter = new Parameter();
        parameter.setConstructDictionaryInDisk(true);
        parameter.setDocumentLimit(1);
        parameter.setWordLimit(10);
        Collection collection = new Collection("testCollection2", parameter);
        System.out.println();
    }

    @Test
    public void testConstructDictionaryAndIndexesInDisk() {
        Parameter parameter = new Parameter();
        parameter.setConstructDictionaryInDisk(true);
        parameter.setNGramIndex(false);
        Collection collection = new Collection("testCollection", parameter);
        System.out.println();
    }

}
