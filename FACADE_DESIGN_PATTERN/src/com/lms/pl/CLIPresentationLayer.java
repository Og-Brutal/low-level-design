package com.lms.pl;
import com.lms.bll.IBusinessLayer;

public class CLIPresentationLayer implements UI {
    private IBusinessLayer serviceLayer;

    public CLIPresentationLayer(IBusinessLayer serviceLayer) {
        this.serviceLayer = serviceLayer;
    }

    public void launch() {
    }
}
