package com.epam.esm.model.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Entity object encapsulating information about Gift Certificate. Used for
 * Service Layer <-> Repository layer communication
 */
public class GiftCertificate implements Serializable {
	@Serial
	private static final long serialVersionUID = 5241233617393280481L;
	private int id = -1;
	private String name;
	private String description;
	private double price = -1;
	private int duration = -1;
	private LocalDateTime createDate;
	private LocalDateTime lastUpdateDate;

	public GiftCertificate() {
	}

	public GiftCertificate(int id, String name, String description,
	                       double price,
	                       int duration, LocalDateTime createDate,
	                       LocalDateTime lastUpdateDate)
	{
		this.id = id;
		this.name = name;
		this.description = description;
		this.price = price;
		this.duration = duration;
		this.createDate = createDate;
		this.lastUpdateDate = lastUpdateDate;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public LocalDateTime getCreateDate() {
		return createDate;
	}

	public void setCreateDate(LocalDateTime createDate) {
		this.createDate = createDate;
	}

	public LocalDateTime getLastUpdateDate() {
		return lastUpdateDate;
	}

	public void setLastUpdateDate(LocalDateTime lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		GiftCertificate that = (GiftCertificate) o;

		if (id != that.id) {
			return false;
		}
		if (Double.compare(that.price, price) != 0) {
			return false;
		}
		if (duration != that.duration) {
			return false;
		}
		if (name != null ? !name.equals(that.name) : that.name != null) {
			return false;
		}
		if (description != null ? !description.equals(that.description) :
				that.description != null)
		{
			return false;
		}
		if (createDate != null ? !createDate.equals(that.createDate) :
				that.createDate != null)
		{
			return false;
		}
		return lastUpdateDate != null ?
				lastUpdateDate.equals(that.lastUpdateDate) :
				that.lastUpdateDate == null;
	}

	@Override
	public int hashCode() {
		int result;
		long temp;
		result = id;
		result = 31 * result + (name != null ? name.hashCode() : 0);
		result = 31 * result +
				(description != null ? description.hashCode() : 0);
		temp = Double.doubleToLongBits(price);
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		result = 31 * result + duration;
		result = 31 * result + (createDate != null ? createDate.hashCode() : 0);
		result = 31 * result +
				(lastUpdateDate != null ? lastUpdateDate.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "GiftCertificate{" +
				"id=" + id +
				", name='" + name + '\'' +
				", description='" + description + '\'' +
				", price=" + price +
				", duration=" + duration +
				", createDate=" + createDate +
				", lastUpdateDate=" + lastUpdateDate +
				'}';
	}
}
