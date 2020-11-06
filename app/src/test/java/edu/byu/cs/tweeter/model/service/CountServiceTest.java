package edu.byu.cs.tweeter.model.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.ServerFacadeMine;
import edu.byu.cs.tweeter.model.service.request.CountRequest;
import edu.byu.cs.tweeter.model.service.response.CountResponse;

public class CountServiceTest {

    private CountRequest validRequest;
    private CountRequest invalidRequest;
    private CountResponse successResponse;
    private CountResponse failureResponse;
    private CountService countServiceSpy;
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
        ServerFacadeMine mockServerFacade = Mockito.mock(ServerFacadeMine.class);
        Mockito.when(mockServerFacade.getCount(validRequest)).thenReturn(successResponse);

        failureResponse = new CountResponse("An exception occured");
        Mockito.when(mockServerFacade.getCount(invalidRequest)).thenReturn(failureResponse);

        // Create a CountService instance and wrap it with a spy that will use the mock service
        countServiceSpy = Mockito.spy(new CountService());
        Mockito.when(countServiceSpy.getServerFacade()).thenReturn(mockServerFacade);
    }

    @Test
    public void testGetCount_validRequest_correctResponse() throws IOException {
        CountResponse response = countServiceSpy.getCount(validRequest);
        Assertions.assertEquals(successResponse, response);
    }

    @Test
    public void testGetCount_invalidRequest_returnsNoCount() throws IOException {
        CountResponse response = countServiceSpy.getCount(invalidRequest);
        Assertions.assertEquals(failureResponse, response);
    }

}
