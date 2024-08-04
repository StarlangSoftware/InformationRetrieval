package InformationRetrieval.Document;

import Corpus.*;
import InformationRetrieval.Index.CategoryNode;
import InformationRetrieval.Index.CategoryTree;

import java.util.ArrayList;

public class Document {

    private final String absoluteFileName;
    private final String fileName;
    private final int docId;
    private int size = 0;
    private final DocumentType documentType;
    private CategoryNode category;

    /**
     * Constructor for the Document class. Sets the attributes.
     * @param documentType Type of the document. Can be normal for normal documents, categorical for categorical
     *                     documents.
     * @param absoluteFileName Absolute file name of the document
     * @param fileName Relative file name of the document.
     * @param docId Id of the document
     */
    public Document(DocumentType documentType, String absoluteFileName, String fileName, int docId){
        this.docId = docId;
        this.absoluteFileName = absoluteFileName;
        this.fileName = fileName;
        this.documentType = documentType;
    }

    /**
     * Loads the document from input stream. For normal documents, it reads as a corpus. For categorical documents, the
     * first line contains categorical information, second line contains name of the product, third line contains
     * detailed info about the product.
     * @return Loaded document text.
     */
    public DocumentText loadDocument(){
        DocumentText documentText;
        switch (documentType){
            case NORMAL:
            default:
                documentText = new DocumentText(absoluteFileName, new TurkishSplitter());
                size = documentText.numberOfWords();
                break;
            case CATEGORICAL:
                Corpus corpus = new Corpus(absoluteFileName);
                if (corpus.sentenceCount() >= 2){
                    documentText = new DocumentText();
                    ArrayList<Sentence> sentences = new TurkishSplitter().split(corpus.getSentence(1).toString());
                    for (Sentence sentence : sentences){
                        documentText.addSentence(sentence);
                    }
                    size = documentText.numberOfWords();
                } else {
                    return null;
                }
                break;
        }
        return documentText;
    }

    /**
     * Loads the category of the document and adds it to the category tree. Category information is stored in the first
     * line of the document.
     * @param categoryTree Category tree to which new product will be added.
     */
    public void loadCategory(CategoryTree categoryTree){
        if (documentType == DocumentType.CATEGORICAL){
            Corpus corpus = new Corpus(absoluteFileName);
            if (corpus.sentenceCount() >= 2){
                category = categoryTree.addCategoryHierarchy(corpus.getSentence(0).toString());
            }
        }
    }

    /**
     * Accessor for the docId attribute.
     * @return docId attribute.
     */
    public int getDocId(){
        return docId;
    }

    /**
     * Accessor for the fileName attribute.
     * @return fileName attribute.
     */
    public String getFileName(){
        return fileName;
    }

    /**
     * Accessor for the absoluteFileName attribute.
     * @return absoluteFileName attribute.
     */
    public String getAbsoluteFileName(){
        return absoluteFileName;
    }

    /**
     * Accessor for the size attribute.
     * @return size attribute.
     */
    public int getSize(){
        return size;
    }

    /**
     * Mutator for the size attribute.
     * @param size New size attribute.
     */
    public void setSize(int size){
        this.size = size;
    }

    /**
     * Mutator for the category attribute.
     * @param categoryTree Category tree to which new category will be added.
     * @param category New category that will be added
     */
    public void setCategory(CategoryTree categoryTree, String category){
        this.category = categoryTree.addCategoryHierarchy(category);
    }

    /**
     * Accessor for the category attribute.
     * @return Category attribute as a String
     */
    public String getCategory(){
        return category.toString();
    }

    /**
     * Accessor for the category attribute.
     * @return Category attribute as a CategoryNode.
     */
    public CategoryNode getCategoryNode(){
        return category;
    }

}
