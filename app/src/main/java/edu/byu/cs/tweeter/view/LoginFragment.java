package edu.byu.cs.tweeter.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import edu.byu.cs.tweeter.view.main.MainActivity;

/**
 * The fragment that displays on the 'Login' tab.
 */
public class LoginFragment extends Fragment implements LoginPresenter.View, LoginTask.Observer {

    private static final String LOG_TAG = "LoginFragment";
    private static final String USER_KEY = "UserKey";
    private static final String FOLLOW_KEY = "FollowKey";
    private static final String AUTH_TOKEN_KEY = "AuthTokenKey";

    private LoginPresenter presenter;
    private Toast loginToast;

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
        super.onCreate(savedInstanceState);
        presenter = new LoginPresenter(this);

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
                loginToast = null;
                if(isEmpty(userNameLogin) || isEmpty(passwordLogin)) {
                    loginToast = Toast.makeText(getActivity(), "First Name, Last Name, Username, and Password values can't be empty" , Toast.LENGTH_LONG);
                    loginToast.show();
                } else if (!hasAtSymbol(userNameLogin)) {
                    loginToast = Toast.makeText(getActivity(), "Username must have @ symbol" , Toast.LENGTH_LONG);
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

    /**
     * Checks to make sure the username is of the correct format
     * @param text The username
     * @return True if the format works, false if not.
     */
    private boolean hasAtSymbol(EditText text) {
        CharSequence str = text.getText().toString();
        char result = str.charAt(0);
        if (result == '@') {
            return true;
        }
        return false;
    }

    /**
     * Checks to make sure the username is registered in the system.
     * @param text The username
     * @return True if the user is registered, false if not.
     * TODO: Delete the hard coded value here after milestone 2.
     */
    private boolean isRegistered(EditText text) {
        CharSequence str = text.getText().toString();
        if (str.equals("@TestUser")) {
            return true;
        }
        return false;
    }


    /**
     * Checks to make sure the password is registered in the system.
     * @param text The password
     * @return True if the password is correct, false if not.
     * TODO: Delete the hard coded value here after milestone 2.
     */
    private boolean isPasswordCorrect(EditText text) {
        CharSequence str = text.getText().toString();
        if (str.equals("password")) {
            return true;
        }
        return false;
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

    /**
     * The callback method that gets invoked for a successful login. Displays the MainActivity.
     *
     * @param loginResponse the response from the login request.
     */
    @Override
    public void loginSuccessful(LoginResponse loginResponse) {
        Intent intent = new Intent(getActivity(), MainActivity.class);

        intent.putExtra(MainActivity.CURRENT_USER_KEY, loginResponse.getUser());
        intent.putExtra(MainActivity.CURRENT_FOLLOW_KEY, loginResponse.getUser());
        intent.putExtra(MainActivity.AUTH_TOKEN_KEY, loginResponse.getAuthToken());

        loginToast.cancel();
        startActivity(intent);
    }

    /**
     * The callback method that gets invoked for an unsuccessful login. Displays a toast with a
     * message indicating why the login failed.
     *
     * @param loginResponse the response from the register request.
     */
    @Override
    public void loginUnsuccessful(LoginResponse loginResponse) {
        Toast.makeText(getActivity(), "Failed to register. " + loginResponse.getMessage(), Toast.LENGTH_LONG).show();
    }

    /**
     * A callback indicating that an exception was thrown in an asynchronous method called on the
     * presenter.
     *
     * @param ex the exception.
     */
    @Override
    public void handleException(Exception ex) {
        Log.e(LOG_TAG, ex.getMessage(), ex);
        Toast.makeText(getActivity(), "Failed to register because of exception: " + ex.getMessage(), Toast.LENGTH_LONG).show();
    }
}
