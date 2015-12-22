package net.wandroid.badooconvert;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.gson.Gson;

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
 * A placeholder fragment containing a simple view.
 */
public class ProductFragment extends Fragment {

    public static final String FIRST_SET_TRANSACTIONS_JSON = "first_set/transactions.json";
    public static final String NR_TRANSACTIONS = "nrTransactions";
    public static final String SKU = "sku";

    private ListView mProductListView;
    private ListAdapter mListAdapter;
    private Map<String, ArrayList<Transaction>> mTransactionMap = new HashMap<>();
    private IProductFragmentListener mProductFragmentListener;

    public ProductFragment() {
    }

    public static ProductFragment newInstance() {
        return new ProductFragment();
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
        mListAdapter = getAdapter();
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

    private SimpleAdapter getAdapter() {
        List<Transaction> transactions = null;
        try {
            transactions = Arrays.asList(loadJsonFromFile(FIRST_SET_TRANSACTIONS_JSON, getActivity().getAssets()));
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

    private Transaction[] loadJsonFromFile(String path, AssetManager assetManager) throws IOException {
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

    public interface IProductFragmentListener {
        void onItemClicked(String sku, ArrayList<Transaction> transactions);
    }
}
