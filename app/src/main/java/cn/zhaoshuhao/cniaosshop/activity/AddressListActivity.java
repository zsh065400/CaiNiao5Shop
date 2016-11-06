package cn.zhaoshuhao.cniaosshop.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.zhaoshuhao.cniaosshop.CainiaoApplication;
import cn.zhaoshuhao.cniaosshop.Contants;
import cn.zhaoshuhao.cniaosshop.R;
import cn.zhaoshuhao.cniaosshop.adapter.AddressAdapter;
import cn.zhaoshuhao.cniaosshop.adapter.decoration.DividerItemDecoration;
import cn.zhaoshuhao.cniaosshop.bean.Address;
import cn.zhaoshuhao.cniaosshop.http.OkHttpHelper;
import cn.zhaoshuhao.cniaosshop.http.SpotsCallback;
import cn.zhaoshuhao.cniaosshop.msg.BaseRespMsg;
import cn.zhaoshuhao.cniaosshop.utils.ToastUtils;
import cn.zhaoshuhao.cniaosshop.widget.CNToolbar;
import okhttp3.Response;

public class AddressListActivity extends AppCompatActivity {

    @ViewInject(R.id.id_toolbar)
    private CNToolbar mToolBar;

    @ViewInject(R.id.id_recycle_view)
    private RecyclerView mRecyclerview;

    private AddressAdapter mAdapter;
    private OkHttpHelper mHelper = OkHttpHelper.getInstance();
    private Handler mUiHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_list);
        x.view().inject(this);
        mUiHandler = new Handler(getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                initAddress();
            }
        };
        initToolbar();
        initAddress();
    }

    private void initToolbar() {
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        mToolBar.setOnRightButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toAddActivity();
            }
        });
    }

    private void toAddActivity() {
        Intent intent = new Intent(this, AddressAddActivity.class);
        startActivityForResult(intent, Contants.REQUEST_CODE);
    }

    private void initAddress() {
        Map<String, String> params = new HashMap<>(1);
        params.put("user_id", "" + CainiaoApplication.getInstance().getUser().getId());
        mHelper.doGet(Contants.API.ADDRESS_LIST, params, new SpotsCallback<List<Address>>
                (AddressListActivity.this) {
            @Override
            public void onSuccess(Response response, List<Address> data) {
                showAddress(data);
            }

            @Override
            public void onError(Response response, int errCode, Exception e) {

            }
        });
    }

    private void showAddress(List<Address> data) {
        Collections.sort(data);
        if (mAdapter == null) {
            mAdapter = new AddressAdapter(this, data, new AddressAdapter.AddressLisneter() {
                @Override
                public void setDefault(Address address) {
                    updateAddress(address);
                }

                @Override
                public void delete(final Address address) {
                    deleteAddress(address);
                }

                @Override
                public void edit(Address address) {
                    editAddress(address);
                }
            });
            mRecyclerview.setAdapter(mAdapter);
            mRecyclerview.setLayoutManager(new LinearLayoutManager(this));
            mRecyclerview.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        } else {
            mAdapter.refreshData(data);
        }
    }

    private void editAddress(Address address) {
        Intent i = new Intent(this, AddressAddActivity.class);
        i.putExtra("data", address);
        i.putExtra("type", AddressAddActivity.ADDRESS_EDIT);
        startActivityForResult(i, Contants.REQUEST_CODE);
    }

    private void deleteAddress(final Address address) {
        TextView textView = new TextView(this);
        textView.setText("是否删除");
        // TODO: 2016/11/6 自定义dialog
        Dialog builder = new AlertDialog.Builder(this)
                .setView(textView)
                .setMessage("确认删除么")
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        delChoose(address);
                    }
                })
                .setNegativeButton("返回", null)
                .create();
        builder.show();
//        DialogUtils.showSimpleDialog(AddressListActivity.this, "删除", "确认删除么", "确定", new
//                DialogInterface
//                        .OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//
//                    }
//                });
    }

    private void delChoose(Address address) {
        Map<String, String> params = new HashMap<>(1);
        params.put("id", "" + address.getId());
        mHelper.doPost(Contants.API.ADDRESS_DELETE, params, new
                SpotsCallback<BaseRespMsg>
                        (AddressListActivity.this) {
                    @Override
                    public void onSuccess(Response response, BaseRespMsg o) {
                        System.out.println(response.message());
                        if (o.getStatus() == BaseRespMsg.STATUS_SUCCESS) {
                            ToastUtils.show(AddressListActivity.this, "删除成功");
                            mUiHandler.sendEmptyMessage(0);
                        }
                    }

                    @Override
                    public void onError(Response response, int errCode, Exception e) {
                        System.out.println(response.message());
                        ToastUtils.show(AddressListActivity.this, "删除失败");
                    }
                });
    }

    private void updateAddress(Address address) {
        Map<String, String> params = new HashMap<>(6);
        params.put("id", "" + address.getId());
        params.put("consignee", address.getConsignee());
        params.put("phone", address.getPhone());
        params.put("addr", address.getAddr());
        params.put("zip_code", address.getZipCode());
        params.put("is_default", address.getIsDefault() + "");
        mHelper.doPost(Contants.API.ADDRESS_UPDATE, params, new SpotsCallback<BaseRespMsg>(this) {

            @Override
            public void onSuccess(Response response, BaseRespMsg baseRespMsg) {
                if (baseRespMsg.getStatus() == BaseRespMsg.STATUS_SUCCESS) {
                    initAddress();
                }
            }

            @Override
            public void onError(Response response, int errCode, Exception e) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Contants.REQUEST_CODE && resultCode == RESULT_OK) {
            initAddress();
        }
    }
}
