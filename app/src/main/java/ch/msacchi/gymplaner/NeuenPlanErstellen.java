package ch.msacchi.gymplaner;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class NeuenPlanErstellen extends Fragment {
    TextView test;
    private EditText planName;
    private CheckBox planFav;
    private  long lastInsertID;

    public NeuenPlanErstellen() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_neuen_plan_erstellen, container, false);

        /**
         * nach insert jump zu Gerät hinzufügen -> id von T-Plan mitübergeben.
         */
        planName = (EditText) v.findViewById(R.id.eTneuerPlanName);
        planFav = (CheckBox)v.findViewById(R.id.cBplanFav);
        test = (TextView)v.findViewById(R.id.neuenPlanSpeichern);
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.neuenPlanSpeichern) {
                    if (planName.getText().toString().equals("") || planName.getText().toString().equals(null)) {
                        Toast.makeText(getContext(), "Planname darf nicht leer sein!", Toast.LENGTH_LONG).show();
                    } else {
                        DBSource sqDB = new DBSource(getContext());
                        sqDB.openDB();
                        lastInsertID = sqDB.euenPlanErstellen(planName.getText().toString(), planFav.isChecked());
                        Log.i("Neuer Plan-> ID ", ""+lastInsertID);
                        sqDB.closeDB();
                        //gehe zu Fragment Plan Navigation -> lastId übergeben (lastInsertID)
                        Bundle daten = new Bundle();
                        daten.putLong("id", lastInsertID);
                        PlanNavigation planNavigation = new PlanNavigation();
                        planNavigation.setArguments(daten);
                        new ChangeFragmentVFragment().goToFragment(planNavigation,
                                                    getString(R.string.label_plan_navi),
                                                    getActivity(),
                                                    getFragmentManager());
                    }
                }
            }
        });
        return v;
    }



}
