package com.mostafa.firebaseimagestorewithvalue;

public class Student {
    String course,duration, name,image;

    public Student(String course, String duration, String name,String image) {
        this.course = course;
        this.duration = duration;
        this.name = name;
        this.image = image;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
