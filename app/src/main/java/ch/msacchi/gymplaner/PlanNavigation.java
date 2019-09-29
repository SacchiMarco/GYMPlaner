package ch.msacchi.gymplaner;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlanNavigation extends Fragment {
    private long planId;
    private DBSource db;
    private String planName;
    private TextView tvPlanName;
    private ImageButton ibPlanDelete;
    private View v;
    private EditText etDialogPlanName;
    private Switch switchPlanFav;
    private ListView lvPlanNavi, lvDialogGeraete;
    private FloatingActionButton floatingButton;
    private AlertDialog dialogGeraetHinzu, dialogGeraeteLv;


    public PlanNavigation() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_plan_navigation, container, false);

        /**
         * @param planId -> verwenden für die Abfrage DB plan.id
         */
        if( getArguments() != null){
          planId = getArguments().getLong("id");
        }

        db = new DBSource(getContext());
        tvPlanName = (TextView)v.findViewById(R.id.tv_plan_navi_name);
        ibPlanDelete = (ImageButton)v.findViewById(R.id.iv_plan_nav_plan_delete);
        switchPlanFav = (Switch)v.findViewById(R.id.sw_plan_navi_fav);
        lvPlanNavi = (ListView)v.findViewById(R.id.lv_plan_navi);
        floatingButton = (FloatingActionButton)v.findViewById(R.id.fab_planNavi);

        //Plan Name und Favoritenstatus auslesen
        db.openDB();
        Cursor c = db.getPlanById(planId);
        c.moveToFirst();
        planName = c.getString(c.getColumnIndex("p_name"));
        Integer planFav = c.getInt(c.getColumnIndex("p_fav"));
        tvPlanName.setText(planName);
        db.closeDB();
        if(planFav == 1){
            switchPlanFav.setChecked(true);
        }
        else{
            switchPlanFav.setChecked(false);
        }

        //ListView der zugewiesenen Geräte initialisieren
        db.openDB();
        lvPlanNavi.setAdapter(new GeraeteMeinAdapter(getContext(), planId, false , true));
        lvPlanNavi.setEmptyView(v.findViewById(R.id.emptyElement));
        registerForContextMenu(lvPlanNavi);

        db.closeDB();

        return v;
    }

    //ToDo Starten des Trainingsplanes
    //ToDo hinzufügen von Trainingsgeräten
    //ToDo T-Geräte Liste klick -> Gerät bearbeiten

    @Override
    public void onStart() {
        super.onStart();
        tvPlanName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialogPlanName = new AlertDialog.Builder(getContext());
                final View myDialog;
                final LayoutInflater inflater1 = getActivity().getLayoutInflater();
                myDialog = inflater1.inflate(R.layout.dialog_plan_namen_change,null);
                etDialogPlanName = (EditText)myDialog.findViewById(R.id.etDialog_PlanName);
                dialogPlanName.setView(myDialog)
                        .setTitle(R.string.dialog_planTitel_titel)
                        .setPositiveButton(R.string.dialog_planName_speichern, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                String neuerPlanName = etDialogPlanName.getText().toString();
                                db.openDB();
                                db.updatePlanName(planId, neuerPlanName);
                                tvPlanName.setText(neuerPlanName);
                            }
                        })
                        .setNegativeButton(R.string.dialog_planName_abbrechen, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog dialog = dialogPlanName.create();
                dialog.show();
                etDialogPlanName.setText(tvPlanName.getText());


            }
        });

        ibPlanDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialogPlanDelete = new AlertDialog.Builder(getContext());
                dialogPlanDelete
                        .setTitle(R.string.txtButton_PlanLöschen)
                        .setMessage(getString(R.string.dialog_PlanWirklichDeleten,planName))
                        //Positive Antwort Plan löschen -> Nach gelschtem Plan zu home sprigen
                        .setPositiveButton(R.string.dialog_Ja, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                db.openDB();
                                db.deletePlan(planId);
                                db.closeDB();
                                Toast.makeText(getContext(),getString(R.string.toast_XY_gelöscht, planName),Toast.LENGTH_SHORT).show();
                                new ChangeFragmentVFragment().goToFragment(new Home(), getString(R.string.label_home) ,getActivity(),getFragmentManager());
                            }
                        })
                        .setNegativeButton(R.string.dialog_Nein, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog dialogPD = dialogPlanDelete.create();
                dialogPD.show();

            }
        });

        switchPlanFav.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                db.openDB();
                db.updateFavoritStatus(planId,isChecked);
                db.closeDB();
            }
        });

        floatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] choice = new String[]{getString(R.string.txtButton_VorhandenesGeraetHinzufügen), getString(R.string.txtButton_NeueGeraetErstellen)};
                final AlertDialog.Builder alerBuilder = new AlertDialog.Builder(getContext());
                alerBuilder.setTitle(getString(R.string.dialog_GeraetHInzufügen))
                        .setMultiChoiceItems(choice, null, new DialogInterface.OnMultiChoiceClickListener() {
<<<<<<<
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                switch (which){
                                    case 0:
                                        db.openDB();
                                        if(db.countGeraet() <= 0){
                                            Toast.makeText(getContext(),getString(R.string.kein_geraet_vorhanden)
                                            ,Toast.LENGTH_SHORT).show();
                                            NeuesGeraetErstellenUndZuweisen();
                                            dialogGeraetHinzu.cancel();
                                            return;
                                        }
                                        final View myDialogView;
                                        final LayoutInflater inflater = getActivity().getLayoutInflater();
                                        myDialogView = inflater.inflate(R.layout.dialog_geraet_lv, null);
                                        lvDialogGeraete = (ListView)myDialogView.findViewById(R.id.lv_dialog_gereat);
                                        lvDialogGeraete.setAdapter(new GeraeteMeinAdapter(getContext(),planId, true, false));
                                        AlertDialog.Builder adBuilder = new AlertDialog.Builder(getContext());
                                        adBuilder.setView(myDialogView);

                                        dialogGeraeteLv = adBuilder.create();
                                        dialogGeraeteLv.show();
                                        LvDialogGeraeteClickListener();
=======
                                        @Override
                                        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                            switch (which){
                                                case 0:
                                        Toast.makeText(getContext(),"Vorhandes",Toast.LENGTH_SHORT).show();
>>>>>>>
                                        dialogGeraetHinzu.cancel();
                                        break;
                                    case 1:
                                        NeuesGeraetErstellenUndZuweisen();
                                        dialogGeraetHinzu.cancel();
                                        break;
                                }
                            }
                        })
                        .setNegativeButton(R.string.dialog_planName_abbrechen, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialogGeraetHinzu.cancel();
                            }
                        });
                dialogGeraetHinzu = alerBuilder.create();
                dialogGeraetHinzu.show();
            }
        });
    }

    private void LvDialogGeraeteClickListener(){
        lvDialogGeraete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                db.openDB();
                db.setAddGeraetToPlan(planId,id);
                db.closeDB();
                dialogGeraeteLv.cancel();
                lvPlanNavi.setAdapter(new GeraeteMeinAdapter(getContext(),planId, false, true));
            }
        });
    }

    private void NeuesGeraetErstellenUndZuweisen(){
        NeuesGeraetErstellen geraetErstellen = new NeuesGeraetErstellen();
        Bundle bundle = new Bundle();
        bundle.putString("gStatus", "zuweisen");
        bundle.putLong("pId", planId);
        geraetErstellen.setArguments(bundle);
        new ChangeFragmentVFragment().goToFragment(geraetErstellen,
                getString(R.string.lable_geraeterstellen),
                getActivity(),
                getFragmentManager());
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater menuInflater = new MenuInflater(getContext());
        menuInflater.inflate(R.menu.plannavi_geraeteliste_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)
                item.getMenuInfo();
        switch (item.getItemId()){
            case R.id.g_menu_bearbeiten:
                String id = String.valueOf(info.id);
                Bundle bundle = new Bundle();
                bundle.putString("gStatus","planbearbeiten");
                bundle.putLong("gId",info.id);
                bundle.putLong("pId", planId);
                NeuesGeraetErstellen neuesGeraetErstellen = new NeuesGeraetErstellen();
                neuesGeraetErstellen.setArguments(bundle);
                new ChangeFragmentVFragment().goToFragment(neuesGeraetErstellen,
                        getString(R.string.label_geraet_bearbeiten),
                        getActivity(),
                        getFragmentManager());
                break;
            case R.id.g_menu_delete:
                db.openDB();
                Cursor cursor = db.getGeraetById(info.id);
                cursor.moveToFirst();
                final String tempgName = cursor.getString(cursor.getColumnIndex("g_name"));
                AlertDialog.Builder delete = new AlertDialog.Builder(v.getContext());
                delete.setTitle(R.string.dialog_Löschen)
                        .setMessage(getString(R.string.dialog_GerätWirklichDeleten,tempgName))
                        .setPositiveButton(R.string.dialog_Ja, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                db.openDB();
                                db.delGereatVonEinemPlan(planId, info.id);
                                db.closeDB();
                                lvPlanNavi.setAdapter(new GeraeteMeinAdapter(getContext(), planId, false, true));
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
                db.closeDB();
                break;
        }
        return super.onContextItemSelected(item);
    }
}
