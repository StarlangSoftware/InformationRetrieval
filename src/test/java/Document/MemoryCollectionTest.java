package Document;

import InformationRetrieval.Document.MemoryCollection;
import InformationRetrieval.Document.DocumentType;
import InformationRetrieval.Document.IndexType;
import InformationRetrieval.Document.Parameter;
import InformationRetrieval.Query.*;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class MemoryCollectionTest {

    @Test
    public void testIncidenceMatrixSmall() {
        Parameter parameter = new Parameter();
        parameter.setIndexType(IndexType.INCIDENCE_MATRIX);
        MemoryCollection memoryCollection = new MemoryCollection("testCollection2", parameter);
        assertEquals(2, memoryCollection.size());
        assertEquals(26, memoryCollection.vocabularySize());
    }

    @Test
    public void testIncidenceMatrixQuery() {
        Parameter parameter = new Parameter();
        parameter.setIndexType(IndexType.INCIDENCE_MATRIX);
        MemoryCollection memoryCollection = new MemoryCollection("testCollection2", parameter);
        Query query = new Query("Brutus");
        SearchParameter searchParameter = new SearchParameter();
        searchParameter.setRetrievalType(RetrievalType.BOOLEAN);
        QueryResult result = memoryCollection.searchCollection(query, searchParameter);
        assertEquals(2, result.getItems().size());
        query = new Query("Brutus Caesar");
        result = memoryCollection.searchCollection(query, searchParameter);
        assertEquals(2, result.getItems().size());
        query = new Query("enact");
        result = memoryCollection.searchCollection(query, searchParameter);
        assertEquals(1, result.getItems().size());
        query = new Query("noble");
        result = memoryCollection.searchCollection(query, searchParameter);
        assertEquals(1, result.getItems().size());
        query = new Query("a");
        result = memoryCollection.searchCollection(query, searchParameter);
        assertEquals(0, result.getItems().size());
    }

    @Test
    public void testInvertedIndexBooleanQuery() {
        Parameter parameter = new Parameter();
        parameter.setNGramIndex(true);
        MemoryCollection memoryCollection = new MemoryCollection("testCollection2", parameter);
        Query query = new Query("Brutus");
        SearchParameter searchParameter = new SearchParameter();
        searchParameter.setRetrievalType(RetrievalType.BOOLEAN);
        QueryResult result = memoryCollection.searchCollection(query, searchParameter);
        assertEquals(2, result.getItems().size());
        query = new Query("Brutus Caesar");
        result = memoryCollection.searchCollection(query, searchParameter);
        assertEquals(2, result.getItems().size());
        query = new Query("enact");
        result = memoryCollection.searchCollection(query, searchParameter);
        assertEquals(1, result.getItems().size());
        query = new Query("noble");
        result = memoryCollection.searchCollection(query, searchParameter);
        assertEquals(1, result.getItems().size());
        query = new Query("a");
        result = memoryCollection.searchCollection(query, searchParameter);
        assertEquals(0, result.getItems().size());
    }

    @Test
    public void testPositionalIndexBooleanQuery() {
        Parameter parameter = new Parameter();
        parameter.setNGramIndex(true);
        MemoryCollection memoryCollection = new MemoryCollection("testCollection2", parameter);
        Query query = new Query("Julius Caesar");
        SearchParameter searchParameter = new SearchParameter();
        searchParameter.setRetrievalType(RetrievalType.POSITIONAL);
        QueryResult result = memoryCollection.searchCollection(query, searchParameter);
        assertEquals(2, result.getItems().size());
        query = new Query("I was killed");
        result = memoryCollection.searchCollection(query, searchParameter);
        assertEquals(1, result.getItems().size());
        query = new Query("The noble Brutus");
        result = memoryCollection.searchCollection(query, searchParameter);
        assertEquals(1, result.getItems().size());
        query = new Query("a");
        result = memoryCollection.searchCollection(query, searchParameter);
        assertEquals(0, result.getItems().size());
    }

    @Test
    public void testPositionalIndexRankedQuery() {
        Parameter parameter = new Parameter();
        parameter.setLoadIndexesFromFile(true);
        MemoryCollection memoryCollection = new MemoryCollection("testCollection2", parameter);
        Query query = new Query("Caesar");
        SearchParameter searchParameter = new SearchParameter();
        searchParameter.setRetrievalType(RetrievalType.RANKED);
        searchParameter.setDocumentsRetrieved(2);
        QueryResult result = memoryCollection.searchCollection(query, searchParameter);
        assertEquals(2, result.getItems().size());
        assertEquals(1, result.getItems().get(0).getDocId());
        query = new Query("Caesar was killed");
        result = memoryCollection.searchCollection(query, searchParameter);
        assertEquals(2, result.getItems().size());
        assertEquals(0, result.getItems().get(0).getDocId());
        query = new Query("in the Capitol");
        result = memoryCollection.searchCollection(query, searchParameter);
        assertEquals(1, result.getItems().size());
        query = new Query("a");
        result = memoryCollection.searchCollection(query, searchParameter);
        assertEquals(0, result.getItems().size());
    }

    @Test
    public void testLoadIndexesFromFileSmall() {
        Parameter parameter = new Parameter();
        parameter.setNGramIndex(true);
        parameter.setLoadIndexesFromFile(true);
        MemoryCollection memoryCollection = new MemoryCollection("testCollection2", parameter);
        assertEquals(2, memoryCollection.size());
        assertEquals(26, memoryCollection.vocabularySize());
    }

    @Test
    public void testLimitNumberOfDocumentsSmall() {
        Parameter parameter = new Parameter();
        parameter.setNGramIndex(false);
        parameter.setLimitNumberOfDocumentsLoaded(true);
        parameter.setDocumentLimit(1);
        MemoryCollection memoryCollection = new MemoryCollection("testCollection2", parameter);
        assertEquals(1, memoryCollection.size());
        assertEquals(15, memoryCollection.vocabularySize());
    }

    @Test
    public void testCategoricalCollection() {
        Parameter parameter = new Parameter();
        parameter.setDocumentType(DocumentType.CATEGORICAL);
        parameter.setLoadIndexesFromFile(true);
        parameter.setPhraseIndex(false);
        parameter.setNGramIndex(false);
        MemoryCollection memoryCollection = new MemoryCollection("testCollection3", parameter);
        assertEquals(1000, memoryCollection.size());
        assertEquals(2283, memoryCollection.vocabularySize());
    }
    @Test
    public void testAttributeQuery() {
        Parameter parameter = new Parameter();
        parameter.setDocumentType(DocumentType.CATEGORICAL);
        parameter.setLoadIndexesFromFile(true);
        MemoryCollection memoryCollection = new MemoryCollection("testCollection3", parameter);
        SearchParameter searchParameter = new SearchParameter();
        searchParameter.setSearchAttributes(true);
        searchParameter.setDocumentsRetrieved(400);
        searchParameter.setRetrievalType(RetrievalType.RANKED);
        Query query = new Query("Çift Yönlü");
        QueryResult result = memoryCollection.searchCollection(query, searchParameter);
        assertEquals(10, result.getItems().size());
        query = new Query("Müzikli");
        result = memoryCollection.searchCollection(query, searchParameter);
        assertEquals(4, result.getItems().size());
        query = new Query("Çift Yönlü Alüminyum Bebek Arabası");
        result = memoryCollection.searchCollection(query, searchParameter);
        assertEquals(2, result.getItems().size());
    }

    @Test
    public void testCategoricalQuery() {
        Parameter parameter = new Parameter();
        parameter.setDocumentType(DocumentType.CATEGORICAL);
        parameter.setLoadIndexesFromFile(true);
        MemoryCollection memoryCollection = new MemoryCollection("testCollection3", parameter);
        SearchParameter searchParameter = new SearchParameter();
        searchParameter.setFocusType(FocusType.CATEGORY);
        searchParameter.setRetrievalType(RetrievalType.BOOLEAN);
        Query query = new Query("Çift Yönlü Bebek Arabası");
        QueryResult result = memoryCollection.searchCollection(query, searchParameter);
        assertEquals(10, result.getItems().size());
        searchParameter.setRetrievalType(RetrievalType.BOOLEAN);
        query = new Query("Terlik");
        result = memoryCollection.searchCollection(query, searchParameter);
        assertEquals(5, result.getItems().size());
    }

    @Test
    public void testAutoCompleteWord() {
        Parameter parameter = new Parameter();
        parameter.setNGramIndex(true);
        parameter.setLoadIndexesFromFile(true);
        MemoryCollection memoryCollection = new MemoryCollection("testCollection2", parameter);
        ArrayList<String> autoCompleteList = memoryCollection.autoCompleteWord("kill");
        assertEquals(1, autoCompleteList.size());
        autoCompleteList = memoryCollection.autoCompleteWord("Ca");
        assertEquals(2, autoCompleteList.size());
        memoryCollection = new MemoryCollection("testCollection3", parameter);
        parameter.setDocumentType(DocumentType.CATEGORICAL);
        autoCompleteList = memoryCollection.autoCompleteWord("Yeni");
        assertEquals(6, autoCompleteList.size());
        autoCompleteList = memoryCollection.autoCompleteWord("Ka");
        assertEquals(68, autoCompleteList.size());
        autoCompleteList = memoryCollection.autoCompleteWord("Bebe");
        assertEquals(12, autoCompleteList.size());
    }

}
