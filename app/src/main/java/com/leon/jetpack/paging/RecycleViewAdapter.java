/**
 * FileName: RecycleViewAdapter
 * Author: shiwenliang
 * Date: 2021/9/10 10:04
 * Description:
 */
package com.leon.jetpack.paging;

import androidx.annotation.NonNull;
import androidx.paging.PositionalDataSource;

public class RecycleViewAdapter extends PositionalDataSource<PageBean> {
    @Override
    public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback<PageBean> callback) {

        //在这里可以通过callback 来配置每一页加载多少数据，总多少条数据，加载的起始位置
    }

    @Override
    public void loadRange(@NonNull LoadRangeParams params, @NonNull LoadRangeCallback<PageBean> callback) {

    }


}
