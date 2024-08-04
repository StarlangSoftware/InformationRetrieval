package InformationRetrieval.Index;

import Dictionary.WordComparator;
import InformationRetrieval.Query.Query;
import InformationRetrieval.Query.QueryResult;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeMap;

public class InvertedIndex {
    private final TreeMap<Integer, PostingList> index;

    /**
     * Constructs an empty inverted index.
     */
    public InvertedIndex(){
        index = new TreeMap<>();
    }

    /**
     * Constructs an inverted index from a list of sorted tokens. The terms array should be sorted before calling this
     * method. Multiple occurrences of the same term from the same document are merged in the index. Instances of the
     * same term are then grouped, and the result is split into a postings list.
     * @param dictionary Term dictionary
     * @param terms Sorted list of tokens in the memory collection.
     * @param comparator Comparator method to compare two terms.
     */
    public InvertedIndex(TermDictionary dictionary, ArrayList<TermOccurrence> terms, WordComparator comparator){
        this();
        int i, termId, prevDocId;
        TermOccurrence term, previousTerm;
        if (!terms.isEmpty()){
            term = terms.get(0);
            i = 1;
            previousTerm = term;
            termId = dictionary.getWordIndex(term.getTerm().getName());
            add(termId, term.getDocID());
            prevDocId = term.getDocID();
            while (i < terms.size()){
                term = terms.get(i);
                termId = dictionary.getWordIndex(term.getTerm().getName());
                if (termId != -1){
                    if (term.isDifferent(previousTerm, comparator)){
                        add(termId, term.getDocID());
                        prevDocId = term.getDocID();
                    } else {
                        if (prevDocId != term.getDocID()){
                            add(termId, term.getDocID());
                            prevDocId = term.getDocID();
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
     * Reads the postings list of the inverted index from an input file. The postings are stored in two lines. The first
     * line contains the term id and the number of postings for that term. The second line contains the postings
     * list for that term.
     * @param fileName Inverted index file.
     */
    private void readPostingList(String fileName){
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(fileName + "-postings.txt")), StandardCharsets.UTF_8));
            String line = br.readLine();
            while (line != null){
                String[] items = line.split(" ");
                int wordId = Integer.parseInt(items[0]);
                line = br.readLine();
                index.put(wordId, new PostingList(line));
                line = br.readLine();
            }
            br.close();
        } catch (IOException ignored) {
        }
    }

    /**
     * Reads the inverted index from an input file.
     * @param fileName Input file name for the inverted index.
     */
    public InvertedIndex(String fileName){
        index = new TreeMap<>();
        readPostingList(fileName);
    }

    /**
     * Saves the inverted index into the index file. The postings are stored in two lines. The first
     * line contains the term id and the number of postings for that term. The second line contains the postings
     * list for that term.
     * @param fileName Index file name. Real index file name is created by attaching -postings.txt to this
     *                 file name
     */
    public void save(String fileName){
        try {
            PrintWriter printWriter = new PrintWriter(fileName + "-postings.txt", "UTF-8");
            for (Integer key : index.keySet()){
                index.get(key).writeToFile(printWriter, key);
            }
            printWriter.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Adds a possible new term with a document id to the inverted index. First the term is searched in the hash map,
     * then the document id is put into the correct postings list.
     * @param termId Id of the term
     * @param docId Document id in which the term exists
     */
    public void add(int termId, int docId){
        PostingList postingList;
        if (!index.containsKey(termId)){
            postingList = new PostingList();
        } else {
            postingList = index.get(termId);
        }
        postingList.add(docId);
        index.put(termId, postingList);
    }

    /**
     * Constructs a sorted array list of frequency counts for a word list and also sorts the word list according to
     * those frequencies.
     * @param wordList Word list for which frequency array is constructed.
     * @param dictionary Term dictionary
     */
    public void autoCompleteWord(ArrayList<String> wordList, TermDictionary dictionary){
        ArrayList<Integer> counts = new ArrayList<>();
        for (String word : wordList){
            counts.add(index.get(dictionary.getWordIndex(word)).size());
        }
        for (int i = 0; i < wordList.size() - 1; i++){
            for (int j = i + 1; j < wordList.size(); j++){
                if (counts.get(i) < counts.get(j)){
                    Collections.swap(counts, i, j);
                    Collections.swap(wordList, i, j);
                }
            }
        }
    }

    /**
     * Searches a given query in the document collection using inverted index boolean search.
     * @param query Query string
     * @param dictionary Term dictionary
     * @return The result of the query obtained by doing inverted index boolean search in the collection.
     */
    public QueryResult search(Query query, TermDictionary dictionary){
        int i, termIndex;
        PostingList result;
        PostingListComparator comparator = new PostingListComparator();
        ArrayList<PostingList> queryTerms = new ArrayList<>();
        for (i = 0; i < query.size(); i++){
            termIndex = dictionary.getWordIndex(query.getTerm(i).getName());
            if (termIndex != -1){
                queryTerms.add(index.get(termIndex));
            } else {
                return new QueryResult();
            }
        }
        queryTerms.sort(comparator);
        result = queryTerms.get(0);
        for (i = 1; i < queryTerms.size(); i++){
            result = result.intersection(queryTerms.get(i));
        }
        return result.toQueryResult();
    }

}
