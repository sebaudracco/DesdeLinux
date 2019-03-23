package sebaudracco.proyectocoop.sqlitetoexcel;


import android.Manifest;
import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Base64;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Message;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;


import sebaudracco.proyectocoop.MainActivity;
import sebaudracco.proyectocoop.R;
import sebaudracco.proyectocoop.SQLiteToExcel;
import sebaudracco.proyectocoop.mDataBase.DBHelper;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;



public class SqliteActivity extends AppCompatActivity{

    private static final Object WritableCell = null ;
    private Button sqliteToExcelButton2, attachment;
    private static final String TAG = "drive-quickstart";
    private static final int REQUEST_CODE_SIGN_IN = 0;
    private static final int REQUEST_CODE_CAPTURE_IMAGE = 1;
    private static final int REQUEST_CODE_CREATOR = 2;

    GoogleAccountCredential mCredential;
    ProgressDialog mProgress;
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private final String[] SCOPES = {
            GmailScopes.GMAIL_LABELS,
            GmailScopes.GMAIL_COMPOSE,
            GmailScopes.GMAIL_INSERT,
            GmailScopes.GMAIL_MODIFY,
            GmailScopes.GMAIL_READONLY,
            GmailScopes.MAIL_GOOGLE_COM
    };
    private InternetDetector internetDetector;
    private final int SELECT_PHOTO = 1;
    //MimeBodyPart attachmentBody;
    public String fileName = "";


    DBHelper dbHelper;

    String directory_path = Environment.getExternalStorageDirectory().getPath() + "/";
    SQLiteToExcel sqliteToExcel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( sebaudracco.proyectocoop.R.layout.activity_sqlite);
        init();
        findViewById(R.id.attachment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*
                if (Uti.checkPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    photoPickerIntent.setType("application/vnd.ms-excel");

                    startActivityForResult(photoPickerIntent, SELECT_PHOTO);
                } else {
                    ActivityCompat.requestPermissions(SqliteActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, SELECT_PHOTO);
                }*/
                 fileName = "/storage/emulated/0/LectorDeEstados.xls";
            //    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
             //       attachment.setBackgroundTintList (ColorStateList.valueOf (getResources ().getColor (R.color.colorPrimary)));
            //    }
                 attachment.setText("Listo!");
            }
        });

        sqliteToExcelButton2 =  findViewById(R.id.SqliteToExcel2);
        sqliteToExcelButton2.setOnClickListener(new View.OnClickListener() {
         //   @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick  (View v) {

           //     sqliteToExcelButton2.setBackgroundTintList (ColorStateList.valueOf (getResources ().getColor (R.color.colorPrimary)));

                File file = new File (SqliteActivity.this.getExternalCacheDir (), "LecturaDeEstados.xls");


                openActivity ();

                getResultsFromApi (v);



            }
        });


        if (!checkPermission()) {
          //  openActivity();
        } else {
            if (checkPermission()) {
                requestPermissionAndContinue();
            } else {
               // openActivity();
            }
        }
    }



    private void init() {
        // Initializing Internet Checker
        internetDetector = new InternetDetector(getApplicationContext());

        // Initialize credentials and service object.
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff ());

        // Initializing Progress Dialog
        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Enviando Email a la Cooperativa!");

        sqliteToExcelButton2 =  findViewById(R.id.SqliteToExcel2);
        attachment =findViewById (R.id.attachment);
        //sendFabButton = findViewById(R.id.sendFabButton);
      //  edtToAddress = findViewById(R.id.EmailAddress);
      //  edtMessage = findViewById(R.id.note_value);
        // sendFabButton = findViewById (R.id.sendFabButton);


    }

    private void showMessage(View view, String message) {
// Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
        Intent Intentg = new Intent(SqliteActivity.this, MainActivity.class);
        startActivity(Intentg);
    }

    private void getResultsFromApi(View view) {
        if (!isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount(view);
        } else if (!internetDetector.checkMobileInternetConn()) {
            showMessage(view, "No network connection available.");
        }

          //else if (!Uti.isNotEmpty(edtToAddress)) {
          //  showMessage(view, "To address Required");
          //else if (!Uti.isNotEmpty(edtSubject)) {
          // showMessage(view, "Subject Required");
          // }
          // else if (!Uti.isNotEmpty(edtMessage)) {
          //   showMessage(view, "Message Required");
            else

            new MakeRequestTask(this, mCredential).execute();

    }

    // Method for Checking Google Play Service is Available
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    // Method to Show Info, If Google Play Service is Not Available.
    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }

    // Method for Google Play Services Error Info
    void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                SqliteActivity.this,
                connectionStatusCode,
                Uti.REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    // Storing Mail ID using Shared Preferences
    private void chooseAccount(View view) {
        if (Uti.checkPermission(getApplicationContext(), Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getPreferences(Context.MODE_PRIVATE).getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi(view);
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(mCredential.newChooseAccountIntent(), Uti.REQUEST_ACCOUNT_PICKER);
            }
        } else {
            ActivityCompat.requestPermissions(SqliteActivity.this,
                    new String[]{Manifest.permission.GET_ACCOUNTS}, Uti.REQUEST_PERMISSION_GET_ACCOUNTS);
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Uti.REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    showMessage(sqliteToExcelButton2, "This app requires Google Play Services. Please install " +
                            "Google Play Services on your device and relaunch this app.");
                } else {
                    getResultsFromApi(sqliteToExcelButton2);
                }
                break;
            case Uti.REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
                    String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getResultsFromApi(sqliteToExcelButton2);
                    }
                }
                break;
            case Uti.REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getResultsFromApi(sqliteToExcelButton2);
                }
                break;
            case SELECT_PHOTO:
                if (resultCode == RESULT_OK) {
                    final Uri imageUri = data.getData();
                    fileName = getPathFromURI(imageUri);
                    attachment.setText(fileName);
                }
        }
    }

    public String getPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, "", null, "");
        assert cursor != null;
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

    // Async Task for sending Mail using GMail OAuth
    private class MakeRequestTask extends AsyncTask<Void, Void, String> {

        private com.google.api.services.gmail.Gmail mService = null;
        private Exception mLastError = null;
        private View view = sqliteToExcelButton2;
        private SqliteActivity activity;

        MakeRequestTask(SqliteActivity activity, GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.gmail.Gmail.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName(getResources().getString(R.string.app_name))
                    .build();
            this.activity = activity;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                return getDataFromApi();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        private String getDataFromApi() throws IOException {
            // getting Values for to Address, from Address, Subject and Body
            String user = "me";
            String to = "cooppiquillin@hotmail.com";
            String from = mCredential.getSelectedAccountName();
            String subject = "Lector de Estados";
            File file = new File (SqliteActivity.this.getExternalCacheDir (), "/LecturaDeEstados.xls");
            String bodyText = "Estimado/a Administrador/a:\n \n  Envío Lectura de Estados,\n \n  Saludos!";
            MimeMessage mimeMessage;
            String response = "";
            try {

                mimeMessage = createEmail(to, from, subject,  bodyText);
                response = sendMessage(mService, user, mimeMessage);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
            return response;
        }

        // Method to send email
        private String sendMessage(Gmail service,
                                   String userId,
                                   MimeMessage email)
                throws MessagingException, IOException {
            Message message = createMessageWithEmail(email);
            // GMail's official method to send email with oauth2.0
            message = service.users().messages().send(userId, message).execute();

            System.out.println("Message id: " + message.getId());
            System.out.println(message.toPrettyString());
            return message.getId();
        }


        // Method to create email Params
        private MimeMessage createEmail(String to,
                                        String from,
                                        String subject,
                                        String bodyText) throws MessagingException {
            Properties props = new Properties();
            Session session = Session.getDefaultInstance(props, null);

            MimeMessage email = new MimeMessage(session);
            InternetAddress tAddress = new InternetAddress(to);
            InternetAddress fAddress = new InternetAddress(from);

            email.setFrom(fAddress);
            email.addRecipient(javax.mail.Message.RecipientType.TO, tAddress);
            email.setSubject(subject);
            email.setText(bodyText);

            // Create Multipart object and add MimeBodyPart objects to this object
            Multipart multipart = new MimeMultipart();

            // Changed for adding attachment and text

            BodyPart textBody = new MimeBodyPart();
            textBody.setText(bodyText);
            multipart.addBodyPart(textBody);



            if (!(activity.fileName.equals(""))) {
                // Create new MimeBodyPart object and set DataHandler object to this object
             /*    MimeBodyPart attachmentBody = new MimeBodyPart();
                //hoY PIENSO QUE ESTE ES EL PROBLEMA----<>>>> NO RECOGER BIEN EL FILE EXCEL

               String file = "/LectorDeEstados.xls"; // change accordingly
                String filename = "LectorDeEstador.xls";
                DataSource source = new FileDataSource(file);
                attachmentBody.setDataHandler(new DataHandler(source));
                attachmentBody.setFileName(filename);

                multipart.addBodyPart(textBody);
                multipart.addBodyPart(attachmentBody);

                email.setContent(multipart);*/

                // Create new MimeBodyPart object and set DataHandler object to this object
                MimeBodyPart attachmentBody = new MimeBodyPart();
                String filename = fileName; // change accordingly
                DataSource source = new FileDataSource(filename);
                attachmentBody.setDataHandler(new DataHandler(source));
                attachmentBody.setFileName(filename);
                multipart.addBodyPart(attachmentBody);
            }


            //Set the multipart object to the message object
            email.setContent(multipart);

            return email;



        }

        private Message createMessageWithEmail(MimeMessage email)
                throws MessagingException, IOException {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            email.writeTo(bytes);
            String encodedEmail = Base64.encodeBase64URLSafeString(bytes.toByteArray());
            Message message = new Message();
            message.setRaw(encodedEmail);
            return message;
        }

        @Override
        protected void onPreExecute() {
            mProgress.show();
        }

        @Override
        protected void onPostExecute(String output) {
            mProgress.hide();
            if (output == null || output.length() == 0) {
                showMessage(view, "No results returned.");
            } else {
                showMessage(view, output);
            }
        }

        @Override
        protected void onCancelled() {
            mProgress.hide();
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            Uti.REQUEST_AUTHORIZATION);
                } else {
                    showMessage(view, "The following error occurred:\n" + mLastError);
                    Log.v("Error", mLastError + "");
                }
            } else {
                showMessage(view, "Request Cancelled.");
            }
        }
    }








    private static final int PERMISSION_REQUEST_CODE = 200;

    private boolean checkPermission() {

        return ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissionAndContinue() {
        if (ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, WRITE_EXTERNAL_STORAGE)
                    && ActivityCompat.shouldShowRequestPermissionRationale(this, READ_EXTERNAL_STORAGE)) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                alertBuilder.setCancelable(true);
                alertBuilder.setTitle("Permission");
                alertBuilder.setMessage("Enable");
                alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(SqliteActivity.this, new String[]{WRITE_EXTERNAL_STORAGE
                                , READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                    }
                });
                AlertDialog alert = alertBuilder.create();
                alert.show();
                Log.e("", "permission denied, show dialog");
            } else {
                ActivityCompat.requestPermissions(SqliteActivity.this, new String[]{WRITE_EXTERNAL_STORAGE,
                        READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            }
        } else {
            openActivity();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (permissions.length > 0 && grantResults.length > 0) {

                boolean flag = true;
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        flag = false;
                    }
                }
                if (flag) {
                    openActivity();
                } else {
                    finish();
                }

            } else {
                finish();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void openActivity() {

        DBHelper dbHelper = new DBHelper ( this );
        dbHelper.insertData ();

        final Cursor cursor = dbHelper.getuser ();

        File sd = Environment.getExternalStorageDirectory ();


        File directory = new File ( sd.getAbsolutePath () );
        //create directory if not exist
        if (!directory.isDirectory ()) {
            directory.mkdirs ();
        }
        try {

            sqliteToExcel = new SQLiteToExcel(getApplicationContext(),"hh_DB", directory_path);
            String csvFile = "LectorDeEstados.xls";
            sqliteToExcel.exportAllTables("LectorDeEstados.xls", new SQLiteToExcel.ExportListener() {
                @Override
                public void onStart() {

                }

                @Override
                public void onCompleted(String filePath) {
                 //   Utils.showSnackBar(view, "Successfully Exported");
                }

                @Override
                public void onError(Exception e) {
                   // Utils.showSnackBar(view, e.getMessage());
                }
            });



                Toast.makeText ( getApplication (), " EXCEL ha sido Exportado a la Memoria Interna del Dispositivo ", Toast.LENGTH_LONG ).show ();

            } catch(Exception e){
                e.printStackTrace ();
            }

        }


    }

