package com.epam.esm.db.helper;

/**
 * Helper functional interface which represents Consumer with three parameters
 */
@FunctionalInterface
public interface TriConsumer<T1, T2, T3> {
	void accept(T1 t1, T2 t2, T3 t3);
}
