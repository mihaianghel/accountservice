package com.demo.accountservice.service.impl;

import com.demo.accountservice.util.Count;
import com.demo.accountservice.service.CounterService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

@Service
public class CounterServiceImpl implements CounterService {

    public Count count(List<Integer> notes, BigDecimal amount) {

        Collections.sort(notes, Collections.reverseOrder());
        int N = notes.size();
        int W = amount.intValue();
        int val[] = notes.stream().mapToInt(i -> i).toArray();

        Count[][] V = new Count[N + 1][W + 1];

        initializeFirstRow(N, V);
        initializeFirstColumn(W, V);

        IntStream.rangeClosed(1, N)
                .forEach(i -> IntStream.rangeClosed(1, W)
                                .forEach(w -> {

                            if (val[i - 1] <= w) {

                                int value = val[i - 1];
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
                        })
        );

        return V[N][W];
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