package edu.byu.cs.tweeter.client.model.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;

import edu.byu.cs.tweeter.shared.model.domain.User;
import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.shared.model.service.request.RetrieveUserRequest;
import edu.byu.cs.tweeter.shared.model.service.response.RetrieveUserResponse;

public class RetrieveUserServiceProxyTest {

    String username = "@TestUser";
    final String MALE_IMAGE_URL = "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/donald_duck.png";
    final User TestUser = new User("Test", "User", "@TestUser", MALE_IMAGE_URL, "password");
    private RetrieveUserRequest validRequest;
    private RetrieveUserRequest invalidRequestOne;
    private RetrieveUserRequest invalidRequestTwo;
    private RetrieveUserRequest invalidRequestThree;
    private RetrieveUserResponse successResponse;
    private RetrieveUserResponse failureResponse;
    private RetrieveUserServiceProxy retrieveUserServiceProxySpy;

    @BeforeEach
    public void setup() {
        ServerFacade mockServerFacade = Mockito.mock(ServerFacade.class);
        User currentUser = new User("Test", "User", MALE_IMAGE_URL, "password");

        // Setup request objects to use in the tests
        validRequest = new RetrieveUserRequest(username);
        invalidRequestOne = new RetrieveUserRequest("@NotRegistered"); // Username not registered
        invalidRequestTwo = new RetrieveUserRequest(null); // Username is null
        invalidRequestThree = new RetrieveUserRequest(""); // Username is null

        // Setup a mock ServerFacade that will return known responses
        successResponse = new RetrieveUserResponse(TestUser);
        Mockito.when(mockServerFacade.retrieveUser(validRequest)).thenReturn(successResponse);

        failureResponse = new RetrieveUserResponse("An exception occured");
        Mockito.when(mockServerFacade.retrieveUser(invalidRequestOne)).thenReturn(failureResponse);
        Mockito.when(mockServerFacade.retrieveUser(invalidRequestTwo)).thenReturn(failureResponse);
        Mockito.when(mockServerFacade.retrieveUser(invalidRequestThree)).thenReturn(failureResponse);

        // Create a RetrieveUserService instance and wrap it with a spy that will use the mock edu.byu.cs.server.edu.byu.cs.tweeter.server.edu.byu.cs.shared.edu.byu.cs.tweeter.shared.model.edu.byu.cs.tweeter.server.service
        retrieveUserServiceProxySpy = Mockito.spy(new RetrieveUserServiceProxy());
        Mockito.when(retrieveUserServiceProxySpy.getServerFacade()).thenReturn(mockServerFacade);
    }

    @Test
    public void testRetrieveUser_validRequest_correctResponse() throws IOException {
        RetrieveUserResponse response = retrieveUserServiceProxySpy.retrieveUser(validRequest);
        Assertions.assertEquals(successResponse, response);
    }

    @Test
    public void testRetrieveUser_invalidRequest_usernameNotRegistered() throws IOException {
        RetrieveUserResponse response = retrieveUserServiceProxySpy.retrieveUser(invalidRequestOne);
        Assertions.assertEquals(failureResponse, response);
    }

    @Test
    public void testRetrieveUser_invalidRequest_usernameIsNull() throws IOException {
        RetrieveUserResponse response = retrieveUserServiceProxySpy.retrieveUser(invalidRequestTwo);
        Assertions.assertEquals(failureResponse, response);
    }

    @Test
    public void testRetrieveUser_invalidRequest_usernameIsEmpty() throws IOException {
        RetrieveUserResponse response = retrieveUserServiceProxySpy.retrieveUser(invalidRequestThree);
        Assertions.assertEquals(failureResponse, response);
    }

}
