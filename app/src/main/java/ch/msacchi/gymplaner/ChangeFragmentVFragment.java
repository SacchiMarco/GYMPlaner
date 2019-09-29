package ch.msacchi.gymplaner;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;

/**
 * Created by Marco on 25.04.2017.
 */

public class ChangeFragmentVFragment {

    public void goToFragment(Fragment fragmentClass, String label, Activity activity, FragmentManager getFragmentManager){
        TextView headerLabel = (TextView) activity.findViewById(R.id.HeaderLabel);
        //headerLabel Animation
        AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
        fadeIn.setDuration(1200);
        fadeIn.setFillAfter(true);
        fadeOut.setDuration(1200);
        fadeOut.setFillAfter(true);
        fadeOut.setStartOffset(4200 + fadeIn.getStartOffset());

        headerLabel.startAnimation(fadeOut);
        headerLabel.setText(label);
        headerLabel.startAnimation(fadeIn);

        FragmentManager fragmentManager = getFragmentManager;
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .replace(R.id.content, fragmentClass)
                .commit();
    }

}
