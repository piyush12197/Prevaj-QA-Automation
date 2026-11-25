package com.prevaj.automation.cart;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public final class PriceCalculator {

    private static final int MONEY_SCALE = 2;

    private PriceCalculator() {
    }

    /**
     * Sum of price multiplied by quantity for each line. Prices and quantities are matched by position.
     */
    public static BigDecimal itemTotal(List<BigDecimal> prices, List<Integer> quantities) {
        if (prices.size() != quantities.size()) {
            throw new IllegalArgumentException("Expected one quantity per price, but got %d prices and %d quantities"
                    .formatted(prices.size(), quantities.size()));
        }
        BigDecimal itemTotal = BigDecimal.ZERO;
        for (int i = 0; i < prices.size(); i++) {
            itemTotal = itemTotal.add(prices.get(i).multiply(BigDecimal.valueOf(quantities.get(i))));
        }
        return itemTotal;
    }

    /**
     * The tax on an amount at a whole-number percentage rate, rounded half up to cents.
     */
    public static BigDecimal tax(BigDecimal amount, int ratePercent) {
        return amount.multiply(BigDecimal.valueOf(ratePercent).movePointLeft(2))
                .setScale(MONEY_SCALE, RoundingMode.HALF_UP);
    }
}
