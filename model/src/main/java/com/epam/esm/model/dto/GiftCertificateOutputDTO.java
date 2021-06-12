package com.epam.esm.model.dto;

import com.epam.esm.model.entity.GiftCertificate;
import lombok.Value;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * DTO object encapsulating information about Gift Certificates. Used for Web Layer <- Service layer communication
 */
@Value
public class GiftCertificateOutputDTO {
	Integer id;
	String name;
	String description;
	Double price;
	Integer duration;
	Set<TagDTO> tags;
}
