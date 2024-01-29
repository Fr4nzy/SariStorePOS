package com.projectfkklp.saristorepos.utils;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;

import com.projectfkklp.saristorepos.interfaces.PdfCustomWrite;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public abstract class PdfWriter {
    private File file;
    private String filename;
    private PdfDocument document;
    private PdfDocument.PageInfo pageInfo;
    PdfDocument.Page page;
    Canvas canvas;
    Paint paint;

    private int pageWidth = 1080;
    private int pageHeight = 1920;
    private int margin = 70;
    private int x = margin;
    private int y = margin + 10;
    private boolean isBold = false;

    public PdfWriter(String filename){
        this.filename = filename;

        // Fundamentals
        document = new PdfDocument();
        pageInfo = new PdfDocument
                .PageInfo
                .Builder(pageWidth, pageHeight, 1)
                .create();
        page = document.startPage(pageInfo);

        // Canvas
        canvas = page.getCanvas();
        paint = new Paint();
    }

    public void start() throws IOException {
        write();

        save();
    }

    protected abstract void write();

    public void save() throws IOException {
        // Save to downloads
        document.finishPage(page);
        File downloadDirs = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        file = new File(downloadDirs, filename);

        FileOutputStream fos = new FileOutputStream(file);
        document.writeTo(fos);
    }

    public void setTextSize(float textSize){
        paint.setTextSize(textSize);
    }

    public void setIsBold(boolean isBold){
        paint.setFakeBoldText(isBold);
        this.isBold=isBold;
    }

    public void setColor(int color){
        paint.setColor(color);
    }

    public void writeText(String text){
        canvas.drawText(text, x, y, paint);
        y+=paint.getTextSize() + (isBold? 5:0);
    }

    public void writeLine(int height){
        canvas.drawLine(x, y-20, pageWidth - margin, y-20, paint);
        y += height;
    }

    public void writeCustom(PdfCustomWrite writer){
        writer.write(this);
    }

    public Canvas getCanvas(){
        return canvas;
    }

    public Paint getPaint(){
        return paint;
    }

    public int getPageWidth(){
        return pageWidth;
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }

    public boolean isBold(){
        return isBold;
    }

    public void setY(int y){
        this.y=y;
    }
}
