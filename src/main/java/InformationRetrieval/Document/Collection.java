package InformationRetrieval.Document;

import Dictionary.*;
import InformationRetrieval.Index.*;
import InformationRetrieval.Query.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Collection {
    private final IndexType indexType;
    private TermDictionary dictionary;
    private TermDictionary phraseDictionary;
    private TermDictionary biGramDictionary;
    private TermDictionary triGramDictionary;
    private final ArrayList<Document> documents;
    private IncidenceMatrix incidenceMatrix;
    private InvertedIndex invertedIndex;
    private NGramIndex biGramIndex;
    private NGramIndex triGramIndex;
    private PositionalIndex positionalIndex;
    private InvertedIndex phraseIndex;
    private PositionalIndex phrasePositionalIndex;
    private final WordComparator comparator;
    private final String name;
    private final Parameter parameter;

    public Collection(String directory,
                      Parameter parameter){
        this.name = directory;
        this.indexType = parameter.getIndexType();
        this.comparator = parameter.getWordComparator();
        this.parameter = parameter;
        documents = new ArrayList<>();
        File folder = new File(directory);
        File[] listOfFiles = folder.listFiles();
        if (listOfFiles != null) {
            Arrays.sort(listOfFiles);
            int fileLimit = listOfFiles.length;
            if (parameter.limitNumberOfDocumentsLoaded()){
                fileLimit = parameter.getDocumentLimit();
            }
            int i = 0;
            int j = 0;
            while (i < listOfFiles.length && j < fileLimit) {
                File file = listOfFiles[i];
                if (file.isFile() && file.getName().endsWith(".txt")) {
                    Document document = new Document(file.getAbsolutePath(), file.getName(), j);
                    documents.add(document);
                    j++;
                }
                i++;
            }
        }
        if (parameter.loadIndexesFromFile()){
            dictionary = new TermDictionary(comparator, directory);
            invertedIndex = new InvertedIndex(directory);
            if (parameter.constructPositionalIndex()){
                positionalIndex = new PositionalIndex(directory);
                positionalIndex.setDocumentSizes(documents);
            }
            if (parameter.constructPhraseIndex()){
                phraseDictionary = new TermDictionary(comparator, directory + "-phrase");
                phraseIndex = new InvertedIndex(directory + "-phrase");
                if (parameter.constructPositionalIndex()){
                    phrasePositionalIndex = new PositionalIndex(directory + "-phrase");
                }
            }
            if (parameter.constructNGramIndex()){
                biGramDictionary = new TermDictionary(comparator, directory + "-biGram");
                triGramDictionary = new TermDictionary(comparator, directory + "-triGram");
                biGramIndex = new NGramIndex(directory + "-biGram");
                triGramIndex = new NGramIndex(directory + "-triGram");
            }
        } else {
            if (parameter.constructDictionaryInDisk()){
                constructDictionaryInDisk();
            } else {
                if (parameter.constructIndexInDisk()){
                    constructIndexesInDisk();
                } else {
                    constructIndexesInMemory();
                }
            }
        }
    }

    public int size(){
        return documents.size();
    }

    public int vocabularySize(){
        return dictionary.size();
    }

    public void save(){
        if (indexType == IndexType.INVERTED_INDEX){
            dictionary.save(name);
            invertedIndex.save(name);
            if (parameter.constructPositionalIndex()){
                positionalIndex.save(name);
            }
            if (parameter.constructPhraseIndex()){
                phraseDictionary.save(name + "-phrase");
                phraseIndex.save(name + "-phrase");
                if (parameter.constructPositionalIndex()){
                    phrasePositionalIndex.save(name + "-phrase");
                }
            }
            if (parameter.constructNGramIndex()){
                biGramDictionary.save(name + "-biGram");
                triGramDictionary.save(name + "-triGram");
                biGramIndex.save(name + "-biGram");
                triGramIndex.save(name + "-triGram");
            }
        }
    }

    private void constructDictionaryInDisk(){
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

    private void constructIndexesInDisk(){
        HashSet<String> wordList = constructDistinctWordList(TermType.TOKEN);
        dictionary = new TermDictionary(comparator, wordList);
        constructInvertedIndexInDisk(dictionary, TermType.TOKEN);
        if (parameter.constructPositionalIndex()){
            constructPositionalIndexInDisk(dictionary, TermType.TOKEN);
        }
        if (parameter.constructPhraseIndex()){
            wordList = constructDistinctWordList(TermType.PHRASE);
            phraseDictionary = new TermDictionary(comparator, wordList);
            constructInvertedIndexInDisk(phraseDictionary, TermType.PHRASE);
            if (parameter.constructPositionalIndex()){
                constructPositionalIndexInDisk(phraseDictionary, TermType.PHRASE);
            }
        }
        if (parameter.constructNGramIndex()){
            constructNGramIndex();
        }
    }

    private void constructIndexesInMemory(){
        ArrayList<TermOccurrence> terms = constructTerms(TermType.TOKEN);
        dictionary = new TermDictionary(comparator, terms);
        switch (indexType){
            case INCIDENCE_MATRIX:
                incidenceMatrix = new IncidenceMatrix(terms, dictionary, documents.size());
                break;
            case INVERTED_INDEX:
                invertedIndex = new InvertedIndex(dictionary, terms, comparator);
                if (parameter.constructPositionalIndex()){
                    positionalIndex = new PositionalIndex(dictionary, terms, comparator);
                }
                if (parameter.constructPhraseIndex()){
                    terms = constructTerms(TermType.PHRASE);
                    phraseDictionary = new TermDictionary(comparator, terms);
                    phraseIndex = new InvertedIndex(phraseDictionary, terms, comparator);
                    if (parameter.constructPositionalIndex()){
                        phrasePositionalIndex = new PositionalIndex(phraseDictionary, terms, comparator);
                    }
                }
                if (parameter.constructNGramIndex()){
                    constructNGramIndex();
                }
                break;
        }
    }
    private ArrayList<TermOccurrence> constructTerms(TermType termType){
        TermOccurrenceComparator termComparator = new TermOccurrenceComparator(comparator);
        ArrayList<TermOccurrence> terms = new ArrayList<>();
        ArrayList<TermOccurrence> docTerms;
        for (Document doc : documents){
            DocumentText documentText = doc.loadDocument();
            docTerms = documentText.constructTermList(doc.getDocId(), termType);
            terms.addAll(docTerms);
        }
        terms.sort(termComparator);
        return terms;
    }

    private HashSet<String> constructDistinctWordList(TermType termType){
        HashSet<String> words = new HashSet<>();
        for (Document doc : documents){
            DocumentText documentText = doc.loadDocument();
            words.addAll(documentText.constructDistinctWordList(termType));
        }
        return words;
    }

    private boolean notCombinedAllIndexes(int[] currentIdList){
        for (int id : currentIdList){
            if (id != -1){
                return true;
            }
        }
        return false;
    }

    private boolean notCombinedAllDictionaries(String[] currentWords){
        for (String word : currentWords){
            if (word != null){
                return true;
            }
        }
        return false;
    }

    private ArrayList<Integer> selectIndexesWithMinimumTermIds(int[] currentIdList){
        ArrayList<Integer> result = new ArrayList<>();
        int min = Integer.MAX_VALUE;
        for (int id : currentIdList){
            if (id != -1 && id < min){
                min = id;
            }
        }
        for (int i = 0; i < currentIdList.length; i++){
            if (currentIdList[i] == min){
                result.add(i);
            }
        }
        return result;
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

    private void combineMultipleInvertedIndexesInDisk(String name, String tmpName, int blockCount){
        BufferedReader[] files;
        int[] currentIdList;
        PostingList[] currentPostingLists;
        currentIdList = new int[blockCount];
        currentPostingLists = new PostingList[blockCount];
        files = new BufferedReader[blockCount];
        try{
            PrintWriter printWriter = new PrintWriter(name + "-postings.txt", "UTF-8");
            for (int i = 0; i < blockCount; i++){
                files[i] = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get("tmp-" + tmpName + i + "-postings.txt")), StandardCharsets.UTF_8));
                String line = files[i].readLine();
                String[] items = line.split(" ");
                currentIdList[i] = Integer.parseInt(items[0]);
                line = files[i].readLine();
                currentPostingLists[i] = new PostingList(line);
            }
            while (notCombinedAllIndexes(currentIdList)){
                ArrayList<Integer> indexesToCombine = selectIndexesWithMinimumTermIds(currentIdList);
                PostingList mergedPostingList = currentPostingLists[indexesToCombine.get(0)];
                for (int i = 1; i < indexesToCombine.size(); i++){
                    mergedPostingList = mergedPostingList.union(currentPostingLists[indexesToCombine.get(i)]);
                }
                mergedPostingList.writeToFile(printWriter, currentIdList[indexesToCombine.get(0)]);
                for (int i : indexesToCombine) {
                    String line = files[i].readLine();
                    if (line != null) {
                        String[] items = line.split(" ");
                        currentIdList[i] = Integer.parseInt(items[0]);
                        line = files[i].readLine();
                        currentPostingLists[i] = new PostingList(line);
                    } else {
                        currentIdList[i] = -1;
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

    private void constructInvertedIndexInDisk(TermDictionary dictionary, TermType termType){
        int i = 0, blockCount = 0;
        InvertedIndex invertedIndex = new InvertedIndex();
        for (Document doc : documents){
            if (i < parameter.getDocumentLimit()){
                i++;
            } else {
                invertedIndex.save("tmp-" + blockCount);
                invertedIndex = new InvertedIndex();
                blockCount++;
                i = 0;
            }
            DocumentText documentText = doc.loadDocument();
            HashSet<String> wordList = documentText.constructDistinctWordList(termType);
            for (String word : wordList){
                int termId = dictionary.getWordIndex(word);
                invertedIndex.add(termId, doc.getDocId());
            }
        }
        if (documents.size() != 0){
            invertedIndex.save("tmp-" + blockCount);
            blockCount++;
        }
        if (termType == TermType.TOKEN){
            combineMultipleInvertedIndexesInDisk(name, "", blockCount);
        } else {
            combineMultipleInvertedIndexesInDisk(name + "-phrase", "", blockCount);
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

    private void combineMultiplePositionalIndexesInDisk(String name, int blockCount){
        BufferedReader[] files;
        int[] currentIdList;
        PositionalPostingList[] currentPostingLists;
        currentIdList = new int[blockCount];
        currentPostingLists = new PositionalPostingList[blockCount];
        files = new BufferedReader[blockCount];
        try{
            PrintWriter printWriter = new PrintWriter(name + "-positionalPostings.txt", "UTF-8");
            for (int i = 0; i < blockCount; i++){
                files[i] = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get("tmp-" + i + "-positionalPostings.txt")), StandardCharsets.UTF_8));
                String line = files[i].readLine();
                String[] items = line.split(" ");
                currentIdList[i] = Integer.parseInt(items[0]);
                currentPostingLists[i] = new PositionalPostingList(files[i], Integer.parseInt(items[1]));
            }
            while (notCombinedAllIndexes(currentIdList)){
                ArrayList<Integer> indexesToCombine = selectIndexesWithMinimumTermIds(currentIdList);
                PositionalPostingList mergedPostingList = currentPostingLists[indexesToCombine.get(0)];
                for (int i = 1; i < indexesToCombine.size(); i++){
                    mergedPostingList = mergedPostingList.union(currentPostingLists[indexesToCombine.get(i)]);
                }
                mergedPostingList.writeToFile(printWriter, currentIdList[indexesToCombine.get(0)]);
                for (int i : indexesToCombine) {
                    String line = files[i].readLine();
                    if (line != null) {
                        String[] items = line.split(" ");
                        currentIdList[i] = Integer.parseInt(items[0]);
                        currentPostingLists[i] = new PositionalPostingList(files[i], Integer.parseInt(items[1]));
                    } else {
                        currentIdList[i] = -1;
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

    private void constructPositionalIndexInDisk(TermDictionary dictionary, TermType termType){
        int i = 0, blockCount = 0;
        PositionalIndex positionalIndex = new PositionalIndex();
        for (Document doc : documents){
            if (i < parameter.getDocumentLimit()){
                i++;
            } else {
                positionalIndex.save("tmp-" + blockCount);
                positionalIndex = new PositionalIndex();
                blockCount++;
                i = 0;
            }
            DocumentText documentText = doc.loadDocument();
            ArrayList<TermOccurrence> terms = documentText.constructTermList(doc.getDocId(), termType);
            for (TermOccurrence termOccurrence : terms){
                int termId = dictionary.getWordIndex(termOccurrence.getTerm().getName());
                positionalIndex.addPosition(termId, termOccurrence.getDocID(), termOccurrence.getPosition());
            }
        }
        if (documents.size() != 0){
            positionalIndex.save("tmp-" + blockCount);
            blockCount++;
        }
        if (termType == TermType.TOKEN){
            combineMultiplePositionalIndexesInDisk(name, blockCount);
        } else {
            combineMultiplePositionalIndexesInDisk(name + "-phrase", blockCount);
        }
    }

    private void constructNGramIndex(){
        ArrayList<TermOccurrence> terms = dictionary.constructTermsFromDictionary(2);
        biGramDictionary = new TermDictionary(comparator, terms);
        biGramIndex = new NGramIndex(biGramDictionary, terms, comparator);
        terms = dictionary.constructTermsFromDictionary(3);
        triGramDictionary = new TermDictionary(comparator, terms);
        triGramIndex = new NGramIndex(triGramDictionary, terms, comparator);
    }

    public QueryResult searchCollection(Query query, RetrievalType retrievalType, TermWeighting termWeighting, DocumentWeighting documentWeighting){
        switch (indexType){
            case INCIDENCE_MATRIX:
                return incidenceMatrix.search(query, dictionary);
            case   INVERTED_INDEX:
                switch (retrievalType){
                    case    BOOLEAN:return invertedIndex.search(query, dictionary);
                    case POSITIONAL:return positionalIndex.positionalSearch(query, dictionary);
                    case     RANKED:return positionalIndex.rankedSearch(query, dictionary, documents, termWeighting, documentWeighting);
                }
        }
        return new QueryResult();
    }

}
