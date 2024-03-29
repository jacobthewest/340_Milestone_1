package edu.byu.cs.tweeter.presenter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.service.RegisterService;
import edu.byu.cs.tweeter.model.service.request.RegisterRequest;
import edu.byu.cs.tweeter.model.service.response.RegisterResponse;
import edu.byu.cs.tweeter.view.util.ImageUtils;

public class RegisterPresenterTest {

    private RegisterRequest validRequest;
    private RegisterService mockRegisterService;
    private RegisterPresenter presenter;
    private RegisterResponse successResponse;
    private RegisterResponse failureResponse;
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

        // Setup a mock ServerFacade that will return known responses
        successResponse = new RegisterResponse(user, authToken);

        // Create a mock FollowersService
        mockRegisterService = Mockito.mock(RegisterService.class);

        // Wrap a FollowersPresenter in a spy that will use the mock service.
        presenter = Mockito.spy(new RegisterPresenter(new RegisterPresenter.View() {}));
        Mockito.when(presenter.getRegisterService()).thenReturn(mockRegisterService);
    }

    @Test
    public void testRegister_validRequest_correctResponse() throws IOException {
        Mockito.when(mockRegisterService.getRegister(validRequest)).thenReturn(successResponse);

        // Assert that the presenter returns the same response as the service (it doesn't do
        // anything else, so there's nothing else to test).
        Assertions.assertEquals(successResponse, presenter.getRegister(validRequest));
    }
}

