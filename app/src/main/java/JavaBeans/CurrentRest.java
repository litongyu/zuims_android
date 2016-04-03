package JavaBeans;

/**
 * Created by kubenetes on 16/4/2.
 */
public class CurrentRest {

    private Integer restaurantId;

    private Integer whetherInfoComplete;

    private static CurrentRest ourInstance = new CurrentRest();

    public static CurrentRest getInstance() {
        return ourInstance;
    }


    public static void setOurInstance(CurrentRest ourInstance) {
        CurrentRest.ourInstance = ourInstance;
    }

    private CurrentRest() {
    }

    public Integer getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Integer restaurantId) {
        this.restaurantId = restaurantId;
    }

    public Integer getWhetherInfoComplete() {
        return whetherInfoComplete;
    }

    public void setWhetherInfoComplete(Integer whetherInfoComplete) {
        this.whetherInfoComplete = whetherInfoComplete;
    }
}
