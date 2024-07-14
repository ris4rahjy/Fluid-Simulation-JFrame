public class Particle {
    double diameter;
    Vector2D velocity;
    Vector2D position;



    public Particle(double posX, double posY, double xVel, double yVel, double diameter){

        this.position = new Vector2D(posX, posY);
        this.velocity = new Vector2D(0, 1);

        this.diameter = diameter;

    }


    public void updatePosition(int PanelWidth, int PanelHeight, double collisionDamping, double deltaTime, double GRAVITY){

        //Create Velocity Vector
        Vector2D propulsionEffect = new Vector2D(0, 1);
        propulsionEffect.multiply(GRAVITY * deltaTime);

        velocity.add(propulsionEffect); //This is yVel

        //In order to not change Velocity, create new temp Velocity
        Vector2D velCalculation = new Vector2D(velocity.getX() * deltaTime, velocity.getY() * deltaTime);

        position.add(velCalculation);



        //Resolve bounding errors
        resolveCollisions(PanelWidth, PanelHeight, collisionDamping);

        //Debug velocity & position stamps
        System.out.println("Delta Time = "  + deltaTime + " X = " + (int) position.getX() + " Y = " +  (int) position.getY() +  " Velocity Calculated = " + velocity.getY());

    }

    private void resolveCollisions(int PanelWidth, int PanelHeight, double collisionDamping) {

        //Resolve Height Bound Issues
        if(position.getY() >= PanelHeight - diameter || position.getY() < 0){
            if(position.getY() >= PanelHeight - diameter){
                position.setY(PanelHeight - diameter);
            }else if(position.getY() < 0){
                position.setY(0);
            }
            double temp = velocity.getY();
            velocity.setY(temp * -1 * collisionDamping);
        }

        //Resolve Width Bound Issues
        if(position.getX() >= PanelWidth - diameter || position.getX() < 0){
            if(position.getX() >= PanelWidth - diameter){
                position.setX(PanelWidth - diameter);
            }else if(position.getX() < 0){
                position.setX(0);
            }
            velocity.setX(velocity.getX() * -1 * collisionDamping);
        }

    }

    public double SmoothingKernel(float diameter, double dst){
        double value = Math.max(0.0, ((diameter / 2) * (diameter / 2) - (dst * dst)));
        double newVal = (value * value * value);
        return newVal;
    }


    /*
    public boolean intersects(Particle other){
        // Calculate distance between centers
        double dx = (x + (diameter / 2)) - (other.x + other.diameter / 2);
        double dy = (y + (diameter / 2)) - (other.y + other.diameter / 2);
        double distance = Math.sqrt(dx * dx + dy * dy);

        //Check if Distance is less than sum of radi
        return distance < (diameter + other.diameter) / 2;
    }

     */

}
