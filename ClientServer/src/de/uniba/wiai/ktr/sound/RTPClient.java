package de.uniba.wiai.ktr.sound;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.media.*;

public class RTPClient implements ControllerListener, Runnable {

	Player p;
	MediaLocator src;

	public static void main(String[] args) {
		RTPClient rtp = new RTPClient("141.13.92.68");
		ExecutorService es = Executors.newSingleThreadExecutor();
		Thread t = new Thread(rtp);
		while (true) {
			es.execute(rtp);
		}
		// t.start();

	}

	public RTPClient(String ip) {
		String srcUrl = "rtp://" + ip + ":42050/audio/1";
		DataSink sink;
		try {
			src = new MediaLocator(srcUrl);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run() {
		try {

			p = Manager.createPlayer(src);
			p.addControllerListener(this);
			p.realize();
			p.start();

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public synchronized void controllerUpdate(ControllerEvent evt) {
		if (evt instanceof EndOfMediaEvent) {
			System.exit(0);
		} else {
			System.out.println(evt.toString());
		}
	}
}
