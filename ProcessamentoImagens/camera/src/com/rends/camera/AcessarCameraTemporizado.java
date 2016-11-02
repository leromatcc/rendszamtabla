package com.rends.camera;

import java.util.Timer;
import java.util.TimerTask;

import lombok.Data;

@Data
public class AcessarCameraTemporizado extends TimerTask {
	Camera camera;

	public void gravarImagem() {
		camera.salvarImagemDaCamera();
	}

	public void ativar(long delay, long period) {
		Timer timer = new Timer();
		timer.schedule(this, delay, period);
	}

	public void run() {
		gravarImagem();
	}
}
