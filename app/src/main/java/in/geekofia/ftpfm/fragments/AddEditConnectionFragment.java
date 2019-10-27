package in.geekofia.ftpfm.fragments;

import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import in.geekofia.ftpfm.R;

import static in.geekofia.ftpfm.activities.MainActivity.EXTRA_HOST;
import static in.geekofia.ftpfm.activities.MainActivity.EXTRA_ID;
import static in.geekofia.ftpfm.activities.MainActivity.EXTRA_NAME;
import static in.geekofia.ftpfm.activities.MainActivity.EXTRA_PASSWORD;
import static in.geekofia.ftpfm.activities.MainActivity.EXTRA_PORT;
import static in.geekofia.ftpfm.activities.MainActivity.EXTRA_TITLE;
import static in.geekofia.ftpfm.activities.MainActivity.EXTRA_USER_NAME;

public class AddEditConnectionFragment extends Fragment {

    private Toolbar mToolBar;
    private TextInputLayout mHostLayout, mPortLayout, mUsernameLayout, mPasswordLayout;
    private TextInputEditText mName, mHost, mPort, mUsername, mPassword;
    private RadioGroup mRadioConnectionTypeGroup;
    private Bundle mBundle;
    private int id;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_connection, container, false);

        setHasOptionsMenu(true);

        initViews(view);

        mBundle = this.getArguments();
        if (mBundle != null) {
            getActivity().setTitle(mBundle.getString(EXTRA_TITLE));
            id = mBundle.getInt(EXTRA_ID);
            mName.setText(mBundle.getString(EXTRA_NAME));
            mHost.setText(mBundle.getString(EXTRA_HOST));
            mPort.setText(String.valueOf(mBundle.getInt(EXTRA_PORT)));

            if (mBundle.getString(EXTRA_USER_NAME).isEmpty() && mBundle.getString(EXTRA_PASSWORD).isEmpty()){
                mRadioConnectionTypeGroup.check(R.id.connection_anonymous);
            } else {
                mUsername.setText(mBundle.getString(EXTRA_USER_NAME));
                mPassword.setText(mBundle.getString(EXTRA_PASSWORD));
            }
        } else {
            getActivity().setTitle("New Connection");
            id = -1;
        }

        return view;
    }

    private void initViews(View view) {

        // Listen click on back arrow
        mToolBar = getActivity().findViewById(R.id.toolbar);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectionsFragment connectionsFragment = new ConnectionsFragment();
                if (getActivity().getSupportFragmentManager() != null) {
                    getActivity().getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, connectionsFragment, "CONNECTION_FRAGMENT").commit();
                }
            }
        });

        // Real views
        mHostLayout = view.getRootView().findViewById(R.id.host_layout);
        mPortLayout = view.getRootView().findViewById(R.id.port_layout);
        mUsernameLayout = view.getRootView().findViewById(R.id.username_layout);
        mPasswordLayout = view.getRootView().findViewById(R.id.password_layout);
        mRadioConnectionTypeGroup = view.getRootView().findViewById(R.id.radio_connection_type);

        mName = view.getRootView().findViewById(R.id.id_edit_name);
        mHost = view.getRootView().findViewById(R.id.id_edit_host);
        mPort = view.getRootView().findViewById(R.id.id_edit_port);
        mUsername = view.getRootView().findViewById(R.id.id_edit_username);
        mPassword = view.getRootView().findViewById(R.id.id_edit_password);

        mPort.setInputType(InputType.TYPE_CLASS_NUMBER);
        mUsername.setInputType(InputType.TYPE_CLASS_TEXT);

        mRadioConnectionTypeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                if (checkedId != R.id.connection_registered) {
                    mUsernameLayout.setVisibility(View.GONE);
                    mPasswordLayout.setVisibility(View.GONE);
                    mUsername.setText("");
                    mPassword.setText("");
                } else {
                    mUsernameLayout.setVisibility(View.VISIBLE);
                    mPasswordLayout.setVisibility(View.VISIBLE);
                }
            }
        });
    }


    private void saveProfile() {
        String name = mName.getText().toString();
        String host = mHost.getText().toString();
        int port = 21;
        String userName = mUsername.getText().toString();
        String password = mPassword.getText().toString();

        if (name.trim().isEmpty())
            name = "New Connection " + (int)(Math.random() * 999.0);

        if (host.trim().isEmpty()) {
            Toast.makeText(getContext(), "Host address can't be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!(mPort.getText().toString().trim().isEmpty()))
            port = Integer.parseInt(mPort.getText().toString());

        // TODO: A new viewmodel
        // Profile newProfile = new Profile(name, host, port, userName, password);

        ConnectionsFragment connectionsFragment = new ConnectionsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_NAME, name);
        bundle.putString(EXTRA_HOST, host);
        bundle.putInt(EXTRA_PORT, port);
        bundle.putString(EXTRA_USER_NAME, userName);
        bundle.putString(EXTRA_PASSWORD, password);

        // bundle presents if id != -1. Which means it is updating. So id is required.
        if (id != -1 ){
            bundle.putInt(EXTRA_ID, id);
        }

        connectionsFragment.setArguments(bundle);
        if (getActivity().getSupportFragmentManager() != null) {
            getActivity().getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, connectionsFragment, "CONNECTION_FRAGMENT").commit();
        }

    }

//    @Override
//    public void onPrepareOptionsMenu(Menu menu) {
//        MenuItem item = menu.findItem(R.id.toolbar_info);
//        if(item != null)
//            item.setVisible(false);
//    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_edit_connection, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toolbar_save:
                saveProfile();
                return true;
            default:
                return false;
        }
    }
}