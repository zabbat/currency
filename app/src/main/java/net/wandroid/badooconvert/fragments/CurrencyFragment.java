package net.wandroid.badooconvert.fragments;


import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.gson.Gson;

import net.wandroid.badooconvert.MainActivity;
import net.wandroid.badooconvert.R;
import net.wandroid.badooconvert.json.Rate;
import net.wandroid.badooconvert.json.Transaction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class CurrencyFragment extends Fragment {


    public static final String KEY_SKU = "KEY_SKU";
    public static final String KEY_TRANSACTIONS = "KEY_TRANSACTIONS";
    public static final String LOCAL_CURRENCY = "LOCAL_CURRENCY";
    public static final String GBP = "GBP";
    public static final String DEFAULT_CURRENCY = "GBP";

    private ListView mCurrencyListView;
    private TextView mTotalTextView;

    /**
     * Contains all currency conversion nodes. The key is the from name (ex USD) and the value is
     * a map with to as key (ex GBP) and the conversion rate as value.
     */
    private Map<String, Map<String, Double>> mConvertNodeMap = new HashMap<>();

    private View mView;

    /**
     * List of all transactions for this product
     */
    private List<Transaction> mTransactions = new ArrayList<>();

    public CurrencyFragment() {
    }


    /**
     * Creates a CurrencyFragment
     *
     * @param sku          the sku
     * @param transactions the transaktions for the sku
     * @return CurrencyFragment
     */
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

        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_currency, container, false);

            mCurrencyListView = (ListView) mView.findViewById(R.id.currency_list);
            mTotalTextView = (TextView) mView.findViewById(R.id.total_text);

            Bundle args = getArguments();
            if (args != null) {
                if (args.containsKey(KEY_SKU)) {
                    AppCompatActivity activity = (AppCompatActivity) getActivity();
                    activity.getSupportActionBar().setTitle(args.getString(KEY_SKU));
                    activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                }
                if (args.containsKey(KEY_TRANSACTIONS)) {
                    mTransactions = (List<Transaction>) args.getSerializable(KEY_TRANSACTIONS);
                }
            }
        }
        return mView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initList();
    }

    /**
     * Initiate the list of all transactions for the sku. This will do a lot of calculation and read from file and should
     * preferable be run in background
     */
    private void initList() {

        try {
            Rate[] rates = loadJsonFromFile(MainActivity.DATA_SET_RATES_JSON, getActivity().getAssets());
            if (rates != null) {
                createConvertMap(rates);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<Map<String, String>> simpleList = new ArrayList<>();
        double total = 0;
        for (Transaction t : mTransactions) {
            Map<String, String> listMapping = new HashMap<>();
            listMapping.put(LOCAL_CURRENCY, t.getAmount() + " " + t.getCurrency());
            try {
                double d = recursiveStart(t.getCurrency(), DEFAULT_CURRENCY);
                double amount = t.getAmount() * d;
                listMapping.put(GBP, amount + " " + DEFAULT_CURRENCY);
                total += amount;
            } catch (IllegalArgumentException e) {
                listMapping.put(GBP, getString(R.string.no_currency_available));
            }
            simpleList.add(listMapping);
        }
        mTotalTextView.setText(getString(R.string.total_str, total, DEFAULT_CURRENCY));
        SimpleAdapter adapter = new SimpleAdapter(getContext(), simpleList, android.R.layout.simple_list_item_2,
                new String[]{LOCAL_CURRENCY, GBP}, new int[]{android.R.id.text1, android.R.id.text2});
        mCurrencyListView.setAdapter(adapter);

    }

    /**
     * Populates mConvertNodeMap
     *
     * @param rates the rates to be used
     */
    private void createConvertMap(Rate[] rates) {
        for (Rate r : rates) {
            Map<String, Double> toMap;
            if (mConvertNodeMap.containsKey(r.getFrom())) {
                toMap = mConvertNodeMap.get(r.getFrom());
            } else {
                toMap = new HashMap<>();
                mConvertNodeMap.put(r.getFrom(), toMap);
            }
            toMap.put(r.getTo(), r.getRate());
        }

    }


    /**
     * Loads Rate from json file in the assets.
     *
     * @param path         the asset path. Example if you have a file in assets/a/b.json, then the path should be "a/b.json"
     * @param assetManager the AssetManager
     * @return All rates. Can be null
     * @throws IOException
     */
    private
    @Nullable
    Rate[] loadJsonFromFile(String path, AssetManager assetManager) throws IOException {
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

    /**
     * Starts recursive depth first search for currency
     * Will throw IllegalArgumentException if there is no possible conversion
     * Same currency will always be converted to 1
     *
     * @param from from currency
     * @param to   to currency
     * @return the rate that should be used as "from *rate = to"
     */
    private double recursiveStart(String from, String to) {
        if (from.equals(to)) {
            return 1;
        }
        return recursive(from, to, 1, new HashSet<Pair<String, String>>());
    }

    /**
     * Recursive depth first method for finding the rate
     * Will throw illegalArgumentException if there is no possible conversion
     *
     * @param from    from currency
     * @param to      to currency
     * @param rate    the rate
     * @param visited set containing already visited conversions. This graph can be cyclic.
     * @return the rate
     */
    private double recursive(String from, String to, double rate, Set<Pair<String, String>> visited) {
        Map<String, Double> toMap = mConvertNodeMap.get(from);
        if (toMap != null) {
            for (String key : toMap.keySet()) {
                if (visited.contains(new Pair<>(from, key))) { // already visited, skip.
                    continue;
                }
                if (key.equals(to)) { //we are done
                    return toMap.get(key) * rate;
                } else {
                    visited.add(new Pair<>(from, key));
                    return recursive(key, to, rate * toMap.get(key), visited);
                }
            }
        }
        throw new IllegalArgumentException("Could not convert " + from + "  to " + to);
    }


}
