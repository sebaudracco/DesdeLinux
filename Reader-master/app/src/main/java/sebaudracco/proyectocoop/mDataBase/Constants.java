package sebaudracco.proyectocoop.mDataBase;
public class Constants {

    //COLUMNS
    public static final String ROW_ID= "id";
    public static final String NAME="name";
    public static final String ADDRESS="address";
    public static final String MEDIDOR="medidor";
    public static final String NEW= "nueva" ;
 //   public static final String FOTO="foto";


    /* DB PROPERTIES */
    static final String DB_NAME="hh_DB";
    static final String TB_TABLE="hh_TB";

    static final int DB_VERSION=1;




    /*CREATE TB STMT
    static final String CREATE_TB = "CREATE TABLE" + DB_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "name TEXT , ide TEXT, address TEXT, nuevo TEXT);";

*/


    //DROP TB STMT
    static final String DROP_TB="DROP TABLE IF EXISTS "+DB_NAME;
}

