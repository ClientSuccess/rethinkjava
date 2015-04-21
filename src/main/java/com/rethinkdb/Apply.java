package com.rethinkdb;

interface Apply<T, R> {
    public R apply(T var);
}
