package JavaBeans;

/**
 * Created by kubenetes on 16/5/19.
 */
public class RestInfo {

    private Integer restaurantId;

    private String restaurantName;

    private String restaurantTele;

    private String restaurantAddress;

    private String restaurantOpenTime;

    private String introduction;

    private String noonPrice;

    private String nightPrice;

    private String smoke;

    private String park;

    private String[] images;

    private Double latitude;

    private Double longitude;

    private String city;

    private Integer persistTable;

    private String restaurantType;

    private String[] discountType;

    private Integer persistTime;

    private String hotelName;

    private Integer contractState;

    private Integer sellerId;

    private Integer recommendLevel;

    private static RestInfo ourInstance = new RestInfo();

    public static RestInfo getInstance() {
        return ourInstance;
    }

    private RestInfo() {
    }

    public static void setOurInstance(RestInfo ourInstance) {
        RestInfo.ourInstance = ourInstance;
    }

    public Integer getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Integer restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getRestaurantTele() {
        return restaurantTele;
    }

    public void setRestaurantTele(String restaurantTele) {
        this.restaurantTele = restaurantTele;
    }

    public String getRestaurantAddress() {
        return restaurantAddress;
    }

    public void setRestaurantAddress(String restaurantAddress) {
        this.restaurantAddress = restaurantAddress;
    }

    public String getRestaurantOpenTime() {
        return restaurantOpenTime;
    }

    public void setRestaurantOpenTime(String restaurantOpenTime) {
        this.restaurantOpenTime = restaurantOpenTime;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getNoonPrice() {
        return noonPrice;
    }

    public void setNoonPrice(String noonPrice) {
        this.noonPrice = noonPrice;
    }

    public String getNightPrice() {
        return nightPrice;
    }

    public void setNightPrice(String nightPrice) {
        this.nightPrice = nightPrice;
    }

    public String getSmoke() {
        return smoke;
    }

    public void setSmoke(String smoke) {
        this.smoke = smoke;
    }

    public String getPark() {
        return park;
    }

    public void setPark(String park) {
        this.park = park;
    }

    public String[] getImages() {
        return images;
    }

    public void setImages(String[] images) {
        this.images = images;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Integer getPersistTable() {
        return persistTable;
    }

    public void setPersistTable(Integer persistTable) {
        this.persistTable = persistTable;
    }

    public String getRestaurantType() {
        return restaurantType;
    }

    public void setRestaurantType(String restaurantType) {
        this.restaurantType = restaurantType;
    }

    public String[] getDiscountType() {
        return discountType;
    }

    public void setDiscountType(String[] discountType) {
        this.discountType = discountType;
    }

    public Integer getPersistTime() {
        return persistTime;
    }

    public void setPersistTime(Integer persistTime) {
        this.persistTime = persistTime;
    }

    public String getHotelName() {
        return hotelName;
    }

    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }

    public Integer getContractState() {
        return contractState;
    }

    public void setContractState(Integer contractState) {
        this.contractState = contractState;
    }

    public Integer getSellerId() {
        return sellerId;
    }

    public void setSellerId(Integer sellerId) {
        this.sellerId = sellerId;
    }

    public Integer getRecommendLevel() {
        return recommendLevel;
    }

    public void setRecommendLevel(Integer recommendLevel) {
        this.recommendLevel = recommendLevel;
    }
}
