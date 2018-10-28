package jogl01start;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;


import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

/**
 * trida pro zobrazeni sceny v OpenGL:
 * inicializace, prekresleni, udalosti, viewport 
 * @author PGRF FIM UHK
 * @version 2015
 */

public class TestRenderer implements GLEventListener, MouseListener,
		MouseMotionListener, KeyListener {

	/**
	 * metoda inicializace, volana pri vytvoreni okna
	 */
	@Override
	public void init(GLAutoDrawable glDrawable) {
		GL2 gl = glDrawable.getGL().getGL2();
		System.out.println("Init GL is " + gl.getClass().getName());
		System.out.println("GL_VENDOR " + gl.glGetString(GL2.GL_VENDOR)); // vyrobce
		System.out.println("GL_RENDERER " + gl.glGetString(GL2.GL_RENDERER)); // graficka karta
		System.out.println("GL_VERSION " + gl.glGetString(GL2.GL_VERSION)); // verze OpenGL
		System.out.println("GL_EXTENSIONS " + gl.glGetString(GL2.GL_EXTENSIONS)); // implementovana rozsireni

	}

	/**
	 * metoda zobrazeni, volana pri prekresleni kazdeho snimku
	 */
	@Override
	public void display(GLAutoDrawable glDrawable) {
		GL2 gl = glDrawable.getGL().getGL2();
		
		// nulujeme misto pro kresleni
		gl.glClearColor(0f, 0f, 0f, 1f);
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT);

		// nastaveni modelovaci transformace
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity(); // inicializace na jednotkovou matici

		// nastaveni projekce
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity(); // inicializace na jednotkovou matici

        //gl.glScaled(1,2,1);
        gl.glRotated(90,0,0,1);
        gl.glTranslated(0,0.5,0);


		gl.glBegin(GL2.GL_TRIANGLES); // vykreslime trojuhelnik
		gl.glColor3f(1.0f, 0.0f, 0.0f); // barva prvniho vrcholu CERVENA
		gl.glVertex2f(-0.2f, -0.2f); // souradnice v 2D (levy dolni roh)
		gl.glColor3f(0.0f, 1.0f, 0.0f); // barva prvniho vrcholu ZELENA
		gl.glVertex2f(0.2f, -0.2f); // souradnice v 2D (stred prave strany)
		gl.glColor3f(0.0f, 0.0f, 1.0f); // barva prvniho vrcholu MODRA
		gl.glVertex2f(0.0f, 0.2f); // souradnice v 2D (stred horni strany)
		gl.glEnd(); // ukoncime kresleni trojuhelniku
	}

	
	/**
	 * metoda volana pri zmene velikosti okna a pri prvnim vykresleni
	 */
	@Override
	public void reshape(GLAutoDrawable glDrawable, int x, int y, int width,
			int height) {
		GL2 gl = glDrawable.getGL().getGL2();
		 gl.glViewport(0,0,width,height);
	}

	// mouse listener
	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// if (e.getButton() == MouseEvent.BUTTON1) { }
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// if (e.getButton() == MouseEvent.BUTTON1) { }
	}

	// mouse motion listener
	@Override
	public void mouseDragged(MouseEvent e) {
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}

	// key listener
	@Override
	public void keyPressed(KeyEvent e) {
		// switch (e.getKeyCode()) {
		// case KeyEvent.VK_A: //A
		// ;
		// }
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// switch (e.getKeyCode()) {}
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}
	
	@Override
	public void dispose(GLAutoDrawable arg0) {
	}
}