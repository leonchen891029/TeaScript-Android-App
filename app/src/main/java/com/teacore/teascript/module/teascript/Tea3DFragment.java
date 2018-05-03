package com.teacore.teascript.module.teascript;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ramotion.foldingcell.FoldingCell;
import com.teacore.teascript.R;
import com.teacore.teascript.base.BaseFragment;

/**
 * Created by apple on 18/4/19.
 */

public class Tea3DFragment extends BaseFragment{

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_tea3d;
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

        final FoldingCell fc=(FoldingCell) view.findViewById(R.id.folding_cell);

        fc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fc.toggle(false);
            }
        });

        initView(view);
    }

    @Override
    public void initView(View view){

    }
}
