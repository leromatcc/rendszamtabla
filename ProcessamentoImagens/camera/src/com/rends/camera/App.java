package com.rends.camera;

public class App {
	public static void main(String[] args) {
		System.out.println("Hello World!");
		Camera cam = new Camera("http://192.168.0.150/dms", "placas", "reconhecer");

		AcessarCameraTemporizado acesso = new AcessarCameraTemporizado();
		acesso.setCamera(cam);
		acesso.ativar(0, 5000);
	}
}
