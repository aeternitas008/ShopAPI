package com.example.shop.exception;

public class NotEnoughGoodException extends CustomException {
    private final long amount;
    private final long currentAmount;

    public NotEnoughGoodException(String message, long amount, long currentAmount) {
        super(message + "\nAre not enough good, currentAmount: " + currentAmount + ", but you need: " + amount);
        this.amount = amount;
        this.currentAmount = currentAmount;
    }

    public long getAmount() {
        return amount;
    }

    public long getCurrentAmount() {
        return currentAmount;
    }
}
