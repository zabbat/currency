package net.wandroid.badooconvert;


import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.gson.Gson;

import net.wandroid.badooconvert.json.Rate;
import net.wandroid.badooconvert.json.Transaction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CurrencyFragment extends Fragment {


    public static final String KEY_SKU = "KEY_SKU";
    public static final String KEY_TRANSACTIONS = "KEY_TRANSACTIONS";
    public static final String LOCAL_CURRENCY = "LOCAL_CURRENCY";
    public static final String GBP = "GBP";

    private ListView mCurrencyListView;
    private TextView mTotalTextView;
    private Map<String,Double> mCurrencyMap = new HashMap<>();

    private List<Transaction> mTransactions = new ArrayList<>();

    public CurrencyFragment() {
    }


    public static CurrencyFragment newInstance(String sku, ArrayList<Transaction> transactions) {
        Bundle args = new Bundle();
        args.putString(KEY_SKU, sku);
        args.putSerializable(KEY_TRANSACTIONS, transactions);
        CurrencyFragment fragment = new CurrencyFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_currency, container, false);

        mCurrencyListView = (ListView) view.findViewById(R.id.currency_list);
        mTotalTextView = (TextView) view.findViewById(R.id.total_text);

        Bundle args = getArguments();
        if (args != null) {
            if (args.containsKey(KEY_SKU)) {

            }
            if (args.containsKey(KEY_TRANSACTIONS)) {
                mTransactions = (List<Transaction>) args.getSerializable(KEY_TRANSACTIONS);
            }
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initList();
    }

    private void initList() {

        try {
            Rate[] rates = loadJsonFromFile("first_set/rates.json",getActivity().getAssets());
            if(rates!=null){
                for(Rate r: rates){
                    
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<Map<String, String>> simpleList = new ArrayList<>();
        for (Transaction t : mTransactions) {
            Map<String, String> listMapping = new HashMap<>();
            listMapping.put(LOCAL_CURRENCY, t.getCurrency());
            listMapping.put(GBP, "subtitle");
            simpleList.add(listMapping);
        }
        SimpleAdapter adapter = new SimpleAdapter(getContext(), simpleList, android.R.layout.simple_list_item_2,
                new String[]{LOCAL_CURRENCY, GBP}, new int[]{android.R.id.text1, android.R.id.text2});
        mCurrencyListView.setAdapter(adapter);

    }


    private Rate[] loadJsonFromFile(String path, AssetManager assetManager) throws IOException {
        InputStream is = null;
        try {
            is = assetManager.open(path);
            Gson gson = new Gson();
            Rate[] rates = gson.fromJson(new BufferedReader(new InputStreamReader(is)), Rate[].class);
            return rates;
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }


}
