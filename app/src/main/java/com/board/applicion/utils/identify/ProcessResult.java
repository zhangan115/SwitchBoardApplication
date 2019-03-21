package com.board.applicion.utils.identify;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * Created by xty on 19-2-16.
 */

public class ProcessResult  {


    Context context;
    public ProcessResult(Context context){
        this.context = context;
    }
    public void getGetAssets(){
        context.getAssets();
    }


    private static final int TF_OD_API_INPUT_SIZE = 300;
    private static final String TF_OD_API_MODEL_FILE =
            "file:///android_asset/frozen_inference_graph.pb";
    private static final String TF_OD_API_LABELS_FILE = "file:///android_asset/label_map.pbtxt";
    private static final String TAG  = "ProcessResult";
    private enum DetectorMode {
        TF_OD_API;
    }
    private static final DetectorMode MODE = DetectorMode.TF_OD_API;

    private static final float MINIMUM_CONFIDENCE_TF_OD_API = 0.4f;

    private Classifier detector;

    static {
        //加载libtensorflow_inference.so库文件
        System.loadLibrary("tensorflow_inference");
        Log.e(TAG,"libtensorflow_inference.so库加载成功");
    }

    //
    public String LoadModel(String imagepath) {
        Log.w(TAG,"LoadModel create start ..");

        try {
            Log.w(TAG,"Detector inlinte start ..");
            detector = TensorFlowObjectDetectionAPIModel.create(
                    context.getAssets(), TF_OD_API_MODEL_FILE, TF_OD_API_LABELS_FILE, TF_OD_API_INPUT_SIZE);
            Log.w(TAG,"detector create success ..");
            //cropSize = TF_OD_API_INPUT_SIZE;
            final long startTime = SystemClock.uptimeMillis();
            Log.w(TAG,"startTime done ..");
            //FileInputStream fis = new FileInputStream("/storage/emulated/0/360/1.jpg");
            FileInputStream fis = new FileInputStream(imagepath);
            Log.w(TAG,"read 1.jpg from Phone success ..");
            Bitmap bitmap = BitmapFactory.decodeStream(fis);
            Log.w(TAG,"size mbitmap"+getBitmapSize(bitmap));
            //imageView.setImageBitmap(bitmap);
            Log.w(TAG,"bitmp creat ..");
            //Bitmap mbitmap = scaleBitmap(bitmap, 0.2f);//缩小5倍
            //Log.w(TAG,"size mbitmap"+getBitmapSize(mbitmap));
            //Log.w(TAG,"image read and resize success  ..");

            Bitmap mbitmap = getScaleBitmap(bitmap,TF_OD_API_INPUT_SIZE);
            Log.w(TAG,"mbitmp creat resize 300x300 ..");
            final List<Classifier.Recognition> results = detector.recognizeImage(mbitmap);
            Log.w(TAG,"results Classfer对象 ："+results.size());
            Log.w(TAG,"API run for results success ..");
            final long lastProcessingTimeMs = SystemClock.uptimeMillis() - startTime;
            Log.w(TAG,"take time .. "+lastProcessingTimeMs+"ms ..");

            //float minimumConfidence = MINIMUM_CONFIDENCE_TF_OD_API;
            //return ""+results.get(30).getLocation();
            return sortResult(results);



        } catch (final IOException e) {
            Log.w(TAG,"Exception happend ..");
            return "ASDF";

        }

    }

    //bitmap size
    public static int getBitmapSize(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {  //SInce API 19
            return bitmap.getAllocationByteCount();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) { //Since API 12
            return bitmap.getByteCount();
        }

        return bitmap.getRowBytes() * bitmap.getHeight();
    }

    //List<string>  to string
    public static String listToString(List<String> list){

        if(list==null){
            return null;
        }

        StringBuilder result = new StringBuilder();
        boolean first = true;

        //第一个前面不拼接","
        for(String string :list) {
            if(first) {
                first=false;
            }else{
                result.append(",");
            }
            result.append(string);
        }
        return result.toString();
    }
    // 缩放到300x300
    private static Bitmap getScaleBitmap(Bitmap bitmap, int size) throws IOException {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float scaleWidth = ((float) size) / width;
        float scaleHeight = ((float) size) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
    }
    //ArrayList<Float> to String
    private static String FloatToString(List<Float> ls){
        String st = "";
        if(ls.size()==0){
            return "null";
        }

        for(int i=0;i<ls.size();i++){
            Float d  = ls.get(i);
            String b = ""+d;
            String c = b.substring(0,5);
            //st+=ls.get(i);
            st+=c;
            st+="||";
        }
        return st+"*";
    }

    private static String sortResult(List<Classifier.Recognition> results){
        class T{
            String id;
            Float confidence;
            Float x;
            Float y;
            public T(String id, Float confidence, Float x, Float y){
                this.id = id;
                this.confidence = confidence;
                this.x = x;
                this.y = y;
            }
            public String getId(){
                return id;
            }
            public  float getX(){
                return x;
            }
            public float getY(){
                return y;
            }
        }
        Log.w(TAG,"SortResult start ... amount of results ： "+results.size());
        List<T> copyResult = new ArrayList<>();

        //List<Classifier.Recognition> mresults = results; centerX
        for (Classifier.Recognition result : results){
            copyResult.add(new T(result.getTitle(),result.getConfidence(),
                                 result.getLocation().left,result.getLocation().top));
        }
        Log.w(TAG,"copyReslut [0] left and top : x  "+copyResult.get(0).x+" y "+copyResult.get(0).y);
        Log.w(TAG,"SortResult copyResult done ...");
        Log.w(TAG,"copyResult amount is "+copyResult.size());
        //sort by X Y /maopao sort
        //only sort result confidence > 0.2

        List<T> conResults = new ArrayList<>();
        for (T result : copyResult){
            if(result.confidence>=MINIMUM_CONFIDENCE_TF_OD_API){
                conResults.add(result);
            };
        }
        Log.w(TAG,"confidence > 0.2f results : coResults is "+conResults.size());


        int h = conResults.size()/9;
        Log.w(TAG,"n ： "+h+"  conresults.size(): "+conResults.size());
        //sort by  Y
        Collections.sort(conResults, new Comparator<T>() {

            @Override
            public int compare(T a, T b) {
                float diff = a.getY()-b.getY();
                if (diff > 0) {
                    return 1;
                }else if (diff < 0) {
                    return -1;
                }
                return 0;
            }
        });
        //sort by x (hang nei)
        if(conResults.size()%9==0) {
            for (int i = 0; i < conResults.size(); i += 9) {
                List<T> part = new ArrayList<>();
                for (int j = i; j < i + 9; j++) {
                    part.add(conResults.get(j));
                }
                Collections.sort(part, new Comparator<T>() {
                    @Override
                    public int compare(T a, T b) {
                        float diff = a.getX() - b.getX();
                        if (diff > 0) {
                            return 1;
                        } else if (diff < 0) {
                            return -1;
                        }
                        return 0; //相等为0

                    }
                });

                for (int k = 0; k < part.size(); k++) {
                    // results.set(k+i,part.get(k));
                    conResults.set(k + i, part.get(k));

                }
            }
        }

        Log.w(TAG,"SortResult copyResult sort by x,y done ...");
        //get id(title)
        String st = ""+9;
        st = h+st;
       Log.w(TAG,st);
        for(int i=0;i<conResults.size();i++){

            st+=conResults.get(i).id;


        }
        Log.w(TAG,st);
        //01 format
        String ss = st.replaceAll(" ", "");


        Log.w(TAG,"SortResult success for String ...");
//
//        Log.w(TAG,"x0: "+conResults.get(0).x+"y0: "+conResults.get(0).y+"id "+conResults.get(0).id);
//        Log.w(TAG,"x1: "+conResults.get(1).x+"y1: "+conResults.get(1).y+"id "+conResults.get(1).id);
//        Log.w(TAG,"x2: "+conResults.get(2).x+"y2: "+conResults.get(2).y+"id "+conResults.get(2).id);
//        Log.w(TAG,"x3: "+conResults.get(3).x+"y3: "+conResults.get(3).y);
//        Log.w(TAG,"x4: "+conResults.get(4).x+"y4: "+conResults.get(4).y);
//        Log.w(TAG,"x5: "+conResults.get(5).x+"y5: "+conResults.get(5).y);
//        Log.w(TAG,"x6: "+conResults.get(6).x+"y6: "+conResults.get(6).y);
//        Log.w(TAG,"x7: "+conResults.get(7).x+"y7: "+conResults.get(7).y);
//        Log.w(TAG,"x8: "+conResults.get(8).x+"y8: "+conResults.get(8).y);
//        Log.w(TAG,"x9: "+conResults.get(9).x+"y9: "+conResults.get(9).y);
//        Log.w(TAG,"x10: "+conResults.get(10).x+"y10: "+conResults.get(10).y);
//        Log.w(TAG,"x11: "+conResults.get(11).x+"y11: "+conResults.get(11).y);
//        Log.w(TAG,"x12: "+conResults.get(12).x+"y12: "+conResults.get(12).y);
//        Log.w(TAG,"x13: "+conResults.get(13).x+"y13: "+conResults.get(13).y);
//        Log.w(TAG,"x14: "+conResults.get(14).x+"y14: "+conResults.get(14).y);
//        Log.w(TAG,"x15: "+conResults.get(15).x+"y15: "+conResults.get(15).y);
//        Log.w(TAG,"x16: "+conResults.get(16).x+"y16: "+conResults.get(16).y);
//        Log.w(TAG,"x17: "+conResults.get(17).x+"y17: "+conResults.get(17).y);
//        Log.w(TAG,"x18: "+conResults.get(18).x+"y18: "+conResults.get(18).y);
//        Log.w(TAG,"x19: "+conResults.get(19).x+"y19: "+conResults.get(19).y);
//        Log.w(TAG,"x20: "+conResults.get(20).x+"y20: "+conResults.get(20).y);
//        Log.w(TAG,"x21: "+conResults.get(21).x+"y21: "+conResults.get(21).y);
//        Log.w(TAG,"x22: "+conResults.get(22).x+"y22: "+conResults.get(22).y);
//        Log.w(TAG,"x23: "+conResults.get(23).x+"y23: "+conResults.get(23).y);
//        Log.w(TAG,"x24: "+conResults.get(24).x+"y24: "+conResults.get(24).y);
//        Log.w(TAG,"x25: "+conResults.get(25).x+"y25: "+conResults.get(25).y);
//        Log.w(TAG,"x26: "+conResults.get(26).x+"y26: "+conResults.get(26).y);
//        Log.w(TAG,"x27: "+conResults.get(27).x+"y27: "+conResults.get(27).y);
//        Log.w(TAG,"x28: "+conResults.get(28).x+"y28: "+conResults.get(28).y);
//        Log.w(TAG,"x29: "+conResults.get(29).x+"y29: "+conResults.get(29).y);
//        Log.w(TAG,"x30: "+conResults.get(30).x+"y30: "+conResults.get(30).y);
//        Log.w(TAG,"x31: "+conResults.get(31).x+"y31: "+conResults.get(31).y);
//        Log.w(TAG,"x32: "+conResults.get(32).x+"y32: "+conResults.get(32).y);
        //Log.w(TAG,"x33: "+conResults.get(33).x+"y33: "+conResults.get(33).y);
       // Log.w(TAG,"x34: "+conResults.get(34).x+"y34: "+conResults.get(34).y);
       // Log.w(TAG,"x35: "+conResults.get(35).x+"y35: "+conResults.get(35).y);




        return ss;
        //return st+conResults.size();
    }

    private static void swap(List<?> list, int i, int j){
        final List l=list;
        l.set(i, l.set(j, l.get(i)));
    }

}
