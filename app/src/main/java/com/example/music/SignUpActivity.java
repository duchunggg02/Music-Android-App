package com.example.music;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SignUpActivity extends AppCompatActivity {
    TextView tv_HaveAccount;
    Button buttonRegister;
    EditText edt_userName;
    EditText edt_pass2;
    EditText edt_pass1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        tv_HaveAccount = findViewById(R.id.tv_HaveAccount);
        buttonRegister = findViewById(R.id.txt_Register);
        edt_userName = findViewById(R.id.edt_userName);
        edt_pass2 = findViewById(R.id.edt_pass2);
        edt_pass1 = findViewById(R.id.edt_pass1);

        tv_HaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username1 = edt_userName.getText().toString();
                String password1 = edt_pass1.getText().toString();
                String confirmPassword = edt_pass2.getText().toString();

                // Kiểm tra xem tên đăng nhập đã tồn tại chưa
                DatabaseHelper db = new DatabaseHelper(SignUpActivity.this);
                boolean isUsernameExists = db.checkUsernameExists(username1);

                if(!username1.isEmpty() && !password1.isEmpty() && !confirmPassword.isEmpty()) {
                    if (isUsernameExists) {
                        Toast.makeText(SignUpActivity.this, "Tên đăng nhập đã tồn tại", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (password1.equals(confirmPassword)) {
                        long newRowId = db.addUser(username1, password1);

                        if (newRowId != -1) {
                            Toast.makeText(SignUpActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(SignUpActivity.this, "Đăng ký thất bại", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(SignUpActivity.this, "Mật khẩu không trùng khớp", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SignUpActivity.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}