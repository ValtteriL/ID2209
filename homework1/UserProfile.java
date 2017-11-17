package homework1;
import java.io.Serializable;


public class UserProfile implements Serializable {
    public int minAge;
    public int maxAge;

    public UserProfile(int min, int max) {
        this.minAge = min;
        this.maxAge = max;
    }
}