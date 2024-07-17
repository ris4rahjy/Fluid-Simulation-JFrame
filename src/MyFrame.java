import java.awt.*;
import java.awt.event.*;
import java.security.Key;
import javax.swing.*;

public class MyFrame extends JFrame implements MouseListener, MouseMotionListener  {
    MyPanel panel;
    private boolean isMouseHeld;
    private Point mousePosition;
    private boolean leftMouse;

    MyFrame(){
        panel = new MyPanel();
        panel.setFrame(this);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.add(panel);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);

        ImageIcon img = new ImageIcon("src/ruby.png");
        this.setIconImage(img.getImage());

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_P){
                    panel.togglePause();
                } else if(e.getKeyCode() == KeyEvent.VK_W){ //Start Mouse
                    panel.adjustStrength(1); //Little increase
                }else if(e.getKeyCode() == KeyEvent.VK_S){
                    panel.adjustStrength(-1); //Little decrease
                }else if(e.getKeyCode() == KeyEvent.VK_E){
                    panel.adjustStrength(5); //Bigger increase
                }else if(e.getKeyCode() == KeyEvent.VK_D){
                    panel.adjustStrength(-5); //Bigger decrease
                }else if(e.getKeyCode() == KeyEvent.VK_HOME){ //Start Radius
                    panel.adjustMouseRadius(1); //Little Increase
                }else if(e.getKeyCode() == KeyEvent.VK_END){
                    panel.adjustMouseRadius(-1); //Little Decrease
                }else if(e.getKeyCode() == KeyEvent.VK_PAGE_UP){
                    panel.adjustMouseRadius(5); //Bigger Increase
                }else if(e.getKeyCode() == KeyEvent.VK_PAGE_DOWN){
                    panel.adjustMouseRadius(-5); //Bigger Decrease
                }else if(e.getKeyCode() == KeyEvent.VK_COMMA){
                    panel.adjustSmoothingRadius(-0.1f);
                }else if(e.getKeyCode() == KeyEvent.VK_PERIOD){
                    panel.adjustSmoothingRadius(0.1f);
                }else if(e.getKeyCode() == KeyEvent.VK_G){
                    panel.keyGravity();
                }else if(e.getKeyCode() == KeyEvent.VK_I){
                    panel.Circle();
                }else if(e.getKeyCode() == KeyEvent.VK_O){
                    panel.Gradient();
                }else if(e.getKeyCode() == KeyEvent.VK_NUMPAD4){
                    panel.adjustTargetDensity(.1f);
                }else if(e.getKeyCode() == KeyEvent.VK_NUMPAD1){
                    panel.adjustTargetDensity(-.1f);
                }else if(e.getKeyCode() == KeyEvent.VK_NUMPAD5){
                    panel.adjustPressureMultiplier(.1f);
                }else if(e.getKeyCode() == KeyEvent.VK_NUMPAD2){
                    panel.adjustPressureMultiplier(-.1f);
                }
            }
        });

        panel.addMouseListener(this); //
        panel.addMouseMotionListener(this); //

        isMouseHeld = false;
        mousePosition = null;

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        leftMouse = SwingUtilities.isLeftMouseButton(e); //True = Left Mouse, False = Right Mouse
    }

    @Override
    public void mousePressed(MouseEvent e) {
        isMouseHeld = true;
        mousePosition = e.getPoint();
        leftMouse = SwingUtilities.isLeftMouseButton(e); //True = Left Mouse, False = Right Mouse
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        isMouseHeld = true;
        mousePosition = null;
        leftMouse = SwingUtilities.isLeftMouseButton(e); //True = Left Mouse, False = Right Mouse
    }

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mouseDragged(MouseEvent e) {mousePosition = e.getPoint();}

    @Override
    public void mouseMoved(MouseEvent e) {}

    public boolean isMouseHeld(){return isMouseHeld;}
    public Point getMousePosition(){return mousePosition;}

    public boolean whichButton(){return leftMouse;}



}
