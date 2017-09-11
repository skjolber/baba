package no.rutebanken.baba.organisation.model.organisation;

import no.rutebanken.baba.organisation.model.CodeSpaceEntity;
import org.hibernate.annotations.*;
import org.springframework.util.CollectionUtils;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(uniqueConstraints = {
		                           @UniqueConstraint(name = "org_unique_id", columnNames = {"code_space_pk", "privateCode", "entityVersion"})
})
public abstract class Organisation extends CodeSpaceEntity {

	private Long companyNumber;

	@NotNull
	private String name;

	@OneToMany(cascade = CascadeType.ALL)
	private Set<OrganisationPart> parts;

	public Long getCompanyNumber() {
		return companyNumber;
	}

	public void setCompanyNumber(Long companyNumber) {
		this.companyNumber = companyNumber;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<OrganisationPart> getParts() {
		if (parts == null) {
			this.parts = new HashSet<>();
		}
		return parts;
	}

	public void setParts(Set<OrganisationPart> parts) {
		getParts().clear();
		getParts().addAll(parts);
	}


	public OrganisationPart getOrganisationPart(String id) {
		if (id != null && !CollectionUtils.isEmpty(parts)) {
			for (OrganisationPart existingPart : parts) {
				if (id.equals(existingPart.getId())) {
					return existingPart;
				}
			}
		}
		throw new IllegalArgumentException(getClass().getSimpleName() + " with id: " + id + " not found");
	}

}