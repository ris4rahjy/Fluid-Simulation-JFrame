import java.awt.*;
import java.util.Random;
public class Particle {
    int x, y, diameter;
    int maxY;
    double xVel, yVel;

    private static final double GRAVITY = 9.8;

    public Particle(int posX, int posY, double xVel, double yVel, int diameter){
        this.x = posX;
        this.y = posY;

        //Intial Max Height is Initial Y value
        this.maxY = posY;

        this.xVel = xVel;
        this.yVel = yVel;
        this.diameter = diameter;

    }

    public void updatePosition(int PanelWidth, int PanelHeight){

        //Update Y Position
        if(y <= 0 || y + diameter >= PanelHeight){
            yVel = yVel * -1;
        }

        //Update X Position
        if(x <= 0 || x + diameter >= PanelWidth){
            xVel = xVel * -1;
        }

        y += yVel;
        x += xVel;

    }

    public boolean intersects(Particle other){
        // Calculate distance between centers
        int dx = (x + (diameter / 2)) - (other.x + other.diameter / 2);
        int dy = (y + (diameter / 2)) - (other.y + other.diameter / 2);
        double distance = Math.sqrt(dx * dx + dy * dy);

        //Check if Distance is less than sum of radi
        return distance < (diameter + other.diameter) / 2;
    }

    public void reverseVelocity(){
        xVel = -xVel;
        yVel = -yVel;
    }

    public void applyGravity(){
        //Apply Gravity to Y Velocity
        yVel += GRAVITY;
    }

    public boolean atBottom(int PanelHeight){
        if(y < 0 || y >= PanelHeight-diameter){
            System.out.println("At Bottom");
            return true;
        }
        System.out.println("Not at Bottom");
        return false;
    }





}
