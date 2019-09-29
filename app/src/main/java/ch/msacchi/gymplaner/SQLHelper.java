package ch.msacchi.gymplaner;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Marco on 24.04.2017.
 */

public class SQLHelper extends SQLiteOpenHelper {
    private static final String  DB_NAME = "TrainingsPlanerDB.db";
    private static final int DB_VERSION = 1;
    private Context context;

    //DB GRUNDVERSION
    // Table geraet:
    private final String geraet_GRUNDVERSION =
            "CREATE TABLE \"main\".\"geraet\" (\n" +
                    "\"id\"  INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "\"user_id\"  INTEGER,\n" +
                    "\"g_name\"  TEXT,\n" +
                    "\"g_position\"  INTEGER,\n" +
                    "\"g_einstellungen\"  TEXT,\n" +
                    "\"g_setwiederholungen\"  TEXT,\n" +
                    "\"g_gewicht\"  TEXT\n" +
                    ");";
    //Table plan:
    private final String plan_GRUNDVERSION =
            "CREATE TABLE \"plan\" (\n" +
                "\"id\"  INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "\"user_id\"  INTEGER,\n" +
                "\"p_name\"  TEXT,\n" +
                "\"p_fav\"  INTEGER NOT NULL DEFAULT 0,\n" +
                "\"p_visability\"  INTEGER DEFAULT 0\n" +
                ");";
    //Table user:
    private final String user_GRUNDVERSION =
            "CREATE TABLE \"user\" (\n" +
                    "\"id\"  INTEGER,\n" +
                    "\"u_web_id\"  INTEGER,\n" +
                    "\"u_mail\"  TEXT,\n" +
                    "\"u_password\"  TEXT,\n" +
                    "\"u_firstName\"  TEXT,\n" +
                    "\"u_lastName\"  TEXT,\n" +
                    "\"u_lastSync\"  INTEGER,\n" +
                    "\"\"  ,\n" +
                    "PRIMARY KEY (\"id\" ASC)\n" +
                    ");";
    //Table verbindung
    private final String verbindung_GRUNDVERSION =
            "CREATE TABLE \"main\".\"verbindung\" (\n" +
                    "\"v_p_id\"  INTEGER NOT NULL,\n" +
                    "\"v_g_id\"  INTEGER NOT NULL,\n" +
                    "\"v_g_planposi\"  INTEGER,\n" +
                    "\"user_id\"  INTEGER,\n" +
                    "\"\"  ,\n" +
                    "FOREIGN KEY (\"v_p_id\") REFERENCES \"plan\" (\"id\") ON DELETE CASCADE,\n" +
                    "FOREIGN KEY (\"v_g_id\") REFERENCES \"geraet\" (\"id\") ON DELETE CASCADE\n" +
                    ");";

    /** Table pref
     * zum Appeinstellungen speichern
     */
    private final String pref_GRUNDVERSION =
            "CREATE TABLE \"pref\" (\n" +
                    "\"key\"  TEXT NOT NULL,\n" +
                    "\"text\"  TEXT,\n" +
                    "\"bool\"  INTEGER,\n" +
                    "PRIMARY KEY (\"key\" ASC)\n" +
                    ");";

    //Key's für die Appeinstellungen
    private final String [] keys_GRUNDVERSION =
            {"INSERT INTO \"pref\" VALUES ('homeliste', null, 0);"};

    //ENDE DB GRUNDVERSION



    public SQLHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(geraet_GRUNDVERSION);
            db.execSQL(plan_GRUNDVERSION);
            db.execSQL(user_GRUNDVERSION);
            db.execSQL(verbindung_GRUNDVERSION);
            db.execSQL(pref_GRUNDVERSION);

            for(int i = 0; i < keys_GRUNDVERSION.length; i++){
                db.execSQL(keys_GRUNDVERSION[i]);
            }
        }
        catch (Exception ex){
            Toast.makeText(this.context, "Database Error: Fail on Create", Toast.LENGTH_LONG).show();
            Log.e("DataBase",ex.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
