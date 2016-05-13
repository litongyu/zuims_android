package JavaBeans;

import com.google.gson.Gson;

/**
 * Created by kubenetes on 16/4/2.
 */
public class Order {

    private Integer orderId;

    private Integer restaurantId;

    private String  phoneId;

    private String  orderTime;

    private String  state;

    private String  createTime;

    private Integer dinerNum;

    private String  more;

    private String  handleType;

    private Integer dorderSum;

    private String  restaurantName;

    private String  userLastName;

    private String  userFirstName;

    private String  userEmail;

    private Integer gender;

    private String  userVipLevel;

    public Order(){

    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Integer restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getPhoneId() {
        return phoneId;
    }

    public void setPhoneId(String phoneId) {
        this.phoneId = phoneId;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Integer getDinerNum() {
        return dinerNum;
    }

    public void setDinerNum(Integer dinerNum) {
        this.dinerNum = dinerNum;
    }

    public String getMore() {
        return more;
    }

    public void setMore(String more) {
        this.more = more;
    }

    public String getHandleType() {
        return handleType;
    }

    public void setHandleType(String handleType) {
        this.handleType = handleType;
    }

    public Integer getDorderSum() {
        return dorderSum;
    }

    public void setDorderSum(Integer dorderSum) {
        this.dorderSum = dorderSum;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getUserLastName() {
        return userLastName;
    }

    public void setUserLastName(String userLastName) {
        this.userLastName = userLastName;
    }

    public String getUserFirstName() {
        return userFirstName;
    }

    public void setUserFirstName(String userFirstName) {
        this.userFirstName = userFirstName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getUserVipLevel() {
        return userVipLevel;
    }

    public void setUserVipLevel(String userVipLevel) {
        this.userVipLevel = userVipLevel;
    }

    public String getSearchString(){
        return "" + orderId + phoneId + orderTime + state + createTime + dinerNum
                + more + handleType + dorderSum + restaurantName + userLastName + userFirstName
                + userEmail + (gender==1?"男":"女") + userVipLevel;
    }

    @Override
    public String toString(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
