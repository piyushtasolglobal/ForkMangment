package com.forkmang.fragment;

import static com.forkmang.helper.Constant.MOBILE;
import static com.forkmang.helper.Constant.SUCCESS_CODE;
import static com.forkmang.helper.Constant.TOKEN_LOGIN;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.forkmang.R;
import com.forkmang.activity.Activity_PaymentSummary;
import com.forkmang.adapter.PickupFoodList_Adapter;
import com.forkmang.adapter.PickupListingAdapter;
import com.forkmang.data.CartBooking;
import com.forkmang.data.Category_ItemList;
import com.forkmang.data.Extra_Topping;
import com.forkmang.data.RestoData;
import com.forkmang.helper.Constant;
import com.forkmang.helper.StorePrefrence;
import com.forkmang.helper.Utils;
import com.forkmang.network_call.Api;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PickupSelect_Food_Fragment extends Fragment {

    public  static PickupSelect_Food_Fragment instance_pickup;
    RecyclerView recyclerView;
    static String category_id ;
    ArrayList<Category_ItemList> category_itemLists;
    ArrayList<Extra_Topping> extra_toppingArrayList;
    public ArrayList<CartBooking> cartBookingArrayList;
    String booking_id="0";
    int selectedId_radiobtn_topping;
    StorePrefrence storePrefrence;
    static RestoData restoData;
    ProgressBar progressBar;
    ProgressBar progressBar_alertview;
    PickupFoodList_Adapter all_orderFood_adapter;
    RecyclerView recycleView;

    public static PickupSelect_Food_Fragment newInstance(/*TableList tableList,*/ RestoData bookTable) {
        //category_id = category_id_val;
        //Log.d("idval",category_id);
        //tableList_get = tableList;
        restoData = bookTable;
        return new PickupSelect_Food_Fragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orderfood_layout, container, false);
        instance_pickup = this;
        storePrefrence=new StorePrefrence(getContext());
        progressBar = view.findViewById(R.id.progressBar);
        recyclerView = view.findViewById(R.id.order_food_recycleview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;

    }

    @Override
    public void onResume() {
        super.onResume();
       // callApi_addtocart("1","2","10","1,2");
       // callApi_fooditem(category_id);
    }

    public void callApi_fooditem(String category_id)
    {
        //Toast.makeText(getContext(),"CategoryID->"+category_id,Toast.LENGTH_SHORT).show();
        progressBar.setVisibility(View.VISIBLE);
        Api.getInfo().getres_catitemlist(category_id).
                enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        try{
                            //Log.d("Result", jsonObject.toString());
                            if(response.code() == Constant.SUCCESS_CODE_n)
                            {
                                JSONObject jsonObject = new JSONObject(new Gson().toJson(response.body()));
                                if(jsonObject.getString("status").equalsIgnoreCase(SUCCESS_CODE))
                                {

                                    category_itemLists = new ArrayList<>();

                                    JSONArray mjson_arr = jsonObject.getJSONArray("data");

                                    for(int i = 0 ; i < mjson_arr.length(); i++)
                                    {
                                        Category_ItemList category_itemList = new Category_ItemList();

                                        JSONObject mjson_obj = mjson_arr.getJSONObject(i);
                                        category_itemList.setId(mjson_obj.getString("id"));
                                        category_itemList.setCategory_id(mjson_obj.getString("category_id"));
                                        category_itemList.setName(mjson_obj.getString("name"));
                                        category_itemList.setPrice(mjson_obj.getString("price"));
                                        category_itemList.setImage(mjson_obj.getString("image"));



                                        JSONArray mjson_arr_extra = mjson_obj.getJSONArray("extra");
                                        extra_toppingArrayList = new ArrayList<>();
                                        for(int j = 0; j<mjson_arr_extra.length(); j++)
                                        {
                                            JSONObject mjson_obj_extra = mjson_arr_extra.getJSONObject(j);

                                            Extra_Topping extra_topping = new Extra_Topping();

                                            extra_topping.setId(mjson_obj_extra.getString("id"));
                                            extra_topping.setItem_id(mjson_obj_extra.getString("item_id"));
                                            extra_topping.setName(mjson_obj_extra.getString("name"));
                                            extra_topping.setPrice(mjson_obj_extra.getString("price"));
                                            extra_toppingArrayList.add(extra_topping);

                                        }
                                        category_itemList.setExtra_toppingArrayList(extra_toppingArrayList);
                                        category_itemLists.add(category_itemList);

                                    }

                                    progressBar.setVisibility(View.GONE);
                                    all_orderFood_adapter = new PickupFoodList_Adapter(getContext(),getActivity(), category_itemLists, PickupSelect_Food_Fragment.this,restoData);
                                    recyclerView.setAdapter(all_orderFood_adapter);



                                    /*if(all_orderFood_adapter == null)
                                    {
                                        all_orderFood_adapter = new All_Food_Adapter(getContext(),getActivity(), category_itemLists, Select_Food_Fragment.this);
                                        recyclerView.setAdapter(all_orderFood_adapter);
                                    }
                                    else{
                                        all_orderFood_adapter.notifyDataSetChanged();
                                    }*/

                                }

                            }
                            else if(response.code() == Constant.ERROR_CODE)
                            {
                                JSONObject jsonObject = new JSONObject(response.errorBody().string());
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                        catch (Exception ex)
                        {
                            ex.printStackTrace();
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "Error occur please try again", Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Toast.makeText(getContext(), "Error occur please try again", Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);

                    }
                });
    }


    public void callApi_searchfooditem(String category_id,String search_item)
    {
        //Toast.makeText(getContext(),"CategoryID->"+category_id,Toast.LENGTH_SHORT).show();
        progressBar.setVisibility(View.VISIBLE);
        Api.getInfo().getres_catitemlist_search(category_id,search_item).
                enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        try{
                            //Log.d("Result", jsonObject.toString());
                            if(response.code() == Constant.SUCCESS_CODE_n)
                            {
                                JSONObject jsonObject = new JSONObject(new Gson().toJson(response.body()));
                                if(jsonObject.getString("status").equalsIgnoreCase(SUCCESS_CODE))
                                {

                                    category_itemLists = new ArrayList<>();
                                    JSONArray mjson_arr = jsonObject.getJSONArray("data");
                                    for(int i = 0 ; i < mjson_arr.length(); i++)
                                    {
                                        Category_ItemList category_itemList = new Category_ItemList();
                                        JSONObject mjson_obj = mjson_arr.getJSONObject(i);
                                        category_itemList.setId(mjson_obj.getString("id"));
                                        category_itemList.setCategory_id(mjson_obj.getString("category_id"));
                                        category_itemList.setName(mjson_obj.getString("name"));
                                        category_itemList.setPrice(mjson_obj.getString("price"));
                                        category_itemList.setImage(mjson_obj.getString("image"));
                                        JSONArray mjson_arr_extra = mjson_obj.getJSONArray("extra");
                                        extra_toppingArrayList = new ArrayList<>();
                                        for(int j = 0; j<mjson_arr_extra.length(); j++)
                                        {
                                            JSONObject mjson_obj_extra = mjson_arr_extra.getJSONObject(j);
                                            Extra_Topping extra_topping = new Extra_Topping();
                                            extra_topping.setId(mjson_obj_extra.getString("id"));
                                            extra_topping.setItem_id(mjson_obj_extra.getString("item_id"));
                                            extra_topping.setName(mjson_obj_extra.getString("name"));
                                            extra_topping.setPrice(mjson_obj_extra.getString("price"));
                                            extra_toppingArrayList.add(extra_topping);
                                        }
                                        category_itemList.setExtra_toppingArrayList(extra_toppingArrayList);
                                        category_itemLists.add(category_itemList);

                                    }
                                    progressBar.setVisibility(View.GONE);
                                    all_orderFood_adapter = new PickupFoodList_Adapter(getContext(),getActivity(), category_itemLists, PickupSelect_Food_Fragment.this,restoData);
                                    recyclerView.setAdapter(all_orderFood_adapter);
                                }

                            }
                            else if(response.code() == Constant.ERROR_CODE)
                            {
                                JSONObject jsonObject = new JSONObject(response.errorBody().string());
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                        catch (Exception ex)
                        {
                            ex.printStackTrace();
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "Error occur please try again", Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Toast.makeText(getContext(), "Error occur please try again", Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);

                    }
                });
    }



    public void callApi_addtocart(String item_id, String qty, String booking_table_id, String item_extra,String type)
     {
        //showProgress();
        progressBar.setVisibility(View.VISIBLE);
        Api.getInfo().additem_cart("Bearer "+ storePrefrence.getString(TOKEN_LOGIN),item_id, qty, booking_table_id, item_extra,storePrefrence.getString(Constant.IDENTFIER),type).
                enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        try{
                            //Log.d("Result", jsonObject.toString());
                            if(response.code() == Constant.SUCCESS_CODE_n)
                            {
                                JSONObject jsonObject = new JSONObject(new Gson().toJson(response.body()));
                                if(jsonObject.getString("status").equalsIgnoreCase("200"))
                                {
                                    Toast.makeText(getContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                                    storePrefrence.setString(Constant.CARTID, jsonObject.getJSONObject("data").getString("cart_id"));
                                    storePrefrence.setString(Constant.CART_ITEMID, jsonObject.getJSONObject("data").getString("item_id"));
                                    progressBar.setVisibility(View.GONE);

                                    cartListingView();
                                }
                            }
                            else if(response.code() == Constant.ERROR_CODE_n || response.code() == Constant.ERROR_CODE)
                            {
                                JSONObject jsonObject = new JSONObject(response.errorBody().string());
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(getContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();

                                /*JSONObject obj = new JSONObject(loadJSONFromAsset());
                                if(obj.getString("status").equalsIgnoreCase("200"))
                                {
                                    Toast.makeText(getContext(), obj.getString("message")+" offline ", Toast.LENGTH_LONG).show();
                                    storePrefrence.setString("cartid", obj.getJSONObject("data").getString("cart_id"));
                                    storePrefrence.setString("item_id", obj.getJSONObject("data").getString("item_id"));
                                    cartListingView();

                                }*/

                            }
                        }
                        catch (Exception ex)
                        {
                            ex.printStackTrace();
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "Error occur please try again", Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Toast.makeText(getContext(), "Error occur please try again", Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                        //stopProgress();

                    }
                });
    }

    public void callApi_food_1(String category_id, String booking_id)
    {
        if (Utils.isNetworkAvailable(getContext())) {
            this.booking_id = booking_id;
            callApi_fooditem((category_id));

        }
        else{
            Toast.makeText(getContext(), Constant.NETWORKEROORMSG, Toast.LENGTH_SHORT).show();
        }
    }

    public  void showAlertView(Category_ItemList category_itemList)
    {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.addqty_alertview, null);
        alertDialog.setView(dialogView);
        alertDialog.setCancelable(true);
        final AlertDialog dialog = alertDialog.create();
        Button btn_add,btn_reserve ;
        TextView plus_btn, txt_qty, minus1;
        LinearLayout lyt;
        ArrayList<String>radio_btn_id_arr = new ArrayList<>();


        RadioButton radioButton4_extra, radioButton5_extra, radioButton6_extra;
        /*radioButton4_extra=dialogView.findViewById(R.id.radioButton4);
        radioButton5_extra=dialogView.findViewById(R.id.radioButton5);
        radioButton6_extra=dialogView.findViewById(R.id.radioButton6);

        ArrayList<Extra_Topping> extra_toppingArrayList_get;
        extra_toppingArrayList_get = category_itemList.getExtra_toppingArrayList();
        for(int i=0;i<extra_toppingArrayList_get.size(); i++)
        {
            Extra_Topping extra_topping = extra_toppingArrayList_get.get(i);
            radioButton4_extra

        }*/

        ArrayList<Extra_Topping> extra_toppingArrayList_get;
        extra_toppingArrayList_get = category_itemList.getExtra_toppingArrayList();
        final RadioButton[] rb = new RadioButton[extra_toppingArrayList_get.size()];

        RadioGroup rg = new RadioGroup(getContext()); //create the RadioGroup
        rg.setOrientation(RadioGroup.VERTICAL);//or RadioGroup.VERTICAL

        LinearLayout layout2 = new LinearLayout(getContext());
        layout2.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        layout2.setOrientation(LinearLayout.VERTICAL);
        //rg.setBackgroundColor(Color.parseColor("#3F51B5"));



        ColorStateList colorStateList = new ColorStateList(
                new int[][]
                        {
                                new int[]{-android.R.attr.state_enabled}, // Disabled
                                new int[]{android.R.attr.state_enabled}   // Enabled
                        },
                new int[]
                        {
                                Color.parseColor("#000000"), // disabled
                                Color.parseColor("#C91107")   // enabled
                        }
        );

        for(int i=0;i<extra_toppingArrayList_get.size(); i++)
        {
            Extra_Topping extra_topping = extra_toppingArrayList_get.get(i);
            rb[i]  = new RadioButton(getContext());
            rb[i].setText(extra_topping.getName());
            rb[i].setTextColor(getResources().getColor(R.color.black));

            rb[i].setButtonTintList(colorStateList);
            rb[i].setId(Integer.parseInt(extra_topping.getId()));

            //rg.addView(rb[i]);
            layout2.addView(rb[i]);

            rb[i].setOnCheckedChangeListener((buttonView, isChecked) -> {
                if(buttonView.isChecked())
                {
                    radio_btn_id_arr.add(String.valueOf(buttonView.getId()));
                }
            });
        }

        lyt=dialogView.findViewById(R.id.lyt);
        lyt.addView(layout2);//you add the whole RadioGroup to the layout
        //rg.setOnCheckedChangeListener((group, checkedId) -> selectedId_radiobtn_topping = rg.getCheckedRadioButtonId());

        btn_add=dialogView.findViewById(R.id.btn_add);
        btn_reserve=dialogView.findViewById(R.id.btn_reserve);
        plus_btn=dialogView.findViewById(R.id.plus_btn);
        minus1=dialogView.findViewById(R.id.minus_btn);
        txt_qty=dialogView.findViewById(R.id.txt_qty);

        plus_btn.setOnClickListener(v -> {
            int value = Integer.parseInt(txt_qty.getText().toString());
            ++value;
            txt_qty.setText(String.valueOf(value));
        });

        minus1.setOnClickListener(v -> {
            int value = Integer.parseInt(txt_qty.getText().toString());
            --value;
            txt_qty.setText(String.valueOf(value));
        });

         btn_add.setOnClickListener(v -> {
           // dialog.dismiss();
           String item_id = category_itemList.getId();
           String qty = txt_qty.getText().toString();
           //String extra ="1,2";
            String extra ="";

           for(int i = 0; i<radio_btn_id_arr.size(); i++)
           {
               if(i==0)
               {
                   extra = radio_btn_id_arr.get(i);
               }
               else{
                   extra = extra+ "," + radio_btn_id_arr.get(i);
               }
           }

           //Log.d("booking_id", booking_id);
            radio_btn_id_arr.clear();
           if(extra.length()==0)
           {
               extra="1,2"; //hardcoded please correct it
           }

           Log.d("extra", extra);
           Log.d("qty", qty);
           Log.d("item_id", item_id);
           Log.d("selectedId", ""+selectedId_radiobtn_topping);

           //api call
            if (Utils.isNetworkAvailable(getContext())) {
                dialog.dismiss();

                callApi_addtocart(item_id,qty,"",extra, "pickup");

            }
            else{
                Toast.makeText(getContext(), Constant.NETWORKEROORMSG, Toast.LENGTH_SHORT).show();
             }
        });

        btn_reserve.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }


    public void cartListingView()
    {
        final Dialog dialog = new Dialog(getActivity(),R.style.FullHeightDialog);
        dialog.setContentView(R.layout.cartview_alertview_2);

        if (dialog.getWindow() != null){
            dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        }

        Button btn_pay_table_food,btn_pay_table;
        ImageView img_close;
        TextView txt_restroname,txt_custname,txt_datetime, txt_phoneno;
        EditText etv_noperson;
        LinearLayout linear_view1, linear_view_layout_2;

        linear_view1 =dialog.findViewById(R.id.linear_view1);
        linear_view1.setVisibility(View.GONE);

       // linear_view_layout_2 =dialog.findViewById(R.id.linear_view_layout_2);
       // linear_view_layout_2.setVisibility(View.GONE);

        txt_restroname=dialog.findViewById(R.id.txt_restroname);
        txt_custname=dialog.findViewById(R.id.txt_custname);
        txt_datetime=dialog.findViewById(R.id.txt_datetime);
        txt_phoneno=dialog.findViewById(R.id.txt_phoneno);
        etv_noperson=dialog.findViewById(R.id.etv_noperson);
        recycleView = dialog.findViewById(R.id.recycleview);
        btn_pay_table_food=dialog.findViewById(R.id.btn_pay_table_food);
        btn_pay_table=dialog.findViewById(R.id.btn_pay_table);
        img_close=dialog.findViewById(R.id.img_close);
        progressBar_alertview=dialog.findViewById(R.id.progressBar_alertview);

        txt_restroname.setText(restoData.getRest_name());
        txt_custname.setText(storePrefrence.getString(Constant.NAME));
        txt_phoneno.setText(storePrefrence.getString(MOBILE));
        //txt_datetime.setText(tableList_get.getStr_time());


        img_close.setOnClickListener(v -> {
            dialog.dismiss();
        });

        recycleView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if(Utils.isNetworkAvailable(getContext()))
        {
            callApi_cartListview();
        }
        else{
            Toast.makeText(getContext(),Constant.NETWORKEROORMSG,Toast.LENGTH_SHORT).show();
        }



        btn_pay_table_food.setOnClickListener(v -> {
            dialog.dismiss();
            final Intent mainIntent = new Intent(getContext(), Activity_PaymentSummary.class);
            //Bundle bundle = new Bundle();
            //bundle.putParcelableArrayList("cartbookingarraylist", cartBookingArrayList);
            mainIntent.putExtra("comingfrom", "PickupFood");
            mainIntent.putExtra("restromodel", restoData);
            startActivity(mainIntent);
            //getActivity().finish();



        });

        btn_pay_table.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        dialog.show();

    }


    //Fragment Instance
    public static PickupSelect_Food_Fragment GetInstance()
    {
        return instance_pickup;
    }


    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getActivity().getAssets().open("local2.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public String loadJSONFromAsset_t() {
        String json = null;
        try {
            InputStream is = getActivity().getAssets().open("local4.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }


    public void callApi_cartListview()
    {
        //showProgress();
        progressBar_alertview.setVisibility(View.VISIBLE);
        Api.getInfo().getcart_detail("Bearer "+storePrefrence.getString(TOKEN_LOGIN), storePrefrence.getString(Constant.IDENTFIER)).
                enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        try{
                            //Log.d("Result", jsonObject.toString());
                            if(response.code() == Constant.SUCCESS_CODE_n)
                            {
                                JSONObject obj = new JSONObject(new Gson().toJson(response.body()));
                                if(obj.getString("status").equalsIgnoreCase("200"))
                                {
                                    JSONObject data_obj = obj.getJSONObject("data");
                                    JSONArray cart_array_item = data_obj.getJSONArray("cart_item");
                                    cartBookingArrayList = new ArrayList<>();
                                    for(int i = 0; i<cart_array_item.length(); i++)
                                    {
                                        CartBooking cartBooking = new CartBooking();

                                        JSONObject cart_detail_obj = cart_array_item.getJSONObject(i);


                                        //data obj
                                        cartBooking.setData_userid(data_obj.getString("user_id"));

                                        if(data_obj.has("booking_table_id"))
                                        {
                                            cartBooking.setData_booking_table_id(data_obj.getString("booking_table_id"));
                                        }
                                        else{
                                            cartBooking.setData_booking_table_id("");
                                        }


                                        cartBooking.setData_total(data_obj.getString("total"));
                                        //cart_item obj
                                        cartBooking.setCart_item_qty(cart_detail_obj.getString("qty"));
                                        cartBooking.setCart_item_cartid(cart_detail_obj.getString("cart_id"));
                                        cartBooking.setCart_item_id(cart_detail_obj.getString("id"));

                                        if(cart_detail_obj.has("item_extra_id"))
                                        {
                                            cartBooking.setCart_item_extra_id(cart_detail_obj.getString("item_extra_id"));
                                        }
                                        else{
                                            cartBooking.setCart_item_extra_id("0");
                                        }
                                        //cart_item_details obj
                                        cartBooking.setCart_item_details_category_id(cart_detail_obj.getJSONObject("cart_item_details").getString("category_id"));
                                        cartBooking.setCart_item_details_name(cart_detail_obj.getJSONObject("cart_item_details").getString("name"));
                                        cartBooking.setCart_item_details_price(cart_detail_obj.getJSONObject("cart_item_details").getString("price"));


                                        //extra_item_details obj
                                        ArrayList<String>extra_namelist = new ArrayList<>();
                                        ArrayList<String>extra_pricelist = new ArrayList<>();

                                        if(cart_detail_obj.getJSONArray("extra_item_details").length()>0)
                                        {
                                            for(int j = 0; j<cart_detail_obj.getJSONArray("extra_item_details").length(); j++)
                                            {
                                               JSONObject extra_item_obj = cart_detail_obj.getJSONArray("extra_item_details").getJSONObject(j);
                                                if(extra_item_obj.has("name"))
                                                {
                                                    extra_namelist.add(extra_item_obj.getString("name"));
                                                }
                                                else{
                                                    extra_namelist.add("");
                                                }

                                                if(extra_item_obj.has("price"))
                                                {
                                                    extra_pricelist.add(extra_item_obj.getString("price"));
                                                }
                                                else{
                                                    extra_pricelist.add("");
                                                }
                                                //cartBooking.setExtra_item_details_item_id(extra_item_obj.getString("item_id"));
                                            }

                                        }
                                        else{
                                            extra_namelist.add("");
                                            extra_pricelist.add("");
                                        }

                                        //extra name
                                        String str_extraname ="";
                                        for(int k =0; k<extra_namelist.size(); k++)
                                        {
                                            if (k == 0)
                                            { str_extraname = extra_namelist.get(k); }
                                            else{  str_extraname = str_extraname+","+extra_namelist.get(k);}
                                        }
                                        cartBooking.setExtra_item_details_name(str_extraname);

                                        //extra price
                                        String str_extraprice ="";
                                        for(int k =0; k<extra_pricelist.size(); k++)
                                        {
                                            if (k == 0)
                                            { str_extraprice = extra_pricelist.get(k); }
                                            else{  str_extraprice = str_extraprice+","+extra_pricelist.get(k);}
                                        }
                                        cartBooking.setExtra_item_details_price(str_extraprice);
                                        cartBookingArrayList.add(cartBooking);

                                    }

                                    progressBar_alertview.setVisibility(View.GONE);
                                    //call adapter
                                    PickupListingAdapter pickupListingAdapter = new PickupListingAdapter(getContext(),cartBookingArrayList);
                                    recycleView.setAdapter(pickupListingAdapter);

                                }
                            }
                            else if(response.code() == Constant.ERROR_CODE_n || response.code() == Constant.ERROR_CODE)
                            {
                                JSONObject jsonObject = new JSONObject(response.errorBody().string());
                                Toast.makeText(getContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                                progressBar_alertview.setVisibility(View.GONE);
                                /*JSONObject obj = new JSONObject(loadJSONFromAsset_t());

                                if(obj.getString("status").equalsIgnoreCase("200"))
                                {
                                    JSONObject data_obj = obj.getJSONObject("data");
                                    JSONArray cart_array_item = data_obj.getJSONArray("cart_item");
                                    cartBookingArrayList = new ArrayList<>();
                                    for(int i = 0; i<cart_array_item.length(); i++)
                                    {
                                        CartBooking cartBooking = new CartBooking();
                                        JSONObject cart_detail_obj = cart_array_item.getJSONObject(i);


                                        //data obj
                                        cartBooking.setData_userid(data_obj.getString("user_id"));
                                        cartBooking.setData_booking_table_id(data_obj.getString("booking_table_id"));
                                        cartBooking.setData_total(data_obj.getString("total"));

                                        //cart_item obj
                                        cartBooking.setCart_item_qty(cart_detail_obj.getString("qty"));
                                        cartBooking.setCart_item_cartid(cart_detail_obj.getString("cart_id"));
                                        cartBooking.setCart_item_id(cart_detail_obj.getString("item_id"));
                                        cartBooking.setCart_item_extra_id(cart_detail_obj.getString("item_extra_id"));

                                        //cart_item_details obj
                                        cartBooking.setCart_item_details_category_id(cart_detail_obj.getJSONObject("cart_item_details").getString("category_id"));
                                        cartBooking.setCart_item_details_name(cart_detail_obj.getJSONObject("cart_item_details").getString("name"));
                                        cartBooking.setCart_item_details_price(cart_detail_obj.getJSONObject("cart_item_details").getString("price"));


                                        //extra_item_details obj
                                        ArrayList<String>extra_namelist = new ArrayList<>();
                                        ArrayList<String>extra_pricelist = new ArrayList<>();
                                        for(int j = 0; j<cart_detail_obj.getJSONArray("extra_item_details").length(); j++)
                                        {
                                            JSONObject extra_item_obj = cart_detail_obj.getJSONArray("extra_item_details").getJSONObject(j);

                                            extra_namelist.add(extra_item_obj.getString("name"));
                                            extra_pricelist.add(extra_item_obj.getString("price"));


                                            //cartBooking.setExtra_item_details_item_id(extra_item_obj.getString("item_id"));
                                        }

                                        //extra name
                                        String str_extraname ="";
                                        for(int k =0; k<extra_namelist.size(); k++)
                                        {
                                            if (k == 0)
                                            { str_extraname = extra_namelist.get(k); }
                                            else{  str_extraname = str_extraname+","+extra_namelist.get(k);}
                                        }
                                        cartBooking.setExtra_item_details_name(str_extraname);

                                        //extra price
                                        String str_extraprice ="";
                                        for(int k =0; k<extra_pricelist.size(); k++)
                                        {
                                            if (k == 0)
                                            { str_extraprice = extra_pricelist.get(k); }
                                            else{  str_extraprice = str_extraprice+","+extra_pricelist.get(k);}
                                        }
                                        cartBooking.setExtra_item_details_price(str_extraprice);
                                        cartBookingArrayList.add(cartBooking);

                                   }
                                    progressBar.setVisibility(View.GONE);
                                    //call adapter
                                    CartBookingAdapter cartBookingAdapter = new CartBookingAdapter(getActivity(),cartBookingArrayList);
                                    recycleView.setAdapter(cartBookingAdapter);

                                }*/
                            }
                        }
                        catch (Exception ex)
                        {
                            ex.printStackTrace();
                            progressBar_alertview.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "Error occur please try again", Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        progressBar_alertview.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Error occur please try again", Toast.LENGTH_LONG).show();
                        //stopProgress();

                    }
                });
    }

    public void callApi_addqty(String cart_itemid,String qty)
    {
        //showProgress();
        progressBar.setVisibility(View.VISIBLE);
        Api.getInfo().cart_updateqty("Bearer "+storePrefrence.getString(TOKEN_LOGIN),cart_itemid, qty,storePrefrence.getString(Constant.IDENTFIER)).
                enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        try{
                            //Log.d("Result", jsonObject.toString());
                            if(response.code() == Constant.SUCCESS_CODE_n)
                            {
                                JSONObject obj = new JSONObject(new Gson().toJson(response.body()));
                                if(obj.getString("status").equalsIgnoreCase("200"))
                                {
                                    Toast.makeText(getContext(), obj.getString("message"),Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                    if (Utils.isNetworkAvailable(getContext())) {
                                        callApi_cartListview();
                                    }
                                    else{
                                        Toast.makeText(getContext(), Constant.NETWORKEROORMSG, Toast.LENGTH_SHORT).show();

                                    }

                                }
                            }
                            else if(response.code() == Constant.ERROR_CODE_n || response.code() == Constant.ERROR_CODE)
                            {
                                JSONObject jsonObject = new JSONObject(response.errorBody().string());
                                Toast.makeText(getContext(), jsonObject.getString("message"),Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);

                            }
                        }
                        catch (Exception ex)
                        {
                            ex.printStackTrace();
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "Error occur please try again", Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Error occur please try again", Toast.LENGTH_LONG).show();

                    }
                });
    }

    public void callApi_removeitemcart(String cart_itemid)
    {
        //showProgress();
        progressBar.setVisibility(View.VISIBLE);
        Api.getInfo().cart_removeqty("Bearer "+storePrefrence.getString(TOKEN_LOGIN), cart_itemid, storePrefrence.getString(Constant.IDENTFIER)).
                enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        try{
                            //Log.d("Result", jsonObject.toString());
                            if(response.code() == Constant.SUCCESS_CODE_n)
                            {
                                JSONObject obj = new JSONObject(new Gson().toJson(response.body()));
                                if(obj.getString("status").equalsIgnoreCase("200"))
                                {
                                    Toast.makeText(getContext(), obj.getString("message"),Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);

                                    if (Utils.isNetworkAvailable(getContext())) {
                                        callApi_cartListview();
                                    }
                                    else{
                                        Toast.makeText(getContext(), Constant.NETWORKEROORMSG, Toast.LENGTH_SHORT).show();

                                    }


                                }
                            }
                            else if(response.code() == Constant.ERROR_CODE_n || response.code() == Constant.ERROR_CODE)
                            {
                                JSONObject jsonObject = new JSONObject(response.errorBody().string());
                                Toast.makeText(getContext(), jsonObject.getString("message"),Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);

                            }
                        }
                        catch (Exception ex)
                        {
                            ex.printStackTrace();
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "Error occur please try again", Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Error occur please try again", Toast.LENGTH_LONG).show();

                    }
                });
    }


}
