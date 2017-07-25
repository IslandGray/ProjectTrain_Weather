package com.wcp.weathertest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.ExpandedMenuView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.wcp.adapter.CityAdapter;
import com.wcp.adapter.TravelAdapter;
import com.wcp.data.CityData;

import java.util.ArrayList;
import java.util.List;

public class AddCity extends AppCompatActivity {
    private EditText NewCity;

    private RecyclerView CityList;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<CityData> Cities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_city);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        this.setTitle("变更城市");
        NewCity = (EditText) findViewById(R.id.NewCity);
        CityList=(RecyclerView)findViewById(R.id.citylist);

        CityList.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(AddCity.this);
        CityList.setLayoutManager(mLayoutManager);

        Cities=new ArrayList<>();
        Cities.add(new CityData("省份","城市"));
        mAdapter = new CityAdapter(Cities,NewCity,AddCity.this);
        CityList.setAdapter(mAdapter);
        CityList.setItemAnimator(new DefaultItemAnimator());

        NewCity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.i("TAG","before");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    Cities.clear();
                    String input = NewCity.getText().toString();
                    Log.i("TAG","Input:"+input);
                    String[] provinces = getResources().getStringArray(R.array.province);
                    TypedArray pros = getResources().obtainTypedArray(R.array.pros);
                    for (int i = 0; i < pros.length(); i++) {
                        String[] cities = getResources().getStringArray(pros.getResourceId(i, -1));
                        for (Object x : cities) {
                            Log.i("TAG","get:"+x);
                            if (((String) x).length()>=input.length() && ((String) x).substring(0, input.length()).equals(input)) {
                                Cities.add(new CityData(provinces[i], (String) x));
                            }
                        }
                    }
                    mAdapter.notifyDataSetChanged();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        /*
        Confirm = (Button) findViewById(R.id.changecity);
        Confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = getIntent();
                Bundle bundle = intent.getExtras();
                bundle.putCharSequence("newcity", NewCity.getText().toString());
                intent.putExtras(bundle);

                setResult(0x111, intent);
                finish();
            }
        });
        */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.single_back_and_save, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home: {
                finish();
                break;
            }
            case R.id.save:{
                //TODO:更改城市
                final Intent intent = getIntent();
                Bundle bundle = intent.getExtras();
                bundle.putCharSequence("newcity", NewCity.getText().toString());
                intent.putExtras(bundle);

                setResult(0x111, intent);
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
