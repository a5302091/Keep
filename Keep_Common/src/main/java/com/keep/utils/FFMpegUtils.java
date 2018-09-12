package com.keep.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FFMpegUtils {

    private String ffmpegExE;

    public FFMpegUtils(String ffmpegExE) {
        super();
        this.ffmpegExE = ffmpegExE;
    }

    public void convertor(String videoInputPath, String videoOutputPath) throws Exception {
        List<String> list = new ArrayList<String>();
        list.add(ffmpegExE);

        list.add("-i");
        list.add(videoInputPath);
        list.add(videoOutputPath);

        for (String l : list) {
            System.out.println(l);
        }

        ProcessBuilder processBuilder = new ProcessBuilder(list);
        Process process = processBuilder.start();
        InputStream errorStream = process.getErrorStream();
        InputStreamReader inputStreamReader = new InputStreamReader(errorStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        String line = "";
        while ((line = bufferedReader.readLine()) != null) {

        }
        if (bufferedReader != null) {
            bufferedReader.close();
        }
        if (inputStreamReader != null) {
            inputStreamReader.close();
        }
        if (errorStream != null) {
            errorStream.close();
        }
    }


    public void convertorMusic(String videoInputPath, String Mp3Input, double seconds, String videoOutputPath) throws Exception {
        List<String> list = new ArrayList<>();
        list.add(ffmpegExE);

        list.add("-i");
        list.add(videoInputPath);

        list.add("-i");
        list.add(Mp3Input);

        list.add("-t");
        list.add(String.valueOf(seconds));

        list.add("-y");
        list.add(videoOutputPath);

        for (String l : list) {
            System.out.print(l + " ");
        }

        ProcessBuilder processBuilder = new ProcessBuilder(list);
        Process process = processBuilder.start();
        InputStream errorStream = process.getErrorStream();
        InputStreamReader inputStreamReader = new InputStreamReader(errorStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        if (bufferedReader != null) {
            bufferedReader.close();
        }
        if (inputStreamReader != null) {
            inputStreamReader.close();
        }
        if (errorStream != null) {
            errorStream.close();
        }
    }


    public static void main(String[] args) {
        FFMpegUtils ffMpegUtils = new FFMpegUtils("E:\\ffmpeg\\bin\\ffmpeg.exe");
        try {
            ffMpegUtils.
        convertorMusic("D:\\qq.mp4", "D:\\music.mp3", 4, "D:\\哈哈.mp4");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
