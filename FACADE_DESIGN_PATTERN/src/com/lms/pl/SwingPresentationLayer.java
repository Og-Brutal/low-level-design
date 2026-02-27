package com.lms.pl;
import com.lms.bll.IBusinessLayer;

public class SwingPresentationLayer implements UI {
    private IBusinessLayer serviceLayer;

    public SwingPresentationLayer(IBusinessLayer serviceLayer) {
        this.serviceLayer = serviceLayer;
    }

    public void launch() {
    }
}
