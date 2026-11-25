package com.prevaj.automation.cart;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The products a scenario expects in the shopping cart, with their quantities.
 */
public class Basket {

    private final Map<String, Integer> quantities = new LinkedHashMap<>();

    public void add(String productName) {
        quantities.merge(productName, 1, Integer::sum);
    }

    /**
     * Removes the whole cart line for the product, as the application does.
     */
    public void remove(String productName) {
        if (quantities.remove(productName) == null) {
            throw new IllegalArgumentException("'%s' is not in the expected basket %s".formatted(productName, quantities.keySet()));
        }
    }

    public List<String> productNames() {
        return List.copyOf(quantities.keySet());
    }

    /**
     * The expected quantity of each given product, in the same order. Products not in the basket get 0.
     */
    public List<Integer> quantitiesOf(List<String> productNames) {
        return productNames.stream().map(productName -> quantities.getOrDefault(productName, 0)).toList();
    }
}
