package com.example.coffeeshop.view.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.coffeeshop.controller.LoginController;
import com.example.coffeeshop.model.User;
import com.example.coffeeshop.view.main.MainActivity;
import com.example.coffeeshop.R;
import com.example.coffeeshop.databinding.ActivityLoginBinding;
import com.example.coffeeshop.view.register.RegisterActivity;

public class LoginActivity extends AppCompatActivity {

    private LoginController loginController;
    private ActivityLoginBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginController = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginController.class);

        final EditText usernameEditText = binding.username;
        final EditText passwordEditText = binding.password;
        final Button loginButton = binding.login;
        final ProgressBar loadingProgressBar = binding.loading;
        final TextView registerTextView = binding.textViewRegister;

        // Listener para o botão "Cadastre-se"
        registerTextView.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        loginController.getLoginFormState().observe(this, loginFormState -> {
            if (loginFormState == null) {
                return;
            }
            loginButton.setEnabled(loginFormState.isDataValid());
            if (loginFormState.getUsernameError() != null) {
                usernameEditText.setError(getString(loginFormState.getUsernameError()));
            }
            if (loginFormState.getPasswordError() != null) {
                passwordEditText.setError(getString(loginFormState.getPasswordError()));
            }
        });

        loginController.getLoginResult().observe(this, loginResult -> {
            if (loginResult == null) {
                return;
            }

            loadingProgressBar.setVisibility(View.GONE);

            if (loginResult.getError() != null) {
                showLoginFailed(loginResult.getError());
            }

            if (loginResult.getSuccess() != null) {
                updateUiWithUser(loginResult.getSuccess());
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginController.loginDataChanged(
                        usernameEditText.getText().toString(),
                        passwordEditText.getText().toString()
                );
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loginController.login(
                        usernameEditText.getText().toString(),
                        passwordEditText.getText().toString()
                );
            }
            return false;
        });

        loginButton.setOnClickListener(v -> {
            loadingProgressBar.setVisibility(View.VISIBLE);
            loginController.login(
                    usernameEditText.getText().toString(),
                    passwordEditText.getText().toString()
            );
        });
    }

    private void updateUiWithUser(User model) {
        // Exibir mensagem de boas-vindas
        String welcome = getString(R.string.welcome) + model.getName();
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();

        // Criar um Intent para iniciar a MainActivity
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);

        // Adicionar informações do usuário ao Intent
        intent.putExtra("USER_ID", model.getId()); // Certifique-se de enviar o ID
        intent.putExtra("USER_NAME", model.getName());
        intent.putExtra("USER_EMAIL", model.getEmail());
        intent.putExtra("USER_USERNAME", model.getUsername());
        intent.putExtra("USER_PHONE", model.getPhone());
        intent.putExtra("USER_ADDRESS", model.getAddress());

        // Se os pedidos estiverem disponíveis, adicioná-los ao Intent
        if (model.getOrders() != null) {
            intent.putExtra("USER_ORDERS", (java.io.Serializable) model.getOrders());
        }
        // Logs para depuração
        System.out.println("LoginActivity: Dados enviados para MainActivity:");
        System.out.println("USER_ID: " + model.getId());
        System.out.println("USER_NAME: " + model.getName());
        System.out.println("USER_EMAIL: " + model.getEmail());
        System.out.println("USER_USERNAME: " + model.getUsername());
        System.out.println("USER_PHONE: " + model.getPhone());
        System.out.println("USER_ADDRESS: " + model.getAddress());

        // Garante que a LoginActivity seja removida da pilha
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        // Inicia a MainActivity
        startActivity(intent);

        // Finaliza a LoginActivity
        finish();
    }



    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }
}
