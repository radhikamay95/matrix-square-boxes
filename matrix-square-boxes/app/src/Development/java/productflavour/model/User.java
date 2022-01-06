package productflavour.model;

public class User {
    private String userName;
    private String useriD;
    private String userScore;
    private String time;
    private String attempt;

    public User(String useriD, String userName, String userScore, String time) {
        this.useriD = useriD;
        this.userName = userName;
        this.userScore = userScore;
        this.time = time;
    }

    public User() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserScore() {
        return userScore;
    }

    public void setUserScore(String userScore) {
        this.userScore = userScore;
    }

    public String getTime() {
        return time;
    }

    public String getAttempt() {
        return attempt;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setAttempt(String attempt) {
        this.attempt = attempt;
    }

    public String getUseriD() {
        return useriD;
    }

    public void setUseriD(String useriD) {
        this.useriD = useriD;
    }
}
