package org.example.classes;

public class Person {

    private String firstName;
    private String lastName;
    private int age;

    public Person(String lastName, String firstName, int age) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.age = age;
    }

    public Person() {
        this("", "", 0);
    }
}
