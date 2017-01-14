package com.itheima.swipedelete97;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private ListView listview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listview = (ListView) findViewById(R.id.listview);

        listview.setAdapter(new MyAdapter());

        //监听listview的滚动
        listview.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(openedLayout!=null){
                    openedLayout.closeLayout();
                }
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });


//        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(MainActivity.this, ""+position, Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    SwipeLayout openedLayout;//用来记录打开的SwipeLayout
    class MyAdapter extends BaseAdapter implements SwipeLayout.OnSwipeListener{
        @Override
        public int getCount() {
            return Constant.NAMES.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            MyHolder myHolder = null;
            if(convertView==null){
                convertView = View.inflate(parent.getContext(), R.layout.adapter_list, null);
                myHolder = new MyHolder(convertView);
                convertView.setTag(myHolder);
            }else {
                myHolder = (MyHolder) convertView.getTag();
            }

            //绑定数据
            myHolder.tvName.setText(Constant.NAMES[position]);

            //设置监听器
            myHolder.swipeLayout.setOnSwipeListener(this);

            myHolder.swipeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(MainActivity.this, "第"+position+"个", Toast.LENGTH_SHORT).show();
                }
            });

            return convertView;
        }
        @Override
        public void onOpen(SwipeLayout currentLayout) {
            //应该关闭当前已经打开的
            if(openedLayout!=null && openedLayout!=currentLayout){
                openedLayout.closeLayout();
            }

            openedLayout = currentLayout;
        }
        @Override
        public void onClose(SwipeLayout currentLayout) {
            if(openedLayout==currentLayout){
                openedLayout = null;
            }
        }
    }
    static class MyHolder {
        @Bind(R.id.tv_name)
        TextView tvName;
        @Bind(R.id.tv_delete)
        TextView tvDelete;
        @Bind(R.id.swipeLayout)
        SwipeLayout swipeLayout;

        MyHolder(View view) {
            ButterKnife.bind(this, view);

        }
    }
}
