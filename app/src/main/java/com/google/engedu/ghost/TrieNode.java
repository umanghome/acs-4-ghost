package com.google.engedu.ghost;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;


public class TrieNode {
    private HashMap<String, TrieNode> children;
    private boolean isWord;

    public TrieNode() {
        children = new HashMap<>();
        isWord = false;
    }

    public void add(String s) {

        // If length is less than 1, don't do anything and return
        if (s.length() < 1) return;

        // Create node reference
        TrieNode node;

        // If node exists for first character, assign it to node reference
        if (this.children.containsKey("" + s.charAt(0))) {
            node = children.get("" + s.charAt(0));
        }
        // Otherwise, create a new node and assign it to node reference
        // and add the new node to children corresponding to the first character
        else {
            node = new TrieNode();
            children.put("" + s.charAt(0), node);
        }

        // If this is the last character of the word,
        // The node corresponding to this is a word
        if (s.length() == 1) {
            node.setWord(true);
            return;
        }

        // Add characters except the first one
        node.add(s.substring(1));

    }

    // Sets setWord to given boolean
    public void setWord (boolean isWord) {
        this.isWord = isWord;
    }

    // Is the current node a word?
    public boolean isNodeAWord () {
        return isWord;
    }

    public boolean isWord(String s) {

        // If this is the node that corresponds
        if (s.length() == 0) {
            return isWord;
        }

        // If there is no node corresponding to the first character,
        // the word doesn't exist
        if (!this.children.containsKey("" + s.charAt(0))) {
            return false;
        }

        // Get the node corresponding to the first character
        TrieNode node = children.get("" + s.charAt(0));

        // And check if the rest of the word is valid further
        return node.isWord(s.substring(1));

    }

    public String getAnyWordStartingWith(TrieNode root, String s) {

        // Get last node for string
        TrieNode lastNode = TrieNode.getLastNodeFor(root, s);

        // Get all the words below the last node
        ArrayList<String> wordsBelow = lastNode.findWordsBelow();

        // If a word exists
        if (wordsBelow.size() > 0) {
            // Pick a random word and add s to the front
            return (s + wordsBelow.get(new Random().nextInt(wordsBelow.size())));
        }
        // Word doesn't exist, return null
        else {
            return null;
        }

    }

    public String getGoodWordStartingWith(TrieNode root, String s, int whoWentFirst) {

        String selected = null;

        // Get last node for string
        TrieNode lastNode = TrieNode.getLastNodeFor(root, s);

        // Get all the words below the last node
        ArrayList<String> wordsBelow = lastNode.findWordsBelow();

        // No words exist, return null
        if (wordsBelow.size() < 1) return null;

        // Initialize words to consider
        ArrayList<String> wordsToConsider = new ArrayList<String>();

        // Populate wordsToConsider
        for (int i = 0; i < wordsBelow.size(); i++) {
            String word = wordsBelow.get(i);
            if (word.length() % 2 == whoWentFirst) {
                wordsToConsider.add(word);
            }
        }

        // If no wordsToConsider is empty
        if (wordsToConsider.size() == 0) {
            selected = getAnyWordStartingWith(root, s);
        }
        // Select a random word from wordsToConsider, if wordsToConsider is not empty
        // and add prefix (s) at the beginning.
        else {
            selected = s + wordsToConsider.get(new Random().nextInt(wordsToConsider.size()));
        }

        return selected;

    }

    // Gets the last node for any string, starting from root
    public static TrieNode getLastNodeFor (TrieNode root, String s) {

        // If string's length is 0, this is the last node
        if (s.length() == 0) {
            return root;
        }

        // If root has a child for s
        if (root.hasChild(s)) {
            // Continue to find last node
            return TrieNode.getLastNodeFor(root.getChild(s), s.substring(1));
        }
        // Return null if root does not have a child for s
        else {
            return null;
        }
    }

    // Determines if the node has a child for s
    public boolean hasChild (String s) {
        // If length is less than 1, there can exist no child
        if (s.length() < 1) return false;

        // Return the boolean about whether the node has a child for s
        return this.children.containsKey("" + s.charAt(0));
    }

    // Returns the child node for s
    public TrieNode getChild (String s) {
        // If length is less than 1, there can exist no child
        if (s.length() < 1) return null;

        // If there exists a child for s, return it
        if (this.children.containsKey("" + s.charAt(0))) {
            return children.get("" + s.charAt(0));
        }
        // Return null if a child for s doesn't exist
        else {
            return null;
        }
    }

    // Finds all the words below the current node
    public ArrayList<String> findWordsBelow () {

        // Initialize words to an empty ArrayList
        ArrayList<String> words = new ArrayList<String>();

        // Get keys
        Set<String> keys = this.children.keySet();

        // Get iterator
        Iterator iterator = keys.iterator();

        // Loop
        while (iterator.hasNext()) {

            // Get key
            String key = (String) iterator.next();

            // Get node from key
            TrieNode child = this.children.get(key);

            // If node is a word, add it to words
            if (child.isNodeAWord()) {
                words.add(key);
            }

            // Get words below node
            ArrayList<String> wordsBelowChild = child.findWordsBelow();

            // Iterate over wordsBelowChild and add
            for (int i = 0; i < wordsBelowChild.size(); i++) {
                String wordBelowChild = wordsBelowChild.get(i);
                words.add(key + wordBelowChild);
            }
        }

        // Return words
        return words;

    }
}
