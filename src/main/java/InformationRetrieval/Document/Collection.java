package InformationRetrieval.Document;

import Dictionary.WordComparator;
import InformationRetrieval.Index.*;
import InformationRetrieval.Query.*;
import Math.Matrix;

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
            invertedIndex = new InvertedIndex(directory, vocabularySize());
            if (parameter.constructPositionalIndex()){
                positionalIndex = new PositionalIndex(directory, vocabularySize());
            }
            if (parameter.constructPhraseIndex()){
                phraseDictionary = new TermDictionary(comparator, directory + "-phrase");
                phraseIndex = new InvertedIndex(directory + "-phrase", phraseSize());
                if (parameter.constructPositionalIndex()){
                    phrasePositionalIndex = new PositionalIndex(directory + "-phrase", phraseSize());
                }
            }
            if (parameter.constructKGramIndex()){
                biGramDictionary = new TermDictionary(comparator, directory + "-biGram");
                triGramDictionary = new TermDictionary(comparator, directory + "-triGram");
                biGramIndex = new NGramIndex(directory + "-biGram", biGramSize());
                triGramIndex = new NGramIndex(directory + "-triGram", triGramSize());
            }
        } else {
            if (parameter.constructIndexInMemory()){
                constructIndexesInMemory();
            } else {
                constructIndexesInDisk();
            }
        }
    }

    public int size(){
        return documents.size();
    }

    public int vocabularySize(){
        return dictionary.size();
    }

    public int biGramSize(){
        return biGramDictionary.size();
    }

    public int triGramSize(){
        return triGramDictionary.size();
    }

    public int phraseSize(){
        return phraseDictionary.size();
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
            if (parameter.constructKGramIndex()){
                biGramDictionary.save(name + "-biGram");
                triGramDictionary.save(name + "-triGram");
                biGramIndex.save(name + "-biGram");
                triGramIndex.save(name + "-triGram");
            }
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
        if (parameter.constructKGramIndex()){
            constructKGramIndex();
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
                invertedIndex = new InvertedIndex(dictionary, terms, vocabularySize(), comparator);
                if (parameter.constructPositionalIndex()){
                    positionalIndex = new PositionalIndex(dictionary, terms, vocabularySize(), comparator);
                }
                if (parameter.constructPhraseIndex()){
                    terms = constructTerms(TermType.PHRASE);
                    phraseDictionary = new TermDictionary(comparator, terms);
                    phraseIndex = new InvertedIndex(phraseDictionary, terms, phraseSize(), comparator);
                    if (parameter.constructPositionalIndex()){
                        phrasePositionalIndex = new PositionalIndex(phraseDictionary, terms, phraseSize(), comparator);
                    }
                }
                if (parameter.constructKGramIndex()){
                    constructKGramIndex();
                }
                break;
        }
    }
    private ArrayList<TermOccurrence> constructTerms(TermType termType){
        TermOccurrenceComparator termComparator = new TermOccurrenceComparator(comparator);
        ArrayList<TermOccurrence> terms = new ArrayList<>();
        ArrayList<TermOccurrence> docTerms;
        for (Document doc : documents){
            DocumentText documentText = doc.loadDocument(parameter.tokenizeDocument());
            docTerms = documentText.constructTermList(doc, termType);
            terms.addAll(docTerms);
        }
        terms.sort(termComparator);
        return terms;
    }

    private HashSet<String> constructDistinctWordList(TermType termType){
        HashSet<String> words = new HashSet<>();
        for (Document doc : documents){
            DocumentText documentText = doc.loadDocument(parameter.tokenizeDocument());
            words.addAll(documentText.constructDistinctWordList(termType));
        }
        return words;
    }

    private boolean notFinishedCombination(int[] currentIdList){
        for (int id : currentIdList){
            if (id != -1){
                return true;
            }
        }
        return false;
    }

    private ArrayList<Integer> selectIdFromCombination(int[] currentIdList){
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

    private void combineMultipleInvertedIndexesInDisk(String name, int blockCount){
        BufferedReader[] files;
        int[] currentIdList;
        PostingList[] currentPostingLists;
        currentIdList = new int[blockCount];
        currentPostingLists = new PostingList[blockCount];
        files = new BufferedReader[blockCount];
        try{
            PrintWriter printWriter = new PrintWriter(name + "-postings.txt", "UTF-8");
            for (int i = 0; i < blockCount; i++){
                files[i] = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get("tmp-" + i + "-postings.txt")), StandardCharsets.UTF_8));
                String line = files[i].readLine();
                String[] items = line.split(" ");
                currentIdList[i] = Integer.parseInt(items[0]);
                line = files[i].readLine();
                currentPostingLists[i] = new PostingList(line);
            }
            while (notFinishedCombination(currentIdList)){
                ArrayList<Integer> indexesToCombine = selectIdFromCombination(currentIdList);
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
        InvertedIndex invertedIndex = new InvertedIndex(dictionary.size());
        for (Document doc : documents){
            if (i < parameter.getBlockSize()){
                i++;
            } else {
                invertedIndex.save("tmp-" + blockCount);
                invertedIndex = new InvertedIndex(dictionary.size());
                blockCount++;
                i = 0;
            }
            DocumentText documentText = doc.loadDocument(parameter.tokenizeDocument());
            HashSet<String> wordList = documentText.constructDistinctWordList(termType);
            for (String word : wordList){
                int termId = dictionary.getWordIndex(word);
                invertedIndex.add(termId, doc.getDocId());
            }
        }
        if (i != 0){
            invertedIndex.save("tmp-" + blockCount);
            blockCount++;
        }
        if (termType == TermType.TOKEN){
            combineMultipleInvertedIndexesInDisk(name, blockCount);
        } else {
            combineMultipleInvertedIndexesInDisk(name + "-phrase", blockCount);
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
            while (notFinishedCombination(currentIdList)){
                ArrayList<Integer> indexesToCombine = selectIdFromCombination(currentIdList);
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

    private void constructPositionalIndexInDisk(TermDictionary dictionary, TermType termType){
        int i = 0, blockCount = 0;
        PositionalIndex positionalIndex = new PositionalIndex(dictionary.size());
        for (Document doc : documents){
            if (i < parameter.getBlockSize()){
                i++;
            } else {
                positionalIndex.save("tmp-" + blockCount);
                positionalIndex = new PositionalIndex(dictionary.size());
                blockCount++;
                i = 0;
            }
            DocumentText documentText = doc.loadDocument(parameter.tokenizeDocument());
            ArrayList<TermOccurrence> terms = documentText.constructTermList(doc, termType);
            for (TermOccurrence termOccurrence : terms){
                int termId = dictionary.getWordIndex(termOccurrence.getTerm().getName());
                positionalIndex.addPosition(termId, termOccurrence.getDocID(), termOccurrence.getPosition());
            }
        }
        if (i != 0){
            positionalIndex.save("tmp-" + blockCount);
            blockCount++;
        }
        if (termType == TermType.TOKEN){
            combineMultiplePositionalIndexesInDisk(name, blockCount);
        } else {
            combineMultiplePositionalIndexesInDisk(name + "-phrase", blockCount);
        }
    }

    private void constructKGramIndex(){
        ArrayList<TermOccurrence> terms = dictionary.constructTermsFromDictionary(2);
        biGramDictionary = new TermDictionary(comparator, terms);
        biGramIndex = new NGramIndex(biGramDictionary, terms, biGramSize(), comparator);
        terms = dictionary.constructTermsFromDictionary(3);
        triGramDictionary = new TermDictionary(comparator, terms);
        triGramIndex = new NGramIndex(triGramDictionary, terms, triGramSize(), comparator);
    }

    public VectorSpaceModel getVectorSpaceModel(int docId, TermWeighting termWeighting, DocumentWeighting documentWeighting){
        return new VectorSpaceModel(positionalIndex.getTermFrequencies(docId), positionalIndex.getDocumentFrequencies(), documents.size(), termWeighting, documentWeighting);
    }

    public double cosineSimilarity(Collection collection2, VectorSpaceModel spaceModel1, VectorSpaceModel spaceModel2){
        int index1, index2;
        double sum = 0.0;
        for (index1 = 0; index1 < vocabularySize(); index1++){
            if (spaceModel1.get(index1) > 0.0){
                index2 = collection2.dictionary.getWordIndex(dictionary.getWord(index1).getName());
                if (index2 != -1 && spaceModel2.get(index2) > 0.0){
                    sum += spaceModel1.get(index1) * spaceModel2.get(index2);
                }
            }
        }
        return sum;
    }

    private double cosineSimilarity(VectorSpaceModel spaceModel1, VectorSpaceModel spaceModel2){
        int index;
        double sum = 0.0;
        for (index = 0; index < vocabularySize(); index++){
            sum += spaceModel1.get(index) * spaceModel2.get(index);
        }
        return sum;
    }

    public Matrix cosineSimilarity(TermWeighting termWeighting, DocumentWeighting documentWeighting){
        Matrix result = new Matrix(size(), size());
        VectorSpaceModel[] models;
        models = new VectorSpaceModel[documents.size()];
        for (int i = 0; i < documents.size(); i++){
            models[i] = getVectorSpaceModel(i, termWeighting, documentWeighting);
        }
        for (int i = 0; i < documents.size(); i++){
            for (int j = 0; j < documents.size(); j++){
                result.setValue(i, j, cosineSimilarity(models[i], models[j]));
            }
        }
        return result;
    }

    public ArrayList<String> sharedWordList(Collection collection2, VectorSpaceModel spaceModel1, VectorSpaceModel spaceModel2){
        int index1, index2;
        ArrayList<String> list = new ArrayList<>();
        for (index1 = 0; index1 < vocabularySize(); index1++){
            if (spaceModel1.get(index1) > 0.0){
                index2 = collection2.dictionary.getWordIndex(dictionary.getWord(index1).getName());
                if (index2 != -1 && spaceModel2.get(index2) > 0.0){
                    list.add(dictionary.getWord(index1).getName());
                }
            }
        }
        return list;
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
