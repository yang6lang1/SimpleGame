import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

//Does not extend SolidRectangle on purpose
public class LocalPlayer extends Movable
{

    int width, height;
    boolean crouching;
    int crouchingHeightChange;

    Color color;

    GameController controller;

    public LocalPlayer(int x, int y, int width, int height, GameContent game)
    {
        super(x, y, 8, 18, game);
        this.width = width;
        this.height =  height;

        this.color = Color.WHITE;

        this.crouching = false;
        crouchingHeightChange = (int)(height*.3);

    }

    public void control()
    {
        ControllerState state = controller.getControllerState();
        if (state.exit)
        {
            controller.hasBeenHandled(controller.keyExit);
            game.panel.loadSavedState();
//            game.saveState();
//            System.exit(0);
        }

        if (state.select)
        {
            game.toggleReferenceFrame();
            controller.hasBeenHandled(controller.keySelect);
            
        }

        // Slow motion for debugging purposes
        if (state.start)
        {
//            game.panel.toggleTimerSpeed();
            controller.hasBeenHandled(controller.keyStart);
            game.panel.setSaveState();
        }

        if (state.left) ddx = -1;
        else if (state.right) ddx = 1;
        else ddx = 0;

        if (state.down)
        {
            if (!crouching)
            {
                height -= crouchingHeightChange;
                y += crouchingHeightChange;
                crouching = true;
            }
        }
        else
        {
            if (crouching && roomToUncrouch())
            {
                y -= crouchingHeightChange;
                height += crouchingHeightChange;
                crouching = false;
            }
        }

        if (state.jump) jump();
    }

    public void update()
    {
        super.update();
        if (turning()) dx /= 2;
    }

    public boolean turning()
    {
        return dx * ddx <= 0;
    }

    public boolean roomToUncrouch()
    {
        return game.findSolidRectanglesInArea(new Rectangle(x, y - crouchingHeightChange, width, height)).isEmpty();
    }

    public void jump()
    {
        dy = -10;
    }

    public void land()
    {
        dy = ddy = 0;
    }

    public void draw(Graphics g)
    {
        g.setColor(color);
        g.fillRect(x, y, width, height);
    }

    public Rectangle toRectangle()
    {
        return new Rectangle(x, y, width, height);
    }

}
