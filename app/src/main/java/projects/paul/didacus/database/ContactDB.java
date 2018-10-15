package projects.paul.didacus.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/*

 Classe che implementa la creazione del Database di Contatti

*/

public class ContactDB extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_CONTACTS = "contact";

    private static final String DATABASE_NAME = "kinglon.db";

    public static final String COLUMN_ID      = "id_contact";
    public static final String COLUMN_NAME    = "name";
    public static final String COLUMN_SURNAME = "surname";
    public static final String COLUMN_PHONE   = "phone";
    public static final String COLUMN_ADDRESS = "address";
    public static final String COLUMN_IMAGE   = "image";
    public static final String COLUMN_COMPANY = "company";
    public static final String COLUMN_MAIL    = "mail";
    public static final String COLUMN_JOB     = "job";


    private static final String DATABASE_TABLE_CREATE =
            "CREATE TABLE " + TABLE_CONTACTS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " TEXT NOT NULL, " +
                    COLUMN_SURNAME + " TEXT NOT NULL, " +
                    COLUMN_PHONE + " INTEGER NOT NULL," +
                    COLUMN_ADDRESS + " TEXT , " +
                    COLUMN_IMAGE + " BLOB , " +
                    COLUMN_COMPANY + " TEXT, " +
                    COLUMN_MAIL + " TEXT, " +
                    COLUMN_JOB + " TEXT );";


    public ContactDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // ======= Metodo che crea la tabella definita sopra ======= //
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_TABLE_CREATE);
    }

    // ====== Metodo che controlla l'esistenza delle tabelle (implementabile la gestione delle versioni database) === //
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        db.execSQL(" DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        onCreate(db);
    }

    // ===== Istruzione SQL per la selezione dei Contatti ======= //
    public static final String SELECT_ALL_CONTACTS = "SELECT "
            + COLUMN_ID + ","
            + COLUMN_NAME + ","
            + COLUMN_SURNAME + ","
            + COLUMN_PHONE + ","
            + COLUMN_ADDRESS + ","
            + COLUMN_IMAGE + ","
            + COLUMN_COMPANY + ","
            + COLUMN_MAIL + ","
            + COLUMN_JOB + " FROM " + TABLE_CONTACTS;

    // ===== Istruzione SQL per la selezione di un solo Contatto ======= //
    public static final String SELECT_SINGLE_CONTACT = " SELECT * FROM " + TABLE_CONTACTS + " WHERE id_contact=";
}