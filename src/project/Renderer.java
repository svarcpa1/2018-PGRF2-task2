package project;

import com.jogamp.opengl.*;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import javafx.geometry.Point3D;
import utils.OglUtils;

import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * class for representation of scene in OpenGL:
 * init, overwriting, events, viewport
 * @author PGRF FIM UHK; Pavel Švarc
 * @version 2018
 */

public class Renderer implements GLEventListener, MouseListener,
		MouseMotionListener, KeyListener {

    //for generating end
    private Random random = new Random();
    private int randomForEnd = random.nextInt(5);
    private boolean ended = false;
    private List<Point3D> endingPoints = new ArrayList<>();

    //for textures
    private Texture textureWall, textureFloor, textureTop;

    //basic grid
    private MapGrid mapGrid = new MapGrid();

    //others
    private JFrame frame;
    private GLU glu;
    private long oldmils, oldFPSmils, drunknessStarted, lightLess;
    private float angle =45;
    private int width;
    private int height;
    private int ox;
    private int oy;
    private float cutoff = 44.0f;
    private int movementTmp = 0;

    //traps
    private float fogTrapValue = 6.0f;
    private float fogTrapX, fogTrapZ, drunkTrapX, drunkTrapZ, lightTrapX, lightTrapZ;
    private boolean fogTrap, drunkTrap, lightTrap, fogTrapSpreadingEnded = false;

    //camera setup (lookAt method)
    private double ex = 0.5;
    private double ey = 0.5;
    private double ez = 1.0;
    private double px = 6.0;
    private double pz = -6.0;
    private double pxOld = 6.0;
    private double pzOld = -6.0;


    private float zenit, azimut;

    //booleans for movement direction
    private boolean north, northeast, east, southeast, south, southwest, west, northwest = false;

	/**
	 * init method called when the windows is created
	 */
	@Override
	public void init(GLAutoDrawable glDrawable) {
		GL2 gl = glDrawable.getGL().getGL2();
        glu=new GLU();

        OglUtils.printOGLparameters(gl);

        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glFrontFace(GL2.GL_CCW);

        //material setup
        float[] mat_dif = new float[] { 0.3f, 0.3f, 0.3f, 0.3f };
        float[] mat_spec = new float[] { 1, 1, 1, 1 };
        float[] mat_amb = new float[] { 0.3f, 0.3f, 0.3f, 0.3f };
        gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, mat_amb, 0);
        gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE, mat_dif, 0);
        gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_SPECULAR, mat_spec, 0);

        //white light
        float[] lightWhite = new float[] { 1, 1, 1, 1 };
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT_AND_DIFFUSE, lightWhite, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, lightWhite, 0);
        //white light vol 2
        float[] lightWhite2 = new float[] { 1, 1, 1, 1 };
        gl.glLightfv(GL2.GL_LIGHT3, GL2.GL_AMBIENT_AND_DIFFUSE, lightWhite2, 0);
        gl.glLightfv(GL2.GL_LIGHT3, GL2.GL_SPECULAR, lightWhite2, 0);
        //red light
        float[] lightRed = new float[] { 1, 0, 0, 1 };
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_AMBIENT_AND_DIFFUSE, lightRed, 0);
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_SPECULAR, lightRed, 0);
        //blue light
        float[] lightBlue = new float[] { 0, 0, 1, 1 };
        gl.glLightfv(GL2.GL_LIGHT2, GL2.GL_AMBIENT_AND_DIFFUSE, lightBlue, 0);
        gl.glLightfv(GL2.GL_LIGHT2, GL2.GL_SPECULAR, lightBlue, 0);

        GLUquadric quadratic = glu.gluNewQuadric();
        glu.gluQuadricNormals(quadratic, GLU.GLU_SMOOTH); //normals for shading
        glu.gluQuadricTexture(quadratic, true); //texture coordinates

        //loading wall texture
        InputStream is = getClass().getResourceAsStream("/wall_texture2.jpg");
        if (is == null)
            System.out.println("File not found");
        else
            try {
                textureWall = TextureIO.newTexture(is, true, "jpg");
            } catch (GLException | IOException e) {
                System.err.println("Error during loading the file");
            }
        //loading floor texture
        is = getClass().getResourceAsStream("/floor_texture2.jpg");
        if (is == null)
            System.out.println("File not found");
        else
            try {
                textureFloor = TextureIO.newTexture(is, true, "jpg");
            } catch (GLException | IOException e) {
                System.err.println("Error during loading the file");
            }
        //loading top texture
        is = getClass().getResourceAsStream("/top_texture2.jpg");
        if (is == null)
            System.out.println("File not found");
        else
            try {
                textureTop = TextureIO.newTexture(is, true, "jpg");
            } catch (GLException | IOException e) {
                System.err.println("Error during loading the file");
            }

        //display list(cube)
        gl.glNewList(1, GL2.GL_COMPILE);
        gl.glPolygonMode(GL2.GL_FRONT, GL2.GL_FLAT);
        gl.glBegin(GL2.GL_QUADS);
        //front
        gl.glTexCoord2f(0f, 0f);
        gl.glNormal3f(0,0,1);
        gl.glVertex3f(-0.5f, -0.5f, 0.5f);  //7
        gl.glTexCoord2f(1f, 0f);
        gl.glNormal3f(0,0,1);
        gl.glVertex3f(0.5f, -0.5f, 0.5f);   //8
        gl.glTexCoord2f(1f, 1f);
        gl.glNormal3f(0,0,1);
        gl.glVertex3f(0.5f, 1.0f, 0.5f);    //3
        gl.glTexCoord2f(0f, 1f);
        gl.glNormal3f(0,0,1);
        gl.glVertex3f(-0.5f, 1.0f, 0.5f);   //4
        //back
        gl.glTexCoord2f(0f, 0f);
        gl.glNormal3f(0,0,-1);
        gl.glVertex3f(0.5f, -0.5f, -0.5f);  //5
        gl.glTexCoord2f(1f, 0f);
        gl.glNormal3f(0,0,-1);
        gl.glVertex3f(-0.5f, -0.5f, -0.5f); //6
        gl.glTexCoord2f(1f, 1f);
        gl.glNormal3f(0,0,-1);
        gl.glVertex3f(-0.5f, 1.0f, -0.5f);  //2
        gl.glTexCoord2f(0f, 1f);
        gl.glNormal3f(0,0,-1);
        gl.glVertex3f(0.5f, 1.0f, -0.5f);   //1
        //right
        gl.glTexCoord2f(0f, 0f);
        gl.glNormal3f(1,0,0);
        gl.glVertex3f(0.5f, -0.5f, 0.5f);   //8
        gl.glTexCoord2f(1f, 0f);
        gl.glNormal3f(1,0,0);
        gl.glVertex3f(0.5f, -0.5f, -0.5f);  //5
        gl.glTexCoord2f(1f, 1f);
        gl.glNormal3f(1,0,0);
        gl.glVertex3f(0.5f, 1.0f, -0.5f);   //1
        gl.glTexCoord2f(0f, 1f);
        gl.glNormal3f(1,0,0);
        gl.glVertex3f(0.5f, 1.0f, 0.5f);    //3
        //left
        gl.glTexCoord2f(0f, 0f);
        gl.glNormal3f(-1,0,0);
        gl.glVertex3f(-0.5f, -0.5f, -0.5f); //6
        gl.glTexCoord2f(1f, 0f);
        gl.glNormal3f(-1,0,0);
        gl.glVertex3f(-0.5f, -0.5f, 0.5f);  //7
        gl.glTexCoord2f(1f, 1f);
        gl.glNormal3f(-1,0,0);
        gl.glVertex3f(-0.5f, 1.0f, 0.5f);   //4
        gl.glTexCoord2f(0f, 1f);
        gl.glNormal3f(-1,0,0);
        gl.glVertex3f(-0.5f, 1.0f, -0.5f);  //2
        gl.glEnd();
        gl.glEndList();

        //display list(floor)
        gl.glNewList(2, GL2.GL_COMPILE);
        gl.glPolygonMode(GL2.GL_FRONT, GL2.GL_FLAT);
        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2f(0f, 0f);
        gl.glNormal3f(0,1,0);
        gl.glVertex3f(-0.5f, -0.5f, -0.5f); //6
        gl.glTexCoord2f(1f, 0f);
        gl.glNormal3f(0,1,0);
        gl.glVertex3f(0.5f, -0.5f, -0.5f);  //5
        gl.glTexCoord2f(1f, 1f);
        gl.glNormal3f(0,1,0);
        gl.glVertex3f(0.5f, -0.5f, 0.5f);   //8
        gl.glTexCoord2f(0f, 1f);
        gl.glNormal3f(0,1,0);
        gl.glVertex3f(-0.5f, -0.5f, 0.5f);  //7
        gl.glEnd();
        gl.glEndList();

        //display list(top)
        gl.glNewList(4, GL2.GL_COMPILE);
        gl.glPolygonMode(GL2.GL_FRONT, GL2.GL_FLAT);
        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2f(0f, 0f);
        gl.glNormal3f(0,1,0);
        gl.glVertex3f(-0.5f, 1.0f, -0.5f); //6
        gl.glTexCoord2f(1f, 0f);
        gl.glNormal3f(0,1,0);
        gl.glVertex3f(0.5f, 1.0f, -0.5f);  //5
        gl.glTexCoord2f(1f, 1f);
        gl.glNormal3f(0,1,0);
        gl.glVertex3f(0.5f, 1.0f, 0.5f);   //8
        gl.glTexCoord2f(0f, 1f);
        gl.glNormal3f(0,1,0);
        gl.glVertex3f(-0.5f, 1.0f, 0.5f);  //7
        gl.glEnd();
        gl.glEndList();

        //display list diamond
        gl.glNewList(3, GL2.GL_COMPILE);
        gl.glPolygonMode(GL2.GL_FRONT, GL2.GL_FILL);
        gl.glBegin(GL2.GL_TRIANGLE_FAN);
        gl.glColor3f(0.0f, 0.0f,1.0f);
        gl.glVertex3f(0,-0.1f,0);
        gl.glColor3f(0.0f, 1.0f,0.0f);
        gl.glVertex3f(0.1f,0.2f,0.1f);
        gl.glColor3f(0.0f, 1.0f,0.0f);
        gl.glVertex3f(-0.1f,0.2f,0.1f);
        gl.glColor3f(0.0f, 1.0f,0.0f);
        gl.glVertex3f(-0.1f,0.2f,-0.1f);
        gl.glColor3f(0.0f, 1.0f,0.0f);
        gl.glVertex3f(0.1f,0.2f,-0.1f);
        gl.glEnd();
        gl.glBegin(GL2.GL_TRIANGLE_FAN);
        gl.glColor3f(1.0f, 0.0f,0.0f);
        gl.glVertex3f(0,0.3f,0);
        gl.glColor3f(0.0f, 1.0f,0.0f);
        gl.glVertex3f(0.1f,0.2f,0.1f);
        gl.glColor3f(0.0f, 1.0f,0.0f);
        gl.glVertex3f(-0.1f,0.2f,0.1f);
        gl.glColor3f(0.0f, 1.0f,0.0f);
        gl.glVertex3f(-0.1f,0.2f,-0.1f);
        gl.glColor3f(0.0f, 1.0f,0.0f);
        gl.glVertex3f(0.1f,0.2f,-0.1f);
        gl.glEnd();
        gl.glEndList();

        //display list(trap)
        gl.glNewList(5, GL2.GL_COMPILE);
        gl.glPolygonMode(GL2.GL_FRONT, GL2.GL_FLAT);
        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex3f(-0.3f, -0.49f, -0.3f); //6
        gl.glVertex3f(0.3f, -0.49f, -0.3f);  //5
        gl.glVertex3f(0.3f, -0.49f, 0.3f);   //8
        gl.glVertex3f(-0.3f, -0.49f, 0.3f);  //7
        gl.glEnd();
        gl.glEndList();

        mapGrid.setUpMap();

        do {
            fogTrapX = random.nextInt(mapGrid.getMapGrid().length);
            fogTrapZ = random.nextInt(mapGrid.getMapGrid()[0].length);
        }while ((mapGrid.getField((int)fogTrapX,(int)fogTrapZ)!=3) );

        do {
            drunkTrapX = random.nextInt(mapGrid.getMapGrid().length);
            drunkTrapZ = random.nextInt(mapGrid.getMapGrid()[0].length);
        }while ((mapGrid.getField((int)drunkTrapX,(int)drunkTrapZ)!=3));

        do {
            lightTrapX = random.nextInt(mapGrid.getMapGrid().length);
            lightTrapZ = random.nextInt(mapGrid.getMapGrid()[0].length);
        }while ((mapGrid.getField((int)lightTrapX,(int)lightTrapZ)!=3) );
    }

	/**
	 * method called when each frame is rendered
	 */
	@Override
	public void display(GLAutoDrawable glDrawable) {
		GL2 gl = glDrawable.getGL().getGL2();

        Long mils = System.currentTimeMillis();
        if ((mils - oldFPSmils)>300){
            oldFPSmils=mils;
        }
        oldmils = mils;

        cutoff +=0.5f;
        if(cutoff ==70.0f){
            cutoff =0.0f;
        }

		//cleaning space for drawing
		gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glClearColor(0f, 0f, 0f, 1f);
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

		//projection setup
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
        glu.gluPerspective(45, width /(float) height, 0.1f, 5000.0f);

        //model and view setup
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();

        double py = 0.25;
        double ux = 0;
        double uy = 1;
        double uz = 0;
        glu.gluLookAt(px, py, pz, px-ex, py -ey, pz-ez, ux, uy, uz);

        //fog
        gl.glPushMatrix();
        float[] color = new float[]{0.05f, 0.05f, 0.05f, 0.05f};
        gl.glFogi(GL2.GL_FOG_MODE,GL2.GL_LINEAR);
        gl.glFogfv(GL2.GL_FOG_COLOR, color, 0);
        gl.glFogi(GL2.GL_FOG_START,(int)(1.0f));
        gl.glFogi(GL2.GL_FOG_END,(int)(fogTrapValue));
        gl.glEnable(GL2.GL_FOG);
        gl.glPopMatrix();

        //lights
        float[] light_position = new float[]{4.0f, 3.0f, 15.0f, 1.0f};
        float[] light_direction = new float[]{0.0f, 0.0f, -1.0f, 1.0f};
        gl.glPushMatrix();
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, light_position, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPOT_DIRECTION, light_direction, 0);
        gl.glLightf(GL2.GL_LIGHT0,GL2.GL_SPOT_CUTOFF, cutoff);
        gl.glLightf(GL2.GL_LIGHT0,GL2.GL_SPOT_EXPONENT,80.0f);
        gl.glPopMatrix();

        float[] light_position2 = new float[]{12.0f, 1.5f, 0.0f, 1.0f};
        float[] light_direction2 = new float[]{0.0f, 0.0f, -1.0f, 0.0f};
        gl.glPushMatrix();
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, light_position2, 0);
        gl.glLightf(GL2.GL_LIGHT1,GL2.GL_SPOT_CUTOFF,15.0f);
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_SPOT_DIRECTION, light_direction2, 0);
        gl.glLightf(GL2.GL_LIGHT1,GL2.GL_SPOT_EXPONENT,1.0f);
        gl.glPopMatrix();

        float[] light_position3 = new float[]{10.0f, 1.5f, -11.0f, 1.0f};
        float[] light_direction3 = new float[]{1.0f, 0.0f, 0.0f, 0.0f};
        gl.glPushMatrix();
        gl.glLightfv(GL2.GL_LIGHT2, GL2.GL_POSITION, light_position3, 0);
        gl.glLightf(GL2.GL_LIGHT2,GL2.GL_SPOT_CUTOFF,15.0f);
        gl.glLightfv(GL2.GL_LIGHT2, GL2.GL_SPOT_DIRECTION, light_direction3, 0);
        gl.glLightf(GL2.GL_LIGHT2,GL2.GL_SPOT_EXPONENT,1.0f);
        gl.glPopMatrix();

        float[] light_position4 = new float[]{8.0f, 3.0f, -15.0f, 1.0f};
        float[] light_direction4 = new float[]{0.0f, 0.0f, 1.0f, 1.0f};
        gl.glPushMatrix();
        gl.glLightfv(GL2.GL_LIGHT3, GL2.GL_POSITION, light_position4, 0);
        gl.glLightfv(GL2.GL_LIGHT3, GL2.GL_SPOT_DIRECTION, light_direction4, 0);
        gl.glLightf(GL2.GL_LIGHT3,GL2.GL_SPOT_CUTOFF, cutoff);
        gl.glLightf(GL2.GL_LIGHT3,GL2.GL_SPOT_EXPONENT,20.0f);
        gl.glPopMatrix();

        gl.glColorMaterial (GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE ) ;
        gl.glEnable(GL2.GL_COLOR_MATERIAL);

        gl.glEnable(GL2.GL_LIGHTING);

        if(!lightTrap) {
            gl.glEnable(GL2.GL_LIGHT0);
            gl.glEnable(GL2.GL_LIGHT1);
            gl.glEnable(GL2.GL_LIGHT2);
            gl.glEnable(GL2.GL_LIGHT3);
        }

        gl.glShadeModel(GL2.GL_SMOOTH);
        gl.glHint(GL.GL_LINE_SMOOTH_HINT,GL.GL_NICEST);

        gl.glEnable(GL2.GL_TEXTURE_2D);
        gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE);

        if(drunkTrap) {
            gl.glMatrixMode(GL2.GL_TEXTURE);
            gl.glLoadIdentity();
            gl.glTranslatef(0.5f, 0.5f, 0);
            gl.glRotatef(angle / 2, 0, 0, 1);
            gl.glTranslatef(-0.5f, -0.5f, 0);
        }

        gl.glMatrixMode(GL2.GL_MODELVIEW);

        //generating maze
        for (int i=0; i<mapGrid.getMapGrid().length; i++){
            for (int j=0; j<mapGrid.getMapGrid().length;j++){
                if (mapGrid.getMapGrid()[i][j]==0){
                    gl.glColor3f(0.2f, 0.2f,0.2f);
                    textureWall.enable(gl);
                    textureWall.bind(gl);
                    gl.glPushMatrix();
                    gl.glTranslated((double)i,0,(double)-j);
                    gl.glCallList(1);
                    gl.glPopMatrix();
                }

                if (mapGrid.getMapGrid()[i][j]==2){
                    endingPoints.add(new Point3D(i,1,-j));
                }

                if (mapGrid.getMapGrid()[i][j]==3 || mapGrid.getMapGrid()[i][j]==2){
                    gl.glColor3f(0.7f, 0.7f,0.7f);
                    textureFloor.enable(gl);
                    textureFloor.bind(gl);
                    gl.glPushMatrix();
                    gl.glTranslated((double)i,0,(double)-j);
                    gl.glCallList(2);
                    gl.glPopMatrix();

                    gl.glColor3f(1.0f, 0.0f,0.0f);
                    textureTop.enable(gl);
                    textureTop.bind(gl);
                    gl.glPushMatrix();
                    gl.glTranslated((double)i,0,(double)-j);
                    gl.glCallList(4);
                    gl.glPopMatrix();
                }
            }
        }

        //checking if the diamond is found
        if (px == endingPoints.get(randomForEnd).getX() && pz == endingPoints.get(randomForEnd).getZ() && !ended){
            ended=true;
            mapGrid.setField((int)px,(int)pz*-1,3);
            JOptionPane.showMessageDialog(frame, "Congratulations, diamond found! End of the game.");
            System.exit(0);
        }

        //checking if the trap is pressed
        if (px == fogTrapX && pz == fogTrapZ *-1 && !fogTrap){
            fogTrap =true;
            JOptionPane.showMessageDialog(frame, "Fog trap on! Hurry up!");
        }
        if (px == lightTrapX && pz == lightTrapZ *-1 && !lightTrap) {
            lightTrap = true;
            lightLess = System.currentTimeMillis();
            JOptionPane.showMessageDialog(frame, "Light trap on!");
        }
        if (px == drunkTrapX && pz == drunkTrapZ *-1 && !drunkTrap) {
            drunkTrap = true;
            drunknessStarted = System.currentTimeMillis();
            JOptionPane.showMessageDialog(frame, "Drunk trap on!");
        }

        //traps restarting
        if((drunkTrap) && (System.currentTimeMillis()-drunknessStarted)>30000){
            drunkTrap=false;
            gl.glMatrixMode(GL2.GL_TEXTURE);
            gl.glLoadIdentity();
        }
        if((lightTrap) && (System.currentTimeMillis()-lightLess)>20000){
            lightTrap=false;
        }

        //texture rotation (drunk trap)
        if(drunkTrap) {
            gl.glMatrixMode(GL2.GL_TEXTURE);
            gl.glLoadIdentity();
            gl.glTranslatef(0.5f, 0.5f, 0);
            gl.glRotatef(angle / 2, 0, 0, 1);
            gl.glTranslatef(-0.5f, -0.5f, 0);
        }

        if (fogTrap && !fogTrapSpreadingEnded){
            fogTrapValue -= 0.005f;
            if (fogTrapValue <2.5f){
                fogTrapSpreadingEnded = true;
            }
        }

        gl.glDisable(GL2.GL_LIGHTING);
        gl.glDisable(GL2.GL_LIGHT0);
        gl.glDisable(GL2.GL_LIGHT1);
        gl.glDisable(GL2.GL_LIGHT2);
        gl.glDisable(GL2.GL_LIGHT3);

        if(!ended) {
            angle++;
            gl.glPushMatrix();
            gl.glTranslated(endingPoints.get(randomForEnd).getX(), 0, endingPoints.get(randomForEnd).getZ());
            gl.glRotated(angle, 0, 1, 0);
            gl.glCallList(3);
            gl.glPopMatrix();
            angle = angle % 360;
        }

        if(!fogTrap) {
            gl.glPushMatrix();
            gl.glColor3f(0.1f,0.1f,0.5f);
            gl.glTranslated(fogTrapX, 0, fogTrapZ * -1);
            gl.glCallList(5);
            gl.glPopMatrix();
        }

        if(!drunkTrap) {
            gl.glPushMatrix();
            gl.glColor3f(0.1f,0.5f,0.1f);
            gl.glTranslated(drunkTrapX, 0, drunkTrapZ * -1);
            gl.glCallList(5);
            gl.glPopMatrix();
        }

        if(!lightTrap) {
            gl.glPushMatrix();
            gl.glColor3f(0.5f,0.1f,0.1f);
            gl.glTranslated(lightTrapX, 0, lightTrapZ * -1);
            gl.glCallList(5);
            gl.glPopMatrix();
        }

        //movement handeling
        if (southwest) {
            px -= 0.02f;
            pz += 0.02f;
            movementTmp +=1;
            if (movementTmp == 50){
                movementTmp =0;
                px=pxOld-1;
                pz=pzOld+1;
                southwest =false;
            }
        }
        if (west) {
            px -= 0.02f;
            movementTmp +=1;
            if (movementTmp == 50){
                movementTmp =0;
                px=pxOld-1;
                west =false;
            }
        }
        if (northwest) {
            px -= 0.02f;
            pz -= 0.02f;
            movementTmp +=1;
            if (movementTmp == 50){
                movementTmp =0;
                px=pxOld-1;
                pz=pzOld-1;
                northwest =false;
            }
        }
        if (north) {
            pz -= 0.02f;
            movementTmp +=1;
            if (movementTmp == 50){
                movementTmp =0;
                pz=pzOld-1;
                north =false;
            }
        }
        if (northeast) {
            px += 0.02f;
            pz -= 0.02f;
            movementTmp +=1;
            if (movementTmp == 50){
                movementTmp =0;
                px=pxOld+1;
                pz=pzOld-1;
                northeast =false;
            }
        }
        if (east) {
            px += 0.02f;
            movementTmp +=1;
            if (movementTmp == 50){
                movementTmp =0;
                px=pxOld+1;
                east =false;
            }
        }

        if (southeast) {
            px += 0.02f;
            pz += 0.02f;
            movementTmp +=1;
            if (movementTmp == 50){
                movementTmp =0;
                px=pxOld+1;
                pz=pzOld+1;
                southeast =false;
            }
        }
        if (south) {
            pz += 0.02f;
            movementTmp +=1;
            if (movementTmp == 50){
                movementTmp =0;
                pz=pzOld+1;
                south =false;
            }
        }
	}
	
	/**
	 * method called when the window is reshaped or first frame is rendered
	 */
	@Override
	public void reshape(GLAutoDrawable glDrawable, int x, int y, int width,
			int height) {
        this.width = width;
        this.height = height;
        glDrawable.getGL().getGL2().glViewport(0, 0, width , height);
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
	}
	@Override
	public void mouseReleased(MouseEvent e) {
	}
	// mouse motion listener
	@Override
	public void mouseDragged(MouseEvent e) {
	}
	@Override
	public void mouseMoved(MouseEvent e) {
        int dx = e.getX() - ox;
        int dy = e.getY() - oy;
        ox = e.getX();
        oy = e.getY();

        zenit += dy;
        if (zenit > 60)
            zenit = 60;
        if (zenit <= -45)
            zenit = -45;
        azimut += dx;
        azimut = azimut % 360;
        if(azimut<0) azimut =360;
        double a_rad = azimut * Math.PI / 180;
        double z_rad = zenit * Math.PI / 180;
        ex = (Math.sin(a_rad) * Math.cos(z_rad));
        ey = (Math.sin(z_rad));
        ez = (-Math.cos(a_rad) * Math.cos(z_rad));
	}
	// key listener
	@Override
	public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_W) {
            if (azimut > 22 && azimut < 67) {
                if (mapGrid.getMapGrid()[(int) px - 1][((int) pz + 1) * -1] != 0) {
                    if (!southwest && !west && !northwest && !north && !northeast && !east && !southeast && !south) {
                        pxOld = px;
                        pzOld = pz;
                        southwest = true;
                    }
                }
            }
            if (azimut >= 67 && azimut < 112) {
                if (mapGrid.getMapGrid()[(int) px - 1][((int) pz) * -1] != 0) {
                    if (!southwest && !west && !northwest && !north && !northeast && !east && !southeast && !south) {
                        pxOld = px;
                        west = true;
                    }
                }
            }
            if (azimut >= 112 && azimut < 157) {
                if (mapGrid.getMapGrid()[(int) px - 1][((int) pz - 1) * -1] != 0) {
                    if (!southwest && !west && !northwest && !north && !northeast && !east && !southeast && !south) {
                        pxOld = px;
                        pzOld = pz;
                        northwest = true;
                    }
                }
            }
            if (azimut >= 157 && azimut < 202) {
                if (mapGrid.getMapGrid()[(int) px][((int) pz - 1) * -1] != 0) {
                    if (!southwest && !west && !northwest && !north && !northeast && !east && !southeast && !south) {
                        pzOld = pz;
                        north = true;
                    }
                }
            }
            if (azimut >= 202 && azimut < 247) {
                if (mapGrid.getMapGrid()[(int) px + 1][((int) pz - 1) * -1] != 0) {
                    if (!southwest && !west && !northwest && !north && !northeast && !east && !southeast && !south) {
                        pxOld = px;
                        pzOld = pz;
                        northeast = true;
                    }
                }
            }
            if (azimut >= 247 && azimut < 292) {
                if (mapGrid.getMapGrid()[(int) px + 1][((int) pz) * -1] != 0) {
                    if (!southwest && !west && !northwest && !north && !northeast && !east && !southeast && !south) {
                        pxOld = px;
                        east = true;
                    }
                }
            }
            if (azimut >= 292 && azimut < 337) {
                if (mapGrid.getMapGrid()[(int) px + 1][((int) pz + 1) * -1] != 0) {
                    if (!southwest && !west && !northwest && !north && !northeast && !east && !southeast && !south) {
                        pxOld = px;
                        pzOld = pz;
                        southeast = true;
                    }
                }
            }
            if ((azimut >= 337 && azimut <= 360) || (azimut >= 0 && azimut <= 22)) {
                if (mapGrid.getMapGrid()[(int) px][((int) pz + 1) * -1] != 0) {
                    if (!southwest && !west && !northwest && !north && !northeast && !east && !southeast && !south) {
                        pzOld = pz;
                        south = true;
                    }
                }
            }
        }

        if (e.getKeyCode() == KeyEvent.VK_A) {
            if (azimut > 22 && azimut < 67) {
                if (mapGrid.getMapGrid()[(int) px + 1][((int) pz + 1) * -1] != 0) {
                    if (!southwest && !west && !northwest && !north && !northeast && !east && !southeast && !south) {
                        pxOld = px;
                        pzOld = pz;
                        southeast = true;
                    }
                }
            }
            if (azimut >= 67 && azimut < 112) {
                if (mapGrid.getMapGrid()[(int) px][((int) pz + 1) * -1] != 0) {
                    if (!southwest && !west && !northwest && !north && !northeast && !east && !southeast && !south) {
                        pzOld = pz;
                        south = true;
                    }
                }
            }
            if (azimut >= 112 && azimut < 157) {
                if (mapGrid.getMapGrid()[(int) px - 1][((int) pz + 1) * -1] != 0) {
                    if (!southwest && !west && !northwest && !north && !northeast && !east && !southeast && !south) {
                        pxOld = px;
                        pzOld = pz;
                        southwest = true;
                    }
                }
            }
            if (azimut >= 157 && azimut < 202) {
                if (mapGrid.getMapGrid()[(int) px - 1][((int) pz) * -1] != 0) {
                    if (!southwest && !west && !northwest && !north && !northeast && !east && !southeast && !south) {
                        pxOld = px;
                        west = true;
                    }
                }
            }
            if (azimut >= 202 && azimut < 247) {
                if (mapGrid.getMapGrid()[(int) px - 1][((int) pz - 1) * -1] != 0) {
                    if (!southwest && !west && !northwest && !north && !northeast && !east && !southeast && !south) {
                        pxOld = px;
                        pzOld = pz;
                        northwest = true;
                    }
                }
            }
            if (azimut >= 247 && azimut < 292) {
                if (mapGrid.getMapGrid()[(int) px][((int) pz - 1) * -1] != 0) {
                    if (!southwest && !west && !northwest && !north && !northeast && !east && !southeast && !south) {
                        pzOld = pz;
                        north = true;
                    }
                }
            }
            if (azimut >= 292 && azimut < 337) {
                if (mapGrid.getMapGrid()[(int) px + 1][((int) pz - 1) * -1] != 0) {
                    if (!southwest && !west && !northwest && !north && !northeast && !east && !southeast && !south) {
                        pxOld = px;
                        pzOld = pz;
                        northeast = true;
                    }
                }
            }
            if ((azimut >= 337 && azimut <= 360) || (azimut >= 0 && azimut <= 22)) {
                if (mapGrid.getMapGrid()[(int) px + 1][((int) pz) * -1] != 0) {
                    if (!southwest && !west && !northwest && !north && !northeast && !east && !southeast && !south) {
                        pxOld = px;
                        east = true;
                    }
                }
            }
        }

        if (e.getKeyCode() == KeyEvent.VK_S) {
            if (azimut > 22 && azimut < 67) {
                if (mapGrid.getMapGrid()[(int) px + 1][((int) pz - 1) * -1] != 0) {
                    if (!southwest && !west && !northwest && !north && !northeast && !east && !southeast && !south) {
                        pxOld = px;
                        pzOld = pz;
                        northeast = true;
                    }
                }
            }
            if (azimut >= 67 && azimut < 112) {
                if (mapGrid.getMapGrid()[(int) px + 1][((int) pz) * -1] != 0) {
                    if (!southwest && !west && !northwest && !north && !northeast && !east && !southeast && !south) {
                        pxOld = px;
                        east = true;
                    }
                }
            }
            if (azimut >= 112 && azimut < 157) {
                if (mapGrid.getMapGrid()[(int) px + 1][((int) pz + 1) * -1] != 0) {
                    if (!southwest && !west && !northwest && !north && !northeast && !east && !southeast && !south) {
                        pxOld = px;
                        pzOld = pz;
                        southeast = true;
                    }
                }
            }
            if (azimut >= 157 && azimut < 202) {
                if (mapGrid.getMapGrid()[(int) px][((int) pz + 1) * -1] != 0) {
                    if (!southwest && !west && !northwest && !north && !northeast && !east && !southeast && !south) {
                        pzOld = pz;
                        south = true;
                    }
                }
            }
            if (azimut >= 202 && azimut < 247) {
                if (mapGrid.getMapGrid()[(int) px - 1][((int) pz + 1) * -1] != 0) {
                    if (!southwest && !west && !northwest && !north && !northeast && !east && !southeast && !south) {
                        pxOld = px;
                        pzOld = pz;
                        southwest = true;
                    }
                }
            }
            if (azimut >= 247 && azimut < 292) {
                if (mapGrid.getMapGrid()[(int) px - 1][((int) pz) * -1] != 0) {
                    if (!southwest && !west && !northwest && !north && !northeast && !east && !southeast && !south) {
                        pxOld = px;
                        west = true;
                    }
                }
            }
            if (azimut >= 292 && azimut < 337) {
                if (mapGrid.getMapGrid()[(int) px - 1][((int) pz - 1) * -1] != 0) {
                    if (!southwest && !west && !northwest && !north && !northeast && !east && !southeast && !south) {
                        pxOld = px;
                        pzOld = pz;
                        northwest = true;
                    }
                }
            }
            if ((azimut >= 337 && azimut <= 360) || (azimut >= 0 && azimut <= 22)) {
                if (mapGrid.getMapGrid()[(int) px][((int) pz - 1) * -1] != 0) {
                    if (!southwest && !west && !northwest && !north && !northeast && !east && !southeast && !south) {
                        pzOld = pz;
                        north = true;
                    }
                }
            }
        }

        if (e.getKeyCode() == KeyEvent.VK_D) {
            if (azimut > 22 && azimut < 67) {
                if (mapGrid.getMapGrid()[(int) px - 1][((int) pz - 1) * -1] != 0) {
                    if (!southwest && !west && !northwest && !north && !northeast && !east && !southeast && !south) {
                        pxOld = px;
                        pzOld = pz;
                        northwest = true;
                    }
                }
            }
            if (azimut >= 67 && azimut < 112) {
                if (mapGrid.getMapGrid()[(int) px][((int) pz - 1) * -1] != 0) {
                    if (!southwest && !west && !northwest && !north && !northeast && !east && !southeast && !south) {
                        pzOld = pz;
                        north = true;
                    }
                }
            }
            if (azimut >= 112 && azimut < 157) {
                if (mapGrid.getMapGrid()[(int) px + 1][((int) pz - 1) * -1] != 0) {
                    if (!southwest && !west && !northwest && !north && !northeast && !east && !southeast && !south) {
                        pxOld = px;
                        pzOld = pz;
                        northeast = true;
                    }
                }
            }
            if (azimut >= 157 && azimut < 202) {
                if (mapGrid.getMapGrid()[(int) px + 1][((int) pz) * -1] != 0) {
                    if (!southwest && !west && !northwest && !north && !northeast && !east && !southeast && !south) {
                        pxOld = px;
                        east = true;
                    }
                }
            }
            if (azimut >= 202 && azimut < 247) {
                if (mapGrid.getMapGrid()[(int) px + 1][((int) pz + 1) * -1] != 0) {
                    if (!southwest && !west && !northwest && !north && !northeast && !east && !southeast && !south) {
                        pxOld = px;
                        pzOld = pz;
                        southeast = true;
                    }
                }
            }
            if (azimut >= 247 && azimut < 292) {
                if (mapGrid.getMapGrid()[(int) px][((int) pz + 1) * -1] != 0) {
                    if (!southwest && !west && !northwest && !north && !northeast && !east && !southeast && !south) {
                        pzOld = pz;
                        south = true;
                    }
                }
            }
            if (azimut >= 292 && azimut < 337) {
                if (mapGrid.getMapGrid()[(int) px - 1][((int) pz + 1) * -1] != 0) {
                    if (!southwest && !west && !northwest && !north && !northeast && !east && !southeast && !south) {
                        pxOld = px;
                        pzOld = pz;
                        southwest = true;
                    }
                }
            }
            if ((azimut >= 337 && azimut <= 360) || (azimut >= 0 && azimut <= 22)) {
                if (mapGrid.getMapGrid()[(int) px - 1][((int) pz) * -1] != 0) {
                    if (!southwest && !west && !northwest && !north && !northeast && !east && !southeast && !south) {
                        pxOld = px;
                        west = true;
                    }
                }
            }
        }
    }
	@Override
	public void keyReleased(KeyEvent e) {
	}
	@Override
	public void keyTyped(KeyEvent e) {
	}
	@Override
	public void dispose(GLAutoDrawable arg0) {
	}
}