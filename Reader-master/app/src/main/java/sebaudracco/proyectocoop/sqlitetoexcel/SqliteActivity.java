package sebaudracco.proyectocoop.sqlitetoexcel;


import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.ImageView;
import android.widget.Toast;


import java.io.File;

import sebaudracco.proyectocoop.R;
import sebaudracco.proyectocoop.SQLiteToExcel;
import sebaudracco.proyectocoop.mDataBase.DBHelper;


import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;



public class SqliteActivity extends AppCompatActivity{

    private static final Object WritableCell = null ;
    private Button sqliteToExcelButton2;

    DBHelper dbHelper;

    String directory_path = Environment.getExternalStorageDirectory().getPath() + "/Backup/";
    SQLiteToExcel sqliteToExcel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( sebaudracco.proyectocoop.R.layout.activity_sqlite);


        sqliteToExcelButton2 =  findViewById(R.id.SqliteToExcel2);
        sqliteToExcelButton2.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick  (View v) {

                sqliteToExcelButton2.setBackgroundTintList ( ColorStateList.valueOf ( getResources ().getColor ( R.color.mi_color_verde ) ) );


                File file = new File ( SqliteActivity.this.getExternalCacheDir (), "LecturaDeEstados.xls" );
                //  File file = getFileStreamPath();
                if (file.exists ()) {
                    Log.v ( "Farmgraze", "Email file_exists!" );
                } else {
                    Log.v ( "Farmgraze", "Email file does not exist!" );
                }
                Intent emailIntent = new Intent ( android.content.Intent.ACTION_SEND );
                emailIntent.putExtra ( android.content.Intent.EXTRA_EMAIL, new String[]{"sebaudracco@gmail.com"} );
                emailIntent.putExtra ( android.content.Intent.EXTRA_SUBJECT, "Lectura de Estados" );
                emailIntent.putExtra ( android.content.Intent.EXTRA_TEXT, "Estimado/a Administrador/a:    Envío Lectura de Estados, Saludos!" );
                emailIntent.putExtra ( android.content.Intent.EXTRA_STREAM, Uri.parse ( "LecturaDeEstados.xls" ) );
                emailIntent.putExtra ( Intent.EXTRA_STREAM, Uri.fromFile ( new File ( "/Backup/") ) );
                emailIntent.setType ( "message/rfc822" );
                emailIntent.setType ( "*/*" );
                Log.v ( "FarmGraze", "SEND EMAIL FileUri=" + Uri.parse ( "file:///mnt/sdcard/" + (SqliteActivity.this.getExternalCacheDir () + "LecturaDeEstados.xls") ) );
                emailIntent.putExtra ( Intent.EXTRA_STREAM, Uri.parse ( "/Backup/"+ (SqliteActivity.this.getExternalCacheDir () + "LecturaDeEstados.xls") ) );

                startActivity ( Intent.createChooser ( emailIntent, "Desea Realizar el aviso por email?" ) );

                if (emailIntent.resolveActivity ( getPackageManager () ) != null) {
                    startActivity ( emailIntent );


                }
            }

            


        });
        if (!checkPermission()) {
            openActivity();
        } else {
            if (checkPermission()) {
                requestPermissionAndContinue();
            } else {
                openActivity();
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
        String csvFile = "LectorDeEstados.xls";

        File directory = new File ( sd.getAbsolutePath () );
        //create directory if not exist
        if (!directory.isDirectory ()) {
            directory.mkdirs ();
        }
        try {

            sqliteToExcel = new SQLiteToExcel(getApplicationContext(),"hh_DB", directory_path);
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

