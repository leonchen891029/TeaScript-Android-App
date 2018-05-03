package com.teacore.teascript.bean;

import java.io.Serializable;
import java.util.List;

/**Entity列表接口
 * Created by apple on 17/10/19.
 */

public interface EntityList<T extends Entity> extends Serializable{
       public List<T> getList();
}
