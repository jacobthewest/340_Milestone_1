package edu.byu.cs.tweeter.client.model.service;

import java.io.IOException;

import edu.byu.cs.tweeter.shared.model.domain.User;
import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.shared.model.service.request.RegisterRequest;
import edu.byu.cs.tweeter.shared.model.service.response.RegisterResponse;
import edu.byu.cs.tweeter.client.util.ByteArrayUtils;

/**
 * Contains the business logic to support the register operation.
 */
public class RegisterServiceProxy {

    public RegisterResponse getRegister(RegisterRequest request) throws IOException {
        ServerFacade serverFacade = getServerFacade();
        RegisterResponse registerResponse = serverFacade.register(request);

        if(registerResponse.isSuccess()) {
            loadImage(registerResponse.getUser());
        }

        return registerResponse;
    }

    /**
     * Loads the profile image data for the user.
     *
     * @param user the user whose profile image data is to be loaded.
     */
    private void loadImage(User user) throws IOException {
        byte[] preExistingBytes = user.getImageBytes();
        byte [] bytes;
        if(preExistingBytes != null) {
            preExistingBytes = user.getImageBytes();
            user.setImageBytes(preExistingBytes);
        } else {
            bytes = ByteArrayUtils.bytesFromUrl(user.getImageUrl());
            user.setImageBytes(bytes);
        }
    }

    /**
     * Returns an instance of {@link ServerFacade}. Allows mocking of the ServerFacade class for
     * testing purposes. All usages of ServerFacade should get their ServerFacade instance from this
     * method to allow for proper mocking.
     *
     * @return the instance.
     */
    public ServerFacade getServerFacade() {
        return new ServerFacade();
    }
}
