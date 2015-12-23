package net.wandroid.badooconvert.fragments;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.gson.Gson;

import net.wandroid.badooconvert.MainActivity;
import net.wandroid.badooconvert.R;
import net.wandroid.badooconvert.json.Transaction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A fragment that displays number transactions for all products
 */
public class TransactionFragment extends Fragment {


    public static final String NR_TRANSACTIONS = "nrTransactions";
    public static final String SKU = "sku";

    private ListView mProductListView;
    private ListAdapter mListAdapter;
    /**
     * Contains has of products as key and a list of transactions as value
     */
    private Map<String, ArrayList<Transaction>> mTransactionMap = new HashMap<>();
    private IProductFragmentListener mProductFragmentListener;

    public TransactionFragment() {
    }

    public static TransactionFragment newInstance() {
        return new TransactionFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_products, container, false);

        mProductListView = (ListView) view.findViewById(R.id.product_list);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListAdapter = getAdapter(MainActivity.DATA_SET_TRANSACTIONS_JSON);
        mProductListView.setAdapter(mListAdapter);
        mProductListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, String> productMap = (Map<String, String>) mListAdapter.getItem(position);
                if (mProductFragmentListener != null) {
                    String sku = productMap.get(SKU);
                    mProductFragmentListener.onItemClicked(sku, mTransactionMap.get(sku));
                }
            }
        });
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setTitle(R.string.app_name);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof IProductFragmentListener) {
            mProductFragmentListener = (IProductFragmentListener) context;
        }
    }

    @Override
    public void onDetach() {
        mProductFragmentListener = null;
        super.onDetach();
    }

    /**
     * Creates a SimpleAdapter, filled with data parsed from json
     * This will do calculations and read from file and should preferable be run from background
     *
     * @param jsonPath the asset path. Example if you have a file in assets/a/b.json, then the path should be "a/b.json"
     * @return The adapter.
     */
    private SimpleAdapter getAdapter(String jsonPath) {
        List<Transaction> transactions = null;
        try {
            Transaction[] tmp = loadJsonFromFile(jsonPath, getActivity().getAssets());
            if (tmp != null) {
                transactions = Arrays.asList(tmp);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (transactions != null) {
            mTransactionMap = new HashMap<>();
            ArrayList<Transaction> transactionList = null;
            for (Transaction t : transactions) {
                if (mTransactionMap.containsKey(t.getSku())) {
                    transactionList = mTransactionMap.get(t.getSku());
                } else {
                    transactionList = new ArrayList();
                    mTransactionMap.put(t.getSku(), transactionList);
                }
                transactionList.add(t);

            }
        }

        ArrayList<Map<String, String>> list = new ArrayList<>();
        for (String key : mTransactionMap.keySet()) {
            Map<String, String> map = new HashMap();
            map.put(SKU, key);
            map.put(NR_TRANSACTIONS, Integer.toString(mTransactionMap.get(key).size()));
            list.add(map);
        }

        String[] from = new String[]{SKU, NR_TRANSACTIONS};
        int[] to = new int[]{android.R.id.text1, android.R.id.text2};
        SimpleAdapter adapter = new SimpleAdapter(getContext(), list, android.R.layout.simple_list_item_2, from, to);
        return adapter;

    }

    /**
     * Loads Transaction from json.
     *
     * @param path         path to the file in the asset folder
     * @param assetManager assetManager
     * @return the Transactions. Can be null.
     * @throws IOException
     */
    private
    @Nullable
    Transaction[] loadJsonFromFile(String path, AssetManager assetManager) throws IOException {
        InputStream is = null;
        try {
            is = assetManager.open(path);
            Gson gson = new Gson();
            Transaction[] transactions = gson.fromJson(new BufferedReader(new InputStreamReader(is)), Transaction[].class);
            return transactions;
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    /**
     * Interface to interact with an Activity
     */
    public interface IProductFragmentListener {
        /**
         * triggered when an item in the list is clicked.
         *
         * @param sku          the sku of the transaction
         * @param transactions all transactions for the sku as a list
         */
        void onItemClicked(String sku, ArrayList<Transaction> transactions);
    }
}
