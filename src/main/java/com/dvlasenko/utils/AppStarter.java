package com.dvlasenko.utils;

import com.dvlasenko.controller.AppController;
import com.dvlasenko.view.AppView;

public class AppStarter {

    public static void startApp() {
        AppView view = new AppView();
        AppController controller = new AppController(view);
        controller.runApp();
    }
}
