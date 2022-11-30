package InformationRetrieval.Document;

import Dictionary.Word;
import InformationRetrieval.Index.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;

public class LargeCollection extends DiskCollection{

    public LargeCollection(String directory, Parameter parameter) {
        super(directory, parameter);
        constructDictionaryAndIndexesInDisk();
    }

    private void constructDictionaryAndIndexesInDisk(){
        constructDictionaryAndInvertedIndexInDisk(TermType.TOKEN);
        if (parameter.constructPositionalIndex()){
            constructDictionaryAndPositionalIndexInDisk(TermType.TOKEN);
        }
        if (parameter.constructPhraseIndex()){
            constructDictionaryAndInvertedIndexInDisk(TermType.PHRASE);
            if (parameter.constructPositionalIndex()){
                constructDictionaryAndPositionalIndexInDisk(TermType.PHRASE);
            }
        }
        if (parameter.constructNGramIndex()){
            constructNGramDictionaryAndIndexInDisk();
        }
    }

    private boolean notCombinedAllDictionaries(String[] currentWords){
        for (String word : currentWords){
            if (word != null){
                return true;
            }
        }
        return false;
    }

    private ArrayList<Integer> selectDictionariesWithMinimumWords(String[] currentWords){
        ArrayList<Integer> result = new ArrayList<>();
        String min = null;
        for (String word: currentWords){
            if (word != null && (min == null || comparator.compare(new Word(word), new Word(min)) < 0)){
                min = word;
            }
        }
        for (int i = 0; i < currentWords.length; i++){
            if (currentWords[i] != null && currentWords[i].equals(min)){
                result.add(i);
            }
        }
        return result;
    }

    private void combineMultipleDictionariesInDisk(String name, String tmpName, int blockCount){
        BufferedReader[] files;
        int[] currentIdList;
        String[] currentWords;
        currentIdList = new int[blockCount];
        currentWords = new String[blockCount];
        files = new BufferedReader[blockCount];
        try{
            PrintWriter printWriter = new PrintWriter(name + "-dictionary.txt", "UTF-8");
            for (int i = 0; i < blockCount; i++){
                files[i] = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get("tmp-" + tmpName + i + "-dictionary.txt")), StandardCharsets.UTF_8));
                String line = files[i].readLine();
                currentIdList[i] = Integer.parseInt(line.substring(0, line.indexOf(" ")));
                currentWords[i] = line.substring(line.indexOf(" ") + 1);
            }
            while (notCombinedAllDictionaries(currentWords)){
                ArrayList<Integer> indexesToCombine = selectDictionariesWithMinimumWords(currentWords);
                printWriter.write(currentIdList[indexesToCombine.get(0)] + " " + currentWords[indexesToCombine.get(0)] + "\n");
                for (int i : indexesToCombine) {
                    String line = files[i].readLine();
                    if (line != null) {
                        currentIdList[i] = Integer.parseInt(line.substring(0, line.indexOf(" ")));
                        currentWords[i] = line.substring(line.indexOf(" ") + 1);
                    } else {
                        currentWords[i] = null;
                    }
                }
            }
            for (int i = 0; i < blockCount; i++){
                files[i].close();
            }
            printWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void constructDictionaryAndInvertedIndexInDisk(TermType termType){
        int i = 0, blockCount = 0;
        InvertedIndex invertedIndex = new InvertedIndex();
        TermDictionary dictionary = new TermDictionary(comparator);
        for (Document doc : documents){
            if (i < parameter.getDocumentLimit()){
                i++;
            } else {
                dictionary.save("tmp-" + blockCount);
                dictionary = new TermDictionary(comparator);
                invertedIndex.save("tmp-" + blockCount);
                invertedIndex = new InvertedIndex();
                blockCount++;
                i = 0;
            }
            DocumentText documentText = doc.loadDocument();
            HashSet<String> wordList = documentText.constructDistinctWordList(termType);
            for (String word : wordList){
                int termId;
                int wordIndex = dictionary.getWordIndex(word);
                if (wordIndex != -1){
                    termId = ((Term) dictionary.getWord(wordIndex)).getTermId();
                } else {
                    termId = Math.abs(word.hashCode());
                    dictionary.addTerm(word, termId);
                }
                invertedIndex.add(termId, doc.getDocId());
            }
        }
        if (documents.size() != 0){
            dictionary.save("tmp-" + blockCount);
            invertedIndex.save("tmp-" + blockCount);
            blockCount++;
        }
        if (termType == TermType.TOKEN){
            combineMultipleDictionariesInDisk(name, "", blockCount);
            combineMultipleInvertedIndexesInDisk(name, "", blockCount);
        } else {
            combineMultipleDictionariesInDisk(name + "-phrase", "", blockCount);
            combineMultipleInvertedIndexesInDisk(name + "-phrase", "", blockCount);
        }
    }

    private void constructDictionaryAndPositionalIndexInDisk(TermType termType){
        int i = 0, blockCount = 0;
        PositionalIndex positionalIndex = new PositionalIndex();
        TermDictionary dictionary = new TermDictionary(comparator);
        for (Document doc : documents){
            if (i < parameter.getDocumentLimit()){
                i++;
            } else {
                dictionary.save("tmp-" + blockCount);
                dictionary = new TermDictionary(comparator);
                positionalIndex.save("tmp-" + blockCount);
                positionalIndex = new PositionalIndex();
                blockCount++;
                i = 0;
            }
            DocumentText documentText = doc.loadDocument();
            ArrayList<TermOccurrence> terms = documentText.constructTermList(doc.getDocId(), termType);
            for (TermOccurrence termOccurrence : terms){
                int termId;
                int wordIndex = dictionary.getWordIndex(termOccurrence.getTerm().getName());
                if (wordIndex != -1){
                    termId = ((Term) dictionary.getWord(wordIndex)).getTermId();
                } else {
                    termId = Math.abs(termOccurrence.getTerm().getName().hashCode());
                    dictionary.addTerm(termOccurrence.getTerm().getName(), termId);
                }
                positionalIndex.addPosition(termId, termOccurrence.getDocID(), termOccurrence.getPosition());
            }
        }
        if (documents.size() != 0){
            dictionary.save("tmp-" + blockCount);
            positionalIndex.save("tmp-" + blockCount);
            blockCount++;
        }
        if (termType == TermType.TOKEN){
            combineMultipleDictionariesInDisk(name, "", blockCount);
            combineMultiplePositionalIndexesInDisk(name, blockCount);
        } else {
            combineMultipleDictionariesInDisk(name + "-phrase", "", blockCount);
            combineMultiplePositionalIndexesInDisk(name + "-phrase", blockCount);
        }
    }

    private void addNGramsToDictionaryAndIndex(String line, int k, TermDictionary nGramDictionary, NGramIndex nGramIndex){
        int wordId = Integer.parseInt(line.substring(0, line.indexOf(" ")));
        String word = line.substring(line.indexOf(" ") + 1);
        ArrayList<TermOccurrence> biGrams = TermDictionary.constructNGrams(word, wordId, k);
        for (TermOccurrence term : biGrams){
            int termId;
            int wordIndex = nGramDictionary.getWordIndex(term.getTerm().getName());
            if (wordIndex != -1){
                termId = ((Term) nGramDictionary.getWord(wordIndex)).getTermId();
            } else {
                termId = Math.abs(term.getTerm().getName().hashCode());
                nGramDictionary.addTerm(term.getTerm().getName(), termId);
            }
            nGramIndex.add(termId, wordId);
        }
    }

    private void constructNGramDictionaryAndIndexInDisk(){
        int i = 0, blockCount = 0;
        TermDictionary biGramDictionary = new TermDictionary(comparator);
        TermDictionary triGramDictionary = new TermDictionary(comparator);
        NGramIndex biGramIndex = new NGramIndex();
        NGramIndex triGramIndex = new NGramIndex();
        BufferedReader br;
        try {
            br = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(name + "-dictionary.txt")), StandardCharsets.UTF_8));
            String line = br.readLine();
            while (line != null) {
                if (i < parameter.getWordLimit()){
                    i++;
                } else {
                    biGramDictionary.save("tmp-biGram-" + blockCount);
                    triGramDictionary.save("tmp-triGram-" + blockCount);
                    biGramDictionary = new TermDictionary(comparator);
                    triGramDictionary = new TermDictionary(comparator);
                    biGramIndex.save("tmp-biGram-" + blockCount);
                    biGramIndex = new NGramIndex();
                    triGramIndex.save("tmp-triGram-" + blockCount);
                    triGramIndex = new NGramIndex();
                    blockCount++;
                    i = 0;
                }
                addNGramsToDictionaryAndIndex(line, 2, biGramDictionary, biGramIndex);
                addNGramsToDictionaryAndIndex(line, 3, triGramDictionary, triGramIndex);
                line = br.readLine();
            }
            br.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (documents.size() != 0){
            biGramDictionary.save("tmp-biGram-" + blockCount);
            triGramDictionary.save("tmp-triGram-" + blockCount);
            biGramIndex.save("tmp-biGram-" + blockCount);
            triGramIndex.save("tmp-triGram-" + blockCount);
            blockCount++;
        }
        combineMultipleDictionariesInDisk(name + "-biGram", "biGram-", blockCount);
        combineMultipleDictionariesInDisk(name + "-triGram", "triGram-", blockCount);
        combineMultipleInvertedIndexesInDisk(name + "-biGram", "biGram-", blockCount);
        combineMultipleInvertedIndexesInDisk(name + "-triGram", "triGram-", blockCount);
    }

}
