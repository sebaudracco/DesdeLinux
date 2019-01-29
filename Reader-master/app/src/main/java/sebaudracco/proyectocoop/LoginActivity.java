package sebaudracco.proyectocoop;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;



public class LoginActivity extends AppCompatActivity {
    private static final int REQUEST_READ_CONTACTS = 0;

    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };

    private EditText mPasswordView, mUserName;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // declaring obejct of EditText control
        mUserName = findViewById (R.id.txtUserName);
        mPasswordView = findViewById(R.id.txtPassword);

        Button btnLogin =  findViewById(R.id.btnLogin);
        // Ingreso del Administrador

        btnLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                String UserName = mUserName.getText().toString();
                String Pwd = mPasswordView.getText().toString();

                if(UserName.equalsIgnoreCase("1") && Pwd.equals("2")) {
                    Intent MainIntent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(MainIntent);
                    Toast.makeText(LoginActivity.this," Bienvenido! El Ingreso ha sido exitoso!", Toast.LENGTH_LONG).show();
                }

                //Ingreso del Lecturista
                 else if(UserName.equalsIgnoreCase("usuario") && Pwd.equals("lector")) {
                    Intent MainIntent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(MainIntent);
                    Toast.makeText(LoginActivity.this," Bienvenido! El Ingreso ha sido exitoso!", Toast.LENGTH_LONG).show();
                }

                else{
                    Toast.makeText(LoginActivity.this,"Disculpe, los datos son incorrectos!", Toast.LENGTH_LONG).show();
                }


            }
        });







        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }
}

