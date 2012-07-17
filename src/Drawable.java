import java.awt.Graphics;
import java.io.Serializable;

public abstract class Drawable implements Serializable
{
    int x, y;
    GameContent game;

    public Drawable(int x, int y, GameContent game)
    {
        this.x = x;
        this.y = y;
        this.game = game;
    }

    public void unconditionalShift(int dx, int dy)
    {
        x += dx;
        y += dy;
    }

    public void remove()
    {
        game.remove(this);
    }

    public abstract void draw(Graphics g);
    
    public boolean isOnScreen()
    {
        return true;
    }
}
