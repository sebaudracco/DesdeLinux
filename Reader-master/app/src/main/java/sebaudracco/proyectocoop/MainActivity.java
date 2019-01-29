package sebaudracco.proyectocoop;


import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.sql.Blob;
import java.util.ArrayList;

import sebaudracco.proyectocoop.mDataBase.DBAdapter;
import sebaudracco.proyectocoop.mDataObject.Spacecraft;
import sebaudracco.proyectocoop.mListView.CustomAdapter;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.support.v7.widget.AppCompatDrawableManager.get;
import static sebaudracco.proyectocoop.R.*;
import static sebaudracco.proyectocoop.R.id.*;
import static sebaudracco.proyectocoop.R.id.imagemId;
import static sebaudracco.proyectocoop.mDataBase.Constants.FOTO;


public class MainActivity extends AppCompatActivity {

    ListView lv;
    EditText nameEditText, nameEditText2, nameEditText3,nameEditText4,nameEditText5, nameEditText6;
    Button saveBtn,retrieveBtn;
    ArrayList<Spacecraft> spacecrafts=new ArrayList<>();
    CustomAdapter adapter;
    final Boolean forUpdate=true;
    private final String CARPETA_RAIZ="misImagenesPrueba/";
    private final String RUTA_IMAGEN=CARPETA_RAIZ+"misFotos";

    final int COD_SELECCIONA=10;
    final int COD_FOTO=20;

    Button botonCargar;
    ImageView imagemId, ima2;
    String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_main);
        Toolbar toolbar =  findViewById(id.toolbar);
        setSupportActionBar(toolbar);

        Spacecraft spacecraft = new Spacecraft ();
        spacecraft.getId ();
        spacecraft.getName();
        spacecraft.getAddress ();
        spacecraft.getMedidor ();
        spacecraft.getNueva ();
        spacecraft.getBitmap2 ();

        lv=  findViewById(id.lv);
        adapter=new CustomAdapter(this,spacecrafts);

        this.getSpacecrafts();
        lv.setAdapter(adapter);

        final FloatingActionButton fab =  findViewById(id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                displayDialog(false);
                fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(color.mi_color_verde)));
            }
        });


         final FloatingActionButton share = findViewById(id.share);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                share.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(color.mi_color_verde)));
                Intent Myintent = new Intent ( MainActivity.this, Main2Activity.class );
                startActivity ( Myintent );
                Toast.makeText ( MainActivity.this, " Estas a punto de exportar la Lista a Excel!", Toast.LENGTH_LONG ).show ();

            }
        });
        }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void displayDialog(Boolean forUpdate)
    {


        Dialog d=new Dialog(this);
        d.setTitle("SQLITE DATA");
        d.setContentView(layout.dialog_layout);

        nameEditText=  d.findViewById(nameEditTxt);
        nameEditText2=  d.findViewById(nameEditTxt2);
        nameEditText3=  d.findViewById(nameEditTxt3);
        nameEditText4=  d.findViewById(nameEditTxt4);
        nameEditText5=  d.findViewById(nameEditTxt5);
        imagemId=  d.findViewById(id.imagemId);
        botonCargar=  d.findViewById(btnCargarImg);



        saveBtn=  d.findViewById(id.saveBtn);
        retrieveBtn=  d.findViewById(id.retrieveBtn);

        if(!forUpdate)
        {
                        saveBtn.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onClick(View v) {
                        String nameTxt = nameEditText.getText().toString();
                        String nameTxt2 = nameEditText2.getText().toString();
                        String nameTxt3 = nameEditText3.getText().toString();
                        String nameTxt4 = nameEditText4.getText().toString();
                        String nameTxt5 = nameEditText5.getText().toString();
                        ImageView imagemId= cargarImagen ();

                        if(nameTxt.length () != 0 && nameTxt2.length() != 0 && nameTxt3.length() != 0&& nameTxt4.length() != 0 && nameTxt5.length() != 0 ){
                            save(nameTxt,nameTxt2, nameTxt3, nameTxt4, nameTxt5, imagemId);
                            nameEditText.setText("");
                            nameEditText2.setText("");
                            nameEditText3.setText("");
                            nameEditText4.setText("");
                            nameEditText5.setText("");

                            Toast.makeText(MainActivity.this,"Registro Agregado!",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(MainActivity.this,"Debes Completar los campos requeridos!!",Toast.LENGTH_LONG).show();
                        }
                    }

                        });

              retrieveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getSpacecrafts();
                }
            });
        }

        else {

            //SET SELECTED TEXT
            nameEditText.setText(adapter.getSelectedItemID ());
            nameEditText2.setText(adapter.getSelectedItemName());
            nameEditText3.setText(adapter.getSelectedItemAddress ());
            nameEditText4.setText(adapter.getSelectedItemMedidor ());
            nameEditText5.setText(adapter.getSelectedItemNuevo ());
            imagemId.setImageBitmap ( adapter.getSelectedItemFoto () );



            saveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    update(nameEditText.getText().toString());
                    update(nameEditText2.getText().toString());
                    update(nameEditText3.getText().toString());
                    update(nameEditText4.getText().toString());
                    update(nameEditText5.getText().toString());

                }
            });
            retrieveBtn.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                    getSpacecrafts();
                }
            });
        }

        d.show();


    }




    private boolean validaPermisos() {

        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.M){
            return true;
        }

        if((checkSelfPermission(CAMERA)==PackageManager.PERMISSION_GRANTED)&&
                (checkSelfPermission(WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED)){
            return true;
        }

        if((shouldShowRequestPermissionRationale(CAMERA)) ||
                (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE))){
            cargarDialogoRecomendacion();
        }else{
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,CAMERA},100);
        }

        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==100){
            if(grantResults.length==2 && grantResults[0]==PackageManager.PERMISSION_GRANTED
                    && grantResults[1]==PackageManager.PERMISSION_GRANTED){
                botonCargar.setEnabled(true);
            }else{
                solicitarPermisosManual();
            }
        }






    }

    private void solicitarPermisosManual() {
        final CharSequence[] opciones={"si","no"};
        final AlertDialog.Builder alertOpciones=new AlertDialog.Builder(MainActivity.this);
        alertOpciones.setTitle("¿Desea configurar los permisos de forma manual?");
        alertOpciones.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (opciones[i].equals("si")){
                    Intent intent=new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri=Uri.fromParts("package",getPackageName(),null);
                    intent.setData(uri);
                    startActivity(intent);
                }else{
                    Toast.makeText(getApplicationContext(),"Los permisos no fueron aceptados",Toast.LENGTH_SHORT).show();
                    dialogInterface.dismiss();
                }
            }
        });
        alertOpciones.show();
    }

    private void cargarDialogoRecomendacion() {
        AlertDialog.Builder dialogo=new AlertDialog.Builder(MainActivity.this);
        dialogo.setTitle("Permisos Desactivados");
        dialogo.setMessage("Debe aceptar los permisos para el correcto funcionamiento de la App");

        dialogo.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,CAMERA},100);
            }
        });
        dialogo.show();
    }



    public void onclick(View view) {
        cargarImagen();
    }

    private ImageView cargarImagen() {

        final CharSequence[] opciones={"Tomar Foto","Cargar Imagen","Cancelar"};
        final AlertDialog.Builder alertOpciones=new AlertDialog.Builder(MainActivity.this);
        alertOpciones.setTitle("Seleccione una Opción");
        alertOpciones.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (opciones[i].equals("Tomar Foto")){
                    tomarFotografia();
                }else{
                    if (opciones[i].equals("Cargar Imagen")){
                        Intent intent=new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image/");
                        startActivityForResult(intent.createChooser(intent,"Seleccione la Aplicación"),COD_SELECCIONA);
                    }else{
                        dialogInterface.dismiss();
                    }
                }
            }
        });
        alertOpciones.show();



        return imagemId;
    }

    private  void tomarFotografia() {
        File fileImagen = new File ( Environment.getExternalStorageDirectory (), RUTA_IMAGEN );
        boolean isCreada = fileImagen.exists ();
        String nombreImagen = "";
        if (isCreada == false) {
            isCreada = fileImagen.mkdirs ();
        }

        if (isCreada == true) {
            nombreImagen = (System.currentTimeMillis () / 1000) + ".png";
        }


        path = Environment.getExternalStorageDirectory () +
                File.separator + RUTA_IMAGEN + File.separator + nombreImagen;

        File imagen = new File ( path );

        Intent intent = null;
        intent = new Intent ( MediaStore.ACTION_IMAGE_CAPTURE );
        ////
        Uri imageUri = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ) {
            String authorities = getApplicationContext ().getPackageName () + ".provider";
            imageUri = FileProvider.getUriForFile ( this, authorities, imagen );
            intent.putExtra ( MediaStore.EXTRA_OUTPUT, imageUri );
        } else {
            intent.putExtra ( MediaStore.EXTRA_OUTPUT, Uri.fromFile ( imagen ) );
        }
        startActivityForResult ( intent, COD_FOTO );


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK){

            switch (requestCode){
                case COD_SELECCIONA:
                    Uri miPath=data.getData();
                   imagemId.setImageURI (miPath);
                    break;

                case COD_FOTO:
                    MediaScannerConnection.scanFile(this, new String[]{path}, null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                @Override
                                public void onScanCompleted(String path, Uri uri) {
                                    Log.i("Ruta de almacenamiento","Path: "+path);
                                }
                            });

                  Bitmap bitmap= BitmapFactory.decodeFile(path);
                   imagemId.setImageBitmap(bitmap);

                    break;
            }


        }
    }

    //SAVE
    private void save(String id, String name, String address, String medidor, String nueva, ImageView foto)
    {
        DBAdapter db=new DBAdapter(this);
        db.openDB();
        boolean saved;
        if (db.add ( id, name , address, medidor, nueva, foto )) saved = true;
        else saved = false;

        if(saved)
        {
            nameEditText.setText("");
            nameEditText2.setText("");
            nameEditText3.setText("");
            nameEditText4.setText("");
            nameEditText5.setText("");


            getSpacecrafts();
        }

        else {
            Toast.makeText(this,"Imposible Grabar",Toast.LENGTH_SHORT).show();
        }
    }

    //RETRIEVE OR GETSPACECRAFTS
    private void getSpacecrafts()
    {
        spacecrafts.clear();
        DBAdapter db=new DBAdapter(this);
        db.openDB();
        Cursor c=db.retrieve();
        Spacecraft spacecraft=null;

        while (c.moveToNext())
        {
            String id=c.getString(0);
            String name=c.getString(1);
            String address=c.getString(2);
            String medidor=c.getString(3);
            String nueva=c.getString(4);

            byte[] img = c.getBlob ( 5 );

       //     Bitmap bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);
         //   ByteArrayOutputStream stream = new ByteArrayOutputStream ();
           // bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);


            spacecraft=new Spacecraft();
            spacecraft.setId(id);
            spacecraft.setName(name);
            spacecraft.setAddress(address);
            spacecraft.setMedidor (medidor);
            spacecraft.setNueva(nueva);

            // convert from byte array to bitmap

            Bitmap bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);
            ByteArrayOutputStream stream = new ByteArrayOutputStream ();
//            bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;

            bitmap = BitmapFactory.decodeFile(FOTO,options);

            spacecraft.setBitmap2(bitmap);

            spacecrafts.add(spacecraft);
        }

        db.closeDB();
        lv.setAdapter(adapter);


    }


    public class convertidor {

        // convert from bitmap to byte array
        public  byte[] getBytes(Bitmap bitmap) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 0, stream);
            return stream.toByteArray();
        }

        // convert from byte array to bitmap
        public  Bitmap getImage(byte[] img) {
            return BitmapFactory.decodeByteArray(img, 0, img.length);
        }
    }

    //UPDATE OR EDIT
    private void update(String newName)
    {
        //GET ID OF SPACECRAFT
        String id=adapter.getSelectedItemID();

        //UPDATE IN DB
        DBAdapter db=new DBAdapter(this);
        db.openDB();
        boolean updated=db.update(newName,id);
        db.closeDB();

        if(updated)
        {
            nameEditText.setText(newName);
            getSpacecrafts();

            Toast.makeText(this,"Ultima Lectura Actualizada!",Toast.LENGTH_SHORT).show();

        }else {
            Toast.makeText(this,"No se pudo Actualizar",Toast.LENGTH_SHORT).show();
        }

    }

    private void delete()
    {
        //GET ID
        String id=adapter.getSelectedItemID();


        //DELETE FROM DB
        DBAdapter db=new DBAdapter(this);
        db.openDB();
        boolean deleted=db.delete(id);
        db.closeDB();

        if(deleted)
        {
            getSpacecrafts();
            Toast.makeText(MainActivity.this,"Registro Borrado!",Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(this,"No es posible Borrar el Registro!",Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        CharSequence title=item.getTitle();
        if(title=="NUEVO REGISTRO")
        {
            displayDialog(!forUpdate);

        }else  if(title=="EDITAR ULTIMO ESTADO")
        {
            displayDialog(forUpdate);

        }else  if(title=="BORRAR")
        {
            delete();
        }

        return super.onContextItemSelected(item);
    }





}
