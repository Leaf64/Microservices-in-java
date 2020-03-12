package introtask.dto;

public class Stats {
    private String userId;
    private int amount;

    public Stats(String userId, int amount) {
        this.userId = userId;
        this.amount = amount;
    }

    public Stats() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
