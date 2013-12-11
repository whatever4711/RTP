package de.uniba.wiai.ktr.sound;

import java.io.File;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.media.ConfigureCompleteEvent;
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.EndOfMediaEvent;
import javax.media.Format;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.Processor;
import javax.media.RealizeCompleteEvent;
import javax.media.control.TrackControl;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.DataSource;
import javax.media.rtp.RTPManager;
import javax.media.rtp.SendStream;
import javax.media.rtp.SessionAddress;

public class RTPServer implements ControllerListener, Runnable {
	private boolean realized = false;
	private boolean configured = false;
	private String ipAddress;
	Processor p;
	MediaLocator src;

	public static void main(String[] args) {

		RTPServer rtp;
		try {
			
			
			
			ExecutorService es = Executors.newSingleThreadExecutor();
		
				rtp = new RTPServer("224.0.0.1", "mp3/CallMe.mp3");
				es.execute(rtp);
					
				

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public RTPServer(String ip, String song) throws MalformedURLException {
		File f = new File(song);

		ipAddress = ip;
		src = new MediaLocator(f.toURI().toURL());

	}

	private void setTrackFormat(Processor p) {
		// Get the tracks from the processor
		TrackControl[] tracks = p.getTrackControls();
		// Do we have atleast one track?
		if (tracks == null || tracks.length < 1) {
			System.out.println("Couldn't find tracks in processor");
			System.exit(1);
		}

		// Set the output content descriptor to RAW_RTP
		// This will limit the supported formats reported from
		// Track.getSupportedFormats to only valid RTP formats.
		ContentDescriptor cd = new ContentDescriptor(ContentDescriptor.RAW_RTP);
		p.setContentDescriptor(cd);

		Format supported[];
		Format chosen;
		//boolean atLeastOneTrack = false;

		// Program the tracks.
		for (int i = 0; i < tracks.length; i++) {
			Format format = tracks[i].getFormat();
			System.out.println("Trenutni format je " + format.getEncoding());
			if (tracks[i].isEnabled()) {
				supported = tracks[i].getSupportedFormats();
				for (int n = 0; n < supported.length; n++)
					System.out.println("Supported format: " + supported[n]);

				// We've set the output content to the RAW_RTP.
				// So all the supported formats should work with RTP.
				// We'll just pick the first one.

				if (supported.length > 0) {
					chosen = supported[0]; // this is where I tried changing
											// formats
					tracks[i].setFormat(chosen);
					System.out.println("Track " + i
							+ " is set to transmit as: " + chosen);
					//atLeastOneTrack = true;
				} else
					tracks[i].setEnabled(false);
			} else
				tracks[i].setEnabled(false);
		}
	}

	private void transmit(Processor p) {
		try {
			DataSource output = p.getDataOutput();
			//PushBufferDataSource pbds = (PushBufferDataSource) output;
			RTPManager rtpMgr = RTPManager.newInstance();
			SessionAddress localAddr, destAddr;
			SendStream sendStream;
			int port = 42050;
			//SourceDescription srcDesList[];
			localAddr = new SessionAddress(InetAddress.getLocalHost(), 42055);
			InetAddress ipAddr = InetAddress.getByName(ipAddress);
			destAddr = new SessionAddress(ipAddr, port);
			rtpMgr.initialize(localAddr);
			rtpMgr.addTarget(destAddr);
			sendStream = rtpMgr.createSendStream(output, 0);
			sendStream.start();
			System.out.println("Created RTP session: " + ipAddress + " " + port);
			p.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized void controllerUpdate(ControllerEvent evt) {
		if (evt instanceof RealizeCompleteEvent) {
			realized = true;
		} else if (evt instanceof ConfigureCompleteEvent) {
			configured = true;
		} else if (evt instanceof EndOfMediaEvent) {
			System.exit(0);
		} else {
			// System.out.println(evt.toString());
		}
	}

	@SuppressWarnings("static-access")
	public void run() {

		try {
			p = Manager.createProcessor(src);
			p.addControllerListener(this);
			p.configure();
			while (!configured) {
				try {
					Thread.currentThread().sleep(100L);
					;
				} catch (InterruptedException e) {
					// ignore
				}
			}

			setTrackFormat(p);
			p.setContentDescriptor(new ContentDescriptor(
					ContentDescriptor.RAW_RTP));

			p.realize();
			while (!realized) {
				try {
					Thread.currentThread().sleep(100L);
					;
				} catch (InterruptedException e) {
					// ignore
				}
			}
			transmit(p);

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}