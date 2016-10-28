package main.itemsim;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * parse the items from the returned JSON file
 * @author linhong
 */
public class ParseData {
    private ObjectMapper mapper = new ObjectMapper();
    private String fileName;
    private boolean dedupID;

    /**
     *
     * @param fileName String of the Json file name
     * @param _dedup  boolean: if it is true, we merge items with the same item ID as a single item before going to
     *                finding similar item procedure (i.e., the unique identifier is the item ID); if it is false, although two items in the list have the same item
     *                ID, we consider them are different (i.e., the unique identifier is the position in the list of
     *                search result)
     */
    public ParseData(String fileName, boolean _dedup){

        this.fileName = fileName;
        this.dedupID = _dedup;
    }

    public List<Item> readData(){
        try {
            List<Item> itemList = new ArrayList<>();
            System.out.println("Parsing Json File... ");
            JsonNode rootNode = mapper.readTree(new File(fileName));
            JsonNode responseNode = rootNode.get("findItemsByKeywordsResponse");
            for (JsonNode respondElement: responseNode) {
                Iterator<String>  responseFields = respondElement.fieldNames();
                while (responseFields.hasNext()) {
                    String name = responseFields.next();
                    // System.out.println("field: \t" + name);
                    // only parse the searchResult
                    if (name.equals("searchResult")) {
                        JsonNode searchResults = respondElement.get(name);
                        for (JsonNode itemElements: searchResults) {
                            Iterator<String>  itemFields = itemElements.fieldNames();
                            while (itemFields.hasNext()) {
                                String itemName = itemFields.next();
                                if (itemName.equals("item")) {
                                    // Parse Item Nodes
                                    JsonNode items = itemElements.get(itemName);
                                    itemList = parseItemNode(items);
                                }
                            }
                        }
                    }
                }
            }
            return itemList;
        } catch (IOException ex) {
            Logger.getLogger(ParseData.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /*
    parse the Item Node information from the Json Tree
    */
    private List<Item> parseItemNode(JsonNode itemElements) {
        List<Item> itemList = new ArrayList<>();
        int cnt = 0;
        for (JsonNode item: itemElements) {
            Item newItem = new Item();
            Iterator<String>  itemNames = item.fieldNames();
            while (itemNames.hasNext()) {
                String itemTitle = itemNames.next();
                JsonNode itemProperty = item.get(itemTitle);
                if ("itemId".equals(itemTitle)) {
                    for (JsonNode itemId: itemProperty) {
                        newItem.setId(itemId.asText());
                    }
                }
                if ("title".equals(itemTitle)) {
                    for (JsonNode title: itemProperty) {
                        newItem.setTitle(title.asText());
                    }
                }
                if ("subtitle".equals(itemTitle)) {
                    for (JsonNode subTitle : itemProperty) {
                        newItem.setSubtitle(subTitle.asText());
                    }
                }

                if ("location".equals(itemTitle)) {
                    for (JsonNode location: itemProperty) {
                        newItem.setLocation(location.asText());
                    }
                }
                if ("postalCode".equals(itemTitle)) {
                    for (JsonNode postalCode: itemProperty) {
                        newItem.setPostalCode(postalCode.asText());
                    }
                }
                if ("returnsAccepted".equals(itemTitle)) {
                    for (JsonNode returnsAccepted: itemProperty) {
                        newItem.setReturn(Boolean.valueOf(returnsAccepted.asText()));
                    }
                }

                if ("viewItemURL".equals(itemTitle)) {
                    for (JsonNode itemURL: itemProperty) {
                        newItem.setURL(itemURL.asText());
                    }
                }

                if ("sellingStatus".equals(itemTitle)) {
                    for (JsonNode selling: itemProperty) {
                        Iterator<String>  sellingField = selling.fieldNames();
                        while (sellingField.hasNext()) {
                            String str = sellingField.next();
                            if ("convertedCurrentPrice".equals(str)) {
                                for (JsonNode itemValueNode: selling.get(str)) {
                                    String valStr = itemValueNode.get("__value__").asText();
                                    newItem.setPrice(Double.valueOf(valStr));
                                }
                            }
                        }
                    }
                }

                if ("condition".equals(itemTitle)) {
                    for (JsonNode condition: itemProperty) {
                        Iterator<String> conditionField = condition.fieldNames();
                        while (conditionField.hasNext()) {
                            String str = conditionField.next();
                            if ("conditionDisplayName".equals(str)) {
                                for (JsonNode conditionValue: condition.get(str)) {
                                    newItem.setCondition(conditionValue.asText());
                                }
                            }
                        }
                    }
                }

                if ("shippingInfo".equals(itemTitle)) {
                    // System.out.println(itemProperty);
                    boolean _exped = false;
                    boolean _oneday = false;
                    int _htime = 0;
                    double _cost = 0;
                    ArrayList<String> locations = new ArrayList<>();
                    for (JsonNode shipping: itemProperty) {
                        Iterator<String> shippingField = shipping.fieldNames();
                        while (shippingField.hasNext()) {
                            String str = shippingField.next();
                            if ("expeditedShipping".equals(str)) {
                                for (JsonNode isExpeditedShipping: shipping.get(str)) {
                                    _exped = Boolean.valueOf(isExpeditedShipping.asText());
                                }
                            }
                            if ("oneDayShippingAvailable".equals(str)) {
                                for (JsonNode oneDayShippingAvailable: shipping.get(str)) {
                                    _oneday = Boolean.valueOf(oneDayShippingAvailable.asText());
                                }
                            }
                            if ("handlingTime".equals(str)) {
                                for (JsonNode handlingTime: shipping.get(str)) {
                                    _htime = Integer.valueOf(handlingTime.asText());
                                }
                            }
                            if ("shippingServiceCost".equals(str)) {
                                for (JsonNode shippingServiceCost: shipping.get(str)) {
                                    String valStr = shippingServiceCost.get("__value__").asText();
                                    _cost = Double.valueOf(valStr);
                                }
                            }
                            if ("shipToLocations".equals(str)) {
                                for (JsonNode shipToLocations: shipping.get(str)) {
                                    locations.add(shipToLocations.asText());
                                }
                            }
                        }
                    }
                    newItem.setShiping(_exped,_oneday,_htime,_cost,locations);
                }

                // process unused fields here
                if ("country".equals(itemTitle)) {
                }

                if ("globalId".equals(itemTitle)) {
                }

                if ("topRatedListing".equals(itemTitle)) {
                }

                if ("galleryURL".equals(itemTitle)) {
                }

                if ("autoPay".equals(itemTitle)) {
                }
                if ("paymentMethod".equals(itemTitle)) {
                }

                if ("primaryCategory".equals(itemTitle)) {
                }

                if ("listingInfo".equals(itemTitle)) {
                }
            }
            itemList.add(newItem);
            cnt++;
        }
        System.out.println("Parsing finished: " + cnt + " Items in total");
        if (dedupID == true) {
            HashSet<Item> candidates = new HashSet<Item>(itemList.size());
            candidates.addAll(itemList);
            itemList.clear();
            itemList.addAll(candidates);
        }
        System.out.println("# of Unique items: " + itemList.size() + "\n");
        return itemList;
    }

    public static void main(String []args) throws IOException {
        ParseData myparser = new ParseData("searchout/test_facebook.json",true);
        List<Item> itemList = myparser.readData();
        System.out.println(itemList.size());
        int cnt = 0;
        for (Item item: itemList) {
            System.out.println("--- item " + cnt + "---");
            System.out.println(item);
            cnt++;
        }
    }
}
