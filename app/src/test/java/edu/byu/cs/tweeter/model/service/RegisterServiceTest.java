package edu.byu.cs.tweeter.model.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.ServerFacadeMine;
import edu.byu.cs.tweeter.model.service.request.RegisterRequest;
import edu.byu.cs.tweeter.model.service.response.RegisterResponse;
import edu.byu.cs.tweeter.view.util.ImageUtils;

public class RegisterServiceTest {

    private RegisterRequest validRequest;
    private RegisterRequest invalidRequestOne;
    private RegisterRequest invalidRequestTwo;
    private RegisterResponse successResponse;
    private RegisterResponse failureResponse;
    private RegisterService registerServiceSpy;
    private static final String MALE_IMAGE_URL = "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/donald_duck.png";
    User TestUser = new User("Test", "User", "@TestUser", MALE_IMAGE_URL, "password");

    @BeforeEach
    public void setup() throws IOException {

        String username = "@TestUser";
        String password = "password";
        String firstName = "Test";
        String lastName = "User";
        String imageUrl = MALE_IMAGE_URL;
        byte[] imageBytes = null;
        AuthToken authToken = new AuthToken(username);
        User user = new User(firstName, lastName, username, imageUrl, imageBytes, password);

        try {
            InputStream iStream = new ByteArrayInputStream(imageUrl.getBytes());
            imageBytes = ImageUtils.byteArrayFromUri(iStream);
        } catch (Exception e) {
            e.printStackTrace();
        }


        // Setup request objects to use in the tests
        validRequest = new RegisterRequest(username, password, firstName, lastName, imageUrl, imageBytes);
        invalidRequestOne = new RegisterRequest(username, password, firstName, lastName, imageUrl, null);
            // Screw up the imageBytes
        invalidRequestTwo = new RegisterRequest("BadUsername", password, firstName, lastName, imageUrl, imageBytes);
            // Screw up the username

        // Setup a mock ServerFacade that will return known responses
        successResponse = new RegisterResponse(user, authToken);
        ServerFacadeMine mockServerFacade = Mockito.mock(ServerFacadeMine.class);
        Mockito.when(mockServerFacade.register(validRequest)).thenReturn(successResponse);

        failureResponse = new RegisterResponse("An exception occured");
        Mockito.when(mockServerFacade.register(invalidRequestOne)).thenReturn(failureResponse);
        Mockito.when(mockServerFacade.register(invalidRequestTwo)).thenReturn(failureResponse);

        // Create a RegisterService instance and wrap it with a spy that will use the mock service
        registerServiceSpy = Mockito.spy(new RegisterService());
        Mockito.when(registerServiceSpy.getServerFacade()).thenReturn(mockServerFacade);
    }

    @Test
    public void testRegister_validRequest_correctResponse() throws IOException {
        RegisterResponse response = registerServiceSpy.getRegister(validRequest);
        Assertions.assertEquals(successResponse, response);
    }

    @Test
    public void testRegister_invalidRequest_emptyImageBytes() throws IOException {
        RegisterResponse response = registerServiceSpy.getRegister(invalidRequestOne);
        Assertions.assertEquals(failureResponse, response);
    }

    @Test
    public void testRegister_invalidRequest_usernameDoesNotMatchWithAuthToken() throws IOException {
        RegisterResponse response = registerServiceSpy.getRegister(invalidRequestTwo);
        Assertions.assertEquals(failureResponse, response);
    }
}
