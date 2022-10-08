package Document;

import InformationRetrieval.Document.Collection;
import InformationRetrieval.Document.DocumentWeighting;
import InformationRetrieval.Document.IndexType;
import InformationRetrieval.Document.Parameter;
import InformationRetrieval.Index.TermWeighting;
import InformationRetrieval.Query.Query;
import InformationRetrieval.Query.QueryResult;
import InformationRetrieval.Query.RetrievalType;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CollectionTest {

    @Test
    public void testIncidenceMatrixSmall() {
        Parameter parameter = new Parameter();
        parameter.setIndexType(IndexType.INCIDENCE_MATRIX);
        Collection collection = new Collection("testCollection2", parameter);
        assertEquals(2, collection.size());
        assertEquals(26, collection.vocabularySize());
    }

    @Test
    public void testIncidenceMatrixQuery() {
        Parameter parameter = new Parameter();
        parameter.setIndexType(IndexType.INCIDENCE_MATRIX);
        Collection collection = new Collection("testCollection2", parameter);
        Query query = new Query("Brutus");
        QueryResult result = collection.searchCollection(query, RetrievalType.BOOLEAN, TermWeighting.NATURAL, DocumentWeighting.NO_IDF);
        assertEquals(2, result.getItems().size());
        query = new Query("Brutus Caesar");
        result = collection.searchCollection(query, RetrievalType.BOOLEAN, TermWeighting.NATURAL, DocumentWeighting.NO_IDF);
        assertEquals(2, result.getItems().size());
        query = new Query("enact");
        result = collection.searchCollection(query, RetrievalType.BOOLEAN, TermWeighting.NATURAL, DocumentWeighting.NO_IDF);
        assertEquals(1, result.getItems().size());
        query = new Query("noble");
        result = collection.searchCollection(query, RetrievalType.BOOLEAN, TermWeighting.NATURAL, DocumentWeighting.NO_IDF);
        assertEquals(1, result.getItems().size());
        query = new Query("a");
        result = collection.searchCollection(query, RetrievalType.BOOLEAN, TermWeighting.NATURAL, DocumentWeighting.NO_IDF);
        assertEquals(0, result.getItems().size());
    }

    @Test
    public void testInvertedIndexBooleanQuery() {
        Parameter parameter = new Parameter();
        parameter.setNGramIndex(true);
        Collection collection = new Collection("testCollection2", parameter);
        Query query = new Query("Brutus");
        QueryResult result = collection.searchCollection(query, RetrievalType.BOOLEAN, TermWeighting.NATURAL, DocumentWeighting.NO_IDF);
        assertEquals(2, result.getItems().size());
        query = new Query("Brutus Caesar");
        result = collection.searchCollection(query, RetrievalType.BOOLEAN, TermWeighting.NATURAL, DocumentWeighting.NO_IDF);
        assertEquals(2, result.getItems().size());
        query = new Query("enact");
        result = collection.searchCollection(query, RetrievalType.BOOLEAN, TermWeighting.NATURAL, DocumentWeighting.NO_IDF);
        assertEquals(1, result.getItems().size());
        query = new Query("noble");
        result = collection.searchCollection(query, RetrievalType.BOOLEAN, TermWeighting.NATURAL, DocumentWeighting.NO_IDF);
        assertEquals(1, result.getItems().size());
        query = new Query("a");
        result = collection.searchCollection(query, RetrievalType.BOOLEAN, TermWeighting.NATURAL, DocumentWeighting.NO_IDF);
        assertEquals(0, result.getItems().size());
    }

    @Test
    public void testPositionalIndexBooleanQuery() {
        Parameter parameter = new Parameter();
        parameter.setNGramIndex(true);
        Collection collection = new Collection("testCollection2", parameter);
        Query query = new Query("Julius Caesar");
        QueryResult result = collection.searchCollection(query, RetrievalType.POSITIONAL, TermWeighting.NATURAL, DocumentWeighting.NO_IDF);
        assertEquals(2, result.getItems().size());
        query = new Query("I was killed");
        result = collection.searchCollection(query, RetrievalType.POSITIONAL, TermWeighting.NATURAL, DocumentWeighting.NO_IDF);
        assertEquals(1, result.getItems().size());
        query = new Query("The noble Brutus");
        result = collection.searchCollection(query, RetrievalType.POSITIONAL, TermWeighting.NATURAL, DocumentWeighting.NO_IDF);
        assertEquals(1, result.getItems().size());
        query = new Query("a");
        result = collection.searchCollection(query, RetrievalType.POSITIONAL, TermWeighting.NATURAL, DocumentWeighting.NO_IDF);
        assertEquals(0, result.getItems().size());
    }

    @Test
    public void testPositionalIndexRankedQuery() {
        Parameter parameter = new Parameter();
        parameter.setLoadIndexesFromFile(true);
        Collection collection = new Collection("testCollection2", parameter);
        Query query = new Query("Caesar");
        QueryResult result = collection.searchCollection(query, RetrievalType.RANKED, TermWeighting.NATURAL, DocumentWeighting.NO_IDF);
        assertEquals(2, result.getItems().size());
        assertEquals(1, result.getItems().get(0).getDocId());
        query = new Query("Caesar was killed");
        result = collection.searchCollection(query, RetrievalType.RANKED, TermWeighting.NATURAL, DocumentWeighting.NO_IDF);
        assertEquals(2, result.getItems().size());
        assertEquals(0, result.getItems().get(0).getDocId());
        query = new Query("in the Capitol");
        result = collection.searchCollection(query, RetrievalType.RANKED, TermWeighting.NATURAL, DocumentWeighting.NO_IDF);
        assertEquals(1, result.getItems().size());
        query = new Query("a");
        result = collection.searchCollection(query, RetrievalType.RANKED, TermWeighting.NATURAL, DocumentWeighting.NO_IDF);
        assertEquals(0, result.getItems().size());
    }

    @Test
    public void testSaveIndexesToFileSmall() {
        Parameter parameter = new Parameter();
        parameter.setNGramIndex(true);
        Collection collection = new Collection("testCollection2", parameter);
        collection.save();
    }

    @Test
    public void testLoadIndexesFromFileSmall() {
        Parameter parameter = new Parameter();
        parameter.setNGramIndex(true);
        parameter.setLoadIndexesFromFile(true);
        Collection collection = new Collection("testCollection2", parameter);
        assertEquals(2, collection.size());
        assertEquals(26, collection.vocabularySize());
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
    public void testLimitNumberOfDocumentsSmall() {
        Parameter parameter = new Parameter();
        parameter.setNGramIndex(false);
        parameter.setLimitNumberOfDocumentsLoaded(true);
        parameter.setDocumentLimit(1);
        Collection collection = new Collection("testCollection2", parameter);
        assertEquals(1, collection.size());
        assertEquals(15, collection.vocabularySize());
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

}
