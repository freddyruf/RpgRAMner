package it.unicam.cs.mpgc.rpg130077.model;

public class RandomNumbersGenerator {
    public int getInt(){
        return (int) Math.random();
    }
    public double getDouble(){
        return Math.random();
    }
    public int getInt(int min, int max){
        return getInt() * (max - min + 1) + min;
    }
    public double getDouble(int min, int max){
        return getDouble() * (max - min + 1) + min;
    }
}
