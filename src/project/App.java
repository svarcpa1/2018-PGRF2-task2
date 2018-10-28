package project;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class App {
	private static final int FPS = 60; // animator's target frames per second

	public void start(){
		try {
			Frame mazeFrame = new Frame("Maze");

			// setup OpenGL Version 2
	    	GLProfile profile = GLProfile.get(GLProfile.GL2);
	    	GLCapabilities capabilities = new GLCapabilities(profile);
	    	capabilities.setRedBits(8);
			capabilities.setBlueBits(8);
			capabilities.setGreenBits(8);
			capabilities.setAlphaBits(8);
			capabilities.setDepthBits(24);

	    	// The canvas is the widget that's drawn in the JFrame
	    	GLCanvas canvas = new GLCanvas(capabilities);
	    	Renderer ren = new Renderer();
			canvas.addGLEventListener(ren);
			canvas.addMouseListener(ren);
			canvas.addMouseMotionListener(ren);
			canvas.addKeyListener(ren);
	    	canvas.setSize( 800, 600 );
	    	
	    	
	    	mazeFrame.add(canvas);
			
	        final FPSAnimator animator = new FPSAnimator(canvas, FPS, true);
	    	 
	    	mazeFrame.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					new Thread() {
	                     @Override
	                     public void run() {
	                        if (animator.isStarted()) animator.stop();
	                        System.exit(0);
	                     }
	                  }.start();
				}
			});
	    	mazeFrame.setTitle("Maze");
	    	mazeFrame.pack();
			mazeFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
	    	mazeFrame.setVisible(true);
            mazeFrame.toFront();
            mazeFrame.requestFocus();
            animator.start(); // start the animation loop

			MenuBar menuBar = new MenuBar();
			Menu menu = new Menu("Menu");
			MenuItem controls = new MenuItem("controls");
            MenuItem about = new MenuItem("about");
            menu.add(controls);
            menu.add(about);
            menuBar.add(menu);
            mazeFrame.setMenuBar(menuBar);

            controls.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
					JFrame controlsFrame = new JFrame("Maze - controls");
					JTextArea controlsArea = new JTextArea( " W - forward"   + "\n"+
                                                            " S - backward"  + "\n"+
                                                            " A - left"      + "\n"+
                                                            " D - right"     + "\n"+
                                                            " mouse - look"  + "\n"
                    );
					controlsArea.setEditable(false);
					controlsFrame.add(controlsArea);
					controlsFrame.setSize(300, 120);
					controlsFrame.setVisible(true);
					controlsFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                }
            });
            about.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JFrame aboutFrame = new JFrame("Maze - about");
                    JTextArea aboutArea = new JTextArea (
                            " MAZE 3D" + "\n"+
                            " -en--------------------------------------------------------------------------" + "\n"+
                            " Created by: Pavel Svarc" + "\n"+
                            " Course: PGRF2, group_1, University of Hradec Kralove " + "\n"+
                            " Date: 02.05.2018"+ "\n"+
                            " " + "\n"+
                            " You are supposed to find diamond as soon as possible but do not hurry so much!" + "\n"+
                            " You should be careful and you should look under your feet because of traps," + "\n"+
                            " deadly traps. Each trap is different but they have a common goal - to confuse you!"
                            + "\n"+
                            " " + "\n"+
                            " " + "\n"+
                            " BLUDIŠTĚ 3D" + "\n"+
                            " -cs--------------------------------------------------------------------------" + "\n"+
                            " Vytvořil: Pavel Švarc" + "\n"+
                            " Předmět: PGRF2, skupina 1 , Univerzita Hradec Králové " + "\n"+
                            " Datum: 02.05.2018"+ "\n"+
                            " " + "\n"+
                            " Cíl hry je jednoduchý, najít diamant. Nebude to ale tak snadné!" + "\n"+
                            " Měli byste být opartní, protože v bludišti jsou pasti," + "\n"+
                            " smrtící pasti. Každá z nich je jiná, ale jejich cíl je společný - zmást vás!" + "\n"
                    );
                    aboutArea.setEditable(false);
                    aboutFrame.add(aboutArea);
                    aboutFrame.setSize(600, 450);
                    aboutFrame.setVisible(true);
                    aboutFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                }
            });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new App().start());
	}
}