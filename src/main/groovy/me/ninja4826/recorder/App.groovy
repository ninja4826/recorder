package me.ninja4826.recorder

import javax.swing.SwingUtilities

class App {
	static void main(String... args) {
		def debug = false
		for (String s : args) {
			if (s == 'debug') {
				debug = true
			}
		}
		SwingUtilities.invokeLater new WebcamTest(debug)
	}
}
