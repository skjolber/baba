/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 *
 */

package no.rutebanken.baba.organisation.rest;

import com.google.common.collect.Sets;

import no.rutebanken.baba.organisation.TestConstantsOrganisation;
import no.rutebanken.baba.organisation.model.user.NotificationType;
import no.rutebanken.baba.organisation.model.user.eventfilter.JobState;
import no.rutebanken.baba.organisation.repository.BaseIntegrationTest;
import no.rutebanken.baba.organisation.rest.dto.user.ContactDetailsDTO;
import no.rutebanken.baba.organisation.rest.dto.user.EventFilterDTO;
import no.rutebanken.baba.organisation.rest.dto.user.NotificationConfigDTO;
import no.rutebanken.baba.organisation.rest.dto.user.UserDTO;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;

import java.net.URI;
import java.util.Arrays;
import java.util.Set;


public class UserResourceIntegrationTest extends BaseIntegrationTest {
    @Autowired
    private TestRestTemplate restTemplate;

    private static final String PATH = "/services/organisations/users";


    @Test
    public void userNotFound() throws Exception {
        ResponseEntity<UserDTO> entity = restTemplate.getForEntity(PATH + "/unknownUser",
                UserDTO.class);
        Assert.assertEquals(HttpStatus.NOT_FOUND, entity.getStatusCode());
    }


    @Test
    public void crudUser() throws Exception {
        ContactDetailsDTO createContactDetails = new ContactDetailsDTO("first", "last", "phone", "email@email.com");
        UserDTO createUser = createUser("userName", TestConstantsOrganisation.ORGANISATION_ID, createContactDetails);
        ResponseEntity<String> createResponse = restTemplate.postForEntity(PATH, createUser, String.class);
        Assert.assertNotNull(createResponse.getBody());
        URI uri = createResponse.getHeaders().getLocation();
        assertUser(createUser, uri);

        ContactDetailsDTO updateContactDetails = new ContactDetailsDTO("otherFirst", "otherLast", null, "other@email.org");
        UserDTO updateUser = createUser(createUser.username, createUser.organisationRef, updateContactDetails);
        restTemplate.put(uri, updateUser);
        assertUser(updateUser, uri);

        UserDTO[] allUsers =
                restTemplate.getForObject(PATH, UserDTO[].class);
        assertUserInArray(updateUser, allUsers);

        UserDTO[] allUsersWithFullDetails =
                restTemplate.getForObject(PATH + "?full=true", UserDTO[].class);
        assertUserInArray(updateUser, allUsersWithFullDetails);
        Assert.assertNotNull(allUsersWithFullDetails[0].organisation.name);


        ResponseEntity<String> resetPasswordResponse = restTemplate.postForEntity(uri.getPath() + "/resetPassword", createUser, String.class);
        Assert.assertNotNull(resetPasswordResponse.getBody());
        Assert.assertNotEquals(resetPasswordResponse.getBody(), createResponse.getBody());

        restTemplate.delete(uri);

        ResponseEntity<UserDTO> entity = restTemplate.getForEntity(uri,
                UserDTO.class);
        Assert.assertEquals(HttpStatus.NOT_FOUND, entity.getStatusCode());
    }

    @Test
    public void updateUserWithNotificationConfigurations() {
        ContactDetailsDTO createContactDetails = new ContactDetailsDTO("first", "last", "phone", "email@email.com");
        UserDTO user = createUser("userWithNotificationConfig", TestConstantsOrganisation.ORGANISATION_ID, createContactDetails);
        URI uri = restTemplate.postForLocation(PATH, user);
        assertUser(user, uri);

        Set<NotificationConfigDTO> config = Sets.newHashSet(new NotificationConfigDTO(NotificationType.WEB, false, jobEventFilter("action", JobState.FAILED)));
        ResourceTestUtils.setNotificationConfig(restTemplate, user.username, config);

        user.contactDetails.firstName = "changeFirstName";
        restTemplate.put(uri, user);
        assertUser(user, uri);
    }

    @Test
    public void updateUsersResponsibilitySets() throws Exception {
        ContactDetailsDTO contactDetails = new ContactDetailsDTO("first", "last", "phone", "email@email.com");
        UserDTO user = createUser("userName", TestConstantsOrganisation.ORGANISATION_ID, contactDetails);
        URI uri = restTemplate.postForLocation(PATH, user);
        assertUser(user, uri);

        user.responsibilitySetRefs = Arrays.asList(TestConstantsOrganisation.RESPONSIBILITY_SET_ID);
        restTemplate.put(uri, user);
        assertUser(user, uri);

        user.responsibilitySetRefs = Arrays.asList(TestConstantsOrganisation.RESPONSIBILITY_SET_ID, TestConstantsOrganisation.RESPONSIBILITY_SET_ID_2);
        restTemplate.put(uri, user);
        assertUser(user, uri);

        user.responsibilitySetRefs = Arrays.asList(TestConstantsOrganisation.RESPONSIBILITY_SET_ID_2);
        restTemplate.put(uri, user);
        assertUser(user, uri);

        user.responsibilitySetRefs = null;
        restTemplate.put(uri, user);
        assertUser(user, uri);
    }

    private void assertUserInArray(UserDTO user, UserDTO[] array) {
        Assert.assertNotNull(array);
        Assert.assertTrue(Arrays.stream(array).anyMatch(r -> r.username.equals(user.username.toLowerCase())));
    }

    protected UserDTO createUser(String username, String orgRef, ContactDetailsDTO contactDetails, String... respSetRefs) {
        UserDTO user = new UserDTO();
        user.username = username;
        user.organisationRef = orgRef;
        user.contactDetails = contactDetails;
        if (respSetRefs != null) {
            user.responsibilitySetRefs = Arrays.asList(respSetRefs);
        }

        return user;
    }


    protected void assertUser(UserDTO inUser, URI uri) {
        Assert.assertNotNull(uri);
        ResponseEntity<UserDTO> rsp = restTemplate.getForEntity(uri, UserDTO.class);
        UserDTO outUser = rsp.getBody();

        assertUserBasics(inUser, outUser);
        Assert.assertNull(outUser.organisation);
        Assert.assertNull(outUser.responsibilitySets);


        ResponseEntity<UserDTO> fullRsp = restTemplate.getForEntity(uri.toString() + "?full=true", UserDTO.class);
        UserDTO fullOutUser = fullRsp.getBody();
        assertUserBasics(inUser, fullOutUser);
        Assert.assertNotNull(fullOutUser.organisation.name);
        Assert.assertEquals(inUser.responsibilitySetRefs == null ? 0 : inUser.responsibilitySetRefs.size(), fullOutUser.responsibilitySets.size());
        Assert.assertTrue(fullOutUser.responsibilitySets.stream().allMatch(rs -> rs.name != null));
    }

    private void assertUserBasics(UserDTO inUser, UserDTO outUser) {

        Assert.assertEquals(inUser.username.toLowerCase(), outUser.username);
        Assert.assertEquals(inUser.privateCode, outUser.privateCode);

        if (CollectionUtils.isEmpty(inUser.responsibilitySetRefs)) {
            Assert.assertTrue(CollectionUtils.isEmpty(outUser.responsibilitySetRefs));
        } else {
            Assert.assertEquals(inUser.responsibilitySetRefs.size(), outUser.responsibilitySetRefs.size());
            Assert.assertTrue(inUser.responsibilitySetRefs.containsAll(outUser.responsibilitySetRefs));
        }

        if (inUser.contactDetails == null) {
            Assert.assertNull(outUser.contactDetails);
        } else {
            Assert.assertEquals(inUser.contactDetails.firstName, outUser.contactDetails.firstName);
            Assert.assertEquals(inUser.contactDetails.lastName, outUser.contactDetails.lastName);
            Assert.assertEquals(inUser.contactDetails.email, outUser.contactDetails.email);
            Assert.assertEquals(inUser.contactDetails.phone, outUser.contactDetails.phone);
        }

    }

    @Test
    public void createInvalidUser() throws Exception {
        UserDTO inUser = createUser("user name", "privateCode", null);
        ResponseEntity<String> rsp = restTemplate.postForEntity(PATH, inUser, String.class);

        Assert.assertEquals(HttpStatus.BAD_REQUEST, rsp.getStatusCode());
    }


    private EventFilterDTO jobEventFilter(String action, JobState jobState) {
        EventFilterDTO eventFilterDTO = new EventFilterDTO(EventFilterDTO.EventFilterType.JOB);
        eventFilterDTO.actions = Sets.newHashSet(action);
        eventFilterDTO.jobDomain = EventFilterDTO.JobDomain.TIMETABLE;
        eventFilterDTO.states = Sets.newHashSet(jobState);
        return eventFilterDTO;
    }
}
