package ch.msacchi.gymplaner;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Marco on 24.04.2017.
 */

public class DBSource {
    private SQLiteDatabase sqDB;
    private SQLHelper sqlHelper;
    private Context context;

    public DBSource(Context context){
        sqlHelper = new SQLHelper(context);
        this.context = context;
    }

    //DB verbindung öffnen
    public void openDB(){
        sqDB = sqlHelper.getWritableDatabase();
    }

    //DB verbindung schliessen
    public void closeDB(){
        sqDB.close();
    }

    private int getLastInsertID(String tableName){
        Cursor cursor = sqDB.rawQuery("Select * From sqlite_sequence Where name = ?",new String[]{tableName});
        cursor.moveToFirst();
        int lastID = cursor.getInt(cursor.getColumnIndex("seq"));
        return lastID;
    }

    /**
     * Methoden für den Table PlanTable
     */
    //Count Table Plan
    public int countPlan(){
        String sql = "Select * from plan";
        try {
            Cursor cursor = sqDB.rawQuery(sql, null);
            return cursor.getCount();
        }
        catch (Exception ex){
            return 0;
        }
    }

    //Alle Daten von PlanTable
    public Cursor allFormPlan(){
        Cursor cursor = sqDB.rawQuery("Select * From plan ORDER BY p_fav DESC",null);
        return cursor;
    }

    //Plan nach id zurückgeben
    public Cursor getPlanById(long id){
        Cursor cursor = sqDB.rawQuery("Select * From plan Where id = ?", new String[]{String.valueOf(id)});
        return cursor;
    }

    //PlanName Updaten
    public void updatePlanName(long id, String neuerPlanName){
        Cursor cursor = sqDB.rawQuery("Update plan Set p_name = ? Where id = ?", new String[]{neuerPlanName, String.valueOf(id)});
        cursor.moveToFirst();
        cursor.close();
    }

    //Plan Favoritstatus Updaten
    public void updateFavoritStatus(long id, boolean planFavorit){
        int pFav;

        if(planFavorit){
            pFav = 1;
        }
        else{
            pFav = 0;
        }
        Cursor cursor = sqDB.rawQuery("Update plan Set p_fav = ? Where id = ?",new String[]{String.valueOf(pFav),String.valueOf(id)});
        cursor.moveToFirst();
        cursor.close();
    }

    //Plan erstellen und letze id zurückgeben.
    public long euenPlanErstellen(String planName, boolean planFavorit){
        int pFav;
        long rowId = 0;
        if(planFavorit){
            pFav = 1;
        }
        else{
            pFav = 0;
        }

        if(countUser() == 0) {
            //sqDB.execSQL("Insert Into plan (p_name,p_fav) Values (?,?)", new String[]{planName, String.valueOf(pFav)});

            /**
             * ToDo lesen
             * andere insert version Testen
             * finde die Besser weil sie die lezte Id driekt zurückgiebt.
             */
            ContentValues values = new ContentValues();
            values.put("p_name", planName);
            values.put("p_fav",String.valueOf(pFav));

            rowId = sqDB.insert("plan",null,values);

        }
        else {
            //ToDo check ob ein webaccount exisiert wenn ja PlanTable.user_id(user.web_id) einfügen
            ContentValues values = new ContentValues();
            try {
                throw new Exception("user_id muss noch zugewiesen werden");
            } catch (Exception e) {
                e.printStackTrace();
            }
            values.put("user_id","");
            values.put("p_name", planName);
            values.put("p_fav",String.valueOf(pFav));

            rowId = sqDB.insert("plan",null,values);
        }
        //return getLastInsertID("plan");
        Toast.makeText(context, planName + " Plan gespeichert", Toast.LENGTH_SHORT).show(); //todo String im Stringeditor erstellen
        return rowId;
    }

    //Plan löschen!!
    public void deletePlan(long id){
        Cursor cursor = sqDB.rawQuery("Delete From plan Where id = ?",new String[]{String.valueOf(id)});
        cursor.moveToFirst();
        cursor.close();
    }

    /**
     * Methoden für Tabel user
     */
    public int countUser(){
        Cursor cursor = sqDB.rawQuery("Select * From user",null);
        return cursor.getCount();
    }

    /**
     * Methoden für Table pref
     */
    public Cursor getPref(String key){
        Cursor cursor = sqDB.rawQuery("Select * From pref Where key = ?", new String[]{key});
        return cursor;
    }

    public void setPref(String key, String text, int intWert){
        Cursor c = sqDB.rawQuery("Update pref Set text = ?, bool = ? Where key = ?",
                new String[]{text, String.valueOf(intWert), key});
        c.moveToFirst();
        c.close();
    }

    /**
     * Methoden für Table geraet
     */
    public int countGeraet(){
        Cursor cursor = sqDB.rawQuery("Select * From geraet", null);
        int count = cursor.getCount();
        return count;
    }

    //Alle Geräte auslesen
    public Cursor getAllFromGeraete(){
        Cursor cursor = sqDB.rawQuery("Select * From geraet",null);
        return cursor;
    }

    //Gerät nach Id auslesen
    public Cursor getGeraetById(long id){
        Cursor cursor = sqDB.rawQuery("Select * From geraet Where id = ?", new String[]{String.valueOf(id)});
        return cursor;
    }

    //Gerät löschen
    public boolean deleteGeraet(long id){
        Cursor cursor = sqDB.rawQuery("Delete From geraet Where id = ?", new String[]{String.valueOf(id)});
        try {
            cursor.moveToFirst();
        }
        catch (SQLException e){
            Log.e("DBSource","deleteGeraet() "+e.getMessage());
            cursor.close();
            return false;
        }
        finally {
            cursor.close();
        }
        return true;
    }

    //Gerät updaten
    public void setGeraetUpdate(long id, String gName, String gEinstellungen,
                                String gSetsWiederh, String gGewicht){
        Cursor cursor = sqDB.rawQuery("Update geraet Set " +
                "g_name = ?, " +
                "g_einstellungen = ?, " +
                "g_setwiederholungen = ?, " +
                "g_gewicht = ? " +
                "Where id = ?",
                new String[]{gName, gEinstellungen, gSetsWiederh, gGewicht, String.valueOf(id)});
        try{
            cursor.moveToFirst();
        }
        catch (SQLException e){
            Log.e("DBSource","setGeraetUpdate() "+e.getMessage());
        }
        finally {
            cursor.close();
        }

    }

    //Neues Gerät eintragen
    public long neuesGeraetErstellen(String gName, Integer gPosition, String gEinstellungen,
                                     String gSetsWiederholungen, String gGewicht){
        long lastId = 0;
        ContentValues values = new ContentValues();
        if(countUser() == 0){
            values.put("g_name",gName);
            values.put("g_position", gPosition);
            values.put("g_einstellungen", gEinstellungen);
            values.put("g_setwiederholungen",gSetsWiederholungen);
            values.put("g_gewicht",gGewicht);
            try{
                lastId = sqDB.insert("geraet",null,values);
            }
            catch (Exception e){
                Log.e("neuesGeraetErstellen()","Fehler beim Eintragen");
            }
        }
        else {

        }
        return lastId;
    }

    /**
     * Methoden für Tabel vergindung
     */

    //Count verbingung nach planId
    public int getVerbindungCountNachPlanId(long planId){
        Cursor cursor = sqDB.rawQuery("Select * From verbindung Where v_p_id = ?",
                new String[]{String.valueOf(planId)});
        return cursor.getCount();
    }

    //Select alle Geräte von einem zugewiesenen Plan
    public Cursor getAlleGeräteVonEinemPlan(long planId){
        Cursor cursor = sqDB.rawQuery("SELECT verbindung.v_p_id," +
                " verbindung.v_g_id," +
                " geraet.id," +
                " geraet.user_id," +
                " geraet.g_name," +
                " geraet.g_position," +
                " geraet.g_einstellungen," +
                " geraet.g_setwiederholungen," +
                " geraet.g_gewicht" +
                " FROM verbindung" +
                " INNER JOIN geraet ON verbindung.v_g_id = geraet.id" +
                " Where verbindung.v_p_id = ?",
                new String[]{String.valueOf(planId)});
        return cursor;
    }

    //weist ein Gerät einem Plan zu
    public void setAddGeraetToPlan(long planId, long geraetId){
        Cursor cursor = sqDB.rawQuery("Insert Into verbindung (v_p_id, v_g_id) Values (?, ?)"
                ,new String[]{String.valueOf(planId), String.valueOf(geraetId)});

        cursor.moveToFirst();
        cursor.close();
    }

    //Geräet von einem Plan löschen
    public void delGereatVonEinemPlan(long planId, long geraetId){
        Cursor cursor = sqDB.rawQuery("Delete From verbindung Where v_p_id = ? And v_g_id = ?",
                new String[]{String.valueOf(planId), String.valueOf(geraetId)});
        cursor.moveToFirst();
        cursor.close();
    }

}
