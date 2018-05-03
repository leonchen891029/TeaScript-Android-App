package com.teacore.teascript.module.teascript;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ramotion.foldingcell.FoldingCell;
import com.teacore.teascript.R;
import com.teacore.teascript.base.BaseFragment;

import github.ishaan.buttonprogressbar.ButtonProgressBar;

/**
 * 介绍TeaScript的Fragment
 * @author 陈晓帆
 * @version 1.0
 * Created 2018-2-1
 */

public class TeaScriptFragment extends BaseFragment{

    private FoldingCell mTeaScriptFoldingCell;
    private FoldingCell mIdeFoldingCell;
    private ButtonProgressBar mTeaScriptBPB;
    private ButtonProgressBar mIdeBPB;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_teascript;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mLayoutInflater = inflater;
        View view = inflater.inflate(getLayoutId(), container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        mTeaScriptFoldingCell=(FoldingCell) view.findViewById(R.id.teascript_folding_cell);

        mIdeFoldingCell=(FoldingCell) view.findViewById(R.id.ide_folding_cell);

        mTeaScriptBPB=(ButtonProgressBar) view.findViewById(R.id.teascript_button_progress_bar);

        mIdeBPB=(ButtonProgressBar) view.findViewById(R.id.ide_button_progress_bar);

        initView(view);
    }

    @Override
    public void initView(View view){

        mTeaScriptFoldingCell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTeaScriptFoldingCell.toggle(false);
            }
        });

        mIdeFoldingCell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIdeFoldingCell.toggle(false);
            }
        });

        mTeaScriptBPB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTeaScriptBPB.startLoader();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mTeaScriptBPB.stopLoader();
                    }
                }, 5000);
            }
        });

        mIdeBPB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIdeBPB.startLoader();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mIdeBPB.stopLoader();
                    }
                }, 5000);
            }
        });

    }

}
