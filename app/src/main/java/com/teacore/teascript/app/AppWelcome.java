package com.teacore.teascript.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import com.codemybrainsout.onboarder.AhoyOnboarderActivity;
import com.codemybrainsout.onboarder.AhoyOnboarderCard;
import com.teacore.teascript.R;
import com.teacore.teascript.module.main.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class AppWelcome extends AhoyOnboarderActivity{

    AhoyOnboarderCard tsCard=new AhoyOnboarderCard("TeaScript语言社区","加入我们!优秀的语言社区需要同样优秀的工程师", R.drawable.appintro1);
    AhoyOnboarderCard tvmCard=new AhoyOnboarderCard("Tea Virtual Machine","全新虚拟机",R.drawable.appintro2);
    AhoyOnboarderCard tea3dCard=new AhoyOnboarderCard("Tea3D","全新图形平台",R.drawable.appintro3);
    AhoyOnboarderCard intelligenceCard=new AhoyOnboarderCard("智能化我们的生活",null,R.drawable.appintro4);

    List<AhoyOnboarderCard> cardList=new ArrayList<AhoyOnboarderCard>();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        tsCard.setBackgroundColor(R.color.teascript);
        tsCard.setTitleColor(R.color.white);
        tsCard.setDescriptionColor(R.color.white);
        tsCard.setTitleTextSize(dpToPixels(10, this));
        tsCard.setDescriptionTextSize(dpToPixels(8, this));
        tsCard.setIconLayoutParams(1200,1200,0,0,0,0);

        tvmCard.setBackgroundColor(R.color.teascript);
        tvmCard.setTitleColor(R.color.white);
        tvmCard.setDescriptionColor(R.color.white);
        tvmCard.setTitleTextSize(dpToPixels(10, this));
        tvmCard.setDescriptionTextSize(dpToPixels(8, this));
        tvmCard.setIconLayoutParams(1200,1200,0,0,0,0);

        tea3dCard.setBackgroundColor(R.color.teascript);
        tea3dCard.setTitleColor(R.color.white);
        tea3dCard.setDescriptionColor(R.color.white);
        tea3dCard.setTitleTextSize(dpToPixels(10, this));
        tea3dCard.setDescriptionTextSize(dpToPixels(8, this));
        tea3dCard.setIconLayoutParams(1200,1200,0,0,0,0);

        intelligenceCard.setBackgroundColor(R.color.teascript);
        intelligenceCard.setTitleColor(R.color.white);
        intelligenceCard.setDescriptionColor(R.color.white);
        intelligenceCard.setTitleTextSize(dpToPixels(10, this));
        intelligenceCard.setDescriptionTextSize(dpToPixels(8, this));
        intelligenceCard.setIconLayoutParams(1200,1200,0,0,0,0);

        cardList.add(tsCard);
        cardList.add(tvmCard);
        cardList.add(tea3dCard);
        cardList.add(intelligenceCard);

        setColorBackground(R.color.teascript);
        setOnboardPages(cardList);

        setFinishButtonTitle("进入TeaScript的语言世界吧!");
        setFinishButtonDrawableStyle(ContextCompat.getDrawable(this,R.drawable.appintro_button));
    }

    @Override
    public void onFinishButtonPressed(){
        Intent intent=new Intent(AppWelcome.this, MainActivity.class);
        startActivity(intent);
    }

}
