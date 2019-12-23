package com.meiduohui.groupbuying.UI.activitys.publish.videoCompress;

/**
 */
public interface CompressListener {
    public void onExecSuccess(String message);

    public void onExecFail(String reason);

    public void onExecProgress(String message);
}
