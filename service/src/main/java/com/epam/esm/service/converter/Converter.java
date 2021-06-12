package com.epam.esm.service.converter;

public interface Converter <Source, Destination>{
	Destination convert(Source source);
}
