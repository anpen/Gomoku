package com.example.frank.gomoku.model;

import android.graphics.Point;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by Frank on 2016/1/25.
 */
public class Chessman implements Serializable {

    private int x;
    private int y;

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getColor() {
        return color;
    }

    private int color;

    public Chessman(int x, int y, int color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }

    public Chessman(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setColor(int c) {
        color = c;
    }

    public byte[] serialize() throws IOException {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        ObjectOutputStream o = new ObjectOutputStream(b);
        o.writeObject(this);
        return b.toByteArray();
    }
    //AbstractMessage was actually the message type I used, but feel free to choose your own type
    public static Chessman deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream b = new ByteArrayInputStream(bytes);
        ObjectInputStream o = new ObjectInputStream(b);
        return (Chessman) o.readObject();
    }
}
