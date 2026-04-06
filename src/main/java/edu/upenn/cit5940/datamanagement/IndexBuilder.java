package edu.upenn.cit5940.datamanagement;

import edu.upenn.cit5940.common.dto.Article;

import java.util.*;

import static edu.upenn.cit5940.common.dto.StringHelper.stringTokenizer;

public class IndexBuilder {

    Map<String, Set<Integer>> invertedIndex = new HashMap<>();

    //constructor
    public IndexBuilder(List<Article> articles) {
        this.invertedIndex = new HashMap<>();
        buildIndex(articles);
    }

    //getter
    public Map<String, Set<Integer>> getInvertedIndex() {
        return invertedIndex;
    }

    /**
     *
     * @param articles
     */
    private void buildIndex(List<Article> articles) {

        for (int i = 0; i < articles.size(); i++) {
            addArticle(articles.get(i), i);
        }
    }

    /**
     *
     * @param article
     * @param docID
     */
    public void addArticle(Article article, int docID) {
        List<String> tokens = stringTokenizer(article.getTitle() + " " + article.getBody());
        for (String token : tokens) {
            if (!StopWords.stopWordChecker(token) && !token.isEmpty()) {
                invertedIndex.computeIfAbsent(token, k -> new HashSet<>()).add(docID);
            }
        }
    }
}
