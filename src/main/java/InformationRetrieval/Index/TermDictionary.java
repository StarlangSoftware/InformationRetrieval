package InformationRetrieval.Index;

import Dictionary.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

/**
 * A dictionary of terms is kept for easy access.
 */
public class TermDictionary extends Dictionary{

    private final HashMap<Integer, Term> idMap;

    /**
     * Constructor of the TermDictionary. Initializes the comparator for terms and the hasp map.
     * @param comparator Comparator method to compare two terms.
     */
    public TermDictionary(WordComparator comparator){
        super(comparator);
        idMap = new HashMap<>();
    }

    /**
     * Constructor of the TermDictionary. Reads the terms and their ids from the given dictionary file. Each line stores
     * the term id and the term name separated via space.
     * @param comparator Comparator method to compare two terms.
     * @param fileName Dictionary file name
     */
    public TermDictionary(WordComparator comparator, String fileName){
        super(comparator);
        idMap = new HashMap<>();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(fileName + "-dictionary.txt")), StandardCharsets.UTF_8));
            String line = br.readLine();
            while (line != null){
                int termId = Integer.parseInt(line.substring(0, line.indexOf(" ")));
                Term newTerm = new Term(line.substring(line.indexOf(" ") + 1), termId);
                words.add(newTerm);
                idMap.put(termId, newTerm);
                line = br.readLine();
            }
            br.close();
        } catch (IOException ignored) {
        }
    }

    /**
     * Constructs the TermDictionary from a list of tokens (term occurrences). The terms array should be sorted
     * before calling this method. Constructs the distinct terms and their corresponding term ids.
     * @param comparator Comparator method to compare two terms.
     * @param terms Sorted list of tokens in the memory collection.
     */
    public TermDictionary(WordComparator comparator, ArrayList<TermOccurrence> terms){
        super(comparator);
        idMap = new HashMap<>();
        int i, termId = 0;
        TermOccurrence term, previousTerm;
        if (!terms.isEmpty()){
            term = terms.get(0);
            addTerm(term.getTerm().getName(), termId);
            termId++;
            previousTerm = term;
            i = 1;
            while (i < terms.size()){
                term = terms.get(i);
                if (term.isDifferent(previousTerm, comparator)){
                    addTerm(term.getTerm().getName(), termId);
                    termId++;
                }
                i++;
                previousTerm = term;
            }
        }
    }

    /**
     * Constructs the TermDictionary from a hash set of tokens (strings). Constructs sorted dictinct terms array and
     * their corresponding term ids.
     * @param comparator Comparator method to compare two terms.
     * @param words Hash set of tokens in the memory collection.
     */
    public TermDictionary(WordComparator comparator, HashSet<String> words){
        super(comparator);
        idMap = new HashMap<>();
        ArrayList<Word> wordList = new ArrayList<>();
        for (String word : words){
            wordList.add(new Word(word));
        }
        wordList.sort(comparator);
        int termID = 0;
        for (Word term : wordList){
            addTerm(term.getName(), termID);
            termID++;
        }
    }

    /**
     * Adds a new term to the sorted words array. First the term is searched in the words array using binary search,
     * then the word is added into the correct place.
     * @param name Lemma of the term
     * @param termId Id of the term
     */
    public void addTerm(String name, int termId){
        int middle = Collections.binarySearch(words, new Word(name), comparator);
        if (middle < 0){
            Term newTerm = new Term(name, termId);
            words.add(-middle - 1, newTerm);
            idMap.put(termId, newTerm);
        } else {
            System.out.println(name);
        }
    }

    /**
     * Saves the term dictionary into the dictionary file. Each line stores the term id and the term name separated via
     * space.
     * @param fileName Dictionary file name. Real dictionary file name is created by attaching -dictionary.txt to this
     *                 file name
     */
    public void save(String fileName){
        try {
            PrintWriter printWriter = new PrintWriter(fileName + "-dictionary.txt", "UTF-8");
            for (Word word : words) {
                Term term = (Term) word;
                printWriter.write(term.getTermId() + " " + term.getName() + "\n");
            }
            printWriter.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the term with the given term id. Terms are sorted in the words array according to their lemma. In order
     * to get the terms asap w.r.t. their ids, we have a hash map to map the term ids to themselves.
     * @param termId Term id to search and get.
     * @return Term with the given term id.
     */
    public Term getTerm(int termId){
        return idMap.get(termId);
    }

    /**
     * Constructs all NGrams from a given word. For example, 3 grams for word "term" are "$te", "ter", "erm", "rm$".
     * @param word Word for which NGrams will b created.
     * @param termId Term id to add into the posting list.
     * @param N N in NGram.
     * @return An array of NGrams for a given word.
     */
    public static ArrayList<TermOccurrence> constructNGrams(String word, int termId, int N){
        ArrayList<TermOccurrence> nGrams = new ArrayList<>();
        if (word.length() >= N - 1){
            for (int l = -1; l < word.length() - N + 2; l++){
                String term;
                if (l == -1){
                    term = "$" + word.substring(0, N - 1);
                } else {
                    if (l == word.length() - N + 1){
                        term = word.substring(l, l + N - 1) + "$";
                    } else {
                        term = word.substring(l, l + N);
                    }
                }
                nGrams.add(new TermOccurrence(new Word(term), termId, l));
            }
        }
        return nGrams;
    }

    /**
     * Constructs all NGrams for all words in the dictionary. For example, 3 grams for word "term" are "$te", "ter",
     * "erm", "rm$".
     * @param N N in NGram.
     * @return A sorted array of NGrams for all words in the dictionary.
     */
    public ArrayList<TermOccurrence> constructNGramTermsFromDictionary(int N){
        TermOccurrenceComparator termComparator = new TermOccurrenceComparator(comparator);
        ArrayList<TermOccurrence> terms = new ArrayList<>();
        for (int i = 0; i < size(); i++){
            String word = getWord(i).getName();
            terms.addAll(TermDictionary.constructNGrams(word, i, N));
        }
        terms.sort(termComparator);
        return terms;
    }
}
