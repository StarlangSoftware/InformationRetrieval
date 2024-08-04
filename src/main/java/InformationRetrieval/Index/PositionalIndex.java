package InformationRetrieval.Index;

import Dictionary.WordComparator;
import InformationRetrieval.Document.Document;
import InformationRetrieval.Query.Query;
import InformationRetrieval.Query.QueryResult;
import InformationRetrieval.Query.SearchParameter;
import InformationRetrieval.Query.VectorSpaceModel;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

/**
 * Positional index is an extension of inverted index, the postings lists are replaced with positional postings lists.
 */
public class PositionalIndex {

    private final TreeMap<Integer, PositionalPostingList> positionalIndex;

    /**
     * Constructs an empty inverted index.
     */
    public PositionalIndex(){
        positionalIndex = new TreeMap<>();
    }

    /**
     * Reads the positional inverted index from an input file.
     * @param fileName Input file name for the positional inverted index.
     */
    public PositionalIndex(String fileName){
        positionalIndex = new TreeMap<>();
        readPositionalPostingList(fileName);
    }

    /**
     * Constructs a positional inverted index from a list of sorted tokens. The terms array should be sorted before
     * calling this method. Multiple occurrences of the same term from the same document are enlisted separately in the
     * index.
     * @param dictionary Term dictionary
     * @param terms Sorted list of tokens in the memory collection.
     * @param comparator Comparator method to compare two terms.
     */
    public PositionalIndex(TermDictionary dictionary, ArrayList<TermOccurrence> terms, WordComparator comparator){
        this();
        int i, termId, prevDocId;
        TermOccurrence term, previousTerm;
        if (!terms.isEmpty()){
            term = terms.get(0);
            i = 1;
            previousTerm = term;
            termId = dictionary.getWordIndex(term.getTerm().getName());
            addPosition(termId, term.getDocID(), term.getPosition());
            prevDocId = term.getDocID();
            while (i < terms.size()){
                term = terms.get(i);
                termId = dictionary.getWordIndex(term.getTerm().getName());
                if (termId != -1){
                    if (term.isDifferent(previousTerm, comparator)){
                        addPosition(termId, term.getDocID(), term.getPosition());
                        prevDocId = term.getDocID();
                    } else {
                        if (prevDocId != term.getDocID()){
                            addPosition(termId, term.getDocID(), term.getPosition());
                            prevDocId = term.getDocID();
                        } else {
                            addPosition(termId, term.getDocID(), term.getPosition());
                        }
                    }
                } else {
                    System.out.println("Error: Term " + term.getTerm().getName() + " does not exist");
                }
                i++;
                previousTerm = term;
            }
        }
    }

    /**
     * Reads the positional postings list of the positional index from an input file. The postings are stored in n
     * lines. The first line contains the term id and the number of documents that term occurs. Other n - 1 lines
     * contain the postings list for that term for a separate document.
     * @param fileName Positional index file.
     */
    private void readPositionalPostingList(String fileName){
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(fileName + "-positionalPostings.txt")), StandardCharsets.UTF_8));
            String line = br.readLine();
            while (line != null){
                String[] items = line.split(" ");
                int wordId = Integer.parseInt(items[0]);
                positionalIndex.put(wordId, new PositionalPostingList(br, Integer.parseInt(items[1])));
                line = br.readLine();
            }
            br.close();
        } catch (IOException ignored) {
        }
    }

    /**
     * Saves the positional index into the index file. The postings are stored in n lines. The first line contains the
     * term id and the number of documents that term occurs. Other n - 1 lines contain the postings list for that term
     * for a separate document.
     * @param fileName Index file name. Real index file name is created by attaching -positionalPostings.txt to this
     *                 file name
     */
    public void save(String fileName){
        try {
            PrintWriter printWriter = new PrintWriter(fileName + "-positionalPostings.txt", "UTF-8");
            for (Integer key : positionalIndex.keySet()){
                positionalIndex.get(key).writeToFile(printWriter, key);
            }
            printWriter.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Adds a possible new term with a position and document id to the positional index. First the term is searched in
     * the hash map, then the position and the document id is put into the correct postings list.
     * @param termId Id of the term
     * @param docId Document id in which the term exists
     * @param position Position of the term in the document with id docId
     */
    public void addPosition(int termId, int docId, int position){
        PositionalPostingList positionalPostingList;
        if (!positionalIndex.containsKey(termId)){
            positionalPostingList = new PositionalPostingList();
        } else {
            positionalPostingList = positionalIndex.get(termId);
        }
        positionalPostingList.add(docId, position);
        positionalIndex.put(termId, positionalPostingList);
    }

    /**
     * Searches a given query in the document collection using positional index boolean search.
     * @param query Query string
     * @param dictionary Term dictionary
     * @return The result of the query obtained by doing positional index boolean search in the collection.
     */
    public QueryResult positionalSearch(Query query, TermDictionary dictionary){
        int i, term;
        PositionalPostingList postingResult = null;
        for (i = 0; i < query.size(); i++){
            term = dictionary.getWordIndex(query.getTerm(i).getName());
            if (term != -1){
                if (i == 0){
                    postingResult = positionalIndex.get(term);
                } else {
                    if (postingResult != null){
                        postingResult = postingResult.intersection(positionalIndex.get(term));
                    } else {
                        return new QueryResult();
                    }
                }
            } else {
                return new QueryResult();
            }
        }
        if (postingResult != null) {
            return postingResult.toQueryResult();
        } else {
            return new QueryResult();
        }
    }

    /**
     * Returns the term frequencies  in a given document.
     * @param docId Id of the document
     * @return Term frequencies of the given document.
     */
    public int[] getTermFrequencies(int docId){
        int[] tf;
        int index;
        PositionalPostingList positionalPostingList;
        tf = new int[positionalIndex.size()];
        int i = 0;
        for (Integer key : positionalIndex.keySet()){
            positionalPostingList = positionalIndex.get(key);
            index = positionalPostingList.getIndex(docId);
            if (index != -1){
                tf[i] = positionalPostingList.get(index).size();
            } else {
                tf[i] = 0;
            }
            i++;
        }
        return tf;
    }

    /**
     * Returns the document frequencies of the terms in the collection.
     * @return The document frequencies of the terms in the collection.
     */
    public int[] getDocumentFrequencies(){
        int[] df;
        df = new int[positionalIndex.size()];
        int i = 0;
        for (Integer key : positionalIndex.keySet()){
            df[i] = positionalIndex.get(key).size();
            i++;
        }
        return df;
    }

    /**
     * Calculates and sets the number of terms in each document in the document collection.
     * @param documents Document collection.
     */
    public void setDocumentSizes(ArrayList<Document> documents){
        int[] sizes = new int[documents.size()];
        for (int termId : positionalIndex.keySet()){
            PositionalPostingList positionalPostingList = positionalIndex.get(termId);
            for (int j = 0; j < positionalPostingList.size(); j++) {
                PositionalPosting positionalPosting = positionalPostingList.get(j);
                int docId = positionalPosting.getDocId();
                sizes[docId] += positionalPosting.size();
            }
        }
        for (Document document : documents){
            document.setSize(sizes[document.getDocId()]);
        }
    }

    /**
     * Calculates and updates the frequency counts of the terms in each category node.
     * @param documents Document collection.
     */
    public void setCategoryCounts(ArrayList<Document> documents){
        for (int termId : positionalIndex.keySet()) {
            PositionalPostingList positionalPostingList = positionalIndex.get(termId);
            for (int j = 0; j < positionalPostingList.size(); j++) {
                PositionalPosting positionalPosting = positionalPostingList.get(j);
                int docId = positionalPosting.getDocId();
                CategoryNode categoryNode = documents.get(docId).getCategoryNode();
                if (categoryNode != null){
                    categoryNode.addCounts(termId, positionalPosting.size());
                }
            }
        }
    }

    /**
     * Searches a given query in the document collection using inverted index ranked search.
     * @param query Query string
     * @param dictionary Term dictionary
     * @param documents Document collection
     * @param parameter Search parameter
     * @return The result of the query obtained by doing inverted index ranked search in the collection.
     */
    public QueryResult rankedSearch(Query query,
                                    TermDictionary dictionary,
                                    ArrayList<Document> documents,
                                    SearchParameter parameter){
        int i, j, term, docID, N = documents.size(), tf, df;
        QueryResult result = new QueryResult();
        HashMap<Integer, Double> scores = new HashMap<>();
        PositionalPostingList positionalPostingList;
        for (i = 0; i < query.size(); i++){
            term = dictionary.getWordIndex(query.getTerm(i).getName());
            if (term != -1){
                positionalPostingList = positionalIndex.get(term);
                for (j = 0; j < positionalPostingList.size(); j++){
                    PositionalPosting positionalPosting = positionalPostingList.get(j);
                    docID = positionalPosting.getDocId();
                    tf = positionalPosting.size();
                    df = positionalIndex.get(term).size();
                    if (tf > 0 && df > 0){
                        double score = VectorSpaceModel.weighting(tf, df, N, parameter.getTermWeighting(), parameter.getDocumentWeighting());
                        if (scores.containsKey(docID)){
                            scores.put(docID, scores.get(docID) + score);
                        } else {
                            scores.put(docID, score);
                        }
                    }
                }
            }
        }
        for (int docId : scores.keySet()){
            result.add(docId, scores.get(docId) / documents.get(docId).getSize());
        }
        return result;
    }

}
