package InformationRetrieval.Document;

import Corpus.*;
import Dictionary.Word;
import InformationRetrieval.Index.TermOccurrence;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;

public class Document {

    private String absoluteFileName;
    private String fileName;
    private Corpus corpus;
    private int docId;

    public Document(String absoluteFileName, String fileName, int docId){
        this.docId = docId;
        this.absoluteFileName = absoluteFileName;
        this.fileName = fileName;
        this.corpus = new Corpus(absoluteFileName);
    }

    public Document(String absoluteFileName, String fileName, int docId, HashMap<String, String> rootList){
        String line;
        this.docId = docId;
        this.absoluteFileName = absoluteFileName;
        this.fileName = fileName;
        this.corpus = new Corpus();
        try {
            FileReader fr = new FileReader(fileName);
            BufferedReader br = new BufferedReader(fr);
            line = br.readLine();
            while (line != null) {
                String[] wordArray = line.split(" ");
                Sentence sentence = new Sentence();
                for (String word : wordArray){
                    String lowerCased = word.toLowerCase(new Locale("tr"));
                    if (rootList.containsKey(lowerCased)){
                        sentence.addWord(new Word(rootList.get(lowerCased)));
                    }
                }
                corpus.addSentence(sentence);
                line = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public int size(){
        return corpus.numberOfWords();
    }

    ArrayList<TermOccurrence> getTerms(){
        ArrayList<TermOccurrence> terms = new ArrayList<TermOccurrence>();
        int size = 0;
        for (int i = 0; i < corpus.sentenceCount(); i++){
            for (int j = 0; j < corpus.getSentence(i).wordCount(); j++){
                terms.add(new TermOccurrence(corpus.getSentence(i).getWord(j), docId, size));
                size++;
            }
        }
        return terms;
    }

}
