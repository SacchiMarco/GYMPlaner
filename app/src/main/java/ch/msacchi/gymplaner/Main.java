package ch.msacchi.gymplaner;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class Main extends AppCompatActivity implements View.OnClickListener {
    ImageButton HamburgerButton, homeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        changeFragmentLabel(new Home(), getString(R.string.label_home));

        HamburgerButton = (ImageButton)findViewById(R.id.HamburgerMenu);
        homeButton = (ImageButton)findViewById(R.id.headerHomeButton);
        HamburgerButton.setOnClickListener(this);
        homeButton.setOnClickListener(this);


    }

    //Fragment und Label wechseln
    public void changeFragmentLabel(Fragment fragmentClass, String label){
        TextView headerLabel = (TextView)findViewById(R.id.HeaderLabel);
        //headerLabel Animation
        AlphaAnimation fadeIn = new AlphaAnimation(0.0f , 1.0f ) ;
        AlphaAnimation fadeOut = new AlphaAnimation( 1.0f , 0.0f ) ;
        fadeIn.setDuration(1200);
        fadeIn.setFillAfter(true);
        fadeOut.setDuration(1200);
        fadeOut.setFillAfter(true);
        fadeOut.setStartOffset(4200+fadeIn.getStartOffset());

        headerLabel.startAnimation(fadeOut);
        headerLabel.setText(label);
        headerLabel.startAnimation(fadeIn);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.setCustomAnimations(R.anim.fade_in,R.anim.fade_out)
                .replace(R.id.content,fragmentClass)
                .commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.HamburgerMenu:
                PopupMenu popupMenu = new PopupMenu(this,HamburgerButton);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu,popupMenu.getMenu());

                MenuBuilder menuBuilder = (MenuBuilder)popupMenu.getMenu();
                MenuPopupHelper menuPopupHelper = new MenuPopupHelper(this,menuBuilder,v);
                menuPopupHelper.setForceShowIcon(true);
                menuPopupHelper.show();

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.homeButton:
                                changeFragmentLabel(new Home(), getString(R.string.label_home));
                                break;
                            case R.id.plan_neu:
                                changeFragmentLabel(new NeuenPlanErstellen(), getString(R.string.label_planerstellen));
                                break;
                            case R.id.geraet_neu:
                                NeuesGeraetErstellen neuesGeraetErstellen = new NeuesGeraetErstellen();
                                Bundle bundle = new Bundle();
                                bundle.putString("gStatus","neu");
                                neuesGeraetErstellen.setArguments(bundle);
                                changeFragmentLabel(neuesGeraetErstellen, getString(R.string.lable_geraeterstellen));
                                break;
                        }
                        return false;
                    }
                });
                break;
            case R.id.headerHomeButton:
                changeFragmentLabel(new Home(), getString(R.string.label_home));
                break;
        }
    }


    public void GoToErstellen(View v){
        changeFragmentLabel(new NeuenPlanErstellen(), getString(R.string.label_planerstellen));
    }

    public void goToGeraetErstellen(View v){
        changeFragmentLabel(new NeuesGeraetErstellen(), getString(R.string.lable_geraeterstellen));
    }


    @Override
    public void onBackPressed() {
        int backStackEntry = getSupportFragmentManager().getBackStackEntryCount();
        if(backStackEntry == 1) {
            //ToDo Doppel Backpress hinzufügen um zu beenden...
            //ToDo weis nicht ob mir BackStack gefällt in der App
            finish();
        }
        else{
            if (getFragmentManager().getBackStackEntryCount() > 1) {
                getFragmentManager().popBackStack();
            } else {
                super.onBackPressed();
            }

        }
    }
}
