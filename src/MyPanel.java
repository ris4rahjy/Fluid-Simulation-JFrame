import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import javax.swing.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyPanel extends JPanel implements ActionListener, ComponentListener {

    private static final int NUM_THREADS = Runtime.getRuntime().availableProcessors();
    private Particle testMin;
    double maxDensity;
    Timer timer;


    private final int DesiredFrameRate = 0; //33 = 30fps, 16 = 60fps, 8 = 120fps , 0 = 180fps
    private final int numParticle = 3000;
    private double smoothingRadius = 3; //0.3 //3

    private int mouseRadius = 10;
    private int mouseStrength = 5;
    double pressureMultiplier = 7;//4.5;
    double targetDensity = 1; //1.5

    final double BOUNCE_FACTOR = 0.3;
    double GRAVITY = 0; // 9.8

    private static final int sim_speed = 1;

    private double deltaTime;
    private long myLastTime;

    private long lastTime;
    private int frameCount;
    double fps;

    private MyFrame frame;


    private int PANEL_WIDTH = 1000;
    private int PANEL_HEIGHT = 1000;


    final double CircleDiameter = 20;


    private final Particle[] listOfParticles;

    private double[] density;
    private double[] vx;
    private double[] vy;

    private int startHeight = 300;
    private boolean isPaused = false;

    private double maxVelocity;
    private boolean gravityOn = false;
    private boolean drawCircle = true;
    private boolean drawGradient = false;


    public MyPanel(){
        this.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        this.setBackground(Color.darkGray);
        this.setOpaque(true);
        this.addComponentListener(this);

        listOfParticles = new Particle[numParticle];
        density = new double[numParticle];
        vx = new double[numParticle];
        vy = new double[numParticle];
        //listOfParticles = new Particle[2];
        //listOfParticles[0] = new Particle(500, 500, CircleDiameter, 0);
        //listOfParticles[1] = new Particle(500, 500, CircleDiameter, 1);
        Random rand = new Random();

        for(int i = 0; i < numParticle; i++){
            // Move up to next stack of particles



            // add x & start height
            listOfParticles[i] = new Particle(rand.nextInt(0, PANEL_WIDTH), rand.nextInt(0, PANEL_HEIGHT), CircleDiameter, i);
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


        //Find max density
        double maxDensity = 0.0001;
        double minDensity = listOfParticles[0].density;

        for(Particle particle : listOfParticles){
            if(maxDensity < particle.density){
                maxDensity = particle.density;
            }else if(minDensity > particle.density){
                minDensity = particle.density;
                testMin = particle;
            }
        }
        //System.out.println("Max Density: " + maxDensity + " Minimum Density: " + minDensity);

        /*
        for(Particle particle : listOfParticles){
             double currentDensity = (float) particle.density / maxDensity;
             int red = (int) (255 * ((particle.density - minDensity) / (maxDensity - minDensity))); // particle density = x(i)
             int green = (int) ( 255 * ((particle.density - minDensity) / (maxDensity - minDensity)));
             int blue = 255;

             //System.out.println("Density: " + particle.density + " Red : " + red + " Green : " + green + " Blue : " + blue);
             Color color = new Color(red, green ,blue);
             g2d.setColor(color);
             int width = 10;
             int height = 10;
             g2d.fillRect((int) particle.position.getX() - width, (int) particle.position.getY() - height, (int) particle.position.getX() + width, (int) particle.position.getY() + height);
        }
         */


        //Draw each gradient
        if(drawGradient){
            for (Particle particle : listOfParticles) {

                float centerX = (float) (particle.position.getX() + particle.diameter / 2);
                float centerY = (float) (particle.position.getY() + particle.diameter / 2);

                float num = 7.5f;
                float gradientRadius = ((float) CircleDiameter / 2) * num;

                RadialGradientPaint radialPaint = new RadialGradientPaint(
                        (float) particle.position.getX(), (float) particle.position.getY(), gradientRadius,
                        new float[]{0f, 1f},
                        new Color[]{getColor(particle.velocity.magnitude()), Color.darkGray},
                        MultipleGradientPaint.CycleMethod.NO_CYCLE
                );

                g2d.setPaint(radialPaint);
                g2d.fillOval((int) (centerX - gradientRadius), (int) (centerY - gradientRadius), (int) (2 * gradientRadius), (int) (2 * gradientRadius));

            }
        }

        //Draw each circle
        if(drawCircle){
            for(Particle particle: listOfParticles){
                //g2d.setColor(Color.CYAN);
                if(maxVelocity == 0){Color c = new Color(0, 0, 255); g2d.setColor(c);}
                else{g2d.setColor(getColor(particle.velocity.magnitude()));}

                g2d.fillOval((int) particle.position.getX(), (int) particle.position.getY(), (int) particle.diameter, (int) particle.diameter);
                //g2d.fillOval((int) particle.x, (int) particle.y, (int) particle.diameter, (int) particle.diameter);
            }
        }




        // Draw the FPS counter
        g2d.setColor(Color.BLACK);
        g2d.drawString(String.format("FPS: %.2f", fps), 10, 20);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(isPaused){return;}
        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);

        try{
            long myCurrentTime = System.nanoTime();
            deltaTime = ((myCurrentTime - myLastTime) / 1_000_000_000.0) * sim_speed;
            myLastTime = myCurrentTime;

            java.util.List<Future<Void>> gravityFutures = new ArrayList<>();
            for(Particle c : listOfParticles){
                gravityFutures.add(executor.submit(() -> {
                    c.addGravity(listOfParticles ,GRAVITY, deltaTime);
                    return null;
                }));
            }
            waitForCompletion(gravityFutures);

            maxDensity = Double.MIN_VALUE;

            java.util.List<Future<Void>> densityFutures = new ArrayList<>();
            for(Particle c : listOfParticles){
                densityFutures.add(executor.submit(() -> {
                    c.addDensity(listOfParticles, smoothingRadius, density);
                    if(c.density > maxDensity){maxDensity = c.density;}
                    return null;
                }));
            }
            waitForCompletion(densityFutures);


            java.util.List<Future<Void>> maxFutures = new ArrayList<>();
            for(Particle c : listOfParticles){
                maxFutures.add(executor.submit(() -> {
                    c.density /= maxDensity;
                    return null;
                }));
            }
            waitForCompletion(maxFutures);



            java.util.List<Future<Void>> pressureFutures = new ArrayList<>();
            for(Particle c : listOfParticles){
                pressureFutures.add(executor.submit(() -> {
                    c.applyPressureForce(listOfParticles, smoothingRadius, deltaTime, pressureMultiplier, targetDensity);
                    return null;
                }));
            }
            waitForCompletion(pressureFutures);

            maxVelocity = 0;
            java.util.List<Future<Void>> updateFutures = new ArrayList<>();
            for(Particle c : listOfParticles){
                updateFutures.add(executor.submit(() -> {
                    Vector2D interactionForce = new Vector2D(0,0);
                    if(frame != null && frame.isMouseHeld()){
                        Point mousePos = frame.getMousePosition();
                        if(mousePos != null){
                            //Vector2D mouse = new Vector2D(mousePos.getX(), mousePos.getY());
                            //System.out.println("X = " + mouse.getX() + " Y = " + mouse.getY());
                            interactionForce = c.applyInteraction(mousePos, mouseRadius, mouseStrength);
                        }
                    }
                    if(mouseStrength == 0 || mouseRadius == 0){interactionForce = new Vector2D(0,0);}
                    assert frame != null;
                    c.update(PANEL_WIDTH, PANEL_HEIGHT, listOfParticles ,BOUNCE_FACTOR, interactionForce, frame.whichButton());
                    if(maxVelocity < c.velocity.magnitude() ){maxVelocity = c.velocity.magnitude();}
                    vx[c.index] = c.velocity.getX();
                    vy[c.index] = c.velocity.getY();
                    return null;
                }));
            }
            waitForCompletion(updateFutures);


        } finally {
            executor.shutdown();
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


    public void togglePause() {
        isPaused = !isPaused;
        if(!isPaused){
            lastTime = System.currentTimeMillis();

            myLastTime = System.nanoTime();
            deltaTime = 0.0;

            frameCount = 0;
        }
    }

    private Color getColor(double magnitude){
        double fraction =  magnitude / maxVelocity;

        int blue = (int) (255 * (1 - fraction));
        int green = 0;
        int red = (int) (255 * fraction);

        return new Color(red, green, blue);
    }

    public void setFrame(MyFrame frame){ this.frame = frame;}

    private static void waitForCompletion(java.util.List<Future<Void>> futures) {
        for (Future<Void> future : futures) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    public void adjustStrength(int a) {
        mouseStrength += a;
        if(mouseStrength <= 0){mouseStrength = 0;}
        System.out.println("Mouse Strength = " + mouseStrength);
    }
    public void adjustMouseRadius(int a){
        mouseRadius += a;
        if(mouseRadius <= 0){mouseRadius = 0;}
        System.out.println("Mouse Radius = " + mouseRadius);
    }

    public void adjustSmoothingRadius(double v) {
        smoothingRadius += v;
        if(smoothingRadius <= 0){smoothingRadius = 0.1;}
        System.out.println("Smoothing Radius = " + smoothingRadius);
    }

    public void keyGravity() {
        gravityOn = !gravityOn;
        GRAVITY = gravityOn ? 9.8 : 0;
        if(gravityOn){System.out.println("Gravity On");}else{System.out.println("Gravity Off");}
    }

    public void Circle() {
        drawCircle = !drawCircle;
    }

    public void Gradient() {
        drawGradient = !drawGradient;
    }

    public void adjustTargetDensity(double v) {
        targetDensity = (targetDensity + v <= 0) ? 0.1 : targetDensity + v;
        System.out.println("Target Density = " + targetDensity);
    }

    public void adjustPressureMultiplier(double v) {
        pressureMultiplier = (pressureMultiplier + v <= 0) ? 0.1 : pressureMultiplier + v;
        System.out.println("Pressure Multiplier = " + pressureMultiplier);
    }
}
