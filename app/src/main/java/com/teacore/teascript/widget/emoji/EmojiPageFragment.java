package com.teacore.teascript.widget.emoji;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;

import com.teacore.teascript.R;

import java.util.ArrayList;
import java.util.List;

/**表情页每页的显示
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-2-20
 */
@SuppressLint("ValidFragment")
public class EmojiPageFragment extends Fragment {

    private List<EmojiIcon> datas;
    private GridView gridView;
    private EmojiAdapter adapter;
    private OnEmojiClickListener listener;

    public EmojiPageFragment(int index,int type,OnEmojiClickListener emojiClickListener){
        initData(index,type);
        this.listener=emojiClickListener;
    }


    private void initData(int index, int type) {
        datas = new ArrayList<EmojiIcon>();
        if (EmojiFragment.EMOJI_TABS > 1) {

            datas = EmojiDisplayRules.getAllByType(type);

        } else {

            List<EmojiIcon> dataAll = EmojiDisplayRules.getAllByType(type);

            int max = Math.min((index + 1) * EmojiConfig.COUNT_IN_PAGE,
                    dataAll.size());

            for (int i = index * EmojiConfig.COUNT_IN_PAGE; i < max; i++) {

                datas.add(dataAll.get(i));

            }

            datas.add(new EmojiIcon(EmojiConfig.DELETE_EMOJI_ID, 1, "delete:",
                    "delete:"));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        gridView = new GridView(getActivity());

        gridView.setNumColumns(EmojiConfig.COLUMNS);

        adapter = new EmojiAdapter(getActivity(), datas);

        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                EditText editText = (EditText) getActivity().findViewById(
                        R.id.emoji_titile_et);

                if (listener != null) {

                    listener.onEmojiClick((EmojiIcon) parent.getAdapter()
                            .getItem(position));

                }

                EmojiInputUtils.input2OSC(editText, (EmojiIcon) parent.getAdapter()
                        .getItem(position));
            }
        });

        return gridView;
    }

    public GridView getRootView() {
        return gridView;
    }



}
