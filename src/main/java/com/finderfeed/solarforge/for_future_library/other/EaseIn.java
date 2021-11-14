package com.finderfeed.solarforge.for_future_library.other;

import com.finderfeed.solarforge.for_future_library.helpers.FinderfeedMathHelper;
import net.minecraft.util.Mth;

import java.util.function.Function;

public class EaseIn implements CanTick{

    private int ticker = 0;
    private double duration;
    private double start = 0;
    private double end;

    public EaseIn(double start,double end, double duration){
        this.start = start;
        this.duration = duration;
        this.end = end;
    }



    public double getValue(){
        double time = FinderfeedMathHelper.clamp(start,ticker,duration);
        return Mth.lerp(FinderfeedMathHelper.SQUARE.apply(time/duration),start,end);
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public void setEnd(double end) {
        this.end = end;
    }

    @Override
    public void tick(){
        if (ticker+1 <= duration){
            ticker++;
        }
    }
    public void tickBackwards(){
        if (ticker-1 >= 0){
            ticker--;
        }
    }

    public void reset(){
        this.ticker = 0;
    }

}
