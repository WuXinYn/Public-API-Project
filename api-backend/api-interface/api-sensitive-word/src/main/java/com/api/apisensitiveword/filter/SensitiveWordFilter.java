package com.api.apisensitiveword.filter;

import java.util.*;

public class SensitiveWordFilter {

    private static final String REPLACEMENT = "***"; // 敏感词替换字符
    private final TrieNode root = new TrieNode(); // Trie 树根节点
    private final String[] list = {"傻逼", "你妈", "草"};

    // Trie 树节点
    private static class TrieNode {
        private boolean isEnd; // 是否是敏感词的结尾
        private final Map<Character, TrieNode> subNodes = new HashMap<>(); // 子节点

        public void addSubNode(Character key, TrieNode node) {
            subNodes.put(key, node);
        }

        public TrieNode getSubNode(Character key) {
            return subNodes.get(key);
        }

        public boolean isEnd() {
            return isEnd;
        }

        public void setEnd(boolean end) {
            isEnd = end;
        }
    }

    // 添加敏感词
    public void addWord(String word) {
        if (word == null || word.isEmpty()) return;

        TrieNode tempNode = root;
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            TrieNode node = tempNode.getSubNode(c);

            if (node == null) {
                node = new TrieNode();
                tempNode.addSubNode(c, node);
            }

            tempNode = node;

            if (i == word.length() - 1) {
                tempNode.setEnd(true);
            }
        }
    }

    // 过滤敏感词
    public String filter(String text) {
        if (text == null || text.isEmpty()) return text;

        StringBuilder result = new StringBuilder();
        TrieNode tempNode = root;
        int begin = 0; // 起始位置
        int position = 0; // 当前比较位置

        while (position < text.length()) {
            char c = text.charAt(position);

            // 跳过特殊字符（如空格、符号）
            if (isSymbol(c)) {
                if (tempNode == root) {
                    result.append(c);
                    begin++;
                }
                position++;
                continue;
            }

            tempNode = tempNode.getSubNode(c);
            if (tempNode == null) {
                result.append(text.charAt(begin));
                position = begin + 1;
                begin = position;
                tempNode = root;
            } else if (tempNode.isEnd()) {
                result.append(REPLACEMENT);
                position++;
                begin = position;
                tempNode = root;
            } else {
                position++;
            }
        }

        result.append(text.substring(begin));
        return result.toString();
    }

    // 判断是否是特殊字符
    private boolean isSymbol(char c) {
        return !Character.isLetterOrDigit(c);
    }
}
