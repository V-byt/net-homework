public class User {
    private String userId;
    private String passName;
    private Integer money;

    public User(String userId, String passName, Integer money) {
        this.userId = userId;
        this.passName = passName;
        this.money = money;
    }
    public User(){}

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassName() {
        return passName;
    }

    public void setPassName(String passName) {
        this.passName = passName;
    }

    public Integer getMoney() {
        return money;
    }

    public void setMoney(Integer money) {
        this.money = money;
    }
}
