package cn.zhaoshuhao.cniaosshop.activity;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import cn.zhaoshuhao.cniaosshop.CainiaoApplication;
import cn.zhaoshuhao.cniaosshop.Contants;
import cn.zhaoshuhao.cniaosshop.R;
import cn.zhaoshuhao.cniaosshop.bean.Address;
import cn.zhaoshuhao.cniaosshop.city.XmlParserHandler;
import cn.zhaoshuhao.cniaosshop.city.model.CityModel;
import cn.zhaoshuhao.cniaosshop.city.model.DistrictModel;
import cn.zhaoshuhao.cniaosshop.city.model.ProvinceModel;
import cn.zhaoshuhao.cniaosshop.http.OkHttpHelper;
import cn.zhaoshuhao.cniaosshop.http.SpotsCallback;
import cn.zhaoshuhao.cniaosshop.msg.BaseRespMsg;
import cn.zhaoshuhao.cniaosshop.widget.CNToolbar;
import okhttp3.Response;

public class AddressAddActivity extends AppCompatActivity {

    public static final int ADDRESS_EDIT = 623;

    @ViewInject(R.id.id_toolbar)
    private CNToolbar mToolbar;

    @ViewInject(R.id.id_tv_address)
    private TextView mTvAddress;

    @ViewInject(R.id.id_et_consignee)
    private EditText mEtConsignee;

    @ViewInject(R.id.id_et_phone)
    private EditText mEtPhone;

    @ViewInject(R.id.id_et_addr)
    private EditText mEtAddr;

    private OptionsPickerView mCityPickerView;

    private ArrayList<ProvinceModel> mProvince;
    private ArrayList<ArrayList<String>> mCitys = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<String>>> mDistricts = new ArrayList<>();

    private OkHttpHelper mHelper = OkHttpHelper.getInstance();
    private int mType;
    private Address mAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_add);
        x.view().inject(this);

        init();
        getData();
        initToolbar();
    }

    private void getData() {
        Intent intent = getIntent();
        mType = intent.getIntExtra("type", 0);
        if (mType == 0) return;

        mAddress = (Address) intent.getSerializableExtra("data");

        String consignee = mAddress.getConsignee();
        String phone = mAddress.getPhone();
        String addr = mAddress.getAddr();
        /*做字符串分割时要注意源数据为null的问题，在保存时做为空处理或分割时做处理*/
        String addrLast = addr.split("   ")[0];
        String addrNext = addr.split("   ")[1];

        mEtConsignee.setText(consignee);
        mEtPhone.setText(phone);
        mTvAddress.setText(addrLast);
        mEtAddr.setText(addrNext);
    }

    private void initToolbar() {
        if (mType == ADDRESS_EDIT) {
            mToolbar.setTitle("修改地址");
        }
        mToolbar.setOnRightButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mType == ADDRESS_EDIT) {
                    updateAddress();
                    return;
                }
                createAddress();
            }


        });
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void updateAddress() {
        String consignee = mEtConsignee.getText().toString();
        String phone = mEtPhone.getText().toString();
        String address = mTvAddress.getText().toString() + "   " + mEtAddr.getText().toString();

        Map<String, String> params = new HashMap<>(6);
        params.put("id", mAddress.getId() + "");
        params.put("consignee", consignee);
        params.put("phone", phone);
        params.put("addr", address);
        params.put("zip_code", mAddress.getZipCode());
        params.put("is_default", mAddress.getIsDefault() + "");

        mHelper.doPost(Contants.API.ADDRESS_UPDATE, params, new SpotsCallback<BaseRespMsg>(this) {
            @Override
            public void onSuccess(Response response, BaseRespMsg baseRespMsg) {
                if (baseRespMsg.getStatus() == BaseRespMsg.STATUS_SUCCESS) {
                    setResult(RESULT_OK);
                    finish();
                }
            }

            @Override
            public void onError(Response response, int code, Exception e) {
                Log.e(TAG, "onError: " + response.message());
            }
        });
    }

    private static final String TAG = "AddressAddActivity";

    private void createAddress() {
        String consignee = mEtConsignee.getText().toString();
        String phone = mEtPhone.getText().toString();
        String address = mTvAddress.getText().toString() + "   " + mEtAddr.getText().toString();

        Map<String, String> params = new HashMap<>(5);
        params.put("user_id", "" + CainiaoApplication.getInstance().getUser().getId());
        params.put("consignee", consignee);
        params.put("phone", phone);
        params.put("addr", address);
        params.put("zip_code", "000000");

        mHelper.doPost(Contants.API.ADDRESS_CREATE, params, new SpotsCallback<BaseRespMsg>(this) {
            @Override
            public void onSuccess(Response response, BaseRespMsg baseRespMsg) {
                if (baseRespMsg.getStatus() == BaseRespMsg.STATUS_SUCCESS) {
                    setResult(RESULT_OK);
                    finish();
                }
            }

            @Override
            public void onError(Response response, int code, Exception e) {
                Log.e(TAG, "onError: " + response.message());
            }
        });
    }


    private void init() {
        initProvinceDatas();
        mCityPickerView = new OptionsPickerView(this);
        mCityPickerView.setPicker(mProvince, mCitys, mDistricts, true);
        mCityPickerView.setTitle("选择城市");
        mCityPickerView.setCyclic(false, false, false);
        mCityPickerView.setSelectOptions(0, 0, 0);
        mCityPickerView.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                String address = mProvince.get(options1).getName() + " " +
                        mCitys.get(options1).get(option2) + " " +
                        mDistricts.get(options1).get(option2).get(options3);
                mTvAddress.setText(address);
            }
        });
    }

    private void initProvinceDatas() {
        AssetManager assets = getAssets();
        try {
            InputStream is = assets.open("province_data.xml");
            /*factory、parser、handler*/
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser sp = spf.newSAXParser();
            XmlParserHandler xph = new XmlParserHandler();
            sp.parse(is, xph);
            is.close();
            mProvince = (ArrayList<ProvinceModel>) xph.getDataList();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (mProvince != null) {
            for (ProvinceModel pm : mProvince) {
                List<CityModel> cityList = pm.getCityList();
                ArrayList<String> cityStrs = new ArrayList<>(cityList.size());
                for (CityModel cm : cityList) {
                    cityStrs.add(cm.getName());

//                  用于放置所有的县的list
                    ArrayList<ArrayList<String>> dts = new ArrayList<>();
//                  将dm中的县重组成string，每个市的县
                    List<DistrictModel> districtList = cm.getDistrictList();
                    ArrayList<String> districtStrs = new ArrayList<>(districtList.size());
                    for (DistrictModel dm : districtList) {
                        districtStrs.add(dm.getName());
                    }
                    dts.add(districtStrs);
                    mDistricts.add(dts);
                }
                mCitys.add(cityStrs);
            }
        }
    }

    @Event(R.id.id_tv_address)
    private void showCityPickerView(View v) {
        mCityPickerView.show();
    }
}
