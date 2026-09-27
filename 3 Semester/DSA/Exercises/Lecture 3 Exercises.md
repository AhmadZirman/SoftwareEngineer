
# Exercise 1: Warm-up, Picking the Right Type
For each attribute description below, pick the PostgreSQL data type that best fits it, from: NUMERIC(p, s), BOOLEAN, TEXT, TIMESTAMPZ, BIGINT. Briefly justify your choice.

**Q1**: A Product's price, which must be stored exactly, in whole cents (no rounding errors), with up to two decimal places.

**A1**: NUMERIC(10, 2), 10 total numbers allowed with number of digits allowed after decimal point, 2 digits. 