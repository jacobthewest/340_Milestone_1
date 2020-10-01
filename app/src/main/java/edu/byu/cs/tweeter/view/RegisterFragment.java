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
import edu.byu.cs.tweeter.model.service.request.RegisterRequest;
import edu.byu.cs.tweeter.model.service.response.RegisterResponse;
import edu.byu.cs.tweeter.presenter.RegisterPresenter;
import edu.byu.cs.tweeter.view.asyncTasks.RegisterTask;

/**
 * The fragment that displays on the 'Register' tab.
 */
public class RegisterFragment extends Fragment implements RegisterPresenter.View, RegisterTask.Observer {

    private static final String LOG_TAG = "RegisterFragment";
    private static final String USER_KEY = "UserKey";
    private static final String AUTH_TOKEN_KEY = "AuthTokenKey";

    private User user;
    private String imageUrl;
    private AuthToken authToken;
    private RegisterPresenter presenter;

    /**
     * Creates an instance of the fragment and places the user and auth token in an arguments
     * bundle assigned to the fragment.
     *
     * @return the fragment.
     */
    public static RegisterFragment newInstance() {
        RegisterFragment fragment = new RegisterFragment();

        Bundle args = new Bundle(0);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        Button registerButton = view.findViewById(R.id.RegisterButton);
        EditText firstName = view.findViewById(R.id.firstName);
        EditText lastName = view.findViewById(R.id.lastName);
        EditText userNameRegister = view.findViewById(R.id.userNameRegister);
        EditText passwordRegister = view.findViewById(R.id.passwordRegister);

        registerButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Makes a Register request. The user is hard-coded, so it doesn't matter what data we put
             * in the RegisterRequest object.
             *
             * @param view the view object that was clicked.
             */
            @Override
            public void onClick(View view) {
                String toastText = "";
                Toast registerToast = null;
                if(isEmpty(userNameRegister) || isEmpty(passwordRegister) || isEmpty(firstName) || isEmpty(lastName)) {
                    registerToast = Toast.makeText(getActivity(), "First Name, Last Name, Username, and Password values can't be empty" , Toast.LENGTH_LONG);
                    registerToast.show();
                } else {
                    registerToast = Toast.makeText(getActivity(), "Registering User", Toast.LENGTH_LONG);
                    registerToast.show();

                    RegisterRequest registerRequest = getRegisterRequest(userNameRegister, passwordRegister, firstName, lastName);
                    RegisterTask registerTask = new RegisterTask(presenter, getObserver());

                    registerTask.execute(registerRequest);
                }
            }
        });
        return view;
    }

    private RegisterTask.Observer getObserver() {
        return this;
    }

    private RegisterRequest getRegisterRequest(EditText userName, EditText password, EditText first, EditText last) {
        String userNameString = editTextToString(userName);
        String passwordString = editTextToString(password);
        String firstNameString = editTextToString(first);
        String lastNameString = editTextToString(last);
        return new RegisterRequest(userNameString, passwordString, firstNameString, lastNameString, imageUrl);
    }

    private boolean isEmpty(EditText text) {
        CharSequence str = text.getText().toString();
        return TextUtils.isEmpty(str);
    }

    private String editTextToString(EditText text) {
        return text.getText().toString();
    }

    @Override
    public void registerSuccessful(RegisterResponse registerResponse) {

    }

    @Override
    public void registerUnsuccessful(RegisterResponse registerResponse) {

    }

    @Override
    public void handleException(Exception ex) {

    }
}

