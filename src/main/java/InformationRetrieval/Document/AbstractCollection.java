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

    /**
     * Constructor for the AbstractCollection class. All collections, disk, memory, large, medium are extended from this
     * basic class. Loads the attribute list from attribute file if required. Loads the names of the documents from
     * the document collection. If the collection is a categorical collection, also loads the category tree.
     * @param directory Directory where the document collection resides.
     * @param parameter Search parameter
     */
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

    /**
     * Loads the category tree for the categorical collections from category index file. Each line of the category index
     * file stores the index of the category and the category name with its hierarchy. Hierarchy string is obtained by
     * concatenating the names of all nodes in the path from root node to a leaf node separated with '%'.
     */
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
        } catch (IOException ignored) {
        }
    }

    /**
     * Loads the attribute list from attribute index file. Attributes are single or bi-word phrases representing the
     * important features of products in the collection. Each line of the attribute file contains either single or a two
     * word expression.
     */
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

    /**
     * Returns size of the document collection.
     * @return Size of the document collection.
     */
    public int size(){
        return documents.size();
    }

    /**
     * Returns size of the term dictionary.
     * @return Size of the term dictionary.
     */
    public int vocabularySize(){
        return dictionary.size();
    }

    /**
     * Constructs bi-gram and tri-gram indexes in memory.
     */
    protected void constructNGramIndex(){
        ArrayList<TermOccurrence> terms = dictionary.constructNGramTermsFromDictionary(2);
        biGramDictionary = new TermDictionary(comparator, terms);
        biGramIndex = new NGramIndex(biGramDictionary, terms, comparator);
        terms = dictionary.constructNGramTermsFromDictionary(3);
        triGramDictionary = new TermDictionary(comparator, terms);
        triGramIndex = new NGramIndex(triGramDictionary, terms, comparator);
    }

    /**
     * The method prints the representative words to the output file. Representative words are the
     * most frequent words that appears in the name of the product.
     * @param fileName Output file name.
     */
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
