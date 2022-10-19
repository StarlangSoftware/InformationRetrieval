package InformationRetrieval.Document;

import Corpus.*;

import java.util.ArrayList;

public class Document {

    private final String absoluteFileName;
    private final String fileName;
    private final int docId;
    private int size = 0;
    private final DocumentType documentType;
    private CategoryHierarchy categoryHierarchy;

    public Document(DocumentType documentType, String absoluteFileName, String fileName, int docId){
        this.docId = docId;
        this.absoluteFileName = absoluteFileName;
        this.fileName = fileName;
        this.documentType = documentType;
    }

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
                    categoryHierarchy = new CategoryHierarchy(corpus.getSentence(0).toString());
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
    public int getDocId(){
        return docId;
    }

    public String getFileName(){
        return fileName;
    }

    public String getAbsoluteFileName(){
        return absoluteFileName;
    }

    public int getSize(){
        return size;
    }

    public void setSize(int size){
        this.size = size;
    }

    public void setCategoryHierarchy(String categoryHierarchy){
        this.categoryHierarchy = new CategoryHierarchy(categoryHierarchy);
    }

    public CategoryHierarchy getCategoryHierarchy(){
        return categoryHierarchy;
    }

}
