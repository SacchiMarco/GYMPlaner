package ch.msacchi.gymplaner;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class Home extends Fragment {
    private DBSource sqDB;
    private TextView buttonListeWechseln;
    private Integer homeListeInt;
    private TextView headerLabel;
    private View v;
    private ListView homeListView;
    private FloatingActionButton floatButton;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private void listWechselnButton(){
        headerLabel = (TextView) getActivity().findViewById(R.id.HeaderLabel);
        buttonListeWechseln = (TextView)v.findViewById(R.id.tvButtonListeWechseln);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_home, container, false);
        homeListView = (ListView)v.findViewById(R.id.lvHome);
        floatButton = (FloatingActionButton) v.findViewById(R.id.home_floatingButton);
        listWechselnButton();
        sqDB = new DBSource(v.getContext());
        sqDB.openDB();

        Cursor homeliste = sqDB.getPref("homeliste");
        homeliste.moveToFirst();
        homeListeInt = homeliste.getInt(homeliste.getColumnIndex("bool"));
        if( homeListeInt == 0){
            buttonListeWechseln.setText(R.string.txtButton_GraeteListe);
            headerLabel.setText(R.string.label_home_plaene);

            if(sqDB.countPlan() == 0){
                //Zeige keine Pläne vorhanden.
                v = inflater.inflate(R.layout.fragment_kein_plan_vorhanden, container, false);
                listWechselnButton();
                buttonListeWechseln.setText(R.string.txtButton_GraeteListe);
                headerLabel.setText(R.string.label_home_plaene);

            }
            else{
                //Wenn Pläne vorhanden Lade liste
                homeListView.setAdapter(new HomeMeinAdapter(getContext()));
                registerForContextMenu(homeListView);

                //Planliste bei Klick Plannavigation öffnen.
                homeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        /**
                         * @param id -> liefert Datenbanktable plan.id zurück.
                         */
                        Bundle bundle = new Bundle();
                        bundle.putLong("id", id);
                        PlanNavigation planNavigation = new PlanNavigation();
                        planNavigation.setArguments(bundle);
                        new ChangeFragmentVFragment().goToFragment(planNavigation,
                                getString(R.string.label_plan_navi),
                                getActivity(),
                                getFragmentManager());
                    }
                });
            }
            sqDB.closeDB();
        }
        else {
            listWechselnButton();
            buttonListeWechseln.setText(R.string.txtButton_PlanListe);
            headerLabel.setText(R.string.label_home_geraete);
            sqDB.openDB();
            if(sqDB.countGeraet() == 0){
                //Zeige Fragment kein/e Gerät/e vorhanden.
                v = inflater.inflate(R.layout.fragment_kein_geraet_vorhanden,container,false);
                listWechselnButton();
                buttonListeWechseln.setText(R.string.txtButton_PlanListe);
                headerLabel.setText(R.string.label_home_geraete);
            }
            else {
                homeListView.setAdapter(new GeraeteMeinAdapter(getContext()));

                //ToDo Marco: Clicklistener
                //Dropdownmenu
                registerForContextMenu(homeListView);

            }
            sqDB.closeDB();
        }


        sqDB.closeDB();
        return v;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater menuInflater = new MenuInflater(getContext());
        menuInflater.inflate(R.menu.geraeteliste_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        if(homeListeInt ==1){
            switch (item.getItemId()){
                case R.id.g_menu_bearbeiten:
                    String id = String.valueOf(info.id);
                    Bundle bundle = new Bundle();
                    bundle.putString("gStatus","bearbeiten");
                    bundle.putLong("gId",info.id);
                    NeuesGeraetErstellen neuesGeraetErstellen = new NeuesGeraetErstellen();
                    neuesGeraetErstellen.setArguments(bundle);
                    new ChangeFragmentVFragment().goToFragment(neuesGeraetErstellen,
                            getString(R.string.label_geraet_bearbeiten),
                            getActivity(),
                            getFragmentManager());
                    break;
                case R.id.g_menu_starten:
                    //ToDo Marco: Gerät starten
                    break;
                case R.id.g_menu_delete:
                    sqDB.openDB();
                    Cursor cursor = sqDB.getGeraetById(info.id);
                    cursor.moveToFirst();
                    final String tempgName = cursor.getString(cursor.getColumnIndex("g_name"));
                    AlertDialog.Builder delete = new AlertDialog.Builder(v.getContext());
                    delete.setTitle(R.string.dialog_Löschen)
                            .setMessage(getString(R.string.dialog_GerätWirklichDeleten,tempgName))
                            .setPositiveButton(R.string.dialog_Ja, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    sqDB.openDB();
                                    if(sqDB.deleteGeraet(info.id)){
                                        Toast.makeText(getContext(),
                                                getString(R.string.toast_XY_gelöscht, tempgName),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                    sqDB.closeDB();
                                    new ChangeFragmentVFragment().goToFragment(new Home(),
                                            getString(R.string.label_home_geraete),
                                            getActivity(), getFragmentManager());
                                }
                            })
                            .setNegativeButton(R.string.dialog_Nein, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog finalDialog = delete.create();
                    finalDialog.show();
                    cursor.close();
                    sqDB.closeDB();
                    break;
            }
        }else if(homeListeInt == 0){
            switch (item.getItemId()){
                case R.id.g_menu_bearbeiten:
                    break;
                case R.id.g_menu_starten:
                    break;
                case R.id.g_menu_delete:
                    break;
            }
        }

        return super.onContextItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        //Wechseln zwischen Geräte- und Pläneliste.
        buttonListeWechseln.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId() == R.id.tvButtonListeWechseln){
                    if(homeListeInt == 0){
                        sqDB.openDB();
                        sqDB.setPref("homeliste","",1);
                        new ChangeFragmentVFragment().goToFragment(new Home(),
                                getString(R.string.label_home),
                                getActivity(),
                                getFragmentManager());
                        sqDB.closeDB();
                    }
                    else if(homeListeInt == 1){
                        sqDB.openDB();
                        sqDB.setPref("homeliste","",0);
                        new ChangeFragmentVFragment().goToFragment(new Home(),
                                getString(R.string.label_home),
                                getActivity(),
                                getFragmentManager());
                        sqDB.closeDB();
                    }

                }
            }
        });

        floatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId() == R.id.home_floatingButton){
                    if(homeListeInt == 0){
                        new ChangeFragmentVFragment().goToFragment(new NeuenPlanErstellen(),
                                getString(R.string.label_planerstellen),
                                getActivity(),
                                getFragmentManager());
                    }
                    else if(homeListeInt == 1){
                        new ChangeFragmentVFragment().goToFragment(new NeuesGeraetErstellen(),
                                getString(R.string.lable_geraeterstellen),
                                getActivity(),
                                getFragmentManager());
                    }
                }
            }
        });

    }
}
