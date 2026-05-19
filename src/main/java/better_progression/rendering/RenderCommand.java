package better_progression.rendering;

import better_progression.skillTree.nodeTypes.ChoiceNode;
import better_progression.skillTree.nodeTypes.Node;

public interface RenderCommand {
    class Line implements RenderCommand {
        private final int startX, startY;
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

    class Background implements RenderCommand {
        private final Node node;
        private final int x, y, textureType;

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

    // NEU: Befehl für das kombinierte 2-in-1 Choice Rendering
    class ChoiceBackground implements RenderCommand {
        private final ChoiceNode choiceNode;
        private final int x, y;

        public ChoiceBackground(ChoiceNode choiceNode, int x, int y) {
            this.choiceNode = choiceNode;
            this.x = x;
            this.y = y;
        }
        public ChoiceNode getChoiceNode() { return choiceNode; }
        public int getX() { return x; }
        public int getY() { return y; }
    }
}
