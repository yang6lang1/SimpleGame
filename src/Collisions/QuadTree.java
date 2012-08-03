import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;

public class QuadTree implements Serializable
{
    static final int MAX_LEVEL = 9;
    static final int BRANCHING_LIMIT = 32;
    static final int MAX_NUM_LEAVES = 1 << MAX_LEVEL;
    static final int WIDTH = GameContent.GAME_WIDTH;
    static final int HEIGHT = GameContent.GAME_HEIGHT;

    private Node root;
    ArrayList<ArrayList<Drawable>> leaves;

    // ArrayList<Node> leafNodes;

    public QuadTree()
    {
        leaves = new ArrayList<ArrayList<Drawable>>(MAX_NUM_LEAVES);
        // leafNodes = new ArrayList<Node>(MAX_NUM_LEAVES);
        root = new Node(0, WIDTH, 0, HEIGHT, 0);
    }

    public QuadTree(QuadTree old)
    {
        leaves = new ArrayList<ArrayList<Drawable>>((int)(old.leaves.size() * 1.2));
        // leafNodes = new ArrayList<Node>((int)(old.leaves.size() * 1.2));
        root = new Node(0, old.root);
    }

    // public void draw(Graphics g)
    // {
    // g.setColor(Color.gray);
    // for(Node node : leafNodes)
    // {
    // g.drawRect(node.xMin, node.yMin, node.xMax - node.xMin, node.yMax -
    // node.yMin);
    // }
    // }

    public void add(Drawable drawable)
    {
        root.add(drawable);
    }

    public ArrayList<Drawable> getDrawablesInRegion(Rectangle region)
    {
        ArrayList<Drawable> drawables = new ArrayList<Drawable>();
        if(root.intersectsRectangle(region))
        {
            root.addDrawablesInRegion(region, drawables);
        }
        return drawables;
    }

    public ArrayList<Drawable> getDrawablesIntersecting(Drawable region)
    {
        ArrayList<Drawable> drawables = new ArrayList<Drawable>();
        if(root.intersectsDrawable(region))
        {
            root.addDrawablesInRegion(region, drawables);
        }
        return drawables;
    }

    private class Node implements Serializable
    {
        int level;
        int xMin, xMax;
        int yMin, yMax;
        Node upperLeft, upperRight, lowerLeft, lowerRight;
        int drawableCount;
        ArrayList<Drawable> drawablesInRegion;

        public Node(int xMin, int xMax, int yMin, int yMax, int level)
        {
            this.xMin = xMin;
            this.xMax = xMax;
            this.yMin = yMin;
            this.yMax = yMax;
            this.level = level;

            drawableCount = 0;

            if(level < MAX_LEVEL)
            {
                int xMid = xMin / 2 + xMax / 2;
                int yMid = yMin / 2 + yMax / 2;
                int nextLevel = level + 1;
                upperLeft = new Node(xMin, xMid, yMin, yMid, nextLevel);
                upperRight = new Node(xMid, xMax, yMin, yMid, nextLevel);
                lowerLeft = new Node(xMin, xMid, yMid, yMax, nextLevel);
                lowerRight = new Node(xMid, xMax, yMid, yMax, nextLevel);
            }
            else
            {
                // speed consideration
                // only the leaves have non-null arraylists
                drawablesInRegion = new ArrayList<Drawable>();
                leaves.add(drawablesInRegion);
                // leafNodes.add(this);
            }
        }

        public Node(int xMin, int xMax, int yMin, int yMax, int stopLevel, boolean dummy)
        {
            this.xMin = xMin;
            this.xMax = xMax;
            this.yMin = yMin;
            this.yMax = yMax;
            this.level = stopLevel;

            drawableCount = 0;

            drawablesInRegion = new ArrayList<Drawable>();
            leaves.add(drawablesInRegion);
            // leafNodes.add(this);
        }

        public Node(int level, Node oldNode)
        {

            this.xMin = oldNode.xMin;
            this.xMax = oldNode.xMax;
            this.yMin = oldNode.yMin;
            this.yMax = oldNode.yMax;
            this.level = oldNode.level;

            drawableCount = 0;

            if(level < MAX_LEVEL && oldNode.drawableCount > BRANCHING_LIMIT)
            {
                int nextLevel = level + 1;
                if(oldNode.isLeaf())
                {
                    int xMid = xMin / 2 + xMax / 2;
                    int yMid = yMin / 2 + yMax / 2;

                    upperLeft = new Node(xMin, xMid, yMin, yMid, nextLevel, true);
                    upperRight = new Node(xMid, xMax, yMin, yMid, nextLevel, true);
                    lowerLeft = new Node(xMin, xMid, yMid, yMax, nextLevel, true);
                    lowerRight = new Node(xMid, xMax, yMid, yMax, nextLevel, true);
                }
                else
                {
                    upperLeft = new Node(nextLevel, oldNode.upperLeft);
                    upperRight = new Node(nextLevel, oldNode.upperRight);
                    lowerLeft = new Node(nextLevel, oldNode.lowerLeft);
                    lowerRight = new Node(nextLevel, oldNode.lowerRight);
                }
            }
            else
            {
                // speed consideration
                // only the leaves have non-null arraylists
                drawablesInRegion = new ArrayList<Drawable>();
                leaves.add(drawablesInRegion);
                // leafNodes.add(this);
            }
        }

        public boolean isLeaf()
        {
            return this.lowerLeft == null;
        }

        // Assumes it intersects the root
        public void addDrawablesInRegion(Rectangle region, ArrayList<Drawable> drawables)
        {
            if(this.isLeaf())
            {
                drawables.addAll(drawablesInRegion);
            }
            else
            {
                if(upperLeft.intersectsRectangle(region))
                    upperLeft.addDrawablesInRegion(region, drawables);
                if(upperRight.intersectsRectangle(region))
                    upperRight.addDrawablesInRegion(region, drawables);
                if(lowerLeft.intersectsRectangle(region))
                    lowerLeft.addDrawablesInRegion(region, drawables);
                if(lowerRight.intersectsRectangle(region))
                    lowerRight.addDrawablesInRegion(region, drawables);
            }

        }

        // Assumes it intersects the root
        public void addDrawablesInRegion(Drawable region, ArrayList<Drawable> drawables)
        {
            if(this.isLeaf())
            {
                drawables.addAll(drawablesInRegion);
            }
            else
            {
                if(upperLeft.intersectsDrawable(region))
                    upperLeft.addDrawablesInRegion(region, drawables);
                if(upperRight.intersectsDrawable(region))
                    upperRight.addDrawablesInRegion(region, drawables);
                if(lowerLeft.intersectsDrawable(region))
                    lowerLeft.addDrawablesInRegion(region, drawables);
                if(lowerRight.intersectsDrawable(region))
                    lowerRight.addDrawablesInRegion(region, drawables);
            }

        }

        public void add(Drawable drawable)
        {
            ArrayList<Node> queue = new ArrayList<Node>(64);
            queue.add(this);
            Node current;
            int lastIndex;
            while((lastIndex = queue.size()-1) >= 0)
            {
                current = queue.remove(lastIndex);
                if(current.intersectsDrawable(drawable))
                {
                    current.drawableCount++;
                    if(!current.isLeaf())
                    {
                        queue.add(current.upperLeft);
                        queue.add(current.upperRight);
                        queue.add(current.lowerLeft);
                        queue.add(current.lowerRight);
                    }
                    else
                    {
                        // only leaves have non-null arraylists
                        current.drawablesInRegion.add(drawable);
                    }
                }
            }
        }

        public boolean intersectsDrawable(Drawable drawable)
        {
            return(drawable.x + drawable.width >= xMin && drawable.x < xMax && drawable.y + drawable.height >= yMin && drawable.y < yMax);
        }

        public boolean intersectsRectangle(Rectangle rectangle)
        {
            return(rectangle.x + rectangle.width >= xMin && rectangle.x < xMax && rectangle.y + rectangle.height >= yMin && rectangle.y < yMax);
        }

    }
}