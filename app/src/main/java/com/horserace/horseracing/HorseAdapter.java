package com.horserace.horseracing;

import android.app.Activity;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class HorseAdapter extends RecyclerView.Adapter<HorseAdapter.CustomViewHolder> {

    private OnItemClick onItemClick;
    private Handler handler = new Handler();
    private Runnable runnable;
    private ArrayList<HorseData> items = new ArrayList<HorseData>();
    private Activity activity;
    private int horseNum = 1; //말 번호
    int rank = 1; // 순위
    int total = 0; // 전체거리

    public HorseAdapter(ArrayList<HorseData> items, Activity activity, OnItemClick onItemClick) {
        this.activity = activity;
        this.items = items;
        this.onItemClick = onItemClick;
    }

    //말추가
    public void addItem(HorseData data) {
        data.setHorseNum(horseNum);
        horseNum++;
        items.add(data);
        notifyDataSetChanged();
    }

    //말 삭제
    public void deleteItem() {
        horseNum--;
        items.remove(items.size() - 1);
        notifyDataSetChanged();
    }

    public void allDeleteItem(){
        horseNum = 1;
        items.clear();
        notifyDataSetChanged();
    }

    public void setItems(ArrayList<HorseData> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    //경마 시작
    public void startHorse() {
        move();
    }

    //말 움직이기
    private void move() {
        runnable = new Runnable() {
            @Override
            public void run() {
                for (HorseData item : new ArrayList<>(items)) {
                    int distance = item.getDistance(); // 이동거리
                    boolean isStopped = item.isStopped(); //넘어진말 여부
                    int retryCnt = item.getRetryCnt(); //넘어진말 멈춤위함

                    if (retryCnt <= 0) {
                        isStopped = false;
                    }

                    if (isStopped) { // isStopped = ture
                        retryCnt -= 1;
                        item.setRetryCnt(retryCnt);
                        if( item.getRetryCnt() == 0){ //넘어진 말 1초동안 못움직이게
                            item.setStopHorseFast(true); //넘어진 말 빠르게 하기 위함
                            item.setStopped(false); // 말 넘어짐 여부 초기화
                        }
                    } else { //말 이동 여부 isStopped = false
                        int random = (int) (Math.random() * 50);
                        isStopped = random == 30;
                        int stopRandom = (int) (Math.random() * 10);
                        Boolean stopHorseNum = stopRandom == 3;
                        if (isStopped) { // 넘어진 말 isStopped = ture
                            if( stopHorseNum ){
                                item.setStopped(isStopped);
                                item.setRetryCnt(20);
                            }
                        } else { // 말 이동 isStopped = false
                            boolean fast = item.isStopHorseFast(); //넘어진말 빨리 달릴수 있는 여부

                            if(fast){ // 넘어진 말에 경우. 랜덤으로 빨리달릴지 여부
                                item.setStopHorseFast(false);
                                int randomFast = (int) (Math.random() * 8);
                                Boolean fastYN = randomFast == 5;
                                random = (int) (Math.random() * 10);
                                if(fastYN){ // 넘어진 말이 더 빨리달릴 경우
                                    random = (int) (Math.random() * 100);
                                }
                            }else{ //넘어지지 않은 말
                                random = (int) (Math.random() * 10);
                            }
                            distance = distance + random;
                        }
                        item.setDistance(distance);
                    }

                    //다 도착했을시 종료
                    if( distance > 0 && total > 0 ){
                        if( distance >= total){
                            if( 0 == item.getRankHorse() ){
                                item.setRankHorse(rank);
                                rank++;
                            }
                            if(items.size() <= (rank - 1)){
                                if (runnable != null) {
                                    handler.removeCallbacks(runnable);
                                    runnable = null;
                                    onFinish();
                                }

                            }
                        }
                    }

                }
                handler.postDelayed(runnable, 50);
                notifyDataSetChanged();
            }
        };
        handler.postDelayed(runnable, 50 );
    }

    //중지 시킬시
    public void onStop(){
        handler.removeCallbacks(runnable);
        rank = 1;
        horseNum = 1;
    }

    //중지버튼 누를시 말 멈춤
    public void stopRunning(){
        handler.removeCallbacks(runnable);
    }

    //중지버튼 누르고 난후 다시 재개
    public void goRunning(){
        handler.postDelayed(runnable, 50 );
    }

    //경주 종료
    public void onFinish(){
        rank = 1;
        horseNum = 1;
        onItemClick.rankHorse(items);
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.horse_item_list, parent, false);
        CustomViewHolder customViewHolder = new CustomViewHolder(view);
        return customViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        final HorseData item = items.get(position);
        int resId = R.drawable.horse_01;
        if (total == 0) {
            int layoutWidth = holder.layout.getWidth(); // 전체 거리
            if (layoutWidth > 0) {
                total = layoutWidth;
            }
        }
        int distance = item.getDistance(); // 말 움직인 거리

        LinearLayout.LayoutParams horseParams = (LinearLayout.LayoutParams) holder.horse.getLayoutParams();

        //말 달리는 중에만 바뀌도록
        if( distance > 0 ){
            if (item.getImgNum() == resId) {
                resId = R.drawable.horse_02;
            }
        }

        //화면이 말의 레이아웃보다 클시 움직이도록
        if( total > distance + horseParams.width ){
            if( item.isStopped() == true ){
                resId = R.drawable.horse_03;
            }
            horseParams.leftMargin = distance;
        }else{ //화면이 말의 레이아웃보다 작을시 멈추도록
            if (distance > 0 && total > 0) {
                horseParams.leftMargin = total - horseParams.width;
                resId = R.drawable.horse_01;

            }
        }
        item.setImgNum(resId);
        holder.horse.setLayoutParams(horseParams);
        holder.horse.setImageResource(item.getImgNum());

    }

    @Override
    public int getItemCount() {
        return (items != null ? items.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        protected ImageView horse;
        protected LinearLayout layout;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.horse = itemView.findViewById(R.id.horse);
            this.layout = itemView.findViewById(R.id.horse_item_list_id);
        }
    }
}