package com.example.music;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SignInActivity extends AppCompatActivity {
    private EditText editTextUsername;
    private EditText editTextPassword;
    private Button buttonLogin;
    private Button btn_Register, btn_Skip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        btn_Register = findViewById(R.id.btn_Register);
        btn_Skip = findViewById(R.id.btn_Skip);

        btn_Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
        btn_Skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username = editTextUsername.getText().toString();
                String password = editTextPassword.getText().toString();

                DatabaseHelper db = new DatabaseHelper(SignInActivity.this);
                boolean isValid = db.isUserValid(username, password);

                if (isValid) {
                    Toast.makeText(SignInActivity.this, "Đăng nhập thành công!!!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();

                    // Sau khi đăng nhập thành công, lưu tên người dùng vào SharedPreferences
                    SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("username", username);
                    int userId = db.getUserId(username, password);
                    editor.putInt("userId", userId);
                    // username là tên người dùng đã đăng nhập
                    editor.apply();

                } else {
                    // Đăng nhập thất bại
                    Toast.makeText(SignInActivity.this, "Đăng nhập không thành công!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        String username = prefs.getString("username", "");
        // Lấy tên người dùng đã lưu
        // Nếu tên người dùng đã lưu trong SharedPreferences không rỗng
        // tức là người dùng đã đăng nhập trước đó
        if (!username.isEmpty()) {
            // Chuyển hướng đến MainActivity
            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Kết thúc SignInActivity để không thể quay lại màn hình đăng nhập
        }
    }
}