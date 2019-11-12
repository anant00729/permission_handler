package com.binarynumbers.gokozo;

public interface OnContentShare {
    void onShare(String shareContent);
    void onExit();
    void onShareLink(String link);
}
