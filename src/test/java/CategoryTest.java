import DataStructure.CounterHashMap;
import InformationRetrieval.Document.DocumentType;
import InformationRetrieval.Document.MemoryCollection;
import InformationRetrieval.Document.Parameter;
import InformationRetrieval.Index.CategoryNode;
import InformationRetrieval.Index.CategoryTree;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class CategoryTest {

    private HashMap<Integer, CategoryNode> loadCategoryTree(CategoryTree tree, String fileName){
        HashMap<Integer, CategoryNode> categoryMapping = new HashMap<>();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(fileName)), StandardCharsets.UTF_8));
            String line = br.readLine();
            while (line != null){
                String[] items = line.split("\t");
                if (items.length > 0){
                    int id = Integer.parseInt(items[0]);
                    if (items.length > 1){
                        CategoryNode categoryNode = tree.addCategoryHierarchy(items[1]);
                        categoryMapping.put(id, categoryNode);
                    }
                }
                line = br.readLine();
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return categoryMapping;
    }

    private void loadCategories(){
        CategoryTree starlangTree = new CategoryTree("Starlang");
        HashMap<Integer, CategoryNode> starlang = loadCategoryTree(starlangTree, "../../../../SearchEngine/Starlang.txt");
        CategoryTree amazonTree = new CategoryTree("Amazon");
        HashMap<Integer, CategoryNode> amazonMap = loadCategoryTree(starlangTree, "../../../../SearchEngine/Amazon.txt");
        CategoryTree ciceksepetiTree = new CategoryTree("Ciceksepeti");
        HashMap<Integer, CategoryNode> ciceksepetiMap = loadCategoryTree(starlangTree, "../../../../SearchEngine/Ciceksepeti.txt");
        CategoryTree gittigidiyorTree = new CategoryTree("Gittigidiyor");
        HashMap<Integer, CategoryNode> gittigidiyorMap = loadCategoryTree(starlangTree, "../../../../SearchEngine/Gittigidiyor.txt");
        CategoryTree hepsiburadaTree = new CategoryTree("Hepsiburada");
        HashMap<Integer, CategoryNode> hepsiburadaMap = loadCategoryTree(starlangTree, "../../../../SearchEngine/Hepsiburada.txt");
        CategoryTree trendyolTree = new CategoryTree("Trendyol");
        HashMap<Integer, CategoryNode> trendyolMap = loadCategoryTree(starlangTree, "../../../../SearchEngine/Trendyol.txt");
        CategoryTree n11Tree = new CategoryTree("N11");
        HashMap<Integer, CategoryNode> n11Map = loadCategoryTree(starlangTree, "../../../../SearchEngine/N11.txt");
    }

    public void printRepresentatives(){
        int count = 100;
        Parameter parameter = new Parameter();
        parameter.setRepresentativeCount(count);
        parameter.setDocumentType(DocumentType.CATEGORICAL);
        parameter.setLoadIndexesFromFile(true);
        parameter.setNGramIndex(false);
        MemoryCollection amazon = new MemoryCollection("../../../../SearchEngine/Amazon", parameter);
        amazon.printRepresentatives("Amazon-list" + count + ".txt");
        MemoryCollection ciceksepeti = new MemoryCollection("../../../../SearchEngine/Ciceksepeti", parameter);
        ciceksepeti.printRepresentatives("Ciceksepeti-list" + count + ".txt");
        MemoryCollection hepsiburada = new MemoryCollection("../../../../SearchEngine/Hepsiburada", parameter);
        hepsiburada.printRepresentatives("Hepsiburada-list" + count + ".txt");
        MemoryCollection gittigidiyor = new MemoryCollection("../../../../SearchEngine/Gittigidiyor", parameter);
        gittigidiyor.printRepresentatives("Gittigidiyor-list" + count + ".txt");
        MemoryCollection trendyol = new MemoryCollection("../../../../SearchEngine/Trendyol", parameter);
        trendyol.printRepresentatives("Trendyol-list" + count + ".txt");
        MemoryCollection n11 = new MemoryCollection("../../../../SearchEngine/N11", parameter);
        n11.printRepresentatives("N11-list" + count + ".txt");
    }

    private HashMap<String, ArrayList<String>> loadRepresentatives(String fileName, int maxLevel){
        HashMap<String, ArrayList<String>> result = new HashMap<>();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(fileName)), StandardCharsets.UTF_8));
            String line = br.readLine();
            while (line != null){
                String[] items = line.split("\t");
                if (items.length > 1){
                    String category = items[0];
                    ArrayList<String> representatives = new ArrayList<>();
                    for (int i = 1; i < items.length; i++){
                        representatives.add(items[i].toLowerCase(new Locale("tr")));
                    }
                    if (category.split("%").length <= maxLevel){
                        result.put(category, representatives);
                    }
                }
                line = br.readLine();
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    ArrayList<String> getMatches(ArrayList<String> list1, ArrayList<String> list2){
        ArrayList<String> result = new ArrayList<>();
        for (String item1 : list1){
            if (!result.contains(item1) && list2.contains(item1)){
                result.add(item1);
            }
        }
        return result;
    }

    @Test
    public void testCompareCategories() {
        int maxLevel = 4;
        int representativeCount = 100;
        int displayCount = 1000;
        HashMap<String, ArrayList<String>>[] sites = new HashMap[6];
        String[] siteNames = new String[6];
        siteNames[0] = "Amazon";
        siteNames[1] = "Ciceksepeti";
        siteNames[2] = "Hepsiburada";
        siteNames[3] = "Trendyol";
        siteNames[4] = "Gittigidiyor";
        siteNames[5] = "N11";
        for (int i = 0; i < 6; i++){
            sites[i] = loadRepresentatives("../../../../SearchEngine/" + siteNames[i] + "-list" + representativeCount + ".txt", maxLevel);
        }
        CounterHashMap<Integer> matchCounts = new CounterHashMap<>();
        for (int i = 0; i < 6; i++){
            HashMap<String, ArrayList<String>> site1 = sites[i];
            for (int j = i + 1; j < 6; j++){
                HashMap<String, ArrayList<String>> site2 = sites[j];
                for (String category1 : sites[i].keySet()){
                    for (String category2 : sites[j].keySet()){
                        ArrayList<String> matches = getMatches(site1.get(category1), site2.get(category2));
                        matchCounts.put(matches.size());
                    }
                }
                System.out.println("Compared " + siteNames[i] + " with " + siteNames[j]);
            }
        }
        int minMatch = 2;
        int total = 0;
        for (int i = representativeCount; i >= 0; i--){
            if (total + matchCounts.count(i) > displayCount){
                minMatch = i;
                break;
            }
            total += matchCounts.count(i);
        }
        for (int i = 0; i < 6; i++){
            HashMap<String, ArrayList<String>> site1 = sites[i];
            for (int j = i + 1; j < 6; j++){
                HashMap<String, ArrayList<String>> site2 = sites[j];
                for (String category1 : sites[i].keySet()){
                    for (String category2 : sites[j].keySet()){
                        ArrayList<String> matches = getMatches(site1.get(category1), site2.get(category2));
                        if (matches.size() >= minMatch){
                            StringBuilder matchedWords = new StringBuilder(matches.get(0));
                            for (int k = 1; k < matches.size(); k++){
                                if (!matchedWords.toString().contains(matches.get(k))){
                                    matchedWords.append(" ").append(matches.get(k));
                                }
                            }
                            System.out.println(matches.size() + "\t" + siteNames[i] + "\t" + siteNames[j] + "\t" + category1 + "\t" + category2 + "\t" + matchedWords);
                        }
                    }
                }
            }
        }
    }

}
