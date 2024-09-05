package net.monoamin.rivergen.terrain;

public class ContextLayer {
    public enum Types {
        ELEVATION,
        CONNECTION_GRAPH
    }

    Object layerObject;

    public ContextLayer(Object layerObject){
        this.layerObject = layerObject;
    }
}
