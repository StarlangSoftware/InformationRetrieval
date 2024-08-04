package InformationRetrieval.Document;

import InformationRetrieval.Index.*;
import InformationRetrieval.Query.*;

import java.io.*;
import java.util.*;

public class MemoryCollection extends AbstractCollection {
    private final IndexType indexType;

    /**
     * Constructor for the MemoryCollection class. In small collections, dictionary and indexes are kept in memory.
     * Memory collection also supports categorical documents.
     * @param directory Directory where the document collection resides.
     * @param parameter Search parameter
     */
    public MemoryCollection(String directory, Parameter parameter) {
        super(directory, parameter);
        this.indexType = parameter.getIndexType();
        if (parameter.loadIndexesFromFile()) {
            loadIndexesFromFile(directory);
        } else {
            constructIndexesInMemory();
        }
        if (parameter.getDocumentType() == DocumentType.CATEGORICAL) {
            positionalIndex.setCategoryCounts(documents);
            categoryTree.setRepresentativeCount(parameter.getRepresentativeCount());
        }
    }

    /**
     * The method loads the term dictionary, inverted index, positional index, phrase and N-Gram indexes from dictionary
     * and index files to the memory.
     * @param directory Directory where the document collection resides.
     */
    protected void loadIndexesFromFile(String directory) {
        dictionary = new TermDictionary(comparator, directory);
        invertedIndex = new InvertedIndex(directory);
        if (parameter.constructPositionalIndex()) {
            positionalIndex = new PositionalIndex(directory);
            positionalIndex.setDocumentSizes(documents);
        }
        if (parameter.constructPhraseIndex()) {
            phraseDictionary = new TermDictionary(comparator, directory + "-phrase");
            phraseIndex = new InvertedIndex(directory + "-phrase");
            if (parameter.constructPositionalIndex()) {
                phrasePositionalIndex = new PositionalIndex(directory + "-phrase");
            }
        }
        if (parameter.constructNGramIndex()) {
            biGramDictionary = new TermDictionary(comparator, directory + "-biGram");
            triGramDictionary = new TermDictionary(comparator, directory + "-triGram");
            biGramIndex = new NGramIndex(directory + "-biGram");
            triGramIndex = new NGramIndex(directory + "-triGram");
        }
    }

    /**
     * The method saves the term dictionary, inverted index, positional index, phrase and N-Gram indexes to the dictionary
     * and index files. If the collection is a categorical collection, categories are also saved to the category
     * files.
     */
    public void save() {
        if (indexType == IndexType.INVERTED_INDEX) {
            dictionary.save(name);
            invertedIndex.save(name);
            if (parameter.constructPositionalIndex()) {
                positionalIndex.save(name);
            }
            if (parameter.constructPhraseIndex()) {
                phraseDictionary.save(name + "-phrase");
                phraseIndex.save(name + "-phrase");
                if (parameter.constructPositionalIndex()) {
                    phrasePositionalIndex.save(name + "-phrase");
                }
            }
            if (parameter.constructNGramIndex()) {
                biGramDictionary.save(name + "-biGram");
                triGramDictionary.save(name + "-triGram");
                biGramIndex.save(name + "-biGram");
                triGramIndex.save(name + "-triGram");
            }
        }
        if (parameter.getDocumentType() == DocumentType.CATEGORICAL) {
            saveCategories();
        }
    }

    /**
     * The method saves the category tree for the categorical collections.
     */
    private void saveCategories() {
        try {
            PrintWriter printWriter = new PrintWriter(name + "-categories.txt", "UTF-8");
            for (Document document : documents) {
                printWriter.write(document.getDocId() + "\t" + document.getCategory() + "\n");
            }
            printWriter.close();
        } catch (IOException ignored) {
        }
    }

    /**
     * The method constructs the term dictionary, inverted index, positional index, phrase and N-Gram indexes in memory.
     */
    private void constructIndexesInMemory() {
        ArrayList<TermOccurrence> terms = constructTerms(TermType.TOKEN);
        dictionary = new TermDictionary(comparator, terms);
        switch (indexType) {
            case INCIDENCE_MATRIX:
                incidenceMatrix = new IncidenceMatrix(terms, dictionary, documents.size());
                break;
            case INVERTED_INDEX:
                invertedIndex = new InvertedIndex(dictionary, terms, comparator);
                if (parameter.constructPositionalIndex()) {
                    positionalIndex = new PositionalIndex(dictionary, terms, comparator);
                }
                if (parameter.constructPhraseIndex()) {
                    terms = constructTerms(TermType.PHRASE);
                    phraseDictionary = new TermDictionary(comparator, terms);
                    phraseIndex = new InvertedIndex(phraseDictionary, terms, comparator);
                    if (parameter.constructPositionalIndex()) {
                        phrasePositionalIndex = new PositionalIndex(phraseDictionary, terms, comparator);
                    }
                }
                if (parameter.constructNGramIndex()) {
                    constructNGramIndex();
                }
                if (parameter.getDocumentType() == DocumentType.CATEGORICAL) {
                    categoryTree = new CategoryTree(name);
                    for (Document document : documents) {
                        document.loadCategory(categoryTree);
                    }
                }
                break;
        }
    }

    /**
     * Given the document collection, creates an array list of terms. If term type is TOKEN, the terms are single
     * word, if the term type is PHRASE, the terms are bi-words. Each document is loaded into memory and
     * word list is created. Since the dictionary can be kept in memory, all operations can be done in memory.
     * @param termType If term type is TOKEN, the terms are single word, if the term type is PHRASE, the terms are
     *                 bi-words.
     * @return Array list of terms occurring in the document collection.
     */
    private ArrayList<TermOccurrence> constructTerms(TermType termType) {
        TermOccurrenceComparator termComparator = new TermOccurrenceComparator(comparator);
        ArrayList<TermOccurrence> terms = new ArrayList<>();
        ArrayList<TermOccurrence> docTerms;
        for (Document doc : documents) {
            DocumentText documentText = doc.loadDocument();
            docTerms = documentText.constructTermList(doc.getDocId(), termType);
            terms.addAll(docTerms);
        }
        terms.sort(termComparator);
        return terms;
    }

    /**
     * The method searches given query string in the document collection using the attribute list according to the
     * given search parameter. First, the original query is filtered by removing phrase attributes, shortcuts and single
     * word attributes. At this stage, we get the word and phrase attributes in the original query and the remaining
     * words in the original query as two separate queries. Second, both single word and phrase attributes in the
     * original query are searched in the document collection. Third, these intermediate query results are then
     * intersected. Fourth, we put this results into either (i) an inverted index (ii) or a ranked based positional
     * filtering with the filtered query to get the end result.
     * @param query Query string
     * @param parameter Search parameter for the query
     * @return The intermediate result of the query obtained by doing attribute list based search in the collection.
     */
    private QueryResult attributeSearch(Query query, SearchParameter parameter) {
        Query termAttributes = new Query();
        Query phraseAttributes = new Query();
        QueryResult termResult = new QueryResult(), phraseResult = new QueryResult();
        QueryResult attributeResult, filteredResult;
        Query filteredQuery = query.filterAttributes(attributeList, termAttributes, phraseAttributes);
        if (termAttributes.size() > 0) {
            termResult = invertedIndex.search(termAttributes, dictionary);
        }
        if (phraseAttributes.size() > 0) {
            phraseResult = phraseIndex.search(phraseAttributes, phraseDictionary);
        }
        if (termAttributes.size() == 0) {
            attributeResult = phraseResult;
        } else {
            if (phraseAttributes.size() == 0) {
                attributeResult = termResult;
            } else {
                attributeResult = termResult.intersectionFastSearch(phraseResult);
            }
        }
        if (filteredQuery.size() == 0){
            return attributeResult;
        } else {
            if (parameter.getRetrievalType() != RetrievalType.RANKED) {
                filteredResult = searchWithInvertedIndex(filteredQuery, parameter);
                return filteredResult.intersectionFastSearch(attributeResult);
            } else {
                filteredResult = positionalIndex.rankedSearch(filteredQuery,
                        dictionary,
                        documents,
                        parameter);
                if (attributeResult.size() < 10) {
                    filteredResult = filteredResult.intersectionLinearSearch(attributeResult);
                } else {
                    filteredResult = filteredResult.intersectionBinarySearch(attributeResult);
                }
                filteredResult.getBest(parameter.getDocumentsRetrieved());
                return filteredResult;
            }
        }
    }

    /**
     * The method searches given query string in the document collection using the inverted index according to the
     * given search parameter. If the search is (i) boolean, inverted index is used (ii) positional, positional
     * inverted index is used, (iii) ranked, positional inverted index is used with a ranking algorithm at the end.
     * @param query Query string
     * @param parameter Search parameter for the query
     * @return The intermediate result of the query obtained by doing inverted index based search in the collection.
     */
    private QueryResult searchWithInvertedIndex(Query query, SearchParameter parameter) {
        switch (parameter.getRetrievalType()) {
            case BOOLEAN:
                return invertedIndex.search(query, dictionary);
            case POSITIONAL:
                return positionalIndex.positionalSearch(query, dictionary);
            case RANKED:
                QueryResult result = positionalIndex.rankedSearch(query,
                        dictionary,
                        documents,
                        parameter);
                result.getBest(parameter.getDocumentsRetrieved());
                return result;
        }
        return new QueryResult();
    }

    /**
     * Filters current search result according to the predicted categories from the query string. For every search
     * result, if it is in one of the predicated categories, is added to the filtered end result. Otherwise, it is
     * omitted in the end result.
     * @param currentResult Current search result before filtering.
     * @param categories Predicted categories that match the query string.
     * @return Filtered query result
     */
    private QueryResult filterAccordingToCategories(QueryResult currentResult, ArrayList<CategoryNode> categories) {
        QueryResult filteredResult = new QueryResult();
        ArrayList<QueryResultItem> items = currentResult.getItems();
        for (QueryResultItem queryResultItem : items) {
            CategoryNode categoryNode = documents.get(queryResultItem.getDocId()).getCategoryNode();
            for (CategoryNode possibleAncestor : categories) {
                if (categoryNode.isDescendant(possibleAncestor)) {
                    filteredResult.add(queryResultItem.getDocId(), queryResultItem.getScore());
                    break;
                }
            }
        }
        return filteredResult;
    }

    /**
     * Searches a document collection for a given query according to the given search parameters. The documents are
     * searched using (i) incidence matrix if the index type is incidence matrix, (ii) attribute list if search
     * attributes option is selected, (iii) inverted index if the index type is inverted index and no attribute
     * search is done. After the initial search, if there is a categorical focus, it filters the results
     * according to the predicted categories from the query string.
     * @param query Query string
     * @param searchParameter Search parameter for the query
     * @return The result of the query obtained by doing search in the collection.
     */
    public QueryResult searchCollection(Query query, SearchParameter searchParameter) {
        QueryResult currentResult;
        if (searchParameter.getFocusType().equals(FocusType.CATEGORY)){
            if (searchParameter.getSearchAttributes()){
                currentResult = attributeSearch(query, searchParameter);
            } else {
                currentResult = searchWithInvertedIndex(query, searchParameter);
            }
            ArrayList<CategoryNode> categories = categoryTree.getCategories(query, dictionary, searchParameter.getCategoryDeterminationType());
            return filterAccordingToCategories(currentResult, categories);
        } else {
            switch (indexType){
                case INCIDENCE_MATRIX:
                    return incidenceMatrix.search(query, dictionary);
                case   INVERTED_INDEX:
                    if (searchParameter.getSearchAttributes()){
                        return attributeSearch(query, searchParameter);
                    } else {
                        return searchWithInvertedIndex(query, searchParameter);
                    }
            }
        }
        return new QueryResult();
    }

    /**
     * Constructs an auto complete list of product names for a given prefix. THe results are sorted according to
     * frequencies.
     * @param prefix Prefix of the name of the product.
     * @return An auto complete list of product names for a given prefix.
     */
    public ArrayList<String> autoCompleteWord(String prefix) {
        ArrayList<String> result = new ArrayList<>();
        int i = dictionary.getWordStartingWith(prefix);
        while (i < dictionary.size()) {
            if (dictionary.getWord(i).getName().startsWith(prefix)) {
                result.add(dictionary.getWord(i).getName());
            } else {
                break;
            }
            i++;
        }
        invertedIndex.autoCompleteWord(result, dictionary);
        return result;
    }

}
