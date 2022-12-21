package com.horserace.horseracing;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.horserace.horseracing.databinding.HorseMainBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class HorseMainActivity extends AppCompatActivity implements OnItemClick{
    private ArrayList<HorseData> horseDataArrayList;
    private HorseAdapter horseAdapter;
    private LinearLayoutManager linearLayoutManager;
    private int num = 2;
    private HorseData horseData = new HorseData();
    private HorseMainBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = HorseMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        linearLayoutManager = new LinearLayoutManager(this);
        binding.horseRecyclerview.setLayoutManager(linearLayoutManager);

        horseDataArrayList = new ArrayList<>();
        horseAdapter = new HorseAdapter(horseDataArrayList,this, this);
        binding.horseRecyclerview.setAdapter(horseAdapter);

        binding.stopBtn.setEnabled(false); //중지버튼 비활성화

        addHorse();
        addHorse();

        //마이너스 버튼 누를시 동작
        binding.minusBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(num == 2){
                    return;
                }
                num--;
                binding.numberText.setText(""+num);
                deleteHorse();
            }
        });

        //플러스 버튼 누를시 동작
        binding.plusBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                num++;
                binding.numberText.setText(""+num);
                addHorse();
            }
        });

        //입력 버튼 누를시 동작
        binding.textBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(HorseMainActivity.this);
                alert.setTitle("말 개수 입력");
                alert.setMessage("말 개수를 집적 입력하시겠습니까?");

                alert.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        editHorseNum();
                    }
                });

                alert.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alert.setCancelable(false);
                alert.show();
            }
        });

        //시작 버튼 누를시 동작
        binding.startBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                binding.stopBtn.setEnabled(true); //중지버튼 활성화
                binding.textBtn.setEnabled(false);//입력버튼 비활성화
                binding.minusBtn.setEnabled(false);//마이너스 버튼 비활성화
                binding.plusBtn.setEnabled(false);//플러스 버튼 비활성화
                binding.startBtn.setEnabled(false);//시작 버튼 비활성화

                horseAdapter.startHorse();
            }
        });

        //중지 버튼 누를시 동작
        binding.stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                horseAdapter.stopRunning();

                AlertDialog.Builder alert = new AlertDialog.Builder(HorseMainActivity.this);
                alert.setTitle("중지");
                alert.setMessage("중지시키겠습니까?");

                alert.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        binding.stopBtn.setEnabled(false); //중지버튼 비활성화
                        binding.textBtn.setEnabled(true);
                        binding.minusBtn.setEnabled(true);//마이너스 버튼 활성화
                        binding.plusBtn.setEnabled(true);//플러스 버튼 활성화
                        binding.startBtn.setEnabled(true);//시작 버튼 활성화
                        horseAdapter.onStop();
                        horseAdapter.allDeleteItem();
                        binding.numberText.setText("2");
                        num = 2;
                        addHorse();
                        addHorse();
                    }
                });

                alert.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        horseAdapter.goRunning();
                    }
                });
                alert.setCancelable(false);
                alert.show();
            }
        });

        //종료 버튼 누를시 동작
        binding.endBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                horseAdapter.stopRunning();

                AlertDialog.Builder alert = new AlertDialog.Builder(HorseMainActivity.this);
                alert.setTitle("종료");
                alert.setMessage("종료하시겠습니까?");

                alert.setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });

                alert.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        horseAdapter.goRunning();
                    }
                });
                alert.show();
            }
        });
    }

    //말 추가
    public void addHorse(){
        horseData = new HorseData();
        horseAdapter.addItem(horseData);
    }

    //말 삭제
    public void deleteHorse(){
        horseAdapter.deleteItem();
    }

    //순위 알림창
    @Override
    public void rankHorse(ArrayList<HorseData> horseList) {
        String rank = "";
        Collections.sort(horseList,sortByRank);//순위별 정렬

        for(int i=0; i<horseList.size(); i++){
            rank +=horseList.get(i).getRankHorse()+"등 : "+horseList.get(i).getHorseNum()+"번 말\n";
        }

        String finalRank = rank;

        AlertDialog.Builder alert = new AlertDialog.Builder(HorseMainActivity.this);
        alert.setTitle("순위");
        alert.setMessage(finalRank.toString());

        alert.setPositiveButton("다시시작", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                binding.stopBtn.setEnabled(false); //중지버튼 비활성화
                binding.textBtn.setEnabled(true);
                binding.minusBtn.setEnabled(true);//마이너스 버튼 활성화
                binding.plusBtn.setEnabled(true);//플러스 버튼 활성화
                binding.startBtn.setEnabled(true);//시작 버튼 활성화
                horseAdapter.allDeleteItem();
                binding.numberText.setText("2");
                num = 2;
                addHorse();
                addHorse();
            }
        });
        alert.setNegativeButton("종료", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        alert.setCancelable(false);

        AlertDialog alertDialog = alert.create();
        alertDialog.show();

    }

    //순위별 정렬
    private final static  Comparator<HorseData>sortByRank=new Comparator<HorseData>() {
        @Override
        public int compare(HorseData horseData1, HorseData horseData2) {
            return Integer.compare(horseData1.getRankHorse(),horseData2.getRankHorse());
        }
    };

    public void editHorseNum(){
        AlertDialog.Builder editAlert = new AlertDialog.Builder(HorseMainActivity.this);

        EditText input = new EditText(this);
        input.setInputType(2);
        editAlert.setTitle("말 개수 입력");
        editAlert.setView(input);

        editAlert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        editAlert.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        editAlert.setCancelable(false);
        AlertDialog dialog = editAlert.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String horseNumbers = input.getText().toString();

                if( "".equals(horseNumbers) || null == horseNumbers ){
                    Toast.makeText(HorseMainActivity.this,"숫자를 입력해주세요.",Toast.LENGTH_SHORT).show();
                    return;
                }
                int horseNum = Integer.parseInt(horseNumbers);
                if( horseNum < 2 ){
                    Toast.makeText(HorseMainActivity.this,"2마리 이상 입력해주세요.",Toast.LENGTH_SHORT).show();
                }else{
                    binding.numberText.setText(""+horseNum);
                    num = horseNum;
                    horseAdapter.allDeleteItem();
                    for(int i = 0; i < horseNum; i++){
                        addHorse();
                    }
                    dialog.dismiss();
                }
            }
        });

    }

}
