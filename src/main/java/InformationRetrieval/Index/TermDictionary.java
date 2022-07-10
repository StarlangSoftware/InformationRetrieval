package InformationRetrieval.Index;

import Dictionary.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;

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
}
