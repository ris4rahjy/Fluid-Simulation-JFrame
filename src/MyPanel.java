import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.swing.*;

public class MyPanel extends JPanel implements ActionListener {

    Timer timer;
    final int PANEL_WIDTH = 1000;
    final int PANEL_HEIGHT = 500;

    final double BOUNCE_FACTOR = 0.8;

    final int CircleDiameter = 10;

    private long lastTime;
    private int frameCount;
    double fps;


    int numParticle = 5;

    Particle listOfParticles [];

    int startHeight = 250;
    //int xVel = 1;
    //int yVel = 1;
    int xVel;
    int yVel;

    public MyPanel(){
        this.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        this.setBackground(Color.LIGHT_GRAY);
        this.setOpaque(true);

        listOfParticles = new Particle[numParticle];
        for(int i = 0; i < numParticle; i++){
            int x = ((i + 1) * (PANEL_WIDTH / numParticle));


            listOfParticles[i] = new Particle(x, startHeight, randomize(), randomize(), CircleDiameter);
        }

        lastTime = System.currentTimeMillis();
        frameCount = 0;
        fps = 0.0;

        timer = new Timer(100, this);
        timer.start();

    }

    public void paint(Graphics g){

        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        //Draw each circle
        for(Particle particle: listOfParticles){
            g2d.setColor(Color.BLUE);
            g2d.fillOval(particle.x, particle.y, particle.diameter, particle.diameter);
        }

        // Draw the FPS counter
        g2d.setColor(Color.BLACK);
        g2d.drawString(String.format("FPS: %.2f", fps), 10, 20);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        for(int i = 0; i < numParticle; i++){
            Particle c = listOfParticles[i];

            c.updatePosition(PANEL_WIDTH, PANEL_HEIGHT);

            //Check for Collisions
            for(int j = i + 1; j < numParticle; j++){
                Particle d = listOfParticles[j];
                if(c.intersects(d)){
                    //Handle Collision
                    c.reverseVelocity();
                    d.reverseVelocity();
                }
            }

            //Check if apply gravity
            if(c.atBottom(PANEL_HEIGHT)){
                c.y = PANEL_HEIGHT - c.diameter;
                c.yVel *= -BOUNCE_FACTOR;
            }else{
                c.applyGravity();
            }

            if(Math.abs(c.yVel) < 0.1){
                c.yVel = 0.0;
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
}
