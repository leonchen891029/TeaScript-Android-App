package com.teacore.teascript.widget.emoji;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**EmojiPager的Adapter
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-2-20
 */
public class EmojiPagerAdapter extends FragmentPagerAdapter{

    private OnEmojiClickListener listener;

    public EmojiPagerAdapter(FragmentManager fragmentManager){
        super(fragmentManager);
    }

    public EmojiPagerAdapter(FragmentManager fragmentManager,int tabCount,OnEmojiClickListener emojiClickListener){
        super(fragmentManager);
        EmojiFragment.EMOJI_TABS=tabCount;
        listener=emojiClickListener;
    }

    @Override
    public EmojiPageFragment getItem(int index) {

        if (EmojiFragment.EMOJI_TABS > 1) {
            return new EmojiPageFragment(index, index, listener);
        } else {
            return new EmojiPageFragment(index, 0, listener);
        }

    }


    /**
     * 显示模式：如果只有一种Emoji表情，则像QQ表情一样左右滑动分页显示
     * 如果有多种Emoji表情，每页显示一种，Emoji筛选时上下滑动筛选。
     */
    @Override
    public int getCount() {

        if (EmojiFragment.EMOJI_TABS > 1) {
            return EmojiFragment.EMOJI_TABS;
        } else {
            // 采用进一法取小数
            return (EmojiDisplayRules.getAllByType(0).size() - 1 + EmojiConfig.COUNT_IN_PAGE)
                    / EmojiConfig.COUNT_IN_PAGE;
        }
    }
}
