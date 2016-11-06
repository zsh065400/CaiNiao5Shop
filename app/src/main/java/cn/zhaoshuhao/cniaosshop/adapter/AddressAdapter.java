package cn.zhaoshuhao.cniaosshop.adapter;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.List;

import cn.zhaoshuhao.cniaosshop.R;
import cn.zhaoshuhao.cniaosshop.bean.Address;


/**
 * Created by <a href="http://www.cniao5.com">菜鸟窝</a>
 * 一个专业的Android开发在线教育平台
 */
public class AddressAdapter extends SimpleAdapter<Address> {


    private AddressLisneter mAddressLisneter;

    public AddressAdapter(Context context, List<Address> datas, AddressLisneter lisneter) {
        super(context, datas, R.layout.template_address);
        this.mAddressLisneter = lisneter;
    }

//    public String replacePhoneNum(String phone) {
//        return phone.substring(0, phone.length() - (phone.substring(3)).length()) + "****" + phone.substring(7);
//    }

    @Override
    public void bindData(BaseViewHolder holder, final Address address) {
        holder.getTextView(R.id.id_tv_name).setText(address.getConsignee());
        holder.getTextView(R.id.id_tv_phone).setText(address.getPhone());
        holder.getTextView(R.id.id_tv_addr).setText(address.getAddr());
        final CheckBox checkBox = holder.getCheckBox(R.id.id_cb_default);
        final boolean isDefault = address.getIsDefault();
        checkBox.setChecked(isDefault);
        if (isDefault) {
            checkBox.setText("默认地址");
        } else {
            checkBox.setClickable(true);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked && mAddressLisneter != null) {
                        address.setIsDefault(true);
                        mAddressLisneter.setDefault(address);
                    }
                }
            });
        }

        TextView tvDelete = holder.getTextView(R.id.id_tv_delete);
        TextView tvEdit = holder.getTextView(R.id.id_tv_edit);
        tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAddressLisneter != null) mAddressLisneter.delete(address);
            }
        });
        tvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAddressLisneter != null) mAddressLisneter.edit(address);
            }
        });
    }


    public interface AddressLisneter {
        void setDefault(Address address);

        void delete(Address address);

        void edit(Address address);
    }


}
