package com.demo.accountservice.service.impl;

import com.demo.accountservice.util.Count;
import com.demo.accountservice.service.CounterService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.IntStream;

@Service
public class CounterServiceImpl implements CounterService {

    @Override
    public Count count(List<Integer> notes, BigDecimal amount) {

        Collections.sort(notes, Collections.reverseOrder());
        int N = notes.size();
        int W = amount.intValue();
        int values[] = notes.stream().mapToInt(i -> i).toArray();

        Count[][] V = new Count[N + 1][W + 1];

        initializeFirstRow(N, V);
        initializeFirstColumn(W, V);

        IntStream.rangeClosed(1, N)
            .forEach(i -> IntStream.rangeClosed(1, W).forEach(w -> {
                if (values[i - 1] <= w) {
                    int value = values[i - 1];
                    Count previousCell = V[i - 1][w - value];
                    Count currentCell = V[i - 1][w];

                    if (value + previousCell.getSum() > currentCell.getSum()) {
                        Collections.addAll(previousCell.getValues(), value);
                        V[i][w] = new Count(value + previousCell.getSum(), previousCell.getValues());
                    } else {
                        V[i][w] = new Count(currentCell);
                    }

                } else {
                    V[i][w] = new Count(V[i - 1][w]);
                }
            }));

        Count count = V[N][W];

        List<Integer> selectedItems = count.getValues();
        Collection<Integer> remainingItems = CollectionUtils.disjunction(notes, selectedItems);

        //check if the remaining items contain an item of 5 so it could be broken down further
        if (!selectedItems.contains(5) && remainingItems.contains(5)) {

            //get min item to be further broken down
            Integer minSelectedItem = Collections.min(selectedItems);

            //collect the selected items excluding the smallest one which is broken down further
            Collection<Integer> remainingSelectedItems = CollectionUtils.disjunction(selectedItems, Arrays.asList(minSelectedItem));

            //solve the sub-problem recursively for the smallest item
            Count subCount = count(new ArrayList<>(remainingItems), new BigDecimal(minSelectedItem));

            //combine the initial selected items with the result from the sub-problem if it was resolved
            //keep the initial solution otherwise
            if (subCount.getSum() == minSelectedItem) {
                remainingSelectedItems.addAll(subCount.getValues());
            } else {
                remainingSelectedItems.add(minSelectedItem);
            }

            return new Count(count.getSum(), new ArrayList<>(remainingSelectedItems));
        }

        return count;
    }

    private static void initializeFirstRow(int n, Count[][] v) {
        for (int r = 0; r <= n; r++) {
            v[r][0] = new Count(0);
        }
    }

    private static void initializeFirstColumn(int w, Count[][] v) {
        for (int c = 0; c <= w; c++) {
            v[0][c] = new Count(0);
        }
    }
}