package ch.msacchi.gymplaner;


import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class NeuesGeraetErstellen extends Fragment {
    private View v;
    private EditText gName, gEinstellungen, gSetsWiederholungen, gGewicht;
    private TextView speichern, abbrechen;
    private ImageButton delete;
    private String gStatus = "neu";
    private DBSource db;
    private long lastId, gId, pId;
    private Fragment fragment;


    public NeuesGeraetErstellen() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_neues_geraet_erstellen, container, false);
        if(getArguments() != null){
            gStatus = getArguments().getString("gStatus");
            gId = getArguments().getLong("gId");
            pId = getArguments().getLong("pId");
        }
        else{
            gStatus = "neu";
        }
        gName = (EditText)v.findViewById(R.id.etGeraetName);
        gEinstellungen = (EditText)v.findViewById(R.id.etGeraetEinstellungen);
        gSetsWiederholungen = (EditText)v.findViewById(R.id.etGeraetSetsWiederholungen);
        gGewicht = (EditText)v.findViewById(R.id.etGeraetGewicht);
        speichern = (TextView)v.findViewById(R.id.tvGeraetSpeichern);
        abbrechen = (TextView)v.findViewById(R.id.tvGeraetAbbrechen);
        delete = (ImageButton)v.findViewById(R.id.ibGeraetDelete);
        db = new DBSource(getContext());

        abbrechen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zurGeraeteListe();
            }
        });

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        switch (gStatus){
            case "zuweisen":
            case "neu":
                speichern.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if("".equals(gName.getText().toString())){
                            //ToDo String von tost in die String xml
                            Toast.makeText(getContext(),"Gerätename darf nicht leer sein!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        db.openDB();
                        lastId = db.neuesGeraetErstellen(gName.getText().toString(),
                                    0,
                                    gEinstellungen.getText().toString(),
                                    gSetsWiederholungen.getText().toString(),
                                    gGewicht.getText().toString());
                            if (gStatus.equals("zuweisen")) {
                                db.setAddGeraetToPlan(pId,lastId);
                            }
                            db.closeDB();
                        if(lastId == 0){
                            //ToDo String von tost in die String xml
                            Toast.makeText(getContext(),"Fehler beim Speichern!", Toast.LENGTH_SHORT).show();
                        }
                        else if(gStatus.equals("zuweisen")){
                            ZumPlanMitId();
                        }
                        else{
                            zurGeraeteListe();
                        }


                    }
                });
                break;
            case "planbearbeiten":
            case "bearbeiten":
                db.openDB();
                Cursor cursor = db.getGeraetById(gId);
                cursor.moveToFirst();
                gName.setText(cursor.getString(cursor.getColumnIndex("g_name")));
                gEinstellungen.setText(cursor.getString(cursor.getColumnIndex("g_einstellungen")));
                gSetsWiederholungen.setText(cursor.getString(cursor.getColumnIndex("g_setwiederholungen")));
                gGewicht.setText(cursor.getString(cursor.getColumnIndex("g_gewicht")));
                cursor.close();
                db.closeDB();

                speichern.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        db.openDB();
                        db.setGeraetUpdate(gId, gName.getText().toString(),
                                gEinstellungen.getText().toString(),
                                gSetsWiederholungen.getText().toString(),
                                gGewicht.getText().toString());

                        Toast.makeText(getContext(),
                                getString(R.string.toast_XY_wurde_gespeichert, gName.getText().toString()),
                                Toast.LENGTH_SHORT).show();
                        if(gStatus.equals("planbearbeiten")){
                            ZumPlanMitId();
                        }else {
                            zurGeraeteListe();
                        }

                    }
                });
                db.closeDB();
                break;
        }
    }

    private void zurGeraeteListe(){
        db.openDB();
        db.setPref("homeliste","",1);
        db.closeDB();
        new ChangeFragmentVFragment().goToFragment(new Home(),
                getString(R.string.label_home_geraete),
                getActivity(),getFragmentManager());
    }

    private void ZumPlanMitId(){
        Bundle bundle = new Bundle();
        PlanNavigation planNavigation = new PlanNavigation();
        bundle.putLong("id", pId);
        planNavigation.setArguments(bundle);
        new ChangeFragmentVFragment().goToFragment(planNavigation,
                getString(R.string.label_plan_navi),
                getActivity(),
                getFragmentManager());
    }
}
