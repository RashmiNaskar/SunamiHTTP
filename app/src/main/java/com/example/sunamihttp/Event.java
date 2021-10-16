package com.example.sunamihttp;

public class Event {

    public final String title;
    public final String time;
    public final String alert;



            public Event(String a, String b, String c)
            {
                title = a;
                time = b;
                alert = c;
            }

    public String getAlert() {
        return alert;
    }

    public String getTime() {
        return time;
    }

    public String getTitle() {
        return title;
    }
}
