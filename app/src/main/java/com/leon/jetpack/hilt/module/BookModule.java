/**
 * FileName: BookModule
 * Author: shiwenliang
 * Date: 2021/8/11 16:58
 * Description:
 */
package com.leon.jetpack.hilt.module;

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;

@Module
@InstallIn(ActivityComponent.class)
public abstract class BookModule {

    @Binds
    abstract IBook getBook(KotlinBook kotlinBook);
}
