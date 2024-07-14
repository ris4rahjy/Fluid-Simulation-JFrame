public class Vector2D {
    private double x, y;

    public static final Vector2D down = new Vector2D(0, 1);
    public Vector2D(double x, double y){
        this.x = x;
        this.y = y;
    }

    public Vector2D subtract(Vector2D other) {
        return new Vector2D(this.x - other.x, this.y - other.y);
    }

    public Vector2D add(Vector2D other) {
        this.x += other.x;
        this.y += other.y;
        return this;
    }

    public double magnitude() {
        return Math.sqrt(x * x + y * y);
    }

    public double getX(){
        return x;
    }

    public double getY(){
        return y;
    }

    public void setX(double x){
         this.x = x;
    }
    public void setY(double y){
        this.y = y;
    }

    public Vector2D multiply(double scalar){
        this.x *= scalar;
        this.y *= scalar;
        return this;
    }






}
