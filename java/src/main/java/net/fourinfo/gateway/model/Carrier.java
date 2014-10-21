package net.fourinfo.gateway.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * A SMS Message Carrier (a cell phone company).
 * 
 * @author Jason Thrasher
 */
public class Carrier {
	private static final long serialVersionUID = 3832626162173359411L;

	private Long id;

	private String name;

	public Carrier(Long id, String name) {
		this.id = id;
		this.name = name;
	}

	public Carrier() {
		// no-op
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return this.id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Carrier))
			return false;

		final Carrier c = (Carrier) o;

		return new EqualsBuilder().append(this.name, c.name).isEquals();
	}

	public int hashCode() {
		return new HashCodeBuilder(1923028325, -1034774645).append(this.name)
				.toHashCode();
	}

	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE).append(
				"id", this.id).append("name", this.name).toString();
	}

}
