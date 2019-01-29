package sebaudracco.proyectocoop.mDataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.graphics.drawable.IconCompat;
import android.util.Log;
import android.widget.ImageView;

import sebaudracco.proyectocoop.MainActivity;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

import sebaudracco.proyectocoop.R;
import sebaudracco.proyectocoop.mDataObject.Spacecraft;

import static android.graphics.BitmapFactory.decodeFile;

import static sebaudracco.proyectocoop.mDataBase.Constants.ADDRESS;
import static sebaudracco.proyectocoop.mDataBase.Constants.DB_NAME;
import static sebaudracco.proyectocoop.mDataBase.Constants.FOTO;
import static sebaudracco.proyectocoop.mDataBase.Constants.MEDIDOR;
import static sebaudracco.proyectocoop.mDataBase.Constants.NAME;
import static sebaudracco.proyectocoop.mDataBase.Constants.NEW;
import static sebaudracco.proyectocoop.mDataBase.Constants.ROW_ID;
import static sebaudracco.proyectocoop.mDataBase.Constants.TB_TABLE;

public class
DBAdapter {

    Context c;
    SQLiteDatabase db;
    DBHelper helper;

    public DBAdapter(Context c) {
        this.c = c;
        helper=new DBHelper(c);
    }

    //OPEN CON
    public void openDB()
    {
        try
        {
            db=helper.getWritableDatabase();
        }catch (SQLException e)
        {

        }
    }
    //CLOSE DB
    public void closeDB()
    {
        try
        {
            helper.close();
        }catch (SQLException e)
        {

        }
    }

    //SAVE
    public boolean add(String id, String name, String address, String medidor, String nueva, ImageView foto)
    {
//Convierto la Imagen en Bitmap
        Bitmap b;
        b = ((BitmapDrawable) foto.getDrawable()).getBitmap();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.PNG, 0, bos);
        byte[] img = bos.toByteArray();





        ContentValues contentValues = new ContentValues();
        contentValues.put(  ROW_ID , id);
        contentValues.put(NAME, name);
        contentValues.put(ADDRESS, address);
        contentValues.put(MEDIDOR,medidor);
        contentValues.put(NEW , nueva);
        //AGREGO foto COMO BITMAP
        contentValues.put(FOTO,img);

        long result = db.insert(DB_NAME, null, contentValues);

        //if date as inserted incorrectly it will return -1
        if (result == -1) {
            return false;
        } else {
            return true;
        }
        /*
            // convert from bitmap to byte array
            public static byte[] getBytes(Bitmap bitmap) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
                return stream.toByteArray();
            }

            // convert from byte array to bitmap
            public static Bitmap getImage(byte[] image) {
                return BitmapFactory.decodeByteArray(image, 0, image.length);
            }*/
        }







    //SELECT
    public Cursor retrieve()
    {
        String[] columns={ROW_ID , NAME, ADDRESS, MEDIDOR,  NEW, FOTO };

        Cursor c=db.query(Constants.DB_NAME,columns,null,null,null,null,null,null);

        return c;
    }


    //UPDATE/edit
    public boolean update(String newName,String id)
    {
        try
        {
            ContentValues cv=new ContentValues();
            cv.put(Constants.NEW,newName);
            cv.put(Constants.FOTO,newName);

            int result=db.update(Constants.DB_NAME,cv, Constants.ROW_ID + " =?", new String[]{String.valueOf(id)});
            if(result>0)
            {
                return true;
            }
        }catch (SQLException e)
        {
            e.printStackTrace();
        }

        return false;

    }

    //DELETE/REMOVE
    public boolean delete(String id)
    {
        try
        {
            int result=db.delete(Constants.DB_NAME, ROW_ID+" =?",new String[]{String.valueOf(id)});
            if(result>0)
            {
                return true;
            }

        }catch (SQLException e)
        {
            e.printStackTrace();
        }

        return false;
    }



}




