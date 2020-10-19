package Adapter;

import com.fangfangtech.oidbox.R;
import com.scwang.smartrefresh.layout.adapter.SmartRecyclerAdapter;
import com.scwang.smartrefresh.layout.adapter.SmartViewHolder;

import java.util.Collection;

/**
 * 基于simple_list_item_2简单的适配器
 *
 * @author XUE
 * @since 2019/4/1 11:04
 */
public class SimpleRecyclerAdapter_Records extends SmartRecyclerAdapter<FangRecords> {

    public SimpleRecyclerAdapter_Records() {
        super(R.layout.listitem_main3_records);
    }

    public SimpleRecyclerAdapter_Records(Collection<FangRecords> data) {
        super(data, R.layout.listitem_main3_records);
    }

    /**
     * 绑定布局控件
     *
     * @param holder
     * @param fangRecords
     * @param position
     */
    @Override
    protected void onBindViewHolder(SmartViewHolder holder, FangRecords fangRecords, int position) {
        holder.text(R.id.listitem_main3_records_tv_id, fangRecords.id);
        holder.text(R.id.listitem_main3_records_tv_time, fangRecords.time);
        holder.text(R.id.listitem_main3_records_tv_grade, fangRecords.record);
    }


}
