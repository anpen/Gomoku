package com.example.frank.gomoku.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.example.frank.gomoku.R;
import com.example.frank.gomoku.model.Chessman;
import com.example.frank.gomoku.util.MySoundPlayer;

import java.util.ArrayList;

/**
 * Created by Frank on 2016/1/23.
 */
public class ChessBoard extends View {

    private Paint mBoardPaint;
    private Bitmap mBoardBitmap;
    private Bitmap mWhiteBitmap;
    private Bitmap mScaledWhiteBitmap;
    private Bitmap mBlackBitmap;
    private Bitmap mScaledBlackBitmap;
    private Canvas mBoardCanvas;
    private Paint mChessmanPaint;

    private int num = 15;
    private int sideLen;
    private int viewWidth, viewHeight;
    private int currentX = -1, currentY = -1;

    private int[][] chessmans;
    private ArrayList<Chessman> chessmanHistory;

    public final  static int EMPTY = 0;
    public final  static int BLACK = 1;
    public final  static int WHITE = 2;

    private boolean isStart = true;
    private boolean isSound = false;

    private OnChessmanDroppedListener mListener;

    private static final String TAG  = ChessBoard.class.getSimpleName();

    public ChessBoard(Context context) {
        super(context);
        init();
    }

    public ChessBoard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public interface OnChessmanDroppedListener {
        void onChessmanDropped(Chessman cm);
    }

    public void setOnChessmanDroppedListener(OnChessmanDroppedListener listener) {
        this.mListener = listener;
    }

    private void init() {
        mBoardPaint = new Paint();
        mChessmanPaint = new Paint();
        mBoardPaint.setColor(Color.BLACK);
        mChessmanPaint.setColor(Color.WHITE);

        mWhiteBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.white_stone);
        mBlackBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.black_stone);

        chessmans = new int[num + 1][num + 1];
        chessmanHistory = new ArrayList<>();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        viewWidth = w;
        viewHeight = h;
        sideLen = Math.min(w / (num + 2), h / (num + 2));
        mBoardBitmap = Bitmap.createBitmap((int) viewWidth, (int) viewHeight, Bitmap.Config.ARGB_8888);
        mBoardCanvas = new Canvas(mBoardBitmap);
        mScaledWhiteBitmap = Bitmap.createScaledBitmap(mWhiteBitmap,sideLen,sideLen,true);
        mScaledBlackBitmap = Bitmap.createScaledBitmap(mBlackBitmap,sideLen,sideLen,true);
        drawBoard();
        Log.i(TAG, String.format("%s: width : %d, height : %d, sideLen : %d", "onSizeChanged", w, h, sideLen));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int type = event.getAction();
        switch (type) {
            case MotionEvent.ACTION_DOWN:
                float tmpX = event.getX();
                float tmpY = event.getY();
                int totalLen = sideLen * num;
                currentX = (int) ((tmpX - (viewWidth - totalLen) / 2) / sideLen + 0.5);
                currentY = (int) ((tmpY - (viewHeight - totalLen) / 2) / sideLen + 0.5);
                verify();
                Log.i(TAG, String.format("%s: x: %f, y: %f", "click", tmpX, tmpY));
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.i(TAG, "onDraw");
        drawBoard();
        drawChesses();
        canvas.drawBitmap(mBoardBitmap, 0, 0, mBoardPaint);
        if (isInBound(currentX,currentX) && isFive(currentX, currentY)) {
            if (chessmans[currentX][currentY] == BLACK) {
                Toast.makeText(getContext(),"black wins out",Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(),"white wins out",Toast.LENGTH_SHORT).show();
            }
            isStart = false;
        }
    }

    private void playSound() {
        if (isSound)
            MySoundPlayer.playSound(getContext(),MySoundPlayer.S1);
    }

    // get the count of the chessmans
    public int getCount() {
        return chessmanHistory.size();
    }

    private void verify() {
        if (isStart && isCanDrop(currentX, currentY)) {
            Chessman cm = new Chessman(currentX, currentY);
            mListener.onChessmanDropped(cm);
        }
    }

    public void drop(Chessman cm) {
        chessmanHistory.add(cm);
        currentX = cm.getX();
        currentY = cm.getY();
        chessmans[currentX][currentY] = cm.getColor();
        playSound();
        invalidate();
    }

    public void back() {
        Chessman cm = null;
        if (chessmanHistory.size() > 0) {
             cm = chessmanHistory.remove(chessmanHistory.size() - 1);
        }
        if (cm != null) {
            chessmans[cm.getX()][cm.getY()] = EMPTY;
        }
        isStart = true;
        invalidate();
    }

    public void clear() {
        for (Chessman cm : chessmanHistory) {
            chessmans[cm.getX()][cm.getY()] = EMPTY;
        }
        chessmanHistory.clear();
        isStart = true;
        invalidate();
    }

    public void setSound(boolean enable) {
        isSound = enable;
    }

    public void setStart(boolean isStart) {
        this.isStart = isStart;
    }

    private void drawChesses() {
        Bitmap mScaledBitmap = null;
        for (Chessman cm : chessmanHistory) {
            if (cm.getColor() == WHITE) {
//                mChessmanPaint.setColor(Color.WHITE);
                mScaledBitmap = mScaledWhiteBitmap;
            } else {
//                mChessmanPaint.setColor(Color.RED);
                mScaledBitmap = mScaledBlackBitmap;
            }
            mBoardCanvas.drawBitmap(mScaledBitmap, (viewWidth - sideLen * num) / 2 + cm.getX() * sideLen - sideLen / 2, (viewHeight - sideLen * num) / 2 + cm.getY() * sideLen - sideLen / 2, mChessmanPaint);
//            mBoardCanvas.drawCircle((viewWidth - sideLen * num) / 2 + cm.getX() * sideLen, (viewHeight - sideLen * num) / 2 + cm.getY() * sideLen, sideLen / 2, mChessmanPaint);
        }
    }

    private void drawBoard() {
        mBoardCanvas.drawColor(getResources().getColor(R.color.board));
        float totalLen = sideLen * num;
        // draw horizental lines
        for (int i = 0; i < num + 1; i++) {
            mBoardCanvas.drawLine((viewWidth - totalLen) / 2,i * sideLen + (viewHeight - totalLen) / 2,(viewWidth + totalLen) / 2,i * sideLen + (viewHeight - totalLen) / 2, mBoardPaint);
        }
        // draw vertical lines
        for (int i = 0; i < num + 1; i++) {
            mBoardCanvas.drawLine(i * sideLen + (viewWidth - totalLen) / 2,(viewHeight - totalLen) / 2,i * sideLen + (viewWidth - totalLen) / 2,(viewHeight + totalLen) / 2, mBoardPaint);
        }
    }

    private boolean isInBound(int x, int y) {
        if (x < 0 || x > num || y < 0 || y > num)
            return false;
        return true;
    }

    private boolean isEmpty(int x, int y) {
        return chessmans[x][y] == EMPTY;
    }

    // Whether you can drop the chessman here.
    private boolean isCanDrop(int x, int y) {
        return isInBound(x,y) && isEmpty(x,y);
    }

    // main logic here
    private boolean isFive(int x, int y) {
        if (isEmpty(x,y))
            return false;
        return judgeHor(x,y) || judgeVer(x,y) || judgeDiag(x,y) || judgeAntiDiag(x,y);
    }

    private boolean judgeHor(int x, int y) {
        int preHorNum = 0,postHorNum = 0;
        for (int i = x - 1 ; i >= 0; i--) {
            if (chessmans[x][y] == chessmans[i][y]) {
                ++preHorNum;
                if (preHorNum == 4) {
                    Log.i(TAG,preHorNum + "");
                    return true;
                }

            } else {
                break;
            }
        }
        for (int i = x + 1; i <= num; i++) {
            if (chessmans[x][y] == chessmans[i][y]) {
                ++postHorNum;
                if (preHorNum + postHorNum == 4)
                    return true;
            } else {
                break;
            }
        }
        return false;
    }

    private boolean judgeVer(int x,int y) {
        int preVerNum = 0,postVerNum = 0;
        for (int j = y - 1 ; j >= 0; j--) {
            if (chessmans[x][y] == chessmans[x][j]) {
                ++preVerNum;
                if (preVerNum == 4)
                    return true;
            } else {
                break; // out or the current loop
            }
        }
        for (int j = y + 1; j <= num; j++) {
            if (chessmans[x][y] == chessmans[x][j]) {
                ++postVerNum;
                if (preVerNum + postVerNum == 4)
                    return true;
            } else {
                break;
            }
        }
        return false;
    }

    private boolean judgeDiag(int x, int y) {
        int preDiagNum = 0,postDiagNum = 0;
        for (int i = x - 1, j = y - 1; i >= 0 && j >= 0; i--, j--) {
            if (chessmans[x][y] == chessmans[i][j]) {
                preDiagNum++;
                if (preDiagNum == 4)
                    return true;
            } else {
                break;
            }
        }
        for (int i = x + 1, j = y + 1; i <= num && j <= num; i++, j++) {
            if (chessmans[x][y] == chessmans[i][j]) {
                postDiagNum++;
                if (preDiagNum + postDiagNum == 4)
                    return true;
            } else {
                break;
            }
        }
        return false;
    }

    private boolean judgeAntiDiag(int x, int y) {
        int preAntiDiag = 0,postAntiDiag = 0;
        for (int i = x - 1, j = y + 1; i >= 0 && j <= num; i--, j++) {
            if (chessmans[x][y] == chessmans[i][j]) {
                preAntiDiag++;
                if (preAntiDiag == 4)
                    return true;
            } else {
                break;
            }
        }
        for (int i = x + 1, j = y - 1; i <= num && j >= 0; i++, j--) {
            if (chessmans[x][y] == chessmans[i][j]) {
                postAntiDiag++;
                if (preAntiDiag + postAntiDiag == 4)
                    return true;
            } else {
                break;
            }
        }
        return  false;
    }
}
