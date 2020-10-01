package edu.byu.cs.tweeter.view;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import edu.byu.cs.tweeter.R;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.service.request.LoginRequest;
import edu.byu.cs.tweeter.model.service.response.LoginResponse;
import edu.byu.cs.tweeter.presenter.LoginPresenter;
import edu.byu.cs.tweeter.presenter.RegisterPresenter;
import edu.byu.cs.tweeter.view.asyncTasks.LoginTask;
import edu.byu.cs.tweeter.view.asyncTasks.RegisterTask;

/**
 * The fragment that displays on the 'Login' tab.
 */
public class LoginFragment extends Fragment implements LoginPresenter.View, LoginTask.Observer {

    private static final String LOG_TAG = "LoginFragment";
    private static final String USER_KEY = "UserKey";
    private static final String AUTH_TOKEN_KEY = "AuthTokenKey";

    private User user;
    private AuthToken authToken;
    private LoginPresenter presenter;

    /**
     * Creates an instance of the fragment and places the user and auth token in an arguments
     * bundle assigned to the fragment.
     *
     * @return the fragment.
     */
    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();

        Bundle args = new Bundle(0);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        Button loginButton = view.findViewById(R.id.LoginButton);
        EditText userNameLogin = view.findViewById(R.id.userNameLogin);
        EditText passwordLogin = view.findViewById(R.id.passwordLogin);

        loginButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Makes a Login request. The user is hard-coded, so it doesn't matter what data we put
             * in the LoginRequest object.
             *
             * @param view the view object that was clicked.
             */
            @Override
            public void onClick(View view) {
                String toastText = "";
                Toast loginToast = null;
                if(isEmpty(userNameLogin) || isEmpty(passwordLogin)) {
                    loginToast = Toast.makeText(getActivity(), "First Name, Last Name, Username, and Password values can't be empty" , Toast.LENGTH_LONG);
                    loginToast.show();
                } else {
                    loginToast = Toast.makeText(getActivity(), "Logging in User", Toast.LENGTH_LONG);
                    loginToast.show();

                    LoginRequest loginRequest = getLoginRequest(userNameLogin, passwordLogin);
                    LoginTask loginTask = new LoginTask(presenter, getObserver());

                    loginTask.execute(loginRequest);
                }
            }
        });
        return view;
    }

    private LoginTask.Observer getObserver() {
        return this;
    }

    private LoginRequest getLoginRequest(EditText userName, EditText password) {
        String userNameString = editTextToString(userName);
        String passwordString = editTextToString(password);

        return new LoginRequest(userNameString, passwordString);
    }

    private boolean isEmpty(EditText text) {
        CharSequence str = text.getText().toString();
        return TextUtils.isEmpty(str);
    }

    private String editTextToString(EditText text) {
        return text.getText().toString();
    }

    @Override
    public void loginSuccessful(LoginResponse loginResponse) {

    }

    @Override
    public void loginUnsuccessful(LoginResponse loginResponse) {

    }

    @Override
    public void handleException(Exception ex) {

    }
}
