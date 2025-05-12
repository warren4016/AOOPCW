package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ModelTest {

    private Model model;

    @BeforeEach
    void setUp() {
        // 初始化 Model 对象，使用一个简单的字典文件
        model = new Model("dictionary.txt");
    }

    @Test
    void testRandomWordsGeneration() {
        // 测试随机生成的起始单词和目标单词是否有效
        model.setRandomWords(true);
        assertNotNull(model.getStartWord());
        assertNotNull(model.getTargetWord());
        assertNotEquals(model.getStartWord(), model.getTargetWord());
    }

    @Test
    void testMakeMoveValid() {
        // 测试有效的移动
        model.setRandomWords(false); // 使用固定的起始和目标单词
        model.setStartWord("WORD");
        model.setTargetWord("WORT");
        assertFalse(model.makeMove("WORD"));



        assertEquals("No change!", model.getErrorMessage());
    }





    @Test
    void testRestartGame() {
        // 测试重新开始游戏
        model.setRandomWords(false); // 使用固定的起始和目标单词
        model.setStartWord("WAST");
        model.setTargetWord("WEST");
        model.makeMove("WAST");
        model.restartGame();
        assertTrue(model.getMoveHistory().isEmpty());
    }}




