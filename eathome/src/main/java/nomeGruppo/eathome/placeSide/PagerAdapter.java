package nomeGruppo.eathome.placeSide;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import nomeGruppo.eathome.actors.Place;

public class PagerAdapter extends FragmentStatePagerAdapter {
    private int tabCount;
    private Place place;

    public PagerAdapter(FragmentManager fragmentManager,int tabCount, Place place){
        super(fragmentManager);
        this.tabCount=tabCount;
        this.place=place;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new TabToday(place);
            case 1:
                return new TabPrevious(place);
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return this.tabCount;
    }
}
