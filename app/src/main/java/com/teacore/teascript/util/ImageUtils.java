package com.teacore.teascript.util;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.DisplayMetrics;

import com.teacore.teascript.app.AppContext;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * 图片操作工具包
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-12
 */

public class ImageUtils {

    //Environment.getExternalStorageDirectory()获得
    public static final String MNT_SDCARD="/mnt/sdcard";
    public static final String SDCARD="/sdcard";

    //请求相册
    public static final int REQUEST_CODE_GETIMAGE_BYSDCARD=0;
    //请求相机
    public static final int REQUEST_CODE_GETIMAGE_BYCAMERA=1;
    //请求裁剪
    public static final int REQUEST_CODE_GETIMAGE_BYCROP=2;
    //从图片浏览界面发送动弹
    public static final int REQUEST_CODE_GETIMAGE_IMAGEPAGER=3;

    //写图片文件，保存在Android内部存储空间，/data/data/PACKAGE_NAME/files目录下
    public static void saveImage(Context context, String fileName, Bitmap bitmap)
            throws IOException {
        saveImage(context, fileName, bitmap, 100);
    }

    public static void saveImage(Context context, String fileName,
                                 Bitmap bitmap, int quality) throws IOException {
        if (bitmap == null || fileName == null || context == null)
            return;
        FileOutputStream fos = context.openFileOutput(fileName,
                Context.MODE_PRIVATE);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.JPEG, quality, stream);
        byte[] bytes = stream.toByteArray();
        fos.write(bytes);
        fos.close();
    }

    //写图片到外部的SD卡中
    public static void saveImageToSD(Context context, String filePath,
                                     Bitmap bitmap, int quality) throws IOException {
        if (bitmap != null) {
            File file = new File(filePath.substring(0,
                    filePath.lastIndexOf(File.separator)));
            if (!file.exists()) {
                file.mkdirs();
            }
            BufferedOutputStream bos = new BufferedOutputStream(
                    new FileOutputStream(filePath));
            bitmap.compress(CompressFormat.JPEG, quality, bos);
            bos.flush();
            bos.close();
            if (context != null) {
                scanPhoto(context, filePath);
            }
        }
    }

    //保存背景图片
    public static void saveBackgroundImage(Context context, String filePath,
                                           Bitmap bitmap, int quality) throws IOException {
        if (bitmap != null) {
            File file = new File(filePath.substring(0,
                    filePath.lastIndexOf(File.separator)));
            if (!file.exists()) {
                file.mkdirs();
            }
            BufferedOutputStream bos = new BufferedOutputStream(
                    new FileOutputStream(filePath));
            bitmap.compress(CompressFormat.PNG, quality, bos);
            bos.flush();
            bos.close();
            if (context != null) {
                scanPhoto(context, filePath);
            }
        }
    }

    //主动通知系统的图库更新图片
    private static void scanPhoto(Context context, String imgFileName) {
        Intent mediaScanIntent = new Intent(
                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file = new File(imgFileName);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }

    //获取一个Bitmap文件
    public static Bitmap getBitmap(Context context,String fileName){
        FileInputStream fis=null;
        Bitmap bitmap=null;
        try{
            fis=context.openFileInput(fileName);
            bitmap= BitmapFactory.decodeStream(fis);
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }catch(OutOfMemoryError e){
            e.printStackTrace();
        }finally {
            try{
                fis.close();
            }catch(Exception e){
            }
        }
        return bitmap;
    }

    //文件路径获取图片
    public static Bitmap getBitmapByPath(String filePath){
        return getBitmapByPath(filePath,null);
    }

    public static Bitmap getBitmapByPath(String filePath,BitmapFactory.Options opts){
        FileInputStream fis=null;
        Bitmap bitmap=null;
        try{
            File file=new File(filePath);
            fis=new FileInputStream(file);
            bitmap=BitmapFactory.decodeStream(fis,null,opts);
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } finally {
            try {
                fis.close();
            } catch (Exception e) {
            }
        }
        return bitmap;
    }

    //通过文件对象获取bitmap
    public static Bitmap getBitmapByFile(File file){
        FileInputStream fis=null;
        Bitmap bitmap=null;
        try {
            fis = new FileInputStream(file);
            bitmap = BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } finally {
            try {
                fis.close();
            } catch (Exception e) {
            }
        }
        return bitmap;
    }

    //使用当前时间戳拼接一个唯一的文件名
    public static String getTempFileName(){
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss_SS");
        String fileName = dateFormat.format(new Timestamp(System.currentTimeMillis()));
        return fileName;
    }

    //获取照相机使用的目录
    public static String getCameraPath(){
        return Environment.getExternalStorageDirectory()+File.separator+"FounderNews"+File.separator;
    }

    //判断当前Uri是否是标准的content://形式，如果不是，则返回绝对路径
    public static String getAbsolutePathFromNotStandardUri(Uri uri){
        String filePath=null;
        String mUriString=uri.toString();

        String pre1="file://"+SDCARD+File.separator;
        String pre2="file://"+MNT_SDCARD+File.separator;
        if (mUriString.startsWith(pre1)) {
            filePath = Environment.getExternalStorageDirectory().getPath()
                    + File.separator + mUriString.substring(pre1.length());
        } else if (mUriString.startsWith(pre2)) {
            filePath = Environment.getExternalStorageDirectory().getPath()
                    + File.separator + mUriString.substring(pre2.length());
        }
        return filePath;
    }

    //通过uri获取图片的绝对路径
    public static String getAbsoluteImagePath(Activity context, Uri uri) {
        String imagePath = "";
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
        if (cursor != null) {
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                imagePath = cursor.getString(column_index);
            }
        }

        return imagePath;
    }

    //获取图片的缩略图
    public static Bitmap loadImgThumbnail(Activity context,String imgName,int kind){
        Bitmap bitmap=null;
        String[] proj={MediaStore.Images.Media._ID,MediaStore.Images.Media.DISPLAY_NAME};
        Cursor cursor=context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, proj,
                MediaStore.Images.Media.DISPLAY_NAME + "='" + imgName + "'",
                null, null);
        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
            ContentResolver crThumb = context.getContentResolver();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 1;
            bitmap = MethodsCompat.getThumbnail(crThumb, cursor.getInt(0),
                    kind, options);
        }
        return bitmap;
    }

    public static Bitmap loadImgThumbnail(String filePath, int w, int h) {
        Bitmap bitmap = getBitmapByPath(filePath);
        return zoomBitmap(bitmap, w, h);
    }

    //获取SD卡中最新的图片
    public static String getLatestImage(Activity context){
        String latestImage=null;
        String[] items = {MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, items, null,
                null, MediaStore.Images.Media._ID + " desc");

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
                    .moveToNext()) {
                latestImage = cursor.getString(1);
                break;
            }
        }
        return latestImage;
    }

    //计算缩放图片的宽高
    public static int[] scaleImageSize(int[] img_size, int square_size) {
        if (img_size[0] <= square_size && img_size[1] <= square_size)
            return img_size;
        double ratio = square_size
                / (double) Math.max(img_size[0], img_size[1]);
        return new int[]{(int) (img_size[0] * ratio),
                (int) (img_size[1] * ratio)};
    }

    /*
    *创建缩略图
    * @param largeImagePath 原始大图路径
    * @param thumbfilePath  输出缩略图路径
    * @param square_size    输出图片的宽度
    * @param quality        输出图片质量
     */
    public static void createImageThumbnail(Context context,String largeImagePath,String thumbfilePath,
                                            int square_size,int quality) throws IOException{
        BitmapFactory.Options opts=new BitmapFactory.Options();
        opts.inSampleSize=1;

        //原始图片Bitmap
        Bitmap cur_img=getBitmapByPath(largeImagePath,opts);
        if(cur_img==null)
            return;
        //原始图片的宽高
        int[] cur_img_size=new int[]{cur_img.getWidth(),cur_img.getHeight()};
        //计算原始图片缩放后的宽高
        int[] new_img_size=scaleImageSize(cur_img_size,square_size);
        //生成缩放后的Bitmap
        Bitmap new_img=zoomBitmap(cur_img,new_img_size[0],new_img_size[1]);
        //生成缩放后的图片文件
        saveImageToSD(null,thumbfilePath,new_img,quality);
    }

    //生成缩放文件
    public static Bitmap zoomBitmap(Bitmap bitmap,int w,int h){
        Bitmap newBitmap=null;
        if(bitmap!=null){
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            Matrix matrix = new Matrix();
            float scaleWidht = ((float) w / width);
            float scaleHeight = ((float) h / height);
            matrix.postScale(scaleWidht, scaleHeight);
            newBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix,
                    true);
        }
        return newBitmap;
    }

    //生成200*200的Bitmap
    public static Bitmap scaleBitmap(Bitmap bitmap){
        //获取图片的宽和高
        int width=bitmap.getWidth();
        int height=bitmap.getHeight();
        //定义预转换成的图片宽度和高度
        int newWidth=200;
        int newHeight=200;
        //计算缩放率
        float scaleWidth=((float) newWidth)/width;
        float scaleHeight=((float) newHeight)/height;
        //创建一个matrix对象
        Matrix matrix=new Matrix();
        //缩放图片操作
        matrix.postScale(scaleWidth,scaleHeight);
        //创建新的图片
        Bitmap newBitmap=Bitmap.createBitmap(bitmap,0,0,width,height,matrix,true);

        return newBitmap;

    }

    //重绘图片(图片的宽度大于屏幕的有效宽度)
    public static Bitmap redrawBitmap(Activity context,Bitmap bitmap){
        DisplayMetrics dm=new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(dm);

        //获取屏幕的有效尺寸
        int sWidth=dm.widthPixels;
        int sHeight=dm.heightPixels;
        //获取图片的尺寸
        int width=bitmap.getWidth();
        int height=bitmap.getHeight();
        float zoomScale;

        if (width >= sWidth)
            zoomScale = ((float) sWidth) / width;
        else
            zoomScale = 1.0f;
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 缩放图片动作
        matrix.postScale(zoomScale, zoomScale);
        Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return newBitmap;
    }

    //将Drawable转化为Bitmap
    public static Bitmap drawableToBitmap(Drawable drawable) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, drawable
                .getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;
    }

    //获取圆角图片的方法
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap,float roundPx){
        //获取输出的bitmap
        Bitmap newBitmap=Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas=new Canvas(newBitmap);

        final int color=0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return newBitmap;
    }

    //获取带倒影图片
    public static Bitmap createReflectionImage(Bitmap bitmap){
        final int reflectionGap = 4;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);

        Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, height / 2,
                width, height / 2, matrix, false);

        Bitmap bitmapWithReflection = Bitmap.createBitmap(width,
                (height + height / 2), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmapWithReflection);
        canvas.drawBitmap(bitmap, 0, 0, null);
        Paint deafalutPaint = new Paint();
        canvas.drawRect(0, height, width, height + reflectionGap, deafalutPaint);

        canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

        Paint paint = new Paint();
        LinearGradient shader = new LinearGradient(0, bitmap.getHeight(), 0,
                bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff,
                0x00ffffff, Shader.TileMode.CLAMP);
        paint.setShader(shader);
        // Set the Transfer mode to be porter duff and destination in
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        // Draw a rectangle using the paint with our linear gradient
        canvas.drawRect(0, height, width, bitmapWithReflection.getHeight()
                + reflectionGap, paint);

        return bitmapWithReflection;
    }

    //将bitmap转换为drawable
    public static Drawable bitmapToDrawable(Bitmap bitmap) {
        Drawable drawable = new BitmapDrawable(AppContext.getInstance().getResources(),bitmap);
        return drawable;
    }

    //判断图片的类型
    public static String getImageType(byte[] bytes){
        if(isJPEG(bytes)){
            return "image/jpeg";
        }
        if(isGIF(bytes)){
            return "image/gif";
        }
        if(isPNG(bytes)){
            return "image/png";
        }
        if(isBMP(bytes)){
            return "application/x-bmp";
        }
        return null;
    }

    private static boolean isJPEG(byte[] b) {
        if (b.length < 2) {
            return false;
        }
        return (b[0] == (byte) 0xFF) && (b[1] == (byte) 0xD8);
    }

    private static boolean isGIF(byte[] b) {
        if (b.length < 6) {
            return false;
        }
        return b[0] == 'G' && b[1] == 'I' && b[2] == 'F' && b[3] == '8'
                && (b[4] == '7' || b[4] == '9') && b[5] == 'a';
    }

    private static boolean isPNG(byte[] b) {
        if (b.length < 8) {
            return false;
        }
        return (b[0] == (byte) 137 && b[1] == (byte) 80 && b[2] == (byte) 78
                && b[3] == (byte) 71 && b[4] == (byte) 13 && b[5] == (byte) 10
                && b[6] == (byte) 26 && b[7] == (byte) 10);
    }

    private static boolean isBMP(byte[] b) {
        if (b.length < 2) {
            return false;
        }
        return (b[0] == 0x42) && (b[1] == 0x4d);
    }

    public static String getImageType(InputStream inputStream){
        if (inputStream == null) {
            return null;
        }
        try {
            byte[] bytes = new byte[8];
            inputStream.read(bytes);
            return getImageType(bytes);
        } catch (IOException e) {
            return null;
        }
    }

    public static String getImageType(File file) {
        if (file == null || !file.exists()) {
            return null;
        }
        InputStream in = null;
        try {
            in = new FileInputStream(file);
            String type = getImageType(in);
            return type;
        } catch (IOException e) {
            return null;
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
            }
        }
    }

    //通过uri获取文件路径(图片媒体，文件等)
    public static String getPath(Context context,Uri uri){

        //uri在kitkat前后是不一样的形式，所以我们需要做一个判断
        String selection=null;
        String[] selectionArgs=null;
        //判断uri是不是被document封装过
        if(Build.VERSION.SDK_INT>=19&& DocumentsContract.isDocumentUri(context.getApplicationContext(),uri)){
            String authority=uri.getAuthority();
            if("com.android.externalstorage.documents".equals(authority)){
                //ExternalStorageDocument
                final String docId=DocumentsContract.getDocumentId(uri);
                final String[] split=docId.split(":");
                return Environment.getExternalStorageDirectory()+"/"+split[1];
            }else if ("com.android.providers.downloads.documents".equals(authority)) {
                // isDownloadsDocument
                final String id = DocumentsContract.getDocumentId(uri);
                uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
            } else if ("com.android.providers.media.documents".equals(authority)) {
                // isMediaDocument
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("image".equals(type)) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                selection = "_id=?";
                selectionArgs = new String[]{
                        split[1]
                };
            }
        }
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver()
                        .query(uri, projection, selection, selectionArgs, null);
                if (cursor != null) {
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    if (cursor.moveToFirst()) {
                        return cursor.getString(column_index);
                    }
                }
            } catch (Exception e) {
                e.fillInStackTrace();
            } finally {
                if (cursor != null)
                    cursor.close();
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    static Bitmap bitmap=null;
    //上传图片到picasa
    public static Bitmap loadPicasaImageFromGalley(final Activity context,final Uri uri
                                                   ) {


        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.DISPLAY_NAME};
        Cursor cursor = context.getContentResolver().query(uri, projection,
                null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();

            int columIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME);
            if (columIndex != -1) {
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            bitmap = android.provider.MediaStore.Images.Media
                                    .getBitmap(context.getContentResolver(),
                                            uri);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }).start();
            }
            cursor.close();
            return bitmap;
        } else
            return null;
    }


}





