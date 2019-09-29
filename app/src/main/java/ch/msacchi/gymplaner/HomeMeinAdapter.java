package ch.msacchi.gymplaner;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Marco on 29.04.2017.
 */

public class HomeMeinAdapter extends BaseAdapter {

    private ArrayList<PlanTable> plan;
    private LayoutInflater inflater;
    private DBSource db;

    public HomeMeinAdapter(Context context){
        inflater = LayoutInflater.from(context);
        db = new DBSource(context);
        plan = creatHomeArrayListe();
    }

    private ArrayList<PlanTable> creatHomeArrayListe() {
        ArrayList<PlanTable> plan = new ArrayList<>();
        db.openDB();
        Cursor allFormPlan = db.allFormPlan();
        for(allFormPlan.moveToFirst();!allFormPlan.isAfterLast();allFormPlan.moveToNext()){
            Integer p_id, user_id, p_fav, p_visability;
            String p_name;

            p_id = allFormPlan.getInt(allFormPlan.getColumnIndex("id"));
            p_name = allFormPlan.getString(allFormPlan.getColumnIndex("p_name"));
            user_id = allFormPlan.getInt(allFormPlan.getColumnIndex("user_id"));
            p_fav = allFormPlan.getInt(allFormPlan.getColumnIndex("p_fav"));
            p_visability = allFormPlan.getInt(allFormPlan.getColumnIndex("p_visability"));

            plan.add(new PlanTable(p_id, user_id, p_name, p_fav,p_visability));
        }
        db.closeDB();
        return plan;
    }

    @Override
    public int getCount() {
        return plan.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        //Setzt die plan.id als Klickrückgabe long
        return plan.get(position).p_id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            convertView = inflater.inflate(R.layout.home_fundapter,parent,false);
            holder = new ViewHolder();
            holder.name = (TextView)convertView.findViewById(R.id.txtHomePlanName);
            holder.fav = (ImageView)convertView.findViewById(R.id.imgHomeFav);
            convertView.setTag(holder);
        }
        else{
            holder =(ViewHolder) convertView.getTag();
        }

        holder.name.setText(plan.get(position).p_name);
        //Zeigt den Stern nur wenn alls Favorit markiert
        if(plan.get(position).p_fav == 1){
            holder.fav.setImageResource(R.drawable.stern);
        }
        else{
            //Transparentes png einfügen um fehler mit der ListView zu vermeiden.
            holder.fav.setImageResource(R.drawable.invis_pic);
        }

        return convertView;
    }

    private static class ViewHolder{
        TextView name;
        ImageView fav;

    }
}
