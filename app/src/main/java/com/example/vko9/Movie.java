package com.example.vko9;

public class Movie {
    private String title;
    private String time;
    private int hrs;



    public Movie (String title, String time){
        String[] parts = time.split("T");
        String[] time_splitted = parts[1].split(":");

        hrs = Integer.parseInt(time_splitted[0] + time_splitted[1]);

        this.title = title;
        this.time = time_splitted[0] + ":" + time_splitted[1];
    }

    public String getTitle(){
        return title;
    }


    public String getTime(){
        return time;
    }

    public int getHrs(){
        return hrs;
    }


    @Override
    public String toString() {
        return title + " " + time;
    }

}