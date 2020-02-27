package com.team5667;

public class timer implements Runnable {
    protected int time;
    public  timer(int maxTime){
       time=maxTime;
    }
    public int getTime() {
        return time;
    }
    public void setTime(int time){this.time=time;}
    @Override
    public void run() {

    }
}