package dev.pczigany.newisgood.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Pure calculation logic. Contains no framework annotations and no
 * dependencies; safe to instantiate with {@code new}.
 */
public final class CalculationService {

    public List<Long> calculateA(List<Integer> input) {
        long productOfNonZeroElements = 1;
        int zeroCount = 0;

        for (Integer number : input) {
            if (number == 0) {
                zeroCount++;
            } else {
                productOfNonZeroElements *= number;
            }
        }

        if (zeroCount > 1) {
            return new ArrayList<>(Collections.nCopies(input.size(), 0L));
        }

        List<Long> result = new ArrayList<>(input.size());

        for (Integer number : input) {
            if (zeroCount == 1) {
                result.add(number == 0 ? productOfNonZeroElements : 0L);
            } else {
                result.add(productOfNonZeroElements / number);
            }
        }

        return result;
    }

    public List<Long> calculateB(List<Integer> input) {
        List<Long> result = new ArrayList<>(input.size());

        for (int i = 0; i < input.size(); i++) {
            long product = 1;

            for (int j = 0; j < input.size(); j++) {
                if (i != j) {
                    product *= input.get(j);
                }
            }

            result.add(product);
        }

        return result;
    }

    public List<Long> calculateC(List<Integer> input) {
        int size = input.size();
        List<Long> result = new ArrayList<>(
                Collections.nCopies(size, 1L)
        );

        long prefixProduct = 1L;

        for (int i = 0; i < size; i++) {
            result.set(i, prefixProduct);
            prefixProduct *= input.get(i);
        }

        long suffixProduct = 1L;

        for (int i = size - 1; i >= 0; i--) {
            result.set(i, result.get(i) * suffixProduct);
            suffixProduct *= input.get(i);
        }

        return result;
    }
}
