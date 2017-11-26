package com.mmjang.quizhelper.data.dict.customdict;

/**
 * Created by liao on 2017/8/17.
 */

public interface CustomDictionaryParser {
    CustomDictionaryInformation getCustomDictionaryInformation();
    boolean hasNext();
    CustomDictionaryEntry getNextEntry();
}
