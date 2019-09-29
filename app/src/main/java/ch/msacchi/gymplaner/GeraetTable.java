package ch.msacchi.gymplaner;

/**
 * Created by Marco on 04.06.2017.
 */

public class GeraetTable {
    public long g_id;
    public Integer user_id, g_position;
    public String g_name, g_einstellungen, g_setwiederholungen, g_gewicht;

    public GeraetTable(){}

    public GeraetTable(long id,
                       Integer user_id,
                       String g_name,
                       Integer g_position,
                       String g_einstellungen,
                       String g_setwiederholungen,
                       String g_gewicht){
        this.g_id = id;
        this.user_id = user_id;
        this.g_name = g_name;
        this.g_position = g_position;
        this.g_einstellungen = g_einstellungen;
        this.g_setwiederholungen = g_setwiederholungen;
        this.g_gewicht = g_gewicht;
    }

    public GeraetTable(long id,
                       String g_name,
                       Integer g_position){
        this.g_id = id;
        this.g_name = g_name;
        this.g_position = g_position;
    }
}
