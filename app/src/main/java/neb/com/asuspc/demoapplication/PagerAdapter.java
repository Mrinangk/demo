package neb.com.asuspc.demoapplication;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class PagerAdapter extends FragmentPagerAdapter {

    private int noOfTabs;

    public PagerAdapter(FragmentManager fm,int noOfTabs) {
        super(fm);
        this.noOfTabs = noOfTabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new UsersFragment();
            case 1:
                return new EnrollFragment();
            default:
                return null;
        }
    }
    @Override
    public int getCount() {
        return noOfTabs;
    }
}
