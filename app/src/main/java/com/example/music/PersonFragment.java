package com.example.music;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Locale;

public class PersonFragment extends Fragment {
    Button btn_logOut, btn_changeLanguage;
    private Button btnChangePassword;

    private EditText etOldPassword, etNewPassword1, etNewPassword2;
    private boolean isChangePasswordVisible = false;
    SharedPreferences p_lang;
    TextView tv_username_avatar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_person, container, false);
        anhXa(view);
        LocaleManager.setLocale(getContext());
        SharedPreferences prefs = getActivity().getSharedPreferences("prefs",android.content.Context.MODE_PRIVATE);
        Integer userId = prefs.getInt("userId", -1);
        String username = prefs.getString("username","");

        if (userId != -1) {
            btn_logOut.setText(getString(R.string.logout));
            btnChangePassword.setVisibility(View.VISIBLE);
            tv_username_avatar.setText(username);
        } else {
            btn_logOut.setText(getString(R.string.signin));
            btnChangePassword.setVisibility(View.GONE);
            tv_username_avatar.setText("MUSIC PLAYER");
        }


        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isChangePasswordVisible) {
                    etOldPassword.setVisibility(View.VISIBLE);
                    etNewPassword1.setVisibility(View.VISIBLE);
                    etNewPassword2.setVisibility(View.VISIBLE);
                    isChangePasswordVisible = true;
                } else {

                    String oldPassword = etOldPassword.getText().toString();
                    String newPassword1 = etNewPassword1.getText().toString();
                    String newPassword2 = etNewPassword2.getText().toString();
                    String pass = prefs.getString("password", "");
                    if (!newPassword1.isEmpty() || !newPassword2.isEmpty() || !oldPassword.isEmpty()) {
                    if (!newPassword1.equals(newPassword2) || !oldPassword.equals(pass)) {
                        Toast.makeText(getActivity(), getString(R.string.passNotMatch), Toast.LENGTH_SHORT).show();
                    } else {
                        DatabaseHelper db = new DatabaseHelper(getActivity());
                        db.changePassword(userId, newPassword1);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("password",newPassword1);
                        editor.apply();
                        Toast.makeText(getActivity(), getString(R.string.changPassSuccess), Toast.LENGTH_SHORT).show();
                        etOldPassword.setText("");
                        etNewPassword1.setText("");
                        etNewPassword2.setText("");
                        etOldPassword.setVisibility(View.GONE);
                        etNewPassword1.setVisibility(View.GONE);
                        etNewPassword2.setVisibility(View.GONE);
                        isChangePasswordVisible = false;
                    }
                } else {
                        Toast.makeText(getActivity(), getString(R.string.typePrompt), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        btn_logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userId != -1) {
                    logOut();
                } else {
                    Intent intent = new Intent(getActivity(), SignInActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }
            }
        });

        btn_changeLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangeLanguageDialog();
            }
        });


        return view;
    }

    private void anhXa(View view) {
        btn_logOut = view.findViewById(R.id.btn_logOut);
        btnChangePassword = view.findViewById(R.id.btn_changePassword);
        etOldPassword = view.findViewById(R.id.et_oldPassword);
        etNewPassword1 = view.findViewById(R.id.et_newPassword1);
        etNewPassword2 = view.findViewById(R.id.et_newPassword2);
        btn_changeLanguage = (Button) view.findViewById(R.id.btn_changeLanguage);
        tv_username_avatar = (TextView) view.findViewById(R.id.tv_username_avatar);
    }

    private void logOut() {
        SharedPreferences prefs = getActivity().getSharedPreferences("prefs",android.content.Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(getActivity(), SignInActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
    private void showChangeLanguageDialog() {
        final String[] list = {getString(R.string.en),getString(R.string.vi)};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.selectLang));
        builder.setSingleChoiceItems(list, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 0) {
                    setLocale("en");
                    requireActivity().recreate();
                } else  {
                    setLocale("vi");
                    requireActivity().recreate();
                }
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
        p_lang= getActivity().getSharedPreferences("lang", Context.MODE_PRIVATE);
        SharedPreferences.Editor e_lang = p_lang.edit();
        e_lang.putString("lang", lang);
        e_lang.apply();
    }

//    private void loadLocale() {
//        SharedPreferences prefs = getActivity().getSharedPreferences("lang",Context.MODE_PRIVATE);
//        String language = prefs.getString("language","");
//        setLocale(language);
//    }

}