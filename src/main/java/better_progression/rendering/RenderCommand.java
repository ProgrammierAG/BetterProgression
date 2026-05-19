package better_progression.rendering;

import better_progression.skillTree.nodeTypes.Node;

public interface RenderCommand {

    class Line implements RenderCommand {
        private final int startX;
        private final int startY;
        private final Node targetNode;
        private final int color;

        public Line(int startX, int startY, Node targetNode, int color) {
            this.startX = startX;
            this.startY = startY;
            this.targetNode = targetNode;
            this.color = color;
        }

        public int getStartX() { return startX; }
        public int getStartY() { return startY; }
        public Node getTargetNode() { return targetNode; }
        public int getColor() { return color; }
    }

    // Ein Befehl zum Zeichnen eines Button-Hintergrunds (bleibt unverändert)
    class Background implements RenderCommand {
        private final Node node;
        private final int x;
        private final int y;
        private final int textureType;

        public Background(Node node, int x, int y, int textureType) {
            this.node = node;
            this.x = x;
            this.y = y;
            this.textureType = textureType;
        }

        public Node getNode() { return node; }
        public int getX() { return x; }
        public int getY() { return y; }
        public int getTextureType() { return textureType; }
    }
}
