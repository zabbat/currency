package net.wandroid.badooconvert;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.gson.Gson;

import net.wandroid.badooconvert.json.Product;

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
    private ListView mProductListView;
    private ListAdapter mListAdapter;

    public ProductFragment() {
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
    }

    private SimpleAdapter getAdapter() {
        List<Product> products = null;
        try {
            products = Arrays.asList(loadJsonFromFile(FIRST_SET_TRANSACTIONS_JSON, getActivity().getAssets()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<Map<String, String>> list = new ArrayList<>();
        if (products != null) {
            for (Product p : products) {
                Map<String, String> map = new HashMap();
                map.put(Product.SKU, p.getSku());
                map.put(Product.AMOUNT, p.getAmount());
                list.add(map);
            }
        }

        String[] from = new String[]{Product.SKU, Product.AMOUNT};
        int[] to = new int[]{android.R.id.text1, android.R.id.text2};
        SimpleAdapter adapter = new SimpleAdapter(getContext(), list, android.R.layout.simple_list_item_2, from, to);
        return adapter;

    }

    private Product[] loadJsonFromFile(String path, AssetManager assetManager) throws IOException {
        InputStream is = null;
        try {
            is = assetManager.open(path);
            Gson gson = new Gson();
            Product[] products = gson.fromJson(new BufferedReader(new InputStreamReader(is)), Product[].class);
            return products;
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }
}
