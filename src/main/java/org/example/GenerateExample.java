package org.example;


import org.example.classes.Person;
import org.example.generator.ObjectFactory;

public class GenerateExample {

    static void main() {
        ObjectFactory objectFactory = new ObjectFactory();
        try {
            Person generated = objectFactory.create(Person.class);
            System.out.println(generated);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}