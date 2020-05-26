package com.treestructure.certinator.model;

public enum KeyStoreType {

    KEYSTORE("KEYSTORE"),
    PASSWORDSTORE("PASSWORDSTORE");

    private final String type;

    KeyStoreType(final String text) {
        this.type = text;
    }

    @Override
    public String toString() {
        return type;
    }
}
