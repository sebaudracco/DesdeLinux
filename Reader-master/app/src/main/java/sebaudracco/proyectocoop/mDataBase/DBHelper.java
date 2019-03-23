package sebaudracco.proyectocoop.mDataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static sebaudracco.proyectocoop.mDataBase.Constants.ADDRESS;
import static sebaudracco.proyectocoop.mDataBase.Constants.DB_NAME;
import static sebaudracco.proyectocoop.mDataBase.Constants.DB_VERSION;
import static sebaudracco.proyectocoop.mDataBase.Constants.MEDIDOR;
import static sebaudracco.proyectocoop.mDataBase.Constants.NAME;
import static sebaudracco.proyectocoop.mDataBase.Constants.NEW;
import static sebaudracco.proyectocoop.mDataBase.Constants.ROW_ID;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            String createTable = " CREATE TABLE " + DB_NAME +
                    "(" + " _id integer primary key autoincrement,"
                    + ROW_ID + ","
                    + NAME + ","
                    + ADDRESS + ","
                    + MEDIDOR + ","
                    + NEW + ")";
                   // + FOTO   +")";

            db.execSQL(createTable);

                /*CREATE TB STMT
    static final String CREATE_TB = "CREATE TABLE" + DB_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "name TEXT , ide TEXT, address TEXT, nuevo TEXT);";

*/
        }

        catch (SQLException e)
        {
            e.printStackTrace();
        }

    }




    public void insertData(){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
   //     contentValues.put(Constants.ROW_ID, "id");
    //    contentValues.put(Constants.NAME, "name");
    //    contentValues.put(Constants.ADDRESS, "address");
     //   contentValues.put(Constants.MEDIDOR, "medidor");
     //   contentValues.put(Constants.NEW, "nueva");
   //     contentValues.put(Constants.FOTO, "foto");
//        db.insert(DB_NAME, null, contentValues);



    }

    public long insertNewRow(String rowId, String name, String address, String medidor, String aNew){

        SQLiteDatabase db = this.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(Constants.ROW_ID, rowId);
        values.put(Constants.NAME, name);
        values.put(Constants.ADDRESS, address);
        values.put(Constants.MEDIDOR, medidor);
        values.put(Constants.NEW, aNew);
        // Insert the new row, returning the primary key value of the new row
        // Insert the new row, returning the primary key value of the new row
        long newRowId;
        newRowId = db.insert(
                Constants.DB_NAME,
                null,
                values);
        return newRowId;
    }


    @Override
    public void onUpgrade(SQLiteDatabase db,int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF  EXISTS " + DB_NAME);
        onCreate(db);
    }

    public Cursor getuser() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + DB_NAME + " ",
                null);
        return res;
    }
}

