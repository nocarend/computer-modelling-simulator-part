package ru.nsu.valikov.modelling.algorithm;

public interface EdgeIterationCallback<T, U> {
    void process(T from, T to, U content);
}
