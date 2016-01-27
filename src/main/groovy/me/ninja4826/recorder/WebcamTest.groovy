package me.ninja4826.recorder

import java.awt.BorderLayout
import java.awt.event.ItemEvent
import java.awt.event.ItemListener
import java.awt.event.WindowEvent
import java.awt.event.WindowListener
import java.lang.Thread.UncaughtExceptionHandler

import javax.swing.JFrame
import javax.swing.JOptionPane
import javax.swing.SwingUtilities

import com.github.sarxos.webcam.Webcam
import com.github.sarxos.webcam.WebcamDiscoveryEvent
import com.github.sarxos.webcam.WebcamDiscoveryListener
import com.github.sarxos.webcam.WebcamEvent
import com.github.sarxos.webcam.WebcamListener
import com.github.sarxos.webcam.WebcamPanel
import com.github.sarxos.webcam.WebcamPicker
import com.github.sarxos.webcam.WebcamResolution

class WebcamTest extends JFrame implements Runnable,
	WebcamListener,
	WindowListener,
	UncaughtExceptionHandler,
	ItemListener,
	WebcamDiscoveryListener
{
	
	private Webcam webcam = null
	private WebcamPanel panel = null
	private WebcamPicker picker = null
	public boolean debug = false
	
	public WebcamTest(boolean debug = false) {
		this.debug = debug
		println "Debug: ${debug}"
	}
	
	@Override
	public void run() {
		println "Debug from run: ${this.debug}"
		Webcam.addDiscoveryListener this
		
		setTitle "Webcam Test"
		setDefaultCloseOperation JFrame.EXIT_ON_CLOSE
		setLayout new BorderLayout()
		
		addWindowListener this
		
		picker = new WebcamPicker()
		picker.addItemListener this
		
		webcam = picker.getSelectedWebcam()
		
		if (webcam == null) {
			println "No webcams found..."
			JOptionPane.showMessageDialog(null, "No webcams found. Exiting.", getTitle(), JOptionPane.ERROR_MESSAGE)
//			System.exit(1)
		}
		
		webcam.setViewSize WebcamResolution.VGA.getSize()
//		webcam.addWebcamListener WebcamTest.this
		webcam.addWebcamListener this
		
		panel = new WebcamPanel(webcam, false)
//		panel.setFPSDisplayed WebcamTest.this.debug
		panel.setFPSDisplayed true
		
		add picker, BorderLayout.NORTH
		add panel, BorderLayout.CENTER
		
		pack()
		
		setVisible true
		
		Thread t = { panel.start() }
		
		t.setName "example-starter"
		t.setDaemon true
		t.setUncaughtExceptionHandler this
		t.start()
	}
	
	@Override
	public void webcamOpen(WebcamEvent we) {
		println "webcam open"
	}

	@Override
	public void webcamClosed(WebcamEvent we) {
		println "webcam closed"
	}

	@Override
	public void webcamDisposed(WebcamEvent we) {
		println "webcam disposed"
	}

	@Override
	public void webcamImageObtained(WebcamEvent we) {
		// do nothing
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowClosed(WindowEvent e) {
		webcam.close()
	}

	@Override
	public void windowClosing(WindowEvent e) {
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		println "webcam viewer resumed"
		panel.resume()
	}

	@Override
	public void windowIconified(WindowEvent e) {
		println "webcam viewer paused"
		panel.pause()
	}

	@Override
	public void uncaughtException(Thread t, Throwable e) {
//		System.err.println(String.format("Exception in thread %s", t.getName()));
		System.err.println "Exception in thread ${t.getName}"
		e.printStackTrace()
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getItem() != webcam) {
			if (webcam != null) {
				println "Debug from itemStateChanged: ${this.debug}"
				panel.stop()
				
				remove panel
				webcam.removeWebcamListener this
				webcam.close()
				
				webcam = (Webcam) e.getItem
				webcam.setViewSize WebcamResolution.VGA.getSize()
				webcam.addWebcamListener this
				
				println "selected ${webcam.getName}"
				
				panel = new WebcamPanel(webcam, false)
//				panel.setFPSDisplayed(this.debug)
				panel.setFPSDisplayed true
				
				add panel, BorderLayout.CENTER
				pack()
				Thread t = { panel.start() }
				
				t.setName "example-stopper"
				t.setDaemon true
				t.setUncaughtExceptionHandler this
				t.start()
			}
		}
	}

	@Override
	public void webcamFound(WebcamDiscoveryEvent event) {
		if (picker != null) {
			picker.addItem(event.getWebcam());
		}
	}

	@Override
	public void webcamGone(WebcamDiscoveryEvent event) {
		if (picker != null) {
			picker.removeItem(event.getWebcam());
		}
	}
	
	
}
