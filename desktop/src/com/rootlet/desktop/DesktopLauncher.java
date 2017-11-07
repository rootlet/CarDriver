package com.rootlet.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.rootlet.cardriver.CarDriver;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Car Driver";
		config.width = 1024;
		config.height = 768;
		new LwjglApplication(new CarDriver(), config);
	}
}
