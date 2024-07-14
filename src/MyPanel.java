import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.swing.*;

public class MyPanel extends JPanel implements ActionListener, ComponentListener {


    Timer timer;

    private final int DesiredFrameRate = 8; //33 = 30fps, 16 = 60fps, 8 = 120fps , 0 = 180fps
    private final int numParticle = 100;

    private double deltaTime;
    private long myLastTime;
    private static final int sim_speed = 10;

    private long lastTime;
    private int frameCount;
    double fps;


    private int PANEL_WIDTH = 1000;
    private int PANEL_HEIGHT = 500;

    final double BOUNCE_FACTOR = 0.3;
    final double GRAVITY = 9.81;

    final double CircleDiameter = 10.0;


    private Particle[] listOfParticles;

    private int startHeight = 300;


    public MyPanel(){
        this.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        this.setBackground(Color.darkGray);
        this.setOpaque(true);

        listOfParticles = new Particle[numParticle];



        for(int i = 0; i < numParticle; i++){
            // Move up to next stack of particles

            Random rand = new Random();

            // add x & start height
            listOfParticles[i] = new Particle(rand.nextInt(0, PANEL_WIDTH), rand.nextInt(0, PANEL_HEIGHT), randomize(), randomize(), CircleDiameter);
            //listOfParticles[i] = new Particle(499, 200, randomize(), randomize(), CircleDiameter);
        }

        lastTime = System.currentTimeMillis();

        myLastTime = System.nanoTime();
        deltaTime = 0.0;

        frameCount = 0;
        fps = 0.0;

        timer = new Timer(DesiredFrameRate, this);
        timer.start();

    }

    public void paint(Graphics g){

        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        //Draw each circle
        for(Particle particle: listOfParticles){
            g2d.setColor(Color.CYAN);
            g2d.fillOval((int) particle.position.getX(), (int) particle.position.getY(), (int) particle.diameter, (int) particle.diameter);
            //g2d.fillOval((int) particle.x, (int) particle.y, (int) particle.diameter, (int) particle.diameter);
        }


        // Draw the FPS counter
        g2d.setColor(Color.BLACK);
        g2d.drawString(String.format("FPS: %.2f", fps), 10, 20);

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        long myCurrentTime = System.nanoTime();
        deltaTime = ((myCurrentTime - myLastTime) / 1_000_000_000.0) * sim_speed;
        myLastTime = myCurrentTime;

        for(int i = 0; i < numParticle; i++){

            // Grab particle from list
            Particle c = listOfParticles[i];

            //Calculate density for all particles
            float density = 0;
            float mass = 1;

            /*
            for(int j = 0; j < numParticle; j++){
                Particle d = listOfParticles[j];

                //Calculate new Vector
                double newX = c.getX() - d.x;
                double newY = c.y - d.y;

                //Grab Magnitude of new Vector
                double dst = magnitude(newX, newY);
                
            }
            */

            c.updatePosition(PANEL_WIDTH, PANEL_HEIGHT, BOUNCE_FACTOR, deltaTime, GRAVITY);


            //Check for Collisions
            for(int j = i + 1; j < numParticle; j++){
                Particle d = listOfParticles[j];
                //if(c.intersects(d)){
                    //Handle Collision
                    //c.reverseVelocity();
                    //d.reverseVelocity();
                //}
            }
        }


        //Calculate FPS
        frameCount++;
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - lastTime;

        if(elapsedTime >= 1000){
            fps = (double) frameCount / (elapsedTime / 1000.0);
            frameCount = 0;
            lastTime = currentTime;
        }

        repaint();

    }

    public int randomize(){
        Random rand = new Random();
        int randomX = rand.nextInt(2);
        if(randomX == 0){
            return 1;
        }else{
            return -1;
        }
    }


    @Override
    public void componentResized(ComponentEvent e) {
        PANEL_WIDTH = this.getWidth();
        PANEL_HEIGHT = this.getHeight();
        System.out.println("Panel Resized");
    }

    @Override
    public void componentMoved(ComponentEvent e) {

    }

    @Override
    public void componentShown(ComponentEvent e) {

    }

    @Override
    public void componentHidden(ComponentEvent e) {

    }
}
