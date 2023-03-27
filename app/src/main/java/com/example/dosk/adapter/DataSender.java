package com.example.dosk.adapter;

import com.example.dosk.NewPost;

import java.util.List;

public interface DataSender {
    public void onDataReceived(List<NewPost> listData);
}
