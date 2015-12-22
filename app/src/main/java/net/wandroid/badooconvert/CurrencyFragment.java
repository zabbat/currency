package net.wandroid.badooconvert;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class CurrencyFragment extends Fragment {


    public CurrencyFragment() {
    }


    public static CurrencyFragment newInstance() {
        return new CurrencyFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_currency, container, false);
    }


}
