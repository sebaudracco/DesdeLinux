package sebaudracco.proyectocoop;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import sebaudracco.proyectocoop.mDataObject.Spacecraft;
import sebaudracco.proyectocoop.sqlitetoexcel.SqliteActivity;

public class Main2Activity extends AppCompatActivity {

    private Button sqliteToExcelButton,readExcelButton ;
    private TextView tv;
    private String outputFile;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        tv = (TextView) findViewById(R.id.tv);
        sqliteToExcelButton =  findViewById(R.id.SqliteToExcel);
        sqliteToExcelButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(Main2Activity.this, SqliteActivity.class);
                startActivity(intent);
                sqliteToExcelButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.mi_color_verde)));

            }
        });

        readExcelButton =  findViewById(R.id.readExcelButton);
        readExcelButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                try {
                    doIntent();
                } catch (FileNotFoundException e) {
                    e.printStackTrace ();
                }
                Intent intent = new Intent(Main2Activity.this, MainActivity.class);
                startActivity(intent);
                readExcelButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.mi_color_verde)));
            }


        });

        Spacecraft spacecraft = new Spacecraft ();
        spacecraft.add();

    }


    private void doIntent() throws FileNotFoundException {
        File file = new File(Environment.getExternalStorageDirectory() + "LecturaDeEstados.xls");
        Uri path = Uri.fromFile(file );

        File f = new File(file.getAbsolutePath(), "prueba_sd.txt");

        OutputStreamWriter fout =
                new OutputStreamWriter (
                        new FileOutputStream (f));

        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
        pdfIntent.setDataAndType(path , "application/vnd.ms-excel");
        if (file .exists())
        {
            pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            try
            {
                startActivity(pdfIntent ); }
            catch (ActivityNotFoundException e)
            {
                Toast.makeText(Main2Activity.this,"Please install MS-Excel app to view the file.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }





}
