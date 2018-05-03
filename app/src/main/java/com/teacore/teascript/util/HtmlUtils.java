package com.teacore.teascript.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**HTML工具类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-3-8
 */

public class HtmlUtils {
    //定义Script的正则表达式
    private static final String regEx_script = "<script[^>]*?>[\\s\\S]*?<\\/script>";
    //定义style的正则表达式
    private static final String regEx_style = "<style[^>]*?>[\\s\\S]*?<\\/style>";
    //定义HTML标签的正则表达式
    private static final String regEx_html = "<[^>]+>";
    // 过滤所有以<开头以>结尾的标签
    private final static String regxpForHtml = "<([^>]*)>";

    //过滤掉HTML标签
    public static String delHTMLTag(String htmlStr) {
        //过滤掉Script标签
        Pattern p_script = Pattern.compile(regEx_script,
                Pattern.CASE_INSENSITIVE);
        Matcher m_script = p_script.matcher(htmlStr);
        htmlStr = m_script.replaceAll("");

        //过滤style标签
        Pattern p_style = Pattern
                .compile(regEx_style, Pattern.CASE_INSENSITIVE);
        Matcher m_style = p_style.matcher(htmlStr);
        htmlStr = m_style.replaceAll("");

        // 过滤html标签
        Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
        Matcher m_html = p_html.matcher(htmlStr);
        htmlStr = m_html.replaceAll("");

        //返回字符串
        return htmlStr.trim();
    }

    //替换标记以正常显示
    public static String replaceTag(String input) {

        if (!hasSpecialChars(input)) {
            return input;
        }

        StringBuffer filtered = new StringBuffer(input.length());

        char c;

        for (int i = 0; i <= input.length() - 1; i++) {
            c = input.charAt(i);
            switch (c) {
                case '<':
                    filtered.append("&lt;");
                    break;
                case '>':
                    filtered.append("&gt;");
                    break;
                case '"':
                    filtered.append("&quot;");
                    break;
                case '&':
                    filtered.append("&amp;");
                    break;
                default:
                    filtered.append(c);
            }

        }
        return (filtered.toString());
    }


    //判断标记是否存在
    public static boolean hasSpecialChars(String input) {
        boolean flag = false;
        if ((input != null) && (input.length() > 0)) {
            char c;
            for (int i = 0; i <= input.length() - 1; i++) {
                c = input.charAt(i);
                switch (c) {
                    case '>':
                        flag = true;
                        break;
                    case '<':
                        flag = true;
                        break;
                    case '"':
                        flag = true;
                        break;
                    case '&':
                        flag = true;
                        break;
                }
            }
        }
        return flag;
    }

    //基本功能：过滤所有以"<"开头以">"结尾的标签
    public static String filterHtml(String str) {
        Pattern pattern = Pattern.compile(regxpForHtml);
        Matcher matcher = pattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        boolean result1 = matcher.find();
        while (result1) {
            matcher.appendReplacement(sb, "");
            result1 = matcher.find();
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 基本功能：过滤指定标签
     * @param str
     * @param tag 指定标签
     */
    public static String fiterHtmlTag(String str, String tag) {
        String regxp = "<\\s*" + tag + "\\s+([^>]*)\\s*>";
        Pattern pattern = Pattern.compile(regxp);
        Matcher matcher = pattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        boolean result1 = matcher.find();
        while (result1) {
            matcher.appendReplacement(sb, "");
            result1 = matcher.find();
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 基本功能：替换指定的标签
     * @param beforeTag 要替换的标签
     * @param tagAttrib 要替换的标签属性值
     * @param startTag  新标签开始标记
     * @param endTag    新标签结束标记
     */
    public static String replaceHtmlTag(String str, String beforeTag,
                                        String tagAttrib, String startTag, String endTag) {

        String regxpForTag = "<\\s*" + beforeTag + "\\s+([^>]*)\\s*>";

        String regxpForTagAttrib = tagAttrib + "=\"([^\"]+)\"";

        Pattern patternForTag = Pattern.compile(regxpForTag);

        Pattern patternForAttrib = Pattern.compile(regxpForTagAttrib);

        Matcher matcherForTag = patternForTag.matcher(str);

        StringBuffer sb = new StringBuffer();

        boolean result = matcherForTag.find();

        while (result) {
            StringBuffer sbreplace = new StringBuffer();
            Matcher matcherForAttrib = patternForAttrib.matcher(matcherForTag
                    .group(1));
            if (matcherForAttrib.find()) {
                matcherForAttrib.appendReplacement(sbreplace, startTag
                        + matcherForAttrib.group(1) + endTag);
            }
            matcherForTag.appendReplacement(sb, sbreplace.toString());
            result = matcherForTag.find();
        }
        matcherForTag.appendTail(sb);
        return sb.toString();
    }

    //去掉域名中的反斜杠号
    public static String cleanSlash(String stringUrl){
        return stringUrl.replaceAll("\\\\","");
    }

}
