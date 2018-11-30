package com.wd.tech.di.component;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import com.wd.tech.di.module.MainModule;

import com.jess.arms.di.scope.ActivityScope;
import com.wd.tech.mvp.ui.activity.MainActivity;

@ActivityScope
@Component(modules = MainModule.class, dependencies = AppComponent.class)
public interface MainComponent {
    void inject(MainActivity activity);
}