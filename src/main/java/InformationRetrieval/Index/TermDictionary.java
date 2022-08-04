package InformationRetrieval.Index;

import Dictionary.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class TermDictionary extends Dictionary{

    public TermDictionary(WordComparator comparator){
        super(comparator);
    }

    public TermDictionary(WordComparator comparator, String fileName){
        super(comparator);
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(fileName + "-dictionary.txt")), StandardCharsets.UTF_8));
            String line = br.readLine();
            while (line != null){
                int termId = Integer.parseInt(line.substring(0, line.indexOf(" ")));
                words.add(new Term(line.substring(line.indexOf(" ") + 1), termId));
                line = br.readLine();
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public TermDictionary(WordComparator comparator, ArrayList<TermOccurrence> terms){
        super(comparator);
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
            words.add(-middle - 1, new Term(name, termId));
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

    public ArrayList<TermOccurrence> constructTermsFromDictionary(int k){
        TermOccurrenceComparator termComparator = new TermOccurrenceComparator(comparator);
        ArrayList<TermOccurrence> terms = new ArrayList<>();
        for (int i = 0; i < size(); i++){
            String word = getWord(i).getName();
            terms.addAll(NGramIndex.constructNGrams(word, i, k));
        }
        terms.sort(termComparator);
        return terms;
    }
}
