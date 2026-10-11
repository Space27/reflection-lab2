package org.example;


import org.example.classes.Company;
import org.example.generator.ObjectFactory;

public class GenerateExample {

    static void main() {
        ObjectFactory objectFactory = new ObjectFactory();
        try {
            Company generated = objectFactory.create(Company.class);
            System.out.println(generated);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}