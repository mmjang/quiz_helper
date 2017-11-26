package com.mmjang.quizhelper.data.dict;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liao on 2017/7/30.
 */

public class YoudaoResult {
    public String returnPhrase = "";
    public String phonetic = "";
    public List<String> translation = new ArrayList<>();
    public Map<String, List<String>> webTranslation = new LinkedHashMap<>();

}
