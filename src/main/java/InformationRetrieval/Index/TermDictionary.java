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

public class TermDictionary extends Dictionary{

    private HashMap<Integer, Term> idMap;

    public TermDictionary(WordComparator comparator){
        super(comparator);
        idMap = new HashMap<>();
    }

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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public TermDictionary(WordComparator comparator, ArrayList<TermOccurrence> terms){
        super(comparator);
        idMap = new HashMap<>();
        int i, termId = 0;
        TermOccurrence term, previousTerm;
        if (terms.size() > 0){
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
    public TermDictionary(WordComparator comparator, HashSet<String> words){
        super((comparator));
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

    public Term getTerm(int termId){
        return idMap.get(termId);
    }

    public static ArrayList<TermOccurrence> constructNGrams(String word, int termId, int k){
        ArrayList<TermOccurrence> nGrams = new ArrayList<>();
        if (word.length() >= k - 1){
            for (int l = -1; l < word.length() - k + 2; l++){
                String term;
                if (l == -1){
                    term = "$" + word.substring(0, k - 1);
                } else {
                    if (l == word.length() - k + 1){
                        term = word.substring(l, l + k - 1) + "$";
                    } else {
                        term = word.substring(l, l + k);
                    }
                }
                nGrams.add(new TermOccurrence(new Word(term), termId, l));
            }
        }
        return nGrams;
    }
    public ArrayList<TermOccurrence> constructTermsFromDictionary(int k){
        TermOccurrenceComparator termComparator = new TermOccurrenceComparator(comparator);
        ArrayList<TermOccurrence> terms = new ArrayList<>();
        for (int i = 0; i < size(); i++){
            String word = getWord(i).getName();
            terms.addAll(TermDictionary.constructNGrams(word, i, k));
        }
        terms.sort(termComparator);
        return terms;
    }
}
