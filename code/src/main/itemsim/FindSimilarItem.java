package main.itemsim;

import java.io.IOException;
import java.util.*;
import java.io.File;

import static java.lang.System.exit;

/**
 * Given a keyword, this class will call the Ebay API, get a list of product items. Then for each item, we will return
 * another item which is most similar to the given item
 * @author linhong
 */
public class FindSimilarItem {
    // all the returned item results that match search query
    private List<Item> candidates;

    // LSH index for items
    private ItemLSHIndex itemIndex;

    /**
     * construct a class from list of items and bucket size
     * init candidates and build the index
     * @param candidates
     * @param bucket
     */
    public FindSimilarItem(List<Item> candidates, int bucket) {
        initItemsAndBuildIndex(candidates, bucket);
    }

    /**
     * given a keyword, and bucket size, construct a FindSimilarItem class
     * init candidates and build the index
     * @param keyWord
     * @param bucket
     * @throws IOException
     * @throws InterruptedException
     */
    public FindSimilarItem(String keyWord, int bucket, boolean dedupID) throws IOException, InterruptedException {
        List<Item> itemList = readFileAndParseItem(keyWord,dedupID);
        if (itemList.size() == 0) {
            System.out.println("Query result is empty");
            exit(1);
        }
        // init item list and build LSH index for items
        initItemsAndBuildIndex(itemList, bucket);
    }

    /**
     * given a keyword, find a list of items either from API call or old json file
     * @return ItemList (a List of Item )
     * @throws InterruptedException
     * @throws IOException
     */


    /**
     * given a keyword, find a list of items either from API call or old json file
     * @param keyWord
     * @return ItemList (a List of Item )
     * @throws InterruptedException
     * @throws IOException
     */
    private List<Item> readFileAndParseItem(String keyWord, boolean dedupID) throws InterruptedException, IOException {
        List<Item> itemList = new ArrayList<>();

        // check whether search result exist, if not calling ebay api to find the results
        String fileName = "searchout/" + keyWord.toLowerCase()+".json";
        File f = new File(fileName);
        if (!f.exists()) {
            System.out.println("Calling Ebay API");
            String[] commandline= {"python", "call_ebay_api.py", keyWord};
            Process p = Runtime.getRuntime().exec(commandline);
            p.waitFor();
            if (p.exitValue() !=0) {
                System.out.println("API call timeout, exit");
                return itemList;
            }
        }

        // parse the json file
        ParseData myParser = new ParseData(fileName,dedupID);
        itemList = myParser.readData();
        return itemList;
    }

    /**
     * @param bucket : number of buckets in LSH
     * Compute the LSH for items
     */
    private void initItemsAndBuildIndex(List<Item> itemList, int bucket) {
        // init itemList
        this.candidates = itemList;
        // build LSH index
        itemIndex = new ItemLSHIndex(candidates);
        itemIndex.createLSH(bucket);
    }

    /**
     * given a searchIndex of one item, find the item which is the most similar to the searching item
     * @param searchIndex : the Integer ID of the given item
     * @return : result the Integer ID of the most similar item Integer
     */
    public int searchSimItem(int searchIndex) {
        List<Integer> candidateItemIndexs = itemIndex.findCandidateItems(searchIndex);
        double maxSim = 0;
        int result = -1;
        SimilarityMeasure linearSimilarity = new LinearSimilarity();
        for (int index: candidateItemIndexs) {
            double simScore = linearSimilarity.calculateSimilarityScore(candidates.get(searchIndex),
                    itemIndex.getTFs()[searchIndex], candidates.get(index), itemIndex.getTFs()[index]);
            if (simScore > maxSim) {
                maxSim = simScore;
                result = index;
            }
        }
        return result;
    }

    /**
     * For each item, find its most similar item
     * print the result using the following format
     * Item_ID_1 \t Item_ID_2
     */
    public void enumerateAndPrint() {
        System.out.println("Item ID\tIs Most Similar To");
        System.out.println("------------\t------------");
        for (int i = 0; i < this.candidates.size(); i++) {
            int result = this.searchSimItem(i);
            if (result >= 0) {
                System.out.println(candidates.get(i).getID() + "\t" + candidates.get(result).getID());
            }
        }
    }

    /**
     * For each item, find its most similar item
     * print the result using the debug format: print item's title and URL
     */
    public void enumerateAndPrintDebug() {
        int count = 0;
        for (int i = 0; i < this.candidates.size(); i++) {
            int result = this.searchSimItem(i);
            System.out.println("Searching similar items for item "+candidates.get(i).getID()
                    +"\t"+candidates.get(i).getTitle()+"\t"+candidates.get(i).getViewItemURL());
            if (result != -1) {
                System.out.println(candidates.get(result).getTitle() + "\t" + candidates.get(result).getViewItemURL());
            } else {
                count++;
            }
            System.out.println("--------------");
        }
        System.out.println("number of missing iterms "+count);
    }

    /**
     *
     * @param GT ground truth data
     * @param a an item ID
     * @param b a similar item ID
     */
    public void addGT(HashMap<String, HashSet<String>> GT, String a, String b){
        HashSet<String> sims;
        if (GT.containsKey(a) == false) {
            sims = new HashSet<String>(10);
        } else {
            sims=GT.get(a);
        }
        sims.add(b);
        GT.put(a,sims);
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        if (args.length < 1) {
            System.out.println("Please type the keyword of the query");
            System.out.println("Usage: keywords [dedupID][bucket]");
            System.out.println("keywords: string, search term passing to ebay api");
            System.out.print("dedupID: boolean, true denotes the unique identifier is the Item ID and we need to merge");
            System.out.println("two items in the search result list having the same Item ID,");
            System.out.print("false denotes the unique identifier is the position in the list. Although two items in the ");
            System.out.println("list have the same Item ID, they are considered as two different ones");
            System.out.println("bucket: integer, parameter in LSH index");
            return;
        }

        System.out.println("Search keyword: " + args[0]);
        String keyWord = args[0];
        boolean dedupID = true;
        if (args.length >= 2) {
            dedupID = Boolean.parseBoolean(args[1]);
        }
        int bucket = 2;
        if (args.length >= 3) {
            bucket = Integer.parseInt(args[2]);
        }
        // find similarity item and output
        FindSimilarItem computeSim = new FindSimilarItem(keyWord, bucket,dedupID);
        computeSim.enumerateAndPrint();
    }
}
