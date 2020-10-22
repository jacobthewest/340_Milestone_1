package edu.byu.cs.tweeter.client.model.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;

import edu.byu.cs.tweeter.shared.model.domain.User;
import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.shared.model.service.request.CountRequest;
import edu.byu.cs.tweeter.shared.model.service.response.CountResponse;

public class CountServiceProxyTest {

    private CountRequest validRequest;
    private CountRequest invalidRequest;
    private CountResponse successResponse;
    private CountResponse failureResponse;
    private CountServiceProxy countServiceProxySpy;
    private int followingCount = 29;
    private int followersCount = 29;

    @BeforeEach
    public void setup() {
        User currentUser = new User("Test", "User", null, "password");

        // Setup request objects to use in the tests
        validRequest = new CountRequest(currentUser);
        invalidRequest = new CountRequest(null);

        // Setup a mock ServerFacade that will return known responses
        successResponse = new CountResponse(currentUser, followingCount, followersCount );
        ServerFacade mockServerFacade = Mockito.mock(ServerFacade.class);
        Mockito.when(mockServerFacade.getCount(validRequest)).thenReturn(successResponse);

        failureResponse = new CountResponse("An exception occured");
        Mockito.when(mockServerFacade.getCount(invalidRequest)).thenReturn(failureResponse);

        // Create a CountService instance and wrap it with a spy that will use the mock edu.byu.cs.server.edu.byu.cs.tweeter.server.edu.byu.cs.shared.edu.byu.cs.tweeter.shared.model.edu.byu.cs.tweeter.server.service
        countServiceProxySpy = Mockito.spy(new CountServiceProxy());
        Mockito.when(countServiceProxySpy.getServerFacade()).thenReturn(mockServerFacade);
    }

    @Test
    public void testGetCount_validRequest_correctResponse() throws IOException {
        CountResponse response = countServiceProxySpy.getCount(validRequest);
        Assertions.assertEquals(successResponse, response);
    }

    @Test
    public void testGetCount_invalidRequest_returnsNoCount() throws IOException {
        CountResponse response = countServiceProxySpy.getCount(invalidRequest);
        Assertions.assertEquals(failureResponse, response);
    }

}
