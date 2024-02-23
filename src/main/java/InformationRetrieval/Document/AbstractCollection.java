package InformationRetrieval.Document;

import Dictionary.WordComparator;
import InformationRetrieval.Index.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public abstract class AbstractCollection {

    protected TermDictionary dictionary;
    protected TermDictionary phraseDictionary;
    protected TermDictionary biGramDictionary;
    protected TermDictionary triGramDictionary;
    protected ArrayList<Document> documents;
    protected IncidenceMatrix incidenceMatrix;
    protected InvertedIndex invertedIndex;
    protected NGramIndex biGramIndex;
    protected NGramIndex triGramIndex;
    protected PositionalIndex positionalIndex;
    protected InvertedIndex phraseIndex;
    protected PositionalIndex phrasePositionalIndex;

    protected WordComparator comparator;

    protected String name;
    protected Parameter parameter;
    protected CategoryTree categoryTree = null;
    protected HashSet<String> attributeList;

    public AbstractCollection(String directory,
                            Parameter parameter) {
        this.name = directory;
        this.comparator = parameter.getWordComparator();
        this.parameter = parameter;
        loadAttributeList();
        File folder = new File(directory);
        File[] listOfFiles = folder.listFiles();
        if (listOfFiles != null) {
            Arrays.sort(listOfFiles);
            int fileLimit = listOfFiles.length;
            documents = new ArrayList<>(fileLimit);
            if (parameter.limitNumberOfDocumentsLoaded()) {
                fileLimit = parameter.getDocumentLimit();
            }
            int i = 0;
            int j = 0;
            while (i < listOfFiles.length && j < fileLimit) {
                File file = listOfFiles[i];
                if (file.getName().endsWith(".txt")) {
                    Document document = new Document(parameter.getDocumentType(), file.getAbsolutePath(), file.getName(), j);
                    documents.add(document);
                    j++;
                }
                i++;
            }
        }
        if (parameter.getDocumentType() == DocumentType.CATEGORICAL) {
            loadCategories();
        }
    }
    private void loadCategories(){
        try {
            categoryTree = new CategoryTree(name);
            BufferedReader br = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(name + "-categories.txt")), StandardCharsets.UTF_8));
            String line = br.readLine();
            while (line != null){
                String[] items = line.split("\t");
                if (items.length > 0){
                    int docId = Integer.parseInt(items[0]);
                    if (items.length > 1){
                        documents.get(docId).setCategory(categoryTree, items[1]);
                    }
                }
                line = br.readLine();
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void loadAttributeList(){
        try {
            attributeList = new HashSet<>();
            BufferedReader br = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(name + "-attributelist.txt")), StandardCharsets.UTF_8));
            String line = br.readLine();
            while (line != null){
                attributeList.add(line);
                line = br.readLine();
            }
            br.close();
        } catch (IOException ignored) {
        }
    }

    public int size(){
        return documents.size();
    }

    public int vocabularySize(){
        return dictionary.size();
    }

    protected void constructNGramIndex(){
        ArrayList<TermOccurrence> terms = dictionary.constructTermsFromDictionary(2);
        biGramDictionary = new TermDictionary(comparator, terms);
        biGramIndex = new NGramIndex(biGramDictionary, terms, comparator);
        terms = dictionary.constructTermsFromDictionary(3);
        triGramDictionary = new TermDictionary(comparator, terms);
        triGramIndex = new NGramIndex(triGramDictionary, terms, comparator);
    }

    public void printRepresentatives(String fileName){
        try {
            PrintWriter output = new PrintWriter(fileName, "UTF-8");
            categoryTree.printRepresentatives(output, dictionary);
            output.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
