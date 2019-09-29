package ch.msacchi.gymplaner;

import android.content.Context;
import android.database.Cursor;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.zip.Inflater;

/**
 * Created by Marco on 06.06.2017.
 */

public class GeraeteMeinAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private ArrayList<GeraetTable> arrayList;
    private DBSource db;
    private boolean hinweisZigen = true;

    public GeraeteMeinAdapter(Context context){
        inflater = LayoutInflater.from(context);
        db = new DBSource(context);
        arrayList = creatGeraeteArrayListe();
    }

    public GeraeteMeinAdapter(Context context, boolean hinweisZigen){
        inflater = LayoutInflater.from(context);
        db = new DBSource(context);
        arrayList = creatGeraeteArrayListe();
        this.hinweisZigen = hinweisZigen;
    }

    public GeraeteMeinAdapter(Context context, long planId, boolean verbindungAussortieren
            ,boolean hinweisZigen){
        inflater = LayoutInflater.from(context);
        db = new DBSource(context);
        if(verbindungAussortieren){
            arrayList = OhnePlanZugewiesnenGeraeten(planId);
        }else {
            arrayList = creatGeraeteArrayListe(planId);
        }

        this.hinweisZigen = hinweisZigen;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return arrayList.get(position).g_id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if(convertView == null){
            convertView = inflater.inflate(R.layout.geraet_adapter, parent, false);
            holder = new Holder();
            holder.gName = (TextView)convertView.findViewById(R.id.adabterGName);
            holder.gPos = (TextView)convertView.findViewById(R.id.geraet_pos);
            holder.gHinweis = (TextView)convertView.findViewById(R.id.adabterGInfo);
            convertView.setTag(holder);
        }
        else {
            holder = (Holder) convertView.getTag();
        }

        holder.gName.setText(arrayList.get(position).g_name);
        if(arrayList.get(position).g_position != 0){
            holder.gPos.setText(arrayList.get(position).g_position);
        }
        if(!hinweisZigen){
            holder.gHinweis.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    private static class Holder{
        TextView gName, gPos, gHinweis;
    }

    private ArrayList<GeraetTable> creatGeraeteArrayListe(){
        db.openDB();
        ArrayList<GeraetTable> arrayList = new ArrayList<>();
        Cursor allFromGeraete = db.getAllFromGeraete();
        for(allFromGeraete.moveToFirst(); !allFromGeraete.isAfterLast(); allFromGeraete.moveToNext()){
            String gName;
            Integer gPos;
            long gId;
            gName = allFromGeraete.getString(allFromGeraete.getColumnIndex("g_name"));
            gPos = allFromGeraete.getInt(allFromGeraete.getColumnIndex("g_position"));
            gId = allFromGeraete.getLong(allFromGeraete.getColumnIndex("id"));
            arrayList.add(new GeraetTable(gId ,gName, gPos));
        }
        db.closeDB();
        return arrayList;
    }

    //Liste für einem Plan zugewiesenen Geräte
    private ArrayList<GeraetTable> creatGeraeteArrayListe(long planId){
        db.openDB();
        ArrayList<GeraetTable> arrayList = new ArrayList<>();
        Cursor allFromGeraete = db.getAlleGeräteVonEinemPlan(planId);
        for(allFromGeraete.moveToFirst(); !allFromGeraete.isAfterLast(); allFromGeraete.moveToNext()){
            String gName;
            Integer gPos;
            long gId;
            gName = allFromGeraete.getString(allFromGeraete.getColumnIndex("g_name"));
            gPos = allFromGeraete.getInt(allFromGeraete.getColumnIndex("g_position"));
            gId = allFromGeraete.getLong(allFromGeraete.getColumnIndex("id"));
            arrayList.add(new GeraetTable(gId ,gName, gPos));
        }
        db.closeDB();
        return arrayList;
    }

    //Liste ohne dem Plan zugewiesenen Geräten
    private ArrayList<GeraetTable> OhnePlanZugewiesnenGeraeten(long planId){
        db.openDB();
        ArrayList<GeraetTable> arrayList = new ArrayList<>();


        Cursor allFromGeraete = db.getAllFromGeraete();
        String gName;
        Integer gPos;
        long gId;
        boolean put;
        for(allFromGeraete.moveToFirst(); !allFromGeraete.isAfterLast(); allFromGeraete.moveToNext()){
            put = true;
            gName = allFromGeraete.getString(allFromGeraete.getColumnIndex("g_name"));
            gPos = allFromGeraete.getInt(allFromGeraete.getColumnIndex("g_position"));
            gId = allFromGeraete.getLong(allFromGeraete.getColumnIndex("id"));

            Cursor geraeteVonPlan = db.getAlleGeräteVonEinemPlan(planId);
            for(geraeteVonPlan.moveToFirst(); !geraeteVonPlan.isAfterLast(); geraeteVonPlan.moveToNext()){
                long gid2;
                gid2 =  geraeteVonPlan.getLong(geraeteVonPlan.getColumnIndex("id"));
                if(gid2 == gId){
                    put = false;
                }
            }
            if(put) {
                arrayList.add(new GeraetTable(gId, gName, gPos));
            }
        }
        db.closeDB();
        return arrayList;
    }
}
