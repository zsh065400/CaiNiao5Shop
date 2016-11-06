package cn.zhaoshuhao.cniaosshop.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.w4lle.library.NineGridAdapter;
import com.w4lle.library.NineGridlayout;

import java.util.List;

import cn.zhaoshuhao.cniaosshop.R;
import cn.zhaoshuhao.cniaosshop.bean.Order;
import cn.zhaoshuhao.cniaosshop.bean.OrderItem;
import cn.zhaoshuhao.cniaosshop.bean.Wares;
import cn.zhaoshuhao.cniaosshop.utils.CartProvider;
import cn.zhaoshuhao.cniaosshop.utils.ToastUtils;

/**
 * Created by zsh06
 * Created on 2016/11/6 10:08.
 */

public class MyOrderAdapter extends SimpleAdapter<Order> {
    private CartProvider mProvider;
    private JumpToDeatilActivity mListener;

    public MyOrderAdapter(Context context, List<Order> datas, JumpToDeatilActivity listener) {
        super(context, datas, R.layout.template_my_orders);
        mProvider = CartProvider.getInstance(context);
        mListener = listener;
    }

    @Override
    public void bindData(BaseViewHolder holder, final Order order) {
        holder.getTextView(R.id.id_tv_order_num).setText("订单号：" + order.getOrderNum());
        holder.getTextView(R.id.id_tv_order_money).setText("实付金额： ￥：" + order.getAmount());
        TextView txtStatus = holder.getTextView(R.id.id_tv_status);
        switch (order.getStatus()) {
            case Order.STATUS_SUCCESS:
                txtStatus.setText("成功");
                txtStatus.setTextColor(Color.parseColor("#ff4CAF50"));
                break;
            case Order.STATUS_PAY_FAIL:
                txtStatus.setText("支付失败");
                txtStatus.setTextColor(Color.parseColor("#ffF44336"));
                break;
            case Order.STATUS_PAY_WAIT:
                txtStatus.setText("等待支付");
                txtStatus.setTextColor(Color.parseColor("#ffFFEB3B"));
                break;

        }

        Button btnBuyAgain = holder.getButton(R.id.id_btn_buy_again);
//      评论晒单可单做
        Button btnComment = holder.getButton(R.id.id_btn_comment);
        btnBuyAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (OrderItem item : order.getItems()) {
                    Wares wares = item.getWares();
                    mProvider.put(wares);
                    ToastUtils.show(mContext, "添加购物车成功");
//                    mContext.startActivity(new Intent(mContext, CreateOrderActivity.class));
                }
            }
        });

        NineGridlayout nineGridlayout = (NineGridlayout) holder.getView(R.id.id_ngl_image);
        nineGridlayout.setGap(5);
        nineGridlayout.setDefaultWidth(50);
        nineGridlayout.setDefaultHeight(50);
        nineGridlayout.setAdapter(new ORderItemAdapter(mContext, order.getItems()));
        nineGridlayout.setOnItemClickListerner(new NineGridlayout.OnItemClickListerner() {
            @Override
            public void onItemClick(View view, int position) {
//              通过定义接口及实现接口来实现跳转
                if (mListener != null) {
                    mListener.jump(position);
                }
            }
        });
    }

    class ORderItemAdapter extends NineGridAdapter {

        public ORderItemAdapter(Context context, List<OrderItem> list) {
            super(context, list);
        }

        @Override
        public int getCount() {
            return (list == null) ? 0 : list.size();
        }

        @Override
        public String getUrl(int position) {
            OrderItem item = getItem(position);
            return item == null ? null : item.getWares().getImgUrl();
        }

        @Override
        public OrderItem getItem(int position) {
            return (list == null) ? null : (OrderItem) list.get(position);
        }

        @Override
        public long getItemId(int position) {
            OrderItem item = getItem(position);
            return item == null ? 0 : item.getId();
        }

        @Override
        public View getView(int i, View view) {
            ImageView iv = new ImageView(context);
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            iv.setBackgroundColor(Color.parseColor("#f5f5f5"));
            Picasso.with(context).load(getUrl(i)).placeholder(new ColorDrawable(Color.parseColor("#f5f5f5"))).into(iv);
            return iv;
        }
    }

    public interface JumpToDeatilActivity {
        void jump(int position);
    }

}
