import java.awt.*;

public class Particle {
    final double mass = 1;
    double diameter;
    double density;
    int index;
    Vector2D velocity;
    Vector2D position;
    Vector2D predictedPosition;
    //double pressureMultiplier = 7;//4.5;
    //double targetDensity = 1; //1.5

    final double minSeparationDistance = 1; // AND TAKE OFF THE FUNCTION THAT USES THIS

    public Particle(double posX, double posY, double diameter, int index){

        this.position = new Vector2D(posX, posY);
        this.velocity = new Vector2D(0, 0);
        this.index = index;
        this.diameter = diameter;

    }



    private Vector2D calculatePressure(Particle[] listOfParticles, double smoothingRadius, double pressureMultiplier, double targetDensity) {
        Vector2D pressureForce = new Vector2D(0,0);

        for(Particle p : listOfParticles){
            if(this.index == p.index){continue;}

            //Vector2D offset = p.position.subtract(this.position);
            //double dst = offset.magnitude();
            double dst = calculateDistance(p, this);
            Vector2D dir;
            if(dst == 0){
                dir = getRandomDir();
            }else{
                dir = new Vector2D(((p.getCenterX() - this.getCenterX()) / dst), ((p.getCenterY() - this.getCenterY()) / dst));
                //dir = new Vector2D((offset.getX() / dst), (offset.getY() / dst)); // Maybe wrong
            }

            double slope = SmoothingKernelDerivative(smoothingRadius, dst);
            double currentDensity = p.density;
            double sharedPressure = CalculateSharedPressure(currentDensity, this.density, pressureMultiplier, targetDensity);

            //Vector2D forceContribution = dir.multiply(sharedPressure * slope * mass / density);
            //pressureForce + sharedPressure * dir * slope * mass / density
            dir.multiply(sharedPressure);
            dir.multiply(slope);
            dir.multiply(mass);
            dir = new Vector2D(dir.getX() / this.density, dir.getY() / this.density);
            pressureForce.add(dir);

            //pressureForce.add(forceContribution);
        }

        return pressureForce;
    }

    private double CalculateSharedPressure(double densityOne, double densityTwo, double pressureMultiplier, double targetDensity) {
        double pressureOne = convertDensityToPressure(densityOne, pressureMultiplier, targetDensity);
        double pressureTwo = convertDensityToPressure(densityTwo, pressureMultiplier, targetDensity);
        return (pressureOne + pressureTwo) / 2;
    }

    private double convertDensityToPressure(double d, double pressureMultiplier, double targetDensity) {
        double densityError = d - targetDensity;
        return densityError * pressureMultiplier;
    }

    private static Vector2D getRandomDir() {
        double angle = Math.random() * 2 * Math.PI;
        return new Vector2D(Math.cos(angle), Math.sin(angle));
    }

    private void calculateDensity(Particle[] listOfParticles, double smoothingRadius, double[] d) {
        density = 0;

        for(Particle p : listOfParticles){
            //if (p.index == index){continue;}
            //Vector2D dstVector = new Vector2D((p.predictedPosition.getX() - this.predictedPosition.getX()),(p.predictedPosition.getY() - this.predictedPosition.getY()));
            //double dst = dstVector.magnitude();
            double dst = calculateDistance(p, this);
            double influence = SmoothingKernel(smoothingRadius, dst);
            density += mass * influence;
        }

        d[index] = density;
    }

    private Vector2D calculateVelocity(double GRAVITY, double deltaTime) {
        Vector2D propulsionEffect = new Vector2D(0, 1);
        propulsionEffect.multiply(GRAVITY * deltaTime);

        velocity.add(propulsionEffect); //This is yVel

        //In order to not change Velocity, create new temp Velocity
        //return new Vector2D(velocity.getX() * deltaTime, velocity.getY() * deltaTime);
        return new Vector2D(velocity.getX() * deltaTime, velocity.getY() * deltaTime);
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
            //velocity.setY(temp * -1);
        }

        //Resolve Width Bound Issues
        if(position.getX() >= PanelWidth - diameter || position.getX() < 0){
            if(position.getX() >= PanelWidth - diameter){
                position.setX(PanelWidth - diameter);
            }else if(position.getX() < 0){
                position.setX(0);
            }
            double temp = velocity.getX();
            velocity.setX(temp * -1 * collisionDamping);
            //velocity.setX(temp * -1);
        }


    }

    //TO DO: Fix Density Functions
    public double SmoothingKernel(double radius, double dst){
        if(dst >= radius){return 0;}
        double volume = (Math.PI * Math.pow(radius, 4)) / 6;
        return (radius - dst) * (radius - dst) / volume;

    }
    public double SmoothingKernelDerivative(double radius, double dst){
        if(dst >= radius){return 0;}
        double scale = 12 / (Math.pow(radius, 4) * Math.PI);
        return (dst - radius) * scale;
    }


    public void addGravity(Particle[] listOfParticles, double GRAVITY, double deltaTime) {
        Vector2D velCalculation = calculateVelocity(GRAVITY, deltaTime);
        //velocity.add(velCalculation);

        //Predict next position
        Vector2D temp = new Vector2D(velocity.getX(), velocity.getY());
        temp.multiply(deltaTime);
        //predictedPosition = new Vector2D(position.getX() + temp.getX(), position.getY() + temp.getY());---------------------------------------------------------------------------
        predictedPosition = new Vector2D(getCenterX() + (temp.getX() + diameter / 2), getCenterY() + (temp.getY() + diameter / 2));
    }

    public void addDensity(Particle[] listOfParticles, double smoothingRadius, double[] d) {
        calculateDensity(listOfParticles, smoothingRadius, d);
    }

    public void applyPressureForce(Particle[] listOfParticles, double smoothingRadius, double deltaTime, double pressureMultiplier, double targetDensity) {
        Vector2D pressureForce = calculatePressure(listOfParticles, smoothingRadius, pressureMultiplier, targetDensity);
        Vector2D pressureAcceleration = new Vector2D((pressureForce.getX() / density), (pressureForce.getY() / density));
        pressureAcceleration.multiply(deltaTime);


        //velocity.multiply(0.99); //Take this out ---------------------------------------------------------------------------------
        velocity.add(pressureAcceleration); //With Inertia
        //velocity = pressureAcceleration; //without Inertia

    }

    public void update(int PANEL_WIDTH, int PANEL_HEIGHT,Particle[] listofParticles , double collisionDamping, Vector2D interactionForce, boolean button) {
        position.add(velocity);

        position = !button ? position.add(interactionForce) : position.subtract(interactionForce);


        //position.add(interactionForce);
        //position = position.subtract(interactionForce);

        resolveCollisions(PANEL_WIDTH, PANEL_HEIGHT, collisionDamping);
        //enforceMinimumSeparation(listofParticles);
    }

    private void enforceMinimumSeparation(Particle[] listOfParticles) {
        for(Particle other : listOfParticles){
            if(other == this){ continue;}
            //Vector2D offset = other.position.subtract(position);
            Vector2D offset = new Vector2D(other.getCenterX() - this.getCenterX(), other.getCenterY() - this.getCenterY());
            //double distance = offset.magnitude();
            double distance = calculateDistance(other, this);
            if(distance > 0 && distance < minSeparationDistance){
                //Calculate displacement vector to move particle apart
                //Vector2D displacement = offset.multiply((minSeparationDistance - distance) / distance);
                //position = position.subtract(displacement);
                double factor = (minSeparationDistance - distance) / distance;
                double dx = offset.getX() * factor;
                double dy = offset.getY() * factor;
                //position.setX(position.getX() + dx); //Incorrect Way
                //position.setY(position.getY() + dy);
                position.setX(position.getX() - dx); //Correct Way
                position.setY(position.getY() - dy);
            }
        }
    }



    public double getCenterX(){
        return position.getX() + diameter / 2;
    }
    public double getCenterY(){
        return position.getY() + diameter / 2;
    }

    private double calculateDistance(Particle p1, Particle p2){
        double dx = p1.getCenterX() - p2.getCenterX();
        double dy = p1.getCenterY() - p2.getCenterY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    public Vector2D applyInteraction(Point mousePos, double radius, int strength) {
        Vector2D interactionForce = new Vector2D(0, 0);
        Vector2D offset = new Vector2D((mousePos.getX() - getCenterX()),(mousePos.getY() - getCenterY()));
        double sqrDst = (offset.getX() * offset.getX()) + (offset.getY() * offset.getY());
        Vector2D dirToInput;
        if (sqrDst < radius * radius) {
            double dst = Math.sqrt(sqrDst);
            if(dst <= Float.MIN_VALUE){
                dirToInput = new Vector2D(0, 0);
            }else{
                dirToInput = new Vector2D((offset.getX() / dst), (offset.getY() / dst));
            }
            double centerT = 1 - dst / radius;
            dirToInput = dirToInput.multiply(strength);
            dirToInput = dirToInput.subtract(velocity);
            dirToInput.multiply(centerT);
            interactionForce.add(dirToInput);
        }

        return interactionForce;
    }
}
