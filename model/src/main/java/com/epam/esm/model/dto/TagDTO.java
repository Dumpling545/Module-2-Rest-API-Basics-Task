package com.epam.esm.model.dto;

import com.epam.esm.model.entity.Tag;
import lombok.Value;

import java.io.Serial;

/**
 * DTO object encapsulating information about Tag. Used for Web Layer <-> Service layer communication
 */
@Value
public class TagDTO {
	Integer id;
	String name;
	public TagDTO(String name){
		this.name = name;
		id = null;
	}
}
