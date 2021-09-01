/**
 * FileName: DataBean
 * Author: shiwenliang
 * Date: 2021/8/13 9:26
 * Description:
 */
package com.leon.jetpack.databinding;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.leon.jetpack.BR;

public class DataBean extends BaseObservable {

    @Bindable
    public String getResultDesc() {
        return resultDesc;
    }

    public void setResultDesc(String resultDesc) {
        this.resultDesc = resultDesc;
        notifyPropertyChanged(BR.resultDesc);
    }

    @Bindable
    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
        notifyPropertyChanged(BR.resultCode);
    }

    private String resultDesc;
    private int resultCode;

}
