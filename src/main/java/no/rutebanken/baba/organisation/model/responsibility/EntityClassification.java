package no.rutebanken.baba.organisation.model.responsibility;

import no.rutebanken.baba.organisation.model.CodeSpaceEntity;
import no.rutebanken.baba.organisation.model.TypeEntity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

@Entity
@Table(uniqueConstraints = {
                                   @UniqueConstraint(name = "entity_classification_unique_id",
                                           columnNames = {"code_space_pk", "privateCode", "entityVersion", "entity_type_pk"})
})
public class EntityClassification extends CodeSpaceEntity implements TypeEntity {

    public static final String ALL_TYPES = "*";

    @NotNull
    @ManyToOne
    private EntityType entityType;

    @NotNull
    private String name;

    public EntityType getEntityType() {
        return entityType;
    }

    public void setEntityType(EntityType entityType) {
        this.entityType = entityType;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getType() {
        return entityType.getPrivateCode();
    }

}