package com.horserace.horseracing;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class HorseData {

    private int imgNum = R.drawable.horse_01;
    private int distance;
    private boolean isStopped;
    private int retryCnt;
    private int totalDistance;
    private int horseNum;
    private int rankHorse;
    private boolean stopHorseFast;
}
