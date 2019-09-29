package ch.msacchi.gymplaner;

import android.view.View;



/**
 * Created by Marco on 27.04.2017.
 */

public class PlanTable {
    public Integer p_id, user_id, p_fav, p_visability;
    public String p_name;

    public PlanTable(Integer p_id, Integer user_id, String p_name, Integer p_fav, Integer p_visability){
        this.p_id = p_id;
        this.user_id = user_id;
        this.p_name = p_name;
        this.p_fav = p_fav;
        this.p_visability = p_visability;
    }

    public PlanTable(){

    }


}
