package edu.byu.cs.tweeter.shared.model.service;

import java.io.IOException;

import edu.byu.cs.tweeter.shared.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.shared.model.service.request.LoginRequest;
import edu.byu.cs.tweeter.shared.model.service.response.LoginResponse;

public interface LoginService {

    LoginResponse login(LoginRequest request) throws IOException, TweeterRemoteException;
}
