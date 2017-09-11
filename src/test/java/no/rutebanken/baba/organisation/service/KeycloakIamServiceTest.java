package no.rutebanken.baba.organisation.service;

import com.google.common.collect.Sets;
import no.rutebanken.baba.organisation.model.organisation.AdministrativeZone;
import no.rutebanken.baba.organisation.model.organisation.Authority;
import no.rutebanken.baba.organisation.model.organisation.Organisation;
import no.rutebanken.baba.organisation.model.responsibility.EntityClassification;
import no.rutebanken.baba.organisation.model.responsibility.EntityClassificationAssignment;
import no.rutebanken.baba.organisation.model.responsibility.EntityType;
import no.rutebanken.baba.organisation.model.responsibility.ResponsibilityRoleAssignment;
import no.rutebanken.baba.organisation.model.responsibility.Role;
import org.junit.Assert;
import org.junit.Test;
import org.rutebanken.helper.organisation.RoleAssignment;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class KeycloakIamServiceTest {


    @Test
    public void testMapResponsibilityRoleAssignmentToKeycloakRoleAssignment() {
        ResponsibilityRoleAssignment orgRegRoleAssignment = new ResponsibilityRoleAssignment();
        Role role = new Role();
        role.setPrivateCode("testRole");
        Organisation organisation = new Authority();
        organisation.setPrivateCode("testOrg");

        EntityType entityType = new EntityType();
        entityType.setPrivateCode("StopPlace");

        EntityClassification entityClassification = new EntityClassification();
        entityClassification.setPrivateCode("*");
        entityClassification.setEntityType(entityType);
        EntityClassificationAssignment entityClassificationAssignment = new EntityClassificationAssignment(entityClassification, orgRegRoleAssignment, true);
        orgRegRoleAssignment.getResponsibleEntityClassifications().add(entityClassificationAssignment);

        EntityClassification entityClassificationNegated = new EntityClassification();
        entityClassificationNegated.setPrivateCode("buss");
        entityClassificationNegated.setEntityType(entityType);
        EntityClassificationAssignment entityClassificationAssignmentNegated = new EntityClassificationAssignment(entityClassificationNegated, orgRegRoleAssignment, false);
        orgRegRoleAssignment.getResponsibleEntityClassifications().add(entityClassificationAssignmentNegated);


        orgRegRoleAssignment.setTypeOfResponsibilityRole(role);
        orgRegRoleAssignment.setResponsibleOrganisation(organisation);

        AdministrativeZone administrativeZone = new AdministrativeZone();
        administrativeZone.setSource("KVE");
        administrativeZone.setPrivateCode("05");
        orgRegRoleAssignment.setResponsibleArea(administrativeZone);

        RoleAssignment keycloakRoleAssignment = new KeycloakIamService().toRoleAssignment(orgRegRoleAssignment);

        Assert.assertEquals(role.getPrivateCode(), keycloakRoleAssignment.getRole());
        Assert.assertEquals(organisation.getPrivateCode(), keycloakRoleAssignment.getOrganisation());


        Set<String> expectedCodes = Sets.newHashSet(entityClassification.getPrivateCode(), "!" + entityClassificationNegated.getPrivateCode());
        List<String> classificationCodeList = keycloakRoleAssignment.getEntityClassifications().get(entityType.getPrivateCode());
        Assert.assertEquals(expectedCodes,
                new HashSet<>(classificationCodeList));

        Assert.assertEquals("KVE:TopographicPlace:05", keycloakRoleAssignment.getAdministrativeZone());
    }

    @Test
    public void testGeneratePassword() {
        KeycloakIamService iamService = new KeycloakIamService();

        String password = iamService.generatePassword();
        Assert.assertEquals(12, password.length());
        Assert.assertNotEquals(password.toLowerCase(), password);
        Assert.assertNotEquals(password.toUpperCase(), password);
    }


}