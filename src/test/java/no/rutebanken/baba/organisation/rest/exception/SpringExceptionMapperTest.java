package no.rutebanken.baba.organisation.rest.exception;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.transaction.TransactionSystemException;

import javax.validation.ValidationException;
import javax.ws.rs.core.Response;

public class SpringExceptionMapperTest {


	@Test
	public void testMapValidationExceptionToBadRequest() {
		Response rsp = new SpringExceptionMapper().toResponse(new TransactionSystemException("", new ValidationException()));
		Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), rsp.getStatus());
	}

	@Test
	public void testMapUnknownExceptionToInternalServerError() {
		Response rsp = new SpringExceptionMapper().toResponse(new TransactionSystemException("", new RuntimeException()));
		Assert.assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), rsp.getStatus());
	}
}