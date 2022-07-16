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
                words.add(new Word(line.substring(line.indexOf(" ") + 1)));
                line = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public TermDictionary(WordComparator comparator, ArrayList<TermOccurrence> terms){
        super(comparator);
        int i;
        TermOccurrence term, previousTerm;
        if (terms.size() > 0){
            term = terms.get(0);
            addTerm(term.getTerm());
            previousTerm = term;
            i = 1;
            while (i < terms.size()){
                term = terms.get(i);
                if (term.isDifferent(previousTerm)){
                    addTerm(term.getTerm());
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
        for (Word term : wordList){
            addTerm(term);
        }
    }

    public void addTerm(Word term){
        int middle = Collections.binarySearch(words, new Word(term.getName()), comparator);
        if (middle < 0){
            words.add(-middle - 1, term);
        }
    }

    public void save(String fileName){
        try {
            PrintWriter printWriter = new PrintWriter(fileName + "-dictionary.txt", "UTF-8");
            int i = 0;
            for (Word word : words) {
                printWriter.write(i + " " + word + "\n");
                i++;
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
                    terms.add(new TermOccurrence(new Word(term), i, l));
                }
            }
        }
        terms.sort(termComparator);
        return terms;
    }
}
