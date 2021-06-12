package com.epam.esm.service.merger;

public interface Merger<Base, Merged>{
	Base merge(Base base, Merged merged);
}
