package net.monoamin.rivergen.terrain;

import java.util.HashMap;
import java.util.Map;

public class ContextLayerManager {
    Map<ContextLayer.Types, ContextLayer> contextLayers;

    public ContextLayerManager(){
        contextLayers = new HashMap<>();
    }

    public void addLayer(ContextLayer.Types type, ContextLayer layer){
        contextLayers.put(type, layer);
    }

    public ContextLayer getLayer(ContextLayer.Types type){
        return contextLayers.get(type);
    }
}
