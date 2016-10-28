package main.itemsim;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;

/**
 * The in-memory data structure for item class
 * @author linhong
 */
public class Item {
    @JsonProperty("itemId")
    String id;

    @JsonProperty("title")
    String title;

    @JsonProperty("subtitle")
    String subtitle;

    @JsonProperty("location")
    String location;

    @JsonProperty("postalCode")
    String postalCode;

    @JsonProperty("returnsAccepted")
    boolean returnsAccepted;

    @JsonProperty("convertedCurrentPrice")
    double convertedprice;

    @JsonProperty("conditionDisplayName")
    String conditionDisplayName;

    @JsonProperty("expeditedShipping")
    boolean expeditedship;

    @JsonProperty("oneDayShippingAvailable")
    boolean oneDayShippingAvailable;

    @JsonProperty("handlingTime")
    int handlingTime;

    @JsonProperty("shippingServiceCost.__value__")
    double shippingServiceCost;

    @JsonProperty("shipToLocations")
    ArrayList<String> shiplocations;

    @JsonProperty("viewItemURLv")
    String viewItemURL;

    public Item(){
        
    }

    /**
     * initilize the Item class with hard identifier
     * @param _id
     */
    public Item(String _id){
        id = _id;
    }

    /**
     * Initilize the title and subtitle information
     * @param _id
     */
    public void setId(String _id) {
        id = _id;
    }
    public void setTitle(String _title) {
        this.title = _title;
    }

    public void setSubtitle(String _subtitle) {
        this.subtitle = _subtitle;
    }

    /**
     * Initilize the location informaiton
     * @param _location
     */
    public void setLocation(String _location) {
        this.location = _location;
    }

    public void setPostalCode (String _postcode) {
        this.postalCode = _postcode;
    }

    /**
     * Initilize the shipping information
     * @param _expedited
     * @param oneday
     * @param _handingtime
     * @param _shipcost
     * @param _shiplocs
     */
    public void setShiping(boolean _expedited, boolean oneday, int _handingtime, double _shipcost, ArrayList<String> _shiplocs){
        this.expeditedship = _expedited;
        this.oneDayShippingAvailable = oneday;
        this.handlingTime = _handingtime;
        this.shippingServiceCost = _shipcost;
        shiplocations = _shiplocs;
    }

    public void setReturn(boolean _return) {
        this.returnsAccepted = _return;
    }

    /**
     *  Initilize the price information
     * @param _price
     */
    public void setPrice(double _price){
        this.convertedprice = _price;
    }

    /**
     * Initilize the new and used condition information
     * @param _condition
     */
    public void setCondition(String _condition){
        this.conditionDisplayName = _condition;
    }

    public void setURL (String _url) {
        this.viewItemURL = _url;
    }

    public String getTitle(){
        return this.title;
    }

    public String getSubtitle() {
        return this.subtitle;
    }

    public String getCondition() {
        return this.conditionDisplayName;
    }

    public String getID(){
        return this.id;
    }

    public double getPrice() { return this.convertedprice;}

    public String getLocation (){
        return this.location;
    }

    public ArrayList<String> getShiplocations() {
        return this.shiplocations;
    }

    public String getViewItemURL() {
        return this.viewItemURL;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof  Item) {
            Item item = (Item) obj;
            return id.equals(item.getID());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return this.getID().hashCode();
    }

    @Override
    public String toString(){
        String str1 = "id: " + this.id + "\ntitle: " + this.title + " \nsubtitle: " + this.subtitle;
        String str2 = "postcode: " + this.postalCode + "\nprice: " + this.convertedprice +
                "\nshippingcost: " + this.shippingServiceCost +
                "\noneDayShippingAvailable: " + this.oneDayShippingAvailable + "\nlocation: " + this.location;
        String str3 = "shiplocations: ";
        for (String loc: this.shiplocations) {
            str3 += loc + " ";
        }
        String str4 = "itemURL: " + this.viewItemURL;
        return str1 + "\n" + str2 + "\n" + str3 + "\n" + str4;
    }

    public static void main(String []args){
        Item i = new Item("1");
        i.setTitle("Apple Iphone 6s");
        i.setPrice(699);
        System.out.println(i.toString());
    }
}
