package fr.anto42.emma.game.modes.bingo.impl;

public class BingoConfig {
    private int cartSize = 5;

    public int getCartSize() {
        return cartSize;
    }

    public void setCartSize(int cartSize) {
        this.cartSize = cartSize;
    }

    private boolean firstWin = true;

    public boolean isFirstWin() {
        return firstWin;
    }

    public void setFirstWin(boolean firstWin) {
        this.firstWin = firstWin;
    }
}
