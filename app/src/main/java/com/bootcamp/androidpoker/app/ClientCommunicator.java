package com.bootcamp.androidpoker.app;

/**
 * Created by freopen on 12/4/14.
 */
public abstract class ClientCommunicator {
    public void call() {

    }

    public void raise(int amount) {

    }

    public void fold() {

    }

    abstract void onShowCards(String first, String second);

    abstract void onHideCards();

    abstract void onChangeCash(int cash);
}
