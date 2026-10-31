package org.example.classes;

import java.util.List;
import java.util.Map;

public class Company {

    private final Map<String, List<Product>> departmentProducts;

    public Company(Map<String, List<Product>> departmentProducts) {
        this.departmentProducts = departmentProducts;
    }

    public Map<String, List<Product>> getDepartmentProducts() {
        return departmentProducts;
    }

    @Override
    public String toString() {
        return "Company{" +
                "departmentProducts=" + departmentProducts +
                '}';
    }
}
